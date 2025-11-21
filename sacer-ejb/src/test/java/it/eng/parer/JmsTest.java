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

package it.eng.parer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.ArquillianUtils;

@ArquillianTest
public class JmsTest {
    @Resource(mappedName = "SacerJmsXA")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/queue/VerificaFirmeDataVersQueue")
    private Queue verificaFirmeDataVersQueue;

    @Resource(mappedName = "/jms/queue/OggettiDaMigrareQueue")
    private Queue oggettiDaMigrareQueue;

    @Resource(mappedName = "/jms/queue/IndiceAipUnitaDocQueue")
    private Queue indiceAipUnitaDocQueue;

    public static boolean readOk = false;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ArquillianUtils.createEnterpriseArchive("testJms",
                ArquillianUtils.createSacerJavaArchive(Arrays.asList(""), JmsTest.class));
    }

    @Test
    void resourcesIsAvailable() {
        assertNotNull(connectionFactory);
        assertNotNull(verificaFirmeDataVersQueue);
        assertNotNull(indiceAipUnitaDocQueue);

    }

    @Test
    void verificaFirmeDataVersQueueSendMessage() {
        testSendMessage(verificaFirmeDataVersQueue);

    }

    @Test
    void oggettiDaMigrareQueueSendMessage() {
        testSendMessage(oggettiDaMigrareQueue);
    }

    @Test
    void indiceAipUnitaDocQueueSendMessage() {
        testSendMessage(indiceAipUnitaDocQueue);
    }

    private void testSendMessage(Queue queue) {
        MessageProducer messageProducer = null;
        Connection connection = null;
        Session session = null;
        TextMessage textMessage = null;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            messageProducer = session.createProducer(queue);
            textMessage = session.createTextMessage();
            textMessage.setStringProperty("messaggio", "test");
            messageProducer.send(textMessage);
            assertTrue(true);
        } catch (JMSException e) {
            fail("Impossibile inviare messaggi sulla coda " + e);
        }
    }
}
