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
        DATA_SET = Arrays.asList(
                new WorkStep()
                        .setWorkStepId(1)
                        .setOrder(1)
                        .setName("Fabricação")
                        .setKind(0)
                        .setGoForward(0),

                new WorkStep()
                        .setWorkStepId(2)
                        .setOrder(2)
                        .setName("Pedido Vidro")
                        .setKind(1)
                        .setGoForward(0),

                new WorkStep()
                        .setWorkStepId(3)
                        .setOrder(3)
                        .setName("Embalagem")
                        .setKind(0)
                        .setGoForward(0),

                new WorkStep()
                        .setWorkStepId(4)
                        .setOrder(4)
                        .setName("Entrega")
                        .setKind(0)
                        .setGoForward(0),

                new WorkStep()
                        .setWorkStepId(5)
                        .setOrder(5)
                        .setName("Liberação Obra")
                        .setKind(0)
                        .setGoForward(0),

                new WorkStep()
                        .setWorkStepId(6)
                        .setOrder(6)
                        .setName("Instalação")
                        .setKind(0)
                        .setGoForward(0)
        );
    }

    public static List<WorkStep> getDataSet() {
        Collections.sort(DATA_SET);
        return DATA_SET;
    }
}
