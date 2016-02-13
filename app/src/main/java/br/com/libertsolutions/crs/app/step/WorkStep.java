package br.com.libertsolutions.crs.app.step;

import android.support.annotation.NonNull;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 10/02/2016
 * @since 0.1.0
 */
public class WorkStep implements Comparable<WorkStep> {
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
}