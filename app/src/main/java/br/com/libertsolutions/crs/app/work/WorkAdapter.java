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
import br.com.libertsolutions.crs.app.date.DateUtil;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.List;

/**
 * Project adapter.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 03/03/2016
 * @since 0.1.0
 */
public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.ViewHolder> {
    @NonNull private List<Work> mWorks;
    @NonNull private Context mContext;

    public WorkAdapter(@NonNull Context context, List<Work> workList) {
        mContext = context;
        mWorks = workList;
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

        holder.workCode.setText(
                String.format("%s/%s", work.getCode(), work.getJob()));
        holder.customerName.setText(work.getCustomer().getNome());

        final CharSequence dateString = DateUtil
                .formatAsRelativeDateFromNow(work.getDate());
        if (dateString != null) {
            holder.workDate.setText(dateString);
        } else {
            holder.workDate.setText(work.getDate());
        }

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

    public int getRunningWorksCount() {
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
        @Bind(R.id.workCode) TextView workCode;
        @Bind(R.id.customerName) TextView customerName;
        @Bind(R.id.workDate) TextView workDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
