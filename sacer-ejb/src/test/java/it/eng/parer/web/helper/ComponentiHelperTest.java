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
import static it.eng.ArquillianUtils.aDateArray;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.assertExceptionMessage;
import static it.eng.ArquillianUtils.createPaginatorJavaArchive;
import static it.eng.ArquillianUtils.createSacerJavaArchive;
import static it.eng.ArquillianUtils.emptySet;
import static it.eng.ArquillianUtils.todayTs;
import static it.eng.ArquillianUtils.tomorrowTs;
import static it.eng.parer.web.helper.HelperTest.createSacerLogJar;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.firma.xml.VFTipoControlloType;
import it.eng.parer.slite.gen.viewbean.AroVRicCompTableBean;
import it.eng.parer.slite.gen.viewbean.ElvVListaCompElvTableBean;

@ArquillianTest
public class ComponentiHelperTest {
    @EJB
    private ComponentiHelper helper;

    @Test
    void getAroCompDocRowBean() {
        helper.getAroCompDocRowBean(BigDecimal.ONE, BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    void testGetBlobboByteList_queryIsOk() {
        try {
            helper.getBlobboByteList(BigDecimal.ONE);
        } catch (Exception e) {
            // non trova nessun FirCertifCa con id 1
            assertExceptionMessage(e, "NullPointerException");
        }
    }

    @Test
    void testRetrieveListaCRL_queryIsOk() {
        helper.retrieveListaCRL(BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    void testRetrieveListaCertificatiCA_queryIsOk() {
        helper.retrieveListaCertificatiCA(BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    void testGetAroVVisCompRowBean_queryIsOk() {
        helper.getAroVVisCompRowBean(BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    void testGetAroVLisFirmaCompTableBean_queryIsOk() {
        helper.getAroVLisFirmaCompTableBean(BigDecimal.ONE, 1);
        assertTrue(true);
    }

    @Test
    void testGetAroVLisMarcaCompTableBean_queryIsOk() {
        helper.getAroVLisMarcaCompTableBean(BigDecimal.ONE, 1);
        assertTrue(true);
    }

    @Test
    void testGetAroVVisMarcaCompRowBean_queryIsOk() {
        helper.getAroVVisMarcaCompRowBean(BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    void testGetAroVLisCertifCaMarcaCompTableBean_queryIsOk() {
        for (VFTipoControlloType controlloType : VFTipoControlloType.values()) {
            helper.getAroVLisCertifCaMarcaCompTableBean(BigDecimal.ONE, controlloType.name());
        }
        assertTrue(true);
    }

    @Test
    void testGetAroVVisFirmaCompRowBean_queryIsOk() {
        helper.getAroVVisFirmaCompRowBean(BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    void testGetAroVLisCertifCaFirmaCompTableBean_queryIsOk() {
        for (VFTipoControlloType controlloType : VFTipoControlloType.values()) {
            helper.getAroVLisCertifCaFirmaCompTableBean(BigDecimal.ONE, controlloType.name());
        }
        assertTrue(true);
    }

    @Test
    void testGetAroVLisControfirmaFirmaTableBean_queryIsOk() {
        helper.getAroVLisControfirmaFirmaTableBean(BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    void testIsComponenteInElenco_queryIsOk() {
        helper.isComponenteInElenco(BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    void testIsComponenteInVolume_queryIsOk() {
        helper.isComponenteInVolume(BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    void testGetAroVVisCompVolRowBean_queryIsOk() {
        helper.getAroVVisCompVolRowBean(BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    void testGetAroCompDocsByIdUnitaDoc_queryIsOk() {
        helper.getAroCompDocsByIdUnitaDoc(1L);
        assertTrue(true);
    }

    @Test
    void testIsAroUnitaDocReferredByOtherAroCompDocs_queryIsOk() {
        helper.isAroUnitaDocReferredByOtherAroCompDocs(1L, BigDecimal.ONE);
        assertTrue(true);
    }

    @Test
    void getAroVRicompViewBean_jpqlIsValid() {
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
                dateAcquisizioneValidate, maxResults, registro, anno, codice, annoRangeDa,
                annoRangeA, codiceRangeDa, codiceRangeA, idComp, tipoStrutDoc, tipoCompDoc, formato,
                fileSizeDa, fileSizeA, presenza, conformita, esitoFirme, dtScadFirmaCompDa,
                dtScadFirmaCompA, flRifTempVers, dsRifTempVers, dsNomeCompVers, flHashVers,
                dsHashFileVers, nmMimetypeFile, dlUrnCompVers, dsFormatoRapprCalc,
                dsFormatoRapprEstesoCalc, forzaAcc, forzaConserva, tiEsitoContrFormatoFile,
                dsHashFileCalc, dsAlgoHashFileCalc, cdEncodingHashFileCalc, dsUrnCompCalc,
                tiSupportoComp, nmTipoRapprComp, subStruts, keySet, idTipoUnitaDocSet,
                idTipoDocSet);
        assertNotNull(tableBean);
    }

    @Test
    public void testGetElvVListaCompElvViewBean_jpqlIsValid() {
        System.out.println("getElvVListaCompElvViewBean");
        BigDecimal idElencoVers = aBigDecimal();
        Long idSubStrut = aLong();
        ElvVListaCompElvTableBean tableBean = helper.getElvVListaCompElvViewBean(idElencoVers,
                idSubStrut);
        assertNotNull(tableBean);
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(ComponentiHelperTest.class.getSimpleName(),
                createPaginatorJavaArchive(), createSacerLogJar(),
                createSacerJavaArchive(Collections.emptyList(), ComponentiHelper.class,
                        ComponentiHelperTest.class,
                        it.eng.parer.firma.xml.VFTipoControlloType.class,
                        it.eng.parer.web.util.BlobObject.class,
                        it.eng.parer.web.util.Transform.class,
                        it.eng.parer.crypto.model.CryptoEnums.class));
    }
}
