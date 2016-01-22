package br.com.libertsolutions.crs.app.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.SharedPreferencesCompat;

/**
 * Utilities methods used in {@link SettingsActivity} or {@link SettingsActivityCompat}.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 22/01/2016
 * @since 0.1.0
 */
public class SettingsHelper {
    private static final String KEY_APPLIED_ON_FIRST_RUN = "appliedOnFirstRun";

    private SettingsHelper() {
        // no constructor
    }

    public static boolean isAppliedOnFirstRun(@NonNull Context context) {
        final SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(KEY_APPLIED_ON_FIRST_RUN, false);
    }

    public static void setAppliedOnFirstRun(@NonNull Context context) {
        final SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferencesCompat.EditorCompat
                .getInstance()
                .apply(
                        sharedPreferences.edit()
                                .putBoolean(KEY_APPLIED_ON_FIRST_RUN, true));
    }
}
