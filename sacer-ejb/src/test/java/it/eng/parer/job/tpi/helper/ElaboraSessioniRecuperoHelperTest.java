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

package it.eng.parer.job.tpi.helper;

import it.eng.parer.job.utils.JobConstants;

import it.eng.parer.web.helper.HelperTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import java.time.LocalDate;
import java.time.ZoneId;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class ElaboraSessioniRecuperoHelperTest {
    @EJB
    private ElaboraSessioniRecuperoHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(ElaboraSessioniRecuperoHelperTest.class.getSimpleName(),
                HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), ElaboraSessioniRecuperoHelperTest.class,
                        ElaboraSessioniRecuperoHelper.class, JobConstants.class));
    }

    @Test
    public void testGetSessioniRecuperoInCorso() throws Exception {
        helper.getSessioniRecuperoInCorso();
        assertTrue(true);
    }

    @Test
    public void testGetVrsDtVersByDate() throws Exception {
        // MAC#27666
        // Date dtVers = todayTs();
        LocalDate dtVers = todayTs().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        // end MAC#27666
        helper.getVrsDtVersByDate(dtVers);
        assertTrue(true);
    }

}
