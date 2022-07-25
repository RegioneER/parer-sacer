/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.monitoraggio.ejb;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.ws.monitoraggio.dto.DLQMsgInfo;
import it.eng.parer.ws.utils.Costanti;

/**
 * Classe per gestire i messaggi in coda
 *
 * @author sinatti_s
 */
@Stateless
@LocalBean
public class MonitorCoda {

    private static final Logger LOG = LoggerFactory.getLogger(MonitorCoda.class);

    public static final String PAYLOAD_NOTYPE = "NON_PRESENTE";
    public static final String PAYLOAD_NOSTATE = "NO_STATE";

    @Resource(mappedName = "jms/ProducerConnectionFactory")
    private QueueConnectionFactory connFactory;
    @Resource(mappedName = "jms/dmq")
    private Queue dmqQueue;

    /**
     * Recupera i messaggi dalla coda
     * 
     * @param queue
     *            nome coda
     * @param messageSelector
     *            selettore
     * 
     * @return lista di messaggi
     * 
     * @throws JMSException
     *             errore generico
     */
    public List<DLQMsgInfo> retrieveMsgInQueue(String queue, String messageSelector) throws JMSException {
        List<DLQMsgInfo> msgList = new ArrayList<DLQMsgInfo>();
        QueueConnection queueConn = null;
        QueueBrowser queueBrowser = null;
        QueueSession queueSession = null;
        try {
            queueConn = connFactory.createQueueConnection();
            queueSession = queueConn.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
            if (queue.equals(NomeCoda.dmqQueue.name())) {
                queueBrowser = queueSession.createBrowser(dmqQueue, messageSelector);
                // queueBrowser = queueSession.createBrowser(dmqQueue);
            } else {
                // coda non presente
            }

            Enumeration<?> e = queueBrowser.getEnumeration();
            while (e.hasMoreElements()) {

                DLQMsgInfo infoCoda = new DLQMsgInfo();
                Message objMessage = (Message) e.nextElement();

                /**
                 * JMS Message Metadata custom info (PARER) a) tipoPayload b) statoElenco
                 */
                if (objMessage.getStringProperty(Costanti.JMSMsgProperties.MSG_K_PAYLOADTYPE) != null) {
                    infoCoda.setPayloadType(objMessage.getStringProperty(Costanti.JMSMsgProperties.MSG_K_PAYLOADTYPE));
                } else {
                    // continue; // passa al messaggio successivo (Nota: avendo inserito un selettore, caso che non
                    // // dovrebbe mai verificarsi)
                    infoCoda.setPayloadType(PAYLOAD_NOTYPE);
                }
                if (objMessage.getStringProperty(Costanti.JMSMsgProperties.MSG_K_STATUS) != null) {
                    infoCoda.setState(objMessage.getStringProperty(Costanti.JMSMsgProperties.MSG_K_STATUS));
                } else /* no state */ {
                    infoCoda.setState(PAYLOAD_NOSTATE);
                }
                infoCoda.setSentTimestamp(new Date(objMessage.getJMSTimestamp()));

                infoCoda.setMessageID(objMessage.getJMSMessageID());

                if (objMessage.getStringProperty("JMSXDeliveryCount") != null) {
                    infoCoda.setDeliveryCount(Integer.parseInt(objMessage.getStringProperty("JMSXDeliveryCount")));
                }

                msgList.add(infoCoda);
            }
        } catch (Exception ex) {
            throw new JMSException("Errore nella lettura dalla coda '" + queue);
        } finally {
            if (queueBrowser != null) {
                queueBrowser.close();
            }
            if (queueSession != null) {
                queueSession.close();
            }
            if (queueConn != null) {
                queueConn.close();
            }
        }
        return msgList;
    }

    public enum NomeCoda {
        dmqQueue;
    }

}
