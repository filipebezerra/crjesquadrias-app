package br.com.libertsolutions.crs.app.work;

/**
 * Entidade Cliente, representa o contratante do serviço da obra. Esta classe é o modelo
 * de persistência local.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 18/03/2016
 * @since 0.1.0
 */
public class ClientEntity {
    private String mName;

    public static ClientEntity of(Client client) {
        final ClientEntity clientEntity = new ClientEntity();
        clientEntity.setName(client.name);
        return clientEntity;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
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
