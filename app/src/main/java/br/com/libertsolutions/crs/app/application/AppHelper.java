package br.com.libertsolutions.crs.app.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.SharedPreferencesCompat;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 27/03/2016
 * @since 0.1.0
 */
public class AppHelper {
    private static final String KEY_LAST_SERVER_SYNC = "lastServerSync";

    private AppHelper() {}

    public static String getLastServerSync(@NonNull Context context) {
        final SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getString(KEY_LAST_SERVER_SYNC, null);
    }

    public static void setLastServerSync(@NonNull Context context, String lastSyncDate) {
        final SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferencesCompat.EditorCompat
                .getInstance()
                .apply(sharedPreferences.edit()
                        .putString(KEY_LAST_SERVER_SYNC, lastSyncDate));
    }
}
