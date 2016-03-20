package br.com.libertsolutions.crs.app.work;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Entidade Obra, representa os projetos de construção ou reforma dos interiores de um imóvel.
 * Esta classe é o modelo de persistência local.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 20/03/2016
 * @since 0.1.0
 */
public class WorkEntity extends RealmObject {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_STARTED = 1;

    @PrimaryKey
    private Long workId;

    private ClientEntity client;

    private String code;

    private String date;

    private String job;

    private Integer status;

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof WorkEntity) {
            final WorkEntity anotherWork = (WorkEntity) o;
            return getWorkId().compareTo(anotherWork.getWorkId()) == 0;
        }
        return false;
    }
}
