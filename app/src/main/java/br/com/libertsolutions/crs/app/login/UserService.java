package br.com.libertsolutions.crs.app.login;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 04/03/2016
 * @since 0.1.0
 */
public interface UserService {
    @POST("UsuarioApi/Get")
    Observable<User> authenticateUser(@Body LoginBody loginBody);
}
