package br.com.libertsolutions.crs.app.data.checkin;

import br.com.libertsolutions.crs.app.data.util.RealmObservable;
import br.com.libertsolutions.crs.app.domain.entity.CheckinEntity;
import br.com.libertsolutions.crs.app.domain.entity.ItemEntity;
import br.com.libertsolutions.crs.app.domain.entity.OrderGlassEntity;
import br.com.libertsolutions.crs.app.domain.entity.ProductEntity;
import br.com.libertsolutions.crs.app.domain.pojo.Checkin;
import br.com.libertsolutions.crs.app.domain.pojo.Item;
import br.com.libertsolutions.crs.app.domain.pojo.OrderGlass;
import br.com.libertsolutions.crs.app.domain.pojo.Product;
import io.realm.RealmList;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;

/**
 * Implementação do contrato de acesso e modificação à dados dos {@link Checkin}s.
 * É especializada para executar transações e manipular os dados utilizando a biblioteca Realm.
 *
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class CheckinRealmDataService implements CheckinDataService {

    private Checkin checkinFromRealm(CheckinEntity checkinEntity) {
        if (checkinEntity == null) {
            return null;
        }

        final Long checkinId = checkinEntity.getCheckinId();
        final Long flowId = checkinEntity.getFlowId();
        final String date = checkinEntity.getDate();
        final Integer status = checkinEntity.getStatus();
        final ItemEntity item = checkinEntity.getItem();
        final OrderGlassEntity orderGlass = checkinEntity.getOrderGlass();
        final String location = checkinEntity.getLocation();

        return new Checkin(checkinId, flowId, date, status, itemFromRealm(item),
                orderGlassFromRealm(orderGlass), location);
    }

    private Item itemFromRealm(ItemEntity item) {
        if (item == null) {
            return null;
        }

        final Long itemId = item.getItemId();
        final Integer quantity = item.getQuantity();
        final Float width = item.getWidth();
        final Float height = item.getHeight();
        final Float weight = item.getWeight();
        final String treatment = item.getTreatment();
        final ProductEntity product = item.getProduct();

        return new Item(itemId, quantity, width, height, weight, treatment,
                productFromRealm(product));
    }

    private OrderGlass orderGlassFromRealm(OrderGlassEntity orderGlass) {
        if (orderGlass == null) {
            return null;
        }

        final Long orderGlassId = orderGlass.getOrderGlassId();
        final Integer quantity = orderGlass.getQuantity();
        final String number = orderGlass.getNumber();
        final String color = orderGlass.getColor();
        final Float width = orderGlass.getWidth();
        final Float height = orderGlass.getHeight();
        final Float weight = orderGlass.getWeight();
        final ProductEntity product = orderGlass.getProduct();

        return new OrderGlass(orderGlassId, quantity, number, color, width, height, weight,
                productFromRealm(product));
    }

    private Product productFromRealm(ProductEntity product) {
        final Long productId = product.getProductId();
        final String code = product.getCode();
        final String description = product.getDescription();
        final Float weight = product.getWeight();
        final String treatment = product.getTreatment();
        final String type = product.getType();
        final String line = product.getLine();

        return new Product(productId, code, description, weight, treatment, type, line);
    }

    @Override
    public Observable<List<Checkin>> list(final long flowId) {
        return RealmObservable.results(realm -> {
            // find all
            return realm.where(CheckinEntity.class)
                    .equalTo(CheckinEntity.FIELD_FLOW_ID, flowId)
                    .findAll();
        }).map(checkinEntities -> {
            // map them to UI objects
            final List<Checkin> checkinList = new ArrayList<>(checkinEntities.size());
            for (CheckinEntity checkinEntity : checkinEntities) {
                checkinList.add(checkinFromRealm(checkinEntity));
            }

            return checkinList;
        });
    }

    @Override
    public Observable<List<Checkin>> saveAll(final List<Checkin> checkinList) {
        return RealmObservable.list(realm -> {
            List<CheckinEntity> checkinEntityList = new ArrayList<>(checkinList.size());

            for(Checkin checkin : checkinList) {
                ItemEntity itemEntity = null;
                OrderGlassEntity orderGlassEntity = null;
                ProductEntity productEntity = new ProductEntity();

                if (checkin.getItem() != null) {
                    productEntity.setProductId(
                            checkin.getItem().getProduct().getProductId());
                    productEntity.setCode(
                            checkin.getItem().getProduct().getCode());
                    productEntity.setDescription(
                            checkin.getItem().getProduct().getDescription());
                    productEntity.setWeight(
                            checkin.getItem().getProduct().getWeight());
                    productEntity.setTreatment(
                            checkin.getItem().getProduct().getTreatment());
                    productEntity.setType(checkin.getItem().getProduct().getType());
                    productEntity.setLine(checkin.getItem().getProduct().getLine());
                    productEntity = realm.copyToRealmOrUpdate(productEntity);

                    itemEntity = new ItemEntity();
                    itemEntity.setItemId(checkin.getItem().getItemId());
                    itemEntity.setQuantity(checkin.getItem().getQuantity());
                    itemEntity.setWidth(checkin.getItem().getWidth());
                    itemEntity.setHeight(checkin.getItem().getHeight());
                    itemEntity.setWeight(checkin.getItem().getWeight());
                    itemEntity.setTreatment(checkin.getItem().getTreatment());
                    itemEntity.setProduct(productEntity);
                    itemEntity = realm.copyToRealmOrUpdate(itemEntity);
                } else {
                    productEntity.setProductId(
                            checkin.getOrderGlass().getProduct().getProductId());
                    productEntity.setCode(
                            checkin.getOrderGlass().getProduct().getCode());
                    productEntity.setDescription(
                            checkin.getOrderGlass().getProduct().getDescription());
                    productEntity.setWeight(
                            checkin.getOrderGlass().getProduct().getWeight());
                    productEntity.setTreatment(
                            checkin.getOrderGlass().getProduct().getTreatment());
                    productEntity.setType(checkin.getOrderGlass().getProduct().getType());
                    productEntity.setLine(checkin.getOrderGlass().getProduct().getLine());
                    productEntity = realm.copyToRealmOrUpdate(productEntity);

                    orderGlassEntity = new OrderGlassEntity();
                    orderGlassEntity.setOrderGlassId(checkin.getOrderGlass().getOrderGlassId());
                    orderGlassEntity.setQuantity(checkin.getOrderGlass().getQuantity());
                    orderGlassEntity.setNumber(checkin.getOrderGlass().getNumber());
                    orderGlassEntity.setColor(checkin.getOrderGlass().getColor());
                    orderGlassEntity.setWidth(checkin.getOrderGlass().getWidth());
                    orderGlassEntity.setHeight(checkin.getOrderGlass().getHeight());
                    orderGlassEntity.setWeight(checkin.getOrderGlass().getWeight());
                    orderGlassEntity.setProduct(productEntity);
                    orderGlassEntity = realm.copyToRealmOrUpdate(orderGlassEntity);
                }

                final CheckinEntity checkinEntity = new CheckinEntity();
                checkinEntity.setCheckinId(checkin.getCheckinId());
                checkinEntity.setFlowId(checkin.getFlowId());
                checkinEntity.setDate(checkin.getDate());
                checkinEntity.setStatus(checkin.getStatus());
                checkinEntity.setItem(itemEntity);
                checkinEntity.setOrderGlass(orderGlassEntity);
                checkinEntity.setLocation(checkin.getLocation());

                checkinEntityList.add(realm.copyToRealmOrUpdate(checkinEntity));
            }

            return new RealmList<>(checkinEntityList.toArray(
                    new CheckinEntity[checkinEntityList.size()]));
        }).map(checkinEntities -> {
            List<Checkin> list = new ArrayList<>(checkinEntities.size());
            for (CheckinEntity checkinEntity : checkinEntities) {
                list.add(checkinFromRealm(checkinEntity));
            }

            return list;
        });
    }

    @Override
    public Observable<Checkin> updateSyncState(final Checkin checkin, final boolean syncPending) {
        return RealmObservable.object(realm -> {
            CheckinEntity checkinEntity = realm.where(CheckinEntity.class)
                    .equalTo(CheckinEntity.FIELD_CHECKIN_ID, checkin.getCheckinId())
                    .findFirst();

            if (checkinEntity != null) {
                if (checkinEntity.getStatus() == Checkin.STATUS_PENDING
                        && checkin.getStatus() == Checkin.STATUS_FINISHED) {
                    checkinEntity.setStatus(checkin.getStatus());

                    if (checkin.getDate() != null) {
                        checkinEntity.setDate(checkin.getDate());
                    }

                    checkinEntity.setPendingSynchronization(syncPending);

                    checkinEntity = realm.copyToRealmOrUpdate(checkinEntity);
                }
            }

            return checkinEntity;
        }).map(this::checkinFromRealm);
    }

    @Override
    public Observable<List<Checkin>> updateSyncState(final List<Checkin> checkins,
            final boolean syncPending) {
        return RealmObservable.list(realm -> {
            List<CheckinEntity> checkinEntityList = new ArrayList<>(checkins.size());

            for(Checkin checkin : checkins) {
                CheckinEntity checkinEntity = realm.where(CheckinEntity.class)
                        .equalTo(CheckinEntity.FIELD_CHECKIN_ID, checkin.getCheckinId())
                        .findFirst();

                if (checkinEntity != null) {
                    if (checkinEntity.getStatus() == Checkin.STATUS_PENDING
                            && checkin.getStatus() == Checkin.STATUS_FINISHED) {
                        checkinEntity.setStatus(checkin.getStatus());

                        if (checkin.getDate() != null) {
                            checkinEntity.setDate(checkin.getDate());
                        }

                        checkinEntity.setPendingSynchronization(syncPending);

                        checkinEntityList.add(realm.copyToRealmOrUpdate(checkinEntity));
                    }
                }
            }

            return new RealmList<>(checkinEntityList.toArray(
                    new CheckinEntity[checkinEntityList.size()]));
        }).map(checkinEntities -> {
            List<Checkin> list = new ArrayList<>(checkinEntities.size());
            for (CheckinEntity checkinEntity : checkinEntities) {
                list.add(checkinFromRealm(checkinEntity));
            }

            return list;
        });
    }

    @Override
    public Observable<List<Checkin>> listPendingSynchronization() {
        return RealmObservable.results(realm -> {
            // find all pending synchronization
            return realm.where(CheckinEntity.class)
                    .equalTo(CheckinEntity.FIELD_PENDING_SYNCHRONIZATION, true)
                    .findAll();
        }).map(checkinEntities -> {
            // map them to UI objects
            final List<Checkin> checkinList = new ArrayList<>(checkinEntities.size());
            for (CheckinEntity checkinEntity : checkinEntities) {
                checkinList.add(checkinFromRealm(checkinEntity));
            }

            return checkinList;
        });
    }
}
