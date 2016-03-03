package br.com.libertsolutions.crs.app.work;

import com.google.gson.annotations.SerializedName;

/**
 * Entidade Cliente, representa o contratante do serviço da obra.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 03/03/2016
 * @since 0.1.0
 * @see Work
 */
public class Customer {
    @SerializedName("nome")
    String mNome;

    public String getNome() {
        return mNome;
    }

    public Customer setNome(String nome) {
        mNome = nome;
        return this;
    }
}
