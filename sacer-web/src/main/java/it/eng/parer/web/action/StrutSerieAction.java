package it.eng.parer.web.action;

import it.eng.parer.amministrazioneStrutture.gestioneDatiSpecifici.ejb.DatiSpecificiEjb;
import it.eng.parer.amministrazioneStrutture.gestioneRegistro.ejb.RegistroEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoDoc.ejb.TipoDocumentoEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb.TipoUnitaDocEjb;
import it.eng.parer.exception.ParerErrorSeverity;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.ejb.common.helper.ParamApplicHelper;
import it.eng.parer.sacerlog.slite.gen.form.GestioneLogEventiForm;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.sacerlog.util.web.SpagoliteLogUtil;
import it.eng.parer.serie.ejb.ModelliSerieEjb;
import it.eng.parer.serie.ejb.SerieEjb;
import static it.eng.parer.serie.ejb.SerieEjb.CD_SERIE_PATTERN;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.StrutSerieAbstractAction;
import it.eng.parer.slite.gen.form.StrutSerieForm;
import it.eng.parer.slite.gen.tablebean.DecAttribDatiSpecRowBean;
import it.eng.parer.slite.gen.tablebean.DecAttribDatiSpecTableBean;
import it.eng.parer.slite.gen.tablebean.DecAttribDatiSpecTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecCampoInpUdRowBean;
import it.eng.parer.slite.gen.tablebean.DecCampoInpUdTableBean;
import it.eng.parer.slite.gen.tablebean.DecCampoOutSelUdRowBean;
import it.eng.parer.slite.gen.tablebean.DecCampoOutSelUdTableBean;
import it.eng.parer.slite.gen.tablebean.DecFiltroSelUdAttbRowBean;
import it.eng.parer.slite.gen.tablebean.DecFiltroSelUdAttbTableBean;
import it.eng.parer.slite.gen.tablebean.DecFiltroSelUdRowBean;
import it.eng.parer.slite.gen.tablebean.DecFiltroSelUdTableBean;
import it.eng.parer.slite.gen.tablebean.DecNotaTipoSerieRowBean;
import it.eng.parer.slite.gen.tablebean.DecNotaTipoSerieTableBean;
import it.eng.parer.slite.gen.tablebean.DecOutSelUdRowBean;
import it.eng.parer.slite.gen.tablebean.DecOutSelUdTableBean;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoNotaSerieTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoSerieRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoSerieTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoSerieUdRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoSerieUdTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.DecVLisTiUniDocAmsRowBean;
import it.eng.parer.slite.gen.viewbean.DecVLisTiUniDocAmsTableBean;
import it.eng.parer.web.dto.DecFiltroSelUdAttbBean;
import it.eng.parer.web.dto.DecFiltroSelUdDatoBean;
import it.eng.parer.web.dto.DefinitoDaBean;
import it.eng.parer.serie.ejb.TipoSerieEjb;
import it.eng.parer.slite.gen.form.StruttureForm;
import it.eng.parer.slite.gen.tablebean.DecOutSelUdTableDescriptor;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.ActionUtils;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.web.validator.UnitaDocumentarieValidator;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoIFace.Values;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.decodemap.DecodeMapIF;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.form.fields.impl.Input;
import java.util.List;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.Secure;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.util.CollectionUtils;

/**
 *
 * @author Parucci_M feat. Gilioli_P
 */
public class StrutSerieAction extends StrutSerieAbstractAction {

    private static Logger logger = LoggerFactory.getLogger(StrutSerieAction.class.getName());
    @EJB(mappedName = "java:app/Parer-ejb/TipoSerieEjb")
    private TipoSerieEjb tipoSerieEjb;
    @EJB(mappedName = "java:app/Parer-ejb/SerieEjb")
    private SerieEjb serieEjb;
    // REFACTORING_HELPER Bisognerebbe eliminare l'helper e fare un ejb
    @EJB(mappedName = "java:app/Parer-ejb/UnitaDocumentarieHelper")
    private UnitaDocumentarieHelper udHelper;
    @EJB(mappedName = "java:app/Parer-ejb/ModelliSerieEjb")
    private ModelliSerieEjb modelliEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoUnitaDocEjb")
    private TipoUnitaDocEjb tipoUnitaDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/RegistroEjb")
    private RegistroEjb registroEjb;
    @EJB(mappedName = "java:app/Parer-ejb/DatiSpecificiEjb")
    private DatiSpecificiEjb datiSpecEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoDocumentoEjb")
    private TipoDocumentoEjb tipoDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @EJB(mappedName = "java:app/sacerlog-ejb/ParamApplicHelper")
    private ParamApplicHelper paramApplicHelper;

    @Override
    public void initOnClick() throws EMFError {
    }

    @Override
    public void loadDettaglio() throws EMFError {
        try {
            if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                    || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                    || getNavigationEvent().equals(ListAction.NE_NEXT)
                    || getNavigationEvent().equals(ListAction.NE_PREV)) {

                if (islistaEqual(getTableName(), getForm().getTipologieSerieList())) {
                    visualizzaTipoSerie();
                }

                if (islistaEqual(getTableName(), getForm().getTipoSerieRegistriList())) {
                    visualizzaTipoSerieRegistro();
                }

                if (islistaEqual(getTableName(), getForm().getRegistroRegoleFiltraggioList())) {
                    visualizzaRegolaDiFiltraggio(getForm().getRegolaDiFiltraggioDetail(), BaseElements.Status.view);
                }

                if (islistaEqual(getTableName(), getForm().getRegoleAcquisizioneList())
                        || getForm().getRegoleAcquisizioneDetail().getName().equals(getTableName())) {
                    switch (getNavigationEvent()) {
                    case NE_DETTAGLIO_VIEW:
                        getForm().getRegoleAcquisizioneList().setUserOperations(false, false, false, false);
                        getForm().getRegoleAcquisizioneList().setStatus(Status.view);
                        break;
                    case NE_DETTAGLIO_UPDATE:
                        editRegoleAcquisizione(Status.update);
                        break;
                    }
                }

                if (islistaEqual(getTableName(), getForm().getNoteTipoSerieList())) {
                    visualizzaDecNotaTipoSerie(BaseElements.Status.view);
                }
            }

            if (islistaEqual(getTableName(), getForm().getAssociazioneDatiSpecList())) {
                ricaricaListaFiltriDatiSpecificiPerAssociazioneRegistroTipoUnitaDoc();
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }

    }

    /**
     * Carica il dettaglio con relative liste della pagina "Dettaglio Tipo Serie Unità Documentaria"
     *
     * @throws EMFError
     *             errore generico
     */
    private void visualizzaTipoSerie() throws EMFError {
        StrutSerieForm.TipoSerieDetail tipoSerieDetail = getForm().getTipoSerieDetail();
        // Metto tutto in "Modalità Visualizzazione"
        tipoSerieDetail.setViewMode();
        tipoSerieDetail.setStatus(BaseElements.Status.view);
        getForm().getTipologieSerieList().setStatus(BaseElements.Status.view);
        // Recupero l'idStrut
        BigDecimal idStrut = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable().getCurrentRow())
                .getIdStrut();
        OrgStrutRowBean struttura = struttureEjb.getOrgStrutRowBean(idStrut);
        if ("1".equals(struttura.getFlCessato())) {
            getRequest().setAttribute("cessato", true);
        }
        // Recupero l'idTipoSerie
        BigDecimal idTipoSerie = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable().getCurrentRow())
                .getIdTipoSerie();
        // Visualizzo i bottoni della listNavBar a seconda che il tipo serie sia modificabile o meno
        boolean isTipoSerieModificabile = tipoSerieEjb.isTipoSerieModificabile(idTipoSerie);
        if (isTipoSerieModificabile) {
            getForm().getTipologieSerieList().setUserOperations(true, true, true, true);
        } else {
            getForm().getTipologieSerieList().setUserOperations(true, false, false, false);
        }
        // Recupero i campi da DB
        DecTipoSerieRowBean tipoSerieRowBean = tipoSerieEjb.getDecTipoSerieRowBean(idTipoSerie);
        // Carico le combo e le liste presenti nella pagina di dettaglio Tipologia Serie
        caricaComboListeTipoSerie(idTipoSerie);
        // Valorizzo i campi
        tipoSerieDetail.copyFromBean(tipoSerieRowBean);

        // Setto i valori delle combo ambiente/ente/struttura
        try {
            ActionUtils utile = new ActionUtils();
            utile.initGenericComboAmbienteEnteStruttura(getForm().getTipoSerieDetail(), getUser().getIdUtente(),
                    tipoSerieRowBean.getIdStrut(), Boolean.TRUE);
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore inatteso nel recupero delle strutture abilitate");
        }

        BigDecimal niMesiCreazioneSerie;
        if ((niMesiCreazioneSerie = tipoSerieRowBean.getNiMmCreaAutom()) != null) {
            if (niMesiCreazioneSerie.doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.DECADE.getNumSerie()
                    .doubleValue()) {
                tipoSerieDetail.getNi_transcoded_mm_crea_autom()
                        .setValue(CostantiDB.IntervalliMeseCreazioneSerie.DECADE.name());
            } else if (niMesiCreazioneSerie.doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.QUINDICINA
                    .getNumSerie().doubleValue()) {
                tipoSerieDetail.getNi_transcoded_mm_crea_autom()
                        .setValue(CostantiDB.IntervalliMeseCreazioneSerie.QUINDICINA.name());
            } else if (niMesiCreazioneSerie.doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.MESE.getNumSerie()
                    .doubleValue()) {
                tipoSerieDetail.getNi_transcoded_mm_crea_autom()
                        .setValue(CostantiDB.IntervalliMeseCreazioneSerie.MESE.name());
            } else if (niMesiCreazioneSerie.doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.BIMESTRE
                    .getNumSerie().doubleValue()) {
                tipoSerieDetail.getNi_transcoded_mm_crea_autom()
                        .setValue(CostantiDB.IntervalliMeseCreazioneSerie.BIMESTRE.name());
            } else if (niMesiCreazioneSerie.doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.TRIMESTRE
                    .getNumSerie().doubleValue()) {
                tipoSerieDetail.getNi_transcoded_mm_crea_autom()
                        .setValue(CostantiDB.IntervalliMeseCreazioneSerie.TRIMESTRE.name());
            } else if (niMesiCreazioneSerie.doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.QUADRIMESTRE
                    .getNumSerie().doubleValue()) {
                tipoSerieDetail.getNi_transcoded_mm_crea_autom()
                        .setValue(CostantiDB.IntervalliMeseCreazioneSerie.QUADRIMESTRE.name());
            } else if (niMesiCreazioneSerie.doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.SEMESTRE
                    .getNumSerie().doubleValue()) {
                tipoSerieDetail.getNi_transcoded_mm_crea_autom()
                        .setValue(CostantiDB.IntervalliMeseCreazioneSerie.SEMESTRE.name());
            }
        }

        BigDecimal ni_anni_conserv = tipoSerieDetail.getNi_anni_conserv().parse();
        if (ni_anni_conserv != null) {
            long anni = ni_anni_conserv.longValue();
            if (anni == 9999) {
                tipoSerieDetail.getConserv_unlimited().setValue(JobConstants.ComboFlag.SI.getValue());
                tipoSerieDetail.getNi_anni_conserv().setValue("");
            } else {
                tipoSerieDetail.getConserv_unlimited().setValue(JobConstants.ComboFlag.NO.getValue());
            }
        }

        tipoSerieDetail.getNi_anni_conserv_padre().setViewMode();

        if (tipoSerieRowBean.getIdTipoSeriePadre() != null) {
            tipoSerieDetail.getViewDettaglioSeriePadre().setEditMode();
            DecTipoSerieRowBean tipoSeriePadreRowBean = tipoSerieEjb
                    .getDecTipoSerieRowBean(tipoSerieRowBean.getIdTipoSeriePadre());
            tipoSerieDetail.getNi_anni_conserv_padre()
                    .setValue(tipoSeriePadreRowBean.getNiAnniConserv().toPlainString());
            tipoSerieDetail.getId_tipo_serie_padre().setValue(tipoSerieRowBean.getIdTipoSeriePadre().toPlainString());
        } else {
            tipoSerieDetail.getViewDettaglioSeriePadre().setViewMode();
        }

        if (tipoSerieDetail.getFl_crea_autom().getValue().equals("0")) {
            tipoSerieDetail.getTi_stato_ver_serie_autom().setDecodeMap(new DecodeMap());
        }

        String cessato = (String) getRequest().getParameter("cessato");
        if ((StringUtils.isNotBlank(cessato) && "1".equals(cessato)) || getRequest().getAttribute("cessato") != null) {
            getForm().getTipologieSerieList().setUserOperations(true, false, false, false);
        }
    }

    @Override
    public void undoDettaglio() throws EMFError {
        String publisher = getLastPublisher();
        if (publisher.equals(Application.Publisher.TIPO_SERIE_DETAIL)
                && getForm().getTipoSerieDetail().getStatus() != null
                && getForm().getTipoSerieDetail().getStatus().toString().equals("update")) {
            visualizzaTipoSerie();
            getForm().getTipoSerieDetail().setViewMode();
            getForm().getTipoSerieDetail().setStatus(Status.view);
            getForm().getTipologieSerieList().setStatus(Status.view);

            forwardToPublisher(Application.Publisher.TIPO_SERIE_DETAIL);
        } else if (publisher.equals(Application.Publisher.REG_TIPO_UNITA_DOC_SERIE_DETAIL)
                && getForm().getTipoSerieRegistriList().getStatus() != null
                && getForm().getTipoSerieRegistriList().getStatus().toString().equals("update")) {
            visualizzaTipoSerieRegistro();
            getForm().getRegistroDetail().setViewMode();
            getForm().getRegistroDetail().setStatus(Status.view);
            getForm().getTipoSerieRegistriList().setStatus(Status.view);

            forwardToPublisher(Application.Publisher.REG_TIPO_UNITA_DOC_SERIE_DETAIL);
        } else {
            goBack();
        }
    }

    @Override
    public void insertDettaglio() throws EMFError {
        try {
            StrutSerieForm form = getForm();
            String lista = getRequest().getParameter("table");

            if (form.getTipologieSerieList().getName().equals(lista)) {
                StrutSerieForm.TipoSerieDetail tipoSerieDetail = form.getTipoSerieDetail();
                tipoSerieDetail.reset();
                // Sono in inserimento e quindi idTipoSerie non \u00e8 valorizzato...
                caricaComboTipoSerie();
                tipoSerieDetail.getFl_annuale().setValue(JobConstants.ComboFlag.NO.getValue());
                tipoSerieDetail.getConserv_unlimited().setValue(JobConstants.ComboFlag.NO.getValue());
                tipoSerieDetail.getFl_crea_autom().setValue(JobConstants.ComboFlag.NO.getValue());
                tipoSerieDetail.getTi_conservazione_serie()
                        .setValue(CostantiDB.TipoConservazioneSerie.IN_ARCHIVIO.name());
                // In questa fase di inserimento rendo editabili campi ambiente/ente/struttura
                // e li popolo coi valori dei filtri di ricerca
                getForm().getTipoSerieDetail().getId_ambiente()
                        .setDecodeMap(getForm().getFiltriTipologieSerie().getId_ambiente().getDecodeMap());
                getForm().getTipoSerieDetail().getId_ambiente()
                        .setValue("" + getForm().getFiltriTipologieSerie().getId_ambiente().parse());
                getForm().getTipoSerieDetail().getId_ente()
                        .setDecodeMap(getForm().getFiltriTipologieSerie().getId_ente().getDecodeMap());
                getForm().getTipoSerieDetail().getId_ente()
                        .setValue("" + getForm().getFiltriTipologieSerie().getId_ente().parse());
                getForm().getTipoSerieDetail().getId_strut()
                        .setDecodeMap(getForm().getFiltriTipologieSerie().getId_strut().getDecodeMap());
                getForm().getTipoSerieDetail().getId_strut()
                        .setValue("" + getForm().getFiltriTipologieSerie().getId_strut().parse());
                getForm().getTipoSerieDetail().getId_ambiente().setReadonly(false);
                getForm().getTipoSerieDetail().getId_ente().setReadonly(false);
                getForm().getTipoSerieDetail().getId_strut().setReadonly(false);
                // Preimposto le date
                Calendar oggi = Calendar.getInstance();
                DateFormat formattatore = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);
                getForm().getTipoSerieDetail().getDt_istituz().setValue(formattatore.format(oggi.getTime()));
                getForm().getTipoSerieDetail().getDt_soppres().setValue("31/12/2444");
                // Tutto editabile tranne Tipo creazione standard e Modello di tipo serie
                tipoSerieDetail.setEditMode();
                tipoSerieDetail.getTi_crea_standard().setViewMode();
                tipoSerieDetail.getNm_modello_tipo_serie().setViewMode();
                tipoSerieDetail.getViewDettaglioSeriePadre().setViewMode();
                tipoSerieDetail.setStatus(BaseElements.Status.insert);
                form.getTipologieSerieList().setStatus(BaseElements.Status.insert);
                forwardToPublisher(Application.Publisher.TIPO_SERIE_DETAIL);
            } else if (form.getTipoSerieRegistriList().getName().equals(lista)) {
                StrutSerieForm.RegistroDetail registroDetail = form.getRegistroDetail();
                registroDetail.clear();
                popolaStrutRif();
                caricaComboRegistroDetail();
                registroDetail.getFl_sel_unita_doc_annul().setValue(JobConstants.ComboFlag.NO.getValue());
                registroDetail.setEditMode();
                registroDetail.getNi_anni_conserv().setViewMode();
                // Setto i campi di ambiente/ente/struttura
                initInputAmbienteEnteStruttura(registroDetail);
                registroDetail.setStatus(BaseElements.Status.insert);
                form.getTipoSerieRegistriList().setStatus(BaseElements.Status.insert);
                form.getTipoSerieRegistriList().setHideUpdateButton(false);
                forwardToPublisher(Application.Publisher.REG_TIPO_UNITA_DOC_SERIE_DETAIL);
            } else if (form.getRegistroRegoleFiltraggioList().getName().equals(lista)) {
                StrutSerieForm.RegolaDiFiltraggioDetail regolaDiFiltraggioDetail = form.getRegolaDiFiltraggioDetail();
                visualizzaRegolaDiFiltraggio(regolaDiFiltraggioDetail, BaseElements.Status.insert);
                regolaDiFiltraggioDetail.setEditMode();
                // Setto i campi di ambiente/ente/struttura
                initInputAmbienteEnteStruttura(regolaDiFiltraggioDetail);
                regolaDiFiltraggioDetail.setStatus(BaseElements.Status.insert);
                form.getRegistroRegoleFiltraggioList().setStatus(BaseElements.Status.insert);
                forwardToPublisher(Application.Publisher.REGOLA_DI_FILTRAGGIO_TIPO_SERIE);
            } else if (getForm().getAssociazioneDatiSpecList().getName().equals(lista)) {
                form.getFiltriDatiSpec().getSalvaFiltriDatiSpec().setHidden(false);
                form.getFiltriDatiSpec().getSalvaFiltriDatiSpec().setEditMode();
                // Setto i campi relativi ad ambiente/ente/struttura
                initInputAmbienteEnteStruttura(form.getFiltriDatiSpec());
                forwardToPublisher(Application.Publisher.FILTRI_DATI_SPEC_ASS_REG_TIPO_UNI_DOC);
            } else if (form.getRegoleDiRappresentazioneList().getName().equals(lista)) {
                form.getRegoleRapprDetail().clear();
                form.getRegoleRapprDetail().setEditMode();
                form.getRegoleRapprDetail().setStatus(BaseElements.Status.insert);
                caricaComboRegoleRappresentazione();
                form.getInserimentoWizard().reset();
                // Setto i campi relativi ad ambiente/ente/struttura
                initInputAmbienteEnteStruttura(form.getRegoleRapprDetail());
                form.getRegoleDiRappresentazioneList().setStatus(BaseElements.Status.insert);
                forwardToPublisher(Application.Publisher.REGOLA_RAPPRESENTAZIONE_DETAIL);
            } else if (form.getRegoleAcquisizioneList().getName().equals(lista)) {
                editRegoleAcquisizione(Status.insert);
                forwardToPublisher(Application.Publisher.REGOLA_ACQUISIZIONE_DETAIL);
            } else if (form.getNoteTipoSerieList().getName().equals(lista)) {
                visualizzaDecNotaTipoSerie(BaseElements.Status.insert);
                forwardToPublisher(Application.Publisher.NOTA_TIPO_SERIE_DETAIL);
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
    }

    private void visualizzaRegolaAcquisizione(BigDecimal idTipoSerie, BaseElements.Status stato) throws EMFError {
        StrutSerieForm form = getForm();

        /* Inizializza sezione e contenuto di Dati Profilo */
        StrutSerieForm.DatiProfiloList datiProfiloList = getForm().getDatiProfiloList();
        datiProfiloList.getNm_campo().setReadonly(true);
        // datiProfiloList.getFl_selezionato().setReadonly(false);
        datiProfiloList.getPg_ord_campo_dato_profilo().setHidden(false);
        getForm().getDatiProfiloSection().setHidden(false);
        getForm().getDatiProfiloSection().setLoadOpened(true);
        /* Carica i dati nella lista Dati Profilo */
        datiProfiloList.setTable(tipoSerieEjb.getDecCampoInpDatiProfilo(idTipoSerie,
                CostantiDB.TipoCampo.DATO_PROFILO.name(), BaseElements.Status.view.equals(stato)));
        datiProfiloList.getTable().setPageSize(300);

        /* Inizializza sezione e contenuto Dati Specifici Tipo Unità Documentaria */
        StrutSerieForm.AttributiTipoUnitaDocList attributiTipoUnitaDocList = getForm().getAttributiTipoUnitaDocList();
        attributiTipoUnitaDocList.getTi_trasform_campo_tipo_unita_doc()
                .setDecodeMap(ComboGetter.getMappaTipoDiTrasformatoreInp());
        // attributiTipoUnitaDocList.getTipo_unita_selezionato().setReadonly(false);
        attributiTipoUnitaDocList.getPg_ord_campo_tipo_unita_doc().setHidden(false);
        getForm().getDatiSpecTipoUdSection().setHidden(true);
        getForm().getDatiSpecTipoUdSection().setLoadOpened(true);

        /* Inizializza sezione e contenuto Dati Specifici Tipo Doc */
        StrutSerieForm.AttributiTipoDocList attributiTipoDocList = getForm().getAttributiTipoDocList();
        attributiTipoDocList.getTi_trasform_campo_tipo_doc().setDecodeMap(ComboGetter.getMappaTipoDiTrasformatoreInp());
        // attributiTipoDocList.getTipo_doc_selezionato().setReadonly(false);
        attributiTipoDocList.getPg_ord_campo_tipo_doc().setHidden(false);
        getForm().getDatiSpecTipoDocSection().setHidden(true);
        getForm().getDatiSpecTipoDocSection().setLoadOpened(true);

        /* Carica i dati specifici nelle liste relative e Tipo Unità Documentaria e Tipo Doc */
        ricaricaDatiSpecificiPerRegolaAcquisizione(idTipoSerie, BaseElements.Status.view.equals(stato));
        int fullSize = 0;
        BaseTableInterface<?> tabletud = form.getAttributiTipoUnitaDocList().getTable();
        if (tabletud != null && !tabletud.isEmpty()) {
            tabletud.setCurrentRowIndex(0);
            fullSize = tabletud.fullSize();
            tabletud.setPageSize(fullSize);
        }

        BaseTableInterface<?> tabletd = form.getAttributiTipoDocList().getTable();
        if (tabletd != null && !tabletd.isEmpty()) {
            tabletd.setCurrentRowIndex(0);
            fullSize = tabletd.fullSize();
            tabletd.setPageSize(fullSize);
        }
        if (BaseElements.Status.view.equals(stato)) {
            DecCampoInpUdTableBean regoleAcquisizionePerTipoSerie = tipoSerieEjb
                    .getRegoleAcquisizionePerTipoSerie(idTipoSerie);
            StrutSerieForm.RegoleAcquisizioneListVW regoleAcquisizioneList = getForm().getRegoleAcquisizioneListVW();
            regoleAcquisizioneList.setTable(regoleAcquisizionePerTipoSerie);

            regoleAcquisizioneList.getTable().setCurrentRowIndex(
                    regoleAcquisizionePerTipoSerie != null ? regoleAcquisizionePerTipoSerie.fullSize() : 0);
            regoleAcquisizioneList.getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            regoleAcquisizioneList.setHideDeleteButton(true);
            regoleAcquisizioneList.setHideDetailButton(true);
            regoleAcquisizioneList.setHideUpdateButton(true);
            regoleAcquisizioneList.setHideInsertButton(true);
            regoleAcquisizioneList.setViewMode();
        } else {
            // datiProfiloList.getFl_selezionato().setEditMode();
            // datiProfiloList.getFl_selezionato().setHidden(false);
            datiProfiloList.getPg_ord_campo_dato_profilo().setEditMode();
            attributiTipoUnitaDocList.getTi_trasform_campo_tipo_unita_doc().setEditMode();
            attributiTipoUnitaDocList.getPg_ord_campo_tipo_unita_doc().setEditMode();
            // attributiTipoUnitaDocList.getTipo_unita_selezionato().setHidden(false);
            // attributiTipoUnitaDocList.getTipo_unita_selezionato().setEditMode();
            attributiTipoDocList.getTi_trasform_campo_tipo_doc().setEditMode();
            // attributiTipoDocList.getTipo_doc_selezionato().setHidden(false);
            // attributiTipoDocList.getTipo_doc_selezionato().setEditMode();
            attributiTipoDocList.getPg_ord_campo_tipo_doc().setEditMode();
        }
        form.getRegoleAcquisizioneDetail().getDl_valore().setHidden(true);
        form.getRegoleAcquisizioneDetail().setStatus(stato);
        form.getRegoleAcquisizioneList().setStatus(stato);

        // Setto i campi relativi ad ambiente/ente/struttura
        initInputAmbienteEnteStruttura(form.getRegoleAcquisizioneDetail());

        popolaStrutRif();
    }

    private void caricaComboRegoleFiltraggio() {
        StrutSerieForm form = getForm();
        form.getFiltriDatiSpecList().getTi_oper().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("operatore", CostantiDB.TipoOperatoreDatiSpec.values()));
        caricaTipoSerieUdRif(form);
    }

    private void visualizzaRegolaDiFiltraggio(StrutSerieForm.RegolaDiFiltraggioDetail regolaDiFiltraggioDetail,
            BaseElements.Status status) throws EMFError {
        StrutSerieForm form = getForm();
        StrutSerieForm.RegistroRegoleFiltraggioList registroRegoleFiltraggioList = form
                .getRegistroRegoleFiltraggioList();
        popolaStrutRif();
        caricaComboRegoleDiFiltraggio();
        DecTipoSerieUdRowBean registro = (DecTipoSerieUdRowBean) form.getTipoSerieRegistriList().getTable()
                .getCurrentRow();
        BigDecimal idTipoUnitaDoc = registro.getIdTipoUnitaDoc();
        if (BaseElements.Status.insert.equals(status)) {
            regolaDiFiltraggioDetail.clear();
            regolaDiFiltraggioDetail.setEditMode();
        } else {
            // FLAG DATI SPECIFICI... per adesso (gennaio 2016) lasciamoli perchè non si sa mai...
            // DecFiltroSelUdRowBean rowBean = ((DecFiltroSelUdRowBean)
            // registroRegoleFiltraggioList.getTable().getCurrentRow());
            // regolaDiFiltraggioDetail.copyFromBean(rowBean);
            // BigDecimal idTipoDocPrinc = rowBean.getIdTipoDocPrinc();
            // if (idTipoDocPrinc != null) {
            // boolean hasDSsuTipiDoc = tipoSerieEjb.getVersioniXsdPerTipoEntita(idTipoDocPrinc,
            // Constants.TipoEntitaSacer.DOC);
            // regolaDiFiltraggioDetail.getFlag_dati_spec_presenti_doc().setChecked(hasDSsuTipiDoc);
            // }

            // Recupero le regole di filtraggio da DB e setto la mappa per la loro gestione "particolare"
            Object[] obj = tipoSerieEjb.getDecFiltroSelUdAndMappaFiltri(registro.getIdTipoSerieUd());
            DecFiltroSelUdTableBean tb = (DecFiltroSelUdTableBean) obj[0];
            getSession().setAttribute("mappaRegoleFiltraggio", (Map<BigDecimal, Map<String, BigDecimal>>) obj[1]);
            DecFiltroSelUdTableBean decFiltroSelUdTableBean = tb;

            // Prendo l'unico rowBean presente
            DecFiltroSelUdRowBean rowBean = decFiltroSelUdTableBean.getRow(0);

            // Ricavo i "tipo documento principale" separati da punto e virgola e li setto nella multiselect
            String idTipoDocSeparatiDaVirgola = rowBean.getString("id_tipo_doc_concatenati");
            String[] parts = idTipoDocSeparatiDaVirgola.split(";");
            regolaDiFiltraggioDetail.getId_tipo_doc_princ().setValues(parts);

            if (BaseElements.Status.view.equals(status)) {
                regolaDiFiltraggioDetail.setViewMode();
                registroRegoleFiltraggioList.setViewMode();
            } else if (BaseElements.Status.update.equals(status)) {
                regolaDiFiltraggioDetail.setEditMode();
            }
        }

        caricaComboRegoleFiltraggio();

        // Setto i campi relativi ad ambiente/ente/struttura
        initInputAmbienteEnteStruttura(regolaDiFiltraggioDetail);

        regolaDiFiltraggioDetail.getTi_filtro().setValue("TIPO_DOC_PRINC");
        regolaDiFiltraggioDetail.setStatus(status);
        registroRegoleFiltraggioList.setStatus(status);
        if (idTipoUnitaDoc != null) {
            boolean hasUnitaDocDatiSpecifici = tipoSerieEjb.getVersioniXsdPerTipoEntita(idTipoUnitaDoc,
                    Constants.TipoEntitaSacer.UNI_DOC);
            if (hasUnitaDocDatiSpecifici) {
                regolaDiFiltraggioDetail.getFlag_dati_spec_presenti_sm().setChecked(true);
                regolaDiFiltraggioDetail.getFlag_dati_spec_presenti_sm().setValue("1");
            } else {
                regolaDiFiltraggioDetail.getFlag_dati_spec_presenti_sm().setChecked(false);
                regolaDiFiltraggioDetail.getFlag_dati_spec_presenti_sm().setValue("0");
            }
        }
    }

    private void popolaStrutRif() {
        StrutSerieForm form = getForm();
        StrutSerieForm.StrutRif strutRif = form.getStrutRif();
        StrutSerieForm.TipoSerieRegistriList tipoSerieRegistriList = form.getTipoSerieRegistriList();
        BigDecimal idTipoSerie = ((DecTipoSerieRowBean) form.getTipologieSerieList().getTable().getCurrentRow())
                .getIdTipoSerie();
        DecTipoSerieRowBean tipoSerieRowBean = tipoSerieEjb.getDecTipoSerieRowBean(idTipoSerie);
        strutRif.getNm_tipo_serie().setValue(tipoSerieRowBean.getNmTipoSerie());
        strutRif.getNm_tipo_serie_padre().setValue(tipoSerieRowBean.getString("nm_serie_padre"));

        if (tipoSerieRegistriList.getTable() != null && !tipoSerieRegistriList.getTable().isEmpty()) {
            BigDecimal idTipoSerieUd = ((DecTipoSerieUdRowBean) tipoSerieRegistriList.getTable().getCurrentRow())
                    .getIdTipoSerieUd();
            DecTipoSerieUdRowBean tipoSerieUdRowBean = tipoSerieEjb.getDecTipoSerieUdById(idTipoSerieUd);
            BigDecimal idRegistroUnitaDoc = tipoSerieUdRowBean.getIdRegistroUnitaDoc();
            BigDecimal idTipoUnitaDoc = tipoSerieUdRowBean.getIdTipoUnitaDoc();

            DecRegistroUnitaDocRowBean registro = registroEjb.getDecRegistroUnitaDocRowBean(idRegistroUnitaDoc, null);
            DecTipoUnitaDocRowBean tipoUnitaDoc = tipoUnitaDocEjb.getDecTipoUnitaDocRowBean(idTipoUnitaDoc, null);
            strutRif.getRegistro_unita_doc().setValue(registro.getCdRegistroUnitaDoc());
            strutRif.getTipo_unita_doc().setValue(tipoUnitaDoc.getNmTipoUnitaDoc());
        }
    }

    @Override
    public void saveDettaglio() throws EMFError {
        String publisher = getLastPublisher();

        if (publisher.equals(Application.Publisher.TIPO_SERIE_DETAIL)) {
            salvaTipoSerie();
        }
        if (publisher.equals(Application.Publisher.REG_TIPO_UNITA_DOC_SERIE_DETAIL)) {
            salvaRegistroTipoSerie();
        }
        if (publisher.equals(Application.Publisher.REGOLA_DI_FILTRAGGIO_TIPO_SERIE)) {
            salvaRegolaDiFiltraggio();
        }
        if (publisher.equals(Application.Publisher.REGOLA_ACQUISIZIONE_DETAIL)) {
            salvaRegoleAcquisizione();
        }
        if (publisher.equals(Application.Publisher.NOTA_TIPO_SERIE_DETAIL)) {
            salvaNotaTipoSerie();
        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                || getNavigationEvent().equals(ListAction.NE_NEXT) || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getForm().getTipologieSerieList().getName().equals(getTableName())) {
                forwardToPublisher(Application.Publisher.TIPO_SERIE_DETAIL);
            }

            if (getForm().getTipoSerieRegistriList().getName().equals(getTableName())) {
                forwardToPublisher(Application.Publisher.REG_TIPO_UNITA_DOC_SERIE_DETAIL);
            }

            if (getForm().getRegistroRegoleFiltraggioList().getName().equals(getTableName())) {
                forwardToPublisher(Application.Publisher.REGOLA_DI_FILTRAGGIO_TIPO_SERIE);
            }

            if (getForm().getAssociazioneDatiSpecList().getName().equals(getTableName())) {
                forwardToPublisher(Application.Publisher.FILTRI_DATI_SPEC_ASS_REG_TIPO_UNI_DOC);
            }

            if (getForm().getRegoleDiRappresentazioneList().getName().equals(getTableName())) {
                if (NE_DETTAGLIO_INSERT.equals(getNavigationEvent())) {
                    getForm().getInserimentoWizard().reset();
                }
                forwardToPublisher(Application.Publisher.REGOLA_RAPPRESENTAZIONE_DETAIL);
            }
            if (getForm().getRegoleAcquisizioneList().getName().equals(getTableName())
                    || getForm().getRegoleAcquisizioneDetail().getName().equals(getTableName())) {
                getForm().getRegoleAcquisizioneDetail().getDl_valore().setHidden(true);
                forwardToPublisher(Application.Publisher.REGOLA_ACQUISIZIONE_DETAIL);
            }
            if (getForm().getNoteTipoSerieList().getName().equals(getTableName())) {
                forwardToPublisher(Application.Publisher.NOTA_TIPO_SERIE_DETAIL);
            }
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.TIPO_SERIE_DETAIL;
    }

    @Override
    public void reloadAfterGoBack(String publisher) {
        try {
            if (Application.Publisher.TIPO_SERIE_DETAIL.equals(publisher)) {
                visualizzaTipoSerie();
            } else if (Application.Publisher.REG_TIPO_UNITA_DOC_SERIE_DETAIL.equals(publisher)) {
                visualizzaTipoSerieRegistro();
                ricaricaListaFiltriDatiSpecificiPerAssociazioneRegistroTipoUnitaDoc();
            } else if (Application.Publisher.GESTIONE_TIPI_SERIE.equals(publisher)) {
                // Lista tipi Serie
                getForm().getTipologieSerieList()
                        .setTable(tipoSerieEjb.getDecTipoSerieTableBean(getUser().getIdUtente(),
                                getForm().getFiltriTipologieSerie().getId_ambiente().parse(),
                                getForm().getFiltriTipologieSerie().getId_ente().parse(),
                                getForm().getFiltriTipologieSerie().getId_strut().parse(),
                                getForm().getFiltriTipologieSerie().getIsAttivo().parse(),
                                getForm().getFiltriTipologieSerie().getTipi_serie_no_gen_modello().parse(),
                                getForm().getFiltriTipologieSerie().getId_modello_tipo_serie().parse()));
                getForm().getTipologieSerieList().getTable().setPageSize(10);
                getForm().getTipologieSerieList().getTable().first();
                getForm().getTipologieSerieList().setUserOperations(true, true, true, true);
                // Imposto come visibile il bottone di ricerca tipologia serie e disabilito la clessidra (per IE)
                getForm().getFiltriTipologieSerie().getRicercaTipologieSerieButton().setEditMode();
                getForm().getFiltriTipologieSerie().getRicercaTipologieSerieButton().setDisableHourGlass(true);
            }
            postLoad();

        } catch (EMFError ex) {
            logger.error("Errore nella procedura reloadAfterGoBack di StrutSerieAction", ex);
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.STRUT_SERIE;
    }

    @Override
    public void viewDettaglioSeriePadre() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    @Override
    public void updateTipoSerieDetail() throws EMFError {
        updateTipologieSerieList();
    }

    @Override
    public void updateTipologieSerieList() throws EMFError {
        BigDecimal idTipoSerie = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable().getCurrentRow())
                .getIdTipoSerie();
        boolean isTipoSerieModificabile = tipoSerieEjb.isTipoSerieModificabile(idTipoSerie);
        if (isTipoSerieModificabile) {
            visualizzaTipoSerie();
            caricaComboTipoSerie();
            StrutSerieForm.TipoSerieDetail tipoSerieDetail = getForm().getTipoSerieDetail();
            if (JobConstants.ComboFlag.SI.getValue().equals(tipoSerieDetail.getConserv_unlimited().parse())) {
                tipoSerieDetail.getNi_anni_conserv().setValue("");
            }
            // Tutto editabile tranne Tipo creazione standard e Modello di tipo serie
            tipoSerieDetail.setEditMode();
            tipoSerieDetail.getTi_crea_standard().setViewMode();
            tipoSerieDetail.getNm_modello_tipo_serie().setViewMode();
            tipoSerieDetail.getViewDettaglioSeriePadre().setViewMode();
            tipoSerieDetail.setStatus(BaseElements.Status.update);
            getForm().getTipologieSerieList().setStatus(BaseElements.Status.update);
            forwardToPublisher(Application.Publisher.TIPO_SERIE_DETAIL);
        } else {
            getForm().getTipologieSerieList().setUserOperations(true, true, true, true);
            forwardToPublisher(Application.Publisher.GESTIONE_TIPI_SERIE);
            getMessageBox().addError("Attenzione: tipologia serie non modificabile");
        }

    }

    @Override
    public void deleteTipologieSerieList() throws EMFError {
        getMessageBox().clear();
        String lastPublisher = getLastPublisher();
        boolean isFromDetailOrIsEmpty = Application.Publisher.TIPO_SERIE_DETAIL.equals(lastPublisher)
                || "".equals(lastPublisher);
        DecTipoSerieRowBean tipoSerieRowBean = null;
        tipoSerieRowBean = (DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable().getCurrentRow();
        int rowIndex = getForm().getTipologieSerieList().getTable().getCurrentRowIndex();
        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.TIPO_SERIE_DETAIL)) {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            } else if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.CREA_STRUTTURA)) {
                StruttureForm formPrecedente = (StruttureForm) SessionManager.getLastExecutionHistory(this.getSession())
                        .getForm();
                param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(formPrecedente,
                        formPrecedente.getTipologieSerieList()));
            } else {
                param.setNomeAzione(
                        SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getTipologieSerieList()));
            }
            tipoSerieEjb.deleteDecTipoSerie(param, tipoSerieRowBean.getIdTipoSerie().longValue());
            getForm().getTipologieSerieList().getTable().remove(rowIndex);
            getMessageBox().addMessage(new Message(Message.MessageLevel.INF, "Tipologia serie eliminata con successo"));
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        } finally {
            if (isFromDetailOrIsEmpty) {
                goBack();
            }
        }
    }

    @Override
    public void deleteTipoSerieDetail() throws EMFError {
        deleteTipologieSerieList();
    }

    @Override
    public void deleteTipoSerieRegistriList() throws EMFError {
        getMessageBox().clear();
        String lastPublisher = getLastPublisher();
        boolean isFromDetailOrIsEmpty = Application.Publisher.REG_TIPO_UNITA_DOC_SERIE_DETAIL.equals(lastPublisher)
                || "".equals(lastPublisher);
        DecTipoSerieUdRowBean decTipoSerieUd = (DecTipoSerieUdRowBean) getForm().getTipoSerieRegistriList().getTable()
                .getCurrentRow();
        try {
            BigDecimal idTipoSerie = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable()
                    .getCurrentRow()).getIdTipoSerie();
            if (serieEjb.checkSerieModificabili(idTipoSerie)) {
                Object[] attributi = new Object[3];
                attributi[0] = decTipoSerieUd;
                attributi[1] = idTipoSerie;
                attributi[2] = isFromDetailOrIsEmpty;
                getSession().setAttribute("salvataggioAttributesDeleteRegistroTipoSerie", attributi);
                getRequest().setAttribute("customBox", true);
            } else {
                eseguiCancellazioneRegistroTipoUdTipoSerie(decTipoSerieUd, idTipoSerie, isFromDetailOrIsEmpty);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            getMessageBox().addError(e.getMessage());
        }
    }

    @Override
    public void deleteRegistroDetail() throws EMFError {

        deleteTipoSerieRegistriList();

    }

    // N.B: VECCHIO METODO NON PIU' UTILIZZATO A PARTIRE DAL 22 GENNAIO 2016. Consigliata rimozione a distanza di tempo
    // /**
    // * Eliminazione di un elemento della lista "Regole di filtraggio". Viene
    // * eliminato il record dalla relativa tabella su DB (DEC_FILTRO_SEL_UD) e
    // * dei record aventi i dati specifici in caso il filtro sia di
    // * TIPO_DOC_PRINC dalle tabelle su DB (DEC_FILTRO_SEL_UD_ATTB e
    // * DEC_CAMPO_OUT_SEL_UD)
    // *
    // * @throws EMFError errore generico
    // */
    // @Override
    // public void deleteRegistroRegoleFiltraggioList() throws EMFError {
    // String lastPublisher = getLastPublisher();
    // boolean isFromDetailOrIsEmpty = Application.Publisher.REGOLA_DI_FILTRAGGIO_TIPO_SERIE.equals(lastPublisher) ||
    // "".equals(lastPublisher);
    // DecFiltroSelUdRowBean decFiltroSelUd = (DecFiltroSelUdRowBean)
    // getForm().getRegistroRegoleFiltraggioList().getTable().getCurrentRow();
    // DecOutSelUdTableBean decOutSelUdTableBean = (DecOutSelUdTableBean)
    // getForm().getRegoleDiRappresentazioneList().getTable();
    //// try {
    //// BigDecimal idTipoSerie = ((DecTipoSerieRowBean)
    // getForm().getTipologieSerieList().getTable().getCurrentRow()).getIdTipoSerie();
    //// tipoSerieEjb.deleteDecFiltroSelUd(decFiltroSelUd, decOutSelUdTableBean, idTipoSerie);
    ////
    //// getMessageBox().addMessage(new Message(Message.MessageLevel.INF, "Regola di filtraggio eliminata con
    // successo"));
    //// if (isFromDetailOrIsEmpty) {
    //// goBack();
    //// } else {
    //// DecTipoSerieUdRowBean tipoSerieUd = ((DecTipoSerieUdRowBean)
    // getForm().getTipoSerieRegistriList().getTable().getCurrentRow());
    //// caricaListeRegistro(tipoSerieUd.getIdTipoSerieUd());
    //// }
    //// } catch (Exception ex) {
    //// getMessageBox().addError(ex.getMessage());
    //// }
    //
    // try {
    // BigDecimal idTipoSerie = ((DecTipoSerieRowBean)
    // getForm().getTipologieSerieList().getTable().getCurrentRow()).getIdTipoSerie();
    // if (serieEjb.checkSerieModificabili(idTipoSerie)) {
    // Object[] attributi = new Object[4];
    // attributi[0] = decFiltroSelUd;
    // attributi[1] = decOutSelUdTableBean;
    // attributi[2] = idTipoSerie;
    // attributi[3] = isFromDetailOrIsEmpty;
    // getSession().setAttribute("salvataggioAttributesDeleteRegoleFiltraggioTipoSerie", attributi);
    // getRequest().setAttribute("customBox", true);
    // } else {
    // eseguiCancellazioneRegoleFiltraggioTipoSerie(decFiltroSelUd, decOutSelUdTableBean, idTipoSerie,
    // isFromDetailOrIsEmpty);
    // }
    // } catch (Exception ex) {
    // getMessageBox().addError(ex.getMessage());
    // }
    //
    // }
    /**
     * "Nuovo" metodo di eliminazione dell'unico record della lista "Regole di filtraggio" relativo al tipo filtro
     * "TIPO_DOC_PRINC". Viene eliminato il record dalla relativa tabella su DB (DEC_FILTRO_SEL_UD) e dei record aventi
     * i dati specifici in caso il filtro sia di TIPO_DOC_PRINC dalle tabelle su DB (DEC_FILTRO_SEL_UD_ATTB e
     * DEC_CAMPO_OUT_SEL_UD)
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteRegistroRegoleFiltraggioList() throws EMFError {
        String lastPublisher = getLastPublisher();
        boolean isFromDetailOrIsEmpty = Application.Publisher.REGOLA_DI_FILTRAGGIO_TIPO_SERIE.equals(lastPublisher)
                || "".equals(lastPublisher);

        // Ricavo il tipo serie ud dalla lista associazione registri/tipoUd
        BigDecimal idTipoSerieUd = ((DecTipoSerieUdTableBean) getForm().getTipoSerieRegistriList().getTable())
                .getCurrentRow().getIdTipoSerieUd();

        // Ricavo la mappa da passare al metodo di cancellazione
        Object[] obj = tipoSerieEjb.getDecFiltroSelUdAndMappaFiltri(idTipoSerieUd);
        Map<BigDecimal, Map<String, BigDecimal>> mappa = (Map<BigDecimal, Map<String, BigDecimal>>) obj[1];

        // Ricavo le regole di rappresentazione che vanno modificate
        // (togliere gli eventuali dati specifici dei tipi doc princ eliminati eliminando le regole di filtraggio)
        DecFiltroSelUdRowBean decFiltroSelUd = (DecFiltroSelUdRowBean) getForm().getRegistroRegoleFiltraggioList()
                .getTable().getCurrentRow();
        decFiltroSelUd.setIdTipoSerieUd(((DecTipoSerieUdTableBean) getForm().getTipoSerieRegistriList().getTable())
                .getCurrentRow().getIdTipoSerieUd());
        DecOutSelUdTableBean decOutSelUdTableBean = (DecOutSelUdTableBean) getForm().getRegoleDiRappresentazioneList()
                .getTable();

        try {
            BigDecimal idTipoSerie = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable()
                    .getCurrentRow()).getIdTipoSerie();
            if (serieEjb.checkSerieModificabili(idTipoSerie)) {
                Object[] attributi = new Object[5];
                attributi[0] = decFiltroSelUd;
                attributi[1] = decOutSelUdTableBean;
                attributi[2] = idTipoSerie;
                attributi[3] = isFromDetailOrIsEmpty;
                attributi[4] = mappa;
                getSession().setAttribute("salvataggioAttributesDeleteRegoleFiltraggioTipoSerie", attributi);
                getRequest().setAttribute("customBox", true);
            } else {
                eseguiCancellazioneRegoleFiltraggioTipoSerie2(decFiltroSelUd, decOutSelUdTableBean, idTipoSerie,
                        isFromDetailOrIsEmpty, mappa);
            }
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
            getMessageBox().addError(ex.getMessage());
        }
    }

    @Override
    public void deleteRegolaDiFiltraggioDetail() throws EMFError {
        deleteRegistroRegoleFiltraggioList();
    }

    @Override
    public void updateRegistroRegoleFiltraggioList() throws EMFError {
        StrutSerieForm.RegolaDiFiltraggioDetail regolaDiFiltraggioDetail = getForm().getRegolaDiFiltraggioDetail();
        visualizzaRegolaDiFiltraggio(regolaDiFiltraggioDetail, BaseElements.Status.update);
        super.updateRegistroRegoleFiltraggioList(); // To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteRegoleDiRappresentazioneList() throws EMFError {
        getMessageBox().clear();
        String lastPublisher = getLastPublisher();
        boolean isFromDetailOrIsEmpty = Application.Publisher.REGOLA_RAPPRESENTAZIONE_DETAIL.equals(lastPublisher)
                || "".equals(lastPublisher);
        DecOutSelUdRowBean decOutSelUdRowBean = (DecOutSelUdRowBean) getForm().getRegoleDiRappresentazioneList()
                .getTable().getCurrentRow();

        try {
            BigDecimal idTipoSerieUd = decOutSelUdRowBean.getIdTipoSerieUd();
            BigDecimal idTipoSerie = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable()
                    .getCurrentRow()).getIdTipoSerie();
            if (serieEjb.checkSerieModificabili(idTipoSerie)) {
                Object[] attributi = new Object[4];
                attributi[0] = decOutSelUdRowBean;
                attributi[1] = idTipoSerie;
                attributi[2] = idTipoSerieUd;
                attributi[3] = isFromDetailOrIsEmpty;
                getSession().setAttribute("salvataggioAttributesDeleteRegoleRappresentazioneTipoSerie", attributi);
                getRequest().setAttribute("customBox", true);
            } else {
                eseguiCancellazioneRegoleRappresentazioneTipoSerie(decOutSelUdRowBean, idTipoSerie, idTipoSerieUd,
                        isFromDetailOrIsEmpty);
            }
        } catch (Exception ex) {
            getMessageBox().addError(ex.getMessage());
        }
    }

    @Override
    public void updateRegolaDiFiltraggioDetail() throws EMFError {
        updateRegistroRegoleFiltraggioList();
    }

    private void caricaComboListeTipoSerie(BigDecimal idTipoSerie) throws EMFError {
        caricaComboTipoSerie();
        caricaListeSerie(idTipoSerie);
    }

    /**
     * Carica le combo della pagina di dettaglio Tipologia Serie
     *
     * @throws EMFError
     *             errore generico
     */
    private void caricaComboTipoSerie() throws EMFError {
        // BigDecimal idStrut = getForm().getStrutRif().getId_strut().parse();
        // Imposto le combo di ambiente/ente/struttura in sola lettura
        BigDecimal idStrut = null;
        if (getForm().getTipoSerieDetail().getId_strut() != null) {
            if (getForm().getTipoSerieDetail().getId_strut().parse() != null) {
                idStrut = getForm().getTipoSerieDetail().getId_strut().parse();
            }
        }

        // Imposto in sola lettura codeste combo
        getForm().getTipoSerieDetail().getId_ambiente().setReadonly(true);
        getForm().getTipoSerieDetail().getId_ente().setReadonly(true);
        getForm().getTipoSerieDetail().getId_strut().setReadonly(true);

        DecodeMapIF mappaGenericFlagSiNo = ComboGetter.getMappaGenericFlagSiNo();
        StrutSerieForm.TipoSerieDetail tipoSerieDetail = getForm().getTipoSerieDetail();
        tipoSerieDetail.getFl_crea_autom().setDecodeMap(mappaGenericFlagSiNo);
        tipoSerieDetail.getFl_annuale().setDecodeMap(mappaGenericFlagSiNo);
        tipoSerieDetail.getFl_controllo_consist_obblig().setDecodeMap(mappaGenericFlagSiNo);
        tipoSerieDetail.getConserv_unlimited().setDecodeMap(mappaGenericFlagSiNo);
        tipoSerieDetail.getTi_sel_ud().setDecodeMap(ComboGetter.getMappaTiSelUd());
        tipoSerieDetail.getTipo_conten_serie().setDecodeMap(ComboGetter.getMappaTipo_conten_Serie());
        tipoSerieDetail.getNi_transcoded_mm_crea_autom().setDecodeMap(ComboGetter.getMappaNiMesiCreazioneSerie());
        if (idStrut != null) {
            DecTipoSerieTableBean seriepadreTB = tipoSerieEjb.getDecTipoSeriePadrePerStrutturaTableBean(idStrut);
            DecodeMap mappaTipoSeriePadre = DecodeMap.Factory.newInstance(seriepadreTB, "id_tipo_serie",
                    "nm_tipo_serie");
            tipoSerieDetail.getId_tipo_serie_padre().setDecodeMap(mappaTipoSeriePadre);
        }
        tipoSerieDetail.getTi_conservazione_serie().setDecodeMap(ComboGetter
                .getMappaSortedGenericEnum("ti_conservazione_serie", CostantiDB.TipoConservazioneSerie.values()));
        tipoSerieDetail.getTi_stato_ver_serie_autom().setDecodeMap(ComboGetter.getMappaSortedGenericEnum(
                "ti_stato_ver_serie_autom", CostantiDB.StatoVersioneSerie.getStatiVerSerieAutom()));
    }

    private void caricaComboRegoleDiFiltraggio() throws EMFError {
        StrutSerieForm.RegolaDiFiltraggioDetail regolaDiFiltraggioDetail = getForm().getRegolaDiFiltraggioDetail();
        DecTipoSerieUdRowBean tipoSerieUdRowBean = ((DecTipoSerieUdRowBean) getForm().getTipoSerieRegistriList()
                .getTable().getCurrentRow());
        regolaDiFiltraggioDetail.getTi_filtro().setDecodeMap(ComboGetter.getMappaTiFiltro());
        regolaDiFiltraggioDetail.getTi_filtro().setValue(CostantiDB.TipoFiltroSerieUd.TIPO_DOC_PRINC.name());

        DecTipoDocTableBean documentiPrincipaliPerTipoUnitaDoc = tipoSerieEjb
                .getDocumentiPrincipaliPerTipoUnitaDoc(tipoSerieUdRowBean.getIdTipoUnitaDoc());
        DecodeMap decodeMapTipoDocPrinc = DecodeMap.Factory.newInstance(documentiPrincipaliPerTipoUnitaDoc,
                "id_tipo_doc", "nm_tipo_doc");
        regolaDiFiltraggioDetail.getId_tipo_doc_princ().setDecodeMap(decodeMapTipoDocPrinc);
    }

    private void caricaListeSerie(BigDecimal idTipoSerie) {
        if (idTipoSerie != null) {
            DecTipoSerieUdTableBean tipoSerieUdTableBean = tipoSerieEjb.getDecTipoSerieUdTableBean(idTipoSerie);
            Iterator<DecTipoSerieUdRowBean> iterator = tipoSerieUdTableBean.iterator();
            while (iterator.hasNext()) {
                DecTipoSerieUdRowBean tipoSerieUdBean = iterator.next();
                BigDecimal idRegistroUnitaDoc = tipoSerieUdBean.getIdRegistroUnitaDoc();
                BigDecimal idTipoUnitaDoc = tipoSerieUdBean.getIdTipoUnitaDoc();
                if (idRegistroUnitaDoc != null) {
                    DecRegistroUnitaDocRowBean regBean = registroEjb.getDecRegistroUnitaDocRowBean(idRegistroUnitaDoc,
                            null);
                    tipoSerieUdBean.setString("cd_registro_unita_doc", regBean.getCdRegistroUnitaDoc());
                    if (regBean.getNiAnniConserv() != null) {
                        tipoSerieUdBean.setString("ni_anni_conserv", regBean.getNiAnniConserv().longValue() == 9999
                                ? "Illimitata" : regBean.getNiAnniConserv().toPlainString());
                    }
                }
                if (idTipoUnitaDoc != null) {
                    DecTipoUnitaDocRowBean tipoDocRow = tipoUnitaDocEjb.getDecTipoUnitaDocRowBean(idTipoUnitaDoc, null);
                    tipoSerieUdBean.setString("nm_tipo_unita_doc", tipoDocRow.getNmTipoUnitaDoc());
                }
            }

            getForm().getTipoSerieRegistriList().setTable(tipoSerieUdTableBean);
            getForm().getTipoSerieRegistriList().getTable().setCurrentRowIndex(0);
            getForm().getTipoSerieRegistriList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            DecCampoInpUdTableBean regoleAcquisizionePerTipoSerie = tipoSerieEjb
                    .getRegoleAcquisizionePerTipoSerie(idTipoSerie);
            StrutSerieForm.RegoleAcquisizioneList regoleAcquisizioneList = getForm().getRegoleAcquisizioneList();
            regoleAcquisizioneList.setTable(regoleAcquisizionePerTipoSerie);
            regoleAcquisizioneList.getTable().setCurrentRowIndex(0);
            regoleAcquisizioneList.getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            regoleAcquisizioneList.setHideDeleteButton(false);
            regoleAcquisizioneList.setHideDetailButton(false);
            regoleAcquisizioneList.setHideUpdateButton(false);
            regoleAcquisizioneList.setHideInsertButton(false);
            regoleAcquisizioneList.setStatus(BaseElements.Status.view);
            regoleAcquisizioneList.setViewMode();
            getForm().getRegoleAcquisizioneDetail().setStatus(BaseElements.Status.view);
            getForm().getRegoleAcquisizioneDetail().setViewMode();
            int cri = 0;
            if (getForm().getNoteTipoSerieList().getTable() != null) {
                cri = getForm().getNoteTipoSerieList().getTable().getCurrentRowIndex();
            }
            DecNotaTipoSerieTableBean notes = tipoSerieEjb.getDecNoteTipoSerie(idTipoSerie);
            notes.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            notes.setCurrentRowIndex(cri);
            StrutSerieForm.NoteTipoSerieList noteTipoSerieList = getForm().getNoteTipoSerieList();
            noteTipoSerieList.setTable(notes);

            // Recupero Tutti i tipi nota per la decodifica nella lista.
            DecTipoNotaSerieTableBean decTipoNotaSerieTableBean = tipoSerieEjb.getDecTipoNotaSerieTableBean(null);
            DecodeMap mappaTipoNotaSerie = new DecodeMap();
            mappaTipoNotaSerie.populatedMap(decTipoNotaSerieTableBean, "id_tipo_nota_serie", "ds_tipo_nota_serie");
            noteTipoSerieList.getId_tipo_nota_serie().setDecodeMap(mappaTipoNotaSerie);

            // Se il tipo serie è modificabile, allora mostro i bottoni di inserimento per le liste
            if (tipoSerieEjb.isTipoSerieModificabile(idTipoSerie)) {
                getForm().getTipoSerieRegistriList().setUserOperations(true, true, true, true);
            } else {
                getForm().getTipoSerieRegistriList().setUserOperations(true, false, false, false);
            }

            getSession().removeAttribute("salvataggioAttributesTipoSerie");
            getSession().removeAttribute("salvataggioAttributesRegistroTipoSerie");
            getSession().removeAttribute("salvataggioAttributesDeleteRegistroTipoSerie");

            String cessato = (String) getRequest().getParameter("cessato");
            if ((StringUtils.isNotBlank(cessato) && "1".equals(cessato))
                    || getRequest().getAttribute("cessato") != null) {
                getForm().getTipoSerieRegistriList().setUserOperations(true, false, false, false);
                getForm().getRegoleAcquisizioneList().setUserOperations(true, false, false, false);
                getForm().getNoteTipoSerieList().setUserOperations(true, false, false, false);
            }
        }
    }

    /**
     * Carica le liste della pagina "Dettaglio Associazione registro - tipologia di Unità documentarie"
     *
     * @param idTipoSerieUd
     *            id tipo serie unita doc
     */
    private void caricaListeRegistro(BigDecimal idTipoSerieUd) {
        StrutSerieForm.RegistroRegoleFiltraggioList registroRegoleFiltraggioList = getForm()
                .getRegistroRegoleFiltraggioList();
        StrutSerieForm.AssociazioneDatiSpecList associazioneDatiSpecList = getForm().getAssociazioneDatiSpecList();
        StrutSerieForm.RegoleDiRappresentazioneList regoleDiRappresentazioneList = getForm()
                .getRegoleDiRappresentazioneList();
        if (idTipoSerieUd != null) {
            // Regole di filtraggio
            // N.B.: si tratta di una lista particolare, di massimo un solo record,
            // costruito sulla base dei record ricavati dalla query di ricerca delle regole di filtraggio su DB
            Object[] obj = tipoSerieEjb.getDecFiltroSelUdAndMappaFiltri(idTipoSerieUd);
            DecFiltroSelUdTableBean tb = (DecFiltroSelUdTableBean) obj[0];
            getSession().setAttribute("mappaRegoleFiltraggio", (Map<BigDecimal, Map<String, BigDecimal>>) obj[1]);
            registroRegoleFiltraggioList.setTable(tb);
            registroRegoleFiltraggioList.getTable().setCurrentRowIndex(0);
            registroRegoleFiltraggioList.getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            // Filtri su Dati specifici
            DecFiltroSelUdAttbTableBean decFiltroSelUdAttbTableBean = tipoSerieEjb
                    .getDecFiltroSelUdAttbList(idTipoSerieUd);
            decFiltroSelUdAttbTableBean.setCurrentRowIndex(0);
            decFiltroSelUdAttbTableBean.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            associazioneDatiSpecList.setTable(decFiltroSelUdAttbTableBean);
            // Regole di rappresentazione
            DecOutSelUdTableBean decOutSelUdTableBean = tipoSerieEjb.getDecOutSelUdTableBean(idTipoSerieUd);
            decOutSelUdTableBean.setCurrentRowIndex(0);
            decOutSelUdTableBean.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            regoleDiRappresentazioneList.setTable(decOutSelUdTableBean);
            regoleDiRappresentazioneList.setHideDetailButton(true);

            // Se il tipo serie è modificabile, allora mostro i bottoni di inserimento per le liste
            BigDecimal idTipoSerie = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable()
                    .getCurrentRow()).getIdTipoSerie();
            if (tipoSerieEjb.isTipoSerieModificabile(idTipoSerie)) {
                // Regole di filtraggio
                getForm().getRegistroRegoleFiltraggioList().setUserOperations(true, true, true, true);
                // Dati specifici
                getForm().getAssociazioneDatiSpecList().setUserOperations(true, true, true, true);
                // Regole di rappresentazione
                getForm().getRegoleDiRappresentazioneList().setUserOperations(false, true, true, true);
            } else {
                // Regole di filtraggio
                getForm().getRegistroRegoleFiltraggioList().setUserOperations(true, false, false, false);
                // Dati specifici
                getForm().getAssociazioneDatiSpecList().setUserOperations(true, false, false, false);
                // Regole di rappresentazione
                getForm().getRegoleDiRappresentazioneList().setUserOperations(false, false, false, false);
            }

            // Se ho già un record con le regole di filtraggio, non mostro il bottone di inserimento
            // N.B.: non viene effettuato nessun controllo circa la natura del tipo filtro, al momento non necessario
            if (registroRegoleFiltraggioList.getTable().isEmpty()) {
                registroRegoleFiltraggioList.setHideInsertButton(false);
            } else {
                registroRegoleFiltraggioList.setHideInsertButton(true);
            }
        }

        String cessato = (String) getRequest().getParameter("cessato");
        if ((StringUtils.isNotBlank(cessato) && "1".equals(cessato)) || getRequest().getAttribute("cessato") != null) {
            registroRegoleFiltraggioList.setUserOperations(true, false, false, false);
            associazioneDatiSpecList.setUserOperations(true, false, false, false);
            regoleDiRappresentazioneList.setUserOperations(true, false, false, false);
        }
    }

    private void checkObbligatori(DecTipoSerieRowBean rowBean, MessageBox msgBox) {

        if (StringUtils.isEmpty(rowBean.getNmTipoSerie())) {
            msgBox.addError("Attenzione : il campo nome della tipologia di seria \u00E8 obbligatorio <br/>");
        }
        if (StringUtils.isEmpty(rowBean.getTipoContenSerie())) {
            msgBox.addError("Attenzione : il campo tipo contenuto \u00E8 obbligatorio <br/>");
        }
        if (StringUtils.isEmpty(rowBean.getTiSelUd())) {
            msgBox.addError(
                    "Attenzione : il campo tipo di selezione delle unit\u00E0 documentarie \u00E8 obbligatorio <br/>");
        }

        checkAnniConservazioneIllimitata(rowBean.getConserv_unlimited(), rowBean.getNiAnniConserv());

        if (rowBean.getIdTipoSeriePadre() != null) {

            DecTipoSerieRowBean decTipoSeriePadreRowBean = tipoSerieEjb
                    .getDecTipoSerieRowBean(rowBean.getIdTipoSeriePadre());
            BigDecimal niAnniPadre = decTipoSeriePadreRowBean.getNiAnniConserv();
            BigDecimal niAnniSerie = rowBean.getNiAnniConserv();
            if (niAnniSerie.compareTo(niAnniPadre) > 0) {
                msgBox.addError(
                        "Il tempo di conservazione non deve essere superiore a quello della tipologia di serie di appartenenza<br/>");
            }
        }
        if (rowBean.getGgCreaAutom() != null) {
            Pattern pattern = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])\\/(0?[1-9]|1[012])");
            Matcher matcher = pattern.matcher(rowBean.getGgCreaAutom());
            if (!matcher.matches()) {
                getMessageBox().addError(
                        "Errore di compilazione form: formato campo 'Giorno di creazione in automatico della serie' non corretto. Da valorizzare nel formato gg/mm <br/>");
            }
        }
        BigDecimal aaIniCreaAutom = rowBean.getAaIniCreaAutom();
        BigDecimal aaFinCreaAutom = rowBean.getAaFinCreaAutom();
        if (aaFinCreaAutom != null && aaIniCreaAutom != null && aaIniCreaAutom.compareTo(aaFinCreaAutom) > 0) {
            msgBox.addError("L'anno di fine periodo non pu\u00F2 essere minore dell'anno di inizio <br/>");
        }

        if (JobConstants.ComboFlag.SI.getValue().equals(rowBean.getFlCreaAutom())) {
            if (rowBean.getCdSerieDefault() == null || rowBean.getDsSerieDefault() == null
                    || rowBean.getGgCreaAutom() == null || rowBean.getAaIniCreaAutom() == null
                    || rowBean.getTiStatoVerSerieAutom() == null) {
                getMessageBox().addError(
                        "Errore di compilazione form: \u00E8 stata abilitata la creazione in automatico ma uno o pi\u00F9 campi non sono stati compilati! <br/>");
            }
            // else if (rowBean.getTiSelUd().equals(CostantiDB.TipoSelUdTipiSerie.DT_UD_SERIE.name()) &&
            // rowBean.getNiMmCreaAutom() == null) {
            // getMessageBox().addError("Errore di compilazione form: deve essere valorizzato anche il campo '" +
            // getForm().getTipoSerieDetail().getNi_transcoded_mm_crea_autom().getDescription()+ "'<br/>");
            // }
        }

        if (StringUtils.isNotBlank(rowBean.getCdSerieDefault())) {
            if (!CD_SERIE_PATTERN.matcher(rowBean.getCdSerieDefault()).matches()) {
                getMessageBox().addError(
                        "Errore di compilazione form - caratteri consentiti per il codice: lettere, numeri,.,-,_,: <br/>");
            }
        }
        if (!rowBean.getTiSelUd().equals(CostantiDB.TipoSelUdTipiSerie.DT_UD_SERIE.name())
                && rowBean.getNiAaSelUd() != null) {
            msgBox.addError("Il campo '" + getForm().getTipoSerieDetail().getNi_aa_sel_ud().getName()
                    + "' deve essere valorizzato solo nel caso in cui il campo '"
                    + getForm().getTipoSerieDetail().getTi_sel_ud().getDescription() + "' sia uguale a '"
                    + CostantiDB.TipoSelUdTipiSerie.DT_UD_SERIE.toString() + "'");
        }
    }

    private void checkAnniConservazioneIllimitata(String unlimited, BigDecimal anniConserv) {
        boolean error = false;
        if (unlimited != null && anniConserv != null) {
            if ((unlimited.equals("1") && !anniConserv.equals(new BigDecimal(9999)))
                    || ((unlimited.equals("0") && anniConserv.equals(new BigDecimal(9999))))) {
                error = true;
            }
        }
        if ((anniConserv == null && StringUtils.isBlank(unlimited))
                || ((unlimited != null && unlimited.equals("0") && anniConserv == null))) {
            error = true;
        }
        if (error) {
            getMessageBox().addError("'Anni di conservazione' \u00E8 alternativo a 'Conservazione illimitata'");
        }
    }

    private void salvaTipoSerie() throws EMFError {

        MessageBox msgBox = getMessageBox();
        msgBox.clear();
        StrutSerieForm.TipoSerieDetail tipoSerieDetail = getForm().getTipoSerieDetail();
        tipoSerieDetail.post(getRequest());
        boolean validate = tipoSerieDetail.validate(msgBox);

        if (validate) {
            if (msgBox.isEmpty()) {
                DecTipoSerieRowBean rowBean = new DecTipoSerieRowBean();
                try {
                    tipoSerieDetail.copyToBean(rowBean);
                    String niMmCreaAutomTranscode = tipoSerieDetail.getNi_transcoded_mm_crea_autom().parse();
                    if (StringUtils.isNotBlank(niMmCreaAutomTranscode)) {
                        if (niMmCreaAutomTranscode.equals(CostantiDB.IntervalliMeseCreazioneSerie.DECADE.name())) {
                            rowBean.setNiMmCreaAutom(CostantiDB.IntervalliMeseCreazioneSerie.DECADE.getNumSerie());
                        } else if (niMmCreaAutomTranscode
                                .equals(CostantiDB.IntervalliMeseCreazioneSerie.QUINDICINA.name())) {
                            rowBean.setNiMmCreaAutom(CostantiDB.IntervalliMeseCreazioneSerie.QUINDICINA.getNumSerie());
                        } else if (niMmCreaAutomTranscode.equals(CostantiDB.IntervalliMeseCreazioneSerie.MESE.name())) {
                            rowBean.setNiMmCreaAutom(CostantiDB.IntervalliMeseCreazioneSerie.MESE.getNumSerie());
                        } else if (niMmCreaAutomTranscode
                                .equals(CostantiDB.IntervalliMeseCreazioneSerie.BIMESTRE.name())) {
                            rowBean.setNiMmCreaAutom(CostantiDB.IntervalliMeseCreazioneSerie.BIMESTRE.getNumSerie());
                        } else if (niMmCreaAutomTranscode
                                .equals(CostantiDB.IntervalliMeseCreazioneSerie.TRIMESTRE.name())) {
                            rowBean.setNiMmCreaAutom(CostantiDB.IntervalliMeseCreazioneSerie.TRIMESTRE.getNumSerie());
                        } else if (niMmCreaAutomTranscode
                                .equals(CostantiDB.IntervalliMeseCreazioneSerie.QUADRIMESTRE.name())) {
                            rowBean.setNiMmCreaAutom(
                                    CostantiDB.IntervalliMeseCreazioneSerie.QUADRIMESTRE.getNumSerie());
                        } else if (niMmCreaAutomTranscode
                                .equals(CostantiDB.IntervalliMeseCreazioneSerie.SEMESTRE.name())) {
                            rowBean.setNiMmCreaAutom(CostantiDB.IntervalliMeseCreazioneSerie.SEMESTRE.getNumSerie());
                        } else {
                            msgBox.addError("Errore inaspettato nel controllo del campo '"
                                    + tipoSerieDetail.getNi_transcoded_mm_crea_autom().getDescription() + "'");
                        }
                    } else {
                        rowBean.setNiMmCreaAutom(null);
                    }
                    checkObbligatori(rowBean, msgBox);

                    if (msgBox.isEmpty()) {
                        if (JobConstants.DB_TRUE.equals(rowBean.getConserv_unlimited())) {
                            rowBean.setNiAnniConserv(new BigDecimal(9999));
                        }
                        rowBean.setFlTipoSeriePadre("0");
                        // BigDecimal idStrut = getForm().getStrutRif().getId_strut().parse();
                        BigDecimal idStrut = getForm().getTipoSerieDetail().getId_strut().parse();
                        rowBean.setIdStrut(idStrut);
                        /*
                         * Codice aggiuntivo per il logging...
                         */
                        LogParam param = SpagoliteLogUtil.getLogParam(
                                configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null,
                                        null, null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                        if (tipoSerieDetail.getStatus().equals(BaseElements.Status.insert)) {
                            param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                            tipoSerieEjb.insertDecTipoSerie(param, rowBean);
                            rowBean = tipoSerieEjb.getDecTipoSerieRowBeanByNameAndIdStrut(rowBean.getNmTipoSerie(),
                                    idStrut.longValue());
                            DecTipoSerieTableBean tableBean = new DecTipoSerieTableBean();
                            tableBean.add(rowBean);
                            tableBean.setCurrentRowIndex(0);
                            tableBean.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                            getForm().getTipologieSerieList().setTable(tableBean);
                            msgBox.addInfo("Nuova Tipologia di serie inserita con successo");
                        } else if (tipoSerieDetail.getStatus().equals(BaseElements.Status.update)) {
                            param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                            // Eseguo la post dei campi e controllo se ho modificato alcuni campi
                            boolean campiModificati = checkModificheCampiTipoSerieDetail(
                                    ((DecTipoSerieTableBean) getForm().getTipologieSerieList().getTable())
                                            .getCurrentRow().getIdTipoSerie());
                            BigDecimal idTipoSerie = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable()
                                    .getCurrentRow()).getIdTipoSerie();
                            if (serieEjb.checkSerieModificabili(idTipoSerie) && campiModificati) {
                                Object[] attributi = new Object[2];
                                attributi[0] = rowBean;
                                attributi[1] = idTipoSerie;
                                getSession().setAttribute("salvataggioAttributesTipoSerie", attributi);
                                getRequest().setAttribute("customBox", true);
                            } else {
                                eseguiModificaTipoSerie(param, rowBean, idTipoSerie);
                            }
                        }
                        // Aggiungere gestione redirect dettaglio.
                        tipoSerieDetail.setViewMode();
                        tipoSerieDetail.setStatus(BaseElements.Status.view);
                        // caricaListeSerie(rowBean.getIdTipoSerie());
                        getForm().getTipologieSerieList().setStatus(BaseElements.Status.view);
                        msgBox.setViewMode(MessageBox.ViewMode.plain);
                        forwardToPublisher(Application.Publisher.TIPO_SERIE_DETAIL);
                    }
                } catch (EMFError | ParerUserError ex) {
                    logger.error(ex.getLocalizedMessage(), ex);
                    msgBox.addError(ex.getMessage());
                    forwardToPublisher(Application.Publisher.TIPO_SERIE_DETAIL);
                } catch (Exception ex) {
                    logger.error(ex.getLocalizedMessage(), ex);
                    msgBox.addError(ex.getMessage());
                    forwardToPublisher(Application.Publisher.TIPO_SERIE_DETAIL);
                }
            }
        }
    }

    /**
     * Verifica che siano state apportate modifiche ad almeno uno dei seguenti campi di Dettaglio Tipologia Serie: -
     * nmTipoSerie; - cdSerieDefault; - dsSerieDefault; - flConservIllimit; - niAnniConserv; - tiConservazioneSerie; -
     * tipoContenSerie; - tiSelUd; - niAaSelUd; - niUnitaDocVolume; - flControlloConsistObblig; - idTipoSeriePadre;
     *
     * @param idTipoSerie
     *            id tipo serie
     * 
     * @return true o false, a seconda che uno o più campi siano stati modificati
     * 
     * @throws EMFError
     *             errore generico
     */
    private boolean checkModificheCampiTipoSerieDetail(BigDecimal idTipoSerie) throws EMFError {
        // Ricavo i valori da DB, ovvero i valori dei campi prima di eventuali modifiche
        DecTipoSerieRowBean tipoSeriePreRowBean = tipoSerieEjb.getDecTipoSerieRowBean(idTipoSerie);
        String nmTipoSeriePreMod = tipoSeriePreRowBean.getNmTipoSerie();
        String cdSerieDefaultPreMod = tipoSeriePreRowBean.getCdSerieDefault();
        String dsSerieDefaultPreMod = tipoSeriePreRowBean.getDsSerieDefault();
        String niAnniConservPreMod = tipoSeriePreRowBean.getNiAnniConserv() != null
                ? tipoSeriePreRowBean.getNiAnniConserv().toString() : "";
        if (niAnniConservPreMod.equals("9999")) {
            tipoSeriePreRowBean.setConserv_unlimited(JobConstants.ComboFlag.SI.getValue());
        } else {
            tipoSeriePreRowBean.setConserv_unlimited(JobConstants.ComboFlag.NO.getValue());
        }
        String flConservIllimitPreMod = tipoSeriePreRowBean.getConserv_unlimited();
        String tiConservazioneSeriePreMod = tipoSeriePreRowBean.getTiConservazioneSerie();
        String tipoContenSeriePreMod = tipoSeriePreRowBean.getTipoContenSerie();
        String tiSelUdPreMod = tipoSeriePreRowBean.getTiSelUd();
        String niAaSelUdPreMod = tipoSeriePreRowBean.getNiAaSelUd() != null
                ? tipoSeriePreRowBean.getNiAaSelUd().toString() : "";
        String niUnitaDocVolumePreMod = tipoSeriePreRowBean.getNiUnitaDocVolume() != null
                ? tipoSeriePreRowBean.getNiUnitaDocVolume().toString() : "";
        String flControlloConsistObbligPreMod = getForm().getTipoSerieDetail().getFl_controllo_consist_obblig().parse();
        String idTipoSeriePadrePreMod = tipoSeriePreRowBean.getIdTipoSeriePadre() != null
                ? tipoSeriePreRowBean.getIdTipoSeriePadre().toString() : "";
        // Ricavo i valori dopo eventuali modifiche
        String nmTipoSeriePostMod = getForm().getTipoSerieDetail().getNm_tipo_serie().parse();
        String cdSerieDefaultPostMod = getForm().getTipoSerieDetail().getCd_serie_default().parse();
        String dsSerieDefaultPostMod = getForm().getTipoSerieDetail().getDs_serie_default().parse();
        String flConservIllimitPostMod = getForm().getTipoSerieDetail().getConserv_unlimited().parse();
        String niAnniConservPostMod = getForm().getTipoSerieDetail().getNi_anni_conserv().parse() != null
                ? getForm().getTipoSerieDetail().getNi_anni_conserv().parse().toString() : "";
        String tiConservazioneSeriePostMod = getForm().getTipoSerieDetail().getTi_conservazione_serie().parse();
        String tipoContenSeriePostMod = getForm().getTipoSerieDetail().getTipo_conten_serie().parse();
        String tiSelUdPostMod = getForm().getTipoSerieDetail().getTi_sel_ud().parse();
        String niAaSelUdPostMod = getForm().getTipoSerieDetail().getNi_aa_sel_ud().parse() != null
                ? getForm().getTipoSerieDetail().getNi_aa_sel_ud().parse().toString() : "";
        String niUnitaDocVolumePostMod = getForm().getTipoSerieDetail().getNi_unita_doc_volume().parse() != null
                ? getForm().getTipoSerieDetail().getNi_unita_doc_volume().parse().toString() : "";
        String flControlloConsistObbligPostMod = getForm().getTipoSerieDetail().getFl_controllo_consist_obblig()
                .parse();
        String idTipoSeriePadrePostMod = getForm().getTipoSerieDetail().getId_tipo_serie_padre().parse() != null
                ? getForm().getTipoSerieDetail().getId_tipo_serie_padre().parse().toString() : "";

        // Controllo se ci sono state modifiche, non considerando il flagConservazione
        boolean mod1 = !StringUtils.equals(nmTipoSeriePreMod, nmTipoSeriePostMod)
                || !StringUtils.equals(cdSerieDefaultPreMod, cdSerieDefaultPostMod)
                || !StringUtils.equals(dsSerieDefaultPreMod, dsSerieDefaultPostMod)
                || !StringUtils.equals(flConservIllimitPreMod, flConservIllimitPostMod)
                || !StringUtils.equals(tiConservazioneSeriePreMod, tiConservazioneSeriePostMod)
                || !StringUtils.equals(tipoContenSeriePreMod, tipoContenSeriePostMod)
                || !StringUtils.equals(tiSelUdPreMod, tiSelUdPostMod)
                || !StringUtils.equals(niAaSelUdPreMod, niAaSelUdPostMod)
                || !StringUtils.equals(niUnitaDocVolumePreMod, niUnitaDocVolumePostMod)
                || !StringUtils.equals(flControlloConsistObbligPreMod, flControlloConsistObbligPostMod)
                || !StringUtils.equals(idTipoSeriePadrePreMod, idTipoSeriePadrePostMod);

        if (!mod1) {
            if (flConservIllimitPostMod.equals("0")) {
                return !StringUtils.equals(niAnniConservPreMod, niAnniConservPostMod);
            }
        }
        return mod1;
    }

    /**
     * Verifica che siano state apportate modifiche ad almeno uno dei seguenti campi di Dettaglio Tipologia Serie: -
     * dsTipoSerie; - flCreaAutom; - ggCreaAutom; - aaIniCreaAutom; - aaFinCreaAutom; - niTranscodedMmCreaAutom;
     *
     * @param idTipoSerie
     *            id tipo serie
     * 
     * @return true o false, a seconda che uno o più campi siano stati modificati
     * 
     * @throws EMFError
     *             errore generico
     */
    private boolean checkModificheCampiCreazioneAutomTipoSerieDetail(BigDecimal idTipoSerie) throws EMFError {
        // Ricavo i valori da DB, ovvero i valori dei campi prima di eventuali modifiche
        DecTipoSerieRowBean tipoSeriePreRowBean = tipoSerieEjb.getDecTipoSerieRowBean(idTipoSerie);
        String dsTipoSeriePreMod = tipoSeriePreRowBean.getDsTipoSerie() != null ? tipoSeriePreRowBean.getDsTipoSerie()
                : "";
        String flCreaAutomPreMod = tipoSeriePreRowBean.getFlCreaAutom() != null ? tipoSeriePreRowBean.getFlCreaAutom()
                : "";
        String ggCreaAutomPreMod = tipoSeriePreRowBean.getGgCreaAutom() != null ? tipoSeriePreRowBean.getGgCreaAutom()
                : "";
        String aaIniCreaAutomPreMod = tipoSeriePreRowBean.getAaIniCreaAutom() != null
                ? tipoSeriePreRowBean.getAaIniCreaAutom().toString() : "";
        String aaFinCreaAutomPreMod = tipoSeriePreRowBean.getAaFinCreaAutom() != null
                ? tipoSeriePreRowBean.getAaFinCreaAutom().toString() : "";
        String niTranscodedMmCreaAutomPreMod = tipoSeriePreRowBean.getNiMmCreaAutom() != null
                ? tipoSeriePreRowBean.getNiMmCreaAutom().toString() : "";

        // Ricavo i valori dopo eventuali modifiche
        String dsTipoSeriePostMod = getForm().getTipoSerieDetail().getDs_tipo_serie().parse();
        String flCreaAutomPostMod = getForm().getTipoSerieDetail().getFl_crea_autom().parse();
        String ggCreaAutomPostMod = getForm().getTipoSerieDetail().getGg_crea_autom().parse();
        String aaIniCreaAutomPostMod = getForm().getTipoSerieDetail().getAa_ini_crea_autom() != null
                ? getForm().getTipoSerieDetail().getAa_ini_crea_autom().toString() : "";
        String aaFinCreaAutomPostMod = getForm().getTipoSerieDetail().getAa_fin_crea_autom() != null
                ? getForm().getTipoSerieDetail().getAa_fin_crea_autom().toString() : "";
        String niTranscodedMmCreaAutomPostMod = getForm().getTipoSerieDetail().getNi_transcoded_mm_crea_autom() != null
                ? getForm().getTipoSerieDetail().getNi_transcoded_mm_crea_autom().toString() : "";

        // Controllo se ci sono state modifiche
        return !StringUtils.equals(dsTipoSeriePreMod, dsTipoSeriePostMod)
                || !StringUtils.equals(flCreaAutomPreMod, flCreaAutomPostMod)
                || !StringUtils.equals(ggCreaAutomPreMod, ggCreaAutomPostMod)
                || !StringUtils.equals(aaIniCreaAutomPreMod, aaIniCreaAutomPostMod)
                || !StringUtils.equals(aaFinCreaAutomPreMod, aaFinCreaAutomPostMod)
                || !StringUtils.equals(niTranscodedMmCreaAutomPreMod, niTranscodedMmCreaAutomPostMod);
    }

    /**
     * Metodo chiamato al click del tasto 'SI' della finestra javascript
     * /js/sips/customTipologiaSerieVincolataMessageBox.js Essendo utilizzata in varie pagine, a seconda del tipo di
     * parametri salvati in sessione, gestisce il caso relativo
     *
     */
    @Override
    public void confermaSalvataggioTipoSerie() {
        /* SALVATAGGIO TIPO SERIE */
        if (getSession().getAttribute("salvataggioAttributesTipoSerie") != null) {
            Object[] attributi = (Object[]) getSession().getAttribute("salvataggioAttributesTipoSerie");
            DecTipoSerieRowBean rowBean = (DecTipoSerieRowBean) attributi[0];
            BigDecimal idTipoSerie = (BigDecimal) attributi[1];
            try {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null,
                                null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                eseguiModificaTipoSerie(param, rowBean, idTipoSerie);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
                getMessageBox().addError(e.getMessage());
            }
            getSession().removeAttribute("salvataggioAttributesTipoSerie");
            forwardToPublisher(Application.Publisher.TIPO_SERIE_DETAIL);
        }

        /* MODIFICA ASSOCIAZIONE REGISTRI-TIPI UD DEL TIPO SERIE */
        if ((getSession().getAttribute("salvataggioAttributesRegistroTipoSerie") != null)) {
            BaseElements.Status status = getForm().getTipoSerieRegistriList().getStatus();
            Object[] attributi = (Object[]) getSession().getAttribute("salvataggioAttributesRegistroTipoSerie");
            try {
                eseguiModificaRegistroTipoUdTipoSerie((String) attributi[0], (Set<String>) attributi[1],
                        (BigDecimal) attributi[2], (String) attributi[3], (BigDecimal) attributi[4], status);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
                getMessageBox().addError(e.getMessage());
            }
            getSession().removeAttribute("salvataggioAttributesRegistroTipoSerie");
            forwardToPublisher(Application.Publisher.REG_TIPO_UNITA_DOC_SERIE_DETAIL);
        }

        /* CANCELLAZIONE ASSOCIAZIONE REGISTRI-TIPI UD DEL TIPO SERIE */
        if ((getSession().getAttribute("salvataggioAttributesDeleteRegistroTipoSerie") != null)) {
            Object[] attributi = (Object[]) getSession().getAttribute("salvataggioAttributesDeleteRegistroTipoSerie");
            DecTipoSerieUdRowBean rowBean = (DecTipoSerieUdRowBean) attributi[0];
            BigDecimal idTipoSerie = (BigDecimal) attributi[1];
            boolean isFromDetailOrIsEmpty = (boolean) attributi[2];
            try {
                eseguiCancellazioneRegistroTipoUdTipoSerie(rowBean, idTipoSerie, isFromDetailOrIsEmpty);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
                getMessageBox().addError(e.getMessage());
            }
            getSession().removeAttribute("salvataggioAttributesDeleteRegistroTipoSerie");
        }

        /* SALVATAGGIO REGOLE DI FILTRAGGIO DEL TIPO SERIE */
        if (getSession().getAttribute("salvataggioAttributesRegoleFiltraggioTipoSerie") != null) {
            Object[] attributi = (Object[]) getSession().getAttribute("salvataggioAttributesRegoleFiltraggioTipoSerie");
            DecFiltroSelUdRowBean decFiltroSelUdRowBean = (DecFiltroSelUdRowBean) attributi[0];
            BigDecimal idTipoSerie = (BigDecimal) attributi[1];
            Status status = (Status) attributi[2];
            Map<BigDecimal, Map<String, BigDecimal>> mappa = (Map<BigDecimal, Map<String, BigDecimal>>) attributi[3];
            try {
                eseguiModificaRegoleFiltraggioTipoSerie2(decFiltroSelUdRowBean, idTipoSerie, status, mappa);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
                getMessageBox().addError(e.getMessage());
            }
            getSession().removeAttribute("salvataggioAttributesRegoleFiltraggioTipoSerie");
            getSession().removeAttribute("mappaRegoleFiltraggio");
            forwardToPublisher(Application.Publisher.REGOLA_DI_FILTRAGGIO_TIPO_SERIE);
        }

        /* CANCELLAZIONE REGOLE DI FILTRAGGIO DEL TIPO SERIE */
        if ((getSession().getAttribute("salvataggioAttributesDeleteRegoleFiltraggioTipoSerie") != null)) {
            Object[] attributi = (Object[]) getSession()
                    .getAttribute("salvataggioAttributesDeleteRegoleFiltraggioTipoSerie");
            DecFiltroSelUdRowBean decFiltroSelUd = (DecFiltroSelUdRowBean) attributi[0];
            DecOutSelUdTableBean decOutSelUdTableBean = (DecOutSelUdTableBean) attributi[1];
            BigDecimal idTipoSerie = (BigDecimal) attributi[2];
            boolean isFromDetailOrIsEmpty = (boolean) attributi[3];
            Map<BigDecimal, Map<String, BigDecimal>> mappa = (Map<BigDecimal, Map<String, BigDecimal>>) attributi[4];
            try {
                eseguiCancellazioneRegoleFiltraggioTipoSerie2(decFiltroSelUd, decOutSelUdTableBean, idTipoSerie,
                        isFromDetailOrIsEmpty, mappa);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
                getMessageBox().addError(e.getMessage());
            }
            getSession().removeAttribute("salvataggioAttributesDeleteRegoleFiltraggioTipoSerie");
            getSession().removeAttribute("mappaRegoleFiltraggio");
        }

        /* SALVATAGGIO REGOLE DI RAPPRESENTAZIONE TIPO SERIE */
        if (getSession().getAttribute("salvataggioAttributesRegoleRappresentazioneTipoSerie") != null) {
            Object[] attributi = (Object[]) getSession()
                    .getAttribute("salvataggioAttributesRegoleRappresentazioneTipoSerie");
            DecTipoSerieUdRowBean decTipoSerieUd = (DecTipoSerieUdRowBean) attributi[0];
            DecOutSelUdRowBean rowBean = (DecOutSelUdRowBean) attributi[1];
            Map<String, Map<String, String>> listaAttributiSelezionati = (Map<String, Map<String, String>>) attributi[2];
            String tiOut = (String) attributi[3];
            String dlFormatoOut = (String) attributi[4];
            Status status = (Status) attributi[5];
            BigDecimal idTipoSerie = (BigDecimal) attributi[6];
            try {
                eseguiModificaRegoleRappresentazioneTipoSerie(decTipoSerieUd, rowBean, listaAttributiSelezionati, tiOut,
                        dlFormatoOut, status, idTipoSerie);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
                getMessageBox().addError(e.getMessage());
            }
            getSession().removeAttribute("salvataggioAttributesRegoleRappresentazioneTipoSerie");
        }

        /* CANCELLAZIONE REGOLE DI RAPPRESENTAZIONE TIPO SERIE */
        if ((getSession().getAttribute("salvataggioAttributesDeleteRegoleRappresentazioneTipoSerie") != null)) {
            Object[] attributi = (Object[]) getSession()
                    .getAttribute("salvataggioAttributesDeleteRegoleRappresentazioneTipoSerie");
            DecOutSelUdRowBean decOutSelUdRowBean = (DecOutSelUdRowBean) attributi[0];
            BigDecimal idTipoSerie = (BigDecimal) attributi[1];
            BigDecimal idTipoSerieUd = (BigDecimal) attributi[2];
            boolean isFromDetailOrIsEmpty = (boolean) attributi[3];
            try {
                eseguiCancellazioneRegoleRappresentazioneTipoSerie(decOutSelUdRowBean, idTipoSerie, idTipoSerieUd,
                        isFromDetailOrIsEmpty);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
                getMessageBox().addError(e.getMessage());
            }
            getSession().removeAttribute("salvataggioAttributesDeleteRegoleRappresentazioneTipoSerie");
        }

        /* SALVATAGGIO DATI SPECIFICI DEL TIPO SERIE */
        if ((getSession().getAttribute("salvataggioAttributesDatiSpecificiTipoSerie") != null)) {
            Object[] attributi = (Object[]) getSession().getAttribute("salvataggioAttributesDatiSpecificiTipoSerie");
            List<DecFiltroSelUdAttbBean> listaDatiSpecOnLine = (List<DecFiltroSelUdAttbBean>) attributi[0];
            DecFiltroSelUdAttbTableBean table = (DecFiltroSelUdAttbTableBean) attributi[1];
            BigDecimal idTipoSerieUd = (BigDecimal) attributi[2];
            BigDecimal idTipoSerie = (BigDecimal) attributi[3];
            try {
                eseguiModificaDatiSpecificiTipoSerie(listaDatiSpecOnLine, table, idTipoSerieUd, idTipoSerie);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
                getMessageBox().addError(e.getMessage());
            }
            getSession().removeAttribute("salvataggioAttributesDatiSpecificiTipoSerie");
            forwardToPublisher(Application.Publisher.FILTRI_DATI_SPEC_ASS_REG_TIPO_UNI_DOC);
        }

        /* CANCELLAZIONE ATTRIBUTI DATI SPECIFICI DEL TIPO SERIE */
        if ((getSession().getAttribute("salvataggioAttributesDeleteDatiSpecificiTipoSerie") != null)) {
            Object[] attributi = (Object[]) getSession()
                    .getAttribute("salvataggioAttributesDeleteDatiSpecificiTipoSerie");
            DecFiltroSelUdAttbRowBean filtro = (DecFiltroSelUdAttbRowBean) attributi[0];
            BigDecimal idTipoSerie = (BigDecimal) attributi[1];
            try {
                eseguiCancellazioneDatiSpecificiTipoSerie(filtro, idTipoSerie);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
                getMessageBox().addError(e.getMessage());
            }
            getSession().removeAttribute("salvataggioAttributesDeleteDatiSpecificiTipoSerie");
            forwardToPublisher(Application.Publisher.REG_TIPO_UNITA_DOC_SERIE_DETAIL);
        }

        /* SALVATAGGIO REGOLE DI INDIVIDUAZIONE DEL TIPO SERIE */
        if ((getSession().getAttribute("salvataggioAttributesRegoleAcquisizioneTipoSerie") != null)) {
            Object[] attributi = (Object[]) getSession()
                    .getAttribute("salvataggioAttributesRegoleAcquisizioneTipoSerie");
            DecTipoSerieRowBean decTipoSerieRowBean = (DecTipoSerieRowBean) attributi[0];
            Map<String, Map<String, String>> listaAttributiSelezionati = (Map<String, Map<String, String>>) attributi[1];
            BigDecimal idTipoSerie = (BigDecimal) attributi[2];
            Status status = (Status) attributi[3];
            try {
                eseguiModificaRegoleAcquisizioneTipoSerie(decTipoSerieRowBean, listaAttributiSelezionati, idTipoSerie,
                        status);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
                getMessageBox().addError(e.getMessage());
            }
            getSession().removeAttribute("salvataggioAttributesRegoleAcquisizioneTipoSerie");
            forwardToPublisher(Application.Publisher.REGOLA_ACQUISIZIONE_DETAIL);
        }

        /* CANCELLAZIONE REGOLE DI INDIVIDUAZIONE DEL TIPO SERIE */
        if ((getSession().getAttribute("salvataggioAttributesDeleteRegoleAcquisizioneTipoSerie") != null)) {
            Object[] attributi = (Object[]) getSession()
                    .getAttribute("salvataggioAttributesDeleteRegoleAcquisizioneTipoSerie");
            BigDecimal idRegolaAcquisizioneFile = (BigDecimal) attributi[0];
            BigDecimal idTipoSerie = (BigDecimal) attributi[1];
            boolean isFromDetailOrIsEmpty = (boolean) attributi[2];
            try {
                eseguiCancellazioneRegoleAcquisizioneTipoSerie(idRegolaAcquisizioneFile, idTipoSerie,
                        isFromDetailOrIsEmpty);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
                getMessageBox().addError(e.getMessage());
            }
            getSession().removeAttribute("salvataggioAttributesDeleteRegoleAcquisizioneTipoSerie");
        }
        postLoad();
    }

    @Override
    public void annullaSalvataggioTipoSerie() throws Throwable {
        // Nascondo i bottoni con javascript disattivato
        getForm().getTipoSerieCustomMessageButtonList().setViewMode();
        getSession().removeAttribute("salvataggioAttributesTipoSerie");
        getSession().removeAttribute("salvataggioAttributesRegistroTipoSerie");
        getSession().removeAttribute("salvataggioAttributesDeleteRegistroTipoSerie");
        getSession().removeAttribute("salvataggioAttributesRegoleFiltraggioTipoSerie");
        getSession().removeAttribute("salvataggioAttributesDeleteRegoleFiltraggioTipoSerie");
        getSession().removeAttribute("mappaRegoleFiltraggio");
        getSession().removeAttribute("salvataggioAttributesRegoleRappresentazioneTipoSerie");
        getSession().removeAttribute("salvataggioAttributesDeleteRegoleRappresentazioneTipoSerie");
        getSession().removeAttribute("salvataggioAttributesRegoleAcquisizioneTipoSerie");
        getSession().removeAttribute("salvataggioAttributesDeleteRegoleAcquisizioneTipoSerie");
        goBack();
    }

    public void eseguiModificaTipoSerie(LogParam param, DecTipoSerieRowBean rowBean, BigDecimal idTipoSerie)
            throws ParerUserError, EMFError {
        boolean campiModificati = checkModificheCampiTipoSerieDetail(idTipoSerie);
        tipoSerieEjb.updateDecTipoSerie(param, idTipoSerie, rowBean, campiModificati);
        rowBean.setIdTipoSerie(idTipoSerie);
        getMessageBox().addInfo("Tipologia di serie aggiornata con successo");
    }

    public void eseguiModificaRegistroTipoUdTipoSerie(String idRegistro, Set<String> idTipoUnitaDocPerRegistro,
            BigDecimal idTipoSerie, String flSelUnitaDocAnnul, BigDecimal idTipoSerieUdDaMod, Status status)
            throws EMFError, ParerUserError {
        long idTipoSeriePostOperazione = 0L;
        if (BaseElements.Status.insert.equals(status)) {
            idTipoSerieUdDaMod = null;
        }
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                        CostantiDB.TipoAplVGetValAppart.APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        if (BaseElements.Status.insert.equals(status)) {
            param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
        } else {
            param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
        }
        idTipoSeriePostOperazione = tipoSerieEjb.insertRegistroTipoUnitaDocTipoSerie(param, idRegistro,
                idTipoUnitaDocPerRegistro, idTipoSerie, flSelUnitaDocAnnul, idTipoSerieUdDaMod);
        if (BaseElements.Status.insert.equals(status)) {
            DecTipoSerieUdTableBean tableBean = new DecTipoSerieUdTableBean();
            DecTipoSerieUdRowBean rowBean = tipoSerieEjb
                    .getDecTipoSerieUdById(new BigDecimal(idTipoSeriePostOperazione));
            tableBean.add(rowBean);
            tableBean.setCurrentRowIndex(0);
            tableBean.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getTipoSerieRegistriList().setTable(tableBean);
            getMessageBox().addInfo("Associazione registro - tipo unit\u00e0 documentaria inserita con successo.");
        } else if (BaseElements.Status.update.equals(status)) {
            getMessageBox().addInfo("Associazione registro - tipo unit\u00e0 documentaria modificata con successo.");
        }
        visualizzaTipoSerieRegistro();
        // Rimetto in ogni caso in VIEWMODE, lo devo fare qua perchè ci sono condizioni
        // nella jsp a seconda che sia in view o edit mode di visualizzazione liste
        getForm().getRegistroDetail().setViewMode();
        getForm().getRegistroDetail().setStatus(BaseElements.Status.view);
        getForm().getTipoSerieRegistriList().setStatus(BaseElements.Status.view);
    }

    public void eseguiCancellazioneRegistroTipoUdTipoSerie(DecTipoSerieUdRowBean decTipoSerieUd, BigDecimal idTipoSerie,
            boolean isFromDetailOrIsEmpty) throws ParerUserError {
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                        CostantiDB.TipoAplVGetValAppart.APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.REG_TIPO_UNITA_DOC_SERIE_DETAIL)) {
            param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
        } else {
            param.setNomeAzione(
                    SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getTipoSerieRegistriList()));
        }
        tipoSerieEjb.deleteDecTipoSerieUd(param, decTipoSerieUd, idTipoSerie);
        getMessageBox().addMessage(new Message(Message.MessageLevel.INF,
                "Associazione Registro  - Tipologia unit\u00E0 documentaria eliminata con successo"));
        if (isFromDetailOrIsEmpty) {
            goBack();
        } else {
            DecTipoSerieRowBean tipoSerie = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable()
                    .getCurrentRow());
            caricaListeSerie(tipoSerie.getIdTipoSerie());
        }
    }

    /**
     * "Nuovo" metodo di inserimento/modifica regole di filtraggio basato sull'utilizzo di una mappa
     *
     * @param decFiltroSelUdRowBean
     *            il rowBean "particolare" della regola di filtraggio contenente gli unici elementi univoci vale a dire
     *            il tipoFiltro e l'idTipoSerieUD (associazione registro/tipoUd)
     * @param mappa
     *            contiene i dati del filtri che sto per inserire o cancellare K = idTipoDoc V = (K = nmTipoDoc, V =
     *            idFiltroSelUd).
     * @param idTipoSerie
     *            id tipo serie
     * @param status
     *            insert od update
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public void eseguiModificaRegoleFiltraggioTipoSerie2(DecFiltroSelUdRowBean decFiltroSelUdRowBean,
            BigDecimal idTipoSerie, Status status, Map<BigDecimal, Map<String, BigDecimal>> mappa)
            throws ParerUserError {
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                        CostantiDB.TipoAplVGetValAppart.APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        if (BaseElements.Status.insert.equals(status)) {
            param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
            tipoSerieEjb.insertRegolaDiFiltraggio2(param, decFiltroSelUdRowBean, mappa);
            DecFiltroSelUdTableBean table = new DecFiltroSelUdTableBean();
            table.add(decFiltroSelUdRowBean);
            table.setCurrentRowIndex(0);
            table.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getRegistroRegoleFiltraggioList().setTable(table);
            getMessageBox().addInfo("Nuove regole di filtraggio inserite con successo");
        } else if (BaseElements.Status.update.equals(status)) {
            param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
            DecOutSelUdTableBean decOutSelUdTableBean = (DecOutSelUdTableBean) getForm()
                    .getRegoleDiRappresentazioneList().getTable();
            tipoSerieEjb.updateRegolaDiFiltraggio(param, decFiltroSelUdRowBean, decOutSelUdTableBean, idTipoSerie,
                    mappa);
        }
        getSession().removeAttribute("mappaRegoleFiltraggio");
        getForm().getRegolaDiFiltraggioDetail().setViewMode();
        getForm().getRegolaDiFiltraggioDetail().setStatus(BaseElements.Status.view);
        getForm().getRegistroRegoleFiltraggioList().setStatus(BaseElements.Status.view);
    }

    /*
     * N.B: VECCHIO METODO NON PIU' UTILIZZATO A PARTIRE DAL 22 GENNAIO 2016. Consigliata rimozione a distanza di tempo
     *
     * @param decFiltroSelUd
     * 
     * @param decOutSelUdTableBean
     * 
     * @param idTipoSerie
     * 
     * @param isFromDetailOrIsEmpty
     */
    // public void eseguiCancellazioneRegoleFiltraggioTipoSerie(DecFiltroSelUdRowBean decFiltroSelUd,
    // DecOutSelUdTableBean decOutSelUdTableBean, BigDecimal idTipoSerie, boolean isFromDetailOrIsEmpty) {
    // try {
    // tipoSerieEjb.deleteDecFiltroSelUd(decFiltroSelUd, decOutSelUdTableBean, idTipoSerie);
    // getMessageBox().addMessage(new Message(Message.MessageLevel.INF, "Regola di filtraggio eliminata con successo"));
    // // Setto a true il flag fl_tipo_serie_upd
    // serieEjb.setFlagContenutoVerSerieDaAggiornare(idTipoSerie);
    // if (isFromDetailOrIsEmpty) {
    // goBack();
    // } else {
    // forwardToPublisher(Application.Publisher.REG_TIPO_UNITA_DOC_SERIE_DETAIL);
    // }
    // DecTipoSerieUdRowBean tipoSerieUd = ((DecTipoSerieUdRowBean)
    // getForm().getTipoSerieRegistriList().getTable().getCurrentRow());
    // caricaListeRegistro(tipoSerieUd.getIdTipoSerieUd());
    // } catch (Exception ex) {
    // getMessageBox().addError(ex.getMessage());
    // }
    // }
    /**
     * "Nuovo" metodo di cancellazione regole di filtraggio basato sull'utilizzo di una mappa
     *
     * @param decFiltroSelUdRowBean
     *            il rowBean "particolare" della regola di filtraggio contenente gli unici elementi univoci vale a dire
     *            il tipoFiltro e l'idTipoSerieUD (associazione registro/tipoUd)
     * @param mappa
     *            contiene i dati del filtri che sto per cancellare. K = idTipoDoc V = (K = nmTipoDoc, V =
     *            idFiltroSelUd)
     * @param decOutSelUdTableBean
     *            table bean delle regole di rappresentazione che verranno "modificate" a loro volta a seguito della
     *            cancellazione delle regole di filtraggio
     * @param idTipoSerie
     *            id tipo serie
     * @param isFromDetailOrIsEmpty
     *            true/false
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public void eseguiCancellazioneRegoleFiltraggioTipoSerie2(DecFiltroSelUdRowBean decFiltroSelUdRowBean,
            DecOutSelUdTableBean decOutSelUdTableBean, BigDecimal idTipoSerie, boolean isFromDetailOrIsEmpty,
            Map<BigDecimal, Map<String, BigDecimal>> mappa) throws ParerUserError {
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                        CostantiDB.TipoAplVGetValAppart.APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.REGOLA_DI_FILTRAGGIO_TIPO_SERIE)) {
            param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
        } else {
            param.setNomeAzione(
                    SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getRegistroRegoleFiltraggioList()));
        }
        // Passa il valore TRUE per forzarlo a fare il log tipoSerie
        tipoSerieEjb.deleteDecFiltroSelUd2(param, decFiltroSelUdRowBean, decOutSelUdTableBean, idTipoSerie, mappa,
                true);
        getMessageBox()
                .addMessage(new Message(Message.MessageLevel.INF, "Regole di filtraggio eliminate con successo"));
        // A seconda di dove provengo, foruordo
        if (isFromDetailOrIsEmpty) {
            goBack();
        } else {
            forwardToPublisher(Application.Publisher.REG_TIPO_UNITA_DOC_SERIE_DETAIL);
        }
        DecTipoSerieUdRowBean tipoSerieUd = ((DecTipoSerieUdRowBean) getForm().getTipoSerieRegistriList().getTable()
                .getCurrentRow());
        caricaListeRegistro(tipoSerieUd.getIdTipoSerieUd());
    }

    public void eseguiCancellazioneRegoleRappresentazioneTipoSerie(DecOutSelUdRowBean decOutSelUdRowBean,
            BigDecimal idTipoSerie, BigDecimal idTipoSerieUd, boolean isFromDetailOrIsEmpty) throws ParerUserError {
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                        CostantiDB.TipoAplVGetValAppart.APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setNomeAzione(
                SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getRegoleDiRappresentazioneList()));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        int deleteDecOutSelUd = tipoSerieEjb.deleteDecOutSelUd(param, decOutSelUdRowBean, idTipoSerie);
        if (deleteDecOutSelUd == 1) {
            getMessageBox().addMessage(
                    new Message(Message.MessageLevel.INF, "Regola di rappresentazione eliminata con successo"));
            if (isFromDetailOrIsEmpty) {
                goBack();
            } else {
                caricaListeRegistro(idTipoSerieUd);
            }
        }
    }

    @Override
    public JSONObject triggerTipoSerieDetailId_tipo_serie_padreOnTrigger() throws EMFError {
        StrutSerieForm.TipoSerieDetail tipoSerieDetail = getForm().getTipoSerieDetail();
        tipoSerieDetail.post(getRequest());
        BigDecimal idTipoSeriePadre = tipoSerieDetail.getId_tipo_serie_padre().parse();
        if (idTipoSeriePadre != null && idTipoSeriePadre.longValue() > 0) {
            DecTipoSerieRowBean rowBean = tipoSerieEjb.getDecTipoSerieRowBean(idTipoSeriePadre);
            if (rowBean != null) {
                tipoSerieDetail.getNi_anni_conserv_padre().setValue(rowBean.getNiAnniConserv().toPlainString());
            }
        } else {
            tipoSerieDetail.getNi_anni_conserv_padre().setValue(null);
        }
        return tipoSerieDetail.asJSON();
    }

    @Override
    public JSONObject triggerRegistroDetailId_registro_unita_docOnTrigger() throws EMFError {
        StrutSerieForm.RegistroDetail registroDetail = getForm().getRegistroDetail();
        registroDetail.post(getRequest());
        BigDecimal idRegistro = registroDetail.getId_registro_unita_doc().parse();
        if (idRegistro != null) {
            DecRegistroUnitaDocRowBean regRow = registroEjb.getDecRegistroUnitaDocRowBean(idRegistro, null);
            BigDecimal niAnniConserv = regRow.getNiAnniConserv();
            if (niAnniConserv != null) {
                String ni_anni_conserv_reg = regRow.getNiAnniConserv().intValue() == 9999 ? "Illimitata"
                        : regRow.getNiAnniConserv().toPlainString();
                registroDetail.getNi_anni_conserv().setValue(ni_anni_conserv_reg);
            }
            DecVLisTiUniDocAmsRowBean tipoUnitaDocAmmessoRowBean = new DecVLisTiUniDocAmsRowBean();
            tipoUnitaDocAmmessoRowBean.setIdRegistroUnitaDoc(idRegistro);
            DecVLisTiUniDocAmsTableBean tipoUnitaDocAmmessoTableBean = tipoUnitaDocEjb
                    .getDecVLisTiUniDocAmsTableBean(tipoUnitaDocAmmessoRowBean);
            if (tipoUnitaDocAmmessoTableBean != null) {
                DecodeMap mappaRegistro = new DecodeMap();
                mappaRegistro.populatedMap(tipoUnitaDocAmmessoTableBean, "id_tipo_unita_doc", "nm_tipo_unita_doc");
                registroDetail.getId_tipo_unita_doc().setDecodeMap(mappaRegistro);
            }
        }
        return registroDetail.asJSON();
    }

    public JSONObject triggerRegistroDetailId_tipo_unita_docOnTrigger() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    private void caricaComboRegistroDetail() throws EMFError {
        // StrutSerieForm.StrutRif strutRif = getForm().getStrutRif();
        StrutSerieForm.RegistroDetail registroDetail = getForm().getRegistroDetail();
        DecodeMapIF mappaGenericFlagSiNo = ComboGetter.getMappaGenericFlagSiNo();
        registroDetail.getFl_sel_unita_doc_annul().setDecodeMap(mappaGenericFlagSiNo);
        DecRegistroUnitaDocTableBean decRegistroUnitaDocTableBeanForSerieByIdStrut = tipoSerieEjb
                .getDecRegistroUnitaDocTableBeanForSerieByIdStrut(getForm().getTipoSerieDetail().getId_strut().parse());
        DecodeMap mappaRegistroUnitaDoc = new DecodeMap();
        mappaRegistroUnitaDoc.populatedMap(decRegistroUnitaDocTableBeanForSerieByIdStrut, "id_registro_unita_doc",
                "cd_registro_unita_doc");
        registroDetail.getId_registro_unita_doc().setDecodeMap(mappaRegistroUnitaDoc);
    }

    private boolean islistaEqual(String lista, it.eng.spagoLite.form.list.List<SingleValueField<?>> tableList) {
        return lista.equals(tableList.getName()) && (tableList.getTable() != null) && (tableList.getTable().size() > 0);
    }

    private void visualizzaTipoSerieRegistro() throws EMFError {
        visualizzaTipoSerieRegistro(null);
    }

    private void visualizzaTipoSerieRegistro(BaseElements.Status mode) throws EMFError {
        if (mode == null) {
            mode = BaseElements.Status.view;
        }
        StrutSerieForm.RegistroDetail registroDetail = getForm().getRegistroDetail();
        if (mode == BaseElements.Status.view) {
            registroDetail.setViewMode();
        }
        registroDetail.setStatus(mode);
        StrutSerieForm.TipoSerieRegistriList tipoSerieRegistriList = getForm().getTipoSerieRegistriList();
        tipoSerieRegistriList.setStatus(mode);
        caricaComboRegistroDetail();
        // Recupero l'idStrut
        BigDecimal idStrut = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable().getCurrentRow())
                .getIdStrut();
        OrgStrutRowBean struttura = struttureEjb.getOrgStrutRowBean(idStrut);
        if ("1".equals(struttura.getFlCessato())) {
            getRequest().setAttribute("cessato", true);
        }
        BigDecimal idTipoSerie = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable().getCurrentRow())
                .getIdTipoSerie();
        // Ricavo il record relativo all'assaciazione "Registro-TipoUd" (DecTipoSerieUd)
        DecTipoSerieUdRowBean tipoSerieUdRowBean = ((DecTipoSerieUdRowBean) tipoSerieRegistriList.getTable()
                .getCurrentRow());
        DecTipoUnitaDocRowBean tipounitaDocRowBean = tipoUnitaDocEjb
                .getDecTipoUnitaDocRowBean(tipoSerieUdRowBean.getIdTipoUnitaDoc(), null);
        DecTipoSerieRowBean tipoSerieRowBean = tipoSerieEjb.getDecTipoSerieRowBean(idTipoSerie);
        DecRegistroUnitaDocRowBean regRowBean = registroEjb
                .getDecRegistroUnitaDocRowBean(tipoSerieUdRowBean.getIdRegistroUnitaDoc(), null);

        getForm().getStrutRif().getNm_tipo_serie().setValue(tipoSerieRowBean.getNmTipoSerie());
        getForm().getStrutRif().getNm_tipo_serie_padre().setValue(tipoSerieRowBean.getString("Nm_serie_padre"));
        registroDetail.copyFromBean(tipoSerieUdRowBean);
        // Setto i campi relativi ad ambiente/ente/struttura
        initInputAmbienteEnteStruttura(registroDetail);

        BigDecimal niAnniConserv = regRowBean.getNiAnniConserv();
        if (niAnniConserv != null) {
            registroDetail.getNi_anni_conserv()
                    .setValue(niAnniConserv.longValue() == 9999 ? "Illimitata" : niAnniConserv.toPlainString());
        }

        String tipo_unita_doc_vis = tipounitaDocRowBean.getNmTipoUnitaDoc();
        registroDetail.getTipo_unita_doc_vis().setValue(tipo_unita_doc_vis);

        /* Setto la relativa decodeMap */
        DecodeMap mappaTipoUnitaDoc = new DecodeMap();
        DecTipoUnitaDocTableBean tipoUnitaDocTableBean = new DecTipoUnitaDocTableBean();
        tipoUnitaDocTableBean.add(tipounitaDocRowBean);
        mappaTipoUnitaDoc.populatedMap(tipoUnitaDocTableBean, "id_tipo_unita_doc", "nm_tipo_unita_doc");
        registroDetail.getId_tipo_unita_doc().setDecodeMap(mappaTipoUnitaDoc);
        registroDetail.getId_tipo_unita_doc().setValue(tipounitaDocRowBean.getIdTipoUnitaDoc().toPlainString());
        // Carica liste presenti nella pagina di Dettaglio Associazione registro - tipologia di Unità documentarie
        caricaListeRegistro(tipoSerieUdRowBean.getIdTipoSerieUd());
        ricaricaListaFiltriDatiSpecificiPerAssociazioneRegistroTipoUnitaDoc();
    }

    private void salvaRegistroTipoSerie() {
        MessageBox msgBox = getMessageBox();
        msgBox.clear();

        StrutSerieForm.RegistroDetail registroDetail = getForm().getRegistroDetail();
        BaseElements.Status status = getForm().getTipoSerieRegistriList().getStatus();
        try {
            registroDetail.post(getRequest());
            if (registroDetail.validate(msgBox)) {
                BigDecimal idTipoSerie = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable()
                        .getCurrentRow()).getIdTipoSerie();

                Set<String> idTipoUnitaDocPerRegistro = new java.util.HashSet<>();
                idTipoUnitaDocPerRegistro.add(registroDetail.getId_tipo_unita_doc().getValue());
                String idRegistro = registroDetail.getId_registro_unita_doc().getValue();
                String flSelUnitaDocAnnul = registroDetail.getFl_sel_unita_doc_annul().parse();
                if (BaseElements.Status.insert.equals(status)) {
                    checkObbligatoriRegistro(idRegistro, idTipoUnitaDocPerRegistro, idTipoSerie, msgBox);
                }
                if (msgBox.isEmpty()) {
                    if (serieEjb.checkSerieModificabili(idTipoSerie)) {
                        Object[] attributiRegistroTipoSerie = new Object[5];
                        attributiRegistroTipoSerie[0] = idRegistro;
                        attributiRegistroTipoSerie[1] = idTipoUnitaDocPerRegistro;
                        attributiRegistroTipoSerie[2] = idTipoSerie;
                        attributiRegistroTipoSerie[3] = flSelUnitaDocAnnul;
                        if (BaseElements.Status.insert.equals(status)) {
                            attributiRegistroTipoSerie[4] = null;
                        } else if (BaseElements.Status.update.equals(status)) {
                            attributiRegistroTipoSerie[4] = ((DecTipoSerieUdRowBean) getForm()
                                    .getTipoSerieRegistriList().getTable().getCurrentRow()).getIdTipoSerieUd();
                        }
                        getSession().setAttribute("salvataggioAttributesRegistroTipoSerie", attributiRegistroTipoSerie);
                        getRequest().setAttribute("customBox", true);
                    } else {
                        BigDecimal idTipoSerieUd = null;
                        if (BaseElements.Status.update.equals(status)) {
                            idTipoSerieUd = ((DecTipoSerieUdRowBean) getForm().getTipoSerieRegistriList().getTable()
                                    .getCurrentRow()).getIdTipoSerieUd();
                        }
                        eseguiModificaRegistroTipoUdTipoSerie(idRegistro, idTipoUnitaDocPerRegistro, idTipoSerie,
                                flSelUnitaDocAnnul, idTipoSerieUd, status);
                    }
                }
            }
            forwardToPublisher(Application.Publisher.REG_TIPO_UNITA_DOC_SERIE_DETAIL);
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
            msgBox.addError(ex.getMessage());
        }
    }

    private void checkObbligatoriRegistro(String idRegistro, Set<String> idTipoUnitaDocPerRegistro,
            BigDecimal idTipoSerie, MessageBox msgBox) throws EMFError {

        checkAnniConservPerRegistroSerie(idRegistro, idTipoSerie, msgBox);
        for (String idTipoUnitaDoc : idTipoUnitaDocPerRegistro) {
            String registroDescr = getForm().getRegistroDetail().getId_registro_unita_doc().getDecodedValue();
            boolean isRegistroTipoUnitaDocTouplePresent = checkExistRegistroTipoUnitaDocTouple(idRegistro,
                    idTipoUnitaDoc);
            if (isRegistroTipoUnitaDocTouplePresent) {
                DecodeMapIF mappaTipiUnitaDoc = getForm().getRegistroDetail().getId_tipo_unita_doc().getDecodeMap();
                String descTipoUnitaDoc = mappaTipiUnitaDoc.getDescrizione(idTipoUnitaDoc);

                StringBuilder errorMsg = new StringBuilder("Associazione registro ");
                errorMsg.append(registroDescr);
                errorMsg.append(" - ");
                errorMsg.append(descTipoUnitaDoc);
                errorMsg.append(" gi\u00E0 presente per la tipologia di serie");
                msgBox.addError(errorMsg.toString());
            }
        }
    }

    private void checkAnniConservPerRegistroSerie(String idRegistro, BigDecimal idTipoSerie, MessageBox msgBox) {

        BigDecimal idRegistroNum = new BigDecimal(idRegistro);
        DecRegistroUnitaDocRowBean decRegistroUnitaDocRowBean = registroEjb.getDecRegistroUnitaDocRowBean(idRegistroNum,
                null);
        BigDecimal niAnniConservRegistro = decRegistroUnitaDocRowBean.getNiAnniConserv();
        DecTipoSerieRowBean decTipoSerieRowBean = tipoSerieEjb.getDecTipoSerieRowBean(idTipoSerie);
        BigDecimal niAnniConservTipoSerie = decTipoSerieRowBean.getNiAnniConserv();
        if ((niAnniConservRegistro == null || niAnniConservRegistro.intValue() == 0)
                && ((niAnniConservTipoSerie == null || niAnniConservTipoSerie.intValue() == 0))) {
            msgBox.addError(
                    "E' necessario definire gli anni di conservazione sulla tipologia di serie o sul registro di creazione della serie");
        }

    }

    private boolean checkExistRegistroTipoUnitaDocTouple(String idRegistro, String idTipoUnitaDoc) {
        BigDecimal idTipoSerie = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable().getCurrentRow())
                .getIdTipoSerie();
        BigDecimal idRegistroNum = new BigDecimal(idRegistro);
        BigDecimal idTipoUnitaDocNum = new BigDecimal(idTipoUnitaDoc);
        DecTipoSerieUdRowBean decTipoSerieUdRowBean = tipoSerieEjb.getDecTipoSerieUdRowBean(idTipoSerie, idRegistroNum,
                idTipoUnitaDocNum);
        return (decTipoSerieUdRowBean != null);
    }

    @Override
    public void tabDefinizioneOnClick() throws EMFError {
        String lastPage = (String) getSession().getAttribute("lastPage");
        StrutSerieForm.RegistroRegoleFiltraggioList registroRegoleFiltraggioList = getForm()
                .getRegistroRegoleFiltraggioList();
        // Salva i filtri che l'utente ha compilato
        salvaFiltriDatiSpecCompilati();

        // Controlla che i filtri dati specifici siano stati compilati correttamente
        checkFiltriSettatiSuDatiSpecifici();

        if (!getMessageBox().hasError()) {
            if (lastPage != null && lastPage.equals("insertCriterio")) {
                getForm().getRegolaDiFiltraggioDetail().setStatus(BaseElements.Status.insert);
                getSession().removeAttribute("lastPage");
            }
            BaseElements.Status status = getForm().getRegolaDiFiltraggioDetail().getStatus();
            if (BaseElements.Status.update.equals(status) || BaseElements.Status.insert.equals(status)) {
                registroRegoleFiltraggioList.setHideUpdateButton(false);
                registroRegoleFiltraggioList.setUserOperations(true, true, true, true);
            }
            getForm().getRegolaDiFiltraggioTabs().setCurrentTab(getForm().getRegolaDiFiltraggioTabs().getDefinizione());
        }
        forwardToPublisher(Application.Publisher.REGOLA_DI_FILTRAGGIO_TIPO_SERIE);
    }

    @Override
    public void tabFiltriDatiSpecOnClick() throws EMFError {
        StrutSerieForm.RegolaDiFiltraggioDetail regolaDiFiltraggioDetail = getForm().getRegolaDiFiltraggioDetail();
        BaseElements.Status status = regolaDiFiltraggioDetail.getStatus();
        StrutSerieForm.RegistroRegoleFiltraggioList registroRegoleFiltraggioList = getForm()
                .getRegistroRegoleFiltraggioList();
        if (status.equals(BaseElements.Status.update) || status.equals(BaseElements.Status.insert)) {
            regolaDiFiltraggioDetail.post(getRequest());
            registroRegoleFiltraggioList.setHideUpdateButton(true);
            registroRegoleFiltraggioList.setUserOperations(false, false, false, false);
        }

        regolaDiFiltraggioDetail.post(getRequest());
        getForm().getRegolaDiFiltraggioTabs().setCurrentTab(getForm().getRegolaDiFiltraggioTabs().getFiltriDatiSpec());
        forwardToPublisher(Application.Publisher.REGOLA_DI_FILTRAGGIO_TIPO_SERIE);
    }

    // VECCHIA VERSIONE DEL TRIGGER QUANDO IL CAMPO ERA COMBOBOX E NON MULTISELECT.
    // Non più utilizzato dal 22 gennaio 2016 e dunque consiglio cancellazione dopo ragionevole periodo di tempo
    // @Override
    // public JSONObject triggerRegolaDiFiltraggioDetailId_tipo_doc_princOnTrigger() throws EMFError {
    // StrutSerieForm.RegolaDiFiltraggioDetail regolaDiFiltraggioDetail = getForm().getRegolaDiFiltraggioDetail();
    // BigDecimal idTipoDocPre = regolaDiFiltraggioDetail.getId_tipo_doc_princ().parse();
    // String nmTipoDocPre = regolaDiFiltraggioDetail.getId_tipo_doc_princ().getDecodedValue();
    // if (idTipoDocPre != null) {
    // DecTipoDocRowBean decTipoDocRowBean = struttureEjb.getDecTipoDocRowBean(idTipoDocPre, null);
    // nmTipoDocPre = decTipoDocRowBean.getNmTipoDoc();
    //
    // }
    // regolaDiFiltraggioDetail.post(getRequest());
    // BigDecimal idTipoDocSelected = regolaDiFiltraggioDetail.getId_tipo_doc_princ().parse();
    // // Ricavo la Lista Dati Specifici compilati a video
    // List<DecFiltroSelUdAttbBean> listaDatiSpecOnLine = (ArrayList) getSession().getAttribute("listaDatiSpecOnLine")
    // != null ? (ArrayList) getSession().getAttribute("listaDatiSpecOnLine") : new ArrayList<>();
    //
    // // Per ogni DATO SPECIFICO di questo TIPO DOCUMENTO
    // // rimuovo il riferimento al tipo documento
    // for (DecFiltroSelUdAttbBean datoSpec : listaDatiSpecOnLine) {
    // List<DecFiltroSelUdDatoBean> tabellaDefinitoDa = datoSpec.getDecFiltroSelUdDatos();
    // for (int i = 0; i < tabellaDefinitoDa.size(); i++) {
    // String nmTipoDoc = tabellaDefinitoDa.get(i).getNmTipoDoc();
    // if (nmTipoDoc != null
    // && nmTipoDoc.equals(nmTipoDocPre)) {
    // tabellaDefinitoDa.remove(i);
    // }
    // }
    // }
    //
    // DecAttribDatiSpecTableBean datiSpecTB = udHelper.getDecAttribDatiSpecTableBean(idTipoDocSelected,
    // Constants.TipoEntitaSacer.DOC);
    // aggiungiDatiSpecPerTipoDoc(datiSpecTB, listaDatiSpecOnLine);
    //
    //// // Controllo se ho ancora dati specifici per tutti i tipi documento
    //// boolean hasDSsuTipiDoc = false;
    //// Iterator it = listaDatiSpecOnLine.iterator();
    //// while (it.hasNext()) {
    //// DecFiltroSelUdAttbBean datoSpec = (DecFiltroSelUdAttbBean) it.next();
    //// List<DecFiltroSelUdDatoBean> tabellaDefinitoDa = datoSpec.getDecFiltroSelUdDatos();
    //// if (tabellaDefinitoDa.isEmpty()) {
    //// it.remove();
    //// } else {
    //// for (DecFiltroSelUdDatoBean rigaDefinitoDa : tabellaDefinitoDa) {
    //// if (rigaDefinitoDa.getNmTipoDoc() != null) {
    //// hasDSsuTipiDoc = true;
    //// }
    //// }
    //// }
    //// }
    //// hasDSsuTipiDoc = tipoSerieEjb.getVersioniXsdPerTipoEntita(idTipoDocSelected, Constants.TipoEntitaSacer.DOC);
    ////
    //// regolaDiFiltraggioDetail.getFlag_dati_spec_presenti_doc().setChecked(hasDSsuTipiDoc);
    // // Aggiorno l'interfaccia online
    // updateInterfacciaOnLineDatiSpec(listaDatiSpecOnLine, false);
    //
    // return regolaDiFiltraggioDetail.asJSON();
    //
    // }
    /**
     * "Nuova" versione del trigger sulla multiselect relativa ai tipi doc principale delle regole di filtraggio.
     *
     * @return oggetto json {@link JSONObject}
     * 
     * @throws EMFError
     *             errore generico
     */
    @Override
    public JSONObject triggerRegolaDiFiltraggioDetailId_tipo_doc_princOnTrigger() throws EMFError {
        // Ricavo la mappa con le regole già settate
        Map<BigDecimal, Map<String, BigDecimal>> mappa = new HashMap<>();
        if ((Map<BigDecimal, Map<String, BigDecimal>>) getSession().getAttribute("mappaRegoleFiltraggio") != null) {
            mappa = (Map<BigDecimal, Map<String, BigDecimal>>) getSession().getAttribute("mappaRegoleFiltraggio");
        }

        // Valori selezionati prima
        List<BigDecimal> idTipoDocPreList = getForm().getRegolaDiFiltraggioDetail().getId_tipo_doc_princ().parse();
        // Post
        getForm().getRegolaDiFiltraggioDetail().post(getRequest());
        // Valori selezionati dopo post
        List<BigDecimal> idTipoDocSelectedList = getForm().getRegolaDiFiltraggioDetail().getId_tipo_doc_princ().parse();

        // Ricavo ciò che ho aggiunto...
        BigDecimal elementoAggiunto = null;
        for (BigDecimal idTipoDocSelected : idTipoDocSelectedList) {
            if (!idTipoDocPreList.contains(idTipoDocSelected)) {
                elementoAggiunto = idTipoDocSelected;
                // e lo aggiungo alla mappa
                String nmTipoDoc = ((DecTipoDocRowBean) tipoDocEjb.getDecTipoDocRowBean(elementoAggiunto, null))
                        .getNmTipoDoc();
                Map<String, BigDecimal> mappaInterna = new HashMap<>();
                mappaInterna.put(nmTipoDoc, null);
                mappa.put(elementoAggiunto, mappaInterna);
                break;
            }
        }

        // ... o ciò che ho tolto!
        BigDecimal elementoRimosso = null;
        String elementoRimossoString = null;
        for (BigDecimal idTipoDocPre : idTipoDocPreList) {
            if (!idTipoDocSelectedList.contains(idTipoDocPre)) {
                elementoRimosso = idTipoDocPre;
                // e lo tolgo dalla mappa
                elementoRimossoString = ((Map<String, BigDecimal>) mappa.get(elementoRimosso)).keySet().iterator()
                        .next();
                mappa.remove(elementoRimosso);
                break;
            }
        }

        // Ricavo la Lista Dati Specifici compilati a video
        List<DecFiltroSelUdAttbBean> listaDatiSpecOnLine = (ArrayList) getSession()
                .getAttribute("listaDatiSpecOnLine") != null
                        ? (ArrayList) getSession().getAttribute("listaDatiSpecOnLine") : new ArrayList<>();

        /*
         * Per ogni DATO SPECIFICO dei tipi documento rimossi rimuovo il riferimento al tipo documento
         */
        for (DecFiltroSelUdAttbBean datoSpec : listaDatiSpecOnLine) {
            List<DecFiltroSelUdDatoBean> tabellaDefinitoDa = datoSpec.getDecFiltroSelUdDatos();
            for (int i = 0; i < tabellaDefinitoDa.size(); i++) {
                String nmTipoDoc = tabellaDefinitoDa.get(i).getNmTipoDoc();
                if (nmTipoDoc != null && nmTipoDoc.equals(elementoRimossoString)) {
                    tabellaDefinitoDa.remove(i);
                }
            }
        }

        // Se ho aggiunto un tipo doc, aggiorno i dati specifici
        if (elementoAggiunto != null) {
            DecAttribDatiSpecTableBean datiSpecTB = udHelper.getDecAttribDatiSpecTableBean(elementoAggiunto,
                    Constants.TipoEntitaSacer.DOC);
            aggiungiDatiSpecPerTipoDoc(datiSpecTB, listaDatiSpecOnLine);
        }

        // // Controllo se ho ancora dati specifici per tutti i tipi documento
        // boolean hasDSsuTipiDoc = false;
        // Iterator it = listaDatiSpecOnLine.iterator();
        // while (it.hasNext()) {
        // DecFiltroSelUdAttbBean datoSpec = (DecFiltroSelUdAttbBean) it.next();
        // List<DecFiltroSelUdDatoBean> tabellaDefinitoDa = datoSpec.getDecFiltroSelUdDatos();
        // if (tabellaDefinitoDa.isEmpty()) {
        // it.remove();
        // } else {
        // for (DecFiltroSelUdDatoBean rigaDefinitoDa : tabellaDefinitoDa) {
        // if (rigaDefinitoDa.getNmTipoDoc() != null) {
        // hasDSsuTipiDoc = true;
        // }
        // }
        // }
        // }
        // hasDSsuTipiDoc = tipoSerieEjb.getVersioniXsdPerTipoEntita(idTipoDocSelected, Constants.TipoEntitaSacer.DOC);
        //
        // regolaDiFiltraggioDetail.getFlag_dati_spec_presenti_doc().setChecked(hasDSsuTipiDoc);
        // Aggiorno l'interfaccia online
        updateInterfacciaOnLineDatiSpec(listaDatiSpecOnLine, false);

        // "Risetto" i valori nella mappa
        getSession().setAttribute("mappaRegoleFiltraggio", mappa);
        return getForm().getRegolaDiFiltraggioDetail().asJSON();

    }

    private void salvaFiltriDatiSpecCompilati() {

        // Ricavo la struttura dati contenente la Lista Dati Specifici compilati a video
        java.util.List<DecFiltroSelUdAttbBean> listaDatiSpecOnLine = getSession()
                .getAttribute("listaDatiSpecOnLine") != null
                        ? (ArrayList) getSession().getAttribute("listaDatiSpecOnLine") : new ArrayList<>();

        // Ricavo i filtri compilati nel tab precedente
        if (getForm().getFiltriDatiSpecList().getTable() != null) {
            for (int i = 0; i < getForm().getFiltriDatiSpecList().getTable().size(); i++) {
                BaseRowInterface r = getForm().getFiltriDatiSpecList().getTable().getRow(i);
                if (getRequest().getParameterValues("Ti_oper") != null) {
                    r.setString("ti_oper", getRequest().getParameterValues("Ti_oper")[i]);
                }
                if (getRequest().getParameterValues("Dl_valore") != null) {
                    r.setString("dl_valore", getRequest().getParameterValues("Dl_valore")[i]);
                }
                for (DecFiltroSelUdAttbBean rigaDatoSpec : listaDatiSpecOnLine) {
                    if (rigaDatoSpec.getNmAttribDatiSpec().equals(r.getString("nm_attrib_dati_spec"))) {
                        rigaDatoSpec.setTiOper(r.getString("ti_oper"));
                        rigaDatoSpec.setDlValore(r.getString("dl_valore"));
                        rigaDatoSpec.setIdFiltroSelUdAttb(r.getBigDecimal("id_filtro_sel_ud_attb").longValue());
                    }
                }
            }
        }

        // Risalvo in sessione la struttura dati contenente la Lista Dari Specifici compilati a video
        getSession().setAttribute("listaDatiSpecOnLine", listaDatiSpecOnLine);

    }

    public void checkFiltriSettatiSuDatiSpecifici() {
        // Ricavo la Lista Dati Specifici compilati a video
        StrutSerieForm.RegolaDiFiltraggioDetail regolaDiFiltraggioDetail = getForm().getRegolaDiFiltraggioDetail();
        List<DecFiltroSelUdAttbBean> listaDatiSpecOnLine = (ArrayList) getSession()
                .getAttribute("listaDatiSpecOnLine") != null
                        ? (ArrayList) getSession().getAttribute("listaDatiSpecOnLine") : new ArrayList();

        List<DefinitoDaBean> listaDefinitoDa = new ArrayList();
        Set<String> insiemeTipiUnitaDoc = new HashSet();
        Set<String> insiemeTipiDoc = new HashSet();
        StringBuilder filtriDatiSpec = new StringBuilder();

        // Per ogni dato specifico
        for (DecFiltroSelUdAttbBean datiSpec : listaDatiSpecOnLine) {
            /*
             * Se il filtro \u00E8 compilato, ricavo le informazioni che mi servono: aggiungo un elemento in
             * ListaDefinitoDa e nel relativo insieme
             */
            if (datiSpec.getTiOper() != null && datiSpec.getDlValore() != null) {
                if (!datiSpec.getTiOper().equals("") || !datiSpec.getDlValore().equals("")) {

                    // Ricavo la listaDefinitoDa di quel preciso dato specifico
                    List<DecFiltroSelUdDatoBean> decFiltroSelUdDatoList = datiSpec.getDecFiltroSelUdDatos();

                    /*
                     * Scorro questa lista per andare ad inserire l'elemento nella lista principale, ovvero
                     * ListaDefinitoDa
                     */
                    for (DecFiltroSelUdDatoBean decFiltroSelUdDato : decFiltroSelUdDatoList) {
                        DefinitoDaBean definitoDa = new DefinitoDaBean();
                        definitoDa.setIdAttribDatiSpec(decFiltroSelUdDato.getIdAttribDatiSpec());
                        definitoDa.setTiEntitaSacer(decFiltroSelUdDato.getTiEntitaSacer());
                        definitoDa.setNmTipoDoc(decFiltroSelUdDato.getNmTipoDoc());
                        definitoDa.setNmTipoUnitaDoc(decFiltroSelUdDato.getNmTipoUnitaDoc());
                        definitoDa.setNmAttribDatiSpec(datiSpec.getNmAttribDatiSpec());
                        definitoDa.setTiOper(datiSpec.getTiOper());
                        definitoDa.setDlValore(datiSpec.getDlValore());
                        listaDefinitoDa.add(definitoDa);
                        // Annoto quale elemento sto trattando inserendolo nel relativo insieme
                        // Caso UNI_DOC
                        if (definitoDa.getNmTipoUnitaDoc() != null) {
                            insiemeTipiUnitaDoc.add(definitoDa.getNmTipoUnitaDoc());
                        } // Caso DOC
                        else if (definitoDa.getNmTipoDoc() != null) {
                            insiemeTipiDoc.add(definitoDa.getNmTipoDoc());
                        }
                    }
                }
            }
        }

        // Valido i filtri compilati
        UnitaDocumentarieValidator validator = new UnitaDocumentarieValidator(getMessageBox());
        validator.validaDatiSpec(listaDefinitoDa);

        // Se la validazione non ha portato errori
        if (!getMessageBox().hasError()) {
            // Comincio a costruire la label dei Filtri Dati Specifici
            if (!insiemeTipiUnitaDoc.isEmpty()) {
                boolean firstTimeDefinitoDa = true;
                Iterator<String> it = insiemeTipiUnitaDoc.iterator();

                // Per ogni nm_tipo_unita_doc presente in insiemeTipiUnitaDoc
                while (it.hasNext()) {
                    if (firstTimeDefinitoDa) {
                        filtriDatiSpec.append(" e ((");
                        firstTimeDefinitoDa = false;
                    } else {
                        filtriDatiSpec.append("\n  o (");
                    }
                    boolean firstTimeTipoUD = true;
                    String nmTipoUnitaDoc = it.next();
                    for (DefinitoDaBean definitoDa : listaDefinitoDa) {
                        if (definitoDa.getNmTipoUnitaDoc() != null
                                && definitoDa.getNmTipoUnitaDoc().equals(nmTipoUnitaDoc)) {
                            if (firstTimeTipoUD) {
                                firstTimeTipoUD = false;
                            } else {
                                filtriDatiSpec.append("\n  e ");
                            }
                            filtriDatiSpec.append("tipo unit\u00E0 doc = ").append(nmTipoUnitaDoc);
                            filtriDatiSpec.append(" e ").append(definitoDa.getNmAttribDatiSpec());
                            filtriDatiSpec.append(" ").append(definitoDa.getTiOper());
                            filtriDatiSpec.append(" ").append(definitoDa.getDlValore());
                        } // END IF
                    } // END FOR di ListaDefinitoDa
                    filtriDatiSpec.append(")");
                } // END WHILE sull'insieme dei TipiUnit\u00E0 Doc
                filtriDatiSpec.append(")");
            }

            if (!insiemeTipiDoc.isEmpty()) {
                boolean firstTimeDefinitoDa = true;
                Iterator<String> it = insiemeTipiDoc.iterator();

                // Per ogni nm_tipo_doc presente in insiemeTipiDoc
                while (it.hasNext()) {
                    if (firstTimeDefinitoDa) {
                        /*
                         * Controllo filtriDatiSpec e'!= da stringa vuota, significa che in precedenza ho gia' scritto
                         * qualcosa e dunque vado a capo
                         */
                        if (filtriDatiSpec.length() > 0) {
                            filtriDatiSpec.append("\n");
                        }
                        filtriDatiSpec.append(" e ((");
                        firstTimeDefinitoDa = false;
                    } else {
                        filtriDatiSpec.append("\n  o (");
                    }
                    boolean firstTimeTipoDoc = true;
                    String nmTipoDoc = it.next();
                    for (DefinitoDaBean definitoDa : listaDefinitoDa) {
                        if (definitoDa.getNmTipoDoc() != null && definitoDa.getNmTipoDoc().equals(nmTipoDoc)) {
                            if (firstTimeTipoDoc) {
                                firstTimeTipoDoc = false;
                            } else {
                                filtriDatiSpec.append("\n  e ");
                            }
                            filtriDatiSpec.append("tipo doc = ").append(nmTipoDoc);
                            filtriDatiSpec.append(" e ").append(definitoDa.getNmAttribDatiSpec());
                            filtriDatiSpec.append(" ").append(definitoDa.getTiOper());
                            filtriDatiSpec.append(" ").append(definitoDa.getDlValore());
                        } // END IF
                    } // END FOR di ListaDefinitoDa
                    filtriDatiSpec.append(")");
                } // END WHILE sull'insieme dei TipiDoc
                filtriDatiSpec.append(")");
            }
        }
    }

    private void aggiungiDatiSpecPerTipoDoc(DecAttribDatiSpecTableBean datiSpecTB,
            List<DecFiltroSelUdAttbBean> listaDatiSpecOnLine) throws EMFError {
        // Per ogni DATO SPECIFICO di questo TIPO UNITA' DOCUMENTARIA AGGIUNTO
        for (DecAttribDatiSpecRowBean rigaDatoSpecifico : datiSpecTB) {
            // Ricavo l'informazione "Definito da" per il TIPO DOCUMENTO
            DecFiltroSelUdDatoBean filtroSelUdDato = new DecFiltroSelUdDatoBean();
            String nmTipoDoc = udHelper.getDecTipoDocRowBean(rigaDatoSpecifico.getIdTipoDoc()).getNmTipoDoc();
            String dsVersioni = udHelper.getVersioniXsd(rigaDatoSpecifico.getIdAttribDatiSpec(),
                    rigaDatoSpecifico.getIdTipoDoc(), Constants.TipoEntitaSacer.DOC);
            filtroSelUdDato.setTiEntitaSacer(Constants.TipoEntitaSacer.DOC.name());
            filtroSelUdDato.setNmTipoUnitaDoc(null);
            filtroSelUdDato.setNmTipoDoc(nmTipoDoc);
            filtroSelUdDato.setIdAttribDatiSpec(rigaDatoSpecifico.getIdAttribDatiSpec());
            filtroSelUdDato.setDsListaVersioniXsd(dsVersioni);
            filtroSelUdDato.setOrdine(BigDecimal.ONE);

            // Inserisco le informazioni del dato specifico aggiunto
            insertFiltroDatoSpecifico(rigaDatoSpecifico, listaDatiSpecOnLine, filtroSelUdDato);

        } // end For di controllo di ogni dato specifico

        // Aggiorno l'interfaccia online
        updateInterfacciaOnLineDatiSpec(listaDatiSpecOnLine, true);
    }

    private void insertFiltroDatoSpecifico(DecAttribDatiSpecRowBean rigaDatoSpecifico,
            List<DecFiltroSelUdAttbBean> listaDatiSpecOnLine, DecFiltroSelUdDatoBean filtroSelUdDato) {
        boolean giaPresente = false;
        List<DecFiltroSelUdDatoBean> totaleDefinitoDaList = new ArrayList();
        // Controllo se il dato specifico che sto trattando e' gia' stato inserito
        // nella Lista Dati Specifici presentata a video
        if (!listaDatiSpecOnLine.isEmpty()) {
            for (int j = 0; j < listaDatiSpecOnLine.size(); j++) {
                if (listaDatiSpecOnLine.get(j).getNmAttribDatiSpec().equals(rigaDatoSpecifico.getNmAttribDatiSpec())) {
                    giaPresente = true;
                    // Se il dato specifico e' gia' presente, ricavo la lista dei suoi totaliDefinitoDa
                    // e vi aggiungo ad essa il TIPO UNITA' DOCUMENTARIA
                    totaleDefinitoDaList = listaDatiSpecOnLine.get(j).getDecFiltroSelUdDatos();
                    totaleDefinitoDaList.add(filtroSelUdDato);
                    // FIXME: Controllare l'ordinamento (vedi voce Ordinamenti di liste con "delegate" sulla wiki)
                    Collections.sort(totaleDefinitoDaList);
                    break;
                }
            }
        }

        // Se invece il dato specifico non e' presente, lo inserisco
        // e aggiunto l'informazione su dove e' "Definito da"
        if (!giaPresente) {
            DecFiltroSelUdAttbBean datoSpec = new DecFiltroSelUdAttbBean();
            datoSpec.setNmAttribDatiSpec(rigaDatoSpecifico.getString("nm_attrib_dati_spec"));
            datoSpec.setTiOper(rigaDatoSpecifico.getString("ti_oper"));
            datoSpec.setDlValore(rigaDatoSpecifico.getString("dl_valore"));
            // if (rigaDatoSpecifico.getBigDecimal("id_filtro_sel_ud_attb") != null) {
            // datoSpec.setIdFiltroSelUdAttb(rigaDatoSpecifico.getBigDecimal("id_filtro_sel_ud_attb").longValue());
            // }
            totaleDefinitoDaList.add(filtroSelUdDato);
            datoSpec.setDecFiltroSelUdDatos(totaleDefinitoDaList);
            listaDatiSpecOnLine.add(datoSpec);
        }
    }

    private void updateInterfacciaOnLineDatiSpec(List<DecFiltroSelUdAttbBean> listaDatiSpecOnLine, boolean isAggiunta) {
        // Costruisco l'interfaccia on-line sulla base
        // delle due strutture in memoria
        BaseTableInterface tabellaDatiSpec = listBean2TableBean(listaDatiSpecOnLine);

        // Setto la "nuova" Lista Dati Specifici a video
        getForm().getFiltriDatiSpecList().setTable(tabellaDatiSpec);
        getForm().getFiltriDatiSpecList().getTable().setPageSize(300);
        // Salvo in sessione le informazioni
        getSession().setAttribute("listaDatiSpecOnLine", listaDatiSpecOnLine);

        // Aggiorno la text area Filtri dati specifici
        checkFiltriSettatiSuDatiSpecifici();

        // Ordino per nome dato specifico
        getForm().getFiltriDatiSpecList().getTable()
                .addSortingRule(DecAttribDatiSpecTableDescriptor.COL_NM_ATTRIB_DATI_SPEC, SortingRule.ASC);
        getForm().getFiltriDatiSpecList().getTable().sort();
        getForm().getFiltriDatiSpecList().getTable().first();
        getForm().getFiltriDatiSpecList().getTi_oper().setEditMode();
        getForm().getFiltriDatiSpecList().getDl_valore().setEditMode();
    }

    // Metodo utilizzato per costruire l'interfaccia on-line sulla base
    // della lista di bean in memoria
    private BaseTableInterface listBean2TableBean(List<DecFiltroSelUdAttbBean> listaDatiSpec) {
        BaseTableInterface tabellaDatiSpec = new BaseTable();
        if (listaDatiSpec != null) {
            for (DecFiltroSelUdAttbBean datoSpec : listaDatiSpec) {
                BaseRowInterface rigaDatoSpec = new BaseRow();
                rigaDatoSpec.setString("nm_attrib_dati_spec", datoSpec.getNmAttribDatiSpec());
                rigaDatoSpec.setString("ti_oper", datoSpec.getTiOper());
                rigaDatoSpec.setString("dl_valore", datoSpec.getDlValore());
                rigaDatoSpec.setBigDecimal("id_filtro_sel_ud_attb", new BigDecimal(datoSpec.getIdFiltroSelUdAttb()));

                BaseRowInterface newRow = new BaseRow();
                if (rigaDatoSpec.getObject(Values.SUB_LIST) == null) {
                    rigaDatoSpec.setObject(Values.SUB_LIST, new BaseTable());
                }

                List<DecFiltroSelUdDatoBean> definitoDa = datoSpec.getDecFiltroSelUdDatos();
                for (DecFiltroSelUdDatoBean definitoRow : definitoDa) {
                    String rigaDefinitoDa = "";
                    if (definitoRow.getNmTipoUnitaDoc() != null) {
                        rigaDefinitoDa = "Tipo unit\u00E0 doc.: " + definitoRow.getNmTipoUnitaDoc()
                                + (!definitoRow.getDsListaVersioniXsd().equals("")
                                        ? " (" + definitoRow.getDsListaVersioniXsd() + ")" : "");
                    } else if (definitoRow.getNmTipoDoc() != null) {
                        rigaDefinitoDa = "Tipo doc.: " + definitoRow.getNmTipoDoc()
                                + (!definitoRow.getDsListaVersioniXsd().equals("")
                                        ? " (" + definitoRow.getDsListaVersioniXsd() + ")" : "");
                    }
                    newRow.setString("definito_da_record", rigaDefinitoDa);
                    ((BaseTableInterface) rigaDatoSpec.getObject(Values.SUB_LIST)).add(newRow);
                }
                tabellaDatiSpec.add(rigaDatoSpec);
            }
        } else {

            BaseRowInterface rigaDatoSpec = new BaseRow();
            rigaDatoSpec.setString("nm_attrib_dati_spec", null);
            rigaDatoSpec.setString("ti_oper", null);
            rigaDatoSpec.setString("dl_valore", null);

            BaseRowInterface newRow = new BaseRow();
            if (rigaDatoSpec.getObject(Values.SUB_LIST) == null) {
                rigaDatoSpec.setObject(Values.SUB_LIST, new BaseTable());
            }
            tabellaDatiSpec.add(rigaDatoSpec);

        }
        return tabellaDatiSpec;
    }

    public void aggiungiDatiSpecPerTipoUnitaDoc(DecAttribDatiSpecTableBean datiSpecTB,
            List<DecFiltroSelUdAttbBean> listaDatiSpecOnLine) throws EMFError {
        // Per ogni DATO SPECIFICO di questo TIPO UNITA' DOCUMENTARIA AGGIUNTO
        for (DecAttribDatiSpecRowBean rigaDatoSpecifico : datiSpecTB) {
            // Se passo di qua, significa che ho dei dati specifici
            // per questo Tipo Unita' Documentaria e dunque spunto il flag a video
            getForm().getRegolaDiFiltraggioDetail().getFlag_dati_spec_presenti_sm().setChecked(true);

            // Ricavo l'informazione "Definito da" per il TIPO UNITA' DOCUMENTARIA
            DecFiltroSelUdDatoBean filtroSelUdDato = new DecFiltroSelUdDatoBean();
            String nmTipoUnitaDoc = udHelper.getDecTipoUnitaDocRowBean(rigaDatoSpecifico.getIdTipoUnitaDoc())
                    .getNmTipoUnitaDoc();
            String dsVersioni = udHelper.getVersioniXsd(rigaDatoSpecifico.getIdAttribDatiSpec(),
                    rigaDatoSpecifico.getIdTipoUnitaDoc(), Constants.TipoEntitaSacer.UNI_DOC);
            filtroSelUdDato.setTiEntitaSacer(Constants.TipoEntitaSacer.UNI_DOC.name());
            filtroSelUdDato.setNmTipoUnitaDoc(nmTipoUnitaDoc);
            filtroSelUdDato.setNmTipoDoc(null);
            filtroSelUdDato.setIdAttribDatiSpec(rigaDatoSpecifico.getIdAttribDatiSpec());
            filtroSelUdDato.setDsListaVersioniXsd(dsVersioni);
            filtroSelUdDato.setOrdine(BigDecimal.ZERO);

            // Inserisco le informazioni del dato specifico aggiunto
            insertFiltroDatoSpecifico(rigaDatoSpecifico, listaDatiSpecOnLine, filtroSelUdDato);
        } // end For di controllo di ogni dato specifico

        // Aggiorno l'interfaccia online
        updateInterfacciaOnLineDatiSpec(listaDatiSpecOnLine, true);
    }

    /**
     * "Nuovo" metodo di salvataggio delle regole di filtraggio secondo la nuova gestione (unica riga nella tabella
     * delle regole a video)
     *
     * @throws EMFError
     *             errore generico
     */
    private void salvaRegolaDiFiltraggio() throws EMFError {
        MessageBox msgBox = getMessageBox();
        msgBox.clear();

        // Recupero dalla sessione la mappa associazione nmTipoDoc-idFiltroSelUd
        Map<BigDecimal, Map<String, BigDecimal>> mappa = (Map<BigDecimal, Map<String, BigDecimal>>) getSession()
                .getAttribute("mappaRegoleFiltraggio");

        getForm().getTipoSerieRegistriList();
        BigDecimal idTipoSerieUd = ((DecTipoSerieUdRowBean) getForm().getTipoSerieRegistriList().getTable()
                .getCurrentRow()).getIdTipoSerieUd();
        BigDecimal pgFiltro = BigDecimal.ONE;

        try {
            getForm().getRegolaDiFiltraggioDetail().post(getRequest());
            String ti_Filtro = getForm().getRegolaDiFiltraggioDetail().getTi_filtro().parse();
            CostantiDB.TipoFiltroSerieUd byName = CostantiDB.TipoFiltroSerieUd.byName(ti_Filtro);

            // Controlli sul "Tipo filtro"
            switch (byName) {
            case TIPO_DOC_PRINC:
                List<BigDecimal> idTipoDocPrincList = getForm().getRegolaDiFiltraggioDetail().getId_tipo_doc_princ()
                        .parse();
                if (idTipoDocPrincList.isEmpty()) {
                    msgBox.addError(
                            "Il filtro \u00E8 per tipo di documento principale : \u00E8 necessario selezionare il tipo di documento principale");
                }
                break;
            default:
                msgBox.addError("Selezionare almeno un tipo di filtro <br/>");
                break;
            }

            // Se non ci sono problemi, procedo al salvataggio
            if (msgBox.isEmpty()) {
                DecFiltroSelUdRowBean decFiltroSelUdRowBean = new DecFiltroSelUdRowBean();
                getForm().getRegolaDiFiltraggioDetail().copyToBean(decFiltroSelUdRowBean);
                decFiltroSelUdRowBean.setIdTipoSerieUd(idTipoSerieUd);
                decFiltroSelUdRowBean.setPgFiltro(pgFiltro);
                decFiltroSelUdRowBean.setTiFiltro(ti_Filtro);
                BigDecimal idTipoSerie = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable()
                        .getCurrentRow()).getIdTipoSerie();
                if (serieEjb.checkSerieModificabili(idTipoSerie)) {
                    Object[] attributi = new Object[4];
                    attributi[0] = decFiltroSelUdRowBean;
                    attributi[1] = idTipoSerie;
                    attributi[2] = getForm().getRegolaDiFiltraggioDetail().getStatus();
                    attributi[3] = mappa;
                    getSession().setAttribute("salvataggioAttributesRegoleFiltraggioTipoSerie", attributi);
                    getRequest().setAttribute("customBox", true);
                } else {
                    eseguiModificaRegoleFiltraggioTipoSerie2(decFiltroSelUdRowBean, idTipoSerie,
                            getForm().getRegolaDiFiltraggioDetail().getStatus(), mappa);
                }

            }
            forwardToPublisher(Application.Publisher.REGOLA_DI_FILTRAGGIO_TIPO_SERIE);
        } catch (Exception ex) {
            // getSession().removeAttribute("mappaRegoleFiltraggio");
            logger.error(ex.getLocalizedMessage(), ex);
            msgBox.addError("Errore nel salvataggio della regola di filtraggio", ex);
            forwardToPublisher(Application.Publisher.REGOLA_DI_FILTRAGGIO_TIPO_SERIE);
        }
    }

    @Override
    public void salvaFiltriDatiSpec() {
        try {
            StrutSerieForm.FiltriDatiSpecList listaFiltriDaRequest = recuperaListaFiltriDaRequest();
            BaseTableInterface<?> baseTable = listaFiltriDaRequest.getTable();
            DecFiltroSelUdAttbTableBean table = new DecFiltroSelUdAttbTableBean();
            Iterator<BaseRowInterface> iterator = (Iterator<BaseRowInterface>) baseTable.iterator();
            while (iterator.hasNext()) {
                BaseRowInterface row = iterator.next();
                if (row != null) {
                    String ti_oper = row.getString(StrutSerieForm.FiltriDatiSpecList.ti_oper);
                    if (StringUtils.isNotBlank(ti_oper)) {
                        DecFiltroSelUdAttbRowBean rowBean = new DecFiltroSelUdAttbRowBean();
                        rowBean.setNmAttribDatiSpec(
                                row.getString(StrutSerieForm.FiltriDatiSpecList.nm_attrib_dati_spec));
                        rowBean.setTiOper(row.getString(StrutSerieForm.FiltriDatiSpecList.ti_oper));
                        rowBean.setDlValore(row.getString(StrutSerieForm.FiltriDatiSpecList.dl_valore));
                        rowBean.setIdFiltroSelUdAttb(
                                row.getBigDecimal(StrutSerieForm.FiltriDatiSpecList.id_filtro_sel_ud_attb));
                        table.add(rowBean);
                    }
                }
            }

            List<DecFiltroSelUdAttbBean> listaDatiSpecOnLine = (ArrayList) getSession()
                    .getAttribute("listaDatiSpecOnLine") != null
                            ? (ArrayList) getSession().getAttribute("listaDatiSpecOnLine") : new ArrayList<>();
            BigDecimal idTipoSerieUd = ((DecTipoSerieUdRowBean) getForm().getTipoSerieRegistriList().getTable()
                    .getCurrentRow()).getIdTipoSerieUd();

            // Salva i filtri che l'utente ha compilato
            salvaFiltriDatiSpecCompilati();

            // Controlla che i filtri dati specifici siano stati compilati correttamente
            checkFiltriSettatiSuDatiSpecifici();

            if (!getMessageBox().hasError()) {
                BigDecimal idTipoSerie = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable()
                        .getCurrentRow()).getIdTipoSerie();
                if (serieEjb.checkSerieModificabili(idTipoSerie)) {
                    Object[] attributi = new Object[4];
                    attributi[0] = listaDatiSpecOnLine;
                    attributi[1] = table;
                    attributi[2] = idTipoSerieUd;
                    attributi[3] = idTipoSerie;
                    getSession().setAttribute("salvataggioAttributesDatiSpecificiTipoSerie", attributi);
                    getRequest().setAttribute("customBox", true);
                } else {
                    eseguiModificaDatiSpecificiTipoSerie(listaDatiSpecOnLine, table, idTipoSerieUd, idTipoSerie);
                }
            }
            forwardToPublisher(Application.Publisher.FILTRI_DATI_SPEC_ASS_REG_TIPO_UNI_DOC);
            // } catch (EMFError ex) {
            // logger.error(ex.getLocalizedMessage(), ex);
            // getMessageBox().addMessage(Message.MessageLevel.ERR, ex.getLocalizedMessage());
            // try {
            // ricaricaListaFiltriDatiSpecificiPerAssociazioneRegistroTipoUnitaDoc();
            // } catch (EMFError ex1) {
            // logger.error(ex1.getLocalizedMessage(), ex1);
            // }
            // forwardToPublisher(Application.Publisher.FILTRI_DATI_SPEC_ASS_REG_TIPO_UNI_DOC);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            getMessageBox().addError(e.getMessage());
        }
    }

    public void eseguiModificaDatiSpecificiTipoSerie(List<DecFiltroSelUdAttbBean> listaDatiSpecOnLine,
            DecFiltroSelUdAttbTableBean table, BigDecimal idTipoSerieUd, BigDecimal idTipoSerie) throws ParerUserError {
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                        CostantiDB.TipoAplVGetValAppart.APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setNomeAzione(SpagoliteLogUtil.getButtonActionName(getForm(), getForm().getFiltriDatiSpec(),
                getForm().getFiltriDatiSpec().getSalvaFiltriDatiSpec().getName()));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        tipoSerieEjb.saveFiltrIDatiSpecTipoSerieUd(param, idTipoSerieUd.longValue(), table, listaDatiSpecOnLine,
                idTipoSerie);
        getMessageBox().addMessage(Message.MessageLevel.INF, "Filtri sui dati specifici inseriti con successo");
    }

    public void eseguiModificaRegoleRappresentazioneTipoSerie(DecTipoSerieUdRowBean decTipoSerieUd,
            DecOutSelUdRowBean rowBean, Map<String, Map<String, String>> listaAttributiSelezionati, String tiOut,
            String dlFormatoOut, Status status, BigDecimal idTipoSerie) throws ParerUserError {

        if (BaseElements.Status.insert.equals(status)) {
            rowBean = new DecOutSelUdRowBean();
            rowBean.setTiOut(tiOut);
        } else if (BaseElements.Status.update.equals(status)) {
            rowBean = (DecOutSelUdRowBean) getForm().getRegoleDiRappresentazioneList().getTable().getCurrentRow();
        }
        rowBean.setIdTipoSerieUd(decTipoSerieUd.getIdTipoSerieUd());
        rowBean.setDlFormatoOut(dlFormatoOut);

        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                        CostantiDB.TipoAplVGetValAppart.APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        tipoSerieEjb.insertDecOutSelUd(param, rowBean, listaAttributiSelezionati, idTipoSerie);
        String infisso = BaseElements.Status.update.equals(status) ? "modificata" : "inserita";
        getMessageBox().addInfo("Regola di Rappresentazione " + infisso + " con successo <br/>");
        getForm().getRegoleDiRappresentazioneList().setStatus(BaseElements.Status.view);
        getForm().getRegoleRapprDetail().setStatus(BaseElements.Status.view);
        goBackTo(Application.Publisher.REG_TIPO_UNITA_DOC_SERIE_DETAIL);
    }

    private void ricaricaListaFiltriDatiSpecificiPerAssociazioneRegistroTipoUnitaDoc() throws EMFError {
        StrutSerieForm form = getForm();
        form.getFiltriDatiSpecList().getTi_oper().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("operatore", CostantiDB.TipoOperatoreDatiSpec.values()));

        DecTipoSerieUdRowBean decTipoSerieUd = (DecTipoSerieUdRowBean) form.getTipoSerieRegistriList().getTable()
                .getCurrentRow();
        List<DecFiltroSelUdAttbBean> listaDatiSpecOnLine = new ArrayList<>();
        DecAttribDatiSpecTableBean datiSpecTB = udHelper
                .getDecAttribDatiSpecTableBean(decTipoSerieUd.getIdTipoUnitaDoc(), Constants.TipoEntitaSacer.UNI_DOC);
        aggiungiDatiSpecPerTipoUnitaDoc(datiSpecTB, listaDatiSpecOnLine);
        DecFiltroSelUdTableBean decFiltroSelUdTableBean = tipoSerieEjb
                .getDecFiltroSelUdTableBean(decTipoSerieUd.getIdTipoSerieUd());
        for (DecFiltroSelUdRowBean row : decFiltroSelUdTableBean) {
            String tiFiltro = row.getTiFiltro();
            if (CostantiDB.TipoFiltroSerieUd.TIPO_DOC_PRINC.name().equals(tiFiltro)) {

                BigDecimal idTipoDocPrinc = row.getIdTipoDocPrinc();
                DecAttribDatiSpecTableBean datiSpecTipoDocTB = udHelper.getDecAttribDatiSpecTableBean(idTipoDocPrinc,
                        Constants.TipoEntitaSacer.DOC);
                aggiungiDatiSpecPerTipoDoc(datiSpecTipoDocTB, listaDatiSpecOnLine);
            }
        }
        DecFiltroSelUdAttbTableBean decFiltroSelUdAttbList = tipoSerieEjb
                .getDecFiltroSelUdAttbList(decTipoSerieUd.getIdTipoSerieUd());
        decFiltroSelUdAttbList.setCurrentRowIndex(0);
        decFiltroSelUdAttbList.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        form.getAssociazioneDatiSpecList().setTable(decFiltroSelUdAttbList);
        List<DecFiltroSelUdAttbBean> listaDatiSpecOnLineNonCompilati = (List<DecFiltroSelUdAttbBean>) getSession()
                .getAttribute("listaDatiSpecOnLine");
        if (listaDatiSpecOnLineNonCompilati != null) {
            for (DecFiltroSelUdAttbBean datoSpecOnLineNonCompilato : listaDatiSpecOnLineNonCompilati) {
                for (DecFiltroSelUdAttbRowBean r : decFiltroSelUdAttbList) {
                    if (datoSpecOnLineNonCompilato.getNmAttribDatiSpec().equals(r.getNmAttribDatiSpec())) {
                        // modifica del valore per riferimento
                        datoSpecOnLineNonCompilato.setTiOper(r.getTiOper());
                        datoSpecOnLineNonCompilato.setDlValore(r.getDlValore());
                        datoSpecOnLineNonCompilato.setIdFiltroSelUdAttb(r.getIdFiltroSelUdAttb().longValue());
                    }
                }
            }
            updateInterfacciaOnLineDatiSpec(listaDatiSpecOnLineNonCompilati, false);
        }
        // TODO: VERIFICARE
        String cessato = (String) getRequest().getParameter("cessato");
        if ((StringUtils.isNotBlank(cessato) && "1".equals(cessato)) || getRequest().getAttribute("cessato") != null) {
            form.getFiltriDatiSpecList().setUserOperations(true, false, false, false);
        }
        // end TODO
    }

    private StrutSerieForm.FiltriDatiSpecList recuperaListaFiltriDaRequest() {

        StrutSerieForm.FiltriDatiSpecList filtriDatiSpecList = getForm().getFiltriDatiSpecList();
        String tiOper = filtriDatiSpecList.getTi_oper().getName();
        String dlValore = filtriDatiSpecList.getDl_valore().getName();
        HttpServletRequest request = getRequest();
        String tiOperValue = null;
        String dlValoreValue = null;
        BaseTableInterface<?> table = filtriDatiSpecList.getTable();
        int size = table.size();
        for (int i = 0; i < size; i++) {
            BaseRowInterface row = table.getRow(i);
            tiOperValue = request.getParameterValues(tiOper)[i];
            dlValoreValue = request.getParameterValues(dlValore)[i];
            if (StringUtils.isNotBlank(tiOperValue)) {
                row.setString(tiOper, tiOperValue);
                row.setString(dlValore, dlValoreValue);
            } else {
                row.setString(tiOper, null);
                row.setString(dlValore, null);
            }
        }
        return filtriDatiSpecList;
    }

    @Override
    public boolean inserimentoWizardOnSave() throws EMFError {
        StrutSerieForm form = getForm();
        MessageBox msgBox = getMessageBox();
        StrutSerieForm.RegoleRapprDetail regoleRapprDetail = form.getRegoleRapprDetail();
        /* Eseguo la post di tutti i dati presenti nella pagina (campi di dettaglio ed editableList) */
        regoleRapprDetail.post(getRequest());
        form.getAttributiTipoUnitaDocList().post(getRequest());
        form.getAttributiTipoDocList().post(getRequest());

        StrutSerieForm.RegoleDiRappresentazioneList regoleDiRappresentazioneList = form
                .getRegoleDiRappresentazioneList();

        BaseElements.Status status = regoleDiRappresentazioneList.getStatus();
        String tiOut = regoleRapprDetail.getTi_out().parse();
        String dlFormatoOut = regoleRapprDetail.getDl_formato_out().parse();

        StrutSerieForm.AttributiTipoUnitaDocList attributiTipoUnitaDocList = form.getAttributiTipoUnitaDocList();
        StrutSerieForm.AttributiTipoDocList attributiTipoDocList = form.getAttributiTipoDocList();
        List<String> recuperaListaDatiProfiloDaRequest = recuperaListaDatiProfiloDaRequest();

        /* Recupero i DATI PROFILO selezionati */
        BaseTableInterface<?> table = form.getDatiProfiloList().getTable();
        Map<String, Map<String, String>> listaAttributiSelezionati = new HashMap();
        Map<String, String> datiProfilo = new HashMap<>();
        boolean controlloPeriodiValidita = false;
        if (recuperaListaDatiProfiloDaRequest != null) {
            for (String dato : recuperaListaDatiProfiloDaRequest) {
                if (StringUtils.isNumeric(dato)) {
                    BaseRowInterface row = table.getRow(Integer.parseInt(dato));
                    String nmCampo = row.getString("key_campo");
                    if (CostantiDB.TipoDiRappresentazione.PG_UD_SERIE.name().equals(tiOut)) {
                        if (!CostantiDB.NomeCampo.NUMERO.name().equals(nmCampo)) {
                            getMessageBox().addError("Nel caso il nome campo sia '"
                                    + CostantiDB.TipoDiRappresentazione.PG_UD_SERIE.toString()
                                    + "' il dato profilo deve essere di tipo 'NUMERO' <br/>");
                        } else {
                            controlloPeriodiValidita = true;
                        }
                    }
                    if (CostantiDB.TipoDiRappresentazione.DS_KEY_ORD_UD_SERIE.name().equals(tiOut)
                            && CostantiDB.NomeCampo.NUMERO.name().equals(nmCampo)) {
                        datiProfilo.put(nmCampo, CostantiDB.TipoTrasformatore.OUT_PAD_CHAR.getTransformString());
                    } else {
                        datiProfilo.put(nmCampo, null);
                    }
                } else {
                    logger.error("Dato = " + dato);
                }
            }
        }

        /* Metto tutti i dati selezionati in un'unica struttura */
        listaAttributiSelezionati.put(CostantiDB.TipoCampo.DATO_PROFILO.name(), datiProfilo);
        recuperaAttributiSelezionati(attributiTipoUnitaDocList.getTable(), listaAttributiSelezionati,
                CostantiDB.TipoCampo.DATO_SPEC_UNI_DOC.name(), false);
        recuperaAttributiSelezionati(attributiTipoDocList.getTable(), listaAttributiSelezionati,
                CostantiDB.TipoCampo.DATO_SPEC_DOC_PRINC.name(), false);
        /* Controllo la TextArea del formato output in base ai campi selezionati */
        dlFormatoOut = controllaUsoDatiSpecificiNelFormato2(dlFormatoOut, listaAttributiSelezionati);
        regoleRapprDetail.getDl_formato_out().setValue(dlFormatoOut);

        /* Se è tutto OK, procedo al salvataggio */
        DecTipoSerieUdRowBean decTipoSerieUd = (DecTipoSerieUdRowBean) form.getTipoSerieRegistriList().getTable()
                .getCurrentRow();
        if (!ciSonoSelezionati(listaAttributiSelezionati)) {
            msgBox.addError("Selezionare almeno un dato.");
        }
        boolean skipSaveForUserAction = false;
        if (!msgBox.hasError() && controlloPeriodiValidita) {
            try {
                registroEjb.checkPeriodiValiditaProgressivo(decTipoSerieUd.getIdRegistroUnitaDoc());
            } catch (ParerUserError ex) {
                switch (ex.getSeverity()) {
                case ParerErrorSeverity.ERROR:
                    msgBox.addError(ex.getDescription());
                    break;
                case ParerErrorSeverity.WARNING:
                    getRequest().setAttribute("warningPeriodiValBox", true);
                    getSession().setAttribute("SAVE_ATTRIBUTI_REG", listaAttributiSelezionati);
                    skipSaveForUserAction = true;
                    break;
                default:
                    msgBox.addError("Errore nel salvataggio della regola di rappresentazione");
                    break;
                }
            }
        }

        if (!msgBox.hasError()) {
            int indexCurrentRow = getForm().getRegoleDiRappresentazioneList().getTable().getCurrentRowIndex();
            // Controlli di coerenza - se è definito il tipo PG_UD_SERIE, il tipo DS_KEY_ORD_UD_SERIE deve iniziare con
            // lo stesso valore
            DecOutSelUdTableBean regoleRapprList = (DecOutSelUdTableBean) getForm().getRegoleDiRappresentazioneList()
                    .getTable();
            List<Object> tiOutList = regoleRapprList.toList(DecOutSelUdTableDescriptor.COL_TI_OUT);

            int indexElement = -1;
            String dlFormatoOutProgressivo = null;
            String dlFormatoOutKeyOrd = null;
            if (CostantiDB.TipoDiRappresentazione.DS_KEY_ORD_UD_SERIE.name().equals(tiOut)) {
                if ((indexElement = tiOutList.indexOf(CostantiDB.TipoDiRappresentazione.PG_UD_SERIE.name())) != -1) {
                    List<Object> dlFormatoOutList = regoleRapprList
                            .toList(DecOutSelUdTableDescriptor.COL_DL_FORMATO_OUT);
                    dlFormatoOutProgressivo = (String) dlFormatoOutList.get(indexElement);

                }
            } else if (CostantiDB.TipoDiRappresentazione.PG_UD_SERIE.name().equals(tiOut)) {
                if ((indexElement = tiOutList
                        .indexOf(CostantiDB.TipoDiRappresentazione.DS_KEY_ORD_UD_SERIE.name())) != -1) {
                    List<Object> dlFormatoOutList = regoleRapprList
                            .toList(DecOutSelUdTableDescriptor.COL_DL_FORMATO_OUT);
                    dlFormatoOutKeyOrd = (String) dlFormatoOutList.get(indexElement);
                }
            }
            if (indexElement != -1) {
                if (StringUtils.isNotBlank(dlFormatoOutProgressivo)) {
                    if (!dlFormatoOut.startsWith(dlFormatoOutProgressivo)) {
                        msgBox.addError(
                                "La regola definita sul progressivo deve coincidere o rappresentare la prima parte della regola definita sull'ordinamento delle unit\u00E0 documentarie appartenenti alla serie");
                    }
                } else if (StringUtils.isNotBlank(dlFormatoOutKeyOrd)) {
                    if (!dlFormatoOutKeyOrd.startsWith(dlFormatoOut)) {
                        msgBox.addError(
                                "La regola definita sul progressivo deve coincidere o rappresentare la prima parte della regola definita sull'ordinamento delle unit\u00E0 documentarie appartenenti alla serie");
                    }
                }
            }
            getForm().getRegoleDiRappresentazioneList().getTable().setCurrentRowIndex(indexCurrentRow);
        }

        boolean goToSave = false;
        if (!msgBox.hasError() && !skipSaveForUserAction) {
            goToSave = salvaRegolaRappresentazione(listaAttributiSelezionati, tiOut, dlFormatoOut);
        }

        return !msgBox.hasError() && !skipSaveForUserAction && goToSave;
    }

    public void confermaSalvataggioRegolaRappresentazione() throws EMFError {
        Map<String, Map<String, String>> listaAttributiSelezionati = (Map<String, Map<String, String>>) getSession()
                .getAttribute("SAVE_ATTRIBUTI_REG");
        String tiOut = getForm().getRegoleRapprDetail().getTi_out().parse();
        String dlFormatoOut = getForm().getRegoleRapprDetail().getDl_formato_out().parse();
        if (listaAttributiSelezionati != null) {
            salvaRegolaRappresentazione(listaAttributiSelezionati, tiOut, dlFormatoOut);
        } else {
            getMessageBox().addError("Errore nel salvataggio della regola di rappresentazione");
        }
    }

    private boolean salvaRegolaRappresentazione(Map<String, Map<String, String>> listaAttributiSelezionati,
            String tiOut, String dlFormatoOut) {
        boolean goToSave = false;
        try {
            DecTipoSerieUdRowBean decTipoSerieUd = (DecTipoSerieUdRowBean) getForm().getTipoSerieRegistriList()
                    .getTable().getCurrentRow();
            DecOutSelUdRowBean rowBean = null;
            BaseElements.Status status = getForm().getRegoleDiRappresentazioneList().getStatus();
            // if (BaseElements.Status.insert.equals(status)) {
            // rowBean = new DecOutSelUdRowBean();
            // rowBean.setTiOut(tiOut);
            // rowBean.setIdTipoSerieUd(decTipoSerieUd.getIdTipoSerieUd());
            // rowBean.setDlFormatoOut(dlFormatoOut);
            // tipoSerieEjb.insertDecOutSelUd(rowBean, listaAttributiSelezionati);
            // getMessageBox().addInfo("Regola di Rappresentazione inserita con successo <br/>");
            // } else if (BaseElements.Status.update.equals(status)) {
            // rowBean = (DecOutSelUdRowBean) getForm().getRegoleDiRappresentazioneList().getTable().getCurrentRow();
            // rowBean.setIdTipoSerieUd(decTipoSerieUd.getIdTipoSerieUd());
            // rowBean.setDlFormatoOut(dlFormatoOut);
            // tipoSerieEjb.insertDecOutSelUd(rowBean, listaAttributiSelezionati);
            // getMessageBox().addInfo("Regola di Rappresentazione modificata con successo <br/>");
            //
            // }
            // getForm().getRegoleDiRappresentazioneList().setStatus(BaseElements.Status.view);
            // getForm().getRegoleRapprDetail().setStatus(BaseElements.Status.view);
            // goBackTo(Application.Publisher.REG_TIPO_UNITA_DOC_SERIE_DETAIL);

            BigDecimal idTipoSerie = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable()
                    .getCurrentRow()).getIdTipoSerie();

            if (serieEjb.checkSerieModificabili(idTipoSerie)) {
                Object[] attributi = new Object[7];
                attributi[0] = decTipoSerieUd;
                attributi[1] = rowBean;
                attributi[2] = listaAttributiSelezionati;
                attributi[3] = tiOut;
                attributi[4] = dlFormatoOut;
                attributi[5] = status;
                attributi[6] = idTipoSerie;
                getSession().setAttribute("salvataggioAttributesRegoleRappresentazioneTipoSerie", attributi);
                getRequest().setAttribute("customBox", true);
            } else {
                goToSave = true;
                eseguiModificaRegoleRappresentazioneTipoSerie(decTipoSerieUd, rowBean, listaAttributiSelezionati, tiOut,
                        dlFormatoOut, status, idTipoSerie);
            }

        } catch (Exception ex) {
            logger.error(ExceptionUtils.getRootCauseMessage(ex), ex);
            getMessageBox().addError(ex.getMessage());
        }
        return goToSave;
    }

    /**
     * Recupera i valori delle liste dei dati specifici su Tipo Unita' Documentaria e Tipo Documento e li mette in una
     * mappa
     *
     * @param table
     *            bean {@link BaseTableInterface}
     * @param mappaAttributiSelezionati
     *            mappa chiave/valore
     * @param daDove
     *            da dove
     * @param isInput
     *            true/false
     */
    private void recuperaAttributiSelezionati(BaseTableInterface<?> table,
            Map<String, Map<String, String>> mappaAttributiSelezionati, String daDove, boolean isInput) {
        if (table != null) {
            String nm_attrib_dati_spec = "Nm_attrib_dati_spec";
            String ti_trasform_campo = "Ti_trasform_campo";
            String pgOrdCampo = "";
            String fl_selezionato = null;
            CostantiDB.TipoCampo tipoCampo = CostantiDB.TipoCampo.valueOf(daDove);

            switch (tipoCampo) {
            case DATO_SPEC_UNI_DOC:
                fl_selezionato = "Tipo_unita_selezionato";
                ti_trasform_campo = getForm().getAttributiTipoUnitaDocList().getTi_trasform_campo_tipo_unita_doc()
                        .getName();// "Ti_unita_trasform_campo";
                pgOrdCampo = getForm().getAttributiTipoUnitaDocList().getPg_ord_campo_tipo_unita_doc().getName();
                break;
            case DATO_SPEC_DOC_PRINC:
                fl_selezionato = "Tipo_doc_selezionato";
                ti_trasform_campo = getForm().getAttributiTipoDocList().getTi_trasform_campo_tipo_doc().getName();// "Ti_doc_trasform_campo";
                pgOrdCampo = getForm().getAttributiTipoDocList().getPg_ord_campo_tipo_doc().getName();
                break;

            default:
                fl_selezionato = null;
                ti_trasform_campo = null;
            }
            Map<String, String> campiMap = mappaAttributiSelezionati.get(daDove);
            if (campiMap == null) {
                campiMap = new HashMap();
            }

            // Scorro tutti gli elementi della lista (selezionati o meno)
            for (BaseRowInterface row : table) {
                String nmAttributiDatiSpec = row.getString(nm_attrib_dati_spec);
                if (nmAttributiDatiSpec.contains("(vers.")) {
                    nmAttributiDatiSpec = nmAttributiDatiSpec.substring(0, nmAttributiDatiSpec.indexOf("(vers."))
                            .trim();
                }
                // Se ho selezionato l'elemento, procedo ai controlli
                BigDecimal flag = row.getBigDecimal(fl_selezionato);
                if (flag != null && flag.equals(BigDecimal.ONE)) {
                    String tiTrasformCampo = row.getString(ti_trasform_campo);
                    if (StringUtils.isBlank(tiTrasformCampo)) {
                        tiTrasformCampo = "none";
                    }
                    String pgOrdCampoValue = row.getString(pgOrdCampo);

                    DecAttribDatiSpecRowBean decAttribDatiSpec = tipoSerieEjb
                            .getDecAttribDatiSpecById(row.getBigDecimal("id_attrib_dati_spec"), tiTrasformCampo);
                    if (decAttribDatiSpec != null) {
                        if (isInput) {
                            // Il value della mappa Ã¨ una concatenazione di valori :
                            // {tiTRASFORM}|{pgORDCampo}|{idAttributoDatiSpec}
                            // sono i valori inseriti dall'utente rispettivamente
                            // nella combo del trasformatore e nel campo dell'ordine.
                            campiMap.put(nmAttributiDatiSpec, tiTrasformCampo + "|" + pgOrdCampoValue + "|"
                                    + decAttribDatiSpec.getIdAttribDatiSpec().toPlainString());
                        } else {
                            campiMap.put(nmAttributiDatiSpec, tiTrasformCampo);
                        }
                    }
                }
            }
            mappaAttributiSelezionati.put(daDove, campiMap);
        }
    }

    @Override
    public void inserimentoWizardOnCancel() throws EMFError {
        getForm().getRegoleDiRappresentazioneList().setStatus(BaseElements.Status.view);
        goBack();
    }

    @Override
    public String getDefaultInserimentoWizardPublisher() throws EMFError {
        return Application.Publisher.REGOLA_RAPPRESENTAZIONE_DETAIL;
    }

    @Override
    public void inserimentoWizardTipoRapprOnEnter() throws EMFError {

        StrutSerieForm.RegoleDiRappresentazioneList regoleDiRappresentazioneList = getForm()
                .getRegoleDiRappresentazioneList();
        StrutSerieForm.RegoleRapprDetail regoleRapprDetail = getForm().getRegoleRapprDetail();
        BaseElements.Status status = regoleDiRappresentazioneList.getStatus();
        List<String> listaGiaInseriti = new ArrayList<>();
        if (BaseElements.Status.insert.equals(status)) {
            BigDecimal idTipoSerieUd = ((DecTipoSerieUdRowBean) getForm().getTipoSerieRegistriList().getTable()
                    .getCurrentRow()).getIdTipoSerieUd();
            DecOutSelUdTableBean decOutSelUdTableBean = tipoSerieEjb.getDecOutSelUdTableBean(idTipoSerieUd);
            for (DecOutSelUdRowBean rowBean : decOutSelUdTableBean) {
                String tiOut = rowBean.getTiOut();
                listaGiaInseriti.add(tiOut);
            }
            DecodeMap mappaTipoDiRappresentazione = getMappaTipoDiRappresentazione(listaGiaInseriti);
            regoleRapprDetail.getTi_out().setDecodeMap(mappaTipoDiRappresentazione);

        }
        if (BaseElements.Status.update.equals(status)) {
            regoleRapprDetail.getTi_out().setViewMode();
        }
    }

    @Override
    public boolean inserimentoWizardTipoRapprOnExit() throws EMFError {
        boolean vai = true;
        StrutSerieForm.RegoleRapprDetail regoleRapprDetail = getForm().getRegoleRapprDetail();
        regoleRapprDetail.post(getRequest());
        String ti_out = regoleRapprDetail.getTi_out().parse();
        if (StringUtils.isBlank(ti_out)) {
            vai = false;
            getMessageBox().addError("Selezionare il tipo di rappresentazione! <br/>");
        }
        return vai;
    }

    @Override
    public void inserimentoWizardCampiOutOnEnter() throws EMFError {
        /* 'DATO_PROFILO', 'DATO_SPEC_DOC_PRINC', 'DATO_SPEC_UNI_DOC' */
        StrutSerieForm.RegoleRapprDetail regoleRapprDetail = getForm().getRegoleRapprDetail();
        StrutSerieForm.DatiProfiloList datiProfiloList = getForm().getDatiProfiloList();

        // datiProfiloList.getFl_selezionato().setReadonly(false);
        // datiProfiloList.getFl_selezionato().setEditMode();
        //
        // datiProfiloList.getNm_campo().setReadonly(true);
        // datiProfiloList.getNm_campo().setHidden(false);
        //
        // datiProfiloList.getPg_ord_campo_dato_profilo().setViewMode();
        // datiProfiloList.getPg_ord_campo_dato_profilo().setHidden(true);
        setVisibilityFiedsForUpdateDatiProfiloList(false, true, false, true);

        BigDecimal idOutSelUd = null;
        Set<CostantiDB.NomeCampo> listaTiCampo = null;
        BaseElements.Status status = regoleRapprDetail.getStatus();
        regoleRapprDetail.post(getRequest());

        if (BaseElements.Status.update.equals(status)) {
            DecOutSelUdRowBean outSelUdRowBean = (DecOutSelUdRowBean) getForm().getRegoleDiRappresentazioneList()
                    .getTable().getCurrentRow();
            idOutSelUd = outSelUdRowBean.getIdOutSelUd();
            regoleRapprDetail.getDl_formato_out().setValue(outSelUdRowBean.getDlFormatoOut());
            listaTiCampo = getListaNomeCampoPerDecOutSelUd(idOutSelUd, CostantiDB.TipoCampo.DATO_PROFILO.name());

        }
        DecTipoSerieUdRowBean decTipoSerieUd = (DecTipoSerieUdRowBean) getForm().getTipoSerieRegistriList().getTable()
                .getCurrentRow();
        BigDecimal idTipoSerieUd = decTipoSerieUd.getIdTipoSerieUd();
        DecFiltroSelUdTableBean decFiltroSelUdTableBean = tipoSerieEjb.getDecFiltroSelUdTableBean(idTipoSerieUd);
        List<DecFiltroSelUdRowBean> listaTipiDocPrinc = getListaFiltriPerTipo(decFiltroSelUdTableBean,
                CostantiDB.TipoFiltroSerieUd.TIPO_DOC_PRINC.name());

        ricaricaDatiSpecificiPerRegoladiRappresentazione(idOutSelUd);
        inizializzaListaDatiProfilo(listaTiCampo, datiProfiloList);

        // StrutSerieForm.AttributiTipoUnitaDocList attributiTipoUnitaDocList =
        // getForm().getAttributiTipoUnitaDocList();
        // attributiTipoUnitaDocList.getTi_trasform_campo_tipo_unita_doc().setEditMode();
        // attributiTipoUnitaDocList.getTipo_unita_selezionato().setReadonly(false);
        // attributiTipoUnitaDocList.getTipo_unita_selezionato().setEditMode();
        // attributiTipoUnitaDocList.getPg_ord_campo_tipo_unita_doc().setViewMode();
        // attributiTipoUnitaDocList.getPg_ord_campo_tipo_unita_doc().setHidden(true);
        setVisibilityFiedsForUpdateDatiSpecTipoUdList(false, true, false, true);

        StrutSerieForm.AttributiTipoDocList attributiTipoDocList = getForm().getAttributiTipoDocList();
        // attributiTipoDocList.getTi_trasform_campo_tipo_doc().setEditMode();
        // attributiTipoDocList.getTipo_doc_selezionato().setReadonly(false);
        // attributiTipoDocList.getTipo_doc_selezionato().setEditMode();
        // attributiTipoDocList.getPg_ord_campo_tipo_doc().setViewMode();
        // attributiTipoDocList.getPg_ord_campo_tipo_doc().setHidden(true);

        setVisibilityFiedsForUpdateDatiSpecTipoDocList(false, true, false, true);

        if (listaTipiDocPrinc != null && listaTipiDocPrinc.size() == 1) {
            getForm().getDatiSpecTipoDocSection().setHidden(false);
        } else {
            attributiTipoDocList.clear();
            getForm().getDatiSpecTipoDocSection().setHidden(true);
        }
    }

    private void inizializzaListaDatiProfilo(Set<CostantiDB.NomeCampo> listaTiCampo,
            StrutSerieForm.DatiProfiloList datiProfiloList) {
        BaseTableInterface tabella = new BaseTable();
        String key1 = "key_campo";
        String key2 = "nm_campo";
        for (CostantiDB.NomeCampo campo : CostantiDB.NomeCampo.getListaDatoProfilo()) {
            BaseRow row = new BaseRow();
            String name = campo.name();
            row.setString(key1, name);
            row.setString(key2, campo.getDescrizione());
            BigDecimal selezionato = (listaTiCampo != null && listaTiCampo.contains(campo)) ? BigDecimal.ONE
                    : BigDecimal.ZERO;
            row.setBigDecimal("fl_selezionato", selezionato);
            tabella.add(row);
        }
        datiProfiloList.setTable(tabella);
        datiProfiloList.getTable().setPageSize(300);
    }

    @Override
    public boolean inserimentoWizardCampiOutOnExit() throws EMFError {
        aggiornaListaDatiProfilo();
        getForm().getRegoleRapprDetail().post(getRequest());
        recuperaListaDatiSpecificiDaRequest(Constants.TipoEntitaSacer.UNI_DOC);
        recuperaListaDatiSpecificiDaRequest(Constants.TipoEntitaSacer.DOC);
        return true;
    }

    private void caricaComboRegoleRappresentazione() {
        StrutSerieForm form = getForm();
        StrutSerieForm.RegoleRapprDetail dettaglio = form.getRegoleRapprDetail();
        StrutSerieForm.TipoSerieRegistriList tipoSerieRegistriList = getForm().getTipoSerieRegistriList();
        DecTipoSerieUdRowBean decTipoSerieUd = (DecTipoSerieUdRowBean) tipoSerieRegistriList.getTable().getCurrentRow();
        DecOutSelUdTableBean decOutSelUdTableBean = tipoSerieEjb
                .getDecOutSelUdTableBean(decTipoSerieUd.getIdTipoSerieUd());
        List<String> giaInseriti = new ArrayList<>();
        for (DecOutSelUdRowBean rowBean : decOutSelUdTableBean) {
            String tiOut = rowBean.getTiOut();
            giaInseriti.add(tiOut);
        }
        dettaglio.getTi_out().setDecodeMap(getMappaTipoDiRappresentazione(giaInseriti));
        StrutSerieForm.AttributiTipoUnitaDocList attributiTipoUnitaDocList = form.getAttributiTipoUnitaDocList();
        StrutSerieForm.AttributiTipoDocList attributiTipoDocList = form.getAttributiTipoDocList();
        attributiTipoUnitaDocList.getTi_trasform_campo_tipo_unita_doc()
                .setDecodeMap(ComboGetter.getMappaTipoDiTrasformatore());
        attributiTipoUnitaDocList.getTipo_unita_selezionato().setReadonly(false);
        attributiTipoDocList.getTi_trasform_campo_tipo_doc().setDecodeMap(ComboGetter.getMappaTipoDiTrasformatore());
        attributiTipoDocList.getTipo_doc_selezionato().setReadonly(false);

        caricaTipoSerieUdRif(form);
    }

    private void caricaTipoSerieUdRif(StrutSerieForm form) {
        DecTipoSerieUdRowBean row = ((DecTipoSerieUdRowBean) form.getTipoSerieRegistriList().getTable()
                .getCurrentRow());
        BigDecimal idTipoUnitaDoc = row.getIdTipoUnitaDoc();
        DecTipoUnitaDocRowBean decTipoUnitaDoc = tipoUnitaDocEjb.getDecTipoUnitaDoc(idTipoUnitaDoc, null, null);
        form.getStrutRif().getTipo_unita_doc().setValue(decTipoUnitaDoc.getNmTipoUnitaDoc());
        BigDecimal idRegistroUnitaDoc = row.getIdRegistroUnitaDoc();
        DecRegistroUnitaDocRowBean decRegistroUnitaDocRowBean = registroEjb
                .getDecRegistroUnitaDocRowBean(idRegistroUnitaDoc, null);
        form.getStrutRif().getRegistro_unita_doc().setValue(decRegistroUnitaDocRowBean.getCdRegistroUnitaDoc());
    }

    private DecodeMap getMappaTipoDiRappresentazione(List<String> listaGiaInseriti) {
        BaseTable bt = new BaseTable();
        String key = "ti_out";
        String key2 = "transcode_out";
        DecodeMap mappaTiOut = new DecodeMap();
        for (CostantiDB.TipoDiRappresentazione tiOut : CostantiDB.TipoDiRappresentazione
                .getComboTipoDiRappresentazione()) {
            if (listaGiaInseriti == null || !listaGiaInseriti.contains(tiOut.name())) {
                BaseRow row = new BaseRow();
                row.setString(key, tiOut.name());
                row.setString(key2, tiOut.toString());
                bt.add(row);
            }
        }
        mappaTiOut.populatedMap(bt, key, key2);
        return mappaTiOut;
    }

    private Set<CostantiDB.NomeCampo> getListaNomeCampoPerDecOutSelUd(BigDecimal idOutSelUd, String tipoCampo) {
        Set<CostantiDB.NomeCampo> result = null;

        DecCampoOutSelUdTableBean decCampoOutSelUdTableBeanPerTIpoSerieUd = tipoSerieEjb
                .getDecCampoOutSelUdTableBeanPerDecOutSelUd(idOutSelUd, tipoCampo);
        for (DecCampoOutSelUdRowBean row : decCampoOutSelUdTableBeanPerTIpoSerieUd) {
            if (result == null) {
                result = new HashSet<>();
            }
            if (row != null) {
                String nmCampo = row.getNmCampo();
                CostantiDB.NomeCampo b = CostantiDB.NomeCampo.byName(nmCampo);
                result.add(b);
            }
        }
        return result;
    }

    private List<String> recuperaListaDatiProfiloDaRequest() {
        List<String> selezionatiList = null;
        StrutSerieForm.DatiProfiloList datiProfiloListList = getForm().getDatiProfiloList();
        String dlValore = datiProfiloListList.getFl_selezionato().getName();
        HttpServletRequest request = getRequest();
        String[] selezionati = request.getParameterValues(dlValore);
        if (selezionati != null) {
            selezionatiList = Arrays.asList(selezionati);
        }
        return selezionatiList;
    }

    /**
     * Aggiorna a video i flag presenti nella Lista Dati di Profilo nel dettaglio delle regole di rappresentazione
     *
     * @throws EMFError
     *             errore generico
     */
    private Map<String, String> aggiornaListaDatiProfilo() throws EMFError {
        /* Eseguo il post dei flag della lista Dati di Profilo */
        // getForm().getDatiProfiloList().post(getRequest());
        /* Recupero solo i record selezionati */
        List<String> listaDatiProfiloDaRequest = recuperaListaDatiProfiloDaRequest();
        BaseTableInterface<?> table = getForm().getDatiProfiloList().getTable();// ;
        StrutSerieForm.DatiProfiloList datiProfiloList = getForm().getDatiProfiloList();
        int size = table.size();
        /*
         * Checka o dechecka i flag a seconda delle informazioni provenienti dalla request Se ho almeno un flag
         * checkato, discerno i casi...
         */
        Map<String, String> datiProfilo = new HashMap<>();
        if (listaDatiProfiloDaRequest != null) {
            for (int i = 0; i < size; i++) {
                BaseRowInterface row = table.getRow(i);
                if (listaDatiProfiloDaRequest.contains(("" + i))) {
                    row.setBigDecimal(datiProfiloList.getFl_selezionato().getName(), BigDecimal.ONE);
                    String pg_ord_campo = getForm().getDatiProfiloList().getPg_ord_campo_dato_profilo().getName();
                    String nmCampo = row.getString("key_campo");
                    String pg_ord_campoValue = null;
                    if (CollectionUtils.contains(getRequest().getParameterNames(), pg_ord_campo)) {
                        pg_ord_campoValue = getRequest().getParameterValues(pg_ord_campo)[i];
                        if (StringUtils.isNotBlank(pg_ord_campoValue)) {
                            if (!StringUtils.isNumeric(pg_ord_campoValue)) {
                                if (!getMessageBox().hasError()) {
                                    getMessageBox().addError(
                                            "Attenzione: uno dei campi selezionati ha un numero ordine formalmente non corretto");
                                }
                            }
                        } else if (!getMessageBox().hasError()) {
                            getMessageBox().addError("Attenzione: numero d'ordine assente");
                        }
                    }
                    row.setString(pg_ord_campo, pg_ord_campoValue);
                    datiProfilo.put(nmCampo, pg_ord_campoValue);

                } else {
                    row.setBigDecimal(datiProfiloList.getFl_selezionato().getName(), BigDecimal.ZERO);
                }
            }
        } /* altrimenti sono tutti da decheckare */ else {
            for (int i = 0; i < size; i++) {
                BaseRowInterface row = table.getRow(i);
                row.setBigDecimal(datiProfiloList.getFl_selezionato().getName(), BigDecimal.ZERO);
            }
        }
        datiProfiloList.setTable(table);
        datiProfiloList.getTable().setPageSize(300);
        return datiProfilo;
    }

    private void ricaricaDatiSpecificiPerRegoladiRappresentazione(BigDecimal idOutSelUd) {
        StrutSerieForm form = getForm();
        Map<String, DecCampoOutSelUdRowBean> listaDatiSpecSalvati = new HashMap<>();

        DecTipoSerieUdRowBean decTipoSerieUd = (DecTipoSerieUdRowBean) form.getTipoSerieRegistriList().getTable()
                .getCurrentRow();
        BigDecimal idTipoSerieUd = decTipoSerieUd.getIdTipoSerieUd();
        DecAttribDatiSpecTableBean datiSpecTB = tipoSerieEjb
                .getDecAttribDatiSpecTableBean(decTipoSerieUd.getIdTipoUnitaDoc(), Constants.TipoEntitaSacer.UNI_DOC);
        BaseTableInterface datiSpecTipoUnitaDocList = listBean2TableBeanPerRappresentazione(datiSpecTB,
                Constants.TipoEntitaSacer.UNI_DOC);

        // controllaSeDatiSpecPresentiInAssociazione(datiSpecTipoUnitaDocList, listaDatiSpecDaAssociazione);
        DecCampoOutSelUdTableBean decCampoOutSelUdTableBeanPerTIpoSerieUd = tipoSerieEjb
                .getDecCampoOutSelUdTableBeanPerDecOutSelUd(idOutSelUd, CostantiDB.TipoCampo.DATO_SPEC_UNI_DOC.name());
        for (DecCampoOutSelUdRowBean row : decCampoOutSelUdTableBeanPerTIpoSerieUd) {
            BigDecimal idAttribDatiSpec = row.getIdAttribDatiSpec();
            DecAttribDatiSpecRowBean attributo = datiSpecEjb.getDecAttribDatiSpecRowBean(idAttribDatiSpec);
            listaDatiSpecSalvati.put(attributo.getNmAttribDatiSpec(), row);
        }
        Iterator iterator = datiSpecTipoUnitaDocList.iterator();
        while (iterator.hasNext()) {
            BaseRow row = (BaseRow) iterator.next();
            //
            String attrib = row.getString("nm_attrib_dati_spec");
            if (attrib.indexOf("(vers.") > 0) {
                attrib = attrib.substring(0, attrib.indexOf("(vers.")).trim();
            }
            row.setString("key_campo", attrib);
            if (listaDatiSpecSalvati.keySet().contains(attrib)) {

                row.setBigDecimal("tipo_unita_selezionato", BigDecimal.ONE);
                DecCampoOutSelUdRowBean rowBean = listaDatiSpecSalvati.get(attrib);
                String tiTrasformCampo = StringEscapeUtils.escapeHtml4(rowBean.getTiTrasformCampo());
                // String name = form.getAttributiTipoUnitaDocList().getTi_trasform_campo_tipo_unita_doc().getName();
                row.setString("ti_trasform_campo", tiTrasformCampo);
            }

        }
        form.getAttributiTipoUnitaDocList().setTable(datiSpecTipoUnitaDocList);
        form.getAttributiTipoUnitaDocList().getTable().setPageSize(300);
        form.getAttributiTipoUnitaDocList().getPg_ord_campo_tipo_unita_doc().setHidden(true);

        /*
         * Esamino i dati specifici sul tipo documento controllando DEC_FILTRO_SEL_UD RICAVO I DATI SPECIFICI SUL TIPO
         * DOC SOLO SE HO UN SOLO TIPO DOC PRINCIPALE
         */
        BaseTableInterface datiSpecTipoDocList = null;
        DecFiltroSelUdTableBean decFiltroSelUdTableBean = tipoSerieEjb.getDecFiltroSelUdTableBean(idTipoSerieUd);
        int contaTipiDocPrinc = 0;
        for (DecFiltroSelUdRowBean row : decFiltroSelUdTableBean) {
            String tiFiltro = row.getTiFiltro();
            if (CostantiDB.TipoFiltroSerieUd.TIPO_DOC_PRINC.name().equals(tiFiltro) && contaTipiDocPrinc < 1) {
                contaTipiDocPrinc++;
                BigDecimal idTipoDocPrinc = row.getIdTipoDocPrinc();
                DecAttribDatiSpecTableBean datiSpecTipoDocTB = tipoSerieEjb
                        .getDecAttribDatiSpecTableBean(idTipoDocPrinc, Constants.TipoEntitaSacer.DOC);
                BaseTableInterface table = null;
                if (datiSpecTipoDocList == null) {
                    datiSpecTipoDocList = listBean2TableBeanPerRappresentazione(datiSpecTipoDocTB,
                            Constants.TipoEntitaSacer.DOC);
                } else {
                    table = listBean2TableBeanPerRappresentazione(datiSpecTipoDocTB, Constants.TipoEntitaSacer.DOC);
                    for (Iterator it = table.iterator(); it.hasNext();) {
                        BaseRowInterface tablerow = (BaseRowInterface) it.next();
                        datiSpecTipoDocList.add(tablerow);
                    }
                }
            }
        }

        listaDatiSpecSalvati.clear();

        decCampoOutSelUdTableBeanPerTIpoSerieUd = tipoSerieEjb.getDecCampoOutSelUdTableBeanPerDecOutSelUd(idOutSelUd,
                CostantiDB.TipoCampo.DATO_SPEC_DOC_PRINC.name());
        for (DecCampoOutSelUdRowBean row : decCampoOutSelUdTableBeanPerTIpoSerieUd) {
            BigDecimal idAttribDatiSpec = row.getIdAttribDatiSpec();
            DecAttribDatiSpecRowBean attributo = datiSpecEjb.getDecAttribDatiSpecRowBean(idAttribDatiSpec);
            listaDatiSpecSalvati.put(attributo.getNmAttribDatiSpec(), row);
        }
        if (datiSpecTipoDocList != null) {
            iterator = datiSpecTipoDocList.iterator();
            while (iterator.hasNext()) {
                BaseRow row = (BaseRow) iterator.next();
                //
                String attrib = row.getString("nm_attrib_dati_spec");
                if (attrib.indexOf("(vers.") > 0) {
                    attrib = attrib.substring(0, attrib.indexOf("(vers.")).trim();
                }
                row.setString("key_campo", attrib);
                if (listaDatiSpecSalvati.keySet().contains(attrib)) {
                    row.setBigDecimal("tipo_doc_selezionato", BigDecimal.ONE);
                    DecCampoOutSelUdRowBean rowBean = listaDatiSpecSalvati.get(attrib);
                    // String name = form.getAttributiTipoDocList().getTi_trasform_campo_tipo_doc().getName();
                    row.setString("ti_trasform_campo", rowBean.getTiTrasformCampo());
                }
            }
        }
        form.getAttributiTipoDocList().setTable(datiSpecTipoDocList);
        if (datiSpecTipoDocList != null) {
            form.getAttributiTipoDocList().getTable().setPageSize(300);
        }
        form.getAttributiTipoDocList().getPg_ord_campo_tipo_doc().setHidden(true);
    }

    private BaseTableInterface listBean2TableBeanPerRappresentazione(DecAttribDatiSpecTableBean listaDatiSpec,
            Constants.TipoEntitaSacer tipoEntita) {
        BaseTableInterface tabellaDatiSpec = new BaseTable();
        /*
         * <Input Type="STRING" Description="Nome attributo" Name="nm_attrib_dati_spec" /> <ComboBox
         * Name="ti_trasform_campo" Description="Tipo di trasformazione" Type="STRING" /> <CheckBox
         * Name="fl_selezionato" Description="" Type="INTEGER" Readonly="true" /> tipo_unita_selezionato
         */
        String fl_selezionato = null;
        switch (tipoEntita) {
        case UNI_DOC:
            fl_selezionato = "tipo_unita_selezionato";
            break;

        case DOC:
            fl_selezionato = "tipo_doc_selezionato";
            break;

        default:
            fl_selezionato = "";
        }
        if (listaDatiSpec != null) {
            for (DecAttribDatiSpecRowBean datoSpec : listaDatiSpec) {
                BaseRowInterface rigaDatoSpec = new BaseRow();
                String nomeAttributo = datoSpec.getNmAttribDatiSpec();
                rigaDatoSpec.setBigDecimal("id_attrib_dati_spec", datoSpec.getIdAttribDatiSpec());
                rigaDatoSpec.setString("nm_attrib_dati_spec", nomeAttributo);
                rigaDatoSpec.setString("nm_campo", nomeAttributo);
                rigaDatoSpec.setString("ti_trasform_campo", null);
                rigaDatoSpec.setBigDecimal(fl_selezionato, BigDecimal.ZERO);

                tabellaDatiSpec.add(rigaDatoSpec);
            }
        } else {

            BaseRowInterface rigaDatoSpec = new BaseRow();
            rigaDatoSpec.setBigDecimal("id_attrib_dati_spec", null);
            rigaDatoSpec.setString("nm_attrib_dati_spec", null);
            rigaDatoSpec.setString("nm_campo", null);
            rigaDatoSpec.setString("ti_trasform_campo", null);
            rigaDatoSpec.setBigDecimal(fl_selezionato, BigDecimal.ZERO);
            tabellaDatiSpec.add(rigaDatoSpec);

        }
        return tabellaDatiSpec;
    }

    private String controllaUsoDatiSpecificiNelFormato2(String dlFormatoOut,
            Map<String, Map<String, String>> listaAttributiSelezionati) {
        List<String> listaPerVerifica = ListaTagSelezionatiPerVerifica(listaAttributiSelezionati);

        /* Ricavo la lista dei tag selezionati dalle CheckBox */
        List<String> listaTagSelezionatiCheckBox = new ArrayList<>(listaPerVerifica);
        /* Ricavo la lista dei tag presenti nella TextArea */
        List<String> listaTagTextArea = new ArrayList<>();
        if (dlFormatoOut != null) {
            Pattern tagPatternTextArea = Pattern.compile("<(\\w+[-\\w]*)>");
            Matcher mTA = tagPatternTextArea.matcher(dlFormatoOut);
            while (mTA.find()) {
                listaTagTextArea.add(mTA.group(1));
            }
        }

        /*
         * Se ho un solo campo selezionato ho solo due possibilità: - o c'è quel campo - o non deve esserci nulla!
         */
        if (listaTagSelezionatiCheckBox.size() == 1) {
            /*
             * Se ho scritto "a mano" qualcosa nella text area, controllo che sia presente l'unico campo selezionato
             */
            if (!StringUtils.isBlank(dlFormatoOut)) {
                boolean trovato = false;
                for (String tagTextArea : listaTagTextArea) {
                    if (listaTagSelezionatiCheckBox.get(0).equals(tagTextArea)) {
                        trovato = true;
                    }
                    if (!trovato) {
                        getMessageBox().addError("Errore: l'attributo " + listaTagSelezionatiCheckBox.get(0)
                                + " non \u00e8 stato inserito! <br/>");
                    }
                }
            } /* Altrimenti se non è presente nulla lo inserisco io in automatico */ else {
                dlFormatoOut = "<" + listaTagSelezionatiCheckBox.get(0) + ">";
            }
        } else if (listaTagSelezionatiCheckBox.size() > 1) {
            /* Cerco eventuali campi settati nelle checkBox ma non presenti nella Text Area */

            List<String> nonTrovatiNellaTextArea = new ArrayList<>();
            for (String tagSelezionatiCheckBox : listaTagSelezionatiCheckBox) {
                boolean trovato = false;
                for (String tagTextArea : listaTagTextArea) {
                    if (tagSelezionatiCheckBox.equals(tagTextArea)) {
                        trovato = true;
                    }

                }
                if (!trovato) {
                    nonTrovatiNellaTextArea.add(tagSelezionatiCheckBox);
                }

            }
            if (!nonTrovatiNellaTextArea.isEmpty()) {
                getMessageBox().addError(
                        "I seguenti attributi non sono stati inseriti nella text area per il formato di rappresentazione: <br/>");
                for (String nonTrovatoNellaTextArea : nonTrovatiNellaTextArea) {
                    getMessageBox().addError(nonTrovatoNellaTextArea + " <br/>");
                }
                getMessageBox().addError("<br/>");
            }

        }

        /* Cerco eventuali campi settati nella Text Area ma non selezionati da CheckBox */
        if (!listaTagTextArea.isEmpty()) {
            List<String> nonTrovatiNelleCheckBox = new ArrayList<>();
            for (String tagTextArea : listaTagTextArea) {
                boolean trovato = false;
                for (String tagSelezionatiCheckBox : listaTagSelezionatiCheckBox) {
                    if (tagSelezionatiCheckBox.equals(tagTextArea)) {
                        trovato = true;
                    }
                }
                if (!trovato) {
                    nonTrovatiNelleCheckBox.add(tagTextArea);
                }

            }
            if (!nonTrovatiNelleCheckBox.isEmpty()) {
                getMessageBox().addError("I seguenti attributi non sono stati selezionati nelle liste: <br/>");
                for (String nonTrovatoNelleCheckBox : nonTrovatiNelleCheckBox) {
                    getMessageBox().addError(nonTrovatoNelleCheckBox + " <br/>");
                }
                getMessageBox().addError("<br/>");
            }

        }

        return dlFormatoOut;
    }

    private List<String> ListaTagSelezionatiPerVerifica(Map<String, Map<String, String>> listaAttributiSelezionati) {
        List<String> result = new ArrayList<>();
        /*
         * listaAttributiSelezionati.put("DATO_PROFILO", datiProfilo);
         * recuperaAttributiSelezionati(attributiTipoUnitaDocList.getTable(), listaAttributiSelezionati,
         * "DATO_SPEC_UNI_DOC"); recuperaAttributiSelezionati(attributiTipoDocList.getTable(),
         * listaAttributiSelezionati, "DATO_SPEC_DOC_PRINC");
         */
        String chiave = "DATO_PROFILO";
        Map<String, String> mappa = listaAttributiSelezionati.get(chiave);
        if (mappa != null && !mappa.keySet().isEmpty()) {
            Iterator<String> iterator = mappa.keySet().iterator();
            while (iterator.hasNext()) {
                String attrib = iterator.next();
                if (attrib.contains("(vers.")) {

                    attrib = attrib.substring(0, attrib.indexOf("(vers.")).trim();

                }
                result.add(attrib);
            }
        }
        chiave = "DATO_SPEC_UNI_DOC";
        mappa = listaAttributiSelezionati.get(chiave);
        if (mappa != null && !mappa.keySet().isEmpty()) {
            Iterator<String> iterator = mappa.keySet().iterator();
            while (iterator.hasNext()) {
                String attrib = iterator.next();
                if (attrib.contains("(vers.")) {

                    attrib = attrib.substring(0, attrib.indexOf("(vers.")).trim();

                }
                result.add(attrib);
            }
        }

        chiave = "DATO_SPEC_DOC_PRINC";
        mappa = listaAttributiSelezionati.get(chiave);
        if (mappa != null && !mappa.keySet().isEmpty()) {
            Iterator<String> iterator = mappa.keySet().iterator();
            while (iterator.hasNext()) {
                String attrib = iterator.next();
                if (attrib.contains("(vers.")) {

                    attrib = attrib.substring(0, attrib.indexOf("(vers.")).trim();

                }
                result.add(attrib);
            }
        }

        return result;
    }

    private void controllaSeDatiSpecPresentiInAssociazione(BaseTableInterface datiSpecList,
            List<String> listaDatiSpecDaAssociazione) {
        BaseTable appo = new BaseTable();
        Iterator iterator = datiSpecList.iterator();
        while (iterator.hasNext()) {
            BaseRow row = (BaseRow) iterator.next();
            String attrib = row.getString("nm_attrib_dati_spec");
            if (attrib.contains("(vers.")) {

                attrib = attrib.substring(0, attrib.indexOf("(vers.")).trim();

            }
            if (attrib != null & listaDatiSpecDaAssociazione.contains(attrib)) {
                appo.add(row);
            }

        }

        datiSpecList.removeAll();
        datiSpecList.load(appo);
        //

        /*
         * for (DecAttribDatiSpecRowBean datoSpec : datiSpecTB) { String nmAttribDatiSpec =
         * datoSpec.getNmAttribDatiSpec(); if(listaDatiSpecDaAssociazione.contains(nmAttribDatiSpec)){
         * appo.add(datoSpec); } }
         */
    }

    /**
     * Eseguo la "post" a mano delle liste Dati Specifici su Tipo Unita' Documentaria e Tipo Documento
     *
     * @param tipoEntita
     *            di tipo {@link TipoEntitaSacer}
     */
    private void recuperaListaDatiSpecificiDaRequest(Constants.TipoEntitaSacer tipoEntita) {
        BaseTableInterface<?> table = null;
        String tiTrasform = null;
        String flagSelezionato = null;
        String[] attribSelezionati = null;
        HttpServletRequest request = getRequest();
        String pg_ord_campo = "Pg_ord_campo";
        switch (tipoEntita) {
        case UNI_DOC:
            table = getForm().getAttributiTipoUnitaDocList().getTable();
            tiTrasform = getForm().getAttributiTipoUnitaDocList().getTi_trasform_campo_tipo_unita_doc().getName();
            flagSelezionato = getForm().getAttributiTipoUnitaDocList().getTipo_unita_selezionato().getName();
            pg_ord_campo = getForm().getAttributiTipoUnitaDocList().getPg_ord_campo_tipo_unita_doc().getName();
            break;

        case DOC:
            table = getForm().getAttributiTipoDocList().getTable();
            tiTrasform = getForm().getAttributiTipoDocList().getTi_trasform_campo_tipo_doc().getName();
            flagSelezionato = getForm().getAttributiTipoDocList().getTipo_doc_selezionato().getName();
            pg_ord_campo = getForm().getAttributiTipoDocList().getPg_ord_campo_tipo_doc().getName();
            break;
        }
        boolean isPgOrdCampoPresente = false;
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameter = parameterNames.nextElement();
            if (parameter.equalsIgnoreCase(pg_ord_campo)) {
                isPgOrdCampoPresente = true;
                break;
            }
        }
        String tiTrasformValue = null;
        String pg_ord_campoValue = null;
        if (table != null && !table.isEmpty()) {
            int tableSize = table.size();
            for (int j = 0; j < tableSize; j++) {
                table.getRow(j).setBigDecimal(flagSelezionato, BigDecimal.ZERO);
            }
        }
        attribSelezionati = request.getParameterValues(flagSelezionato);
        if (attribSelezionati != null) {
            for (int i = 0; i < attribSelezionati.length; i++) {
                int rigaSelezionata = Integer.parseInt(attribSelezionati[i]);
                BaseRowInterface row = table.getRow(rigaSelezionata);
                tiTrasformValue = request.getParameterValues(tiTrasform)[rigaSelezionata];
                row.setBigDecimal(flagSelezionato, BigDecimal.ONE);
                if (!StringUtils.isBlank(tiTrasformValue) || tiTrasformValue.equals("")) {
                    row.setString(tiTrasform, tiTrasformValue);
                }
                // Se è presente anche il "Numero d'ordine" (caso "Regole di individuazione file")
                if (isPgOrdCampoPresente) {
                    pg_ord_campoValue = request.getParameterValues(pg_ord_campo)[rigaSelezionata];
                    if (!StringUtils.isBlank(pg_ord_campoValue)) {
                        if (StringUtils.isNumeric(pg_ord_campoValue)) {
                            row.setString(pg_ord_campo.toLowerCase(), pg_ord_campoValue);
                        } else if (getMessageBox().isEmpty()) {
                            getMessageBox().addError(
                                    "Attenzione: uno dei campi selezionati ha un numero ordine formalmente non corretto");
                        }
                    } else if (getMessageBox().isEmpty()) {
                        getMessageBox().addError("Attenzione: numero d'ordine assente");
                    }
                    row.setString(pg_ord_campo, pg_ord_campoValue);
                }
            }
        }
    }

    @Override
    public void updateRegoleDiRappresentazioneList() throws EMFError {
        visualizzaRegolaDiRappresentazione(BaseElements.Status.update);
    }

    private void visualizzaRegolaDiRappresentazione(BaseElements.Status status) throws EMFError {
        StrutSerieForm form = getForm();
        StrutSerieForm.RegoleDiRappresentazioneList regoleRapprList = form.getRegoleDiRappresentazioneList();
        StrutSerieForm.RegoleRapprDetail regoleRapprDetail = form.getRegoleRapprDetail();
        DecOutSelUdRowBean decOutSelUdRowBean = (DecOutSelUdRowBean) regoleRapprList.getTable().getCurrentRow();
        regoleRapprDetail.copyFromBean(decOutSelUdRowBean);
        caricaComboRegoleRappresentazione();
        popolaStrutRif();
        regoleRapprDetail.setStatus(status);
        regoleRapprList.setStatus(status);
        if (BaseElements.Status.update.equals(status)) {
            form.getInserimentoWizard().reset();
            regoleRapprDetail.getTi_out().setDecodeMap(getMappaTipoDiRappresentazione(null));
            regoleRapprDetail.getDl_formato_out().setReadonly(false);
            regoleRapprDetail.getDl_formato_out().setEditMode();
            StrutSerieForm.DatiProfiloList datiProfiloList = getForm().getDatiProfiloList();
            datiProfiloList.getNm_campo().setReadonly(true);
            datiProfiloList.getPg_ord_campo_dato_profilo().setHidden(true);
            datiProfiloList.getFl_selezionato().setReadonly(false);
            datiProfiloList.getFl_selezionato().setEditMode();
            BigDecimal idOutSelUd = decOutSelUdRowBean.getIdOutSelUd();
            Set<CostantiDB.NomeCampo> listaTiCampo = getListaNomeCampoPerDecOutSelUd(idOutSelUd,
                    CostantiDB.TipoCampo.DATO_PROFILO.name());
            inizializzaListaDatiProfilo(listaTiCampo, datiProfiloList);
        }
        // Setto i campi relativi ad ambiente/ente/struttura
        initInputAmbienteEnteStruttura(regoleRapprDetail);
    }

    @Override
    public void updateRegoleRapprDetail() throws EMFError {
        visualizzaRegolaDiRappresentazione(BaseElements.Status.update);
    }

    @Override
    public void deleteRegoleAcquisizioneList() throws EMFError {
        dltRegolaAcquisizione(false);
    }

    private void ricaricaDatiSpecificiPerRegolaAcquisizione(BigDecimal idTipoSerie, boolean soloSelezionati)
            throws EMFError {
        // try {
        StrutSerieForm form = getForm();
        form.getAttributiTipoUnitaDocList().clear();
        form.getAttributiTipoDocList().clear();
        Map<String, DecCampoInpUdRowBean> listaDatiSpecSalvati = new HashMap<>();
        Set<BigDecimal> idTipiUnitaDoc = new HashSet<>();
        Set<String> idTipoDocCensiti = new TreeSet<>();
        /* Ricavo le associazioni registro/tipo ud */
        DecTipoSerieUdTableBean tipoSerieUdTable = (DecTipoSerieUdTableBean) form.getTipoSerieRegistriList().getTable();
        List<DecTipoSerieUdRowBean> listaAssociazioniRegTipoUd = getListaAssociazioniRegUnitConVersioniXSD(
                tipoSerieUdTable);
        if (listaAssociazioniRegTipoUd != null) {

            /*
             * Gestione dei dati specifici sul TIPO UNITA' DOCUMENTARIA Ricavo i diversi ID TIPO UNITA' DOCUMENTARIA
             */
            for (DecTipoSerieUdRowBean decTipoSerieUd : tipoSerieUdTable) {
                BigDecimal idTipoSerieUd = decTipoSerieUd.getIdTipoSerieUd();
                if (idTipoSerieUd != null) {
                    BigDecimal idTipoUnitaDoc = decTipoSerieUd.getIdTipoUnitaDoc();
                    idTipiUnitaDoc.add(idTipoUnitaDoc);
                }
            }

            BaseTableInterface<?> table = null;
            BaseTable appoTable = new BaseTable();
            DecCampoInpUdTableBean decCampoInpUdTableBeanPerTIpoSerie = null;
            Iterator iterator = null;

            /* GESTIONE DATI SPECIFICI SU TIPO UNITA' DOCUMENTARIA */
            if (idTipiUnitaDoc.size() == 1) {
                BigDecimal idTipoUnitaDoc = null;
                Iterator it = idTipiUnitaDoc.iterator();
                while (it.hasNext()) {
                    idTipoUnitaDoc = (BigDecimal) it.next();
                }
                DecAttribDatiSpecTableBean datiSpecTB = tipoSerieEjb.getDecAttribDatiSpecTableBean(idTipoUnitaDoc,
                        Constants.TipoEntitaSacer.UNI_DOC);
                BaseTableInterface datiSpecTipoUnitaDocList = listBean2TableBeanPerRappresentazione(datiSpecTB,
                        Constants.TipoEntitaSacer.UNI_DOC);

                decCampoInpUdTableBeanPerTIpoSerie = tipoSerieEjb.getDecCampoInpUdTableBeanPerDecTipoSerie(idTipoSerie,
                        CostantiDB.TipoCampo.DATO_SPEC_UNI_DOC.name());
                for (DecCampoInpUdRowBean row : decCampoInpUdTableBeanPerTIpoSerie) {
                    BigDecimal idAttribDatiSpec = row.getIdAttribDatiSpec();

                    DecAttribDatiSpecRowBean attributo = datiSpecEjb.getDecAttribDatiSpecRowBean(idAttribDatiSpec);
                    listaDatiSpecSalvati.put(attributo.getNmAttribDatiSpec(), row);
                }
                iterator = datiSpecTipoUnitaDocList.iterator();

                while (iterator.hasNext()) {
                    BaseRow row = (BaseRow) iterator.next();
                    row.setBigDecimal("id_tipo_unita_doc", idTipoUnitaDoc);
                    String attrib = row.getString("nm_attrib_dati_spec");
                    if (attrib.indexOf("(vers.") > 0) {
                        attrib = attrib.substring(0, attrib.indexOf("(vers.")).trim();
                    }
                    row.setString("key_campo", attrib);
                    Long idAttribDatiSpec = tipoSerieEjb.getIdDecAttribDatiSpecByName(attrib, null, idTipoUnitaDoc,
                            null);
                    if (idAttribDatiSpec != null) {
                        row.setBigDecimal("id_attrib_dati_spec", new BigDecimal(idAttribDatiSpec));
                    }
                    if (listaDatiSpecSalvati.keySet().contains(attrib)) {

                        row.setBigDecimal("tipo_unita_selezionato", BigDecimal.ONE);
                        DecCampoInpUdRowBean rowBean = listaDatiSpecSalvati.get(attrib);
                        String tiTrasformCampo = StringEscapeUtils.unescapeHtml4(rowBean.getTiTrasformCampo());
                        String name = form.getAttributiTipoUnitaDocList().getTi_trasform_campo_tipo_unita_doc()
                                .getName();
                        String pg_ord_campo = form.getAttributiTipoUnitaDocList().getPg_ord_campo_tipo_unita_doc()
                                .getName();
                        row.setString(name, tiTrasformCampo);
                        row.setString(pg_ord_campo, rowBean.getPgOrdCampo().toString());
                        if (soloSelezionati) {
                            appoTable.add(row);
                        }
                    }

                }
                table = form.getAttributiTipoUnitaDocList().getTable();
                getForm().getDatiSpecTipoUdSection().setHidden(false);
                getForm().getDatiSpecTipoUdSection().setLegend("Dati specifici sul tipo unità documentaria "
                        + ((DecTipoUnitaDocRowBean) tipoUnitaDocEjb.getDecTipoUnitaDocRowBean(idTipoUnitaDoc, null))
                                .getNmTipoUnitaDoc());
                if (soloSelezionati) {
                    form.getAttributiTipoUnitaDocList().setTable(appoTable);
                } else if (table == null || table.isEmpty()) {
                    form.getAttributiTipoUnitaDocList().setTable(datiSpecTipoUnitaDocList);
                }
                if (form.getAttributiTipoUnitaDocList().getTable() != null) {
                    form.getAttributiTipoUnitaDocList().getTable().setPageSize(300);
                }
            } /* Se ho più di un tipo unità documentaria (o nessuno) non mostro i dati specifici */ else {
                form.getAttributiTipoUnitaDocList().clear();
                getForm().getDatiSpecTipoUdSection().setHidden(true);
            }
            form.getAttributiTipoUnitaDocList().getPg_ord_campo_tipo_unita_doc().setHidden(false);

            /*
             * GESTIONE DATI SPECIFICI SU TIPO DOCUMENTO Per ogni elemento di DecTipoSerieUd (tabella delle associazioni
             * registro/tipoUd)
             */
            int numTipiDocPrinc = 0;
            for (DecTipoSerieUdRowBean decTipoSerieUd : tipoSerieUdTable) {
                BigDecimal idTipoSerieUd = decTipoSerieUd.getIdTipoSerieUd();
                if (idTipoSerieUd != null) {
                    /* Ricavo i TIPI_DOC_PRINC */
                    DecFiltroSelUdTableBean decFiltroSelUdTableBean = tipoSerieEjb
                            .getDecFiltroSelUdTableBean(idTipoSerieUd);
                    List<DecFiltroSelUdRowBean> listaFiltri = getListaFiltriPerTipo(decFiltroSelUdTableBean,
                            CostantiDB.TipoFiltroSerieUd.TIPO_DOC_PRINC.name());
                    numTipiDocPrinc = numTipiDocPrinc + listaFiltri.size();
                    BaseTableInterface datiSpecTipoDocList = null;
                    if (numTipiDocPrinc <= 1) {
                        BigDecimal idTipoDocPrinc = null;
                        for (DecFiltroSelUdRowBean row : listaFiltri) {
                            String tiFiltro = row.getTiFiltro();
                            if (CostantiDB.TipoFiltroSerieUd.TIPO_DOC_PRINC.name().equals(tiFiltro)) {
                                idTipoDocPrinc = row.getIdTipoDocPrinc();

                                if (!idTipoDocCensiti.contains(idTipoDocPrinc.toPlainString())) {
                                    idTipoDocCensiti.add(idTipoDocPrinc.toPlainString());
                                    DecAttribDatiSpecTableBean datiSpecTipoDocTB = tipoSerieEjb
                                            .getDecAttribDatiSpecTableBean(idTipoDocPrinc,
                                                    Constants.TipoEntitaSacer.DOC);
                                    table = null;
                                    if (datiSpecTipoDocList == null) {
                                        datiSpecTipoDocList = listBean2TableBeanPerRappresentazione(datiSpecTipoDocTB,
                                                Constants.TipoEntitaSacer.DOC);
                                    } else {
                                        table = listBean2TableBeanPerRappresentazione(datiSpecTipoDocTB,
                                                Constants.TipoEntitaSacer.DOC);
                                        Iterator itr = table.iterator();
                                        while (itr.hasNext()) {
                                            getForm().getDatiSpecTipoUdSection().setHidden(false);

                                            BaseRowInterface tableRow = (BaseRowInterface) itr.next();
                                            datiSpecTipoDocList.add(tableRow);
                                        }
                                    }
                                }
                            }
                        }
                        listaDatiSpecSalvati.clear();
                        appoTable.clear();
                        if (datiSpecTipoDocList != null && !datiSpecTipoDocList.isEmpty()) {

                            decCampoInpUdTableBeanPerTIpoSerie = tipoSerieEjb.getDecCampoInpUdTableBeanPerDecTipoSerie(
                                    idTipoSerie, CostantiDB.TipoCampo.DATO_SPEC_DOC_PRINC.name());
                            for (DecCampoInpUdRowBean row : decCampoInpUdTableBeanPerTIpoSerie) {
                                BigDecimal idAttribDatiSpec = row.getIdAttribDatiSpec();
                                DecAttribDatiSpecRowBean attributo = datiSpecEjb
                                        .getDecAttribDatiSpecRowBean(idAttribDatiSpec);
                                listaDatiSpecSalvati.put(attributo.getNmAttribDatiSpec(), row);
                            }
                            iterator = datiSpecTipoDocList.iterator();
                            while (iterator.hasNext()) {
                                BaseRow row = (BaseRow) iterator.next();
                                String attrib = row.getString("nm_attrib_dati_spec");
                                if (attrib.indexOf("(vers.") > 0) {
                                    attrib = attrib.substring(0, attrib.indexOf("(vers.")).trim();
                                }
                                row.setString("key_campo", attrib);
                                if (listaDatiSpecSalvati.keySet().contains(attrib)) {

                                    row.setBigDecimal("tipo_doc_selezionato", BigDecimal.ONE);
                                    DecCampoInpUdRowBean rowBean = listaDatiSpecSalvati.get(attrib);
                                    String name = form.getAttributiTipoDocList().getTi_trasform_campo_tipo_doc()
                                            .getName();
                                    String pg_ord_campo = form.getAttributiTipoDocList().getPg_ord_campo_tipo_doc()
                                            .getName();
                                    row.setString(name, rowBean.getTiTrasformCampo());
                                    row.setString(pg_ord_campo, rowBean.getPgOrdCampo().toString());
                                    if (soloSelezionati) {
                                        appoTable.add(row);
                                    }
                                }

                            }
                        }

                        table = form.getAttributiTipoDocList().getTable();
                        if (idTipoDocPrinc != null && datiSpecTipoDocList != null && !datiSpecTipoDocList.isEmpty()) {
                            getForm().getDatiSpecTipoDocSection().setHidden(false);
                            getForm().getDatiSpecTipoDocSection().setLegend("Dati specifici sul tipo documento "
                                    + ((DecTipoDocRowBean) tipoDocEjb.getDecTipoDocRowBean(idTipoDocPrinc, null))
                                            .getNmTipoDoc());
                        } else {
                            getForm().getDatiSpecTipoDocSection().setHidden(true);
                        }

                        if (soloSelezionati) {
                            form.getAttributiTipoDocList().setTable(appoTable);
                        } else if (table == null || table.isEmpty()) {
                            form.getAttributiTipoDocList().setTable(datiSpecTipoDocList);
                        }
                        if (form.getAttributiTipoDocList().getTable() != null) {
                            form.getAttributiTipoDocList().getTable().setPageSize(300);
                        }
                    } /* Non mostro i dati specifici per il Tipo Documento */ else {
                        form.getAttributiTipoDocList().clear();
                        getForm().getDatiSpecTipoDocSection().setHidden(true);
                    }
                    form.getAttributiTipoDocList().getPg_ord_campo_tipo_doc().setHidden(false);
                }
            }
        }
        // } catch (EMFError ex) {
        // logger.error(ex.getLocalizedMessage(), ex);
        // }
    }

    // /**
    // * Salva Regola di individuazione. Viene salvato: - il dettaglio della regola - la lista dei Dati Profilo - la
    // lista
    // * degli eventuali dati specifici relativi alla tipologia di unità documentaria - la lista degli eventuali dati
    // * specifici relativi al tipo documento principale
    // *
    // * @throws EMFError errore generico
    // */
    // private void salvaRegolaAcquisizione() throws EMFError {
    // StrutSerieForm form = getForm();
    // MessageBox msgBox = getMessageBox();
    // // Eseguo i vari post delle liste
    // form.getDatiProfiloList().post(getRequest());
    // form.getAttributiTipoUnitaDocList().post(getRequest());
    // form.getAttributiTipoDocList().post(getRequest());
    // // Recupero le varie liste
    // BaseTableInterface<?> table = form.getDatiProfiloList().getTable();
    // StrutSerieForm.AttributiTipoUnitaDocList attributiTipoUnitaDocList = form.getAttributiTipoUnitaDocList();
    // StrutSerieForm.AttributiTipoDocList attributiTipoDocList = form.getAttributiTipoDocList();
    // Map<String, Map<String, String>> listaAttributiSelezionati = new HashMap();
    // // Salvo i Dati Profilo selezionati con relativo numero d'ordine e li inserisco in "listaAttributiSelezionati"
    // Map<String, String> datiProfilo = recuperaListaDatiProfiloPerIndividuazione(table);
    // listaAttributiSelezionati.put(CostantiDB.TipoCampo.DATO_PROFILO.name(), datiProfilo);
    // // Controllo e salvo i Dati Specifici di tipo ud e tipo doc con relativo numero d'ordine
    // if (!getMessageBox().hasError()) {
    // recuperaListaDatiSpecificiDaRequest(Constants.TipoEntitaSacer.UNI_DOC);
    // }
    // if (!getMessageBox().hasError()) {
    // recuperaListaDatiSpecificiDaRequest(Constants.TipoEntitaSacer.DOC);
    // }
    // // Caccio in una mappa i valori già validati
    // if (!getMessageBox().hasError()) {
    // recuperaAttributiSelezionati(attributiTipoUnitaDocList.getTable(), listaAttributiSelezionati,
    // CostantiDB.TipoCampo.DATO_SPEC_UNI_DOC.name(), true);
    // recuperaAttributiSelezionati(attributiTipoDocList.getTable(), listaAttributiSelezionati,
    // CostantiDB.TipoCampo.DATO_SPEC_DOC_PRINC.name(), true);
    // }
    //
    // if (!getMessageBox().hasError()) {
    // DecTipoSerieRowBean decTipoSerieRowBean = (DecTipoSerieRowBean)
    // form.getTipologieSerieList().getTable().getCurrentRow();
    // BaseElements.Status status = form.getRegoleAcquisizioneList().getStatus();
    // if (ciSonoSelezionati(listaAttributiSelezionati)) {
    // // Controllo l'univocità dei numeri d'ordine
    // checkUnivocitaNumeriOrdine(listaAttributiSelezionati);
    // if (!msgBox.hasError()) {
    // try {
    // // Ricarico i dati della pagina e foruordo
    // BigDecimal idTipoSerie = ((DecTipoSerieRowBean)
    // form.getTipologieSerieList().getTable().getCurrentRow()).getIdTipoSerie();
    // eseguiModificaRegoleAcquisizioneTipoSerie(decTipoSerieRowBean, listaAttributiSelezionati, idTipoSerie, status);
    // forwardToPublisher(Application.Publisher.REGOLA_ACQUISIZIONE_DETAIL);
    // } catch (Exception ex) {
    // logger.error(ex.getLocalizedMessage(), ex);
    // msgBox.addError("Errore nel salvataggio delle regole di acquisizione file", ex);
    // visualizzaRegolaAcquisizione(decTipoSerieRowBean.getIdTipoSerie(), status);
    // reimpostaTabelleRegoleAcquisizione(listaAttributiSelezionati);
    // forwardToPublisher(Application.Publisher.REGOLA_ACQUISIZIONE_DETAIL);
    // }
    // } else {
    // forwardToPublisher(Application.Publisher.REGOLA_ACQUISIZIONE_DETAIL);
    // }
    // } else {
    // forwardToPublisher(Application.Publisher.REGOLA_ACQUISIZIONE_DETAIL);
    // visualizzaRegolaAcquisizione(decTipoSerieRowBean.getIdTipoSerie(), status);
    // msgBox.addError("E' necessario selezionare almeno un attributo");
    // }
    // } else {
    // forwardToPublisher(Application.Publisher.REGOLA_ACQUISIZIONE_DETAIL);
    // }
    // }
    private void salvaRegoleAcquisizione() throws EMFError {
        logger.debug("Eseguo i controlli sul salvataggio della regola di acquisizione del tipo serie");
        BigDecimal idTipoSerie = getForm().getTipoSerieDetail().getId_tipo_serie().parse();
        String[] pgOrdDatiProfilo = getRequest()
                .getParameterValues(getForm().getDatiProfiloList().getPg_ord_campo_dato_profilo().getName());
        String[] pgOrdDatiSpecUniDoc = getRequest().getParameterValues(
                getForm().getAttributiTipoUnitaDocList().getPg_ord_campo_tipo_unita_doc().getName());
        String[] tiTrasformDatiSpecUniDoc = getRequest().getParameterValues(
                getForm().getAttributiTipoUnitaDocList().getTi_trasform_campo_tipo_unita_doc().getName());
        String[] pgOrdDatiSpecDoc = getRequest()
                .getParameterValues(getForm().getAttributiTipoDocList().getPg_ord_campo_tipo_doc().getName());
        String[] tiTrasformDatiSpecDoc = getRequest()
                .getParameterValues(getForm().getAttributiTipoDocList().getTi_trasform_campo_tipo_doc().getName());

        try {
            DecCampoInpUdTableBean datiProfiloTable = (DecCampoInpUdTableBean) getForm().getDatiProfiloList()
                    .getTable();
            DecCampoInpUdTableBean datiSpecTipoUdTable = (DecCampoInpUdTableBean) getForm()
                    .getAttributiTipoUnitaDocList().getTable();
            DecCampoInpUdTableBean datiSpecTipoDocTable = (DecCampoInpUdTableBean) getForm().getAttributiTipoDocList()
                    .getTable();
            DecCampoInpUdTableBean datiCompilati = new DecCampoInpUdTableBean();
            datiCompilati.addSortingRule("pg_ord_campo", SortingRule.ASC);
            Set<String> pgOrds = new HashSet<>();
            if (!getMessageBox().hasError()) {
                checkRegoleAcquisizione(datiProfiloTable, pgOrdDatiProfilo, null, datiCompilati, pgOrds,
                        CostantiDB.TipoCampo.DATO_PROFILO.name());
                if (pgOrdDatiSpecUniDoc != null) {
                    checkRegoleAcquisizione(datiSpecTipoUdTable, pgOrdDatiSpecUniDoc, tiTrasformDatiSpecUniDoc,
                            datiCompilati, pgOrds, CostantiDB.TipoCampo.DATO_SPEC_UNI_DOC.name());
                }
                if (pgOrdDatiSpecDoc != null) {
                    checkRegoleAcquisizione(datiSpecTipoDocTable, pgOrdDatiSpecDoc, tiTrasformDatiSpecDoc,
                            datiCompilati, pgOrds, CostantiDB.TipoCampo.DATO_SPEC_DOC_PRINC.name());
                }
            }
            if (!getMessageBox().hasError()) {
                datiCompilati.sort();
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null,
                                null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                /* Se la lista è vuota mi metto in modalita' inserimento altrimenti in modifica */
                if (getForm().getRegoleAcquisizioneList().getTable().isEmpty()) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                } else {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                }
                tipoSerieEjb.saveDecCampoInpUd(param, idTipoSerie, datiCompilati);
                getMessageBox().addInfo("Regola salvata con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        if (getMessageBox().hasError()) {
            forwardToPublisher(getLastPublisher());
        } else {
            goBack();
        }
    }

    private void checkRegoleAcquisizione(DecCampoInpUdTableBean table, String[] pgOrdTable, String[] tiTrasformTable,
            DecCampoInpUdTableBean fillingTable, Set<String> pgOrds, String tiCampo) {
        // Scorro ogni record
        for (int index = 0; index < pgOrdTable.length; index++) {
            String pgOrdCampo = pgOrdTable[index];
            DecCampoInpUdRowBean row = table.getRow(index);
            String tiTrasform = null;
            if (tiTrasformTable != null) {
                tiTrasform = tiTrasformTable[index];
            }
            if (StringUtils.isNotBlank(pgOrdCampo)) {
                if (StringUtils.isNumeric(pgOrdCampo)) {
                    if (pgOrds.add(pgOrdCampo)) {
                        row.setPgOrdCampo(new BigDecimal(pgOrdCampo));
                        row.setTiTrasformCampo(tiTrasform);
                        row.setTiCampo(tiCampo);
                        fillingTable.add(row);
                    } else if (!getMessageBox().hasError()) {
                        getMessageBox()
                                .addError("Attenzione: uno o pi\u00F9 elementi hanno lo stesso numero d'ordine!");
                    }
                } else if (!getMessageBox().hasError()) {
                    getMessageBox()
                            .addError("Inserire un numero d'ordine valido per il campo '" + row.getNmCampo() + "'");
                }
            }
        }
    }

    private void editRegoleAcquisizione(Status status) throws EMFError, ParerUserError {
        getForm().getRegoleAcquisizioneList().setStatus(status);
        getForm().getRegoleAcquisizioneDetail().setStatus(status);
        BigDecimal idTipoSerie = getForm().getTipoSerieDetail().getId_tipo_serie().parse();
        if (idTipoSerie != null) {
            getForm().getDatiProfiloList().setTable(
                    tipoSerieEjb.getDecCampoInpUdTableBean(idTipoSerie, CostantiDB.TipoCampo.DATO_PROFILO.name()));
            getForm().getDatiProfiloList().getTable().setPageSize(300);
            // getForm().getDatiProfiloList().getNm_campo().setHidden(true);
            // getForm().getDatiProfiloList().getNm_campo().setViewMode();
            // getForm().getDatiProfiloList().getPg_ord_campo_dato_profilo().setHidden(false);
            // getForm().getDatiProfiloList().getPg_ord_campo_dato_profilo().setEditMode();
            // getForm().getDatiProfiloList().getFl_selezionato().setHidden(true);
            // getForm().getDatiProfiloList().getFl_selezionato().setViewMode();
            setVisibilityFiedsForUpdateDatiProfiloList(true, false, true, false);

            getForm().getDatiSpecTipoDocSection().setHidden(true);
            getForm().getDatiSpecTipoUdSection().setHidden(true);
            if (getForm().getTipoSerieRegistriList().getTable().size() == 1) {
                BigDecimal idTipoUnitaDoc = getForm().getTipoSerieRegistriList().getTable().getRow(0)
                        .getBigDecimal("id_tipo_unita_doc");
                if (idTipoUnitaDoc != null) {
                    getForm().getAttributiTipoUnitaDocList().getTi_trasform_campo_tipo_unita_doc()
                            .setDecodeMap(ComboGetter.getMappaTipoDiTrasformatoreInp());
                    getForm().getAttributiTipoUnitaDocList().setTable(tipoSerieEjb.getDecCampoInpUdDatiSpecTableBean(
                            idTipoSerie, idTipoUnitaDoc, CostantiDB.TipoCampo.DATO_SPEC_UNI_DOC.name()));
                    getForm().getAttributiTipoUnitaDocList().getTable().setPageSize(300);
                    // getForm().getAttributiTipoUnitaDocList().getTipo_unita_selezionato().setHidden(true);
                    // getForm().getAttributiTipoUnitaDocList().getTipo_unita_selezionato().setViewMode();
                    // getForm().getAttributiTipoUnitaDocList().getPg_ord_campo_tipo_unita_doc().setHidden(false);
                    // getForm().getAttributiTipoUnitaDocList().getPg_ord_campo_tipo_unita_doc().setEditMode();
                    // getForm().getAttributiTipoUnitaDocList().getTi_trasform_campo_tipo_unita_doc().setEditMode();
                    setVisibilityFiedsForUpdateDatiSpecTipoUdList(true, false, true, false);
                    getForm().getDatiSpecTipoUdSection().setHidden(false);
                    getForm().getDatiSpecTipoUdSection().setLegend("Dati specifici sul tipo unità documentaria "
                            + ((DecTipoUnitaDocRowBean) tipoUnitaDocEjb.getDecTipoUnitaDocRowBean(idTipoUnitaDoc, null))
                                    .getNmTipoUnitaDoc());
                }
            }

            DecFiltroSelUdTableBean filtriDocPrinc = tipoSerieEjb.getDecFiltroSelUdTableBean(idTipoSerie,
                    CostantiDB.TipoFiltroSerieUd.TIPO_DOC_PRINC);
            // if (filtriDocPrinc.size() == 1) {
            List<BigDecimal> idTipiDocPrincWithDS = tipiDocPrincWithDatiSpec(filtriDocPrinc);
            if (idTipiDocPrincWithDS.size() == 1) {
                // BigDecimal idTipoDocPrinc = filtriDocPrinc.getRow(0).getIdTipoDocPrinc();
                BigDecimal idTipoDocPrinc = idTipiDocPrincWithDS.get(0);
                getForm().getAttributiTipoDocList().getTi_trasform_campo_tipo_doc()
                        .setDecodeMap(ComboGetter.getMappaTipoDiTrasformatoreInp());
                getForm().getAttributiTipoDocList().setTable(tipoSerieEjb.getDecCampoInpUdDatiSpecTableBean(idTipoSerie,
                        idTipoDocPrinc, CostantiDB.TipoCampo.DATO_SPEC_DOC_PRINC.name()));
                getForm().getAttributiTipoDocList().getTable().setPageSize(300);
                // getForm().getAttributiTipoDocList().getTipo_doc_selezionato().setHidden(true);
                // getForm().getAttributiTipoDocList().getTipo_doc_selezionato().setViewMode();
                // getForm().getAttributiTipoDocList().getPg_ord_campo_tipo_doc().setHidden(false);
                // getForm().getAttributiTipoDocList().getPg_ord_campo_tipo_doc().setEditMode();
                // getForm().getAttributiTipoDocList().getTi_trasform_campo_tipo_doc().setEditMode();
                setVisibilityFiedsForUpdateDatiSpecTipoDocList(true, false, true, false);
                getForm().getDatiSpecTipoDocSection().setHidden(false);
                getForm().getDatiSpecTipoDocSection().setLegend("Dati specifici sul tipo documento "
                        + ((DecTipoDocRowBean) tipoDocEjb.getDecTipoDocRowBean(idTipoDocPrinc, null)).getNmTipoDoc());
            }
        }
    }

    /**
     * Imposto la visibilità dei campi della lista Dati Profilo in fase di inserimento/modifica
     *
     * @param hideFlSel
     *            true/false
     * @param hidePgOrdCampo
     *            true/false
     * @param hideNmCampo
     *            true/false
     * @param hideKeyCampo
     *            true/false
     */
    private void setVisibilityFiedsForUpdateDatiProfiloList(boolean hideFlSel, boolean hidePgOrdCampo,
            boolean hideNmCampo, boolean hideKeyCampo) {
        // Flag di selezione: se visibile, deve essere editabile
        getForm().getDatiProfiloList().getFl_selezionato().setHidden(hideFlSel);
        if (hideFlSel) {
            getForm().getDatiProfiloList().getFl_selezionato().setViewMode();
            getForm().getDatiProfiloList().getFl_selezionato().setReadonly(true);
        } else {
            getForm().getDatiProfiloList().getFl_selezionato().setEditMode();
            getForm().getDatiProfiloList().getFl_selezionato().setReadonly(false);
        }
        // Progressivo: se visibile, deve essere editabile
        getForm().getDatiProfiloList().getPg_ord_campo_dato_profilo().setHidden(hidePgOrdCampo);
        if (hidePgOrdCampo) {
            getForm().getDatiProfiloList().getPg_ord_campo_dato_profilo().setViewMode();
            getForm().getDatiProfiloList().getPg_ord_campo_dato_profilo().setReadonly(true);
        } else {
            getForm().getDatiProfiloList().getPg_ord_campo_dato_profilo().setEditMode();
            getForm().getDatiProfiloList().getPg_ord_campo_dato_profilo().setReadonly(false);
        }
        // Nome campo: di default readonly, va impostata solo la visibilità
        getForm().getDatiProfiloList().getNm_campo().setHidden(hideNmCampo);
        // Descrizione: di default readonly, va impostata solo la visibilità
        getForm().getDatiProfiloList().getKey_campo().setHidden(hideKeyCampo);
    }

    /**
     * Imposto la visibilità dei campi della lista Dati Specifici tipo ud in fase di inserimento/modifica
     *
     * @param hideFlSel
     *            true/false
     * @param hidePgOrdCampo
     *            true/false
     * @param hideNmCampo
     *            true/false
     * @param hideKeyCampo
     *            true/false
     */
    private void setVisibilityFiedsForUpdateDatiSpecTipoUdList(boolean hideFlSel, boolean hidePgOrdCampo,
            boolean hideNmCampo, boolean hideKeyCampo) {
        // Flag di selezione: se visibile, deve essere editabile
        getForm().getAttributiTipoUnitaDocList().getTipo_unita_selezionato().setHidden(hideFlSel);
        if (hideFlSel) {
            getForm().getAttributiTipoUnitaDocList().getTipo_unita_selezionato().setViewMode();
            getForm().getAttributiTipoUnitaDocList().getTipo_unita_selezionato().setReadonly(true);
        } else {
            getForm().getAttributiTipoUnitaDocList().getTipo_unita_selezionato().setEditMode();
            getForm().getAttributiTipoUnitaDocList().getTipo_unita_selezionato().setReadonly(false);
        }
        // Progressivo: se visibile, deve essere editabile
        getForm().getAttributiTipoUnitaDocList().getPg_ord_campo_tipo_unita_doc().setHidden(hidePgOrdCampo);
        if (hidePgOrdCampo) {
            getForm().getAttributiTipoUnitaDocList().getPg_ord_campo_tipo_unita_doc().setViewMode();
            getForm().getAttributiTipoUnitaDocList().getPg_ord_campo_tipo_unita_doc().setReadonly(true);
        } else {
            getForm().getAttributiTipoUnitaDocList().getPg_ord_campo_tipo_unita_doc().setEditMode();
            getForm().getAttributiTipoUnitaDocList().getPg_ord_campo_tipo_unita_doc().setReadonly(false);
        }

        // Nome campo: di default readonly, va impostata solo la visibilità
        getForm().getAttributiTipoUnitaDocList().getNm_campo().setHidden(hideNmCampo);

        // Descrizione: di default readonly, va impostata solo la visibilità
        getForm().getAttributiTipoUnitaDocList().getKey_campo().setHidden(hideKeyCampo);

        // Tipo di rappresentazione: va impostato solo lo status, sempre update
        getForm().getAttributiTipoUnitaDocList().getTi_trasform_campo_tipo_unita_doc().setEditMode();
    }

    /**
     * Imposto la visibilità dei campi della lista Dati Specifici tipo doc in fase di inserimento/modifica
     *
     * @param hideFlSel
     *            true/false
     * @param hidePgOrdCampo
     *            true/false
     * @param hideNmCampo
     *            true/false
     * @param hideKeyCampo
     *            true/false
     */
    private void setVisibilityFiedsForUpdateDatiSpecTipoDocList(boolean hideFlSel, boolean hidePgOrdCampo,
            boolean hideNmCampo, boolean hideKeyCampo) {
        // Flag di selezione: se visibile, deve essere editabile
        getForm().getAttributiTipoDocList().getTipo_doc_selezionato().setHidden(hideFlSel);
        if (hideFlSel) {
            getForm().getAttributiTipoDocList().getTipo_doc_selezionato().setViewMode();
            getForm().getAttributiTipoDocList().getTipo_doc_selezionato().setReadonly(true);
        } else {
            getForm().getAttributiTipoDocList().getTipo_doc_selezionato().setEditMode();
            getForm().getAttributiTipoDocList().getTipo_doc_selezionato().setReadonly(false);
        }
        // Progressivo: se visibile, deve essere editabile
        getForm().getAttributiTipoDocList().getPg_ord_campo_tipo_doc().setHidden(hidePgOrdCampo);
        if (hidePgOrdCampo) {
            getForm().getAttributiTipoDocList().getPg_ord_campo_tipo_doc().setViewMode();
            getForm().getAttributiTipoDocList().getPg_ord_campo_tipo_doc().setReadonly(true);
        } else {
            getForm().getAttributiTipoDocList().getPg_ord_campo_tipo_doc().setEditMode();
            getForm().getAttributiTipoDocList().getPg_ord_campo_tipo_doc().setReadonly(false);
        }

        // Nome campo: di default readonly, va impostata solo la visibilità
        getForm().getAttributiTipoDocList().getNm_campo().setHidden(hideNmCampo);

        // Descrizione: di default readonly, va impostata solo la visibilità
        getForm().getAttributiTipoDocList().getKey_campo().setHidden(hideKeyCampo);

        // Tipo di rappresentazione: va impostato solo lo status, sempre update
        getForm().getAttributiTipoDocList().getTi_trasform_campo_tipo_doc().setEditMode();
    }

    public void eseguiModificaRegoleAcquisizioneTipoSerie(DecTipoSerieRowBean decTipoSerieRowBean,
            Map<String, Map<String, String>> listaAttributiSelezionati, BigDecimal idTipoSerie, Status status)
            throws EMFError, ParerUserError {
        boolean isMod = BaseElements.Status.update.equals(status);
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                        CostantiDB.TipoAplVGetValAppart.APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        if (isMod) {
            param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
        } else {
            param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
        }
        tipoSerieEjb.insertDecCampiInpUdPerTipoSerie(param, decTipoSerieRowBean, listaAttributiSelezionati, isMod);
        getMessageBox()
                .addInfo("Regole di acquisizione file " + (isMod ? "modificate" : "inserite") + " con successo <br/>");
        getForm().getRegoleAcquisizioneList().setHideDeleteButton(false);
        getForm().getRegoleAcquisizioneList().setHideDetailButton(false);
        getForm().getRegoleAcquisizioneList().setHideUpdateButton(false);
        getForm().getRegoleAcquisizioneList().setHideInsertButton(false);
        getForm().getRegoleAcquisizioneList().setStatus(BaseElements.Status.view);
        getForm().getRegoleAcquisizioneList().setViewMode();
        getForm().getRegoleAcquisizioneDetail().setStatus(BaseElements.Status.view);
        getForm().getRegoleAcquisizioneDetail().setViewMode();
        /* Carica i dati specifici nelle liste relative e Tipo Unità Documentaria e Tipo Doc */
        ricaricaDatiSpecificiPerRegolaAcquisizione(idTipoSerie, true);
        visualizzaRegolaAcquisizione(idTipoSerie, BaseElements.Status.update);
    }

    public void checkUnivocitaNumeriOrdine(Map<String, Map<String, String>> listaAttributiSelezionati) {
        Iterator it = listaAttributiSelezionati.entrySet().iterator();
        Set<Integer> numeriOrdineDistinti = new HashSet<>();
        int contaElementi = 0;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();

            Map<String, String> elenco = (Map<String, String>) entry.getValue();
            Iterator it2 = elenco.entrySet().iterator();

            while (it2.hasNext()) {
                Map.Entry entry2 = (Map.Entry) it2.next();

                String[] pezzi = StringUtils.split((String) entry2.getValue(), "|");
                if (((String) entry.getKey()).equals("DATO_PROFILO")) {
                    numeriOrdineDistinti.add(Integer.parseInt(pezzi[0]));
                    contaElementi++;
                } else if (((String) entry.getKey()).equals("DATO_SPEC_DOC_PRINC")
                        || ((String) entry.getKey()).equals("DATO_SPEC_UNI_DOC")) {
                    numeriOrdineDistinti.add(Integer.parseInt(pezzi[1]));
                    contaElementi++;
                }
            }
        }
        if (numeriOrdineDistinti.size() != contaElementi) {
            getMessageBox().addError("Attenzione: uno o più elementi hanno lo stesso numero d'ordine!");
        }
    }

    /**
     * Recupera i record selezionati dalla lista "Dati Profilo" restituendo in un mappa chiave-valore il nome del campo
     * con il suo numero d'ordine A questo livello viene controllata la correttezza formale dei numeri d'ordine inseriti
     *
     * @param table
     *            campo di tipo {@link BaseTableInterface}
     * 
     * @return mappa chiave/valore
     */
    private Map<String, String> recuperaListaDatiProfiloPerIndividuazione(BaseTableInterface<?> table) throws EMFError {
        Map<String, String> datiProfilo = aggiornaListaDatiProfilo();
        return datiProfilo;
    }

    @Override
    public void updateTipoSerieRegistriList() throws EMFError {
        visualizzaTipoSerieRegistro();
        StrutSerieForm.RegistroDetail registroDetail = getForm().getRegistroDetail();
        registroDetail.setStatus(BaseElements.Status.update);
        StrutSerieForm.TipoSerieRegistriList tipoSerieRegistriList = getForm().getTipoSerieRegistriList();
        tipoSerieRegistriList.setStatus(BaseElements.Status.update);
        registroDetail.getId_registro_unita_doc().setViewMode();
        registroDetail.getId_tipo_unita_doc().setViewMode();
        registroDetail.getNi_anni_conserv().setViewMode();
        registroDetail.getFl_sel_unita_doc_annul().setEditMode();
    }

    @Override
    public void updateRegistroDetail() throws EMFError {
        updateTipoSerieRegistriList();
    }

    @Override
    public void updateRegoleAcquisizioneDetail() throws EMFError {
        try {
            editRegoleAcquisizione(Status.update);
            forwardToPublisher(getLastPublisher());
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
    }

    @Override
    public void deleteRegoleAcquisizioneDetail() throws EMFError {
        dltRegolaAcquisizione(true);
    }

    private void dltRegolaAcquisizione(boolean isFromDetailOrIsEmpty) {
        getMessageBox().clear();
        try {
            BigDecimal idRegolaAcquisizioneFile = ((DecCampoInpUdRowBean) getForm().getRegoleAcquisizioneList()
                    .getTable().getCurrentRow()).getIdCampoInpUd();
            BigDecimal idTipoSerie = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable()
                    .getCurrentRow()).getIdTipoSerie();
            // tipoSerieEjb.deleteDecCampoInpUd(idRegolaAcquisizioneFile, idTipoSerie);
            //
            // getMessageBox().addMessage(new Message(Message.MessageLevel.INF, "Regole di acquisizione file eliminate
            // con successo"));
            // if (isFromDetailOrIsEmpty) {
            // goBack();
            // } else {
            // visualizzaTipoSerie();
            // }

            if (serieEjb.checkSerieModificabili(idTipoSerie)) {
                Object[] attributi = new Object[3];
                attributi[0] = idRegolaAcquisizioneFile;
                attributi[1] = idTipoSerie;
                attributi[2] = isFromDetailOrIsEmpty;
                getSession().setAttribute("salvataggioAttributesDeleteRegoleAcquisizioneTipoSerie", attributi);
                getRequest().setAttribute("customBox", true);
                forwardToPublisher(getLastPublisher());
            } else {
                eseguiCancellazioneRegoleAcquisizioneTipoSerie(idRegolaAcquisizioneFile, idTipoSerie,
                        isFromDetailOrIsEmpty);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            getMessageBox().addError(e.getMessage());
        }
    }

    public void eseguiCancellazioneRegoleAcquisizioneTipoSerie(BigDecimal idRegolaAcquisizioneFile,
            BigDecimal idTipoSerie, boolean isFromDetailOrIsEmpty) throws ParerUserError, EMFError {
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                        CostantiDB.TipoAplVGetValAppart.APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setNomeAzione(
                SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getRegoleAcquisizioneList()));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        tipoSerieEjb.deleteDecCampoInpUd(param, idRegolaAcquisizioneFile, idTipoSerie);
        getMessageBox().addMessage(
                new Message(Message.MessageLevel.INF, "Regole di acquisizione file eliminate con successo"));
        if (isFromDetailOrIsEmpty) {
            goBack();
        } else {
            visualizzaTipoSerie();
            forwardToPublisher(Application.Publisher.TIPO_SERIE_DETAIL);
        }
    }

    /**
     * Effettua la "post" (salvataggio dati) dei valori presenti nelle liste Dati Profilo, Dati Specifici Tipo Unita'
     * Documentaria e Dati Specifici Tipo Documento
     *
     * @param listaAttributiSelezionati
     *            mappa chiave/valore
     */
    private void reimpostaTabelleRegoleAcquisizione(Map<String, Map<String, String>> listaAttributiSelezionati) {

        BaseTableInterface<?> table = null;

        Map<String, String> datiProfilo = listaAttributiSelezionati.get(CostantiDB.TipoCampo.DATO_PROFILO.name());
        Map<String, String> datiUniDoc = listaAttributiSelezionati.get(CostantiDB.TipoCampo.DATO_SPEC_UNI_DOC.name());
        Map<String, String> datiDocPrinc = listaAttributiSelezionati
                .get(CostantiDB.TipoCampo.DATO_SPEC_DOC_PRINC.name());

        // Reimposto i dati della lista Dati Profilo
        String[] tiTrasformValue = null;
        if (datiProfilo != null && !datiProfilo.isEmpty()) {
            table = getForm().getDatiProfiloList().getTable();
            for (BaseRowInterface row : table) {
                String nmCampo = row.getString("nm_campo");
                Set<String> dati = datiProfilo.keySet();
                if (dati.contains(nmCampo)) {
                    row.setBigDecimal("fl_selezionato", BigDecimal.ONE);
                } else {
                    row.setBigDecimal("fl_selezionato", BigDecimal.ZERO);
                }
            }

        }
        table = null;

        // Reimposto i dati della lista Dati Specifici Tipo Unita' Documentaria
        if (datiUniDoc != null && !datiUniDoc.isEmpty()) {

            table = getForm().getAttributiTipoUnitaDocList().getTable();
            for (BaseRowInterface row : table) {
                String nmCampo = row.getString("nm_attrib_dati_spec");
                if (nmCampo.indexOf("(vers.") > 0) {
                    nmCampo = nmCampo.substring(0, nmCampo.indexOf("(vers.")).trim();
                }

                Set<String> dati = datiUniDoc.keySet();
                if (dati.contains(nmCampo)) {
                    // row.setBigDecimal(getForm().getAttributiTipoUnitaDocList().getTipo_unita_selezionato().getName(),
                    // BigDecimal.ONE);
                    String valore = datiUniDoc.get(nmCampo);
                    tiTrasformValue = valore.split("[|]");
                    if (tiTrasformValue != null && tiTrasformValue.length > 2) {
                        row.setString(getForm().getAttributiTipoUnitaDocList().getTi_trasform_campo_tipo_unita_doc()
                                .getName(), tiTrasformValue[0]);
                        BigDecimal pgCampo = new BigDecimal(tiTrasformValue[1]);
                        row.setString(
                                getForm().getAttributiTipoUnitaDocList().getPg_ord_campo_tipo_unita_doc().getName(),
                                pgCampo.toString());
                    }
                }
            }

        }
        tiTrasformValue = null;
        table = null;
        if (datiDocPrinc != null && !datiDocPrinc.isEmpty()) {

            table = getForm().getAttributiTipoDocList().getTable();
            for (BaseRowInterface row : table) {
                String nmCampo = row.getString("nm_attrib_dati_spec");
                if (nmCampo.indexOf("(vers.") > 0) {
                    nmCampo = nmCampo.substring(0, nmCampo.indexOf("(vers.")).trim();
                }
                Set<String> dati = datiDocPrinc.keySet();
                if (dati.contains(nmCampo)) {
                    // row.setBigDecimal(getForm().getAttributiTipoDocList().getTipo_doc_selezionato().getName(),
                    // BigDecimal.ONE);
                    String valore = datiDocPrinc.get(nmCampo);
                    tiTrasformValue = valore.split("[|]");
                    if (tiTrasformValue != null && tiTrasformValue.length > 2) {
                        row.setString(getForm().getAttributiTipoDocList().getTi_trasform_campo_tipo_doc().getName(),
                                tiTrasformValue[0]);
                        BigDecimal pgCampo = new BigDecimal(tiTrasformValue[1]);
                        row.setString(getForm().getAttributiTipoDocList().getPg_ord_campo_tipo_doc().getName(),
                                pgCampo.toString());
                    }
                }
            }
        }
    }

    private boolean ciSonoSelezionati(Map<String, Map<String, String>> listaAttributiSelezionati) {
        boolean ciSono = false;
        Map<String, String> datoProf = listaAttributiSelezionati.get(CostantiDB.TipoCampo.DATO_PROFILO.name());
        Map<String, String> datoUni = listaAttributiSelezionati.get(CostantiDB.TipoCampo.DATO_SPEC_UNI_DOC.name());
        Map<String, String> datoDoc = listaAttributiSelezionati.get(CostantiDB.TipoCampo.DATO_SPEC_DOC_PRINC.name());

        ciSono = (datoProf != null && !datoProf.isEmpty()) || (datoUni != null && !datoUni.isEmpty())
                || (datoDoc != null && !datoDoc.isEmpty());
        return ciSono;
    }

    // private String seSoloUnoRitornalo(Map<String, Map<String, String>> listaAttributiSelezionati) {
    // int ciSono = 0;
    // String singolo = null;
    // Map<String, String> datoProf = listaAttributiSelezionati.get(CostantiDB.TipoCampo.DATO_PROFILO.name());
    // Map<String, String> datoUni = listaAttributiSelezionati.get(CostantiDB.TipoCampo.DATO_SPEC_UNI_DOC.name());
    // Map<String, String> datoDoc = listaAttributiSelezionati.get(CostantiDB.TipoCampo.DATO_SPEC_DOC_PRINC.name());
    // ciSono = (datoProf == null ? 0 : datoProf.size()) + (datoUni == null ? 0 : datoUni.size()) + (datoDoc == null ? 0
    // : datoDoc.size());
    // if (ciSono == 1) {
    // singolo = "<" + (datoProf != null && datoProf.size() > 0 ? (String) datoProf.keySet().toArray()[0] : "") +
    // (datoUni != null && datoUni.size() > 0 ? (String) datoUni.keySet().toArray()[0] : "") + (datoDoc != null &&
    // datoDoc.size() > 0 ? (String) datoDoc.keySet().toArray()[0] : "") + ">";
    //
    // }
    // return singolo;
    // }
    @Override
    public void deleteNoteTipoSerieDetail() throws EMFError {
        deleteNoteTipoSerieList();
    }

    @Override
    public void deleteNoteTipoSerieList() throws EMFError {
        getMessageBox().clear();
        String lastPublisher = getLastPublisher();
        boolean isFromDetailOrIsEmpty = Application.Publisher.NOTA_TIPO_SERIE_DETAIL.equals(lastPublisher)
                || "".equals(lastPublisher);
        DecNotaTipoSerieRowBean notaRowBean = (DecNotaTipoSerieRowBean) getForm().getNoteTipoSerieList().getTable()
                .getCurrentRow();
        // if (!isThisNotaMine()) {
        // getMessageBox().addError("L'utente non pu\u00f2 eliminare la nota perch\u00E8 non corrisponde all'autore");
        // } else {
        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.NOTA_TIPO_SERIE_DETAIL)) {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            } else {
                param.setNomeAzione(
                        SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getNoteTipoSerieList()));
            }
            tipoSerieEjb.deleteDecNoteTipoSerie(param, notaRowBean.getIdNotaTipoSerie());

            getMessageBox().addMessage(new Message(Message.MessageLevel.INF, "Nota eliminata con successo"));
            if (isFromDetailOrIsEmpty) {
                goBack();
            } else {
                DecTipoSerieRowBean tipoSerie = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable()
                        .getCurrentRow());
                caricaListeSerie(tipoSerie.getIdTipoSerie());
            }
        } catch (Exception ex) {
            getMessageBox().addError(ex.getMessage());
            if (!isFromDetailOrIsEmpty) {
                goBack();

            } else {
                forwardToPublisher(Application.Publisher.NOTA_TIPO_SERIE_DETAIL);
            }
        }
        // }
    }

    @Override
    public void updateNoteTipoSerieDetail() throws EMFError {
        updateNoteTipoSerieList();
    }

    @Override
    public void updateNoteTipoSerieList() throws EMFError {
        visualizzaDecNotaTipoSerie(BaseElements.Status.update);
    }

    private void visualizzaDecNotaTipoSerie(BaseElements.Status mode) throws EMFError {
        StrutSerieForm.NoteTipoSerieDetail form = getForm().getNoteTipoSerieDetail();
        BigDecimal idTipoSerie = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable().getCurrentRow())
                .getIdTipoSerie();
        DecTipoNotaSerieTableBean decTipoNotaSerieTableBean = tipoSerieEjb.getDecTipoNotaSerieTableBean(idTipoSerie);
        boolean isInsert = BaseElements.Status.insert.equals(mode);
        boolean isUpdate = BaseElements.Status.update.equals(mode);
        boolean isView = BaseElements.Status.view.equals(mode);
        BaseTableInterface<?> table = getForm().getNoteTipoSerieList().getTable();
        if (!isInsert && (table != null && !table.isEmpty())) {
            DecNotaTipoSerieRowBean row = (DecNotaTipoSerieRowBean) table.getCurrentRow();
            row.getIdNotaTipoSerie();
            form.copyFromBean(row);
            form.setViewMode();
        } else {
            form.clear();
            SimpleDateFormat sdf = new SimpleDateFormat(WebConstants.DATE_FORMAT_HOUR_MINUTE_TYPE);
            String oggi = sdf.format(new java.util.Date());
            form.getDt_nota_tipo_serie().setValue(oggi);
        }
        if (isInsert || isUpdate) {
            form.setEditMode();
        }
        if (isUpdate || isView) {
            form.getId_tipo_nota_serie().setViewMode();
            decTipoNotaSerieTableBean = tipoSerieEjb.getDecTipoNotaSerieTableBean(null);
        }
        DecodeMap mappaTipoNotaSerie = new DecodeMap();
        mappaTipoNotaSerie.populatedMap(decTipoNotaSerieTableBean, "id_tipo_nota_serie", "ds_tipo_nota_serie");
        form.getId_tipo_nota_serie().setDecodeMap(mappaTipoNotaSerie);
        form.setStatus(mode);
        getForm().getNoteTipoSerieList().setStatus(mode);

        // Setto i campi di ambiente/ente/struttura
        initInputAmbienteEnteStruttura(form);
        popolaStrutRif();
    }

    private void salvaNotaTipoSerie() throws EMFError {
        MessageBox msgBox = getMessageBox();
        msgBox.clear();
        StrutSerieForm.NoteTipoSerieDetail dettaglio = getForm().getNoteTipoSerieDetail();
        StrutSerieForm.NoteTipoSerieList noteTipoSerieList = getForm().getNoteTipoSerieList();
        dettaglio.post(getRequest());
        BaseElements.Status stato = dettaglio.getStatus();
        Timestamp data = dettaglio.getDt_nota_tipo_serie().parse();
        BigDecimal idTipoSerie = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable().getCurrentRow())
                .getIdTipoSerie();
        BigDecimal idTipoNotaSerie = dettaglio.getId_tipo_nota_serie().parse();

        if (idTipoNotaSerie == null) {
            msgBox.addError("Tipo nota obbligatorio<br/> ");
        }
        if (data == null) {
            msgBox.addError("Data Oblligatoria<br/>");
        }
        String testo = dettaglio.getDs_nota_tipo_serie().parse();
        if (StringUtils.isBlank(testo)) {
            msgBox.addError("Descrizione obbligatoria<br/>");
        } else if (testo.length() > 1024) {
            msgBox.addError("La descrizione della nota non pu\u00f2 essere pi\u00F9 lunga di 1024 caratteri<br/>");
        }

        if (msgBox.isEmpty()) {
            try {
                DecNotaTipoSerieRowBean row = new DecNotaTipoSerieRowBean();
                dettaglio.copyToBean(row);
                long idUtente = getUser().getIdUtente();
                row.setIdUserIam(new BigDecimal(idUtente));
                row.setIdTipoSerie(idTipoSerie);
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null,
                                null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                if (BaseElements.Status.insert.equals(stato)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                    tipoSerieEjb.insertDecNotaTipoSerie(param, row);
                    msgBox.addInfo("Elemento di descrizione del tipo di serie inserito con successo");
                    DecNotaTipoSerieTableBean tableBean = new DecNotaTipoSerieTableBean();
                    tableBean.add(row);
                    tableBean.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    tableBean.setCurrentRowIndex(0);
                    noteTipoSerieList.setTable(tableBean);
                } else if (BaseElements.Status.update.equals(stato)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                    BigDecimal idNotaTipoSerie = ((DecNotaTipoSerieRowBean) noteTipoSerieList.getTable()
                            .getCurrentRow()).getIdNotaTipoSerie();
                    Date dataUpd = tipoSerieEjb.updateDecNotaTipoSerie(param, idNotaTipoSerie, row);
                    msgBox.addInfo("Elemento di descrizione del tipo di serie modificato con successo");
                    SimpleDateFormat sdf = new SimpleDateFormat(WebConstants.DATE_FORMAT_HOUR_MINUTE_TYPE);
                    String dataFormattata = sdf.format(dataUpd);
                    dettaglio.getDt_nota_tipo_serie().setValue(dataFormattata);
                    caricaListeSerie(idTipoSerie);
                }
                dettaglio.setStatus(BaseElements.Status.view);
                noteTipoSerieList.setStatus(BaseElements.Status.view);
                dettaglio.setViewMode();
                msgBox.setViewMode(MessageBox.ViewMode.plain);
                forwardToPublisher(Application.Publisher.NOTA_TIPO_SERIE_DETAIL);
            } catch (EMFError ex) {
                logger.error(ex.getLocalizedMessage(), ex);
                msgBox.addError("Errore nel salvataggio dell'elemento di descrizione sulla tipologia di serie", ex);
                forwardToPublisher(Application.Publisher.NOTA_TIPO_SERIE_DETAIL);

            }
            /*
             * tipoSerieDetail.setViewMode(); tipoSerieDetail.setStatus(BaseElements.Status.view);
             * caricaListeSerie(rowBean.getIdTipoSerie());
             * getForm().getTipologieSerieList().setStatus(BaseElements.Status.view);
             * msgBox.setViewMode(MessageBox.ViewMode.plain);
             * forwardToPublisher(Application.Publisher.TIPO_SERIE_DETAIL);
             * 
             */

        } else {
            forwardToPublisher(Application.Publisher.NOTA_TIPO_SERIE_DETAIL);

        }

    }

    private boolean isThisNotaMine() {
        boolean result = false;
        DecNotaTipoSerieRowBean notaTipoSerieRow = (DecNotaTipoSerieRowBean) getForm().getNoteTipoSerieList().getTable()
                .getCurrentRow();
        BigDecimal idUserIam = notaTipoSerieRow.getIdUserIam();
        long idUtente = getUser().getIdUtente();
        result = idUtente == idUserIam.longValue();
        return result;
    }

    private List<DecFiltroSelUdRowBean> getListaFiltriPerTipo(DecFiltroSelUdTableBean table, String filtro) {
        List<DecFiltroSelUdRowBean> result = new ArrayList<>();
        for (int i = 0; i < table.size(); i++) {
            DecFiltroSelUdRowBean row = table.getRow(i);
            String tiFiltro = row.getTiFiltro();

            if (filtro.equals(tiFiltro)) {
                boolean versioniXsdPerTipoEntita = tipoSerieEjb.getVersioniXsdPerTipoEntita(row.getIdTipoDocPrinc(),
                        Constants.TipoEntitaSacer.DOC);
                if (versioniXsdPerTipoEntita) {
                    result.add(row);
                }
            }
        }

        return result;
    }

    private List<BigDecimal> tipiDocPrincWithDatiSpec(DecFiltroSelUdTableBean filtroSelUdTableBean) {
        List<BigDecimal> idTipoDocPrincWithDS = new ArrayList();
        for (DecFiltroSelUdRowBean filtroSelUdRowBean : filtroSelUdTableBean) {
            boolean versioniXsdPerTipoEntita = tipoSerieEjb
                    .getVersioniXsdPerTipoEntita(filtroSelUdRowBean.getIdTipoDocPrinc(), Constants.TipoEntitaSacer.DOC);
            if (versioniXsdPerTipoEntita) {
                idTipoDocPrincWithDS.add(filtroSelUdRowBean.getIdTipoDocPrinc());
            }
        }
        return idTipoDocPrincWithDS;
    }

    private List<DecTipoSerieUdRowBean> getListaAssociazioniRegUnitConVersioniXSD(
            DecTipoSerieUdTableBean tipoSerieUdTable) {
        List<DecTipoSerieUdRowBean> result = new ArrayList<>();

        for (DecTipoSerieUdRowBean row : tipoSerieUdTable) {
            BigDecimal idTipoUnitaDoc = row.getIdTipoUnitaDoc();
            boolean versioniXsdPerTipoEntita = tipoSerieEjb.getVersioniXsdPerTipoEntita(idTipoUnitaDoc,
                    Constants.TipoEntitaSacer.UNI_DOC);
            if (versioniXsdPerTipoEntita) {
                result.add(row);
            }
        }
        return result;
    }

    /**
     * Elimina un record dalla tabella DecFiltroSelUdAttb relativa alla lista AssociazioniDatiSpecList contenente i
     * filtri dati specifici presenti nel "Dettaglio Associazione registro - tipologia di Unità documentarie"
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteAssociazioneDatiSpecList() throws EMFError {
        // Pulisco la messageBox
        getMessageBox().clear();
        DecFiltroSelUdAttbRowBean filtro = ((DecFiltroSelUdAttbTableBean) getForm().getAssociazioneDatiSpecList()
                .getTable()).getCurrentRow();
        BigDecimal idTipoSerie = ((DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable().getCurrentRow())
                .getIdTipoSerie();
        try {
            if (serieEjb.checkSerieModificabili(idTipoSerie)) {
                Object[] attributi = new Object[2];
                attributi[0] = filtro;
                attributi[1] = idTipoSerie;
                getSession().setAttribute("salvataggioAttributesDeleteDatiSpecificiTipoSerie", attributi);
                getRequest().setAttribute("customBox", true);
            } else {
                eseguiCancellazioneDatiSpecificiTipoSerie(filtro, idTipoSerie);
            }
            forwardToPublisher(getLastPublisher());
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            getMessageBox().addError(e.getMessage());
        }
    }

    public void eseguiCancellazioneDatiSpecificiTipoSerie(DecFiltroSelUdAttbRowBean filtro, BigDecimal idTipoSerie)
            throws ParerUserError {
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                        CostantiDB.TipoAplVGetValAppart.APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setNomeAzione(
                SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getAssociazioneDatiSpecList()));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        tipoSerieEjb.deleteDecFiltroSelUdAttb(param, filtro.getIdFiltroSelUdAttb(), idTipoSerie);
        getMessageBox().addMessage(new Message(MessageLevel.INF, "Filtro su dati specifici eliminato con successo"));
        DecFiltroSelUdAttbTableBean filtriDatiSpec = tipoSerieEjb.getDecFiltroSelUdAttbList(filtro.getIdTipoSerieUd());
        getForm().getAssociazioneDatiSpecList().setTable(filtriDatiSpec);
        getForm().getAssociazioneDatiSpecList().getTable().first();
        getForm().getAssociazioneDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
    }

    @Secure(action = "Menu.Serie.TipiSerie")
    public void loadListaTipiSerie() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Serie.TipiSerie");

        // Inizializzo le combo
        try {
            ActionUtils utile = new ActionUtils();
            utile.initGenericComboAmbienteEnteStruttura(getForm().getFiltriTipologieSerie(), getUser().getIdUtente(),
                    getUser().getIdOrganizzazioneFoglia(), Boolean.TRUE);
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore inatteso nel recupero delle strutture abilitate");
        }
        getForm().getFiltriTipologieSerie().getId_modello_tipo_serie().setDecodeMap(DecodeMap.Factory.newInstance(
                modelliEjb.getDecModelloTipoSerieAllAbilitatiTableBean(getUser().getIdOrganizzazioneFoglia(), true),
                "id_modello_tipo_serie", "nm_modello_tipo_serie"));
        getForm().getFiltriTipologieSerie().getIsAttivo().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriTipologieSerie().setEditMode();

        // Lista tipi Serie
        getForm().getTipologieSerieList().setTable(tipoSerieEjb.getDecTipoSerieTableBean(
                getUser().getIdOrganizzazioneFoglia(), getForm().getTipologieSerieList().isFilterValidRecords()));
        getForm().getTipologieSerieList().getTable().setPageSize(10);
        getForm().getTipologieSerieList().getTable().first();
        getForm().getTipologieSerieList().setUserOperations(true, true, true, true);

        // Imposto come visibile il bottone di ricerca tipologia serie e disabilito la clessidra (per IE)
        getForm().getFiltriTipologieSerie().getRicercaTipologieSerieButton().setEditMode();
        getForm().getFiltriTipologieSerie().getRicercaTipologieSerieButton().setDisableHourGlass(true);

        forwardToPublisher(Application.Publisher.GESTIONE_TIPI_SERIE);
    }

    @Override
    public void filterInactiveRecordsTipologieSerieList() throws EMFError {
        int rowIndex = 0;
        int pageSize = WebConstants.DEFAULT_PAGE_SIZE;
        if (getForm().getTipologieSerieList().getTable() != null) {
            rowIndex = getForm().getTipologieSerieList().getTable().getCurrentRowIndex();
            pageSize = getForm().getTipologieSerieList().getTable().getPageSize();
        }

        // Lista tipi Serie
        getForm().getTipologieSerieList().setTable(tipoSerieEjb.getDecTipoSerieTableBean(
                getUser().getIdOrganizzazioneFoglia(), getForm().getTipologieSerieList().isFilterValidRecords()));
        getForm().getTipologieSerieList().getTable().setCurrentRowIndex(rowIndex);
        getForm().getTipologieSerieList().getTable().setPageSize(pageSize);
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public JSONObject triggerTipoSerieDetailConserv_unlimitedOnTrigger() throws EMFError {
        getForm().getTipoSerieDetail().post(getRequest());
        return ActionUtils.getConservUnlimitedTrigger(getForm().getTipoSerieDetail());
    }

    // /**
    // * Inizializza i filtri Ambiente/Ente/Struttura di Gestione Tipi Serie Unità
    // * Documentaria in base alla struttura con la quale l'utente è loggato
    // *
    // * @throws EMFError errore generico
    // */
    // private void initComboAmbienteEnteStruttura(Fields field, BigDecimal idStrut) throws EMFError {
    // // Azzero i filtri
    // ((ComboBox) field.getComponent("id_ambiente")).reset();
    // ((ComboBox) field.getComponent("id_ente")).reset();
    // ((ComboBox) field.getComponent("id_strut")).reset();
    // // Ricavo id struttura, ente ed ambiente attuali, controllando che la struttura
    // // ricavata dal nodo foglia esista (verifico che non sia stata cancellata nel frattempo)
    // BigDecimal idStruttura = idStrut == null ?
    // struttureEjb.existsStruttura(getUser().getIdOrganizzazioneFoglia().longValue()) ?
    // getUser().getIdOrganizzazioneFoglia() : null : idStrut;
    // BigDecimal idEnte = idStruttura != null ? monitoraggioHelper.getIdEnte(idStruttura) : null;
    // BigDecimal idAmbiente = idEnte != null ? monitoraggioHelper.getIdAmbiente(idEnte) : null;
    //
    // // Inizializzo le combo settando la struttura corrente
    // OrgAmbienteTableBean tmpTableBeanAmbiente = new OrgAmbienteTableBean();
    // OrgEnteTableBean tmpTableBeanEnte = new OrgEnteTableBean();
    // OrgStrutTableBean tmpTableBeanStruttura = new OrgStrutTableBean();
    //
    // try {
    // // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
    // tmpTableBeanAmbiente = comboHelper.getAmbienteFromAbil(getUser().getIdUtente());
    // DecodeMap mappaAmbiente = new DecodeMap();
    // mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
    // ((ComboBox) field.getComponent("id_ambiente")).setDecodeMap(mappaAmbiente);
    // if (idAmbiente != null) {
    // ((ComboBox) field.getComponent("id_ambiente")).setValue(idAmbiente.toString());
    // }
    //
    // // Ricavo i valori della combo ENTE
    // DecodeMap mappaEnte = new DecodeMap();
    // ((ComboBox) field.getComponent("id_ente")).setDecodeMap(mappaEnte);
    // if (idEnte != null) {
    // tmpTableBeanEnte = comboHelper.getEnteNoTemplateFromAmbiente(getUser().getIdUtente(), idAmbiente.longValue());
    // mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
    // ((ComboBox) field.getComponent("id_ente")).setDecodeMap(mappaEnte);
    // ((ComboBox) field.getComponent("id_ente")).setValue(idEnte.toString());
    // }
    //
    // // Ricavo i valori della combo STRUTTURA
    // DecodeMap mappaStrut = new DecodeMap();
    // ((ComboBox) field.getComponent("id_strut")).setDecodeMap(mappaStrut);
    // if (idStruttura != null) {
    // tmpTableBeanStruttura = comboHelper.getStrutFromEnte(getUser().getIdUtente(), idEnte.longValue());
    // mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
    // ((ComboBox) field.getComponent("id_strut")).setDecodeMap(mappaStrut);
    // ((ComboBox) field.getComponent("id_strut")).setValue(idStruttura.toString());
    // }
    // } catch (Exception ex) {
    // logger.error("Errore in ricerca ambiente", ex);
    // }
    // }
    @Override
    public JSONObject triggerFiltriTipologieSerieId_ambienteOnTrigger() throws EMFError {
        getForm().getFiltriTipologieSerie().post(getRequest());
        ActionUtils utile = new ActionUtils();
        utile.triggerAmbienteGenerico(getForm().getFiltriTipologieSerie(), getUser().getIdUtente(), Boolean.TRUE);
        BigDecimal idAmbiente = getForm().getFiltriTipologieSerie().getId_ambiente().parse();
        if (idAmbiente != null) {
            getForm().getFiltriTipologieSerie().getId_modello_tipo_serie()
                    .setDecodeMap(DecodeMap.Factory.newInstance(
                            modelliEjb.getDecModelloTipoSerieAbilitatiAmbienteTableBean(idAmbiente, true),
                            "id_modello_tipo_serie", "nm_modello_tipo_serie"));
        } else {
            getForm().getFiltriTipologieSerie().getId_modello_tipo_serie().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriTipologieSerie().asJSON();
    }

    @Override
    public JSONObject triggerFiltriTipologieSerieId_enteOnTrigger() throws EMFError {
        getForm().getFiltriTipologieSerie().post(getRequest());
        ActionUtils utile = new ActionUtils();
        utile.triggerEnteGenerico(getForm().getFiltriTipologieSerie(), getUser().getIdUtente(), Boolean.TRUE);
        return getForm().getFiltriTipologieSerie().asJSON();
    }

    @Override
    public JSONObject triggerFiltriTipologieSerieId_strutOnTrigger() throws EMFError {
        getForm().getFiltriTipologieSerie().post(getRequest());
        BigDecimal idStrut = getForm().getFiltriTipologieSerie().getId_strut().parse();
        if (idStrut != null) {
            getForm().getFiltriTipologieSerie().getId_modello_tipo_serie()
                    .setDecodeMap(DecodeMap.Factory.newInstance(
                            modelliEjb.getDecModelloTipoSerieAllAbilitatiTableBean(idStrut, true),
                            "id_modello_tipo_serie", "nm_modello_tipo_serie"));
        } else {
            getForm().getFiltriTipologieSerie().getId_modello_tipo_serie().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriTipologieSerie().asJSON();
    }

    @Override
    public void ricercaTipologieSerieButton() throws EMFError {
        getForm().getFiltriTipologieSerie().post(getRequest());
        BigDecimal idAmbiente = getForm().getFiltriTipologieSerie().getId_ambiente().parse();
        BigDecimal idEnte = getForm().getFiltriTipologieSerie().getId_ente().parse();
        BigDecimal idStrut = getForm().getFiltriTipologieSerie().getId_strut().parse();
        String isAttivo = getForm().getFiltriTipologieSerie().getIsAttivo().parse();
        String tipiSerieNoGenModello = getForm().getFiltriTipologieSerie().getTipi_serie_no_gen_modello().parse();
        BigDecimal idModelloTipoSerie = getForm().getFiltriTipologieSerie().getId_modello_tipo_serie().parse();

        // Lista tipi Serie
        int pageSize = getForm().getTipologieSerieList().getTable().getPageSize();
        getForm().getTipologieSerieList().setTable(tipoSerieEjb.getDecTipoSerieTableBean(getUser().getIdUtente(),
                idAmbiente, idEnte, idStrut, isAttivo, tipiSerieNoGenModello, idModelloTipoSerie));
        getForm().getTipologieSerieList().getTable().setPageSize(pageSize);
        getForm().getTipologieSerieList().getTable().first();
        getForm().getTipologieSerieList().getTable()
                .addSortingRule(DecTipoSerieRowBean.TABLE_DESCRIPTOR.COL_NM_TIPO_SERIE, SortingRule.ASC);
        getForm().getTipologieSerieList().getTable().sort();

        forwardToPublisher(Application.Publisher.GESTIONE_TIPI_SERIE);
    }

    @Override
    public JSONObject triggerTipoSerieDetailId_ambienteOnTrigger() throws EMFError {
        getForm().getTipoSerieDetail().post(getRequest());
        ActionUtils utile;
        try {
            utile = new ActionUtils();
            utile.triggerAmbienteGenerico(getForm().getTipoSerieDetail(), getUser().getIdUtente(), Boolean.TRUE);
        } catch (Exception ex) {
            // LoggerFactory.getLogger(StrutSerieAction.class.getName()).log(Level.SEVERE, null, ex);
            LoggerFactory.getLogger(StrutSerieAction.class.getName()).error("Eccezione", ex);
        }

        if (getForm().getTipoSerieDetail().getId_strut().parse() != null) {
            DecTipoSerieTableBean seriepadreTB = tipoSerieEjb
                    .getDecTipoSeriePadrePerStrutturaTableBean(getForm().getTipoSerieDetail().getId_strut().parse());
            DecodeMap mappaTipoSeriePadre = DecodeMap.Factory.newInstance(seriepadreTB, "id_tipo_serie",
                    "nm_tipo_serie");
            getForm().getTipoSerieDetail().getId_tipo_serie_padre().setDecodeMap(mappaTipoSeriePadre);
        } else {
            getForm().getTipoSerieDetail().getId_tipo_serie_padre().setDecodeMap(new DecodeMap());
        }

        return getForm().getTipoSerieDetail().asJSON();
    }

    @Override
    public JSONObject triggerTipoSerieDetailId_enteOnTrigger() throws EMFError {
        getForm().getTipoSerieDetail().post(getRequest());
        ActionUtils utile = new ActionUtils();
        utile.triggerEnteGenerico(getForm().getTipoSerieDetail(), getUser().getIdUtente(), Boolean.TRUE);
        if (getForm().getTipoSerieDetail().getId_strut().parse() != null) {
            DecTipoSerieTableBean seriepadreTB = tipoSerieEjb
                    .getDecTipoSeriePadrePerStrutturaTableBean(getForm().getTipoSerieDetail().getId_strut().parse());
            DecodeMap mappaTipoSeriePadre = DecodeMap.Factory.newInstance(seriepadreTB, "id_tipo_serie",
                    "nm_tipo_serie");
            getForm().getTipoSerieDetail().getId_tipo_serie_padre().setDecodeMap(mappaTipoSeriePadre);
        } else {
            getForm().getTipoSerieDetail().getId_tipo_serie_padre().setDecodeMap(new DecodeMap());
        }

        return getForm().getTipoSerieDetail().asJSON();
    }

    @Override
    public JSONObject triggerTipoSerieDetailId_strutOnTrigger() throws EMFError {
        getForm().getTipoSerieDetail().post(getRequest());
        BigDecimal idStrut = null;
        if (getForm().getTipoSerieDetail().getId_strut() != null) {
            if (getForm().getTipoSerieDetail().getId_strut().parse() != null) {
                idStrut = getForm().getTipoSerieDetail().getId_strut().parse();
            }
        }

        if (idStrut != null) {
            DecTipoSerieTableBean seriepadreTB = tipoSerieEjb.getDecTipoSeriePadrePerStrutturaTableBean(idStrut);
            DecodeMap mappaTipoSeriePadre = DecodeMap.Factory.newInstance(seriepadreTB, "id_tipo_serie",
                    "nm_tipo_serie");
            getForm().getTipoSerieDetail().getId_tipo_serie_padre().setDecodeMap(mappaTipoSeriePadre);
        }

        return getForm().getTipoSerieDetail().asJSON();
    }

    /**
     * Metodo di utilità per settare i campi (di tipo input) di ambiente, ente e struttura in base a quanto ricavato dal
     * dettaglio tipologia serie
     *
     * @param field
     *            campo di tipo {@link Fields}
     * 
     * @throws EMFError
     *             errore generico
     */
    private void initInputAmbienteEnteStruttura(Fields field) throws EMFError {
        ((Input) field.getComponent("nm_ambiente"))
                .setValue(getForm().getTipoSerieDetail().getId_ambiente().getDecodedValue());
        ((Input) field.getComponent("nm_ambiente")).setViewMode();
        ((Input) field.getComponent("nm_ente")).setValue(getForm().getTipoSerieDetail().getId_ente().getDecodedValue());
        ((Input) field.getComponent("nm_ente")).setViewMode();
        ((Input) field.getComponent("nm_strut"))
                .setValue(getForm().getTipoSerieDetail().getId_strut().getDecodedValue());
        ((Input) field.getComponent("nm_strut")).setViewMode();
    }

    @Override
    public void logEventi() throws EMFError {
        GestioneLogEventiForm form = new GestioneLogEventiForm();
        form.getOggettoDetail().getNmApp().setValue(paramApplicHelper.getApplicationName().getDsValoreParamApplic());
        form.getOggettoDetail().getNm_tipo_oggetto().setValue(SacerLogConstants.TIPO_OGGETTO_TIPO_SERIE);
        DecTipoSerieRowBean rb = (DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable().getCurrentRow();
        form.getOggettoDetail().getIdOggetto().setValue(rb.getIdTipoSerie().toPlainString());
        redirectToAction(it.eng.parer.sacerlog.slite.gen.Application.Actions.GESTIONE_LOG_EVENTI,
                "?operation=inizializzaLogEventi", form);
    }

    @Override
    protected void postLoad() {
        super.postLoad();
        Object ogg = getForm();
        if (ogg instanceof StrutSerieForm) {
            StrutSerieForm form = (StrutSerieForm) ogg;
            if (form.getTipologieSerieList().getStatus().equals(Status.view)) {
                form.getTipoSerieDetail().getLogEventi().setEditMode();
            } else {
                form.getTipoSerieDetail().getLogEventi().setViewMode();
            }
        }
    }

}
