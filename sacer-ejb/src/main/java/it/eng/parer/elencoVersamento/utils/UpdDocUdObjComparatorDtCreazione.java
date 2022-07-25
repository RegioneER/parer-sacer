package it.eng.parer.elencoVersamento.utils;

import java.util.Comparator;

/**
 *
 * @author DiLorenzo_F
 */
public class UpdDocUdObjComparatorDtCreazione implements Comparator<UpdDocUdObj> {

    @Override
    public int compare(UpdDocUdObj o1, UpdDocUdObj o2) {
        if (o1.getTiEntitaSacer().equals(o2.getTiEntitaSacer())) {
            if (o1.getDtCreazione().getTime() - o2.getDtCreazione().getTime() < 0) {
                return -1;
            } else if (o1.getDtCreazione().getTime() - o2.getDtCreazione().getTime() > 0) {
                return 1;
            } else {
                return 0;
            }
        } else
            return 0;
    }
}
