package br.com.libertsolutions.crs.app.flow;

import com.google.gson.annotations.SerializedName;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 21/03/2016
 * @since 0.1.0
 */
public class Flow {
    @SerializedName("Etapa")
    private final WorkStep step;

    @SerializedName("id_fluxo")
    private final long flowId;

    @SerializedName("status")
    private final int status;

    public Flow(WorkStep step, long flowId, int status) {
        this.step = step;
        this.flowId = flowId;
        this.status = status;
    }

    public WorkStep getStep() {
        return step;
    }

    public long getFlowId() {
        return flowId;
    }

    public int getStatus() {
        return status;
    }
}
