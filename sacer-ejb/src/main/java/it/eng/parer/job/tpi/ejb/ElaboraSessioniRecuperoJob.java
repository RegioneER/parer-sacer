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

import java.time.LocalDate;
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

import it.eng.parer.entity.RecDtVersRecup;
import it.eng.parer.entity.RecSessioneRecup;
import it.eng.parer.entity.VrsDtVers;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.tpi.helper.ElaboraSessioniRecuperoHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.tpi.bean.RetrieveFileUnitaDocRisposta;
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
public class ElaboraSessioniRecuperoJob {

    Logger log = LoggerFactory.getLogger(ElaboraSessioniRecuperoJob.class);
    @EJB
    private JobHelper jobHelper;
    @EJB
    private ElaboraSessioniRecuperoHelper elabRecHelper;
    // MAC#27666
    // private final SimpleDateFormat requestDateFormat = new SimpleDateFormat("ddMMyyyy");
    private final DateTimeFormatter requestDateFormat = DateTimeFormatter.ofPattern("ddMMyyyy");
    // end MAC#27666

    public void elaboraSessioniRecupero() throws ParerInternalError {
        log.info("{} --- determina le sessioni di recupero con stato = IN_CORSO",
                JobConstants.JobEnum.ELABORA_SESSIONI_RECUPERO);
        List<RecSessioneRecup> sessioni = elabRecHelper.getSessioniRecuperoInCorso();
        log.info("{} --- Trovate {} sessioni", JobConstants.JobEnum.ELABORA_SESSIONI_RECUPERO,
                sessioni.size());
        Map<String, String> params = jobHelper.getParamMap();
        // TODO: Controllo spazio libero FileServUtils
        boolean jobChiusoOk = true;

        for (RecSessioneRecup sessione : sessioni) {
            String dirStruttura = sessione.getRecUnitaDocRecup().getAroUnitaDoc().getOrgStrut()
                    .getOrgEnte().getOrgAmbiente().getNmAmbiente()
                    + "-"
                    + sessione.getRecUnitaDocRecup().getAroUnitaDoc().getOrgStrut().getOrgEnte()
                            .getNmEnte()
                    + "-"
                    + sessione.getRecUnitaDocRecup().getAroUnitaDoc().getOrgStrut().getNmStrut();
            String dirUnitaDoc = sessione.getRecUnitaDocRecup().getAroUnitaDoc()
                    .getCdRegistroKeyUnitaDoc() + "-"
                    + sessione.getRecUnitaDocRecup().getAroUnitaDoc().getAaKeyUnitaDoc() + "-"
                    + sessione.getRecUnitaDocRecup().getAroUnitaDoc().getCdKeyUnitaDoc();
            int countRecuperate = 0;
            boolean sessioneErrata = false;
            for (RecDtVersRecup dataRecup : sessione.getRecDtVersRecups()) {
                if (dataRecup.getTiStatoDtVersRecup()
                        .equals(JobConstants.StatoDtVersRecupEnum.RECUPERATA.name())) {
                    countRecuperate++;
                } else if (dataRecup.getTiStatoDtVersRecup()
                        .equals(JobConstants.StatoDtVersRecupEnum.DA_RECUPERARE.name())) {
                    List<VrsDtVers> listaDateVers = elabRecHelper
                            .getVrsDtVersByDate(dataRecup.getDtVers());
                    countRecuperate++;

                    // il file da recuperare è memorizzato su nastro, nel sistema Tivoli.
                    // invoco il WS dedicato del TPI
                    // MAC#27666
                    // Date dataVers = dataRecup.getDtVers();
                    LocalDate dataVers = dataRecup.getDtVers();
                    // end MAC#27666
                    String dateString = requestDateFormat.format(dataVers);
                    log.info("{} --- chiamo il servizio di retrieve archiviazione per la data {}",
                            JobConstants.JobEnum.ELABORA_SESSIONI_RECUPERO, dateString);
                    String urlRequest = params.get(CostantiDB.ParametroAppl.TPI_TPI_HOST_URL)
                            + params.get(CostantiDB.ParametroAppl.TPI_URL_RETRIEVEFILEUNITADOC);
                    Integer timeout = Integer
                            .parseInt(params.get(CostantiDB.ParametroAppl.TPI_TIMEOUT_RETRIEVE));

                    RichiestaTpiInput inputParams = new RichiestaTpiInput(
                            RichiestaTpi.TipoRichiesta.RETRIEVE_FILE_UNITA_DOC, urlRequest, timeout,
                            new BasicNameValuePair(RichiestaTpi.NM_USER,
                                    params.get(CostantiDB.ParametroAppl.TPI_NM_USER_TPI)),
                            new BasicNameValuePair(RichiestaTpi.CD_PSW,
                                    params.get(CostantiDB.ParametroAppl.TPI_CD_PSW_TPI)),
                            new BasicNameValuePair(RichiestaTpi.ROOT_DT_VERS,
                                    listaDateVers.get(0).getDlPathDtVers()),
                            new BasicNameValuePair(RichiestaTpi.DT_VERS, dateString),
                            new BasicNameValuePair(RichiestaTpi.DIR_STRUTTURA, dirStruttura),
                            new BasicNameValuePair(RichiestaTpi.DIR_UNITA_DOC, dirUnitaDoc));

                    EsitoConnessione esitoConn = RichiestaWSTpi.callWs(inputParams);
                    String codiceErrore = esitoConn.getCodiceErrore();
                    String codiceEsito = esitoConn.getCodiceEsito();
                    String messaggioErrore = esitoConn.getMessaggioErrore();

                    if (esitoConn.isErroreConnessione()) {
                        log.error("{} --- {}", JobConstants.JobEnum.ELABORA_SESSIONI_RECUPERO,
                                esitoConn.getDescrErrConnessione());
                        // Il servizio non ha risposto per un errore di connessione
                        // Registro l'errore e chiudo il job
                        jobHelper.writeAtomicLogJob(
                                JobConstants.JobEnum.ELABORA_SESSIONI_RECUPERO.name(),
                                JobConstants.OpTypeEnum.ERRORE.name(),
                                "Timeout dal servizio RetrieveFileUnitaDoc");
                        jobChiusoOk = false;
                        break;
                    } else if (codiceEsito.equals(EsitoConnessione.Esito.KO.name())) {
                        log.error("{} --- RetrieveFileUnitaDoc - {} - {}",
                                JobConstants.JobEnum.ELABORA_SESSIONI_RECUPERO, codiceErrore,
                                messaggioErrore);
                        // se il risultato è stato inaspettatamente NEGATIVO registro la sessione
                        // con stato di errore e
                        // chiudo il job
                        if (!codiceErrore.equals(JobConstants.RetrieveTpiErrors.RETRIEVE_004.name())
                                && !codiceErrore.equals(
                                        JobConstants.RetrieveTpiErrors.RETRIEVE_006.name())) {
                            log.debug(
                                    "{} --- RetrieveFileUnitaDoc - imposto il record della data con stato ERRORE e la sessione con stato CHIUSO_ERR",
                                    JobConstants.JobEnum.ELABORA_SESSIONI_RECUPERO);
                            elabRecHelper.setStatoSessErrata(dataRecup.getIdDtVersRecup(),
                                    " --- RetrieveFileUnitaDoc - " + codiceErrore + " - "
                                            + messaggioErrore);
                        }

                        throw new ParerInternalError(
                                "RetrieveFileUnitaDoc - " + codiceErrore + " - " + messaggioErrore);
                    } else {
                        // risultato OK! Mi tengo l'oggetto di risposta
                        RetrieveFileUnitaDocRisposta resp = (RetrieveFileUnitaDocRisposta) esitoConn
                                .getResponse();
                        if (resp != null) {
                            log.debug(
                                    "{} --- Servizio RetrieveFileUnitaDoc OK : gestisco la risposta",
                                    JobConstants.JobEnum.ELABORA_SESSIONI_RECUPERO);
                            elabRecHelper.retrieveFileUnitaDoc(dataRecup.getIdDtVersRecup(),
                                    countRecuperate, sessione.getRecDtVersRecups().size());
                        } else {
                            log.error(
                                    "{} --- Risposta inaspettata dal servizio RetrieveFileUnitaDoc",
                                    JobConstants.JobEnum.ELABORA_SESSIONI_RECUPERO);
                            throw new ParerInternalError(
                                    "Risposta inaspettata dal servizio RetrieveFileUnitaDoc");
                        }
                    }
                }
            }
            if (jobChiusoOk) {
                // Chiudo tutte le possibili casistiche (i.e. Sessione in corso e tutte le date
                // recuperate)
                if ((!sessioneErrata) && countRecuperate == sessione.getRecDtVersRecups().size()) {
                    elabRecHelper.setSessioneChiusoOk(sessione.getIdSessioneRecup());
                }
            } else {
                break;
            }
        }

        if (jobChiusoOk) {
            log.info("{} --- Fine schedulazione job",
                    JobConstants.JobEnum.ELABORA_SESSIONI_RECUPERO);
            jobHelper.writeAtomicLogJob(JobConstants.JobEnum.ELABORA_SESSIONI_RECUPERO.name(),
                    JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name());
        }
    }
}
