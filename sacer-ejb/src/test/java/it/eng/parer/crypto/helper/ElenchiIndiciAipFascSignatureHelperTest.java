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

package it.eng.parer.crypto.helper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.entity.HsmSessioneFirma;
import it.eng.parer.entity.IamUser;
import it.eng.parer.firma.crypto.helper.ElenchiIndiciAipFascSignatureHelper;
import it.eng.parer.firma.crypto.helper.SigningHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.HelperTest;

public class ElenchiIndiciAipFascSignatureHelperTest
        extends HelperTest<ElenchiIndiciAipFascSignatureHelper> {
    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(
                ElenchiIndiciAipFascSignatureHelperTest.class.getSimpleName(),
                HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""),
                        ElenchiIndiciAipFascSignatureHelperTest.class,
                        ElenchiIndiciAipFascSignatureHelper.class, SigningHelper.class,
                        ConfigurationHelper.class));
    }

    @Test
    void createSessioneFirma_queryIsOk() {
        Long userId = 5000L;// admin_generale
        helper.createSessioneFirma(userId);
        assertTrue(true);
    }

    private HsmSessioneFirma aHsmSessioneFirma() {
        HsmSessioneFirma session = new HsmSessioneFirma();
        session.setIdSessioneFirma(aLong());
        return session;
    }

    @Test
    void getActiveSessionsByUser_IamUser_queryIsOk() {
        IamUser user = myIamUser();
        helper.getActiveSessionsByUser(user);
        assertTrue(true);
    }

    private IamUser myIamUser() {
        IamUser user = new IamUser();
        user.setIdUserIam(aLong());
        return user;
    }

    @Test
    void getActiveSessionsByUser_long_queryIsOk() {
        long userId = aLong();
        helper.getActiveSessionsByUser(userId);
        assertTrue(true);
    }

    @Test
    void getBlockedSessionsByUser_IamUser_queryIsOk() {
        IamUser user = myIamUser();
        helper.getBlockedSessionsByUser(user);
        assertTrue(true);
    }

    @Test
    void getBlockedSessionsByUser_long_queryIsOk() {
        long userId = aLong();
        helper.getBlockedSessionsByUser(userId);
        assertTrue(true);
    }

    @Test
    void isAllFileSigned_HsmSessioneFirma_queryIsOk() {
        HsmSessioneFirma session = aHsmSessioneFirma();
        helper.isAllFileSigned(session);
        assertTrue(true);
    }

    @Test
    void isAllFileSigned_long_queryIsOk() {
        long sessionId = aLong();
        helper.isAllFileSigned(sessionId);
        assertTrue(true);
    }

    @Test
    void findElencoFascSes_HsmSessioneFirma_long_queryIsOk() {
        HsmSessioneFirma session = aHsmSessioneFirma();
        long idElenco = aLong();
        helper.findElencoFascSes(session, idElenco);
        assertTrue(true);
    }

    @Test
    void findElencoFascSes_long_long_queryIsOk() {
        long sessionId = aLong();
        long idElenco = aLong();
        helper.findElencoFascSes(sessionId, idElenco);
        assertTrue(true);
    }
}
