package it.eng.parer.exception;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Un'istanza di <code>ParerInternalError</code> rappresenta un errore non codificato. Questo significa che non esiste
 * nessun repository contenente il riferimento a questo errore.
 * 
 * @version 1.0, 06/03/2002
 * 
 * @author Luigi Bellio
 */
public class ParerInternalError extends ParerAbstractError implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String INTERNAL_ERROR_ELEMENT = "INTERNAL_ERROR";
    public static final String ERROR_NATIVE_EXCEPTION = "NATIVE_EXCEPTION";
    private Exception _nativeException = null;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public ParerInternalError(String severity, Exception ex) {
        super();
        init(severity, null, ex);
    } // public ParerInternalError(String severity, Exception ex)

    public ParerInternalError(Exception ex) {
        super();
        init(ParerErrorSeverity.ERROR, null, ex);
    }

    public ParerInternalError(String description) {
        super();
        init(ParerErrorSeverity.ERROR, description, null);
    }

    /**
     * Costruisce un oggetto di tipo <code>ParerInternalError</code> identificandolo tramite una severity , una
     * descrizione e un'eccezione.
     * 
     * @param severity
     *            severity dell'errore.
     * @param description
     *            descrizione dell'errore.
     * @param ex
     *            eccezione.
     */
    public ParerInternalError(String severity, String description, Exception ex) {
        super();
        init(severity, description, ex);
    } // public ParerInternalError(String severity, String description, Exception ex)

    /**
     * Questo metodo ha il compito di inizializzare lo stato dell'oggetto,viene invocato da tutti i costruttori di
     * <code>ParerInternalError</code>.
     */
    private void init(String severity, String description, Exception ex) {
        logger.debug("ParerInternalError::init: invocato");
        setSeverity(severity);
        logger.debug("ParerInternalError::init: severity [" + getSeverity() + "]");
        _nativeException = null;
        if (ex != null) {
            _nativeException = ex;
            StringWriter exStringWriter = new StringWriter();
            PrintWriter exPrintWriter = new PrintWriter(exStringWriter);
            ex.printStackTrace(exPrintWriter);
            if (description == null)
                description = exStringWriter.toString();
            else
                description += "\n" + exStringWriter.toString();
        } // if (ex != null)
        setDescription(description);
        logger.debug("ParerInternalError::init: description [" + getDescription() + "]");
    }

    public String getCategory() {
        return ParerErrorCategory.INTERNAL_ERROR;
    }

    /**
     * Se l'oggetto è stato costruito ricevendo come parametro un' eccezione questa viene ritornata.
     * 
     * @return <code>Exception</code> parametro passato in input.
     */
    public Exception getNativeException() {
        return _nativeException;
    } // public Exception getNativeException()

} // public class ParerInternalError extends EMFAbstractError implements Serializable
