package br.com.libertsolutions.crs.app.main;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.activity.BaseActivity;
import br.com.libertsolutions.crs.app.android.recyclerview.GridDividerDecoration;
import br.com.libertsolutions.crs.app.android.recyclerview.OnClickListener;
import br.com.libertsolutions.crs.app.android.recyclerview.OnTouchListener;
import br.com.libertsolutions.crs.app.application.AppHelper;
import br.com.libertsolutions.crs.app.checkin.Checkin;
import br.com.libertsolutions.crs.app.checkin.CheckinRealmDataService;
import br.com.libertsolutions.crs.app.checkin.CheckinService;
import br.com.libertsolutions.crs.app.drawable.DrawableHelper;
import br.com.libertsolutions.crs.app.feedback.FeedbackHelper;
import br.com.libertsolutions.crs.app.flow.Flow;
import br.com.libertsolutions.crs.app.flow.FlowRealmDataService;
import br.com.libertsolutions.crs.app.flow.FlowService;
import br.com.libertsolutions.crs.app.login.LoginHelper;
import br.com.libertsolutions.crs.app.login.User;
import br.com.libertsolutions.crs.app.navigation.NavigationHelper;
import br.com.libertsolutions.crs.app.network.NetworkUtil;
import br.com.libertsolutions.crs.app.retrofit.RetrofitHelper;
import br.com.libertsolutions.crs.app.settings.SettingsHelper;
import br.com.libertsolutions.crs.app.work.Work;
import br.com.libertsolutions.crs.app.work.WorkAdapter;
import br.com.libertsolutions.crs.app.work.WorkDataService;
import br.com.libertsolutions.crs.app.work.WorkRealmDataService;
import br.com.libertsolutions.crs.app.work.WorkService;
import butterknife.Bind;
import com.afollestad.materialdialogs.MaterialDialog;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Tela principal, nesta são listadas as obras cadastradas no servidor.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 27/03/2016
 * @since 0.1.0
 */
public class MainActivity extends BaseActivity implements OnClickListener {

    private WorkAdapter mWorkAdapter;

    private WorkDataService mWorkDataService;

    private CompositeSubscription mCompositeSubscription;

    private MaterialDialog mProgressDialog;

    @Bind(R.id.list) RecyclerView mWorksView;

    @Override
    protected int provideLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_main);
        }

        changeListLayout(getResources().getConfiguration());
        mWorksView.addItemDecoration(new GridDividerDecoration(this));
        mWorksView.setHasFixedSize(true);
        mWorksView.addOnItemTouchListener(
                new OnTouchListener(this, mWorksView, this));

        if (mToolbarAsActionBar != null) {
            final Drawable navigationIcon = DrawableHelper.withContext(this)
                    .withColor(R.color.white)
                    .withDrawable(R.drawable.ic_worker)
                    .tint()
                    .get();

            mToolbarAsActionBar.setNavigationIcon(navigationIcon);
        }

        mWorkDataService = new WorkRealmDataService(this);

        mCompositeSubscription = new CompositeSubscription();

        showUserLoggedInfo();
    }

    private void showProgressDialog(@StringRes int titleRes, @StringRes int contentRes) {
        if (mProgressDialog == null || !mProgressDialog.isShowing()) {
            mProgressDialog = new MaterialDialog.Builder(this)
                    .title(titleRes)
                    .content(contentRes)
                    .progress(true, 0)
                    .progressIndeterminateStyle(true)
                    .cancelable(false)
                    .show();
        }
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            mProgressDialog = null;
        }
    }

    private void showEmptyState() {
        // TODO: show empty state
        // TODO: enable BroadcastReceiver to network events
    }

    private void updateLastServerSync(WorkService workService) {
        //TODO
    }

    private void importInitialData(WorkService workService, FlowService flowService,
            CheckinService checkinService) {
        final AtomicBoolean workServiceWorking = new AtomicBoolean(false);
        final AtomicBoolean flowServiceWorking = new AtomicBoolean(false);
        final AtomicBoolean checkinServiceWorking = new AtomicBoolean(false);

        workService.getAllWithUpdates("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        new Subscriber<List<Work>>() {
                            @Override
                            public void onStart() {
                                workServiceWorking.set(true);
                                showProgressDialog(R.string.title_importing_data,
                                        R.string.content_importing_data);
                            }

                            @Override
                            public void onError(Throwable e) {
                                workServiceWorking.set(false);

                                if (!flowServiceWorking.get() && !checkinServiceWorking.get()) {
                                    hideProgressDialog();
                                }

                                showError(R.string.error_importing_data, e);
                            }

                            @Override
                            public void onNext(List<Work> workList) {
                                saveAllToLocalStorage(workList);
                            }

                            @Override
                            public void onCompleted() {
                                workServiceWorking.set(false);

                                if (!flowServiceWorking.get() && !checkinServiceWorking.get()) {
                                    hideProgressDialog();
                                }
                            }
                        }
                );

        flowService.getAllWithUpdates("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        new Subscriber<List<Flow>>() {
                            @Override
                            public void onStart() {
                                flowServiceWorking.set(true);
                                showProgressDialog(R.string.title_importing_data,
                                        R.string.content_importing_data);
                            }

                            @Override
                            public void onError(Throwable e) {
                                flowServiceWorking.set(false);

                                if (!workServiceWorking.get()
                                        && !checkinServiceWorking.get()) {
                                    hideProgressDialog();
                                }

                                showError(R.string.error_importing_data, e);
                            }

                            @Override
                            public void onNext(List<Flow> flowList) {
                                new FlowRealmDataService(MainActivity.this)
                                        .saveAll(flowList)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.io())
                                        .subscribe(
                                                new Action1<List<Flow>>() {
                                                    @Override
                                                    public void call(List<Flow> flows) {
                                                        flowServiceWorking.set(false);

                                                        if (!workServiceWorking.get()
                                                                && !checkinServiceWorking.get()) {
                                                            hideProgressDialog();
                                                        }
                                                    }
                                                },

                                                new Action1<Throwable>() {
                                                    @Override
                                                    public void call(Throwable e) {
                                                        flowServiceWorking.set(false);

                                                        if (!workServiceWorking.get()
                                                                && !checkinServiceWorking.get()) {
                                                            hideProgressDialog();
                                                        }

                                                        showError(
                                                                R.string.title_dialog_error_saving_data,
                                                                e);
                                                    }
                                                }
                                        );
                            }

                            @Override
                            public void onCompleted() {}
                        }
                );

        checkinService.getAllWithUpdates("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        new Subscriber<List<Checkin>>() {
                            @Override
                            public void onStart() {
                                checkinServiceWorking.set(true);
                                showProgressDialog(R.string.title_importing_data,
                                        R.string.content_importing_data);
                            }

                            @Override
                            public void onError(Throwable e) {
                                checkinServiceWorking.set(false);

                                if (!workServiceWorking.get()
                                        && !flowServiceWorking.get()) {
                                    hideProgressDialog();
                                }

                                showError(R.string.error_importing_data, e);
                            }

                            @Override
                            public void onNext(List<Checkin> checkinList) {
                                new CheckinRealmDataService(MainActivity.this)
                                        .saveAll(checkinList)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.io())
                                        .subscribe(
                                                new Action1<List<Checkin>>() {
                                                    @Override
                                                    public void call(List<Checkin> checkins) {
                                                        checkinServiceWorking.set(false);

                                                        if (!workServiceWorking.get()
                                                                && !flowServiceWorking.get()) {
                                                            hideProgressDialog();
                                                        }
                                                    }
                                                },

                                                new Action1<Throwable>() {
                                                    @Override
                                                    public void call(Throwable e) {
                                                        checkinServiceWorking.set(false);

                                                        if (!workServiceWorking.get()
                                                                && !flowServiceWorking.get()) {
                                                            hideProgressDialog();
                                                        }

                                                        showError(
                                                                R.string.title_dialog_error_saving_data,
                                                                e);
                                                    }
                                                }
                                        );
                            }

                            @Override
                            public void onCompleted() {}
                        }
                );
    }

    private void retrieveDataUpdates(WorkService workService, FlowService flowService,
            CheckinService checkinService) {
        // TODO: update local database with server updates
    }

    private void showWorksFromDatabase() {
        final Subscription subscription = mWorkDataService.list()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        new Action1<List<Work>>() {
                            @Override
                            public void call(List<Work> list) {
                                mWorksView.setAdapter(mWorkAdapter =
                                        new WorkAdapter(MainActivity.this, list));

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
    protected void onStart() {
        super.onStart();

        final String lastServerSync = AppHelper.getLastServerSync(this);
        final boolean hasNetworkConnection = NetworkUtil.isDeviceConnectedToInternet(this);

        if (lastServerSync == null && !hasNetworkConnection) {
            showEmptyState();
        } else {
            final WorkService workService = RetrofitHelper.createService(WorkService.class,
                    this);
            final FlowService flowService = RetrofitHelper.createService(FlowService.class,
                    this);
            final CheckinService checkinService = RetrofitHelper.createService(
                    CheckinService.class, this);

            if (workService != null && flowService != null && checkinService != null) {
                if (lastServerSync == null) {
                    importInitialData(workService, flowService, checkinService);
                } else {
                    if (hasNetworkConnection) {
                        retrieveDataUpdates(workService, flowService, checkinService);
                    }

                    showWorksFromDatabase();
                }
            } else {
                validateAppSettings();
            }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(menu.findItem(R.id.menu_search));
        searchView.setQueryHint(getString(R.string.search_query_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        });
        return true;
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

    private void changeListLayout(Configuration configuration) {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mWorksView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mWorksView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void showUserLoggedInfo() {
        final User userLogged = LoginHelper.getUserLogged(this);
        if (userLogged != null) {
            FeedbackHelper
                    .snackbar(mRootView, String.format("Logado como %s.",
                            LoginHelper.formatCpf(userLogged.getName())), false);
        }
    }

    private void saveAllToLocalStorage(List<Work> works) {
        final Subscription subscription = mWorkDataService.saveAll(works)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        new Action1<List<Work>>() {
                            @Override
                            public void call(List<Work> workList) {
                                mWorksView.setAdapter(
                                        mWorkAdapter = new WorkAdapter(MainActivity.this,
                                                workList));

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

    private void validateAppSettings() {
        if (!SettingsHelper.isSettingsApplied(this)) {
            FeedbackHelper.snackbar(mRootView, getString(R.string.msg_settings_not_applied), true,
                    new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            NavigationHelper.navigateToSettingsScreen(MainActivity.this);
                        }
                    });
        }
    }

    private void showError(@StringRes int titleRes, Throwable e) {
        //TODO: tratamento de exceção
        new MaterialDialog.Builder(MainActivity.this)
                .title(titleRes)
                .content(e.getMessage())
                .positiveText(R.string.text_dialog_button_ok)
                .show();
    }

    private void updateSubtitle() {
        if (mWorkAdapter != null) {
            final int count = mWorkAdapter.getRunningWorksCount();
            if (count == 0) {
                setSubtitle(getString(R.string.no_work_running));
            } else {
                setSubtitle(getString(R.string.works_running,
                        count));
            }
        }
    }
}
