package br.com.libertsolutions.crs.app.presentation.checkin;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.presentation.activity.BaseActivity;
import br.com.libertsolutions.crs.app.domain.pojo.Checkin;
import br.com.libertsolutions.crs.app.data.checkin.CheckinDataService;
import br.com.libertsolutions.crs.app.data.checkin.CheckinRealmDataService;
import br.com.libertsolutions.crs.app.domain.pojo.Flow;
import br.com.libertsolutions.crs.app.data.sync.SyncService;
import br.com.libertsolutions.crs.app.data.sync.event.EventBusManager;
import br.com.libertsolutions.crs.app.data.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.data.sync.event.SyncStatus;
import br.com.libertsolutions.crs.app.presentation.util.FeedbackHelper;
import br.com.libertsolutions.crs.app.presentation.util.NetworkUtils;
import butterknife.BindView;
import com.afollestad.materialdialogs.MaterialDialog;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class CheckinActivity extends BaseActivity
        implements CheckinAdapter.CheckinCallback, SwipeRefreshLayout.OnRefreshListener,
        SearchView.OnQueryTextListener {

    public static final String EXTRA_FLOW = "flow";

    private Flow mFlow;

    private CheckinAdapter mCheckinAdapter;

    private CheckinDataService mCheckinDataService;

    private Subscription mCheckinDataSubscription;

    private MenuItem mSearchMenuItem;

    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.flow_name) TextView mFlowNameTitleView;
    @BindView(R.id.checkins_finished) TextView mCheckinsFinishedView;
    @BindView(R.id.list) RecyclerView mCheckinsView;
    @BindView(R.id.swipe_container) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.empty_view) LinearLayout mEmptyStateView;

    @Override
    protected int provideLayoutResource() {
        return R.layout.activity_checkin;
    }

    @Override
    protected int provideUpIndicatorResource() {
        return R.drawable.ic_arrow_back_24dp;
    }

    @Override
    protected int provideMenuResource() {
        return R.menu.menu_checkin;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validateExtraFlowId();
        initTitleAppBar();
        setupRecyclerView();
        setupSwipeRefreshLayout();
    }

    private void validateExtraFlowId() {
        if (getIntent().hasExtra(EXTRA_FLOW)) {
            mFlow = getIntent().getParcelableExtra(EXTRA_FLOW);

            if (mFlow == null) {
                throw new IllegalArgumentException(
                        "You need to set a valid br.com.libertsolutions.crs.app.domain.pojo.Flow "
                                + "instance as android.os.Parcelable");
            }
        } else {
            Toast.makeText(getApplicationContext(), "Developer, you need to use the method "
                    + "NavigationHelper.navigateToCheckinScreen() passing the "
                    + "br.com.libertsolutions.crs.app.domain.pojo.Flow instance as android.os.Parcelable "
                    + "in the second parameter", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initTitleAppBar() {
        setTitle(getString(R.string.title_activity_checkin));

        mCollapsingToolbarLayout.setExpandedTitleColor(
                ContextCompat.getColor(this, android.R.color.transparent));

        mFlowNameTitleView.setText(mFlow.getStep().getName());
    }

    private void setupRecyclerView() {
        changeListLayout(getResources().getConfiguration());
        mCheckinsView.setHasFixedSize(true);
    }

    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
    }

    @Override
    public void onRefresh() {
        if (NetworkUtils.isDeviceConnectedToInternet(this)) {
            SyncService.requestCompleteSync();
        } else {
            FeedbackHelper.toast(this, getString(R.string.no_connection_to_force_update), false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncEvent(SyncEvent event) {
        Timber.i("Sync event with %s in %s", event.getType(), event.getStatus());

        if (event.getStatus() == SyncStatus.IN_PROGRESS) {
            Timber.i("Sync in progress");
            if (!mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(true);
        } else {
            Timber.i("Sync completed");
            if (mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false);

            loadCheckinData();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBusManager.register(this);
        loadCheckinData();
    }

    private void loadCheckinData() {
        mCheckinDataSubscription = getCheckinDataService().list(mFlow.getFlowId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::showCheckinData,

                        e -> showError(R.string.title_dialog_error_loading_data_from_local, e)
                );
    }

    private void showCheckinData(List<Checkin> list) {
        if (!list.isEmpty()) {
            mCheckinsView.setAdapter(
                    mCheckinAdapter = new CheckinAdapter(CheckinActivity.this, list));
            mCheckinAdapter.setCheckinCallback(CheckinActivity.this);
            showEmptyView(false);
        }

        updateSubtitle();
        collapseSearchView();
    }

    private void collapseSearchView() {
        if (mSearchMenuItem != null && mSearchMenuItem.isActionViewExpanded()) {
            mSearchMenuItem.collapseActionView();
        }
    }

    @Override
    public void onCheckinDone(Checkin checkin) {
        updateSubtitle();
        setToBeSynced(checkin);
    }

    private void setToBeSynced(Checkin checkin) {
        if (checkin == null) {
            return;
        }

        getCheckinDataService().updateSyncState(checkin, true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        ignored -> {},

                        e -> showError(R.string.title_dialog_error_saving_data, e)
                );
    }

    @Override
    public void onCheckinsAllDone(List<Checkin> checkinsUpdated) {
        updateSubtitle();
        setAllToBeSynced(checkinsUpdated);
    }

    private void updateSubtitle() {
        if (!hasCheckinData()) {
            showEmptyView(true);
        } else {
            final int finishedCount = mCheckinAdapter.getFinishedCheckinsCount();
            if (finishedCount == 0) {
                mCheckinsFinishedView.setText(getString(R.string.no_checkin_finished));
            } else {
                final int itemCount = mCheckinAdapter.getItemCount();

                if (itemCount == finishedCount) {
                    mCheckinsFinishedView.setText(getString(R.string.checkins_all_finished));
                } else {
                    mCheckinsFinishedView.setText(getString(R.string.checkins_finished,
                            finishedCount));
                }
            }
        }
        supportInvalidateOptionsMenu();
    }

    private void showEmptyView(boolean visible) {
        mCheckinsView.setVisibility(visible ? View.GONE : View.VISIBLE);
        mEmptyStateView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setAllToBeSynced(List<Checkin> checkins) {
        if (checkins == null || checkins.isEmpty()) {
            return;
        }

        getCheckinDataService().updateSyncState(checkins, true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        ignored -> {},

                        e -> showError(R.string.title_dialog_error_saving_data, e)
                );
    }

    private CheckinDataService getCheckinDataService() {
        if (mCheckinDataService == null) {
            mCheckinDataService = new CheckinRealmDataService(this);
        }
        return mCheckinDataService;
    }

    private void showError(int titleRes, Throwable e) {
        Timber.e(e, getString(titleRes));

        new MaterialDialog.Builder(this)
                .title(titleRes)
                .content(e.getMessage())
                .positiveText(R.string.text_dialog_button_ok)
                .show();
    }

    @Override
    public void onCheckinsAlreadyDone() {
        FeedbackHelper.snackbar(mRootView, getString(R.string.checkins_already_done), true);
    }

    @Override
    public void onStatusCannotChange() {
        FeedbackHelper.snackbar(mRootView, getString(R.string.checkins_status_cannot_change), true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean hasOptionsMenu = super.onCreateOptionsMenu(menu);
        setupSearchView(menu);
        return hasOptionsMenu;
    }

    private void setupSearchView(Menu menu) {
        mSearchMenuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);
        searchView.setQueryHint(getString(R.string.checkin_search_query_hint));
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (mCheckinAdapter != null) {
            mCheckinAdapter.getFilter().filter(newText);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final boolean menusEnabled = hasCheckinData();
        menu.findItem(R.id.menu_search).setVisible(menusEnabled);
        menu.findItem(R.id.action_check_all).setVisible(menusEnabled);
        return true;
    }

    private boolean hasCheckinData() {
        return mCheckinAdapter != null && mCheckinAdapter.getItemCount() > 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_check_all) {
            mCheckinAdapter.checkAllDone();
            return true;
        } else {
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
            mCheckinsView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mCheckinsView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBusManager.unregister(this);
        if (mCheckinDataSubscription != null && mCheckinDataSubscription.isUnsubscribed()) {
            mCheckinDataSubscription.unsubscribe();
        }
    }
}