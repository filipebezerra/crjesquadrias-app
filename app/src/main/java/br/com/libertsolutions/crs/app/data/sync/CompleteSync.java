package br.com.libertsolutions.crs.app.data.sync;

import android.content.Context;
import br.com.libertsolutions.crs.app.data.checkin.CheckinDataService;
import br.com.libertsolutions.crs.app.data.checkin.CheckinRealmDataService;
import br.com.libertsolutions.crs.app.data.checkin.CheckinService;
import br.com.libertsolutions.crs.app.data.flow.FlowDataService;
import br.com.libertsolutions.crs.app.data.flow.FlowRealmDataService;
import br.com.libertsolutions.crs.app.data.flow.FlowService;
import br.com.libertsolutions.crs.app.data.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.data.sync.event.SyncType;
import br.com.libertsolutions.crs.app.data.work.WorkDataService;
import br.com.libertsolutions.crs.app.data.work.WorkRealmDataService;
import br.com.libertsolutions.crs.app.data.work.WorkService;
import br.com.libertsolutions.crs.app.domain.pojo.Checkins;
import br.com.libertsolutions.crs.app.domain.pojo.Flows;
import br.com.libertsolutions.crs.app.domain.pojo.Works;
import rx.Subscription;
import timber.log.Timber;

import static br.com.libertsolutions.crs.app.data.config.ConfigDataHelper.isInitialDataImported;
import static br.com.libertsolutions.crs.app.data.login.LoginDataHelper.getUserLogged;
import static br.com.libertsolutions.crs.app.data.sync.SyncService.SYNC_TAG;
import static br.com.libertsolutions.crs.app.data.sync.event.SyncStatus.COMPLETED;
import static br.com.libertsolutions.crs.app.data.sync.event.SyncStatus.IN_PROGRESS;
import static br.com.libertsolutions.crs.app.data.sync.event.SyncType.COMPLETE_SYNC;
import static br.com.libertsolutions.crs.app.data.util.Constants.PAGE_START;
import static br.com.libertsolutions.crs.app.data.util.RxUtil.exponentialBackoff;
import static br.com.libertsolutions.crs.app.data.util.RxUtil.timeoutException;
import static br.com.libertsolutions.crs.app.data.util.ServiceGenerator.createService;
import static java.util.concurrent.TimeUnit.SECONDS;
import static rx.schedulers.Schedulers.io;
import static timber.log.Timber.e;
import static timber.log.Timber.tag;

/**
 * @author Filipe Bezerra
 * @since 0.2.0
 */
class CompleteSync extends AbstractSync {
    private final CheckinDataService checkinDataService;
    private final CheckinService checkinService;
    private final WorkService workService;
    private final WorkDataService workDataService;
    private final FlowDataService flowDataService;
    private final FlowService flowService;

    private int currentPageWorks = PAGE_START;
    private int currentPageFlows = PAGE_START;
    private int currentPageCheckins = PAGE_START;

    static {
        tag(SYNC_TAG);
    }

    CompleteSync(Context context) {
        super(context);
        checkinDataService = new CheckinRealmDataService();
        checkinService = createService(CheckinService.class, context);
        workDataService = new WorkRealmDataService();
        workService = createService(WorkService.class, context);
        flowDataService = new FlowRealmDataService();
        flowService = createService(FlowService.class, context);
    }

    @Override
    protected SyncType getSyncType() {
        return COMPLETE_SYNC;
    }

    @Override
    protected void doSync() {
        if (!isInitialDataImported(applicationContext))
            return;

        SyncEvent.send(getSyncType(), IN_PROGRESS);
        postCheckinUpdates();
    }

    private void postCheckinUpdates() {
        Timber.i("Posting checkin updates");
        //noinspection ConstantConditions
        final String userCpf = getUserLogged(applicationContext).getCpf();
        final Subscription subscription = checkinDataService
                .listPendingSynchronization()
                .retryWhen(timeoutException())
                .retryWhen(exponentialBackoff(3, 5, SECONDS))
                .filter(checkins -> checkins != null && !checkins.isEmpty())
                .subscribeOn(io())
                .flatMap(checkins -> checkinService.patch(userCpf, checkins))
                .flatMap(checkinDataService::saveAll)
                .doOnError(e -> e(e, "Erro enviando atualizações nos check-ins"))
                .doOnCompleted(this::getWorkUpdates)
                .subscribe();
        compositeSubscription.add(subscription);
    }

    private void getWorkUpdates() {
        Timber.i("Getting work updates since %s", getLastSyncDate());
        final Subscription subscription = workService
                .getAllWithUpdates(getLastSyncDate(), currentPageWorks)
                .retryWhen(timeoutException())
                .retryWhen(exponentialBackoff(3, 5, SECONDS))
                .filter(works -> works != null && (works.list != null && !works.list.isEmpty()))
                .subscribe(this::saveWorkUpdates, e -> e(e, "Erro obtendo atualizações nas obras"));
        compositeSubscription.add(subscription);
    }

    private void saveWorkUpdates(Works works) {
        final Subscription subscription = workDataService
                .saveAll(works.list)
                .doOnError(e -> e(e, "Erro persistindo atualizações nas obras"))
                .doOnCompleted(
                        () -> {
                            if (currentPageWorks == works.totalPaginas) {
                                getFlowUpdates();
                            } else {
                                currentPageWorks++;
                                getWorkUpdates();
                            }
                        })
                .subscribeOn(io())
                .subscribe();
        compositeSubscription.add(subscription);
    }

    private void getFlowUpdates() {
        Timber.i("Getting flow updates since %s", getLastSyncDate());
        Subscription subscription = flowService
                .getAllWithUpdates(getLastSyncDate(), currentPageFlows)
                .retryWhen(timeoutException())
                .retryWhen(exponentialBackoff(3, 5, SECONDS))
                .filter(flows -> flows != null && (flows.list != null && !flows.list.isEmpty()))
                .subscribe(this::saveFlowUpdates, e -> e(e, "Erro obtendo atualizações nos fluxos"));
        compositeSubscription.add(subscription);
    }

    private void saveFlowUpdates(Flows flows) {
        final Subscription subscription = flowDataService
                .saveAll(flows.list)
                .doOnError(e -> e(e, "Erro persistindo atualizações nos fluxos"))
                .doOnCompleted(
                        () -> {
                            if (currentPageFlows == flows.totalPaginas) {
                                getCheckinUpdates();
                            } else {
                                currentPageFlows++;
                                getFlowUpdates();
                            }
                        })
                .subscribeOn(io())
                .subscribe();
        compositeSubscription.add(subscription);
    }

    private void getCheckinUpdates() {
        Timber.i("Getting checkin updates since %s", getLastSyncDate());
        final Subscription subscription = checkinService
                .getAllWithUpdates(getLastSyncDate(), currentPageCheckins)
                .retryWhen(timeoutException())
                .retryWhen(exponentialBackoff(3, 5, SECONDS))
                .filter(checkins ->
                        checkins != null && (checkins.list != null && !checkins.list.isEmpty()))
                .subscribe(this::saveCheckinUpdates,
                        e -> e(e, "Erro obtendo atualizações nos check-ins"));
        compositeSubscription.add(subscription);
    }

    private void saveCheckinUpdates(Checkins checkins) {
        final Subscription subscription = checkinDataService
                .saveAll(checkins.list)
                .doOnError(e -> e(e, "Erro persistindo atualizações nos check-ins"))
                .doOnCompleted(
                        () -> {
                            if (currentPageCheckins == checkins.totalPaginas) {
                                syncDone();
                                SyncEvent.send(getSyncType(), COMPLETED);
                                compositeSubscription.clear();
                            } else {
                                currentPageCheckins++;
                                getCheckinUpdates();
                            }
                        })
                .subscribeOn(io())
                .subscribe();
        compositeSubscription.add(subscription);
    }
}
