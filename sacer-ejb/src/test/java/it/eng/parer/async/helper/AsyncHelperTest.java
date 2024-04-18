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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.async.helper;

import it.eng.integriam.server.ws.reputente.Utente;
import it.eng.parer.job.helper.JobHelperTest;
import it.eng.parer.job.tpi.helper.AggiornaStatoArchiviazioneHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.util.ejb.AppServerInstance;
import it.eng.parer.web.helper.HelperTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author manuel.bertuzzi@eng.it
 */
public class AsyncHelperTest extends HelperTest<AsyncHelper> {
    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(AsyncHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(
                        Arrays.asList("it.eng.parer.ws.dto", "it.eng.tpi.bean", "it.eng.parer.ws.versamento.dto"),
                        AsyncHelper.class, AsyncHelperTest.class,

                        it.eng.parer.job.helper.JobHelper.class, AggiornaStatoArchiviazioneHelper.class,
                        AppServerInstance.class, it.eng.parer.ws.ejb.ControlliSemantici.class,
                        it.eng.parer.ws.ejb.ControlliTpi.class, it.eng.parer.web.helper.ConfigurationHelper.class,
                        it.eng.parer.ws.utils.Costanti.class, it.eng.parer.job.utils.JobConstants.class));
    }

    @Test
    public void countLock_queryIsOk() {
        String asyncTask = aString();
        Long idStrut = aLong();
        helper.countLock(asyncTask, idStrut);
        Assert.assertTrue(true);
    }

    @Test
    public void getLock_queryIsOk() {
        String asyncTask = aString();
        Long idStrut = aLong();
        try {
            helper.getLock(asyncTask, idStrut);
            Assert.assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void lockRecord_queryIsOk() {
        Long idLock = aLong();
        try {
            helper.lockRecord(idLock);
            Assert.assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }
}
