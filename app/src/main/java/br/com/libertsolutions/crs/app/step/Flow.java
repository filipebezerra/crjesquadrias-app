package br.com.libertsolutions.crs.app.step;

import com.google.gson.annotations.SerializedName;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 20/03/2016
 * @since 0.1.0
 */
public class Flow {
    @SerializedName("Etapa")
    private final WorkStep step;

    @SerializedName("status")
    private final int status;

    public Flow(WorkStep step, int status) {
        this.step = step;
        this.status = status;
    }

    public WorkStep getStep() {
        return step;
    }

    public int getStatus() {
        return status;
    }
}
