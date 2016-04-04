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
 * @version 0.1.0, 03/04/2016
 * @since 0.1.0
 */
public class CheckinEntity extends RealmObject {
    public static final String FIELD_CHECKIN_ID = "checkinId";
    public static final String FIELD_FLOW_ID = "flowId";
    public static final String FIELD_PENDING_SYNCHRONIZATION = "pendingSynchronization";

    @PrimaryKey
    private Long checkinId;

    private Long flowId;

    private String date;

    private Integer status;

    private ItemEntity item;

    private OrderGlassEntity orderGlass;

    private Boolean pendingSynchronization;

    private String location;

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

    public Boolean isPendingSynchronization() {
        return pendingSynchronization;
    }

    public void setPendingSynchronization(Boolean pendingSynchronization) {
        this.pendingSynchronization = pendingSynchronization;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
