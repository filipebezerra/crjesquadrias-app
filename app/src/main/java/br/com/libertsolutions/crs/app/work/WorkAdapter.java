package br.com.libertsolutions.crs.app.work;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
 * @version 0.1.0, 27/02/2016
 * @since 0.1.0
 */
public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.ViewHolder> {
    @NonNull private List<Work> mWorks;
    @NonNull private Context mContext;

    private static DateFormat sDateInstance = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);

    public WorkAdapter(@NonNull Context context) {
        mContext = context;
        mWorks = Works.getDataSet();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_work, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Work work = mWorks.get(position);

        holder.projectId.setText(work.getWorkId());
        holder.customerName.setText(work.getCustomerName());

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(work.getDeliveryDate());

        holder.deliveryOrStartDate.setText(sDateInstance.format(calendar.getTime()));

        switch (work.getStatus()) {
            case Work.STATUS_PENDING:
                holder.workStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.status_pending));
                break;

            case Work.STATUS_STARTED:
                holder.workStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.status_started));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mWorks.size();
    }

    public int getWorksInRunning() {
        int count = 0;
        for (Work work : mWorks) {
            if (work.getStatus() == Work.STATUS_STARTED) {
                count++;
            }
        }
        return count;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.workStatus) View workStatus;
        @Bind(R.id.workId) TextView projectId;
        @Bind(R.id.customerName) TextView customerName;
        @Bind(R.id.deliveryDate) TextView deliveryOrStartDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
