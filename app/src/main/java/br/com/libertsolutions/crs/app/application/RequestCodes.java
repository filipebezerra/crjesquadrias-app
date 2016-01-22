package br.com.libertsolutions.crs.app.application;

import br.com.libertsolutions.crs.app.login.LoginActivity;
import br.com.libertsolutions.crs.app.settings.SettingsActivity;
import br.com.libertsolutions.crs.app.settings.SettingsActivityCompat;

/**
 * Request codes used in global application.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 22/01/2016
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
}
