package br.com.libertsolutions.crs.app.data.sync;

import android.content.Context;
import br.com.libertsolutions.crs.app.data.checkin.CheckinDataService;
import br.com.libertsolutions.crs.app.data.checkin.CheckinRealmDataService;
import br.com.libertsolutions.crs.app.data.checkin.CheckinService;
import br.com.libertsolutions.crs.app.data.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.data.sync.event.SyncType;
import br.com.libertsolutions.crs.app.domain.pojo.Checkin;
import br.com.libertsolutions.crs.app.domain.pojo.Checkins;
import rx.Subscription;
import timber.log.Timber;

import static br.com.libertsolutions.crs.app.data.config.ConfigDataHelper.isInitialDataImported;
import static br.com.libertsolutions.crs.app.data.login.LoginDataHelper.getUserLogged;
import static br.com.libertsolutions.crs.app.data.sync.SyncService.SYNC_TAG;
import static br.com.libertsolutions.crs.app.data.sync.event.SyncStatus.COMPLETED;
import static br.com.libertsolutions.crs.app.data.sync.event.SyncStatus.IN_PROGRESS;
import static br.com.libertsolutions.crs.app.data.sync.event.SyncType.CHECKINS;
import static br.com.libertsolutions.crs.app.data.util.Constants.PAGE_START;
import static br.com.libertsolutions.crs.app.data.util.RxUtil.exponentialBackoff;
import static br.com.libertsolutions.crs.app.data.util.RxUtil.timeoutException;
import static br.com.libertsolutions.crs.app.data.util.ServiceGenerator.createService;
import static java.util.concurrent.TimeUnit.SECONDS;
import static rx.schedulers.Schedulers.io;
import static timber.log.Timber.e;
import static timber.log.Timber.tag;

/**
 * Classe para execução de sincronização das atualizações nos {@link Checkin}s.
 * <br><br>
 * Todas alterações locais nos dados são enviadas para o servidor e depois todas alterações
 * remotas são obtidas e salvas localmente.
 * <br><br>
 * <b>A sincronização das alterações locais nos {@link Checkin}s devem ser obrigatoriamente
 * feita primeiramente, pois estas alterações locais irão causar modificação nos dados da
 * obra e do fluxo relacionado no servidor.</b>
 *
 * @author Filipe Bezerra
 * @since 0.1.1
 */
class CheckinsSync extends AbstractSync {

    private final CheckinDataService checkinDataService;

    private final CheckinService checkinService;

    private int currentPageCheckins = PAGE_START;

    static {
        tag(SYNC_TAG);
    }

    CheckinsSync(Context context) {
        super(context);
        checkinDataService = new CheckinRealmDataService(context);
        checkinService = createService(CheckinService.class, context);
    }

    @Override
    protected SyncType getSyncType() {
        return CHECKINS;
    }

    @Override
    protected void doSync() {
        if (!isInitialDataImported(applicationContext))
            return;

        SyncEvent.send(getSyncType(), IN_PROGRESS);
        postUpdates();
    }

    private void postUpdates() {
        Timber.i("Posting checkin updates");
        //noinspection ConstantConditions
        final String userCpf = getUserLogged(applicationContext).getCpf();
        checkinDataService
                .listPendingSynchronization()
                .observeOn(io())
                .retryWhen(timeoutException())
                .retryWhen(exponentialBackoff(3, 5, SECONDS))
                .filter(checkins -> checkins != null && !checkins.isEmpty())
                .flatMap(checkins -> checkinService.patch(userCpf, checkins))
                .flatMap(checkinDataService::saveAll)
                .doOnError(e -> e(e, "Erro enviando atualizações nos check-ins"))
                .doOnCompleted(this::getUpdates)
                .subscribe();
    }

    private void getUpdates() {
        Timber.i("Getting checkin updates since %s", getLastSyncDate());
        final Subscription subscription = checkinService
                .getAllWithUpdates(getLastSyncDate(), currentPageCheckins)
                .retryWhen(timeoutException())
                .retryWhen(exponentialBackoff(3, 5, SECONDS))
                .filter(checkins ->
                        checkins != null && (checkins.list != null && !checkins.list.isEmpty()))
                .subscribe(this::saveUpdates, e -> e(e, "Erro obtendo atualizações nos check-ins"));
        compositeSubscription.add(subscription);
    }

    private void saveUpdates(Checkins checkins) {
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
                                getUpdates();
                            }
                        })
                .subscribeOn(io())
                .subscribe();
        compositeSubscription.add(subscription);
    }
}
