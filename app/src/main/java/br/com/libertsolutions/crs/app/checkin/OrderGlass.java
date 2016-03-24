package br.com.libertsolutions.crs.app.checkin;

import com.google.gson.annotations.SerializedName;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 23/03/2016
 * @since 0.1.0
 */
public class OrderGlass {
    @SerializedName("idPedVidro")
    private final long orderGlassId;

    @SerializedName("quantidade")
    private final int quantity;

    @SerializedName("numero")
    private final String number;

    @SerializedName("cor")
    private final String color;

    @SerializedName("largura")
    private final float width;

    @SerializedName("altura")
    private final float height;

    @SerializedName("peso")
    private final float weight;

    @SerializedName("Produto")
    private final Product product;

    public OrderGlass(long orderGlassId, int quantity, String number, String color, float width,
            float height, float weight, Product product) {
        this.orderGlassId = orderGlassId;
        this.quantity = quantity;
        this.number = number;
        this.color = color;
        this.width = width;
        this.height = height;
        this.weight = weight;
        this.product = product;
    }

    public long getOrderGlassId() {
        return orderGlassId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getNumber() {
        return number;
    }

    public String getColor() {
        return color;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getWeight() {
        return weight;
    }

    public Product getProduct() {
        return product;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof OrderGlass) {
            final OrderGlass anotherOrderGlass = (OrderGlass) o;
            return getOrderGlassId() == anotherOrderGlass.getOrderGlassId();
        }
        return false;
    }
}
