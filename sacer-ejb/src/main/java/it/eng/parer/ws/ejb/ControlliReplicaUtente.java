package it.eng.parer.ws.ejb;

import it.eng.parer.entity.IamUser;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "ControlliReplicaUtente")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class ControlliReplicaUtente {

    private static final Logger log = LoggerFactory.getLogger(ControlliReplicaUtente.class);
    //
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    /**
     * Verifica l'esistenza di un utente in base all'identificazione in input
     *
     * @param idUserIam
     *            l'id dell'utente
     * 
     * @return RispostaControlli.isrBoolean() == true se esiste l'utente
     *
     */
    public RispostaControlli verificaEsistenzaUtente(long idUserIam) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            IamUser user = entityManager.find(IamUser.class, idUserIam);
            if (user != null) {
                rispostaControlli.setrBoolean(true);
            }

        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666, e.getMessage()));
            log.error("Eccezione nella lettura della tabella degli utenti ", e);
        }
        return rispostaControlli;
    }

    /**
     * Verifica l'esistenza di un utente in base all'identificazione in input
     *
     * @param idUserIam
     *            l'id dell'utente
     * 
     * @return RispostaControlli.isrBoolean() == true se esiste l'utente ed è attivo
     *
     */
    public RispostaControlli verificaEsistenzaUtenteAttivo(long idUserIam) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            Query query = entityManager
                    .createQuery("SELECT u FROM IamUser u WHERE u.idUserIam = :idUserIam AND u.flAttivo = '1' ");
            query.setParameter("idUserIam", idUserIam);
            List<IamUser> userList = query.getResultList();
            if (userList != null && !userList.isEmpty()) {
                rispostaControlli.setrBoolean(true);
            }

        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666, e.getMessage()));
            log.error("Eccezione nella lettura della tabella degli utenti ", e);
        }
        return rispostaControlli;
    }

    /**
     * Verifica l'esistenza di uno userid in base al valore in input
     *
     * @param nmUserid
     *            il nome utente da verificare
     * 
     * @return RispostaControlli.isrBoolean() == true se esiste
     *
     */
    public RispostaControlli verificaEsistenzaNmUserid(String nmUserid) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            if (nmUserid != null) {
                Query query = entityManager
                        .createQuery("SELECT u FROM IamUser u WHERE u.nmUserid = :nmUserid AND u.flAttivo = '1'");
                query.setParameter("nmUserid", nmUserid);
                List<IamUser> userList = query.getResultList();
                if (userList != null && !userList.isEmpty()) {
                    rispostaControlli.setrBoolean(true);
                }
            }
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666, e.getMessage()));
            log.error("Eccezione nella lettura della tabella degli utenti ", e);
        }
        return rispostaControlli;
    }
}
