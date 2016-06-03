package br.com.libertsolutions.crs.app.application;

import android.app.Application;
import br.com.libertsolutions.crs.app.BuildConfig;
import br.com.libertsolutions.crs.app.utils.logging.CrashReportingTree;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 18/03/2016
 * @since 0.1.0
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        initLoggingWithTimber();

        Fabric.with(this, new Crashlytics());

        RealmConfiguration configuration = new RealmConfiguration.Builder(this)
                .name("crs.realm")
                .build();
        Realm.setDefaultConfiguration(configuration);
    }

    private void initLoggingWithTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }
}
