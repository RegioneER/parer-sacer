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

package it.eng.parer.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class UtilsTest {

    @Test
    public void composeVersioniSrting_nullList() {
        final String versioni = Utils.composeVersioniString(null);
        assertEquals("", versioni);
    }

    @Test
    public void composeVersioniSrting_emptyList() {
        final String versioni = Utils.composeVersioniString(Collections.emptyList());
        assertEquals("", versioni);
    }

    @Test
    public void composeVersioniSrting_unaVersione() {
        final String versioni = Utils.composeVersioniString(Arrays.asList("1.0.0"));
        assertEquals("(vers. 1.0.0)", versioni);
    }

    @Test
    public void composeVersioniSrting_piuVersioni() {
        final String versioni = Utils.composeVersioniString(Arrays.asList("1.0.0", "2.0.0"));
        assertEquals("(vers. 1.0.0,  2.0.0)", versioni);
    }
}
