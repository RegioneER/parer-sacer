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

package it.eng.parer.web.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aDateArray;
import static it.eng.ArquillianUtils.aFlag;
import static it.eng.ArquillianUtils.aListOfBigDecimal;
import static it.eng.ArquillianUtils.aListOfString;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aSetOfBigDecimal;
import static it.eng.ArquillianUtils.aSetOfString;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.anAroUnitaDoc;
import static it.eng.ArquillianUtils.assertNoResultException;
import static it.eng.ArquillianUtils.todayTs;
import static it.eng.ArquillianUtils.tomorrowTs;
import static it.eng.parer.web.helper.HelperTest.createSacerLogJar;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import it.eng.ArquillianUtils;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.constraint.AroCompUrnCalc;
import it.eng.parer.entity.constraint.DecModelloXsdUd;
import it.eng.parer.util.Utils;
import it.eng.parer.volume.helper.VolumeHelper;
import it.eng.parer.volume.utils.DatiSpecQueryParams;
import it.eng.parer.volume.utils.ReturnParams;
import it.eng.parer.web.dto.DecCriterioAttribBean;
import it.eng.parer.web.dto.DecCriterioDatiSpecBean;
import it.eng.parer.web.dto.DefinitoDaBean;
import it.eng.parer.web.util.Constants;

@ArquillianTest
public class UnitaDocumentarieHelperTest {
    @EJB
    private UnitaDocumentarieHelper helper;

    @Test
    void getAroVRicUnitaDocRowBean_queryIsOk() {
        BigDecimal idUnitaDoc = aBigDecimal();
        BigDecimal idStruttura = aBigDecimal();
        String statoDoc = aString();

        helper.getAroVRicUnitaDocRowBean(idUnitaDoc, idStruttura, statoDoc);
        assertTrue(true);
    }

    @Test
    @Disabled("è un metodo wrapper usato solo per bypassare la paginazione del framework, testo direttamente il metodo getAroVRicUnitaDocRicSempliceViewBean")
    void getAroVRicUnitaDocRicSempliceViewBeanNoLimitQueryIsOk() {
        assertTrue(true);
    }

    @Test
    void getAroVRicUnitaDocRicSempliceViewBeanPlainFilter_queryIsOk() {
        UnitaDocumentarieHelper.FiltriUnitaDocumentarieSemplicePlain filtri = aFiltriUnitaDocumentarieSemplicePlain();
        List<BigDecimal> idTipoUnitaDocList = aListOfBigDecimal(2);
        List<String> cdRegistroUnitaDocList = aListOfString(2);
        List<BigDecimal> idTipoDocList = aListOfBigDecimal(2);
        Date[] dateAcquisizioneValidate = aDateArray(2);
        Date[] dateUnitaDocValidate = aDateArray(2);
        BigDecimal idStruttura = aBigDecimal();
        int maxResults = 10;
        helper.getAroVRicUnitaDocRicSempliceViewBeanPlainFilter(idTipoUnitaDocList,
                cdRegistroUnitaDocList, idTipoDocList, dateAcquisizioneValidate,
                dateUnitaDocValidate, idStruttura, filtri, maxResults, false);
        assertTrue(true);
    }

    @Test
    void getAroVRicUnitaDocRicSempliceNuovaViewBeanPlainFilter_queryIsOk() {
        UnitaDocumentarieHelper.FiltriUnitaDocumentarieSemplicePlain filtri = aFiltriUnitaDocumentarieSemplicePlain();
        List<BigDecimal> idTipoUnitaDocList = aListOfBigDecimal(2);
        List<String> cdRegistroUnitaDocList = aListOfString(2);
        List<BigDecimal> idTipoDocList = aListOfBigDecimal(2);
        Date[] dateAcquisizioneValidate = aDateArray(2);
        Date[] dateUnitaDocValidate = aDateArray(2);
        BigDecimal idStruttura = aBigDecimal();
        int maxResults = 10;
        helper.getAroVRicUnitaDocRicSempliceViewBeanNewPlainFilter(idTipoUnitaDocList,
                cdRegistroUnitaDocList, idTipoDocList, dateAcquisizioneValidate,
                dateUnitaDocValidate, idStruttura, filtri, maxResults, false);
        assertTrue(true);
    }

    private UnitaDocumentarieHelper.FiltriUnitaDocumentarieSemplicePlain aFiltriUnitaDocumentarieSemplicePlain() {
        UnitaDocumentarieHelper.FiltriUnitaDocumentarieSemplicePlain filtri = new UnitaDocumentarieHelper.FiltriUnitaDocumentarieSemplicePlain();
        filtri.setAnno(aBigDecimal());
        filtri.setAnnoRangeA(aBigDecimal());
        filtri.setAnnoRangeDa(aBigDecimal());
        filtri.setAutoreDocMeta(aString());
        filtri.setCdKeyDocVers(aString());
        filtri.setCdVersioneXsdDoc(aString());
        filtri.setCdVersioneXsdUd(aString());
        filtri.setCodice(aString());
        filtri.setCodiceRangeA(aString());
        filtri.setCodiceRangeDa(aString());
        filtri.setDescrizioneDocMeta(aString());
        filtri.setDocAggiunti(aString());
        filtri.setEsito(aString());
        filtri.setFlHashVers(aFlag());
        filtri.setForzaAcc(aFlag());
        filtri.setForzaColleg(aFlag());
        filtri.setForzaConserva(aFlag());
        filtri.setOggettoMeta(aString());
        filtri.setPresenza(aFlag());
        filtri.setStatoConserva(aString());
        filtri.setSubStruts(aListOfBigDecimal(2));
        filtri.setTiDoc(aString());
        filtri.setTipoConservazione(aString());
        filtri.setTiStatoUdElencoVers(aString());
        filtri.setUnitaDocAnnul(aString());
        filtri.setProfiloNorm("PROFILO_NORM");
        return filtri;
    }

    @Test
    @Disabled("è un metodo wrapper usato solo per bypassare la paginazione del framework, testo direttamente il metodo getAroVRicUnitaDocRicAvanzataViewBean")
    void getAroVRicUnitaDocRicAvanzataViewBeanNoLimitQueryIsOk() {
        assertTrue(true);
    }

    @Test
    void getAroVRicUnitaDocRicAvanzataViewBeanPlainFilter_queryIsOk() {

        List<BigDecimal> idTipoUnitaDocList = aListOfBigDecimal(2);
        Set<String> cdRegistroUnitaDocSet = aSetOfString(2);
        List<BigDecimal> idTipoDocList = aListOfBigDecimal(2);
        List<DecCriterioDatiSpecBean> listaDatiSpecOnLine = Collections.emptyList();
        Date[] dateAcquisizioneValidate = aDateArray(2);
        Date[] dateCreazioneCompValidate = aDateArray(2);
        BigDecimal idStruttura = aBigDecimal();
        UnitaDocumentarieHelper.FiltriCollegamentiUnitaDocumentariePlain filtriCollegamenti = aFiltriCollegamentiUnitaDocumentariePlain();
        UnitaDocumentarieHelper.FiltriUnitaDocumentarieAvanzataPlain filtri = aFiltriUnitaDocumentarieAvanzataPlain();
        UnitaDocumentarieHelper.FiltriComponentiUnitaDocumentariePlain filtriComponenti = aFiltriComponentiUnitaDocumentariePlain();
        UnitaDocumentarieHelper.FiltriFirmatariUnitaDocumentariePlain filtriFirmatari = aFiltriFirmatariUnitaDocumentariePlain();
        UnitaDocumentarieHelper.FiltriFascicoliUnitaDocumentariePlain filtriFascicoli = aFiltriFascicoliUnitaDocumentariePlain();
        boolean addButton = true;

        helper.getAroVRicUnitaDocRicAvanzataViewBeanPlainFilter(idTipoUnitaDocList,
                cdRegistroUnitaDocSet, idTipoDocList, listaDatiSpecOnLine, dateAcquisizioneValidate,
                dateCreazioneCompValidate, idStruttura, addButton, filtri, filtriCollegamenti,
                filtriFirmatari, filtriComponenti, filtriFascicoli, false);
        assertTrue(true);
    }

    private UnitaDocumentarieHelper.FiltriFascicoliUnitaDocumentariePlain aFiltriFascicoliUnitaDocumentariePlain() {
        UnitaDocumentarieHelper.FiltriFascicoliUnitaDocumentariePlain filtriFascicoli = new UnitaDocumentarieHelper.FiltriFascicoliUnitaDocumentariePlain();
        filtriFascicoli.setAaFascicolo(aBigDecimal());
        filtriFascicoli.setAaFascicoloA(BigDecimal.TEN);
        filtriFascicoli.setAaFascicoloDa(BigDecimal.ZERO);
        filtriFascicoli.setCdCompositoVoceTitol(aString());
        filtriFascicoli.setCdKeyFascicolo(aString());
        filtriFascicoli.setCdKeyFascicoloA(aString());
        filtriFascicoli.setCdKeyFascicoloDa(aString());
        filtriFascicoli.setDsOggettoFascicolo(aString());
        filtriFascicoli.setDtApeFascicoloA(tomorrowTs());
        filtriFascicoli.setDtApeFascicoloDa(todayTs());
        filtriFascicoli.setDtChiuFascicoloA(tomorrowTs());
        filtriFascicoli.setDtChiuFascicoloDa(todayTs());
        filtriFascicoli.setNmTipoFascicolo(aBigDecimal());
        return filtriFascicoli;
    }

    private UnitaDocumentarieHelper.FiltriComponentiUnitaDocumentariePlain aFiltriComponentiUnitaDocumentariePlain() {
        UnitaDocumentarieHelper.FiltriComponentiUnitaDocumentariePlain filtriComponenti = new UnitaDocumentarieHelper.FiltriComponentiUnitaDocumentariePlain();
        filtriComponenti.setCdEncodingHashFileCalc(aString());
        filtriComponenti.setDlUrnCompVers(aString());
        filtriComponenti.setDsAlgoHashFileCalc(aString());
        filtriComponenti.setDsFormatoRapprCalc(aString());
        filtriComponenti.setDsFormatoRapprEstesoCalc(aString());
        filtriComponenti.setDsHashFileCalc(aString());
        filtriComponenti.setDsHashFileVers(aString());
        filtriComponenti.setDsIdCompVers(aString());
        filtriComponenti.setDsNomeCompVers(aString());
        filtriComponenti.setDsRifTempVers(aString());
        filtriComponenti.setDsUrnCompCalc(aString());
        filtriComponenti.setDtScadFirmaCompA(tomorrowTs());
        filtriComponenti.setDtScadFirmaCompDa(todayTs());
        filtriComponenti.setFlCompFirmato(aFlag());
        filtriComponenti.setFlForzaAccettazioneComp(aFlag());
        filtriComponenti.setFlForzaConservazioneComp(aFlag());
        filtriComponenti.setFlRifTempVers(aFlag());
        filtriComponenti.setNiSizeFileA(aBigDecimal());
        filtriComponenti.setNiSizeFileDa(aBigDecimal());
        filtriComponenti.setNmFormatoFileVers(aBigDecimal());
        filtriComponenti.setNmMimetypeFile(aString());
        filtriComponenti.setNmTipoCompDoc(aBigDecimal());
        filtriComponenti.setNmTipoRapprComp(aBigDecimal());
        filtriComponenti.setNmTipoStrutDoc(aBigDecimal());
        filtriComponenti.setTiEsitoContrConforme(aFlag());
        filtriComponenti.setTiEsitoContrFormatoFile(aFlag());
        filtriComponenti.setTiEsitoVerifFirma(aFlag());
        filtriComponenti.setTiEsitoVerifFirmeChiuse(aFlag());
        filtriComponenti.setTiSupportoComp(aFlag());
        return filtriComponenti;
    }

    private UnitaDocumentarieHelper.FiltriFirmatariUnitaDocumentariePlain aFiltriFirmatariUnitaDocumentariePlain() {
        UnitaDocumentarieHelper.FiltriFirmatariUnitaDocumentariePlain filtriFirmatari = new UnitaDocumentarieHelper.FiltriFirmatariUnitaDocumentariePlain();
        // filtriFirmatari.setConFirmatario(aFlag());
        filtriFirmatari.setCdFirmatario(aString());
        filtriFirmatari.setNmCognomeFirmatario(aString());
        filtriFirmatari.setNmFirmatario(aString());
        return filtriFirmatari;
    }

    private UnitaDocumentarieHelper.FiltriUnitaDocumentarieAvanzataPlain aFiltriUnitaDocumentarieAvanzataPlain() {
        UnitaDocumentarieHelper.FiltriUnitaDocumentarieAvanzataPlain filtri = new UnitaDocumentarieHelper.FiltriUnitaDocumentarieAvanzataPlain();
        filtri.setAaKeyUnitaDoc(aBigDecimal());
        filtri.setAaKeyUnitaDocA(BigDecimal.TEN);
        filtri.setAaKeyUnitaDocDa(BigDecimal.ONE);
        filtri.setCdFascic(aString());
        filtri.setCdKeyDocVers(aString());
        filtri.setCdKeyUnitaDoc(aString());
        filtri.setCdKeyUnitaDocA(aString());
        filtri.setCdKeyUnitaDocDa(aString());
        filtri.setCdSottofascic(aString());
        filtri.setCdVersioneXsdDoc(aString());
        filtri.setCdVersioneXsdUd(aString());
        filtri.setDataMetaA(tomorrowTs());
        filtri.setDataMetaDa(todayTs());
        filtri.setDlDoc(aString());
        filtri.setDlOggettoUnitaDoc(aString());
        filtri.setDsAutoreDoc(aString());
        filtri.setDsClassif(aString());
        filtri.setDsOggettoFascic(aString());
        filtri.setDsOggettoSottofascic(aString());
        filtri.setFlDocAggiunti(aFlag());
        filtri.setFlForzaAccettazione(aFlag());
        filtri.setFlForzaCollegamento(aFlag());
        filtri.setFlForzaConservazione(aFlag());
        filtri.setFlHashVers(aFlag());
        filtri.setFlUnitaDocAnnul(aFlag());
        filtri.setFlUnitaDocFirmato(aFlag());
        filtri.setNmSistemaMigraz(aListOfString(2));
        filtri.setNmSubStrut(aListOfBigDecimal(2));
        filtri.setTiConservazione(aString());
        filtri.setTiDoc(aString());
        filtri.setTiEsitoVerifFirme(aListOfString(2));
        filtri.setTiStatoConservazione(aString());
        filtri.setTiStatoUdElencoVers(aString());
        filtri.setProfiloNorm("PROFILO_NORM");
        return filtri;
    }

    private UnitaDocumentarieHelper.FiltriCollegamentiUnitaDocumentariePlain aFiltriCollegamentiUnitaDocumentariePlain() {
        UnitaDocumentarieHelper.FiltriCollegamentiUnitaDocumentariePlain filtriCollegamenti = new UnitaDocumentarieHelper.FiltriCollegamentiUnitaDocumentariePlain();
        filtriCollegamenti.setAaKeyUnitaDocLink(aBigDecimal());
        filtriCollegamenti.setCdKeyUnitaDocLink(aString());
        filtriCollegamenti.setCdRegistroKeyUnitaDocLink(aString());
        filtriCollegamenti.setCollegamentoRisolto(aFlag());
        filtriCollegamenti.setConCollegamento(aFlag());
        filtriCollegamenti.setDsLinkUnitaDoc(aString());
        filtriCollegamenti.setDsLinkUnitaDocOggetto(aString());
        filtriCollegamenti.setIsOggettoCollegamento(aFlag());
        return filtriCollegamenti;
    }

    @Test
    void getAroVRicUnitaDocsById_queryIsOk() {
        List<BigDecimal> ids = aListOfBigDecimal(2);

        helper.getAroVRicUnitaDocsById(ids);
        assertTrue(true);
    }

    @Test
    void getAroVLisDocTableBeanByIdDoc_queryIsOk() {
        Set<BigDecimal> idDocSet = aSetOfBigDecimal(2);

        helper.getAroVLisDocTableBeanByIdDoc(idDocSet);
        assertTrue(true);
    }

    @Test
    void getElvVLisUpdUdTableBeanByIdUpd_queryIsOk() {
        Set<BigDecimal> idUpdUnitaDocSet = aSetOfBigDecimal(2);

        helper.getElvVLisUpdUdTableBeanByIdUpd(idUpdUnitaDocSet);
        assertTrue(true);
    }

    @Test
    void getAroVVisUnitaDocIamRowBean_queryIsOk() {
        BigDecimal idud = aBigDecimal();
        try {
            helper.getAroVVisUnitaDocIamRowBean(idud);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void getAroVLisDocTableBean_queryIsOk() {
        BigDecimal idud = aBigDecimal();
        String tipoDoc = aString();

        helper.getAroVLisDocTableBean(idud, tipoDoc);
        assertTrue(true);
    }

    @Test
    void getAroVVisDocAggIamTableBean_queryIsOk() {
        BigDecimal idud = aBigDecimal();
        Date dataCreazioneUd = todayTs();

        helper.getAroVVisDocAggIamTableBean(idud, dataCreazioneUd);
        assertTrue(true);
    }

    @Test
    void getAroVLisLinkUnitaDocTableBean_queryIsOk() {
        BigDecimal idud = aBigDecimal();
        int maxResults = 100;

        helper.getAroVLisLinkUnitaDocTableBean(idud, maxResults);
        assertTrue(true);
    }

    @Test
    void getAroVLisArchivUnitaDocTableBean_queryIsOk() {
        BigDecimal idud = aBigDecimal();
        int maxResults = 100;

        helper.getAroVLisArchivUnitaDocTableBean(idud, maxResults);
        assertTrue(true);
    }

    @Test
    void getAroVVisDocIamRowBean_queryIsOk() {
        BigDecimal iddoc = aBigDecimal();

        helper.getAroVVisDocIamRowBean(iddoc);
        assertTrue(true);
    }

    @Test
    void getAroVVisDocIamTableBean_queryIsOk() {
        BigDecimal iddoc = aBigDecimal();

        helper.getAroVVisDocIamTableBean(iddoc);
        assertTrue(true);
    }

    @Test
    void getAroVVisUpdUnitaDocRowBean_queryIsOk() {
        BigDecimal idupdunitadoc = aBigDecimal();

        helper.getAroVVisUpdUnitaDocRowBean(idupdunitadoc);
        assertTrue(true);
    }

    @Test
    void getAroVVisUpdUnitaDocTableBean_queryIsOk() {
        BigDecimal idupdunitadoc = aBigDecimal();

        helper.getAroVVisUpdUnitaDocTableBean(idupdunitadoc);
        assertTrue(true);
    }

    @Test
    void getAroVLisCompDocTableBean_queryIsOk() {
        BigDecimal iddoc = aBigDecimal();
        int maxResults = 10;

        helper.getAroVLisCompDocTableBean(iddoc, maxResults);
        assertTrue(true);
    }

    @Test
    void getAroVLisUpdDocUnitaDocTableBean_queryIsOk() {
        BigDecimal idupd = aBigDecimal();
        int maxResults = 10;

        helper.getAroVLisUpdDocUnitaDocTableBean(idupd, maxResults);
        assertTrue(true);
    }

    @Test
    void getAroVLisVolNoValDocTableBean_queryIsOk() {
        BigDecimal iddoc = aBigDecimal();
        int maxResults = 10;

        helper.getAroVLisVolNoValDocTableBean(iddoc, maxResults);
        assertTrue(true);
    }

    @Test
    void getAroVLisUpdCompUnitaDocTableBean_queryIsOk() {
        BigDecimal idupd = aBigDecimal();
        int maxResults = 10;

        helper.getAroVLisUpdCompUnitaDocTableBean(idupd, maxResults);
        assertTrue(true);
    }

    @Test
    void getAroVLisUpdKoRisoltiTableBean_queryIsOk() {
        BigDecimal idupd = aBigDecimal();
        int maxResults = 10;

        helper.getAroVLisUpdKoRisoltiTableBean(idupd, maxResults);
        assertTrue(true);
    }

    @Test
    void getAroWarnUpdUnitaDocTableBean_queryIsOk() {
        BigDecimal idupd = aBigDecimal();
        int maxResults = 10;

        helper.getAroWarnUpdUnitaDocTableBean(idupd, maxResults);
        assertTrue(true);
    }

    @Test
    void getVersioniXsd_queryIsOk() {
        BigDecimal idAttribDatiSpec = aBigDecimal();
        BigDecimal id = aBigDecimal();
        for (Constants.TipoEntitaSacer tipoEntitaSacer : Constants.TipoEntitaSacer.values()) {
            helper.getVersioniXsd(idAttribDatiSpec, id, tipoEntitaSacer);
            assertTrue(true);
        }
    }

    @Test
    void getVersioniXsdSisMigr_queryIsOk() {
        BigDecimal idAttribDatiSpec = aBigDecimal();
        String tipoEntitaSacer = aString();
        String nmSistemaMigraz = aString();

        helper.getVersioniXsdSisMigr(idAttribDatiSpec, tipoEntitaSacer, nmSistemaMigraz);
        assertTrue(true);
    }

    @Test
    void getDecAttribDatiSpecTableBean_queryIsOk() {
        BigDecimal id = aBigDecimal();
        for (Constants.TipoEntitaSacer tipoEntitaSacer : Constants.TipoEntitaSacer.values()) {
            helper.getDecAttribDatiSpecTableBean(id, tipoEntitaSacer);
            assertTrue(true);
        }
    }

    @Test
    void getDecAttribDatiSpecSisMigrTableBean_queryIsOk() {
        String tipoSistemaMigrazione = aString();
        BigDecimal idStrut = aBigDecimal();

        helper.getDecAttribDatiSpecSisMigrTableBean(tipoSistemaMigrazione, idStrut);
        assertTrue(true);
    }

    @Test
    void getAroVLisCompDocFileTableBean_queryIsOk() {
        BigDecimal iddoc = aBigDecimal();

        helper.getAroVLisCompDocFileTableBean(iddoc);
        assertTrue(true);
    }

    @Test
    void getAroVLisDatiSpecTableBean_queryIsOk() {
        BigDecimal identificativo = aBigDecimal();
        String tipoDatiSpec = aString();
        int maxResults = 100;
        for (Constants.TipoEntitaSacer tipoEntitaSacer : Constants.TipoEntitaSacer.values()) {
            helper.getAroVLisDatiSpecTableBean(identificativo, tipoEntitaSacer, tipoDatiSpec,
                    maxResults);
            assertTrue(true);
        }
    }

    @Test
    void getUrnCalc_queryIsOk() {
        long idud = aLong();
        try {
            helper.getUrnCalc(idud);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void getStatiDoc_queryIsOk() {
        BigDecimal idUnitaDoc = aBigDecimal();

        helper.getStatiDoc(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    void getDecTipoDocRowBean_queryIsOk() {
        BigDecimal idTipoDoc = aBigDecimal();

        helper.getDecTipoDocRowBean(idTipoDoc);
        assertTrue(true);
    }

    @Test
    void getDecTipoUnitaDocRowBean_queryIsOk() {
        BigDecimal idTipoUnitaDoc = aBigDecimal();

        helper.getDecTipoUnitaDocRowBean(idTipoUnitaDoc);
        assertTrue(true);
    }

    @Test
    void getTipoSaveFile_queryIsOk() {
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        try {
            helper.getTipoSaveFile(idTipoUnitaDoc);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void isDocumentoAggiunto_queryIsOk() {
        BigDecimal idDoc = aBigDecimal();
        try {
            helper.isDocumentoAggiunto(idDoc);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void getAroVerIndiceAipUdTableBean_queryIsOk() {
        BigDecimal idUnitaDoc = aBigDecimal();
        int maxResults = 10;

        helper.getAroVerIndiceAipUdTableBean(idUnitaDoc, maxResults);
        assertTrue(true);
    }

    @Test
    void getAroFileVerIndiceAipUdRowBean_queryIsOk() {
        BigDecimal idVerIndiceAip = aBigDecimal();
        int maxResults = 10;

        helper.getAroFileVerIndiceAipUdRowBean(idVerIndiceAip, maxResults);
        assertTrue(true);
    }

    @Test
    void getAroVLisVolCorViewBean_queryIsOk() {
        BigDecimal idUnitaDoc = aBigDecimal();

        helper.getAroVLisVolCorViewBean(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    void getAroVLisElvVerViewBean_queryIsOk() {
        BigDecimal idUnitaDoc = aBigDecimal();

        helper.getAroVLisElvVerViewBean(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    void getLastIdUnitaDocAnnullata_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String cdRegistroKeyUnitaDoc = aString();
        BigDecimal aaKeyUnitaDoc = aBigDecimal();
        String cdKeyUnitaDoc = aString();

        helper.getLastIdUnitaDocAnnullata(idStrut, cdRegistroKeyUnitaDoc, aaKeyUnitaDoc,
                cdKeyUnitaDoc);
        assertTrue(true);
    }

    @Test
    void getIdUnitaDocVersataNoAnnul_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String cdRegistroKeyUnitaDoc = aString();
        BigDecimal aaKeyUnitaDoc = aBigDecimal();
        String cdKeyUnitaDoc = aString();

        helper.getIdUnitaDocVersataNoAnnul(idStrut, cdRegistroKeyUnitaDoc, aaKeyUnitaDoc,
                cdKeyUnitaDoc);
        assertTrue(true);
    }

    @Test
    void existAroUnitaDoc_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String cdRegistroKeyUnitaDoc = aString();
        BigDecimal aaKeyUnitaDoc = aBigDecimal();
        String cdKeyUnitaDoc = aString();

        helper.existAroUnitaDoc(idStrut, cdRegistroKeyUnitaDoc, aaKeyUnitaDoc, cdKeyUnitaDoc);
        assertTrue(true);
    }

    @Test
    void getSerieAppartenenzaList_queryIsOk() {
        BigDecimal idUnitaDoc = aBigDecimal();

        helper.getSerieAppartenenzaList(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    void getFascicoliAppartenenzaViewBean_queryIsOk() {
        BigDecimal idUnitaDoc = aBigDecimal();

        helper.getFascicoliAppartenenzaViewBean(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    void getAggiornamentiMetadatiTableBean_queryIsOk() {
        BigDecimal idUnitaDoc = aBigDecimal();

        helper.getAggiornamentiMetadatiTableBean(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    void progressivoEsistenteUnitaDocList_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idUnitaDoc = aBigDecimal();
        String cdRegistroKeyUnitaDoc = aString();
        BigDecimal aaKeyUnitaDoc = aBigDecimal();
        BigDecimal nuovoProgressivo = aBigDecimal();

        helper.progressivoEsistenteUnitaDocList(idStrut, idUnitaDoc, cdRegistroKeyUnitaDoc,
                aaKeyUnitaDoc, nuovoProgressivo);
        assertTrue(true);
    }

    @Test
    @Disabled("non voglio testare in maniera automatica metodi che modificano i dati")
    void salvaAssegnaProgressivo_queryIsOk() {
        BigDecimal idUnitaDoc = aBigDecimal();
        BigDecimal pgUnitaDoc = aBigDecimal();
        helper.salvaAssegnaProgressivo(idUnitaDoc, pgUnitaDoc);
        assertTrue(true);
    }

    @Test
    void getPgUnitaDoc_queryIsOk() {
        BigDecimal idUnitaDoc = aBigDecimal();
        try {
            helper.getPgUnitaDoc(idUnitaDoc);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void existAroUnitaDocsFromRegistro_queryIsOk() {
        BigDecimal idRegistroUnitaDoc = aBigDecimal();
        BigDecimal idStrut = aBigDecimal();

        helper.existAroUnitaDocsFromRegistro(idRegistroUnitaDoc, idStrut);
        assertTrue(true);
    }

    @Test
    void existAroUnitaDocsFromTipoUnita_queryIsOk() {
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        BigDecimal idStrut = aBigDecimal();

        helper.existAroUnitaDocsFromTipoUnita(idTipoUnitaDoc, idStrut);
        assertTrue(true);
    }

    @Test
    void existsRelationsWithUnitaDoc_queryIsOk() {
        long idTipoDato = aLong();
        Constants.TipoDato tipoDato = Constants.TipoDato.REGISTRO;
        helper.existsRelationsWithUnitaDoc(idTipoDato, tipoDato);
        assertTrue(true);
        tipoDato = Constants.TipoDato.TIPO_UNITA_DOC;
        helper.existsRelationsWithUnitaDoc(idTipoDato, tipoDato);
        assertTrue(true);
        tipoDato = Constants.TipoDato.TIPO_DOC;
        helper.existsRelationsWithUnitaDoc(idTipoDato, tipoDato);
        assertTrue(true);
    }

    @Test
    void existsUdWithSameNormalizedNumber_queryIsOk() {
        long idRegistroUnitaDoc = aLong();
        BigDecimal annoRegistro = aBigDecimal();
        String numeroNormalizzato = aString();
        String numeroNonNormalizzato = aString();

        helper.existsUdWithSameNormalizedNumber(idRegistroUnitaDoc, annoRegistro,
                numeroNormalizzato, numeroNonNormalizzato);
        assertTrue(true);
    }

    @Test
    @Disabled("modifica i dati su DB")
    void getAndSetAroDocOrderedByTypeAndDateProg_queryIsOk() {
        AroUnitaDoc aroUnitaDoc = anAroUnitaDoc();
        helper.getAndSetAroDocOrderedByTypeAndDateProg(aroUnitaDoc);
        assertTrue(true);
    }

    @Test
    void findAroCompUrnCalcByType_queryIsOk() {
        AroCompDoc aroCompDoc = new AroCompDoc();
        aroCompDoc.setIdCompDoc(aLong());
        for (AroCompUrnCalc.TiUrn tiUrn : AroCompUrnCalc.TiUrn.values()) {
            helper.findAroCompUrnCalcByType(aroCompDoc, tiUrn);
            assertTrue(true);
        }
    }

    @Test
    void getAroVRicUnitaDocRicAnnullateTableBeanPlainFilterLazy_queryIsOk() {
        UnitaDocumentarieHelper.FiltriUnitaDocumentarieAnnullatePlain filtri = aFiltriUnitaDocumentarieAnnullatePlain();
        List<BigDecimal> idTipoUnitaDocList = aListOfBigDecimal(2);
        Set<String> cdRegistroUnitaDocSet = aSetOfString(2);
        List<BigDecimal> idTipoDocList = aListOfBigDecimal(2);
        helper.getAroVRicUnitaDocRicAnnullateTableBeanPlainFilter(idTipoUnitaDocList,
                cdRegistroUnitaDocSet, idTipoDocList, filtri, true);
        assertTrue(true);
    }

    @Test
    void getAroVRicUnitaDocRicAnnullateTableBeanPlainFilterEager_queryIsOk() {
        UnitaDocumentarieHelper.FiltriUnitaDocumentarieAnnullatePlain filtri = aFiltriUnitaDocumentarieAnnullatePlain();
        List<BigDecimal> idTipoUnitaDocList = aListOfBigDecimal(2);
        Set<String> cdRegistroUnitaDocSet = aSetOfString(2);
        List<BigDecimal> idTipoDocList = aListOfBigDecimal(2);
        helper.getAroVRicUnitaDocRicAnnullateTableBeanPlainFilter(idTipoUnitaDocList,
                cdRegistroUnitaDocSet, idTipoDocList, filtri, false);
        assertTrue(true);
    }

    private UnitaDocumentarieHelper.FiltriUnitaDocumentarieAnnullatePlain aFiltriUnitaDocumentarieAnnullatePlain() {
        UnitaDocumentarieHelper.FiltriUnitaDocumentarieAnnullatePlain filtri = new UnitaDocumentarieHelper.FiltriUnitaDocumentarieAnnullatePlain();
        filtri.setAaKeyUnitaDoc(aBigDecimal());
        filtri.setAaKeyUnitaDocA(BigDecimal.TEN);
        filtri.setAaKeyUnitaDocDa(BigDecimal.ONE);
        filtri.setCdKeyUnitaDoc(aString());
        filtri.setCdKeyUnitaDocA(aString());
        filtri.setCdKeyUnitaDocDa(aString());
        filtri.setDataAnnulA(tomorrowTs());
        filtri.setDataAnnulDa(todayTs());
        filtri.setDataCreazioneA(tomorrowTs());
        filtri.setDataCreazioneDa(todayTs());
        filtri.setDataRegUnitaDocA(tomorrowTs());
        filtri.setDataRegUnitaDocDa(todayTs());
        return filtri;
    }

    @Test
    void checkFiltriImpostati_conFiltri() {
        UnitaDocumentarieHelper.FiltriFascicoliUnitaDocumentariePlain filtri = new UnitaDocumentarieHelper.FiltriFascicoliUnitaDocumentariePlain();
        filtri.setAaFascicolo(aBigDecimal());
        assertTrue(filtri.isFiltriImpostati());
    }

    @Test
    void checkFiltriImpostati_senzaFiltri() {
        UnitaDocumentarieHelper.FiltriFascicoliUnitaDocumentariePlain filtri = new UnitaDocumentarieHelper.FiltriFascicoliUnitaDocumentariePlain();
        assertFalse(filtri.isFiltriImpostati());
    }

    @Test
    void checkFiltriImpostati_conFiltriNull() {
        UnitaDocumentarieHelper.FiltriFascicoliUnitaDocumentariePlain filtri = new UnitaDocumentarieHelper.FiltriFascicoliUnitaDocumentariePlain();
        filtri.setAaFascicolo(null);
        assertFalse(filtri.isFiltriImpostati());
    }

    @Test
    void checkFiltriImpostati_conStringaVuota() {
        UnitaDocumentarieHelper.FiltriFascicoliUnitaDocumentariePlain filtri = new UnitaDocumentarieHelper.FiltriFascicoliUnitaDocumentariePlain();
        filtri.setCdCompositoVoceTitol("");
        assertTrue(filtri.isFiltriImpostati());
    }

    @Test
    void findById_queryIsOk() {
        try {
            helper.findById(AroUnitaDoc.class, 0L);
            fail("non dovrebbe trovare nessun record");
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void getAroVDtVersMaxByUd_queryIsOk() {
        helper.getAroVDtVersMaxByUd(0L);
        assertTrue(true);
    }

    @Test
    void getUltimaVersioneIndiceAip_queryIsOk() {
        helper.getUltimaVersioneIndiceAip(0L);
        assertTrue(true);
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        final JavaArchive sacerJavaArchive = ArquillianUtils.createSacerJavaArchive(
                Collections.emptyList(), UnitaDocumentarieHelper.class, VolumeHelper.class,
                DefinitoDaBean.class, ReturnParams.class, Constants.class, Utils.class,
                DecCriterioAttribBean.class, DatiSpecQueryParams.class, ConfigurationHelper.class,
                UnitaDocumentarieHelperTest.class);
        return HelperTest.createEnterpriseArchive(UnitaDocumentarieHelperTest.class.getSimpleName(),
                sacerJavaArchive, HelperTest.createPaginatorJavaArchive(), createSacerLogJar());
    }

    @Test
    void getListaNoteUnitaDocumentarieTableBean_queryIsOk() {
        final BigDecimal idUnitaDoc = BigDecimal.ZERO;
        helper.getListaNoteUnitaDocumentarieTableBean(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    void getAroVLisNotaUnitaDoc_queryIsOk() {
        final BigDecimal idUnitaDoc = BigDecimal.ZERO;
        helper.getAroVLisNotaUnitaDoc(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    void getMaxPgNota_queryIsOk() {
        final BigDecimal idUnitaDoc = BigDecimal.ZERO;
        final BigDecimal idTipoNotaUnitaDoc = BigDecimal.ZERO;
        helper.getMaxPgNota(idUnitaDoc, idTipoNotaUnitaDoc);
        assertTrue(true);
    }

    @Test
    void getDecTipoNotaUnitaDocList_queryIsOk() {
        helper.getDecTipoNotaUnitaDocList();
        assertTrue(true);
    }

    @Test
    void getDecTipoNotaUnitaDocListByIdUserIam_queryIsOk() {
        final long idUtente = 0L;
        try {
            helper.getDecTipoNotaUnitaDocListByIdUserIam(idUtente);
            fail("non dovrebbe trovare un utente con id " + idUtente);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void getDecTipoNotaUnitaDocNotInUnitaDoc_queryIsOk() {
        final BigDecimal idUnitaDoc = BigDecimal.ZERO;
        helper.getDecTipoNotaUnitaDocNotInUnitaDoc(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    void getDecTipoNotaUnitaDocNotInUnitaDocByIdUserIam_queryIsOk() {
        final long idUtente = 0L;
        final BigDecimal idUnitaDoc = BigDecimal.ZERO;
        helper.getDecTipoNotaUnitaDocNotInUnitaDocByIdUserIam(idUtente, idUnitaDoc);
        assertTrue(true);
    }

    @Test
    void getDecTipoNotaUnitaDocNotInVerIndiceAip_queryIsOk() {
        final BigDecimal idVerIndiceAip = BigDecimal.ZERO;
        helper.getDecTipoNotaUnitaDocNotInVerIndiceAip(idVerIndiceAip);
        assertTrue(true);
    }

    @Test
    void getAroVVisNotaUnitaDocRowBean_queryIsOk() {
        final BigDecimal idNotaUnitaDoc = BigDecimal.ZERO;
        helper.getAroVVisNotaUnitaDocRowBean(idNotaUnitaDoc);
        assertTrue(true);
    }

    @Test
    void retrieveVersioneIndiceAipUdById_queryIsOk() {
        final long idVerIndiceAip = 0L;
        helper.retrieveVersioneIndiceAipUdById(idVerIndiceAip);
        assertTrue(true);
    }

    @Test
    void isUserAppartAllOk_queryIsOk() {
        final long idUtente = 0L;
        try {
            helper.isUserAppartAllOk(idUtente);
            fail("non deve trovare nessun utente con id " + idUtente);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void getProfiloNormativo_queryIsOk() {
        final String tiUsoModelloXsd = "tiUsoModelloXsd";
        final long idUnitaDoc = 0L;
        for (DecModelloXsdUd.TiModelloXsdUd tiModelloXsdUd : DecModelloXsdUd.TiModelloXsdUd
                .values()) {
            helper.getProfiloNormativo(tiUsoModelloXsd, tiModelloXsdUd, idUnitaDoc);
        }
        assertTrue(true);
    }
}
