package it.eng.parer.elencoVersFascicoli.utils;

import java.util.Comparator;

/**
 *
 * @author DiLorenzo_F
 */
public class FasFascicoloObjComparatorTsVersFascicolo implements Comparator<FasFascicoloObj> {

    @Override
    public int compare(FasFascicoloObj o1, FasFascicoloObj o2) {
        if (o1.getTsVersFascicolo().getTime() - o2.getTsVersFascicolo().getTime() < 0) {
            return -1;
        } else if (o1.getTsVersFascicolo().getTime() - o2.getTsVersFascicolo().getTime() > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
