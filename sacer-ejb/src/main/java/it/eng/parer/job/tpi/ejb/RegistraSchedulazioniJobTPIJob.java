package it.eng.parer.job.tpi.ejb;

import it.eng.parer.job.tpi.helper.RegistraSchedulazioniJobTPIHelper;
import it.eng.parer.entity.TpiDtSched;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.tpi.bean.SchedulazioniJobTPIRisposta;
import it.eng.tpi.dto.EsitoConnessione;
import it.eng.tpi.dto.RichiestaTpi;
import it.eng.tpi.dto.RichiestaTpiInput;
import it.eng.tpi.util.RichiestaWSTpi;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class RegistraSchedulazioniJobTPIJob {

    Logger log = LoggerFactory.getLogger(RegistraSchedulazioniJobTPIJob.class);
    @EJB
    private JobHelper jobHelper;
    @EJB
    private RegistraSchedulazioniJobTPIHelper regSchedHelper;
    SimpleDateFormat requestDateFormat = new SimpleDateFormat("ddMMyyyy");

    public void elaboraSchedulazioni() throws ParerInternalError {
        log.info(JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI.name()
                + "--- individua l'ultima data registrata e registra tutte le date "
                + "da quella immediatamente successiva a quella attuale");
        Calendar todayDate = Calendar.getInstance();
        todayDate.set(Calendar.HOUR_OF_DAY, 0);
        todayDate.set(Calendar.MINUTE, 0);
        todayDate.set(Calendar.SECOND, 0);
        todayDate.set(Calendar.MILLISECOND, 0);
        // Map<String, String> params = jobHelper.getParamMap(Constants.SACER);
        Map<String, String> params = jobHelper.getParamMap();
        //
        Date tmpDate = regSchedHelper.findLastDaySched();
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(tmpDate);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.MILLISECOND, 0);
        while (currentDate.before(todayDate)) {
            currentDate.add(Calendar.DATE, 1);
            regSchedHelper.creaDataRegistrata(currentDate.getTime());
        }

        log.info(JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI.name()
                + " --- determina le date di schedulazione con stato = REGISTRATA ");
        List<TpiDtSched> dateReg = regSchedHelper.getTpiDtSchedbyStatus(JobConstants.StatoSchedJob.REGISTRATA.name());
        log.info(JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI.name() + " --- Trovate " + dateReg.size()
                + " schedulazioni");

        boolean jobChiusoOk = true;

        for (TpiDtSched sched : dateReg) {
            log.info(JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI.name()
                    + " --- Chiamo il servizio del TPI per ogni schedulazione trovata");
            String dateString = requestDateFormat.format(sched.getDtSched());
            String urlRequest = params.get(CostantiDB.ParametroAppl.TPI_TPI_HOST_URL)
                    + params.get(CostantiDB.ParametroAppl.TPI_URL_SCHEDULAZIONIJOB);
            Integer timeout = Integer.parseInt(params.get(CostantiDB.ParametroAppl.TPI_TIMEOUT));
            RichiestaTpiInput inputParams = new RichiestaTpiInput(RichiestaTpi.TipoRichiesta.SCHEDULAZIONI_JOB_TPI,
                    urlRequest, timeout,
                    new BasicNameValuePair(RichiestaTpi.NM_USER, params.get(CostantiDB.ParametroAppl.TPI_NM_USER_TPI)),
                    new BasicNameValuePair(RichiestaTpi.CD_PSW, params.get(CostantiDB.ParametroAppl.TPI_CD_PSW_TPI)),
                    new BasicNameValuePair(RichiestaTpi.DT_SCHED, dateString));

            EsitoConnessione esitoConn = RichiestaWSTpi.callWs(inputParams);
            String codiceErrore = esitoConn.getCodiceErrore();
            String codiceEsito = esitoConn.getCodiceEsito();
            String messaggioErrore = esitoConn.getMessaggioErrore();

            if (esitoConn.isErroreConnessione()) {
                log.error(JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI.name() + " --- "
                        + esitoConn.getDescrErrConnessione());
                // Il servizio non ha risposto per un errore di connessione
                // Registro l'errore e chiudo il job
                jobHelper.writeAtomicLogJob(JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI.name(),
                        JobConstants.OpTypeEnum.ERRORE.name(), "Timeout dal servizio SchedulazioniJobTPI");
                jobChiusoOk = false;
                break;
            } else if (codiceEsito.equals(EsitoConnessione.Esito.KO.name())) {
                log.error(JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI.name() + " --- SchedulazioniJobTPI - "
                        + codiceErrore + " - " + messaggioErrore);
                // se il risultato è stato inaspettatamente NEGATIVO registro la sessione con stato di errore e chiudo
                // il job

                throw new ParerInternalError("SchedulazioniJobTPI - " + codiceErrore + " - " + messaggioErrore);
            } else {
                // risultato OK! Mi tengo l'oggetto di risposta
                SchedulazioniJobTPIRisposta resp = (SchedulazioniJobTPIRisposta) esitoConn.getResponse();
                if (resp != null) {
                    log.debug(JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI.name()
                            + " --- Servizio RetrieveFileUnitaDoc OK : gestisco la risposta");
                    regSchedHelper.handleResp(resp, sched.getIdDtSched(), todayDate.getTime());
                } else {
                    log.error(JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI.name()
                            + " --- Risposta inaspettata dal servizio SchedulazioniJobTPI");
                    throw new ParerInternalError("Risposta inaspettata dal servizio SchedulazioniJobTPI");
                }
            }
        }
        //
        if (jobChiusoOk) {
            log.info(JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI.name() + " --- Fine schedulazione job");
            jobHelper.writeAtomicLogJob(JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI.name(),
                    JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name());
        }
    }
}
