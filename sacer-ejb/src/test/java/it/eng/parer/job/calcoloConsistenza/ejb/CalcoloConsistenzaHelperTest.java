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

package it.eng.parer.job.calcoloConsistenza.ejb;

import java.util.Arrays;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;

import it.eng.parer.web.helper.HelperTest;

public class CalcoloConsistenzaHelperTest extends HelperTest<CalcoloConsistenzaHelper> {

    @Deployment
    public static Archive<?> createTestArchive_queryIsOk() {
        return HelperTest.createEnterpriseArchive(CalcoloConsistenzaHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), CalcoloConsistenzaHelper.class,
                        CalcoloConsistenzaHelperTest.class));
    }

}
