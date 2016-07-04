package br.com.libertsolutions.crs.app.checkinscreen;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.ToggleButton;
import br.com.libertsolutions.crs.app.R;
import br.com.libertsolutions.crs.app.checkin.Checkin;
import br.com.libertsolutions.crs.app.checkin.CheckinComparator;
import br.com.libertsolutions.crs.app.checkin.Product;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Filipe Bezerra
 * @version 0.2.0
 * @since 0.1.0
 */
public class CheckinAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    private static final int VIEW_TYPE_ITEM = 0;

    private static final int VIEW_TYPE_ORDER_GLASS = 1;

    @NonNull private List<Checkin> mCheckins;

    @NonNull private Context mContext;

    @Nullable private CheckinCallback mCheckinCallback;

    private List<Checkin> mOriginalCheckins;

    private CheckinFilter mCheckinFilter;

    public CheckinAdapter(@NonNull Context context, @NonNull List<Checkin> checkins) {
        mContext = context;
        mCheckins = checkins;
        Collections.sort(mCheckins, new CheckinComparator());
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

            ((ViewHolderItem) holder).productType.setText(product.getType());
            ((ViewHolderItem) holder).itemLocation.setText(checkin.getLocation());
            ((ViewHolderItem) holder).productLine.setText(product.getLine());
            ((ViewHolderItem) holder).itemProcessing.setText(product.getTreatment());

        } else {
            width = checkin.getOrderGlass().getWidth();
            height = checkin.getOrderGlass().getHeight();
        }

        ((BaseViewHolder) holder).productMeasures.setText(
                String.format("%sx%s", NUMBER_FORMAT.format(width), NUMBER_FORMAT.format(height)));
        ((BaseViewHolder) holder).itemDone.setChecked(
                checkin.getStatus() == Checkin.STATUS_FINISHED);

        switch (checkin.getStatus()) {
            case Checkin.STATUS_PENDING:
                ((BaseViewHolder) holder).checkinStatus
                        .setBackgroundResource(R.drawable.circle_status_pending);
                break;

            case Checkin.STATUS_FINISHED:
                ((BaseViewHolder) holder).checkinStatus
                        .setBackgroundResource(R.drawable.circle_status_finished);
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
        List<Checkin> checkinsUpdated = new ArrayList<>();
        for (Checkin checkin : mCheckins) {
            if (checkin.getStatus() != Checkin.STATUS_FINISHED) {
                checkin.setStatus(Checkin.STATUS_FINISHED);
                checkinsUpdated.add(checkin);
            }
        }

        if (!checkinsUpdated.isEmpty()) {
            if (mCheckinCallback != null) {
                mCheckinCallback.onCheckinsAllDone(checkinsUpdated);
            }

            notifyDataSetChanged();
        } else {
            if (mCheckinCallback != null) {
                mCheckinCallback.onCheckinsAlreadyDone();
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

    public void updateCheckin(Checkin checkinDone) {
        final int index = mCheckins.indexOf(checkinDone);

        if (index != -1) {
            mCheckins.get(index).setDate(checkinDone.getDate());
        }
    }

    public void updateCheckin(List<Checkin> checkinsDone) {
        for (Checkin checkinDone : checkinsDone) {
            updateCheckin(checkinDone);
        }
    }

    @Override
    public Filter getFilter() {
        if (mCheckinFilter == null) {
            mCheckinFilter = new CheckinFilter();
        }
        return mCheckinFilter;
    }

    class CheckinFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (mOriginalCheckins == null) {
                mOriginalCheckins = new ArrayList<>(mCheckins);
            }

            if (TextUtils.isEmpty(constraint)) {
                results.count = mOriginalCheckins.size();
                results.values = mOriginalCheckins;
            } else {
                final String filterText = constraint.toString().trim().toLowerCase();
                final List<Checkin> newList = new ArrayList<>();

                for (Checkin checkin : mOriginalCheckins) {
                    if (!TextUtils.isEmpty(checkin.getLocation())) {
                        if (checkin.getLocation().toLowerCase().contains(filterText)) {
                            newList.add(checkin);
                            continue;
                        }
                    }

                    int width, height;
                    if (checkin.getOrderGlass() != null) {
                        width = (int) checkin.getOrderGlass().getWidth();
                        height = (int) checkin.getOrderGlass().getHeight();
                    } else {
                        width = (int) checkin.getItem().getWidth();
                        height = (int) checkin.getItem().getHeight();
                    }

                    final String measure = width + "x" + height;
                    if (String.valueOf(width).startsWith(filterText)
                            || String.valueOf(height).startsWith(filterText)
                            || measure.startsWith(filterText)) {
                        newList.add(checkin);
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
            mCheckins = (List<Checkin>) results.values;
            notifyDataSetChanged();
        }
    }

    class ViewHolderItem extends BaseViewHolder {
        @BindView(R.id.product_type) TextView productType;
        @BindView(R.id.checkin_location) TextView itemLocation;
        @BindView(R.id.product_line) TextView productLine;
        @BindView(R.id.product_processing) TextView itemProcessing;

        public ViewHolderItem(View itemView) {
            super(itemView);
        }
    }

    class ViewHolderOrderGlass extends BaseViewHolder {
        public ViewHolderOrderGlass(View itemView) {
            super(itemView);
        }
    }

    abstract class BaseViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.checkin_status) View checkinStatus;
        @BindView(R.id.product_measures) TextView productMeasures;
        @BindView(R.id.checkin_toggle_action) ToggleButton itemDone;

        public BaseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.checkin_toggle_action)
        public void onClickItemDone() {
            final Checkin checkin = mCheckins.get(getLayoutPosition());

            if (checkin != null) {
                if (itemDone.isChecked()) {
                    checkin.setStatus(Checkin.STATUS_FINISHED);

                    if (mCheckinCallback != null) {
                        mCheckinCallback.onCheckinDone(checkin);
                    }

                    notifyItemChanged(getLayoutPosition());
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

        void onCheckinDone(Checkin checkin);

        void onCheckinsAllDone(List<Checkin> checkinsUpdated);

        void onCheckinsAlreadyDone();

        void onStatusCannotChange();
    }
}
