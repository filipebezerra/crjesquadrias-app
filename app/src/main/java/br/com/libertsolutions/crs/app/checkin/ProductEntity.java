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
public class ProductEntity extends RealmObject {
    @PrimaryKey
    private Long productId;

    private String code;

    private String description;

    private Float weight;

    private String treatment;

    private String type;

    private String line;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof ProductEntity) {
            final ProductEntity anotherProduct = (ProductEntity) o;
            return getProductId().compareTo(anotherProduct.getProductId()) == 0;
        }
        return false;
    }
}
