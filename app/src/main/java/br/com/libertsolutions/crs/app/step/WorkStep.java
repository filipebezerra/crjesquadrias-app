package br.com.libertsolutions.crs.app.step;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
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
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_STARTED = 1;
    public static final int STATUS_FINISHED = 2;

    long mWorkStepId;

    int mOrder;

    String mName;

    int mType;

    int mGoForward;

    int mStatus;

    public long getWorkStepId() {
        return mWorkStepId;
    }

    public WorkStep setWorkStepId(long workStepId) {
        mWorkStepId = workStepId;
        return this;
    }

    public int getOrder() {
        return mOrder;
    }

    public WorkStep setOrder(int order) {
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

    public int getType() {
        return mType;
    }

    public WorkStep setType(int type) {
        mType = type;
        return this;
    }

    public int getGoForward() {
        return mGoForward;
    }

    public WorkStep setGoForward(int goForward) {
        mGoForward = goForward;
        return this;
    }

    public int getStatus() {
        return mStatus;
    }

    public WorkStep setStatus(int status) {
        mStatus = status;
        return this;
    }

    @Override
    public int compareTo(@NonNull  WorkStep another) {
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