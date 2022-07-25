/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.elencoVersamento.validation;

import it.eng.parer.entity.DecCriterioRaggr;
import it.eng.parer.web.util.Constants;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AaKeyUnitaDocValidator implements ConstraintValidator<ValidateAaKeyUnitaDoc, CriterioRaggrValidation> {

    @Override
    public void initialize(ValidateAaKeyUnitaDoc constraintAnnotation) {
    }

    @Override
    public boolean isValid(CriterioRaggrValidation criterioRaggr, ConstraintValidatorContext context) {

        DecCriterioRaggr criterio = criterioRaggr.getCriterioRaggr();
        BigDecimal aaKeyUnitaDoc = criterioRaggr.getAaKeyUnitaDoc(); // TODO: verificare se va bene per ud, doc e upd

        LocalDate currentDate = LocalDate.now();

        // valido filtro su range anno chiave
        if (criterio.getAaKeyUnitaDoc() != null && !criterio.getAaKeyUnitaDoc().equals(aaKeyUnitaDoc)) {
            return false;
        }
        if (criterio.getAaKeyUnitaDocDa() != null && criterio.getAaKeyUnitaDocA() != null
                && criterio.getAaKeyUnitaDocDa().compareTo(aaKeyUnitaDoc) > 0
                && criterio.getAaKeyUnitaDocA().compareTo(aaKeyUnitaDoc) < 0) {
            return false;
        }
        if (criterio.getAaKeyUnitaDocDa() != null && criterio.getAaKeyUnitaDocA() == null
                && criterio.getAaKeyUnitaDocDa().compareTo(aaKeyUnitaDoc) > 0
                && currentDate.getYear() < aaKeyUnitaDoc.intValue()) {
            return false;
        }
        if (criterio.getAaKeyUnitaDocDa() == null && criterio.getAaKeyUnitaDocA() != null
                && new BigDecimal(Constants.START_MILLENNIUM_YYYY).compareTo(aaKeyUnitaDoc) > 0
                && criterio.getAaKeyUnitaDocA().compareTo(aaKeyUnitaDoc) < 0) {
            return false;
        }

        return true;
    }
}
