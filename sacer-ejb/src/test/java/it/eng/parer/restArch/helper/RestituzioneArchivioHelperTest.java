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

package it.eng.parer.restArch.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aListOfLong;
import static it.eng.ArquillianUtils.aListOfString;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aOrgStrut;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.assertNoResultException;
import static it.eng.ArquillianUtils.createSacerJavaArchive;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.async.utils.UdSerFascObj;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.AroAipRestituzioneArchivio;
import it.eng.parer.entity.constraint.AroRichiestaRa;
import it.eng.parer.restArch.dto.RicercaRichRestArchBean;
import it.eng.parer.web.helper.HelperTest;

@ArquillianTest
public class RestituzioneArchivioHelperTest {
    @EJB
    private RestituzioneArchivioHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(
                RestituzioneArchivioHelperTest.class.getSimpleName(),
                HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
                createSacerJavaArchive(Arrays.asList(""), RestituzioneArchivioHelperTest.class,
                        RestituzioneArchivioHelper.class, RicercaRichRestArchBean.class,
                        UdSerFascObj.class));
    }

    @Test
    void isRichRestArchExisting_queryIsOk() {
        BigDecimal idEnteConvenz = aBigDecimal();
        helper.isRichRestArchExisting(idEnteConvenz);
        assertTrue(true);
    }

    @Test
    void retrieveRichRestArchExpiredToProcess_queryIsOk() {
        BigDecimal idEnteConvenz = aBigDecimal();
        helper.retrieveRichRestArchExpiredToProcess(idEnteConvenz);
        assertTrue(true);
    }

    @Test
    void retrieveUdSerFascToProcess_queryIsOk() {
        OrgStrut struttura = aOrgStrut();
        helper.retrieveUdSerFascToProcess(struttura);
        assertTrue(true);
    }

    @Test
    void retrieveAroVRicRichRa_queryIsOk() {
        RicercaRichRestArchBean filtri = aRicercaRichRestArchBean();
        List<Long> idEnteConvenzList = aListOfLong(2);
        helper.retrieveAroVRicRichRa(filtri, idEnteConvenzList);
        assertTrue(true);
        idEnteConvenzList = aListOfLong(1);
        helper.retrieveAroVRicRichRa(filtri, idEnteConvenzList);
        assertTrue(true);

    }

    private RicercaRichRestArchBean aRicercaRichRestArchBean() {
        RicercaRichRestArchBean filtri = new RicercaRichRestArchBean();
        filtri.setId_ambiente(aBigDecimal());
        filtri.setId_ente(aBigDecimal());
        filtri.setId_strut(aBigDecimal());
        filtri.setTi_rich_rest_arch(aString());
        filtri.setTi_stato_rich_rest_arch_cor(aListOfString(2));
        return filtri;
    }

    @Test
    void retrieveOrgEnteSiamList_queryIsOk() {
        RicercaRichRestArchBean filtri = aRicercaRichRestArchBean();
        helper.retrieveOrgEnteSiamList(filtri);
        assertTrue(true);
    }

    @Test
    void getAroVLisItemRa_queryIsOk() {
        BigDecimal idRichRestArch = aBigDecimal();
        BigDecimal idStrut = aBigDecimal();
        helper.getAroVLisItemRa(idRichRestArch, idStrut);
        assertTrue(true);
    }

    @Test
    void countAroItemRichRestArch_queryIsOk() {
        BigDecimal idRichRestArch = aBigDecimal();
        AroAipRestituzioneArchivio.TiStatoAroAipRa[] tiStato = AroAipRestituzioneArchivio.TiStatoAroAipRa
                .values();
        helper.countAroItemRichRestArch(idRichRestArch, tiStato);
        assertTrue(true);
    }

    @Test
    void retrieveAroErrItemRestArch_queryIsOk() {
        long idRichRestArch = aLong();
        helper.retrieveAroErrItemRestArch(idRichRestArch);
        assertTrue(true);
    }

    @Test
    void getIdStrutFirstStateRich_queryIsOk() {
        BigDecimal idRichRestArch = BigDecimal.valueOf(1002473);
        try {
            helper.getIdStrutFirstStateRich(idRichRestArch);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void isRichRestArchByStatoExisting_queryIsOk() {
        helper.isRichRestArchByStatoExisting(BigDecimal.ZERO,
                Arrays.asList(AroRichiestaRa.AroRichiestaTiStato.ESTRATTO,
                        AroRichiestaRa.AroRichiestaTiStato.ANNULLATO));
        assertTrue(true);
    }

}
