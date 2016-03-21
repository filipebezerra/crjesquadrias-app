package br.com.libertsolutions.crs.app.flow;

import android.support.annotation.IntRange;
import io.realm.RealmObject;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 21/03/2016
 * @since 0.1.0
 */
public class FlowEntity extends RealmObject {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_STARTED = 1;
    public static final int STATUS_FINISHED = 2;

    private WorkStepEntity step;

    private Integer status;

    public WorkStepEntity getStep() {
        return step;
    }

    public void setStep(WorkStepEntity step) {
        this.step = step;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(@IntRange(from = STATUS_PENDING, to = STATUS_FINISHED) Integer status) {
        this.status = status;
    }
}
