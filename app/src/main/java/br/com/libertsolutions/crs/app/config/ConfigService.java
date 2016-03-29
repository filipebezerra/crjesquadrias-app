package br.com.libertsolutions.crs.app.config;

import retrofit2.http.GET;
import rx.Observable;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 29/03/2016
 * @since 0.1.0
 */
public interface ConfigService {
    @GET("ConfigApi/Get")
    Observable<Config> get();
}
