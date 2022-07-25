/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.exception;

import it.eng.parer.exception.ParerErrorCategory.SacerErrorCategory;

/**
 *
 * @author sinatti_s
 */
public class SacerRuntimeException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -8085057602273373872L;
    private final SacerErrorCategory category;

    public SacerRuntimeException() {
        super();
        this.category = SacerErrorCategory.INTERNAL_ERROR; // default
    }

    public SacerRuntimeException(SacerErrorCategory category) {
        super();
        this.category = category;
    }

    public SacerRuntimeException(String message, Throwable throwable, SacerErrorCategory category) {
        super(message, throwable);
        this.category = category;
    }

    public SacerRuntimeException(Throwable throwable, SacerErrorCategory category) {
        super(throwable);
        this.category = category;
    }

    public SacerRuntimeException(String message, SacerErrorCategory category) {
        super(message);
        this.category = category;
    }

    public SacerErrorCategory getCategory() {
        return category;
    }

    @Override
    public String getLocalizedMessage() {
        return "[" + getCategory().toString() + "]" + "  " + super.getLocalizedMessage();
    }

    @Override
    public String getMessage() {
        return "[" + getCategory().toString() + "]" + "  " + super.getMessage();
    }

}
