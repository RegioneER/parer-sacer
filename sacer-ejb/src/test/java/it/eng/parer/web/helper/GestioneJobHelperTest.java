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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.job.tpi.helper.AggiornaStatoArchiviazioneHelper;
import it.eng.parer.util.ejb.AppServerInstance;

public class GestioneJobHelperTest extends HelperTest<GestioneJobHelper> {
    @Deployment
    public static Archive<?> createTestArchive() {
	return HelperTest.createEnterpriseArchive(GestioneJobHelperTest.class.getSimpleName(),
		HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
		HelperTest.createSacerJavaArchive(
			Arrays.asList("it.eng.parer.ws.versamento.dto", "it.eng.parer.ws.dto",
				"it.eng.parer.ws.dto"),
			GestioneJobHelper.class, GestioneJobHelperTest.class,
			it.eng.parer.job.helper.JobHelper.class,
			it.eng.parer.ws.ejb.ControlliSemantici.class,
			it.eng.parer.ws.ejb.ControlliTpi.class,
			it.eng.parer.ws.dto.RispostaControlli.class,
			it.eng.parer.util.ejb.AppServerInstance.class,
			AggiornaStatoArchiviazioneHelper.class, AppServerInstance.class,
			it.eng.parer.web.helper.ConfigurationHelper.class,
			it.eng.parer.ws.utils.Costanti.class,
			it.eng.parer.job.utils.JobConstants.class,
			it.eng.tpi.bean.StatoArchiviazioneCartellaRisposta.class));
    }

    @Test
    void getDecJobList() {
	final String nmAmbito = "nmAmbito";
	final String dsJob = "dsJob";
	final List<String> tiStato = Arrays.asList("ATTIVO", "DISATTIVO");
	helper.getDecJobList(nmAmbito, dsJob, tiStato);
	assertTrue(true);
    }

    @Test
    void getDecJobListPerAmm() {
	assertFalse(helper.getDecJobListPerAmm().isEmpty());
    }

    @Test
    void getDecJobFotoListPerAmm() {
	assertFalse(helper.getDecJobFotoListPerAmm().isEmpty());
    }

    @Test
    void findLogByJob() {
	final String nmJob = "nmJob";
	helper.findLogByJob(nmJob);
	assertTrue(true);
    }

    @Test
    void getAmbitoJob() {
	assertFalse(helper.getAmbitoJob().isEmpty());
    }

    @Test
    void getInfoJob() {
	assertTrue(helper.getInfoJob().length > 0);
    }

    @Test
    void getNumJobFoto() {
	assertTrue(helper.getNumJobFoto() > 0);
    }

    @Test
    void getNumJobFotoAttivi() {
	assertTrue(helper.getNumJobFotoAttivi() > 0);
    }

    @Test
    void getNumJobRimossiPresenti() {
	assertTrue(helper.getNumJobRimossiPresenti().length > 0);
    }

    @Test
    void getNomiJobRimossiPresenti() {
	assertTrue(helper.getNomiJobRimossiPresenti().length > 0);
    }

    @Test
    void isDecJobFotoAttivo() {
	assertFalse(helper.isDecJobFotoAttivo(41L));
    }

    @Test
    void isDecJobFotoEmpty() {
	assertFalse(helper.isDecJobFotoEmpty());
    }

    @Test
    void getDataLastFotoJob() {
	assertNotNull(helper.getDataLastFotoJob());
    }

    @Test
    void areAllJobsDisattivati() {
	assertTrue(helper.areAllJobsDisattivati());
    }
}
