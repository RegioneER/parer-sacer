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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.util.ejb;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.utils.Costanti;

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

    // MAC#27499
    public void manageMessageGroupingInFormatoJson(ConnectionFactory connectionFactory, Queue queue,
            Object objectToSerializeInJson, String tipoPayload, String groupId) {
        MessageProducer messageProducer = null;
        TextMessage textMessage = null;
        Connection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            messageProducer = session.createProducer(queue);
            textMessage = session.createTextMessage();
            textMessage.setStringProperty("JMSXGroupID", groupId);
            // app selector
            textMessage.setStringProperty(Costanti.JMSMsgProperties.MSG_K_APP, Constants.SACER);
            textMessage.setStringProperty("tipoPayload", tipoPayload);
            ObjectMapper jsonMapper = new ObjectMapper();
            textMessage.setText(jsonMapper.writeValueAsString(objectToSerializeInJson));
            log.debug(String.format("JmsProducer [JSON] %s", textMessage.getText()));
            messageProducer.send(textMessage);
            log.debug(String.format("JmsProducer messaggio inviato con groupId %s", groupId));
        } catch (JMSException ex) {
            throw new RuntimeException(String.format("Errore nell'invio del messaggio con groupId %s in coda", groupId),
                    ex);
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
    // end MAC#27499
}
