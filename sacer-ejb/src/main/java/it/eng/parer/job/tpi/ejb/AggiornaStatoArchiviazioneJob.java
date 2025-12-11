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

package it.eng.parer.job.tpi.ejb;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.VrsDtVers;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.tpi.helper.AggiornaStatoArchiviazioneHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.tpi.bean.StatoArchiviazioneCartellaRisposta;
import it.eng.tpi.dto.EsitoConnessione;
import it.eng.tpi.dto.RichiestaTpi;
import it.eng.tpi.dto.RichiestaTpiInput;
import it.eng.tpi.util.RichiestaWSTpi;

/**
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
@Interceptors({
        it.eng.parer.aop.TransactionInterceptor.class })
public class AggiornaStatoArchiviazioneJob {

    private Logger log = LoggerFactory.getLogger(AggiornaStatoArchiviazioneJob.class);
    @EJB
    private JobHelper jobHelper;
    @EJB
    private AggiornaStatoArchiviazioneHelper statoArkHelper;
    // MAC#27666
    // private final SimpleDateFormat requestDateFormat = new SimpleDateFormat("ddMMyyyy");
    private final DateTimeFormatter requestDateFormat = DateTimeFormatter.ofPattern("ddMMyyyy");
    // end MAC#27666

    public void aggiornaStatoArk() throws ParerInternalError {

        log.info(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name()
                + " --- ricerca date versamento con stato " + "REGISTRATA, DA_ARCHIVIARE, "
                + "DA_RI-ARCHIVIARE o ARCHIVIATA_ERR");
        List<VrsDtVers> dateVers = statoArkHelper.findArkDatesByStatus(
                JobConstants.ArkStatusEnum.REGISTRATA.name(),
                JobConstants.ArkStatusEnum.DA_ARCHIVIARE.name(),
                JobConstants.ArkStatusEnum.DA_RI_ARCHIVIARE.name(),
                JobConstants.ArkStatusEnum.ARCHIVIATA_ERR.name());
        log.info(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name() + " --- Trovate "
                + dateVers.size() + " date");
        // Map<String, String> params = jobHelper.getParamMap(Constants.SACER);
        Map<String, String> params = jobHelper.getParamMap();

        boolean jobChiusoOk = true;

        for (VrsDtVers vrsDate : dateVers) {
            String dateString = requestDateFormat.format(vrsDate.getDtVers());
            log.info(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name()
                    + " --- chiamo il servizio di stato archiviazione per la data " + dateString);

            String urlRequest = params.get(CostantiDB.ParametroAppl.TPI_TPI_HOST_URL)
                    + params.get(CostantiDB.ParametroAppl.TPI_URL_STATOARKCARTELLE);
            Integer timeout = Integer.parseInt(params.get(CostantiDB.ParametroAppl.TPI_TIMEOUT));
            RichiestaTpiInput inputParams = new RichiestaTpiInput(
                    RichiestaTpi.TipoRichiesta.STATO_ARK_CARTELLA, urlRequest, timeout,
                    new BasicNameValuePair(RichiestaTpi.NM_USER,
                            params.get(CostantiDB.ParametroAppl.TPI_NM_USER_TPI)),
                    new BasicNameValuePair(RichiestaTpi.CD_PSW,
                            params.get(CostantiDB.ParametroAppl.TPI_CD_PSW_TPI)),
                    new BasicNameValuePair(RichiestaTpi.FL_CARTELLA_MIGRAZ,
                            String.valueOf(vrsDate.getFlMigraz().equals(JobConstants.DB_TRUE))),
                    new BasicNameValuePair(RichiestaTpi.DT_VERS, dateString));

            EsitoConnessione esitoConn = RichiestaWSTpi.callWs(inputParams);
            String codiceErrore = esitoConn.getCodiceErrore();
            String codiceEsito = esitoConn.getCodiceEsito();
            String messaggioErrore = esitoConn.getMessaggioErrore();

            if (esitoConn.isErroreConnessione()) {
                log.error(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name() + " --- "
                        + esitoConn.getDescrErrConnessione());
                // Il servizio non ha risposto per un errore di connessione
                // Registro l'errore e chiudo il job
                jobHelper.writeAtomicLogJob(
                        JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name(),
                        JobConstants.OpTypeEnum.ERRORE.name(),
                        "Timeout dal servizio StatoArkCartella");
                jobChiusoOk = false;
                break;
            } else if (codiceEsito.equals(EsitoConnessione.Esito.KO.name())) {
                log.error(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name()
                        + " --- StatoArkCartella - " + codiceErrore + " - " + messaggioErrore);
                // se il risultato è stato inaspettatamente NEGATIVO registro la sessione con stato
                // di errore e chiudo
                // il job
                throw new ParerInternalError(
                        "StatoArkCartella - " + codiceErrore + " - " + messaggioErrore);
            } else {
                // risultato OK! Mi tengo l'oggetto di risposta
                StatoArchiviazioneCartellaRisposta resp = (StatoArchiviazioneCartellaRisposta) esitoConn
                        .getResponse();
                if (resp != null) {
                    log.debug(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name()
                            + " --- Servizio StatoArkCartella OK : gestisco la risposta");
                    if (!statoArkHelper.handleResponse(resp, vrsDate.getIdDtVers(), params)) {
                        jobChiusoOk = false;
                        break;
                    }
                } else {
                    log.error(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name()
                            + " --- Risposta inaspettata dal servizio StatoArkCartella");
                    throw new ParerInternalError(
                            "Risposta inaspettata dal servizio StatoArkCartella");
                }
            }
        }
        //
        if (jobChiusoOk) {
            log.info(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name()
                    + " --- Fine schedulazione job");
            jobHelper.writeAtomicLogJob(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name(),
                    JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name());
        }
    }
}
