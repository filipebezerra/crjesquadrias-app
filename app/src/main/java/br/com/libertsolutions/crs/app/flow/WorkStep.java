package br.com.libertsolutions.crs.app.flow;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

/**
 * @author Filipe Bezerra
 * @version 0.2.0
 * @since 0.1.0
 */
public class WorkStep implements Comparable<WorkStep>, Parcelable {
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

    protected WorkStep(Parcel source) {
        workStepId = source.readLong();
        order = source.readInt();
        name = source.readString();
        type = source.readInt();
        goForward = source.readInt();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(workStepId);
        out.writeInt(order);
        out.writeString(name);
        out.writeInt(type);
        out.writeInt(goForward);
    }

    public static final Creator<WorkStep> CREATOR = new Creator<WorkStep>() {
        public WorkStep createFromParcel(Parcel source) {
            return new WorkStep(source);
        }

        public WorkStep[] newArray(int size) {
            return new WorkStep[size];
        }
    };
}