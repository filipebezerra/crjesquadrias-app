package br.com.libertsolutions.crs.app.data.sync;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import br.com.libertsolutions.crs.app.domain.pojo.Checkin;
import br.com.libertsolutions.crs.app.data.checkin.CheckinDataService;
import br.com.libertsolutions.crs.app.data.checkin.CheckinRealmDataService;
import br.com.libertsolutions.crs.app.data.checkin.CheckinService;
import br.com.libertsolutions.crs.app.data.config.ConfigDataHelper;
import br.com.libertsolutions.crs.app.data.login.LoginDataHelper;
import br.com.libertsolutions.crs.app.data.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.data.sync.event.SyncStatus;
import br.com.libertsolutions.crs.app.data.sync.event.SyncType;
import br.com.libertsolutions.crs.app.data.util.RxUtil;
import br.com.libertsolutions.crs.app.data.util.ServiceGenerator;
import rx.schedulers.Schedulers;
import timber.log.Timber;

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

    private final CheckinDataService mCheckinDataService;

    private final CheckinService mCheckinService;

    static {
        Timber.tag(SyncService.SYNC_TAG);
    }

    public CheckinsSync(Context context) {
        super(context);
        mCheckinDataService = new CheckinRealmDataService(context);
        mCheckinService = ServiceGenerator.createService(CheckinService.class, context);
    }

    @Override
    protected SyncType getSyncType() {
        return SyncType.CHECKINS;
    }

    @Override
    protected void doSync() {
        if (!ConfigDataHelper.isInitialDataImported(mContext)) return;

        SyncEvent.send(getSyncType(), SyncStatus.IN_PROGRESS);
        postUpdates();
    }

    private void postUpdates() {
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

                        this::getUpdates
                );
    }

    private void getUpdates() {
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
