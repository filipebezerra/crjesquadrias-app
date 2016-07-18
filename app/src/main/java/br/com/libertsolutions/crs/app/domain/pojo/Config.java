package br.com.libertsolutions.crs.app.domain.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class Config {
    @SerializedName("dataAtual")
    private final String dataAtual;

    public Config(String dataAtual) {
        this.dataAtual = dataAtual;
    }

    public String getDataAtual() {
        return dataAtual;
    }
}
