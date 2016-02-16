package br.com.libertsolutions.crs.app.checkin;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 15/02/2016
 * @since 0.1.0
 */
public class Product {
    long mProductId;

    String mDescription;

    String mCode;

    float mWeight;

    String mTreatment;

    String mType;

    public long getProductId() {
        return mProductId;
    }

    public Product setProductId(long productId) {
        mProductId = productId;
        return this;
    }

    public String getDescription() {
        return mDescription;
    }

    public Product setDescription(String description) {
        mDescription = description;
        return this;
    }

    public String getCode() {
        return mCode;
    }

    public Product setCode(String code) {
        mCode = code;
        return this;
    }

    public float getWeight() {
        return mWeight;
    }

    public Product setWeight(float weight) {
        mWeight = weight;
        return this;
    }

    public String getTreatment() {
        return mTreatment;
    }

    public Product setTreatment(String treatment) {
        mTreatment = treatment;
        return this;
    }

    public String getType() {
        return mType;
    }

    public Product setType(String type) {
        mType = type;
        return this;
    }
}
