package br.com.libertsolutions.crs.app.sync;

import android.content.Context;
import br.com.libertsolutions.crs.app.checkin.Checkin;
import br.com.libertsolutions.crs.app.checkin.CheckinDataService;
import br.com.libertsolutions.crs.app.checkin.CheckinRealmDataService;
import br.com.libertsolutions.crs.app.checkin.CheckinService;
import br.com.libertsolutions.crs.app.config.ConfigHelper;
import br.com.libertsolutions.crs.app.login.LoginHelper;
import br.com.libertsolutions.crs.app.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.sync.event.SyncStatus;
import br.com.libertsolutions.crs.app.sync.event.SyncType;
import br.com.libertsolutions.crs.app.utils.rx.RxUtil;
import br.com.libertsolutions.crs.app.utils.webservice.ServiceGenerator;
import java.util.concurrent.TimeUnit;
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
 * @version #, 06/06/2016
 * @since #
 */
public class CheckinsSync extends AbstractSync {

    private final CheckinDataService mCheckinDataService;

    private final CheckinService mCheckinService;

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
        if (!ConfigHelper.isInitialDataImported(mContext)) return;

        SyncEvent.send(getSyncType(), SyncStatus.IN_PROGRESS);
        postUpdates();
    }

    private void postUpdates() {
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
                        checkinsUpdated -> !checkinsUpdated.isEmpty())
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
        final String lastSyncDate = ConfigHelper.getLastCheckinsSyncDate(mContext);
        mCheckinService
                .getAllWithUpdates(lastSyncDate)
                .observeOn(Schedulers.io())
                .retryWhen(
                        RxUtil.timeoutException())
                .retryWhen(
                        RxUtil.exponentialBackoff(3, 5, TimeUnit.SECONDS))
                .filter(
                        checkinsUpdated -> !checkinsUpdated.isEmpty())
                .flatMap(
                        mCheckinDataService::saveAll)
                .subscribe(
                        checkinsReceived -> {
                            SyncEvent.send(getSyncType(), SyncStatus.COMPLETED);
                            syncDone();
                        },

                        error -> Timber.e(error, "Erro obtendo atualizações nos check-ins")
                );
    }
}
