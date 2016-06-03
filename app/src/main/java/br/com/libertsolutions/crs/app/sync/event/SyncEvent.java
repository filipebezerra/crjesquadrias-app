package br.com.libertsolutions.crs.app.sync.event;

import android.support.annotation.NonNull;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 03/06/2016
 * @since #
 */
public class SyncEvent {
    private SyncType syncType;
    private SyncStatus syncStatus;

    private SyncEvent(@NonNull SyncType type, @NonNull SyncStatus status) {
        syncType = type;
        syncStatus = status;
    }

    public SyncType getType() {
        return syncType;
    }

    public SyncStatus getStatus() {
        return syncStatus;
    }

    public static void send(@NonNull final SyncType type, @NonNull final SyncStatus status) {
        Timber.i("SyncEvent send");
        EventBus.getDefault().post(new SyncEvent(type, status));
    }
}
