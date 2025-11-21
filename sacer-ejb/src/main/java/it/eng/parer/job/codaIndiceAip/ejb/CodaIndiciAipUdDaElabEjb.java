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

package it.eng.parer.job.codaIndiceAip.ejb;

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
import it.eng.parer.ws.utils.CostantiDB;

@Stateless
@LocalBean
@Interceptors({
        it.eng.parer.aop.TransactionInterceptor.class })
public class CodaIndiciAipUdDaElabEjb {

    private static final Logger LOG = LoggerFactory.getLogger(CodaIndiciAipUdDaElabEjb.class);
    private static final String JOB_NAME = "PRODUCER_CODA_INDICI_AIP_DA_ELAB";

    @EJB
    private ElencoVersamentoHelper evHelper;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private CodaIndiciAipUdDaElabEjb me;
    @EJB
    private ElenchiVersamentoEjb evEjb;
    @EJB
    private GenericHelper genericHelper;

    @Resource(mappedName = "jms/ProducerConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/queue/IndiciAIPUDDaElabQueue")
    private Queue queue;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void codaIndiciAipUdDaElab() throws Exception {
        LOG.info(JOB_NAME + " - Coda indici aip unità documentarie da elaborare...");

        int numMaxUdInCodaFase2 = Integer.parseInt(configurationHelper.getValoreParamApplicByApplic(
                CostantiDB.ParametroAppl.NUM_MAX_UD_IN_CODA_GENERA_AIP));
        Integer totMessiInCodaFase2 = 0;

        int numGgResetStatoInElenco = Integer
                .parseInt(configurationHelper.getValoreParamApplicByApplic(
                        CostantiDB.ParametroAppl.NUM_GG_RESET_STATO_IN_ELENCO));

        List<OrgVLisStrutPerEle> strutture = evHelper.retrieveStrutturePerEle();
        for (OrgVLisStrutPerEle struttura : strutture) {

            if (totMessiInCodaFase2 < numMaxUdInCodaFase2) {
                // Fase 2 elenchi in stato FIRME_VERIFICATE_DT_VERS o IN_CODA_JMS_INDICE_AIP_DA_ELAB
                totMessiInCodaFase2 = elaboraStrutturaFase2(struttura.getIdStrut().longValue(),
                        totMessiInCodaFase2, numMaxUdInCodaFase2, numGgResetStatoInElenco);
            } else {
                LOG.info(JOB_NAME + " - processate " + totMessiInCodaFase2
                        + " unità documentarie su " + numMaxUdInCodaFase2
                        + ", salta messa in coda per produzione indici aip ed esce");
            }

        }
        // TODO: spostare la seguente riga nel timer.E' sbagliato avere una dipendenza da jobHelper
        // in questa classe.
        jobHelper.writeLogJob(OpTypeEnum.PRODUCER_CODA_INDICI_AIP_DA_ELAB.name(),
                OpTypeEnum.FINE_SCHEDULAZIONE.name());
    }

    private Integer elaboraStrutturaFase2(long idStruttura, Integer totMessiInCoda, int numMax,
            int numGgResetStatoInElenco) {
        // determino gli elenchi appartenenti alla struttura corrente, con stato
        // FIRME_VERIFICATE_DT_VERS o
        // IN_CODA_JMS_INDICE_AIP_DA_ELAB in modo che si elaborano prima i fiscali, per anno del
        // contenuto descending e
        // per data di firma e poi i non fiscali (tabella ELV_ELENCO_VERS_DA_ELAB)
        List<ElvElencoVersDaElab> elenchiPerAip = evHelper.retrieveElenchi(idStruttura,
                ElencoEnums.ElencoStatusEnum.FIRME_VERIFICATE_DT_VERS,
                ElencoEnums.ElencoStatusEnum.IN_CODA_JMS_INDICE_AIP_DA_ELAB);
        LOG.info(JOB_NAME + " - struttura id " + idStruttura + ": trovati " + elenchiPerAip.size()
                + " elenchi da processare con stato FIRME_VERIFICATE_DT_VERS o IN_CODA_JMS_INDICE_AIP_DA_ELAB");

        // elaboro gli elenchi
        for (ElvElencoVersDaElab elencoDaElab : elenchiPerAip) {
            genericHelper.detachEntity(elencoDaElab);
            genericHelper.detachEntity(elencoDaElab.getElvElencoVer());
            // MEV#26288
            if (evHelper.checkStatoAllUdInElencoPerCodaIndiceAipDaElab(
                    elencoDaElab.getElvElencoVer().getIdElencoVers(),
                    ElencoEnums.UdDocStatusEnum.IN_ELENCO_IN_CODA_INDICE_AIP.name(),
                    ElencoEnums.UdDocStatusEnum.IN_ELENCO_CON_INDICI_AIP_GENERATI.name())) {
                me.aggiornaElencoFase2(elencoDaElab.getElvElencoVer().getIdElencoVers(),
                        elencoDaElab.getIdElencoVersDaElab());
            } // end MEV#26288
            else if (totMessiInCoda < numMax) {
                // MAC #18167
                me.processaElencoPerLeFasi(elencoDaElab,
                        ElencoEnums.ElencoStatusEnum.IN_CODA_JMS_INDICE_AIP_DA_ELAB.name());
                // ii) il sistema determina l'insieme delle unità documentarie appartenenti
                // all'elenco (sia come
                // versate, che a seguito di documenti aggiunti, che a seguito di aggiornamenti
                // metadati) il cui stato
                // relativo all'elenco sia pari a IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS
                boolean almenoUnaUd = false;
                for (BigDecimal idUd : evHelper.retrieveUdVersOrAggOrUpdInElencoValidate(
                        elencoDaElab.getElvElencoVer().getIdElencoVers(),
                        ElencoEnums.UdDocStatusEnum.IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS.name(),
                        numGgResetStatoInElenco)) {
                    if (totMessiInCoda < numMax) {
                        me.elaboraUdPerLeFasi(idUd.longValue(), elencoDaElab,
                                ElencoEnums.ElencoStatusEnum.IN_CODA_JMS_INDICE_AIP_DA_ELAB.name());
                        totMessiInCoda++; // Incrementa il contatore che non deve superare il numero
                        // massimo configurato
                        // su DB
                        almenoUnaUd = true;
                    } else {
                        LOG.info(JOB_NAME + " - struttura id " + idStruttura + ": processate "
                                + totMessiInCoda + " unità documentarie dei " + elenchiPerAip.size()
                                + " elenchi trovati da processare con stato FIRME_VERIFICATE_DT_VERS o IN_CODA_JMS_INDICE_AIP_DA_ELAB");
                        return totMessiInCoda;
                    }
                }
                if (almenoUnaUd) {
                    boolean esisteStato = evEjb.isStatoElencoCorrente(
                            elencoDaElab.getElvElencoVer().getIdElencoVers(),
                            ElvStatoElencoVer.TiStatoElenco.IN_CODA_JMS_INDICE_AIP_DA_ELAB);
                    if (!esisteStato) {
                        // MEV 19304
                        evEjb.registraStatoElencoVersamento(
                                BigDecimal
                                        .valueOf(elencoDaElab.getElvElencoVer().getIdElencoVers()),
                                "IN_CODA_INDICE_AIP_DA_ELAB",
                                "Messa in coda unità doc per indice AIP da elaborare: nell’elenco di versamento e’ presente almeno una unità doc non annullata",
                                ElvStatoElencoVer.TiStatoElenco.IN_CODA_JMS_INDICE_AIP_DA_ELAB,
                                null);
                    }
                }
            } else {
                LOG.info(JOB_NAME + " - struttura id " + idStruttura + ": processate "
                        + totMessiInCoda + " unità documentarie dei " + elenchiPerAip.size()
                        + " elenchi trovati da processare con stato FIRME_VERIFICATE_DT_VERS o IN_CODA_JMS_INDICE_AIP_DA_ELAB");
                return totMessiInCoda;
            }
        }
        LOG.info(JOB_NAME + " - struttura id " + idStruttura + ": processati "
                + elenchiPerAip.size()
                + " elenchi con stato FIRME_VERIFICATE_DT_VERS o IN_CODA_JMS_INDICE_AIP_DA_ELAB");
        return totMessiInCoda;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void processaElencoPerLeFasi(ElvElencoVersDaElab elencoDaElab, String stato) {
        // Rilegge L'elenco in questa nuova transazione altrimenti la modifica non la salva
        // perché l'elenco appartiene ad un altro contesto transazionale
        ElvElencoVersDaElab elencoDaElabInNuovaTransazione = genericHelper
                .findById(ElvElencoVersDaElab.class, elencoDaElab.getIdElencoVersDaElab());
        if (evEjb.soloUdAnnul(BigDecimal
                .valueOf(elencoDaElabInNuovaTransazione.getElvElencoVer().getIdElencoVers()))) {
            elencoDaElabInNuovaTransazione.getElvElencoVer()
                    .setTiStatoElenco(ElencoEnums.ElencoStatusEnum.COMPLETATO.name());
            String nota = elencoDaElabInNuovaTransazione.getElvElencoVer().getNtElencoChiuso();
            elencoDaElabInNuovaTransazione.getElvElencoVer().setNtElencoChiuso(
                    (org.apache.commons.lang3.StringUtils.isNotBlank(nota) ? nota + ";" : "")
                            + "L'elenco contiene solo versamenti annullati");
            // MEV#22934
            // il sistema registra lo stato pari a COMPLETATO in ELV_STATO_ELENCO_VERS
            evEjb.registraStatoElencoVersamento(
                    BigDecimal.valueOf(elencoDaElab.getElvElencoVer().getIdElencoVers()),
                    "IN_CODA_INDICE_AIP_DA_ELAB",
                    "Messa in coda unità documentarie per indice AIP da elaborare: tutte le unità documentarie dell’elenco di versamento sono annullate",
                    ElvStatoElencoVer.TiStatoElenco.COMPLETATO, null);
            // end MEV#22934
            // Elimina l’elenco dalla coda degli elenchi da elaborare
            genericHelper.removeEntity(elencoDaElabInNuovaTransazione, true);
        } else {
            // CAMBIA LO STATO e setta il timestamp
            elencoDaElabInNuovaTransazione.setTiStatoElenco(stato);
            // MAC#18167: Job PRODUCER_CODA_INDICI_AIP_DA_ELAB: set timestamp su elenchi solo se
            // serve
            if (elencoDaElab.getTiStatoElenco()
                    .equals(ElencoEnums.ElencoStatusEnum.FIRME_VERIFICATE_DT_VERS.name())) {
                elencoDaElabInNuovaTransazione.setTsStatoElenco(new Date());
            }
            elencoDaElabInNuovaTransazione.getElvElencoVer().setTiStatoElenco(stato);
        }
    }

    // MEV#26288
    /**
     * Aggiornamento elenco <strong>fase 2</strong>.
     *
     * <ol>
     * <li>Il sistema aggiorna elenco corrente (ELV_ELENCO_VERS) assegnando stato =
     * IN_CODA_INDICE_AIP</li>
     * <li>Il sistema elimina da ELV_ELENCO_VERS_DA_ELAB l'elenco corrente</li>
     * </ol>
     *
     * @param idElencoVers       - elenco di cui deve essere aggiornato lo stato
     * @param idElencoVersDaElab - elencoDaElab da eliminare
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiornaElencoFase2(long idElencoVers, long idElencoVersDaElab) {
        evHelper.aggiornaElencoCorrente(idElencoVers,
                ElencoEnums.ElencoStatusEnum.IN_CODA_INDICE_AIP);
        evHelper.aggiornaElencoDaElabCorrente(idElencoVersDaElab,
                ElencoEnums.ElencoStatusEnum.IN_CODA_INDICE_AIP);
        // EVO 19304
        evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(idElencoVers),
                "ESEGUITA_VERIFICA_FIRMA_DT_VERS",
                "Tutte le unità documentarie non annullate sono IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS",
                ElvStatoElencoVer.TiStatoElenco.IN_CODA_INDICE_AIP, null);
    }
    // end MEV#26288

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void elaboraUdPerLeFasi(long idUd, ElvElencoVersDaElab elencoDaElab, String stato) {
        // TODO: verificare campo per timestamp di assunzione dello stato (anche in ELV_ELENCO_VERS)
        // TODO: verificare Lock su ud
        // Mette in lock l'UD e la rilascerà al termine di questo metodo.
        // AroUnitaDoc aud=evHelper.findByIdWithLock(AroUnitaDoc.class, idUd);

        Date systemDate = new Date();
        // Aggiorno l'unità doc presente nell'elenco, assegnando stato relativo a quello passato in
        // input
        evHelper.aggiornaStatoUnitaDocInElenco(idUd,
                elencoDaElab.getElvElencoVer().getIdElencoVers(), stato, systemDate, null, null);
        // Aggiorno i documenti aggiunti appartenenti all'unità doc presenti nell'elenco assegnando
        // stato relativo a
        // quello passato in input
        evHelper.aggiornaStatoDocInElenco(idUd, elencoDaElab.getElvElencoVer().getIdElencoVers(),
                stato, systemDate, null, null);
        // Aggiorno gli aggiornamenti metadati relativi all'unità doc presenti nell'elenco
        // assegnando stato relativo a
        // quello passato in input
        evHelper.aggiornaStatoUpdInElenco(idUd, elencoDaElab.getElvElencoVer().getIdElencoVers(),
                stato, systemDate, null, null);
        // METTO IN CODA l'ud
        TextMessage textMessage = null;
        long idElencoDaElab = elencoDaElab.getIdElencoVersDaElab();
        try (Connection connection = connectionFactory.createConnection();
                Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
                MessageProducer messageProducer = session.createProducer(queue);) {
            LOG.debug(String.format("Creo la connessione alla coda per l'ud %s dell'elenco %s",
                    idUd, elencoDaElab.getElvElencoVer().getIdElencoVers()));
            textMessage = session.createTextMessage();
            textMessage.setStringProperty("tipoPayload", "CodaIndiciAIPUDDaElab");
            textMessage.setStringProperty("statoElencoUd", stato);
            textMessage.setText(
                    String.format("%s,%s", String.valueOf(idElencoDaElab), String.valueOf(idUd)));
            messageProducer.send(textMessage);
            LOG.debug(String.format("Messaggio inviato per l'ud %s dell'elenco %s", idUd,
                    elencoDaElab.getElvElencoVer().getIdElencoVers()));
        } catch (JMSException ex) {
            throw new EJBException("ERRORE nell'invio del messaggio per l'ud " + idUd
                    + " dell'elenco " + elencoDaElab.getElvElencoVer().getIdElencoVers(), ex);
        }
    }

}
