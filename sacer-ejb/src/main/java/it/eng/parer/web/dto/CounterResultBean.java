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

package it.eng.parer.web.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.eng.parer.web.util.Constants;

/**
 * DTO che rappresenta il risultato del WS REST DocCounter. Il ws è presente nello strato web. La sua implementazione è
 * 
 * <pre>
 * {@code it.eng.parer.restWS.DocCounter}
 * </pre>
 *
 * @author Filippini_M
 */
public class CounterResultBean implements Serializable {

    private static final long serialVersionUID = -591027416472646269L;

    private String date;
    private long counter;

    public CounterResultBean() {

    }

    /**
     * Costrutture parametrico. Imposta gli attributi dell'oggetto solamente se rispondenti.
     *
     * @param date
     *            data
     * @param counter
     *            contatore
     */
    public CounterResultBean(Object date, BigDecimal counter) {
        if (date != null && date instanceof Date && counter != null) {
            this.date = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE).format(date);
            this.counter = counter.longValue();
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

}
