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

package it.eng.parer.elencoVersFascicoli.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aDecCriterioDatiSpecBean;
import static it.eng.ArquillianUtils.aDecCriterioRaggrFasc;
import static it.eng.ArquillianUtils.aElvElencoVersFasc;
import static it.eng.ArquillianUtils.aFasFascicolo;
import static it.eng.ArquillianUtils.aLogJob;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aOrgStrut;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.aStringArray;
import static it.eng.ArquillianUtils.anAroDoc;
import static it.eng.ArquillianUtils.assertExceptionMessage;
import static it.eng.ArquillianUtils.assertNoResultException;
import static it.eng.ArquillianUtils.todayTs;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.elencoVersFascicoli.utils.FasFascicoloObj;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.DecCriterioRaggrFasc;
import it.eng.parer.entity.ElvElencoVersFasc;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.LogJob;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.ElvElencoVersFascDaElab;
import it.eng.parer.entity.constraint.ElvStatoElencoVersFasc;
import it.eng.parer.entity.constraint.FasStatoFascicoloElenco;
import it.eng.parer.entity.constraint.HsmElencoFascSesFirma;
import it.eng.parer.exception.ParerNoResultException;
import it.eng.parer.volume.utils.DatiSpecQueryParams;
import it.eng.parer.volume.utils.ReturnParams;
import it.eng.parer.web.dto.DecCriterioAttribBean;
import it.eng.parer.web.dto.DefinitoDaBean;
import it.eng.parer.web.helper.HelperTest;

@ArquillianTest
public class ElencoVersFascicoliHelperTest {
    @EJB
    private ElencoVersFascicoliHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(
                ElencoVersFascicoliHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""),
                        ElencoVersFascicoliHelperTest.class, ElencoVersFascicoliHelper.class,
                        ReturnParams.class, DefinitoDaBean.class, FasFascicoloObj.class,
                        DecCriterioAttribBean.class, DatiSpecQueryParams.class));
    }

    @Test
    void retrieveElenchiDaProcessare_queryIsOk() {
        long idStrut = aLong();
        helper.retrieveElenchiDaProcessare(idStrut);
        assertTrue(true);
    }

    @Test
    void retrieveStrutture_queryIsOk() {
        helper.retrieveStrutture();
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
    void retrieveElencoByCriterio_queryIsOk() throws ParerNoResultException {
        DecCriterioRaggrFasc criterio = aDecCriterioRaggrFasc();
        BigDecimal aaFascicolo = aBigDecimal();
        OrgStrut struttura = aOrgStrut();
        try {
            helper.retrieveElencoByCriterio(criterio, aaFascicolo, struttura);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void retrieveFascicoliToProcess_queryIsOk() {
        DecCriterioRaggrFasc criterio = aDecCriterioRaggrFasc();
        helper.retrieveFascicoliToProcess(criterio);
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
    void retrieveFascicoliInQueue_queryIsOk() {
        OrgStrut struttura = aOrgStrut();
        LogJob logJob = aLogJob();
        helper.retrieveFascicoliInQueue(struttura, logJob);
        assertTrue(true);
    }

    @Test
    void countDocsInUnitaDocCustom_queryIsOk() {
        BigDecimal unitaDoc = aBigDecimal();
        helper.countDocsInUnitaDocCustom(unitaDoc);
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
    void retrieveFasFascicoliInElenco_queryIsOk() {
        ElvElencoVersFasc elenco = aElvElencoVersFasc();
        helper.retrieveFasFascicoliInElenco(elenco);
        assertTrue(true);
    }

    @Test
    void retrieveElencoById_queryIsOk() {
        Long idElenco = aLong();
        helper.retrieveElencoById(idElenco);
        assertTrue(true);
    }

    @Test
    void retrieveStatoElencoByIdElencoVersFascStato_queryIsOk() {
        Long idElencoVersFasc = aLong();
        for (ElvStatoElencoVersFasc.TiStatoElencoFasc status : ElvStatoElencoVersFasc.TiStatoElencoFasc
                .values()) {
            try {
                helper.retrieveStatoElencoByIdElencoVersFascStato(idElencoVersFasc, status);
                assertTrue(true);
            } catch (Exception e) {
                assertExceptionMessage(e, "Errore nel reperimento");
            }
        }
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
    void retrieveFasFascicoloById_queryIsOk() {
        long idFascicolo = aLong();
        helper.retrieveFasFascicoloById(idFascicolo);
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
    void aggiornaStatoInElencoCor_queryIsOk() {
        ElvElencoVersFasc elenco = aElvElencoVersFasc();
        BigDecimal idStatoElencoVersFasc = aBigDecimal();
        try {
            helper.aggiornaStatoInElencoCor(elenco, idStatoElencoVersFasc);
        } catch (Exception e) {
            // ElvElencoVersFasc non è persistito su DB
            assertExceptionMessage(e, "Transaction rolled back");
        }
    }

    @Test
    void checkFascicoloAnnullato_queryIsOk() {
        FasFascicolo ff = aFasFascicolo();
        try {
            helper.checkFascicoloAnnullato(ff);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void checkFreeSpaceElenco_queryIsOk() {
        ElvElencoVersFasc elenco = aElvElencoVersFasc();
        helper.checkFreeSpaceElenco(elenco);
        assertTrue(true);
    }

    @Test
    void retrieveIdElenchiDaElaborare_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        for (ElvElencoVersFascDaElab.TiStatoElencoFascDaElab statoElenco : ElvElencoVersFascDaElab.TiStatoElencoFascDaElab
                .values()) {
            helper.retrieveIdElenchiDaElaborare(idStrut, statoElenco);
            assertTrue(true);
        }
    }

    @Test
    void retrieveElencoInQueue_queryIsOk() {
        ElvElencoVersFasc elenco = aElvElencoVersFasc();
        try {
            helper.retrieveElencoInQueue(elenco);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void retrieveListaElencoInError_queryIsOk() {
        ElvElencoVersFasc elenco = aElvElencoVersFasc();
        for (HsmElencoFascSesFirma.TiEsitoFirmaElencoFasc esito : HsmElencoFascSesFirma.TiEsitoFirmaElencoFasc
                .values()) {
            helper.retrieveListaElencoInError(elenco, esito);
            assertTrue(true);
        }
    }

    @Test
    void setStatoFascicoloElenco_queryIsOk() {
        ElvElencoVersFasc elenco = aElvElencoVersFasc();
        for (FasStatoFascicoloElenco.TiStatoFascElenco status : FasStatoFascicoloElenco.TiStatoFascElenco
                .values()) {
            helper.setStatoFascicoloElenco(elenco, status);
            assertTrue(true);
        }
    }

    @Test
    void setFasFascicoliStatus_queryIsOk() {
        ElvElencoVersFasc elenco = aElvElencoVersFasc();
        for (it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers status : it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers
                .values()) {
            helper.setFasFascicoliStatus(elenco, status);
            assertTrue(true);
        }
    }

    @Test
    void getStatoElencoByIdElencoVersFascStato_queryIsOk() {
        Long idElencoVersFasc = aLong();
        for (ElvStatoElencoVersFasc.TiStatoElencoFasc status : ElvStatoElencoVersFasc.TiStatoElencoFasc
                .values()) {
            try {
                helper.getStatoElencoByIdElencoVersFascStato(idElencoVersFasc, status);
                assertTrue(true);
            } catch (Exception e) {
                assertExceptionMessage(e, "Errore nel reperimento");
            }
        }
    }

    @Test
    void getElvElencoVersFascDaElabByIdElencoVersFasc_queryIsOk() {
        long idElencoVersFasc = aLong();
        try {
            helper.getElvElencoVersFascDaElabByIdElencoVersFasc(idElencoVersFasc);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void retrieveElvFileElencoVersFasc_queryIsOk() {
        long idElencoVersFasc = aLong();
        String tiFileElencoVers = aString();
        helper.retrieveElvFileElencoVersFasc(idElencoVersFasc, tiFileElencoVers);
        assertTrue(true);
    }

    @Test
    void retrieveFileIndiceElenco_long_String_queryIsOk() {
        long idElencoVersFasc = aLong();
        String tiFileElencoVers = aString();
        helper.retrieveFileIndiceElenco(idElencoVersFasc, tiFileElencoVers);
        assertTrue(true);
    }

    @Test
    void getFileIndiceElenco_queryIsOk() {
        long idElencoVersFasc = aLong();
        String tiFileElencoVers = aString();
        helper.getFileIndiceElenco(idElencoVersFasc, tiFileElencoVers);
        assertTrue(true);
    }

    @Test
    void retrieveFileIndiceElenco_long_StringArr_queryIsOk() {
        long idElencoVersFasc = aLong();
        String[] tiFileElencoVers = aStringArray(2);
        helper.retrieveFileIndiceElenco(idElencoVersFasc, tiFileElencoVers);
        assertTrue(true);
    }

    @Test
    void contaFascVersati_queryIsOk() {
        Long idElencoVersFasc = aLong();
        helper.contaFascVersati(idElencoVersFasc);
        assertTrue(true);
    }

    @Test
    void retrieveElenchiIndiciAipFascicoliDaProcessare_queryIsOk() {
        helper.retrieveElenchiIndiciAipFascicoliDaProcessare();
        assertTrue(true);
    }

    @Test
    void existFascVersAnnullati_queryIsOk() {
        BigDecimal idElencoVersFasc = aBigDecimal();
        helper.existFascVersAnnullati(idElencoVersFasc);
        assertTrue(true);
    }

    @Test
    void getFasFascicoloVersatiElenco_queryIsOk() {
        BigDecimal idElencoVersFasc = aBigDecimal();
        helper.getFasFascicoloVersatiElenco(idElencoVersFasc);
        assertTrue(true);
    }

    @Test
    void retrieveFascVersInElenco_queryIsOk() {
        long idElenco = aLong();
        helper.retrieveFascVersInElenco(idElenco);
        assertTrue(true);
    }

    @Test
    void retrieveFascVersInElencoAipCreato_queryIsOk() {
        long idElenco = 8784221L;
        helper.retrieveFascVersInElencoAipCreato(idElenco);
        assertTrue(true);
    }
}
