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

/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.web.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.assertNoResultException;
import static it.eng.ArquillianUtils.tomorrowTs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import it.eng.parer.grantedEntity.UsrUser;

@ArquillianTest
public class UserHelperTest {
    @EJB
    private UserHelper helper;

    @Deployment
    public static Archive<?> createTestArchive_queryIsOk() {
        return HelperTest.createEnterpriseArchive(UserHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Collections.singletonList(""),
                        UserHelperTest.class, UserHelper.class));
    }

    private final Long noUserId = -1L;
    private final String noUsername = "nonEsiste";

    @Test
    void findUsrUser_queryIsOk() {
        try {
            helper.findUsrUser(noUsername);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void findIamUser_queryIsOk() {
        try {
            helper.findIamUser(noUsername);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void findIamUserList_queryIsOk() {
        try {
            helper.findIamUserList(noUsername);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void findUserById_queryIsOk() {
        try {
            helper.findUserById(noUserId);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void getOrgAmbienteById_queryIsOk() {
        BigDecimal idAmbiente = aBigDecimal();
        helper.getOrgAmbienteById(idAmbiente);
        assertTrue(true);
    }

    @Test
    void getOrgEnteById_queryIsOk() {
        BigDecimal idEnte = aBigDecimal();
        helper.getOrgEnteById(idEnte);
        assertTrue(true);
    }

    @Test
    void getOrgStrutById_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        helper.getOrgStrutById(idStrut);
        assertTrue(true);
    }

    @Test
    void getTipoUnitaDocById_queryIsOk() {
        BigDecimal idTipoUd = aBigDecimal();
        helper.getTipoUnitaDocById(idTipoUd);
        assertTrue(true);
    }

    @Test
    void getRegUnitaDocById_queryIsOk() {
        BigDecimal idRegUd = aBigDecimal();
        helper.getRegUnitaDocById(idRegUd);
        assertTrue(true);
    }

    @Test
    @Disabled("modifica i dati su db, rischioso")
    void resetPwd_3args_queryIsOk() {
        long idUtente = noUserId;
        String randomPwd = aString();
        Date scad = tomorrowTs();
        try {
            helper.resetPwd(idUtente, randomPwd, scad);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    @Disabled("modifica i dati su db, rischioso")
    void resetPwd_long_String_queryIsOk() {
        long idUtente = noUserId;
        String randomPwd = aString();
        try {
            helper.resetPwd(idUtente, randomPwd);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void getAmbienti_queryIsOk() {
        helper.getAmbienti();
        assertTrue(true);
    }

    @Test
    void checkEnteConvenzionatoAppart_queryIsOk() {
        helper.checkEnteConvenzionatoAppart(noUserId);
        assertTrue(true);
    }

    @Test
    void findUtentiPerUsernameCaseInsensitive() {
        final List<UsrUser> users = helper.findUtentiPerUsernameCaseInsensitive("admin_generale");
        assertEquals(1, users.size());
    }
}
