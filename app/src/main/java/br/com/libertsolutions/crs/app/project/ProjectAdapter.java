package br.com.libertsolutions.crs.app.project;

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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Project adapter.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 22/01/2016
 * @since 0.1.0
 */
public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {
    @NonNull private List<Project> mProjects;

    private static DateFormat sDateInstance = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);

    public ProjectAdapter() {
        mProjects = Arrays.asList(
                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis())
        );
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Project project = mProjects.get(position);

        holder.projectId.setText(project.getProjectId());
        holder.customerName.setText(project.getCustomerName());

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(project.getDeliveryForecast());

        holder.deliveryOrStartDate.setText(sDateInstance.format(calendar.getTime()));
    }

    @Override
    public int getItemCount() {
        return mProjects.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.project_id) TextView projectId;
        @Bind(R.id.customer_name) TextView customerName;
        @Bind(R.id.delivery_or_start_date) TextView deliveryOrStartDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
