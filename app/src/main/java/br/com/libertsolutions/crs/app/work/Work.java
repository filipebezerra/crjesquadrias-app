package br.com.libertsolutions.crs.app.work;

import com.google.gson.annotations.SerializedName;

/**
 * Objeto de transferência dos dados de Obra. Representa os projetos de construção ou reforma
 * dos interiores de um imóvel. Esta classe é o modelo da camada da API
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 18/03/2016
 * @since 0.1.0
 */
public class Work {
    @SerializedName("idObra")
    public long id;

    @SerializedName("Cliente")
    public Client client;

    @SerializedName("codigo")
    public String code;

    @SerializedName("data")
    public String date;

    @SerializedName("obra")
    public String job;

    @SerializedName("status")
    public int status;
}
