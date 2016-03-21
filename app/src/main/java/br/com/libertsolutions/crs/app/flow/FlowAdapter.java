package br.com.libertsolutions.crs.app.flow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.work.WorkEntity;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.List;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 18/03/2016
 * @since 0.1.0
 */
public class FlowAdapter extends RecyclerView.Adapter<FlowAdapter.ViewHolder> {
    @NonNull private final List<Flow> mFlows;
    @NonNull private Context mContext;

    public FlowAdapter(@NonNull Context context, @NonNull List<Flow> flowList) {
        mContext = context;
        mFlows = flowList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_work_step, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Flow flow = mFlows.get(position);
        final WorkStep workStep = flow.getStep();
        holder.stepName.setText(workStep.getName());

        switch (flow.getStatus()) {
            case FlowEntity.STATUS_PENDING:
                holder.stepStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.statusPending));
                break;

            case FlowEntity.STATUS_STARTED:
                holder.stepStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.statusStarted));
                break;

            case FlowEntity.STATUS_FINISHED:
                holder.stepStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.statusFinished));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mFlows.size();
    }

    public Flow getItem(int position) {
        if (position < 0 || position >= mFlows.size()) {
            return null;
        }

        return mFlows.get(position);
    }

    public int getRunningFlowsCount() {
        int count = 0;
        for (Flow flow : mFlows) {
            if (flow.getStatus() == WorkEntity.STATUS_STARTED) {
                count++;
            }
        }
        return count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.stepStatus) View stepStatus;
        @Bind(R.id.stepName) TextView stepName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}