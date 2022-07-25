/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
     * @param str
     *            la stringa di cui effettuare il padding
     * @param paddingChar
     *            il carattere o stringa con cui eseguire il padding
     * @param lngth
     *            il numero di caratteri della stringa di output
     * @param paddingSide
     *            l'orientamento del padding (sinistra, destra)
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
