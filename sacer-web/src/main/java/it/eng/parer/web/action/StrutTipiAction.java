package it.eng.parer.web.action;

import static it.eng.parer.serie.ejb.SerieEjb.CD_SERIE_PATTERN;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneDatiSpecifici.ejb.DatiSpecificiEjb;
import it.eng.parer.amministrazioneStrutture.gestioneModelliXsdUd.ejb.ModelliXsdUdEjb;
import it.eng.parer.amministrazioneStrutture.gestioneRegistro.ejb.RegistroEjb;
import it.eng.parer.amministrazioneStrutture.gestioneSottoStrutture.ejb.SottoStruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoDoc.ejb.TipoDocumentoEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoStrutturaDoc.ejb.TipoStrutturaDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb.TipoUnitaDocEjb;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.exception.ParerWarningException;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.slite.gen.form.GestioneLogEventiForm;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.sacerlog.util.web.SpagoliteLogUtil;
import it.eng.parer.serie.ejb.ModelliSerieEjb;
import it.eng.parer.serie.ejb.TipoSerieEjb;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.StrutTipiAbstractAction;
import it.eng.parer.slite.gen.form.CriteriRaggruppamentoForm;
import it.eng.parer.slite.gen.form.StrutDatiSpecForm;
import it.eng.parer.slite.gen.form.StrutTipiForm;
import it.eng.parer.slite.gen.form.StrutTipiForm.RegistroCreazioneCriterio;
import it.eng.parer.slite.gen.form.StrutTipiForm.RegistroUnitaDoc;
import it.eng.parer.slite.gen.form.StrutTipiForm.TipoDoc;
import it.eng.parer.slite.gen.form.StrutTipiForm.TipoDocAmmesso;
import it.eng.parer.slite.gen.form.StrutTipiForm.TipoDocCreazioneCriterio;
import it.eng.parer.slite.gen.form.StrutTipiForm.TipoStrutUnitaDoc;
import it.eng.parer.slite.gen.form.StrutTipiForm.TipoUnitaDoc;
import it.eng.parer.slite.gen.form.StrutTipiForm.TipoUnitaDocAmmesso;
import it.eng.parer.slite.gen.form.StrutTipiForm.TipoUnitaDocCreazioneCriterio;
import it.eng.parer.slite.gen.form.StrutTipoStrutForm;
import it.eng.parer.slite.gen.form.StruttureForm;
import it.eng.parer.slite.gen.tablebean.AplParamApplicRowBean;
import it.eng.parer.slite.gen.tablebean.AplParamApplicTableBean;
import it.eng.parer.slite.gen.tablebean.AplSistemaVersanteTableBean;
import it.eng.parer.slite.gen.tablebean.DecAaRegistroUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecAaRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecCategTipoUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecErrAaRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecModelloTipoSerieTableBean;
import it.eng.parer.slite.gen.tablebean.DecModelloXsdUdRowBean;
import it.eng.parer.slite.gen.tablebean.DecParteNumeroRegistroRowBean;
import it.eng.parer.slite.gen.tablebean.DecParteNumeroRegistroTableBean;
import it.eng.parer.slite.gen.tablebean.DecParteNumeroRegistroTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocAmmessoRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocAmmessoTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocAmmessoTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecTipoDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutDocAmmessoRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutDocAmmessoTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocAmmessoRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecUsoModelloXsdDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecUsoModelloXsdDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecUsoModelloXsdUniDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecUsoModelloXsdUniDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecXsdDatiSpecRowBean;
import it.eng.parer.slite.gen.tablebean.DecXsdDatiSpecTableBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgCampoValSubStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgCampoValSubStrutTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteRowBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgRegolaValSubStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgRegolaValSubStrutTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.slite.gen.tablebean.OrgTipoServizioTableBean;
import it.eng.parer.slite.gen.viewbean.DecVCalcTiServOnTipoUdRowBean;
import it.eng.parer.slite.gen.viewbean.DecVLisTiUniDocAmsRowBean;
import it.eng.parer.slite.gen.viewbean.DecVLisTiUniDocAmsTableBean;
import it.eng.parer.slite.gen.viewbean.DecVRicCriterioRaggrTableBean;
import it.eng.parer.util.Utils;
import it.eng.parer.volume.utils.VolumeEnums;
import it.eng.parer.web.ejb.AmministrazioneEjb;
import it.eng.parer.web.ejb.CriteriRaggruppamentoEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.MonitoraggioHelper;
import it.eng.parer.web.util.ActionMap;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Constants.TipoDato;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TiUsoModelloXsd;
import it.eng.parer.ws.utils.KeyOrdUtility;
import it.eng.parer.ws.versamento.dto.ConfigRegAnno;
import it.eng.parer.ws.versamentoTpi.utils.FileServUtils;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.base.BaseForm;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.form.fields.impl.ComboBox;
import it.eng.spagoLite.form.fields.impl.Input;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import java.util.Iterator;

public class StrutTipiAction extends StrutTipiAbstractAction {

    private static Logger logger = LoggerFactory.getLogger(StrutTipiAction.class.getName());
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioHelper")
    private MonitoraggioHelper monitoraggioHelper;
    @EJB(mappedName = "java:app/Parer-ejb/ModelliSerieEjb")
    private ModelliSerieEjb modelliSerieEjb;
    @EJB(mappedName = "java:app/Parer-ejb/RegistroEjb")
    private RegistroEjb registroEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoUnitaDocEjb")
    private TipoUnitaDocEjb tipoUnitaDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoSerieEjb")
    private TipoSerieEjb tipoSerieEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @EJB(mappedName = "java:app/Parer-ejb/DatiSpecificiEjb")
    private DatiSpecificiEjb datiSpecEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ModelliXsdUdEjb")
    private ModelliXsdUdEjb modelliXsdUdEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoDocumentoEjb")
    private TipoDocumentoEjb tipoDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoStrutturaDocEjb")
    private TipoStrutturaDocEjb tipoStrutDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/SottoStruttureEjb")
    private SottoStruttureEjb subStrutEjb;
    @EJB(mappedName = "java:app/Parer-ejb/CriteriRaggruppamentoEjb")
    private CriteriRaggruppamentoEjb critRaggrEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmministrazioneEjb")
    private AmministrazioneEjb amministrazioneEjb;

    // Pattern per l'inserimento del nome registro conforme al set di caratteri
    // ammessi
    private static final String NOME_REG = "^[A-Za-z0-9]?$";
    private static final String INSIEME_REG = "^([A-Za-z0-9]+(,[A-Za-z0-9]+)*)?$";
    private static final String RANGE_REG = "^((\\<(\\d+)\\>-\\<(\\d+)\\>))?$";
    private static final Pattern regPattern = Pattern.compile(NOME_REG);
    private static final Pattern insiemePattern = Pattern.compile(INSIEME_REG);
    private static final Pattern rangePattern = Pattern.compile(RANGE_REG);
    private static final String PARAMETER_ID_PARTI_ELIMINATE = "PARAMETER_ID_PARTI_ELIMINATE";

    @Override
    public void initOnClick() throws EMFError {
        /*
         * Empty method
         */
    }

    @Override
    public void process() throws EMFError {
        /*
         * Empty method
         */
    }

    @Override
    public void loadDettaglio() throws EMFError {
        try {
            String lista = getTableName();
            String action = getNavigationEvent();
            ActionMap mappa = getActionMapFromSession();
            if (lista != null && (action != null && !action.equals(NE_DETTAGLIO_INSERT))) {
                String cessato = getRequest().getParameter("cessato");
                if (lista.equals(getForm().getTipoUnitaDocList().getName())
                        && (getForm().getTipoUnitaDocList().getTable() != null)
                        && (getForm().getTipoUnitaDocList().getTable().size() > 0)) {

                    BigDecimal idTipoUnita = ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable()
                            .getCurrentRow()).getIdTipoUnitaDoc();

                    loadTipoUnitaDoc(idTipoUnita);

                } else if (lista.equals(getForm().getTipoDocList().getName())
                        && (getForm().getTipoDocList().getTable() != null)
                        && (getForm().getTipoDocList().getTable().size() > 0)) {

                    getForm().getTipoDoc().setViewMode();
                    getForm().getTipoDocList().setStatus(Status.view);
                    getForm().getTipoDoc().setStatus(Status.view);
                    getForm().getTipoDocCreazioneCriterio().setEditMode();
                    getForm().getTipoDocCreazioneCriterio().clear();
                    getForm().getTipoDoc().getLogEventiTipoDoc().setEditMode();
                    getForm().getTipoDoc().getFl_tipo_doc_principale()
                            .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
                    BigDecimal idTipoDoc = ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow())
                            .getIdTipoDoc();
                    // Nascondo il tab (all'interno del quale ci sarà il flag) per la creazione del
                    // criterio
                    getForm().getCreaCriterioTipoDocSection().setHidden(true);

                    DecTipoDocRowBean tipoDocRowBean = tipoDocEjb.getDecTipoDocRowBean(idTipoDoc, null);
                    getForm().getTipoDoc().copyFromBean(tipoDocRowBean);

                    if (getForm().getTipoDocList().getTable().size() > 0) {
                        // Carico gli xsd dati specifici
                        DecXsdDatiSpecTableBean xsdDatiSpecTableBean = new DecXsdDatiSpecTableBean();
                        DecXsdDatiSpecRowBean xsdDatiSpecRowBean = new DecXsdDatiSpecRowBean();
                        xsdDatiSpecRowBean.setIdTipoDoc(
                                ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow())
                                        .getIdTipoDoc());
                        xsdDatiSpecRowBean.setIdStrut(tipoDocRowBean.getIdStrut());
                        xsdDatiSpecRowBean.setTiUsoXsd(CostantiDB.TipiUsoDatiSpec.VERS.name());
                        xsdDatiSpecRowBean.setTiEntitaSacer(CostantiDB.TipiEntitaSacer.DOC.name());

                        xsdDatiSpecTableBean = datiSpecEjb.getDecXsdDatiSpecTableBean(xsdDatiSpecRowBean);

                        getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
                        getForm().getXsdDatiSpecList().getTable().first();
                        getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

                        // Se è documento principale, carico e mostro le regole
                        if (tipoDocRowBean.getFlTipoDocPrincipale() != null
                                && tipoDocRowBean.getFlTipoDocPrincipale().equals("1")) {
                            getForm().getRegoleSubStrutTab().setHidden(false);
                            OrgRegolaValSubStrutTableBean regoleTableBean = subStrutEjb
                                    .getOrgRegolaValSubStrutTableBean(idTipoDoc, Constants.TipoDato.TIPO_DOC,
                                            getForm().getRegoleSubStrutList().isFilterValidRecords());
                            regoleTableBean.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                            getForm().getRegoleSubStrutList().setTable(regoleTableBean);
                            // Nascondo il campo relativo al nome tipo documento
                            getForm().getRegoleSubStrutList().getNm_tipo_doc().setHidden(true);
                            getForm().getRegoleSubStrutList().getTable().first();
                        } else {
                            getForm().getRegoleSubStrutTab().setHidden(true);
                        }

                        // Lista tipo struttura documento ammessa
                        DecTipoStrutDocAmmessoTableBean tipoStrutDocTableBean = tipoDocEjb
                                .getDecTipoStrutDocAmmessoTableBeanByIdTipoDoc(idTipoDoc);
                        getForm().getTipoStrutDocAmmessoDaTipoDocList().setTable(tipoStrutDocTableBean);
                        getForm().getTipoStrutDocAmmessoDaTipoDocList().getTable().first();
                        getForm().getTipoStrutDocAmmessoDaTipoDocList().getTable()
                                .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

                        // Lista criteri di raggruppamento
                        DecVRicCriterioRaggrTableBean criteri = critRaggrEjb
                                .getCriteriRaggrDaTipoDoc(tipoDocRowBean.getIdStrut(), idTipoDoc);
                        criteri.addSortingRule("nm_criterio_raggr", SortingRule.ASC);
                        criteri.sort();
                        getForm().getCriteriRaggruppamentoList().setTable(criteri);
                        getForm().getCriteriRaggruppamentoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    }

                    if (StringUtils.isNotBlank(cessato) && "1".equals(cessato)) {
                        getForm().getTipoDocList().setUserOperations(true, false, false, false);
                        getForm().getRegoleSubStrutList().setUserOperations(true, false, false, false);
                        getForm().getTipoStrutDocAmmessoDaTipoDocList().setUserOperations(true, false, false, false);
                        getForm().getXsdDatiSpecList().setUserOperations(true, false, false, false);
                        getForm().getCriteriRaggruppamentoList().setUserOperations(true, false, false, false);
                    }
                } else if (lista.equals(getForm().getRegistroUnitaDocList().getName())
                        && (getForm().getRegistroUnitaDocList().getTable() != null)
                        && (getForm().getRegistroUnitaDocList().getTable().size() > 0)
                        || lista.equals(getForm().getRegistroUnitaDoc().getName())) {
                    getSession().removeAttribute("fromDuplicaRegistro");
                    getForm().getRegistroUnitaDoc().setViewMode();
                    getForm().getRegistroUnitaDoc().setStatus(Status.view);
                    getForm().getRegistroUnitaDocList().setStatus(Status.view);

                    if ((StringUtils.isNotBlank(cessato) && "1".equals(cessato))
                            || getRequest().getAttribute("cessato") != null) {
                        getForm().getRegistroUnitaDoc().getDuplicaRegistroButton().setViewMode();
                    } else {
                        getForm().getRegistroUnitaDoc().getDuplicaRegistroButton().setEditMode();
                    }
                    getForm().getRegistroUnitaDoc().getLogEventi().setEditMode();
                    getForm().getRegistroCreazioneCriterio().setEditMode();
                    // Nascondo il tab (all'interno del quale ci sarà il flag) per la creazione del
                    // criterio
                    getForm().getCreaCriterioRegistroSection().setHidden(true);

                    getForm().getRegistroUnitaDoc().getFl_registro_fisc()
                            .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
                    getForm().getRegistroUnitaDoc().getFl_crea_tipo_serie_standard()
                            .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
                    BigDecimal idStrut = getForm().getIdList().getId_strut().parse();

                    DecModelloTipoSerieTableBean listaModelli = modelliSerieEjb
                            .getDecModelloTipoSerieAllAbilitatiTableBean(idStrut, true);
                    getForm().getRegistroUnitaDoc().getId_modello_tipo_serie().setDecodeMap(DecodeMap.Factory
                            .newInstance(listaModelli, "id_modello_tipo_serie", "nm_modello_tipo_serie"));

                    BigDecimal idRegistroUnitaDoc = ((DecRegistroUnitaDocRowBean) getForm().getRegistroUnitaDocList()
                            .getTable().getCurrentRow()).getIdRegistroUnitaDoc();
                    getForm().getRegistroUnitaDoc().getConserv_unlimited()
                            .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

                    DecRegistroUnitaDocRowBean registroUnitaDocRowBean = registroEjb
                            .getDecRegistroUnitaDocRowBean(idRegistroUnitaDoc, null);

                    getForm().getRegistroUnitaDoc().copyFromBean(registroUnitaDocRowBean);

                    String maxLenNumero = registroEjb.calcolaMaxLenNumeroChiave(registroUnitaDocRowBean);
                    this.setMaxLenNumeroChiaveToSession(maxLenNumero);
                    getForm().getRegistroUnitaDoc().getMax_len_numero().setValue(maxLenNumero);

                    if ((getForm().getRegistroUnitaDocList().getTable().size() > 0)) {

                        BigDecimal idRegistro = ((DecRegistroUnitaDocRowBean) getForm().getRegistroUnitaDocList()
                                .getTable().getCurrentRow()).getIdRegistroUnitaDoc();

                        DecVLisTiUniDocAmsRowBean tipoUnitaDocAmmessoRowBean = new DecVLisTiUniDocAmsRowBean();
                        DecVLisTiUniDocAmsTableBean tipoUnitaDocAmmessoTableBean = new DecVLisTiUniDocAmsTableBean();

                        tipoUnitaDocAmmessoRowBean.setIdRegistroUnitaDoc(idRegistro);
                        tipoUnitaDocAmmessoTableBean = tipoUnitaDocEjb
                                .getDecVLisTiUniDocAmsTableBean(tipoUnitaDocAmmessoRowBean);

                        getForm().getRegistroTipoUnitaDocAmmessoList().setTable(tipoUnitaDocAmmessoTableBean);
                        getForm().getRegistroTipoUnitaDocAmmessoList().getTable().first();
                        getForm().getRegistroTipoUnitaDocAmmessoList().getTable()
                                .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

                        DecAaRegistroUnitaDocTableBean aaRegistroUnitaDocTableBean = registroEjb
                                .getDecAARegistroUnitaDocTableBean(idRegistro);
                        if (aaRegistroUnitaDocTableBean != null) {
                            getForm().getAaRegistroUnitaDocList().setTable(aaRegistroUnitaDocTableBean);
                            getForm().getAaRegistroUnitaDocList().getTable().first();
                            getForm().getAaRegistroUnitaDocList().getTable()
                                    .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                        }

                        // Lista criteri di raggruppamento
                        DecVRicCriterioRaggrTableBean criteri = critRaggrEjb.getCriteriRaggrDaRegistro(
                                registroUnitaDocRowBean.getIdStrut(), registroUnitaDocRowBean.getIdRegistroUnitaDoc());
                        criteri.addSortingRule("nm_criterio_raggr", SortingRule.ASC);
                        criteri.sort();
                        getForm().getCriteriRaggruppamentoList().setTable(criteri);
                        getForm().getCriteriRaggruppamentoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    }

                    if (registroUnitaDocRowBean.getNiAnniConserv() != null) {
                        if (registroUnitaDocRowBean.getNiAnniConserv().equals(new BigDecimal(9999))) {
                            getForm().getRegistroUnitaDoc().getConserv_unlimited().setValue("1");
                        } else {
                            getForm().getRegistroUnitaDoc().getConserv_unlimited().setValue("0");
                        }
                    }

                    if ((StringUtils.isNotBlank(cessato) && "1".equals(cessato))
                            || getRequest().getAttribute("cessato") != null) {
                        getForm().getRegistroUnitaDocList().setUserOperations(true, false, false, false);
                        getForm().getRegistroTipoUnitaDocAmmessoList().setUserOperations(true, false, false, false);
                        getForm().getAaRegistroUnitaDocList().setUserOperations(true, false, false, false);
                        getForm().getCriteriRaggruppamentoList().setUserOperations(true, false, false, false);
                    }
                } else if (lista.equals(getForm().getTipoStrutUnitaDocList().getName())
                        && (getForm().getTipoStrutUnitaDocList().getTable() != null)
                        && (getForm().getTipoStrutUnitaDocList().getTable().size() > 0)) {

                    BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
                    OrgStrutRowBean struttura = struttureEjb.getOrgStrutRowBean(idStrut);
                    if ("1".equals(struttura.getFlCessato())) {
                        getRequest().setAttribute("cessato", true);
                    }

                    getForm().getTipoStrutUnitaDoc().setViewMode();
                    getForm().getTipoStrutUnitaDocList().setStatus(Status.view);
                    getForm().getTipoStrutUnitaDoc().setStatus(Status.view);

                    BigDecimal idTipoUnitaDoc = getForm().getTipoUnitaDoc().getId_tipo_unita_doc().parse();

                    // Riempio le multiselect relative a registro e sistema versante
                    getForm().getTipoStrutUnitaDoc().getRegistro_tipo_strut_unita_doc()
                            .setDecodeMap(DecodeMap.Factory.newInstance(
                                    tipoUnitaDocEjb.getRegistriByTipoUnitaDoc(idTipoUnitaDoc), "id_registro_unita_doc",
                                    "cd_registro_unita_doc"));
                    getForm().getTipoStrutUnitaDoc().getSis_vers_tipo_strut_unita_doc().setDecodeMap(
                            DecodeMap.Factory.newInstance(tipoUnitaDocEjb.getAplSistemaVersanteTableBean(),
                                    "id_sistema_versante", "nm_sistema_versante"));
                    getForm().getTipoStrutUnitaDoc().getMetadati_specifici()
                            .setDecodeMap(DecodeMap.Factory.newInstance(
                                    tipoUnitaDocEjb.getDecXsdDatiSpecTableBean(idTipoUnitaDoc), "id_xsd_dati_spec",
                                    "descrizione"));

                    BigDecimal idTipoStrutUnitaDoc = ((DecTipoStrutUnitaDocRowBean) getForm().getTipoStrutUnitaDocList()
                            .getTable().getCurrentRow()).getIdTipoStrutUnitaDoc();

                    DecTipoStrutUnitaDocRowBean tipoStrutUnitaDocRowBean = tipoUnitaDocEjb
                            .getDecTipoStrutUnitaDocRowBean(idTipoStrutUnitaDoc);
                    getForm().getTipoStrutUnitaDoc().copyFromBean(tipoStrutUnitaDocRowBean);

                    if (getForm().getTipoStrutUnitaDocList().getTable().size() > 0) {

                        DecTipoDocAmmessoTableBean tipoDocAmmessoTableBean = tipoUnitaDocEjb
                                .getDecTipoDocAmmessoTableBean(
                                        ((DecTipoStrutUnitaDocRowBean) getForm().getTipoStrutUnitaDocList().getTable()
                                                .getCurrentRow()).getIdTipoStrutUnitaDoc());

                        getForm().getTipoDocAmmessoList().setTable(tipoDocAmmessoTableBean);
                        getForm().getTipoDocAmmessoList().getTable().first();
                        getForm().getTipoDocAmmessoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

                        getForm().getTipoStrutUnitaDoc().setStatus(Status.view);
                        getForm().getTipoStrutUnitaDoc().setViewMode();
                    }

                    // Recupero i registri e i sistemi versanti
                    List<String> idRegistroUnitaDocList = tipoUnitaDocEjb
                            .getIdRegistroUnitaDocListByTipoStrutUnitaDoc(idTipoStrutUnitaDoc);
                    String[] arrReg = idRegistroUnitaDocList.toArray(new String[idRegistroUnitaDocList.size()]);
                    getForm().getTipoStrutUnitaDoc().getRegistro_tipo_strut_unita_doc().setValues(arrReg);

                    List<String> idSistemaVersanteList = tipoUnitaDocEjb
                            .getIdSistemaVersanteListByTipoStrutUnitaDoc(idTipoStrutUnitaDoc);
                    String[] arrSis = idSistemaVersanteList.toArray(new String[idSistemaVersanteList.size()]);
                    getForm().getTipoStrutUnitaDoc().getSis_vers_tipo_strut_unita_doc().setValues(arrSis);

                    List<String> idXsdDatiSpecList = tipoUnitaDocEjb
                            .getIdXsdDatiSpecListByTipoStrutUnitaDoc(idTipoStrutUnitaDoc);
                    String[] arrXsd = idXsdDatiSpecList.toArray(new String[idXsdDatiSpecList.size()]);
                    getForm().getTipoStrutUnitaDoc().getMetadati_specifici().setValues(arrXsd);

                    if ((StringUtils.isNotBlank(cessato) && "1".equals(cessato))
                            || getRequest().getAttribute("cessato") != null) {
                        getForm().getTipoDocAmmessoList().setUserOperations(true, false, false, false);
                    }
                } else if (lista.equals(getForm().getTipoDocAmmessoList().getName())
                        && (getForm().getTipoDocAmmessoList().getTable() != null)) {
                    getForm().getTipoDocAmmesso().setViewMode();
                    getForm().getTipoDocAmmessoList().setStatus(Status.view);

                    BigDecimal idTipoStrutUnitaDoc = ((DecTipoStrutUnitaDocRowBean) getForm().getTipoStrutUnitaDocList()
                            .getTable().getCurrentRow()).getIdTipoStrutUnitaDoc();

                    long id = struttureEjb.getIdStrutFromTipoStrutUnitaDoc(idTipoStrutUnitaDoc);
                    setTipoDocAmmessoComboBox(id);

                    if ((getForm().getTipoDocAmmessoList().getTable().size() > 0)) {

                        BigDecimal idTipoDocAmmesso = ((DecTipoDocAmmessoRowBean) getForm().getTipoDocAmmessoList()
                                .getTable().getCurrentRow()).getIdTipoDocAmmesso();

                        DecTipoDocAmmessoRowBean tipoDocAmmessoRowBean = tipoUnitaDocEjb
                                .getDecTipoDocAmmessoRowBean(idTipoDocAmmesso, BigDecimal.ZERO, BigDecimal.ZERO);
                        getForm().getTipoDocAmmesso().copyFromBean(tipoDocAmmessoRowBean);
                        String nmTipoDoc = StringUtils
                                .isNotBlank(getForm().getTipoDocAmmesso().getId_tipo_doc().getDecodedValue())
                                        ? getForm().getTipoDocAmmesso().getId_tipo_doc().getDecodedValue()
                                        : getForm().getTipoDocAmmessoList().getTable().getCurrentRow()
                                                .getString("nm_tipo_doc");
                        getForm().getTipoDocAmmesso().getNm_tipo_doc().setValue(nmTipoDoc);
                    }

                    DecTipoStrutUnitaDocRowBean tipoStrutUnitaDocRowBean = ((DecTipoStrutUnitaDocRowBean) getForm()
                            .getTipoStrutUnitaDocList().getTable().getCurrentRow());
                    getForm().getTipoDocAmmesso().getNm_tipo_strut_unita_doc()
                            .setValue(tipoStrutUnitaDocRowBean.getNmTipoStrutUnitaDoc());

                } else if (lista.equals(getForm().getAaRegistroUnitaDocList().getName())
                        && (getForm().getAaRegistroUnitaDocList().getTable() != null)
                        && (getForm().getAaRegistroUnitaDocList().getTable().size() > 0)) {

                    getForm().getAARegistroUnitaDoc().setViewMode();
                    getForm().getAARegistroUnitaDoc().setStatus(Status.view);
                    getForm().getAaRegistroUnitaDocList().setStatus(Status.view);

                    BigDecimal idAaRegistroUnitaDoc = ((DecAaRegistroUnitaDocRowBean) getForm()
                            .getAaRegistroUnitaDocList().getTable().getCurrentRow()).getIdAaRegistroUnitaDoc();

                    DecAaRegistroUnitaDocRowBean aaRegistroUnitaDocRowBean = registroEjb
                            .getDecAaRegistroUnitaDocRowBean(idAaRegistroUnitaDoc);
                    getForm().getAARegistroUnitaDoc().copyFromBean(aaRegistroUnitaDocRowBean);

                    // Inizializzo la combo "Ti_parte" facente parte (ahah il gioco di parole!) di
                    // una lista
                    getForm().getDecParteNumRegistroList().getTi_parte()
                            .setDecodeMap(ComboGetter.getMappaTiParte(ConfigRegAnno.TiParte.values()));

                    DecParteNumeroRegistroTableBean decParteNumeroRegistroTableBean = registroEjb
                            .getDecParteNumeroRegistroTableBean(idAaRegistroUnitaDoc);
                    getForm().getDecParteNumRegistroList().setTable(decParteNumeroRegistroTableBean);
                    getForm().getDecParteNumRegistroList().getTable().first();
                    getForm().getDecParteNumRegistroList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

                    DecErrAaRegistroUnitaDocTableBean decErrRegistroUnitaDocTableBean = registroEjb
                            .getDecErrAaRegistroUnitaDocTableBean(idAaRegistroUnitaDoc);
                    getForm().getErroriSuRegistroList().setTable(decErrRegistroUnitaDocTableBean);
                    getForm().getErroriSuRegistroList().getTable().first();
                    getForm().getErroriSuRegistroList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                } else if (lista.equals(getForm().getRegoleSubStrutList().getName())
                        && getForm().getRegoleSubStrutList().getTable() != null
                        && !getForm().getRegoleSubStrutList().getTable().isEmpty()) {
                    BigDecimal idStrut;
                    OrgRegolaValSubStrutRowBean row = (OrgRegolaValSubStrutRowBean) getForm().getRegoleSubStrutList()
                            .getTable().getCurrentRow();
                    getForm().getRegolaSubStrut().copyFromBean(row);

                    if (getLastPublisher().equals(Application.Publisher.TIPO_UNITA_DOC_DETAIL)) {
                        idStrut = ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                                .getIdStrut();
                        getForm().getRegolaSubStrut().getId_tipo_doc().setDecodeMap(DecodeMap.Factory
                                .newInstance(tipoDocEjb.getDocumentiPrincipali(idStrut), "id_tipo_doc", "nm_tipo_doc"));
                        getForm().getRegolaSubStrut().getId_tipo_doc().setValue("" + row.getIdTipoDoc());

                        OrgStrutRowBean struttura = struttureEjb.getOrgStrutRowBean(idStrut);
                        if ("1".equals(struttura.getFlCessato())) {
                            getRequest().setAttribute("cessato", true);
                        }
                    } else if (getLastPublisher().equals(Application.Publisher.TIPO_DOC_DETAIL)) {
                        idStrut = ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow())
                                .getIdStrut();
                        getForm().getRegolaSubStrut().getId_tipo_unita_doc()
                                .setDecodeMap(DecodeMap.Factory.newInstance(
                                        tipoUnitaDocEjb.getDecTipoUnitaDocTableBean(idStrut), "id_tipo_unita_doc",
                                        "nm_tipo_unita_doc"));
                        getForm().getRegolaSubStrut().getId_tipo_unita_doc().setValue("" + row.getIdTipoUnitaDoc());

                        OrgStrutRowBean struttura = struttureEjb.getOrgStrutRowBean(idStrut);
                        if ("1".equals(struttura.getFlCessato())) {
                            getRequest().setAttribute("cessato", true);
                        }
                    }

                    getForm().getCampiSubStrutList()
                            .setTable(subStrutEjb.getOrgCampoValSubStrutTableBean(row.getIdRegolaValSubStrut()));
                    getForm().getCampiSubStrutList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    getForm().getCampiSubStrutList().getTable().first();

                    if (getRequest().getAttribute("cessato") != null) {
                        getForm().getCampiSubStrutList().setUserOperations(true, false, false, false);
                    }
                } else if (lista.equals(getForm().getCampiSubStrutList().getName())
                        && getForm().getCampiSubStrutList().getTable() != null
                        && !getForm().getCampiSubStrutList().getTable().isEmpty()) {

                    getForm().getCampoSubStrut().getTi_campo().setDecodeMap(ComboGetter.getMappaTiCampo());
                    OrgCampoValSubStrutRowBean row = (OrgCampoValSubStrutRowBean) getForm().getCampiSubStrutList()
                            .getTable().getCurrentRow();
                    getForm().getCampoSubStrut().getTi_campo().setValue(row.getTiCampo());
                    populateNmCampo(row.getTiCampo());
                    getForm().getCampoSubStrut().copyFromBean(row);
                } else if (lista.equals(getForm().getXsdModelliUdList().getName())) {
                    // Carico gli xsd dei modelli (tipo unita doc)
                    DecUsoModelloXsdUniDocRowBean row = (DecUsoModelloXsdUniDocRowBean) getForm().getXsdModelliUdList()
                            .getTable().getCurrentRow();
                    // init combos
                    DecodeMap mpTiModelloXsd = modelliXsdUdEjb.getTiModelloXsdInUsoByIdAmbTiUso(row.getIdAmbiente(),
                            row.getTiUsoModelloXsd(), "ti_modello_xsd");
                    getForm().getXsdModelliUdDetail().getTi_modello_xsd().setDecodeMap(mpTiModelloXsd);
                    DecodeMap mpCdXsd = modelliXsdUdEjb.getCdXsdInUsoByIdAmbTiModUso(row.getIdAmbiente(),
                            row.getTiModelloXsd(), row.getTiUsoModelloXsd(), "cd_xsd");
                    getForm().getXsdModelliUdDetail().getCd_xsd().setDecodeMap(mpCdXsd);

                    // Caricamento dettaglio modello xsd ud
                    DecUsoModelloXsdUniDocRowBean usoModelloXsdUniDocRowBean = modelliXsdUdEjb
                            .getDecModelloXsdUdInUsoOnUniDoc(getForm().getIdList().getId_tipo_unita_doc().parse(),
                                    row.getIdModelloXsdUd(), row.getTiUsoModelloXsd(), row.getCdXsd());
                    //
                    getForm().getXsdModelliUdDetail().copyFromBean(usoModelloXsdUniDocRowBean);
                    //
                    getForm().getXsdModelliUdDetail().setViewMode();
                    // button edit mode
                    getForm().getXsdModelliUdDetail().getScaricaXsdModelliUdButton().setEditMode();
                    getForm().getXsdModelliUdDetail().setStatus(Status.view);
                }
            }

            if (lista != null && (lista.equals(getForm().getRegistroTipoUnitaDocAmmessoList().getName())
                    || lista.equals(getForm().getTipoUnitaDocAmmessoList().getName()))) {
                // Carico la vista nella sua tabella, per averla dopo
                getForm().getTipoUnitaDocAmmesso().getCreaTipoSerieStandard().setEditMode();
                getForm().getTipoUnitaDocAmmesso().getCreaTipoSerieStandard().setHidden(false);
                BigDecimal idStrut = null;
                if (lista.equals(getForm().getRegistroTipoUnitaDocAmmessoList().getName())) {
                    getForm().getRegistroTipoUnitaDocAmmessoList().setStatus(Status.view);
                    idStrut = ((DecRegistroUnitaDocRowBean) getForm().getRegistroUnitaDocList().getTable()
                            .getCurrentRow()).getIdStrut();

                    DecVLisTiUniDocAmsRowBean currentRow = (DecVLisTiUniDocAmsRowBean) getForm()
                            .getRegistroTipoUnitaDocAmmessoList().getTable().getCurrentRow();
                    setTipoUnitaDocAmmessoComboBox(idStrut);

                    if (currentRow != null) {
                        getForm().getTipoUnitaDocAmmesso().getId_registro_unita_doc()
                                .setValue(currentRow.getIdRegistroUnitaDoc().toString());
                        getForm().getTipoUnitaDocAmmesso().getId_tipo_unita_doc()
                                .setValue(currentRow.getIdTipoUnitaDoc().toString());

                        try {
                            if (tipoSerieEjb.checkTipoSerieStandardDaRegistroOTipoUd(currentRow.getIdRegistroUnitaDoc(),
                                    currentRow.getIdTipoUnitaDoc()) == null) {
                                getForm().getTipoUnitaDocAmmesso().getCreaTipoSerieStandard().setHidden(true);
                            }
                        } catch (ParerUserError ex) {
                            // In caso di eccezione non mostra il bottone, senza mostrare errori
                            getForm().getTipoUnitaDocAmmesso().getCreaTipoSerieStandard().setHidden(true);
                        }
                    }

                    getForm().getTipoUnitaDocAmmesso().getId_tipo_unita_doc().setViewMode();

                } else if (lista.equals(getForm().getTipoUnitaDocAmmessoList().getName())) {

                    getForm().getTipoUnitaDocAmmessoList().setStatus(Status.view);
                    if (mappa != null
                            && Application.Publisher.TIPO_UNITA_DOC_DETAIL.equalsIgnoreCase(mappa.getAction())) {
                        BigDecimal id = (BigDecimal) mappa.getId();
                        idStrut = tipoUnitaDocEjb.getDecTipoUnitaDoc(id, null, null).getIdStrut();
                    } else {
                        idStrut = ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                                .getIdStrut();
                    }

                    DecVLisTiUniDocAmsRowBean currentRow = (DecVLisTiUniDocAmsRowBean) getForm()
                            .getTipoUnitaDocAmmessoList().getTable().getCurrentRow();
                    setTipoUnitaDocAmmessoComboBox(idStrut);

                    if (currentRow != null) {
                        getForm().getTipoUnitaDocAmmesso().getId_registro_unita_doc()
                                .setValue(currentRow.getIdRegistroUnitaDoc().toString());
                        getForm().getTipoUnitaDocAmmesso().getId_tipo_unita_doc()
                                .setValue(currentRow.getIdTipoUnitaDoc().toString());

                        try {
                            if (tipoSerieEjb.checkTipoSerieStandardDaRegistroOTipoUd(currentRow.getIdRegistroUnitaDoc(),
                                    currentRow.getIdTipoUnitaDoc()) == null) {
                                getForm().getTipoUnitaDocAmmesso().getCreaTipoSerieStandard().setHidden(true);
                            }
                        } catch (ParerUserError ex) {
                            // In caso di eccezione non mostra il bottone, senza mostrare errori
                            getForm().getTipoUnitaDocAmmesso().getCreaTipoSerieStandard().setHidden(true);
                        }
                    }
                    getForm().getTipoUnitaDocAmmesso().getId_registro_unita_doc().setViewMode();
                }

            }

            // Lista "Tipo Struttura Documento Ammesso"
            if (lista != null && (lista.equals(getForm().getTipoStrutDocAmmessoDaTipoDocList().getName()))) {
                BigDecimal idStrut = ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow())
                        .getIdStrut();
                /* Ricavo il record corrente della lista */
                DecTipoStrutDocAmmessoRowBean currentRow = (DecTipoStrutDocAmmessoRowBean) getForm()
                        .getTipoStrutDocAmmessoDaTipoDocList().getTable().getCurrentRow();
                /* Ricavo i tipi struttura documento selezionabili e li carico nella combo */
                DecTipoStrutDocTableBean table = tipoDocEjb.getDecTipoStrutDocTableBeanByIdStrut(idStrut, new Date());
                DecodeMap mappaTipoStrutDoc = new DecodeMap();
                mappaTipoStrutDoc.populatedMap(table, "id_tipo_strut_doc", "nm_tipo_strut_doc");
                getForm().getTipoStrutDocAmmessoDaTipoDoc().getId_tipo_strut_doc_ammesso()
                        .setDecodeMap(mappaTipoStrutDoc);
                /*
                 * Se il record corrente è presente (non sono in insert), setto ilvalore del tipo struttura documento
                 */
                if (currentRow != null) {
                    getForm().getTipoStrutDocAmmessoDaTipoDoc().getId_tipo_strut_doc_ammesso()
                            .setValue(currentRow.getIdTipoStrutDoc().toString());
                }
                getForm().getTipoStrutDocAmmessoDaTipoDoc().getId_tipo_strut_doc_ammesso().setViewMode();
                getForm().getTipoStrutDocAmmessoDaTipoDocList().setStatus(Status.view);
            }

        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
    }

    @Override
    public void undoDettaglio() throws EMFError {
        setActionToSession(null, true);
        String publisher = getLastPublisher();

        if (publisher.equals(Application.Publisher.REGISTRO_UNITA_DOC_DETAIL)
                && getForm().getRegistroUnitaDoc().getStatus() != null
                && getForm().getRegistroUnitaDoc().getStatus().toString().equals("insert")) {
            getSession().removeAttribute("fromDuplicaRegistro");
            getSession().removeAttribute("salvataggioAttributesCreazioneCriteriDaControlliModificaRegistro");
            goBack();
        } else if (publisher.equals(Application.Publisher.TIPO_UNITA_DOC_DETAIL)
                && getForm().getTipoUnitaDoc().getStatus() != null
                && getForm().getTipoUnitaDoc().getStatus().toString().equals("insert")) {
            goBack();
        } else if (publisher.equals(Application.Publisher.TIPO_DOC_DETAIL) && getForm().getTipoDoc().getStatus() != null
                && getForm().getTipoDoc().getStatus().toString().equals("insert")) {
            goBack();
        } else if (publisher.equals(Application.Publisher.TIPO_UNITA_DOC_AMMESSO_DETAIL)
                && getForm().getTipoUnitaDocAmmesso().getStatus() != null
                && getForm().getTipoUnitaDocAmmesso().getStatus().toString().equals("insert")) {
            goBack();
        } else if (publisher.equals(Application.Publisher.TIPO_DOC_AMMESSO_DETAIL)
                && getForm().getTipoDocAmmesso().getStatus() != null
                && getForm().getTipoDocAmmesso().getStatus().toString().equals("insert")) {
            goBack();
        } else if (publisher.equals(Application.Publisher.TIPO_STRUT_UNITA_DOC_DETAIL)
                && getForm().getTipoStrutUnitaDoc().getStatus() != null
                && getForm().getTipoStrutUnitaDoc().getStatus().toString().equals("insert")) {
            goBack();
        } else if (publisher.equals(Application.Publisher.AA_REGISTRO_UNITA_DOC_DETAIL)
                && getForm().getAARegistroUnitaDoc().getStatus() != null
                && getForm().getAARegistroUnitaDoc().getStatus().toString().equals("insert")) {
            goBack();
        } else if (publisher.equals(Application.Publisher.PARAMETRI_TIPO_UD)
                && getForm().getTipoUnitaDoc().getStatus() != null) {
            ricercaParametriTipoUdButton();
            getForm().getTipoUnitaDoc().setStatus(Status.view);
            setViewModeListeParametri();
            forwardToPublisher(publisher);
        } else if (publisher.equals(Application.Publisher.REGOLA_DETAIL)) {
            if (getForm().getRegoleSubStrutList().getStatus().equals(Status.insert)) {
                goBack();
            } else {
                getForm().getRegolaSubStrut()
                        .copyFromBean(getForm().getRegoleSubStrutList().getTable().getCurrentRow());
                getForm().getRegoleSubStrutList().setStatus(Status.view);
                getForm().getRegolaSubStrut().setStatus(Status.view);
                getForm().getRegolaSubStrut().setViewMode();
            }
        } else if (publisher.equals(Application.Publisher.CAMPO_REGOLA_DETAIL)) {
            if (getForm().getCampiSubStrutList().getStatus().equals(Status.insert)) {
                goBack();
            } else {
                getForm().getCampoSubStrut().copyFromBean(getForm().getCampiSubStrutList().getTable().getCurrentRow());
                getForm().getCampiSubStrutList().setStatus(Status.view);
                getForm().getCampoSubStrut().setStatus(Status.view);
                getForm().getCampoSubStrut().setViewMode();
                forwardToPublisher(publisher);
            }
        } else if (publisher.equals(Application.Publisher.ASSOCIAZIONE_TIPO_DOC_TIPO_STRUT_DOC)
                && getForm().getTipoStrutDocAmmessoDaTipoDoc().getStatus() != null
                && getForm().getTipoStrutDocAmmessoDaTipoDoc().getStatus().toString().equals("insert")) {
            goBack();
        } else if (publisher.equals(Application.Publisher.MODELLO_XSD_UD_AMMESSO_DETAIL)) {
            getForm().getXsdModelliUdDetail().setStatus(Status.view);
            getForm().getXsdModelliUdList().setStatus(Status.view);
            getForm().getXsdModelliUdDetail().setViewMode();
            goBack();
        } else {
            loadDettaglio();
        }
    }

    @Override
    public void insertDettaglio() throws EMFError {
        String lista = getRequest().getParameter("table");

        Calendar data = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);
        Date today = data.getTime();
        String stringToday = formatter.format(today);

        if (lista.equals(getForm().getRegistroUnitaDocList().getName())) {

            getForm().getRegistroUnitaDoc().setEditMode();
            getForm().getRegistroUnitaDoc().clear();
            getForm().getRegistroUnitaDoc().getDt_istituz().setValue(stringToday);
            getForm().getRegistroCreazioneCriterio().setEditMode();
            getForm().getRegistroCreazioneCriterio().clear();
            // Visualizzo il tab (che contiene il flag) e nascondo il bottone per la
            // creazione del criterio
            getForm().getCreaCriterioRegistroSection().setHidden(false);
            getForm().getRegistroCreazioneCriterio().getCreaCriterioRaggrStandardRegistroButton().setHidden(true);

            getForm().getRegistroUnitaDoc().getConserv_unlimited().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
            getForm().getRegistroUnitaDoc().getFl_registro_fisc().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
            getForm().getRegistroUnitaDoc().getFl_registro_fisc().setValue(JobConstants.ComboFlag.NO.getValue());
            getForm().getRegistroUnitaDoc().getFl_crea_tipo_serie_standard()
                    .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
            BigDecimal idStrut = getForm().getIdList().getId_strut().parse();

            DecModelloTipoSerieTableBean listaModelli = modelliSerieEjb
                    .getDecModelloTipoSerieAllAbilitatiTableBean(idStrut, true);
            getForm().getRegistroUnitaDoc().getId_modello_tipo_serie().setDecodeMap(
                    DecodeMap.Factory.newInstance(listaModelli, "id_modello_tipo_serie", "nm_modello_tipo_serie"));

            getForm().getRegistroUnitaDoc().setStatus(Status.insert);
            getForm().getRegistroUnitaDocList().setStatus(Status.insert);
        } else if (lista.equals(getForm().getTipoUnitaDocList().getName())) {

            getForm().getTipoUnitaDoc().setEditMode();
            getForm().getTipoUnitaDoc().clear();
            getForm().getTipoUnitaDoc().getDt_istituz().setValue(stringToday);
            getForm().getTipoUnitaDocCreazioneCriterio().setEditMode();
            getForm().getTipoUnitaDocCreazioneCriterio().clear();

            // Visualizzo il tab (che contiene il flag) e nascondo il bottone per la
            // creazione del criterio
            getForm().getCreaCriterioTipoUnitaDocSection().setHidden(false);
            getForm().getTipoUnitaDocCreazioneCriterio().getCreaCriterioRaggrStandardTipoUdButton().setHidden(true);

            setTipoUnitaComboBox();
            disabileTipoUdParametersSections(true);

            DecodeMap map = new DecodeMap();
            getForm().getTipoUnitaDoc().getNm_categ_strut().setDecodeMap(map);

            BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
            DecModelloTipoSerieTableBean listaModelli = modelliSerieEjb
                    .getDecModelloTipoSerieAllAbilitatiTableBean(idStrut, true);
            getForm().getTipoUnitaDoc().getId_modello_tipo_serie().setDecodeMap(
                    DecodeMap.Factory.newInstance(listaModelli, "id_modello_tipo_serie", "nm_modello_tipo_serie"));
            getForm().getTipoUnitaDoc().getFl_crea_tipo_serie_standard()
                    .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

            // Popolo le combo dei Tipo servizio e setto il valore
            OrgTipoServizioTableBean listaTipiServizioConserv = tipoUnitaDocEjb
                    .getOrgTipoServizioTableBean(CostantiDB.TiClasseTipoServizio.CONSERVAZIONE.name());
            getForm().getTipoUnitaDoc().getId_tipo_servizio().setDecodeMap(
                    DecodeMap.Factory.newInstance(listaTipiServizioConserv, "id_tipo_servizio", "cd_tipo_servizio"));
            OrgTipoServizioTableBean listaTipiServizioAttiv = tipoUnitaDocEjb
                    .getOrgTipoServizioTableBean(CostantiDB.TiClasseTipoServizio.ATTIVAZIONE_SISTEMA_VERSANTE.name());
            getForm().getTipoUnitaDoc().getId_tipo_servizio_attiv().setDecodeMap(
                    DecodeMap.Factory.newInstance(listaTipiServizioAttiv, "id_tipo_servizio", "cd_tipo_servizio"));

            OrgTipoServizioTableBean listaTipiServConservTipoUd = tipoUnitaDocEjb
                    .getOrgTipoServizioTableBean(CostantiDB.TiClasseTipoServizio.CONSERVAZIONE.name());
            getForm().getTipoUnitaDoc().getId_tipo_serv_conserv_tipo_ud().setDecodeMap(
                    DecodeMap.Factory.newInstance(listaTipiServConservTipoUd, "id_tipo_servizio", "cd_tipo_servizio"));
            OrgTipoServizioTableBean listaTipiServAttivTipoUd = tipoUnitaDocEjb
                    .getOrgTipoServizioTableBean(CostantiDB.TiClasseTipoServizio.ATTIVAZIONE_TIPO_UD.name());
            getForm().getTipoUnitaDoc().getId_tipo_serv_attiv_tipo_ud().setDecodeMap(
                    DecodeMap.Factory.newInstance(listaTipiServAttivTipoUd, "id_tipo_servizio", "cd_tipo_servizio"));

            // Data primo versamento non editabile
            getForm().getTipoUnitaDoc().getDt_first_vers().setViewMode();

            getForm().getTipoUnitaDoc().setStatus(Status.insert);
            getForm().getTipoUnitaDocList().setStatus(Status.insert);

        } else if (lista.equals(getForm().getTipoDocList().getName())) {
            getForm().getTipoDoc().setEditMode();
            getForm().getTipoDoc().clear();
            getForm().getTipoDoc().getDt_istituz().setValue(stringToday);
            getForm().getTipoDoc().getFl_tipo_doc_principale().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
            getForm().getTipoDoc().getFl_tipo_doc_principale().setValue(JobConstants.ComboFlag.NO.getValue());
            getForm().getTipoDocCreazioneCriterio().setEditMode();
            getForm().getTipoDocCreazioneCriterio().clear();

            getForm().getTipoDocCreazioneCriterio().getCreaCriterioRaggrStandardTipoDocButton().setHidden(true);
            getForm().getCreaCriterioTipoDocSection().setHidden(false);

            getForm().getTipoDoc().setStatus(Status.insert);
            getForm().getTipoDocList().setStatus(Status.insert);

        } else if (lista.equals(getForm().getRegistroTipoUnitaDocAmmessoList().getName())) {
            DecRegistroUnitaDocRowBean registroUnitaDocRow = (DecRegistroUnitaDocRowBean) getForm()
                    .getRegistroUnitaDocList().getTable().getCurrentRow();

            getForm().getTipoUnitaDocAmmesso().setEditMode();
            getForm().getTipoUnitaDocAmmesso().clear();

            getForm().getTipoUnitaDocAmmesso().getId_registro_unita_doc()
                    .setValue(registroUnitaDocRow.getIdRegistroUnitaDoc().toString());
            getForm().getTipoUnitaDocAmmesso().getId_registro_unita_doc().setViewMode();

            getForm().getTipoUnitaDocAmmesso().getCreaTipoSerieStandard().setHidden(true);

            getForm().getTipoUnitaDocAmmesso().setStatus(Status.insert);
            getForm().getRegistroTipoUnitaDocAmmessoList().setStatus(Status.insert);
        } else if (lista.equals(getForm().getTipoUnitaDocAmmessoList().getName())) {

            DecTipoUnitaDocRowBean tipoUnitaDocRow = (DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable()
                    .getCurrentRow();

            getForm().getTipoUnitaDocAmmesso().setEditMode();
            getForm().getTipoUnitaDocAmmesso().clear();

            getForm().getTipoUnitaDocAmmesso().getId_tipo_unita_doc()
                    .setValue(tipoUnitaDocRow.getIdTipoUnitaDoc().toString());
            getForm().getTipoUnitaDocAmmesso().getId_tipo_unita_doc().setViewMode();

            getForm().getTipoUnitaDocAmmesso().getCreaTipoSerieStandard().setHidden(true);

            getForm().getTipoUnitaDocAmmesso().setStatus(Status.insert);
            getForm().getTipoUnitaDocAmmessoList().setStatus(Status.insert);

        } else if (lista.equals(getForm().getTipoStrutUnitaDocList().getName())) {

            getForm().getTipoStrutUnitaDoc().setEditMode();
            getForm().getTipoStrutUnitaDoc().clear();
            getForm().getTipoStrutUnitaDoc().getDt_istituz().setValue(stringToday);

            BigDecimal idTipoUnitaDoc = getForm().getTipoUnitaDoc().getId_tipo_unita_doc().parse();

            // Riempio le multiselect relative a registro e sistema versante
            getForm().getTipoStrutUnitaDoc().getRegistro_tipo_strut_unita_doc().setDecodeMap(
                    DecodeMap.Factory.newInstance(tipoUnitaDocEjb.getRegistriByTipoUnitaDoc(idTipoUnitaDoc),
                            "id_registro_unita_doc", "cd_registro_unita_doc"));
            getForm().getTipoStrutUnitaDoc().getSis_vers_tipo_strut_unita_doc()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tipoUnitaDocEjb.getAplSistemaVersanteTableBean(),
                            "id_sistema_versante", "nm_sistema_versante"));

            getForm().getTipoStrutUnitaDoc().setStatus(Status.insert);
            getForm().getTipoStrutUnitaDocList().setStatus(Status.insert);

        } else if (lista.equals(getForm().getTipoDocAmmessoList().getName())) {

            getForm().getTipoDocAmmesso().setEditMode();
            getForm().getTipoDocAmmesso().getNm_tipo_strut_unita_doc().setViewMode();
            getForm().getTipoDocAmmesso().getFl_obbl().clear();
            getForm().getTipoDocAmmesso().getId_tipo_doc().clear();
            getForm().getTipoDocAmmesso().getTi_doc().clear();

            BigDecimal idTipoStrutUnitaDoc = ((DecTipoStrutUnitaDocRowBean) getForm().getTipoStrutUnitaDocList()
                    .getTable().getCurrentRow()).getIdTipoStrutUnitaDoc();

            long id = struttureEjb.getIdStrutFromTipoStrutUnitaDoc(idTipoStrutUnitaDoc);
            setTipoDocAmmessoComboBox(id);

            getForm().getTipoDocAmmesso().setStatus(Status.insert);
            getForm().getTipoDocAmmessoList().setStatus(Status.insert);

        } else if (lista.equals(getForm().getAaRegistroUnitaDocList().getName())) {
            getForm().getAaRegistroUnitaDocList().setStatus(Status.insert);
            getForm().getAARegistroUnitaDoc().setStatus(Status.insert);

            getSession().removeAttribute(PARAMETER_ID_PARTI_ELIMINATE);
            getForm().getInserimentoWizard().reset();
            getForm().getDatiAnniParte().reset();
            getForm().getDatiParti().reset();

            getForm().getDatiAnniParte().setEditMode();
            getForm().getPartiList().setTable(new DecParteNumeroRegistroTableBean());
            getForm().getPartiList().getTi_parte()
                    .setDecodeMap(ComboGetter.getMappaTiParte(ConfigRegAnno.TiParte.values()));
        } else if (lista.equals(getForm().getRegoleSubStrutList().getName())) {
            getForm().getRegolaSubStrut().setEditMode();
            getForm().getRegolaSubStrut().clear();

            BigDecimal idStrut = null;
            if (getSession().getAttribute("provenienzaRegola").equals(Application.Publisher.TIPO_UNITA_DOC_DETAIL)) {
                idStrut = ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                        .getIdStrut();
                getForm().getRegolaSubStrut().getId_tipo_doc().setDecodeMap(DecodeMap.Factory
                        .newInstance(tipoDocEjb.getDocumentiPrincipali(idStrut), "id_tipo_doc", "nm_tipo_doc"));
            } else if (getSession().getAttribute("provenienzaRegola").equals(Application.Publisher.TIPO_DOC_DETAIL)) {
                idStrut = ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow()).getIdStrut();
                getForm().getRegolaSubStrut().getId_tipo_unita_doc().setDecodeMap(
                        DecodeMap.Factory.newInstance(tipoUnitaDocEjb.getDecTipoUnitaDocTableBean(idStrut),
                                "id_tipo_unita_doc", "nm_tipo_unita_doc"));
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(2444, 11, 31, 0, 0, 0);

            DateFormat formato = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);
            getForm().getRegolaSubStrut().getDt_soppres().setValue(formato.format(calendar.getTime()));

            getForm().getRegolaSubStrut().setStatus(Status.insert);
            getForm().getRegoleSubStrutList().setStatus(Status.insert);
        } else if (lista.equals(getForm().getCampiSubStrutList().getName())) {
            getForm().getCampoSubStrut().setEditMode();
            getForm().getCampoSubStrut().clear();

            getForm().getCampoSubStrut().getTi_campo().setDecodeMap(ComboGetter.getMappaTiCampo());
            getForm().getCampoSubStrut().getNm_campo().setDecodeMap(new DecodeMap());
            getForm().getCampoSubStrut().getId_sub_strut().setDecodeMap(new DecodeMap());
            getForm().getCampoSubStrut().getId_attrib_dati_spec().setDecodeMap(new DecodeMap());

            getForm().getCampoSubStrut().setStatus(Status.insert);
            getForm().getCampiSubStrutList().setStatus(Status.insert);
        } else if (lista.equals(getForm().getCriteriRaggruppamentoList().getName())) {
            redirectToCreaCriterioRaggrPage();
        } else if (lista.equals(getForm().getTipoStrutDocAmmessoDaTipoDocList().getName())) {
            /* Pulisco la bombo */
            getForm().getTipoStrutDocAmmessoDaTipoDoc().getId_tipo_strut_doc_ammesso().clear();
            getForm().getTipoStrutDocAmmessoDaTipoDoc().setEditMode();
            getForm().getTipoStrutDocAmmessoDaTipoDoc().setStatus(Status.insert);
            getForm().getTipoStrutDocAmmessoDaTipoDocList().setStatus(Status.insert);
        } else if (lista.equals(getForm().getXsdModelliUdList().getName())) {
            // clear / reset
            getForm().getXsdModelliUdDetail().reset();
            // init combos
            BigDecimal idAmbiente = null;
            if (getForm().getIdList().getId_strut().parse() != null) {
                OrgStrutRowBean strutRowBean = struttureEjb
                        .getOrgStrutRowBean(getForm().getIdList().getId_strut().parse());
                OrgEnteRowBean enteRowBean = struttureEjb.getOrgEnteRowBean(strutRowBean.getIdEnte());
                idAmbiente = enteRowBean.getIdAmbiente();
                getForm().getXsdModelliUdDetail().getId_ambiente().setValue(idAmbiente.toString());
            }
            if (getForm().getIdList().getId_tipo_doc().parse() != null) {
                getForm().getXsdModelliUdDetail().getId_tipo_uni_doc()
                        .setValue(getForm().getIdList().getId_tipo_doc().parse().toString());
            }
            if (getForm().getIdList().getId_tipo_unita_doc().parse() != null) {
                getForm().getXsdModelliUdDetail().getId_tipo_uni_doc()
                        .setValue(getForm().getIdList().getId_tipo_unita_doc().parse().toString());
            }

            String tiUsoModelloXsd = TiUsoModelloXsd.VERS.name();
            if (getForm().getIdList().getNm_sys_migraz().parse() != null) {
                tiUsoModelloXsd = TiUsoModelloXsd.MIGRAZ.name();
            }
            DecodeMap mpTiModelloXsd = modelliXsdUdEjb.getTiModelloXsdInUsoByIdAmbTiUso(idAmbiente, tiUsoModelloXsd,
                    "ti_modello_xsd");
            getForm().getXsdModelliUdDetail().getTi_modello_xsd().setDecodeMap(mpTiModelloXsd);
            getForm().getXsdModelliUdDetail().getCd_xsd().setDecodeMap(new DecodeMap());

            // set tipo uso
            getForm().getXsdModelliUdDetail().getTi_uso_modello_xsd().setValue(tiUsoModelloXsd);
            //
            getForm().getXsdModelliUdDetail().setEditMode();
            getForm().getXsdModelliUdDetail().getScaricaXsdModelliUdButton().setHidden(true);

            // preset date
            Calendar calendar = Calendar.getInstance();
            calendar.set(2444, 11, 31, 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);

            getForm().getXsdModelliUdDetail().getDt_uso_istituz().setValue(df.format(Calendar.getInstance().getTime()));
            getForm().getXsdModelliUdDetail().getDt_uso_soppres().setValue(df.format(calendar.getTime()));

            getForm().getXsdModelliUdDetail().setStatus(Status.insert);
            getForm().getXsdModelliUdList().setStatus(Status.insert);

        }
    }

    private void disabileTipoUdParametersSections(boolean disable) {
        getForm().getParametriAmministrazioneSection().setHidden(disable);
        getForm().getParametriConservazioneSection().setHidden(disable);
        getForm().getParametriGestioneSection().setHidden(disable);
    }

    @Override
    public void saveDettaglio() throws EMFError {

        String publisher = getLastPublisher();

        if (publisher.equals(Application.Publisher.REGISTRO_UNITA_DOC_DETAIL)) {
            if (getSession().getAttribute("fromDuplicaRegistro") != null) {
                salvaDuplicaRegistroUnitaDoc();
            } else {
                salvaRegistroUnitaDoc();
            }
        } else if (publisher.equals(Application.Publisher.TIPO_UNITA_DOC_DETAIL)) {
            salvaTipoUnitaDoc();
        } else if (publisher.equals(Application.Publisher.TIPO_DOC_DETAIL)) {
            salvaTipoDoc();
        } else if (publisher.equals(Application.Publisher.TIPO_UNITA_DOC_AMMESSO_DETAIL)) {
            salvaTipoUnitaDocAmmesso();
        } else if (publisher.equals(Application.Publisher.TIPO_STRUT_UNITA_DOC_DETAIL)) {
            salvaTipoStrutUnitaDoc();
        } else if (publisher.equals(Application.Publisher.TIPO_DOC_AMMESSO_DETAIL)) {
            salvaTipoDocAmmesso();
        } else if (publisher.equals(Application.Publisher.REGOLA_DETAIL)) {
            salvaRegola();
        } else if (publisher.equals(Application.Publisher.CAMPO_REGOLA_DETAIL)) {
            salvaCampoTipoUd();
        } else if (publisher.equals(Application.Publisher.ASSOCIAZIONE_TIPO_DOC_TIPO_STRUT_DOC)) {
            salvaTipoStrutDocAmmesso();
        } else if (publisher.equals(Application.Publisher.PARAMETRI_TIPO_UD)) {
            salvaParametriTipoUd();
        } else if (publisher.equals(Application.Publisher.MODELLO_XSD_UD_AMMESSO_DETAIL)) {
            salvaModelloXsdUdAmmesso();
        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {

        String lista = getTableName();
        String action = getNavigationEvent();
        ActionMap mappa = (ActionMap) getSession().getAttribute("ActionMap");
        getSession().setAttribute("lista", lista);
        if (getForm().getXsdDatiSpecList().getName().equals(lista)) {
            /*
             * Se l'evento non è delete allora fa la redirect
             */
            boolean redirect = false;
            if (getNavigationEvent().equals(NE_DETTAGLIO_DELETE)) {
                DecXsdDatiSpecRowBean xsdDatiSpecRowBean = ((DecXsdDatiSpecRowBean) getForm().getXsdDatiSpecList()
                        .getTable().getCurrentRow());
                getMessageBox().clear();
                Date dtSoppres = xsdDatiSpecRowBean.getDtSoppres();
                Date today = Calendar.getInstance().getTime();
                if (dtSoppres.compareTo(today) < 0) {
                    getMessageBox().addError("Versione XSD gi\u00E0 disattivata in precedenza");
                    forwardToPublisher(getLastPublisher());
                } else {
                    // Il sistema controlla che tale attributo non sia associato a nessun tipo
                    // serie, altrimenti da
                    // errore
                    if (datiSpecEjb.isXsdDatiSpecInUseInTipiSerie(xsdDatiSpecRowBean.getIdXsdDatiSpec())) {
                        getMessageBox().addError(
                                "Almeno un attributo dell'xsd \u00E8 utilizzato da un tipo serie. L'eliminazione dell'xsd non \u00E8 consentita");
                        forwardToPublisher(getLastPublisher());
                    }
                    if (!getMessageBox().hasError()) {
                        boolean isInUse = datiSpecEjb.isXsdDatiSpecInUse(xsdDatiSpecRowBean);
                        boolean isInUseOnCampiRegole = datiSpecEjb.isXsdDatiSpecInUseOnCampi(
                                xsdDatiSpecRowBean.getIdXsdDatiSpec(), "DATO_SPEC_UNI_DOC", "DATO_SPEC_DOC_PRINC");
                        // se in uso non posso cancellare, ma posso disattivare
                        if (isInUse || isInUseOnCampiRegole) {
                            // Mostra messaggio di disattivazione
                            getRequest().setAttribute("confermaDisattivazioneXsd", true);
                            forwardToPublisher(getLastPublisher());
                        } else {
                            redirect = true;
                        }
                    }
                }
            } else {
                redirect = true;
            }
            if (redirect) {
                /*
                 *
                 * Ricordarsi di togliere gli append inutili nella stringa se non serve passare i parametri in quella
                 * maniera
                 */
                StrutDatiSpecForm form = new StrutDatiSpecForm();
                Integer row = getForm().getXsdDatiSpecList().getTable().getCurrentRowIndex();

                StringBuilder string = new StringBuilder(
                        "?operation=listNavigationOnClick&navigationEvent=" + getNavigationEvent() + "&table="
                                + StrutDatiSpecForm.XsdDatiSpecList.NAME + "&riga=" + row.toString());
                form.getXsdDatiSpecList().setTable(getForm().getXsdDatiSpecList().getTable());

                /*
                 * Propago l'idStruttura che ho salvato in memoria, per passarlo con la nuova form che porterà nella
                 * nuova action
                 */
                BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
                form.getIdList().getId_strut().setValue(idStrut.toString());

                OrgStrutRowBean struttura = struttureEjb.getOrgStrutRowBean(idStrut);
                string.append("&cessato=").append(struttura.getFlCessato());

                if (getLastPublisher().equals(Application.Publisher.TIPO_DOC_DETAIL)) {
                    string.append("&idTipoDoc=")
                            .append(((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow())
                                    .getIdTipoDoc().intValue());
                    form.getIdList().getId_tipo_doc()
                            .setValue(((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow())
                                    .getIdTipoDoc().toString());
                    getSession().setAttribute("lastPage", "tipoDoc");
                    form.getTipoDocRif().getNm_tipo_doc().setValue(
                            ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow()).getNmTipoDoc());
                    form.getTipoDocRif().getDs_tipo_doc().setValue(
                            ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow()).getDsTipoDoc());
                } else if (getLastPublisher().equals(Application.Publisher.TIPO_UNITA_DOC_DETAIL)) {
                    string.append("&idTipoUnitaDoc=").append(
                            ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                                    .getIdTipoUnitaDoc().intValue());
                    form.getIdList().getId_tipo_unita_doc().setValue(
                            ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                                    .getIdTipoUnitaDoc().toString());
                    getSession().setAttribute("lastPage", "tipoUnitaDoc");
                    form.getTipoUdRif().getNm_tipo_unita_doc().setValue(
                            ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                                    .getNmTipoUnitaDoc());
                    form.getTipoUdRif().getDs_tipo_unita_doc().setValue(
                            ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                                    .getDsTipoUnitaDoc());
                }

                form.getStrutRif().getStruttura().setValue(getForm().getStrutRif().getStruttura().parse());
                form.getStrutRif().getId_ente().setValue(getForm().getStrutRif().getId_ente().getDecodedValue());

                this.setInsertAction(false);
                this.setEditAction(false);
                this.setDeleteAction(false);
                redirectToAction(Application.Actions.STRUT_DATI_SPEC, string.toString(), form);
            }
        } else if (getForm().getTipoUnitaDocList().getName().equals(lista)) {
            getSession().setAttribute("provenienzaRegola", Application.Publisher.TIPO_UNITA_DOC_DETAIL);
            forwardToPublisher(Application.Publisher.TIPO_UNITA_DOC_DETAIL);

        } else if (getForm().getTipoDocList().getName().equals(lista)) {
            getSession().setAttribute("provenienzaRegola", Application.Publisher.TIPO_DOC_DETAIL);
            forwardToPublisher(Application.Publisher.TIPO_DOC_DETAIL);

        } else if (getForm().getRegistroUnitaDocList().getName().equals(lista)) {
            forwardToPublisher(Application.Publisher.REGISTRO_UNITA_DOC_DETAIL);
        } else if (lista.equals(getForm().getRegoleSubStrutList().getName()) && !action.equals(NE_DETTAGLIO_DELETE)) {
            // Se provengo da tipo unità documentaria mostro i campi di tipo ud
            if (getLastPublisher().equals(Application.Publisher.TIPO_UNITA_DOC_DETAIL)) {
                getForm().getTipoUnitaDoc().getNm_tipo_unita_doc().setHidden(false);
                getForm().getTipoUnitaDoc().getDs_tipo_unita_doc().setHidden(false);
                getForm().getRegolaSubStrut().getId_tipo_doc().setHidden(false);
                getForm().getTipoDoc().getNm_tipo_doc().setHidden(true);
                getForm().getTipoDoc().getDs_tipo_doc().setHidden(true);
                getForm().getRegolaSubStrut().getId_tipo_unita_doc().setHidden(true);
                getSession().setAttribute("provenienzaRegola", Application.Publisher.TIPO_UNITA_DOC_DETAIL);
            } // Se provengo da tipo documento mostro i campi di tipo doc
            else if (getLastPublisher().equals(Application.Publisher.TIPO_DOC_DETAIL)) {
                getForm().getTipoUnitaDoc().getNm_tipo_unita_doc().setHidden(true);
                getForm().getTipoUnitaDoc().getDs_tipo_unita_doc().setHidden(true);
                getForm().getRegolaSubStrut().getId_tipo_doc().setHidden(true);
                getForm().getTipoDoc().getNm_tipo_doc().setHidden(false);
                getForm().getTipoDoc().getDs_tipo_doc().setHidden(false);
                getForm().getRegolaSubStrut().getId_tipo_unita_doc().setHidden(false);
                getSession().setAttribute("provenienzaRegola", Application.Publisher.TIPO_DOC_DETAIL);
            }

            getForm().getRegolaSubStrut().setViewMode();
            getForm().getRegolaSubStrut().setStatus(Status.view);
            getForm().getRegoleSubStrutList().setStatus(Status.view);
            forwardToPublisher(Application.Publisher.REGOLA_DETAIL);
        } else if (lista.equals(getForm().getCampiSubStrutList().getName())
                || lista.equals(getForm().getCampoSubStrut().getName())) {
            getForm().getCampoSubStrut().setViewMode();
            getForm().getCampoSubStrut().setStatus(Status.view);
            getForm().getCampiSubStrutList().setStatus(Status.view);
            forwardToPublisher(Application.Publisher.CAMPO_REGOLA_DETAIL);
        } else if (getForm().getCriteriRaggruppamentoList().getName().equals(lista)
                && action.equals(ListAction.NE_DETTAGLIO_VIEW)) {
            redirectToCreaCriterioRaggrPage();
        } else if (!action.equals(NE_DETTAGLIO_DELETE)) {

            if (getForm().getRegistroTipoUnitaDocAmmessoList().getName().equals(lista)) {

                forwardToPublisher(Application.Publisher.TIPO_UNITA_DOC_AMMESSO_DETAIL);
            } else if (getForm().getTipoUnitaDocAmmessoList().getName().equals(lista)) {

                if (NE_DETTAGLIO_INSERT.equals(action)) {
                    DecTipoDocAmmessoTableBean tipoDocAmmessoTableBean = new DecTipoDocAmmessoTableBean();
                    getForm().getTipoDocAmmessoList().setTable(tipoDocAmmessoTableBean);
                    getForm().getTipoDocAmmessoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                }
                if (NE_DETTAGLIO_VIEW.equals(action)) {

                    getForm().getTipoDocAmmessoList().setStatus(Status.view);
                    getForm().getTipoDocAmmesso().setStatus(Status.view);

                }
                forwardToPublisher(Application.Publisher.TIPO_UNITA_DOC_AMMESSO_DETAIL);
            } else if (getForm().getTipoStrutUnitaDocList().getName().equals(lista)) {
                forwardToPublisher(Application.Publisher.TIPO_STRUT_UNITA_DOC_DETAIL);
            } else if (getForm().getTipoDocAmmessoList().getName().equals(lista)) {
                forwardToPublisher(Application.Publisher.TIPO_DOC_AMMESSO_DETAIL);
            } else if (getForm().getAaRegistroUnitaDocList().getName().equals(lista)
                    && (action.equals(NE_DETTAGLIO_UPDATE) || action.equals(NE_DETTAGLIO_INSERT))) {
                forwardToPublisher(Application.Publisher.AA_REGISTRO_UNITA_DOC_WIZARD);
            } else if (getForm().getAaRegistroUnitaDocList().getName().equals(lista)) {
                forwardToPublisher(Application.Publisher.AA_REGISTRO_UNITA_DOC_DETAIL);
            } else if (getForm().getTipoStrutDocAmmessoDaTipoDocList().getName().equals(lista)) {
                forwardToPublisher(Application.Publisher.ASSOCIAZIONE_TIPO_DOC_TIPO_STRUT_DOC);
            } else if (getForm().getXsdModelliUdList().getName().equals(lista)) {
                forwardToPublisher(Application.Publisher.MODELLO_XSD_UD_AMMESSO_DETAIL);
            }
        }
    }

    public void confermaDisattivazioneXsd() throws EMFError {
        /*
         *
         * Ricordarsi di togliere gli append inutili nella stringa se non serve passare i parametri in quella maniera
         */
        StrutDatiSpecForm form = new StrutDatiSpecForm();
        Integer row = getForm().getXsdDatiSpecList().getTable().getCurrentRowIndex();

        form.getXsdDatiSpecList().setTable(getForm().getXsdDatiSpecList().getTable());
        form.getXsdDatiSpecList().getTable().setCurrentRowIndex(row);
        /*
         * Propago l'idStruttura che ho salvato in memoria, per passarlo con la nuova form che porterà nella nuova
         * action
         */
        BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
        form.getIdList().getId_strut().setValue(idStrut.toString());

        if (getLastPublisher().equals(Application.Publisher.TIPO_DOC_DETAIL)) {
            form.getIdList().getId_tipo_doc()
                    .setValue(((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow()).getIdTipoDoc()
                            .toString());
            getSession().setAttribute("lastPage", "tipoDoc");
            form.getTipoDocRif().getNm_tipo_doc().setValue(
                    ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow()).getNmTipoDoc());
            form.getTipoDocRif().getDs_tipo_doc().setValue(
                    ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow()).getDsTipoDoc());
        } else if (getLastPublisher().equals(Application.Publisher.TIPO_UNITA_DOC_DETAIL)) {
            form.getIdList().getId_tipo_unita_doc()
                    .setValue(((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                            .getIdTipoUnitaDoc().toString());
            getSession().setAttribute("lastPage", "tipoUnitaDoc");
            form.getTipoUdRif().getNm_tipo_unita_doc()
                    .setValue(((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                            .getNmTipoUnitaDoc());
            form.getTipoUdRif().getDs_tipo_unita_doc()
                    .setValue(((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                            .getDsTipoUnitaDoc());
        }

        form.getStrutRif().getStruttura().setValue(getForm().getStrutRif().getStruttura().parse());
        form.getStrutRif().getId_ente().setValue(getForm().getStrutRif().getId_ente().getDecodedValue());

        this.setInsertAction(false);
        this.setEditAction(false);
        this.setDeleteAction(false);
        redirectToAction(Application.Actions.STRUT_DATI_SPEC, "?operation=confermaDisattivazione", form);
    }

    @Override
    public void elencoOnClick() throws EMFError {
        // Ripristino la visibilità dei campi "condivisi" nella gestione delle regole
        if (getLastPublisher().equals(Application.Publisher.REGISTRO_UNITA_DOC_DETAIL)) {
            getForm().getTipoUnitaDoc().getNm_tipo_unita_doc().setHidden(false);
            getForm().getTipoUnitaDoc().getDs_tipo_unita_doc().setHidden(false);
            getForm().getTipoDoc().getNm_tipo_doc().setHidden(false);
            getForm().getTipoDoc().getDs_tipo_doc().setHidden(false);
        }
        if (getForm().getTipoUnitaDoc() != null) {
            getForm().getTipoUnitaDoc().setStatus(Status.view);
        }
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.TIPO_DOC_DETAIL;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        setActionToSession(null, true);
        BigDecimal idStrut = null;
        try {
            idStrut = getForm().getIdList().getId_strut().parse();
        } catch (EMFError ex) {
            logger.error(ex.getMessage(), ex);
            getMessageBox().addError("Errore inaspettato durante il recupero dell'identificativo della struttura");
        } finally {
            if (idStrut == null && !getMessageBox().hasError()) {
                getMessageBox().addError("Errore inaspettato durante il recupero dell'identificativo della struttura");
            }
        }

        if (publisherName.equals(Application.Publisher.TIPO_DOC_DETAIL)
                || publisherName.equals(Application.Publisher.TIPO_UNITA_DOC_DETAIL)
                || publisherName.equals(Application.Publisher.TIPO_COMP_DOC_DETAIL)
                || publisherName.equals(Application.Publisher.REGISTRO_UNITA_DOC_DETAIL)) {

            DecXsdDatiSpecTableBean xsdDatiSpecTableBean = new DecXsdDatiSpecTableBean();
            DecXsdDatiSpecRowBean xsdDatiSpecRowBean = new DecXsdDatiSpecRowBean();

            xsdDatiSpecRowBean.setIdStrut(idStrut);

            if (publisherName.equals(Application.Publisher.TIPO_DOC_DETAIL)) {

                xsdDatiSpecRowBean.setIdTipoDoc(
                        ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow()).getIdTipoDoc());
                xsdDatiSpecRowBean.setTiUsoXsd(CostantiDB.TipiUsoDatiSpec.VERS.name());
                xsdDatiSpecRowBean.setTiEntitaSacer(CostantiDB.TipiEntitaSacer.DOC.name());
                xsdDatiSpecTableBean = datiSpecEjb.getDecXsdDatiSpecTableBean(xsdDatiSpecRowBean);

                getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
                getForm().getXsdDatiSpecList().getTable().first();
                getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getXsdDatiSpecList().setStatus(Status.view);

                DecTipoDocRowBean tipoDocRB = (DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow();

                // Lista tipo struttura documento ammessa
                DecTipoStrutDocAmmessoTableBean tipoStrutDocTableBean = tipoDocEjb
                        .getDecTipoStrutDocAmmessoTableBeanByIdTipoDoc(tipoDocRB.getIdTipoDoc());
                getForm().getTipoStrutDocAmmessoDaTipoDocList().setTable(tipoStrutDocTableBean);
                getForm().getTipoStrutDocAmmessoDaTipoDocList().getTable().first();
                getForm().getTipoStrutDocAmmessoDaTipoDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

                // Lista criteri di raggruppamento
                DecVRicCriterioRaggrTableBean criteri = critRaggrEjb.getCriteriRaggrDaTipoDoc(tipoDocRB.getIdStrut(),
                        tipoDocRB.getIdTipoDoc());
                criteri.addSortingRule("nm_criterio_raggr", SortingRule.ASC);
                criteri.sort();
                getForm().getCriteriRaggruppamentoList().setTable(criteri);
                getForm().getCriteriRaggruppamentoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

            } else if (publisherName.equals(Application.Publisher.TIPO_UNITA_DOC_DETAIL)) {
                try {
                    loadTipoUnitaDoclists(false);
                } catch (EMFError | ParerUserError ex) {
                    logger.error(ex.getMessage(), ex);
                    getMessageBox().addError(
                            "Errore inaspettato durante il caricamento della lista tipologia unit\u00E0 documentaria");
                }
            } else if (publisherName.equals(Application.Publisher.REGISTRO_UNITA_DOC_DETAIL)) {
                DecRegistroUnitaDocRowBean registroRB = (DecRegistroUnitaDocRowBean) getForm().getRegistroUnitaDocList()
                        .getTable().getCurrentRow();
                DecRegistroUnitaDocRowBean registroUdRB = registroEjb
                        .getDecRegistroUnitaDocRowBean(registroRB.getIdRegistroUnitaDoc(), registroRB.getIdStrut());
                DecVRicCriterioRaggrTableBean criteri = critRaggrEjb
                        .getCriteriRaggrDaRegistro(registroUdRB.getIdStrut(), registroUdRB.getIdRegistroUnitaDoc());
                criteri.addSortingRule("nm_criterio_raggr", SortingRule.ASC);
                criteri.sort();
                getForm().getCriteriRaggruppamentoList().setTable(criteri);
                getForm().getCriteriRaggruppamentoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            }
        } else if (publisherName.equals(Application.Publisher.AA_REGISTRO_UNITA_DOC_DETAIL)) {
            BigDecimal idAaRegistroUnitaDoc = ((DecAaRegistroUnitaDocRowBean) getForm().getAaRegistroUnitaDocList()
                    .getTable().getCurrentRow()).getIdAaRegistroUnitaDoc();
            try {
                DecAaRegistroUnitaDocRowBean aaRegistroUnitaDocRowBean = registroEjb
                        .getDecAaRegistroUnitaDocRowBean(idAaRegistroUnitaDoc);
                getForm().getAARegistroUnitaDoc().copyFromBean(aaRegistroUnitaDocRowBean);
            } catch (EMFError ex) {
                logger.error(ex.getMessage(), ex);
                getMessageBox().addError("Errore inaspettato durante il recupero dell'identificativo della struttura");
            }
            DecParteNumeroRegistroTableBean decParteNumeroRegistroTableBean = registroEjb
                    .getDecParteNumeroRegistroTableBean(idAaRegistroUnitaDoc);
            getForm().getDecParteNumRegistroList().setTable(decParteNumeroRegistroTableBean);
            getForm().getDecParteNumRegistroList().getTable().first();
            getForm().getDecParteNumRegistroList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

            DecErrAaRegistroUnitaDocTableBean decErrRegistroUnitaDocTableBean = registroEjb
                    .getDecErrAaRegistroUnitaDocTableBean(idAaRegistroUnitaDoc);
            getForm().getErroriSuRegistroList().setTable(decErrRegistroUnitaDocTableBean);
            getForm().getErroriSuRegistroList().getTable().first();
            getForm().getErroriSuRegistroList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        }

        if (getLastPublisher().equals(Application.Publisher.REGISTRO_UNITA_DOC_DETAIL)) {

            DecRegistroUnitaDocTableBean registroUnitaDocTableBean = registroEjb.getDecRegistroUnitaDocTableBean(
                    idStrut, getForm().getRegistroUnitaDocList().isFilterValidRecords());

            getForm().getRegistroUnitaDocList().setTable(registroUnitaDocTableBean);
            getForm().getRegistroUnitaDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getRegistroUnitaDocList().setStatus(Status.view);

        } else if (getLastPublisher().equals(Application.Publisher.TIPO_UNITA_DOC_DETAIL)) {
            DecTipoUnitaDocTableBean tipoUnitaTableBean = tipoUnitaDocEjb.getDecTipoUnitaDocTableBean(idStrut,
                    getForm().getTipoUnitaDocList().isFilterValidRecords());
            getForm().getTipoUnitaDocList().setTable(tipoUnitaTableBean);
            getForm().getTipoUnitaDocList().getTable().first();
            getForm().getTipoUnitaDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getTipoUnitaDocList().setStatus(Status.view);

        } else if (getLastPublisher().equals(Application.Publisher.TIPO_DOC_DETAIL)) {

            DecTipoDocTableBean tipoDocTableBean = tipoDocEjb.getDecTipoDocTableBean(idStrut,
                    getForm().getTipoDocList().isFilterValidRecords());

            getForm().getTipoDocList().setTable(tipoDocTableBean);
            getForm().getTipoDocList().getTable().first();
            getForm().getTipoDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getTipoDocList().setStatus(Status.view);

        }
        if (getLastPublisher().equals(Application.Publisher.TIPO_UNITA_DOC_AMMESSO_DETAIL)) {

            DecVLisTiUniDocAmsRowBean VTipoUnitaDocAmmessoRowBean = new DecVLisTiUniDocAmsRowBean();
            DecVLisTiUniDocAmsTableBean tipoUnitaDocAmmessoTableBean = new DecVLisTiUniDocAmsTableBean();

            if (getForm().getTipoUnitaDocList().getTable() != null) {
                VTipoUnitaDocAmmessoRowBean.setIdTipoUnitaDoc(
                        ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                                .getIdTipoUnitaDoc());
                try {
                    loadTipoUnitaDoclists(false);
                } catch (EMFError | ParerUserError ex) {
                    logger.error("Eccezione", ex);
                    getMessageBox()
                            .addError("Errore inaspettato al caricamento della lista tipo unit\u00E0 dcoumentaria");
                }
            } else if (getForm().getRegistroUnitaDocList() != null) {
                VTipoUnitaDocAmmessoRowBean.setIdRegistroUnitaDoc(
                        ((DecRegistroUnitaDocRowBean) getForm().getRegistroUnitaDocList().getTable().getCurrentRow())
                                .getIdRegistroUnitaDoc());
                tipoUnitaDocAmmessoTableBean = tipoUnitaDocEjb
                        .getDecVLisTiUniDocAmsTableBean(VTipoUnitaDocAmmessoRowBean);

                getForm().getTipoUnitaDocAmmessoList().setTable(tipoUnitaDocAmmessoTableBean);
                getForm().getTipoUnitaDocAmmessoList().getTable().first();
                getForm().getTipoUnitaDocAmmessoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getTipoUnitaDocAmmessoList().setStatus(Status.view);
            }

        }
        if (getLastPublisher().equals(Application.Publisher.AA_REGISTRO_UNITA_DOC_DETAIL)
                || getLastPublisher().equals(Application.Publisher.AA_REGISTRO_UNITA_DOC_WIZARD)) {

            DecAaRegistroUnitaDocTableBean tableBean = new DecAaRegistroUnitaDocTableBean();
            tableBean = registroEjb.getDecAARegistroUnitaDocTableBean(
                    ((DecRegistroUnitaDocRowBean) getForm().getRegistroUnitaDocList().getTable().getCurrentRow())
                            .getIdRegistroUnitaDoc());

            getForm().getAaRegistroUnitaDocList().setTable(tableBean);
            getForm().getAaRegistroUnitaDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getAaRegistroUnitaDocList().setStatus(Status.view);

        }
        if (getLastPublisher().equals(Application.Publisher.TIPO_STRUT_UNITA_DOC_DETAIL)) {
            try {
                loadTipoUnitaDoclists(false);
            } catch (EMFError | ParerUserError ex) {
                logger.error("Eccezione", ex);
            }
        } else if (getLastPublisher().equals(Application.Publisher.TIPO_DOC_AMMESSO_DETAIL)) {
            DecTipoDocAmmessoTableBean tipoDocAmmessoTableBean = tipoUnitaDocEjb.getDecTipoDocAmmessoTableBean(
                    ((DecTipoStrutUnitaDocRowBean) getForm().getTipoStrutUnitaDocList().getTable().getCurrentRow())
                            .getIdTipoStrutUnitaDoc());
            getForm().getTipoDocAmmessoList().setTable(tipoDocAmmessoTableBean);
            getForm().getTipoDocAmmessoList().getTable().first();
            getForm().getTipoDocAmmessoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getTipoDocAmmessoList().setStatus(Status.view);
        } else if (getLastPublisher().equals(Application.Publisher.CAMPO_REGOLA_DETAIL)) {
            OrgRegolaValSubStrutRowBean row = (OrgRegolaValSubStrutRowBean) getForm().getRegoleSubStrutList().getTable()
                    .getCurrentRow();
            getForm().getCampiSubStrutList()
                    .setTable(subStrutEjb.getOrgCampoValSubStrutTableBean(row.getIdRegolaValSubStrut()));
            getForm().getCampiSubStrutList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getCampiSubStrutList().getTable().first();
        } else if (getLastPublisher().equals(Application.Publisher.REGOLA_DETAIL)) {
            if (getSession().getAttribute("provenienzaRegola").equals(Application.Publisher.TIPO_UNITA_DOC_DETAIL)) {
                DecTipoUnitaDocRowBean row = (DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable()
                        .getCurrentRow();
                getForm().getRegoleSubStrutList()
                        .setTable(subStrutEjb.getOrgRegolaValSubStrutTableBean(row.getIdTipoUnitaDoc(),
                                Constants.TipoDato.TIPO_UNITA_DOC,
                                getForm().getRegoleSubStrutList().isFilterValidRecords()));
            } else if (getSession().getAttribute("provenienzaRegola").equals(Application.Publisher.TIPO_DOC_DETAIL)) {
                DecTipoDocRowBean row = (DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow();
                getForm().getRegoleSubStrutList()
                        .setTable(subStrutEjb.getOrgRegolaValSubStrutTableBean(row.getIdTipoDoc(),
                                Constants.TipoDato.TIPO_DOC, getForm().getRegoleSubStrutList().isFilterValidRecords()));
            }
            getForm().getRegoleSubStrutList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getRegoleSubStrutList().getTable().first();
        }

    }

    @Override
    public String getControllerName() {
        return Application.Actions.STRUT_TIPI;
    }

    /**
     * Metodo di caricamento Tab corrispondente alla lista di DecTipoStrutUnitaDoc
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void tabDecTipoStrutUnitaDocOnClick() throws EMFError {

        getForm().getDecTipoUnitaDocTab().setCurrentTab(getForm().getDecTipoUnitaDocTab().getDecTipoStrutUnitaDoc());
        final BigDecimal idTipoUnitaDoc = ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable()
                .getCurrentRow()).getIdTipoUnitaDoc();
        DecTipoStrutUnitaDocTableBean tipoStrutUnitaDocTableBean = tipoStrutDocEjb.getDecTipoStrutUnitaDocTableBean(
                idTipoUnitaDoc, getForm().getTipoStrutUnitaDocList().isFilterValidRecords());

        getForm().getTipoStrutUnitaDocList().setTable(tipoStrutUnitaDocTableBean);
        getForm().getTipoStrutUnitaDocList().getTable().first();
        getForm().getTipoStrutUnitaDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        forwardToPublisher(Application.Publisher.TIPO_UNITA_DOC_DETAIL);
    }

    /**
     * Metodo di caricamento Tab corrispondente alla lista di DecTipoUnitaDocAmmesso
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void tabDecTipoUnitaDocAmmessoOnClick() throws EMFError {
        getForm().getDecTipoUnitaDocTab().setCurrentTab(getForm().getDecTipoUnitaDocTab().getDecTipoUnitaDocAmmesso());

        DecVLisTiUniDocAmsRowBean tipoUnitaDocAmmessoRowBean = new DecVLisTiUniDocAmsRowBean();
        DecVLisTiUniDocAmsTableBean tipoUnitaDocAmmessoTableBean = new DecVLisTiUniDocAmsTableBean();

        tipoUnitaDocAmmessoRowBean
                .setIdTipoUnitaDoc(((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                        .getIdTipoUnitaDoc());
        tipoUnitaDocAmmessoTableBean = tipoUnitaDocEjb.getDecVLisTiUniDocAmsTableBean(tipoUnitaDocAmmessoRowBean);

        getForm().getTipoUnitaDocAmmessoList().setTable(tipoUnitaDocAmmessoTableBean);
        getForm().getTipoUnitaDocAmmessoList().getTable().first();
        getForm().getTipoUnitaDocAmmessoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        forwardToPublisher(Application.Publisher.TIPO_UNITA_DOC_DETAIL);
    }

    @Override
    public void tabDecTipoDocXsdDatiSpecOnClick() throws EMFError {

        getForm().getDecTipoDocTab().setCurrentTab(getForm().getDecTipoDocTab().getDecTipoDocXsdDatiSpec());

        DecXsdDatiSpecTableBean xsdDatiSpecTableBean = new DecXsdDatiSpecTableBean();
        DecXsdDatiSpecRowBean xsdDatiSpecRowBean = new DecXsdDatiSpecRowBean();

        xsdDatiSpecRowBean.setIdTipoDoc(
                ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow()).getIdTipoDoc());
        xsdDatiSpecRowBean
                .setIdStrut(((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow()).getIdStrut());
        xsdDatiSpecRowBean.setTiUsoXsd(CostantiDB.TipiUsoDatiSpec.VERS.name());
        xsdDatiSpecRowBean.setTiEntitaSacer(CostantiDB.TipiEntitaSacer.DOC.name());
        xsdDatiSpecTableBean = datiSpecEjb.getDecXsdDatiSpecTableBean(xsdDatiSpecRowBean);

        getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
        getForm().getXsdDatiSpecList().getTable().first();
        getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getXsdDatiSpecList().setStatus(Status.view);

        forwardToPublisher(Application.Publisher.TIPO_DOC_DETAIL);
    }

    @Override
    public void scaricaXsdButton() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void caricaXsdButton() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void tabDecTipoUnitaDocXsdDatiSpecOnClick() throws EMFError {

        getForm().getDecTipoUnitaDocTab()
                .setCurrentTab(getForm().getDecTipoUnitaDocTab().getDecTipoUnitaDocXsdDatiSpec());

        getForm().getXsdDatiSpec().setStatus(Status.view);
        getForm().getXsdDatiSpecList().setStatus(Status.view);

        DecXsdDatiSpecTableBean xsdDatiSpecTableBean = new DecXsdDatiSpecTableBean();
        DecXsdDatiSpecRowBean xsdDatiSpecRowBean = new DecXsdDatiSpecRowBean();
        xsdDatiSpecRowBean
                .setIdTipoUnitaDoc(((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                        .getIdTipoUnitaDoc());
        xsdDatiSpecRowBean.setIdStrut(
                ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow()).getIdStrut());
        xsdDatiSpecRowBean.setTiUsoXsd(CostantiDB.TipiUsoDatiSpec.VERS.name());
        xsdDatiSpecRowBean.setTiEntitaSacer(CostantiDB.TipiEntitaSacer.UNI_DOC.name());
        xsdDatiSpecTableBean = datiSpecEjb.getDecXsdDatiSpecTableBean(xsdDatiSpecRowBean);

        getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
        getForm().getXsdDatiSpecList().getTable().first();
        getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        forwardToPublisher(Application.Publisher.TIPO_UNITA_DOC_DETAIL);
    }

    /**
     * Metodo per popolare la combobox relativa alla form corrispondente alla tabella DecTipoDocAmmesso
     */
    private void setTipoDocAmmessoComboBox(Long id) throws EMFError {
        getForm().getTipoDocAmmesso().getTi_doc()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_doc", VolumeEnums.DocTypeEnum.values()));

        BigDecimal idStrut = new BigDecimal(id);
        DecTipoDocTableBean table = tipoDocEjb.getDecTipoDocTableBean(idStrut, true);
        table.addSortingRule("nm_tipo_doc", SortingRule.ASC);
        table.sort();
        DecodeMap mappaTipoDoc = new DecodeMap();
        mappaTipoDoc.populatedMap(table, "id_tipo_doc", "nm_tipo_doc");
        getForm().getTipoDocAmmesso().getId_tipo_doc().setDecodeMap(mappaTipoDoc);
    }

    /**
     * Metodo per popolare a seconda della struttura di riferimento, la ComboBox relativa alla tabella
     * TipoUnitaDocAmmesso
     *
     * @param idStrut
     * 
     * @throws EMFError
     *             errore generico
     */
    private void setTipoUnitaDocAmmessoComboBox(BigDecimal idStrut) throws EMFError {
        DecRegistroUnitaDocTableBean registroUnitaTableBean = new DecRegistroUnitaDocTableBean();
        registroUnitaTableBean = registroEjb.getDecRegistroUnitaDocTableBean(idStrut, false);
        // Ordino per registroUnitaDoc.cdRegistroUnitaDoc
        registroUnitaTableBean.addSortingRule("cd_registro_unita_doc", SortingRule.ASC);
        registroUnitaTableBean.sort();
        DecodeMap mappaRegistroUnitaDoc = new DecodeMap();
        mappaRegistroUnitaDoc.populatedMap(registroUnitaTableBean, "id_registro_unita_doc", "cd_registro_unita_doc");
        getForm().getTipoUnitaDocAmmesso().getId_registro_unita_doc().setDecodeMap(mappaRegistroUnitaDoc);
        DecTipoUnitaDocTableBean tipoUnitaTableBean = tipoUnitaDocEjb.getDecTipoUnitaDocTableBean(idStrut, true);
        // Ordino per registroUnitaDoc.cdRegistroUnitaDoc
        tipoUnitaTableBean.addSortingRule("nm_tipo_unita_doc", SortingRule.ASC);
        tipoUnitaTableBean.sort();
        DecodeMap mappaTipoUnitaDoc = new DecodeMap();
        mappaTipoUnitaDoc.populatedMap(tipoUnitaTableBean, "id_tipo_unita_doc", "nm_tipo_unita_doc");
        getForm().getTipoUnitaDocAmmesso().getId_tipo_unita_doc().setDecodeMap(mappaTipoUnitaDoc);
    }

    /**
     * Metodo che visualizza la form associata a DecRegistroUnitaDoc in status update
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void updateRegistroUnitaDocList() throws EMFError {

        getForm().getRegistroUnitaDoc().setEditMode();
        getForm().getRegistroUnitaDoc().getCd_registro_unita_doc().setViewMode();
        getForm().getRegistroUnitaDoc().getFl_registro_fisc().setViewMode();
        getForm().getRegistroUnitaDoc().getControllo_formato().setViewMode();
        getForm().getRegistroUnitaDoc().getDt_istituz().setViewMode();
        // Nascondo flag
        getForm().getCreaCriterioRegistroSection().setHidden(true);

        DecRegistroUnitaDocRowBean registroCurrent = (DecRegistroUnitaDocRowBean) getForm().getRegistroUnitaDocList()
                .getTable().getCurrentRow();
        if (!struttureEjb.existAroUnitaDocByIdRegistroUnitaDoc(registroCurrent.getIdRegistroUnitaDoc(),
                registroCurrent.getIdStrut())) {
            getForm().getRegistroUnitaDoc().setEditMode();
        }

        // Se non è presente un criterio di raggruppamento per il registro corrente,
        // visualizzo il bottone per la
        // creazione del criterio
        if (!critRaggrEjb.existsCriterioStandardPerTipoDato(registroCurrent.getIdRegistroUnitaDoc().longValue(),
                TipoDato.REGISTRO)) {
            getForm().getRegistroCreazioneCriterio().getCreaCriterioRaggrStandardRegistroButton().setHidden(false);
        } else {
            getForm().getRegistroCreazioneCriterio().getCreaCriterioRaggrStandardRegistroButton().setHidden(true);
        }

        // Se il registro è associato ad un tipo ud in una tipologia serie non rende
        // editabile il flag
        if (tipoSerieEjb.existDecTipoSerieUdForRegistro(registroCurrent.getIdRegistroUnitaDoc())) {
            getForm().getRegistroUnitaDoc().getFl_crea_serie().setViewMode();
        }

        getForm().getRegistroUnitaDoc().setStatus(Status.update);
        getForm().getRegistroUnitaDocList().setStatus(Status.update);
        getSession().setAttribute("id_registro_lavorato", null);
    }

    /**
     * Metodo che visualizza la form associata a DecTipoUnitaDoc in status update
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void updateTipoUnitaDocList() throws EMFError {
        getForm().getTipoUnitaDoc().setEditMode();
        getForm().getTipoUnitaDoc().getNm_tipo_unita_doc().setViewMode();
        getForm().getTipoUnitaDoc().getTi_save_file().setViewMode();
        getForm().getTipoUnitaDoc().getDt_istituz().setViewMode();
        // Nascondo il tab (che contiene il flag) per la creazione del criterio
        getForm().getCreaCriterioTipoUnitaDocSection().setHidden(true);

        DecTipoUnitaDocRowBean tipoUnitaDocCurrent = (DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable()
                .getCurrentRow();

        // sono in update e quindi non imposto i valori di default per il tipo
        // salvataggio
        // andrebbe a sovrascrivere il valore effettivo del record in modifica.
        setTipoUnitaComboBox(true);
        if (!struttureEjb.existAroUnitaDocByIdTipoUnitaDoc(tipoUnitaDocCurrent.getIdTipoUnitaDoc(),
                tipoUnitaDocCurrent.getIdStrut())) {
            getForm().getTipoUnitaDoc().setEditMode();
        }

        // Se non è presente un criterio di raggruppamento per la tipologia unità
        // documentaria corrente, visualizzo il
        // bottone di creazione criterio
        if (!critRaggrEjb.existsCriterioStandardPerTipoDato(tipoUnitaDocCurrent.getIdTipoUnitaDoc().longValue(),
                TipoDato.TIPO_UNITA_DOC)) {
            getForm().getTipoUnitaDocCreazioneCriterio().getCreaCriterioRaggrStandardTipoUdButton().setHidden(false);
        } else {
            getForm().getTipoUnitaDocCreazioneCriterio().getCreaCriterioRaggrStandardTipoUdButton().setHidden(true);
        }

        // getForm().getParametriAmministrazioneTipoUdList().getDs_valore_param_applic_tipo_ud_amm().setEditMode();
        // getForm().getParametriConservazioneTipoUdList().getDs_valore_param_applic_tipo_ud_cons().setEditMode();
        // getForm().getParametriGestioneTipoUdList().getDs_valore_param_applic_tipo_ud_gest().setEditMode();
        // getForm().getParametriAmministrazioneTipoUdList().setHideDeleteButton(true);
        // getForm().getParametriConservazioneTipoUdList().setHideDeleteButton(true);
        // getForm().getParametriGestioneTipoUdList().setHideDeleteButton(true);
        // Data primo versamento non editabile
        getForm().getTipoUnitaDoc().getDt_first_vers().setViewMode();

        getForm().getTipoUnitaDocList().setStatus(Status.update);
        getForm().getTipoUnitaDoc().setStatus(Status.update);
    }

    @Override
    public void updateTipoUnitaDocAmmessoList() throws EMFError {
        getForm().getTipoUnitaDocAmmesso().getCreaTipoSerieStandard().setHidden(true);

        getForm().getTipoUnitaDocAmmessoList().setStatus(Status.update);
        getForm().getTipoUnitaDocAmmesso().setStatus(Status.update);
        getForm().getTipoUnitaDocAmmesso().getId_registro_unita_doc().setEditMode();
    }

    @Override
    public void updateTipoUnitaDocAmmesso() throws EMFError {
        getForm().getTipoUnitaDocAmmesso().getCreaTipoSerieStandard().setHidden(true);

        getForm().getTipoUnitaDocAmmessoList().setStatus(Status.update);
        getForm().getTipoUnitaDocAmmesso().setStatus(Status.update);
        getForm().getTipoUnitaDocAmmesso().getId_registro_unita_doc().setEditMode();
        getForm().getTipoUnitaDocAmmesso().getId_tipo_unita_doc().setEditMode();
    }

    @Override
    public void updateRegistroTipoUnitaDocAmmessoList() throws EMFError {
        getForm().getTipoUnitaDocAmmesso().getCreaTipoSerieStandard().setHidden(true);

        getForm().getRegistroTipoUnitaDocAmmessoList().setStatus(Status.update);
        getForm().getTipoUnitaDocAmmesso().setStatus(Status.update);
        getForm().getTipoUnitaDocAmmesso().getId_tipo_unita_doc().setEditMode();
    }

    @Override
    public void updateAaRegistroUnitaDocList() throws EMFError {
        getForm().getAaRegistroUnitaDocList().setStatus(Status.update);
        getForm().getAARegistroUnitaDoc().setStatus(Status.update);

        loadWizard();
    }

    @Override
    public void updateTipoStrutDocAmmessoDaTipoDocList() throws EMFError {
        /* Imposto il valore nella combo */
        BigDecimal idTipoStrutDoc = getForm().getTipoStrutDocAmmessoDaTipoDoc().getId_tipo_strut_doc_ammesso().parse();
        getForm().getTipoStrutDocAmmessoDaTipoDoc().getId_tipo_strut_doc_ammesso().setValue(idTipoStrutDoc.toString());

        getForm().getTipoStrutDocAmmessoDaTipoDoc().getId_tipo_strut_doc_ammesso().setEditMode();
        getForm().getTipoStrutDocAmmessoDaTipoDocList().setStatus(Status.update);
        getForm().getTipoStrutDocAmmessoDaTipoDoc().setStatus(Status.update);
    }

    /**
     * Metodo che inizializza la ComboBox presente nella form relativa a TipoUnita
     */
    private void setTipoUnitaComboBox() {
        setTipoUnitaComboBox(false);
    }

    /**
     * Metodo che inizializza la ComboBox presente nella form relativa a TipoUnita
     */
    private void setTipoUnitaComboBox(boolean isUpdate) {
        getForm().getTipoUnitaDoc().getTi_save_file().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("ti_save_file", CostantiDB.TipoSalvataggioFile.values()));
        if (!isUpdate) {
            getForm().getTipoUnitaDoc().getTi_save_file().setValue(CostantiDB.TipoSalvataggioFile.BLOB.name());
        }

        DecCategTipoUnitaDocTableBean table = tipoUnitaDocEjb.getDecCategTipoUnitaDocTableBean(true);
        DecodeMap mappaTiCateg = new DecodeMap();
        mappaTiCateg.populatedMap(table, "id_categ_tipo_unita_doc", "cd_categ_tipo_unita_doc");
        getForm().getTipoUnitaDoc().getTi_categ_strut().setDecodeMap(mappaTiCateg);

    }

    /**
     * Metodo che visualizza la form associata a DecTipoDoc in status update
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void updateTipoDocList() throws EMFError {
        getForm().getTipoDoc().getFl_tipo_doc_principale().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getTipoDoc().setEditMode();

        // Nascondo il tab (all'interno del quale ci sarà il flag) per la creazione del
        // criterio
        getForm().getCreaCriterioTipoDocSection().setHidden(true);

        DecTipoDocRowBean tipoDocCurrent = (DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow();

        String flTipoDocPrincipale = getForm().getTipoDoc().getFl_tipo_doc_principale().parse();

        // Se non è presente un criterio di raggruppamento per il tipo documento
        // corrente, visualizzo il bottone per la
        // creazione del criterio
        if (flTipoDocPrincipale != null && flTipoDocPrincipale.equals("1") && !critRaggrEjb
                .existsCriterioStandardPerTipoDato(tipoDocCurrent.getIdTipoDoc().longValue(), TipoDato.TIPO_DOC)) {
            getForm().getTipoDocCreazioneCriterio().getCreaCriterioRaggrStandardTipoDocButton().setHidden(false);
        } else {
            getForm().getTipoDocCreazioneCriterio().getCreaCriterioRaggrStandardTipoDocButton().setHidden(true);
        }

        getForm().getTipoDoc().setStatus(Status.update);
        getForm().getTipoDocList().setStatus(Status.update);
    }

    /**
     * Metodo che visualizza la form associata a DecTipoDocAmmesso in status update
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void updateTipoDocAmmessoList() throws EMFError {
        getForm().getTipoDocAmmesso().setEditMode();
        getForm().getTipoDocAmmesso().getNm_tipo_strut_unita_doc().setViewMode();
        getForm().getTipoDocAmmesso().getId_tipo_doc().setViewMode();
        getForm().getTipoDocAmmesso().setStatus(Status.update);
        getForm().getTipoDocAmmessoList().setStatus(Status.update);
    }

    /**
     * Metodo che visualizza la form associata a DecTipoStrutUnitaDoc in status update
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void updateTipoStrutUnitaDocList() throws EMFError {
        getForm().getTipoStrutUnitaDoc().setEditMode();
        getForm().getTipoStrutUnitaDoc().setStatus(Status.update);
        getForm().getTipoStrutUnitaDocList().setStatus(Status.update);
    }

    /**
     * Metodo che cancella l'entitÃ  DecRegistroUnitaDoc corrispondente al record della lista selezionato
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteRegistroUnitaDocList() throws EMFError {
        getMessageBox().clear();
        String lastPublisher = getLastPublisher();
        DecRegistroUnitaDocRowBean registroUnitaDocRowBean = new DecRegistroUnitaDocRowBean();

        ActionMap mappa = (ActionMap) getSession().getAttribute("ActionMap");

        if (mappa != null && Application.Publisher.REGISTRO_UNITA_DOC_DETAIL.equalsIgnoreCase(mappa.getAction())) {
            BigDecimal id = (BigDecimal) mappa.getId();
            BigDecimal idStrut = (BigDecimal) mappa.getAppo();
            registroUnitaDocRowBean = registroEjb.getDecRegistroUnitaDocRowBean(id, idStrut);
        } else {
            registroUnitaDocRowBean = (DecRegistroUnitaDocRowBean) getForm().getRegistroUnitaDocList().getTable()
                    .getCurrentRow();
        }

        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            if (Application.Publisher.REGISTRO_UNITA_DOC_DETAIL.equalsIgnoreCase(param.getNomePagina())) {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            } else {
                StruttureForm form = (StruttureForm) SpagoliteLogUtil.getForm(this);
                param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(form, form.getRegistroUnitaDocList()));
            }
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            registroEjb.deleteDecRegistroUnitaDoc(param, registroUnitaDocRowBean.getIdRegistroUnitaDoc().longValue());
            getMessageBox()
                    .addMessage(new Message(MessageLevel.INF, "Registro Unita' Documentaria eliminato con successo"));
            // Reload list
            DecRegistroUnitaDocTableBean registroUnitaDocTableBean = new DecRegistroUnitaDocTableBean();
            registroUnitaDocTableBean = registroEjb.getDecRegistroUnitaDocTableBean(
                    getForm().getIdList().getId_strut().parse(),
                    getForm().getRegistroUnitaDocList().isFilterValidRecords());

            getForm().getRegistroUnitaDocList().setTable(registroUnitaDocTableBean);
            getForm().getRegistroUnitaDocList().getTable().first();
            getForm().getRegistroUnitaDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            if (Application.Publisher.REGISTRO_UNITA_DOC_DETAIL.equals(lastPublisher) || "".equals(lastPublisher)) {
                goBack();
            }

        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
            if (!Application.Publisher.REGISTRO_UNITA_DOC_DETAIL.equals(lastPublisher)) {
                goBack();
                getSession().removeAttribute("FromRegistroUnitaDocList");
            } else {
                forwardToPublisher(Application.Publisher.REGISTRO_UNITA_DOC_DETAIL);
            }
        }
    }

    /**
     * Metodo che cancella l'entità DecTipoUnitaDoc corrispondente al record della lista selezionato
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteTipoUnitaDocList() throws EMFError {
        getMessageBox().clear();
        String lastPublisher = getLastPublisher();
        BigDecimal idTipoUnitaDoc = ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable()
                .getCurrentRow()).getIdTipoUnitaDoc();

        if (getMessageBox().isEmpty()) {
            try {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null,
                                null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                if (Application.Publisher.CREA_STRUTTURA.equalsIgnoreCase(param.getNomePagina())) {
                    StruttureForm form = (StruttureForm) SpagoliteLogUtil.getForm(this);
                    param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(form, form.getTipoUnitaDocList()));
                } else {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
                }
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                tipoUnitaDocEjb.deleteDecTipoUnitaDoc(param, idTipoUnitaDoc.longValue());
                getMessageBox()
                        .addMessage(new Message(MessageLevel.INF, "Tipo Unita' Documentaria eliminato con successo"));
                final BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
                DecTipoUnitaDocTableBean tipoUnitaDocTableBean = tipoUnitaDocEjb.getDecTipoUnitaDocTableBean(idStrut,
                        getForm().getTipoUnitaDocList().isFilterValidRecords());
                getForm().getTipoUnitaDocList().setTable(tipoUnitaDocTableBean);
                getForm().getTipoUnitaDocList().getTable().first();
                getForm().getTipoUnitaDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                goBack();
            } catch (ParerUserError e) {
                getMessageBox().addError(e.getDescription());
                if (!Application.Publisher.TIPO_UNITA_DOC_DETAIL.equals(lastPublisher)) {
                    goBack();
                } else {
                    forwardToPublisher(Application.Publisher.TIPO_UNITA_DOC_DETAIL);
                }
            }
        }
    }

    /**
     * Metodo che cancella l'entitÃ  DecTipoDoc corrispondente al record della lista selezionato
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteTipoDocList() throws EMFError {
        getMessageBox().clear();
        DecTipoDocRowBean tipoDocRowBean = new DecTipoDocRowBean();
        tipoDocRowBean = (DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow();
        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            if (Application.Publisher.TIPO_DOC_DETAIL.equalsIgnoreCase(param.getNomePagina())) {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            } else {
                StruttureForm form = (StruttureForm) SpagoliteLogUtil.getForm(this);
                param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(form, form.getTipoDocList()));
            }
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            tipoDocEjb.deleteDecTipoDoc(param, tipoDocRowBean.getIdTipoDoc().longValue());
            getMessageBox().addMessage(new Message(MessageLevel.INF, "Tipo Documento eliminato con successo"));
            goBack();
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
            if (!Application.Publisher.TIPO_DOC_DETAIL.equals(getLastPublisher())) {
                goBack();
            } else {
                forwardToPublisher(Application.Publisher.TIPO_DOC_DETAIL);
            }
        }
    }

    @Override
    public void deleteRegistroTipoUnitaDocAmmessoList() {
        getMessageBox().clear();
        String lastPublisher = getLastPublisher();
        DecVLisTiUniDocAmsRowBean VTipoUnitaDocAmmessoRowBean = (DecVLisTiUniDocAmsRowBean) getForm()
                .getRegistroTipoUnitaDocAmmessoList().getTable().getCurrentRow();

        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            if (Application.Publisher.REGISTRO_UNITA_DOC_DETAIL.equalsIgnoreCase(param.getNomePagina())) {
                StrutTipiForm form = (StrutTipiForm) SpagoliteLogUtil.getForm(this);
                param.setNomeAzione(
                        SpagoliteLogUtil.getDetailActionNameDelete(form, form.getRegistroTipoUnitaDocAmmessoList()));
            } else {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            }
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            String azioneModCriterioPerDeleteAssociazione = SpagoliteLogUtil.getButtonActionName(getForm(),
                    getForm().getTipoUnitaDocAmmesso(), getForm().getTipoUnitaDocAmmesso()
                            .getUpdCriterioRaggrByTipoUnitaDocFromAssociazioneDelete().getName());
            String nmPaginaPerCriteri = Application.Publisher.TIPO_UNITA_DOC_AMMESSO_DETAIL;
            tipoUnitaDocEjb.deleteDecTipoUnitaDocAmmesso(param, azioneModCriterioPerDeleteAssociazione,
                    nmPaginaPerCriteri, VTipoUnitaDocAmmessoRowBean.getIdTipoUnitaDocAmmesso());
            getMessageBox().addMessage(
                    new Message(MessageLevel.INF, "Tipo Unita' Documentaria Ammessa eliminata con successo"));
            DecVLisTiUniDocAmsTableBean tipoUnitaDocAmmessoTableBean = tipoUnitaDocEjb
                    .getDecVLisTiUniDocAmsTableBean(VTipoUnitaDocAmmessoRowBean);
            VTipoUnitaDocAmmessoRowBean = new DecVLisTiUniDocAmsRowBean();
            VTipoUnitaDocAmmessoRowBean.setIdRegistroUnitaDoc(
                    ((DecRegistroUnitaDocRowBean) getForm().getRegistroUnitaDocList().getTable().getCurrentRow())
                            .getIdRegistroUnitaDoc());

            getForm().getRegistroTipoUnitaDocAmmessoList().setTable(tipoUnitaDocAmmessoTableBean);
            getForm().getRegistroTipoUnitaDocAmmessoList().getTable().first();
            getForm().getRegistroTipoUnitaDocAmmessoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            if (Application.Publisher.TIPO_UNITA_DOC_AMMESSO_DETAIL.equals(lastPublisher)) {
                goBack();
            } else {
                getForm().getIdList().getId_registro_unita_doc()
                        .setValue(VTipoUnitaDocAmmessoRowBean.getIdRegistroUnitaDoc().toPlainString());
                reloadRegistroLists();
            }
        } catch (Exception e) {
            getMessageBox().addError(e.getMessage());
            if (!Application.Publisher.TIPO_UNITA_DOC_AMMESSO_DETAIL.equals(lastPublisher)) {
                goBack();
            } else {
                forwardToPublisher(Application.Publisher.TIPO_UNITA_DOC_AMMESSO_DETAIL);
            }
        }
    }

    @Override
    public void deleteTipoUnitaDocAmmessoList() {

        getMessageBox().clear();

        DecVLisTiUniDocAmsRowBean VTipoUnitaDocAmmessoRowBean = (DecVLisTiUniDocAmsRowBean) getForm()
                .getTipoUnitaDocAmmessoList().getTable().getCurrentRow();

        String lastPublisher = getLastPublisher();
        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            if (Application.Publisher.TIPO_UNITA_DOC_DETAIL.equalsIgnoreCase(param.getNomePagina())) {
                StrutTipiForm form = (StrutTipiForm) SpagoliteLogUtil.getForm(this);
                param.setNomeAzione(
                        SpagoliteLogUtil.getDetailActionNameDelete(form, form.getTipoUnitaDocAmmessoList()));
            } else {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            }
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            String azioneModCriterioPerDeleteAssociazione = SpagoliteLogUtil.getButtonActionName(getForm(),
                    getForm().getTipoUnitaDocAmmesso(), getForm().getTipoUnitaDocAmmesso()
                            .getUpdCriterioRaggrByTipoUnitaDocFromAssociazioneDelete().getName());
            String nmPaginaPerCriteri = Application.Publisher.TIPO_UNITA_DOC_AMMESSO_DETAIL;
            tipoUnitaDocEjb.deleteDecTipoUnitaDocAmmesso(param, azioneModCriterioPerDeleteAssociazione,
                    nmPaginaPerCriteri, VTipoUnitaDocAmmessoRowBean.getIdTipoUnitaDocAmmesso());
            getMessageBox().addMessage(new Message(MessageLevel.INF,
                    "Associazione tipologia unita' documentaria - tipo registro eliminata con successo"));
            VTipoUnitaDocAmmessoRowBean = new DecVLisTiUniDocAmsRowBean();
            VTipoUnitaDocAmmessoRowBean.setIdTipoUnitaDoc(
                    ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                            .getIdTipoUnitaDoc());
            DecVLisTiUniDocAmsTableBean tipoUnitaDocAmmessoTableBean = tipoUnitaDocEjb
                    .getDecVLisTiUniDocAmsTableBean(VTipoUnitaDocAmmessoRowBean);
            getForm().getTipoUnitaDocAmmessoList().setTable(tipoUnitaDocAmmessoTableBean);
            getForm().getTipoUnitaDocAmmessoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            if (Application.Publisher.TIPO_UNITA_DOC_AMMESSO_DETAIL.equals(lastPublisher)) {
                goBack();
            } else {
                loadTipoUnitaDoclists(false);
            }
        } catch (Exception e) {
            getMessageBox().addError(e.getLocalizedMessage(), e);
            if (!Application.Publisher.TIPO_UNITA_DOC_AMMESSO_DETAIL.equals(lastPublisher)) {
                goBack();
                getSession().removeAttribute("FromTipoUnitaDocAmmessoList");
            } else {
                forwardToPublisher(Application.Publisher.TIPO_UNITA_DOC_AMMESSO_DETAIL);
            }
        }
    }

    @Override
    public void deleteTipoStrutUnitaDocList() throws EMFError {

        getMessageBox().clear();
        String lastPublisher = getLastPublisher();
        DecTipoStrutUnitaDocRowBean tipoStrutUnitaDocRowBean = (DecTipoStrutUnitaDocRowBean) getForm()
                .getTipoStrutUnitaDocList().getTable().getCurrentRow();
        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));

            StrutTipiForm form = (StrutTipiForm) SpagoliteLogUtil.getForm(this);
            if (Application.Publisher.TIPO_UNITA_DOC_DETAIL.equalsIgnoreCase(param.getNomePagina())) {
                param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(form, form.getTipoStrutUnitaDocList()));
            } else {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            }
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            tipoUnitaDocEjb.deleteDecTipoStrutUnitaDoc(param, tipoStrutUnitaDocRowBean);
            final BigDecimal idTipoUnitaDoc = ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable()
                    .getCurrentRow()).getIdTipoUnitaDoc();
            DecTipoStrutUnitaDocTableBean tipoStrutUnitaDocTableBean = tipoStrutDocEjb.getDecTipoStrutUnitaDocTableBean(
                    idTipoUnitaDoc, getForm().getTipoStrutUnitaDocList().isFilterValidRecords());
            getMessageBox().addMessage(new Message(MessageLevel.INF, "Tipo Struttura eliminato con successo"));
            getForm().getTipoStrutUnitaDocList().setTable(tipoStrutUnitaDocTableBean);
            getForm().getTipoStrutUnitaDocList().getTable().first();
            getForm().getTipoStrutUnitaDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            if (Application.Publisher.TIPO_STRUT_UNITA_DOC_DETAIL.equals(lastPublisher) || "".equals(lastPublisher)) {
                goBack();
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
            if (StringUtils.isNotBlank(lastPublisher)) {
                forwardToPublisher(lastPublisher);
            } else {
                forwardToPublisher(Application.Publisher.TIPO_UNITA_DOC_DETAIL);
            }
        }
    }

    @Override
    public void deleteTipoDocAmmessoList() throws EMFError {

        getMessageBox().clear();
        String lastPublisher = getLastPublisher();

        DecTipoDocAmmessoRowBean tipoDocAmmessoRowBean = (DecTipoDocAmmessoRowBean) getForm().getTipoDocAmmessoList()
                .getTable().getCurrentRow();

        try {

            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            if (Application.Publisher.TIPO_STRUT_UNITA_DOC_DETAIL.equalsIgnoreCase(param.getNomePagina())) {
                StrutTipiForm form = (StrutTipiForm) SpagoliteLogUtil.getForm(this);
                param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(form, form.getTipoDocAmmessoList()));
            } else {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            }
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            tipoUnitaDocEjb.deleteDecTipoDocAmmesso(param, tipoDocAmmessoRowBean);
            getMessageBox().addMessage(new Message(MessageLevel.INF, "Tipo documento ammesso eliminato con successo"));
            DecTipoDocAmmessoTableBean tipoDocAmmessoTableBean = tipoUnitaDocEjb.getDecTipoDocAmmessoTableBean(
                    ((DecTipoStrutUnitaDocRowBean) getForm().getTipoStrutUnitaDocList().getTable().getCurrentRow())
                            .getIdTipoStrutUnitaDoc());
            getForm().getTipoDocAmmessoList().setTable(tipoDocAmmessoTableBean);
            getForm().getTipoDocAmmessoList().getTable().first();
            getForm().getTipoDocAmmessoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

            if (Application.Publisher.TIPO_DOC_AMMESSO_DETAIL.equals(lastPublisher)) {
                goBack();
            }

        } catch (Exception e) {
            getMessageBox().addError(e.getLocalizedMessage());
            if (!Application.Publisher.TIPO_DOC_AMMESSO_DETAIL.equals(lastPublisher)) {
                goBack();

            } else {
                forwardToPublisher(Application.Publisher.TIPO_DOC_AMMESSO_DETAIL);
            }
        }
    }

    @Override
    public void deleteAaRegistroUnitaDocList() throws EMFError {
        getMessageBox().clear();
        String lastPublisher = getLastPublisher();

        BigDecimal idRegistroUnitaDoc = ((DecRegistroUnitaDocRowBean) getForm().getRegistroUnitaDocList().getTable()
                .getCurrentRow()).getIdRegistroUnitaDoc();
        DecAaRegistroUnitaDocRowBean aaRegistroUnitaDocRowBean = ((DecAaRegistroUnitaDocRowBean) getForm()
                .getAaRegistroUnitaDocList().getTable().getCurrentRow());
        aaRegistroUnitaDocRowBean.setIdRegistroUnitaDoc(idRegistroUnitaDoc);

        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            if (Application.Publisher.REGISTRO_UNITA_DOC_DETAIL.equalsIgnoreCase(param.getNomePagina())) {
                StrutTipiForm form = (StrutTipiForm) SpagoliteLogUtil.getForm(this);
                param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(form, form.getAaRegistroUnitaDocList()));
            } else {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            }
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            registroEjb.deleteDecAaRegistroUnitaDoc(param, aaRegistroUnitaDocRowBean);
            setTableName(getForm().getRegistroUnitaDocList().getName());
            loadDettaglio();
            getMessageBox()
                    .addMessage(new Message(MessageLevel.INF, "Periodo di validità registro eliminato con successo"));

            if (Application.Publisher.AA_REGISTRO_UNITA_DOC_DETAIL.equals(lastPublisher)) {
                goBack();
            }
        } catch (Exception e) {
            getMessageBox().addError(e.getLocalizedMessage());
            if (!Application.Publisher.AA_REGISTRO_UNITA_DOC_DETAIL.equals(lastPublisher)) {
                goBack();
            } else {
                forwardToPublisher(Application.Publisher.AA_REGISTRO_UNITA_DOC_DETAIL);
            }
        }
    }

    /**
     *
     * Metodo per il salvataggio o la modifica di un'entita' RegistroUnitaDoc
     *
     * @throws EMFError
     *             errore generico
     */
    private void salvaRegistroUnitaDoc() throws EMFError {

        getMessageBox().clear();

        DecRegistroUnitaDocRowBean registroUnitaDocRowBean = new DecRegistroUnitaDocRowBean();
        RegistroUnitaDoc registroUnitaDoc = getForm().getRegistroUnitaDoc();
        RegistroCreazioneCriterio creazioneCriterio = getForm().getRegistroCreazioneCriterio();
        registroUnitaDoc.post(getRequest());
        creazioneCriterio.post(getRequest());

        if (registroUnitaDoc.validate(getMessageBox())) {

            if (registroUnitaDoc.getCd_registro_unita_doc().parse() == null) {
                getMessageBox().addError("Errore di compilazione form: codice registro non inserito<br/>");
            } else {
                // Controllo che il nome struttura rispetti
                FileServUtils fsu = new FileServUtils();
                if (!fsu.controllaSubPath(registroUnitaDoc.getCd_registro_normaliz().parse())) {
                    getMessageBox().addError(
                            "Errore di compilazione form: Tipo registro contenente caratteri non permessi <br/>");
                } else {
                    /*
                     * Controllo che la lunghezza totale, tenendo presente anche ambiente, ente e struttura, sia minore
                     * o uguale a 100 caratteri (aggiungo 3 che è il numero di underscores di separazione)
                     */
                    int lunghezzaAmbEnteStrutReg = getForm().getStrutRif().getNm_ambiente().parse().length()
                            + getForm().getStrutRif().getId_ente().parse().length()
                            + getForm().getStrutRif().getNm_strut().parse().length()
                            + getForm().getRegistroUnitaDoc().getCd_registro_unita_doc().parse().length() + 3;

                    if (lunghezzaAmbEnteStrutReg > 100) {
                        getMessageBox().addError(
                                "Errore inserimento Registro: lunghezza nome ambiente + nome ente + nome struttura + tipo registro superiore a 100 caratteri <br/>");
                    }
                }
            }

            if (registroUnitaDoc.getDs_registro_unita_doc().parse() == null) {
                getMessageBox().addError("Errore di compilazione form: descrizione registro non inserito<br/>");
            }
            if (registroUnitaDoc.getDt_istituz().parse() == null) {
                getMessageBox().addError("Errore di compilazione form: data istituzione non inserita<br/>");
            }
            if (registroUnitaDoc.getDt_soppres().parse() == null) {
                registroUnitaDoc.getDt_soppres().setValue(getDefaultDate());
            }

            String registroFiscale = registroUnitaDoc.getFl_registro_fisc().parse();

            if (StringUtils.isBlank(registroFiscale)) {
                getMessageBox()
                        .addError("Errore di compilazione form:  selezionare un valore per la voce \"Fiscale\"<br/>");
            }
            if (registroUnitaDoc.getDt_istituz().parse() != null && registroUnitaDoc.getDt_soppres().parse() != null
                    && registroUnitaDoc.getDt_istituz().parse().after(registroUnitaDoc.getDt_soppres().parse())) {
                getMessageBox()
                        .addError("Errore di compilazione form: data soppressione precedente a data istituzione<br/>");
            }
            if (registroUnitaDoc.getFl_crea_serie().getValue().equals("1")) {
                checkAnniConservazioneIllimitata(registroUnitaDoc.getConserv_unlimited().parse(),
                        registroUnitaDoc.getNi_anni_conserv().parse());
            } else {
                registroUnitaDoc.getNi_anni_conserv().setValue(null);
            }

            try {
                BigDecimal idStrut = (BigDecimal) getSession().getAttribute("id_struttura_lavorato");
                if (idStrut == null) {
                    idStrut = getForm().getIdList().getId_strut().parse();
                }
                BigDecimal idRegistroUnitaDoc = null;
                String cdRegistroUnitaDoc = null;
                if (getForm().getRegistroUnitaDoc().getStatus().equals(Status.update)) {
                    DecRegistroUnitaDocRowBean registroRow = ((DecRegistroUnitaDocRowBean) getForm()
                            .getRegistroUnitaDocList().getTable().getCurrentRow());
                    idRegistroUnitaDoc = registroRow.getIdRegistroUnitaDoc();
                    cdRegistroUnitaDoc = registroRow.getCdRegistroUnitaDoc();
                }

                // Controlli sulla configurazione serie
                boolean creaTipoSerieStandard = registroUnitaDoc.getFl_crea_tipo_serie_standard().parse() != null
                        ? registroUnitaDoc.getFl_crea_tipo_serie_standard().parse().equals("1") : false;
                String tipoSerie = registroUnitaDoc.getNm_tipo_serie_da_creare().parse();
                String descrizioneTipoSerie = registroUnitaDoc.getDs_tipo_serie_da_creare().parse();
                String codiceSerie = registroUnitaDoc.getCd_serie_da_creare().parse();
                String descrizioneSerie = registroUnitaDoc.getDs_serie_da_creare().parse();
                BigDecimal idModelloTipoSerie = registroUnitaDoc.getId_modello_tipo_serie().parse();
                if (!getMessageBox().hasError() && creaTipoSerieStandard) {
                    if (StringUtils.isNotBlank(codiceSerie)) {
                        if (!CD_SERIE_PATTERN.matcher(codiceSerie).matches()) {
                            getMessageBox().addError("Caratteri consentiti per il campo '"
                                    + registroUnitaDoc.getCd_serie_da_creare().getDescription()
                                    + "': lettere, numeri,.,-,_,: <br/>");
                        }
                    }
                    if (idModelloTipoSerie != null) {
                        modelliSerieEjb.checkModelloTipoUdRegistroPerSerie(idStrut, idModelloTipoSerie, tipoSerie,
                                descrizioneTipoSerie, codiceSerie, descrizioneSerie, idRegistroUnitaDoc, null);
                    } else {
                        getMessageBox().addError(
                                "\u00C8 stato indicato che il registro sia utilizzato per la creazione di serie standard; \u00E8 necessario indicare il modello da utilizzare");
                    }
                }
                if (!getMessageBox().hasError() && creaTipoSerieStandard) {
                    if (idRegistroUnitaDoc != null) {
                        boolean checkTipiUdAmmessi = registroEjb
                                .checkTipoSerieStandardForTipiUdAmmessi(idRegistroUnitaDoc);
                        if (checkTipiUdAmmessi) {
                            getMessageBox().addError(
                                    "Le tipologie di unit\u00E0 documentaria cui il registro \u00E8 associato hanno dei valori di creazione del tipo serie standard non compatibili con il registro. Impossibile eseguire la modifica");
                        }
                    }
                } else if (!getMessageBox().hasError() && !creaTipoSerieStandard) {
                    if (idModelloTipoSerie != null) {
                        getMessageBox().addError(
                                "\u00C8 stato indicato che il registro non sia utilizzato per la creazione di serie standard; Il modello di serie non deve essere valorizzato");
                    }
                    if (idRegistroUnitaDoc != null) {
                        boolean checkTipiUdAmmessi = registroEjb
                                .checkTipoSerieStandardForTipiUdAmmessi(idRegistroUnitaDoc);
                        if (StringUtils.isNotBlank(tipoSerie) && !checkTipiUdAmmessi) {
                            getMessageBox().addError("Il campo '"
                                    + registroUnitaDoc.getNm_tipo_serie_da_creare().getDescription()
                                    + "' \u00E8 stato valorizzato ma il registro non prevede la creazione di serie standard e nessun tipo di unit\u00E0 documentaria cui il registro \u00E8 associato prevede la creazione di serie standard");
                        }
                        if (StringUtils.isNotBlank(descrizioneTipoSerie) && !checkTipiUdAmmessi) {
                            getMessageBox().addError("Il campo '"
                                    + registroUnitaDoc.getDs_tipo_serie_da_creare().getDescription()
                                    + "' \u00E8 stato valorizzato ma il registro non prevede la creazione di serie standard e nessun tipo di unit\u00E0 documentaria cui il registro \u00E8 associato prevede la creazione di serie standard");
                        }
                        if (StringUtils.isNotBlank(codiceSerie) && !checkTipiUdAmmessi) {
                            getMessageBox().addError("Il campo '"
                                    + registroUnitaDoc.getCd_serie_da_creare().getDescription()
                                    + "' \u00E8 stato valorizzato ma il registro non prevede la creazione di serie standard e nessun tipo di unit\u00E0 documentaria cui il registro \u00E8 associato prevede la creazione di serie standard");
                        }
                        if (StringUtils.isNotBlank(descrizioneSerie) && !checkTipiUdAmmessi) {
                            getMessageBox().addError("Il campo '"
                                    + registroUnitaDoc.getDs_serie_da_creare().getDescription()
                                    + "' \u00E8 stato valorizzato ma il registro non prevede la creazione di serie standard e nessun tipo di unit\u00E0 documentaria cui il registro \u00E8 associato prevede la creazione di serie standard");
                        }
                    }
                }

                if (!getMessageBox().hasError()) {

                    registroUnitaDoc.copyToBean(registroUnitaDocRowBean);
                    registroUnitaDocRowBean.setIdStrut(idStrut);
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null,
                                    null, null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    if (getForm().getRegistroUnitaDoc().getStatus().equals(Status.insert)) {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarSave(false));
                        registroEjb.insertDecRegistroUnitaDoc(param, registroUnitaDocRowBean,
                                creazioneCriterio.getCriterio_autom_registro().parse());
                        getForm().getRegistroTipoUnitaDocAmmessoList().clear();
                        getForm().getRegistroUnitaDocList().getTable().last();
                        registroUnitaDocRowBean = registroEjb.getDecRegistroUnitaDocRowBean(
                                registroUnitaDocRowBean.getCdRegistroUnitaDoc(), registroUnitaDocRowBean.getIdStrut());
                        getForm().getIdList().getId_registro_unita_doc()
                                .setValue(registroUnitaDocRowBean.getIdRegistroUnitaDoc().toPlainString());
                        DecRegistroUnitaDocTableBean rudTable = new DecRegistroUnitaDocTableBean();
                        rudTable.add(registroUnitaDocRowBean);
                        getForm().getRegistroUnitaDocList().setTable(rudTable);
                        getForm().getRegistroUnitaDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                        getForm().getRegistroUnitaDocList().getTable().setCurrentRowIndex(0);
                        getMessageBox()
                                .addMessage(new Message(MessageLevel.INF, "Nuovo registro salvato con successo"));
                        getMessageBox().setViewMode(ViewMode.plain);
                        // Ricarico tutto il registro e i suoi figli
                        registroUnitaDocRowBean = registroEjb.getDecRegistroUnitaDocRowBean(
                                registroUnitaDocRowBean.getCdRegistroUnitaDoc(), registroUnitaDocRowBean.getIdStrut());
                        getForm().getIdList().getId_registro_unita_doc()
                                .setValue(registroUnitaDocRowBean.getIdRegistroUnitaDoc().toPlainString());
                        reloadRegistroLists();
                    } else if (getForm().getRegistroUnitaDoc().getStatus().equals(Status.update)) {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarSave(true));
                        eseguiStepControlloTipiSerieSalvataggioModificaRegistro(param, null, idStrut,
                                idRegistroUnitaDoc, cdRegistroUnitaDoc, registroUnitaDocRowBean, creazioneCriterio);
                    }
                }
            } catch (ParerUserError e) {
                setActionToSession(null, true);
                getSession().removeAttribute("id_registro_lavorato");
                getSession().removeAttribute("salvataggioAttributesCreazioneCriteriDaControlliModificaRegistro");
                getMessageBox().addError(e.getDescription());
            }
        }
        forwardToPublisher(Application.Publisher.REGISTRO_UNITA_DOC_DETAIL);
    }

    private void eseguiStepControlloTipiSerieSalvataggioModificaRegistro(LogParam param, String nmAzionePerCriteri,
            BigDecimal idStrut, BigDecimal idRegistroUnitaDoc, String cdRegistroUnitaDoc,
            DecRegistroUnitaDocRowBean registroUnitaDocRowBean, RegistroCreazioneCriterio creazioneCriterio)
            throws EMFError {
        try {
            // Devo mostrare un popup. Purtroppo il punto più "decente" è questo. Fa schifo
            // e lo sappiamo entrambi.
            if (cdRegistroUnitaDoc != null
                    && !cdRegistroUnitaDoc.equals(registroUnitaDocRowBean.getCdRegistroUnitaDoc())) {
                String message = registroEjb.checkEditRegistroForTipiSerie(idStrut, idRegistroUnitaDoc,
                        registroUnitaDocRowBean.getCdRegistroUnitaDoc());
                if (StringUtils.isNotBlank(message)) {
                    getRequest().setAttribute("nmAzionePerCriteri", nmAzionePerCriteri);
                    throw new ParerWarningException(message, "warningModificaSerie");
                }
            }
            String azioneInsCriterio = SpagoliteLogUtil.getButtonActionName(getForm(), getForm().getRegistroUnitaDoc(),
                    getForm().getRegistroUnitaDoc().getInsCriterioRaggrByTipoUnitaDocFromReg().getName());
            String azioneModCriterio = SpagoliteLogUtil.getButtonActionName(getForm(), getForm().getRegistroUnitaDoc(),
                    getForm().getRegistroUnitaDoc().getUpdCriterioRaggrByTipoUnitaDocFromReg().getName());
            registroEjb.updateDecRegistroUnitaDoc(param, azioneInsCriterio, azioneModCriterio, idRegistroUnitaDoc,
                    registroUnitaDocRowBean, creazioneCriterio.getCriterio_autom_registro().parse());
            getMessageBox().addMessage(new Message(MessageLevel.INF, "Registro modificato con successo"));
            getMessageBox().setViewMode(ViewMode.plain);
            // Ricarico tutto il registro e i suoi figli
            registroUnitaDocRowBean = registroEjb.getDecRegistroUnitaDocRowBean(
                    registroUnitaDocRowBean.getCdRegistroUnitaDoc(), registroUnitaDocRowBean.getIdStrut());
            getForm().getIdList().getId_registro_unita_doc()
                    .setValue(registroUnitaDocRowBean.getIdRegistroUnitaDoc().toPlainString());
            reloadRegistroLists();
            String maxLenNumero = registroEjb.calcolaMaxLenNumeroChiave(registroUnitaDocRowBean);
            this.setMaxLenNumeroChiaveToSession(maxLenNumero);
            getForm().getRegistroUnitaDoc().getMax_len_numero().setValue(maxLenNumero);
        } catch (ParerUserError e) {
            setActionToSession(null, true);
            getSession().removeAttribute("id_registro_lavorato");
            getMessageBox().addError(e.getDescription());
        } catch (ParerWarningException ex) {
            switch ((String) ex.getAdditionalInfo()) {
            case "warningModificaSerie":
                // warning tipi serie
                getRequest().setAttribute("warningModificaSerie", ex.getDescription());
                break;
            case "warningModificaRegistro":
                getMessageBox().setViewMode(ViewMode.alert);
                // Ricarico tutto il registro e i suoi figli
                registroUnitaDocRowBean = registroEjb.getDecRegistroUnitaDocRowBean(
                        registroUnitaDocRowBean.getCdRegistroUnitaDoc(), registroUnitaDocRowBean.getIdStrut());
                getForm().getIdList().getId_registro_unita_doc()
                        .setValue(registroUnitaDocRowBean.getIdRegistroUnitaDoc().toPlainString());
                reloadRegistroLists();
                String maxLenNumero = registroEjb.calcolaMaxLenNumeroChiave(registroUnitaDocRowBean);
                this.setMaxLenNumeroChiaveToSession(maxLenNumero);
                getForm().getRegistroUnitaDoc().getMax_len_numero().setValue(maxLenNumero);
                getMessageBox().addWarning(ex.getDescription());
                break;
            }
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

    /**
     * Metodo per il salvataggio o la modifica di un'entita' TipoUnitaDoc
     *
     * @throws EMFError
     *             errore generico
     */
    private void salvaTipoUnitaDoc() throws EMFError {

        // getForm().getParametriAmministrazioneTipoUdList().post(getRequest());
        // getForm().getParametriConservazioneTipoUdList().post(getRequest());
        // getForm().getParametriGestioneTipoUdList().post(getRequest());
        getMessageBox().clear();

        DecTipoUnitaDocRowBean tipoUnitaDocRowBean = new DecTipoUnitaDocRowBean();

        TipoUnitaDoc tipoUnitaDoc = getForm().getTipoUnitaDoc();
        TipoUnitaDocCreazioneCriterio creazioneCriterio = getForm().getTipoUnitaDocCreazioneCriterio();
        tipoUnitaDoc.post(getRequest());
        creazioneCriterio.post(getRequest());

        if (tipoUnitaDoc.validate(getMessageBox())) {

            BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
            if (tipoUnitaDoc.getNm_tipo_unita_doc().parse() == null) {
                getMessageBox().addError(
                        "Errore di compilazione form: nome tipologia unit\u00E0 documentaria non inserito<br/>");
            }
            if (tipoUnitaDoc.getDs_tipo_unita_doc().parse() == null) {
                getMessageBox().addError(
                        "Errore di compilazione form: descrizione tipologia unit\u00E0 documentaria non inserito<br/>");
            }
            if (tipoUnitaDoc.getDt_istituz().parse() == null) {
                getMessageBox().addError("Errore di compilazione form: data istituzione non inserita<br/>");
            }
            if (tipoUnitaDoc.getTi_save_file().parse() == null) {
                getMessageBox().addError("Errore di compilazione form: tipo salvataggio file non inserito<br/>");
            }
            if (tipoUnitaDoc.getDt_soppres().parse() == null) {

                tipoUnitaDoc.getDt_soppres().setValue(getDefaultDate());
            }
            if (tipoUnitaDoc.getDt_istituz().parse() != null && tipoUnitaDoc.getDt_istituz().parse() != null
                    && tipoUnitaDoc.getDt_istituz().parse().after(tipoUnitaDoc.getDt_soppres().parse())) {
                getMessageBox()
                        .addError("Errore di compilazione form: data soppressione precedente a data istituzione<br/>");
            }
            if (tipoUnitaDoc.getNm_categ_strut().parse() == null) {
                getMessageBox().addError("Errore di compilazione form: inserire categoria tipo unit\u00E0<br/>");
            }

            try {

                BigDecimal idTipoUnitaDoc = null;
                if (getForm().getTipoUnitaDoc().getStatus().equals(Status.update)) {
                    idTipoUnitaDoc = ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable()
                            .getCurrentRow()).getIdTipoUnitaDoc();
                }
                // Controlli sulla configurazione serie
                boolean creaTipoSerieStandard = tipoUnitaDoc.getFl_crea_tipo_serie_standard().parse() != null
                        ? tipoUnitaDoc.getFl_crea_tipo_serie_standard().parse().equals("1") : false;
                if (!getMessageBox().hasError() && creaTipoSerieStandard) {
                    BigDecimal idModelloTipoSerie = tipoUnitaDoc.getId_modello_tipo_serie().parse();
                    String codiceSerie = tipoUnitaDoc.getCd_serie_da_creare().parse();
                    if (StringUtils.isNotBlank(codiceSerie)) {
                        if (!CD_SERIE_PATTERN.matcher(codiceSerie).matches()) {
                            getMessageBox().addError("Caratteri consentiti per il campo '"
                                    + tipoUnitaDoc.getCd_serie_da_creare().getDescription()
                                    + "': lettere, numeri,.,-,_,: <br/>");
                        }
                    }
                    if (idModelloTipoSerie != null) {
                        String tipoSerie = tipoUnitaDoc.getNm_tipo_serie_da_creare().parse();
                        String descrizioneTipoSerie = tipoUnitaDoc.getDs_tipo_serie_da_creare().parse();
                        String descrizioneSerie = tipoUnitaDoc.getDs_serie_da_creare().parse();
                        modelliSerieEjb.checkModelloTipoUdRegistroPerSerie(idStrut, idModelloTipoSerie, tipoSerie,
                                descrizioneTipoSerie, codiceSerie, descrizioneSerie, null, idTipoUnitaDoc);
                    } else {
                        getMessageBox().addError(
                                "\u00C8 stato indicato che la tipologia di unit\u00E0 documentaria sia utilizzata per la creazione di serie standard; \u00E8 necessario indicare il modello da utilizzare");
                    }
                }
                if (!getMessageBox().hasError() && creaTipoSerieStandard) {
                    if (idTipoUnitaDoc != null) {
                        boolean checkRegistriAmmessi = tipoUnitaDocEjb
                                .checkTipoSerieStandardForRegistriAmmessi(idTipoUnitaDoc);
                        if (checkRegistriAmmessi) {
                            getMessageBox().addError(
                                    "Il tipo di unit\u00E0 documentaria \u00E8 associato a dei registri che hanno dei valori di creazione del tipo serie standard non compatibili tra loro. Impossibile eseguire la modifica");
                        }
                    }
                }

                // // Controllo valori possibili su tipo unità documentaria
                // AplParamApplicTableBean parametriAmministrazione = (AplParamApplicTableBean)
                // getForm().getParametriAmministrazioneTipoUdList().getTable();
                // AplParamApplicTableBean parametriConservazione = (AplParamApplicTableBean)
                // getForm().getParametriConservazioneTipoUdList().getTable();
                // AplParamApplicTableBean parametriGestione = (AplParamApplicTableBean)
                // getForm().getParametriGestioneTipoUdList().getTable();
                // String error = amministrazioneEjb.checkParametriAmmessi("tipo_ud",
                // parametriAmministrazione,
                // parametriConservazione, parametriGestione);
                // if (error != null) {
                // getMessageBox().addError(error);
                // }
                if (getMessageBox().isEmpty()) {

                    tipoUnitaDoc.copyToBean(tipoUnitaDocRowBean);

                    tipoUnitaDocRowBean.setIdCategTipoUnitaDoc(tipoUnitaDoc.getNm_categ_strut().parse());
                    tipoUnitaDocRowBean.setIdStrut(idStrut);

                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null,
                                    null, null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this), SpagoliteLogUtil
                                    .getToolbarSave(getForm().getTipoUnitaDoc().getStatus().equals(Status.update)));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    if (getForm().getTipoUnitaDoc().getStatus().equals(Status.insert)) {

                        tipoUnitaDocEjb.insertDecTipoUnitaDoc(param, tipoUnitaDocRowBean,
                                creazioneCriterio.getCriterio_autom_tipo_ud().parse());
                        tipoUnitaDocRowBean = tipoUnitaDocEjb
                                .getDecTipoUnitaDocRowBean(tipoUnitaDocRowBean.getNmTipoUnitaDoc(), idStrut);
                        getForm().getTipoUnitaDocAmmessoList().clear();
                        getForm().getTipoStrutUnitaDocList().clear();
                        getMessageBox().addMessage(new Message(MessageLevel.INF,
                                "Nuova tipologia unit\u00E0 documentaria salvata con successo"));

                        DecTipoUnitaDocTableBean table = new DecTipoUnitaDocTableBean();
                        table.add(tipoUnitaDocRowBean);
                        getForm().getTipoUnitaDocList().setTable(table);
                        getForm().getTipoUnitaDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                        getForm().getTipoUnitaDocList().getTable().setCurrentRowIndex(0);
                        getForm().getIdList().getId_tipo_unita_doc()
                                .setValue(tipoUnitaDocRowBean.getIdTipoUnitaDoc().toPlainString());
                        loadTipoUnitaDoclists(true);

                    } else if (getForm().getTipoUnitaDoc().getStatus().equals(Status.update)) {
                        tipoUnitaDocEjb.updateDecTipoUnitaDoc(param, idTipoUnitaDoc, tipoUnitaDocRowBean,
                                creazioneCriterio.getCriterio_autom_tipo_ud().parse());
                        getMessageBox().addMessage(new Message(MessageLevel.INF,
                                "Tipologia unit\u00E0 documentaria modificata con successo"));
                        getForm().getIdList().getId_tipo_unita_doc().setValue(idTipoUnitaDoc.toPlainString());
                        loadTipoUnitaDoclists(true);
                    }

                    getForm().getTipoUnitaDoc().setViewMode();
                    getForm().getTipoUnitaDoc().setStatus(Status.view);
                    getForm().getTipoUnitaDocList().setStatus(Status.view);
                    getForm().getTipoUnitaDoc().getLogEventiTipoUD().setEditMode();
                    getMessageBox().setViewMode(ViewMode.plain);
                }
            } catch (ParerUserError e) {
                getMessageBox().addError(e.getDescription());
            }
        }
        forwardToPublisher(Application.Publisher.TIPO_UNITA_DOC_DETAIL);
    }

    private boolean inValoriPossibili(String dsValoreParamApplicEnte, String dsListaValoriAmmessi) {
        String[] tokens = dsListaValoriAmmessi.split("\\|");
        Set<String> mySet = new HashSet<String>(Arrays.asList(tokens));
        return mySet.contains(dsValoreParamApplicEnte);
    }

    /**
     * Metodo per il salvataggio o la modifica di un'entita' DecTipoDocAmmesso
     *
     * @throws EMFError
     *             errore generico
     */
    private void salvaTipoDocAmmesso() throws EMFError {

        getMessageBox().clear();
        DecTipoDocAmmessoRowBean tipoDocAmmessoRowBean = new DecTipoDocAmmessoRowBean();

        BigDecimal idTipoStrutUnitaDoc = ((DecTipoStrutUnitaDocRowBean) getForm().getTipoStrutUnitaDocList().getTable()
                .getCurrentRow()).getIdTipoStrutUnitaDoc();

        TipoDocAmmesso tipoDocAmmesso = getForm().getTipoDocAmmesso();
        tipoDocAmmesso.post(getRequest());

        if (tipoDocAmmesso.getTi_doc().parse() == null) {
            getMessageBox().addError("Errore di compilazione form: tipo documento<br/>");
        }
        if (tipoDocAmmesso.getId_tipo_doc().parse() == null) {
            getMessageBox().addError("Errore di compilazione form: tipo documento non inserito<br/>");
        }
        if (getForm().getTipoDocAmmesso().getStatus().equals(Status.insert)
                && tipoUnitaDocEjb.existsDecTipoDocAmmesso(null, tipoDocAmmesso.getId_tipo_doc().parse(),
                        idTipoStrutUnitaDoc, tipoDocAmmesso.getTi_doc().parse())) {
            getMessageBox().addError(
                    "Errore di compilazione form: tipo documento gi\u00E0 presente nella struttura di unit\u00E0 documentaria con lo stesso tipo elemento<br/>");
        }

        // I flag devono essere inizializzati
        tipoDocAmmesso.copyToBean(tipoDocAmmessoRowBean);

        DecTipoDocAmmessoTableBean tb = (DecTipoDocAmmessoTableBean) getForm().getTipoDocAmmessoList().getTable();
        int currentRowIndex = tb.getCurrentRowIndex();
        // Controllo, in caso di documento principale, che il flag obbligatorio sia
        // checkato
        if (tipoDocAmmesso.getTi_doc().parse().equals("PRINCIPALE")) {
            if (tipoDocAmmessoRowBean.getFlObbl().equals("0")) {
                getMessageBox().addError(
                        "Errore di compilazione form: l'elemento principale deve essere impostato come obbligatorio<br/>");
            }
        } else {
            // Controllo che, se il documento non è principale, il tipo documento sia
            // diverso da quello principale (se
            // esiste)
            List<Object> toList = tb.toList(DecTipoDocAmmessoTableDescriptor.COL_TI_DOC);
            BigDecimal rowPrincipale = null;
            if (toList.contains("PRINCIPALE")) {
                rowPrincipale = new BigDecimal(toList.indexOf("PRINCIPALE"));
            }
            if (rowPrincipale != null) {
                BigDecimal idTipoDoc = tb.getRow(rowPrincipale.intValue()).getIdTipoDoc();
                if (idTipoDoc.compareTo(tipoDocAmmessoRowBean.getIdTipoDoc()) == 0) {
                    getMessageBox().addError(
                            "Errore di compilazione form: il tipo documento deve essere diverso da quello del documento principale");
                }
            }
        }

        try {
            if (getMessageBox().isEmpty()) {

                tipoDocAmmessoRowBean.setIdTipoStrutUnitaDoc(idTipoStrutUnitaDoc);
                getForm().getTipoDocAmmessoList().getTable().setCurrentRowIndex(currentRowIndex);
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null,
                                null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                if (getForm().getTipoDocAmmesso().getStatus().equals(Status.insert)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                    tipoUnitaDocEjb.insertDecTipoDocAmmesso(param, tipoDocAmmessoRowBean);
                    tipoDocAmmessoRowBean = tipoUnitaDocEjb.getDecTipoDocAmmessoRowBean(null,
                            tipoDocAmmessoRowBean.getIdTipoDoc(), tipoDocAmmessoRowBean.getIdTipoStrutUnitaDoc(),
                            tipoDocAmmessoRowBean.getTiDoc());
                    tipoDocAmmesso.getNm_tipo_doc().setValue(tipoDocAmmesso.getId_tipo_doc().getHtmlDecodedValue());

                    getMessageBox().addMessage(new Message(MessageLevel.INF,
                            "Nuovo Tipo documento ammesso nella struttura UD creato con successo"));
                    getForm().getTipoDocAmmessoList().getTable().last();
                    getForm().getTipoDocAmmessoList().getTable().add(tipoDocAmmessoRowBean);

                } else if (getForm().getTipoDocAmmesso().getStatus().equals(Status.update)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                    String tiDocInziale = ((DecTipoDocAmmessoRowBean) getForm().getTipoDocAmmessoList().getTable()
                            .getCurrentRow()).getTiDoc();
                    BigDecimal idTipoDocAmmesso = ((DecTipoDocAmmessoRowBean) getForm().getTipoDocAmmessoList()
                            .getTable().getCurrentRow()).getIdTipoDocAmmesso();
                    tipoUnitaDocEjb.updateDecTipoDocAmmesso(param, idTipoDocAmmesso, tipoDocAmmessoRowBean,
                            tiDocInziale);
                    getMessageBox().addMessage(new Message(MessageLevel.INF,
                            "Tipo documento ammesso nella struttura UD modificato con successo"));
                    /*
                     * Setto sulla lista (ancora da ricaricare) il nuovo valore di idTipoStrutDoc, in maniera tale che
                     * in caso di immediata ri-modifica prenda su, dalla loadDettaglio, il valore corretto
                     */
                    ((DecTipoDocAmmessoRowBean) getForm().getTipoDocAmmessoList().getTable().getCurrentRow())
                            .setIdTipoDocAmmesso(idTipoDocAmmesso);
                }
                getForm().getTipoDocAmmesso().setViewMode();
                getForm().getTipoDocAmmesso().setStatus(Status.view);
                getForm().getTipoDocAmmessoList().setStatus(Status.view);
                getMessageBox().setViewMode(ViewMode.plain);

            }
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
        }
        forwardToPublisher(Application.Publisher.TIPO_DOC_AMMESSO_DETAIL);
    }

    /**
     * Metodo per il salvataggio o la modifica di un'entita' DecTipoStrutUnitaDoc
     *
     * @throws EMFError
     *             errore generico
     */
    private void salvaTipoStrutUnitaDoc() throws EMFError {

        getMessageBox().clear();
        DecTipoStrutUnitaDocRowBean tipoStrutUnitaDocRowBean = new DecTipoStrutUnitaDocRowBean();

        TipoStrutUnitaDoc tipoStrutUnitaDoc = new TipoStrutUnitaDoc();

        tipoStrutUnitaDoc = getForm().getTipoStrutUnitaDoc();
        tipoStrutUnitaDoc.post(getRequest());

        if (tipoStrutUnitaDoc.validate(getMessageBox())) {

            if (tipoStrutUnitaDoc.getNm_tipo_strut_unita_doc().parse() == null) {
                getMessageBox().addError("Errore di compilazione form: nome tipo struttura non inserito<br/>");
            }
            if (tipoStrutUnitaDoc.getDt_istituz().parse() == null) {
                getMessageBox().addError("Errore di compilazione form: data istituzione non inserita<br/>");
            }
            if (tipoStrutUnitaDoc.getDt_soppres().parse() == null) {
                tipoStrutUnitaDoc.getDt_soppres().setValue(getDefaultDate());
            }
            if (tipoStrutUnitaDoc.getAa_min_tipo_strut_unita_doc().parse() != null
                    && tipoStrutUnitaDoc.getAa_max_tipo_strut_unita_doc().parse() != null
                    && tipoStrutUnitaDoc.getAa_min_tipo_strut_unita_doc().parse()
                            .compareTo(tipoStrutUnitaDoc.getAa_max_tipo_strut_unita_doc().parse()) > 0) {
                getMessageBox().addError(
                        "Errore di compilazione form: anno inizio validità maggiore di anno fine validità<br/>");
            }
            if (tipoStrutUnitaDoc.getDt_istituz().parse().after(tipoStrutUnitaDoc.getDt_soppres().parse())) {
                getMessageBox().addError(
                        "Errore di compilazione form: data di attivazione superiore a data di fine validità<br/>");
            }

            try {
                if (getMessageBox().isEmpty()) {
                    BigDecimal idTipoUnitaDoc = null;

                    tipoStrutUnitaDoc.copyToBean(tipoStrutUnitaDocRowBean);
                    List<BigDecimal> idSistemaVersanteList = getForm().getTipoStrutUnitaDoc()
                            .getSis_vers_tipo_strut_unita_doc().parse();
                    List<BigDecimal> idRegistroUnitaDocList = getForm().getTipoStrutUnitaDoc()
                            .getRegistro_tipo_strut_unita_doc().parse();
                    List<BigDecimal> idXsdDatiSpecList = getForm().getTipoStrutUnitaDoc().getMetadati_specifici()
                            .parse();

                    idTipoUnitaDoc = ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable()
                            .getCurrentRow()).getIdTipoUnitaDoc();
                    tipoStrutUnitaDocRowBean.setIdTipoUnitaDoc(idTipoUnitaDoc);
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null,
                                    null, null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    if (getForm().getTipoStrutUnitaDoc().getStatus().equals(Status.insert)) {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                        tipoUnitaDocEjb.insertDecTipoStrutUnitaDoc(param, tipoStrutUnitaDocRowBean,
                                idSistemaVersanteList, idRegistroUnitaDocList, idXsdDatiSpecList);
                        tipoStrutUnitaDocRowBean = tipoUnitaDocEjb.getDecTipoStrutUnitaDocRowBean(
                                tipoStrutUnitaDocRowBean.getNmTipoStrutUnitaDoc(), idTipoUnitaDoc);
                        getMessageBox().addMessage(new Message(MessageLevel.INF,
                                "Tipo di Struttura associato con successo al Tipo di Unita' Documentaria"));
                        getMessageBox().setViewMode(ViewMode.plain);
                        DecTipoStrutUnitaDocTableBean table = new DecTipoStrutUnitaDocTableBean();
                        table.add(tipoStrutUnitaDocRowBean);
                        getForm().getTipoStrutUnitaDocList().setTable(table);
                        getForm().getTipoStrutUnitaDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                        getForm().getTipoStrutUnitaDocList().getTable().setCurrentRowIndex(0);
                        getForm().getIdList().getId_tipo_strut_doc()
                                .setValue(tipoStrutUnitaDocRowBean.getIdTipoStrutUnitaDoc().toPlainString());
                        DecTipoDocAmmessoTableBean tdaTable = new DecTipoDocAmmessoTableBean();
                        getForm().getTipoDocAmmessoList().setTable(tdaTable);
                        getForm().getTipoDocAmmessoList().getTable().first();
                        getForm().getTipoDocAmmessoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    } else if (getForm().getTipoStrutUnitaDoc().getStatus().equals(Status.update)) {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                        BigDecimal idTipoStrutUnitaDoc = null;
                        idTipoStrutUnitaDoc = ((DecTipoStrutUnitaDocRowBean) getForm().getTipoStrutUnitaDocList()
                                .getTable().getCurrentRow()).getIdTipoStrutUnitaDoc();
                        tipoUnitaDocEjb.updateDecTipoStrutUnitaDoc(param, idTipoStrutUnitaDoc, tipoStrutUnitaDocRowBean,
                                idSistemaVersanteList, idRegistroUnitaDocList, idXsdDatiSpecList);
                        getMessageBox().addMessage(new Message(MessageLevel.INF, "Modifica effettuata con successo"));
                    }
                    getForm().getTipoStrutUnitaDoc().setViewMode();
                    getForm().getTipoStrutUnitaDoc().setStatus(Status.view);
                    getForm().getTipoStrutUnitaDocList().setStatus(Status.view);
                    getMessageBox().setViewMode(ViewMode.plain);
                }
            } catch (ParerUserError e) {
                getMessageBox().addError(e.getDescription());
                forwardToPublisher(Application.Publisher.TIPO_STRUT_UNITA_DOC_DETAIL);
            }
        }
        forwardToPublisher(Application.Publisher.TIPO_STRUT_UNITA_DOC_DETAIL);

    }

    /**
     * Metodo per il salvataggio o la modifica di un'entita' TipoUnitaDocAmmesso
     *
     * @throws EMFError
     *             errore generico
     */
    private void salvaTipoUnitaDocAmmesso() throws EMFError {

        getMessageBox().clear();

        DecTipoUnitaDocAmmessoRowBean tipoUnitaDocAmmessoRowBean = null;
        TipoUnitaDocAmmesso tipoUnitaDocAmmesso = getForm().getTipoUnitaDocAmmesso();
        tipoUnitaDocAmmesso.post(getRequest());

        DecVLisTiUniDocAmsRowBean VTipoUnitaDocAmmessoRowBean = new DecVLisTiUniDocAmsRowBean();

        if (tipoUnitaDocAmmesso.getId_registro_unita_doc().parse() == null) {
            getMessageBox().addError("Errore di compilazione form: registro unit\u00E0 documentaria non inserito<br/>");
        }
        if (tipoUnitaDocAmmesso.getId_tipo_unita_doc().parse() == null) {
            getMessageBox().addError("Errore di compilazione form: tipo unit\u00E0 documentaria non inserito<br/>");
        }

        if (getMessageBox().isEmpty()) {
            BigDecimal idRegistroUnitaDoc = getForm().getTipoUnitaDocAmmesso().getId_registro_unita_doc().parse();
            BigDecimal idTipoUnitaDoc = getForm().getTipoUnitaDocAmmesso().getId_tipo_unita_doc().parse();
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            try {
                if (getForm().getTipoUnitaDocAmmesso().getStatus().equals(Status.insert)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                    if (tipoUnitaDocEjb.getDecTipoUnitaDocAmmessoByParentId(idRegistroUnitaDoc,
                            idTipoUnitaDoc) != null) {
                        throw new ParerUserError("Associazione gi\u00e0 esistente all'interno della struttura</br>");
                    }
                    // Se tutto OK, proseguo
                    eseguiSalvataggioAssociazione(param, null, null, idRegistroUnitaDoc, idTipoUnitaDoc);
                } else if (getForm().getTipoUnitaDocAmmesso().getStatus().equals(Status.update)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());

                    if (getForm().getTipoUnitaDocAmmessoList().getStatus().equals(Status.update)) {
                        VTipoUnitaDocAmmessoRowBean = (DecVLisTiUniDocAmsRowBean) getForm().getTipoUnitaDocAmmessoList()
                                .getTable().getCurrentRow();
                    } else if (getForm().getRegistroTipoUnitaDocAmmessoList().getStatus().equals(Status.update)) {
                        VTipoUnitaDocAmmessoRowBean = (DecVLisTiUniDocAmsRowBean) getForm()
                                .getRegistroTipoUnitaDocAmmessoList().getTable().getCurrentRow();
                    }

                    tipoUnitaDocAmmessoRowBean = tipoUnitaDocEjb.getDecTipoUnitaDocAmmessoRowBean(
                            VTipoUnitaDocAmmessoRowBean.getIdRegistroUnitaDoc(),
                            VTipoUnitaDocAmmessoRowBean.getIdTipoUnitaDoc());

                    tipoUnitaDocEjb.updateDecTipoUnitaDocAmmesso(param,
                            tipoUnitaDocAmmessoRowBean.getIdTipoUnitaDocAmmesso(), idRegistroUnitaDoc, idTipoUnitaDoc);
                    getMessageBox().setViewMode(ViewMode.plain);
                    setAssociazioneAfterSave(tipoUnitaDocAmmessoRowBean.getIdTipoUnitaDocAmmesso(), idRegistroUnitaDoc,
                            idTipoUnitaDoc);
                }
            } catch (ParerUserError e) {
                getMessageBox().addError(e.getDescription());
            }
        }
        forwardToPublisher(Application.Publisher.TIPO_UNITA_DOC_AMMESSO_DETAIL);
    }

    public void eseguiSalvataggioAssociazione(LogParam param, String nmAzionePerCriteri, String tipoAzione,
            BigDecimal idRegistroUnitaDoc, BigDecimal idTipoUnitaDoc) throws ParerUserError {
        param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
        String azioneInsCriterio = SpagoliteLogUtil.getButtonActionName(getForm(), getForm().getTipoUnitaDocAmmesso(),
                getForm().getTipoUnitaDocAmmesso().getInsCriterioRaggrByTipoUnitaDocFromAssociazione().getName());
        String azioneModCriterio = SpagoliteLogUtil.getButtonActionName(getForm(), getForm().getTipoUnitaDocAmmesso(),
                getForm().getTipoUnitaDocAmmesso().getUpdCriterioRaggrByTipoUnitaDocFromAssociazione().getName());
        try {
            tipoUnitaDocEjb.insertDecTipoUnitaDocAmmesso(param, azioneInsCriterio, azioneModCriterio,
                    idRegistroUnitaDoc, idTipoUnitaDoc);
            DecTipoUnitaDocAmmessoRowBean tipoUnitaDocAmmessoRowBean = tipoUnitaDocEjb
                    .getDecTipoUnitaDocAmmessoRowBean(idRegistroUnitaDoc, idTipoUnitaDoc);
            tipoUnitaDocEjb.getDecTipoUnitaDocAmmessoRowBean(idRegistroUnitaDoc, idTipoUnitaDoc);
            getMessageBox().setViewMode(ViewMode.plain);
            getMessageBox().addInfo("Nuovo tipo registro associato con successo al tipo di unit\u00E0 documentaria");
            setAssociazioneAfterSave(tipoUnitaDocAmmessoRowBean.getIdTipoUnitaDocAmmesso(), idRegistroUnitaDoc,
                    idTipoUnitaDoc);
        } catch (ParerWarningException ex) {
            DecTipoUnitaDocAmmessoRowBean tipoUnitaDocAmmessoRowBean = tipoUnitaDocEjb
                    .getDecTipoUnitaDocAmmessoRowBean(idRegistroUnitaDoc, idTipoUnitaDoc);
            tipoUnitaDocEjb.getDecTipoUnitaDocAmmessoRowBean(idRegistroUnitaDoc, idTipoUnitaDoc);
            getMessageBox().setViewMode(ViewMode.alert);
            getMessageBox().addWarning(ex.getDescription());
            setAssociazioneAfterSave(tipoUnitaDocAmmessoRowBean.getIdTipoUnitaDocAmmesso(), idRegistroUnitaDoc,
                    idTipoUnitaDoc);
        }
    }

    public void setAssociazioneAfterSave(BigDecimal idTipoUnitaDocAmmesso, BigDecimal idRegistroUnitaDoc,
            BigDecimal idTipoUnitaDoc) {
        DecVLisTiUniDocAmsRowBean vTipoUnitaDocAmmessoRowBean = new DecVLisTiUniDocAmsRowBean();

        if (getForm().getTipoUnitaDocList().getTable() != null) {
            vTipoUnitaDocAmmessoRowBean.setIdTipoUnitaDoc(
                    ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                            .getIdTipoUnitaDoc());
        } else if (getForm().getRegistroUnitaDocList() != null) {
            vTipoUnitaDocAmmessoRowBean.setIdRegistroUnitaDoc(
                    ((DecRegistroUnitaDocRowBean) getForm().getRegistroUnitaDocList().getTable().getCurrentRow())
                            .getIdRegistroUnitaDoc());
        }

        DecVLisTiUniDocAmsTableBean tipoUnitaDocAmmessoTableBean = tipoUnitaDocEjb
                .getDecVLisTiUniDocAmsTableBean(vTipoUnitaDocAmmessoRowBean);

        if (getForm().getTipoUnitaDocList().getTable() != null) {

            getForm().getTipoUnitaDocAmmessoList().setTable(tipoUnitaDocAmmessoTableBean);
            getForm().getTipoUnitaDocAmmessoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getTipoUnitaDocAmmessoList().setStatus(Status.view);

        } else if (getForm().getRegistroUnitaDocList().getTable() != null) {

            getForm().getRegistroTipoUnitaDocAmmessoList().setTable(tipoUnitaDocAmmessoTableBean);
            getForm().getRegistroTipoUnitaDocAmmessoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getRegistroTipoUnitaDocAmmessoList().setStatus(Status.view);

        }
        getForm().getTipoUnitaDocAmmesso().setViewMode();
        getForm().getTipoUnitaDocAmmesso().getId_tipo_unita_doc().setViewMode();
        getForm().getTipoUnitaDocAmmesso().setStatus(Status.view);
        getForm().getIdList().getId_tipo_unita_doc_ammesso().setValue(idTipoUnitaDocAmmesso.toPlainString());
        getForm().getTipoUnitaDocAmmesso().getCreaTipoSerieStandard().setEditMode();
        getForm().getTipoUnitaDocAmmesso().getCreaTipoSerieStandard().setHidden(false);

        try {
            if (tipoSerieEjb.checkTipoSerieStandardDaRegistroOTipoUd(idRegistroUnitaDoc, idTipoUnitaDoc) == null) {
                getForm().getTipoUnitaDocAmmesso().getCreaTipoSerieStandard().setHidden(true);
            }
        } catch (ParerUserError ex) {
            // In caso di eccezione non mostra il bottone, senza mostrare errori
            getForm().getTipoUnitaDocAmmesso().getCreaTipoSerieStandard().setHidden(true);
        }
    }

    /**
     * Metpodo per il salvataggio o la modifica di un'entita' TipoDoc
     *
     * @throws EMFError
     *             errore generico
     */
    private void salvaTipoDoc() throws EMFError {

        getMessageBox().clear();

        DecTipoDocRowBean tipoDocRowBean = new DecTipoDocRowBean();

        TipoDocCreazioneCriterio creazioneCriterio = getForm().getTipoDocCreazioneCriterio();
        TipoDoc tipoDoc = getForm().getTipoDoc();
        String flTipoDocPrincipaleBeforeChange = getForm().getTipoDoc().getFl_tipo_doc_principale().parse();
        tipoDoc.post(getRequest());
        creazioneCriterio.post(getRequest());

        if (tipoDoc.validate(getMessageBox())) {

            if (tipoDoc.getNm_tipo_doc().parse() == null) {
                getMessageBox().addError("Errore di compilazione form: nome tipo non inserito<br/>");
            }
            if (tipoDoc.getDs_tipo_doc().parse() == null) {
                getMessageBox().addError("Errore di compilazione form: descrizione tipo non inserito<br/>");
            }
            if (tipoDoc.getDt_istituz().parse() == null) {
                getMessageBox().addError("Errore di compilazione form: data istituzione non inserita<br/>");
            }
            if (tipoDoc.getDt_soppres().parse() == null) {
                tipoDoc.getDt_soppres().setValue(getDefaultDate());
            }

            if (tipoDoc.getDt_istituz().parse() != null && tipoDoc.getDt_soppres().parse() != null
                    && tipoDoc.getDt_istituz().parse().after(tipoDoc.getDt_soppres().parse())) {
                getMessageBox()
                        .addError("Errore di compilazione form: data soppressione precedente a data istituzione<br/>");
            }
            String tipoDocPrincipale = tipoDoc.getFl_tipo_doc_principale().parse();

            if (StringUtils.isBlank(tipoDocPrincipale)) {
                getMessageBox().addError(
                        "Errore di compilazione form:  selezionare un valore per la voce \"Tipo documento principale\"<br/>");
            }
            try {

                if (getMessageBox().isEmpty()) {

                    tipoDoc.copyToBean(tipoDocRowBean);

                    tipoDocRowBean.setFlTipoDocPrincipale(tipoDocPrincipale);
                    BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
                    tipoDocRowBean.setIdStrut(idStrut);
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null,
                                    null, null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    if (getForm().getTipoDoc().getStatus().equals(Status.insert)) {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                        tipoDocEjb.insertDecTipoDoc(param, tipoDocRowBean,
                                creazioneCriterio.getCriterio_autom_tipo_doc().parse());
                        tipoDocRowBean = tipoDocEjb.getDecTipoDocRowBean(tipoDocRowBean.getNmTipoDoc(), idStrut);

                        DecTipoDocTableBean tdTable = new DecTipoDocTableBean();
                        tdTable.add(tipoDocRowBean);
                        getForm().getTipoDocList().setTable(tdTable);
                        getForm().getTipoDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                        getForm().getTipoDocList().getTable().setCurrentRowIndex(0);
                        DecXsdDatiSpecTableBean xsdTable = new DecXsdDatiSpecTableBean();
                        getForm().getXsdDatiSpecList().setTable(xsdTable);
                        getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                        getForm().getXsdDatiSpecList().getTable().first();

                        DecTipoStrutDocAmmessoTableBean tsdaTable = new DecTipoStrutDocAmmessoTableBean();
                        getForm().getTipoStrutDocAmmessoDaTipoDocList().setTable(tsdaTable);
                        getForm().getTipoStrutDocAmmessoDaTipoDocList().getTable()
                                .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                        getForm().getTipoStrutDocAmmessoDaTipoDocList().getTable().first();

                        if (tipoDocRowBean.getFlTipoDocPrincipale() != null
                                && tipoDocRowBean.getFlTipoDocPrincipale().equals("1")) {
                            getForm().getRegoleSubStrutTab().setHidden(false);
                        } else {
                            getForm().getRegoleSubStrutTab().setHidden(true);
                        }

                        OrgRegolaValSubStrutTableBean regoleTableBean = new OrgRegolaValSubStrutTableBean();
                        regoleTableBean.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                        getForm().getRegoleSubStrutList().setTable(regoleTableBean);
                        // Nascondo il campo relativo al nome tipo documento
                        getForm().getRegoleSubStrutList().getNm_tipo_doc().setHidden(true);
                        getForm().getRegoleSubStrutList().getTable().first();

                        getMessageBox().addMessage(new Message(MessageLevel.INF, "Nuovo Tipo salvato con successo"));

                    } else if (getForm().getTipoDoc().getStatus().equals(Status.update)) {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                        BigDecimal idTipo = null;

                        idTipo = ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow())
                                .getIdTipoDoc();

                        tipoDocEjb.updateDecTipoDoc(param, idTipo, tipoDocRowBean,
                                creazioneCriterio.getCriterio_autom_tipo_doc().parse());
                        tipoDocRowBean.setIdTipoDoc(idTipo);

                        if (tipoDocRowBean.getFlTipoDocPrincipale() != null
                                && tipoDocRowBean.getFlTipoDocPrincipale().equals("1")) {
                            getForm().getRegoleSubStrutTab().setHidden(false);
                            // Se prima della post era era 0 o null
                            if (flTipoDocPrincipaleBeforeChange == null
                                    || flTipoDocPrincipaleBeforeChange.equals("0")) {
                                OrgRegolaValSubStrutTableBean regoleTableBean = new OrgRegolaValSubStrutTableBean();
                                regoleTableBean.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                                getForm().getRegoleSubStrutList().setTable(regoleTableBean);
                                // Nascondo il campo relativo al nome tipo documento
                                getForm().getRegoleSubStrutList().getNm_tipo_doc().setHidden(true);
                                getForm().getRegoleSubStrutList().getTable().first();
                            }
                        } else {
                            getForm().getRegoleSubStrutTab().setHidden(true);
                            OrgRegolaValSubStrutTableBean regoleTableBean = new OrgRegolaValSubStrutTableBean();
                            regoleTableBean.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                            getForm().getRegoleSubStrutList().setTable(regoleTableBean);
                            // Nascondo il campo relativo al nome tipo documento
                            getForm().getRegoleSubStrutList().getNm_tipo_doc().setHidden(true);
                            getForm().getRegoleSubStrutList().getTable().first();
                        }
                        getMessageBox()
                                .addMessage(new Message(MessageLevel.INF, "Tipo documento modificato con successo"));

                    }

                    // Lista criteri di raggruppamento
                    DecVRicCriterioRaggrTableBean criteri = critRaggrEjb
                            .getCriteriRaggrDaTipoDoc(tipoDocRowBean.getIdStrut(), tipoDocRowBean.getIdTipoDoc());
                    criteri.addSortingRule("nm_criterio_raggr", SortingRule.ASC);
                    criteri.sort();
                    getForm().getCriteriRaggruppamentoList().setTable(criteri);
                    getForm().getCriteriRaggruppamentoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

                    getForm().getTipoDoc().setViewMode();
                    getForm().getTipoDoc().setStatus(Status.view);
                    getForm().getTipoDocList().setStatus(Status.view);
                    getForm().getTipoDoc().getLogEventiTipoDoc().setEditMode();

                    getMessageBox().setViewMode(ViewMode.plain);
                }
                if (tipoDocRowBean.getIdTipoDoc() != null) {
                    getForm().getIdList().getId_tipo_doc().setValue(tipoDocRowBean.getIdTipoDoc().toPlainString());
                }

            } catch (ParerUserError e) {
                getMessageBox().addError(e.getDescription());
            }
        }
        forwardToPublisher(Application.Publisher.TIPO_DOC_DETAIL);
    }

    /**
     * Metodo per il salvataggio o la modifica di un'entita' TipoStrutDocAmmesso
     *
     * @throws EMFError
     *             errore generico
     */
    private void salvaTipoStrutDocAmmesso() throws EMFError {
        getForm().getTipoStrutDocAmmessoDaTipoDoc().post(getRequest());
        if (getForm().getTipoStrutDocAmmessoDaTipoDoc().getId_tipo_strut_doc_ammesso().parse() == null) {
            getMessageBox().addError("Errore di compilazione form: tipo struttura documento non inserito<br/>");
        }
        try {
            if (getMessageBox().isEmpty()) {

                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null,
                                null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                if (getForm().getTipoStrutDocAmmessoDaTipoDoc().getStatus().equals(Status.insert)) {
                    /* Inserisco su DB */
                    BigDecimal idTipoDoc = ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow())
                            .getIdTipoDoc();
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                    tipoDocEjb.insertDecTipoStrutDocAmmesso(param, idTipoDoc,
                            getForm().getTipoStrutDocAmmessoDaTipoDoc().getId_tipo_strut_doc_ammesso().parse());
                    getMessageBox().addMessage(
                            new Message(MessageLevel.INF, "Tipo struttura documento ammesso inserita con successo!"));
                    /* Inserisco la riga in fondo alla tabella */
                    DecTipoStrutDocAmmessoRowBean row = tipoDocEjb.getDecTipoStrutDocAmmessoRowBean(idTipoDoc,
                            getForm().getTipoStrutDocAmmessoDaTipoDoc().getId_tipo_strut_doc_ammesso().parse());
                    getForm().getTipoStrutDocAmmessoDaTipoDocList().getTable().last();
                    getForm().getTipoStrutDocAmmessoDaTipoDocList().getTable().add(row);
                } else if (getForm().getTipoStrutDocAmmessoDaTipoDoc().getStatus().equals(Status.update)) {
                    /* Modifico su DB */
                    BigDecimal idTipoStrutDocAmmessoDB = ((DecTipoStrutDocAmmessoRowBean) getForm()
                            .getTipoStrutDocAmmessoDaTipoDocList().getTable().getCurrentRow())
                                    .getIdTipoStrutDocAmmesso();
                    BigDecimal idTipoDocDB = ((DecTipoStrutDocAmmessoRowBean) getForm()
                            .getTipoStrutDocAmmessoDaTipoDocList().getTable().getCurrentRow()).getIdTipoDoc();
                    BigDecimal idTipoStrutDocInModifica = getForm().getTipoStrutDocAmmessoDaTipoDoc()
                            .getId_tipo_strut_doc_ammesso().parse();
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                    tipoDocEjb.updateDecTipoStrutDocAmmessoFromTipoDoc(param, idTipoStrutDocAmmessoDB, idTipoDocDB,
                            idTipoStrutDocInModifica);
                    getMessageBox().addMessage(
                            new Message(MessageLevel.INF, "Tipo struttura documento ammesso modificata con successo!"));
                    /*
                     * Setto sulla lista (ancora da ricaricare) il nuovo valore di idTipoStrutDoc, in maniera tale che
                     * in caso di immediata ri-modifica prenda su, dalla loadDettaglio, il valore corretto
                     */
                    ((DecTipoStrutDocAmmessoRowBean) getForm().getTipoStrutDocAmmessoDaTipoDocList().getTable()
                            .getCurrentRow()).setIdTipoStrutDoc(idTipoStrutDocInModifica);
                }
                getMessageBox().setViewMode(ViewMode.plain);
                getForm().getTipoStrutDocAmmessoDaTipoDoc().setViewMode();
                getForm().getTipoStrutDocAmmessoDaTipoDocList().setViewMode();
                getForm().getTipoStrutDocAmmessoDaTipoDoc().setStatus(Status.view);
                getForm().getTipoStrutDocAmmessoDaTipoDocList().setStatus(Status.view);
                getMessageBox().setViewMode(ViewMode.plain);
            }
            forwardToPublisher(Application.Publisher.ASSOCIAZIONE_TIPO_DOC_TIPO_STRUT_DOC);
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
            forwardToPublisher(Application.Publisher.ASSOCIAZIONE_TIPO_DOC_TIPO_STRUT_DOC);
        }

    }

    private String getDefaultDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2444, 11, 31, 0, 0, 0);

        Date dtSoppress = null;
        dtSoppress = calendar.getTime();
        DateFormat formato = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);

        String dtSoppressString = formato.format(dtSoppress);
        return dtSoppressString;
    }

    private void loadTipoUnitaDoc(BigDecimal idTipoUnitaDoc) throws EMFError, ParerUserError {
        getForm().getTipoUnitaDoc().setViewMode();
        getForm().getTipoUnitaDoc().setStatus(Status.view);
        getForm().getTipoUnitaDocList().setStatus(Status.view);
        getForm().getTipoUnitaDoc().getLogEventiTipoUD().setEditMode();

        getForm().getTipoUnitaDocCreazioneCriterio().setEditMode();
        getForm().getTipoUnitaDocCreazioneCriterio().clear();
        // Nascondo il tab (all'interno del quale ci sarà il flag) per la creazione del
        // criterio
        getForm().getCreaCriterioTipoUnitaDocSection().setHidden(true);

        BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
        DecModelloTipoSerieTableBean listaModelli = modelliSerieEjb.getDecModelloTipoSerieAllAbilitatiTableBean(idStrut,
                true);
        getForm().getTipoUnitaDoc().getId_modello_tipo_serie().setDecodeMap(
                DecodeMap.Factory.newInstance(listaModelli, "id_modello_tipo_serie", "nm_modello_tipo_serie"));
        getForm().getTipoUnitaDoc().getFl_crea_tipo_serie_standard()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

        // Tipo servizio
        OrgTipoServizioTableBean listaTipiServizioConserv = tipoUnitaDocEjb
                .getOrgTipoServizioTableBean(CostantiDB.TiClasseTipoServizio.CONSERVAZIONE.name());
        getForm().getTipoUnitaDoc().getId_tipo_servizio().setDecodeMap(
                DecodeMap.Factory.newInstance(listaTipiServizioConserv, "id_tipo_servizio", "cd_tipo_servizio"));
        OrgTipoServizioTableBean listaTipiServizioAttiv = tipoUnitaDocEjb
                .getOrgTipoServizioTableBean(CostantiDB.TiClasseTipoServizio.ATTIVAZIONE_SISTEMA_VERSANTE.name());
        getForm().getTipoUnitaDoc().getId_tipo_servizio_attiv().setDecodeMap(
                DecodeMap.Factory.newInstance(listaTipiServizioAttiv, "id_tipo_servizio", "cd_tipo_servizio"));

        OrgTipoServizioTableBean listaTipiServConservTipoUd = tipoUnitaDocEjb
                .getOrgTipoServizioTableBean(CostantiDB.TiClasseTipoServizio.CONSERVAZIONE.name());
        getForm().getTipoUnitaDoc().getId_tipo_serv_conserv_tipo_ud().setDecodeMap(
                DecodeMap.Factory.newInstance(listaTipiServConservTipoUd, "id_tipo_servizio", "cd_tipo_servizio"));
        OrgTipoServizioTableBean listaTipiServAttivTipoUd = tipoUnitaDocEjb
                .getOrgTipoServizioTableBean(CostantiDB.TiClasseTipoServizio.ATTIVAZIONE_TIPO_UD.name());
        getForm().getTipoUnitaDoc().getId_tipo_serv_attiv_tipo_ud().setDecodeMap(
                DecodeMap.Factory.newInstance(listaTipiServAttivTipoUd, "id_tipo_servizio", "cd_tipo_servizio"));

        DecTipoUnitaDocRowBean tipoUnitaRowBean = tipoUnitaDocEjb.getDecTipoUnitaDocRowBean(idTipoUnitaDoc, null);
        setTipoUnitaComboBox();
        getForm().getTipoUnitaDoc().copyFromBean(tipoUnitaRowBean);
        getForm().getIdList().getId_tipo_unita_doc().setValue("" + idTipoUnitaDoc);
        loadTipoUnitaDoclists(true);

        if (tipoUnitaRowBean.getIdCategTipoUnitaDoc() != null) {
            BigDecimal idCategStrutPadre = tipoUnitaDocEjb
                    .getDecCategTipoUnitaDocRowBean(tipoUnitaRowBean.getIdCategTipoUnitaDoc())
                    .getIdCategTipoUnitaDocPadre();
            // Sono una sottocategoria
            if (idCategStrutPadre != null) {
                DecCategTipoUnitaDocTableBean table = tipoUnitaDocEjb
                        .getDecCategTipoUnitaDocChildTableBean(idCategStrutPadre);
                DecodeMap mappaCateg = new DecodeMap();
                mappaCateg.populatedMap(table, "id_categ_tipo_unita_doc", "cd_categ_tipo_unita_doc");
                getForm().getTipoUnitaDoc().getNm_categ_strut().setDecodeMap(mappaCateg);
                getForm().getTipoUnitaDoc().getNm_categ_strut()
                        .setValue(tipoUnitaRowBean.getIdCategTipoUnitaDoc().toString());
                getForm().getTipoUnitaDoc().getTi_categ_strut().setValue(idCategStrutPadre.toString());
            } // Sono una categoria
            else {
                DecCategTipoUnitaDocTableBean table = tipoUnitaDocEjb
                        .getDecCategTipoUnitaDocChildTableBean(tipoUnitaRowBean.getIdCategTipoUnitaDoc());
                DecodeMap mappaCateg = new DecodeMap();
                mappaCateg.populatedMap(table, "id_categ_tipo_unita_doc", "cd_categ_tipo_unita_doc");
                getForm().getTipoUnitaDoc().getNm_categ_strut().setDecodeMap(mappaCateg);
                getForm().getTipoUnitaDoc().getTi_categ_strut()
                        .setValue(tipoUnitaRowBean.getIdCategTipoUnitaDoc().toString());
            }
        }
        // Parametri
        disabileTipoUdParametersSections(false);
        loadListeParametriTipoUd(idStrut, idTipoUnitaDoc, null, false, false, false, false);

        String cessato = getRequest().getParameter("cessato");
        if ((StringUtils.isNotBlank(cessato) && "1".equals(cessato)) || getRequest().getAttribute("cessato") != null) {
            getForm().getParametriTipoUdButtonList().getParametriAmministrazioneTipoUdButton().setViewMode();
            getForm().getParametriTipoUdButtonList().getParametriConservazioneTipoUdButton().setViewMode();
            getForm().getParametriTipoUdButtonList().getParametriGestioneTipoUdButton().setViewMode();
        } else {
            getForm().getParametriTipoUdButtonList().getParametriAmministrazioneTipoUdButton().setEditMode();
            getForm().getParametriTipoUdButtonList().getParametriConservazioneTipoUdButton().setEditMode();
            getForm().getParametriTipoUdButtonList().getParametriGestioneTipoUdButton().setEditMode();
        }
        getSession().removeAttribute("provenienzaParametri");

        // // Section parametri chiuse
        // getForm().getParametriAmministrazioneSection().setLoadOpened(false);
        // getForm().getParametriConservazioneSection().setLoadOpened(false);
        // getForm().getParametriGestioneSection().setLoadOpened(false);
        if ((StringUtils.isNotBlank(cessato) && "1".equals(cessato)) || getRequest().getAttribute("cessato") != null) {
            getForm().getTipoUnitaDocList().setUserOperations(true, false, false, false);
        }
    }

    private void loadTipoUnitaDoclists(boolean isFirst) throws EMFError, ParerUserError {

        HashMap<String, Integer> indMap = new HashMap();
        BigDecimal idTipoUnitaDoc = null;
        if (!isFirst) {
            if (getForm().getTipoUnitaDocAmmessoList().getTable() != null) {
                indMap.put("tipoUnitaDocAmmesso",
                        getForm().getTipoUnitaDocAmmessoList().getTable().getCurrentRowIndex());
                indMap.put("tipoUnitaDocAmmessoPS", getForm().getTipoUnitaDocAmmessoList().getTable().getPageSize());
            }
            if (getForm().getXsdDatiSpecList().getTable() != null) {
                indMap.put("xsdDatiSpec", getForm().getXsdDatiSpecList().getTable().getCurrentRowIndex());
                indMap.put("xsdDatiSpecPS", getForm().getXsdDatiSpecList().getTable().getPageSize());
            }
            if (getForm().getXsdModelliUdList().getTable() != null) {
                indMap.put("xsdModelli", getForm().getXsdModelliUdList().getTable().getCurrentRowIndex());
                indMap.put("xsdModelliPS", getForm().getXsdModelliUdList().getTable().getPageSize());
            }
            if (getForm().getTipoStrutUnitaDocList().getTable() != null) {
                indMap.put("tipoStrutUnitaDoc", getForm().getTipoStrutUnitaDocList().getTable().getCurrentRowIndex());
                indMap.put("tipoStrutUnitaDocPS", getForm().getTipoStrutUnitaDocList().getTable().getPageSize());
            }
            if (getForm().getRegoleSubStrutList().getTable() != null) {
                indMap.put("regoleSubStrut", getForm().getRegoleSubStrutList().getTable().getCurrentRowIndex());
                indMap.put("regoleSubStrutPS", getForm().getRegoleSubStrutList().getTable().getPageSize());
            }
            if (getForm().getSistemiVersantiList().getTable() != null) {
                indMap.put("sisVers", getForm().getSistemiVersantiList().getTable().getCurrentRowIndex());
                indMap.put("sisVersPS", getForm().getSistemiVersantiList().getTable().getPageSize());
            }
            if (getForm().getCriteriRaggruppamentoList().getTable() != null) {
                indMap.put("criteriRaggruppamento",
                        getForm().getCriteriRaggruppamentoList().getTable().getCurrentRowIndex());
                indMap.put("criteriRaggruppamentoPS",
                        getForm().getCriteriRaggruppamentoList().getTable().getPageSize());
            }
        }

        // Lista tipi unita doc ammessi
        DecVLisTiUniDocAmsTableBean tipoUnitaDocAmmessoTableBean = new DecVLisTiUniDocAmsTableBean();
        DecVLisTiUniDocAmsRowBean tipoUnitaDocAmmessoRowBean = new DecVLisTiUniDocAmsRowBean();
        BigDecimal idStrut = null;

        idTipoUnitaDoc = getForm().getIdList().getId_tipo_unita_doc().parse();
        idStrut = getForm().getIdList().getId_strut().parse();
        tipoUnitaDocAmmessoRowBean.setIdTipoUnitaDoc(idTipoUnitaDoc);
        tipoUnitaDocAmmessoTableBean = tipoUnitaDocEjb.getDecVLisTiUniDocAmsTableBean(tipoUnitaDocAmmessoRowBean);

        getForm().getTipoUnitaDocAmmessoList().setTable(tipoUnitaDocAmmessoTableBean);
        getForm().getTipoUnitaDocAmmessoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        // Lista xsd dati specifici
        DecXsdDatiSpecTableBean xsdDatiSpecTableBean = new DecXsdDatiSpecTableBean();
        DecXsdDatiSpecRowBean xsdDatiSpecRowBean = new DecXsdDatiSpecRowBean();

        xsdDatiSpecRowBean.setIdTipoUnitaDoc(idTipoUnitaDoc);
        xsdDatiSpecRowBean.setIdStrut(idStrut);
        xsdDatiSpecRowBean.setTiUsoXsd(CostantiDB.TipiUsoDatiSpec.VERS.name());
        xsdDatiSpecRowBean.setTiEntitaSacer(CostantiDB.TipiEntitaSacer.UNI_DOC.name());
        xsdDatiSpecTableBean = datiSpecEjb.getDecXsdDatiSpecTableBean(xsdDatiSpecRowBean);

        getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
        getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        // Carico gli xsd dei modelli
        DecUsoModelloXsdUniDocTableBean modelloXsdUdTableBean = modelliXsdUdEjb
                .getDecModelliXsdUdInUsoOnUniDoc(idTipoUnitaDoc, CostantiDB.TipiUsoDatiSpec.VERS.name());

        getForm().getXsdModelliUdList().setTable(modelloXsdUdTableBean);
        getForm().getXsdModelliUdList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        // Lista tipi struttura documento
        DecTipoStrutUnitaDocTableBean tipoStrutUnitaDocTableBean = tipoStrutDocEjb
                .getDecTipoStrutUnitaDocTableBean(idTipoUnitaDoc, false);

        getForm().getTipoStrutUnitaDocList().setTable(tipoStrutUnitaDocTableBean);
        getForm().getTipoStrutUnitaDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        // Lista regole
        OrgRegolaValSubStrutTableBean regoleTableBean = subStrutEjb.getOrgRegolaValSubStrutTableBean(idTipoUnitaDoc,
                Constants.TipoDato.TIPO_UNITA_DOC, getForm().getRegoleSubStrutList().isFilterValidRecords());
        regoleTableBean.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getRegoleSubStrutList().setTable(regoleTableBean);
        // Nascono il campo "Tipo unit\u00E0 documentaria"
        getForm().getRegoleSubStrutList().getNm_tipo_unita_doc().setHidden(true);

        // Sistemi versanti
        AplSistemaVersanteTableBean listaSistemiVersanti = tipoUnitaDocEjb
                .getAplSistemaVersanteTableBean(idTipoUnitaDoc);
        getForm().getSistemiVersantiList().setTable(listaSistemiVersanti);
        getForm().getSistemiVersantiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getTipoUnitaDoc().getDt_first_vers().setValue("");
        if (getForm().getSistemiVersantiList().getTable().getRow(0) != null
                && getForm().getSistemiVersantiList().getTable().getRow(0).getObject("dt_first_vers") != null) {
            SimpleDateFormat format = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);
            Date d = (Date) getForm().getSistemiVersantiList().getTable().getRow(0).getObject("dt_first_vers");
            getForm().getTipoUnitaDoc().getDt_first_vers().setValue("" + format.format(d));
        }

        // Lista criteri di raggruppamento
        DecVRicCriterioRaggrTableBean criteri = critRaggrEjb.getCriteriRaggrDaTipoUnitaDoc(idStrut, idTipoUnitaDoc);
        criteri.addSortingRule("nm_criterio_raggr", SortingRule.ASC);
        criteri.sort();
        getForm().getCriteriRaggruppamentoList().setTable(criteri);
        getForm().getCriteriRaggruppamentoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        // Liste parametri
        loadListeParametriTipoUd(idStrut, idTipoUnitaDoc, null, false, false, false, false);

        getForm().getTipoUnitaDocAmmessoList().getTable().first();
        getForm().getXsdDatiSpecList().getTable().first();
        getForm().getXsdModelliUdList().getTable().first();
        getForm().getTipoStrutUnitaDocList().getTable().first();
        getForm().getRegoleSubStrutList().getTable().first();
        getForm().getCriteriRaggruppamentoList().getTable().first();
        getForm().getSistemiVersantiList().getTable().first();

        getForm().getParametriAmministrazioneSection().setLoadOpened(false);
        getForm().getParametriConservazioneSection().setLoadOpened(false);
        getForm().getParametriGestioneSection().setLoadOpened(false);

        if (!indMap.isEmpty()) {
            if (getForm().getTipoUnitaDocAmmessoList().getTable() != null
                    && indMap.containsKey("tipoUnitaDocAmmesso")) {
                getForm().getTipoUnitaDocAmmessoList().getTable().setCurrentRowIndex(indMap.get("tipoUnitaDocAmmesso"));
                getForm().getTipoUnitaDocAmmessoList().getTable().setPageSize(indMap.get("tipoUnitaDocAmmessoPS"));
            }
            if (getForm().getXsdDatiSpecList().getTable() != null && indMap.containsKey("xsdDatiSpec")) {
                getForm().getXsdDatiSpecList().getTable().setCurrentRowIndex(indMap.get("xsdDatiSpec"));
                getForm().getXsdDatiSpecList().getTable().setPageSize(indMap.get("xsdDatiSpecPS"));
            }
            if (getForm().getXsdModelliUdList().getTable() != null && indMap.containsKey("xsdModelli")) {
                getForm().getXsdModelliUdList().getTable().setCurrentRowIndex(indMap.get("xsdModelli"));
                getForm().getXsdModelliUdList().getTable().setCurrentRowIndex(indMap.get("xsdModelliPS"));
            }
            if (getForm().getTipoStrutUnitaDocList().getTable() != null && indMap.containsKey("tipoStrutUnitaDoc")) {
                getForm().getTipoStrutUnitaDocList().getTable().setCurrentRowIndex(indMap.get("tipoStrutUnitaDoc"));
                getForm().getTipoStrutUnitaDocList().getTable().setPageSize(indMap.get("tipoStrutUnitaDocPS"));
            }
            if (getForm().getRegoleSubStrutList().getTable() != null && indMap.containsKey("regoleSubStrut")) {
                getForm().getRegoleSubStrutList().getTable().setCurrentRowIndex(indMap.get("regoleSubStrut"));
                getForm().getRegoleSubStrutList().getTable().setPageSize(indMap.get("regoleSubStrutPS"));
            }
            if (getForm().getRegoleSubStrutList().getTable() != null && indMap.containsKey("sisVers")) {
                getForm().getRegoleSubStrutList().getTable().setCurrentRowIndex(indMap.get("sisVers"));
                getForm().getRegoleSubStrutList().getTable().setPageSize(indMap.get("sisVersPS"));
            }
            if (getForm().getCriteriRaggruppamentoList().getTable() != null
                    && indMap.containsKey("criteriRaggruppamento")) {
                getForm().getCriteriRaggruppamentoList().getTable()
                        .setCurrentRowIndex(indMap.get("criteriRaggruppamento"));
                getForm().getCriteriRaggruppamentoList().getTable().setPageSize(indMap.get("criteriRaggruppamentoPS"));
            }
        }

        String cessato = getRequest().getParameter("cessato");
        if ((StringUtils.isNotBlank(cessato) && "1".equals(cessato)) || getRequest().getAttribute("cessato") != null) {
            getForm().getTipoUnitaDocAmmessoList().setUserOperations(true, false, false, false);
            getForm().getXsdDatiSpecList().setUserOperations(true, false, false, false);
            getForm().getXsdModelliUdList().setUserOperations(true, false, false, false);
            getForm().getTipoStrutUnitaDocList().setUserOperations(true, false, false, false);
            getForm().getRegoleSubStrutList().setUserOperations(true, false, false, false);
            getForm().getCriteriRaggruppamentoList().setUserOperations(true, false, false, false);
        }
    }

    private void loadListeParametriTipoUd(BigDecimal idStrut, BigDecimal idTipoUnitaDoc, List<String> funzione,
            boolean hideDeleteButtons, boolean editModeAmministrazione, boolean editModeConservazione,
            boolean editModeGestione) throws ParerUserError {
        BigDecimal idAmbiente = null;
        if (idStrut != null) {
            OrgStrutRowBean strutRowBean = struttureEjb.getOrgStrutRowBean(idStrut);
            OrgEnteRowBean enteRowBean = struttureEjb.getOrgEnteRowBean(strutRowBean.getIdEnte());
            idAmbiente = enteRowBean.getIdAmbiente();
        }
        Object[] parametriObj = amministrazioneEjb.getAplParamApplicTipoUd(idAmbiente, idStrut, idTipoUnitaDoc,
                funzione);

        // MEV26587
        AplParamApplicTableBean parametriAmministrazione = (AplParamApplicTableBean) parametriObj[0];
        AplParamApplicTableBean parametriGestione = (AplParamApplicTableBean) parametriObj[1];
        AplParamApplicTableBean parametriConservazione = (AplParamApplicTableBean) parametriObj[2];

        if (!editModeAmministrazione)
            parametriAmministrazione = obfuscatePasswordParamApplic(parametriAmministrazione);

        if (!editModeGestione)
            parametriGestione = obfuscatePasswordParamApplic(parametriGestione);

        if (!editModeConservazione)
            parametriConservazione = obfuscatePasswordParamApplic(parametriConservazione);

        // getForm().getParametriAmministrazioneSection().setLoadOpened(true);
        // getForm().getParametriConservazioneSection().setLoadOpened(true);
        // getForm().getParametriGestioneSection().setLoadOpened(true);
        getForm().getParametriAmministrazioneTipoUdList().setTable((AplParamApplicTableBean) parametriAmministrazione);
        getForm().getParametriAmministrazioneTipoUdList().getTable().setPageSize(300);
        getForm().getParametriAmministrazioneTipoUdList().getTable().first();
        getForm().getParametriGestioneTipoUdList().setTable((AplParamApplicTableBean) parametriGestione);
        getForm().getParametriGestioneTipoUdList().getTable().setPageSize(300);
        getForm().getParametriGestioneTipoUdList().getTable().first();
        getForm().getParametriConservazioneTipoUdList().setTable((AplParamApplicTableBean) parametriConservazione);
        getForm().getParametriConservazioneTipoUdList().getTable().setPageSize(300);
        getForm().getParametriConservazioneTipoUdList().getTable().first();
        getForm().getParametriAmministrazioneTipoUdList().setHideDeleteButton(hideDeleteButtons);
        getForm().getParametriGestioneTipoUdList().setHideDeleteButton(hideDeleteButtons);
        getForm().getParametriConservazioneTipoUdList().setHideDeleteButton(hideDeleteButtons);
        if (editModeAmministrazione) {
            getForm().getParametriAmministrazioneTipoUdList().getDs_valore_param_applic_tipo_ud_amm().setEditMode();
            getForm().getParametriAmministrazioneTipoUdList().setStatus(Status.update);
        } else {
            getForm().getParametriAmministrazioneTipoUdList().getDs_valore_param_applic_tipo_ud_amm().setViewMode();
            getForm().getParametriAmministrazioneTipoUdList().setStatus(Status.view);
        }

        if (editModeConservazione) {
            getForm().getParametriConservazioneTipoUdList().getDs_valore_param_applic_tipo_ud_cons().setEditMode();
            getForm().getParametriConservazioneTipoUdList().setStatus(Status.update);
        } else {
            getForm().getParametriConservazioneTipoUdList().getDs_valore_param_applic_tipo_ud_cons().setViewMode();
            getForm().getParametriConservazioneTipoUdList().setStatus(Status.view);
        }

        if (editModeGestione) {
            getForm().getParametriGestioneTipoUdList().getDs_valore_param_applic_tipo_ud_gest().setEditMode();
            getForm().getParametriGestioneTipoUdList().setStatus(Status.update);
        } else {
            getForm().getParametriGestioneTipoUdList().getDs_valore_param_applic_tipo_ud_gest().setViewMode();
            getForm().getParametriGestioneTipoUdList().setStatus(Status.view);
        }
    }

    @Override
    public JSONObject triggerTipoUnitaDocTi_categ_strutOnTrigger() throws EMFError {
        getForm().getTipoUnitaDoc().post(getRequest());
        getForm().getTipoUnitaDoc().getId_tipo_servizio().setValue(null);
        getForm().getTipoUnitaDoc().getId_tipo_servizio_attiv().setValue(null);
        getForm().getTipoUnitaDoc().getId_tipo_serv_conserv_tipo_ud().setValue(null);
        getForm().getTipoUnitaDoc().getId_tipo_serv_attiv_tipo_ud().setValue(null);
        if (getForm().getTipoUnitaDoc().getTi_categ_strut().parse() != null) {
            DecCategTipoUnitaDocTableBean table = tipoUnitaDocEjb
                    .getDecCategTipoUnitaDocChildTableBean(getForm().getTipoUnitaDoc().getTi_categ_strut().parse());
            DecodeMap mappaCateg = new DecodeMap();
            mappaCateg.populatedMap(table, "id_categ_tipo_unita_doc", "cd_categ_tipo_unita_doc");
            getForm().getTipoUnitaDoc().getNm_categ_strut().setDecodeMap(mappaCateg);
        } else {
            DecodeMap mappaCateg = new DecodeMap();
            getForm().getTipoUnitaDoc().getNm_categ_strut().setDecodeMap(mappaCateg);
        }
        return getForm().getTipoUnitaDoc().asJSON();
    }

    @Override
    public JSONObject triggerTipoUnitaDocNm_categ_strutOnTrigger() throws EMFError {
        getForm().getTipoUnitaDoc().post(getRequest());
        getForm().getTipoUnitaDoc().getId_tipo_servizio().setValue(null);
        getForm().getTipoUnitaDoc().getId_tipo_servizio_attiv().setValue(null);
        getForm().getTipoUnitaDoc().getId_tipo_serv_conserv_tipo_ud().setValue(null);
        getForm().getTipoUnitaDoc().getId_tipo_serv_attiv_tipo_ud().setValue(null);
        if (getForm().getTipoUnitaDoc().getNm_categ_strut().parse() != null) {
            DecVCalcTiServOnTipoUdRowBean rb = tipoUnitaDocEjb.getDecVCalcTiServOnTipoUd(
                    getForm().getIdList().getId_strut().parse(),
                    getForm().getTipoUnitaDoc().getNm_categ_strut().parse(), "CLASSE_ENTE");
            DecVCalcTiServOnTipoUdRowBean rb2 = tipoUnitaDocEjb.getDecVCalcTiServOnTipoUd(
                    getForm().getIdList().getId_strut().parse(),
                    getForm().getTipoUnitaDoc().getNm_categ_strut().parse(), "NO_CLASSE_ENTE");
            if (rb != null) {
                if (rb.getIdTipoServizioConserv() != null) {
                    getForm().getTipoUnitaDoc().getId_tipo_servizio().setValue("" + rb.getIdTipoServizioConserv());
                }
                if (rb.getIdTipoServizioAttiv() != null) {
                    getForm().getTipoUnitaDoc().getId_tipo_servizio_attiv().setValue("" + rb.getIdTipoServizioAttiv());
                }
            }
            if (rb2 != null) {
                if (rb2.getIdTipoServizioConserv() != null) {
                    getForm().getTipoUnitaDoc().getId_tipo_serv_conserv_tipo_ud()
                            .setValue("" + rb2.getIdTipoServizioConserv());
                }
                if (rb2.getIdTipoServizioAttiv() != null) {
                    getForm().getTipoUnitaDoc().getId_tipo_serv_attiv_tipo_ud()
                            .setValue("" + rb2.getIdTipoServizioAttiv());
                }
            }

        }
        return getForm().getTipoUnitaDoc().asJSON();
    }

    private void setActionToSession(ActionMap mappa, boolean setToNull) {
        if (setToNull) {
            getSession().removeAttribute("ActionMap");
        } else {
            getSession().setAttribute("ActionMap", mappa);
        }
    }

    private ActionMap getActionMapFromSession() {
        ActionMap result = (ActionMap) getSession().getAttribute("ActionMap");
        return result;
    }

    private void setMaxLenNumeroChiaveToSession(String numero) {
        getSession().setAttribute("MaxLenNumeroChiave", numero);
    }

    private String getMaxLenNumeroChiaveFromSession() {
        String tmpString = (String) getSession().getAttribute("MaxLenNumeroChiave");
        return tmpString;
    }

    @Override
    public void updateAttribDatiSpec() throws EMFError {
        super.updateAttribDatiSpec();
    }

    @Override
    public void deleteTipoDocAmmesso() throws EMFError {
        super.deleteTipoDocAmmesso();
    }

    @Override
    public void updateTipoDocAmmesso() throws EMFError {
        super.updateTipoDocAmmesso();
    }

    @Override
    public void deleteTipoStrutUnitaDoc() throws EMFError {
        super.deleteTipoStrutUnitaDoc();
    }

    @Override
    public void updateTipoStrutUnitaDoc() throws EMFError {
        super.updateTipoStrutUnitaDoc();
    }

    @Override
    public void deleteTipoUnitaDocAmmesso() throws EMFError {
        super.deleteTipoUnitaDocAmmesso();
    }

    @Override
    public void deleteTipoDoc() throws EMFError {
        super.deleteTipoDoc();
    }

    @Override
    public void updateTipoDoc() throws EMFError {
        super.updateTipoDoc();
    }

    @Override
    public void deleteTipoUnitaDoc() throws EMFError {
        super.deleteTipoUnitaDoc();
    }

    @Override
    public void updateTipoUnitaDoc() throws EMFError {
        if (getSession().getAttribute("provenienzaParametri") == null) {
            getForm().getTipoUnitaDoc().getDs_tipo_unita_doc().setEditMode();
            getForm().getTipoUnitaDoc().getDt_soppres().setEditMode();
            getForm().getTipoUnitaDoc().getTi_categ_strut().setEditMode();
            getForm().getTipoUnitaDoc().getNm_categ_strut().setEditMode();

            BigDecimal idTipoUnitaDoc = getForm().getIdList().getId_tipo_unita_doc().parse();
            BigDecimal idStrut = getForm().getIdList().getId_strut().parse();

            // sono in update e quindi non imposto i valori di default per il tipo
            // salvataggio
            // andrebbe a sovrascrivere il valore effettivo del record in modifica.
            setTipoUnitaComboBox(true);
            if (!struttureEjb.existAroUnitaDocByIdTipoUnitaDoc(idTipoUnitaDoc, idStrut)) {
                getForm().getTipoUnitaDoc().setEditMode();
            }

            // getForm().getParametriAmministrazioneTipoUdList().getDs_valore_param_applic_tipo_ud_amm().setEditMode();
            // getForm().getParametriConservazioneTipoUdList().getDs_valore_param_applic_tipo_ud_cons().setEditMode();
            // getForm().getParametriGestioneTipoUdList().getDs_valore_param_applic_tipo_ud_gest().setEditMode();
            // getForm().getParametriAmministrazioneTipoUdList().setHideDeleteButton(true);
            // getForm().getParametriConservazioneTipoUdList().setHideDeleteButton(true);
            // getForm().getParametriGestioneTipoUdList().setHideDeleteButton(true);
            // Data primo versamento non editabile
            getForm().getTipoUnitaDoc().getDt_first_vers().setViewMode();
            getForm().getTipoUnitaDocList().setStatus(Status.update);
            getForm().getTipoUnitaDoc().setStatus(Status.update);
        } else {

            if (getSession().getAttribute("provenienzaParametri") != null) {
                String provenienzaParametri = (String) getSession().getAttribute("provenienzaParametri");
                try {
                    if (provenienzaParametri.equals("amministrazione")) {

                        setEditModeParametriAmministrazione();

                    } else if (provenienzaParametri.equals("conservazione")) {
                        setEditModeParametriConservazione();
                    } else if (provenienzaParametri.equals("gestione")) {
                        setEditModeParametriGestione();
                    }
                    forwardToPublisher(Application.Publisher.PARAMETRI_TIPO_UD);
                } catch (Throwable ex) {
                    getMessageBox().addError("Errore durante il caricamento dei parametri");
                }
            }
        }
    }

    private void setEditModeParametriAmministrazione() {
        getForm().getTipoUnitaDoc().setStatus(Status.update);
        getForm().getParametriAmministrazioneTipoUdList().setStatus(Status.update);
        getForm().getParametriConservazioneTipoUdList().setStatus(Status.update);
        getForm().getParametriGestioneTipoUdList().setStatus(Status.update);
        getForm().getParametriAmministrazioneTipoUdList().getDs_valore_param_applic_tipo_ud_amm().setEditMode();
        getForm().getParametriConservazioneTipoUdList().getDs_valore_param_applic_tipo_ud_cons().setEditMode();
        getForm().getParametriGestioneTipoUdList().getDs_valore_param_applic_tipo_ud_gest().setEditMode();
    }

    private void setEditModeParametriConservazione() {
        getForm().getTipoUnitaDoc().setStatus(Status.update);
        getForm().getParametriConservazioneTipoUdList().setStatus(Status.update);
        getForm().getParametriGestioneTipoUdList().setStatus(Status.update);
        getForm().getParametriConservazioneTipoUdList().getDs_valore_param_applic_tipo_ud_cons().setEditMode();
        getForm().getParametriGestioneTipoUdList().getDs_valore_param_applic_tipo_ud_gest().setEditMode();
    }

    private void setEditModeParametriGestione() {
        getForm().getTipoUnitaDoc().setStatus(Status.update);
        getForm().getParametriGestioneTipoUdList().setStatus(Status.update);
        getForm().getParametriGestioneTipoUdList().getDs_valore_param_applic_tipo_ud_gest().setEditMode();
    }

    @Override
    public void updateAARegistroUnitaDoc() throws EMFError {
        updateAaRegistroUnitaDocList();
    }

    @Override
    public void deleteRegistroUnitaDoc() throws EMFError {
        getMessageBox().clear();

        DecRegistroUnitaDocRowBean registroUnitaDocRowBean = (DecRegistroUnitaDocRowBean) getForm()
                .getRegistroUnitaDocList().getTable().getCurrentRow();
        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this), SpagoliteLogUtil.getToolbarDelete());
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            registroEjb.deleteDecRegistroUnitaDoc(param, registroUnitaDocRowBean.getIdRegistroUnitaDoc().longValue());
            getMessageBox().addMessage(
                    new Message(MessageLevel.INF, "Registro unit\u00E0 documentaria eliminato con successo"));
            // Reload list
            DecRegistroUnitaDocTableBean registroUnitaDocTableBean = registroEjb.getDecRegistroUnitaDocTableBean(
                    getForm().getIdList().getId_strut().parse(),
                    getForm().getRegistroUnitaDocList().isFilterValidRecords());

            getForm().getRegistroUnitaDocList().setTable(registroUnitaDocTableBean);
            getForm().getRegistroUnitaDocList().getTable().first();
            getForm().getRegistroUnitaDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            goBack();
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
            forwardToPublisher(Application.Publisher.REGISTRO_UNITA_DOC_DETAIL);
        }
    }

    @Override
    public void updateRegistroUnitaDoc() throws EMFError {

        getForm().getRegistroUnitaDoc().setEditMode();
        getForm().getRegistroUnitaDoc().getCd_registro_unita_doc().setViewMode();
        getForm().getRegistroUnitaDoc().getFl_registro_fisc().setViewMode();
        getForm().getRegistroUnitaDoc().getControllo_formato().setViewMode();
        getForm().getRegistroUnitaDoc().getDt_istituz().setViewMode();

        BigDecimal idRegistroUnitaDoc = getForm().getIdList().getId_registro_unita_doc().parse();
        BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
        if (!struttureEjb.existAroUnitaDocByIdRegistroUnitaDoc(idRegistroUnitaDoc, idStrut)) {
            getForm().getRegistroUnitaDoc().setEditMode();
        }

        getForm().getRegistroUnitaDoc().setStatus(Status.update);
        getForm().getRegistroUnitaDocList().setStatus(Status.update);
    }

    private void reloadRegistroLists() throws EMFError {

        getForm().getRegistroUnitaDoc().setViewMode();
        getForm().getRegistroUnitaDoc().setStatus(Status.view);
        getForm().getRegistroUnitaDocList().setStatus(Status.view);
        getForm().getRegistroUnitaDoc().getDuplicaRegistroButton().setEditMode();
        getForm().getRegistroUnitaDoc().getLogEventi().setEditMode();

        BigDecimal idRegistroUnitaDoc = getForm().getIdList().getId_registro_unita_doc().parse();

        DecRegistroUnitaDocRowBean registroUnitaDocRowBean = registroEjb
                .getDecRegistroUnitaDocRowBean(idRegistroUnitaDoc, null);
        getForm().getRegistroUnitaDoc().getConserv_unlimited().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getRegistroUnitaDoc().copyFromBean(registroUnitaDocRowBean);

        if (registroUnitaDocRowBean.getNiAnniConserv() != null) {
            if (registroUnitaDocRowBean.getNiAnniConserv().compareTo(new BigDecimal(9999)) < 0) {
                getForm().getRegistroUnitaDoc().getNi_anni_conserv()
                        .setValue(registroUnitaDocRowBean.getNiAnniConserv().toString());
                getForm().getRegistroUnitaDoc().getConserv_unlimited().setValue("0");
            } else {
                getForm().getRegistroUnitaDoc().getConserv_unlimited().setValue("1");
            }
        }

        DecVLisTiUniDocAmsRowBean tipoUnitaDocAmmessoRowBean = new DecVLisTiUniDocAmsRowBean();

        tipoUnitaDocAmmessoRowBean.setIdRegistroUnitaDoc(idRegistroUnitaDoc);
        DecVLisTiUniDocAmsTableBean tipoUnitaDocAmmessoTableBean = tipoUnitaDocEjb
                .getDecVLisTiUniDocAmsTableBean(tipoUnitaDocAmmessoRowBean);

        getForm().getRegistroTipoUnitaDocAmmessoList().setTable(tipoUnitaDocAmmessoTableBean);
        getForm().getRegistroTipoUnitaDocAmmessoList().getTable().first();
        getForm().getRegistroTipoUnitaDocAmmessoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        DecAaRegistroUnitaDocTableBean aaRegistroUnitaDocTableBean = new DecAaRegistroUnitaDocTableBean();
        aaRegistroUnitaDocTableBean = registroEjb.getDecAARegistroUnitaDocTableBean(idRegistroUnitaDoc);
        if (aaRegistroUnitaDocTableBean != null) {
            getForm().getAaRegistroUnitaDocList().setTable(aaRegistroUnitaDocTableBean);
            getForm().getAaRegistroUnitaDocList().getTable().first();
            getForm().getAaRegistroUnitaDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        }

        // Lista criteri di raggruppamento
        DecVRicCriterioRaggrTableBean criteri = critRaggrEjb.getCriteriRaggrDaRegistro(
                registroUnitaDocRowBean.getIdStrut(), registroUnitaDocRowBean.getIdRegistroUnitaDoc());
        criteri.addSortingRule("nm_criterio_raggr", SortingRule.ASC);
        criteri.sort();
        getForm().getCriteriRaggruppamentoList().setTable(criteri);
        getForm().getCriteriRaggruppamentoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
    }

    @Override
    public void updateRegoleSubStrutList() throws EMFError {
        getForm().getRegolaSubStrut().setEditMode();

        BigDecimal idStrut = null;
        if (getSession().getAttribute("provenienzaRegola").equals(Application.Publisher.TIPO_UNITA_DOC_DETAIL)) {
            idStrut = ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                    .getIdStrut();
            getForm().getRegolaSubStrut().getId_tipo_doc().setDecodeMap(DecodeMap.Factory
                    .newInstance(tipoDocEjb.getDocumentiPrincipali(idStrut), "id_tipo_doc", "nm_tipo_doc"));
        } else if (getSession().getAttribute("provenienzaRegola").equals(Application.Publisher.TIPO_DOC_DETAIL)) {
            idStrut = ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow()).getIdStrut();
            getForm().getRegolaSubStrut().getId_tipo_unita_doc().setDecodeMap(DecodeMap.Factory.newInstance(
                    tipoUnitaDocEjb.getDecTipoUnitaDocTableBean(idStrut), "id_tipo_unita_doc", "nm_tipo_unita_doc"));
        }

        getForm().getRegolaSubStrut().setStatus(Status.update);
        getForm().getRegoleSubStrutList().setStatus(Status.update);
    }

    private void salvaRegola() throws EMFError {

        getForm().getRegolaSubStrut().post(getRequest());
        BigDecimal idTipoUnitaDoc = null;
        BigDecimal idTipoDoc = null;
        String nmTipoUnitaDoc = null;

        String nmTipoDoc = null;

        if (getSession().getAttribute("provenienzaRegola").equals(Application.Publisher.TIPO_UNITA_DOC_DETAIL)) {
            idTipoUnitaDoc = ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                    .getIdTipoUnitaDoc();
            idTipoDoc = getForm().getRegolaSubStrut().getId_tipo_doc().parse();
            nmTipoUnitaDoc = ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                    .getNmTipoUnitaDoc();
            // Se provengo da Tipo Unita Doc setto la combo nascosta di tipo unit\u00E0 doc
            // col valore gi\u00E0
            // impostato
            BaseTable bt = new BaseTable();
            BaseRow br = new BaseRow();
            DecodeMap mappaTipoUD = new DecodeMap();
            br.setBigDecimal("id_tipo_unita_doc", idTipoUnitaDoc);
            br.setString("nm_tipo_unita_doc", nmTipoUnitaDoc);
            bt.add(br);
            mappaTipoUD.populatedMap(bt, "id_tipo_unita_doc", "nm_tipo_unita_doc");
            getForm().getRegolaSubStrut().getId_tipo_unita_doc().setDecodeMap(mappaTipoUD);
            getForm().getRegolaSubStrut().getId_tipo_unita_doc().setValue("" + idTipoUnitaDoc);
        } else if (getSession().getAttribute("provenienzaRegola").equals(Application.Publisher.TIPO_DOC_DETAIL)) {
            // Se provengo da Tipo Doc setto la combo nascosta di tipo doc col valore
            // gi\u00E0 impostato
            idTipoDoc = ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow()).getIdTipoDoc();
            idTipoUnitaDoc = getForm().getRegolaSubStrut().getId_tipo_unita_doc().parse();
            nmTipoDoc = ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow()).getNmTipoDoc();
            BaseTable bt = new BaseTable();
            BaseRow br = new BaseRow();
            DecodeMap mappaTipoDoc = new DecodeMap();
            br.setBigDecimal("id_tipo_doc", idTipoDoc);
            br.setString("nm_tipo_doc", nmTipoDoc);
            bt.add(br);
            mappaTipoDoc.populatedMap(bt, "id_tipo_doc", "nm_tipo_doc");
            getForm().getRegolaSubStrut().getId_tipo_doc().setDecodeMap(mappaTipoDoc);
            getForm().getRegolaSubStrut().getId_tipo_doc().setValue("" + idTipoDoc);
        }

        if (getForm().getRegolaSubStrut().validate(getMessageBox())) {
            Date dtSoppres;
            if (getForm().getRegolaSubStrut().getDt_soppres().parse() != null) {
                dtSoppres = new Date(getForm().getRegolaSubStrut().getDt_soppres().parse().getTime());
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.set(2444, 11, 31, 0, 0, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                dtSoppres = calendar.getTime();
            }
            Date dtIstituz = new Date(getForm().getRegolaSubStrut().getDt_istituz().parse().getTime());

            if (getForm().getRegoleSubStrutList().getStatus().equals(Status.insert)) {
                if (!subStrutEjb.existOrgRegolaSubStrut(null, idTipoUnitaDoc, idTipoDoc, dtIstituz, dtSoppres)) {
                    Long idRegolaValSubStrut = null;
                    try {
                        LogParam param = SpagoliteLogUtil.getLogParam(
                                configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null,
                                        null, null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                                getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                                SpagoliteLogUtil.getToolbarInsert());
                        param.setNomeTipoOggetto(SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA);
                        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                        idRegolaValSubStrut = subStrutEjb.saveRegolaSubStrut(param, idTipoUnitaDoc, idTipoDoc,
                                dtIstituz, dtSoppres);
                    } catch (ParerUserError ex) {
                        getMessageBox().addError(ex.getMessage());
                    }
                    if (!getMessageBox().hasError()) {
                        if (idRegolaValSubStrut != null) {
                            getForm().getRegolaSubStrut().getId_regola_val_sub_strut()
                                    .setValue(String.valueOf(idRegolaValSubStrut));
                        }

                        // Gestisco la lista per avere la riga corrente
                        OrgRegolaValSubStrutTableBean table = new OrgRegolaValSubStrutTableBean();
                        OrgRegolaValSubStrutRowBean row = new OrgRegolaValSubStrutRowBean();
                        getForm().getRegolaSubStrut().copyToBean(row);
                        table.add(row);
                        table.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                        table.first();

                        getForm().getRegoleSubStrutList().setTable(table);
                        getForm().getCampiSubStrutList().setTable(new OrgCampoValSubStrutTableBean());
                        getForm().getCampiSubStrutList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                        getForm().getCampiSubStrutList().getTable().first();
                    }
                } else {
                    getMessageBox().addError(
                            "Per la tipologia di unit\u00E0 documentaria esiste una regola avente data di disattivazione superiore alla data corrente; modificare tale regola prima di eseguire il salvataggio della nuova regola");
                }
            } else if (getForm().getRegoleSubStrutList().getStatus().equals(Status.update)) {
                OrgRegolaValSubStrutRowBean row = (OrgRegolaValSubStrutRowBean) getForm().getRegoleSubStrutList()
                        .getTable().getCurrentRow();
                BigDecimal idRegolaSubStrut = row.getIdRegolaValSubStrut();
                if (idRegolaSubStrut == null) {
                    getMessageBox().addError("Errore inaspettato. Ritentare il caricamento e la modifica della regola");
                } else if (!subStrutEjb.existOrgRegolaSubStrut(idRegolaSubStrut, idTipoUnitaDoc, idTipoDoc, dtIstituz,
                        dtSoppres)) {
                    try {
                        LogParam param = SpagoliteLogUtil.getLogParam(
                                configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null,
                                        null, null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                                getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                                SpagoliteLogUtil.getToolbarUpdate());
                        param.setNomeTipoOggetto(SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA);
                        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                        if (getSession().getAttribute("provenienzaRegola")
                                .equals(Application.Publisher.TIPO_UNITA_DOC_DETAIL)) {
                            param.setIdOggetto(idTipoUnitaDoc);
                            subStrutEjb.saveRegolaSubStrut(param, idRegolaSubStrut, dtIstituz, dtSoppres, idTipoDoc);
                        } else if (getSession().getAttribute("provenienzaRegola")
                                .equals(Application.Publisher.TIPO_DOC_DETAIL)) {
                            param.setIdOggetto(idTipoUnitaDoc);
                            subStrutEjb.saveRegolaSubStrutFromTipoDoc(param, idRegolaSubStrut, dtIstituz, dtSoppres,
                                    idTipoUnitaDoc);
                        }
                    } catch (ParerUserError ex) {
                        getMessageBox().addError(ex.getMessage());
                    }
                } else {
                    getMessageBox().addError(
                            "Per la tipologia di unit\u00E0 documentaria esiste una regola avente data di disattivazione superiore alla data corrente; modificare tale regola prima di eseguire il salvataggio della nuova regola");
                }
            }
            if (!getMessageBox().hasError()) {
                getMessageBox().addInfo("Regola salvata con successo");
                getMessageBox().setViewMode(MessageBox.ViewMode.plain);

                getForm().getRegoleSubStrutList().setStatus(Status.view);
                getForm().getRegolaSubStrut().setStatus(Status.view);
                getForm().getRegolaSubStrut().setViewMode();
            }
        }

        forwardToPublisher(Application.Publisher.REGOLA_DETAIL);
    }

    @Override
    public void deleteRegoleSubStrutList() throws EMFError {
        getMessageBox().clear();
        String lastPublisher = getLastPublisher();
        boolean isFromDetailOrIsEmpty = Application.Publisher.REGOLA_DETAIL.equals(lastPublisher)
                || "".equals(lastPublisher);
        int index = getForm().getRegoleSubStrutList().getTable().getCurrentRowIndex();
        OrgRegolaValSubStrutRowBean rowBean = (OrgRegolaValSubStrutRowBean) getForm().getRegoleSubStrutList().getTable()
                .getCurrentRow();
        BigDecimal idRegolaValSubStrut = rowBean.getIdRegolaValSubStrut();
        if (idRegolaValSubStrut == null) {
            getMessageBox().addError("Errore inaspettato. Ritentare il caricamento e la modifica della regola");
        } else {
            try {
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null,
                                null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.REGOLA_DETAIL)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
                } else if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.TIPO_DOC_DETAIL)) {
                    param.setNomeAzione(
                            SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getRegoleSubStrutList()));
                } else if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.TIPO_UNITA_DOC_DETAIL)) {
                    param.setNomeAzione(
                            SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getRegoleSubStrutList()));
                }
                param.setIdOggetto(rowBean.getIdTipoUnitaDoc());
                param.setNomeTipoOggetto(SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA);
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                /*
                 * if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.REGOLA_DETAIL)) {
                 * param.setIdOggetto(((OrgRegolaValSubStrutRowBean)
                 * getForm().getRegoleSubStrutList().getTable().getCurrentRow()). getIdTipoUnitaDoc());
                 * param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete()); } else { param.setNomeAzione(
                 * SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getRegoleSubStrutList()) ); }
                 * param.setIdOggetto(((OrgRegolaValSubStrutRowBean)
                 * getForm().getRegoleSubStrutList().getTable().getCurrentRow()). getIdTipoUnitaDoc());
                 */
                subStrutEjb.deleteRegolaSubStrut(param, idRegolaValSubStrut);
                getMessageBox().addInfo("Regola eliminata con successo");
                getMessageBox().setViewMode(MessageBox.ViewMode.plain);

                if (isFromDetailOrIsEmpty) {
                    goBack();
                } else {
                    getForm().getRegoleSubStrutList().getTable().remove(index);
                    forwardToPublisher(lastPublisher);
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getMessage());
                if (isFromDetailOrIsEmpty) {
                    goBack();
                } else {
                    forwardToPublisher(lastPublisher);
                }
            }
        }
    }

    @Override
    public void deleteCampiSubStrutList() throws EMFError {
        getMessageBox().clear();
        String lastPublisher = getLastPublisher();
        boolean isFromDetailOrIsEmpty = Application.Publisher.CAMPO_REGOLA_DETAIL.equals(lastPublisher)
                || "".equals(lastPublisher);
        int index = getForm().getCampiSubStrutList().getTable().getCurrentRowIndex();
        BigDecimal idCampoValSubStrut = ((OrgCampoValSubStrutRowBean) getForm().getCampiSubStrutList().getTable()
                .getCurrentRow()).getIdCampoValSubStrut();
        if (idCampoValSubStrut == null) {
            getMessageBox().addError("Errore inaspettato. Ritentare il caricamento e la modifica del campo");
        } else {
            try {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                BigDecimal idTipoUnitaDoc = ((OrgRegolaValSubStrutRowBean) getForm().getRegoleSubStrutList().getTable()
                        .getCurrentRow()).getIdTipoUnitaDoc();
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null,
                                null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                param.setNomeTipoOggetto(SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA);
                param.setIdOggetto(idTipoUnitaDoc);
                if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.CAMPO_REGOLA_DETAIL)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
                } else {
                    param.setNomeAzione(
                            SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getCampiSubStrutList()));
                }
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                subStrutEjb.deleteCampoSubStrut(param, idCampoValSubStrut);
                getMessageBox().addInfo("Campo eliminato con successo");
                getMessageBox().setViewMode(MessageBox.ViewMode.plain);

                if (isFromDetailOrIsEmpty) {
                    goBack();
                } else {
                    getForm().getCampiSubStrutList().getTable().remove(index);
                    forwardToPublisher(lastPublisher);
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getMessage());
                if (isFromDetailOrIsEmpty) {
                    goBack();
                } else {
                    forwardToPublisher(lastPublisher);
                }
            }
        }
    }

    @Override
    public void deleteCampoSubStrut() throws EMFError {
        deleteCampiSubStrutList();
    }

    private void salvaCampoTipoUd() throws EMFError {
        if (getForm().getCampoSubStrut().postAndValidate(getRequest(), getMessageBox())) {
            BigDecimal idRegolaSubStrut = ((OrgRegolaValSubStrutRowBean) getForm().getRegoleSubStrutList().getTable()
                    .getCurrentRow()).getIdRegolaValSubStrut();
            String tiCampo = getForm().getCampoSubStrut().getTi_campo().parse();
            String nmCampo = getForm().getCampoSubStrut().getNm_campo().parse();
            BigDecimal idRecord = null;
            CostantiDB.TipoCampo campo = CostantiDB.TipoCampo.valueOf(tiCampo);
            switch (campo) {
            case DATO_PROFILO:
                if (StringUtils.isBlank(nmCampo)) {
                    getMessageBox().addError("\u00C8 necessario indicare il dato di profilo di riferimento");
                }
                break;
            case DATO_SPEC_DOC_PRINC:
                idRecord = getForm().getCampoSubStrut().getId_attrib_dati_spec().parse();
                if (idRecord == null) {
                    getMessageBox().addError("\u00C8 necessario indicare il dato specifico di riferimento");
                } else {
                    nmCampo = getForm().getCampoSubStrut().getId_attrib_dati_spec().getDecodedValue();
                }
                break;
            case DATO_SPEC_UNI_DOC:
                idRecord = getForm().getCampoSubStrut().getId_attrib_dati_spec().parse();
                if (idRecord == null) {
                    getMessageBox().addError("\u00C8 necessario indicare il dato specifico di riferimento");
                } else {
                    nmCampo = getForm().getCampoSubStrut().getId_attrib_dati_spec().getDecodedValue();
                }
                break;
            case SUB_STRUT:
                if (StringUtils.isBlank(nmCampo)) {
                    nmCampo = CostantiDB.NomeCampo.SUB_STRUTTURA.name();
                    getForm().getCampoSubStrut().getNm_campo().setValue(CostantiDB.NomeCampo.SUB_STRUTTURA.name());
                }
                idRecord = getForm().getCampoSubStrut().getId_sub_strut().parse();
                if (idRecord == null) {
                    getMessageBox().addError("\u00C8 necessario indicare la sottostruttura");
                }
                break;
            }
            if (!getMessageBox().hasError()) {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                BigDecimal idTipoUnitaDoc = ((OrgRegolaValSubStrutRowBean) getForm().getRegoleSubStrutList().getTable()
                        .getCurrentRow()).getIdTipoUnitaDoc();
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null,
                                null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this), SpagoliteLogUtil.getToolbarSave(
                                getForm().getCampiSubStrutList().getStatus().equals(Status.update)),
                        idTipoUnitaDoc);
                param.setNomeTipoOggetto(SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA);
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                if (getForm().getCampiSubStrutList().getStatus().equals(Status.insert)) {
                    if (!subStrutEjb.existOrgCampoSubStrut(null, idRegolaSubStrut, tiCampo, nmCampo, idRecord)) {
                        Long idCampoValSubStrut = null;
                        try {
                            param.setIdOggetto(idTipoUnitaDoc);
                            idCampoValSubStrut = subStrutEjb.saveCampoSubStrut(param, idRegolaSubStrut, tiCampo,
                                    nmCampo, idRecord);
                        } catch (ParerUserError ex) {
                            getMessageBox().addError(ex.getMessage());
                        }
                        if (!getMessageBox().hasError()) {
                            if (idCampoValSubStrut != null) {
                                getForm().getCampoSubStrut().getId_campo_val_sub_strut()
                                        .setValue(String.valueOf(idCampoValSubStrut));
                            }

                            // Gestisco la lista per avere la riga corrente
                            OrgCampoValSubStrutTableBean table = new OrgCampoValSubStrutTableBean();
                            OrgCampoValSubStrutRowBean row = new OrgCampoValSubStrutRowBean();
                            getForm().getCampoSubStrut().copyToBean(row);
                            table.add(row);
                            table.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                            table.first();

                            getForm().getCampiSubStrutList().setTable(table);
                        }
                    } else {
                        getMessageBox().addError("Per la regola esiste gi\u00E0 un campo contenente i dati inseriti");
                    }
                } else if (getForm().getCampiSubStrutList().getStatus().equals(Status.update)) {
                    OrgCampoValSubStrutRowBean row = (OrgCampoValSubStrutRowBean) getForm().getCampiSubStrutList()
                            .getTable().getCurrentRow();
                    BigDecimal idCampoValSubStrut = row.getIdCampoValSubStrut();
                    if (idCampoValSubStrut == null) {
                        getMessageBox()
                                .addError("Errore inaspettato. Ritentare il caricamento e la modifica del campo");
                    } else if (!subStrutEjb.existOrgCampoSubStrut(idCampoValSubStrut, idRegolaSubStrut, tiCampo,
                            nmCampo, idRecord)) {
                        try {
                            subStrutEjb.saveCampoSubStrut(param, tiCampo, nmCampo, idRecord, idCampoValSubStrut);
                        } catch (ParerUserError ex) {
                            getMessageBox().addError(ex.getMessage());
                        }
                    } else {
                        getMessageBox().addError("Per la regola esiste gi\u00E0 un campo contenente i dati inseriti");
                    }
                }
                if (!getMessageBox().hasError()) {
                    getMessageBox().addInfo("Campo salvato con successo");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);

                    getForm().getCampiSubStrutList().setStatus(Status.view);
                    getForm().getCampoSubStrut().setStatus(Status.view);
                    getForm().getCampoSubStrut().setViewMode();
                }
            }
            forwardToPublisher(Application.Publisher.CAMPO_REGOLA_DETAIL);
        }
    }

    @Override
    public JSONObject triggerCampoSubStrutTi_campoOnTrigger() throws EMFError {
        getForm().getCampoSubStrut().post(getRequest());
        String tiCampo = getForm().getCampoSubStrut().getTi_campo().parse();
        populateNmCampo(tiCampo);

        return getForm().getCampoSubStrut().asJSON();
    }

    public void populateNmCampo(String tiCampo) {
        if (StringUtils.isNotBlank(tiCampo)) {
            BigDecimal idStrut = null;
            if (getSession().getAttribute("provenienzaRegola").equals(Application.Publisher.TIPO_UNITA_DOC_DETAIL)) {
                idStrut = ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                        .getIdStrut();
            } else if (getSession().getAttribute("provenienzaRegola").equals(Application.Publisher.TIPO_DOC_DETAIL)) {
                idStrut = ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow()).getIdStrut();
            }
            BigDecimal idTipoUnitaDoc = ((OrgRegolaValSubStrutRowBean) getForm().getRegoleSubStrutList().getTable()
                    .getCurrentRow()).getIdTipoUnitaDoc();
            BigDecimal idTipoDoc = ((OrgRegolaValSubStrutRowBean) getForm().getRegoleSubStrutList().getTable()
                    .getCurrentRow()).getIdTipoDoc();
            CostantiDB.TipoCampo campo = CostantiDB.TipoCampo.valueOf(tiCampo);
            switch (campo) {
            case DATO_PROFILO:
                getForm().getCampoSubStrut().getNm_campo().setDecodeMap(ComboGetter.getMappaNmCampoDatoProfilo());
                getForm().getCampoSubStrut().getId_sub_strut().setDecodeMap(new DecodeMap());
                getForm().getCampoSubStrut().getId_attrib_dati_spec().setDecodeMap(new DecodeMap());
                break;
            case DATO_SPEC_DOC_PRINC:
                getForm().getCampoSubStrut().getNm_campo().setDecodeMap(new DecodeMap());
                getForm().getCampoSubStrut().getId_sub_strut().setDecodeMap(new DecodeMap());
                getForm().getCampoSubStrut().getId_attrib_dati_spec()
                        .setDecodeMap(
                                DecodeMap.Factory.newInstance(
                                        datiSpecEjb.getDecAttribDatiSpecCombo(idStrut,
                                                CostantiDB.TipiEntitaSacer.DOC.name(), idTipoDoc),
                                        "id_attrib_dati_spec", "nm_attrib_dati_spec"));
                break;
            case DATO_SPEC_UNI_DOC:
                getForm().getCampoSubStrut().getNm_campo().setDecodeMap(new DecodeMap());
                getForm().getCampoSubStrut().getId_sub_strut().setDecodeMap(new DecodeMap());
                getForm().getCampoSubStrut().getId_attrib_dati_spec()
                        .setDecodeMap(DecodeMap.Factory.newInstance(
                                datiSpecEjb.getDecAttribDatiSpecCombo(idStrut,
                                        CostantiDB.TipiEntitaSacer.UNI_DOC.name(), idTipoUnitaDoc),
                                "id_attrib_dati_spec", "nm_attrib_dati_spec"));
                break;
            case SUB_STRUT:
                getForm().getCampoSubStrut().getNm_campo().setDecodeMap(ComboGetter.getMappaNmCampoSubStruttura());
                getForm().getCampoSubStrut().getNm_campo().setValue(CostantiDB.NomeCampo.SUB_STRUTTURA.name());
                getForm().getCampoSubStrut().getId_sub_strut().setDecodeMap(DecodeMap.Factory
                        .newInstance(subStrutEjb.getOrgSubStrutTableBean(idStrut), "id_sub_strut", "nm_sub_strut"));
                getForm().getCampoSubStrut().getId_attrib_dati_spec().setDecodeMap(new DecodeMap());
                break;
            }
        } else {
            getForm().getCampoSubStrut().getNm_campo().setDecodeMap(new DecodeMap());
            getForm().getCampoSubStrut().getId_sub_strut().setDecodeMap(new DecodeMap());
            getForm().getCampoSubStrut().getId_attrib_dati_spec().setDecodeMap(new DecodeMap());
        }
    }

    @Override
    public void updateCampiSubStrutList() throws EMFError {
        getForm().getCampoSubStrut().setEditMode();

        getForm().getCampoSubStrut().setStatus(Status.update);
        getForm().getCampiSubStrutList().setStatus(Status.update);
    }

    @Override
    public void updateCampoSubStrut() throws EMFError {
        updateCampiSubStrutList();
    }

    private void redirectToCreaCriterioRaggrPage() throws EMFError {
        CriteriRaggruppamentoForm form = new CriteriRaggruppamentoForm();
        ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) form
                .getComponent(form.getCriterioRaggrList().getName()))
                        .setTable(getForm().getCriteriRaggruppamentoList().getTable());
        BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
        OrgStrutRowBean struttura = struttureEjb.getOrgStrutRowBean(idStrut);
        form.getIdFields().getId_strut().setValue("" + idStrut);
        if (getLastPublisher().equals(Application.Publisher.REGISTRO_UNITA_DOC_DETAIL)) {
            form.getIdFields().getId_registro_unita_doc().setValue(
                    "" + ((DecRegistroUnitaDocRowBean) getForm().getRegistroUnitaDocList().getTable().getCurrentRow())
                            .getIdRegistroUnitaDoc());
        } else if (getLastPublisher().equals(Application.Publisher.TIPO_UNITA_DOC_DETAIL)) {
            form.getIdFields().getId_tipo_unita_doc()
                    .setValue("" + ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                            .getIdTipoUnitaDoc());
        } else if (getLastPublisher().equals(Application.Publisher.TIPO_DOC_DETAIL)) {
            form.getIdFields().getId_tipo_doc().setValue(
                    "" + ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow()).getIdTipoDoc());
        }
        redirectToAction(Application.Actions.CRITERI_RAGGRUPPAMENTO,
                "?operation=listNavigationOnClick&navigationEvent=" + getNavigationEvent() + "&table="
                        + form.getCriterioRaggrList().getName() + "&riga="
                        + getForm().getCriteriRaggruppamentoList().getTable().getCurrentRowIndex() + "&cessato="
                        + struttura.getFlCessato(),
                form);
    }

    /**
     * Inizializza le combo ambiente/ente/struttura del DETTAGLIO DI UN CRITERIO di raggruppamento, ricavando i valori
     * da una struttura impostata
     *
     * @param criteriForm
     *            criteri
     * @param idStrut
     *            id struttura
     */
    public void initComboAmbienteEnteStrutCreaCriteriRaggr(CriteriRaggruppamentoForm criteriForm, BigDecimal idStrut) {

        if (idStrut != null) {
            // Ricavo id struttura, ente ed ambiente attuali
            BigDecimal idEnte = monitoraggioHelper.getIdEnte(idStrut);
            BigDecimal idAmbiente = monitoraggioHelper.getIdAmbiente(idEnte);

            // Inizializzo le combo settando la struttura corrente
            OrgAmbienteTableBean tmpTableBeanAmbiente = null;
            OrgEnteTableBean tmpTableBeanEnte = null;
            OrgStrutTableBean tmpTableBeanStruttura = null;
            try {
                // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
                tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());

                // Ricavo i valori della combo ENTE
                tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(),
                        idAmbiente.longValue(), Boolean.FALSE);

                // Ricavo i valori della combo STRUTTURA
                tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte,
                        Boolean.FALSE);

            } catch (Exception ex) {
                logger.error("Errore in ricerca ambiente", ex);
            }

            DecodeMap mappaAmbiente = new DecodeMap();
            mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
            criteriForm.getCreaCriterioRaggr().getId_ambiente().setDecodeMap(mappaAmbiente);
            criteriForm.getCreaCriterioRaggr().getId_ambiente().setValue(idAmbiente.toString());

            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
            criteriForm.getCreaCriterioRaggr().getId_ente().setDecodeMap(mappaEnte);
            criteriForm.getCreaCriterioRaggr().getId_ente().setValue(idEnte.toString());

            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
            criteriForm.getCreaCriterioRaggr().getId_strut().setDecodeMap(mappaStrut);
            criteriForm.getCreaCriterioRaggr().getId_strut().setValue(idStrut.toString());
        }
    }

    @Override
    public void updateCriteriRaggruppamentoList() throws EMFError {
        redirectToCreaCriterioRaggrPage();
    }

    @Override
    public void deleteCriteriRaggruppamentoList() throws EMFError {
        redirectToCreaCriterioRaggrPage();
    }

    /**
     * Metodo richiamato dal link per accedere alla pagina di dettaglio Registro
     *
     * @throws EMFError
     *             errore generico
     */
    public void loadDettaglioRegistro() throws EMFError {
        BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
        String riga = getRequest().getParameter("riga");
        BigDecimal numberRiga = BigDecimal.ZERO;
        if (StringUtils.isNotBlank(riga)) {
            numberRiga = new BigDecimal(riga);
        }
        // Recupero il registro
        String registroAmmesso = ((DecVLisTiUniDocAmsTableBean) getForm().getTipoUnitaDocAmmessoList().getTable())
                .getRow(numberRiga.intValue()).getCdRegistroUnitaDoc();
        DecRegistroUnitaDocRowBean registroRow = registroEjb.getDecRegistroUnitaDocRowBean(registroAmmesso, idStrut);
        // Setto la tabella dei registri aggiungendo solo quello recuperato
        DecRegistroUnitaDocTableBean registroTable = new DecRegistroUnitaDocTableBean();
        registroTable.add(registroRow);
        registroTable.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getRegistroUnitaDocList().setTable(registroTable);
        setTableName(getForm().getRegistroUnitaDocList().getName());
        setNavigationEvent(NE_DETTAGLIO_VIEW);
        OrgStrutRowBean struttura = struttureEjb.getOrgStrutRowBean(idStrut);
        if ("1".equals(struttura.getFlCessato())) {
            getRequest().setAttribute("cessato", true);
        }
        // lancio loadDettaglio() e dettaglioOnClick()
        loadDettaglio();
        dettaglioOnClick();
    }

    /**
     * Metodo richiamato dal link per accedere alla pagina di dettaglio Tipologia Ud
     *
     * @throws EMFError
     *             errore generico
     */
    public void loadDettaglioTipoUd() throws EMFError {
        BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
        String riga = getRequest().getParameter("riga");
        BigDecimal numberRiga = BigDecimal.ZERO;
        if (StringUtils.isNotBlank(riga)) {
            numberRiga = new BigDecimal(riga);
        }
        // Recupero il tipo ud
        String tipoUd = ((DecVLisTiUniDocAmsTableBean) getForm().getRegistroTipoUnitaDocAmmessoList().getTable())
                .getRow(numberRiga.intValue()).getNmTipoUnitaDoc();
        DecTipoUnitaDocRowBean tipoUdRow = tipoUnitaDocEjb.getDecTipoUnitaDocRowBean(tipoUd, idStrut);
        // Setto la tabella dei registri aggiungendo solo quello recuperato
        DecTipoUnitaDocTableBean tipoUdTable = new DecTipoUnitaDocTableBean();
        tipoUdTable.add(tipoUdRow);
        tipoUdTable.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getTipoUnitaDocList().setTable(tipoUdTable);
        setTableName(getForm().getTipoUnitaDocList().getName());
        setNavigationEvent(NE_DETTAGLIO_VIEW);
        OrgStrutRowBean struttura = struttureEjb.getOrgStrutRowBean(idStrut);
        if ("1".equals(struttura.getFlCessato())) {
            getRequest().setAttribute("cessato", true);
        }
        // lancio loadDettaglio() e dettaglioOnClick()
        loadDettaglio();
        dettaglioOnClick();
    }

    /**
     * Metodo richiamato dal link per accedere alla pagina di dettaglio Tipo Struttura Documento
     *
     * @throws EMFError
     *             errore generico
     */
    public void loadDettaglioTipoStrutDoc() throws EMFError {
        BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
        String riga = getRequest().getParameter("riga");
        BigDecimal numberRiga = BigDecimal.ZERO;
        if (StringUtils.isNotBlank(riga)) {
            numberRiga = new BigDecimal(riga);
        }
        // Recupero il tipo strut doc
        BigDecimal idTipoStrutDoc = ((DecTipoStrutDocAmmessoTableBean) getForm().getTipoStrutDocAmmessoDaTipoDocList()
                .getTable()).getRow(numberRiga.intValue()).getIdTipoStrutDoc();
        DecTipoStrutDocRowBean tipoStrutDocRow = tipoStrutDocEjb.getDecTipoStrutDocRowBean(idTipoStrutDoc, idStrut);
        // Setto la tabella dei tipi struttura documento aggiungendo solo quella
        // recuperata
        DecTipoStrutDocTableBean tipoStrutDocTable = new DecTipoStrutDocTableBean();
        tipoStrutDocTable.add(tipoStrutDocRow);
        tipoStrutDocTable.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getTipoStrutDocAmmessoDaTipoDocList().setTable(tipoStrutDocTable);
        setTableName(getForm().getTipoStrutDocAmmessoDaTipoDocList().getName());
        setNavigationEvent(NE_DETTAGLIO_VIEW);
        redirectToTipoStrutDocPage();
    }

    public void loadWizard() throws EMFError {
        getForm().getInserimentoWizard().reset();
        getForm().getDatiAnniParte().reset();
        getForm().getDatiParti().reset();
        DecAaRegistroUnitaDocRowBean row = (DecAaRegistroUnitaDocRowBean) getForm().getAaRegistroUnitaDocList()
                .getTable().getCurrentRow();
        initWizard(row);

    }

    public void initWizard(DecAaRegistroUnitaDocRowBean row) throws EMFError {
        getSession().removeAttribute(PARAMETER_ID_PARTI_ELIMINATE);
        getForm().getDatiAnniParte().setEditMode();
        getForm().getDatiAnniParte().copyFromBean(row);
        DecParteNumeroRegistroTableBean parti = registroEjb
                .getDecParteNumeroRegistroTableBean(row.getIdAaRegistroUnitaDoc());
        getForm().getPartiList().setTable(parti);
        getForm().getDatiDescFormatoNumero().setEditMode();
        getForm().getDatiDescFormatoNumero().copyFromBean(row);
    }

    @Override
    public boolean inserimentoWizardOnSave() throws EMFError {
        DecParteNumeroRegistroTableBean tb = (DecParteNumeroRegistroTableBean) getForm().getPartiList().getTable();
        tb.clearSortingRule();
        tb.addSortingRule(DecParteNumeroRegistroTableDescriptor.COL_NI_PARTE_NUMERO_REGISTRO);
        tb.sort();
        String tmpML = this.getMaxLenNumeroChiaveFromSession();
        int maxLenPossibile = 50;
        if (tmpML != null && !tmpML.isEmpty()) {
            maxLenPossibile = Integer.parseInt(tmpML);
        }
        BigDecimal min = getForm().getDatiAnniParte().getAa_min_registro_unita_doc().parse();
        BigDecimal max = getForm().getDatiAnniParte().getAa_max_registro_unita_doc().parse();
        String cdFormatoNumero = getForm().getDatiDescFormatoNumero().getCd_formato_numero().parse();
        String dsFormatoNumero = getForm().getDatiDescFormatoNumero().getDs_formato_numero().parse();
        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this), SpagoliteLogUtil
                            .getToolbarSave(getForm().getAaRegistroUnitaDocList().getStatus().equals(Status.update)));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            if (getForm().getAaRegistroUnitaDocList().getStatus().equals(Status.update)) {
                DecAaRegistroUnitaDocRowBean row = (DecAaRegistroUnitaDocRowBean) getForm().getAaRegistroUnitaDocList()
                        .getTable().getCurrentRow();
                Set<BigDecimal> idPartiEliminate = (Set<BigDecimal>) getSession()
                        .getAttribute(PARAMETER_ID_PARTI_ELIMINATE);
                registroEjb.saveDecAaRegistroUnitaDoc(param, row.getIdAaRegistroUnitaDoc(), min, max, tb,
                        idPartiEliminate, cdFormatoNumero, dsFormatoNumero);
                registroEjb.checkUdNelPeriodoValidita(row.getIdAaRegistroUnitaDoc());
            } else {
                BigDecimal idRegistroUnitaDoc = getForm().getRegistroUnitaDoc().getId_registro_unita_doc().parse();
                BigDecimal idAaRegistroUnitaDoc = registroEjb.saveDecAaRegistroUnitaDoc(param, idRegistroUnitaDoc, min,
                        max, tb, cdFormatoNumero, dsFormatoNumero);
                if (idAaRegistroUnitaDoc != null) {
                    registroEjb.checkUdNelPeriodoValidita(idAaRegistroUnitaDoc);
                } else {
                    throw new ParerUserError("Errore inaspettato: periodo di validit\u00E0 non salvato correttamente");
                }
            }

            long maxSizeNum = 0;
            List<Object> maxSizes = tb.toList(DecParteNumeroRegistroTableDescriptor.COL_NI_MAX_CHAR_PARTE);
            for (int i = 0; i < maxSizes.size(); i++) {
                Object tmpobj = maxSizes.get(i);
                if (tmpobj != null) {
                    maxSizeNum += ((BigDecimal) tmpobj).longValue();
                } else {
                    BigDecimal niMinCharParte = ((DecParteNumeroRegistroRowBean) tb.getRow(i)).getNiMinCharParte();
                    String tipoPadding = ((DecParteNumeroRegistroRowBean) tb.getRow(i)).getTiPadSxParte();
                    String tipoDato = ((DecParteNumeroRegistroRowBean) tb.getRow(i)).getTiCharParte();
                    if ((StringUtils.isNotEmpty(tipoPadding)
                            && tipoPadding.equals(ConfigRegAnno.TipiPadding.RIEMPI_0_A_SX_LESS12.name()))
                            || tipoDato.equals(KeyOrdUtility.TipiCalcolo.NUMERICO.name())
                            || tipoDato.equals(KeyOrdUtility.TipiCalcolo.NUMERICO_GENERICO.name())) {
                        maxSizeNum += 12L;
                    } else {
                        maxSizeNum += niMinCharParte.longValue();
                    }
                }
            }
            if (maxSizeNum > maxLenPossibile) {
                getMessageBox().addWarning("Periodo di validit\u00E0 salvato con successo, ma la dimensione "
                        + "minima normalizzata del numero (" + maxSizeNum + ") supera " + maxLenPossibile
                        + " caratteri. " + "Il versamento potrà avvenire solo attraverso la forzatura");
            } else {
                getMessageBox().addInfo("Periodo di validit\u00E0 salvato con successo");
            }
            getMessageBox().setViewMode(MessageBox.ViewMode.plain);
            getForm().getAaRegistroUnitaDocList().setStatus(Status.view);
            goBackTo(Application.Publisher.REGISTRO_UNITA_DOC_DETAIL);
        } catch (ParerUserError ex) {
            getMessageBox().addError("Il periodo non pu\u00F2 essere salvato: " + ex.getDescription());
        } catch (Exception ex) {
            logger.error("Eccezione generica nel salvataggio del periodo di validit\u00E0", ex);
            getMessageBox().addError("Si \u00E8 verificata un'eccezione nel salvataggio del periodo di validit\u00E0");
        }
        return !getMessageBox().hasError();
    }

    @Override
    public void inserimentoWizardOnCancel() throws EMFError {
        getForm().getAaRegistroUnitaDocList().setStatus(Status.view);
        goBack();
    }

    @Override
    public String getDefaultInserimentoWizardPublisher() throws EMFError {
        return Application.Publisher.AA_REGISTRO_UNITA_DOC_WIZARD;
    }

    @Override
    public void inserimentoWizardAnniParteOnEnter() throws EMFError {
        getForm().getDatiAnniParte().setEditMode();
        forwardToPublisher(getDefaultInserimentoWizardPublisher());
    }

    @Override
    public boolean inserimentoWizardAnniParteOnExit() throws EMFError {
        forwardToPublisher(getDefaultInserimentoWizardPublisher());
        if (getForm().getDatiAnniParte().postAndValidate(getRequest(), getMessageBox())) {
            String annoValidoMinimo = configurationHelper.getValoreParamApplic(
                    CostantiDB.ParametroAppl.REG_ANNO_VALID_MINIMO, null, null, null, null,
                    CostantiDB.TipoAplVGetValAppart.APPLIC);
            if (StringUtils.isBlank(annoValidoMinimo) || !StringUtils.isNumeric(annoValidoMinimo)) {
                getMessageBox().addError(
                        "Parametro di configurazione applicativo per l'anno minimo di validit\u00E0 non valido");
            } else {
                int dbAnnoMinimo = Integer.parseInt(annoValidoMinimo);
                if (StringUtils.isBlank(getForm().getDatiAnniParte().getAa_min_registro_unita_doc().getValue())
                        || Integer.parseInt(getForm().getDatiAnniParte().getAa_min_registro_unita_doc()
                                .getValue()) < dbAnnoMinimo) {
                    getMessageBox().addError(
                            "Errore di compilazione: anno di inizio validit\u00E0 inferiore a " + annoValidoMinimo);
                } else if (StringUtils
                        .isNotBlank(getForm().getDatiAnniParte().getAa_max_registro_unita_doc().getValue())
                        && Integer.parseInt(getForm().getDatiAnniParte().getAa_max_registro_unita_doc()
                                .getValue()) < Integer.parseInt(
                                        getForm().getDatiAnniParte().getAa_min_registro_unita_doc().getValue())) {
                    getMessageBox().addError("Attenzione intervallo non valido");
                }

                if (!getMessageBox().hasError()) {
                    BigDecimal max = getForm().getDatiAnniParte().getAa_max_registro_unita_doc().parse();
                    BigDecimal min = getForm().getDatiAnniParte().getAa_min_registro_unita_doc().parse();
                    if (max == null) {
                        max = new BigDecimal(9999);
                    }
                    try {
                        BigDecimal idRegistroUnitaDoc = null;
                        BigDecimal idAaRegistroUnitaDoc = null;
                        if (getForm().getAaRegistroUnitaDocList().getStatus().equals(Status.update)) {
                            DecAaRegistroUnitaDocRowBean row = (DecAaRegistroUnitaDocRowBean) getForm()
                                    .getAaRegistroUnitaDocList().getTable().getCurrentRow();
                            idRegistroUnitaDoc = row.getIdRegistroUnitaDoc();
                            idAaRegistroUnitaDoc = row.getIdAaRegistroUnitaDoc();
                        } else {
                            idRegistroUnitaDoc = getForm().getRegistroUnitaDoc().getId_registro_unita_doc().parse();
                        }
                        registroEjb.checkDecAaRegistroUnitaDoc(idAaRegistroUnitaDoc, idRegistroUnitaDoc, min, max);
                    } catch (ParerUserError e) {
                        getMessageBox().addError(e.getDescription());
                    }
                }
            }
        }
        return !getMessageBox().hasError();
    }

    @Override
    public void inserimentoWizardPartiOnEnter() throws EMFError {
        getForm().getPartiInserimento().clear();
        getForm().getPartiInserimento().setEditMode();
        getForm().getPartiList().setHideDeleteButton(false);
        getForm().getPartiList().setHideUpdateButton(false);
        getForm().getPartiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getPartiList().getTable().first();
        getForm().getPartiList().setStatus(Status.insert);
        // Inizializzo la combo "Ti_parte" facente parte (ahah il gioco di parole!) di
        // una lista
        getForm().getPartiList().getTi_parte()
                .setDecodeMap(ComboGetter.getMappaTiParte(ConfigRegAnno.TiParte.values()));
        setDecodeMaps();
        increaseNiNumeroRegistro();
        forwardToPublisher(getDefaultInserimentoWizardPublisher());
    }

    @Override
    public boolean inserimentoWizardPartiOnExit() throws EMFError {
        DecParteNumeroRegistroTableBean tb = (DecParteNumeroRegistroTableBean) getForm().getPartiList().getTable();
        tb.clearSortingRule();
        tb.addSortingRule(DecParteNumeroRegistroTableDescriptor.COL_NI_PARTE_NUMERO_REGISTRO);
        tb.sort();
        if (tb.isEmpty()) {
            getMessageBox().addError("Deve essere definita almeno una parte per il registro");
        }
        if (!getMessageBox().hasError()) {
            List<Object> tipiCarattere = tb.toList(DecParteNumeroRegistroTableDescriptor.COL_TI_CHAR_PARTE);
            if (tipiCarattere.size() > 1 && tipiCarattere.contains(KeyOrdUtility.TipiCalcolo.GENERICO.name())) {
                getMessageBox().addError(
                        "Il carattere GENERICO \u00E8 ammesso solo se il registro si compone di una sola parte");
            }
        }
        String tmpML = this.getMaxLenNumeroChiaveFromSession();
        int maxLenPossibile = 50;
        if (tmpML != null && !tmpML.isEmpty()) {
            maxLenPossibile = Integer.parseInt(tmpML);
        }
        if (!getMessageBox().hasError()) {
            BigDecimal tmpMinCharParte = tb.sum(DecParteNumeroRegistroTableDescriptor.COL_NI_MIN_CHAR_PARTE);
            if (tmpMinCharParte != null) {
                int sumNiMinCharParte = tmpMinCharParte.intValue();

                // integro nel conteggio anche la dimensione dei separatori
                List<Object> tiCharSeps = tb.toList(DecParteNumeroRegistroTableDescriptor.COL_TI_CHAR_SEP);
                for (Object tmpobj : tiCharSeps) {
                    if (StringUtils.isNotEmpty((String) tmpobj)) {
                        sumNiMinCharParte++;
                    }
                }

                if (sumNiMinCharParte > maxLenPossibile) {
                    getMessageBox()
                            .addError("La somma delle dimensioni minime delle parti deve essere minore o uguale a "
                                    + maxLenPossibile + " caratteri (la somma calcolata risulta essere "
                                    + sumNiMinCharParte + " caratteri).");
                }
            }
        }
        if (!getMessageBox().hasError()) {
            List<Object> progressivi = tb.toList(DecParteNumeroRegistroTableDescriptor.COL_NI_PARTE_NUMERO_REGISTRO);
            Set<Object> set = new HashSet<>(progressivi);
            if (set.size() < progressivi.size()) {
                getMessageBox().addError("Almeno un numero d'ordine \u00E8 definito su pi\u00F9 di una parte");
            }
        }
        if (!getMessageBox().hasError()) {
            List<Object> niMaxCharParti = tb.toList(DecParteNumeroRegistroTableDescriptor.COL_NI_MAX_CHAR_PARTE,
                    new SortingRule[] { SortingRule.getAscending(
                            DecParteNumeroRegistroRowBean.TABLE_DESCRIPTOR.COL_NI_PARTE_NUMERO_REGISTRO) });
            int numeroNulli = 0;
            for (Object niMaxObject : niMaxCharParti) {
                BigDecimal niMax = (BigDecimal) niMaxObject;
                if (niMax == null) {
                    numeroNulli++;
                }
            }
            if (numeroNulli > 1) {
                getMessageBox().addError(
                        "Il numero massimo di caratteri pu\u00F2 NON essere valorizzato solo sull'ultima parte del registro");
            } else if (numeroNulli > 0) {
                if (niMaxCharParti.get(niMaxCharParti.size() - 1) != null) {
                    getMessageBox().addError(
                            "Il numero massimo di caratteri pu\u00F2 NON essere valorizzato solo sull'ultima parte del registro");
                }
            }
        }
        if (!getMessageBox().hasError()) {
            List<Object> tiParteList = tb.toList(DecParteNumeroRegistroTableDescriptor.COL_TI_PARTE);
            int contaTiParte = 0;
            for (Object tiParte : tiParteList) {
                if (tiParte != null) {
                    if (((String) tiParte).equals(ConfigRegAnno.TiParte.PROGR.name())) {
                        contaTiParte++;
                    }
                    if (contaTiParte > 1) {
                        // Se la somma dei record vale più di 1
                        getMessageBox().addError(
                                "La coincidenza con il progressivo può essere valorizzata solo su una parte del registro");
                        break;
                    }
                }
            }

        }
        if (!getMessageBox().hasError()) {
            List<Object> tiCharSeps = tb.toList(DecParteNumeroRegistroTableDescriptor.COL_TI_CHAR_SEP);
            boolean existingLastCharSep = false;
            boolean emptyNotLastCharSepOrWrongNi = false;
            for (int i = 0; i < tiCharSeps.size(); i++) {
                String tiCharSep = (String) tiCharSeps.get(i);
                if (i != (tiCharSeps.size() - 1)) {
                    BigDecimal niMinCharParte = ((DecParteNumeroRegistroRowBean) tb.getRow(i)).getNiMinCharParte();
                    BigDecimal niMaxCharParte = ((DecParteNumeroRegistroRowBean) tb.getRow(i)).getNiMaxCharParte();
                    if (StringUtils.isEmpty(tiCharSep) && niMinCharParte.compareTo(niMaxCharParte) != 0) {
                        emptyNotLastCharSepOrWrongNi = true;
                    }
                } else if (StringUtils.isNotEmpty(tiCharSep)) {
                    existingLastCharSep = true;
                }
            }
            if (existingLastCharSep) {
                getMessageBox().addError(
                        "Sull'ultima parte del registro non \u00E8 possibile inserire il carattere separatore");
            }
            if (emptyNotLastCharSepOrWrongNi) {
                getMessageBox().addError(
                        "Tutte le parti devono avere il carattere separatore valorizzato o l'indicazione del numero fisso di caratteri accettato (numero minimo di caratteri deve coincidere con numero massimo di caratteri)");
            }
        }
        return !getMessageBox().hasError();
    }

    @Override
    public void inserimentoWizardDescFormatoNumeroOnEnter() throws EMFError {
        // getForm().getDatiDescFormatoNumero().clear();
        // getForm().getDatiDescFormatoNumero().setEditMode();
        // Tolgo le operazioni su lista parti
        getForm().getPartiList().setHideDeleteButton(true);
        getForm().getPartiList().setHideUpdateButton(true);

        if (getForm().getAaRegistroUnitaDocList().getStatus().equals(Status.insert)) {
            if (getForm().getPartiList().getTable().size() == 1
                    && getForm().getAaRegistroUnitaDocList().getStatus().equals(Status.insert)) {
                getForm().getDatiDescFormatoNumero().getCd_formato_numero()
                        .setValue(getForm().getPartiList().getTable().getRow(0).getString("nm_parte_numero_registro"));
                getForm().getDatiDescFormatoNumero().getDs_formato_numero()
                        .setValue(getForm().getPartiList().getTable().getRow(0).getString("ds_parte_numero_registro"));
            } else {
                getForm().getDatiDescFormatoNumero().getCd_formato_numero().setValue("");
                getForm().getDatiDescFormatoNumero().getDs_formato_numero().setValue("");
            }
        }
        forwardToPublisher(getDefaultInserimentoWizardPublisher());
    }

    @Override
    public boolean inserimentoWizardDescFormatoNumeroOnExit() throws EMFError {
        forwardToPublisher(getDefaultInserimentoWizardPublisher());
        if (getForm().getDatiDescFormatoNumero().postAndValidate(getRequest(), getMessageBox())) {
            return true;
        }
        return false;
    }

    @Override
    public void addParte() throws EMFError {
        if (getForm().getPartiInserimento().postAndValidate(getRequest(), getMessageBox())) {

            // Valido i campi con i dati della parte inserita/modificata
            // if (getForm().getPartiInserimento().validate(getMessageBox())) {
            BigDecimal min = getForm().getPartiInserimento().getNi_min_char_parte().parse();
            BigDecimal max = getForm().getPartiInserimento().getNi_max_char_parte().parse();
            String tiCharParte = getForm().getPartiInserimento().getTi_char_parte().parse();
            String tipoRiempimento = getForm().getPartiInserimento().getTi_pad_sx_parte().parse();
            String dlValoreParte = getForm().getPartiInserimento().getDl_valori_parte().parse();

            DecParteNumeroRegistroRowBean row = null;
            Integer rowIndex = null;

            // Se sono in update, prendo la riga che voglio modificare
            if (getForm().getPartiList().getStatus().equals(Status.update)) {
                row = (DecParteNumeroRegistroRowBean) getForm().getPartiList().getTable().getCurrentRow();
                rowIndex = getForm().getPartiList().getTable().getCurrentRowIndex();
            }

            // Controllo carattere separatore
            String separatore = getForm().getPartiInserimento().getTi_char_sep().getValue();
            if (StringUtils.isNotEmpty(separatore) && !separatore.equals("SPAZIO")) {
                Matcher matcher = regPattern.matcher(separatore);
                if (matcher.matches()) {
                    getMessageBox().addError("Carattere non utilizzabile come elemento separatore");
                }
            }

            // Controllo esistenza nome della parte
            if (!getMessageBox().hasError()) {
                String nomeParte = getForm().getPartiInserimento().getNm_parte_numero_registro().parse();
                List<Object> nomi = ((DecParteNumeroRegistroTableBean) getForm().getPartiList().getTable())
                        .toList(DecParteNumeroRegistroTableDescriptor.COL_NM_PARTE_NUMERO_REGISTRO);
                if (getForm().getPartiList().getStatus().equals(Status.insert)) {
                    if (nomi.contains(nomeParte)) {
                        getMessageBox().addError("Esiste gi\u00E0 nella lista una parte con nome " + nomeParte);
                    }
                } else {
                    String oldName = (row != null ? row.getNmParteNumeroRegistro() : null);
                    if (!nomeParte.equals(oldName) && nomi.contains(nomeParte)) {
                        getMessageBox().addError("Esiste gi\u00E0 nella lista una parte con nome " + nomeParte);
                    }
                }
            }

            // Controllo tipo di riempimento
            if (!getMessageBox().hasError()) {
                if (StringUtils.isNotBlank(tipoRiempimento)) {
                    if (max != null) {
                        getMessageBox().addError(
                                "Tipo di riempimento non pu\u00F2 essere valorizzato nel caso in cui 'Numero massimo di caratteri' sia valorizzato o 'Caratteri ammessi' sia diverso da ALFANUMERICO o ALFABETICO");
                    } else if (StringUtils.isBlank(tiCharParte)
                            || (!tiCharParte.equals(KeyOrdUtility.TipiCalcolo.ALFABETICO.name())
                                    && !tiCharParte.equals(KeyOrdUtility.TipiCalcolo.ALFANUMERICO.name()))) {
                        getMessageBox().addError(
                                "Tipo di riempimento non pu\u00F2 essere valorizzato nel caso in cui 'Numero massimo di caratteri' sia valorizzato o 'Caratteri ammessi' sia diverso da ALFANUMERICO o ALFABETICO");
                    }
                }
            }

            // Controllo valori accettati
            if (!getMessageBox().hasError()) {
                if (StringUtils.isNotBlank(dlValoreParte)) {
                    String dlValoreCopy = StringUtils.deleteWhitespace(dlValoreParte);
                    Matcher insiemeMatcher = insiemePattern.matcher(dlValoreCopy);
                    Matcher rangeMatcher = rangePattern.matcher(dlValoreCopy);
                    BigDecimal realMax = max != null ? max : new BigDecimal(9999);
                    int minInt = min.intValue();
                    int maxInt = realMax.intValue();
                    if (insiemeMatcher.matches()) {
                        if (dlValoreCopy.contains(",")) {
                            // Contiene più di un valore
                            String[] valori = StringUtils.split(dlValoreCopy, ",");
                            for (String valoreStr : valori) {
                                if (valoreStr.length() > maxInt || valoreStr.length() < minInt) {
                                    getMessageBox().addError(
                                            "I valori accettati non sono coerenti con il numero di caratteri indicato");
                                    break;
                                }
                            }
                        } else // Contiene un solo valore
                        if (dlValoreCopy.length() > maxInt || dlValoreCopy.length() < minInt) {
                            getMessageBox().addError(
                                    "I valori accettati non sono coerenti con il numero di caratteri indicato");
                        }
                    } else if (rangeMatcher.matches()) {
                        Pattern singlePattern = Pattern.compile("<(\\d+)>");
                        Matcher matcher = singlePattern.matcher(dlValoreCopy);
                        BigDecimal[] valori = new BigDecimal[2];
                        int counter = 0;
                        while (matcher.find()) {
                            String valoreStr = matcher.group(1);
                            BigDecimal valore = new BigDecimal(valoreStr);
                            valori[counter++] = valore;
                            if (valoreStr.length() > maxInt || valoreStr.length() < minInt) {
                                getMessageBox().addError(
                                        "I valori accettati non sono coerenti con il numero di caratteri indicato");
                                break;
                            }
                        }
                        if (!getMessageBox().hasError()) {
                            if (valori[0].compareTo(valori[1]) > 0) {
                                getMessageBox().addError(
                                        "Se i valori accettati sono un insieme, ogni valore deve essere separato mediante \",\" senza spazi; se i valori accettati sono definiti da un range numerico, deve assumere formato &lt;valore minimo&gt;-&lt;valore massimo&gt;");
                            }
                        }
                    } else {
                        getMessageBox().addError(
                                "Se i valori accettati sono un insieme, ogni valore deve essere separato mediante \",\" senza spazi; se i valori accettati sono definiti da un range numerico, deve assumere formato &lt;valore minimo&gt;-&lt;valore massimo&gt;");
                    }
                }
            }

            // Se tutti i controlli sono andati a buon fine, inserisco/modifico il record
            // nella lista a video
            if (!getMessageBox().hasError()) {
                DecParteNumeroRegistroTableBean tb = (DecParteNumeroRegistroTableBean) getForm().getPartiList()
                        .getTable();
                tb.clearSortingRule();
                tb.addSortingRule(DecParteNumeroRegistroTableDescriptor.COL_NI_PARTE_NUMERO_REGISTRO);

                if (getForm().getPartiList().getStatus().equals(Status.insert)) {
                    DecParteNumeroRegistroRowBean tmp = new DecParteNumeroRegistroRowBean();
                    getForm().getPartiInserimento().copyToBean(tmp);
                    tb.add(tmp);
                } else {
                    getForm().getPartiInserimento().copyToBean(row);
                    tb.remove(rowIndex);
                    tb.add(row);
                }

                tb.sort();
                resetPartiInserimento();
                getForm().getPartiList().setStatus(Status.insert);
            }
        }
        forwardToPublisher(getDefaultInserimentoWizardPublisher());
    }

    @Override
    public void cleanParte() throws EMFError {
        resetPartiInserimento();
        forwardToPublisher(getDefaultInserimentoWizardPublisher());
    }

    private void resetPartiInserimento() {
        getForm().getPartiInserimento().clear();
        getForm().getPartiInserimento().setEditMode();
        getForm().getPartiList().setStatus(Status.insert);
        increaseNiNumeroRegistro();
        setDecodeMaps();
    }

    @Override
    public void deletePartiList() throws EMFError {
        DecParteNumeroRegistroTableBean partiTableBean = (DecParteNumeroRegistroTableBean) getForm().getPartiList()
                .getTable();
        int index = partiTableBean.getCurrentRowIndex();
        DecParteNumeroRegistroRowBean currentRow = (DecParteNumeroRegistroRowBean) partiTableBean.getCurrentRow();
        if (currentRow.getIdParteNumeroRegistro() != null) {
            Set<BigDecimal> idParti = (Set<BigDecimal>) getSession().getAttribute(PARAMETER_ID_PARTI_ELIMINATE);
            if (idParti == null) {
                idParti = new HashSet<>();
            }
            idParti.add(currentRow.getIdParteNumeroRegistro());
            getSession().setAttribute(PARAMETER_ID_PARTI_ELIMINATE, idParti);
        }
        getForm().getPartiList().remove(index);

        index = 1;
        partiTableBean.clearSortingRule();
        partiTableBean.addSortingRule(DecParteNumeroRegistroTableDescriptor.COL_NI_PARTE_NUMERO_REGISTRO);
        partiTableBean.sort();
        for (DecParteNumeroRegistroRowBean row : partiTableBean) {
            row.setNiParteNumeroRegistro(new BigDecimal(index++));
        }
        getForm().getPartiInserimento().getNi_parte_numero_registro().setValue(String.valueOf(index));
        getForm().getPartiInserimento().getNi_parte_numero_registro().setViewMode();

        resetPartiInserimento();
        forwardToPublisher(getDefaultInserimentoWizardPublisher());
    }

    @Override
    public void updatePartiList() throws EMFError {
        // Recupero i dati della riga che devo modificare per andarli posi a settare
        // nelle caselle di testo di dettaglio
        DecParteNumeroRegistroRowBean row = (DecParteNumeroRegistroRowBean) getForm().getPartiList().getTable()
                .getCurrentRow();

        // Riempio la combo "Coincidenza con" solo con i valori ammessi in base al
        // valore di "Caratteri ammessi"
        // (ti_char_parte)
        String tiCharParte = row.getTiCharParte();
        getForm().getPartiInserimento().getTi_parte().setDecodeMap(new DecodeMap());
        if (StringUtils.isNotBlank(tiCharParte)) {
            getForm().getPartiInserimento().getTi_parte().setDecodeMap(ComboGetter.getCustomMappaTiParte(tiCharParte));
        }

        getForm().getPartiInserimento().copyFromBean(row);
        getForm().getPartiList().setStatus(Status.update);
    }

    private void setDecodeMaps() {
        getForm().getPartiInserimento().getTi_char_parte().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("ti_char_parte", KeyOrdUtility.TipiCalcolo.values()));
        getForm().getPartiInserimento().getTi_char_sep().setDecodeMap(ComboGetter.getMappaSeparatori("ti_char_sep"));
        getForm().getPartiInserimento().getTi_pad_sx_parte().setDecodeMap(ComboGetter.getMappaSortedGenericEnum(
                "ti_pad_sx_parte", ConfigRegAnno.TipiPadding.RIEMPI_0_A_SX_LESS12, ConfigRegAnno.TipiPadding.STANDARD));
        getForm().getPartiInserimento().getTi_parte().setDecodeMap(new DecodeMap());
    }

    private void increaseNiNumeroRegistro() {
        int sizeParti = getForm().getPartiList().getTable().size();
        BigDecimal lastPgParte = BigDecimal.ZERO;
        if (sizeParti > 0) {
            lastPgParte = getForm().getPartiList().getTable().getRow(sizeParti - 1)
                    .getBigDecimal(DecParteNumeroRegistroRowBean.TABLE_DESCRIPTOR.COL_NI_PARTE_NUMERO_REGISTRO);
        }
        String nextPg = String.valueOf(sizeParti + 1);

        if (lastPgParte.intValue() > sizeParti) {
            // Buco di numerazione
            DecParteNumeroRegistroTableBean tb = (DecParteNumeroRegistroTableBean) getForm().getPartiList().getTable();
            List<Object> progressivi = tb.toList(
                    DecParteNumeroRegistroRowBean.TABLE_DESCRIPTOR.COL_NI_PARTE_NUMERO_REGISTRO,
                    new SortingRule[] { SortingRule.getAscending(
                            DecParteNumeroRegistroRowBean.TABLE_DESCRIPTOR.COL_NI_PARTE_NUMERO_REGISTRO) });
            int index = 1;
            for (Object progressivo : progressivi) {
                int pg = ((BigDecimal) progressivo).intValue();
                if (index != pg) {
                    nextPg = String.valueOf(index);
                    break;
                }
                index++;
            }
        }
        getForm().getPartiInserimento().getNi_parte_numero_registro().setValue(nextPg);
        getForm().getPartiInserimento().getNi_parte_numero_registro().setViewMode();
    }

    @Override
    public void deleteTipoStrutDocAmmessoDaTipoDocList() {

        try {
            BigDecimal idTipoStrutDocAmmesso = ((DecTipoStrutDocAmmessoTableBean) getForm()
                    .getTipoStrutDocAmmessoDaTipoDocList().getTable()).getCurrentRow().getIdTipoStrutDocAmmesso();
            BigDecimal idTipoDoc = getForm().getTipoDoc().getId_tipo_doc().parse();
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            if (Application.Publisher.TIPO_DOC_DETAIL.equalsIgnoreCase(param.getNomePagina())) {
                StrutTipiForm form = (StrutTipiForm) SpagoliteLogUtil.getForm(this);
                param.setNomeAzione(
                        SpagoliteLogUtil.getDetailActionNameDelete(form, form.getTipoStrutDocAmmessoDaTipoDocList()));
            } else {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            }
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            tipoDocEjb.deleteDecTipoStrutDocAmmesso(param, idTipoStrutDocAmmesso, idTipoDoc);
            getMessageBox().addMessage(
                    new Message(MessageLevel.INF, "Tipo struttura documento ammesso eliminata con successo"));

            DecTipoStrutDocAmmessoTableBean tipoStrutDocAmmessoTableBean = tipoDocEjb
                    .getDecTipoStrutDocAmmessoTableBeanByIdTipoDoc(idTipoDoc);

            getForm().getTipoStrutDocAmmessoDaTipoDocList().setTable(tipoStrutDocAmmessoTableBean);
            getForm().getTipoStrutDocAmmessoDaTipoDocList().getTable().first();
            getForm().getTipoStrutDocAmmessoDaTipoDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        } catch (Exception e) {
            getMessageBox().addError(e.getMessage());
        } finally {
            if (Application.Publisher.ASSOCIAZIONE_TIPO_DOC_TIPO_STRUT_DOC.equals(getLastPublisher())) {
                goBack();
            } else {
                forwardToPublisher(Application.Publisher.TIPO_DOC_DETAIL);
            }

        }
    }

    private void redirectToTipoStrutDocPage() throws EMFError {
        StrutTipoStrutForm form = prepareRedirectToStrutTipoStrut();
        redirectToPage(Application.Actions.STRUT_TIPO_STRUT, form, form.getTipoStrutDocList().getName(),
                getForm().getTipoStrutDocAmmessoDaTipoDocList().getTable(), getNavigationEvent());
    }

    private void redirectToPage(final String action, BaseForm form, String listToPopulate, BaseTableInterface<?> table,
            String event) throws EMFError {
        BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
        OrgStrutRowBean struttura = struttureEjb.getOrgStrutRowBean(idStrut);
        ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) form.getComponent(listToPopulate)).setTable(table);
        redirectToAction(action, "?operation=listNavigationOnClick&navigationEvent=" + event + "&table="
                + listToPopulate + "&riga=" + table.getCurrentRowIndex() + "&cessato=" + struttura.getFlCessato(),
                form);
    }

    private StrutTipoStrutForm prepareRedirectToStrutTipoStrut() throws EMFError {
        StrutTipoStrutForm form = new StrutTipoStrutForm();

        BigDecimal idStrut = getForm().getIdList().getId_strut().parse();

        // salvo l'idStrut in modo da poterlo propagare più avanti se necessario
        form.getIdList().getId_strut().setValue(idStrut.toString());

        form.getStrutRif().getNm_strut().setValue(getForm().getStrutRif().getNm_strut().parse());
        form.getStrutRif().getDs_strut().setValue(getForm().getStrutRif().getDs_strut().parse());
        form.getStrutRif().getId_ente().setValue(getForm().getStrutRif().getId_ente().parse());
        form.getStrutRif().getStruttura().setValue(getForm().getStrutRif().getStruttura().parse());
        return form;
    }

    @Override
    public JSONObject triggerPartiInserimentoTi_pad_sx_parteOnTrigger() throws EMFError {
        getForm().getPartiInserimento().post(getRequest());
        String tipo = getForm().getPartiInserimento().getTi_pad_sx_parte().parse();
        if (StringUtils.isNotBlank(tipo)) {
            if (tipo.equals(ConfigRegAnno.TipiPadding.STANDARD.name())) {
                getForm().getPartiInserimento().getDesc_pad_sx_parte().setValue("Non viene eseguito il riempimento");
            } else if (tipo.equals(ConfigRegAnno.TipiPadding.RIEMPI_0_A_SX_LESS12.name())) {
                getForm().getPartiInserimento().getDesc_pad_sx_parte().setValue(
                        "Esegue riempimento di 0 a sinistra se la dimensione del numero \u00E8 inferiore a 12 caratteri");
            }
        } else {
            getForm().getPartiInserimento().getDesc_pad_sx_parte().setValue("");
        }
        return getForm().getPartiInserimento().asJSON();
    }

    @Override
    public JSONObject triggerPartiInserimentoTi_char_parteOnTrigger() throws EMFError {
        getForm().getPartiInserimento().post(getRequest());
        String tiCharParte = getForm().getPartiInserimento().getTi_char_parte().parse();
        getForm().getPartiInserimento().getTi_parte().setDecodeMap(new DecodeMap());
        if (StringUtils.isNotBlank(tiCharParte)) {
            getForm().getPartiInserimento().getTi_parte().setDecodeMap(ComboGetter.getCustomMappaTiParte(tiCharParte));
        }
        return getForm().getPartiInserimento().asJSON();
    }

    @Override
    public void filterInactiveRecordsRegoleSubStrutList() throws EMFError {
        int rowIndex = 0;
        int pageSize = WebConstants.DEFAULT_PAGE_SIZE;
        if (getForm().getRegoleSubStrutList().getTable() != null) {
            rowIndex = getForm().getRegoleSubStrutList().getTable().getCurrentRowIndex();
            pageSize = getForm().getRegoleSubStrutList().getTable().getPageSize();
        }

        if (getSession().getAttribute("provenienzaRegola").equals(Application.Publisher.TIPO_UNITA_DOC_DETAIL)) {
            DecTipoUnitaDocRowBean row = (DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable()
                    .getCurrentRow();
            getForm().getRegoleSubStrutList()
                    .setTable(subStrutEjb.getOrgRegolaValSubStrutTableBean(row.getIdTipoUnitaDoc(),
                            Constants.TipoDato.TIPO_UNITA_DOC,
                            getForm().getRegoleSubStrutList().isFilterValidRecords()));
        } else if (getSession().getAttribute("provenienzaRegola").equals(Application.Publisher.TIPO_DOC_DETAIL)) {
            DecTipoDocRowBean row = (DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow();
            getForm().getRegoleSubStrutList().setTable(subStrutEjb.getOrgRegolaValSubStrutTableBean(row.getIdTipoDoc(),
                    Constants.TipoDato.TIPO_DOC, getForm().getRegoleSubStrutList().isFilterValidRecords()));
        }

        getForm().getRegoleSubStrutList().getTable().setCurrentRowIndex(rowIndex);
        getForm().getRegoleSubStrutList().getTable().setPageSize(pageSize);
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void filterInactiveRecordsTipoStrutUnitaDocList() throws EMFError {
        int rowIndex = 0;
        int pageSize = WebConstants.DEFAULT_PAGE_SIZE;
        if (getForm().getTipoStrutUnitaDocList().getTable() != null) {
            rowIndex = getForm().getTipoStrutUnitaDocList().getTable().getCurrentRowIndex();
            pageSize = getForm().getTipoStrutUnitaDocList().getTable().getPageSize();
        }

        final BigDecimal idTipoUnitaDoc = ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable()
                .getCurrentRow()).getIdTipoUnitaDoc();
        DecTipoStrutUnitaDocTableBean tipoStrutUnitaDocTableBean = tipoStrutDocEjb.getDecTipoStrutUnitaDocTableBean(
                idTipoUnitaDoc, getForm().getTipoStrutUnitaDocList().isFilterValidRecords());
        getForm().getTipoStrutUnitaDocList().setTable(tipoStrutUnitaDocTableBean);

        getForm().getTipoStrutUnitaDocList().getTable().setCurrentRowIndex(rowIndex);
        getForm().getTipoStrutUnitaDocList().getTable().setPageSize(pageSize);
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public JSONObject triggerRegistroUnitaDocConserv_unlimitedOnTrigger() throws EMFError {
        return getConservUnlimitedTrigger(getForm().getRegistroUnitaDoc());
    }

    private JSONObject getConservUnlimitedTrigger(Fields<Field> fields) throws EMFError {
        fields.post(getRequest());
        ComboBox<String> conservUnlimited = ((ComboBox<String>) fields
                .getComponent(getForm().getRegistroUnitaDoc().getConserv_unlimited().getName()));
        Input<BigDecimal> niAnniConserv = ((Input<BigDecimal>) fields
                .getComponent(getForm().getRegistroUnitaDoc().getNi_anni_conserv().getName()));
        String unlimited = conservUnlimited.parse();
        BigDecimal anniConserv = niAnniConserv.parse();
        if (unlimited != null && unlimited.equals("1")) {
            niAnniConserv.setValue("9999");
        } else if (anniConserv != null && anniConserv.equals(new BigDecimal(9999))) {
            niAnniConserv.setValue(null);
        }
        return fields.asJSON();
    }

    public void duplicaRegistroOperation() throws EMFError {
        // Metto in edit mode i campi che mi interessano e carico il dettaglio
        // con i dati del registro da duplicare
        getForm().getRegistroUnitaDoc().setEditMode();
        getForm().getRegistroUnitaDoc().setStatus(Status.insert);
        getForm().getRegistroUnitaDocList().setStatus(Status.insert);

        getForm().getRegistroUnitaDoc().getFl_registro_fisc().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getRegistroUnitaDoc().getConserv_unlimited().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        // Precompilo i campi del registro
        BigDecimal idRegistroUnitaDoc = ((DecRegistroUnitaDocRowBean) getForm().getRegistroUnitaDocList().getTable()
                .getCurrentRow()).getIdRegistroUnitaDoc();
        DecRegistroUnitaDocRowBean registroUnitaDocRowBean = registroEjb
                .getDecRegistroUnitaDocRowBean(idRegistroUnitaDoc, null);
        getForm().getRegistroUnitaDoc().copyFromBean(registroUnitaDocRowBean);
        getForm().getRegistroUnitaDoc().getCd_registro_unita_doc().clear();
        if (registroUnitaDocRowBean.getNiAnniConserv() != null) {
            if (registroUnitaDocRowBean.getNiAnniConserv().equals(new BigDecimal(9999))) {
                getForm().getRegistroUnitaDoc().getConserv_unlimited().setValue("1");
            } else {
                getForm().getRegistroUnitaDoc().getConserv_unlimited().setValue("0");
            }
        }

        // Valorizzo le date
        Calendar oggi = Calendar.getInstance();
        DateFormat formattatore = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);
        getForm().getRegistroUnitaDoc().getDt_istituz().setValue(formattatore.format(oggi.getTime()));
        getForm().getRegistroUnitaDoc().getDt_soppres().setValue("31/12/2444");

        // Controllo se spuntare o meno il flag di creazione automatica criteria a
        // seconda o meno che ce ne siano di
        // standard
        getForm().getRegistroCreazioneCriterio().getCriterio_autom_registro().setChecked(
                critRaggrEjb.existsCriterioStandardPerTipoDato(idRegistroUnitaDoc.longValue(), TipoDato.REGISTRO));
        // Mi setto un attributo in request per far capire che arrivo dalla "duplica
        // registro"
        getSession().setAttribute("fromDuplicaRegistro", true);
        forwardToPublisher(Application.Publisher.REGISTRO_UNITA_DOC_DETAIL);
    }

    private void salvaDuplicaRegistroUnitaDoc() throws EMFError {
        getForm().getRegistroUnitaDoc().post(getRequest());
        getForm().getRegistroCreazioneCriterio().post(getRequest());
        BigDecimal idStrut = (BigDecimal) getSession().getAttribute("id_struttura_lavorato");
        if (idStrut == null) {
            idStrut = getForm().getIdList().getId_strut().parse();
        }
        // Ricavo i dati del dettaglio registro
        DecRegistroUnitaDocRowBean registroRowBean = new DecRegistroUnitaDocRowBean();
        getForm().getRegistroUnitaDoc().copyToBean(registroRowBean);
        registroRowBean.setIdStrut(idStrut);
        String criterioAutomRegistro = getForm().getRegistroCreazioneCriterio().getCriterio_autom_registro().parse();

        // Validazione formale dei dati
        if (getForm().getRegistroUnitaDoc().validate(getMessageBox())) {
            try {
                /*
                 * Codice aggiuntivo per il logging... Stessa operazione dell'Inserimento di un registro anche se si
                 * tratta di una duplicazione
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null,
                                null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                        SpagoliteLogUtil.getToolbarSave(false));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                registroEjb.duplicaRegistroUnitaDoc(param, registroRowBean, criterioAutomRegistro);
                DecRegistroUnitaDocTableBean registroTableBean = new DecRegistroUnitaDocTableBean();
                registroTableBean.add(registroRowBean);
                getForm().getRegistroUnitaDocList().setTable(registroTableBean);
                getForm().getRegistroUnitaDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getRegistroUnitaDocList().getTable().setCurrentRowIndex(0);
                // Setto il nuovo id anche nella lista id e carico le liste
                getForm().getIdList().getId_registro_unita_doc()
                        .setValue(registroRowBean.getIdRegistroUnitaDoc().toPlainString());
                reloadRegistroLists();
                getForm().getRegistroUnitaDoc().setViewMode();
                getForm().getRegistroUnitaDoc().setStatus(Status.view);
                getForm().getRegistroUnitaDocList().setStatus(Status.view);
                getMessageBox().addMessage(new Message(MessageLevel.INF, "Nuovo registro duplicato con successo!"));
                getMessageBox().setViewMode(ViewMode.plain);
                getSession().removeAttribute("fromDuplicaRegistro");
                getForm().getTipoUnitaDoc().getLogEventiTipoUD().setEditMode();
                getForm().getRegistroUnitaDoc().getLogEventi().setEditMode();

            } catch (Exception e) {
                getMessageBox().addError("Errore nella duplicazione del registro");
            }
        }
        forwardToPublisher(Application.Publisher.REGISTRO_UNITA_DOC_DETAIL);
    }

    @Override
    public void duplicaRegistroButton() throws EMFError {
        duplicaRegistroOperation();
    }

    @Override
    public void creaTipoSerieStandard() throws EMFError {
        BigDecimal idRegistro = getForm().getTipoUnitaDocAmmesso().getId_registro_unita_doc().parse();
        BigDecimal idTipoUd = getForm().getTipoUnitaDocAmmesso().getId_tipo_unita_doc().parse();
        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setNomeAzione(SpagoliteLogUtil.getButtonActionName(getForm(), getForm().getTipoUnitaDocAmmesso(),
                    getForm().getTipoUnitaDocAmmesso().getCreaTipoSerieStandard().getName()));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            int tipiSerieCreati = tipoSerieEjb.createTipoSerieStandardDaRegistroOTipoUd(param, idRegistro, idTipoUd);
            if (tipiSerieCreati > 0) {
                StringBuilder messaggio = new StringBuilder(
                        "Creazione tipi di serie standard completata con successo : ");
                if (tipiSerieCreati == 1) {
                    messaggio.append("creato 1 tipo di serie standard");
                } else {
                    messaggio.append("creati ").append(tipiSerieCreati).append(" tipi di serie standard");
                }
                getMessageBox().addInfo(messaggio.toString());

            } else {
                getMessageBox().addWarning("Il tipo di serie non può essere creato perchè già presente nel sistema");
            }
            getMessageBox().setViewMode(ViewMode.plain);
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public JSONObject triggerTipoDocAmmessoTi_docOnTrigger() throws EMFError {
        getForm().getTipoDocAmmesso().post(getRequest());
        String tiDoc = getForm().getTipoDocAmmesso().getTi_doc().parse();
        if (StringUtils.isNotBlank(tiDoc)) {
            getForm().getTipoDocAmmesso().getFl_obbl()
                    .setChecked(tiDoc.equalsIgnoreCase(CostantiDB.TipoDocumento.PRINCIPALE));
        }
        DecTipoStrutUnitaDocRowBean tipoStrutUnitaDocRowBean = ((DecTipoStrutUnitaDocRowBean) getForm()
                .getTipoStrutUnitaDocList().getTable().getCurrentRow());
        getForm().getTipoDocAmmesso().getNm_tipo_strut_unita_doc()
                .setValue(tipoStrutUnitaDocRowBean.getNmTipoStrutUnitaDoc());
        return getForm().getTipoDocAmmesso().asJSON();
    }

    @Override
    public void logEventi() throws EMFError {
        logEventiCommon(SacerLogConstants.TIPO_OGGETTO_REGISTRO,
                getForm().getRegistroUnitaDoc().getId_registro_unita_doc().getValue());
    }

    @Override
    public void logEventiTipoUD() throws EMFError {
        DecTipoUnitaDocRowBean riga = (DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable()
                .getCurrentRow();
        logEventiCommon(SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA, riga.getIdTipoUnitaDoc().toString());
    }

    @Override
    public void logEventiTipoDoc() throws EMFError {
        DecTipoDocRowBean riga = (DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow();
        logEventiCommon(SacerLogConstants.TIPO_OGGETTO_TIPO_DOCUMENTO, riga.getIdTipoDoc().toString());
    }

    private void logEventiCommon(String tipoOggetto, String idOggetto) throws EMFError {
        GestioneLogEventiForm form = new GestioneLogEventiForm();
        form.getOggettoDetail().getNmApp().setValue(configurationHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC));
        form.getOggettoDetail().getNm_tipo_oggetto().setValue(tipoOggetto);
        form.getOggettoDetail().getIdOggetto().setValue(idOggetto);
        redirectToAction(it.eng.parer.sacerlog.slite.gen.Application.Actions.GESTIONE_LOG_EVENTI,
                "?operation=inizializzaLogEventi", form);
    }

    @Override
    public void confermaSalvataggioRegistroUnitaDoc() throws EMFError {
        BigDecimal idRegistroUnitaDoc = getForm().getRegistroUnitaDoc().getId_registro_unita_doc().parse();
        String criterioAutomRegistro = getForm().getRegistroCreazioneCriterio().getCriterio_autom_registro().parse();
        BigDecimal idStrut = (BigDecimal) getSession().getAttribute("id_struttura_lavorato");
        if (idStrut == null) {
            idStrut = getForm().getIdList().getId_strut().parse();
        }

        DecRegistroUnitaDocRowBean registroUnitaDocRowBean = new DecRegistroUnitaDocRowBean();
        getForm().getRegistroUnitaDoc().copyToBean(registroUnitaDocRowBean);
        registroUnitaDocRowBean.setIdStrut(idStrut);
        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());

            String azioneInsCriterio = SpagoliteLogUtil.getButtonActionName(getForm(), getForm().getRegistroUnitaDoc(),
                    getForm().getRegistroUnitaDoc().getInsCriterioRaggrByTipoUnitaDocFromReg().getName());
            String azioneModCriterio = SpagoliteLogUtil.getButtonActionName(getForm(), getForm().getRegistroUnitaDoc(),
                    getForm().getRegistroUnitaDoc().getUpdCriterioRaggrByTipoUnitaDocFromReg().getName());
            registroEjb.updateDecRegistroUnitaDoc(param, azioneInsCriterio, azioneModCriterio, idRegistroUnitaDoc,
                    registroUnitaDocRowBean, criterioAutomRegistro);
            getMessageBox().addMessage(new Message(MessageLevel.INF, "Registro modificato con successo"));
            getMessageBox().setViewMode(ViewMode.plain);
            // Ricarico tutto il registro e i suoi figli
            registroUnitaDocRowBean = registroEjb.getDecRegistroUnitaDocRowBean(
                    registroUnitaDocRowBean.getCdRegistroUnitaDoc(), registroUnitaDocRowBean.getIdStrut());
            getForm().getIdList().getId_registro_unita_doc()
                    .setValue(registroUnitaDocRowBean.getIdRegistroUnitaDoc().toPlainString());
            reloadRegistroLists();
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
        } catch (ParerWarningException ex) {
            getMessageBox().setViewMode(ViewMode.alert);
            // Ricarico tutto il registro e i suoi figli
            registroUnitaDocRowBean = registroEjb.getDecRegistroUnitaDocRowBean(
                    registroUnitaDocRowBean.getCdRegistroUnitaDoc(), registroUnitaDocRowBean.getIdStrut());
            getForm().getIdList().getId_registro_unita_doc()
                    .setValue(registroUnitaDocRowBean.getIdRegistroUnitaDoc().toPlainString());
            reloadRegistroLists();
            getMessageBox().addWarning(ex.getDescription());
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void creaCriterioRaggrStandardRegistroButton() throws EMFError {
        BigDecimal idRegistroUnitaDoc = getForm().getRegistroUnitaDoc().getId_registro_unita_doc().parse();
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                        CostantiDB.TipoAplVGetValAppart.APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                SpagoliteLogUtil.getButtonActionName(getForm(), getForm().getRegistroCreazioneCriterio(), getForm()
                        .getRegistroCreazioneCriterio().getCreaCriterioRaggrStandardRegistroButton().getName()));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        try {
            critRaggrEjb.creaCriteriRaggruppamentoRegistroDaBottone(param, idRegistroUnitaDoc);
            // Una volta effettuata la creazione, nascondo il bottone
            getForm().getRegistroCreazioneCriterio().getCreaCriterioRaggrStandardRegistroButton().setHidden(true);
            getMessageBox().addInfo("Criterio di raggruppamento creato con successo!");
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
        }
        forwardToPublisher(Application.Publisher.REGISTRO_UNITA_DOC_DETAIL);
    }

    @Override
    public void creaCriterioRaggrStandardTipoUdButton() throws EMFError {
        BigDecimal idTipoUnitaDoc = getForm().getTipoUnitaDoc().getId_tipo_unita_doc().parse();
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                        CostantiDB.TipoAplVGetValAppart.APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                SpagoliteLogUtil.getButtonActionName(getForm(), getForm().getTipoUnitaDocCreazioneCriterio(), getForm()
                        .getTipoUnitaDocCreazioneCriterio().getCreaCriterioRaggrStandardTipoUdButton().getName()));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        try {
            critRaggrEjb.creaCriteriRaggruppamentoTipoUdDaBottone(param, idTipoUnitaDoc);
            // Una volta effettuata la creazione, nascondo il bottone
            getForm().getTipoUnitaDocCreazioneCriterio().getCreaCriterioRaggrStandardTipoUdButton().setHidden(true);
            getMessageBox().addInfo("Criterio di raggruppamento creato con successo!");
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
        }
        forwardToPublisher(Application.Publisher.TIPO_UNITA_DOC_DETAIL);
    }

    @Override
    public void creaCriterioRaggrStandardTipoDocButton() throws EMFError {
        BigDecimal idTipoDoc = getForm().getTipoDoc().getId_tipo_doc().parse();
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                        CostantiDB.TipoAplVGetValAppart.APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                SpagoliteLogUtil.getButtonActionName(getForm(), getForm().getTipoDocCreazioneCriterio(),
                        getForm().getTipoDocCreazioneCriterio().getCreaCriterioRaggrStandardTipoDocButton().getName()));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        try {
            critRaggrEjb.creaCriteriRaggruppamentoTipoDocDaBottone(param, idTipoDoc);
            // Una volta effettuata la creazione, nascondo il bottone
            getForm().getTipoDocCreazioneCriterio().getCreaCriterioRaggrStandardTipoDocButton().setHidden(true);
            getMessageBox().addInfo("Criterio di raggruppamento creato con successo!");
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
        }
        forwardToPublisher(Application.Publisher.TIPO_DOC_DETAIL);
    }

    @Override
    public JSONObject triggerTipoDocFl_tipo_doc_principaleOnTrigger() throws EMFError {
        JSONObject returnObject = getForm().asJSON();
        try {
            getForm().getTipoDoc().post(getRequest());
            String flTipoDocPrincipale = getForm().getTipoDoc().getFl_tipo_doc_principale().parse();
            DecTipoDocRowBean tipoDocCurrent = (DecTipoDocRowBean) getForm().getTipoDocList().getTable()
                    .getCurrentRow();

            // Se non è presente un criterio di raggruppamento per il tipo documento
            // corrente, visualizzo il bottone per
            // la creazione del criterio
            if (flTipoDocPrincipale != null && flTipoDocPrincipale.equals("1") && !critRaggrEjb
                    .existsCriterioStandardPerTipoDato(tipoDocCurrent.getIdTipoDoc().longValue(), TipoDato.TIPO_DOC)) {
                getForm().getTipoDocCreazioneCriterio().getCreaCriterioRaggrStandardTipoDocButton().setHidden(false);
            } else {
                getForm().getTipoDocCreazioneCriterio().getCreaCriterioRaggrStandardTipoDocButton().setHidden(true);
                getForm().getTipoDoc().getDs_periodicita_vers().clear();
            }

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(getForm().getTipoDocCreazioneCriterio().asJSON());
            jsonArray.put(getForm().getTipoDoc().asJSON());

            returnObject.put("map", jsonArray);

        } catch (JSONException ex) {
            getMessageBox().addError(
                    "Errore imprevisto durante la visualizzazione del bottone di creazione criterio da tipo documento");
        }
        return returnObject;
    }

    @Override
    public void insCriterioRaggrByTipoUnitaDocFromReg() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
        // Tools | Templates.
    }

    @Override
    public void updCriterioRaggrByTipoUnitaDocFromReg() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
        // Tools | Templates.
    }

    @Override
    public void insCriterioRaggrByTipoUnitaDocFromAssociazione() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
        // Tools | Templates.
    }

    @Override
    public void updCriterioRaggrByTipoUnitaDocFromAssociazione() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
        // Tools | Templates.
    }

    @Override
    public void updCriterioRaggrByTipoUnitaDocFromAssociazioneDelete() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
        // Tools | Templates.
    }

    @Override
    public JSONObject triggerRegistroUnitaDocCd_registro_unita_docOnTrigger() throws EMFError {
        getForm().getRegistroUnitaDoc().post(getRequest());
        String cdRegistroUnitaDoc = getForm().getRegistroUnitaDoc().getCd_registro_unita_doc().parse();
        if (cdRegistroUnitaDoc != null) {
            String cdRegistroNormaliz = Utils.getNormalizedUDCode(cdRegistroUnitaDoc);
            getForm().getRegistroUnitaDoc().getCd_registro_normaliz().setValue(cdRegistroNormaliz);
        }
        return getForm().getRegistroUnitaDoc().asJSON();
    }

    /**
     * Elimina un parametro di amministrazione dalla lista
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteParametriAmministrazioneTipoUdList() throws EMFError {
        AplParamApplicRowBean row = (AplParamApplicRowBean) getForm().getParametriAmministrazioneTipoUdList().getTable()
                .getCurrentRow();
        // int deletedRowIndex =
        // getForm().getParametriAmministrazioneTipoUdList().getTable().getCurrentRowIndex();
        // getForm().getParametriAmministrazioneTipoUdList().getTable().remove(deletedRowIndex);
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(configurationHelper.getParamApplicApplicationName(),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                getForm().getParametriAmministrazioneTipoUdList()));
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroTipoUd(param, idValoreParamApplic)) {
                getMessageBox().addInfo("Parametro di amministrazione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox()
                    .addWarning("Valore sul tipo unità documentaria non presente: nessuna cancellazione effettuata");
        }
        try {
            loadTipoUnitaDoc(((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                    .getIdTipoUnitaDoc());
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        forwardToPublisher(Application.Publisher.TIPO_UNITA_DOC_DETAIL);
    }

    /**
     * Elimina un parametro di conservazione dalla lista
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteParametriConservazioneTipoUdList() throws EMFError {
        AplParamApplicRowBean row = (AplParamApplicRowBean) getForm().getParametriConservazioneTipoUdList().getTable()
                .getCurrentRow();
        // int deletedRowIndex =
        // getForm().getParametriConservazioneTipoUdList().getTable().getCurrentRowIndex();
        // getForm().getParametriConservazioneTipoUdList().getTable().remove(deletedRowIndex);
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(configurationHelper.getParamApplicApplicationName(),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        param.setNomeAzione(
                SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getParametriConservazioneTipoUdList()));
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroTipoUd(param, idValoreParamApplic)) {
                getMessageBox().addInfo("Parametro di conservazione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox()
                    .addWarning("Valore sul tipo unità docuementaria non presente: nessuna cancellazione effettuata");
        }
        try {
            loadTipoUnitaDoc(((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                    .getIdTipoUnitaDoc());
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        forwardToPublisher(Application.Publisher.TIPO_UNITA_DOC_DETAIL);
    }

    /**
     * Elimina un parametro di gestione dalla lista
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteParametriGestioneTipoUdList() throws EMFError {
        AplParamApplicRowBean row = (AplParamApplicRowBean) getForm().getParametriGestioneTipoUdList().getTable()
                .getCurrentRow();
        // int deletedRowIndex =
        // getForm().getParametriGestioneTipoUdList().getTable().getCurrentRowIndex();
        // getForm().getParametriGestioneTipoUdList().getTable().remove(deletedRowIndex);
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(configurationHelper.getParamApplicApplicationName(),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        param.setNomeAzione(
                SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getParametriGestioneTipoUdList()));
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroTipoUd(param, idValoreParamApplic)) {
                getMessageBox().addInfo("Parametro di gestione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox()
                    .addWarning("Valore sul tipo unità documentaria non presente: nessuna cancellazione effettuata");
        }
        try {
            loadTipoUnitaDoc(((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                    .getIdTipoUnitaDoc());
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        forwardToPublisher(Application.Publisher.TIPO_UNITA_DOC_DETAIL);
    }

    @Override
    public void parametriAmministrazioneTipoUdButton() throws Throwable {
        BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
        BigDecimal idTipoUnitaDoc = ((BaseRowInterface) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                .getBigDecimal("id_tipo_unita_doc");
        loadListeParametriTipoUd(idStrut, idTipoUnitaDoc, null, false, true, true, true);
        getForm().getTipoUnitaDoc().setStatus(Status.update);
        getForm().getRicercaParametriTipoUd().setEditMode();
        BaseTable tb = struttureEjb.getFunzioneParametriTableBean();
        getForm().getRicercaParametriTipoUd().getFunzione().reset();
        getForm().getRicercaParametriTipoUd().getFunzione()
                .setDecodeMap(DecodeMap.Factory.newInstance(tb, "funzione", "funzione"));
        getSession().setAttribute("provenienzaParametri", "amministrazione");
        forwardToPublisher(Application.Publisher.PARAMETRI_TIPO_UD);
    }

    @Override
    public void parametriConservazioneTipoUdButton() throws Throwable {
        BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
        BigDecimal idTipoUnitaDoc = ((BaseRowInterface) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                .getBigDecimal("id_tipo_unita_doc");
        loadListeParametriTipoUd(idStrut, idTipoUnitaDoc, null, false, false, true, true);
        getForm().getTipoUnitaDoc().setStatus(Status.update);
        getForm().getRicercaParametriTipoUd().setEditMode();
        BaseTable tb = struttureEjb.getFunzioneParametriTableBean();
        getForm().getRicercaParametriTipoUd().getFunzione().reset();
        getForm().getRicercaParametriTipoUd().getFunzione()
                .setDecodeMap(DecodeMap.Factory.newInstance(tb, "funzione", "funzione"));
        getSession().setAttribute("provenienzaParametri", "conservazione");
        forwardToPublisher(Application.Publisher.PARAMETRI_TIPO_UD);
    }

    @Override
    public void parametriGestioneTipoUdButton() throws Throwable {
        BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
        BigDecimal idTipoUnitaDoc = ((BaseRowInterface) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                .getBigDecimal("id_tipo_unita_doc");
        loadListeParametriTipoUd(idStrut, idTipoUnitaDoc, null, false, false, false, true);
        getForm().getTipoUnitaDoc().setStatus(Status.update);
        getForm().getRicercaParametriTipoUd().setEditMode();
        BaseTable tb = struttureEjb.getFunzioneParametriTableBean();
        getForm().getRicercaParametriTipoUd().getFunzione().reset();
        getForm().getRicercaParametriTipoUd().getFunzione()
                .setDecodeMap(DecodeMap.Factory.newInstance(tb, "funzione", "funzione"));
        getSession().setAttribute("provenienzaParametri", "gestione");
        forwardToPublisher(Application.Publisher.PARAMETRI_TIPO_UD);
    }

    private void salvaParametriTipoUd() throws EMFError {
        getForm().getParametriAmministrazioneTipoUdList().post(getRequest());
        getForm().getParametriConservazioneTipoUdList().post(getRequest());
        getForm().getParametriGestioneTipoUdList().post(getRequest());

        BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
        BigDecimal idTipoUnitaDoc = ((BaseRowInterface) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                .getBigDecimal("id_tipo_unita_doc");

        // Controllo valori possibili su struttura
        AplParamApplicTableBean parametriAmministrazione = (AplParamApplicTableBean) getForm()
                .getParametriAmministrazioneTipoUdList().getTable();
        AplParamApplicTableBean parametriConservazione = (AplParamApplicTableBean) getForm()
                .getParametriConservazioneTipoUdList().getTable();
        AplParamApplicTableBean parametriGestione = (AplParamApplicTableBean) getForm().getParametriGestioneTipoUdList()
                .getTable();
        String error = amministrazioneEjb.checkParametriAmmessi("tipo_ud", parametriAmministrazione,
                parametriConservazione, parametriGestione);
        if (error != null) {
            getMessageBox().addError(error);
        }

        if (!getMessageBox().hasError()) {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                    SpagoliteLogUtil.getToolbarSave(getForm().getTipoUnitaDoc().getStatus().equals(Status.update)));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            tipoUnitaDocEjb.saveParametriTipoUd(param, parametriAmministrazione, parametriConservazione,
                    parametriGestione, idTipoUnitaDoc);
            getMessageBox().addInfo("Parametri tipo unità documentaria salvati con successo");
            getMessageBox().setViewMode(ViewMode.plain);
            ricercaParametriTipoUdButton();
            getForm().getTipoUnitaDoc().setStatus(Status.view);
            setViewModeListeParametri();
            try {
                loadListeParametriTipoUd(idStrut, idTipoUnitaDoc, null, false, false, false, false);
            } catch (ParerUserError ex) {
                getMessageBox().addError(
                        "Errore durante il ricaricamento dei parametri tipo ud a seguito del salvataggio degli stessi");
            }
        }
        forwardToPublisher(getLastPublisher());
    }

    private void setViewModeListeParametri() {
        getForm().getTipoUnitaDoc().setStatus(Status.view);
        getForm().getParametriAmministrazioneTipoUdList().setStatus(Status.view);
        getForm().getParametriConservazioneTipoUdList().setStatus(Status.view);
        getForm().getParametriGestioneTipoUdList().setStatus(Status.view);
        getForm().getParametriAmministrazioneTipoUdList().getDs_valore_param_applic_tipo_ud_amm().setViewMode();
        getForm().getParametriConservazioneTipoUdList().getDs_valore_param_applic_tipo_ud_cons().setViewMode();
        getForm().getParametriGestioneTipoUdList().getDs_valore_param_applic_tipo_ud_gest().setViewMode();
    }

    @Override
    public void ricercaParametriTipoUdButton() throws EMFError {
        getForm().getRicercaParametriTipoUd().post(getRequest());
        List<String> funzione = getForm().getRicercaParametriTipoUd().getFunzione().parse();
        BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
        BigDecimal idTipoUnitaDoc = ((BaseRowInterface) getForm().getTipoUnitaDocList().getTable().getCurrentRow())
                .getBigDecimal("id_tipo_unita_doc");
        try {
            if (getSession().getAttribute("provenienzaParametri") != null) {
                String provenzienzaParametri = (String) getSession().getAttribute("provenienzaParametri");
                if (provenzienzaParametri.equals("amministrazione")) {
                    loadListeParametriTipoUd(idStrut, idTipoUnitaDoc, funzione, false, true, true, true);
                } else if (provenzienzaParametri.equals("conservazione")) {
                    loadListeParametriTipoUd(idStrut, idTipoUnitaDoc, funzione, false, false, true, true);
                } else if (provenzienzaParametri.equals("gestione")) {
                    loadListeParametriTipoUd(idStrut, idTipoUnitaDoc, funzione, false, false, false, true);
                }
            }
            setViewModeListeParametri();
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il caricamento dei parametri tipo unità documentaria");
        }
        forwardToPublisher(Application.Publisher.PARAMETRI_TIPO_UD);
    }

    @Override
    public void scaricaXsdModelliUdButton() throws EMFError {
        String nomeTipo = null;

        if (getForm().getIdList().getId_tipo_doc().parse() != null) {
            nomeTipo = tipoDocEjb.getDecTipoDocRowBean(getForm().getIdList().getId_tipo_doc().parse(), null)
                    .getNmTipoDoc();
        } else if (getForm().getIdList().getId_tipo_unita_doc().parse() != null) {
            nomeTipo = tipoUnitaDocEjb
                    .getDecTipoUnitaDocRowBean(getForm().getIdList().getId_tipo_unita_doc().parse(), null)
                    .getNmTipoUnitaDoc();
        }

        if (getForm().getIdList().getNm_sys_migraz().parse() != null) {
            String sysMigraz = getForm().getIdList().getNm_sys_migraz().parse();
            nomeTipo = "Migr_" + sysMigraz + "_" + getForm().getIdList().getNm_sacer_type().parse();
        }

        // definiamo l'output previsto che sarà un file in formato zip
        // di cui si occuperà la servlet per fare il download
        try (ZipOutputStream out = new ZipOutputStream(getServletOutputStream())) {
            String tipoXsd = getForm().getXsdModelliUdDetail().getTi_modello_xsd().parse();
            String blXsd = getForm().getXsdModelliUdDetail().getBl_xsd().parse();
            String codiceVersione = getForm().getXsdModelliUdDetail().getCd_xsd().parse();

            String filename = nomeTipo + "_" + tipoXsd + "_xsd_" + codiceVersione;

            getResponse().setContentType(WebConstants.MIME_TYPE_ZIP);
            getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".zip");

            zipXsdTipoDoc(out, blXsd, filename);
            out.flush();
            freeze();
        } catch (Exception e) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR, "Errore nel recupero dei file da zippare"));
            logger.error(e.getMessage(), e);
        }

    }

    private void zipXsdTipoDoc(ZipOutputStream out, String blXsd, String filename) throws IOException {

        // definiamo il buffer per lo stream di bytes
        byte[] data = new byte[1000];
        if (StringUtils.isNotBlank(blXsd)) {
            byte[] blob = blXsd.getBytes();
            if (blob != null) {
                try (InputStream is = new ByteArrayInputStream(blob)) {
                    int count;
                    out.putNextEntry(new ZipEntry(filename + ".xsd"));
                    while ((count = is.read(data, 0, 1000)) != -1) {
                        out.write(data, 0, count);
                    }
                    out.closeEntry();
                }
            }
        }
    }

    @Override
    public void updateXsdModelliUdList() throws EMFError {
        getForm().getXsdModelliUdDetail().setViewMode();

        getForm().getXsdModelliUdDetail().getScaricaXsdModelliUdButton().setViewMode();
        getForm().getXsdModelliUdDetail().getDt_uso_soppres().setEditMode();
        getForm().getXsdModelliUdDetail().getFl_standard().setEditMode();

        getForm().getXsdModelliUdDetail().setStatus(Status.update);
        getForm().getXsdModelliUdList().setStatus(Status.update);
    }

    @Override
    public JSONObject triggerXsdModelliUdDetailTi_modello_xsdOnTrigger() throws EMFError {
        getForm().getXsdModelliUdDetail().getTi_modello_xsd().post(getRequest());
        String tiModelloXsd = getForm().getXsdModelliUdDetail().getTi_modello_xsd().parse();
        //
        if (StringUtils.isNotBlank(tiModelloXsd)) {
            String tiUsoModello = getForm().getIdList().getNm_sys_migraz().parse() != null
                    ? TiUsoModelloXsd.MIGRAZ.name() : TiUsoModelloXsd.VERS.name();
            DecodeMap mpCdXsd = modelliXsdUdEjb.getCdXsdInUsoByIdAmbTiModUso(
                    getForm().getXsdModelliUdDetail().getId_ambiente().parse(), tiModelloXsd, tiUsoModello, "cd_xsd");
            getForm().getXsdModelliUdDetail().getCd_xsd().setDecodeMap(mpCdXsd);
            // pre-select element (default)
            Optional<DecModelloXsdUdRowBean> modelloXsdStandard = modelliXsdUdEjb.getDefaultDecModelloXsdUd(
                    getForm().getXsdModelliUdDetail().getId_ambiente().parse(), tiModelloXsd, tiUsoModello);
            if (modelloXsdStandard.isPresent()) {
                // set values
                getForm().getXsdModelliUdDetail().getCd_xsd().setValue(modelloXsdStandard.get().getCdXsd());
                getForm().getXsdModelliUdDetail().getDs_xsd().setValue(modelloXsdStandard.get().getDsXsd());
                getForm().getXsdModelliUdDetail().getDt_soppres()
                        .setValue(new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE)
                                .format(modelloXsdStandard.get().getDtSoppres().getTime()));
                getForm().getXsdModelliUdDetail().getBl_xsd().setValue(modelloXsdStandard.get().getBlXsd());
                // enable button scarica
                getForm().getXsdModelliUdDetail().getScaricaXsdModelliUdButton().setHidden(false);
                getForm().getXsdModelliUdDetail().getScaricaXsdModelliUdButton().setViewMode();
            }
        } else {
            getForm().getXsdModelliUdDetail().getCd_xsd().setDecodeMap(new DecodeMap());
            getForm().getXsdModelliUdDetail().getDs_xsd().clear();
            getForm().getXsdModelliUdDetail().getDt_soppres().clear();
            getForm().getXsdModelliUdDetail().getBl_xsd().clear();
            // disable button scarica
            getForm().getXsdModelliUdDetail().getScaricaXsdModelliUdButton().setHidden(true);
        }
        return getForm().getXsdModelliUdDetail().asJSON();
    }

    @Override
    public JSONObject triggerXsdModelliUdDetailCd_xsdOnTrigger() throws EMFError {
        getForm().getXsdModelliUdDetail().getCd_xsd().post(getRequest());
        String cdXsd = getForm().getXsdModelliUdDetail().getCd_xsd().parse();
        //
        if (StringUtils.isNotBlank(cdXsd)) {
            // modello generico
            DecModelloXsdUdRowBean modelloXsdUd = new DecModelloXsdUdRowBean();
            getForm().getXsdModelliUdDetail().copyToBean(modelloXsdUd);
            modelloXsdUd.setCdXsd(cdXsd);
            modelloXsdUd = modelliXsdUdEjb.getDecModelloXsdUd(modelloXsdUd.getIdAmbiente(),
                    modelloXsdUd.getTiModelloXsd(), modelloXsdUd.getTiUsoModelloXsd(), modelloXsdUd.getCdXsd(), false);
            // set values
            getForm().getXsdModelliUdDetail().getDs_xsd().setValue(modelloXsdUd.getDsXsd());
            getForm().getXsdModelliUdDetail().getDt_soppres()
                    .setValue(new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE)
                            .format(modelloXsdUd.getDtSoppres().getTime()));
            getForm().getXsdModelliUdDetail().getBl_xsd().setValue(modelloXsdUd.getBlXsd());
            // enable button scarica
            getForm().getXsdModelliUdDetail().getScaricaXsdModelliUdButton().setHidden(false);
            getForm().getXsdModelliUdDetail().getScaricaXsdModelliUdButton().setViewMode();
        } else {
            getForm().getXsdModelliUdDetail().getDs_xsd().clear();
            getForm().getXsdModelliUdDetail().getDt_soppres().clear();
            getForm().getXsdModelliUdDetail().getBl_xsd().clear();
            // disable button scarica
            getForm().getXsdModelliUdDetail().getScaricaXsdModelliUdButton().setHidden(true);
        }
        getForm().getXsdModelliUdDetail().getBl_xsd().setViewMode();
        return getForm().getXsdModelliUdDetail().asJSON();
    }

    private void salvaModelloXsdUdAmmesso() throws EMFError {
        //
        if (getForm().getXsdModelliUdDetail().postAndValidate(getRequest(), getMessageBox())) {
            //
            try {
                boolean isUniDoc = getForm().getIdList().getId_tipo_unita_doc().parse() != null;
                boolean isDoc = getForm().getIdList().getId_tipo_doc().parse() != null;

                // utilizzo "generico" modello
                DecModelloXsdUdRowBean modelloXsdUdRowBean = new DecModelloXsdUdRowBean();
                // copy bean
                getForm().getXsdModelliUdDetail().copyToBean(modelloXsdUdRowBean);
                // checks
                Date today = Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                Date dtUsoIstituz = new Date(modelloXsdUdRowBean.getDtUsoIstituz().getTime());
                Date dtUsoSoppres = new Date(modelloXsdUdRowBean.getDtUsoSoppres().getTime());
                Date dtSoppres = new Date(modelloXsdUdRowBean.getDtSoppres().getTime());

                // check only on insert
                if (getForm().getXsdModelliUdDetail().getStatus().equals(Status.insert)) {
                    if (!dtUsoIstituz.equals(today) && dtUsoIstituz.before(today)) {
                        getMessageBox()
                                .addError("La data di attivazione deve essere successiva o uguale alla data odierna");
                    }
                    // verifica eistenza in uso
                    boolean inUso = false;
                    if (isUniDoc) {
                        inUso = modelliXsdUdEjb.existDecUsoModelloXsdUdOnUniDoc(
                                getForm().getIdList().getId_tipo_unita_doc().parse(),
                                modelloXsdUdRowBean.getTiUsoModelloXsd(), modelloXsdUdRowBean.getCdXsd());
                    } else if (isDoc) {
                        inUso = modelliXsdUdEjb.existDecUsoModelloXsdUdOnDoc(
                                getForm().getIdList().getId_tipo_doc().parse(),
                                modelloXsdUdRowBean.getTiUsoModelloXsd(), modelloXsdUdRowBean.getCdXsd());
                    }
                    if (inUso) {
                        getMessageBox()
                                .addError("Esiste gi\u00E0 una relazione associata al modello e versione selezionato");
                    }
                }
                // check date
                if (dtUsoSoppres.before(dtUsoIstituz)) {
                    getMessageBox()
                            .addError("La data di fine validit\u00E0 deve essere successiva alla data di attivazione");
                }
                if (dtUsoSoppres.after(dtSoppres)) {
                    getMessageBox().addError(
                            "La data di fine validit\u00E0 deve essere precedente o uguale alla data di fine validit\u00E0 del modello XSD ("
                                    + new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE).format(dtSoppres) + ")");
                }
                // check standard
                if (modelloXsdUdRowBean.getFlStandard().equals(CostantiDB.Flag.TRUE)) {
                    boolean finded = isUniDoc
                            && modelliXsdUdEjb.existDecUsoModelloXsdUdOnUniDocStandard(
                                    getForm().getIdList().getId_tipo_unita_doc().parse(),
                                    modelloXsdUdRowBean.getIdModelloXsdUd(), modelloXsdUdRowBean.getTiUsoModelloXsd())
                            || isDoc && modelliXsdUdEjb.existDecUsoModelloXsdUdOnDocStandard(
                                    getForm().getIdList().getId_tipo_doc().parse(),
                                    modelloXsdUdRowBean.getIdModelloXsdUd(), modelloXsdUdRowBean.getTiUsoModelloXsd());
                    if (finded) {
                        getMessageBox()
                                .addError("Esiste gi\u00E0 una relazione standard per il tipo modello xsd selezionato");
                    }
                }
                // no errors
                if (!getMessageBox().hasError() && (isUniDoc || isDoc)) {
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null,
                                    null, null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                    if (getForm().getXsdModelliUdDetail().getStatus().equals(Status.insert)) {
                        if (getForm().getIdList().getId_tipo_unita_doc().parse() != null) {
                            //
                            modelliXsdUdEjb.saveUsoModelloXsdUniDoc(param, modelloXsdUdRowBean);

                            // Aggiungo in fondo alla lista il modello xsd ud associato (tipo uni doc)
                            DecUsoModelloXsdUniDocRowBean row = new DecUsoModelloXsdUniDocRowBean();
                            getForm().getXsdModelliUdDetail().copyToBean(row);
                            getForm().getXsdModelliUdList().getTable().last();
                            getForm().getXsdModelliUdList().getTable().add(row);
                        } else if (getForm().getIdList().getId_tipo_doc().parse() != null) {
                            //
                            modelliXsdUdEjb.saveUsoModelloXsdDoc(param, modelloXsdUdRowBean);

                            // Aggiungo in fondo alla lista il modello xsd ud associato (tipo doc)
                            DecUsoModelloXsdDocRowBean row = new DecUsoModelloXsdDocRowBean();
                            getForm().getXsdModelliUdDetail().copyToBean(row);
                            getForm().getXsdModelliUdList().getTable().last();
                            getForm().getXsdModelliUdList().getTable().add(row);
                        }
                    } else if (getForm().getXsdModelliUdDetail().getStatus().equals(Status.update)) {
                        if (getForm().getIdList().getId_tipo_unita_doc().parse() != null) {
                            modelliXsdUdEjb.updateDecModelloXsdUdInUsoUniDoc(param, modelloXsdUdRowBean);
                        } else if (getForm().getIdList().getId_tipo_doc().parse() != null) {
                            modelliXsdUdEjb.updateDecModelloXsdUdInUsoDoc(param, modelloXsdUdRowBean);
                        }
                    }
                    //
                    getForm().getXsdModelliUdDetail().setStatus(Status.view);
                    getForm().getXsdModelliUdList().setStatus(Status.view);
                    getForm().getXsdModelliUdDetail().setViewMode();
                    goBack();
                    getMessageBox().setViewMode(ViewMode.plain);
                } else {
                    forwardToPublisher(Application.Publisher.MODELLO_XSD_UD_AMMESSO_DETAIL);
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
                forwardToPublisher(Application.Publisher.MODELLO_XSD_UD_AMMESSO_DETAIL);
            }
        }
    }

    @Override
    public void deleteXsdModelliUdList() throws EMFError {
        boolean isUniDoc = getForm().getIdList().getId_tipo_unita_doc().parse() != null;
        boolean isDoc = getForm().getIdList().getId_tipo_doc().parse() != null;
        // utilizzo il "generico" modello
        DecModelloXsdUdRowBean modelloXsdUdRowBean = new DecModelloXsdUdRowBean();
        // casistica uni doc
        if (isUniDoc) {
            DecUsoModelloXsdUniDocRowBean row = (DecUsoModelloXsdUniDocRowBean) getForm().getXsdModelliUdList()
                    .getTable().getCurrentRow();
            modelloXsdUdRowBean.copyFromBaseRow(row);
        } /* casistica doc */else if (isDoc) {
            DecUsoModelloXsdDocRowBean row = (DecUsoModelloXsdDocRowBean) getForm().getXsdModelliUdList().getTable()
                    .getCurrentRow();
            modelloXsdUdRowBean.copyFromBaseRow(row);
        }
        boolean deleted = true;
        int row = getForm().getXsdModelliUdList().getTable().getCurrentRowIndex();

        Date dtSoppres = modelloXsdUdRowBean.getDtUsoSoppres();
        Date today = Calendar.getInstance().getTime();
        if (dtSoppres.compareTo(today) < 0) {
            getMessageBox().addError("Versione modello XSD ammesso gi\u00E0 disattivato in precedenza");
            if (StringUtils.isNotBlank(getLastPublisher())) {
                forwardToPublisher(getLastPublisher());
            } else {
                goBack();
            }
        } else {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(configurationHelper.getParamApplicApplicationName(),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            if (getLastPublisher().equals(Application.Publisher.MODELLO_XSD_UD_AMMESSO_DETAIL)) {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            } else {
                param.setNomeAzione(
                        SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getXsdModelliUdList()));
            }
            //
            if (!getMessageBox().hasError()) {
                try {
                    boolean isInUse = false;
                    if (isUniDoc) {
                        isInUse = modelliXsdUdEjb.isUsoModelloXsdUdUniDocInUse(
                                getForm().getIdList().getId_strut().parse(),
                                modelloXsdUdRowBean.getIdUsoModelloXsdUniDoc());
                    } else if (isDoc) {
                        isInUse = modelliXsdUdEjb.isUsoModelloXsdUdDocInUse(getForm().getIdList().getId_strut().parse(),
                                modelloXsdUdRowBean.getIdUsoModelloXsdDoc());
                    }

                    if (isUniDoc || isDoc) {
                        // se in uso non posso cancellare, ma posso disattivare
                        if (isInUse) {
                            if (StringUtils.isNotBlank(getLastPublisher())) {
                                deleted = false;
                                // Mostra messaggio di disattivazione
                                getRequest().setAttribute("confermaDisattivazioneXsdUdAmmesso", true);
                                forwardToPublisher(getLastPublisher());
                            } else {
                                deleteXsdModelloAmmesso(param, modelloXsdUdRowBean, isUniDoc, isDoc);
                            }
                        } else {
                            deleteXsdModelloAmmesso(param, modelloXsdUdRowBean, isUniDoc, isDoc);
                        }
                    }
                } catch (ParerUserError ex) {
                    deleted = false;
                    getMessageBox().addError(
                            "Versione modello xsd ammesso non pu\u00F2 essere eliminato: " + ex.getDescription());
                }

                //
                if (deleted) {
                    getForm().getXsdModelliUdList().getTable().remove(row);
                    getMessageBox().setViewMode(ViewMode.plain);
                }
            }
            if (!getMessageBox().hasError()
                    && getLastPublisher().equals(Application.Publisher.MODELLO_XSD_UD_AMMESSO_DETAIL)) {
                goBack();
            } else {
                forwardToPublisher(getLastPublisher());
            }
        }
    }

    private void deleteXsdModelloAmmesso(LogParam param, DecModelloXsdUdRowBean modelloXsdUdRowBean, boolean isUniDoc,
            boolean isDoc) throws EMFError, ParerUserError {
        if (isUniDoc) {
            modelliXsdUdEjb.deleteDecUsoModelloXsdUniDoc(param, getForm().getIdList().getId_strut().parse(),
                    modelloXsdUdRowBean.getIdTipoUniDoc(), modelloXsdUdRowBean.getIdUsoModelloXsdUniDoc());
        } else if (isDoc) {
            modelliXsdUdEjb.deleteDecUsoModelloXsdDoc(param, getForm().getIdList().getId_strut().parse(),
                    modelloXsdUdRowBean.getIdTipoDoc(), modelloXsdUdRowBean.getIdUsoModelloXsdDoc());
        }
    }

    public void confermaDisattivazioneXsdUdAmmesso() throws EMFError {
        boolean isUniDoc = getForm().getIdList().getId_tipo_unita_doc().parse() != null;
        boolean isDoc = getForm().getIdList().getId_tipo_doc().parse() != null;
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                        CostantiDB.TipoAplVGetValAppart.APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());

        DecModelloXsdUdRowBean modelloXsdUdRowBean = new DecModelloXsdUdRowBean();
        if (isUniDoc) {
            DecUsoModelloXsdUniDocRowBean row = (DecUsoModelloXsdUniDocRowBean) getForm().getXsdModelliUdList()
                    .getTable().getCurrentRow();
            modelloXsdUdRowBean.copyFromBaseRow(row);
        } else if (isDoc) {
            DecUsoModelloXsdDocRowBean row = (DecUsoModelloXsdDocRowBean) getForm().getXsdModelliUdList().getTable()
                    .getCurrentRow();
            modelloXsdUdRowBean.copyFromBaseRow(row);
        }

        DecUsoModelloXsdUniDocTableBean usoModelloXsdUniDocTableBean = null;
        DecUsoModelloXsdDocTableBean usoModelloXsdDocTableBean = null;
        try {
            if (isUniDoc) {
                usoModelloXsdUniDocTableBean = modelliXsdUdEjb.deactivateDecModelloXsdUdInUsoUniDoc(param,
                        modelloXsdUdRowBean);
            } else if (isDoc) {
                usoModelloXsdDocTableBean = modelliXsdUdEjb.deactivateDecModelloXsdUdInUsoDoc(param,
                        modelloXsdUdRowBean);
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        //
        if (!getMessageBox().hasError()) {
            //
            if (isUniDoc) {
                getForm().getXsdModelliUdList().setTable(usoModelloXsdUniDocTableBean);
            } else if (isDoc) {
                getForm().getXsdModelliUdList().setTable(usoModelloXsdDocTableBean);

            }

            getForm().getXsdModelliUdList().getTable().first();
            getForm().getXsdModelliUdList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            //
            getForm().getXsdModelliUdDetail().setStatus(Status.view);
            getForm().getXsdModelliUdList().setStatus(Status.view);
            getForm().getXsdModelliUdDetail().setViewMode();
            if (getLastPublisher().equals(Application.Publisher.MODELLO_XSD_UD_AMMESSO_DETAIL)) {
                goBack();
            } else if (StringUtils.isNotBlank(getLastPublisher())) {
                forwardToPublisher(getLastPublisher());
            } else {
                goBack();
            }
            getMessageBox().setViewMode(ViewMode.plain);
        } else if (StringUtils.isNotBlank(getLastPublisher())) {
            forwardToPublisher(getLastPublisher());
        } else {
            goBack();
        }
    }

    private AplParamApplicTableBean obfuscatePasswordParamApplic(AplParamApplicTableBean paramApplicTableBean) {
        // MEV25687 - offusca le password
        Iterator<AplParamApplicRowBean> rowIt = paramApplicTableBean.iterator();
        while (rowIt.hasNext()) {
            AplParamApplicRowBean rowBean = rowIt.next();
            if (rowBean.getTiValoreParamApplic().equals(Constants.ComboValueParamentersType.PASSWORD.name())) {
                rowBean.setString("ds_valore_param_applic", Constants.OBFUSCATED_STRING);

                if (rowBean.getString("ds_valore_param_applic_applic") != null)
                    rowBean.setString("ds_valore_param_applic_applic", Constants.OBFUSCATED_STRING);

                if (rowBean.getString("ds_valore_param_applic_ambiente") != null)
                    rowBean.setString("ds_valore_param_applic_ambiente", Constants.OBFUSCATED_STRING);

                if (rowBean.getString("ds_valore_param_applic_strut") != null)
                    rowBean.setString("ds_valore_param_applic_strut", Constants.OBFUSCATED_STRING);

                if (rowBean.getString("ds_valore_param_applic_tipo_ud_amm") != null)
                    rowBean.setString("ds_valore_param_applic_tipo_ud_amm", Constants.OBFUSCATED_STRING);

                if (rowBean.getString("ds_valore_param_applic_tipo_ud_gest") != null)
                    rowBean.setString("ds_valore_param_applic_tipo_ud_gest", Constants.OBFUSCATED_STRING);

                if (rowBean.getString("ds_valore_param_applic_tipo_ud_cons") != null)
                    rowBean.setString("ds_valore_param_applic_tipo_ud_cons", Constants.OBFUSCATED_STRING);

            }
        }

        return paramApplicTableBean;
    }

}
