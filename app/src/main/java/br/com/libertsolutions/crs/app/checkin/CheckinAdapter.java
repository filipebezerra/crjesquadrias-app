package br.com.libertsolutions.crs.app.checkin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import br.com.libertsolutions.crs.app.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 16/02/2016
 * @since 0.1.0
 */
public class CheckinAdapter extends RecyclerView.Adapter<CheckinAdapter.ViewHolder> {
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    @NonNull private List<Checkin> mOriginalCheckins;
    @NonNull private List<Checkin> mCheckins;
    @NonNull private Context mContext;

    public CheckinAdapter(@NonNull List<Checkin> checkins, @NonNull Context context) {
        mOriginalCheckins = checkins;
        mContext = context;

        mCheckins = new ArrayList<>();
        for(Checkin checkin : mOriginalCheckins) {
            if (checkin.getItem() != null) {
                mCheckins.add(checkin);
            } else {
                for (int i = 0; i < checkin.getOrderGlass().getQuantity(); i++) {
                    mCheckins.add(new Checkin(checkin));
                }
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_checkin, parent, false);
        return new CheckinAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Checkin checkin = mCheckins.get(position);

        Product product;
        float width, height;
        if (checkin.getItem() != null) {
            product = checkin.getItem().getProduct();
            width = checkin.getItem().getWidth();
            height = checkin.getItem().getHeight();
        } else {
            product = checkin.getOrderGlass().getProduct();
            width = checkin.getOrderGlass().getWidth();
            height = checkin.getOrderGlass().getHeight();
        }

        holder.productType.setText(product.getType());
        holder.productWidth.setText(NUMBER_FORMAT.format(width));
        holder.productHeight.setText(NUMBER_FORMAT.format(height));
        holder.itemLocation.setText("-");
        holder.productLine.setText("-");
        holder.itemTreatment.setText(product.getTreatment());
        holder.itemDone.setChecked(checkin.getStatus() == Checkin.STATUS_FINISHED);
        holder.itemDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CheckBox checkBox = (CheckBox) v;
                final Checkin checkin = mCheckins.get(holder.getAdapterPosition());
                checkin.setStatus(checkBox.isChecked() ?
                        Checkin.STATUS_FINISHED : Checkin.STATUS_PENDING);
                notifyItemChanged(holder.getAdapterPosition());
            }
        });

        switch (checkin.getStatus()) {
            case Checkin.STATUS_PENDING:
                holder.checkinStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.status_finished));

                break;

            case Checkin.STATUS_FINISHED:
                holder.checkinStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.status_pending));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mCheckins.size();
    }

    public void checkAllDone() {
        boolean updated = false;
        for (Checkin checkin : mCheckins) {
            if (checkin.getStatus() != Checkin.STATUS_FINISHED) {
                checkin.setStatus(Checkin.STATUS_FINISHED);
                updated = true;
            }
        }

        if (updated) {
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.checkinStatus) View checkinStatus;
        @Bind(R.id.productType) TextView productType;
        @Bind(R.id.productWidth) TextView productWidth;
        @Bind(R.id.productHeight) TextView productHeight;
        @Bind(R.id.itemLocation) TextView itemLocation;
        @Bind(R.id.productLine) TextView productLine;
        @Bind(R.id.itemTreatment) TextView itemTreatment;
        @Bind(R.id.itemDone) CheckBox itemDone;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
