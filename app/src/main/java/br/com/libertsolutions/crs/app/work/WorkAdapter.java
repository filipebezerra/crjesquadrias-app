package br.com.libertsolutions.crs.app.work;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.com.libertsolutions.crs.app.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Project adapter.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 22/01/2016
 * @since 0.1.0
 */
public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.ViewHolder> {
    @NonNull private List<Work> mProjects;

    private static DateFormat sDateInstance = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);

    public WorkAdapter() {
        mProjects = Works.getDataSet();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_work, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Work project = mProjects.get(position);

        holder.projectId.setText(project.getWorkId());
        holder.customerName.setText(project.getCustomerName());

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(project.getDeliveryDate());

        holder.deliveryOrStartDate.setText(sDateInstance.format(calendar.getTime()));
    }

    @Override
    public int getItemCount() {
        return mProjects.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.workId) TextView projectId;
        @Bind(R.id.customerName) TextView customerName;
        @Bind(R.id.deliveryDate) TextView deliveryOrStartDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
