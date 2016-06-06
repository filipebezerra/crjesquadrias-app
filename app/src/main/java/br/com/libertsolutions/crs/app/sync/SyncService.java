package br.com.libertsolutions.crs.app.sync;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import br.com.libertsolutions.crs.app.sync.event.EventBusManager;
import br.com.libertsolutions.crs.app.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.sync.event.SyncRequestEvent;
import br.com.libertsolutions.crs.app.sync.event.SyncType;
import org.greenrobot.eventbus.Subscribe;

import static br.com.libertsolutions.crs.app.sync.event.SyncStatus.COMPLETED;
import static br.com.libertsolutions.crs.app.sync.event.SyncType.CHECKINS;
import static br.com.libertsolutions.crs.app.sync.event.SyncType.COMPLETE_SYNC;

/**
 * Serviço responsável por solicitar a sincronização dos dados. Este serviço também controla
 * a ordem de sincronização, neste caso em especial, pois os dados de check-in modificados
 * localmente, alteram os dados na obra e no fluxo relacionado, devendo serem sincronizados
 * prioritariamente.
 *
 * @author Filipe Bezerra
 * @version #, 06/06/2016
 * @since #
 */
public class SyncService extends Service {

    private SyncManager mSyncManager;

    private boolean mIsCompleteSyncRequested = false;

    private final Object mWaitMonitor = new Object();

    @Override
    public void onCreate() {
        mSyncManager = new SyncManager(getApplicationContext());
        EventBusManager.register(this);
    }

    @Override
    public void onDestroy() {
        EventBusManager.unregister(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe
    public void onSyncRequestEvent(SyncRequestEvent event) {
        if (event.getSyncType() == COMPLETE_SYNC) {
            mIsCompleteSyncRequested = true;
            mSyncManager.dispatchSync(CHECKINS);
        } else {
            mSyncManager.dispatchSync(event.getSyncType());
        }
    }

    @Subscribe
    public void onSyncEvent(SyncEvent event) {
        synchronized (mWaitMonitor) {
            if (mIsCompleteSyncRequested
                    && event.getType() == CHECKINS && event.getStatus() == COMPLETED) {
                mSyncManager.dispatchSync(SyncType.WORKS);
                mSyncManager.dispatchSync(SyncType.FLOWS);
                mIsCompleteSyncRequested = false;
            }
        }
    }

    public static void start(@NonNull Context context) {
        final Intent intent = new Intent(context, SyncService.class);
        context.startService(intent);
    }

    public static void requestSync(@NonNull SyncType syncType) {
        EventBusManager.send(new SyncRequestEvent(syncType));
    }

    public static void requestCompleteSync() {
        EventBusManager.send(new SyncRequestEvent(COMPLETE_SYNC));
    }
}
