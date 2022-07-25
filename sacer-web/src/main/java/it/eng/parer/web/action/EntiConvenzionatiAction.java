package it.eng.parer.web.action;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.sacerlog.util.web.SpagoliteLogUtil;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.EntiConvenzionatiAbstractAction;
import it.eng.parer.slite.gen.form.StruttureForm;
import it.eng.parer.slite.gen.tablebean.SIOrgEnteConvenzOrgRowBean;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements;
import it.eng.spagoLite.message.MessageBox;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.ejb.EJB;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Bonora_L
 */
public class EntiConvenzionatiAction extends EntiConvenzionatiAbstractAction {

    private static final Logger logger = LoggerFactory.getLogger(EntiConvenzionatiAction.class);
    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;

    @Override
    public void initOnClick() throws EMFError {
    }

    @Override
    public void loadDettaglio() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                || getNavigationEvent().equals(ListAction.NE_NEXT) || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getEnteConvenzOrgList().getName())) {
                BaseTable ambienteEnteTable = ambienteEjb
                        .getUsrVAbilAmbEnteConvenzTableBean(new BigDecimal(getUser().getIdUtente()));
                DecodeMap mappaAmbienteEnte = new DecodeMap();
                mappaAmbienteEnte.populatedMap(ambienteEnteTable, "id_ambiente_ente_convenz",
                        "nm_ambiente_ente_convenz");
                getForm().getEnteConvenzOrg().getId_ambiente_ente_convenz().setDecodeMap(mappaAmbienteEnte);

                BigDecimal idEnteConvenzOrg = ((SIOrgEnteConvenzOrgRowBean) getForm().getEnteConvenzOrgList().getTable()
                        .getCurrentRow()).getBigDecimal("id_ente_convenz_org");
                SIOrgEnteConvenzOrgRowBean enteConvenzOrgRowBean = ambienteEjb
                        .getSIOrgEnteConvenzOrgRowBean(idEnteConvenzOrg);

                // BigDecimal idAmbienteEnteConvenz = currentRow.getBigDecimal("id_ambiente_ente_convenz");
                // Ricavo il TableBean relativo agli enti convenzionati
                BaseTable enteConvenzTableBean = ambienteEjb
                        .getSIOrgEnteSiamTableBean(enteConvenzOrgRowBean.getBigDecimal("id_ambiente_ente_convenz"));
                DecodeMap mappaEntiConvenz = new DecodeMap();
                mappaEntiConvenz.populatedMap(enteConvenzTableBean, "id_ente_siam", "nm_ente_siam");
                getForm().getEnteConvenzOrg().getId_ente_convenz().setDecodeMap(mappaEntiConvenz);

                getForm().getEnteConvenzOrg().copyFromBean(enteConvenzOrgRowBean);

                getForm().getEnteConvenzOrg().setViewMode();
                getForm().getEnteConvenzOrgList().setStatus(BaseElements.Status.view);
                getForm().getEnteConvenzOrg().setStatus(BaseElements.Status.view);

                String cessato = (String) getRequest().getParameter("cessato");
                if (StringUtils.isNotBlank(cessato) && "1".equals(cessato)) {
                    getForm().getEnteConvenzOrgList().setUserOperations(true, false, false, false);
                }
            }
        }
    }

    @Override
    public void undoDettaglio() throws EMFError {
        elencoOnClick();
    }

    @Override
    public void insertDettaglio() throws EMFError {
        if (getTableName().equals(getForm().getEnteConvenzOrgList().getName())) {
            getForm().getEnteConvenzOrg().clear();

            BaseTable ambienteEnteTable = ambienteEjb
                    .getUsrVAbilAmbEnteConvenzTableBean(new BigDecimal(getUser().getIdUtente()));
            DecodeMap mappaAmbienteEnte = new DecodeMap();
            mappaAmbienteEnte.populatedMap(ambienteEnteTable, "id_ambiente_ente_convenz", "nm_ambiente_ente_convenz");
            getForm().getEnteConvenzOrg().getId_ambiente_ente_convenz().setDecodeMap(mappaAmbienteEnte);

            getForm().getEnteConvenzOrg().getId_ente_convenz().setDecodeMap(new DecodeMap());

            getForm().getEnteConvenzOrg().setEditMode();
            getForm().getEnteConvenzOrgList().setStatus(BaseElements.Status.insert);
            getForm().getEnteConvenzOrg().setStatus(BaseElements.Status.insert);

            forwardToPublisher(getDefaultPublsherName());
        }
    }

    @Override
    public void saveDettaglio() throws EMFError {
        try {
            if (getTableName().equals(getForm().getEnteConvenzOrgList().getName())
                    || getTableName().equals(getForm().getEnteConvenzOrg().getName())) {
                BigDecimal idStrut = getForm().getStrutRif().getId_strut().parse();
                if (getForm().getEnteConvenzOrg().postAndValidate(getRequest(), getMessageBox())) {
                    BigDecimal idEnteConvenz = getForm().getEnteConvenzOrg().getId_ente_convenz().parse();
                    Date dtIniVal = getForm().getEnteConvenzOrg().getDt_ini_val().parse();
                    Date dtFineVal = getForm().getEnteConvenzOrg().getDt_fine_val().parse();

                    if (dtIniVal.after(dtFineVal)) {
                        getMessageBox()
                                .addError("Attenzione: data di inizio validità superiore a data di fine validità");
                    }

                    if (!getMessageBox().hasError()) {

                        /*
                         * Codice aggiuntivo per il logging...
                         */
                        LogParam param = SpagoliteLogUtil.getLogParam(
                                configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null,
                                        null, null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                        if (getForm().getEnteConvenzOrg().getStatus().equals(BaseElements.Status.insert)) {
                            param.setNomeAzione(SpagoliteLogUtil.getToolbarSave(false));
                            BigDecimal idEnteConvenzOrg = ambienteEjb.insertEnteConvenzOrg(param, idStrut,
                                    idEnteConvenz, dtIniVal, dtFineVal);
                            getForm().getEnteConvenzOrg().getId_ente_convenz_org()
                                    .setValue(idEnteConvenzOrg.toPlainString());

                            SIOrgEnteConvenzOrgRowBean row = new SIOrgEnteConvenzOrgRowBean();
                            getForm().getEnteConvenzOrg().copyToBean(row);
                            row.setBigDecimal("id_ambiente_ente_convenz",
                                    getForm().getEnteConvenzOrg().getId_ambiente_ente_convenz().parse());
                            row.setString("nm_ambiente_ente_convenz",
                                    getForm().getEnteConvenzOrg().getId_ambiente_ente_convenz().getDecodedValue());
                            row.setString("nm_ente_convenz",
                                    getForm().getEnteConvenzOrg().getId_ente_convenz().getDecodedValue());

                            getForm().getEnteConvenzOrgList().getTable().last();
                            getForm().getEnteConvenzOrgList().getTable().add(row);
                        } else if (getForm().getEnteConvenzOrg().getStatus().equals(BaseElements.Status.update)) {
                            param.setNomeAzione(SpagoliteLogUtil.getToolbarSave(true));
                            BigDecimal idEnteConvenzOrg = getForm().getEnteConvenzOrg().getId_ente_convenz_org()
                                    .parse();
                            // La modifica, se interessa anche l'ente convenzionato, porta ad una modifica dell'id
                            // dell'associazione
                            BigDecimal idEnteConvenzOrgNew = ambienteEjb.updateEnteConvenzOrg(param, idEnteConvenzOrg,
                                    idEnteConvenz, idStrut, dtIniVal, dtFineVal);

                            if (idEnteConvenzOrg.compareTo(idEnteConvenzOrgNew) != 0) {
                                getForm().getEnteConvenzOrg().getId_ente_convenz_org()
                                        .setValue(idEnteConvenzOrgNew.toPlainString());
                                getForm().getEnteConvenzOrgList().getTable().getCurrentRow()
                                        .setBigDecimal("id_ente_convenz_org", idEnteConvenzOrgNew);
                            }
                        }
                        getMessageBox().addInfo("Associazione con l'ente convenzionato salvata con successo");
                        getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                        loadDettaglio();
                    }
                }

                if (!getMessageBox().hasError()) {
                    getForm().getEnteConvenzOrgList().setStatus(BaseElements.Status.view);
                    getForm().getEnteConvenzOrg().setStatus(BaseElements.Status.view);
                    getForm().getEnteConvenzOrg().setViewMode();
                }
                forwardToPublisher(Application.Publisher.ENTE_CONVENZIONATO_DETAIL);
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                || getNavigationEvent().equals(ListAction.NE_NEXT) || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getEnteConvenzOrgList().getName())) {
                forwardToPublisher(getDefaultPublsherName());
            }
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.ENTE_CONVENZIONATO_DETAIL;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
    }

    @Override
    public String getControllerName() {
        return Application.Actions.ENTI_CONVENZIONATI;
    }

    @Override
    public void updateEnteConvenzOrgList() throws EMFError {
        getForm().getEnteConvenzOrg().setEditMode();
        getForm().getEnteConvenzOrgList().setStatus(BaseElements.Status.update);
        getForm().getEnteConvenzOrg().setStatus(BaseElements.Status.update);
    }

    @Override
    public void updateEnteConvenzOrg() throws EMFError {
        updateEnteConvenzOrgList();
    }

    @Override
    public JSONObject triggerEnteConvenzOrgId_ambiente_ente_convenzOnTrigger() throws EMFError {
        getForm().getEnteConvenzOrg().post(getRequest());
        BigDecimal idAmbienteEnteConvenz = getForm().getEnteConvenzOrg().getId_ambiente_ente_convenz().parse();
        // Ricavo il TableBean relativo agli enti convenzionati: in questa fase vanno selezionati tutti in quanto
        // il controllo di validità verrà eseguito in fase di salvataggio
        // MEV#20463
        BaseTable enteSiamTableBean = ambienteEjb.getSIOrgEnteConvenzAccordoValidoTableBean(getUser().getIdUtente(),
                idAmbienteEnteConvenz);
        // end MEV#20463
        DecodeMap mappaEntiSiam = new DecodeMap();
        mappaEntiSiam.populatedMap(enteSiamTableBean, "id_ente_siam", "nm_ente_siam");
        getForm().getEnteConvenzOrg().getId_ente_convenz().setDecodeMap(mappaEntiSiam);
        return getForm().getEnteConvenzOrg().asJSON();
    }

    @Override
    public JSONObject triggerEnteConvenzOrgId_ente_convenzOnTrigger() throws EMFError {
        getForm().getEnteConvenzOrg().post(getRequest());
        BigDecimal idEnteConvenz = getForm().getEnteConvenzOrg().getId_ente_convenz().parse();
        DateFormat formato = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);

        try {
            BaseRow dateEnteSiamStrut = ambienteEjb.getDateAssociazioneEnteSiamStrutRowBean(idEnteConvenz);
            if (dateEnteSiamStrut != null) {
                getForm().getEnteConvenzOrg().getDt_ini_val()
                        .setValue(formato.format(dateEnteSiamStrut.getTimestamp("dt_ini_val")));
                ambienteEjb.getOrgEnteRowBean(idEnteConvenz);
                getForm().getEnteConvenzOrg().getDt_fine_val()
                        .setValue(formato.format(dateEnteSiamStrut.getTimestamp("dt_fine_val")));
            } else {
                //
                getForm().getEnteConvenzOrg().getDt_ini_val()
                        .setValue(formato.format(Calendar.getInstance().getTime()));
                Calendar c = Calendar.getInstance();
                c.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
                getForm().getEnteConvenzOrg().getDt_fine_val().setValue(formato.format(c.getTime()));
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        return getForm().getEnteConvenzOrg().asJSON();
    }

    @Override
    public void deleteEnteConvenzOrgList() throws EMFError {
        SIOrgEnteConvenzOrgRowBean row = (SIOrgEnteConvenzOrgRowBean) getForm().getEnteConvenzOrgList().getTable()
                .getCurrentRow();
        BigDecimal idEnteConvenzOrg = row.getIdEnteConvenzOrg();
        int riga = getForm().getEnteConvenzOrgList().getTable().getCurrentRowIndex();
        // Eseguo giusto un controllo per verificare che io stia prendendo la riga giusta se sono nel dettaglio
        if (getLastPublisher().equals(Application.Publisher.ENTE_CONVENZIONATO_DETAIL)) {
            if (!idEnteConvenzOrg.equals(getForm().getEnteConvenzOrg().getId_ente_convenz_org().parse())) {
                getMessageBox()
                        .addError("Eccezione imprevista nell'eliminazione dell'associazione all'ente convenzionato");
            }
        }
        // Controllo che l'associazione non sia l'unica presente. In tal caso, l'eliminazione non è consentita
        if (getForm().getEnteConvenzOrgList().getTable().size() == 1) {
            getMessageBox().addError(
                    "La struttura è associata ad un solo ente convenzionato. Non è possibile eseguire l'eliminazione");
        }

        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                        CostantiDB.TipoAplVGetValAppart.APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        if (Application.Publisher.CREA_STRUTTURA.equalsIgnoreCase(param.getNomePagina())) {
            StruttureForm form = (StruttureForm) SpagoliteLogUtil.getForm(this);
            param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(form, form.getEnteConvenzOrgList()));
        } else {
            param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
        }
        if (!getMessageBox().hasError() && idEnteConvenzOrg != null) {
            try {
                ambienteEjb.deleteEnteConvenzOrg(param, idEnteConvenzOrg);
                getForm().getEnteConvenzOrgList().getTable().remove(riga);

                getMessageBox().addInfo("Associazione all'ente convenzionato eliminata con successo");
                getMessageBox().setViewMode(MessageBox.ViewMode.plain);
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
        goBack();
    }

    @Override
    public void deleteEnteConvenzOrg() throws EMFError {
        deleteEnteConvenzOrgList();
    }

}
