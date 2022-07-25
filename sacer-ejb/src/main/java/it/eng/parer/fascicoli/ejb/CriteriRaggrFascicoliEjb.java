package it.eng.parer.fascicoli.ejb;

import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.DecCriterioRaggrFasc;
import it.eng.parer.fascicoli.helper.CriteriRaggrFascicoliHelper;
import it.eng.parer.slite.gen.tablebean.DecCriterioRaggrFascTableBean;
import it.eng.parer.web.util.Transform;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class CriteriRaggrFascicoliEjb {

    @EJB
    private CriteriRaggrFascicoliHelper crHelper;

    private static final Logger logger = LoggerFactory.getLogger(CriteriRaggrFascicoliEjb.class);

    public DecCriterioRaggrFascTableBean getDecCriterioRaggrFascTableBean(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStrut, String nmCriterioRaggr) {
        DecCriterioRaggrFascTableBean table = new DecCriterioRaggrFascTableBean();
        List<DecCriterioRaggrFasc> list = crHelper.retrieveDecCriterioRaggrFascList(idAmbiente, idEnte, idStrut,
                nmCriterioRaggr);
        if (list != null && !list.isEmpty()) {
            try {
                table = (DecCriterioRaggrFascTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dei criteri di raggruppamento fascicoli "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalStateException("Errore durante il recupero dei criteri di raggruppamento fascicoli");
            }
        }
        return table;
    }
}
