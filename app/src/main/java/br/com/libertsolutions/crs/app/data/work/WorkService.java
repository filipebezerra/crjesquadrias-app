package br.com.libertsolutions.crs.app.data.work;

import br.com.libertsolutions.crs.app.domain.pojo.Works;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author Filipe Bezerra
 * @since 0.1.0,
 */
public interface WorkService {
    @GET("ObraApi/Get")
    Observable<Works> getAll(@Query("page") int page);

    @GET("ObraApi/Get")
    Observable<Works> getAllWithUpdates(
            @Query("ultimaAtualizacao") String lastUpdate, @Query("page") int page);
}
