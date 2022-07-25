package it.eng.parer.web.util;

import javax.ejb.EJB;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import it.eng.parer.web.helper.LoginLogHelper;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.security.User;

/**
 *
 * @author fioravanti_f
 */
public class AuditSessionListener implements HttpSessionListener {

    public static final String CLIENT_IP_ADDRESS = "###_LOG#_CLIENT_IP_ADDRESS";

    @EJB(mappedName = "java:app/Parer-ejb/LoginLogHelper")
    private LoginLogHelper loginLogHelper;

    @Override
    public void sessionCreated(HttpSessionEvent se) {

    }

    /**
     *
     * @param se
     *            sessione {@link HttpSessionEvent}
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {

        HttpSession sessione = se.getSession();
        User tmpUser = (User) SessionManager.getUser(sessione);
        String ipVers = (String) sessione.getAttribute(CLIENT_IP_ADDRESS);

        // queste due variabili possono essere nulle, la fase di logout infatti
        // provoca lo scatenamento ripetuto di questo evento, ma in uno
        // solo dei casi (la vera fine della sessione applicativa) queste
        // variabili sono ancora in sessione. Ovviamente è questo il punto in
        // cui l'evento deve essere loggato sul db
        if (tmpUser != null && ipVers != null) {
            loginLogHelper.writeLogEvento(tmpUser, ipVers, LoginLogHelper.TipiEvento.LOGOUT);
        }
    }

}
