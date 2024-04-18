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

package it.eng.parer.firma.crypto.verifica;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.OpTypeEnum;
import it.eng.parer.entity.ElvElencoVersDaElab;
import it.eng.parer.entity.constraint.ElvStatoElencoVer;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.viewEntity.OrgVLisStrutPerEle;
import it.eng.parer.web.ejb.ElenchiVersamentoEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;

@Stateless
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class VerificaFirmeEjb {

    private static final Logger LOG = LoggerFactory.getLogger(VerificaFirmeEjb.class);
    private static final String JOB_NAME = "VERIFICA_FIRME_DATA_VERSAMENTO";

    @EJB
    private ElencoVersamentoHelper evHelper;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private VerificaFirmeEjb me;
    @EJB
    private ElenchiVersamentoEjb evEjb;
    @EJB
    private GenericHelper genericHelper;

    @Resource(mappedName = "jms/ProducerConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/queue/VerificaFirmeDataVersQueue")
    private Queue queue;

    public VerificaFirmeEjb() {
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void verificaFirme() throws Exception {
        LOG.info(JOB_NAME + " - Verifica firme alla data di versamento...");
        int numMaxUdInCodaFase1 = Integer.parseInt(configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NUM_MAX_UD_IN_CODA_VERIFICA_FIRME_DT_VERS));
        Integer totMessiInCodaFase1 = 0;
        int numGgResetStatoInElenco = Integer.parseInt(configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NUM_GG_RESET_STATO_IN_ELENCO));

        List<OrgVLisStrutPerEle> strutture = evHelper.retrieveStrutturePerEle();
        for (OrgVLisStrutPerEle struttura : strutture) {
            if (totMessiInCodaFase1 < numMaxUdInCodaFase1) {
                // Fase 1 elenchi VALIDATI o IN_CODA_JMS_VERIFICA_FIRME_DT_VERS
                totMessiInCodaFase1 = elaboraStrutturaFase1(struttura.getIdStrut().longValue(), totMessiInCodaFase1,
                        numMaxUdInCodaFase1, numGgResetStatoInElenco);
            } else {
                LOG.info(JOB_NAME + " - processate " + totMessiInCodaFase1 + " unità documentarie su "
                        + numMaxUdInCodaFase1 + ", salta messa in coda per verifica firme");
            }
        }
        jobHelper.writeLogJob(OpTypeEnum.VERIFICA_FIRME_A_DATA_VERS.name(), OpTypeEnum.FINE_SCHEDULAZIONE.name());
    }

    private Integer elaboraStrutturaFase1(long idStruttura, Integer totMessiInCoda, int numMax,
            int numGgResetStatoInElenco) throws Exception {
        // determino gli elenchi in stato VALIDATO o IN_CODA_JMS_VERIFICA_FIRME_DT_VERS in modo che si elaborano prima i
        // fiscali, per anno del contenuto descending e per data di firma e poi i non fiscali e appartenenti alla
        // struttura corrente
        List<ElvElencoVersDaElab> elenchiDaVerificare = evHelper.retrieveElenchi(idStruttura,
                ElencoEnums.ElencoStatusEnum.VALIDATO, ElencoEnums.ElencoStatusEnum.IN_CODA_JMS_VERIFICA_FIRME_DT_VERS);
        LOG.info(JOB_NAME + " - struttura id " + idStruttura + ": trovati " + elenchiDaVerificare.size()
                + " elenchi da processare con stato VALIDATO o IN_CODA_JMS_VERIFICA_FIRME_DT_VERS");

        // elaboro gli elenchi
        for (ElvElencoVersDaElab elencoDaElab : elenchiDaVerificare) {
            // non voglio che sia gestito in sessione e quindi che entri in gioco il dirty checker
            genericHelper.detachEntity(elencoDaElab);
            genericHelper.detachEntity(elencoDaElab.getElvElencoVer());
            // MEV#26288
            if (evHelper.checkStatoAllUdInElencoPerVerificaFirmeDtVers(elencoDaElab.getElvElencoVer().getIdElencoVers(),
                    ElencoEnums.UdDocStatusEnum.IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS.name())) {
                me.aggiornaElencoFase1(elencoDaElab.getElvElencoVer().getIdElencoVers(),
                        elencoDaElab.getIdElencoVersDaElab());
            } // end MEV#26288
            else if (totMessiInCoda < numMax) {
                // MAC #18167
                me.processaElencoPerLeFasi(elencoDaElab,
                        ElencoEnums.ElencoStatusEnum.IN_CODA_JMS_VERIFICA_FIRME_DT_VERS.name());
                // ii) il sistema determina l'insieme delle unità documentarie appartenenti all'elenco (sia come
                // versate, che a seguito di documenti aggiunti, che a seguito di aggiornamenti metadati) il cui stato
                // relativo all'elenco sia pari a IN_ELENCO_VALIDATO
                boolean almenoUnaUd = false;
                for (BigDecimal idUd : evHelper.retrieveUdVersOrAggOrUpdInElencoValidate(
                        elencoDaElab.getElvElencoVer().getIdElencoVers(),
                        ElencoEnums.UdDocStatusEnum.IN_ELENCO_VALIDATO.name(), numGgResetStatoInElenco)) {
                    if (totMessiInCoda < numMax) {
                        me.elaboraUdPerLeFasi(idUd.longValue(), elencoDaElab,
                                ElencoEnums.ElencoStatusEnum.IN_CODA_JMS_VERIFICA_FIRME_DT_VERS.name());
                        totMessiInCoda++; // Incrementa il contatore che non deve superare il numero massimo configurato
                        // su DB
                        almenoUnaUd = true;
                    } else {
                        LOG.info(JOB_NAME + " - struttura id " + idStruttura + ": processate " + totMessiInCoda
                                + " unità documentarie dei " + elenchiDaVerificare.size()
                                + " elenchi trovati da processare con stato VALIDATO o IN_CODA_JMS_VERIFICA_FIRME_DT_VERS");
                        return totMessiInCoda;
                    }
                }
                if (almenoUnaUd) {
                    boolean esisteStato = evEjb.isStatoElencoCorrente(elencoDaElab.getElvElencoVer().getIdElencoVers(),
                            ElvStatoElencoVer.TiStatoElenco.IN_CODA_JMS_VERIFICA_FIRME_DT_VERS);
                    if (!esisteStato) {
                        // EVO 19304
                        evEjb.registraStatoElencoVersamento(
                                BigDecimal.valueOf(elencoDaElab.getElvElencoVer().getIdElencoVers()),
                                "IN_CODA_VERIFICA_FIRMA_DT_VERS",
                                "Verifica firme data versamento: nell’elenco di versamento è presente almeno una unità documentaria non annullata",
                                ElvStatoElencoVer.TiStatoElenco.IN_CODA_JMS_VERIFICA_FIRME_DT_VERS, null);
                    }
                }
            } else {
                LOG.info(JOB_NAME + " - struttura id " + idStruttura + ": processate " + totMessiInCoda
                        + " unità documentarie dei " + elenchiDaVerificare.size()
                        + " elenchi trovati da processare con stato VALIDATO o IN_CODA_JMS_VERIFICA_FIRME_DT_VERS");
                return totMessiInCoda;
            }
        }
        LOG.info(JOB_NAME + " - struttura id " + idStruttura + ": processati " + elenchiDaVerificare.size()
                + " elenchi con stato VALIDATO o IN_CODA_JMS_VERIFICA_FIRME_DT_VERS");
        return totMessiInCoda;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void processaElencoPerLeFasi(ElvElencoVersDaElab elencoDaElab, String stato) {
        // Rilegge L'elenco in questa nuova transazione altrimenti la modifica non la salva
        // perché l'elenco appartiene ad un altro contesto transazionale
        ElvElencoVersDaElab elencoDaElabInNuovaTransazione = genericHelper.findById(ElvElencoVersDaElab.class,
                elencoDaElab.getIdElencoVersDaElab());
        if (evEjb.soloUdAnnul(BigDecimal.valueOf(elencoDaElabInNuovaTransazione.getElvElencoVer().getIdElencoVers()))) {
            elencoDaElabInNuovaTransazione.getElvElencoVer()
                    .setTiStatoElenco(ElencoEnums.ElencoStatusEnum.COMPLETATO.name());
            String nota = elencoDaElabInNuovaTransazione.getElvElencoVer().getNtElencoChiuso();
            elencoDaElabInNuovaTransazione.getElvElencoVer()
                    .setNtElencoChiuso((org.apache.commons.lang3.StringUtils.isNotBlank(nota) ? nota + ";" : "")
                            + "L'elenco contiene solo versamenti annullati");
            // MEV#22934
            // il sistema registra lo stato pari a COMPLETATO in ELV_STATO_ELENCO_VERS
            evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elencoDaElab.getElvElencoVer().getIdElencoVers()),
                    "IN_CODA_VERIFICA_FIRMA_DT_VERS",
                    "Verifica firme data versamento: tutte le unità documentarie dell’elenco di versamento sono annullate",
                    ElvStatoElencoVer.TiStatoElenco.COMPLETATO, null);
            // end MEV#22934
            // Elimina l’elenco dalla coda degli elenchi da elaborare
            genericHelper.removeEntity(elencoDaElabInNuovaTransazione, true);
        } else {
            // CAMBIA LO STATO e setta il timestamp
            elencoDaElabInNuovaTransazione.setTiStatoElenco(stato);
            // MAC#18167: Job VERIFICA_FIRME_DT_VERS: set timestamp su elenchi solo se serve
            if (elencoDaElab.getTiStatoElenco().equals(ElencoEnums.ElencoStatusEnum.VALIDATO.name())) {
                elencoDaElabInNuovaTransazione.setTsStatoElenco(new Date());
            }
            elencoDaElabInNuovaTransazione.getElvElencoVer().setTiStatoElenco(stato);
        }
    }

    // MEV#26288
    /**
     * Aggiornamento elenco <strong>fase 1</strong>.
     *
     * <ol>
     * <li>Il sistema aggiorna elenco corrente (ELV_ELENCO_VERS) assegnando stato = FIRME_VERIFICATE_DT_VERS</li>
     * <li>Il sistema aggiorna elenco da elaborare corrente (ELV_ELENCO_VERS_DA_ELAB) assegnando stato =
     * FIRME_VERIFICATE_DT_VERS</li>
     * </ol>
     *
     * @param idElencoVers
     *            - elenco di cui deve essere aggiornato lo stato
     * @param idElencoVersDaElab
     *            - elencoDaElab di cui aggiornare lo stato
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiornaElencoFase1(long idElencoVers, long idElencoVersDaElab) {
        evHelper.aggiornaElencoCorrente(idElencoVers, ElencoEnums.ElencoStatusEnum.FIRME_VERIFICATE_DT_VERS);
        evHelper.aggiornaElencoDaElabCorrente(idElencoVersDaElab,
                ElencoEnums.ElencoStatusEnum.FIRME_VERIFICATE_DT_VERS);
        // EVO 19304
        evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(idElencoVers), "IN_CODA_VERIFICA_FIRMA_DT_VERS",
                "Tutte le unità documentarie non annullate sono IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS",
                ElvStatoElencoVer.TiStatoElenco.FIRME_VERIFICATE_DT_VERS, null);
    }
    // end MEV#26288

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void elaboraUdPerLeFasi(long idUd, ElvElencoVersDaElab elencoDaElab, String stato) {
        // TODO: verificare campo per timestamp di assunzione dello stato (anche in ELV_ELENCO_VERS)
        // TODO: verificare Lock su ud
        // Mette in lock l'UD e la rilascerà al termine di questo metodo.
        // AroUnitaDoc aud=evHelper.findByIdWithLock(AroUnitaDoc.class, idUd);

        Date systemDate = new Date();
        // Aggiorno l'unità doc presente nell'elenco, assegnando stato relativo a quello passato in input
        evHelper.aggiornaStatoUnitaDocInElenco(idUd, elencoDaElab.getElvElencoVer().getIdElencoVers(), stato,
                systemDate, null, null);
        // Aggiorno i documenti aggiunti appartenenti all'unità doc presenti nell'elenco assegnando stato relativo a
        // quello passato in input
        evHelper.aggiornaStatoDocInElenco(idUd, elencoDaElab.getElvElencoVer().getIdElencoVers(), stato, systemDate,
                null, null);
        // Aggiorno gli aggiornamenti metadati relativi all'unità doc presenti nell'elenco assegnando stato relativo a
        // quello passato in input
        evHelper.aggiornaStatoUpdInElenco(idUd, elencoDaElab.getElvElencoVer().getIdElencoVers(), stato, systemDate,
                null, null);
        // METTO IN CODA l'ud
        MessageProducer messageProducer = null;
        Connection connection = null;
        Session session = null;
        TextMessage textMessage = null;
        long idElencoDaElab = elencoDaElab.getIdElencoVersDaElab();
        try {
            connection = connectionFactory.createConnection();
            LOG.debug(String.format("Creo la connessione alla coda per l'ud %s dell'elenco %s", idUd,
                    elencoDaElab.getElvElencoVer().getIdElencoVers()));
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            messageProducer = session.createProducer(queue);
            textMessage = session.createTextMessage();
            // app selector
            textMessage.setStringProperty(Costanti.JMSMsgProperties.MSG_K_APP, Constants.SACER);
            textMessage.setStringProperty("tipoPayload", "CodaVerificaFirmeDataVers");
            textMessage.setStringProperty("statoElencoUd", stato);
            textMessage.setText(String.format("%s,%s", String.valueOf(idElencoDaElab), String.valueOf(idUd)));
            messageProducer.send(textMessage);
            LOG.debug(String.format("Messaggio inviato per l'ud %s dell'elenco %s", idUd,
                    elencoDaElab.getElvElencoVer().getIdElencoVers()));
        } catch (JMSException ex) {
            throw new EJBException("ERRORE nell'invio del messaggio per l'ud " + idUd + " dell'elenco "
                    + elencoDaElab.getElvElencoVer().getIdElencoVers(), ex);
        } finally {
            try {
                if (messageProducer != null) {
                    messageProducer.close();
                }
                if (session != null) {
                    session.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception ex) {
            }
        }
    }

}
