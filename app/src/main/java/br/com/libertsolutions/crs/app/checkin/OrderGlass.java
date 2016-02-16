package br.com.libertsolutions.crs.app.checkin;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 15/02/2016
 * @since 0.1.0
 */
public class OrderGlass {
    long mOrderGlassId;

    int mQuantity;

    String mNumber;

    String mColor;

    float mWidth;

    float mHeight;

    float mWeight;

    Product mProduct;

    public long getOrderGlassId() {
        return mOrderGlassId;
    }

    public OrderGlass setOrderGlassId(long orderGlassId) {
        mOrderGlassId = orderGlassId;
        return this;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public OrderGlass setQuantity(int quantity) {
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

    public float getWidth() {
        return mWidth;
    }

    public OrderGlass setWidth(float width) {
        mWidth = width;
        return this;
    }

    public float getHeight() {
        return mHeight;
    }

    public OrderGlass setHeight(float height) {
        mHeight = height;
        return this;
    }

    public float getWeight() {
        return mWeight;
    }

    public OrderGlass setWeight(float weight) {
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
