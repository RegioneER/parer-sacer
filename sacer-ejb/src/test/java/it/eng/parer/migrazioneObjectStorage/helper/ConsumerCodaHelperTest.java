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

package it.eng.parer.migrazioneObjectStorage.helper;

import it.eng.parer.job.verificaCompTipoFasc.helper.VerificaCompTipoFascHelperTest;
import it.eng.parer.migrazioneObjectStorage.exception.MigObjStorageCompHashCalcMoreThanOneException;

import it.eng.parer.web.helper.HelperTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class ConsumerCodaHelperTest {
    @EJB
    private ConsumerCodaHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(ConsumerCodaHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), ConsumerCodaHelperTest.class,
                        ConsumerCodaHelper.class, MigObjStorageCompHashCalcMoreThanOneException.class));
    }

    @Test
    public void getOstMigrazFileLocked_queryIsOk() {
        String nmTabellaIdOggetto = aString();
        BigDecimal idOggetto = aBigDecimal();
        helper.getOstMigrazFileLocked(nmTabellaIdOggetto, idOggetto);
        assertTrue(true);
    }

    @Test
    public void getAroCompHashCalc_queryIsOk() {
        long idCompDoc = aLong();
        String dsAlgoHashFile = aString();
        helper.getAroCompHashCalc(idCompDoc, dsAlgoHashFile);
        assertTrue(true);
    }

    @Test
    public void getAroCompHashCalcByIdOggetto_queryIsOk() {
        BigDecimal idOggetto = aBigDecimal();
        try {
            helper.getAroCompHashCalcByIdOggetto(idOggetto);
            assertTrue(true);
        } catch (Exception e) {
            assertExceptionMessage(e, "MigObjStorageCompHashCalcMoreThanOneException");
        }
    }
}
