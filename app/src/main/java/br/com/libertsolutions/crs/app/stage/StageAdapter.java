package br.com.libertsolutions.crs.app.stage;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.com.libertsolutions.crs.app.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.List;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 10/02/2016
 * @since #
 */
public class StageAdapter extends RecyclerView.Adapter<StageAdapter.ViewHolder> {
    @NonNull  private final List<Stage> mStages;

    public StageAdapter() {
        mStages = Stages.getDataSet();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stage, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Stage item = mStages.get(position);
        holder.stageName.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return mStages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.stage_name) TextView stageName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
