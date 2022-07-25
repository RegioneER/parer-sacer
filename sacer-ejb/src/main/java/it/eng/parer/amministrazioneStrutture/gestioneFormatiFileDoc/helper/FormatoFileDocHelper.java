package it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc.helper;

import it.eng.parer.entity.DecFormatoFileDoc;
import it.eng.parer.helper.GenericHelper;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper dei formati file doc per le strutture
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
public class FormatoFileDocHelper extends GenericHelper {

    private static final Logger logger = LoggerFactory.getLogger(FormatoFileDocHelper.class);

    /**
     * Recupera i formati file doc per la struttura, legati al formato file standard
     *
     * @param idStrut
     *            struttura di riferimento
     * @param filterValid
     *            true se devono essere recuperati solo quelli validi in data odierna
     * 
     * @return DecFormatoFileDocTableBean
     */
    public List<DecFormatoFileDoc> retrieveDecFormatoFileDocList(BigDecimal idStrut, boolean filterValid) {
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT formatoFileDoc FROM DecUsoFormatoFileStandard uso "
                + "JOIN uso.decFormatoFileDoc formatoFileDoc " + "JOIN formatoFileDoc.orgStrut strut ");
        String clause = " WHERE ";

        if (idStrut != null) {
            queryStr.append(clause).append("strut.idStrut = :idStrut ");
            clause = " AND ";
        }
        if (filterValid) {
            queryStr.append(clause)
                    .append("formatoFileDoc.dtIstituz <= :filterDate AND formatoFileDoc.dtSoppres >= :filterDate ");
        }

        queryStr.append("ORDER BY uso.niOrdUso ASC ");

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut.longValue());
        }
        if (filterValid) {
            Date now = Calendar.getInstance().getTime();
            query.setParameter("filterDate", now);
        }

        List<DecFormatoFileDoc> list = query.getResultList();

        return list;
    }

    /**
     * Recupera i formati file doc per la struttura
     *
     * @param idStrut
     *            struttura di riferimento
     * 
     * @return DecFormatoFileDocTableBean
     */
    public List<DecFormatoFileDoc> retrieveDecFormatoFileDocList(BigDecimal idStrut) {
        Query query = getEntityManager()
                .createQuery("SELECT u FROM DecFormatoFileDoc u WHERE u.orgStrut.idStrut = :idstrut");
        query.setParameter("idstrut", idStrut);
        List<DecFormatoFileDoc> listaFormati = query.getResultList();
        return listaFormati;
    }

    public DecFormatoFileDoc getDecFormatoFileDocByName(String nmFormatoFileDoc, BigDecimal idStrut) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT formatoFileDoc FROM DecFormatoFileDoc formatoFileDoc WHERE formatoFileDoc.nmFormatoFileDoc = :nmFormatoFileDoc");

        if (idStrut != null) {
            queryStr.append(" AND formatoFileDoc.orgStrut.idStrut=:idStrut");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }

        query.setParameter("nmFormatoFileDoc", nmFormatoFileDoc);
        List<DecFormatoFileDoc> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    // public DecFormatoFileDoc getDecFormatoFileDocAttivoByName(String nmFormatoFileDoc, BigDecimal idStrut) {
    // StringBuilder queryStr = new StringBuilder("SELECT formatoFileDoc FROM DecFormatoFileDoc formatoFileDoc "
    // + "WHERE formatoFileDoc.nmFormatoFileDoc = :nmFormatoFileDoc "
    // + "AND formatoFileDoc.dtSoppres < :dataOdierna ");
    //
    // if (idStrut != null) {
    // queryStr.append(" AND formatoFileDoc.orgStrut.idStrut=:idStrut");
    // }
    //
    // Query query = getEntityManager().createQuery(queryStr.toString());
    //
    // query.setParameter("dataOdierna", new Date());
    // if (idStrut != null) {
    // query.setParameter("idStrut", idStrut);
    // }
    //
    // query.setParameter("nmFormatoFileDoc", nmFormatoFileDoc);
    // List<DecFormatoFileDoc> list = query.getResultList();
    //
    // if (list.isEmpty()) {
    // return null;
    // }
    //
    // return list.get(0);
    // }

    public int removeUsoFormatoFileStandardByFormatoFDoc(BigDecimal idFormatoFileDoc) {
        String queryStr = "DELETE FROM DecUsoFormatoFileStandard uffs "
                + " WHERE  uffs.decFormatoFileDoc.idFormatoFileDoc = :idFormatoFileDoc";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idFormatoFileDoc", idFormatoFileDoc);
        return query.executeUpdate();
    }

    public BigDecimal getUsoFormatoFileStandardMaxNrOrder(BigDecimal idFormatoFileDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT MAX(usoFormatoFileStandard.niOrdUso) FROM DecUsoFormatoFileStandard usoFormatoFileStandard "
                        + "WHERE usoFormatoFileStandard.decFormatoFileDoc.idFormatoFileDoc = :idFormatoFileDoc");
        query.setParameter("idFormatoFileDoc", idFormatoFileDoc);

        BigDecimal nrOrd = (BigDecimal) query.getSingleResult();
        if (nrOrd == null) {
            nrOrd = BigDecimal.ZERO;
        }

        return nrOrd;
    }

    public List<Object[]> getDecFormatoFileAmmessoList(BigDecimal idTipoCompDoc) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT formatoFileAmmesso.idFormatoFileAmmesso, formatoFileDoc FROM DecFormatoFileAmmesso formatoFileAmmesso JOIN formatoFileAmmesso.decFormatoFileDoc formatoFileDoc ");

        if (idTipoCompDoc != null) {
            queryStr.append("WHERE formatoFileAmmesso.decTipoCompDoc.idTipoCompDoc = :idTipoCompDoc");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idTipoCompDoc != null) {
            query.setParameter("idTipoCompDoc", idTipoCompDoc);
        }
        return query.getResultList();
    }

    public List<DecFormatoFileDoc> getDecFormatoFileAmmessoNotInList(Set<String> formati, BigDecimal idStrut) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT formato FROM DecFormatoFileDoc formato WHERE formato.orgStrut.idStrut = :strut ");

        if (!formati.isEmpty()) {
            queryStr.append("AND UPPER(formato.nmFormatoFileDoc) NOT IN :nmformati");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("strut", idStrut);
        if (!formati.isEmpty()) {
            query.setParameter("nmformati", formati);
        }
        return query.getResultList();
    }

    /* Restituisce i formati file per la struttura e per i nomi dei formati passati */
    // FIXMEPLEASE: Metodo curioso dato che prende una lista a cui viene passato sempre un solo elemento
    public List<DecFormatoFileDoc> retrieveDecFormatoFileDocList(BigDecimal idStrut, List<String> formatoFileDocList) {
        if (!formatoFileDocList.isEmpty()) {
            Query q = getEntityManager().createQuery("SELECT u FROM DecFormatoFileDoc u "
                    + "WHERE u.nmFormatoFileDoc IN :formatoFileDocList " + "AND u.orgStrut.idStrut = :idStrut ");
            q.setParameter("formatoFileDocList", formatoFileDocList);
            q.setParameter("idStrut", idStrut);
            return q.getResultList();
        }
        return null;
    }

    public boolean checkRelationsAreEmptyForDecFormatoFileDoc(long idFormatoFileDoc) {
        boolean result = true;

        String queryStr = " select a from DecFormatoFileDoc a  " + " where a.idFormatoFileDoc=:idFormatoFileDoc "
                + " AND NOT EXISTS (select p from AroCompDoc p where p.decFormatoFileDoc.idFormatoFileDoc=a.idFormatoFileDoc) ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idFormatoFileDoc", idFormatoFileDoc);
        List<Object[]> list = query.getResultList();
        result = list.isEmpty();
        return result;
    }

    public boolean checkRelationsAreEmptyForDecFormatoFileDocCont(long idFormatoFileDoc) {
        boolean result = true;

        String queryStr = " select a from DecFormatoFileDoc a  " + " where a.idFormatoFileDoc=:idFormatoFileDoc "
                + " AND NOT EXISTS (select q from DecTipoRapprComp q where q.decFormatoFileDocCont.idFormatoFileDoc=a.idFormatoFileDoc) ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idFormatoFileDoc", idFormatoFileDoc);
        List<Object[]> list = query.getResultList();
        result = list.isEmpty();
        return result;
    }

    public boolean checkRelationsAreEmptyForDecFormatoFileDocConv(long idFormatoFileDoc) {
        boolean result = true;

        String queryStr = " select a from DecFormatoFileDoc a  " + " where a.idFormatoFileDoc=:idFormatoFileDoc "
                + " AND NOT EXISTS (select q from DecTipoRapprComp q where q.decFormatoFileDocConv.idFormatoFileDoc=a.idFormatoFileDoc) ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idFormatoFileDoc", idFormatoFileDoc);
        List<Object[]> list = query.getResultList();
        result = list.isEmpty();
        return result;
    }

    public void deleteDecFormatoFileDocList(BigDecimal idStrut, List<String> formatoFileDocList) {
        if (!formatoFileDocList.isEmpty()) {
            Query q = getEntityManager().createQuery("DELETE FROM DecFormatoFileDoc u "
                    + "WHERE u.nmFormatoFileDoc IN :formatoFileDocList " + "AND u.orgStrut.idStrut = :idStrut ");
            q.setParameter("formatoFileDocList", formatoFileDocList);
            q.setParameter("idStrut", idStrut);
            q.executeUpdate();
            getEntityManager().flush();
        }
    }

    // FIXME: Di questa query non è molto chiara l'utilità
    public List<DecFormatoFileDoc> retrieveFormatoFileDocNiOrdUsoTB(BigDecimal idStruttura) {
        Query query = getEntityManager().createQuery(
                "SELECT DISTINCT u FROM DecUsoFormatoFileStandard uffs JOIN uffs.decFormatoFileDoc u WHERE uffs.niOrdUso=1 AND u.orgStrut.idStrut = :idstrut ORDER BY u.nmFormatoFileDoc");
        query.setParameter("idstrut", idStruttura);
        List<DecFormatoFileDoc> listaFormati = query.getResultList();
        return listaFormati;
    }
}
