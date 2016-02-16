package br.com.libertsolutions.crs.app.checkin;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 15/02/2016
 * @since 0.1.0
 */
public class Item {
    long mItemId;

    int mQuantity;

    float mWidth;

    float mHeight;

    float mWeight;

    String mLocation;

    String mTreatment;

    Product mProduct;

    public long getItemId() {
        return mItemId;
    }

    public Item setItemId(long itemId) {
        mItemId = itemId;
        return this;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public Item setQuantity(int quantity) {
        mQuantity = quantity;
        return this;
    }

    public float getWidth() {
        return mWidth;
    }

    public Item setWidth(float width) {
        mWidth = width;
        return this;
    }

    public float getHeight() {
        return mHeight;
    }

    public Item setHeight(float height) {
        mHeight = height;
        return this;
    }

    public float getWeight() {
        return mWeight;
    }

    public Item setWeight(float weight) {
        mWeight = weight;
        return this;
    }

    public String getLocation() {
        return mLocation;
    }

    public Item setLocation(String location) {
        mLocation = location;
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
