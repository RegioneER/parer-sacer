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

/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.web.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aString;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.slite.gen.tablebean.AroUpdUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.ElvVLisUpdUdTableBean;

@ArquillianTest
public class AggiornamentiHelperTest {
    @EJB
    private AggiornamentiHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return HelperTest.createEnterpriseArchive(AggiornamentiHelperTest.class.getSimpleName(),
		HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
		HelperTest.createSacerJavaArchive(Arrays.asList(""), AggiornamentiHelperTest.class,
			AggiornamentiHelper.class));
    }

    @Test
    void getAroUpdUnitaDocRowBean_isValidJpql() throws Exception {
	BigDecimal idUpdUnitaDoc = aBigDecimal();
	BigDecimal idStrut = aBigDecimal();
	AroUpdUnitaDocRowBean aroUpdUnitaDocRowBean = helper.getAroUpdUnitaDocRowBean(idUpdUnitaDoc,
		idStrut);
	assertNotNull(aroUpdUnitaDocRowBean);
    }

    @Test
    void testGetElvVLisUpdUdViewBean_isValidJpql() throws Exception {
	System.out.println("getElvVLisUpdUdViewBean");
	BigDecimal idElencoVers = aBigDecimal();
	String registro = aString();
	BigDecimal anno = aBigDecimal();
	String codice = aString();
	BigDecimal annpRangeDa = aBigDecimal();
	BigDecimal annoRangeA = aBigDecimal();
	String codiceRangeDa = aString();
	String codiceRangeA = aString();
	ElvVLisUpdUdTableBean elvVLisUpdUdViewBean = helper.getElvVLisUpdUdViewBean(idElencoVers,
		registro, anno, codice, annpRangeDa, annoRangeA, codiceRangeDa, codiceRangeA);
	assertNotNull(elvVLisUpdUdViewBean);
    }

}
