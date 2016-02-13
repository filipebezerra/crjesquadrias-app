package br.com.libertsolutions.crs.app.step;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 10/02/2016
 * @since 0.1.0
 */
@ParcelablePlease
public class WorkStep implements Comparable<WorkStep>,Parcelable {
    long mWorkStepId;

    int mOrder;

    String mName;

    int mKind;

    int mGoForward;

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

    public int getKind() {
        return mKind;
    }

    public WorkStep setKind(int kind) {
        mKind = kind;
        return this;
    }

    public int getGoForward() {
        return mGoForward;
    }

    public WorkStep setGoForward(int goForward) {
        mGoForward = goForward;
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