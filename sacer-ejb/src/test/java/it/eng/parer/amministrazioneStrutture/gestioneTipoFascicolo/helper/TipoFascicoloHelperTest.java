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

package it.eng.parer.amministrazioneStrutture.gestioneTipoFascicolo.helper;

import it.eng.parer.fascicoli.helper.FascicoliHelperTest;
import it.eng.parer.web.helper.HelperTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import javax.ejb.EJB;

import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class TipoFascicoloHelperTest {
    @EJB
    private TipoFascicoloHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(TipoFascicoloHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), TipoFascicoloHelperTest.class,
                        TipoFascicoloHelper.class));
    }

    @Test
    public void getDecTipoFascicoloList_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        boolean filterValid = false;
        helper.getDecTipoFascicoloList(idStrut, filterValid);
        assertTrue(true);
    }

    @Test
    public void getTipiFascicoloAbilitati_queryIsOk() {
        long idUtente = aLong();
        BigDecimal idStrut = aBigDecimal();
        boolean filterValid = false;
        helper.getTipiFascicoloAbilitati(idUtente, idStrut, filterValid);
        assertTrue(true);
    }

    @Test
    public void isTipoFascicoloAbilitato_queryIsOk() {
        long idUtente = aLong();
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idTipoFascicolo = aBigDecimal();
        boolean filterValid = false;
        helper.isTipoFascicoloAbilitato(idUtente, idStrut, idTipoFascicolo, filterValid);
        assertTrue(true);
    }

    @Test
    public void getLastDecAaTipoFascicolo_queryIsOk() {
        BigDecimal idTipoFascicolo = aBigDecimal();
        helper.getLastDecAaTipoFascicolo(idTipoFascicolo);
        assertTrue(true);
    }

    @Test
    public void getDecAaTipoFascicoloList_queryIsOk() {
        BigDecimal idTipoFascicolo = aBigDecimal();
        helper.getDecAaTipoFascicoloList(idTipoFascicolo);
        assertTrue(true);
    }

    @Test
    public void getDecCriterioRaggrFascList_queryIsOk() {
        BigDecimal idTipoFascicolo = aBigDecimal();
        helper.getDecCriterioRaggrFascList(idTipoFascicolo);
        assertTrue(true);
    }

    @Test
    public void existsDecTipoFascicoloCaseInsensitive_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String nmTipoFascicolo = aString();
        helper.existsDecTipoFascicoloCaseInsensitive(idStrut, nmTipoFascicolo);
        assertTrue(true);
    }

    @Test
    public void existsDecCriterioRaggrFascStandard_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idTipoFascicolo = aBigDecimal();
        helper.existsDecCriterioRaggrFascStandard(idStrut, idTipoFascicolo);
        assertTrue(true);
    }

    @Test
    public void retrieveDecModelloXsdFascicolo_queryIsOk() {
        BigDecimal idAmbiente = aBigDecimal();
        Date data = todayTs();
        String flDefault = aString();
        String tiUsoModelloXsd = aString();
        String tiModelloXsd = aString();
        helper.retrieveDecModelloXsdFascicolo(idAmbiente, data, flDefault, tiUsoModelloXsd);
        assertTrue(true);
    }

    @Test
    public void getDecParteNumeroFascicoloList_queryIsOk() {
        BigDecimal idAaTipoFascicolo = aBigDecimal();
        helper.getDecParteNumeroFascicoloList(idAaTipoFascicolo);
        assertTrue(true);
    }

    @Test
    public void getDecVChkFmtNumeroFascForPeriodo_queryIsOk() {
        BigDecimal idAaTipoFascicolo = aBigDecimal();
        try {
            helper.getDecVChkFmtNumeroFascForPeriodo(idAaTipoFascicolo);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void getDecErrAaTipoFascicolo_queryIsOk() {
        BigDecimal idAaTipoFascicolo = aBigDecimal();
        Integer aaFascicolo = aInt();
        helper.getDecErrAaTipoFascicolo(idAaTipoFascicolo, aaFascicolo);
        assertTrue(true);
    }

    @Test
    public void getDecUsoModelloXsdFascList_queryIsOk() {
        BigDecimal idAaTipoFascicolo = aBigDecimal();
        BigDecimal idModelloXsdFascicolo = aBigDecimal();
        String flStandard = aString();
        helper.getDecUsoModelloXsdFascList(idAaTipoFascicolo, idModelloXsdFascicolo, flStandard);
        assertTrue(true);
    }

    @Test
    public void existPeriodiValiditaSovrappostiFascicoli_queryIsOk() {
        BigDecimal idAaTipoFascicoloExcluded = aBigDecimal();
        BigDecimal idTipoFascicolo = aBigDecimal();
        BigDecimal aaIniTipoFascicolo = aBigDecimal();
        BigDecimal aaFinTipoFascicolo = aBigDecimal();
        helper.existPeriodiValiditaSovrappostiFascicoli(idAaTipoFascicoloExcluded, idTipoFascicolo, aaIniTipoFascicolo,
                aaFinTipoFascicolo);
        assertTrue(true);
    }

    @Test
    public void getNmAttribFascList_queryIsOk() {
        final BigDecimal idAaTipoFascicolo = BigDecimal.ZERO;
        final BigDecimal idTipoFascicolo = BigDecimal.ZERO;
        helper.getNmAttribFascList(idAaTipoFascicolo, idTipoFascicolo);
        assertTrue(true);
    }

    @Test
    public void retrieveDecAttribFascicoloList_queryIsOk() {
        final BigDecimal idXsdFascicolo = BigDecimal.ZERO;
        final BigDecimal idAaTipoFascicolo = BigDecimal.ZERO;
        helper.retrieveDecAttribFascicoloList(idXsdFascicolo, idAaTipoFascicolo);
        assertTrue(true);
    }

    @Test
    public void getDecXsdAttribFascicoloByAttrib_queryIsOk() {
        final BigDecimal idAttribFascicolo = BigDecimal.ZERO;
        final BigDecimal idXsdFascicolo = BigDecimal.ZERO;
        helper.getDecXsdAttribFascicoloByAttrib(idAttribFascicolo, idXsdFascicolo);
        assertTrue(true);
    }

    @Test
    public void getDecModelloXsdAttribFascicoloByAttrib_queryIsOk() {
        final BigDecimal idAaTipoFascicolo = BigDecimal.ZERO;
        final BigDecimal idModelloXsdFascicolo = BigDecimal.ZERO;
        helper.getDecUsoModelloXsdFascicoloByAttrib(idAaTipoFascicolo, idModelloXsdFascicolo);
        assertTrue(true);
    }

    @Test
    public void getDecUsoModelloXsdFascicoloByAttrib_queryIsOk() {
        final BigDecimal idAaTipoFascicolo = BigDecimal.ZERO;
        final BigDecimal idModelloXsdFascicolo = BigDecimal.ZERO;
        helper.getDecUsoModelloXsdFascicoloByAttrib(idAaTipoFascicolo, idModelloXsdFascicolo);
        assertTrue(true);
    }
}
