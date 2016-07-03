package br.com.libertsolutions.crs.app.application;

import br.com.libertsolutions.crs.app.launchscreen.LaunchScreenActivity;
import br.com.libertsolutions.crs.app.loginscreen.LoginActivity;
import br.com.libertsolutions.crs.app.settingscreen.SettingsActivity;
import br.com.libertsolutions.crs.app.settingscreen.SettingsActivityCompat;

/**
 * Request codes used in global application.
 *
 * @author Filipe Bezerra
 * @version 0.1.0
 * @since 0.1.0
 */
public interface RequestCodes {
    /**
     * Internal request code used to request launching {@link SettingsActivity} or
     * {@link SettingsActivityCompat} asynchronously.
     */
    int LAUNCH_SETTINGS_SCREEN = 1;

    /**
     * Internal request code used to request launching {@link LoginActivity} asynchronously.
     */
    int LAUNCH_LOGIN_SCREEN = 2;

    /**
     * Internal request code used to request launching {@link LaunchScreenActivity} asynchronously.
     */
    int LAUNCH_BRAND_SCREEN = 3;
}
