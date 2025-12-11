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

package it.eng.parer.exception;

/**
 *
 * @author Bonora_L
 */
public class AsyncException extends Exception {

    private static final long serialVersionUID = 1L;
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

    public AsyncException(String message, String asyncTask, Long idLock, Long idStrut,
            Exception ex) {
        super(message);
        this.asyncTask = asyncTask;
        this.idLock = idLock;
        this.idStrut = idStrut;
        this.ex = ex;
    }
}
