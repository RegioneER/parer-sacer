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

package it.eng.parer.amministrazioneStrutture.gestioneTipoRappresentazione.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aString;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.ws.dto.CSVersatore;

@ArquillianTest
public class TipoRappresentazioneHelperTest {
    @EJB
    private TipoRappresentazioneHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return HelperTest.createEnterpriseArchive(
		TipoRappresentazioneHelperTest.class.getSimpleName(),
		HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
		HelperTest.createSacerJavaArchive(Arrays.asList(""),
			TipoRappresentazioneHelperTest.class, TipoRappresentazioneHelper.class,
			CSVersatore.class));
    }

    @Test
    void getDecTipoRapprCompByName_queryIsOk() {
	String nmTipoRapprComp = aString();
	BigDecimal idStrut = aBigDecimal();
	helper.getDecTipoRapprCompByName(nmTipoRapprComp, idStrut);
	assertTrue(true);
    }

    @Test
    void retrieveDecRapprCompList_queryIsOk() {
	BigDecimal idStrut = aBigDecimal();
	boolean filterValid = false;
	helper.retrieveDecRapprCompList(idStrut, filterValid);
	assertTrue(true);
    }

    @Test
    void checkRelationsAreEmptyForDecTipoRapprComp_queryIsOk() {
	long idTipoRapprComp = aLong();
	helper.checkRelationsAreEmptyForDecTipoRapprComp(idTipoRapprComp);
	assertTrue(true);
    }

    @Test
    void retrieveDecTrasformTipoRapprList_queryIsOk() {
	Long idTipoRapprComp = aLong();
	helper.retrieveDecTrasformTipoRapprList(idTipoRapprComp);
	assertTrue(true);
    }

    @Test
    void retrieveDecImageTrasformList_queryIsOk() {
	BigDecimal idTrasformTipoRappr = aBigDecimal();
	helper.retrieveDecImageTrasformList(idTrasformTipoRappr);
	assertTrue(true);
    }

    @Test
    void getCSVersatoreForImageTrasform_queryIsOk() {
	BigDecimal idImageTrasform = aBigDecimal();
	helper.getCSVersatoreForImageTrasform(idImageTrasform);
	assertTrue(true);
    }

    @Test
    void getCSVersatoreForTrasformTipoRappr_queryIsOk() {
	BigDecimal idTrasformTipoRappr = aBigDecimal();
	helper.getCSVersatoreForTrasformTipoRappr(idTrasformTipoRappr);
	assertTrue(true);
    }

    @Test
    void getDecImageTrasformByName_queryIsOk() {
	BigDecimal idTrasformTipoRappr = aBigDecimal();
	String nmImageTrasform = aString();
	helper.getDecImageTrasformByName(idTrasformTipoRappr, nmImageTrasform);
	assertTrue(true);
    }
}
