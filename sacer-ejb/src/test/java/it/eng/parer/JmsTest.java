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

package it.eng.parer;

import it.eng.ArquillianUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import javax.jms.*;

import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
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
    public void resourcesIsAvailable() {
        assertNotNull(connectionFactory);
        assertNotNull(verificaFirmeDataVersQueue);
        assertNotNull(indiceAipUnitaDocQueue);

    }

    @Test
    public void verificaFirmeDataVersQueueSendMessage() {
        testSendMessage(verificaFirmeDataVersQueue);

    }

    @Test
    public void oggettiDaMigrareQueueSendMessage() {
        testSendMessage(oggettiDaMigrareQueue);
    }

    @Test
    public void indiceAipUnitaDocQueueSendMessage() {
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
