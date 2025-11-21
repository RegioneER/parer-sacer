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

package it.eng.parer.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

public class UtilsTest {

    @Test
    void composeVersioniSrting_nullList() {
        final String versioni = Utils.composeVersioniString(null);
        assertEquals("", versioni);
    }

    @Test
    void composeVersioniSrting_emptyList() {
        final String versioni = Utils.composeVersioniString(Collections.emptyList());
        assertEquals("", versioni);
    }

    @Test
    void composeVersioniSrting_unaVersione() {
        final String versioni = Utils.composeVersioniString(Arrays.asList("1.0.0"));
        assertEquals("(vers. 1.0.0)", versioni);
    }

    @Test
    void composeVersioniSrting_piuVersioni() {
        final String versioni = Utils.composeVersioniString(Arrays.asList("1.0.0", "2.0.0"));
        assertEquals("(vers. 1.0.0,  2.0.0)", versioni);
    }

    @Test
    void longFromBigDecimal_null() {
        assertNull(Utils.longFromBigDecimal(null));
    }

    @Test
    void longFromInteger_null() {
        assertNull(Utils.longFromBigDecimal(null));
    }

    @Test
    void longFromBigDecimal_conValore() {
        assertEquals(Long.valueOf(1), Utils.longFromBigDecimal(BigDecimal.ONE));
    }

    @Test
    void longFromInteger_conValore() {
        assertEquals(Long.valueOf(2), Utils.longFromInteger(Integer.valueOf(2)));
    }

    @Test
    void bigDecimalFromLong_null() {
        Long nullLong = null;
        assertEquals(null, Utils.bigDecimalFromLong(nullLong));
    }

    @Test
    void bigDecimalFromLong_conValore() {
        Long uno = 1L;
        assertEquals(BigDecimal.ONE, Utils.bigDecimalFromLong(uno));
    }

    @Test
    void bigDecimalFromInteger_null() {
        Integer nullInt = null;
        assertEquals(null, Utils.bigDecimalFromInteger(nullInt));
    }

    @Test
    void bigDecimalFromInteger_conValore() {
        Integer uno = 1;
        assertEquals(BigDecimal.ONE, Utils.bigDecimalFromInteger(uno));
    }

}
