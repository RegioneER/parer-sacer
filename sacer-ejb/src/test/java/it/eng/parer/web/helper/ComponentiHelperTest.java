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

import it.eng.parer.firma.xml.VFTipoControlloType;
import it.eng.parer.slite.gen.viewbean.AroVRicCompTableBean;
import it.eng.parer.slite.gen.viewbean.ElvVListaCompElvTableBean;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import static it.eng.parer.web.helper.HelperTest.createSacerLogJar;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author manuel.bertuzzi@eng.it
 */

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class ComponentiHelperTest {
    @EJB
    private ComponentiHelper helper;

    @Test
    public void getAroCompDocRowBean() {
        helper.getAroCompDocRowBean(BigDecimal.ONE, BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    public void testGetBlobboByteList_queryIsOk() {
        try {
            helper.getBlobboByteList(BigDecimal.ONE);
        } catch (Exception e) {
            // non trova nessun FirCertifCa con id 1
            assertExceptionMessage(e, "NullPointerException");
        }
    }

    @Test
    public void testRetrieveListaCRL_queryIsOk() {
        helper.retrieveListaCRL(BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    public void testRetrieveListaCertificatiCA_queryIsOk() {
        helper.retrieveListaCertificatiCA(BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    public void testGetAroVVisCompRowBean_queryIsOk() {
        helper.getAroVVisCompRowBean(BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    public void testGetAroVLisFirmaCompTableBean_queryIsOk() {
        helper.getAroVLisFirmaCompTableBean(BigDecimal.ONE, 1);
        assertTrue(true);
    }

    @Test
    public void testGetAroVLisMarcaCompTableBean_queryIsOk() {
        helper.getAroVLisMarcaCompTableBean(BigDecimal.ONE, 1);
        assertTrue(true);
    }

    @Test
    public void testGetAroVVisMarcaCompRowBean_queryIsOk() {
        helper.getAroVVisMarcaCompRowBean(BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    public void testGetAroVLisCertifCaMarcaCompTableBean_queryIsOk() {
        for (VFTipoControlloType controlloType : VFTipoControlloType.values()) {
            helper.getAroVLisCertifCaMarcaCompTableBean(BigDecimal.ONE, controlloType.name());
        }
        assertTrue(true);
    }

    @Test
    public void testGetAroVVisFirmaCompRowBean_queryIsOk() {
        helper.getAroVVisFirmaCompRowBean(BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    public void testGetAroVLisCertifCaFirmaCompTableBean_queryIsOk() {
        for (VFTipoControlloType controlloType : VFTipoControlloType.values()) {
            helper.getAroVLisCertifCaFirmaCompTableBean(BigDecimal.ONE, controlloType.name());
        }
        assertTrue(true);
    }

    @Test
    public void testGetAroVLisControfirmaFirmaTableBean_queryIsOk() {
        helper.getAroVLisControfirmaFirmaTableBean(BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    public void testIsComponenteInElenco_queryIsOk() {
        helper.isComponenteInElenco(BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    public void testIsComponenteInVolume_queryIsOk() {
        helper.isComponenteInVolume(BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    public void testGetAroVVisCompVolRowBean_queryIsOk() {
        helper.getAroVVisCompVolRowBean(BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    public void testGetAroCompDocsByIdUnitaDoc_queryIsOk() {
        helper.getAroCompDocsByIdUnitaDoc(1L);
        assertTrue(true);
    }

    @Test
    public void testIsAroUnitaDocReferredByOtherAroCompDocs_queryIsOk() {
        helper.isAroUnitaDocReferredByOtherAroCompDocs(1L, BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    public void getAroVRicompViewBean_jpqlIsValid() {
        BigDecimal idStrut = BigDecimal.ONE;
        Date[] dateAcquisizioneValidate = aDateArray(2);
        int maxResults = 100;
        final String registro = "A";
        final BigDecimal anno = BigDecimal.ONE;
        final String codice = "A";
        final BigDecimal annoRangeDa = BigDecimal.ONE;
        final BigDecimal annoRangeA = BigDecimal.ONE;
        final String codiceRangeDa = "A";
        final String codiceRangeA = "A";
        final String idComp = "A";
        final BigDecimal tipoStrutDoc = BigDecimal.ONE;
        final BigDecimal tipoCompDoc = BigDecimal.ONE;
        final String formato = "A";
        BigDecimal fileSizeDa = BigDecimal.ONE;
        final BigDecimal fileSizeA = BigDecimal.ONE;
        final String presenza = "A";
        final String conformita = "A";
        final String esitoFirme = "A";
        final Timestamp dtScadFirmaCompDa = todayTs();
        final Timestamp dtScadFirmaCompA = tomorrowTs();
        final String flRifTempVers = "A";
        final String dsRifTempVers = "A";
        final String dsNomeCompVers = "A";
        final String flHashVers = "A";
        final String dsHashFileVers = "A";
        final String nmMimetypeFile = "A";
        final String dlUrnCompVers = "A";
        final String dsFormatoRapprCalc = "A";
        final String dsFormatoRapprEstesoCalc = "A";
        final String forzaAcc = "A";
        final String forzaConserva = "A";
        final String tiEsitoContrFormatoFile = "A";
        final String dsHashFileCalc = "A";
        final String dsAlgoHashFileCalc = "A";
        final String cdEncodingHashFileCalc = "A";
        final String dsUrnCompCalc = "A";
        final String tiSupportoComp = "A";
        final String nmTipoRapprComp = "A";
        final List<BigDecimal> subStruts = new ArrayList<>();
        final Set<Object> keySet = new HashSet<Object>(0);
        Set<BigDecimal> idTipoUnitaDocSet = emptySet();
        Set<BigDecimal> idTipoDocSet = emptySet();
        AroVRicCompTableBean tableBean = helper.getAroVRicCompViewBeanSimpleTypeParameters(idStrut,
                dateAcquisizioneValidate, maxResults, registro, anno, codice, annoRangeDa, annoRangeA, codiceRangeDa,
                codiceRangeA, idComp, tipoStrutDoc, tipoCompDoc, formato, fileSizeDa, fileSizeA, presenza, conformita,
                esitoFirme, dtScadFirmaCompDa, dtScadFirmaCompA, flRifTempVers, dsRifTempVers, dsNomeCompVers,
                flHashVers, dsHashFileVers, nmMimetypeFile, dlUrnCompVers, dsFormatoRapprCalc, dsFormatoRapprEstesoCalc,
                forzaAcc, forzaConserva, tiEsitoContrFormatoFile, dsHashFileCalc, dsAlgoHashFileCalc,
                cdEncodingHashFileCalc, dsUrnCompCalc, tiSupportoComp, nmTipoRapprComp, subStruts, keySet,
                idTipoUnitaDocSet, idTipoDocSet);
        assertNotNull(tableBean);
    }

    @Test
    public void testGetElvVListaCompElvViewBean_jpqlIsValid() {
        System.out.println("getElvVListaCompElvViewBean");
        BigDecimal idElencoVers = aBigDecimal();
        long idUtente = aLong();
        BigDecimal idStrut = aBigDecimal();
        ElvVListaCompElvTableBean tableBean = helper.getElvVListaCompElvViewBean(idElencoVers, idUtente, idStrut);
        assertNotNull(tableBean);
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(ComponentiHelperTest.class.getSimpleName(),
                createPaginatorJavaArchive(), createSacerLogJar(),
                createSacerJavaArchive(Collections.emptyList(), ComponentiHelper.class, ComponentiHelperTest.class,
                        it.eng.parer.firma.xml.VFTipoControlloType.class, it.eng.parer.web.util.BlobObject.class,
                        it.eng.parer.web.util.Transform.class, it.eng.parer.crypto.model.CryptoEnums.class));
    }
}
