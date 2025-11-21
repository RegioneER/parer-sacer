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

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.entity.IamUser;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.grantedEntity.UsrUser;
import it.eng.parer.sacerlog.ejb.helper.SacerLogHelper;
import it.eng.parer.sacerlog.entity.constraint.ConstLogEventoLoginUser;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.SceltaOrganizzazioneAbstractAction;
import it.eng.parer.slite.gen.tablebean.IamAbilOrganizRowBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.LoginLogHelper;
import it.eng.parer.web.helper.UserHelper;
import it.eng.parer.web.security.SacerAuthenticator;
import it.eng.parer.web.util.AuditSessionListener;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.User;
import it.eng.spagoLite.security.auth.PwdUtil;
import it.eng.util.EncryptionUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Filippini_M
 * @author Gilioli_P
 */
public class SceltaOrganizzazioneAction extends SceltaOrganizzazioneAbstractAction {

    private static Logger log = LoggerFactory.getLogger(SceltaOrganizzazioneAction.class.getName());

    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configHelper;
    @EJB(mappedName = "java:app/Parer-ejb/UserHelper")
    private UserHelper userHelper;
    @EJB(mappedName = "java:app/Parer-ejb/LoginLogHelper")
    private LoginLogHelper loginLogHelper;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogHelper")
    private SacerLogHelper sacerLogHelper;

    @Autowired
    private SacerAuthenticator authenticator;

    @Override
    public void process() throws EMFError {
        // Ricavo ambiente, ente e struttura di default in base all'utente
        User utente = getUser();
        // MEV#23905 - Associazione utente SPID con anagrafica utenti
        if (utente.isUtenteDaAssociare()) {
            gestisciUtenteDaAssociare(utente);
        } else {
            IamUser iamUser = null;
            try {
                // recupero l'ID utente nella tabella locale, partendo da nmUserid IAM univoco
                iamUser = userHelper.findIamUser(utente.getUsername());
            } catch (Exception e) {
                getMessageBox().addError(
                        "Impossibile accedere a Sacer. Riprovare ad effettuare il login tra qualche minuto.");
                getMessageBox().setViewMode(ViewMode.plain);
                getForm().getStrutture().getId_ambiente().setHidden(true);
                getForm().getStrutture().getId_ente().setHidden(true);
                getForm().getStrutture().getId_strut().setHidden(true);
                getRequest().setAttribute("errore", true);
                forwardToPublisher(Application.Publisher.SCELTA_STRUTTURA);
                return;
            }
            utente.setIdUtente(iamUser.getIdUserIam());
            // loggo, se necessario, l'avvenuto login nell'applicativo
            this.loginLogger(utente);
            IamAbilOrganizRowBean iamRB = struttureEjb.getAmbEnteStrutDefault(utente.getIdUtente());
            // Inizializzo le combo settando la struttura corrente
            OrgAmbienteTableBean tmpTableBeanAmbiente = null;
            OrgEnteTableBean tmpTableBeanEnte = null;
            OrgStrutTableBean tmpTableBeanStruttura = null;
            // Se è presente una struttura di default, popolo le combo
            if (iamRB != null) {
                long idAmbienteDefault = (Long) iamRB.getObject("idAmbiente");
                long idEnteDefault = (Long) iamRB.getObject("idEnte");
                BigDecimal idStrutDefault = iamRB.getIdOrganizApplic();
                try {
                    // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
                    tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(utente.getIdUtente());
                    // Ricavo i valori della combo ENTE
                    tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(utente.getIdUtente(),
                            idAmbienteDefault, Boolean.TRUE);
                    // Ricavo i valori della combo STRUTTURA
                    tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(utente.getIdUtente(),
                            new BigDecimal(idEnteDefault), Boolean.TRUE);
                } catch (Exception ex) {
                    log.error("Errore nel recupero dei valori di default dell'organizzazione: "
                            + ex.getMessage(), ex);
                }
                // Popolo le combo e setto i valori di default
                DecodeMap mappaAmbiente = new DecodeMap();
                mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
                getForm().getStrutture().getId_ambiente().setDecodeMap(mappaAmbiente);
                getForm().getStrutture().getId_ambiente().setValue("" + idAmbienteDefault);

                DecodeMap mappaEnte = new DecodeMap();
                mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
                getForm().getStrutture().getId_ente().setDecodeMap(mappaEnte);
                getForm().getStrutture().getId_ente().setValue("" + idEnteDefault);

                DecodeMap mappaStrut = new DecodeMap();
                mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
                getForm().getStrutture().getId_strut().setDecodeMap(mappaStrut);
                getForm().getStrutture().getId_strut().setValue(idStrutDefault.toString());
            } else {
                try {
                    OrgAmbienteTableBean ambientiTableBean = ambienteEjb
                            .getAmbientiAbilitati(utente.getIdUtente());
                    DecodeMap mappaAmbiente = new DecodeMap();
                    mappaAmbiente.populatedMap(ambientiTableBean, "id_ambiente", "nm_ambiente");
                    getForm().getStrutture().getId_ambiente().setDecodeMap(mappaAmbiente);
                    /*
                     * Se ho un solo ambiente lo setto già impostato nella combo e procedo con i
                     * controlli successivi
                     */
                    if (ambientiTableBean.size() == 1) {
                        getForm().getStrutture().getId_ambiente()
                                .setValue(ambientiTableBean.getRow(0).getIdAmbiente().toString());
                        BigDecimal idAmbiente = ambientiTableBean.getRow(0).getIdAmbiente();
                        checkUniqueAmbienteInCombo(utente.getIdUtente(), idAmbiente);
                    } /*
                       * altrimenti imposto la combo ambiente con i diversi valori ma senza averne
                       * selezionato uno in particolare e imposto vuote le altre combo
                       */ else {
                        getForm().getStrutture().getId_ente().setDecodeMap(new DecodeMap());
                        getForm().getStrutture().getId_strut().setDecodeMap(new DecodeMap());
                    }
                } catch (ParerUserError ex) {
                    getMessageBox().addError("Errore inatteso nel caricamento della pagina");
                    getMessageBox().setViewMode(ViewMode.plain);
                }

            }
            getForm().getStrutture().setEditMode();
            forwardToPublisher(Application.Publisher.SCELTA_STRUTTURA);
        }
    }

    // MEV#23905 - Associazione utente SPID con anagrafica utenti
    private void gestisciUtenteDaAssociare(User utente) throws EMFError {
        this.freeze();
        String username = "NON_PRESENTE";
        /*
         * MEV#22913 - Logging accessi SPID non autorizzati In caso di utente SPID lo username non
         * c'è ancora perché deve essere ancora associato Quindi si prende il suo codice fiscale se
         * presente, altrimenti una stringa fissa come username
         */
        if (utente.getCodiceFiscale() != null && !utente.getCodiceFiscale().isEmpty()) {
            username = utente.getCodiceFiscale().toUpperCase();
        }
        sacerLogHelper.insertEventoLoginUser(username, getIpClient(), new Date(),
                ConstLogEventoLoginUser.TipoEvento.BAD_CF.name(),
                "SACER - " + ConstLogEventoLoginUser.DS_EVENTO_BAD_CF_SPID, utente.getCognome(),
                utente.getNome(), utente.getCodiceFiscale(), utente.getExternalId(),
                utente.getEmail());

        String retURL = configHelper.getUrlBackAssociazioneUtenteCf();
        String salt = Base64.encodeBase64URLSafeString(PwdUtil.generateSalt());
        byte[] cfCriptato = EncryptionUtil.aesCrypt(utente.getCodiceFiscale(),
                EncryptionUtil.Aes.BIT_256);
        String f = Base64.encodeBase64URLSafeString(cfCriptato);
        byte[] cogCriptato = EncryptionUtil.aesCrypt(utente.getCognome(),
                EncryptionUtil.Aes.BIT_256);
        String c = Base64.encodeBase64URLSafeString(cogCriptato);
        byte[] nomeCriptato = EncryptionUtil.aesCrypt(utente.getNome(), EncryptionUtil.Aes.BIT_256);
        String n = Base64.encodeBase64URLSafeString(nomeCriptato);

        String hmac = EncryptionUtil.getHMAC(retURL + ":" + utente.getCodiceFiscale() + ":" + salt);
        try {
            this.getResponse()
                    .sendRedirect(configHelper.getValoreParamApplicByApplic(
                            CostantiDB.ParametroAppl.URL_ASSOCIAZIONE_UTENTE_CF) + "?r="
                            + Base64.encodeBase64URLSafeString(retURL.getBytes()) + "&h=" + hmac
                            + "&s=" + salt + "&f=" + f + "&c=" + c + "&n=" + n);
        } catch (IOException ex) {
            throw new EMFError("ERROR", "Errore nella sendRedirect verso Iam");
        }

    }

    public void backFromAssociation() throws EMFError {
        User user = getUser();
        if (user.getCodiceFiscale() != null && !user.getCodiceFiscale().isEmpty()) {
            List<UsrUser> l = userHelper.findByCodiceFiscale(user.getCodiceFiscale());
            if (l.size() == 1) {
                UsrUser us = l.iterator().next();
                user.setUtenteDaAssociare(false);
                user.setUsername(us.getNmUserid());
                user.setIdUtente(us.getIdUserIam());
                process();
                getMessageBox().addInfo(
                        "L'utente loggato è stato ricondotto con successo all'utenza Parer.");
                return;
            }
        }
        /*
         * Per sicurezza se qualcuno forza l'accesso con la URL senza provenire da IAM lo butto
         * fuori!
         */
        log.error(
                "Chiamata al metodo beckFromAssociation non autorizzata! Effettuo il logout forzato!");
        redirectToAction("Logout.html");
    }

    /**
     * Metodo utilizzato per controllare il valore nella combo ambiente quando questo è l'unico
     * presente e settare di conseguenza la combo ente
     *
     * @param idUtente   id utente
     * @param idAmbiente id ambiente
     *
     * @throws EMFError errore generico
     */
    public void checkUniqueAmbienteInCombo(long idUtente, BigDecimal idAmbiente) throws EMFError {
        if (idAmbiente != null) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(idUtente,
                    idAmbiente.longValue(), Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
            getForm().getStrutture().getId_ente().setDecodeMap(mappaEnte);

            // Se la combo ente ha un solo valore presente, lo imposto e faccio controllo su di essa
            if (tmpTableBeanEnte.size() == 1) {
                getForm().getStrutture().getId_ente()
                        .setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                checkUniqueEnteInCombo(idUtente, tmpTableBeanEnte.getRow(0).getIdEnte());
            } else {
                getForm().getStrutture().getId_strut().setDecodeMap(new DecodeMap());
            }
        }
    }

    /**
     * Metodo utilizzato per controllare il valore nella combo ente quando questo è l'unico presente
     * e settare di conseguenza la combo struttura
     *
     * @param idUtente id utente
     * @param idEnte   id ente
     *
     * @throws EMFError errore generico
     */
    public void checkUniqueEnteInCombo(long idUtente, BigDecimal idEnte) throws EMFError {
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb.getOrgStrutTableBean(idUtente,
                    idEnte, Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");
            getForm().getStrutture().getId_strut().setDecodeMap(mappaStrut);

            // Se la combo struttura ha un solo valore presente, lo imposto e faccio controllo su di
            // essa
            if (tmpTableBeanStrut.size() == 1) {
                getForm().getStrutture().getId_strut()
                        .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
            }
        }
    }

    @Override
    public void initOnClick() throws EMFError {
    }

    @Override
    public void selezionaStruttura() throws EMFError {

        getForm().getStrutture().post(getRequest());
        BigDecimal idStruttura = getForm().getStrutture().getId_strut().parse();
        if (idStruttura == null) {
            getMessageBox().setViewMode(ViewMode.plain);
            getMessageBox().addError("Selezionare la struttura su cui operare");
            forwardToPublisher(Application.Publisher.SCELTA_STRUTTURA);
        } else {
            User user = getUser();
            user.setIdOrganizzazioneFoglia(idStruttura);
            Map<String, String> organizzazione = new LinkedHashMap<String, String>();
            organizzazione.put(WebConstants.Organizzazione.AMBIENTE.name(),
                    getForm().getStrutture().getId_ambiente().getDecodedValue());
            organizzazione.put(WebConstants.Organizzazione.ENTE.name(),
                    getForm().getStrutture().getId_ente().getDecodedValue());
            organizzazione.put(WebConstants.Organizzazione.STRUTTURA.name(),
                    getForm().getStrutture().getId_strut().getDecodedValue());
            user.setOrganizzazioneMap(organizzazione);
            user.setConfigurazione(configHelper.getConfiguration());
            authenticator.recuperoAutorizzazioni(getSession());
            redirectToAction(Application.Actions.HOME);
        }
    }

    @Override
    public void insertDettaglio() throws EMFError {
    }

    @Override
    public void loadDettaglio() throws EMFError {
    }

    @Override
    public void undoDettaglio() throws EMFError {
    }

    @Override
    public void saveDettaglio() throws EMFError {
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
    }

    @Override
    public void elencoOnClick() throws EMFError {
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.SCELTA_STRUTTURA;
    }

    @Override
    public String getControllerName() {
        return Application.Actions.SCELTA_ORGANIZZAZIONE;
    }

    // DA NON RIMUOVERE! TUTTI GLI UTENTI DEVONO POTER SCEGLIERE LA STRUTTURA
    @Override
    public boolean isAuthorized(String destination) {
        return true;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
    }

    @Override
    public JSONObject triggerStruttureId_ambienteOnTrigger() throws EMFError {
        getForm().getStrutture().post(getRequest());
        BigDecimal idAmbiente = getForm().getStrutture().getId_ambiente().parse();
        if (idAmbiente != null) {
            long idUtente = getUser().getIdUtente();
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(idUtente,
                    idAmbiente.longValue(), Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
            getForm().getStrutture().getId_ente().setDecodeMap(mappaEnte);
            // Se ho un solo ente lo setto gi� impostato nella combo
            if (tmpTableBeanEnte.size() == 1) {
                getForm().getStrutture().getId_ente()
                        .setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                checkUniqueEnteInCombo(idUtente, tmpTableBeanEnte.getRow(0).getIdEnte());
            } else {
                getForm().getStrutture().getId_strut().setDecodeMap(new DecodeMap());
            }
        } else {
            getForm().getStrutture().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getStrutture().getId_strut().setDecodeMap(new DecodeMap());

        }
        return getForm().getStrutture().asJSON();
    }

    @Override
    public JSONObject triggerStruttureId_enteOnTrigger() throws EMFError {
        getForm().getStrutture().post(getRequest());
        BigDecimal idEnte = getForm().getStrutture().getId_ente().parse();
        if (idEnte != null) {
            long idUtente = getUser().getIdUtente();
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb.getOrgStrutTableBean(idUtente,
                    idEnte, Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");
            getForm().getStrutture().getId_strut().setDecodeMap(mappaStrut);
            // Se ho una sola struttura la setto gia impostata nella combo
            if (tmpTableBeanStrut.size() == 1) {
                getForm().getStrutture().getId_strut()
                        .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
            }
        } else {
            getForm().getStrutture().getId_strut().setDecodeMap(new DecodeMap());

        }
        return getForm().getStrutture().asJSON();
    }

    private void loginLogger(User utente) {
        if (utente.getConfigurazione() == null && utente.getOrganizzazioneMap() == null) {
            // è un login iniziale e non un ritorno sulla form per un cambio struttura.
            // Se fosse un cambio di struttura, queste variabili sarebbero valorizzate
            // poiché riportano i dati relativi alla struttura su cui l'utente
            // sta operando.

            HttpServletRequest request = getRequest();
            String ipVers = request.getHeader("RERFwFor");
            // cerco l'header custom della RER
            if (ipVers == null || ipVers.isEmpty()) {
                ipVers = request.getHeader("X-FORWARDED-FOR");
                // se non c'e`, uso l'header standard
            }
            if (ipVers == null || ipVers.isEmpty()) {
                ipVers = request.getRemoteAddr();
                // se non c'e` perche' la macchina e' esposta direttamente,
                // leggo l'IP fisico del chiamante
            }
            log.debug("Indirizzo da cui l'utente si connette: " + ipVers);
            loginLogHelper.writeLogEvento(utente, ipVers, LoginLogHelper.TipiEvento.LOGIN);
            getSession().setAttribute(AuditSessionListener.CLIENT_IP_ADDRESS, ipVers);
        }
    }
}
