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
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb.TipoUnitaDocEjb;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.MonitoraggioSinteticoAbstractAction;
import it.eng.parer.slite.gen.form.MonitoraggioForm;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.volume.utils.VolumeEnums;
import it.eng.parer.web.ejb.MonitoraggioSinteticoEjb;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.WebConstants;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.security.Secure;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Bonora_L
 */
public class MonitoraggioSinteticoAction extends MonitoraggioSinteticoAbstractAction {

    private static Logger log = LoggerFactory
            .getLogger(MonitoraggioSinteticoAbstractAction.class.getName());

    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioSinteticoEjb")
    private MonitoraggioSinteticoEjb monitSintEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoUnitaDocEjb")
    private TipoUnitaDocEjb tipoUnitaDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;

    @Override
    public void initOnClick() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JSONObject triggerRiepilogoVersamentiSinteticoId_ambienteOnTrigger() throws EMFError {
        getForm().getRiepilogoVersamentiSintetico().post(getRequest());
        BigDecimal idAmbiente = getForm().getRiepilogoVersamentiSintetico().getId_ambiente()
                .parse();
        if (idAmbiente != null) {
            OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(
                    getUser().getIdUtente(), idAmbiente.longValue(), Boolean.TRUE);
            getForm().getRiepilogoVersamentiSintetico().getId_ente().setDecodeMap(
                    DecodeMap.Factory.newInstance(tmpTableBeanEnte, "id_ente", "nm_ente"));

            if (tmpTableBeanEnte.size() == 1) {
                // Esiste solo un ente, la setto immediatamente e verifico le strutture
                getForm().getRiepilogoVersamentiSintetico().getId_ente()
                        .setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                OrgStrutTableBean tmpTableBeanStrut = struttureEjb.getOrgStrutTableBean(
                        getUser().getIdUtente(), tmpTableBeanEnte.getRow(0).getIdEnte(),
                        Boolean.TRUE);
                getForm().getRiepilogoVersamentiSintetico().getId_strut().setDecodeMap(
                        DecodeMap.Factory.newInstance(tmpTableBeanStrut, "id_strut", "nm_strut"));
                if (tmpTableBeanStrut.size() == 1) {
                    getForm().getRiepilogoVersamentiSintetico().getId_strut()
                            .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                    DecTipoUnitaDocTableBean tmpTableBeanTUD = tipoUnitaDocEjb
                            .getTipiUnitaDocAbilitati(getUser().getIdUtente(),
                                    tmpTableBeanStrut.getRow(0).getIdStrut());
                    getForm().getRiepilogoVersamentiSintetico().getId_tipo_unita_doc()
                            .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanTUD,
                                    "id_tipo_unita_doc", "nm_tipo_unita_doc"));
                } else {
                    getForm().getRiepilogoVersamentiSintetico().getId_tipo_unita_doc()
                            .setDecodeMap(new DecodeMap());
                }
            } else {
                getForm().getRiepilogoVersamentiSintetico().getId_strut()
                        .setDecodeMap(new DecodeMap());
                getForm().getRiepilogoVersamentiSintetico().getId_tipo_unita_doc()
                        .setDecodeMap(new DecodeMap());
            }
        } else {
            getForm().getRiepilogoVersamentiSintetico().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getRiepilogoVersamentiSintetico().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getRiepilogoVersamentiSintetico().getId_tipo_unita_doc()
                    .setDecodeMap(new DecodeMap());
        }

        return getForm().getRiepilogoVersamentiSintetico().asJSON();
    }

    @Override
    public JSONObject triggerRiepilogoVersamentiSinteticoId_enteOnTrigger() throws EMFError {
        getForm().getRiepilogoVersamentiSintetico().post(getRequest());
        BigDecimal idEnte = getForm().getRiepilogoVersamentiSintetico().getId_ente().parse();
        if (idEnte != null) {
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb
                    .getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.TRUE);
            getForm().getRiepilogoVersamentiSintetico().getId_strut().setDecodeMap(
                    DecodeMap.Factory.newInstance(tmpTableBeanStrut, "id_strut", "nm_strut"));
            if (tmpTableBeanStrut.size() == 1) {
                getForm().getRiepilogoVersamentiSintetico().getId_strut()
                        .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                DecTipoUnitaDocTableBean tmpTableBeanTUD = tipoUnitaDocEjb.getTipiUnitaDocAbilitati(
                        getUser().getIdUtente(), tmpTableBeanStrut.getRow(0).getIdStrut());
                getForm().getRiepilogoVersamentiSintetico().getId_tipo_unita_doc()
                        .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanTUD,
                                "id_tipo_unita_doc", "nm_tipo_unita_doc"));
            } else {
                getForm().getRiepilogoVersamentiSintetico().getId_tipo_unita_doc()
                        .setDecodeMap(new DecodeMap());
            }
        } else {
            getForm().getRiepilogoVersamentiSintetico().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getRiepilogoVersamentiSintetico().getId_tipo_unita_doc()
                    .setDecodeMap(new DecodeMap());
        }

        return getForm().getRiepilogoVersamentiSintetico().asJSON();
    }

    @Override
    public JSONObject triggerRiepilogoVersamentiSinteticoId_strutOnTrigger() throws EMFError {
        getForm().getRiepilogoVersamentiSintetico().post(getRequest());
        BigDecimal idStrut = getForm().getRiepilogoVersamentiSintetico().getId_strut().parse();
        if (idStrut != null) {
            DecTipoUnitaDocTableBean tmpTableBeanTUD = tipoUnitaDocEjb
                    .getTipiUnitaDocAbilitati(getUser().getIdUtente(), idStrut);
            getForm().getRiepilogoVersamentiSintetico().getId_tipo_unita_doc()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanTUD,
                            "id_tipo_unita_doc", "nm_tipo_unita_doc"));
        } else {
            getForm().getRiepilogoVersamentiSintetico().getId_tipo_unita_doc()
                    .setDecodeMap(new DecodeMap());
        }
        return getForm().getRiepilogoVersamentiSintetico().asJSON();
    }

    @Override
    public void generaRiepilogoVersSinteticoButton() throws EMFError {
        if (getForm().getRiepilogoVersamentiSintetico().postAndValidate(getRequest(),
                getMessageBox())) {
            BigDecimal idAmbiente = getForm().getRiepilogoVersamentiSintetico().getId_ambiente()
                    .parse();
            BigDecimal idEnte = getForm().getRiepilogoVersamentiSintetico().getId_ente().parse();
            BigDecimal idStruttura = getForm().getRiepilogoVersamentiSintetico().getId_strut()
                    .parse();
            BigDecimal idTipoUnitaDoc = getForm().getRiepilogoVersamentiSintetico()
                    .getId_tipo_unita_doc().parse();
            Set<String> fieldsSet = new HashSet<String>();
            getSession().setAttribute(WebConstants.PARAMETER_SESSION_GET_CNT, fieldsSet);

            calcolaRiepilogoSinteticoGlobale(idAmbiente, idEnte, idStruttura, idTipoUnitaDoc);
        }

        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void insertDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.MONITORAGGIO_RIEPILOGO_VERS_SINTETICO;
    }

    @Override
    public void process() throws EMFError {
    }

    @Override
    public void reloadAfterGoBack(String publisher) {
        try {
            BigDecimal idAmbiente = getForm().getRiepilogoVersamentiSintetico().getId_ambiente()
                    .parse();
            BigDecimal idEnte = getForm().getRiepilogoVersamentiSintetico().getId_ente().parse();
            BigDecimal idStruttura = getForm().getRiepilogoVersamentiSintetico().getId_strut()
                    .parse();
            BigDecimal idTipoUnitaDoc = getForm().getRiepilogoVersamentiSintetico()
                    .getId_tipo_unita_doc().parse();

            if (idTipoUnitaDoc != null) {
                getRequest().setAttribute("Id_tipo_unita_doc", idTipoUnitaDoc.intValue());
            }

            calcolaRiepilogoSinteticoGlobale(idAmbiente, idEnte, idStruttura, idTipoUnitaDoc);
        } catch (EMFError e) {
            log.error("Errore nel ricaricamento del riepilogo versamenti sintetico", e);
            getMessageBox().addError("Errore nel ricaricamento del riepilogo versamenti sintetico");
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.MONITORAGGIO_SINTETICO;
    }

    @Secure(action = "Menu.Monitoraggio.RiepilogoVersamentiSintetico")
    public void loadRiepilogoVersamentiSintetico() throws EMFError {
        try {
            // BigDecimal idStrut = new BigDecimal(getRequest().getParameter("idStrut") != null ?
            // (String)
            // getRequest().getParameter("idStrut") : "0");

            getUser().getMenu().reset();
            getUser().getMenu().select("Menu.Monitoraggio.RiepilogoVersamentiSintetico");

            // Resetto tutti i campi di riepilogo versamenti (filtri e totali)
            getForm().getRiepilogoVersamentiSintetico().reset();
            getSession().removeAttribute(WebConstants.PARAMETER_SESSION_GET_CNT);

            // Ricavo id struttura, ente ed ambiente attuali
            BigDecimal idStruttura = getUser().getIdOrganizzazioneFoglia();
            BigDecimal idEnte = monitSintEjb.getEnte(idStruttura);
            BigDecimal idAmbiente = monitSintEjb.getAmbiente(idEnte);

            System.out.println("Inizio calcolo ambienti abilitati");

            // Inizializzo le combo settando la struttura corrente
            // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
            OrgAmbienteTableBean tmpTableBeanAmbiente = ambienteEjb
                    .getAmbientiAbilitati(getUser().getIdUtente());

            System.out.println("Fine calcolo ambienti abilitati");
            System.out.println("Inizio calcolo enti abilitati");
            // Ricavo i valori della combo ENTE
            OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(
                    getUser().getIdUtente(), idAmbiente.longValue(), Boolean.TRUE);

            System.out.println("Fine calcolo enti abilitati");
            System.out.println("Inizio calcolo strutture abilitate");
            // Ricavo i valori della combo STRUTTURA
            OrgStrutTableBean tmpTableBeanStruttura = struttureEjb
                    .getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.TRUE);
            System.out.println("Fine calcolo strutture abilitate");
            System.out.println("Inizio calcolo tipi ud abilitate");
            // Ricavo i valori della combo TIPO UNITA' DOC.
            DecTipoUnitaDocTableBean tmpTableBeanTUD = tipoUnitaDocEjb
                    .getTipiUnitaDocAbilitati(getUser().getIdUtente(), idStruttura);

            System.out.println("Fine calcolo tipi ud abilitate");

            getForm().getRiepilogoVersamentiSintetico().getId_ambiente()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanAmbiente, "id_ambiente",
                            "nm_ambiente"));
            getForm().getRiepilogoVersamentiSintetico().getId_ente().setDecodeMap(
                    DecodeMap.Factory.newInstance(tmpTableBeanEnte, "id_ente", "nm_ente"));
            getForm().getRiepilogoVersamentiSintetico().getId_strut().setDecodeMap(
                    DecodeMap.Factory.newInstance(tmpTableBeanStruttura, "id_strut", "nm_strut"));
            getForm().getRiepilogoVersamentiSintetico().getId_tipo_unita_doc()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanTUD,
                            "id_tipo_unita_doc", "nm_tipo_unita_doc"));

            getForm().getRiepilogoVersamentiSintetico().getId_ambiente()
                    .setValue(idAmbiente.toString());
            getForm().getRiepilogoVersamentiSintetico().getId_ente().setValue(idEnte.toString());
            getForm().getRiepilogoVersamentiSintetico().getId_strut()
                    .setValue(idStruttura.toString());
            // Imposto le combo in editMode
            getForm().getRiepilogoVersamentiSintetico().setEditMode();
            getForm().getCalcolaTotaliButtonList().setEditMode();

            calcolaRiepilogoSinteticoGlobale(idAmbiente, idEnte, idStruttura, null);

        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore inatteso nel caricamento della pagina");
        }
        // Eseguo forward alla stessa pagina
        forwardToPublisher(Application.Publisher.MONITORAGGIO_RIEPILOGO_VERS_SINTETICO);
    }

    public void loadRiepilogoVersamentiSinteticoByStrut() throws EMFError {
        try {
            BigDecimal idStrut = new BigDecimal(getRequest().getParameter("idStrut") != null
                    ? (String) getRequest().getParameter("idStrut")
                    : "0");

            // getUser().getMenu().reset();
            // getUser().getMenu().select("Menu.Monitoraggio.RiepilogoVersamentiSintetico");
            // Resetto tutti i campi di riepilogo versamenti (filtri e totali)
            getForm().getRiepilogoVersamentiSintetico().reset();
            getSession().removeAttribute(WebConstants.PARAMETER_SESSION_GET_CNT);

            // Ricavo id struttura, ente ed ambiente attuali
            BigDecimal idStruttura = idStrut.equals(BigDecimal.ZERO)
                    ? getUser().getIdOrganizzazioneFoglia()
                    : idStrut;
            BigDecimal idEnte = monitSintEjb.getEnte(idStruttura);
            BigDecimal idAmbiente = monitSintEjb.getAmbiente(idEnte);

            // Inizializzo le combo settando la struttura corrente
            // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
            OrgAmbienteTableBean tmpTableBeanAmbiente = ambienteEjb
                    .getAmbientiAbilitati(getUser().getIdUtente());

            // Ricavo i valori della combo ENTE
            OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(
                    getUser().getIdUtente(), idAmbiente.longValue(), Boolean.TRUE);

            // Ricavo i valori della combo STRUTTURA
            OrgStrutTableBean tmpTableBeanStruttura = struttureEjb
                    .getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.TRUE);

            // Ricavo i valori della combo TIPO UNITA' DOC.
            DecTipoUnitaDocTableBean tmpTableBeanTUD = tipoUnitaDocEjb
                    .getTipiUnitaDocAbilitati(getUser().getIdUtente(), idStruttura);

            getForm().getRiepilogoVersamentiSintetico().getId_ambiente()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanAmbiente, "id_ambiente",
                            "nm_ambiente"));
            getForm().getRiepilogoVersamentiSintetico().getId_ente().setDecodeMap(
                    DecodeMap.Factory.newInstance(tmpTableBeanEnte, "id_ente", "nm_ente"));
            getForm().getRiepilogoVersamentiSintetico().getId_strut().setDecodeMap(
                    DecodeMap.Factory.newInstance(tmpTableBeanStruttura, "id_strut", "nm_strut"));
            getForm().getRiepilogoVersamentiSintetico().getId_tipo_unita_doc()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanTUD,
                            "id_tipo_unita_doc", "nm_tipo_unita_doc"));

            getForm().getRiepilogoVersamentiSintetico().getId_ambiente()
                    .setValue(idAmbiente.toString());
            getForm().getRiepilogoVersamentiSintetico().getId_ente().setValue(idEnte.toString());
            getForm().getRiepilogoVersamentiSintetico().getId_strut()
                    .setValue(idStruttura.toString());
            // Imposto le combo in editMode
            getForm().getRiepilogoVersamentiSintetico().setEditMode();
            getForm().getCalcolaTotaliButtonList().setEditMode();

            calcolaRiepilogoSinteticoGlobale(idAmbiente, idEnte, idStruttura, null);

        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore inatteso nel caricamento della pagina");
        }
        // Eseguo forward alla stessa pagina
        forwardToPublisher(Application.Publisher.MONITORAGGIO_RIEPILOGO_VERS_SINTETICO);
    }

    private void calcolaRiepilogoSinteticoGlobale(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStruttura, BigDecimal idTipoUnitaDoc) throws EMFError {
        Set<String> fieldsSet = (Set<String>) getSession()
                .getAttribute(WebConstants.PARAMETER_SESSION_GET_CNT);
        if (fieldsSet == null) {
            fieldsSet = new HashSet<String>();
            getSession().setAttribute(WebConstants.PARAMETER_SESSION_GET_CNT, fieldsSet);
        }
        LinkedHashMap<String, BaseRowInterface> formMap = monitSintEjb.calcolaRiepilogoSintetico(
                getUser().getIdUtente(), idAmbiente, idEnte, idStruttura, idTipoUnitaDoc,
                fieldsSet);
        getForm().getUnitaDocVersate()
                .copyFromBean(formMap.get(getForm().getUnitaDocVersate().getName()));
        getForm().getDocAggiunti().copyFromBean(formMap.get(getForm().getDocAggiunti().getName()));
        getForm().getVersamentiFalliti()
                .copyFromBean(formMap.get(getForm().getVersamentiFalliti().getName()));
        getForm().getAggiunteDocumentiFallite()
                .copyFromBean(formMap.get(getForm().getAggiunteDocumentiFallite().getName()));
        getForm().getUnitaDocDaVersFalliti()
                .copyFromBean(formMap.get(getForm().getUnitaDocDaVersFalliti().getName()));
        getForm().getDocDaVersFalliti()
                .copyFromBean(formMap.get(getForm().getDocDaVersFalliti().getName()));
        getForm().getUnitaDocAnnul()
                .copyFromBean(formMap.get(getForm().getUnitaDocAnnul().getName()));
    }

    public void listaDocumentiDaMenu() throws EMFError {
        try {
            getUser().getMenu().reset();
            getUser().getMenu().select("Menu.Monitoraggio.ListaDocumenti");

            // Resetto tutti i campi di riepilogo versamenti (filtri e totali)
            getForm().getRiepilogoVersamentiSintetico().reset();
            getSession().removeAttribute(WebConstants.PARAMETER_SESSION_GET_CNT);
            getSession().setAttribute("hideBackButton", true);

            // Ricavo id struttura, ente ed ambiente attuali
            BigDecimal idStruttura = getUser().getIdOrganizzazioneFoglia();
            BigDecimal idEnte = monitSintEjb.getEnte(idStruttura);
            BigDecimal idAmbiente = monitSintEjb.getAmbiente(idEnte);

            // Inizializzo le combo settando la struttura corrente
            // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
            OrgAmbienteTableBean tmpTableBeanAmbiente = ambienteEjb
                    .getAmbientiAbilitati(getUser().getIdUtente());

            // Ricavo i valori della combo ENTE
            OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(
                    getUser().getIdUtente(), idAmbiente.longValue(), Boolean.TRUE);

            // Ricavo i valori della combo STRUTTURA
            OrgStrutTableBean tmpTableBeanStruttura = struttureEjb
                    .getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.TRUE);

            // Ricavo i valori della combo TIPO UNITA' DOC.
            DecTipoUnitaDocTableBean tmpTableBeanTUD = tipoUnitaDocEjb
                    .getTipiUnitaDocAbilitati(getUser().getIdUtente(), idStruttura);

            getForm().getRiepilogoVersamentiSintetico().getId_ambiente()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanAmbiente, "id_ambiente",
                            "nm_ambiente"));
            getForm().getRiepilogoVersamentiSintetico().getId_ente().setDecodeMap(
                    DecodeMap.Factory.newInstance(tmpTableBeanEnte, "id_ente", "nm_ente"));
            getForm().getRiepilogoVersamentiSintetico().getId_strut().setDecodeMap(
                    DecodeMap.Factory.newInstance(tmpTableBeanStruttura, "id_strut", "nm_strut"));
            getForm().getRiepilogoVersamentiSintetico().getId_tipo_unita_doc()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanTUD,
                            "id_tipo_unita_doc", "nm_tipo_unita_doc"));

            getForm().getRiepilogoVersamentiSintetico().getId_ambiente()
                    .setValue(idAmbiente.toString());
            getForm().getRiepilogoVersamentiSintetico().getId_ente().setValue(idEnte.toString());
            getForm().getRiepilogoVersamentiSintetico().getId_strut()
                    .setValue(idStruttura.toString());
            // Imposto le combo in editMode
            getForm().getRiepilogoVersamentiSintetico().setEditMode();
            getForm().getCalcolaTotaliButtonList().setEditMode();
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore inatteso nel caricamento della pagina");
        }

        listaDocumenti();
    }

    /**
     * Metodo che esegue una redirectToAction a Monitoraggio - lista documenti in base ai parametri
     * selezionati
     *
     * @throws EMFError errore generico
     */
    public void listaDocumenti() throws EMFError {
        MonitoraggioForm form = new MonitoraggioForm();
        form.getRiepilogoVersamenti().getId_ambiente().setDecodeMap(
                getForm().getRiepilogoVersamentiSintetico().getId_ambiente().getDecodeMap());
        form.getRiepilogoVersamenti().getId_ambiente()
                .setValue(getForm().getRiepilogoVersamentiSintetico().getId_ambiente().getValue());

        form.getRiepilogoVersamenti().getId_ente().setDecodeMap(
                getForm().getRiepilogoVersamentiSintetico().getId_ente().getDecodeMap());
        form.getRiepilogoVersamenti().getId_ente()
                .setValue(getForm().getRiepilogoVersamentiSintetico().getId_ente().getValue());

        form.getRiepilogoVersamenti().getId_strut().setDecodeMap(
                getForm().getRiepilogoVersamentiSintetico().getId_strut().getDecodeMap());
        form.getRiepilogoVersamenti().getId_strut()
                .setValue(getForm().getRiepilogoVersamentiSintetico().getId_strut().getValue());

        form.getRiepilogoVersamenti().getId_tipo_unita_doc().setDecodeMap(
                getForm().getRiepilogoVersamentiSintetico().getId_tipo_unita_doc().getDecodeMap());
        form.getRiepilogoVersamenti().getId_tipo_unita_doc().setValue(
                getForm().getRiepilogoVersamentiSintetico().getId_tipo_unita_doc().getValue());

        String stato = getRequest().getParameter(WebConstants.PARAMETER_STATO);
        String tiCreazione = getRequest().getParameter(WebConstants.PARAMETER_CREAZIONE);
        String tipo = getRequest().getParameter(WebConstants.PARAMETER_TIPO);
        StringBuilder additionalParams = new StringBuilder();

        if (StringUtils.isNotBlank(stato) && StringUtils.isNotBlank(tiCreazione)
                && StringUtils.isNotBlank(tipo)) {
            if (StringUtils.isNotBlank(stato)) {
                switch (stato) {
                case WebConstants.PARAMETER_STATO_TUTTI:
                    break;
                default:
                    // Verifico che lo stato sia definito nell'enum
                    VolumeEnums.DocStatusEnum status = VolumeEnums.DocStatusEnum.valueOf(stato);
                    additionalParams.append("&statoDoc=").append(status.name());
                    break;
                }
            }

            if (StringUtils.isNotBlank(tiCreazione)) {
                additionalParams.append("&tiCreazione=").append(tiCreazione);
            }

            if (StringUtils.isNotBlank(tipo)) {
                switch (tipo) {
                case WebConstants.PARAMETER_TIPO_UD:
                    additionalParams.append("&tipoDoc=").append("1");
                    break;
                case WebConstants.PARAMETER_TIPO_DOC:
                    additionalParams.append("&tipoDoc=").append("0");
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Parametro inaspettato nella sezione di monitoraggio riepilogo versamenti sintetico");
                }
            }
            // Monitoraggio.html?operation=monitoraggioListe&tipoDoc=1&periodo=OGGI&pagina=1
            // Prepara la form per eseguire la redirectToAction a Monitoraggio
            redirectToAction(Application.Actions.MONITORAGGIO,
                    "?operation=monitoraggioListe&pagina=1" + additionalParams.toString(), form);
        }
    }

    /**
     * Metodo che esegue una redirectToAction a Monitoraggio - lista versamenti falliti in base ai
     * parametri selezionati
     *
     * @throws EMFError errore generico
     */
    public void listaVersamentiFalliti() throws EMFError {
        MonitoraggioForm form = new MonitoraggioForm();
        form.getFiltriDocumenti().setEditMode();
        form.getRiepilogoVersamenti().getId_ambiente().setDecodeMap(
                getForm().getRiepilogoVersamentiSintetico().getId_ambiente().getDecodeMap());
        form.getRiepilogoVersamenti().getId_ambiente()
                .setValue(getForm().getRiepilogoVersamentiSintetico().getId_ambiente().getValue());

        form.getRiepilogoVersamenti().getId_ente().setDecodeMap(
                getForm().getRiepilogoVersamentiSintetico().getId_ente().getDecodeMap());
        form.getRiepilogoVersamenti().getId_ente()
                .setValue(getForm().getRiepilogoVersamentiSintetico().getId_ente().getValue());

        form.getRiepilogoVersamenti().getId_strut().setDecodeMap(
                getForm().getRiepilogoVersamentiSintetico().getId_strut().getDecodeMap());
        form.getRiepilogoVersamenti().getId_strut()
                .setValue(getForm().getRiepilogoVersamentiSintetico().getId_strut().getValue());

        form.getRiepilogoVersamenti().getId_tipo_unita_doc().setDecodeMap(
                getForm().getRiepilogoVersamentiSintetico().getId_tipo_unita_doc().getDecodeMap());
        form.getRiepilogoVersamenti().getId_tipo_unita_doc().setValue(
                getForm().getRiepilogoVersamentiSintetico().getId_tipo_unita_doc().getValue());

        String stato = getRequest().getParameter(WebConstants.PARAMETER_STATO);
        String tiCreazione = getRequest().getParameter(WebConstants.PARAMETER_CREAZIONE);
        String tipo = getRequest().getParameter(WebConstants.PARAMETER_TIPO);
        StringBuilder additionalParams = new StringBuilder();

        if (StringUtils.isNotBlank(stato) && StringUtils.isNotBlank(tiCreazione)
                && StringUtils.isNotBlank(tipo)) {
            switch (stato) {
            case WebConstants.PARAMETER_STATO_TUTTI:
                break;
            default:
                // Verifico che lo stato sia definito nell'enum
                VolumeEnums.VersStatusEnum status = VolumeEnums.VersStatusEnum.valueOf(stato);
                switch (status) {
                case RISOLTO:
                    additionalParams.append("&risolti=1");
                    break;
                case NO_VERIF:
                    additionalParams.append("&risolti=0&verificati=0");
                    break;
                case VERIF:
                    additionalParams.append("&risolti=0&nonrisolubili=0&verificati=1");
                    break;
                case NO_RISOLUB:
                    additionalParams.append("&risolti=0&nonrisolubili=1&verificati=1");
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Parametro inaspettato nella sezione di monitoraggio riepilogo versamenti sintetico");
                }
                break;
            }

            switch (tiCreazione) {
            case WebConstants.PARAMETER_CREAZIONE_OGGI:
                additionalParams.append("&tiCreazione=OGGI");
                break;
            case WebConstants.PARAMETER_CREAZIONE_30GG:
                additionalParams.append("&tiCreazione=30gg");
                break;
            case WebConstants.PARAMETER_CREAZIONE_B30:
                additionalParams.append("&tiCreazione=" + WebConstants.PARAMETER_CREAZIONE_B30);
                break;
            default:
                throw new IllegalArgumentException(
                        "Parametro inaspettato nella sezione di monitoraggio riepilogo versamenti sintetico");
            }

            Constants.TipoSessione tipoSessione = Constants.TipoSessione.valueOf(tipo);
            switch (tipoSessione) {
            case VERSAMENTO:
                additionalParams.append("&tipoSes=" + tipo);
                break;
            case AGGIUNGI_DOCUMENTO:
                additionalParams.append("&tipoSes=" + tipo);
                break;
            default:
                throw new IllegalArgumentException(
                        "Parametro inaspettato nella sezione di monitoraggio riepilogo versamenti sintetico");
            }
            // Monitoraggio.html?operation=monitoraggioListe&periodo=OGGI&pagina=2
            // Prepara la form per eseguire la redirectToAction a Monitoraggio
            redirectToAction(Application.Actions.MONITORAGGIO,
                    "?operation=monitoraggioListe&pagina=2" + additionalParams.toString(), form);
        }
    }

    /**
     * Metodo che esegue una redirectToAction a Monitoraggio - lista unitÃ doc / documenti derivanti
     * da versamenti falliti in base ai parametri selezionati
     *
     * @throws EMFError errore generico
     */
    public void listaDocDaVersFalliti() throws EMFError {
        MonitoraggioForm form = new MonitoraggioForm();
        form.getFiltriDocumenti().setEditMode();
        form.getRiepilogoVersamenti().getId_ambiente().setDecodeMap(
                getForm().getRiepilogoVersamentiSintetico().getId_ambiente().getDecodeMap());
        form.getRiepilogoVersamenti().getId_ambiente()
                .setValue(getForm().getRiepilogoVersamentiSintetico().getId_ambiente().getValue());

        form.getRiepilogoVersamenti().getId_ente().setDecodeMap(
                getForm().getRiepilogoVersamentiSintetico().getId_ente().getDecodeMap());
        form.getRiepilogoVersamenti().getId_ente()
                .setValue(getForm().getRiepilogoVersamentiSintetico().getId_ente().getValue());

        form.getRiepilogoVersamenti().getId_strut().setDecodeMap(
                getForm().getRiepilogoVersamentiSintetico().getId_strut().getDecodeMap());
        form.getRiepilogoVersamenti().getId_strut()
                .setValue(getForm().getRiepilogoVersamentiSintetico().getId_strut().getValue());

        form.getRiepilogoVersamenti().getId_tipo_unita_doc().setDecodeMap(
                getForm().getRiepilogoVersamentiSintetico().getId_tipo_unita_doc().getDecodeMap());
        form.getRiepilogoVersamenti().getId_tipo_unita_doc().setValue(
                getForm().getRiepilogoVersamentiSintetico().getId_tipo_unita_doc().getValue());

        String stato = getRequest().getParameter(WebConstants.PARAMETER_STATO);
        String tipo = getRequest().getParameter(WebConstants.PARAMETER_TIPO);
        StringBuilder additionalParams = new StringBuilder();

        if (StringUtils.isNotBlank(stato) && StringUtils.isNotBlank(tipo)) {
            switch (stato) {
            case WebConstants.PARAMETER_STATO_TUTTI:
                break;
            default:
                // Verifico che lo stato sia definito nell'enum
                VolumeEnums.VersStatusEnum status = VolumeEnums.VersStatusEnum.valueOf(stato);
                switch (status) {
                case RISOLTO:
                    throw new IllegalArgumentException(
                            "Parametro inaspettato nella sezione di monitoraggio riepilogo versamenti sintetico");
                case NO_VERIF:
                    additionalParams.append("&flVerificato=0");
                    break;
                case VERIF:
                    additionalParams.append("&flVerificato=1&flNonRisolub=0");
                    break;
                case NO_RISOLUB:
                    additionalParams.append("&flVerificato=1&flNonRisolub=1");
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Parametro inaspettato nella sezione di monitoraggio riepilogo versamenti sintetico");
                }
                break;
            }

            additionalParams.append("&tipoLista=" + tipo);
            // Monitoraggio.html?operation=monitoraggioListe&pagina=3
            // Prepara la form per eseguire la redirectToAction a Monitoraggio
            redirectToAction(Application.Actions.MONITORAGGIO,
                    "?operation=monitoraggioListe&pagina=3" + additionalParams.toString(), form);
        }
    }

    public void listaDocAnnullati() throws EMFError {
        MonitoraggioForm form = new MonitoraggioForm();
        form.getFiltriDocumenti().setEditMode();
        form.getRiepilogoVersamenti().getId_ambiente().setDecodeMap(
                getForm().getRiepilogoVersamentiSintetico().getId_ambiente().getDecodeMap());
        form.getRiepilogoVersamenti().getId_ambiente()
                .setValue(getForm().getRiepilogoVersamentiSintetico().getId_ambiente().getValue());

        form.getRiepilogoVersamenti().getId_ente().setDecodeMap(
                getForm().getRiepilogoVersamentiSintetico().getId_ente().getDecodeMap());
        form.getRiepilogoVersamenti().getId_ente()
                .setValue(getForm().getRiepilogoVersamentiSintetico().getId_ente().getValue());

        form.getRiepilogoVersamenti().getId_strut().setDecodeMap(
                getForm().getRiepilogoVersamentiSintetico().getId_strut().getDecodeMap());
        form.getRiepilogoVersamenti().getId_strut()
                .setValue(getForm().getRiepilogoVersamentiSintetico().getId_strut().getValue());

        form.getRiepilogoVersamenti().getId_tipo_unita_doc().setDecodeMap(
                getForm().getRiepilogoVersamentiSintetico().getId_tipo_unita_doc().getDecodeMap());
        form.getRiepilogoVersamenti().getId_tipo_unita_doc().setValue(
                getForm().getRiepilogoVersamentiSintetico().getId_tipo_unita_doc().getValue());

        String stato = getRequest().getParameter(WebConstants.PARAMETER_STATO);
        String tipo = getRequest().getParameter(WebConstants.PARAMETER_TIPO);
        StringBuilder additionalParams = new StringBuilder();

        if (StringUtils.isNotBlank(stato) && StringUtils.isNotBlank(tipo)) {
            switch (stato) {
            case WebConstants.PARAMETER_STATO_TUTTI:
                break;
            default:
                // Verifico che lo stato sia definito nell'enum
                VolumeEnums.MonitAnnulStatusEnum status = VolumeEnums.MonitAnnulStatusEnum
                        .valueOf(stato);
                additionalParams.append("&tipoStato=" + status.name());
                break;
            }

            VolumeEnums.TipoVersAnnul tipoVers = VolumeEnums.TipoVersAnnul.valueOf(tipo);
            additionalParams.append("&tipoVers=" + tipoVers.name());
            // Monitoraggio.html?operation=monitoraggioListe&pagina=4
            // Prepara la form per eseguire la redirectToAction a Monitoraggio
            redirectToAction(Application.Actions.MONITORAGGIO,
                    "?operation=monitoraggioListe&pagina=4" + additionalParams.toString(), form);
        }
    }

    @Override
    public void calcTotUdVersateButton() throws EMFError {
        setRicalcoloSessionAttribute(MonitoraggioSinteticoEjb.fieldSetToPopulate.UNITA_DOC_VERSATE);
        if (getForm().getRiepilogoVersamentiSintetico().postAndValidate(getRequest(),
                getMessageBox())) {
            BigDecimal idAmbiente = getForm().getRiepilogoVersamentiSintetico().getId_ambiente()
                    .parse();
            BigDecimal idEnte = getForm().getRiepilogoVersamentiSintetico().getId_ente().parse();
            BigDecimal idStruttura = getForm().getRiepilogoVersamentiSintetico().getId_strut()
                    .parse();
            BigDecimal idTipoUnitaDoc = getForm().getRiepilogoVersamentiSintetico()
                    .getId_tipo_unita_doc().parse();
            calcolaRiepilogoSinteticoGlobale(idAmbiente, idEnte, idStruttura, idTipoUnitaDoc);
            // BaseRowInterface row = monitSintEjb.calcolaTot(getUser().getIdUtente(), idAmbiente,
            // idEnte, idStruttura,
            // idTipoUnitaDoc, MonitoraggioSinteticoEjb.fieldSetToPopulate.UNITA_DOC_VERSATE,
            // false);
            // getForm().getUnitaDocVersate().copyFromBean(row);
        }
    }

    @Override
    public void calcTotDocAggiuntiButton() throws Throwable {
        setRicalcoloSessionAttribute(MonitoraggioSinteticoEjb.fieldSetToPopulate.DOC_AGGIUNTI);
        if (getForm().getRiepilogoVersamentiSintetico().postAndValidate(getRequest(),
                getMessageBox())) {
            BigDecimal idAmbiente = getForm().getRiepilogoVersamentiSintetico().getId_ambiente()
                    .parse();
            BigDecimal idEnte = getForm().getRiepilogoVersamentiSintetico().getId_ente().parse();
            BigDecimal idStruttura = getForm().getRiepilogoVersamentiSintetico().getId_strut()
                    .parse();
            BigDecimal idTipoUnitaDoc = getForm().getRiepilogoVersamentiSintetico()
                    .getId_tipo_unita_doc().parse();
            calcolaRiepilogoSinteticoGlobale(idAmbiente, idEnte, idStruttura, idTipoUnitaDoc);
            // BaseRowInterface row = monitSintEjb.calcolaTot(getUser().getIdUtente(), idAmbiente,
            // idEnte, idStruttura,
            // idTipoUnitaDoc, MonitoraggioSinteticoEjb.fieldSetToPopulate.DOC_AGGIUNTI, false);
            // getForm().getDocAggiunti().copyFromBean(row);
        }
    }

    @Override
    public void calcTotVersFallitiButton() throws Throwable {
        setRicalcoloSessionAttribute(MonitoraggioSinteticoEjb.fieldSetToPopulate.VERS_FALLITI);
        if (getForm().getRiepilogoVersamentiSintetico().postAndValidate(getRequest(),
                getMessageBox())) {
            BigDecimal idAmbiente = getForm().getRiepilogoVersamentiSintetico().getId_ambiente()
                    .parse();
            BigDecimal idEnte = getForm().getRiepilogoVersamentiSintetico().getId_ente().parse();
            BigDecimal idStruttura = getForm().getRiepilogoVersamentiSintetico().getId_strut()
                    .parse();
            BigDecimal idTipoUnitaDoc = getForm().getRiepilogoVersamentiSintetico()
                    .getId_tipo_unita_doc().parse();
            calcolaRiepilogoSinteticoGlobale(idAmbiente, idEnte, idStruttura, idTipoUnitaDoc);
            // BaseRowInterface row = monitSintEjb.calcolaTot(getUser().getIdUtente(), idAmbiente,
            // idEnte, idStruttura,
            // idTipoUnitaDoc, MonitoraggioSinteticoEjb.fieldSetToPopulate.VERS_FALLITI, false);
            // getForm().getVersamentiFalliti().copyFromBean(row);
        }
    }

    @Override
    public void calcTotAggFallitiButton() throws Throwable {
        setRicalcoloSessionAttribute(
                MonitoraggioSinteticoEjb.fieldSetToPopulate.AGGIUNTE_DOC_FALLITE);
        if (getForm().getRiepilogoVersamentiSintetico().postAndValidate(getRequest(),
                getMessageBox())) {
            BigDecimal idAmbiente = getForm().getRiepilogoVersamentiSintetico().getId_ambiente()
                    .parse();
            BigDecimal idEnte = getForm().getRiepilogoVersamentiSintetico().getId_ente().parse();
            BigDecimal idStruttura = getForm().getRiepilogoVersamentiSintetico().getId_strut()
                    .parse();
            BigDecimal idTipoUnitaDoc = getForm().getRiepilogoVersamentiSintetico()
                    .getId_tipo_unita_doc().parse();
            calcolaRiepilogoSinteticoGlobale(idAmbiente, idEnte, idStruttura, idTipoUnitaDoc);
            // BaseRowInterface row = monitSintEjb.calcolaTot(getUser().getIdUtente(), idAmbiente,
            // idEnte, idStruttura,
            // idTipoUnitaDoc, MonitoraggioSinteticoEjb.fieldSetToPopulate.AGGIUNTE_DOC_FALLITE,
            // false);
            // getForm().getAggiunteDocumentiFallite().copyFromBean(row);
        }
    }

    @Override
    public void calcTotUdNonVersButton() throws Throwable {
        setRicalcoloSessionAttribute(
                MonitoraggioSinteticoEjb.fieldSetToPopulate.UNITA_DOC_VERS_FALLITI);
        if (getForm().getRiepilogoVersamentiSintetico().postAndValidate(getRequest(),
                getMessageBox())) {
            BigDecimal idAmbiente = getForm().getRiepilogoVersamentiSintetico().getId_ambiente()
                    .parse();
            BigDecimal idEnte = getForm().getRiepilogoVersamentiSintetico().getId_ente().parse();
            BigDecimal idStruttura = getForm().getRiepilogoVersamentiSintetico().getId_strut()
                    .parse();
            BigDecimal idTipoUnitaDoc = getForm().getRiepilogoVersamentiSintetico()
                    .getId_tipo_unita_doc().parse();
            calcolaRiepilogoSinteticoGlobale(idAmbiente, idEnte, idStruttura, idTipoUnitaDoc);
            // BaseRowInterface row = monitSintEjb.calcolaTot(getUser().getIdUtente(), idAmbiente,
            // idEnte, idStruttura,
            // idTipoUnitaDoc, MonitoraggioSinteticoEjb.fieldSetToPopulate.UNITA_DOC_VERS_FALLITI,
            // false);
            // getForm().getUnitaDocDaVersFalliti().copyFromBean(row);
        }
    }

    @Override
    public void calcTotDocNonVersButton() throws Throwable {
        setRicalcoloSessionAttribute(
                MonitoraggioSinteticoEjb.fieldSetToPopulate.DOC_AGGIUNTI_VERS_FALLITI);
        if (getForm().getRiepilogoVersamentiSintetico().postAndValidate(getRequest(),
                getMessageBox())) {
            BigDecimal idAmbiente = getForm().getRiepilogoVersamentiSintetico().getId_ambiente()
                    .parse();
            BigDecimal idEnte = getForm().getRiepilogoVersamentiSintetico().getId_ente().parse();
            BigDecimal idStruttura = getForm().getRiepilogoVersamentiSintetico().getId_strut()
                    .parse();
            BigDecimal idTipoUnitaDoc = getForm().getRiepilogoVersamentiSintetico()
                    .getId_tipo_unita_doc().parse();
            BaseRowInterface row = monitSintEjb.calcolaTot(getUser().getIdUtente(), idAmbiente,
                    idEnte, idStruttura, idTipoUnitaDoc,
                    MonitoraggioSinteticoEjb.fieldSetToPopulate.DOC_AGGIUNTI_VERS_FALLITI, false);
            getForm().getDocDaVersFalliti().copyFromBean(row);
        }
    }

    @Override
    public void calcTotVersUdAnnulButton() throws Throwable {
        setRicalcoloSessionAttribute(MonitoraggioSinteticoEjb.fieldSetToPopulate.UNITA_DOC_ANNUL);
        if (getForm().getRiepilogoVersamentiSintetico().postAndValidate(getRequest(),
                getMessageBox())) {
            BigDecimal idAmbiente = getForm().getRiepilogoVersamentiSintetico().getId_ambiente()
                    .parse();
            BigDecimal idEnte = getForm().getRiepilogoVersamentiSintetico().getId_ente().parse();
            BigDecimal idStruttura = getForm().getRiepilogoVersamentiSintetico().getId_strut()
                    .parse();
            BigDecimal idTipoUnitaDoc = getForm().getRiepilogoVersamentiSintetico()
                    .getId_tipo_unita_doc().parse();
            BaseRowInterface row = monitSintEjb.calcolaTot(getUser().getIdUtente(), idAmbiente,
                    idEnte, idStruttura, idTipoUnitaDoc,
                    MonitoraggioSinteticoEjb.fieldSetToPopulate.UNITA_DOC_ANNUL, false);
            getForm().getUnitaDocAnnul().copyFromBean(row);
        }
    }

    public void setRicalcoloSessionAttribute(MonitoraggioSinteticoEjb.fieldSetToPopulate field) {
        Set<String> fieldSet = (Set<String>) getSession()
                .getAttribute(WebConstants.PARAMETER_SESSION_GET_CNT);
        if (fieldSet == null) {
            fieldSet = new HashSet<String>();
        }
        fieldSet.add(field.name());
        getSession().setAttribute(WebConstants.PARAMETER_SESSION_GET_CNT, fieldSet);
    }

    @Override
    public void calcTotUdVersateB30Button() throws Throwable {
        setRicalcoloSessionAttribute(
                MonitoraggioSinteticoEjb.fieldSetToPopulate.UNITA_DOC_VERSATE_B30);
        if (getForm().getRiepilogoVersamentiSintetico().postAndValidate(getRequest(),
                getMessageBox())) {
            BigDecimal idAmbiente = getForm().getRiepilogoVersamentiSintetico().getId_ambiente()
                    .parse();
            BigDecimal idEnte = getForm().getRiepilogoVersamentiSintetico().getId_ente().parse();
            BigDecimal idStruttura = getForm().getRiepilogoVersamentiSintetico().getId_strut()
                    .parse();
            BigDecimal idTipoUnitaDoc = getForm().getRiepilogoVersamentiSintetico()
                    .getId_tipo_unita_doc().parse();
            calcolaRiepilogoSinteticoGlobale(idAmbiente, idEnte, idStruttura, idTipoUnitaDoc);
            // BaseRowInterface row = monitSintEjb.calcolaTot(getUser().getIdUtente(), idAmbiente,
            // idEnte, idStruttura,
            // idTipoUnitaDoc, MonitoraggioSinteticoEjb.fieldSetToPopulate.UNITA_DOC_VERSATE, true);
            // getForm().getUnitaDocVersate().copyFromBean(row);
        }
    }

    @Override
    public void calcTotDocAggiuntiB30Button() throws Throwable {
        setRicalcoloSessionAttribute(MonitoraggioSinteticoEjb.fieldSetToPopulate.DOC_AGGIUNTI_B30);
        if (getForm().getRiepilogoVersamentiSintetico().postAndValidate(getRequest(),
                getMessageBox())) {
            BigDecimal idAmbiente = getForm().getRiepilogoVersamentiSintetico().getId_ambiente()
                    .parse();
            BigDecimal idEnte = getForm().getRiepilogoVersamentiSintetico().getId_ente().parse();
            BigDecimal idStruttura = getForm().getRiepilogoVersamentiSintetico().getId_strut()
                    .parse();
            BigDecimal idTipoUnitaDoc = getForm().getRiepilogoVersamentiSintetico()
                    .getId_tipo_unita_doc().parse();
            calcolaRiepilogoSinteticoGlobale(idAmbiente, idEnte, idStruttura, idTipoUnitaDoc);
            // BaseRowInterface row = monitSintEjb.calcolaTot(getUser().getIdUtente(), idAmbiente,
            // idEnte, idStruttura,
            // idTipoUnitaDoc, MonitoraggioSinteticoEjb.fieldSetToPopulate.DOC_AGGIUNTI,true);
            // getForm().getDocAggiunti().copyFromBean(row);
        }
    }

    @Override
    public void calcTotVersFallitiB30Button() throws Throwable {
        setRicalcoloSessionAttribute(MonitoraggioSinteticoEjb.fieldSetToPopulate.VERS_FALLITI_B30);
        if (getForm().getRiepilogoVersamentiSintetico().postAndValidate(getRequest(),
                getMessageBox())) {
            BigDecimal idAmbiente = getForm().getRiepilogoVersamentiSintetico().getId_ambiente()
                    .parse();
            BigDecimal idEnte = getForm().getRiepilogoVersamentiSintetico().getId_ente().parse();
            BigDecimal idStruttura = getForm().getRiepilogoVersamentiSintetico().getId_strut()
                    .parse();
            BigDecimal idTipoUnitaDoc = getForm().getRiepilogoVersamentiSintetico()
                    .getId_tipo_unita_doc().parse();
            calcolaRiepilogoSinteticoGlobale(idAmbiente, idEnte, idStruttura, idTipoUnitaDoc);
            // BaseRowInterface row = monitSintEjb.calcolaTot(getUser().getIdUtente(), idAmbiente,
            // idEnte, idStruttura,
            // idTipoUnitaDoc, MonitoraggioSinteticoEjb.fieldSetToPopulate.VERS_FALLITI, true);
            // getForm().getVersamentiFalliti().copyFromBean(row);
        }
    }

    @Override
    public void calcTotAggFallitiB30Button() throws Throwable {
        setRicalcoloSessionAttribute(
                MonitoraggioSinteticoEjb.fieldSetToPopulate.AGGIUNTE_DOC_FALLITE_B30);
        if (getForm().getRiepilogoVersamentiSintetico().postAndValidate(getRequest(),
                getMessageBox())) {
            BigDecimal idAmbiente = getForm().getRiepilogoVersamentiSintetico().getId_ambiente()
                    .parse();
            BigDecimal idEnte = getForm().getRiepilogoVersamentiSintetico().getId_ente().parse();
            BigDecimal idStruttura = getForm().getRiepilogoVersamentiSintetico().getId_strut()
                    .parse();
            BigDecimal idTipoUnitaDoc = getForm().getRiepilogoVersamentiSintetico()
                    .getId_tipo_unita_doc().parse();
            calcolaRiepilogoSinteticoGlobale(idAmbiente, idEnte, idStruttura, idTipoUnitaDoc);
            // BaseRowInterface row = monitSintEjb.calcolaTot(getUser().getIdUtente(), idAmbiente,
            // idEnte, idStruttura,
            // idTipoUnitaDoc,
            // MonitoraggioSinteticoEjb.fieldSetToPopulate.AGGIUNTE_DOC_FALLITE,true);
            // getForm().getAggiunteDocumentiFallite().copyFromBean(row);
        }
    }
}
