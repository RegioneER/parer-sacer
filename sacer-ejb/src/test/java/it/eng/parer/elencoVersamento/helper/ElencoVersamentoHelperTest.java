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

package it.eng.parer.elencoVersamento.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import it.eng.parer.elencoVersamento.utils.AggiornamentoInElenco;
import it.eng.parer.elencoVersamento.utils.ComponenteDaVerificare;
import it.eng.parer.elencoVersamento.utils.ComponenteInElenco;
import it.eng.parer.elencoVersamento.utils.DocUdObj;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.elencoVersamento.utils.UnitaDocumentariaInElenco;
import it.eng.parer.elencoVersamento.utils.UpdDocUdObj;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroIndiceAipUdDaElab;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.entity.DecCriterioRaggr;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.entity.LogJob;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.HsmElencoSessioneFirma;
import it.eng.parer.exception.ParerNoResultException;
import it.eng.parer.volume.utils.DatiSpecQueryParams;
import it.eng.parer.volume.utils.ReturnParams;
import it.eng.parer.web.dto.DecCriterioAttribBean;
import it.eng.parer.web.dto.DefinitoDaBean;
import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.utils.BinEncUtility;
import it.eng.parer.ws.utils.HashCalculator;

public class ElencoVersamentoHelperTest extends HelperTest<ElencoVersamentoHelper> {
    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(ElencoVersamentoHelperTest.class.getSimpleName(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""),
                        ElencoVersamentoHelperTest.class, ElencoVersamentoHelper.class,
                        ReturnParams.class, DefinitoDaBean.class, ElencoEnums.class,
                        ComponenteInElenco.class, ComponenteDaVerificare.class, UpdDocUdObj.class,
                        UnitaDocumentariaInElenco.class, DocUdObj.class,
                        AggiornamentoInElenco.class, DecCriterioAttribBean.class,
                        DatiSpecQueryParams.class, HashCalculator.class, BinEncUtility.class,
                        DecoderException.class, it.eng.parer.ws.dto.CSVersatore.class,
                        it.eng.parer.ws.dto.CSChiave.class,
                        it.eng.parer.job.dto.SessioneVersamentoExt.class,
                        Constants.TipoEntitaSacer.class, it.eng.parer.web.util.Constants.class),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar());
    }

    @Test
    void retrieveElenchiScadutiDaProcessare_queryIsOk() {
        long idStrut = aLong();
        helper.retrieveElenchiScadutiDaProcessare(idStrut);
        assertTrue(true);
    }

    @Test
    void retrieveElenchiVuotiDaProcessare_queryIsOk() {
        long idStrut = aLong();
        helper.retrieveElenchiVuotiDaProcessare(idStrut);
        assertTrue(true);
    }

    @Test
    void retrieveStrutture_queryIsOk() {
        helper.retrieveStrutture();
        assertTrue(true);
    }

    @Test
    void retrieveStrutturePerEle_queryIsOk() {
        helper.retrieveStrutturePerEle();
        assertTrue(true);
    }

    @Test
    void retrieveCriterioByStrut_queryIsOk() {
        OrgStrut struttura = aOrgStrut();
        Date jobStartDate = todayTs();
        helper.retrieveCriterioByStrut(struttura, jobStartDate);
        assertTrue(true);
    }

    @Test
    void retrieveElencoByCriterio_queryIsOk() {
        DecCriterioRaggr criterio = aDecCriterioRaggr();
        BigDecimal aaKeyUnitaDoc = aBigDecimal();
        OrgStrut struttura = aOrgStrut();
        try {
            helper.retrieveElencoByCriterio(criterio, aaKeyUnitaDoc, struttura);
            assertTrue(true);
        } catch (Exception e) {
            assertExceptionMessage(e, "MissingResourceException", "ParerNoResultException");
        }
    }

    @Test
    void retrieveUpdDocUdToProcess_anno_queryIsOk() {
        DecCriterioRaggr criterio = aDecCriterioRaggr();
        criterio.setAaKeyUnitaDoc(BigDecimal.valueOf(2020));
        criterio.setAaKeyUnitaDocDa(null);
        criterio.setAaKeyUnitaDocA(null);
        helper.retrieveUpdDocUdToProcess(criterio);
        assertTrue(true);
    }

    @Test
    void retrieveUpdDocUdToProcess_annoDa_queryIsOk() {
        DecCriterioRaggr criterio = aDecCriterioRaggr();
        criterio.setAaKeyUnitaDoc(null);
        criterio.setAaKeyUnitaDocDa(BigDecimal.valueOf(2020));
        criterio.setAaKeyUnitaDocA(null);
        helper.retrieveUpdDocUdToProcess(criterio);
        assertTrue(true);
    }

    @Test
    void retrieveUpdDocUdToProcess_annoA_queryIsOk() {
        DecCriterioRaggr criterio = aDecCriterioRaggr();
        criterio.setAaKeyUnitaDoc(null);
        criterio.setAaKeyUnitaDocDa(null);
        criterio.setAaKeyUnitaDocA(BigDecimal.valueOf(2021));
        helper.retrieveUpdDocUdToProcess(criterio);
        assertTrue(true);
    }

    @Test
    void retrieveUpdDocUdToProcess_rangeAnno_queryIsOk() {
        DecCriterioRaggr criterio = aDecCriterioRaggr();
        criterio.setAaKeyUnitaDoc(null);
        criterio.setAaKeyUnitaDocDa(BigDecimal.valueOf(2020));
        criterio.setAaKeyUnitaDocA(BigDecimal.valueOf(2021));
        helper.retrieveUpdDocUdToProcess(criterio);
        assertTrue(true);
    }

    @Test
    void retrieveUnitaDocToProcess_queryIsOk() {
        DecCriterioRaggr criterio = aDecCriterioRaggr();
        helper.retrieveUnitaDocToProcess(criterio);
        assertTrue(true);
    }

    @Test
    void retrieveDocToProcess_queryIsOk() {
        DecCriterioRaggr criterio = aDecCriterioRaggr();
        helper.retrieveDocToProcess(criterio);
        assertTrue(true);
    }

    @Test
    void retrieveUpdToProcess_queryIsOk() {
        DecCriterioRaggr criterio = aDecCriterioRaggr();
        helper.retrieveUpdToProcess(criterio);
        assertTrue(true);
    }

    @Test
    void buildQueryForDatiSpec_queryIsOk() {
        List datiSpecList = new ArrayList();
        datiSpecList.add(aDecCriterioDatiSpecBean());
        helper.buildQueryForDatiSpec(datiSpecList);
        assertTrue(true);
    }

    @Test
    void setNonElabSched_queryIsOk() {
        OrgStrut struttura = aOrgStrut();
        LogJob logJob = aLogJob();
        helper.setNonElabSched(struttura, logJob);
        assertTrue(true);
    }

    @Test
    void countDocsInUnitaDocCustom_queryIsOk() {
        BigDecimal unitaDoc = aBigDecimal();
        helper.countDocsInUnitaDocCustom(unitaDoc);
        assertTrue(true);
    }

    @Test
    void countElenchiGgByCritNonAperti_queryIsOk() {
        BigDecimal idCrit = aBigDecimal();
        helper.countElenchiGgByCritNonAperti(idCrit);
        assertTrue(true);
    }

    @Test
    void countElenchiGgByCritAperti_queryIsOk() {
        BigDecimal idCrit = aBigDecimal();
        helper.countElenchiGgByCritAperti(idCrit);
        assertTrue(true);
    }

    @Test
    void retrieveCompsInDoc_queryIsOk() {
        AroDoc doc = anAroDoc();
        helper.retrieveCompsInDoc(doc);
        assertTrue(true);
    }

    @Test
    void numCompsAndSizeInUnitaDocCustom_queryIsOk() {
        BigDecimal unitaDocId = aBigDecimal();
        helper.numCompsAndSizeInUnitaDocCustom(unitaDocId);
        assertTrue(true);
    }

    @Test
    void numCompsAndSizeInDoc_queryIsOk() {
        BigDecimal docId = aBigDecimal();
        helper.numCompsAndSizeInDoc(docId);
        assertTrue(true);
    }

    @Test
    void retrieveOrgStrutByid_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        helper.retrieveOrgStrutByid(idStrut);
        assertTrue(true);
    }

    @Test
    void retrieveLogJobByid_queryIsOk() {
        long idLogJob = aLong();
        helper.retrieveLogJobByid(idLogJob);
        assertTrue(true);
    }

    @Test
    void retrieveUserIdByUsername_queryIsOk() {
        String username = aString();
        try {
            helper.retrieveUserIdByUsername(username);
            assertTrue(true);
        } catch (Exception e) {
            assertExceptionMessage(e, "non esiste l'utente");
        }
    }

    @Test
    void retrieveUnitaDocById_queryIsOk() {
        long idUnitaDoc = aLong();
        helper.retrieveUnitaDocById(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    void retrieveAndLockUnitaDocById_queryIsOk() {
        long idUnitaDoc = aLong();
        helper.retrieveAndLockUnitaDocById(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    void retrieveDocById_queryIsOk() {
        long idDoc = aLong();
        helper.retrieveDocById(idDoc);
        assertTrue(true);
    }

    @Test
    void retrieveUpdById_queryIsOk() {
        long idUpdUnitaDoc = aLong();
        helper.retrieveUpdById(idUpdUnitaDoc);
        assertTrue(true);
    }

    @Test
    void retrieveCompDocById_queryIsOk() {
        long idCompDoc = aLong();
        helper.retrieveCompDocById(idCompDoc);
        assertTrue(true);
    }

    @Test
    void flush_queryIsOk() {
        helper.flush();
        assertTrue(true);
    }

    @Test
    void retrieveCriterioByid_queryIsOk() {
        long idCriterio = aLong();
        helper.retrieveCriterioByid(idCriterio);
        assertTrue(true);
    }

    @Test
    void atomicSetNonElabSched_queryIsOk() {
        OrgStrut struttura = aOrgStrut();
        LogJob logJob = aLogJob();
        helper.atomicSetNonElabSched(struttura, logJob);
        assertTrue(true);
    }

    @Test
    @Disabled("difficile da simulare, servono dati reali")
    void registraInAroIndiceAipUdDaElab_queryIsOk() {
        long idElenco = aLong();
        Date dataCreazione = todayTs();
        boolean hasDocumentiAggiunti = false;
        try {
            helper.registraInAroIndiceAipUdDaElab(anAroUnitaDoc(), idElenco, dataCreazione,
                    hasDocumentiAggiunti);
            assertTrue(true);
        } catch (Exception e) {
            assertNoAutogeneratedSequence(e);
        }
    }

    @Test
    void checkUdAnnullataByElenco_queryIsOk() throws ParseException {
        ElvElencoVer elenco = aElvElencoVer();
        try {
            helper.checkUdAnnullataByElenco(elenco);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void checkUdAnnullata_queryIsOk() throws ParseException {
        AroUnitaDoc ud = anAroUnitaDoc();
        try {
            helper.checkUdAnnullata(ud);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void checkDocAnnullato_queryIsOk() throws ParseException {
        AroDoc doc = anAroDoc();
        try {
            helper.checkDocAnnullato(doc);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void checkFreeSpaceElenco_queryIsOk() {
        ElvElencoVer elenco = aElvElencoVer();
        long numComps = aLong();
        helper.checkFreeSpaceElenco(elenco, numComps);
        assertTrue(true);
    }

    @Test
    void retrieveElenchi_queryIsOk() {
        long idStrut = aLong();
        ElencoEnums.ElencoStatusEnum[] statoElenco = ElencoEnums.ElencoStatusEnum.values();
        helper.retrieveElenchi(idStrut, statoElenco);
        assertTrue(true);
    }

    @Test
    void retrieveUdVersOrAggOrUpdInElencoValidate_queryIsOk() {
        long idElenco = aLong();
        String stato = aString();
        int numGiorni = aInt();
        helper.retrieveUdVersOrAggOrUpdInElencoValidate(idElenco, stato, numGiorni);
        assertTrue(true);
    }

    @Test
    void retrieveUdVersOrAggInElencoValidate_queryIsOk() {
        long idElenco = 2793L;
        final Collection<Long> list = helper.retrieveUdVersOrAggInElencoValidate(idElenco);
        assertTrue(true);
    }

    @Test
    void retrieveCompsToVerify_queryIsOk() {
        long idElenco = aLong();
        long idUd = aLong();
        helper.retrieveCompsToVerify(idElenco, idUd);
        assertTrue(true);
    }

    @Test
    void retrieveUdInElenco_queryIsOk() {
        long idElencoVers = aLong();
        helper.retrieveUdInElenco(idElencoVers);
        assertTrue(true);
    }

    @Test
    void retrieveUdInElencoByStato_queryIsOk() {
        long idUd = aLong();
        long idElenco = aLong();
        try {
            helper.retrieveUdInElencoByStato(idUd, idElenco);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void checkStatoElencoUdPerLeFasi_queryIsOk() {
        long idUd = aLong();
        long idElenco = aLong();
        String statoElencoUd = aString();
        try {
            helper.checkStatoElencoUdPerLeFasi(idUd, idElenco, statoElencoUd);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void checkStatoAllUdInElencoPerLeFasi_queryIsOk() {
        long idElenco = aLong();
        String[] stato = aStringArray(2);
        helper.checkStatoAllUdInElencoPerLeFasi(idElenco, stato);
        assertTrue(true);
    }

    @Test
    void retrieveComponentiInElenco_queryIsOk() {
        long idUnitaDoc = aLong();
        long idElencoVers = aLong();
        helper.retrieveComponentiInElenco(idUnitaDoc, idElencoVers);
        assertTrue(true);
    }

    @Test
    void retrieveAggiornamentiInElenco_queryIsOk() {
        long idUnitaDoc = aLong();
        long idElencoVers = aLong();
        helper.retrieveAggiornamentiInElenco(idUnitaDoc, idElencoVers);
        assertTrue(true);
    }

    @Test
    void getDataMinimaDocInElenco_queryIsOk() {
        long idUnitaDoc = aLong();
        long idElencoVers = aLong();
        helper.getDataMinimaDocInElenco(idUnitaDoc, idElencoVers);
        assertTrue(true);
    }

    @Test
    void retrieveDocNonInElenco_queryIsOk() {
        long idUnitaDocCorrente = aLong();
        Date dataCreazioneMinima = todayTs();
        long idElencoVersCorrente = aLong();
        helper.retrieveDocNonInElenco(idUnitaDocCorrente, dataCreazioneMinima,
                idElencoVersCorrente);
        assertTrue(true);
    }

    @Test
    void getPgMinimoUpdInElenco_queryIsOk() {
        long idUnitaDoc = aLong();
        long idElencoVers = aLong();
        helper.getPgMinimoUpdInElenco(idUnitaDoc, idElencoVers);
        assertTrue(true);
    }

    @Test
    void retrieveUpdNonInElenco_queryIsOk() {
        long idUnitaDocCorrente = aLong();
        BigDecimal pgMinimoUpdInElenco = aBigDecimal();
        long idElencoVersCorrente = aLong();
        it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers[] tiStatoUpdElencoVers = it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers
                .values();
        helper.retrieveUpdNonInElenco(idUnitaDocCorrente, pgMinimoUpdInElenco, idElencoVersCorrente,
                tiStatoUpdElencoVers);
        assertTrue(true);
    }

    @Test
    void retrieveCompInElenco_queryIsOk() {
        long idUnitaDoc = aLong();
        long idElencoVers = aLong();
        helper.retrieveCompInElenco(idUnitaDoc, idElencoVers);
        assertTrue(true);
    }

    @Test
    void retrieveUpdInElenco_queryIsOk() {
        long idUnitaDoc = aLong();
        long idElencoVers = aLong();
        it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers[] tiStatoUpdElencoVers = it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers
                .values();
        helper.retrieveUpdInElenco(idUnitaDoc, idElencoVers, tiStatoUpdElencoVers);
        assertTrue(true);
    }

    @Test
    void retrieveIdElenchiDaElaborare_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String statoElenco = aString();
        helper.retrieveIdElenchiDaElaborare(idStrut, statoElenco);
        assertTrue(true);
    }

    @Test
    void retrieveIdElenchiDaValidare_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String statoElenco = aString();
        String numMaxElenchiDaValidare = "1";
        helper.retrieveIdElenchiDaValidare(idStrut, statoElenco, numMaxElenchiDaValidare);
        assertTrue(true);
    }

    @Test
    void storeFileIntoElenco_queryIsOk() throws IOException, NoSuchAlgorithmException {
        ElvElencoVer elenco = aElvElencoVer();
        byte[] file = {};
        String fileType = aString();
        helper.storeFileIntoElenco(elenco, file, fileType);
        assertTrue(true);
    }

    @Test
    void getNiResetStatoUnitaDocInElenco_queryIsOk() {
        long idUnitaDoc = aLong();
        long idElencoVers = aLong();
        helper.getNiResetStatoUnitaDocInElenco(idUnitaDoc, idElencoVers);
        assertTrue(true);
    }

    @Test
    void getNiResetStatoDocInElenco_queryIsOk() {
        long idUnitaDoc = aLong();
        long idElencoVers = aLong();
        helper.getNiResetStatoDocInElenco(idUnitaDoc, idElencoVers);
        assertTrue(true);
    }

    @Test
    void getNiResetStatoUpdInElenco_queryIsOk() {
        long idUnitaDoc = aLong();
        long idElencoVers = aLong();
        helper.getNiResetStatoUpdInElenco(idUnitaDoc, idElencoVers);
        assertTrue(true);
    }

    @Test
    void aggiornaStatoUnitaDocInElenco_queryIsOk() {
        long idUnitaDoc = aLong();
        long idElencoVers = aLong();
        String stato = aString();
        Date tsStatoElencoVers = todayTs();
        Date tsLastResetStato = tomorrowTs();
        BigDecimal niResetStato = aBigDecimal();
        helper.aggiornaStatoUnitaDocInElenco(idUnitaDoc, idElencoVers, stato, tsStatoElencoVers,
                tsLastResetStato, niResetStato);
        assertTrue(true);
    }

    @Test
    void aggiornaStatoDocInElenco_queryIsOk() {
        long idUnitaDoc = aLong();
        long idElencoVers = aLong();
        String stato = aString();
        Date tsStatoElencoVers = todayTs();
        Date tsLastResetStato = tomorrowTs();
        BigDecimal niResetStato = aBigDecimal();
        helper.aggiornaStatoDocInElenco(idUnitaDoc, idElencoVers, stato, tsStatoElencoVers,
                tsLastResetStato, niResetStato);
        assertTrue(true);
    }

    @Test
    void aggiornaStatoUpdInElenco_queryIsOk() {
        long idUnitaDoc = aLong();
        long idElencoVers = aLong();
        String stato = aString();
        Date tsStatoElencoVers = tomorrowTs();
        Date tsLastResetStato = todayTs();
        BigDecimal niResetStato = aBigDecimal();
        helper.aggiornaStatoUpdInElenco(idUnitaDoc, idElencoVers, stato, tsStatoElencoVers,
                tsLastResetStato, niResetStato);
        assertTrue(true);
    }

    @Test
    void retrieveElencoInQueue_queryIsOk() {
        ElvElencoVer elenco = aElvElencoVer();
        try {
            helper.retrieveElencoInQueue(elenco);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void retrieveListaElencoInError_queryIsOk() {
        ElvElencoVer elenco = aElvElencoVer();
        for (HsmElencoSessioneFirma.TiEsitoFirmaElenco esito : HsmElencoSessioneFirma.TiEsitoFirmaElenco
                .values()) {
            helper.retrieveListaElencoInError(elenco, esito);
            assertTrue(true);
        }
    }

    @Test
    void setUdsStatus_queryIsOk() {
        ElvElencoVer elenco = aElvElencoVer();
        String status = aString();
        helper.setUdsStatus(elenco, status);
        assertTrue(true);
    }

    @Test
    void setDocsStatus_queryIsOk() {
        ElvElencoVer elenco = aElvElencoVer();
        String status = aString();
        helper.setDocsStatus(elenco, status);
        assertTrue(true);
    }

    @Test
    void setUpdsStatus_queryIsOk() {
        ElvElencoVer elenco = aElvElencoVer();
        for (it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers status : it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers
                .values()) {
            helper.setUpdsStatus(elenco, status);
            assertTrue(true);
        }
    }

    @Test
    void getElvElencoVersDaElabByIdElencoVers_queryIsOk() {
        long idElencoVers = aLong();
        try {
            helper.getElvElencoVersDaElabByIdElencoVers(idElencoVers);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void retrieveFileIndiceElenco_long_String_queryIsOk() {
        long idElencoVers = aLong();
        String tiFileElencoVers = aString();
        byte[] result = helper.retrieveFileIndiceElenco(idElencoVers, tiFileElencoVers);
        assertTrue(true);
    }

    @Test
    void retrieveFileIndiceElenco_long_StringArr_queryIsOk() {
        long idElencoVers = aLong();
        String[] tiFileElencoVers = aStringArray(2);
        helper.retrieveFileIndiceElenco(idElencoVers, tiFileElencoVers);
        assertTrue(true);
    }

    @Test
    void getFileIndiceElenco_queryIsOk() {
        long idElencoVers = aLong();
        String tiFileElencoVers = aString();
        helper.getFileIndiceElenco(idElencoVers, tiFileElencoVers);
        assertTrue(true);
    }

    @Test
    void retrieveDocVersList_queryIsOk() {
        AroUnitaDoc ud = anAroUnitaDoc();
        helper.retrieveDocVersList(ud);
        assertTrue(true);
    }

    @Test
    void retrieveUdsWithDocAggInElenco_queryIsOk() {
        long idElenco = aLong();
        helper.retrieveUdsWithDocAggInElenco(idElenco);
        assertTrue(true);
    }

    @Test
    void retrieveUdsModifDocAggUpdInElenco_queryIsOk() {
        long idElenco = 523L;
        try {
            final List<AroUnitaDoc> aroUnitaDocs = helper
                    .retrieveUdsModifDocAggUpdInElenco(idElenco);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void retrieveDocAggList_queryIsOk() {
        AroUnitaDoc ud = anAroUnitaDoc();
        long idElenco = aLong();
        helper.retrieveDocAggList(ud, idElenco);
        assertTrue(true);
    }

    @Test
    void retrieveDocAggUpdList_queryIsOk() {
        AroUnitaDoc ud = anAroUnitaDoc();
        long idElenco = aLong();
        helper.retrieveDocAggUpdList(ud, idElenco);
        assertTrue(true);
    }

    @Test
    void contaUdVersate_queryIsOk() {
        Long idElencoVers = aLong();
        helper.contaUdVersate(idElencoVers);
        assertTrue(true);
    }

    @Test
    void contaFascVersati_queryIsOk() {
        Long idElencoVersFasc = aLong();
        helper.contaFascVersati(idElencoVersFasc);
        assertTrue(true);
    }

    @Test
    void contaDocVersati_queryIsOk() {
        Long idElencoVers = aLong();
        helper.contaDocVersati(idElencoVers);
        assertTrue(true);
    }

    @Test
    void contaCompVersati_queryIsOk() {
        Long idElencoVers = aLong();
        helper.contaCompVersati(idElencoVers);
        assertTrue(true);
    }

    @Test
    void contaUdModificatePerDocAggiunti_queryIsOk() {
        Long idElencoVers = aLong();
        helper.contaUdModificatePerDocAggiunti(idElencoVers);
        assertTrue(true);
    }

    @Test
    void contaUdModificatePerByDocAggiuntiByUpd_queryIsOk() {
        Long idElencoVers = aLong();
        try {
            helper.contaUdModificatePerByDocAggiuntiByUpd(idElencoVers);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void contaDocAggiunti_queryIsOk() {
        Long idElencoVers = aLong();
        helper.contaDocAggiunti(idElencoVers);
        assertTrue(true);
    }

    @Test
    void contaUpdUd_queryIsOk() {
        Long idElencoVers = aLong();
        helper.contaUpdUd(idElencoVers);
        assertTrue(true);
    }

    @Test
    void contaCompPerDocAggiunti_queryIsOk() {
        Long idElencoVers = aLong();
        helper.contaCompPerDocAggiunti(idElencoVers);
        assertTrue(true);
    }

    @Test
    void retrieveElenchiIndiciAipDaProcessare_queryIsOk() {
        String tiStatoElenco = aString();
        helper.retrieveElenchiIndiciAipDaProcessare(tiStatoElenco);
        assertTrue(true);
    }

    @Test
    void retrieveElenchiIndiciAipDaMarcare_queryIsOk() {
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idEnte = aBigDecimal();
        BigDecimal idStrut = aBigDecimal();
        long idUserIam = aLong();
        // String tiGestElenco = aString();
        List<String> tiGestElenco = aListOfString(0);
        helper.retrieveElenchiIndiciAipDaMarcare(idAmbiente, idEnte, idStrut, idUserIam,
                tiGestElenco);
        assertTrue(true);
    }

    @Test
    void retrieveUdVersOrAggInElenco_queryIsOk() {
        long idElenco = aLong();
        helper.retrieveUdVersOrAggInElenco(idElenco);
        assertTrue(true);
    }

    @Test
    void existUdVersDocAggAnnullati_queryIsOk() {
        BigDecimal idElencoVers = aBigDecimal();
        helper.existUdVersDocAggAnnullati(idElencoVers);
        assertTrue(true);
    }

    @Test
    void getUnitaDocVersateElenco_queryIsOk() {
        BigDecimal idElencoVers = aBigDecimal();
        helper.getUnitaDocVersateElenco(idElencoVers);
        assertTrue(true);
    }

    @Test
    void getDocAggiuntiElenco_queryIsOk() {
        BigDecimal idElencoVers = aBigDecimal();
        helper.getDocAggiuntiElenco(idElencoVers);
        assertTrue(true);
    }

    @Test
    void getUpdMetadatiElenco_queryIsOk() {
        BigDecimal idElencoVers = aBigDecimal();
        helper.getUpdMetadatiElenco(idElencoVers);
        assertTrue(true);
    }

    @Test
    void retrieveElvVChkAddDocAggByIdDocAggByIdElenco_queryIsOk() {
        long idDoc = aLong();
        long idElencoVers = aLong();
        try {
            helper.retrieveElvVChkAddDocAggByIdDocAggByIdElenco(idDoc, idElencoVers);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void retrieveElvVChkAddUpdUdByIdUpdUdByIdElenco_queryIsOk() {
        long idUpdUnitaDoc = aLong();
        long idElencoVers = aLong();
        try {
            helper.retrieveElvVChkAddUpdUdByIdUpdUdByIdElenco(idUpdUnitaDoc, idElencoVers);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void retrieveUdInElencoByElencoIdList_queryIsOk() {
        helper.retrieveUdInElencoByElencoIdList(0L);
        assertTrue(true);
    }

    @Test
    void retrieveElvVLisAllUdByElenco_queryIsOk() {
        try {
            helper.retrieveElvVLisAllUdByElenco(0L, 0L);
            fail("mi aspetto che non trovi nulla ");
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void leggiXmlVersamentiElencoDaUnitaDoc_queryIsOk() throws ParerNoResultException {
        try {
            helper.leggiXmlVersamentiElencoDaUnitaDoc(0L, "baseUrnUnitaDoc");
            fail("mi aspetto che non trovi nulla e sollevi un ParerNoResultException");
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void aggiornaElencoCorrente_queryIsOk() {
        final long idElencoVers = 0L;
        for (ElencoEnums.ElencoStatusEnum stato : ElencoEnums.ElencoStatusEnum.values()) {
            try {
                helper.aggiornaElencoCorrente(idElencoVers, stato);
            } catch (Exception e) {
                assertNoResultException(e);
            }
        }
        assertTrue(true);
    }

    @Test
    void aggiornaElencoDaElabCorrente_queryIsOk() {
        final long idElencoDaEleb = 0L;
        for (ElencoEnums.ElencoStatusEnum stato : ElencoEnums.ElencoStatusEnum.values()) {
            try {
                helper.aggiornaElencoDaElabCorrente(idElencoDaEleb, stato);
            } catch (Exception e) {
                assertNoResultException(e);
            }
        }
        assertTrue(true);
    }

    @Test
    void aggiornaElencoDaElabCorrenteAnnullaTimestamp_queryIsOk() {
        final long idElencoDaEleb = 0L;
        for (ElencoEnums.ElencoStatusEnum stato : ElencoEnums.ElencoStatusEnum.values()) {
            try {
                helper.aggiornaElencoDaElabCorrente(idElencoDaEleb, stato, true);
                helper.aggiornaElencoDaElabCorrente(idElencoDaEleb, stato, false);
            } catch (Exception e) {
                assertNoResultException(e);
            }
        }
        assertTrue(true);
    }

    @Test
    void retrieveStruttureByAmb_queryIsOk() {
        helper.retrieveStruttureByAmb(BigDecimal.ZERO);
        assertTrue(true);
    }

    @Test
    void deleteDocFromQueue_queryIsOk() {
        helper.deleteDocFromQueue(anAroDoc());
        assertTrue(true);
    }

    @Test
    void deleteUpdFromQueue_queryIsOk() {
        helper.deleteUpdFromQueue(anAroUpdUnitaDoc());
        assertTrue(true);
    }

    @Test
    void retrieveDocsInElenco_queryIsOk() {
        final ElvElencoVer elenco = new ElvElencoVer();
        elenco.setAroDocs(new ArrayList<>());
        elenco.getAroDocs().add(new AroDoc());
        final Long idDoc = -1L;
        elenco.getAroDocs().get(0).setIdDoc(idDoc);
        final List<AroDoc> aroDocs = helper.retrieveDocsInElenco(elenco);
        assertEquals(1, aroDocs.size());
        assertEquals(idDoc, aroDocs.get(0).getIdDoc());
    }

    @Test
    void retrieveUdDocsInElenco_queryIsOk() {
        final ElvElencoVer elenco = new ElvElencoVer();
        elenco.setAroUnitaDocs(new ArrayList<>());
        elenco.getAroUnitaDocs().add(new AroUnitaDoc());
        final Long idUnitaDoc = -1L;
        elenco.getAroUnitaDocs().get(0).setIdUnitaDoc(idUnitaDoc);
        final List<AroUnitaDoc> aroUnitaDocs = helper.retrieveUdDocsInElenco(elenco);
        assertEquals(1, aroUnitaDocs.size());
        assertEquals(idUnitaDoc, aroUnitaDocs.get(0).getIdUnitaDoc());
    }

    @Test
    void retrieveUpdsInElenco_queryIsOk() {
        final ElvElencoVer elenco = new ElvElencoVer();
        elenco.setAroUpdUnitaDocs(new ArrayList<>());
        elenco.getAroUpdUnitaDocs().add(new AroUpdUnitaDoc());
        final Long idUpdUnitaDoc = -1L;
        elenco.getAroUpdUnitaDocs().get(0).setIdUpdUnitaDoc(idUpdUnitaDoc);
        final List<AroUpdUnitaDoc> aroUpdUnitaDocs = helper.retrieveUpdsInElenco(elenco);
        assertEquals(1, aroUpdUnitaDocs.size());
        assertEquals(idUpdUnitaDoc, aroUpdUnitaDocs.get(0).getIdUpdUnitaDoc());
    }

    @Test
    void retrieveElencoById_queryIsOk() {
        helper.retrieveElencoById(0L);
        assertTrue(true);
    }

    @Test
    @Disabled("difficile da lasciare come test automatico, fa delle insert e alla seconda esecuzione va in errore per doppia PK")
    void writeLogElencoVers_queryIsOk() {
        helper.writeLogElencoVers(anElvElencoVer(), anOrgStrut(), 0L, "tipoOper", anAroDoc(),
                anAroUpdUnitaDoc(), anAroUnitaDoc(), aLogJob());
        assertTrue(true);
    }

    @Test
    @Disabled("difficile da lasciare come test automatico, fa delle insert e alla seconda esecuzione va in errore per doppia PK")
    void writeLogElencoVers4_queryIsOk() {
        helper.writeLogElencoVers(anElvElencoVer(), anOrgStrut(), 0L, "tipoOper", anAroDoc(),
                anAroUnitaDoc());
        assertTrue(true);
    }

    @Test
    @Disabled("difficile da lasciare come test automatico, fa delle insert e alla seconda esecuzione va in errore per doppia PK")
    void writeLogElencoVers1_queryIsOk() {
        helper.writeLogElencoVers(anElvElencoVer(), anOrgStrut(), 0L, "tipoOper",
                anAroUpdUnitaDoc(), anAroUnitaDoc());
        assertTrue(true);
    }

    @Test
    @Disabled("difficile da lasciare come test automatico, fa delle insert e alla seconda esecuzione va in errore per doppia PK")
    void writeLogElencoVers2_queryIsOk() {
        helper.writeLogElencoVers(anElvElencoVer(), anOrgStrut(), 0L, "tipoOper");
        assertTrue(true);
    }

    @Test
    @Disabled("difficile da lasciare come test automatico, fa delle insert e alla seconda esecuzione va in errore per doppia PK")
    void writeLogElencoVers3_queryIsOk() {
        helper.writeLogElencoVers(anElvElencoVer(), anOrgStrut(), "tipoOper", aLogJob());
        assertTrue(true);
    }

    @Test
    @Disabled("difficile da simulare, servono dati reali")
    void registraInAroCompIndiceAipUdDaElab_queryIsOk() {
        helper.registraInAroCompIndiceAipUdDaElab(0L, anAroIndiceAipUdDaElab());
        assertTrue(true);
    }

    private AroIndiceAipUdDaElab anAroIndiceAipUdDaElab() {
        final AroIndiceAipUdDaElab aroIndiceAipUdDaElab = new AroIndiceAipUdDaElab();
        aroIndiceAipUdDaElab.setIdIndiceAipDaElab(0L);
        return aroIndiceAipUdDaElab;
    }

    @Test
    @Disabled("difficile da simulare, servono dati reali")
    void registraInAroUpdUdIndiceAipUdDaElab_queryIsOk() {
        helper.registraInAroUpdUdIndiceAipUdDaElab(0L, anAroIndiceAipUdDaElab());
        assertTrue(true);
    }

    @Test
    void getUltimaVersioneIndiceAip_queryIsOk() {
        helper.getUltimaVersioneIndiceAip(0L);
        assertTrue(true);
    }

    @Test
    @Disabled("funziona solo se di da un ID esistente")
    void deleteElencoVersDaElab_queryIsOk() {
        helper.deleteElencoVersDaElab(0L);
        assertTrue(true);
    }

    @Test
    @Disabled("funziona solo se di da un ID esistente")
    void deleteElvElencoVer_queryIsOk() {
        helper.deleteElvElencoVer(BigDecimal.ZERO);
        assertTrue(true);
    }

    @Test
    @Disabled("funziona solo se di da un ID esistente")
    void deleteElvElencoVersFasc_queryIsOk() {
        helper.deleteElvElencoVersFasc(BigDecimal.ZERO);
        assertTrue(true);
    }

    @Test
    @Disabled("funziona solo se di da un ID esistente")
    void insertUdCodaUdDaElab_queryIsOk() {
        helper.insertUdCodaUdDaElab(0L, ElencoEnums.UdDocStatusEnum.IN_ATTESA_SCHED);
        assertTrue(true);
    }

    @Test
    @Disabled("funziona solo se di da un ID esistente")
    void insertDocCodaDocDaElab_queryIsOk() {
        helper.insertDocCodaDocDaElab(0L, ElencoEnums.DocStatusEnum.IN_ATTESA_SCHED);
        assertTrue(true);
    }

    @Test
    @Disabled("funziona solo se di da un ID esistente")
    void insertUpdCodaUpdDaElab_queryIsOk() {
        helper.insertUpdCodaUpdDaElab(0L,
                it.eng.parer.entity.constraint.ElvUpdUdDaElabElenco.ElvUpdUdDaElabTiStatoUpdElencoVers.IN_ATTESA_SCHED);
        assertTrue(true);
    }

    @Test
    void retrieveUrnElencoVersList_queryIsOk() {
        helper.retrieveUrnElencoVersList(0L);
        assertTrue(true);
    }

    @Test
    void retrieveStatiElencoByElencoVers_queryIsOk() {
        helper.retrieveStatiElencoByElencoVers(BigDecimal.ZERO);
        assertTrue(true);
    }

    @Test
    void getPgStatoElencoVers_queryIsOk() {
        helper.getPgStatoElencoVers(BigDecimal.ZERO);
        assertTrue(true);
    }

    @Test
    void getIdTiEveStatoElencoVers_queryIsOk() {
        helper.getIdTiEveStatoElencoVers("cdTiEveStatoElencoVers");
        assertTrue(true);
    }

    @Test
    void getElenchiFiscaliByStrutturaAperti_queryIsOk() {
        helper.getElenchiFiscaliByStrutturaAperti(0L, 2021);
        assertTrue(true);
    }

    @Test
    void isStatoElencoCorrente_queryIsOk() {
        for (it.eng.parer.entity.constraint.ElvStatoElencoVer.TiStatoElenco tiStatoElenco : it.eng.parer.entity.constraint.ElvStatoElencoVer.TiStatoElenco
                .values()) {
            helper.isStatoElencoCorrente(0L, tiStatoElenco);
        }
        assertTrue(true);
    }

    @Test
    void setNonSelezSchedJms_queryIsOk() {
        final BigDecimal id = BigDecimal.ZERO;
        final OrgStrut orgStrut = anOrgStrut();
        for (Constants.TipoEntitaSacer tiEntitaSacer : Constants.TipoEntitaSacer.values()) {
            helper.setNonSelezSchedJms(orgStrut, id, tiEntitaSacer);
        }
        assertTrue(true);
    }

    @Test
    void retrieveDecCriterioRaggrByid_queryIsOk() {
        helper.retrieveDecCriterioRaggrByid(BigDecimal.ZERO);
        assertTrue(true);
    }

    @Test
    void checkStatoAllUdInElencoPerVerificaFirmeDtVers_queryIsOk() {
        helper.checkStatoAllUdInElencoPerVerificaFirmeDtVers(0L, "STATO1", "STATO2");
        assertTrue(true);
    }

    private OrgStrut anOrgStrut() {
        OrgStrut struttura = new OrgStrut();
        struttura.setIdStrut(0L);
        return struttura;
    }

    private ElvElencoVer anElvElencoVer() {
        ElvElencoVer elenco = new ElvElencoVer();
        elenco.setIdElencoVers(0L);
        elenco.setNmElenco(ElencoVersamentoHelperTest.class.getSimpleName());
        return elenco;
    }

    @Test
    void retrieveAmbienti_queryIsOk() {
        final List<OrgAmbiente> orgAmbientes = helper.retrieveAmbienti();
        assertFalse(orgAmbientes.isEmpty());
    }

    @Test
    void retrieveUpdDocUdToProcess_queryIsOk() {
        helper.retrieveUpdDocUdToProcess(aDecCriterioRaggr());
        assertTrue(true);
    }

    @Test
    @Disabled("attenzione fa una delete")
    void deleteUdDocFromQueue_queryIsOk() {
        assertTrue(true);
    }

    @Test
    void checkStatoAllUdInElencoPerCodaIndiceAipDaElab_queryIsOk() {
        helper.checkStatoAllUdInElencoPerCodaIndiceAipDaElab(0L, "STATO", "ALTRO STATO");
    }
}
