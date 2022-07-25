package it.eng.parer.exception;

/**
 *
 * @author Bonora_L
 */
public class AsyncException extends Exception {

    Long idLock;
    Long idStrut;
    String asyncTask;
    Exception ex;

    public Long getIdLock() {
        return idLock;
    }

    public void setIdLock(Long idLock) {
        this.idLock = idLock;
    }

    public String getAsyncTask() {
        return asyncTask;
    }

    public void setAsyncTask(String asyncTask) {
        this.asyncTask = asyncTask;
    }

    public Exception getEx() {
        return ex;
    }

    public void setEx(Exception ex) {
        this.ex = ex;
    }

    public Long getIdStrut() {
        return idStrut;
    }

    public void setIdStrut(Long idStrut) {
        this.idStrut = idStrut;
    }

    public AsyncException(String message, String asyncTask, Long idLock, Long idStrut, Exception ex) {
        super(message);
        this.asyncTask = asyncTask;
        this.idLock = idLock;
        this.idStrut = idStrut;
        this.ex = ex;
    }
}
