package it.eng.parer.elencoVersamento.utils;

import java.util.Comparator;

/**
 *
 * @author gilioli_p
 */
public class DocUdObjComparatorDtCreazione implements Comparator<DocUdObj> {

    @Override
    public int compare(DocUdObj o1, DocUdObj o2) {
        if (o1.getDtCreazione().getTime() - o2.getDtCreazione().getTime() < 0) {
            return -1;
        } else if (o1.getDtCreazione().getTime() - o2.getDtCreazione().getTime() > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
