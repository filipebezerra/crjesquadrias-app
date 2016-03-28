package br.com.libertsolutions.crs.app.flow;

import android.support.annotation.IntRange;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import static br.com.libertsolutions.crs.app.flow.Flow.STATUS_FINISHED;
import static br.com.libertsolutions.crs.app.flow.Flow.STATUS_PENDING;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 21/03/2016
 * @since 0.1.0
 */
public class FlowEntity extends RealmObject {
    public static final String FIELD_WORK_ID = "workId";

    private WorkStepEntity step;

    @PrimaryKey
    private Long flowId;

    @Required
    private Long workId;

    private Integer status;

    public WorkStepEntity getStep() {
        return step;
    }

    public void setStep(WorkStepEntity step) {
        this.step = step;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(@IntRange(from = STATUS_PENDING, to = STATUS_FINISHED) Integer status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof FlowEntity) {
            final FlowEntity anotherFlow = (FlowEntity) o;
            return getFlowId().compareTo(anotherFlow.getFlowId()) == 0;
        }
        return false;
    }
}
