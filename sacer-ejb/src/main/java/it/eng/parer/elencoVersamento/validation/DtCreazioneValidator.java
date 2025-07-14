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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;

@SuppressWarnings("rawtypes")
public class DtCreazioneValidator
	implements ConstraintValidator<ValidateDtCreazione, CriterioRaggrValidation> {

    private static final String DESC_CONSUMER = "Consumer coda degli elenchi da elaborare";

    @Override
    public void initialize(ValidateDtCreazione constraintAnnotation) {
    }

    @Override
    public boolean isValid(CriterioRaggrValidation critRaggr, ConstraintValidatorContext context) {

	try {

	    DecCriterioRaggr criterio = critRaggr.getCriterioRaggr();
	    Date dtCreazione = critRaggr.getDtCreazione();

	    // valida filtro su data creazione
	    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/mm/dd 23:59:59");
	    SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/mm/dd HH:mm:ss");
	    SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy/dd/mm");

	    Date currentDate = new Date();

	    if (criterio.getDtCreazioneUnitaDocDa() != null
		    && criterio.getDtCreazioneUnitaDocA() != null
		    && (!sdf.format(criterio.getDtCreazioneUnitaDocA())
			    .equals(Constants.START_DAYS_HH_MM_SS)
			    && criterio.getDtCreazioneUnitaDocDa().compareTo(dtCreazione) <= 0
			    && criterio.getDtCreazioneUnitaDocA().compareTo(dtCreazione) >= 0)) {
		return true;
	    }
	    if (criterio.getDtCreazioneUnitaDocDa() != null
		    && criterio.getDtCreazioneUnitaDocA() != null
		    && sdf.format(criterio.getDtCreazioneUnitaDocA())
			    .equals(Constants.START_DAYS_HH_MM_SS)
		    && criterio.getDtCreazioneUnitaDocDa().compareTo(dtCreazione) <= 0
		    && sdf3.parse(sdf2.format(criterio.getDtCreazioneUnitaDocA()))
			    .compareTo(dtCreazione) >= 0) {
		return true;

	    }
	    if (criterio.getDtCreazioneUnitaDocDa() != null
		    && criterio.getDtCreazioneUnitaDocA() == null
		    && criterio.getDtCreazioneUnitaDocDa().compareTo(dtCreazione) <= 0
		    && currentDate.compareTo(dtCreazione) >= 0) {
		return true;
	    }
	    if (criterio.getDtCreazioneUnitaDocDa() == null
		    && criterio.getDtCreazioneUnitaDocA() != null
		    && (!sdf.format(criterio.getDtCreazioneUnitaDocA())
			    .equals(Constants.START_DAYS_HH_MM_SS)
			    && sdf4.parse(Constants.START_YEARS_YYYY_MM_DD)
				    .compareTo(dtCreazione) <= 0
			    && criterio.getDtCreazioneUnitaDocA().compareTo(dtCreazione) >= 0)) {
		return true;

	    }
	    if (criterio.getDtCreazioneUnitaDocDa() == null
		    && criterio.getDtCreazioneUnitaDocA() != null
		    && (sdf.format(criterio.getDtCreazioneUnitaDocA())
			    .equals(Constants.START_DAYS_HH_MM_SS)
			    && sdf4.parse(Constants.START_YEARS_YYYY_MM_DD)
				    .compareTo(dtCreazione) <= 0
			    && sdf3.parse(sdf2.format(criterio.getDtCreazioneUnitaDocA()))
				    .compareTo(dtCreazione) >= 0)) {
		return true;

	    }
	    if (criterio.getDtCreazioneUnitaDocDa() == null
		    && criterio.getDtCreazioneUnitaDocA() == null) {
		return true;
	    }

	} catch (ParseException ex) {
	    throw new ValidationException(DESC_CONSUMER + ": " + ex.getMessage());
	}

	return false;
    }
}
