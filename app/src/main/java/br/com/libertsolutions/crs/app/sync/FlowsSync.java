package br.com.libertsolutions.crs.app.sync;

import android.content.Context;
import br.com.libertsolutions.crs.app.config.ConfigHelper;
import br.com.libertsolutions.crs.app.flow.Flow;
import br.com.libertsolutions.crs.app.flow.FlowDataService;
import br.com.libertsolutions.crs.app.flow.FlowRealmDataService;
import br.com.libertsolutions.crs.app.flow.FlowService;
import br.com.libertsolutions.crs.app.sync.event.SyncEvent;
import br.com.libertsolutions.crs.app.sync.event.SyncStatus;
import br.com.libertsolutions.crs.app.sync.event.SyncType;
import br.com.libertsolutions.crs.app.utils.webservice.ServiceGenerator;
import java.util.List;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 04/06/2016
 * @since #
 */
public class FlowsSync extends AbstractSync {

    private final FlowDataService mFlowDataService;

    private final FlowService mFlowService;

    public FlowsSync(Context context) {
        super(context);
        mFlowDataService = new FlowRealmDataService(context);
        mFlowService = ServiceGenerator.createService(FlowService.class, context);
    }

    @Override
    protected SyncType getSyncType() {
        return SyncType.FLOWS;
    }

    @Override
    protected void doSync() {
        if (!ConfigHelper.isInitialDataImported(mContext)) return;

        mFlowService
                .getAllWithUpdates(ConfigHelper.getLastServerSync(mContext))
                .observeOn(Schedulers.io())
                .filter(new Func1<List<Flow>, Boolean>() {
                    @Override
                    public Boolean call(List<Flow> flows) {
                        Timber.i("FlowsSync is filtering the data received from server");
                        return !flows.isEmpty();
                    }
                })
                .map(new Func1<List<Flow>, Void>() {
                    @Override
                    public Void call(List<Flow> flows) {
                        Timber.i("FlowsSync is saving data to the local storage");
                        mFlowDataService.saveAllSync(flows);
                        return null;
                    }
                })
                .subscribe(
                        new Subscriber<Void>() {
                            @Override
                            public void onStart() {
                                Timber.i("FlowsSync just started working");
                                SyncEvent.send(getSyncType(), SyncStatus.IN_PROGRESS);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e, "FlowsSync encountered a error");
                            }

                            @Override
                            public void onNext(Void ignored) {
                                //TODO: ConfigHelper.setLastServerSync(mContext, date);
                            }

                            @Override
                            public void onCompleted() {
                                Timber.i("FlowsSync just completed their work");
                                SyncEvent.send(getSyncType(), SyncStatus.COMPLETED);
                            }
                        }
                );
    }
}
