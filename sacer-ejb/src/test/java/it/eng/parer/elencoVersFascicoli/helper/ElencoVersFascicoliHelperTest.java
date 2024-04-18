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

package it.eng.parer.elencoVersFascicoli.helper;

import it.eng.parer.elencoVersFascicoli.utils.FasFascicoloObj;
import it.eng.parer.entity.*;
import it.eng.parer.entity.constraint.ElvElencoVersFascDaElab;
import it.eng.parer.entity.constraint.ElvStatoElencoVersFasc;
import it.eng.parer.entity.constraint.FasStatoFascicoloElenco;
import it.eng.parer.entity.constraint.HsmElencoFascSesFirma;
import it.eng.parer.exception.ParerNoResultException;
import it.eng.parer.volume.utils.DatiSpecQueryParams;
import it.eng.parer.volume.utils.ReturnParams;
import it.eng.parer.web.dto.DecCriterioAttribBean;
import it.eng.parer.web.dto.DefinitoDaBean;
import it.eng.parer.web.helper.AggiornamentiHelperTest;
import it.eng.parer.web.helper.HelperTest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class ElencoVersFascicoliHelperTest {
    @EJB
    private ElencoVersFascicoliHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(ElencoVersFascicoliHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), ElencoVersFascicoliHelperTest.class,
                        ElencoVersFascicoliHelper.class, ReturnParams.class, DefinitoDaBean.class,
                        FasFascicoloObj.class, DecCriterioAttribBean.class, DatiSpecQueryParams.class));
    }

    @Test
    public void retrieveElenchiDaProcessare_queryIsOk() {
        long idStrut = aLong();
        helper.retrieveElenchiDaProcessare(idStrut);
        assertTrue(true);
    }

    @Test
    public void retrieveStrutture_queryIsOk() {
        helper.retrieveStrutture();
        assertTrue(true);
    }

    @Test
    public void retrieveCriterioByStrut_queryIsOk() {
        OrgStrut struttura = aOrgStrut();
        Date jobStartDate = todayTs();
        helper.retrieveCriterioByStrut(struttura, jobStartDate);
        assertTrue(true);
    }

    @Test
    public void retrieveElencoByCriterio_queryIsOk() throws ParerNoResultException {
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
    public void retrieveFascicoliToProcess_queryIsOk() {
        DecCriterioRaggrFasc criterio = aDecCriterioRaggrFasc();
        helper.retrieveFascicoliToProcess(criterio);
        assertTrue(true);
    }

    @Test
    public void buildQueryForDatiSpec_queryIsOk() {
        List datiSpecList = new ArrayList();
        datiSpecList.add(aDecCriterioDatiSpecBean());
        helper.buildQueryForDatiSpec(datiSpecList);
        assertTrue(true);
    }

    @Test
    public void setNonElabSched_queryIsOk() {
        OrgStrut struttura = aOrgStrut();
        LogJob logJob = aLogJob();
        helper.setNonElabSched(struttura, logJob);
        assertTrue(true);
    }

    @Test
    public void retrieveFascicoliInQueue_queryIsOk() {
        OrgStrut struttura = aOrgStrut();
        LogJob logJob = aLogJob();
        helper.retrieveFascicoliInQueue(struttura, logJob);
        assertTrue(true);
    }

    @Test
    public void countDocsInUnitaDocCustom_queryIsOk() {
        BigDecimal unitaDoc = aBigDecimal();
        helper.countDocsInUnitaDocCustom(unitaDoc);
        assertTrue(true);
    }

    @Test
    public void retrieveCompsInDoc_queryIsOk() {
        AroDoc doc = anAroDoc();
        helper.retrieveCompsInDoc(doc);
        assertTrue(true);
    }

    @Test
    public void numCompsAndSizeInUnitaDocCustom_queryIsOk() {
        BigDecimal unitaDocId = aBigDecimal();
        helper.numCompsAndSizeInUnitaDocCustom(unitaDocId);
        assertTrue(true);
    }

    @Test
    public void numCompsAndSizeInDoc_queryIsOk() {
        BigDecimal docId = aBigDecimal();
        helper.numCompsAndSizeInDoc(docId);
        assertTrue(true);
    }

    @Test
    public void retrieveFasFascicoliInElenco_queryIsOk() {
        ElvElencoVersFasc elenco = aElvElencoVersFasc();
        helper.retrieveFasFascicoliInElenco(elenco);
        assertTrue(true);
    }

    @Test
    public void retrieveElencoById_queryIsOk() {
        Long idElenco = aLong();
        helper.retrieveElencoById(idElenco);
        assertTrue(true);
    }

    @Test
    public void retrieveStatoElencoByIdElencoVersFascStato_queryIsOk() {
        Long idElencoVersFasc = aLong();
        for (ElvStatoElencoVersFasc.TiStatoElencoFasc status : ElvStatoElencoVersFasc.TiStatoElencoFasc.values()) {
            try {
                helper.retrieveStatoElencoByIdElencoVersFascStato(idElencoVersFasc, status);
                assertTrue(true);
            } catch (Exception e) {
                assertExceptionMessage(e, "Errore nel reperimento");
            }
        }
    }

    @Test
    public void retrieveOrgStrutByid_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        helper.retrieveOrgStrutByid(idStrut);
        assertTrue(true);
    }

    @Test
    public void retrieveLogJobByid_queryIsOk() {
        long idLogJob = aLong();
        helper.retrieveLogJobByid(idLogJob);
        assertTrue(true);
    }

    @Test
    public void retrieveUserIdByUsername_queryIsOk() {
        String username = aString();
        try {
            helper.retrieveUserIdByUsername(username);
            assertTrue(true);
        } catch (Exception e) {
            assertExceptionMessage(e, "non esiste l'utente");
        }
    }

    @Test
    public void retrieveFasFascicoloById_queryIsOk() {
        long idFascicolo = aLong();
        helper.retrieveFasFascicoloById(idFascicolo);
        assertTrue(true);
    }

    @Test
    public void retrieveAndLockUnitaDocById_queryIsOk() {
        long idUnitaDoc = aLong();
        helper.retrieveAndLockUnitaDocById(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void retrieveDocById_queryIsOk() {
        long idDoc = aLong();
        helper.retrieveDocById(idDoc);
        assertTrue(true);
    }

    @Test
    public void retrieveCompDocById_queryIsOk() {
        long idCompDoc = aLong();
        helper.retrieveCompDocById(idCompDoc);
        assertTrue(true);
    }

    @Test
    public void flush_queryIsOk() {
        helper.flush();
        assertTrue(true);
    }

    @Test
    public void retrieveCriterioByid_queryIsOk() {
        long idCriterio = aLong();
        helper.retrieveCriterioByid(idCriterio);
        assertTrue(true);
    }

    @Test
    public void atomicSetNonElabSched_queryIsOk() {
        OrgStrut struttura = aOrgStrut();
        LogJob logJob = aLogJob();
        helper.atomicSetNonElabSched(struttura, logJob);
        assertTrue(true);
    }

    @Test
    public void aggiornaStatoInElencoCor_queryIsOk() {
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
    public void checkFascicoloAnnullato_queryIsOk() {
        FasFascicolo ff = aFasFascicolo();
        try {
            helper.checkFascicoloAnnullato(ff);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void checkFreeSpaceElenco_queryIsOk() {
        ElvElencoVersFasc elenco = aElvElencoVersFasc();
        helper.checkFreeSpaceElenco(elenco);
        assertTrue(true);
    }

    @Test
    public void retrieveIdElenchiDaElaborare_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        for (ElvElencoVersFascDaElab.TiStatoElencoFascDaElab statoElenco : ElvElencoVersFascDaElab.TiStatoElencoFascDaElab
                .values()) {
            helper.retrieveIdElenchiDaElaborare(idStrut, statoElenco);
            assertTrue(true);
        }
    }

    @Test
    public void retrieveElencoInQueue_queryIsOk() {
        ElvElencoVersFasc elenco = aElvElencoVersFasc();
        try {
            helper.retrieveElencoInQueue(elenco);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void retrieveListaElencoInError_queryIsOk() {
        ElvElencoVersFasc elenco = aElvElencoVersFasc();
        for (HsmElencoFascSesFirma.TiEsitoFirmaElencoFasc esito : HsmElencoFascSesFirma.TiEsitoFirmaElencoFasc
                .values()) {
            helper.retrieveListaElencoInError(elenco, esito);
            assertTrue(true);
        }
    }

    @Test
    public void setStatoFascicoloElenco_queryIsOk() {
        ElvElencoVersFasc elenco = aElvElencoVersFasc();
        for (FasStatoFascicoloElenco.TiStatoFascElenco status : FasStatoFascicoloElenco.TiStatoFascElenco.values()) {
            helper.setStatoFascicoloElenco(elenco, status);
            assertTrue(true);
        }
    }

    @Test
    public void setFasFascicoliStatus_queryIsOk() {
        ElvElencoVersFasc elenco = aElvElencoVersFasc();
        for (it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers status : it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers
                .values()) {
            helper.setFasFascicoliStatus(elenco, status);
            assertTrue(true);
        }
    }

    @Test
    public void getStatoElencoByIdElencoVersFascStato_queryIsOk() {
        Long idElencoVersFasc = aLong();
        for (ElvStatoElencoVersFasc.TiStatoElencoFasc status : ElvStatoElencoVersFasc.TiStatoElencoFasc.values()) {
            try {
                helper.getStatoElencoByIdElencoVersFascStato(idElencoVersFasc, status);
                assertTrue(true);
            } catch (Exception e) {
                assertExceptionMessage(e, "Errore nel reperimento");
            }
        }
    }

    @Test
    public void getElvElencoVersFascDaElabByIdElencoVersFasc_queryIsOk() {
        long idElencoVersFasc = aLong();
        try {
            helper.getElvElencoVersFascDaElabByIdElencoVersFasc(idElencoVersFasc);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void retrieveElvFileElencoVersFasc_queryIsOk() {
        long idElencoVersFasc = aLong();
        String tiFileElencoVers = aString();
        helper.retrieveElvFileElencoVersFasc(idElencoVersFasc, tiFileElencoVers);
        assertTrue(true);
    }

    @Test
    public void retrieveFileIndiceElenco_long_String_queryIsOk() {
        long idElencoVersFasc = aLong();
        String tiFileElencoVers = aString();
        helper.retrieveFileIndiceElenco(idElencoVersFasc, tiFileElencoVers);
        assertTrue(true);
    }

    @Test
    public void getFileIndiceElenco_queryIsOk() {
        long idElencoVersFasc = aLong();
        String tiFileElencoVers = aString();
        helper.getFileIndiceElenco(idElencoVersFasc, tiFileElencoVers);
        assertTrue(true);
    }

    @Test
    public void retrieveFileIndiceElenco_long_StringArr_queryIsOk() {
        long idElencoVersFasc = aLong();
        String[] tiFileElencoVers = aStringArray(2);
        helper.retrieveFileIndiceElenco(idElencoVersFasc, tiFileElencoVers);
        assertTrue(true);
    }

    @Test
    public void contaFascVersati_queryIsOk() {
        Long idElencoVersFasc = aLong();
        helper.contaFascVersati(idElencoVersFasc);
        assertTrue(true);
    }

    @Test
    public void retrieveElenchiIndiciAipFascicoliDaProcessare_queryIsOk() {
        helper.retrieveElenchiIndiciAipFascicoliDaProcessare();
        assertTrue(true);
    }

    @Test
    public void existFascVersAnnullati_queryIsOk() {
        BigDecimal idElencoVersFasc = aBigDecimal();
        helper.existFascVersAnnullati(idElencoVersFasc);
        assertTrue(true);
    }

    @Test
    public void getFasFascicoloVersatiElenco_queryIsOk() {
        BigDecimal idElencoVersFasc = aBigDecimal();
        helper.getFasFascicoloVersatiElenco(idElencoVersFasc);
        assertTrue(true);
    }

    @Test
    public void retrieveFascVersInElenco_queryIsOk() {
        long idElenco = aLong();
        helper.retrieveFascVersInElenco(idElenco);
        assertTrue(true);
    }

    @Test
    public void retrieveFascVersInElencoAipCreato_queryIsOk() {
        long idElenco = 8784221L;
        helper.retrieveFascVersInElencoAipCreato(idElenco);
        assertTrue(true);
    }
}
