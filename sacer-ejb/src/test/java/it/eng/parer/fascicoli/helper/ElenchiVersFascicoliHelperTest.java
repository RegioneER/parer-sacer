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

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aFlag;
import static it.eng.ArquillianUtils.aListOfBigDecimal;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aString;
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

import it.eng.parer.elencoVersFascicoli.utils.ElencoEnums;
import it.eng.parer.web.helper.HelperTest;

@ArquillianTest
public class ElenchiVersFascicoliHelperTest {
    @EJB
    private ElenchiVersFascicoliHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(
                ElenchiVersFascicoliHelperTest.class.getSimpleName(),
                HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""),
                        ElenchiVersFascicoliHelperTest.class, ElenchiVersFascicoliHelper.class,
                        ElencoEnums.class));
    }

    @Test
    void retrieveElvVRicElencoFascList_queryIsOk() {
        long idUserIam = aLong();
        final ElenchiVersFascicoliHelper.Filtri filtri = aFiltri();
        filtri.setIdTipoFascicolo(null);
        filtri.setCdKeyFascicolo(null);
        filtri.setCdKeyFascicoloDa(null);
        filtri.setCdKeyFascicoloA(null);
        filtri.setCdCompositoVoceTitol(null);
        filtri.setAaFascicolo(null);
        filtri.setAaFascicoloDa(null);
        filtri.setAaFascicoloA(null);
        helper.retrieveElvVRicElencoFascList(idUserIam, filtri);
        assertTrue(true);
    }

    private ElenchiVersFascicoliHelper.Filtri aFiltri() {
        ElenchiVersFascicoliHelper.Filtri filtri = new ElenchiVersFascicoliHelper.Filtri();
        filtri.setAaFascicolo(aBigDecimal());
        filtri.setAaFascicoloA(BigDecimal.valueOf(2021));
        filtri.setAaFascicoloDa(BigDecimal.valueOf(2020));
        filtri.setCdCompositoVoceTitol(aString());
        filtri.setCdKeyFascicolo(aString());
        filtri.setCdKeyFascicoloA(aString());
        filtri.setCdKeyFascicoloDa(aString());
        filtri.setCreazioneElencoA(tomorrowTs());
        filtri.setCreazioneElencoDa(todayTs());
        filtri.setFlElencoStandard(aFlag());
        filtri.setIdCriterioRaggrFasc(aBigDecimal());
        filtri.setIdElencoVersFasc(aBigDecimal());
        filtri.setIdTipoFascicolo(aBigDecimal());
        filtri.setNtElencoChiuso(aString());
        filtri.setNtIndiceElenco(aString());
        filtri.setTiStato(aString());
        filtri.setIdAmbiente(aBigDecimal());
        filtri.setIdEnte(aBigDecimal());
        filtri.setIdStrut(aBigDecimal());
        return filtri;
    }

    @Test
    void retrieveElvVRicElencoFascByStatoList_queryIsOk() {
        long idUserIam = aLong();
        ElenchiVersFascicoliHelper.Filtri filtri = aFiltri();
        filtri.setIdTipoFascicolo(null);
        filtri.setCdKeyFascicolo(null);
        filtri.setCdKeyFascicoloA(null);
        filtri.setCdKeyFascicoloDa(null);
        filtri.setCdCompositoVoceTitol(null);
        filtri.setAaFascicolo(null);
        filtri.setAaFascicoloDa(null);
        filtri.setAaFascicoloA(null);
        helper.retrieveElvVRicElencoFascByStatoList(idUserIam, filtri);
        assertTrue(true);
    }

    @Test
    void retrieveElvVRicElencoFascByFasList_queryIsOk() {
        long idUserIam = aLong();
        ElenchiVersFascicoliHelper.Filtri filtri = aFiltri();
        helper.retrieveElvVRicElencoFascByFasList(idUserIam, filtri);
        assertTrue(true);
    }

    @Test
    void getListaElenchiVersFascicoliDaFirmare_7args_queryIsOk() {
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idEnte = aBigDecimal();
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idElencoVersFasc = BigDecimal.ZERO;
        String note = aString();
        Date[] dateCreazioneElencoFascValidate = {
                todayTs(), tomorrowTs() };
        long idUserIam = aLong();
        for (ElencoEnums.ElencoStatusEnum tiStato : ElencoEnums.ElencoStatusEnum.values()) {
            helper.getListaElenchiVersFascicoliDaFirmare(idAmbiente, idEnte, idStrut,
                    idElencoVersFasc, note, tiStato, dateCreazioneElencoFascValidate, idUserIam);
            assertTrue(true);
        }
    }

    @Test
    void getListaElenchiVersFascicoliDaFirmare_List_Long_queryIsOk() {
        List<BigDecimal> idElencoVersFascList = aListOfBigDecimal(2);
        Long idUserIam = aLong();
        helper.getListaElenchiVersFascicoliDaFirmare(idElencoVersFascList, idUserIam);
        assertTrue(true);
    }

    @Test
    void isElencoDeletable_queryIsOk() {
        BigDecimal idElencoVersFasc = aBigDecimal();
        helper.isElencoDeletable(idElencoVersFasc);
        assertTrue(true);
    }

    @Test
    void isElencoClosable_queryIsOk() {
        BigDecimal idElencoVersFasc = aBigDecimal();
        helper.isElencoClosable(idElencoVersFasc);
        assertTrue(true);
    }

    @Test
    void isElencoClosable2_queryIsOk() {
        BigDecimal idElencoVersFasc = aBigDecimal();
        helper.isElencoClosable2(idElencoVersFasc);
        assertTrue(true);
    }

    @Test
    void areFascDeletables_queryIsOk() {
        BigDecimal idElencoVersFasc = aBigDecimal();
        helper.areFascDeletables(idElencoVersFasc);
        assertTrue(true);
    }

    @Test
    void areAllElenchiNonPresentiFirmati_queryIsOk() {
        List<BigDecimal> idElencoVersFascSelezionatiList = aListOfBigDecimal(2);
        Date dataChiusura = todayTs();
        BigDecimal idStrut = aBigDecimal();
        helper.areAllElenchiNonPresentiFirmati(idElencoVersFascSelezionatiList, dataChiusura,
                idStrut);
        assertTrue(true);
    }

    @Test
    void existIdElenco_queryIsOk() {
        BigDecimal idElencoVersFasc = aBigDecimal();
        BigDecimal idStrut = aBigDecimal();
        helper.existIdElenco(idElencoVersFasc, idStrut);
        assertTrue(true);
    }

    @Test
    void retrieveStatiElencoByElencoVersFasc_queryIsOk() {
        BigDecimal idElvElencoVersFasc = aBigDecimal();
        helper.retrieveStatiElencoByElencoVersFasc(idElvElencoVersFasc);
        assertTrue(true);
    }
}
