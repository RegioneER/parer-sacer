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
import static it.eng.ArquillianUtils.aString;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.web.util.Transform;

@ArquillianTest
public class MonitoraggioTpiHelperTest {
    @EJB
    private MonitoraggioTpiHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(MonitoraggioTpiHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""),
                        MonitoraggioTpiHelperTest.class, MonitoraggioTpiHelper.class,
                        Transform.class));
    }

    @Test
    void getDateVersamentoArk_queryIsOk() {

        Date dateVersDa = new Date();
        Date dateVersA = new Date();
        String flMigraz = aString();
        String statoDtVers = aString();
        helper.getDateVersamentoArk(dateVersDa, dateVersA, flMigraz, statoDtVers);
        assertTrue(true);
    }

    @Test
    void getPathsDateVersamentoArk_queryIsOk() {

        BigDecimal idVrsDtVers = aBigDecimal();
        helper.getPathsDateVersamentoArk(idVrsDtVers);
        assertTrue(true);
    }

    @Test
    void getFilesNoArkPath_queryIsOk() {

        BigDecimal idPathDtVers = aBigDecimal();
        String tiArkFileNoArk = aString();
        helper.getFilesNoArkPath(idPathDtVers, tiArkFileNoArk);
        assertTrue(true);
    }

    @Test
    void getDateSchedulazioniTpi_queryIsOk() {

        Date dateSchedDa = new Date();
        Date dateSchedA = new Date();
        String flAnomalie = aString();
        String statoDtSched = aString();
        helper.getDateSchedulazioniTpi(dateSchedDa, dateSchedA, flAnomalie, statoDtSched);
        assertTrue(true);
    }

    @Test
    void getDataSchedulazioneTpi_queryIsOk() {

        BigDecimal idTpiDtSched = aBigDecimal();
        helper.getDataSchedulazioneTpi(idTpiDtSched);
        assertTrue(true);
    }

    @Test
    void getJobList_queryIsOk() {

        BigDecimal idTpiDtSched = aBigDecimal();
        String flMigraz = aString();
        String tiTpiSchedJob = aString();
        helper.getJobList(idTpiDtSched, flMigraz, tiTpiSchedJob);
        assertTrue(true);
    }

    @Test
    void getErrArkJobList_queryIsOk() {

        BigDecimal idTpiSchedJob = aBigDecimal();
        helper.getErrArkJobList(idTpiSchedJob);
        assertTrue(true);
    }

    @Test
    void getPathElabJobList_queryIsOk() {

        BigDecimal idTpiSchedJob = aBigDecimal();
        helper.getPathElabJobList(idTpiSchedJob);
        assertTrue(true);
    }

}
