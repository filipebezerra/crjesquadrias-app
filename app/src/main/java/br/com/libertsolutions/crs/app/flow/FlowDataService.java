package br.com.libertsolutions.crs.app.flow;

import java.util.List;
import rx.Observable;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 04/06/2016
 * @since 0.1.0
 */
public interface FlowDataService {
    Observable<List<Flow>> list(long workId);
    Observable<List<Flow>> saveAll(List<Flow> flowList);
    void saveAllSync(List<Flow> flowList);
}
