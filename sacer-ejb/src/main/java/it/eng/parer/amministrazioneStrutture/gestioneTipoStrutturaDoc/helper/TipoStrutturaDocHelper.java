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

package it.eng.parer.amministrazioneStrutture.gestioneTipoStrutturaDoc.helper;

import it.eng.parer.entity.*;
import it.eng.parer.helper.GenericHelper;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 * Helper delle tipologie di struttura documento
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
public class TipoStrutturaDocHelper extends GenericHelper {

    public DecTipoStrutDoc getDecTipoStrutDocByName(String nmTipoStrutDoc, BigDecimal idStrut) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT tipoStrutDoc FROM DecTipoStrutDoc tipoStrutDoc WHERE tipoStrutDoc.nmTipoStrutDoc = :nmTipoStrutDoc");
        if (idStrut != null) {
            queryStr.append(" AND tipoStrutDoc.orgStrut.idStrut=:idStrut");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idStrut != null) {
            query.setParameter("idStrut", longFromBigDecimal(idStrut));
        }

        query.setParameter("nmTipoStrutDoc", nmTipoStrutDoc);
        List<DecTipoStrutDoc> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    public List<DecTipoStrutDoc> getDecTipoStrutDocList(BigDecimal idStrut, boolean filterValid) {
        StringBuilder queryStr = new StringBuilder("SELECT tipoStrutDoc FROM DecTipoStrutDoc tipoStrutDoc ");
        String clause = " WHERE ";
        if (idStrut != null) {
            queryStr.append(clause).append("tipoStrutDoc.orgStrut.idStrut = :idStrut");
            clause = " AND ";
        }
        if (filterValid) {
            queryStr.append(clause)
                    .append(" tipoStrutDoc.dtIstituz <= :filterDate AND tipoStrutDoc.dtSoppres >= :filterDate ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idStrut != null) {
            query.setParameter("idStrut", longFromBigDecimal(idStrut));
        }
        if (filterValid) {
            Date now = Calendar.getInstance().getTime();
            query.setParameter("filterDate", now);
        }

        return query.getResultList();
    }

    public DecTipoCompDoc getDecTipoCompDocByName(String nmTipoCompDoc, BigDecimal idTipoStrutDoc) {
        Query query = getEntityManager().createQuery("SELECT tipoCompDoc FROM DecTipoCompDoc tipoCompDoc "
                + "WHERE tipoCompDoc.nmTipoCompDoc = :nmTipoCompDoc and tipoCompDoc.decTipoStrutDoc.idTipoStrutDoc = :tipostrut");
        query.setParameter("nmTipoCompDoc", nmTipoCompDoc);
        query.setParameter("tipostrut", longFromBigDecimal(idTipoStrutDoc));
        List<DecTipoCompDoc> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public DecTipoCompDoc getDecTipoCompDocByName(BigDecimal idStrut, String nmTipoStrutDoc, String nmTipoCompDoc) {
        String queryStr = "SELECT tipoCompDoc FROM DecTipoCompDoc tipoCompDoc "
                + "WHERE tipoCompDoc.nmTipoCompDoc = :nmTipoCompDoc "
                + "AND tipoCompDoc.decTipoStrutDoc.nmTipoStrutDoc = :nmTipoStrutDoc "
                + "AND tipoCompDoc.decTipoStrutDoc.orgStrut.idStrut = :idStrut ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("nmTipoCompDoc", nmTipoCompDoc);
        query.setParameter("nmTipoStrutDoc", nmTipoStrutDoc);
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        List<DecTipoCompDoc> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public List<DecTipoCompDoc> getDecTipoCompDocList(BigDecimal idTipoStrutDoc, boolean filterValid) {
        StringBuilder queryStr = new StringBuilder("SELECT tipoCompDoc FROM DecTipoCompDoc tipoCompDoc ");
        String clause = " WHERE ";
        if (idTipoStrutDoc != null) {
            queryStr.append(clause).append(" tipoCompDoc.decTipoStrutDoc.idTipoStrutDoc = :idTipoStrutDoc");
            clause = " AND ";
        }
        if (filterValid) {
            queryStr.append(clause)
                    .append(" tipoCompDoc.dtIstituz <= :filterDate AND tipoCompDoc.dtSoppres >= :filterDate ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idTipoStrutDoc != null) {
            query.setParameter("idTipoStrutDoc", longFromBigDecimal(idTipoStrutDoc));
        }
        if (filterValid) {
            Date now = Calendar.getInstance().getTime();
            query.setParameter("filterDate", now);
        }

        return query.getResultList();
    }

    public List<DecTipoCompDoc> getDecTipoCompDocList(Long idStrut, Date data, Long idTipoStrutDoc) {
        String queryStr = "SELECT u FROM DecTipoCompDoc u " + "WHERE u.decTipoStrutDoc.orgStrut.idStrut = :idStrut "
                + "AND u.decTipoStrutDoc.idTipoStrutDoc = :idTipoStrutDoc " + "AND u.dtSoppres > :data ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        query.setParameter("idTipoStrutDoc", idTipoStrutDoc);
        query.setParameter("data", data);
        return query.getResultList();
    }

    public List<DecTipoCompDoc> getDecTipoCompDocListByStrut(BigDecimal idStrut) {
        Query query = getEntityManager().createQuery("SELECT tipoCompDoc FROM DecTipoCompDoc tipoCompDoc "
                + "WHERE tipoCompDoc.decTipoStrutDoc.orgStrut.idStrut = :idStrut");

        query.setParameter("idStrut", longFromBigDecimal(idStrut));

        return query.getResultList();
    }

    public List<DecTipoCompDoc> getDecTipoCompDocList() {
        Query query = getEntityManager().createQuery("SELECT tipoCompDoc FROM DecTipoCompDoc tipoCompDoc ");
        return query.getResultList();
    }

    public List<DecTipoStrutUnitaDoc> getDecTipoStrutUnitaDocList(BigDecimal idTipoUnitaDoc, boolean filterValid) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT  tipoStrutUnitaDoc FROM DecTipoStrutUnitaDoc tipoStrutUnitaDoc ");
        String clause = " WHERE ";
        if (idTipoUnitaDoc != null) {
            queryStr.append(clause).append("tipoStrutUnitaDoc.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc ");
            clause = " AND ";
        }
        if (filterValid) {
            queryStr.append(clause).append(
                    " tipoStrutUnitaDoc.dtIstituz <= :filterDate AND tipoStrutUnitaDoc.dtSoppres >= :filterDate ");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idTipoUnitaDoc != null) {
            query.setParameter("idTipoUnitaDoc", longFromBigDecimal(idTipoUnitaDoc));
        }
        if (filterValid) {
            Date now = Calendar.getInstance().getTime();
            query.setParameter("filterDate", now);
        }
        return query.getResultList();
    }

    public DecTipoStrutUnitaDoc getDecTipoStrutUnitaDocByName(BigDecimal idStrut, String nmTipoUnitaDoc,
            String nmTipoStrutUnitaDoc) {
        String queryStr = "SELECT tipoStrutUnitaDoc FROM DecTipoStrutUnitaDoc tipoStrutUnitaDoc "
                + "WHERE tipoStrutUnitaDoc.nmTipoStrutUnitaDoc = :nmTipoStrutUnitaDoc "
                + "AND tipoStrutUnitaDoc.decTipoUnitaDoc.nmTipoUnitaDoc = :nmTipoUnitaDoc "
                + "AND tipoStrutUnitaDoc.decTipoUnitaDoc.orgStrut.idStrut = :idStrut ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("nmTipoStrutUnitaDoc", nmTipoStrutUnitaDoc);
        query.setParameter("nmTipoUnitaDoc", nmTipoUnitaDoc);
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        List<DecTipoStrutUnitaDoc> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public void deleteDecFormatoFileAmmessoList(List<BigDecimal> idFormatoFileAmmessoList) {
        Query q = getEntityManager().createQuery("DELETE FROM DecFormatoFileAmmesso u "
                + "WHERE u.idFormatoFileAmmesso IN (:idFormatoFileAmmessoList) ");
        q.setParameter("idFormatoFileAmmessoList", longListFrom(idFormatoFileAmmessoList));
        q.executeUpdate();
        getEntityManager().flush();
    }

    public boolean existsRelationsWithStrutDoc(long idTipoStrutDoc) {
        String queryStr = "SELECT COUNT(strutDoc) FROM AroStrutDoc strutDoc "
                + "WHERE strutDoc.decTipoStrutDoc.idTipoStrutDoc = :idTipoStrutDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTipoStrutDoc", idTipoStrutDoc);
        return (Long) query.getSingleResult() > 0;
    }

    public boolean checkManyRelationsAreEmptyForDecTipoCompDoc(long idTipoCompDoc) {
        String queryStr = "SELECT COUNT(compDoc) FROM AroCompDoc compDoc WHERE compDoc.decTipoCompDoc.idTipoCompDoc = :idTipoCompDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTipoCompDoc", idTipoCompDoc);
        return (Long) query.getSingleResult() > 0;
    }

    public List<DecTipoRapprAmmesso> retrieveDecTipoRapprAmmessoByIdTipoCompDoc(Long idTipoCompDoc) {
        return retrieveDecTipoRapprAmmesso(idTipoCompDoc, null);
    }

    public List<DecTipoRapprAmmesso> retrieveDecTipoRapprAmmessoByIdTipoRapprComp(Long idTipoRapprComp) {
        return retrieveDecTipoRapprAmmesso(null, idTipoRapprComp);
    }

    public List<DecTipoRapprAmmesso> retrieveDecTipoRapprAmmesso(Long idTipoCompDoc, Long idTipoRapprComp) {
        StringBuilder queryStr = new StringBuilder("SELECT u FROM DecTipoRapprAmmesso u WHERE ");
        if (idTipoCompDoc != null) {
            queryStr.append("u.decTipoCompDoc.idTipoCompDoc = :idTipoCompDoc ");
        }
        if (idTipoCompDoc != null && idTipoRapprComp != null) {
            queryStr.append(" AND ");
        }
        if (idTipoRapprComp != null) {
            queryStr.append("u.decTipoRapprComp.idTipoRapprComp = :idTipoRapprComp ");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idTipoCompDoc != null) {
            query.setParameter("idTipoCompDoc", idTipoCompDoc);
        }
        if (idTipoRapprComp != null) {
            query.setParameter("idTipoRapprComp", idTipoRapprComp);
        }
        List<DecTipoRapprAmmesso> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list;
    }

    public DecTipoRapprAmmesso getDecTipoRapprAmmessoByParentId(Long idTipoCompDoc, Long idTipoRapprComp) {
        List<DecTipoRapprAmmesso> list = retrieveDecTipoRapprAmmesso(idTipoCompDoc, idTipoRapprComp);
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public List<DecTipoRapprComp> getDecTipoRapprCompListByIdStrut(Long idStrut, Date data) {
        String queryStr = "SELECT u FROM DecTipoRapprComp u " + "WHERE u.orgStrut.idStrut = :idStrut "
                + "AND u.dtSoppres > :data ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        query.setParameter("data", data);
        return query.getResultList();
    }

    public DecFormatoFileAmmesso getDecFormatoFileAmmesso(BigDecimal idTipoCompDoc, BigDecimal idFormatoFileDoc) {
        String queryStr = "SELECT formatoFileAmmesso FROM DecFormatoFileAmmesso formatoFileAmmesso WHERE formatoFileAmmesso.decFormatoFileDoc.idFormatoFileDoc = :idFormatoFileDoc AND "
                + "formatoFileAmmesso.decTipoCompDoc.idTipoCompDoc=:idTipoCompDoc";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTipoCompDoc", longFromBigDecimal(idTipoCompDoc));
        query.setParameter("idFormatoFileDoc", longFromBigDecimal(idFormatoFileDoc));
        List<DecFormatoFileAmmesso> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}
