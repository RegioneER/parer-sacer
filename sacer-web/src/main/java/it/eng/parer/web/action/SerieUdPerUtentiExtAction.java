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

import bsh.StringUtil;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneRegistro.ejb.RegistroEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb.TipoUnitaDocEjb;
import it.eng.parer.entity.SerUrnFileVerSerie;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.firma.crypto.verifica.VerFormatiEnums;
import it.eng.parer.objectstorage.dto.RecuperoDocBean;
import it.eng.parer.serie.dto.RicercaSerieBean;
import it.eng.parer.serie.dto.RicercaUdAppartBean;
import it.eng.parer.serie.ejb.SerieEjb;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.SerieUdPerUtentiExtAbstractAction;
import it.eng.parer.slite.gen.form.SerieUDForm;
import it.eng.parer.slite.gen.form.SerieUdPerUtentiExtForm;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.slite.gen.viewbean.AroVRicUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.SerVLisErrContenSerieUdTableBean;
import it.eng.parer.slite.gen.viewbean.SerVLisNotaSerieTableBean;
import it.eng.parer.slite.gen.viewbean.SerVLisStatoSerieTableBean;
import it.eng.parer.slite.gen.viewbean.SerVLisUdAppartSerieRowBean;
import it.eng.parer.slite.gen.viewbean.SerVLisUdAppartSerieTableBean;
import it.eng.parer.slite.gen.viewbean.SerVLisVerSeriePrecTableBean;
import it.eng.parer.slite.gen.viewbean.SerVLisVolSerieUdTableBean;
import it.eng.parer.slite.gen.viewbean.SerVRicSerieUdUsrRowBean;
import it.eng.parer.slite.gen.viewbean.SerVRicSerieUdUsrTableBean;
import it.eng.parer.slite.gen.viewbean.SerVVisSerieUdRowBean;
import it.eng.parer.web.ejb.ElenchiVersamentoEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.ActionUtils;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.ws.dto.CSChiaveSerie;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.ejb.RecuperoDocumento;
import it.eng.parer.ws.recupero.dto.ComponenteRec;
import it.eng.parer.ws.recupero.ejb.oracleBlb.RecBlbOracle;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.AbstractBaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.impl.ComboBox;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.MessageBox;
import it.eng.spagoLite.security.Secure;
import java.util.List;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

/**
 *
 * @author Bonora_L
 */
public class SerieUdPerUtentiExtAction extends SerieUdPerUtentiExtAbstractAction {

    private static final Logger log = LoggerFactory.getLogger(SerieUdPerUtentiExtAction.class);

    @EJB(mappedName = "java:app/Parer-ejb/SerieEjb")
    private SerieEjb serieEjb;
    @EJB(mappedName = "java:app/Parer-ejb/UnitaDocumentarieHelper")
    private UnitaDocumentarieHelper udHelper;
    @EJB(mappedName = "java:app/Parer-ejb/ElenchiVersamentoEjb")
    private ElenchiVersamentoEjb evEjb;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoUnitaDocEjb")
    private TipoUnitaDocEjb tipoUnitaDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/RegistroEjb")
    private RegistroEjb registroEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/Parer-ejb/RecuperoDocumento")
    private RecuperoDocumento recuperoDocumento;

    private static final String ECCEZIONE_RECUPERO_INDICE_AIP = "Errore non gestito nel recupero del file";

    // <editor-fold defaultstate="collapsed" desc="Ricerca serie per utenti esterni">
    @Secure(action = "Menu.Serie.RicercaSeriePerUtentiExt")
    public void loadRicercaSerie() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Serie.RicercaSeriePerUtentiExt");

        getSession().removeAttribute("navTableSerie");
        getForm().getFiltriRicercaSerie().reset();
        getForm().getFiltriRicercaSerie().setEditMode();

        initFiltriStrut(getForm().getFiltriRicercaSerie().getName());
        DecTipoUnitaDocTableBean tmpTableBeanTipoUD = tipoUnitaDocEjb.getTipiUnitaDocAbilitati(getUser().getIdUtente(),
                getUser().getIdOrganizzazioneFoglia());
        DecodeMap mappaTipoUD = DecodeMap.Factory.newInstance(tmpTableBeanTipoUD, "id_tipo_unita_doc",
                "nm_tipo_unita_doc");
        getForm().getFiltriRicercaSerie().getNm_tipo_unita_doc().setDecodeMap(mappaTipoUD);

        DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb.getRegistriUnitaDocAbilitati(getUser().getIdUtente(),
                getUser().getIdOrganizzazioneFoglia());
        DecodeMap mappaRegistro = DecodeMap.Factory.newInstance(tmpTableBeanReg, "id_registro_unita_doc",
                "cd_registro_unita_doc");
        getForm().getFiltriRicercaSerie().getCd_registro_unita_doc().setDecodeMap(mappaRegistro);

        getForm().getSerieList().setTable(null);

        forwardToPublisher(Application.Publisher.RICERCA_SERIE_PER_UTENTE_EXT);
    }

    private void initFiltriStrut(String fieldSetName) {
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
            tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(), idAmbiente.longValue(),
                    Boolean.TRUE);

            // Ricavo i valori della combo STRUTTURA
            tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.TRUE);

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
        ComboBox<BigDecimal> comboEnte = ((ComboBox<BigDecimal>) ((Fields<Field>) getForm().getComponent(fieldSetName))
                .getComponent("Id_ente"));
        comboEnte.setDecodeMap(mappaEnte);
        comboEnte.setValue(idEnte.toString());

        DecodeMap mappaStrut = new DecodeMap();
        mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
        ComboBox<BigDecimal> comboStrut = ((ComboBox<BigDecimal>) ((Fields<Field>) getForm().getComponent(fieldSetName))
                .getComponent("Id_strut"));
        comboStrut.setDecodeMap(mappaStrut);
        comboStrut.setValue(idStrut.toString());
    }

    @Override
    public JSONObject triggerFiltriRicercaSerieId_ambienteOnTrigger() throws EMFError {
        getForm().getFiltriRicercaSerie().post(getRequest());
        ActionUtils utile = new ActionUtils();
        utile.triggerAmbienteGenerico(getForm().getFiltriRicercaSerie(), getUser().getIdUtente(), Boolean.TRUE);
        return getForm().getFiltriRicercaSerie().asJSON();
    }

    @Override
    public JSONObject triggerFiltriRicercaSerieId_enteOnTrigger() throws EMFError {
        getForm().getFiltriRicercaSerie().post(getRequest());
        ActionUtils utile = new ActionUtils();
        utile.triggerEnteGenerico(getForm().getFiltriRicercaSerie(), getUser().getIdUtente(), Boolean.TRUE);
        return getForm().getFiltriRicercaSerie().asJSON();
    }

    @Override
    public JSONObject triggerFiltriRicercaSerieId_strutOnTrigger() throws EMFError {
        getForm().getFiltriRicercaSerie().post(getRequest());
        BigDecimal idStrut = getForm().getFiltriRicercaSerie().getId_strut().parse();
        if (idStrut != null) {
            DecTipoUnitaDocTableBean tmpTableBeanTipoUD = tipoUnitaDocEjb
                    .getTipiUnitaDocAbilitati(getUser().getIdUtente(), idStrut);
            DecodeMap mappaTipoUD = DecodeMap.Factory.newInstance(tmpTableBeanTipoUD, "id_tipo_unita_doc",
                    "nm_tipo_unita_doc");
            getForm().getFiltriRicercaSerie().getNm_tipo_unita_doc().setDecodeMap(mappaTipoUD);

            DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb
                    .getRegistriUnitaDocAbilitati(getUser().getIdUtente(), idStrut);
            DecodeMap mappaRegistro = DecodeMap.Factory.newInstance(tmpTableBeanReg, "id_registro_unita_doc",
                    "cd_registro_unita_doc");
            getForm().getFiltriRicercaSerie().getCd_registro_unita_doc().setDecodeMap(mappaRegistro);
        } else {
            getForm().getFiltriRicercaSerie().getCd_registro_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaSerie().getNm_tipo_unita_doc().setDecodeMap(new DecodeMap());
        }

        return getForm().getFiltriRicercaSerie().asJSON();
    }

    @Override
    public void ricercaSerie() throws EMFError {
        if (getForm().getFiltriRicercaSerie().postAndValidate(getRequest(), getMessageBox())) {
            RicercaSerieBean filtri = new RicercaSerieBean(getForm().getFiltriRicercaSerie());
            if (filtri.getAa_serie_a() == null) {
                int annoCorrente = Calendar.getInstance().get(Calendar.YEAR);
                BigDecimal anno = new BigDecimal(annoCorrente - 1);
                filtri.setAa_serie_a(anno);

                getForm().getFiltriRicercaSerie().getAa_serie_a().setValue(anno.toPlainString());
            }
            if (!getMessageBox().hasError()) {
                SerVRicSerieUdUsrTableBean table = serieEjb.getSerVRicSerieUdUsrTableBean(getUser().getIdUtente(),
                        filtri);
                getForm().getSerieList().setTable(table);
                getForm().getSerieList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getSerieList().getTable().first();
            }
        }
        forwardToPublisher(Application.Publisher.RICERCA_SERIE_PER_UTENTE_EXT);
    }
    // </editor-fold>

    @Override
    public void tabInfoPrincipaliOnClick() throws EMFError {
        getForm().getSerieDetailTabs().setCurrentTab(getForm().getSerieDetailTabs().getInfoPrincipali());
        forwardToPublisher(Application.Publisher.SERIE_UD_PER_UTENTE_EXT_DETAIL);
    }

    @Override
    public void tabInfoVersateOnClick() throws EMFError {
        getForm().getSerieDetailTabs().setCurrentTab(getForm().getSerieDetailTabs().getInfoVersate());
        forwardToPublisher(Application.Publisher.SERIE_UD_PER_UTENTE_EXT_DETAIL);
    }

    @Override
    public void tabIndiceAipOnClick() throws EMFError {
        getForm().getSerieDetailTabs().setCurrentTab(getForm().getSerieDetailTabs().getIndiceAip());
        forwardToPublisher(Application.Publisher.SERIE_UD_PER_UTENTE_EXT_DETAIL);
    }

    @Override
    public void tabListaUnitaDocumentarieOnClick() throws EMFError {
        getForm().getSerieDetailSubTabs().setCurrentTab(getForm().getSerieDetailSubTabs().getListaUnitaDocumentarie());
        forwardToPublisher(Application.Publisher.SERIE_UD_PER_UTENTE_EXT_DETAIL);
    }

    @Override
    public void tabListaNoteOnClick() throws EMFError {
        getForm().getSerieDetailSubTabs().setCurrentTab(getForm().getSerieDetailSubTabs().getListaNote());
        forwardToPublisher(Application.Publisher.SERIE_UD_PER_UTENTE_EXT_DETAIL);
    }

    @Override
    public void tabListaVolumiOnClick() throws EMFError {
        getForm().getSerieDetailSubTabs().setCurrentTab(getForm().getSerieDetailSubTabs().getListaVolumi());
        forwardToPublisher(Application.Publisher.SERIE_UD_PER_UTENTE_EXT_DETAIL);
    }

    @Override
    public void tabListaStatiOnClick() throws EMFError {
        getForm().getSerieDetailSubTabs().setCurrentTab(getForm().getSerieDetailSubTabs().getListaStati());
        forwardToPublisher(Application.Publisher.SERIE_UD_PER_UTENTE_EXT_DETAIL);
    }

    @Override
    public void tabListaErroriContenutoOnClick() throws EMFError {
        getForm().getSerieDetailSubTabs().setCurrentTab(getForm().getSerieDetailSubTabs().getListaErroriContenuto());
        forwardToPublisher(Application.Publisher.SERIE_UD_PER_UTENTE_EXT_DETAIL);
    }

    @Override
    public void tabListaVersioniPrecedentiOnClick() throws EMFError {
        getForm().getSerieDetailSubTabs().setCurrentTab(getForm().getSerieDetailSubTabs().getListaVersioniPrecedenti());
        forwardToPublisher(Application.Publisher.SERIE_UD_PER_UTENTE_EXT_DETAIL);
    }

    @Override
    public void initOnClick() throws EMFError {
    }

    @Override
    public void downloadAIP() throws EMFError {
        BigDecimal idSerie = getForm().getSerieDetail().getId_serie().parse();
        BigDecimal idVerSerie = getForm().getSerieDetail().getId_ver_serie().parse();
        String tiStatoVerSerie = getForm().getSerieDetail().getTi_stato_ver_serie().parse();
        String extension = null;
        String tiFileVerSerie = null;
        if (tiStatoVerSerie.equals(CostantiDB.StatoVersioneSerie.DA_FIRMARE.name())) {
            extension = ".xml";
            tiFileVerSerie = CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO.name();
        } else if (tiStatoVerSerie.equals(CostantiDB.StatoVersioneSerie.FIRMATA.name())) {
            tiFileVerSerie = CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO_FIRMATO.name();

            // MEV#15967 - Attivazione della firma Xades e XadesT
            try {
                String tiFirma = serieEjb.getTipoFirmaFileVerSerie(idVerSerie, tiFileVerSerie);
                if (tiFirma != null
                        && tiFirma.equals(it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma.XADES.name())) {
                    extension = ".xml";
                } else {
                    extension = ".xml.p7m";
                }
            } catch (ParerUserError ex) {
                throw new EMFError(ex.getMessage(), ex);
            }
            //

        } else {
            getMessageBox().addError(
                    "Errore inaspettato per il download dell'indice AIP : stato versione diverso da 'DA_FIRMARE' o 'FIRMATA'");
        }

        String maxResultStandard = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
        SerUrnFileVerSerie verIndice = serieEjb.getUrnFileVerSerieNormalizzatoByIdVerSerieAndTiFile(idVerSerie,
                tiFileVerSerie);
        String filename = null;

        if (verIndice != null) {
            filename = verIndice.getDsUrn();
        }

        if (StringUtils.isNotBlank(filename)) {
            ZipArchiveOutputStream out = new ZipArchiveOutputStream(getServletOutputStream());
            getResponse().setContentType("application/zip");
            getResponse().setHeader("Content-Disposition",
                    "attachment; filename=\"" + MessaggiWSFormat.bonificaUrnPerNomeFile(filename) + ".zip");

            try {
                // Nome del file IndiceAIPSerieUD-<versione serie>_<ambiente>_<ente>_<struttura>_<codice serie>”
                String cdVersioneSerie = getForm().getSerieDetail().getCd_ver_serie().parse();
                String[] ambEnteStrutSerie = serieEjb.ambienteEnteStrutturaSerie(idSerie);
                String cdSerie = getForm().getSerieDetail().getCd_composito_serie().parse();
                String nomeXml = "IndiceAIPSerieUD-" + cdVersioneSerie + "_" + ambEnteStrutSerie[0] + "_"
                        + ambEnteStrutSerie[1] + "_" + ambEnteStrutSerie[2] + "_" + cdSerie;

                nomeXml = nomeXml + extension;

                zippaIndiceAIPOs(out, idVerSerie.longValue(), nomeXml, tiFileVerSerie);
                out.flush();
                out.close();
            } catch (Exception e) {
                log.error("Eccezione", e);
            } finally {
                freeze();
            }
        }
    }

    // MEV#30400
    private void zippaIndiceAIPOs(ZipArchiveOutputStream zipOutputStream, Long idVerIndiceAip, String nomeXml,
            String tiFile) throws IOException {

        ZipArchiveEntry verIndiceAipZae = new ZipArchiveEntry(nomeXml);
        zipOutputStream.putArchiveEntry(verIndiceAipZae);

        // recupero documento blob vs obj storage
        // build dto per recupero
        RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(Constants.TiEntitaSacerObjectStorage.INDICE_AIP_SERIE,
                idVerIndiceAip, zipOutputStream, RecBlbOracle.TabellaBlob.SER_FILE_VER_SERIE, tiFile);
        // recupero
        boolean esitoRecupero = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);

        if (!esitoRecupero) {
            throw new IOException(ECCEZIONE_RECUPERO_INDICE_AIP);
        }

        zipOutputStream.closeArchiveEntry();
    }
    // end MEV#30400

    @Override
    public void downloadPacchettoArk() throws EMFError {
        BigDecimal idSerie = getForm().getSerieDetail().getId_serie().parse();
        BigDecimal idVerSerie = getForm().getSerieDetail().getId_ver_serie().parse();
        String cdVersioneSerie = getForm().getSerieDetail().getCd_ver_serie().parse();
        String cdSerie = getForm().getSerieDetail().getCd_composito_serie().parse();
        String[] ambEnteStrutSerie = serieEjb.ambienteEnteStrutturaSerie(idSerie);
        String filename = ambEnteStrutSerie[0] + "_" + ambEnteStrutSerie[1] + "_" + ambEnteStrutSerie[2] + "_" + cdSerie
                + ".zip";
        String aipFirmato = "IndiceAIPSerieUD-" + cdVersioneSerie + "_" + ambEnteStrutSerie[0] + "_"
                + ambEnteStrutSerie[1] + "_" + ambEnteStrutSerie[2] + "_" + cdSerie;
        String volIx = "IndiceVolumeSerie_" + ambEnteStrutSerie[0] + "_" + ambEnteStrutSerie[1] + "_"
                + ambEnteStrutSerie[2] + "_" + cdSerie + "-";

        if (!getMessageBox().hasError()) {
            File tmpFile = new File(System.getProperty("java.io.tmpdir"), filename);
            FileOutputStream fileOs = null;
            ZipOutputStream out = null;
            try {
                fileOs = new FileOutputStream(tmpFile);
                out = new ZipOutputStream(fileOs);

                serieEjb.createZipPacchettoArk(idVerSerie, out, aipFirmato, volIx);

                getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(), getControllerName());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(), tmpFile.getName());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(), tmpFile.getPath());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
                        Boolean.toString(true));
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(), "application/zip");
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            } catch (IOException ex) {
                log.error("Errore in download " + ExceptionUtils.getRootCauseMessage(ex), ex);
                getMessageBox().addError("Errore inatteso nella preparazione del download");
            } finally {
                IOUtils.closeQuietly(out);
                IOUtils.closeQuietly(fileOs);
            }
        }

        if (getMessageBox().hasError()) {
            forwardToPublisher(getLastPublisher());
        } else {
            forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
        }
    }

    @Override
    public void downloadContenuto() throws Throwable {
        BigDecimal anno = getForm().getSerieDetail().getAa_serie().parse();
        String cdCompSerie = getForm().getSerieDetail().getCd_composito_serie().parse();
        String cdVerSerie = getForm().getSerieDetail().getCd_ver_serie().parse();
        String tipoContenuto = CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name();
        String filename = "Contenuto_" + tipoContenuto + "_" + cdCompSerie + "_" + anno.toPlainString() + "_"
                + cdVerSerie + ".csv";
        File tmpFile = new File(System.getProperty("java.io.tmpdir"), filename);

        try {
            SerVLisUdAppartSerieTableBean fullUDListTable = serieEjb.getSerVLisUdAppartSerieTableBeanForDownload(
                    getForm().getSerieDetail().getId_contenuto_eff().parse(), new RicercaUdAppartBean());
            // uso il Fields solo per sapere i nomi di colonna, ma poi estraggo la table completa, bypassando il lazy
            // loading usato per l'interfaccia utente
            ActionUtils.buildCsvString(getForm().getUdList(), fullUDListTable,
                    SerVLisUdAppartSerieRowBean.TABLE_DESCRIPTOR, tmpFile);
            getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(), getControllerName());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(), tmpFile.getName());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(), tmpFile.getPath());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(), Boolean.toString(true));
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                    VerFormatiEnums.CSV_MIME);
        } catch (IOException ex) {
            log.error("Errore in download " + ExceptionUtils.getRootCauseMessage(ex), ex);
            getMessageBox().addError("Errore inatteso nella preparazione del download");
        }

        if (getMessageBox().hasError()) {
            forwardToPublisher(getLastPublisher());
        } else {
            forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
        }
    }

    @Override
    public void visualizzaConsistenzaAttesa() throws EMFError {
        BaseRow row = new BaseRow();
        getForm().getSerieDetail().copyToBean(row);

        SerieUDForm form = new SerieUDForm();
        form.getSerieDetail().copyFromBean(row);
        redirectToAction(Application.Actions.SERIE_UD, "?operation=visualizzaConsistenzaAttesa",
                Application.Publisher.SERIE_UD_DETAIL, form);
    }

    @Override
    public void loadDettaglio() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                || getNavigationEvent().equals(ListAction.NE_NEXT) || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getSerieList().getName())) {
                SerVRicSerieUdUsrRowBean currentRow = (SerVRicSerieUdUsrRowBean) getForm().getSerieList().getTable()
                        .getCurrentRow();
                BigDecimal idVerSerie = currentRow.getIdVerSerie();
                loadDettaglioSerie(idVerSerie);
            }
        }
    }

    private void loadDettaglioSerie(BigDecimal idVerSerie) throws EMFError {
        initSerieDetail();
        SerVVisSerieUdRowBean detailRow = serieEjb.getSerVVisSerieUdRowBean(idVerSerie);
        getForm().getSerieDetail().copyFromBean(detailRow);

        CSVersatore tmpVers = new CSVersatore();
        tmpVers.setAmbiente(detailRow.getNmAmbiente());
        tmpVers.setEnte(detailRow.getNmEnte());
        tmpVers.setStruttura(detailRow.getNmStrut());

        CSChiaveSerie chiaveSerie = new CSChiaveSerie();
        chiaveSerie.setAnno(detailRow.getAaSerie().intValue());
        chiaveSerie.setNumero(detailRow.getCdCompositoSerie());

        getForm().getSerieDetail().getUrn_serie()
                .setValue(MessaggiWSFormat.formattaUrnDocUniDoc(
                        MessaggiWSFormat.formattaBaseUrnSerie(MessaggiWSFormat.formattaUrnPartVersatore(tmpVers),
                                MessaggiWSFormat.formattaUrnPartSerie(chiaveSerie))));

        log.info("Carico le liste contenute nei vari tab di dettaglio");
        getForm().getSerieDetailTabs().setCurrentTab(getForm().getSerieDetailTabs().getInfoPrincipali());
        getForm().getSerieDetailSubTabs().setCurrentTab(getForm().getSerieDetailSubTabs().getListaUnitaDocumentarie());
        getForm().getSerieDetailButtonList().setEditMode();
        getForm().getSerieDetailButtonList().hideAll();
        getForm().getSerieDetail().getNi_mb_size_contenuto_eff().setHidden(true);
        getForm().getSerieDetailButtonList().getCalcolaDimensioneSerie().setHidden(false);

        SerVLisNotaSerieTableBean noteTb = serieEjb.getSerVLisNotaSerieTableBean(idVerSerie);
        getForm().getNoteList().setTable(noteTb);
        getForm().getNoteList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getNoteList().getTable().first();

        SerVLisStatoSerieTableBean statiTb = serieEjb.getSerVLisStatoSerieTableBean(idVerSerie);
        getForm().getStatiList().setTable(statiTb);
        getForm().getStatiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getStatiList().getTable().first();

        SerVLisVolSerieUdTableBean volTb = serieEjb.getSerVLisVolSerieUdTableBean(idVerSerie);
        getForm().getVolumiList().setTable(volTb);
        getForm().getVolumiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getVolumiList().getTable().first();

        SerVLisVerSeriePrecTableBean verPrecTb = serieEjb.getSerVLisVerSeriePrecTableBean(idVerSerie);
        getForm().getVersioniPrecedentiList().setTable(verPrecTb);
        getForm().getVersioniPrecedentiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getVersioniPrecedentiList().getTable().first();

        if (detailRow.getIdConsistVerSerie() != null && (detailRow.getNiUnitaDocAttese() != null
                || detailRow.getCdFirstUnitaDocAttesa() != null || detailRow.getCdLastUnitaDocAttesa() != null
                || detailRow.getTiModConsistFirstLast() != null || detailRow.getCdDocConsistVerSerie() != null
                || detailRow.getDsDocConsistVerSerie() != null)) {
            getForm().getSerieDetailButtonList().getVisualizzaConsistenzaAttesa().setHidden(false);
        }
        if (StringUtils.isNotBlank(detailRow.getBlFileIxAip())) {
            getForm().getSerieDetailButtonList().getDownloadAIP().setHidden(false);
            getForm().getSerieDetailButtonList().getDownloadAIP().setDisableHourGlass(true);
        }

        log.info("Carico le liste contenute nei vari tab di dettaglio");
        BigDecimal idContenutoEff = detailRow.getIdContenutoEff();
        SerVLisUdAppartSerieTableBean udTb = serieEjb.getSerVLisUdAppartSerieTableBean(idContenutoEff,
                new RicercaUdAppartBean());
        if (!udTb.isEmpty()) {
            getForm().getSerieDetailButtonList().getDownloadContenuto().setHidden(false);
            getForm().getSerieDetailButtonList().getDownloadContenuto().setDisableHourGlass(true);
        }
        if (detailRow.getTiStatoVerSerie().equals(CostantiDB.StatoVersioneSerie.FIRMATA.name())
                || detailRow.getTiStatoVerSerie().equals(CostantiDB.StatoVersioneSerie.IN_CUSTODIA.name())
                || detailRow.getTiStatoVerSerie().equals(CostantiDB.StatoVersioneSerie.ANNULLATA.name())) {
            getForm().getSerieDetailButtonList().getDownloadPacchettoArk().setHidden(false);
            getForm().getSerieDetailButtonList().getDownloadPacchettoArk().setDisableHourGlass(true);
        }
        if (getForm().getUdList().getTable() != null) {
            // Ricarico le liste con paginazione
            int paginaCorrenteUd = getForm().getUdList().getTable().getCurrentPageIndex();
            int inizioUd = getForm().getUdList().getTable().getFirstRowPageIndex();
            int pageSizeUd = getForm().getUdList().getTable().getPageSize();

            getForm().addComponent(new SerieUdPerUtentiExtForm.UdList());
            getForm().getUdList().setTable(udTb);
            getForm().getUdList().getTable().first();
            getForm().getUdList().getTable().setPageSize(pageSizeUd);
            this.lazyLoadGoPage(getForm().getUdList(), paginaCorrenteUd);
            // Ritorno alla pagina
            getForm().getUdList().getTable().setCurrentRowIndex(inizioUd);
        } else {
            getForm().addComponent(new SerieUdPerUtentiExtForm.UdList());

            getForm().getUdList().setTable(udTb);
            getForm().getUdList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getUdList().getTable().first();
        }
        SerVLisErrContenSerieUdTableBean errContenTb = serieEjb.getSerVLisErrContenSerieUdTableBean(idContenutoEff);
        if (getForm().getErroriContenutiList().getTable() != null) {
            int pageSizeErrCon = getForm().getErroriContenutiList().getTable().getPageSize();
            getForm().getErroriContenutiList().setTable(errContenTb);
            getForm().getErroriContenutiList().getTable().setPageSize(pageSizeErrCon);
            getForm().getErroriContenutiList().getTable().first();
        } else {
            getForm().getErroriContenutiList().setTable(errContenTb);
            getForm().getErroriContenutiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getErroriContenutiList().getTable().first();
        }
    }

    private void initSerieDetail() {
        getForm().getSerieDetail().getFl_err_contenuto_file().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getSerieDetail().getFl_err_contenuto_acq().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getSerieDetail().getFl_fornito_ente().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
    }

    @Override
    public void undoDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    @Override
    public void insertDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    @Override
    public void saveDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW) || getNavigationEvent().equals(ListAction.NE_NEXT)
                || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getSerieList().getName())) {
                forwardToPublisher(Application.Publisher.SERIE_UD_PER_UTENTE_EXT_DETAIL);
            } else if (getTableName().equals(getForm().getUdList().getName())) {
                SerVLisUdAppartSerieRowBean currentRowBean = (SerVLisUdAppartSerieRowBean) getForm().getUdList()
                        .getTable().getCurrentRow();
                BigDecimal idUnitaDoc = currentRowBean.getIdUnitaDoc();
                if (idUnitaDoc != null) {
                    AroVRicUnitaDocRowBean aroVRicUnitaDocRowBean = udHelper.getAroVRicUnitaDocRowBean(idUnitaDoc, null,
                            null);
                    if (!aroVRicUnitaDocRowBean.getTiStatoConservazione()
                            .equals(CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name())
                            && !"1".equals(aroVRicUnitaDocRowBean.getFlUnitaDocAnnul())) {
                        SerVLisUdAppartSerieTableBean tb = (SerVLisUdAppartSerieTableBean) getForm().getUdList()
                                .getTable();
                        loadUdDetail(tb);
                    } else {
                        getMessageBox().addError(
                                "Operazione non possibile in quanto l'unità documentaria ha stato di conservazione = ANNULLATA");
                        forwardToPublisher(getLastPublisher());
                    }
                }
            }
        }
    }

    private void loadUdDetail(AbstractBaseTable tb) {
        UnitaDocumentarieForm form = new UnitaDocumentarieForm();
        form.getUnitaDocumentarieList().setTable(tb);
        redirectToAction(Application.Actions.UNITA_DOCUMENTARIE,
                "?operation=listNavigationOnClick&navigationEvent=" + ListAction.NE_DETTAGLIO_VIEW + "&table="
                        + UnitaDocumentarieForm.UnitaDocumentarieList.NAME + "&riga=" + tb.getCurrentRowIndex(),
                form);
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.RICERCA_SERIE_PER_UTENTE_EXT;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        try {
            if (publisherName.equals(Application.Publisher.SERIE_UD_PER_UTENTE_EXT_DETAIL)) {
                BigDecimal idVerSerie = getForm().getSerieDetail().getId_ver_serie().parse();
                String primaryTab = getForm().getSerieDetailTabs().getCurrentTab().getName();
                String subTab = getForm().getSerieDetailSubTabs().getCurrentTab().getName();
                loadDettaglioSerie(idVerSerie);
                getForm().getSerieDetailTabs().setCurrentTab(getForm().getSerieDetailTabs().getComponent(primaryTab));
                getForm().getSerieDetailSubTabs().setCurrentTab(getForm().getSerieDetailSubTabs().getComponent(subTab));
            } else if (publisherName.equals(Application.Publisher.RICERCA_SERIE_PER_UTENTE_EXT)) {
                RicercaSerieBean filtri = new RicercaSerieBean(getForm().getFiltriRicercaSerie());
                if (filtri.getAa_serie_a() == null) {
                    int annoCorrente = Calendar.getInstance().get(Calendar.YEAR);
                    BigDecimal anno = new BigDecimal(annoCorrente - 1);
                    filtri.setAa_serie_a(anno);

                    getForm().getFiltriRicercaSerie().getAa_serie_a().setValue(anno.toPlainString());
                }
                if (!getMessageBox().hasError()) {
                    SerVRicSerieUdUsrTableBean table = serieEjb.getSerVRicSerieUdUsrTableBean(getUser().getIdUtente(),
                            filtri);
                    getForm().getSerieList().setTable(table);
                    getForm().getSerieList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    getForm().getSerieList().getTable().first();
                }
            }
        } catch (EMFError e) {
            log.error("Errore nel ricaricamento della pagina " + publisherName, e);
            getMessageBox().addError("Errore nel ricaricamento della pagina " + publisherName);
            getMessageBox().setViewMode(MessageBox.ViewMode.plain);
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.SERIE_UD_PER_UTENTI_EXT;
    }

    public void download() throws EMFError, IOException {
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
                getResponse().setContentType(StringUtils.isBlank(contentType) ? "application/zip" : contentType);
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
                    log.error("Eccezione nel recupero del documento ", e);
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
    public void calcolaDimensioneSerie() throws EMFError {
        BigDecimal idContenutoEff = getForm().getSerieDetail().getId_contenuto_eff().parse();
        BigDecimal niMb = serieEjb.getDimensioneSerie(idContenutoEff);
        getForm().getSerieDetail().getNi_mb_size_contenuto_eff().setValue(niMb.toPlainString());
        getForm().getSerieDetail().getNi_mb_size_contenuto_eff().setHidden(false);
        forwardToPublisher(getLastPublisher());
    }

    public void scaricaIndiceVolume() throws EMFError {
        // BigDecimal idVolumeVerSerie = getForm().getVolumeDetail().getId_vol_ver_serie().parse();
        String riga = getRequest().getParameter("riga");
        BigDecimal numberRiga = BigDecimal.ZERO;
        if (StringUtils.isNotBlank(riga)) {
            numberRiga = new BigDecimal(riga);
        }
        // Recupero l'idVolumeVerSerie
        BigDecimal idVolumeVerSerie = ((SerVLisVolSerieUdTableBean) getForm().getVolumiList().getTable())
                .getRow(numberRiga.intValue()).getIdVolVerSerie();

        try {
            // Ricavo il file da scaricare
            String[] nameAndFileVolume = serieEjb.getNameAndFileIndiceVolume(idVolumeVerSerie.longValue());
            String nomeFileVolume = nameAndFileVolume[0];
            String fileVolume = nameAndFileVolume[1];

            // Nome del file IndiceAIPSerieUD-<versione serie>_<ambiente>_<ente>_<struttura>_<codice serie>”
            getResponse().setContentType("application/xml");
            getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + nomeFileVolume + ".xml");
            // Ricavo lo stream di output
            OutputStream out = getServletOutputStream();
            try {
                // Caccio dentro nello zippone il blobbo
                if (fileVolume != null) {
                    // Ricavo lo stream di input
                    InputStream is = new ByteArrayInputStream(fileVolume.getBytes());
                    byte[] data = new byte[1024];
                    int count;

                    while ((count = is.read(data, 0, 1024)) != -1) {
                        out.write(data, 0, count);
                    }
                    IOUtils.closeQuietly(is);
                }
                out.flush();
            } catch (IOException e) {
                getMessageBox().addMessage(new Message(Message.MessageLevel.ERR,
                        "Errore nel recupero del file XML relativo all'indice del volume "));
                log.error("Eccezione", e);
            } finally {
                IOUtils.closeQuietly(out);
                out = null;
                if (!getMessageBox().hasError()) {
                    freeze();
                }
            }
        } catch (ParerUserError ex) {
            log.error("Errore nel recupero del file XML relativo all'indice del volume:" + ex.getMessage());
            getMessageBox().addError("Errore nel recupero del file XML relativo all'indice del volume");
        }

        if (getMessageBox().hasError()) {
            forwardToPublisher(Application.Publisher.VOLUME_SERIE_UD_DETAIL);
        }
    }

}
