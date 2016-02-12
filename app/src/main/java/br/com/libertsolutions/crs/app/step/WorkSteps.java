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
                        .setOrder(1)
                        .setName("Fabricação")
                        .setKind(0)
                        .setGoForward(0),

                new WorkStep()
                        .setOrder(2)
                        .setName("Pedido Vidro")
                        .setKind(1)
                        .setGoForward(0),

                new WorkStep()
                        .setOrder(3)
                        .setName("Embalagem")
                        .setKind(0)
                        .setGoForward(0),

                new WorkStep()
                        .setOrder(4)
                        .setName("Entrega")
                        .setKind(0)
                        .setGoForward(0),

                new WorkStep()
                        .setOrder(5)
                        .setName("Liberação Obra")
                        .setKind(0)
                        .setGoForward(0),

                new WorkStep()
                        .setOrder(6)
                        .setName("Instalação")
                        .setKind(0)
                        .setGoForward(0)
        );

        DATA_SET = list;
    }

    public static List<WorkStep> getDataSet() {
        Collections.sort(DATA_SET);
        return DATA_SET;
    }
}
