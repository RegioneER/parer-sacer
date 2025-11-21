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

package it.eng.parer.job.calcoloConsistenza.ejb;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.exception.ParerErrorSeverity;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.web.util.Constants;
import java.text.DateFormat;
import java.text.ParseException;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "CalcoloConsistenzaEjb")
@LocalBean
@Interceptors({
        it.eng.parer.aop.TransactionInterceptor.class })
public class CalcoloConsistenzaEjb {

    Logger log = LoggerFactory.getLogger(CalcoloConsistenzaEjb.class);
    @EJB
    private CalcoloConsistenzaHelper ccHelper;
    @EJB
    private JobHelper jobHelper;

    public void calcolaConsistenza() throws ParerInternalError {
        // Pulizia di MON_CONTA_BY_STATO_CONSERV_NEW_LAST_180
        ccHelper.truncateMonContaByStatoConservNewLast180();
        log.debug(
                "{} - Pulizia di MON_CONTA_BY_STATO_CONSERV_NEW_LAST_180 prima dell'inizio del JOB",
                JobConstants.JobEnum.CALCOLO_CONSISTENZA.name());

        // Calcolo il periodo di esecuzione del JOB
        Calendar dtRifContaDa = ccHelper.getUltimaDtRifContaA();
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Calendar dtRifContaA = Calendar.getInstance();
        dtRifContaA.set(Calendar.HOUR_OF_DAY, 0);
        dtRifContaA.set(Calendar.MINUTE, 0);
        dtRifContaA.set(Calendar.SECOND, 0);
        dtRifContaA.set(Calendar.MILLISECOND, 0);
        dtRifContaA.add(Calendar.DATE, -1);

        Date dtRifContaDateDa = dtRifContaDa.getTime();
        Date dtRifContaDateA = dtRifContaA.getTime();

        SimpleDateFormat formattaData = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
        boolean firstTime = dtRifContaDa.compareTo(ccHelper.get1Dicembre2011()) == 0;

        /**
         * ******************************************
         *
         * POPOLAMENTO MON_CONTA_BY_STATO_CONSERV_NEW
         *
         ********************************************
         */

        // 1° giro scompattando mese per mese l'intero periodo
        if (firstTime) {
            if (log.isDebugEnabled()) {
                log.debug(
                        "Calcolo Consistenza - Inserimento totali per l'intervallo temporale {} "
                                + " - {} ",
                        formattaData.format(dtRifContaDateDa),
                        formattaData.format(dtRifContaDateA));
            }

            int yearDa = dtRifContaDa.get(Calendar.YEAR);
            int yearA = dtRifContaA.get(Calendar.YEAR);
            for (int i = yearDa; i <= yearA; i++) {
                if (i == yearDa) {
                    Calendar a = ccHelper.get1Dicembre2011();
                    a.add(Calendar.DATE, 30);
                    ccHelper.eseguiPrimoGiroByRange(dtRifContaDateDa, a.getTime());
                } else if (i == yearA) {
                    int monthA = dtRifContaA.get(Calendar.MONTH) + 1;
                    for (int j = 1; j <= monthA; j++) {
                        Month month = Month.of(j);
                        YearMonth ym = YearMonth.of(i, month);

                        // Sono arrivato all'ultimo mese dell'ultimo anno, conto i giorni
                        if (j == monthA) {
                            Calendar da = Calendar.getInstance();
                            Calendar a = Calendar.getInstance();

                            setCalendarDay(ym, da, true);

                            ccHelper.eseguiPrimoGiroByRange(da.getTime(), dtRifContaDateA);
                        } else {
                            Calendar da = Calendar.getInstance();
                            Calendar a = Calendar.getInstance();
                            setCalendarDay(ym, da, true);

                            setCalendarDay(ym, a, false);

                            ccHelper.eseguiPrimoGiroByRange(da.getTime(), a.getTime());
                        }
                    }
                } else {
                    for (Month month : Month.values()) {
                        YearMonth ym = YearMonth.of(i, month);

                        Calendar da = Calendar.getInstance();
                        Calendar a = Calendar.getInstance();

                        setCalendarDay(ym, da, true);
                        setCalendarDay(ym, a, false);

                        ccHelper.eseguiPrimoGiroByRange(da.getTime(), a.getTime());

                    }
                }
            }
        } else {
            try {
                // 2° giro, prendo l'intero intervallo
                log.debug(
                        "{} - Ricalcolo degli ultimi 6 mesi rispetto all'ultima dtRifContaA e salvataggio dei record in MON_CONTA_BY_STATO_CONSERV_NEW_LAST_180",
                        JobConstants.JobEnum.CALCOLO_CONSISTENZA.name());
                dtRifContaDa.add(Calendar.DAY_OF_MONTH, -180);
                dtRifContaDateDa = dtRifContaDa.getTime();
                String dataRicalcoloDaString = sdf.format(dtRifContaDa.getTime());
                String dataRicalcoloAString = sdf.format(dtRifContaA.getTime());

                log.info(
                        "{} - Calcolo dell'intervallo temporale {} - {} e salvataggio in MON_CONTA_BY_STATO_CONSERV_NEW_LAST_180",
                        JobConstants.JobEnum.CALCOLO_CONSISTENZA.name(), dataRicalcoloDaString,
                        dataRicalcoloAString);

                // Inserisco i totali in MON_CONTA_BY_STATO_CONSERV_NEW_LAST_180
                ccHelper.insertTotaliPerGiornoLast180(dtRifContaDateDa, dtRifContaDateA);

                // Inserisco i totali da LAST_180 a MON_CONTA
                ccHelper.insertTotaliPerGiornoOptimized(firstTime, dtRifContaDateDa,
                        dtRifContaDateA);
            } catch (ParseException ex) {
                String errore = "Calcolo Consistenza - Errore durante il calcolo "
                        + "per l'intervallo temporale "
                        + formattaData.format(dtRifContaDa.getTime()) + " - "
                        + formattaData.format(dtRifContaA.getTime());
                throw new ParerInternalError(ParerErrorSeverity.ERROR, errore, ex);
            }
        }

        /* Scrivo in LogJob la fine corretta dell'esecuzione del job di Calcolo Consistenza */
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CALCOLO_CONSISTENZA.name(),
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null);
        log.info("Calcolo Consistenza - Esecuzione job terminata con successo!");
    }

    private void setCalendarDay(YearMonth ym, Calendar c, boolean isDa) {
        if (isDa) {
            c.set(Calendar.DATE, 1);
        } else {
            c.set(Calendar.DATE, ym.lengthOfMonth());
        }
        c.set(Calendar.YEAR, ym.getYear());
        c.set(Calendar.MONTH, ym.getMonth().getValue() - 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

}
