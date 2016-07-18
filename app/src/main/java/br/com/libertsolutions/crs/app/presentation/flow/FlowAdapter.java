package br.com.libertsolutions.crs.app.presentation.flow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.domain.pojo.Flow;
import br.com.libertsolutions.crs.app.domain.pojo.WorkStep;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.util.List;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class FlowAdapter extends RecyclerView.Adapter<FlowAdapter.ViewHolder> {
    @NonNull private final List<Flow> mFlows;
    @NonNull private Context mContext;
    @NonNull private View.OnClickListener mCardViewListener;

    public FlowAdapter(@NonNull Context context, @NonNull List<Flow> flowList,
            @NonNull View.OnClickListener cardViewListener) {
        mContext = context;
        mFlows = flowList;
        mCardViewListener = cardViewListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_flow, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Flow flow = mFlows.get(position);
        final WorkStep workStep = flow.getStep();
        holder.stepName.setText(workStep.getName());

        switch (flow.getStatus()) {
            case Flow.STATUS_PENDING:
                holder.stepStatus.setBackgroundResource(R.drawable.circle_status_pending);
                break;

            case Flow.STATUS_STARTED:
                holder.stepStatus.setBackgroundResource(R.drawable.circle_status_started);
                break;

            case Flow.STATUS_FINISHED:
                holder.stepStatus.setBackgroundResource(R.drawable.circle_status_finished);
                break;
        }

        holder.flow = flow;
        holder.cardView.setTag(holder);
        holder.cardView.setOnClickListener(mCardViewListener);
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
            if (flow.getStatus() == Flow.STATUS_STARTED) {
                count++;
            }
        }
        return count;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_view) CardView cardView;
        @BindView(R.id.flow_status) View stepStatus;
        @BindView(R.id.flow_name) TextView stepName;
        Flow flow;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
