package br.com.libertsolutions.crs.app.domain.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class Checkin {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_FINISHED = 2;

    @SerializedName("idCheckim")
    private final long checkinId;

    @SerializedName("id_fluxo")
    private final long flowId;

    @SerializedName("data")
    private String date;

    @SerializedName("status")
    private int status;

    @SerializedName("Itens")
    private final Item item;

    @SerializedName("Vidros")
    private final OrderGlass orderGlass;

    @SerializedName("localizacao")
    private final String location;

    public Checkin(long checkinId, long flowId, String date, int status, Item item,
            OrderGlass orderGlass, String location) {
        this.checkinId = checkinId;
        this.flowId = flowId;
        this.date = date;
        this.status = status;
        this.item = item;
        this.orderGlass = orderGlass;
        this.location = location;
    }

    public long getCheckinId() {
        return checkinId;
    }

    public long getFlowId() {
        return flowId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public Item getItem() {
        return item;
    }

    public OrderGlass getOrderGlass() {
        return orderGlass;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Checkin) {
            final Checkin anotherCheckin = (Checkin) o;
            return getCheckinId() == anotherCheckin.getCheckinId();
        }
        return false;
    }
}
