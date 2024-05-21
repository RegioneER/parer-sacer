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

import it.eng.parer.amministrazioneStrutture.gestioneRegistro.ejb.RegistroEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoDoc.ejb.TipoDocumentoEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb.TipoUnitaDocEjb;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.MonitoraggioAggMetaAbstractAction;
import it.eng.parer.slite.gen.action.MonitoraggioSinteticoAbstractAction;
import it.eng.parer.slite.gen.form.MonitoraggioAggMetaForm;
import it.eng.parer.slite.gen.tablebean.AroWarnUpdUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.slite.gen.tablebean.VrsErrSesUpdUnitaDocErrTableBean;
import it.eng.parer.slite.gen.tablebean.VrsErrSesUpdUnitaDocKoTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisUpdCompUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisUpdDocUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisUpdKoRisoltiTableBean;
import it.eng.parer.slite.gen.viewbean.AroVVisUpdUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.MonVLisUpdUdErrRowBean;
import it.eng.parer.slite.gen.viewbean.MonVLisUpdUdErrTableBean;
import it.eng.parer.slite.gen.viewbean.MonVVisUdUpdKoRowBean;
import it.eng.parer.slite.gen.viewbean.MonVVisUpdUdErrRowBean;
import it.eng.parer.slite.gen.viewbean.MonVVisUpdUdKoRowBean;
import it.eng.parer.web.ejb.MonitoraggioAggMetaEjb;
import it.eng.parer.web.ejb.MonitoraggioAggMetaEjb.Stato;
import it.eng.parer.web.ejb.MonitoraggioAggMetaEjb.StatoGenerazioneIndiceAip;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.web.util.XmlPrettyPrintFormatter;
import it.eng.parer.web.validator.MonitoraggioAggMetaValidator;
import it.eng.parer.web.validator.TypeValidator.ChiaveBean;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.message.MessageBox;
import it.eng.spagoLite.security.Secure;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;

/**
 *
 * @author Gilioli_P
 */
public class MonitoraggioAggMetaAction extends MonitoraggioAggMetaAbstractAction {

    private static Logger logger = LoggerFactory.getLogger(MonitoraggioSinteticoAbstractAction.class.getName());

    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioAggMetaEjb")
    private MonitoraggioAggMetaEjb monitAggMetaEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoUnitaDocEjb")
    private TipoUnitaDocEjb tipoUnitaDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/RegistroEjb")
    private RegistroEjb registroEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoDocumentoEjb")
    private TipoDocumentoEjb tipoDocumentoEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configHelper;
    @EJB(mappedName = "java:app/Parer-ejb/ObjectStorageService")
    private ObjectStorageService objectStorageService;

    public static final String PAR_TI_CREAZIONE = "ti_creazione";
    public static final String TI_CREAZIONE_OGGI = "OGGI";
    public static final String TI_CREAZIONE_30GG = "30gg";
    public static final String TI_CREAZIONE_B30GG = "B30gg";

    public static final String PAR_TI_STATO = "ti_stato";
    public static final String TI_STATO_TOTALE = "TOTALE";
    public static final String TI_STATO_IN_ATTESA_SCHED = "IN_ATTESA_SCHED";
    public static final String TI_STATO_NON_SELEZ_SCHED = "NON_SELEZ_SCHED";
    public static final String TI_STATO_NON_RISOLUBILE = "NON_RISOLUBILE";
    public static final String TI_STATO_VERIFICATO = "VERIFICATO";
    public static final String TI_STATO_NON_VERIFICATO = "NON_VERIFICATO";

    @Override
    public void initOnClick() throws EMFError {
    }

    @Override
    public JSONObject triggerFiltriRicercaMonitoraggioAggMetaId_ambienteOnTrigger() throws EMFError {
        getForm().getFiltriRicercaMonitoraggioAggMeta().post(getRequest());
        BigDecimal idAmbiente = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ambiente().parse();

        if (idAmbiente != null) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean enteTableBean = ambienteEjb.getEntiAbilitatiAmbitoCateg(getUser().getIdUtente(),
                    idAmbiente, null, null, Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(enteTableBean, "id_ente", "nm_ente");
            getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ente().setDecodeMap(mappaEnte);
            getForm().getFiltriRicercaMonitoraggioAggMeta().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc().setValue("");
            getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_da().setValue("");
            getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_a().setValue("");
            getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaMonitoraggioAggMeta().getId_registro_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_doc().setDecodeMap(new DecodeMap());
        } else {
            getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaMonitoraggioAggMeta().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc().setValue("");
            getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_da().setValue("");
            getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_a().setValue("");
            getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaMonitoraggioAggMeta().getId_registro_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_doc().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriRicercaMonitoraggioAggMeta().asJSON();
    }

    @Override
    public JSONObject triggerFiltriRicercaMonitoraggioAggMetaId_enteOnTrigger() throws EMFError {
        getForm().getFiltriRicercaMonitoraggioAggMeta().post(getRequest());

        BigDecimal idEnte = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ente().parse();
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean strutTableBean = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte,
                    Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(strutTableBean, "id_strut", "nm_strut");
            getForm().getFiltriRicercaMonitoraggioAggMeta().getId_strut().setDecodeMap(mappaStrut);
            getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc().setValue("");
            getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_da().setValue("");
            getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_a().setValue("");
            getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaMonitoraggioAggMeta().getId_registro_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_doc().setDecodeMap(new DecodeMap());
        } else {
            getForm().getFiltriRicercaMonitoraggioAggMeta().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc().setValue("");
            getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_da().setValue("");
            getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_a().setValue("");
            getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaMonitoraggioAggMeta().getId_registro_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_doc().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriRicercaMonitoraggioAggMeta().asJSON();
    }

    @Override
    public JSONObject triggerFiltriRicercaMonitoraggioAggMetaId_strutOnTrigger() throws EMFError {
        MonitoraggioAggMetaForm.FiltriRicercaMonitoraggioAggMeta filtri = getForm()
                .getFiltriRicercaMonitoraggioAggMeta();
        // Eseguo la post del filtri
        filtri.post(getRequest());
        // Ricavo la struttura selezionata
        BigDecimal idStrut = filtri.getId_strut().parse();
        if (idStrut != null) {
            // Ricavo i tablebean
            DecRegistroUnitaDocTableBean registroUnitaDocTableBean = registroEjb
                    .getRegistriUnitaDocAbilitati(getUser().getIdUtente(), idStrut);
            DecTipoUnitaDocTableBean tipoUDTableBean = tipoUnitaDocEjb.getTipiUnitaDocAbilitati(getUser().getIdUtente(),
                    idStrut);
            DecTipoDocTableBean tipoDocTableBean = tipoDocumentoEjb.getDecTipoDocTableBean(idStrut, true, false);

            // Creo le mappe
            DecodeMap mappaRegistro = new DecodeMap();
            mappaRegistro.populatedMap(registroUnitaDocTableBean, "id_registro_unita_doc", "cd_registro_unita_doc");
            DecodeMap mappaTipoUD = new DecodeMap();
            mappaTipoUD.populatedMap(tipoUDTableBean, "id_tipo_unita_doc", "nm_tipo_unita_doc");
            DecodeMap mappaTipoDoc = new DecodeMap();
            mappaTipoDoc.populatedMap(tipoDocTableBean, "id_tipo_doc", "nm_tipo_doc");
            // Setto le combo
            filtri.getId_registro_unita_doc().setDecodeMap(mappaRegistro);
            filtri.getId_tipo_unita_doc().setDecodeMap(mappaTipoUD);
            filtri.getId_tipo_doc().setDecodeMap(mappaTipoDoc);
            filtri.getAa_key_unita_doc().setValue("");
            filtri.getAa_key_unita_doc_da().setValue("");
            filtri.getAa_key_unita_doc_a().setValue("");
        } else {
            filtri.getId_registro_unita_doc().setDecodeMap(new DecodeMap());
            filtri.getId_tipo_unita_doc().setDecodeMap(new DecodeMap());
            filtri.getId_tipo_doc().setDecodeMap(new DecodeMap());
            filtri.getAa_key_unita_doc().setValue("");
            filtri.getAa_key_unita_doc_da().setValue("");
            filtri.getAa_key_unita_doc_a().setValue("");
        }
        return getForm().getFiltriRicercaMonitoraggioAggMeta().asJSON();
    }

    @Override
    public void generaMonitoraggioAggMetaButton() throws EMFError {
        if (getForm().getFiltriRicercaMonitoraggioAggMeta().postAndValidate(getRequest(), getMessageBox())) {
            BigDecimal idAmbiente = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ambiente().parse();
            BigDecimal idEnte = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ente().parse();
            BigDecimal idStruttura = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_strut().parse();
            BigDecimal idTipoUnitaDoc = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_unita_doc().parse();
            BigDecimal idRegistroUnitaDoc = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_registro_unita_doc()
                    .parse();
            BigDecimal idTipoDocPrinc = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_doc().parse();
            BigDecimal aaKeyUnitaDoc = getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc().parse();
            BigDecimal aaKeyUnitaDocDa = getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_da()
                    .parse();
            BigDecimal aaKeyUnitaDocA = getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_a().parse();
            Set<String> fieldsSet = new HashSet<String>();
            getSession().setAttribute(WebConstants.PARAMETER_SESSION_GET_CNT_AGG_META, fieldsSet);

            // Controllo di aver inserito l'anno o il range di anni, non entrambi
            if (aaKeyUnitaDoc != null && (aaKeyUnitaDocDa != null || aaKeyUnitaDocA != null)) {
                getMessageBox().addError("Inserire l'anno o in alternativa il range di anni, non entrambi");
            }

            if ((aaKeyUnitaDocDa != null && aaKeyUnitaDocA == null)
                    || (aaKeyUnitaDocDa == null && aaKeyUnitaDocA != null)) {
                getMessageBox().addError("Range anni non completo");
            }

            if (aaKeyUnitaDocDa != null && aaKeyUnitaDocA != null) {
                if (aaKeyUnitaDocDa.compareTo(aaKeyUnitaDocA) > 0) {
                    getMessageBox().addError("Anno da superiore ad Anno a");
                }
            }

            if (!getMessageBox().hasError()) {
                calcolaRiepilogoAggMetaGlobale(idAmbiente, idEnte, idStruttura, aaKeyUnitaDoc, aaKeyUnitaDocDa,
                        aaKeyUnitaDocA, idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDocPrinc);
            }
        }

        forwardToPublisher(getLastPublisher());
    }

    @Secure(action = "Menu.Monitoraggio.RiepilogoAggiornamentoMetadati")
    public void loadRiepilogoAggiornamentoMetadati() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Monitoraggio.RiepilogoAggiornamentoMetadati");

        // Resetto tutti i campi di ricerca
        getForm().getFiltriRicercaMonitoraggioAggMeta().reset();
        getSession().removeAttribute(WebConstants.PARAMETER_SESSION_GET_CNT_AGG_META);

        /* Inizializzo il campo di ricerca ambiente, l'unico obbligatorio */
        OrgAmbienteTableBean tmpTableBeanAmbiente = null;
        try {
            tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());
        } catch (Exception ex) {
            logger.error("Errore nel recupero ambiente", ex);
        }
        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ambiente().setDecodeMap(mappaAmbiente);
        if (tmpTableBeanAmbiente.size() == 1) {
            getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ambiente()
                    .setValue("" + tmpTableBeanAmbiente.getRow(0).getIdAmbiente());
        }

        getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ente().setDecodeMap(new DecodeMap());
        getForm().getFiltriRicercaMonitoraggioAggMeta().getId_strut().setDecodeMap(new DecodeMap());

        // Imposto le combo in editMode
        getForm().getFiltriRicercaMonitoraggioAggMeta().setEditMode();
        getForm().getCalcolaTotaliButtonList().setEditMode();

        // Azzero i campi
        getForm().getAggiornamentiMetadati().clear();
        getForm().getAggiornamentiMetadatiFalliti().clear();

        // Eseguo forward alla stessa pagina
        forwardToPublisher(Application.Publisher.MONITORAGGIO_RIEPILOGO_AGG_META);
    }

    @Secure(action = "Menu.Logging.AggiornamentoMetadatiErrati")
    public void loadAggiornamentoMetadatiErrati() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Logging.AggiornamentoMetadatiErrati");

        // Resetto tutti i campi di ricerca
        getForm().getFiltriAggMetaErrati().reset();
        getForm().getFiltriAggMetaErrati().setEditMode();

        getForm().getFiltriAggMetaErrati().getTi_stato_ses().setDecodeMap(ComboGetter.getMappaStatoAggiornamento());
        getForm().getFiltriAggMetaErrati().getId_classe_err_sacer()
                .setDecodeMap(monitAggMetaEjb.getIdClasseErrSacerDecodeMap(
                        Arrays.asList(new String[] { MonitoraggioAggMetaEjb.tipoUsoClasseErrore.VERS_UNITA_DOC.name(),
                                MonitoraggioAggMetaEjb.tipoUsoClasseErrore.GENERICO.name() })));
        getForm().getFiltriAggMetaErrati().getId_err_sacer().setDecodeMap(new DecodeMap());

        getForm().getAggMetaErratiList().setTable(null);
        forwardToPublisher(getLastPublisher());

        // Eseguo forward alla stessa pagina
        forwardToPublisher(Application.Publisher.LISTA_AGG_META_ERRATI);
    }

    @Override
    public void insertDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
        // Tools | Templates.
    }

    @Override
    public void loadDettaglio() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                || getNavigationEvent().equals(ListAction.NE_NEXT) || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getAggMetaList().getName())) {
                BaseRow r = ((BaseTable) getForm().getAggMetaList().getTable()).getCurrentRow();
                BigDecimal idUpdUnitaDoc = r.getBigDecimal("id_upd_unita_doc");
                AroVVisUpdUnitaDocRowBean rb = monitAggMetaEjb.getAroVVisUpdUnitaDocRowBean(idUpdUnitaDoc);
                getForm().getDettaglioAggMeta().copyFromBean(rb);
                getForm().getDettaglioAggMeta().getScaricaXmlAggButton().setEditMode();
                getForm().getDettaglioAggMeta().getScaricaXmlAggButton().setDisableHourGlass(true);

                // Mi posiziono sul tab principale
                getForm().getAggMetaTabs().setCurrentTab(getForm().getAggMetaTabs().getInformazioniPrincipaliAggMeta());

                // Formatto gli xml di richiesta e risposta in modo tale che compaiano on-line in versione
                // "pretty-print"
                String xmlrich = rb.getBlXmlRich();
                String xmlrisp = rb.getBlXmlRisp();
                XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
                if (xmlrich != null && xmlrisp != null) {
                    xmlrich = formatter.prettyPrintWithDOM3LS(xmlrich);
                    xmlrisp = formatter.prettyPrintWithDOM3LS(xmlrisp);
                    getForm().getDettaglioAggMeta().getBl_xml_rich().setValue(xmlrich);
                    getForm().getDettaglioAggMeta().getBl_xml_risp().setValue(xmlrisp);
                }

                // MEV#29089
                addXmlVersUpdFromOStoAroVVisUpdUnitaDocBean(rb);
                // end MEV#29089

                String sistemaConservazione = configHelper
                        .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);

                CSVersatore tmpVers = new CSVersatore();
                tmpVers.setSistemaConservazione(sistemaConservazione);
                tmpVers.setAmbiente(rb.getNmAmbiente());
                tmpVers.setEnte(rb.getNmEnte());
                tmpVers.setStruttura(rb.getNmStrut());

                CSChiave tmpChiave = new CSChiave();
                tmpChiave.setTipoRegistro(rb.getCdRegistroKeyUnitaDoc());
                tmpChiave.setAnno(rb.getAaKeyUnitaDoc().longValue());
                tmpChiave.setNumero(rb.getCdKeyUnitaDoc());

                getForm().getDettaglioAggMeta().getDs_urn_upd_unita_doc()
                        .setValue(MessaggiWSFormat.formattaUrnUpdUniDoc(
                                MessaggiWSFormat.formattaBaseUrnUpd(MessaggiWSFormat.formattaUrnPartVersatore(tmpVers),
                                        MessaggiWSFormat.formattaUrnPartUnitaDoc(tmpChiave),
                                        rb.getPgUpdUnitaDoc().toString())));

                // Lista documenti aggiornati
                AroVLisUpdDocUnitaDocTableBean tDoc = monitAggMetaEjb.getAroVLisUpdDocUnitaDocTableBean(idUpdUnitaDoc);
                getForm().getDocumentiAggiornatiList().setTable(tDoc);
                getForm().getDocumentiAggiornatiList().getTable().first();
                getForm().getDocumentiAggiornatiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                // Lista componenti aggiornati
                AroVLisUpdCompUnitaDocTableBean tComp = monitAggMetaEjb
                        .getAroVLisUpdCompUnitaDocTableBean(idUpdUnitaDoc);
                getForm().getComponentiAggiornatiList().setTable(tComp);
                getForm().getComponentiAggiornatiList().getTable().first();
                getForm().getComponentiAggiornatiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                // Lista aggiornamenti metadati risolti
                AroVLisUpdKoRisoltiTableBean tRis = monitAggMetaEjb.getAroVLisUpdKoRisoltiTableBean(idUpdUnitaDoc);
                getForm().getAggMetaRisoltiList().setTable(tRis);
                getForm().getAggMetaRisoltiList().getTable().first();
                getForm().getAggMetaRisoltiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                // Lista warning rilevati
                AroWarnUpdUnitaDocTableBean tWar = monitAggMetaEjb.getAroWarnUpdUnitaDocTableBean(idUpdUnitaDoc);
                getForm().getWarningRilevatiList().setTable(tWar);
                getForm().getWarningRilevatiList().getTable().first();
                getForm().getWarningRilevatiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            } else if (getTableName().equals(getForm().getAggMetaFallitiList().getName())) {
                BaseRow r = ((BaseTable) getForm().getAggMetaFallitiList().getTable()).getCurrentRow();
                BigDecimal idSesUpdUnitaDocKo = r.getBigDecimal("id_ses_upd_unita_doc_ko");
                caricaDettaglioAggMetaFallito(idSesUpdUnitaDocKo);
            } else if (getTableName().equals(getForm().getUnitaDocAggMetaFallitiList().getName())) {
                BaseRow r = ((BaseTable) getForm().getUnitaDocAggMetaFallitiList().getTable()).getCurrentRow();
                BigDecimal idUpdUnitaDocKo = r.getBigDecimal("id_upd_unita_doc_ko");
                caricaDettaglioUnitaDocAggMetaFallito(idUpdUnitaDocKo);
            } else if (getTableName().equals(getForm().getAggMetaErratiList().getName())) {
                MonVLisUpdUdErrRowBean r = ((MonVLisUpdUdErrTableBean) getForm().getAggMetaErratiList().getTable())
                        .getCurrentRow();
                BigDecimal idSesUpdUnitaDocErr = r.getBigDecimal("id_ses_upd_unita_doc_err");
                caricaDettaglioAggMetaErrato(idSesUpdUnitaDocErr);
            }
        }
    }

    private void caricaDettaglioAggMetaFallito(BigDecimal idSesUpdUnitaDocKo) throws EMFError {
        MonVVisUpdUdKoRowBean rb = monitAggMetaEjb.getMonVVisUpdUdKoRowBean(idSesUpdUnitaDocKo);
        getForm().getDettaglioAggMetaFallito().getTi_stato_ses_upd_ko()
                .setDecodeMap(ComboGetter.getMappaStatoSesUpdKo());
        getForm().getDettaglioAggMetaFallito().copyFromBean(rb);
        getForm().getAggMetaFallitiList().setStatus(Status.view);
        getForm().getDettaglioAggMetaFallito().setStatus(Status.view);
        getForm().getDettaglioAggMetaFallito().setViewMode();
        getForm().getDettaglioAggMetaFallito().getScaricaXmlAggFallitoButton().setEditMode();
        getForm().getDettaglioAggMetaFallito().getScaricaXmlAggFallitoButton().setDisableHourGlass(true);

        // Mi posiziono sul tab principale
        getForm().getAggMetaFallitiTabs()
                .setCurrentTab(getForm().getAggMetaFallitiTabs().getInformazioniPrincipaliFalliti());

        // Formatto gli xml di richiesta e risposta in modo tale che compaiano on-line in versione "pretty-print"
        String xmlrich = rb.getBlXmlRich();
        String xmlrisp = rb.getBlXmlRisp();
        XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
        if (xmlrich != null && xmlrisp != null) {
            xmlrich = formatter.prettyPrintWithDOM3LS(xmlrich);
            xmlrisp = formatter.prettyPrintWithDOM3LS(xmlrisp);
            getForm().getDettaglioAggMetaFallito().getBl_xml_rich().setValue(xmlrich);
            getForm().getDettaglioAggMetaFallito().getBl_xml_risp().setValue(xmlrisp);
        }

        // MEV#29089
        aggiungiXmlSesAggMdFallitaDaObjectStorage(rb);
        // end MEV#29089

        // Ulteriori errori
        VrsErrSesUpdUnitaDocKoTableBean tl = monitAggMetaEjb
                .ricercaVersamentiErrSesUpdUnitaDocKo(rb.getIdSesUpdUnitaDocKo());
        getForm().getUlterioriErroriList().setTable(tl);
        getForm().getUlterioriErroriList().getTable().first();
        getForm().getUlterioriErroriList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
    }

    // MEV#22438
    private void caricaDettaglioUnitaDocAggMetaFallito(BigDecimal idUpdUnitaDocKo) throws EMFError {
        MonVVisUdUpdKoRowBean rb = monitAggMetaEjb.getMonVVisUdUpdKoRowBean(idUpdUnitaDocKo);
        getForm().getDettaglioUnitaDocAggMetaFallito().getTi_stato_upd_ud_ko()
                .setDecodeMap(ComboGetter.getMappaStatoUpdUdKo());
        getForm().getDettaglioUnitaDocAggMetaFallito().copyFromBean(rb);
        getForm().getUnitaDocAggMetaFallitiList().setStatus(Status.view);
        getForm().getDettaglioUnitaDocAggMetaFallito().setStatus(Status.view);
        getForm().getDettaglioUnitaDocAggMetaFallito().setViewMode();
        getForm().getDettaglioUnitaDocAggMetaFallito().getScaricaXmlAggFallitoLastButton().setEditMode();
        getForm().getDettaglioUnitaDocAggMetaFallito().getScaricaXmlAggFallitoLastButton().setDisableHourGlass(true);

        // Mi posiziono sul tab principale
        getForm().getUnitaDocAggMetaFallitiTabs()
                .setCurrentTab(getForm().getUnitaDocAggMetaFallitiTabs().getInformazioniPrincipaliUnitaDocAggFalliti());

        // Formatto gli xml di richiesta e risposta dell'ultimo aggiornamento in modo tale che compaiano on-line in
        // versione "pretty-print"
        String xmlrichlast = rb.getBlXmlRichLast();
        String xmlrisplast = rb.getBlXmlRispLast();
        XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
        if (xmlrichlast != null && xmlrisplast != null) {
            xmlrichlast = formatter.prettyPrintWithDOM3LS(xmlrichlast);
            xmlrisplast = formatter.prettyPrintWithDOM3LS(xmlrisplast);
            getForm().getDettaglioUnitaDocAggMetaFallito().getBl_xml_rich_last().setValue(xmlrichlast);
            getForm().getDettaglioUnitaDocAggMetaFallito().getBl_xml_risp_last().setValue(xmlrisplast);
        }

        // MEV#29089
        aggiungiXmlSesAggMdFallitaDaObjectStorageByUpdUd(rb);
        // end MEV#29089

        // Ulteriori errori
        List<Long> idSesUpdUnitaDocKoList = monitAggMetaEjb.ricercaVrsSesUpdUnitaDocKo(rb.getIdUpdUnitaDocKo());

        VrsErrSesUpdUnitaDocKoTableBean tl = monitAggMetaEjb
                .ricercaVersamentiErrSesUpdUnitaDocKo(idSesUpdUnitaDocKoList);
        getForm().getUlterioriErroriUnitaDocAggMetaFallitiList().setTable(tl);
        getForm().getUlterioriErroriUnitaDocAggMetaFallitiList().getTable().first();
        getForm().getUlterioriErroriUnitaDocAggMetaFallitiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

    }
    // end MEV#22438

    private void caricaDettaglioAggMetaErrato(BigDecimal idSesUpdUnitaDocErr) throws EMFError {
        MonVVisUpdUdErrRowBean rb = monitAggMetaEjb.getMonVVisUpdUdErrRowBean(idSesUpdUnitaDocErr);
        getForm().getDettaglioAggMetaErrato().getTi_stato_ses().setDecodeMap(ComboGetter.getMappaStatoAggiornamento());
        getForm().getDettaglioAggMetaErrato().copyFromBean(rb);
        getForm().getAggMetaErratiList().setStatus(Status.view);
        getForm().getDettaglioAggMetaErrato().setStatus(Status.view);
        getForm().getDettaglioAggMetaErrato().setViewMode();
        getForm().getDettaglioAggMetaErrato().getScaricaXmlAggErratoButton().setEditMode();
        getForm().getDettaglioAggMetaErrato().getScaricaXmlAggErratoButton().setDisableHourGlass(true);

        // Mi posiziono sul tab principale
        getForm().getAggMetaErratiTabs()
                .setCurrentTab(getForm().getAggMetaErratiTabs().getInformazioniPrincipaliErrati());

        // Formatto gli xml di richiesta e risposta in modo tale che compaiano on-line in versione "pretty-print"
        String xmlrich = rb.getBlXmlRich();
        String xmlrisp = rb.getBlXmlRisp();
        XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
        if (xmlrich != null && xmlrisp != null) {
            xmlrich = formatter.prettyPrintWithDOM3LS(xmlrich);
            xmlrisp = formatter.prettyPrintWithDOM3LS(xmlrisp);
            getForm().getDettaglioAggMetaErrato().getBl_xml_rich().setValue(xmlrich);
            getForm().getDettaglioAggMetaErrato().getBl_xml_risp().setValue(xmlrisp);
        }

        // MEV#29089
        aggiungiXmlSesAggMdErrataDaObjectStorage(rb);
        // end MEV#29089

        // Ulteriori errori
        VrsErrSesUpdUnitaDocErrTableBean tl = monitAggMetaEjb
                .ricercaVersamentiErrSesUpdUnitaDocErr(rb.getIdSesUpdUnitaDocErr());
        getForm().getUlterioriErroriErratiList().setTable(tl);
        getForm().getUlterioriErroriErratiList().getTable().first();
        getForm().getUlterioriErroriErratiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
    }

    // MEV#29089
    /**
     * Nel caso in cui il backend di salvataggio degli XML di versamento dell'aggiornamento metadati sia l'object
     * storage (gestito dal parametro <strong>applicativo</strong>) si possono verificare 2 casi:
     * <ul>
     * <li>gli xml sono <em>ancora</em> sul DB perché non ancora migrati</li>
     * <li>gli xml sono effettivamente sull'object storage</li>
     * </ul>
     * Se si avvera il secondo caso li devo recuperare
     *
     * @param riga
     *            AroVVisUpdUnitaDocRowBean
     */
    private void addXmlVersUpdFromOStoAroVVisUpdUnitaDocBean(AroVVisUpdUnitaDocRowBean riga) {
        boolean xmlVersVuoti = riga.getBlXmlRich() == null && riga.getBlXmlRisp() == null;
        /*
         * Se gli xml non sono ancora stati migrati, però, sono ancora presenti sulle tabelle
         */
        if (xmlVersVuoti) {
            Map<String, String> xmls = objectStorageService.getObjectXmlVersAggMd(riga.getIdUpdUnitaDoc().longValue());
            // recupero oggetti da O.S. (se presenti)
            if (!xmls.isEmpty()) {
                XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
                getForm().getDettaglioAggMeta().getBl_xml_rich()
                        .setValue(formatter.prettyPrintWithDOM3LS(xmls.get(CostantiDB.TipiXmlDati.RICHIESTA)));
                getForm().getDettaglioAggMeta().getBl_xml_risp()
                        .setValue(formatter.prettyPrintWithDOM3LS(xmls.get(CostantiDB.TipiXmlDati.RISPOSTA)));
            }
        }
    }

    /**
     * Nel caso in cui il backend di salvataggio degli XML di versamento aggiornamento metadati fallito sia l'object
     * storage (gestito dal parametro <strong>applicativo</strong>) si possono verificare 2 casi:
     * <ul>
     * <li>gli xml sono <em>ancora</em> sul DB perché non ancora migrati</li>
     * <li>gli xml sono effettivamente sull'object storage</li>
     * </ul>
     * Se si avvera il secondo caso li devo recuperare
     *
     * @param riga
     *            MonVVisUdUpdKoRowBean
     */
    private void aggiungiXmlSesAggMdFallitaDaObjectStorageByUpdUd(MonVVisUdUpdKoRowBean riga) {
        boolean xmlSesAggMdKoVuoti = riga.getBlXmlRichLast() == null && riga.getBlXmlRispLast() == null;
        /*
         * Se gli xml non sono ancora stati migrati, però, sono ancora presenti sulle tabelle
         */
        if (riga.getIdSesUpdUdKoLast() != null && xmlSesAggMdKoVuoti) {
            Map<String, String> xmls = objectStorageService
                    .getObjectSipAggMdFallito(riga.getIdSesUpdUdKoLast().longValue());
            // recupero oggetti se presenti su O.S
            if (!xmls.isEmpty()) {
                XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
                getForm().getDettaglioUnitaDocAggMetaFallito().getBl_xml_rich_last()
                        .setValue(formatter.prettyPrintWithDOM3LS(xmls.get(CostantiDB.TipiXmlDati.RICHIESTA)));
                getForm().getDettaglioUnitaDocAggMetaFallito().getBl_xml_risp_last()
                        .setValue(formatter.prettyPrintWithDOM3LS(xmls.get(CostantiDB.TipiXmlDati.RISPOSTA)));
            }
        }
    }

    /**
     * Nel caso in cui il backend di salvataggio degli XML di versamento aggiornamento metadati fallito sia l'object
     * storage (gestito dal parametro <strong>applicativo</strong>) si possono verificare 2 casi:
     * <ul>
     * <li>gli xml sono <em>ancora</em> sul DB perché non ancora migrati</li>
     * <li>gli xml sono effettivamente sull'object storage</li>
     * </ul>
     * Se si avvera il secondo caso li devo recuperare
     *
     * @param riga
     *            MonVVisUpdUdKoRowBean
     */
    private void aggiungiXmlSesAggMdFallitaDaObjectStorage(MonVVisUpdUdKoRowBean riga) {
        boolean xmlSesAggMdKoVuoti = riga.getBlXmlRich() == null && riga.getBlXmlRisp() == null;
        /*
         * Se gli xml non sono ancora stati migrati, però, sono ancora presenti sulle tabelle
         */
        if (riga.getIdSesUpdUnitaDocKo() != null && xmlSesAggMdKoVuoti) {
            Map<String, String> xmls = objectStorageService
                    .getObjectSipAggMdFallito(riga.getIdSesUpdUnitaDocKo().longValue());
            // recupero oggetti se presenti su O.S
            if (!xmls.isEmpty()) {
                XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
                getForm().getDettaglioAggMetaFallito().getBl_xml_rich()
                        .setValue(formatter.prettyPrintWithDOM3LS(xmls.get(CostantiDB.TipiXmlDati.RICHIESTA)));
                getForm().getDettaglioAggMetaFallito().getBl_xml_risp()
                        .setValue(formatter.prettyPrintWithDOM3LS(xmls.get(CostantiDB.TipiXmlDati.RISPOSTA)));
            }
        }
    }

    /**
     * Nel caso in cui il backend di salvataggio degli XML di versamento aggiornamento metadati errato sia l'object
     * storage (gestito dal parametro <strong>applicativo</strong>) si possono verificare 2 casi:
     * <ul>
     * <li>gli xml sono <em>ancora</em> sul DB perché non ancora migrati</li>
     * <li>gli xml sono effettivamente sull'object storage</li>
     * </ul>
     * Se si avvera il secondo caso li devo recuperare
     *
     * @param riga
     *            MonVVisUpdUdErrRowBean
     */
    private void aggiungiXmlSesAggMdErrataDaObjectStorage(MonVVisUpdUdErrRowBean riga) {
        boolean xmlSesAggMdErrVuoti = riga.getBlXmlRich() == null && riga.getBlXmlRisp() == null;
        /*
         * Se gli xml non sono ancora stati migrati, però, sono ancora presenti sulle tabelle
         */
        if (riga.getIdSesUpdUnitaDocErr() != null && xmlSesAggMdErrVuoti) {
            Map<String, String> xmls = objectStorageService
                    .getObjectSipAggMdErrato(riga.getIdSesUpdUnitaDocErr().longValue());
            // recupero oggetti se presenti su O.S
            if (!xmls.isEmpty()) {
                XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
                getForm().getDettaglioAggMetaErrato().getBl_xml_rich()
                        .setValue(formatter.prettyPrintWithDOM3LS(xmls.get(CostantiDB.TipiXmlDati.RICHIESTA)));
                getForm().getDettaglioAggMetaErrato().getBl_xml_risp()
                        .setValue(formatter.prettyPrintWithDOM3LS(xmls.get(CostantiDB.TipiXmlDati.RISPOSTA)));
            }
        }
    }
    // end MEV#29089

    @Override
    public void undoDettaglio() throws EMFError {
        if (getTableName().equals(getForm().getAggMetaFallitiList().getName())) {
            BigDecimal idSesUpdUnitaDocKo = getForm().getDettaglioAggMetaFallito().getId_ses_upd_unita_doc_ko().parse();
            caricaDettaglioAggMetaFallito(idSesUpdUnitaDocKo);
            forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META_FALLITO);
        } else if (getTableName().equals(getForm().getUnitaDocAggMetaFallitiList().getName())) {
            BigDecimal idUpdUnitaDocKo = getForm().getDettaglioUnitaDocAggMetaFallito().getId_upd_unita_doc_ko()
                    .parse();
            caricaDettaglioUnitaDocAggMetaFallito(idUpdUnitaDocKo);
            forwardToPublisher(Application.Publisher.DETTAGLIO_UNITA_DOC_AGG_META_FALLITO);
        } else if (getTableName().equals(getForm().getAggMetaErratiList().getName())) {
            BigDecimal idSesUpdUnitaDocErr = getForm().getDettaglioAggMetaErrato().getId_ses_upd_unita_doc_err()
                    .parse();
            caricaDettaglioAggMetaErrato(idSesUpdUnitaDocErr);
            forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META_ERRATO);
        }
    }

    @Override
    public void saveDettaglio() throws EMFError {
        if (getTableName().equals(getForm().getAggMetaFallitiList().getName())) {
            try {
                String statoPre = getForm().getDettaglioAggMetaFallito().getTi_stato_ses_upd_ko().parse();
                getForm().getDettaglioAggMetaFallito().post(getRequest());
                if (getForm().getDettaglioAggMetaFallito().validate(getMessageBox())) {
                    String statoPost = getForm().getDettaglioAggMetaFallito().getTi_stato_ses_upd_ko().parse();
                    monitAggMetaEjb.salvaStatoAggiornamentoFallito(
                            getForm().getDettaglioAggMetaFallito().getId_ses_upd_unita_doc_ko().parse(), statoPre,
                            statoPost);
                    getMessageBox().addInfo("Salvataggio modifica stato aggiornamento fallito eseguito con successo!");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                    getForm().getDettaglioAggMetaFallito().setViewMode();
                    getForm().getDettaglioAggMetaFallito().setStatus(BaseElements.Status.view);
                    getForm().getAggMetaFallitiList().setStatus(BaseElements.Status.view);
                }
            } catch (ParerInternalError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        if (getTableName().equals(getForm().getAggMetaFallitiList().getName())) {
            forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META_FALLITO);
        } else if (getTableName().equals(getForm().getUnitaDocAggMetaFallitiList().getName())) {
            forwardToPublisher(Application.Publisher.DETTAGLIO_UNITA_DOC_AGG_META_FALLITO);
        } else if (getTableName().equals(getForm().getAggMetaList().getName())) {
            forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META);
        } else if (getTableName().equals(getForm().getAggMetaErratiList().getName())) {
            forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META_ERRATO);
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.MONITORAGGIO_RIEPILOGO_AGG_META;
    }

    @Override
    public void process() throws EMFError {
    }

    @Override
    public void reloadAfterGoBack(String string) {
        try {
            if (getTableName() != null) {
                if (getTableName().equals(getForm().getAggMetaFallitiList().getName())) {
                    internalRicercaAggMetaFalliti();
                }
            }
        } catch (EMFError ex) {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.MONITORAGGIO_AGG_META;
    }

    private void calcolaRiepilogoAggMetaGlobale(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStruttura,
            BigDecimal aaKeyUnitaDoc, BigDecimal aaKeyUnitaDocDa, BigDecimal aaKeyUnitaDocA, BigDecimal idTipoUnitaDoc,
            BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDocPrinc) throws EMFError {
        BaseRow rowBean = monitAggMetaEjb.calcolaRiepilogoAggMeta(new BigDecimal(getUser().getIdUtente()), idAmbiente,
                idEnte, idStruttura, aaKeyUnitaDoc, aaKeyUnitaDocDa, aaKeyUnitaDocA, idTipoUnitaDoc, idRegistroUnitaDoc,
                idTipoDocPrinc);
        getForm().getAggiornamentiMetadati().copyFromBean(rowBean);
        getForm().getAggiornamentiMetadatiFalliti().copyFromBean(rowBean);
        // MEV#22438
        getForm().getUnitaDocAggiornamentiMetadatiFalliti().copyFromBean(rowBean);
        // end MEV#22438
    }

    private void calcolaRiepilogoAggMetaDataCorrente(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStruttura,
            BigDecimal aaKeyUnitaDoc, BigDecimal aaKeyUnitaDocDa, BigDecimal aaKeyUnitaDocA, BigDecimal idTipoUnitaDoc,
            BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDocPrinc) throws EMFError {
        BaseRow rowBean = monitAggMetaEjb.calcolaRiepilogoAggMetaDataCorrente(new BigDecimal(getUser().getIdUtente()),
                idAmbiente, idEnte, idStruttura, aaKeyUnitaDoc, aaKeyUnitaDocDa, aaKeyUnitaDocA, idTipoUnitaDoc,
                idRegistroUnitaDoc, idTipoDocPrinc);
        getForm().getAggiornamentiMetadati().getNi_upd_corr().setValue("" + rowBean.getBigDecimal("ni_upd_corr"));
        getForm().getAggiornamentiMetadati().getNi_upd_attesa_sched_corr()
                .setValue("" + rowBean.getBigDecimal("ni_upd_attesa_sched_corr"));
        getForm().getAggiornamentiMetadati().getNi_upd_nosel_sched_corr()
                .setValue("" + rowBean.getBigDecimal("ni_upd_nosel_sched_corr"));
    }

    private void calcolaRiepilogoAggMetaFalliti(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStruttura,
            BigDecimal aaKeyUnitaDoc, BigDecimal aaKeyUnitaDocDa, BigDecimal aaKeyUnitaDocA, BigDecimal idTipoUnitaDoc,
            BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDocPrinc) throws EMFError {
        BaseRow rowBean = monitAggMetaEjb.calcolaRiepilogoAggMetaFalliti(new BigDecimal(getUser().getIdUtente()),
                idAmbiente, idEnte, idStruttura, aaKeyUnitaDoc, aaKeyUnitaDocDa, aaKeyUnitaDocA, idTipoUnitaDoc,
                idRegistroUnitaDoc, idTipoDocPrinc);
        getForm().getAggiornamentiMetadatiFalliti().getNi_upd_ko_totali_corr()
                .setValue("" + rowBean.getBigDecimal("ni_upd_ko_totali_corr"));
        getForm().getAggiornamentiMetadatiFalliti().getNi_upd_ko_norisolub_corr()
                .setValue("" + rowBean.getBigDecimal("ni_upd_ko_norisolub_corr"));
        getForm().getAggiornamentiMetadatiFalliti().getNi_upd_ko_verif_corr()
                .setValue("" + rowBean.getBigDecimal("ni_upd_ko_verif_corr"));
        getForm().getAggiornamentiMetadatiFalliti().getNi_upd_ko_noverif_corr()
                .setValue("" + rowBean.getBigDecimal("ni_upd_ko_noverif_corr"));
    }

    // MEV#22438
    private void calcolaRiepilogoUnitaDocAggMetaFalliti(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStruttura, BigDecimal aaKeyUnitaDoc, BigDecimal aaKeyUnitaDocDa, BigDecimal aaKeyUnitaDocA,
            BigDecimal idTipoUnitaDoc, BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDocPrinc) throws EMFError {
        BaseRow rowBean = monitAggMetaEjb.calcolaRiepilogoUnitaDocAggMetaFalliti(
                new BigDecimal(getUser().getIdUtente()), idAmbiente, idEnte, idStruttura, aaKeyUnitaDoc,
                aaKeyUnitaDocDa, aaKeyUnitaDocA, idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDocPrinc);
        getForm().getUnitaDocAggiornamentiMetadatiFalliti().getNi_ud_upd_ko_totali()
                .setValue("" + rowBean.getBigDecimal("ni_ud_upd_ko_totali"));
        getForm().getUnitaDocAggiornamentiMetadatiFalliti().getNi_ud_upd_ko_norisolub()
                .setValue("" + rowBean.getBigDecimal("ni_ud_upd_ko_norisolub"));
        getForm().getUnitaDocAggiornamentiMetadatiFalliti().getNi_ud_upd_ko_verif()
                .setValue("" + rowBean.getBigDecimal("ni_ud_upd_ko_verif"));
        getForm().getUnitaDocAggiornamentiMetadatiFalliti().getNi_ud_upd_ko_noverif()
                .setValue("" + rowBean.getBigDecimal("ni_ud_upd_ko_noverif"));
    }
    // end MEV#22438

    @Override
    public void calcNumAggDataCorrenteButton() throws Throwable {
        if (getForm().getFiltriRicercaMonitoraggioAggMeta().postAndValidate(getRequest(), getMessageBox())) {
            BigDecimal idAmbiente = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ambiente().parse();
            BigDecimal idEnte = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ente().parse();
            BigDecimal idStruttura = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_strut().parse();
            BigDecimal idTipoUnitaDoc = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_unita_doc().parse();
            BigDecimal idRegistroUnitaDoc = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_registro_unita_doc()
                    .parse();
            BigDecimal idTipoDocPrinc = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_doc().parse();
            BigDecimal aaKeyUnitaDoc = getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc().parse();
            BigDecimal aaKeyUnitaDocDa = getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_da()
                    .parse();
            BigDecimal aaKeyUnitaDocA = getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_a().parse();
            // Controllo di aver inserito l'anno o il range di anni, non entrambi
            if (aaKeyUnitaDoc != null && (aaKeyUnitaDocDa != null || aaKeyUnitaDocA != null)) {
                getMessageBox().addError("Inserire l'anno o in alternativa il range di anni, non entrambi");
            }

            if ((aaKeyUnitaDocDa != null && aaKeyUnitaDocA == null)
                    || (aaKeyUnitaDocDa == null && aaKeyUnitaDocA != null)) {
                getMessageBox().addError("Range anni non completo");
            }

            if (aaKeyUnitaDocDa != null && aaKeyUnitaDocA != null) {
                if (aaKeyUnitaDocDa.compareTo(aaKeyUnitaDocA) > 0) {
                    getMessageBox().addError("Anno da superiore ad Anno a");
                }
            }

            if (!getMessageBox().hasError()) {
                calcolaRiepilogoAggMetaDataCorrente(idAmbiente, idEnte, idStruttura, aaKeyUnitaDoc, aaKeyUnitaDocDa,
                        aaKeyUnitaDocA, idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDocPrinc);
            }
        }
    }

    @Override
    public void calcTotAggFallitiButton() throws Throwable {
        if (getForm().getFiltriRicercaMonitoraggioAggMeta().postAndValidate(getRequest(), getMessageBox())) {
            BigDecimal idAmbiente = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ambiente().parse();
            BigDecimal idEnte = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ente().parse();
            BigDecimal idStruttura = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_strut().parse();
            BigDecimal idTipoUnitaDoc = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_unita_doc().parse();
            BigDecimal idRegistroUnitaDoc = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_registro_unita_doc()
                    .parse();
            BigDecimal idTipoDocPrinc = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_doc().parse();
            BigDecimal aaKeyUnitaDoc = getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc().parse();
            BigDecimal aaKeyUnitaDocDa = getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_da()
                    .parse();
            BigDecimal aaKeyUnitaDocA = getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_a().parse();
            // Controllo di aver inserito l'anno o il range di anni, non entrambi
            if (aaKeyUnitaDoc != null && (aaKeyUnitaDocDa != null || aaKeyUnitaDocA != null)) {
                getMessageBox().addError("Inserire l'anno o in alternativa il range di anni, non entrambi");
            }

            if ((aaKeyUnitaDocDa != null && aaKeyUnitaDocA == null)
                    || (aaKeyUnitaDocDa == null && aaKeyUnitaDocA != null)) {
                getMessageBox().addError("Range anni non completo");
            }

            if (aaKeyUnitaDocDa != null && aaKeyUnitaDocA != null) {
                if (aaKeyUnitaDocDa.compareTo(aaKeyUnitaDocA) > 0) {
                    getMessageBox().addError("Anno da superiore ad Anno a");
                }
            }

            if (!getMessageBox().hasError()) {
                calcolaRiepilogoAggMetaFalliti(idAmbiente, idEnte, idStruttura, aaKeyUnitaDoc, aaKeyUnitaDocDa,
                        aaKeyUnitaDocA, idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDocPrinc);
            }
        }
    }

    // MEV#22438
    @Override
    public void calcTotUdAggFallitiButton() throws Throwable {
        if (getForm().getFiltriRicercaMonitoraggioAggMeta().postAndValidate(getRequest(), getMessageBox())) {
            BigDecimal idAmbiente = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ambiente().parse();
            BigDecimal idEnte = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ente().parse();
            BigDecimal idStruttura = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_strut().parse();
            BigDecimal idTipoUnitaDoc = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_unita_doc().parse();
            BigDecimal idRegistroUnitaDoc = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_registro_unita_doc()
                    .parse();
            BigDecimal idTipoDocPrinc = getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_doc().parse();
            BigDecimal aaKeyUnitaDoc = getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc().parse();
            BigDecimal aaKeyUnitaDocDa = getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_da()
                    .parse();
            BigDecimal aaKeyUnitaDocA = getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_a().parse();
            // Controllo di aver inserito l'anno o il range di anni, non entrambi
            if (aaKeyUnitaDoc != null && (aaKeyUnitaDocDa != null || aaKeyUnitaDocA != null)) {
                getMessageBox().addError("Inserire l'anno o in alternativa il range di anni, non entrambi");
            }

            if ((aaKeyUnitaDocDa != null && aaKeyUnitaDocA == null)
                    || (aaKeyUnitaDocDa == null && aaKeyUnitaDocA != null)) {
                getMessageBox().addError("Range anni non completo");
            }

            if (aaKeyUnitaDocDa != null && aaKeyUnitaDocA != null) {
                if (aaKeyUnitaDocDa.compareTo(aaKeyUnitaDocA) > 0) {
                    getMessageBox().addError("Anno da superiore ad Anno a");
                }
            }

            if (!getMessageBox().hasError()) {
                calcolaRiepilogoUnitaDocAggMetaFalliti(idAmbiente, idEnte, idStruttura, aaKeyUnitaDoc, aaKeyUnitaDocDa,
                        aaKeyUnitaDocA, idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDocPrinc);
            }
        }
    }
    // end MEV#22438

    @Override
    public JSONObject triggerFiltriAggMetaId_ambienteOnTrigger() throws EMFError {
        getForm().getFiltriAggMeta().post(getRequest());
        BigDecimal idAmbiente = getForm().getFiltriAggMeta().getId_ambiente().parse();

        if (idAmbiente != null) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean enteTableBean = ambienteEjb.getEntiAbilitatiAmbitoCateg(getUser().getIdUtente(),
                    idAmbiente, null, null, Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(enteTableBean, "id_ente", "nm_ente");
            getForm().getFiltriAggMeta().getId_ente().setDecodeMap(mappaEnte);
            getForm().getFiltriAggMeta().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getFiltriAggMeta().getAa_key_unita_doc().setValue("");
            getForm().getFiltriAggMeta().getAa_key_unita_doc_da().setValue("");
            getForm().getFiltriAggMeta().getAa_key_unita_doc_a().setValue("");
            getForm().getFiltriAggMeta().getId_tipo_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getFiltriAggMeta().getId_registro_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getFiltriAggMeta().getId_tipo_doc().setDecodeMap(new DecodeMap());
        } else {
            getForm().getFiltriAggMeta().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriAggMeta().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getFiltriAggMeta().getAa_key_unita_doc().setValue("");
            getForm().getFiltriAggMeta().getAa_key_unita_doc_da().setValue("");
            getForm().getFiltriAggMeta().getAa_key_unita_doc_a().setValue("");
            getForm().getFiltriAggMeta().getId_tipo_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getFiltriAggMeta().getId_registro_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getFiltriAggMeta().getId_tipo_doc().setDecodeMap(new DecodeMap());
        }

        return getForm().getFiltriAggMeta().asJSON();
    }

    @Override
    public JSONObject triggerFiltriAggMetaId_enteOnTrigger() throws EMFError {
        getForm().getFiltriAggMeta().post(getRequest());

        BigDecimal idEnte = getForm().getFiltriAggMeta().getId_ente().parse();
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean strutTableBean = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte,
                    Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(strutTableBean, "id_strut", "nm_strut");
            getForm().getFiltriAggMeta().getId_strut().setDecodeMap(mappaStrut);
            getForm().getFiltriAggMeta().getAa_key_unita_doc().setValue("");
            getForm().getFiltriAggMeta().getAa_key_unita_doc_da().setValue("");
            getForm().getFiltriAggMeta().getAa_key_unita_doc_a().setValue("");
            getForm().getFiltriAggMeta().getId_tipo_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getFiltriAggMeta().getId_registro_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getFiltriAggMeta().getId_tipo_doc().setDecodeMap(new DecodeMap());
        } else {
            getForm().getFiltriAggMeta().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getFiltriAggMeta().getAa_key_unita_doc().setValue("");
            getForm().getFiltriAggMeta().getAa_key_unita_doc_da().setValue("");
            getForm().getFiltriAggMeta().getAa_key_unita_doc_a().setValue("");
            getForm().getFiltriAggMeta().getId_tipo_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getFiltriAggMeta().getId_registro_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getFiltriAggMeta().getId_tipo_doc().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriAggMeta().asJSON();
    }

    @Override
    public JSONObject triggerFiltriAggMetaId_strutOnTrigger() throws EMFError {
        MonitoraggioAggMetaForm.FiltriAggMeta filtri = getForm().getFiltriAggMeta();
        // Eseguo la post del filtri
        filtri.post(getRequest());
        // Ricavo la struttura selezionata
        BigDecimal idStrut = filtri.getId_strut().parse();
        if (idStrut != null) {
            // Ricavo i tablebean
            DecRegistroUnitaDocTableBean registroUnitaDocTableBean = registroEjb
                    .getRegistriUnitaDocAbilitati(getUser().getIdUtente(), idStrut);
            DecTipoUnitaDocTableBean tipoUDTableBean = tipoUnitaDocEjb.getTipiUnitaDocAbilitati(getUser().getIdUtente(),
                    idStrut);
            DecTipoDocTableBean tipoDocTableBean = tipoDocumentoEjb.getDecTipoDocTableBean(idStrut, true, false);

            // Creo le mappe
            DecodeMap mappaRegistro = new DecodeMap();
            mappaRegistro.populatedMap(registroUnitaDocTableBean, "id_registro_unita_doc", "cd_registro_unita_doc");
            DecodeMap mappaTipoUD = new DecodeMap();
            mappaTipoUD.populatedMap(tipoUDTableBean, "id_tipo_unita_doc", "nm_tipo_unita_doc");
            DecodeMap mappaTipoDoc = new DecodeMap();
            mappaTipoDoc.populatedMap(tipoDocTableBean, "id_tipo_doc", "nm_tipo_doc");
            // Setto le combo
            filtri.getId_registro_unita_doc().setDecodeMap(mappaRegistro);
            filtri.getId_tipo_unita_doc().setDecodeMap(mappaTipoUD);
            filtri.getId_tipo_doc().setDecodeMap(mappaTipoDoc);
            filtri.getAa_key_unita_doc().setValue("");
            filtri.getAa_key_unita_doc_da().setValue("");
            filtri.getAa_key_unita_doc_a().setValue("");
        } else {
            filtri.getId_registro_unita_doc().setDecodeMap(new DecodeMap());
            filtri.getId_tipo_unita_doc().setDecodeMap(new DecodeMap());
            filtri.getId_tipo_doc().setDecodeMap(new DecodeMap());
            filtri.getAa_key_unita_doc().setValue("");
            filtri.getAa_key_unita_doc_da().setValue("");
            filtri.getAa_key_unita_doc_a().setValue("");
        }
        return filtri.asJSON();
    }

    @Override
    public void ricercaAggMetaButton() throws EMFError {
        if (getForm().getFiltriAggMeta().postAndValidate(getRequest(), getMessageBox())) {
            internalRicercaAggMeta();
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void ricercaAggMetaFallitiButton() throws EMFError {
        if (getForm().getFiltriAggMeta().postAndValidate(getRequest(), getMessageBox())) {
            internalRicercaAggMetaFalliti();
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void ricercaUnitaDocAggMetaFallitiButton() throws EMFError {
        if (getForm().getFiltriAggMeta().postAndValidate(getRequest(), getMessageBox())) {
            internalRicercaUnitaDocAggMetaFalliti();
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void tabInformazioniPrincipaliFallitiOnClick() throws EMFError {
        getForm().getAggMetaFallitiTabs()
                .setCurrentTab(getForm().getAggMetaFallitiTabs().getInformazioniPrincipaliFalliti());
        forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META_FALLITO);
    }

    @Override
    public void tabIndiceSipAggiornamentoFallitiOnClick() throws EMFError {
        getForm().getAggMetaFallitiTabs()
                .setCurrentTab(getForm().getAggMetaFallitiTabs().getIndiceSipAggiornamentoFalliti());
        forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META_FALLITO);
    }

    @Override
    public void tabEsitoNegativoVersamentoFallitiOnClick() throws EMFError {
        getForm().getAggMetaFallitiTabs()
                .setCurrentTab(getForm().getAggMetaFallitiTabs().getEsitoNegativoVersamentoFalliti());
        forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META_FALLITO);
    }

    @Override
    public void tabListaUlterioriErroriFallitiOnClick() throws EMFError {
        getForm().getAggMetaFallitiTabs()
                .setCurrentTab(getForm().getAggMetaFallitiTabs().getListaUlterioriErroriFalliti());
        forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META_FALLITO);
    }

    // MEV#22438
    @Override
    public void tabInformazioniPrincipaliUnitaDocAggFallitiOnClick() throws EMFError {
        getForm().getUnitaDocAggMetaFallitiTabs()
                .setCurrentTab(getForm().getUnitaDocAggMetaFallitiTabs().getInformazioniPrincipaliUnitaDocAggFalliti());
        forwardToPublisher(Application.Publisher.DETTAGLIO_UNITA_DOC_AGG_META_FALLITO);
    }

    @Override
    public void tabListaUlterioriErroriUnitaDocAggFallitiOnClick() throws EMFError {
        getForm().getUnitaDocAggMetaFallitiTabs()
                .setCurrentTab(getForm().getUnitaDocAggMetaFallitiTabs().getListaUlterioriErroriUnitaDocAggFalliti());
        forwardToPublisher(Application.Publisher.DETTAGLIO_UNITA_DOC_AGG_META_FALLITO);
    }
    // end MEV#22438

    @Override
    public void tabInformazioniPrincipaliErratiOnClick() throws EMFError {
        getForm().getAggMetaErratiTabs()
                .setCurrentTab(getForm().getAggMetaErratiTabs().getInformazioniPrincipaliErrati());
        forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META_ERRATO);
    }

    @Override
    public void tabIndiceSipAggiornamentoErratiOnClick() throws EMFError {
        getForm().getAggMetaErratiTabs()
                .setCurrentTab(getForm().getAggMetaErratiTabs().getIndiceSipAggiornamentoErrati());
        forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META_ERRATO);
    }

    @Override
    public void tabEsitoNegativoVersamentoErratiOnClick() throws EMFError {
        getForm().getAggMetaErratiTabs()
                .setCurrentTab(getForm().getAggMetaErratiTabs().getEsitoNegativoVersamentoErrati());
        forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META_ERRATO);
    }

    @Override
    public void tabListaUlterioriErroriErratiOnClick() throws EMFError {
        getForm().getAggMetaErratiTabs()
                .setCurrentTab(getForm().getAggMetaErratiTabs().getListaUlterioriErroriErrati());
        forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META_ERRATO);
    }

    @Override
    public void scaricaXmlAggFallitoButton() throws EMFError {
        downloadFileCommon(getForm().getDettaglioAggMetaFallito().getBl_xml_rich().getValue(),
                getForm().getDettaglioAggMetaFallito().getBl_xml_risp().getValue(), "AggiornamentoFallito_",
                getForm().getDettaglioAggMetaFallito().getId_ses_upd_unita_doc_ko().parse(), "IndiceSIP",
                "EsitoNegativo");
    }

    // MEV#22438
    @Override
    public void scaricaXmlAggFallitoLastButton() throws EMFError {
        downloadFileCommon(getForm().getDettaglioUnitaDocAggMetaFallito().getBl_xml_rich_last().getValue(),
                getForm().getDettaglioUnitaDocAggMetaFallito().getBl_xml_risp_last().getValue(),
                "AggiornamentoFallito_",
                getForm().getDettaglioUnitaDocAggMetaFallito().getId_ses_upd_ud_ko_last().parse(), "IndiceSIP",
                "EsitoNegativo");
    }
    // end MEV#22438

    @Override
    public void scaricaXmlAggErratoButton() throws EMFError {
        downloadFileCommon(getForm().getDettaglioAggMetaErrato().getBl_xml_rich().getValue(),
                getForm().getDettaglioAggMetaErrato().getBl_xml_risp().getValue(), "AggiornamentoErrato_",
                getForm().getDettaglioAggMetaErrato().getId_ses_upd_unita_doc_err().parse(), "IndiceSIP",
                "EsitoNegativo");
    }

    private void downloadFileCommon(String xmlRich, String xmlRisp, String nomeFileZip, BigDecimal idSessione,
            String nomeFileXmlRich, String nomeFileXmlRisp) throws EMFError {
        ZipArchiveOutputStream tmpZipOutputStream = null;
        FileOutputStream tmpOutputStream = null;
        try {
            // Controllo per scrupolo
            if (xmlRich == null) {
                xmlRich = "";
            }
            if (xmlRisp == null) {
                xmlRisp = "";
            }
            String strIdSessione = "";
            if (idSessione != null) {
                strIdSessione = idSessione.toPlainString();
            }
            File zipDaScaricare = new File(System.getProperty("java.io.tmpdir"), nomeFileZip + strIdSessione + ".zip");
            tmpOutputStream = new FileOutputStream(zipDaScaricare);
            tmpZipOutputStream = new ZipArchiveOutputStream(tmpOutputStream);
            tmpZipOutputStream.putArchiveEntry(new ZipArchiveEntry(nomeFileXmlRich + ".xml"));
            tmpZipOutputStream.write((byte[]) xmlRich.getBytes("UTF-8"));
            tmpZipOutputStream.closeArchiveEntry();
            tmpZipOutputStream.putArchiveEntry(new ZipArchiveEntry(nomeFileXmlRisp + ".xml"));
            tmpZipOutputStream.write((byte[]) xmlRisp.getBytes("UTF-8"));
            tmpZipOutputStream.closeArchiveEntry();
            tmpZipOutputStream.flush();
            getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(), getControllerName());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(), zipDaScaricare.getName());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(), zipDaScaricare.getPath());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(), Boolean.toString(true));
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                    WebConstants.MIME_TYPE_ZIP);
        } catch (Exception ex) {
            logger.error("Errore in download " + ExceptionUtils.getRootCauseMessage(ex), ex);
            getMessageBox().addError("Errore inatteso nella preparazione del download<br/>");
        } finally {
            IOUtils.closeQuietly(tmpZipOutputStream);
            IOUtils.closeQuietly(tmpOutputStream);
            tmpZipOutputStream = null;
            tmpOutputStream = null;
        }

        if (getMessageBox().hasError()) {
            forwardToPublisher(getLastPublisher());
        } else {
            forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
        }
    }

    public void download() throws EMFError {
        logger.debug(">>>DOWNLOAD");
        String filename = (String) getSession().getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name());
        String path = (String) getSession().getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name());
        Boolean deleteFile = Boolean.parseBoolean(
                (String) getSession().getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name()));
        String contentType = (String) getSession()
                .getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name());
        if (path != null && filename != null) {
            File fileToDownload = new File(path);
            if (fileToDownload.exists()) {
                /*
                 * Definiamo l'output previsto che sarà un file in formato zip di cui si occuperà la servlet per fare il
                 * download
                 */
                OutputStream outUD = getServletOutputStream();
                getResponse()
                        .setContentType(StringUtils.isBlank(contentType) ? WebConstants.MIME_TYPE_ZIP : contentType);
                getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + filename);
                FileInputStream inputStream = null;
                try {
                    getResponse().setHeader("Content-Length", String.valueOf(fileToDownload.length()));
                    inputStream = new FileInputStream(fileToDownload);
                    byte[] bytes = new byte[8000];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(bytes)) != -1) {
                        outUD.write(bytes, 0, bytesRead);
                    }
                    outUD.flush();
                } catch (IOException e) {
                    logger.error("Eccezione nel recupero del documento ", e);
                    getMessageBox().addError("Eccezione nel recupero del documento");
                } finally {
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(outUD);
                    inputStream = null;
                    outUD = null;
                    freeze();
                }
                // Nel caso sia stato richiesto, elimina il file
                if (deleteFile) {
                    fileToDownload.delete();
                }
            } else {
                getMessageBox().addError("Errore durante il tentativo di download. File non trovato");
                forwardToPublisher(getLastPublisher());
            }
        } else {
            getMessageBox().addError("Errore durante il tentativo di download. File non trovato");
            forwardToPublisher(getLastPublisher());
        }
        getSession().removeAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name());
    }

    @Override
    public JSONObject triggerFiltriAggMetaErratiId_classe_err_sacerOnTrigger() throws EMFError {
        getForm().getFiltriAggMetaErrati().post(getRequest());
        BigDecimal idClasse = getForm().getFiltriAggMetaErrati().getId_classe_err_sacer().parse();
        getForm().getFiltriAggMetaErrati().getId_err_sacer()
                .setDecodeMap(monitAggMetaEjb.getErrSacerByIdClasseDecodeMap(idClasse));
        return getForm().getFiltriAggMetaErrati().asJSON();
    }

    @Override
    public void ricercaAggMetaErratiButton() throws EMFError {
        if (getForm().getFiltriAggMetaErrati().postAndValidate(getRequest(), getMessageBox())) {
            internalRicercaAggMetaErrati();
        }
        forwardToPublisher(getLastPublisher());
    }

    public void internalRicercaAggMetaErrati() throws EMFError {
        MonitoraggioAggMetaValidator validator = new MonitoraggioAggMetaValidator(getMessageBox());
        Date[] dateValidate = validator.validaDate(getForm().getFiltriAggMetaErrati().getDt_ini_ses_da().parse(),
                getForm().getFiltriAggMetaErrati().getHh_ini_ses_da().parse(),
                getForm().getFiltriAggMetaErrati().getMm_ini_ses_da().parse(),
                getForm().getFiltriAggMetaErrati().getDt_ini_ses_a().parse(),
                getForm().getFiltriAggMetaErrati().getHh_ini_ses_a().parse(),
                getForm().getFiltriAggMetaErrati().getMm_ini_ses_a().parse(),
                getForm().getFiltriAggMetaErrati().getDt_ini_ses_da().getHtmlDescription(),
                getForm().getFiltriAggMetaErrati().getDt_ini_ses_a().getHtmlDescription());
        if (!getMessageBox().hasError()) {
            Set<String> tiStatoSes = getForm().getFiltriAggMetaErrati().getTi_stato_ses().getValues();
            BigDecimal idClasseErr = getForm().getFiltriAggMetaErrati().getId_classe_err_sacer().parse();
            BigDecimal idErr = getForm().getFiltriAggMetaErrati().getId_err_sacer().parse();

            MonVLisUpdUdErrTableBean t = monitAggMetaEjb.ricercaAggMetaErrati(dateValidate, tiStatoSes, idClasseErr,
                    idErr);
            getForm().getAggMetaErratiList().setTable(t);
            getForm().getAggMetaErratiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getAggMetaErratiList().getTable().first();
        }
    }

    @Override
    public void tabInformazioniPrincipaliAggMetaOnClick() throws EMFError {
        getForm().getAggMetaTabs().setCurrentTab(getForm().getAggMetaTabs().getInformazioniPrincipaliAggMeta());
        forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META);
    }

    @Override
    public void tabInformazioniVersamentoAggMetaOnClick() throws EMFError {
        getForm().getAggMetaTabs().setCurrentTab(getForm().getAggMetaTabs().getInformazioniVersamentoAggMeta());
        forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META);
    }

    @Override
    public void tabIndiceSipAggiornamentoAggMetaOnClick() throws EMFError {
        getForm().getAggMetaTabs().setCurrentTab(getForm().getAggMetaTabs().getIndiceSipAggiornamentoAggMeta());
        forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META);
    }

    @Override
    public void tabRapportoVersamentoAggMetaOnClick() throws EMFError {
        getForm().getAggMetaTabs().setCurrentTab(getForm().getAggMetaTabs().getRapportoVersamentoAggMeta());
        forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META);
    }

    @Override
    public void tabListaDocAggiornatiAggMetaOnClick() throws EMFError {
        getForm().getAggMetaListsTabs().setCurrentTab(getForm().getAggMetaListsTabs().getListaDocAggiornatiAggMeta());
        forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META);
    }

    @Override
    public void tabListaCompAggiornatiAggMetaOnClick() throws EMFError {
        getForm().getAggMetaListsTabs().setCurrentTab(getForm().getAggMetaListsTabs().getListaCompAggiornatiAggMeta());
        forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META);
    }

    @Override
    public void tabListaAggMetaRisoltiAggMetaOnClick() throws EMFError {
        getForm().getAggMetaListsTabs().setCurrentTab(getForm().getAggMetaListsTabs().getListaAggMetaRisoltiAggMeta());
        forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META);
    }

    @Override
    public void tabListaWarnRilevatiAggMetaOnClick() throws EMFError {
        getForm().getAggMetaListsTabs().setCurrentTab(getForm().getAggMetaListsTabs().getListaWarnRilevatiAggMeta());
        forwardToPublisher(Application.Publisher.DETTAGLIO_AGG_META);
    }

    @Override
    public void scaricaXmlAggButton() throws EMFError {
        String registro = getForm().getDettaglioAggMeta().getCd_registro_key_unita_doc().getValue();
        String anno = getForm().getDettaglioAggMeta().getAa_key_unita_doc().getValue();
        String numero = getForm().getDettaglioAggMeta().getCd_key_unita_doc().getValue();
        String prog = getForm().getDettaglioAggMeta().getPg_upd_unita_doc().getValue();

        String nomeCartella = "SIP_AGGIORNAMENTO_" + registro + "-" + anno + "-" + numero + "-" + prog;

        downloadFileCommon(getForm().getDettaglioAggMeta().getBl_xml_rich().getValue(),
                getForm().getDettaglioAggMeta().getBl_xml_risp().getValue(), nomeCartella, null, "IndiceSIP", "RdV");
    }

    @Override
    public void verificaAggMetaFallitiButton() throws EMFError {
        List<String> stato = getForm().getFiltriAggMeta().getTi_stato_ses_upd_ko().parse();
        if (stato != null && stato.size() == 1 && stato.contains("NON_VERIFICATO")) {
            // Eseguo la post sulla lista
            getForm().getAggMetaFallitiList().post(getRequest());
            int aggiornati = 0;
            String[] indiceScelti = getRequest().getParameterValues("Scelto");
            List<BigDecimal> idAggDaModificare = new ArrayList<BigDecimal>();
            if (indiceScelti != null) {
                for (String indexString : indiceScelti) {
                    if (!indexString.equals("on")) {
                        Integer index = Integer.parseInt(indexString);
                        BigDecimal idAgg = getForm().getAggMetaFallitiList().getTable().getRow(index)
                                .getBigDecimal("id_ses_upd_unita_doc_ko");
                        idAggDaModificare.add(idAgg);
                    }
                }
            }

            for (BigDecimal id : idAggDaModificare) {
                try {
                    monitAggMetaEjb.salvaStatoAggiornamentoFallito(id, "NON_VERIFICATO", "VERIFICATO");
                    aggiornati++;
                } catch (ParerInternalError ex) {
                    getMessageBox().addError(ex.getMessage());
                    logger.error("Errore nel salvataggio dello stato dell'aggiornamento fallito", ex);
                } catch (Exception ex) {
                    getMessageBox().addError(
                            "Errore nel salvataggio dello stato dell'aggiornamento fallito con identificativo " + id);
                    logger.error(
                            "Errore nel salvataggio dello stato dell'aggiornamento fallito con identificativo" + id,
                            ex);
                }
            }
            if (aggiornati > 0) {
                getMessageBox().addInfo(
                        "Verifica aggiornamenti metadati falliti eseguita con successo per " + aggiornati + " record");
            } else if (!getMessageBox().hasError()) {
                getMessageBox().addInfo("Nessun record selezionato: non è stato eseguito alcun aggiornamento");
            }
            internalRicercaAggMetaFalliti();
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void assegnaNonRisolubAggMetaFallitiButton() throws EMFError {
        List<String> stato = getForm().getFiltriAggMeta().getTi_stato_ses_upd_ko().parse();
        if (stato != null && stato.size() == 1 && stato.contains("VERIFICATO")) {
            // Eseguo la post sulla lista
            getForm().getAggMetaFallitiList().post(getRequest());
            int aggiornati = 0;
            String[] indiceScelti = getRequest().getParameterValues("Scelto");
            List<BigDecimal> idAggDaModificare = new ArrayList<BigDecimal>();
            if (indiceScelti != null) {
                for (String indexString : indiceScelti) {
                    if (!indexString.equals("on")) {
                        Integer index = Integer.parseInt(indexString);
                        BigDecimal idAgg = getForm().getAggMetaFallitiList().getTable().getRow(index)
                                .getBigDecimal("id_ses_upd_unita_doc_ko");
                        idAggDaModificare.add(idAgg);
                    }
                }
            }

            for (BigDecimal id : idAggDaModificare) {
                try {
                    monitAggMetaEjb.salvaStatoAggiornamentoFallito(id, "VERIFICATO", "NON_RISOLUBILE");
                    aggiornati++;
                } catch (ParerInternalError ex) {
                    getMessageBox().addError(ex.getMessage());
                    logger.error("Errore nel salvataggio dello stato dell'aggiornamento fallito", ex);
                } catch (Exception ex) {
                    getMessageBox().addError(
                            "Errore nel salvataggio dello stato dell'aggiornamento fallito con identificativo " + id);
                    logger.error(
                            "Errore nel salvataggio dello stato dell'aggiornamento fallito con identificativo" + id,
                            ex);
                }
            }
            if (aggiornati > 0) {
                getMessageBox().addInfo(
                        "Verifica aggiornamenti metadati falliti eseguita con successo per " + aggiornati + " record");
            } else if (!getMessageBox().hasError()) {
                getMessageBox().addInfo("Nessun record selezionato: non è stato eseguito alcun aggiornamento");
            }
            internalRicercaAggMetaFalliti();
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void assegnaNonVerifAggMetaFallitiButton() throws EMFError {
        List<String> stato = getForm().getFiltriAggMeta().getTi_stato_ses_upd_ko().parse();
        if (stato != null && stato.size() == 1 && stato.contains("VERIFICATO")) {
            // Eseguo la post sulla lista
            getForm().getAggMetaFallitiList().post(getRequest());
            int aggiornati = 0;
            String[] indiceScelti = getRequest().getParameterValues("Scelto");
            List<BigDecimal> idAggDaModificare = new ArrayList<BigDecimal>();
            if (indiceScelti != null) {
                for (String indexString : indiceScelti) {
                    if (!indexString.equals("on")) {
                        Integer index = Integer.parseInt(indexString);
                        BigDecimal idAgg = getForm().getAggMetaFallitiList().getTable().getRow(index)
                                .getBigDecimal("id_ses_upd_unita_doc_ko");
                        idAggDaModificare.add(idAgg);
                    }
                }
            }

            for (BigDecimal id : idAggDaModificare) {
                try {
                    monitAggMetaEjb.salvaStatoAggiornamentoFallito(id, "VERIFICATO", "NON_VERIFICATO");
                    aggiornati++;
                } catch (ParerInternalError ex) {
                    getMessageBox().addError(ex.getMessage());
                    logger.error("Errore nel salvataggio dello stato dell'aggiornamento fallito", ex);
                } catch (Exception ex) {
                    getMessageBox().addError(
                            "Errore nel salvataggio dello stato dell'aggiornamento fallito con identificativo " + id);
                    logger.error(
                            "Errore nel salvataggio dello stato dell'aggiornamento fallito con identificativo" + id,
                            ex);
                }
            }
            if (aggiornati > 0) {
                getMessageBox().addInfo(
                        "Verifica aggiornamenti metadati falliti eseguita con successo per " + aggiornati + " record");
            } else if (!getMessageBox().hasError()) {
                getMessageBox().addInfo("Nessun record selezionato: non è stato eseguito alcun aggiornamento");
            }
            internalRicercaAggMetaFalliti();
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void assegnaVerifAggMetaFallitiButton() throws EMFError {
        List<String> stato = getForm().getFiltriAggMeta().getTi_stato_ses_upd_ko().parse();
        if (stato != null && stato.size() == 1
                && (stato.contains("NON_VERIFICATO") || stato.contains("NON_RISOLUBILE"))) {
            // Eseguo la post sulla lista
            getForm().getAggMetaFallitiList().post(getRequest());
            int aggiornati = 0;
            String[] indiceScelti = getRequest().getParameterValues("Scelto");
            List<BigDecimal> idAggDaModificare = new ArrayList<BigDecimal>();
            if (indiceScelti != null) {
                for (String indexString : indiceScelti) {
                    if (!indexString.equals("on")) {
                        Integer index = Integer.parseInt(indexString);
                        BigDecimal idAgg = getForm().getAggMetaFallitiList().getTable().getRow(index)
                                .getBigDecimal("id_ses_upd_unita_doc_ko");
                        idAggDaModificare.add(idAgg);
                    }
                }
            }

            for (BigDecimal id : idAggDaModificare) {
                try {
                    monitAggMetaEjb.salvaStatoAggiornamentoFallito(id, stato.get(0), "VERIFICATO");
                    aggiornati++;
                } catch (ParerInternalError ex) {
                    getMessageBox().addError(ex.getMessage());
                    logger.error("Errore nel salvataggio dello stato dell'aggiornamento fallito", ex);
                } catch (Exception ex) {
                    getMessageBox().addError(
                            "Errore nel salvataggio dello stato dell'aggiornamento fallito con identificativo " + id);
                    logger.error(
                            "Errore nel salvataggio dello stato dell'aggiornamento fallito con identificativo" + id,
                            ex);
                }
            }
            if (aggiornati > 0) {
                getMessageBox().addInfo(
                        "Verifica aggiornamenti metadati falliti eseguita con successo per " + aggiornati + " record");
            } else if (!getMessageBox().hasError()) {
                getMessageBox().addInfo("Nessun record selezionato: non è stato eseguito alcun aggiornamento");
            }
            internalRicercaAggMetaFalliti();
        }
        forwardToPublisher(getLastPublisher());
    }

    public void listaAggMeta() throws EMFError {
        getForm().getFiltriAggMeta().reset();
        getForm().getFiltriAggMeta().setEditMode();
        getForm().getFiltriAggMeta().getId_ambiente()
                .setDecodeMap(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ambiente().getDecodeMap());
        getForm().getFiltriAggMeta().getId_ambiente()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ambiente().getValue());
        getForm().getFiltriAggMeta().getId_ente()
                .setDecodeMap(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ente().getDecodeMap());
        getForm().getFiltriAggMeta().getId_ente()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ente().getValue());
        getForm().getFiltriAggMeta().getId_strut()
                .setDecodeMap(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_strut().getDecodeMap());
        getForm().getFiltriAggMeta().getId_strut()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_strut().getValue());
        getForm().getFiltriAggMeta().getId_tipo_unita_doc()
                .setDecodeMap(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_unita_doc().getDecodeMap());
        getForm().getFiltriAggMeta().getId_tipo_unita_doc()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_unita_doc().getValue());
        getForm().getFiltriAggMeta().getId_registro_unita_doc().setDecodeMap(
                getForm().getFiltriRicercaMonitoraggioAggMeta().getId_registro_unita_doc().getDecodeMap());
        getForm().getFiltriAggMeta().getId_registro_unita_doc()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_registro_unita_doc().getValue());
        getForm().getFiltriAggMeta().getId_tipo_doc()
                .setDecodeMap(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_doc().getDecodeMap());
        getForm().getFiltriAggMeta().getId_tipo_doc()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_doc().getValue());
        getForm().getFiltriAggMeta().getAa_key_unita_doc()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc().getValue());
        getForm().getFiltriAggMeta().getAa_key_unita_doc_da()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_da().getValue());
        getForm().getFiltriAggMeta().getAa_key_unita_doc_a()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_a().getValue());
        getForm().getFiltriAggMeta().getTi_stato_upd_elenco_vers().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("ti_stato_upd_elenco_vers", StatoGenerazioneIndiceAip.values()));
        getForm().getFiltriAggMeta().getFl_ses_upd_ko_risolti().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

        // analisi del tipo creazione selezionato
        String tiCreazione = getRequest().getParameter(PAR_TI_CREAZIONE);
        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
        Date oggi = new Date();
        switch (tiCreazione) {
        case TI_CREAZIONE_OGGI:
            getForm().getFiltriAggMeta().getDt_ini_ses_da().setValue(df.format(oggi));
            getForm().getFiltriAggMeta().getDt_ini_ses_a().setValue(df.format(oggi));
            break;
        case TI_CREAZIONE_30GG:
            Date oggiMeno30Gg = new DateTime(oggi).minusDays(30).toDate();
            Date oggiMeno1Gg = new DateTime(oggi).minusDays(1).toDate();
            getForm().getFiltriAggMeta().getDt_ini_ses_da().setValue(df.format(oggiMeno30Gg));
            getForm().getFiltriAggMeta().getDt_ini_ses_a().setValue(df.format(oggiMeno1Gg));
            break;
        case TI_CREAZIONE_B30GG:
            Date before30Gg = new DateTime(2000, 1, 1, 0, 0, 0, 0).toDate();
            getForm().getFiltriAggMeta().getDt_ini_ses_da().setValue(df.format(before30Gg));
            getForm().getFiltriAggMeta().getDt_ini_ses_a()
                    .setValue(df.format(new DateTime(oggi).minusDays(31).toDate()));
            break;
        }
        getForm().getFiltriAggMeta().getHh_ini_ses_da().setValue("00");
        getForm().getFiltriAggMeta().getHh_ini_ses_a().setValue("23");
        getForm().getFiltriAggMeta().getMm_ini_ses_da().setValue("00");
        getForm().getFiltriAggMeta().getMm_ini_ses_a().setValue("59");

        // analisi dello stato selezionato
        String tiStato = getRequest().getParameter(PAR_TI_STATO);
        switch (tiStato) {
        case TI_STATO_TOTALE:
            getForm().getFiltriAggMeta().getTi_stato_upd_elenco_vers().clear();
            break;
        case TI_STATO_IN_ATTESA_SCHED:
            getForm().getFiltriAggMeta().getTi_stato_upd_elenco_vers().setValues(new String[] { "IN_ATTESA_SCHED" });
            break;
        case TI_STATO_NON_SELEZ_SCHED:
            getForm().getFiltriAggMeta().getTi_stato_upd_elenco_vers().setValues(new String[] { "NON_SELEZ_SCHED" });
            break;
        }
        getForm().getFiltriAggMeta().getFl_ses_upd_ko_risolti().clear();
        internalRicercaAggMeta();
        forwardToPublisher(Application.Publisher.LISTA_AGG_META);
    }

    public void listaAggMetaFalliti() throws EMFError {
        getForm().getFiltriAggMeta().reset();
        getForm().getFiltriAggMeta().setEditMode();
        getForm().getFiltriAggMeta().getId_ambiente()
                .setDecodeMap(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ambiente().getDecodeMap());
        getForm().getFiltriAggMeta().getId_ambiente()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ambiente().getValue());
        getForm().getFiltriAggMeta().getId_ente()
                .setDecodeMap(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ente().getDecodeMap());
        getForm().getFiltriAggMeta().getId_ente()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ente().getValue());
        getForm().getFiltriAggMeta().getId_strut()
                .setDecodeMap(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_strut().getDecodeMap());
        getForm().getFiltriAggMeta().getId_strut()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_strut().getValue());
        getForm().getFiltriAggMeta().getId_tipo_unita_doc()
                .setDecodeMap(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_unita_doc().getDecodeMap());
        getForm().getFiltriAggMeta().getId_tipo_unita_doc()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_unita_doc().getValue());
        getForm().getFiltriAggMeta().getId_registro_unita_doc().setDecodeMap(
                getForm().getFiltriRicercaMonitoraggioAggMeta().getId_registro_unita_doc().getDecodeMap());
        getForm().getFiltriAggMeta().getId_registro_unita_doc()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_registro_unita_doc().getValue());
        getForm().getFiltriAggMeta().getId_tipo_doc()
                .setDecodeMap(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_doc().getDecodeMap());
        getForm().getFiltriAggMeta().getId_tipo_doc()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_doc().getValue());
        getForm().getFiltriAggMeta().getAa_key_unita_doc()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc().getValue());
        getForm().getFiltriAggMeta().getAa_key_unita_doc_da()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_da().getValue());
        getForm().getFiltriAggMeta().getAa_key_unita_doc_a()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_a().getValue());
        getForm().getFiltriAggMeta().getTi_stato_ses_upd_ko()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato_ses_upd_ko", Stato.values()));
        getForm().getFiltriAggMeta().getCd_classe_err()
                .setDecodeMap(monitAggMetaEjb.getIdClasseErrSacerDecodeMap(
                        Arrays.asList(new String[] { MonitoraggioAggMetaEjb.tipoUsoClasseErrore.VERS_UNITA_DOC.name(),
                                MonitoraggioAggMetaEjb.tipoUsoClasseErrore.GENERICO.name() })));

        // analisi del tipo creazione selezionato
        String tiCreazione = getRequest().getParameter(PAR_TI_CREAZIONE);
        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
        Date oggi = new Date();
        switch (tiCreazione) {
        case TI_CREAZIONE_OGGI:
            getForm().getFiltriAggMeta().getDt_ini_ses_da().setValue(df.format(oggi));
            getForm().getFiltriAggMeta().getDt_ini_ses_a().setValue(df.format(oggi));
            break;
        case TI_CREAZIONE_30GG:
            Date oggiMeno30Gg = new DateTime(oggi).minusDays(30).toDate();
            Date oggiMeno1Gg = new DateTime(oggi).minusDays(1).toDate();
            getForm().getFiltriAggMeta().getDt_ini_ses_da().setValue(df.format(oggiMeno30Gg));
            getForm().getFiltriAggMeta().getDt_ini_ses_a().setValue(df.format(oggiMeno1Gg));
            break;
        case TI_CREAZIONE_B30GG:
            Date before30Gg = new DateTime(2000, 1, 1, 0, 0, 0, 0).toDate();
            getForm().getFiltriAggMeta().getDt_ini_ses_da().setValue(df.format(before30Gg));
            getForm().getFiltriAggMeta().getDt_ini_ses_a()
                    .setValue(df.format(new DateTime(oggi).minusDays(31).toDate()));
            break;
        }
        getForm().getFiltriAggMeta().getHh_ini_ses_da().setValue("00");
        getForm().getFiltriAggMeta().getHh_ini_ses_a().setValue("23");
        getForm().getFiltriAggMeta().getMm_ini_ses_da().setValue("00");
        getForm().getFiltriAggMeta().getMm_ini_ses_a().setValue("59");

        // analisi dello stato selezionato
        String tiStato = getRequest().getParameter(PAR_TI_STATO);
        switch (tiStato) {
        case TI_STATO_TOTALE:
            getForm().getFiltriAggMeta().getTi_stato_ses_upd_ko().clear();
            break;
        case TI_STATO_NON_RISOLUBILE:
            getForm().getFiltriAggMeta().getTi_stato_ses_upd_ko().setValues(new String[] { TI_STATO_NON_RISOLUBILE });
            break;
        case TI_STATO_VERIFICATO:
            getForm().getFiltriAggMeta().getTi_stato_ses_upd_ko().setValues(new String[] { TI_STATO_VERIFICATO });
            break;
        case TI_STATO_NON_VERIFICATO:
            getForm().getFiltriAggMeta().getTi_stato_ses_upd_ko().setValues(new String[] { TI_STATO_NON_VERIFICATO });
            break;
        }
        internalRicercaAggMetaFalliti();
        forwardToPublisher(Application.Publisher.LISTA_AGG_META_FALLITI);
    }

    // MEV#22438
    public void listaUnitaDocAggMetaFalliti() throws EMFError {
        getForm().getFiltriAggMeta().reset();
        getForm().getFiltriAggMeta().setEditMode();
        getForm().getFiltriAggMeta().getId_ambiente()
                .setDecodeMap(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ambiente().getDecodeMap());
        getForm().getFiltriAggMeta().getId_ambiente()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ambiente().getValue());
        getForm().getFiltriAggMeta().getId_ente()
                .setDecodeMap(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ente().getDecodeMap());
        getForm().getFiltriAggMeta().getId_ente()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_ente().getValue());
        getForm().getFiltriAggMeta().getId_strut()
                .setDecodeMap(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_strut().getDecodeMap());
        getForm().getFiltriAggMeta().getId_strut()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_strut().getValue());
        getForm().getFiltriAggMeta().getId_tipo_unita_doc()
                .setDecodeMap(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_unita_doc().getDecodeMap());
        getForm().getFiltriAggMeta().getId_tipo_unita_doc()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_unita_doc().getValue());
        getForm().getFiltriAggMeta().getId_registro_unita_doc().setDecodeMap(
                getForm().getFiltriRicercaMonitoraggioAggMeta().getId_registro_unita_doc().getDecodeMap());
        getForm().getFiltriAggMeta().getId_registro_unita_doc()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_registro_unita_doc().getValue());
        getForm().getFiltriAggMeta().getId_tipo_doc()
                .setDecodeMap(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_doc().getDecodeMap());
        getForm().getFiltriAggMeta().getId_tipo_doc()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getId_tipo_doc().getValue());
        getForm().getFiltriAggMeta().getAa_key_unita_doc()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc().getValue());
        getForm().getFiltriAggMeta().getAa_key_unita_doc_da()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_da().getValue());
        getForm().getFiltriAggMeta().getAa_key_unita_doc_a()
                .setValue(getForm().getFiltriRicercaMonitoraggioAggMeta().getAa_key_unita_doc_a().getValue());
        getForm().getFiltriAggMeta().getTi_stato_ses_upd_ko()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato_ses_upd_ko", Stato.values()));
        getForm().getFiltriAggMeta().getCd_classe_err()
                .setDecodeMap(monitAggMetaEjb.getIdClasseErrSacerDecodeMap(
                        Arrays.asList(new String[] { MonitoraggioAggMetaEjb.tipoUsoClasseErrore.VERS_UNITA_DOC.name(),
                                MonitoraggioAggMetaEjb.tipoUsoClasseErrore.GENERICO.name() })));

        // analisi dello stato selezionato
        String tiStato = getRequest().getParameter(PAR_TI_STATO);
        switch (tiStato) {
        case TI_STATO_TOTALE:
            getForm().getFiltriAggMeta().getTi_stato_ses_upd_ko().clear();
            break;
        case TI_STATO_NON_RISOLUBILE:
            getForm().getFiltriAggMeta().getTi_stato_ses_upd_ko().setValues(new String[] { TI_STATO_NON_RISOLUBILE });
            break;
        case TI_STATO_VERIFICATO:
            getForm().getFiltriAggMeta().getTi_stato_ses_upd_ko().setValues(new String[] { TI_STATO_VERIFICATO });
            break;
        case TI_STATO_NON_VERIFICATO:
            getForm().getFiltriAggMeta().getTi_stato_ses_upd_ko().setValues(new String[] { TI_STATO_NON_VERIFICATO });
            break;
        }
        internalRicercaUnitaDocAggMetaFalliti();
        forwardToPublisher(Application.Publisher.LISTA_UNITA_DOC_AGG_META_FALLITI);
    }
    // end MEV#22438

    // Metodo richiamato sia dal monitoraggio che dal pulsante di ricerca
    public void internalRicercaAggMeta() throws EMFError {
        MonitoraggioAggMetaValidator validator = new MonitoraggioAggMetaValidator(getMessageBox());
        Date[] dateValidate = validator.validaDate(getForm().getFiltriAggMeta().getDt_ini_ses_da().parse(),
                getForm().getFiltriAggMeta().getHh_ini_ses_da().parse(),
                getForm().getFiltriAggMeta().getMm_ini_ses_da().parse(),
                getForm().getFiltriAggMeta().getDt_ini_ses_a().parse(),
                getForm().getFiltriAggMeta().getHh_ini_ses_a().parse(),
                getForm().getFiltriAggMeta().getMm_ini_ses_a().parse(),
                getForm().getFiltriAggMeta().getDt_ini_ses_da().getHtmlDescription(),
                getForm().getFiltriAggMeta().getDt_ini_ses_a().getHtmlDescription());
        ChiaveBean bean = validator.validaChiavi(getForm().getChiaveAggMetaSection().getDescription(),
                getForm().getFiltriAggMeta().getAa_key_unita_doc(),
                getForm().getFiltriAggMeta().getAa_key_unita_doc_da(),
                getForm().getFiltriAggMeta().getAa_key_unita_doc_a(),
                getForm().getFiltriAggMeta().getCd_key_unita_doc(),
                getForm().getFiltriAggMeta().getCd_key_unita_doc_da(),
                getForm().getFiltriAggMeta().getCd_key_unita_doc_a());
        if (!getMessageBox().hasError()) {
            BigDecimal idUser = new BigDecimal(getUser().getIdUtente());
            BigDecimal idAmbiente = getForm().getFiltriAggMeta().getId_ambiente().parse();
            BigDecimal idEnte = getForm().getFiltriAggMeta().getId_ente().parse();
            BigDecimal idStruttura = getForm().getFiltriAggMeta().getId_strut().parse();
            BigDecimal idTipoUnitadoc = getForm().getFiltriAggMeta().getId_tipo_unita_doc().parse();
            BigDecimal idRegistroUnitadoc = getForm().getFiltriAggMeta().getId_registro_unita_doc().parse();
            BigDecimal idTipodoc = getForm().getFiltriAggMeta().getId_tipo_doc().parse();

            Set<String> statoIndiceAip = getForm().getFiltriAggMeta().getTi_stato_upd_elenco_vers().getValues();
            String flSesUpdKoRisolti = getForm().getFiltriAggMeta().getFl_ses_upd_ko_risolti().getValue();
            BaseTable t = monitAggMetaEjb.ricercaAggMetaPerMonitoraggio(idUser, idAmbiente, idEnte, idStruttura,
                    idTipoUnitadoc, idRegistroUnitadoc, idTipodoc, dateValidate,
                    bean.isSingleValue() ? bean.getAnno() : bean.getAnnoDa(), bean.getAnnoA(),
                    bean.isSingleValue() ? bean.getNumero() : bean.getNumeroDa(), bean.getNumeroA(), statoIndiceAip,
                    flSesUpdKoRisolti);
            getForm().getAggMetaList().setTable(t);
            getForm().getAggMetaList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getAggMetaList().getTable().first();
        }
    }

    // Metodo richiamato sia dal monitoraggio che dal pulsante di ricerca
    public void internalRicercaAggMetaFalliti() throws EMFError {
        MonitoraggioAggMetaValidator validator = new MonitoraggioAggMetaValidator(getMessageBox());
        Date[] dateValidate = validator.validaDate(getForm().getFiltriAggMeta().getDt_ini_ses_da().parse(),
                getForm().getFiltriAggMeta().getHh_ini_ses_da().parse(),
                getForm().getFiltriAggMeta().getMm_ini_ses_da().parse(),
                getForm().getFiltriAggMeta().getDt_ini_ses_a().parse(),
                getForm().getFiltriAggMeta().getHh_ini_ses_a().parse(),
                getForm().getFiltriAggMeta().getMm_ini_ses_a().parse(),
                getForm().getFiltriAggMeta().getDt_ini_ses_da().getHtmlDescription(),
                getForm().getFiltriAggMeta().getDt_ini_ses_a().getHtmlDescription());
        ChiaveBean bean = validator.validaChiavi(getForm().getChiaveAggMetaSection().getDescription(),
                getForm().getFiltriAggMeta().getAa_key_unita_doc(),
                getForm().getFiltriAggMeta().getAa_key_unita_doc_da(),
                getForm().getFiltriAggMeta().getAa_key_unita_doc_a(),
                getForm().getFiltriAggMeta().getCd_key_unita_doc(),
                getForm().getFiltriAggMeta().getCd_key_unita_doc_da(),
                getForm().getFiltriAggMeta().getCd_key_unita_doc_a());
        if (!getMessageBox().hasError()) {
            BigDecimal idUser = new BigDecimal(getUser().getIdUtente());
            BigDecimal idAmbiente = getForm().getFiltriAggMeta().getId_ambiente().parse();
            BigDecimal idEnte = getForm().getFiltriAggMeta().getId_ente().parse();
            BigDecimal idStruttura = getForm().getFiltriAggMeta().getId_strut().parse();
            BigDecimal idTipoUnitadoc = getForm().getFiltriAggMeta().getId_tipo_unita_doc().parse();
            BigDecimal idRegistroUnitadoc = getForm().getFiltriAggMeta().getId_registro_unita_doc().parse();
            BigDecimal idTipodoc = getForm().getFiltriAggMeta().getId_tipo_doc().parse();
            Set<String> statoSessione = getForm().getFiltriAggMeta().getTi_stato_ses_upd_ko().getValues();
            BigDecimal idClasseErr = getForm().getFiltriAggMeta().getCd_classe_err().parse();
            BigDecimal idErr = getForm().getFiltriAggMeta().getCd_err().parse();
            BaseTable t = null;
            // Ho dovuto differenziare i metodi altrimenti il paginatore faceva casino con le count(*) su valori
            // DISTINCT
            if (idClasseErr != null || idErr != null) {
                t = monitAggMetaEjb.ricercaAggMetaFallitiPerMonitoraggio(idUser, idAmbiente, idEnte, idStruttura,
                        idTipoUnitadoc, idRegistroUnitadoc, idTipodoc, dateValidate,
                        bean.isSingleValue() ? bean.getAnno() : bean.getAnnoDa(), bean.getAnnoA(),
                        bean.isSingleValue() ? bean.getNumero() : bean.getNumeroDa(), bean.getNumeroA(), statoSessione,
                        idClasseErr, idErr);
            } else {
                t = monitAggMetaEjb.ricercaAggMetaFallitiPerMonitoraggioErr(idUser, idAmbiente, idEnte, idStruttura,
                        idTipoUnitadoc, idRegistroUnitadoc, idTipodoc, dateValidate,
                        bean.isSingleValue() ? bean.getAnno() : bean.getAnnoDa(), bean.getAnnoA(),
                        bean.isSingleValue() ? bean.getNumero() : bean.getNumeroDa(), bean.getNumeroA(), statoSessione,
                        idClasseErr, idErr);
            }
            getForm().getAggMetaFallitiList().setTable(t);
            getForm().getAggMetaFallitiList().getTable().first();
            getForm().getAggMetaFallitiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

            getForm().getAggMetaFallitiList().getScelto().setHidden(true);
            getForm().getFiltriAggMeta().getVerificaAggMetaFallitiButton().setViewMode();
            getForm().getFiltriAggMeta().getAssegnaNonRisolubAggMetaFallitiButton().setViewMode();
            getForm().getFiltriAggMeta().getAssegnaNonVerifAggMetaFallitiButton().setViewMode();
            getForm().getFiltriAggMeta().getAssegnaVerifAggMetaFallitiButton().setViewMode();

            if (statoSessione != null && statoSessione.size() == 1) {
                getForm().getAggMetaFallitiList().getScelto().setHidden(false);
                if (statoSessione.contains("NON_VERIFICATO")) {
                    getForm().getFiltriAggMeta().getVerificaAggMetaFallitiButton().setEditMode();
                } else if (statoSessione.contains("VERIFICATO")) {
                    getForm().getFiltriAggMeta().getAssegnaNonRisolubAggMetaFallitiButton().setEditMode();
                    getForm().getFiltriAggMeta().getAssegnaNonVerifAggMetaFallitiButton().setEditMode();
                } else if (statoSessione.contains("NON_RISOLUBILE")) {
                    getForm().getFiltriAggMeta().getAssegnaVerifAggMetaFallitiButton().setEditMode();
                }
            }
        }
    }

    // MEV#22438
    // Metodo richiamato sia dal monitoraggio che dal pulsante di ricerca
    public void internalRicercaUnitaDocAggMetaFalliti() throws EMFError {
        MonitoraggioAggMetaValidator validator = new MonitoraggioAggMetaValidator(getMessageBox());
        Date[] dateValidate = validator.validaDate(getForm().getFiltriAggMeta().getDt_ini_ses_da().parse(),
                getForm().getFiltriAggMeta().getHh_ini_ses_da().parse(),
                getForm().getFiltriAggMeta().getMm_ini_ses_da().parse(),
                getForm().getFiltriAggMeta().getDt_ini_ses_a().parse(),
                getForm().getFiltriAggMeta().getHh_ini_ses_a().parse(),
                getForm().getFiltriAggMeta().getMm_ini_ses_a().parse(),
                getForm().getFiltriAggMeta().getDt_ini_ses_da().getHtmlDescription(),
                getForm().getFiltriAggMeta().getDt_ini_ses_a().getHtmlDescription());
        ChiaveBean bean = validator.validaChiavi(getForm().getChiaveAggMetaSection().getDescription(),
                getForm().getFiltriAggMeta().getAa_key_unita_doc(),
                getForm().getFiltriAggMeta().getAa_key_unita_doc_da(),
                getForm().getFiltriAggMeta().getAa_key_unita_doc_a(),
                getForm().getFiltriAggMeta().getCd_key_unita_doc(),
                getForm().getFiltriAggMeta().getCd_key_unita_doc_da(),
                getForm().getFiltriAggMeta().getCd_key_unita_doc_a());
        if (!getMessageBox().hasError()) {
            BigDecimal idUser = new BigDecimal(getUser().getIdUtente());
            BigDecimal idAmbiente = getForm().getFiltriAggMeta().getId_ambiente().parse();
            BigDecimal idEnte = getForm().getFiltriAggMeta().getId_ente().parse();
            BigDecimal idStruttura = getForm().getFiltriAggMeta().getId_strut().parse();
            BigDecimal idTipoUnitadoc = getForm().getFiltriAggMeta().getId_tipo_unita_doc().parse();
            BigDecimal idRegistroUnitadoc = getForm().getFiltriAggMeta().getId_registro_unita_doc().parse();
            BigDecimal idTipodoc = getForm().getFiltriAggMeta().getId_tipo_doc().parse();
            Set<String> statoSessione = getForm().getFiltriAggMeta().getTi_stato_ses_upd_ko().getValues();
            BigDecimal idClasseErr = getForm().getFiltriAggMeta().getCd_classe_err().parse();
            BigDecimal idErr = getForm().getFiltriAggMeta().getCd_err().parse();
            BaseTable t = monitAggMetaEjb.ricercaUnitaDocAggMetaFallitiPerMonitoraggio(idUser, idAmbiente, idEnte,
                    idStruttura, idTipoUnitadoc, idRegistroUnitadoc, idTipodoc, dateValidate,
                    bean.isSingleValue() ? bean.getAnno() : bean.getAnnoDa(), bean.getAnnoA(),
                    bean.isSingleValue() ? bean.getNumero() : bean.getNumeroDa(), bean.getNumeroA(), statoSessione,
                    idClasseErr, idErr);
            getForm().getUnitaDocAggMetaFallitiList().setTable(t);
            getForm().getUnitaDocAggMetaFallitiList().getTable().first();
            getForm().getUnitaDocAggMetaFallitiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

            getForm().getFiltriAggMeta().getVerificaAggMetaFallitiButton().setViewMode();
            getForm().getFiltriAggMeta().getAssegnaNonRisolubAggMetaFallitiButton().setViewMode();
            getForm().getFiltriAggMeta().getAssegnaNonVerifAggMetaFallitiButton().setViewMode();
            getForm().getFiltriAggMeta().getAssegnaVerifAggMetaFallitiButton().setViewMode();
        }
    }
    // end MEV#22438

    @Override
    public JSONObject triggerFiltriAggMetaCd_classe_errOnTrigger() throws EMFError {
        getForm().getFiltriAggMeta().post(getRequest());
        BigDecimal idClasse = getForm().getFiltriAggMeta().getCd_classe_err().parse();
        getForm().getFiltriAggMeta().getCd_err().setDecodeMap(monitAggMetaEjb.getErrSacerByIdClasseDecodeMap(idClasse));
        return getForm().getFiltriAggMeta().asJSON();
    }

    @Override
    protected void postLoad() {
        super.postLoad();
        if (getForm() instanceof MonitoraggioAggMetaForm) {
            getForm().getFiltriRicercaMonitoraggioAggMeta().getGeneraMonitoraggioAggMetaButton().setEditMode();
            getForm().getCalcolaTotaliButtonList().getCalcTotAggFallitiButton().setEditMode();
            getForm().getCalcolaTotaliButtonList().getCalcNumAggDataCorrenteButton().setEditMode();
            // MEV#22438
            getForm().getCalcolaTotaliButtonList().getCalcTotUdAggFallitiButton().setEditMode();
            // end MEV#22438
            // Imposto le combo in editMode
            getForm().getFiltriRicercaMonitoraggioAggMeta().setEditMode();
            getForm().getCalcolaTotaliButtonList().setEditMode();
            getForm().getFiltriAggMeta().setEditMode();
        }
    }

    @Override
    public void updateAggMetaFallitiList() {
        try {
            getForm().getDettaglioAggMetaFallito().getTi_stato_ses_upd_ko().setEditMode();
            String stato = getForm().getDettaglioAggMetaFallito().getTi_stato_ses_upd_ko().parse();

            DecodeMap mappa = new DecodeMap();
            BaseRow r1 = new BaseRow();
            BaseRow r2 = new BaseRow();
            BaseRow r3 = new BaseRow();
            BaseTable t = new BaseTable();

            switch (stato) {
            case TI_STATO_NON_VERIFICATO:
                r1.setString("ti_stato_ses_upd_ko", TI_STATO_NON_VERIFICATO);
                r2.setString("ti_stato_ses_upd_ko", TI_STATO_VERIFICATO);
                break;
            case TI_STATO_VERIFICATO:
                r1.setString("ti_stato_ses_upd_ko", TI_STATO_NON_VERIFICATO);
                r2.setString("ti_stato_ses_upd_ko", TI_STATO_VERIFICATO);
                r3.setString("ti_stato_ses_upd_ko", TI_STATO_NON_RISOLUBILE);
                t.add(r3);
                break;
            case TI_STATO_NON_RISOLUBILE:
                r1.setString("ti_stato_ses_upd_ko", TI_STATO_VERIFICATO);
                r2.setString("ti_stato_ses_upd_ko", TI_STATO_NON_RISOLUBILE);
                break;
            }

            t.add(r1);
            t.add(r2);
            mappa.populatedMap(t, "ti_stato_ses_upd_ko", "ti_stato_ses_upd_ko");
            getForm().getDettaglioAggMetaFallito().getTi_stato_ses_upd_ko().setDecodeMap(mappa);
            getForm().getDettaglioAggMetaFallito().getTi_stato_ses_upd_ko().setValue(stato);

            getForm().getDettaglioAggMetaFallito().setStatus(BaseElements.Status.update);
            getForm().getAggMetaFallitiList().setStatus(BaseElements.Status.update);

        } catch (EMFError ex) {
            getMessageBox().addError("Errore durante la modifica dell'aggiornamento metadati fallito");
            forwardToPublisher(getLastPublisher());
        }
    }

}
