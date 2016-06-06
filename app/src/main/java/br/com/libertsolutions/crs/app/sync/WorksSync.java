package br.com.libertsolutions.crs.app.sync;

import android.content.Context;
import br.com.libertsolutions.crs.app.config.ConfigHelper;
import br.com.libertsolutions.crs.app.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.sync.event.SyncStatus;
import br.com.libertsolutions.crs.app.sync.event.SyncType;
import br.com.libertsolutions.crs.app.utils.rx.RxUtil;
import br.com.libertsolutions.crs.app.utils.webservice.ServiceGenerator;
import br.com.libertsolutions.crs.app.work.Work;
import br.com.libertsolutions.crs.app.work.WorkDataService;
import br.com.libertsolutions.crs.app.work.WorkRealmDataService;
import br.com.libertsolutions.crs.app.work.WorkService;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Subscriber;
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
 * @version #, 06/06/2016
 * @since #
 */
public class WorksSync extends AbstractSync {

    private final WorkService mWorkService;

    private final WorkDataService mWorkDataService;

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
        if (!ConfigHelper.isInitialDataImported(mContext)) return;

        final String lastSyncDate = ConfigHelper.getLastWorksSyncDate(mContext);
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
                        new Subscriber<List<Work>>() {
                            @Override
                            public void onStart() {
                                SyncEvent.send(getSyncType(), SyncStatus.IN_PROGRESS);
                            }

                            @Override
                            public void onError(Throwable error) {
                                Timber.e(error, "Erro obtendo atualizações nas obras");
                            }

                            @Override
                            public void onNext(List<Work> worksReceived) {}

                            @Override
                            public void onCompleted() {
                                SyncEvent.send(getSyncType(), SyncStatus.COMPLETED);
                                syncDone();
                            }
                        }
                );
    }
}
