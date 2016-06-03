package br.com.libertsolutions.crs.app.utils.realm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 03/06/2016
 * @since #
 */
public class RealmUtil {
    private RealmUtil() {
    }

    public static void executeTransaction(@NonNull Context context,
            @NonNull Realm.Transaction transaction) {
        Realm realm = null;
        try {
            RealmConfiguration.Builder builder = new RealmConfiguration.Builder(context);
            realm = Realm.getInstance(builder.build());
            realm.executeTransaction(transaction);
        } catch (Throwable e) {
            Timber.e(e, "executeTransaction");
        } finally {
            close(realm);
        }
    }

    private static void close(@Nullable Realm realm) {
        if (realm != null) {
            realm.close();
        }
    }
}
