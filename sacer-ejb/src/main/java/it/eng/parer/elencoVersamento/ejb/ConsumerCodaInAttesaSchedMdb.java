/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import it.eng.parer.exception.ParerWarningException;
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
import javax.interceptor.ExcludeClassInterceptors;
import javax.interceptor.ExcludeDefaultInterceptors;
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
@MessageDriven(name = "ConsumerCodaInAttesaSchedMdb", activationConfig = {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/ElenchiDaElabInAttesaSchedQueue"),
        @ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "900") })
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ConsumerCodaInAttesaSchedMdb implements MessageListener {

    private static final String DESC_CONSUMER = "Consumer coda degli elenchi da elaborare in attesa sched";

    Logger log = LoggerFactory.getLogger(ConsumerCodaInAttesaSchedMdb.class);

    @EJB
    private ElencoVersamentoHelper evHelper;
    @EJB
    private ElencoVersamentoEjb elencoVersEjb;
    @Resource
    private MessageDrivenContext mdc;

    @Inject
    private Validator validator;

    @Override
    public void onMessage(Message message) {

        PayLoad payloadCodaElenchiDaElabInAttesaSched = null;

        try {
            log.debug(String.format("%s: inizio lavorazione messaggio", DESC_CONSUMER));

            TextMessage textMessage = (TextMessage) message;
            payloadCodaElenchiDaElabInAttesaSched = new ObjectMapper().readValue(textMessage.getText(), PayLoad.class);

            long idStrut = payloadCodaElenchiDaElabInAttesaSched.getIdStrut();
            BigDecimal id = BigDecimal.valueOf(payloadCodaElenchiDaElabInAttesaSched.getId());
            BigDecimal aaKeyUnitaDoc = BigDecimal.valueOf(payloadCodaElenchiDaElabInAttesaSched.getAaKeyUnitaDoc());
            Date dtCreazione = new Date(payloadCodaElenchiDaElabInAttesaSched.getDtCreazione());
            TipoEntitaSacer tipoEntitaSacer = TipoEntitaSacer
                    .valueOf(payloadCodaElenchiDaElabInAttesaSched.getTipoEntitaSacer());
            String statoDaElab = payloadCodaElenchiDaElabInAttesaSched.getStato();

            OrgStrut struttura = evHelper.findById(OrgStrut.class, new BigDecimal(idStrut));
            log.info("Struttura: id ='" + idStrut + "' nome = '" + struttura.getNmStrut() + "'");

            if (statoDaElab.equals(ElencoEnums.UdDocStatusEnum.IN_ATTESA_SCHED.name())) {

                /*
                 * determino tutti i criteri di raggruppamento appartenenti alla struttura versante corrente, il cui
                 * intervallo (data istituzione - data soppressione) includa la data corrente (con estremi compresi); i
                 * criteri sono selezionati in ordine di data istituzione
                 */
                List<DecCriterioRaggr> criteriRaggr = evHelper.retrieveCriterioByStrut(struttura, dtCreazione);
                for (DecCriterioRaggr criterio : criteriRaggr) {

                    log.debug("Criterio della struttura '" + struttura.getNmStrut() + "' trovato: nome criterio = '"
                            + criterio.getNmCriterioRaggr() + "' (id = '" + criterio.getIdCriterioRaggr() + "')");

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

                    try {

                        switch (tipoEntitaSacer) {
                        case UNI_DOC:
                            AroUnitaDoc ud = evHelper.retrieveUnitaDocById(id.longValue());
                            if (ud != null && ud.getTiStatoUdElencoVers().equals(statoDaElab)) {

                                CriterioRaggrValidation critRaggrValidation = new CriterioRaggrValidation(criterio, ud,
                                        aaKeyUnitaDoc, dtCreazione);
                                Set<ConstraintViolation<CriterioRaggrValidation>> violations = validator
                                        .validate(critRaggrValidation);
                                for (ConstraintViolation<CriterioRaggrValidation> violation : violations) {
                                    log.warn(violation.getMessage());
                                }

                                if (violations.isEmpty()) {
                                    elencoVersEjb.manageUdJms(id, aaKeyUnitaDoc, criterio.getIdCriterioRaggr(),
                                            struttura.getIdStrut(), null, true, false, numElenchi);
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
                                for (ConstraintViolation<CriterioRaggrValidation> violation : violations) {
                                    log.warn(violation.getMessage());
                                }

                                if (violations.isEmpty()) {
                                    elencoVersEjb.manageDocJms(id, aaKeyUnitaDoc, criterio.getIdCriterioRaggr(),
                                            struttura.getIdStrut(), null, true, false, numElenchi);
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
                                for (ConstraintViolation<CriterioRaggrValidation> violation : violations) {
                                    log.warn(violation.getMessage());
                                }

                                if (violations.isEmpty()) {
                                    elencoVersEjb.manageUpdJms(id, aaKeyUnitaDoc, criterio.getIdCriterioRaggr(),
                                            struttura.getIdStrut(), null, true, false, numElenchi);
                                }
                            }
                            break;
                        }
                    } catch (ParerInternalError ex) {
                        log.warn(
                                "Attenzione: possibile errore nella configurazione del criterio. Salto a quello successivo");
                    }
                }
            } else {
                throw new EJBException(DESC_CONSUMER + ": errore, STATO non previsto!");
            }
            /* Cambio stato alla ud/doc/upd corrente, non selezionata dai criteri */
            evHelper.setNonSelezSchedJms(struttura, id, tipoEntitaSacer);

            log.debug(String.format("%s: fine lavorazione messaggio", DESC_CONSUMER));
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
}
