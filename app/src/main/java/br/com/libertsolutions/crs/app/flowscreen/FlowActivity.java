package br.com.libertsolutions.crs.app.flowscreen;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.activity.BaseActivity;
import br.com.libertsolutions.crs.app.android.recyclerview.OnClickListener;
import br.com.libertsolutions.crs.app.android.recyclerview.OnTouchListener;
import br.com.libertsolutions.crs.app.flow.Flow;
import br.com.libertsolutions.crs.app.flow.FlowDataService;
import br.com.libertsolutions.crs.app.flow.FlowRealmDataService;
import br.com.libertsolutions.crs.app.sync.SyncService;
import br.com.libertsolutions.crs.app.sync.event.EventBusManager;
import br.com.libertsolutions.crs.app.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.sync.event.SyncStatus;
import br.com.libertsolutions.crs.app.utils.feedback.FeedbackHelper;
import br.com.libertsolutions.crs.app.utils.navigation.NavigationHelper;
import br.com.libertsolutions.crs.app.utils.network.NetworkUtil;
import br.com.libertsolutions.crs.app.work.Work;
import butterknife.BindView;
import com.afollestad.materialdialogs.MaterialDialog;
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
 * @since 0.1.0
 */
public class FlowActivity extends BaseActivity
        implements OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String EXTRA_WORK = "work";

    private Work mWork;

    private FlowAdapter mFlowAdapter;

    private FlowDataService mFlowDataService;

    private Subscription mFlowDataSubscription;

    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.job_title) TextView mJobTitleView;
    @BindView(R.id.code_subtitle) TextView mCodeSubtitleView;
    @BindView(R.id.work_steps_running) TextView mWorkStepsRunningView;
    @BindView(R.id.list) RecyclerView mWorkStepsView;
    @BindView(R.id.swipe_container) SwipeRefreshLayout mSwipeRefreshLayout;

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
        initTitleAppBar();
        setupRecyclerView();
        setupSwipeRefreshLayout();
    }

    private void validateExtraWorkId() {
        if (getIntent().hasExtra(EXTRA_WORK)) {
            mWork = getIntent().getParcelableExtra(EXTRA_WORK);

            if (mWork == null) {
                throw new IllegalArgumentException(
                        "You need to set a valid br.com.libertsolutions.crs.app.work.Work "
                                + "instance as android.os.Parcelable");
            }
        } else {
            Toast.makeText(getApplicationContext(), "Developer, you need to use the method "
                    + "NavigationHelper.navigateToFlowScreen() passing the "
                    + "br.com.libertsolutions.crs.app.work.Work instance as android.os.Parcelable "
                    + "in the second parameter", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initTitleAppBar() {
        setTitle(mWork.getJob());

        mCollapsingToolbarLayout.setExpandedTitleColor(
                ContextCompat.getColor(this, android.R.color.transparent));

        mJobTitleView.setText(mWork.getJob());
        mCodeSubtitleView.setText(mWork.getCode());
    }

    private void setupRecyclerView() {
        changeListLayout(getResources().getConfiguration());
        mWorkStepsView.setHasFixedSize(true);
        mWorkStepsView.addOnItemTouchListener(new OnTouchListener(this, mWorkStepsView, this));
    }

    @Override
    public void onSingleTapUp(View view, int position) {
        if (mFlowAdapter != null) {
            final Flow item = mFlowAdapter.getItem(position);

            if (item != null) {
                NavigationHelper.navigateToCheckinScreen(this, item);
            }
        }
    }

    @Override
    public void onLongPress(View view, int position) {}

    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
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
            Timber.i("Sync in progress");
            if (!mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(true);
        } else {
            Timber.i("Sync completed");
            if (mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false);

            loadFlowData();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBusManager.register(this);
        loadFlowData();
    }

    private void loadFlowData() {
        if (mFlowDataService == null) {
            mFlowDataService = new FlowRealmDataService(this);
        }

        mFlowDataSubscription = mFlowDataService.list(mWork.getWorkId())
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
        updateTitleAppBar();
    }

    private void updateTitleAppBar() {
        if (mFlowAdapter != null) {
            final int count = mFlowAdapter.getRunningFlowsCount();
            if (count == 0) {
                mWorkStepsRunningView.setText(getString(R.string.no_work_step_running));
            } else {
                mWorkStepsRunningView.setText(getString(R.string.work_steps_running, count));
            }
        }
    }

    private void showError(@StringRes int titleRes, Throwable e) {
        Timber.e(e, getString(titleRes));

        new MaterialDialog.Builder(FlowActivity.this)
                .title(titleRes)
                .content(e.getMessage())
                .positiveText(R.string.text_dialog_button_ok)
                .show();
    }

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
    protected void onStop() {
        super.onStop();
        EventBusManager.unregister(this);
        if (mFlowDataSubscription != null && mFlowDataSubscription.isUnsubscribed()) {
            mFlowDataSubscription.unsubscribe();
        }
    }
}
