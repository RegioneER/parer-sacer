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

package it.eng.parer.crypto.helper;

import it.eng.parer.entity.HsmSessioneFirma;
import it.eng.parer.entity.IamUser;
import it.eng.parer.firma.crypto.helper.ElenchiIndiciAipSignatureHelper;
import it.eng.parer.firma.crypto.helper.SigningHelper;
import it.eng.parer.job.indiceAipSerieUd.helper.CreazioneIndiceAipSerieUdHelperTest;
import it.eng.parer.web.helper.ConfigurationHelper;

import it.eng.parer.web.helper.HelperTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.assertTrue;

import javax.ejb.EJB;

import org.junit.Test;

import java.util.Arrays;

public class ElenchiIndiciAipSignatureHelperTest extends HelperTest<ElenchiIndiciAipSignatureHelper> {
    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(ElenchiIndiciAipSignatureHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), ElenchiIndiciAipSignatureHelperTest.class,
                        ElenchiIndiciAipSignatureHelper.class, SigningHelper.class, ConfigurationHelper.class));
    }

    @Test
    public void createSessioneFirma_queryIsOk() {
        long userId = 5000L;// admin_generale
        helper.createSessioneFirma(userId);
        assertTrue(true);
    }

    @Test
    public void getActiveSessionsByUser_IamUser_queryIsOk() {
        IamUser user = null;
        helper.getActiveSessionsByUser(user);
        assertTrue(true);
    }

    @Test
    public void getActiveSessionsByUser_long_queryIsOk() {
        long userId = aLong();
        helper.getActiveSessionsByUser(userId);
        assertTrue(true);
    }

    @Test
    public void getBlockedSessionsByUser_IamUser_queryIsOk() {
        IamUser user = null;
        helper.getBlockedSessionsByUser(user);
        assertTrue(true);
    }

    @Test
    public void getBlockedSessionsByUser_long_queryIsOk() {
        long userId = aLong();
        helper.getBlockedSessionsByUser(userId);
        assertTrue(true);
    }

    @Test
    public void isAllFileSigned_HsmSessioneFirma_queryIsOk() {
        HsmSessioneFirma session = null;
        helper.isAllFileSigned(session);
        assertTrue(true);
    }

    @Test
    public void isAllFileSigned_long_queryIsOk() {
        long sessionId = aLong();
        helper.isAllFileSigned(sessionId);
        assertTrue(true);
    }

    @Test
    public void findElencoSessione_HsmSessioneFirma_long_queryIsOk() {
        HsmSessioneFirma session = null;
        long idElenco = aLong();
        helper.findElencoSessione(session, idElenco);
        assertTrue(true);
    }

    @Test
    public void findElencoSessione_long_long_queryIsOk() {
        long sessionId = aLong();
        long idElenco = aLong();
        helper.findElencoSessione(sessionId, idElenco);
        assertTrue(true);
    }
}
