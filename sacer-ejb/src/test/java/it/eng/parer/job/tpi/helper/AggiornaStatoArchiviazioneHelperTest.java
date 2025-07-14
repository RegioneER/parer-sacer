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
package it.eng.parer.job.tpi.helper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Date;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;

import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.util.ejb.AppServerInstance;
import it.eng.parer.util.ejb.JmsProducerUtilEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.ws.ejb.ControlliSemantici;
import it.eng.parer.ws.ejb.ControlliTpi;
import it.eng.parer.ws.utils.Costanti;

/**
 * @author manuel.bertuzzi@eng.it
 */
public class AggiornaStatoArchiviazioneHelperTest
	extends HelperTest<AggiornaStatoArchiviazioneHelper> {
    @Deployment
    public static Archive<?> createTestArchive() {
	final JavaArchive sacerJavaArchive = HelperTest.createSacerJavaArchive(
		Arrays.asList("it.eng.parer.ws.dto", "it.eng.tpi.bean",
			"it.eng.parer.ws.versamento.dto", "com.fasterxml.jackson.core"),
		JobHelper.class, AggiornaStatoArchiviazioneHelper.class, AppServerInstance.class,
		ControlliSemantici.class, ControlliTpi.class, ConfigurationHelper.class,
		Costanti.class, AggiornaStatoArchiviazioneHelperTest.class,
		JmsProducerUtilEjb.class);
	sacerJavaArchive.addAsResource(
		HelperTest.class.getClassLoader().getResource("jboss-ejb3.xml"),
		"META-INF/jboss-ejb3.xml");
	return HelperTest.createEnterpriseArchive(
		AggiornaStatoArchiviazioneHelperTest.class.getSimpleName(),
		HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
		sacerJavaArchive);
    }

    void findArkDatesByStatus_queryIsOk() {
	String[] tiStatoDtVers = aStringArray(2);
	helper.findArkDatesByStatus(tiStatoDtVers);
	assertTrue(true);
    }

    @Test
    void getIdStrutList_queryIsOk() {
	helper.getIdStrutList();
	assertTrue(true);
    }

    @Test
    void getPathDtVers_queryIsOk() {
	Long idDtVers = aLong();
	String path = aString();
	helper.getPathDtVers(idDtVers, path);
	assertTrue(true);
    }

    @Test
    void getPathString_queryIsOk() {
	Long idStrut = aLong();
	try {
	    helper.getPathString(idStrut);
	    assertTrue(true);
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void checkNiFilePath_queryIsOk() {
	Long idDtVers = aLong();
	helper.checkNiFilePath(idDtVers);
	assertTrue(true);
    }

    @Test
    void getComponentCount_queryIsOk() {
	Long idStrut = aLong();
	Date from = todayTs();
	Date to = tomorrowTs();
	helper.getComponentCount(idStrut, from, to);
	assertTrue(true);
    }

    @Test
    // @Ignore("è private, va cambiato in public per lanciare il test")
    void updateDocDaElabElenco_queryIsOk() {
	helper.updateDocDaElabElenco(todayTs(), tomorrowTs(), aListOfLong(2));
	assertTrue(true);
    }

    @Test
    // @Ignore("è private, va cambiato in public per lanciare il test")
    void updateUdDaElabElenco_queryIsOk() {
	helper.updateUdDaElabElenco(todayTs(), tomorrowTs(), aListOfLong(2));
	assertTrue(true);
    }

    @Test
    void updateFlCancVrsPaths_queryIsOk() {
	helper.updateFlCancVrsPaths(0L);
	assertTrue(true);
    }

    @Test
    // @Ignore("è private, va cambiato in public per lanciare il test")
    void deleteArkPath_queryIsOk() {
	helper.deleteArkPath("VrsArkPathDtVers", 0L);
	assertTrue(true);
    }

    void handleResponse_queryIsOk() {
    }
}
