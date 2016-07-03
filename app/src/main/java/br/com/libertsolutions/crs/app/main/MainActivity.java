package br.com.libertsolutions.crs.app.main;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
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

import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.activity.BaseActivity;
import br.com.libertsolutions.crs.app.android.recyclerview.GridDividerDecoration;
import br.com.libertsolutions.crs.app.android.recyclerview.OnClickListener;
import br.com.libertsolutions.crs.app.android.recyclerview.OnTouchListener;
import br.com.libertsolutions.crs.app.checkin.Checkin;
import br.com.libertsolutions.crs.app.checkin.CheckinRealmDataService;
import br.com.libertsolutions.crs.app.checkin.CheckinService;
import br.com.libertsolutions.crs.app.config.ConfigHelper;
import br.com.libertsolutions.crs.app.config.ConfigService;
import br.com.libertsolutions.crs.app.flow.Flow;
import br.com.libertsolutions.crs.app.flow.FlowRealmDataService;
import br.com.libertsolutions.crs.app.flow.FlowService;
import br.com.libertsolutions.crs.app.login.LoginHelper;
import br.com.libertsolutions.crs.app.login.User;
import br.com.libertsolutions.crs.app.sync.SyncService;
import br.com.libertsolutions.crs.app.sync.event.EventBusManager;
import br.com.libertsolutions.crs.app.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.sync.event.SyncStatus;
import br.com.libertsolutions.crs.app.sync.event.SyncType;
import br.com.libertsolutions.crs.app.utils.drawable.DrawableHelper;
import br.com.libertsolutions.crs.app.utils.feedback.FeedbackHelper;
import br.com.libertsolutions.crs.app.utils.navigation.NavigationHelper;
import br.com.libertsolutions.crs.app.utils.network.NetworkUtil;
import br.com.libertsolutions.crs.app.utils.rx.RxUtil;
import br.com.libertsolutions.crs.app.utils.webservice.ServiceGenerator;
import br.com.libertsolutions.crs.app.work.Work;
import br.com.libertsolutions.crs.app.work.WorkAdapter;
import br.com.libertsolutions.crs.app.work.WorkDataService;
import br.com.libertsolutions.crs.app.work.WorkRealmDataService;
import br.com.libertsolutions.crs.app.work.WorkService;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Activity da interface de usuário da lista de {@link Work}s.
 *
 * @author Filipe Bezerra
 * @since 0.2.0
 * @since 0.1.0
 */
public class MainActivity extends BaseActivity implements OnClickListener,
        SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private WorkAdapter mWorkAdapter;

    private WorkDataService mWorkDataService;

    private CompositeSubscription mCompositeSubscription;

    private User mUserLogged;

    @Bind(R.id.list) RecyclerView mWorksView;
    @Bind(R.id.empty_state) LinearLayout mEmptyStateView;
    @Bind(R.id.swipe_container) SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected int provideLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setupActionBar();
        setupRecyclerView();
        setupSwipeRefreshLayout();
    }

    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mCompositeSubscription = new CompositeSubscription();
        loadViewData();
    }

    private void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_main);
        }

        if (mToolbarAsActionBar != null) {
            final Drawable navigationIcon = DrawableHelper.withContext(this)
                    .withColor(R.color.white)
                    .withDrawable(R.drawable.ic_worker)
                    .tint()
                    .get();

            mToolbarAsActionBar.setNavigationIcon(navigationIcon);
        }
    }

    private void setupRecyclerView() {
        changeListLayout(getResources().getConfiguration());
        mWorksView.addItemDecoration(new GridDividerDecoration(this));
        mWorksView.setHasFixedSize(true);
        mWorksView.addOnItemTouchListener(new OnTouchListener(this, mWorksView, this));
    }

    private void loadViewData() {
        showUserLoggedInfo();

        final boolean isInitialDataImported = ConfigHelper.isInitialDataImported(this);

        if (isInitialDataImported) {
            loadWorkData();
            Timber.i("Loading view data, requesting complete sync");
            requestCompleteSync();
        } else if (NetworkUtil.isDeviceConnectedToInternet(this)) {
            startImportingData();
        } else {
            showNoDataAndNetworkState();
        }
    }

    private void requestCompleteSync() {
        SyncService.requestCompleteSync();
        Timber.i("Complete sync requested");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncEvent(SyncEvent event) {
        Timber.i("Sync event with %s in %s", event.getType(), event.getStatus());

        if (event.getStatus() == SyncStatus.IN_PROGRESS) {
            if (!mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(true);
        } else {
            if (mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(false);

            if (event.getType() == SyncType.WORKS) {
                Timber.i("Sync completed");
                loadWorkData();
            }
        }
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

    private void showNoDataAndNetworkState() {
        showEmptyState(R.drawable.ic_no_connection, R.string.title_no_connection,
                R.string.tagline_no_connection);
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

    private void showUserLoggedInfo() {
        if (mUserLogged == null) {
            mUserLogged = LoginHelper.getUserLogged(this);
        }

        if (mUserLogged != null) {
            FeedbackHelper
                    .snackbar(mRootView, String.format("Logado como %s.",
                            LoginHelper.formatCpf(mUserLogged.getName())), false);
        }
    }

    private WorkDataService getWorkDataService() {
        if (mWorkDataService == null) {
            mWorkDataService = new WorkRealmDataService(this);
        }
        return mWorkDataService;
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

    private void showError(@StringRes int titleRes, Throwable e) {
        Timber.e(e, getString(titleRes));

        new MaterialDialog.Builder(MainActivity.this)
                .title(titleRes)
                .content(e.getMessage())
                .positiveText(R.string.text_dialog_button_ok)
                .show();
    }

    private void showWorkData(List<Work> list) {
        if (!list.isEmpty()) {
            mWorksView.setAdapter(mWorkAdapter = new WorkAdapter(MainActivity.this, list));
            showEmptyView(false);
        } else {
            showEmptyDataState();
        }
        updateSubtitle();
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

    @Override
    protected void onStart() {
        super.onStart();
        EventBusManager.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBusManager.unregister(this);
        mCompositeSubscription.clear();
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        return mWorkAdapter != null && mWorkAdapter.getItemCount() != 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                LoginHelper.logoutUser(this);
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
    public void onSingleTapUp(View view, int position) {
        if (mWorkAdapter != null) {
            final Work item = mWorkAdapter.getItem(position);

            if (item != null) {
                NavigationHelper.navigateToFlowScreen(this, item.getWorkId());
            }
        }
    }

    @Override
    public void onLongPress(View view, int position) {}

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
                        l -> ConfigHelper.setWorkDataAsImported(MainActivity.this))
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
                        d -> ConfigHelper.setFlowDataAsImported(MainActivity.this))
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
                        d -> ConfigHelper.setCheckinDataAsImported(MainActivity.this))
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
                        c -> ConfigHelper.setDataImportationAsLastSyncDate(
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

    @Override
    public void onRefresh() {
        if (NetworkUtil.isDeviceConnectedToInternet(this)) {
            requestCompleteSync();
        } else {
            FeedbackHelper.toast(this, getString(R.string.no_connection_to_force_update), false);
        }
    }
}
