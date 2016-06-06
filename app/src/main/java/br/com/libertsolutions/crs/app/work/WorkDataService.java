package br.com.libertsolutions.crs.app.work;

import java.util.List;
import rx.Observable;

/**
 * Interface que define as operações de acesso e modificação aos dados de {@link Work}s ou
 * obras.
 *
 * @author Filipe Bezerra
 * @version #, 06/06/2016
 * @since #
 */
public interface WorkDataService {
    Observable<List<Work>> list();
    Observable<List<Work>> saveAll(List<Work> workList);
}
