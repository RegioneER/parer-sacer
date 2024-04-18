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

package it.eng.parer.job.verificaCompRegistro.helper;

import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.helper.TipoUnitaDocHelperTest;
import it.eng.parer.web.helper.HelperTest;

import java.util.Arrays;
import java.util.List;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class VerificaCompRegHelperTest {
    @EJB
    private VerificaCompRegHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(VerificaCompRegHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), VerificaCompRegHelperTest.class,
                        VerificaCompRegHelper.class));
    }

    @Test
    public void getAaRegistroUnitaDocDaElab_queryIsOk() {
        helper.getAaRegistroUnitaDocDaElab();
        assertTrue(true);
    }

    @Test
    public void getListaUdDaVerificare_queryIsOk() {
        Long idRegistroUnitaDoc = aLong();
        List<Long> idSubStruts = aListOfLong(2);
        Long anno = aLong();
        helper.getListaUdDaVerificare(idRegistroUnitaDoc, idSubStruts, anno);
        assertTrue(true);
    }

    @Test
    public void sbloccaAaRegistroUnitaDoc_queryIsOk() {
        long idAaRegistroUnitaDoc = aLong();
        helper.sbloccaAaRegistroUnitaDoc(idAaRegistroUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void pulisciErroriRegAnnoNewTrans_queryIsOk() {
        long idAaRegistroUnitaDoc = aLong();
        long anno = aLong();
        helper.pulisciErroriRegAnnoNewTrans(idAaRegistroUnitaDoc, anno);
        assertTrue(true);
    }
}
