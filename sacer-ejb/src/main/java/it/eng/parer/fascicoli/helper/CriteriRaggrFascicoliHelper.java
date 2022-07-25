package it.eng.parer.fascicoli.helper;

import it.eng.parer.entity.OrgStrut;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.DecCriterioRaggrFasc;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.web.util.Transform;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class CriteriRaggrFascicoliHelper extends GenericHelper {

    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;

    private static final Logger log = LoggerFactory.getLogger(CriteriRaggrFascicoliHelper.class.getName());

    public List<DecCriterioRaggrFasc> retrieveDecCriterioRaggrFascList(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStrut, String nmCriterioRaggr) {
        StringBuilder queryStr = new StringBuilder("SELECT u FROM DecCriterioRaggrFasc u ");
        String whereWord = "WHERE ";
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.orgStrut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.orgStrut.orgEnte.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.orgStrut.idStrut = :idStrut ");
            whereWord = "AND ";
        }
        if (nmCriterioRaggr != null) {
            queryStr.append(whereWord).append("u.nmCriterioRaggr = :nmCriterioRaggr");
        }
        queryStr.append(" ORDER BY u.nmCriterioRaggr ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }
        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }
        if (nmCriterioRaggr != null) {
            query.setParameter("nmCriterioRaggr", nmCriterioRaggr);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<DecCriterioRaggrFasc> listaCritRaggrFasc = query.getResultList();
        return listaCritRaggrFasc;
    }

    public List<DecCriterioRaggrFasc> retrieveDecCriterioRaggrFascList(BigDecimal idStruttura) {
        return retrieveDecCriterioRaggrFascList(null, null, idStruttura, null);
    }

    public OrgStrutRowBean getOrgStrutById(BigDecimal idStrut) {
        OrgStrutRowBean strutRB = new OrgStrutRowBean();
        try {
            strutRB = (OrgStrutRowBean) Transform
                    .entity2RowBean(getEntityManager().find(OrgStrut.class, idStrut.longValue()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return strutRB;
    }

}
