package br.com.libertsolutions.crs.app.sync.event;

import android.support.annotation.NonNull;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 03/06/2016
 * @since #
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
