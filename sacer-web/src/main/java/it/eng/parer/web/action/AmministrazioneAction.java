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

import it.eng.parer.amministrazioneStrutture.gestioneSistemaMigrazione.ejb.SistemaMigrazioneEjb;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.slite.gen.form.GestioneLogEventiForm;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.sacerlog.util.web.SpagoliteLogUtil;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.AmministrazioneAbstractAction;
import it.eng.parer.slite.gen.tablebean.AplParamApplicRowBean;
import it.eng.parer.slite.gen.tablebean.AplParamApplicTableBean;
import it.eng.parer.slite.gen.tablebean.AplParamApplicTableDescriptor;
import it.eng.parer.slite.gen.tablebean.AplSistemaMigrazRowBean;
import it.eng.parer.slite.gen.tablebean.AplSistemaMigrazTableBean;
import it.eng.parer.web.ejb.AmministrazioneEjb;
import it.eng.parer.web.helper.AmministrazioneHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Constants.ComboValueParamentersType;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.Secure;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.ejb.EJB;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmministrazioneAction extends AmministrazioneAbstractAction {

    private static Logger log = LoggerFactory.getLogger(AmministrazioneAction.class.getName());
    @EJB(mappedName = "java:app/Parer-ejb/AmministrazioneHelper")
    private AmministrazioneHelper amministrazioneHelper;

    @EJB(mappedName = "java:app/Parer-ejb/AmministrazioneEjb")
    private AmministrazioneEjb amministrazioneEjb;

    @EJB(mappedName = "java:app/Parer-ejb/SistemaMigrazioneEjb")
    private SistemaMigrazioneEjb sistemaMigrazioneEjb;

    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configHelper;

    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;

    @Override
    public String getControllerName() {
        return Application.Actions.AMMINISTRAZIONE;
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.AMMINISTRAZIONE_CONFIG_LIST;
    }

    /**
     * Carica la pagina di lista parametri SACER
     *
     * @throws EMFError errore generico
     */
    @Secure(action = "Menu.Amministrazione.ListaConfigurazioni")
    public void loadListaConfigurazioni() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Amministrazione.ListaConfigurazioni");
        getForm().getConfiguration().clear();
        getForm().getConfigurationList().clear();

        initConfigurationCombo();

        getForm().getConfiguration().getTi_param_applic_combo().setEditMode();
        getForm().getConfiguration().getTi_gestione_param_combo().setEditMode();
        getForm().getConfiguration().getFl_appart_applic_combo().setEditMode();
        getForm().getConfiguration().getFl_appart_ambiente_combo().setEditMode();
        getForm().getConfiguration().getFl_appart_strut_combo().setEditMode();
        getForm().getConfiguration().getFl_appart_tipo_unita_doc_combo().setEditMode();
        getForm().getConfiguration().getFl_appart_aa_tipo_fascicolo_combo().setEditMode();
        getForm().getConfiguration().getCd_versione_app_ini().setEditMode();
        getForm().getConfiguration().getCd_versione_app_fine().setEditMode();

        getForm().getConfiguration().getLoad_config_list().setEditMode();

        getForm().getConfigurationList().setHideInsertButton(false);
        getForm().getConfigurationList().setHideUpdateButton(false);
        getForm().getConfigurationList().setHideDeleteButton(false);
        getForm().getConfiguration().getLogEventiRegistroParametri().setEditMode();

        getForm().getConfigurationList().setFilterValidRecords(Boolean.TRUE);

        // Carico la lista dei configurazioni
        forwardToPublisher(Application.Publisher.AMMINISTRAZIONE_CONFIG_LIST);
    }

    /**
     * Carica la lista dei parametri in base ai filtri scelti
     *
     * @throws EMFError errore generico
     */
    @Override
    public void load_config_list() throws EMFError {
        // Recupero i valori dai filtri
        getForm().getConfiguration().post(getRequest());
        reloadConfigurationList();
    }

    private void reloadConfigurationList() throws EMFError {
        refreshConfigurationVersionFilters();
        String tiParamApplic = getForm().getConfiguration().getTi_param_applic_combo().parse();
        String tiGestioneParam = getForm().getConfiguration().getTi_gestione_param_combo().parse();
        String flAppartApplic = getForm().getConfiguration().getFl_appart_applic_combo().parse();
        String flAppartAmbiente = getForm().getConfiguration().getFl_appart_ambiente_combo()
                .parse();
        String flAppartStrut = getForm().getConfiguration().getFl_appart_strut_combo().parse();
        String flAppartTipoUnitaDoc = getForm().getConfiguration()
                .getFl_appart_tipo_unita_doc_combo().parse();
        String flAppartAaTipoFascicolo = getForm().getConfiguration()
                .getFl_appart_aa_tipo_fascicolo_combo().parse();
        String cdVersioneAppIni = getForm().getConfiguration().getCd_versione_app_ini().parse();
        String cdVersioneAppFine = getForm().getConfiguration().getCd_versione_app_fine().parse();

        getForm().getConfigurationList().getTi_gestione_param()
                .setDecodeMap(ComboGetter.getMappaTiGestioneParam());
        getForm().getConfigurationList().getTi_valore_param_applic()
                .setDecodeMap(ComboGetter.getTiValoreParamApplicCombo());

        // Carico i valori della lista configurazioni
        AplParamApplicTableBean paramApplicTableBean = amministrazioneEjb
                .getAplParamApplicTableBean(tiParamApplic, tiGestioneParam, flAppartApplic,
                        flAppartAmbiente, flAppartStrut, flAppartTipoUnitaDoc,
                        flAppartAaTipoFascicolo, cdVersioneAppIni, cdVersioneAppFine);

        paramApplicTableBean = obfuscatePasswordParamApplic(paramApplicTableBean);

        getForm().getConfigurationList().setTable(paramApplicTableBean);
        getForm().getConfigurationList().setStatus(Status.view);

        setConfigListReadOnly();

        forwardToPublisher(Application.Publisher.AMMINISTRAZIONE_CONFIG_LIST);
    }

    private void refreshConfigurationVersionFilters() throws EMFError {
        String selectedCdVersioneAppIni = getForm().getConfiguration().getCd_versione_app_ini()
                .getValue();
        String selectedCdVersioneAppFine = getForm().getConfiguration().getCd_versione_app_fine()
                .getValue();

        BaseTable cdVersioniAppIni = amministrazioneEjb.getCdVersioneAppIniBaseTable();
        DecodeMap mappaCdVersioniAppIni = DecodeMap.Factory.newInstance(cdVersioniAppIni,
                AplParamApplicTableDescriptor.COL_CD_VERSIONE_APP_INI,
                AplParamApplicTableDescriptor.COL_CD_VERSIONE_APP_INI);
        BaseTable cdVersioniAppFine = amministrazioneEjb.getCdVersioneAppFineBaseTable();
        DecodeMap mappaCdVersioniAppFine = DecodeMap.Factory.newInstance(cdVersioniAppFine,
                AplParamApplicTableDescriptor.COL_CD_VERSIONE_APP_FINE,
                AplParamApplicTableDescriptor.COL_CD_VERSIONE_APP_FINE);

        getForm().getConfiguration().getCd_versione_app_ini().setDecodeMap(mappaCdVersioniAppIni);
        getForm().getConfiguration().getCd_versione_app_fine().setDecodeMap(mappaCdVersioniAppFine);

        getForm().getConfiguration().getCd_versione_app_ini()
                .setValue(isValueInTable(cdVersioniAppIni,
                        AplParamApplicTableDescriptor.COL_CD_VERSIONE_APP_INI,
                        selectedCdVersioneAppIni) ? selectedCdVersioneAppIni : "");
        getForm().getConfiguration().getCd_versione_app_fine()
                .setValue(isValueInTable(cdVersioniAppFine,
                        AplParamApplicTableDescriptor.COL_CD_VERSIONE_APP_FINE,
                        selectedCdVersioneAppFine) ? selectedCdVersioneAppFine : "");
    }

    private boolean isValueInTable(BaseTable table, String columnName, String value) {
        if (StringUtils.isBlank(value)) {
            return true;
        }

        for (int index = 0; index < table.size(); index++) {
            BaseRowInterface row = table.getRow(index);
            if (value.equals(row.getString(columnName))) {
                return true;
            }
        }

        return false;
    }

    private void setConfigListReadOnly() {
        getForm().getConfigurationList().getTable().setPageSize(10);
        getForm().getConfigurationList().getTable().first();

        // Rendo non modificabili i campi della lista
        getForm().getConfigurationList().getTi_param_applic().setViewMode();
        getForm().getConfigurationList().getTi_gestione_param().setViewMode();
        getForm().getConfigurationList().getNm_param_applic().setViewMode();
        getForm().getConfigurationList().getDm_param_applic().setViewMode();
        getForm().getConfigurationList().getDs_param_applic().setViewMode();
        getForm().getConfigurationList().getTi_valore_param_applic().setViewMode();
        getForm().getConfigurationList().getDs_lista_valori_ammessi().setViewMode();
        getForm().getConfigurationList().getDs_valore_param_applic().setViewMode();
        getForm().getConfigurationList().getCd_versione_app_ini().setViewMode();
        getForm().getConfigurationList().getCd_versione_app_fine().setViewMode();
        getForm().getConfigurationList().getFl_multi().setEditMode();
        getForm().getConfigurationList().getFl_appart_applic().setEditMode();
        getForm().getConfigurationList().getFl_appart_ambiente().setEditMode();
        getForm().getConfigurationList().getFl_appart_strut().setEditMode();
        getForm().getConfigurationList().getFl_appart_tipo_unita_doc().setEditMode();
        getForm().getConfigurationList().getFl_appart_aa_tipo_fascicolo().setEditMode();
        getForm().getConfigurationList().getFl_multi().setReadonly(true);
        getForm().getConfigurationList().getFl_appart_applic().setReadonly(true);
        getForm().getConfigurationList().getFl_appart_ambiente().setReadonly(true);
        getForm().getConfigurationList().getFl_appart_strut().setReadonly(true);
        getForm().getConfigurationList().getFl_appart_tipo_unita_doc().setReadonly(true);
        getForm().getConfigurationList().getFl_appart_aa_tipo_fascicolo().setReadonly(true);
    }

    private AplParamApplicTableBean obfuscatePasswordParamApplic(
            AplParamApplicTableBean paramApplicTableBean) {
        Iterator<AplParamApplicRowBean> rowIt = paramApplicTableBean.iterator();
        while (rowIt.hasNext()) {
            AplParamApplicRowBean rowBean = rowIt.next();
            if (rowBean.getTiValoreParamApplic()
                    .equals(Constants.ComboValueParamentersType.PASSWORD.name())) {
                rowBean.setString("ds_valore_param_applic", Constants.OBFUSCATED_STRING);
            }
        }

        return paramApplicTableBean;
    }

    /**
     * Elimina un parametro dalla lista
     *
     * @throws EMFError errore generico
     */
    @Override
    public void deleteConfigurationList() throws EMFError {
        AplParamApplicRowBean row = (AplParamApplicRowBean) getForm().getConfigurationList()
                .getTable().getCurrentRow();
        int deletedRowIndex = getForm().getConfigurationList().getTable().getCurrentRowIndex();
        getForm().getConfigurationList().getTable().remove(deletedRowIndex);

        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));

        if (getLastPublisher().equals(Application.Publisher.CONFIGURATION_DETAIL)) {
            param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
        } else {
            param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                    getForm().getConfigurationList()));
        }
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());

        if (row.getIdParamApplic() != null
                && amministrazioneHelper.deleteAplParamApplicRowBean(param, row)) {
            getMessageBox().addInfo("Configurazione eliminata con successo");
            getMessageBox().setViewMode(ViewMode.plain);
            reloadConfigurationList();
            return;
        }
    }

    private boolean inValoriPossibili(String dsValoreParamApplicEnte, String dsListaValoriAmmessi) {
        String[] tokens = dsListaValoriAmmessi.split("\\|");
        Set<String> mySet = new HashSet<String>(Arrays.asList(tokens));
        return mySet.contains(dsValoreParamApplicEnte);
    }

    /**
     * Inizializza la combo dei tipi parametro
     *
     * @throws EMFError errore generico
     */
    private void initConfigurationCombo() throws EMFError {
        BaseTable tiParamApplic = amministrazioneEjb.getTiParamApplicBaseTable();
        DecodeMap mappaTiParamApplic = DecodeMap.Factory.newInstance(tiParamApplic,
                AplParamApplicTableDescriptor.COL_TI_PARAM_APPLIC,
                AplParamApplicTableDescriptor.COL_TI_PARAM_APPLIC);
        BaseTable cdVersioniAppIni = amministrazioneEjb.getCdVersioneAppIniBaseTable();
        DecodeMap mappaCdVersioniAppIni = DecodeMap.Factory.newInstance(cdVersioniAppIni,
                AplParamApplicTableDescriptor.COL_CD_VERSIONE_APP_INI,
                AplParamApplicTableDescriptor.COL_CD_VERSIONE_APP_INI);
        BaseTable cdVersioniAppFine = amministrazioneEjb.getCdVersioneAppFineBaseTable();
        DecodeMap mappaCdVersioniAppFine = DecodeMap.Factory.newInstance(cdVersioniAppFine,
                AplParamApplicTableDescriptor.COL_CD_VERSIONE_APP_FINE,
                AplParamApplicTableDescriptor.COL_CD_VERSIONE_APP_FINE);
        getForm().getConfiguration().getTi_param_applic_combo().setDecodeMap(mappaTiParamApplic);
        getForm().getConfiguration().getCd_versione_app_ini().setDecodeMap(mappaCdVersioniAppIni);
        getForm().getConfiguration().getCd_versione_app_fine().setDecodeMap(mappaCdVersioniAppFine);

        getForm().getConfiguration().getTi_gestione_param_combo()
                .setDecodeMap(ComboGetter.getMappaTiGestioneParam());
        getForm().getConfiguration().getFl_appart_applic_combo()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getConfiguration().getFl_appart_ambiente_combo()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getConfiguration().getFl_appart_strut_combo()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getConfiguration().getFl_appart_tipo_unita_doc_combo()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getConfiguration().getFl_appart_aa_tipo_fascicolo_combo()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
    }

    @Override
    public void initOnClick() throws EMFError {
    }

    @Override
    public void insertDettaglio() throws EMFError {
        if (getTableName().equals(getForm().getConfigurationList().getName())) {
            getForm().getConfigurationList().setStatus(Status.insert);
            getForm().getConfigurationDetail().clear();
            getForm().getConfigurationDetail().setStatus(Status.insert);
            getForm().getConfigurationDetail().setEditMode();
            // Inizializza le decode maps per i campi combo del detail
            getForm().getConfigurationDetail().getTi_gestione_param()
                    .setDecodeMap(ComboGetter.getMappaTiGestioneParam());
            getForm().getConfigurationDetail().getTi_valore_param_applic()
                    .setDecodeMap(ComboGetter.getTiValoreParamApplicCombo());
            forwardToPublisher(Application.Publisher.CONFIGURATION_DETAIL);
        } else if (getTableName().equals(getForm().getSistemiMigrazioneList().getName())) {
            getForm().getDettaglioSistemaMigrazione().clear();
            getForm().getDettaglioSistemaMigrazione().setEditMode();
            getForm().getDettaglioSistemaMigrazione().setStatus(Status.insert);
            getForm().getSistemiMigrazioneList().setStatus(Status.insert);
            forwardToPublisher(Application.Publisher.DETTAGLIO_SIS_MIGR);
        }
    }

    @Override
    public void loadDettaglio() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                || getNavigationEvent().equals(ListAction.NE_NEXT)
                || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getConfigurationList().getName())) {
                if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)) {
                    loadDettaglioConfiguration(true);
                } else {
                    loadDettaglioConfiguration(false);
                }
                // forwardToPublisher(Application.Publisher.CONFIGURATION_DETAIL);
            } else if (getTableName().equals(getForm().getSistemiMigrazioneList().getName())) {
                AplSistemaMigrazRowBean currentRow = (AplSistemaMigrazRowBean) getForm()
                        .getSistemiMigrazioneList().getTable().getCurrentRow();
                loadDettaglioSistemaMigrazione(currentRow.getIdSistemaMigraz());
            }
        }
    }

    private void loadDettaglioConfiguration(boolean editMode) throws EMFError {
        if (getForm().getConfigurationList().getTable() == null
                || getForm().getConfigurationList().getTable().isEmpty()) {
            return;
        }

        if (getForm().getConfigurationList().getTable().getCurrentRow() == null) {
            getForm().getConfigurationList().getTable().first();
        }

        // Inizializza le decode maps per i campi combo del detail
        getForm().getConfigurationDetail().getTi_gestione_param()
                .setDecodeMap(ComboGetter.getMappaTiGestioneParam());
        getForm().getConfigurationDetail().getTi_valore_param_applic()
                .setDecodeMap(ComboGetter.getTiValoreParamApplicCombo());

        AplParamApplicRowBean currentRow = (AplParamApplicRowBean) getForm().getConfigurationList()
                .getTable().getCurrentRow();
        if (currentRow.getIdParamApplic() != null) {
            AplParamApplicRowBean detailRow = amministrazioneEjb
                    .getAplParamApplicRowBean(currentRow.getIdParamApplic());
            if (detailRow != null) {
                // syncConfigurationCurrentRow(detailRow);
                getForm().getConfigurationDetail().copyFromBean(detailRow);
            }
        }

        if (editMode) {
            getForm().getConfigurationDetail().setStatus(Status.update);
            getForm().getConfigurationList().setStatus(Status.update);
            getForm().getConfigurationDetail().setEditMode();
        } else {
            getForm().getConfigurationList().setStatus(Status.view);
            getForm().getConfigurationDetail().setStatus(Status.view);
            getForm().getConfigurationDetail().setViewMode();
        }
    }

    private void syncConfigurationCurrentRow(AplParamApplicRowBean row) {
        if (row == null || getForm().getConfigurationList().getTable() == null
                || getForm().getConfigurationList().getTable().isEmpty()) {
            return;
        }

        AplParamApplicRowBean currentRow = (AplParamApplicRowBean) getForm().getConfigurationList()
                .getTable().getCurrentRow();
        if (currentRow == null) {
            return;
        }

        copyConfigurationRow(row, currentRow);
    }

    private void copyConfigurationRow(AplParamApplicRowBean source, AplParamApplicRowBean target) {
        target.setIdParamApplic(source.getIdParamApplic());
        target.setTiParamApplic(source.getTiParamApplic());
        target.setTiGestioneParam(source.getTiGestioneParam());
        target.setNmParamApplic(source.getNmParamApplic());
        target.setDmParamApplic(source.getDmParamApplic());
        target.setDsParamApplic(source.getDsParamApplic());
        target.setTiValoreParamApplic(source.getTiValoreParamApplic());
        target.setDsListaValoriAmmessi(source.getDsListaValoriAmmessi());
        target.setString("ds_valore_param_applic", source.getString("ds_valore_param_applic"));
        target.setCdVersioneAppIni(source.getCdVersioneAppIni());
        target.setCdVersioneAppFine(source.getCdVersioneAppFine());
        target.setFlMulti(source.getFlMulti());
        target.setFlAppartApplic(source.getFlAppartApplic());
        target.setFlAppartAmbiente(source.getFlAppartAmbiente());
        target.setFlAppartStrut(source.getFlAppartStrut());
        target.setFlAppartTipoUnitaDoc(source.getFlAppartTipoUnitaDoc());
        target.setFlAppartAaTipoFascicolo(source.getFlAppartAaTipoFascicolo());
    }

    private void loadDettaglioSistemaMigrazione(BigDecimal idSistemaMigraz) throws EMFError {
        AplSistemaMigrazRowBean detailRow = sistemaMigrazioneEjb
                .getAplSistemaMigrazRowBean(idSistemaMigraz);
        getForm().getDettaglioSistemaMigrazione().copyFromBean(detailRow);
        getForm().getSistemiMigrazioneList().setStatus(Status.view);
        getForm().getDettaglioSistemaMigrazione().setStatus(Status.view);
        getForm().getDettaglioSistemaMigrazione().setViewMode();
    }

    @Override
    public void undoDettaglio() throws EMFError {
        if (getLastPublisher().equals(Application.Publisher.CONFIGURATION_DETAIL)) {
            reloadConfigurationList();
        } else if (getLastPublisher().equals(Application.Publisher.DETTAGLIO_SIS_MIGR)
                && (getForm().getSistemiMigrazioneList().getStatus().equals(Status.update))) {
            BaseRowInterface currentRow = getForm().getSistemiMigrazioneList().getTable()
                    .getCurrentRow();
            BigDecimal idSistemaMigraz = currentRow.getBigDecimal("id_sistema_migraz");
            if (idSistemaMigraz != null) {
                loadDettaglioSistemaMigrazione(idSistemaMigraz);
            }
            forwardToPublisher(Application.Publisher.DETTAGLIO_SIS_MIGR);
        } else {
            goBack();
        }
    }

    @Override
    public void saveDettaglio() throws EMFError {
        if (getLastPublisher().equals(Application.Publisher.CONFIGURATION_DETAIL)) {
            saveConfigurationDettaglio();
            return;
        }

        if (getForm().getDettaglioSistemaMigrazione().postAndValidate(getRequest(),
                getMessageBox())) {
            try {
                if (!getMessageBox().hasError()) {
                    String nmSistemaMigraz = getForm().getDettaglioSistemaMigrazione()
                            .getNm_sistema_migraz().parse();
                    String dsSistemaMigraz = getForm().getDettaglioSistemaMigrazione()
                            .getDs_sistema_migraz().parse();

                    if (getForm().getSistemiMigrazioneList().getStatus().equals(Status.insert)) {
                        Long idSistemaMigraz = sistemaMigrazioneEjb
                                .saveSistemaMigrazione(nmSistemaMigraz, dsSistemaMigraz);
                        if (idSistemaMigraz != null) {
                            getForm().getDettaglioSistemaMigrazione().getId_sistema_migraz()
                                    .setValue(idSistemaMigraz.toString());
                        }
                        AplSistemaMigrazRowBean row = new AplSistemaMigrazRowBean();
                        getForm().getDettaglioSistemaMigrazione().copyToBean(row);
                        getForm().getSistemiMigrazioneList().getTable().last();
                        getForm().getSistemiMigrazioneList().getTable().add(row);
                    } else if (getForm().getSistemiMigrazioneList().getStatus()
                            .equals(Status.update)) {
                        BigDecimal idSistemaMigraz = getForm().getDettaglioSistemaMigrazione()
                                .getId_sistema_migraz().parse();
                        sistemaMigrazioneEjb.saveSistemaMigrazione(idSistemaMigraz, nmSistemaMigraz,
                                dsSistemaMigraz);
                    }

                    getForm().getDettaglioSistemaMigrazione().setViewMode();
                    getForm().getSistemiMigrazioneList().setStatus(Status.view);
                    getForm().getDettaglioSistemaMigrazione().setStatus(Status.view);
                    getMessageBox().addInfo("Sistema di migrazione salvato con successo");
                    getMessageBox().setViewMode(ViewMode.plain);

                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }

        }
        forwardToPublisher(Application.Publisher.DETTAGLIO_SIS_MIGR);
    }

    private void saveConfigurationDettaglio() throws EMFError {
        getMessageBox().clear();

        AplParamApplicRowBean row = (AplParamApplicRowBean) getForm().getConfigurationList()
                .getTable().getCurrentRow();
        if (row == null) {
            row = new AplParamApplicRowBean();
            getForm().getConfigurationList().getTable().add(row);
            getForm().getConfigurationList().getTable().last();
        }

        populateConfigurationDetailRowFromRequest(row);
        getForm().getConfigurationDetail().copyFromBean(row);

        validateConfigurationRow(row);

        if (!getMessageBox().hasError()) {
            try {
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configHelper
                                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                if (getForm().getConfigurationList().getStatus().equals(Status.insert)) {
                    if (getLastPublisher().equals(Application.Publisher.CONFIGURATION_DETAIL)) {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                    } else {
                        param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameInsert(getForm(),
                                getForm().getConfigurationList()));
                    }
                } else if (getForm().getConfigurationList().getStatus().equals(Status.update)) {
                    if (getLastPublisher().equals(Application.Publisher.CONFIGURATION_DETAIL)) {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                    } else {
                        param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameEdit(getForm(),
                                getForm().getConfigurationList()));
                    }
                }

                if (!amministrazioneEjb.saveConfiguration(row)) {
                    getMessageBox().addError("Errore durante il salvataggio della configurazione");
                }

                if (!getMessageBox().hasError()) {
                    sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                            param.getNomeUtente(), param.getNomeAzione(),
                            SacerLogConstants.TIPO_OGGETTO_REGISTRO_PARAMETRI, BigDecimal.ZERO,
                            param.getNomePagina());

                    AplParamApplicRowBean detailRow = amministrazioneEjb
                            .getAplParamApplicRowBean(row.getIdParamApplic());
                    if (detailRow != null) {
                        if (ComboValueParamentersType.PASSWORD.name()
                                .equals(detailRow.getTiValoreParamApplic())) {
                            detailRow.setString("ds_valore_param_applic",
                                    Constants.OBFUSCATED_STRING);
                        }
                        syncConfigurationCurrentRow(detailRow);
                        getForm().getConfigurationDetail().copyFromBean(detailRow);
                    }

                    getForm().getConfigurationDetail().setStatus(Status.view);
                    getForm().getConfigurationList().setStatus(Status.view);
                    getForm().getConfigurationDetail().setViewMode();
                    getMessageBox().addInfo("Configurazione salvata con successo");
                    getMessageBox().setViewMode(ViewMode.plain);
                }
            } catch (Exception ex) {
                log.error("Errore nel salvataggio della configurazione", ex);
                getMessageBox().addError("Errore durante il salvataggio della configurazione");
            }
        }

        forwardToPublisher(Application.Publisher.CONFIGURATION_DETAIL);
    }

    private void populateConfigurationDetailRowFromRequest(AplParamApplicRowBean row) {
        row.setIdParamApplic(parseBigDecimalParameter(
                getForm().getConfigurationDetail().getId_param_applic().getName()));
        row.setTiParamApplic(getRequest()
                .getParameter(getForm().getConfigurationDetail().getTi_param_applic().getName()));
        row.setTiGestioneParam(getRequest()
                .getParameter(getForm().getConfigurationDetail().getTi_gestione_param().getName()));
        row.setNmParamApplic(getRequest()
                .getParameter(getForm().getConfigurationDetail().getNm_param_applic().getName()));
        row.setDmParamApplic(getRequest()
                .getParameter(getForm().getConfigurationDetail().getDm_param_applic().getName()));
        row.setDsParamApplic(getRequest()
                .getParameter(getForm().getConfigurationDetail().getDs_param_applic().getName()));
        row.setTiValoreParamApplic(getRequest().getParameter(
                getForm().getConfigurationDetail().getTi_valore_param_applic().getName()));
        row.setDsListaValoriAmmessi(getRequest().getParameter(
                getForm().getConfigurationDetail().getDs_lista_valori_ammessi().getName()));
        row.setString("ds_valore_param_applic", getRequest().getParameter(
                getForm().getConfigurationDetail().getDs_valore_param_applic().getName()));
        row.setCdVersioneAppIni(getRequest().getParameter(
                getForm().getConfigurationDetail().getCd_versione_app_ini().getName()));
        row.setCdVersioneAppFine(getRequest().getParameter(
                getForm().getConfigurationDetail().getCd_versione_app_fine().getName()));
        row.setFlMulti(
                getCheckboxValue(getForm().getConfigurationDetail().getFl_multi().getName()));
        row.setFlAppartApplic(getCheckboxValue(
                getForm().getConfigurationDetail().getFl_appart_applic().getName()));
        row.setFlAppartAmbiente(getCheckboxValue(
                getForm().getConfigurationDetail().getFl_appart_ambiente().getName()));
        row.setFlAppartStrut(getCheckboxValue(
                getForm().getConfigurationDetail().getFl_appart_strut().getName()));
        row.setFlAppartTipoUnitaDoc(getCheckboxValue(
                getForm().getConfigurationDetail().getFl_appart_tipo_unita_doc().getName()));
        row.setFlAppartAaTipoFascicolo(getCheckboxValue(
                getForm().getConfigurationDetail().getFl_appart_aa_tipo_fascicolo().getName()));
    }

    private BigDecimal parseBigDecimalParameter(String parameterName) {
        String value = getRequest().getParameter(parameterName);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return new BigDecimal(value);
    }

    private String getCheckboxValue(String parameterName) {
        return getRequest().getParameter(parameterName) != null ? "1" : "0";
    }

    private boolean isDuplicateParamApplic(AplParamApplicRowBean row) {
        BigDecimal idParamApplic = null;
        if (getForm().getConfigurationList().getStatus().equals(Status.update)) {
            idParamApplic = row.getIdParamApplic();
        }
        return amministrazioneEjb.checkParamApplic(row.getNmParamApplic(), idParamApplic);
    }

    private void validateConfigurationRow(AplParamApplicRowBean row) {
        if (StringUtils.isBlank(row.getTiParamApplic())
                || StringUtils.isBlank(row.getTiGestioneParam())
                || StringUtils.isBlank(row.getNmParamApplic())
                || StringUtils.isBlank(row.getDsParamApplic())
                || StringUtils.isBlank(row.getTiValoreParamApplic())
                || StringUtils.isBlank(row.getCdVersioneAppIni())) {
            getMessageBox()
                    .addError("Almeno un parametro non ha tutti i campi obbligatori valorizzati");
            getMessageBox().setViewMode(ViewMode.plain);
        }

        String dsValoreParamApplicValue = row.getString("ds_valore_param_applic");
        if (StringUtils.isNotBlank(dsValoreParamApplicValue)
                && !"1".equals(row.getFlAppartApplic())) {
            getMessageBox().addError(
                    "Il valore del parametro può essere indicato solo se il parametro ha il flag Applicazione alzato");
            getMessageBox().setViewMode(ViewMode.plain);
        }

        if (isDuplicateParamApplic(row)) {
            getMessageBox().addError("Attenzione: parametro " + row.getNmParamApplic()
                    + " già presente nel sistema");
        }

        if (StringUtils.isNotBlank(row.getDsListaValoriAmmessi())
                && StringUtils.isNotBlank(dsValoreParamApplicValue)
                && !inValoriPossibili(dsValoreParamApplicValue, row.getDsListaValoriAmmessi())) {
            getMessageBox().addError(
                    "Il valore del parametro non è compreso tra i valori ammessi sul parametro");
        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                || getNavigationEvent().equals(ListAction.NE_NEXT)
                || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getConfigurationList().getName())) {
                forwardToPublisher(Application.Publisher.CONFIGURATION_DETAIL);
            } else if (getTableName().equals(getForm().getSistemiMigrazioneList().getName())) {
                forwardToPublisher(Application.Publisher.DETTAGLIO_SIS_MIGR);
            }
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        try {
            ricercaSistemiMigrazioneButton();
        } catch (EMFError ex) {
            getMessageBox().addError(ex.getDescription());
        }
    }

    /**
     * Carica la lista dei livelli di log
     *
     * @throws EMFError errore generico
     */
    @Secure(action = "Menu.Amministrazione.LivelliLogger")
    public void loadLoggerLevels() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Amministrazione.LivelliLogger");
        // FIXME: su JBOSS non funziona questa logica.
        // Una possibile soluzione (da testare in ambiente cluster) è reperibile su
        // https://rersvn.ente.regione.emr.it/projects/parer/wiki/Logging_profile
        boolean onJboss = true;
        getMessageBox().addWarning(
                "Attenzione: su jboss è possibile agire a runtime sui livelli di log del cluster. Al momento questa funzionalità è disabilitata.");
        if (onJboss) {
            return;
        }

        /**
         * Questa logica viene commentata per l'introduzione della implementazione dei log con SLF4J
         */
        // // Generate a list of all the loggers and levels
        // HashSet<String> loggers = new HashSet<String>();
        // ArrayList<String> levels = new ArrayList<String>();
        // HashMap<String, Object> loggersHM = new HashMap<String, Object>();
        //
        // // GetRootLogger
        // Logger rootLogger = LogManager.getRootLogger();
        // String rootLoggerName = rootLogger.getName();
        // loggers.add(rootLoggerName);
        // loggersHM.put(rootLoggerName, rootLogger);
        //
        // // All Other Loggers
        // Enumeration e = LogManager.getCurrentLoggers();
        // while (e.hasMoreElements()) {
        // Logger t1Logger = (Logger) e.nextElement();
        // // se il logger ha l'attributo di additività a false
        // // deve essere mostrato perchè non sente le modifiche al parent
        // if (!t1Logger.getAdditivity()) {
        // loggersHM.put(t1Logger.getName(), t1Logger);
        // loggers.add(t1Logger.getName());
        // }
        // if (loggers.add(t1Logger.getParent().getName())) {
        // loggersHM.put(t1Logger.getParent().getName(), t1Logger.getParent());
        // loggers.add(t1Logger.getParent().getName());
        // }
        // }
        //
        // String[] arrayNomi = loggers.toArray(new String[0]);
        // Arrays.sort(arrayNomi, new LengthComparator());
        //
        // for (String nome : arrayNomi) {
        // // Logger log4j
        // if (loggersHM.get(nome) instanceof Category) {
        // Category tmp = (Category) loggersHM.get(nome);
        // levels.add(tmp.getEffectiveLevel().toString());
        // } // Logger java.util.logging
        // else {
        // java.util.logging.Logger tmp = (java.util.logging.Logger) loggersHM.get(nome);
        // levels.add(tmp.getLevel() != null ? tmp.getLevel().toString() : "");
        //
        // }
        // }
        //
        // getRequest().setAttribute("loggers", arrayNomi);
        // getRequest().setAttribute("levels", levels);
        //
        // getForm().getBottoni().getApplica_livelli().setEditMode();
        // forwardToPublisher(Application.Publisher.CONFIGURAZIONE_LOGGER);
    }

    @Override
    public void ricercaSistemiMigrazioneButton() throws EMFError {
        // Esegue la post dei filtri compilati
        getForm().getFiltriRicercaSistemiMigrazione().post(getRequest());

        // Valida i filtri per verificare quelli obbligatori
        if (getForm().getFiltriRicercaSistemiMigrazione().validate(getMessageBox())) {
            // Valida in maniera pi\u00e0¹ specifica i dati
            String nmSistemaMigraz = getForm().getFiltriRicercaSistemiMigrazione()
                    .getNm_sistema_migraz_ric().parse();
            String dsSistemaMigraz = getForm().getFiltriRicercaSistemiMigrazione()
                    .getDs_sistema_migraz_ric().parse();

            if (!getMessageBox().hasError()) {
                AplSistemaMigrazTableBean sistemaMigrazTableBean = sistemaMigrazioneEjb
                        .getAplSistemaMigrazTableBean(nmSistemaMigraz, dsSistemaMigraz);
                getForm().getSistemiMigrazioneList().setTable(sistemaMigrazTableBean);
                getForm().getSistemiMigrazioneList().getTable().setPageSize(10);
                getForm().getSistemiMigrazioneList().getTable().first();
            }
        }
        forwardToPublisher(Application.Publisher.RICERCA_SIS_MIGR);
    }

    /**
     * Inner class che esegue la comparazione di stringhe verificando la lunghezza
     */
    class LengthComparator implements Comparator<String>, Serializable {

        @Override
        public int compare(String first, String second) {
            int result = 0;
            if (first.length() > second.length()) {
                result = 1;
            } else if (first.length() < second.length()) {
                result = -1;
            }
            return result;
        }
    }

    /**
     * Questa logica viene commentata per l'introduzione della implementazione dei log con SLF4J
     *
     * Esegue il salvataggio dei livelli
     *
     * @throws EMFError errore generico
     */
    @Override
    public void applica_livelli() throws EMFError {
        // String[] names = getRequest().getParameterValues("loggers");
        // for (int i = 0; i < names.length; i++) {
        // String thisLevel = getRequest().getParameter("loggerlevel_" + (i + 1));
        // if (names[i].startsWith("org.eclipse.persistence.session")) {
        // java.util.logging.Logger jpaLogger =
        // java.util.logging.LogManager.getLogManager().getLogger(names[i]);
        // jpaLogger.setLevel(!thisLevel.equals("") ? java.util.logging.Level.parse(thisLevel) :
        // null);
        // } else {
        // Level lev = Level.toLevel(thisLevel);
        // if (names[i].equalsIgnoreCase("root")) {
        // LogManager.getRootLogger().setLevel(lev);
        // }
        // Logger tmpLogger = Logger.getLogger(names[i]);
        // tmpLogger.setLevel(lev);
        // }
        // }
        // getMessageBox().addInfo("Livelli applicati con successo");
        // getMessageBox().setViewMode(ViewMode.plain);
        //
        // loadLoggerLevels();
    }

    @Override
    public void deleteSistemiMigrazioneList() throws EMFError {
        BaseRowInterface currentRow = getForm().getSistemiMigrazioneList().getTable()
                .getCurrentRow();
        BigDecimal idSistemaMigraz = currentRow.getBigDecimal("id_sistema_migraz");
        int riga = getForm().getSistemiMigrazioneList().getTable().getCurrentRowIndex();
        // Eseguo giusto un controllo per verificare che io stia prendendo la riga giusta se sono
        // nel dettaglio
        if (getLastPublisher().equals(Application.Publisher.DETTAGLIO_SIS_MIGR)) {
            if (!idSistemaMigraz.equals(
                    getForm().getDettaglioSistemaMigrazione().getId_sistema_migraz().parse())) {
                getMessageBox().addError(
                        "Eccezione imprevista nell'eliminazione del sistema di migrazione");
            }
        }

        if (!getMessageBox().hasError() && idSistemaMigraz != null) {
            try {
                if (!getMessageBox().hasError()) {
                    sistemaMigrazioneEjb.deleteAplSistemaMigraz(idSistemaMigraz);
                    getForm().getSistemiMigrazioneList().getTable().remove(riga);
                    getMessageBox().addInfo("Sistema di migrazione eliminato con successo");
                    getMessageBox().setViewMode(ViewMode.plain);
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
        if (!getMessageBox().hasError()
                && getLastPublisher().equals(Application.Publisher.DETTAGLIO_SIS_MIGR)) {
            goBackTo(Application.Publisher.RICERCA_SIS_MIGR);
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    /**
     * Carica la pagina di ricerca sistemi di migrazione
     *
     * @throws EMFError errore generico
     */
    @Secure(action = "Menu.Amministrazione.GestioneSistemiMigrazione")
    public void loadListaSistemiMigrazione() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Amministrazione.GestioneSistemiMigrazione");
        getForm().getSistemiMigrazioneList().clear();
        getForm().getFiltriRicercaSistemiMigrazione().clear();

        getForm().getFiltriRicercaSistemiMigrazione().setEditMode();

        forwardToPublisher(Application.Publisher.RICERCA_SIS_MIGR);
    }

    @Override
    public void updateSistemiMigrazioneList() throws EMFError {
        getForm().getDettaglioSistemaMigrazione().setEditMode();
        getForm().getSistemiMigrazioneList().setStatus(Status.update);
        getForm().getDettaglioSistemaMigrazione().setStatus(Status.update);
    }

    @Override
    public void logEventiRegistroParametri() throws EMFError {
        // BaseRowInterface bean = getForm().getConfigurationList().getTable().getCurrentRow();
        GestioneLogEventiForm form = new GestioneLogEventiForm();
        form.getOggettoDetail().getNmApp().setValue(
                configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC));
        form.getOggettoDetail().getNm_tipo_oggetto()
                .setValue(SacerLogConstants.TIPO_OGGETTO_REGISTRO_PARAMETRI);
        form.getOggettoDetail().getIdOggetto().setValue(BigDecimal.ZERO.toString());
        redirectToAction(it.eng.parer.sacerlog.slite.gen.Application.Actions.GESTIONE_LOG_EVENTI,
                "?operation=inizializzaLogEventi", form);
    }

    @Override
    public void filterInactiveRecordsConfigurationList() throws EMFError {
        int rowIndex = 0;
        int pageSize = WebConstants.DEFAULT_PAGE_SIZE;
        if (getForm().getConfigurationList().getTable() != null) {
            rowIndex = getForm().getConfigurationList().getTable().getCurrentRowIndex();
            pageSize = getForm().getConfigurationList().getTable().getPageSize();
        }

        getForm().getConfiguration().post(getRequest());
        String tiParamApplic = getForm().getConfiguration().getTi_param_applic_combo().parse();
        String tiGestioneParam = getForm().getConfiguration().getTi_gestione_param_combo().parse();
        String flAppartApplic = getForm().getConfiguration().getFl_appart_applic_combo().parse();
        String flAppartAmbiente = getForm().getConfiguration().getFl_appart_ambiente_combo()
                .parse();
        String flAppartStrut = getForm().getConfiguration().getFl_appart_strut_combo().parse();
        String flAppartTipoUnitaDoc = getForm().getConfiguration()
                .getFl_appart_tipo_unita_doc_combo().parse();
        String flAppartAaTipoFascicolo = getForm().getConfiguration()
                .getFl_appart_aa_tipo_fascicolo_combo().parse();
        String cdVersioneAppIni = getForm().getConfiguration().getCd_versione_app_ini().parse();
        String cdVersioneAppFine = getForm().getConfiguration().getCd_versione_app_fine().parse();

        // Carico i valori della lista configurazioni
        AplParamApplicTableBean paramApplicTableBean = amministrazioneEjb
                .getAplParamApplicTableBean(tiParamApplic, tiGestioneParam, flAppartApplic,
                        flAppartAmbiente, flAppartStrut, flAppartTipoUnitaDoc,
                        flAppartAaTipoFascicolo, cdVersioneAppIni, cdVersioneAppFine,
                        getForm().getConfigurationList().isFilterValidRecords());

        paramApplicTableBean = obfuscatePasswordParamApplic(paramApplicTableBean);

        getForm().getConfigurationList().setTable(paramApplicTableBean);

        setConfigListReadOnly();

        getForm().getConfigurationList().getTable().setCurrentRowIndex(rowIndex);
        getForm().getConfigurationList().getTable().setPageSize(pageSize);

        forwardToPublisher(Application.Publisher.AMMINISTRAZIONE_CONFIG_LIST);
    }

}
