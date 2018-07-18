package br.com.libertsolutions.crs.app.presentation.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.IntentCompat;
import br.com.libertsolutions.crs.app.domain.pojo.Flow;
import br.com.libertsolutions.crs.app.domain.pojo.Work;
import br.com.libertsolutions.crs.app.presentation.checkin.CheckinActivity;
import br.com.libertsolutions.crs.app.presentation.flow.FlowActivity;
import br.com.libertsolutions.crs.app.presentation.login.LoginActivity;
import br.com.libertsolutions.crs.app.presentation.main.MainActivity;
import br.com.libertsolutions.crs.app.presentation.settings.SettingsActivity;
import br.com.libertsolutions.crs.app.presentation.settings.SettingsActivityCompat;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class NavigationHelper {
    private NavigationHelper(){}

    public static void navigateToLoginScreen(@NonNull Activity activity) {
        activity.startActivity(new Intent(activity, LoginActivity.class));
    }

    public static void navigateToMainScreen(@NonNull Activity activity) {
        activity.startActivity(Intent
                .makeMainActivity(new ComponentName(activity, MainActivity.class))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void navigateToSettingsScreen(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            activity.startActivity(new Intent(activity, SettingsActivity.class));
        } else {
            activity.startActivity(new Intent(activity, SettingsActivityCompat.class));
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
