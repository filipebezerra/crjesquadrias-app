package br.com.libertsolutions.crs.app.checkin;

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
 * @version 0.1.0, 28/03/2016
 * @since 0.1.0
 */
public class CheckinRealmDataService implements CheckinDataService {
    private final Context mContext;

    public CheckinRealmDataService(Context context) {
        mContext = context;
    }

    @Override
    public Observable<List<Checkin>> list(final long flowId) {
        return RealmObservable.results(mContext, new Func1<Realm, RealmResults<CheckinEntity>>() {
            @Override
            public RealmResults<CheckinEntity> call(Realm realm) {
                // find all
                return realm.where(CheckinEntity.class)
                        .equalTo(CheckinEntity.FIELD_FLOW_ID, flowId)
                        .findAll();
            }
        }).map(new Func1<RealmResults<CheckinEntity>, List<Checkin>>() {
            @Override
            public List<Checkin> call(RealmResults<CheckinEntity> checkinEntities) {
                // map them to UI objects
                final List<Checkin> checkinList = new ArrayList<>(checkinEntities.size());
                for (CheckinEntity checkinEntity : checkinEntities) {
                    checkinList.add(checkinFromRealm(checkinEntity));
                }

                return checkinList;
            }
        });
    }

    @Override
    public Observable<List<Checkin>> saveAll(final List<Checkin> checkinList) {
        return RealmObservable.list(mContext, new Func1<Realm, RealmList<CheckinEntity>>() {
            @Override
            public RealmList<CheckinEntity> call(Realm realm) {
                List<CheckinEntity> checkinEntityList = new ArrayList<>(checkinList.size());

                for(Checkin checkin : checkinList) {
                    ItemEntity itemEntity = null;
                    OrderGlassEntity orderGlassEntity = null;
                    ProductEntity productEntity = new ProductEntity();

                    if (checkin.getItem() != null) {
                        productEntity.setProductId(checkin.getItem().getProduct().getProductId());
                        productEntity.setCode(checkin.getItem().getProduct().getCode());
                        productEntity.setDescription(checkin.getItem().getProduct().getDescription());
                        productEntity.setWeight(checkin.getItem().getProduct().getWeight());
                        productEntity.setTreatment(checkin.getItem().getProduct().getTreatment());
                        productEntity.setType(checkin.getItem().getProduct().getType());
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
                        productEntity.setProductId(checkin.getOrderGlass().getProduct().getProductId());
                        productEntity.setCode(checkin.getOrderGlass().getProduct().getCode());
                        productEntity.setDescription(checkin.getOrderGlass().getProduct().getDescription());
                        productEntity.setWeight(checkin.getOrderGlass().getProduct().getWeight());
                        productEntity.setTreatment(checkin.getOrderGlass().getProduct().getTreatment());
                        productEntity.setType(checkin.getOrderGlass().getProduct().getType());
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

                    checkinEntityList.add(realm.copyToRealmOrUpdate(checkinEntity));
                }

                return new RealmList<>(checkinEntityList.toArray(
                        new CheckinEntity[checkinEntityList.size()]));
            }
        }).map(new Func1<RealmList<CheckinEntity>, List<Checkin>>() {
            @Override
            public List<Checkin> call(RealmList<CheckinEntity> checkinEntities) {
                List<Checkin> list = new ArrayList<>(checkinEntities.size());
                for (CheckinEntity checkinEntity : checkinEntities) {
                    list.add(checkinFromRealm(checkinEntity));
                }

                return list;
            }
        });
    }

    private Checkin checkinFromRealm(CheckinEntity workEntity) {
        final Long checkinId = workEntity.getCheckinId();
        final Long flowId = workEntity.getFlowId();
        final String date = workEntity.getDate();
        final Integer status = workEntity.getStatus();
        final ItemEntity item = workEntity.getItem();
        final OrderGlassEntity orderGlass = workEntity.getOrderGlass();

        return new Checkin(checkinId, flowId, date, status, itemFromRealm(item),
                orderGlassFromRealm(orderGlass));
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

        return new Product(productId, code, description, weight, treatment, type);
    }
}
