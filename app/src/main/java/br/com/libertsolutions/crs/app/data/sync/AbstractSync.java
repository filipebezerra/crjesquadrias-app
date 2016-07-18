package br.com.libertsolutions.crs.app.data.sync;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import br.com.libertsolutions.crs.app.domain.pojo.Config;
import br.com.libertsolutions.crs.app.data.config.ConfigDataHelper;
import br.com.libertsolutions.crs.app.data.config.ConfigService;
import br.com.libertsolutions.crs.app.data.sync.event.SyncType;
import br.com.libertsolutions.crs.app.presentation.util.NetworkUtils;
import br.com.libertsolutions.crs.app.data.util.RxUtil;
import br.com.libertsolutions.crs.app.data.util.ServiceGenerator;
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
        if (NetworkUtils.isDeviceConnectedToInternet(mContext)) {
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
                ConfigDataHelper.setLastWorksSyncDate(mContext, config.getDataAtual());
                break;

            case FLOWS:
                ConfigDataHelper.setLastFlowsSyncDate(mContext, config.getDataAtual());
                break;

            case CHECKINS:
                ConfigDataHelper.setLastCheckinsSyncDate(mContext, config.getDataAtual());
                break;

            case COMPLETE_SYNC:
                ConfigDataHelper.setLastWorksSyncDate(mContext, config.getDataAtual());
                ConfigDataHelper.setLastFlowsSyncDate(mContext, config.getDataAtual());
                ConfigDataHelper.setLastCheckinsSyncDate(mContext, config.getDataAtual());
                break;
        }
    }
}
