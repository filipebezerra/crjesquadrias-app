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
import br.com.libertsolutions.crs.app.work.Work;
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
 * @version 0.1.0, 27/02/2016
 * @since 0.1.0
 */
public class WorkStepActivity extends BaseActivity implements OnClickListener {

    private static final String EXTRA_DATA = "data";

    private Work mWorkRelatedTo;
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

    public static Intent getLauncherIntent(@NonNull Context context, @NonNull Work work) {
        return new Intent(context, WorkStepActivity.class)
                .putExtra(EXTRA_DATA, work);
    }

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);

        if (getIntent().hasExtra(EXTRA_DATA)) {
            mWorkRelatedTo = getIntent().getParcelableExtra(EXTRA_DATA);
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
            service.getAll(mWorkRelatedTo.getWorkdId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Subscriber<List<Flow>>() {
                        @Override
                        public void onCompleted() {
                            setSubtitle(getString(R.string.steps_in_running,
                                    mFlowAdapter.getRunningFlowsCount()));
                        }

                        @Override
                        public void onError(Throwable e) {
                            new MaterialDialog.Builder(WorkStepActivity.this)
                                    .title("Erro")
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
            startActivity(CheckinActivity.getLauncherIntent(this, item));
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
