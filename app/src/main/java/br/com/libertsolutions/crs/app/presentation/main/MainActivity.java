package br.com.libertsolutions.crs.app.presentation.main;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.data.checkin.CheckinRealmDataService;
import br.com.libertsolutions.crs.app.data.checkin.CheckinService;
import br.com.libertsolutions.crs.app.data.config.ConfigDataHelper;
import br.com.libertsolutions.crs.app.data.config.ConfigService;
import br.com.libertsolutions.crs.app.data.flow.FlowRealmDataService;
import br.com.libertsolutions.crs.app.data.flow.FlowService;
import br.com.libertsolutions.crs.app.data.login.LoginDataHelper;
import br.com.libertsolutions.crs.app.data.sync.SyncService;
import br.com.libertsolutions.crs.app.data.sync.event.EventBusManager;
import br.com.libertsolutions.crs.app.data.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.data.sync.event.SyncStatus;
import br.com.libertsolutions.crs.app.data.work.WorkDataService;
import br.com.libertsolutions.crs.app.data.work.WorkRealmDataService;
import br.com.libertsolutions.crs.app.data.work.WorkService;
import br.com.libertsolutions.crs.app.domain.pojo.Checkins;
import br.com.libertsolutions.crs.app.domain.pojo.Flows;
import br.com.libertsolutions.crs.app.domain.pojo.User;
import br.com.libertsolutions.crs.app.domain.pojo.Work;
import br.com.libertsolutions.crs.app.domain.pojo.Works;
import br.com.libertsolutions.crs.app.presentation.activity.BaseActivity;
import br.com.libertsolutions.crs.app.presentation.util.FeedbackHelper;
import br.com.libertsolutions.crs.app.presentation.util.NavigationHelper;
import br.com.libertsolutions.crs.app.presentation.util.NetworkUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.MaterialDialog;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
import static br.com.libertsolutions.crs.app.data.config.ConfigDataHelper.setCheckinDataAsImported;
import static br.com.libertsolutions.crs.app.data.config.ConfigDataHelper.setDataImportationAsLastSyncDate;
import static br.com.libertsolutions.crs.app.data.config.ConfigDataHelper.setFlowDataAsImported;
import static br.com.libertsolutions.crs.app.data.config.ConfigDataHelper.setWorkDataAsImported;
import static br.com.libertsolutions.crs.app.data.util.Constants.PAGE_START;
import static br.com.libertsolutions.crs.app.data.util.RxUtil.exponentialBackoff;
import static br.com.libertsolutions.crs.app.data.util.RxUtil.timeoutException;
import static br.com.libertsolutions.crs.app.data.util.ServiceGenerator.createService;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.greenrobot.eventbus.ThreadMode.MAIN;
import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class MainActivity extends BaseActivity implements OnQueryTextListener, OnRefreshListener {

    private MainWorkAdapter workAdapter;

    private WorkDataService workDataService;

    private CompositeSubscription compositeSubscription;

    private User mUserLogged;

    private int currentPageWorks = PAGE_START;
    private int currentPageFlows = PAGE_START;
    private int currentPageCheckins = PAGE_START;

    @BindView(R.id.list) RecyclerView recyclerViewWorks;
    @BindView(R.id.empty_state) LinearLayout viewEmptyState;
    @BindView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected int provideLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle inState) {
        SyncService.start(this);
        super.onCreate(inState);
        setupActionBar();
        setupRecyclerView();
        setupSwipeRefreshLayout();
        compositeSubscription = new CompositeSubscription();
        showUserLoggedInfo();
        loadViewData();
    }

    private void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_main);
        }

        if (mToolbarAsActionBar != null) {
            mToolbarAsActionBar.setNavigationIcon(R.drawable.ic_worker);
        }
    }

    private void setupRecyclerView() {
        changeListLayout(getResources().getConfiguration());
        recyclerViewWorks.setHasFixedSize(true);
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
    }

    @Override
    public void onRefresh() {
        requestCompleteSync();
    }

    private void showUserLoggedInfo() {
        if (mUserLogged == null) {
            mUserLogged = LoginDataHelper.getUserLogged(this);
        }

        if (mUserLogged != null) {
            FeedbackHelper
                    .snackbar(mRootView, String.format("Logado como %s.",
                            LoginDataHelper.formatCpf(mUserLogged.getName())), false);
        }
    }

    private void loadViewData() {
        final boolean isInitialDataImported = ConfigDataHelper.isInitialDataImported(this);

        if (isInitialDataImported) {
            loadWorkData();
            requestCompleteSync();
        } else if (NetworkUtils.isDeviceConnectedToInternet(this)) {
            startImportingData();
        } else {
            showNoDataAndNetworkState();
        }
    }

    private void requestCompleteSync() {
        if (NetworkUtils.isDeviceConnectedToInternet(this)) {
            SyncService.requestCompleteSync();
        } else {
            stopRefreshingProgress();
            FeedbackHelper.toast(this, getString(R.string.no_connection_to_force_update), false);
        }
    }

    @Subscribe(threadMode = MAIN)
    public void onSyncEvent(SyncEvent event) {
        Timber.i("Sync event with %s in %s", event.getType(), event.getStatus());

        if (event.getStatus() == SyncStatus.IN_PROGRESS) {
            Timber.i("Sync in progress");
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
        } else {
            Timber.i("Sync completed");
            stopRefreshingProgress();
            loadWorkData();
        }
    }

    private void stopRefreshingProgress() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void startImportingData() {
        showImportingDataState();
        requestWorkData();
    }

    private void showImportingDataState() {
        showEmptyState(R.drawable.ic_import_state, R.string.title_importing_data,
                R.string.tagline_importing_data);
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
    }

    @SuppressWarnings("ConstantConditions")
    private void requestWorkData() {
        final Subscription subscription = createService(WorkService.class, this)
                .getAll(currentPageWorks)
                .retryWhen(timeoutException())
                .retryWhen(exponentialBackoff(3, 5, SECONDS))
                .observeOn(mainThread())
                .subscribe(this::saveWorkData, e -> showError(R.string.error_importing_data, e));
        compositeSubscription.add(subscription);
    }

    @SuppressWarnings("ConstantConditions")
    private void saveWorkData(Works works) {
        if (works == null || (works.list == null || works.list.isEmpty())) {
            showEmptyView(true);
            return;
        }

        final Subscription subscription = getWorkDataService()
                .saveAll(works.list)
                .observeOn(mainThread())
                .subscribeOn(io())
                .doOnError(e -> showError(R.string.title_dialog_error_saving_data, e))
                .doOnCompleted(
                        () -> {
                            if (currentPageWorks == works.totalPaginas) {
                                setWorkDataAsImported(MainActivity.this);
                                requestFlowData();
                            } else {
                                currentPageWorks++;
                                requestWorkData();
                            }
                        })
                .subscribe();
        compositeSubscription.add(subscription);
    }

    @SuppressWarnings("ConstantConditions")
    private void requestFlowData() {
        final Subscription subscription = createService(FlowService.class, this)
                .getAll(currentPageFlows)
                .retryWhen(timeoutException())
                .retryWhen(exponentialBackoff(3, 5, SECONDS))
                .observeOn(mainThread())
                .subscribe(this::saveFlowData, e -> showError(R.string.error_importing_data, e));
        compositeSubscription.add(subscription);
    }

    private void saveFlowData(Flows flows) {
        final Subscription subscription = new FlowRealmDataService(this)
                .saveAll(flows.list)
                .observeOn(mainThread())
                .subscribeOn(io())
                .doOnError(e -> showError(R.string.title_dialog_error_saving_data, e))
                .doOnCompleted(
                        () -> {
                            if (currentPageFlows == flows.totalPaginas) {
                                setFlowDataAsImported(MainActivity.this);
                                requestCheckinData();
                            } else {
                                currentPageFlows++;
                                requestFlowData();
                            }
                        })
                .subscribe();
        compositeSubscription.add(subscription);
    }

    @SuppressWarnings("ConstantConditions")
    private void requestCheckinData() {
        final Subscription subscription = createService(CheckinService.class, this)
                .getAll(currentPageCheckins)
                .retryWhen(timeoutException())
                .retryWhen(exponentialBackoff(3, 5, SECONDS))
                .observeOn(mainThread())
                .subscribe(this::saveCheckinData, e -> showError(R.string.error_importing_data, e));
        compositeSubscription.add(subscription);
    }

    private void saveCheckinData(Checkins checkins) {
        final Subscription subscription = new CheckinRealmDataService(this)
                .saveAll(checkins.list)
                .observeOn(mainThread())
                .subscribeOn(io())
                .doOnError(e -> showError(R.string.title_dialog_error_saving_data, e))
                .doOnCompleted(
                        () -> {
                            if (currentPageCheckins == checkins.totalPaginas) {
                                setCheckinDataAsImported(MainActivity.this);
                                finishImportingData();
                            } else {
                                currentPageCheckins++;
                                requestCheckinData();
                            }
                        })
                .subscribe();
        compositeSubscription.add(subscription);
    }

    @SuppressWarnings("ConstantConditions")
    private void finishImportingData() {
        final Subscription subscription = createService(ConfigService.class, this)
                .get()
                .retryWhen(timeoutException())
                .retryWhen(exponentialBackoff(3, 5, SECONDS))
                .observeOn(mainThread())
                .subscribe(
                        c -> setDataImportationAsLastSyncDate(MainActivity.this, c.currentDate),
                        e -> showError(R.string.error_importing_data, e),
                        () -> {
                            setRequestedOrientation(SCREEN_ORIENTATION_UNSPECIFIED);
                            loadWorkData();
                        }
                );
        compositeSubscription.add(subscription);
    }

    private void showError(@StringRes int titleRes, Throwable e) {
        Timber.e(e, getString(titleRes));

        new MaterialDialog.Builder(MainActivity.this)
                .title(titleRes)
                .content(e.getMessage())
                .positiveText(R.string.text_dialog_button_ok)
                .show();
    }

    private void loadWorkData() {
        final Subscription subscription = getWorkDataService()
                .list()
                .observeOn(mainThread())
                .subscribeOn(io())
                .subscribe(
                        this::showWorkData,
                        e -> showError(R.string.title_dialog_error_loading_data_from_local, e)
                );
        compositeSubscription.add(subscription);
    }

    private WorkDataService getWorkDataService() {
        if (workDataService == null) {
            workDataService = new WorkRealmDataService(this);
        }
        return workDataService;
    }

    private void showWorkData(List<Work> list) {
        if (!list.isEmpty()) {
            workAdapter = new MainWorkAdapter(this, list, this::handleListItemClick);
            recyclerViewWorks.setAdapter(workAdapter);
            showEmptyView(false);
        } else {
            showEmptyDataState();
        }
        updateSubtitle();
    }

    public void handleListItemClick(View view) {
        final MainWorkAdapter.ViewHolder viewHolder = (MainWorkAdapter.ViewHolder) view.getTag();
        final Work work = viewHolder.work;
        NavigationHelper.navigateToFlowScreen(this, work);
    }

    private void showEmptyDataState() {
        showEmptyState(R.drawable.ic_no_works, R.string.title_no_data,
                R.string.tagline_no_data);
        // TODO: enable BroadcastReceiver to network events
    }

    private void updateSubtitle() {
        final int count = workAdapter != null ? workAdapter.getRunningWorksCount() : 0;
        if (count == 0) {
            setSubtitle(getString(R.string.no_work_running));
        } else {
            setSubtitle(getString(R.string.works_running,
                    count));
        }
        supportInvalidateOptionsMenu();
    }

    private void showNoDataAndNetworkState() {
        showEmptyState(R.drawable.ic_no_connection, R.string.title_no_connection,
                R.string.tagline_no_connection);
    }

    private void showEmptyState(@DrawableRes int image, @StringRes int title,
            @StringRes int tagline) {
        ButterKnife.<ImageView>findById(viewEmptyState, R.id.empty_image)
                .setImageResource(image);
        ButterKnife.<TextView>findById(viewEmptyState, R.id.empty_title)
                .setText(title);
        ButterKnife.<TextView>findById(viewEmptyState, R.id.empty_tagline)
                .setText(tagline);
        showEmptyView(true);
    }

    private void showEmptyView(boolean visible) {
        recyclerViewWorks.setVisibility(visible ? View.GONE : View.VISIBLE);
        viewEmptyState.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBusManager.register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setupSearchView(menu);
        return true;
    }

    private void setupSearchView(Menu menu) {
        SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(menu.findItem(R.id.menu_search));
        searchView.setQueryHint(getString(R.string.work_search_query_hint));
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (workAdapter != null) {
            workAdapter.getFilter().filter(newText);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return workAdapter != null && workAdapter.getItemCount() != 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                LoginDataHelper.logoutUser(this);
                finish();
                return true;

            case R.id.action_settings:
                NavigationHelper.navigateToSettingsScreen(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        changeListLayout(newConfig);
    }

    private void changeListLayout(Configuration configuration) {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerViewWorks.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerViewWorks.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBusManager.unregister(this);
        compositeSubscription.clear();
    }
}
