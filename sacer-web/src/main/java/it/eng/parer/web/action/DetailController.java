/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.web.action;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.async.helper.CalcoloMonitoraggioHelper;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.slite.gen.form.StruttureForm;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.slite.gen.viewbean.AroVRicUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVRicUnitaDocTableBean;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.security.SacerAuthenticator;
import it.eng.parer.web.util.WebConstants;
import it.eng.spagoIFace.session.SessionCoreManager;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.security.User;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Bonora_L
 */
@Controller
@RequestMapping("/")
public class DetailController {

    private StruttureEjb struttureEjb;
    private UnitaDocumentarieHelper udHelper;
    private CalcoloMonitoraggioHelper calcoloMonHelper;
    private ConfigurationHelper configurationHelper;
    @Autowired
    private SacerAuthenticator authenticator;

    private static final Logger logger = LoggerFactory.getLogger(DetailController.class);

    @PostConstruct
    public void init() {
        try {
            configurationHelper = (ConfigurationHelper) new InitialContext()
                    .lookup("java:app/Parer-ejb/ConfigurationHelper");
            calcoloMonHelper = (CalcoloMonitoraggioHelper) new InitialContext()
                    .lookup("java:app/Parer-ejb/CalcoloMonitoraggioHelper");
            udHelper = (UnitaDocumentarieHelper) new InitialContext()
                    .lookup("java:app/Parer-ejb/UnitaDocumentarieHelper");
            struttureEjb = (StruttureEjb) new InitialContext().lookup("java:app/Parer-ejb/StruttureEjb");
        } catch (NamingException ex) {
            logger.error("Errore nel recupero dell'EJB ConfigurationHelper ", ex);
            throw new IllegalStateException(ex);
        }
    }

    @GetMapping(value = "/strut/{id}")
    public @ResponseBody void detailStrut(final HttpServletRequest request, final HttpServletResponse res,
            @PathVariable BigDecimal id, Model model) {
        model.addAttribute("id", id);

        OrgStrutRowBean row = struttureEjb.getOrgStrutRowBean(id);
        OrgStrutTableBean table = new OrgStrutTableBean();
        table.add(row);
        StruttureForm form = new StruttureForm();
        form.getStruttureList().setTable(table);
        StruttureAction action = new StruttureAction();

        SessionManager.clearActionHistory(request.getSession());
        SessionCoreManager.setLastPublisher(request.getSession(), "");
        SessionManager.setForm(request.getSession(), form);
        SessionManager.setCurrentAction(request.getSession(), action.getControllerName());
        SessionManager.initMessageBox(request.getSession());
        SessionManager.addPrevExecutionToHistory(request.getSession(), true, false, null);
        User user = checkUser(request.getSession());
        if (user != null) {
            user.setIdOrganizzazioneFoglia(id);
            Map<String, String> organizzazione = new LinkedHashMap<>();
            organizzazione.put(WebConstants.Organizzazione.AMBIENTE.name(), row.getString("nm_ambiente"));
            organizzazione.put(WebConstants.Organizzazione.ENTE.name(), row.getString("nm_ente"));
            organizzazione.put(WebConstants.Organizzazione.STRUTTURA.name(), row.getNmStrut());
            user.setOrganizzazioneMap(organizzazione);
            user.setConfigurazione(configurationHelper.getConfiguration());
            try {
                authenticator.recuperoAutorizzazioni(request.getSession());
                // ?operation=listNavigationOnClick&table=StruttureList&navigationEvent=dettaglioView&riga=0
                res.sendRedirect(request.getServletContext().getContextPath() + "/" + action.getControllerName()
                        + "?operation=listNavigationOnClick&table=" + form.getStruttureList().getName()
                        + "&navigationEvent=" + StruttureAction.NE_DETTAGLIO_VIEW + "&riga=0");
            } catch (WebServiceException ex) {
                logger.error("Eccezione", ex);
                // TODO : Inviare a una pagina che indica l'impossibilità di caricare la pagina? Direi di si
            } catch (IOException ex) {
                logger.error("Errore nel caricamento del dettaglio struttura", ex);
            }
        } else {
            logger.error("Utente non autenticato: impossibile valorizzare i dati utente.");
        }
    }

    @GetMapping(value = "/ud/{idStrut}/{registro}/{anno}/{numero}")
    public @ResponseBody void detailUd(final HttpServletRequest request, final HttpServletResponse res,
            @PathVariable BigDecimal idStrut, @PathVariable String registro, @PathVariable BigDecimal anno,
            @PathVariable String numero, Model model) {
        model.addAttribute("idStrut", idStrut);
        model.addAttribute("registro", registro);
        model.addAttribute("anno", anno);
        model.addAttribute("numero", numero);

        AroUnitaDoc ud = calcoloMonHelper.getUnitaDocIfExists(idStrut.longValue(), numero, anno, registro);
        AroVRicUnitaDocRowBean row = udHelper.getAroVRicUnitaDocRowBean(BigDecimal.valueOf(ud.getIdUnitaDoc()), null,
                null);
        AroVRicUnitaDocTableBean table = new AroVRicUnitaDocTableBean();
        table.add(row);

        OrgStrutRowBean strut = struttureEjb.getOrgStrutRowBean(row.getIdStrutUnitaDoc());
        UnitaDocumentarieForm form = new UnitaDocumentarieForm();
        form.getUnitaDocumentarieList().setTable(table);
        UnitaDocumentarieAction action = new UnitaDocumentarieAction();

        SessionManager.clearActionHistory(request.getSession());
        SessionCoreManager.setLastPublisher(request.getSession(), "");
        SessionManager.setForm(request.getSession(), form);
        SessionManager.setCurrentAction(request.getSession(), action.getControllerName());
        SessionManager.initMessageBox(request.getSession());
        SessionManager.addPrevExecutionToHistory(request.getSession(), true, false, null);
        User user = checkUser(request.getSession());
        user.setIdOrganizzazioneFoglia(row.getIdStrutUnitaDoc());
        Map<String, String> organizzazione = new LinkedHashMap<String, String>();
        organizzazione.put(WebConstants.Organizzazione.AMBIENTE.name(), strut.getString("nm_ambiente"));
        organizzazione.put(WebConstants.Organizzazione.ENTE.name(), strut.getString("nm_ente"));
        organizzazione.put(WebConstants.Organizzazione.STRUTTURA.name(), strut.getNmStrut());
        user.setOrganizzazioneMap(organizzazione);
        user.setConfigurazione(configurationHelper.getConfiguration());
        SessionManager.setUser(request.getSession(), user);
        try {
            authenticator.recuperoAutorizzazioni(request.getSession());
            res.sendRedirect(request.getServletContext().getContextPath() + "/" + action.getControllerName()
                    + "?operation=listNavigationOnClick&table=" + form.getUnitaDocumentarieList().getName()
                    + "&navigationEvent=" + UnitaDocumentarieAction.NE_DETTAGLIO_VIEW + "&riga=0");
        } catch (WebServiceException ex) {
            logger.error("Eccezione", ex);
            // TODO : Inviare a una pagina che indica l'impossibilità di caricare la pagina? Direi di si
        } catch (IOException ex) {
            logger.error("Errore nel caricamento del dettaglio unit\u00E0 documentaria", ex);
        }
    }

    private User checkUser(HttpSession session) {
        User user = (User) SessionManager.getUser(session);
        if (user != null) {
            logger.info("Login gi\u00E0 effettuato per l'utente " + user.getUsername());
        } else {
            try {
                user = authenticator.doLogin(session);
            } catch (SOAPFaultException ex) {
                user = null;
            }
        }
        return user;
    }
}
