/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.elencoVersamento.validation;

import it.eng.parer.entity.DecCriterioRaggr;
import it.eng.parer.web.util.Constants;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@SuppressWarnings("rawtypes")
public class AaKeyUnitaDocValidator
	implements ConstraintValidator<ValidateAaKeyUnitaDoc, CriterioRaggrValidation> {

    @Override
    public void initialize(ValidateAaKeyUnitaDoc constraintAnnotation) {
    }

    @Override
    public boolean isValid(CriterioRaggrValidation criterioRaggr,
	    ConstraintValidatorContext context) {

	DecCriterioRaggr criterio = criterioRaggr.getCriterioRaggr();
	BigDecimal aaKeyUnitaDoc = criterioRaggr.getAaKeyUnitaDoc(); // TODO: verificare se va bene
								     // per ud, doc e upd

	LocalDate currentDate = LocalDate.now();

	// valido filtro su range anno chiave
	if (criterio.getAaKeyUnitaDoc() != null
		&& !criterio.getAaKeyUnitaDoc().equals(aaKeyUnitaDoc)) {
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
