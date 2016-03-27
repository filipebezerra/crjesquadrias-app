package br.com.libertsolutions.crs.app.flow;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.activity.BaseActivity;
import br.com.libertsolutions.crs.app.android.recyclerview.GridDividerDecoration;
import br.com.libertsolutions.crs.app.android.recyclerview.OnClickListener;
import br.com.libertsolutions.crs.app.android.recyclerview.OnTouchListener;
import br.com.libertsolutions.crs.app.feedback.FeedbackHelper;
import br.com.libertsolutions.crs.app.navigation.NavigationHelper;
import br.com.libertsolutions.crs.app.network.NetworkUtil;
import br.com.libertsolutions.crs.app.retrofit.RetrofitHelper;
import br.com.libertsolutions.crs.app.settings.SettingsHelper;
import butterknife.Bind;
import com.afollestad.materialdialogs.MaterialDialog;
import java.util.List;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 27/03/2016
 * @since 0.1.0
 */
public class FlowActivity extends BaseActivity implements OnClickListener {

    public static final String EXTRA_WORK_ID = "workId";

    private Long mWorkId;

    private FlowAdapter mFlowAdapter;

    private FlowDataService mFlowDataService;

    private CompositeSubscription mCompositeSubscription;
    
    @Bind(android.R.id.list) RecyclerView mWorkStepsView;

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

        if (getIntent().hasExtra(EXTRA_WORK_ID)) {
            mWorkId = getIntent().getLongExtra(EXTRA_WORK_ID, INVALID_EXTRA_ID);

            if (mWorkId == INVALID_EXTRA_ID) {
                throw new IllegalArgumentException(
                        "You need to set a valid workId as long type");
            }
        } else {
            throw new IllegalStateException("You need to use the method "
                    + "WorkStepActivity.getLauncherIntent() passing the "
                    + "Work in the second parameter");
        }

        changeListLayout(getResources().getConfiguration());
        mWorkStepsView.setHasFixedSize(true);
        mWorkStepsView.addItemDecoration(new GridDividerDecoration(this));
        mWorkStepsView.addOnItemTouchListener(
                new OnTouchListener(this, mWorkStepsView, this));

        mFlowDataService = new FlowRealmDataService(this);

        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (NetworkUtil.isDeviceConnectedToInternet(this)) {
            final FlowService service = RetrofitHelper.createService(FlowService.class, this);

            if (service != null) {
                final Subscription subscription = service.getAll(mWorkId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(
                                new Action1<List<Flow>>() {
                                    @Override
                                    public void call(List<Flow> flowList) {
                                        if (flowList.isEmpty()) {
                                            // TODO: carregar estado vazio
                                            // TODO: atualizar o banco de dados
                                        } else {
                                            saveAllToLocalStorage(flowList);
                                        }
                                    }
                                },

                                new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable e) {
                                        showError(R.string.title_dialog_error_loading_data_from_server, e);
                                    }
                                }
                        );
                mCompositeSubscription.add(subscription);
            } else {
                validateAppSettings();
            }
        } else {
            final Subscription subscription = mFlowDataService.list(mWorkId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            new Action1<List<Flow>>() {
                                @Override
                                public void call(List<Flow> list) {
                                    mWorkStepsView.setAdapter(mFlowAdapter =
                                            new FlowAdapter(FlowActivity.this, list));

                                    updateSubtitle();
                                }
                            },

                            new Action1<Throwable>() {
                                @Override
                                public void call(Throwable e) {
                                    showError(R.string.title_dialog_error_loading_data_from_local, e);
                                }
                            }
                    );
            mCompositeSubscription.add(subscription);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }

    @Override
    public void onSingleTapUp(View view, int position) {
        if (mFlowAdapter != null) {
            final Flow item = mFlowAdapter.getItem(position);

            if (item != null) {
                NavigationHelper.navigateToCheckinScreen(this, mWorkId,
                        item.getStep().getWorkStepId());
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

    private void validateAppSettings() {
        if (!SettingsHelper.isSettingsApplied(this)) {
            FeedbackHelper.snackbar(mRootView, getString(R.string.msg_settings_not_applied), true,
                    new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            NavigationHelper.navigateToSettingsScreen(FlowActivity.this);
                        }
                    });
        }
    }

    private void saveAllToLocalStorage(List<Flow> flows) {
        final Subscription subscription = mFlowDataService.saveAll(mWorkId, flows)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).
                        subscribe(
                                new Action1<List<Flow>>() {
                                    @Override
                                    public void call(List<Flow> flowList) {
                                        mWorkStepsView.setAdapter(mFlowAdapter =
                                                new FlowAdapter(FlowActivity.this, flowList));

                                        updateSubtitle();
                                    }
                                },

                                new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable e) {
                                        showError(R.string.title_dialog_error_saving_data, e);
                                    }
                                }
                        );
        mCompositeSubscription.add(subscription);
    }

    private void showError(@StringRes int titleRes, Throwable e) {
        //TODO: tratamento de exceção
        new MaterialDialog.Builder(FlowActivity.this)
                .title(titleRes)
                .content(e.getMessage())
                .positiveText(R.string.text_dialog_button_ok)
                .show();
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
}
