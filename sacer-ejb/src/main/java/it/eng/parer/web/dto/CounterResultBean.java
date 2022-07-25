package it.eng.parer.web.dto;

import it.eng.parer.web.util.Constants;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

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
