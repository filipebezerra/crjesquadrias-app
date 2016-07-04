package br.com.libertsolutions.crs.app.utils.navigation;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.IntentCompat;
import br.com.libertsolutions.crs.app.application.RequestCodes;
import br.com.libertsolutions.crs.app.checkinscreen.CheckinActivity;
import br.com.libertsolutions.crs.app.flow.Flow;
import br.com.libertsolutions.crs.app.flowscreen.FlowActivity;
import br.com.libertsolutions.crs.app.loginscreen.LoginActivity;
import br.com.libertsolutions.crs.app.mainscreen.MainActivity;
import br.com.libertsolutions.crs.app.settingscreen.SettingsActivity;
import br.com.libertsolutions.crs.app.settingscreen.SettingsActivityCompat;
import br.com.libertsolutions.crs.app.work.Work;

/**
 * @author Filipe Bezerra
 * @version 0.2.0
 * @since 0.1.0
 */
public class NavigationHelper {
    private NavigationHelper(){}

    public static void navigateToLoginScreen(@NonNull Activity activity) {
        activity.startActivity(new Intent(activity, LoginActivity.class));
    }

    public static void navigateToMainScreen(@NonNull Activity activity) {
        activity.startActivity(IntentCompat
                .makeMainActivity(new ComponentName(activity, MainActivity.class))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void navigateToSettingsScreen(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            activity.startActivityForResult(
                    new Intent(activity, SettingsActivity.class),
                    RequestCodes.LAUNCH_SETTINGS_SCREEN);
        } else {
            activity.startActivityForResult(
                    new Intent(activity, SettingsActivityCompat.class),
                    RequestCodes.LAUNCH_SETTINGS_SCREEN);
        }
    }

    public static void navigateToFlowScreen(@NonNull Activity activity, @NonNull Work work) {
        activity.startActivity(new Intent(activity, FlowActivity.class)
                .putExtra(FlowActivity.EXTRA_WORK, work));
    }

    public static void navigateToCheckinScreen(@NonNull Context context, @NonNull Flow flow) {
        context.startActivity(new Intent(context, CheckinActivity.class)
                .putExtra(CheckinActivity.EXTRA_FLOW, flow));
    }
}
