package br.com.libertsolutions.crs.app.checkin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 13/02/2016
 * @since #
 */
public final class Checkins {
    private static final List<Checkin> DATA_SET;

    static {
        List<Checkin> list = Arrays.asList();

        DATA_SET = Collections.unmodifiableList(list);
    }

    public static List<Checkin> getDataSet() {
        return DATA_SET;
    }
}
