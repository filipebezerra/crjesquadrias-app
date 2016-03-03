package br.com.libertsolutions.crs.app.checkin;

import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 03/03/2016
 * @since 0.1.0
 */
public interface CheckinService {
    @GET("CheckinApi/Get")
    Observable<List<Checkin>> getAllByStep(
            @Query("id_obra") long workId, @Query("id_etapa") long stepId);
}
