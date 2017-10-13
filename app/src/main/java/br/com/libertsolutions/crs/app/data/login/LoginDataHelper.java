package br.com.libertsolutions.crs.app.data.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.SharedPreferencesCompat;
import android.text.TextUtils;
import br.com.libertsolutions.crs.app.domain.pojo.User;
import br.com.libertsolutions.crs.app.presentation.login.LoginActivity;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Utilities methods used in {@link LoginActivity}.
 *
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class LoginDataHelper {
    private static final String KEY_IS_USER_LOGGED = "isUserLogged";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_CPF = "cpf";
    private static final String KEY_USER_EMAIL = "email";

    private LoginDataHelper() {
        // no constructor
    }

    public static boolean isUserLogged(@NonNull Context context) {
        final SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(KEY_IS_USER_LOGGED, false);
    }

    @SuppressLint("CommitPrefEdits")
    public static void loginUser(@NonNull Context context, @NonNull User user) {
        if (isUserLogged(context))
            return;

        if (!isValidUser(user))
            return;

        final SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferencesCompat.EditorCompat
                .getInstance()
                .apply(
                        sharedPreferences.edit()
                                .putBoolean(KEY_IS_USER_LOGGED, true)
                                .putString(KEY_USER_NAME, user.getName())
                                .putString(KEY_USER_CPF, user.getCpf())
                                .putString(KEY_USER_EMAIL, user.getEmail()));

        if (Fabric.isInitialized()) {
            Crashlytics.setUserIdentifier(user.getCpf());
            Crashlytics.setUserEmail(user.getEmail());
            Crashlytics.setUserName(user.getName());
        }
    }

    public static User getUserLogged(@NonNull Context context) {
        if (!isUserLogged(context))
            return null;

        final SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(context);

        return new User()
                .setCpf(sharedPreferences.getString(KEY_USER_CPF, null))
                .setName(sharedPreferences.getString(KEY_USER_NAME, null))
                .setEmail(sharedPreferences.getString(KEY_USER_EMAIL, null));
    }

    @SuppressLint("CommitPrefEdits")
    public static void logoutUser(@NonNull Context context) {
        if (!isUserLogged(context))
            return;

        final SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferencesCompat.EditorCompat
                .getInstance()
                .apply(
                        sharedPreferences.edit()
                                .putBoolean(KEY_IS_USER_LOGGED, false)
                                .remove(KEY_USER_NAME)
                                .remove(KEY_USER_CPF)
                                .remove(KEY_USER_EMAIL));
    }

    public static String formatCpf(@NonNull String cpf) {
        if (TextUtils.isEmpty(cpf) || cpf.length() != 11) {
            return cpf;
        }

        return new StringBuilder(cpf)
                .insert(3, ".")
                .insert(7, ".")
                .insert(11, "-")
                .toString();
    }

    public static boolean isValidUser(User user) {
        return user != null &&
                !TextUtils.isEmpty(user.getCpf()) &&
                !TextUtils.isEmpty(user.getEmail()) &&
                !TextUtils.isEmpty(user.getName());
    }
}
