package br.com.libertsolutions.crs.app.date;

import android.text.format.DateUtils;
import com.crashlytics.android.Crashlytics;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 03/03/2016
 * @since 0.1.0
 */
public class DateUtil {
    private static final String DATE_FORMAT_WS = "yyyy-MM-dd'T'HH:mm:ss";

    private static final Locale BRAZIL_LOCALE = new Locale("pt", "BR");

    private static final SimpleDateFormat DATE_FORMATTER_WS =
            new SimpleDateFormat(DATE_FORMAT_WS, BRAZIL_LOCALE);

    public static CharSequence formatAsRelativeDateFromNow(String dateString) {
        try {
            final Date date = DATE_FORMATTER_WS.parse(dateString);
            return DateUtils.getRelativeTimeSpanString(date.getTime(),
                    System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS);
        } catch (ParseException e) {
            Crashlytics.logException(e);
            return null;
        }
    }
}
