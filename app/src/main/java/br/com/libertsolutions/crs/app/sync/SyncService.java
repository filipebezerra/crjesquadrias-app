package br.com.libertsolutions.crs.app.sync;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import br.com.libertsolutions.crs.app.sync.event.EventBusManager;
import br.com.libertsolutions.crs.app.sync.event.SyncRequestEvent;
import br.com.libertsolutions.crs.app.sync.event.SyncType;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 02/06/2016
 * @since #
 */
public class SyncService extends Service {

    private SyncManager mSyncManager;

    @Override
    public void onCreate() {
        Timber.i("SyncService onCreate");
        mSyncManager = new SyncManager(getApplicationContext());
        EventBusManager.register(this);
    }

    @Override
    public void onDestroy() {
        Timber.i("SyncService onDestroy");
        EventBusManager.unregister(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe
    public void onEvent(SyncRequestEvent event) {
        Timber.i("SyncService onEvent with %s sync type", event.getSyncType());
        mSyncManager.dispatchSync(event.getSyncType());
    }

    public static void start(@NonNull Context context) {
        final Intent intent = new Intent(context, SyncService.class);
        context.startService(intent);
    }

    public static void request(@NonNull SyncType syncType) {
        EventBusManager.send(new SyncRequestEvent(syncType));
    }
}
