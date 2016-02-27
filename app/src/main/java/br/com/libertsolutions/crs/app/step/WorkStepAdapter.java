package br.com.libertsolutions.crs.app.step;

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
public class WorkStepAdapter extends RecyclerView.Adapter<WorkStepAdapter.ViewHolder> {
    @NonNull  private final List<WorkStep> mWorkSteps;

    public WorkStepAdapter() {
        mWorkSteps = WorkSteps.getDataSet();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_work_step, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final WorkStep item = mWorkSteps.get(position);
        holder.stageName.setText(item.getName());

        if (item.getGoForward() == 0 && position != 0) {
            holder.stageName.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.blackTranslucent));
        }
    }

    @Override
    public int getItemCount() {
        return mWorkSteps.size();
    }

    public WorkStep getItem(int position) {
        if (position < 0 || position >= mWorkSteps.size()) {
            return null;
        }

        return mWorkSteps.get(position);
    }

    public int getWorkStepsInRunning() {
        int count = 0;
        for (WorkStep workStep : mWorkSteps) {
            if (workStep.getStatus() == Work.STATUS_STARTED) {
                count++;
            }
        }
        return count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.stage_name) TextView stageName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
