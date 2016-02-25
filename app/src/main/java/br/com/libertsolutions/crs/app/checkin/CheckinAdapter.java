package br.com.libertsolutions.crs.app.checkin;

import android.support.annotation.NonNull;
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

    @NonNull List<Checkin> mOriginalCheckins;
    @NonNull List<Checkin> mCheckins;

    public CheckinAdapter(@NonNull List<Checkin> checkins) {
        mOriginalCheckins = checkins;

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
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkin, parent, false);
        return new CheckinAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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
