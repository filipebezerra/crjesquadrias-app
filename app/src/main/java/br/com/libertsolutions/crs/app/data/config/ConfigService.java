package br.com.libertsolutions.crs.app.data.config;

import br.com.libertsolutions.crs.app.domain.pojo.Config;
import retrofit2.http.GET;
import rx.Observable;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public interface ConfigService {
    @GET("ConfigApi/Get")
    Observable<Config> get();
}
