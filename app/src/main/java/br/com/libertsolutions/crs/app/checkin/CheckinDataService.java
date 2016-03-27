package br.com.libertsolutions.crs.app.checkin;

import java.util.List;
import rx.Observable;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 24/03/2016
 * @since 0.1.0
 */
public interface CheckinDataService {
    Observable<List<Checkin>> list(long flowId);
    Observable<List<Checkin>> saveAll(long flowId, List<Checkin> checkinList);
}