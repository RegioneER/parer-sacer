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

package it.eng.parer.job.verificaCompRegistro.helper;

import static it.eng.ArquillianUtils.aListOfLong;
import static it.eng.ArquillianUtils.aLong;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.web.helper.HelperTest;

@ArquillianTest
public class VerificaCompRegHelperTest {
    @EJB
    private VerificaCompRegHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return HelperTest.createEnterpriseArchive(VerificaCompRegHelperTest.class.getSimpleName(),
		HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
		HelperTest.createSacerJavaArchive(Arrays.asList(""),
			VerificaCompRegHelperTest.class, VerificaCompRegHelper.class));
    }

    @Test
    void getAaRegistroUnitaDocDaElab_queryIsOk() {
	helper.getAaRegistroUnitaDocDaElab();
	assertTrue(true);
    }

    @Test
    void getListaUdDaVerificare_queryIsOk() {
	Long idRegistroUnitaDoc = aLong();
	List<Long> idSubStruts = aListOfLong(2);
	Long anno = aLong();
	helper.getListaUdDaVerificare(idRegistroUnitaDoc, idSubStruts, anno);
	assertTrue(true);
    }

    @Test
    void sbloccaAaRegistroUnitaDoc_queryIsOk() {
	long idAaRegistroUnitaDoc = aLong();
	helper.sbloccaAaRegistroUnitaDoc(idAaRegistroUnitaDoc);
	assertTrue(true);
    }

    @Test
    void pulisciErroriRegAnnoNewTrans_queryIsOk() {
	long idAaRegistroUnitaDoc = aLong();
	long anno = aLong();
	helper.pulisciErroriRegAnnoNewTrans(idAaRegistroUnitaDoc, anno);
	assertTrue(true);
    }
}
