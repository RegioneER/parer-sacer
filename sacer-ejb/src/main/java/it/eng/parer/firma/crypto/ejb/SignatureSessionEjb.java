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

package it.eng.parer.firma.crypto.ejb;

import java.util.Date;
import java.util.List;

import it.eng.parer.common.signature.SignatureSession;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma;
import it.eng.parer.entity.HsmSessioneFirma;
import it.eng.parer.entity.IamUser;
import it.eng.parer.firma.crypto.sign.SigningRequest;

/**
 * Defines the methods must be implemented by an ejb that manages a signature session
 *
 * @author Moretti_Lu
 */
public interface SignatureSessionEjb {

    /**
     * Creates and stores a {@link HsmSessioneFirma}
     *
     * @param request
     *            SigningRequest
     *
     * @return HsmSessioneFirma
     */
    public HsmSessioneFirma startSessioneFirma(SigningRequest request);

    /**
     * Returns the file to sign
     *
     * @param idFile
     *            id file
     *
     * @return byte[]
     */
    public byte[] getFile2Sign(long idFile);

    /**
     * Returns {@literal true} if {@code fileSbustato} is the same as the original one, otherwise {@literal false}
     *
     * @param idFile
     *            id file
     * @param fileSbustato
     *            file sbustato in byte
     *
     * @return true/false
     */
    public boolean isFileEquals(long idFile, byte[] fileSbustato);

    /**
     * Stores the signature file
     *
     * @param sessionId
     *            id of the current signature session
     * @param idFile
     *            id file
     * @param signedFile
     *            file firmato in byte
     * @param signingDate
     *            data firma
     * @param tipoFirma
     *            tipo firma (XADES o CADES)
     *
     * @throws Exception
     *             errore generico
     */
    public void storeSignature(long sessionId, long idFile, byte[] signedFile, Date signingDate, TipoFirma tipoFirma)
            throws Exception;

    /**
     * Stores the signature file
     *
     * @param session
     *            current signature session
     * @param idFile
     *            id file
     * @param signedFile
     *            firmato in byte
     * @param signingDate
     *            data firma
     * @param tipoFirma
     *            tipo firma (XADES o CADES)
     *
     * @throws Exception
     *             errore generico
     */
    public void storeSignature(HsmSessioneFirma session, long idFile, byte[] signedFile, Date signingDate,
            ElencoEnums.TipoFirma tipoFirma) throws Exception;

    /**
     * Stores the file signing failed and the file status is set the previous one.
     *
     * @param sessionId
     *            id of the current signature session
     * @param idFile
     *            id file
     * @param codError
     *            codice errore
     * @param descrError
     *            descrizione errore
     */
    public void errorFile(long sessionId, long idFile, String codError, String descrError);

    /**
     * Stores the file signing failed and the file status is set the previous one.
     *
     * @param session
     *            current signature session
     * @param idFile
     *            id file
     * @param codError
     *            codice errore
     * @param descrError
     *            descrizione errore
     */
    public void errorFile(HsmSessioneFirma session, long idFile, String codError, String descrError);

    /**
     * Stores the failure of signature session and invokes {@link #errorFile } for all his files
     *
     * @param session
     *            sessione HSM
     * @param codError
     *            if {@literal null} it will be set with
     *            {@link it.eng.parer.common.signature.SignatureSession.CdErr#UNKNOWN_ERROR }
     */
    public void errorSessioneFirma(HsmSessioneFirma session, SignatureSession.CdErr codError);

    /**
     * Stores the failure of signature session and invokes {@link #errorFile } for all his files
     *
     * @param session
     *            sessione HSM
     * @param codError
     *            if {@literal null} it will be set with
     *            {@link it.eng.parer.common.signature.SignatureSession.CdErr#UNKNOWN_ERROR }
     * @param descError
     *            descrizione errore
     */
    public void errorSessioneFirma(HsmSessioneFirma session, SignatureSession.CdErr codError, String descError);

    /**
     * Closes the signature session and stores it
     *
     * @param sessionId
     *            id sessione
     *
     * @return true/false
     */
    public boolean closeSessioneFirma(long sessionId);

    /**
     * Closes the signature session and stores it
     *
     * @param session
     *            sessione HSM
     *
     * @return true/false
     */
    public boolean closeSessioneFirma(HsmSessioneFirma session);

    /**
     * Returns {@literal true} if the user has any active sessionsReturns, otherwise {@literal false}
     *
     * @param user
     *            entity IamUser
     *
     * @return true/false
     */
    public boolean hasUserActiveSessions(IamUser user);

    /**
     * Returns {@literal true} if the user has any active sessionsReturns, otherwise {@literal false}
     *
     * @param userId
     *            the id of the user
     *
     * @return true/false
     */
    public boolean hasUserActiveSessions(long userId);

    /**
     * Returns the list of the active sessions that the user has
     *
     * @param user
     *            entity IamUser
     *
     * @return restituisce lista di elementi di tipo HsmSessioneFirma
     */
    public List<HsmSessioneFirma> getActiveSessionsByUser(IamUser user);

    /**
     * Returns the list of the active sessions that the user has
     *
     * @param userId
     *            the id of the user
     *
     * @return restituisce lista di elementi di tipo HsmSessioneFirma
     */
    public List<HsmSessioneFirma> getActiveSessionsByUser(long userId);

    /**
     * Returns {@literal true} if the user has any blocked sessionsReturns, otherwise {@literal false}
     *
     * @param user
     *            entity IamUser
     *
     * @return true/false
     */
    public boolean hasUserBlockedSessions(IamUser user);

    /**
     * Returns {@literal true} if the user has any blocked sessionsReturns, otherwise {@literal false}
     *
     * @param userId
     *            the id of the user
     *
     * @return true/false
     */
    public boolean hasUserBlockedSessions(long userId);

    /**
     * Unlocks the blocked sessions of this user
     *
     * @param user
     *            entity IamUser
     */
    public void unlockBlockedSessions(IamUser user);

    /**
     * Unlocks the blocked sessions of this user
     *
     * @param userId
     *            the id of the user
     */
    public void unlockBlockedSessions(long userId);
}
