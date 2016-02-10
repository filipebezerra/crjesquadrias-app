package br.com.libertsolutions.crs.app.stage;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.base.BaseActivity;

public class StageActivity extends BaseActivity {

    @Override
    protected int provideLayoutResource() {
        return R.layout.activity_stage;
    }

    @Override
    protected int provideUpIndicatorResource() {
        return R.drawable.ic_arrow_back_24dp;
    }

    public static Intent getLauncherIntent(@NonNull Context context) {
        return new Intent(context, StageActivity.class);
    }
}
