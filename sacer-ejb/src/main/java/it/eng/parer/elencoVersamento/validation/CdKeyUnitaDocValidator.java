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

public class CdKeyUnitaDocValidator implements ConstraintValidator<ValidateCdKeyUnitaDoc, CriterioFiltroUnitaDoc> {

    @Override
    public void initialize(ValidateCdKeyUnitaDoc constraintAnnotation) {
    }

    @Override
    public boolean isValid(CriterioFiltroUnitaDoc critFiltroUnitaDoc, ConstraintValidatorContext context) {

        DecCriterioRaggr criterio = critFiltroUnitaDoc.getCriterioRaggr();
        AroUnitaDoc unitaDoc = critFiltroUnitaDoc.getUnitaDoc();

        // valido filtro su range numero chiave
        if (criterio.getCdKeyUnitaDocDa() != null && criterio.getCdKeyUnitaDocA() != null
                && (StringUtils.leftPad(criterio.getCdKeyUnitaDocDa(), 12, "0")
                        .compareTo(StringUtils.leftPad(unitaDoc.getCdKeyUnitaDoc(), 12, "0")) <= 0
                        && StringUtils.leftPad(criterio.getCdKeyUnitaDocA(), 12, "0")
                                .compareTo(StringUtils.leftPad(unitaDoc.getCdKeyUnitaDoc(), 12, "0")) >= 0)) {
            return true;
        }
        if (criterio.getCdKeyUnitaDocDa() != null && criterio.getCdKeyUnitaDocA() == null
                && (StringUtils.leftPad(criterio.getCdKeyUnitaDocDa(), 12, "0")
                        .compareTo(StringUtils.leftPad(unitaDoc.getCdKeyUnitaDoc(), 12, "0")) <= 0
                        && "zzzzzzzzzzzz".compareTo(StringUtils.leftPad(unitaDoc.getCdKeyUnitaDoc(), 12, "0")) >= 0)) {
            return true;
        }
        if (criterio.getCdKeyUnitaDocDa() == null && criterio.getCdKeyUnitaDocA() != null
                && ("000000000000".compareTo(StringUtils.leftPad(unitaDoc.getCdKeyUnitaDoc(), 12, "0")) <= 0
                        && StringUtils.leftPad(criterio.getCdKeyUnitaDocA(), 12, "0")
                                .compareTo(StringUtils.leftPad(unitaDoc.getCdKeyUnitaDoc(), 12, "0")) >= 0)) {
            return true;
        }

        if (criterio.getCdKeyUnitaDocDa() == null && criterio.getCdKeyUnitaDocA() == null) {
            return true;
        }

        return false;
    }
}
