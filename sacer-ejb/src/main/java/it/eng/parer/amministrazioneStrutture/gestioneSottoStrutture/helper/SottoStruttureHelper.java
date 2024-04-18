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

package it.eng.parer.amministrazioneStrutture.gestioneSottoStrutture.helper;

import it.eng.parer.entity.OrgCampoValSubStrut;
import it.eng.parer.entity.OrgRegolaValSubStrut;
import it.eng.parer.entity.OrgSubStrut;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.utils.CostantiDB;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Helper di gestione delle sottostrutture
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
public class SottoStruttureHelper extends GenericHelper {

    public List<OrgSubStrut> getOrgSubStrut(String nmSubStrut, BigDecimal idStrut) {
        StringBuilder queryStr = new StringBuilder(
                "Select subStrut FROM OrgSubStrut subStrut WHERE subStrut.orgStrut.idStrut = :idStrut");
        if (nmSubStrut != null) {
            queryStr.append(" AND subStrut.nmSubStrut = :nmSubStrut");
        }
        queryStr.append(" ORDER BY subStrut.nmSubStrut");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        if (nmSubStrut != null) {
            query.setParameter("nmSubStrut", nmSubStrut);
        }
        return query.getResultList();
    }

    public int countUdInSubStrut(BigDecimal idSubStrut) {
        Query query = getEntityManager().createQuery(
                "SELECT count(ud) FROM AroUnitaDoc ud JOIN ud.orgSubStrut subStrut WHERE subStrut.idSubStrut = :idSubStrut");
        query.setParameter("idSubStrut", longFromBigDecimal(idSubStrut));
        Long count = (Long) query.getSingleResult();
        return count.intValue();
    }

    public List<OrgRegolaValSubStrut> getOrgRegolaValSubStrut(BigDecimal idStrut, BigDecimal idSubStrut) {
        String queryStr = "SELECT regola FROM OrgCampoValSubStrut campo " + "JOIN campo.orgRegolaValSubStrut regola "
        // + "LEFT JOIN campo.orgRegolaValSubStrut regola "
                + "JOIN regola.decTipoUnitaDoc tipoUnitaDoc "
                // + "WHERE campo.orgSubStrut.idSubStrut = :idSubStrut "
                + "WHERE tipoUnitaDoc.orgStrut.idStrut = :idStrut " + "AND campo.orgSubStrut.idSubStrut = :idSubStrut "
                + "ORDER BY tipoUnitaDoc.nmTipoUnitaDoc ";

        Query query = this.getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        query.setParameter("idSubStrut", longFromBigDecimal(idSubStrut));
        return query.getResultList();
    }

    public List<OrgRegolaValSubStrut> getOrgRegolaSubStrut(BigDecimal id, Constants.TipoDato tipoDato,
            boolean filterValid) {
        String tmpTipoDato = null;
        String tmpNmTipoDato = null;
        switch (tipoDato) {
        case TIPO_UNITA_DOC:
            tmpTipoDato = "regola.decTipoUnitaDoc.idTipoUnitaDoc";
            tmpNmTipoDato = "regola.decTipoDoc.nmTipoDoc";
            break;
        case TIPO_DOC:
            tmpTipoDato = "regola.decTipoDoc.idTipoDoc";
            tmpNmTipoDato = "regola.decTipoUnitaDoc.nmTipoUnitaDoc";
            break;
        default:
            break;
        }

        String queryStr = String.format("SELECT regola FROM OrgRegolaValSubStrut regola " + "WHERE %s = :id ",
                tmpTipoDato);
        if (filterValid) {
            queryStr += " AND regola.dtIstituz <= :filterDate AND regola.dtSoppres >= :filterDate ";
        }
        queryStr += " ORDER BY " + tmpNmTipoDato;

        Query query = this.getEntityManager().createQuery(queryStr);
        query.setParameter("id", longFromBigDecimal(id));
        if (filterValid) {
            Date now = Calendar.getInstance().getTime();
            query.setParameter("filterDate", now);
        }
        return query.getResultList();
    }

    public List<OrgCampoValSubStrut> getOrgCampoValSubStrut(BigDecimal idRegolaValSubStrut) {
        Query query = getEntityManager().createQuery(
                "SELECT campo FROM OrgCampoValSubStrut campo WHERE campo.orgRegolaValSubStrut.idRegolaValSubStrut = :idRegolaValSubStrut");
        query.setParameter("idRegolaValSubStrut", longFromBigDecimal(idRegolaValSubStrut));

        return query.getResultList();
    }

    public boolean existOrgRegolaSubStrut(BigDecimal idRegolaValSubStrut, BigDecimal idTipoUnitaDoc,
            BigDecimal idTipoDoc, Date dtIstituz, Date dtSoppres) {
        StringBuilder queryStr = new StringBuilder("SELECT count(regola.idRegolaValSubStrut) "
                + "FROM OrgRegolaValSubStrut regola " + "WHERE regola.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc "
                + "AND regola.decTipoDoc.idTipoDoc = :idTipoDoc "
                + "AND ((regola.dtIstituz between :dtIstituz AND :dtSoppres) "
                + "OR (regola.dtSoppres between :dtIstituz AND :dtSoppres))");
        if (idRegolaValSubStrut != null) {
            queryStr.append(" AND ").append(" regola.idRegolaValSubStrut != :idRegolaValSubStrut");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idRegolaValSubStrut != null) {
            query.setParameter("idRegolaValSubStrut", longFromBigDecimal(idRegolaValSubStrut));
        }
        query.setParameter("idTipoUnitaDoc", longFromBigDecimal(idTipoUnitaDoc));
        query.setParameter("idTipoDoc", longFromBigDecimal(idTipoDoc));
        query.setParameter("dtIstituz", dtIstituz);
        query.setParameter("dtSoppres", dtSoppres);
        Long count = (Long) query.getSingleResult();
        return count.intValue() > 0;
    }

    public boolean existOrgRegolaSubStrut(BigDecimal idStrut, String nmTipoUnitaDoc, String nmTipoDoc) {
        Query query = getEntityManager().createQuery("SELECT COUNT(regola) FROM OrgRegolaValSubStrut regola "
                + "WHERE regola.decTipoUnitaDoc.nmTipoUnitaDoc = :nmTipoUnitaDoc "
                + "AND regola.decTipoUnitaDoc.orgStrut.idStrut = :idStrut "
                + "AND regola.decTipoDoc.nmTipoDoc = :nmTipoDoc "
                + "AND regola.decTipoDoc.orgStrut.idStrut = :idStrut ");
        query.setParameter("nmTipoUnitaDoc", nmTipoUnitaDoc);
        query.setParameter("nmTipoDoc", nmTipoDoc);
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        return (long) query.getSingleResult() > 0;
    }

    public OrgRegolaValSubStrut getOrgRegolaSubStrut(Long idStrut, String nmTipoUnitaDoc, String nmTipoDoc,
            Date dtIstituz, Date dtSoppres) {
        Query query = getEntityManager().createQuery("SELECT regola FROM OrgRegolaValSubStrut regola "
                + "JOIN regola.decTipoUnitaDoc tipoUd JOIN regola.decTipoDoc tipoDoc "
                + "WHERE tipoUd.nmTipoUnitaDoc = :nmTipoUnitaDoc " + "AND tipoUd.orgStrut.idStrut = :idStrut "
                + "AND tipoDoc.nmTipoDoc = :nmTipoDoc " + "AND tipoDoc.orgStrut.idStrut = :idStrut "
                + "AND regola.dtIstituz = :dtIstituz AND regola.dtSoppres = :dtSoppres");
        query.setParameter("nmTipoUnitaDoc", nmTipoUnitaDoc);
        query.setParameter("nmTipoDoc", nmTipoDoc);
        query.setParameter("dtIstituz", dtIstituz);
        query.setParameter("dtSoppres", dtSoppres);
        query.setParameter("idStrut", idStrut);
        List<OrgRegolaValSubStrut> lista = query.getResultList();
        if (lista != null && (!lista.isEmpty())) {
            return lista.get(0);
        } else {
            return null;
        }
    }

    public boolean existOrgCampoSubStrut(BigDecimal idCampoValSubStrut, BigDecimal idRegolaValSubStrut, String tiCampo,
            String nmCampo, BigDecimal idRecord) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT count(campi.idCampoValSubStrut) " + "FROM OrgCampoValSubStrut campi "
                        + "WHERE campi.tiCampo = :tiCampo " + "AND campi.nmCampo = :nmCampo "
                        + "AND campi.orgRegolaValSubStrut.idRegolaValSubStrut = :idRegolaValSubStrut ");
        CostantiDB.TipoCampo campo = CostantiDB.TipoCampo.valueOf(tiCampo);
        switch (campo) {
        case DATO_PROFILO:
            break;
        case DATO_SPEC_DOC_PRINC:
        case DATO_SPEC_UNI_DOC:
            queryStr.append(" AND ").append("campi.decAttribDatiSpec.idAttribDatiSpec = :idAttribDatiSpec");
            break;
        case SUB_STRUT:
            queryStr.append(" AND ").append("campi.orgSubStrut.idSubStrut = :idSubStrut");
            break;
        }
        if (idCampoValSubStrut != null) {
            queryStr.append(" AND ").append(" campi.idCampoValSubStrut != :idCampoValSubStrut");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idCampoValSubStrut != null) {
            query.setParameter("idCampoValSubStrut", longFromBigDecimal(idCampoValSubStrut));
        }
        query.setParameter("idRegolaValSubStrut", longFromBigDecimal(idRegolaValSubStrut));
        query.setParameter("tiCampo", tiCampo);
        query.setParameter("nmCampo", nmCampo);
        switch (campo) {
        case DATO_PROFILO:
            break;
        case DATO_SPEC_DOC_PRINC:
        case DATO_SPEC_UNI_DOC:
            query.setParameter("idAttribDatiSpec", longFromBigDecimal(idRecord));
            break;
        case SUB_STRUT:
            query.setParameter("idSubStrut", longFromBigDecimal(idRecord));
            break;
        }

        Long count = (Long) query.getSingleResult();
        return count.intValue() > 0;
    }

    public void deleteRegole(BigDecimal idTipoDoc) {
        String queryStr = "DELETE FROM OrgRegolaValSubStrut u WHERE u.decTipoDoc.idTipoDoc = :idTipoDoc";
        Query q = getEntityManager().createQuery(queryStr);
        q.setParameter("idTipoDoc", longFromBigDecimal(idTipoDoc));
        q.executeUpdate();
        getEntityManager().flush();
    }

    public List<OrgSubStrut> retrieveOrgSubStrutListAbilitate(long idUtente, BigDecimal idStruttura) {
        String queryStr = "SELECT DISTINCT u FROM OrgSubStrut u, IamAbilTipoDato iatd "
                + "WHERE iatd.nmClasseTipoDato = 'SUB_STRUTTURA' "
                + "AND iatd.iamAbilOrganiz.iamUser.idUserIam = :idUtente "
                + "and iatd.iamAbilOrganiz.idOrganizApplic = :idstrut " + "AND u.idSubStrut = iatd.idTipoDatoApplic "
                + "ORDER BY u.nmSubStrut ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUtente", idUtente);
        query.setParameter("idstrut", idStruttura);
        return query.getResultList();
    }
}
