package br.com.libertsolutions.crs.app.checkin;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 23/03/2016
 * @since 0.1.0
 */
public class OrderGlassEntity extends RealmObject {
    @PrimaryKey
    private Long orderGlassId;

    private Integer quantity;

    private String number;

    private String color;

    private Float width;

    private Float height;

    private Float weight;

    private ProductEntity product;

    public Long getOrderGlassId() {
        return orderGlassId;
    }

    public void setOrderGlassId(Long orderGlassId) {
        this.orderGlassId = orderGlassId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof OrderGlassEntity) {
            final OrderGlassEntity anotherOrderGlass = (OrderGlassEntity) o;
            return getOrderGlassId().compareTo(anotherOrderGlass.getOrderGlassId()) == 0;
        }
        return false;
    }
}
