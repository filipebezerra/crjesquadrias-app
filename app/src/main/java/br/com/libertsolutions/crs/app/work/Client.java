package br.com.libertsolutions.crs.app.work;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Objeto de transferência dos dados de Cliente. Representa o contratante do serviço da obra.
 * Esta classe é o modelo da camada da API.
 *
 * @author Filipe Bezerra
 * @version 0.2.0
 * @since 0.1.0
 */
public class Client implements Parcelable {
    @SerializedName("nome")
    private final String name;

    public Client(String name) {
        this.name = name;
    }

    protected Client(Parcel source) {
        name = source.readString();
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
    }

    public static final Creator<Client> CREATOR = new Creator<Client>() {
        public Client createFromParcel(Parcel source) {
            return new Client(source);
        }

        public Client[] newArray(int size) {
            return new Client[size];
        }
    };
}
