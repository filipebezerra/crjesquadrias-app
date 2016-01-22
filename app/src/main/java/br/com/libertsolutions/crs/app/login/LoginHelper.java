package br.com.libertsolutions.crs.app.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.SharedPreferencesCompat;

/**
 * Utilities methods used in {@link LoginActivity}.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 22/01/2016
 * @since 0.1.0
 */
public class LoginHelper {
    private static final String KEY_IS_USER_LOGGED = "isUserLogged";

    private LoginHelper() {
        // no constructor
    }

    public static boolean isUserLogged(@NonNull Context context) {
        final SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(KEY_IS_USER_LOGGED, false);
    }

    public static void loginUser(@NonNull Context context) {
        if (isUserLogged(context))
            return;

        final SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferencesCompat.EditorCompat
                .getInstance()
                .apply(
                        sharedPreferences.edit()
                                .putBoolean(KEY_IS_USER_LOGGED, true));
    }

    public static void logoutUser(@NonNull Context context) {
        if (!isUserLogged(context))
            return;

        final SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferencesCompat.EditorCompat
                .getInstance()
                .apply(
                        sharedPreferences.edit()
                                .putBoolean(KEY_IS_USER_LOGGED, false));

        //TODO: delete stored user data
    }
}
