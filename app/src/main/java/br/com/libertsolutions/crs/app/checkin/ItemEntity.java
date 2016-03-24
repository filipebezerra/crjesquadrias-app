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
public class ItemEntity extends RealmObject {
    @PrimaryKey
    private Long itemId;

    private Integer quantity;

    private Float width;

    private Float height;

    private Float weight;

    private String treatment;

    private ProductEntity product;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof ItemEntity) {
            final ItemEntity anotherProduct = (ItemEntity) o;
            return getItemId().compareTo(anotherProduct.getItemId()) == 0;
        }
        return false;
    }
}
