package it.eng.parer.web.action;

import it.eng.parer.amministrazioneStrutture.gestioneRegistro.ejb.RegistroEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoDoc.ejb.TipoDocumentoEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb.TipoUnitaDocEjb;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.ejb.common.helper.ParamApplicHelper;
import it.eng.parer.sacerlog.slite.gen.form.GestioneLogEventiForm;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.sacerlog.util.web.SpagoliteLogUtil;
import it.eng.parer.serie.dto.CreazioneModelloSerieBean;
import it.eng.parer.serie.ejb.ModelliSerieEjb;
import static it.eng.parer.serie.ejb.SerieEjb.CD_SERIE_PATTERN;
import it.eng.parer.serie.ejb.TipoSerieEjb;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.ModelliSerieAbstractAction;
import it.eng.parer.slite.gen.form.ModelliSerieForm.ModelliTipiSerieDetail;
import it.eng.parer.slite.gen.tablebean.DecModelloCampoInpUdRowBean;
import it.eng.parer.slite.gen.tablebean.DecModelloCampoInpUdTableBean;
import it.eng.parer.slite.gen.tablebean.DecModelloCampoOutSelUdRowBean;
import it.eng.parer.slite.gen.tablebean.DecModelloCampoOutSelUdTableBean;
import it.eng.parer.slite.gen.tablebean.DecModelloFiltroSelUdattbRowBean;
import it.eng.parer.slite.gen.tablebean.DecModelloFiltroSelUdattbTableBean;
import it.eng.parer.slite.gen.tablebean.DecModelloFiltroTiDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecModelloFiltroTiDocTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecModelloOutSelUdRowBean;
import it.eng.parer.slite.gen.tablebean.DecModelloOutSelUdTableBean;
import it.eng.parer.slite.gen.tablebean.DecModelloOutSelUdTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecModelloTipoSerieRowBean;
import it.eng.parer.slite.gen.tablebean.DecNotaModelloTipoSerieRowBean;
import it.eng.parer.slite.gen.tablebean.DecNotaModelloTipoSerieTableBean;
import it.eng.parer.slite.gen.tablebean.DecUsoModelloTipoSerieRowBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.ActionUtils;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.web.validator.UnitaDocumentarieValidator;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoIFace.Values;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.Secure;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Bonora_L
 */
public class ModelliSerieAction extends ModelliSerieAbstractAction {

    private static final Logger logger = LoggerFactory.getLogger(ModelliSerieAction.class);

    @EJB(mappedName = "java:app/Parer-ejb/ModelliSerieEjb")
    private ModelliSerieEjb modelliSerieEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configHelper;
    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoUnitaDocEjb")
    private TipoUnitaDocEjb tipoUnitaDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/RegistroEjb")
    private RegistroEjb registroEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoDocumentoEjb")
    private TipoDocumentoEjb tipoDocumentoEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoSerieEjb")
    private TipoSerieEjb tipoSerieEjb;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
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
                if (getTableName().equals(getForm().getModelliTipiSerieList().getName())) {
                    initModelloSerieDetail();
                    DecModelloTipoSerieRowBean currentRow = (DecModelloTipoSerieRowBean) getForm()
                            .getModelliTipiSerieList().getTable().getCurrentRow();
                    loadDettaglioModello(currentRow.getIdModelloTipoSerie());
                } else if (getTableName().equals(getForm().getNoteModelloTipoSerieList().getName())) {
                    DecNotaModelloTipoSerieRowBean currentRow = (DecNotaModelloTipoSerieRowBean) getForm()
                            .getNoteModelloTipoSerieList().getTable().getCurrentRow();
                    getForm().getNoteModelloTipoSerieDetail().getId_tipo_nota_serie()
                            .setDecodeMap(DecodeMap.Factory.newInstance(tipoSerieEjb.getDecTipoNotaSerieTableBean(null),
                                    "id_tipo_nota_serie", "ds_tipo_nota_serie"));

                    getForm().getNoteModelloTipoSerieDetail().copyFromBean(currentRow);

                    getForm().getNoteModelloTipoSerieList().setStatus(Status.view);
                    getForm().getNoteModelloTipoSerieDetail().setStatus(Status.view);
                    getForm().getNoteModelloTipoSerieDetail().setViewMode();
                } else if (getTableName().equals(getForm().getRegoleAcquisizioneList().getName())) {
                    switch (getNavigationEvent()) {
                    case NE_DETTAGLIO_VIEW:
                        getForm().getRegoleAcquisizioneList().setUserOperations(false, false, false, false);
                        getForm().getRegoleAcquisizioneList().setStatus(Status.view);
                        break;
                    case NE_DETTAGLIO_UPDATE:
                        editRegoleAcquisizione(Status.update);
                        break;
                    }
                } else if (getTableName().equals(getForm().getRegoleFiltraggioList().getName())) {
                    BigDecimal idModelloTipoSerie = getForm().getModelliTipiSerieDetail().getId_modello_tipo_serie()
                            .parse();
                    initRegoleFiltraggioDetail(idModelloTipoSerie);
                    getForm().getRegoleFiltraggioDetail().setViewMode();
                    getForm().getRegoleFiltraggioDetail().setStatus(Status.view);
                } else if (getTableName().equals(getForm().getDatiSpecList().getName())) {
                    BigDecimal idModelloTipoSerie = getForm().getModelliTipiSerieDetail().getId_modello_tipo_serie()
                            .parse();
                    if (idModelloTipoSerie != null) {
                        getForm().getFiltriDatiSpecList().getTi_oper().setDecodeMap(ComboGetter
                                .getMappaSortedGenericEnum("operatore", CostantiDB.TipoOperatoreDatiSpec.values()));
                        BigDecimal idTipoUnitaDoc = getForm().getModelliTipiSerieDetail()
                                .getId_tipo_unita_doc_dati_spec().parse();
                        BigDecimal idTipoDoc = getForm().getModelliTipiSerieDetail().getId_tipo_doc_dati_spec().parse();
                        getForm().getFiltriDatiSpecList().setTable(modelliSerieEjb
                                .getDecModelloFiltroSelUdattbTableBean(idModelloTipoSerie, idTipoUnitaDoc, idTipoDoc));
                        getForm().getFiltriDatiSpecList().getTable().setPageSize(300);
                        getForm().getFiltriDatiSpecList().getTable().first();
                        getForm().getFiltriDatiSpecList().getTi_oper().setEditMode();
                        getForm().getFiltriDatiSpecList().getDl_valore().setEditMode();

                        getForm().getDatiSpecDetail().setStatus(Status.update);
                    }
                } else if (getTableName().equals(getForm().getRegoleRapprList().getName())) {
                    editRegoleRappresentazione(Status.update);
                }
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Caricamento dettaglio modello">
    private void loadDettaglioModello(BigDecimal idModelloTipoSerie) throws EMFError, ParerUserError {
        DecModelloTipoSerieRowBean detailRow = modelliSerieEjb.getDecModelloTipoSerieRowBean(idModelloTipoSerie);
        getForm().getModelliTipiSerieDetail().copyFromBean(detailRow);

        getForm().getModelliTipiSerieDetail().setViewMode();
        getForm().getModelliTipiSerieDetail().setStatus(Status.view);
        getForm().getModelliTipiSerieList().setStatus(Status.view);

        BigDecimal niMesiCreazioneSerie;
        if ((niMesiCreazioneSerie = detailRow.getNiMmCreaAutom()) != null) {
            if (niMesiCreazioneSerie.doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.DECADE.getNumSerie()
                    .doubleValue()) {
                getForm().getModelliTipiSerieDetail().getNi_transcoded_mm_crea_autom()
                        .setValue(CostantiDB.IntervalliMeseCreazioneSerie.DECADE.name());
            } else if (niMesiCreazioneSerie.doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.QUINDICINA
                    .getNumSerie().doubleValue()) {
                getForm().getModelliTipiSerieDetail().getNi_transcoded_mm_crea_autom()
                        .setValue(CostantiDB.IntervalliMeseCreazioneSerie.QUINDICINA.name());
            } else if (niMesiCreazioneSerie.doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.MESE.getNumSerie()
                    .doubleValue()) {
                getForm().getModelliTipiSerieDetail().getNi_transcoded_mm_crea_autom()
                        .setValue(CostantiDB.IntervalliMeseCreazioneSerie.MESE.name());
            } else if (niMesiCreazioneSerie.doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.BIMESTRE
                    .getNumSerie().doubleValue()) {
                getForm().getModelliTipiSerieDetail().getNi_transcoded_mm_crea_autom()
                        .setValue(CostantiDB.IntervalliMeseCreazioneSerie.BIMESTRE.name());
            } else if (niMesiCreazioneSerie.doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.TRIMESTRE
                    .getNumSerie().doubleValue()) {
                getForm().getModelliTipiSerieDetail().getNi_transcoded_mm_crea_autom()
                        .setValue(CostantiDB.IntervalliMeseCreazioneSerie.TRIMESTRE.name());
            } else if (niMesiCreazioneSerie.doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.QUADRIMESTRE
                    .getNumSerie().doubleValue()) {
                getForm().getModelliTipiSerieDetail().getNi_transcoded_mm_crea_autom()
                        .setValue(CostantiDB.IntervalliMeseCreazioneSerie.QUADRIMESTRE.name());
            } else if (niMesiCreazioneSerie.doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.SEMESTRE
                    .getNumSerie().doubleValue()) {
                getForm().getModelliTipiSerieDetail().getNi_transcoded_mm_crea_autom()
                        .setValue(CostantiDB.IntervalliMeseCreazioneSerie.SEMESTRE.name());
            }
        }

        BigDecimal ni_anni_conserv = detailRow.getNiAnniConserv();
        if (ni_anni_conserv != null) {
            long anni = ni_anni_conserv.longValue();
            if (anni == 9999L) {
                getForm().getModelliTipiSerieDetail().getConserv_unlimited()
                        .setValue(JobConstants.ComboFlag.SI.getValue());
            } else {
                getForm().getModelliTipiSerieDetail().getConserv_unlimited()
                        .setValue(JobConstants.ComboFlag.NO.getValue());
            }
        }

        getForm().getNoteModelloTipoSerieList()
                .setTable(modelliSerieEjb.getDecNotaModelloTipoSerieTableBean(detailRow.getIdModelloTipoSerie()));
        getForm().getNoteModelloTipoSerieList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getNoteModelloTipoSerieList().getTable().first();

        getForm().getRegoleRapprList()
                .setTable(modelliSerieEjb.getDecModelloOutSelUdTableBean(detailRow.getIdModelloTipoSerie()));
        getForm().getRegoleRapprList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getRegoleRapprList().getTable().first();
        // Se sono state già inserite tutte le regole di rappresentazione allora nasconde il bottone di insert
        getForm().getRegoleRapprList().setHideInsertButton(getForm().getRegoleRapprList().getTable()
                .size() == CostantiDB.TipoDiRappresentazione.getComboTipoDiRappresentazione().length);

        getForm().getRegoleFiltraggioList().setTable(
                modelliSerieEjb.getDecModelloFiltroTiDocSingleRowTableBean(detailRow.getIdModelloTipoSerie()));
        getForm().getRegoleFiltraggioList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getRegoleFiltraggioList().getTable().first();
        if (detailRow.getTiRglFiltroTiDoc().equals(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name())) {
            if (getForm().getRegoleFiltraggioList().getTable().isEmpty()) {
                getForm().getRegoleFiltraggioList().setHideInsertButton(false);
            } else {
                getForm().getRegoleFiltraggioList().setHideInsertButton(true);
            }
        } else {
            getForm().getRegoleFiltraggioList().setHideInsertButton(true);
        }

        getForm().getDatiSpecList()
                .setTable(modelliSerieEjb.getDecModelloFiltroSelUdattbTableBean(detailRow.getIdModelloTipoSerie()));
        getForm().getDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getDatiSpecList().getTable().first();

        getForm().getRegoleAcquisizioneList()
                .setTable(modelliSerieEjb.getDecModelloCampoInpUdTableBean(detailRow.getIdModelloTipoSerie()));
        getForm().getRegoleAcquisizioneList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getRegoleAcquisizioneList().getTable().first();
        getForm().getRegoleAcquisizioneList().setUserOperations(true, true, true, true);
        getForm().getRegoleAcquisizioneList().setStatus(Status.view);
        getForm().getRegoleAcquisizioneDetail().setStatus(Status.view);

        getForm().getStrutModelloList()
                .setTable(modelliSerieEjb.getDecUsoModelloTipoSerieTableBean(detailRow.getIdModelloTipoSerie()));
        getForm().getStrutModelloList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getStrutModelloList().getTable().first();
    }

    private void initModelloSerieDetail() {
        BaseTableInterface ambienteTableBean = ambienteEjb.getAmbientiAbilitatiPerStrut(getUser().getIdUtente(),
                configHelper.getValoreParamApplic("NM_APPLIC", null, null, null, null,
                        CostantiDB.TipoAplVGetValAppart.APPLIC));
        ambienteTableBean.addSortingRule("nm_ambiente", SortingRule.ASC);
        ambienteTableBean.sort();
        getForm().getModelliTipiSerieDetail().getId_ambiente()
                .setDecodeMap(DecodeMap.Factory.newInstance(ambienteTableBean, "id_ambiente", "nm_ambiente"));
        getForm().getModelliTipiSerieDetail().getTi_conservazione_serie()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum(
                        ModelliTipiSerieDetail.ti_conservazione_serie.toLowerCase(),
                        CostantiDB.TipoConservazioneSerie.values()));
        getForm().getModelliTipiSerieDetail().getTi_rgl_anni_conserv().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum(ModelliTipiSerieDetail.ti_rgl_anni_conserv.toLowerCase(),
                        CostantiDB.TipoRegolaModelloTipoSerie.getTiRglAnniConserv()));
        getForm().getModelliTipiSerieDetail().getTi_rgl_cd_serie().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum(ModelliTipiSerieDetail.ti_rgl_cd_serie.toLowerCase(),
                        CostantiDB.TipoRegolaModelloTipoSerie.getTiRglCdSerie()));
        getForm().getModelliTipiSerieDetail().getTi_rgl_conservazione_serie()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum(
                        ModelliTipiSerieDetail.ti_rgl_conservazione_serie.toLowerCase(),
                        CostantiDB.TipoRegolaModelloTipoSerie.getTiRglConservazioneSerie()));
        getForm().getModelliTipiSerieDetail().getTi_rgl_ds_serie().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum(ModelliTipiSerieDetail.ti_rgl_ds_serie.toLowerCase(),
                        CostantiDB.TipoRegolaModelloTipoSerie.getTiRglDsSerie()));
        getForm().getModelliTipiSerieDetail().getTi_rgl_ds_tipo_serie().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum(ModelliTipiSerieDetail.ti_rgl_ds_tipo_serie.toLowerCase(),
                        CostantiDB.TipoRegolaModelloTipoSerie.getTiRglDsTipoSerie()));
        getForm().getModelliTipiSerieDetail().getTi_rgl_filtro_ti_doc().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum(ModelliTipiSerieDetail.ti_rgl_filtro_ti_doc.toLowerCase(),
                        CostantiDB.TipoRegolaModelloTipoSerie.getTiRglFiltroTiDoc()));
        getForm().getModelliTipiSerieDetail().getTi_rgl_nm_tipo_serie().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum(ModelliTipiSerieDetail.ti_rgl_nm_tipo_serie.toLowerCase(),
                        CostantiDB.TipoRegolaModelloTipoSerie.getTiRglNmTipoSerie()));
        getForm().getModelliTipiSerieDetail().getTi_rgl_range_anni_crea_autom()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum(
                        ModelliTipiSerieDetail.ti_rgl_range_anni_crea_autom.toLowerCase(),
                        CostantiDB.TipoRegolaModelloTipoSerie.getTiRglRangeAnniCreaAutom()));
        getForm().getModelliTipiSerieDetail().getTi_sel_ud().setDecodeMap(ComboGetter.getMappaTiSelUd());
        getForm().getModelliTipiSerieDetail().getConserv_unlimited()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getModelliTipiSerieDetail().getFl_controllo_consist_obblig()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getModelliTipiSerieDetail().getFl_crea_autom().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getModelliTipiSerieDetail().getNi_transcoded_mm_crea_autom()
                .setDecodeMap(ComboGetter.getMappaNiMesiCreazioneSerie());
        getForm().getModelliTipiSerieDetail().getTi_stato_ver_serie_autom()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum(
                        ModelliTipiSerieDetail.ti_stato_ver_serie_autom.toLowerCase(),
                        CostantiDB.StatoVersioneSerie.getStatiVerSerieAutom()));
    }
    // </editor-fold>

    @Override
    public void undoDettaglio() throws EMFError {
        try {
            if (getLastPublisher().equals(Application.Publisher.MODELLO_SERIE_DETAIL)
                    && getForm().getModelliTipiSerieList().getStatus().equals(Status.update)) {
                DecModelloTipoSerieRowBean currentRow = (DecModelloTipoSerieRowBean) getForm().getModelliTipiSerieList()
                        .getTable().getCurrentRow();
                BigDecimal idModelloTipoSerie = currentRow.getIdModelloTipoSerie();
                if (idModelloTipoSerie != null) {
                    loadDettaglioModello(idModelloTipoSerie);
                }
                getForm().getModelliTipiSerieDetail().setViewMode();
                getForm().getModelliTipiSerieDetail().setStatus(Status.view);
                getForm().getModelliTipiSerieList().setStatus(Status.view);

                forwardToPublisher(Application.Publisher.MODELLO_SERIE_DETAIL);
            } else {
                goBack();
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void insertDettaglio() throws EMFError {
        try {
            if (getTableName().equals(getForm().getModelliTipiSerieList().getName())) {
                getForm().getModelliTipiSerieDetail().reset();
                initModelloSerieDetail();

                getForm().getModelliTipiSerieDetail().setEditMode();
                getForm().getModelliTipiSerieDetail().getDt_istituz()
                        .setValue(ActionUtils.getStringDate(Calendar.getInstance().getTime()));
                getForm().getModelliTipiSerieDetail().getDt_soppres().setValue(ActionUtils.getStringDate(null));
                getForm().getModelliTipiSerieDetail().getTi_rgl_nm_tipo_serie()
                        .setValue(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name());
                getForm().getModelliTipiSerieDetail().getTi_rgl_ds_tipo_serie()
                        .setValue(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name());
                getForm().getModelliTipiSerieDetail().getTi_rgl_cd_serie()
                        .setValue(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name());
                getForm().getModelliTipiSerieDetail().getTi_rgl_ds_serie()
                        .setValue(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name());
                getForm().getModelliTipiSerieDetail().getTi_rgl_anni_conserv()
                        .setValue(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name());
                getForm().getModelliTipiSerieDetail().getTi_rgl_conservazione_serie()
                        .setValue(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name());
                getForm().getModelliTipiSerieDetail().getTi_rgl_range_anni_crea_autom()
                        .setValue(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name());
                getForm().getModelliTipiSerieDetail().getTi_rgl_filtro_ti_doc()
                        .setValue(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name());
                getForm().getModelliTipiSerieDetail().getTi_stato_ver_serie_autom()
                        .setValue(CostantiDB.StatoVersioneSerie.DA_VALIDARE.name());

                getForm().getModelliTipiSerieList().setStatus(Status.insert);
                getForm().getModelliTipiSerieDetail().setStatus(Status.insert);

                forwardToPublisher(Application.Publisher.MODELLO_SERIE_DETAIL);
            } else if (getTableName().equals(getForm().getNoteModelloTipoSerieList().getName())) {

                DateFormat formato = new SimpleDateFormat(WebConstants.DATE_FORMAT_HOUR_MINUTE_TYPE);

                getForm().getNoteModelloTipoSerieDetail().clear();
                getForm().getNoteModelloTipoSerieDetail().setViewMode();

                getForm().getNoteModelloTipoSerieDetail().getPg_nota_tipo_serie().setValue(null);
                getForm().getNoteModelloTipoSerieDetail().getDt_nota_tipo_serie()
                        .setValue(formato.format(Calendar.getInstance().getTime()));
                getForm().getNoteModelloTipoSerieDetail().getId_tipo_nota_serie()
                        .setDecodeMap(DecodeMap.Factory.newInstance(
                                tipoSerieEjb.getDecTipoNotaSerieNotInModelloTableBean(
                                        getForm().getModelliTipiSerieDetail().getId_modello_tipo_serie().parse()),
                                "id_tipo_nota_serie", "ds_tipo_nota_serie"));
                getForm().getNoteModelloTipoSerieDetail().getId_tipo_nota_serie().setEditMode();
                getForm().getNoteModelloTipoSerieDetail().getDs_nota_tipo_serie().setEditMode();

                getForm().getNoteModelloTipoSerieList().setStatus(Status.insert);
                getForm().getNoteModelloTipoSerieDetail().setStatus(Status.insert);

                forwardToPublisher(Application.Publisher.NOTA_MODELLO_SERIE_DETAIL);
            } else if (getTableName().equals(getForm().getRegoleAcquisizioneList().getName())) {
                editRegoleAcquisizione(Status.insert);
                forwardToPublisher(Application.Publisher.REGOLA_MODELLO_ACQUISIZIONE_DETAIL);
            } else if (getTableName().equals(getForm().getRegoleFiltraggioList().getName())) {
                BigDecimal idModelloTipoSerie = getForm().getModelliTipiSerieDetail().getId_modello_tipo_serie()
                        .parse();
                initRegoleFiltraggioDetail(idModelloTipoSerie);
                getForm().getRegoleFiltraggioDetail().setEditMode();
                getForm().getRegoleFiltraggioDetail().setStatus(Status.insert);
                forwardToPublisher(Application.Publisher.REGOLA_MODELLO_FILTRAGGIO_DETAIL);
            } else if (getTableName().equals(getForm().getDatiSpecList().getName())) {
                BigDecimal idModelloTipoSerie = getForm().getModelliTipiSerieDetail().getId_modello_tipo_serie()
                        .parse();
                if (idModelloTipoSerie != null) {
                    getForm().getFiltriDatiSpecList().getTi_oper().setDecodeMap(ComboGetter
                            .getMappaSortedGenericEnum("operatore", CostantiDB.TipoOperatoreDatiSpec.values()));
                    BigDecimal idTipoUnitaDoc = getForm().getModelliTipiSerieDetail().getId_tipo_unita_doc_dati_spec()
                            .parse();
                    BigDecimal idTipoDoc = getForm().getModelliTipiSerieDetail().getId_tipo_doc_dati_spec().parse();
                    getForm().getFiltriDatiSpecList().setTable(modelliSerieEjb
                            .getDecModelloFiltroSelUdattbTableBean(idModelloTipoSerie, idTipoUnitaDoc, idTipoDoc));
                    getForm().getFiltriDatiSpecList().getTable().setPageSize(300);
                    getForm().getFiltriDatiSpecList().getTable().first();
                    getForm().getFiltriDatiSpecList().getTi_oper().setEditMode();
                    getForm().getFiltriDatiSpecList().getDl_valore().setEditMode();

                    getForm().getDatiSpecDetail().setStatus(Status.insert);
                    forwardToPublisher(Application.Publisher.FILTRI_DATI_SPEC_MODELLO_DETAIL);
                }
            } else if (getTableName().equals(getForm().getRegoleRapprList().getName())) {
                editRegoleRappresentazione(Status.insert);
                forwardToPublisher(Application.Publisher.REGOLA_MODELLO_RAPPRESENTAZIONE_DETAIL);
            } else if (getTableName().equals(getForm().getStrutModelloList().getName())) {
                getForm().getStrutModelloDetail().setEditMode();
                getForm().getStrutModelloDetail().setStatus(Status.insert);

                BigDecimal idAmbiente = getForm().getModelliTipiSerieDetail().getId_ambiente().parse();
                BigDecimal idModelloTipoSerie = getForm().getModelliTipiSerieDetail().getId_modello_tipo_serie()
                        .parse();
                getForm().getStrutModelloDetail().getId_modello_tipo_serie()
                        .setValue(idModelloTipoSerie.toPlainString());
                getForm().getStrutModelloDetail().getId_ambiente().setValue(idAmbiente.toPlainString());
                OrgEnteTableBean orgEnteTableBean = ambienteEjb.getEntiAbilitati(getUser().getIdUtente(),
                        idAmbiente.longValue(), Boolean.FALSE);
                getForm().getStrutModelloDetail().getNm_ente()
                        .setDecodeMap(DecodeMap.Factory.newInstance(orgEnteTableBean, "id_ente", "nm_ente"));

                getForm().getStrutRicercateList().setTable(new OrgStrutTableBean());
                getForm().getStrutRicercateList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getStrutSelezionateList().setTable(new OrgStrutTableBean());
                getForm().getStrutSelezionateList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

                forwardToPublisher(Application.Publisher.STRUT_MODELLO_SERIE_DETAIL);
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
    }

    @Override
    public void saveDettaglio() throws EMFError {
        if (getTableName().equals(getForm().getModelliTipiSerieList().getName())
                || getTableName().equals(getForm().getModelliTipiSerieDetail().getName())) {
            saveModelloTipoSerie();
        } else if (getTableName().equals(getForm().getNoteModelloTipoSerieList().getName())
                || getTableName().equals(getForm().getNoteModelloTipoSerieDetail().getName())) {
            saveNotaModelloTipoSerie();
        } else if (getTableName().equals(getForm().getRegoleAcquisizioneDetail().getName())) {
            saveRegoleModelloAcquisizione();
        } else if (getTableName().equals(getForm().getRegoleFiltraggioDetail().getName())) {
            saveRegoleModelloFiltraggio();
        } else if (getTableName().equals(getForm().getDatiSpecDetail().getName())) {
            saveDatiSpecificiModello();
        } else if (getTableName().equals(getForm().getRegoleRapprDetail().getName())) {
            saveRegoleModelloRappresentazione();
        } else if (getTableName().equals(getForm().getStrutModelloDetail().getName())) {
            saveAssociazioniStrutturaModello();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Salvataggio/Modifica/Elimina modello di tipo serie">
    private void saveModelloTipoSerie() throws EMFError {
        if (getForm().getModelliTipiSerieDetail().postAndValidate(getRequest(), getMessageBox())) {
            try {
                checkModelloTipoSerie();
                if (!getMessageBox().hasError()) {
                    // Salva il modello
                    if (!getMessageBox().hasError()) {
                        CreazioneModelloSerieBean creazioneBean = new CreazioneModelloSerieBean(
                                getForm().getModelliTipiSerieDetail());
                        /*
                         * Codice aggiuntivo per il logging...
                         */
                        LogParam param = SpagoliteLogUtil.getLogParam(
                                configHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null,
                                        null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                        if (getForm().getModelliTipiSerieList().getStatus().equals(Status.insert)) {
                            param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                            Long idModelloTipoSerie = modelliSerieEjb.saveModelloTipoSerie(param, creazioneBean);
                            if (idModelloTipoSerie != null) {
                                getForm().getModelliTipiSerieDetail().getId_modello_tipo_serie()
                                        .setValue(idModelloTipoSerie.toString());
                            }
                            DecModelloTipoSerieRowBean row = new DecModelloTipoSerieRowBean();
                            getForm().getModelliTipiSerieDetail().copyToBean(row);
                            getForm().getModelliTipiSerieList().getTable().last();
                            getForm().getModelliTipiSerieList().getTable().add(row);
                        } else if (getForm().getModelliTipiSerieList().getStatus().equals(Status.update)) {
                            param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                            BigDecimal idModelloTipoSerie = getForm().getModelliTipiSerieDetail()
                                    .getId_modello_tipo_serie().parse();
                            modelliSerieEjb.saveModelloTipoSerie(param, idModelloTipoSerie, creazioneBean);
                        }
                        BigDecimal idModelloTipoSerie = getForm().getModelliTipiSerieDetail().getId_modello_tipo_serie()
                                .parse();
                        if (idModelloTipoSerie != null) {
                            loadDettaglioModello(idModelloTipoSerie);
                        }
                        getForm().getModelliTipiSerieDetail().setViewMode();
                        getForm().getModelliTipiSerieList().setStatus(Status.view);
                        getForm().getModelliTipiSerieDetail().setStatus(Status.view);
                        getMessageBox().addInfo("Modello salvato con successo");
                        getMessageBox().setViewMode(ViewMode.plain);
                    }
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
            forwardToPublisher(Application.Publisher.MODELLO_SERIE_DETAIL);
        }
    }

    private void checkModelloTipoSerie() throws EMFError, ParerUserError {
        logger.debug("Eseguo i controlli sul salvataggio del modello");
        ModelliTipiSerieDetail modelliTipiSerieDetail = getForm().getModelliTipiSerieDetail();
        BigDecimal idAmbiente = modelliTipiSerieDetail.getId_ambiente().parse();
        String nmModello = modelliTipiSerieDetail.getNm_modello_tipo_serie().parse();
        BigDecimal idModelloUpdate = modelliTipiSerieDetail.getId_modello_tipo_serie().parse();
        // Controllo esistenza modello
        DecModelloTipoSerieRowBean tmpRow = modelliSerieEjb.getDecModelloTipoSerieRowBean(nmModello, idAmbiente);
        if (idModelloUpdate != null) {
            if (tmpRow != null && !idModelloUpdate.equals(tmpRow.getIdModelloTipoSerie())) {
                throw new ParerUserError("Il nome del modello di tipo serie \u00E8 gi\u00E0 presente nell'ambiente");
            }
        } else if (tmpRow != null) {
            throw new ParerUserError("Il nome del modello di tipo serie \u00E8 gi\u00E0 presente nell'ambiente");
        }

        String tiRglNmTipoSerie = modelliTipiSerieDetail.getTi_rgl_nm_tipo_serie().parse();
        String nmTipoSerie = modelliTipiSerieDetail.getNm_tipo_serie_da_creare().parse();
        if (tiRglNmTipoSerie.equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_TIPO_UD_REG.name())
                && StringUtils.isNotBlank(nmTipoSerie)) {
            throw new ParerUserError(
                    "\u00C8 stato indicato che tipologia serie sia definita sul registro o sulla tipologia di unit\u00E0 documentaria; il relativo campo non deve essere valorizzato");
        } else if (tiRglNmTipoSerie.equals(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name())
                && StringUtils.isBlank(nmTipoSerie)) {
            throw new ParerUserError(
                    "\u00C8 stato indicato che tipologia serie sia definita sul modello; occorre valorizzare il relativo campo");
        }
        String tiRglDsTipoSerie = modelliTipiSerieDetail.getTi_rgl_ds_tipo_serie().parse();
        String dsTipoSerie = modelliTipiSerieDetail.getDs_tipo_serie_da_creare().parse();
        if (tiRglDsTipoSerie.equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_TIPO_UD_REG.name())
                && StringUtils.isNotBlank(dsTipoSerie)) {
            throw new ParerUserError(
                    "\u00C8 stato indicato che tipologia serie sia definita sul registro o sulla tipologia di unit\u00E0 documentaria; il relativo campo non deve essere valorizzato");
        } else if (tiRglDsTipoSerie.equals(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name())
                && StringUtils.isBlank(dsTipoSerie)) {
            throw new ParerUserError(
                    "\u00C8 stato indicato che tipologia serie sia definita sul modello; occorre valorizzare il relativo campo");
        }
        String tiRglCdSerie = modelliTipiSerieDetail.getTi_rgl_cd_serie().parse();
        String cdSerie = modelliTipiSerieDetail.getCd_serie_da_creare().parse();
        if (tiRglCdSerie.equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_TIPO_UD_REG.name())
                && StringUtils.isNotBlank(cdSerie)) {
            throw new ParerUserError(
                    "\u00C8 stato indicato che il codice serie sia definito sul registro o sulla tipologia di unit\u00E0 documentaria; il relativo campo non deve essere valorizzato");
        } else if (tiRglCdSerie.equals(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name())
                && StringUtils.isBlank(cdSerie)) {
            throw new ParerUserError(
                    "\u00C8 stato indicato che il codice serie sia definito sul modello; occorre valorizzare il relativo campo");
        } else if (StringUtils.isNotBlank(cdSerie)) {
            if (!CD_SERIE_PATTERN.matcher(cdSerie).matches()) {
                throw new ParerUserError("Caratteri consentiti per il codice serie: lettere, numeri,.,-,_,: ");
            }
        }
        String tiRglDsSerie = modelliTipiSerieDetail.getTi_rgl_ds_serie().parse();
        String dsSerie = modelliTipiSerieDetail.getDs_serie_da_creare().parse();
        if (tiRglDsSerie.equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_TIPO_UD_REG.name())
                && StringUtils.isNotBlank(dsSerie)) {
            throw new ParerUserError(
                    "\u00C8 stato indicato che la descrizione della serie sia definita sul registro o sulla tipologia di unit\u00E0 documentaria; il relativo campo non deve essere valorizzato");
        } else if (tiRglDsSerie.equals(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name())
                && StringUtils.isBlank(dsSerie)) {
            throw new ParerUserError(
                    "\u00C8 stato indicato che la descrizione della  serie sia definita sul modello; occorre valorizzare il relativo campo");
        }
        String tiRglAnniConserv = modelliTipiSerieDetail.getTi_rgl_anni_conserv().parse();
        BigDecimal niAnniConserv = modelliTipiSerieDetail.getNi_anni_conserv().parse();
        String conservUnlimited = modelliTipiSerieDetail.getConserv_unlimited().parse();
        if (tiRglAnniConserv.equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_REG.name())
                && (StringUtils.isNotBlank(conservUnlimited) || niAnniConserv != null)) {
            throw new ParerUserError(
                    "\u00C8 stato indicato che gli anni di conservazione siano definiti sul registro o sulla tipologia di unit\u00E0 documentaria; i relativi campi non devono essere valorizzati");
        } else if (tiRglAnniConserv.equals(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name())) {
            if (StringUtils.isBlank(conservUnlimited) && niAnniConserv == null) {
                throw new ParerUserError(
                        "\u00C8 stato indicato che la descrizione della  serie sia definita sul modello; occorre valorizzare il relativo campo");
            } else {
                boolean error = false;
                if (conservUnlimited != null && niAnniConserv != null) {
                    if ((conservUnlimited.equals("1") && !niAnniConserv.equals(new BigDecimal(9999)))
                            || ((conservUnlimited.equals("0") && niAnniConserv.equals(new BigDecimal(9999))))) {
                        error = true;
                    }
                }
                if ((niAnniConserv == null && StringUtils.isBlank(conservUnlimited))
                        || ((conservUnlimited != null && conservUnlimited.equals("0") && niAnniConserv == null))) {
                    error = true;
                }
                if (error) {
                    throw new ParerUserError("'Anni di conservazione' \u00E8 alternativo a 'Conservazione illimitata'");
                }
            }
        }
        boolean flCreaSerieAutom = modelliTipiSerieDetail.getFl_crea_autom().parse().equals("1");
        String tiRglRangeAnniCreaAutom = modelliTipiSerieDetail.getTi_rgl_range_anni_crea_autom().parse();
        BigDecimal aaIniCreaAutom = modelliTipiSerieDetail.getAa_ini_crea_autom().parse();
        BigDecimal aaFinCreaAutom = modelliTipiSerieDetail.getAa_fin_crea_autom().parse();
        if (flCreaSerieAutom) {
            if (tiRglRangeAnniCreaAutom.equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_REG.name())
                    && (aaIniCreaAutom != null || aaFinCreaAutom != null)) {
                throw new ParerUserError(
                        "\u00C8 stato indicato che gli anni di creazione in automatico della serie siano definiti sul registro o sulla tipologia di unit\u00E0 documentaria; i relativi campi non devono essere valorizzati");
            } else if (tiRglRangeAnniCreaAutom
                    .equals(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name())) {
                if (aaIniCreaAutom == null && aaFinCreaAutom == null) {
                    throw new ParerUserError(
                            "\u00C8 stato indicato che gli anni di creazione in automatico della serie siano definiti sul modello; occorre valorizzare i relativi campi");
                } else if (aaIniCreaAutom == null) {
                    throw new ParerUserError("L'anno di inizio periodo deve essere valorizzato");
                } else if (aaFinCreaAutom != null && aaIniCreaAutom.compareTo(aaFinCreaAutom) > 0) {
                    throw new ParerUserError(
                            "L'anno di fine periodo non pu\u00F2 essere minore dell'anno di inizio <br/>");
                }
            }
            String ggCreaAutom = modelliTipiSerieDetail.getGg_crea_autom().parse();
            if (StringUtils.isNotBlank(ggCreaAutom)) {
                Pattern pattern = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])\\/(0?[1-9]|1[012])");
                Matcher matcher = pattern.matcher(ggCreaAutom);
                if (!matcher.matches()) {
                    throw new ParerUserError(
                            "Il formato campo 'Giorno di creazione in automatico della serie' non \u00E8 corretto. Da valorizzare nel formato gg/mm <br/>");
                } else {
                    String[] ggMM = ggCreaAutom.split("/");
                    if (ggMM != null) {
                        int day = Integer.parseInt(ggMM[0]);
                        int month = Integer.parseInt(ggMM[1]);
                        try {
                            Calendar cal = Calendar.getInstance();
                            cal.setLenient(false);
                            cal.set(2016, month - 1, day);

                            Date date = cal.getTime();
                            logger.debug("Date " + date);
                        } catch (IllegalArgumentException ex) {
                            throw new ParerUserError(
                                    "Il valore inserito nel campo 'Giorno di creazione in automatico della serie' non \u00E8 un giorno corretto");
                        }
                    }
                }
            } else {
                throw new ParerUserError(
                        "Deve essere inserito il valore del campo 'Giorno di creazione in automatico della serie'");
            }
        }
    }

    @Override
    public void updateModelliTipiSerieList() throws EMFError {
        getForm().getModelliTipiSerieDetail().setEditMode();
        getForm().getModelliTipiSerieList().setStatus(Status.update);

        ActionUtils utile = new ActionUtils();
        utile.triggerAmbienteGenerico(getForm().getModelliTipiSerieDetail(), getUser().getIdUtente(), Boolean.FALSE);

        forwardToPublisher(Application.Publisher.MODELLO_SERIE_DETAIL);
    }

    @Override
    public void deleteModelliTipiSerieList() throws EMFError {
        DecModelloTipoSerieRowBean currentRow = (DecModelloTipoSerieRowBean) getForm().getModelliTipiSerieList()
                .getTable().getCurrentRow();
        BigDecimal idModelloTipoSerie = currentRow.getIdModelloTipoSerie();
        int riga = getForm().getModelliTipiSerieList().getTable().getCurrentRowIndex();
        // Eseguo giusto un controllo per verificare che io stia prendendo la riga giusta se sono nel dettaglio
        if (getLastPublisher().equals(Application.Publisher.MODELLO_SERIE_DETAIL)) {
            if (!idModelloTipoSerie.equals(getForm().getModelliTipiSerieDetail().getId_modello_tipo_serie().parse())) {
                getMessageBox().addError("Eccezione imprevista nell'eliminazione del modello");
            }
        }

        if (!getMessageBox().hasError() && idModelloTipoSerie != null) {
            try {
                if (tipoSerieEjb.existDecTipoSerieForIdModello(idModelloTipoSerie)) {
                    getMessageBox().addError(
                            "Il modello di tipo serie \u00E8 gi\u00E0 stato utilizzato per gestire delle tipologie di serie: eliminazione non consentita");
                } else if (tipoUnitaDocEjb.existDecTipoUnitaDocForIdModello(idModelloTipoSerie)) {
                    getMessageBox().addError(
                            "Il modello di tipo serie \u00E8 associato a una o pi\u00F9 tipologie di unit\u00E0 documentarie: eliminazione non consentita");
                } else if (registroEjb.existDecRegistroUnitaDocForIdModello(idModelloTipoSerie)) {
                    getMessageBox().addError(
                            "Il modello di tipo serie \u00E8 associato a uno o pi\u00F9 registri: eliminazione non consentita");
                } else {
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null,
                                    null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.MODELLO_SERIE_DETAIL)) {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
                    } else {
                        param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                                getForm().getModelliTipiSerieList()));
                    }
                    modelliSerieEjb.deleteDecModelloTipoSerie(param, idModelloTipoSerie);
                    getForm().getModelliTipiSerieList().getTable().remove(riga);

                    getMessageBox().addInfo("Modello eliminato con successo");
                    getMessageBox().setViewMode(ViewMode.plain);
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError("Il modello non pu\u00F2 essere eliminato: " + ex.getDescription());
            }
        }
        if (!getMessageBox().hasError() && getLastPublisher().equals(Application.Publisher.MODELLO_SERIE_DETAIL)) {
            goBack();
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Salvataggio/Modifica/Elimina note modello di tipo serie">
    private void saveNotaModelloTipoSerie() throws EMFError {
        logger.debug("Eseguo i controlli sul salvataggio della nota del modello");
        BigDecimal idModelloTipoSerie = getForm().getModelliTipiSerieDetail().getId_modello_tipo_serie().parse();
        if (getForm().getNoteModelloTipoSerieDetail().postAndValidate(getRequest(), getMessageBox())) {
            BigDecimal idTipoNotaSerie = getForm().getNoteModelloTipoSerieDetail().getId_tipo_nota_serie().parse();
            BigDecimal pgNota = getForm().getNoteModelloTipoSerieDetail().getPg_nota_tipo_serie().parse();
            String dsNota = getForm().getNoteModelloTipoSerieDetail().getDs_nota_tipo_serie().parse();
            Date dtNota = new Date(getForm().getNoteModelloTipoSerieDetail().getDt_nota_tipo_serie().parse().getTime());
            try {
                int rowIndex = getForm().getNoteModelloTipoSerieList().getTable().getCurrentRowIndex();
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                                CostantiDB.TipoAplVGetValAppart.APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                if (getForm().getNoteModelloTipoSerieList().getStatus().equals(Status.insert)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                    BigDecimal idNota = modelliSerieEjb.saveNotaModelloTipoSerie(param, getUser().getIdUtente(),
                            idModelloTipoSerie, idTipoNotaSerie, pgNota, dsNota, dtNota);
                    if (idNota != null) {
                        getForm().getNoteModelloTipoSerieDetail().getId_nota_modello_tipo_serie()
                                .setValue(idNota.toPlainString());
                    }
                    rowIndex = getForm().getNoteModelloTipoSerieList().getTable().size();
                } else if (getForm().getNoteModelloTipoSerieList().getStatus().equals(Status.update)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                    BigDecimal idNota = getForm().getNoteModelloTipoSerieDetail().getId_nota_modello_tipo_serie()
                            .parse();
                    modelliSerieEjb.saveNotaModelloTipoSerie(param, idNota, dsNota, getUser().getIdUtente(), dtNota);
                }
                DecNotaModelloTipoSerieRowBean notaRowBean = new DecNotaModelloTipoSerieRowBean();
                getForm().getNoteModelloTipoSerieDetail().copyToBean(notaRowBean);
                if (getForm().getNoteModelloTipoSerieList().getTable() != null) {
                    int pageSize = getForm().getNoteModelloTipoSerieList().getTable().getPageSize();
                    getForm().getNoteModelloTipoSerieList().getTable().last();
                    getForm().getNoteModelloTipoSerieList().getTable().add(notaRowBean);
                    getForm().getNoteModelloTipoSerieList().getTable().setPageSize(pageSize);
                } else {
                    DecNotaModelloTipoSerieTableBean noteTb = modelliSerieEjb
                            .getDecNotaModelloTipoSerieTableBean(idModelloTipoSerie);
                    getForm().getNoteModelloTipoSerieList().setTable(noteTb);
                    getForm().getNoteModelloTipoSerieList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    getForm().getNoteModelloTipoSerieList().getTable().setCurrentRowIndex(rowIndex);
                }

                getMessageBox().addInfo("Elemento di descrizione salvato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            } catch (ParerUserError ex) {
                getMessageBox()
                        .addError("L'elemento di descrizione non pu\u00F2 essere salvato: " + ex.getDescription());
            }
            // Salvataggio nota
            if (!getMessageBox().hasError()) {
                getForm().getNoteModelloTipoSerieList().setStatus(Status.view);
                getForm().getNoteModelloTipoSerieDetail().setStatus(Status.view);
                getForm().getNoteModelloTipoSerieDetail().setViewMode();
            }
            forwardToPublisher(Application.Publisher.NOTA_MODELLO_SERIE_DETAIL);
        }
    }

    @Override
    public JSONObject triggerNoteModelloTipoSerieDetailId_tipo_nota_serieOnTrigger() throws EMFError {
        getForm().getNoteModelloTipoSerieDetail().post(getRequest());
        BigDecimal idTipoNotaSerie = getForm().getNoteModelloTipoSerieDetail().getId_tipo_nota_serie().parse();
        BigDecimal idModelloTipoSerie = getForm().getModelliTipiSerieDetail().getId_modello_tipo_serie().parse();

        if (idTipoNotaSerie != null) {
            BigDecimal lastPgNota = modelliSerieEjb.getMaxPgNotaModelloTipoSerie(idModelloTipoSerie, idTipoNotaSerie);
            String nextPg = lastPgNota.add(BigDecimal.ONE).toPlainString();
            getForm().getNoteModelloTipoSerieDetail().getPg_nota_tipo_serie().setValue(nextPg);
        } else {
            getForm().getNoteModelloTipoSerieDetail().getPg_nota_tipo_serie().setValue(BigDecimal.ONE.toPlainString());
        }

        return getForm().getNoteModelloTipoSerieDetail().asJSON();
    }

    @Override
    public void deleteNoteModelloTipoSerieList() throws EMFError {
        DecNotaModelloTipoSerieRowBean currentRow = (DecNotaModelloTipoSerieRowBean) getForm()
                .getNoteModelloTipoSerieList().getTable().getCurrentRow();
        BigDecimal idNota = currentRow.getIdNotaModelloTipoSerie();
        int riga = getForm().getNoteModelloTipoSerieList().getTable().getCurrentRowIndex();
        // Eseguo giusto un controllo per verificare che io stia prendendo la riga giusta se sono nel dettaglio
        if (getLastPublisher().equals(Application.Publisher.NOTA_MODELLO_SERIE_DETAIL)) {
            if (!idNota.equals(getForm().getNoteModelloTipoSerieDetail().getId_nota_modello_tipo_serie().parse())) {
                getMessageBox().addError("Eccezione imprevista nell'eliminazione dell'elemento di descrizione");
            }
        }

        if (!getMessageBox().hasError() && idNota != null) {
            try {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                                CostantiDB.TipoAplVGetValAppart.APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.NOTA_MODELLO_SERIE_DETAIL)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
                } else {
                    param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                            getForm().getNoteModelloTipoSerieList()));
                }
                modelliSerieEjb.deleteNotaModelloTipoSerie(param, idNota);
                getForm().getNoteModelloTipoSerieList().getTable().remove(riga);

                getMessageBox().addInfo("Elemento di descrizione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            } catch (ParerUserError ex) {
                getMessageBox()
                        .addError("L'elemento di descrizione non pu\u00F2 essere eliminato: " + ex.getDescription());
            }
        }
        if (!getMessageBox().hasError() && getLastPublisher().equals(Application.Publisher.NOTA_MODELLO_SERIE_DETAIL)) {
            goBack();
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void updateNoteModelloTipoSerieList() throws EMFError {
        getForm().getNoteModelloTipoSerieDetail().getDs_nota_tipo_serie().setEditMode();
        DateFormat formato = new SimpleDateFormat(WebConstants.DATE_FORMAT_HOUR_MINUTE_TYPE);
        getForm().getNoteModelloTipoSerieDetail().getDt_nota_tipo_serie()
                .setValue(formato.format(Calendar.getInstance().getTime()));

        getForm().getNoteModelloTipoSerieList().setStatus(Status.update);
        getForm().getNoteModelloTipoSerieDetail().setStatus(Status.update);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Salvataggio/Modifica/Elimina regole acquisizione modello di tipo
    // serie">
    private void editRegoleAcquisizione(Status status) throws EMFError, ParerUserError {
        getForm().getRegoleAcquisizioneList().setStatus(status);
        getForm().getRegoleAcquisizioneDetail().setStatus(status);
        BigDecimal idModelloTipoSerie = getForm().getModelliTipiSerieDetail().getId_modello_tipo_serie().parse();
        if (idModelloTipoSerie != null) {
            getForm().getDatiProfiloList().setTable(modelliSerieEjb.getDecModelloCampoInpUdTableBean(idModelloTipoSerie,
                    CostantiDB.TipoCampo.DATO_PROFILO.name()));
            getForm().getDatiProfiloList().getTable().setPageSize(300);
            getForm().getDatiProfiloList().getPg_ord_campo_dato_profilo().setHidden(false);
            getForm().getDatiProfiloList().getPg_ord_campo_dato_profilo().setEditMode();
            getForm().getDatiProfiloList().getFl_selezionato().setHidden(true);
            getForm().getDatiProfiloList().getFl_selezionato().setViewMode();

            getForm().getDatiSpecTipoDocSection().setHidden(true);
            getForm().getDatiSpecTipoUdSection().setHidden(true);
            BigDecimal idTipoUnitaDoc = getForm().getModelliTipiSerieDetail().getId_tipo_unita_doc_dati_spec().parse();
            if (idTipoUnitaDoc != null) {
                getForm().getAttributiTipoUnitaDocList().getTi_trasform_campo_unita_doc()
                        .setDecodeMap(ComboGetter.getMappaTipoDiTrasformatoreInp());
                getForm().getAttributiTipoUnitaDocList()
                        .setTable(modelliSerieEjb.getDecModelloCampoInpUdDatiSpecTableBean(idModelloTipoSerie,
                                idTipoUnitaDoc, CostantiDB.TipoCampo.DATO_SPEC_UNI_DOC.name()));
                getForm().getAttributiTipoUnitaDocList().getTable().setPageSize(300);
                getForm().getAttributiTipoUnitaDocList().getFl_tipo_ud_selezionato().setHidden(true);
                getForm().getAttributiTipoUnitaDocList().getFl_tipo_ud_selezionato().setViewMode();
                getForm().getAttributiTipoUnitaDocList().getPg_ord_campo_unita_doc().setHidden(false);
                getForm().getAttributiTipoUnitaDocList().getPg_ord_campo_unita_doc().setEditMode();
                getForm().getAttributiTipoUnitaDocList().getTi_trasform_campo_unita_doc().setEditMode();
                getForm().getDatiSpecTipoUdSection().setHidden(false);
            }
            BigDecimal idTipoDoc = getForm().getModelliTipiSerieDetail().getId_tipo_doc_dati_spec().parse();
            if (idTipoDoc != null) {
                getForm().getAttributiTipoDocList().getTi_trasform_campo_doc()
                        .setDecodeMap(ComboGetter.getMappaTipoDiTrasformatoreInp());
                getForm().getAttributiTipoDocList().setTable(modelliSerieEjb.getDecModelloCampoInpUdDatiSpecTableBean(
                        idModelloTipoSerie, idTipoDoc, CostantiDB.TipoCampo.DATO_SPEC_DOC_PRINC.name()));
                getForm().getAttributiTipoDocList().getTable().setPageSize(300);
                getForm().getAttributiTipoDocList().getFl_tipo_doc_selezionato().setHidden(true);
                getForm().getAttributiTipoDocList().getFl_tipo_doc_selezionato().setViewMode();
                getForm().getAttributiTipoDocList().getPg_ord_campo_doc().setHidden(false);
                getForm().getAttributiTipoDocList().getPg_ord_campo_doc().setEditMode();
                getForm().getAttributiTipoDocList().getTi_trasform_campo_doc().setEditMode();
                getForm().getDatiSpecTipoDocSection().setHidden(false);
            }
        }
    }

    private void saveRegoleModelloAcquisizione() throws EMFError {
        logger.debug("Eseguo i controlli sul salvataggio della regola del modello");
        BigDecimal idModelloTipoSerie = getForm().getModelliTipiSerieDetail().getId_modello_tipo_serie().parse();
        String[] pgOrdDatiProfilo = getRequest()
                .getParameterValues(getForm().getDatiProfiloList().getPg_ord_campo_dato_profilo().getName());
        String[] pgOrdDatiSpecUniDoc = getRequest()
                .getParameterValues(getForm().getAttributiTipoUnitaDocList().getPg_ord_campo_unita_doc().getName());
        String[] tiTrasformDatiSpecUniDoc = getRequest().getParameterValues(
                getForm().getAttributiTipoUnitaDocList().getTi_trasform_campo_unita_doc().getName());
        String[] pgOrdDatiSpecDoc = getRequest()
                .getParameterValues(getForm().getAttributiTipoDocList().getPg_ord_campo_doc().getName());
        String[] tiTrasformDatiSpecDoc = getRequest()
                .getParameterValues(getForm().getAttributiTipoDocList().getTi_trasform_campo_doc().getName());

        try {
            DecModelloCampoInpUdTableBean datiProfiloTable = (DecModelloCampoInpUdTableBean) getForm()
                    .getDatiProfiloList().getTable();
            DecModelloCampoInpUdTableBean datiSpecTipoUdTable = (DecModelloCampoInpUdTableBean) getForm()
                    .getAttributiTipoUnitaDocList().getTable();
            DecModelloCampoInpUdTableBean datiSpecTipoDocTable = (DecModelloCampoInpUdTableBean) getForm()
                    .getAttributiTipoDocList().getTable();
            DecModelloCampoInpUdTableBean datiCompilati = new DecModelloCampoInpUdTableBean();
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
                        configHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                                CostantiDB.TipoAplVGetValAppart.APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                /* Se la lista è vuota mi metto in modalita' inserimento altrimenti in modifica */
                if (getForm().getRegoleAcquisizioneList().getTable().isEmpty()) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                } else {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                }
                modelliSerieEjb.saveDecModelloCampoInpUd(param, idModelloTipoSerie, datiCompilati);
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

    private void checkRegoleAcquisizione(DecModelloCampoInpUdTableBean table, String[] pgOrdTable,
            String[] tiTrasformTable, DecModelloCampoInpUdTableBean fillingTable, Set<String> pgOrds, String tiCampo) {
        // Scorro ogni record
        for (int index = 0; index < pgOrdTable.length; index++) {
            String pgOrdCampo = pgOrdTable[index];
            DecModelloCampoInpUdRowBean row = table.getRow(index);
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

    @Override
    public void deleteRegoleAcquisizioneList() throws EMFError {
        DecModelloCampoInpUdRowBean currentRow = (DecModelloCampoInpUdRowBean) getForm().getRegoleAcquisizioneList()
                .getTable().getCurrentRow();
        BigDecimal idModelloCampoInpUd = currentRow.getIdModelloCampoInpUd();
        int riga = getForm().getRegoleAcquisizioneList().getTable().getCurrentRowIndex();

        if (!getMessageBox().hasError() && idModelloCampoInpUd != null) {
            try {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                                CostantiDB.TipoAplVGetValAppart.APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                        SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getRegoleAcquisizioneList()));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                modelliSerieEjb.deleteDecModelloCampoInpUd(param, idModelloCampoInpUd);
                getForm().getRegoleAcquisizioneList().getTable().remove(riga);

                getMessageBox().addInfo("Regola di acquisizione eliminata con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            } catch (ParerUserError ex) {
                getMessageBox()
                        .addError("La regola di acquisizione non pu\u00F2 essere eliminata: " + ex.getDescription());
            }
        }
        forwardToPublisher(getLastPublisher());
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Salvataggio/Modifica/Elimina regole filtraggio doc principale modello
    // di tipo serie">
    private void initRegoleFiltraggioDetail(BigDecimal idModelloTipoSerie) throws EMFError, ParerUserError {
        if (idModelloTipoSerie != null) {
            BaseTableInterface ambienteTableBean = ambienteEjb.getAmbientiAbilitatiPerStrut(getUser().getIdUtente(),
                    configHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC));
            ambienteTableBean.addSortingRule("nm_ambiente", SortingRule.ASC);
            ambienteTableBean.sort();
            DecModelloFiltroTiDocTableBean decModelloFiltroTiDocTableBean = modelliSerieEjb
                    .getDecModelloFiltroTiDocTableBean(idModelloTipoSerie);
            List<Object> toList = decModelloFiltroTiDocTableBean
                    .toList(DecModelloFiltroTiDocTableDescriptor.COL_NM_TIPO_DOC);
            getForm().getRegoleFiltraggioDetail().getNm_tipo_doc_dati_spec().setDecodeMap(
                    DecodeMap.Factory.newInstance(decModelloFiltroTiDocTableBean, "nm_tipo_doc", "nm_tipo_doc"));
            getForm().getRegoleFiltraggioDetail().getNm_tipo_doc_dati_spec()
                    .setValues(toList.toArray(new String[toList.size()]));
            getForm().getRegoleFiltraggioDetail().getId_ambiente()
                    .setDecodeMap(DecodeMap.Factory.newInstance(ambienteTableBean, "id_ambiente", "nm_ambiente"));
            getForm().getRegoleFiltraggioDetail().getId_ambiente()
                    .setValue(getForm().getModelliTipiSerieDetail().getId_ambiente().parse().toPlainString());
            getForm().getRegoleFiltraggioDetail().getNm_ambiente()
                    .setValue(getForm().getModelliTipiSerieDetail().getId_ambiente().getDecodedValue());

            ActionUtils utile = new ActionUtils();
            utile.triggerAmbienteGenerico(getForm().getRegoleFiltraggioDetail(), getUser().getIdUtente(),
                    Boolean.FALSE);

            getForm().getRegoleFiltraggioDetail().getId_strut().clear();
            getForm().getRegoleFiltraggioDetail().getId_tipo_doc_dati_spec_combo().clear();
        }
    }

    private void saveRegoleModelloFiltraggio() throws EMFError {
        logger.debug("Eseguo i controlli sul salvataggio della regola del modello");
        BigDecimal idModelloTipoSerie = getForm().getModelliTipiSerieDetail().getId_modello_tipo_serie().parse();
        getForm().getRegoleFiltraggioDetail().getNm_tipo_doc_dati_spec().post(getRequest());
        List<String> nmTipiDocs = getForm().getRegoleFiltraggioDetail().getNm_tipo_doc_dati_spec().parse();
        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            if (getForm().getRegoleFiltraggioDetail().getStatus().equals(Status.insert)) {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
            } else {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
            }
            modelliSerieEjb.saveDecModelloFiltroTiDoc(param, idModelloTipoSerie, nmTipiDocs);
            getMessageBox().addInfo("Regola salvata con successo");
            getMessageBox().setViewMode(ViewMode.plain);
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        if (getMessageBox().hasError()) {
            forwardToPublisher(getLastPublisher());
        } else {
            goBack();
        }
    }

    @Override
    public void deleteRegoleFiltraggioList() throws EMFError {
        // Elimina tutte le regole
        BigDecimal idModelloTipoSerie = getForm().getModelliTipiSerieDetail().getId_modello_tipo_serie().parse();
        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                    SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getRegoleFiltraggioList()));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            modelliSerieEjb.deleteDecModelloFiltroTiDoc(param, idModelloTipoSerie);
            getMessageBox().addInfo("Regole eliminate con successo");
            getMessageBox().setViewMode(ViewMode.plain);

            getForm().getRegoleFiltraggioList().getTable().clear();
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void updateRegoleFiltraggioList() throws EMFError {
        getForm().getRegoleFiltraggioDetail().setEditMode();
        getForm().getRegoleFiltraggioDetail().setStatus(Status.update);
    }

    @Override
    public void updateRegoleFiltraggioDetail() throws EMFError {
        updateRegoleFiltraggioList();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Salvataggio/Modifica/Elimina dati specifici modello di tipo serie">
    private void saveDatiSpecificiModello() throws EMFError {
        logger.debug("Eseguo i controlli sul salvataggio dei dati specifici del modello");
        BigDecimal idModelloTipoSerie = getForm().getModelliTipiSerieDetail().getId_modello_tipo_serie().parse();
        String[] tiOper = getRequest().getParameterValues(getForm().getFiltriDatiSpecList().getTi_oper().getName());
        String[] dlValore = getRequest().getParameterValues(getForm().getFiltriDatiSpecList().getDl_valore().getName());

        try {
            DecModelloFiltroSelUdattbTableBean datiSpecTable = (DecModelloFiltroSelUdattbTableBean) getForm()
                    .getFiltriDatiSpecList().getTable();
            DecModelloFiltroSelUdattbTableBean datiCompilati = new DecModelloFiltroSelUdattbTableBean();
            datiCompilati.addSortingRule("nm_filtro", SortingRule.ASC);
            UnitaDocumentarieValidator validator = new UnitaDocumentarieValidator(getMessageBox());
            // Scorro ogni record
            if (tiOper != null) {
                for (int index = 0; index < tiOper.length; index++) {
                    String tiOperCampo = tiOper[index];
                    String dlValoreCampo = dlValore[index];
                    DecModelloFiltroSelUdattbRowBean row = datiSpecTable.getRow(index);
                    validator.checkDatoSpecifico(tiOperCampo, dlValoreCampo);
                    row.setTiOper(tiOperCampo);
                    row.setDlValore(dlValoreCampo);

                    if (!getMessageBox().hasError()
                            && (StringUtils.isNotBlank(tiOperCampo) || StringUtils.isNotBlank(dlValoreCampo))) {
                        BaseTableInterface<BaseRowInterface> datiSpec = (BaseTableInterface<BaseRowInterface>) row
                                .getObject(Values.SUB_LIST);
                        if (datiSpec != null && !datiSpec.isEmpty()) {
                            for (BaseRowInterface rowDatiSpec : datiSpec) {
                                row.setString("ti_filtro", rowDatiSpec.getString("ti_filtro"));
                                datiCompilati.add(row);
                            }
                        } else {
                            datiCompilati.add(row);
                        }
                    }
                }

                if (!getMessageBox().hasError()) {
                    datiCompilati.sort();
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null,
                                    null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    if (getForm().getDatiSpecList().getTable().isEmpty()) {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                    } else {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                    }
                    modelliSerieEjb.saveDecModelloFiltroSelUdattb(param, idModelloTipoSerie, datiCompilati);

                    getMessageBox().addInfo("Filtri salvati con successo");
                    getMessageBox().setViewMode(ViewMode.plain);
                }
            } else {
                getMessageBox().addInfo("Nessuna modifica apportata in quando non erano presenti dati specifici");
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

    @Override
    public void deleteDatiSpecList() throws EMFError {
        DecModelloFiltroSelUdattbRowBean currentRow = (DecModelloFiltroSelUdattbRowBean) getForm().getDatiSpecList()
                .getTable().getCurrentRow();
        BigDecimal idModelloFiltroSelUdattb = currentRow.getIdModelloFiltroSelUdattb();
        int riga = getForm().getDatiSpecList().getTable().getCurrentRowIndex();

        if (!getMessageBox().hasError() && idModelloFiltroSelUdattb != null) {
            try {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                                CostantiDB.TipoAplVGetValAppart.APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getDatiSpecList()));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                modelliSerieEjb.deleteDecModelloFiltroSelUdattb(param, idModelloFiltroSelUdattb);
                getForm().getDatiSpecList().getTable().remove(riga);

                getMessageBox().addInfo("Filtro sui dati specifici eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            } catch (ParerUserError ex) {
                getMessageBox()
                        .addError("Il filtro sui dati specifici non pu\u00F2 essere eliminato: " + ex.getDescription());
            }
        }
        forwardToPublisher(getLastPublisher());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Salvataggio/Modifica/Elimina regole rappresentazione modello di tipo
    // serie">
    private void editRegoleRappresentazione(Status status) throws EMFError, ParerUserError {
        getForm().getRegoleRapprList().setStatus(status);
        getForm().getRegoleRapprDetail().setStatus(status);
        DecModelloOutSelUdTableBean table = (DecModelloOutSelUdTableBean) getForm().getRegoleRapprList().getTable();
        getForm().getRegoleRapprDetail().clear();
        BigDecimal idModelloOutSelUd = null;
        BigDecimal idModelloTipoSerie = getForm().getModelliTipiSerieDetail().getId_modello_tipo_serie().parse();
        getForm().getRegoleRapprDetail().setEditMode();
        getForm().getRegoleRapprDetail().getId_modello_tipo_serie().setValue(idModelloTipoSerie.toPlainString());
        if (status.equals(Status.update)) {
            DecModelloOutSelUdRowBean currentRow = (DecModelloOutSelUdRowBean) getForm().getRegoleRapprList().getTable()
                    .getCurrentRow();
            idModelloOutSelUd = currentRow.getIdModelloOutSelUd();
            getForm().getRegoleRapprDetail().getTi_out()
                    .setDecodeMap(DecodeMap.Factory.newInstance(table, "ti_out", "desc_ti_out"));
            getForm().getRegoleRapprDetail().copyFromBean(currentRow);
            getForm().getRegoleRapprDetail().getTi_out().setViewMode();
        } else if (status.equals(Status.insert)) {
            BaseTable regoleMancanti = new BaseTable();
            List<Object> regole = table.toList("ti_out");
            for (CostantiDB.TipoDiRappresentazione tipoRappr : CostantiDB.TipoDiRappresentazione
                    .getComboTipoDiRappresentazione()) {
                if (!regole.contains(tipoRappr.name())) {
                    BaseRow regolaMancante = new BaseRow();
                    regolaMancante.setString("ti_out", tipoRappr.name());
                    regolaMancante.setString("desc_ti_out", tipoRappr.toString());
                    regoleMancanti.add(regolaMancante);
                }
            }
            getForm().getRegoleRapprDetail().getTi_out()
                    .setDecodeMap(DecodeMap.Factory.newInstance(regoleMancanti, "ti_out", "desc_ti_out"));
        }

        getForm().getDatiProfiloList().setTable(modelliSerieEjb.getDecModelloCampoOutSelUdTableBean(idModelloOutSelUd,
                CostantiDB.TipoCampo.DATO_PROFILO.name()));
        getForm().getDatiProfiloList().getTable().setPageSize(300);
        getForm().getDatiProfiloList().getPg_ord_campo_dato_profilo().setHidden(true);
        getForm().getDatiProfiloList().getPg_ord_campo_dato_profilo().setViewMode();
        getForm().getDatiProfiloList().getFl_selezionato().setEditMode();
        getForm().getDatiProfiloList().getFl_selezionato().setHidden(false);

        getForm().getDatiSpecTipoDocSection().setHidden(true);
        getForm().getDatiSpecTipoUdSection().setHidden(true);
        BigDecimal idTipoUnitaDoc = getForm().getModelliTipiSerieDetail().getId_tipo_unita_doc_dati_spec().parse();
        if (idTipoUnitaDoc != null) {
            getForm().getAttributiTipoUnitaDocList().getTi_trasform_campo_unita_doc()
                    .setDecodeMap(ComboGetter.getMappaTipoDiTrasformatore());
            getForm().getAttributiTipoUnitaDocList().setTable(modelliSerieEjb.getDecModelloCampoOutSelUdTableBean(
                    idModelloOutSelUd, idTipoUnitaDoc, CostantiDB.TipoCampo.DATO_SPEC_UNI_DOC.name()));
            getForm().getAttributiTipoUnitaDocList().getTable().setPageSize(300);
            getForm().getAttributiTipoUnitaDocList().getPg_ord_campo_unita_doc().setHidden(true);
            getForm().getAttributiTipoUnitaDocList().getPg_ord_campo_unita_doc().setViewMode();
            getForm().getAttributiTipoUnitaDocList().getFl_tipo_ud_selezionato().setHidden(false);
            getForm().getAttributiTipoUnitaDocList().getFl_tipo_ud_selezionato().setEditMode();
            getForm().getAttributiTipoUnitaDocList().getTi_trasform_campo_unita_doc().setEditMode();
            getForm().getDatiSpecTipoUdSection().setHidden(false);
        }
        BigDecimal idTipoDoc = getForm().getModelliTipiSerieDetail().getId_tipo_doc_dati_spec().parse();
        if (idTipoDoc != null) {
            getForm().getAttributiTipoDocList().getTi_trasform_campo_doc()
                    .setDecodeMap(ComboGetter.getMappaTipoDiTrasformatore());
            getForm().getAttributiTipoDocList().setTable(modelliSerieEjb.getDecModelloCampoOutSelUdTableBean(
                    idModelloOutSelUd, idTipoDoc, CostantiDB.TipoCampo.DATO_SPEC_DOC_PRINC.name()));
            getForm().getAttributiTipoDocList().getTable().setPageSize(300);
            getForm().getAttributiTipoDocList().getPg_ord_campo_doc().setHidden(true);
            getForm().getAttributiTipoDocList().getPg_ord_campo_doc().setViewMode();
            getForm().getAttributiTipoDocList().getFl_tipo_doc_selezionato().setHidden(false);
            getForm().getAttributiTipoDocList().getFl_tipo_doc_selezionato().setEditMode();
            getForm().getAttributiTipoDocList().getTi_trasform_campo_doc().setEditMode();
            getForm().getDatiSpecTipoDocSection().setHidden(false);
        }
    }

    private void saveRegoleModelloRappresentazione() throws EMFError {
        logger.debug("Eseguo i controlli sul salvataggio della regola del modello");
        BigDecimal idModelloTipoSerie = getForm().getRegoleRapprDetail().getId_modello_tipo_serie().parse();
        BigDecimal idModelloOutSelUd = getForm().getRegoleRapprDetail().getId_modello_out_sel_ud().parse();
        if (getForm().getRegoleRapprDetail().postAndValidate(getRequest(), getMessageBox())) {
            String tiOut = getForm().getRegoleRapprDetail().getTi_out().parse();
            String dlFormatoOut = getForm().getRegoleRapprDetail().getDl_formato_out().parse();
            dlFormatoOut = StringUtils.isBlank(dlFormatoOut) ? StringUtils.EMPTY : dlFormatoOut;
            String[] flSelezionatoDatiProfilo = ArrayUtils.nullToEmpty(
                    getRequest().getParameterValues(getForm().getDatiProfiloList().getFl_selezionato().getName()));
            String[] flSelezionatoDatiSpecUniDoc = ArrayUtils.nullToEmpty(getRequest().getParameterValues(
                    getForm().getAttributiTipoUnitaDocList().getFl_tipo_ud_selezionato().getName()));
            String[] tiTrasformDatiSpecUniDoc = ArrayUtils.nullToEmpty(getRequest().getParameterValues(
                    getForm().getAttributiTipoUnitaDocList().getTi_trasform_campo_unita_doc().getName()));
            String[] flSelezionatoDatiSpecDoc = ArrayUtils.nullToEmpty(getRequest()
                    .getParameterValues(getForm().getAttributiTipoDocList().getFl_tipo_doc_selezionato().getName()));
            String[] tiTrasformDatiSpecDoc = ArrayUtils.nullToEmpty(getRequest()
                    .getParameterValues(getForm().getAttributiTipoDocList().getTi_trasform_campo_doc().getName()));

            try {
                DecModelloCampoOutSelUdTableBean datiProfiloTable = (DecModelloCampoOutSelUdTableBean) getForm()
                        .getDatiProfiloList().getTable();
                clearTable(datiProfiloTable);
                DecModelloCampoOutSelUdTableBean datiSpecTipoUdTable = (DecModelloCampoOutSelUdTableBean) getForm()
                        .getAttributiTipoUnitaDocList().getTable();
                clearTable(datiSpecTipoUdTable);
                DecModelloCampoOutSelUdTableBean datiSpecTipoDocTable = (DecModelloCampoOutSelUdTableBean) getForm()
                        .getAttributiTipoDocList().getTable();
                clearTable(datiSpecTipoDocTable);
                DecModelloCampoOutSelUdTableBean datiCompilati = new DecModelloCampoOutSelUdTableBean();

                // Se il campo prevede sostituzioni di testo, ricerco i tag al suo interno
                List<String> listaTagTextArea = new ArrayList<>();
                List<String> listaTagAssentiTextArea = new ArrayList<>();
                Pattern tagPattern = Pattern.compile("<(\\w+[-\\w]*)>");
                Matcher m = tagPattern.matcher(dlFormatoOut);
                while (m.find()) {
                    listaTagTextArea.add(m.group(1));
                }
                int numeroTagSelezionati = flSelezionatoDatiProfilo.length + flSelezionatoDatiSpecUniDoc.length
                        + flSelezionatoDatiSpecDoc.length;
                if (numeroTagSelezionati == 0 && listaTagTextArea.isEmpty()) {
                    getMessageBox().addError("Selezionare almeno un dato");
                } else if (numeroTagSelezionati == 0 && !listaTagTextArea.isEmpty()) {
                    // Se non sono stati selezionati attributi ma è stata popolata la textArea do errore
                    getMessageBox().addError("I seguenti attributi non sono stati selezionati nelle liste: <br/>");
                    for (String nonTrovatoNelleCheckBox : listaTagTextArea) {
                        getMessageBox().addError(nonTrovatoNelleCheckBox + " <br/>");
                    }
                } else if (numeroTagSelezionati == 1 && listaTagTextArea.isEmpty()) {
                    // Se è stato selezionato un attributo ma non è contenuto nella textArea
                    String flSelezionato;
                    DecModelloCampoOutSelUdTableBean table;
                    String tiTrasform = null;
                    boolean datiProfilo = false;
                    if (flSelezionatoDatiProfilo.length > 0) {
                        flSelezionato = flSelezionatoDatiProfilo[0];
                        table = datiProfiloTable;
                        datiProfilo = true;
                    } else if (flSelezionatoDatiSpecUniDoc.length > 0) {
                        flSelezionato = flSelezionatoDatiSpecUniDoc[0];
                        table = datiSpecTipoUdTable;
                        tiTrasform = (tiTrasformDatiSpecUniDoc != null && tiTrasformDatiSpecUniDoc.length > 0)
                                ? tiTrasformDatiSpecUniDoc[0] : null;
                    } else {
                        flSelezionato = flSelezionatoDatiSpecDoc[0];
                        table = datiSpecTipoDocTable;
                        tiTrasform = (tiTrasformDatiSpecDoc != null && tiTrasformDatiSpecDoc.length > 0)
                                ? tiTrasformDatiSpecDoc[0] : null;
                    }
                    if (StringUtils.isNumeric(flSelezionato)) {
                        DecModelloCampoOutSelUdRowBean row = table.getRow(Integer.parseInt(flSelezionato));
                        dlFormatoOut = "<" + row.getNmCampo() + ">";
                        checkCampoSelezionato(row, tiOut, tiTrasform, datiProfilo);
                        if (!getMessageBox().hasError()) {
                            datiCompilati.add(row);
                        }
                    }
                } else {
                    // Eseguo i controlli regole su ogni tabella - DATI PROFILO, DATI SPEC TIPI UD e DATI SPEC TIPI DOC
                    // if (!getMessageBox().hasError()) {
                    checkRegolaRappresentazione(listaTagTextArea, listaTagAssentiTextArea, tiOut, datiProfiloTable,
                            flSelezionatoDatiProfilo, null, datiCompilati, true);
                    // }
                    // if (!getMessageBox().hasError()) {
                    checkRegolaRappresentazione(listaTagTextArea, listaTagAssentiTextArea, tiOut, datiSpecTipoUdTable,
                            flSelezionatoDatiSpecUniDoc, tiTrasformDatiSpecUniDoc, datiCompilati, false);
                    // }
                    // if (!getMessageBox().hasError()) {
                    checkRegolaRappresentazione(listaTagTextArea, listaTagAssentiTextArea, tiOut, datiSpecTipoDocTable,
                            flSelezionatoDatiSpecDoc, tiTrasformDatiSpecDoc, datiCompilati, false);
                    // }
                    if (!listaTagAssentiTextArea.isEmpty()) {
                        getMessageBox().addError(
                                "I seguenti attributi non sono stati inseriti nella text area per il formato di rappresentazione: <br/>");
                        for (String nonTrovatoNellaTextArea : listaTagAssentiTextArea) {
                            getMessageBox().addError(nonTrovatoNellaTextArea + " <br/>");
                        }
                    }
                }

                if (!getMessageBox().hasError()) {
                    // Controlli di coerenza - se è definito il tipo PG_UD_SERIE, il tipo DS_KEY_ORD_UD_SERIE deve avere
                    // lo stesso valore
                    DecModelloOutSelUdTableBean regoleRapprList = (DecModelloOutSelUdTableBean) getForm()
                            .getRegoleRapprList().getTable();
                    List<Object> tiOutList = regoleRapprList.toList(DecModelloOutSelUdTableDescriptor.COL_TI_OUT);

                    int indexElement = -1;
                    String dlFormatoOutProgressivo = null;
                    String dlFormatoOutKeyOrd = null;
                    if (CostantiDB.TipoDiRappresentazione.DS_KEY_ORD_UD_SERIE.name().equals(tiOut)) {
                        if ((indexElement = tiOutList
                                .indexOf(CostantiDB.TipoDiRappresentazione.PG_UD_SERIE.name())) != -1) {
                            List<Object> dlFormatoOutList = regoleRapprList
                                    .toList(DecModelloOutSelUdTableDescriptor.COL_DL_FORMATO_OUT);
                            dlFormatoOutProgressivo = (String) dlFormatoOutList.get(indexElement);

                        }
                    } else if (CostantiDB.TipoDiRappresentazione.PG_UD_SERIE.name().equals(tiOut)) {
                        if ((indexElement = tiOutList
                                .indexOf(CostantiDB.TipoDiRappresentazione.DS_KEY_ORD_UD_SERIE.name())) != -1) {
                            List<Object> dlFormatoOutList = regoleRapprList
                                    .toList(DecModelloOutSelUdTableDescriptor.COL_DL_FORMATO_OUT);
                            dlFormatoOutKeyOrd = (String) dlFormatoOutList.get(indexElement);
                        }
                    }
                    if (indexElement != -1) {
                        if (StringUtils.isNotBlank(dlFormatoOutProgressivo)) {
                            if (!dlFormatoOut.startsWith(dlFormatoOutProgressivo)) {
                                getMessageBox().addError(
                                        "La regola definita sul progressivo deve coincidere o rappresentare la prima parte della regola definita sull'ordinamento delle unit\u00E0 documentarie appartenenti alla serie");
                            }
                        } else if (StringUtils.isNotBlank(dlFormatoOutKeyOrd)) {
                            if (!dlFormatoOutKeyOrd.startsWith(dlFormatoOut)) {
                                getMessageBox().addError(
                                        "La regola definita sul progressivo deve coincidere o rappresentare la prima parte della regola definita sull'ordinamento delle unit\u00E0 documentarie appartenenti alla serie");
                            }
                        }
                    }
                }

                if (!getMessageBox().hasError()) {
                    datiCompilati.sort();
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null,
                                    null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    if (getForm().getRegoleRapprDetail().getStatus().equals(Status.update)
                            && idModelloOutSelUd != null) {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                        modelliSerieEjb.saveDecModelloOutSelUd(param, idModelloOutSelUd, tiOut, dlFormatoOut,
                                datiCompilati);
                    } else if (getForm().getRegoleRapprDetail().getStatus().equals(Status.insert)
                            && idModelloTipoSerie != null) {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                        modelliSerieEjb.saveDecModelloOutSelUd(param, tiOut, dlFormatoOut, datiCompilati,
                                idModelloTipoSerie);
                    } else {
                        throw new ParerUserError("Errore inaspettato nel salvataggio della regola di rappresentazione");
                    }

                    getMessageBox().addInfo("Regola salvata con successo");
                    getMessageBox().setViewMode(ViewMode.plain);
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
        if (getMessageBox().hasError()) {
            forwardToPublisher(getLastPublisher());
        } else {
            goBack();
        }
    }

    private void clearTable(DecModelloCampoOutSelUdTableBean tableBean) {
        if (tableBean != null) {
            for (DecModelloCampoOutSelUdRowBean row : tableBean) {
                row.setBigDecimal("fl_selezionato", BigDecimal.ZERO);
            }
        }
    }

    private void checkRegolaRappresentazione(List<String> listaTagTextArea, List<String> listaTagAssentiTextArea,
            String tiOut, DecModelloCampoOutSelUdTableBean table, String[] flSelezionatoTable, String[] tiTrasformTable,
            DecModelloCampoOutSelUdTableBean datiCompilati, boolean datiProfilo) throws ParerUserError {
        for (int index = 0; index < flSelezionatoTable.length; index++) {
            String flSelezionato = flSelezionatoTable[index];
            if (StringUtils.isNumeric(flSelezionato)) {
                int flSelezionatoIndex = Integer.parseInt(flSelezionato);
                DecModelloCampoOutSelUdRowBean row = table.getRow(flSelezionatoIndex);
                String tiTrasform = null;
                if (tiTrasformTable != null) {
                    tiTrasform = tiTrasformTable[flSelezionatoIndex];
                }
                checkCampoSelezionato(row, tiOut, tiTrasform, datiProfilo);
                if (!listaTagTextArea.contains(row.getNmCampo())) {
                    listaTagAssentiTextArea.add(row.getNmCampo());
                }
                if (!getMessageBox().hasError() && listaTagAssentiTextArea.isEmpty()) {
                    datiCompilati.add(row);
                }
            } else {
                throw new ParerUserError("Errore inaspettato nel controllo dei dati inseriti");
            }
        }
    }

    private void checkCampoSelezionato(DecModelloCampoOutSelUdRowBean row, String tiOut, String tiTrasform,
            boolean datiProfilo) {
        String nmCampo = row.getNmCampo();
        row.setBigDecimal("fl_selezionato", BigDecimal.ONE);
        // Se si tratta dei DATI_PROFILO, il campo PROGRESSIVO deve essere definito come NUMERO
        if (datiProfilo && tiOut.equals(CostantiDB.TipoDiRappresentazione.PG_UD_SERIE.name())
                && !CostantiDB.NomeCampo.NUMERO.name().equals(nmCampo)) {
            getMessageBox()
                    .addError("Nel caso il nome campo sia '" + CostantiDB.TipoDiRappresentazione.PG_UD_SERIE.toString()
                            + "' il dato profilo deve essere di tipo 'NUMERO' <br/>");
        } else if (CostantiDB.TipoDiRappresentazione.DS_KEY_ORD_UD_SERIE.name().equals(tiOut)
                && CostantiDB.NomeCampo.NUMERO.name().equals(nmCampo)) {
            row.setTiTrasformCampo(CostantiDB.TipoTrasformatore.OUT_PAD_CHAR.getTransformString());
        } else {
            row.setTiTrasformCampo(tiTrasform);
        }
    }

    @Override
    public void deleteRegoleRapprList() throws EMFError {
        DecModelloOutSelUdRowBean currentRow = (DecModelloOutSelUdRowBean) getForm().getRegoleRapprList().getTable()
                .getCurrentRow();
        BigDecimal idModelloOutSelUd = currentRow.getIdModelloOutSelUd();
        int riga = getForm().getRegoleRapprList().getTable().getCurrentRowIndex();

        if (!getMessageBox().hasError() && idModelloOutSelUd != null) {
            try {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                                CostantiDB.TipoAplVGetValAppart.APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                        SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getRegoleRapprList()));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                modelliSerieEjb.deleteDecModelloOutSelUd(param, idModelloOutSelUd);
                getForm().getRegoleRapprList().getTable().remove(riga);

                getMessageBox().addInfo("Regola di rappresentazione eliminata con successo");
                getMessageBox().setViewMode(ViewMode.plain);
                getForm().getRegoleRapprList().setHideInsertButton(getForm().getRegoleRapprList().getTable()
                        .size() == CostantiDB.TipoDiRappresentazione.getComboTipoDiRappresentazione().length);
            } catch (ParerUserError ex) {
                getMessageBox().addError(
                        "La regola di rappresentazione non pu\u00F2 essere eliminata: " + ex.getDescription());
            }
        }
        forwardToPublisher(getLastPublisher());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Salvataggio//Elimina associazioni strutture modello di tipo serie">
    private void saveAssociazioniStrutturaModello() throws EMFError {
        if (!getForm().getStrutSelezionateList().getTable().isEmpty()) {
            BigDecimal idModelloTipoSerie = getForm().getStrutModelloDetail().getId_modello_tipo_serie().parse();
            try {
                if (idModelloTipoSerie != null) {
                    OrgStrutTableBean table = (OrgStrutTableBean) getForm().getStrutSelezionateList().getTable();
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null,
                                    null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                            SpagoliteLogUtil.getToolbarInsert());
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    modelliSerieEjb.saveDecUsoModelloTipoSerie(param, idModelloTipoSerie, table);
                    if (!getMessageBox().hasError()) {
                        getMessageBox().addInfo("Le strutture sono state aggiunte con successo al modello");
                        getMessageBox().setViewMode(ViewMode.plain);

                        getForm().getStrutSelezionateList().getTable().clear();
                    }
                } else {
                    getMessageBox()
                            .addError("Errore inaspettato nell'aggiunta di strutture al modello: modello non presente");
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        } else {
            getMessageBox().addError("Selezionare almeno una struttura da associare al modello");
        }
        if (!getMessageBox().hasError()) {
            goBack();
        } else {
            forwardToPublisher(Application.Publisher.STRUT_MODELLO_SERIE_DETAIL);
        }
    }

    @Override
    public void deleteStrutModelloList() throws EMFError {
        DecUsoModelloTipoSerieRowBean currentRow = (DecUsoModelloTipoSerieRowBean) getForm().getStrutModelloList()
                .getTable().getCurrentRow();
        BigDecimal idUsoModelloTipoSerie = currentRow.getIdUsoModelloTipoSerie();
        int riga = getForm().getStrutModelloList().getTable().getCurrentRowIndex();

        if (!getMessageBox().hasError() && idUsoModelloTipoSerie != null) {
            try {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                                CostantiDB.TipoAplVGetValAppart.APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                        SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getStrutModelloList()));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                modelliSerieEjb.deleteDecUsoModelloTipoSerie(param, idUsoModelloTipoSerie);
                getForm().getStrutModelloList().getTable().remove(riga);

                getMessageBox().addInfo("Associazione alla struttura eliminata con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            } catch (ParerUserError ex) {
                getMessageBox().addError(
                        "L'associazione alla struttura non pu\u00F2 essere eliminata: " + ex.getDescription());
            }
        }
        forwardToPublisher(getLastPublisher());
    }
    // </editor-fold>

    @Override
    public void dettaglioOnClick() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                || getNavigationEvent().equals(ListAction.NE_NEXT) || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getModelliTipiSerieList().getName())) {
                forwardToPublisher(Application.Publisher.MODELLO_SERIE_DETAIL);
            } else if (getTableName().equals(getForm().getNoteModelloTipoSerieList().getName())) {
                forwardToPublisher(Application.Publisher.NOTA_MODELLO_SERIE_DETAIL);
            } else if (getTableName().equals(getForm().getRegoleAcquisizioneList().getName())) {
                forwardToPublisher(Application.Publisher.REGOLA_MODELLO_ACQUISIZIONE_DETAIL);
            } else if (getTableName().equals(getForm().getRegoleFiltraggioList().getName())) {
                forwardToPublisher(Application.Publisher.REGOLA_MODELLO_FILTRAGGIO_DETAIL);
            } else if (getTableName().equals(getForm().getDatiSpecList().getName())) {
                forwardToPublisher(Application.Publisher.FILTRI_DATI_SPEC_MODELLO_DETAIL);
            } else if (getTableName().equals(getForm().getRegoleRapprList().getName())) {
                forwardToPublisher(Application.Publisher.REGOLA_MODELLO_RAPPRESENTAZIONE_DETAIL);
            }
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.GESTIONE_MODELLI_SERIE;
    }

    @Override
    public String getControllerName() {
        return Application.Actions.MODELLI_SERIE;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        try {
            if (publisherName.equals(Application.Publisher.GESTIONE_MODELLI_SERIE)) {
                int rowIndex;
                int pageSize;
                if (getForm().getModelliTipiSerieList().getTable() != null) {
                    rowIndex = getForm().getModelliTipiSerieList().getTable().getCurrentRowIndex();
                    pageSize = getForm().getModelliTipiSerieList().getTable().getPageSize();
                } else {
                    rowIndex = 0;
                    pageSize = WebConstants.DEFAULT_PAGE_SIZE;
                }
                getForm().getModelliTipiSerieList()
                        .setTable(modelliSerieEjb.getDecModelloTipoSerieAllAbilitatiTableBean(
                                getUser().getIdOrganizzazioneFoglia(),
                                getForm().getModelliTipiSerieList().isFilterValidRecords()));
                getForm().getModelliTipiSerieList().getTable().setPageSize(pageSize);
                getForm().getModelliTipiSerieList().getTable().setCurrentRowIndex(rowIndex);
            } else if (publisherName.equals(Application.Publisher.MODELLO_SERIE_DETAIL)) {
                DecModelloTipoSerieRowBean currentRow = (DecModelloTipoSerieRowBean) getForm().getModelliTipiSerieList()
                        .getTable().getCurrentRow();
                BigDecimal idModelloTipoSerie = currentRow.getIdModelloTipoSerie();
                if (idModelloTipoSerie != null) {
                    loadDettaglioModello(idModelloTipoSerie);
                }
                getForm().getModelliTipiSerieDetail().setViewMode();
                getForm().getModelliTipiSerieDetail().setStatus(Status.view);
                getForm().getModelliTipiSerieList().setStatus(Status.view);
            }
            postLoad();

        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
            forwardToPublisher(getLastPublisher());
        } catch (EMFError e) {
            logger.error("Errore nel ricaricamento della pagina " + publisherName, e);
            getMessageBox().addError("Errore nel ricaricamento della pagina " + publisherName);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Lista modelli">
    @Secure(action = "Menu.Serie.ModelliTipiSerie")
    public void loadListaModelliSerie() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Serie.ModelliTipiSerie");

        // Lista modelli tipi Serie
        getForm().getModelliTipiSerieList().setTable(modelliSerieEjb.getDecModelloTipoSerieAllAbilitatiTableBean(
                getUser().getIdOrganizzazioneFoglia(), getForm().getModelliTipiSerieList().isFilterValidRecords()));
        getForm().getModelliTipiSerieList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getModelliTipiSerieList().getTable().first();

        forwardToPublisher(Application.Publisher.GESTIONE_MODELLI_SERIE);
    }

    @Override
    public void filterInactiveRecordsModelliTipiSerieList() throws EMFError {
        int rowIndex = 0;
        int pageSize = WebConstants.DEFAULT_PAGE_SIZE;
        if (getForm().getModelliTipiSerieList().getTable() != null) {
            rowIndex = getForm().getModelliTipiSerieList().getTable().getCurrentRowIndex();
            pageSize = getForm().getModelliTipiSerieList().getTable().getPageSize();
        }

        // Lista modelli tipi Serie
        getForm().getModelliTipiSerieList().setTable(modelliSerieEjb.getDecModelloTipoSerieAllAbilitatiTableBean(
                getUser().getIdOrganizzazioneFoglia(), getForm().getModelliTipiSerieList().isFilterValidRecords()));
        getForm().getModelliTipiSerieList().getTable().setCurrentRowIndex(rowIndex);
        getForm().getModelliTipiSerieList().getTable().setPageSize(pageSize);
        forwardToPublisher(getLastPublisher());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Gestione trigger inserimento/modifica modello">
    @Override
    public JSONObject triggerModelliTipiSerieDetailConserv_unlimitedOnTrigger() throws EMFError {
        getForm().getModelliTipiSerieDetail().post(getRequest());
        return ActionUtils.getConservUnlimitedTrigger(getForm().getModelliTipiSerieDetail());
    }

    @Override
    public JSONObject triggerModelliTipiSerieDetailId_ambienteOnTrigger() throws EMFError {
        getForm().getModelliTipiSerieDetail().post(getRequest());
        ActionUtils utile = new ActionUtils();
        utile.triggerAmbienteGenerico(getForm().getModelliTipiSerieDetail(), getUser().getIdUtente(), Boolean.FALSE);
        return getForm().getModelliTipiSerieDetail().asJSON();
    }

    public void triggerModelliTipiSerieDetailId_enteOnTriggerJs() throws EMFError {
        getForm().getModelliTipiSerieDetail().getId_ente().post(getRequest());
        ActionUtils utile = new ActionUtils();
        utile.triggerEnteGenerico(getForm().getModelliTipiSerieDetail(), getUser().getIdUtente(), Boolean.FALSE);
        BigDecimal idStrut = null;
        if ((idStrut = getForm().getModelliTipiSerieDetail().getId_strut().parse()) != null) {
            getForm().getModelliTipiSerieDetail().getId_tipo_unita_doc_dati_spec_combo()
                    .setDecodeMap(DecodeMap.Factory.newInstance(
                            tipoUnitaDocEjb.getTipiUnitaDocAbilitati(getUser().getIdUtente(), idStrut),
                            "id_tipo_unita_doc", "nm_tipo_unita_doc"));
            getForm().getModelliTipiSerieDetail().getId_tipo_doc_dati_spec_combo()
                    .setDecodeMap(DecodeMap.Factory.newInstance(
                            tipoDocumentoEjb.getTipiDocPrincipaliAbilitati(getUser().getIdUtente(), idStrut),
                            "id_tipo_doc", "nm_tipo_doc"));
        }
        redirectToAjax(getForm().getModelliTipiSerieDetail().asJSON());
    }

    public void triggerModelliTipiSerieDetailId_strutOnTriggerJs() throws EMFError {
        getForm().getModelliTipiSerieDetail().getId_strut().post(getRequest());
        BigDecimal idStrut = getForm().getModelliTipiSerieDetail().getId_strut().parse();
        if (idStrut != null) {
            getForm().getModelliTipiSerieDetail().getId_tipo_unita_doc_dati_spec_combo()
                    .setDecodeMap(DecodeMap.Factory.newInstance(
                            tipoUnitaDocEjb.getTipiUnitaDocAbilitati(getUser().getIdUtente(), idStrut),
                            "id_tipo_unita_doc", "nm_tipo_unita_doc"));
            getForm().getModelliTipiSerieDetail().getId_tipo_doc_dati_spec_combo()
                    .setDecodeMap(DecodeMap.Factory.newInstance(
                            tipoDocumentoEjb.getTipiDocPrincipaliAbilitati(getUser().getIdUtente(), idStrut),
                            "id_tipo_doc", "nm_tipo_doc"));
        } else {
            getForm().getModelliTipiSerieDetail().getId_tipo_unita_doc_dati_spec_combo().setDecodeMap(new DecodeMap());
            getForm().getModelliTipiSerieDetail().getId_tipo_doc_dati_spec_combo().setDecodeMap(new DecodeMap());
        }
        redirectToAjax(getForm().getModelliTipiSerieDetail().asJSON());
    }

    public void populateIdTipoUnitaDocDatiSpecOnTriggerJs() throws EMFError {
        getForm().getModelliTipiSerieDetail().getId_tipo_unita_doc_dati_spec().setValue(getRequest()
                .getParameter(getForm().getModelliTipiSerieDetail().getId_tipo_unita_doc_dati_spec().getName()));
        getForm().getModelliTipiSerieDetail().getNm_tipo_unita_doc_dati_spec().setValue(getRequest()
                .getParameter(getForm().getModelliTipiSerieDetail().getNm_tipo_unita_doc_dati_spec().getName()));
        redirectToAjax(getForm().getModelliTipiSerieDetail().asJSON());
    }

    public void populateIdTipoDocDatiSpecOnTriggerJs() throws EMFError {
        getForm().getModelliTipiSerieDetail().getId_tipo_doc_dati_spec().setValue(
                getRequest().getParameter(getForm().getModelliTipiSerieDetail().getId_tipo_doc_dati_spec().getName()));
        getForm().getModelliTipiSerieDetail().getNm_tipo_doc_dati_spec().setValue(
                getRequest().getParameter(getForm().getModelliTipiSerieDetail().getNm_tipo_doc_dati_spec().getName()));
        redirectToAjax(getForm().getModelliTipiSerieDetail().asJSON());
    }

    @Override
    public void cercaTipoUd() throws EMFError {
        getForm().getModelliTipiSerieDetail().post(getRequest());
        if (getForm().getModelliTipiSerieDetail().getId_ambiente().parse() == null) {
            getMessageBox().addError("Inserire l'ambiente per il modello");
        } else {
            getForm().getModelliTipiSerieDetail().getNm_ambiente()
                    .setValue(getForm().getModelliTipiSerieDetail().getId_ambiente().getDecodedValue());
            getForm().getModelliTipiSerieDetail().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getModelliTipiSerieDetail().getId_tipo_unita_doc_dati_spec_combo().setDecodeMap(new DecodeMap());
            getRequest().setAttribute("customModelloTipoUdBox", true);
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void cercaTipoDoc() throws EMFError {
        getForm().getModelliTipiSerieDetail().post(getRequest());
        if (getForm().getModelliTipiSerieDetail().getId_ambiente().parse() == null) {
            getMessageBox().addError("Inserire l'ambiente per il modello");
        } else {
            getForm().getModelliTipiSerieDetail().getNm_ambiente()
                    .setValue(getForm().getModelliTipiSerieDetail().getId_ambiente().getDecodedValue());
            getForm().getModelliTipiSerieDetail().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getModelliTipiSerieDetail().getId_tipo_doc_dati_spec_combo().setDecodeMap(new DecodeMap());
            getRequest().setAttribute("customModelloTipoDocBox", true);
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void rimuoviTipoUd() throws EMFError {
        getForm().getModelliTipiSerieDetail().getId_tipo_unita_doc_dati_spec().setValue(null);
        getForm().getModelliTipiSerieDetail().getNm_tipo_unita_doc_dati_spec().setValue(null);
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void rimuoviTipoDoc() throws EMFError {
        getForm().getModelliTipiSerieDetail().getId_tipo_doc_dati_spec().setValue(null);
        getForm().getModelliTipiSerieDetail().getNm_tipo_doc_dati_spec().setValue(null);
        forwardToPublisher(getLastPublisher());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Gestione trigger inserimento/modifica regole di filtraggio">
    @Override
    public void cercaTipoDocFiltraggio() throws EMFError {
        // Eseguo la post sulla multiselect altrimenti perde i valori eventualmente cancellati
        getForm().getRegoleFiltraggioDetail().getNm_tipo_doc_dati_spec().post(getRequest());
        getForm().getRegoleFiltraggioDetail().getNm_ambiente()
                .setValue(getForm().getModelliTipiSerieDetail().getId_ambiente().getDecodedValue());
        getForm().getRegoleFiltraggioDetail().getId_ente().clear();
        getForm().getRegoleFiltraggioDetail().getId_strut().setDecodeMap(new DecodeMap());
        getForm().getRegoleFiltraggioDetail().getId_tipo_doc_dati_spec_combo().setDecodeMap(new DecodeMap());
        getRequest().setAttribute("customModelloTipoDocFiltraggioBox", true);
        forwardToPublisher(getLastPublisher());
    }

    public void triggerRegoleFiltraggioDetailId_enteOnTriggerJs() throws EMFError {
        getForm().getRegoleFiltraggioDetail().getId_ente().post(getRequest());
        ActionUtils utile = new ActionUtils();
        utile.triggerEnteGenerico(getForm().getRegoleFiltraggioDetail(), getUser().getIdUtente(), Boolean.FALSE);
        redirectToAjax(getForm().getRegoleFiltraggioDetail().asJSON());
    }

    public void triggerRegoleFiltraggioDetailId_strutOnTriggerJs() throws EMFError {
        getForm().getRegoleFiltraggioDetail().getId_strut().post(getRequest());
        BigDecimal idStrut = getForm().getRegoleFiltraggioDetail().getId_strut().parse();
        if (idStrut != null) {
            getForm().getRegoleFiltraggioDetail().getId_tipo_doc_dati_spec_combo()
                    .setDecodeMap(DecodeMap.Factory.newInstance(
                            tipoDocumentoEjb.getTipiDocPrincipaliAbilitati(getUser().getIdUtente(), idStrut),
                            "id_tipo_doc", "nm_tipo_doc"));
        } else {
            getForm().getRegoleFiltraggioDetail().getId_tipo_doc_dati_spec_combo().setDecodeMap(new DecodeMap());
        }
        redirectToAjax(getForm().getRegoleFiltraggioDetail().asJSON());
    }

    public void populateIdTipoDocDatiSpecFiltraggioOnTriggerJs() throws EMFError {
        String nmTipoDoc = getRequest()
                .getParameter(getForm().getRegoleFiltraggioDetail().getNm_tipo_doc_dati_spec().getName());
        Set<String> values = getForm().getRegoleFiltraggioDetail().getNm_tipo_doc_dati_spec().getValues();
        values.add(nmTipoDoc);
        BaseTable tb = new BaseTable();
        for (String value : values) {
            tb.add().setString("nm_tipo_doc_dati_spec", value);
        }
        getForm().getRegoleFiltraggioDetail().getNm_tipo_doc_dati_spec()
                .setDecodeMap(DecodeMap.Factory.newInstance(tb, "nm_tipo_doc_dati_spec", "nm_tipo_doc_dati_spec"));
        getForm().getRegoleFiltraggioDetail().getNm_tipo_doc_dati_spec()
                .setValues(values.toArray(new String[values.size()]));

        redirectToAjax(getForm().getRegoleFiltraggioDetail().asJSON());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Gestione associazione strutture/modello">
    @Override
    public void cercaStrutture() throws EMFError {
        if (getForm().getStrutModelloDetail().postAndValidate(getRequest(), getMessageBox())) {
            BigDecimal idModelloTipoSerie = getForm().getStrutModelloDetail().getId_modello_tipo_serie().parse();
            BigDecimal idAmbiente = getForm().getStrutModelloDetail().getId_ambiente().parse();
            BigDecimal idEnte = getForm().getStrutModelloDetail().getNm_ente().parse();
            String nmStrut = getForm().getStrutModelloDetail().getNm_strut().parse();
            if (!getMessageBox().hasError()) {
                try {
                    OrgStrutTableBean table = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), nmStrut,
                            idEnte, idAmbiente, idModelloTipoSerie, Boolean.FALSE);
                    getForm().getStrutRicercateList().setTable(table);
                    getForm().getStrutRicercateList().getTable().addSortingRule("nm_ente", SortingRule.ASC);
                    getForm().getStrutRicercateList().getTable().addSortingRule("nm_strut", SortingRule.ASC);
                    getForm().getStrutRicercateList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    getForm().getStrutRicercateList().getTable().first();

                    getForm().getStrutSelezionateList().setTable(new OrgStrutTableBean());
                    getForm().getStrutSelezionateList().getTable().addSortingRule("nm_ente", SortingRule.ASC);
                    getForm().getStrutSelezionateList().getTable().addSortingRule("nm_strut", SortingRule.ASC);
                    getForm().getStrutSelezionateList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    getForm().getStrutSelezionateList().getTable().first();
                } catch (ParerUserError ex) {
                    getMessageBox().addError(ex.getDescription());
                }
            }
        }
        forwardToPublisher(Application.Publisher.STRUT_MODELLO_SERIE_DETAIL);
    }

    @Override
    public void selectStrutSelezionateList() throws EMFError {
        BaseRowInterface row = getForm().getStrutSelezionateList().getTable().getCurrentRow();
        int index = getForm().getStrutSelezionateList().getTable().getCurrentRowIndex();
        getForm().getStrutSelezionateList().getTable().remove(index);
        getForm().getStrutRicercateList().getTable().add(row);
        getForm().getStrutRicercateList().getTable().sort();

        forwardToPublisher(Application.Publisher.STRUT_MODELLO_SERIE_DETAIL);
    }

    @Override
    public void selectStrutRicercateList() throws EMFError {
        BaseRowInterface row = getForm().getStrutRicercateList().getTable().getCurrentRow();
        int index = getForm().getStrutRicercateList().getTable().getCurrentRowIndex();
        getForm().getStrutRicercateList().getTable().remove(index);
        getForm().getStrutSelezionateList().getTable().add(row);
        getForm().getStrutSelezionateList().getTable().sort();

        forwardToPublisher(Application.Publisher.STRUT_MODELLO_SERIE_DETAIL);
    }
    // </editor-fold>

    @Override
    public void logEventi() throws EMFError {
        GestioneLogEventiForm form = new GestioneLogEventiForm();
        form.getOggettoDetail().getNmApp().setValue(paramApplicHelper.getApplicationName().getDsValoreParamApplic());
        form.getOggettoDetail().getNm_tipo_oggetto().setValue(SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_SERIE);
        DecModelloTipoSerieRowBean rb = (DecModelloTipoSerieRowBean) getForm().getModelliTipiSerieList().getTable()
                .getCurrentRow();
        form.getOggettoDetail().getIdOggetto().setValue(rb.getIdModelloTipoSerie().toPlainString());
        redirectToAction(it.eng.parer.sacerlog.slite.gen.Application.Actions.GESTIONE_LOG_EVENTI,
                "?operation=inizializzaLogEventi", form);
    }

    @Override
    protected void postLoad() {
        super.postLoad();
        if (getForm().getModelliTipiSerieList().getStatus().equals(Status.view)) {
            getForm().getModelliTipiSerieDetail().getLogEventi().setEditMode();
        } else {
            getForm().getModelliTipiSerieDetail().getLogEventi().setViewMode();
        }
    }

}
