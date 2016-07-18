package br.com.libertsolutions.crs.app.data.work;

import br.com.libertsolutions.crs.app.domain.pojo.Work;
import java.util.List;
import rx.Observable;

/**
 * Interface que define as operações de acesso e modificação aos dados de {@link Work}s ou
 * obras.
 *
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public interface WorkDataService {
    Observable<List<Work>> list();
    Observable<List<Work>> saveAll(List<Work> workList);
}
