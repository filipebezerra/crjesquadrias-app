package br.com.libertsolutions.crs.app.data.sync;

import android.content.Context;
import br.com.libertsolutions.crs.app.data.flow.FlowDataService;
import br.com.libertsolutions.crs.app.data.flow.FlowRealmDataService;
import br.com.libertsolutions.crs.app.data.flow.FlowService;
import br.com.libertsolutions.crs.app.data.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.data.sync.event.SyncType;
import br.com.libertsolutions.crs.app.domain.pojo.Flow;
import br.com.libertsolutions.crs.app.domain.pojo.Flows;
import rx.Subscription;
import timber.log.Timber;

import static br.com.libertsolutions.crs.app.data.config.ConfigDataHelper.isInitialDataImported;
import static br.com.libertsolutions.crs.app.data.sync.SyncService.SYNC_TAG;
import static br.com.libertsolutions.crs.app.data.sync.event.SyncStatus.COMPLETED;
import static br.com.libertsolutions.crs.app.data.sync.event.SyncStatus.IN_PROGRESS;
import static br.com.libertsolutions.crs.app.data.sync.event.SyncType.FLOWS;
import static br.com.libertsolutions.crs.app.data.util.Constants.PAGE_START;
import static br.com.libertsolutions.crs.app.data.util.RxUtil.exponentialBackoff;
import static br.com.libertsolutions.crs.app.data.util.RxUtil.timeoutException;
import static br.com.libertsolutions.crs.app.data.util.ServiceGenerator.createService;
import static java.util.concurrent.TimeUnit.SECONDS;
import static rx.schedulers.Schedulers.io;
import static timber.log.Timber.e;
import static timber.log.Timber.tag;

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

    private final FlowDataService flowDataService;

    private final FlowService flowService;

    private int currentPageFlows = PAGE_START;

    static {
        tag(SYNC_TAG);
    }

    FlowsSync(Context context) {
        super(context);
        flowDataService = new FlowRealmDataService();
        flowService = createService(FlowService.class, context);
    }

    @Override
    protected SyncType getSyncType() {
        return FLOWS;
    }

    @Override
    protected void doSync() {
        if (!isInitialDataImported(applicationContext))
            return;

        SyncEvent.send(getSyncType(), IN_PROGRESS);
        getUpdates();
    }

    private void getUpdates() {
        Timber.i("Getting flow updates since %s", getLastSyncDate());
        Subscription subscription = flowService
                .getAllWithUpdates(getLastSyncDate(), currentPageFlows)
                .retryWhen(timeoutException())
                .retryWhen(exponentialBackoff(3, 5, SECONDS))
                .filter(flows -> flows != null && (flows.list != null && !flows.list.isEmpty()))
                .subscribe(this::saveUpdates, e -> e(e, "Erro obtendo atualizações nos fluxos"));
        compositeSubscription.add(subscription);
    }

    private void saveUpdates(Flows flows) {
        final Subscription subscription = flowDataService
                .saveAll(flows.list)
                .doOnError(e -> e(e, "Erro persistindo atualizações nos fluxos"))
                .doOnCompleted(
                        () -> {
                            if (currentPageFlows == flows.totalPaginas) {
                                syncDone();
                                SyncEvent.send(getSyncType(), COMPLETED);
                                compositeSubscription.clear();
                            } else {
                                currentPageFlows++;
                                getUpdates();
                            }
                        })
                .subscribeOn(io())
                .subscribe();
        compositeSubscription.add(subscription);
    }
}
