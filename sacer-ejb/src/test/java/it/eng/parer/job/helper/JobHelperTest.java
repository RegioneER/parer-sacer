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

package it.eng.parer.job.helper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.integriam.server.ws.reputente.Utente;
import it.eng.parer.job.tpi.helper.AggiornaStatoArchiviazioneHelper;
import it.eng.parer.util.ejb.AppServerInstance;
import it.eng.parer.web.helper.HelperTest;

public class JobHelperTest extends HelperTest<JobHelper> {
    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(JobHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), createSacerLogJar(),
                HelperTest.createSacerJavaArchive(
                        Arrays.asList("it.eng.parer.ws.dto", "it.eng.tpi.bean",
                                "it.eng.parer.ws.versamento.dto"),
                        it.eng.parer.job.helper.JobHelper.class,
                        AggiornaStatoArchiviazioneHelper.class, AppServerInstance.class,
                        it.eng.parer.ws.ejb.ControlliSemantici.class,
                        it.eng.parer.ws.ejb.ControlliTpi.class,
                        it.eng.parer.web.helper.ConfigurationHelper.class,
                        it.eng.parer.ws.utils.Costanti.class, Utente.class, JobHelperTest.class));
    }

    @Test
    public void findUltimaAttivazioneByJob_queryIsOk() {
        String nmJob = aString();
        helper.findUltimaAttivazioneByJob(nmJob);
        assertTrue(true);
    }
}
