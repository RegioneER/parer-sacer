package it.eng.parer.exception;

/**
 * Eccezione specifica lanciata quando la fase atomica di preparazione e avvio della cancellazione
 * fisica fallisce.
 */
public class PreparazioneFisicaException extends RuntimeException {
    public PreparazioneFisicaException(String message, Throwable cause) {
	super(message, cause);
    }
}