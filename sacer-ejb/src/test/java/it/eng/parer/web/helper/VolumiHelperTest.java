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

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.todayTs;
import static it.eng.ArquillianUtils.tomorrowTs;
import static it.eng.parer.web.helper.HelperTest.createSacerLogJar;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;

import it.eng.ArquillianUtils;
import it.eng.parer.volume.utils.VolumeEnums;
import org.junit.runner.RunWith;

/**
 * @author manuel.bertuzzi@eng.it
 */
@RunWith(Arquillian.class)
public class VolumiHelperTest {

    @EJB
    private VolumiHelper helper;

    @Test
    public void ejbInject_ok() {
        Assert.assertNotNull(helper);
    }

    @Test
    public void getVolVRicVolumeTB_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        Assert.assertNotNull(helper.getVolVRicVolumeTB(idStrut, 1));
    }

    @Test
    public void getVolVRicVolumeViewBean_queryIsOk() {
        BigDecimal idStrut = BigDecimal.ONE;
        int maxResults = 100;
        String stato = aString();
        BigDecimal idVolume = BigDecimal.ONE;
        String nmVolumeConserv = aString();
        String dsVolumeConserv = aString();
        String creatoMan = aString();
        Timestamp dtCreazioneDa = todayTs();
        Timestamp dtCreazioneA = tomorrowTs();
        String registro = aString();
        BigDecimal anno = aBigDecimal();
        String codice = aString();
        BigDecimal anno_range_da = aBigDecimal();
        BigDecimal anno_range_a = anno_range_da.add(BigDecimal.ONE);
        String codice_range_da = aString();
        String codice_range_a = aString();
        String criterio = aString();
        String presenza = aString();
        String validita = aString();
        String ntVolumeChiuso = aString();
        String ntIndiceVolume = aString();
        Assert.assertNotNull(helper.getVolVRicVolumeViewBeanPlainFilters(idStrut, maxResults, stato, idVolume,
                nmVolumeConserv, dsVolumeConserv, creatoMan, dtCreazioneDa, dtCreazioneA, registro, anno, codice,
                anno_range_da, anno_range_a, codice_range_da, codice_range_a, criterio, presenza, validita,
                ntVolumeChiuso, ntIndiceVolume));
    }

    @Test
    public void findVolVRicVolume_queryIsOk() {
        BigDecimal idVol = aBigDecimal();
        Assert.assertNotNull(helper.findVolVRicVolume(idVol));
    }

    @Test
    public void retrieveUserById_queryIsOk() {
        Assert.assertNotNull(helper.retrieveUserById(1L));
    }

    @Test
    public void existNomeVolume_queryIsOk() {
        String nome = "fake";
        BigDecimal idStruttura = BigDecimal.ZERO;
        Assert.assertFalse(helper.existNomeVolume(nome, idStruttura));
    }

    @Test
    public void getOrgStrutRowBean_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        Assert.assertNotNull(helper.getOrgStrutRowBean(idStrut));
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        return ArquillianUtils.createEnterpriseArchive(
                VolumiHelperTest.class.getSimpleName(), ArquillianUtils.createSacerJavaArchive(Collections.emptyList(),
                        VolumiHelper.class, VolumeEnums.class, VolumiHelperTest.class),
                HelperTest.createPaginatorJavaArchive(), createSacerLogJar());
    }
}
