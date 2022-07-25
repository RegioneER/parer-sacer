package it.eng.parer.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.ejb.EJB;
import javax.naming.NamingException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc.ejb.FormatoFileDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneRegistro.ejb.RegistroEjb;
import it.eng.parer.amministrazioneStrutture.gestioneSottoStrutture.ejb.SottoStruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoDoc.ejb.TipoDocumentoEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoRappresentazione.ejb.TipoRappresentazioneEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoStrutturaDoc.ejb.TipoStrutturaDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb.TipoUnitaDocEjb;
import it.eng.parer.firma.xml.VFTipoControlloType;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.ComponentiAbstractAction;
import it.eng.parer.slite.gen.form.ComponentiForm.RicComponentiFiltri;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoCompDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoRapprCompTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.OrgSubStrutTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisCertifCaFirmaCompTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisCertifCaMarcaCompTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisControfirmaFirmaTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisDatiSpecTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisFirmaCompRowBean;
import it.eng.parer.slite.gen.viewbean.AroVLisFirmaCompTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisMarcaCompRowBean;
import it.eng.parer.slite.gen.viewbean.AroVLisMarcaCompTableBean;
import it.eng.parer.slite.gen.viewbean.AroVRicCompTableBean;
import it.eng.parer.slite.gen.viewbean.AroVRicUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVVisCompRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisCompVolRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisFirmaCompRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisMarcaCompRowBean;
import it.eng.parer.volume.utils.VolumeEnums;
import it.eng.parer.web.helper.ComponentiHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Constants.TipoEntitaSacer;
import it.eng.parer.web.util.DownloadDip;
import it.eng.parer.web.util.RecuperoWeb;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.web.validator.UnitaDocumentarieValidator;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.parer.ws.recupero.dto.ParametriRecupero;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.recuperoDip.ejb.RecuperoDip;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.versamento.dto.ComponenteVers;
import it.eng.parer.ws.xml.versReqStato.ChiaveType;
import it.eng.parer.ws.xml.versReqStato.Recupero;
import it.eng.parer.ws.xml.versReqStato.VersatoreType;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.security.Secure;

/**
 *
 * @author Gilioli_P
 */
public class ComponentiAction extends ComponentiAbstractAction {

    private static Logger logger = LoggerFactory.getLogger(ComponentiAction.class.getName());
    @EJB(mappedName = "java:app/Parer-ejb/ComponentiHelper")
    private ComponentiHelper componentiHelper;
    @EJB(mappedName = "java:app/Parer-ejb/UnitaDocumentarieHelper")
    private UnitaDocumentarieHelper udHelper;
    @EJB(mappedName = "java:app/Parer-ejb/RecuperoDip")
    private RecuperoDip recuperoDip;
    @EJB(mappedName = "java:app/Parer-ejb/TipoUnitaDocEjb")
    private TipoUnitaDocEjb tipoUnitaDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/RegistroEjb")
    private RegistroEjb registroEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoDocumentoEjb")
    private TipoDocumentoEjb tipoDocumentoEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoStrutturaDocEjb")
    private TipoStrutturaDocEjb tipoStrutDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/FormatoFileDocEjb")
    private FormatoFileDocEjb formatoFileDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoRappresentazioneEjb")
    private TipoRappresentazioneEjb tipoRapprEjb;
    @EJB(mappedName = "java:app/Parer-ejb/SottoStruttureEjb")
    private SottoStruttureEjb subStrutEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;

    private BigDecimal getIdStrut() {
        return getUser().getIdOrganizzazioneFoglia();
    }

    /**
     * Metodo di inizializzazione form di ricerca componenti
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    @Secure(action = "Menu.UnitaDocumentarie.Componenti")
    public void initOnClick() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.UnitaDocumentarie.Componenti");

        // Pulisco i filtri della form di ricerca
        getForm().getRicComponentiFiltri().reset();

        getSession().removeAttribute(WebConstants.DOWNLOAD_DIP.DIP_RISPOSTA_WS.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_DIP.DIP_RECUPERO_EXT.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_DIP.DIP_ENTITA.name());

        // Imposto i valori della combo TIPO STRUTTURA DOCUMENTO ricavati dalla tabella
        // DEC_TIPO_STRUT_DOC
        DecTipoStrutDocTableBean tmpTableBeanTipoStrutDoc = tipoStrutDocEjb.getDecTipoStrutDocTableBean(getIdStrut(),
                false);
        DecodeMap mappaTipoStrutDoc = new DecodeMap();
        mappaTipoStrutDoc.populatedMap(tmpTableBeanTipoStrutDoc, "id_tipo_strut_doc", "nm_tipo_strut_doc");

        // Imposto i valori della combo FORMATO_FILE_DOC ricavati dalla tabella
        // DEC_FORMATO_FILE_DOC
        DecFormatoFileDocTableBean tmpTableBeanFormatoFileDoc = formatoFileDocEjb
                .getDecFormatoFileDocTableBean(getIdStrut());
        DecodeMap mappaFormatoFileDoc = new DecodeMap();
        mappaFormatoFileDoc.populatedMap(tmpTableBeanFormatoFileDoc, "nm_formato_file_doc", "nm_formato_file_doc");

        // Setto i valori della combo TIPO REGISTRO ricavati dalla tabella
        // DEC_REGISTRO_UNITA_DOC
        DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb.getRegistriUnitaDocAbilitati(getUser().getIdUtente(),
                getIdStrut());
        DecodeMap mappaRegistro = new DecodeMap();
        mappaRegistro.populatedMap(tmpTableBeanReg, "cd_registro_unita_doc", "cd_registro_unita_doc");

        DecTipoRapprCompTableBean tmpRapprTableBean = tipoRapprEjb.getDecTipoRapprCompTableBean(getIdStrut(), false);
        DecodeMap mappaRappr = DecodeMap.Factory.newInstance(tmpRapprTableBean, "nm_tipo_rappr_comp",
                "nm_tipo_rappr_comp");

        // Setto i valori della combo SOTTO STRUTTURE ricavati dalla tabella
        // ORG_SUB_STRUT
        OrgSubStrutTableBean tmpSubStrutsTableBean = subStrutEjb
                .getOrgSubStrutTableBeanAbilitate(getUser().getIdUtente(), getIdStrut());
        DecodeMap mappaSubStruts = DecodeMap.Factory.newInstance(tmpSubStrutsTableBean, "id_sub_strut", "nm_sub_strut");
        getForm().getRicComponentiFiltri().getNm_sub_strut().setDecodeMap(mappaSubStruts);
        // Precompilo la mappa con tutti i valori
        Iterator it = mappaSubStruts.keySet().iterator();
        String[] chiavi = new String[mappaSubStruts.keySet().size()];
        int i = 0;
        while (it.hasNext()) {
            BigDecimal chiave = (BigDecimal) it.next();
            chiavi[i] = "" + chiave;
            i++;
        }

        // Imposto le mappe di valori precedentemente creati sulle combo dei filtri
        getForm().getRicComponentiFiltri().getNm_tipo_strut_doc().setDecodeMap(mappaTipoStrutDoc);
        getForm().getRicComponentiFiltri().getNm_formato_file_vers().setDecodeMap(mappaFormatoFileDoc);
        getForm().getRicComponentiFiltri().getTi_esito_contr_conforme().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("stato", VolumeEnums.ControlloConformitaEnum.values()));
        getForm().getRicComponentiFiltri().getTi_esito_verif_firme().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("ti_esito_verif_firme", VolumeEnums.StatoVerifica.values()));
        // getForm().getRicComponentiFiltri().getTi_esito_verif_firme_chius().setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_esito_verif_firme_chius",
        // VolumeEnums.StatoVerifica.getComboEsitoVerifFirmeChius()));
        getForm().getRicComponentiFiltri().getFl_comp_firmato().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getRicComponentiFiltri().getCd_registro_key_unita_doc().setDecodeMap(mappaRegistro);
        getForm().getRicComponentiFiltri().getDs_algo_hash_file_calc()
                .setDecodeMap(ComboGetter.getMappaHashAlgorithm());
        getForm().getRicComponentiFiltri().getCd_encoding_hash_file_calc()
                .setDecodeMap(ComboGetter.getMappaHashEncoding());
        getForm().getRicComponentiFiltri().getFl_forza_accettazione()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getRicComponentiFiltri().getFl_forza_conservazione()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getRicComponentiFiltri().getTi_esito_contr_formato_file().setDecodeMap(ComboGetter
                .getMappaSortedGenericEnum("ti_esito_verif_formato_vers", VolumeEnums.StatoFormatoVersamento.values()));
        getForm().getRicComponentiFiltri().getTi_supporto_comp().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("ti_supporto_comp", ComponenteVers.TipiSupporto.values()));
        getForm().getRicComponentiFiltri().getFl_rif_temp_vers().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getRicComponentiFiltri().getFl_hash_vers().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getRicComponentiFiltri().getNm_tipo_rappr_comp().setDecodeMap(mappaRappr);
        // getForm().getRicComponentiFiltri().getFl_doc_annul().setValue("0");
        getForm().getRicComponentiFiltri().getNm_sub_strut().setValues(chiavi);

        // if (mappaSubStruts.keySet().size() == 1) {
        // BigDecimal singleKey = (BigDecimal)
        // mappaSubStruts.keySet().iterator().next();
        // getForm().getRicComponentiFiltri().getNm_sub_strut().setValues(new
        // String[]{String.valueOf(singleKey.longValue())});
        // }
        getForm().getComponentiList().setTable(null);

        // getForm().getRicComponentiFiltri().getFl_doc_annul().setValue("0");
        // Carico la pagina di ricerca
        forwardToPublisher(Application.Publisher.COMPONENTI_RICERCA);
        // Imposto i filtri in edit mode
        getForm().getRicComponentiFiltri().setEditMode();
    }

    /**
     * Metodo scatenato al click del bottone di ricerca all'interno della pagina di ricerca componenti
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void ricerca() throws EMFError {
        // Inizializzo la lista fittizia nel caso si voglia visualizzare unità
        // documentaria
        getForm().getUnitaDocumentariaList().setTable(new AroVRicUnitaDocTableBean());
        RicComponentiFiltri compfiltri = getForm().getRicComponentiFiltri();
        // Esegue la post dei filtri compilati
        compfiltri.post(getRequest());
        // Valida i filtri per verificare quelli obbligatori
        if (compfiltri.validate(getMessageBox())) {
            UnitaDocumentarieValidator validator = new UnitaDocumentarieValidator(getMessageBox());
            // Valida in maniera più specifica i dati
            Date[] dateAcquisizioneValidate = validator.validaDate(compfiltri.getDt_creazione_da().parse(),
                    compfiltri.getOre_dt_creazione_da().parse(), compfiltri.getMinuti_dt_creazione_da().parse(),
                    compfiltri.getDt_creazione_a().parse(), compfiltri.getOre_dt_creazione_a().parse(),
                    compfiltri.getMinuti_dt_creazione_a().parse(), compfiltri.getDt_creazione_da().getHtmlDescription(),
                    compfiltri.getDt_creazione_a().getHtmlDescription());

            validator.validaOrdineDateOrari(compfiltri.getDt_scad_firma_comp_da().parse(),
                    compfiltri.getDt_scad_firma_comp_a().parse(),
                    compfiltri.getDt_scad_firma_comp_da().getHtmlDescription(),
                    compfiltri.getDt_scad_firma_comp_a().getHtmlDescription());
            validator.validaDimensioniKb(compfiltri.getNi_size_file_da().parse(),
                    compfiltri.getNi_size_file_a().parse());
            validator.controllaPresenzaAnno(compfiltri.getAa_key_unita_doc().parse(),
                    compfiltri.getAa_key_unita_doc_da().parse(), compfiltri.getAa_key_unita_doc_a().parse());
            Object[] chiavi = null;
            if (!getMessageBox().hasError()) {
                // Valida i campi di Range di chiavi unità documentaria
                chiavi = validator.validaChiaviUnitaDoc(compfiltri.getCd_registro_key_unita_doc().getValue(),
                        compfiltri.getAa_key_unita_doc().parse(), compfiltri.getCd_key_unita_doc().parse(),
                        compfiltri.getAa_key_unita_doc_da().parse(), compfiltri.getAa_key_unita_doc_a().parse(),
                        compfiltri.getCd_key_unita_doc_da().parse(), compfiltri.getCd_key_unita_doc_a().parse());
            }

            /* Gestione dei tipi dato soggetti alle abilitazioni */
            DecTipoUnitaDocTableBean tmpTableBeanTipoUD = tipoUnitaDocEjb
                    .getTipiUnitaDocAbilitati(getUser().getIdUtente(), getIdStrut());
            DecTipoDocTableBean tmpTableBeanTipoDoc = tipoDocumentoEjb.getTipiDocAbilitati(getUser().getIdUtente(),
                    getIdStrut());

            if (!getMessageBox().hasError()) {
                // La validazione non ha riportato errori.
                if (chiavi != null && chiavi.length == 5) {
                    compfiltri.getAa_key_unita_doc_da()
                            .setValue(chiavi[1] != null ? ((BigDecimal) chiavi[1]).toString() : null);
                    compfiltri.getAa_key_unita_doc_a()
                            .setValue(chiavi[2] != null ? ((BigDecimal) chiavi[2]).toString() : null);
                    compfiltri.getCd_key_unita_doc_da().setValue(chiavi[3] != null ? (String) chiavi[3] : null);
                    compfiltri.getCd_key_unita_doc_a().setValue(chiavi[4] != null ? (String) chiavi[4] : null);
                }
                // La validazione non ha riportato errori. carico la tabella con i filtri
                // impostati
                String maxResultRicercaComp = configurationHelper.getValoreParamApplic("MAX_RESULT_RICERCA_COMP", null,
                        null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC);
                AroVRicCompTableBean componentiTableBean = componentiHelper.getAroVRicCompViewBean(getIdStrut(),
                        compfiltri, dateAcquisizioneValidate, tmpTableBeanTipoUD, tmpTableBeanTipoDoc,
                        Integer.parseInt(maxResultRicercaComp));
                getForm().getComponentiList().setTable(componentiTableBean);
                getForm().getComponentiList().getTable().setPageSize(10);
                // Imposto la lista come non modificabile
                getForm().getComponentiList().setUserOperations(true, false, false, false);
                // Workaround in modo che la lista punti al primo record, non all'ultimo
                getForm().getComponentiList().getTable().first();
            }
        }
        // Workaround per evitare che il trigger scarichi la pagina HTML anziché
        // visualizzarla sul browser
        forwardToPublisher(Application.Publisher.COMPONENTI_RICERCA);
    }

    /**
     * Metodo invocato dal bottone omonimo dei filtri ricerca unità documentarie per ripulire i filtri
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void pulisci() throws EMFError {
        this.initOnClick();
    }

    /**
     * Metodo invocato sul bottone di dettaglio/modifica di una riga della lista componenti, esegue il caricamento dei
     * dati della riga selezionata per visualizzare il dettaglio
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void loadDettaglio() throws EMFError {
        // Controllo per quale tabella è stato invocato il metodo
        if (getRequest().getParameter("table").equals(getForm().getComponentiList().getName())) {
            // LISTA COMPONENTI
            // Ottengo l'id componente
            BigDecimal idComp = getForm().getComponentiList().getTable().getCurrentRow().getBigDecimal("id_comp_doc");
            getSession().setAttribute("idComp", idComp);
            // Carico il rowbean corrispondente all'id ottenuto
            AroVVisCompRowBean componenteRB = componentiHelper.getAroVVisCompRowBean(idComp);

            // Copio nella form di dettaglio i dati del rowbean
            getForm().getComponentiDetail().copyFromBean(componenteRB);
            // Copia la stessa urn doc impostata nel form precedente
            getForm().getComponentiDetail().getUrn_doc().setValue((String) getSession().getAttribute("UD_URN_DOC"));
            if (componenteRB.getTiSupportoComp().equals("FILE")) {
                getForm().getComponentiDetail().getScarica_comp_file().setEditMode();
                getForm().getComponentiDetail().getScarica_comp_file().setDisableHourGlass(true);
            }
            // Se ti_stato_doc != null, inserisco le informazioni della vista AroVVisCompVol
            AroVVisCompVolRowBean compVolRB = componentiHelper.getAroVVisCompVolRowBean(idComp);
            if (componenteRB.getTiStatoDoc() != null && compVolRB.getIdCompDoc() != null) {
                getForm().getComponentiDetail().getId_volume_conserv()
                        .setValue(compVolRB.getIdVolumeConserv().toString());
                getForm().getComponentiDetail().getTi_stato_volume_conserv()
                        .setValue(compVolRB.getTiStatoVolumeConserv());
                getForm().getComponentiDetail().getDt_chius_volume()
                        .setValue(compVolRB.getDtChiusVolume() != null ? compVolRB.getDtChiusVolume().toString() : "");
                getForm().getComponentiDetail().getTi_esito_verif_firme_chius()
                        .setValue(compVolRB.getTiEsitoVerifFirmeChius());
                getForm().getComponentiDetail().getDs_esito_verif_firme_chius()
                        .setValue(compVolRB.getDsEsitoVerifFirmeChius());
            }

            // Se è diverso da documento principale, aggiungo il progressivo
            if (!componenteRB.getTiDoc().equals("PRINCIPALE")) {
                componenteRB.setTiDoc(componenteRB.getTiDoc() + " " + componenteRB.getPgDoc());
            }
            // Inverto la visualizzazione del valore dei flag, avendo,
            // come riporta la label, il significato contrario
            if (getForm().getComponentiDetail().getFl_no_calc_fmt_verif_firme().isChecked()) {
                getForm().getComponentiDetail().getFl_no_calc_fmt_verif_firme().setChecked(false);
            } else {
                getForm().getComponentiDetail().getFl_no_calc_fmt_verif_firme().setChecked(true);
            }

            if (getForm().getComponentiDetail().getFl_no_calc_hash_file().isChecked()) {
                getForm().getComponentiDetail().getFl_no_calc_hash_file().setChecked(false);
            } else {
                getForm().getComponentiDetail().getFl_no_calc_hash_file().setChecked(true);
            }

            // Se il componente è in realtà un sottocomponente, nascondo il campo relativo
            // al Formato del comp. sbustato
            getForm().getComponentiDetail().getNm_formato_calc().setHidden(false);
            if (componenteRB.getNiOrdCompPadre() != null) {
                getForm().getComponentiDetail().getNm_formato_calc().setHidden(true);
            }
            // // Se Tipo Supporto = 'RIFERIMENTO', visualizzo Registro, Anno e Numero
            // getForm().getComponentiDetail().getCd_registro_key_unita_doc_rif().setHidden(true);
            // getForm().getComponentiDetail().getAa_key_unita_doc_rif().setHidden(true);
            // getForm().getComponentiDetail().getCd_key_unita_doc_rif().setHidden(true);
            // getSession().setAttribute("visualizzaLabel", false);
            // if (componenteRB.getTiSupportoComp() != null &&
            // componenteRB.getTiSupportoComp().equals("RIFERIMENTO")) {
            // getForm().getComponentiDetail().getCd_registro_key_unita_doc_rif().setHidden(false);
            // getForm().getComponentiDetail().getAa_key_unita_doc_rif().setHidden(false);
            // getForm().getComponentiDetail().getCd_key_unita_doc_rif().setHidden(false);
            // getSession().setAttribute("visualizzaLabel", true);
            // }

            Calendar cal = Calendar.getInstance();
            cal.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date dtAnnulDoc = new Date(componenteRB.getDtAnnulDoc().getTime());
            Date dtAnnulUd = new Date(componenteRB.getDtAnnulUnitaDoc().getTime());
            if (dtAnnulUd.compareTo(cal.getTime()) == 0) {
                getForm().getVersamentoAnnullatoUDSection().setHidden(true);
                getForm().getComponentiDetail().getDt_annul_unita_doc().setValue(null);
            } else {
                getForm().getVersamentoAnnullatoUDSection().setHidden(false);
            }

            getForm().getVersamentoAnnullatoDocSection().setHidden(true);
            // Se il documento è valido
            if (dtAnnulDoc.compareTo(cal.getTime()) == 0) {
                getForm().getVersamentoAnnullatoDocSection().setHidden(true);
                getForm().getComponentiDetail().getDt_annul_doc().setValue(null);
            } else /*
                    * Se il documento è annullato e l'unità doc è valida oppure l'unità documentaria è annullata e la
                    * data è diversa da quella di annullamento del documento
                    */ if (dtAnnulUd.compareTo(cal.getTime()) == 0 || dtAnnulUd.compareTo(dtAnnulDoc) != 0) {
                getForm().getVersamentoAnnullatoDocSection().setHidden(false);
            }

            // Hash "personalizzato"
            if (componenteRB.getDsAlgoHashFileCalc() != null) {
                String descrizione = "Hash " + componenteRB.getDsAlgoHashFileCalc() + " ("
                        + componenteRB.getCdEncodingHashFileCalc() + ")";
                String valore = componenteRB.getDsHashFileCalc();
                getForm().getComponentiDetail().getHash_calc_personalizzato().setHidden(false);
                getForm().getComponentiDetail().getHash_calc_personalizzato().setDescription(descrizione);
                getForm().getComponentiDetail().getHash_calc_personalizzato().setValue(valore);
            } else {
                getForm().getComponentiDetail().getHash_calc_personalizzato().setHidden(true);
            }

            if (componenteRB.getDsAlgoHashFile_256() != null) {
                String descrizione = "Hash " + componenteRB.getDsAlgoHashFile_256() + " ("
                        + componenteRB.getCdEncodingHashFile_256() + ")";
                String valore = componenteRB.getDsHashFile_256();
                getForm().getComponentiDetail().getHash_personalizzato().setHidden(false);
                getForm().getComponentiDetail().getHash_personalizzato().setDescription(descrizione);
                getForm().getComponentiDetail().getHash_personalizzato().setValue(valore);
            } else {
                getForm().getComponentiDetail().getHash_personalizzato().setHidden(true);
            }

            // Informazioni sull'archiviazione
            showHideTipoArchiviazione(componenteRB.getTipoArchiviazione());

            // Trasformatore
            if (componenteRB.getTiSupportoComp().equals("RIFERIMENTO")) {
                String trasformatore = componenteRB.getCdRegistroKeyUnitaDocRif() + " - "
                        + componenteRB.getAaKeyUnitaDocRif() + " - " + componenteRB.getCdKeyUnitaDocRif();
                getForm().getComponentiDetail().getTrasformatore().setHidden(false);
                getForm().getComponentiDetail().getTrasformatore().setValue(trasformatore);
            } else {
                getForm().getComponentiDetail().getTrasformatore().setHidden(true);
            }

            String maxResultStandard = configurationHelper.getValoreParamApplic("MAX_RESULT_STANDARD", null, null, null,
                    null, CostantiDB.TipoAplVGetValAppart.APPLIC);
            // Carico la lista firme
            AroVLisFirmaCompTableBean listFirmaCompTB = componentiHelper.getAroVLisFirmaCompTableBean(idComp,
                    Integer.parseInt(maxResultStandard));
            getForm().getFirmeList().setTable(listFirmaCompTB);
            getForm().getFirmeList().getTable().setPageSize(10);
            getForm().getFirmeList().getTable().first();
            // Carico la lista marche
            AroVLisMarcaCompTableBean listMarcaCompTB = componentiHelper.getAroVLisMarcaCompTableBean(idComp,
                    Integer.parseInt(maxResultStandard));
            getForm().getMarcheList().setTable(listMarcaCompTB);
            getForm().getMarcheList().getTable().setPageSize(10);
            getForm().getMarcheList().getTable().first();
            // Carico la lista dei dati specifici
            AroVLisDatiSpecTableBean listDatiSpecTB = udHelper.getAroVLisDatiSpecTableBean(idComp, TipoEntitaSacer.COMP,
                    Constants.TI_USO_XSD_VERS, Integer.parseInt(maxResultStandard));
            getForm().getDatiSpecificiCompList().setTable(listDatiSpecTB);
            getForm().getDatiSpecificiCompList().getTable().setPageSize(10);
            getForm().getDatiSpecificiCompList().getTable().first();
            // Carico la lista dei dati specifici di migrazione
            AroVLisDatiSpecTableBean listDatiSpecMigrazioneTB = udHelper.getAroVLisDatiSpecTableBean(idComp,
                    TipoEntitaSacer.COMP, Constants.TI_USO_XSD_MIGR, Integer.parseInt(maxResultStandard));
            getForm().getDatiSpecificiMigrazioneCompList().setTable(listDatiSpecMigrazioneTB);
            getForm().getDatiSpecificiMigrazioneCompList().getTable().setPageSize(10);
            getForm().getDatiSpecificiMigrazioneCompList().getTable().first();
            // Setto la versione XSD per dati specifici standard e di migrazione
            if (listDatiSpecTB != null && listDatiSpecTB.size() > 0
                    && listDatiSpecTB.getRow(0).getCdVersioneXsd() != null) {
                getForm().getComponentiDetail().getVersione_xsd_dati_spec_comp()
                        .setValue(listDatiSpecTB.getRow(0).getCdVersioneXsd());
            }
            if (listDatiSpecMigrazioneTB != null && listDatiSpecMigrazioneTB.size() > 0
                    && listDatiSpecMigrazioneTB.getRow(0).getCdVersioneXsd() != null) {
                getForm().getComponentiDetail().getVersione_xsd_dati_spec_migr_comp()
                        .setValue(listDatiSpecMigrazioneTB.getRow(0).getCdVersioneXsd());
            }

            RispostaWSRecupero rispostaWs = new RispostaWSRecupero();
            RecuperoExt myRecuperoExt = new RecuperoExt();
            myRecuperoExt.setParametriRecupero(new ParametriRecupero());
            // verifica se l'unità documentaria richiesta contiene file convertibili
            myRecuperoExt.getParametriRecupero().setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.COMP_DIP);
            myRecuperoExt.getParametriRecupero().setUtente(getUser());
            myRecuperoExt.getParametriRecupero().setIdUnitaDoc(componenteRB.getIdUnitaDoc().longValue());
            myRecuperoExt.getParametriRecupero().setIdComponente(idComp.longValue());
            recuperoDip.contaComponenti(rispostaWs, myRecuperoExt);
            getForm().getComponentiDetail().getScarica_dip_comp().setEditMode();
            if (rispostaWs.getSeverity() == SeverityEnum.OK
                    && rispostaWs.getDatiRecuperoDip().getNumeroElementiTrovati() > 0) {
                getForm().getComponentiDetail().getScarica_dip_comp().setHidden(false);
            } else {
                getForm().getComponentiDetail().getScarica_dip_comp().setHidden(true);
            }
            // Imposto visibile il bottone per scaricare i files DIP per esibizione
            getForm().getComponentiDetail().getScarica_dip_esibizione_comp_doc().setEditMode();

            // Imposto visibile il bottone per scaricare il report di verifica firma
            if (StringUtils.isNotBlank(componenteRB.getFlCompFirmato())
                    && componenteRB.getFlCompFirmato().equalsIgnoreCase(CostantiDB.Flag.TRUE)) {
                getForm().getComponentiDetail().getScarica_report_firma().setEditMode();
            } else {
                getForm().getComponentiDetail().getScarica_report_firma().setHidden(true);
            }
        } else if (getRequest().getParameter("table").equals(getForm().getMarcheList().getName())) {
            // LISTA MARCHE
            // Ottengo l'id marca
            BigDecimal idMarca = ((AroVLisMarcaCompRowBean) getForm().getMarcheList().getTable().getCurrentRow())
                    .getIdMarcaComp();
            getSession().setAttribute("idMarca", idMarca);
            // Carico il rowbean corrispondente all'id ottenuto
            AroVVisMarcaCompRowBean marcaRB = componentiHelper.getAroVVisMarcaCompRowBean(idMarca);
            // Copio nella form di dettaglio i dati del rowbean
            getForm().getMarcheUnitaDocumentarieDetail().copyFromBean(marcaRB);
            // Carico la lista certificati CA CATENA_TRUSTED
            AroVLisCertifCaMarcaCompTableBean listCertifCaMarcaCompTB = componentiHelper
                    .getAroVLisCertifCaMarcaCompTableBean(idMarca, VFTipoControlloType.CATENA_TRUSTED.name());
            // Carico la lista certificati
            getForm().getCertificatiCAList().setTable(listCertifCaMarcaCompTB);
            getForm().getCertificatiCAList().getTable().setPageSize(10);
            getForm().getCertificatiCAList().getTable().first();
            // Carico la lista certificati CA OCSP
            AroVLisCertifCaMarcaCompTableBean listCertifCaOcspMarcaCompTB = componentiHelper
                    .getAroVLisCertifCaMarcaCompTableBean(idMarca, VFTipoControlloType.OCSP.name());
            // Carico la lista certificati
            getForm().getCertificatiCAOCSPList().setTable(listCertifCaOcspMarcaCompTB);
            getForm().getCertificatiCAOCSPList().getTable().setPageSize(10);
            getForm().getCertificatiCAOCSPList().getTable().first();
        } else if (getRequest().getParameter("table").equals(getForm().getFirmeList().getName())) {
            // LISTA FIRME
            // Ottengo l'id firma
            BigDecimal idFirma = ((AroVLisFirmaCompRowBean) getForm().getFirmeList().getTable().getCurrentRow())
                    .getIdFirmaComp();
            getSession().setAttribute("idFirma", idFirma);
            // Carico il rowbean corrispondente all'id ottenuto
            AroVVisFirmaCompRowBean firmaRB = componentiHelper.getAroVVisFirmaCompRowBean(idFirma);
            // modifico il campo Tipo rif. temporale usato del rowbean concatenandogli
            // un'altra stringa (di descrizione)
            // prima di presentarlo a video

            if (firmaRB.getTiRifTempUsato() != null) {
                if (firmaRB.getTiRifTempUsato().equals("DATA_FIRMA")) {
                    firmaRB.setTiRifTempUsato(firmaRB.getTiRifTempUsato() + " (Data della firma)");
                } else if (firmaRB.getTiRifTempUsato().equals("DATA_VERS")) {
                    firmaRB.setTiRifTempUsato(firmaRB.getTiRifTempUsato() + " (Data di versamento)");
                } else if (firmaRB.getTiRifTempUsato().equals("MT_VERS_NORMA")) {
                    firmaRB.setTiRifTempUsato(firmaRB.getTiRifTempUsato() + " (Marca temporale versata, a norma)");
                } else if (firmaRB.getTiRifTempUsato().equals("MT_VERS_SEMPLICE")) {
                    firmaRB.setTiRifTempUsato(firmaRB.getTiRifTempUsato() + " (Marca temporale versata, semplice)");
                } else if (firmaRB.getTiRifTempUsato().equals("RIF_TEMP_VERS")) {
                    String rif = componentiHelper
                            .getAroCompDocRowBean((BigDecimal) getSession().getAttribute("idComp"), getIdStrut())
                            .getDsRifTempVers();
                    if (rif != null) {
                        firmaRB.setTiRifTempUsato(
                                firmaRB.getTiRifTempUsato() + " (Riferimento temporale versato: " + rif + ")");
                    } else {
                        firmaRB.setTiRifTempUsato(firmaRB.getTiRifTempUsato() + " (Riferimento temporale versato)");
                    }
                }
            }

            // Copio nella form di dettaglio i dati del rowbean
            getForm().getFirmeUnitaDocumentarieDetail().copyFromBean(firmaRB);

            // Carico la lista certificati CA CATENA_TRUSTED
            AroVLisCertifCaFirmaCompTableBean listCertifCaFirmaCompTB = componentiHelper
                    .getAroVLisCertifCaFirmaCompTableBean(idFirma, VFTipoControlloType.CATENA_TRUSTED.name());
            getForm().getCertificatiCAList().setTable(listCertifCaFirmaCompTB);
            getForm().getCertificatiCAList().getTable().setPageSize(10);
            getForm().getCertificatiCAList().getTable().first();
            // Carico la lista certificati CA OCSP
            AroVLisCertifCaFirmaCompTableBean listCertifCaOcspFirmaCompTB = componentiHelper
                    .getAroVLisCertifCaFirmaCompTableBean(idFirma, VFTipoControlloType.OCSP.name());
            getForm().getCertificatiCAOCSPList().setTable(listCertifCaOcspFirmaCompTB);
            getForm().getCertificatiCAOCSPList().getTable().setPageSize(10);
            getForm().getCertificatiCAOCSPList().getTable().first();
            // Carico la lista contro firmatari
            AroVLisControfirmaFirmaTableBean listControfirmatariFirmaCompTB = componentiHelper
                    .getAroVLisControfirmaFirmaTableBean(idFirma);
            getForm().getControfirmatariList().setTable(listControfirmatariFirmaCompTB);
            getForm().getControfirmatariList().getTable().setPageSize(10);
            getForm().getControfirmatariList().getTable().first();
        }
    }

    private void showHideTipoArchiviazione(String tipoArchiviazione) {
        boolean hideObjectStorage = true;
        boolean hideNastro = true;
        boolean hideOracle = true;
        if (tipoArchiviazione.equals("OBJECT_STORAGE")) {
            hideObjectStorage = false;
        } else if (tipoArchiviazione.equals("NASTRO")) {
            hideNastro = false;
        } else if (tipoArchiviazione.equals("ORACLE")) {
            hideOracle = false;
        }

        getForm().getComponentiDetail().getNm_tenant().setHidden(hideObjectStorage);
        getForm().getComponentiDetail().getNm_bucket().setHidden(hideObjectStorage);
        getForm().getComponentiDetail().getCd_key_file().setHidden(hideObjectStorage);
        getForm().getComponentiDetail().getTi_stato_dt_vers().setHidden(hideNastro);
        getForm().getComponentiDetail().getDs_nome_file_ark().setHidden(hideNastro);
        getForm().getComponentiDetail().getCd_sub_partition().setHidden(hideOracle);
        getForm().getComponentiDetail().getId_file_oracle().setHidden(hideOracle);
    }

    @Override
    public void undoDettaglio() throws EMFError {
    }

    @Override
    public void insertDettaglio() throws EMFError {
    }

    @Override
    public void saveDettaglio() throws EMFError {
    }

    /**
     * Metodo utilizzato dal framework quando clicco sul tasto "Indietro" nella barra di scorrimento del dettaglio di un
     * record
     * 
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.COMPONENTI_RICERCA;
    }

    @Override
    public String getControllerName() {
        return Application.Actions.COMPONENTI;
    }

    /**
     * Trigger invocato alla modifica della combo NM_TIPO_STRUT_DOC per caricare i dati sulla combo NM_TIPO_COMP_DOC
     *
     * @return I filtri della form come JSONObject
     * 
     * @throws EMFError
     *             errore generico
     */
    @Override
    public JSONObject triggerRicComponentiFiltriNm_tipo_strut_docOnTrigger() throws EMFError {
        RicComponentiFiltri cf = getForm().getRicComponentiFiltri();
        // Eseguo la post dei filtri
        cf.post(getRequest());
        BigDecimal idStrutDoc = cf.getNm_tipo_strut_doc().parse();

        DecodeMap mappaTipoCompDoc = new DecodeMap();
        if (idStrutDoc != null) {
            // Setto i valori della combo TIPO COMPONENTE DOCUMENTO ricavati dalla tabella
            // DEC_TIPO_STRUT_DOC
            DecTipoCompDocTableBean tmpTableBeanTipoCompDoc = tipoStrutDocEjb.getDecTipoCompDocTableBean(idStrutDoc,
                    false);
            mappaTipoCompDoc.populatedMap(tmpTableBeanTipoCompDoc, "id_tipo_comp_doc", "nm_tipo_comp_doc");
        }
        // Setto i dati ricavati dalla query nella combo NM_TIPO_COMP_DOC
        getForm().getRicComponentiFiltri().getNm_tipo_comp_doc().setDecodeMap(mappaTipoCompDoc);
        return getForm().getRicComponentiFiltri().asJSON();
    }

    /**
     * Metodo invocato sul bottone di dettaglio/modifica di una riga della lista componenti, esegue il caricamento della
     * pagina per visualizzare il dettaglio, in seguito alla loadDettaglio
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void dettaglioOnClick() throws EMFError {
        // Controllo per quale tabella è stato invocato il metodo
        if (getRequest().getParameter("table").equals(getForm().getComponentiList().getName())) {
            // LISTA COMPONENTI
            getForm().getComponentiDettaglioTabs()
                    .setCurrentTab(getForm().getComponentiDettaglioTabs().getInfoPrincipaliComp());
            getForm().getComponentiDettaglioListsTabs()
                    .setCurrentTab(getForm().getComponentiDettaglioListsTabs().getListaFirmeComp());
            // apri le sezioni
            getForm().getDocumento().setLoadOpened(true);
            getForm().getProfiloDoc().setLoadOpened(false);
            getForm().getProfiloComponente().setLoadOpened(true);
            // visualizzo i bottoni
            getForm().getComponentiDetail().getDettaglio_ud().setEditMode();
            getForm().getComponentiDetail().getDettaglio_doc().setEditMode();
            forwardToPublisher(Application.Publisher.COMPONENTI_DETAIL);
        } else if (getRequest().getParameter("table").equals(getForm().getFirmeList().getName())) {
            // LISTA FIRME
            getForm().getFirmeDettaglioTabs().setCurrentTab(getForm().getFirmeDettaglioTabs().getInfoPrincipaliFirme());
            getForm().getFirmeDettaglioListsTabs()
                    .setCurrentTab(getForm().getFirmeDettaglioListsTabs().getListaCertificatiCAFirme());
            // apri le sezioni
            getForm().getDocumento().setLoadOpened(true);
            getForm().getComponente().setLoadOpened(true);
            // Visualizzo le section a seconda dei casi
            if (componentiHelper
                    .isComponenteInElenco(getForm().getFirmeUnitaDocumentarieDetail().getId_comp_doc().parse())) {
                getForm().getFirmaDataVersamento().setHidden(false);
            } else {
                getForm().getFirmaDataVersamento().setHidden(true);
            }

            if (componentiHelper
                    .isComponenteInVolume(getForm().getFirmeUnitaDocumentarieDetail().getId_comp_doc().parse())) {
                getForm().getFirmaChiusuraVolume().setHidden(false);
            } else {
                getForm().getFirmaChiusuraVolume().setHidden(true);
            }

            forwardToPublisher(Application.Publisher.FIRME_DETAIL);
        } else if (getRequest().getParameter("table").equals(getForm().getMarcheList().getName())) {
            // LISTA MARCHE
            getForm().getMarcheDettaglioTabs()
                    .setCurrentTab(getForm().getMarcheDettaglioTabs().getInfoPrincipaliMarche());
            // apri le sezioni
            getForm().getDocumento().setLoadOpened(true);
            getForm().getComponente().setLoadOpened(true);
            forwardToPublisher(Application.Publisher.MARCHE_DETAIL);
        } // Se ho cliccato sull'icona del "Visualizza unità documentaria" di Lista
          // Componenti
        else if ((getRequest().getParameter("table").equals(getForm().getUnitaDocumentariaList().getName()))) {
            Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
            BigDecimal idCompDoc = getForm().getComponentiList().getTable().getRow(riga).getBigDecimal("id_comp_doc");
            UnitaDocumentarieForm form = new UnitaDocumentarieForm();
            AroVRicUnitaDocTableBean unitaDocTB = new AroVRicUnitaDocTableBean();
            BigDecimal[] ids = componentiHelper.retrieveStrutUnitaDoc(idCompDoc);
            unitaDocTB.add(udHelper.getAroVRicUnitaDocRowBean(ids[0], ids[1], null));
            form.getUnitaDocumentarieList().setTable(unitaDocTB);
            redirectToAction(Application.Actions.UNITA_DOCUMENTARIE,
                    "?operation=listNavigationOnClick&navigationEvent=" + ListAction.NE_DETTAGLIO_VIEW + "&table="
                            + UnitaDocumentarieForm.UnitaDocumentarieList.NAME + "&riga=0",
                    form);
        }
    }

    @Override
    public void dettaglio_ud() throws EMFError {
        UnitaDocumentarieForm form = new UnitaDocumentarieForm();
        AroVRicUnitaDocTableBean unitaDocTB = new AroVRicUnitaDocTableBean();
        BigDecimal idUnitaDoc = getForm().getComponentiDetail().getId_unita_doc().parse();
        BigDecimal idStrut = getForm().getComponentiDetail().getId_strut_unita_doc().parse();
        unitaDocTB.add(udHelper.getAroVRicUnitaDocRowBean(idUnitaDoc, idStrut, null));
        form.getUnitaDocumentarieList().setTable(unitaDocTB);
        redirectToAction(Application.Actions.UNITA_DOCUMENTARIE,
                "?operation=listNavigationOnClick&navigationEvent=" + ListAction.NE_DETTAGLIO_VIEW + "&table="
                        + UnitaDocumentarieForm.UnitaDocumentarieList.NAME + "&riga=0",
                form);
    }

    @Override
    public void dettaglio_doc() throws EMFError {
        UnitaDocumentarieForm form = new UnitaDocumentarieForm();
        AroVLisDocTableBean docTB = new AroVLisDocTableBean();
        BigDecimal idDoc = getForm().getComponentiDetail().getId_doc().parse();
        docTB.add(udHelper.getAroVVisDocIamRowBean(idDoc));
        form.getDocumentiUDList().setTable(docTB);
        redirectToAction(Application.Actions.UNITA_DOCUMENTARIE, "?operation=listNavigationOnClick&navigationEvent="
                + ListAction.NE_DETTAGLIO_VIEW + "&table=" + UnitaDocumentarieForm.DocumentiUDList.NAME + "&riga=0",
                form);
    }

    // Gestione COMPONENTI DETTAGLIO TABS
    @Override
    public void tabInfoPrincipaliCompOnClick() throws EMFError {
        getForm().getComponentiDettaglioTabs()
                .setCurrentTab(getForm().getComponentiDettaglioTabs().getInfoPrincipaliComp());
        forwardToPublisher(Application.Publisher.COMPONENTI_DETAIL);
    }

    @Override
    public void tabInfoVersateCompOnClick() throws EMFError {
        getForm().getComponentiDettaglioTabs()
                .setCurrentTab(getForm().getComponentiDettaglioTabs().getInfoVersateComp());
        forwardToPublisher(Application.Publisher.COMPONENTI_DETAIL);
    }

    @Override
    public void tabInfoVolumeConservCompOnClick() throws EMFError {
        getForm().getComponentiDettaglioTabs()
                .setCurrentTab(getForm().getComponentiDettaglioTabs().getInfoVolumeConservComp());
        forwardToPublisher(Application.Publisher.COMPONENTI_DETAIL);
    }

    @Override
    public void tabListaFirmeCompOnClick() throws EMFError {
        getForm().getComponentiDettaglioListsTabs()
                .setCurrentTab(getForm().getComponentiDettaglioListsTabs().getListaFirmeComp());
        forwardToPublisher(Application.Publisher.COMPONENTI_DETAIL);
    }

    // @Override
    // public void tabInfoElencoVersCompOnClick() throws EMFError {
    // getForm().getComponentiDettaglioTabs().setCurrentTab(getForm().getComponentiDettaglioTabs().getInfoElencoVersComp());
    // forwardToPublisher(Application.Publisher.COMPONENTI_DETAIL);
    // }
    @Override
    public void tabListaMarcheCompOnClick() throws EMFError {
        getForm().getComponentiDettaglioListsTabs()
                .setCurrentTab(getForm().getComponentiDettaglioListsTabs().getListaMarcheComp());
        forwardToPublisher(Application.Publisher.COMPONENTI_DETAIL);
    }

    @Override
    public void tabListaDatiSpecificiCompOnClick() throws EMFError {
        getForm().getComponentiDettaglioListsTabs()
                .setCurrentTab(getForm().getComponentiDettaglioListsTabs().getListaDatiSpecificiComp());
        forwardToPublisher(Application.Publisher.COMPONENTI_DETAIL);
    }

    @Override
    public void tabListaDatiSpecificiMigrazioneCompOnClick() throws EMFError {
        getForm().getComponentiDettaglioListsTabs()
                .setCurrentTab(getForm().getComponentiDettaglioListsTabs().getListaDatiSpecificiMigrazioneComp());
        forwardToPublisher(Application.Publisher.COMPONENTI_DETAIL);
    }

    // Gestione MARCHE DETTAGLIO TABS
    @Override
    public void tabInfoPrincipaliMarcheOnClick() throws EMFError {
        getForm().getMarcheDettaglioTabs().setCurrentTab(getForm().getMarcheDettaglioTabs().getInfoPrincipaliMarche());
        forwardToPublisher(Application.Publisher.MARCHE_DETAIL);
    }

    @Override
    public void tabControlloMarcaOnClick() throws EMFError {
        getForm().getMarcheDettaglioTabs().setCurrentTab(getForm().getMarcheDettaglioTabs().getControlloMarca());
        forwardToPublisher(Application.Publisher.MARCHE_DETAIL);
    }

    @Override
    public void tabListaCertificatiCAMarcheOnClick() throws EMFError {
        getForm().getMarcheDettaglioListsTabs()
                .setCurrentTab(getForm().getMarcheDettaglioListsTabs().getListaCertificatiCAMarche());
        forwardToPublisher(Application.Publisher.MARCHE_DETAIL);
    }

    @Override
    public void tabListaCertificatiCAOCSPMarcheOnClick() throws EMFError {
        getForm().getMarcheDettaglioListsTabs()
                .setCurrentTab(getForm().getMarcheDettaglioListsTabs().getListaCertificatiCAOCSPMarche());
        forwardToPublisher(Application.Publisher.MARCHE_DETAIL);
    }

    // Gestione FIRME DETTAGLIO TABS
    @Override
    public void tabInfoPrincipaliFirmeOnClick() throws EMFError {
        getForm().getFirmeDettaglioTabs().setCurrentTab(getForm().getFirmeDettaglioTabs().getInfoPrincipaliFirme());
        forwardToPublisher(Application.Publisher.FIRME_DETAIL);
    }

    @Override
    public void tabCertificatoFirmatarioOnClick() throws EMFError {
        getForm().getFirmeDettaglioTabs().setCurrentTab(getForm().getFirmeDettaglioTabs().getCertificatoFirmatario());
        forwardToPublisher(Application.Publisher.FIRME_DETAIL);
    }

    @Override
    public void tabListaCertificatiCAOCSPFirmeOnClick() throws EMFError {
        getForm().getFirmeDettaglioListsTabs()
                .setCurrentTab(getForm().getFirmeDettaglioListsTabs().getListaCertificatiCAOCSPFirme());
        forwardToPublisher(Application.Publisher.FIRME_DETAIL);
    }

    // @Override
    // public void tabControlloFirmaVersOnClick() throws EMFError {
    // getForm().getFirmeDettaglioTabs().setCurrentTab(getForm().getFirmeDettaglioTabs().getControlloFirmaVers());
    // forwardToPublisher(Application.Publisher.FIRME_DETAIL);
    // }
    //
    // @Override
    // public void tabControlloFirmaChiusVolOnClick() throws EMFError {
    // getForm().getFirmeDettaglioTabs().setCurrentTab(getForm().getFirmeDettaglioTabs().getControlloFirmaChiusVol());
    // forwardToPublisher(Application.Publisher.FIRME_DETAIL);
    // }
    @Override
    public void tabControlliFirmaOnClick() throws EMFError {
        getForm().getFirmeDettaglioTabs().setCurrentTab(getForm().getFirmeDettaglioTabs().getControlliFirma());
        forwardToPublisher(Application.Publisher.FIRME_DETAIL);
    }

    @Override
    public void tabListaCertificatiCAFirmeOnClick() throws EMFError {
        getForm().getFirmeDettaglioListsTabs()
                .setCurrentTab(getForm().getFirmeDettaglioListsTabs().getListaCertificatiCAFirme());
        forwardToPublisher(Application.Publisher.FIRME_DETAIL);
    }

    @Override
    public void tabListaControfirmatariOnClick() throws EMFError {
        getForm().getFirmeDettaglioListsTabs()
                .setCurrentTab(getForm().getFirmeDettaglioListsTabs().getListaControfirmatari());
        forwardToPublisher(Application.Publisher.FIRME_DETAIL);
    }

    /**
     * Metodo invocato al click del bottone di scaricamento file componente
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void scarica_comp_file() throws EMFError {
        scaricaComp(CostantiDB.TipiEntitaRecupero.COMP);
    }

    @Override
    public void scarica_dip_esibizione_comp_doc() throws EMFError {
        scaricaComp(CostantiDB.TipiEntitaRecupero.COMP_DIP_ESIBIZIONE);
    }

    private void scaricaComp(CostantiDB.TipiEntitaRecupero tipoEntitaRecupero) throws EMFError {
        BigDecimal idUnitaDoc = getForm().getComponentiDetail().getId_unita_doc().parse();
        BigDecimal idComp = (BigDecimal) getSession().getAttribute("idComp");
        String tipoSaveFile = udHelper.getTipoSaveFile(getForm().getComponentiDetail().getId_tipo_unita_doc().parse());
        CostantiDB.TipoSalvataggioFile tipoSalvataggioFile = CostantiDB.TipoSalvataggioFile.valueOf(tipoSaveFile);

        Recupero recupero = new Recupero();
        recupero.setVersione("Web");
        // Versatore
        recupero.setVersatore(new VersatoreType());
        recupero.getVersatore()
                .setAmbiente(getUser().getOrganizzazioneMap().get(WebConstants.Organizzazione.AMBIENTE.name()));
        recupero.getVersatore().setEnte(getUser().getOrganizzazioneMap().get(WebConstants.Organizzazione.ENTE.name()));
        recupero.getVersatore()
                .setStruttura(getUser().getOrganizzazioneMap().get(WebConstants.Organizzazione.STRUTTURA.name()));
        recupero.getVersatore().setUserID(getUser().getIdUtente() + "");
        // Chiave
        recupero.setChiave(new ChiaveType());
        recupero.getChiave().setTipoRegistro(getForm().getComponentiDetail().getCd_registro_key_unita_doc().parse());
        recupero.getChiave()
                .setAnno(BigInteger.valueOf(getForm().getComponentiDetail().getAa_key_unita_doc().parse().longValue()));
        recupero.getChiave().setNumero(getForm().getComponentiDetail().getCd_key_unita_doc().parse());

        RecuperoWeb recuperoDoc = new RecuperoWeb(recupero, getUser(), idUnitaDoc, idComp, tipoSalvataggioFile,
                tipoEntitaRecupero);
        RispostaWSRecupero rispostaWs = recuperoDoc.recuperaOggetto();

        switch (rispostaWs.getSeverity()) {
        case OK:
            getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(), getControllerName());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(), rispostaWs.getNomeFile());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(),
                    rispostaWs.getRifFileBinario().getFileSuDisco().getPath());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(), Boolean.toString(true));
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
    }

    public void download() throws EMFError {
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

    @Override
    public void reloadAfterGoBack(String publisherName) {
    }

    @Override
    public void scarica_dip_comp() throws EMFError {
        BigDecimal idComp = (BigDecimal) getSession().getAttribute("idComp");
        DownloadDip ddip = null;
        try {
            ddip = new DownloadDip(getUser(), getForm().getComponentiDetail().getId_unita_doc().parse());
            ddip.setIdCompDoc(idComp);
            ddip.setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.COMP_DIP);
            BaseTable tableBean = ddip.populateComponentiDipTable();
            if (ddip.getRispostaWs().getSeverity() == SeverityEnum.OK) {
                if (!ddip.getRispostaWs().getDatiRecuperoDip().getElementiTrovati().isEmpty()) {
                    getForm().getComponentiDipList().setTable(tableBean);
                } else {
                    getForm().getComponentiDipList().setTable(null);
                    getMessageBox().addError("Non sono stati trovati componenti recuperabili");
                }
            } else {
                getMessageBox().addError(ddip.getRispostaWs().getErrorMessage());
            }
        } catch (NamingException ex) {
            // logger.fatal(ex);
            logger.error("Eccezione", ex);
            getMessageBox().addFatal("Impossibile completare l'operazione, contattare l'assistenza tecnica", ex);
        }
        if (getMessageBox().isEmpty()) {
            getSession().setAttribute(WebConstants.DOWNLOAD_DIP.DIP_RECUPERO_EXT.name(), ddip.getRecuperoExt());
            getSession().setAttribute(WebConstants.DOWNLOAD_DIP.DIP_RISPOSTA_WS.name(), ddip.getRispostaWs());
            getSession().setAttribute(WebConstants.DOWNLOAD_DIP.DIP_ENTITA.name(),
                    CostantiDB.TipiEntitaRecupero.COMP_DIP.name());
            getForm().getScaricaDipBL().setEditMode();
            forwardToPublisher(Application.Publisher.LISTA_DIP_COMP);
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    public void scaricaDip() throws EMFError {
        RispostaWSRecupero rispostaWs = (RispostaWSRecupero) getSession()
                .getAttribute(WebConstants.DOWNLOAD_DIP.DIP_RISPOSTA_WS.name());
        RecuperoExt myRecuperoExt = (RecuperoExt) getSession()
                .getAttribute(WebConstants.DOWNLOAD_DIP.DIP_RECUPERO_EXT.name());

        setTableName(getForm().getComponentiDipList().getName());
        setRiga(getRequest().getParameter("riga"));
        getForm().getComponentiDipList().getTable().setCurrentRowIndex(Integer.parseInt(getRiga()));

        myRecuperoExt.getParametriRecupero().setIdComponente(
                getForm().getComponentiDipList().getTable().getCurrentRow().getBigDecimal("id_comp").longValue());
        myRecuperoExt.getParametriRecupero().setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.COMP_DIP);

        DownloadDip ddip;
        try {
            ddip = new DownloadDip(myRecuperoExt, rispostaWs);
            ddip.scaricaDipZip(DownloadDip.TIPO_DOWNLOAD.SCARICA_COMP_CONV);
        } catch (NamingException ex) {
            // logger.fatal(ex);
            logger.error("Eccezione", ex);
            getMessageBox().addFatal("Impossibile completare l'operazione, contattare l'assistenza tecnica", ex);
        }
        if (getMessageBox().isEmpty()) {
            switch (rispostaWs.getSeverity()) {
            case OK:
                getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(), getControllerName());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(),
                        rispostaWs.getNomeFile());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(),
                        rispostaWs.getRifFileBinario().getFileSuDisco().getPath());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
                        Boolean.toString(true));
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                        rispostaWs.getMimeType());
                break;
            case WARNING:
                getMessageBox().addInfo(rispostaWs.getErrorMessage());
                break;
            case ERROR:
                getMessageBox().addError(rispostaWs.getErrorMessage());
                break;
            }
        }
        if (!getMessageBox().isEmpty()) {
            if (!rispostaWs.getDatiRecuperoDip().getElementiTrovati().isEmpty()) {
                getForm().getComponentiDipList().setTable(
                        DownloadDip.generaTableBean(rispostaWs.getDatiRecuperoDip().getElementiTrovati().values()));
            } else {
                getForm().getComponentiDipList().setTable(null);
            }
            forwardToPublisher(getLastPublisher());
        } else {
            forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
        }
    }

    @Override
    public void scaricaZip() throws EMFError {
        RispostaWSRecupero rispostaWs = (RispostaWSRecupero) getSession()
                .getAttribute(WebConstants.DOWNLOAD_DIP.DIP_RISPOSTA_WS.name());
        RecuperoExt myRecuperoExt = (RecuperoExt) getSession()
                .getAttribute(WebConstants.DOWNLOAD_DIP.DIP_RECUPERO_EXT.name());
        CostantiDB.TipiEntitaRecupero tipoEntita = CostantiDB.TipiEntitaRecupero
                .valueOf((String) getSession().getAttribute(WebConstants.DOWNLOAD_DIP.DIP_ENTITA.name()));

        DownloadDip ddip;
        try {
            ddip = new DownloadDip(myRecuperoExt, rispostaWs);
            ddip.setTipoEntitaSacer(tipoEntita);
            ddip.scaricaDipZip(DownloadDip.TIPO_DOWNLOAD.SCARICA_ZIP);
        } catch (NamingException ex) {
            // logger.fatal(ex);
            logger.error("Eccezione", ex);
            getMessageBox().addFatal("Impossibile completare l'operazione, contattare l'assistenza tecnica", ex);
        }
        if (getMessageBox().isEmpty()) {
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
        }
        if (!getMessageBox().isEmpty()) {
            if (!rispostaWs.getDatiRecuperoDip().getElementiTrovati().isEmpty()) {
                getForm().getComponentiDipList().setTable(
                        DownloadDip.generaTableBean(rispostaWs.getDatiRecuperoDip().getElementiTrovati().values()));
            } else {
                getForm().getComponentiDipList().setTable(null);
            }
            forwardToPublisher(getLastPublisher());
        } else {
            forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
        }
    }

    @Override
    public void scarica_report_firma() throws EMFError {
        BigDecimal idUnitaDoc = getForm().getComponentiDetail().getId_unita_doc().parse();
        BigDecimal idComp = (BigDecimal) getSession().getAttribute("idComp");

        Recupero recupero = new Recupero();
        recupero.setVersione("Web");
        // Versatore
        recupero.setVersatore(new VersatoreType());
        recupero.getVersatore()
                .setAmbiente(getUser().getOrganizzazioneMap().get(WebConstants.Organizzazione.AMBIENTE.name()));
        recupero.getVersatore().setEnte(getUser().getOrganizzazioneMap().get(WebConstants.Organizzazione.ENTE.name()));
        recupero.getVersatore()
                .setStruttura(getUser().getOrganizzazioneMap().get(WebConstants.Organizzazione.STRUTTURA.name()));
        recupero.getVersatore().setUserID(getUser().getIdUtente() + "");
        // Chiave
        recupero.setChiave(new ChiaveType());
        recupero.getChiave().setTipoRegistro(getForm().getComponentiDetail().getCd_registro_key_unita_doc().parse());
        recupero.getChiave()
                .setAnno(BigInteger.valueOf(getForm().getComponentiDetail().getAa_key_unita_doc().parse().longValue()));
        recupero.getChiave().setNumero(getForm().getComponentiDetail().getCd_key_unita_doc().parse());

        RecuperoWeb recuperoReport = new RecuperoWeb(recupero, getUser(), idUnitaDoc, idComp,
                CostantiDB.TipoSalvataggioFile.FILE, CostantiDB.TipiEntitaRecupero.REPORT_FIRMA);
        RispostaWSRecupero rispostaWs = recuperoReport.recuperaReportFirma();

        switch (rispostaWs.getSeverity()) {
        case OK:
            getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(), getControllerName());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(), rispostaWs.getNomeFile());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                    rispostaWs.getMimeType());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(),
                    rispostaWs.getRifFileBinario().getFileSuDisco().getPath());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(), Boolean.toString(true));
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
    }

}
