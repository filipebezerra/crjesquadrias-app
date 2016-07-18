package br.com.libertsolutions.crs.app.presentation.main;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.presentation.activity.BaseActivity;
import br.com.libertsolutions.crs.app.domain.pojo.Checkin;
import br.com.libertsolutions.crs.app.data.checkin.CheckinRealmDataService;
import br.com.libertsolutions.crs.app.data.checkin.CheckinService;
import br.com.libertsolutions.crs.app.data.config.ConfigDataHelper;
import br.com.libertsolutions.crs.app.data.config.ConfigService;
import br.com.libertsolutions.crs.app.domain.pojo.Flow;
import br.com.libertsolutions.crs.app.data.flow.FlowRealmDataService;
import br.com.libertsolutions.crs.app.data.flow.FlowService;
import br.com.libertsolutions.crs.app.data.login.LoginDataHelper;
import br.com.libertsolutions.crs.app.domain.pojo.User;
import br.com.libertsolutions.crs.app.data.sync.SyncService;
import br.com.libertsolutions.crs.app.data.sync.event.EventBusManager;
import br.com.libertsolutions.crs.app.data.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.data.sync.event.SyncStatus;
import br.com.libertsolutions.crs.app.presentation.util.FeedbackHelper;
import br.com.libertsolutions.crs.app.presentation.util.NavigationHelper;
import br.com.libertsolutions.crs.app.presentation.util.NetworkUtils;
import br.com.libertsolutions.crs.app.data.util.RxUtil;
import br.com.libertsolutions.crs.app.data.util.ServiceGenerator;
import br.com.libertsolutions.crs.app.domain.pojo.Work;
import br.com.libertsolutions.crs.app.data.work.WorkDataService;
import br.com.libertsolutions.crs.app.data.work.WorkRealmDataService;
import br.com.libertsolutions.crs.app.data.work.WorkService;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.MaterialDialog;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class MainActivity extends BaseActivity
        implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private MainWorkAdapter mWorkAdapter;

    private WorkDataService mWorkDataService;

    private CompositeSubscription mCompositeSubscription;

    private User mUserLogged;

    @BindView(R.id.list) RecyclerView mWorksView;
    @BindView(R.id.empty_state) LinearLayout mEmptyStateView;
    @BindView(R.id.swipe_container) SwipeRefreshLayout mSwipeRefreshLayout;

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
        mCompositeSubscription = new CompositeSubscription();
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
        mWorksView.setHasFixedSize(true);
    }

    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncEvent(SyncEvent event) {
        Timber.i("Sync event with %s in %s", event.getType(), event.getStatus());

        if (event.getStatus() == SyncStatus.IN_PROGRESS) {
            Timber.i("Sync in progress");
            if (!mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        } else {
            Timber.i("Sync completed");
            stopRefreshingProgress();
            loadWorkData();
        }
    }

    private void stopRefreshingProgress() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void startImportingData() {
        showImportingDataState();
        requestWorkData();
    }

    private void showImportingDataState() {
        showEmptyState(R.drawable.ic_import_state, R.string.title_importing_data,
                R.string.tagline_importing_data);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @SuppressWarnings("ConstantConditions")
    private void requestWorkData() {
        final Subscription subscription = ServiceGenerator
                .createService(WorkService.class, this)
                .getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(
                        e -> showError(R.string.error_importing_data, e))
                .retryWhen(
                        RxUtil.timeoutException())
                .retryWhen(
                        RxUtil.exponentialBackoff(3, 5, TimeUnit.SECONDS))
                .doOnNext(
                        this::saveWorkData)
                .subscribe();
        mCompositeSubscription.add(subscription);
    }

    @SuppressWarnings("ConstantConditions")
    private void saveWorkData(List<Work> works) {
        if (works.isEmpty()) {
            showEmptyView(true);
            return;
        }

        final Subscription subscription = getWorkDataService()
                .saveAll(works)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnError(
                        e -> showError(R.string.title_dialog_error_saving_data, e))
                .doOnNext(
                        l -> ConfigDataHelper.setWorkDataAsImported(MainActivity.this))
                .doOnCompleted(
                        this::requestFlowData)
                .subscribe();
        mCompositeSubscription.add(subscription);
    }

    @SuppressWarnings("ConstantConditions")
    private void requestFlowData() {
        final Subscription subscription = ServiceGenerator
                .createService(FlowService.class, this)
                .getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(
                        e -> showError(R.string.error_importing_data, e))
                .retryWhen(
                        RxUtil.timeoutException())
                .retryWhen(
                        RxUtil.exponentialBackoff(3, 5, TimeUnit.SECONDS))
                .doOnNext(
                        this::saveFlowData)
                .subscribe();
        mCompositeSubscription.add(subscription);
    }

    private void saveFlowData(List<Flow> flows) {
        final Subscription subscription = new FlowRealmDataService(this)
                .saveAll(flows)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnError(
                        e -> showError(R.string.title_dialog_error_saving_data, e))
                .doOnNext(
                        d -> ConfigDataHelper.setFlowDataAsImported(MainActivity.this))
                .doOnCompleted(
                        this::requestCheckinData)
                .subscribe();
        mCompositeSubscription.add(subscription);
    }

    @SuppressWarnings("ConstantConditions")
    private void requestCheckinData() {
        final Subscription subscription = ServiceGenerator
                .createService(CheckinService.class, this)
                .getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(
                        e -> showError(R.string.error_importing_data, e))
                .retryWhen(
                        RxUtil.timeoutException())
                .retryWhen(
                        RxUtil.exponentialBackoff(3, 5, TimeUnit.SECONDS))
                .doOnNext(
                        this::saveCheckinData)
                .subscribe();
        mCompositeSubscription.add(subscription);
    }

    private void saveCheckinData(List<Checkin> checkins) {
        final Subscription subscription = new CheckinRealmDataService(this)
                .saveAll(checkins)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnError(
                        e -> showError(R.string.title_dialog_error_saving_data, e))
                .doOnNext(
                        d -> ConfigDataHelper.setCheckinDataAsImported(MainActivity.this))
                .doOnCompleted(
                        this::finishImportingData)
                .subscribe();
        mCompositeSubscription.add(subscription);
    }

    @SuppressWarnings("ConstantConditions")
    private void finishImportingData() {
        final Subscription subscription = ServiceGenerator
                .createService(ConfigService.class, this)
                .get()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(
                        e -> showError(R.string.error_importing_data, e))
                .retryWhen(
                        RxUtil.timeoutException())
                .retryWhen(
                        RxUtil.exponentialBackoff(3, 5, TimeUnit.SECONDS))
                .doOnNext(
                        c -> ConfigDataHelper.setDataImportationAsLastSyncDate(
                                MainActivity.this, c.getDataAtual()))
                .doOnCompleted(
                        () -> {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                            loadWorkData();
                        }
                )
                .subscribe();
        mCompositeSubscription.add(subscription);
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnError(
                        e -> showError(R.string.title_dialog_error_loading_data_from_local, e))
                .doOnNext(
                        this::showWorkData)
                .subscribe();
        mCompositeSubscription.add(subscription);
    }

    private WorkDataService getWorkDataService() {
        if (mWorkDataService == null) {
            mWorkDataService = new WorkRealmDataService(this);
        }
        return mWorkDataService;
    }

    private void showWorkData(List<Work> list) {
        if (!list.isEmpty()) {
            mWorkAdapter = new MainWorkAdapter(MainActivity.this, list, this::handleListItemClick);
            mWorksView.setAdapter(mWorkAdapter);
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
        final int count = mWorkAdapter != null ? mWorkAdapter.getRunningWorksCount() : 0;
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
        ButterKnife.<ImageView>findById(mEmptyStateView, R.id.empty_image)
                .setImageResource(image);
        ButterKnife.<TextView>findById(mEmptyStateView, R.id.empty_title)
                .setText(title);
        ButterKnife.<TextView>findById(mEmptyStateView, R.id.empty_tagline)
                .setText(tagline);
        showEmptyView(true);
    }

    private void showEmptyView(boolean visible) {
        mWorksView.setVisibility(visible ? View.GONE : View.VISIBLE);
        mEmptyStateView.setVisibility(visible ? View.VISIBLE : View.GONE);
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
        if (mWorkAdapter != null) {
            mWorkAdapter.getFilter().filter(newText);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return mWorkAdapter != null && mWorkAdapter.getItemCount() != 0;
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
            mWorksView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mWorksView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBusManager.unregister(this);
        mCompositeSubscription.clear();
    }
}
