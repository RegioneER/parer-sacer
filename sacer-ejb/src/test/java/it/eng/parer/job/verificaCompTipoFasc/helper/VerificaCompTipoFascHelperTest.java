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

package it.eng.parer.job.verificaCompTipoFasc.helper;

import it.eng.parer.job.verificaCompRegistro.helper.VerificaCompRegHelperTest;
import it.eng.parer.web.helper.HelperTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.Arrays;

public class VerificaCompTipoFascHelperTest extends HelperTest<VerificaCompTipoFascHelper> {
    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(VerificaCompTipoFascHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), VerificaCompTipoFascHelperTest.class,
                        VerificaCompTipoFascHelper.class));
    }

    @Test
    public void getAaTipoFascicoloDaElab_queryIsOk() {
        helper.getAaTipoFascicoloDaElab();
        assertTrue(true);
    }

    @Test
    public void getListaFasFascicoloDaVerificare_queryIsOk() {
        long idTipoFascicolo = aLong();
        long idStrut = aLong();
        Long anno = aLong();
        helper.getListaFasFascicoloDaVerificare(idTipoFascicolo, idStrut, anno);
        assertTrue(true);
    }

    @Test
    public void sbloccaAaTipoFascicolo_queryIsOk() {
        long idAaTipoFasc = aLong();
        helper.sbloccaAaTipoFascicolo(idAaTipoFasc);
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
