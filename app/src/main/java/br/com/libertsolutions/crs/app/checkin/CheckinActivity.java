package br.com.libertsolutions.crs.app.checkin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.activity.BaseActivity;
import br.com.libertsolutions.crs.app.android.recyclerview.DividerDecoration;
import br.com.libertsolutions.crs.app.login.LoginHelper;
import br.com.libertsolutions.crs.app.retrofit.RetrofitHelper;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.MaterialDialog;
import java.util.List;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 07/03/2016
 * @since 0.1.0
 */
public class CheckinActivity extends BaseActivity implements CheckinAdapter.CheckinCallback {

    private static final String EXTRA_WORK_ID = "workId";
    private static final String EXTRA_STEP_ID = "stepId";

    private Long mWorkId;
    private Long mStepId;
    private CheckinAdapter mCheckinAdapter;
    private CheckinService mCheckinService;

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

    public static Intent getLauncherIntent(@NonNull Context context,
            Long workId, @NonNull Long stepId) {
        return new Intent(context, CheckinActivity.class)
                .putExtra(EXTRA_WORK_ID, workId)
                .putExtra(EXTRA_STEP_ID, stepId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(EXTRA_WORK_ID) 
                && getIntent().hasExtra(EXTRA_STEP_ID)) {
            mWorkId = getIntent().getLongExtra(EXTRA_WORK_ID, 0);
            mStepId = getIntent().getLongExtra(EXTRA_STEP_ID, 0);
        } else {
            throw new IllegalStateException("You need to use the method "
                    + "CheckinActivity.getLauncherIntent() passing the "
                    + "Work ID in the second parameter and the Step ID " 
                    + "in the third parameter");
        }

        mCheckinsView = ButterKnife.findById(this, android.R.id.list);
        mCheckinsView.setLayoutManager(new LinearLayoutManager(this));
        mCheckinsView.setHasFixedSize(true);
        mCheckinsView.addItemDecoration(
                new DividerDecoration(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mCheckinService == null) {
            mCheckinService = RetrofitHelper
                    .createService(CheckinService.class, this);
        }

        if (mCheckinService != null) {
            mCheckinService.getAllByStep(mWorkId, mStepId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Subscriber<List<Checkin>>() {
                        @Override
                        public void onCompleted() {
                            updateSubtitle();
                        }

                        @Override
                        public void onError(Throwable e) {
                            new MaterialDialog.Builder(CheckinActivity.this)
                                    .title("Falha ao tentar carregar dados")
                                    .content(e.getMessage())
                                    .positiveText("OK")
                                    .show();
                        }

                        @Override
                        public void onNext(List<Checkin> checkins) {
                            mCheckinsView.setAdapter(
                                    mCheckinAdapter = new CheckinAdapter(CheckinActivity.this,
                                            checkins));
                            mCheckinAdapter.setCheckinCallback(CheckinActivity.this);
                        }
                    });
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

        if (mCheckinService != null) {
            final String cpf = LoginHelper.getUserLogged(this);
            mCheckinService.post(cpf, checkin)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Subscriber<Checkin>() {
                        @Override
                        public void onCompleted() {}

                        @Override
                        public void onError(Throwable e) {
                            new MaterialDialog.Builder(CheckinActivity.this)
                                    .title("Falha ao tentar atualizar os dados")
                                    .content(e.getMessage())
                                    .positiveText("OK")
                                    .show();
                        }

                        @Override
                        public void onNext(Checkin checkin) {
                            checkin.setStatus(checkin.getStatus());
                            checkin.setDate(checkin.getDate());
                        }
                    });
        }
    }

    @Override
    public void onCheckinsAllDone() {
        updateSubtitle();
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
}