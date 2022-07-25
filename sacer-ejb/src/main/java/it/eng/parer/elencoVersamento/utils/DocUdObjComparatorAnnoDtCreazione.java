package it.eng.parer.elencoVersamento.utils;

/**
 *
 * @author gilioli_p
 */
public class DocUdObjComparatorAnnoDtCreazione extends DocUdObjComparatorDtCreazione {

    @Override
    public int compare(DocUdObj o1, DocUdObj o2) {
        if (o1.getAaKeyUnitaDoc().intValue() > o2.getAaKeyUnitaDoc().intValue()) {
            return 1;
        } else if (o1.getAaKeyUnitaDoc().intValue() == o2.getAaKeyUnitaDoc().intValue()) { // A PARITA' DI ANNO, ORDINO
                                                                                           // PER DATA CREAZIONE
            return super.compare(o1, o2);
        } else {
            return -1;
        }
    }
}
