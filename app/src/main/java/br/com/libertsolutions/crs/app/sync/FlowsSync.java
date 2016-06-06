package br.com.libertsolutions.crs.app.sync;

import android.content.Context;
import br.com.libertsolutions.crs.app.config.ConfigHelper;
import br.com.libertsolutions.crs.app.flow.Flow;
import br.com.libertsolutions.crs.app.flow.FlowDataService;
import br.com.libertsolutions.crs.app.flow.FlowRealmDataService;
import br.com.libertsolutions.crs.app.flow.FlowService;
import br.com.libertsolutions.crs.app.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.sync.event.SyncStatus;
import br.com.libertsolutions.crs.app.sync.event.SyncType;
import br.com.libertsolutions.crs.app.utils.rx.RxUtil;
import br.com.libertsolutions.crs.app.utils.webservice.ServiceGenerator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Subscriber;
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
 * @version #, 06/06/2016
 * @since #
 */
public class FlowsSync extends AbstractSync {

    private final FlowDataService mFlowDataService;

    private final FlowService mFlowService;

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
        if (!ConfigHelper.isInitialDataImported(mContext)) return;

        final String lastSyncDate = ConfigHelper.getLastFlowsSyncDate(mContext);
        mFlowService
                .getAllWithUpdates(lastSyncDate)
                .observeOn(Schedulers.io())
                .retryWhen(
                        RxUtil.timeoutException())
                .retryWhen(
                        RxUtil.exponentialBackoff(3, 5, TimeUnit.SECONDS))
                .filter(
                        flowsUpdate -> !flowsUpdate.isEmpty())
                .flatMap(
                        mFlowDataService::saveAll)
                .subscribe(
                        new Subscriber<List<Flow>>() {
                            public void onStart() {
                                SyncEvent.send(getSyncType(), SyncStatus.IN_PROGRESS);
                            }

                            @Override
                            public void onError(Throwable error) {
                                Timber.e(error, "Erro obtendo atualizações nos fluxos");
                            }

                            @Override
                            public void onNext(List<Flow> flowsReceived) {}

                            @Override
                            public void onCompleted() {
                                SyncEvent.send(getSyncType(), SyncStatus.COMPLETED);
                                syncDone();
                            }
                        }
                );
    }
}
