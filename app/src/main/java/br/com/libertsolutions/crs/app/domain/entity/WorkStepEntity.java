package br.com.libertsolutions.crs.app.domain.entity;

import android.support.annotation.IntRange;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class WorkStepEntity extends RealmObject {
    public static final int TYPE_PRODUCT = 0;
    public static final int TYPE_ORDER_GLASS = 1;

    @PrimaryKey
    private Long workStepId;

    private Integer order;

    private String name;

    private Integer type;

    private Integer goForward;

    public Long getWorkStepId() {
        return workStepId;
    }

    public void setWorkStepId(Long workStepId) {
        this.workStepId = workStepId;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(@IntRange(from = TYPE_PRODUCT, to = TYPE_ORDER_GLASS) Integer type) {
        this.type = type;
    }

    public Integer getGoForward() {
        return goForward;
    }

    public void setGoForward(@IntRange(from = 0, to = 1) Integer goForward) {
        this.goForward = goForward;
    }
}
