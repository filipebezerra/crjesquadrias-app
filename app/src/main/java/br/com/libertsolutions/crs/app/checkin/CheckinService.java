package br.com.libertsolutions.crs.app.checkin;

import java.util.List;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 04/03/2016
 * @since 0.1.0
 */
public interface CheckinService {
    @GET("CheckinApi/Get")
    Observable<List<Checkin>> getByFlowId(@Query("id_obra") long workId,
            @Query("id_etapa") long stepId);

    @GET("CheckinApi/Get")
    Observable<List<Checkin>> getAllWithUpdates(@Query("ultimaAtualizacao") String lastUpdate);

    @POST("CheckinApi/Post")
    Observable<Checkin> post(@Query("cpf") String cpf, @Body Checkin checkin);
}
