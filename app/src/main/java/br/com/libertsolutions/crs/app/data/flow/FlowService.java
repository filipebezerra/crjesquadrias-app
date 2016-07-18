package br.com.libertsolutions.crs.app.data.flow;

import br.com.libertsolutions.crs.app.domain.pojo.Flow;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public interface FlowService {
    @GET("FluxoApi/Get")
    Observable<List<Flow>> getAll();

    @GET("FluxoApi/Get")
    Observable<List<Flow>> getByWorkId(@Query("id_obra") long workId);

    @GET("FluxoApi/Get")
    Observable<List<Flow>> getAllWithUpdates(@Query("ultimaAtualizacao") String lastUpdate);
}