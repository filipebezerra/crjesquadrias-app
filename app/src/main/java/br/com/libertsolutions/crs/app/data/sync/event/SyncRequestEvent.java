package br.com.libertsolutions.crs.app.data.sync.event;

import android.support.annotation.NonNull;

/**
 * @author Filipe Bezerra
 * @since 0.1.1
 */
public class SyncRequestEvent {
    private final SyncType mSyncType;

    public SyncRequestEvent(@NonNull SyncType syncType) {
        mSyncType = syncType;
    }

    public SyncType getSyncType() {
        return mSyncType;
    }
}
