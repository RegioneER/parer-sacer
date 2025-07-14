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

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Iacolucci_M
 */
public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    /*
     * Torna una lista di date con tutti i giorni compresi tra due date con orario a 0 fino ai
     * millisecondi
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
     * Restituisce una stringa normalizzata secondo le regole cel codice UD normalizzato sostituendo
     * tutti i caratteri accentati con i corrispondenti non accentati e ammettendo solo lettere,
     * numeri, '.', '-' e '_'. Tutto il resto viene convertito in '_'.
     */
    public static String getNormalizedUDCode(String udCode) {
	return Normalizer.normalize(udCode, Normalizer.Form.NFD).replaceAll(" ", "_")
		.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
		.replaceAll("[^A-Za-z0-9\\. _-]", "_");
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

    public static String composeVersioniString(List<String> listaVersioni) {
	StringBuilder sb = new StringBuilder();
	if (listaVersioni != null && !listaVersioni.isEmpty()) {
	    final String separatore = ", ";
	    sb.append("(vers.");
	    listaVersioni.stream().forEachOrdered(v -> {
		sb.append(" ").append(v);
		sb.append(separatore);
	    });
	    // rimuovo l'ultima separatore
	    sb.delete(sb.length() - separatore.length(), sb.length());
	    sb.append(")");
	}
	return sb.toString();
    }

    /* Type conversion */
    public static BigDecimal bigDecimalFromLong(Long numero) {
	return numero == null ? null : BigDecimal.valueOf(numero);
    }

    public static BigDecimal bigDecimalFromInteger(Integer numero) {
	return numero == null ? null : BigDecimal.valueOf(numero);
    }

    public static List<BigDecimal> bigDecimalFromLong(Collection<Long> longList) {
	return longList.stream().map(BigDecimal::valueOf).collect(Collectors.toList());
    }

    public static List<Long> longListFrom(
	    Collection<? extends BigDecimal> idElencoVersFascSelezionatiList) {
	return idElencoVersFascSelezionatiList.stream().map(BigDecimal::longValue)
		.collect(Collectors.toList());
    }

    public static Long longFromBigDecimal(BigDecimal bigDecimal) {
	return bigDecimal == null ? null : bigDecimal.longValue();
    }

    public static Long longFromInteger(Integer integer) {
	return integer == null ? null : integer.longValue();
    }

    /* Miscelenous */

    public static void createEmptyDir(String fullPath) throws IOException {
	Path dirPath = Paths.get(fullPath);
	File directory = dirPath.toFile();
	if (!directory.exists()) {
	    logger.debug("La cartella {} non esiste, la creo", fullPath);
	    Files.createDirectories(dirPath);
	}
    }

    public static void createEmptyDirWithDelete(String fullPath) throws IOException {
	Path dirPath = Paths.get(fullPath);
	File directory = dirPath.toFile();
	if (directory.exists()) {
	    logger.debug("La cartella {} esiste, la dobbiamo svuotare", fullPath);
	    File[] files = directory.listFiles((dir, name) -> {
		boolean toDelete = !name.matches("\\.nfs.+");
		logger.debug("File {} lo devo cancellare? {}", name, toDelete);
		return toDelete;
	    });
	    if (files != null) {
		for (File file : files) {
		    logger.debug("Procedo alla cancellazione di {}", file.getAbsolutePath());
		    FileUtils.forceDelete(file);
		}
	    }
	} else {
	    logger.debug("La cartella {} non esiste, la creo", fullPath);
	    Files.createDirectories(dirPath);
	}
    }
}
