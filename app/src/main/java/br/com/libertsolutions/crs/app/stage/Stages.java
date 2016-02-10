package br.com.libertsolutions.crs.app.stage;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 10/02/2016
 * @since #
 */
public final class Stages {
    private static final List<Stage> DATA_SET;

    static {
        List<Stage> list = Arrays.asList(
                new Stage()
                        .setName("Liberação Fabricação"),

                new Stage()
                        .setName("Liberação Compra Vidro"),

                new Stage()
                        .setName("Entrega dos Vidros"),

                new Stage()
                        .setName("Expedição/Checagem"),

                new Stage()
                        .setName("Embalagem"),

                new Stage()
                        .setName("Liberou Obra"),

                new Stage()
                        .setName("Entrega"),

                new Stage()
                        .setName("Instalação")
        );

        DATA_SET = Collections.unmodifiableList(list);
    }

    public static List<Stage> getDataSet() {
        return DATA_SET;
    }
}
