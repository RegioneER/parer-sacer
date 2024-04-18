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

package it.eng.parer.util;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.xadisk.additional.XAFileInputStreamWrapper;
import org.xadisk.additional.XAFileOutputStreamWrapper;
import org.xadisk.bridge.proxies.interfaces.XADiskBasicIOOperations;
import org.xadisk.bridge.proxies.interfaces.XAFileInputStream;
import org.xadisk.bridge.proxies.interfaces.XAFileOutputStream;
import org.xadisk.filesystem.exceptions.DirectoryNotEmptyException;
import org.xadisk.filesystem.exceptions.FileAlreadyExistsException;
import org.xadisk.filesystem.exceptions.FileNotExistsException;
import org.xadisk.filesystem.exceptions.FileUnderUseException;
import org.xadisk.filesystem.exceptions.InsufficientPermissionOnFileException;
import org.xadisk.filesystem.exceptions.LockingFailedException;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;

import it.eng.parer.exception.XAGenericException;

/**
 *
 * @author Bonora_L
 */
public class XAUtil {

    public static OutputStream createFileOS(XADiskBasicIOOperations session, File file, boolean createFile)
            throws XAGenericException {
        try {
            if (createFile) {
                session.createFile(file, false);
            }
            XAFileOutputStream xafos;
            xafos = session.createXAFileOutputStream(file, true);
            return new XAFileOutputStreamWrapper(xafos);
        } catch (FileNotExistsException | FileUnderUseException | InsufficientPermissionOnFileException
                | LockingFailedException | NoTransactionAssociatedException | InterruptedException
                | FileAlreadyExistsException e) {
            throw new XAGenericException(e);

        }
    }

    public static InputStream createFileIS(XADiskBasicIOOperations session, File file, boolean createFile)
            throws XAGenericException {
        try {
            if (createFile) {
                session.createFile(file, false);
            }
            XAFileInputStream xafis;
            xafis = session.createXAFileInputStream(file);
            return new XAFileInputStreamWrapper(xafis);
        } catch (FileAlreadyExistsException | FileNotExistsException | InsufficientPermissionOnFileException
                | LockingFailedException | NoTransactionAssociatedException | InterruptedException e) {
            throw new XAGenericException(e);

        }
    }

    public static void createDirectory(XADiskBasicIOOperations session, File dir) throws XAGenericException {
        try {
            session.createFile(dir, true);
        } catch (FileAlreadyExistsException | FileNotExistsException | InsufficientPermissionOnFileException
                | LockingFailedException | NoTransactionAssociatedException | InterruptedException e) {
            throw new XAGenericException(e);

        }
    }

    public static File[] listFiles(XADiskBasicIOOperations session, File root) throws XAGenericException {
        try {

            String[] filesName = session.listFiles(root);
            File[] files = new File[filesName.length];
            for (int i = 0; i < filesName.length; i++) {
                files[i] = new File(root, filesName[i]);
            }
            return files;
        } catch (FileNotExistsException | LockingFailedException | NoTransactionAssociatedException
                | InsufficientPermissionOnFileException | InterruptedException e) {
            throw new XAGenericException(e);
        }
    }

    public static void moveFile(XADiskBasicIOOperations session, File file, File dest) throws XAGenericException {
        try {
            session.moveFile(file, dest);
        } catch (FileNotExistsException | LockingFailedException | NoTransactionAssociatedException
                | InsufficientPermissionOnFileException | InterruptedException | FileAlreadyExistsException
                | FileUnderUseException e) {
            throw new XAGenericException(e);
        }
    }

    public static void copyFile(XADiskBasicIOOperations session, File file, File dest) throws XAGenericException {
        try {
            session.copyFile(file, dest);
        } catch (FileAlreadyExistsException | FileNotExistsException | InsufficientPermissionOnFileException
                | LockingFailedException | NoTransactionAssociatedException | InterruptedException e) {
            throw new XAGenericException(e);
        }
    }

    public static void deleteFile(XADiskBasicIOOperations session, File file) throws XAGenericException {

        try {
            session.deleteFile(file);
        } catch (DirectoryNotEmptyException | FileNotExistsException | FileUnderUseException
                | InsufficientPermissionOnFileException | LockingFailedException | NoTransactionAssociatedException
                | InterruptedException e) {
            throw new XAGenericException(e);
        }

    }

    public static boolean fileExistsAndIsDirectory(XADiskBasicIOOperations session, File file)
            throws XAGenericException {
        try {
            return session.fileExistsAndIsDirectory(file);
        } catch (InsufficientPermissionOnFileException | LockingFailedException | NoTransactionAssociatedException
                | InterruptedException e) {
            throw new XAGenericException(e);
        }

    }

    public static boolean fileExistsAndIsDirectoryLockExclusive(XADiskBasicIOOperations session, File file)
            throws XAGenericException {
        try {
            return session.fileExistsAndIsDirectory(file, true);
        } catch (InsufficientPermissionOnFileException | LockingFailedException | NoTransactionAssociatedException
                | InterruptedException e) {
            throw new XAGenericException(e);
        }

    }

    public static boolean fileExists(XADiskBasicIOOperations session, File file) throws XAGenericException {
        try {
            return session.fileExists(file);
        } catch (InsufficientPermissionOnFileException | LockingFailedException | NoTransactionAssociatedException
                | InterruptedException e) {
            throw new XAGenericException(e);
        }

    }

    public static long getFileLength(XADiskBasicIOOperations session, File file) throws XAGenericException {
        try {
            return session.getFileLength(file);
        } catch (InterruptedException | FileNotExistsException | LockingFailedException
                | NoTransactionAssociatedException | InsufficientPermissionOnFileException e) {
            throw new XAGenericException(e);
        }

    }

    /**
     * Metodo statico che rimuove ricorsivamente i dati contenuti in una directory in transazione
     *
     * @param session
     *            la sessione in transazione
     * @param dirPath
     *            la directory
     * 
     * @throws XAGenericException
     *             errore generico Xa
     * @throws DirectoryNotEmptyException
     *             errore generico
     * @throws FileNotExistsException
     *             errore file non presente
     * @throws FileUnderUseException
     *             errore generico su file
     * @throws InsufficientPermissionOnFileException
     *             errore su privilegi accesso file
     * @throws LockingFailedException
     *             errore su lock accesso
     * @throws NoTransactionAssociatedException
     *             errore generico su transazione
     * @throws InterruptedException
     *             errore generico
     */
    public static void rimuoviFileRicorsivamente(XADiskBasicIOOperations session, File dirPath)
            throws XAGenericException, DirectoryNotEmptyException, FileNotExistsException, FileUnderUseException,
            InsufficientPermissionOnFileException, LockingFailedException, NoTransactionAssociatedException,
            InterruptedException {
        File[] elencoFile = XAUtil.listFiles(session, dirPath);
        if (elencoFile != null && elencoFile.length > 0) {
            for (File tmpFile : elencoFile) {
                if (XAUtil.fileExistsAndIsDirectory(session, tmpFile)) {
                    rimuoviFileRicorsivamente(session, tmpFile);
                } else {
                    XAUtil.deleteFile(session, tmpFile);
                }
            }
            XAUtil.deleteFile(session, dirPath);
        }
    }
}
