package br.com.libertsolutions.crs.app.work;

import java.util.List;
import retrofit2.http.GET;
import rx.Observable;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 02/03/2016
 * @since 0.1.0
 */
public interface WorkService {
    @GET("ObraApi/Get")
    Observable<List<Work>> getAllRunning();
}
