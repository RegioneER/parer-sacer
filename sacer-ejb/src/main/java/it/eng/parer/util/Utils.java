package it.eng.parer.util;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 *
 * @author Iacolucci_M
 */
public class Utils {
    /*
     * Torna una lista di date con tutti i giorni compresi tra due date con orario a 0 fino ai millisecondi
     */
    public static List<Date> getDatesBetween(Date startDate, Date endDate) {
        List<Date> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.set(Calendar.HOUR_OF_DAY, 0);
        endCalendar.set(Calendar.MINUTE, 0);
        endCalendar.set(Calendar.SECOND, 0);
        endCalendar.set(Calendar.MILLISECOND, 0);
        endCalendar.setTime(endDate);
        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return datesInRange;
    }

    public static Date getIeri() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    /*
     * Restituisce una stringa normalizzata secondo le regole cel codice UD normalizzato sostituendo tutti i caratteri
     * accentati con i corrispondenti non accentati e ammettendo solo lettere, numeri, '.', '-' e '_'. Tutto il resto
     * viene convertito in '_'.
     */
    public static String getNormalizedUDCode(String udCode) {
        return Normalizer.normalize(udCode, Normalizer.Form.NFD).replaceAll(" ", "_")
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "").replaceAll("[^A-Za-z0-9\\. _-]", "_");
    }

    public static BigDecimal getNumericAnnoMeseFromDate(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        month++;
        String annoMese = "" + year + "" + (month > 9 ? month : "0" + month);
        return new BigDecimal(annoMese);
    }

    public static Date getDataInfinita() {
        Calendar c = Calendar.getInstance();
        c.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
}
