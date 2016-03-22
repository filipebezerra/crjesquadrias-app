package br.com.libertsolutions.crs.app.application;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;

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

        RealmConfiguration configuration = new RealmConfiguration.Builder(this)
                .name("crs.realm")
                .build();
        Realm.setDefaultConfiguration(configuration);
    }
}
