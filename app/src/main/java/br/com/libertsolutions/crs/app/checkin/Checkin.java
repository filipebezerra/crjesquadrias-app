package br.com.libertsolutions.crs.app.checkin;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 03/03/2016
 * @since 0.1.0
 */
public class Checkin implements Comparable<Checkin> {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_FINISHED = 2;

    @SerializedName("idCheckim")
    Long mCheckinId;

    @SerializedName("data")
    String mDate;

    @SerializedName("status")
    Integer mStatus;

    @SerializedName("Itens")
    Item mItem;

    @SerializedName("Vidros")
    OrderGlass mOrderGlass;

    public Checkin() {
    }

    public Checkin(Checkin checkin) {
        this.mCheckinId = checkin.getCheckinId();
        this.mStatus = checkin.getStatus();
        this.mItem = checkin.getItem();
        this.mOrderGlass = checkin.getOrderGlass();
    }

    public Long getCheckinId() {
        return mCheckinId;
    }

    public Checkin setCheckinId(Long checkinId) {
        mCheckinId = checkinId;
        return this;
    }

    public String getDate() {
        return mDate;
    }

    public Checkin setDate(String date) {
        mDate = date;
        return this;
    }

    public Integer getStatus() {
        return mStatus;
    }

    public Checkin setStatus(
            @IntRange(from = STATUS_PENDING, to = STATUS_FINISHED) Integer status) {
        mStatus = status;
        return this;
    }

    public Item getItem() {
        return mItem;
    }

    public Checkin setItem(Item item) {
        mItem = item;
        return this;
    }

    public OrderGlass getOrderGlass() {
        return mOrderGlass;
    }

    public Checkin setOrderGlass(OrderGlass orderGlass) {
        mOrderGlass = orderGlass;
        return this;
    }

    @Override
    public int compareTo(@NonNull Checkin another) {
        Product product;
        Product anotherProduct;

        if (getOrderGlass() != null && another.getOrderGlass() != null) {
            product = getOrderGlass().getProduct();
            anotherProduct = another.getOrderGlass().getProduct();

            return product.getDescription().compareTo(anotherProduct.getDescription());
        } else {
            product = getItem().getProduct();
            anotherProduct = another.getItem().getProduct();

            int typeComparison = product.getType().compareTo(anotherProduct.getType());

            if (typeComparison == 0) {
                return product.getDescription().compareTo(anotherProduct.getDescription());
            } else {
                return typeComparison;
            }
        }
    }
}
