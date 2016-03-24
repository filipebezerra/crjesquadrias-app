package br.com.libertsolutions.crs.app.checkin;

import android.support.annotation.IntRange;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import static br.com.libertsolutions.crs.app.checkin.Checkin.STATUS_FINISHED;
import static br.com.libertsolutions.crs.app.checkin.Checkin.STATUS_PENDING;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 23/03/2016
 * @since 0.1.0
 */
public class CheckinEntity extends RealmObject {
    public static final String FIELD_FLOW_ID = "flowId";

    @PrimaryKey
    private Long checkinId;

    private Long flowId;

    private String date;

    private Integer status;

    private ItemEntity item;

    private OrderGlassEntity orderGlass;

    public Long getCheckinId() {
        return checkinId;
    }

    public void setCheckinId(Long checkinId) {
        this.checkinId = checkinId;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(@IntRange(from = STATUS_PENDING, to = STATUS_FINISHED) Integer status) {
        this.status = status;
    }

    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    public OrderGlassEntity getOrderGlass() {
        return orderGlass;
    }

    public void setOrderGlass(OrderGlassEntity orderGlass) {
        this.orderGlass = orderGlass;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof CheckinEntity) {
            final CheckinEntity anotherCheckin = (CheckinEntity) o;
            return getCheckinId().compareTo(anotherCheckin.getCheckinId()) == 0;
        }
        return false;
    }
}
