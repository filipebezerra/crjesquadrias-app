package br.com.libertsolutions.crs.app.data.sync;

import android.content.Context;
import br.com.libertsolutions.crs.app.data.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.data.sync.event.SyncType;
import br.com.libertsolutions.crs.app.data.work.WorkDataService;
import br.com.libertsolutions.crs.app.data.work.WorkRealmDataService;
import br.com.libertsolutions.crs.app.data.work.WorkService;
import br.com.libertsolutions.crs.app.domain.pojo.Work;
import br.com.libertsolutions.crs.app.domain.pojo.Works;
import rx.Subscription;
import timber.log.Timber;

import static br.com.libertsolutions.crs.app.data.config.ConfigDataHelper.isInitialDataImported;
import static br.com.libertsolutions.crs.app.data.sync.SyncService.SYNC_TAG;
import static br.com.libertsolutions.crs.app.data.sync.event.SyncStatus.COMPLETED;
import static br.com.libertsolutions.crs.app.data.sync.event.SyncStatus.IN_PROGRESS;
import static br.com.libertsolutions.crs.app.data.sync.event.SyncType.WORKS;
import static br.com.libertsolutions.crs.app.data.util.Constants.PAGE_START;
import static br.com.libertsolutions.crs.app.data.util.RxUtil.exponentialBackoff;
import static br.com.libertsolutions.crs.app.data.util.RxUtil.timeoutException;
import static br.com.libertsolutions.crs.app.data.util.ServiceGenerator.createService;
import static java.util.concurrent.TimeUnit.SECONDS;
import static rx.schedulers.Schedulers.io;
import static timber.log.Timber.e;
import static timber.log.Timber.tag;

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

    private final WorkService workService;

    private final WorkDataService workDataService;

    private int currentPageWorks = PAGE_START;

    static {
        tag(SYNC_TAG);
    }

    WorksSync(Context context) {
        super(context);
        workDataService = new WorkRealmDataService(context);
        workService = createService(WorkService.class, context);
    }

    @Override
    protected SyncType getSyncType() {
        return WORKS;
    }

    @Override
    protected void doSync() {
        if (!isInitialDataImported(applicationContext))
            return;

        SyncEvent.send(getSyncType(), IN_PROGRESS);
        getUpdates();
    }

    private void getUpdates() {
        Timber.i("Getting work updates since %s", getLastSyncDate());
        final Subscription subscription = workService
                .getAllWithUpdates(getLastSyncDate(), currentPageWorks)
                .retryWhen(timeoutException())
                .retryWhen(exponentialBackoff(3, 5, SECONDS))
                .filter(works -> works != null && (works.list != null && !works.list.isEmpty()))
                .subscribe(this::saveUpdates, e -> e(e, "Erro obtendo atualizações nas obras"));
        compositeSubscription.add(subscription);
    }

    private void saveUpdates(Works works) {
        final Subscription subscription = workDataService
                .saveAll(works.list)
                .doOnError(e -> e(e, "Erro persistindo atualizações nas obras"))
                .doOnCompleted(
                        () -> {
                            if (currentPageWorks == works.totalPaginas) {
                                syncDone();
                                SyncEvent.send(getSyncType(), COMPLETED);
                                compositeSubscription.clear();
                            } else {
                                currentPageWorks++;
                                getUpdates();
                            }
                        })
                .subscribeOn(io())
                .subscribe();
        compositeSubscription.add(subscription);
    }
}
