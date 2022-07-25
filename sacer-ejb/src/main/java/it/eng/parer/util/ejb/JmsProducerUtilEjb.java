/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.util.ejb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.utils.Costanti;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
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

/**
 *
 * @author Iacolucci_M
 */
@Stateless(mappedName = "JmsProducerUtilEjb")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class JmsProducerUtilEjb {

    private final static Logger log = LoggerFactory.getLogger(JmsProducerUtilEjb.class);

    public void inviaMessaggioInFormatoJson(ConnectionFactory connectionFactory, Queue queue,
            Object objectToSerializeInJson, String tipoPayload) {
        MessageProducer messageProducer = null;
        TextMessage textMessage = null;
        Connection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            messageProducer = session.createProducer(queue);
            textMessage = session.createTextMessage();
            // app selector
            textMessage.setStringProperty(Costanti.JMSMsgProperties.MSG_K_APP, Constants.SACER);
            textMessage.setStringProperty("tipoPayload", tipoPayload);
            ObjectMapper jsonMapper = new ObjectMapper();
            textMessage.setText(jsonMapper.writeValueAsString(objectToSerializeInJson));
            log.debug(String.format("JmsProducer [JSON] %s", textMessage.getText()));
            messageProducer.send(textMessage);
            log.debug("JmsProducer messaggio inviato");
        } catch (JMSException ex) {
            throw new RuntimeException("Errore nell'invio del messaggio in coda", ex);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Errore nella serializzazione in JSON del messaggio per la coda", ex);
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
            } catch (JMSException ex) {
                log.error("Errore (trappato) JMS durante la chiusura delle risorse", ex);
            }
        }

    }
}
