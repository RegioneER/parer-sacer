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

import it.eng.parer.amministrazioneStrutture.gestioneRegistro.ejb.RegistroEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.annulVers.dto.RicercaRichAnnulVersBean;
import it.eng.parer.annulVers.ejb.AnnulVersEjb;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.firma.crypto.verifica.SpringTikaSingleton;
import it.eng.parer.firma.crypto.verifica.VerFormatiEnums;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.AnnulVersAbstractAction;
import it.eng.parer.slite.gen.form.FascicoliForm;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DmUdDelTableBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisItemRichAnnvrsRowBean;
import it.eng.parer.slite.gen.viewbean.AroVLisItemRichAnnvrsTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisStatoRichAnnvrsRowBean;
import it.eng.parer.slite.gen.viewbean.AroVLisStatoRichAnnvrsTableBean;
import it.eng.parer.slite.gen.viewbean.AroVRicRichAnnvrsRowBean;
import it.eng.parer.slite.gen.viewbean.AroVRicRichAnnvrsTableBean;
import it.eng.parer.slite.gen.viewbean.AroVVisRichAnnvrsRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisStatoRichAnnvrsRowBean;
import it.eng.parer.web.ejb.DataMartEjb;
import it.eng.parer.web.ejb.ElenchiVersamentoEjb;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.web.validator.TypeValidator;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.impl.ComboBox;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.MessageBox;
import it.eng.spagoLite.security.Secure;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import javax.ejb.EJB;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Bonora_L
 */
public class AnnulVersAction extends AnnulVersAbstractAction {

    private static final Logger log = LoggerFactory.getLogger(AnnulVersAction.class.getName());

    @EJB(mappedName = "java:app/Parer-ejb/SpringTikaSingleton")
    private SpringTikaSingleton tikaSingleton;
    @EJB(mappedName = "java:app/Parer-ejb/AnnulVersEjb")
    private AnnulVersEjb annulVersEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ElenchiVersamentoEjb")
    private ElenchiVersamentoEjb evEjb;
    @EJB(mappedName = "java:app/Parer-ejb/RegistroEjb")
    private RegistroEjb registroEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
    @EJB(mappedName = "java:app/Parer-ejb/DataMartEjb")
    private DataMartEjb dataMartEjb;

    @Override
    public void initOnClick() throws EMFError {
    }

    @Override
    public void insertDettaglio() throws EMFError {
        if (getTableName().equals(getForm().getRichAnnulVersList().getName())) {
            getForm().getCreazioneRichAnnulVers().reset();
            getForm().getCreazioneRichAnnulVers().setEditMode();
            getForm().getCreazioneRichAnnulVers().getFl_forza_annul()
                    .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
            getForm().getCreazioneRichAnnulVers().getFl_immediata().setValue("0");
            getSession().setAttribute(
                    getForm().getCreazioneRichAnnulVers().getFl_immediata().getName(), "0");

            if (getSession().getAttribute("tiRichAnnulVers") != null) {
                if (((String) getSession().getAttribute("tiRichAnnulVers")).equals("UNITA_DOC")) {
                    getForm().getCreazioneRichAnnulVers().getTi_rich_annul_vers()
                            .setValue("UNITA_DOC");
                } else if (((String) getSession().getAttribute("tiRichAnnulVers"))
                        .equals("FASCICOLI")) {
                    getForm().getCreazioneRichAnnulVers().getTi_rich_annul_vers()
                            .setValue("FASCICOLI");
                }
            }

            forwardToPublisher(Application.Publisher.CREAZIONE_RICH_ANNUL_VERS);
        } else if (getTableName().equals(getForm().getItemList().getName())) {
            final BigDecimal idRichAnnulVers = getForm().getRichAnnulVersDetail()
                    .getId_rich_annul_vers().parse();
            final String tiRichAnnulVers = getForm().getRichAnnulVersDetail()
                    .getTi_rich_annul_vers().parse();
            if (!annulVersEjb.checkStatoRichiesta(idRichAnnulVers,
                    CostantiDB.StatoRichAnnulVers.APERTA.name())) {
                getMessageBox().addError(
                        "Non \u00E8 possibile aggiungere unit\u00E0 documentarie/fascicoli alla richiesta in quanto ha stato corrente diverso da APERTA");
                forwardToPublisher(getLastPublisher());
            } else {
                if (tiRichAnnulVers.equals("UNITA_DOC")) {
                    // Inizializzo la form per andare alla ricerca ud avanzata inserendo i valori
                    // trovati
                    UnitaDocumentarieForm unitaDocumentarieForm = new UnitaDocumentarieForm();
                    unitaDocumentarieForm.getUnitaDocumentariePerRichAnnulVers()
                            .getId_rich_annul_vers().setValue(idRichAnnulVers.toPlainString());

                    redirectToAction(Application.Actions.UNITA_DOCUMENTARIE,
                            "?operation=unitaDocumentarieRicercaAvanzata", unitaDocumentarieForm);
                } else if (tiRichAnnulVers.equals("FASCICOLI")) {
                    // Inizializzo la form per andare alla ricerca fascicoli inserendo i valori
                    // trovati
                    FascicoliForm fascicoliForm = new FascicoliForm();
                    fascicoliForm.getFascicoliPerRichAnnulVers().getId_rich_annul_vers()
                            .setValue(idRichAnnulVers.toPlainString());

                    redirectToAction(Application.Actions.FASCICOLI,
                            "?operation=fascicoliRicercaSemplice", fascicoliForm);
                }
            }
        }
    }

    @Override
    public void loadDettaglio() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                || getNavigationEvent().equals(ListAction.NE_NEXT)
                || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getRichAnnulVersList().getName())) {
                AroVRicRichAnnvrsRowBean row = (AroVRicRichAnnvrsRowBean) getForm()
                        .getRichAnnulVersList().getTable().getCurrentRow();
                BigDecimal idRichAnnulVers = row.getIdRichAnnulVers();

                loadDettaglioRichiesta(idRichAnnulVers);
                getForm().getRichAnnulVersDetailSubTabs()
                        .setCurrentTab(getForm().getRichAnnulVersDetailSubTabs().getListaItem());
                getForm().getRichAnnulVersDetail().setViewMode();
                getForm().getRichAnnulVersDetail().getBl_file().setEditMode();
                getForm().getRichAnnulVersList().setStatus(Status.view);
            } else if (getTableName().equals(getForm().getStatiList().getName())) {
                AroVLisStatoRichAnnvrsRowBean currentRow = (AroVLisStatoRichAnnvrsRowBean) getForm()
                        .getStatiList().getTable().getCurrentRow();
                AroVVisStatoRichAnnvrsRowBean aroVVisStatoRichAnnvrsRowBean = annulVersEjb
                        .geAroVVisStatoRichAnnvrsRowBean(currentRow.getIdStatoRichAnnulVers());
                getForm().getStatoRichAnnulVersDetail().copyFromBean(aroVVisStatoRichAnnvrsRowBean);

                getForm().getStatoRichAnnulVersDetail().setViewMode();
                getForm().getStatiList().setStatus(Status.view);
            }
        }
    }

    private void loadDettaglioRichiesta(BigDecimal idRichAnnulVers) throws EMFError {
        AroVVisRichAnnvrsRowBean detailRow = annulVersEjb
                .getAroVVisRichAnnvrsRowBean(idRichAnnulVers);
        getForm().getRichAnnulVersDetail().getFl_forza_annul_combo()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getRichAnnulVersDetail().copyFromBean(detailRow);

        AroVLisStatoRichAnnvrsTableBean listaStati = annulVersEjb
                .getAroVLisStatoRichAnnvrsTableBean(idRichAnnulVers);
        getForm().getStatiList().setTable(listaStati);
        getForm().getStatiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getStatiList().getTable().first();

        AroVLisItemRichAnnvrsTableBean listaItem = annulVersEjb
                .getAroVLisItemRichAnnvrsTableBean(idRichAnnulVers);
        getForm().getItemList().setTable(listaItem);
        getForm().getItemList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getItemList().getTable().first();

        // 39187: recupero gli eventuali item cancellati
        DmUdDelTableBean listaItemCancellati = dataMartEjb.getDmUdDelAnnulVersTableBean(
                idRichAnnulVers, CostantiDB.TiStatoUdCancellate.CANCELLATA_DB_SACER.name());
        getForm().getItemCancellatiList().setTable(listaItemCancellati);
        getForm().getItemCancellatiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getItemCancellatiList().getTable().first();

        getForm().getItemList().setHideInsertButton(true);
        getForm().getItemList().setHideDeleteButton(true);
        getForm().getItemList().setHideDetailButton(true);

        getForm().getStatiList().setHideDetailButton(false);
        getForm().getStatiList().setHideUpdateButton(false);

        getForm().getRichAnnulVersList().setHideUpdateButton(true);
        getForm().getRichAnnulVersList().setHideDeleteButton(true);

        getForm().getRichAnnulVersDetailButtonList().getUploadFile().setHidden(true);
        getForm().getRichAnnulVersDetail().getBl_file().setHidden(true);
        getForm().getRichAnnulVersDetail().getBl_file().setEditMode();

        getForm().getRichAnnulVersDetailButtonList().setEditMode();
        getForm().getRichAnnulVersDetailButtonList().hideAll();

        if (annulVersEjb.checkStatoRichiesta(idRichAnnulVers,
                CostantiDB.StatoRichAnnulVers.APERTA.name())) {
            getForm().getRichAnnulVersList().setHideUpdateButton(false);
            getForm().getRichAnnulVersList().setHideDeleteButton(false);

            getForm().getRichAnnulVersDetailButtonList().getRifiutaRichiesta().setHidden(false);
            getForm().getRichAnnulVersDetailButtonList().getControllaRichiesta().setHidden(false);

            if (listaItem.isEmpty() && detailRow.getTiCreazioneRichAnnulVers()
                    .equals(CostantiDB.TipoCreazioneRichAnnulVers.ON_LINE.name())) {
                getForm().getRichAnnulVersDetail().getBl_file().setHidden(false);
                getForm().getRichAnnulVersDetailButtonList().getUploadFile().setHidden(false);
            }

            getForm().getItemList().setHideInsertButton(false);
            getForm().getItemList().setHideDeleteButton(false);
            getForm().getItemList().setHideDetailButton(false);

            // se nella richiesta non sono presenti item con stato = DA_ANNULLARE_IN_PING ed è
            // presente almeno un item
            // con stato = DA_ANNULLARE_IN_SACER
            if (!annulVersEjb.checkStatoItems(idRichAnnulVers,
                    CostantiDB.StatoItemRichAnnulVers.DA_ANNULLARE_IN_PING.name())
                    && annulVersEjb.checkStatoItems(idRichAnnulVers,
                            CostantiDB.StatoItemRichAnnulVers.DA_ANNULLARE_IN_SACER.name())) {
                getForm().getRichAnnulVersDetailButtonList().getChiudiRichiesta().setHidden(false);
            }
        } else if (annulVersEjb.checkStatoRichiesta(idRichAnnulVers,
                CostantiDB.StatoRichAnnulVers.CHIUSA.name())) {
            getForm().getRichAnnulVersList().setHideDeleteButton(false);
        }
    }

    @Override
    public void undoDettaglio() throws EMFError {
        if (getLastPublisher().equals(Application.Publisher.RICH_ANNUL_VERS_DETAIL)) {
            AroVRicRichAnnvrsRowBean row = (AroVRicRichAnnvrsRowBean) getForm()
                    .getRichAnnulVersList().getTable().getCurrentRow();
            BigDecimal idRichAnnulVers = row.getIdRichAnnulVers();
            if (idRichAnnulVers != null) {
                loadDettaglioRichiesta(idRichAnnulVers);
            }
            getForm().getRichAnnulVersDetailSubTabs()
                    .setCurrentTab(getForm().getRichAnnulVersDetailSubTabs().getListaItem());
            getForm().getRichAnnulVersDetail().setViewMode();
            getForm().getRichAnnulVersDetail().getBl_file().setEditMode();
            getForm().getRichAnnulVersList().setStatus(Status.view);

            forwardToPublisher(Application.Publisher.RICH_ANNUL_VERS_DETAIL);
        } else {
            goBack();
        }
    }

    @Override
    public void saveDettaglio() throws EMFError {
        if (getTableName().equals(getForm().getRichAnnulVersList().getName())) {
            if (getForm().getRichAnnulVersDetail().validate(getMessageBox())) {
                BigDecimal idStrut = getForm().getRichAnnulVersDetail().getId_strut().parse();
                BigDecimal idRichAnnulVers = getForm().getRichAnnulVersDetail()
                        .getId_rich_annul_vers().parse();
                String cdRichAnnulVers = getForm().getRichAnnulVersDetail().getCd_rich_annul_vers()
                        .parse();
                String dsRichAnnulVers = getForm().getRichAnnulVersDetail().getDs_rich_annul_vers()
                        .parse();
                String ntRichAnnulVers = getForm().getRichAnnulVersDetail().getNt_rich_annul_vers()
                        .parse();
                String flForzaAnnul = getForm().getRichAnnulVersDetail().getFl_forza_annul_combo()
                        .parse();

                try {
                    annulVersEjb.saveRichAnnulVers(idRichAnnulVers, cdRichAnnulVers,
                            dsRichAnnulVers, ntRichAnnulVers, flForzaAnnul, idStrut);
                    getMessageBox().addInfo("Richiesta modificata con successo");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);

                    loadDettaglioRichiesta(idRichAnnulVers);
                    getForm().getRichAnnulVersList().setStatus(BaseElements.Status.view);
                    getForm().getRichAnnulVersDetail().setViewMode();
                    getForm().getRichAnnulVersDetail().getBl_file().setEditMode();
                } catch (ParerUserError ex) {
                    log.error(ex.getDescription(), ex);
                    getMessageBox().addError(ex.getDescription());
                }
                forwardToPublisher(Application.Publisher.RICH_ANNUL_VERS_DETAIL);
            }
        } else if (getTableName().equals(getForm().getStatiList().getName())) {
            getForm().getStatoRichAnnulVersDetail().getDs_nota_rich_annul_vers().post(getRequest());
            Message validate = getForm().getStatoRichAnnulVersDetail().getDs_nota_rich_annul_vers()
                    .validate();
            if (validate == null) {
                BigDecimal idStato = getForm().getStatoRichAnnulVersDetail()
                        .getId_stato_rich_annul_vers().parse();
                String dsNota = getForm().getStatoRichAnnulVersDetail().getDs_nota_rich_annul_vers()
                        .parse();
                try {
                    if (idStato != null) {
                        annulVersEjb.saveStatoRichAnnulVers(idStato, dsNota);

                        getMessageBox().addInfo("Stato salvato con successo");
                        getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                    }
                } catch (ParerUserError ex) {
                    getMessageBox().addError(
                            "Lo stato non pu\u00F2 essere salvato: " + ex.getDescription());
                }
            } else {
                getMessageBox().addMessage(validate);
            }
            // Salvataggio stato
            if (!getMessageBox().hasError()) {
                getForm().getStatiList().setStatus(Status.view);
                getForm().getStatoRichAnnulVersDetail().setViewMode();
            }
            forwardToPublisher(Application.Publisher.STATO_RICH_ANNUL_VERS_DETAIL);
        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_NEXT)
                || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getRichAnnulVersList().getName())) {
                forwardToPublisher(Application.Publisher.RICH_ANNUL_VERS_DETAIL);
            } else if (getTableName().equals(getForm().getItemList().getName())) {
                // Verifica l'esistenza dell'UD e passa a UnitaDocumentarieAction
                AroVLisItemRichAnnvrsRowBean rowBean = (AroVLisItemRichAnnvrsRowBean) getForm()
                        .getItemList().getTable().getCurrentRow();
                if (rowBean.getIdUnitaDoc() != null) {
                    AroVLisItemRichAnnvrsTableBean listaItem = (AroVLisItemRichAnnvrsTableBean) getForm()
                            .getItemList().getTable();
                    UnitaDocumentarieForm form = new UnitaDocumentarieForm();
                    form.getUnitaDocumentarieList().setTable(listaItem);
                    redirectToAction(Application.Actions.UNITA_DOCUMENTARIE,
                            "?operation=listNavigationOnClick&navigationEvent="
                                    + ListAction.NE_DETTAGLIO_VIEW + "&table="
                                    + UnitaDocumentarieForm.UnitaDocumentarieList.NAME + "&riga="
                                    + listaItem.getCurrentRowIndex(),
                            form);
                } else {
                    getMessageBox().addError(
                            "Versamento relativo a una unit\u00E0 documentaria inesistente");
                    forwardToPublisher(getLastPublisher());
                }
            } else if (getTableName().equals(getForm().getStatiList().getName())) {
                forwardToPublisher(Application.Publisher.STATO_RICH_ANNUL_VERS_DETAIL);
            }
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        if (getLastPublisher().equals(Application.Publisher.RICH_ANNUL_VERS_DETAIL)) {
            goBackTo(getDefaultPublsherName());
        } else {
            goBack();
        }
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.RICERCA_RICH_ANNUL_VERS;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        try {
            if (publisherName.equals(Application.Publisher.RICERCA_RICH_ANNUL_VERS)) {
                int rowIndex;
                int pageSize;
                RicercaRichAnnulVersBean filtri = new RicercaRichAnnulVersBean(
                        getForm().getFiltriRicercaRichAnnullVers());
                if (!getMessageBox().hasError()) {
                    AroVRicRichAnnvrsTableBean table = annulVersEjb
                            .getAroVRicRichAnnvrsTableBean(getUser().getIdUtente(), filtri);
                    if (getForm().getRichAnnulVersList().getTable() != null) {
                        rowIndex = getForm().getRichAnnulVersList().getTable().getCurrentRowIndex();
                        pageSize = getForm().getRichAnnulVersList().getTable().getPageSize();
                    } else {
                        rowIndex = 0;
                        pageSize = WebConstants.DEFAULT_PAGE_SIZE;
                    }
                    getForm().getRichAnnulVersList().setTable(table);
                    getForm().getRichAnnulVersList().getTable().setPageSize(pageSize);
                    getForm().getRichAnnulVersList().getTable().setCurrentRowIndex(rowIndex);
                }
                getForm().getRichAnnulVersList().setHideUpdateButton(false);
                getForm().getRichAnnulVersList().setHideDeleteButton(false);
            } else if (publisherName.equals(Application.Publisher.RICH_ANNUL_VERS_DETAIL)) {
                BigDecimal idRichAnnulVers = getForm().getRichAnnulVersDetail()
                        .getId_rich_annul_vers().parse();
                if (idRichAnnulVers != null) {
                    loadDettaglioRichiesta(idRichAnnulVers);
                }
                getForm().getRichAnnulVersDetailSubTabs()
                        .setCurrentTab(getForm().getRichAnnulVersDetailSubTabs().getListaItem());
                getForm().getRichAnnulVersDetail().setViewMode();
                getForm().getRichAnnulVersDetail().getBl_file().setEditMode();
                getForm().getRichAnnulVersList().setStatus(Status.view);
            }
        } catch (EMFError e) {
            log.error("Errore nel ricaricamento della pagina " + publisherName, e);
            getMessageBox().addError("Errore nel ricaricamento della pagina " + publisherName);
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.ANNUL_VERS;
    }

    @Override
    public void process() throws EMFError {
        boolean isMultipart = ServletFileUpload.isMultipartContent(getRequest());
        if (isMultipart) {
            int size10Mb = 10 * WebConstants.FILESIZE * WebConstants.FILESIZE;
            String[] paramMethods = null;
            try {
                if (getLastPublisher().equals(Application.Publisher.CREAZIONE_RICH_ANNUL_VERS)) {
                    paramMethods = getForm().getCreazioneRichAnnulVers().postMultipart(getRequest(),
                            size10Mb);
                } else if (getLastPublisher()
                        .equals(Application.Publisher.RICH_ANNUL_VERS_DETAIL)) {
                    paramMethods = getForm().getRichAnnulVersDetail().postMultipart(getRequest(),
                            size10Mb);
                }
                if (paramMethods != null) {
                    String operationMethod = paramMethods[0];
                    if (paramMethods.length > 1) {
                        String[] navigationParams = Arrays.copyOfRange(paramMethods, 1,
                                paramMethods.length);

                        Method method = AnnulVersAction.class.getMethod(operationMethod,
                                String[].class);
                        method.invoke(this, (Object) navigationParams);
                    } else {
                        Method method = AnnulVersAction.class.getMethod(operationMethod);
                        method.invoke(this);
                    }
                }

            } catch (FileUploadException | NoSuchMethodException | SecurityException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                log.error("Errore nell'invocazione del metodo :"
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                getMessageBox().addError("Errore inatteso nella procedura invocata");
                forwardToPublisher(getLastPublisher());
            }
        }
    }

    @Override
    public void deleteRichAnnulVersList() throws EMFError {
        AroVRicRichAnnvrsRowBean row = (AroVRicRichAnnvrsRowBean) getForm().getRichAnnulVersList()
                .getTable().getCurrentRow();
        int riga = getForm().getRichAnnulVersList().getTable().getCurrentRowIndex();
        BigDecimal idRichAnnulVers = row.getIdRichAnnulVers();
        if (!annulVersEjb.checkStatoRichiesta(idRichAnnulVers,
                CostantiDB.StatoRichAnnulVers.APERTA.name(),
                CostantiDB.StatoRichAnnulVers.CHIUSA.name())) {
            getMessageBox().addError(
                    "La richiesta non \u00E8 eliminabile perch\u00E9 ha stato corrente diverso da APERTA o CHIUSA");
        } else {
            // Elimina richiesta
            try {
                annulVersEjb.deleteRichAnnulVers(idRichAnnulVers);

                getForm().getRichAnnulVersList().getTable().remove(riga);
                getMessageBox().addInfo("Richiesta eliminata con successo");
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
        if (!getMessageBox().hasError()
                && getLastPublisher().equals(Application.Publisher.RICH_ANNUL_VERS_DETAIL)) {
            goBackTo(Application.Publisher.RICERCA_RICH_ANNUL_VERS);
        } else {
            getForm().getRichAnnulVersList().setHideUpdateButton(false);
            getForm().getRichAnnulVersList().setHideDeleteButton(false);

            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void updateRichAnnulVersList() throws EMFError {
        AroVRicRichAnnvrsRowBean row = (AroVRicRichAnnvrsRowBean) getForm().getRichAnnulVersList()
                .getTable().getCurrentRow();
        BigDecimal idRichAnnulVers = row.getIdRichAnnulVers();
        if (!annulVersEjb.checkStatoRichiesta(idRichAnnulVers,
                CostantiDB.StatoRichAnnulVers.APERTA.name())) {
            getForm().getRichAnnulVersList().setHideUpdateButton(false);
            getForm().getRichAnnulVersList().setHideDeleteButton(false);

            getMessageBox().addError(
                    "La richiesta non \u00E8 modificabile perch\u00E9 ha stato corrente diverso da APERTA");
            forwardToPublisher(getLastPublisher());
        } else {
            // Modifica richiesta
            getForm().getRichAnnulVersList().setStatus(Status.update);

            getForm().getRichAnnulVersDetail().getCd_rich_annul_vers().setEditMode();
            getForm().getRichAnnulVersDetail().getDs_rich_annul_vers().setEditMode();
            getForm().getRichAnnulVersDetail().getNt_rich_annul_vers().setEditMode();
            getForm().getRichAnnulVersDetail().getFl_forza_annul_combo().setEditMode();

            getForm().getRichAnnulVersDetailButtonList().hideAll();
            getForm().getItemList().setHideInsertButton(true);
            getForm().getItemList().setHideDeleteButton(true);
            getForm().getItemList().setHideDetailButton(true);

            getForm().getStatiList().setHideDetailButton(true);
            getForm().getStatiList().setHideUpdateButton(true);

            forwardToPublisher(Application.Publisher.RICH_ANNUL_VERS_DETAIL);
        }
    }

    @Override
    public void deleteItemList() throws EMFError {
        AroVLisItemRichAnnvrsRowBean row = (AroVLisItemRichAnnvrsRowBean) getForm().getItemList()
                .getTable().getCurrentRow();
        int riga = getForm().getItemList().getTable().getCurrentRowIndex();
        BigDecimal idItemRichAnnulVers = row.getIdItemRichAnnulVers();
        BigDecimal idRichAnnulVers = row.getIdRichAnnulVers();
        if (!annulVersEjb.checkStatoRichiesta(idRichAnnulVers,
                CostantiDB.StatoRichAnnulVers.APERTA.name())) {
            getMessageBox().addError(
                    "Il versamento non \u00E8 eliminabile perch\u00E9 la richiesta ha stato corrente diverso da APERTA");
        } else {
            // Elimina richiesta
            try {
                annulVersEjb.deleteItemRichAnnulVers(idRichAnnulVers, idItemRichAnnulVers);

                getForm().getItemList().getTable().remove(riga);
                getMessageBox().addInfo("Versamento eliminato con successo");
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void updateStatiList() throws EMFError {
        AroVLisStatoRichAnnvrsRowBean currentRow = (AroVLisStatoRichAnnvrsRowBean) getForm()
                .getStatiList().getTable().getCurrentRow();
        if (!currentRow.getNmUserid().equals(getUser().getUsername())) {
            getMessageBox().addError(
                    "L'utente non pu\u00F2 modificare lo stato perch\u00E9 non corrisponde all'utente che lo ha registrato");
            forwardToPublisher(getLastPublisher());
        } else {
            getForm().getStatoRichAnnulVersDetail().getDs_nota_rich_annul_vers().setEditMode();
            getForm().getStatiList().setStatus(Status.update);

            forwardToPublisher(Application.Publisher.STATO_RICH_ANNUL_VERS_DETAIL);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="UI Creazione richiesta annullamento versamenti">
    @Override
    public void creaRichAnnulVers() throws EMFError {
        // Metodo richiamato dal metodo process in quanto richiesta MultiPart
        getForm().getCreazioneRichAnnulVers().validate(getMessageBox());
        String flImmediata = null;
        try {
            byte[] fileByteArray = null;
            final String cdRichAnnulVers = getForm().getCreazioneRichAnnulVers()
                    .getCd_rich_annul_vers().parse();
            final String dsRichAnnulVers = getForm().getCreazioneRichAnnulVers()
                    .getDs_rich_annul_vers().parse();
            final String ntRichAnnulVers = getForm().getCreazioneRichAnnulVers()
                    .getNt_rich_annul_vers().parse();
            final String tiRichAnnulVers = getForm().getCreazioneRichAnnulVers()
                    .getTi_rich_annul_vers().parse();

            final String flForzaAnnul = getForm().getCreazioneRichAnnulVers().getFl_forza_annul()
                    .parse();
            final String tiAnnullamento = getForm().getCreazioneRichAnnulVers().getTi_annullamento()
                    .getValue() == null
                    || !getForm().getCreazioneRichAnnulVers().getTi_annullamento().getValue()
                            .equals("CANCELLAZIONE") ? "ANNULLAMENTO_VERSAMENTO" : "CANCELLAZIONE";

            flImmediata = (String) getSession().getAttribute(
                    getForm().getCreazioneRichAnnulVers().getFl_immediata().getName());
            if (!getMessageBox().hasError() && StringUtils.isBlank(flImmediata)) {
                getMessageBox().addError("Campo '"
                        + getForm().getCreazioneRichAnnulVers().getFl_immediata().getName()
                        + "' obbligatorio");
            }
            final String blFile = getForm().getCreazioneRichAnnulVers().getBl_file().parse();
            // if (!getMessageBox().hasError() && flImmediata.equals("1") && blFile == null) {
            // getMessageBox().addError(
            // "Siccome la richiesta prevede l'annullamento immediato, deve essere definito il file
            // di cui fare upload
            // che elenca i versamenti da annullare");
            // }
            if (!getMessageBox().hasError() && blFile != null) {
                fileByteArray = getForm().getCreazioneRichAnnulVers().getBl_file().getFileBytes();
                String mime = tikaSingleton.detectMimeType(fileByteArray);
                if (!mime.equals(VerFormatiEnums.CSV_MIME)
                        && !mime.equals(VerFormatiEnums.TEXT_PLAIN_MIME)) {
                    getMessageBox().addError(
                            "Il formato del file caricato non corrisponde al tipo testo/csv");
                } else {
                    if (tiRichAnnulVers.equals("UNITA_DOC")
                            && !annulVersEjb.checkCsvHeaders(fileByteArray)) {
                        getMessageBox().addError(
                                "Il primo record del file non definisce i nomi dei campi previsti (\"REGISTRO\", \"ANNO\" e \"NUMERO\")");
                    }
                    if (tiRichAnnulVers.equals("FASCICOLI")
                            && !annulVersEjb.checkCsvHeadersFasc(fileByteArray)) {
                        getMessageBox().addError(
                                "Il primo record del file non definisce i nomi dei campi previsti (\"ANNO\" e \"NUMERO\")");
                    }
                }
            }
            if (!getMessageBox().hasError()) {
                if (annulVersEjb.checkCdRichAnnulVersExisting(cdRichAnnulVers,
                        getUser().getIdOrganizzazioneFoglia())) {
                    getMessageBox().addError(
                            "Nella struttura versante corrente \u00E8 gi\u00E0 presente una richiesta di annullamento versamenti con lo stesso codice");
                }
            }
            if (!getMessageBox().hasError()) {
                // Salvo la richiesta di annullamento versamento
                Long idRichAnnulVers = annulVersEjb.saveRichAnnulVers(getUser().getIdUtente(),
                        cdRichAnnulVers, dsRichAnnulVers, ntRichAnnulVers, flImmediata,
                        fileByteArray, getUser().getIdOrganizzazioneFoglia(), flForzaAnnul,
                        tiAnnullamento, tiRichAnnulVers);
                //
                if (idRichAnnulVers != null && flImmediata.equals("1")) {
                    // Determino l'utente che ha creato la richiesta (ovvero il primo stato). In
                    // questo caso è l'utente
                    // corrente
                    annulVersEjb.elaboraRichiestaAnnullamento(idRichAnnulVers,
                            getUser().getIdUtente(), Constants.ANNULLAMENTO_ONLINE);
                }
                if (idRichAnnulVers != null) {
                    AroVRicRichAnnvrsRowBean tmpRow = new AroVRicRichAnnvrsRowBean();
                    tmpRow.setIdRichAnnulVers(new BigDecimal(idRichAnnulVers));
                    if (getForm().getRichAnnulVersList().getTable() == null
                            || getForm().getRichAnnulVersList().getTable().isEmpty()) {
                        getForm().getRichAnnulVersList().setTable(new AroVRicRichAnnvrsTableBean());
                        getForm().getRichAnnulVersList().getTable().add(tmpRow);
                    } else {
                        getForm().getRichAnnulVersList().getTable().last();
                        getForm().getRichAnnulVersList().getTable().add(tmpRow);
                    }
                    loadDettaglioRichiesta(new BigDecimal(idRichAnnulVers));
                }
            }
        } catch (IOException ex) {
            log.error("Eccezione nell'upload del file", ex);
            getMessageBox().addError("Si \u00E8 verificata un'eccezione nell'upload del file", ex);
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        } catch (Exception ex) {
            log.error("Eccezione generica nella creazione della richiesta di annullamento: "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            getMessageBox().addError(
                    "Si \u00E8 verificata un'eccezione nella creazione della richiesta di annullamento");
        }

        if (getMessageBox().hasError()) {
            getForm().getCreazioneRichAnnulVers().getFl_immediata().setValue(flImmediata);
            forwardToPublisher(Application.Publisher.CREAZIONE_RICH_ANNUL_VERS);
        } else {
            forwardToPublisher(Application.Publisher.RICH_ANNUL_VERS_DETAIL);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="UI Ricerca annullamento versamenti">
    @Secure(action = "Menu.AnnulVers.RicercaAnnulVers")
    public void loadRicercaAnnulVers() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.AnnulVers.RicercaAnnulVers");
        caricaContenutoPaginaRicercaAnnulVers("UNITA_DOC");
    }

    @Secure(action = "Menu.AnnulVers.RicercaAnnulVersFasc")
    public void loadRicercaAnnulVersFasc() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.AnnulVers.RicercaAnnulVersFasc");
        caricaContenutoPaginaRicercaAnnulVers("FASCICOLI");
    }

    private void caricaContenutoPaginaRicercaAnnulVers(String tiRichAnnulVers) throws EMFError {
        getSession()
                .removeAttribute(getForm().getCreazioneRichAnnulVers().getFl_immediata().getName());

        getForm().getFiltriRicercaRichAnnullVers().reset();
        getForm().getFiltriRicercaRichAnnullVers().setEditMode();

        initFiltriStrut(getForm().getFiltriRicercaRichAnnullVers().getName());

        getForm().getRichAnnulVersList().setTable(new AroVRicRichAnnvrsTableBean());

        getForm().getFiltriRicercaRichAnnullVers().getFl_immediata()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriRicercaRichAnnullVers().getFl_non_annul()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriRicercaRichAnnullVers().getTi_stato_rich_annul_vers_cor()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato_rich_annul_vers",
                        CostantiDB.StatoRichAnnulVers.values()));

        getSession().setAttribute("tiRichAnnulVers", tiRichAnnulVers);
        getForm().getFiltriRicercaRichAnnullVers().getTi_rich_annul_vers()
                .setValue(tiRichAnnulVers);

        getForm().getFiltriRicercaRichAnnullVers().getTi_annullamento()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_annullamento",
                        CostantiDB.TipoAnnullamento.values()));

        if (tiRichAnnulVers.equals("UNITA_DOC")) {
            getForm().getFiltriRicercaRichAnnullVers().getFl_annul_ping().setHidden(false);
            getForm().getFiltriRicercaRichAnnullVers().getFl_annul_ping()
                    .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

            getForm().getUdSection().setHidden(false);
            getForm().getFiltriRicercaRichAnnullVers().getCd_registro_key_unita_doc()
                    .setHidden(false);
            getForm().getFiltriRicercaRichAnnullVers().getAa_key_unita_doc().setHidden(false);
            getForm().getFiltriRicercaRichAnnullVers().getCd_key_unita_doc().setHidden(false);

            DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb.getRegistriUnitaDocAbilitati(
                    getUser().getIdUtente(), getUser().getIdOrganizzazioneFoglia());
            DecodeMap mappaRegistro = DecodeMap.Factory.newInstance(tmpTableBeanReg,
                    "cd_registro_unita_doc", "cd_registro_unita_doc");
            getForm().getFiltriRicercaRichAnnullVers().getCd_registro_key_unita_doc()
                    .setDecodeMap(mappaRegistro);

            getForm().getFascSection().setHidden(true);
            getForm().getFiltriRicercaRichAnnullVers().getAa_fascicolo().setHidden(true);
            getForm().getFiltriRicercaRichAnnullVers().getCd_key_fascicolo().setHidden(true);

            // getForm().getFiltriRicercaRichAnnullVers().getCreaRichAnnulImmediata().setHidden(false);
            // getForm().getRichAnnulVersList().setHideInsertButton(false);
        } else {
            getForm().getFiltriRicercaRichAnnullVers().getFl_annul_ping().setHidden(true);

            getForm().getUdSection().setHidden(true);
            getForm().getFiltriRicercaRichAnnullVers().getCd_registro_key_unita_doc()
                    .setHidden(true);
            getForm().getFiltriRicercaRichAnnullVers().getAa_key_unita_doc().setHidden(true);
            getForm().getFiltriRicercaRichAnnullVers().getCd_key_unita_doc().setHidden(true);

            getForm().getFascSection().setHidden(false);
            getForm().getFiltriRicercaRichAnnullVers().getAa_fascicolo().setHidden(false);
            getForm().getFiltriRicercaRichAnnullVers().getCd_key_fascicolo().setHidden(false);

            // getForm().getFiltriRicercaRichAnnullVers().getCreaRichAnnulImmediata().setHidden(true);
            // getForm().getRichAnnulVersList().setHideInsertButton(true);
        }

        forwardToPublisher(Application.Publisher.RICERCA_RICH_ANNUL_VERS);
    }

    private void initFiltriStrut(String fieldSetName) throws EMFError {
        /* Ricavo Ambiente, Ente e Struttura CORRENTI */
        OrgStrutRowBean strutWithAmbienteEnte = evEjb
                .getOrgStrutRowBeanWithAmbienteEnte(getUser().getIdOrganizzazioneFoglia());
        // Ricavo id struttura, ente ed ambiente attuali
        BigDecimal idAmbiente = strutWithAmbienteEnte.getBigDecimal("id_ambiente");
        BigDecimal idEnte = strutWithAmbienteEnte.getIdEnte();
        BigDecimal idStrut = strutWithAmbienteEnte.getIdStrut();

        // Inizializzo le combo settando la struttura corrente
        OrgAmbienteTableBean tmpTableBeanAmbiente = null;
        OrgEnteTableBean tmpTableBeanEnte = null;
        OrgStrutTableBean tmpTableBeanStruttura = null;
        try {
            // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
            tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());

            // Ricavo i valori della combo ENTE
            tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(),
                    idAmbiente.longValue(), Boolean.TRUE);

            // Ricavo i valori della combo STRUTTURA
            tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(),
                    idEnte, Boolean.TRUE);

        } catch (Exception ex) {
            log.error("Errore durante il recupero dei filtri Ambiente - Ente - Struttura", ex);
        }

        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        ComboBox<BigDecimal> comboAmbiente = ((ComboBox<BigDecimal>) ((Fields<Field>) getForm()
                .getComponent(fieldSetName)).getComponent("Id_ambiente"));
        comboAmbiente.setDecodeMap(mappaAmbiente);
        comboAmbiente.setValue(idAmbiente.toString());

        DecodeMap mappaEnte = new DecodeMap();
        mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
        ComboBox<BigDecimal> comboEnte = ((ComboBox<BigDecimal>) ((Fields<Field>) getForm()
                .getComponent(fieldSetName)).getComponent("Id_ente"));
        comboEnte.setDecodeMap(mappaEnte);
        comboEnte.setValue(idEnte.toString());

        DecodeMap mappaStrut = new DecodeMap();
        mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
        ComboBox<BigDecimal> comboStrut = ((ComboBox<BigDecimal>) ((Fields<Field>) getForm()
                .getComponent(fieldSetName)).getComponent("Id_strut"));
        comboStrut.setDecodeMap(mappaStrut);
        comboStrut.setValue(idStrut.toString());
    }

    @Override
    public JSONObject triggerFiltriRicercaRichAnnullVersId_ambienteOnTrigger() throws EMFError {
        getForm().getFiltriRicercaRichAnnullVers().post(getRequest());

        // Azzero i valori preimpostati delle varie combo
        getForm().getFiltriRicercaRichAnnullVers().getId_ente().setValue("");
        getForm().getFiltriRicercaRichAnnullVers().getId_strut().setValue("");
        getForm().getFiltriRicercaRichAnnullVers().getCd_registro_key_unita_doc().setValue("");

        BigDecimal idAmbiente = getForm().getFiltriRicercaRichAnnullVers().getId_ambiente().parse();
        if (idAmbiente != null) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(
                    getUser().getIdUtente(), idAmbiente.longValue(), Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
            getForm().getFiltriRicercaRichAnnullVers().getId_ente().setDecodeMap(mappaEnte);
            // Se ho un solo ente lo setto già impostato nella combo
            if (tmpTableBeanEnte.size() == 1) {
                getForm().getFiltriRicercaRichAnnullVers().getId_ente()
                        .setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                BigDecimal idEnte = tmpTableBeanEnte.getRow(0).getIdEnte();
                if (idEnte != null) {
                    // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
                    OrgStrutTableBean tmpTableBeanStrut = struttureEjb
                            .getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.TRUE);
                    DecodeMap mappaStrut = new DecodeMap();
                    mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");
                    getForm().getFiltriRicercaRichAnnullVers().getId_strut()
                            .setDecodeMap(mappaStrut);

                    // Se la combo struttura ha un solo valore presente, lo imposto e faccio
                    // controllo su di essa
                    if (tmpTableBeanStrut.size() == 1) {
                        getForm().getFiltriRicercaRichAnnullVers().getId_strut()
                                .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                        BigDecimal idStrut = tmpTableBeanStrut.getRow(0).getIdStrut();

                        DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb
                                .getRegistriUnitaDocAbilitati(getUser().getIdUtente(), idStrut);
                        DecodeMap mappaRegistro = DecodeMap.Factory.newInstance(tmpTableBeanReg,
                                "cd_registro_unita_doc", "cd_registro_unita_doc");
                        getForm().getFiltriRicercaRichAnnullVers().getCd_registro_key_unita_doc()
                                .setDecodeMap(mappaRegistro);

                        if (tmpTableBeanReg.size() == 1) {
                            getForm().getFiltriRicercaRichAnnullVers()
                                    .getCd_registro_key_unita_doc()
                                    .setValue(tmpTableBeanReg.getRow(0).getCdRegistroUnitaDoc());
                        }
                    }
                }
            } else {
                getForm().getFiltriRicercaRichAnnullVers().getId_strut()
                        .setDecodeMap(new DecodeMap());
                getForm().getFiltriRicercaRichAnnullVers().getCd_registro_key_unita_doc()
                        .setDecodeMap(new DecodeMap());
            }
        } else {
            getForm().getFiltriRicercaRichAnnullVers().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaRichAnnullVers().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaRichAnnullVers().getCd_registro_key_unita_doc()
                    .setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriRicercaRichAnnullVers().asJSON();
    }

    @Override
    public JSONObject triggerFiltriRicercaRichAnnullVersId_enteOnTrigger() throws EMFError {
        getForm().getFiltriRicercaRichAnnullVers().post(getRequest());

        // Azzero i valori preimpostati delle varie combo
        getForm().getFiltriRicercaRichAnnullVers().getId_strut().setValue("");
        getForm().getFiltriRicercaRichAnnullVers().getCd_registro_key_unita_doc().setValue("");

        BigDecimal idEnte = getForm().getFiltriRicercaRichAnnullVers().getId_ente().parse();
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb
                    .getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");
            getForm().getFiltriRicercaRichAnnullVers().getId_strut().setDecodeMap(mappaStrut);
            // Se ho una sola struttura la setto già impostata nella combo
            if (tmpTableBeanStrut.size() == 1) {
                getForm().getFiltriRicercaRichAnnullVers().getId_strut()
                        .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                BigDecimal idStrut = tmpTableBeanStrut.getRow(0).getIdStrut();

                DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb
                        .getRegistriUnitaDocAbilitati(getUser().getIdUtente(), idStrut);
                DecodeMap mappaRegistro = DecodeMap.Factory.newInstance(tmpTableBeanReg,
                        "cd_registro_unita_doc", "cd_registro_unita_doc");
                getForm().getFiltriRicercaRichAnnullVers().getCd_registro_key_unita_doc()
                        .setDecodeMap(mappaRegistro);

                if (tmpTableBeanReg.size() == 1) {
                    getForm().getFiltriRicercaRichAnnullVers().getCd_registro_key_unita_doc()
                            .setValue(tmpTableBeanReg.getRow(0).getCdRegistroUnitaDoc());
                }
            }
        } else {
            getForm().getFiltriRicercaRichAnnullVers().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaRichAnnullVers().getCd_registro_key_unita_doc()
                    .setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriRicercaRichAnnullVers().asJSON();
    }

    @Override
    public JSONObject triggerFiltriRicercaRichAnnullVersId_strutOnTrigger() throws EMFError {
        getForm().getFiltriRicercaRichAnnullVers().post(getRequest());
        getForm().getFiltriRicercaRichAnnullVers().getCd_registro_key_unita_doc().setValue("");

        BigDecimal idStrut = getForm().getFiltriRicercaRichAnnullVers().getId_strut().parse();
        if (idStrut != null) {
            DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb
                    .getRegistriUnitaDocAbilitati(getUser().getIdUtente(), idStrut);
            DecodeMap mappaRegistro = DecodeMap.Factory.newInstance(tmpTableBeanReg,
                    "cd_registro_unita_doc", "cd_registro_unita_doc");
            getForm().getFiltriRicercaRichAnnullVers().getCd_registro_key_unita_doc()
                    .setDecodeMap(mappaRegistro);

            if (tmpTableBeanReg.size() == 1) {
                getForm().getFiltriRicercaRichAnnullVers().getCd_registro_key_unita_doc()
                        .setValue(tmpTableBeanReg.getRow(0).getCdRegistroUnitaDoc());
            }
        } else {
            getForm().getFiltriRicercaRichAnnullVers().getCd_registro_key_unita_doc()
                    .setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriRicercaRichAnnullVers().asJSON();
    }

    @Override
    public void ricercaRichAnnulVers() throws EMFError {
        if (getForm().getFiltriRicercaRichAnnullVers().postAndValidate(getRequest(),
                getMessageBox())) {
            /* Imposto il filtro tiRichAnnulVers in base alla mia provenienza iniziale */
            if ((String) getSession().getAttribute("tiRichAnnulVers") != null) {
                getForm().getFiltriRicercaRichAnnullVers().getTi_rich_annul_vers()
                        .setValue((String) getSession().getAttribute("tiRichAnnulVers"));
            }

            RicercaRichAnnulVersBean filtri = new RicercaRichAnnulVersBean(
                    getForm().getFiltriRicercaRichAnnullVers());
            TypeValidator validator = new TypeValidator(getMessageBox());
            validator.validaOrdineDateOrari(filtri.getDt_creazione_rich_annul_vers_da(),
                    filtri.getDt_creazione_rich_annul_vers_a(),
                    getForm().getFiltriRicercaRichAnnullVers().getDt_creazione_rich_annul_vers_da()
                            .getName(),
                    getForm().getFiltriRicercaRichAnnullVers().getDt_creazione_rich_annul_vers_a()
                            .getName());
            if (!getMessageBox().hasError()) {
                AroVRicRichAnnvrsTableBean table = annulVersEjb
                        .getAroVRicRichAnnvrsTableBean(getUser().getIdUtente(), filtri);
                getForm().getRichAnnulVersList().setTable(table);
                getForm().getRichAnnulVersList().getTable()
                        .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getRichAnnulVersList().getTable().first();
            }
        }
        forwardToPublisher(Application.Publisher.RICERCA_RICH_ANNUL_VERS);
    }

    @Override
    public void creaRichAnnulImmediata() throws EMFError {
        getForm().getCreazioneRichAnnulVers().reset();
        getForm().getCreazioneRichAnnulVers().setEditMode();
        getForm().getCreazioneRichAnnulVers().getFl_immediata().setValue("1");
        getForm().getCreazioneRichAnnulVers().getFl_forza_annul()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getSession().setAttribute(getForm().getCreazioneRichAnnulVers().getFl_immediata().getName(),
                "1");
        getForm().getCreazioneRichAnnulVers().getTi_annullamento()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_annullamento",
                        CostantiDB.TipoAnnullamento.values()));
        getForm().getCreazioneRichAnnulVers().getTi_annullamento()
                .setValue("ANNULLAMENTO_VERSAMENTO");

        if (getSession().getAttribute("tiRichAnnulVers") != null) {
            if (((String) getSession().getAttribute("tiRichAnnulVers")).equals("UNITA_DOC")) {
                getForm().getCreazioneRichAnnulVers().getTi_rich_annul_vers().setValue("UNITA_DOC");
            } else if (((String) getSession().getAttribute("tiRichAnnulVers"))
                    .equals("FASCICOLI")) {
                getForm().getCreazioneRichAnnulVers().getTi_rich_annul_vers().setValue("FASCICOLI");
            }
        }

        forwardToPublisher(Application.Publisher.CREAZIONE_RICH_ANNUL_VERS);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="UI parziale Dettaglio annullamento versamenti">
    @Override
    public void tabListaItemOnClick() throws EMFError {
        getForm().getRichAnnulVersDetailSubTabs()
                .setCurrentTab(getForm().getRichAnnulVersDetailSubTabs().getListaItem());
        forwardToPublisher(Application.Publisher.RICH_ANNUL_VERS_DETAIL);
    }

    @Override
    public void tabListaStatiOnClick() throws EMFError {
        getForm().getRichAnnulVersDetailSubTabs()
                .setCurrentTab(getForm().getRichAnnulVersDetailSubTabs().getListaStati());
        forwardToPublisher(Application.Publisher.RICH_ANNUL_VERS_DETAIL);
    }

    @Override
    public void tabListaItemCancellatiOnClick() throws EMFError {
        getForm().getRichAnnulVersDetailSubTabs()
                .setCurrentTab(getForm().getRichAnnulVersDetailSubTabs().getListaItemCancellati());
        forwardToPublisher(Application.Publisher.RICH_ANNUL_VERS_DETAIL);
    }

    @Override
    public void rifiutaRichiesta() throws Throwable {
        AroVRicRichAnnvrsRowBean row = (AroVRicRichAnnvrsRowBean) getForm().getRichAnnulVersList()
                .getTable().getCurrentRow();
        BigDecimal idRichAnnulVers = row.getIdRichAnnulVers();
        if (annulVersEjb.checkStatoRichiesta(idRichAnnulVers,
                CostantiDB.StatoRichAnnulVers.APERTA.name())) {
            cambiaStato(idRichAnnulVers, CostantiDB.StatoRichAnnulVers.RIFIUTATA.name());
        } else {
            getMessageBox()
                    .addError("Modifica non possibile in quanto la richiesta non ha stato APERTA");
        }
    }

    @Override
    public void controllaRichiesta() throws Throwable {
        final BigDecimal idRichAnnulVers = getForm().getRichAnnulVersDetail()
                .getId_rich_annul_vers().parse();
        if (annulVersEjb.checkStatoRichiesta(idRichAnnulVers,
                CostantiDB.StatoRichAnnulVers.APERTA.name())) {
            try {
                // Ricavo l'utente che ha creato la richiesta (il primo stato di essa)
                Long idUtente = annulVersEjb.getUserFirstStateRich(idRichAnnulVers);
                annulVersEjb.controlloItemOnline(idRichAnnulVers, idUtente);
                if (idRichAnnulVers != null) {
                    loadDettaglioRichiesta(idRichAnnulVers);
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        } else {
            getMessageBox()
                    .addError("Controllo non possibile in quanto la richiesta non ha stato APERTA");
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void chiudiRichiesta() throws Throwable {
        AroVRicRichAnnvrsRowBean row = (AroVRicRichAnnvrsRowBean) getForm().getRichAnnulVersList()
                .getTable().getCurrentRow();
        BigDecimal idRichAnnulVers = row.getIdRichAnnulVers();
        if (annulVersEjb.checkStatoRichiesta(idRichAnnulVers,
                CostantiDB.StatoRichAnnulVers.APERTA.name())) {
            // se nella richiesta non sono presenti item con stato = DA_ANNULLARE_IN_PING ed è
            // presente almeno un item
            // con stato = DA_ANNULLARE_IN_SACER
            if (!annulVersEjb.checkStatoItems(idRichAnnulVers,
                    CostantiDB.StatoItemRichAnnulVers.DA_ANNULLARE_IN_PING.name())
                    && annulVersEjb.checkStatoItems(idRichAnnulVers,
                            CostantiDB.StatoItemRichAnnulVers.DA_ANNULLARE_IN_SACER.name())) {
                cambiaStato(idRichAnnulVers, CostantiDB.StatoRichAnnulVers.CHIUSA.name());
            } else {
                getMessageBox().addError(
                        "Modifica non possibile in quanto nella richiesta sono presenti versamenti con stato DA_ANNULLARE_IN_PING o non c'\u00E8 nessun versamento con stato DA_ANNULLARE_IN_SACER");
            }
        } else {
            getMessageBox()
                    .addError("Modifica non possibile in quanto la richiesta non ha stato APERTA");
        }
    }

    private void cambiaStato(BigDecimal idRichAnnulVers, String tiStatoRichAnnulVers) {
        getForm().getCambioStatoRichiesta().getId_rich_annul_vers()
                .setValue(idRichAnnulVers.toPlainString());
        getForm().getCambioStatoRichiesta().getDs_nota_rich_annul_vers().setEditMode();
        getForm().getCambioStatoRichiesta().getDs_nota_rich_annul_vers().clear();

        getForm().getCambioStatoRichiesta().getTi_stato_rich_annul_vers()
                .setValue(tiStatoRichAnnulVers);
        getForm().getCambioStatoRichiesta().getConfermaCambioStato().setEditMode();
        try {
            if (tiStatoRichAnnulVers.equals(CostantiDB.StatoRichAnnulVers.CHIUSA.name())) {
                // Ricavo l'utente che ha creato la richiesta (il primo stato di essa)
                Long idUtente = annulVersEjb.getUserFirstStateRich(idRichAnnulVers);
                annulVersEjb.controlloItemOnline(idRichAnnulVers, idUtente);

                Long numeroItems = annulVersEjb.countItemsInRichAnnulVers(idRichAnnulVers);
                Long numeroItemsNonAnnullabili = annulVersEjb.countItemsInRichAnnulVers(
                        idRichAnnulVers, CostantiDB.StatoItemRichAnnulVers.NON_ANNULLABILE.name());

                if (numeroItems > 0L && numeroItemsNonAnnullabili > 0L) {
                    if (numeroItemsNonAnnullabili.equals(numeroItems)) {
                        getRequest().setAttribute("tuttiItemNonFattibili", true);
                    } else {
                        getRequest().setAttribute("itemNonFattibili", true);
                    }
                }
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }

        if (!getMessageBox().hasError()) {
            forwardToPublisher(Application.Publisher.CAMBIO_STATO_RICH_ANNUL_VERS);
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void confermaCambioStato() throws EMFError {
        getForm().getCambioStatoRichiesta().getDs_nota_rich_annul_vers().post(getRequest());
        BigDecimal idRichAnnulVers = getForm().getCambioStatoRichiesta().getId_rich_annul_vers()
                .parse();
        String dsNotaRichAnnulVers = getForm().getCambioStatoRichiesta()
                .getDs_nota_rich_annul_vers().parse();
        String tiStatoRichAnnulVers = getForm().getCambioStatoRichiesta()
                .getTi_stato_rich_annul_vers().parse();
        String tiStatoRichAnnulVersOld = getForm().getRichAnnulVersDetail()
                .getTi_stato_rich_annul_vers().parse();
        try {
            annulVersEjb.cambiaStato(getUser().getIdUtente(), idRichAnnulVers,
                    tiStatoRichAnnulVersOld, tiStatoRichAnnulVers, dsNotaRichAnnulVers);
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        goBack();
    }
    // </editor-fold>

    @Override
    public void uploadFile() throws EMFError {
        try {
            byte[] fileByteArray = null;
            final String blFile = getForm().getRichAnnulVersDetail().getBl_file().parse();
            final String tiRichAnnulVers = getForm().getRichAnnulVersDetail()
                    .getTi_rich_annul_vers().parse();
            if (blFile == null) {
                getMessageBox().addError(
                        "Deve essere definito il file di cui fare upload che elenca i versamenti da annullare");
            }
            if (!getMessageBox().hasError() && blFile != null) {
                fileByteArray = getForm().getRichAnnulVersDetail().getBl_file().getFileBytes();
                String mime = tikaSingleton.detectMimeType(fileByteArray);
                if (!mime.equals(VerFormatiEnums.CSV_MIME)
                        && !mime.equals(VerFormatiEnums.TEXT_PLAIN_MIME)) {
                    getMessageBox().addError(
                            "Il formato del file caricato non corrisponde al tipo testo/csv");
                } else {
                    if (tiRichAnnulVers.equals("UNITA_DOC")
                            && !annulVersEjb.checkCsvHeaders(fileByteArray)) {
                        getMessageBox().addError(
                                "Il primo record del file non definisce i nomi dei campi previsti (\"REGISTRO\", \"ANNO\" e \"NUMERO\")");
                    }
                    if (tiRichAnnulVers.equals("FASCICOLI")
                            && !annulVersEjb.checkCsvHeadersFasc(fileByteArray)) {
                        getMessageBox().addError(
                                "Il primo record del file non definisce i nomi dei campi previsti (\"ANNO\" e \"NUMERO\")");
                    }
                }
            }
            if (!getMessageBox().hasError()) {
                BigDecimal idRichAnnulVers = getForm().getRichAnnulVersDetail()
                        .getId_rich_annul_vers().parse();
                // Ricavo l'utente che ha creato la richiesta (il primo stato di essa)
                Long idUtente = annulVersEjb.getUserFirstStateRich(idRichAnnulVers);
                annulVersEjb.saveRichAnnulVers(idRichAnnulVers, fileByteArray, idUtente,
                        tiRichAnnulVers);
                loadDettaglioRichiesta(idRichAnnulVers);
            }
        } catch (IOException ex) {
            log.error("Eccezione nell'upload del file", ex);
            getMessageBox().addError("Si \u00E8 verificata un'eccezione nell'upload del file", ex);
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        } catch (Exception ex) {
            log.error("Eccezione generica nella modifica della richiesta di annullamento: "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            getMessageBox().addError(
                    "Si \u00E8 verificata un'eccezione nella modifica della richiesta di annullamento");
        }

        forwardToPublisher(Application.Publisher.RICH_ANNUL_VERS_DETAIL);
    }
}
