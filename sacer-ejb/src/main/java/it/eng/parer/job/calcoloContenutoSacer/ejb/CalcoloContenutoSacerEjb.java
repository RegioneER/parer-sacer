package it.eng.parer.job.calcoloContenutoSacer.ejb;

import it.eng.parer.exception.ParerErrorSeverity;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.web.util.Constants;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "CalcoloContenutoSacerEjb")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class CalcoloContenutoSacerEjb {

    Logger log = LoggerFactory.getLogger(CalcoloContenutoSacerEjb.class);
    @EJB
    private CalcoloContenutoSacerHelper ccsHelper;
    @EJB
    private JobHelper jobHelper;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;

    public void calcolaContenutoSacer() throws ParerInternalError {
        /* Ricavo l'intervallo di giorni da elaborare */
        Calendar start = ccsHelper.getDataInizioCalcolo();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.DATE, -1);
        SimpleDateFormat formattaData = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
        log.info("Calcolo Contenuto Sacer - Intervallo di date da elaborare: " + formattaData.format(start.getTime())
                + " e " + formattaData.format(end.getTime()));

        /* Ciclo sui giorni eseguendo il calcolo per ogni giorno */
        while (!start.after(end)) {
            Date targetDay = start.getTime();
            log.info("Calcolo Contenuto Sacer - Inserimento totali del giorno " + formattaData.format(targetDay));
            try {
                // Inserisco i totali
                ccsHelper.insertTotaliPerGiorno(targetDay);
            } catch (Exception ex) {
                String errore = "Calcolo Contenuto Sacer - Errore durante il calcolo per il giorno: "
                        + formattaData.format(targetDay);
                // log.fatal(errore, ex);
                log.error(errore, ex);
                throw new ParerInternalError(ParerErrorSeverity.ERROR, errore, ex);
            }
            start.add(Calendar.DATE, 1);
        }

        /*
         * Codice aggiuntivo per il logging su un oggetto di IAM...
         */
        LogParam param = new LogParam("SACER_IAM", "Job Calcolo contenuto Sacer", "CALCOLO_CONTENUTO_SACER",
                "Set data erogazione servizi");
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        // ccsHelper.setDtErogByTiServ(param);
        // ccsHelper.setDtErogBySistVers(param);
        ccsHelper.setDtErog(param);
        // ccsHelper.setDtErogByTiServ(CostantiDB.TiClasseTipoServizio.ATTIVAZIONE_SISTEMA_VERSANTE);

        /* Scrivo in LogJob la fine corretta dell'esecuzione del job di Calcolo Contenuto Sacer */
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CALCOLO_CONTENUTO_SACER.name(),
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null);
        log.info("Calcolo Contenuto Sacer - Esecuzione job terminata con successo!");
    }
}
