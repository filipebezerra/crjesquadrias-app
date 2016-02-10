package br.com.libertsolutions.crs.app.step;

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
public final class WorkSteps {
    private static final List<WorkStep> DATA_SET;

    static {
        List<WorkStep> list = Arrays.asList(
                new WorkStep()
                        .setName("Liberação Fabricação"),

                new WorkStep()
                        .setName("Liberação Compra Vidro"),

                new WorkStep()
                        .setName("Entrega dos Vidros"),

                new WorkStep()
                        .setName("Expedição/Checagem"),

                new WorkStep()
                        .setName("Embalagem"),

                new WorkStep()
                        .setName("Liberou Obra"),

                new WorkStep()
                        .setName("Entrega"),

                new WorkStep()
                        .setName("Instalação")
        );

        DATA_SET = Collections.unmodifiableList(list);
    }

    public static List<WorkStep> getDataSet() {
        return DATA_SET;
    }
}
