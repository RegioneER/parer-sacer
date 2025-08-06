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

package it.eng.parer.ws.replicaUtente.ejb;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.exception.ExceptionUtils;

import it.eng.parer.entity.IamUser;
import it.eng.parer.exception.ParerErrorSeverity;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.replicaUtente.dto.CancellaUtenteExt;
import it.eng.parer.ws.replicaUtente.dto.RispostaWSCancellaUtente;
import it.eng.parer.ws.utils.MessaggiWSBundle;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "CancellaUtenteHelper")
@LocalBean
public class CancellaUtenteHelper {

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;
    @EJB
    private CancellaUtenteHelper cuHelper;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteIamUser(CancellaUtenteExt cuExt, RispostaWSCancellaUtente rispostaWs)
	    throws ParerInternalError {
	try {
	    /*
	     * Verifica se l'utente è referenziato nelle sessioni o nelle unità documentarie o nei
	     * volumi o nei log delle operazioni sui volumi
	     */
	    boolean isReferenced = cuHelper.isReferenced(cuExt.getIdUserIam());
	    /* Elimino/modifico l'utente */
	    if (isReferenced) {
		IamUser u = entityManager.getReference(IamUser.class,
			cuExt.getIdUserIam().longValue());
		u.setFlAttivo("0");
	    } else {
		deleteIamUser(cuExt.getIdUserIam().longValue());
	    }
	} catch (Exception ex) {
	    rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
	    rispostaWs.setErrorCode(MessaggiWSBundle.SERVIZI_USR_001);
	    rispostaWs.setErrorMessage("Errore nella cancellazione dell'utente "
		    + ExceptionUtils.getRootCauseMessage(ex));
	    throw new ParerInternalError(ParerErrorSeverity.ERROR,
		    "Errore nella cancellazione dell'utente "
			    + ExceptionUtils.getRootCauseMessage(ex),
		    ex);
	}
    }

    public void deleteIamUser(Long idUserIam) {
	Query q = entityManager
		.createQuery("DELETE FROM IamUser u WHERE u.idUserIam = :idUserIam ");
	q.setParameter("idUserIam", idUserIam);
	q.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public boolean isReferenced(Integer idUserIam) {
	String queryStr = "SELECT u FROM IamUser u " + "WHERE u.idUserIam = :idUserIam "
		+ "AND ( EXISTS (SELECT s FROM VrsSessioneVers s "
		+ "            WHERE s.iamUser.idUserIam = :idUserIam ) "
		+ "OR EXISTS (SELECT sk FROM VrsSessioneVersKo sk "
		+ "            WHERE sk.iamUser.idUserIam = :idUserIam ) "
		+ "OR EXISTS (SELECT s2 FROM RecSessioneRecup s2 "
		+ "           WHERE s2.iamUser.idUserIam = :idUserIam ) "
		+ "OR EXISTS (SELECT ud FROM AroUnitaDoc ud "
		+ "           WHERE ud.iamUser.idUserIam = :idUserIam ) "
		+ "OR EXISTS (SELECT v FROM VolVolumeConserv v "
		+ "           WHERE v.iamUserCreazione.idUserIam = :idUserIam ) "
		+ "OR EXISTS (SELECT v2 FROM VolVolumeConserv v2 "
		+ "           WHERE v2.iamUserFirmaMarca.idUserIam = :idUserIam ) "
		+ "OR EXISTS (SELECT e1 FROM ElvElencoVer e1 "
		+ "           WHERE e1.iamUserFirmaIndice.idUserIam = :idUserIam ) "
		+ "OR EXISTS (SELECT e2 FROM ElvElencoVer e2 "
		+ "           WHERE e2.iamUserChiusoElenco.idUserIam = :idUserIam ) "
		+ "OR EXISTS (SELECT elog FROM ElvLogElencoVer elog "
		+ "           WHERE elog.iamUser.idUserIam = :idUserIam ) "
		+ "OR EXISTS (SELECT l FROM LogOper l "
		+ "           WHERE l.iamUser.idUserIam = :idUserIam) "
		+ "OR EXISTS (SELECT statoRichAnnulVers FROM AroStatoRichAnnulVers statoRichAnnulVers "
		+ "           WHERE statoRichAnnulVers.iamUser.idUserIam = :idUserIam) "
		+ "OR EXISTS (SELECT notaVerSerie FROM SerNotaVerSerie notaVerSerie "
		+ "           WHERE notaVerSerie.iamUser.idUserIam = :idUserIam) "
		+ "OR EXISTS (SELECT notaTipoSerie FROM DecNotaTipoSerie notaTipoSerie "
		+ "           WHERE notaTipoSerie.iamUser.idUserIam = :idUserIam) "
		+ "OR EXISTS (SELECT statoVerSerie FROM SerStatoVerSerie statoVerSerie "
		+ "           WHERE statoVerSerie.iamUser.idUserIam = :idUserIam) "
		+ "OR EXISTS (SELECT consistVerSerie FROM SerConsistVerSerie consistVerSerie "
		+ "           WHERE consistVerSerie.iamUser.idUserIam = :idUserIam) ) ";

	Query q = entityManager.createQuery(queryStr);
	q.setParameter("idUserIam", idUserIam.longValue());
	List<IamUser> iuList = q.getResultList();
	return !iuList.isEmpty();
    }

}
