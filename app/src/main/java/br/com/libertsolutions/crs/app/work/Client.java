package br.com.libertsolutions.crs.app.work;

import com.google.gson.annotations.SerializedName;

/**
 * Objeto de transferência dos dados de Cliente. Representa o contratante do serviço da obra.
 * Esta classe é o modelo da camada da API.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 20/03/2016
 * @since 0.1.0
 */
public class Client {
    @SerializedName("nome")
    private final String name;

    public Client(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
