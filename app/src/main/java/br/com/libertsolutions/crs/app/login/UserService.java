package br.com.libertsolutions.crs.app.login;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 04/03/2016
 * @since 0.1.0
 */
public interface UserService {
    @GET("UsuarioApi/Get")
    Observable<Boolean> validateUser(
            @Query("cpf") String cpf, @Query("senha") String password);
}
