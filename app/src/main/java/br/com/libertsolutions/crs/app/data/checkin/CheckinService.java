package br.com.libertsolutions.crs.app.data.checkin;

import br.com.libertsolutions.crs.app.domain.pojo.Checkin;
import java.util.List;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public interface CheckinService {
    @GET("CheckinApi/Get")
    Observable<List<Checkin>> getAll();

    @GET("CheckinApi/Get")
    Observable<List<Checkin>> getByFlowId(@Query("id_obra") long workId,
            @Query("id_etapa") long stepId);

    @GET("CheckinApi/Get")
    Observable<List<Checkin>> getAllWithUpdates(@Query("ultimaAtualizacao") String lastUpdate);

    @POST("CheckinApi/Post")
    Observable<Checkin> post(@Query("cpf") String cpf, @Body Checkin checkin);

    @PATCH("CheckinApi/Patch")
    Observable<List<Checkin>> patch(@Query("cpf") String cpf, @Body List<Checkin> checkins);
}