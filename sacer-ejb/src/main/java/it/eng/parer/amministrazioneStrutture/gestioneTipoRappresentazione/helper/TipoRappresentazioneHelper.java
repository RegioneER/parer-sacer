package it.eng.parer.amministrazioneStrutture.gestioneTipoRappresentazione.helper;

import it.eng.parer.entity.DecImageTrasform;
import it.eng.parer.entity.DecTipoRapprComp;
import it.eng.parer.entity.DecTrasformTipoRappr;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.ws.dto.CSVersatore;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper delle tipologie di rappresentazione
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
public class TipoRappresentazioneHelper extends GenericHelper {

    private static final Logger logger = LoggerFactory.getLogger(TipoRappresentazioneHelper.class);

    public DecTipoRapprComp getDecTipoRapprCompByName(String nmTipoRapprComp, BigDecimal idStrut) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT tipoRapprComp FROM DecTipoRapprComp tipoRapprComp WHERE tipoRapprComp.nmTipoRapprComp = :nmTipoRapprComp");

        if (idStrut != null) {
            queryStr.append(" AND tipoRapprComp.orgStrut.idStrut=:idStrut");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }

        query.setParameter("nmTipoRapprComp", nmTipoRapprComp);
        List<DecTipoRapprComp> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    public List<DecTipoRapprComp> retrieveDecRapprCompList(BigDecimal idStrut, boolean filterValid) {
        StringBuilder queryStr = new StringBuilder("SELECT tipoRapprComp FROM DecTipoRapprComp tipoRapprComp ");
        String clause = " WHERE ";
        if (idStrut != null) {
            queryStr.append(clause).append("tipoRapprComp.orgStrut.idStrut = :idStrut");
            clause = " AND ";
        }
        if (filterValid) {
            queryStr.append(clause)
                    .append(" tipoRapprComp.dtIstituz <= :filterDate AND tipoRapprComp.dtSoppres >= :filterDate ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }
        if (filterValid) {
            Date now = Calendar.getInstance().getTime();
            query.setParameter("filterDate", now);
        }

        List<DecTipoRapprComp> list = query.getResultList();

        return list;
    }

    public boolean checkRelationsAreEmptyForDecTipoRapprComp(long idTipoRapprComp) {
        boolean result;
        Query query = getEntityManager().createQuery("SELECT a FROM DecTipoRapprComp a "
                + "WHERE a.idTipoRapprComp = :idTipoRapprComp "
                + "AND NOT EXISTS (select p from AroCompDoc p where p.decTipoRapprComp.idTipoRapprComp=a.idTipoRapprComp) ");
        query.setParameter("idTipoRapprComp", idTipoRapprComp);
        List<Object[]> list = query.getResultList();
        result = list.isEmpty();
        return result;
    }

    public List<DecTrasformTipoRappr> retrieveDecTrasformTipoRapprList(Long idTipoRapprComp) {
        Query query = getEntityManager().createQuery(
                "Select t from DecTrasformTipoRappr t where t.decTipoRapprComp.idTipoRapprComp=:idTipoRapprComp");
        query.setParameter("idTipoRapprComp", idTipoRapprComp);
        List<DecTrasformTipoRappr> result = query.getResultList();
        return result;
    }

    public List<DecImageTrasform> retrieveDecImageTrasformList(BigDecimal idTrasformTipoRappr) {
        List<DecImageTrasform> result;
        String sQuery = "Select t from DecImageTrasform t where t.decTrasformTipoRappr.idTrasformTipoRappr=:idTrasformTipoRappr";
        Query query = getEntityManager().createQuery(sQuery);
        query.setParameter("idTrasformTipoRappr", idTrasformTipoRappr);
        result = query.getResultList();

        return result;
    }

    public CSVersatore getCSVersatoreForImageTrasform(BigDecimal idImageTrasform) {
        CSVersatore result = null;
        String sQuery = "Select strut.nmStrut, ente.nmEnte, amb.nmAmbiente from DecImageTrasform dit "
                + " join dit.decTrasformTipoRappr ttr" + " join ttr.decTipoRapprComp dtrc" + " join dtrc.orgStrut strut"
                + " join strut.orgEnte ente" + " join ente.orgAmbiente amb"
                + " where dit.idImageTrasform=:idImageTrasform";
        Query query = getEntityManager().createQuery(sQuery);
        query.setParameter("idImageTrasform", idImageTrasform);
        List<Object[]> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            result = new CSVersatore();
            Object[] datiV = list.get(0);

            result.setStruttura((String) datiV[0]);
            result.setEnte((String) datiV[1]);
            result.setAmbiente((String) datiV[2]);
        }
        return result;
    }

    public CSVersatore getCSVersatoreForTrasformTipoRappr(BigDecimal idTrasformTipoRappr) {
        CSVersatore result = null;
        String sQuery = "Select strut.nmStrut, ente.nmEnte, amb.nmAmbiente from DecTrasformTipoRappr ttr"
                + " join ttr.decTipoRapprComp dtrc" + " join dtrc.orgStrut strut" + " join strut.orgEnte ente"
                + " join ente.orgAmbiente amb" + " where ttr.idTrasformTipoRappr=:idTrasformTipoRappr";
        Query query = getEntityManager().createQuery(sQuery);
        query.setParameter("idTrasformTipoRappr", idTrasformTipoRappr);
        List<Object[]> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            result = new CSVersatore();
            Object[] datiV = list.get(0);

            result.setStruttura((String) datiV[0]);
            result.setEnte((String) datiV[1]);
            result.setAmbiente((String) datiV[2]);
        }
        return result;
    }

    public DecImageTrasform getDecImageTrasformByName(BigDecimal idTrasformTipoRappr, String nmImageTrasform) {
        DecImageTrasform result = null;
        String sQuery = "Select t from DecImageTrasform t where t.nmImageTrasform=:nmImageTrasform AND t.decTrasformTipoRappr.idTrasformTipoRappr = :idTrasformTipoRappr";
        Query query = getEntityManager().createQuery(sQuery);
        query.setParameter("nmImageTrasform", nmImageTrasform);
        query.setParameter("idTrasformTipoRappr", idTrasformTipoRappr);
        List<DecImageTrasform> lista = query.getResultList();
        if (lista != null && !lista.isEmpty()) {
            result = lista.get(0);
        }
        return result;
    }
}
