package br.com.libertsolutions.crs.app.checkin;

import java.util.List;
import rx.Observable;

/**
 * Interface que define as operações de acesso e modificação aos dados de {@link Checkin}s.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 01/06/2016
 * @since 0.1.0
 */
public interface CheckinDataService {
    Observable<List<Checkin>> list(long flowId);
    Observable<List<Checkin>> saveAll(List<Checkin> checkinList);
    Observable<Checkin> updateSyncState(Checkin checkin, boolean syncPending);
    Observable<List<Checkin>> updateSyncState(List<Checkin> checkins, boolean syncPending);
    Observable<List<Checkin>> listPendingSynchronization();
}
