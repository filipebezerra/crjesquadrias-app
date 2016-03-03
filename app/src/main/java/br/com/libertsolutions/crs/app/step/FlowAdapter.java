package br.com.libertsolutions.crs.app.step;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.work.Work;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.List;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 27/02/2016
 * @since #
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
            case Flow.STATUS_PENDING:
                holder.stepStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.status_pending));
                break;

            case Flow.STATUS_STARTED:
                holder.stepStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.status_started));
                break;

            case Flow.STATUS_FINISHED:
                holder.stepStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.status_finished));
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
            if (flow.getStatus() == Work.STATUS_STARTED) {
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
