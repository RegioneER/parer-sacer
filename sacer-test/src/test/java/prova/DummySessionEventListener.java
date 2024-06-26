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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prova;

import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventListener;

/**
 *
 * @author Iacolucci_M
 */
public class DummySessionEventListener implements SessionEventListener {

    @Override
    public void missingDescriptor(SessionEvent se) {
    }

    @Override
    public void moreRowsDetected(SessionEvent se) {
    }

    @Override
    public void noRowsModified(SessionEvent se) {
    }

    @Override
    public void outputParametersDetected(SessionEvent se) {
    }

    @Override
    public void postAcquireClientSession(SessionEvent se) {
    }

    @Override
    public void postAcquireConnection(SessionEvent se) {
    }

    @Override
    public void postAcquireExclusiveConnection(SessionEvent se) {
    }

    @Override
    public void postAcquireUnitOfWork(SessionEvent se) {
    }

    @Override
    public void postBeginTransaction(SessionEvent se) {
    }

    @Override
    public void preCalculateUnitOfWorkChangeSet(SessionEvent se) {
    }

    @Override
    public void postCalculateUnitOfWorkChangeSet(SessionEvent se) {
    }

    @Override
    public void postCommitTransaction(SessionEvent se) {
    }

    @Override
    public void postCommitUnitOfWork(SessionEvent se) {
    }

    @Override
    public void postConnect(SessionEvent se) {
    }

    @Override
    public void postExecuteQuery(SessionEvent se) {
    }

    @Override
    public void postReleaseClientSession(SessionEvent se) {
    }

    @Override
    public void postReleaseUnitOfWork(SessionEvent se) {
    }

    @Override
    public void postResumeUnitOfWork(SessionEvent se) {
    }

    @Override
    public void postRollbackTransaction(SessionEvent se) {
    }

    @Override
    public void postDistributedMergeUnitOfWorkChangeSet(SessionEvent se) {
    }

    @Override
    public void postMergeUnitOfWorkChangeSet(SessionEvent se) {
    }

    @Override
    public void preBeginTransaction(SessionEvent se) {
    }

    @Override
    public void preCommitTransaction(SessionEvent se) {
    }

    @Override
    public void preCommitUnitOfWork(SessionEvent se) {
    }

    @Override
    public void preExecuteQuery(SessionEvent se) {
    }

    @Override
    public void prepareUnitOfWork(SessionEvent se) {
    }

    @Override
    public void preReleaseClientSession(SessionEvent se) {
    }

    @Override
    public void preReleaseConnection(SessionEvent se) {
    }

    @Override
    public void preReleaseExclusiveConnection(SessionEvent se) {
    }

    @Override
    public void preReleaseUnitOfWork(SessionEvent se) {
    }

    @Override
    public void preRollbackTransaction(SessionEvent se) {
    }

    @Override
    public void preDistributedMergeUnitOfWorkChangeSet(SessionEvent se) {
    }

    @Override
    public void preMergeUnitOfWorkChangeSet(SessionEvent se) {
    }

    @Override
    public void preLogin(SessionEvent se) {
    }

    @Override
    public void postLogin(SessionEvent se) {
    }

    @Override
    public void preLogout(SessionEvent se) {
    }

    @Override
    public void postLogout(SessionEvent se) {
    }
    
}
