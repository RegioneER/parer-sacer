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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.ElvElencoVersFasc;
import it.eng.parer.entity.HsmElencoFascSesFirma;
import it.eng.parer.entity.HsmSessioneFirma;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.constraint.HsmElencoFascSesFirma.TiEsitoFirmaElencoFasc;
import it.eng.parer.entity.constraint.HsmSessioneFirma.TiSessioneFirma;

/**
 * This helper read and store the informations about signature session of <code>Elenco</code>
 *
 * @author DiLorenzo_F
 */
@SuppressWarnings({
	"unchecked" })
@Stateless(mappedName = "ElenchiFascSignatureHelper")
@LocalBean
public class ElenchiFascSignatureHelper extends SigningHelper {

    private static final Logger logger = LoggerFactory.getLogger(ElenchiFascSignatureHelper.class);

    @Override
    public long createSessioneFirma(long userId) {
	HsmSessioneFirma session = new HsmSessioneFirma();
	session.setIamUser(findById(IamUser.class, userId));
	session.setTiSessioneFirma(TiSessioneFirma.ELENCHI_FASC);
	session.setTsInizio(new Date());

	if (session.getHsmElencoFascSesFirmas() == null) {
	    session.setHsmElencoFascSesFirmas(new ArrayList<HsmElencoFascSesFirma>());
	}
	insertEntity(session, true);
	logger.debug("Create new HsmSessioneFirma " + session.getIdSessioneFirma());
	return session.getIdSessioneFirma();
    }

    @Override
    public void addFile2SessioneFirma(HsmSessioneFirma session, long elencoId) {
	if (session == null) {
	    throw new IllegalArgumentException();
	}

	this.addFile2SessioneFirma(session.getIdSessioneFirma(), elencoId);
    }

    @Override
    public void addFile2SessioneFirma(long sessionId, long elencoId) {
	HsmSessioneFirma session = findById(HsmSessioneFirma.class, sessionId);

	HsmElencoFascSesFirma hsmElencoFascSesFirma = new HsmElencoFascSesFirma();
	hsmElencoFascSesFirma.setElvElencoVersFasc(findById(ElvElencoVersFasc.class, elencoId));
	hsmElencoFascSesFirma.setTiEsito(TiEsitoFirmaElencoFasc.DA_FARE);
	hsmElencoFascSesFirma.setTsEsito(new Date());
	session.addHsmElencoFascSesFirma(hsmElencoFascSesFirma);
	logger.debug("Added elenco (id " + elencoId + ") to HsmSessioneFirma (id "
		+ session.getIdSessioneFirma() + ")");
    }

    @Override
    public List<HsmSessioneFirma> getActiveSessionsByUser(IamUser user) {
	List<HsmSessioneFirma> result = null;
	if (user != null) {
	    result = this.getActiveSessionsByUser(user.getIdUserIam());
	}
	return result;
    }

    @Override
    public List<HsmSessioneFirma> getActiveSessionsByUser(long userId) {
	List<HsmSessioneFirma> result;

	Query query = getEntityManager().createQuery("SELECT s " + "FROM HsmSessioneFirma s "
		+ "WHERE s.iamUser.idUserIam = :idUser AND s.tsFine IS NULL AND s.tiSessioneFirma = :type");
	query.setParameter("idUser", userId);
	query.setParameter("type", TiSessioneFirma.ELENCHI_FASC);
	result = query.getResultList();
	return result;
    }

    @Override
    public List<HsmSessioneFirma> getBlockedSessionsByUser(IamUser user) {
	List<HsmSessioneFirma> result = null;
	if (user != null) {
	    result = this.getBlockedSessionsByUser(user.getIdUserIam());
	}
	return result;
    }

    @Override
    public List<HsmSessioneFirma> getBlockedSessionsByUser(long userId) {
	List<HsmSessioneFirma> result = new LinkedList<>();

	Query query = getEntityManager()
		.createQuery("SELECT se.idSessioneFirma, MAX(e.tsEsito) AS TsLastOperation "
			+ "FROM HsmSessioneFirma se INNER JOIN se.hsmElencoFascSesFirmas e "
			+ "WHERE se.tsFine IS NULL " + "AND se.iamUser.idUserIam = :userId "
			+ "AND se.tiSessioneFirma = :type " + "GROUP BY se.idSessioneFirma");
	query.setParameter("userId", userId);
	query.setParameter("type", TiSessioneFirma.ELENCHI_FASC);

	List<Object[]> list = query.getResultList();
	if (list != null) {
	    for (Object[] obj : list) {
		Date TsLastOperation = (Date) obj[1];
		Long diff = new Date().getTime() - TsLastOperation.getTime();
		if (diff > getTimeSessionBlock()) {
		    HsmSessioneFirma session = findById(HsmSessioneFirma.class, (Long) obj[0]);
		    result.add(session);
		}
	    }
	}
	return result;
    }

    @Override
    public boolean isAllFileSigned(HsmSessioneFirma session) {
	boolean result = false;
	if (session != null) {
	    result = this.isAllFileSigned(session.getIdSessioneFirma());
	}
	return result;
    }

    @Override
    public boolean isAllFileSigned(long sessionId) {
	boolean result = false;
	HsmSessioneFirma session = findById(HsmSessioneFirma.class, sessionId);
	if (session != null) {
	    List<HsmElencoFascSesFirma> listFile = session.getHsmElencoFascSesFirmas();
	    result = true;
	    for (HsmElencoFascSesFirma e : listFile) {
		if (!e.isSigned()) {
		    result = false;
		    break;
		}
	    }
	}
	return result;
    }

    /**
     * Returns the {@link HsmElencoFascSesFirma} of {@code session} whose <code>Elenco</code> id is
     * {@code idElenco}
     *
     * @param session  sessione HSM
     * @param idElenco id elenco
     *
     * @return HsmElencoFascSesFirma entity HsmElencoFascSesFirma
     */
    public HsmElencoFascSesFirma findElencoSessione(HsmSessioneFirma session, long idElenco) {
	HsmElencoFascSesFirma result = null;
	if (session != null) {
	    result = this.findElencoFascSes(session.getIdSessioneFirma(), idElenco);
	}
	return result;
    }

    /**
     * Returns the {@link HsmElencoFascSesFirma} of {@code session} whose <code>Elenco</code> id is
     * {@code idElenco}
     *
     * @param sessionId id sessione
     * @param idElenco  id elenco
     *
     * @return HsmElencoFascSesFirma entity HsmElencoFascSesFirma
     */
    public HsmElencoFascSesFirma findElencoFascSes(long sessionId, long idElenco) {
	HsmElencoFascSesFirma result = null;

	Query query = getEntityManager().createQuery("SELECT e " + "FROM HsmElencoFascSesFirma e "
		+ "WHERE e.hsmSessioneFirma.idSessioneFirma = :sessionId "
		+ "AND e.elvElencoVersFasc.idElencoVersFasc = :idElenco");
	query.setParameter("sessionId", sessionId);
	query.setParameter("idElenco", idElenco);
	List<HsmElencoFascSesFirma> list = query.getResultList();
	if (list != null && list.size() == 1) {
	    result = list.get(0);
	}
	return result;
    }
}
