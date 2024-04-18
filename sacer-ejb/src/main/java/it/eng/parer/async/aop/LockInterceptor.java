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

package it.eng.parer.async.aop;

import it.eng.parer.async.helper.AsyncHelper;
import it.eng.parer.exception.AsyncException;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.utils.JobConstants;
import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 *
 * @author Bonora_L
 */
public class LockInterceptor {

    @EJB
    private AsyncHelper asyncHelper;

    @AroundInvoke
    public Object catchException(InvocationContext inv) throws Exception {
        try {
            Object obj = inv.proceed();
            return obj;
        } catch (ParerInternalError ie) {
            String message = null;
            Long idLock = null;
            Long idStrut = null;
            String task = null;
            Exception nativeExcp = ie.getNativeException();
            if (nativeExcp != null) {
                message = nativeExcp.getMessage();
                if (nativeExcp instanceof AsyncException) {
                    idLock = ((AsyncException) nativeExcp).getIdLock();
                    task = ((AsyncException) nativeExcp).getAsyncTask();
                    idStrut = ((AsyncException) nativeExcp).getIdStrut();
                }
            }
            if (ie.getCause() != null) {
                message = ie.getCause().getMessage();
            }
            if (message == null) {
                message = ie.getDescription();
            }
            if (message.length() > 1024) {
                message = message.substring(0, 1024);
            }

            if (idLock != null && task != null) {
                asyncHelper.writeEndLogLock(idLock, task, JobConstants.OpTypeEnum.ERRORE.name(), message, idStrut);
            }
            throw ie;
        }
    }

}
