package br.com.libertsolutions.crs.app.flow;

import android.content.Context;
import br.com.libertsolutions.crs.app.rx.RealmObservable;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 28/03/2016
 * @since 0.1.0
 */
public class FlowRealmDataService implements FlowDataService {
    private final Context mContext;

    public FlowRealmDataService(Context context) {
        mContext = context;
    }

    @Override
    public Observable<List<Flow>> list(final long workId) {
        return RealmObservable.results(mContext, new Func1<Realm, RealmResults<FlowEntity>>() {
            @Override
            public RealmResults<FlowEntity> call(Realm realm) {
                return realm.where(FlowEntity.class).equalTo(FlowEntity.FIELD_WORK_ID, workId)
                        .findAll();
            }
        }).map(new Func1<RealmResults<FlowEntity>, List<Flow>>() {
            @Override
            public List<Flow> call(RealmResults<FlowEntity> flowEntities) {
                final List<Flow> flowList = new ArrayList<>(flowEntities.size());
                for (FlowEntity flowEntity : flowEntities) {
                    flowList.add(flowFromRealm(flowEntity));
                }
                return flowList;
            }
        });
    }

    @Override
    public Observable<List<Flow>> saveAll(final List<Flow> flowList) {
        return RealmObservable.list(mContext, new Func1<Realm, RealmList<FlowEntity>>() {
            @Override
            public RealmList<FlowEntity> call(Realm realm) {
                List<FlowEntity> flowEntityList = new ArrayList<>(flowList.size());

                for (Flow flow : flowList) {
                    WorkStepEntity workStepEntity = new WorkStepEntity();
                    workStepEntity.setWorkStepId(flow.getStep().getWorkStepId());
                    workStepEntity.setName(flow.getStep().getName());
                    workStepEntity.setType(flow.getStep().getType());
                    workStepEntity.setOrder(flow.getStep().getOrder());
                    workStepEntity.setGoForward(flow.getStep().getGoForward());
                    workStepEntity = realm.copyToRealmOrUpdate(workStepEntity);

                    FlowEntity flowEntity = new FlowEntity();
                    flowEntity.setFlowId(flow.getFlowId());
                    flowEntity.setWorkId(flow.getWorkId());
                    flowEntity.setStep(workStepEntity);
                    flowEntity.setStatus(flow.getStatus());

                    flowEntityList.add(realm.copyToRealmOrUpdate(flowEntity));
                }

                return new RealmList<>(flowEntityList.toArray(
                        new FlowEntity[flowEntityList.size()]));
            }
        }).map(new Func1<RealmList<FlowEntity>, List<Flow>>() {
            @Override
            public List<Flow> call(RealmList<FlowEntity> flowEntities) {
                List<Flow> list = new ArrayList<>(flowEntities.size());
                for (FlowEntity flowEntity : flowEntities) {
                    list.add(flowFromRealm(flowEntity));
                }
                return list;
            }
        });
    }

    private static Flow flowFromRealm(FlowEntity flowEntity) {
        final WorkStep workStep = workStepFromRealm(flowEntity.getStep());
        final Long flowId = flowEntity.getFlowId();
        final Long workId = flowEntity.getWorkId();
        final Integer status = flowEntity.getStatus();

        return new Flow(workStep, flowId, workId, status);
    }

    private static WorkStep workStepFromRealm(WorkStepEntity workStepEntity) {
        final Long workStepId = workStepEntity.getWorkStepId();
        final String name = workStepEntity.getName();
        final Integer type = workStepEntity.getType();
        final Integer order = workStepEntity.getOrder();
        final Integer goForward = workStepEntity.getGoForward();

        return new WorkStep(workStepId, order, name, type, goForward);
    }
}
