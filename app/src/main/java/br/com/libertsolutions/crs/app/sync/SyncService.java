package br.com.libertsolutions.crs.app.sync;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;

import org.greenrobot.eventbus.Subscribe;

import br.com.libertsolutions.crs.app.sync.event.EventBusManager;
import br.com.libertsolutions.crs.app.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.sync.event.SyncRequestEvent;
import br.com.libertsolutions.crs.app.sync.event.SyncType;
import timber.log.Timber;

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
 * @since 0.1.1
 */
public class SyncService extends Service {

    public static final String SYNC_TAG = "Sync";

    private SyncManager mSyncManager;

    private boolean mIsCompleteSyncRequested = false;

    private final Object mWaitMonitor = new Object();

    static {
        Timber.tag(SYNC_TAG);
    }

    @Override
    public void onCreate() {
        Timber.i("Sync service created");
        mSyncManager = new SyncManager(getApplicationContext());
        EventBusManager.register(this);
    }

    @Override
    public void onDestroy() {
        Timber.i("Sync service destroyed");
        EventBusManager.unregister(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe
    public void onSyncRequestEvent(SyncRequestEvent event) {
        Timber.i("Sync request event with %s", event.getSyncType());
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
            Timber.i("Sync event with %s in %s", event.getType(), event.getStatus());
            if (mIsCompleteSyncRequested
                    && event.getType() == CHECKINS && event.getStatus() == COMPLETED) {
                Timber.i("Checkin sync completed. Dispatching other syncs");
                mSyncManager.dispatchSync(SyncType.WORKS);
                mSyncManager.dispatchSync(SyncType.FLOWS);
                mIsCompleteSyncRequested = false;
            }
        }
    }

    public static void start(@NonNull Context context) {
        Timber.i("Starting sync service");
        final Intent intent = new Intent(context, SyncService.class);
        context.startService(intent);
    }

    public static void requestCompleteSync() {
        Timber.i("Complete sync requested");
        EventBusManager.send(new SyncRequestEvent(COMPLETE_SYNC));
    }
}
