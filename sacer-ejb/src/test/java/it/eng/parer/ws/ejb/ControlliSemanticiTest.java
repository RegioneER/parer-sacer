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

package it.eng.parer.ws.ejb;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.Costanti;

public class ControlliSemanticiTest extends HelperTest<ControlliSemantici> {

    @Test
    void caricaDefaultDaDBParametriApplicQueryIsOk() {
	String tipoPar = "TEST";
	helper.caricaDefaultDaDBParametriApplic(tipoPar);
	assertTrue(true);
    }

    @Test
    void checkIdStrutQueryIsOk() {
	CSVersatore vers = new CSVersatore();
	for (Costanti.TipiWSPerControlli tipows : Costanti.TipiWSPerControlli.values()) {
	    helper.checkIdStrut(vers, tipows);
	}
	assertTrue(true);
    }

    @Test
    void checkChiaveQueryIsOk() {
	CSChiave key = new CSChiave();
	Long idStruttura = 0L;
	for (ControlliSemantici.TipiGestioneUDAnnullate tguda : ControlliSemantici.TipiGestioneUDAnnullate
		.values()) {
	    helper.checkChiave(key, idStruttura, tguda);
	}
	assertTrue(true);
    }

    @Test
    void caricaPartiAARegistroQueryIsOk() {
	long idAaRegistroUnitaDoc = 0L;
	helper.caricaPartiAARegistro(idAaRegistroUnitaDoc);
	assertTrue(true);
    }

    @Test
    void checkStrutCessataQueryIsOk() {
	long idStrut = 0L;
	helper.checkStrutCessata(idStrut);
	assertTrue(true);
    }

    @Deployment
    public static Archive<?> createTestArchive() {
	return createEnterpriseArchive(ControlliSemanticiTest.class.getSimpleName(),
		createSacerLogJar(), createPaginatorJavaArchive(),
		createSacerJavaArchive(
			Arrays.asList("it.eng.parer.ws.versamento.dto", "it.eng.parer.ws.dto",
				"it.eng.parer.ws.utils", "it.eng.spagoCore.util",
				"com.fasterxml.uuid", "com.fasterxml.uuid.impl"),
			ControlliSemanticiTest.class, it.eng.parer.ws.ejb.ControlliSemantici.class,
			it.eng.parer.web.helper.ConfigurationHelper.class,
			it.eng.parer.ws.utils.Costanti.class));
    }
}
