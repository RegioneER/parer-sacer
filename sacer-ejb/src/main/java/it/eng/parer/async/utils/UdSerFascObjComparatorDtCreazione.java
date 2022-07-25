package it.eng.parer.async.utils;

import java.util.Comparator;

/**
 *
 * @author DiLorenzo_F
 */
public class UdSerFascObjComparatorDtCreazione implements Comparator<UdSerFascObj> {

    @Override
    public int compare(UdSerFascObj o1, UdSerFascObj o2) {
        /* Tip: Per ottimizzare l'estrazione bisogna ordinare il result set. Per ora gestisco i file in ordine sparso */
        if (o1.getTiEntitaSacer().equals(o2.getTiEntitaSacer())) {
            // if (o1.getDtCreazione().getTime() - o2.getDtCreazione().getTime() < 0) {
            // return -1;
            // } else if (o1.getDtCreazione().getTime() - o2.getDtCreazione().getTime() > 0) {
            // return 1;
            // } else {
            return 0;
            // }
        } else
            return 0;
    }
}
