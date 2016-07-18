package br.com.libertsolutions.crs.app.data.sync;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import br.com.libertsolutions.crs.app.data.config.ConfigDataHelper;
import br.com.libertsolutions.crs.app.data.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.data.sync.event.SyncStatus;
import br.com.libertsolutions.crs.app.data.sync.event.SyncType;
import br.com.libertsolutions.crs.app.data.util.RxUtil;
import br.com.libertsolutions.crs.app.data.util.ServiceGenerator;
import br.com.libertsolutions.crs.app.domain.pojo.Work;
import br.com.libertsolutions.crs.app.data.work.WorkDataService;
import br.com.libertsolutions.crs.app.data.work.WorkRealmDataService;
import br.com.libertsolutions.crs.app.data.work.WorkService;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Classe para execução de sincronização das atualizações nos {@link Work}s ou obras.
 * <br><br>
 * Todas alterações do servidor são obtidas e salvas localmente. As alterações dos dados
 * das obras são aplicadas somente no servidor, assim consequentemente replicadas para
 * o armazenamento local.
 *
 * @author Filipe Bezerra
 * @since 0.1.1
 */
class WorksSync extends AbstractSync {

    private final WorkService mWorkService;

    private final WorkDataService mWorkDataService;

    static {
        Timber.tag(SyncService.SYNC_TAG);
    }

    public WorksSync(Context context) {
        super(context);
        mWorkDataService = new WorkRealmDataService(context);
        mWorkService = ServiceGenerator.createService(WorkService.class, context);
    }

    @Override
    protected SyncType getSyncType() {
        return SyncType.WORKS;
    }

    @Override
    protected void doSync() {
        if (!ConfigDataHelper.isInitialDataImported(mContext)) return;

        SyncEvent.send(getSyncType(), SyncStatus.IN_PROGRESS);

        final String lastSyncDate = ConfigDataHelper.getLastWorksSyncDate(mContext);
        Timber.i("Getting work updates since %s", lastSyncDate);
        mWorkService
                .getAllWithUpdates(lastSyncDate)
                .observeOn(Schedulers.io())
                .retryWhen(
                        RxUtil.timeoutException())
                .retryWhen(
                        RxUtil.exponentialBackoff(3, 5, TimeUnit.SECONDS))
                .filter(
                        worksUpdated -> !worksUpdated.isEmpty())
                .flatMap(
                        mWorkDataService::saveAll)
                .subscribe(
                        worksReceived -> {},

                        e -> Timber.e(e, "Erro obtendo atualizações nas obras"),

                        () -> {
                            syncDone();
                            SyncEvent.send(getSyncType(), SyncStatus.COMPLETED);
                        }
                );
    }
}
