package br.com.libertsolutions.crs.app.work;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Entidade Cliente, representa o contratante do serviço da obra. Esta classe é o modelo
 * de persistência local.
 *
 * @author Kirill Boyarshinov
 * @version 0.1.0, 20/03/2016
 * @since 0.1.0
 */
public class ClientEntity extends RealmObject {
    @PrimaryKey
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof ClientEntity) {
            final ClientEntity anotherClient = (ClientEntity) o;
            return getName().equalsIgnoreCase(anotherClient.getName());
        }
        return true;
    }
}
