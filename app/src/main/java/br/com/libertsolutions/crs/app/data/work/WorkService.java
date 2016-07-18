package br.com.libertsolutions.crs.app.data.work;

import br.com.libertsolutions.crs.app.domain.pojo.Work;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author Filipe Bezerra
 * @since 0.1.0,
 */
public interface WorkService {
    @GET("ObraApi/Get")
    Observable<List<Work>> getAll();

    @GET("ObraApi/Get")
    Observable<List<Work>> getAllWithUpdates(@Query("ultimaAtualizacao") String lastUpdate);
}
