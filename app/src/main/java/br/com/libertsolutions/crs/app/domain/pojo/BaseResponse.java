package br.com.libertsolutions.crs.app.domain.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * @author Filipe Bezerra
 * @since 1.0.1
 */
public abstract class BaseResponse {
    @SerializedName("TotalPaginas") public int totalPaginas;
}
