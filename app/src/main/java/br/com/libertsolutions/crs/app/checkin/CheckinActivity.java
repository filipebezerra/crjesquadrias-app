package br.com.libertsolutions.crs.app.checkin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.spinner.SpinnerAdapter;
import br.com.libertsolutions.crs.app.base.BaseActivity;
import br.com.libertsolutions.crs.app.recyclerview.DividerDecoration;
import br.com.libertsolutions.crs.app.recyclerview.OnClickListener;
import br.com.libertsolutions.crs.app.recyclerview.OnTouchListener;
import br.com.libertsolutions.crs.app.step.WorkStep;
import br.com.libertsolutions.crs.app.step.WorkSteps;
import butterknife.ButterKnife;
import java.util.List;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 13/02/2016
 * @since 0.1.0
 */
public class CheckinActivity extends BaseActivity implements OnClickListener {

    private static final String EXTRA_DATA = "data";

    private WorkStep mWorkStep;
    private CheckinAdapter mCheckinAdapter;

    @Override
    protected int provideLayoutResource() {
        return R.layout.activity_checkin;
    }

    @Override
    protected int provideUpIndicatorResource() {
        return R.drawable.ic_arrow_back_24dp;
    }

    public static Intent getLauncherIntent(@NonNull Context context,
            @NonNull WorkStep step) {
        return new Intent(context, CheckinActivity.class)
                .putExtra(EXTRA_DATA, step);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mWorkStep = getIntent().getParcelableExtra(EXTRA_DATA);

        setupSpinner();

        final List<Checkin> checkins = Checkins.getDataSet(mWorkStep.getType());

        RecyclerView checkingsView = ButterKnife.findById(this, android.R.id.list);
        checkingsView.setLayoutManager(new LinearLayoutManager(this));
        checkingsView.setHasFixedSize(true);
        checkingsView.setAdapter(
                mCheckinAdapter = new CheckinAdapter(checkins));
        checkingsView.addItemDecoration(
                new DividerDecoration(this));
        checkingsView.addOnItemTouchListener(
                new OnTouchListener(this, checkingsView, this));
    }

    private void setupSpinner() {
        final List<WorkStep> steps = WorkSteps.getDataSet();
        final String actualStepName = mWorkStep.getName();

        String[] stepList = new String[steps.size()];
        int i = 0;
        int actualStepPosition = 0;
        for(WorkStep step : steps) {
            stepList[i] = step.getName();

            if (step.getName().equals(actualStepName)) {
                actualStepPosition = i;
            }

            i++;
        }

        // Setup spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new SpinnerAdapter(
                mToolbarAsActionBar.getContext(), stepList));

        spinner.setSelection(actualStepPosition, true);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onSingleTapUp(View view, int position) {

    }

    @Override
    public void onLongPress(View view, int position) {

    }
}