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

/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.web.security;

import it.eng.integriam.client.util.UserUtil;
import it.eng.integriam.client.ws.IAMSoapClients;
import it.eng.integriam.client.ws.recauth.AuthWSException_Exception;
import it.eng.integriam.client.ws.recauth.RecuperoAutorizzazioni;
import it.eng.integriam.client.ws.recauth.RecuperoAutorizzazioniRisposta;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.security.User;
import it.eng.spagoLite.security.auth.Authenticator;
import javax.ejb.EJB;
import javax.servlet.http.HttpSession;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Quaranta_M
 */
public class SacerAuthenticator extends Authenticator {

    private static final Logger log = LoggerFactory.getLogger(SacerAuthenticator.class);

    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configHelper;

    /*
     * @EJB(mappedName = "java:app/Parer-ejb/UserHelper") private UserHelper userHelper;
     */
    @Override
    public User recuperoAutorizzazioni(HttpSession httpSession) {
        User utente = (User) SessionManager.getUser(httpSession);
        /*
         * try { // recupero l'ID utente nella tabella locale, partendo da nmUserid IAM univoco
         * UsrUser user = userHelper.findUsrUser(utente.getUsername());
         * utente.setIdUtente(user.getIdUserIam()); utente.setScadenzaPwd(user.getDtScadPsw()); }
         * catch (Exception e) { throw new WebServiceException(
         * "L'Utente non è ancora censito nella tabella IAMUSER locale:  " + e.getMessage()); }
         */
        //
        // RecuperoAutorizzazioni client =
        // IAMSoapClients.recuperoAutorizzazioniClient(utente.getConfigurazione().get("USERID_RECUP_INFO"),
        // utente.getConfigurazione().get("PSW_RECUP_INFO"),
        // "http://localhost:8080/saceriam/RecuperoAutorizzazioni");
        String psw = configHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.PSW_RECUP_INFO);
        String user = configHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.USERID_RECUP_INFO);
        String url = configHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.URL_RECUP_AUTOR_USER);
        String timeoutString = configHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.TIMEOUT_RECUP_AUTOR_USER);

        RecuperoAutorizzazioni client = IAMSoapClients.recuperoAutorizzazioniClient(user, psw, url);
        if (client == null) {
            throw new WebServiceException(
                    "Non è stato possibile recuperare la lista delle autorizzazioni da SIAM");
        }

        // imposto il valore di timeout. vedi MEV #23814
        if (timeoutString != null && timeoutString.matches("^[0-9]+$")) {
            int timeoutRecuperoAutorizzazioni = Integer.parseInt(timeoutString);
            IAMSoapClients.changeRequestTimeout((BindingProvider) client,
                    timeoutRecuperoAutorizzazioni);
        } else {
            log.warn("Il valore personalizzato \"" + timeoutString
                    + "\" per il parametro TIMEOUT_RECUP_AUTOR_USER non è corretto. Utilizzo il valore predefinito");
        }

        RecuperoAutorizzazioniRisposta resp;
        try {
            resp = client.recuperoAutorizzazioniPerNome(utente.getUsername(), getAppName(),
                    utente.getIdOrganizzazioneFoglia().intValue());
        } catch (AuthWSException_Exception e) {
            throw new RuntimeException(e);
        }
        UserUtil.fillComponenti(utente, resp);
        SessionManager.setUser(httpSession, utente);
        return utente;

    }

    @Override
    protected String getAppName() {
        return configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC);
    }

}
