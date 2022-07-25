/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.web.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 *
 * @author Gilioli_P
 */
public class Formatter {

    public static DecimalFormat getDecimalFormatter() {
        // formatto il campo size bytes
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("it"));
        String format = "###,###.###";
        DecimalFormat formatter = new DecimalFormat(format, symbols);
        formatter.setGroupingSize(3);
        return formatter;
    }

}
