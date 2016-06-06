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
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 05/06/2016
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

        mWorkService
                .getAllWithUpdates(ConfigHelper.getLastWorksSyncDate(mContext))
                .observeOn(Schedulers.io())
                .retryWhen(
                        RxUtil.timeoutException())
                .retryWhen(
                        RxUtil.exponentialBackoff(3, 5, TimeUnit.SECONDS))
                .filter(
                        works -> !works.isEmpty())
                .map(
                        (Func1<List<Work>, Void>) works -> {
                            mWorkDataService.saveAllSync(works);
                            return null;
                        })
                .subscribe(
                        new Subscriber<Void>() {
                            @Override
                            public void onStart() {
                                SyncEvent.send(getSyncType(), SyncStatus.IN_PROGRESS);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e, "WorksSync encountered a error");
                            }

                            @Override
                            public void onNext(Void ignored) {}

                            @Override
                            public void onCompleted() {
                                SyncEvent.send(getSyncType(), SyncStatus.COMPLETED);
                                syncDone();
                            }
                        }
                );
    }
}
