package br.com.libertsolutions.crs.app.domain.pojo;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * @author Filipe Bezerra
 */
public class Works extends BaseResponse {
    @SerializedName("Obra") public List<Work> list;
}
