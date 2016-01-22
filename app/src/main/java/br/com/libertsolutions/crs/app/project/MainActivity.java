package br.com.libertsolutions.crs.app.project;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.application.RequestCodes;
import br.com.libertsolutions.crs.app.base.BaseActivity;
import br.com.libertsolutions.crs.app.login.LoginActivity;
import br.com.libertsolutions.crs.app.login.LoginHelper;
import br.com.libertsolutions.crs.app.settings.SettingsActivity;
import br.com.libertsolutions.crs.app.settings.SettingsActivityCompat;
import br.com.libertsolutions.crs.app.settings.SettingsHelper;

/**
 * Application main screen.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 22/01/2016
 * @since 0.1.0
 */
public class MainActivity extends BaseActivity {

    @Override
    protected int provideLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_main);
        }

        if (!SettingsHelper.isAppliedOnFirstRun(this)) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1)
                startActivityForResult(
                        SettingsActivity.getLauncherIntent(getApplicationContext()),
                        RequestCodes.LAUNCH_SETTINGS_SCREEN);
            else
                startActivityForResult(
                        SettingsActivityCompat.getLauncherIntent(getApplicationContext()),
                        RequestCodes.LAUNCH_SETTINGS_SCREEN);
        } else if (!LoginHelper.isUserLogged(this)) {
            startActivityForResult(
                    LoginActivity.getLauncherIntent(getApplicationContext()),
                    RequestCodes.LAUNCH_LOGIN_SCREEN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RequestCodes.LAUNCH_SETTINGS_SCREEN:
                    break;

                case RequestCodes.LAUNCH_LOGIN_SCREEN:
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
