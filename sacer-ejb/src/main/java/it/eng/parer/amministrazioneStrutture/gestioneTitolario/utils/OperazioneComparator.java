package it.eng.parer.amministrazioneStrutture.gestioneTitolario.utils;

import it.eng.parer.titolario.xml.OperazioneCreaType;
import it.eng.parer.titolario.xml.OperazioneModificaType;
import it.eng.parer.titolario.xml.OperazioneType;
import it.eng.parer.amministrazioneStrutture.gestioneTitolario.dto.Voce;
import java.util.Comparator;

/**
 *
 * @author Bonora_L
 */
public class OperazioneComparator implements Comparator<OperazioneType> {

    @Override
    public int compare(OperazioneType o1, OperazioneType o2) {
        String codiceVoceComposito1 = null;
        String codiceVoceComposito2 = null;
        Integer comparatorValue1 = null;
        Integer comparatorValue2 = null;
        if (o1 instanceof OperazioneCreaType) {
            codiceVoceComposito1 = ((OperazioneCreaType) o1).getCreaVoce().getCodiceVoceComposito();
            comparatorValue1 = Voce.Operation.CREA.getComparatorValue();
        } else if (o1 instanceof OperazioneModificaType) {
            OperazioneModificaType tmpOp = ((OperazioneModificaType) o1);
            if (tmpOp.getCreaVoce() != null) {
                codiceVoceComposito1 = tmpOp.getCreaVoce().getCodiceVoceComposito();
                comparatorValue1 = Voce.Operation.CREA.getComparatorValue();
            } else if (tmpOp.getModificaVoce() != null) {
                codiceVoceComposito1 = tmpOp.getModificaVoce().getCodiceVoceComposito();
                comparatorValue1 = Voce.Operation.MODIFICA.getComparatorValue();
            } else if (tmpOp.getChiudiVoce() != null) {
                codiceVoceComposito1 = tmpOp.getChiudiVoce().getCodiceVoceComposito();
                comparatorValue1 = Voce.Operation.CHIUDI.getComparatorValue();
            }
        }

        if (o2 instanceof OperazioneCreaType) {
            codiceVoceComposito2 = ((OperazioneCreaType) o2).getCreaVoce().getCodiceVoceComposito();
            comparatorValue2 = Voce.Operation.CREA.getComparatorValue();
        } else if (o2 instanceof OperazioneModificaType) {
            OperazioneModificaType tmpOp = ((OperazioneModificaType) o2);
            if (tmpOp.getCreaVoce() != null) {
                codiceVoceComposito2 = tmpOp.getCreaVoce().getCodiceVoceComposito();
                comparatorValue2 = Voce.Operation.CREA.getComparatorValue();
            } else if (tmpOp.getModificaVoce() != null) {
                codiceVoceComposito2 = tmpOp.getModificaVoce().getCodiceVoceComposito();
                comparatorValue2 = Voce.Operation.MODIFICA.getComparatorValue();
            } else if (tmpOp.getChiudiVoce() != null) {
                codiceVoceComposito2 = tmpOp.getChiudiVoce().getCodiceVoceComposito();
                comparatorValue2 = Voce.Operation.CHIUDI.getComparatorValue();
            }
        }

        if (codiceVoceComposito1 == null || codiceVoceComposito2 == null || comparatorValue1 == null
                || comparatorValue2 == null) {
            throw new RuntimeException(
                    "Errore inaspettato nel recupero del codice voce composito all'interno delle voci");
        }

        int ret = comparatorValue1 - comparatorValue2;

        if (ret == 0) {
            ret = codiceVoceComposito1.compareToIgnoreCase(codiceVoceComposito2);
        }

        return ret;
    }

}
