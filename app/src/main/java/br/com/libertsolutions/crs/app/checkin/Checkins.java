package br.com.libertsolutions.crs.app.checkin;

import android.support.annotation.IntRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 13/02/2016
 * @since 0.1.0
 */
public final class Checkins {
    private static final List<Checkin> DATA_SET;

    static {
        List<Checkin> list = Arrays.asList(
                new Checkin()
                        .setCheckinId(164)
                        .setStatus(Checkin.STATUS_PENDING)
                        .setItem(
                                new Item()
                                        .setItemId(22)
                                        .setQuantity(1)
                                        .setWidth(4500)
                                        .setHeight(2400)
                                        .setWeight(81.259f)
                                        .setTreatment(null)
                                        .setProduct(
                                                new Product()
                                                        .setProductId(26)
                                                        .setCode("GOL-PC40000")
                                                        .setDescription(
                                                                "PORTA DE CORRER 4 FOLHAS - VIDRO TEMPERADO 6MM INCOLOR - FECHO CONCHA COM CHAVE")
                                                        .setWeight(81259)
                                                        .setTreatment(
                                                                "PINTURA BRANCO BRILHANTE - RAL9003B")
                                                        .setType("C3")
                                        )
                        )
                        .setOrderGlass(null),

                new Checkin()
                        .setCheckinId(165)
                        .setStatus(Checkin.STATUS_PENDING)
                        .setItem(
                                new Item()
                                        .setItemId(23)
                                        .setQuantity(1)
                                        .setWidth(4200)
                                        .setHeight(2400)
                                        .setWeight(78.654f)
                                        .setTreatment(null)
                                        .setProduct(
                                                new Product()
                                                        .setProductId(27)
                                                        .setCode("GOL-PC40000")
                                                        .setDescription(
                                                                "PORTA DE CORRER 4 FOLHAS - VIDRO TEMPERADO 6MM INCOLOR - FECHO CONCHA COM CHAVE")
                                                        .setWeight(81259)
                                                        .setTreatment(
                                                                "PINTURA BRANCO BRILHANTE - RAL9003B")
                                                        .setType("C4")
                                        )
                        )
                        .setOrderGlass(null),

                new Checkin()
                        .setCheckinId(166)
                        .setStatus(Checkin.STATUS_PENDING)
                        .setItem(
                                new Item()
                                        .setItemId(24)
                                        .setQuantity(1)
                                        .setWidth(7500)
                                        .setHeight(2400)
                                        .setWeight(112.815f)
                                        .setTreatment(null)
                                        .setProduct(
                                                new Product()
                                                        .setProductId(28)
                                                        .setCode("GOL-PC60000")
                                                        .setDescription(
                                                                "PORTA DE CORRER 6 FOLHAS - VIDRO TEMPERADO 6MM INCOLOR - FECHO CONCHA COM CHAVE")
                                                        .setWeight(112815)
                                                        .setTreatment(
                                                                "PINTURA BRANCO BRILHANTE - RAL9003B")
                                                        .setType("C5")
                                        )
                        )
                        .setOrderGlass(null),

                new Checkin()
                        .setCheckinId(192)
                        .setStatus(Checkin.STATUS_PENDING)
                        .setItem(null)
                        .setOrderGlass(
                                new OrderGlass()
                                        .setOrderGlassId(13)
                                        .setQuantity(3)
                                        .setNumber(null)
                                        .setColor("INCOLOR")
                                        .setWidth(1106)
                                        .setHeight(2224)
                                        .setWeight(0)
                                        .setProduct(
                                                new Product()
                                                        .setProductId(31)
                                                        .setCode("V-TEMP-06")
                                                        .setDescription("Temperado de 6 mm")
                                                        .setWeight(0)
                                                        .setTreatment(null)
                                                        .setType(null)
                                        )
                        ),

                new Checkin()
                        .setCheckinId(206)
                        .setStatus(Checkin.STATUS_PENDING)
                        .setItem(null)
                        .setOrderGlass(
                                new OrderGlass()
                                        .setOrderGlassId(19)
                                        .setQuantity(2)
                                        .setNumber(null)
                                        .setColor("INCOLOR")
                                        .setWidth(455)
                                        .setHeight(299)
                                        .setWeight(0)
                                        .setProduct(
                                                new Product()
                                                        .setProductId(41)
                                                        .setCode("V-MINB-04")
                                                        .setDescription("Mineboreal de 4 mm")
                                                        .setWeight(0)
                                                        .setTreatment(null)
                                                        .setType(null)
                                        )
                        ),

                new Checkin()
                        .setCheckinId(209   )
                        .setStatus(Checkin.STATUS_PENDING)
                        .setItem(null)
                        .setOrderGlass(
                                new OrderGlass()
                                        .setOrderGlassId(21)
                                        .setQuantity(1)
                                        .setNumber(null)
                                        .setColor("INCOLOR")
                                        .setWidth(1917)
                                        .setHeight(980)
                                        .setWeight(0)
                                        .setProduct(
                                                new Product()
                                                        .setProductId(47)
                                                        .setCode("VLC-06_3_3")
                                                        .setDescription("Laminado 06mm (3+3) com PVB colorido")
                                                        .setWeight(0)
                                                        .setTreatment(null)
                                                        .setType(null)
                                        )
                        )
        );

        DATA_SET = Collections.unmodifiableList(list);
    }

    public static List<Checkin> getDataSet(@IntRange(from = 0, to = 1) int type) {
        List<Checkin> list = new ArrayList<>();
        for(Checkin checkin : DATA_SET) {
            switch (type) {
                case 0:
                    if (checkin.getItem() != null) {
                        list.add(checkin);
                    }
                    continue;

                case 1:
                    if (checkin.getOrderGlass() != null) {
                        list.add(checkin);
                    }
            }
        }
        return list;
    }
}
