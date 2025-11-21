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

package it.eng.parer.amministrazioneStrutture.gestioneSottoStrutture.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.todayTs;
import static it.eng.ArquillianUtils.tomorrowTs;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.utils.CostantiDB;

@ArquillianTest
public class SottoStruttureHelperTest {
    @EJB
    private SottoStruttureHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(SottoStruttureHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), SottoStruttureHelperTest.class,
                        SottoStruttureHelper.class, Constants.class));
    }

    @Test
    void getOrgSubStrut_queryIsOk() {
        String nmSubStrut = aString();
        BigDecimal idStrut = aBigDecimal();
        helper.getOrgSubStrut(nmSubStrut, idStrut);
        assertTrue(true);
    }

    @Test
    void countUdInSubStrut_queryIsOk() {
        BigDecimal idSubStrut = aBigDecimal();
        helper.countUdInSubStrut(idSubStrut);
        assertTrue(true);
    }

    @Test
    void getOrgRegolaValSubStrut_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idSubStrut = BigDecimal.ZERO;
        helper.getOrgRegolaValSubStrut(idStrut, idSubStrut);
        assertTrue(true);
    }

    @Test
    void getOrgRegolaSubStrut_3args_queryIsOk() {
        BigDecimal id = aBigDecimal();
        boolean filterValid = false;
        for (Constants.TipoDato tipoDato : Constants.TipoDato.values()) {
            helper.getOrgRegolaSubStrut(id, tipoDato, filterValid);
            assertTrue(true);
        }
    }

    @Test
    void getOrgCampoValSubStrut_queryIsOk() {
        BigDecimal idRegolaValSubStrut = aBigDecimal();
        helper.getOrgCampoValSubStrut(idRegolaValSubStrut);
        assertTrue(true);
    }

    @Test
    void existOrgRegolaSubStrut_5args_queryIsOk() {
        BigDecimal idRegolaValSubStrut = aBigDecimal();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        BigDecimal idTipoDoc = aBigDecimal();
        Date dtIstituz = todayTs();
        Date dtSoppres = tomorrowTs();
        helper.existOrgRegolaSubStrut(idRegolaValSubStrut, idTipoUnitaDoc, idTipoDoc, dtIstituz,
                dtSoppres);
        assertTrue(true);
    }

    @Test
    void existOrgRegolaSubStrut_3args_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String nmTipoUnitaDoc = aString();
        String nmTipoDoc = aString();
        helper.existOrgRegolaSubStrut(idStrut, nmTipoUnitaDoc, nmTipoDoc);
        assertTrue(true);
    }

    @Test
    void getOrgRegolaSubStrut_5args_queryIsOk() {
        Long idStrut = aLong();
        String nmTipoUnitaDoc = aString();
        String nmTipoDoc = aString();
        Date dtIstituz = todayTs();
        Date dtSoppres = tomorrowTs();
        helper.getOrgRegolaSubStrut(idStrut, nmTipoUnitaDoc, nmTipoDoc, dtIstituz, dtSoppres);
        assertTrue(true);
    }

    @Test
    void existOrgCampoSubStrut_queryIsOk() {
        BigDecimal idCampoValSubStrut = aBigDecimal();
        BigDecimal idRegolaValSubStrut = aBigDecimal();
        String nmCampo = aString();
        BigDecimal idRecord = aBigDecimal();
        for (CostantiDB.TipoCampo tipoCampo : CostantiDB.TipoCampo.values()) {
            helper.existOrgCampoSubStrut(idCampoValSubStrut, idRegolaValSubStrut, tipoCampo.name(),
                    nmCampo, idRecord);
            assertTrue(true);
        }
    }

    @Test
    void retrieveOrgSubStrutListAbilitate_queryIsOk() {
        long idUtente = aLong();
        BigDecimal idStruttura = aBigDecimal();
        helper.retrieveOrgSubStrutListAbilitate(idUtente, idStruttura);
        assertTrue(true);
    }
}
