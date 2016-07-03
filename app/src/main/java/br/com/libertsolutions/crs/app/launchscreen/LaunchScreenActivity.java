package br.com.libertsolutions.crs.app.launchscreen;

import android.os.Bundle;
import android.os.Handler;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.android.activity.BaseActivity;
import br.com.libertsolutions.crs.app.utils.navigation.NavigationHelper;

/**
 * @author Filipe Bezerra
 * @version 0.2.0
 * @since 0.1.0
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
        new Handler().postDelayed(
                () -> NavigationHelper.navigateToLoginScreen(LaunchScreenActivity.this),
                SPLASH_TIME);
    }
}
