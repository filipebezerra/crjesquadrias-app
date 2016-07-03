package br.com.libertsolutions.crs.app.checkinscreen;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.activity.BaseActivity;
import br.com.libertsolutions.crs.app.android.recyclerview.DividerDecoration;
import br.com.libertsolutions.crs.app.checkin.Checkin;
import br.com.libertsolutions.crs.app.checkin.CheckinDataService;
import br.com.libertsolutions.crs.app.checkin.CheckinRealmDataService;
import br.com.libertsolutions.crs.app.sync.SyncService;
import br.com.libertsolutions.crs.app.sync.event.EventBusManager;
import br.com.libertsolutions.crs.app.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.sync.event.SyncStatus;
import br.com.libertsolutions.crs.app.sync.event.SyncType;
import br.com.libertsolutions.crs.app.utils.feedback.FeedbackHelper;
import br.com.libertsolutions.crs.app.utils.network.NetworkUtil;
import butterknife.BindView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Activity da interface de usuário da lista de {@link Checkin}s.
 *
 * @author Filipe Bezerra
 * @version 0.2.0
 * @since 0.1.0
 */
public class CheckinActivity extends BaseActivity
        implements CheckinAdapter.CheckinCallback, SwipeRefreshLayout.OnRefreshListener,
        SearchView.OnQueryTextListener {

    public static final String EXTRA_FLOW_ID = "flowId";

    private Long mFlowId;

    private CheckinAdapter mCheckinAdapter;

    private CheckinDataService mCheckinDataService;

    private Subscription mCheckinDataSubscription;

    private MenuItem mSearchMenuItem;

    @BindView(R.id.list) RecyclerView mCheckinsView;
    @BindView(R.id.swipe_container) SwipeRefreshLayout mSwipeRefreshLayout;

    private void showError(int titleRes, Throwable e) {
        Crashlytics.logException(e);

        new MaterialDialog.Builder(this)
                .title(titleRes)
                .content(e.getMessage())
                .positiveText(R.string.text_dialog_button_ok)
                .show();
    }

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
        setupRecyclerView();
        setupSwipeRefreshLayout();
        loadCheckinData();
    }

    private void validateExtraFlowId() {
        if (getIntent().hasExtra(EXTRA_FLOW_ID)) {
            mFlowId = getIntent().getLongExtra(EXTRA_FLOW_ID, 0);
        } else {
            Toast.makeText(getApplicationContext(), "Developer, you need to use the method "
                    + "NavigationHelper.navigateToCheckinScreen() passing the "
                    + "Flow ID in the second parameter", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupRecyclerView() {
        mCheckinsView.setLayoutManager(new LinearLayoutManager(this));
        mCheckinsView.setHasFixedSize(true);
        mCheckinsView.addItemDecoration(new DividerDecoration(this));
    }

    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
    }

    private void loadCheckinData() {
        mCheckinDataSubscription = getCheckinDataService().list(mFlowId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::showCheckinData,

                        e -> showError(R.string.title_dialog_error_loading_data_from_local, e)
                );
    }

    private void showCheckinData(List<Checkin> list) {
        mCheckinsView.setAdapter(
                mCheckinAdapter = new CheckinAdapter(CheckinActivity.this, list));
        mCheckinAdapter.setCheckinCallback(CheckinActivity.this);
        updateSubtitle();
        collapseSearchView();
    }

    private void collapseSearchView() {
        if (mSearchMenuItem != null && mSearchMenuItem.isActionViewExpanded()) {
            mSearchMenuItem.collapseActionView();
        }
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
    protected void onStart() {
        super.onStart();
        EventBusManager.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBusManager.unregister(this);
        if (mCheckinDataSubscription != null && mCheckinDataSubscription.isUnsubscribed()) {
            mCheckinDataSubscription.unsubscribe();
        }
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

    @Override
    public void onCheckinsAlreadyDone() {
        FeedbackHelper.snackbar(mRootView, getString(R.string.checkins_already_done), true);
    }

    @Override
    public void onStatusCannotChange() {
        FeedbackHelper.snackbar(mRootView, getString(R.string.checkins_status_cannot_change), true);
    }

    private void updateSubtitle() {
        if (mCheckinAdapter != null) {
            final int finishedCount = mCheckinAdapter.getFinishedCheckinsCount();
            if (finishedCount == 0) {
                setSubtitle(getString(R.string.no_checkin_finished));
            } else {
                final int itemCount = mCheckinAdapter.getItemCount();

                if (itemCount == finishedCount) {
                    setSubtitle(getString(R.string.checkins_all_finished));
                } else {
                    setSubtitle(getString(R.string.checkins_finished, finishedCount));
                }
            }
        }
    }

    private CheckinDataService getCheckinDataService() {
        if (mCheckinDataService == null) {
            mCheckinDataService = new CheckinRealmDataService(this);
        }
        return mCheckinDataService;
    }

    @Override
    public void onRefresh() {
        if (NetworkUtil.isDeviceConnectedToInternet(this)) {
            SyncService.requestCompleteSync();
        } else {
            FeedbackHelper.toast(this, getString(R.string.no_connection_to_force_update), false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncEvent(SyncEvent event) {
        Timber.i("Sync event with %s in %s", event.getType(), event.getStatus());

        if (event.getStatus() == SyncStatus.IN_PROGRESS) {
            if (!mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        } else {
            Timber.i("Sync completed");

            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }

            if (event.getType() == SyncType.CHECKINS) {
                loadCheckinData();
            }
        }
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
}