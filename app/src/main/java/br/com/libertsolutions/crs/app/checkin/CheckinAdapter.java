package br.com.libertsolutions.crs.app.checkin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import br.com.libertsolutions.crs.app.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.text.NumberFormat;
import java.util.List;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 07/03/2016
 * @since 0.1.0
 */
public class CheckinAdapter extends RecyclerView.Adapter<CheckinAdapter.ViewHolder> {
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    @NonNull private List<Checkin> mCheckins;
    @NonNull private Context mContext;
    @Nullable private CheckinCallback mCheckinCallback;

    public CheckinAdapter(@NonNull Context context, @NonNull List<Checkin> checkins) {
        mCheckins = checkins;
        mContext = context;
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
        holder.productMeasures.setText(String.format("%sx%s",
                NUMBER_FORMAT.format(width), NUMBER_FORMAT.format(height)));
        holder.itemLocation.setText("-");
        holder.productLine.setText("-");
        holder.itemTreatment.setText(product.getTreatment());
        holder.itemDone.setChecked(checkin.getStatus() == Checkin.STATUS_FINISHED);
        /*holder.itemDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Checkin checkin = mCheckins.get(holder.getAdapterPosition());

                if (checkin.getStatus() != Checkin.STATUS_FINISHED) {
                    checkin.setStatus(Checkin.STATUS_FINISHED);
                    notifyItemChanged(holder.getAdapterPosition());

                    if (mCheckinCallback != null) {
                        mCheckinCallback.onStatusChanged(checkin);
                    }
                } else {
                    final CheckBox checkBox = (CheckBox) v;
                    checkBox.setChecked(true);
                }
            }
        });*/

        holder.itemDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final Checkin checkin = mCheckins.get(holder.getAdapterPosition());

                if (isChecked) {
                    checkin.setStatus(Checkin.STATUS_FINISHED);
                    notifyItemChanged(holder.getAdapterPosition());

                    if (mCheckinCallback != null) {
                        mCheckinCallback.onStatusChanged(checkin);
                    }
                } else if (checkin.getStatus() == Checkin.STATUS_FINISHED) {
                    buttonView.setChecked(true);
                    
                    if (mCheckinCallback != null) {
                        mCheckinCallback.onStatusCannotChange();
                    }
                }
            }
        });

        switch (checkin.getStatus()) {
            case Checkin.STATUS_PENDING:
                holder.checkinStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.statusPending));
                break;

            case Checkin.STATUS_FINISHED:
                holder.checkinStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.statusFinished));
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

            if (mCheckinCallback != null) {
                mCheckinCallback.onCheckinsAllDone();
            }
        }
    }

    public int getFinishedCheckinsCount() {
        int count = 0;
        for (Checkin checkin : mCheckins) {
            if (checkin.getStatus() == Checkin.STATUS_FINISHED) {
                count++;
            }
        }
        return count;
    }

    public void setCheckinCallback(CheckinCallback checkinCallback) {
        mCheckinCallback = checkinCallback;
    }

    public List<Checkin> getAll() {
        return mCheckins;
    }

    public void updateCheckin(Checkin checkinDone) {
        for (Checkin checkin : mCheckins) {
            if (checkin.getCheckinId().equals(checkinDone.getCheckinId())) {
                checkin.setDate(checkinDone.getDate());
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.checkinStatus) View checkinStatus;
        @Bind(R.id.productType) TextView productType;
        @Bind(R.id.productMeasures) TextView productMeasures;
        @Bind(R.id.itemLocation) TextView itemLocation;
        @Bind(R.id.productLine) TextView productLine;
        @Bind(R.id.itemTreatment) TextView itemTreatment;
        @Bind(R.id.itemDone) CheckBox itemDone;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface CheckinCallback {
        void onStatusChanged(Checkin checkin);
        void onCheckinsAllDone();
        void onStatusCannotChange();
    }
}
