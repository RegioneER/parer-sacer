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
import static it.eng.ArquillianUtils.aFlag;
import static it.eng.ArquillianUtils.aListOfBigDecimal;
import static it.eng.ArquillianUtils.aListOfString;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.aStringArray;
import static it.eng.ArquillianUtils.todayTs;
import static it.eng.ArquillianUtils.tomorrowTs;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.AggiornamentoInElenco;
import it.eng.parer.elencoVersamento.utils.ComponenteDaVerificare;
import it.eng.parer.elencoVersamento.utils.ComponenteInElenco;
import it.eng.parer.elencoVersamento.utils.DocUdObj;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.elencoVersamento.utils.UnitaDocumentariaInElenco;
import it.eng.parer.elencoVersamento.utils.UpdDocUdObj;
import it.eng.parer.volume.utils.ReturnParams;
import it.eng.parer.web.dto.DefinitoDaBean;

@ArquillianTest
public class ElenchiVersamentoHelperTest {
    @EJB
    private ElenchiVersamentoHelper helper;

    private ElenchiVersamentoHelper.Filtri aFiltri() {
        ElenchiVersamentoHelper.Filtri filtri = new ElenchiVersamentoHelper.Filtri();
        filtri.setAaKeyUnitaDoc(BigDecimal.valueOf(2020));
        filtri.setAaKeyUnitaDocDa(BigDecimal.valueOf(2019));
        filtri.setAaKeyUnitaDocA(BigDecimal.valueOf(2021));
        filtri.setCdKeyUnitaDoc(aString());
        filtri.setCdKeyUnitaDocA(aString());
        filtri.setCdKeyUnitaDocDa(aString());
        filtri.setCdRegistroKeyUnitaDoc(aString());
        filtri.setCreazioneElencoA(tomorrowTs());
        filtri.setCreazioneElencoDa(todayTs());
        filtri.setCreazioneElencoIxAipA(tomorrowTs());
        filtri.setCreazioneElencoIxAipDa(todayTs());
        filtri.setDsElenco(aString());
        filtri.setFirmaElencoIxAipA(tomorrowTs());
        filtri.setFirmaElencoIxAipDa(todayTs());
        filtri.setFlElencoFirmato(aFlag());
        filtri.setFlElencoFisc(aFlag());
        filtri.setFlElencoIndiciAipCreato(aFlag());
        filtri.setFlElencoStandard(aFlag());
        filtri.setHhStatoElencoInCodaJms(aBigDecimal());
        filtri.setIdElencoVers(aBigDecimal());
        filtri.setNmCriterioRaggr(aString());
        filtri.setNmElenco(aString());
        filtri.setNtElencoChiuso(aString());
        filtri.setNtIndiceElenco(aString());
        filtri.setTiGestElenco(aString());
        filtri.setTiStatoElenco(aListOfString(2));
        filtri.setValidazioneElencoA(tomorrowTs());
        filtri.setValidazioneElencoDa(todayTs());
        filtri.setIdAmbiente(aBigDecimal());
        filtri.setIdEnte(aBigDecimal());
        filtri.setIdStrut(aBigDecimal());
        return filtri;
    }

    @Test
    void retrieveElvVRicElencoVersByStatoList_queryIsOk() {
        long idUserIam = aLong();
        ElenchiVersamentoHelper.Filtri filtri = aFiltri();
        filtri.setCdRegistroKeyUnitaDoc(null);
        filtri.setAaKeyUnitaDoc(null);
        filtri.setAaKeyUnitaDocDa(null);
        filtri.setAaKeyUnitaDocA(null);
        filtri.setCdKeyUnitaDoc(null);
        filtri.setCdKeyUnitaDocDa(null);
        filtri.setCdKeyUnitaDocA(null);
        helper.retrieveElvVRicElencoVersByStatoList(idUserIam, filtri);
        assertTrue(true);
    }

    @Test
    void retrieveElvVRicElencoVersList_queryIsOk() {
        long idUserIam = aLong();
        ElenchiVersamentoHelper.Filtri filtri = aFiltri();
        filtri.setCdRegistroKeyUnitaDoc(null);
        filtri.setAaKeyUnitaDoc(null);
        filtri.setAaKeyUnitaDocDa(null);
        filtri.setAaKeyUnitaDocA(null);
        filtri.setCdKeyUnitaDoc(null);
        filtri.setCdKeyUnitaDocDa(null);
        filtri.setCdKeyUnitaDocA(null);
        filtri.setHhStatoElencoInCodaJms(null);
        helper.retrieveElvVRicElencoVersList(idUserIam, filtri);
        assertTrue(true);
    }

    @Test
    void retrieveElvVRicElencoVersByUdList_queryIsOk() {
        long idUserIam = aLong();
        ElenchiVersamentoHelper.Filtri filtri = aFiltri();
        filtri.setHhStatoElencoInCodaJms(null);
        helper.retrieveElvVRicElencoVersByUdList(idUserIam, filtri);
        assertTrue(true);
    }

    @Test
    void getListaElenchiDaFirmare_9args_queryIsOk() {
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idEnte = aBigDecimal();
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idElencoVers = BigDecimal.ZERO;
        String note = aString();
        String flElencoFisc = aString();
        List<String> tiGestElenco = aListOfString(2);
        Date[] dateCreazioneElencoValidate = {
                todayTs(), tomorrowTs() };
        long idUserIam = aLong();
        String[] statiElenco = aStringArray(2);
        helper.getListaElenchiDaFirmare(idAmbiente, idEnte, idStrut, idElencoVers, note,
                flElencoFisc, tiGestElenco, dateCreazioneElencoValidate, idUserIam, statiElenco);
        assertTrue(true);
    }

    @Test
    void getListaElenchiDaFirmare_List_Long_queryIsOk() {
        List<BigDecimal> idElencoVersList = aListOfBigDecimal(2);
        Long idUserIam = aLong();
        helper.getListaElenchiDaFirmare(idElencoVersList, idUserIam);
        assertTrue(true);
    }

    @Test
    void countElencIndiciAipInStates_queryIsOk() {
        long idUserIam = aLong();
        List<String> elencoStates = aListOfString(2);
        helper.countElencIndiciAipInStates(idUserIam, elencoStates);
        assertTrue(true);
    }

    @Test
    void isElencoDeletable_queryIsOk() {
        BigDecimal idElencoVers = aBigDecimal();
        helper.isElencoDeletable(idElencoVers);
        assertTrue(true);
    }

    @Test
    void isElencoClosable_queryIsOk() {
        BigDecimal idElencoVers = aBigDecimal();
        helper.isElencoClosable(idElencoVers);
        assertTrue(true);
    }

    @Test
    void isElencoValidable_queryIsOk() {
        BigDecimal idElencoVers = aBigDecimal();
        helper.isElencoValidable(idElencoVers);
        assertTrue(true);
    }

    @Test
    void areUdDocDeletables_queryIsOk() {
        BigDecimal idElencoVers = aBigDecimal();
        helper.areUdDocDeletables(idElencoVers);
        assertTrue(true);
    }

    @Test
    void areUpdDeletables_queryIsOk() {
        BigDecimal idElencoVers = aBigDecimal();
        helper.areUpdDeletables(idElencoVers);
        assertTrue(true);
    }

    @Test
    void areAllElenchiNonPresentiFirmati_queryIsOk() {
        List<BigDecimal> idElencoVersSelezionatiList = aListOfBigDecimal(2);
        Date dataChiusura = todayTs();
        BigDecimal idStrut = aBigDecimal();
        helper.areAllElenchiNonPresentiFirmati(idElencoVersSelezionatiList, dataChiusura, idStrut);
        assertTrue(true);
    }

    @Test
    void existNomeElenco_queryIsOk() {
        String nmElenco = aString();
        BigDecimal idStrut = aBigDecimal();
        helper.existNomeElenco(nmElenco, idStrut);
        assertTrue(true);
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(ElenchiVersamentoHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""),
                        ElenchiVersamentoHelperTest.class, ElenchiVersamentoHelper.class,
                        ElencoVersamentoHelper.class, ReturnParams.class, DefinitoDaBean.class,
                        ElencoEnums.class, ComponenteInElenco.class, ComponenteDaVerificare.class,
                        UpdDocUdObj.class, UnitaDocumentariaInElenco.class, DocUdObj.class,
                        AggiornamentoInElenco.class, it.eng.parer.ws.dto.CSVersatore.class,
                        it.eng.parer.ws.dto.CSChiave.class,
                        it.eng.parer.job.dto.SessioneVersamentoExt.class));
    }
}
