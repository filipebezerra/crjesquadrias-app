package br.com.libertsolutions.crs.app.data.login;

import br.com.libertsolutions.crs.app.domain.pojo.LoginBody;
import br.com.libertsolutions.crs.app.domain.pojo.User;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public interface UserService {
    @POST("UsuarioApi/Get")
    Observable<User> authenticateUser(@Body LoginBody loginBody);
}
