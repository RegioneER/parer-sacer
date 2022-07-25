/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.firma.crypto.ejb;

import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.ElvElencoVersDaElab;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.viewEntity.ElvVLisUdByStato;
import java.math.BigDecimal;
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
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Iacolucci_M
 */
@MessageDriven(name = "ConsumerCodaVerificaFirmeMdb", activationConfig = {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/VerificaFirmeDataVersQueue"),
        @ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "900") })
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ConsumerCodaVerificaFirmeMdb implements MessageListener {

    private static final String DESC_CONSUMER = "Consumer coda verifica firma";

    Logger log = LoggerFactory.getLogger(ConsumerCodaVerificaFirmeMdb.class);

    @EJB
    private ElencoVersamentoHelper evHelper;
    @EJB
    private ElaboraCodaVerificaFirmeEjb elaboraCodaVerificaFirmeEjb;
    @Resource
    private MessageDrivenContext mdc;

    @Override
    public void onMessage(Message message) {

        try {
            log.debug(String.format("%s: inizio lavorazione messaggio", DESC_CONSUMER));
            TextMessage textMessage = (TextMessage) message;
            String statoElencoUd = textMessage.getStringProperty("statoElencoUd");
            String[] ids = textMessage.getText().split(",");
            long idElencoDaElab = Long.parseLong(ids[0]);
            long idUd = Long.parseLong(ids[1]);
            ElvElencoVersDaElab elencoDaElab = evHelper.findById(ElvElencoVersDaElab.class,
                    new BigDecimal(idElencoDaElab));
            AroUnitaDoc ud = evHelper.findById(AroUnitaDoc.class, new BigDecimal(idUd));
            if (elencoDaElab != null && ud != null) {
                // Modifica messa in sospeso perché forse bisognerà fare dei lock piu selettivi nella fase 2 !!
                // MAC #16385 - Job creazione indici AIP - stato elenco non coerente con stati UD (Lock sull'elenco)
                // ElvElencoVer elvElencoVer=evHelper.findByIdWithLock(ElvElencoVer.class,
                // elenco.getElvElencoVer().getIdElencoVers());
                if (statoElencoUd.equals(ElencoEnums.ElencoStatusEnum.IN_CODA_JMS_VERIFICA_FIRME_DT_VERS.name())) {
                    log.debug(String.format("Processo unità documentaria [%s] dell'elenco [%s] aventi stato [%s]", idUd,
                            elencoDaElab.getElvElencoVer().getIdElencoVers(), statoElencoUd));
                    // throw new EJBException("ESPLOSIONE SIMULATA!!");
                    // Verifico che l'unità doc appartenente all'elenco definiti nel payload del messaggio abbia stato =
                    // IN_CODA_JMS_VERIFICA_FIRME_DT_VERS e che l'elenco abbia stato IN_CODA_JMS_VERIFICA_FIRME_DT_VERS
                    // (vedi vista ELV_V_LIS_UD_BY_STATO)
                    if (evHelper.checkStatoElencoUdPerLeFasi(idUd, elencoDaElab.getElvElencoVer().getIdElencoVers(),
                            statoElencoUd)) {
                        try {
                            elaboraCodaVerificaFirmeEjb.elaboraUDFase1(idUd,
                                    elencoDaElab.getElvElencoVer().getIdElencoVers(),
                                    elencoDaElab.getElvElencoVer().getDtFirmaIndice());
                        } catch (ParerUserError ex) {
                            log.info(DESC_CONSUMER + ": " + ex.getMessage() + ", passo all'UD successiva");
                        }
                    }
                } else {
                    throw new EJBException(DESC_CONSUMER + ": errore, STATO non previsto!");
                }
            } else {
                log.debug(String.format(
                        "L'unità documentaria con ID %s o l'elenco da elaborare con ID %s per il messaggio con stato %s non esiste!",
                        idUd, idElencoDaElab, statoElencoUd));
            }
            log.debug(String.format("%s: fine lavorazione messaggio", DESC_CONSUMER));
        } catch (JMSException ex) {
            log.error("Errore nel consumer: JMSException " + ExceptionUtils.getRootCauseMessage(ex), ex);
            mdc.setRollbackOnly();
            // throw new EJBException(ex);
        }

    }
}
