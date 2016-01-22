package br.com.libertsolutions.crs.app.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

/**
 * Utilities methods used in {@link SettingsActivity} or {@link SettingsActivityCompat}.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 22/01/2016
 * @since 0.1.0
 */
public class SettingsHelper {
    private static final String KEY_APPLIED_ON_FIRST_RUN = "applied_on_first_run";
    public static final String KEY_SERVER_URL = "server_url";
    public static final String KEY_AUTH_KEY = "auth_key";

    private SettingsHelper() {
        // no constructor
    }

    public static boolean isSettingsApplied(@NonNull Context context) {
        final SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(context);

        return (sharedPreferences.getString(KEY_SERVER_URL, null) != null)
                && (sharedPreferences.getString(KEY_AUTH_KEY, null) != null);
    }
}
