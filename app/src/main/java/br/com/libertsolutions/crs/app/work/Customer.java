package br.com.libertsolutions.crs.app.work;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * Entidade Cliente, representa o contratante do serviço da obra.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 03/03/2016
 * @since 0.1.0
 * @see Work
 */
@ParcelablePlease
public class Customer implements Parcelable {
    @SerializedName("nome")
    String mNome;

    public String getNome() {
        return mNome;
    }

    public Customer setNome(String nome) {
        mNome = nome;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        CustomerParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<Customer> CREATOR = new Creator<Customer>() {
        public Customer createFromParcel(Parcel source) {
            Customer target = new Customer();
            CustomerParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };
}
