package br.com.libertsolutions.crs.app.flow;

import java.util.List;
import rx.Observable;

/**
 * Interface que define as operações de acesso e modificação aos dados de {@link Flow}s ou
 * fluxos.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 06/06/2016
 * @since 0.1.0
 * @see FlowRealmDataService
 */
public interface FlowDataService {
    Observable<List<Flow>> list(long workId);
    Observable<List<Flow>> saveAll(List<Flow> flowList);
}
