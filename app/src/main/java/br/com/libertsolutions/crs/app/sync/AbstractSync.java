package br.com.libertsolutions.crs.app.sync;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import br.com.libertsolutions.crs.app.config.Config;
import br.com.libertsolutions.crs.app.config.ConfigHelper;
import br.com.libertsolutions.crs.app.config.ConfigService;
import br.com.libertsolutions.crs.app.sync.event.SyncType;
import br.com.libertsolutions.crs.app.utils.network.NetworkUtil;
import br.com.libertsolutions.crs.app.utils.rx.RxUtil;
import br.com.libertsolutions.crs.app.utils.webservice.ServiceGenerator;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Classe abstrata base para executores de sincronização.
 *
 * @author Filipe Bezerra
 * @since 0.1.1
 * @see CheckinsSync
 * @see FlowsSync
 * @see WorksSync
 */
abstract class AbstractSync {

    protected Context mContext;

    private final ConfigService mConfigService;

    static {
        Timber.tag(SyncService.SYNC_TAG);
    }

    public AbstractSync(Context context) {
        mContext = context.getApplicationContext();
        mConfigService = ServiceGenerator.createService(ConfigService.class, context);
    }

    void sync() {
        if (NetworkUtil.isDeviceConnectedToInternet(mContext)) {
            Timber.i("Doing sync");
            doSync();
        } else {
            Timber.i("No network connectivity");
        }
    }

    protected abstract SyncType getSyncType();

    protected abstract void doSync();

    protected void syncDone() {
        Timber.i("Sync done");
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
        Timber.i("Updating %s config", getSyncType());
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

            case COMPLETE_SYNC:
                ConfigHelper.setLastWorksSyncDate(mContext, config.getDataAtual());
                ConfigHelper.setLastFlowsSyncDate(mContext, config.getDataAtual());
                ConfigHelper.setLastCheckinsSyncDate(mContext, config.getDataAtual());
                break;
        }
    }
}
