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

package it.eng.parer.helper;

import it.eng.parer.elencoVersamento.helper.IndiceElencoVersHelperTest;
import it.eng.parer.web.helper.HelperTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.*;
import org.junit.Test;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class GenericHelperTest {
    @EJB
    private GenericHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(GenericHelperTest.class.getSimpleName(),
                HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), GenericHelperTest.class, GenericHelper.class));
    }

    @Test
    public void getEntityManager() {
        assertNotNull(helper.getEntityManager());
    }

    @Test
    public void longListFromBigDecimalList() {
        GenericHelper localHelper = new GenericHelper();
        Collection<BigDecimal> bigDecimalCollection = Arrays.asList(BigDecimal.ZERO, BigDecimal.TEN);
        List<Long> longList = localHelper.longListFrom(bigDecimalCollection);
        assertEquals(2, longList.size());
        assertEquals(0L, longList.get(0).longValue());
        assertEquals(10L, longList.get(1).longValue());
    }

    @Test
    public void bigDecimalListFromLongList() {
        GenericHelper localHelper = new GenericHelper();
        Collection<Long> longList = Arrays.asList(0L, 10L);
        List<BigDecimal> bigDecimalList = localHelper.bigDecimalListFrom(longList);
        assertEquals(2, bigDecimalList.size());
        assertEquals(BigDecimal.ZERO, bigDecimalList.get(0));
        assertEquals(BigDecimal.TEN, bigDecimalList.get(1));
    }

    @Test
    public void getDataNonAnnullata() {
        GenericHelper localHelper = new GenericHelper();
        final String expectedDate = "31/12/2444";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        assertEquals(expectedDate, sdf.format(localHelper.getDataNonAnnullata()));
    }

    @Test
    public void longFromBigDecimal_null() {
        assertNull(GenericHelper.longFromBigDecimal(null));
    }

    @Test
    public void longFromInteger_null() {
        assertNull(GenericHelper.longFromBigDecimal(null));
    }

    @Test
    public void longFromBigDecimal_conValore() {
        assertEquals(Long.valueOf(1), GenericHelper.longFromBigDecimal(BigDecimal.ONE));
    }

    @Test
    public void longFromInteger_conValore() {
        assertEquals(Long.valueOf(2), GenericHelper.longFromInteger(Integer.valueOf(2)));
    }

    @Test
    public void bigDecimalFromLong_null() {
        Long nullLong = null;
        assertEquals(null, GenericHelper.bigDecimalFromLong(nullLong));
    }

    @Test
    public void bigDecimalFromLong_conValore() {
        Long uno = 1L;
        assertEquals(BigDecimal.ONE, GenericHelper.bigDecimalFromLong(uno));
    }

    @Test
    public void bigDecimalFromInteger_null() {
        Integer nullInt = null;
        assertEquals(null, GenericHelper.bigDecimalFromInteger(nullInt));
    }

    @Test
    public void bigDecimalFromInteger_conValore() {
        Integer uno = 1;
        assertEquals(BigDecimal.ONE, GenericHelper.bigDecimalFromInteger(uno));
    }

}
