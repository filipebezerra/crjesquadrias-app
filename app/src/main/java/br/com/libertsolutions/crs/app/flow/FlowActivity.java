package br.com.libertsolutions.crs.app.flow;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.activity.BaseActivity;
import br.com.libertsolutions.crs.app.android.recyclerview.GridDividerDecoration;
import br.com.libertsolutions.crs.app.android.recyclerview.OnClickListener;
import br.com.libertsolutions.crs.app.android.recyclerview.OnTouchListener;
import br.com.libertsolutions.crs.app.sync.SyncService;
import br.com.libertsolutions.crs.app.sync.event.EventBusManager;
import br.com.libertsolutions.crs.app.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.sync.event.SyncStatus;
import br.com.libertsolutions.crs.app.sync.event.SyncType;
import br.com.libertsolutions.crs.app.utils.feedback.FeedbackHelper;
import br.com.libertsolutions.crs.app.utils.navigation.NavigationHelper;
import br.com.libertsolutions.crs.app.utils.network.NetworkUtil;
import butterknife.Bind;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Activity da interface de usuário da lista de {@link Flow}s.
 *
 * @author Filipe Bezerra
 * @version 0.2.0
 */
public class FlowActivity extends BaseActivity
        implements OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String EXTRA_WORK_ID = "workId";

    private Long mWorkId;

    private FlowAdapter mFlowAdapter;

    private FlowDataService mFlowDataService;

    private Subscription mFlowDataSubscription;

    @Bind(R.id.list) RecyclerView mWorkStepsView;
    @Bind(R.id.swipe_container) SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected int provideLayoutResource() {
        return R.layout.activity_flow;
    }

    @Override
    protected int provideUpIndicatorResource() {
        return R.drawable.ic_arrow_back_24dp;
    }

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        validateExtraWorkId();
        setupRecyclerView();
        setupSwipeRefreshLayout();
        loadFlowData();
    }

    private void validateExtraWorkId() {
        if (getIntent().hasExtra(EXTRA_WORK_ID)) {
            mWorkId = getIntent().getLongExtra(EXTRA_WORK_ID, INVALID_EXTRA_ID);

            if (mWorkId == INVALID_EXTRA_ID) {
                throw new IllegalArgumentException(
                        "You need to set a valid workId as long type");
            }
        } else {
            Toast.makeText(getApplicationContext(), "Developer, you need to use the method "
                    + "NavigationHelper.navigateToFlowScreen() passing the "
                    + "Work ID in the second parameter", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupRecyclerView() {
        changeListLayout(getResources().getConfiguration());
        mWorkStepsView.setHasFixedSize(true);
        mWorkStepsView.addItemDecoration(new GridDividerDecoration(this));
        mWorkStepsView.addOnItemTouchListener(new OnTouchListener(this, mWorkStepsView, this));
    }

    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
    }

    private void loadFlowData() {
        if (mFlowDataService == null) {
            mFlowDataService = new FlowRealmDataService(this);
        }

        mFlowDataSubscription = mFlowDataService.list(mWorkId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::showFlowData,

                        e -> showError(R.string.title_dialog_error_loading_data_from_local, e)
                );
    }

    private void showFlowData(List<Flow> list) {
        if (mFlowAdapter == null) {
            mWorkStepsView.setAdapter(mFlowAdapter = new FlowAdapter(FlowActivity.this, list));
        } else {
            mFlowAdapter.swapData(list);
        }
        updateSubtitle();
    }

    private void updateSubtitle() {
        if (mFlowAdapter != null) {
            final int count = mFlowAdapter.getRunningFlowsCount();
            if (count == 0) {
                setSubtitle(getString(R.string.no_work_step_running));
            } else {
                setSubtitle(getString(R.string.work_steps_running,
                        count));
            }
        }
    }

    private void showError(@StringRes int titleRes, Throwable e) {
        Crashlytics.logException(e);

        new MaterialDialog.Builder(FlowActivity.this)
                .title(titleRes)
                .content(e.getMessage())
                .positiveText(R.string.text_dialog_button_ok)
                .show();
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
        if (mFlowDataSubscription != null && mFlowDataSubscription.isUnsubscribed()) {
            mFlowDataSubscription.unsubscribe();
        }
    }

    @Override
    public void onSingleTapUp(View view, int position) {
        if (mFlowAdapter != null) {
            final Flow item = mFlowAdapter.getItem(position);

            if (item != null) {
                NavigationHelper.navigateToCheckinScreen(this, item.getFlowId());
            }
        }
    }

    @Override
    public void onLongPress(View view, int position) {}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        changeListLayout(newConfig);
    }

    private void changeListLayout(Configuration configuration) {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mWorkStepsView.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            mWorkStepsView.setLayoutManager(new LinearLayoutManager(this));
        }
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
            if (!mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(true);
        } else if (event.getType() == SyncType.FLOWS) {
            Timber.i("Sync completed");

            if (mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false);

            loadFlowData();
        }
    }
}
