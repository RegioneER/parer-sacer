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
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.firma.crypto.verifica.SpringTikaSingleton;
import it.eng.parer.firma.crypto.verifica.VerFormatiEnums;
import it.eng.parer.scarto.dto.RicercaRichScartoVersBean;
import it.eng.parer.scarto.ejb.ScartoEjb;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.ScartoAbstractAction;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DmUdDelTableBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisItemRichScartoRowBean;
import it.eng.parer.slite.gen.viewbean.AroVLisItemRichScartoTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisStatoRichScartoRowBean;
import it.eng.parer.slite.gen.viewbean.AroVLisStatoRichScartoTableBean;
import it.eng.parer.slite.gen.viewbean.AroVRicRichScartoRowBean;
import it.eng.parer.slite.gen.viewbean.AroVRicRichScartoTableBean;
import it.eng.parer.slite.gen.viewbean.AroVVisRichScartoRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisStatoRichScartoRowBean;
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
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.impl.ComboBox;
import it.eng.spagoLite.security.Secure;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import javax.ejb.EJB;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Gilioli_P
 */
public class ScartoAction extends ScartoAbstractAction {

    private static final Logger log = LoggerFactory.getLogger(ScartoAction.class.getName());

    @EJB(mappedName = "java:app/Parer-ejb/SpringTikaSingleton")
    private SpringTikaSingleton tikaSingleton;
    @EJB(mappedName = "java:app/Parer-ejb/ScartoEjb")
    private ScartoEjb scartoEjb;
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

    }

    @Override
    public void loadDettaglio() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                || getNavigationEvent().equals(ListAction.NE_NEXT)
                || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getRichScartoVersList().getName())) {
                AroVRicRichScartoRowBean row = (AroVRicRichScartoRowBean) getForm()
                        .getRichScartoVersList().getTable().getCurrentRow();
                BigDecimal idRichScartoVers = row.getIdRichScartoVers();

                loadDettaglioRichiesta(idRichScartoVers);
                getForm().getRichScartoVersDetailSubTabs()
                        .setCurrentTab(getForm().getRichScartoVersDetailSubTabs().getListaItem());
                getForm().getRichScartoVersDetail().setViewMode();
                getForm().getRichScartoVersDetail().getBl_file().setEditMode();
                getForm().getRichScartoVersList().setStatus(Status.view);
            } else if (getTableName().equals(getForm().getStatiList().getName())) {
                AroVLisStatoRichScartoRowBean currentRow = (AroVLisStatoRichScartoRowBean) getForm()
                        .getStatiList().getTable().getCurrentRow();
                AroVVisStatoRichScartoRowBean aroVVisStatoRichScartoRowBean = scartoEjb
                        .geAroVVisStatoRichScartoRowBean(currentRow.getIdStatoRichScartoVers());
                getForm().getStatoRichScartoVersDetail()
                        .copyFromBean(aroVVisStatoRichScartoRowBean);

                getForm().getStatoRichScartoVersDetail().setViewMode();
                getForm().getStatiList().setStatus(Status.view);
            }
        }
    }

    private void loadDettaglioRichiesta(BigDecimal idRichScartoVers) throws EMFError {
        AroVVisRichScartoRowBean detailRow = scartoEjb
                .getAroVVisRichScartoRowBean(idRichScartoVers);
        getForm().getRichScartoVersDetail().copyFromBean(detailRow);

        AroVLisStatoRichScartoTableBean listaStati = scartoEjb
                .getAroVLisStatoRichScartoTableBean(idRichScartoVers);
        getForm().getStatiList().setTable(listaStati);
        getForm().getStatiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getStatiList().getTable().first();

        AroVLisItemRichScartoTableBean listaItem = scartoEjb
                .getAroVLisItemRichScartoTableBean(idRichScartoVers);
        getForm().getItemList().setTable(listaItem);
        getForm().getItemList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getItemList().getTable().first();

        // 39187: recupero gli eventuali item cancellati
        DmUdDelTableBean listaItemCancellati = dataMartEjb.getDmUdDelScartoVersTableBean(
                idRichScartoVers, CostantiDB.TiStatoUdCancellate.CANCELLATA_DB_SACER.name());
        getForm().getItemCancellatiList().setTable(listaItemCancellati);
        getForm().getItemCancellatiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getItemCancellatiList().getTable().first();

        getForm().getItemList().setHideInsertButton(true);
        getForm().getItemList().setHideDeleteButton(true);
        getForm().getItemList().setHideDetailButton(true);

        getForm().getStatiList().setHideDetailButton(false);
        getForm().getStatiList().setHideUpdateButton(false);

        getForm().getRichScartoVersList().setHideUpdateButton(true);
        getForm().getRichScartoVersList().setHideDeleteButton(true);

        getForm().getRichScartoVersDetailButtonList().getUploadFile().setHidden(true);
        getForm().getRichScartoVersDetail().getBl_file().setHidden(true);
        getForm().getRichScartoVersDetail().getBl_file().setEditMode();

        getForm().getRichScartoVersDetailButtonList().setEditMode();
        getForm().getRichScartoVersDetailButtonList().hideAll();

        if (scartoEjb.checkStatoRichiestaScarto(idRichScartoVers,
                CostantiDB.StatoRichAnnulVers.APERTA.name())) {
            getForm().getRichScartoVersList().setHideUpdateButton(false);
            getForm().getRichScartoVersList().setHideDeleteButton(false);

            getForm().getItemList().setHideInsertButton(false);
            getForm().getItemList().setHideDeleteButton(false);
            getForm().getItemList().setHideDetailButton(false);

        } else if (scartoEjb.checkStatoRichiestaScarto(idRichScartoVers,
                CostantiDB.StatoRichAnnulVers.CHIUSA.name())) {
            getForm().getRichScartoVersList().setHideDeleteButton(false);
        }
    }

    @Override
    public void undoDettaglio() throws EMFError {

    }

    @Override
    public void saveDettaglio() throws EMFError {

    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_NEXT)
                || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getRichScartoVersList().getName())) {
                forwardToPublisher(Application.Publisher.RICH_SCARTO_VERS_DETAIL);
            } else if (getTableName().equals(getForm().getItemList().getName())) {
                // Verifica l'esistenza dell'UD e passa a UnitaDocumentarieAction
                AroVLisItemRichScartoRowBean rowBean = (AroVLisItemRichScartoRowBean) getForm()
                        .getItemList().getTable().getCurrentRow();
                if (rowBean.getIdUnitaDoc() != null) {
                    AroVLisItemRichScartoTableBean listaItem = (AroVLisItemRichScartoTableBean) getForm()
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
        if (getLastPublisher().equals(Application.Publisher.RICH_SCARTO_VERS_DETAIL)) {
            goBackTo(getDefaultPublsherName());
        } else {
            goBack();
        }

    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.RICERCA_RICH_SCARTO_VERS;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        try {
            if (publisherName.equals(Application.Publisher.RICERCA_RICH_SCARTO_VERS)) {
                int rowIndex;
                int pageSize;
                RicercaRichScartoVersBean filtri = new RicercaRichScartoVersBean(
                        getForm().getFiltriRicercaRichScartoVers());
                if (!getMessageBox().hasError()) {
                    AroVRicRichScartoTableBean table = scartoEjb
                            .getAroVRicRichScartoTableBean(getUser().getIdUtente(), filtri);
                    if (getForm().getRichScartoVersList().getTable() != null) {
                        rowIndex = getForm().getRichScartoVersList().getTable()
                                .getCurrentRowIndex();
                        pageSize = getForm().getRichScartoVersList().getTable().getPageSize();
                    } else {
                        rowIndex = 0;
                        pageSize = WebConstants.DEFAULT_PAGE_SIZE;
                    }
                    getForm().getRichScartoVersList().setTable(table);
                    getForm().getRichScartoVersList().getTable().setPageSize(pageSize);
                    getForm().getRichScartoVersList().getTable().setCurrentRowIndex(rowIndex);
                }
                getForm().getRichScartoVersList().setHideUpdateButton(false);
                getForm().getRichScartoVersList().setHideDeleteButton(false);
            } else if (publisherName.equals(Application.Publisher.RICH_SCARTO_VERS_DETAIL)) {
                BigDecimal idRichScartoVers = getForm().getRichScartoVersDetail()
                        .getId_rich_scarto_vers().parse();
                if (idRichScartoVers != null) {
                    loadDettaglioRichiesta(idRichScartoVers);
                }
                getForm().getRichScartoVersDetailSubTabs()
                        .setCurrentTab(getForm().getRichScartoVersDetailSubTabs().getListaItem());
                getForm().getRichScartoVersDetail().setViewMode();
                getForm().getRichScartoVersDetail().getBl_file().setEditMode();
                getForm().getRichScartoVersList().setStatus(Status.view);
            }
        } catch (EMFError e) {
            log.error("Errore nel ricaricamento della pagina " + publisherName, e);
            getMessageBox().addError("Errore nel ricaricamento della pagina " + publisherName);
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.SCARTO;
    }

    @Override
    public void process() throws EMFError {
        boolean isMultipart = ServletFileUpload.isMultipartContent(getRequest());
        if (isMultipart) {
            int size10Mb = 10 * WebConstants.FILESIZE * WebConstants.FILESIZE;
            String[] paramMethods = null;
            try {
                if (getLastPublisher().equals(Application.Publisher.CREAZIONE_RICH_SCARTO_VERS)) {
                    paramMethods = getForm().getCreazioneRichScartoVers()
                            .postMultipart(getRequest(), size10Mb);
                } else if (getLastPublisher()
                        .equals(Application.Publisher.RICH_SCARTO_VERS_DETAIL)) {
                    paramMethods = getForm().getRichScartoVersDetail().postMultipart(getRequest(),
                            size10Mb);
                }
                if (paramMethods != null) {
                    String operationMethod = paramMethods[0];
                    if (paramMethods.length > 1) {
                        String[] navigationParams = Arrays.copyOfRange(paramMethods, 1,
                                paramMethods.length);

                        Method method = ScartoAction.class.getMethod(operationMethod,
                                String[].class);
                        method.invoke(this, (Object) navigationParams);
                    } else {
                        Method method = ScartoAction.class.getMethod(operationMethod);
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
    public void deleteRichScartoVersList() throws EMFError {
        AroVRicRichScartoRowBean row = (AroVRicRichScartoRowBean) getForm().getRichScartoVersList()
                .getTable().getCurrentRow();
        int riga = getForm().getRichScartoVersList().getTable().getCurrentRowIndex();
        BigDecimal idRichScartoVers = row.getIdRichScartoVers();
        if (!scartoEjb.checkStatoRichiestaScarto(idRichScartoVers,
                CostantiDB.StatoRichScartoVers.APERTA.name(),
                CostantiDB.StatoRichScartoVers.CHIUSA.name())) {
            getMessageBox().addError(
                    "La richiesta non \u00E8 eliminabile perch\u00E9 ha stato corrente diverso da APERTA o CHIUSA");
        } else {
            // Elimina richiesta
            try {
                scartoEjb.deleteRichScartoVers(idRichScartoVers);

                getForm().getRichScartoVersList().getTable().remove(riga);
                getMessageBox().addInfo("Richiesta eliminata con successo");
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
        if (!getMessageBox().hasError()
                && getLastPublisher().equals(Application.Publisher.RICH_SCARTO_VERS_DETAIL)) {
            goBackTo(Application.Publisher.RICERCA_RICH_SCARTO_VERS);
        } else {
            getForm().getRichScartoVersList().setHideUpdateButton(false);
            getForm().getRichScartoVersList().setHideDeleteButton(false);

            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void updateRichScartoVersList() throws EMFError {
        AroVRicRichScartoRowBean row = (AroVRicRichScartoRowBean) getForm().getRichScartoVersList()
                .getTable().getCurrentRow();
        BigDecimal idRichScartoVers = row.getIdRichScartoVers();
        if (!scartoEjb.checkStatoRichiestaScarto(idRichScartoVers,
                CostantiDB.StatoRichScartoVers.APERTA.name())) {
            getForm().getRichScartoVersList().setHideUpdateButton(false);
            getForm().getRichScartoVersList().setHideDeleteButton(false);

            getMessageBox().addError(
                    "La richiesta non \u00E8 modificabile perch\u00E9 ha stato corrente diverso da APERTA");
            forwardToPublisher(getLastPublisher());
        } else {
            // Modifica richiesta
            getForm().getRichScartoVersList().setStatus(Status.update);

            getForm().getRichScartoVersDetail().getCd_rich_scarto_vers().setEditMode();
            getForm().getRichScartoVersDetail().getDs_rich_scarto_vers().setEditMode();
            getForm().getRichScartoVersDetail().getNt_rich_scarto_vers().setEditMode();

            getForm().getRichScartoVersDetailButtonList().hideAll();
            getForm().getItemList().setHideInsertButton(true);
            getForm().getItemList().setHideDeleteButton(true);
            getForm().getItemList().setHideDetailButton(true);

            getForm().getStatiList().setHideDetailButton(true);
            getForm().getStatiList().setHideUpdateButton(true);

            forwardToPublisher(Application.Publisher.RICH_SCARTO_VERS_DETAIL);
        }
    }

    @Override
    public void deleteItemList() throws EMFError {
        AroVLisItemRichScartoRowBean row = (AroVLisItemRichScartoRowBean) getForm().getItemList()
                .getTable().getCurrentRow();
        int riga = getForm().getItemList().getTable().getCurrentRowIndex();
        BigDecimal idItemRichScartoVers = row.getIdItemRichScartoVers();
        BigDecimal idRichScartoVers = row.getIdRichScartoVers();
        if (!scartoEjb.checkStatoRichiestaScarto(idRichScartoVers,
                CostantiDB.StatoRichScartoVers.APERTA.name())) {
            getMessageBox().addError(
                    "Il versamento non \u00E8 eliminabile perch\u00E9 la richiesta ha stato corrente diverso da APERTA");
        } else {
            // Elimina richiesta
            try {
                scartoEjb.deleteItemRichScartoVers(idRichScartoVers, idItemRichScartoVers);

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
        AroVLisStatoRichScartoRowBean currentRow = (AroVLisStatoRichScartoRowBean) getForm()
                .getStatiList().getTable().getCurrentRow();
        if (!currentRow.getNmUserid().equals(getUser().getUsername())) {
            getMessageBox().addError(
                    "L'utente non pu\u00F2 modificare lo stato perch\u00E9 non corrisponde all'utente che lo ha registrato");
            forwardToPublisher(getLastPublisher());
        } else {
            getForm().getStatoRichScartoVersDetail().getDs_nota_rich_scarto_vers().setEditMode();
            getForm().getStatiList().setStatus(Status.update);

            forwardToPublisher(Application.Publisher.STATO_RICH_SCARTO_VERS_DETAIL);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="UI Creazione richiesta scarto versamenti">
    @Override
    public void creaRichScartoVers() throws EMFError {
        // Metodo richiamato dal metodo process in quanto richiesta MultiPart
        getForm().getCreazioneRichScartoVers().validate(getMessageBox());
        try {
            byte[] fileByteArray = null;
            final String cdRichScartoVers = getForm().getCreazioneRichScartoVers()
                    .getCd_rich_scarto_vers().parse();
            final String dsRichScartoVers = getForm().getCreazioneRichScartoVers()
                    .getDs_rich_scarto_vers().parse();
            final String ntRichScartoVers = getForm().getCreazioneRichScartoVers()
                    .getNt_rich_scarto_vers().parse();
            final String blFile = getForm().getCreazioneRichScartoVers().getBl_file().parse();

            if (!getMessageBox().hasError() && blFile != null) {
                fileByteArray = getForm().getCreazioneRichScartoVers().getBl_file().getFileBytes();
                String mime = tikaSingleton.detectMimeType(fileByteArray);
                if (!mime.equals(VerFormatiEnums.CSV_MIME)
                        && !mime.equals(VerFormatiEnums.TEXT_PLAIN_MIME)) {
                    getMessageBox().addError(
                            "Il formato del file caricato non corrisponde al tipo testo/csv");
                } else if (!scartoEjb.checkCsvHeaders(fileByteArray)) {
                    getMessageBox().addError(
                            "Il primo record del file non definisce i nomi dei campi previsti (\"REGISTRO\", \"ANNO\" e \"NUMERO\")");
                }
            }
            if (!getMessageBox().hasError()) {
                if (scartoEjb.checkCdRichScartoVersExisting(cdRichScartoVers,
                        getUser().getIdOrganizzazioneFoglia())) {
                    getMessageBox().addError(
                            "Nella struttura versante corrente \u00E8 gi\u00E0 presente una richiesta di scarto versamenti con lo stesso codice");
                }
            }
            if (!getMessageBox().hasError()) {
                // Salvo la richiesta di scarto versamento
                Long idRichScartoVers = scartoEjb.saveRichScartoVers(getUser().getIdUtente(),
                        cdRichScartoVers, dsRichScartoVers, ntRichScartoVers, fileByteArray,
                        getUser().getIdOrganizzazioneFoglia());
                // ELABORAZIONE RICHIESTA
                if (idRichScartoVers != null) {
                    scartoEjb.elaboraRichiestaScarto(idRichScartoVers, getUser().getIdUtente(),
                            Constants.ANNULLAMENTO_ONLINE);
                }
                if (idRichScartoVers != null) {
                    AroVRicRichScartoRowBean tmpRow = new AroVRicRichScartoRowBean();
                    tmpRow.setIdRichScartoVers(new BigDecimal(idRichScartoVers));
                    if (getForm().getRichScartoVersList().getTable() == null
                            || getForm().getRichScartoVersList().getTable().isEmpty()) {
                        getForm().getRichScartoVersList()
                                .setTable(new AroVRicRichScartoTableBean());
                        getForm().getRichScartoVersList().getTable().add(tmpRow);
                    } else {
                        getForm().getRichScartoVersList().getTable().last();
                        getForm().getRichScartoVersList().getTable().add(tmpRow);
                    }
                    loadDettaglioRichiesta(new BigDecimal(idRichScartoVers));
                }
            }
        } catch (IOException ex) {
            log.error("Eccezione nell'upload del file", ex);
            getMessageBox().addError("Si \u00E8 verificata un'eccezione nell'upload del file", ex);
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        } catch (Exception ex) {
            log.error("Eccezione generica nella creazione della richiesta di scarto: "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            getMessageBox().addError(
                    "Si \u00E8 verificata un'eccezione nella creazione della richiesta di scarto");
        }

        if (getMessageBox().hasError()) {
            forwardToPublisher(Application.Publisher.CREAZIONE_RICH_SCARTO_VERS);
        } else {
            forwardToPublisher(Application.Publisher.RICH_SCARTO_VERS_DETAIL);
        }
    }
    // </editor-fold>

    @Override
    public void uploadFile() throws EMFError {

    }

    // <editor-fold defaultstate="collapsed" desc="UI Ricerca scarto versamenti">
    @Secure(action = "Menu.Scarto.RicercaScartoVers")
    public void loadRicercaScartoVers() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Scarto.RicercaScartoVers");
        caricaContenutoPaginaRicercaScartoVers();
    }

    private void caricaContenutoPaginaRicercaScartoVers() throws EMFError {

        getForm().getFiltriRicercaRichScartoVers().reset();
        getForm().getFiltriRicercaRichScartoVers().setEditMode();

        initFiltriStrut(getForm().getFiltriRicercaRichScartoVers().getName());

        getForm().getRichScartoVersList().setTable(new AroVRicRichScartoTableBean());

        getForm().getFiltriRicercaRichScartoVers().getTi_stato_rich_scarto_vers_cor()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato_rich_scarto_vers",
                        CostantiDB.StatoRichScartoVers.values()));
        getForm().getFiltriRicercaRichScartoVers().getFl_non_scartabile()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

        getForm().getUdSection().setHidden(false);
        getForm().getFiltriRicercaRichScartoVers().getCd_registro_key_unita_doc().setHidden(false);
        getForm().getFiltriRicercaRichScartoVers().getAa_key_unita_doc().setHidden(false);
        getForm().getFiltriRicercaRichScartoVers().getCd_key_unita_doc().setHidden(false);

        DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb.getRegistriUnitaDocAbilitati(
                getUser().getIdUtente(), getUser().getIdOrganizzazioneFoglia());
        DecodeMap mappaRegistro = DecodeMap.Factory.newInstance(tmpTableBeanReg,
                "cd_registro_unita_doc", "cd_registro_unita_doc");
        getForm().getFiltriRicercaRichScartoVers().getCd_registro_key_unita_doc()
                .setDecodeMap(mappaRegistro);

        getForm().getFiltriRicercaRichScartoVers().getCreaRichScartoImmediata().setHidden(false);

        forwardToPublisher(Application.Publisher.RICERCA_RICH_SCARTO_VERS);
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
    public JSONObject triggerFiltriRicercaRichScartoVersId_ambienteOnTrigger() throws EMFError {
        getForm().getFiltriRicercaRichScartoVers().post(getRequest());

        // Azzero i valori preimpostati delle varie combo
        getForm().getFiltriRicercaRichScartoVers().getId_ente().setValue("");
        getForm().getFiltriRicercaRichScartoVers().getId_strut().setValue("");
        getForm().getFiltriRicercaRichScartoVers().getCd_registro_key_unita_doc().setValue("");

        BigDecimal idAmbiente = getForm().getFiltriRicercaRichScartoVers().getId_ambiente().parse();
        if (idAmbiente != null) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(
                    getUser().getIdUtente(), idAmbiente.longValue(), Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
            getForm().getFiltriRicercaRichScartoVers().getId_ente().setDecodeMap(mappaEnte);
            // Se ho un solo ente lo setto già impostato nella combo
            if (tmpTableBeanEnte.size() == 1) {
                getForm().getFiltriRicercaRichScartoVers().getId_ente()
                        .setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                BigDecimal idEnte = tmpTableBeanEnte.getRow(0).getIdEnte();
                if (idEnte != null) {
                    // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
                    OrgStrutTableBean tmpTableBeanStrut = struttureEjb
                            .getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.TRUE);
                    DecodeMap mappaStrut = new DecodeMap();
                    mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");
                    getForm().getFiltriRicercaRichScartoVers().getId_strut()
                            .setDecodeMap(mappaStrut);

                    // Se la combo struttura ha un solo valore presente, lo imposto e faccio
                    // controllo su di essa
                    if (tmpTableBeanStrut.size() == 1) {
                        getForm().getFiltriRicercaRichScartoVers().getId_strut()
                                .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                        BigDecimal idStrut = tmpTableBeanStrut.getRow(0).getIdStrut();

                        DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb
                                .getRegistriUnitaDocAbilitati(getUser().getIdUtente(), idStrut);
                        DecodeMap mappaRegistro = DecodeMap.Factory.newInstance(tmpTableBeanReg,
                                "cd_registro_unita_doc", "cd_registro_unita_doc");
                        getForm().getFiltriRicercaRichScartoVers().getCd_registro_key_unita_doc()
                                .setDecodeMap(mappaRegistro);

                        if (tmpTableBeanReg.size() == 1) {
                            getForm().getFiltriRicercaRichScartoVers()
                                    .getCd_registro_key_unita_doc()
                                    .setValue(tmpTableBeanReg.getRow(0).getCdRegistroUnitaDoc());
                        }
                    }
                }
            } else {
                getForm().getFiltriRicercaRichScartoVers().getId_strut()
                        .setDecodeMap(new DecodeMap());
                getForm().getFiltriRicercaRichScartoVers().getCd_registro_key_unita_doc()
                        .setDecodeMap(new DecodeMap());
            }
        } else {
            getForm().getFiltriRicercaRichScartoVers().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaRichScartoVers().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaRichScartoVers().getCd_registro_key_unita_doc()
                    .setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriRicercaRichScartoVers().asJSON();
    }

    @Override
    public JSONObject triggerFiltriRicercaRichScartoVersId_enteOnTrigger() throws EMFError {
        getForm().getFiltriRicercaRichScartoVers().post(getRequest());

        // Azzero i valori preimpostati delle varie combo
        getForm().getFiltriRicercaRichScartoVers().getId_strut().setValue("");
        getForm().getFiltriRicercaRichScartoVers().getCd_registro_key_unita_doc().setValue("");

        BigDecimal idEnte = getForm().getFiltriRicercaRichScartoVers().getId_ente().parse();
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb
                    .getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");
            getForm().getFiltriRicercaRichScartoVers().getId_strut().setDecodeMap(mappaStrut);
            // Se ho una sola struttura la setto già impostata nella combo
            if (tmpTableBeanStrut.size() == 1) {
                getForm().getFiltriRicercaRichScartoVers().getId_strut()
                        .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                BigDecimal idStrut = tmpTableBeanStrut.getRow(0).getIdStrut();

                DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb
                        .getRegistriUnitaDocAbilitati(getUser().getIdUtente(), idStrut);
                DecodeMap mappaRegistro = DecodeMap.Factory.newInstance(tmpTableBeanReg,
                        "cd_registro_unita_doc", "cd_registro_unita_doc");
                getForm().getFiltriRicercaRichScartoVers().getCd_registro_key_unita_doc()
                        .setDecodeMap(mappaRegistro);

                if (tmpTableBeanReg.size() == 1) {
                    getForm().getFiltriRicercaRichScartoVers().getCd_registro_key_unita_doc()
                            .setValue(tmpTableBeanReg.getRow(0).getCdRegistroUnitaDoc());
                }
            }
        } else {
            getForm().getFiltriRicercaRichScartoVers().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaRichScartoVers().getCd_registro_key_unita_doc()
                    .setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriRicercaRichScartoVers().asJSON();
    }

    @Override
    public JSONObject triggerFiltriRicercaRichScartoVersId_strutOnTrigger() throws EMFError {
        getForm().getFiltriRicercaRichScartoVers().post(getRequest());
        getForm().getFiltriRicercaRichScartoVers().getCd_registro_key_unita_doc().setValue("");

        BigDecimal idStrut = getForm().getFiltriRicercaRichScartoVers().getId_strut().parse();
        if (idStrut != null) {
            DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb
                    .getRegistriUnitaDocAbilitati(getUser().getIdUtente(), idStrut);
            DecodeMap mappaRegistro = DecodeMap.Factory.newInstance(tmpTableBeanReg,
                    "cd_registro_unita_doc", "cd_registro_unita_doc");
            getForm().getFiltriRicercaRichScartoVers().getCd_registro_key_unita_doc()
                    .setDecodeMap(mappaRegistro);

            if (tmpTableBeanReg.size() == 1) {
                getForm().getFiltriRicercaRichScartoVers().getCd_registro_key_unita_doc()
                        .setValue(tmpTableBeanReg.getRow(0).getCdRegistroUnitaDoc());
            }
        } else {
            getForm().getFiltriRicercaRichScartoVers().getCd_registro_key_unita_doc()
                    .setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriRicercaRichScartoVers().asJSON();
    }

    @Override
    public void ricercaRichScartoVers() throws EMFError {
        if (getForm().getFiltriRicercaRichScartoVers().postAndValidate(getRequest(),
                getMessageBox())) {

            RicercaRichScartoVersBean filtri = new RicercaRichScartoVersBean(
                    getForm().getFiltriRicercaRichScartoVers());
            TypeValidator validator = new TypeValidator(getMessageBox());
            validator.validaOrdineDateOrari(filtri.getDt_creazione_rich_scarto_vers_da(),
                    filtri.getDt_creazione_rich_scarto_vers_a(),
                    getForm().getFiltriRicercaRichScartoVers().getDt_creazione_rich_scarto_vers_da()
                            .getName(),
                    getForm().getFiltriRicercaRichScartoVers().getDt_creazione_rich_scarto_vers_a()
                            .getName());
            if (!getMessageBox().hasError()) {
                AroVRicRichScartoTableBean table = scartoEjb
                        .getAroVRicRichScartoTableBean(getUser().getIdUtente(), filtri);
                getForm().getRichScartoVersList().setTable(table);
                getForm().getRichScartoVersList().getTable()
                        .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getRichScartoVersList().getTable().first();
            }
        }
        forwardToPublisher(Application.Publisher.RICERCA_RICH_SCARTO_VERS);
    }

    @Override
    public void creaRichScartoImmediata() throws EMFError {
        getForm().getCreazioneRichScartoVers().reset();
        getForm().getCreazioneRichScartoVers().setEditMode();
        forwardToPublisher(Application.Publisher.CREAZIONE_RICH_SCARTO_VERS);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="UI parziale Dettaglio scarto versamenti">
    @Override
    public void tabListaItemOnClick() throws EMFError {
        getForm().getRichScartoVersDetailSubTabs()
                .setCurrentTab(getForm().getRichScartoVersDetailSubTabs().getListaItem());
        forwardToPublisher(Application.Publisher.RICH_SCARTO_VERS_DETAIL);
    }

    @Override
    public void tabListaStatiOnClick() throws EMFError {
        getForm().getRichScartoVersDetailSubTabs()
                .setCurrentTab(getForm().getRichScartoVersDetailSubTabs().getListaStati());
        forwardToPublisher(Application.Publisher.RICH_SCARTO_VERS_DETAIL);
    }

    @Override
    public void tabListaItemCancellatiOnClick() throws EMFError {
        getForm().getRichScartoVersDetailSubTabs()
                .setCurrentTab(getForm().getRichScartoVersDetailSubTabs().getListaItemCancellati());
        forwardToPublisher(Application.Publisher.RICH_SCARTO_VERS_DETAIL);
    }

    @Override
    public void chiudiRichiesta() throws Throwable {
        // Metodo da implementare in successiva evolutiva
    }

    @Override
    public void confermaCambioStato() throws EMFError {
        // Metodo da implementare in successiva evolutiva
    }
    // </editor-fold>

}
