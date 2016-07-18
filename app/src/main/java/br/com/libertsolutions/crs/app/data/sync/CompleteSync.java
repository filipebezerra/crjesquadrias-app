package br.com.libertsolutions.crs.app.data.sync;

import android.content.Context;
import br.com.libertsolutions.crs.app.data.checkin.CheckinDataService;
import br.com.libertsolutions.crs.app.data.checkin.CheckinRealmDataService;
import br.com.libertsolutions.crs.app.data.checkin.CheckinService;
import br.com.libertsolutions.crs.app.data.config.ConfigDataHelper;
import br.com.libertsolutions.crs.app.data.flow.FlowDataService;
import br.com.libertsolutions.crs.app.data.flow.FlowRealmDataService;
import br.com.libertsolutions.crs.app.data.flow.FlowService;
import br.com.libertsolutions.crs.app.data.login.LoginDataHelper;
import br.com.libertsolutions.crs.app.data.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.data.sync.event.SyncStatus;
import br.com.libertsolutions.crs.app.data.sync.event.SyncType;
import br.com.libertsolutions.crs.app.data.util.RxUtil;
import br.com.libertsolutions.crs.app.data.util.ServiceGenerator;
import br.com.libertsolutions.crs.app.data.work.WorkDataService;
import br.com.libertsolutions.crs.app.data.work.WorkRealmDataService;
import br.com.libertsolutions.crs.app.data.work.WorkService;
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
        if (!ConfigDataHelper.isInitialDataImported(mContext)) return;

        SyncEvent.send(getSyncType(), SyncStatus.IN_PROGRESS);
        postCheckinUpdates();
    }

    private void postCheckinUpdates() {
        Timber.i("Posting checkin updates");
        //noinspection ConstantConditions
        final String userCpf = LoginDataHelper.getUserLogged(mContext).getCpf();
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

                        this::getFlowUpdates
                );
    }

    private void getFlowUpdates() {
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

                        this::getCheckinUpdates
                );
    }

    private void getCheckinUpdates() {
        final String lastSyncDate = ConfigDataHelper.getLastCheckinsSyncDate(mContext);
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
