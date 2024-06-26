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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.elencoVersamento.validation;

import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.DecCriterioRaggr;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author DiLorenzo_F
 */
public class FieldsUnitaDocValidator implements ConstraintValidator<ValidateFieldsUnitaDoc, CriterioFiltroUnitaDoc> {

    @Override
    public void initialize(ValidateFieldsUnitaDoc constraintAnnotation) {
    }

    @Override
    public boolean isValid(CriterioFiltroUnitaDoc critFiltroUnitaDoc, ConstraintValidatorContext context) {

        DecCriterioRaggr criterio = critFiltroUnitaDoc.getCriterioRaggr();
        AroUnitaDoc unitaDoc = critFiltroUnitaDoc.getUnitaDoc();

        // valido filtro su numero chiave
        if (criterio.getCdKeyUnitaDoc() != null && !criterio.getCdKeyUnitaDoc().equals(unitaDoc.getCdKeyUnitaDoc())) {
            return false;
        }

        // valido filtro su firmato
        if (criterio.getFlUnitaDocFirmato() != null
                && !criterio.getFlUnitaDocFirmato().equals(unitaDoc.getFlUnitaDocFirmato())) {
            return false;
        }

        // valida filtro su tipo conservazione
        if (criterio.getTiConservazione() != null
                && !criterio.getTiConservazione().equals(unitaDoc.getTiConservazione())) {
            return false;

        }

        // valida filtro su forza accettazione
        if (criterio.getFlForzaAccettazione() != null
                && !criterio.getFlForzaAccettazione().equals(unitaDoc.getFlForzaAccettazione())) {
            return false;

        }

        // valida filtro su forza conservazione
        if (criterio.getFlForzaConservazione() != null
                && !criterio.getFlForzaConservazione().equals(unitaDoc.getFlForzaConservazione())) {
            return false;
        }

        // valida filtro su oggetto unità doc
        if (criterio.getDlOggettoUnitaDoc() != null
                && !StringUtils.contains(unitaDoc.getDlOggettoUnitaDoc(), criterio.getDlOggettoUnitaDoc())) {
            return false;
        }

        return true;
    }
}
