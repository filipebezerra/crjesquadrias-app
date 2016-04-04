package br.com.libertsolutions.crs.app.webservice;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import br.com.libertsolutions.crs.app.BuildConfig;
import br.com.libertsolutions.crs.app.settings.SettingsHelper;
import io.realm.RealmObject;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Classe utilitária para configuração do {@link Retrofit} e instanciação das
 * classes de serviço que consomem os recursos http do Web service.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 04/04/2016
 * @since 0.1.0
 */
public class ServiceGenerator {
    private static final long HTTP_CACHE_SIZE = 10 * 1024 * 1024;
    private static final String HTTP_CACHE_FILE_NAME = "http";

    private static Retrofit sRetrofit;

    private static String sBaseUrl;
    private static String sAuthKey;

    public static <S> S createService(@NonNull Class<S> serviceClass, @NonNull Context context) {
        final String baseUrl = SettingsHelper.getServerUrl(context);
        final String authKey = SettingsHelper.getServerAuthKey(context);

        if (TextUtils.isEmpty(baseUrl) || TextUtils.isEmpty(authKey)) {
            return null;
        }

        if (sRetrofit == null ||
                (!baseUrl.equals(sBaseUrl) || !authKey.equals(sAuthKey))) {

            final OkHttpClient httpClient = new OkHttpClient
                    .Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .cache(createCache(context))
                    .addInterceptor(createLoggingInterceptor())
                    .addInterceptor(createInterceptorWithAuthKey(authKey))
                    .build();

            final Gson gson = new GsonBuilder()
                    .setExclusionStrategies(new ExclusionStrategy() {
                        @Override
                        public boolean shouldSkipField(FieldAttributes f) {
                            return f.getDeclaringClass().equals(RealmObject.class);
                        }

                        @Override
                        public boolean shouldSkipClass(Class<?> clazz) {
                            return false;
                        }
                    })
                    .create();

            sRetrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(httpClient)
                    .build();

            sAuthKey = authKey;
            sBaseUrl = baseUrl;
        }

        return sRetrofit.create(serviceClass);
    }

    private static Cache createCache(@NonNull Context context) {
        return new Cache(new File(context.getCacheDir(), HTTP_CACHE_FILE_NAME), HTTP_CACHE_SIZE);
    }

    private static Interceptor createLoggingInterceptor() {
        HttpLoggingInterceptor.Level level;
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY;
        } else {
            level = HttpLoggingInterceptor.Level.NONE;
        }
        return new HttpLoggingInterceptor().setLevel(level);
    }

    private static Interceptor createInterceptorWithAuthKey(@NonNull final String authKey) {
        return new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                final HttpUrl httpUrl = original.url()
                        .newBuilder()
                        .addQueryParameter("key", authKey)
                        .build();

                final Request request = original.newBuilder()
                        .url(httpUrl)
                        .header("Accept", "applicaton/json")
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        };
    }
}
