package br.com.libertsolutions.crs.app.launchscreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.activity.BaseActivity;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 22/01/2016
 * @since #
 */
public class LaunchScreenActivity extends BaseActivity {
    private static final int SPLASH_TIME = 3000;

    @Override
    protected int provideLayoutResource() {
        return R.layout.activity_launch_screen;
    }

    public static Intent getLauncherIntent(@NonNull Context context) {
        return new Intent(context, LaunchScreenActivity.class);
    }

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);

        new Handler()
                .postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },
                        SPLASH_TIME
                );
    }
}
