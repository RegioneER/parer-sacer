package it.eng.parer.exception;

public class XAGenericException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -1015652104278250883L;

    public XAGenericException(Exception e) {
        super(e);
    }

}
