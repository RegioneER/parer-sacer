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
package it.eng.parer.ws.recupero.ejb;

import it.eng.parer.entity.*;
import it.eng.parer.entity.constraint.VrsUrnXmlSessioneVers;
import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.RispostaControlli;
import java.math.BigDecimal;
import java.util.Arrays;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author manuel.bertuzzi@eng.it
 */
public class ControlliRecuperoTest extends HelperTest<ControlliRecupero> {

    @Test
    public void leggiUnitaDocQueryIsOk() {
        Long idUnitaDoc = 28L;
        RispostaControlli rispostaControlli = helper.leggiUnitaDoc(idUnitaDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiVolumeConservQueryIsOk() {
        Long idVolumeConserv = 1L;
        RispostaControlli rispostaControlli = helper.leggiVolumeConserv(idVolumeConserv);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiVolumiUnitaDocQueryIsOk() {
        Long idUnitaDoc = 28L;
        RispostaControlli rispostaControlli = helper.leggiVolumiUnitaDoc(idUnitaDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiCompFileInUDQueryIsOk() {
        Long idUnitaDoc = 28L;
        RispostaControlli rispostaControlli = helper.leggiCompFileInUD(idUnitaDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiCompFileInUDAIPV2QueryIsOk() {
        Long idUnitaDoc = 28L;
        RispostaControlli rispostaControlli = helper.leggiCompFileInUDAIPV2(idUnitaDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiCompFileInUDVersamentoUdQueryIsOk() {
        Long idUnitaDoc = 28L;
        RispostaControlli rispostaControlli = helper.leggiCompFileInUDVersamentoUd(idUnitaDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiCompFileInUDByTipoDocQueryIsOk() {
        Long idUnitaDoc = 28L;
        Long idTipoDoc = 7L;
        RispostaControlli rispostaControlli = helper.leggiCompFileInUDByTipoDoc(idUnitaDoc, idTipoDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void getChiaveUdQueryIsOk() {
        AroUnitaDoc aroUnitaDoc = new AroUnitaDoc();
        aroUnitaDoc.setIdUnitaDoc(28L);
        aroUnitaDoc.setAaKeyUnitaDoc(BigDecimal.ZERO);
        helper.getChiaveUd(aroUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void leggiCompFileInDocQueryIsOk() {
        long idDoc = 39L;
        RispostaControlli rispostaControlli = helper.leggiCompFileInDoc(idDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiCompFileInCompQueryIsOk() {
        Long idComp = 71L;
        RispostaControlli rispostaControlli = helper.leggiCompFileInComp(idComp);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiChiaveUnitaDocQueryIsOk() {
        Long idUnitaDoc = 28L;
        RispostaControlli rispostaControlli = helper.leggiChiaveUnitaDoc(idUnitaDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiVersatoreUnitaDocQueryIsOk() {
        Long idUnitaDoc = 28L;
        RispostaControlli rispostaControlli = helper.leggiVersatoreUnitaDoc(idUnitaDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiXMLSessioneversUdQueryIsOk() {
        Long idUnitaDoc = 28L;
        RispostaControlli rispostaControlli = helper.leggiXMLSessioneversUd(idUnitaDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiXMLSessioneversDocQueryIsOk() {
        long idDoc = 39L;
        RispostaControlli rispostaControlli = helper.leggiXMLSessioneversDoc(idDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    @Ignore("richiama leggiXMLSessioneversDoc ma chiede un id realmente presente su db se no da nullpointer")
    public void leggiXMLSessioneversCompQueryIsOk() {
        Long idComp = 71L;
        RispostaControlli rispostaControlli = helper.leggiXMLSessioneversComp(idComp);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiXMLSessioneVersUdPrincipaleQueryIsOk() {
        Long idUnitaDoc = 28L;
        RispostaControlli rispostaControlli = helper.leggiXMLSessioneVersUdPrincipale(idUnitaDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiXMLSessioneVersDocAggiuntoQueryIsOk() {
        long idDoc = 39L;
        RispostaControlli rispostaControlli = helper.leggiXMLSessioneVersDocAggiunto(idDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiXMLSessioneversUpdQueryIsOk() {
        Long idUnitaDoc = 28L;
        RispostaControlli rispostaControlli = helper.leggiXMLSessioneversUpd(idUnitaDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void contaXMLIndiceAIPQueryIsOk() {
        Long idUnitaDoc = 28L;
        RispostaControlli rispostaControlli = helper.contaXMLIndiceAIP(idUnitaDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiXMLIndiceAIPQueryIsOk() {
        Long idUnitaDoc = 28L;
        RispostaControlli rispostaControlli = helper.leggiXMLIndiceAIP(idUnitaDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiXMLIndiceAIPV2QueryIsOk() {
        Long idUnitaDoc = 28L;
        RispostaControlli rispostaControlli = helper.leggiXMLIndiceAIPV2(idUnitaDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiXMLIndiceAIPExternalQueryIsOk() {
        Long idUnitaDoc = 28L;
        RispostaControlli rispostaControlli = helper.leggiXMLIndiceAIPExternal(idUnitaDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiElvFileElencoVersQueryIsOk() {
        Long idUnitaDoc = 28L;
        String tiFileElencoVers = "BOH";
        String tiFileElencoVers2 = "BOH2";
        RispostaControlli rispostaControlli = helper.leggiElvFileElencoVers(idUnitaDoc, tiFileElencoVers,
                tiFileElencoVers2);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiAroUdAppartVerSerieQueryIsOk() {
        Long idUnitaDoc = 28L;
        RispostaControlli rispostaControlli = helper.leggiAroUdAppartVerSerie(idUnitaDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiFasUnitaDocFascicoloQueryIsOk() {
        Long idUnitaDoc = 28L;
        RispostaControlli rispostaControlli = helper.leggiFasUnitaDocFascicolo(idUnitaDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void checkIdDocumentoinUDQueryIsOk() {
        Long idUnitaDoc = 28L;
        String idDocumento = "TEST";
        RispostaControlli rispostaControlli = helper.checkIdDocumentoinUD(idUnitaDoc, idDocumento);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void checkIdComponenteinDocQueryIsOk() {
        long idDoc = 39L;
        long progressivo = -9L;
        RispostaControlli rispostaControlli = helper.checkIdComponenteinDoc(idDoc, progressivo);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void checkTipoDocumentoperStrutQueryIsOk() {
        long idStrut = 2L;
        String nmTipoDoc = "DELIBERA";
        String descChiaveUd = "TEST";
        RispostaControlli rispostaControlli = helper.checkTipoDocumentoperStrut(idStrut, nmTipoDoc, descChiaveUd);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void checkTipoDocumentoinUDQueryIsOk() {
        Long idUnitaDoc = 28L;
        String nmTipoDoc = "TEST";
        String descChiaveUd = "TEST";
        RispostaControlli rispostaControlli = helper.checkTipoDocumentoinUD(idUnitaDoc, nmTipoDoc, descChiaveUd);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    @Ignore("fa delle persist, faccio il test dei metodi privati by reflection")
    public void calcCdKeyNormAndUrnPregQueryIsOk() {
        CSVersatore csvVersatore = new CSVersatore();
        CSChiave cschiave = new CSChiave();
        long idRegistro = -9L;
        long idUnitaDoc = -9L;
        String cdKey = "TEST";
        RispostaControlli rispostaControlli = helper.calcCdKeyNormAndUrnPreg(csvVersatore, cschiave, idRegistro,
                idUnitaDoc, cdKey);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiFirReportIdsAndGenZipNameQueryIsOk() {
        long idCompDoc = 71L;
        RispostaControlli rispostaControlli = helper.leggiFirReportIdsAndGenZipName(idCompDoc);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiListaUserByHsmUsernameQueryIsOk() {
        BigDecimal idAmbiente = BigDecimal.ZERO;
        RispostaControlli rispostaControlli = helper.leggiListaUserByHsmUsername(idAmbiente);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    public void leggiRuoloAuthorizedSignerQueryIsOk() {
        long idUseriamCor = -9L;
        BigDecimal idAmbiente = BigDecimal.ZERO;
        RispostaControlli rispostaControlli = helper.leggiRuoloAuthorizedSigner(idUseriamCor, idAmbiente);
        assertNotEquals("666", rispostaControlli.getCodErr());
    }

    @Test
    @Ignore("metodo private")
    public void checkCdKeyNormalizedQueryIsOk() {
        long idRegistro = 0L;
        CSChiave key = new CSChiave();
        key.setAnno(2021L);
        key.setNumero("1124");
        String cdKeyUnitaDocNormaliz = "TEST";
        // RispostaControlli rispostaControlli = helper.checkCdKeyNormalized(idRegistro, key, cdKeyUnitaDocNormaliz);
        // assertNotEquals("666", rispostaControlli.getCodErr());
        assertTrue(true);
    }

    @Test
    @Ignore("metodo private")
    public void getDtMaxVersMaxByUdQueryIsOk() {
        Long idUnitaDoc = 28L;
        // RispostaControlli rispostaControlli = helper.getDtMaxVersMaxByUd(idUnitaDoc);
        // assertNotEquals("666", rispostaControlli.getCodErr());
        assertTrue(true);
    }

    @Test
    public void findVrsUrnXmlSessioneVersByTiUrnQueryIsOk() {
        final VrsXmlDatiSessioneVers vrsXmlDatiSessioneVers = new VrsXmlDatiSessioneVers();
        vrsXmlDatiSessioneVers.setIdXmlDatiSessioneVers(0L);
        try {
            helper.findVrsUrnXmlSessioneVersByTiUrn(vrsXmlDatiSessioneVers,
                    VrsUrnXmlSessioneVers.TiUrnXmlSessioneVers.ORIGINALE);
        } catch (Exception e) {
            assertExceptionMessage(e, "NoResultException");
        }
    }

    @Test
    public void getTipoSessioneFromQueryIsOk() {
        final VrsXmlDatiSessioneVers vrsXmlDatiSessioneVers = new VrsXmlDatiSessioneVers();
        vrsXmlDatiSessioneVers.setIdXmlDatiSessioneVers(1L);
        final VrsDatiSessioneVers vrsDatiSessioneVers = new VrsDatiSessioneVers();
        vrsDatiSessioneVers.setIdDatiSessioneVers(1L);
        vrsXmlDatiSessioneVers.setVrsDatiSessioneVers(vrsDatiSessioneVers);
        final Constants.TipoSessione tipoSessioneFrom = helper.getTipoSessioneFrom(vrsXmlDatiSessioneVers);
        assertEquals(Constants.TipoSessione.VERSAMENTO, tipoSessioneFrom);
        assertTrue(true);
    }

    @Test
    public void loadElvElencoVerQueryIsOk() {
        final ElvFileElencoVer fileElencoVers = new ElvFileElencoVer();
        fileElencoVers.setElvElencoVer(new ElvElencoVer());
        fileElencoVers.getElvElencoVer().setIdElencoVers(1L);
        try {
            helper.loadElvElencoVer(fileElencoVers);
        } catch (Exception e) {
            assertExceptionMessage(e, "NoResultException");
        }
    }

    @Test
    public void findVolFileVolumeConservQueryIsOk() {
        final VolVolumeConserv volume = new VolVolumeConserv();
        volume.setIdVolumeConserv(1L);
        helper.findVolFileVolumeConserv(volume);
        assertTrue(true);
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        return createEnterpriseArchive(ControlliRecuperoTest.class.getSimpleName(), createSacerLogJar(),
                createPaginatorJavaArchive(), createSacerLogJar(),
                createSacerJavaArchive(
                        Arrays.asList("it.eng.parer.web.dto", "it.eng.parer.ws.dto", "it.eng.parer.ws.recupero.dto",
                                "it.eng.parer.elencoVersamento.utils", "it.eng.parer.job.dto", "it.eng.parer.ws.utils",
                                "com.fasterxml.uuid", "com.fasterxml.uuid.impl"),
                        ControlliRecuperoTest.class, it.eng.parer.ws.recupero.ejb.ControlliRecupero.class,
                        it.eng.parer.volume.helper.VolumeHelper.class,
                        it.eng.parer.web.helper.UnitaDocumentarieHelper.class,
                        it.eng.parer.volume.utils.ReturnParams.class, it.eng.parer.web.helper.ConfigurationHelper.class,
                        it.eng.parer.web.util.Constants.class,
                        it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper.class,
                        it.eng.parer.util.helper.UniformResourceNameUtilHelper.class,
                        it.eng.parer.web.helper.UserHelper.class, it.eng.parer.web.helper.AmministrazioneHelper.class,
                        it.eng.spagoCore.util.UUIDMdcLogUtil.class));
    }
}
