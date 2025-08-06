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
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.web.util;

/**
 *
 * @author Gilioli_P
 */
public class StringPadding {
    public static final int PADDING_LEFT = 0;
    public static final int PADDING_RIGHT = 1;

    /**
     * Metodo per l'esecuzione del padding su una stringa.
     *
     * @param str         la stringa di cui effettuare il padding
     * @param paddingChar il carattere o stringa con cui eseguire il padding
     * @param lngth       il numero di caratteri della stringa di output
     * @param paddingSide l'orientamento del padding (sinistra, destra)
     *
     * @return str, la stringa "paddata"
     */
    public static String padString(String str, String paddingChar, int lngth, int paddingSide) {
	if (str == null) {
	    str = "";
	}

	if (str.length() < lngth) {
	    for (int k = str.length(); k < lngth; k++) {
		if (paddingSide == PADDING_LEFT) {
		    str = paddingChar + str;
		} else if (paddingSide == PADDING_RIGHT) {
		    str = str + paddingChar;
		} else {
		    throw new IllegalArgumentException("Direzione padding errata!");
		}
	    }
	}
	return str;
    }
}
