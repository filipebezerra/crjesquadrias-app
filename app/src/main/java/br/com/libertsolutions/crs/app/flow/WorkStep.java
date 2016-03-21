package br.com.libertsolutions.crs.app.flow;

import android.support.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 20/03/2016
 * @since 0.1.0
 */
public class WorkStep implements Comparable<WorkStep> {
    @SerializedName("idEtapa")
    private final long workStepId;

    @SerializedName("ordem")
    private final int order;

    @SerializedName("nome")
    private final String name;

    @SerializedName("tipo")
    private final int type;

    @SerializedName("avanca")
    private final int goForward;

    public WorkStep(long id, int order, String name, int type, int goForward) {
        this.workStepId = id;
        this.order = order;
        this.name = name;
        this.type = type;
        this.goForward = goForward;
    }

    public long getWorkStepId() {
        return workStepId;
    }

    public int getOrder() {
        return order;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public int getGoForward() {
        return goForward;
    }

    @Override
    public int compareTo(@NonNull WorkStep another) {
        if (getOrder() == another.getOrder()) {
            return 0;
        }

        return getOrder() < another.getOrder() ? -1 : 1;
    }
}