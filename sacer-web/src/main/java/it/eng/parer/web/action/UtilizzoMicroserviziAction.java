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

package it.eng.parer.web.action;

import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.UtilizzoMicroserviziAbstractAction;
import it.eng.parer.util.Client;
import it.eng.parer.util.KeycloakRestUtil;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.security.Secure;
import java.util.List;
import javax.ejb.EJB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Iacolucci_M
 */
public class UtilizzoMicroserviziAction extends UtilizzoMicroserviziAbstractAction {

    private static Logger log = LoggerFactory.getLogger(UtilizzoMicroserviziAction.class.getName());

    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;

    @Override
    public void initOnClick() throws EMFError {
    }

    @Override
    public void insertDettaglio() throws EMFError {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Fields<Field> fields) throws EMFError {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Fields<Field> fields) throws EMFError {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadDettaglio() throws EMFError {
    }

    @Override
    public void undoDettaglio() throws EMFError {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveDettaglio() throws EMFError {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
    }

    @Override
    public void elencoOnClick() throws EMFError {
	goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
	return Application.Publisher.UTILIZZO_MICROSERVIZI;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
    }

    @Override
    public String getControllerName() {
	return Application.Actions.UTILIZZO_MICROSERVIZI;
    }

    @Secure(action = "Menu.Informazioni.UtilizzoMicroservizi")
    public void utilizzoMicroserviziPage() throws EMFError {
	getUser().getMenu().reset();
	getUser().getMenu().select("Menu.Informazioni.UtilizzoMicroservizi");
	// getForm().getDatiUtilizzoMicroserviziList().setTitle("Dati di accesso ai microservizi");
	popolaDatiDaKeycloak();
    }

    @Override
    public void process() throws EMFError {
	//
    }

    private void popolaDatiDaKeycloak() throws EMFError {
	String clientId = configurationHelper
		.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.KEYCLOAK_CLIENT_ID);
	String secret = configurationHelper
		.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.KEYCLOAK_CLIENT_SECRET);
	String url = configurationHelper
		.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.URL_KEYCLOAK);
	KeycloakRestUtil keycloak = new KeycloakRestUtil(url, 10000, clientId, secret);
	getForm().getDatiUtilizzoMicroserviziFields().getNm_url_token()
		.setValue(url + KeycloakRestUtil.PATH_URL_TOKEN);
	BaseTable bt = new BaseTable();
	bt.setPageSize(10);
	BaseRow br = null;
	getForm().getDatiUtilizzoMicroserviziList().setTable(bt);
	try {
	    if (keycloak.getToken()) {
		log.info("Ottenuto il token da keycloak");
		List<Client> l = keycloak.getClients();
		log.info("Ottenuti {} cient da Keycloak", l.size());
		for (Client client : l) {
		    client = keycloak.getClientData(client);
		    br = new BaseRow();
		    br.setString("ds_client", client.getDescription());
		    br.setString("cd_key", client.getClientId());
		    br.setString("cd_secret", client.getSecret());
		    bt.add(br);
		}
	    }
	} catch (Exception ex) {
	    getMessageBox()
		    .addError("Problemi nel contattare l'IDP per ottenere i dati sui microservizi");
	    throw new EMFError(ex.getMessage(), ex);
	}
    }

}
