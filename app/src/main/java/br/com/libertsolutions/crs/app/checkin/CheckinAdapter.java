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
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

import br.com.libertsolutions.crs.app.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 07/03/2016
 * @since 0.1.0
 */
public class CheckinAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_ORDER_GLASS = 1;

    @NonNull private List<Checkin> mCheckins;
    @NonNull private Context mContext;
    @Nullable private CheckinCallback mCheckinCallback;

    public CheckinAdapter(@NonNull Context context, @NonNull List<Checkin> checkins) {
        mContext = context;
        mCheckins = checkins;
        Collections.sort(mCheckins);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            final View itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_checkin, parent, false);
            return new ViewHolderItem(itemView);
        } else {
            final View itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_checkin_order_glass, parent, false);
            return new ViewHolderOrderGlass(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Checkin checkin = mCheckins.get(position);
        float width, height;

        if (holder instanceof ViewHolderItem) {
            final Product product = checkin.getItem().getProduct();
            width = checkin.getItem().getWidth();
            height = checkin.getItem().getHeight();

            ((ViewHolderItem) holder).productType.setText(
                    product.getType());
            ((ViewHolderItem) holder).itemLocation.setText("-");
            ((ViewHolderItem) holder).productLine.setText("-");
            ((ViewHolderItem) holder).itemTreatment.setText(
                    product.getTreatment());

        } else {
            width = checkin.getOrderGlass().getWidth();
            height = checkin.getOrderGlass().getHeight();

            ((ViewHolderOrderGlass) holder).productColor.setText(
                    checkin.getOrderGlass().getColor());
        }

        ((BaseViewHolder) holder).productMeasures.setText(
                String.format("%sx%s", NUMBER_FORMAT.format(width), NUMBER_FORMAT.format(height)));
        ((BaseViewHolder) holder).itemDone.setChecked(
                checkin.getStatus() == Checkin.STATUS_FINISHED);

        switch (checkin.getStatus()) {
            case Checkin.STATUS_PENDING:
                ((BaseViewHolder) holder).checkinStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.statusPending));
                break;

            case Checkin.STATUS_FINISHED:
                ((BaseViewHolder) holder).checkinStatus.setBackgroundColor(
                        ContextCompat.getColor(mContext, R.color.statusFinished));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mCheckins.size();
    }

    @Override
    public int getItemViewType(int position) {
        final Checkin checkin = mCheckins.get(position);

        if (checkin.getOrderGlass() == null) {
            return VIEW_TYPE_ITEM;
        } else {
            return VIEW_TYPE_ORDER_GLASS;
        }
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

    public void setCheckinCallback(@NonNull CheckinCallback checkinCallback) {
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

    class ViewHolderItem extends BaseViewHolder {
        @Bind(R.id.productType) TextView productType;
        @Bind(R.id.itemLocation) TextView itemLocation;
        @Bind(R.id.productLine) TextView productLine;
        @Bind(R.id.itemTreatment) TextView itemTreatment;

        public ViewHolderItem(View itemView) {
            super(itemView);
        }
    }

    class ViewHolderOrderGlass extends BaseViewHolder {
        @Bind(R.id.productColor) TextView productColor;

        public ViewHolderOrderGlass(View itemView) {
            super(itemView);
        }
    }

    abstract class BaseViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.checkinStatus) View checkinStatus;
        @Bind(R.id.productMeasures) TextView productMeasures;
        @Bind(R.id.itemDone) CheckBox itemDone;

        public BaseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.itemDone)
        public void onClickItemDone() {
            final Checkin checkin = mCheckins.get(getLayoutPosition());

            if (checkin != null) {
                if (itemDone.isChecked()) {
                    checkin.setStatus(Checkin.STATUS_FINISHED);
                    notifyItemChanged(getLayoutPosition());

                    if (mCheckinCallback != null) {
                        mCheckinCallback.onStatusChanged(checkin);
                    }
                } else {
                    itemDone.setChecked(true);

                    if (mCheckinCallback != null) {
                        mCheckinCallback.onStatusCannotChange();
                    }
                }
            }
        }
    }

    public interface CheckinCallback {
        void onStatusChanged(Checkin checkin);
        void onCheckinsAllDone();
        void onStatusCannotChange();
    }
}
