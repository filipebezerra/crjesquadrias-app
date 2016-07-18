package br.com.libertsolutions.crs.app.data.sync;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import br.com.libertsolutions.crs.app.data.config.ConfigDataHelper;
import br.com.libertsolutions.crs.app.domain.pojo.Flow;
import br.com.libertsolutions.crs.app.data.flow.FlowDataService;
import br.com.libertsolutions.crs.app.data.flow.FlowRealmDataService;
import br.com.libertsolutions.crs.app.data.flow.FlowService;
import br.com.libertsolutions.crs.app.data.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.data.sync.event.SyncStatus;
import br.com.libertsolutions.crs.app.data.sync.event.SyncType;
import br.com.libertsolutions.crs.app.data.util.RxUtil;
import br.com.libertsolutions.crs.app.data.util.ServiceGenerator;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Classe para execução de sincronização das atualizações nos {@link Flow}s ou fluxos.
 * <br><br>
 * Todas alterações do servidor são obtidas e salvas localmente. As alterações dos dados
 * dos fluxos são aplicadas somente no servidor, assim consequentemente replicadas para
 * o armazenamento local.
 *
 * @author Filipe Bezerra
 * @since 0.1.1
 */
class FlowsSync extends AbstractSync {

    private final FlowDataService mFlowDataService;

    private final FlowService mFlowService;

    static {
        Timber.tag(SyncService.SYNC_TAG);
    }

    public FlowsSync(Context context) {
        super(context);
        mFlowDataService = new FlowRealmDataService(context);
        mFlowService = ServiceGenerator.createService(FlowService.class, context);
    }

    @Override
    protected SyncType getSyncType() {
        return SyncType.FLOWS;
    }

    @Override
    protected void doSync() {
        if (!ConfigDataHelper.isInitialDataImported(mContext)) return;

        SyncEvent.send(getSyncType(), SyncStatus.IN_PROGRESS);

        final String lastSyncDate = ConfigDataHelper.getLastFlowsSyncDate(mContext);
        Timber.i("Getting flow updates since %s", lastSyncDate);
        mFlowService
                .getAllWithUpdates(lastSyncDate)
                .observeOn(Schedulers.io())
                .retryWhen(
                        RxUtil.timeoutException())
                .retryWhen(
                        RxUtil.exponentialBackoff(3, 5, TimeUnit.SECONDS))
                .filter(
                        flowsUpdate -> flowsUpdate != null && !flowsUpdate.isEmpty())
                .flatMap(
                        mFlowDataService::saveAll)
                .subscribe(
                        flowsReceived -> {},

                        e -> Timber.e(e, "Erro obtendo atualizações nos fluxos"),

                        () -> {
                            syncDone();
                            SyncEvent.send(getSyncType(), SyncStatus.COMPLETED);
                        }
                );
    }
}
