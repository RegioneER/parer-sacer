package it.eng.parer.web.helper;

import it.eng.parer.helper.GenericHelper;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
public class MonitoraggioSinteticoHelper extends GenericHelper {

    Logger log = LoggerFactory.getLogger(MonitoraggioSinteticoHelper.class);

    /*
     * GENERAZIONE RIEPILOGO SINTETICO
     */
    /*
     * Generazione MonVChk in base ai parametri scelti dall'utente
     */
    public Object getMonVChk(String viewUd, String parameters, BigDecimal param1, Long param2) {
        String queryUd = "SELECT view FROM " + viewUd + " view WHERE " + parameters;
        Query query = getEntityManager().createQuery(queryUd);
        query.setParameter("param1", param1);
        if (param2 != null) {
            query.setParameter("param2", param2);
        }
        return query.getSingleResult();
    }

    public List getMonVCnt(String viewUd, String parameters, BigDecimal param1, Long param2) {
        String queryUd = "SELECT view FROM " + viewUd + " view WHERE " + parameters;
        Query query = getEntityManager().createQuery(queryUd);
        query.setParameter("param1", param1);
        if (param2 != null) {
            query.setParameter("param2", param2);
        }
        return query.getResultList();
    }
}
