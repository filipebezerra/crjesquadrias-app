package br.com.libertsolutions.crs.app.work;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Objeto de transferência dos dados de Obra. Representa os projetos de construção ou reforma
 * dos interiores de um imóvel. Esta classe é o modelo da camada da API
 *
 * @author Filipe Bezerra
 * @version 0.2.0
 * @since 0.1.0
 */
public class Work implements Parcelable {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_STARTED = 1;

    @SerializedName("id_obra")
    private final long workId;

    @SerializedName("Cliente")
    private final Client client;

    @SerializedName("codigo")
    private final String code;

    @SerializedName("data")
    private final String date;

    @SerializedName("obra")
    private final String job;

    @SerializedName("status")
    private final int status;

    public Work(long id, Client client, String code, String date, String job, int status) {
        this.workId = id;
        this.client = client;
        this.code = code;
        this.date = date;
        this.job = job;
        this.status = status;
    }

    protected Work(Parcel source) {
        workId = source.readLong();
        client = source.readParcelable(getClass().getClassLoader());
        code = source.readString();
        date = source.readString();
        job = source.readString();
        status = source.readInt();
    }

    public long getWorkId() {
        return workId;
    }

    public Client getClient() {
        return client;
    }

    public String getCode() {
        return code;
    }

    public String getDate() {
        return date;
    }

    public String getJob() {
        return job;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(workId);
        out.writeParcelable(client, 0);
        out.writeString(code);
        out.writeString(date);
        out.writeString(job);
        out.writeInt(status);
    }

    public static final Creator<Work> CREATOR = new Creator<Work>() {
        public Work createFromParcel(Parcel source) {
            return new Work(source);
        }

        public Work[] newArray(int size) {
            return new Work[size];
        }
    };
}
