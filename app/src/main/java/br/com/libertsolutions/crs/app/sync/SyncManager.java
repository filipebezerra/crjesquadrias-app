package br.com.libertsolutions.crs.app.sync;

import android.content.Context;
import android.support.annotation.NonNull;
import br.com.libertsolutions.crs.app.sync.event.SyncType;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 02/06/2016
 * @since #
 */
class SyncManager {
    private Map<SyncType, AbstractSync> mSyncMap;

    SyncManager(@NonNull Context context) {
        mSyncMap = new HashMap<>();
        mSyncMap.put(SyncType.ALL, null);
        mSyncMap.put(SyncType.WORKS, new WorksSync(context));
        mSyncMap.put(SyncType.FLOWS, null);
        mSyncMap.put(SyncType.CHECKINS, null);
    }

    void dispatchSync(@NonNull SyncType syncType) {
        Timber.i("SyncManager dispatchSync");
        mSyncMap.get(syncType).sync();
    }
}
