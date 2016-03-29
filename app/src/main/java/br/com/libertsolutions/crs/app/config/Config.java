package br.com.libertsolutions.crs.app.config;

import com.google.gson.annotations.SerializedName;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 29/03/2016
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
