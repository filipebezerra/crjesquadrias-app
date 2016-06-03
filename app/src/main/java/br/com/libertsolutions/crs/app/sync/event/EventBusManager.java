package br.com.libertsolutions.crs.app.sync.event;

import android.support.annotation.NonNull;
import org.greenrobot.eventbus.EventBus;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 02/06/2016
 * @since #
 */
public class EventBusManager {
    public static void register(@NonNull Object subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    public static void unregister(@NonNull Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    public static void send(@NonNull Object event) {
        EventBus.getDefault().post(event);
    }
}
