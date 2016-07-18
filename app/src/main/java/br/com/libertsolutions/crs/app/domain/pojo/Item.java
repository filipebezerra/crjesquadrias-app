package br.com.libertsolutions.crs.app.domain.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class Item {
    @SerializedName("idItem")
    private final long itemId;

    @SerializedName("quantidade")
    private final Integer quantity;

    @SerializedName("largura")
    private final float width;

    @SerializedName("altura")
    private final float height;

    @SerializedName("peso")
    private final float weight;

    @SerializedName("tratamento")
    private final String treatment;

    @SerializedName("Produto")
    private final Product product;

    public Item(long itemId, Integer quantity, float width, float height, float weight,
            String treatment, Product product) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.width = width;
        this.height = height;
        this.weight = weight;
        this.treatment = treatment;
        this.product = product;
    }

    public long getItemId() {
        return itemId;
    }

    public Integer getQuantity() {
        return quantity;
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

    public String getTreatment() {
        return treatment;
    }

    public Product getProduct() {
        return product;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Item) {
            final Item anotherProduct = (Item) o;
            return getItemId() == anotherProduct.getItemId();
        }
        return false;
    }
}
