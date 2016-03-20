package br.com.libertsolutions.crs.app.work;

import java.util.List;
import rx.Observable;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 20/03/2016
 * @since #
 */
public interface WorkDataService {
    Observable<List<Work>> list();
    Observable<Work> save(long id, Client client, String code, String date, String job, int status);
}
