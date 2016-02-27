package br.com.libertsolutions.crs.app.step;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.activity.BaseActivity;
import br.com.libertsolutions.crs.app.android.recyclerview.DividerDecoration;
import br.com.libertsolutions.crs.app.android.recyclerview.OnClickListener;
import br.com.libertsolutions.crs.app.android.recyclerview.OnTouchListener;
import br.com.libertsolutions.crs.app.checkin.CheckinActivity;
import butterknife.ButterKnife;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 27/02/2016
 * @since 0.1.0
 */
public class WorkStepActivity extends BaseActivity implements OnClickListener {

    private WorkStepAdapter mWorkStepAdapter;

    @Override
    protected int provideLayoutResource() {
        return R.layout.activity_work_step;
    }

    @Override
    protected int provideUpIndicatorResource() {
        return R.drawable.ic_arrow_back_24dp;
    }

    public static Intent getLauncherIntent(@NonNull Context context) {
        return new Intent(context, WorkStepActivity.class);
    }

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);

        RecyclerView stagesView = ButterKnife.findById(this, android.R.id.list);
        stagesView.setLayoutManager(new LinearLayoutManager(this));
        stagesView.setHasFixedSize(true);
        stagesView.setAdapter(
                mWorkStepAdapter = new WorkStepAdapter(this));
        stagesView.addItemDecoration(
                new DividerDecoration(this));
        stagesView.addOnItemTouchListener(
                new OnTouchListener(this, stagesView, this));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(getString(R.string.steps_in_running,
                    mWorkStepAdapter.getWorkStepsInRunning()));
        }
    }

    @Override
    public void onSingleTapUp(View view, int position) {
        final WorkStep item = mWorkStepAdapter.getItem(position);

        if (item != null) {
            startActivity(CheckinActivity.getLauncherIntent(this, item));
        }
    }

    @Override
    public void onLongPress(View view, int position) {

    }
}
