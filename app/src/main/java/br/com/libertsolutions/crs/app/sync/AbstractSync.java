package br.com.libertsolutions.crs.app.sync;

import android.content.Context;
import br.com.libertsolutions.crs.app.config.Config;
import br.com.libertsolutions.crs.app.config.ConfigHelper;
import br.com.libertsolutions.crs.app.config.ConfigService;
import br.com.libertsolutions.crs.app.sync.event.SyncType;
import br.com.libertsolutions.crs.app.utils.network.NetworkUtil;
import br.com.libertsolutions.crs.app.utils.rx.RxUtil;
import br.com.libertsolutions.crs.app.utils.webservice.ServiceGenerator;
import java.util.concurrent.TimeUnit;
import rx.schedulers.Schedulers;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 05/06/2016
 * @since #
 */
abstract class AbstractSync {

    protected Context mContext;

    private final ConfigService mConfigService;

    public AbstractSync(Context context) {
        mContext = context.getApplicationContext();
        mConfigService = ServiceGenerator.createService(ConfigService.class, context);
    }

    void sync() {
        if (NetworkUtil.isDeviceConnectedToInternet(mContext)) {
            doSync();
        }
    }

    protected abstract SyncType getSyncType();

    protected abstract void doSync();

    protected void syncDone() {
        mConfigService
                .get()
                .observeOn(Schedulers.io())
                .retryWhen(
                        RxUtil.timeoutException())
                .retryWhen(
                        RxUtil.exponentialBackoff(3, 5, TimeUnit.SECONDS))
                .doOnNext(this::updateSyncDate)
                .subscribe();
    }

    private void updateSyncDate(Config config) {
        switch (getSyncType()) {
            case WORKS:
                ConfigHelper.setLastWorksSyncDate(mContext, config.getDataAtual());
                break;
            case FLOWS:
                ConfigHelper.setLastFlowsSyncDate(mContext, config.getDataAtual());
                break;
            case CHECKINS:
                ConfigHelper.setLastCheckinsSyncDate(mContext, config.getDataAtual());
                break;
        }
    }
}
