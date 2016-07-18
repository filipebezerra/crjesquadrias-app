package br.com.libertsolutions.crs.app.data.work;

import android.content.Context;
import br.com.libertsolutions.crs.app.domain.entity.ClientEntity;
import br.com.libertsolutions.crs.app.domain.entity.WorkEntity;
import br.com.libertsolutions.crs.app.domain.pojo.Client;
import br.com.libertsolutions.crs.app.domain.pojo.Work;
import br.com.libertsolutions.crs.app.data.util.RealmObservable;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;

/**
 * Implementação do contrato de acesso e modificação à dados dos {@link Work}s ou obras.
 * É especializada para executar transações e manipular os dados utilizando a biblioteca Realm.
 *
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class WorkRealmDataService implements WorkDataService {
    private final Context mContext;

    public WorkRealmDataService(Context context) {
        mContext = context;
    }

    @Override
    public Observable<List<Work>> list() {
        return RealmObservable.results(mContext, new Func1<Realm, RealmResults<WorkEntity>>() {
            @Override
            public RealmResults<WorkEntity> call(Realm realm) {
                // find all
                return realm.where(WorkEntity.class).findAll();
            }
        }).map(new Func1<RealmResults<WorkEntity>, List<Work>>() {
            @Override
            public List<Work> call(RealmResults<WorkEntity> workEntities) {
                // map them to UI objects
                final List<Work> workList = new ArrayList<>(workEntities.size());
                for (WorkEntity workEntity : workEntities) {
                    workList.add(workFromRealm(workEntity));
                }
                return workList;
            }
        });
    }

    @Override
    public Observable<List<Work>> saveAll(final List<Work> workList) {
        return RealmObservable.list(mContext, new Func1<Realm, RealmList<WorkEntity>>() {
            @Override
            public RealmList<WorkEntity> call(Realm realm) {
                List<WorkEntity> workEntityList = new ArrayList<>(workList.size());

                for (Work work : workList) {
                    ClientEntity clientEntity = new ClientEntity();
                    clientEntity.setName(work.getClient().getName());
                    clientEntity = realm.copyToRealmOrUpdate(clientEntity);

                    WorkEntity workEntity = new WorkEntity();
                    workEntity.setWorkId(work.getWorkId());
                    workEntity.setClient(clientEntity);
                    workEntity.setCode(work.getCode());
                    workEntity.setDate(work.getDate());
                    workEntity.setJob(work.getJob());
                    workEntity.setStatus(work.getStatus());

                    workEntityList.add(realm.copyToRealmOrUpdate(workEntity));
                }

                return new RealmList<>(workEntityList.toArray(new WorkEntity[workEntityList.size()]));
            }
        }).map(new Func1<RealmList<WorkEntity>, List<Work>>() {
            @Override
            public List<Work> call(RealmList<WorkEntity> workEntities) {
                List<Work> list = new ArrayList<>(workList.size());
                for (WorkEntity workEntity : workEntities) {
                    list.add(workFromRealm(workEntity));
                }
                return list;
            }
        });
    }

    private Work workFromRealm(WorkEntity workEntity) {
        final Long workId = workEntity.getWorkId();
        final ClientEntity client = workEntity.getClient();
        final String code = workEntity.getCode();
        final String date = workEntity.getDate();
        final String job = workEntity.getJob();
        final Integer status = workEntity.getStatus();
        return new Work(workId, clientFromRealm(client), code, date, job, status);
    }

    private Client clientFromRealm(ClientEntity client) {
        return new Client(client.getName());
    }

    private ClientEntity toClientEntity(Client client) {
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setName(client.getName());
        return clientEntity;
    }

    private WorkEntity toWorkEntity(Work work, ClientEntity clientEntity) {
        WorkEntity workEntity = new WorkEntity();
        workEntity.setWorkId(work.getWorkId());
        workEntity.setClient(clientEntity);
        workEntity.setCode(work.getCode());
        workEntity.setDate(work.getDate());
        workEntity.setJob(work.getJob());
        workEntity.setStatus(work.getStatus());
        return workEntity;
    }
}
