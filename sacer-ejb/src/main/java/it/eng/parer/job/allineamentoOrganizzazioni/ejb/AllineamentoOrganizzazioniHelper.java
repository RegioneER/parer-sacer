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

package it.eng.parer.job.allineamentoOrganizzazioni.ejb;

import static it.eng.parer.util.Utils.longFromBigDecimal;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoFascicolo;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.IamOrganizDaReplic;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.OrgSubStrut;
import it.eng.parer.job.allineamentoOrganizzazioni.utils.CostantiReplicaOrg;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings({ "unchecked" })
@Stateless(mappedName = "AllineamentoOrganizzazioniHelper")
@LocalBean
public class AllineamentoOrganizzazioniHelper {
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;
    @EJB
    private AllineamentoOrganizzazioniHelper me;

    public List<IamOrganizDaReplic> getIamOrganizDaReplic() {
        List<IamOrganizDaReplic> organizList;
        String queryStr = "SELECT organiz FROM IamOrganizDaReplic organiz " + "WHERE organiz.tiStatoReplic "
                + "IN ('DA_REPLICARE', 'REPLICA_IN_TIMEOUT', 'REPLICA_IN_ERRORE') "
                + "ORDER BY organiz.dtLogOrganizDaReplic ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        organizList = query.getResultList();
        return organizList;
    }

    public OrgAmbiente getOrgAmbiente(BigDecimal idAmbiente) {
        return entityManager.find(OrgAmbiente.class, idAmbiente.longValue());
    }

    public OrgEnte getOrgEnte(BigDecimal idEnte) {
        return entityManager.find(OrgEnte.class, idEnte.longValue());
    }

    public OrgStrut getOrgStrut(BigDecimal idStrut) {
        return entityManager.find(OrgStrut.class, idStrut.longValue());
    }

    public List<DecTipoUnitaDoc> getDecTipoUnitaDocList(List<Long> idStruts) {
        String queryStr = "SELECT u FROM DecTipoUnitaDoc u " + "WHERE u.orgStrut.idStrut IN (:idStruts) ";
        Query q = entityManager.createQuery(queryStr);
        q.setParameter("idStruts", idStruts);
        return q.getResultList();
    }

    public List<DecTipoDoc> getDecTipoDocPrincipaliList(List<Long> idStruts) {
        String queryStr = "SELECT u FROM DecTipoDoc u " + "WHERE u.orgStrut.idStrut IN (:idStruts) "
                + "AND u.flTipoDocPrincipale = '1' ";
        Query q = entityManager.createQuery(queryStr);
        q.setParameter("idStruts", idStruts);
        return q.getResultList();
    }

    public List<DecTipoDoc> getDecTipoDocList(List<Long> idStruts) {
        String queryStr = "SELECT u FROM DecTipoDoc u " + "WHERE u.orgStrut.idStrut IN (:idStruts) ";
        Query q = entityManager.createQuery(queryStr);
        q.setParameter("idStruts", idStruts);
        return q.getResultList();
    }

    public List<DecRegistroUnitaDoc> getDecRegistroUnitaDocList(List<Long> idStruts) {
        String queryStr = "SELECT u FROM DecRegistroUnitaDoc u " + "WHERE u.orgStrut.idStrut IN (:idStruts) ";
        Query q = entityManager.createQuery(queryStr);
        q.setParameter("idStruts", idStruts);
        return q.getResultList();
    }

    public List<OrgSubStrut> getOrgSubStrutList(List<Long> idStruts) {
        String queryStr = "SELECT u FROM OrgSubStrut u " + "WHERE u.orgStrut.idStrut IN (:idStruts) ";
        Query q = entityManager.createQuery(queryStr);
        q.setParameter("idStruts", idStruts);
        return q.getResultList();
    }

    public List<DecTipoFascicolo> getDecTipoFascicoloList(List<Long> idStruts) {
        String queryStr = "SELECT u FROM DecTipoFascicolo u " + "WHERE u.orgStrut.idStrut IN (:idStruts) ";
        Query q = entityManager.createQuery(queryStr);
        q.setParameter("idStruts", idStruts);
        return q.getResultList();
    }

    public Map<String, Object> getEnteConvenzInfo(BigDecimal idStrut) {
        String queryStr = "SELECT strut.idEnteConvenz, strut.dtIniVal, strut.dtFineVal FROM OrgStrut strut "
                + "WHERE strut.idStrut = :idStrut ";
        Query q = entityManager.createQuery(queryStr);
        q.setParameter("idStrut", longFromBigDecimal(idStrut));
        List<Object[]> strutObjList = q.getResultList();
        Map<String, Object> mappa = new HashMap<>();
        if (!strutObjList.isEmpty()) {
            Object[] strutObj = strutObjList.get(0);
            mappa.put("idEnteConvenz", strutObj[0]);
            mappa.put("dtIniVal", strutObj[1]);
            mappa.put("dtFineVal", strutObj[2]);
        }
        return mappa;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void writeEsitoIamOrganizDaReplic(Long idOrganizDaReplic, CostantiReplicaOrg.EsitoServizio esitoServizio,
            String cdErr, String dsErr) {
        IamOrganizDaReplic organizDaReplic = entityManager.find(IamOrganizDaReplic.class, idOrganizDaReplic);
        if (esitoServizio == CostantiReplicaOrg.EsitoServizio.OK) {
            organizDaReplic.setTiStatoReplic(CostantiReplicaOrg.TiStatoReplic.REPLICA_OK.name());
            organizDaReplic.setCdErr(null);
            organizDaReplic.setDsMsgErr(null);
            organizDaReplic.setDtErr(null);
        } else if (esitoServizio == CostantiReplicaOrg.EsitoServizio.KO) {
            switch (cdErr) {
            case CostantiReplicaOrg.SERVIZI_ORG_001:
            case CostantiReplicaOrg.SERVIZI_ORG_007:
            case CostantiReplicaOrg.SERVIZI_ORG_004:
            case CostantiReplicaOrg.SERVIZI_ORG_006:
                organizDaReplic.setTiStatoReplic(CostantiReplicaOrg.TiStatoReplic.REPLICA_IN_ERRORE.name());
                organizDaReplic.setCdErr(cdErr);
                organizDaReplic.setDsMsgErr(dsErr);
                organizDaReplic.setDtErr(new Date());
                break;
            case CostantiReplicaOrg.SERVIZI_ORG_002:
                organizDaReplic.setTiStatoReplic(CostantiReplicaOrg.TiStatoReplic.REPLICA_OK.name());
                organizDaReplic.setCdErr(null);
                organizDaReplic.setDsMsgErr(null);
                organizDaReplic.setDtErr(null);
                break;
            case CostantiReplicaOrg.SERVIZI_ORG_003:
            case CostantiReplicaOrg.SERVIZI_ORG_005:
                organizDaReplic.setTiStatoReplic(CostantiReplicaOrg.TiStatoReplic.REPLICA_NON_POSSIBILE.name());
                organizDaReplic.setCdErr(cdErr);
                organizDaReplic.setDsMsgErr(dsErr);
                organizDaReplic.setDtErr(new Date());
                break;
            default:
                break;
            }
        } else if (esitoServizio == CostantiReplicaOrg.EsitoServizio.NO_RISPOSTA) {
            organizDaReplic.setTiStatoReplic(CostantiReplicaOrg.TiStatoReplic.REPLICA_IN_TIMEOUT.name());
            organizDaReplic.setCdErr(cdErr);
            organizDaReplic.setDsMsgErr(dsErr);
            organizDaReplic.setDtErr(new Date());
        }
    }
}
