package br.com.libertsolutions.crs.app.checkin;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.activity.BaseActivity;
import br.com.libertsolutions.crs.app.android.recyclerview.DividerDecoration;
import br.com.libertsolutions.crs.app.feedback.FeedbackHelper;
import br.com.libertsolutions.crs.app.login.LoginHelper;
import br.com.libertsolutions.crs.app.login.User;
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
public class CheckinActivity extends BaseActivity implements CheckinAdapter.CheckinCallback {

    public static final String EXTRA_WORK_ID = "workId";
    public static final String EXTRA_FLOW_ID = "flowId";

    private Long mWorkId;
    private Long mFlowId;

    private CheckinAdapter mCheckinAdapter;

    private CheckinDataService mCheckinDataService;

    private CompositeSubscription mCompositeSubscription;

    @Bind(android.R.id.list) RecyclerView mCheckinsView;

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

        if (getIntent().hasExtra(EXTRA_WORK_ID)
                && getIntent().hasExtra(EXTRA_FLOW_ID)) {
            mWorkId = getIntent().getLongExtra(EXTRA_WORK_ID, 0);
            mFlowId = getIntent().getLongExtra(EXTRA_FLOW_ID, 0);
        } else {
            throw new IllegalStateException("You need to use the method "
                    + "CheckinActivity.getLauncherIntent() passing the "
                    + "Work ID in the second parameter and the Step ID "
                    + "in the third parameter");
        }

        mCheckinsView.setLayoutManager(new LinearLayoutManager(this));
        mCheckinsView.setHasFixedSize(true);
        mCheckinsView.addItemDecoration(new DividerDecoration(this));

        mCheckinDataService = new CheckinRealmDataService(this);

        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (NetworkUtil.isDeviceConnectedToInternet(this)) {
            CheckinService service = RetrofitHelper.createService(CheckinService.class, this);

            if (service != null) {
                final Subscription subscription = service.getAllByStep(mWorkId, mFlowId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                new Action1<List<Checkin>>() {
                                    @Override
                                    public void call(List<Checkin> list) {
                                        if (list.isEmpty()) {
                                            // TODO: carregar estado vazio
                                            // TODO: atualizar o banco de dados
                                        } else {
                                            saveAllToLocalStorage(list);
                                        }
                                    }
                                },

                                new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable e) {
                                        showError(
                                                R.string.title_dialog_error_loading_data_from_server,
                                                e);
                                    }
                                }
                        );
                mCompositeSubscription.add(subscription);
            } else {
                validateAppSettings();
            }
        } else {
            final Subscription subscription = mCheckinDataService.list(mFlowId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            new Action1<List<Checkin>>() {
                                @Override
                                public void call(List<Checkin> list) {
                                    mCheckinsView.setAdapter(mCheckinAdapter =
                                            new CheckinAdapter(CheckinActivity.this, list));
                                    mCheckinAdapter.setCheckinCallback(CheckinActivity.this);

                                    updateSubtitle();
                                }
                            },

                            new Action1<Throwable>() {
                                @Override
                                public void call(Throwable e) {
                                    showError(R.string.title_dialog_error_loading_data_from_local,
                                            e);
                                }
                            }
                    );
            mCompositeSubscription.add(subscription);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
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
    public void onStatusChanged(Checkin checkin) {
        updateSubtitle();
        postCheckin(checkin);
    }

    @Override
    public void onCheckinsAllDone() {
        updateSubtitle();

        for (Checkin checkin : mCheckinAdapter.getAll()) {
            postCheckin(checkin);
        }
    }

    @Override
    public void onStatusCannotChange() {
        FeedbackHelper.snackbar(mRootView, "Não é permitido desfazer o check-in!", true);
    }

    private void updateSubtitle() {
        final int count = mCheckinAdapter.getFinishedCheckinsCount();
        if (count == 0) {
            setSubtitle(getString(R.string.no_checkin_finished));
        } else {
            setSubtitle(getString(R.string.checkins_finished,
                    count));
        }
    }

    private void postCheckin(Checkin checkin) {
        if (NetworkUtil.isDeviceConnectedToInternet(this)) {
            CheckinService service = RetrofitHelper.createService(CheckinService.class, this);

            if (service != null) {
                final User userLogged = LoginHelper.getUserLogged(this);
                if (userLogged != null) {
                    final Subscription subscription = service.post(userLogged.getCpf(), checkin)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(
                                    new Action1<Checkin>() {
                                        @Override
                                        public void call(Checkin checkinUpdated) {
                                            mCheckinAdapter.updateCheckin(checkinUpdated);
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
            } else {
                validateUserLogged();
            }
        } else {

        }
    }

    private void validateUserLogged() {
        if (!LoginHelper.isUserLogged(this)) {
            FeedbackHelper.snackbar(mRootView, getString(R.string.msg_user_not_logged), true,
                    new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            NavigationHelper.navigateToLoginScreen(CheckinActivity.this);
                        }
                    });
        }
    }

    private void validateAppSettings() {
        if (!SettingsHelper.isSettingsApplied(this)) {
            FeedbackHelper.snackbar(mRootView, getString(R.string.msg_settings_not_applied), true,
                    new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            NavigationHelper.navigateToSettingsScreen(CheckinActivity.this);
                        }
                    });
        }
    }

    private void showError(int titleRes, Throwable e) {
        //TODO: tratamento de exceção
        new MaterialDialog.Builder(this)
                .title(titleRes)
                .content(e.getMessage())
                .positiveText(R.string.text_dialog_button_ok)
                .show();
    }

    private void saveAllToLocalStorage(List<Checkin> list) {
        final Subscription subscription = mCheckinDataService.saveAll(mFlowId, list)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        new Action1<List<Checkin>>() {
                            @Override
                            public void call(List<Checkin> checkinList) {
                                mCheckinsView.setAdapter(mCheckinAdapter =
                                        new CheckinAdapter(CheckinActivity.this, checkinList));
                                mCheckinAdapter.setCheckinCallback(CheckinActivity.this);

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
}