package br.com.libertsolutions.crs.app.domain.rule;

import br.com.libertsolutions.crs.app.domain.pojo.Checkin;
import br.com.libertsolutions.crs.app.domain.pojo.Product;
import java.util.Comparator;

/**
 * @author Filipe Bezerra
 * @since 0.1.0
 */
public class CheckinComparator implements Comparator<Checkin> {
    private boolean isDigit(char ch) {
        return ch >= 48 && ch <= 57;
    }

    /** Length of string is passed in for improved efficiency (only need to calculate it once) **/
    private String getChunk(String s, int slength, int marker) {
        StringBuilder chunk = new StringBuilder();
        char c = s.charAt(marker);
        chunk.append(c);
        marker++;
        if (isDigit(c)) {
            while (marker < slength) {
                c = s.charAt(marker);
                if (!isDigit(c)) {
                    break;
                }
                chunk.append(c);
                marker++;
            }
        } else {
            while (marker < slength) {
                c = s.charAt(marker);
                if (isDigit(c)) {
                    break;
                }
                chunk.append(c);
                marker++;
            }
        }
        return chunk.toString();
    }

    @Override
    public int compare(Checkin checkin1, Checkin checkin2) {
        Product product;
        Product anotherProduct;

        if (checkin1.getOrderGlass() != null && checkin2.getOrderGlass() != null) {
            product = checkin1.getOrderGlass().getProduct();
            anotherProduct = checkin2.getOrderGlass().getProduct();

            return product.getDescription().compareToIgnoreCase(anotherProduct.getDescription());
        } else {
            product = checkin1.getItem().getProduct();
            anotherProduct = checkin2.getItem().getProduct();

            final String s1 = product.getType();
            final String s2 = anotherProduct.getType();

            int thisMarker = 0;
            int thatMarker = 0;
            int s1Length = s1.length();
            int s2Length = s2.length();

            while (thisMarker < s1Length && thatMarker < s2Length) {
                String thisChunk = getChunk(s1, s1Length, thisMarker);
                thisMarker += thisChunk.length();

                String thatChunk = getChunk(s2, s2Length, thatMarker);
                thatMarker += thatChunk.length();

                // If both chunks contain numeric characters, sort them numerically
                int result;
                if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0))) {
                    // Simple chunk comparison by length.
                    int thisChunkLength = thisChunk.length();
                    result = thisChunkLength - thatChunk.length();
                    // If equal, the first different number counts
                    if (result == 0) {
                        for (int i = 0; i < thisChunkLength; i++) {
                            result = thisChunk.charAt(i) - thatChunk.charAt(i);
                            if (result != 0) {
                                return result;
                            }
                        }
                    }
                } else {
                    result = thisChunk.compareTo(thatChunk);
                }

                if (result != 0) {
                    return result;
                }
            }

            if (checkin1.getLocation() != null) {
                return checkin1.getLocation().compareToIgnoreCase(checkin2.getLocation());
            } else {
                return s1Length - s2Length;
            }
        }
    }
}
