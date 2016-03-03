package br.com.libertsolutions.crs.app.work;

import com.google.gson.annotations.SerializedName;

/**
 * Entidade Obra, representa os projetos de construção ou reforma dos interiores
 * de um imóvel.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 27/02/2016
 * @since 0.1.0
 * @see br.com.libertsolutions.crs.app.step.WorkStep
 */
public class Work {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_STARTED = 1;

    @SerializedName("Cliente")
    Customer mCustomer;

    @SerializedName("idObra")
    Long mWorkdId;

    @SerializedName("codigo")
    String mCode;

    @SerializedName("data")
    String mDate;

    @SerializedName("obra")
    String mJob;

    Integer mStatus;

    public Customer getCustomer() {
        return mCustomer;
    }

    public Work setCustomer(Customer customer) {
        this.mCustomer = customer;
        return this;
    }

    public Long getWorkdId() {
        return mWorkdId;
    }

    public Work setWorkdId(Long workdId) {
        mWorkdId = workdId;
        return this;
    }

    public String getCode() {
        return mCode;
    }

    public Work setCode(String code) {
        this.mCode = code;
        return this;
    }

    public String getDate() {
        return mDate;
    }

    public Work setDate(String date) {
        this.mDate = date;
        return this;
    }

    public String getJob() {
        return mJob;
    }

    public Work setJob(String job) {
        mJob = job;
        return this;
    }

    public Integer getStatus() {
        return mStatus;
    }

    public Work setStatus(Integer status) {
        mStatus = status;
        return this;
    }
}
