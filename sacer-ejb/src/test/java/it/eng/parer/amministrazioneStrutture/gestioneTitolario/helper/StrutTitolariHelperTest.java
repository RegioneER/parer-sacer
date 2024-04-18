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

package it.eng.parer.amministrazioneStrutture.gestioneTitolario.helper;

import it.eng.parer.async.helper.CalcoloMonitoraggioHelperTest;
import it.eng.parer.web.helper.HelperTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import javax.ejb.EJB;

import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class StrutTitolariHelperTest {
    @EJB
    private StrutTitolariHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(StrutTitolariHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), StrutTitolariHelperTest.class,
                        StrutTitolariHelper.class));
    }

    @Test
    public void existChiaveUd_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String cdRegistroKeyUnitaDoc = aString();
        BigDecimal aaKeyUnitaDoc = aBigDecimal();
        String cdKeyUnitaDoc = aString();
        Date dtDocInvio = todayTs();
        helper.existChiaveUd(idStrut, cdRegistroKeyUnitaDoc, aaKeyUnitaDoc, cdKeyUnitaDoc, dtDocInvio);
        assertTrue(true);
    }

    @Test
    public void getVociTree_queryIsOk() {
        BigDecimal idTitol = aBigDecimal();
        Date dtVal = tomorrowTs();
        helper.getVociTree(idTitol, dtVal);
        assertTrue(true);
    }

    @Test
    public void getVociAllPadri_queryIsOk() {
        BigDecimal idNodo = aBigDecimal();
        Date dtVal = tomorrowTs();
        helper.getVociAllPadri(idNodo, dtVal);
        assertTrue(true);
    }

    @Test
    public void getOperTitol_queryIsOk() {
        BigDecimal idTitol = aBigDecimal();
        Date dtVal = tomorrowTs();
        helper.getOperTitol(idTitol, dtVal);
        assertTrue(true);
    }

    @Test
    public void getTracciaList_queryIsOk() {
        BigDecimal idVoceTitol = aBigDecimal();
        Date dtVal = tomorrowTs();
        helper.getTracciaList(idVoceTitol, dtVal);
        assertTrue(true);
    }

    @Test
    public void getLivelliList_queryIsOk() {
        BigDecimal idTitol = aBigDecimal();
        Set<BigDecimal> niLivelloToExclude = aSetOfBigDecimal(2);
        helper.getLivelliList(idTitol, niLivelloToExclude);
        assertTrue(true);
    }

    @Test
    public void getLivello_queryIsOk() {
        BigDecimal niLivello = aBigDecimal();
        BigDecimal idTitol = aBigDecimal();
        helper.getLivello(niLivello, idTitol);
        assertTrue(true);
    }

    @Test
    public void getVoce_queryIsOk() {
        Long idTitol = aLong();
        String cdVoceComposito = aString();
        try {
            helper.getVoce(idTitol, cdVoceComposito);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void getLastDecValVoceTitol_queryIsOk() {
        long idVoceTitol = aLong();
        helper.getLastDecValVoceTitol(idVoceTitol);
        assertTrue(true);
    }

    @Test
    public void getDecVoceTitol_queryIsOk() {
        BigDecimal idVoceTitol = aBigDecimal();
        helper.getDecVoceTitol(idVoceTitol);
        assertTrue(true);
    }

    @Test
    public void getUnitaDoc_queryIsOk() {
        String cdRegistroKeyUnitaDoc = aString();
        BigDecimal aaKeyUnitaDoc = aBigDecimal();
        String cdKeyUnitaDoc = aString();
        BigDecimal idStrut = aBigDecimal();
        helper.getUnitaDoc(cdRegistroKeyUnitaDoc, aaKeyUnitaDoc, cdKeyUnitaDoc, idStrut);
        assertTrue(true);
    }

    @Test
    public void existTitolario_3args_1_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String nmTitol = aString();
        BigDecimal idTitol = aBigDecimal();
        helper.existTitolario(idStrut, nmTitol, idTitol);
        assertTrue(true);
    }

    @Test
    public void existTitolario_3args_2_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        Date dtIniVal = todayTs();
        Date dtFinVal = tomorrowTs();
        helper.existTitolario(idStrut, dtIniVal, dtFinVal);
        assertTrue(true);
    }

    @Test
    public void existVoce_queryIsOk() {
        String cdVoceComposito = aString();
        Long idTitol = aLong();
        helper.existVoce(cdVoceComposito, idTitol);
        assertTrue(true);
    }

    @Test
    public void getDecTitol_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        boolean filterValid = false;
        helper.getDecTitol(idStrut, filterValid);
        assertTrue(true);
    }

    @Test
    public void getDecVoceTitols_queryIsOk() {
        BigDecimal idCriterioRaggrFasc = aBigDecimal();
        String tiSel = aString();
        boolean filterValid = false;
        helper.getDecVoceTitols(idCriterioRaggrFasc, tiSel, filterValid);
        assertTrue(true);
    }
}
