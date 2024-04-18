/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.job.sigillo.ejb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.hsm.beans.HSMUser;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.AmbientiHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.constraint.ElvStatoElencoVer;
import it.eng.parer.entity.constraint.HsmSessioneFirma;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.firma.crypto.ejb.ElencoIndiciAipSignatureSessionEjb;
import it.eng.parer.firma.crypto.sign.SignerHsmEjb;
import it.eng.parer.firma.crypto.sign.SigningRequest;
import it.eng.parer.firma.crypto.sign.SigningResponse;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.viewEntity.ElvVLisElencoVersStato;
import it.eng.parer.web.ejb.ElenchiVersamentoEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.ElenchiVersamentoHelper;
import it.eng.parer.web.helper.UserHelper;
import it.eng.parer.ws.utils.CostantiDB;

/**
 *
 * @author Iacolucci_M
 */
@Stateless(mappedName = "SigilloEjb")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class SigilloEjb {

    Logger log = LoggerFactory.getLogger(SigilloEjb.class);

    // @EJB
    // private SigilloEjb sigilloEjb; // Me stesso!
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private ElenchiVersamentoHelper elenchiVersamentoHelper;
    @EJB
    private ElencoIndiciAipSignatureSessionEjb elencoIndiciAipSignatureSessionEjb;
    @EJB
    private StruttureEjb struttureEjb;
    @EJB
    private ElenchiVersamentoEjb evEjb;
    @EJB
    private AmbientiHelper ambientiHelper;
    @EJB
    private SignerHsmEjb firmaHsmEjb;
    @EJB
    private UserHelper userHelper;
    @Resource
    private SessionContext context;

    /**
     * Metodo chiamato dal JOB di sigillo
     *
     */
    public void sigillo() {
        Date dataInizio = new Date();
        log.info("INIZIO ESECUZIONE DEL JOB SIGILLO [{}]", dataInizio);

        try {
            processaAmbienti();
            jobHelper.writeAtomicLogJob(JobConstants.JobEnum.SIGILLO.name(),
                    JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name());
        } catch (SigilloException ex) {
            String msg = "Errore: " + ex.getMessage();
            jobHelper.writeAtomicLogJob(JobConstants.JobEnum.SIGILLO.name(), JobConstants.OpTypeEnum.ERRORE.name(),
                    msg);
        }

        log.info("FINE ESECUZIONE DEL JOB SIGILLO [{}]", new Date());
    }

    /*
     * Estrae e processa tutti gli ambienti e per ogni ambiente processa tutti gli elenchi
     */
    private void processaAmbienti() throws SigilloException {
        List<OrgAmbiente> l = ambientiHelper.findOrgAmbienteList();
        // Estrae lo username da utilizzare per il sigiloo e la marcatura
        String usernamePerJob = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.USERNAME_JOB_SIGILLO);
        long idUserPerJob = userHelper.findUsrUser(usernamePerJob).getIdUserIam();
        BigDecimal idAmbiente = null;
        for (OrgAmbiente orgAmbiente : l) {
            log.info("Processo l'ambiente [{}]", orgAmbiente.getNmAmbiente());
            idAmbiente = new BigDecimal(orgAmbiente.getIdAmbiente());
            // Se l'ambiente è abilitato e elenchi processati correttamente passa alla marca per ambiente.
            if (processaElenchiPerAmbiente(idAmbiente, idUserPerJob, usernamePerJob)) {
                processaMarcaturaPerAmbiente(idAmbiente, idUserPerJob);
            }
        }
    }

    /*
     * Per ogni ambiente apre una sessione di SIGILLO con numero massimo di elenchi dell'ambiente definiti dal parametro
     * NUM_MAX_ELENCHI_SIGILLO
     */
    private boolean processaElenchiPerAmbiente(BigDecimal idAmbiente, long idUserPerJob, String usernamePerJob)
            throws SigilloException {
        log.info("Firma per l'ambiente {}", idAmbiente);
        boolean ambienteAbilitato = false;
        boolean flSigilloAttivo = Boolean.parseBoolean(
                configurationHelper.getValoreParamApplicByAmb(CostantiDB.ParametroAppl.FL_ABILITA_SIGILLO, idAmbiente));
        String user = configurationHelper.getValoreParamApplicByAmb(CostantiDB.ParametroAppl.HSM_USERNAME_SIGILLO,
                idAmbiente);
        String passwd = configurationHelper.getValoreParamApplicByAmb(CostantiDB.ParametroAppl.HSM_PSW_SIGILLO,
                idAmbiente);
        String otp = configurationHelper.getValoreParamApplicByAmb(CostantiDB.ParametroAppl.HSM_OTP_SIGILLO,
                idAmbiente);
        // MEV#29968
        // Parametro che specifica il numero massimo di elenchi dell'ambiente per cui aprire la sessione HSM per il
        // SIGILLO
        String numMaxElenchiSigillo = configurationHelper
                .getValoreParamApplicByAmb(CostantiDB.ParametroAppl.NUM_MAX_ELENCHI_SIGILLO, idAmbiente);
        // end MEV#29968
        ambienteAbilitato = controlloAbilitazioniPerAmbiente(idAmbiente, flSigilloAttivo, user, passwd, otp,
                numMaxElenchiSigillo);
        if (ambienteAbilitato) {
            ArrayList<String> stati = new ArrayList<>();
            stati.add(ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_CREATO.name());
            ArrayList<String> gestione = new ArrayList<>();
            gestione.add(ElencoEnums.GestioneElencoEnum.SIGILLO.name());
            gestione.add(ElencoEnums.GestioneElencoEnum.MARCA_SIGILLO.name());
            List<ElvVLisElencoVersStato> l = elenchiVersamentoHelper.getListaElenchiDaFirmare(idAmbiente, stati,
                    gestione, Integer.parseInt(numMaxElenchiSigillo));
            log.debug("record estratti: {}", l.size());
            if (l.isEmpty()) {
                log.info("L'ambiente [{}] non ha nulla su cui apporre il sigillo.", idAmbiente);
            } else {
                if (elencoIndiciAipSignatureSessionEjb.hasUserActiveSessions(idUserPerJob)) {
                    log.info("Per l'ambiente [{}] e l'utente [{}] esiste già una sessione aperta quindi lo bypasso.",
                            idAmbiente, idUserPerJob);
                    // Passa al prossimo elenco da firmare
                } else {
                    sessioneDiFirma(l, user, passwd, otp, idUserPerJob, usernamePerJob);
                }
            }
        } else {
            log.info("L'ambiente [{}] non è abilitato il sigillo.", idAmbiente);
        }
        return ambienteAbilitato;
    }

    private void processaMarcaturaPerAmbiente(BigDecimal idAmbiente, long idUserPerJob) throws SigilloException {
        // SESSIONE DI MARCATURA PER AMBIENTE
        try {
            log.info("Marcatura per l'ambiente {}", idAmbiente);
            evEjb.marcaturaFirmaElenchiIndiciAip(idAmbiente, null, null, idUserPerJob, true);
        } catch (ParerUserError ex) {
            throw new SigilloException("Errore marcatura:" + ex.getMessage(), ex);
        }
    }

    private void sessioneDiFirma(List<ElvVLisElencoVersStato> l, String user, String passwd, String otp,
            long idUserPerJob, String usernamePerJob) throws SigilloException {
        SigningRequest request = new SigningRequest(idUserPerJob);
        HSMUser userHSM = new HSMUser(user, passwd.toCharArray());
        userHSM.setOTP(otp.toCharArray());
        request.setUserHSM(userHSM);
        request.setType(HsmSessioneFirma.TiSessioneFirma.ELENCO_INDICI_AIP);
        /*
         * Ho dovuto mettere questa registrazione di stato in un transazione nuova e chiuderla altrimenti nelle fasi
         * successive mi andava tutto in LOCK !!
         */
        context.getBusinessObject(SigilloEjb.class).registraStato(request, l, usernamePerJob);
        Future<SigningResponse> future = null;
        try {
            future = firmaHsmEjb.signP7M(request);
            SigningResponse response = null;
            try {
                // la get() blocca il Thread finché non ha finito di firmare...
                response = future.get();
                if (response.compareTo(SigningResponse.OK) != 0) {
                    throw new SigilloException(response.getDescription());
                }
            } catch (InterruptedException | ExecutionException ex) {
                System.out.println("Interrupted exception o Execution numero UNO");
            }
        } catch (Exception ex) {
            throw new SigilloException("Errore nel processamento asincrono. " + ex.getMessage(), ex);
        }
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void registraStato(SigningRequest request, List<ElvVLisElencoVersStato> l, String usernamePerJob) {
        for (ElvVLisElencoVersStato elvVLisElencoVersStato : l) {
            BigDecimal idElenco = elvVLisElencoVersStato.getIdElencoVers();
            if (evEjb.soloUdAnnul(idElenco)) {
                evEjb.manageElencoUdAnnulDaFirmaElencoIndiciAip(idElenco);
            } else {
                // EVO 19304: prima dei controlli sulla vista ELV_V_CHK_UNA_UD_ANNUL (evEjb.almenoUnaUdAnnul)
                // e relativa gestione delle ud
                // Il sistema registra lo stato dell’elenco creato
                evEjb.registraStatoElencoVersamento(idElenco, "RICHIESTA_FIRMA_ELENCO_INDICI_AIP",
                        "Richiesta firma elenco indici AIP",
                        ElvStatoElencoVer.TiStatoElenco.ELENCO_INDICI_AIP_FIRMA_IN_CORSO, usernamePerJob);
                request.addFile(idElenco);
            }
        }
    }

    /*
     * Se il sigillo è attivo per l'ambiente torna true. Se mancano altri parametri blocca il flusso con un eccezione.
     */
    private boolean controlloAbilitazioniPerAmbiente(BigDecimal idAmbiente, boolean flSigilloAttivo, String user,
            String passwd, String otp, String numMaxElenchiSigillo) throws SigilloException {
        if (StringUtils.isBlank(user)) {
            throw new SigilloException("Il parametro " + CostantiDB.ParametroAppl.HSM_USERNAME_SIGILLO
                    + " per l'ambiente [" + idAmbiente + "] non è stato valorizzato.");
        }
        if (StringUtils.isBlank(passwd)) {
            throw new SigilloException("Il parametro " + CostantiDB.ParametroAppl.HSM_PSW_SIGILLO + " per l'ambiente ["
                    + idAmbiente + "] non è stato valorizzato.");
        }
        if (StringUtils.isBlank(otp)) {
            throw new SigilloException("Il parametro " + CostantiDB.ParametroAppl.HSM_OTP_SIGILLO + " per l'ambiente ["
                    + idAmbiente + "] non è stato valorizzato.");
        }
        // MEV#29968
        if (StringUtils.isBlank(numMaxElenchiSigillo)) {
            throw new SigilloException("Il parametro " + CostantiDB.ParametroAppl.NUM_MAX_ELENCHI_SIGILLO
                    + " per l'ambiente [" + idAmbiente + "] non è stato valorizzato.");
        }
        // end MEV#29968
        return flSigilloAttivo;
    }
}
