package br.com.libertsolutions.crs.app.data.flow;

import br.com.libertsolutions.crs.app.data.util.RealmObservable;
import br.com.libertsolutions.crs.app.domain.entity.FlowEntity;
import br.com.libertsolutions.crs.app.domain.entity.WorkStepEntity;
import br.com.libertsolutions.crs.app.domain.pojo.Flow;
import br.com.libertsolutions.crs.app.domain.pojo.WorkStep;
import io.realm.RealmList;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;

/**
 * Implementação do contrato de acesso e modificação à dados dos {@link Flow}s ou fluxos.
 * É especializada para executar transações e manipular os dados utilizando a biblioteca Realm.
 *
 * @author Filipe Bezerra
 * @since 0.1.0
 * @see FlowDataService
 */
public class FlowRealmDataService implements FlowDataService {

    @Override
    public Observable<List<Flow>> list(final long workId) {
        return RealmObservable.results(
                realm -> realm.where(FlowEntity.class).equalTo(FlowEntity.FIELD_WORK_ID, workId)
                        .findAll()).map(flowEntities -> {
                            final List<Flow> flowList = new ArrayList<>(flowEntities.size());
                            for (FlowEntity flowEntity : flowEntities) {
                                flowList.add(flowFromRealm(flowEntity));
                            }
                            return flowList;
                        });
    }

    @Override
    public Observable<List<Flow>> saveAll(final List<Flow> flowList) {
        return RealmObservable.list(realm -> {
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
        }).map(flowEntities -> {
            List<Flow> list = new ArrayList<>(flowEntities.size());
            for (FlowEntity flowEntity : flowEntities) {
                list.add(flowFromRealm(flowEntity));
            }
            return list;
        });
    }

    private Flow flowFromRealm(FlowEntity flowEntity) {
        final WorkStep workStep = workStepFromRealm(flowEntity.getStep());
        final Long flowId = flowEntity.getFlowId();
        final Long workId = flowEntity.getWorkId();
        final Integer status = flowEntity.getStatus();

        return new Flow(workStep, flowId, workId, status);
    }

    private WorkStep workStepFromRealm(WorkStepEntity workStepEntity) {
        final Long workStepId = workStepEntity.getWorkStepId();
        final String name = workStepEntity.getName();
        final Integer type = workStepEntity.getType();
        final Integer order = workStepEntity.getOrder();
        final Integer goForward = workStepEntity.getGoForward();

        return new WorkStep(workStepId, order, name, type, goForward);
    }

    private WorkStepEntity toWorkStepEntity(WorkStep workStep) {
        final WorkStepEntity workStepEntity = new WorkStepEntity();
        workStepEntity.setWorkStepId(workStep.getWorkStepId());
        workStepEntity.setName(workStep.getName());
        workStepEntity.setType(workStep.getType());
        workStepEntity.setOrder(workStep.getOrder());
        workStepEntity.setGoForward(workStep.getGoForward());
        return workStepEntity;
    }

    private FlowEntity toFlowEntity(Flow flow, WorkStepEntity workStepEntity) {
        final FlowEntity flowEntity = new FlowEntity();
        flowEntity.setFlowId(flow.getFlowId());
        flowEntity.setWorkId(flow.getWorkId());
        flowEntity.setStep(workStepEntity);
        flowEntity.setStatus(flow.getStatus());
        return flowEntity;
    }
}
