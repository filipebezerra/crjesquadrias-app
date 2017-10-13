package br.com.libertsolutions.crs.app.data.util;

import android.support.annotation.NonNull;
import io.realm.Realm;
import io.realm.RealmUtil;
import io.realm.exceptions.RealmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

/**
 * {@link Observable.OnSubscribe} for RealmObject subclass that follows Observable contract
 *
 * @author Kirill Boyarshinov
 * @since 0.1.0
 */
abstract class OnSubscribeRealm<T> implements Observable.OnSubscribe<T> {
    private final List<Subscriber<? super T>> subscribers = new ArrayList<>();
    private final AtomicBoolean canceled = new AtomicBoolean();
    private final Object lock = new Object();

    @Override
    public void call(final Subscriber<? super T> subscriber) {
        synchronized (lock) {
            boolean canceled = this.canceled.get();
            if (!canceled && !subscribers.isEmpty()) {
                subscriber.add(newUnsubscribeAction(subscriber));
                subscribers.add(subscriber);
                return;
            } else if (canceled) {
                return;
            }
        }
        subscriber.add(newUnsubscribeAction(subscriber));
        subscribers.add(subscriber);

        Realm realm = Realm.getDefaultInstance();
        Timber.d(RealmUtil.dumpRealmCount());
        boolean withError = false;

        T object = null;
        try {
            if (!canceled.get()) {
                realm.beginTransaction();
                object = get(realm);
                if (object != null && !canceled.get()) {
                    realm.commitTransaction();
                } else {
                    realm.cancelTransaction();
                }
            }
        } catch (RuntimeException e) {
            realm.cancelTransaction();
            sendOnError(new RealmException("Error during transaction.", e));
            withError = true;
        } catch (Error e) {
            realm.cancelTransaction();
            sendOnError(e);
            withError = true;
        }
        if (object != null && !canceled.get() && !withError) {
            sendOnNext(object);
        }

        try {
            realm.close();
        } catch (RealmException ex) {
            sendOnError(ex);
            withError = true;
        }
        if (!withError) {
            sendOnCompleted();
        }
        canceled.set(false);
    }

    private void sendOnNext(T object) {
        for (int i = 0; i < subscribers.size(); i++) {
            Subscriber<? super T> subscriber = subscribers.get(i);
            subscriber.onNext(object);
        }
    }

    private void sendOnError(Throwable e) {
        for (int i = 0; i < subscribers.size(); i++) {
            Subscriber<? super T> subscriber = subscribers.get(i);
            subscriber.onError(e);
        }
    }

    private void sendOnCompleted() {
        for (int i = 0; i < subscribers.size(); i++) {
            Subscriber<? super T> subscriber = subscribers.get(i);
            subscriber.onCompleted();
        }
    }

    @NonNull
    private Subscription newUnsubscribeAction(final Subscriber<? super T> subscriber) {
        return Subscriptions.create(() -> {
            synchronized (lock) {
                subscribers.remove(subscriber);
                if (subscribers.isEmpty()) {
                    canceled.set(true);
                }
            }
        });
    }

    public abstract T get(Realm realm);
}
