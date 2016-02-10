package br.com.libertsolutions.crs.app.work;

/**
 * Entity project abstraction.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 10/02/2016
 * @since 0.1.0
 */
public class Work {
    String mWorkId;

    String customerName;

    long mDeliveryDate;

    public String getWorkId() {
        return mWorkId;
    }

    public Work setWorkId(String workId) {
        this.mWorkId = workId;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Work setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public long getDeliveryDate() {
        return mDeliveryDate;
    }

    public Work setDeliveryDate(long deliveryDate) {
        this.mDeliveryDate = deliveryDate;
        return this;
    }
}
