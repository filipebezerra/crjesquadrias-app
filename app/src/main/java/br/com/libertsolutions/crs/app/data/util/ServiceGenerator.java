package br.com.libertsolutions.crs.app.data.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import br.com.libertsolutions.crs.app.data.settings.SettingsDataHelper;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.realm.RealmObject;
import java.io.File;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import static br.com.libertsolutions.crs.app.BuildConfig.DEBUG;
import static java.util.concurrent.TimeUnit.SECONDS;
import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;
import static okhttp3.logging.HttpLoggingInterceptor.Level.NONE;
import static rx.schedulers.Schedulers.io;

/**
 * Classe utilitária para configuração do {@link Retrofit} e instanciação das
 * classes de serviço que consomem os recursos http do Web service.
 *
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class ServiceGenerator {
    private static final long HTTP_CACHE_SIZE = 10 * 1024 * 1024;
    private static final String HTTP_CACHE_FILE_NAME = "http";
    private static final String CACHE_CONTROL = "Cache-Control";

    private static Retrofit retrofit;

    private static String baseUrl;
    private static String authKey;

    public static <S> S createService(@NonNull Class<S> serviceClass, @NonNull Context context) {
        final String baseUrl = SettingsDataHelper.getServerUrl(context);
        final String authKey = SettingsDataHelper.getServerAuthKey(context);

        if (TextUtils.isEmpty(baseUrl) || TextUtils.isEmpty(authKey)) {
            return null;
        }

        if (retrofit == null ||
                (!baseUrl.equals(ServiceGenerator.baseUrl) || !authKey.equals(
                        ServiceGenerator.authKey))) {

            final OkHttpClient httpClient = new OkHttpClient
                    .Builder()
                    .connectTimeout(30, SECONDS)
                    .readTimeout(30, SECONDS)
                    .writeTimeout(30, SECONDS)
                    .cache(createCache(context))
                    .addInterceptor(createLoggingInterceptor())
                    .addInterceptor(createInterceptorWithAuthKey(authKey))
                    .addNetworkInterceptor(createCacheInterceptor())
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

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(io()))
                    .client(httpClient)
                    .build();

            ServiceGenerator.authKey = authKey;
            ServiceGenerator.baseUrl = baseUrl;
        }

        return retrofit.create(serviceClass);
    }

    private static Cache createCache(@NonNull Context context) {
        Cache cache = null;
        try {
            cache = new Cache(
                    new File(context.getCacheDir(), HTTP_CACHE_FILE_NAME), HTTP_CACHE_SIZE);
        } catch (Exception e) {
            Timber.e(e, "Could not create Cache!");
        }
        return cache;
    }

    private static Interceptor createLoggingInterceptor() {
        return new HttpLoggingInterceptor().setLevel(DEBUG ? BODY : NONE);
    }

    private static Interceptor createInterceptorWithAuthKey(@NonNull final String authKey) {
        return chain -> {
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
        };
    }

    private static Interceptor createCacheInterceptor() {
        return chain -> {
            Response response = chain.proceed(chain.request());

            // re-write response header to force use of cache
            CacheControl cacheControl = new CacheControl.Builder()
                    .maxAge(2, TimeUnit.MINUTES)
                    .build();

            return response.newBuilder()
                    .header(CACHE_CONTROL, cacheControl.toString())
                    .build();
        };
    }
}
