package br.com.libertsolutions.crs.app.work;

import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 27/03/2016
 * @since 0.1.0,
 */
public interface WorkService {
    @GET("ObraApi/Get")
    Observable<List<Work>> getAll();

    @GET("ObraApi/Get")
    Observable<List<Work>> getAllWithUpdates(@Query("ultimaAtualizacao") String lastUpdate);
}
