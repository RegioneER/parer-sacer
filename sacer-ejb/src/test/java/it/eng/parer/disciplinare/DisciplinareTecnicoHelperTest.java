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

package it.eng.parer.disciplinare;

import static it.eng.ArquillianUtils.aString;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.web.helper.HelperTest;

@ArquillianTest
public class DisciplinareTecnicoHelperTest {
    @EJB
    private DisciplinareTecnicoHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest
                .createEnterpriseArchive(DisciplinareTecnicoHelperTest.class.getSimpleName(),
                        HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
                        HelperTest.createSacerJavaArchive(Arrays.asList(""),
                                DisciplinareTecnicoHelperTest.class,
                                DisciplinareTecnicoHelper.class));
    }

    @Test
    void getAplParamApplicReportByAppReport() {
        String nmApplic = aString();
        String nmReport = aString();
        helper.getAplParamApplicReportByAppReport(nmApplic, nmReport);
        assertTrue(true);
    }
}
