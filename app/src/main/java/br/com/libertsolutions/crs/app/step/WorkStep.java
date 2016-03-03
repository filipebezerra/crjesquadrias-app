package br.com.libertsolutions.crs.app.step;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 27/02/2016
 * @since 0.1.0
 */
@ParcelablePlease
public class WorkStep implements Comparable<WorkStep>, Parcelable {
    public static final int TYPE_PRODUCT = 0;
    public static final int TYPE_ORDER_GLASS = 1;

    @SerializedName("idEtapa")
    Long mWorkStepId;

    @SerializedName("ordem")
    Integer mOrder;

    @SerializedName("nome")
    String mName;

    @SerializedName("tipo")
    Integer mType;

    @SerializedName("avanca")
    Integer mGoForward;

    public Long getWorkStepId() {
        return mWorkStepId;
    }

    public WorkStep setWorkStepId(Long workStepId) {
        mWorkStepId = workStepId;
        return this;
    }

    public Integer getOrder() {
        return mOrder;
    }

    public WorkStep setOrder(Integer order) {
        mOrder = order;
        return this;
    }

    public String getName() {
        return mName;
    }

    public WorkStep setName(String name) {
        mName = name;
        return this;
    }

    public Integer getType() {
        return mType;
    }

    public WorkStep setType(
            @IntRange(from = TYPE_PRODUCT, to = TYPE_ORDER_GLASS) Integer type) {
        mType = type;
        return this;
    }

    public Integer getGoForward() {
        return mGoForward;
    }

    public WorkStep setGoForward(@IntRange(from = 0, to = 1) Integer goForward) {
        mGoForward = goForward;
        return this;
    }

    @Override
    public int compareTo(@NonNull WorkStep another) {
        if (getOrder().equals(another.getOrder())) {
            return 0;
        }

        return getOrder() < another.getOrder() ? -1 : 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        WorkStepParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<WorkStep> CREATOR = new Creator<WorkStep>() {
        public WorkStep createFromParcel(Parcel source) {
            WorkStep target = new WorkStep();
            WorkStepParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public WorkStep[] newArray(int size) {
            return new WorkStep[size];
        }
    };
}