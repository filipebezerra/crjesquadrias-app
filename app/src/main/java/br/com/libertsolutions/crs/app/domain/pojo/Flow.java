package br.com.libertsolutions.crs.app.domain.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class Flow implements Parcelable {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_STARTED = 1;
    public static final int STATUS_FINISHED = 2;

    @SerializedName("Etapa")
    private final WorkStep step;

    @SerializedName("id_fluxo")
    private final long flowId;

    @SerializedName("id_obra")
    private final long workId;

    @SerializedName("status")
    private final int status;

    public Flow(WorkStep step, long flowId, long workId, int status) {
        this.step = step;
        this.flowId = flowId;
        this.workId = workId;
        this.status = status;
    }

    protected Flow(Parcel source) {
        step = source.readParcelable(getClass().getClassLoader());
        flowId = source.readLong();
        workId = source.readLong();
        status = source.readInt();
    }

    public WorkStep getStep() {
        return step;
    }

    public long getFlowId() {
        return flowId;
    }

    public long getWorkId() {
        return workId;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(step, 0);
        out.writeLong(flowId);
        out.writeLong(workId);
        out.writeInt(status);
    }

    public static final Creator<Flow> CREATOR = new Creator<Flow>() {
        public Flow createFromParcel(Parcel source) {
            return new Flow(source);
        }

        public Flow[] newArray(int size) {
            return new Flow[size];
        }
    };
}
