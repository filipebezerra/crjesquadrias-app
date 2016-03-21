package br.com.libertsolutions.crs.app.work;

import android.content.Context;
import br.com.libertsolutions.crs.app.rx.RealmObservable;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 20/03/2016
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
    public Observable<Work> save(final Work work) {
        // map internal UI objects to Realm objects
        final ClientEntity clientEntity = new ClientEntity();
        clientEntity.setName(work.getClient().getName());

        return RealmObservable.object(mContext, new Func1<Realm, WorkEntity>() {
            @Override
            public WorkEntity call(Realm realm) {
                // internal object instances are not created by realm
                // saving them using copyToRealm returning instance associated with realm
                ClientEntity client = realm.copyToRealmOrUpdate(clientEntity);

                WorkEntity workEntity = new WorkEntity();
                workEntity.setWorkId(work.getWorkId());
                workEntity.setClient(client);
                workEntity.setCode(work.getCode());
                workEntity.setDate(work.getDate());
                workEntity.setJob(work.getJob());
                workEntity.setStatus(work.getStatus());

                return realm.copyToRealmOrUpdate(workEntity);
            }
        }).map(new Func1<WorkEntity, Work>() {
            @Override
            public Work call(WorkEntity workEntity) {
                return workFromRealm(workEntity);
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

    private static Work workFromRealm(WorkEntity workEntity) {
        final Long workId = workEntity.getWorkId();
        final ClientEntity client = workEntity.getClient();
        final String code = workEntity.getCode();
        final String date = workEntity.getDate();
        final String job = workEntity.getJob();
        final Integer status = workEntity.getStatus();
        return new Work(workId, clientFromRealm(client), code, date, job, status);
    }

    private static Client clientFromRealm(ClientEntity client) {
        return new Client(client.getName());
    }
}
