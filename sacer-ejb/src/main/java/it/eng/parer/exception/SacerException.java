/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.exception;

import it.eng.parer.exception.ParerErrorCategory.SacerErrorCategory;

public class SacerException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -9173356878746119329L;
    private final SacerErrorCategory category;

    public SacerException() {
        super();
        this.category = SacerErrorCategory.INTERNAL_ERROR; // default
    }

    public SacerException(SacerErrorCategory category) {
        super();
        this.category = category;
    }

    public SacerException(String message, Throwable cause, SacerErrorCategory category) {
        super(message, cause);
        this.category = category;
    }

    public SacerException(String message, SacerErrorCategory category) {
        super(message);
        this.category = category;
    }

    public SacerException(Throwable cause, SacerErrorCategory category) {
        super(cause);
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
