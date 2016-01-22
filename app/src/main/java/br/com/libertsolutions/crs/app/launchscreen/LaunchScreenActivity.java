package br.com.libertsolutions.crs.app.launchscreen;

import android.os.Bundle;
import android.os.Handler;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.base.BaseActivity;
import br.com.libertsolutions.crs.app.main.MainActivity;

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

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);

        new Handler()
                .postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                startActivity(
                                        MainActivity
                                                .getLauncherIntent(LaunchScreenActivity.this));
                            }
                        },
                        SPLASH_TIME
                );
    }
}
