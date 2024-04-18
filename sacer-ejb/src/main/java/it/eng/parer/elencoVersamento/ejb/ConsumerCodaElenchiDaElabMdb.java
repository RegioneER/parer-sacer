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

package it.eng.parer.elencoVersamento.ejb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.elencoVersamento.utils.PayLoad;
import it.eng.parer.elencoVersamento.validation.CriterioRaggrValidation;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.entity.DecCriterioRaggr;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.web.util.Constants.TipoEntitaSacer;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.validation.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DiLorenzo_F
 */
@SuppressWarnings("unchecked")
@MessageDriven(name = "ConsumerCodaElenchiDaElabMdb", activationConfig = {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/ElenchiDaElabQueue"),
        @ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "900") })
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ConsumerCodaElenchiDaElabMdb implements MessageListener {

    public static final String LOG_CRITERIO = "Criterio della struttura '{}' trovato: nome criterio = '{}' (id = '{}')";
    private static final String DESC_CONSUMER = "Consumer coda degli elenchi da elaborare";

    Logger log = LoggerFactory.getLogger(ConsumerCodaElenchiDaElabMdb.class);

    @EJB
    private ElencoVersamentoHelper evHelper;
    @EJB
    private ElencoVersamentoEjb elencoVersEjb;
    @EJB
    private GenericHelper genericHelper;
    @Resource
    private MessageDrivenContext mdc;

    @Inject
    private Validator validator;

    @Override
    public void onMessage(Message message) {

        PayLoad payloadCodaElenchiDaElab = null;

        try {
            log.debug("{}: inizio lavorazione messaggio", DESC_CONSUMER);

            TextMessage textMessage = (TextMessage) message;
            payloadCodaElenchiDaElab = new ObjectMapper().readValue(textMessage.getText(), PayLoad.class);

            long idStrut = payloadCodaElenchiDaElab.getIdStrut();
            BigDecimal id = BigDecimal.valueOf(payloadCodaElenchiDaElab.getId());
            BigDecimal aaKeyUnitaDoc = BigDecimal.valueOf(payloadCodaElenchiDaElab.getAaKeyUnitaDoc());
            Date dtCreazione = new Date(payloadCodaElenchiDaElab.getDtCreazione());
            TipoEntitaSacer tipoEntitaSacer = TipoEntitaSacer.valueOf(payloadCodaElenchiDaElab.getTipoEntitaSacer());
            String statoDaElab = payloadCodaElenchiDaElab.getStato();
            // MEV#27891
            Long idCriterio = payloadCodaElenchiDaElab.getIdCriterio();
            Long idLogJob = payloadCodaElenchiDaElab.getIdLogJob();
            // end MEV#27891
            // MAC#28020
            Boolean isTheFirst = payloadCodaElenchiDaElab.isIsTheFirst();
            Boolean isTheLast = payloadCodaElenchiDaElab.isIsTheLast();
            // end MAC#28020

            OrgStrut struttura = genericHelper.findById(OrgStrut.class, new BigDecimal(idStrut));
            log.debug("Struttura: id ='{}' nome = '{}'", idStrut, struttura.getNmStrut());

            if (statoDaElab.equals(ElencoEnums.UdDocStatusEnum.IN_ATTESA_SCHED.name())) {

                /*
                 * determino tutti i criteri di raggruppamento appartenenti alla struttura versante corrente, il cui
                 * intervallo (data istituzione - data soppressione) includa la data corrente (con estremi compresi); i
                 * criteri sono selezionati in ordine di data istituzione
                 */
                List<DecCriterioRaggr> criteriRaggr = evHelper.retrieveCriterioByStrut(struttura, dtCreazione);

                for (DecCriterioRaggr criterio : criteriRaggr) {

                    log.debug(LOG_CRITERIO, struttura.getNmStrut(), criterio.getNmCriterioRaggr(),
                            criterio.getIdCriterioRaggr());

                    /* Definisco numero elenchi creati in un giorno nullo */
                    Long numElenchi = null;
                    /*
                     * Determino se per il criterio il numero massimo di elenchi che si può creare in un giorno e' non
                     * nullo
                     */
                    if (criterio.getNiMaxElenchiByGg() != null) {
                        long countElenchiNonAperti = evHelper
                                .countElenchiGgByCritNonAperti(new BigDecimal(criterio.getIdCriterioRaggr()));
                        long countElenchiAperti = evHelper
                                .countElenchiGgByCritAperti(new BigDecimal(criterio.getIdCriterioRaggr()));
                        if (countElenchiNonAperti >= criterio.getNiMaxElenchiByGg().longValue()) {
                            continue;
                        } else {
                            numElenchi = countElenchiNonAperti + countElenchiAperti;
                        }
                    }

                    managePayloadByCriterio(struttura, criterio, id, tipoEntitaSacer, statoDaElab, dtCreazione,
                            aaKeyUnitaDoc, null, true, true, numElenchi);
                }

                /* Cambio stato alla ud/doc/upd corrente, non selezionata dai criteri */
                evHelper.setNonSelezSchedJms(struttura, id, tipoEntitaSacer);

            } else if (statoDaElab.equals(ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name())) {

                DecCriterioRaggr criterio = evHelper.retrieveCriterioByid(idCriterio);

                log.debug(LOG_CRITERIO, struttura.getNmStrut(), criterio.getNmCriterioRaggr(),
                        criterio.getIdCriterioRaggr());

                /* Definisco numero elenchi creati in un giorno nullo */
                Long numElenchi = null;
                boolean skipMessage = false;

                /*
                 * Determino se per il criterio il numero massimo di elenchi che si può creare in un giorno e' non nullo
                 */
                if (criterio.getNiMaxElenchiByGg() != null) {
                    long countElenchiNonAperti = evHelper
                            .countElenchiGgByCritNonAperti(new BigDecimal(criterio.getIdCriterioRaggr()));
                    long countElenchiAperti = evHelper
                            .countElenchiGgByCritAperti(new BigDecimal(criterio.getIdCriterioRaggr()));
                    if (countElenchiNonAperti >= criterio.getNiMaxElenchiByGg().longValue()) {
                        skipMessage = true;
                    } else {
                        numElenchi = countElenchiNonAperti + countElenchiAperti;
                    }
                }

                if (!skipMessage) {
                    managePayload(struttura, criterio, id, tipoEntitaSacer, statoDaElab, dtCreazione, aaKeyUnitaDoc,
                            idLogJob, isTheLast, isTheFirst, numElenchi);
                }
            } else {
                throw new EJBException(DESC_CONSUMER + ": errore, STATO non previsto!");
            }

            if (log.isDebugEnabled()) {
                log.debug(String.format("%s: fine lavorazione messaggio", DESC_CONSUMER));
            }
        } catch (JMSException ex) {
            log.error("Errore nel consumer: JMSException " + ExceptionUtils.getRootCauseMessage(ex), ex);
            mdc.setRollbackOnly();
        } catch (JsonProcessingException ex) {
            log.error("Errore nella deserializzazione del messaggio nel consumer: JsonProcessingException "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            mdc.setRollbackOnly();
        } catch (Exception ex) {
            log.error("Errore nell'esecuzione del Consumer di creazione automatica degli elenchi", ex);
            mdc.setRollbackOnly();
        }
    }

    public void managePayload(OrgStrut struttura, DecCriterioRaggr criterio, BigDecimal id,
            TipoEntitaSacer tipoEntitaSacer, String statoDaElab, Date dtCreazione, BigDecimal aaKeyUnitaDoc,
            Long idLogJob, Boolean isTheLast, Boolean isTheFirst, Long numElenchi) throws Exception {
        try {
            switch (tipoEntitaSacer) {
            case UNI_DOC:
                AroUnitaDoc ud = evHelper.retrieveUnitaDocById(id.longValue());
                if (ud != null && ud.getTiStatoUdElencoVers().equals(statoDaElab)) {
                    elencoVersEjb.manageUdJms(id, aaKeyUnitaDoc, criterio.getIdCriterioRaggr(), struttura.getIdStrut(),
                            idLogJob, isTheLast, isTheFirst, numElenchi);
                }
                break;
            case DOC:
                AroDoc doc = evHelper.retrieveDocById(id.longValue());
                if (doc != null && doc.getTiStatoDocElencoVers().equals(statoDaElab)) {
                    elencoVersEjb.manageDocJms(id, aaKeyUnitaDoc, criterio.getIdCriterioRaggr(), struttura.getIdStrut(),
                            idLogJob, isTheLast, isTheFirst, numElenchi);
                }
                break;
            case UPD:
                AroUpdUnitaDoc upd = evHelper.retrieveUpdById(id.longValue());
                if (upd != null && upd.getTiStatoUpdElencoVers().name().equals(statoDaElab)) {
                    elencoVersEjb.manageUpdJms(id, aaKeyUnitaDoc, criterio.getIdCriterioRaggr(), struttura.getIdStrut(),
                            idLogJob, isTheLast, isTheFirst, numElenchi);
                }
                break;
            }

        } catch (ParerInternalError ex) {
            log.warn("Attenzione: possibile errore nella configurazione del criterio. Salto a quello successivo");
        }
    }

    @SuppressWarnings("rawtypes")
    public void managePayloadByCriterio(OrgStrut struttura, DecCriterioRaggr criterio, BigDecimal id,
            TipoEntitaSacer tipoEntitaSacer, String statoDaElab, Date dtCreazione, BigDecimal aaKeyUnitaDoc,
            Long idLogJob, Boolean isTheLast, Boolean isTheFirst, Long numElenchi) throws Exception {

        try {
            switch (tipoEntitaSacer) {
            case UNI_DOC:
                AroUnitaDoc ud = evHelper.retrieveUnitaDocById(id.longValue());
                if (ud != null && ud.getTiStatoUdElencoVers().equals(statoDaElab)) {

                    CriterioRaggrValidation<AroUnitaDoc> critRaggrValidation = new CriterioRaggrValidation<>(criterio,
                            ud, aaKeyUnitaDoc, dtCreazione);
                    Set<ConstraintViolation<CriterioRaggrValidation>> violations = validator
                            .validate(critRaggrValidation);
                    if (violations.isEmpty()) {
                        elencoVersEjb.manageUdJms(id, aaKeyUnitaDoc, criterio.getIdCriterioRaggr(),
                                struttura.getIdStrut(), idLogJob, isTheLast, isTheFirst, numElenchi);
                    }
                }
                break;
            case DOC:
                AroDoc doc = evHelper.retrieveDocById(id.longValue());
                if (doc != null && doc.getTiStatoDocElencoVers().equals(statoDaElab)) {

                    CriterioRaggrValidation critRaggrValidation = new CriterioRaggrValidation(criterio, doc,
                            aaKeyUnitaDoc, dtCreazione);
                    Set<ConstraintViolation<CriterioRaggrValidation>> violations = validator
                            .validate(critRaggrValidation);
                    if (violations.isEmpty()) {
                        elencoVersEjb.manageDocJms(id, aaKeyUnitaDoc, criterio.getIdCriterioRaggr(),
                                struttura.getIdStrut(), idLogJob, isTheLast, isTheFirst, numElenchi);
                    }
                }
                break;
            case UPD:
                AroUpdUnitaDoc upd = evHelper.retrieveUpdById(id.longValue());
                if (upd != null && upd.getTiStatoUpdElencoVers().name().equals(statoDaElab)) {

                    CriterioRaggrValidation critRaggrValidation = new CriterioRaggrValidation(criterio, upd,
                            aaKeyUnitaDoc, dtCreazione);
                    Set<ConstraintViolation<CriterioRaggrValidation>> violations = validator
                            .validate(critRaggrValidation);
                    if (violations.isEmpty()) {
                        elencoVersEjb.manageUpdJms(id, aaKeyUnitaDoc, criterio.getIdCriterioRaggr(),
                                struttura.getIdStrut(), idLogJob, isTheLast, isTheFirst, numElenchi);
                    }
                }
                break;
            }
        } catch (ParerInternalError ex) {
            log.warn("Attenzione: possibile errore nella configurazione del criterio. Salto a quello successivo");
        }
    }

}
