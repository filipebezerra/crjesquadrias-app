package br.com.libertsolutions.crs.app.checkin;

import com.google.gson.annotations.SerializedName;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 03/03/2016
 * @since 0.1.0
 */
public class Item {
    @SerializedName("idItem")
    Long mItemId;

    @SerializedName("quantidade")
    Integer mQuantity;

    @SerializedName("largura")
    Float mWidth;

    @SerializedName("altura")
    Float mHeight;

    @SerializedName("peso")
    Float mWeight;

    @SerializedName("tratamento")
    String mTreatment;

    @SerializedName("Produto")
    Product mProduct;

    public Long getItemId() {
        return mItemId;
    }

    public Item setItemId(Long itemId) {
        mItemId = itemId;
        return this;
    }

    public Integer getQuantity() {
        return mQuantity;
    }

    public Item setQuantity(Integer quantity) {
        mQuantity = quantity;
        return this;
    }

    public Float getWidth() {
        return mWidth;
    }

    public Item setWidth(Float width) {
        mWidth = width;
        return this;
    }

    public Float getHeight() {
        return mHeight;
    }

    public Item setHeight(Float height) {
        mHeight = height;
        return this;
    }

    public Float getWeight() {
        return mWeight;
    }

    public Item setWeight(Float weight) {
        mWeight = weight;
        return this;
    }

    public String getTreatment() {
        return mTreatment;
    }

    public Item setTreatment(String treatment) {
        mTreatment = treatment;
        return this;
    }

    public Product getProduct() {
        return mProduct;
    }

    public Item setProduct(Product product) {
        mProduct = product;
        return this;
    }
}
