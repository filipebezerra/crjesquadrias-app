package br.com.libertsolutions.crs.app.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.SharedPreferencesCompat;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 29/03/2016
 * @since 0.1.0
 */
public class ConfigHelper {
    private static final String KEY_LAST_SERVER_SYNC = "lastServerSync";
    private static final String KEY_WORK_DATA_WAS_IMPORTED = "workDataWasImported";
    private static final String KEY_FLOW_DATA_WAS_IMPORTED = "flowDataWasImported";
    private static final String KEY_CHECKIN_DATA_WAS_IMPORTED = "checkinDataWasImported";

    private ConfigHelper() {}

    public static String getLastServerSync(@NonNull Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_LAST_SERVER_SYNC, null);
    }

    public static void setLastServerSync(@NonNull Context context, String lastSyncDate) {
        SharedPreferencesCompat.EditorCompat
                .getInstance()
                .apply(PreferenceManager.getDefaultSharedPreferences(context)
                        .edit()
                        .putString(KEY_LAST_SERVER_SYNC, lastSyncDate));
    }

    public static void setWorkDataAsImported(@NonNull Context context) {
        SharedPreferencesCompat.EditorCompat
                .getInstance()
                .apply(PreferenceManager.getDefaultSharedPreferences(context)
                        .edit()
                        .putBoolean(KEY_WORK_DATA_WAS_IMPORTED, true));
    }

    public static void setFlowDataAsImported(@NonNull Context context) {
        SharedPreferencesCompat.EditorCompat
                .getInstance()
                .apply(PreferenceManager.getDefaultSharedPreferences(context)
                        .edit()
                        .putBoolean(KEY_FLOW_DATA_WAS_IMPORTED, true));
    }

    public static void setCheckinDataAsImported(@NonNull Context context) {
        SharedPreferencesCompat.EditorCompat
                .getInstance()
                .apply(PreferenceManager.getDefaultSharedPreferences(context)
                        .edit()
                        .putBoolean(KEY_CHECKIN_DATA_WAS_IMPORTED, true));
    }

    public static boolean isInitialDataImported(@NonNull Context context) {
        final SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(KEY_WORK_DATA_WAS_IMPORTED, false)
                && sharedPreferences.getBoolean(KEY_FLOW_DATA_WAS_IMPORTED, false)
                && sharedPreferences.getBoolean(KEY_CHECKIN_DATA_WAS_IMPORTED, false);
    }
}
