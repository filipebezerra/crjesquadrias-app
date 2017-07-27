package br.com.libertsolutions.crs.app.data.flow;

import br.com.libertsolutions.crs.app.domain.pojo.Flows;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public interface FlowService {
    @GET("FluxoApi/Get")
    Observable<Flows> getAll(@Query("page") int page);

    @GET("FluxoApi/Get")
    Observable<Flows> getAllWithUpdates(
            @Query("ultimaAtualizacao") String lastUpdate, @Query("page") int page);
}