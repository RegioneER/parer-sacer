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

package it.eng.parer.job.tpi.helper;

import static it.eng.ArquillianUtils.todayTs;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.web.helper.HelperTest;

@ArquillianTest
public class ElaboraSessioniRecuperoHelperTest {
    @EJB
    private ElaboraSessioniRecuperoHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return HelperTest.createEnterpriseArchive(
		ElaboraSessioniRecuperoHelperTest.class.getSimpleName(),
		HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
		HelperTest.createSacerJavaArchive(Arrays.asList(""),
			ElaboraSessioniRecuperoHelperTest.class,
			ElaboraSessioniRecuperoHelper.class, JobConstants.class));
    }

    @Test
    void testGetSessioniRecuperoInCorso() throws Exception {
	helper.getSessioniRecuperoInCorso();
	assertTrue(true);
    }

    @Test
    void testGetVrsDtVersByDate() throws Exception {
	// MAC#27666
	// Date dtVers = todayTs();
	LocalDate dtVers = todayTs().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	// end MAC#27666
	helper.getVrsDtVersByDate(dtVers);
	assertTrue(true);
    }

}
