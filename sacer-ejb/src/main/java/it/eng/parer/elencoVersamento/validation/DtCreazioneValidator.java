/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.elencoVersamento.validation;

import it.eng.parer.elencoVersamento.ejb.ConsumerCodaInAttesaSchedMdb;
import it.eng.parer.entity.DecCriterioRaggr;
import it.eng.parer.web.util.Constants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DtCreazioneValidator implements ConstraintValidator<ValidateDtCreazione, CriterioRaggrValidation> {

    private static final String DESC_CONSUMER = "Consumer coda degli elenchi da elaborare in attesa sched";

    Logger log = LoggerFactory.getLogger(ConsumerCodaInAttesaSchedMdb.class);

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

            if (criterio.getDtCreazioneUnitaDocDa() != null && criterio.getDtCreazioneUnitaDocA() != null
                    && (!sdf.format(criterio.getDtCreazioneUnitaDocA()).equals(Constants.START_DAYS_HH_MM_SS)
                            && criterio.getDtCreazioneUnitaDocDa().compareTo(dtCreazione) <= 0
                            && criterio.getDtCreazioneUnitaDocA().compareTo(dtCreazione) >= 0)) {
                return true;
            }
            if (criterio.getDtCreazioneUnitaDocDa() != null && criterio.getDtCreazioneUnitaDocA() != null
                    && sdf.format(criterio.getDtCreazioneUnitaDocA()).equals(Constants.START_DAYS_HH_MM_SS)
                    && criterio.getDtCreazioneUnitaDocDa().compareTo(dtCreazione) <= 0
                    && sdf3.parse(sdf2.format(criterio.getDtCreazioneUnitaDocA())).compareTo(dtCreazione) >= 0) {
                return true;

            }
            if (criterio.getDtCreazioneUnitaDocDa() != null && criterio.getDtCreazioneUnitaDocA() == null
                    && criterio.getDtCreazioneUnitaDocDa().compareTo(dtCreazione) <= 0
                    && currentDate.compareTo(dtCreazione) >= 0) {
                return true;
            }
            if (criterio.getDtCreazioneUnitaDocDa() == null && criterio.getDtCreazioneUnitaDocA() != null
                    && (!sdf.format(criterio.getDtCreazioneUnitaDocA()).equals(Constants.START_DAYS_HH_MM_SS)
                            && sdf4.parse(Constants.START_YEARS_YYYY_MM_DD).compareTo(dtCreazione) <= 0
                            && criterio.getDtCreazioneUnitaDocA().compareTo(dtCreazione) >= 0)) {
                return true;

            }
            if (criterio.getDtCreazioneUnitaDocDa() == null && criterio.getDtCreazioneUnitaDocA() != null
                    && (sdf.format(criterio.getDtCreazioneUnitaDocA()).equals(Constants.START_DAYS_HH_MM_SS)
                            && sdf4.parse(Constants.START_YEARS_YYYY_MM_DD).compareTo(dtCreazione) <= 0
                            && sdf3.parse(sdf2.format(criterio.getDtCreazioneUnitaDocA()))
                                    .compareTo(dtCreazione) >= 0)) {
                return true;

            }
            if (criterio.getDtCreazioneUnitaDocDa() == null && criterio.getDtCreazioneUnitaDocA() == null) {
                return true;
            }

        } catch (ParseException ex) {
            throw new ValidationException(DESC_CONSUMER + ": " + ex.getMessage());
        }

        return false;
    }
}
