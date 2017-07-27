package br.com.libertsolutions.crs.app.domain.pojo;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * @author Filipe Bezerra
 * @since 1.0.1
 */
public class Flows extends BaseResponse {
    @SerializedName("Fluxo") public List<Flow> list;
}
