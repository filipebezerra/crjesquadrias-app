package br.com.libertsolutions.crs.app.step;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.base.BaseActivity;
import br.com.libertsolutions.crs.app.recyclerview.DividerDecoration;
import br.com.libertsolutions.crs.app.recyclerview.OnClickListener;
import br.com.libertsolutions.crs.app.recyclerview.OnTouchListener;
import butterknife.ButterKnife;

public class WorkStepActivity extends BaseActivity implements OnClickListener {

    private WorkStepAdapter mStageAdapter;

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
                mStageAdapter = new WorkStepAdapter());
        stagesView.addItemDecoration(
                new DividerDecoration(this));
        stagesView.addOnItemTouchListener(
                new OnTouchListener(this, stagesView, this));
    }

    @Override
    public void onSingleTapUp(View view, int position) {

    }

    @Override
    public void onLongPress(View view, int position) {

    }
}