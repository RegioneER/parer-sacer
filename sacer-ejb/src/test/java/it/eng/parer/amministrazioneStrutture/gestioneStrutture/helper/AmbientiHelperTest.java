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
package it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper;

import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.web.util.ApplEnum;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * @author manuel.bertuzzi@eng.it
 */

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class AmbientiHelperTest {
    @EJB
    private AmbientiHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(AmbientiHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), AmbientiHelperTest.class, AmbientiHelper.class,
                        ApplEnum.class));
    }

    @Test
    public void getOrgEnteByName_queryIsOk() {
        String nmEnte = aString();
        BigDecimal idAmb = aBigDecimal();
        helper.getOrgEnteByName(nmEnte, idAmb);
        assertTrue(true);
    }

    @Test
    public void getOrgAmbienteByName_queryIsOk() {
        String nmAmbiente = aString();
        helper.getOrgAmbienteByName(nmAmbiente);
        assertTrue(true);
    }

    @Test
    public void getOrgAmbitoTerritList_queryIsOk() {
        String tipo = aString();
        helper.getOrgAmbitoTerritList(tipo);
        assertTrue(true);
    }

    @Test
    public void getOrgAmbitoTerritByCode_queryIsOk() {
        String cdAmbitoTerritoriale = aString();
        helper.getOrgAmbitoTerritByCode(cdAmbitoTerritoriale);
        assertTrue(true);
    }

    @Test
    public void getOrgAmbitoTerritChildList_BigDecimal_queryIsOk() {
        BigDecimal idAmbitoTerritoriale = aBigDecimal();
        helper.getOrgAmbitoTerritChildList(idAmbitoTerritoriale);
        assertTrue(true);
    }

    @Test
    public void getOrgAmbitoTerritChildList_List_queryIsOk() {
        List<BigDecimal> idAmbitoTerrit = aListOfBigDecimal(2);
        helper.getOrgAmbitoTerritChildList(idAmbitoTerrit);
        assertTrue(true);
    }

    @Test
    public void getOrgCategEnteList_queryIsOk() {
        String cdCategEnte = aString();
        String dsCategEnte = aString();
        helper.getOrgCategEnteList(cdCategEnte, dsCategEnte);
        assertTrue(true);
    }

    @Test
    public void getOrgCategEnteByCd_queryIsOk() {
        String cdCategEnte = aString();
        helper.getOrgCategEnteByCd(cdCategEnte);
        assertTrue(true);
    }

    @Test
    public void getOrgCategStrutList_queryIsOk() {
        helper.getOrgCategStrutList();
        assertTrue(true);
    }

    @Test
    public void getUsrVChkCreaAmbSacer_queryIsOk() {
        long idUser = aLong();
        String nmApplic = aString();
        try {
            helper.getUsrVChkCreaAmbSacer(idUser, nmApplic);
        } catch (Exception e) {
            assertNoResultException(e);
        }
        assertTrue(true);
    }

    @Test
    public void getAmbientiAbilitatiPerEnte_queryIsOk() {
        long idUser = aLong();
        String nmApplic = aString();
        helper.getAmbientiAbilitatiPerEnte(idUser, nmApplic);
        assertTrue(true);
    }

    @Test
    public void getAmbientiAbilitatiPerStrut_queryIsOk() {
        long idUser = aLong();
        String nmApplic = aString();
        helper.getAmbientiAbilitatiPerStrut(idUser, nmApplic);
        assertTrue(true);
    }

    @Test
    public void getEntiAbilitatiPerStrut_queryIsOk() {
        long idUser = aLong();
        String nmApplic = aString();
        String nmEnte = aString();
        BigDecimal idAmbiente = aBigDecimal();
        List<String> tipoDefTemplateEnte = aListOfString(2);
        helper.getEntiAbilitatiPerStrut(idUser, nmApplic, nmEnte, idAmbiente, tipoDefTemplateEnte);
        assertTrue(true);
    }

    @Test
    public void getAmbientiAbilitatiRicerca_queryIsOk() {
        long idUser = aLong();
        String nmAmbiente = aString();
        helper.getAmbientiAbilitatiRicerca(idUser, nmAmbiente);
        assertTrue(true);
    }

    @Test
    public void getEntiAbilitatiRicerca_queryIsOk() {
        long idUtente = aLong();
        BigDecimal idAmbiente = aBigDecimal();
        String nmEnte = aString();
        String tipoDefTemplateEnte = aString();
        helper.getEntiAbilitatiRicerca(idUtente, idAmbiente, nmEnte, tipoDefTemplateEnte);
        assertTrue(true);
    }

    @Test
    public void getEntiValidiAmbiente_queryIsOk() {
        BigDecimal idAmbiente = aBigDecimal();
        helper.getEntiValidiAmbiente(idAmbiente);
        assertTrue(true);
    }

    @Test
    public void getUtentiAttiviAbilitatiAdAmbiente_queryIsOk() {
        BigDecimal idAmbiente = aBigDecimal();
        helper.getUtentiAttiviAbilitatiAdAmbiente(idAmbiente);
        assertTrue(true);
    }

    @Test
    public void getUtentiAttiviAbilitatiAdAmbiente2_queryIsOk() {
        BigDecimal idAmbiente = aBigDecimal();
        helper.getUtentiAttiviAbilitatiAdAmbiente2(idAmbiente);
        assertTrue(true);
    }

    @Test
    public void getUtentiAttiviAbilitatiAdEnte_queryIsOk() {
        BigDecimal idEnte = aBigDecimal();
        helper.getUtentiAttiviAbilitatiAdEnte(idEnte);
        assertTrue(true);
    }

    @Test
    public void getNmEnteConvenz_queryIsOk() {
        String nmApplic = aString();
        String nmTipoOrganiz = aString();
        BigDecimal idOrganizApplic = aBigDecimal();
        helper.getNmEnteConvenz(nmApplic, nmTipoOrganiz, idOrganizApplic);
        assertTrue(true);
    }

    @Test
    public void retrieveOrgAmbienteFromAbil_queryIsOk() {
        long idUtente = aLong();
        helper.retrieveOrgAmbienteFromAbil(idUtente);
        assertTrue(true);
    }

    @Test
    public void retrieveOrgEnteAbilNoTemplate_queryIsOk() {
        long idUtente = aLong();
        Long idAmbiente = aLong();
        helper.retrieveOrgEnteAbilNoTemplate(idUtente, idAmbiente, true);
        assertTrue(true);
    }

    @Test
    public void retrieveOrgEnteAbil_4args_queryIsOk() {
        long idUtente = aLong();
        Long idAmbiente = aLong();
        List<BigDecimal> idAmbitoTerritList = aListOfBigDecimal(2);
        List<BigDecimal> idCategEnteList = aListOfBigDecimal(2);
        helper.retrieveOrgEnteAbil(idUtente, idAmbiente, idAmbitoTerritList, idCategEnteList, true);
        assertTrue(true);
    }

    @Test
    public void retrieveOrgEnteAbil_5args_queryIsOk() {
        long idUtente = aLong();
        Long idAmbiente = aLong();
        List<BigDecimal> idAmbitoTerritList = aListOfBigDecimal(2);
        List<BigDecimal> idCategEnteList = aListOfBigDecimal(2);
        String[] tipoDefTemplateEnte = aStringArray(2);
        helper.retrieveOrgEnteAbil(idUtente, idAmbiente, idAmbitoTerritList, idCategEnteList, true,
                tipoDefTemplateEnte);
        assertTrue(true);
    }

    @Test
    public void retrieveSiOrgEnteConvenz_queryIsOk() {
        BigDecimal idAmbienteEnteConvenz = aBigDecimal();
        helper.retrieveSiOrgEnteConvenz(idAmbienteEnteConvenz);
        assertTrue(true);
    }

    @Test
    public void retrieveSiOrgEnteConvenzAccordoValido_queryIsOk() {
        BigDecimal idAmbienteEnteConvenz = aBigDecimal();
        helper.retrieveSiOrgEnteConvenzAccordoValido(idAmbienteEnteConvenz);
        assertTrue(true);
    }

    @Test
    public void getEntiConvenzionatiAbilitati_queryIsOk() {
        long idUserIamCor = aLong();
        BigDecimal idAmbienteEnteConvenz = aBigDecimal();
        helper.getEntiConvenzionatiAbilitati(idUserIamCor, idAmbienteEnteConvenz);
        assertTrue(true);
    }

    @Test
    public void countOrgEnteConvenzByAmbitoTerrit_queryIsOk() {
        BigDecimal idAmbitoTerrit = aBigDecimal();
        helper.countOrgEnteConvenzByAmbitoTerrit(idAmbitoTerrit);
        assertTrue(true);
    }

    @Test
    public void countOrgStoEnteConvenzByAmbitoTerrit_queryIsOk() {
        BigDecimal idAmbitoTerrit = aBigDecimal();
        helper.countOrgStoEnteConvenzByAmbitoTerrit(idAmbitoTerrit);
        assertTrue(true);
    }

    @Test
    public void retrieveAmbientiEntiConvenzAbilitati_queryIsOk() {
        BigDecimal idUserIam = aBigDecimal();
        helper.retrieveAmbientiEntiConvenzAbilitati(idUserIam);
        assertTrue(true);
    }

    @Test
    public void retrieveSIOrgEnteConvenzOrg_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        helper.retrieveSIOrgEnteConvenzOrg(idStrut);
        assertTrue(true);
    }

    @Test
    public void getSIOrgEnteConvenzOrg_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idEnteConvenz = aBigDecimal();
        Date dtIniVal = todayTs();
        try {
            helper.getSIOrgEnteConvenzOrg(idStrut, idEnteConvenz, dtIniVal);
        } catch (Exception e) {
            assertNoResultException(e);
        }
        assertTrue(true);
    }

    @Test
    public void getSIOrgAmbienteEnteConvenzByEnteConvenz_queryIsOk() {
        BigDecimal idEnteConvenz = aBigDecimal();
        try {
            helper.getSIOrgAmbienteEnteConvenzByEnteConvenz(idEnteConvenz);
        } catch (Exception e) {
            assertNoResultException(e);
        }
        assertTrue(true);
    }

    @Test
    public void getSIUsrOrganizIam_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        helper.getSIUsrOrganizIam(idStrut);
        assertTrue(true);
    }

    @Test
    public void checkOrgVChkServFattByStrut_queryIsOk() {
        long idEnteConvenz = aLong();
        long idStrut = aLong();
        Date dtIniVal = todayTs();
        try {
            helper.checkOrgVChkServFattByStrut(idEnteConvenz, idStrut, dtIniVal);
        } catch (Exception e) {
            assertNoResultException(e);
        }
        assertTrue(true);
    }

    @Test
    public void existsCdEnteNormaliz_queryIsOk() {
        String cdEnteNormaliz = aString();
        BigDecimal idEnteExcluded = aBigDecimal();
        helper.existsCdEnteNormaliz(cdEnteNormaliz, idEnteExcluded);
        assertTrue(true);
    }

    @Test
    public void getEnteConvenzConservList_queryIsOk() {
        long idUserIamCor = aLong();
        BigDecimal idEnteSiamGestore = aBigDecimal();
        helper.getEnteConvenzConservList(idUserIamCor, idEnteSiamGestore);
        assertTrue(true);
    }

    @Test
    public void checkDateAmbiente_queryIsOk() {
        BigDecimal idAmbiente = aBigDecimal();
        Date dtIniVal = todayTs();
        Date dtFinVal = tomorrowTs();
        helper.checkDateAmbiente(idAmbiente, dtIniVal, dtFinVal);
        assertTrue(true);
    }

    @Test
    public void checkIntervalloSuStorico_queryIsOk() {
        BigDecimal idEnte = aBigDecimal();
        Date dtIniVal = todayTs();
        Date dtFinVal = tomorrowTs();
        helper.checkIntervalloSuStorico(idEnte, dtIniVal, dtFinVal);
        assertTrue(true);
    }

    @Test
    public void getOrgStoricoEnteAmbienteList_queryIsOk() {
        BigDecimal idEnte = aBigDecimal();
        helper.getOrgStoricoEnteAmbienteList(idEnte);
        assertTrue(true);
    }

    @Test
    public void getEntiConvenzionatiValidiAbilitati_queryIsOk() {
        BigDecimal idAmbiente = aBigDecimal();
        long idUser = aLong();
        helper.getEntiConvenzionatiValidiAbilitati(idUser, idAmbiente);
        assertTrue(true);
    }

    @Test
    public void getOrgAmbitoTerritChildListBigDecimal_queryIsOk() {
        helper.getOrgAmbitoTerritChildList(BigDecimal.ZERO);
        assertTrue(true);
    }

    @Test
    public void getOrgAmbitoTerritChildListBigDecimalList_queryIsOk() {
        helper.getOrgAmbitoTerritChildList(Arrays.asList(BigDecimal.ZERO, BigDecimal.ZERO));
        assertTrue(true);
    }

    @Test
    public void getOrgCategEnteByDesc_queryIsOk() {
        helper.getOrgCategEnteByDesc("descrizione");
        assertTrue(true);
    }

    @Test
    public void retrieveOrgEnteAbil_queryIsOk() {
        final long idUtente = 0L;
        final long idAmbiente = 0L;
        final List<BigDecimal> idAmbitoTerritList = Arrays.asList(BigDecimal.ZERO);
        final List<BigDecimal> idCategEnteList = Arrays.asList(BigDecimal.ZERO);
        helper.retrieveOrgEnteAbil(idUtente, idAmbiente, idAmbitoTerritList, idCategEnteList, false);
        helper.retrieveOrgEnteAbil(idUtente, idAmbiente, idAmbitoTerritList, idCategEnteList, true);
        assertTrue(true);
    }

    @Test
    public void retrieveOrgEnteAbilTipoDefTemplateEnte_queryIsOk() {
        final long idUtente = 0L;
        final long idAmbiente = 0L;
        final List<BigDecimal> idAmbitoTerritList = Arrays.asList(BigDecimal.ZERO);
        final List<BigDecimal> idCategEnteList = Arrays.asList(BigDecimal.ZERO);
        helper.retrieveOrgEnteAbil(idUtente, idAmbiente, idAmbitoTerritList, idCategEnteList, false, "tipo");
        helper.retrieveOrgEnteAbil(idUtente, idAmbiente, idAmbitoTerritList, idCategEnteList, true, "tipo");
        assertTrue(true);
    }

    @Test
    public void retrieveOrgEnteAbilListTipoDefTemplateEnte_queryIsOk() {
        final long idUtente = 0L;
        final long idAmbiente = 0L;
        final List<BigDecimal> idAmbitoTerritList = Arrays.asList(BigDecimal.ZERO);
        final List<BigDecimal> idCategEnteList = Arrays.asList(BigDecimal.ZERO);
        helper.retrieveOrgEnteAbil(idUtente, idAmbiente, idAmbitoTerritList, idCategEnteList, false, "tipo", "tipo2");
        helper.retrieveOrgEnteAbil(idUtente, idAmbiente, idAmbitoTerritList, idCategEnteList, true, "tipo", "tipo2");
        assertTrue(true);
    }

    @Test
    public void retrieveOrgAccordoValidoEnteConvenz_queryIsOk() {
        helper.retrieveOrgAccordoValidoEnteConvenz(BigDecimal.ZERO);
        assertTrue(true);
    }

    @Test
    public void getOrgVRicEnteConvenzByEstList_queryIsOk() {
        helper.getOrgVRicEnteConvenzByEstList(BigDecimal.ZERO, BigDecimal.ZERO, "tipoEnteConvenzionato");
        assertTrue(true);
    }

    @Test
    public void findOrgVRicEnteConvenzByEsterno_queryIsOk() {
        helper.findOrgVRicEnteConvenzByEsterno(BigDecimal.valueOf(9005241));
        assertTrue(true);
    }
}
