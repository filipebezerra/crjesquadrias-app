package br.com.libertsolutions.crs.app.utils.logging;

import android.util.Log;
import com.crashlytics.android.Crashlytics;
import timber.log.Timber;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 02/06/2016
 * @since #
 */
public class CrashReportingTree extends Timber.Tree {
    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return;
        }

        if (t == null) {
            Crashlytics.log(priority, tag, message);
        } else {
            if (priority == Log.ERROR) {
                Crashlytics.logException(t);
            } else {
                Crashlytics.log(Log.WARN, tag, message);
            }
        }
    }
}
