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
package it.eng.parer.web.helper;

import it.eng.parer.grantedEntity.UsrUser;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author manuel.bertuzzi@eng.it
 */

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class UserHelperTest {
    @EJB
    private UserHelper helper;

    @Deployment
    public static Archive<?> createTestArchive_queryIsOk() {
        return HelperTest.createEnterpriseArchive(UserHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(), HelperTest
                        .createSacerJavaArchive(Collections.singletonList(""), UserHelperTest.class, UserHelper.class));
    }

    private final Long noUserId = -1L;
    private final String noUsername = "nonEsiste";

    @Test
    public void findUsrUser_queryIsOk() {
        try {
            helper.findUsrUser(noUsername);
            Assert.assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void findIamUser_queryIsOk() {
        try {
            helper.findIamUser(noUsername);
            Assert.assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void findIamUserList_queryIsOk() {
        try {
            helper.findIamUserList(noUsername);
            Assert.assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void findUserById_queryIsOk() {
        try {
            helper.findUserById(noUserId);
            Assert.assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void getOrgAmbienteById_queryIsOk() {
        BigDecimal idAmbiente = aBigDecimal();
        helper.getOrgAmbienteById(idAmbiente);
        Assert.assertTrue(true);
    }

    @Test
    public void getOrgEnteById_queryIsOk() {
        BigDecimal idEnte = aBigDecimal();
        helper.getOrgEnteById(idEnte);
        Assert.assertTrue(true);
    }

    @Test
    public void getOrgStrutById_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        helper.getOrgStrutById(idStrut);
        Assert.assertTrue(true);
    }

    @Test
    public void getTipoUnitaDocById_queryIsOk() {
        BigDecimal idTipoUd = aBigDecimal();
        helper.getTipoUnitaDocById(idTipoUd);
        Assert.assertTrue(true);
    }

    @Test
    public void getRegUnitaDocById_queryIsOk() {
        BigDecimal idRegUd = aBigDecimal();
        helper.getRegUnitaDocById(idRegUd);
        Assert.assertTrue(true);
    }

    @Test
    @Ignore("modifica i dati su db, rischioso")
    public void resetPwd_3args_queryIsOk() {
        long idUtente = noUserId;
        String randomPwd = aString();
        Date scad = tomorrowTs();
        try {
            helper.resetPwd(idUtente, randomPwd, scad);
            Assert.assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    @Ignore("modifica i dati su db, rischioso")
    public void resetPwd_long_String_queryIsOk() {
        long idUtente = noUserId;
        String randomPwd = aString();
        try {
            helper.resetPwd(idUtente, randomPwd);
            Assert.assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void getAmbienti_queryIsOk() {
        helper.getAmbienti();
        Assert.assertTrue(true);
    }

    @Test
    public void checkEnteConvenzionatoAppart_queryIsOk() {
        helper.checkEnteConvenzionatoAppart(noUserId);
        Assert.assertTrue(true);
    }

    @Test
    public void findUtentiPerUsernameCaseInsensitive() {
        final List<UsrUser> users = helper.findUtentiPerUsernameCaseInsensitive("admin_generale");
        Assert.assertEquals(1, users.size());
    }
}
