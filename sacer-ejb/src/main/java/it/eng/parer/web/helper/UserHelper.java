package it.eng.parer.web.helper;

import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.constraint.SIOrgEnteSiam.TiEnteConvenz;
import it.eng.parer.grantedEntity.UsrUser;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@Stateless
@LocalBean
public class UserHelper implements Serializable {

    Logger log = LoggerFactory.getLogger(UserHelper.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager em;

    public void flushEntityManager() {
        em.flush();
    }

    public UsrUser findUsrUser(String username) {
        Query q = em.createQuery("SELECT u FROM UsrUser u WHERE u.nmUserid = :username");
        q.setParameter("username", username);
        UsrUser user = (UsrUser) q.getSingleResult();
        return user;
    }

    /* Introdotta per lo SPID **/
    public List<UsrUser> findByCodiceFiscale(String codiceFiscale) throws NoResultException {
        Query q = em.createQuery(
                "SELECT u FROM UsrUser u WHERE (u.cdFisc = :codiceFiscaleL OR u.cdFisc = :codiceFiscaleU) AND u.flAttivo='1'");
        q.setParameter("codiceFiscaleL", codiceFiscale.toLowerCase());
        q.setParameter("codiceFiscaleU", codiceFiscale.toUpperCase());
        return (List<UsrUser>) q.getResultList();
    }

    public IamUser findIamUser(String username) {
        Query q = em.createQuery("SELECT u FROM IamUser u WHERE u.nmUserid = :username");
        q.setParameter("username", username);
        IamUser user = (IamUser) q.getSingleResult();
        return user;
    }

    public List<IamUser> findIamUserList(String username) {
        Query q = em.createQuery("SELECT u FROM IamUser u WHERE u.nmUserid = :username");
        q.setParameter("username", username);
        return q.getResultList();
    }

    public void updateUserPwd(long idUtente, String oldpassword, String password, Date scadenzaPwd)
            throws NoResultException, Exception {
        IamUser user = findUserById(idUtente);
        if (!oldpassword.equals(user.getCdPsw())) {
            throw new Exception();
        }
        user.setDtScadPsw(scadenzaPwd);
        user.setCdPsw(password);
    }

    public IamUser findUserById(long idUtente) throws NoResultException {
        Query q = em.createQuery("SELECT u FROM IamUser u WHERE u.idUserIam = :iduser");
        q.setParameter("iduser", idUtente);
        IamUser user = null;
        user = (IamUser) q.getSingleResult();
        return user;

    }

    public OrgAmbiente getOrgAmbienteById(BigDecimal idAmbiente) {
        return em.find(OrgAmbiente.class, idAmbiente.longValue());
    }

    public OrgEnte getOrgEnteById(BigDecimal idEnte) {
        return em.find(OrgEnte.class, idEnte.longValue());
    }

    public OrgStrut getOrgStrutById(BigDecimal idStrut) {
        return em.find(OrgStrut.class, idStrut.longValue());
    }

    public DecTipoUnitaDoc getTipoUnitaDocById(BigDecimal idTipoUd) {
        return em.find(DecTipoUnitaDoc.class, idTipoUd.longValue());
    }

    public DecRegistroUnitaDoc getRegUnitaDocById(BigDecimal idRegUd) {
        return em.find(DecRegistroUnitaDoc.class, idRegUd.longValue());
    }

    public IamUser mergeIamUser(IamUser user) {
        return em.merge(user);
    }

    public void resetPwd(long idUtente, String randomPwd, Date scad) {
        IamUser user = findUserById(idUtente);
        user.setDtScadPsw(scad);
        user.setCdPsw(randomPwd);
    }

    public void resetPwd(long idUtente, String randomPwd) {
        resetPwd(idUtente, randomPwd, Calendar.getInstance().getTime());
    }

    public List<OrgAmbiente> getAmbienti() {
        Query q = em.createQuery("SELECT amb FROM OrgAmbiente amb");
        List<OrgAmbiente> ambienti = q.getResultList();
        return ambienti;
    }

    public void deleteIamUser(IamUser user) {
        if (user != null) {
            em.remove(user);
            em.flush();
        }
    }

    public boolean checkEnteConvenzionatoAppart(long idUserIam) {
        Query q = em.createQuery("SELECT user FROM UsrUser user " + "JOIN user.siOrgEnteSiam enteSiam "
                + "WHERE enteSiam.tiEnteConvenz IN :tipi " + "AND user.idUserIam = :idUserIam ");
        List<TiEnteConvenz> tipi = new ArrayList();
        tipi.add(TiEnteConvenz.GESTORE);
        tipi.add(TiEnteConvenz.AMMINISTRATORE);
        tipi.add(TiEnteConvenz.CONSERVATORE);
        q.setParameter("tipi", tipi);
        q.setParameter("idUserIam", idUserIam);
        return !q.getResultList().isEmpty();
    }

}
