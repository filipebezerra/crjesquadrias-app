package br.com.libertsolutions.crs.app.work;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.utils.date.DateUtil;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Work adapter.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 05/06/2016
 * @since 0.1.0
 */
public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.ViewHolder>
    implements Filterable {

    @NonNull private List<Work> mWorks;

    @NonNull private Context mContext;

    @NonNull private final List<Work> mOriginalWorks;

    public WorkAdapter(@NonNull Context context, @NonNull List<Work> workList) {
        mContext = context;
        mWorks = workList;
        mOriginalWorks = Collections.unmodifiableList(mWorks);
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
        holder.customerName.setText(work.getClient().getName());

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
                        ContextCompat.getColor(mContext, R.color.statusPending));
                break;

            case Work.STATUS_STARTED:
                holder.workStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.statusStarted));
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

    public Work getItem(int position) {
        if (position < 0 || position >= mWorks.size()) {
            return null;
        }

        return mWorks.get(position);
    }

    @Override
    public Filter getFilter() {
        return new WorkFilter();
    }

    public void swapData(List<Work> workList) {
        if (workList != null) {
            mWorks.clear();
            mWorks.addAll(workList);
            notifyDataSetChanged();
        }
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

    class WorkFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (TextUtils.isEmpty(constraint)) {
                results.count = mOriginalWorks.size();
                results.values = mOriginalWorks;
            } else {
                final String filterText = constraint.toString().trim().toLowerCase();
                final List<Work> newList = new ArrayList<>();

                for (Work work : mOriginalWorks) {
                    if (work.getJob().toLowerCase().contains(filterText) ||
                            work.getCode().toLowerCase().contains(filterText) ||
                            work.getClient().getName().toLowerCase().contains(filterText)) {
                        newList.add(work);
                    }
                }

                results.count = newList.size();
                results.values = newList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mWorks = (List<Work>) results.values;
            notifyDataSetChanged();
        }
    }
}
