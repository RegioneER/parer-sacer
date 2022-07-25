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

public class DtRegUnitaDocValidator implements ConstraintValidator<ValidateDtRegUnitaDoc, CriterioFiltroUnitaDoc> {

    @Override
    public void initialize(ValidateDtRegUnitaDoc constraintAnnotation) {
    }

    @Override
    public boolean isValid(CriterioFiltroUnitaDoc critFiltroUnitaDoc, ConstraintValidatorContext context) {

        DecCriterioRaggr criterio = critFiltroUnitaDoc.getCriterioRaggr();
        AroUnitaDoc unitaDoc = critFiltroUnitaDoc.getUnitaDoc();

        // valida filtro su data registrazione unita doc
        if ((criterio.getDtRegUnitaDocDa() != null && criterio.getDtRegUnitaDocA() != null
                && criterio.getDtRegUnitaDocDa().compareTo(unitaDoc.getDtRegUnitaDoc()) <= 0
                && criterio.getDtRegUnitaDocA().compareTo(unitaDoc.getDtRegUnitaDoc()) >= 0)) {
            return true;
        }
        if (criterio.getDtRegUnitaDocDa() == null && criterio.getDtRegUnitaDocA() == null) {
            return true;
        }

        return false;
    }
}
