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

package it.eng.parer.firma.crypto.helper;

import java.util.List;

import javax.ejb.EJB;

import it.eng.parer.entity.HsmSessioneFirma;
import it.eng.parer.entity.IamUser;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.web.helper.ConfigurationHelper;

/**
 * Defines the methods must be implemented by the helper that reads and stores informations about
 * signature session
 *
 * @author Moretti_Lu
 */
public abstract class SigningHelper extends GenericHelper {

    /**
     * Costante utilizzata come parametro per IL CAMPO NM_PARAM_APPLICA della APL_PARAM_APPLIC
     */
    public static final String TIME_SESSION_BLOCKED = "TIMEOUT_SESSIONE_BLOCCATA";

    @EJB
    private ConfigurationHelper confHlp;

    /**
     * Creates a new {@link HsmSessioneFirma}
     *
     * @param userId the user's id
     *
     * @return the id of HsmSessioneFirma
     */
    public abstract long createSessioneFirma(long userId);

    /**
     * Adds a document to the session. The kind of the document must be equal as the session's one
     *
     * @param sessionId  id sessione
     * @param documentId id documento
     */
    public abstract void addFile2SessioneFirma(long sessionId, long documentId);

    /**
     * Adds a document to the session. The kind of the document must be equal as the session's one
     *
     * @param session    sessione HSM
     * @param documentId id documento
     */
    public abstract void addFile2SessioneFirma(HsmSessioneFirma session, long documentId);

    /**
     * Returns the list of the active {@link HsmSessioneFirma}
     *
     * @param userId id utente
     *
     * @return lista oggetti di tipo {@link HsmSessioneFirma}
     */
    public abstract List<HsmSessioneFirma> getActiveSessionsByUser(long userId);

    /**
     * Returns the list of the active {@link HsmSessioneFirma}
     *
     * @param user entity IamUser
     *
     * @return lista oggetti di tipo {@link HsmSessioneFirma}
     */
    public abstract List<HsmSessioneFirma> getActiveSessionsByUser(IamUser user);

    /**
     * Returns the list of blocked {@link HsmSessioneFirma}
     *
     * @param userId id utente
     *
     * @return lista oggetti di tipo {@link HsmSessioneFirma}
     */
    public abstract List<HsmSessioneFirma> getBlockedSessionsByUser(long userId);

    /**
     * Returns the lost of blocked {@link HsmSessioneFirma}
     *
     * @param user entity IamUser
     *
     * @return lista oggetti di tipo {@link HsmSessioneFirma}
     */
    public abstract List<HsmSessioneFirma> getBlockedSessionsByUser(IamUser user);

    /**
     * Returns {@literal true} if all files of the session are signed, otherwise {@literal false}
     *
     * @param sessionId id sessione
     *
     * @return true/false
     */
    public abstract boolean isAllFileSigned(long sessionId);

    /**
     * Returns {@literal true} if all files of the session are signed, otherwise {@literal false}
     *
     * @param session oggetto di tipo HsmSessioneFirma
     *
     * @return true/false
     */
    public abstract boolean isAllFileSigned(HsmSessioneFirma session);

    /**
     * Returns the time after which a signature session will be blocked
     *
     * @return long time
     */
    protected long getTimeSessionBlock() {
	// The value's unit is minute
	String value = confHlp.getValoreParamApplicByApplic(TIME_SESSION_BLOCKED);
	int min = Integer.parseInt(value);

	// Converts the value in milliseconds
	long time = min * 60 * 1000;
	return time;
    }
}
