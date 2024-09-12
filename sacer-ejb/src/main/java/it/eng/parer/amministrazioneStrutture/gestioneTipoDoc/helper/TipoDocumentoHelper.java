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

package it.eng.parer.amministrazioneStrutture.gestioneTipoDoc.helper;

import static it.eng.parer.util.Utils.longFromBigDecimal;
import static it.eng.parer.util.Utils.longListFrom;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoStrutDoc;
import it.eng.parer.entity.DecTipoStrutDocAmmesso;
import it.eng.parer.entity.DecTipoStrutUdXsd;
import it.eng.parer.helper.GenericHelper;

/**
 * Helper delle tipologie di documento
 *
 * @author Bonora_L
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class TipoDocumentoHelper extends GenericHelper {

    /**
     * Ritorna l'oggetto DecTipoDoc dato il nome documento e la struttura di riferimento
     *
     * @param nmTipoDoc
     *            nome tipo documento
     * @param idStrut
     *            id struttura
     *
     * @return l'oggetto DecTipoDoc o null se inesistente
     */
    public DecTipoDoc getDecTipoDocByName(String nmTipoDoc, BigDecimal idStrut) {
        return getDecTipoDocByName(nmTipoDoc, idStrut.longValue());
    }

    /**
     * Ritorna l'oggetto DecTipoDoc dato il nome documento e la struttura di riferimento
     *
     * @param nmTipoDoc
     *            nome tipo documento
     * @param idStrut
     *            id struttura
     *
     * @return l'oggetto DecTipoDoc o null se inesistente
     */
    public DecTipoDoc getDecTipoDocByName(String nmTipoDoc, long idStrut) {
        Query query = getEntityManager().createQuery(
                "SELECT tipoDoc FROM DecTipoDoc tipoDoc WHERE UPPER(tipoDoc.nmTipoDoc) = :nmTipoDoc AND tipoDoc.orgStrut.idStrut = :idStrut");
        query.setParameter("nmTipoDoc", nmTipoDoc.toUpperCase());
        query.setParameter("idStrut", idStrut);
        List<DecTipoDoc> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * Recupera i tipi documento: tutti quelli non principali; solo quelli abilitati per l'utente se si tratta invece di
     * documenti principali
     *
     * @param idUtente
     *            id utente
     * @param idStruttura
     *            id struttura
     *
     * @return DecRegistroUnitaDocTableBean
     */
    public List<DecTipoDoc> getTipiDocAbilitati(long idUtente, BigDecimal idStruttura) {
        List<BigDecimal> idStrutList = new ArrayList<>();
        idStrutList.add(idStruttura);
        return getTipiDocAbilitatiDaStrutturaList(idUtente, idStrutList, false);
    }

    /**
     * Recupera i tipi documento: solo quelli abilitati per l'utente se si tratta invece di documenti principali
     *
     * @param idUtente
     *            id utente
     * @param idStruttura
     *            id struttura
     *
     * @return DecRegistroUnitaDocTableBean bean DecRegistroUnitaDoc
     */
    public List<DecTipoDoc> getTipiDocPrincipaliAbilitati(long idUtente, BigDecimal idStruttura) {
        List<BigDecimal> idStrutList = new ArrayList<>();
        idStrutList.add(idStruttura);
        return getTipiDocAbilitatiDaStrutturaList(idUtente, idStrutList, true);
    }

    /**
     * Restituisce il tablebean contenente la lista dei TIPI DOCUMENTO ABILITATI per l'utente corrente sulle strutture
     * selezionate
     *
     * @param idUtente
     *            id utente
     * @param idStrutturaList
     *            lista id struttura
     * @param docPrincipale
     *            documento principale true/false
     *
     * @return lista oggetti di tipo {@link DecTipoDoc}
     */
    public List<DecTipoDoc> getTipiDocAbilitatiDaStrutturaList(long idUtente, List<BigDecimal> idStrutturaList,
            boolean docPrincipale) {
        String queryStr = "SELECT u FROM DecTipoDoc u , IamAbilTipoDato iatd WHERE iatd.idTipoDatoApplic = u.idTipoDoc AND iatd.nmClasseTipoDato = 'TIPO_DOC' AND iatd.iamAbilOrganiz.iamUser.idUserIam = :idUtente AND u.orgStrut.idStrut IN (:idStrutturaList)"
                + (docPrincipale ? " AND u.flTipoDocPrincipale = '1'" : "");
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUtente", idUtente);
        query.setParameter("idStrutturaList", longListFrom(idStrutturaList));
        return query.getResultList();
    }

    public Long countDecTipoDocPrincipalePerTipoUnitaDoc(long idTipoUnitaDoc, long idTipoDoc) {
        Query query = getEntityManager().createQuery(
                "Select COUNT(dtda.decTipoDoc) from DecTipoDocAmmesso dtda WHERE dtda.decTipoDoc.flTipoDocPrincipale='1' AND dtda.decTipoStrutUnitaDoc.decTipoUnitaDoc.idTipoUnitaDoc=:idTipoUnitaDoc AND dtda.decTipoDoc.idTipoDoc = :idTipoDoc");
        query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        query.setParameter("idTipoDoc", idTipoDoc);
        return (Long) query.getSingleResult();
    }

    public List<DecTipoDoc> retrieveDecTipoDocList(BigDecimal idStrut, boolean flPrinc, Date dtSoppressione,
            boolean filterValid) {
        StringBuilder queryStr = new StringBuilder("SELECT tipoDoc FROM DecTipoDoc tipoDoc ");
        String whereClause = " WHERE ";
        if (idStrut != null) {
            queryStr.append(whereClause).append("tipoDoc.orgStrut.idStrut = :idStrut ");
            whereClause = " AND ";
        }
        if (flPrinc) {
            queryStr.append(whereClause).append("tipoDoc.flTipoDocPrincipale = '1' ");
            whereClause = " AND ";
        }
        if (dtSoppressione != null) {
            queryStr.append(whereClause).append("tipoDoc.dtSoppres > :dtSoppressione ");
            whereClause = " AND ";
        }
        if (filterValid) {
            queryStr.append(whereClause)
                    .append(" tipoDoc.dtIstituz <= :filterDate AND tipoDoc.dtSoppres >= :filterDate ");
        }

        queryStr.append("ORDER BY tipoDoc.nmTipoDoc ");
        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idStrut != null) {
            query.setParameter("idStrut", longFromBigDecimal(idStrut));
        }

        if (dtSoppressione != null) {
            query.setParameter("dtSoppressione", dtSoppressione);
        }

        if (filterValid) {
            Date now = Calendar.getInstance().getTime();
            query.setParameter("filterDate", now);
        }

        return query.getResultList();
    }

    public List<DecTipoStrutDocAmmesso> getDecTipoStrutDocAmmessoListByIdTipoDoc(Long idTipoDoc) {
        String queryStr = "SELECT u FROM DecTipoStrutDocAmmesso u " + "WHERE u.decTipoDoc.idTipoDoc = :idTipoDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTipoDoc", idTipoDoc);
        return query.getResultList();
    }

    public List<DecTipoStrutDocAmmesso> getDecTipoStrutDocAmmessoListByIdTipoStrutDoc(Long idTipoStrutDoc) {
        String queryStr = "SELECT u FROM DecTipoStrutDocAmmesso u "
                + "WHERE u.decTipoStrutDoc.idTipoStrutDoc = :idTipoStrutDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTipoStrutDoc", idTipoStrutDoc);
        return query.getResultList();
    }

    public List<DecTipoStrutDoc> getDecTipoStrutDocListByIdStrut(Long idStrut, Date data) {
        String queryStr = "SELECT u FROM DecTipoStrutDoc u " + "WHERE u.orgStrut.idStrut = :idStrut "
                + "AND u.dtSoppres > :data ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        query.setParameter("data", data);
        return query.getResultList();
    }

    public DecTipoStrutDocAmmesso getDecTipoStrutDocAmmessoByName(BigDecimal idStrutCorrente, String nmTipoStrutDoc,
            String nmTipoDoc) {
        String queryStr = "SELECT u FROM DecTipoStrutDocAmmesso u " + "WHERE u.decTipoDoc.nmTipoDoc = :nmTipoDoc "
                + "AND u.decTipoDoc.orgStrut.idStrut = :idStrutCorrente "
                + "AND u.decTipoStrutDoc.nmTipoStrutDoc = :nmTipoStrutDoc "
                + "AND u.decTipoStrutDoc.orgStrut.idStrut = :idStrutCorrente ";

        Query query = getEntityManager().createQuery(queryStr);

        query.setParameter("idStrutCorrente", longFromBigDecimal(idStrutCorrente));
        query.setParameter("nmTipoStrutDoc", nmTipoStrutDoc);
        query.setParameter("nmTipoDoc", nmTipoDoc);

        List<DecTipoStrutDocAmmesso> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    public DecTipoStrutDocAmmesso getDecTipoStrutDocAmmessoByParentId(Long idTipoDoc, Long idTipoStrutDoc) {

        String queryStr = "SELECT u FROM DecTipoStrutDocAmmesso u " + "WHERE u.decTipoDoc.idTipoDoc = :idTipoDoc "
                + "AND u.decTipoStrutDoc.idTipoStrutDoc = :idTipoStrutDoc ";

        Query query = getEntityManager().createQuery(queryStr);

        query.setParameter("idTipoDoc", idTipoDoc);
        query.setParameter("idTipoStrutDoc", idTipoStrutDoc);

        List<DecTipoStrutDocAmmesso> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    public DecTipoStrutUdXsd getDecTipoStrutUdXsdByName(BigDecimal idStrutCorrente, String nmTipoUnitaDoc,
            String nmTipoStrutUnitaDoc) {
        String queryStr = "SELECT u FROM DecTipoStrutUdXsd u "
                + "WHERE u.decTipoStrutUnitaDoc.nmTipoStrutUnitaDoc = :nmTipoStrutUnitaDoc "
                + "AND u.decTipoStrutUnitaDoc.decTipoUnitaDoc.orgStrut.idStrut = :idStrutCorrente "
                + "AND u.decTipoStrutUnitaDoc.decTipoUnitaDoc.nmTipoUnitaDoc = :nmTipoUnitaDoc ";

        Query query = getEntityManager().createQuery(queryStr);

        query.setParameter("idStrutCorrente", longFromBigDecimal(idStrutCorrente));
        query.setParameter("nmTipoUnitaDoc", nmTipoUnitaDoc);
        query.setParameter("nmTipoStrutUnitaDoc", nmTipoStrutUnitaDoc);

        List<DecTipoStrutUdXsd> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

}
