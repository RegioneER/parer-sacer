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

package it.eng.parer.job.indiceAip.ejb;

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
@MessageDriven(name = "ConsumerCodaIndiceAipUnitaDocMdb", activationConfig = {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/IndiceAipUnitaDocQueue"),
        // MEV#30195
        @ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "7200")
        // end MEV#30195
})
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ConsumerCodaIndiceAipUnitaDocMdb implements MessageListener {

    private static final String DESC_CONSUMER = "Consumer coda indice Aip Unità doc";

    Logger log = LoggerFactory.getLogger(ConsumerCodaIndiceAipUnitaDocMdb.class);

    @EJB
    private ElaborazioneRigaIndiceAipDaElab elaborazioneRigaIndiceAipDaElab;
    @Resource
    private MessageDrivenContext mdc;

    @Override
    public void onMessage(Message message) {
        // throw new EJBException("ECCEZIONE FORZATA");
        try {
            log.debug("Inizio lavorazione messaggio");
            TextMessage textMessage = (TextMessage) message;
            long idIndiceAipDaElab = Long.parseLong(textMessage.getText());
            try {
                elaborazioneRigaIndiceAipDaElab.gestisciIndiceAipDaElab(idIndiceAipDaElab);
            } catch (Exception ex) {
                throw new EJBException(
                        String.format("%s: errore nella gestione dell'indice AIP [%s]",
                                DESC_CONSUMER, idIndiceAipDaElab),
                        ex);
            }
            log.debug("Fine lavorazione messaggio");
        } catch (JMSException ex) {
            log.error("Errore nel consumer: JMSException " + ExceptionUtils.getRootCauseMessage(ex),
                    ex);
            mdc.setRollbackOnly();
            // throw new EJBException(ex);
        }

    }

}
