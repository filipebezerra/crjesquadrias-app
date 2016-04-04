package br.com.libertsolutions.crs.app.checkin;

import com.google.gson.annotations.SerializedName;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 23/03/2016
 * @since 0.1.0
 */
public class Product {
    @SerializedName("idProduto")
    private final long productId;

    @SerializedName("codigo")
    private final String code;

    @SerializedName("descricao")
    private final String description;

    @SerializedName("peso")
    private final float weight;

    @SerializedName("tratamento")
    private final String treatment;

    @SerializedName("tipo")
    private final String type;

    @SerializedName("linha")
    private final String line;

    public Product(long productId, String code, String description, float weight,
            String treatment, String type, String line) {
        this.productId = productId;
        this.code = code;
        this.description = description;
        this.weight = weight;
        this.treatment = treatment;
        this.type = type;
        this.line = line;
    }

    public long getProductId() {
        return productId;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public float getWeight() {
        return weight;
    }

    public String getTreatment() {
        return treatment;
    }

    public String getType() {
        return type;
    }

    public String getLine() {
        return line;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Product) {
            final Product anotherProduct = (Product) o;
            return getProductId() == anotherProduct.getProductId();
        }
        return false;
    }
}
