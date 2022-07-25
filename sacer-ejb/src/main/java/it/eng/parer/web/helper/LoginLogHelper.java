/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.web.helper;

import it.eng.parer.grantedEntity.SIAplApplic;
import it.eng.parer.grantedEntity.SLLogLoginUser;
import it.eng.parer.util.ejb.AppServerInstance;
import it.eng.parer.web.util.Constants;
import it.eng.spagoLite.security.User;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fioravanti_f
 */
@Stateless(mappedName = "LoginLogHelper")
@LocalBean
public class LoginLogHelper {

    Logger log = LoggerFactory.getLogger(LoginLogHelper.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;
    @EJB
    private AppServerInstance appServerInstance;

    public enum TipiEvento {
        LOGIN, LOGOUT
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void writeLogEvento(User user, String indIpClient, TipiEvento tipoEvento) {

        try {
            SIAplApplic tmpAplApplic;
            String queryStr = "select t from SIAplApplic t " + "where t.nmApplic = :nmApplic ";
            javax.persistence.Query query = entityManager.createQuery(queryStr, SIAplApplic.class);
            query.setParameter("nmApplic", Constants.SACER);
            tmpAplApplic = (SIAplApplic) query.getSingleResult();

            String localServerName = appServerInstance.getName();

            SLLogLoginUser tmpLLogLoginUser = new SLLogLoginUser();
            tmpLLogLoginUser.setsIAplApplic(tmpAplApplic);
            tmpLLogLoginUser.setNmUserid(user.getUsername());
            tmpLLogLoginUser.setCdIndIpClient(indIpClient);
            tmpLLogLoginUser.setCdIndServer(localServerName);
            tmpLLogLoginUser.setDtEvento(new Date());
            tmpLLogLoginUser.setTipoEvento(tipoEvento.name());
            // Modifica per lo SPID
            if (user.getUserType() != null) {
                tmpLLogLoginUser.setTipoUtenteAuth(user.getUserType().name());
                tmpLLogLoginUser.setCdIdEsterno(user.getExternalId());
            }
            entityManager.persist(tmpLLogLoginUser);
            entityManager.flush();

        } catch (Exception e) {
            log.error("Eccezione nel log dell'evento login/logout (writeLogEvento) ", e);
            throw new RuntimeException(e);
        }
    }
}
