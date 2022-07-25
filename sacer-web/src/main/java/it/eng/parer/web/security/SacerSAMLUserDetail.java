package it.eng.parer.web.security;

import it.eng.parer.web.helper.UserHelper;
import it.eng.spagoLite.security.User;
import it.eng.spagoLite.security.saml.SliteSAMLUserDetail;
import java.util.List;
import javax.ejb.EJB;
import org.slf4j.Logger;
import it.eng.parer.grantedEntity.UsrUser;

import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;

/**
 *
 * @author MIacolucci
 */
public class SacerSAMLUserDetail extends SliteSAMLUserDetail {

    @EJB(mappedName = "java:app/Parer-ejb/UserHelper")
    private UserHelper userHelper;

    private static final Logger logger = LoggerFactory.getLogger(SacerSAMLUserDetail.class);

    protected User verificaEsistenzaUtente(SAMLCredential credential, User user) throws UsernameNotFoundException {

        String codiceFiscaleSpid = getValue(credential, SPID_CODICE_FISCALE);
        String metodoAutenticazioneSpid = getValue(credential, SPID_AUTHENTICATION_METHOD);

        if (isUtenteSpidValido(credential)) {
            /*** SI TRATTA DI UN UTENTE SPID ***/
            List<UsrUser> l = userHelper.findByCodiceFiscale(codiceFiscaleSpid);
            if (l.size() > 1) {
                logger.warn(String.format(MSG_TROPPE_OCCORRENZE_UTENTE, user.getNome(), user.getCognome(),
                        codiceFiscaleSpid));
                throw new UsernameNotFoundException(String.format(MSG_TROPPE_OCCORRENZE_UTENTE, user.getNome(),
                        user.getCognome(), codiceFiscaleSpid));
            } else if (l.size() == 0) {
                logger.warn(String.format(MSG_UTENTE_NON_AUTORIZZATO, user.getNome(), user.getCognome(),
                        codiceFiscaleSpid));
                throw new UsernameNotFoundException(String.format(MSG_UTENTE_NON_AUTORIZZATO, user.getNome(),
                        user.getCognome(), codiceFiscaleSpid));
            } else {
                // recupero l'id dell'utente e lo setto nell'oggetto utente e lo metto in sessione.
                // Modifica fatta perché idp generici non conoscono l'id dell'utente del db di iam.
                UsrUser ut = l.iterator().next();
                user.setUsername(ut.getNmUserid());
                user.setIdUtente(ut.getIdUserIam());
                user.setUserType(User.UserType.SPID_FEDERA);
                user.setExternalId(getValue(credential, SPID_CODE));
                // Logga tutti gli attributi dell'utente SPID
                logInfoUtenteSAML(credential);
            }
        } else if (codiceFiscaleSpid != null) {
            /*
             * Se l'utente non ha scelto secondo livello SPID ma un'altro metodo di autenticazione (federa passa il
             * codice fiscale) Il sistema impedisce di entrare!
             */
            logger.warn(String.format(MSG_UTENTE_LIVELLO_AUTH_INADEGUATO, user.getNome(), user.getCognome(),
                    codiceFiscaleSpid, metodoAutenticazioneSpid));
            throw new UsernameNotFoundException(String.format(MSG_UTENTE_LIVELLO_AUTH_INADEGUATO, user.getNome(),
                    user.getCognome(), codiceFiscaleSpid, metodoAutenticazioneSpid));
        } else {
            /*** UTENTE IDP PARER O PUGLIA ***/
            UsrUser usrUser = userHelper.findUsrUser(user.getUsername());
            user.setIdUtente(usrUser.getIdUserIam());
            user.setScadenzaPwd(usrUser.getDtScadPsw());
        }
        return user;
    }

}
