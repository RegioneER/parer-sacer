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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import javax.ejb.EJB;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoFascicolo.ejb.TipoFascicoloEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoFascicolo.helper.TipoFascicoloHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.fascicoli.ejb.FascicoliEjb;
import it.eng.parer.fascicoli.ejb.FascicoliEjb.DettaglioVersamentoFascicoloKo;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.MonitoraggioFascicoliAbstractAction;
import it.eng.parer.slite.gen.form.FascicoliForm;
import it.eng.parer.slite.gen.form.MonitoraggioFascicoliForm;
import it.eng.parer.slite.gen.form.MonitoraggioFascicoliForm.DettaglioSessFascKo;
import it.eng.parer.slite.gen.form.MonitoraggioFascicoliForm.FiltriFascicoli;
import it.eng.parer.slite.gen.form.MonitoraggioFascicoliForm.RiepilogoVersamentiFascicoli;
import it.eng.parer.slite.gen.tablebean.DecTipoFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.slite.gen.tablebean.VrsSesFascicoloKoTableBean;
import it.eng.parer.slite.gen.viewbean.MonVChkCntFascRowBean;
import it.eng.parer.slite.gen.viewbean.MonVLisFascKoRowBean;
import it.eng.parer.slite.gen.viewbean.MonVLisFascKoTableBean;
import it.eng.parer.slite.gen.viewbean.MonVLisFascTableBean;
import it.eng.parer.web.ejb.MonitoraggioSinteticoEjb;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Utils;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.web.validator.MonitoraggioFascicoliValidator;
import it.eng.parer.web.validator.TypeValidator.ChiaveBean;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.Component;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.base.BaseForm;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.form.fields.impl.ComboBox;
import it.eng.spagoLite.security.Secure;

/**
 *
 * @author Iacolucci_M
 */
public class MonitoraggioFascicoliAction extends MonitoraggioFascicoliAbstractAction {

    private static final Logger logger = LoggerFactory.getLogger(MonitoraggioFascicoliAction.class);

    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioSinteticoEjb")
    private MonitoraggioSinteticoEjb monitSintEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoFascicoloEjb")
    private TipoFascicoloEjb tipoFascicoloEjb;
    @EJB(mappedName = "java:app/Parer-ejb/FascicoliEjb")
    private FascicoliEjb fascicoliEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoFascicoloHelper")
    private TipoFascicoloHelper tipoFascicoloHelper;

    public static final String PAR_TI_CREAZIONE = "ti_creazione";
    public static final String TI_CREAZIONE_OGGI = "OGGI";
    public static final String TI_CREAZIONE_30GG = "30gg";
    public static final String TI_CREAZIONE_B30GG = "B30gg";

    public static final String PAR_TI_STATO = "ti_stato";
    public static final String TI_STATO_TUTTI = "TUTTI";
    public static final String TI_STATO_ATT_MEM = "IN_ATTESA_MEMORIZZAZIONE";
    public static final String TI_STATO_NON_RISOLUBILE = "NON_RISOLUBILE";
    public static final String TI_STATO_VERIFICATO = "VERIFICATO";
    public static final String TI_STATO_NON_VERIFICATO = "NON_VERIFICATO";
    public static final String TI_STATO_IN_ATTESA_SCHED = "IN_ATTESA_SCHED";

    @Secure(action = "Menu.Monitoraggio.RiepilogoVersamentiFascicoli")
    public void loadRiepilogoVersamentiFascicoli() throws EMFError, Throwable {
        try {
            // BigDecimal idStrut = new BigDecimal(getRequest().getParameter("idStrut") != null ? (String)
            // getRequest().getParameter("idStrut") : "0");
            getUser().getMenu().reset();
            getUser().getMenu().select("Menu.Monitoraggio.RiepilogoVersamentiFascicoli");
            // Resetto tutti i campi di riepilogo versamenti (filtri e totali)
            getForm().getRiepilogoVersamentiFascicoli().reset();
            // Ricavo id struttura, ente ed ambiente attuali
            BigDecimal idStruttura = getUser().getIdOrganizzazioneFoglia();
            BigDecimal idEnte = monitSintEjb.getEnte(idStruttura);
            BigDecimal idAmbiente = monitSintEjb.getAmbiente(idEnte);
            // Inizializzo le combo settando la struttura corrente
            // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
            OrgAmbienteTableBean tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());
            // Ricavo i valori della combo ENTE
            OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(),
                    idAmbiente.longValue(), Boolean.TRUE);
            // Ricavo i valori della combo STRUTTURA
            OrgStrutTableBean tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte,
                    Boolean.TRUE);
            // Ricavo i valori della combo TIPO FASCICOLO.
            DecTipoFascicoloTableBean tmpTableBeanFascicolo = tipoFascicoloEjb
                    .getTipiFascicoloAbilitati(getUser().getIdUtente(), idStruttura, false);
            getForm().getRiepilogoVersamentiFascicoli().getId_ambiente()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente"));
            getForm().getRiepilogoVersamentiFascicoli().getId_ente()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanEnte, "id_ente", "nm_ente"));
            getForm().getRiepilogoVersamentiFascicoli().getId_strut()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanStruttura, "id_strut", "nm_strut"));
            getForm().getRiepilogoVersamentiFascicoli().getId_tipo_fascicolo().setDecodeMap(
                    DecodeMap.Factory.newInstance(tmpTableBeanFascicolo, "id_tipo_fascicolo", "nm_tipo_fascicolo"));
            getForm().getRiepilogoVersamentiFascicoli().getId_ambiente().setValue(idAmbiente.toString());
            getForm().getRiepilogoVersamentiFascicoli().getId_ente().setValue(idEnte.toString());
            getForm().getRiepilogoVersamentiFascicoli().getId_strut().setValue(idStruttura.toString());
            calcolaRiepilogo(idAmbiente, idEnte, idStruttura, null);

            calcTotFascicoliVersatiButton();
            calcTotFascicoliVersFallitiButton();
            postLoad();
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore inatteso nel caricamento della pagina");
        }
        // Eseguo forward alla stessa pagina
        forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FASCICOLI);
    }

    @Secure(action = "Menu.Logging.ListaSessioniFascicoliErrate")
    public void loadListaSessFascicoliErr() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Logging.ListaSessioniFascicoliErrate");
        // Resetto tutti i campi di riepilogo versamenti (filtri e totali)
        getForm().getFiltriSessioniFascicoli().reset();
        getForm().getSesFascicoliErrList().clear();
        getForm().getFiltriSessioniFascicoli().getTi_stato_ses().setDecodeMap(ComboGetter
                .getMappaSortedGenericEnum("ti_stato_ses", FascicoliEjb.statoSessioneFascicoliErrata.values()));
        getForm().getFiltriSessioniFascicoli().getCd_classe_err()
                .setDecodeMap(fascicoliEjb.getClasseErrSacerByTipiUsoDecodeMap(
                        Arrays.asList(new String[] { FascicoliEjb.tipoUsoClasseErrore.VERS_FASCICOLO.name(),
                                FascicoliEjb.tipoUsoClasseErrore.GENERICO.name() })));
        postLoad();
        forwardToPublisher(Application.Publisher.LISTA_SESS_FASC_ERRATE);
    }

    @Override
    public void initOnClick() throws EMFError {
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
            if (getTableName().equals(getForm().getFascicoliList().getName())) {
                //
            } else if (getTableName().equals(getForm().getFascicoliKoList().getName())) {
                MonVLisFascKoTableBean t = (MonVLisFascKoTableBean) getForm().getFascicoliKoList().getTable();
                MonVLisFascKoRowBean r = t.getCurrentRow();
                // Verifica se utente abilitato al tipo fascicolo
                if (tipoFascicoloHelper.isTipoFascicoloAbilitato(getUser().getIdUtente(), r.getIdStrut(),
                        r.getIdTipoFascicolo(), false)) {
                    getForm().getDettaglioFascVersKo().getNm_ambiente_ente_struttura()
                            .setValue(r.getNmAmbiente() + "/" + r.getNmEnte() + "/" + r.getNmStrut());
                    getForm().getDettaglioFascVersKo().getAa_fascicolo().setValue(r.getAaFascicolo().toPlainString());
                    getForm().getDettaglioFascVersKo().getCd_key_fascicolo().setValue(r.getCdKeyFascicolo());
                    getForm().getDettaglioFascVersKo().getNm_tipo_fascicolo().setValue(r.getNmTipoFascicolo());
                    getForm().getDettaglioFascVersKo().getId_ses_ko_first()
                            .setValue(r.getIdSesFascicoloKoFirst().toPlainString());
                    getForm().getDettaglioFascVersKo().getTs_ini_first_ses().setValue(r.getTsIniFirstSes().toString());
                    getForm().getDettaglioFascVersKo().getId_ses_ko_last()
                            .setValue(r.getIdSesFascicoloKoLast().toPlainString());
                    getForm().getDettaglioFascVersKo().getTs_ini_last_ses().setValue(r.getTsIniLastSes().toString());
                    getForm().getDettaglioFascVersKo().getCd_err_princ().setValue(r.getCdErrPrinc());
                    getForm().getDettaglioFascVersKo().getDs_err_princ().setValue(r.getDsErrPrinc());
                    getForm().getDettaglioFascVersKo().getTi_stato_fascicolo_ko().setValue(r.getTiStatoFascicoloKo());
                    VrsSesFascicoloKoTableBean tl = fascicoliEjb.ricercaVersamentiFascicoliKo(r.getIdFascicoloKo());
                    getForm().getVersamentiFascicoliKoList().setTable(tl);
                    getForm().getVersamentiFascicoliKoList().getTable().first();
                    getForm().getVersamentiFascicoliKoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    forwardToPublisher(Application.Publisher.DETT_FASCICOLI_VERS_KO);
                } else {
                    getMessageBox().addWarning(
                            "Utente non abilitato a visualizzare i fascicoli di tipo '" + r.getNmTipoFascicolo() + "'");
                    forwardToPublisher(getLastPublisher());
                }
                // DETTAGLIO VERSAMENTO FASCICOLO FALLITO
            } else if (getTableName().equals(getForm().getVersamentiFascicoliKoList().getName())) {
                BigDecimal idSesFascicolo = getForm().getVersamentiFascicoliKoList().getTable().getCurrentRow()
                        .getBigDecimal("id_ses_fascicolo_ko");
                DettaglioVersamentoFascicoloKo dett = fascicoliEjb.getDettaglioVersamentoFascicoloKo(idSesFascicolo);
                getForm().getDettaglioVersamentoKo().copyFromBean(dett.getDettaglioVersamentoRB());
                MonVLisFascKoTableBean t = (MonVLisFascKoTableBean) getForm().getFascicoliKoList().getTable();
                MonVLisFascKoRowBean r = t.getCurrentRow();
                getForm().getDettaglioVersamentoKo().getNm_ambiente_ente_struttura()
                        .setValue(r.getNmAmbiente() + "/" + r.getNmEnte() + "/" + r.getNmStrut());
                getForm().getDettaglioVersamentoKoTabs()
                        .setCurrentTab(getForm().getDettaglioVersamentoKoTabs().getIndiceSip());
                BaseTable tab = dett.getListaErroriTB();
                if (tab != null) {
                    getForm().getErroriVersList().setTable(tab);
                    getForm().getErroriVersList().getTable().first();
                    getForm().getErroriVersList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                }
            } else if (getTableName().equals(getForm().getSesFascicoliErrList().getName())) {
                caricaDatiDettSessFascicoloErrato();
            }
        }
    }

    private void caricaDatiDettSessFascicoloErrato() throws EMFError {
        BigDecimal idSesFascicolo = getForm().getSesFascicoliErrList().getTable().getCurrentRow()
                .getBigDecimal("id_ses_fascicolo_err");
        BaseRow r = fascicoliEjb.caricaDettaglioSessFascErrata(idSesFascicolo);
        getForm().getDettaglioSessFascKo().copyFromBean(r);
        OrgAmbienteTableBean tmpTableBeanAmbiente;
        try {
            // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
            tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());
            getForm().getDettaglioSessFascKo().getId_ambiente()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente"));
            BigDecimal id = Utils.getDecodedBigDecimalFromTablebean(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente",
                    r.getString("nm_ambiente"));
            getForm().getDettaglioSessFascKo().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getDettaglioSessFascKo().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getDettaglioSessFascKo().getId_tipo_fascicolo().setDecodeMap(new DecodeMap());
            // Ricavo i valori della combo ENTE
            if (id != null) {
                getForm().getDettaglioSessFascKo().getId_ambiente().setValue(id.toPlainString());
                OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(),
                        id.longValueExact(), Boolean.TRUE);
                getForm().getDettaglioSessFascKo().getId_ente()
                        .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanEnte, "id_ente", "nm_ente"));
                id = Utils.getDecodedBigDecimalFromTablebean(tmpTableBeanEnte, "id_ente", "nm_ente",
                        r.getString("nm_ente"));
                // Ricavo i valori della combo STRUTTURA
                if (id != null) {
                    getForm().getDettaglioSessFascKo().getId_ente().setValue(id.toPlainString());
                    // Ricavo i valori della combo STRUTTURA
                    OrgStrutTableBean tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(),
                            id, Boolean.TRUE);
                    getForm().getDettaglioSessFascKo().getId_strut()
                            .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanStruttura, "id_strut", "nm_strut"));
                    id = Utils.getDecodedBigDecimalFromTablebean(tmpTableBeanStruttura, "id_strut", "nm_strut",
                            r.getString("nm_strut"));
                    if (id != null) {
                        getForm().getDettaglioSessFascKo().getId_strut().setValue(id.toPlainString());
                        // Ricavo i valori della combo TIPO FASCICOLO.
                        DecTipoFascicoloTableBean tmpTableBeanFascicolo = tipoFascicoloEjb
                                .getTipiFascicoloAbilitati(getUser().getIdUtente(), id, false);
                        getForm().getDettaglioSessFascKo().getId_tipo_fascicolo().setDecodeMap(DecodeMap.Factory
                                .newInstance(tmpTableBeanFascicolo, "id_tipo_fascicolo", "nm_tipo_fascicolo"));
                        id = r.getBigDecimal("id_tipo_fascicolo");
                        if (id != null) {
                            getForm().getDettaglioSessFascKo().getId_tipo_fascicolo().setValue(id.toPlainString());
                        }
                    }
                }
            }
        } catch (ParerUserError ex) {
            throw new EMFError("ERROR", ex);
        }
        getForm().getDettaglioSessFascKoTabs()
                .setCurrentTab(getForm().getDettaglioSessFascKoTabs().getIndiceSipDettSessFascKo());
        getForm().getSesFascicoliErrList().setStatus(Status.view);
    }

    @Override
    public void undoDettaglio() throws EMFError {
        if (getTableName().equals(getForm().getSesFascicoliErrList().getName())) {
            caricaDatiDettSessFascicoloErrato();
        }
    }

    @Override
    public void saveDettaglio() throws EMFError {
        if (getTableName().equals(getForm().getSesFascicoliErrList().getName())) {
            getForm().getDettaglioSessFascKo().post(getRequest());
            if (getForm().getDettaglioSessFascKo().validate(getMessageBox())) {
                FascicoliEjb.SalvaDettaSessErrDto dto = fascicoliEjb.salvaDettaglioSessioneFascicoloErr(
                        getForm().getDettaglioSessFascKo().getId_ses_fascicolo_err().parse(),
                        getForm().getDettaglioSessFascKo().getId_ambiente().parse(),
                        getForm().getDettaglioSessFascKo().getId_ente().parse(),
                        getForm().getDettaglioSessFascKo().getId_strut().parse(),
                        getForm().getDettaglioSessFascKo().getId_tipo_fascicolo().parse(),
                        getForm().getDettaglioSessFascKo().getAa_fascicolo().parse(),
                        getForm().getDettaglioSessFascKo().getCd_key_fascicolo().getValue());
                if (dto.getMsg() == null) {
                    getMessageBox().addInfo("Sessione modificata con successo.");
                    getForm().getSesFascicoliErrList().setStatus(Status.view);
                } else {
                    getMessageBox().addError(dto.getMsg());
                }
                postLoad();
                if (dto.isSessioneCancellata()) {
                    // Rinfresca la lista sessioni errate che non avrà più il record appena cancellato
                    ricercaSessFascicoliErrateButtonInternal();
                    forwardToPublisher(Application.Publisher.LISTA_SESS_FASC_ERRATE);
                } else {
                    forwardToPublisher(getLastPublisher());
                }
            }
        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        if (getTableName().equals(getForm().getFascicoliList().getName())) {
            FascicoliForm form = new FascicoliForm();
            redirectToPage(Application.Actions.FASCICOLI, form, getForm().getFascicoliList().getName(),
                    getForm().getFascicoliList().getTable(), getNavigationEvent());
        } else if (getTableName().equals(getForm().getFascicoliKoList().getName())) {
            // Gia' gestito nella load dettaglio
        } else if (getTableName().equals(getForm().getVersamentiFascicoliKoList().getName())) {
            forwardToPublisher(Application.Publisher.DETT_VERS_FASCICOLO_FALLITO);
        } else if (getTableName().equals(getForm().getSesFascicoliErrList().getName())) {
            forwardToPublisher(Application.Publisher.DETT_SESS_FASCICOLO_ERRATO);
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.MONITORAGGIO_VERS_FASCICOLI;
    }

    @Override
    public void process() throws EMFError {
    }

    @Override
    public void reloadAfterGoBack(String string) {
    }

    @Override
    public String getControllerName() {
        return Application.Actions.MONITORAGGIO_FASCICOLI;
    }

    /*
     * Azioni dei Pulsanti della form
     */
    @Override
    public void generaRiepilogoVersFascicoliButton() throws EMFError {
        if (getForm().getRiepilogoVersamentiFascicoli().postAndValidate(getRequest(), getMessageBox())) {
            BigDecimal idAmbiente = getForm().getRiepilogoVersamentiFascicoli().getId_ambiente().parse();
            BigDecimal idEnte = getForm().getRiepilogoVersamentiFascicoli().getId_ente().parse();
            BigDecimal idStruttura = getForm().getRiepilogoVersamentiFascicoli().getId_strut().parse();
            BigDecimal idTipoFascicolo = getForm().getRiepilogoVersamentiFascicoli().getId_tipo_fascicolo().parse();
            calcolaRiepilogo(idAmbiente, idEnte, idStruttura, idTipoFascicolo);
        }
        forwardToPublisher(getLastPublisher());
    }

    private void calcolaRiepilogo(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStruttura,
            BigDecimal idTipofascicolo) throws EMFError {
        MonVChkCntFascRowBean rowBean = fascicoliEjb.calcolaRiepilogo(new BigDecimal(getUser().getIdUtente()),
                idAmbiente, idEnte, idStruttura, idTipofascicolo);
        getForm().getFascicoliVersati().copyFromBean(rowBean);
        getForm().getVersamentiFalliti().copyFromBean(rowBean);
    }

    @Override
    public void calcTotFascicoliVersatiButton() throws Throwable {
        if (getForm().getRiepilogoVersamentiFascicoli().postAndValidate(getRequest(), getMessageBox())) {
            BigDecimal idUser = new BigDecimal(getUser().getIdUtente());
            BigDecimal idAmbiente = getForm().getRiepilogoVersamentiFascicoli().getId_ambiente().parse();
            BigDecimal idEnte = getForm().getRiepilogoVersamentiFascicoli().getId_ente().parse();
            BigDecimal idStruttura = getForm().getRiepilogoVersamentiFascicoli().getId_strut().parse();
            BigDecimal idTipoFascicolo = getForm().getRiepilogoVersamentiFascicoli().getId_tipo_fascicolo().parse();
            MonVChkCntFascRowBean rowBean = new MonVChkCntFascRowBean();
            getForm().getFascicoliVersati().copyToBean(rowBean);
            getForm().getVersamentiFalliti().copyToBean(rowBean);
            rowBean = fascicoliEjb.calcolaTotFascicoliVersati(rowBean, idUser, idAmbiente, idEnte, idStruttura,
                    idTipoFascicolo);
            getForm().getFascicoliVersati().copyFromBean(rowBean);
            getForm().getVersamentiFalliti().copyFromBean(rowBean);
        }
    }

    @Override
    public void calcTotFascicoliVersFallitiButton() throws Throwable {
        if (getForm().getRiepilogoVersamentiFascicoli().postAndValidate(getRequest(), getMessageBox())) {
            BigDecimal idUser = new BigDecimal(getUser().getIdUtente());
            BigDecimal idAmbiente = getForm().getRiepilogoVersamentiFascicoli().getId_ambiente().parse();
            BigDecimal idEnte = getForm().getRiepilogoVersamentiFascicoli().getId_ente().parse();
            BigDecimal idStruttura = getForm().getRiepilogoVersamentiFascicoli().getId_strut().parse();
            BigDecimal idTipoFascicolo = getForm().getRiepilogoVersamentiFascicoli().getId_tipo_fascicolo().parse();
            MonVChkCntFascRowBean rowBean = new MonVChkCntFascRowBean();
            getForm().getFascicoliVersati().copyToBean(rowBean);
            getForm().getVersamentiFalliti().copyToBean(rowBean);
            rowBean = fascicoliEjb.calcolaTotFascicoliFalliti(rowBean, idUser, idAmbiente, idEnte, idStruttura,
                    idTipoFascicolo);
            getForm().getFascicoliVersati().copyFromBean(rowBean);
            getForm().getVersamentiFalliti().copyFromBean(rowBean);
        }
    }

    public void listaFascicoliVersati() throws EMFError {
        getForm().getFiltriFascicoli().reset();
        getForm().getFiltriFascicoli().getId_ambiente()
                .setDecodeMap(getForm().getRiepilogoVersamentiFascicoli().getId_ambiente().getDecodeMap());
        getForm().getFiltriFascicoli().getId_ambiente()
                .setValue(getForm().getRiepilogoVersamentiFascicoli().getId_ambiente().getValue());
        getForm().getFiltriFascicoli().getId_ente()
                .setDecodeMap(getForm().getRiepilogoVersamentiFascicoli().getId_ente().getDecodeMap());
        getForm().getFiltriFascicoli().getId_ente()
                .setValue(getForm().getRiepilogoVersamentiFascicoli().getId_ente().getValue());
        getForm().getFiltriFascicoli().getId_strut()
                .setDecodeMap(getForm().getRiepilogoVersamentiFascicoli().getId_strut().getDecodeMap());
        getForm().getFiltriFascicoli().getId_strut()
                .setValue(getForm().getRiepilogoVersamentiFascicoli().getId_strut().getValue());
        getForm().getFiltriFascicoli().getId_tipo_fascicolo()
                .setDecodeMap(getForm().getRiepilogoVersamentiFascicoli().getId_tipo_fascicolo().getDecodeMap());
        getForm().getFiltriFascicoli().getId_tipo_fascicolo()
                .setValue(getForm().getRiepilogoVersamentiFascicoli().getId_tipo_fascicolo().getValue());
        getForm().getFiltriFascicoli().getFl_ses_fascicolo_ko().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriFascicoli().getTi_stato_conservazione().setDecodeMap(ComboGetter.getMappaSortedGenericEnum(
                "ti_stato_conservazione", FascicoliEjb.statoConservazioneFascicoliVersati.values()));
        getForm().getFiltriFascicoli().getTi_stato_fasc_elenco_vers()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato_fasc_elenco_vers",
                        ElencoEnums.StatoGenerazioneIndiceAip.values()));

        // analisi del tipo creazione selezionato
        String tiCreazione = getRequest().getParameter(PAR_TI_CREAZIONE);
        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
        Date oggi = new Date();
        switch (tiCreazione) {
        case TI_CREAZIONE_OGGI:
            getForm().getFiltriFascicoli().getGiorno_vers_da().setValue(df.format(oggi));
            getForm().getFiltriFascicoli().getGiorno_vers_a().setValue(df.format(oggi));
            break;
        case TI_CREAZIONE_30GG:
            Date oggiMeno30Gg = new DateTime(oggi).minusDays(30).toDate();
            Date oggiMeno1Gg = new DateTime(oggi).minusDays(1).toDate();
            getForm().getFiltriFascicoli().getGiorno_vers_da().setValue(df.format(oggiMeno30Gg));
            getForm().getFiltriFascicoli().getGiorno_vers_a().setValue(df.format(oggiMeno1Gg));
            break;
        case TI_CREAZIONE_B30GG:
            Date before30Gg = new DateTime(2000, 1, 1, 0, 0, 0, 0).toDate();
            getForm().getFiltriFascicoli().getGiorno_vers_da().setValue(df.format(before30Gg));
            getForm().getFiltriFascicoli().getGiorno_vers_a()
                    .setValue(df.format(new DateTime(oggi).minusDays(31).toDate()));
            break;
        }
        getForm().getFiltriFascicoli().getOre_vers_da().setValue("00");
        getForm().getFiltriFascicoli().getOre_vers_a().setValue("23");
        getForm().getFiltriFascicoli().getMinuti_vers_da().setValue("00");
        getForm().getFiltriFascicoli().getMinuti_vers_a().setValue("59");

        // analisi dello stato selezionato
        String tiStato = getRequest().getParameter(PAR_TI_STATO);
        switch (tiStato) {
        case TI_STATO_TUTTI:
            getForm().getFiltriFascicoli().getTi_stato_fasc_elenco_vers().clear();
            break;
        case TI_STATO_ATT_MEM:
            getForm().getFiltriFascicoli().getTi_stato_fasc_elenco_vers().setValue("IN_ATTESA_SCHED");
            break;
        case TI_STATO_IN_ATTESA_SCHED:
            getForm().getFiltriFascicoli().getTi_stato_fasc_elenco_vers().setValue("NON_SELEZ_SCHED");
            break;
        }
        getForm().getFiltriFascicoli().getTi_stato_conservazione().clear();
        getForm().getFiltriFascicoli().getFl_ses_fascicolo_ko().clear();
        internalRicercaFascicoli();
        forwardToPublisher(Application.Publisher.LISTA_FASCICOLI);
    }

    public void listaFascicoliVersamentiFalliti() throws EMFError {
        getForm().getFiltriFascicoli().reset();
        getForm().getFiltriFascicoli().getId_ambiente()
                .setDecodeMap(getForm().getRiepilogoVersamentiFascicoli().getId_ambiente().getDecodeMap());
        getForm().getFiltriFascicoli().getId_ambiente()
                .setValue(getForm().getRiepilogoVersamentiFascicoli().getId_ambiente().getValue());
        getForm().getFiltriFascicoli().getId_ente()
                .setDecodeMap(getForm().getRiepilogoVersamentiFascicoli().getId_ente().getDecodeMap());
        getForm().getFiltriFascicoli().getId_ente()
                .setValue(getForm().getRiepilogoVersamentiFascicoli().getId_ente().getValue());
        getForm().getFiltriFascicoli().getId_strut()
                .setDecodeMap(getForm().getRiepilogoVersamentiFascicoli().getId_strut().getDecodeMap());
        getForm().getFiltriFascicoli().getId_strut()
                .setValue(getForm().getRiepilogoVersamentiFascicoli().getId_strut().getValue());
        getForm().getFiltriFascicoli().getId_tipo_fascicolo()
                .setDecodeMap(getForm().getRiepilogoVersamentiFascicoli().getId_tipo_fascicolo().getDecodeMap());
        getForm().getFiltriFascicoli().getId_tipo_fascicolo()
                .setValue(getForm().getRiepilogoVersamentiFascicoli().getId_tipo_fascicolo().getValue());
        getForm().getFiltriFascicoli().getTi_stato_ses().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("ti_stato_ses", FascicoliEjb.statoFascicoliVersFalliti.values()));
        getForm().getFiltriFascicoli().getCd_classe_err()
                .setDecodeMap(fascicoliEjb.getClasseErrSacerDecodeMap(
                        Arrays.asList(new String[] { FascicoliEjb.tipoUsoClasseErrore.VERS_FASCICOLO.name(),
                                FascicoliEjb.tipoUsoClasseErrore.GENERICO.name() })));

        // analisi del tipo creazione selezionato
        String tiCreazione = getRequest().getParameter(PAR_TI_CREAZIONE);
        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
        Date oggi = new Date();
        switch (tiCreazione) {
        case TI_CREAZIONE_OGGI:
            getForm().getFiltriFascicoli().getGiorno_vers_da().setValue(df.format(oggi));
            getForm().getFiltriFascicoli().getGiorno_vers_a().setValue(df.format(oggi));
            break;
        case TI_CREAZIONE_30GG:
            Date oggiMeno30Gg = new DateTime(oggi).minusDays(30).toDate();
            Date oggiMeno1Gg = new DateTime(oggi).minusDays(1).toDate();
            getForm().getFiltriFascicoli().getGiorno_vers_da().setValue(df.format(oggiMeno30Gg));
            getForm().getFiltriFascicoli().getGiorno_vers_a().setValue(df.format(oggiMeno1Gg));
            break;
        case TI_CREAZIONE_B30GG:
            Date before30Gg = new DateTime(2000, 1, 1, 0, 0, 0, 0).toDate();
            getForm().getFiltriFascicoli().getGiorno_vers_da().setValue(df.format(before30Gg));
            getForm().getFiltriFascicoli().getGiorno_vers_a()
                    .setValue(df.format(new DateTime(oggi).minusDays(31).toDate()));
            break;
        }
        getForm().getFiltriFascicoli().getOre_vers_da().setValue("00");
        getForm().getFiltriFascicoli().getOre_vers_a().setValue("23");
        getForm().getFiltriFascicoli().getMinuti_vers_da().setValue("00");
        getForm().getFiltriFascicoli().getMinuti_vers_a().setValue("59");
        // analisi dello stato selezionato
        String tiStato = getRequest().getParameter(PAR_TI_STATO);
        switch (tiStato) {
        case TI_STATO_TUTTI:
            getForm().getFiltriFascicoli().getTi_stato_ses().clear();
            break;
        case TI_STATO_NON_RISOLUBILE:
            getForm().getFiltriFascicoli().getTi_stato_ses().setValue(TI_STATO_NON_RISOLUBILE);
            break;
        case TI_STATO_VERIFICATO:
            getForm().getFiltriFascicoli().getTi_stato_ses().setValue(TI_STATO_VERIFICATO);
            break;
        case TI_STATO_NON_VERIFICATO:
            getForm().getFiltriFascicoli().getTi_stato_ses().setValue(TI_STATO_NON_VERIFICATO);
            break;
        }
        getForm().getFiltriFascicoli().getCd_classe_err().clear();
        getForm().getFiltriFascicoli().getCd_err().clear();
        internalRicercaRicercaFascicoliKo();
        forwardToPublisher(Application.Publisher.LISTA_FASCICOLI_DA_VERS_FALLITI);
    }

    public void loadListaFascicoliVersati() throws EMFError {
        forwardToPublisher(Application.Publisher.LISTA_FASCICOLI);
    }

    @Override
    public void ricercaFascicoliButton() throws EMFError {
        if (getForm().getFiltriFascicoli().postAndValidate(getRequest(), getMessageBox())) {
            internalRicercaFascicoli();
        }
        forwardToPublisher(getLastPublisher());
    }

    // Metodo richiamato sia dal monitoraggio che dal pulsnte dalla ricerca KO
    public void internalRicercaFascicoli() throws EMFError {
        MonitoraggioFascicoliValidator validator = new MonitoraggioFascicoliValidator(getMessageBox());
        Date[] dateValidate = validator.validaDate(getForm().getFiltriFascicoli().getGiorno_vers_da().parse(),
                getForm().getFiltriFascicoli().getOre_vers_da().parse(),
                getForm().getFiltriFascicoli().getMinuti_vers_da().parse(),
                getForm().getFiltriFascicoli().getGiorno_vers_a().parse(),
                getForm().getFiltriFascicoli().getOre_vers_a().parse(),
                getForm().getFiltriFascicoli().getMinuti_vers_a().parse(),
                getForm().getFiltriFascicoli().getGiorno_vers_da().getHtmlDescription(),
                getForm().getFiltriFascicoli().getGiorno_vers_a().getHtmlDescription());
        ChiaveBean bean = validator.validaChiavi(getForm().getChiaveFascicoloSection().getDescription(),
                getForm().getFiltriFascicoli().getAa_fascicolo(), getForm().getFiltriFascicoli().getAa_fascicolo_da(),
                getForm().getFiltriFascicoli().getAa_fascicolo_a(),
                getForm().getFiltriFascicoli().getCd_key_fascicolo(),
                getForm().getFiltriFascicoli().getCd_fascicolo_da(),
                getForm().getFiltriFascicoli().getCd_fascicolo_a());
        if (!getMessageBox().hasError()) {
            BigDecimal idUser = new BigDecimal(getUser().getIdUtente());
            BigDecimal idAmbiente = getForm().getFiltriFascicoli().getId_ambiente().parse();
            BigDecimal idEnte = getForm().getFiltriFascicoli().getId_ente().parse();
            BigDecimal idStruttura = getForm().getFiltriFascicoli().getId_strut().parse();
            BigDecimal idTipoFascicolo = getForm().getFiltriFascicoli().getId_tipo_fascicolo().parse();
            String statoIndiceAip = getForm().getFiltriFascicoli().getTi_stato_fasc_elenco_vers().getValue();
            Set<String> statiConservazione = getForm().getFiltriFascicoli().getTi_stato_conservazione().getValues();
            String flSessioneFascicoloKo = getForm().getFiltriFascicoli().getFl_ses_fascicolo_ko().getValue();
            MonVLisFascTableBean t = fascicoliEjb.ricercaFascicoliPerMonitoraggio(idUser, idAmbiente, idEnte,
                    idStruttura, idTipoFascicolo, dateValidate,
                    bean.isSingleValue() ? bean.getAnno() : bean.getAnnoDa(), bean.getAnnoA(),
                    bean.isSingleValue() ? bean.getNumero() : bean.getNumeroDa(), bean.getNumeroA(), statoIndiceAip,
                    statiConservazione, flSessioneFascicoloKo);
            getForm().getFascicoliList().setTable(t);
            getForm().getFascicoliList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getFascicoliList().getTable().first();
        }
    }

    @Override
    public void ricercaFascicoliKoButton() throws EMFError {
        if (getForm().getFiltriFascicoli().postAndValidate(getRequest(), getMessageBox())) {
            internalRicercaRicercaFascicoliKo();
        }
        forwardToPublisher(getLastPublisher());
    }

    // Metodo richiamato sia dal monitoraggio che dal pulsnte dalla ricerca KO
    private void internalRicercaRicercaFascicoliKo() throws EMFError {
        MonitoraggioFascicoliValidator validator = new MonitoraggioFascicoliValidator(getMessageBox());
        Date[] dateValidate = validator.validaDate(getForm().getFiltriFascicoli().getGiorno_vers_da().parse(),
                getForm().getFiltriFascicoli().getOre_vers_da().parse(),
                getForm().getFiltriFascicoli().getMinuti_vers_da().parse(),
                getForm().getFiltriFascicoli().getGiorno_vers_a().parse(),
                getForm().getFiltriFascicoli().getOre_vers_a().parse(),
                getForm().getFiltriFascicoli().getMinuti_vers_a().parse(),
                getForm().getFiltriFascicoli().getGiorno_vers_da().getHtmlDescription(),
                getForm().getFiltriFascicoli().getGiorno_vers_a().getHtmlDescription());
        ChiaveBean bean = validator.validaChiavi(getForm().getChiaveFascicoloSection().getDescription(),
                getForm().getFiltriFascicoli().getAa_fascicolo(), getForm().getFiltriFascicoli().getAa_fascicolo_da(),
                getForm().getFiltriFascicoli().getAa_fascicolo_a(),
                getForm().getFiltriFascicoli().getCd_key_fascicolo(),
                getForm().getFiltriFascicoli().getCd_fascicolo_da(),
                getForm().getFiltriFascicoli().getCd_fascicolo_a());
        if (!getMessageBox().hasError()) {
            BigDecimal idUser = new BigDecimal(getUser().getIdUtente());
            BigDecimal idAmbiente = getForm().getFiltriFascicoli().getId_ambiente().parse();
            BigDecimal idEnte = getForm().getFiltriFascicoli().getId_ente().parse();
            BigDecimal idStruttura = getForm().getFiltriFascicoli().getId_strut().parse();
            BigDecimal idTipoFascicolo = getForm().getFiltriFascicoli().getId_tipo_fascicolo().parse();
            String statoSessione = getForm().getFiltriFascicoli().getTi_stato_ses().getValue();
            String cdClasseErr = getForm().getFiltriFascicoli().getCd_classe_err().getValue();
            String cdErr = getForm().getFiltriFascicoli().getCd_err().getValue();
            MonVLisFascKoTableBean t = null;
            // Ho dovuto differenziare i metodi altrimenti il paginatore faceva casino con le count(*) su valori
            // DISTINCT
            if ((cdClasseErr != null && (!cdClasseErr.equals(""))) || (cdErr != null && (!cdErr.equals("")))) {
                t = fascicoliEjb.ricercaFascicoliKoPerMonitoraggioErr(idUser, idAmbiente, idEnte, idStruttura,
                        idTipoFascicolo, dateValidate, bean.isSingleValue() ? bean.getAnno() : bean.getAnnoDa(),
                        bean.getAnnoA(), bean.isSingleValue() ? bean.getNumero() : bean.getNumeroDa(),
                        bean.getNumeroA(), statoSessione, cdClasseErr, cdErr);
            } else {
                t = fascicoliEjb.ricercaFascicoliKoPerMonitoraggio(idUser, idAmbiente, idEnte, idStruttura,
                        idTipoFascicolo, dateValidate, bean.isSingleValue() ? bean.getAnno() : bean.getAnnoDa(),
                        bean.getAnnoA(), bean.isSingleValue() ? bean.getNumero() : bean.getNumeroDa(),
                        bean.getNumeroA(), statoSessione, cdClasseErr, cdErr);
            }
            getForm().getFascicoliKoList().setTable(t);
            getForm().getFascicoliKoList().getTable().first();
            getForm().getFascicoliKoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        }
    }

    @Override
    public void tabIndiceSipOnClick() throws EMFError {
        getForm().getDettaglioVersamentoKoTabs().setCurrentTab(getForm().getDettaglioVersamentoKoTabs().getIndiceSip());
        forwardToPublisher(Application.Publisher.DETT_VERS_FASCICOLO_FALLITO);
    }

    @Override
    public void tabRapportoVersamentoOnClick() throws EMFError {
        getForm().getDettaglioVersamentoKoTabs()
                .setCurrentTab(getForm().getDettaglioVersamentoKoTabs().getRapportoVersamento());
        forwardToPublisher(Application.Publisher.DETT_VERS_FASCICOLO_FALLITO);
    }

    @Override
    public void tabListaErroriOnClick() throws EMFError {
        getForm().getDettaglioVersamentoKoTabs()
                .setCurrentTab(getForm().getDettaglioVersamentoKoTabs().getListaErrori());
        forwardToPublisher(Application.Publisher.DETT_VERS_FASCICOLO_FALLITO);
    }

    @Override
    public void scaricaXmlVersButton() throws EMFError {
        downloadFileCommon(getForm().getDettaglioVersamentoKo().getBl_xml_sip().getValue(),
                getForm().getDettaglioVersamentoKo().getBl_xml_rapp_vers().getValue(), "versamentoFascicoloFallito",
                getForm().getDettaglioVersamentoKo().getId_ses_fascicolo_ko().parse(), "IndiceSIP",
                "EsitoNegativoVersamento");
    }

    private void downloadFileCommon(String xmlSip, String xmlRisposta, String nomeFileZip, BigDecimal idSessione,
            String nomeFileXmlSip, String nomeFileXmlRisposta) throws EMFError {
        ZipArchiveOutputStream tmpZipOutputStream = null;
        FileOutputStream tmpOutputStream = null;
        try {
            // Controllo per scrupolo
            if (xmlSip == null)
                xmlSip = "";
            if (xmlRisposta == null)
                xmlRisposta = "";
            String strIdSessione = idSessione.toPlainString();
            File zipDaScaricare = new File(System.getProperty("java.io.tmpdir"),
                    nomeFileZip + "_" + strIdSessione + ".zip");
            tmpOutputStream = new FileOutputStream(zipDaScaricare);
            tmpZipOutputStream = new ZipArchiveOutputStream(tmpOutputStream);
            tmpZipOutputStream.putArchiveEntry(new ZipArchiveEntry(nomeFileXmlSip + "_" + strIdSessione + ".xml"));
            tmpZipOutputStream.write((byte[]) xmlSip.getBytes("UTF-8"));
            tmpZipOutputStream.closeArchiveEntry();
            tmpZipOutputStream.putArchiveEntry(new ZipArchiveEntry(nomeFileXmlRisposta + "_" + strIdSessione + ".xml"));
            tmpZipOutputStream.write((byte[]) xmlRisposta.getBytes("UTF-8"));
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

    public void download() throws EMFError, IOException {
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
                getResponse()
                        .setContentType(StringUtils.isBlank(contentType) ? WebConstants.MIME_TYPE_ZIP : contentType);
                getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + filename);

                try (OutputStream outUD = getServletOutputStream();
                        FileInputStream inputStream = new FileInputStream(fileToDownload);) {

                    getResponse().setHeader("Content-Length", String.valueOf(fileToDownload.length()));
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
                    freeze();
                }
                // Nel caso sia stato richiesto, elimina il file
                if (deleteFile.booleanValue()) {
                    FileUtils.deleteQuietly(fileToDownload);
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
    public JSONObject triggerFiltriFascicoliCd_classe_errOnTrigger() throws EMFError {
        getForm().getFiltriFascicoli().post(getRequest());
        String codClasse = getForm().getFiltriFascicoli().getCd_classe_err().getValue();
        getForm().getFiltriFascicoli().getCd_err()
                .setDecodeMap(fascicoliEjb.getErrSacerByCodClasseDecodeMap(codClasse));
        return getForm().getFiltriFascicoli().asJSON();
    }

    @Override
    protected void postLoad() {
        super.postLoad();
        if (getForm() instanceof MonitoraggioFascicoliForm) {
            getForm().getRiepilogoVersamentiFascicoli().getGeneraRiepilogoVersFascicoliButton().setEditMode();
            getForm().getCalcolaTotaliButtonList().getCalcTotFascicoliVersFallitiButton().setEditMode();
            getForm().getCalcolaTotaliButtonList().getCalcTotFascicoliVersatiButton().setEditMode();
            getForm().getDettaglioVersamentoKo().getScaricaXmlVersButton().setEditMode();
            getForm().getDettaglioVersamentoKo().getScaricaXmlVersButton().setDisableHourGlass(true);
            // Imposto le combo in editMode
            getForm().getRiepilogoVersamentiFascicoli().setEditMode();
            getForm().getCalcolaTotaliButtonList().setEditMode();
            getForm().getFiltriFascicoli().setEditMode();
            /*
             * getForm().getDettaglioFascVersKo().getVerificaVersFallitiDettButton().setEditMode();
             * getForm().getDettaglioFascVersKo().getAssegnaNonRisolubileAFascKoButton().setEditMode();
             * getForm().getDettaglioFascVersKo().getAssegnaNonVerifAFascKoButton().setEditMode();
             * getForm().getDettaglioFascVersKo().getModStatoFascKoButton().setEditMode();
             * getForm().getDettaglioFascVersKo().getAssegnaVerifAFascKoButton().setEditMode();
             */
            // Form lista sessioni fascicoli errate
            getForm().getFiltriSessioniFascicoli().setEditMode();
            getForm().getFiltriSessioniFascicoli().getRicercaSessFascicoliErrateButton().setEditMode();
            /*
             * // Anche se c'è il jquery che lo cambia lato client lo metto anche lato server per spegnerlo all'inizio
             * String val=getForm().getFiltriSessioniFascicoli().getTi_stato_ses().getValue(); if (val!=null &&
             * val.equals(FascicoliEjb.statoSessioneFascicoliErrati.NON_VERIFICATA.name())) {
             * getForm().getFiltriSessioniFascicoli().getTrasfSessErrateInVersFallitiButton().setEditMode(); } else {
             * getForm().getFiltriSessioniFascicoli().getTrasfSessErrateInVersFallitiButton().setViewMode(); }
             */
            // Form dettaglio sessioni versamento errate
            if (getForm().getSesFascicoliErrList().getStatus().equals(Status.update)) {
                // getForm().getDettaglioSessFascKo().setStatus(Status.update);
                getForm().getDettaglioSessFascKo().getId_ambiente().setEditMode();
                getForm().getDettaglioSessFascKo().getId_ente().setEditMode();
                // getForm().getDettaglioSessFascKo().getId_ente().reset();
                getForm().getDettaglioSessFascKo().getId_strut().setEditMode();
                // getForm().getDettaglioSessFascKo().getId_strut().reset();
                getForm().getDettaglioSessFascKo().getId_tipo_fascicolo().setEditMode();
                // getForm().getDettaglioSessFascKo().getId_tipo_fascicolo().reset();
                getForm().getDettaglioSessFascKo().getAa_fascicolo().setEditMode();
                getForm().getDettaglioSessFascKo().getCd_key_fascicolo().setEditMode();
            } else {
                // getForm().getDettaglioSessFascKo().setStatus(Status.view);
                getForm().getDettaglioSessFascKo().getId_ambiente().setViewMode();
                getForm().getDettaglioSessFascKo().getId_ente().setViewMode();
                getForm().getDettaglioSessFascKo().getId_strut().setViewMode();
                getForm().getDettaglioSessFascKo().getId_tipo_fascicolo().setViewMode();
                getForm().getDettaglioSessFascKo().getAa_fascicolo().setViewMode();
                getForm().getDettaglioSessFascKo().getCd_key_fascicolo().setViewMode();
            }
            getForm().getDettaglioSessFascKo().getScaricaXmlVersSessKoButton().setEditMode();
            getForm().getDettaglioSessFascKo().getScaricaXmlVersSessKoButton().setDisableHourGlass(true);
        }
    }

    // trigger per la lista sessioni errate
    @Override
    public JSONObject triggerFiltriSessioniFascicoliCd_classe_errOnTrigger() throws EMFError {
        getForm().getFiltriSessioniFascicoli().post(getRequest());
        String codClasse = getForm().getFiltriSessioniFascicoli().getCd_classe_err().getValue();
        getForm().getFiltriSessioniFascicoli().getCd_err()
                .setDecodeMap(fascicoliEjb.getErrSacerByCodClasseDecodeMap(codClasse));
        return getForm().getFiltriSessioniFascicoli().asJSON();
    }

    @Override
    public void ricercaSessFascicoliErrateButton() throws EMFError {
        if (getForm().getFiltriSessioniFascicoli().postAndValidate(getRequest(), getMessageBox())) {
            ricercaSessFascicoliErrateButtonInternal();
        }
        forwardToPublisher(getLastPublisher());
    }

    private void ricercaSessFascicoliErrateButtonInternal() throws EMFError {
        MonitoraggioFascicoliValidator validator = new MonitoraggioFascicoliValidator(getMessageBox());
        Date[] dateValidate = validator.validaDate(getForm().getFiltriSessioniFascicoli().getGiorno_vers_da().parse(),
                getForm().getFiltriSessioniFascicoli().getOre_vers_da().parse(),
                getForm().getFiltriSessioniFascicoli().getMinuti_vers_da().parse(),
                getForm().getFiltriSessioniFascicoli().getGiorno_vers_a().parse(),
                getForm().getFiltriSessioniFascicoli().getOre_vers_a().parse(),
                getForm().getFiltriSessioniFascicoli().getMinuti_vers_a().parse(),
                getForm().getFiltriSessioniFascicoli().getGiorno_vers_da().getHtmlDescription(),
                getForm().getFiltriSessioniFascicoli().getGiorno_vers_a().getHtmlDescription());
        if (!getMessageBox().hasError()) {
            String statoSessione = getForm().getFiltriSessioniFascicoli().getTi_stato_ses().getValue();
            String cdClasseErr = getForm().getFiltriSessioniFascicoli().getCd_classe_err().getValue();
            String cdErr = getForm().getFiltriSessioniFascicoli().getCd_err().getValue();

            BaseTable t = fascicoliEjb.ricercaSessFascErrate(
                    (dateValidate != null && dateValidate[0] != null) ? dateValidate[0] : null,
                    (dateValidate != null && dateValidate[1] != null) ? dateValidate[1] : null, statoSessione,
                    cdClasseErr, cdErr);
            getForm().getSesFascicoliErrList().setTable(t);
            getForm().getSesFascicoliErrList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getSesFascicoliErrList().getTable().first();
        }
    }

    private void redirectToPage(final String action, BaseForm form, String listToPopulate, BaseTableInterface<?> table,
            String event) {
        ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) form.getComponent(listToPopulate)).setTable(table);
        redirectToAction(action, "?operation=listNavigationOnClick&navigationEvent=" + event + "&table="
                + listToPopulate + "&riga=" + table.getCurrentRowIndex(), form);
    }

    @Override
    public void tabIndiceSipDettSessFascKoOnClick() throws EMFError {
        getForm().getDettaglioSessFascKoTabs()
                .setCurrentTab(getForm().getDettaglioSessFascKoTabs().getIndiceSipDettSessFascKo());
        forwardToPublisher(Application.Publisher.DETT_SESS_FASCICOLO_ERRATO);
    }

    @Override
    public void tabRapportoVersamentoDettSessFascKoOnClick() throws EMFError {
        getForm().getDettaglioSessFascKoTabs()
                .setCurrentTab(getForm().getDettaglioSessFascKoTabs().getRapportoVersamentoDettSessFascKo());
        forwardToPublisher(Application.Publisher.DETT_SESS_FASCICOLO_ERRATO);
    }

    @Override
    public void scaricaXmlVersSessKoButton() throws EMFError {
        downloadFileCommon(getForm().getDettaglioSessFascKo().getBl_xml_sip().getValue(),
                getForm().getDettaglioSessFascKo().getBl_xml_rapp_vers().getValue(), "versamentoFascicoloFallito",
                getForm().getDettaglioSessFascKo().getId_ses_fascicolo_err().parse(), "IndiceSIP",
                "EsitoNegativoVersamento");
    }

    /*** TRIGGERS DELLE VARIE COMBO ***/
    // AMBIENTE
    @Override
    public JSONObject triggerFiltriFascicoliId_ambienteOnTrigger() throws EMFError {
        return gestisciTriggerAmbiente(getForm().getFiltriFascicoli());
    }

    @Override
    public JSONObject triggerDettaglioSessFascKoId_ambienteOnTrigger() throws EMFError {
        return gestisciTriggerAmbiente(getForm().getDettaglioSessFascKo());
    }

    @Override
    public JSONObject triggerRiepilogoVersamentiFascicoliId_ambienteOnTrigger() throws EMFError {
        return gestisciTriggerAmbiente(getForm().getRiepilogoVersamentiFascicoli());
    }

    private JSONObject gestisciTriggerAmbiente(Component comp) throws EMFError {
        ComboBox<BigDecimal> cbxAmbiente = null;
        ComboBox<BigDecimal> cbxEnte = null;
        ComboBox<BigDecimal> cbxStruttura = null;
        ComboBox<BigDecimal> cbxTipoFascicolo = null;
        if (comp instanceof FiltriFascicoli) {
            FiltriFascicoli d = (FiltriFascicoli) comp;
            d.post(getRequest());
            cbxAmbiente = d.getId_ambiente();
            cbxEnte = d.getId_ente();
            cbxStruttura = d.getId_strut();
            cbxTipoFascicolo = d.getId_tipo_fascicolo();
        } else if (comp instanceof DettaglioSessFascKo) {
            DettaglioSessFascKo d = (DettaglioSessFascKo) comp;
            d.post(getRequest());
            cbxAmbiente = d.getId_ambiente();
            cbxEnte = d.getId_ente();
            cbxStruttura = d.getId_strut();
            cbxTipoFascicolo = d.getId_tipo_fascicolo();
        } else if (comp instanceof RiepilogoVersamentiFascicoli) {
            RiepilogoVersamentiFascicoli d = (RiepilogoVersamentiFascicoli) comp;
            d.post(getRequest());
            cbxAmbiente = d.getId_ambiente();
            cbxEnte = d.getId_ente();
            cbxStruttura = d.getId_strut();
            cbxTipoFascicolo = d.getId_tipo_fascicolo();
        }

        BigDecimal idAmbiente = cbxAmbiente.parse();
        if (idAmbiente != null) {
            OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(),
                    idAmbiente.longValue(), Boolean.TRUE);
            cbxEnte.setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanEnte, "id_ente", "nm_ente"));
            if (tmpTableBeanEnte.size() == 1) {
                // Esiste solo un ente, la setto immediatamente e verifico le strutture
                cbxEnte.setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                OrgStrutTableBean tmpTableBeanStrut = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(),
                        tmpTableBeanEnte.getRow(0).getIdEnte(), Boolean.TRUE);
                cbxStruttura.setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanStrut, "id_strut", "nm_strut"));
                if (tmpTableBeanStrut.size() == 1) {
                    cbxStruttura.setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                    DecTipoFascicoloTableBean tmpTableBeanFascicolo = tipoFascicoloEjb.getTipiFascicoloAbilitati(
                            getUser().getIdUtente(), tmpTableBeanStrut.getRow(0).getIdStrut(), false);
                    cbxTipoFascicolo.setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanFascicolo,
                            "id_tipo_fascicolo", "nm_tipo_fascicolo"));
                } else {
                    cbxTipoFascicolo.setDecodeMap(new DecodeMap());
                }
            } else {
                cbxStruttura.setDecodeMap(new DecodeMap());
                cbxTipoFascicolo.setDecodeMap(new DecodeMap());
            }
        } else {
            cbxEnte.setDecodeMap(new DecodeMap());
            cbxStruttura.setDecodeMap(new DecodeMap());
            cbxTipoFascicolo.setDecodeMap(new DecodeMap());
        }
        return comp.asJSON();
    }

    /*** FINE TRIGGERS AMBIENTE ***/

    /*** INIZIO TRIGGERS ENTE ***/
    @Override
    public JSONObject triggerDettaglioSessFascKoId_enteOnTrigger() throws EMFError {
        return gestisciTriggerEnte(getForm().getDettaglioSessFascKo());
    }

    @Override
    public JSONObject triggerFiltriFascicoliId_enteOnTrigger() throws EMFError {
        return gestisciTriggerEnte(getForm().getFiltriFascicoli());
    }

    @Override
    public JSONObject triggerRiepilogoVersamentiFascicoliId_enteOnTrigger() throws EMFError {
        return gestisciTriggerEnte(getForm().getRiepilogoVersamentiFascicoli());
    }

    private JSONObject gestisciTriggerEnte(Component comp) throws EMFError {
        ComboBox<BigDecimal> cbxEnte = null;
        ComboBox<BigDecimal> cbxStruttura = null;
        ComboBox<BigDecimal> cbxTipoFascicolo = null;
        if (comp instanceof FiltriFascicoli) {
            FiltriFascicoli d = (FiltriFascicoli) comp;
            d.post(getRequest());
            cbxEnte = d.getId_ente();
            cbxStruttura = d.getId_strut();
            cbxTipoFascicolo = d.getId_tipo_fascicolo();
        } else if (comp instanceof DettaglioSessFascKo) {
            DettaglioSessFascKo d = (DettaglioSessFascKo) comp;
            d.post(getRequest());
            cbxEnte = d.getId_ente();
            cbxStruttura = d.getId_strut();
            cbxTipoFascicolo = d.getId_tipo_fascicolo();
        } else if (comp instanceof RiepilogoVersamentiFascicoli) {
            RiepilogoVersamentiFascicoli d = (RiepilogoVersamentiFascicoli) comp;
            d.post(getRequest());
            cbxEnte = d.getId_ente();
            cbxStruttura = d.getId_strut();
            cbxTipoFascicolo = d.getId_tipo_fascicolo();
        }
        BigDecimal idEnte = cbxEnte.parse();
        if (idEnte != null) {
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte,
                    Boolean.TRUE);
            cbxStruttura.setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanStrut, "id_strut", "nm_strut"));
            if (tmpTableBeanStrut.size() == 1) {
                cbxStruttura.setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                DecTipoFascicoloTableBean tmpTableBeanFascicolo = tipoFascicoloEjb.getTipiFascicoloAbilitati(
                        getUser().getIdUtente(), tmpTableBeanStrut.getRow(0).getIdStrut(), false);
                cbxTipoFascicolo.setDecodeMap(
                        DecodeMap.Factory.newInstance(tmpTableBeanFascicolo, "id_tipo_fascicolo", "nm_tipo_fascicolo"));
            } else {
                cbxTipoFascicolo.setDecodeMap(new DecodeMap());
            }
        } else {
            cbxStruttura.setDecodeMap(new DecodeMap());
            cbxTipoFascicolo.setDecodeMap(new DecodeMap());
        }
        return comp.asJSON();
    }

    /*** FINE TRIGGERS ENTE ***/

    /*** INIZIO TRIGGERS STRUTTURA ***/
    @Override
    public JSONObject triggerFiltriFascicoliId_strutOnTrigger() throws EMFError {
        return gestisciTriggerStruttura(getForm().getFiltriFascicoli());
    }

    @Override
    public JSONObject triggerDettaglioSessFascKoId_strutOnTrigger() throws EMFError {
        return gestisciTriggerStruttura(getForm().getDettaglioSessFascKo());
    }

    @Override
    public JSONObject triggerRiepilogoVersamentiFascicoliId_strutOnTrigger() throws EMFError {
        return gestisciTriggerStruttura(getForm().getRiepilogoVersamentiFascicoli());
    }

    private JSONObject gestisciTriggerStruttura(Component comp) throws EMFError {
        ComboBox<BigDecimal> cbxStruttura = null;
        ComboBox<BigDecimal> cbxTipoFascicolo = null;
        if (comp instanceof FiltriFascicoli) {
            FiltriFascicoli d = (FiltriFascicoli) comp;
            d.post(getRequest());
            cbxStruttura = d.getId_strut();
            cbxTipoFascicolo = d.getId_tipo_fascicolo();
        } else if (comp instanceof DettaglioSessFascKo) {
            DettaglioSessFascKo d = (DettaglioSessFascKo) comp;
            d.post(getRequest());
            cbxStruttura = d.getId_strut();
            cbxTipoFascicolo = d.getId_tipo_fascicolo();
        } else if (comp instanceof RiepilogoVersamentiFascicoli) {
            RiepilogoVersamentiFascicoli d = (RiepilogoVersamentiFascicoli) comp;
            d.post(getRequest());
            cbxStruttura = d.getId_strut();
            cbxTipoFascicolo = d.getId_tipo_fascicolo();
        }
        BigDecimal idStrut = cbxStruttura.parse();
        if (idStrut != null) {
            DecTipoFascicoloTableBean tmpTableBeanFascicolo = tipoFascicoloEjb
                    .getTipiFascicoloAbilitati(getUser().getIdUtente(), idStrut, false);
            cbxTipoFascicolo.setDecodeMap(
                    DecodeMap.Factory.newInstance(tmpTableBeanFascicolo, "id_tipo_fascicolo", "nm_tipo_fascicolo"));
        } else {
            cbxTipoFascicolo.setDecodeMap(new DecodeMap());
        }
        return comp.asJSON();
    }

    @Override
    public void updateSesFascicoliErrList() throws EMFError {
        // super.updateSesFascicoliErrList();
        getForm().getSesFascicoliErrList().setStatus(Status.update);
    }
    /*** FINE TRIGGERS STRUTTURA ***/

}
