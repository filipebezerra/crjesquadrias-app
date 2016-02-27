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
import butterknife.Bind;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 27/02/2016
 * @since 0.1.0
 */
public class WorkStepActivity extends BaseActivity implements OnClickListener {

    private WorkStepAdapter mWorkStepAdapter;
    
    @Bind(android.R.id.list) RecyclerView mWorkStepsView;

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

        changeListLayout(getResources().getConfiguration());
        mWorkStepsView.setHasFixedSize(true);
        mWorkStepsView.setAdapter(mWorkStepAdapter = new WorkStepAdapter(this));
        mWorkStepsView.addItemDecoration(new GridDividerDecoration(this));
        mWorkStepsView.addOnItemTouchListener(
                new OnTouchListener(this, mWorkStepsView, this));

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
