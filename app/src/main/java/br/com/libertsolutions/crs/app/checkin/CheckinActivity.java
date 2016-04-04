package br.com.libertsolutions.crs.app.checkin;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.activity.BaseActivity;
import br.com.libertsolutions.crs.app.android.recyclerview.DividerDecoration;
import br.com.libertsolutions.crs.app.feedback.FeedbackHelper;
import br.com.libertsolutions.crs.app.login.LoginHelper;
import br.com.libertsolutions.crs.app.login.User;
import br.com.libertsolutions.crs.app.navigation.NavigationHelper;
import br.com.libertsolutions.crs.app.network.NetworkUtil;
import br.com.libertsolutions.crs.app.webservice.ServiceGenerator;
import br.com.libertsolutions.crs.app.settings.SettingsHelper;
import butterknife.Bind;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
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
 * @version 0.1.0, 31/03/2016
 * @since 0.1.0
 */
public class CheckinActivity extends BaseActivity implements CheckinAdapter.CheckinCallback {

    public static final String EXTRA_FLOW_ID = "flowId";

    private Long mFlowId;

    private CheckinAdapter mCheckinAdapter;

    private CheckinDataService mCheckinDataService;

    private CompositeSubscription mCompositeSubscription;

    private User mUserLogged;

    private CheckinService mCheckinService;

    @Bind(android.R.id.list) RecyclerView mCheckinsView;

    private void updateSubtitle() {
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
        Crashlytics.logException(e);

        new MaterialDialog.Builder(this)
                .title(titleRes)
                .content(e.getMessage())
                .positiveText(R.string.text_dialog_button_ok)
                .show();
    }

    private void postCheckin(final Checkin checkin) {
        if (checkin == null) {
            return;
        }

        final boolean hasInternetConnection = NetworkUtil.isDeviceConnectedToInternet(this);

        if (hasInternetConnection) {
            if (mCheckinService == null) {
                mCheckinService = ServiceGenerator.createService(CheckinService.class, this);
            }

            if (mCheckinService != null) {
                if (mUserLogged == null) {
                    mUserLogged = LoginHelper.getUserLogged(this);
                }

                if (mUserLogged != null) {
                    final Subscription subscription = mCheckinService
                            .post(mUserLogged.getCpf(), checkin)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(
                                    new Action1<Checkin>() {
                                        @Override
                                        public void call(Checkin checkinUpdated) {
                                            mCheckinAdapter.updateCheckin(checkinUpdated);
                                            updateCheckinStatus(checkinUpdated);
                                        }
                                    },

                                    new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable e) {
                                            showError(R.string.error_sending_data, e);
                                            setCheckinToBeSynchronized(checkin);
                                        }
                                    }
                            );
                    mCompositeSubscription.add(subscription);
                } else {
                    validateUserLogged();
                }
            } else {
                validateAppSettings();
            }
        }

        if (!hasInternetConnection || mCheckinService == null || mUserLogged == null) {
            setCheckinToBeSynchronized(checkin);
        }
    }

    private void updateCheckinStatus(Checkin checkin) {
        if (checkin == null) {
            return;
        }

        if (mCheckinDataService == null) {
            mCheckinDataService = new CheckinRealmDataService(this);
        }

        mCheckinDataService.updateStatus(checkin, false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        new Action1<Checkin>() {
                            @Override
                            public void call(Checkin checkin) {}
                        },

                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable e) {
                                showError(R.string.title_dialog_error_saving_data, e);
                            }
                        }
                );
    }

    private void setCheckinToBeSynchronized(Checkin checkin) {
        if (checkin == null) {
            return;
        }

        if (mCheckinDataService == null) {
            mCheckinDataService = new CheckinRealmDataService(this);
        }

        mCheckinDataService.updateStatus(checkin, true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        new Action1<Checkin>() {
                            @Override
                            public void call(Checkin checkin) {}
                        },

                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable e) {
                                showError(R.string.title_dialog_error_saving_data, e);
                            }
                        }
                );
    }

    private void patchCheckins(final List<Checkin> checkinsUpdated) {
        if (checkinsUpdated == null || checkinsUpdated.isEmpty()) {
            return;
        }

        final boolean hasInternetConnection = NetworkUtil.isDeviceConnectedToInternet(this);

        if (hasInternetConnection) {
            if (mCheckinService == null) {
                mCheckinService = ServiceGenerator.createService(CheckinService.class, this);
            }

            if (mCheckinService != null) {
                if (mUserLogged == null) {
                    mUserLogged = LoginHelper.getUserLogged(this);
                }

                if (mUserLogged != null) {
                    final Subscription subscription = mCheckinService
                            .patch(mUserLogged.getCpf(), checkinsUpdated)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(
                                    new Action1<List<Checkin>>() {
                                        @Override
                                        public void call(List<Checkin> checkins) {
                                            mCheckinAdapter.updateCheckin(checkinsUpdated);
                                            updateCheckinsStatus(checkins);
                                        }
                                    },

                                    new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable e) {
                                            showError(R.string.error_sending_data, e);
                                            setCheckinsToBeSynchronized(checkinsUpdated);
                                        }
                                    }
                            );
                    mCompositeSubscription.add(subscription);
                } else {
                    validateUserLogged();
                }
            } else {
                validateAppSettings();
            }
        }

        if (!hasInternetConnection || mCheckinService == null || mUserLogged == null) {
            setCheckinsToBeSynchronized(checkinsUpdated);
        }
    }

    private void updateCheckinsStatus(List<Checkin> checkins) {
        if (checkins == null || checkins.isEmpty()) {
            return;
        }

        if (mCheckinDataService == null) {
            mCheckinDataService = new CheckinRealmDataService(this);
        }

        mCheckinDataService.updateStatus(checkins, false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        new Action1<List<Checkin>>() {
                            @Override
                            public void call(List<Checkin> checkins) {

                            }
                        },

                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable e) {
                                showError(R.string.title_dialog_error_saving_data, e);
                            }
                        }
                );
    }

    private void setCheckinsToBeSynchronized(List<Checkin> checkins) {
        if (checkins == null || checkins.isEmpty()) {
            return;
        }

        if (mCheckinDataService == null) {
            mCheckinDataService = new CheckinRealmDataService(this);
        }

        mCheckinDataService.updateStatus(checkins, true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        new Action1<List<Checkin>>() {
                            @Override
                            public void call(List<Checkin> checkins) {

                            }
                        },

                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable e) {
                                showError(R.string.title_dialog_error_saving_data, e);
                            }
                        }
                );
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

        if (getIntent().hasExtra(EXTRA_FLOW_ID)) {
            mFlowId = getIntent().getLongExtra(EXTRA_FLOW_ID, 0);
        } else {
            Toast.makeText(getApplicationContext(), "Developer, you need to use the method "
                    + "NavigationHelper.navigateToCheckinScreen() passing the "
                    + "Flow ID in the second parameter", Toast.LENGTH_LONG).show();
            finish();
        }

        mCheckinsView.setLayoutManager(new LinearLayoutManager(this));
        mCheckinsView.setHasFixedSize(true);
        mCheckinsView.addItemDecoration(new DividerDecoration(this));

        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mCheckinDataService == null) {
            mCheckinDataService = new CheckinRealmDataService(this);
        }

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
                                showError(R.string.title_dialog_error_loading_data_from_local, e);
                            }
                        }
                );
        mCompositeSubscription.add(subscription);
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
    public void onCheckinDone(Checkin checkin) {
        updateSubtitle();
        postCheckin(checkin);
    }

    @Override
    public void onCheckinsAllDone(List<Checkin> checkinsUpdated) {
        updateSubtitle();
        patchCheckins(checkinsUpdated);
    }

    @Override
    public void onCheckinsAlreadyDone() {
        FeedbackHelper.snackbar(mRootView, "Todos check-ins já estão marcados!", true);
    }

    @Override
    public void onStatusCannotChange() {
        FeedbackHelper.snackbar(mRootView, "Não é permitido desfazer o check-in!", true);
    }
}