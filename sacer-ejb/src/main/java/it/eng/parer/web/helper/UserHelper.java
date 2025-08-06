/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.web.helper;

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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.SIOrgEnteSiam.TiEnteConvenz;
import it.eng.parer.grantedEntity.UsrUser;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class UserHelper implements Serializable {

    private static final long serialVersionUID = 1L;
    Logger log = LoggerFactory.getLogger(UserHelper.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager em;

    public void flushEntityManager() {
	em.flush();
    }

    public UsrUser findUsrUser(String username) {
	Query q = em.createQuery("SELECT u FROM UsrUser u WHERE u.nmUserid = :username");
	q.setParameter("username", username);
	return (UsrUser) q.getSingleResult();
    }

    /* Introdotta per lo SPID **/
    public List<UsrUser> findByCodiceFiscale(String codiceFiscale) throws NoResultException {
	Query q = em.createQuery(
		"SELECT u FROM UsrUser u WHERE (u.cdFisc = :codiceFiscaleL OR u.cdFisc = :codiceFiscaleU) AND u.flAttivo='1'");
	q.setParameter("codiceFiscaleL", codiceFiscale.toLowerCase());
	q.setParameter("codiceFiscaleU", codiceFiscale.toUpperCase());
	return q.getResultList();
    }

    /*
     * Introdotto per l'itegrazione con SPID Puglia dove a fronte del codice fiscale arrivato da
     * SPID andiamo a cercare sulla usruser un utente avente come username il codice fiscale
     * ignorando il case.
     */
    public List<UsrUser> findUtentiPerUsernameCaseInsensitive(String username) {
	Query q = em.createQuery(
		"SELECT u FROM UsrUser u WHERE lower(u.nmUserid) = :username  AND u.flAttivo='1'");
	q.setParameter("username", username.toLowerCase());
	return q.getResultList();
    }

    public IamUser findIamUser(String username) {
	Query q = em.createQuery("SELECT u FROM IamUser u WHERE u.nmUserid = :username");
	q.setParameter("username", username);
	return (IamUser) q.getSingleResult();
    }

    public List<IamUser> findIamUserList(String username) {
	Query q = em.createQuery("SELECT u FROM IamUser u WHERE u.nmUserid = :username");
	q.setParameter("username", username);
	return q.getResultList();
    }

    public void updateUserPwd(long idUtente, String oldpassword, String password, Date scadenzaPwd)
	    throws Exception {
	IamUser user = findUserById(idUtente);
	if (!oldpassword.equals(user.getCdPsw())) {
	    throw new Exception();
	}
	user.setDtScadPsw(scadenzaPwd);
	user.setCdPsw(password);
    }

    public IamUser findUserById(long idUtente) {
	Query q = em.createQuery("SELECT u FROM IamUser u WHERE u.idUserIam = :iduser");
	q.setParameter("iduser", idUtente);
	return (IamUser) q.getSingleResult();

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
	return q.getResultList();
    }

    public void deleteIamUser(IamUser user) {
	if (user != null) {
	    em.remove(user);
	    em.flush();
	}
    }

    public boolean checkEnteConvenzionatoAppart(long idUserIam) {
	Query q = em.createQuery("SELECT user FROM UsrUser user "
		+ "JOIN user.siOrgEnteSiam enteSiam " + "WHERE enteSiam.tiEnteConvenz IN (:tipi) "
		+ "AND user.idUserIam = :idUserIam ");
	List<TiEnteConvenz> tipi = new ArrayList<>();
	tipi.add(TiEnteConvenz.GESTORE);
	tipi.add(TiEnteConvenz.AMMINISTRATORE);
	tipi.add(TiEnteConvenz.CONSERVATORE);
	q.setParameter("tipi", tipi);
	q.setParameter("idUserIam", idUserIam);
	return !q.getResultList().isEmpty();
    }

}
