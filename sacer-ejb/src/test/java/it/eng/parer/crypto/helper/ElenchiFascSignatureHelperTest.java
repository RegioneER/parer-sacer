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

import static it.eng.ArquillianUtils.aLong;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.entity.HsmSessioneFirma;
import it.eng.parer.entity.IamUser;
import it.eng.parer.firma.crypto.helper.ElenchiFascSignatureHelper;
import it.eng.parer.firma.crypto.helper.SigningHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.HelperTest;

@ArquillianTest
public class ElenchiFascSignatureHelperTest {

    @EJB
    private ElenchiFascSignatureHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return HelperTest.createEnterpriseArchive(
		ElenchiFascSignatureHelperTest.class.getSimpleName(),
		HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
		HelperTest.createSacerJavaArchive(Arrays.asList(""),
			ElenchiFascSignatureHelperTest.class, ElenchiFascSignatureHelper.class,
			SigningHelper.class, ConfigurationHelper.class));
    }

    @Test
    public void getActiveSessionsByUser_IamUser_queryIsOk() {
	IamUser user = myUserIam();
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
	IamUser user = myUserIam();
	helper.getBlockedSessionsByUser(user);
	assertTrue(true);
    }

    private IamUser myUserIam() {
	IamUser user = new IamUser();
	user.setIdUserIam(5000L);
	return user;
    }

    @Test
    public void getBlockedSessionsByUser_long_queryIsOk() {
	long userId = 5000L;
	final List<HsmSessioneFirma> blockedSessionsByUser = helper
		.getBlockedSessionsByUser(userId);
	assertTrue(true);
    }

    @Test
    public void isAllFileSigned_HsmSessioneFirma_queryIsOk() {
	HsmSessioneFirma session = aHsmSessioneFirma();
	helper.isAllFileSigned(session);
	assertTrue(true);
    }

    private HsmSessioneFirma aHsmSessioneFirma() {
	HsmSessioneFirma session = new HsmSessioneFirma();
	session.setIdSessioneFirma(aLong());
	return session;
    }

    @Test
    public void isAllFileSigned_long_queryIsOk() {
	long sessionId = aLong();
	helper.isAllFileSigned(sessionId);
	assertTrue(true);
    }

    @Test
    public void findElencoSessione_queryIsOk() {
	HsmSessioneFirma session = null;
	long idElenco = aLong();
	helper.findElencoSessione(session, idElenco);
	assertTrue(true);
    }

    @Test
    public void findElencoFascSes_queryIsOk() {
	long sessionId = aLong();
	long idElenco = aLong();
	helper.findElencoFascSes(sessionId, idElenco);
	assertTrue(true);
    }
}
