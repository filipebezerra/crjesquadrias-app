package br.com.libertsolutions.crs.app.checkin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.spinner.SpinnerAdapter;
import br.com.libertsolutions.crs.app.base.BaseActivity;
import br.com.libertsolutions.crs.app.step.WorkStep;
import br.com.libertsolutions.crs.app.step.WorkSteps;
import java.util.List;

public class CheckinActivity extends BaseActivity {

    private static final String EXTRA_DATA = "data";

    private WorkStep mWorkStep;

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
}