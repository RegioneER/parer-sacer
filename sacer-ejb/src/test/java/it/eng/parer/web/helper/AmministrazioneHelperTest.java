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

import it.eng.parer.volume.helper.VolumeHelperTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author manuel.bertuzzi@eng.it
 */

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class AmministrazioneHelperTest {
    @EJB
    private AmministrazioneHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(AmministrazioneHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), AmministrazioneHelperTest.class,
                        AmministrazioneHelper.class));
    }

    @Test
    public void getConfigurationTypes_queryIsOk() {
        assertNotNull(helper.getConfigurationTypes());
    }

    @Test
    public void getConfigurationViewBean_queryIsOk() {
        assertNotNull(helper.getConfigurationViewBean(aString()));
    }

    @Test
    public void getAplParamApplicList_queryIsOk() {
        String tiParamApplic = aString();
        String tiGestioneParam = aString();
        String flAppartApplic = aString();
        String flAppartAmbiente = aString();
        String flAppartStrut = aString();
        String flAppartTipoUnitaDoc = aString();
        String flAppartAaTipoFascicolo = aString();
        assertNotNull(helper.getAplParamApplicList(tiParamApplic, tiGestioneParam, flAppartApplic, flAppartAmbiente,
                flAppartStrut, flAppartTipoUnitaDoc, flAppartAaTipoFascicolo));
    }

    @Test
    public void existsAplParamApplic_queryIsOk() {
        String nmParamApplic = aString();
        BigDecimal idParamApplic = aBigDecimal();
        assertFalse(helper.existsAplParamApplic(nmParamApplic, idParamApplic));
    }

    @Test
    public void getAplValoreParamApplic_queryIsOk() {
        long idParamApplic = aLong();
        String tiAppart = aString();
        helper.getAplValoreParamApplic(idParamApplic, tiAppart);
        assertTrue(true);
    }

    @Test
    public void getTiParamApplic_queryIsOk() {
        List<String> tiParamApplic = helper.getTiParamApplic();
        assertNotNull(tiParamApplic);
        assertFalse(tiParamApplic.isEmpty());
    }

    @Test
    public void getAplParamApplicListAmbiente_queryIsOk() {
        assertNotNull(helper.getAplParamApplicListAmbiente(aListOfString(2)));
    }

    @Test
    public void getAplParamApplicListStruttura_queryIsOk() {
        assertNotNull(helper.getAplParamApplicListStruttura(aListOfString(2)));
    }

    @Test
    public void getAplParamApplicListTipoUd_queryIsOk() {
        assertNotNull(helper.getAplParamApplicListTipoUd(aListOfString(2)));
    }

    @Test
    public void getAplParamApplicListAaTipoFascicolo_queryIsOk() {
        assertNotNull(helper.getAplParamApplicListAaTipoFascicolo(aListOfString(2)));
    }

    @Test
    public void getAplParamApplicMultiListAmbiente_queryIsOk() {
        assertNotNull(helper.getAplParamApplicMultiListAmbiente());
    }

    @Test
    public void getAplValoreParamApplic6args_queryIsOk() {
        BigDecimal idParamApplic = aBigDecimal();
        String tiAppart = aString();
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        BigDecimal idAaTipoFascicolo = aBigDecimal();
        helper.getAplValoreParamApplic(idParamApplic, tiAppart, idAmbiente, idStrut, idTipoUnitaDoc, idAaTipoFascicolo);
        assertTrue(true);
    }

    @Test
    public void getAplValParamApplicMultiList_queryIsOk() {
        BigDecimal idParamApplic = aBigDecimal();
        BigDecimal idAmbiente = aBigDecimal();
        assertNotNull(helper.getAplValParamApplicMultiList(idParamApplic, idAmbiente));
    }

    @Test
    public void getAplValParamApplicMulti_queryIsOk() {
        BigDecimal idParamApplic = aBigDecimal();
        BigDecimal idAmbiente = aBigDecimal();
        String token = aString();
        helper.getAplValParamApplicMulti(idParamApplic, idAmbiente, token);
        assertTrue(true);
    }

}
