package br.com.libertsolutions.crs.app.checkin;

import com.google.gson.annotations.SerializedName;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 03/03/2016
 * @since 0.1.0
 */
public class OrderGlass {
    @SerializedName("idPedVidro")
    Long mOrderGlassId;

    @SerializedName("quantidade")
    Integer mQuantity;

    @SerializedName("numero")
    String mNumber;

    @SerializedName("cor")
    String mColor;

    @SerializedName("largura")
    Float mWidth;

    @SerializedName("altura")
    Float mHeight;

    @SerializedName("peso")
    Float mWeight;

    @SerializedName("Produto")
    Product mProduct;

    public Long getOrderGlassId() {
        return mOrderGlassId;
    }

    public OrderGlass setOrderGlassId(Long orderGlassId) {
        mOrderGlassId = orderGlassId;
        return this;
    }

    public Integer getQuantity() {
        return mQuantity;
    }

    public OrderGlass setQuantity(Integer quantity) {
        mQuantity = quantity;
        return this;
    }

    public String getNumber() {
        return mNumber;
    }

    public OrderGlass setNumber(String number) {
        mNumber = number;
        return this;
    }

    public String getColor() {
        return mColor;
    }

    public OrderGlass setColor(String color) {
        mColor = color;
        return this;
    }

    public Float getWidth() {
        return mWidth;
    }

    public OrderGlass setWidth(Float width) {
        mWidth = width;
        return this;
    }

    public Float getHeight() {
        return mHeight;
    }

    public OrderGlass setHeight(Float height) {
        mHeight = height;
        return this;
    }

    public Float getWeight() {
        return mWeight;
    }

    public OrderGlass setWeight(Float weight) {
        mWeight = weight;
        return this;
    }

    public Product getProduct() {
        return mProduct;
    }

    public OrderGlass setProduct(Product product) {
        mProduct = product;
        return this;
    }
}
