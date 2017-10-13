package br.com.libertsolutions.crs.app;

import android.app.Application;
import br.com.libertsolutions.crs.app.presentation.util.ReleaseTree;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.squareup.leakcanary.LeakCanary;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

import static br.com.libertsolutions.crs.app.BuildConfig.DEBUG;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class ApplicationImpl extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initMemoryLeakDetector();
        initLogging();
        initCrashReporting();
        initLocalDataStorage();
    }

    private void initMemoryLeakDetector() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    private void initLogging() {
        if (DEBUG) {
            Timber.plant(new DebugTree());
        } else {
            Timber.plant(new ReleaseTree());
        }
    }

    private void initCrashReporting() {
        if (!DEBUG) {
            Fabric.with(this, new Crashlytics(), new Answers());
        }
    }

    private void initLocalDataStorage() {
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("crs.realm")
                .schemaVersion(BuildConfig.SCHEMA_VERSION)
                .build();
        Realm.setDefaultConfiguration(configuration);
    }
}
