package br.com.libertsolutions.crs.app.data.sync;

import android.content.Context;
import br.com.libertsolutions.crs.app.data.config.ConfigService;
import br.com.libertsolutions.crs.app.data.sync.event.SyncType;
import br.com.libertsolutions.crs.app.domain.pojo.Config;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static android.text.TextUtils.isEmpty;
import static br.com.libertsolutions.crs.app.data.config.ConfigDataHelper.getLastCheckinsSyncDate;
import static br.com.libertsolutions.crs.app.data.config.ConfigDataHelper.setLastCheckinsSyncDate;
import static br.com.libertsolutions.crs.app.data.config.ConfigDataHelper.setLastFlowsSyncDate;
import static br.com.libertsolutions.crs.app.data.config.ConfigDataHelper.setLastWorksSyncDate;
import static br.com.libertsolutions.crs.app.data.sync.SyncService.SYNC_TAG;
import static br.com.libertsolutions.crs.app.data.util.RxUtil.exponentialBackoff;
import static br.com.libertsolutions.crs.app.data.util.RxUtil.timeoutException;
import static br.com.libertsolutions.crs.app.data.util.ServiceGenerator.createService;
import static br.com.libertsolutions.crs.app.presentation.util.NetworkUtils.isDeviceConnectedToInternet;
import static java.util.concurrent.TimeUnit.SECONDS;
import static rx.schedulers.Schedulers.io;

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

    final Context applicationContext;

    final CompositeSubscription compositeSubscription;

    private final ConfigService configService;

    private String lastSyncDate;

    static {
        Timber.tag(SYNC_TAG);
    }

    AbstractSync(Context context) {
        applicationContext = context.getApplicationContext();
        configService = createService(ConfigService.class, context);
        compositeSubscription = new CompositeSubscription();
    }

    void sync() {
        if (isDeviceConnectedToInternet(applicationContext)) {
            Timber.i("Doing sync");
            doSync();
        } else {
            Timber.i("No network connectivity");
        }
    }

    protected abstract SyncType getSyncType();

    protected abstract void doSync();

    void syncDone() {
        Timber.i("Sync done");
        configService
                .get()
                .observeOn(io())
                .retryWhen(timeoutException())
                .retryWhen(exponentialBackoff(3, 5, SECONDS))
                .doOnNext(this::updateSyncDate)
                .subscribe();
    }

    private void updateSyncDate(Config config) {
        Timber.i("Updating %s config", getSyncType());
        final String currentDate = config.currentDate;
        switch (getSyncType()) {
            case WORKS:
                setLastWorksSyncDate(applicationContext, currentDate);
                break;

            case FLOWS:
                setLastFlowsSyncDate(applicationContext, currentDate);
                break;

            case CHECKINS:
                setLastCheckinsSyncDate(applicationContext, currentDate);
                break;

            case COMPLETE_SYNC:
                setLastWorksSyncDate(applicationContext, currentDate);
                setLastFlowsSyncDate(applicationContext, currentDate);
                setLastCheckinsSyncDate(applicationContext, currentDate);
                break;
        }
    }

    String getLastSyncDate() {
        if (isEmpty(lastSyncDate)) {
            lastSyncDate = getLastCheckinsSyncDate(applicationContext);
        }
        return lastSyncDate;
    }
}
