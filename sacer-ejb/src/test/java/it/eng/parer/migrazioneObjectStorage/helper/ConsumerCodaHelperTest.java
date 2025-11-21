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

package it.eng.parer.migrazioneObjectStorage.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.assertExceptionMessage;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.migrazioneObjectStorage.exception.MigObjStorageCompHashCalcMoreThanOneException;
import it.eng.parer.web.helper.HelperTest;

@ArquillianTest
public class ConsumerCodaHelperTest {
    @EJB
    private ConsumerCodaHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(ConsumerCodaHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), ConsumerCodaHelperTest.class,
                        ConsumerCodaHelper.class,
                        MigObjStorageCompHashCalcMoreThanOneException.class));
    }

    @Test
    void getOstMigrazFileLocked_queryIsOk() {
        String nmTabellaIdOggetto = aString();
        BigDecimal idOggetto = aBigDecimal();
        helper.getOstMigrazFileLocked(nmTabellaIdOggetto, idOggetto);
        assertTrue(true);
    }

    @Test
    void getAroCompHashCalc_queryIsOk() {
        long idCompDoc = aLong();
        String dsAlgoHashFile = aString();
        helper.getAroCompHashCalc(idCompDoc, dsAlgoHashFile);
        assertTrue(true);
    }

    @Test
    void getAroCompHashCalcByIdOggetto_queryIsOk() {
        BigDecimal idOggetto = aBigDecimal();
        try {
            helper.getAroCompHashCalcByIdOggetto(idOggetto);
            assertTrue(true);
        } catch (Exception e) {
            assertExceptionMessage(e, "MigObjStorageCompHashCalcMoreThanOneException");
        }
    }
}
