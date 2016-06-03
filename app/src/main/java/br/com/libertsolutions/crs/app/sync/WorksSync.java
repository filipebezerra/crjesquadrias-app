package br.com.libertsolutions.crs.app.sync;

import android.content.Context;
import br.com.libertsolutions.crs.app.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.sync.event.SyncStatus;
import br.com.libertsolutions.crs.app.sync.event.SyncType;
import br.com.libertsolutions.crs.app.utils.webservice.ServiceGenerator;
import br.com.libertsolutions.crs.app.work.Work;
import br.com.libertsolutions.crs.app.work.WorkDataService;
import br.com.libertsolutions.crs.app.work.WorkRealmDataService;
import br.com.libertsolutions.crs.app.work.WorkService;
import java.util.List;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 03/06/2016
 * @since #
 */
public class WorksSync extends AbstractSync {
    private WorkService mWorkService;

    private WorkDataService mWorkDataService;

    public WorksSync(Context context) {
        super(context);
        mWorkService = ServiceGenerator.createService(WorkService.class, context);
        mWorkDataService = new WorkRealmDataService(context);
    }

    @Override
    protected SyncType getSyncType() {
        return SyncType.WORKS;
    }

    @Override
    protected void doSync() {
        mWorkService
                .getAll()
                .observeOn(Schedulers.newThread())
                .filter(new Func1<List<Work>, Boolean>() {
                    @Override
                    public Boolean call(List<Work> works) {
                        Timber.i("WorksSync filtering");
                        return !works.isEmpty();
                    }
                })
                .map(new Func1<List<Work>, Void>() {
                    @Override
                    public Void call(List<Work> works) {
                        Timber.i("WorksSync saving data");
                        mWorkDataService.saveAllSync(works);
                        return null;
                    }
                })
                .subscribe(
                        new Subscriber<Void>() {
                            @Override
                            public void onStart() {
                                Timber.i("WorksSync onStart");
                                SyncEvent.send(getSyncType(), SyncStatus.IN_PROGRESS);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e, "WorksSync onError");
                            }

                            @Override
                            public void onNext(Void ignored) {}

                            @Override
                            public void onCompleted() {
                                Timber.i("WorksSync onCompleted");
                                SyncEvent.send(getSyncType(), SyncStatus.COMPLETED);
                            }
                        }
                );
    }
}
