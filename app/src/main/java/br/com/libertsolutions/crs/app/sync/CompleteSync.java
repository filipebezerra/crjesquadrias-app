package br.com.libertsolutions.crs.app.sync;

import android.content.Context;
import br.com.libertsolutions.crs.app.checkin.CheckinDataService;
import br.com.libertsolutions.crs.app.checkin.CheckinRealmDataService;
import br.com.libertsolutions.crs.app.checkin.CheckinService;
import br.com.libertsolutions.crs.app.config.ConfigHelper;
import br.com.libertsolutions.crs.app.flow.FlowDataService;
import br.com.libertsolutions.crs.app.flow.FlowRealmDataService;
import br.com.libertsolutions.crs.app.flow.FlowService;
import br.com.libertsolutions.crs.app.login.LoginHelper;
import br.com.libertsolutions.crs.app.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.sync.event.SyncStatus;
import br.com.libertsolutions.crs.app.sync.event.SyncType;
import br.com.libertsolutions.crs.app.utils.rx.RxUtil;
import br.com.libertsolutions.crs.app.utils.webservice.ServiceGenerator;
import br.com.libertsolutions.crs.app.work.WorkDataService;
import br.com.libertsolutions.crs.app.work.WorkRealmDataService;
import br.com.libertsolutions.crs.app.work.WorkService;
import java.util.concurrent.TimeUnit;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @author Filipe Bezerra
 * @since 0.2.0
 */
public class CompleteSync extends AbstractSync {
    private final CheckinDataService mCheckinDataService;
    private final CheckinService mCheckinService;
    private final WorkService mWorkService;
    private final WorkDataService mWorkDataService;
    private final FlowDataService mFlowDataService;
    private final FlowService mFlowService;

    static {
        Timber.tag(SyncService.SYNC_TAG);
    }

    public CompleteSync(Context context) {
        super(context);
        mCheckinDataService = new CheckinRealmDataService(context);
        mCheckinService = ServiceGenerator.createService(CheckinService.class, context);
        mWorkDataService = new WorkRealmDataService(context);
        mWorkService = ServiceGenerator.createService(WorkService.class, context);
        mFlowDataService = new FlowRealmDataService(context);
        mFlowService = ServiceGenerator.createService(FlowService.class, context);
    }

    @Override
    protected SyncType getSyncType() {
        return SyncType.COMPLETE_SYNC;
    }

    @Override
    protected void doSync() {
        if (!ConfigHelper.isInitialDataImported(mContext)) return;

        SyncEvent.send(getSyncType(), SyncStatus.IN_PROGRESS);
        postCheckinUpdates();
    }

    private void postCheckinUpdates() {
        Timber.i("Posting checkin updates");
        //noinspection ConstantConditions
        final String userCpf = LoginHelper.getUserLogged(mContext).getCpf();
        mCheckinDataService
                .listPendingSynchronization()
                .observeOn(Schedulers.io())
                .retryWhen(
                        RxUtil.timeoutException())
                .retryWhen(
                        RxUtil.exponentialBackoff(3, 5, TimeUnit.SECONDS))
                .filter(
                        checkinsUpdated -> checkinsUpdated != null && !checkinsUpdated.isEmpty())
                .flatMap(
                        checkinsUpdated -> mCheckinService.patch(userCpf, checkinsUpdated))
                .flatMap(
                        mCheckinDataService::saveAll)
                .subscribe(
                        checkinsSent -> {},

                        error -> Timber.e(error, "Erro enviando atualizações nos check-ins"),

                        this::getWorkUpdates
                );
    }

    private void getWorkUpdates() {
        final String lastSyncDate = ConfigHelper.getLastWorksSyncDate(mContext);
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

                        this::getFlowUpdates
                );
    }

    private void getFlowUpdates() {
        final String lastSyncDate = ConfigHelper.getLastFlowsSyncDate(mContext);
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

                        this::getCheckinUpdates
                );
    }

    private void getCheckinUpdates() {
        final String lastSyncDate = ConfigHelper.getLastCheckinsSyncDate(mContext);
        Timber.i("Getting checkin updates since %s", lastSyncDate);
        mCheckinService
                .getAllWithUpdates(lastSyncDate)
                .observeOn(Schedulers.io())
                .retryWhen(
                        RxUtil.timeoutException())
                .retryWhen(
                        RxUtil.exponentialBackoff(3, 5, TimeUnit.SECONDS))
                .filter(
                        checkinsUpdated -> checkinsUpdated != null && !checkinsUpdated.isEmpty())
                .flatMap(
                        mCheckinDataService::saveAll)
                .subscribe(
                        checkinsReceived -> {},

                        error -> Timber.e(error, "Erro obtendo atualizações nos check-ins"),

                        () -> {
                            syncDone();
                            SyncEvent.send(getSyncType(), SyncStatus.COMPLETED);
                        }
                );
    }
}
