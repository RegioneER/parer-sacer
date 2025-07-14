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

package it.eng.parer.async.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.assertNoResultException;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.async.utils.UdSerFascObj;
import it.eng.parer.entity.AroRichiestaRa;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.web.helper.HelperTest;

@ArquillianTest
public class CalcoloEstrazioneHelperTest {
    @EJB
    private CalcoloEstrazioneHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return HelperTest.createEnterpriseArchive(CalcoloEstrazioneHelperTest.class.getSimpleName(),
		HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
		HelperTest.createSacerJavaArchive(Arrays.asList(""),
			CalcoloEstrazioneHelperTest.class, CalcoloEstrazioneHelper.class,
			UdSerFascObj.class));
    }

    @Test
    void retrieveAroUnitaDocListQueryIsOk() {
	BigDecimal idEnte = aBigDecimal();
	helper.retrieveAroUnitaDocList(idEnte);
	assertTrue(true);
    }

    @Test
    void retrieveEntiQueryIsOk() {
	helper.retrieveEnti();
	assertTrue(true);
    }

    @Test
    void retrieveStrutture_0argsQueryIsOk() {
	helper.retrieveStrutture();
	assertTrue(true);
    }

    @Test
    void retrieveStrutture_BigDecimalQueryIsOk() {
	BigDecimal idEnte = aBigDecimal();
	helper.retrieveStrutture(idEnte);
	assertTrue(true);
    }

    @Test
    void retrieveIamUserByNameQueryIsOk() {
	String nmUserid = aString();
	helper.retrieveIamUserByName(nmUserid);
	assertTrue(true);
    }

    @Test
    void retrieveRichiesteQueryIsOk() {
	helper.retrieveRichieste();
	assertTrue(true);
    }

    @Test
    void retrieveOrgEnteByIdQueryIsOk() {
	BigDecimal idEnte = aBigDecimal();
	helper.retrieveOrgEnteById(idEnte);
	assertTrue(true);
    }

    @Test
    void retrieveOrgEnteConvenzByIdQueryIsOk() {
	BigDecimal idEnteConvenz = aBigDecimal();
	helper.retrieveOrgEnteConvenzById(idEnteConvenz);
	assertTrue(true);
    }

    @Test
    void retrieveOrgStrutByIdQueryIsOk() {
	BigDecimal idStrut = aBigDecimal();
	helper.retrieveOrgStrutById(idStrut);
	assertTrue(true);
    }

    @Test
    void retrieveUnitaDocByIdQueryIsOk() {
	long idUnitaDoc = aLong();
	helper.retrieveUnitaDocById(idUnitaDoc);
	assertTrue(true);
    }

    @Test
    void retrieveIndiceAipUdByIdQueryIsOk() {
	long idIndiceAipUd = aLong();
	helper.retrieveIndiceAipUdById(idIndiceAipUd);
	assertTrue(true);
    }

    @Test
    void retrieveRichiestaByIdQueryIsOk() {
	Long idRichiestaRa = aLong();
	helper.retrieveRichiestaById(idRichiestaRa);
	assertTrue(true);
    }

    @Test
    void getTipoSaveFileQueryIsOk() {
	BigDecimal idTipoUnitaDoc = BigDecimal.valueOf(5);
	helper.getTipoSaveFile(idTipoUnitaDoc);
	assertTrue(true);
    }

    @Test
    void retrieveAroAipRestituzioneArchivioByIdQueryIsOk() {
	Long idAipRestArchivio = aLong();
	helper.retrieveAroAipRestituzioneArchivioById(idAipRestArchivio);
	assertTrue(true);
    }

    @Test
    void retrieveLogJobByIdQueryIsOk() {
	long idLogJob = aLong();
	helper.retrieveLogJobById(idLogJob);
	assertTrue(true);
    }

    @Test
    void retrieveRichiesteScaduteDaProcessareQueryIsOk() {
	long idStrut = aLong();
	helper.retrieveRichiesteScaduteDaProcessare(idStrut);
	assertTrue(true);
    }

    @Test
    void retrieveRichiesteEstrazioniInCorsoQueryIsOk() {
	long idStrut = aLong();
	helper.retrieveRichiesteEstrazioniInCorso(idStrut);
	assertTrue(true);
    }

    @Test
    void checkRichiestaInCodaQueryIsOk() {
	BigDecimal idEnteConvenz = aBigDecimal();
	helper.checkRichiestaInCoda(idEnteConvenz);
	assertTrue(true);
    }

    @Test
    void retrieveUdSerFascToProcessQueryIsOk() {
	OrgStrut struttura = new OrgStrut();
	struttura.setIdStrut(aLong());
	helper.retrieveUdSerFascToProcess(struttura);
	assertTrue(true);
    }

    @Test
    void retrieveAipUdSerFascByRichiestaQueryIsOk() {
	AroRichiestaRa richiesta = new AroRichiestaRa();
	richiesta.setIdRichiestaRa(aLong());
	int maxUd2procRa = 10;
	helper.retrieveAipUdSerFascByRichiesta(richiesta, maxUd2procRa);
	assertTrue(true);
    }

    @Test
    void retrieveIndiceAIPByIdUdQueryIsOk() throws ParerInternalError {
	Long idUnitaDoc = aLong();
	helper.retrieveIndiceAIPByIdUd(idUnitaDoc);
	assertTrue(true);
    }

    @Test
    void retrieveRichiesteRaDaElabQueryIsOk() {
	helper.retrieveRichiesteRaDaElab();
	assertTrue(true);
    }

    @Test
    void checkEstrazioneInCorsoQueryIsOk() {
	BigDecimal idRichiestaRa = aBigDecimal();
	helper.checkEstrazioneInCorso(idRichiestaRa);
	assertTrue(true);
    }

    @Test
    void retrieveLastVerIndiceAIPByIdUdQueryIsOk() throws ParerInternalError {
	Long idUnitaDoc = 0L;
	try {
	    helper.retrieveLastVerIndiceAIPByIdUd(idUnitaDoc);
	    fail("Non deve trovare nulla con id " + idUnitaDoc);
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }
}
