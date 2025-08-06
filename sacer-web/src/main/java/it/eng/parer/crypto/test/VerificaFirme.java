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

package it.eng.parer.crypto.test;

import static it.eng.spagoCore.ConfigProperties.StandardProperty.DISABLE_SECURITY;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.ejb.EJB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.slite.gen.Application;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.spagoCore.ConfigSingleton;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.actions.ActionBase;
import it.eng.spagoLite.security.IUser;

/**
 * Pagina di collegamento per la verifica delle firme.
 */
@SuppressWarnings("rawtypes")
public class VerificaFirme extends ActionBase {

    private final Logger log = LoggerFactory.getLogger(VerificaFirme.class);

    private static final String ENDPOINT_SEPARATOR = "\\|";

    private static final String CRYPTO_CONTEXT = "/verifica";
    private static final String CRYPTO_ENDPOINT = "CRYPTO_VERIFICA_FIRMA_ENDPOINT";

    private static final String EIDAS_CONTEXT = "/validation";
    private static final String EIDAS_ENDPOINT = "EIDAS_VERIFICA_FIRMA_ENDPOINT";

    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;

    @Override
    public void process() {
	List<String> cryptoEndPoints = getEndPoints(CRYPTO_ENDPOINT, ENDPOINT_SEPARATOR);
	Optional<String> cryptoEndPoint = cryptoEndPoints.stream()
		.filter(s -> !s.contains("localhost")).findFirst();
	List<String> eidasEndPoints = getEndPoints(EIDAS_ENDPOINT, ENDPOINT_SEPARATOR);
	Optional<String> eidasEndPoint = eidasEndPoints.stream()
		.filter(s -> !s.contains("localhost")).findFirst();

	if (cryptoEndPoint.isPresent()) {
	    getRequest().setAttribute("CRYPTO_LINK", cryptoEndPoint.get() + CRYPTO_CONTEXT);
	}
	if (eidasEndPoint.isPresent()) {
	    getRequest().setAttribute("EIDAS_LINK", eidasEndPoint.get() + EIDAS_CONTEXT);
	}

	forwardToPublisher(Application.Publisher.VERIFICA_FIRME_TEST);
    }

    @Override
    protected String getDefaultPublsherName() {
	return Application.Publisher.VERIFICA_FIRME_TEST;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
	throw new UnsupportedOperationException();
    }

    @Override
    public String getControllerName() {
	return "VerificaFirme";
    }

    @Override
    protected boolean isAuthorized(String destination) {
	if (ConfigSingleton.getInstance().getBooleanValue(DISABLE_SECURITY.name())) {
	    return true;
	}
	IUser user = SessionManager.getUser(getSession());
	if (user == null) {
	    log.error("Utente non autorizzato alla visualizzazione della pagina {}", destination);
	    return false;
	}
	return true;
    }

    @Override
    protected void check() {
	super.check();
	if (SessionManager.getUser(getSession()) == null) {
	    getMessageBox().addFatal(
		    "Utente non trovato. Si prega di eseguire la procedura di <a href=\"Login.html\" title=\"EXIT\">login</a>");
	    redirectToAction("Login.html");
	}
    }

    private List<String> getEndPoints(final String param, final String separator) {
	final List<String> endPointCL = new LinkedList<>();
	final String endPointsString = configurationHelper.getValoreParamApplicByApplic(param);
	Pattern.compile(separator).splitAsStream(endPointsString).map(String::trim)
		.forEach(endPointCL::add);
	return endPointCL;
    }

}
