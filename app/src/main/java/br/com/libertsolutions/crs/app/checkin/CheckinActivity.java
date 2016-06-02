package br.com.libertsolutions.crs.app.checkin;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.activity.BaseActivity;
import br.com.libertsolutions.crs.app.android.recyclerview.DividerDecoration;
import br.com.libertsolutions.crs.app.utils.feedback.FeedbackHelper;
import butterknife.Bind;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import java.util.List;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Activity da interface de usuário da lista de {@link Checkin}s.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 01/06/2016
 * @since 0.1.0
 */
public class CheckinActivity extends BaseActivity implements CheckinAdapter.CheckinCallback {

    public static final String EXTRA_FLOW_ID = "flowId";

    private Long mFlowId;

    private CheckinAdapter mCheckinAdapter;

    private CheckinDataService mCheckinDataService;

    private Subscription mCheckinDataSubscription;

    @Bind(android.R.id.list) RecyclerView mCheckinsView;

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

    private void loadCheckinData() {
        mCheckinDataSubscription = getCheckinDataService().list(mFlowId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<Checkin>>() {
                            @Override
                            public void call(List<Checkin> list) {
                                showCheckinData(list);
                            }
                        },

                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable e) {
                                showError(R.string.title_dialog_error_loading_data_from_local, e);
                            }
                        }
                );
    }

    private void showCheckinData(List<Checkin> list) {
        mCheckinsView.setAdapter(mCheckinAdapter =
                new CheckinAdapter(CheckinActivity.this, list));
        mCheckinAdapter.setCheckinCallback(CheckinActivity.this);

        updateSubtitle();
    }

    @Override
    protected void onStop() {
        super.onStop();

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
                        new Action1<Checkin>() {
                            @Override
                            public void call(Checkin ignored) {}
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
                        new Action1<List<Checkin>>() {
                            @Override
                            public void call(List<Checkin> ignored) {}
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
    public void onCheckinsAlreadyDone() {
        FeedbackHelper.snackbar(mRootView, "Todos check-ins já estão marcados!", true);
    }

    @Override
    public void onStatusCannotChange() {
        FeedbackHelper.snackbar(mRootView, "Não é permitido desfazer o check-in!", true);
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
}