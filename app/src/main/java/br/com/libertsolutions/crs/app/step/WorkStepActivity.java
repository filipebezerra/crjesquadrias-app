package br.com.libertsolutions.crs.app.step;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.activity.BaseActivity;
import br.com.libertsolutions.crs.app.android.recyclerview.GridDividerDecoration;
import br.com.libertsolutions.crs.app.android.recyclerview.OnClickListener;
import br.com.libertsolutions.crs.app.android.recyclerview.OnTouchListener;
import br.com.libertsolutions.crs.app.checkin.CheckinActivity;
import br.com.libertsolutions.crs.app.retrofit.RetrofitHelper;
import butterknife.Bind;
import com.afollestad.materialdialogs.MaterialDialog;
import java.util.List;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 18/03/2016
 * @since 0.1.0
 */
public class WorkStepActivity extends BaseActivity implements OnClickListener {

    private static final String EXTRA_ID = "data";

    private Long mWorkId;
    private FlowAdapter mFlowAdapter;
    
    @Bind(android.R.id.list) RecyclerView mWorkStepsView;

    @Override
    protected int provideLayoutResource() {
        return R.layout.activity_work_step;
    }

    @Override
    protected int provideUpIndicatorResource() {
        return R.drawable.ic_arrow_back_24dp;
    }

    public static Intent getLauncherIntent(@NonNull Context context, @NonNull Long workId) {
        return new Intent(context, WorkStepActivity.class)
                .putExtra(EXTRA_ID, workId);
    }

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);

        if (getIntent().hasExtra(EXTRA_ID)) {
            mWorkId = getIntent().getLongExtra(EXTRA_ID, INVALID_EXTRA_ID);

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        final FlowService service = RetrofitHelper.createService(FlowService.class, this);
        if (service != null) {
            service.getAll(mWorkId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Subscriber<List<Flow>>() {
                        @Override
                        public void onCompleted() {
                            final int count = mFlowAdapter.getRunningFlowsCount();
                            if (count == 0) {
                                setSubtitle(getString(R.string.no_work_step_running));
                            } else {
                                setSubtitle(getString(R.string.work_steps_running,
                                        count));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            new MaterialDialog.Builder(WorkStepActivity.this)
                                    .title("Falha ao tentar carregar dados")
                                    .content(e.getMessage())
                                    .positiveText("OK")
                                    .show();
                        }

                        @Override
                        public void onNext(List<Flow> flowList) {
                            mWorkStepsView.setAdapter(mFlowAdapter =
                                    new FlowAdapter(WorkStepActivity.this, flowList));
                        }
                    });
        }
    }

    @Override
    public void onSingleTapUp(View view, int position) {
        final WorkStep item = mFlowAdapter.getItem(position).getStep();

        if (item != null) {
            startActivity(CheckinActivity.getLauncherIntent(this, mWorkId, item.getWorkStepId()));
        }
    }

    @Override
    public void onLongPress(View view, int position) {
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
}
