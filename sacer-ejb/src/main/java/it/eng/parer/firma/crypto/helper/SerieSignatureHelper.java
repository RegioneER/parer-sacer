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

import it.eng.parer.entity.HsmSessioneFirma;
import it.eng.parer.entity.HsmSessioneFirma_;
import it.eng.parer.entity.HsmVerSerieSessioneFirma;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.SerVerSerie;
import it.eng.parer.entity.constraint.HsmSessioneFirma.TiSessioneFirma;
import it.eng.parer.entity.constraint.HsmVerSerieSessioneFirma.TiEsitoFirmaVerSerie;

/**
 * This helper read and store the informations about signature session of <code>Serie</code>
 *
 * @author Moretti_Lu
 */
@SuppressWarnings({ "unchecked" })
@Stateless(mappedName = "SerieSignatureHelper")
@LocalBean
public class SerieSignatureHelper extends SigningHelper {

    private static final Logger logger = LoggerFactory.getLogger(SerieSignatureHelper.class);

    @Override
    public long createSessioneFirma(long userId) {
        HsmSessioneFirma session = new HsmSessioneFirma();
        session.setIamUser(findById(IamUser.class, userId));
        session.setTiSessioneFirma(TiSessioneFirma.SERIE);
        session.setTsInizio(new Date());

        if (session.getHsmVerSerieSessioneFirmas() == null) {
            session.setHsmVerSerieSessioneFirmas(new ArrayList<HsmVerSerieSessioneFirma>());
        }
        insertEntity(session, true);
        logger.debug("Create new HsmSessioneFirma " + session.getIdSessioneFirma());
        return session.getIdSessioneFirma();
    }

    @Override
    public void addFile2SessioneFirma(HsmSessioneFirma session, long verSerieId) {
        if (session == null) {
            throw new IllegalArgumentException();
        }

        this.addFile2SessioneFirma(session.getIdSessioneFirma(), verSerieId);
    }

    @Override
    public void addFile2SessioneFirma(long sessionId, long verSerieId) {
        HsmSessioneFirma session = findById(HsmSessioneFirma.class, sessionId);

        HsmVerSerieSessioneFirma hsmVerSerieSessioneFirma = new HsmVerSerieSessioneFirma();
        hsmVerSerieSessioneFirma.setSerVerSerie(findById(SerVerSerie.class, verSerieId));
        hsmVerSerieSessioneFirma.setTiEsito(TiEsitoFirmaVerSerie.DA_FARE);
        hsmVerSerieSessioneFirma.setTsEsito(new Date());
        session.addHsmVerSerieSessioneFirma(hsmVerSerieSessioneFirma);
        logger.debug("Added versioneserie (id " + verSerieId + ") to HsmSessioneFirma (id "
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
        List<HsmSessioneFirma> result = null;

        Query query = getEntityManager().createQuery("SELECT s " + "FROM HsmSessioneFirma s "
                + "WHERE s.iamUser.idUserIam = :idUser AND s.tsFine IS NULL AND s.tiSessioneFirma = :type");
        query.setParameter("idUser", userId);
        query.setParameter("type", TiSessioneFirma.SERIE);
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
                .createQuery("SELECT e.hsmSessioneFirma.idSessioneFirma, MAX(e.tsEsito) AS TsLastOperation "
                        + "FROM HsmSessioneFirma se INNER JOIN se.hsmVerSerieSessioneFirmas e "
                        + "WHERE se.tsFine IS NULL " + "AND se.iamUser.idUserIam = :userId "
                        + "AND se.tiSessioneFirma = :type " + "GROUP BY e.hsmSessioneFirma.idSessioneFirma");
        query.setParameter("userId", userId);
        query.setParameter("type", TiSessioneFirma.SERIE);

        List<Object[]> list = query.getResultList();
        if (list != null) {
            for (Object[] obj : list) {
                Date TsLastOperation = (Date) obj[1];
                Long diff = new Date().getTime() - TsLastOperation.getTime();
                if (diff > getTimeSessionBlock()) {
                    final Object id = obj[0];
                    HsmSessioneFirma session = findById(HsmSessioneFirma.class,
                            HsmSessioneFirma_.idSessioneFirma.getType().getJavaType().cast(id));
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
            List<HsmVerSerieSessioneFirma> listFile = session.getHsmVerSerieSessioneFirmas();
            result = true;
            for (HsmVerSerieSessioneFirma e : listFile) {
                if (!e.isSigned()) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Returns the {@link HsmVerSerieSessioneFirma} of {@code session} whose <code>Elenco</code> id is {@code idElenco}
     *
     * @param session
     *            sessione HSM
     * @param idElenco
     *            id elenco
     *
     * @return entity HsmVerSerieSessioneFirma
     */
    public HsmVerSerieSessioneFirma findSerieSessione(HsmSessioneFirma session, long idElenco) {
        HsmVerSerieSessioneFirma result = null;
        if (session != null) {
            result = this.findSerieSessione(session.getIdSessioneFirma(), idElenco);
        }
        return result;
    }

    /**
     * Returns the {@link HsmVerSerieSessioneFirma} of {@code session} whose <code>Elenco</code> id is {@code idElenco}
     *
     * @param sessionId
     *            id sessione
     * @param idElenco
     *            id elenco
     *
     * @return entity HsmVerSerieSessioneFirma
     */
    public HsmVerSerieSessioneFirma findSerieSessione(long sessionId, long idElenco) {
        HsmVerSerieSessioneFirma result = null;

        Query query = getEntityManager().createQuery("SELECT e " + "FROM HsmVerSerieSessioneFirma e "
                + "WHERE e.hsmSessioneFirma.idSessioneFirma = :sessionId "
                + "AND e.serVerSerie.idVerSerie = :idElenco");
        query.setParameter("sessionId", sessionId);
        query.setParameter("idElenco", idElenco);
        List<HsmVerSerieSessioneFirma> list = query.getResultList();
        if (list != null && list.size() == 1) {
            result = list.get(0);
        }
        return result;
    }
}
