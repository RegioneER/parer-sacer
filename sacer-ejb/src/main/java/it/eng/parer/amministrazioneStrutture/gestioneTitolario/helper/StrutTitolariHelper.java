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

package it.eng.parer.amministrazioneStrutture.gestioneTitolario.helper;

import static it.eng.parer.util.Utils.longFromBigDecimal;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import it.eng.parer.entity.DecLivelloTitol;
import it.eng.parer.entity.DecTitol;
import it.eng.parer.entity.DecValVoceTitol;
import it.eng.parer.entity.DecVoceTitol;
import it.eng.parer.entity.OrgOperTitol;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.viewEntity.AroVRicUnitaDoc;
import it.eng.parer.viewEntity.DecVLisValVoceTitol;
import it.eng.parer.viewEntity.DecVTreeTitol;

/**
 *
 * @author Bonora_L
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class StrutTitolariHelper extends GenericHelper {

    public boolean existChiaveUd(BigDecimal idStrut, String cdRegistroKeyUnitaDoc,
            BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc, Date dtDocInvio) {
        Query query = getEntityManager().createQuery("select count(ud) " + "from AroUnitaDoc ud "
                + "where ud.orgStrut.idStrut = :idStrutIn "
                + "and ud.cdKeyUnitaDoc = :cdKeyUnitaDocIn "
                + "and ud.aaKeyUnitaDoc = :aaKeyUnitaDocIn "
                + "and ud.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDocIn "
                + "and ud.dtAnnul > :data");
        query.setParameter("idStrutIn", longFromBigDecimal(idStrut));
        query.setParameter("cdKeyUnitaDocIn", cdKeyUnitaDoc);
        query.setParameter("aaKeyUnitaDocIn", aaKeyUnitaDoc);
        query.setParameter("cdRegistroKeyUnitaDocIn", cdRegistroKeyUnitaDoc);
        query.setParameter("data", dtDocInvio);
        Long result = (Long) query.getSingleResult();
        return result > 0;
    }

    public List<DecVTreeTitol> getVociTree(BigDecimal idTitol, Date dtVal) {
        Query query = getEntityManager()
                .createQuery("SELECT tree FROM DecVTreeTitol tree WHERE tree.idTitol = :idTitol "
                        + "AND tree.dtIstituz <= :dtVal and tree.dtSoppres >= :dtVal "
                        + "AND tree.dtIniVal <= :dtVal and tree.dtFinVal >= :dtVal ");
        query.setParameter("idTitol", idTitol);
        query.setParameter("dtVal", dtVal);
        return query.getResultList();
    }

    /**
     * Restituisce tutti i nodi padri a partire dal nodo corrente fino a quello root
     *
     * @param idNodo id del titolo corrente
     * @param dtVal  dato che ogni voce del titolario ha un periodo di validità, tale data definisce
     *               quando fare la ricerca
     *
     * @return lista oggetti di tipo {@link DecVTreeTitol}
     */
    public List<DecVTreeTitol> getVociAllPadri(BigDecimal idNodo, Date dtVal) {
        String queryStr = "WITH Q_PADRI(DL_NOTE, NI_ANNI_CONSERV, FL_USO_CLASSIF, DT_FIN_VAL, DT_INI_VAL, DS_VOCE_TITOL, ID_VAL_VOCE_TITOL, NI_FASCIC_VOCI_FIGLIE, NI_FASCIC, CD_COMPOSITO_VOCE_PADRE, ID_VOCE_TITOL_PADRE, DT_SOPPRES, DT_ISTITUZ, NI_ORD_VOCE_TITOL, NI_LIVELLO, NM_LIVELLO_TITOL, ID_LIVELLO_TITOL, CD_VOCE_TITOL, CD_COMPOSITO_VOCE_TITOL, ID_TITOL, ID_VOCE_TITOL) AS ("
                + "SELECT DL_NOTE, NI_ANNI_CONSERV, FL_USO_CLASSIF, DT_FIN_VAL, DT_INI_VAL, DS_VOCE_TITOL, ID_VAL_VOCE_TITOL, NI_FASCIC_VOCI_FIGLIE, NI_FASCIC, CD_COMPOSITO_VOCE_PADRE, ID_VOCE_TITOL_PADRE, DT_SOPPRES, DT_ISTITUZ, NI_ORD_VOCE_TITOL, NI_LIVELLO, NM_LIVELLO_TITOL, ID_LIVELLO_TITOL, CD_VOCE_TITOL, CD_COMPOSITO_VOCE_TITOL, ID_TITOL, ID_VOCE_TITOL "
                + "FROM DEC_V_TREE_TITOL nodo " + "WHERE nodo.ID_VOCE_TITOL = ? "
                + "AND nodo.DT_ISTITUZ <= TO_DATE(?, 'dd-mm-yyyy') "
                + "AND nodo.DT_SOPPRES >= TO_DATE(?, 'dd-mm-yyyy') "
                + "AND nodo.DT_INI_VAL <= TO_DATE(?, 'dd-mm-yyyy') "
                + "AND nodo.DT_FIN_VAL >= TO_DATE(?, 'dd-mm-yyyy') " + "UNION ALL "
                + "SELECT padre.DL_NOTE,padre.NI_ANNI_CONSERV,padre.FL_USO_CLASSIF,padre.DT_FIN_VAL,padre.DT_INI_VAL,padre.DS_VOCE_TITOL,padre.ID_VAL_VOCE_TITOL,padre.NI_FASCIC_VOCI_FIGLIE,padre.NI_FASCIC,padre.CD_COMPOSITO_VOCE_PADRE,padre.ID_VOCE_TITOL_PADRE,padre.DT_SOPPRES,padre.DT_ISTITUZ,padre.NI_ORD_VOCE_TITOL,padre.NI_LIVELLO,padre.NM_LIVELLO_TITOL,padre.ID_LIVELLO_TITOL,padre.CD_VOCE_TITOL,padre.CD_COMPOSITO_VOCE_TITOL,padre.ID_TITOL,padre.ID_VOCE_TITOL "
                + "FROM DEC_V_TREE_TITOL padre, Q_PADRI tt "
                + "WHERE padre.ID_VOCE_TITOL=tt.ID_VOCE_TITOL_PADRE "
                + "AND padre.DT_ISTITUZ <= TO_DATE(?, 'dd-mm-yyyy') "
                + "AND padre.DT_SOPPRES >= TO_DATE(?, 'dd-mm-yyyy') "
                + "AND padre.DT_INI_VAL <= TO_DATE(?, 'dd-mm-yyyy') "
                + "AND padre.DT_FIN_VAL >= TO_DATE(?, 'dd-mm-yyyy') " + ")"
                + "SELECT * FROM Q_PADRI tt ORDER BY tt.CD_COMPOSITO_VOCE_TITOL";

        Query query = getEntityManager().createNativeQuery(queryStr, DecVTreeTitol.class);
        String val = new SimpleDateFormat("dd-MM-yyyy").format(dtVal);

        query.setParameter(1, idNodo.longValue());
        query.setParameter(2, val);
        query.setParameter(3, val);
        query.setParameter(4, val);
        query.setParameter(5, val);
        query.setParameter(6, val);
        query.setParameter(7, val);
        query.setParameter(8, val);
        query.setParameter(9, val);

        return query.getResultList();
    }

    public OrgOperTitol getOperTitol(BigDecimal idTitol, Date dtVal) {
        String dtValString = (dtVal != null ? "AND o.dtValOperTitol <= :dtVal " : "");
        Query query = getEntityManager().createQuery(
                "SELECT o FROM OrgOperTitol o " + "WHERE o.decTitol.idTitol = :idTitol "
                        + dtValString + "ORDER BY o.dtValOperTitol DESC");
        query.setParameter("idTitol", longFromBigDecimal(idTitol));
        if (StringUtils.isNotBlank(dtValString)) {
            query.setParameter("dtVal", dtVal);
        }
        List<OrgOperTitol> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public List<DecVLisValVoceTitol> getTracciaList(BigDecimal idVoceTitol, Date dtVal) {
        String dateConcat;
        if (dtVal != null) {
            dateConcat = " AND d.dtIniVal <= :dtVal ";
        } else {
            dateConcat = "";
        }
        Query query = getEntityManager().createQuery(
                "SELECT d FROM DecVLisValVoceTitol d WHERE d.idVoceTitol = :idVoceTitol"
                        + dateConcat + " ORDER BY d.dtIniVal DESC");
        query.setParameter("idVoceTitol", idVoceTitol);
        if (dtVal != null) {
            query.setParameter("dtVal", dtVal);
        }
        return query.getResultList();
    }

    public List<DecLivelloTitol> getLivelliList(BigDecimal idTitol,
            Set<BigDecimal> niLivelloToExclude) {
        String toExclude = (niLivelloToExclude != null)
                ? " AND l.niLivello NOT IN (:niLivelloToExclude) "
                : "";

        Query query = getEntityManager().createQuery("SELECT l FROM DecLivelloTitol l "
                + "WHERE l.decTitol.idTitol = :idTitol " + toExclude + "ORDER BY l.niLivello");
        query.setParameter("idTitol", longFromBigDecimal(idTitol));
        if (StringUtils.isNotBlank(toExclude)) {
            query.setParameter("niLivelloToExclude", niLivelloToExclude);
        }
        return query.getResultList();
    }

    public DecLivelloTitol getLivello(BigDecimal niLivello, BigDecimal idTitol) {
        Query query = getEntityManager().createQuery("SELECT l FROM DecLivelloTitol l "
                + "WHERE l.decTitol.idTitol = :idTitol " + "AND l.niLivello = :niLivello");
        query.setParameter("idTitol", longFromBigDecimal(idTitol));
        query.setParameter("niLivello", niLivello);

        DecLivelloTitol livello = null;
        if (!query.getResultList().isEmpty()) {
            livello = (DecLivelloTitol) query.getResultList().get(0);
        }
        return livello;
    }

    public DecVoceTitol getVoce(Long idTitol, String cdVoceComposito) {
        Query query = getEntityManager().createQuery(
                "SELECT v FROM DecVoceTitol v " + "WHERE v.decTitol.idTitol = :idTitol "
                        + "AND v.cdCompositoVoceTitol = :cdVoceComposito");
        query.setParameter("idTitol", idTitol);
        query.setParameter("cdVoceComposito", StringEscapeUtils.escapeJava(cdVoceComposito));
        return (DecVoceTitol) query.getSingleResult();
    }

    public DecValVoceTitol getLastDecValVoceTitol(long idVoceTitol) {
        Query query = getEntityManager().createQuery("SELECT v FROM DecValVoceTitol v "
                + "WHERE v.decVoceTitol.idVoceTitol = :idVoceTitol " + "ORDER BY v.dtIniVal DESC");
        query.setParameter("idVoceTitol", idVoceTitol);
        List<DecValVoceTitol> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public DecVoceTitol getDecVoceTitol(BigDecimal idVoceTitol) {
        Query query = getEntityManager().createQuery(
                "SELECT v FROM DecVoceTitol v " + "WHERE v.idVoceTitol = :idVoceTitol ");
        query.setParameter("idVoceTitol", longFromBigDecimal(idVoceTitol));
        List<DecVoceTitol> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public AroVRicUnitaDoc getUnitaDoc(String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc,
            String cdKeyUnitaDoc, BigDecimal idStrut) {
        Query query = getEntityManager().createQuery(
                "SELECT a FROM AroVRicUnitaDoc a WHERE a.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc AND a.aaKeyUnitaDoc = :aaKeyUnitaDoc AND a.cdKeyUnitaDoc = :cdKeyUnitaDoc AND a.idStrutUnitaDoc = :idStrut");
        query.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
        query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        query.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc);
        query.setParameter("idStrut", idStrut);
        List<AroVRicUnitaDoc> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public boolean existTitolario(BigDecimal idStrut, String nmTitol, BigDecimal idTitol) {
        StringBuilder builder = new StringBuilder(
                "SELECT count(titol) FROM DecTitol titol WHERE titol.orgStrut.idStrut = :idStrut AND titol.nmTitol = :nmTitol");
        if (idTitol != null) {
            builder.append(" AND titol.idTitol != :idTitol");
        }
        Query query = getEntityManager().createQuery(builder.toString());
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        query.setParameter("nmTitol", nmTitol);
        if (idTitol != null) {
            query.setParameter("idTitol", longFromBigDecimal(idTitol));
        }
        Long result = (Long) query.getSingleResult();
        return result > 0;
    }

    public boolean existTitolario(BigDecimal idStrut, Date dtIniVal, Date dtFinVal) {
        Query query = getEntityManager().createQuery(
                "SELECT count(titol) FROM DecTitol titol WHERE titol.orgStrut.idStrut = :idStrut AND ((titol.dtIstituz <= :dtFinVal) AND (titol.dtSoppres >= :dtIniVal))");
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        query.setParameter("dtIniVal", dtIniVal);
        query.setParameter("dtFinVal", dtFinVal);
        Long result = (Long) query.getSingleResult();
        return result > 0;
    }

    public boolean existVoce(String cdVoceComposito, Long idTitol) {
        Query query = getEntityManager().createQuery(
                "SELECT count(v) FROM DecVoceTitol v " + "WHERE v.decTitol.idTitol = :idTitol "
                        + "AND v.cdCompositoVoceTitol = :cdVoceComposito");
        query.setParameter("idTitol", idTitol);
        query.setParameter("cdVoceComposito", StringEscapeUtils.escapeJava(cdVoceComposito));
        Long result = (Long) query.getSingleResult();
        return result > 0;
    }

    public List<DecTitol> getDecTitol(BigDecimal idStrut, boolean filterValid) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT titol FROM DecTitol titol WHERE titol.orgStrut.idStrut = :idStrut ");
        String whereClause = " AND ";
        if (filterValid) {
            queryStr.append(whereClause)
                    .append(" titol.dtIstituz <= :filterDate AND titol.dtSoppres >= :filterDate ");
        }
        queryStr.append("ORDER BY titol.dtIstituz DESC");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        if (filterValid) {
            Date now = Calendar.getInstance().getTime();
            query.setParameter("filterDate", now);
        }
        return query.getResultList();
    }

    public List<DecVoceTitol> getDecVoceTitols(BigDecimal idCriterioRaggrFasc, String tiSel,
            boolean filterValid) {

        String whereClause = " and ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT voceTitol FROM DecSelCriterioRaggrFasc scrf, DecVoceTitol voceTitol "
                        + "WHERE scrf.decCriterioRaggrFasc.idCriterioRaggrFasc = :idcrit "
                        + "AND scrf.decVoceTitol.idVoceTitol = voceTitol.idVoceTitol ");
        if (tiSel != null) {
            queryStr.append(whereClause).append("scrf.tiSel = :filtro ");
        }
        if (filterValid) {
            queryStr.append(whereClause).append(
                    "voceTitol.dtIstituz <= :filterDate AND voceTitol.dtSoppres >= :filterDate ");
        }
        queryStr.append("ORDER BY voceTitol.dtIstituz DESC");

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idcrit", longFromBigDecimal(idCriterioRaggrFasc));
        if (tiSel != null) {
            query.setParameter("filtro", tiSel);
        }
        if (filterValid) {
            Date now = Calendar.getInstance().getTime();
            query.setParameter("filterDate", now);
        }

        return query.getResultList();

    }

}
