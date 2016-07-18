package br.com.libertsolutions.crs.app.domain.pojo;

import com.google.gson.annotations.SerializedName;

/**
 /**
 * Entidade Usuário, representa pessoa que está autenticada no aplicativo.
 *
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class User {
    @SerializedName("nome")
    String mName;

    @SerializedName("email")
    String mEmail;

    @SerializedName("cpf")
    String mCpf;

    public String getName() {
        return mName;
    }

    public User setName(String name) {
        mName = name;
        return this;
    }

    public String getEmail() {
        return mEmail;
    }

    public User setEmail(String email) {
        mEmail = email;
        return this;
    }

    public String getCpf() {
        return mCpf;
    }

    public User setCpf(String cpf) {
        mCpf = cpf;
        return this;
    }
}
