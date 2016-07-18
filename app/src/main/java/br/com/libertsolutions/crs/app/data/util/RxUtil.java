package br.com.libertsolutions.crs.app.data.util;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.functions.Func1;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class RxUtil {
    public static Func1<Observable<? extends Throwable>, Observable<?>> exponentialBackoff(
            final int numOfRetries, final int delay, final TimeUnit unit) {
        return errors -> errors
                .zipWith(Observable.range(1, numOfRetries), (n, i) -> i)
                .flatMap(retryCount -> Observable.timer((long) Math.pow(delay, retryCount), unit));
    }

    public static Func1<Observable<? extends Throwable>, Observable<?>> timeoutException() {
        return errors -> errors
                .flatMap(error -> {
                    if (error instanceof SocketTimeoutException) {
                        return Observable.just(null);
                    }

                    return Observable.error(error);
                });
    }
}
