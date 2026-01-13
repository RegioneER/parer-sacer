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
package it.eng.parer.job.allineamentoOrganizzazioni.ejb;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aListOfLong;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import it.eng.parer.job.allineamentoOrganizzazioni.utils.CostantiReplicaOrg;
import it.eng.parer.web.helper.HelperTest;

@ArquillianTest
public class AllineamentoOrganizzazioniHelperTest {
    @EJB
    private AllineamentoOrganizzazioniHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(
                AllineamentoOrganizzazioniHelperTest.class.getSimpleName(),
                HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""),
                        AllineamentoOrganizzazioniHelperTest.class,
                        AllineamentoOrganizzazioniHelper.class, CostantiReplicaOrg.class));
    }

    @Test
    public void getIamOrganizDaReplic_queryIsOk() {
        helper.getIamOrganizDaReplic();
        assertTrue(true);
    }

    @Test
    public void getOrgAmbiente_queryIsOk() {
        BigDecimal idAmbiente = aBigDecimal();
        helper.getOrgAmbiente(idAmbiente);
        assertTrue(true);
    }

    @Test
    public void getOrgEnte_queryIsOk() {
        BigDecimal idEnte = aBigDecimal();
        helper.getOrgEnte(idEnte);
        assertTrue(true);
    }

    @Test
    public void getOrgStrut_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        helper.getOrgStrut(idStrut);
        assertTrue(true);
    }

    @Test
    public void getDecTipoUnitaDocList_queryIsOk() {
        List<Long> idStruts = aListOfLong(2);
        helper.getDecTipoUnitaDocList(idStruts);
        assertTrue(true);
    }

    @Test
    public void getDecTipoDocPrincipaliList_queryIsOk() {
        List<Long> idStruts = aListOfLong(2);
        helper.getDecTipoDocPrincipaliList(idStruts);
        assertTrue(true);
    }

    @Test
    public void getDecTipoDocList_queryIsOk() {
        List<Long> idStruts = aListOfLong(2);
        helper.getDecTipoDocList(idStruts);
        assertTrue(true);
    }

    @Test
    public void getDecRegistroUnitaDocList_queryIsOk() {
        List<Long> idStruts = aListOfLong(2);
        helper.getDecRegistroUnitaDocList(idStruts);
        assertTrue(true);
    }

    @Test
    public void getOrgSubStrutList_queryIsOk() {
        List<Long> idStruts = aListOfLong(2);
        helper.getOrgSubStrutList(idStruts);
        assertTrue(true);
    }

    @Test
    public void getDecTipoFascicoloList_queryIsOk() {
        List<Long> idStruts = aListOfLong(2);
        helper.getDecTipoFascicoloList(idStruts);
        assertTrue(true);
    }

    @Test
    public void getEnteConvenzInfo_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        helper.getEnteConvenzInfo(idStrut);
        assertTrue(true);
    }

}
