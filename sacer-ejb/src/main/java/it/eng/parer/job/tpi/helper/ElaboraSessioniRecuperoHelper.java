package it.eng.parer.job.tpi.helper;

import it.eng.parer.entity.RecDtVersRecup;
import it.eng.parer.entity.RecSessioneRecup;
import it.eng.parer.entity.VrsDtVers;
import it.eng.parer.job.utils.JobConstants;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xadisk.connector.outbound.XADiskConnectionFactory;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "ElaboraSessioniRecuperoHelper")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class ElaboraSessioniRecuperoHelper {

    Logger log = LoggerFactory.getLogger(ElaboraSessioniRecuperoHelper.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    @Resource(mappedName = "jca/xadiskLocal")
    private XADiskConnectionFactory xadCf;

    @EJB
    private ElaboraSessioniRecuperoHelper me;

    public List<RecSessioneRecup> getSessioniRecuperoInCorso() {
        javax.persistence.Query query = entityManager
                .createQuery("SELECT s FROM RecSessioneRecup s WHERE s.tiStatoSessioneRecup = :stato");
        query.setParameter("stato", JobConstants.StatoSessioniRecupEnum.IN_CORSO.name());
        return (List<RecSessioneRecup>) query.getResultList();
    }

    public List<VrsDtVers> getVrsDtVersByDate(Date dtVers) {
        javax.persistence.Query query = entityManager.createQuery("SELECT v FROM VrsDtVers v WHERE v.dtVers = :data");
        query.setParameter("data", dtVers);

        List<VrsDtVers> lstObjects = (List<VrsDtVers>) query.getResultList();
        return lstObjects;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void retrieveFileUnitaDoc(Long idRecDtVersRecup, int countRecuperate, int sizeRecDtVersRecup) {
        RecDtVersRecup recDtVersRecup = entityManager.find(RecDtVersRecup.class, idRecDtVersRecup);
        me.setStatoRecuperata(recDtVersRecup, countRecuperate == sizeRecDtVersRecup);
    }

    public void setStatoRecuperata(RecDtVersRecup recDtVersRecup, boolean closeSession) {
        recDtVersRecup.setTiStatoDtVersRecup(JobConstants.StatoDtVersRecupEnum.RECUPERATA.name());
        if (closeSession) {
            recDtVersRecup.getRecSessioneRecup()
                    .setTiStatoSessioneRecup(JobConstants.StatoSessioniRecupEnum.CHIUSO_OK.name());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setStatoSessErrata(Long idRecDtVersRecup, String causa) {
        RecDtVersRecup recDtVersRecup = entityManager.find(RecDtVersRecup.class, idRecDtVersRecup);
        recDtVersRecup.setTiStatoDtVersRecup(JobConstants.StatoDtVersRecupEnum.ERRORE.name());
        recDtVersRecup.getRecSessioneRecup().setDtChiusura(new Date());
        recDtVersRecup.getRecSessioneRecup()
                .setTiStatoSessioneRecup(JobConstants.StatoSessioniRecupEnum.CHIUSO_ERR.name());
        recDtVersRecup.getRecSessioneRecup().setDlErr(causa);
        recDtVersRecup.getRecSessioneRecup().setCdErr("--");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setSessioneChiusoOk(Long idRecSessioneRecup) {
        RecSessioneRecup recSessioneRecup = entityManager.find(RecSessioneRecup.class, idRecSessioneRecup);
        recSessioneRecup.setTiStatoSessioneRecup(JobConstants.StatoSessioniRecupEnum.CHIUSO_OK.name());
    }

}
