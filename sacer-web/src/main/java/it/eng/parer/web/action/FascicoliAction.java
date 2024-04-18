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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneRegistro.ejb.RegistroEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoFascicolo.ejb.TipoFascicoloEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb.TipoUnitaDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTitolario.ejb.StrutTitolariEjb;
import it.eng.parer.annulVers.ejb.AnnulVersEjb;
import it.eng.parer.entity.FasFileMetaVerAipFasc;
import it.eng.parer.entity.constraint.FasFascicolo.TiConservazione;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoConservazioneNonAnnullato;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers;
import it.eng.parer.entity.constraint.FasMetaVerAipFascicolo;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.fascicoli.dto.RicercaFascicoliBean;
import it.eng.parer.fascicoli.ejb.FascicoliEjb;
import it.eng.parer.fascicoli.helper.FascicoliHelper;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.FascicoliAbstractAction;
import it.eng.parer.slite.gen.form.ElenchiVersFascicoliForm;
import it.eng.parer.slite.gen.form.FascicoliForm.FiltriFascicoliAnnullati;
import it.eng.parer.slite.gen.form.FascicoliForm.FiltriFascicoliRicercaSemplice;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm;
import it.eng.parer.slite.gen.tablebean.DecModelloXsdFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.DecVersioneWsTableBean;
import it.eng.parer.slite.gen.tablebean.FasEventoFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.FasLinkFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.FasSogFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.FasValoreAttribFascicoloTableBean;
import it.eng.parer.slite.gen.viewbean.AroVRicUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.DecVTreeTitolTableBean;
import it.eng.parer.slite.gen.viewbean.FasVLisUdInFascRowBean;
import it.eng.parer.slite.gen.viewbean.FasVLisUdInFascTableBean;
import it.eng.parer.slite.gen.viewbean.FasVRicFascicoliRowBean;
import it.eng.parer.slite.gen.viewbean.FasVRicFascicoliTableBean;
import it.eng.parer.slite.gen.viewbean.FasVVisFascicoloRowBean;
import it.eng.parer.viewEntity.constants.FasVRicFascicoli.TiEsito;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.RecuperoFascWeb;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.web.util.XmlPrettyPrintFormatter;
import it.eng.parer.web.validator.FascicoliValidator;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.recupero.dto.ComponenteRec;
import it.eng.parer.ws.recuperoFasc.dto.RispostaWSRecuperoFasc;
import it.eng.parer.ws.recuperoFasc.ejb.ControlliRecuperoFasc;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.xml.versReqStatoFasc.ChiaveFascType;
import it.eng.parer.ws.xml.versReqStatoFasc.RecuperoFascicolo;
import it.eng.parer.ws.xml.versReqStatoFasc.VersatoreFascType;

import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseForm;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.message.MessageBox;
import it.eng.spagoLite.security.Secure;
import org.codehaus.jettison.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipExtraField;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * @author Moretti_Lu
 */
public class FascicoliAction extends FascicoliAbstractAction {

    private static Logger logger = LoggerFactory.getLogger(FascicoliAction.class.getName());

    @EJB(mappedName = "java:app/Parer-ejb/RegistroEjb")
    private RegistroEjb registroEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoUnitaDocEjb")
    private TipoUnitaDocEjb tipoUnitaDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoFascicoloEjb")
    private TipoFascicoloEjb tipoFascicoliEjb;
    @EJB(mappedName = "java:app/Parer-ejb/FascicoliEjb")
    private FascicoliEjb fascicoliEjb;
    @EJB(mappedName = "java:app/Parer-ejb/StrutTitolariEjb")
    private StrutTitolariEjb titolariEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configHelper;
    @EJB(mappedName = "java:app/Parer-ejb/AnnulVersEjb")
    private AnnulVersEjb annulVersEjb;
    @EJB(mappedName = "java:app/Parer-ejb/UnitaDocumentarieHelper")
    private UnitaDocumentarieHelper udHelper;
    @EJB(mappedName = "java:app/Parer-ejb/FascicoliHelper")
    private FascicoliHelper fascicoliHelper;
    @EJB(mappedName = "java:app/Parer-ejb/ControlliRecuperoFasc")
    private ControlliRecuperoFasc controlliRecuperoFasc;

    private static final String MEX_UD_UNAUTHORIZED = "Attenzione: la visualizzazione del contenuto del fascicolo è parziale: sono state escluse le unità documentarie sulle quali l’utente non dispone di tutte le autorizzazioni";

    @Override
    public void initOnClick() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    @Override
    public void insertDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    @Override
    public void loadDettaglio() throws EMFError {
        String tableFrom = getTableName();

        if (StringUtils.isNotBlank(tableFrom)) {
            BigDecimal idFascicolo;

            if (tableFrom.equals(getForm().getFascicoliList().getName())) {
                idFascicolo = getForm().getFascicoliList().getTable().getCurrentRow().getBigDecimal("id_fascicolo");
                dettaglioFascicolo(idFascicolo);
            } else if (tableFrom.equals(getForm().getCollegamentiList().getName())
                    && getNavigationEvent().equals(NE_DETTAGLIO_VIEW)) {
                idFascicolo = ((FasLinkFascicoloTableBean) getForm().getCollegamentiList().getTable()).getCurrentRow()
                        .getIdFascicoloLink();

                if (idFascicolo == null) {
                    getMessageBox().addError(
                            "Dettaglio non visualizzabile. Il fascicolo collegato non è presente nell’archivio di Sacer");
                } else {
                    dettaglioFascicolo(idFascicolo);
                }
            }
        }
    }

    @Override
    public void tabInfoPrincipaliFascicoloOnClick() throws EMFError {
        getForm().getFascicoliDettaglioTabs()
                .setCurrentTab(getForm().getFascicoliDettaglioTabs().getInfoPrincipaliFascicolo());
        forwardToPublisher(Application.Publisher.FASCICOLI_DETAIL);
    }

    @Override
    public void tabInfoVersamentoFascicoloOnClick() throws EMFError {
        getForm().getFascicoliDettaglioBottomTabs()
                .setCurrentTab(getForm().getFascicoliDettaglioBottomTabs().getInfoVersamentoFascicolo());
        forwardToPublisher(Application.Publisher.FASCICOLI_DETAIL);
    }

    @Override
    public void tabXMLRichiestaFascicoloOnClick() throws EMFError {
        getForm().getFascicoliDettaglioBottomTabs()
                .setCurrentTab(getForm().getFascicoliDettaglioBottomTabs().getXMLRichiestaFascicolo());
        forwardToPublisher(Application.Publisher.FASCICOLI_DETAIL);
    }

    @Override
    public void tabXMLRapportoFascicoloOnClick() throws EMFError {
        getForm().getFascicoliDettaglioBottomTabs()
                .setCurrentTab(getForm().getFascicoliDettaglioBottomTabs().getXMLRapportoFascicolo());
        forwardToPublisher(Application.Publisher.FASCICOLI_DETAIL);
    }

    @Override
    public void tabXMLMetaIndiceAipFascicoloOnClick() throws EMFError {
        getForm().getFascicoliDettaglioBottomTabs()
                .setCurrentTab(getForm().getFascicoliDettaglioBottomTabs().getXMLMetaIndiceAipFascicolo());
        forwardToPublisher(Application.Publisher.FASCICOLI_DETAIL);
    }

    @Override
    public void tabUnitaDocumentarieOnClick() throws EMFError {
        getForm().getFascicoliDettaglioListsTabs()
                .setCurrentTab(getForm().getFascicoliDettaglioListsTabs().getUnitaDocumentarie());
        forwardToPublisher(Application.Publisher.FASCICOLI_DETAIL);
    }

    @Override
    public void tabAmministrazioniPartecipantiOnClick() throws EMFError {
        getForm().getFascicoliDettaglioListsTabs()
                .setCurrentTab(getForm().getFascicoliDettaglioListsTabs().getAmministrazioniPartecipanti());
        forwardToPublisher(Application.Publisher.FASCICOLI_DETAIL);
    }

    @Override
    public void tabSoggettiCoinvoltiOnClick() throws EMFError {
        getForm().getFascicoliDettaglioListsTabs()
                .setCurrentTab(getForm().getFascicoliDettaglioListsTabs().getSoggettiCoinvolti());
        forwardToPublisher(Application.Publisher.FASCICOLI_DETAIL);
    }

    @Override
    public void tabResponsabiliOnClick() throws EMFError {
        getForm().getFascicoliDettaglioListsTabs()
                .setCurrentTab(getForm().getFascicoliDettaglioListsTabs().getResponsabili());
        forwardToPublisher(Application.Publisher.FASCICOLI_DETAIL);
    }

    @Override
    public void tabUOResponsabiliOnClick() throws EMFError {
        getForm().getFascicoliDettaglioListsTabs()
                .setCurrentTab(getForm().getFascicoliDettaglioListsTabs().getUOResponsabili());
        forwardToPublisher(Application.Publisher.FASCICOLI_DETAIL);
    }

    @Override
    public void tabCollegamentiOnClick() throws EMFError {
        getForm().getFascicoliDettaglioListsTabs()
                .setCurrentTab(getForm().getFascicoliDettaglioListsTabs().getCollegamenti());
        forwardToPublisher(Application.Publisher.FASCICOLI_DETAIL);
    }

    @Override
    public void tabElvFascicoliOnClick() throws EMFError {
        getForm().getFascicoliDettaglioListsTabs()
                .setCurrentTab(getForm().getFascicoliDettaglioListsTabs().getElvFascicoli());
        forwardToPublisher(Application.Publisher.FASCICOLI_DETAIL);
    }

    @Override
    public void undoDettaglio() throws EMFError {
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
        String tableFrom = getTableName();

        if (tableFrom.equals(getForm().getFascicoliList().getName())) {
            forwardToPublisher(Application.Publisher.FASCICOLI_DETAIL);
        } else if (tableFrom.equals(getForm().getCollegamentiList().getName())) {
            if (!getMessageBox().hasError()) {
                forwardToPublisher(Application.Publisher.FASCICOLI_DETAIL);
            }
        } else if (tableFrom.equals(getForm().getUnitaDocList().getName())) {
            FasVLisUdInFascRowBean riga = (FasVLisUdInFascRowBean) getForm().getUnitaDocList().getTable()
                    .getCurrentRow();
            BigDecimal idUnitaDoc = riga.getIdUnitaDoc();
            if (idUnitaDoc != null) {
                AroVRicUnitaDocRowBean aroVRicUnitaDocRowBean = udHelper.getAroVRicUnitaDocRowBean(idUnitaDoc, null,
                        null);
                if (!aroVRicUnitaDocRowBean.getTiStatoConservazione()
                        .equals(CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name())
                        && !"1".equals(aroVRicUnitaDocRowBean.getFlUnitaDocAnnul())) {
                    redirectToUnitaDocumentariePage();
                } else {
                    getMessageBox().addError(
                            "Operazione non possibile in quanto l'unità documentaria ha stato di conservazione = ANNULLATA");
                    forwardToPublisher(getLastPublisher());
                }
            }
        } else if (tableFrom.equals(getForm().getElvFascicoliList().getName())) {
            redirectToElvFascicoliPage();
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.FASCICOLI_RICERCA_SEMPLICE;
    }

    @Override
    public void process() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    @Override
    public void reloadAfterGoBack(String string) {
    }

    @Override
    public String getControllerName() {
        return Application.Actions.FASCICOLI;
    }

    /**
     * Metodo di inizializzazione form di ricerca semplice fascicoli
     *
     * @throws EMFError
     *             errore generico
     */
    @Secure(action = "Menu.Fascicoli.FascicoliRicercaSemplice")
    public void fascicoliRicercaSemplice() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Fascicoli.FascicoliRicercaSemplice");

        // Pulisco i filtri di ricerca
        getForm().getFiltriFascicoliRicercaSemplice().reset();
        // Rendo editabili i filtri di ricerca
        getForm().getFiltriFascicoliRicercaSemplice().setEditMode();
        //
        getForm().getFascicoliPerRichAnnulVers().getAddToRichAnnul().setEditMode();

        // Pulisco la lista di ricerca
        getForm().getFascicoliList().clear();
        // Setto le combo dei filtri di ricerca
        initRicercaSempliceFascicoli();

        // Carico la pagina di ricerca
        forwardToPublisher(Application.Publisher.FASCICOLI_RICERCA_SEMPLICE);
    }

    /**
     * Inizializza i combo della form di ricerca
     * 
     * @throws EMFError
     *             errore generico
     */
    private void initRicercaSempliceFascicoli() throws EMFError {
        // Azzero i filtri
        getForm().getFiltriFascicoliRicercaSemplice().reset();

        DecTipoFascicoloTableBean tipiFascicolo = tipoFascicoliEjb.getTipiFascicoloAbilitati(getUser().getIdUtente(),
                getUser().getIdOrganizzazioneFoglia(), false);
        DecodeMap mapTipiFascicolo = new DecodeMap();
        mapTipiFascicolo.populatedMap(tipiFascicolo, "id_tipo_fascicolo", "nm_tipo_fascicolo");
        getForm().getFiltriFascicoliRicercaSemplice().getId_tipo_fascicolo().setDecodeMap(mapTipiFascicolo);

        DecModelloXsdFascicoloTableBean tipiXsd = fascicoliEjb.getDecModelloXsdFascicoloTableBeanInit();
        DecodeMap mapTipiXsd = new DecodeMap();
        mapTipiXsd.populatedMap(tipiXsd, "id_modello_xsd_fascicolo", "ti_modello_xsd");
        getForm().getFiltriFascicoliRicercaSemplice().getId_modello_xsd_fascicolo().setDecodeMap(mapTipiXsd);

        DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb.getRegistriUnitaDocAbilitati(getUser().getIdUtente(),
                getUser().getIdOrganizzazioneFoglia());
        DecodeMap mapRegistro = new DecodeMap();
        mapRegistro.populatedMap(tmpTableBeanReg, "id_registro_unita_doc", "cd_registro_unita_doc");
        getForm().getFiltriFascicoliRicercaSemplice().getCd_registro_key_unita_doc().setDecodeMap(mapRegistro);

        getForm().getFiltriFascicoliRicercaSemplice().getTi_conservazione()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("id_tipo_conservazione", TiConservazione.values()));

        getForm().getFiltriFascicoliRicercaSemplice().getFl_forza_contr_classif()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriFascicoliRicercaSemplice().getFl_forza_contr_numero()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriFascicoliRicercaSemplice().getFl_forza_contr_colleg()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

        DecVersioneWsTableBean versioneWsTableBean = tipoUnitaDocEjb.getDecVersioneWsTableBean("Versamento fascicoli");
        getForm().getFiltriFascicoliRicercaSemplice().getCd_versione_ws()
                .setDecodeMap(DecodeMap.Factory.newInstance(versioneWsTableBean, "cd_versione_ws", "cd_versione_ws"));

        getForm().getFiltriFascicoliRicercaSemplice().getTi_esito()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("id_ti_esito", TiEsito.values()));

        getForm().getFiltriFascicoliRicercaSemplice().getTi_stato_conservazione().setDecodeMap(ComboGetter
                .getMappaSortedGenericEnum("id_tipo_stato_conservazione", TiStatoConservazioneNonAnnullato.values()));

        getForm().getFiltriFascicoliRicercaSemplice().getTi_stato_fasc_elenco_vers().setDecodeMap(ComboGetter
                .getMappaSortedGenericEnum("id_tipo_stato_fasc_elenco_vers", TiStatoFascElencoVers.values()));

        if (getForm().getFascicoliPerRichAnnulVers().getId_rich_annul_vers().parse() != null) {
            getForm().getFascicoliPerRichAnnulVersList().setTable(new BaseTable());
            getForm().getFascicoliPerRichAnnulVersList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getFascicoliPerRichAnnulVersList().getTable().first();

            getForm().getFascicoliToRichAnnulVersSection().setHidden(false);
        } else {
            getForm().getFascicoliToRichAnnulVersSection().setHidden(true);
        }
    }

    private void dettaglioFascicolo(BigDecimal idFascicolo) throws EMFError {
        initDettaglioFascicolo();
        // getForm().getFascicoloDetail().getId_modello_xsd_fascicolo().setDecodeMap(new DecodeMap());

        // Recupero le informazioni sul dettaglio del fascicolo
        FasVVisFascicoloRowBean dettaglio = fascicoliEjb.retrieveFasVVisFascicolo(idFascicolo);
        getForm().getFascicoloDetail().copyFromBean(dettaglio);

        CSVersatore tmpVers = new CSVersatore();
        tmpVers.setAmbiente(dettaglio.getNmAmbiente());
        tmpVers.setEnte(dettaglio.getNmEnte());
        tmpVers.setStruttura(dettaglio.getNmStrut());

        CSChiaveFasc chiaveFasc = new CSChiaveFasc();
        chiaveFasc.setAnno(dettaglio.getAaFascicolo().intValue());
        chiaveFasc.setNumero(dettaglio.getCdKeyFascicolo());

        getForm().getFascicoloDetail().getUrn_fas()
                .setValue(MessaggiWSFormat.formattaUrnDocUniDoc(
                        MessaggiWSFormat.formattaBaseUrnFascicolo(MessaggiWSFormat.formattaUrnPartVersatore(tmpVers),
                                MessaggiWSFormat.formattaUrnPartFasc(chiaveFasc))));

        // Pulisco il dettaglio File fascicolo
        getForm().getMetaFileFascicoloDetail().reset();

        // Creo dto versatore

        // Recupero le informazioni sul dettaglio del File fascicolo
        BaseRow fileMetaFascicoloDetail = fascicoliEjb.retrieveFasFileMetaVerAipFasc(idFascicolo,
                FasMetaVerAipFascicolo.TiMeta.FASCICOLO.name());
        if (fileMetaFascicoloDetail != null) {
            getForm().getMetaFileFascicoloDetail().copyFromBean(fileMetaFascicoloDetail);
        }

        // Pulisco il dettaglio Indice AIP del fascicolo
        getForm().getMetaIndiceAipFascicoloDetail().reset();
        // Recupero le informazioni sul dettaglio Indice AIP del fascicolo
        BaseRow fileMetaIndiceAipDetail = fascicoliEjb.retrieveFasFileMetaVerAipFasc(idFascicolo,
                FasMetaVerAipFascicolo.TiMeta.INDICE.name());
        if (fileMetaIndiceAipDetail != null) {
            getForm().getMetaIndiceAipFascicoloDetail().copyFromBean(fileMetaIndiceAipDetail);

            // Algoritmo ed Hash "personalizzato"
            if (fileMetaIndiceAipDetail.getString("ds_hash_file") != null) {
                String descrizioneAlgoritmo = fileMetaIndiceAipDetail.getString("ds_algo_hash_file");
                String encoding = fileMetaIndiceAipDetail.getString("cd_encoding_hash_file");
                String valoreHash = fileMetaIndiceAipDetail.getString("ds_hash_file");
                getForm().getMetaIndiceAipFascicoloDetail().getAlgoritmo_personalizzato().setHidden(false);
                getForm().getMetaIndiceAipFascicoloDetail().getHash_personalizzato().setHidden(false);
                getForm().getMetaIndiceAipFascicoloDetail().getCd_encoding_hash_aip_fascicolo().setHidden(false);
                getForm().getMetaIndiceAipFascicoloDetail().getAlgoritmo_personalizzato()
                        .setValue(descrizioneAlgoritmo);
                getForm().getMetaIndiceAipFascicoloDetail().getCd_encoding_hash_aip_fascicolo().setValue(encoding);
                getForm().getMetaIndiceAipFascicoloDetail().getHash_personalizzato().setValue(valoreHash);
            } else {
                getForm().getMetaIndiceAipFascicoloDetail().getAlgoritmo_personalizzato().setHidden(true);
                getForm().getMetaIndiceAipFascicoloDetail().getHash_personalizzato().setHidden(true);
                getForm().getMetaIndiceAipFascicoloDetail().getCd_encoding_hash_aip_fascicolo().setHidden(true);
            }

            // EVO#13993
            getForm().getFascicoloDetail().getScarica_xml_unisincro_fasc().setHidden(false);
            getForm().getFascicoloDetail().getScarica_xml_unisincro_fasc().setEditMode();
            getForm().getFascicoloDetail().getScarica_xml_unisincro_fasc().setDisableHourGlass(true);
            // end EVO#13993

            getForm().getFascicoloDetail().getScarica_external_metadata().setHidden(false);
            getForm().getFascicoloDetail().getScarica_external_metadata().setEditMode();
            getForm().getFascicoloDetail().getScarica_external_metadata().setDisableHourGlass(true);

        } else {
            // EVO#13993
            getForm().getFascicoloDetail().getScarica_xml_unisincro_fasc().setHidden(true);
            // end EVO#13993

            getForm().getFascicoloDetail().getScarica_external_metadata().setHidden(true);
        }

        // Popolamento della descrizione dell'indice di classificazione
        DecVTreeTitolTableBean vociAllPadri = new DecVTreeTitolTableBean();
        if (dettaglio.getIdVoceTitol() != null) {
            vociAllPadri = titolariEjb.getVociAllPadri(dettaglio.getIdVoceTitol());
        }
        getForm().getTreeClassif().setTable(vociAllPadri);

        // Tab Contenuto
        FasVLisUdInFascTableBean table_ud = fascicoliEjb.retrieveFasVLisUdInFasc(idFascicolo, getUser().getIdUtente());
        getForm().getUnitaDocList().setTable(table_ud);
        getForm().getUnitaDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getUnitaDocList().getTable().first();

        getForm().getFascicoloDetail().getDs_abilitazione_ud().setReadonly(true);

        BigDecimal numUD = dettaglio.getNiUnitaDoc();
        int numUDViewed = table_ud.size();

        if (numUD.intValue() != numUDViewed) {
            getForm().getFascicoloDetail().getDs_abilitazione_ud().setHidden(false);
            getForm().getFascicoloDetail().getDs_abilitazione_ud().setValue(MEX_UD_UNAUTHORIZED);
        } else {
            getForm().getFascicoloDetail().getDs_abilitazione_ud().setHidden(true);
            getForm().getFascicoloDetail().getDs_abilitazione_ud().setValue("");
        }

        /*
         * TODO: da decommentare quando verranno gestiti i SOTTOFASCICOLI FasFascicoloTableBean table_sotto =
         * fascicoliEjb.getSottofascicoli(idFascicolo); getForm().getSottofascicoliList().setTable(table_sotto);
         * getForm().getSottofascicoliList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
         * getForm().getSottofascicoliList().getTable().first();
         */

        // Tab Profilo Generale
        getForm().getFascicoloDetail().getScarica_xsd_profilo_generale().setEditMode();
        getForm().getFascicoloDetail().getScarica_profilo_generale().setEditMode();

        FasSogFascicoloTableBean table_sogg = fascicoliEjb.retrieveFasSogFascicoloWithEventi(idFascicolo);
        getForm().getSoggettiCoinvoltiList().setTable(table_sogg);
        getForm().getSoggettiCoinvoltiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getSoggettiCoinvoltiList().getTable().first();

        FasEventoFascicoloTableBean table_eventi = fascicoliEjb.retrieveFasEventoFascicolo(idFascicolo);
        getForm().getEventiList().setTable(table_eventi);
        getForm().getEventiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getEventiList().getTable().first();

        // Tab Profilo Archivistico
        getForm().getFascicoloDetail().getScarica_xsd_profilo_archivistico().setEditMode();
        getForm().getFascicoloDetail().getScarica_profilo_archivistico().setEditMode();

        FasLinkFascicoloTableBean table_link = fascicoliEjb.retrieveFasLinkFascicolo(idFascicolo);
        getForm().getCollegamentiList().setTable(table_link);
        getForm().getCollegamentiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getCollegamentiList().getTable().first();

        // Tab Profilo Normativo
        getForm().getFascicoloDetail().getScarica_xsd_profilo_normativo().setEditMode();
        getForm().getFascicoloDetail().getScarica_profilo_normativo().setEditMode();

        String blXmlNormativo = dettaglio.getBlXmlNormativo();

        XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
        if (blXmlNormativo != null) {
            blXmlNormativo = formatter.prettyPrintWithDOM3LS(blXmlNormativo);
            getForm().getFascicoloDetail().getBl_xml_normativo().setValue(blXmlNormativo);

        }

        // Tab Profilo Specifico
        getForm().getFascicoloDetail().getScarica_xsd_profilo_specifico().setEditMode();
        getForm().getFascicoloDetail().getScarica_profilo_specifico().setEditMode();

        FasValoreAttribFascicoloTableBean table_spec = fascicoliEjb.retrieveFasValoreAttribFascicolo(idFascicolo,
                dettaglio.getIdModelloXsdSpecifico());
        getForm().getDatiSpecificiFascicoloList().setTable(table_spec);
        getForm().getDatiSpecificiFascicoloList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getDatiSpecificiFascicoloList().getTable().first();

        // Tab Responsabili
        // FasRespFascicoloTableBean table_resp = fascicoliEjb.retrieveFasRespFascicolo(idFascicolo);
        // getForm().getResponsabiliList().setTable(table_resp);
        // getForm().getResponsabiliList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        // getForm().getResponsabiliList().getTable().first();

        // Tab UO Responsabili
        // FasUniOrgRespFascicoloTableBean table_uo = fascicoliEjb.retrieveFasUniOrgRespFascicolo(idFascicolo);
        // getForm().getUOResponsabiliList().setTable(table_uo);
        // getForm().getUOResponsabiliList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        // getForm().getUOResponsabiliList().getTable().first();

        // Tab Collegamenti
        // FasLinkFascicoloTableBean table_link = fascicoliEjb.retrieveFasLinkFascicolo(idFascicolo);
        // getForm().getCollegamentiList().setTable(table_link);
        // getForm().getCollegamentiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        // getForm().getCollegamentiList().getTable().first();

        // Tab Elenchi di versamento
        // ElvVRicElencoFascByFasTableBean table_elv = fascicoliEjb.retrieveFasElvFascicolo(idFascicolo);
        // getForm().getElvFascicoliList().setTable(table_elv);
        // getForm().getElvFascicoliList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        // getForm().getElvFascicoliList().getTable().first();

        // Disabilito clessidre bottoni download
        getForm().getFascicoloDetail().getScarica_xsd_profilo_generale().setDisableHourGlass(true);
        getForm().getFascicoloDetail().getScarica_profilo_generale().setDisableHourGlass(true);
        getForm().getFascicoloDetail().getScarica_xsd_profilo_archivistico().setDisableHourGlass(true);
        getForm().getFascicoloDetail().getScarica_profilo_archivistico().setDisableHourGlass(true);
        getForm().getFascicoloDetail().getScarica_xsd_profilo_normativo().setDisableHourGlass(true);
        getForm().getFascicoloDetail().getScarica_profilo_normativo().setDisableHourGlass(true);
        getForm().getFascicoloDetail().getScarica_xsd_profilo_specifico().setDisableHourGlass(true);
        getForm().getFascicoloDetail().getScarica_profilo_specifico().setDisableHourGlass(true);

    }

    // EVO#13993
    @Override
    public void scarica_xml_unisincro_fasc() throws EMFError {
        BigDecimal idFascicolo = getForm().getFascicoloDetail().getId_fascicolo().parse();
        try {

            RecuperoFascicolo recuperoFasc = new RecuperoFascicolo();
            recuperoFasc.setVersione("Web");
            // Versatore
            recuperoFasc.setVersatore(new VersatoreFascType());
            recuperoFasc.getVersatore().setAmbiente(getForm().getFascicoloDetail().getNm_ambiente().parse());
            recuperoFasc.getVersatore().setEnte(getForm().getFascicoloDetail().getNm_ente().parse());
            recuperoFasc.getVersatore().setStruttura(getForm().getFascicoloDetail().getNm_strut().parse());
            recuperoFasc.getVersatore().setUserID(getUser().getIdUtente() + "");
            // Chiave
            recuperoFasc.setChiave(new ChiaveFascType());
            recuperoFasc.getChiave()
                    .setAnno(BigInteger.valueOf(getForm().getFascicoloDetail().getAa_fascicolo().parse().longValue()));
            recuperoFasc.getChiave().setNumero(getForm().getFascicoloDetail().getCd_key_fascicolo().parse());
            //
            CostantiDB.TipiEntitaRecupero tipoEntitaRecupero = CostantiDB.TipiEntitaRecupero.FASC_UNISYNCRO;
            // TODO
            // String tipoSaveFile = udHelper
            // .getTipoSaveFile(getForm().getFascicoloDetail().getId_tipo_fascicolo().parse());
            CostantiDB.TipoSalvataggioFile tipoSalvataggioFile = CostantiDB.TipoSalvataggioFile.BLOB;

            RecuperoFascWeb recuperoFASC = new RecuperoFascWeb(recuperoFasc, getUser(), idFascicolo,
                    tipoSalvataggioFile, tipoEntitaRecupero);
            RispostaWSRecuperoFasc rispostaWs = recuperoFASC.recuperaOggettoFasc();
            //
            switch (rispostaWs.getSeverity()) {
            case OK:
                getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(), getControllerName());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(),
                        rispostaWs.getNomeFile());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(),
                        rispostaWs.getRifFileBinario().getFileSuDisco().getPath());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
                        Boolean.toString(true));
                break;
            case WARNING:
                getMessageBox().addInfo(rispostaWs.getErrorMessage());
                break;
            case ERROR:
                getMessageBox().addError(rispostaWs.getErrorMessage());
                break;
            }
            if (!getMessageBox().isEmpty()) {
                forwardToPublisher(getLastPublisher());
            } else {
                forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
            }
        } catch (Exception e) {
            String message = "Eccezione nel recupero AIP Fascicolo" + ExceptionUtils.getRootCauseMessage(e);
            getMessageBox().addError("Eccezione nel recupero AIP Fascicolo: " + message);
            logger.error("Eccezione nel recupero AIP Fascicolo", e);
        }
    }

    public void download() throws EMFError {
        String filename = (String) getSession().getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name());
        String path = (String) getSession().getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name());
        String contentType = (String) getSession()
                .getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name());
        Boolean deleteFile = Boolean.parseBoolean(
                (String) getSession().getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name()));
        if (path != null && filename != null) {
            File fileToDownload = new File(path);
            if (fileToDownload.exists()) {
                /*
                 * Definiamo l'output previsto che sarÃ un file in formato zip di cui si occuperÃ la servlet per fare il
                 * download
                 */
                OutputStream outUD = getServletOutputStream();
                getResponse().setContentType(StringUtils.isBlank(contentType) ? "application/zip" : contentType);
                getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + filename);

                FileInputStream inputStream = null;
                try {
                    getResponse().setHeader("Content-Length", String.valueOf(fileToDownload.length()));
                    inputStream = new FileInputStream(fileToDownload);
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
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(outUD);
                    inputStream = null;
                    outUD = null;
                    freeze();
                }
                // Nel caso sia stato richiesto, elimina il file
                if (deleteFile) {
                    fileToDownload.delete();
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
    // end EVO#13993

    /**
     * Inizializza i combo della form di dettaglio
     *
     */
    private void initDettaglioFascicolo() {
        getForm().getFascicoloDetail().getFl_forza_contr_classif().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFascicoloDetail().getFl_forza_contr_numero().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFascicoloDetail().getFl_forza_contr_colleg().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

        getForm().getFascicoliDettaglioTabs()
                .setCurrentTab(getForm().getFascicoliDettaglioTabs().getInfoPrincipaliFascicolo());

        getForm().getFascicoliDettaglioBottomTabs()
                .setCurrentTab(getForm().getFascicoliDettaglioBottomTabs().getInfoVersamentoFascicolo());
    }

    /**
     * Metodo di ricerca semplice dei fascicoli (invocato dal bottone di ricerca)
     * 
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void ricercaFascicoli() throws EMFError {
        if (getForm().getFiltriFascicoliRicercaSemplice().postAndValidate(getRequest(), getMessageBox())) {
            // Effettua i controlli logici dei valori e li inserisce nell'opportuno bean
            RicercaFascicoliBean filtri = validaFiltriRicercaFascicoli(getForm().getFiltriFascicoliRicercaSemplice());

            if (!getMessageBox().hasError()) {
                FasVRicFascicoliTableBean table = fascicoliEjb.ricercaFascicoli(filtri,
                        getUser().getIdOrganizzazioneFoglia(), getUser().getIdUtente());
                getForm().getFascicoliList().setTable(table);
                getForm().getFascicoliList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getFascicoliList().getTable().first();
            }
        }

        forwardToPublisher(Application.Publisher.FASCICOLI_RICERCA_SEMPLICE);
    }

    private RicercaFascicoliBean validaFiltriRicercaFascicoli(FiltriFascicoliRicercaSemplice filtri) throws EMFError {
        RicercaFascicoliBean result = new RicercaFascicoliBean();
        FascicoliValidator validator = new FascicoliValidator(getMessageBox());
        // Validazione dei filtri della section FASCICOLI
        validator.validaChiaviFascicoli(result, filtri.getAa_fascicolo(), filtri.getAa_fascicolo_da(),
                filtri.getAa_fascicolo_a(), filtri.getCd_key_fascicolo(), filtri.getCd_key_fascicolo_da(),
                filtri.getCd_key_fascicolo_a());

        result.setNm_tipo_fascicolo(filtri.getId_tipo_fascicolo().parse());
        result.setTi_modello_xsd(filtri.getId_modello_xsd_fascicolo().getHtmlDecodedValue());
        result.setCd_xsd(filtri.getCd_xsd().parse());

        result.setDs_oggetto_fascicolo(filtri.getDs_oggetto_fascicolo().parse());

        Date[] obj = validator.validaOrdineDateOrari(filtri.getDt_ape_fasciolo_da(), filtri.getDt_ape_fasciolo_a());
        if (obj != null) {
            result.setDt_ape_fasciolo_da(obj[0]);
            result.setDt_ape_fasciolo_a(obj[1]);
        }
        obj = validator.validaOrdineDateOrari(filtri.getDt_chiu_fasciolo_da(), filtri.getDt_chiu_fasciolo_a());
        if (obj != null) {
            result.setDt_chiu_fasciolo_da(obj[0]);
            result.setDt_chiu_fasciolo_a(obj[1]);
        }

        result.setCd_proc_ammin(filtri.getCd_proc_ammin().parse());
        result.setDs_proc_ammin(filtri.getDs_proc_ammin().parse());
        result.setNi_aa_conservazione(filtri.getNi_aa_conservazione().parse());
        result.setCd_livello_riserv(filtri.getCd_livello_riserv().parse());
        result.setNm_sistema_versante(filtri.getNm_sistema_versante().parse());
        result.setNm_userid(filtri.getNm_userid().parse());

        // Validazione dei filtri della section PROFILO ARCHIVISTICO
        result.setCd_composito_voce_titol(filtri.getCd_composito_voce_titol().parse());

        validator.validaChiaviFascicoliPadre(result, filtri.getAa_fascicolo_padre(), filtri.getAa_fascicolo_padre_da(),
                filtri.getAa_fascicolo_padre_a(), filtri.getCd_key_fascicolo_padre(),
                filtri.getCd_key_fascicolo_padre_da(), filtri.getCd_key_fascicolo_padre_a());

        result.setDs_oggetto_fascicolo_padre(filtri.getDs_oggetto_fascicolo_padre().parse());

        // Validazione dei filtri della section UNITA'DOCUMENTARIE CONTENUTE NEL FASCICOLO
        result.setCd_registro_key_unita_doc(filtri.getCd_registro_key_unita_doc().getDecodedValue());
        validator.validaChiaviUDdelFascicolo(result, filtri.getAa_key_unita_doc(), filtri.getAa_key_unita_doc_da(),
                filtri.getAa_key_unita_doc_a(), filtri.getCd_key_unita_doc(), filtri.getCd_key_unita_doc_da(),
                filtri.getCd_key_unita_doc_a());

        // Validazione dei filtri della section PARAMETRI DI VERSAMENTO
        result.setTi_conservazione(filtri.getTi_conservazione().parse());
        result.setFl_forza_contr_classif(filtri.getFl_forza_contr_classif().parse());
        result.setFl_forza_contr_numero(filtri.getFl_forza_contr_numero().parse());
        result.setFl_forza_contr_colleg(filtri.getFl_forza_contr_colleg().parse());

        // Validazione dei filtri della section PARAMETRI DI VERSAMENTO
        obj = validator.validaDate(filtri.getTs_vers_fascicolo_da(), filtri.getOre_ts_vers_fascicolo_da(),
                filtri.getMinuti_ts_vers_fascicolo_da(), filtri.getTs_vers_fascicolo_a(),
                filtri.getOre_ts_vers_fascicolo_a(), filtri.getMinuti_ts_vers_fascicolo_a());
        if (obj != null) {
            result.setTs_vers_fascicolo_da(obj[0]);
            result.setTs_vers_fascicolo_a(obj[1]);
        }

        result.setTi_esito(filtri.getTi_esito().parse());
        result.setTi_stato_conservazione(filtri.getTi_stato_conservazione().parse());
        result.setTi_stato_fasc_elenco_vers(filtri.getTi_stato_fasc_elenco_vers().parse());
        result.setCd_versione_ws(filtri.getCd_versione_ws().parse());

        return result;
    }

    @Override
    public void pulisciFascicoli() throws EMFError {
        fascicoliRicercaSemplice();
    }

    /*
     * Metodo invocato dall'esterno con una redirectrToAction per la visualizzazione del dettaglio fascicolo. Riceve in
     * input il seguente parametro:
     * 
     * - idFascicolo: id del fascicolo di cui visualizzare il dettaglio
     */
    /*
     * public void chiamataDallEsterno() throws EMFError { FascicoliForm form = new FascicoliForm(); // Viene
     * inizializzata la FascicoliList di questo dettaglio con un record vuoto // contenente solo l'id del fascicolo
     * FasVRicFascicoliTableBean t = new FasVRicFascicoliTableBean(); FasVRicFascicoliRowBean r = new
     * FasVRicFascicoliRowBean(); String id = getRequest().getParameter("idFascicolo"); r.setIdFascicolo(new
     * BigDecimal(id)); t.add(r); t.setPageSize(WebConstants.DEFAULT_PAGE_SIZE); t.first();
     * form.getFascicoliList().setTable(t); setForm(form); loadDettaglio();
     * forwardToPublisher(Application.Publisher.FASCICOLI_DETAIL); }
     */
    private void redirectToUnitaDocumentariePage() throws EMFError {
        UnitaDocumentarieForm form = new UnitaDocumentarieForm();

        form.getUnitaDocumentarieList().setFilterValidRecords(getForm().getUnitaDocList().isFilterValidRecords());
        redirectToPage(Application.Actions.UNITA_DOCUMENTARIE, form, form.getUnitaDocumentarieList().getName(),
                getForm().getUnitaDocList().getTable(), getNavigationEvent());
    }

    private void redirectToElvFascicoliPage() throws EMFError {
        ElenchiVersFascicoliForm form = new ElenchiVersFascicoliForm();

        form.getElenchiVersFascicoliList()
                .setFilterValidRecords(getForm().getElvFascicoliList().isFilterValidRecords());
        redirectToPage(Application.Actions.ELENCHI_VERS_FASCICOLI, form, form.getElenchiVersFascicoliList().getName(),
                getForm().getElvFascicoliList().getTable(), getNavigationEvent());
    }

    private void redirectToPage(final String action, BaseForm form, String listToPopulate, BaseTableInterface<?> table,
            String event) throws EMFError {
        ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) form.getComponent(listToPopulate)).setTable(table);
        redirectToAction(action, "?operation=listNavigationOnClick&navigationEvent=" + event + "&table="
                + listToPopulate + "&riga=" + table.getCurrentRowIndex(), form);
    }

    @Override
    public void addToRichAnnul() throws EMFError {
        boolean forceGoBack = false;
        if (!getForm().getFascicoliPerRichAnnulVersList().getTable().isEmpty()) {
            BigDecimal idRichAnnulVers = getForm().getFascicoliPerRichAnnulVers().getId_rich_annul_vers().parse();
            try {
                if (idRichAnnulVers != null) {
                    BigDecimal ultimoProgressivoItemRichiesta = annulVersEjb
                            .getUltimoProgressivoItemRichiesta(idRichAnnulVers);
                    int progressivo = ultimoProgressivoItemRichiesta.add(BigDecimal.ONE).intValue();
                    for (BaseRowInterface row : getForm().getFascicoliPerRichAnnulVersList().getTable()) {
                        BigDecimal idFascicolo = row.getBigDecimal("id_fascicolo");
                        BigDecimal aaFascicolo = row.getBigDecimal("aa_fascicolo");
                        String cdKeyFascicolo = row.getString("cd_key_fascicolo");

                        if (!annulVersEjb.isFascInRichAnnulVers(idFascicolo)) {
                            annulVersEjb.addFascicoloToRichAnnulVers(idRichAnnulVers, aaFascicolo, cdKeyFascicolo,
                                    progressivo++, getUser().getIdUtente());
                        } else {
                            getMessageBox().addError("Il fascicolo " + aaFascicolo.toPlainString() + "-"
                                    + cdKeyFascicolo + " \u00E8 gi\u00E0 presente in una richiesta di annullamento");
                        }
                    }
                    if (!getMessageBox().hasError()) {
                        getMessageBox()
                                .addInfo("I fascicoli selezionati sono stati aggiunti con successo alla richiesta");
                        getMessageBox().setViewMode(MessageBox.ViewMode.plain);

                        getForm().getFascicoliPerRichAnnulVersList().getTable().clear();
                    }
                } else {
                    getMessageBox().addError(
                            "Errore inaspettato nell'aggiunta di fascicoli alla richiesta di annullamento : richiesta non caricata");
                    forceGoBack = true;
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        } else {
            getMessageBox().addError(
                    "Selezionare almeno un fascicolo da aggiungere alla richiesta di annullamento selezionata");
        }
        if (forceGoBack) {
            goBack();
        } else {
            forwardToPublisher(Application.Publisher.FASCICOLI_RICERCA_SEMPLICE);
        }
    }

    @Override
    public void selectFascicoliPerRichAnnulVersList() throws EMFError {
        BaseRowInterface row = getForm().getFascicoliPerRichAnnulVersList().getTable().getCurrentRow();
        int index = getForm().getFascicoliPerRichAnnulVersList().getTable().getCurrentRowIndex();
        getForm().getFascicoliPerRichAnnulVersList().getTable().remove(index);
        getForm().getFascicoliList().getTable().addFullIdx(row);

        forwardToPublisher(Application.Publisher.FASCICOLI_RICERCA_SEMPLICE);
    }

    @Override
    public void selectFascicoliList() throws EMFError {
        BaseRowInterface row = getForm().getFascicoliList().getTable().getCurrentRow();
        int index = getForm().getFascicoliList().getTable().getCurrentRowIndex();
        BigDecimal idFascicolo = row.getBigDecimal("id_fascicolo");
        BigDecimal aaFascicolo = row.getBigDecimal("aa_fascicolo");
        String cdKeyFascicolo = row.getString("cd_key_fascicolo");
        String tiStatoConvervazione = row.getString("ti_stato_conservazione");
        // Aggiunta a una richiesta di annullamento
        BigDecimal idRichAnnulVers = getForm().getFascicoliPerRichAnnulVers().getId_rich_annul_vers().parse();
        if (!tiStatoConvervazione.equals("ANNULLATO")) {
            // Non deve mai capitare, ma per sicurezza eseguo il controllo
            if (idRichAnnulVers != null) {
                if (!annulVersEjb.isFascInRichAnnulVers(idFascicolo)) {
                    // Dato che la lista è paginata, devo richiamare il metodo removeFullIdx() al posto di remove()
                    getForm().getFascicoliList().getTable().removeFullIdx(index);
                    /* Aggiungo il record nella lista dei fascicoli selezionat */
                    getForm().getFascicoliPerRichAnnulVersList().add(row);
                } else {
                    getMessageBox().addError("Il fascicolo " + aaFascicolo.toPlainString() + "-" + cdKeyFascicolo
                            + " \u00E8 gi\u00E0 presente in una richiesta di annullamento");
                }
            } else {
                getMessageBox()
                        .addError("Errore inaspettato nell'aggiunta di un fascicolo alla richiesta di annullamento");
                // forceGoBack = true;
            }
        } else {
            getMessageBox().addError("Il fascicolo " + aaFascicolo.toPlainString() + "-" + cdKeyFascicolo
                    + " non può essere selezionato in quanto in stato ANNULLATO");
        }
        forwardToPublisher(Application.Publisher.FASCICOLI_RICERCA_SEMPLICE);
    }

    /**
     * Metodo di inizializzazione form di ricerca fascicoli di versamenti annullati
     *
     * @throws EMFError
     *             errore generico
     */
    @Secure(action = "Menu.AnnulVers.FascicoliRicercaVersAnnullati")
    public void fascicoliRicercaVersAnnullati() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.AnnulVers.FascicoliRicercaVersAnnullati");

        // Pulisco i filtri di ricerca fascicoli annullati
        getForm().getFiltriFascicoliAnnullati().reset();

        // Setto le varie combo dei FILTRI di ricerca Fascicoli annullati
        DecTipoFascicoloTableBean tipiFascicolo = tipoFascicoliEjb.getTipiFascicoloAbilitati(getUser().getIdUtente(),
                getUser().getIdOrganizzazioneFoglia(), false);
        DecodeMap mapTipiFascicolo = new DecodeMap();
        mapTipiFascicolo.populatedMap(tipiFascicolo, "id_tipo_fascicolo", "nm_tipo_fascicolo");
        getForm().getFiltriFascicoliAnnullati().getNm_tipo_fascicolo().setDecodeMap(mapTipiFascicolo);

        // Imposto i filtri in edit mode
        getForm().getFiltriFascicoliAnnullati().setEditMode();

        // Inizializzo la lista dei fascicoli annullati vuota e con 10 righe per pagina
        getForm().getFascicoliAnnullatiList().setTable(null);

        // Carico la pagina di ricerca
        forwardToPublisher(Application.Publisher.FASCICOLI_RICERCA_FASC_ANNULLATI);
    }

    private RicercaFascicoliBean validaFiltriRicercaFascicoliAnnullati(FiltriFascicoliAnnullati filtri)
            throws EMFError {
        RicercaFascicoliBean result = new RicercaFascicoliBean();
        FascicoliValidator validator = new FascicoliValidator(getMessageBox());

        // Validazione dei filtri della section FASCICOLI
        validator.validaChiaviFascicoli(result, filtri.getAa_fascicolo(), filtri.getAa_fascicolo_da(),
                filtri.getAa_fascicolo_a(), filtri.getCd_key_fascicolo(), filtri.getCd_key_fascicolo_da(),
                filtri.getCd_key_fascicolo_a());

        result.setNm_tipo_fascicolo(filtri.getNm_tipo_fascicolo().parse());

        // Validazione dei filtri della section PROFILO ARCHIVISTICO
        result.setCd_composito_voce_titol(filtri.getCd_composito_voce_titol().parse());

        return result;
    }

    @Override
    public void pulisciFascAnnullati() throws EMFError {
        fascicoliRicercaVersAnnullati();
    }

    /**
     * Metodo di ricerca dei fascicoli annullati (invocato dal bottone di ricerca)
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void ricercaFascAnnullati() throws EMFError {
        if (getForm().getFiltriFascicoliAnnullati().postAndValidate(getRequest(), getMessageBox())) {
            // Effettua i controlli logici dei valori e li inserisce nell'opportuno bean
            RicercaFascicoliBean filtri = validaFiltriRicercaFascicoliAnnullati(
                    getForm().getFiltriFascicoliAnnullati());

            if (!getMessageBox().hasError()) {
                FasVRicFascicoliTableBean table = fascicoliEjb.ricercaFascicoliAnnullati(filtri,
                        getUser().getIdOrganizzazioneFoglia(), getUser().getIdUtente());
                getForm().getFascicoliAnnullatiList().setTable(table);
                getForm().getFascicoliAnnullatiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getFascicoliAnnullatiList().getTable().first();
            }
        }

        forwardToPublisher(Application.Publisher.FASCICOLI_RICERCA_FASC_ANNULLATI);
    }

    public void scarica_rv_fasc(String... rigaElemento) throws EMFError {
        FasVRicFascicoliRowBean row;
        if (rigaElemento != null) {
            int startIndex = (getForm().getFascicoliAnnullatiList().getTable().getCurrentPageIndex() - 1)
                    * getForm().getFascicoliAnnullatiList().getTable().getPageSize();
            int endIndex = ((getForm().getFascicoliAnnullatiList().getTable().getCurrentPageIndex() - 1)
                    * getForm().getFascicoliAnnullatiList().getTable().getPageSize())
                    + getForm().getFascicoliAnnullatiList().getTable().getPageSize();
            row = (FasVRicFascicoliRowBean) getForm().getFascicoliAnnullatiList().getTable()
                    .getRow((Integer.parseInt(rigaElemento[0]) + startIndex) % endIndex);
        } else {
            row = (FasVRicFascicoliRowBean) getForm().getFascicoliAnnullatiList().getTable().getCurrentRow();
        }

        /*
         * Ricavo il fascicolo prendendo i dati da DB anche se li ho già nella maschera di dettaglio, onde evitare che
         * un domani, eliminando quei campi dall'online, vada tutto in vacca
         */
        FasVVisFascicoloRowBean fascRB = fascicoliEjb.retrieveFasVVisFascicolo(row.getIdFascicolo());
        String filename = "RV-Fascicolo_" + fascRB.getAaFascicolo().toString() + "-" + fascRB.getCdKeyFascicolo();

        /*
         * Definiamo l'output previsto che sarà un file in formato zip di cui si occuperà la servlet per fare il
         * download
         */
        ZipOutputStream outUD = new ZipOutputStream(getServletOutputStream());
        getResponse().setContentType("application/zip");
        getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".zip");

        try {
            zippaRapportoVersamento(outUD, fascRB);
            outUD.flush();
            outUD.close();
        } catch (Exception e) {
            // getMessageBox().addMessage(new Message(MessageLevel.FATAL, "Errore nel recupero dei file da zippare"));
            logger.error("Eccezione", e);
        } finally {
            freeze();
        }
    }

    /**
     * Metodo che genera il file zip dato l'outputStream e i dati da inserire
     *
     * @param out
     *            l'outputStream da utilizzare
     * @param tb
     *            il tableBean contenente i dati
     * 
     * @throws EMFError
     *             errore generico
     * @throws IOException
     *             errore generico
     * @throws UnsupportedEncodingException
     *             errore generico
     */
    private void zippaRapportoVersamento(ZipOutputStream out, FasVVisFascicoloRowBean fascRB)
            throws EMFError, IOException, UnsupportedEncodingException {
        // Definiamo il buffer per lo stream di bytes
        byte[] data = new byte[1000];
        InputStream is = null;

        if (fascRB != null) {
            // Ricavo i nomi dei file xml di richiesta e risposta del fascicolo
            String prefisso = fascRB.getAaFascicolo() + "-" + fascRB.getCdKeyFascicolo();

            String xmlRappVersName;
            if (fascRB.getDsUrnXmlRapp() != null && !fascRB.getDsUrnXmlRapp().isEmpty()) {
                xmlRappVersName = "RapportoVersamento_" + ComponenteRec.estraiNomeFileCompleto(fascRB.getDsUrnXmlRapp())
                        + ".xml";
            } else {
                xmlRappVersName = "RapportoVersamento_" + prefisso + ".xml";
            }

            // Ricavo il CLOBBO, che è uno stringone
            String xmlRappClob = fascRB.getBlXmlVersRapp();

            // Inserisco nello zippone il CLOB dell'xml di rapporto di versamento del fascicolo
            if (xmlRappClob != null && !xmlRappClob.isEmpty()) {
                is = new ByteArrayInputStream(xmlRappClob.getBytes("UTF-8"));
                int count;
                out.putNextEntry(new ZipEntry(xmlRappVersName));
                while ((count = is.read(data, 0, 1000)) != -1) {
                    out.write(data, 0, count);
                }
                out.closeEntry();
            }
        }

        if (is != null) {
            is.close();
        }
    }

    public void scarica_xml_rich(String... rigaElemento) throws EMFError {
        FasVRicFascicoliRowBean row;
        if (rigaElemento != null) {
            int startIndex = (getForm().getFascicoliAnnullatiList().getTable().getCurrentPageIndex() - 1)
                    * getForm().getFascicoliAnnullatiList().getTable().getPageSize();
            int endIndex = ((getForm().getFascicoliAnnullatiList().getTable().getCurrentPageIndex() - 1)
                    * getForm().getFascicoliAnnullatiList().getTable().getPageSize())
                    + getForm().getFascicoliAnnullatiList().getTable().getPageSize();
            row = (FasVRicFascicoliRowBean) getForm().getFascicoliAnnullatiList().getTable()
                    .getRow((Integer.parseInt(rigaElemento[0]) + startIndex) % endIndex);
        } else {
            row = (FasVRicFascicoliRowBean) getForm().getFascicoliAnnullatiList().getTable().getCurrentRow();
        }

        // Ricavo l'identificativo della richiesta di annullamento
        BigDecimal idRichAnnulVers = row.getBigDecimal("id_rich_annul_vers");

        FasVVisFascicoloRowBean fascRB = fascicoliEjb.retrieveFasVVisFascicolo(row.getIdFascicolo());

        String filename = "RichiestaAnnullamento-Fascicolo_" + fascRB.getAaFascicolo().toString() + "-"
                + fascRB.getCdKeyFascicolo();

        /*
         * Definiamo l'output previsto che sarà un file in formato zip di cui si occuperà la servlet per fare il
         * download
         */
        ZipOutputStream outUD = new ZipOutputStream(getServletOutputStream());
        getResponse().setContentType("application/zip");
        getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".zip");

        try {
            zippaXmlAnnulVers(outUD, fascRB, idRichAnnulVers, CostantiDB.TiXmlRichAnnulVers.RICHIESTA);
            outUD.flush();
            outUD.close();
        } catch (Exception e) {
            // getMessageBox().addMessage(new Message(MessageLevel.FATAL, "Errore nel recupero dei file da zippare"));
            logger.error("Eccezione", e);
        } finally {
            freeze();
        }
    }

    public void scarica_xml_risp(String... rigaElemento) throws EMFError {
        FasVRicFascicoliRowBean row;
        if (rigaElemento != null) {
            int startIndex = (getForm().getFascicoliAnnullatiList().getTable().getCurrentPageIndex() - 1)
                    * getForm().getFascicoliAnnullatiList().getTable().getPageSize();
            int endIndex = ((getForm().getFascicoliAnnullatiList().getTable().getCurrentPageIndex() - 1)
                    * getForm().getFascicoliAnnullatiList().getTable().getPageSize())
                    + getForm().getFascicoliAnnullatiList().getTable().getPageSize();
            row = (FasVRicFascicoliRowBean) getForm().getFascicoliAnnullatiList().getTable()
                    .getRow((Integer.parseInt(rigaElemento[0]) + startIndex) % endIndex);
        } else {
            row = (FasVRicFascicoliRowBean) getForm().getFascicoliAnnullatiList().getTable().getCurrentRow();
        }

        // Ricavo l'identificativo della richiesta di annullamento
        BigDecimal idRichAnnulVers = row.getBigDecimal("id_rich_annul_vers");

        FasVVisFascicoloRowBean fascRB = fascicoliEjb.retrieveFasVVisFascicolo(row.getIdFascicolo());

        String filename = "EsitoAnnullamento-Fascicolo_" + fascRB.getAaFascicolo().toString() + "-"
                + fascRB.getCdKeyFascicolo();

        /*
         * Definiamo l'output previsto che sarà un file in formato zip di cui si occuperà la servlet per fare il
         * download
         */
        ZipOutputStream outUD = new ZipOutputStream(getServletOutputStream());
        getResponse().setContentType("application/zip");
        getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".zip");

        try {
            zippaXmlAnnulVers(outUD, fascRB, idRichAnnulVers, CostantiDB.TiXmlRichAnnulVers.RISPOSTA);
            outUD.flush();
            outUD.close();
        } catch (Exception e) {
            // getMessageBox().addMessage(new Message(MessageLevel.FATAL, "Errore nel recupero dei file da zippare"));
            logger.error("Eccezione", e);
        } finally {
            freeze();
        }
    }

    /**
     * Metodo che genera il file zip dato l'outputStream e i dati da inserire
     *
     * @param out
     *            l'outputStream da utilizzare
     * @param fascRB
     *            il rowBean contenente i dati
     * 
     * @throws EMFError
     *             errore generico
     * @throws IOException
     *             errore generico
     * @throws UnsupportedEncodingException
     *             errore generico
     */
    private void zippaXmlAnnulVers(ZipOutputStream out, FasVVisFascicoloRowBean fascRB, BigDecimal idRichAnnulVers,
            CostantiDB.TiXmlRichAnnulVers tiXmlRichAnnulVers)
            throws EMFError, IOException, UnsupportedEncodingException {
        // Definiamo il buffer per lo stream di bytes
        byte[] data = new byte[1000];
        InputStream is = null;

        // Se ho la richiesta di annullamento, piazzo nello zippone i suoi CLOBBI di richiesta o risposta
        if (fascRB != null) {
            // Ricavo i nomi dei file xml di richiesta o risposta
            String prefisso = fascRB.getAaFascicolo() + "-" + fascRB.getCdKeyFascicolo();

            switch (tiXmlRichAnnulVers) {
            case RICHIESTA:
                String xmlRichAnnulVersName = "RichiestaAnnullamento-Fascicolo_" + prefisso + ".xml";

                // Ricavo il CLOBBO, che è uno stringone
                String xmlRichAnnulVersClob = annulVersEjb.getXmlRichAnnulVersByTipo(idRichAnnulVers,
                        tiXmlRichAnnulVers);

                // Inserisco nello zippone il CLOB dell'xml di richiesta dell'annullamento dell'unità documentaria
                if (xmlRichAnnulVersClob != null && !xmlRichAnnulVersClob.isEmpty()) {
                    is = new ByteArrayInputStream(xmlRichAnnulVersClob.getBytes("UTF-8"));
                    int count;
                    out.putNextEntry(new ZipEntry(xmlRichAnnulVersName));
                    while ((count = is.read(data, 0, 1000)) != -1) {
                        out.write(data, 0, count);
                    }
                    out.closeEntry();
                }
                break;
            case RISPOSTA:
                String xmlRispAnnulVersName = "EsitoAnnullamento-Fascicolo_" + prefisso + ".xml";

                // Ricavo il CLOBBO, che è uno stringone
                String xmlRispAnnulVersClob = annulVersEjb.getXmlRichAnnulVersByTipo(idRichAnnulVers,
                        tiXmlRichAnnulVers);

                // Inserisco nello zippone il CLOB dell'xml di risposta dell'annullamento dell'unità documentaria
                if (xmlRispAnnulVersClob != null && !xmlRispAnnulVersClob.isEmpty()) {
                    is = new ByteArrayInputStream(xmlRispAnnulVersClob.getBytes("UTF-8"));
                    int count;
                    out.putNextEntry(new ZipEntry(xmlRispAnnulVersName));
                    while ((count = is.read(data, 0, 1000)) != -1) {
                        out.write(data, 0, count);
                    }
                    out.closeEntry();
                }
                break;
            }

            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * Trigger sul filtro "Tipo Fascicolo"
     * 
     * @return oggetto JSON
     * 
     * @throws EMFError
     *             errore generico
     */
    @Override
    public JSONObject triggerFiltriFascicoliRicercaSempliceId_tipo_fascicoloOnTrigger() throws EMFError {

        getForm().getFiltriFascicoliRicercaSemplice().post(getRequest());
        BigDecimal idTipoFascicolo = getForm().getFiltriFascicoliRicercaSemplice().getId_tipo_fascicolo().parse();

        if (idTipoFascicolo != null) {
            // Recupero i modelli Xsd relativi al tipo fascicolo selezionato
            DecodeMap mappaModelliXsd = new DecodeMap();
            getForm().getFiltriFascicoliRicercaSemplice().getId_modello_xsd_fascicolo().setDecodeMap(new DecodeMap());
            mappaModelliXsd.populatedMap(
                    fascicoliEjb.getDecModelloXsdFascicoloTableBeanByTipoFascicolo(idTipoFascicolo.longValue()),
                    "id_modello_xsd_fascicolo", "ti_modello_xsd");
            getForm().getFiltriFascicoliRicercaSemplice().getId_modello_xsd_fascicolo().setDecodeMap(mappaModelliXsd);
            getForm().getFiltriFascicoliRicercaSemplice().getCd_xsd().setDecodeMap(new DecodeMap());
        } else {
            getForm().getFiltriFascicoliRicercaSemplice().getId_modello_xsd_fascicolo().setDecodeMap(new DecodeMap());
            getForm().getFiltriFascicoliRicercaSemplice().getCd_xsd().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriFascicoliRicercaSemplice().asJSON();
    }

    /**
     * 
     * @return JSONObject oggetto JSON
     * 
     * @throws EMFError
     *             errore generico
     */
    @Override
    public JSONObject triggerFiltriFascicoliRicercaSempliceId_modello_xsd_fascicoloOnTrigger() throws EMFError {
        getForm().getFiltriFascicoliRicercaSemplice().post(getRequest());
        BigDecimal idTipoFascicolo = getForm().getFiltriFascicoliRicercaSemplice().getId_tipo_fascicolo().parse();
        BigDecimal idModelloXsdFascicolo = getForm().getFiltriFascicoliRicercaSemplice().getId_modello_xsd_fascicolo()
                .parse();

        if (idModelloXsdFascicolo != null) {
            // Recupero le versioni dei modelli Xsd relativi al tipo fascicolo selezionato
            DecodeMap mappaModelliXsd = new DecodeMap();
            getForm().getFiltriFascicoliRicercaSemplice().getCd_xsd().setDecodeMap(new DecodeMap());
            mappaModelliXsd.populatedMap(fascicoliEjb.getDecModelloXsdFascicoloTableBeanByTipoFascicoloAndModelloXsd(
                    idTipoFascicolo.longValue(), idModelloXsdFascicolo.longValue()), "cd_xsd", "cd_xsd");
            getForm().getFiltriFascicoliRicercaSemplice().getCd_xsd().setDecodeMap(mappaModelliXsd);
        } else {
            getForm().getFiltriFascicoliRicercaSemplice().getCd_xsd().setDecodeMap(new DecodeMap());
        }

        return getForm().getFiltriFascicoliRicercaSemplice().asJSON();
    }

    @Override
    public void tabContenutoFascicoloOnClick() throws EMFError {
        getForm().getFascicoliDettaglioTabs()
                .setCurrentTab(getForm().getFascicoliDettaglioTabs().getContenutoFascicolo());
        forwardToPublisher(Application.Publisher.FASCICOLI_DETAIL);
    }

    @Override
    public void tabProfiloGeneraleFascicoloOnClick() throws EMFError {
        getForm().getFascicoliDettaglioTabs()
                .setCurrentTab(getForm().getFascicoliDettaglioTabs().getProfiloGeneraleFascicolo());
        forwardToPublisher(Application.Publisher.FASCICOLI_DETAIL);
    }

    @Override
    public void tabProfiloArchivisticoFascicoloOnClick() throws EMFError {
        getForm().getFascicoliDettaglioTabs()
                .setCurrentTab(getForm().getFascicoliDettaglioTabs().getProfiloArchivisticoFascicolo());
        forwardToPublisher(Application.Publisher.FASCICOLI_DETAIL);
    }

    @Override
    public void tabProfiloNormativoFascicoloOnClick() throws EMFError {
        getForm().getFascicoliDettaglioTabs()
                .setCurrentTab(getForm().getFascicoliDettaglioTabs().getProfiloNormativoFascicolo());
        forwardToPublisher(Application.Publisher.FASCICOLI_DETAIL);
    }

    @Override
    public void tabProfiloSpecificoFascicoloOnClick() throws EMFError {
        getForm().getFascicoliDettaglioTabs()
                .setCurrentTab(getForm().getFascicoliDettaglioTabs().getProfiloSpecificoFascicolo());
        forwardToPublisher(Application.Publisher.FASCICOLI_DETAIL);
    }

    final static String XSD_EXTENSION = "xsd";
    final static String XML_EXTENSION = "xml";

    @Override
    public void scarica_xsd_profilo_generale() throws EMFError {
        BigDecimal idFascicolo = getForm().getFascicoloDetail().getId_fascicolo().parse();

        FasVVisFascicoloRowBean fascRB = fascicoliEjb.retrieveFasVVisFascicolo(idFascicolo);

        // String filename = "XsdProfiloGeneraleFascicolo_" + fascRB.getCdXsdProfilo() + "_"
        // + fascRB.getAaFascicolo().toString() + "-" + fascRB.getCdKeyFascicolo();

        String codiceVersione = fascRB.getCdXsdProfilo();
        String nomeTipo = fascRB.getTiModelloXsdProfilo();

        String filename = nomeTipo + "_xsd_" + codiceVersione;

        if (fascRB.getBlXsdVersProfilo() != null) {
            scarica_file_fascicolo(filename, XSD_EXTENSION, fascRB.getBlXsdVersProfilo());
        } else {
            getMessageBox().addWarning("Nessun file da scaricare presente");
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void scarica_xsd_profilo_archivistico() throws EMFError {
        BigDecimal idFascicolo = getForm().getFascicoloDetail().getId_fascicolo().parse();

        FasVVisFascicoloRowBean fascRB = fascicoliEjb.retrieveFasVVisFascicolo(idFascicolo);

        String codiceVersione = fascRB.getCdXsdSegnatura();
        String nomeTipo = fascRB.getTiModelloXsdSegnatura();

        String filename = nomeTipo + "_xsd_" + codiceVersione;
        if (fascRB.getBlXsdSegnatura() != null) {
            scarica_file_fascicolo(filename, XSD_EXTENSION, fascRB.getBlXsdSegnatura());
        } else {
            getMessageBox().addWarning("Nessun file da scaricare presente");
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void scarica_xsd_profilo_normativo() throws EMFError {
        BigDecimal idFascicolo = getForm().getFascicoloDetail().getId_fascicolo().parse();

        FasVVisFascicoloRowBean fascRB = fascicoliEjb.retrieveFasVVisFascicolo(idFascicolo);

        String codiceVersione = fascRB.getCdXsdNormativo();
        String nomeTipo = fascRB.getTiModelloXsdNormativo();

        String filename = nomeTipo + "_xsd_" + codiceVersione;

        if (fascRB.getBlXsdNormativo() != null) {
            scarica_file_fascicolo(filename, XSD_EXTENSION, fascRB.getBlXsdNormativo());
        } else {
            getMessageBox().addWarning("Nessun file da scaricare presente");
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void scarica_xsd_profilo_specifico() throws EMFError {
        BigDecimal idFascicolo = getForm().getFascicoloDetail().getId_fascicolo().parse();

        FasVVisFascicoloRowBean fascRB = fascicoliEjb.retrieveFasVVisFascicolo(idFascicolo);

        String codiceVersione = fascRB.getCdXsdSpecifico();
        String nomeTipo = fascRB.getTiModelloXsdSpecifico();

        String filename = nomeTipo + "_xsd_" + codiceVersione;

        if (fascRB.getBlXsdSpecifico() != null) {
            scarica_file_fascicolo(filename, XSD_EXTENSION, fascRB.getBlXsdSpecifico());
        } else {
            getMessageBox().addWarning("Nessun file da scaricare presente");
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void scarica_profilo_generale() throws EMFError {
        BigDecimal idFascicolo = getForm().getFascicoloDetail().getId_fascicolo().parse();

        FasVVisFascicoloRowBean fascRB = fascicoliEjb.retrieveFasVVisFascicolo(idFascicolo);

        if (fascRB.getBlXmlVersProfilo() != null) {
            String filename = "ProfiloGeneraleFascicolo_" + fascRB.getDsUrnXmlSip().replaceAll(":", "_");
            scarica_file_fascicolo(filename, XML_EXTENSION, fascRB.getBlXmlVersProfilo());
        } else {
            getMessageBox().addWarning("Nessun file da scaricare presente");
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void scarica_profilo_archivistico() throws EMFError {
        BigDecimal idFascicolo = getForm().getFascicoloDetail().getId_fascicolo().parse();

        FasVVisFascicoloRowBean fascRB = fascicoliEjb.retrieveFasVVisFascicolo(idFascicolo);

        if (fascRB.getBlXmlSegnatura() != null) {
            String filename = "ProfiloArchivisticoFascicolo_" + fascRB.getDsUrnXmlSip().replaceAll(":", "_");
            scarica_file_fascicolo(filename, XML_EXTENSION, fascRB.getBlXmlSegnatura());
        } else {
            getMessageBox().addWarning("Nessun file da scaricare presente");
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void scarica_profilo_normativo() throws EMFError {
        BigDecimal idFascicolo = getForm().getFascicoloDetail().getId_fascicolo().parse();

        FasVVisFascicoloRowBean fascRB = fascicoliEjb.retrieveFasVVisFascicolo(idFascicolo);

        if (fascRB.getBlXmlNormativo() != null) {
            String filename = "ProfiloNormativoFascicolo_" + fascRB.getDsUrnXmlSip().replaceAll(":", "_");
            scarica_file_fascicolo(filename, XML_EXTENSION, fascRB.getBlXmlNormativo());
        } else {
            getMessageBox().addWarning("Nessun file da scaricare presente");
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void scarica_profilo_specifico() throws EMFError {
        BigDecimal idFascicolo = getForm().getFascicoloDetail().getId_fascicolo().parse();

        FasVVisFascicoloRowBean fascRB = fascicoliEjb.retrieveFasVVisFascicolo(idFascicolo);

        if (fascRB.getBlXmlSpecifico() != null) {
            String filename = "ProfiloSpecificoFascicolo_" + fascRB.getDsUrnXmlSip().replaceAll(":", "_");
            scarica_file_fascicolo(filename, XML_EXTENSION, fascRB.getBlXmlSpecifico());
        } else {
            getMessageBox().addWarning("Nessun file da scaricare presente");
            forwardToPublisher(getLastPublisher());
        }
    }

    public void scarica_file_fascicolo(String filename, String estensione, String blobbo) throws EMFError {
        ZipOutputStream outUD = new ZipOutputStream(getServletOutputStream());
        getResponse().setContentType("application/zip");
        getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".zip");

        // Bonifico il filename
        filename = filename.replaceAll("/", "_");

        try {
            zipFileProfilo(outUD, blobbo, filename, estensione);
            outUD.flush();
            outUD.close();
        } catch (IOException e) {
            logger.error("Eccezione", e);
        } finally {
            freeze();
        }
    }

    private void zipFileProfilo(ZipOutputStream out, String blobbo, String filename, String estensione)
            throws IOException {

        // definiamo il buffer per lo stream di bytes
        byte[] data = new byte[1000];
        InputStream is = null;
        if (blobbo != null) {

            byte[] blob = blobbo.getBytes();
            if (blob != null) {
                is = new ByteArrayInputStream(blob);
                int count;
                out.putNextEntry(new ZipEntry(filename + "." + estensione));
                while ((count = is.read(data, 0, 1000)) != -1) {
                    out.write(data, 0, count);
                }
                out.closeEntry();
            }
        }
        is.close();
    }

    @Override
    public void scarica_external_metadata() throws EMFError {
        BigDecimal idFascicolo = getForm().getFascicoloDetail().getId_fascicolo().parse();

        ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(getServletOutputStream());
        getResponse().setContentType("application/zip");

        try {
            getMetadatiFascV2(zipOutputStream, idFascicolo.longValue());
            zipOutputStream.flush();
            zipOutputStream.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(FascicoliAction.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            freeze();
        }
    }

    private void getMetadatiFascV2(ZipArchiveOutputStream zipOutputStream, long idFascicolo)
            throws IOException, EMFError {
        RispostaControlli rispostaControlli = controlliRecuperoFasc.leggiXMLMetadatiFasc(idFascicolo);

        List<FasFileMetaVerAipFasc> lstFasFileMetaFasc = (List<FasFileMetaVerAipFasc>) rispostaControlli.getrObject();
        String fileName;
        String fileNameZip = "";

        if (lstFasFileMetaFasc != null && !lstFasFileMetaFasc.isEmpty()) {
            boolean closeEntry = false;
            FasFileMetaVerAipFasc fileMetaIndiceLastVers = lstFasFileMetaFasc.remove(0);
            if (fileMetaIndiceLastVers.getFasMetaVerAipFascicolo() != null) {
                // Recupero lo urn ORIGINALE
                String urnMetaFasc = fileMetaIndiceLastVers.getFasMetaVerAipFascicolo().getDsUrnMetaFascicolo();

                /* Definisco il nome e l'estensione del file */
                fileName = it.eng.parer.async.utils.IOUtils.getFilename(
                        it.eng.parer.async.utils.IOUtils.extractPartUrnName(urnMetaFasc, true),
                        it.eng.parer.async.utils.IOUtils.CONTENT_TYPE.XML.getFileExt());

                fileNameZip = it.eng.parer.async.utils.IOUtils
                        .getFilename(it.eng.parer.async.utils.IOUtils.extractPartUrnName(urnMetaFasc, true));
                /*
                 * Definisco il percorso relativo del file rispetto alla posizione dell'indice di conservazione
                 */
                String path = it.eng.parer.async.utils.IOUtils.getAbsolutePath("metadati", fileName,
                        it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);

                /* Definisco il nome e l'estensione del file */
                // fileName = "Fascicolo.xml";

                ZipArchiveEntry zae = new ZipArchiveEntry(path);
                filterZipEntry(zae);
                zipOutputStream.putArchiveEntry(zae);
                zipOutputStream.write(fileMetaIndiceLastVers.getBlFileVerIndiceAip().getBytes(StandardCharsets.UTF_8));
                closeEntry = true;
            }

            for (FasFileMetaVerAipFasc fileMetaIndicePrecVers : lstFasFileMetaFasc) {
                if (fileMetaIndicePrecVers.getFasMetaVerAipFascicolo() != null) {

                    // Recupero lo urn ORIGINALE
                    String urnMetaFasc = fileMetaIndicePrecVers.getFasMetaVerAipFascicolo().getDsUrnMetaFascicolo();

                    /* Definisco la folder relativa al sistema di conservazione */
                    String folder = it.eng.parer.async.utils.IOUtils.getPath("pindexsource",
                            StringUtils.capitalize(Constants.SACER.toLowerCase()),
                            it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);
                    /* Definisco il nome e l'estensione del file */
                    fileName = it.eng.parer.async.utils.IOUtils.getFilename(
                            it.eng.parer.async.utils.IOUtils.extractPartUrnName(urnMetaFasc, true),
                            it.eng.parer.async.utils.IOUtils.CONTENT_TYPE.XML.getFileExt());
                    /*
                     * Definisco il percorso relativo del file rispetto alla posizione dell'indice di conservazione
                     */
                    String pathPIndexSource = it.eng.parer.async.utils.IOUtils.getAbsolutePath(folder, fileName,
                            it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);

                    ZipArchiveEntry zae = new ZipArchiveEntry(pathPIndexSource);
                    filterZipEntry(zae);
                    zipOutputStream.putArchiveEntry(zae);
                    zipOutputStream
                            .write(fileMetaIndicePrecVers.getBlFileVerIndiceAip().getBytes(StandardCharsets.UTF_8));
                    closeEntry = true;
                }
            }
            if (closeEntry) {
                getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + fileNameZip + ".zip");
                zipOutputStream.closeArchiveEntry();
            }
        }
    }

    private static final long DEFAULT_ZIP_TIMESTAMP = LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0)
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

    private ZipArchiveEntry filterZipEntry(ZipArchiveEntry entry) {
        // Set times
        entry.setCreationTime(FileTime.fromMillis(DEFAULT_ZIP_TIMESTAMP));
        entry.setLastAccessTime(FileTime.fromMillis(DEFAULT_ZIP_TIMESTAMP));
        entry.setLastModifiedTime(FileTime.fromMillis(DEFAULT_ZIP_TIMESTAMP));
        entry.setTime(DEFAULT_ZIP_TIMESTAMP);
        // Remove extended timestamps
        for (ZipExtraField field : entry.getExtraFields()) {
            if (field instanceof X5455_ExtendedTimestamp) {
                entry.removeExtraField(field.getHeaderId());
            }
        }
        return entry;
    }

}
