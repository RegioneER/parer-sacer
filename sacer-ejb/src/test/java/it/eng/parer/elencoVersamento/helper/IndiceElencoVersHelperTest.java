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

package it.eng.parer.elencoVersamento.helper;

import static it.eng.ArquillianUtils.aElvElencoVer;
import static it.eng.ArquillianUtils.aListOfString;
import static it.eng.ArquillianUtils.aVolVolumeConserv;
import static it.eng.ArquillianUtils.anAroUnitaDoc;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.entity.VolVolumeConserv;
import it.eng.parer.web.helper.HelperTest;

@ArquillianTest
public class IndiceElencoVersHelperTest {
    @EJB
    private IndiceElencoVersHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return HelperTest.createEnterpriseArchive(IndiceElencoVersHelperTest.class.getSimpleName(),
		HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
		HelperTest.createSacerJavaArchive(Arrays.asList(""),
			IndiceElencoVersHelperTest.class, IndiceElencoVersHelper.class,
			ElencoEnums.class));
    }

    @Test
    void retrieveUnitaDocsInVolume_queryIsOk() {
	VolVolumeConserv volume = aVolVolumeConserv();

	helper.retrieveUnitaDocsInVolume(volume);
	assertTrue(true);
    }

    @Test
    void getContenutoSinteticoElenco_queryIsOk() {
	VolVolumeConserv volume = aVolVolumeConserv();

	helper.getContenutoSinteticoElenco(volume);
	assertTrue(true);
    }

    @Test
    void getTipologieDocumentoPrincipaleElv_queryIsOk() {
	ElvElencoVer elenco = aElvElencoVer();

	helper.getTipologieDocumentoPrincipaleElv(elenco);
	assertTrue(true);
    }

    @Test
    void getTipologieDocumentoPrincipaleUd_queryIsOk() {
	AroUnitaDoc ud = anAroUnitaDoc();

	helper.getTipologieDocumentoPrincipaleUd(ud);
	assertTrue(true);
    }

    @Test
    void getTipologieUnitaDocumentaria_queryIsOk() {
	ElvElencoVer elenco = aElvElencoVer();

	helper.getTipologieUnitaDocumentaria(elenco);
	assertTrue(true);
    }

    @Test
    void getTipologieRegistro_queryIsOk() {
	ElvElencoVer elenco = aElvElencoVer();

	helper.getTipologieRegistro(elenco);
	assertTrue(true);
    }

    @Test
    void getUtentiVersatori_queryIsOk() {
	ElvElencoVer elenco = aElvElencoVer();

	helper.getUtentiVersatori(elenco);
	assertTrue(true);
    }

    @Test
    void convertListToString_queryIsOk() {
	List<String> listToConvert = aListOfString(2);

	helper.convertListToString(listToConvert);
	assertTrue(true);
    }

    @Test
    void retrieveDateVersamento_queryIsOk() {
	ElvElencoVer elenco = aElvElencoVer();

	helper.retrieveDateVersamento(elenco);
	assertTrue(true);
    }

    @Test
    void retrieveDateVersamentoUdVersate_queryIsOk() {
	ElvElencoVer elenco = aElvElencoVer();

	helper.retrieveDateVersamentoUdVersate(elenco);
	assertTrue(true);
    }

    @Test
    void retrieveDateVersamentoDocAgg_queryIsOk() {
	ElvElencoVer elenco = aElvElencoVer();

	helper.retrieveDateVersamentoDocAgg(elenco);
	assertTrue(true);
    }

    @Test
    void retrieveDateVersamentoUpd_queryIsOk() {
	ElvElencoVer elenco = aElvElencoVer();

	helper.retrieveDateVersamentoUpd(elenco);
	assertTrue(true);
    }
}
