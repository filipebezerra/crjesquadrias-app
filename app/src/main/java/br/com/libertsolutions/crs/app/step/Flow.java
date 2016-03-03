package br.com.libertsolutions.crs.app.step;

import android.support.annotation.IntRange;
import com.google.gson.annotations.SerializedName;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 03/03/2016
 * @since 0.1.0
 */
public class Flow {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_STARTED = 1;
    public static final int STATUS_FINISHED = 2;

    @SerializedName("Etapa")
    WorkStep mStep;

    @SerializedName("status")
    Integer mStatus;

    public WorkStep getStep() {
        return mStep;
    }

    public Flow setStep(WorkStep step) {
        mStep = step;
        return this;
    }

    public Integer getStatus() {
        return mStatus;
    }

    public Flow setStatus(
            @IntRange(from = STATUS_PENDING, to = STATUS_FINISHED) Integer status) {
        mStatus = status;
        return this;
    }
}
