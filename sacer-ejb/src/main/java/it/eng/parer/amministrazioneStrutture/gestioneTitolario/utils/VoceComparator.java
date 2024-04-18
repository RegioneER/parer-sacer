/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.amministrazioneStrutture.gestioneTitolario.utils;

import it.eng.parer.titolario.xml.OperazioneCreaType;
import it.eng.parer.titolario.xml.OperazioneModificaType;
import it.eng.parer.titolario.xml.OperazioneType;
import java.util.Comparator;

/**
 *
 * @author Bonora_L
 */
public class VoceComparator implements Comparator<OperazioneType> {

    @Override
    public int compare(OperazioneType o1, OperazioneType o2) {
        String codiceVoceComposito1 = null;
        String codiceVoceComposito2 = null;
        if (o1 instanceof OperazioneCreaType) {
            codiceVoceComposito1 = ((OperazioneCreaType) o1).getCreaVoce().getCodiceVoceComposito();
        } else if (o1 instanceof OperazioneModificaType) {
            OperazioneModificaType tmpOp = ((OperazioneModificaType) o1);
            if (tmpOp.getCreaVoce() != null) {
                codiceVoceComposito1 = tmpOp.getCreaVoce().getCodiceVoceComposito();
            } else if (tmpOp.getModificaVoce() != null) {
                codiceVoceComposito1 = tmpOp.getModificaVoce().getCodiceVoceComposito();
            } else if (tmpOp.getChiudiVoce() != null) {
                codiceVoceComposito1 = tmpOp.getChiudiVoce().getCodiceVoceComposito();
            }
        }

        if (o2 instanceof OperazioneCreaType) {
            codiceVoceComposito2 = ((OperazioneCreaType) o2).getCreaVoce().getCodiceVoceComposito();
        } else if (o2 instanceof OperazioneModificaType) {
            OperazioneModificaType tmpOp = ((OperazioneModificaType) o2);
            if (tmpOp.getCreaVoce() != null) {
                codiceVoceComposito2 = tmpOp.getCreaVoce().getCodiceVoceComposito();
            } else if (tmpOp.getModificaVoce() != null) {
                codiceVoceComposito2 = tmpOp.getModificaVoce().getCodiceVoceComposito();
            } else if (tmpOp.getChiudiVoce() != null) {
                codiceVoceComposito2 = tmpOp.getChiudiVoce().getCodiceVoceComposito();
            }
        }

        if (codiceVoceComposito1 == null || codiceVoceComposito2 == null) {
            throw new RuntimeException(
                    "Errore inaspettato nel recupero del codice voce composito all'interno delle voci");
        }

        return codiceVoceComposito1.compareToIgnoreCase(codiceVoceComposito2);
    }

}
