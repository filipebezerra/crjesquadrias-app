package br.com.libertsolutions.crs.app.checkin;

import com.google.gson.annotations.SerializedName;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 03/03/2016
 * @since 0.1.0
 */
public class Product {
    @SerializedName("idProduto")
    Long mProductId;

    @SerializedName("codigo")
    String mCode;

    @SerializedName("descricao")
    String mDescription;

    @SerializedName("peso")
    Float mWeight;

    @SerializedName("tratamento")
    String mTreatment;

    @SerializedName("tipo")
    String mType;

    public Long getProductId() {
        return mProductId;
    }

    public Product setProductId(Long productId) {
        mProductId = productId;
        return this;
    }

    public String getCode() {
        return mCode;
    }

    public Product setCode(String code) {
        mCode = code;
        return this;
    }

    public String getDescription() {
        return mDescription;
    }

    public Product setDescription(String description) {
        mDescription = description;
        return this;
    }

    public Float getWeight() {
        return mWeight;
    }

    public Product setWeight(Float weight) {
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
