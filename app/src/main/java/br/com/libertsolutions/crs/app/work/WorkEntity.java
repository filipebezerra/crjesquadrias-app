package br.com.libertsolutions.crs.app.work;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Obra, representa os projetos de construção ou reforma dos interiores de um imóvel.
 * Esta classe é o modelo de persistência local.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 18/03/2016
 * @since 0.1.0
 */
public class WorkEntity {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_STARTED = 1;

    private Long mWorkId;

    private ClientEntity mClient;

    private String mCode;

    private String mDate;

    private String mJob;

    private Integer mStatus;

    public static List<WorkEntity> of(List<Work> works) {
        final List<WorkEntity> list = new ArrayList<>();

        for (Work work : works) {
            WorkEntity workEntity = new WorkEntity();
            workEntity.setWorkId(work.id);
            workEntity.setClient(ClientEntity.of(work.client));
            workEntity.setCode(work.code);
            workEntity.setDate(work.date);
            workEntity.setJob(work.job);
            workEntity.setStatus(work.status);

            list.add(workEntity);
        }

        return list;
    }

    public Long getWorkId() {
        return mWorkId;
    }

    public void setWorkId(Long workId) {
        this.mWorkId = workId;
    }

    public ClientEntity getClient() {
        return mClient;
    }

    public void setClient(ClientEntity client) {
        this.mClient = client;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        this.mCode = code;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public String getJob() {
        return mJob;
    }

    public void setJob(String job) {
        this.mJob = job;
    }

    public Integer getStatus() {
        return mStatus;
    }

    public void setStatus(Integer status) {
        this.mStatus = status;
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
