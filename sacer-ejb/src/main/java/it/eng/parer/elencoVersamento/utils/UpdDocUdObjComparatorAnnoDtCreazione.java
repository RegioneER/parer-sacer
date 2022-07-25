package it.eng.parer.elencoVersamento.utils;

/**
 *
 * @author DiLorenzo_F
 */
public class UpdDocUdObjComparatorAnnoDtCreazione extends UpdDocUdObjComparatorDtCreazione {

    @Override
    public int compare(UpdDocUdObj o1, UpdDocUdObj o2) {
        if (o1.getTiEntitaSacer().equals(o2.getTiEntitaSacer())) {
            if (o1.getAaKeyUnitaDoc().intValue() > o2.getAaKeyUnitaDoc().intValue()) {
                return 1;
            } else if (o1.getAaKeyUnitaDoc().intValue() == o2.getAaKeyUnitaDoc().intValue()) { // A PARITA' DI ANNO,
                                                                                               // ORDINO PER DATA
                                                                                               // CREAZIONE
                return super.compare(o1, o2);
            } else {
                return -1;
            }
        } else
            return 0;
    }
}
