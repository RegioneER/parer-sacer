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
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb.TipoUnitaDocEjb;
import it.eng.parer.entity.dto.ReportScartoUdDTO;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.firma.crypto.verifica.SpringTikaSingleton;
import it.eng.parer.firma.crypto.verifica.VerFormatiEnums;
import it.eng.parer.scarto.dto.FiltriRicercaUdScartoDto;
import it.eng.parer.scarto.dto.RicercaRichScartoVersBean;
import it.eng.parer.scarto.ejb.ScartoEjb;
import it.eng.parer.scarto.helper.ScartoHelper.ColonnaReportUd;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.ScartoAbstractAction;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm;
import it.eng.parer.slite.gen.tablebean.AroPropScartoVersRowBean;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DmUdDelTableBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisItemRichScartoRowBean;
import it.eng.parer.slite.gen.viewbean.AroVLisItemRichScartoTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisStatoRichScartoRowBean;
import it.eng.parer.slite.gen.viewbean.AroVLisStatoRichScartoTableBean;
import it.eng.parer.slite.gen.viewbean.AroVRicPropScartoVersRowBean;
import it.eng.parer.slite.gen.viewbean.AroVRicPropScartoVersTableBean;
import it.eng.parer.slite.gen.viewbean.AroVRicRichScartoRowBean;
import it.eng.parer.slite.gen.viewbean.AroVRicRichScartoTableBean;
import it.eng.parer.slite.gen.viewbean.AroVRicUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVRicUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVVisRichScartoRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisStatoRichScartoRowBean;
import it.eng.parer.web.ejb.DataMartEjb;
import it.eng.parer.web.ejb.ElenchiVersamentoEjb;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.web.validator.ScartoValidator;
import it.eng.parer.web.validator.TypeValidator;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.decodemap.DecodeMapIF;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    @EJB(mappedName = "java:app/Parer-ejb/TipoUnitaDocEjb")
    private TipoUnitaDocEjb tipoUnitaDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
    @EJB(mappedName = "java:app/Parer-ejb/DataMartEjb")
    private DataMartEjb dataMartEjb;
    @EJB(mappedName = "java:app/Parer-ejb/UnitaDocumentarieHelper")
    private UnitaDocumentarieHelper udHelper;

    public enum ModalitaInitScarto {
        VIEW, INS, MOD
    }

    private static final String QTA_RAGGIUNTO_COLUMN_TITLE = "Tempo raggiunto";
    private static final String QTA_NON_RAGGIUNTO_COLUMN_TITLE = "Tempo non raggiunto";
    private static final String QTA_SENZA_INDICAZIONE_COLUMN_TITLE = "Senza indicazione";
    private static final String QTA_TENUTA_ILLIMITATA_COLUMN_TITLE = "Tenuta illimitata";
    private static final String QTA_CONFLITTI_COLUMN_TITLE = "Conflitti";
    private static final String QTA_IN_ALTRE_PROPOSTE_COLUMN_TITLE = "In altre proposte";
    private static final String QTA_TOTALE_COLUMN_TITLE = "Totale";

    @Override
    public void initOnClick() throws EMFError {
    }

    @Override
    public void insertDettaglio() throws EMFError {
        String lista = getRequest().getParameter("table");

        if (lista.equals(getForm().getPropScartoVersList().getName())) {
            getForm().getCreazionePropScartoVers().clear();
            getForm().getFiltriRicercaUdPropScarto().clear();
            getForm().getRiepilogoUdPropScarto().clear();
            initPropScartoVers(ModalitaInitScarto.INS);
            initGestioneItemPropScartoVers(ModalitaInitScarto.INS);
            getForm().getCreazionePropScartoVers().getCd_prop_scarto_vers().setEditMode();

            getForm().getCreazionePropScartoVers().getSalvaProposta().setEditMode();
            getForm().getCreazionePropScartoVers().setStatus(Status.insert);
            getForm().getPropScartoVersList().setStatus(Status.insert);

            forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
        }
    }

    @Override
    public void loadDettaglio() throws EMFError {
        if (!getNavigationEvent().equals(ListAction.NE_DETTAGLIO_INSERT)) {
            if (getTableName().equals(getForm().getPropScartoVersList().getName())
                    || getTableName().equals(getForm().getCreazionePropScartoVers().getName())) {
                AroVRicPropScartoVersRowBean row = (AroVRicPropScartoVersRowBean) getForm()
                        .getPropScartoVersList().getTable().getCurrentRow();
                BigDecimal idPropScartoVers = row.getIdPropScartoVers();
                loadDettaglioProposta(idPropScartoVers);
                getForm().getCreazionePropScartoVers().setStatus(Status.view);
                getForm().getPropScartoVersList().setStatus(Status.view);
            }

            else if (getTableName().equals(getForm().getRichScartoVersList().getName())) {
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

    private void loadDettaglioProposta(BigDecimal idPropScartoVers) throws EMFError {
        initPropScartoVers(ModalitaInitScarto.VIEW);
        initGestioneItemPropScartoVers(ModalitaInitScarto.VIEW);

        // Carica i dati della proposta
        // AroPropScartoVersRowBean detailRow = scartoEjb
        // .getAroPropScartoVersRowBean(idPropScartoVers);
        AroVRicPropScartoVersRowBean detailRow = scartoEjb
                .getAroVRicPropScartoVersRowBean(idPropScartoVers, getUser().getIdUtente());
        getForm().getCreazionePropScartoVers().copyFromBean(detailRow);

        // Carica i totali degli item (num ud, num fascioli, num serie) della proposta
        long numUdInProposta = scartoEjb.getNumeroItemProposta(idPropScartoVers,
                CostantiDB.TiItemPropScartoVers.UNI_DOC.name());
        long numFascInProposta = scartoEjb.getNumeroItemProposta(idPropScartoVers,
                CostantiDB.TiItemPropScartoVers.FASC.name());
        long numSerieInProposta = scartoEjb.getNumeroItemProposta(idPropScartoVers,
                CostantiDB.TiItemPropScartoVers.SERIE.name());

        getForm().getCreazionePropScartoVers().getNi_ud_inserite().setValue("" + numUdInProposta);
        getForm().getCreazionePropScartoVers().getNi_fasc_inseriti()
                .setValue("" + numFascInProposta);
        getForm().getCreazionePropScartoVers().getNi_serie_inserite()
                .setValue("" + numSerieInProposta);

        // Carico la lista delle ud presenti in proposta nella lista UdSelezionatePropScartoList
        BaseTable itemUdTableBean = scartoEjb.getUdItemPropScartoVersTableBean(idPropScartoVers);

        getForm().getUdSelezionatePropScartoList().setTable(itemUdTableBean);
        getForm().getUdSelezionatePropScartoList().getTable().setPageSize(10);
        getForm().getUdSelezionatePropScartoList().getTable().first();

    }

    @Override
    public void undoDettaglio() throws EMFError {
        String publisher = getLastPublisher();

        if (publisher.equals(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS)
                && getForm().getPropScartoVersList().getStatus().equals(Status.insert)) {
            goBack();
        } else if (publisher.equals(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS)
                && getForm().getPropScartoVersList().getStatus().equals(Status.update)) {
            loadDettaglio();
            forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
        }
    }

    @Override
    public void saveDettaglio() throws EMFError {
        String publisher = getLastPublisher();

        if (publisher.equals(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS)
                && getForm().getPropScartoVersList().getStatus().equals(Status.update)) {
            BigDecimal idPropScartoVers = getForm().getCreazionePropScartoVers()
                    .getId_prop_scarto_vers().parse();
            loadDettaglioProposta(idPropScartoVers);
            getForm().getCreazionePropScartoVers().setStatus(Status.view);
            getForm().getPropScartoVersList().setStatus(Status.view);
            forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_NEXT)
                || getNavigationEvent().equals(ListAction.NE_PREV)) {

            if (getTableName().equals(getForm().getPropScartoVersList().getName())) {
                forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
            } else if (getTableName().equals(getForm().getRichScartoVersList().getName())) {
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
            } else if (getTableName().equals(getForm().getUdDaSelPropScartoList().getName())
                    || getTableName().equals(getForm().getUdSelezionatePropScartoList().getName())
                    || getTableName().equals(getForm().getUdInAltrePropScartoList().getName())) {
                UnitaDocumentarieForm form = new UnitaDocumentarieForm();
                AroVRicUnitaDocTableBean unitaDocTB = new AroVRicUnitaDocTableBean();
                Integer riga = Integer.valueOf(getRequest().getParameter("riga"));
                /* Verifico se l'utente può accedere al dettaglio unità documentaria */
                BigDecimal idUnitaDoc;
                BaseRow rigaComp;
                if (getTableName().equals(getForm().getUdDaSelPropScartoList().getName())) {
                    rigaComp = ((BaseTable) getForm().getUdDaSelPropScartoList().getTable())
                            .getRow(riga);
                } else if (getTableName()
                        .equals(getForm().getUdSelezionatePropScartoList().getName())) {
                    rigaComp = ((BaseTable) getForm().getUdSelezionatePropScartoList().getTable())
                            .getRow(riga);
                } else {
                    rigaComp = ((BaseTable) getForm().getUdInAltrePropScartoList().getTable())
                            .getRow(riga);
                }
                idUnitaDoc = rigaComp.getBigDecimal("id_unita_doc");
                if (idUnitaDoc != null) {
                    AroVRicUnitaDocRowBean aroVRicUnitaDocRowBean = udHelper
                            .getAroVRicUnitaDocRowBean(idUnitaDoc, null, null);
                    if (!aroVRicUnitaDocRowBean.getTiStatoConservazione()
                            .equals(CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name())) {
                        unitaDocTB.add(aroVRicUnitaDocRowBean);
                        /* Preparo la LISTA UNITA' DOCUMENTARIE */
                        form.getUnitaDocumentarieList().setTable(unitaDocTB);
                        redirectToAction(Application.Actions.UNITA_DOCUMENTARIE,
                                "?operation=listNavigationOnClick&navigationEvent="
                                        + ListAction.NE_DETTAGLIO_VIEW + "&table="
                                        + UnitaDocumentarieForm.UnitaDocumentarieList.NAME
                                        + "&riga=0",
                                form);
                    } else {
                        getMessageBox().addError(
                                "Operazione non possibile in quanto l'unità documentaria ha stato di conservazione = ANNULLATA");
                        forwardToPublisher(getLastPublisher());
                    }
                }
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
            } else if (publisherName.equals(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS)) {
                try {
                    loadDettaglioProposta(getForm().getCreazionePropScartoVers()
                            .getId_prop_scarto_vers().parse());
                    initPropScartoVers(ModalitaInitScarto.MOD);
                    initGestioneItemPropScartoVers(ModalitaInitScarto.MOD);
                } catch (EMFError ex) {
                    log.error(ex.getDescription(), ex);
                }
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
    public void uploadFile() throws EMFError {

    }

    // <editor-fold defaultstate="collapsed" desc="UI Ricerca proposte scarto versamenti">
    @Secure(action = "Menu.Scarto.ProposteScartoVers")
    public void loadRicercaPropScartoVers() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Scarto.ProposteScartoVers");
        initRicercaPropScartoVers();
    }

    private void initRicercaPropScartoVers() throws EMFError {
        // Reset dei campi di filtro per la ricerca delle proposte
        getForm().getFiltriRicercaPropScartoVers().reset();
        getForm().getFiltriRicercaPropScartoVers().setEditMode();

        // Inizializzazione delle combo Ambiente / Ente / Struttura in base ai permessi dell'utente
        initFiltriStrut(getForm().getFiltriRicercaPropScartoVers().getName());

        // Inizializza la lista dei risultati con un TableBean vuoto
        getForm().getPropScartoVersList().setTable(new BaseTable());

        // Popola la combo Stato proposta
        getForm().getFiltriRicercaPropScartoVers().getTi_stato_prop_scarto_vers()
                .setDecodeMap(ComboGetter.getMappaTiStatoProp());

        forwardToPublisher(Application.Publisher.RICERCA_PROP_SCARTO_VERS);
    }

    @Override
    public JSONObject triggerFiltriRicercaPropScartoVersId_ambienteOnTrigger() throws EMFError {
        getForm().getFiltriRicercaPropScartoVers().post(getRequest());

        // Azzero i valori preimpostati delle varie combo
        getForm().getFiltriRicercaPropScartoVers().getId_ente().setValue("");
        getForm().getFiltriRicercaPropScartoVers().getId_strut().setValue("");

        BigDecimal idAmbiente = getForm().getFiltriRicercaPropScartoVers().getId_ambiente().parse();
        if (idAmbiente != null) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(
                    getUser().getIdUtente(), idAmbiente.longValue(), Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
            getForm().getFiltriRicercaPropScartoVers().getId_ente().setDecodeMap(mappaEnte);

            // Se ho un solo ente lo setto già impostato nella combo
            if (tmpTableBeanEnte.size() == 1) {
                getForm().getFiltriRicercaPropScartoVers().getId_ente()
                        .setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                BigDecimal idEnte = tmpTableBeanEnte.getRow(0).getIdEnte();
                if (idEnte != null) {
                    // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
                    OrgStrutTableBean tmpTableBeanStrut = struttureEjb
                            .getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.TRUE);
                    DecodeMap mappaStrut = new DecodeMap();
                    mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");
                    getForm().getFiltriRicercaPropScartoVers().getId_strut()
                            .setDecodeMap(mappaStrut);

                    // Se la combo struttura ha un solo valore presente, lo imposto
                    if (tmpTableBeanStrut.size() == 1) {
                        getForm().getFiltriRicercaPropScartoVers().getId_strut()
                                .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                    }
                }
            } else {
                getForm().getFiltriRicercaPropScartoVers().getId_strut()
                        .setDecodeMap(new DecodeMap());
            }
        } else {
            getForm().getFiltriRicercaPropScartoVers().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaPropScartoVers().getId_strut().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriRicercaPropScartoVers().asJSON();
    }

    @Override
    public JSONObject triggerFiltriRicercaPropScartoVersId_enteOnTrigger() throws EMFError {
        getForm().getFiltriRicercaPropScartoVers().post(getRequest());

        // Azzero i valori preimpostati delle varie combo
        getForm().getFiltriRicercaPropScartoVers().getId_strut().setValue("");

        BigDecimal idEnte = getForm().getFiltriRicercaPropScartoVers().getId_ente().parse();
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb
                    .getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");
            getForm().getFiltriRicercaPropScartoVers().getId_strut().setDecodeMap(mappaStrut);

            // Se ho una sola struttura la setto già impostata nella combo
            if (tmpTableBeanStrut.size() == 1) {
                getForm().getFiltriRicercaPropScartoVers().getId_strut()
                        .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
            }
        } else {
            getForm().getFiltriRicercaPropScartoVers().getId_strut().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriRicercaPropScartoVers().asJSON();
    }

    @Override
    public void ricercaPropScartoVers() throws EMFError {
        if (getForm().getFiltriRicercaPropScartoVers().postAndValidate(getRequest(),
                getMessageBox())) {

            BigDecimal idStrut = getForm().getFiltriRicercaPropScartoVers().getId_strut().parse();
            String cdPropScartoVers = getForm().getFiltriRicercaPropScartoVers()
                    .getCd_prop_scarto_vers().parse();
            Date dtCreazionePropScartoVersDa = getForm().getFiltriRicercaPropScartoVers()
                    .getDt_creazione_prop_scarto_vers_da().parse();
            Date dtCreazionePropScartoVersA = getForm().getFiltriRicercaPropScartoVers()
                    .getDt_creazione_prop_scarto_vers_a().parse();
            Date dtUltimaModPropScartoVersDa = getForm().getFiltriRicercaPropScartoVers()
                    .getDt_ultima_mod_prop_scarto_vers_da().parse();
            Date dtUltimaModPropScartoVersA = getForm().getFiltriRicercaPropScartoVers()
                    .getDt_ultima_mod_prop_scarto_vers_a().parse();
            String tiStatoPropScartoVers = getForm().getFiltriRicercaPropScartoVers()
                    .getTi_stato_prop_scarto_vers().parse();

            try {
                AroVRicPropScartoVersTableBean proposteTableBean = scartoEjb.ricercaProposteScarto(
                        getUser().getIdUtente(), idStrut, cdPropScartoVers,
                        dtCreazionePropScartoVersDa, dtCreazionePropScartoVersA,
                        dtUltimaModPropScartoVersDa, dtUltimaModPropScartoVersA,
                        tiStatoPropScartoVers);
                getForm().getPropScartoVersList().setTable(proposteTableBean);
                getForm().getPropScartoVersList().getTable().setPageSize(10);
                getForm().getPropScartoVersList().getTable().first();
            } catch (EMFError ex) {
                getMessageBox().addError(ex.getMessage());
            }
        }
        forwardToPublisher(Application.Publisher.RICERCA_PROP_SCARTO_VERS);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="UI Creazione proposta scarto versamenti">
    @Override
    public void salvaProposta() throws EMFError {
        if (getForm().getCreazionePropScartoVers().postAndValidate(getRequest(), getMessageBox())) {

            // Leggo i dati anagrafici
            BigDecimal idPropScartoVers = getForm().getCreazionePropScartoVers()
                    .getId_prop_scarto_vers().parse();
            String descrizione = getForm().getCreazionePropScartoVers().getDs_prop_scarto_vers()
                    .getValue();
            String note = getForm().getCreazionePropScartoVers().getNt_prop_scarto_vers()
                    .getValue();

            try {

                if (getForm().getCreazionePropScartoVers().getStatus().equals(Status.insert)) {

                    // Chiamo l'EJB per fare la INSERT in ARO_PROP_SCARTO_VERS
                    Long idPropostaSalvata = scartoEjb.creaTestataPropostaScarto(descrizione, note,
                            getUser().getIdOrganizzazioneFoglia(), getUser().getIdUtente());

                    // Inizializzo i contatori a 0
                    getForm().getCreazionePropScartoVers().getNi_ud_inserite().setValue("0");
                    getForm().getCreazionePropScartoVers().getNi_fasc_inseriti().setValue("0");
                    getForm().getCreazionePropScartoVers().getNi_serie_inserite().setValue("0");

                    // Mi metto in modalità update sui filtri di ricerca ud
                    initPropScartoVers(ModalitaInitScarto.MOD);
                    initGestioneItemPropScartoVers(ModalitaInitScarto.MOD);

                    /* Inserisco la riga in tabella */
                    AroVRicPropScartoVersRowBean row = scartoEjb.getAroVRicPropScartoVersRowBean(
                            BigDecimal.valueOf(idPropostaSalvata), getUser().getIdUtente());
                    getForm().getPropScartoVersList().getTable().first();
                    getForm().getPropScartoVersList().getTable().add(row);
                    getForm().getCreazionePropScartoVers().copyFromBean(row);

                    // Imposto l'ID e IL CODICE GENERATO nel Form per farli vedere all'utente
                    getForm().getCreazionePropScartoVers().getId_prop_scarto_vers()
                            .setValue(String.valueOf(idPropostaSalvata));
                    getForm().getCreazionePropScartoVers().getCd_prop_scarto_vers()
                            .setValue(row.getCdPropScartoVers());

                    getMessageBox().addInfo("Proposta di scarto " + row.getCdPropScartoVers()
                            + " salvata con successo. E' ora possibile inserire le unità documentarie nella proposta");
                } else {
                    scartoEjb.aggiornaTestataPropostaScarto(idPropScartoVers, descrizione, note);
                    getMessageBox().addInfo("Dati della proposta aggiornati con successo.");
                }

                getForm().getCreazionePropScartoVers().setStatus(Status.update);
                getForm().getPropScartoVersList().setStatus(Status.update);
                getForm().getCreazionePropScartoVers().getCd_prop_scarto_vers().setViewMode();
            } catch (ParerUserError ex) {
                log.error(ex.getDescription(), ex);
                getMessageBox().addError(ex.getDescription());
            }
        }

        forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
    }

    @Override
    public void updatePropScartoVersList() throws EMFError {
        initPropScartoVers(ModalitaInitScarto.MOD);
        initGestioneItemPropScartoVers(ModalitaInitScarto.MOD);
        getForm().getCreazionePropScartoVers().setStatus(Status.update);
        getForm().getPropScartoVersList().setStatus(Status.update);
        forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
    }

    @Override
    public void updateCreazionePropScartoVers() throws EMFError {
        updatePropScartoVersList();
    }

    @Override
    public void tabTabFascicoliOnClick() throws EMFError {
        getForm().getCreazionePropScartoTabs()
                .setCurrentTab(getForm().getCreazionePropScartoTabs().getTabFascicoli());
        forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
    }

    @Override
    public void tabTabSerieOnClick() throws EMFError {
        getForm().getCreazionePropScartoTabs()
                .setCurrentTab(getForm().getCreazionePropScartoTabs().getTabSerie());
        forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
    }

    @Override
    public void tabTabUnitaDocOnClick() throws EMFError {
        getForm().getCreazionePropScartoTabs()
                .setCurrentTab(getForm().getCreazionePropScartoTabs().getTabUnitaDoc());
        forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
    }

    @Override
    public void tabTabPopolaPropostaUnitaDocOnClick() throws EMFError {
        getForm().getUdPropScartoTabs()
                .setCurrentTab(getForm().getUdPropScartoTabs().getTabPopolaPropostaUnitaDoc());
        forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
    }

    @Override
    public void tabTabConsultaUdAltreProposteOnClick() throws EMFError {
        getForm().getUdPropScartoTabs()
                .setCurrentTab(getForm().getUdPropScartoTabs().getTabConsultaUdAltreProposte());
        forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
    }

    @Override
    public void caricaListaUd() throws EMFError {
        // 1. Posto i filtri
        getForm().getFiltriRicercaUdPropScarto().post(getRequest());
        getForm().getUdSelezionatePropScartoList().post(getRequest());

        BigDecimal idPropostaCorrente = getForm().getCreazionePropScartoVers()
                .getId_prop_scarto_vers().parse();

        // 2. Leggo i campi nascosti del click
        String tipologiaCliccata = getForm().getFiltriRicercaUdPropScarto()
                .getHidden_tipologia_cliccata().parse();
        String colonnaCliccataStr = getForm().getFiltriRicercaUdPropScarto()
                .getHidden_colonna_cliccata().parse();

        if (StringUtils.isNotBlank(tipologiaCliccata)
                && StringUtils.isNotBlank(colonnaCliccataStr)) {
            try {
                ColonnaReportUd colonnaEnum = ColonnaReportUd.valueOf(colonnaCliccataStr);

                // 3. Ricostruzione del DTO Filtri
                FiltriRicercaUdScartoDto filtri = new FiltriRicercaUdScartoDto();
                filtri.setIdStrut(getUser().getIdOrganizzazioneFoglia());
                filtri.setRegistro(getForm().getFiltriRicercaUdPropScarto()
                        .getId_registro_unita_doc().parse());
                filtri.setAnno(getForm().getFiltriRicercaUdPropScarto().getAnno().parse());
                filtri.setAnnoDa(getForm().getFiltriRicercaUdPropScarto().getAnno_da().parse());
                filtri.setAnnoA(getForm().getFiltriRicercaUdPropScarto().getAnno_a().parse());
                filtri.setNumeroUd(getForm().getFiltriRicercaUdPropScarto().getNumero_ud().parse());
                filtri.setNumeroDa(getForm().getFiltriRicercaUdPropScarto().getNumero_da().parse());
                filtri.setNumeroA(getForm().getFiltriRicercaUdPropScarto().getNumero_a().parse());
                filtri.setDataUd(getForm().getFiltriRicercaUdPropScarto().getDt_ud().parse());
                filtri.setDataUdDa(getForm().getFiltriRicercaUdPropScarto().getDt_ud_da().parse());
                filtri.setDataUdA(getForm().getFiltriRicercaUdPropScarto().getDt_ud_a().parse());
                filtri.setOggettoUd(
                        getForm().getFiltriRicercaUdPropScarto().getOggetto_ud().parse());
                filtri.setTipologiaUd(
                        getForm().getFiltriRicercaUdPropScarto().getId_tipo_unita_doc().parse());
                filtri.setClassifica(
                        getForm().getFiltriRicercaUdPropScarto().getClassifica().parse());
                filtri.setTempoConservazione(
                        getForm().getFiltriRicercaUdPropScarto().getTempo_conservazione().parse());

                filtri.setIllimitato(getForm().getFiltriRicercaUdPropScarto().getFl_illimitato()
                        .getDecodedValue());
                filtri.setTempoSuperato(getForm().getFiltriRicercaUdPropScarto()
                        .getFl_tempo_superato().getDecodedValue());

                filtri.setIncludiFascicoli("SI".equals(getForm().getFiltriRicercaUdPropScarto()
                        .getFl_includi_fascicoli().parse()));
                filtri.setIncludiSerie("SI".equals(
                        getForm().getFiltriRicercaUdPropScarto().getFl_includi_serie().parse()));

                // =================================================================================
                // BIVIO 1: L'utente ha cliccato la colonna "In Altre Proposte"
                // =================================================================================
                if (colonnaEnum == ColonnaReportUd.IN_ALTRE_PROPOSTE) {

                    BaseTable tbAltreProposte = scartoEjb.estraiListaUdInAltreProposte(filtri,
                            tipologiaCliccata, idPropostaCorrente.longValue());

                    getForm().getUdInAltrePropScartoList().setTable(tbAltreProposte);
                    getForm().getUdInAltrePropScartoList().getTable().setPageSize(10);
                    getForm().getUdInAltrePropScartoList().getTable().first();

                    // Mi sposto nel tab "Consulta UD in altre proposte"
                    getForm().getUdPropScartoTabs().setCurrentTab(
                            getForm().getUdPropScartoTabs().getTabConsultaUdAltreProposte());

                }
                // =================================================================================
                // BIVIO 2: L'utente ha cliccato uno degli altri "Secchielli" o il Totale
                // =================================================================================
                else {
                    // 1. Estraggo la griglia di dettaglio "grezza"
                    BaseTable dettaglioTbRaw = scartoEjb.estraiListaUdPerScarto(filtri,
                            tipologiaCliccata, colonnaEnum, idPropostaCorrente);

                    // 2. Recupero gli ID delle UD già presenti nella lista "Selezionate" (Carrello)
                    Set<Long> idInPropostaCorrente = new HashSet<>();
                    BaseTable tableSelezionate = (BaseTable) getForm()
                            .getUdSelezionatePropScartoList().getTable();

                    if (tableSelezionate != null) {
                        for (int i = 0; i < tableSelezionate.size(); i++) {
                            BaseRow rowSel = (BaseRow) tableSelezionate.getRow(i);
                            BigDecimal idUd = rowSel.getBigDecimal("id_unita_doc");
                            if (idUd != null) {
                                idInPropostaCorrente.add(idUd.longValue());
                            }
                        }
                    }

                    // 3. Se l'utente ha cliccato "TOTALE", ci saranno anche le UD di altre proposte
                    // che non deve poter selezionare. (Negli altri secchielli non ci sono).
                    Set<Long> idInAltreProposte = new HashSet<>();
                    if (colonnaEnum == ColonnaReportUd.TOTALE && dettaglioTbRaw != null
                            && dettaglioTbRaw.size() > 0) {
                        List<Long> idEstratti = new ArrayList<>();
                        for (int i = 0; i < dettaglioTbRaw.size(); i++) {
                            BaseRow rowRaw = (BaseRow) dettaglioTbRaw.getRow(i);
                            BigDecimal idUdVal = rowRaw.getBigDecimal("id_unita_doc");
                            if (idUdVal != null) {
                                idEstratti.add(idUdVal.longValue());
                            }
                        }
                        // Chiamo il DB passando l'ID della proposta corrente per escluderla
                        idInAltreProposte = scartoEjb.getUdGiaInAltreProposte(
                                idPropostaCorrente.longValue(), idEstratti);
                    }

                    // 4. FILTRO LA TABELLA E CONTO I MOTIVI DI SCARTO DALLA VISTA
                    int udTotaliTrovate = dettaglioTbRaw != null ? dettaglioTbRaw.size() : 0;
                    BaseTable dettaglioTbFiltered = new BaseTable();
                    int contatoreCorrente = 0;
                    int contatoreAltre = 0;

                    if (dettaglioTbRaw != null) {
                        for (int i = 0; i < dettaglioTbRaw.size(); i++) {
                            BaseRow rowRaw = (BaseRow) dettaglioTbRaw.getRow(i);
                            Long idRaw = rowRaw.getBigDecimal("id_unita_doc").longValue();

                            if (idInPropostaCorrente.contains(idRaw)) {
                                contatoreCorrente++; // Conta quante UD sono già nel carrello in
                                                     // basso
                            } else if (idInAltreProposte.contains(idRaw)) {
                                contatoreAltre++; // Conta quante sono bloccate in altre proposte
                                                  // (succede solo se ha cliccato TOTALE)
                            } else {
                                dettaglioTbFiltered.add(rowRaw); // La metto tra quelle
                                                                 // selezionabili!
                            }
                        }
                    }

                    // 5. COSTRUZIONE MESSAGGIO DI ALERT DINAMICO
                    if (contatoreCorrente > 0 || contatoreAltre > 0) {
                        int udMostrate = dettaglioTbFiltered.size();

                        StringBuilder msg = new StringBuilder();
                        msg.append(String.format(
                                "Attenzione: saranno visualizzate <b>%d</b> su <b>%d</b> UD poich&eacute; alcune risultano gi&agrave; impegnate.</br><ul>",
                                udMostrate, udTotaliTrovate));

                        if (contatoreCorrente > 0) {
                            String verbo = (contatoreCorrente == 1) ? "&egrave;" : "sono";
                            String participio = (contatoreCorrente == 1) ? "inserita" : "inserite";
                            msg.append("<li><b>").append(contatoreCorrente).append("</b> UD ")
                                    .append(verbo).append(" gi&agrave; ").append(participio)
                                    .append(" in questa proposta.</li>");
                        }

                        if (contatoreAltre > 0) {
                            String verbo = (contatoreAltre == 1) ? "&egrave;" : "sono";
                            String participio = (contatoreAltre == 1) ? "inserita" : "inserite";
                            msg.append("<li><b>").append(contatoreAltre).append("</b> UD ")
                                    .append(verbo).append(" gi&agrave; ").append(participio)
                                    .append(" in altre proposte di scarto.</li>");
                        }
                        msg.append("</ul>");

                        getMessageBox().addWarning(msg.toString());
                    }

                    // 6. Popolo la lista superiore (Da Selezionare) con la tabella filtrata
                    getForm().getUdDaSelPropScartoList().setTable(dettaglioTbFiltered);
                    getForm().getUdDaSelPropScartoList().getTable().setPageSize(10);
                    getForm().getUdDaSelPropScartoList().getTable().first();

                    // Mi sposto nel tab "Popola proposta"
                    getForm().getUdPropScartoTabs().setCurrentTab(
                            getForm().getUdPropScartoTabs().getTabPopolaPropostaUnitaDoc());
                }

            } catch (EMFError e) {
                log.error("Errore nel caricamento delle liste UD delle proposte di scarto: "
                        + e.getMessage(), e);
                getMessageBox()
                        .addError("Errore nel caricamento delle liste UD delle proposte di scarto");
            }
        }
        forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
    }

    @Override
    public void ricercaFasc() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from
                                                                       // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void pulisciFasc() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from
                                                                       // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void ricercaSerie() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from
                                                                       // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void pulisciSerie() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from
                                                                       // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void ricercaUd() throws EMFError {
        getForm().getFiltriRicercaUdPropScarto().post(getRequest());

        // Popolo il DTO
        FiltriRicercaUdScartoDto filtri = new FiltriRicercaUdScartoDto();
        filtri.setIdStrut(getUser().getIdOrganizzazioneFoglia());
        filtri.setRegistro(
                getForm().getFiltriRicercaUdPropScarto().getId_registro_unita_doc().parse());
        filtri.setAnno(getForm().getFiltriRicercaUdPropScarto().getAnno().parse());
        filtri.setAnnoDa(getForm().getFiltriRicercaUdPropScarto().getAnno_da().parse());
        filtri.setAnnoA(getForm().getFiltriRicercaUdPropScarto().getAnno_a().parse());
        filtri.setNumeroUd(getForm().getFiltriRicercaUdPropScarto().getNumero_ud().parse());
        filtri.setNumeroDa(getForm().getFiltriRicercaUdPropScarto().getNumero_da().parse());
        filtri.setNumeroA(getForm().getFiltriRicercaUdPropScarto().getNumero_a().parse());
        filtri.setDataUd(getForm().getFiltriRicercaUdPropScarto().getDt_ud().parse());
        filtri.setDataUdDa(getForm().getFiltriRicercaUdPropScarto().getDt_ud_da().parse());
        filtri.setDataUdA(getForm().getFiltriRicercaUdPropScarto().getDt_ud_a().parse());
        filtri.setOggettoUd(getForm().getFiltriRicercaUdPropScarto().getOggetto_ud().parse());
        filtri.setTipologiaUd(
                getForm().getFiltriRicercaUdPropScarto().getId_tipo_unita_doc().parse());
        filtri.setClassifica(getForm().getFiltriRicercaUdPropScarto().getClassifica().parse());
        filtri.setTempoConservazione(
                getForm().getFiltriRicercaUdPropScarto().getTempo_conservazione().parse());

        // Conversione Flag ComboBox ("SI" o "NO")
        filtri.setIllimitato(
                getForm().getFiltriRicercaUdPropScarto().getFl_illimitato().getDecodedValue());
        filtri.setTempoSuperato(
                getForm().getFiltriRicercaUdPropScarto().getFl_tempo_superato().getDecodedValue());
        filtri.setIncludiFascicoli("SI".equals(getForm().getFiltriRicercaUdPropScarto()
                .getFl_includi_fascicoli().getDecodedValue()));
        filtri.setIncludiSerie("SI".equals(
                getForm().getFiltriRicercaUdPropScarto().getFl_includi_serie().getDecodedValue()));

        // Valida i campi di ricerca
        ScartoValidator validator = new ScartoValidator(getMessageBox());

        // verifico anno e numero
        boolean isValidAnnoNumero = validator.validaChiaviUnitaDocRicUdScarto(filtri);

        // verifico obbligatorietà alternata di registro e tipo ud
        boolean isValidRegistroTipoUd = validator
                .validaRegistroTipoUdRicUdScarto(filtri.getRegistro(), filtri.getTipologiaUd());

        if (isValidAnnoNumero && isValidRegistroTipoUd) {

            BigDecimal idPropCorrente = getForm().getCreazionePropScartoVers()
                    .getId_prop_scarto_vers().parse();

            // Chiamo l'Ejb passando tutti i filtri completi
            ReportScartoUdDTO reportDto = scartoEjb.calcolaReportUdPerScarto(filtri,
                    idPropCorrente.longValue());

            // Popolo la sezione dei "Totali"
            getForm().getRiepilogoUdPropScarto().getTotale_ud()
                    .setValue(String.valueOf(reportDto.getTotaleUd()));
            getForm().getRiepilogoUdPropScarto().getAnni_rif()
                    .setValue(reportDto.getAnniRiferimento());

            // Estraggo la tabella
            getForm().getReportUdPropScartoList().setTable(reportDto.getTableBean());
            getForm().getReportUdPropScartoList().getTable().setPageSize(10);
            getForm().getReportUdPropScartoList().getTable().first();

            // Azzero la tabella delle ud risultato e delle ud in altre proposte
            getForm().getUdDaSelPropScartoList().setTable(new BaseTable());
            getForm().getUdInAltrePropScartoList().setTable(new BaseTable());

            // Se tabella con più di un record, inserisco i totali nel titolo delle colonne
            if (reportDto.getTableBean().size() > 1) {
                Map<String, Long> totaliPerColonna = reportDto.getTotaliPerColonna();
                getForm().getReportUdPropScartoList().getQta_raggiunto()
                        .setDescription(QTA_RAGGIUNTO_COLUMN_TITLE + " ("
                                + totaliPerColonna.get(Constants.TiAlertPropScarto.RAGGIUNTO.name())
                                + ")");
                getForm().getReportUdPropScartoList().getQta_non_raggiunto()
                        .setDescription(
                                QTA_NON_RAGGIUNTO_COLUMN_TITLE + " ("
                                        + totaliPerColonna.get(
                                                Constants.TiAlertPropScarto.NON_RAGGIUNTO.name())
                                        + ")");
                getForm().getReportUdPropScartoList().getQta_senza_indicazione()
                        .setDescription(QTA_SENZA_INDICAZIONE_COLUMN_TITLE + " ("
                                + totaliPerColonna
                                        .get(Constants.TiAlertPropScarto.SENZA_INDICAZIONE.name())
                                + ")");
                getForm().getReportUdPropScartoList().getQta_illimitate()
                        .setDescription(
                                QTA_TENUTA_ILLIMITATA_COLUMN_TITLE + " ("
                                        + totaliPerColonna
                                                .get(Constants.TiAlertPropScarto.ILLIMITATA.name())
                                        + ")");
                getForm().getReportUdPropScartoList().getQta_conflitti()
                        .setDescription(QTA_CONFLITTI_COLUMN_TITLE + " ("
                                + totaliPerColonna.get(Constants.TiAlertPropScarto.CONFLITTI.name())
                                + ")");
                getForm().getReportUdPropScartoList().getQta_in_altre_prop()
                        .setDescription(QTA_IN_ALTRE_PROPOSTE_COLUMN_TITLE + " ("
                                + totaliPerColonna
                                        .get(Constants.TiAlertPropScarto.IN_ALTRE_PROPOSTE.name())
                                + ")");
                getForm().getReportUdPropScartoList().getQta_totale()
                        .setDescription(QTA_TOTALE_COLUMN_TITLE + " ("
                                + totaliPerColonna.get(Constants.TiAlertPropScarto.TOTALE.name())
                                + ")");
            } else {
                getForm().getReportUdPropScartoList().getQta_raggiunto()
                        .setDescription(QTA_RAGGIUNTO_COLUMN_TITLE);
                getForm().getReportUdPropScartoList().getQta_non_raggiunto()
                        .setDescription(QTA_NON_RAGGIUNTO_COLUMN_TITLE);
                getForm().getReportUdPropScartoList().getQta_senza_indicazione()
                        .setDescription(QTA_SENZA_INDICAZIONE_COLUMN_TITLE);
                getForm().getReportUdPropScartoList().getQta_illimitate()
                        .setDescription(QTA_TENUTA_ILLIMITATA_COLUMN_TITLE);
                getForm().getReportUdPropScartoList().getQta_totale()
                        .setDescription(QTA_TOTALE_COLUMN_TITLE);
            }
        }
        forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
    }

    @Override
    public void pulisciUd() throws EMFError {
        getForm().getFiltriRicercaUdPropScarto().clear();
        forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
    }

    // =================================================================
    // NUOVA RICERCA INTERNA (UD GIA' IN PROPOSTA)
    // =================================================================

    @Override
    public void ricercaUdSelezionate() throws EMFError {
        getForm().getFiltriRicercaUdSelezionate().post(getRequest());
        ricaricaListaUdSelezionateDaDb();
        forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
    }

    // @Override
    // public void pulisciUdSelezionate() throws EMFError {
    // getForm().getFiltriRicercaUdSelezionate().reset();
    // ricaricaListaUdSelezionateDaDb();
    // forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
    // }

    /**
     * Metodo di utilità: Legge i filtri della sezione inferiore e ricarica la lista dal DB. VA
     * CHIAMATO ALLA FINE DI OGNI 'AGGIUNGI' O 'RIMUOVI' per mantenere l'allineamento.
     */
    private void ricaricaListaUdSelezionateDaDb() {
        try {
            BigDecimal idPropBD = getForm().getCreazionePropScartoVers().getId_prop_scarto_vers()
                    .parse();
            if (idPropBD == null)
                return;

            // Leggo i filtri dalla nuova maschera
            BigDecimal idTipoUd = getForm().getFiltriRicercaUdSelezionate()
                    .getId_tipo_unita_doc_sel().parse();
            BigDecimal idRegistro = getForm().getFiltriRicercaUdSelezionate()
                    .getId_registro_unita_doc_sel().parse();
            BigDecimal anno = getForm().getFiltriRicercaUdSelezionate().getAnno_sel().parse();
            String numero = getForm().getFiltriRicercaUdSelezionate().getNumero_ud_sel().parse();
            String scartabile = getForm().getFiltriRicercaUdSelezionate().getFl_scartabile_sel()
                    .getDecodedValue();
            String alert = getForm().getFiltriRicercaUdSelezionate().getDs_alert_sel().parse();

            // Chiamo l'EJB
            BaseTable tableSelAggiornata = scartoEjb.getListaUdSalvateInProposta(idPropBD, idTipoUd,
                    idRegistro, anno, numero, scartabile, alert);

            getForm().getUdSelezionatePropScartoList().setTable(tableSelAggiornata);

            // NOTA: il contatore globale in alto (ni_ud_inserite) NON deve usare la size di questa
            // tabella filtrata,
            // ma deve essere ricalcolato sempre con countItemsByTipo dall'EJB, altrimenti se
            // l'utente filtra,
            // vede il totale della proposta cambiare!

        } catch (Exception e) {
            log.error("Errore ricaricamento lista selezionate: ", e);
        }
    }

    private void eseguiSpostamentoESalvataggio(List<Long> idUds, boolean isAggiunta)
            throws EMFError {
        if (idUds == null || idUds.isEmpty())
            return;

        BigDecimal idPropBD = getForm().getCreazionePropScartoVers().getId_prop_scarto_vers()
                .parse();

        // Inserisco o rimuovo direttamente
        if (isAggiunta) {
            scartoEjb.salvaItemUdInProposta(idPropBD.longValue(), idUds);
        } else {
            scartoEjb.rimuoviItemUdDaProposta(idPropBD.longValue(), idUds);
        }

        // Rimuovo le UD interessate dalla lista delle ud selezionabili
        BaseTable tableDaSel = (BaseTable) getForm().getUdDaSelPropScartoList().getTable();
        if (tableDaSel != null) {
            // Ciclo al contrario per non sballare gli indici durante la rimozione
            for (int i = tableDaSel.size() - 1; i >= 0; i--) {
                BaseRow row = (BaseRow) tableDaSel.getRow(i);
                Long currentId = row.getBigDecimal("id_unita_doc").longValue();

                // Se l'ID della riga è tra quelli appena aggiunti in proposta, la tolgo dalla vista
                if (idUds.contains(currentId)) {
                    tableDaSel.remove(i);
                }
            }
        }

        // 3. Ricarico la lista in basso mantenendo i filtri di ricerca dell'utente
        ricaricaListaUdSelezionateDaDb();

        // 4. Aggiorno i contatori presenti nella testata della proposta.
        // ATTENZIONE: Interrogo il DB per avere il vero totale, non uso la size() della tabella
        // perché la tabella potrebbe essere filtrata dall'utente!
        long totaleUdReale = scartoEjb.contaUdInProposta(idPropBD.longValue());
        getForm().getCreazionePropScartoVers().getNi_ud_inserite()
                .setValue(String.valueOf(totaleUdReale));

        getMessageBox().addInfo("Operazione completata con successo.");
    }

    public void confermaInserisciInPropostaUd() throws EMFError {
        eseguiSalvataggioUdInProposta();
        forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
    }

    private void eseguiSalvataggioUdInProposta() throws EMFError {
        BigDecimal idPropScartoVers = getForm().getCreazionePropScartoVers()
                .getId_prop_scarto_vers().parse();

        long numUdInProposta = scartoEjb.salvaItemUdProposta(idPropScartoVers,
                (BaseTable) getForm().getUdSelezionatePropScartoList().getTable());

        getForm().getCreazionePropScartoVers().getNi_ud_inserite().setValue("" + numUdInProposta);

        getMessageBox().addInfo("Proposta aggiornata correttamente.");

        // Ricarico la lista per avere le chiavi primarie degli item inseriti
        BaseTable udSalvateTable = scartoEjb.getUdItemPropScartoVersTableBean(idPropScartoVers);
        getForm().getUdSelezionatePropScartoList().setTable(udSalvateTable);
        getForm().getUdSelezionatePropScartoList().getTable().setPageSize(10);
        getForm().getUdSelezionatePropScartoList().getTable().first();
    }

    /**
     * Metodo di caricamento iniziale per la pagina di Creazione Proposta di Scarto.
     */
    private void initPropScartoVers(ModalitaInitScarto modalita) throws EMFError {
        // Metto tutto in viewMode e nascondo i pulsanti
        getForm().getCreazionePropScartoVers().setViewMode();

        getForm().getCreazionePropScartoVers().getCd_prop_scarto_vers().setHidden(false);
        getForm().getCreazionePropScartoVers().getDt_creazione_prop_scarto_vers().setHidden(false);
        getForm().getCreazionePropScartoVers().getDt_ultima_mod_prop_scarto_vers().setHidden(false);
        getForm().getCreazionePropScartoVers().getTi_stato_prop_scarto_vers_cor().setHidden(false);

        // Popolo le combo Ambiente / ente / struttura della proposta
        popolaAmbienteEnteStrutturaCreazionePropScarto();

        if (modalita.equals(ModalitaInitScarto.MOD)) {
            getForm().getCreazionePropScartoVers().getDs_prop_scarto_vers().setEditMode();
            getForm().getCreazionePropScartoVers().getNt_prop_scarto_vers().setEditMode();
            getForm().getCreazionePropScartoVers().getSalvaProposta().setEditMode();
        }

        if (modalita.equals(ModalitaInitScarto.INS)) {
            getForm().getCreazionePropScartoVers().getDs_prop_scarto_vers().setEditMode();
            getForm().getCreazionePropScartoVers().getNt_prop_scarto_vers().setEditMode();
            // Rendo invisibili alcuni campi della proposta
            getForm().getCreazionePropScartoVers().getCd_prop_scarto_vers().setHidden(true);
            getForm().getCreazionePropScartoVers().getDt_creazione_prop_scarto_vers()
                    .setHidden(true);
            getForm().getCreazionePropScartoVers().getDt_ultima_mod_prop_scarto_vers()
                    .setHidden(true);
            getForm().getCreazionePropScartoVers().getTi_stato_prop_scarto_vers_cor()
                    .setHidden(true);
        }
    }

    private void initGestioneItemPropScartoVers(ModalitaInitScarto modalita) throws EMFError {
        // Resetto i filtri in viewMode
        getForm().getFiltriRicercaFascPropScarto().reset();
        getForm().getFiltriRicercaFascPropScarto().setViewMode();
        getForm().getFiltriRicercaSeriePropScarto().reset();
        getForm().getFiltriRicercaSeriePropScarto().setViewMode();
        getForm().getFiltriRicercaUdPropScarto().reset();
        getForm().getFiltriRicercaUdPropScarto().setViewMode();
        getForm().getFiltriRicercaUdSelezionate().reset();
        getForm().getFiltriRicercaUdSelezionate().setViewMode();

        // Inizializzazione delle liste di ricerca vuote
        getForm().getReportFascPropScartoList().setTable(new BaseTable());
        getForm().getReportSeriePropScartoList().setTable(new BaseTable());
        getForm().getReportUdPropScartoList().setTable(new BaseTable());

        // pulisco la tabella delle ud selezionabili a seguito della ricerca
        getForm().getUdDaSelPropScartoList().setTable(new BaseTable());

        // Inizializzo i bottoni in view mode (nascosti)
        getForm().getFiltriRicercaUdPropScarto().getCaricaListaUd().setViewMode();
        getForm().getFiltriRicercaUdPropScarto().getRicercaUd().setViewMode();
        getForm().getFiltriRicercaUdPropScarto().getPulisciUd().setViewMode();
        // getForm().getFiltriRicercaUdSelezionate().getPulisciUdSelezionate().setViewMode();
        getForm().getFiltriRicercaUdSelezionate().getRicercaUdSelezionate().setViewMode();
        getForm().getSelectButtonList().setViewMode();

        // Pulisco i totali riepilogativi
        getForm().getRiepilogoUdPropScarto().clear();
        getForm().getRiepilogoFascPropScarto().clear();
        getForm().getRiepilogoSeriePropScarto().clear();

        // nascondo i filtri di ricerca UD, la tabella dei totali e le liste delle ud selezionabili
        // e selezionate
        getForm().getFiltriRicercaUdPropostaScartoSection().setHidden(true);
        getForm().getResultTotSection().setHidden(true);
        getForm().getListaUdDaSelSection().setHidden(true);
        getForm().getListaUdSelezionateSection().setHidden(false);
        getForm().getListaUdSelezionateSection().setShowButton(true);

        // FASCICOLI
        getForm().getFiltriRicercaFascicoliPropostaScartoSection().setHidden(true);
        getForm().getListaFascicoliDaSelSection().setHidden(true);
        getForm().getListaFascicoliSelezionatiSection().setHidden(false);
        getForm().getListaFascicoliSelezionatiSection().setShowButton(true);
        // SERIE
        getForm().getFiltriRicercaSeriePropostaScartoSection().setHidden(true);
        getForm().getListaSerieDaSelSection().setHidden(true);
        getForm().getListaSerieSelezionateSection().setHidden(false);
        getForm().getListaSerieSelezionateSection().setShowButton(true);

        getForm().getUdSelezionatePropScartoList().getFl_ud_selected().setHidden(true);

        if (modalita.equals(ModalitaInitScarto.INS)) {
            // Inizializzo le tabelle dei totali, ud selezionabili e selezionate delle UD
            getForm().getUdDaSelPropScartoList().setTable(new BaseTable());
            getForm().getUdSelezionatePropScartoList().setTable(new BaseTable());
        }

        if (modalita.equals(ModalitaInitScarto.MOD)) {
            getForm().getFiltriRicercaFascPropScarto().setEditMode();
            getForm().getFiltriRicercaSeriePropScarto().setEditMode();
            getForm().getFiltriRicercaUdPropScarto().setEditMode();
            getForm().getFiltriRicercaUdSelezionate().setEditMode();

            // Mi posiziono sui tab principali
            getForm().getCreazionePropScartoTabs()
                    .setCurrentTab(getForm().getCreazionePropScartoTabs().getTabUnitaDoc());
            getForm().getUdPropScartoTabs()
                    .setCurrentTab(getForm().getUdPropScartoTabs().getTabPopolaPropostaUnitaDoc());

            // Inizializzazione di eventuali altre combo (es. tipologie UD, registri, ecc.)
            popolaComboRegistroTipoUdCreazionePropScarto();

            getForm().getResultTotSection().setHidden(false);

            getForm().getFiltriRicercaUdPropScarto().getCaricaListaUd().setEditMode();
            getForm().getFiltriRicercaUdPropScarto().getRicercaUd().setEditMode();
            getForm().getFiltriRicercaUdPropScarto().getPulisciUd().setEditMode();
            getForm().getSelectButtonList().setEditMode();

            getForm().getFiltriRicercaUdPropostaScartoSection().setHidden(false);
            getForm().getListaUdDaSelSection().setHidden(false);
            getForm().getListaUdSelezionateSection().setHidden(false);
            getForm().getListaUdSelezionateSection().setShowButton(false);

            getForm().getFiltriRicercaFascicoliPropostaScartoSection().setHidden(false);
            getForm().getListaFascicoliDaSelSection().setHidden(false);
            getForm().getListaFascicoliSelezionatiSection().setHidden(false);
            getForm().getListaFascicoliSelezionatiSection().setShowButton(false);

            getForm().getFiltriRicercaSeriePropostaScartoSection().setHidden(false);
            getForm().getListaSerieDaSelSection().setHidden(false);
            getForm().getListaSerieSelezionateSection().setHidden(false);
            getForm().getListaSerieSelezionateSection().setShowButton(false);

            getForm().getUdSelezionatePropScartoList().getFl_ud_selected().setHidden(false);
        }

        // Al momento setto di default a no e non modificabili i filtri sulla presenza delle ud in
        // fascicoli/serie
        // Popolamento delle combo box SI/NO per tutti i tab
        popolaComboSiNoCreazionePropScarto();
        getForm().getFiltriRicercaUdPropScarto().getFl_includi_fascicoli().setViewMode();
        getForm().getFiltriRicercaUdPropScarto().getFl_includi_serie().setViewMode();
        getForm().getFiltriRicercaUdPropScarto().getFl_includi_fascicoli().setValue("0");
        getForm().getFiltriRicercaUdPropScarto().getFl_includi_serie().setValue("0");
        getForm().getFiltriRicercaUdSelezionate().getDs_alert_sel()
                .setDecodeMap(ComboGetter.getMappaTiAlertPropScarto());

    }

    /**
     * Popola tutte le ComboBox "flag" presenti nei Tab della Creazione Proposta di Scarto con il
     * dominio standard "SI/NO".
     */
    private void popolaComboSiNoCreazionePropScarto() throws EMFError {

        // Recupero la mappa standard per i valori SI/NO
        DecodeMapIF mappaSiNo = ComboGetter.getMappaGenericFlagSiNo();

        // ==========================================
        // TAB 1: FASCICOLI
        // ==========================================
        getForm().getFiltriRicercaFascPropScarto().getFl_illimitato().setDecodeMap(mappaSiNo);
        getForm().getFiltriRicercaFascPropScarto().getFl_tempo_superato().setDecodeMap(mappaSiNo);

        // ==========================================
        // TAB 2: SERIE
        // ==========================================
        getForm().getFiltriRicercaSeriePropScarto().getFl_illimitato().setDecodeMap(mappaSiNo);
        getForm().getFiltriRicercaSeriePropScarto().getFl_tempo_superato().setDecodeMap(mappaSiNo);

        // ==========================================
        // TAB 3: UNITA' DOCUMENTARIE
        // ==========================================
        getForm().getFiltriRicercaUdPropScarto().getFl_illimitato().setDecodeMap(mappaSiNo);
        getForm().getFiltriRicercaUdPropScarto().getFl_tempo_superato().setDecodeMap(mappaSiNo);
        getForm().getFiltriRicercaUdPropScarto().getFl_includi_fascicoli().setDecodeMap(mappaSiNo);
        getForm().getFiltriRicercaUdPropScarto().getFl_includi_serie().setDecodeMap(mappaSiNo);

        // Filtri ricerca ud in proposta
        getForm().getFiltriRicercaUdSelezionate().getFl_scartabile_sel()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

    }

    private void popolaComboRegistroTipoUdCreazionePropScarto() throws EMFError {
        BigDecimal idStrut = getUser().getIdOrganizzazioneFoglia();
        long idUtente = getUser().getIdUtente();
        DecRegistroUnitaDocTableBean tmpTableBeanRegistro = null;
        DecTipoUnitaDocTableBean tmpTableBeanTipoUd = null;
        try {
            // Ricavo i valori della combo REGISTRO
            tmpTableBeanRegistro = registroEjb.getRegistriUnitaDocAbilitati(idUtente, idStrut);

            // Ricavo i valori della combo TIPO UD
            tmpTableBeanTipoUd = tipoUnitaDocEjb.getTipiUnitaDocAbilitati(idUtente, idStrut);

        } catch (Exception ex) {
            log.error(
                    "Errore durante il recupero dei filtri per la ricerca ud nelle proposte di scarto",
                    ex);
        }

        DecodeMap mappaRegistro = new DecodeMap();
        mappaRegistro.populatedMap(tmpTableBeanRegistro, "id_registro_unita_doc",
                "cd_registro_unita_doc");
        getForm().getFiltriRicercaUdPropScarto().getId_registro_unita_doc()
                .setDecodeMap(mappaRegistro);

        DecodeMap mappaTipoUd = new DecodeMap();
        mappaTipoUd.populatedMap(tmpTableBeanTipoUd, "id_tipo_unita_doc", "nm_tipo_unita_doc");
        getForm().getFiltriRicercaUdPropScarto().getId_tipo_unita_doc().setDecodeMap(mappaTipoUd);

        // Popolo tipo ud e registro anche per la ricerca ud già in proposta
        getForm().getFiltriRicercaUdSelezionate().getId_registro_unita_doc_sel()
                .setDecodeMap(mappaRegistro);
        getForm().getFiltriRicercaUdSelezionate().getId_tipo_unita_doc_sel()
                .setDecodeMap(mappaTipoUd);

    }

    private void popolaAmbienteEnteStrutturaCreazionePropScarto() {

        BigDecimal idStrut = getUser().getIdOrganizzazioneFoglia();
        OrgStrutRowBean strutRowBean = struttureEjb.getOrgStrutRowBean(idStrut);

        // Ricavo id struttura, ente ed ambiente attuali
        BigDecimal idEnte = strutRowBean.getIdEnte();
        BigDecimal idAmbiente = strutRowBean.getBigDecimal("id_ambiente");

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
            tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(),
                    idEnte, Boolean.FALSE);

        } catch (ParerUserError ex) {
            log.error(
                    "Errore durante il recupero dei filtri per la ricerca ud nelle proposte di scarto",
                    ex);
        }

        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        getForm().getCreazionePropScartoVers().getId_ambiente().setDecodeMap(mappaAmbiente);
        getForm().getCreazionePropScartoVers().getId_ambiente().setValue(idAmbiente.toString());

        DecodeMap mappaEnte = new DecodeMap();
        mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
        getForm().getCreazionePropScartoVers().getId_ente().setDecodeMap(mappaEnte);
        getForm().getCreazionePropScartoVers().getId_ente().setValue(idEnte.toString());

        DecodeMap mappaStrut = new DecodeMap();
        mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
        getForm().getCreazionePropScartoVers().getId_strut().setDecodeMap(mappaStrut);
        getForm().getCreazionePropScartoVers().getId_strut().setValue(idStrut.toString());
    }

    @Override
    public void selectUd() throws Throwable {
        getForm().getFiltriRicercaUdPropScarto().post(getRequest());

        // Verifico se sto "forzando" (click su "Sì" del popup) o se è il primo click
        String forza = getForm().getFiltriRicercaUdPropScarto().getHidden_forza_aggiunta()
                .getValue();
        List<Long> idDaProcessare = new ArrayList<>();

        // Se vengo dal popup di Conferma...
        if ("SI".equals(forza)) {
            // Recupero gli ID delle UD selezionate da processare
            String ids = getForm().getFiltriRicercaUdPropScarto().getHidden_ud_da_aggiungere()
                    .getValue();
            for (String s : ids.split(","))
                idDaProcessare.add(Long.valueOf(s));

            // Pulisco e procedo
            getForm().getFiltriRicercaUdPropScarto().getHidden_forza_aggiunta().setValue("");
            getForm().getFiltriRicercaUdPropScarto().getHidden_ud_da_aggiungere().setValue("");
            eseguiSpostamentoESalvataggio(idDaProcessare, true);
        } else {
            // Se è il primo click, recupero le ud sulla base dei check
            String[] indexUdDaSel = getRequest().getParameterValues("Fl_ud_da_sel");
            if (indexUdDaSel != null && indexUdDaSel.length > 0) {
                int nTot = 0, nNonRagg = 0, nSenzaInd = 0, nIllim = 0;
                BaseTable table = (BaseTable) getForm().getUdDaSelPropScartoList().getTable();

                for (String idx : indexUdDaSel) {
                    BaseRow riga = (BaseRow) table.getRow(Integer.parseInt(idx));
                    Long idUd = riga.getBigDecimal("id_unita_doc").longValue();
                    idDaProcessare.add(idUd);

                    // Controllo alert
                    String alert = riga.getString("ds_alert_scarto");
                    if (StringUtils.isNotBlank(alert)) {
                        nTot++;
                        if (alert.contains("non raggiunto"))
                            nNonRagg++;
                        if (alert.contains("Senza indicazione"))
                            nSenzaInd++;
                        if (alert.contains("illimitata"))
                            nIllim++;
                    }
                }

                if (nTot > 0) {
                    // Se ci sono incoerenze, preparo l'alert
                    getForm().getFiltriRicercaUdPropScarto().getHidden_ud_da_aggiungere()
                            .setValue(StringUtils.join(idDaProcessare, ","));
                    getRequest().setAttribute("numAlertTotale", nTot);
                    getRequest().setAttribute("numAlertNonRaggiunto", nNonRagg);
                    getRequest().setAttribute("numAlertSenzaInd", nSenzaInd);
                    getRequest().setAttribute("numAlertIllimitata", nIllim);
                    getRequest().setAttribute("azioneInSospeso", "selectUd");
                } else {
                    eseguiSpostamentoESalvataggio(idDaProcessare, true);
                }
            }
        }
        forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
    }

    @Override
    public void deselectUd() throws Throwable {
        // Leggo le checkbox della lista delle ud presenti in proposta
        getForm().getUdSelezionatePropScartoList().post(getRequest());
        String[] indexUdSelected = getRequest().getParameterValues("Fl_ud_selected");

        if (indexUdSelected != null && indexUdSelected.length > 0) {
            List<Long> idDaRimuovere = new ArrayList<>();
            BaseTable tableSel = (BaseTable) getForm().getUdSelezionatePropScartoList().getTable();

            // Recupero gli ID delle UD dalle righe spuntate
            for (String idx : indexUdSelected) {
                if (StringUtils.isNotBlank(idx) && StringUtils.isNumeric(idx)) {
                    BaseRow riga = (BaseRow) tableSel.getRow(Integer.parseInt(idx));
                    BigDecimal idUd = riga.getBigDecimal("id_unita_doc");
                    if (idUd != null) {
                        idDaRimuovere.add(idUd.longValue());
                    }
                }
            }

            // Eseguo la rimozione direttamente da DB (oltre che dalla lista nell'online)
            if (!idDaRimuovere.isEmpty()) {
                eseguiSpostamentoESalvataggio(idDaRimuovere, false);
            }
        }
        forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
    }

    @Override
    public void selectAllUd() throws Throwable {
        getForm().getFiltriRicercaUdPropScarto().post(getRequest());
        String forza = getForm().getFiltriRicercaUdPropScarto().getHidden_forza_aggiunta()
                .getValue();
        List<Long> idDaProcessare = new ArrayList<>();

        if ("SI".equals(forza)) {
            String ids = getForm().getFiltriRicercaUdPropScarto().getHidden_ud_da_aggiungere()
                    .getValue();
            for (String s : ids.split(","))
                idDaProcessare.add(Long.valueOf(s));
            getForm().getFiltriRicercaUdPropScarto().getHidden_forza_aggiunta().setValue("");
            getForm().getFiltriRicercaUdPropScarto().getHidden_ud_da_aggiungere().setValue("");
            eseguiSpostamentoESalvataggio(idDaProcessare, true);
        } else {
            // Aggiungi TUTTE
            BaseTable table = (BaseTable) getForm().getUdDaSelPropScartoList().getTable();
            int nTot = 0, nNonRagg = 0, nSenzaInd = 0, nIllim = 0;

            for (int i = 0; i < table.size(); i++) {
                BaseRow riga = (BaseRow) table.getRow(i);
                idDaProcessare.add(riga.getBigDecimal("id_unita_doc").longValue());

                String alert = riga.getString("ds_alert_scarto");
                if (StringUtils.isNotBlank(alert)) {
                    nTot++;
                    if (alert.contains("non raggiunto"))
                        nNonRagg++;
                    if (alert.contains("Senza indicazione"))
                        nSenzaInd++;
                    if (alert.contains("illimitata"))
                        nIllim++;
                }
            }

            if (nTot > 0) {
                getForm().getFiltriRicercaUdPropScarto().getHidden_ud_da_aggiungere()
                        .setValue(StringUtils.join(idDaProcessare, ","));
                getRequest().setAttribute("numAlertTotale", nTot);
                getRequest().setAttribute("numAlertNonRaggiunto", nNonRagg);
                getRequest().setAttribute("numAlertSenzaInd", nSenzaInd);
                getRequest().setAttribute("numAlertIllimitata", nIllim);
                getRequest().setAttribute("azioneInSospeso", "selectAllUd");
            } else {
                eseguiSpostamentoESalvataggio(idDaProcessare, true);
            }
        }
        forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
    }

    @Override
    public void deselectAllUd() throws Throwable {
        // Recupero la tabella delle selezionate
        BaseTable tableSel = (BaseTable) getForm().getUdSelezionatePropScartoList().getTable();

        if (tableSel != null && tableSel.size() > 0) {
            List<Long> idDaRimuovere = new ArrayList<>();

            // Prendo gli ID di TUTTE le righe
            for (int i = 0; i < tableSel.size(); i++) {
                BaseRow riga = (BaseRow) tableSel.getRow(i);
                BigDecimal idUd = riga.getBigDecimal("id_unita_doc");
                if (idUd != null) {
                    idDaRimuovere.add(idUd.longValue());
                }
            }

            // Eseguo la rimozione
            if (!idDaRimuovere.isEmpty()) {
                eseguiSpostamentoESalvataggio(idDaRimuovere, false);
            }
        }
        forwardToPublisher(Application.Publisher.CREAZIONE_PROP_SCARTO_VERS);
    }

    @Override
    public void deletePropScartoVersList() throws EMFError {
        AroVRicPropScartoVersRowBean row = (AroVRicPropScartoVersRowBean) getForm()
                .getPropScartoVersList().getTable().getCurrentRow();
        int riga = getForm().getPropScartoVersList().getTable().getCurrentRowIndex();
        BigDecimal idPropScartoVers = row.getIdPropScartoVers();
        /* Se non posso eliminare la proposta, avverto l'utente... */
        if (!scartoEjb.isPropostaDeletable(idPropScartoVers)) {
            getMessageBox().addError(
                    "La proposta di scarto versamenti è in stato diverso da APERTA. Impossibile procedere");
        } /* ...altrimenti procedo con l'eliminazione della proposta */ else {
            try {
                scartoEjb.deletePropScartoVers(idPropScartoVers);
                getForm().getPropScartoVersList().getTable().remove(riga);
                getMessageBox().addInfo("Proposta eliminata con successo");
            } catch (ParerUserError e) {
                getMessageBox().addError(
                        "Errore durante l'eliminazione della proposta di scarto versamenti");
            } finally {
                String lastPublisher = getLastPublisher();
                if (Application.Publisher.CREAZIONE_PROP_SCARTO_VERS.equals(lastPublisher)) {
                    goBack();
                } else {
                    forwardToPublisher(Application.Publisher.RICERCA_PROP_SCARTO_VERS);
                }
            }
        }
    }

    @Override
    public void deleteCreazionePropScartoVers() throws EMFError {
        deletePropScartoVersList();
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="UI Ricerca richieste scarto versamenti">
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

    // <editor-fold defaultstate="collapsed" desc="UI Dettaglio richiesta scarto versamenti">
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

}
