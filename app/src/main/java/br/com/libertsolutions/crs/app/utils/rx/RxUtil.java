package br.com.libertsolutions.crs.app.utils.rx;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.functions.Func1;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 05/06/2016
 * @since #
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
