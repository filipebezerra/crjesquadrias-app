package br.com.libertsolutions.crs.app.work;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 10/02/2016
 * @since #
 */
public final class Works {
    private static final List<Work> DATA_SET;

    static {
        List<Work> list = Arrays.asList(
                new Work()
                        .setWorkId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryDate(System.currentTimeMillis()),

                new Work()
                        .setWorkId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryDate(System.currentTimeMillis()),

                new Work()
                        .setWorkId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryDate(System.currentTimeMillis()),

                new Work()
                        .setWorkId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryDate(System.currentTimeMillis()),

                new Work()
                        .setWorkId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryDate(System.currentTimeMillis()),

                new Work()
                        .setWorkId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryDate(System.currentTimeMillis()),

                new Work()
                        .setWorkId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryDate(System.currentTimeMillis()),

                new Work()
                        .setWorkId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryDate(System.currentTimeMillis()),

                new Work()
                        .setWorkId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryDate(System.currentTimeMillis()),

                new Work()
                        .setWorkId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryDate(System.currentTimeMillis()),

                new Work()
                        .setWorkId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryDate(System.currentTimeMillis()),

                new Work()
                        .setWorkId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryDate(System.currentTimeMillis())
        );

        DATA_SET = Collections.unmodifiableList(list);
    }

    public static List<Work> getDataSet() {
        return DATA_SET;
    }
}