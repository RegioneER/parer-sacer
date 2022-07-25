package it.eng.parer.job.indiceAipSerieUd.helper;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.Query;

import it.eng.parer.entity.SerVerSerieDaElab;
import it.eng.parer.helper.GenericHelper;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "CreazioneIndiceAipSerieUdHelper")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class CreazioneIndiceAipSerieUdHelper extends GenericHelper {

    /**
     * Recupera la lista dei record da elaborare per creare l'indice AIP Serie UD in base alla struttura ed allo stato
     * della versione
     *
     * @param idStrut
     *            id struttura
     * @param tiStatoVerSerie
     *            stato versamento serie
     * 
     * @return la lista da elaborare
     */
    public List<SerVerSerieDaElab> getSerVerSerieDaElab(BigDecimal idStrut, String tiStatoVerSerie) {
        List<SerVerSerieDaElab> serieDaElabList;
        String whereWord = "WHERE ";
        String queryStr = "SELECT u FROM SerVerSerieDaElab u ";
        if (idStrut != null) {
            queryStr = queryStr + whereWord + "u.idStrut = :idStrut ";
            whereWord = "AND ";
        }
        if (tiStatoVerSerie != null) {
            queryStr = queryStr + whereWord + "u.tiStatoVerSerie = :tiStatoVerSerie ";
        }
        Query query = getEntityManager().createQuery(queryStr);
        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }
        if (tiStatoVerSerie != null) {
            query.setParameter("tiStatoVerSerie", tiStatoVerSerie);
        }
        serieDaElabList = (List<SerVerSerieDaElab>) query.getResultList();
        return serieDaElabList;
    }

    /**
     * Conta le ud appartenenti al contenuto di tipo EFFETTIVO per le quali la foreign key al volume è nulla
     *
     * @param idVerSerie
     *            id versamento serie
     * 
     * @return pk entity AroUdAppartVerSerie
     */
    public Long getNumUdEffettiveSenzaVolume(Long idVerSerie) {
        String queryStr = "SELECT COUNT(u) FROM AroUdAppartVerSerie u "
                + "JOIN u.serContenutoVerSerie contenutoVerSerie " + "JOIN contenutoVerSerie.serVerSerie verSerie "
                + "WHERE contenutoVerSerie.tiContenutoVerSerie = 'EFFETTIVO' " + "AND u.serVolVerSerie IS NULL "
                + "AND verSerie.idVerSerie = :idVerSerie ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVerSerie", idVerSerie);
        return (Long) query.getSingleResult();
    }

    /**
     * Recupera l'ultimo progressivo stato versione serie
     *
     * @param idVerSerie
     *            id versamento serie
     * 
     * @return progressivo
     */
    public BigDecimal getUltimoProgressivoSerStatoVerSerie(Long idVerSerie) {
        String queryStr = "SELECT MAX(u.pgStatoVerSerie) FROM SerStatoVerSerie u "
                + "WHERE u.serVerSerie.idVerSerie = :idVerSerie ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVerSerie", idVerSerie);
        return (BigDecimal) query.getSingleResult();
    }

    /**
     * Recupera l'ultimo progressivo stato serie
     *
     * @param idSerie
     *            id serie
     * 
     * @return progressivo
     */
    public BigDecimal getUltimoProgressivoSerStatoSerie(Long idSerie) {
        String queryStr = "SELECT MAX(u.pgStatoSerie) FROM SerStatoSerie u " + "WHERE u.serSerie.idSerie = :idSerie ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idSerie", idSerie);
        return (BigDecimal) query.getSingleResult();
    }
}
