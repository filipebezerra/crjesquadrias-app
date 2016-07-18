package br.com.libertsolutions.crs.app;

import android.app.Application;
import br.com.libertsolutions.crs.app.presentation.util.CrashReportingTree;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class ApplicationImpl extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initLoggingWithTimber();
        initCrashReportingWithFabric();
        initDataStorageWithRealm();
    }

    private void initLoggingWithTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    private void initCrashReportingWithFabric() {
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();
        Fabric.with(this, crashlyticsKit);
    }

    private void initDataStorageWithRealm() {
        RealmConfiguration configuration = new RealmConfiguration.Builder(this)
                .name("crs.realm")
                .build();
        Realm.setDefaultConfiguration(configuration);
    }
}
