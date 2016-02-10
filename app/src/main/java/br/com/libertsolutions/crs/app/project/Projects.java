package br.com.libertsolutions.crs.app.project;

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
public final class Projects {
    private static final List<Project> DATA_SET;

    static {
        List<Project> list = Arrays.asList(
                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis()),

                new Project()
                        .setProjectId("JUL.379.15-REV05")
                        .setCustomerName("Gabriella e Cristiano")
                        .setDeliveryForecast(System.currentTimeMillis())
        );

        DATA_SET = Collections.unmodifiableList(list);
    }

    public static List<Project> getDataSet() {
        return DATA_SET;
    }
}
