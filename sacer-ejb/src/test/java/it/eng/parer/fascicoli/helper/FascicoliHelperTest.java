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

package it.eng.parer.fascicoli.helper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.persistence.criteria.CriteriaQuery;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.paginator.helper.LazyListHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.DecCriterioRaggrFasc;
import it.eng.parer.entity.DecTipoFascicolo;
import it.eng.parer.entity.DecVoceTitol;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.slite.gen.tablebean.DecTipoFascicoloTableBean;
import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.web.util.ApplEnum;
import it.eng.spagoLite.db.base.table.BaseTable;

public class FascicoliHelperTest extends HelperTest<FascicoliHelper> {
    @Deployment
    // non uso l'archive che mi fornisce l'HelperTest perché in questo caso ho bisogno di
    // fare un EnterpriseArchive che abbia un modulo separato per il paginator
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(FascicoliHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""),
                        it.eng.parer.fascicoli.dto.RicercaFascicoliBean.class,
                        FascicoliHelper.class, FascicoliHelperTest.class, ApplEnum.class,
                        ElencoEnums.class));
    }

    @EJB
    private LazyListHelper lazyListHelper;

    @Test
    public void retrieveFascicoli_queryIsOk() {
        FascicoliHelper.FiltriRicercaFascicoli filtri = aFiltriRicercaFascicoli();
        BigDecimal idStrut = aBigDecimal();
        long userId = aLong();
        helper.retrieveFascicoli(idStrut, userId, filtri);
        assertTrue(true);
    }

    private FascicoliHelper.FiltriRicercaFascicoli aFiltriRicercaFascicoli() {
        FascicoliHelper.FiltriRicercaFascicoli filtri = new FascicoliHelper.FiltriRicercaFascicoli();
        filtri.setAaFascicolo(aBigDecimal());
        filtri.setAaFascicoloDa(BigDecimal.ONE);
        filtri.setAaFascicoloA(BigDecimal.TEN);
        filtri.setAaFascicoloPadre(aBigDecimal());
        filtri.setAaFascicoloPadreA(BigDecimal.TEN);
        filtri.setAaFascicoloPadreDa(BigDecimal.ONE);
        filtri.setAaKeyUnitaDoc(aBigDecimal());
        filtri.setAaKeyUnitaDocA(BigDecimal.TEN);
        filtri.setAaKeyUnitaDocDa(BigDecimal.ONE);
        filtri.setCdCompositoVoceTitol(aString());
        filtri.setCdKeyFascicolo(aString());
        filtri.setCdKeyFascicoloA(aString());
        filtri.setCdKeyFascicoloDa(aString());
        filtri.setCdKeyFascicoloPadre(aString());
        filtri.setCdKeyFascicoloPadreA(aString());
        filtri.setCdKeyFascicoloPadreDa(aString());
        filtri.setCdKeyUnitaDoc(aString());
        filtri.setCdKeyUnitaDocA(aString());
        filtri.setCdKeyUnitaDocDa(aString());
        filtri.setCdLivelloRiserv(aString());
        filtri.setCdProcAmmin(aString());
        filtri.setCdRegistroKeyUnitaDoc(aString());
        filtri.setDsOggettoFascicolo(aString());
        filtri.setDsOggettoFascicoloPadre(aString());
        filtri.setDsProcAmmin(aString());
        filtri.setDtApeFascioloA(tomorrowTs());
        filtri.setDtApeFascioloDa(todayTs());
        filtri.setDtChiuFascioloA(tomorrowTs());
        filtri.setDtChiuFascioloDa(todayTs());
        filtri.setFlForzaContrClassif(aFlag());
        filtri.setFlForzaContrColleg(aFlag());
        filtri.setFlForzaContrNumero(aFlag());
        filtri.setNiAaConservazione(aBigDecimal());
        filtri.setNmSistemaVersante(aString());
        filtri.setNmTipoFascicolo(aBigDecimal());
        filtri.setNmUserid(aString());
        filtri.setTiConservazione(aString());
        filtri.setTiEsito(aString());
        filtri.setTiStatoConservazione(aString());
        filtri.setTiStatoFascElencoVers(aString());
        filtri.setTsVersFascicoloA(tomorrowTs());
        filtri.setTsVersFascicoloDa(todayTs());
        return filtri;
    }

    @Test
    public void retrieveMonFascicoliByAmbUser_queryIsOk() {
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idUser = aBigDecimal();
        helper.retrieveMonFascicoliByAmbUser(idAmbiente, idUser);
        assertTrue(true);
    }

    @Test
    public void retrieveCntMonFascicoliByAmbUser_queryIsOk() {
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idUser = aBigDecimal();
        helper.retrieveCntMonFascicoliByAmbUser(idAmbiente, idUser);
        assertTrue(true);
    }

    @Test
    public void retrieveMonFascicoliByEnteUser_queryIsOk() {
        BigDecimal idEnte = aBigDecimal();
        BigDecimal idUser = aBigDecimal();
        helper.retrieveMonFascicoliByEnteUser(idEnte, idUser);
        assertTrue(true);
    }

    @Test
    public void retrieveCntMonFascicoliByEnteUser_queryIsOk() {
        BigDecimal idEnte = aBigDecimal();
        BigDecimal idUser = aBigDecimal();
        helper.retrieveCntMonFascicoliByEnteUser(idEnte, idUser);
        assertTrue(true);
    }

    @Test
    public void retrieveMonFascicoliByStrutUser_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idUserIam = aBigDecimal();
        helper.retrieveMonFascicoliByStrutUser(idStrut, idUserIam);
        assertTrue(true);
    }

    @Test
    public void retrieveCntMonFascicoliByStrutUserId_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idUserIam = aBigDecimal();
        helper.retrieveCntMonFascicoliByStrutUserId(idStrut, idUserIam);
        assertTrue(true);
    }

    @Test
    public void retrieveMonFascicoliByTipoFascicolo_queryIsOk() {
        BigDecimal idTipoFascicolo = aBigDecimal();
        helper.retrieveMonFascicoliByTipoFascicolo(idTipoFascicolo);
        assertTrue(true);
    }

    @Test
    public void retrieveCntMonFascicoliByTipoFascicolo_queryIsOk() {
        BigDecimal idTipoFascicolo = aBigDecimal();
        helper.retrieveCntMonFascicoliByTipoFascicolo(idTipoFascicolo);
        assertTrue(true);
    }

    @Test
    public void retrieveMonFascicoliKoByAmbUser_queryIsOk() {
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idUser = aBigDecimal();
        helper.retrieveMonFascicoliKoByAmbUser(idAmbiente, idUser);
        assertTrue(true);
    }

    @Test
    public void retrieveCntMonFascicoliKoByAmbUser_queryIsOk() {
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idUser = aBigDecimal();
        helper.retrieveCntMonFascicoliKoByAmbUser(idAmbiente, idUser);
        assertTrue(true);
    }

    @Test
    public void retrieveMonFascicoliKoByEnteUser_queryIsOk() {
        BigDecimal idEnte = aBigDecimal();
        BigDecimal idUser = aBigDecimal();
        helper.retrieveMonFascicoliKoByEnteUser(idEnte, idUser);
        assertTrue(true);
    }

    @Test
    public void retrieveCntMonFascicoliKoByEnteUser_queryIsOk() {
        BigDecimal idEnte = aBigDecimal();
        BigDecimal idUser = aBigDecimal();
        helper.retrieveCntMonFascicoliKoByEnteUser(idEnte, idUser);
        assertTrue(true);
    }

    @Test
    public void retrieveMonFascicoliKoByStrutUser_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idUserIam = aBigDecimal();
        helper.retrieveMonFascicoliKoByStrutUser(idStrut, idUserIam);
        assertTrue(true);
    }

    @Test
    public void retrieveCntMonFascicoliKoByStrutUserId_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idUserIam = aBigDecimal();
        helper.retrieveCntMonFascicoliKoByStrutUserId(idStrut, idUserIam);
        assertTrue(true);
    }

    @Test
    public void retrieveMonFascicoliKoByTipoFascicolo_queryIsOk() {
        BigDecimal idTipoFascicolo = aBigDecimal();
        helper.retrieveMonFascicoliKoByTipoFascicolo(idTipoFascicolo);
        assertTrue(true);
    }

    @Test
    public void retrieveCntMonFascicoliKoByTipoFascicolo_queryIsOk() {
        BigDecimal idTipoFascicolo = aBigDecimal();
        helper.retrieveCntMonFascicoliKoByTipoFascicolo(idTipoFascicolo);
        assertTrue(true);
    }

    @Test
    public void retrieveSessioniFalliteByIdFascKo_queryIsOk() {
        BigDecimal idFascicoloKo = aBigDecimal();
        helper.retrieveSessioniFalliteByIdFascKo(idFascicoloKo);
        assertTrue(true);
    }

    @Test
    public void retrieveVLisFascKo_queryIsOk() {
        BigDecimal idUser = aBigDecimal();
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idEnte = aBigDecimal();
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idTipoFascicolo = aBigDecimal();
        Date[] dateValidate = aDateArray(2);
        BigDecimal rangeAnnoDa = aBigDecimal();
        BigDecimal rangeAnnoA = aBigDecimal();
        String rangeNumeroDa = aString();
        String rangeNumeroA = aString();
        String statoSessione = aString();
        String cdClasseErr = aString();
        String cdErr = aString();
        final CriteriaQuery criteriaQuery = helper.retrieveVLisFascKoCriteriaQuery(idUser,
                idAmbiente, idEnte, idStrut, idTipoFascicolo, dateValidate, rangeAnnoDa, rangeAnnoA,
                rangeNumeroDa, rangeNumeroA, statoSessione, cdClasseErr, cdErr);
        lazyListHelper.getTableBean(criteriaQuery, l -> new BaseTable());
        assertTrue(true);
    }

    @Test
    public void retrieveVLisFasc_queryIsOk() {
        BigDecimal idUser = aBigDecimal();
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idEnte = aBigDecimal();
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idTipoFascicolo = aBigDecimal();
        Date[] dateValidate = {
                todayTs(), tomorrowTs() };
        BigDecimal rangeAnnoDa = aBigDecimal();
        BigDecimal rangeAnnoA = aBigDecimal();
        String rangeNumeroDa = aString();
        String rangeNumeroA = aString();
        String statoIndiceAip = aString();
        Set<String> statiConservazione = aSetOfString(2);
        String flSesFascicoloKo = aString();
        final CriteriaQuery criteriaQuery = helper.retrieveVLisFascCriteriaQuery(idUser, idAmbiente,
                idEnte, idStrut, idTipoFascicolo, dateValidate, rangeAnnoDa, rangeAnnoA,
                rangeNumeroDa, rangeNumeroA, statoIndiceAip, statiConservazione, flSesFascicoloKo);
        lazyListHelper.getTableBean(criteriaQuery, l -> new BaseTable());
        assertTrue(true);
    }

    @Test
    public void existFascicoliVersatiPerTipoFascicolo_queryIsOk() {
        BigDecimal idTipoFascicolo = aBigDecimal();
        BigDecimal aaIniTipoFascicolo = aBigDecimal();
        BigDecimal aaFinTipoFascicolo = aBigDecimal();
        helper.existFascicoliVersatiPerTipoFascicolo(idTipoFascicolo, aaIniTipoFascicolo,
                aaFinTipoFascicolo);
        assertTrue(true);
    }

    @Test
    public void existFascicoliVersatiPerModelloFascicolo_queryIsOk() {
        BigDecimal idModelloXsdFascicolo = aBigDecimal();
        BigDecimal idTipoFascicolo = aBigDecimal();
        helper.existFascicoliVersatiPerModelloFascicolo(idModelloXsdFascicolo, idTipoFascicolo);
        assertTrue(true);
    }

    @Test
    public void existFascicoliVersatiPerStruttura_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        helper.existFascicoliVersatiPerStruttura(idStrut);
        assertTrue(true);
    }

    @Test
    public void getDecSelCriterioRaggrFascList_DecCriterioRaggrFasc_DecVoceTitol_queryIsOk() {
        DecCriterioRaggrFasc record = aDecCriterioRaggrFasc();
        DecVoceTitol voce = aDecVoceTitol();
        helper.getDecSelCriterioRaggrFascList(record, voce);
        assertTrue(true);
    }

    @Test
    public void getDecVoceTitolList_queryIsOk() {
        List<BigDecimal> voceTitolList = aListOfBigDecimal(2);
        helper.getDecVoceTitolList(voceTitolList);
        assertTrue(true);
    }

    @Test
    public void getDecSelCriterioRaggrFascList_DecCriterioRaggrFasc_DecTipoFascicolo_queryIsOk() {
        DecCriterioRaggrFasc record = aDecCriterioRaggrFasc();
        DecTipoFascicolo tipo = aDecTipoFascicolo();
        helper.getDecSelCriterioRaggrFascList(record, tipo);
        assertTrue(true);
    }

    @Test
    public void getDecTipoFascicoloList_queryIsOk() {
        StringBuilder queryStr = new StringBuilder("SELECT u FROM DecTipoFascicolo u ");
        queryStr.append("WHERE u.idTipoFascicolo in :idtipofascicolo");
        List<BigDecimal> asList = aListOfBigDecimal(2);
        helper.getDecTipoFascicoloList(queryStr, asList);
        assertTrue(true);
    }

    @Test
    public void getOrgStrut_queryIsOk() {
        BigDecimal idStruttura = aBigDecimal();
        try {
            helper.getOrgStrut(idStruttura);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void getDecCriterioRaggrFascByStrutAndCriterio_queryIsOk() {
        BigDecimal idStruttura = aBigDecimal();
        String nmCriterioRaggr = aString();
        try {
            helper.getDecCriterioRaggrFascByStrutAndCriterio(idStruttura, nmCriterioRaggr);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void existElvElencoVersPerCriterioRaggrFasc_queryIsOk() {
        BigDecimal idCriterioRaggrFasc = aBigDecimal();
        helper.existElvElencoVersPerCriterioRaggrFasc(idCriterioRaggrFasc);
        assertTrue(true);
    }

    @Test
    public void getTipiFascicoloAbilitatiDaStrutturaList_queryIsOk() {
        long idUtente = aLong();
        List<BigDecimal> idStrutturaList = aListOfBigDecimal(2);
        helper.getTipiFascicoloAbilitatiDaStrutturaList(idUtente, idStrutturaList);
        assertTrue(true);
    }

    @Test
    public void getListaFasFascicoloElvViewBean_queryIsOk() {
        BigDecimal idElencoVersFasc = aBigDecimal();
        FascicoliHelper.FiltriElenchiVersFascicoli filtri = new FascicoliHelper.FiltriElenchiVersFascicoli();
        filtri.setAaFascicolo(aBigDecimal());
        filtri.setAaFascicoloA(BigDecimal.TEN);
        filtri.setAaFascicoloDa(BigDecimal.ONE);
        filtri.setCdCompositoVoceTitolo(aString());
        filtri.setCdKeyFascicolo(aString());
        filtri.setCdKeyFascicoloA(aString());
        filtri.setCdKeyFascicoloDa(aString());
        filtri.setDtApeFascicoloA(tomorrowTs());
        filtri.setDtApeFascicoloDa(todayTs());
        filtri.setDtChiuFascicoloA(tomorrowTs());
        filtri.setDtChiuFascicoloDa(todayTs());
        filtri.setIdTipoFascicolo(aBigDecimal());
        filtri.setTsIniSesA(tomorrowTs());
        filtri.setTsIniSesDa(todayTs());
        DecTipoFascicoloTableBean tmpTableBeanTipoFasc = new DecTipoFascicoloTableBean();
        helper.getListaFasFascicoloElvViewBean(idElencoVersFasc, tmpTableBeanTipoFasc, filtri);
        assertTrue(true);
    }

    @Test
    public void retrieveFasVLisFascByRichann_queryIsOk() {
        long idRichAnnulVers = aLong();
        helper.retrieveFasVLisFascByRichann(idRichAnnulVers);
        assertTrue(true);
    }

    @Test
    public void getFasFileMetaVerAipFasc_queryIsOk() {
        long idFascicolo = aLong();
        String tiMeta = aString();
        helper.getFasFileMetaVerAipFasc(idFascicolo, tiMeta);
        assertTrue(true);
    }

    @Test
    public void getIdFascVersatoNoAnnul_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        BigDecimal aaFascicolo = aBigDecimal();
        String cdKeyFascicolo = aString();
        helper.getIdFascVersatoNoAnnul(idStrut, aaFascicolo, cdKeyFascicolo);
        assertTrue(true);
    }

    @Test
    public void existsFascicolo_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        BigDecimal aaFascicolo = aBigDecimal();
        String cdKeyFascicolo = aString();
        helper.existsFascicolo(idStrut, aaFascicolo, cdKeyFascicolo);
        assertTrue(true);
    }

    @Test
    public void getAroRichAnnulVersFasc_queryIsOk() {
        helper.getAroRichAnnulVersFasc(aLong());
        assertTrue(true);
    }

    @Test
    public void retrieveFascicoliAnnullati_queryIsOk() {
        helper.retrieveFascicoliAnnullati(aFiltriRicercaFascicoli(), aBigDecimal(), aLong());
        assertTrue(true);
    }

    @Test
    public void retrieveSelCriterioRaggrFascicoli_queryIsOk() {
        helper.retrieveSelCriterioRaggrFascicoli(aBigDecimal(), aString());
        assertTrue(true);
    }

    @Test
    public void retrieveFasElvFascicolo_queryIsOk() {
        helper.retrieveFasElvFascicolo(aLong());
        assertTrue(true);
    }

    @Test
    public void getDecCriterioRaggrFasc_queryIsOk() {
        helper.getDecCriterioRaggrFasc(aBigDecimal(), aString());
        assertTrue(true);
    }

    @Test
    public void existNomeCriterio_queryIsOk() {
        helper.existNomeCriterio(aString(), aBigDecimal());
        assertTrue(true);
    }

    @Test
    public void retrieveCriteriRaggrFascicoli_queryIsOk() {
        final FascicoliHelper.FiltriCriteriRaggrFascicoliPlain filtri = new FascicoliHelper.FiltriCriteriRaggrFascicoliPlain();
        filtri.setAaFascicolo(BigDecimal.valueOf(2020));
        filtri.setCdCompositoVoceTitol(aString());
        filtri.setCdCompositoVoceTitol(aFlag());
        filtri.setFlCriterioRaggrStandard(aFlag());
        filtri.setIdAmbiente(aBigDecimal());
        filtri.setIdEnte(aBigDecimal());
        filtri.setIdStrut(aBigDecimal());
        filtri.setIdTipoFascicolo(aBigDecimal());
        filtri.setNmCriterioRaggr(aString());
        filtri.setCriterioAttivo(aFlag());
        helper.retrieveCriteriRaggrFascicoli(true, filtri);
        assertTrue(true);
    }

    @Test
    public void retrieveMonContaFascicoliByChiaveTotaliz_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        Date dtRifConta = todayTs();
        BigDecimal idTipoFascicolo = aBigDecimal();
        BigDecimal aaFascicolo = aBigDecimal();
        BigDecimal idUserIam = aBigDecimal();
        helper.retrieveMonContaFascicoliByChiaveTotaliz(idStrut, dtRifConta, idTipoFascicolo,
                aaFascicolo, idUserIam);
        assertTrue(true);
    }

    @Test
    public void retrieveMonContaFascicoliNonVersByChiaveTotaliz_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        Date dtRifConta = todayTs();
        BigDecimal idTipoFascicolo = aBigDecimal();
        BigDecimal aaFascicolo = aBigDecimal();
        String tiStatoFacicolo = aString();
        helper.retrieveMonContaFascicoliNonVersByChiaveTotaliz(idStrut, dtRifConta, idTipoFascicolo,
                aaFascicolo, tiStatoFacicolo);
        assertTrue(true);
    }

    @Test
    public void retrieveFasVLisUdInFasc_queryIsOk() {
        long idFascicolo = aLong();
        long userId = aLong();
        helper.retrieveFasVLisUdInFasc(idFascicolo, userId);
        assertTrue(true);
    }

    @Test
    public void getSessioneFallitaByIdSess_queryIsOk() {
        final BigDecimal idSesFascicoloKo = BigDecimal.ZERO;
        helper.getSessioneFallitaByIdSess(idSesFascicoloKo);
        assertTrue(true);
    }

    @Test
    public void bulkDeleteCriteriRaggrFasc_queryIsOk() {
        helper.bulkDeleteCriteriRaggrFasc(aListOfLong(2));
        assertTrue(true);
    }

    @Test
    public void bulkUpdateCriteriRaggrFasc_queryIsOk() {
        helper.bulkUpdateCriteriRaggrFasc(aListOfLong(2));
        assertTrue(true);
    }

    @Test
    public void countSelCriteriRaggrFascPerTipo_queryIsOk() {
        final BigDecimal idCriterioRaggrFasc = BigDecimal.ZERO;
        final String tiSel = "tiSel";
        helper.countSelCriteriRaggrFascPerTipo(idCriterioRaggrFasc, tiSel);
        assertTrue(true);
    }

    @Test
    public void retrieveFasVVisFascicolo_queryIsOk() {
        final long idFascicolo = 0L;
        try {
            helper.retrieveFasVVisFascicolo(idFascicolo);
            fail("non dovrebbe trovare fascicoli con id " + idFascicolo);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void retrieveFasVRicFascicoli_queryIsOk() {
        final long idFascicolo = 0L;
        try {
            helper.retrieveFasVRicFascicoli(idFascicolo);
            fail("non dovrebbe trovare fascicoli con id " + idFascicolo);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void findCountFascicoliVersatiNelGiorno_queryIsOk() {
        final Timestamp data = tomorrowTs();
        helper.findCountFascicoliVersatiNelGiorno(data);
        assertTrue(true);
    }

    @Test
    public void findCountFascicoliNonVersatiNelGiorno_queryIsOk() {
        final Timestamp data = tomorrowTs();
        helper.findCountFascicoliNonVersatiNelGiorno(data);
        assertTrue(true);
    }

    @Test
    public void retrieveFasFascicoloByStrutAnnoNumValid_queryIsOk() {
        final OrgStrut strut = anOrgStrut();
        final long anno = -1900L;
        final String numero = "numero";
        helper.retrieveFasFascicoloByStrutAnnoNumValid(strut, anno, numero);
        assertTrue(true);
    }

    private OrgStrut anOrgStrut() {
        final OrgStrut strut = new OrgStrut();
        strut.setIdStrut(0L);
        return strut;
    }

    @Test
    public void retrieveFasNonVersatoByStrutAnnoNum_queryIsOk() {
        final int anno = -1900;
        final String numero = "numero";
        helper.retrieveFasNonVersatoByStrutAnnoNum(anOrgStrut(), anno, numero, false);
        helper.retrieveFasNonVersatoByStrutAnnoNum(anOrgStrut(), anno, numero, true);
        assertTrue(true);
    }

    @Test
    public void retrieveFasAmminPartec_queryIsOk() {
        final long idFascicolo = 0L;
        helper.retrieveFasAmminPartec(idFascicolo);
        assertTrue(true);
    }

    @Test
    public void retrieveFasSogFascicolo_queryIsOk() {
        final long idFascicolo = 0L;
        helper.retrieveFasSogFascicolo(idFascicolo);
        assertTrue(true);
    }

    @Test
    public void retrieveFasRespFascicolo_queryIsOk() {
        final long idFascicolo = 0L;
        helper.retrieveFasRespFascicolo(idFascicolo);
        assertTrue(true);
    }

    @Test
    public void retrieveFasUniOrgRespFascicolo_queryIsOk() {
        final long idFascicolo = 0L;
        helper.retrieveFasUniOrgRespFascicolo(idFascicolo);
        assertTrue(true);
    }

    @Test
    public void retrieveFasLinkFascicolo_queryIsOk() {
        final long idFascicolo = 0L;
        helper.retrieveFasLinkFascicolo(idFascicolo);
        assertTrue(true);
    }

    @Test
    public void retrieveFasLinkFascicoloParent_queryIsOk() {
        final long idFascicolo = 0L;
        helper.retrieveFasLinkFascicoloParent(idFascicolo);
        assertTrue(true);
    }

    @Test
    public void retrieveClasseErrSacerByTipiUso_queryIsOk() {
        final List<String> tipiUsoErr = aListOfString(2);
        helper.retrieveClasseErrSacerByTipiUso(tipiUsoErr);
        assertTrue(true);
    }

    @Test
    public void retrieveClasseErrSacerByCodice_queryIsOk() {
        final String cdClasseErrSacer = "cdClasseErrSacer";
        helper.retrieveClasseErrSacerByCodice(cdClasseErrSacer);
        assertTrue(true);
    }

    @Test
    public void retrieveVrsVUpdFascicoloKoByFascKo_queryIsOk() {
        final long idFascicoloKo = 0L;
        helper.retrieveVrsVUpdFascicoloKoByFascKo(idFascicoloKo);
        assertTrue(true);
    }

    @Test
    public void retrieveErrSacerByCodClasse_queryIsOk() {
        final String codClasse = "codClasse";
        helper.retrieveErrSacerByCodClasse(codClasse);
        assertTrue(true);
    }

    @Test
    public void retrieveDettSessFascErr_queryIsOk() {
        final BigDecimal idSess = BigDecimal.ZERO;
        helper.retrieveDettSessFascErr(idSess);
        assertTrue(true);
    }

    @Test
    public void retrieveMonContaFascicoliKoByChiaveTotaliz_queryIsOk() {
        final Timestamp dtRifConta = tomorrowTs();
        final OrgStrut orgStrut = anOrgStrut();
        final String tiStatoFascicoloKo = "tiStatoFascicoloKo";
        final long anno = -1900L;
        final DecTipoFascicolo decTipoFascicolo = aDecTipoFascicolo();
        helper.retrieveMonContaFascicoliKoByChiaveTotaliz(dtRifConta, orgStrut, tiStatoFascicoloKo,
                anno, decTipoFascicolo, false);
        helper.retrieveMonContaFascicoliKoByChiaveTotaliz(dtRifConta, orgStrut, tiStatoFascicoloKo,
                anno, decTipoFascicolo, true);
        assertTrue(true);
    }

    @Test
    public void getDecCriterioRaggrFascById_queryIsOk() {
        final BigDecimal idCriterioRaggrFasc = BigDecimal.ZERO;
        helper.getDecCriterioRaggrFascById(idCriterioRaggrFasc);
        assertTrue(true);
    }

    @Test
    public void getTipiFascicoloAbilitati_queryIsOk() {
        helper.getTipiFascicoloAbilitati(0L, BigDecimal.ZERO);
        assertTrue(true);
    }

    @Test
    public void deleteDecSelCriterioRaggrFascNotInVoceTitolo_queryIsOk() {
        helper.deleteDecSelCriterioRaggrFascNotInVoceTitolo(aListOfBigDecimal(2),
                aDecCriterioRaggrFasc());
        assertTrue(true);
    }

    @Test
    public void getDecSelCriterioRaggrFascList_queryIsOk() {
        helper.getDecSelCriterioRaggrFascList(aDecCriterioRaggrFasc(), aDecVoceTitol());
        assertTrue(true);
    }

    @Test
    public void deleteDecSelCriterioRaggrFascFiltroVoceTitolo_queryIsOk() {
        helper.deleteDecSelCriterioRaggrFascFiltroVoceTitolo(aDecCriterioRaggrFasc());
        assertTrue(true);
    }

    @Test
    public void deleteDecSelCriterioRaggrFascFiltroTipoFascicolo_queryIsOk() {
        helper.deleteDecSelCriterioRaggrFascFiltroTipoFascicolo(aDecCriterioRaggrFasc());
        assertTrue(true);
    }

    @Test
    public void deleteDecSelCriterioRaggrFascNotInTipoFascicolo_queryIsOk() {
        helper.deleteDecSelCriterioRaggrFascNotInTipoFascicolo(aDecCriterioRaggrFasc(),
                aListOfBigDecimal(3));
        assertTrue(true);
    }

    @Test
    public void getDecSelCriterioRaggrFascList2_queryIsOk() {
        helper.getDecSelCriterioRaggrFascList(aDecCriterioRaggrFasc(), aDecTipoFascicolo());
        assertTrue(true);
    }
}
