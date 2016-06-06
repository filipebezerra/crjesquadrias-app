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
    private static final String KEY_LAST_WORKS_SYNC_DATE = "lastWorksSyncDate";
    private static final String KEY_LAST_FLOWS_SYNC_DATE = "lastFlowsSyncDate";
    private static final String KEY_LAST_CHECKINS_SYNC_DATE = "lastCheckinsSyncDate";
    private static final String KEY_WORK_DATA_WAS_IMPORTED = "workDataWasImported";
    private static final String KEY_FLOW_DATA_WAS_IMPORTED = "flowDataWasImported";
    private static final String KEY_CHECKIN_DATA_WAS_IMPORTED = "checkinDataWasImported";

    private ConfigHelper() {}

    public static String getLastWorksSyncDate(@NonNull Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_LAST_WORKS_SYNC_DATE, null);
    }

    public static void setLastWorksSyncDate(@NonNull Context context, String syncDate) {
        SharedPreferencesCompat.EditorCompat
                .getInstance()
                .apply(PreferenceManager.getDefaultSharedPreferences(context)
                        .edit()
                        .putString(KEY_LAST_WORKS_SYNC_DATE, syncDate));
    }


    public static String getLastFlowsSyncDate(@NonNull Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_LAST_FLOWS_SYNC_DATE, null);
    }

    public static void setLastFlowsSyncDate(@NonNull Context context, String syncDate) {
        SharedPreferencesCompat.EditorCompat
                .getInstance()
                .apply(PreferenceManager.getDefaultSharedPreferences(context)
                        .edit()
                        .putString(KEY_LAST_FLOWS_SYNC_DATE, syncDate));
    }

    public static String getLastCheckinsSyncDate(@NonNull Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_LAST_CHECKINS_SYNC_DATE, null);
    }

    public static void setLastCheckinsSyncDate(@NonNull Context context, String syncDate) {
        SharedPreferencesCompat.EditorCompat
                .getInstance()
                .apply(PreferenceManager.getDefaultSharedPreferences(context)
                        .edit()
                        .putString(KEY_LAST_CHECKINS_SYNC_DATE, syncDate));
    }

    public static void setDataImportationAsLastSyncDate(@NonNull Context context,
            String syncDate) {
        SharedPreferencesCompat.EditorCompat
                .getInstance()
                .apply(PreferenceManager.getDefaultSharedPreferences(context)
                        .edit()
                        .putString(KEY_LAST_WORKS_SYNC_DATE, syncDate)
                        .putString(KEY_LAST_FLOWS_SYNC_DATE, syncDate)
                        .putString(KEY_LAST_CHECKINS_SYNC_DATE, syncDate));
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
