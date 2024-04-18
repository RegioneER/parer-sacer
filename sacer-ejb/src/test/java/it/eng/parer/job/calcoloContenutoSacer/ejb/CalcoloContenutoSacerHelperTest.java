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

package it.eng.parer.job.calcoloContenutoSacer.ejb;

import it.eng.ArquillianUtils;
import static it.eng.ArquillianUtils.*;

import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.ws.utils.CostantiDB;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;

public class CalcoloContenutoSacerHelperTest extends HelperTest<CalcoloContenutoSacerHelper> {

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(CalcoloContenutoSacerHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), CalcoloContenutoSacerHelper.class,
                        it.eng.parer.sacerlog.ejb.helper.SacerLogHelper.class,
                        it.eng.parer.sacerlog.util.TransactionLogContext.class,
                        it.eng.parer.sacerlog.util.LogParam.class,
                        it.eng.parer.sacerlog.ejb.util.ObjectsToLogBefore.class,
                        CalcoloContenutoSacerHelperTest.class));
    }

    @Test
    public void getDataInizioCalcolo_queryIsOk() {
        helper.getDataInizioCalcolo();
        assertTrue(true);
    }

    @Test
    public void getIdTipiUnitaDocByStrutAndTipoServizio_queryIsOk() {
        BigDecimal idTipoServizio = BigDecimal.ZERO;
        List<BigDecimal> idStrutList = aListOfBigDecimal(2);
        CostantiDB.TiClasseTipoServizio tiClasseTipoServizio = CostantiDB.TiClasseTipoServizio.ALTRO;
        helper.getIdTipiUnitaDocByStrutAndTipoServizio(idTipoServizio, idStrutList, tiClasseTipoServizio);
        assertTrue(true);
        tiClasseTipoServizio = CostantiDB.TiClasseTipoServizio.ATTIVAZIONE_SISTEMA_VERSANTE;
        helper.getIdTipiUnitaDocByStrutAndTipoServizio(idTipoServizio, idStrutList, tiClasseTipoServizio);
        assertTrue(true);
        tiClasseTipoServizio = CostantiDB.TiClasseTipoServizio.CONSERVAZIONE;
        helper.getIdTipiUnitaDocByStrutAndTipoServizio(idTipoServizio, idStrutList, tiClasseTipoServizio);
        assertTrue(true);
    }

    @Test
    public void getMinimumDtRifConta_queryIsOk() {
        List<Long> idTipoUnitaDocList = aListOfLong(2);
        helper.getMinimumDtRifConta(idTipoUnitaDocList);
        assertTrue(true);
    }

    @Test
    public void getMinimumDtRifContaBySistVers_queryIsOk() {
        List<Long> idTipoUnitaDocList = aListOfLong(2);
        BigDecimal idSistemaVersante = aBigDecimal();
        helper.getMinimumDtRifContaBySistVers(idTipoUnitaDocList, idSistemaVersante);
        assertTrue(true);
    }

    @Test
    public void getAplSistemiVersantiSeparatiPerTipoUd_queryIsOk() {
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        helper.getAplSistemiVersantiSeparatiPerTipoUd(idTipoUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getOrgVServTiServDaErog_queryIsOk() {
        helper.getOrgVServTiServDaErog();
        assertTrue(true);
    }

    @Test
    public void getOrgServizioErogs_queryIsOk() {
        helper.getOrgServizioErogs();
        assertTrue(true);
    }

    @Test
    public void getAnnullQuery_queryIsOk() {
        // helper.getAnnullQuery("0)", "", "");
        // assertTrue(true);
    }

    @Test
    public void insertTotaliPerGiorno_queryIsOk() {
        helper.insertTotaliPerGiorno(tomorrowTs());
        assertTrue(true);
    }

    @Test
    public void setDtErog_queryIsOk() {
        helper.setDtErog(aLogParam());
        assertTrue(true);
    }

    @Test
    public void insertMonTipoUnitaDocUserVers_queryIsOk() {
        try {
            helper.insertMonTipoUnitaDocUserVers(0L, 0L, tomorrowTs(), 0L);
            fail("mi aspetto che fallisca perché l'idTipoUnitaDoc non trova record");
        } catch (Exception e) {
            assertExceptionMessage(e, "ConstraintViolationException");
        }
    }
}
