package br.com.libertsolutions.crs.app;

import android.app.Application;
import br.com.libertsolutions.crs.app.presentation.util.CrashReportingTree;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.squareup.leakcanary.LeakCanary;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class ApplicationImpl extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initMemoryLeakDetector();
        initLoggingWithTimber();
        initCrashReportingWithFabric();
        initDataStorageWithRealm();
    }

    private void initMemoryLeakDetector() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    private void initLoggingWithTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
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
