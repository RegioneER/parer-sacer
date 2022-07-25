package it.eng.parer.elencoVersFascicoli.utils;

/**
 *
 * @author DiLorenzo_F
 */
public class FasFascicoloObjComparatorAnnoTsVersFascicolo extends FasFascicoloObjComparatorTsVersFascicolo {

    @Override
    public int compare(FasFascicoloObj o1, FasFascicoloObj o2) {
        if (o1.getAaFascicolo().intValue() > o2.getAaFascicolo().intValue()) {
            return 1;
        } else if (o1.getAaFascicolo().intValue() == o2.getAaFascicolo().intValue()) { // A PARITA' DI ANNO, ORDINO PER
                                                                                       // DATA CREAZIONE
            return super.compare(o1, o2);
        } else {
            return -1;
        }
    }
}
