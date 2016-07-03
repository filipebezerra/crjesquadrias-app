package br.com.libertsolutions.crs.app.utils.navigation;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.IntentCompat;
import br.com.libertsolutions.crs.app.application.RequestCodes;
import br.com.libertsolutions.crs.app.checkin.CheckinActivity;
import br.com.libertsolutions.crs.app.flow.FlowActivity;
import br.com.libertsolutions.crs.app.login.LoginActivity;
import br.com.libertsolutions.crs.app.mainscreen.MainActivity;
import br.com.libertsolutions.crs.app.settings.SettingsActivity;
import br.com.libertsolutions.crs.app.settings.SettingsActivityCompat;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 27/03/2016
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

    public static void navigateToFlowScreen(@NonNull Activity activity , @NonNull Long workId) {
        activity.startActivity(new Intent(activity, FlowActivity.class)
                .putExtra(FlowActivity.EXTRA_WORK_ID, workId));
    }

    public static void navigateToCheckinScreen(@NonNull Context context, @NonNull Long flowId) {
        context.startActivity(new Intent(context, CheckinActivity.class)
                .putExtra(CheckinActivity.EXTRA_FLOW_ID, flowId));
    }
}
