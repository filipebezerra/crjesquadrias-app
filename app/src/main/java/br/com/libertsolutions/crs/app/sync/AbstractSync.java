package br.com.libertsolutions.crs.app.sync;

import android.content.Context;
import br.com.libertsolutions.crs.app.sync.event.SyncType;
import br.com.libertsolutions.crs.app.utils.network.NetworkUtil;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 02/06/2016
 * @since #
 */
abstract class AbstractSync {
    protected Context mContext;

    public AbstractSync(Context context) {
        mContext = context.getApplicationContext();
    }

    void sync() {
        if (NetworkUtil.isDeviceConnectedToInternet(mContext)) {
            doSync();
        }
    }

    protected abstract SyncType getSyncType();

    protected abstract void doSync();
}
