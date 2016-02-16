package br.com.libertsolutions.crs.app.checkin;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 13/02/2016
 * @since 0.1.0
 */
public class Checkin {
    long mCheckinId;

    int mStatus;

    Item mItem;

    OrderGlass mOrderGlass;

    public long getCheckinId() {
        return mCheckinId;
    }

    public Checkin setCheckinId(long checkinId) {
        mCheckinId = checkinId;
        return this;
    }

    public int getStatus() {
        return mStatus;
    }

    public Checkin setStatus(int status) {
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
}
