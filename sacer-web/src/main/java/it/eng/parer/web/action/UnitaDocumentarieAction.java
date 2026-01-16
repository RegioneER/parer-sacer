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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.naming.NamingException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneDatiSpecifici.ejb.DatiSpecificiEjb;
import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc.ejb.FormatoFileDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneRegistro.ejb.RegistroEjb;
import it.eng.parer.amministrazioneStrutture.gestioneSistemaMigrazione.ejb.SistemaMigrazioneEjb;
import it.eng.parer.amministrazioneStrutture.gestioneSottoStrutture.ejb.SottoStruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoDoc.ejb.TipoDocumentoEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoFascicolo.ejb.TipoFascicoloEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoRappresentazione.ejb.TipoRappresentazioneEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoStrutturaDoc.ejb.TipoStrutturaDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb.TipoUnitaDocEjb;
import it.eng.parer.annulVers.ejb.AnnulVersEjb;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.constraint.DecModelloXsdUd;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import it.eng.parer.objectstorage.dto.RecuperoDocBean;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import it.eng.parer.serie.dto.RegistroTipoUnitaDoc;
import it.eng.parer.serie.ejb.SerieEjb;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.UnitaDocumentarieAbstractAction;
import it.eng.parer.slite.gen.form.ComponentiForm;
import it.eng.parer.slite.gen.form.CriteriRaggruppamentoForm;
import it.eng.parer.slite.gen.form.ElenchiVersamentoForm;
import it.eng.parer.slite.gen.form.FascicoliForm;
import it.eng.parer.slite.gen.form.SerieUDForm;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm.FiltriCollegamentiUnitaDocumentarie;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm.FiltriFascicoliUnitaDocumentarie;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm.FiltriFirmatariUnitaDocumentarie;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm.FiltriUnitaDocumentarieDatiSpec;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm.UnitaDocumentarieList;
import it.eng.parer.slite.gen.form.VolumiForm;
import it.eng.parer.slite.gen.tablebean.AroLogStatoConservUdTableBean;
import it.eng.parer.slite.gen.tablebean.AroUpdUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.AroVerIndiceAipUdRowBean;
import it.eng.parer.slite.gen.tablebean.AroVerIndiceAipUdTableBean;
import it.eng.parer.slite.gen.tablebean.AroWarnUpdUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecAttribDatiSpecRowBean;
import it.eng.parer.slite.gen.tablebean.DecAttribDatiSpecTableBean;
import it.eng.parer.slite.gen.tablebean.DecAttribDatiSpecTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoCompDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecTipoFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoFascicoloTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecTipoRapprCompTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecVersioneWsTableBean;
import it.eng.parer.slite.gen.tablebean.ElvElencoVerRowBean;
import it.eng.parer.slite.gen.tablebean.ElvElencoVerTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.slite.gen.tablebean.OrgSubStrutTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisArchivUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisCompDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisDatiSpecTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVLisDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisElvVerTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisFascRowBean;
import it.eng.parer.slite.gen.viewbean.AroVLisFascTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisLinkUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVLisLinkUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisNotaUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVLisNotaUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisUpdCompUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisUpdDocUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisUpdKoRisoltiTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisVolCorTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisVolNoValDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVRicUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVRicUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVRicUnitaDocTableDescriptor;
import it.eng.parer.slite.gen.viewbean.AroVVisDocIamRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisDocIamTableBean;
import it.eng.parer.slite.gen.viewbean.AroVVisNotaUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisUnitaDocIamRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisUpdUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisUpdUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoVersTableBean;
import it.eng.parer.slite.gen.viewbean.FasVVisFascicoloTableBean;
import it.eng.parer.slite.gen.viewbean.SerVRicSerieUdTableBean;
import it.eng.parer.util.helper.UniformResourceNameUtilHelper;
import it.eng.parer.viewEntity.AroVDtVersMaxByUnitaDoc;
import it.eng.parer.volume.utils.VolumeEnums;
import it.eng.parer.web.dto.DecCriterioAttribBean;
import it.eng.parer.web.dto.DecCriterioDatiSpecBean;
import it.eng.parer.web.dto.DefinitoDaBean;
import it.eng.parer.web.ejb.DataMartEjb;
import it.eng.parer.web.ejb.ElenchiVersamentoEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.ActionEnums.TipoRicercaAttribute;
import it.eng.parer.web.util.ActionEnums.UnitaDocAttributes;
import it.eng.parer.web.util.ActionUtils;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Constants.TipoEntitaSacer;
import it.eng.parer.web.util.DownloadDip;
import it.eng.parer.web.util.RecuperoWeb;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.web.util.XmlPrettyPrintFormatter;
import it.eng.parer.web.validator.TypeValidator.ChiaveBean;
import it.eng.parer.web.validator.UnitaDocumentarieValidator;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.parer.ws.ejb.RecuperoDocumento;
import it.eng.parer.ws.recupero.dto.ComponenteRec;
import it.eng.parer.ws.recupero.dto.ParametriRecupero;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.recupero.ejb.oracleClb.RecClbOracle;
import it.eng.parer.ws.recuperoDip.ejb.RecuperoDip;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.parer.ws.versamento.dto.ComponenteVers;
import it.eng.parer.ws.xml.versReqStato.ChiaveType;
import it.eng.parer.ws.xml.versReqStato.Recupero;
import it.eng.parer.ws.xml.versReqStato.VersatoreType;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoIFace.Values;
import it.eng.spagoLite.ExecutionHistory;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.decodemap.DecodeMapIF;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements;
import it.eng.spagoLite.form.base.BaseForm;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.form.fields.impl.ComboBox;
import it.eng.spagoLite.form.fields.impl.MultiSelect;
import it.eng.spagoLite.message.MessageBox;
import it.eng.spagoLite.security.Secure;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import it.eng.parer.entity.constraint.AplValoreParamApplic;
import it.eng.parer.exception.PreparazioneFisicaException;
import it.eng.parer.slite.gen.tablebean.DmUdDelRichiesteRowBean;
import it.eng.parer.slite.gen.tablebean.DmUdDelRichiesteTableBean;
import it.eng.parer.slite.gen.tablebean.DmUdDelTableBean;
import it.eng.parer.web.helper.DataMartHelper;
import it.eng.parer.ws.utils.CostantiDB.TipoRichiesta;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import java.util.stream.Collectors;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings({
        "rawtypes", "unchecked" })
public class UnitaDocumentarieAction extends UnitaDocumentarieAbstractAction {

    private static final String ECCEZIONE_RECUPERO_RAPPORTO_VERSAMENTO = "Eccezione nel recupero del Rapporto di versamento: ";
    private static final String ECCEZIONE_RECUPERO_INDICE_AIP = "Errore non gestito nel recupero del file";

    private static Logger log = LoggerFactory.getLogger(UnitaDocumentarieAction.class.getName());
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/Parer-ejb/UnitaDocumentarieHelper")
    private UnitaDocumentarieHelper udHelper;
    @EJB(mappedName = "java:app/Parer-ejb/RecuperoDip")
    private RecuperoDip recuperoDip;
    @EJB(mappedName = "java:app/Parer-ejb/SerieEjb")
    private SerieEjb serieEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AnnulVersEjb")
    private AnnulVersEjb annulVersEjb;
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
    @EJB(mappedName = "java:app/Parer-ejb/SistemaMigrazioneEjb")
    private SistemaMigrazioneEjb sysMigrazioneEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoRappresentazioneEjb")
    private TipoRappresentazioneEjb tipoRapprEjb;
    @EJB(mappedName = "java:app/Parer-ejb/SottoStruttureEjb")
    private SottoStruttureEjb subStrutEjb;
    @EJB(mappedName = "java:app/Parer-ejb/DatiSpecificiEjb")
    private DatiSpecificiEjb datiSpecEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ElenchiVersamentoEjb")
    private ElenchiVersamentoEjb evEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoFascicoloEjb")
    private TipoFascicoloEjb tipoFascicoloEjb;
    @EJB(mappedName = "java:app/Parer-ejb/UniformResourceNameUtilHelper")
    private UniformResourceNameUtilHelper urnHelper;
    @EJB(mappedName = "java:app/Parer-ejb/RecuperoDocumento")
    private RecuperoDocumento recuperoDocumento;
    @EJB(mappedName = "java:app/Parer-ejb/ObjectStorageService")
    private ObjectStorageService objectStorageService;
    @EJB(mappedName = "java:app/Parer-ejb/DataMartEjb")
    private DataMartEjb dataMartEjb;
    @EJB(mappedName = "java:app/Parer-ejb/DataMartHelper")
    private DataMartHelper dataMartHelper;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;

    private DecodeMap mappaTipoUD;
    private DecodeMap mappaRegistro;
    private DecodeMap mappaTipoDoc;
    private DecodeMap mappaSisMig;
    private DecodeMap mappaTipoFasc;
    private DecodeMap mappaTipoAnnullamento;

    private BigDecimal getIdStrut() {
        return getUser().getIdOrganizzazioneFoglia();
    }

    /**
     * Metodo di inizializzazione form di ricerca unitÃ documentarie
     *
     * @throws EMFError errore generico
     */
    @Override
    public void initOnClick() throws EMFError {
    }

    /**
     * Metodo di inizializzazione form di ricerca unità documentarie semplice Nuova
     *
     * @throws EMFError errore generico
     */
    @Secure(action = "Menu.UnitaDocumentarie.UnitaDocumentarieRicercaSempliceNuova")
    public void unitaDocumentarieRicercaSempliceNuova() throws EMFError {
        unitaDocumentarieRicercaSempliceComune(WebConstants.tipoRicercaSemplice.SEMPLICE_NEW);
    }

    /**
     * Metodo di inizializzazione form di ricerca unità documentarie semplice
     *
     * @throws EMFError errore generico
     */
    @Secure(action = "Menu.UnitaDocumentarie.UnitaDocumentarieRicercaSempliceNuova")
    public void unitaDocumentarieRicercaSemplice() throws EMFError {
        unitaDocumentarieRicercaSempliceComune(WebConstants.tipoRicercaSemplice.SEMPLICE);
    }

    /**
     * Metodo di inizializzazione form di ricerca unità documentarie semplice comune
     *
     * @param tipoRicercaSemplice Indica se si vuole la ricerca nuova o la vecchia o quella per
     *                            profilo normativo
     *
     * @throws EMFError errore generico
     */
    protected void unitaDocumentarieRicercaSempliceComune(
            WebConstants.tipoRicercaSemplice tipoRicercaSemplice) throws EMFError {
        getUser().getMenu().reset();

        switch (tipoRicercaSemplice) {
        case SEMPLICE_NEW:
            getUser().getMenu()
                    .select("Menu.UnitaDocumentarie.UnitaDocumentarieRicercaSempliceNuova");
            break;
        case SEMPLICE:
        default:
            getUser().getMenu().select("Menu.UnitaDocumentarie.UnitaDocumentarieRicercaSemplice");
            break;
        }

        getSession().setAttribute(UnitaDocAttributes.TIPORICERCA.name(),
                TipoRicercaAttribute.SEMPLICE.name());

        // Pulisco i filtri di ricerca unità documentarie semplice
        getForm().getFiltriUnitaDocumentarieSemplice().reset();

        // Setto aperte e con bottone le section base
        setBaseSections(true, false);

        /*
         * Azzero la struttura in memoria che memorizza le info dei filtri dati specifici
         */
        if (getSession().getAttribute("listaDatiSpecOnLine") != null) {
            getSession().removeAttribute("listaDatiSpecOnLine");
        }

        getSession().removeAttribute(WebConstants.DOWNLOAD_DIP.DIP_RISPOSTA_WS.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_DIP.DIP_RECUPERO_EXT.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_DIP.DIP_ENTITA.name());

        initMappeTipiDato();

        // Setto le varie combo dei FILTRI di ricerca Unità Documentarie
        getForm().getFiltriUnitaDocumentarieSemplice().getNm_tipo_unita_doc()
                .setDecodeMap(mappaTipoUD);
        getForm().getFiltriUnitaDocumentarieSemplice().getNm_tipo_doc().setDecodeMap(mappaTipoDoc);
        getForm().getFiltriUnitaDocumentarieSemplice().getFl_unita_doc_firmato()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriUnitaDocumentarieSemplice().getTi_esito_verif_firme()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_esito_verif_firme",
                        VolumeEnums.StatoVerifica.values()));
        getForm().getFiltriUnitaDocumentarieSemplice().getFl_forza_accettazione()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriUnitaDocumentarieSemplice().getFl_forza_conservazione()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriUnitaDocumentarieSemplice().getFl_forza_collegamento()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        DecVersioneWsTableBean versioneWsTableBean = tipoUnitaDocEjb
                .getDecVersioneWsTableBean("Versamento ud");
        getForm().getFiltriUnitaDocumentarieSemplice().getCd_versione_ws()
                .setDecodeMap(DecodeMap.Factory.newInstance(versioneWsTableBean, "cd_versione_ws",
                        "cd_versione_ws"));
        getForm().getFiltriUnitaDocumentarieSemplice().getFl_doc_aggiunti()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriUnitaDocumentarieSemplice().getFl_agg_meta()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriUnitaDocumentarieSemplice().getTi_stato_conservazione()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato_conservazione",
                        CostantiDB.StatoConservazioneUnitaDocNonAnnullata.values()));
        getForm().getFiltriUnitaDocumentarieSemplice().getTi_stato_ud_elenco_vers()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato_ud_elenco_vers",
                        ElencoEnums.UdDocStatusEnum.values()));
        getForm().getFiltriUnitaDocumentarieSemplice().getCd_registro_key_unita_doc()
                .setDecodeMap(mappaRegistro);
        getForm().getFiltriUnitaDocumentarieSemplice().getFl_profilo_normativo()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

        /* Filtro sottostrutture */
        OrgSubStrutTableBean tmpSubStrutsTableBean = subStrutEjb
                .getOrgSubStrutTableBeanAbilitate(getUser().getIdUtente(), getIdStrut());
        DecodeMap mappaSubStruts = DecodeMap.Factory.newInstance(tmpSubStrutsTableBean,
                "id_sub_strut", "nm_sub_strut");
        getForm().getFiltriUnitaDocumentarieSemplice().getNm_sub_strut()
                .setDecodeMap(mappaSubStruts);
        // Precompilo la mappa con tutti i valori
        Iterator it = mappaSubStruts.keySet().iterator();
        String[] chiavi = new String[mappaSubStruts.keySet().size()];
        int i = 0;
        while (it.hasNext()) {
            BigDecimal chiave = (BigDecimal) it.next();
            chiavi[i] = "" + chiave;
            i++;
        }
        getForm().getFiltriUnitaDocumentarieSemplice().getNm_sub_strut().setValues(chiavi);

        switch (tipoRicercaSemplice) {
        case SEMPLICE_NEW:
            forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_SEMPLICE_NUOVA);
            break;
        case SEMPLICE:
        default:
            forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_SEMPLICE);
            break;
        }

        // Imposto i filtri in edit mode
        getForm().getFiltriUnitaDocumentarieSemplice().setEditMode();
        getForm().getUnitaDocumentarieRicercaButtonList().setEditMode();
        // Inizializzo la lista di unitÃ documentarie vuota e con 10 righe per pagina
        getForm().getUnitaDocumentarieList().setTable(null);
        getForm().getUnitaDocumentarieTabs()
                .setCurrentTab(getForm().getUnitaDocumentarieTabs().getFiltriRicercaAvanzata());
        getForm().getFiltriUnitaDocumentarieSemplice().getFl_unita_doc_annul().setValue("0");
        postLoad();
    }

    private void setBaseSections(boolean loadOpen, boolean showButton) {
        getForm().getUnitaDocumentarieChiaveSection().setLoadOpened(loadOpen);
        getForm().getUDRicercaSection().setLoadOpened(loadOpen);
        getForm().getProfiloArchivistico().setLoadOpened(loadOpen);
        getForm().getContrConservRicercaSection().setLoadOpened(loadOpen);
        getForm().getUnitaDocumentarieChiaveSection().setShowButton(showButton);
        getForm().getUDRicercaSection().setShowButton(showButton);
        getForm().getProfiloArchivistico().setShowButton(showButton);
        getForm().getContrConservRicercaSection().setShowButton(showButton);
    }

    /**
     * Metodo di inizializzazione form di ricerca unitÃ documentarie avanzata
     *
     * @throws EMFError errore generico
     */
    @Secure(action = "Menu.UnitaDocumentarie.UnitaDocumentarieRicercaAvanzata")
    public void unitaDocumentarieRicercaAvanzata() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.UnitaDocumentarie.UnitaDocumentarieRicercaAvanzata");
        String cleanHistory = getRequest().getParameter(CLEAN_HISTORY);
        if (Boolean.parseBoolean(cleanHistory)) {
            getForm().getUnitaDocumentariePerSerie().reset();
        }

        getSession().setAttribute(UnitaDocAttributes.TIPORICERCA.name(),
                TipoRicercaAttribute.AVANZATA.name());
        setUnitaDocumentarieRicercaAvanzata();
        getForm().getFiltriUnitaDocumentarieAvanzata().getFl_unita_doc_annul().setValue("0");
        postLoad();
    }

    /**
     * Metodo di inizializzazione form di ricerca unità documentarie dati spec
     *
     * @throws EMFError errore generico
     */
    @Secure(action = "Menu.Amministrazione.UnitaDocumentarieRicercaDatiSpec")
    public void unitaDocumentarieRicercaDatiSpec() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Amministrazione.UnitaDocumentarieRicercaDatiSpec");
        String cleanHistory = getRequest().getParameter(CLEAN_HISTORY);
        if (Boolean.parseBoolean(cleanHistory)) {
            getForm().getUnitaDocumentariePerSerie().reset();
        }

        getSession().setAttribute(UnitaDocAttributes.TIPORICERCA.name(),
                TipoRicercaAttribute.AVANZATA.name());
        setUnitaDocumentarieRicercaDatiSpec();
        // getForm().getFiltriUnitaDocumentarieDatiSpec().getFl_unita_doc_annul().setValue("0");
        postLoad();
    }

    /**
     * Metodo di inizializzazione form di ricerca unita documentarie di versamenti annullati
     *
     * @throws EMFError errore generico
     */
    @Secure(action = "Menu.AnnulVers.UnitaDocumentarieRicercaVersAnnullati")
    public void unitaDocumentarieRicercaVersAnnullati() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.AnnulVers.UnitaDocumentarieRicercaVersAnnullati");
        getSession().setAttribute(UnitaDocAttributes.TIPORICERCA.name(),
                TipoRicercaAttribute.VERS_ANNULLATI.name());
        setUnitaDocumentarieRicercaUDAnnullate();
        getForm().getUnitaDocumentarieRicercaButtonList().getRicercaUDAnnullate().setEditMode();
        getForm().getUnitaDocumentarieRicercaButtonList().getDownloadContenutoAnnullate()
                .setEditMode();
        getForm().getUnitaDocumentarieRicercaButtonList().getDownloadContenutoAnnullate()
                .setDisableHourGlass(true);
        // getForm().getFiltriUnitaDocumentarieAvanzata().getFl_unita_doc_annul().setValue("1");
        postLoad();
    }

    public void setUnitaDocumentarieRicercaAvanzata() throws EMFError {
        /*
         * Azzero violentamente la UnitaDocumentarieList per evitare problemi di visualizzazione del
         * primo record della lista che si manifesta quando passo da una selectedList (ricerca
         * avanzata) ad una list normale (ricerca semplice) all'interno della stessa action
         */
        getForm().addComponent(new UnitaDocumentarieList());

        // Pulisco i filtri di ricerca unità documentarie avanzata
        getForm().getFiltriUnitaDocumentarieAvanzata().reset();
        getForm().getFiltriCollegamentiUnitaDocumentarie().reset();
        getForm().getFiltriComponentiUnitaDocumentarie().reset();
        getForm().getFiltriFirmatariUnitaDocumentarie().reset();
        getForm().getFiltriFascicoliUnitaDocumentarie().reset();

        // Setto aperte e con bottone le section base
        setBaseSections(true, true);
        getForm().getProfiloArchivistico().setLoadOpened(false);

        /*
         * Azzero la struttura in memoria che memorizza le info dei filtri dati specifici
         */
        if (getSession().getAttribute("listaDatiSpecOnLine") != null) {
            getSession().removeAttribute("listaDatiSpecOnLine");
        }

        getSession().removeAttribute(WebConstants.DOWNLOAD_DIP.DIP_RISPOSTA_WS.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_DIP.DIP_RECUPERO_EXT.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_DIP.DIP_ENTITA.name());

        initMappeTipiDato();

        // Setto le varie combo dei FILTRI di ricerca Unità Documentarie
        getForm().getFiltriUnitaDocumentarieAvanzata().getNm_tipo_unita_doc()
                .setDecodeMap(mappaTipoUD);
        getForm().getFiltriUnitaDocumentarieAvanzata().getNm_tipo_doc().setDecodeMap(mappaTipoDoc);
        getForm().getFiltriUnitaDocumentarieAvanzata().getFl_unita_doc_firmato()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriUnitaDocumentarieAvanzata().getTi_esito_verif_firme()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_esito_verif_firme",
                        VolumeEnums.StatoVerifica.values()));
        getForm().getFiltriUnitaDocumentarieAvanzata().getFl_forza_accettazione()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriUnitaDocumentarieAvanzata().getFl_forza_conservazione()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriUnitaDocumentarieAvanzata().getFl_forza_collegamento()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriUnitaDocumentarieAvanzata().getTi_stato_conservazione()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato_conservazione",
                        CostantiDB.StatoConservazioneUnitaDocNonAnnullata.values()));
        getForm().getFiltriUnitaDocumentarieAvanzata().getTi_stato_ud_elenco_vers()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato_ud_elenco_vers",
                        ElencoEnums.UdDocStatusEnum.values()));
        getForm().getFiltriUnitaDocumentarieAvanzata().getCd_registro_key_unita_doc()
                .setDecodeMap(mappaRegistro);
        getForm().getFiltriUnitaDocumentarieAvanzata().getTi_conservazione()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_conservazione",
                        VolumeEnums.TipoConservazione.values()));
        getForm().getFiltriUnitaDocumentarieAvanzata().getNm_sistema_migraz()
                .setDecodeMap(mappaSisMig);
        getForm().getFiltriUnitaDocumentarieAvanzata().getFl_profilo_normativo()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());//

        DecVersioneWsTableBean versioneWsTableBean = tipoUnitaDocEjb
                .getDecVersioneWsTableBean("Versamento ud");
        getForm().getFiltriUnitaDocumentarieAvanzata().getCd_versione_ws()
                .setDecodeMap(DecodeMap.Factory.newInstance(versioneWsTableBean, "cd_versione_ws",
                        "cd_versione_ws"));

        getForm().getFiltriDatiSpecUnitaDocumentarieList().getTi_oper().setDecodeMap(ComboGetter
                .getMappaSortedGenericEnum("operatore", CostantiDB.TipoOperatoreDatiSpec.values()));
        getForm().getFiltriCollegamentiUnitaDocumentarie().getCon_collegamento()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriCollegamentiUnitaDocumentarie().getIs_oggetto_collegamento()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriCollegamentiUnitaDocumentarie().getCd_registro_key_unita_doc_link()
                .setDecodeMap(mappaRegistro);
        getForm().getFiltriUnitaDocumentarieAvanzata().getFl_doc_aggiunti()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriUnitaDocumentarieAvanzata().getFl_agg_meta()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriUnitaDocumentarieAvanzata().getFl_hash_vers()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriComponentiUnitaDocumentarie().getFl_rif_temp_vers()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        // getForm().getFiltriFirmatariUnitaDocumentarie().getCon_firmatario()
        // .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

        /* Filtro sottostrutture */
        OrgSubStrutTableBean tmpSubStrutsTableBean = subStrutEjb
                .getOrgSubStrutTableBeanAbilitate(getUser().getIdUtente(), getIdStrut());
        DecodeMap mappaSubStruts = DecodeMap.Factory.newInstance(tmpSubStrutsTableBean,
                "id_sub_strut", "nm_sub_strut");
        getForm().getFiltriUnitaDocumentarieAvanzata().getNm_sub_strut()
                .setDecodeMap(mappaSubStruts);
        // Precompilo la mappa con tutti i valori
        Iterator it = mappaSubStruts.keySet().iterator();
        String[] chiavi = new String[mappaSubStruts.keySet().size()];
        int i = 0;
        while (it.hasNext()) {
            BigDecimal chiave = (BigDecimal) it.next();
            chiavi[i] = "" + chiave;
            i++;
        }
        getForm().getFiltriUnitaDocumentarieAvanzata().getNm_sub_strut().setValues(chiavi);

        // Setto le combo dei filtri componenti
        getForm().getFiltriComponentiUnitaDocumentarie().getFl_comp_firmato()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

        DecFormatoFileDocTableBean tmpTableBeanFormatoFileDoc = formatoFileDocEjb
                .getDecFormatoFileDocTableBean(getIdStrut());
        getForm().getFiltriComponentiUnitaDocumentarie().getNm_formato_file_vers()
                .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanFormatoFileDoc,
                        "id_formato_file_doc", "nm_formato_file_doc"));

        DecTipoStrutDocTableBean tmpTableBeanTipoStrutDoc = tipoStrutDocEjb
                .getDecTipoStrutDocTableBean(getIdStrut(), false);
        getForm().getFiltriComponentiUnitaDocumentarie().getNm_tipo_strut_doc()
                .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanTipoStrutDoc,
                        "id_tipo_strut_doc", "nm_tipo_strut_doc"));

        DecTipoRapprCompTableBean tmpRapprTableBean = tipoRapprEjb
                .getDecTipoRapprCompTableBean(getIdStrut(), false);
        DecodeMap mappaRappr = DecodeMap.Factory.newInstance(tmpRapprTableBean,
                "id_tipo_rappr_comp", "nm_tipo_rappr_comp");

        getForm().getFiltriComponentiUnitaDocumentarie().getTi_esito_contr_conforme()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("stato",
                        VolumeEnums.ControlloConformitaEnum.values()));
        getForm().getFiltriComponentiUnitaDocumentarie().getTi_esito_verif_firma()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_esito_verif_firme",
                        VolumeEnums.StatoVerifica.values()));
        getForm().getFiltriComponentiUnitaDocumentarie().getTi_esito_verif_firme_chius()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_esito_verif_firme_chius",
                        VolumeEnums.StatoVerifica.getComboEsitoVerifFirmeChius()));
        getForm().getFiltriComponentiUnitaDocumentarie().getTi_esito_contr_formato_file()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_esito_verif_formato_vers",
                        VolumeEnums.StatoFormatoVersamento.values()));
        getForm().getFiltriComponentiUnitaDocumentarie().getDs_algo_hash_file_calc()
                .setDecodeMap(ComboGetter.getMappaHashAlgorithm());
        getForm().getFiltriComponentiUnitaDocumentarie().getCd_encoding_hash_file_calc()
                .setDecodeMap(ComboGetter.getMappaHashEncoding());
        getForm().getFiltriComponentiUnitaDocumentarie().getFl_forza_accettazione_comp()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriComponentiUnitaDocumentarie().getFl_forza_conservazione_comp()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriComponentiUnitaDocumentarie().getTi_supporto_comp()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_supporto_comp",
                        ComponenteVers.TipiSupporto.values()));
        getForm().getFiltriComponentiUnitaDocumentarie().getNm_tipo_rappr_comp()
                .setDecodeMap(mappaRappr);

        // Setto le combo dei filtri su fascicoli
        getForm().getFiltriFascicoliUnitaDocumentarie().getNm_tipo_fascicolo()
                .setDecodeMap(mappaTipoFasc);

        // Setto le section aperte o chiuse
        getForm().getFiltriCollegamenti().setLoadOpened(false);
        getForm().getFiltriFirmatari().setLoadOpened(false);

        // Carico la pagina di ricerca
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_AVANZATA);

        // Imposto i filtri in edit mode
        getForm().getFiltriUnitaDocumentarieAvanzata().setEditMode();
        getForm().getUnitaDocumentarieRicercaButtonList().setEditMode();
        getForm().getFiltriCollegamentiUnitaDocumentarie().setEditMode();
        getForm().getFiltriFirmatariUnitaDocumentarie().setEditMode();
        getForm().getFiltriComponentiUnitaDocumentarie().setEditMode();
        getForm().getFiltriFascicoliUnitaDocumentarie().setEditMode();

        // Inizializzo la lista di unità documentarie vuota e con 10 righe per pagina
        getForm().getUnitaDocumentarieList().setTable(null);

        // Se popolata, svuoto la lista di filtri dati specifici
        if (getForm().getFiltriDatiSpecUnitaDocumentarieList() != null
                && getForm().getFiltriDatiSpecUnitaDocumentarieList().getTable() != null) {
            getForm().getFiltriDatiSpecUnitaDocumentarieList().getTable().removeAll();
        }

        // Setto la tab corrente sulla quale posizionarmi
        getForm().getUnitaDocumentarieTabs()
                .setCurrentTab(getForm().getUnitaDocumentarieTabs().getFiltriRicercaAvanzata());

        if (getForm().getUnitaDocumentariePerSerie().getId_contenuto_serie().parse() != null) {
            // Sto aggiungendo unita documentarie a una serie - inizializzo i campi assegnati
            Set<String> tipiUdSerie = getForm().getUnitaDocumentariePerSerie()
                    .getNm_tipo_unita_doc().getValues();
            Set<String> regSerie = getForm().getUnitaDocumentariePerSerie()
                    .getCd_registro_key_unita_doc().getValues();
            getForm().getFiltriUnitaDocumentarieAvanzata().getNm_tipo_unita_doc()
                    .setValues(tipiUdSerie.toArray(new String[tipiUdSerie.size()]));
            getForm().getFiltriUnitaDocumentarieAvanzata().getCd_registro_key_unita_doc()
                    .setValues(regSerie.toArray(new String[regSerie.size()]));

            getForm().getUnitaDocumentariePerSerieList().setTable(new BaseTable());
            getForm().getUnitaDocumentariePerSerieList().getTable()
                    .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getUnitaDocumentariePerSerieList().getTable().first();

            getForm().getUnitaDocumentarieToSerieSection().setHidden(false);
            getForm().getUnitaDocumentarieToRichAnnulVersSection().setHidden(true);
        } else if (getForm().getUnitaDocumentariePerRichAnnulVers().getId_rich_annul_vers()
                .parse() != null) {
            getForm().getUnitaDocumentariePerRichAnnulVersList().setTable(new BaseTable());
            getForm().getUnitaDocumentariePerRichAnnulVersList().getTable()
                    .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getUnitaDocumentariePerRichAnnulVersList().getTable().first();

            getForm().getUnitaDocumentarieToSerieSection().setHidden(true);
            getForm().getUnitaDocumentarieToRichAnnulVersSection().setHidden(false);
        } else {
            getForm().getUnitaDocumentarieToSerieSection().setHidden(true);
            getForm().getUnitaDocumentarieToRichAnnulVersSection().setHidden(true);
        }
    }

    public void setUnitaDocumentarieRicercaDatiSpec() throws EMFError {
        /*
         * Azzero violentamente la UnitaDocumentarieList per evitare problemi di visualizzazione del
         * primo record della lista che si manifesta quando passo da una selectedList (ricerca
         * avanzata) ad una list normale (ricerca semplice) all'interno della stessa action
         */
        getForm().addComponent(new UnitaDocumentarieList());

        // Pulisco i filtri di ricerca unità documentarie avanzata
        getForm().getFiltriUnitaDocumentarieDatiSpec().reset();
        getForm().getFiltriCollegamentiUnitaDocumentarie().reset();
        getForm().getFiltriComponentiUnitaDocumentarie().reset();
        getForm().getFiltriFirmatariUnitaDocumentarie().reset();
        getForm().getFiltriFascicoliUnitaDocumentarie().reset();

        getForm().getUDRicercaSection().setLegend("Unità documentaria");
        getForm().getUnitaDocumentarieTabs().getFiltriRicercaAvanzata()
                .setDescription("Filtri base");

        /*
         * Azzero la struttura in memoria che memorizza le info dei filtri dati specifici
         */
        if (getSession().getAttribute("listaDatiSpecOnLine") != null) {
            getSession().removeAttribute("listaDatiSpecOnLine");
        }

        getSession().removeAttribute(WebConstants.DOWNLOAD_DIP.DIP_RISPOSTA_WS.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_DIP.DIP_RECUPERO_EXT.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_DIP.DIP_ENTITA.name());

        initMappeTipiDato();

        // Setto le varie combo dei FILTRI di ricerca Unità Documentarie
        getForm().getFiltriUnitaDocumentarieDatiSpec().getNm_tipo_unita_doc()
                .setDecodeMap(mappaTipoUD);
        getForm().getFiltriUnitaDocumentarieDatiSpec().getNm_tipo_doc().setDecodeMap(mappaTipoDoc);
        getForm().getFiltriUnitaDocumentarieDatiSpec().getCd_registro_key_unita_doc()
                .setDecodeMap(mappaRegistro);
        // getForm().getFiltriUnitaDocumentarieDatiSpec().getNm_sistema_migraz().setDecodeMap(mappaSisMig);

        /* Filtro sottostrutture */
        OrgSubStrutTableBean tmpSubStrutsTableBean = subStrutEjb
                .getOrgSubStrutTableBeanAbilitate(getUser().getIdUtente(), getIdStrut());
        DecodeMap mappaSubStruts = DecodeMap.Factory.newInstance(tmpSubStrutsTableBean,
                "id_sub_strut", "nm_sub_strut");
        getForm().getFiltriUnitaDocumentarieDatiSpec().getNm_sub_strut()
                .setDecodeMap(mappaSubStruts);
        // Precompilo la mappa con tutti i valori
        Iterator it = mappaSubStruts.keySet().iterator();
        String[] chiavi = new String[mappaSubStruts.keySet().size()];
        int i = 0;
        while (it.hasNext()) {
            BigDecimal chiave = (BigDecimal) it.next();
            chiavi[i] = "" + chiave;
            i++;
        }
        getForm().getFiltriUnitaDocumentarieDatiSpec().getNm_sub_strut().setValues(chiavi);

        getForm().getFiltriDatiSpecUnitaDocumentarieList().getTi_oper().setDecodeMap(ComboGetter
                .getMappaSortedGenericEnum("operatore", CostantiDB.TipoOperatoreDatiSpec.values()));

        // Carico la pagina di ricerca
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_DATI_SPEC);

        // Imposto i filtri in edit mode
        getForm().getFiltriUnitaDocumentarieDatiSpec().setEditMode();
        getForm().getUnitaDocumentarieRicercaButtonList().setEditMode();
        getForm().getFiltriCollegamentiUnitaDocumentarie().setEditMode();
        getForm().getFiltriFirmatariUnitaDocumentarie().setEditMode();
        getForm().getFiltriComponentiUnitaDocumentarie().setEditMode();
        getForm().getFiltriFascicoliUnitaDocumentarie().setEditMode();

        // Inizializzo la lista di unità documentarie vuota e con 10 righe per pagina
        getForm().getUnitaDocumentarieList().setTable(null);

        // Se popolata, svuoto la lista di filtri dati specifici
        if (getForm().getFiltriDatiSpecUnitaDocumentarieList() != null
                && getForm().getFiltriDatiSpecUnitaDocumentarieList().getTable() != null) {
            getForm().getFiltriDatiSpecUnitaDocumentarieList().getTable().removeAll();
        }

        // Setto la tab corrente sulla quale posizionarmi
        getForm().getUnitaDocumentarieTabs()
                .setCurrentTab(getForm().getUnitaDocumentarieTabs().getFiltriRicercaAvanzata());
    }

    public void setUnitaDocumentarieRicercaUDAnnullate() throws EMFError {
        // Pulisco i filtri di ricerca unità documentarie annullate
        getForm().getFiltriUnitaDocumentarieAnnullate().reset();

        initMappeTipiDato();

        // Setto le varie combo dei FILTRI di ricerca Unità Documentarie annullate
        getForm().getFiltriUnitaDocumentarieAnnullate().getId_tipo_unita_doc()
                .setDecodeMap(mappaTipoUD);
        getForm().getFiltriUnitaDocumentarieAnnullate().getId_tipo_doc().setDecodeMap(mappaTipoDoc);
        getForm().getFiltriUnitaDocumentarieAnnullate().getCd_registro_key_unita_doc()
                .setDecodeMap(mappaRegistro);
        getForm().getFiltriUnitaDocumentarieAnnullate().getTi_annullamento()
                .setDecodeMap(mappaTipoAnnullamento);

        // Imposto i filtri in edit mode
        getForm().getFiltriUnitaDocumentarieAnnullate().setEditMode();

        // Inizializzo la lista di unità documentarie vuota e con 10 righe per pagina
        getForm().getUnitaDocumentarieList().setTable(null);
        // Inizializzo la lista di unità documentarie annullate vuota e con 10 righe per pagina
        getForm().getUnitaDocumentarieAnnullateList().setTable(null);

        // Carico la pagina di ricerca
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_UDANNULLATE);
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_SEMPLICE;
    }

    @Override
    public String getControllerName() {
        return Application.Actions.UNITA_DOCUMENTARIE;
    }

    @Override
    public void process() throws EMFError {
    }

    private void ricercaEDownload(boolean effettuaDownload) throws EMFError {
        // Controllo se la ricerca unità documentaria è semplice o avanzata
        // RICERCA UNITA' DOCUMENTARIE SEMPLICE
        if (getLastPublisher().equals(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_SEMPLICE)
                || getLastPublisher()
                        .equals(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_SEMPLICE_NUOVA)) {
            // Pulisce lo stack di id unità documentarie, necessario per il caricamento delle ud
            // collegate
            getSession().setAttribute("idUdStack", new ArrayList<BigDecimal>());
            UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice filtri = getForm()
                    .getFiltriUnitaDocumentarieSemplice();
            // Esegue la post dei filtri compilati
            filtri.post(getRequest());
            /*
             * Nella Ricerca semplice UD DEVO mettere a null questi due campi perché hanno lo stesso
             * nome di quelli che comopaiono nella lista dei risultati e il framework prende il
             * valore "0" alternativamente facendo una ricerca e poi rifacendla di nuovo.
             */
            filtri.getFl_forza_accettazione().setValue(null);
            filtri.getFl_forza_conservazione().setValue(null);

            // Valida i filtri per verificare quelli obbligatori
            String pageToForward = getLastPublisher();
            // String pageToForward = Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_SEMPLICE;
            if (filtri.validate(getMessageBox())) {
                // Carica le sottostrutture abilitate all'utente
                OrgSubStrutTableBean subStrutAbilUt = subStrutEjb
                        .getOrgSubStrutTableBeanAbilitate(getUser().getIdUtente(), getIdStrut());

                // Converte una lista di Object in una lista di BigDecimal
                List<BigDecimal> subStruttureAbilitateAllUtente = subStrutAbilUt
                        .toList("id_sub_strut").stream().map(obj -> new BigDecimal(obj.toString()))
                        .collect(Collectors.toList());
                // Valida i campi di ricerca
                UnitaDocumentarieValidator validator = new UnitaDocumentarieValidator(
                        getMessageBox());
                // Valido i filtri data acquisizione da - a restituendo le date comprensive di
                // orario
                Date[] dateAcquisizioneValidate = validator.validaDate(
                        filtri.getDt_acquisizione_unita_doc_da().parse(),
                        filtri.getOre_dt_acquisizione_unita_doc_da().parse(),
                        filtri.getMinuti_dt_acquisizione_unita_doc_da().parse(),
                        filtri.getDt_acquisizione_unita_doc_a().parse(),
                        filtri.getOre_dt_acquisizione_unita_doc_a().parse(),
                        filtri.getMinuti_dt_acquisizione_unita_doc_a().parse(),
                        filtri.getDt_acquisizione_unita_doc_da().getHtmlDescription(),
                        filtri.getDt_acquisizione_unita_doc_a().getHtmlDescription());

                Date[] dateUnitaDocValidate = validator.validaDate(
                        filtri.getDt_reg_unita_doc_da().parse(), null, null,
                        filtri.getDt_reg_unita_doc_a().parse(), null, null,
                        filtri.getDt_reg_unita_doc_da().getHtmlDescription(),
                        filtri.getDt_reg_unita_doc_a().getHtmlDescription());

                // Controllo l'obbligatorietà di anno o range anni di chiave unitÃ documentaria
                validator.controllaPresenzaAnno(filtri.getAa_key_unita_doc().parse(),
                        filtri.getAa_key_unita_doc_da().parse(),
                        filtri.getAa_key_unita_doc_a().parse());

                /*
                 * "Rielaboro" i filtri relativi ai tipi dato che necessitano di autorizzazioni.
                 * Controllo i valori che poi dovrò passare come filtro (Tipo Unità Documentaria,
                 * Tipo Documento, Sotto struttura, Registro): se non è valorizzato faccio una
                 * ricerca per tutti i valori ammessi (escamotage per far visualizzare in fase di
                 * ricerca solo i tipi di dato cui l'utente è abilitato) NOTA BENE: Per sotto
                 * struttura, pur essendo un tipo dato che necessita autorizzazioni, questo
                 * passaggio non è necessario: il campo è infatti obbligatorio, quindi per forza il
                 * filtro viene popolato!
                 */
                List<String> cdRegistroKeyUnitaDocListPerRicerca = getValoriForQueryFromFiltroCdRegistroCombo(
                        filtri.getCd_registro_key_unita_doc());
                List<BigDecimal> idTipoUnitaDocListPerRicerca = getValoriForQueryFromFiltroIdTipoUdCombo(
                        filtri.getNm_tipo_unita_doc());
                List<BigDecimal> idTipoDocListPerRicerca = getValoriForQueryFromFiltroIdTipoDocCombo(
                        filtri.getNm_tipo_doc());
                Object[] chiavi = null;
                if (!getMessageBox().hasError()) {
                    // Valida i campi di Range di chiavi unita'  documentaria
                    chiavi = validator.validaChiaviUnitaDocRicUd(
                            filtri.getCd_registro_key_unita_doc().getValue(),
                            filtri.getAa_key_unita_doc().parse(),
                            filtri.getCd_key_unita_doc().parse(),
                            filtri.getAa_key_unita_doc_da().parse(),
                            filtri.getAa_key_unita_doc_a().parse(),
                            filtri.getCd_key_unita_doc_da().parse(),
                            filtri.getCd_key_unita_doc_a().parse(),
                            filtri.getCd_key_unita_doc_contiene().parse());
                }

                if (!getMessageBox().hasError()) {
                    // La validazione non ha riportato errori.
                    if (chiavi != null && chiavi.length == 5) {
                        filtri.getAa_key_unita_doc_da().setValue(
                                chiavi[1] != null ? ((BigDecimal) chiavi[1]).toString() : null);
                        filtri.getAa_key_unita_doc_a().setValue(
                                chiavi[2] != null ? ((BigDecimal) chiavi[2]).toString() : null);
                        filtri.getCd_key_unita_doc_da()
                                .setValue(chiavi[3] != null ? (String) chiavi[3] : null);
                        filtri.getCd_key_unita_doc_a()
                                .setValue(chiavi[4] != null ? (String) chiavi[4] : null);
                    }

                    // Elimino il record dei filtri dati specifici dalla tabella USR_FILTRO_ATTRIB
                    // Effettuo la ricerca per la visualizzazione oppure per il download dei
                    // contenuti
                    if (effettuaDownload) {
                        AroVRicUnitaDocTableBean tb = udHelper
                                .getAroVRicUnitaDocRicSempliceViewBeanNoLimit(filtri,
                                        idTipoUnitaDocListPerRicerca,
                                        cdRegistroKeyUnitaDocListPerRicerca,
                                        idTipoDocListPerRicerca, dateAcquisizioneValidate,
                                        dateUnitaDocValidate, getIdStrut(),
                                        subStruttureAbilitateAllUtente, getLastPublisher().equals(
                                                Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_SEMPLICE_NUOVA));
                        if (!getMessageBox().hasError()) {
                            // MAC#39494 - Correzione metodo di generazione file in fase di
                            // esportazione di una ricerca
                            // File tmpFile = new File(System.getProperty("java.io.tmpdir"),
                            // "Contenuto_ricerca_unita_documentarie.csv");
                            try {
                                Path tmpPath = Files.createTempFile(
                                        "Contenuto_ricerca_unita_documentarie_", ".csv");
                                File tmpFile = tmpPath.toFile();
                                ActionUtils.buildCsvString(getForm().getUnitaDocumentarieList(), tb,
                                        AroVRicUnitaDocTableBean.TABLE_DESCRIPTOR, tmpFile);
                                getRequest().setAttribute(
                                        WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(),
                                        getControllerName());

                                // MAC#39494 - Correzione metodo di generazione file in fase di
                                // esportazione di una ricerca
                                String nomeFile = tmpFile.getName();
                                String nomeFinale = nomeFile.substring(0,
                                        nomeFile.lastIndexOf("_") - 1) + ".csv";
                                getSession().setAttribute(
                                        WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(),
                                        nomeFinale);

                                getSession().setAttribute(
                                        WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(),
                                        tmpFile.getPath());
                                getSession().setAttribute(
                                        WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
                                        Boolean.toString(true));
                                getSession().setAttribute(
                                        WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                                        "text/csv");
                            } catch (IOException ex) {
                                log.error("Errore in download "
                                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                                getMessageBox().addError(
                                        "Errore inatteso nella preparazione del download");
                            }
                        }

                        if (getMessageBox().hasError()) {
                            pageToForward = getLastPublisher();
                        } else {
                            pageToForward = Application.Publisher.DOWNLOAD_PAGE;
                        }

                    } else {
                        String maxResultRicercauD = configurationHelper
                                .getValoreParamApplicByApplic(
                                        CostantiDB.ParametroAppl.MAX_RESULT_RICERCA_UD);
                        AroVRicUnitaDocTableBean tb = udHelper
                                .getAroVRicUnitaDocRicSempliceViewBean(filtri,
                                        idTipoUnitaDocListPerRicerca,
                                        cdRegistroKeyUnitaDocListPerRicerca,
                                        idTipoDocListPerRicerca, dateAcquisizioneValidate,
                                        dateUnitaDocValidate, getIdStrut(),
                                        Integer.parseInt(maxResultRicercauD),
                                        subStruttureAbilitateAllUtente, getLastPublisher().equals(
                                                Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_SEMPLICE_NUOVA));
                        // Carico la tabella con i filtri impostati\
                        getForm().getUnitaDocumentarieList().setTable(tb);
                        getForm().getUnitaDocumentarieList().getTable().setPageSize(10);
                        // Aggiungo alla lista una regola di ordinamento
                        getForm().getUnitaDocumentarieList().getTable().addSortingRule(
                                AroVRicUnitaDocTableDescriptor.COL_DS_KEY_ORD, SortingRule.ASC);

                        // Workaround in modo che la lista punti al primo record, non all'ultimo
                        getForm().getUnitaDocumentarieList().getTable().first();
                    }

                }
            }
            // Carico la pagina di ricerca
            forwardToPublisher(pageToForward);
        } // RICERCA UNITA' DOCUMENTARIE DATI SPEC
        else if (getLastPublisher()
                .equals(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_DATI_SPEC)) {
            /*
             * Resetto la lista per evitare conflitti tra la ricerca avanzata (con selectedList) e
             * la ricerca versamenti annullati (lista "normale") aolo se non sto facendo download
             * altrimenti quando builda il csv non trova nulla nella table
             */
            if (!effettuaDownload) {
                getForm().getUnitaDocumentarieList().setTable(null);
            }
            // Pulisce lo stack di id unità documentarie, necessario per il caricamento delle ud
            // collegate
            getSession().setAttribute("idUdStack", new ArrayList<BigDecimal>());
            final FiltriUnitaDocumentarieDatiSpec filtri = getForm()
                    .getFiltriUnitaDocumentarieDatiSpec();

            int numTipiUdAbil = getForm().getFiltriUnitaDocumentarieDatiSpec()
                    .getNm_tipo_unita_doc().getDecodeMap().keySet().size();
            int numTipiDocAbil = getForm().getFiltriUnitaDocumentarieDatiSpec().getNm_tipo_doc()
                    .getDecodeMap().keySet().size();

            // Esegue la post dei filtri compilati
            filtri.post(getRequest());

            boolean almenoUnTipoUdSelPerRicercaDatiSpec = false;
            boolean almenoUnTipoDocSelPerRicercaDatiSpec = false;
            // Recupero l'informazione se l'utente ha selezionato almeno un TIPO UNITA DOC: mi
            // servirà in fase di
            // costruzione della query sui dati specifici
            // <= infatti se il numero è uguale significa che li ho selezionati tutti e dunque
            // compariranno i filtri
            // dati specifici (che dunque non è il caso di come se non avessi selezionato nessuno)
            if (getForm().getFiltriUnitaDocumentarieDatiSpec().getNm_tipo_unita_doc().parse()
                    .size() >= 1
                    && getForm().getFiltriUnitaDocumentarieDatiSpec().getNm_tipo_unita_doc().parse()
                            .size() <= numTipiUdAbil) {
                almenoUnTipoUdSelPerRicercaDatiSpec = true;
            }
            // Recupero l'informazione se l'utente ha selezionato almeno un TIPO DOCUMENTO: mi
            // servirà in fase di
            // costruzione della query sui dati specifici
            // <= infatti se il numero è uguale significa che li ho selezionati tutti e dunque
            // compariranno i filtri
            // dati specifici (che dunque non è il caso di come se non avessi selezionato nessuno)
            if (getForm().getFiltriUnitaDocumentarieDatiSpec().getNm_tipo_doc().parse().size() >= 1
                    && getForm().getFiltriUnitaDocumentarieDatiSpec().getNm_tipo_doc().parse()
                            .size() <= numTipiDocAbil) {
                almenoUnTipoDocSelPerRicercaDatiSpec = true;
            }

            String pageToRedirect = Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_DATI_SPEC;
            // Valida i filtri per verificare quelli obbligatori e che siano del tipo corretto
            if (filtri.validate(getMessageBox())) {
                // Valida i campi di ricerca
                UnitaDocumentarieValidator validator = new UnitaDocumentarieValidator(
                        getMessageBox());

                //
                // if (!getMessageBox().hasError()) {
                // // Controllo l'obbligatorietà di anno o range anni di chiave unità documentaria
                // validator.controllaPresenzaAnno(filtri.getAa_key_unita_doc().parse(),
                // filtri.getAa_key_unita_doc_da().parse(), filtri.getAa_key_unita_doc_a().parse());
                // }
                Set<String> cdRegistroKeyUnitaDocSetPerRicerca = getValoriForQueryFromFiltroCdRegistroMultiselect(
                        filtri.getCd_registro_key_unita_doc());
                List<BigDecimal> idTipoUnitaDocListPerRicerca = getValoriForQueryFromFiltroIdTipoUdMultiselect(
                        filtri.getNm_tipo_unita_doc());
                List<BigDecimal> idTipoDocListPerRicerca = getValoriForQueryFromFiltroIdTipoDocMultiselect(
                        filtri.getNm_tipo_doc());

                // Valida i campi di Range di chiavi unitÃ documentaria
                Object[] chiavi = null;
                if (!getMessageBox().hasError()) {
                    String[] registro = Arrays.copyOf(
                            filtri.getCd_registro_key_unita_doc().getDecodedValues().toArray(),
                            filtri.getCd_registro_key_unita_doc().getDecodedValues()
                                    .toArray().length,
                            String[].class);
                    chiavi = validator.validaChiaviUnitaDocRicUdDatiSpec(registro,
                            filtri.getAa_key_unita_doc_da().parse(),
                            filtri.getAa_key_unita_doc_a().parse(),
                            filtri.getCd_key_unita_doc_da().parse(),
                            filtri.getCd_key_unita_doc_a().parse());
                }

                // Ricavo la Lista Dati Specifici compilati a video
                List<DecCriterioDatiSpecBean> listaDatiSpecOnLine = (ArrayList) getSession()
                        .getAttribute("listaDatiSpecOnLine") != null
                                ? (ArrayList) getSession().getAttribute("listaDatiSpecOnLine")
                                : new ArrayList();

                boolean entitaPresente = true;
                if (listaDatiSpecOnLine.isEmpty()) {
                    getMessageBox().addError(
                            "Deve essere selezionata almeno un'entità con dati specifici");
                    entitaPresente = false;
                }

                boolean filtroPresente = false;
                if (entitaPresente) {
                    for (DecCriterioDatiSpecBean ds : listaDatiSpecOnLine) {
                        if (ds.getTiOper() != null) {
                            filtroPresente = true;
                        }
                    }

                    if (!filtroPresente) {
                        getMessageBox().addError(
                                "Deve essere compilato almeno un filtro su un dato specifico");
                    }
                }

                if (!getMessageBox().hasError()) {
                    // La validazione non ha riportato errori.
                    // Setto i filtri di chiavi unità documentaria impostando gli eventuali valori
                    // di default
                    if (chiavi != null && chiavi.length == 5) {
                        filtri.getAa_key_unita_doc_da().setValue(
                                chiavi[1] != null ? ((BigDecimal) chiavi[1]).toString() : null);
                        filtri.getAa_key_unita_doc_a().setValue(
                                chiavi[2] != null ? ((BigDecimal) chiavi[2]).toString() : null);
                        filtri.getCd_key_unita_doc_da()
                                .setValue(chiavi[3] != null ? (String) chiavi[3] : null);
                        filtri.getCd_key_unita_doc_a()
                                .setValue(chiavi[4] != null ? (String) chiavi[4] : null);
                    }

                    boolean addSerie = getForm().getUnitaDocumentariePerSerie()
                            .getId_contenuto_serie().parse() != null;
                    boolean addRichAnnulVers = getForm().getUnitaDocumentariePerRichAnnulVers()
                            .getId_rich_annul_vers().parse() != null;

                    if (effettuaDownload) {
                        AroVRicUnitaDocTableBean tb = udHelper
                                .getAroVRicUnitaDocRicDatiSpecViewBeanNoLimit(filtri,
                                        idTipoUnitaDocListPerRicerca,
                                        cdRegistroKeyUnitaDocSetPerRicerca, idTipoDocListPerRicerca,
                                        listaDatiSpecOnLine, getIdStrut(),
                                        addSerie || addRichAnnulVers,
                                        almenoUnTipoUdSelPerRicercaDatiSpec,
                                        almenoUnTipoDocSelPerRicercaDatiSpec);
                        if (!getMessageBox().hasError()) {
                            // MAC#39494 - Correzione metodo di generazione file in fase di
                            // esportazione di una ricerca
                            // File tmpFile = new File(System.getProperty("java.io.tmpdir"),
                            // "Contenuto_ricerca_unita_documentarie.csv");
                            try {
                                // MAC#39494 - Correzione metodo di generazione file in fase di
                                // esportazione di una ricerca
                                Path tmpPath = Files.createTempFile(
                                        "Contenuto_ricerca_unita_documentarie_", ".csv");
                                File tmpFile = tmpPath.toFile();
                                ActionUtils.buildCsvString(getForm().getUnitaDocumentarieList(), tb,
                                        AroVRicUnitaDocTableBean.TABLE_DESCRIPTOR, tmpFile);
                                getRequest().setAttribute(
                                        WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(),
                                        getControllerName());

                                // MAC#39494 - Correzione metodo di generazione file in fase di
                                // esportazione di una ricerca
                                String nomeFile = tmpFile.getName();
                                String nomeFinale = nomeFile.substring(0,
                                        nomeFile.lastIndexOf("_") - 1) + ".csv";
                                getSession().setAttribute(
                                        WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(),
                                        nomeFinale);

                                getSession().setAttribute(
                                        WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(),
                                        tmpFile.getPath());
                                getSession().setAttribute(
                                        WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
                                        Boolean.toString(true));
                                getSession().setAttribute(
                                        WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                                        "text/csv");
                            } catch (IOException ex) {
                                log.error("Errore in download "
                                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                                getMessageBox().addError(
                                        "Errore inatteso nella preparazione del download");
                            }
                        }

                        if (getMessageBox().hasError()) {
                            pageToRedirect = getLastPublisher();
                        } else {
                            pageToRedirect = Application.Publisher.DOWNLOAD_PAGE;
                        }

                    } else {
                        AroVRicUnitaDocTableBean tb = udHelper
                                .getAroVRicUnitaDocRicDatiSpecViewBeanNoLimit(filtri,
                                        idTipoUnitaDocListPerRicerca,
                                        cdRegistroKeyUnitaDocSetPerRicerca, idTipoDocListPerRicerca,
                                        listaDatiSpecOnLine, getIdStrut(),
                                        addSerie || addRichAnnulVers,
                                        almenoUnTipoUdSelPerRicercaDatiSpec,
                                        almenoUnTipoDocSelPerRicercaDatiSpec);
                        // Carico la tabella con i filtri impostati
                        getForm().getUnitaDocumentarieList().setTable(tb);
                        getForm().getUnitaDocumentarieList().getTable().setPageSize(10);
                        // Aggiungo alla lista una regola di ordinamento
                        getForm().getUnitaDocumentarieList().getTable().addSortingRule(
                                AroVRicUnitaDocTableDescriptor.COL_DS_KEY_ORD, SortingRule.ASC);

                        /* Salvo i filtri per eventuale creazione criteri di raggruppamento */
                        getSession().setAttribute("filtriUD", filtri);

                        // Workaround in modo che la lista punti al primo record, non all'ultimo
                        getForm().getUnitaDocumentarieList().getTable().first();
                    }
                }
            }
            // Carico la pagina di ricerca
            forwardToPublisher(pageToRedirect);

        } // RICERCA UNITA' DOCUMENTARIE AVANZATA
        else {
            /*
             * Resetto la lista per evitare conflitti tra la ricerca avanzata (con selectedList) e
             * la ricerca versamenti annullati (lista "normale") aolo se non sto facendo download
             * altrimenti quando builda il csv non trova nulla nella table
             */
            if (!effettuaDownload) {
                getForm().getUnitaDocumentarieList().setTable(null);
            }
            // Pulisce lo stack di id unità documentarie, necessario per il caricamento delle ud
            // collegate
            getSession().setAttribute("idUdStack", new ArrayList<BigDecimal>());
            final FiltriUnitaDocumentarieAvanzata filtri = getForm()
                    .getFiltriUnitaDocumentarieAvanzata();
            final FiltriCollegamentiUnitaDocumentarie filtriCollegamenti = getForm()
                    .getFiltriCollegamentiUnitaDocumentarie();
            final FiltriFirmatariUnitaDocumentarie filtriFirmatari = getForm()
                    .getFiltriFirmatariUnitaDocumentarie();
            final FiltriComponentiUnitaDocumentarie compfiltri = getForm()
                    .getFiltriComponentiUnitaDocumentarie();
            final FiltriFascicoliUnitaDocumentarie fascfiltri = getForm()
                    .getFiltriFascicoliUnitaDocumentarie();
            // Esegue la post dei filtri compilati
            filtri.post(getRequest());
            filtriCollegamenti.post(getRequest());
            filtriFirmatari.post(getRequest());
            compfiltri.post(getRequest());
            fascfiltri.post(getRequest());
            String pageToRedirect = Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_AVANZATA;
            // Valida i filtri per verificare quelli obbligatori e che siano del tipo corretto
            if (filtri.validate(getMessageBox()) && filtriCollegamenti.validate(getMessageBox())
                    && compfiltri.validate(getMessageBox())
                    && fascfiltri.validate(getMessageBox())) {
                // Valida i campi di ricerca
                UnitaDocumentarieValidator validator = new UnitaDocumentarieValidator(
                        getMessageBox());
                Date[] dateAcquisizioneValidate = null;
                if (!getMessageBox().hasError()) {
                    // Valido i filtri data creazione da - a restituendo le date comprensive di
                    // orario
                    dateAcquisizioneValidate = validator.validaDate(
                            filtri.getDt_acquisizione_unita_doc_da().parse(),
                            filtri.getOre_dt_acquisizione_unita_doc_da().parse(),
                            filtri.getMinuti_dt_acquisizione_unita_doc_da().parse(),
                            filtri.getDt_acquisizione_unita_doc_a().parse(),
                            filtri.getOre_dt_acquisizione_unita_doc_a().parse(),
                            filtri.getMinuti_dt_acquisizione_unita_doc_a().parse(),
                            filtri.getDt_acquisizione_unita_doc_da().getHtmlDescription(),
                            filtri.getDt_acquisizione_unita_doc_a().getHtmlDescription());
                }

                Date[] dateUnitaDocValidate = null;
                if (!getMessageBox().hasError()) {
                    dateUnitaDocValidate = validator.validaDate(
                            filtri.getDt_reg_unita_doc_da().parse(), null, null,
                            filtri.getDt_reg_unita_doc_a().parse(), null, null,
                            filtri.getDt_reg_unita_doc_da().getHtmlDescription(),
                            filtri.getDt_reg_unita_doc_a().getHtmlDescription());
                }

                if (!getMessageBox().hasError()) {
                    // Controllo l'obbligatorietÃ di anno o range anni di chiave unitÃ documentaria
                    validator.controllaPresenzaAnno(filtri.getAa_key_unita_doc().parse(),
                            filtri.getAa_key_unita_doc_da().parse(),
                            filtri.getAa_key_unita_doc_a().parse());
                }

                Set<String> cdRegistroKeyUnitaDocSetPerRicerca = getValoriForQueryFromFiltroCdRegistroMultiselect(
                        filtri.getCd_registro_key_unita_doc());
                List<BigDecimal> idTipoUnitaDocListPerRicerca = getValoriForQueryFromFiltroIdTipoUdMultiselect(
                        filtri.getNm_tipo_unita_doc());
                List<BigDecimal> idTipoDocListPerRicerca = getValoriForQueryFromFiltroIdTipoDocMultiselect(
                        filtri.getNm_tipo_doc());
                if (!getMessageBox().hasError()) {
                    // Controlla filtri collegamenti (in caso di javascript disattivato)
                    validator.controllaFiltriCollegamenti(
                            filtriCollegamenti.getCon_collegamento().parse(),
                            filtriCollegamenti.getCollegamento_risolto().parse(),
                            filtriCollegamenti.getDs_link_unita_doc().parse(),
                            filtriCollegamenti.getCd_registro_key_unita_doc_link().parse(),
                            filtriCollegamenti.getAa_key_unita_doc_link().parse(),
                            filtriCollegamenti.getCd_key_unita_doc_link().parse(),
                            filtriCollegamenti.getIs_oggetto_collegamento().parse(),
                            filtriCollegamenti.getDs_link_unita_doc_oggetto().parse());
                }

                // Valida i campi di Range di chiavi unitÃ documentaria
                Object[] chiavi = null;
                if (!getMessageBox().hasError()) {
                    String[] registro = Arrays.copyOf(
                            filtri.getCd_registro_key_unita_doc().getDecodedValues().toArray(),
                            filtri.getCd_registro_key_unita_doc().getDecodedValues()
                                    .toArray().length,
                            String[].class);
                    chiavi = validator.validaChiaviUnitaDocRicUd(registro,
                            filtri.getAa_key_unita_doc().parse(),
                            filtri.getCd_key_unita_doc().parse(),
                            filtri.getAa_key_unita_doc_da().parse(),
                            filtri.getAa_key_unita_doc_a().parse(),
                            filtri.getCd_key_unita_doc_da().parse(),
                            filtri.getCd_key_unita_doc_a().parse(),
                            filtri.getCd_key_unita_doc_contiene().parse());
                }

                /* Eseguo la validazione dei filtri componenti, se presenti */
                Date[] dateCreazioneComp = null;
                if (!getMessageBox().hasError()) {
                    dateCreazioneComp = validator.validaDate(
                            compfiltri.getDt_creazione_da().parse(),
                            compfiltri.getOre_dt_creazione_da().parse(),
                            compfiltri.getMinuti_dt_creazione_da().parse(),
                            compfiltri.getDt_creazione_a().parse(),
                            compfiltri.getOre_dt_creazione_a().parse(),
                            compfiltri.getMinuti_dt_creazione_a().parse(),
                            compfiltri.getDt_creazione_da().getHtmlDescription(),
                            compfiltri.getDt_creazione_a().getHtmlDescription());
                }

                if (!getMessageBox().hasError()) {
                    validator.validaOrdineDateOrari(compfiltri.getDt_scad_firma_comp_da().parse(),
                            compfiltri.getDt_scad_firma_comp_a().parse(),
                            compfiltri.getDt_scad_firma_comp_da().getHtmlDescription(),
                            compfiltri.getDt_scad_firma_comp_a().getHtmlDescription());
                }

                if (!getMessageBox().hasError()) {
                    validator.validaDimensioniKb(compfiltri.getNi_size_file_da().parse(),
                            compfiltri.getNi_size_file_a().parse());
                }

                /* Eseguo la validazione dei filtri fascicoli, se presenti */
                ChiaveBean chiaviFasc = null;
                if (!getMessageBox().hasError()) {
                    chiaviFasc = validator.validaChiavi(getForm().getFiltriFascicoli().getLegend(),
                            fascfiltri.getAa_fascicolo(), fascfiltri.getAa_fascicolo_da(),
                            fascfiltri.getAa_fascicolo_a(), fascfiltri.getCd_key_fascicolo(),
                            fascfiltri.getCd_key_fascicolo_da(),
                            fascfiltri.getCd_key_fascicolo_a());
                }

                if (!getMessageBox().hasError()) {
                    validator.validaOrdineDateOrari(fascfiltri.getDt_ape_fascicolo_da().parse(),
                            fascfiltri.getDt_ape_fascicolo_a().parse(),
                            fascfiltri.getDt_ape_fascicolo_da().getHtmlDescription(),
                            fascfiltri.getDt_ape_fascicolo_a().getHtmlDescription());
                }

                if (!getMessageBox().hasError()) {
                    validator.validaOrdineDateOrari(fascfiltri.getDt_chiu_fascicolo_da().parse(),
                            fascfiltri.getDt_chiu_fascicolo_a().parse(),
                            fascfiltri.getDt_chiu_fascicolo_da().getHtmlDescription(),
                            fascfiltri.getDt_chiu_fascicolo_a().getHtmlDescription());
                }

                if (!getMessageBox().hasError()) {
                    // La validazione non ha riportato errori.
                    // Setto i filtri di chiavi unitÃ documentaria impostando gli eventuali valori
                    // di default
                    if (chiavi != null && chiavi.length == 5) {
                        filtri.getAa_key_unita_doc_da().setValue(
                                chiavi[1] != null ? ((BigDecimal) chiavi[1]).toString() : null);
                        filtri.getAa_key_unita_doc_a().setValue(
                                chiavi[2] != null ? ((BigDecimal) chiavi[2]).toString() : null);
                        filtri.getCd_key_unita_doc_da()
                                .setValue(chiavi[3] != null ? (String) chiavi[3] : null);
                        filtri.getCd_key_unita_doc_a()
                                .setValue(chiavi[4] != null ? (String) chiavi[4] : null);
                    }

                    // Setto i filtri di chiavi fascicolo impostando gli eventuali valori di
                    // default.
                    // Controllo dove sono stati inseriti i filtri tra la chiave fascicolo singola e
                    // la chiave fascicolo
                    // per range
                    if (chiaviFasc != null) {
                        boolean range = false;

                        if (chiaviFasc.getAnnoDa() != null || chiaviFasc.getAnnoA() != null
                                || chiaviFasc.getNumeroDa() != null
                                || chiaviFasc.getNumeroA() != null) {
                            range = true;
                        }

                        if (range) {
                            fascfiltri.getAa_fascicolo_da()
                                    .setValue(chiaviFasc.getAnnoDa() != null
                                            ? chiaviFasc.getAnnoDa().toString()
                                            : null);
                            fascfiltri.getAa_fascicolo_a()
                                    .setValue(chiaviFasc.getAnnoA() != null
                                            ? chiaviFasc.getAnnoA().toString()
                                            : null);
                            fascfiltri.getCd_key_fascicolo_da()
                                    .setValue(chiaviFasc.getNumeroDa() != null
                                            ? chiaviFasc.getNumeroDa()
                                            : null);
                            fascfiltri.getCd_key_fascicolo_a()
                                    .setValue(chiaviFasc.getNumeroA() != null
                                            ? chiaviFasc.getNumeroA()
                                            : null);
                        }
                    }

                    // Elimino il record dei filtri dati specifici dalla tabella USR_FILTRO_ATTRIB
                    // Ricavo la Lista Dati Specifici compilati a video
                    List<DecCriterioDatiSpecBean> listaDatiSpecOnLine = (ArrayList) getSession()
                            .getAttribute("listaDatiSpecOnLine") != null
                                    ? (ArrayList) getSession().getAttribute("listaDatiSpecOnLine")
                                    : new ArrayList();
                    boolean addSerie = getForm().getUnitaDocumentariePerSerie()
                            .getId_contenuto_serie().parse() != null;
                    boolean addRichAnnulVers = getForm().getUnitaDocumentariePerRichAnnulVers()
                            .getId_rich_annul_vers().parse() != null;

                    if (effettuaDownload) {
                        AroVRicUnitaDocTableBean tb = udHelper
                                .getAroVRicUnitaDocRicAvanzataViewBeanNoLimit(filtri,
                                        idTipoUnitaDocListPerRicerca,
                                        cdRegistroKeyUnitaDocSetPerRicerca, idTipoDocListPerRicerca,
                                        listaDatiSpecOnLine, filtriCollegamenti, filtriFirmatari,
                                        compfiltri, fascfiltri, dateAcquisizioneValidate,
                                        dateUnitaDocValidate, dateCreazioneComp, getIdStrut(),
                                        addSerie || addRichAnnulVers);
                        if (!getMessageBox().hasError()) {
                            // MAC#39494 - Correzione metodo di generazione file in fase di
                            // esportazione di una ricerca
                            // File tmpFile = new File(System.getProperty("java.io.tmpdir"),
                            // "Contenuto_ricerca_unita_documentarie.csv");
                            try {
                                // MAC#39494 - Correzione metodo di generazione file in fase di
                                // esportazione di una ricerca
                                Path tmpPath = Files.createTempFile(
                                        "Contenuto_ricerca_unita_documentarie_", ".csv");
                                File tmpFile = tmpPath.toFile();

                                ActionUtils.buildCsvString(getForm().getUnitaDocumentarieList(), tb,
                                        AroVRicUnitaDocTableBean.TABLE_DESCRIPTOR, tmpFile);
                                getRequest().setAttribute(
                                        WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(),
                                        getControllerName());

                                // MAC#39494 - Correzione metodo di generazione file in fase di
                                // esportazione di una ricerca
                                String nomeFile = tmpFile.getName();
                                String nomeFinale = nomeFile.substring(0,
                                        nomeFile.lastIndexOf("_") - 1) + ".csv";
                                getSession().setAttribute(
                                        WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(),
                                        nomeFinale);

                                getSession().setAttribute(
                                        WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(),
                                        tmpFile.getPath());
                                getSession().setAttribute(
                                        WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
                                        Boolean.toString(true));
                                getSession().setAttribute(
                                        WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                                        "text/csv");
                            } catch (IOException ex) {
                                log.error("Errore in download "
                                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                                getMessageBox().addError(
                                        "Errore inatteso nella preparazione del download");
                            }
                        }

                        if (getMessageBox().hasError()) {
                            pageToRedirect = getLastPublisher();
                        } else {
                            pageToRedirect = Application.Publisher.DOWNLOAD_PAGE;
                        }

                    } else {
                        AroVRicUnitaDocTableBean tb = udHelper
                                .getAroVRicUnitaDocRicAvanzataViewBean(filtri,
                                        idTipoUnitaDocListPerRicerca,
                                        cdRegistroKeyUnitaDocSetPerRicerca, idTipoDocListPerRicerca,
                                        listaDatiSpecOnLine, filtriCollegamenti, filtriFirmatari,
                                        compfiltri, fascfiltri, dateAcquisizioneValidate,
                                        dateUnitaDocValidate, dateCreazioneComp, getIdStrut(),
                                        addSerie || addRichAnnulVers);
                        // Carico la tabella con i filtri impostati
                        getForm().getUnitaDocumentarieList().setTable(tb);
                        getForm().getUnitaDocumentarieList().getTable().setPageSize(10);
                        // Aggiungo alla lista una regola di ordinamento
                        getForm().getUnitaDocumentarieList().getTable().addSortingRule(
                                AroVRicUnitaDocTableDescriptor.COL_DS_KEY_ORD, SortingRule.ASC);

                        /* Salvo i filtri per eventuale creazione criteri di raggruppamento */
                        getSession().setAttribute("filtriUD", filtri);

                        // Workaround in modo che la lista punti al primo record, non all'ultimo
                        getForm().getUnitaDocumentarieList().getTable().first();
                    }
                }
            }
            // Carico la pagina di ricerca
            forwardToPublisher(pageToRedirect);
        }
        postLoad();
    }

    /**
     * Metodo scatenato al click del bottone di ricerca all'interno della pagina di ricerca
     * componenti
     *
     * @throws EMFError errore generico
     */
    @Override
    public void ricercaUD() throws EMFError {
        ricercaEDownload(false);
    }

    /**
     * Restituisce una lista contenente i valori degli idRegistroUnitaDoc abilitati da passare ad
     * una query a seconda deglla scelta nell'online dell'utente in un filtro Combo
     *
     */
    private List<String> getValoriForQueryFromFiltroCdRegistroCombo(
            ComboBox<BigDecimal> comboRegistro) throws EMFError {
        List<String> cdRegistroUnitaDocList = new ArrayList<>();
        String cdRegistroUnitaDoc = comboRegistro.getDecodedValue();
        /* Se ho il cdRegistroUnitaDoc, prendo quel valore */
        if (StringUtils.isNotBlank(cdRegistroUnitaDoc)) {
            cdRegistroUnitaDocList.add(cdRegistroUnitaDoc);
        } else {
            /*
             * Altrimenti recupera tutti e soli i cdRegistroUnitaDoc cui l'utente è abilitato (in
             * caso di combo registri vuota, ovvero nessuna abilitazione per l'utente, verrè
             * impostato a null il cdRegistroUnitaDoc)
             */
            DecodeMapIF mappaFiltriRegistri = comboRegistro.getDecodeMap();
            if (mappaFiltriRegistri.isEmpty()) {
                /*
                 * Se non sono abilitato a nulla, nella lista metto cd a null cosè in fase di
                 * ricerca non troverà nulla
                 */
                cdRegistroUnitaDocList.add(null);
            } else {
                Iterator itera = mappaFiltriRegistri.keySet().iterator();
                while (itera.hasNext()) {
                    BigDecimal key = (BigDecimal) itera.next();
                    cdRegistroUnitaDocList.add(mappaFiltriRegistri.getDescrizione(key));
                }
            }
        }
        return cdRegistroUnitaDocList;
    }

    /**
     * Restituisce una lista contenente i valori dei cdRegistroUnitaDoc abilitati da passare ad una
     * query a seconda della scelta nell'online dell'utente in un filtro MultiSelect
     *
     * @param multiselectRegistro
     *
     * @return
     *
     * @throws EMFError errore generico
     */
    private Set<String> getValoriForQueryFromFiltroCdRegistroMultiselect(
            MultiSelect<BigDecimal> multiselectRegistro) throws EMFError {
        /* Prendi i valori selezionati nella multiselect */
        Set<String> cdRegistriKeyUnitaDoc = multiselectRegistro.getDecodedValues();
        /* Se non è stato selezionato nulla, prendi tutti i registri abilitati */
        if (cdRegistriKeyUnitaDoc.isEmpty()) {
            DecodeMapIF mappaFiltriRegistri = multiselectRegistro.getDecodeMap();
            /*
             * Se non è presente alcun registro abilitato all'interno della multiselect aggiungi il
             * valore null in modo tale che la ricerca non trovi nulla per i registri
             */
            if (mappaFiltriRegistri.isEmpty()) {
                cdRegistriKeyUnitaDoc.add(null);
            } else {
                Iterator itera = mappaFiltriRegistri.keySet().iterator();
                while (itera.hasNext()) {
                    BigDecimal key = (BigDecimal) itera.next();
                    cdRegistriKeyUnitaDoc.add(mappaFiltriRegistri.getDescrizione(key));
                }
            }
        }
        return cdRegistriKeyUnitaDoc;
    }

    /**
     * Restituisce una lista contenente i valori degli idTipoUnitaDoc abilitati da passare ad una
     * query a seconda deglla scelta nell'online dell'utente in un filtro Combo
     *
     * @param comboTipoUd
     *
     * @return
     *
     * @throws EMFError errore generico
     */
    private List<BigDecimal> getValoriForQueryFromFiltroIdTipoUdCombo(
            ComboBox<BigDecimal> comboTipoUd) throws EMFError {
        List<BigDecimal> idTipoUnitaDocList = new ArrayList<>();
        BigDecimal idTipoUnitaDoc = comboTipoUd.parse();
        if (idTipoUnitaDoc != null) {
            idTipoUnitaDocList.add(idTipoUnitaDoc);
        } else {
            /*
             * Recupera tutti gli idTipoUnitaDoc cui l'utente è abilitato, che sono quelli presenti
             * nella combo
             */
            DecodeMapIF mappaFiltriTipiUd = comboTipoUd.getDecodeMap();
            /*
             * Se non sono abilitato a nulla, nella lista metto id 0 cosè in fase di ricerca non
             * troverè nulla
             */
            if (mappaFiltriTipiUd.isEmpty()) {
                idTipoUnitaDocList.add(new BigDecimal(BigInteger.ZERO));
            } else {
                Iterator itera = mappaFiltriTipiUd.keySet().iterator();
                while (itera.hasNext()) {
                    idTipoUnitaDocList.add((BigDecimal) itera.next());
                }
            }
        }
        return idTipoUnitaDocList;
    }

    /**
     * Restituisce una lista contenente i valori degli idTipoUnitaDoc abilitati da passare ad una
     * query a seconda della scelta nell'online dell'utente in un filtro MultiSelect
     *
     * @param multiselectTipoUd
     *
     * @return
     *
     * @throws EMFError errore generico
     */
    private List<BigDecimal> getValoriForQueryFromFiltroIdTipoUdMultiselect(
            MultiSelect<BigDecimal> multiselectTipoUd) throws EMFError {
        /* Prendi i valori selezionati nella multiselect */
        List<BigDecimal> idTipoUnitaDoc = multiselectTipoUd.parse();
        /* Se non è stato selezionato nulla, prendi tutti i tipi unita doc abilitati */
        if (idTipoUnitaDoc.isEmpty()) {
            DecodeMapIF mappaFiltriTipiUd = multiselectTipoUd.getDecodeMap();
            /*
             * Se non è presente alcun tipo ud abilitato all'interno della multiselect aggiungi il
             * valore null in modo tale che la ricerca non trovi nulla per i tipi ud
             */
            if (mappaFiltriTipiUd.isEmpty()) {
                idTipoUnitaDoc.add(new BigDecimal(BigInteger.ZERO));
            } else {
                Iterator itera = mappaFiltriTipiUd.keySet().iterator();
                while (itera.hasNext()) {
                    idTipoUnitaDoc.add((BigDecimal) itera.next());
                }
            }
        }
        return idTipoUnitaDoc;
    }

    /**
     * Restituisce una lista contenente i valori degli idTipoDoc abilitati da passare ad una query a
     * seconda della scelta nell'online dell'utente in un filtro Combo
     *
     * @param comboTipoDoc
     *
     * @return
     *
     * @throws EMFError errore generico
     */
    private List<BigDecimal> getValoriForQueryFromFiltroIdTipoDocCombo(
            ComboBox<BigDecimal> comboTipoDoc) throws EMFError {
        List<BigDecimal> idTipoDocList = new ArrayList<>();
        BigDecimal idTipoDoc = comboTipoDoc.parse();
        if (idTipoDoc != null) {
            idTipoDocList.add(idTipoDoc);
        } else {
            /*
             * Recupera tutti gli idTipoDoc cui l'utente è abilitato, che sono quelli presenti nella
             * combo
             */
            DecodeMapIF mappaFiltriTipiDoc = comboTipoDoc.getDecodeMap();
            /*
             * Se non sono abilitato a nulla, nella lista metto id 0 cosè in fase di ricerca non
             * troverè nulla
             */
            if (mappaFiltriTipiDoc.isEmpty()) {
                idTipoDocList.add(new BigDecimal(BigInteger.ZERO));
            } else {
                Iterator itera = mappaFiltriTipiDoc.keySet().iterator();
                while (itera.hasNext()) {
                    idTipoDocList.add((BigDecimal) itera.next());
                }
            }
        }
        return idTipoDocList;
    }

    /**
     * Restituisce una lista contenente i valori degli idTipoDoc abilitati da passare ad una query a
     * seconda della scelta nell'online dell'utente in un filtro MultiSelect
     *
     * @param multiselectTipoDoc
     *
     * @return
     *
     * @throws EMFError errore generico
     */
    private List<BigDecimal> getValoriForQueryFromFiltroIdTipoDocMultiselect(
            MultiSelect<BigDecimal> multiselectTipoDoc) throws EMFError {
        /* Prendi quello che è stato selezionato a video */
        List<BigDecimal> idTipoDocList = multiselectTipoDoc.parse();
        /* Se non è stato selezionato nulla */
        if (idTipoDocList.isEmpty()) {
            DecodeMapIF mappaFiltriTipiDoc = multiselectTipoDoc.getDecodeMap();
            /*
             * Se non è presente alcun tipo doc abilitato all'interno della multiselect aggiungi il
             * valore null in modo tale che la ricerca non trovi nulla per i tipi doc
             */
            if (mappaFiltriTipiDoc.isEmpty()) {
                idTipoDocList.add(new BigDecimal(BigInteger.ZERO));
            } else {
                Iterator itera = mappaFiltriTipiDoc.keySet().iterator();
                while (itera.hasNext()) {
                    idTipoDocList.add((BigDecimal) itera.next());
                }
            }
        }
        return idTipoDocList;
    }

    /**
     * Metodo invocato sul bottone di dettaglio/modifica di una riga della lista unità documentarie,
     * esegue il caricamento della pagina per visualizzare il dettaglio, in seguito alla
     * loadDettaglio
     *
     * @throws EMFError errore generico
     */
    @Override
    public void dettaglioOnClick() throws EMFError {
        // Controllo per quale tabella Ã¨ stato invocato il metodo
        if (getRequest().getParameter("table") != null) {
            if (getRequest().getParameter("table")
                    .equals(getForm().getComponentiList().getName())) {
                // Lista componenti - eseguo una redirect all'action ComponentiAction, per
                // visualizzarne il dettaglio
                // direttamente
                // Carico la table di UnitaDocumentarieForm in ComponentiForm, in modo che la
                // loadDettaglio di
                // ComponentiAction
                // sia in grado di caricare i dati
                ComponentiForm form = new ComponentiForm();
                form.getComponentiList().setTable(getForm().getComponentiList().getTable());
                redirectToAction(Application.Actions.COMPONENTI,
                        "?operation=listNavigationOnClick&navigationEvent="
                                + ListAction.NE_DETTAGLIO_VIEW + "&table="
                                + ComponentiForm.ComponentiList.NAME + "&riga="
                                + getForm().getComponentiList().getTable().getCurrentRowIndex(),
                        form);
            } else if (getRequest().getParameter("table")
                    .equals(getForm().getVolumiList().getName())) {
                /*
                 * Lista volumi - eseguo una redirect all'action VolumiAction, per visualizzarne il
                 * dettaglio direttamente Carico la table di UnitaDocumentarieForm in VolumiForm, in
                 * modo che la loadDettaglio di VolumiAction sia in grado di caricare i dati
                 */
                VolumiForm volumiForm = new VolumiForm();
                volumiForm.getVolumiList().setTable(getForm().getVolumiList().getTable());
                redirectToAction(Application.Actions.VOLUMI,
                        "?operation=listNavigationOnClick&navigationEvent="
                                + ListAction.NE_DETTAGLIO_VIEW + "&table="
                                + VolumiForm.VolumiList.NAME + "&riga="
                                + getForm().getVolumiList().getTable().getCurrentRowIndex(),
                        volumiForm);
            } else if (getRequest().getParameter("table")
                    .equals(getForm().getElenchiVersamentoList().getName())) {
                ElenchiVersamentoForm elenchiForm = new ElenchiVersamentoForm();
                AroVLisElvVerTableBean elenco = (AroVLisElvVerTableBean) getForm()
                        .getElenchiVersamentoList().getTable();
                ElvVRicElencoVersTableBean elenco2 = new ElvVRicElencoVersTableBean();
                elenco2.load(elenco);
                elenchiForm.getElenchiVersamentoList().setTable(elenco2);
                redirectToAction(Application.Actions.ELENCHI_VERSAMENTO,
                        "?operation=listNavigationOnClick&navigationEvent="
                                + ListAction.NE_DETTAGLIO_VIEW + "&table="
                                + ElenchiVersamentoForm.ElenchiVersamentoList.NAME + "&riga="
                                + getForm().getElenchiVersamentoList().getTable()
                                        .getCurrentRowIndex(),
                        elenchiForm);
            } else if (getRequest().getParameter("table")
                    .equals(getForm().getSerieAppartenenzaList().getName())) {
                SerieUDForm serieUdForm = new SerieUDForm();
                BaseTable serie = (BaseTable) getForm().getSerieAppartenenzaList().getTable();
                SerVRicSerieUdTableBean serie2 = new SerVRicSerieUdTableBean();
                serie2.load(serie);
                serieUdForm.getSerieList().setTable(serie2);
                redirectToAction(Application.Actions.SERIE_UD,
                        "?operation=listNavigationOnClick&navigationEvent="
                                + ListAction.NE_DETTAGLIO_VIEW + "&table="
                                + SerieUDForm.SerieList.NAME + "&riga=" + getForm()
                                        .getSerieAppartenenzaList().getTable().getCurrentRowIndex(),
                        serieUdForm);
            } else if (getRequest().getParameter("table")
                    .equals(getForm().getFascicoliAppartenenzaList().getName())) {
                FascicoliForm fascicoliForm = new FascicoliForm();
                AroVLisFascTableBean fascicoli = (AroVLisFascTableBean) getForm()
                        .getFascicoliAppartenenzaList().getTable();
                FasVVisFascicoloTableBean fascicoli2 = new FasVVisFascicoloTableBean();
                fascicoli2.load(fascicoli);
                fascicoliForm.getFascicoliList().setTable(fascicoli2);

                if (!((AroVLisFascRowBean) getForm().getFascicoliAppartenenzaList().getTable()
                        .getCurrentRow()).getTiStatoConservazione()
                        .equals(it.eng.parer.entity.constraint.FasFascicolo.TiStatoConservazione.ANNULLATO
                                .name())) {
                    redirectToAction(Application.Actions.FASCICOLI,
                            "?operation=listNavigationOnClick&navigationEvent="
                                    + ListAction.NE_DETTAGLIO_VIEW + "&table="
                                    + FascicoliForm.FascicoliList.NAME + "&riga="
                                    + getForm().getFascicoliAppartenenzaList().getTable()
                                            .getCurrentRowIndex(),
                            fascicoliForm);
                } else {
                    getMessageBox().addError(
                            "Operazione non possibile in quanto il fascicolo ha stato di conservazione = ANNULLATO");
                    forwardToPublisher(getLastPublisher());
                }

            } else if (getRequest().getParameter("table")
                    .equals(getForm().getUnitaDocumentarieList().getName())) {
                // Lista unitÃ documentarie
                getForm().getUnitaDocumentarieDettaglioTabs().setCurrentTab(
                        getForm().getUnitaDocumentarieDettaglioTabs().getInfoPrincipaliUD());
                getForm().getUnitaDocumentarieDettaglioListsTabs().setCurrentTab(
                        getForm().getUnitaDocumentarieDettaglioListsTabs().getListaDocumentiUD());
                forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
            } else if (getRequest().getParameter("table")
                    .equals(getForm().getCollegamentiList().getName())) {
                // LISTA COLLEGAMENTI
                // Carico qui onde evitare che, scorrendo la lista dei collegamenti con il
                // paginatore,
                // venga lanciata la load dettaglio che caricherebbe controllo del flag risolto
                AroVLisLinkUnitaDocRowBean av = getAroVLisLinkUnitaDocRowBean();

                // Ottengo l'id dell'unitÃ documentaria collegata
                getSession().setAttribute("idud", av.getIdUnitaDoc());
                if (av.getFlRisolto().equals("1")) {
                    // MEV#17625
                    AroVRicUnitaDocRowBean aroVRicUnitaDocRowBean = udHelper
                            .getAroVRicUnitaDocRowBean(av.getIdUnitaDocColleg(), null, null);
                    if (!aroVRicUnitaDocRowBean.getTiStatoConservazione()
                            .equals(CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name())) {
                        // Se il flag risolto Ã¨ a 1 (true) e l'UD non è ANNULLATA carico il
                        // dettaglio disponibile,
                        // aggiungendo
                        // allo stack delle unitÃ documentarie l'id dell'UD attuale
                        List<BigDecimal> idUdStack = getIdUdStack();
                        idUdStack.add(av.getIdUnitaDoc());
                        getSession().setAttribute("idUdStack", idUdStack);
                        getDettaglioUD(av.getIdUnitaDocColleg());
                    } else {
                        getMessageBox().addError(
                                "Operazione non possibile in quanto l'unità documentaria ha stato di conservazione = ANNULLATA");
                    }
                } else {
                    getMessageBox().addError(
                            "Dettaglio non disponibile per questa Unita' Documentaria (collegamento non risolto)");
                }

                // Lista collegamenti unitÃ documentarie
                // devo aver cliccato sul un link dettaglio valido
                if (!getMessageBox().hasError()) {
                    getForm().getUnitaDocumentarieDettaglioTabs().setCurrentTab(
                            getForm().getUnitaDocumentarieDettaglioTabs().getInfoPrincipaliUD());
                    getForm().getUnitaDocumentarieDettaglioListsTabs().setCurrentTab(getForm()
                            .getUnitaDocumentarieDettaglioListsTabs().getListaCollegamentiUD());
                    if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)) {
                        SessionManager.addPrevExecutionToHistory(getSession(), false, true);
                    }
                    forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
                }
            } else if (getRequest().getParameter("table")
                    .equals(getForm().getDocumentiUDList().getName())) {
                // Lista documenti: principale, allegati, annessi, annotazioni
                getForm().getDocumentiDettaglioTabs()
                        .setCurrentTab(getForm().getDocumentiDettaglioTabs().getInfoPrincipali());
                getForm().getDocumentiDettaglioListsTabs().setCurrentTab(
                        getForm().getDocumentiDettaglioListsTabs().getListaComponenti());
                // apri le sezioni
                getForm().getProfiloDoc().setLoadOpened(true);
                getForm().getDatiFiscali().setLoadOpened(false);
                forwardToPublisher(Application.Publisher.DOCUMENTI_UNITA_DOCUMENTARIE_DETAIL);
            } else if (getRequest().getParameter("table")
                    .equals(getForm().getIndiciAIPList().getName())) {
                forwardToPublisher(Application.Publisher.INDICE_AIP_DETAIL);
            } else if (getRequest().getParameter("table")
                    .equals(getForm().getAggiornamentiMetadatiList().getName())) {
                // Lista aggiornamenti
                getForm().getAggiornamentiDettaglioTabs().setCurrentTab(
                        getForm().getAggiornamentiDettaglioTabs().getInfoPrincipaliUpd());
                getForm().getAggiornamentiDettaglioListsTabs().setCurrentTab(
                        getForm().getAggiornamentiDettaglioListsTabs().getListaDocAggiornati());
                // apri le sezioni
                getForm().getProfiloUpd().setLoadOpened(true);
                forwardToPublisher(Application.Publisher.AGGIORNAMENTI_METADATI_UDDETAIL);
            } else if (getRequest().getParameter("table")
                    .equals(getForm().getNoteList().getName())) {
                if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                        || getNavigationEvent().equals(ListAction.NE_NEXT)
                        || getNavigationEvent().equals(ListAction.NE_PREV)) {
                    forwardToPublisher(Application.Publisher.NOTA_UD_DETAIL);
                }
            }
        }
    }

    /**
     * Metodo utilizzato dal framework quando clicco sul tasto "Indietro" nella barra di scorrimento
     * del dettaglio di un record
     *
     * @throws EMFError errore generico
     */
    @Override
    public void elencoOnClick() throws EMFError {
        ExecutionHistory executionHistory = SessionManager.getLastExecutionHistory(getSession());
        if (executionHistory != null && executionHistory.isAction()
                && executionHistory.getName().equals(getControllerName())) {
            // è una redirectToAction ma il controllerName è se stesso?? Beh, sto arrivando da
            // un'altra applicazione
            unitaDocumentarieRicercaAvanzata();
            SessionManager.clearActionHistory(getSession());
        } else {
            goBack();
        }
    }

    @Override
    public void insertDettaglio() throws EMFError {
        if (getTableName().equals(getForm().getNoteList().getName())) {
            BigDecimal idUnitaDoc = getForm().getUnitaDocumentarieDetail().getId_unita_doc()
                    .parse();

            BaseRow tmpRow = new BaseRow();
            getForm().getUnitaDocumentarieDetail().copyToBean(tmpRow);
            getForm().getDatiUDDetail().copyFromBean(tmpRow);

            DateFormat formato = new SimpleDateFormat(WebConstants.DATE_FORMAT_HOUR_MINUTE_TYPE);

            getForm().getNotaDetail().clear();
            getForm().getNotaDetail().setViewMode();

            getForm().getNotaDetail().getPg_nota_unita_doc().setValue(null);
            getForm().getNotaDetail().getNm_userid_nota().setValue(getUser().getUsername());
            getForm().getNotaDetail().getDt_nota_unita_doc()
                    .setValue(formato.format(Calendar.getInstance().getTime()));
            getForm().getNotaDetail().getId_tipo_nota_unita_doc()
                    .setDecodeMap(DecodeMap.Factory.newInstance(
                            udHelper.getDecTipoNotaUnitaDocNotInUnitaDocTableBean(
                                    getUser().getIdUtente(), idUnitaDoc),
                            "id_tipo_nota_unita_doc", "ds_tipo_nota_unita_doc"));
            getForm().getNotaDetail().getId_tipo_nota_unita_doc().setEditMode();
            getForm().getNotaDetail().getDs_nota_unita_doc().setEditMode();

            getForm().getNoteList().setStatus(BaseElements.Status.insert);
            getForm().getNotaDetail().setStatus(BaseElements.Status.insert);

            forwardToPublisher(Application.Publisher.NOTA_UD_DETAIL);
        }
    }

    /**
     * Restituisce il row bean relativo all'unitÃ documentaria collegata selezionata
     *
     * @return AroVLisLinkUnitaDocRowBean
     */
    private AroVLisLinkUnitaDocRowBean getAroVLisLinkUnitaDocRowBean() {
        return (AroVLisLinkUnitaDocRowBean) getForm().getCollegamentiList().getTable()
                .getCurrentRow();
    }

    /**
     * Metodo invocato sul bottone di dettaglio/modifica su una riga delle liste seguenti, esegue il
     * caricamento dei dati della riga selezionata per visualizzare il dettaglio
     *
     * @throws EMFError errore generico
     */
    @Override
    public void loadDettaglio() throws EMFError {
        // Controllo per quale tabella Ã¨ stato invocato il metodo
        String lista = getRequest().getParameter("table");
        if (lista != null) {
            String sistemaConservazione = configurationHelper
                    .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
            if (getRequest().getParameter("table")
                    .equals(getForm().getUnitaDocumentarieList().getName())) {
                // UNITA' DOCUMENTARIA
                BigDecimal idUnitaDoc = getForm().getUnitaDocumentarieList().getTable()
                        .getCurrentRow().getBigDecimal("id_unita_doc");
                if (idUnitaDoc != null) {
                    getDettaglioUD(idUnitaDoc);
                } else if (getNavigationEvent().equals(ListAction.NE_NEXT)
                        || getNavigationEvent().equals(ListAction.NE_PREV)) {
                    int rowIndex = getForm().getUnitaDocumentarieList().getTable()
                            .getCurrentRowIndex();
                    int size = getForm().getUnitaDocumentarieList().getTable().size();

                    if (rowIndex < (size - 1)) {
                        listNavigationOnClick(getTableName(), getNavigationEvent(), getRiga(),
                                getForceReload());
                    } else {
                        String invertedNav = getNavigationEvent().equals(ListAction.NE_NEXT)
                                ? ListAction.NE_PREV
                                : ListAction.NE_NEXT;
                        listNavigationOnClick(getTableName(), invertedNav, getRiga(),
                                getForceReload());
                    }
                }
            } else if (getRequest().getParameter("table")
                    .equals(getForm().getDocumentiUDList().getName())) {
                // DOCUMENTO
                // Ottengo l'id documento
                BigDecimal iddoc = ((AroVLisDocRowBean) getForm().getDocumentiUDList().getTable()
                        .getCurrentRow()).getIdDoc();
                getSession().setAttribute("iddoc", iddoc);
                AroVVisDocIamRowBean documentoAaaRB = udHelper.getAroVVisDocIamRowBean(iddoc);
                // Se Ã¨ diverso da documento principale, aggiungo il progressivo
                String tipoDoc = documentoAaaRB.getTiDoc();
                if (!documentoAaaRB.getTiDoc().equals("PRINCIPALE")) {
                    documentoAaaRB
                            .setTiDoc(documentoAaaRB.getTiDoc() + " " + documentoAaaRB.getPgDoc());
                }

                // Copio i dati del documento sulla form di dettaglio
                getForm().getDocumentiUnitaDocumentarieDetail().copyFromBean(documentoAaaRB);

                // Se provengo dal dettaglio elenco o dettaglio componente popolo la form di
                // dettaglio unità
                // documentaria con i dati dell'ud
                if (getForm().getUnitaDocumentarieList().getTable() == null) {
                    getDettaglioUD(documentoAaaRB.getIdUnitaDoc());
                }

                // Di default nascondo il bottone per il download degli xml e i relativi tab
                getForm().getDocumentiUnitaDocumentarieDetail().getScarica_xml_doc().setViewMode();
                getForm().getDocumentiUnitaDocumentarieDetail().getScarica_xml_doc()
                        .setDisableHourGlass(true);
                getForm().getDocumentiDettaglioTabs().getXMLRichiestaDoc().setHidden(true);
                getForm().getDocumentiDettaglioTabs().getXMLRispostaDoc().setHidden(true);
                getForm().getDocumentiDettaglioTabs().getXMLRapportoDoc().setHidden(true);

                /*
                 * Per i documenti AGGIUNTI devo ricavare anche gli xml di richiesta e risposta.
                 * Quindi, controllo che il documento in questione sia o meno aggiunto, confrontando
                 * la sua data di creazione con quella di creazione UD. Se quest'ultima è
                 * antecedente la data di creazione del documento, significa che esso è stato
                 * AGGIUNTO e dunque ne mostro i valori di xml richiesta e risposta in quanto
                 * sicuramente presenti
                 */
                Date dataUD = getForm().getUnitaDocumentarieDetail().getDt_creazione().parse();
                if (dataUD != null && documentoAaaRB.getDtCreazione() != null) {
                    if (dataUD.before(documentoAaaRB.getDtCreazione())) {
                        // load from O.S.
                        udHelper.addXmlDocFromOStoAroVVisDocIamRowBean(documentoAaaRB);
                        // Formatto gli xml di richiesta e risposta in modo tale che compaiano
                        // on-line in versione
                        // "pretty-print"
                        String xmlrich = documentoAaaRB.getBlXmlRichDoc();
                        String xmlrisp = documentoAaaRB.getBlXmlRispDoc();
                        String xmlrapp = documentoAaaRB.getBlXmlRappDoc();
                        // Se il documento ha presenti gli xml di richiesta e risposta, li presento
                        XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
                        if (xmlrich != null && xmlrisp != null) {
                            xmlrich = formatter.prettyPrintWithDOM3LS(xmlrich);
                            xmlrisp = formatter.prettyPrintWithDOM3LS(xmlrisp);
                            getForm().getDocumentiUnitaDocumentarieDetail().getBl_xml_rich_doc()
                                    .setValue(xmlrich);
                            getForm().getDocumentiUnitaDocumentarieDetail().getBl_xml_risp_doc()
                                    .setValue(xmlrisp);
                            getForm().getDocumentiUnitaDocumentarieDetail().getScarica_xml_doc()
                                    .setEditMode();
                            getForm().getDocumentiUnitaDocumentarieDetail().getScarica_xml_doc()
                                    .setDisableHourGlass(true);
                            getForm().getDocumentiDettaglioTabs().getXMLRichiestaDoc()
                                    .setHidden(false);
                            getForm().getDocumentiDettaglioTabs().getXMLRispostaDoc()
                                    .setHidden(false);
                        }
                        if (xmlrapp != null) {
                            xmlrapp = formatter.prettyPrintWithDOM3LS(xmlrapp);
                            getForm().getDocumentiUnitaDocumentarieDetail().getBl_xml_rapp_doc()
                                    .setValue(xmlrapp);
                            getForm().getDocumentiDettaglioTabs().getXMLRapportoDoc()
                                    .setHidden(false);
                        }
                    }
                }
                getForm().getDocumentiUnitaDocumentarieDetail().getScarica_comp_file_doc()
                        .setEditMode();
                getForm().getDocumentiUnitaDocumentarieDetail().getScarica_comp_file_doc()
                        .setDisableHourGlass(true);

                getForm().getDocumentiUnitaDocumentarieDetail().getScarica_sip_doc().setViewMode();
                if (udHelper.isDocumentoAggiunto(iddoc)) {
                    getForm().getDocumentiUnitaDocumentarieDetail().getScarica_sip_doc()
                            .setEditMode();
                    getForm().getDocumentiUnitaDocumentarieDetail().getScarica_sip_doc()
                            .setDisableHourGlass(true);
                }

                // Imposto visibile il bottone per scaricare i files DIP per esibizione
                getForm().getDocumentiUnitaDocumentarieDetail().getScarica_dip_esibizione_doc()
                        .setEditMode();
                getForm().getDocumentiUnitaDocumentarieDetail().getScarica_dip_esibizione_doc()
                        .setDisableHourGlass(true);

                Calendar cal = Calendar.getInstance();
                cal.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date dtAnnulDoc = new Date(documentoAaaRB.getDtAnnulDoc().getTime());
                Date dtAnnulUd = new Date(documentoAaaRB.getDtAnnulUnitaDoc().getTime());
                getForm().getVersamentoAnnullatoDocSection().setHidden(true);
                getForm().getVersamentoAnnullatoUDSection().setHidden(true);
                // Se il documento Ã¨ valido
                if (dtAnnulDoc.compareTo(cal.getTime()) == 0) {
                    getForm().getVersamentoAnnullatoDocSection().setHidden(true);
                    getForm().getDocumentiUnitaDocumentarieDetail().getDt_annul_doc()
                            .setValue(null);
                } else /*
                        * Se il documento è annullato e l'unità doc è valida oppure l'unità
                        * documentaria è annullata e la data è diversa da quella di annullamento del
                        * documento
                        */ if (dtAnnulUd.compareTo(cal.getTime()) == 0
                        || dtAnnulUd.compareTo(dtAnnulDoc) != 0) {
                    getForm().getVersamentoAnnullatoDocSection().setHidden(false);
                }

                if (dtAnnulUd.compareTo(cal.getTime()) == 0) {
                    getForm().getVersamentoAnnullatoUDSection().setHidden(true);
                    getForm().getDocumentiUnitaDocumentarieDetail().getDt_annul_unita_doc()
                            .setValue(null);
                } else {
                    getForm().getVersamentoAnnullatoUDSection().setHidden(false);
                }

                CSVersatore tmpVers = new CSVersatore();
                tmpVers.setSistemaConservazione(sistemaConservazione);
                tmpVers.setAmbiente(documentoAaaRB.getNmAmbiente());
                tmpVers.setEnte(documentoAaaRB.getNmEnte());
                tmpVers.setStruttura(documentoAaaRB.getNmStrut());

                CSChiave tmpChiave = new CSChiave();
                tmpChiave.setTipoRegistro(documentoAaaRB.getCdRegistroKeyUnitaDoc());
                tmpChiave.setAnno(documentoAaaRB.getAaKeyUnitaDoc().longValue());
                tmpChiave.setNumero(documentoAaaRB.getCdKeyUnitaDoc());

                // MAC#23680
                String urnPartDocumento = (documentoAaaRB.getNiOrdDoc() != null)
                        // EVO#16486
                        ? MessaggiWSFormat.formattaUrnPartDocumento(
                                Costanti.CategoriaDocumento.Documento,
                                documentoAaaRB.getNiOrdDoc().intValue(), true,
                                Costanti.UrnFormatter.DOC_FMT_STRING_V2,
                                Costanti.UrnFormatter.PAD5DIGITS_FMT)
                        // end EVO#16486
                        : MessaggiWSFormat.formattaUrnPartDocumento(
                                Costanti.CategoriaDocumento.getEnum(tipoDoc),
                                documentoAaaRB.getPgDoc().intValue());
                // end MAC#23680
                getForm().getDocumentiUnitaDocumentarieDetail().getUrn_doc().setValue(
                        MessaggiWSFormat.formattaUrnDocUniDoc(MessaggiWSFormat.formattaBaseUrnDoc(
                                MessaggiWSFormat.formattaUrnPartVersatore(tmpVers),
                                MessaggiWSFormat.formattaUrnPartUnitaDoc(tmpChiave),
                                urnPartDocumento)));
                getSession().setAttribute("UD_URN_DOC",
                        getForm().getDocumentiUnitaDocumentarieDetail().getUrn_doc().getValue());
                String maxResultStandard = configurationHelper
                        .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
                // Carico la lista componenti
                AroVLisCompDocTableBean listCompDocTB = udHelper.getAroVLisCompDocTableBean(iddoc,
                        Integer.parseInt(maxResultStandard));
                getForm().getComponentiList().setTable(listCompDocTB);
                getForm().getComponentiList().getTable().setPageSize(10);
                getForm().getComponentiList().getTable().first();
                // Carico la lista volumi
                AroVLisVolNoValDocTableBean listVolNoValDocTB = udHelper
                        .getAroVLisVolNoValDocTableBean(iddoc, Integer.parseInt(maxResultStandard));
                getForm().getVolumiList().setTable(listVolNoValDocTB);
                getForm().getVolumiList().getTable().setPageSize(10);
                getForm().getVolumiList().getTable().first();
                // Carico la lista dei dati specifici
                AroVLisDatiSpecTableBean listDatiSpecTB = udHelper.getAroVLisDatiSpecTableBean(
                        iddoc, TipoEntitaSacer.DOC, Constants.TI_USO_XSD_VERS,
                        Integer.parseInt(maxResultStandard));
                getForm().getDatiSpecificiDocList().setTable(listDatiSpecTB);
                getForm().getDatiSpecificiDocList().getTable().setPageSize(10);
                getForm().getDatiSpecificiDocList().getTable().first();
                // Carico la lista dei dati specifici di migrazione
                AroVLisDatiSpecTableBean listDatiSpecMigrazioneTB = udHelper
                        .getAroVLisDatiSpecTableBean(iddoc, TipoEntitaSacer.DOC,
                                Constants.TI_USO_XSD_MIGR, Integer.parseInt(maxResultStandard));
                getForm().getDatiSpecificiMigrazioneDocList().setTable(listDatiSpecMigrazioneTB);
                getForm().getDatiSpecificiMigrazioneDocList().getTable().setPageSize(10);
                getForm().getDatiSpecificiMigrazioneDocList().getTable().first();
                // Setto la versione XSD per dati specifici standard e di migrazione
                if (listDatiSpecTB != null && listDatiSpecTB.size() > 0
                        && listDatiSpecTB.getRow(0).getCdVersioneXsd() != null) {
                    getForm().getDocumentiUnitaDocumentarieDetail().getVersione_xsd_dati_spec_doc()
                            .setValue(listDatiSpecTB.getRow(0).getCdVersioneXsd());
                }
                if (listDatiSpecMigrazioneTB != null && listDatiSpecMigrazioneTB.size() > 0
                        && listDatiSpecMigrazioneTB.getRow(0).getCdVersioneXsd() != null) {
                    getForm().getDocumentiUnitaDocumentarieDetail()
                            .getVersione_xsd_dati_spec_migr_doc()
                            .setValue(listDatiSpecMigrazioneTB.getRow(0).getCdVersioneXsd());
                }
                // Setto i tab correnti
                getForm().getDocumentiDettaglioTabs()
                        .setCurrentTab(getForm().getDocumentiDettaglioTabs().getInfoPrincipali());
                getForm().getDocumentiDettaglioListsTabs().setCurrentTab(
                        getForm().getDocumentiDettaglioListsTabs().getListaComponenti());

                RispostaWSRecupero rispostaWs = new RispostaWSRecupero();
                RecuperoExt myRecuperoExt = new RecuperoExt();
                myRecuperoExt.setParametriRecupero(new ParametriRecupero());
                // verifica se l'unità documentaria richiesta contiene file convertibili
                myRecuperoExt.getParametriRecupero()
                        .setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.DOC_DIP);
                myRecuperoExt.getParametriRecupero().setUtente(getUser());
                myRecuperoExt.getParametriRecupero()
                        .setIdUnitaDoc(documentoAaaRB.getIdUnitaDoc().longValue());
                myRecuperoExt.getParametriRecupero()
                        .setIdDocumento(documentoAaaRB.getIdDoc().longValue());
                recuperoDip.contaComponenti(rispostaWs, myRecuperoExt);
                getForm().getDocumentiUnitaDocumentarieDetail().getScarica_dip_doc().setEditMode();
                if (rispostaWs.getSeverity() == SeverityEnum.OK
                        && rispostaWs.getDatiRecuperoDip().getNumeroElementiTrovati() > 0) {
                    getForm().getDocumentiUnitaDocumentarieDetail().getScarica_dip_doc()
                            .setHidden(false);
                } else {
                    getForm().getDocumentiUnitaDocumentarieDetail().getScarica_dip_doc()
                            .setHidden(true);
                }
            } else if (getRequest().getParameter("table")
                    .equals(getForm().getIndiciAIPList().getName())) {
                AroVerIndiceAipUdRowBean versioneIndice = (AroVerIndiceAipUdRowBean) getForm()
                        .getIndiciAIPList().getTable().getCurrentRow();
                getForm().getVersioneIndiceAIPDetail().copyFromBean(versioneIndice);
                getForm().getVersioneIndiceAIPDetail().getCd_registro_key_unita_doc()
                        .setValue(getForm().getUnitaDocumentarieDetail()
                                .getCd_registro_key_unita_doc().parse());
                getForm().getVersioneIndiceAIPDetail().getAa_key_unita_doc().setValue(getForm()
                        .getUnitaDocumentarieDetail().getAa_key_unita_doc().parse().toString());
                getForm().getVersioneIndiceAIPDetail().getCd_key_unita_doc().setValue(
                        getForm().getUnitaDocumentarieDetail().getCd_key_unita_doc().parse());
                getForm().getVersioneIndiceAIPDetail().getNm_ambiente()
                        .setValue(getForm().getUnitaDocumentarieDetail().getNm_ambiente().parse());
                getForm().getVersioneIndiceAIPDetail().getNm_ente()
                        .setValue(getForm().getUnitaDocumentarieDetail().getNm_ente().parse());
                getForm().getVersioneIndiceAIPDetail().getNm_strut()
                        .setValue(getForm().getUnitaDocumentarieDetail().getNm_strut().parse());
                getForm().getVersioneIndiceAIPDetail().getScarica_indice_aip_detail().setEditMode();
                getForm().getVersioneIndiceAIPDetail().getScarica_indice_aip_detail()
                        .setDisableHourGlass(true);

                // MEV#30395
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    // recupero documento blob vs obj storage
                    // build dto per recupero
                    RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(
                            Constants.TiEntitaSacerObjectStorage.INDICE_AIP,
                            versioneIndice.getIdVerIndiceAip().longValue(), baos,
                            RecClbOracle.TabellaClob.CLOB);
                    // recupero
                    boolean esitoRecupero = recuperoDocumento
                            .callRecuperoDocSuStream(csRecuperoDoc);
                    if (!esitoRecupero) {
                        throw new IOException(ECCEZIONE_RECUPERO_INDICE_AIP);
                    }
                    XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
                    String xmlIndice = formatter.prettyPrintWithDOM3LS(
                            baos.toString(StandardCharsets.UTF_8.displayName()));
                    getForm().getVersioneIndiceAIPDetail().getBl_file_ver_indice_aip()
                            .setValue(xmlIndice);
                } catch (IOException ex) {
                    getMessageBox().addError(ex.getMessage());
                }
                // MEV#30395
            } else if (getRequest().getParameter("table")
                    .equals(getForm().getAggiornamentiMetadatiList().getName())) {
                // AGGIORNAMENTO METADATI
                // Ottengo l'id aggiornamento
                BigDecimal idupdunitadoc = getForm().getAggiornamentiMetadatiList().getTable()
                        .getCurrentRow().getBigDecimal("id_upd_unita_doc");
                getSession().setAttribute("idupdunitadoc", idupdunitadoc);
                AroVVisUpdUnitaDocRowBean aggiornamentoAaaRB = udHelper
                        .getAroVVisUpdUnitaDocRowBean(idupdunitadoc);

                // Copio i dati dell'aggiornamento sulla form di dettaglio
                getForm().getAggiornamentiMetadatiUDDetail().copyFromBean(aggiornamentoAaaRB);

                // Se provengo dal dettaglio elenco o dettaglio componente popolo la form di
                // dettaglio unità
                // documentaria con i dati dell'ud
                if (getForm().getUnitaDocumentarieList().getTable() == null) {
                    getDettaglioUD(aggiornamentoAaaRB.getIdUnitaDoc());
                }

                // Di default nascondo il bottone per il download degli xml e i relativi tab
                getForm().getAggiornamentiMetadatiUDDetail().getScarica_xml_upd().setViewMode();
                getForm().getAggiornamentiDettaglioTabs().getXMLRichiestaUpd().setHidden(true);
                getForm().getAggiornamentiDettaglioTabs().getXMLRispostaUpd().setHidden(true);

                /*
                 * Per gli AGGIORNAMENTI METADATI devo ricavare anche gli xml di richiesta e
                 * risposta. Quindi controllo l'aggiornamento in questione, confrontando la sua data
                 * di creazione con quella di creazione UD. Se quest'ultima è antecedente la data di
                 * creazione dell'aggiornamento, ne mostro i valori di xml richiesta e risposta in
                 * quanto sicuramente presenti
                 */
                Date dataUD = getForm().getUnitaDocumentarieDetail().getDt_creazione().parse();
                if (dataUD != null && aggiornamentoAaaRB.getTsIniSes() != null) {
                    if (dataUD.before(aggiornamentoAaaRB.getTsIniSes())) {
                        // Formatto gli xml di richiesta e risposta in modo tale che compaiano
                        // on-line in versione
                        // "pretty-print"
                        String xmlrich = aggiornamentoAaaRB.getBlXmlRich();
                        String xmlrisp = aggiornamentoAaaRB.getBlXmlRisp();
                        // Se l'aggiornamento ha presenti gli xml di richiesta e risposta, li
                        // presento
                        XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
                        if (xmlrich != null && xmlrisp != null) {
                            xmlrich = formatter.prettyPrintWithDOM3LS(xmlrich);
                            xmlrisp = formatter.prettyPrintWithDOM3LS(xmlrisp);
                            getForm().getAggiornamentiMetadatiUDDetail().getBl_xml_rich()
                                    .setValue(xmlrich);
                            getForm().getAggiornamentiMetadatiUDDetail().getBl_xml_risp()
                                    .setValue(xmlrisp);
                            getForm().getAggiornamentiMetadatiUDDetail().getScarica_xml_upd()
                                    .setDisableHourGlass(true);
                            getForm().getAggiornamentiMetadatiUDDetail().getScarica_xml_upd()
                                    .setEditMode();
                            getForm().getAggiornamentiDettaglioTabs().getXMLRichiestaUpd()
                                    .setHidden(false);
                            getForm().getAggiornamentiDettaglioTabs().getXMLRispostaUpd()
                                    .setHidden(false);
                        }

                        // MEV#29089
                        addXmlVersUpdFromOStoAroVVisUpdUnitaDocBean(aggiornamentoAaaRB);
                        // end MEV#29089
                    }
                }

                CSVersatore tmpVers = new CSVersatore();
                tmpVers.setSistemaConservazione(sistemaConservazione);
                tmpVers.setAmbiente(aggiornamentoAaaRB.getNmAmbiente());
                tmpVers.setEnte(aggiornamentoAaaRB.getNmEnte());
                tmpVers.setStruttura(aggiornamentoAaaRB.getNmStrut());

                CSChiave tmpChiave = new CSChiave();
                tmpChiave.setTipoRegistro(aggiornamentoAaaRB.getCdRegistroKeyUnitaDoc());
                tmpChiave.setAnno(aggiornamentoAaaRB.getAaKeyUnitaDoc().longValue());
                tmpChiave.setNumero(aggiornamentoAaaRB.getCdKeyUnitaDoc());

                // Carico la lista documenti aggiornati
                String maxResultStandard = configurationHelper
                        .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
                AroVLisUpdDocUnitaDocTableBean listUpdDocTB = udHelper
                        .getAroVLisUpdDocUnitaDocTableBean(idupdunitadoc,
                                Integer.parseInt(maxResultStandard));
                getForm().getDocumentiUpdUDList().setTable(listUpdDocTB);
                getForm().getDocumentiUpdUDList().getTable().setPageSize(10);
                getForm().getDocumentiUpdUDList().getTable().first();
                // Carico la lista componenti aggiornati
                AroVLisUpdCompUnitaDocTableBean listUpdCompUnitaDocTB = udHelper
                        .getAroVLisUpdCompUnitaDocTableBean(idupdunitadoc,
                                Integer.parseInt(maxResultStandard));
                getForm().getCompUpdUDList().setTable(listUpdCompUnitaDocTB);
                getForm().getCompUpdUDList().getTable().setPageSize(10);
                getForm().getCompUpdUDList().getTable().first();
                // Carico la lista degli aggiornamenti metadati ko risolti
                AroVLisUpdKoRisoltiTableBean listUpdKoRisoltiTB = udHelper
                        .getAroVLisUpdKoRisoltiTableBean(idupdunitadoc,
                                Integer.parseInt(maxResultStandard));
                getForm().getUpdUDKoRisoltiList().setTable(listUpdKoRisoltiTB);
                getForm().getUpdUDKoRisoltiList().getTable().setPageSize(10);
                getForm().getUpdUDKoRisoltiList().getTable().first();
                // Carico la lista dei warning rilevati
                AroWarnUpdUnitaDocTableBean listUpdWarnTB = udHelper.getAroWarnUpdUnitaDocTableBean(
                        idupdunitadoc, Integer.parseInt(maxResultStandard));
                getForm().getUpdUDWarningList().setTable(listUpdWarnTB);
                getForm().getUpdUDWarningList().getTable().setPageSize(10);
                getForm().getUpdUDWarningList().getTable().first();

                // Setto i tab correnti
                getForm().getAggiornamentiDettaglioTabs().setCurrentTab(
                        getForm().getAggiornamentiDettaglioTabs().getInfoPrincipaliUpd());
                getForm().getAggiornamentiDettaglioListsTabs().setCurrentTab(
                        getForm().getAggiornamentiDettaglioListsTabs().getListaDocAggiornati());
            } else if (getRequest().getParameter("table")
                    .equals(getForm().getNoteList().getName())) {
                if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                        || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                        || getNavigationEvent().equals(ListAction.NE_NEXT)
                        || getNavigationEvent().equals(ListAction.NE_PREV)) {
                    AroVLisNotaUnitaDocRowBean currentRow = (AroVLisNotaUnitaDocRowBean) getForm()
                            .getNoteList().getTable().getCurrentRow();
                    BigDecimal idNotaUnitaDoc = currentRow.getBigDecimal("id_nota_unita_doc");

                    BaseRow tmpRow = new BaseRow();
                    getForm().getUnitaDocumentarieDetail().copyToBean(tmpRow);
                    getForm().getDatiUDDetail().copyFromBean(tmpRow);

                    getForm().getNotaDetail().getId_tipo_nota_unita_doc()
                            .setDecodeMap(DecodeMap.Factory.newInstance(
                                    udHelper.getSingleDecTipoNotaUnitaDocTableBean(
                                            currentRow.getIdTipoNotaUnitaDoc()),
                                    "id_tipo_nota_unita_doc", "ds_tipo_nota_unita_doc"));
                    getForm().getNotaDetail().setViewMode();

                    AroVVisNotaUnitaDocRowBean notaRB = udHelper
                            .getAroVVisNotaUnitaDocRowBean(idNotaUnitaDoc);

                    // Copio i dati della nota sulla form di dettaglio
                    getForm().getNotaDetail().copyFromBean(notaRB);

                    getForm().getNoteList().setStatus(BaseElements.Status.view);
                    getForm().getNotaDetail().setStatus(BaseElements.Status.view);
                }
            }
        }
    }

    // MEV#29089
    /**
     * Nel caso in cui il backend di salvataggio degli XML di versamento dell'aggiornamento metadati
     * sia l'object storage (gestito dal parametro <strong>applicativo</strong>) si possono
     * verificare 2 casi:
     * <ul>
     * <li>gli xml sono <em>ancora</em> sul DB perché non ancora migrati</li>
     * <li>gli xml sono effettivamente sull'object storage</li>
     * </ul>
     * Se si avvera il secondo caso li devo recuperare
     *
     * @param riga AroVVisUpdUnitaDocRowBean
     */
    private void addXmlVersUpdFromOStoAroVVisUpdUnitaDocBean(AroVVisUpdUnitaDocRowBean riga) {
        boolean xmlVersVuoti = riga.getBlXmlRich() == null && riga.getBlXmlRisp() == null;
        /*
         * Se gli xml non sono ancora stati migrati, però, sono ancora presenti sulle tabelle
         */
        if (xmlVersVuoti) {
            Map<String, String> xmls = objectStorageService
                    .getObjectXmlVersAggMd(riga.getIdUpdUnitaDoc().longValue());
            // recupero oggetti da O.S. (se presenti)
            if (!xmls.isEmpty()) {
                XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
                getForm().getAggiornamentiMetadatiUDDetail().getBl_xml_rich().setValue(formatter
                        .prettyPrintWithDOM3LS(xmls.get(CostantiDB.TipiXmlDati.RICHIESTA)));
                getForm().getAggiornamentiMetadatiUDDetail().getBl_xml_risp().setValue(
                        formatter.prettyPrintWithDOM3LS(xmls.get(CostantiDB.TipiXmlDati.RISPOSTA)));
                getForm().getAggiornamentiMetadatiUDDetail().getScarica_xml_upd()
                        .setDisableHourGlass(true);
                getForm().getAggiornamentiMetadatiUDDetail().getScarica_xml_upd().setEditMode();
                getForm().getAggiornamentiDettaglioTabs().getXMLRichiestaUpd().setHidden(false);
                getForm().getAggiornamentiDettaglioTabs().getXMLRispostaUpd().setHidden(false);
            }
        }
    }
    // end MEV#29089

    /**
     * Metodo di caricamento dell'unitÃ documentaria in base al suo id
     *
     * @param idUnitDoc id dell'unitÃ documentaria
     *
     * @throws EMFError errore generico
     */
    public void getDettaglioUD(BigDecimal idUnitDoc) throws EMFError {
        String sistemaConservazione = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
        // Ottengo l'id dell'unitÃ documentaria
        getSession().setAttribute("idud", idUnitDoc);
        // Dall'id dell'unitÃ documentaria vado a prendere l'AroVVisUnitaDocRowBean
        AroVVisUnitaDocIamRowBean udRB = udHelper.getAroVVisUnitaDocIamRowBean(idUnitDoc);
        // Dall'id dell'unitÃ documentaria vado a prendere gli AroVLisDocTableBean
        AroVLisDocTableBean listDocumenti = udHelper.getAroVLisDocTableBean(idUnitDoc, null);
        String maxResultStandard = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
        // Ricavo la lista collegamenti e lista archiviazioni secondarie
        AroVLisLinkUnitaDocTableBean listCollegamentiTB = udHelper
                .getAroVLisLinkUnitaDocTableBean(idUnitDoc, Integer.parseInt(maxResultStandard));
        AroVLisArchivUnitaDocTableBean listArchiviazioniSecondarieTB = udHelper
                .getAroVLisArchivUnitaDocTableBean(idUnitDoc, Integer.parseInt(maxResultStandard));
        // Ricavo la lista dei dati specifici per unità documentaria
        AroVLisDatiSpecTableBean listDatiSpecTB = udHelper.getAroVLisDatiSpecTableBean(idUnitDoc,
                TipoEntitaSacer.UNI_DOC, Constants.TI_USO_XSD_VERS,
                Integer.parseInt(maxResultStandard));
        AroVLisDatiSpecTableBean listDatiSpecMigrazioneTB = udHelper.getAroVLisDatiSpecTableBean(
                idUnitDoc, TipoEntitaSacer.UNI_DOC, Constants.TI_USO_XSD_MIGR,
                Integer.parseInt(maxResultStandard));
        // Ricavo la lista degli indici AIP
        AroVerIndiceAipUdTableBean listIndiciAIPTB = udHelper
                .getAroVerIndiceAipUdTableBean(idUnitDoc, Integer.parseInt(maxResultStandard));
        // Imposto visibile il bottone per scaricare i componenti di tipo file
        getForm().getUnitaDocumentarieDetail().getScarica_comp_file_ud().setEditMode();
        getForm().getUnitaDocumentarieDetail().getScarica_comp_file_ud().setDisableHourGlass(true);
        // Imposto visibile il bottone per scaricare i files DIP per esibizione
        getForm().getUnitaDocumentarieDetail().getScarica_dip_esibizione_ud().setEditMode();
        getForm().getUnitaDocumentarieDetail().getScarica_dip_esibizione_ud()
                .setDisableHourGlass(true);
        // Imposto visibile il bottone per scaricare gli xml di richiesta e risposta
        getForm().getUnitaDocumentarieDetail().getScarica_xml_ud().setEditMode();
        getForm().getUnitaDocumentarieDetail().getScarica_xml_ud().setDisableHourGlass(true);
        // Imposto visibile il bottone per assegnare il progressivo
        getForm().getUnitaDocumentarieDetail().getAssegna_progressivo().setEditMode();
        getForm().getUnitaDocumentarieDetail().getAssegna_progressivo().setDisableHourGlass(true);
        // Imposto visibile il bottone per scaricare il rapporto di versamento
        getForm().getUnitaDocumentarieDetail().getScarica_rv().setEditMode();
        getForm().getUnitaDocumentarieDetail().getScarica_rv().setDisableHourGlass(true);
        // Imposto visibile il bottone per scaricare il SIP unità documentaria
        getForm().getUnitaDocumentarieDetail().getScarica_sip_ud().setEditMode();
        getForm().getUnitaDocumentarieDetail().getScarica_sip_ud().setDisableHourGlass(true);

        if (!listIndiciAIPTB.isEmpty()) {
            getForm().getUnitaDocumentarieDetail().getScarica_xml_unisincro().setHidden(false);
            getForm().getUnitaDocumentarieDetail().getScarica_xml_unisincro().setEditMode();
            getForm().getUnitaDocumentarieDetail().getScarica_xml_unisincro()
                    .setDisableHourGlass(true);
        } else {
            getForm().getUnitaDocumentarieDetail().getScarica_xml_unisincro().setHidden(true);
        }

        // Carico la pagina di dettaglio dell'unitÃ doc.
        getForm().getUnitaDocumentarieDetail().copyFromBean(udRB);

        Calendar cal = Calendar.getInstance();
        cal.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date dtAnnul = new Date(udRB.getDtAnnul().getTime());
        Long idUnitaDocVerAnn = null;
        if (dtAnnul.compareTo(cal.getTime()) == 0) {
            getForm().getVersamentoAnnullatoUDSection().setHidden(true);
            getForm().getUnitaDocumentarieDetail().getDt_annul().setValue(null);

            // Verifico se esiste la mia ud annullata
            if ((idUnitaDocVerAnn = udHelper.getLastIdUnitaDocAnnullata(getIdStrut(),
                    udRB.getCdRegistroKeyUnitaDoc(), udRB.getAaKeyUnitaDoc(),
                    udRB.getCdKeyUnitaDoc())) != null) {
                getForm().getUnitaDocumentarieDetail().getVisualizza_ud_annul().setEditMode();
                getForm().getUnitaDocumentarieDetail().getVisualizza_ud_versata().setViewMode();
                getForm().getUnitaDocumentarieDetail().getVisualizza_ud_annul()
                        .setDisableHourGlass(true);
                getForm().getUnitaDocumentarieDetail().getId_unita_doc_annullata()
                        .setValue("" + idUnitaDocVerAnn);
            } else {
                getForm().getUnitaDocumentarieDetail().getVisualizza_ud_annul().setViewMode();
                getForm().getUnitaDocumentarieDetail().getVisualizza_ud_versata().setViewMode();
            }

        } else {
            getForm().getVersamentoAnnullatoUDSection().setHidden(false);

            // Verifico se esiste la mia ud versata
            if ((idUnitaDocVerAnn = udHelper.getIdUnitaDocVersataNoAnnul(getIdStrut(),
                    udRB.getCdRegistroKeyUnitaDoc(), udRB.getAaKeyUnitaDoc(),
                    udRB.getCdKeyUnitaDoc())) != null) {
                getForm().getUnitaDocumentarieDetail().getVisualizza_ud_versata().setEditMode();
                getForm().getUnitaDocumentarieDetail().getVisualizza_ud_annul().setViewMode();
                getForm().getUnitaDocumentarieDetail().getVisualizza_ud_versata()
                        .setDisableHourGlass(true);
                getForm().getUnitaDocumentarieDetail().getId_unita_doc_versata()
                        .setValue("" + idUnitaDocVerAnn);
            } else {
                getForm().getUnitaDocumentarieDetail().getVisualizza_ud_versata().setViewMode();
                getForm().getUnitaDocumentarieDetail().getVisualizza_ud_annul().setViewMode();
            }
        }

        CSVersatore tmpVers = new CSVersatore();
        tmpVers.setSistemaConservazione(sistemaConservazione);
        tmpVers.setAmbiente(udRB.getNmAmbiente());
        tmpVers.setEnte(udRB.getNmEnte());
        tmpVers.setStruttura(udRB.getNmStrut());

        CSChiave tmpChiave = new CSChiave();
        tmpChiave.setTipoRegistro(udRB.getCdRegistroKeyUnitaDoc());
        tmpChiave.setAnno(udRB.getAaKeyUnitaDoc().longValue());
        tmpChiave.setNumero(udRB.getCdKeyUnitaDoc());

        getForm().getUnitaDocumentarieDetail().getUrn_ud()
                .setValue(MessaggiWSFormat.formattaUrnDocUniDoc(MessaggiWSFormat
                        .formattaBaseUrnUnitaDoc(MessaggiWSFormat.formattaUrnPartVersatore(tmpVers),
                                MessaggiWSFormat.formattaUrnPartUnitaDoc(tmpChiave))));

        // Di default nascondo il bottone per il download degli xml e i relativi tab
        getForm().getUnitaDocumentarieDetail().getScarica_xml_ud().setViewMode();
        getForm().getUnitaDocumentarieDettaglioTabs().getXMLRichiestaUD().setHidden(true);
        getForm().getUnitaDocumentarieDettaglioTabs().getXMLIndiceUD().setHidden(true);
        getForm().getUnitaDocumentarieDettaglioTabs().getXMLRispostaUD().setHidden(true);
        getForm().getUnitaDocumentarieDettaglioTabs().getXMLRapportoUD().setHidden(true);
        getForm().getUnitaDocumentarieDettaglioTabs().getProfiloNormativoUD().setHidden(true);

        // Formatto gli xml di richiesta e risposta in modo tale che compaiano on-line in versione
        // "pretty-print"
        String xmlrich = udRB.getBlXmlRichUd();
        String xmlindex = udRB.getBlXmlIndexUd();
        String xmlrisp = udRB.getBlXmlRispUd();
        String xmlrapp = udRB.getBlXmlRappUd();
        String profiloNormativo = udHelper.getProfiloNormativo(
                CostantiDB.TiUsoModelloXsd.VERS.name(),
                DecModelloXsdUd.TiModelloXsdUd.PROFILO_NORMATIVO_UNITA_DOC,
                udRB.getIdUnitaDoc().longValue());
        XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
        if (xmlrich != null && xmlrisp != null) {
            xmlrich = formatter.prettyPrintWithDOM3LS(xmlrich);
            xmlrisp = formatter.prettyPrintWithDOM3LS(xmlrisp);
            getForm().getUnitaDocumentarieDetail().getBl_xml_rich_ud().setValue(xmlrich);
            getForm().getUnitaDocumentarieDetail().getBl_xml_risp_ud().setValue(xmlrisp);
            getForm().getUnitaDocumentarieDetail().getScarica_xml_ud().setEditMode();
            getForm().getUnitaDocumentarieDettaglioTabs().getXMLRichiestaUD().setHidden(false);
            getForm().getUnitaDocumentarieDettaglioTabs().getXMLRispostaUD().setHidden(false);
            if (xmlindex != null) {
                xmlindex = formatter.prettyPrintWithDOM3LS(xmlindex);
                getForm().getUnitaDocumentarieDetail().getBl_xml_index_ud().setValue(xmlindex);
                getForm().getUnitaDocumentarieDettaglioTabs().getXMLIndiceUD().setHidden(false);
            }
            if (xmlrapp != null) {
                xmlrapp = formatter.prettyPrintWithDOM3LS(xmlrapp);
                getForm().getUnitaDocumentarieDetail().getBl_xml_rapp_ud().setValue(xmlrapp);
                getForm().getUnitaDocumentarieDettaglioTabs().getXMLRapportoUD().setHidden(false);
            }
        }

        if (profiloNormativo != null) {
            // Ricavo il profilo normativo
            profiloNormativo = formatter.prettyPrintWithDOM3LS(profiloNormativo);
            getForm().getUnitaDocumentarieDetail().getProfilo_normativo()
                    .setValue(profiloNormativo);
            getForm().getUnitaDocumentarieDettaglioTabs().getProfiloNormativoUD().setHidden(false);
        }

        int docUDPageSize = 10;
        if (getForm().getDocumentiUDList().getTable() != null) {
            docUDPageSize = getForm().getDocumentiUDList().getTable().getPageSize();
        }
        getForm().getDocumentiUDList().setTable(listDocumenti);
        getForm().getDocumentiUDList().getTable().setPageSize(docUDPageSize);
        getForm().getDocumentiUDList().getTable().first();
        int collegamentiPageSize = 10;
        if (getForm().getCollegamentiList().getTable() != null) {
            collegamentiPageSize = getForm().getCollegamentiList().getTable().getPageSize();
        }
        getForm().getCollegamentiList().setTable(listCollegamentiTB);
        getForm().getCollegamentiList().getTable().setPageSize(collegamentiPageSize);
        getForm().getCollegamentiList().getTable().first();
        int datiSpecUDPageSize = 10;
        if (getForm().getDatiSpecificiUDList().getTable() != null) {
            datiSpecUDPageSize = getForm().getDatiSpecificiUDList().getTable().getPageSize();
        }
        getForm().getDatiSpecificiUDList().setTable(listDatiSpecTB);
        getForm().getDatiSpecificiUDList().getTable().setPageSize(datiSpecUDPageSize);
        getForm().getDatiSpecificiUDList().getTable().first();
        int datiSpecMigrazionePageSize = 10;
        if (getForm().getDatiSpecificiMigrazioneUDList().getTable() != null) {
            datiSpecMigrazionePageSize = getForm().getDatiSpecificiMigrazioneUDList().getTable()
                    .getPageSize();
        }
        getForm().getDatiSpecificiMigrazioneUDList().setTable(listDatiSpecMigrazioneTB);
        getForm().getDatiSpecificiMigrazioneUDList().getTable()
                .setPageSize(datiSpecMigrazionePageSize);
        getForm().getDatiSpecificiMigrazioneUDList().getTable().first();
        int archSecPageSize = 10;
        if (getForm().getArchiviazioniSecondarieList().getTable() != null) {
            archSecPageSize = getForm().getArchiviazioniSecondarieList().getTable().getPageSize();
        }
        getForm().getArchiviazioniSecondarieList().setTable(listArchiviazioniSecondarieTB);
        getForm().getArchiviazioniSecondarieList().getTable().setPageSize(archSecPageSize);
        getForm().getArchiviazioniSecondarieList().getTable().first();
        getForm().getIndiciAIPList().setTable(listIndiciAIPTB);
        getForm().getIndiciAIPList().getTable().setPageSize(10);
        getForm().getIndiciAIPList().getTable().first();

        // Setto la versione XSD per dati specifici standard e di migrazione
        if (listDatiSpecTB != null && listDatiSpecTB.size() > 0
                && listDatiSpecTB.getRow(0).getCdVersioneXsd() != null) {
            getForm().getUnitaDocumentarieDetail().getVersione_xsd_dati_spec_ud()
                    .setValue(listDatiSpecTB.getRow(0).getCdVersioneXsd());
        }
        if (listDatiSpecMigrazioneTB != null && listDatiSpecMigrazioneTB.size() > 0
                && listDatiSpecMigrazioneTB.getRow(0).getCdVersioneXsd() != null) {
            getForm().getUnitaDocumentarieDetail().getVersione_xsd_dati_spec_migr_ud()
                    .setValue(listDatiSpecMigrazioneTB.getRow(0).getCdVersioneXsd());
        }
        RispostaWSRecupero rispostaWs = new RispostaWSRecupero();
        RecuperoExt myRecuperoExt = new RecuperoExt();
        myRecuperoExt.setParametriRecupero(new ParametriRecupero());
        // verifica se l'unità documentaria richiesta contiene file convertibili
        myRecuperoExt.getParametriRecupero()
                .setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.UNI_DOC_DIP);
        myRecuperoExt.getParametriRecupero().setUtente(getUser());
        myRecuperoExt.getParametriRecupero().setIdUnitaDoc(idUnitDoc.longValue());
        recuperoDip.contaComponenti(rispostaWs, myRecuperoExt);
        getForm().getUnitaDocumentarieDetail().getScarica_dip_ud().setEditMode();
        getForm().getUnitaDocumentarieDetail().getScarica_dip_ud().setDisableHourGlass(true);
        if (rispostaWs.getSeverity() == SeverityEnum.OK
                && rispostaWs.getDatiRecuperoDip().getNumeroElementiTrovati() > 0) {
            getForm().getUnitaDocumentarieDetail().getScarica_dip_ud().setHidden(false);
        } else {
            getForm().getUnitaDocumentarieDetail().getScarica_dip_ud().setHidden(true);
        }
        // Carico l'indice AIP dell'ultima versione, corrispondente alla prima riga
        if (listIndiciAIPTB != null && !listIndiciAIPTB.isEmpty()) {
            AroVerIndiceAipUdRowBean versioneIndice = (AroVerIndiceAipUdRowBean) getForm()
                    .getIndiciAIPList().getTable().remove(0);
            getForm().getVersioneIndiceAIPLast().copyFromBean(versioneIndice);
            getForm().getVersioneIndiceAIPLast().getScarica_indice_aip_last().setEditMode();
            getForm().getVersioneIndiceAIPLast().getScarica_indice_aip_last()
                    .setDisableHourGlass(true);

            // Hash SHA-1
            if (versioneIndice.getDsHashIndiceAip() == null) {
                getForm().getVersioneIndiceAIPLast().getDs_hash_indice_aip().setHidden(true);
            } else {
                getForm().getVersioneIndiceAIPLast().getDs_hash_indice_aip().setHidden(false);
            }

            if (versioneIndice.getIdEnteConserv() != null) {
                getForm().getVersioneIndiceAIPLast().getNm_ente_conserv()
                        .setValue(udHelper
                                .findById(SIOrgEnteSiam.class, versioneIndice.getIdEnteConserv())
                                .getNmEnteSiam());
            }

            // Hash "personalizzato"
            if (versioneIndice.getDsHashAip() != null) {
                String descrizione = "Hash " + versioneIndice.getDsAlgoHashAip() + " ("
                        + versioneIndice.getCdEncodingHashAip() + ")";
                String valore = versioneIndice.getDsHashAip();
                getForm().getVersioneIndiceAIPLast().getHash_personalizzato().setHidden(false);
                getForm().getVersioneIndiceAIPLast().getHash_personalizzato()
                        .setDescription(descrizione);
                getForm().getVersioneIndiceAIPLast().getHash_personalizzato().setValue(valore);
            } else {
                getForm().getVersioneIndiceAIPLast().getHash_personalizzato().setHidden(true);
            }

            // MEV#30395
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // recupero documento blob vs obj storage
                // build dto per recupero
                RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(
                        Constants.TiEntitaSacerObjectStorage.INDICE_AIP,
                        versioneIndice.getIdVerIndiceAip().longValue(), baos,
                        RecClbOracle.TabellaClob.CLOB);
                // recupero
                boolean esitoRecupero = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);
                if (!esitoRecupero) {
                    throw new IOException(ECCEZIONE_RECUPERO_INDICE_AIP);
                }
                String xmlIndice = formatter
                        .prettyPrintWithDOM3LS(baos.toString(StandardCharsets.UTF_8.displayName()));
                getForm().getVersioneIndiceAIPLast().getBl_file_ver_indice_aip()
                        .setValue(xmlIndice);
            } catch (IOException ex) {
                getMessageBox().addError(ex.getMessage());
            }
            // MEV#30395
        } else {
            getForm().getVersioneIndiceAIPLast().reset();
            getForm().getVersioneIndiceAIPLast().getScarica_indice_aip_last().setViewMode();
            getForm().getVersioneIndiceAIPLast().getBl_file_ver_indice_aip().setValue(null);
        }
        // Carico la lista dei volumi e degli elenchi di versamento
        AroVLisVolCorTableBean volumiTB = udHelper.getAroVLisVolCorViewBean(idUnitDoc);
        int volumiPageSize = 10;
        if (getForm().getVolumiList().getTable() != null) {
            volumiPageSize = getForm().getVolumiList().getTable().getPageSize();
        }
        getForm().getVolumiList().setTable(volumiTB);
        getForm().getVolumiList().getTable().setPageSize(volumiPageSize);
        getForm().getVolumiList().getTable().first();
        if (!volumiTB.isEmpty()) {
            getForm().getUnitaDocumentarieDettaglioListsTabs().getListaVolumiUD().setHidden(false);
        } else {
            getForm().getUnitaDocumentarieDettaglioListsTabs().getListaVolumiUD().setHidden(true);
        }

        AroVLisElvVerTableBean elenchiTB = udHelper.getAroVLisElvVerViewBean(idUnitDoc);
        int elenchiPageSize = 10;
        if (getForm().getElenchiVersamentoList().getTable() != null) {
            elenchiPageSize = getForm().getElenchiVersamentoList().getTable().getPageSize();
        }
        getForm().getElenchiVersamentoList().setTable(elenchiTB);
        getForm().getElenchiVersamentoList().getTable().setPageSize(elenchiPageSize);
        getForm().getElenchiVersamentoList().getTable().first();
        if (!elenchiTB.isEmpty()) {
            getForm().getUnitaDocumentarieDettaglioListsTabs().getListaElenchiVersamentoUD()
                    .setHidden(false);
        } else {
            getForm().getUnitaDocumentarieDettaglioListsTabs().getListaElenchiVersamentoUD()
                    .setHidden(true);
        }

        BaseTable serieAppartenenza = udHelper.getSerieAppartenenzaList(idUnitDoc);
        int seriePageSize = 10;
        if (getForm().getSerieAppartenenzaList().getTable() != null) {
            seriePageSize = getForm().getSerieAppartenenzaList().getTable().getPageSize();
        }
        getForm().getSerieAppartenenzaList().setTable(serieAppartenenza);
        getForm().getSerieAppartenenzaList().getTable().setPageSize(seriePageSize);
        getForm().getSerieAppartenenzaList().getTable().first();
        if (!serieAppartenenza.isEmpty()) {
            getForm().getUnitaDocumentarieDettaglioListsTabs().getListaSerieAppartenenzaUD()
                    .setHidden(false);
        } else {
            getForm().getUnitaDocumentarieDettaglioListsTabs().getListaSerieAppartenenzaUD()
                    .setHidden(true);
        }

        AroVLisFascTableBean fascicoliAppartenenza = udHelper
                .getFascicoliAppartenenzaViewBean(idUnitDoc);
        int fascicoliPageSize = 10;
        if (getForm().getFascicoliAppartenenzaList().getTable() != null) {
            fascicoliPageSize = getForm().getFascicoliAppartenenzaList().getTable().getPageSize();
        }
        getForm().getFascicoliAppartenenzaList().setTable(fascicoliAppartenenza);
        getForm().getFascicoliAppartenenzaList().getTable().setPageSize(fascicoliPageSize);
        getForm().getFascicoliAppartenenzaList().getTable().first();
        if (!fascicoliAppartenenza.isEmpty()) {
            getForm().getUnitaDocumentarieDettaglioListsTabs().getListaFascicoliAppartenenzaUD()
                    .setHidden(false);
        } else {
            getForm().getUnitaDocumentarieDettaglioListsTabs().getListaFascicoliAppartenenzaUD()
                    .setHidden(true);
        }

        AroUpdUnitaDocTableBean aggiornamentiMetadati = udHelper
                .getAggiornamentiMetadatiTableBean(idUnitDoc);
        int aggiornamentiPageSize = 10;
        if (getForm().getAggiornamentiMetadatiList().getTable() != null) {
            aggiornamentiPageSize = getForm().getAggiornamentiMetadatiList().getTable()
                    .getPageSize();
        }
        getForm().getAggiornamentiMetadatiList().setTable(aggiornamentiMetadati);
        getForm().getAggiornamentiMetadatiList().getTable().setPageSize(aggiornamentiPageSize);
        getForm().getAggiornamentiMetadatiList().getTable().first();
        if (!aggiornamentiMetadati.isEmpty()) {
            getForm().getUnitaDocumentarieDettaglioListsTabs().getListaAggiornamentiMetadatiUD()
                    .setHidden(false);
        } else {
            getForm().getUnitaDocumentarieDettaglioListsTabs().getListaAggiornamentiMetadatiUD()
                    .setHidden(true);
        }

        // MEV#24597
        AroVLisNotaUnitaDocTableBean note = udHelper
                .getListaNoteUnitaDocumentarieTableBean(idUnitDoc);
        int notePageSize = 10;
        if (getForm().getNoteList().getTable() != null) {
            notePageSize = getForm().getNoteList().getTable().getPageSize();
        }
        getForm().getNoteList().setTable(note);
        getForm().getNoteList().getTable().setPageSize(notePageSize);
        getForm().getNoteList().getTable().first();
        getForm().getUnitaDocumentarieDettaglioListsTabs().getListaNoteUD().setHidden(false);
        // end MEV#24597

        // MEV #31162
        AroLogStatoConservUdTableBean logStatoConservUdTableBean = udHelper
                .getAroLogStatoConservUdTableBean(idUnitDoc);
        int logStatoConservUdPageSize = 10;
        if (getForm().getStatiConservazioneUdList().getTable() != null) {
            logStatoConservUdPageSize = getForm().getStatiConservazioneUdList().getTable()
                    .getPageSize();
        }
        getForm().getStatiConservazioneUdList().setTable(logStatoConservUdTableBean);
        getForm().getStatiConservazioneUdList().getTable().setPageSize(logStatoConservUdPageSize);
        getForm().getStatiConservazioneUdList().getTable().first();
        // end MEV #31162

    }

    @Override
    public void assegna_progressivo() throws EMFError {
        getForm().getUnitaDocumentarieDetail().setStatus(BaseElements.Status.update);
        getForm().getUnitaDocumentarieDetail().getPg_unita_doc().setEditMode();
        getForm().getUnitaDocumentarieDetail().getConfermaModificaAssegnaProgr().setViewMode();
        getForm().getUnitaDocumentarieDetail().getAnnullaModificaAssegnaProgr().setViewMode();
        BigDecimal idUnitaDoc = getForm().getUnitaDocumentarieDetail().getId_unita_doc().parse();
        BigDecimal pgUnitaDoc = udHelper.getPgUnitaDoc(idUnitaDoc);
        getForm().getUnitaDocumentarieDetail().getPg_unita_doc()
                .setValue(pgUnitaDoc != null ? pgUnitaDoc.toString() : "");
        getForm().getVersatoreSection().setLoadOpened(true);
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_ASSEGNA_PROGR);
    }

    /**
     * Metodo invocato al salvataggio dei dati nella form di dettaglio
     *
     * @throws EMFError errore generico
     */
    @Override
    public void saveDettaglio() throws EMFError {
        // Se mi trovo nella pagina di annullamento progressivo
        if (getLastPublisher().equals(Application.Publisher.UNITA_DOCUMENTARIE_ASSEGNA_PROGR)) {
            getForm().getUnitaDocumentarieDetail().post(getRequest());
            if (getForm().getUnitaDocumentarieDetail().validate(getMessageBox())) {
                BigDecimal idStrut = getUser().getIdOrganizzazioneFoglia();
                BigDecimal idUnitaDoc = getForm().getUnitaDocumentarieDetail().getId_unita_doc()
                        .parse();
                String cdRegistroKeyUnitaDoc = getForm().getUnitaDocumentarieDetail()
                        .getCd_registro_key_unita_doc().parse();
                BigDecimal aaKeyUnitaDoc = getForm().getUnitaDocumentarieDetail()
                        .getAa_key_unita_doc().parse();
                BigDecimal nuovoProgressivo = getForm().getUnitaDocumentarieDetail()
                        .getPg_unita_doc().parse();
                List<Object[]> progressivoEsistenteUnitaDocList = progressivoEsistenteUnitaDocList(
                        idStrut, idUnitaDoc, cdRegistroKeyUnitaDoc, aaKeyUnitaDoc,
                        nuovoProgressivo);
                if (!progressivoEsistenteUnitaDocList.isEmpty()) {
                    Object[] attributi = {
                            idUnitaDoc, nuovoProgressivo };
                    getSession().setAttribute("salvataggioAttributesAssegnaProgr", attributi);
                    getRequest().setAttribute("customBoxAssegnaProgr", true);
                    List<String> listaUdString = new ArrayList<>();
                    int count = 0;
                    for (Object[] progressivoEsistenteUnitaDoc : progressivoEsistenteUnitaDocList) {
                        if (count == 3) {
                            listaUdString.add("...");
                            break;
                        }
                        listaUdString.add(progressivoEsistenteUnitaDoc[0] + "-"
                                + progressivoEsistenteUnitaDoc[1] + "-"
                                + progressivoEsistenteUnitaDoc[2]);
                        count++;
                    }
                    getForm().getUnitaDocumentarieDetail().getConfermaModificaAssegnaProgr()
                            .setEditMode();
                    getForm().getUnitaDocumentarieDetail().getAnnullaModificaAssegnaProgr()
                            .setEditMode();
                    getRequest().setAttribute("listaUdString", listaUdString);
                    forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_ASSEGNA_PROGR);
                } else {
                    eseguiModificaAssegnaProgr(idUnitaDoc, nuovoProgressivo);
                    goBack();
                }
            } else {
                forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_ASSEGNA_PROGR);
            }
        } else if (getLastPublisher().equals(Application.Publisher.NOTA_UD_DETAIL)) {
            BigDecimal idUnitaDoc = getForm().getUnitaDocumentarieDetail().getId_unita_doc()
                    .parse();
            if (getForm().getNotaDetail().postAndValidate(getRequest(), getMessageBox())) {
                BigDecimal idTipoNotaUnitaDoc = getForm().getNotaDetail()
                        .getId_tipo_nota_unita_doc().parse();
                BigDecimal pgNota = getForm().getNotaDetail().getPg_nota_unita_doc().parse();
                String dsNota = getForm().getNotaDetail().getDs_nota_unita_doc().parse();
                Date dtNota = new Date(
                        getForm().getNotaDetail().getDt_nota_unita_doc().parse().getTime());

                try {
                    int rowIndex = getForm().getNoteList().getTable().getCurrentRowIndex();
                    if (getForm().getNoteList().getStatus().equals(BaseElements.Status.insert)) {
                        BigDecimal idNota = udHelper.saveNota(getUser().getIdUtente(), idUnitaDoc,
                                idTipoNotaUnitaDoc, pgNota, dsNota, dtNota);
                        if (idNota != null) {
                            getForm().getNotaDetail().getId_nota_unita_doc()
                                    .setValue(idNota.toPlainString());
                        }
                        rowIndex = getForm().getNoteList().getTable().size();
                    } else if (getForm().getNoteList().getStatus()
                            .equals(BaseElements.Status.update)) {
                        BigDecimal idNota = getForm().getNotaDetail().getId_nota_unita_doc()
                                .parse();
                        udHelper.saveNota(idNota, dsNota, getUser().getIdUtente(), dtNota);
                    }
                    AroVLisNotaUnitaDocTableBean noteTb = udHelper
                            .getAroVLisNotaUnitaDocTableBean(idUnitaDoc);
                    if (getForm().getNoteList().getTable() != null) {
                        int pageSize = getForm().getNoteList().getTable().getPageSize();
                        getForm().getNoteList().setTable(noteTb);
                        getForm().getNoteList().getTable().setPageSize(pageSize);
                        getForm().getNoteList().getTable().setCurrentRowIndex(rowIndex);
                    } else {
                        getForm().getNoteList().setTable(noteTb);
                        getForm().getNoteList().getTable()
                                .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                        getForm().getNoteList().getTable().setCurrentRowIndex(rowIndex);
                    }

                    getMessageBox().addInfo("Nota salvata con successo");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                } catch (ParerUserError ex) {
                    getMessageBox().addError(
                            "La nota non pu\u00F2 essere salvata: " + ex.getDescription());
                }
            }
            // Salvataggio nota
            if (!getMessageBox().hasError()) {
                getForm().getNoteList().setStatus(BaseElements.Status.view);
                getForm().getNotaDetail().setStatus(BaseElements.Status.view);
                getForm().getNotaDetail().setViewMode();
            }
            forwardToPublisher(Application.Publisher.NOTA_UD_DETAIL);
        }
    }

    private List<Object[]> progressivoEsistenteUnitaDocList(BigDecimal idStrut,
            BigDecimal idUnitaDoc, String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc,
            BigDecimal nuovoProgressivo) {
        return udHelper.progressivoEsistenteUnitaDocList(idStrut, idUnitaDoc, cdRegistroKeyUnitaDoc,
                aaKeyUnitaDoc, nuovoProgressivo);
    }

    @Override
    public void confermaModificaAssegnaProgr() {
        if (getSession().getAttribute("salvataggioAttributesAssegnaProgr") != null) {
            Object[] attributi = (Object[]) getSession()
                    .getAttribute("salvataggioAttributesAssegnaProgr");
            eseguiModificaAssegnaProgr((BigDecimal) attributi[0], (BigDecimal) attributi[1]);
            getSession().removeAttribute("salvataggioAttributesAssegnaProgr");
            forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
        }
    }

    @Override
    public void annullaModificaAssegnaProgr() throws EMFError {
        getForm().getUnitaDocumentarieDetail().getConfermaModificaAssegnaProgr().setViewMode();
        getForm().getUnitaDocumentarieDetail().getAnnullaModificaAssegnaProgr().setViewMode();
        getSession().removeAttribute("salvataggioAttributesAssegnaProgr");
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_ASSEGNA_PROGR);
    }

    private void eseguiModificaAssegnaProgr(BigDecimal idUnitaDoc, BigDecimal nuovoProgressivo) {
        udHelper.salvaAssegnaProgressivo(idUnitaDoc, nuovoProgressivo);
        getForm().getUnitaDocumentarieDetail().setStatus(BaseElements.Status.view);
        getForm().getUnitaDocumentarieDetail().getPg_unita_doc().setViewMode();
        getMessageBox().addInfo("Progressivo unità documentaria modificato con successo!");
    }

    /**
     * Metodo invocato al click del tasto annulla in inserimento/modifica dei criteri di
     * raggruppamento
     *
     * @throws EMFError errore generico
     */
    @Override
    public void undoDettaglio() throws EMFError {
        if (getLastPublisher().equals(Application.Publisher.UNITA_DOCUMENTARIE_ASSEGNA_PROGR)) {
            getForm().getUnitaDocumentarieDetail().setStatus(BaseElements.Status.view);
            getForm().getUnitaDocumentarieDetail().getPg_unita_doc().setViewMode();
            forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
        } else {
            goBack();
        }
    }

    /**
     * Metodo invocato dal bottone omonimo dei filtri ricerca unitÃ documentarie per ripulire i
     * filtri
     *
     * @throws EMFError errore generico
     */
    @Override
    public void pulisciUD() throws EMFError {
        if (getLastPublisher().equals(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_SEMPLICE)) {
            unitaDocumentarieRicercaSemplice();
        } else if (getLastPublisher()
                .equals(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_SEMPLICE_NUOVA)) {
            unitaDocumentarieRicercaSempliceNuova();
        } else if (getSession().getAttribute(UnitaDocAttributes.TIPORICERCA.name()) != null
                && getSession().getAttribute(UnitaDocAttributes.TIPORICERCA.name())
                        .equals(TipoRicercaAttribute.VERS_ANNULLATI.name())) {
            unitaDocumentarieRicercaVersAnnullati();
        } else {
            unitaDocumentarieRicercaAvanzata();
        }
    }

    /**
     * Attiva il tab Filtri Ricerca di Ricerca UnitÃ Documentarie
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabFiltriRicercaAvanzataOnClick() throws EMFError {
        // Salva i filtri che l'utente ha compilato
        salvaFiltriDatiSpecCompilati();

        // Controlla che i filtri dati specifici siano stati compilati correttamente
        checkFiltriSettatiSuDatiSpecifici();

        forwardToPublisher(getLastPublisher());
    }

    private void salvaFiltriDatiSpecCompilati() {
        // Ricavo la struttura dati contenente la Lista Dati Specifici compilati a video
        List<DecCriterioDatiSpecBean> listaDatiSpecOnLine = (ArrayList) getSession()
                .getAttribute("listaDatiSpecOnLine") != null
                        ? (ArrayList) getSession().getAttribute("listaDatiSpecOnLine")
                        : new ArrayList();

        // Ricavo i filtri compilati nel tab precedente
        if (getForm().getFiltriDatiSpecUnitaDocumentarieList().getTable() != null) {
            for (int i = 0; i < getForm().getFiltriDatiSpecUnitaDocumentarieList().getTable()
                    .size(); i++) {
                BaseRowInterface r = getForm().getFiltriDatiSpecUnitaDocumentarieList().getTable()
                        .getRow(i);
                r.setString("ti_oper", getRequest().getParameterValues("Ti_oper")[i]);
                r.setString("dl_valore", getRequest().getParameterValues("Dl_valore")[i]);
                for (DecCriterioDatiSpecBean rigaDatoSpec : listaDatiSpecOnLine) {
                    if (rigaDatoSpec.getNmAttribDatiSpec()
                            .equals(r.getString("nm_attrib_dati_spec"))) {
                        rigaDatoSpec.setTiOper(r.getString("ti_oper"));
                        rigaDatoSpec.setDlValore(r.getString("dl_valore"));
                    }
                }
            }
        }

        // Risalvo in sessione la struttura dati contenente la Lista Dari Specifici compilati a
        // video
        getSession().setAttribute("listaDatiSpecOnLine", listaDatiSpecOnLine);
    }

    /**
     * Attiva il tab Filtri Dati Specifici di Ricerca Unità Documentarie
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabFiltriDatiSpecOnClick() throws EMFError {
        if (getLastPublisher().equals(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_AVANZATA)) {
            // mi salvo i filtri compilati nel tab precedente
            getForm().getFiltriUnitaDocumentarieAvanzata().post(getRequest());
            getForm().getUnitaDocumentarieTabs()
                    .setCurrentTab(getForm().getUnitaDocumentarieTabs().getFiltriDatiSpec());
            // Workaround per evitare che il trigger scarichi la pagina HTML anzichè visualizzarla
            // sul browser
            forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_AVANZATA);
        } else if (getLastPublisher()
                .equals(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_DATI_SPEC)) {
            // mi salvo i filtri compilati nel tab precedente
            getForm().getFiltriUnitaDocumentarieDatiSpec().post(getRequest());
            getForm().getUnitaDocumentarieTabs()
                    .setCurrentTab(getForm().getUnitaDocumentarieTabs().getFiltriDatiSpec());
            // Workaround per evitare che il trigger scarichi la pagina HTML anzichè visualizzarla
            // sul browser
            forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_DATI_SPEC);
        }
    }

    // Gestione DETTAGLIO UNITA' DOCUMENTARIE TABS
    /**
     * Attiva il tab Info Versate nel dettaglio di un'unitÃ documentaria
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabInfoPrincipaliUDOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioTabs()
                .setCurrentTab(getForm().getUnitaDocumentarieDettaglioTabs().getInfoPrincipaliUD());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    @Override
    public void tabProfiloNormativoUDOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioTabs().setCurrentTab(
                getForm().getUnitaDocumentarieDettaglioTabs().getProfiloNormativoUD());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    /**
     * Attiva il tab Info Versamento nel dettaglio di un'unitÃ documentaria
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabInfoVersamentoUDOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioTabs()
                .setCurrentTab(getForm().getUnitaDocumentarieDettaglioTabs().getInfoVersamentoUD());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    @Override
    public void tabXMLRichiestaUDOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioTabs()
                .setCurrentTab(getForm().getUnitaDocumentarieDettaglioTabs().getXMLRichiestaUD());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    @Override
    public void tabXMLIndiceUDOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioTabs()
                .setCurrentTab(getForm().getUnitaDocumentarieDettaglioTabs().getXMLIndiceUD());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    @Override
    public void tabXMLRispostaUDOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioTabs()
                .setCurrentTab(getForm().getUnitaDocumentarieDettaglioTabs().getXMLRispostaUD());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    @Override
    public void tabXMLRapportoUDOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioTabs()
                .setCurrentTab(getForm().getUnitaDocumentarieDettaglioTabs().getXMLRapportoUD());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    @Override
    public void tabListaDocumentiUDOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioListsTabs().setCurrentTab(
                getForm().getUnitaDocumentarieDettaglioListsTabs().getListaDocumentiUD());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    @Override
    public void tabListaStatiConservUDOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioListsTabs().setCurrentTab(
                getForm().getUnitaDocumentarieDettaglioListsTabs().getListaStatiConservUD());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    /**
     * Attiva il tab Lista Collegamenti nel dettaglio di un'unitÃ documentaria
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabListaCollegamentiUDOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioListsTabs().setCurrentTab(
                getForm().getUnitaDocumentarieDettaglioListsTabs().getListaCollegamentiUD());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    /**
     * Attiva il tab Lista Archiviazioni Secondarie nel dettaglio di un'unitÃ documentaria
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabListaArchiviazioniSecondarieUDOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioListsTabs().setCurrentTab(getForm()
                .getUnitaDocumentarieDettaglioListsTabs().getListaArchiviazioniSecondarieUD());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    /**
     * Attiva il tab Lista Dati Specifici nel dettaglio di un'unitÃ documentaria
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabListaDatiSpecificiUDOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioListsTabs().setCurrentTab(
                getForm().getUnitaDocumentarieDettaglioListsTabs().getListaDatiSpecificiUD());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    /**
     * Attiva il tab Lista Dati Specifici Migrazione nel dettaglio di un'unitÃ documentaria
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabListaDatiSpecificiMigrazioneUDOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioListsTabs().setCurrentTab(getForm()
                .getUnitaDocumentarieDettaglioListsTabs().getListaDatiSpecificiMigrazioneUD());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    /**
     * Attiva il tab Lista Indici AIP nel dettaglio di un'unitÃ documentaria
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabListaIndiciAIPOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioListsTabs().setCurrentTab(
                getForm().getUnitaDocumentarieDettaglioListsTabs().getListaIndiciAIP());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    @Override
    public void tabUltimoIndiceAIPOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioTabs()
                .setCurrentTab(getForm().getUnitaDocumentarieDettaglioTabs().getUltimoIndiceAIP());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    @Override
    public void tabListaVolumiUDOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioListsTabs().setCurrentTab(
                getForm().getUnitaDocumentarieDettaglioListsTabs().getListaVolumiUD());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    @Override
    public void tabListaElenchiVersamentoUDOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioListsTabs().setCurrentTab(
                getForm().getUnitaDocumentarieDettaglioListsTabs().getListaElenchiVersamentoUD());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    @Override
    public void tabListaSerieAppartenenzaUDOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioListsTabs().setCurrentTab(
                getForm().getUnitaDocumentarieDettaglioListsTabs().getListaSerieAppartenenzaUD());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    @Override
    public void tabListaFascicoliAppartenenzaUDOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioListsTabs().setCurrentTab(getForm()
                .getUnitaDocumentarieDettaglioListsTabs().getListaFascicoliAppartenenzaUD());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    /**
     * Attiva il tab Lista Aggiornamento Metadati nel dettaglio di un'unità documentaria
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabListaAggiornamentiMetadatiUDOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioListsTabs().setCurrentTab(getForm()
                .getUnitaDocumentarieDettaglioListsTabs().getListaAggiornamentiMetadatiUD());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    // MEV#24597
    /**
     * Attiva il tab Lista Note Unità Documentaria nel dettaglio di un'unità documentaria
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabListaNoteUDOnClick() throws EMFError {
        getForm().getUnitaDocumentarieDettaglioListsTabs()
                .setCurrentTab(getForm().getUnitaDocumentarieDettaglioListsTabs().getListaNoteUD());
        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }
    // end MEV#24597

    /**
     * Metodo di inizializzazione delle combo comuni a creazione criterio, ricerca unitÃ
     * documentarie e creazione volume
     *
     * @throws EMFError errore generico
     */
    private void initMappeTipiDato() throws EMFError {
        // Setto i valori della combo TIPO UNITA DOC ricavati dalla tabella DEC_TIPO_UNITA_DOC
        DecTipoUnitaDocTableBean tmpTableBeanTipoUD = tipoUnitaDocEjb
                .getTipiUnitaDocAbilitati(getUser().getIdUtente(), getIdStrut());
        mappaTipoUD = new DecodeMap();
        mappaTipoUD.populatedMap(tmpTableBeanTipoUD, "id_tipo_unita_doc", "nm_tipo_unita_doc");

        // Setto i valori della combo TIPO DOC ricavati dalla tabella DEC_TIPO_DOC
        DecTipoDocTableBean tmpTableBeanTipoDoc = tipoDocumentoEjb
                .getTipiDocAbilitati(getUser().getIdUtente(), getIdStrut());
        tmpTableBeanTipoDoc.addSortingRule(DecTipoDocTableDescriptor.COL_NM_TIPO_DOC,
                SortingRule.ASC);
        tmpTableBeanTipoDoc.sort();
        mappaTipoDoc = new DecodeMap();
        mappaTipoDoc.populatedMap(tmpTableBeanTipoDoc, "id_tipo_doc", "nm_tipo_doc");

        // Setto i valori della combo TIPO REGISTRO ricavati dalla tabella DEC_REGISTRO_UNITA_DOC
        DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb
                .getRegistriUnitaDocAbilitati(getUser().getIdUtente(), getIdStrut());
        mappaRegistro = new DecodeMap();
        mappaRegistro.populatedMap(tmpTableBeanReg, "id_registro_unita_doc",
                "cd_registro_unita_doc");

        // Setto i valori della combo SISTEMI DI MIGRAZIONE ricavati dalla tabella DEC_XSD_DATI_SPEC
        BaseTableInterface tmpTableBeanSisMig = sysMigrazioneEjb
                .getNmSistemaMigrazTableBean(getIdStrut());
        mappaSisMig = new DecodeMap();
        mappaSisMig.populatedMap(tmpTableBeanSisMig, "nm_sistema_migraz", "nm_sistema_migraz");

        // Setto i valori della combo TIPO FASCICOLO ricavati dalla tabella DEC_TIPO_FASCICOLO
        DecTipoFascicoloTableBean tmpTableBeanTipoFasc = tipoFascicoloEjb
                .getTipiFascicoloAbilitati(getUser().getIdUtente(), getIdStrut(), true);
        tmpTableBeanTipoFasc.addSortingRule(DecTipoFascicoloTableDescriptor.COL_NM_TIPO_FASCICOLO,
                SortingRule.ASC);
        tmpTableBeanTipoFasc.sort();
        mappaTipoFasc = new DecodeMap();
        mappaTipoFasc.populatedMap(tmpTableBeanTipoFasc, "id_tipo_fascicolo", "nm_tipo_fascicolo");
        // Setto i valori della combo TIPO ANNULLAMENTO
        mappaTipoAnnullamento = ComboGetter.getMappaSortedGenericEnum("ti_annullamento",
                CostantiDB.TipoAnnullamento.values());
    }

    // Gestione DOCUMENTI DETTAGLIO TABS
    /**
     * Attiva il tab Info Principali nel dettaglio di un documento
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabInfoPrincipaliOnClick() throws EMFError {
        getForm().getDocumentiDettaglioTabs()
                .setCurrentTab(getForm().getDocumentiDettaglioTabs().getInfoPrincipali());
        forwardToPublisher(Application.Publisher.DOCUMENTI_UNITA_DOCUMENTARIE_DETAIL);
    }

    /**
     * Attiva il tab Info Fiscali nel dettaglio di un documento
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabInfoVersamentoOnClick() throws EMFError {
        getForm().getDocumentiDettaglioTabs()
                .setCurrentTab(getForm().getDocumentiDettaglioTabs().getInfoVersamento());
        forwardToPublisher(Application.Publisher.DOCUMENTI_UNITA_DOCUMENTARIE_DETAIL);
    }

    @Override
    public void tabXMLRichiestaDocOnClick() throws EMFError {
        getForm().getDocumentiDettaglioTabs()
                .setCurrentTab(getForm().getDocumentiDettaglioTabs().getXMLRichiestaDoc());
        forwardToPublisher(Application.Publisher.DOCUMENTI_UNITA_DOCUMENTARIE_DETAIL);
    }

    @Override
    public void tabXMLRispostaDocOnClick() throws EMFError {
        getForm().getDocumentiDettaglioTabs()
                .setCurrentTab(getForm().getDocumentiDettaglioTabs().getXMLRispostaDoc());
        forwardToPublisher(Application.Publisher.DOCUMENTI_UNITA_DOCUMENTARIE_DETAIL);
    }

    @Override
    public void tabXMLRapportoDocOnClick() throws EMFError {
        getForm().getDocumentiDettaglioTabs()
                .setCurrentTab(getForm().getDocumentiDettaglioTabs().getXMLRapportoDoc());
        forwardToPublisher(Application.Publisher.DOCUMENTI_UNITA_DOCUMENTARIE_DETAIL);
    }

    // Gestione DOCUMENTI DETTAGLIO LISTS TABS
    /**
     * Attiva il tab Lista Componenti nel dettaglio di un documento
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabListaComponentiOnClick() throws EMFError {
        getForm().getDocumentiDettaglioListsTabs()
                .setCurrentTab(getForm().getDocumentiDettaglioListsTabs().getListaComponenti());
        forwardToPublisher(Application.Publisher.DOCUMENTI_UNITA_DOCUMENTARIE_DETAIL);
    }

    /**
     * Attiva il tab Lista Dati Specifici nel dettaglio di un documento
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabListaDatiSpecificiDocOnClick() throws EMFError {
        getForm().getDocumentiDettaglioListsTabs().setCurrentTab(
                getForm().getDocumentiDettaglioListsTabs().getListaDatiSpecificiDoc());
        forwardToPublisher(Application.Publisher.DOCUMENTI_UNITA_DOCUMENTARIE_DETAIL);
    }

    /**
     * Attiva il tab Lista Dati Specifici Migrazione nel dettaglio di un documento
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabListaDatiSpecificiMigrazioneDocOnClick() throws EMFError {
        getForm().getDocumentiDettaglioListsTabs().setCurrentTab(
                getForm().getDocumentiDettaglioListsTabs().getListaDatiSpecificiMigrazioneDoc());
        forwardToPublisher(Application.Publisher.DOCUMENTI_UNITA_DOCUMENTARIE_DETAIL);
    }

    @Override
    public void scarica_comp_file_ud() throws EMFError {
        BigDecimal idUnitaDoc = getForm().getUnitaDocumentarieDetail().getId_unita_doc().parse();
        try {
            // EVO#16486
            this.verificaUrnUd(idUnitaDoc.longValue());
            scaricaCompUd(CostantiDB.TipiEntitaRecupero.UNI_DOC);
        } catch (ParerInternalError e) {
            log.error("Eccezione nel recupero del file unità documentaria ", e);
            getMessageBox().addError(
                    "Eccezione nel recupero del file unità documentaria: " + e.getDescription());
        } catch (Exception e) {
            String message = "Eccezione nel recupero del file unità documentaria "
                    + ExceptionUtils.getRootCauseMessage(e);
            getMessageBox()
                    .addError("Eccezione nel recupero del file unità documentaria: " + message);
            log.error("Eccezione nel recupero del file unità documentaria ", e);
        }
    }

    @Override
    public void scarica_dip_esibizione_ud() throws EMFError {
        BigDecimal idUnitaDoc = getForm().getUnitaDocumentarieDetail().getId_unita_doc().parse();
        try {
            // EVO#16486
            this.verificaUrnUd(idUnitaDoc.longValue());
            scaricaCompUd(CostantiDB.TipiEntitaRecupero.UNI_DOC_DIP_ESIBIZIONE);
        } catch (ParerInternalError e) {
            log.error("Eccezione nel recupero del DIP ", e);
            getMessageBox().addError("Eccezione nel recupero del DIP: " + e.getDescription());
        } catch (Exception e) {
            String message = "Eccezione nel recupero del DIP "
                    + ExceptionUtils.getRootCauseMessage(e);
            getMessageBox().addError("Eccezione nel recupero del DIP: " + message);
            log.error("Eccezione nel recupero del DIP ", e);
        }
    }

    /**
     * Metodo per fare il download dei componenti di tipo file dei documenti di una determinata
     * unitÃ documentaria in file zip (Franz Edition)
     */
    private void scaricaCompUd(CostantiDB.TipiEntitaRecupero tipoEntitaRecupero) throws EMFError {
        Recupero recupero = new Recupero();
        recupero.setVersione("Web");
        // Versatore
        recupero.setVersatore(new VersatoreType());
        recupero.getVersatore()
                .setAmbiente(getForm().getUnitaDocumentarieDetail().getNm_ambiente().parse());
        recupero.getVersatore()
                .setEnte(getForm().getUnitaDocumentarieDetail().getNm_ente().parse());
        recupero.getVersatore()
                .setStruttura(getForm().getUnitaDocumentarieDetail().getNm_strut().parse());
        recupero.getVersatore().setUserID(getUser().getIdUtente() + "");
        // Chiave
        recupero.setChiave(new ChiaveType());
        recupero.getChiave().setTipoRegistro(
                getForm().getUnitaDocumentarieDetail().getCd_registro_key_unita_doc().parse());
        recupero.getChiave().setAnno(BigInteger.valueOf(
                getForm().getUnitaDocumentarieDetail().getAa_key_unita_doc().parse().longValue()));
        recupero.getChiave()
                .setNumero(getForm().getUnitaDocumentarieDetail().getCd_key_unita_doc().parse());

        BigDecimal idUnitaDoc = getForm().getUnitaDocumentarieDetail().getId_unita_doc().parse();
        String tipoSaveFile = udHelper.getTipoSaveFile(
                getForm().getUnitaDocumentarieDetail().getId_tipo_unita_doc().parse());
        CostantiDB.TipoSalvataggioFile tipoSalvataggioFile = CostantiDB.TipoSalvataggioFile
                .valueOf(tipoSaveFile);

        RecuperoWeb recuperoUD = new RecuperoWeb(recupero, getUser(), idUnitaDoc,
                tipoSalvataggioFile, tipoEntitaRecupero);
        RispostaWSRecupero rispostaWs = recuperoUD.recuperaOggetto();

        switch (rispostaWs.getSeverity()) {
        case OK:
            getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(),
                    getControllerName());
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
    }

    public void download() throws EMFError, IOException {
        String filename = (String) getSession()
                .getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name());
        String path = (String) getSession()
                .getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name());
        String contentType = (String) getSession()
                .getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name());
        Boolean deleteFile = Boolean.parseBoolean((String) getSession()
                .getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name()));
        if (path != null && filename != null) {
            File fileToDownload = new File(path);
            if (fileToDownload.exists()) {
                /*
                 * Definiamo l'output previsto che sarÃ un file in formato zip di cui si occuperÃ la
                 * servlet per fare il download
                 */
                getResponse().setContentType(
                        StringUtils.isBlank(contentType) ? "application/zip" : contentType);
                getResponse().setHeader("Content-Disposition",
                        "attachment; filename=\"" + filename);

                try (OutputStream outUD = getServletOutputStream();
                        FileInputStream inputStream = new FileInputStream(fileToDownload);) {

                    getResponse().setHeader("Content-Length",
                            String.valueOf(fileToDownload.length()));
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
                if (Boolean.TRUE.equals(deleteFile)) {
                    FileUtils.deleteQuietly(fileToDownload);
                }
            } else {
                getMessageBox()
                        .addError("Errore durante il tentativo di download. File non trovato");
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

    /**
     * Metodo per fare il download dei componenti di tipo file di un determinato documento in un
     * file zip
     */
    @Override
    public void scarica_comp_file_doc() throws EMFError {
        BigDecimal idUnitaDoc = getForm().getUnitaDocumentarieDetail().getId_unita_doc().parse();
        try {
            // EVO#16486
            this.verificaUrnUd(idUnitaDoc.longValue());
            scaricaCompDoc(CostantiDB.TipiEntitaRecupero.DOC);
        } catch (ParerInternalError e) {
            log.error("Eccezione nel recupero del file documento ", e);
            getMessageBox()
                    .addError("Eccezione nel recupero del file documento: " + e.getDescription());
        } catch (Exception e) {
            String message = "Eccezione nel recupero del file documento "
                    + ExceptionUtils.getRootCauseMessage(e);
            getMessageBox().addError("Eccezione nel recupero del file documento: " + message);
            log.error("Eccezione nel recupero del file documento ", e);
        }
    }

    @Override
    public void scarica_dip_esibizione_doc() throws EMFError {
        scaricaCompDoc(CostantiDB.TipiEntitaRecupero.DOC_DIP_ESIBIZIONE);
    }

    private void scaricaCompDoc(CostantiDB.TipiEntitaRecupero tipoEntitaRecupero) throws EMFError {
        // ricavo la lista dei componenti di tipo file di quel determinato documento
        BigDecimal idDoc = (BigDecimal) getSession().getAttribute("iddoc");
        BigDecimal idUd = (BigDecimal) getSession().getAttribute("idud");
        String tipoSaveFile = udHelper.getTipoSaveFile(
                getForm().getDocumentiUnitaDocumentarieDetail().getId_tipo_unita_doc().parse());
        CostantiDB.TipoSalvataggioFile tipoSalvataggioFile = CostantiDB.TipoSalvataggioFile
                .valueOf(tipoSaveFile);

        Recupero recupero = new Recupero();
        recupero.setVersione("Web");
        // Versatore
        recupero.setVersatore(new VersatoreType());
        recupero.getVersatore().setAmbiente(
                getUser().getOrganizzazioneMap().get(WebConstants.Organizzazione.AMBIENTE.name()));
        recupero.getVersatore().setEnte(
                getUser().getOrganizzazioneMap().get(WebConstants.Organizzazione.ENTE.name()));
        recupero.getVersatore().setStruttura(
                getUser().getOrganizzazioneMap().get(WebConstants.Organizzazione.STRUTTURA.name()));
        recupero.getVersatore().setUserID(getUser().getIdUtente() + "");
        // Chiave
        recupero.setChiave(new ChiaveType());
        recupero.getChiave().setTipoRegistro(getForm().getDocumentiUnitaDocumentarieDetail()
                .getCd_registro_key_unita_doc().parse());
        recupero.getChiave().setAnno(BigInteger.valueOf(getForm()
                .getDocumentiUnitaDocumentarieDetail().getAa_key_unita_doc().parse().longValue()));
        recupero.getChiave().setNumero(
                getForm().getDocumentiUnitaDocumentarieDetail().getCd_key_unita_doc().parse());

        RecuperoWeb recuperoDoc = new RecuperoWeb(recupero, getUser(), idUd, idDoc,
                tipoSalvataggioFile, tipoEntitaRecupero);
        RispostaWSRecupero rispostaWs = recuperoDoc.recuperaOggetto();

        switch (rispostaWs.getSeverity()) {
        case OK:
            getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(),
                    getControllerName());
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
    }

    /**
     * Metodo per fare il download dei componenti di tipo file di un determinato documento in un
     * file zip
     */
    @Override
    public void scarica_xml_unisincro() throws EMFError {
        BigDecimal idUnitaDoc = getForm().getUnitaDocumentarieDetail().getId_unita_doc().parse();
        try {
            // EVO#16486
            this.verificaUrnUd(idUnitaDoc.longValue());
            Recupero recupero = new Recupero();
            recupero.setVersione("Web");
            // Versatore
            recupero.setVersatore(new VersatoreType());
            recupero.getVersatore()
                    .setAmbiente(getForm().getUnitaDocumentarieDetail().getNm_ambiente().parse());
            recupero.getVersatore()
                    .setEnte(getForm().getUnitaDocumentarieDetail().getNm_ente().parse());
            recupero.getVersatore()
                    .setStruttura(getForm().getUnitaDocumentarieDetail().getNm_strut().parse());
            recupero.getVersatore().setUserID(getUser().getIdUtente() + "");
            // Chiave
            recupero.setChiave(new ChiaveType());
            recupero.getChiave().setTipoRegistro(
                    getForm().getUnitaDocumentarieDetail().getCd_registro_key_unita_doc().parse());
            recupero.getChiave().setAnno(BigInteger.valueOf(getForm().getUnitaDocumentarieDetail()
                    .getAa_key_unita_doc().parse().longValue()));
            recupero.getChiave().setNumero(
                    getForm().getUnitaDocumentarieDetail().getCd_key_unita_doc().parse());

            String tipoSaveFile = udHelper.getTipoSaveFile(
                    getForm().getUnitaDocumentarieDetail().getId_tipo_unita_doc().parse());
            CostantiDB.TipoSalvataggioFile tipoSalvataggioFile = CostantiDB.TipoSalvataggioFile
                    .valueOf(tipoSaveFile);
            CostantiDB.TipiEntitaRecupero tipoEntitaRecupero = CostantiDB.TipiEntitaRecupero.UNI_DOC_UNISYNCRO;
            // EVO#20972
            // In questa fase in cui si sta richiedendo il
            // recupero per la generazione del pacchetto AIP,
            // è necessario gestire coerentemente il parametro
            // del servizio di recupero in base alla versione Unisincro con cui
            // è stato prodotto l'ultimo Indice AIP (se presente), perchè il pacchetto AIP viene
            // generato in
            // modo differente impostando il valore
            // UNI_DOC_UNISYNCRO (versioni 0.X) o UNI_DOC_UNISYNCRO_V2 (versioni 1.X).
            AroVerIndiceAipUd verIndiceAipUd = udHelper
                    .getUltimaVersioneIndiceAip(idUnitaDoc.longValue());
            if (verIndiceAipUd != null) {
                // Scompatto il campo cdVerIndiceAip
                String[] numbers = verIndiceAipUd.getCdVerIndiceAip().split("[.]");
                int majorNumber = Integer.parseInt(numbers[0]);
                if (majorNumber > 0) {
                    tipoEntitaRecupero = CostantiDB.TipiEntitaRecupero.UNI_DOC_UNISYNCRO_V2;
                }
            }
            // end EVO#20972
            RecuperoWeb recuperoUD = new RecuperoWeb(recupero, getUser(), idUnitaDoc,
                    tipoSalvataggioFile, tipoEntitaRecupero);
            RispostaWSRecupero rispostaWs = recuperoUD.recuperaOggetto();

            switch (rispostaWs.getSeverity()) {
            case OK:
                getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(),
                        getControllerName());
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
            // } catch (ParerInternalError e) {
            // log.error("Eccezione nel recupero AIP ", e);
            // getMessageBox().addError("Eccezione nel recupero AIP: " + e.getDescription());
        } catch (Exception e) {
            String message = "Eccezione nel recupero AIP " + ExceptionUtils.getRootCauseMessage(e);
            getMessageBox().addError("Eccezione nel recupero AIP: " + message);
            log.error("Eccezione nel recupero AIP ", e);
        }
    }

    public void scarica_xml_rich(String... rigaElemento) throws EMFError {
        AroVRicUnitaDocRowBean row;
        if (rigaElemento != null) {
            int startIndex = (getForm().getUnitaDocumentarieAnnullateList().getTable()
                    .getCurrentPageIndex() - 1)
                    * getForm().getUnitaDocumentarieAnnullateList().getTable().getPageSize();
            int endIndex = ((getForm().getUnitaDocumentarieAnnullateList().getTable()
                    .getCurrentPageIndex() - 1)
                    * getForm().getUnitaDocumentarieAnnullateList().getTable().getPageSize())
                    + getForm().getUnitaDocumentarieAnnullateList().getTable().getPageSize();
            row = (AroVRicUnitaDocRowBean) getForm().getUnitaDocumentarieAnnullateList().getTable()
                    .getRow((Integer.parseInt(rigaElemento[0]) + startIndex) % endIndex);
        } else {
            row = (AroVRicUnitaDocRowBean) getForm().getUnitaDocumentarieAnnullateList().getTable()
                    .getCurrentRow();
        }

        // Ricavo l'identificativo della richiesta di annullamento
        BigDecimal idRichAnnulVers = row.getBigDecimal("id_rich_annul_vers");

        AroVVisUnitaDocIamRowBean udRB = udHelper.getAroVVisUnitaDocIamRowBean(row.getIdUnitaDoc());

        String filename = "RichiestaAnnullamento-UD_" + udRB.getCdRegistroKeyUnitaDoc() + "-"
                + udRB.getAaKeyUnitaDoc().toString() + "-" + udRB.getCdKeyUnitaDoc();

        /*
         * Definiamo l'output previsto che sarà un file in formato zip di cui si occuperà la servlet
         * per fare il download
         */
        ZipOutputStream outUD = new ZipOutputStream(getServletOutputStream());
        getResponse().setContentType("application/zip");
        getResponse().setHeader("Content-Disposition",
                "attachment; filename=\"" + filename + ".zip");

        try {
            zippaXmlAnnulVers(outUD, udRB, idRichAnnulVers,
                    CostantiDB.TiXmlRichAnnulVers.RICHIESTA);
            outUD.flush();
            outUD.close();
        } catch (Exception e) {
            log.error("Eccezione", e);
        } finally {
            freeze();
        }
    }

    public void scarica_xml_risp(String... rigaElemento) throws EMFError {
        AroVRicUnitaDocRowBean row;
        if (rigaElemento != null) {
            int startIndex = (getForm().getUnitaDocumentarieAnnullateList().getTable()
                    .getCurrentPageIndex() - 1)
                    * getForm().getUnitaDocumentarieAnnullateList().getTable().getPageSize();
            int endIndex = ((getForm().getUnitaDocumentarieAnnullateList().getTable()
                    .getCurrentPageIndex() - 1)
                    * getForm().getUnitaDocumentarieAnnullateList().getTable().getPageSize())
                    + getForm().getUnitaDocumentarieAnnullateList().getTable().getPageSize();
            row = (AroVRicUnitaDocRowBean) getForm().getUnitaDocumentarieAnnullateList().getTable()
                    .getRow((Integer.parseInt(rigaElemento[0]) + startIndex) % endIndex);
        } else {
            row = (AroVRicUnitaDocRowBean) getForm().getUnitaDocumentarieAnnullateList().getTable()
                    .getCurrentRow();
        }

        // Ricavo l'identificativo della richiesta di annullamento
        BigDecimal idRichAnnulVers = row.getBigDecimal("id_rich_annul_vers");

        AroVVisUnitaDocIamRowBean udRB = udHelper.getAroVVisUnitaDocIamRowBean(row.getIdUnitaDoc());

        String filename = "EsitoAnnullamento-UD_" + udRB.getCdRegistroKeyUnitaDoc() + "-"
                + udRB.getAaKeyUnitaDoc().toString() + "-" + udRB.getCdKeyUnitaDoc();

        /*
         * Definiamo l'output previsto che sarà un file in formato zip di cui si occuperà la servlet
         * per fare il download
         */
        ZipOutputStream outUD = new ZipOutputStream(getServletOutputStream());
        getResponse().setContentType("application/zip");
        getResponse().setHeader("Content-Disposition",
                "attachment; filename=\"" + filename + ".zip");

        try {
            zippaXmlAnnulVers(outUD, udRB, idRichAnnulVers, CostantiDB.TiXmlRichAnnulVers.RISPOSTA);
            outUD.flush();
            outUD.close();
        } catch (Exception e) {
            log.error("Eccezione", e);
        } finally {
            freeze();
        }
    }

    /**
     * Metodo che genera il file zip dato l'outputStream e i dati da inserire
     *
     * @param out  l'outputStream da utilizzare
     * @param udRB il rowBean contenente i dati
     *
     * @throws EMFError    errore generico
     * @throws IOException
     */
    private void zippaXmlAnnulVers(ZipOutputStream out, AroVVisUnitaDocIamRowBean udRB,
            BigDecimal idRichAnnulVers, CostantiDB.TiXmlRichAnnulVers tiXmlRichAnnulVers)
            throws EMFError, IOException {
        // Definiamo il buffer per lo stream di bytes
        byte[] data = new byte[1000];
        InputStream is = null;

        // Se ho la richiesta di annullamento, piazzo nello zippone i suoi CLOBBI di richiesta o
        // risposta
        if (udRB != null) {
            // Ricavo i nomi dei file xml di richiesta o risposta
            String prefisso = udRB.getCdRegistroKeyUnitaDoc() + "-" + udRB.getAaKeyUnitaDoc() + "-"
                    + udRB.getCdKeyUnitaDoc();

            switch (tiXmlRichAnnulVers) {
            case RICHIESTA:
                String xmlRichAnnulVersName = "RichiestaAnnullamento-UD_" + prefisso + ".xml";

                // Ricavo il CLOBBO, che è uno stringone
                String xmlRichAnnulVersClob = annulVersEjb
                        .getXmlRichAnnulVersByTipo(idRichAnnulVers, tiXmlRichAnnulVers);

                // Inserisco nello zippone il CLOB dell'xml di richiesta dell'annullamento
                // dell'unità documentaria
                if (xmlRichAnnulVersClob != null && !xmlRichAnnulVersClob.isEmpty()) {
                    is = new ByteArrayInputStream(
                            xmlRichAnnulVersClob.getBytes(StandardCharsets.UTF_8));
                    int count;
                    out.putNextEntry(new ZipEntry(xmlRichAnnulVersName));
                    while ((count = is.read(data, 0, 1000)) != -1) {
                        out.write(data, 0, count);
                    }
                    out.closeEntry();
                }
                break;
            case RISPOSTA:
                String xmlRispAnnulVersName = "EsitoAnnullamento-UD_" + prefisso + ".xml";

                // Ricavo il CLOBBO, che è uno stringone
                String xmlRispAnnulVersClob = annulVersEjb
                        .getXmlRichAnnulVersByTipo(idRichAnnulVers, tiXmlRichAnnulVers);

                // Inserisco nello zippone il CLOB dell'xml di risposta dell'annullamento dell'unità
                // documentaria
                if (xmlRispAnnulVersClob != null && !xmlRispAnnulVersClob.isEmpty()) {
                    is = new ByteArrayInputStream(
                            xmlRispAnnulVersClob.getBytes(StandardCharsets.UTF_8));
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

    public void scarica_rv(String... rigaElemento) throws EMFError {
        AroVRicUnitaDocRowBean row;
        if (rigaElemento != null) {
            int startIndex = (getForm().getUnitaDocumentarieAnnullateList().getTable()
                    .getCurrentPageIndex() - 1)
                    * getForm().getUnitaDocumentarieAnnullateList().getTable().getPageSize();
            int endIndex = ((getForm().getUnitaDocumentarieAnnullateList().getTable()
                    .getCurrentPageIndex() - 1)
                    * getForm().getUnitaDocumentarieAnnullateList().getTable().getPageSize())
                    + getForm().getUnitaDocumentarieAnnullateList().getTable().getPageSize();
            row = (AroVRicUnitaDocRowBean) getForm().getUnitaDocumentarieAnnullateList().getTable()
                    .getRow((Integer.parseInt(rigaElemento[0]) + startIndex) % endIndex);
        } else {
            row = (AroVRicUnitaDocRowBean) getForm().getUnitaDocumentarieAnnullateList().getTable()
                    .getCurrentRow();
        }
        getSession().setAttribute("idud", row.getIdUnitaDoc());
        try {
            // EVO#16486
            this.verificaUrnUd(row.getIdUnitaDoc().longValue());
            scarica_rv();
        } catch (ParerInternalError e) {
            log.error("Eccezione nel recupero del Rapporto di versamento ", e);
            getMessageBox().addError(ECCEZIONE_RECUPERO_RAPPORTO_VERSAMENTO + e.getDescription());
        } catch (Exception e) {
            String message = "Eccezione nel recupero del Rapporto di versamento "
                    + ExceptionUtils.getRootCauseMessage(e);
            getMessageBox().addError(ECCEZIONE_RECUPERO_RAPPORTO_VERSAMENTO + message);
            log.error("Eccezione nel recupero del Rapporto di versamento ", e);
        }
    }

    @Override
    public void scarica_rv() throws EMFError {
        BigDecimal idud = (BigDecimal) getSession().getAttribute("idud");
        try {
            // EVO#16486
            this.verificaUrnUd(idud.longValue());
            /*
             * Ricavo l'unità documentaria prendendo i dati da DB anche se li ho già nella maschera
             * di dettaglio, onde evitare che un domani, eliminando quei campi dall'online, vada
             * tutto in vacca
             */
            AroVVisUnitaDocIamRowBean udRB = udHelper.getAroVVisUnitaDocIamRowBean(idud);
            String filename = "RV-UD_" + udRB.getCdRegistroKeyUnitaDoc() + "-"
                    + udRB.getAaKeyUnitaDoc().toString() + "-" + udRB.getCdKeyUnitaDoc();
            Date dataRegUd = udRB.getDtRegUnitaDoc();

            /*
             * Definiamo l'output previsto che sarà un file in formato zip di cui si occuperà la
             * servlet per fare il download
             */
            ZipOutputStream outUD = new ZipOutputStream(getServletOutputStream());
            getResponse().setContentType("application/zip");
            getResponse().setHeader("Content-Disposition",
                    "attachment; filename=\"" + filename + ".zip");

            // Ricavo la lista dei documenti AGGIUNTI per quella determinata UD
            AroVVisDocIamTableBean docTB = udHelper.getAroVVisDocAggIamTableBean(idud, dataRegUd);

            try {
                zippaRapportoVersamento(outUD, udRB, docTB);
                outUD.flush();
                outUD.close();
            } catch (Exception e) {
                log.error("Eccezione", e);
            } finally {
                freeze();
            }
        } catch (ParerInternalError e) {
            log.error("Eccezione nel recupero del Rapporto di versamento ", e);
            getMessageBox().addError(ECCEZIONE_RECUPERO_RAPPORTO_VERSAMENTO + e.getDescription());
        } catch (Exception e) {
            String message = "Eccezione nel recupero del Rapporto di versamento "
                    + ExceptionUtils.getRootCauseMessage(e);
            getMessageBox().addError(ECCEZIONE_RECUPERO_RAPPORTO_VERSAMENTO + message);
            log.error("Eccezione nel recupero del Rapporto di versamento ", e);
        }
    }

    /**
     * Metodo richiamato al click del bottone "Scarica SIP unità documentaria". Implementa la
     * funzionalità con il meccanismo utilizzato per il recupero tramite WS (nonostante non lo sia)
     * effettuando il download dopo alcuni secondi dei file precedentemente salvati su disco
     *
     * @throws EMFError errore generico
     */
    @Override
    public void scarica_sip_ud() throws EMFError {
        BigDecimal idUnitaDoc = getForm().getUnitaDocumentarieDetail().getId_unita_doc().parse();
        try {
            // EVO#16486
            this.verificaUrnUd(idUnitaDoc.longValue());
            Recupero recupero = new Recupero();
            recupero.setVersione("Web");
            // Versatore
            recupero.setVersatore(new VersatoreType());
            recupero.getVersatore()
                    .setAmbiente(getForm().getUnitaDocumentarieDetail().getNm_ambiente().parse());
            recupero.getVersatore()
                    .setEnte(getForm().getUnitaDocumentarieDetail().getNm_ente().parse());
            recupero.getVersatore()
                    .setStruttura(getForm().getUnitaDocumentarieDetail().getNm_strut().parse());
            recupero.getVersatore().setUserID(getUser().getIdUtente() + "");
            // Chiave
            recupero.setChiave(new ChiaveType());
            recupero.getChiave().setTipoRegistro(
                    getForm().getUnitaDocumentarieDetail().getCd_registro_key_unita_doc().parse());
            recupero.getChiave().setAnno(BigInteger.valueOf(getForm().getUnitaDocumentarieDetail()
                    .getAa_key_unita_doc().parse().longValue()));
            recupero.getChiave().setNumero(
                    getForm().getUnitaDocumentarieDetail().getCd_key_unita_doc().parse());

            String tipoSaveFile = udHelper.getTipoSaveFile(
                    getForm().getUnitaDocumentarieDetail().getId_tipo_unita_doc().parse());
            CostantiDB.TipoSalvataggioFile tipoSalvataggioFile = CostantiDB.TipoSalvataggioFile
                    .valueOf(tipoSaveFile);

            RecuperoWeb recuperoSip = new RecuperoWeb(recupero, getUser(), idUnitaDoc,
                    tipoSalvataggioFile, CostantiDB.TipiEntitaRecupero.UNI_DOC);
            RispostaWSRecupero rispostaWs = recuperoSip.recuperaOggettoSip();

            switch (rispostaWs.getSeverity()) {
            case OK:
                getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(),
                        getControllerName());
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
        } catch (ParerInternalError e) {
            log.error("Eccezione nel recupero SIP unità documentaria ", e);
            getMessageBox().addError(
                    "Eccezione nel recupero SIP unità documentaria: " + e.getDescription());
        } catch (Exception e) {
            String message = "Eccezione nel recupero SIP unità documentaria "
                    + ExceptionUtils.getRootCauseMessage(e);
            getMessageBox().addError("Eccezione nel recupero SIP unità documentaria: " + message);
            log.error("Eccezione nel recupero SIP unità documentaria ", e);
        }
    }

    @Override
    public void scarica_sip_doc() throws EMFError {
        BigDecimal idUnitaDoc = getForm().getUnitaDocumentarieDetail().getId_unita_doc().parse();
        try {
            // EVO#16486
            this.verificaUrnUd(idUnitaDoc.longValue());
            Recupero recupero = new Recupero();
            recupero.setVersione("Web");
            // Versatore
            recupero.setVersatore(new VersatoreType());
            recupero.getVersatore()
                    .setAmbiente(getForm().getUnitaDocumentarieDetail().getNm_ambiente().parse());
            recupero.getVersatore()
                    .setEnte(getForm().getUnitaDocumentarieDetail().getNm_ente().parse());
            recupero.getVersatore()
                    .setStruttura(getForm().getUnitaDocumentarieDetail().getNm_strut().parse());
            recupero.getVersatore().setUserID(getUser().getIdUtente() + "");
            // Chiave
            recupero.setChiave(new ChiaveType());
            recupero.getChiave().setTipoRegistro(
                    getForm().getUnitaDocumentarieDetail().getCd_registro_key_unita_doc().parse());
            recupero.getChiave().setAnno(BigInteger.valueOf(getForm().getUnitaDocumentarieDetail()
                    .getAa_key_unita_doc().parse().longValue()));
            recupero.getChiave().setNumero(
                    getForm().getUnitaDocumentarieDetail().getCd_key_unita_doc().parse());

            BigDecimal idDoc = ((AroVLisDocRowBean) getForm().getDocumentiUDList().getTable()
                    .getCurrentRow()).getIdDoc();
            String tipoSaveFile = udHelper.getTipoSaveFile(
                    getForm().getUnitaDocumentarieDetail().getId_tipo_unita_doc().parse());
            CostantiDB.TipoSalvataggioFile tipoSalvataggioFile = CostantiDB.TipoSalvataggioFile
                    .valueOf(tipoSaveFile);

            RecuperoWeb recuperoSip = new RecuperoWeb(recupero, getUser(), idUnitaDoc, idDoc,
                    tipoSalvataggioFile, CostantiDB.TipiEntitaRecupero.DOC);
            RispostaWSRecupero rispostaWs = recuperoSip.recuperaOggettoSip();

            switch (rispostaWs.getSeverity()) {
            case OK:
                getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(),
                        getControllerName());
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
        } catch (ParerInternalError e) {
            log.error("Eccezione nel recupero SIP documento ", e);
            getMessageBox().addError("Eccezione nel recupero SIP documento: " + e.getDescription());
        } catch (Exception e) {
            String message = "Eccezione nel recupero SIP documento "
                    + ExceptionUtils.getRootCauseMessage(e);
            getMessageBox().addError("Eccezione nel recupero SIP documento: " + message);
            log.error("Eccezione nel recupero SIP documento ", e);
        }
    }

    /**
     * Metodo che fornisce uno stack utilizzato per mantenere gli id delle unitÃ documentarie
     * visualizzate
     *
     * @return lo stack di unitÃ documentarie
     */
    public List<BigDecimal> getIdUdStack() {
        if (getSession().getAttribute("idUdStack") == null) {
            getSession().setAttribute("idUdStack", new ArrayList<BigDecimal>());
        }
        return (List<BigDecimal>) getSession().getAttribute("idUdStack");
    }

    public List<Integer> getDocInVolumeChiuso() {
        if (getSession().getAttribute("docInVolumeChiuso") == null) {
            getSession().setAttribute("docInVolumeChiuso", new ArrayList<Integer>());
        }
        return (List<Integer>) getSession().getAttribute("docInVolumeChiuso");
    }

    public List<Integer> getDocInVolumeAperto() {
        if (getSession().getAttribute("docInVolumeAperto") == null) {
            getSession().setAttribute("docInVolumeAperto", new ArrayList<Integer>());
        }
        return (List<Integer>) getSession().getAttribute("docInVolumeAperto");
    }

    public List<Integer> getDocInVolumeInErrore() {
        if (getSession().getAttribute("docInVolumeInErrore") == null) {
            getSession().setAttribute("docInVolumeInErrore", new ArrayList<Integer>());
        }
        return (List<Integer>) getSession().getAttribute("docInVolumeInErrore");
    }

    public List<Integer> getDocInVolumeDaChiudere() {
        if (getSession().getAttribute("docInVolumeDaChiudere") == null) {
            getSession().setAttribute("docInVolumeDaChiudere", new ArrayList<Integer>());
        }
        return (List<Integer>) getSession().getAttribute("docInVolumeDaChiudere");
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        if (publisherName
                .equalsIgnoreCase(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_AVANZATA)) {
            // Risalvo in sessione la struttura dati contenente la Lista Dari Specifici compilati a
            // video
            getSession().setAttribute("listaDatiSpecOnLine",
                    getSession().getAttribute("listaDatiSpecOnLineOld"));

            // Controlla che i filtri dati specifici siano stati compilati correttamente
            updateInterfacciaOnLineDatiSpec((List<DecCriterioDatiSpecBean>) getSession()
                    .getAttribute("listaDatiSpecOnLine"), true);
        } else if (publisherName.equals(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL)
                && !getIdUdStack().isEmpty()) {
            try {
                List<BigDecimal> idUdStack = getIdUdStack();
                BigDecimal id = idUdStack.remove(idUdStack.size() - 1);
                getSession().setAttribute("idUdStack", idUdStack);
                getDettaglioUD(id);

                if (getForm().getFakeUnitaDocumentarieList().getTable() != null
                        && !getForm().getFakeUnitaDocumentarieList().getTable().isEmpty()) {
                    getForm().getFakeUnitaDocumentarieList().getTable().last();
                    getForm().getFakeUnitaDocumentarieList().getTable().remove();
                }
            } catch (EMFError ex) {
                getMessageBox()
                        .addError("Errore inatteso nel caricamento della unit\u00E0 documentaria");
            }
        }
        postLoad();
    }

    // Gestione AGGIORNAMENTO METADATI DETTAGLIO TABS
    /**
     * Attiva il tab Info Principali nel dettaglio di un aggiornamento metadati
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabInfoPrincipaliUpdOnClick() throws EMFError {
        getForm().getAggiornamentiDettaglioTabs()
                .setCurrentTab(getForm().getAggiornamentiDettaglioTabs().getInfoPrincipaliUpd());
        forwardToPublisher(Application.Publisher.AGGIORNAMENTI_METADATI_UDDETAIL);
    }

    /**
     * Attiva il tab Info Versamento nel dettaglio di un aggiornamento metadati
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabInfoVersamentoUpdOnClick() throws EMFError {
        getForm().getAggiornamentiDettaglioTabs()
                .setCurrentTab(getForm().getAggiornamentiDettaglioTabs().getInfoVersamentoUpd());
        forwardToPublisher(Application.Publisher.AGGIORNAMENTI_METADATI_UDDETAIL);
    }

    /**
     * Attiva il tab Indice SIP aggiornamento nel dettaglio di un aggiornamento metadati
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabXMLRichiestaUpdOnClick() throws EMFError {
        getForm().getAggiornamentiDettaglioTabs()
                .setCurrentTab(getForm().getAggiornamentiDettaglioTabs().getXMLRichiestaUpd());
        forwardToPublisher(Application.Publisher.AGGIORNAMENTI_METADATI_UDDETAIL);
    }

    /**
     * Attiva il tab Rapporto versamento nel dettaglio di un aggiornamento metadati
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabXMLRispostaUpdOnClick() throws EMFError {
        getForm().getAggiornamentiDettaglioTabs()
                .setCurrentTab(getForm().getAggiornamentiDettaglioTabs().getXMLRispostaUpd());
        forwardToPublisher(Application.Publisher.AGGIORNAMENTI_METADATI_UDDETAIL);
    }

    // Gestione AGGIORNAMENTO METADATI DETTAGLIO LISTS TABS
    /**
     * Attiva il tab Lista documenti aggiornati nel dettaglio di un aggiornamento
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabListaDocAggiornatiOnClick() throws EMFError {
        getForm().getAggiornamentiDettaglioListsTabs().setCurrentTab(
                getForm().getAggiornamentiDettaglioListsTabs().getListaDocAggiornati());
        forwardToPublisher(Application.Publisher.AGGIORNAMENTI_METADATI_UDDETAIL);
    }

    /**
     * Attiva il tab Lista componenti aggiornati nel dettaglio di un aggiornamento
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabListaCompAggiornatiOnClick() throws EMFError {
        getForm().getAggiornamentiDettaglioListsTabs().setCurrentTab(
                getForm().getAggiornamentiDettaglioListsTabs().getListaCompAggiornati());
        forwardToPublisher(Application.Publisher.AGGIORNAMENTI_METADATI_UDDETAIL);
    }

    /**
     * Attiva il tab Lista aggiornamenti metadati risolti nel dettaglio di un aggiornamento
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabListaUpdRisoltiOnClick() throws EMFError {
        getForm().getAggiornamentiDettaglioListsTabs()
                .setCurrentTab(getForm().getAggiornamentiDettaglioListsTabs().getListaUpdRisolti());
        forwardToPublisher(Application.Publisher.AGGIORNAMENTI_METADATI_UDDETAIL);
    }

    /**
     * Attiva il tab Lista warning rilevati nel dettaglio di un aggiornamento
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabListaUpdWarningOnClick() throws EMFError {
        getForm().getAggiornamentiDettaglioListsTabs()
                .setCurrentTab(getForm().getAggiornamentiDettaglioListsTabs().getListaUpdWarning());
        forwardToPublisher(Application.Publisher.AGGIORNAMENTI_METADATI_UDDETAIL);
    }

    /**
     * Metodo di caricamento della form di creazione dei criteri di raggruppamento
     *
     * @throws EMFError errore generico
     */
    @Override
    public void creaCriterioRaggr() throws EMFError {
        // Apre pagina di creazione criterio
        CriteriRaggruppamentoForm criteri = new CriteriRaggruppamentoForm();
        Fields<Field> filtri;
        Timestamp dataCreazioneDa = null;
        BigDecimal oreCreazioneDa = null;
        BigDecimal minutiCreazioneDa = null;
        Timestamp dataCreazioneA = null;
        BigDecimal oreCreazioneA = null;
        BigDecimal minutiCreazioneA = null;
        String descDataCreazioneDa = null;
        String descDataCreazioneA = null;
        Timestamp dataRegUDDa = null;
        String descDataRegUDDa = null;
        Timestamp dataRegUDA = null;
        String descDataRegUDA = null;
        Object registro = null;
        BigDecimal anno = null;
        String numero = null;
        // Object regRange = null;
        BigDecimal annoDa = null;
        BigDecimal annoA = null;
        String numeroDa = null;
        String numeroA = null;
        String numeroContiene = null;
        if (getRequest().getParameter("simpleSearch") != null) {
            filtri = getForm().getFiltriUnitaDocumentarieSemplice();
        } else {
            filtri = getForm().getFiltriUnitaDocumentarieAvanzata();
        }
        filtri.post(getRequest());
        if (getRequest().getParameter("simpleSearch") != null) {
            dataCreazioneDa = getForm().getFiltriUnitaDocumentarieSemplice()
                    .getDt_acquisizione_unita_doc_da().parse();
            oreCreazioneDa = getForm().getFiltriUnitaDocumentarieSemplice()
                    .getOre_dt_acquisizione_unita_doc_da().parse();
            minutiCreazioneDa = getForm().getFiltriUnitaDocumentarieSemplice()
                    .getMinuti_dt_acquisizione_unita_doc_da().parse();
            dataCreazioneA = getForm().getFiltriUnitaDocumentarieSemplice()
                    .getDt_acquisizione_unita_doc_a().parse();
            oreCreazioneA = getForm().getFiltriUnitaDocumentarieSemplice()
                    .getOre_dt_acquisizione_unita_doc_a().parse();
            minutiCreazioneA = getForm().getFiltriUnitaDocumentarieSemplice()
                    .getMinuti_dt_acquisizione_unita_doc_a().parse();
            descDataCreazioneDa = getForm().getFiltriUnitaDocumentarieSemplice()
                    .getDt_acquisizione_unita_doc_da().getHtmlDescription();
            descDataCreazioneA = getForm().getFiltriUnitaDocumentarieSemplice()
                    .getDt_acquisizione_unita_doc_a().getHtmlDescription();
            dataRegUDDa = getForm().getFiltriUnitaDocumentarieSemplice().getDt_reg_unita_doc_da()
                    .parse();
            descDataRegUDDa = getForm().getFiltriUnitaDocumentarieSemplice()
                    .getDt_reg_unita_doc_da().getHtmlDescription();
            dataRegUDA = getForm().getFiltriUnitaDocumentarieSemplice().getDt_reg_unita_doc_a()
                    .parse();
            descDataRegUDA = getForm().getFiltriUnitaDocumentarieSemplice().getDt_reg_unita_doc_a()
                    .getHtmlDescription();
            registro = getForm().getFiltriUnitaDocumentarieSemplice().getCd_registro_key_unita_doc()
                    .getValue();
            anno = getForm().getFiltriUnitaDocumentarieSemplice().getAa_key_unita_doc().parse();
            numero = getForm().getFiltriUnitaDocumentarieSemplice().getCd_key_unita_doc().parse();
            annoDa = getForm().getFiltriUnitaDocumentarieSemplice().getAa_key_unita_doc_da()
                    .parse();
            annoA = getForm().getFiltriUnitaDocumentarieSemplice().getAa_key_unita_doc_a().parse();
            numeroDa = getForm().getFiltriUnitaDocumentarieSemplice().getCd_key_unita_doc_da()
                    .parse();
            numeroA = getForm().getFiltriUnitaDocumentarieSemplice().getCd_key_unita_doc_a()
                    .parse();
            numeroContiene = getForm().getFiltriUnitaDocumentarieSemplice()
                    .getCd_key_unita_doc_contiene().parse();
        } else {
            dataCreazioneDa = getForm().getFiltriUnitaDocumentarieAvanzata()
                    .getDt_acquisizione_unita_doc_da().parse();
            oreCreazioneDa = getForm().getFiltriUnitaDocumentarieAvanzata()
                    .getOre_dt_acquisizione_unita_doc_da().parse();
            minutiCreazioneDa = getForm().getFiltriUnitaDocumentarieAvanzata()
                    .getMinuti_dt_acquisizione_unita_doc_da().parse();
            dataCreazioneA = getForm().getFiltriUnitaDocumentarieAvanzata()
                    .getDt_acquisizione_unita_doc_a().parse();
            oreCreazioneA = getForm().getFiltriUnitaDocumentarieAvanzata()
                    .getOre_dt_acquisizione_unita_doc_a().parse();
            minutiCreazioneA = getForm().getFiltriUnitaDocumentarieAvanzata()
                    .getMinuti_dt_acquisizione_unita_doc_a().parse();
            descDataCreazioneDa = getForm().getFiltriUnitaDocumentarieAvanzata()
                    .getDt_acquisizione_unita_doc_da().getHtmlDescription();
            descDataCreazioneA = getForm().getFiltriUnitaDocumentarieAvanzata()
                    .getDt_acquisizione_unita_doc_a().getHtmlDescription();
            dataRegUDDa = getForm().getFiltriUnitaDocumentarieAvanzata().getDt_reg_unita_doc_da()
                    .parse();
            descDataRegUDDa = getForm().getFiltriUnitaDocumentarieAvanzata()
                    .getDt_reg_unita_doc_da().getHtmlDescription();
            dataRegUDA = getForm().getFiltriUnitaDocumentarieAvanzata().getDt_reg_unita_doc_a()
                    .parse();
            descDataRegUDA = getForm().getFiltriUnitaDocumentarieSemplice().getDt_reg_unita_doc_a()
                    .getHtmlDescription();
            registro = getForm().getFiltriUnitaDocumentarieAvanzata().getCd_registro_key_unita_doc()
                    .getValues();
            anno = getForm().getFiltriUnitaDocumentarieAvanzata().getAa_key_unita_doc().parse();
            numero = getForm().getFiltriUnitaDocumentarieAvanzata().getCd_key_unita_doc().parse();
            annoDa = getForm().getFiltriUnitaDocumentarieAvanzata().getAa_key_unita_doc_da()
                    .parse();
            annoA = getForm().getFiltriUnitaDocumentarieAvanzata().getAa_key_unita_doc_a().parse();
            numeroDa = getForm().getFiltriUnitaDocumentarieAvanzata().getCd_key_unita_doc_da()
                    .parse();
            numeroA = getForm().getFiltriUnitaDocumentarieAvanzata().getCd_key_unita_doc_a()
                    .parse();
            numeroContiene = getForm().getFiltriUnitaDocumentarieAvanzata()
                    .getCd_key_unita_doc_contiene().parse();
        }
        // Valida i campi di ricerca
        UnitaDocumentarieValidator validator = new UnitaDocumentarieValidator(getMessageBox());
        // Valido i filtri data creazione da - a restituendo le date comprensive di orario
        validator.validaDate(dataCreazioneDa, oreCreazioneDa, minutiCreazioneDa, dataCreazioneA,
                oreCreazioneA, minutiCreazioneA, descDataCreazioneDa, descDataCreazioneA);
        // Valido i filtri data dei metadati unitÃ documentaria
        validator.validaOrdineDateOrari(dataRegUDDa, dataRegUDA, descDataRegUDDa, descDataRegUDA);
        Object[] chiavi;
        // Valida i campi di chiavi unitÃ documentaria
        if (filtri instanceof FiltriUnitaDocumentarieAvanzata) {
            String[] reg = new String[((Set<String>) registro).size()];
            ((Set<String>) registro).toArray(reg);
            chiavi = validator.validaChiaviUnitaDocRicUd(reg, anno, numero, annoDa, annoA, numeroDa,
                    numeroA, numeroContiene);
            if (chiavi != null && chiavi.length == 5) {
                // Nel caso si tratta del range di chiavi risetto i filtri per i valori di default
                ((FiltriUnitaDocumentarieAvanzata) filtri).getAa_key_unita_doc_da()
                        .setValue(((BigDecimal) chiavi[1]).toString());
                ((FiltriUnitaDocumentarieAvanzata) filtri).getAa_key_unita_doc_a()
                        .setValue(((BigDecimal) chiavi[2]).toString());
                ((FiltriUnitaDocumentarieAvanzata) filtri).getCd_key_unita_doc_da()
                        .setValue((String) chiavi[3]);
                ((FiltriUnitaDocumentarieAvanzata) filtri).getCd_key_unita_doc_a()
                        .setValue((String) chiavi[4]);
            }
        } else if (filtri instanceof FiltriUnitaDocumentarieSemplice) {
            String reg = (String) registro;
            chiavi = validator.validaChiaviUnitaDocRicUd(reg, anno, numero, annoDa, annoA, numeroDa,
                    numeroA, numeroContiene);
            if (chiavi != null && chiavi.length == 5) {
                // Nel caso si tratta del range di chiavi risetto i filtri per i valori di default
                ((FiltriUnitaDocumentarieSemplice) filtri).getAa_key_unita_doc_da()
                        .setValue(((BigDecimal) chiavi[1]).toString());
                ((FiltriUnitaDocumentarieSemplice) filtri).getAa_key_unita_doc_a()
                        .setValue(((BigDecimal) chiavi[2]).toString());
                ((FiltriUnitaDocumentarieSemplice) filtri).getCd_key_unita_doc_da()
                        .setValue((String) chiavi[3]);
                ((FiltriUnitaDocumentarieSemplice) filtri).getCd_key_unita_doc_a()
                        .setValue((String) chiavi[4]);
            }
        }

        if (!getMessageBox().hasError()) {
            getSession().setAttribute("filtriUD", filtri);
            redirectToAction(Application.Actions.CRITERI_RAGGRUPPAMENTO,
                    "?operation=creaCriterioRaggr&provenienza=ricercaUnitaDoc", criteri);
        } else if (getRequest().getParameter("simpleSearch") != null) {
            forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_SEMPLICE);
        } else {
            forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_AVANZATA);
        }
    }

    /**
     * Trigger sul filtro "Tipo UnitÃ Documentaria" per l'aggiunta/rimozione dei dati specifici
     * definiti dal filtro stesso
     *
     * @return l'oggetto JSON dei filtri unitÃ documentaria ricerca avanzata
     *
     * @throws EMFError errore generico
     */
    @Override
    public JSONObject triggerFiltriUnitaDocumentarieAvanzataNm_tipo_unita_docOnTrigger()
            throws EMFError {
        FiltriUnitaDocumentarieAvanzata udfa = getForm().getFiltriUnitaDocumentarieAvanzata();
        // Ricavo l'elenco dei tipi unitÃ documentaria PRIMA di modificare (aggiunta/rimozione) il
        // relativo filtro
        List<BigDecimal> tipoUnitaDocPre = udfa.getNm_tipo_unita_doc().parse();
        // Eseguo la post del filtri
        udfa.post(getRequest());
        // Ricavo l'elenco dei tipi unitÃ documentaria DOPO aver modificato (aggiunta/rimozione) il
        // relativo filtro
        List<BigDecimal> tipoUnitaDocPost = udfa.getNm_tipo_unita_doc().parse();
        // Confronto i due elenchi: se la lunghezza di tipoUnitaDocPre Ã¨ inferiore
        // a quella di tipoUnitaDocPost significa che ho fatto un'aggiunta
        boolean aggiunta = false;
        List<BigDecimal> elementoDiverso = (List<BigDecimal>) CollectionUtils
                .disjunction(tipoUnitaDocPre, tipoUnitaDocPost);
        if (tipoUnitaDocPre.size() < tipoUnitaDocPost.size()) {
            aggiunta = true;
        }

        // Ricavo la Lista Dati Specifici compilati a video
        List<DecCriterioDatiSpecBean> listaDatiSpecOnLine = (ArrayList) getSession()
                .getAttribute("listaDatiSpecOnLine") != null
                        ? (ArrayList) getSession().getAttribute("listaDatiSpecOnLine")
                        : new ArrayList();

        // HO FATTO UN'AGGIUNTA
        if (aggiunta) {
            // Ricavo i dati specifici di quel TIPO UNITA' DOCUMENTARIA aggiunto o tolto
            DecAttribDatiSpecTableBean datiSpecTB = udHelper
                    .getDecAttribDatiSpecTableBean(elementoDiverso.get(0), TipoEntitaSacer.UNI_DOC);
            aggiungiDatiSpecPerTipoUnitaDoc(datiSpecTB, listaDatiSpecOnLine);
        } // HO FATTO UNA RIMOZIONE
        else {
            // Ricavo il nome del TIPO UNITA' DOCUMENTARIA RIMOSSO
            String nmTipoUnitaDoc = udHelper.getDecTipoUnitaDocRowBean(elementoDiverso.get(0))
                    .getNmTipoUnitaDoc();

            // Per ogni DATO SPECIFICO di questo TIPO UNITA' DOCUMENTARIA
            // rimuovo il riferimento al tipo unitÃ documentaria
            for (DecCriterioDatiSpecBean datoSpec : listaDatiSpecOnLine) {
                List<DecCriterioAttribBean> tabellaDefinitoDa = datoSpec.getDecCriterioAttribs();
                for (int i = 0; i < tabellaDefinitoDa.size(); i++) {
                    if (tabellaDefinitoDa.get(i).getNmTipoUnitaDoc() != null && tabellaDefinitoDa
                            .get(i).getNmTipoUnitaDoc().equals(nmTipoUnitaDoc)) {
                        tabellaDefinitoDa.remove(i);
                    }
                }
            }

            // Controllo se ho ancora dati specifici per tutti i tipi unitÃ documentaria
            // e nel frattempo rimuovo gli eventuali dati specifici che non hanno piÃ¹ elementi
            // del tipo "definitoDa"
            boolean hasDSsuTipiUnitaDoc = false;
            Iterator it = listaDatiSpecOnLine.iterator();
            while (it.hasNext()) {
                DecCriterioDatiSpecBean datoSpec = (DecCriterioDatiSpecBean) it.next();
                List<DecCriterioAttribBean> tabellaDefinitoDa = datoSpec.getDecCriterioAttribs();
                if (tabellaDefinitoDa.isEmpty()) {
                    it.remove();
                } else {
                    for (DecCriterioAttribBean rigaDefinitoDa : tabellaDefinitoDa) {
                        if (rigaDefinitoDa.getNmTipoUnitaDoc() != null) {
                            hasDSsuTipiUnitaDoc = true;
                        }
                    }
                }
            }

            // Se non ho piÃ¹ dati specifici, tolgo la spunta alla CheckBox
            if (!hasDSsuTipiUnitaDoc) {
                getForm().getFiltriUnitaDocumentarieAvanzata().getFlag_dati_spec_presenti_ud()
                        .setChecked(false);
            }

            // Aggiorno l'interfaccia online
            updateInterfacciaOnLineDatiSpec(listaDatiSpecOnLine, false);
        } // end ELSE

        if (tipoUnitaDocPost.size() == 1) {
            // Recupero le versioni XSD associate al tipo unità documentaria selezionato
            DecodeMap mappaVersioniXsd = new DecodeMap();
            mappaVersioniXsd.populatedMap(
                    datiSpecEjb.getXsdDatiSpecTableBeanByTipoEntita(
                            tipoUnitaDocPost.get(0).longValue(), TipoEntitaSacer.UNI_DOC),
                    "cd_versione_xsd", "cd_versione_xsd");
            getForm().getFiltriUnitaDocumentarieAvanzata().getCd_versione_xsd_ud()
                    .setDecodeMap(mappaVersioniXsd);
        } else {
            getForm().getFiltriUnitaDocumentarieAvanzata().getCd_versione_xsd_ud()
                    .setDecodeMap(new DecodeMap());
        }

        return getForm().getFiltriUnitaDocumentarieAvanzata().asJSON();
    }

    /**
     * Trigger sul filtro "Tipo Documento" per l'aggiunta/rimozione dei dati specifici definiti dal
     * filtro stesso
     *
     * @return l'oggetto JSON dei filtri unitÃ documentaria ricerca avanzata
     *
     * @throws EMFError errore generico
     */
    @Override
    public JSONObject triggerFiltriUnitaDocumentarieAvanzataNm_tipo_docOnTrigger() throws EMFError {
        FiltriUnitaDocumentarieAvanzata udfa = getForm().getFiltriUnitaDocumentarieAvanzata();
        // Ricavo l'elenco dei tipi documento PRIMA di modificare (aggiunta/rimozione) il relativo
        // filtro
        List<BigDecimal> tipoDocPre = udfa.getNm_tipo_doc().parse();
        // Eseguo la post del filtri
        udfa.post(getRequest());
        // Ricavo l'elenco dei tipi documento DOPO aver modificato (aggiunta/rimozione) il relativo
        // filtro
        List<BigDecimal> tipoDocPost = udfa.getNm_tipo_doc().parse();
        // Confronto i due elenchi: se la lunghezza di tipoDocPre Ã¨ inferiore
        // a quella di tipoDocPost significa che ho fatto un'aggiunta
        boolean aggiunta = false;
        List<BigDecimal> elementoDiverso = (List<BigDecimal>) CollectionUtils
                .disjunction(tipoDocPre, tipoDocPost);
        if (tipoDocPre.size() < tipoDocPost.size()) {
            aggiunta = true;
        }

        // Ricavo la Lista Dati Specifici compilati a video
        List<DecCriterioDatiSpecBean> listaDatiSpecOnLine = (ArrayList) getSession()
                .getAttribute("listaDatiSpecOnLine") != null
                        ? (ArrayList) getSession().getAttribute("listaDatiSpecOnLine")
                        : new ArrayList();

        // HO FATTO UN'AGGIUNTA
        if (aggiunta) {
            DecAttribDatiSpecTableBean datiSpecTB = udHelper
                    .getDecAttribDatiSpecTableBean(elementoDiverso.get(0), TipoEntitaSacer.DOC);
            aggiungiDatiSpecPerTipoDoc(datiSpecTB, listaDatiSpecOnLine);
        } // HO FATTO UNA RIMOZIONE
        else {
            // Ricavo il nome del tipo documento
            String nmTipoDoc = udHelper.getDecTipoDocRowBean(elementoDiverso.get(0)).getNmTipoDoc();

            // Per ogni DATO SPECIFICO di questo TIPO DOCUMENTO
            // rimuovo il riferimento al tipo documento
            for (DecCriterioDatiSpecBean datoSpec : listaDatiSpecOnLine) {
                List<DecCriterioAttribBean> tabellaDefinitoDa = datoSpec.getDecCriterioAttribs();
                for (int i = 0; i < tabellaDefinitoDa.size(); i++) {
                    if (tabellaDefinitoDa.get(i).getNmTipoDoc() != null
                            && tabellaDefinitoDa.get(i).getNmTipoDoc().equals(nmTipoDoc)) {
                        tabellaDefinitoDa.remove(i);
                    }
                }
            }

            // Controllo se ho ancora dati specifici per tutti i tipi documento
            boolean hasDSsuTipiDoc = false;
            Iterator it = listaDatiSpecOnLine.iterator();
            while (it.hasNext()) {
                DecCriterioDatiSpecBean datoSpec = (DecCriterioDatiSpecBean) it.next();
                List<DecCriterioAttribBean> tabellaDefinitoDa = datoSpec.getDecCriterioAttribs();
                if (tabellaDefinitoDa.isEmpty()) {
                    it.remove();
                } else {
                    for (DecCriterioAttribBean rigaDefinitoDa : tabellaDefinitoDa) {
                        if (rigaDefinitoDa.getNmTipoDoc() != null) {
                            hasDSsuTipiDoc = true;
                        }
                    }
                }
            }

            if (!hasDSsuTipiDoc) {
                getForm().getFiltriUnitaDocumentarieAvanzata().getFlag_dati_spec_presenti_doc()
                        .setChecked(false);
            }

            // Aggiorno l'interfaccia online
            updateInterfacciaOnLineDatiSpec(listaDatiSpecOnLine, false);
        } // end ELSE

        if (tipoDocPost.size() == 1) {
            // Recupero le versioni XSD associate al tipo documento selezionato
            DecodeMap mappaVersioniXsd = new DecodeMap();
            mappaVersioniXsd
                    .populatedMap(
                            datiSpecEjb.getXsdDatiSpecTableBeanByTipoEntita(
                                    tipoDocPost.get(0).longValue(), TipoEntitaSacer.DOC),
                            "cd_versione_xsd", "cd_versione_xsd");
            getForm().getFiltriUnitaDocumentarieAvanzata().getCd_versione_xsd_doc()
                    .setDecodeMap(mappaVersioniXsd);

            // Gestione Elemento
            getForm().getFiltriUnitaDocumentarieAvanzata().getTi_doc().setDecodeMap(
                    ComboGetter.getMappaSortedGenericEnum("ti_doc", Constants.TiDoc.values()));
        } else {
            getForm().getFiltriUnitaDocumentarieAvanzata().getCd_versione_xsd_doc()
                    .setDecodeMap(new DecodeMap());
            getForm().getFiltriUnitaDocumentarieAvanzata().getTi_doc()
                    .setDecodeMap(new DecodeMap());
        }

        return getForm().getFiltriUnitaDocumentarieAvanzata().asJSON();
    }

    @Override
    public JSONObject triggerFiltriUnitaDocumentarieAvanzataNm_sistema_migrazOnTrigger()
            throws EMFError {
        FiltriUnitaDocumentarieAvanzata udfa = getForm().getFiltriUnitaDocumentarieAvanzata();
        // Ricavo l'elenco dei sitemi di migrazione PRIMA di modificare (aggiunta/rimozione) il
        // relativo filtro
        List<String> tipoSisMigrPre = udfa.getNm_sistema_migraz().parse();
        // Eseguo la post del filtri
        udfa.post(getRequest());
        // Ricavo l'elenco dei sistemi di migrazione DOPO aver modificato (aggiunta/rimozione) il
        // relativo filtro
        List<String> tipoSisMigrPost = udfa.getNm_sistema_migraz().parse();
        // Confronto i due elenchi: se la lunghezza di tipoSisMigrPre Ã¨ inferiore
        // a quella di tipoSisMigrPost significa che ho fatto un'aggiunta
        boolean aggiunta = false;
        List<String> elementoDiverso = (List<String>) CollectionUtils.disjunction(tipoSisMigrPre,
                tipoSisMigrPost);
        if (tipoSisMigrPre.size() < tipoSisMigrPost.size()) {
            aggiunta = true;
        }

        // Ricavo la Lista Dati Specifici compilati a video
        List<DecCriterioDatiSpecBean> listaDatiSpecOnLine = (ArrayList) getSession()
                .getAttribute("listaDatiSpecOnLine") != null
                        ? (ArrayList) getSession().getAttribute("listaDatiSpecOnLine")
                        : new ArrayList();

        // HO FATTO UN'AGGIUNTA
        if (aggiunta) {
            DecAttribDatiSpecTableBean datiSpecTB = udHelper.getDecAttribDatiSpecSisMigrTableBean(
                    elementoDiverso.get(0), getUser().getIdOrganizzazioneFoglia());
            aggiungiDatiSpecPerSisMigr(datiSpecTB, listaDatiSpecOnLine);
        } // HO FATTO UNA RIMOZIONE
        else {
            // Ricavo il nome del sistema di migrazione
            String nmSistemaMigraz = elementoDiverso.get(0);

            // Per ogni DATO SPECIFICO di questo SISTEMA MIGRAZIONE
            // rimuovo il riferimento al sistema migrazione
            for (DecCriterioDatiSpecBean datoSpec : listaDatiSpecOnLine) {
                List<DecCriterioAttribBean> tabellaDefinitoDa = datoSpec.getDecCriterioAttribs();
                Iterator<DecCriterioAttribBean> iterator = tabellaDefinitoDa.iterator();
                while (iterator.hasNext()) {
                    DecCriterioAttribBean attribBean = iterator.next();
                    if (attribBean.getNmSistemaMigraz() != null
                            && attribBean.getNmSistemaMigraz().equals(nmSistemaMigraz)) {
                        iterator.remove();
                    }
                }
            }

            // Controllo se ho ancora dati specifici per tutti i sistemi migrazione
            boolean hasDSsuSisMigr = false;
            Iterator it = listaDatiSpecOnLine.iterator();
            while (it.hasNext()) {
                DecCriterioDatiSpecBean datoSpec = (DecCriterioDatiSpecBean) it.next();
                List<DecCriterioAttribBean> tabellaDefinitoDa = datoSpec.getDecCriterioAttribs();
                if (tabellaDefinitoDa.isEmpty()) {
                    it.remove();
                } else {
                    for (DecCriterioAttribBean rigaDefinitoDa : tabellaDefinitoDa) {
                        if (rigaDefinitoDa.getNmSistemaMigraz() != null) {
                            hasDSsuSisMigr = true;
                        }
                    }
                }
            }

            if (!hasDSsuSisMigr) {
                getForm().getFiltriUnitaDocumentarieAvanzata().getFlag_dati_spec_presenti_sm()
                        .setChecked(false);
            }

            // Aggiorno l'interfaccia online
            updateInterfacciaOnLineDatiSpec(listaDatiSpecOnLine, false);
        } // end ELSE
        return getForm().getFiltriUnitaDocumentarieAvanzata().asJSON();
    }

    @Override
    public JSONObject triggerFiltriCollegamentiUnitaDocumentarieCon_collegamentoOnTrigger()
            throws EMFError {
        FiltriCollegamentiUnitaDocumentarie fcud = getForm()
                .getFiltriCollegamentiUnitaDocumentarie();
        fcud.post(getRequest());
        // Setto dei valori in altri filtri
        if (fcud.getCon_collegamento().getValue().equals("1")
                || fcud.getCon_collegamento().getValue().equals("0")) {
            if (fcud.getCon_collegamento().getValue().equals("1")) {
                fcud.getCollegamento_risolto().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
            } else {
                fcud.getCollegamento_risolto().setDecodeMap(new DecodeMap());
                fcud.getDs_link_unita_doc().setValue("");
                fcud.getCd_registro_key_unita_doc_link().setValue("");
                fcud.getAa_key_unita_doc_link().setValue("");
                fcud.getCd_key_unita_doc_link().setValue("");
            }
        } else {
            fcud.getCollegamento_risolto().setDecodeMap(new DecodeMap());
            fcud.getDs_link_unita_doc().setValue("");
            fcud.getCd_registro_key_unita_doc_link().setValue("");
            fcud.getAa_key_unita_doc_link().setValue("");
            fcud.getCd_key_unita_doc_link().setValue("");
        }
        return fcud.asJSON();
    }

    @Override
    public JSONObject triggerFiltriCollegamentiUnitaDocumentarieIs_oggetto_collegamentoOnTrigger()
            throws EMFError {
        FiltriCollegamentiUnitaDocumentarie fcud = getForm()
                .getFiltriCollegamentiUnitaDocumentarie();
        fcud.post(getRequest());
        // Setto dei valori in altri filtri
        if (fcud.getIs_oggetto_collegamento().getValue().equals("1")
                || fcud.getIs_oggetto_collegamento().getValue().equals("0")) {
            if (fcud.getIs_oggetto_collegamento().getValue().equals("0")) {
                fcud.getDs_link_unita_doc_oggetto().setValue("");
            }
        } else {
            fcud.getDs_link_unita_doc_oggetto().setValue("");
        }
        return fcud.asJSON();
    }

    public void aggiungiDatiSpecPerTipoUnitaDoc(DecAttribDatiSpecTableBean datiSpecTB,
            List<DecCriterioDatiSpecBean> listaDatiSpecOnLine) throws EMFError {
        // Per ogni DATO SPECIFICO di questo TIPO UNITA' DOCUMENTARIA AGGIUNTO
        for (DecAttribDatiSpecRowBean rigaDatoSpecifico : datiSpecTB) {
            // Se passo di qua, significa che ho dei dati specifici
            // per questo Tipo UnitÃ Documentaria e dunque spunto il flag a video
            getForm().getFiltriUnitaDocumentarieAvanzata().getFlag_dati_spec_presenti_ud()
                    .setChecked(true);
            getForm().getFiltriUnitaDocumentarieDatiSpec().getFlag_dati_spec_presenti_ud()
                    .setChecked(true);

            // Ricavo l'informazione "Definito da" per il TIPO UNITA' DOCUMENTARIA
            DecCriterioAttribBean criterioAttrib = new DecCriterioAttribBean();
            String nmTipoUnitaDoc = udHelper
                    .getDecTipoUnitaDocRowBean(rigaDatoSpecifico.getIdTipoUnitaDoc())
                    .getNmTipoUnitaDoc();
            String dsVersioni = udHelper.getVersioniXsd(rigaDatoSpecifico.getIdAttribDatiSpec(),
                    rigaDatoSpecifico.getIdTipoUnitaDoc(), TipoEntitaSacer.UNI_DOC);
            criterioAttrib.setTiEntitaSacer(TipoEntitaSacer.UNI_DOC.name());
            criterioAttrib.setNmTipoUnitaDoc(nmTipoUnitaDoc);
            criterioAttrib.setNmTipoDoc(null);
            criterioAttrib.setNmSistemaMigraz(null);
            criterioAttrib.setIdAttribDatiSpec(rigaDatoSpecifico.getIdAttribDatiSpec());
            criterioAttrib.setDsListaVersioniXsd(dsVersioni);
            criterioAttrib.setOrdine(BigDecimal.ZERO);

            // Inserisco le informazioni del dato specifico aggiunto
            insertFiltroDatoSpecifico(rigaDatoSpecifico, listaDatiSpecOnLine, criterioAttrib);
        } // end For di controllo di ogni dato specifico

        // Aggiorno l'interfaccia online
        updateInterfacciaOnLineDatiSpec(listaDatiSpecOnLine, true);
    }

    public void aggiungiDatiSpecPerTipoDoc(DecAttribDatiSpecTableBean datiSpecTB,
            List<DecCriterioDatiSpecBean> listaDatiSpecOnLine) throws EMFError {
        // Per ogni DATO SPECIFICO di questo TIPO UNITA' DOCUMENTARIA AGGIUNTO
        for (DecAttribDatiSpecRowBean rigaDatoSpecifico : datiSpecTB) {
            // Se passo di qua, significa che ho dei dati specifici
            // per questo Tipo Documento e dunque spunto il flag a video
            getForm().getFiltriUnitaDocumentarieAvanzata().getFlag_dati_spec_presenti_doc()
                    .setChecked(true);
            getForm().getFiltriUnitaDocumentarieDatiSpec().getFlag_dati_spec_presenti_doc()
                    .setChecked(true);

            // Ricavo l'informazione "Definito da" per il TIPO DOCUMENTO
            DecCriterioAttribBean criterioAttrib = new DecCriterioAttribBean();
            String nmTipoDoc = udHelper.getDecTipoDocRowBean(rigaDatoSpecifico.getIdTipoDoc())
                    .getNmTipoDoc();
            String dsVersioni = udHelper.getVersioniXsd(rigaDatoSpecifico.getIdAttribDatiSpec(),
                    rigaDatoSpecifico.getIdTipoDoc(), TipoEntitaSacer.DOC);
            criterioAttrib.setTiEntitaSacer(TipoEntitaSacer.DOC.name());
            criterioAttrib.setNmTipoUnitaDoc(null);
            criterioAttrib.setNmTipoDoc(nmTipoDoc);
            criterioAttrib.setNmSistemaMigraz(null);
            criterioAttrib.setIdAttribDatiSpec(rigaDatoSpecifico.getIdAttribDatiSpec());
            criterioAttrib.setDsListaVersioniXsd(dsVersioni);
            criterioAttrib.setOrdine(BigDecimal.ONE);

            // Inserisco le informazioni del dato specifico aggiunto
            insertFiltroDatoSpecifico(rigaDatoSpecifico, listaDatiSpecOnLine, criterioAttrib);
        } // end For di controllo di ogni dato specifico

        // Aggiorno l'interfaccia online
        updateInterfacciaOnLineDatiSpec(listaDatiSpecOnLine, true);
    }

    public void aggiungiDatiSpecPerSisMigr(DecAttribDatiSpecTableBean datiSpecTB,
            List<DecCriterioDatiSpecBean> listaDatiSpecOnLine) throws EMFError {
        // Per ogni DATO SPECIFICO di questo TIPO UNITA' DOCUMENTARIA AGGIUNTO
        for (DecAttribDatiSpecRowBean rigaDatoSpecifico : datiSpecTB) {
            // Se passo di qua, significa che ho dei dati specifici
            // per questo Tipo Sistema Migrazione e dunque spunto il flag a video
            getForm().getFiltriUnitaDocumentarieAvanzata().getFlag_dati_spec_presenti_sm()
                    .setChecked(true);
            // getForm().getFiltriUnitaDocumentarieDatiSpec().getFlag_dati_spec_presenti_sm().setChecked(true);

            // Ricavo l'informazione "Definito da" per il TIPO SISTEMA MIGRAZIONE
            DecCriterioAttribBean criterioAttrib = new DecCriterioAttribBean();
            String dsVersioni = udHelper.getVersioniXsdSisMigr(
                    rigaDatoSpecifico.getIdAttribDatiSpec(), rigaDatoSpecifico.getTiEntitaSacer(),
                    rigaDatoSpecifico.getNmSistemaMigraz());
            criterioAttrib.setTiEntitaSacer(rigaDatoSpecifico.getTiEntitaSacer());
            criterioAttrib.setNmTipoUnitaDoc(null);
            criterioAttrib.setNmTipoDoc(null);
            criterioAttrib.setNmSistemaMigraz(rigaDatoSpecifico.getNmSistemaMigraz());
            criterioAttrib.setIdAttribDatiSpec(rigaDatoSpecifico.getIdAttribDatiSpec());
            criterioAttrib.setDsListaVersioniXsd(dsVersioni);
            criterioAttrib.setOrdine(BigDecimal.TEN);

            // Inserisco le informazioni del dato specifico aggiunto
            insertFiltroDatoSpecifico(rigaDatoSpecifico, listaDatiSpecOnLine, criterioAttrib);
        } // end For di controllo di ogni dato specifico

        // Aggiorno l'interfaccia online
        updateInterfacciaOnLineDatiSpec(listaDatiSpecOnLine, true);
    }

    public void checkFiltriSettatiSuDatiSpecifici() {
        // Ricavo la Lista Dati Specifici compilati a video
        List<DecCriterioDatiSpecBean> listaDatiSpecOnLine = (ArrayList) getSession()
                .getAttribute("listaDatiSpecOnLine") != null
                        ? (ArrayList) getSession().getAttribute("listaDatiSpecOnLine")
                        : new ArrayList();

        List<DefinitoDaBean> listaDefinitoDa = new ArrayList();
        Set<String> insiemeTipiUnitaDoc = new HashSet();
        Set<String> insiemeTipiDoc = new HashSet();
        Set<String> insiemeSistemiMigrazUniDoc = new HashSet();
        Set<String> insiemeSistemiMigrazDoc = new HashSet();
        StringBuilder filtriDatiSpec = new StringBuilder();

        // Per ogni dato specifico
        for (DecCriterioDatiSpecBean datiSpec : listaDatiSpecOnLine) {
            /*
             * Se il filtro Ã¨ compilato, ricavo le informazioni che mi servono: aggiungo un
             * elemento in ListaDefinitoDa e nel relativo insieme
             */
            if (datiSpec.getTiOper() != null && datiSpec.getDlValore() != null) {
                if (!datiSpec.getTiOper().equals("") || !datiSpec.getDlValore().equals("")) {

                    // Ricavo la listaDefinitoDa di quel preciso dato specifico
                    List<DecCriterioAttribBean> decCriterioAttribList = datiSpec
                            .getDecCriterioAttribs();

                    /*
                     * Scorro questa lista per andare ad inserire l'elemento nella lista principale,
                     * ovvero ListaDefinitoDa
                     */
                    for (DecCriterioAttribBean decCriterioAttrib : decCriterioAttribList) {
                        DefinitoDaBean definitoDa = new DefinitoDaBean();
                        definitoDa.setIdAttribDatiSpec(decCriterioAttrib.getIdAttribDatiSpec());
                        definitoDa.setTiEntitaSacer(decCriterioAttrib.getTiEntitaSacer());
                        definitoDa.setNmTipoDoc(decCriterioAttrib.getNmTipoDoc());
                        definitoDa.setNmTipoUnitaDoc(decCriterioAttrib.getNmTipoUnitaDoc());
                        definitoDa.setNmSistemaMigraz(decCriterioAttrib.getNmSistemaMigraz());
                        definitoDa.setNmAttribDatiSpec(datiSpec.getNmAttribDatiSpec());
                        definitoDa.setTiOper(datiSpec.getTiOper());
                        definitoDa.setDlValore(datiSpec.getDlValore());
                        listaDefinitoDa.add(definitoDa);
                        // Annoto quale elemento sto trattando inserendolo nel relativo insieme
                        // Caso UNI_DOC
                        if (definitoDa.getNmTipoUnitaDoc() != null) {
                            insiemeTipiUnitaDoc.add(definitoDa.getNmTipoUnitaDoc());
                        } // Caso DOC
                        else if (definitoDa.getNmTipoDoc() != null) {
                            insiemeTipiDoc.add(definitoDa.getNmTipoDoc());
                        } // Caso Sistema Migrazione con entitÃ Sacer UNI_DOC
                        else if (definitoDa.getTiEntitaSacer()
                                .equals(TipoEntitaSacer.UNI_DOC.name())) {
                            insiemeSistemiMigrazUniDoc.add(definitoDa.getNmSistemaMigraz());
                        } // Caso Sistema Migrazione con entitÃ Sacer DOC
                        else if (definitoDa.getTiEntitaSacer().equals(TipoEntitaSacer.DOC.name())) {
                            insiemeSistemiMigrazDoc.add(definitoDa.getNmSistemaMigraz());
                        }
                    }
                }
            }
        }

        // Valido i filtri compilati
        UnitaDocumentarieValidator validator = new UnitaDocumentarieValidator(getMessageBox());
        validator.validaDatiSpec(listaDefinitoDa);

        // Se la validazione non ha portato errori
        if (!getMessageBox().hasError()) {
            // Comincio a costruire la label dei Filtri Dati Specifici
            if (!insiemeTipiUnitaDoc.isEmpty()) {
                boolean firstTimeDefinitoDa = true;
                Iterator<String> it = insiemeTipiUnitaDoc.iterator();

                // Per ogni nm_tipo_unita_doc presente in insiemeTipiUnitaDoc
                while (it.hasNext()) {
                    if (firstTimeDefinitoDa) {
                        filtriDatiSpec.append(" e ((");
                        firstTimeDefinitoDa = false;
                    } else {
                        filtriDatiSpec.append("\n  o (");
                    }
                    boolean firstTimeTipoUD = true;
                    String nmTipoUnitaDoc = it.next();
                    for (DefinitoDaBean definitoDa : listaDefinitoDa) {
                        if (definitoDa.getNmTipoUnitaDoc() != null
                                && definitoDa.getNmTipoUnitaDoc().equals(nmTipoUnitaDoc)) {
                            if (firstTimeTipoUD) {
                                firstTimeTipoUD = false;
                            } else {
                                filtriDatiSpec.append("\n  e ");
                            }
                            filtriDatiSpec.append("tipo unit\u00e0 documentaria = ")
                                    .append(nmTipoUnitaDoc);
                            filtriDatiSpec.append(" e ").append(definitoDa.getNmAttribDatiSpec());
                            filtriDatiSpec.append(" ").append(definitoDa.getTiOper());
                            filtriDatiSpec.append(" ").append(definitoDa.getDlValore());
                        } // END IF
                    } // END FOR di ListaDefinitoDa
                    filtriDatiSpec.append(")");
                } // END WHILE sull'insieme dei TipiUnitÃ Doc
                filtriDatiSpec.append(")");
            }

            if (!insiemeTipiDoc.isEmpty()) {
                boolean firstTimeDefinitoDa = true;
                Iterator<String> it = insiemeTipiDoc.iterator();

                // Per ogni nm_tipo_doc presente in insiemeTipiDoc
                while (it.hasNext()) {
                    if (firstTimeDefinitoDa) {
                        /*
                         * Controllo filtriDatiSpec Ã¨ != da stringa vuota, significa che in
                         * precedenza ho giÃ scritto qualcosa e dunque vado a capo
                         */
                        if (filtriDatiSpec.length() > 0) {
                            filtriDatiSpec.append("\n");
                        }
                        filtriDatiSpec.append(" e ((");
                        firstTimeDefinitoDa = false;
                    } else {
                        filtriDatiSpec.append("\n  o (");
                    }
                    boolean firstTimeTipoDoc = true;
                    String nmTipoDoc = it.next();
                    for (DefinitoDaBean definitoDa : listaDefinitoDa) {
                        if (definitoDa.getNmTipoDoc() != null
                                && definitoDa.getNmTipoDoc().equals(nmTipoDoc)) {
                            if (firstTimeTipoDoc) {
                                firstTimeTipoDoc = false;
                            } else {
                                filtriDatiSpec.append("\n  e ");
                            }
                            filtriDatiSpec.append("tipo documento = ").append(nmTipoDoc);
                            filtriDatiSpec.append(" e ").append(definitoDa.getNmAttribDatiSpec());
                            filtriDatiSpec.append(" ").append(definitoDa.getTiOper());
                            filtriDatiSpec.append(" ").append(definitoDa.getDlValore());
                        } // END IF
                    } // END FOR di ListaDefinitoDa
                    filtriDatiSpec.append(")");
                } // END WHILE sull'insieme dei TipiDoc
                filtriDatiSpec.append(")");
            }

            if (!insiemeSistemiMigrazUniDoc.isEmpty()) {
                boolean firstTimeDefinitoDa = true;
                Iterator<String> it = insiemeSistemiMigrazUniDoc.iterator();

                // Per ogni nm_sistema_migr presente in insiemeSistemiMigrazUniDoc
                while (it.hasNext()) {
                    if (firstTimeDefinitoDa) {
                        /*
                         * Controllo filtriDatiSpec Ã¨ != da stringa vuota, significa che in
                         * precedenza ho giÃ scritto qualcosa e dunque vado a capo
                         */
                        if (filtriDatiSpec.length() > 0) {
                            filtriDatiSpec.append("\n");
                        }

                        filtriDatiSpec.append(" e ((");
                        firstTimeDefinitoDa = false;
                    } else {
                        filtriDatiSpec.append("\n  o (");
                    }
                    boolean firstTimeSisMigrUniDoc = true;
                    String nmSisMigr = it.next();
                    for (DefinitoDaBean definitoDa : listaDefinitoDa) {
                        if (definitoDa.getNmSistemaMigraz() != null
                                && definitoDa.getNmSistemaMigraz().equals(nmSisMigr)
                                && definitoDa.getTiEntitaSacer().equals("UNI_DOC")) {
                            if (firstTimeSisMigrUniDoc) {
                                firstTimeSisMigrUniDoc = false;
                            } else {
                                filtriDatiSpec.append("\n  e ");
                            }
                            filtriDatiSpec.append("sistema migrazione = ").append(nmSisMigr)
                                    .append(" per unit\u00e0 documentaria");
                            filtriDatiSpec.append(" e ").append(definitoDa.getNmAttribDatiSpec());
                            filtriDatiSpec.append(" ").append(definitoDa.getTiOper());
                            filtriDatiSpec.append(" ").append(definitoDa.getDlValore())
                                    .append("\n");
                        } // END IF
                    } // END FOR di ListaDefinitoDa
                    filtriDatiSpec.append(")");
                } // END WHILE sull'insieme dei Sistemi Migrazione UNI_DOC
                filtriDatiSpec.append(")");
            }

            if (!insiemeSistemiMigrazDoc.isEmpty()) {
                boolean firstTimeDefinitoDa = true;
                Iterator<String> it = insiemeSistemiMigrazDoc.iterator();

                // Per ogni nm_sistema_migr presente in insiemeSistemiMigrazDoc
                while (it.hasNext()) {
                    if (firstTimeDefinitoDa) {
                        /*
                         * Controllo filtriDatiSpec Ã¨ != da stringa vuota, significa che in
                         * precedenza ho giÃ scritto qualcosa e dunque vado a capo
                         */
                        if (filtriDatiSpec.length() > 0) {
                            filtriDatiSpec.append("\n");
                        }
                        filtriDatiSpec.append(" e ((");
                        firstTimeDefinitoDa = false;
                    } else {
                        filtriDatiSpec.append("\n  o (");
                    }
                    boolean firstTimeSisMigrDoc = true;
                    String nmSisMigr = it.next();
                    for (DefinitoDaBean definitoDa : listaDefinitoDa) {
                        if (definitoDa.getNmSistemaMigraz() != null
                                && definitoDa.getNmSistemaMigraz().equals(nmSisMigr)
                                && definitoDa.getTiEntitaSacer().equals("DOC")) {
                            if (firstTimeSisMigrDoc) {
                                firstTimeSisMigrDoc = false;
                            } else {
                                filtriDatiSpec.append("\n  e ");
                            }
                            filtriDatiSpec.append("sistema migrazione = ").append(nmSisMigr)
                                    .append(" per documento");
                            filtriDatiSpec.append(" e ").append(definitoDa.getNmAttribDatiSpec());
                            filtriDatiSpec.append(" ").append(definitoDa.getTiOper());
                            filtriDatiSpec.append(" ").append(definitoDa.getDlValore())
                                    .append("\n");
                        } // END IF
                    } // END FOR di ListaDefinitoDa
                    filtriDatiSpec.append(")");
                } // END WHILE sull'insieme dei Sistemi Migrazione DOC
                filtriDatiSpec.append(")");
            }

            getForm().getFiltriUnitaDocumentarieAvanzata().getFiltri_dati_spec()
                    .setValue(filtriDatiSpec.toString());
            getForm().getUnitaDocumentarieTabs()
                    .setCurrentTab(getForm().getUnitaDocumentarieTabs().getFiltriRicercaAvanzata());

            getForm().getFiltriUnitaDocumentarieDatiSpec().getFiltri_dati_spec()
                    .setValue(filtriDatiSpec.toString());

        } else {
            getForm().getUnitaDocumentarieTabs()
                    .setCurrentTab(getForm().getUnitaDocumentarieTabs().getFiltriDatiSpec());
        }

    }

    @Override
    public void scarica_xml_ud() throws EMFError {
        BigDecimal idud = (BigDecimal) getSession().getAttribute("idud");
        try {
            // EVO#16486
            this.verificaUrnUd(idud.longValue());
            /*
             * Ricavo l'unità documentaria prendendo i dati da DB anche se li ho già nella maschera
             * di dettaglio, onde evitare che un domani, eliminando quei campi dall'online, vada
             * tutto in vacca
             */
            AroVVisUnitaDocIamRowBean udRB = udHelper.getAroVVisUnitaDocIamRowBean(idud);
            String filename = "DocumentiDiConservazione_" + udRB.getCdRegistroKeyUnitaDoc() + "-"
                    + udRB.getAaKeyUnitaDoc().toString() + "-" + udRB.getCdKeyUnitaDoc();
            Date dataRegUd = udRB.getDtRegUnitaDoc();

            /*
             * Definiamo l'output previsto che sarÃ un file in formato zip di cui si occuperÃ la
             * servlet per fare il download
             */
            ZipOutputStream outUD = new ZipOutputStream(getServletOutputStream());
            getResponse().setContentType("application/zip");
            getResponse().setHeader("Content-Disposition",
                    "attachment; filename=\"" + filename + ".zip");

            // Ricavo la lista dei documenti AGGIUNTI per quella determinata UD
            AroVVisDocIamTableBean docTB = udHelper.getAroVVisDocAggIamTableBean(idud, dataRegUd);

            try {
                zippaXML(outUD, udRB, docTB);
                outUD.flush();
                outUD.close();
            } catch (Exception e) {
                log.error("Eccezione", e);
            } finally {
                freeze();
            }
        } catch (ParerInternalError e) {
            log.error("Eccezione nel recupero documenti di conservazione ", e);
            getMessageBox().addError(
                    "Eccezione nel recupero documenti di conservazione: " + e.getDescription());
        } catch (Exception e) {
            String message = "Eccezione nel recupero documenti di conservazione "
                    + ExceptionUtils.getRootCauseMessage(e);
            getMessageBox()
                    .addError("Eccezione nel recupero documenti di conservazione: " + message);
            log.error("Eccezione nel recupero documenti di conservazione ", e);
        }
    }

    /**
     * Metodo che genera il file zip dato l'outputStream e i dati da inserire
     *
     * @param out   l'outputStream da utilizzare
     * @param udRB  il tableBean contenente i dati AroVVisUnitaDocIamRowBean
     * @param docTB il tableBean contenente i dati AroVVisDocIamTableBean
     *
     * @throws IOException eccezione in fase di scrittura del file
     */
    private void zippaXML(ZipOutputStream out, AroVVisUnitaDocIamRowBean udRB,
            AroVVisDocIamTableBean docTB) throws IOException {
        // Definiamo il buffer per lo stream di bytes
        byte[] data = new byte[1000];
        InputStream is = null;
        /*
         * 2015-12-16 modificata da Fioravanti_F per produrre nomi di file uguali a quelli resi dal
         * WS e per includere, se presente, anche il rapporto di versamento
         */
        // Comincio a piazzare i CLOB dei documenti
        if (docTB != null) {
            for (AroVVisDocIamRowBean docRB : docTB) {
                // Ricavo i nomi dei file xml di richiesta e risposta del documento
                String prefisso = docRB.getTiDoc() + "-" + docRB.getPgDoc();
                String xmlRichName;
                if (docRB.getDsUrnXmlRichDoc() != null && !docRB.getDsUrnXmlRichDoc().isEmpty()) {
                    xmlRichName = ComponenteRec.estraiNomeFileCompleto(docRB.getDsUrnXmlRichDoc())
                            + ".xml";
                } else {
                    xmlRichName = "IndiceSip_" + prefisso + ".xml";
                }
                String xmlRispName;
                if (docRB.getDsUrnXmlRispDoc() != null && !docRB.getDsUrnXmlRispDoc().isEmpty()) {
                    xmlRispName = ComponenteRec.estraiNomeFileCompleto(docRB.getDsUrnXmlRispDoc())
                            + ".xml";
                } else {
                    xmlRispName = "EsitoVersamento_" + prefisso + ".xml";
                }
                String xmlRappVersName;
                if (docRB.getDsUrnXmlRappDoc() != null && !docRB.getDsUrnXmlRappDoc().isEmpty()) {
                    xmlRappVersName = ComponenteRec
                            .estraiNomeFileCompleto(docRB.getDsUrnXmlRappDoc()) + ".xml";
                } else {
                    xmlRappVersName = "RapportoVersamento_" + prefisso + ".xml";
                }

                // Ricavo i tre CLOB, che sono delle stringone
                String xmlRichClob = docRB.getBlXmlRichDoc();
                String xmlRispClob = docRB.getBlXmlRispDoc();
                String xmlRappClob = docRB.getBlXmlRappDoc();

                // Inserisco nello zippone il CLOB dell'xml di richiesta del documento
                if (xmlRichClob != null && !xmlRichClob.isEmpty()) {
                    is = new ByteArrayInputStream(xmlRichClob.getBytes(StandardCharsets.UTF_8));
                    int count;
                    out.putNextEntry(new ZipEntry(xmlRichName));
                    while ((count = is.read(data, 0, 1000)) != -1) {
                        out.write(data, 0, count);
                    }
                    out.closeEntry();
                }

                // Inserisco nello zippone il CLOB dell'xml di risposta del documento
                if (xmlRispClob != null && !xmlRispClob.isEmpty()) {
                    is = new ByteArrayInputStream(xmlRispClob.getBytes(StandardCharsets.UTF_8));
                    int count;
                    out.putNextEntry(new ZipEntry(xmlRispName));
                    while ((count = is.read(data, 0, 1000)) != -1) {
                        out.write(data, 0, count);
                    }
                    out.closeEntry();
                }

                // Inserisco nello zippone il CLOB dell'xml di rapporto di versamento del documento
                if (xmlRappClob != null && !xmlRappClob.isEmpty()) {
                    is = new ByteArrayInputStream(xmlRappClob.getBytes(StandardCharsets.UTF_8));
                    int count;
                    out.putNextEntry(new ZipEntry(xmlRappVersName));
                    while ((count = is.read(data, 0, 1000)) != -1) {
                        out.write(data, 0, count);
                    }
                    out.closeEntry();
                }
            }
        }

        // Se ho anche l'unitÃ documentaria, piazzo nello zippone pure i suoi CLOB
        if (udRB != null) {
            // Ricavo i nomi dei file xml di richiesta e risposta dell'unitÃ documentaria
            String prefisso = udRB.getCdRegistroKeyUnitaDoc() + "-" + udRB.getAaKeyUnitaDoc() + "-"
                    + udRB.getCdKeyUnitaDoc();

            String xmlRichName;
            if (udRB.getDsUrnXmlRichUd() != null && !udRB.getDsUrnXmlRichUd().isEmpty()) {
                xmlRichName = ComponenteRec.estraiNomeFileCompleto(udRB.getDsUrnXmlRichUd())
                        + ".xml";
            } else {
                xmlRichName = "IndiceSip_" + prefisso + ".xml";
            }

            String xmlIndexName;
            if (udRB.getDsUrnXmlIndexUd() != null && !udRB.getDsUrnXmlIndexUd().isEmpty()) {
                xmlIndexName = ComponenteRec.estraiNomeFileCompleto(udRB.getDsUrnXmlIndexUd())
                        + ".xml";
            } else {
                xmlIndexName = "IndicePI_SIP_" + prefisso + ".xml";
            }

            String xmlRispName;
            if (udRB.getDsUrnXmlRispUd() != null && !udRB.getDsUrnXmlRispUd().isEmpty()) {
                xmlRispName = ComponenteRec.estraiNomeFileCompleto(udRB.getDsUrnXmlRispUd())
                        + ".xml";
            } else {
                xmlRispName = "EsitoVersamento_" + prefisso + ".xml";
            }

            String xmlRappVersName;
            if (udRB.getDsUrnXmlRappUd() != null && !udRB.getDsUrnXmlRappUd().isEmpty()) {
                xmlRappVersName = ComponenteRec.estraiNomeFileCompleto(udRB.getDsUrnXmlRappUd())
                        + ".xml";
            } else {
                xmlRappVersName = "RapportoVersamento_" + prefisso + ".xml";
            }

            // Ricavo i quattro CLOB, che sono delle stringone
            String xmlRichClob = udRB.getBlXmlRichUd();
            String xmlIndexClob = udRB.getBlXmlIndexUd();
            String xmlRispClob = udRB.getBlXmlRispUd();
            String xmlRappClob = udRB.getBlXmlRappUd();

            // Inserisco nello zippone il CLOB dell'xml di richiesta dell'UD
            if (xmlRichClob != null && !xmlRichClob.isEmpty()) {
                is = new ByteArrayInputStream(xmlRichClob.getBytes(StandardCharsets.UTF_8));
                int count;
                out.putNextEntry(new ZipEntry(xmlRichName));
                while ((count = is.read(data, 0, 1000)) != -1) {
                    out.write(data, 0, count);
                }
                out.closeEntry();
            }

            // Inserisco nello zippone il CLOB dell'xml di indice dell'UD
            if (xmlIndexClob != null && !xmlIndexClob.isEmpty()) {
                is = new ByteArrayInputStream(xmlIndexClob.getBytes(StandardCharsets.UTF_8));
                int count;
                out.putNextEntry(new ZipEntry(xmlIndexName));
                while ((count = is.read(data, 0, 1000)) != -1) {
                    out.write(data, 0, count);
                }
                out.closeEntry();
            }

            // Inserisco nello zippone il CLOB dell'xml di risposta dell'UD
            if (xmlRispClob != null && !xmlRispClob.isEmpty()) {
                is = new ByteArrayInputStream(xmlRispClob.getBytes(StandardCharsets.UTF_8));
                int count;
                out.putNextEntry(new ZipEntry(xmlRispName));
                while ((count = is.read(data, 0, 1000)) != -1) {
                    out.write(data, 0, count);
                }
                out.closeEntry();
            }

            // Inserisco nello zippone il CLOB dell'xml di rapporto di versamento dell'UD
            if (xmlRappClob != null && !xmlRappClob.isEmpty()) {
                is = new ByteArrayInputStream(xmlRappClob.getBytes(StandardCharsets.UTF_8));
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

    /**
     * Metodo che genera il file zip dato l'outputStream e i dati da inserire
     *
     * @param out   l'outputStream da utilizzare
     * @param udRB  il tableBean AroVVisUnitaDocIamRowBean
     *
     * @param docTB i tableBean AroVVisDocIamTableBean
     *
     * @throws IOException errore in scrittura del file
     */
    private void zippaRapportoVersamento(ZipOutputStream out, AroVVisUnitaDocIamRowBean udRB,
            AroVVisDocIamTableBean docTB) throws IOException {
        // Definiamo il buffer per lo stream di bytes
        byte[] data = new byte[1000];
        InputStream is = null;

        // Comincio a piazzare i CLOBBI dei rapporti di versamento dei documenti
        if (docTB != null) {
            for (AroVVisDocIamRowBean docRB : docTB) {
                // Ricavo i nomi dei file xml di richiesta e risposta del documento
                String prefisso = docRB.getTiDoc() + "-" + docRB.getPgDoc();

                String xmlRappVersName;
                if (docRB.getDsUrnXmlRappDoc() != null && !docRB.getDsUrnXmlRappDoc().isEmpty()) {
                    xmlRappVersName = ComponenteRec
                            .estraiNomeFileCompleto(docRB.getDsUrnXmlRappDoc()) + ".xml";
                } else {
                    xmlRappVersName = "RapportoVersamento_" + prefisso + ".xml";
                }

                String xmlRappClob = docRB.getBlXmlRappDoc();

                // Inserisco nello zippone il CLOB dell'xml di rapporto di versamento del documento
                if (xmlRappClob != null && !xmlRappClob.isEmpty()) {
                    is = new ByteArrayInputStream(xmlRappClob.getBytes(StandardCharsets.UTF_8));
                    int count;
                    out.putNextEntry(new ZipEntry(xmlRappVersName));
                    while ((count = is.read(data, 0, 1000)) != -1) {
                        out.write(data, 0, count);
                    }
                    out.closeEntry();
                }
            }
        }

        // Se ho anche l'unità documentaria, piazzo nello zippone pure i suoi CLOBBI del rapporto di
        // versamento
        if (udRB != null) {
            // Ricavo i nomi dei file xml di richiesta e risposta dell'unitÃ documentaria
            String prefisso = udRB.getCdRegistroKeyUnitaDoc() + "-" + udRB.getAaKeyUnitaDoc() + "-"
                    + udRB.getCdKeyUnitaDoc();

            String xmlRappVersName;
            if (udRB.getDsUrnXmlRappUd() != null && !udRB.getDsUrnXmlRappUd().isEmpty()) {
                xmlRappVersName = ComponenteRec.estraiNomeFileCompleto(udRB.getDsUrnXmlRappUd())
                        + ".xml";
            } else {
                xmlRappVersName = "RapportoVersamento_" + prefisso + ".xml";
            }

            // Ricavo il CLOBBO, che è uno stringone
            String xmlRappClob = udRB.getBlXmlRappUd();

            // Inserisco nello zippone il CLOB dell'xml di rapporto di versamento dell'UD
            if (xmlRappClob != null && !xmlRappClob.isEmpty()) {
                is = new ByteArrayInputStream(xmlRappClob.getBytes(StandardCharsets.UTF_8));
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

    @Override
    public void scarica_xml_doc() throws EMFError {
        BigDecimal idud = (BigDecimal) getSession().getAttribute("idud");
        BigDecimal iddoc = (BigDecimal) getSession().getAttribute("iddoc");
        /*
         * Ricavo l'unitÃ documentaria ed il documento prendendo i dati da DB anche se li ho giÃ
         * nella maschera di dettaglio, onde evitare che un domani, eliminando quei campi
         * dall'online, vada tutto in vacca
         */
        AroVVisDocIamTableBean docTB = udHelper.getAroVVisDocIamTableBean(iddoc);
        AroVVisUnitaDocIamRowBean udRB = udHelper.getAroVVisUnitaDocIamRowBean(idud);
        String filename = "DocumentiDiConservazione_" + udRB.getCdRegistroKeyUnitaDoc() + "-"
                + udRB.getAaKeyUnitaDoc().toString() + "-" + udRB.getCdKeyUnitaDoc() + "-"
                + docTB.getRow(0).getTiDoc() + "-" + docTB.getRow(0).getPgDoc();

        /*
         * Definiamo l'output previsto che sarÃ un file in formato zip di cui si occuperÃ la servlet
         * per fare il download
         */
        ZipOutputStream outUD = new ZipOutputStream(getServletOutputStream());
        getResponse().setContentType("application/zip");
        getResponse().setHeader("Content-Disposition",
                "attachment; filename=\"" + filename + ".zip");

        try {
            zippaXML(outUD, udRB, docTB);
            outUD.flush();
            outUD.close();
        } catch (Exception e) {
            log.error("Eccezione", e);
        } finally {
            freeze();
        }
    }

    @Override
    public void scarica_xml_upd() throws EMFError {
        BigDecimal idud = (BigDecimal) getSession().getAttribute("idud");
        BigDecimal idupd = (BigDecimal) getSession().getAttribute("idupdunitadoc");
        /*
         * Ricavo l'unità documentaria e l'aggiornamento prendendo i dati da DB anche se li ho già
         * nella maschera di dettaglio, onde evitare che un domani, eliminando quei campi
         * dall'online, vada tutto in vacca
         */
        AroVVisUpdUnitaDocTableBean updTB = udHelper.getAroVVisUpdUnitaDocTableBean(idupd);
        AroVVisUnitaDocIamRowBean udRB = udHelper.getAroVVisUnitaDocIamRowBean(idud);
        String filename = "SIP_AGGIORNAMENTO_" + udRB.getCdRegistroKeyUnitaDoc().replace(" ", "_")
                + "-" + udRB.getAaKeyUnitaDoc().toString() + "-"
                + udRB.getCdKeyUnitaDoc().replace(" ", "_") + "-"
                + updTB.getRow(0).getPgUpdUnitaDoc();

        /*
         * Definiamo l'output previsto che sarà un file in formato zip di cui si occuperà la servlet
         * per fare il download
         */
        ZipOutputStream outUD = new ZipOutputStream(getServletOutputStream());
        getResponse().setContentType("application/zip");
        getResponse().setHeader("Content-Disposition",
                "attachment; filename=\"" + filename + ".zip");

        try {
            generaZipSipUpd(outUD, updTB);
            outUD.flush();
            outUD.close();
        } catch (Exception e) {
            log.error("Eccezione", e);
        } finally {
            freeze();
        }
    }

    /**
     * Metodo che genera il file zip dato l'outputStream e i dati da inserire
     *
     * @param out   l'outputStream da utilizzare
     * @param updTB il tableBean contenente i dati
     *
     * @throws EMFError    errore generico
     * @throws IOException
     */
    private void generaZipSipUpd(ZipOutputStream out, AroVVisUpdUnitaDocTableBean updTB)
            throws EMFError, IOException, UnsupportedEncodingException {
        // Definiamo il buffer per lo stream di bytes
        byte[] data = new byte[1000];
        InputStream is = null;

        String dirname = "SIP_AGGIORNAMENTO_" + updTB.getRow(0).getNmAmbiente().replaceAll(" ", "_")
                + "_" + updTB.getRow(0).getNmEnte().replaceAll(" ", "_") + "_"
                + updTB.getRow(0).getNmStrut().replaceAll(" ", "_") + "-"
                + updTB.getRow(0).getCdRegistroKeyUnitaDoc().replaceAll(" ", "_") + "-"
                + updTB.getRow(0).getAaKeyUnitaDoc().toString() + "-"
                + updTB.getRow(0).getCdKeyUnitaDoc().replaceAll(" ", "_") + "-"
                + updTB.getRow(0).getPgUpdUnitaDoc();

        for (AroVVisUpdUnitaDocRowBean updRB : updTB) {
            // Ricavo i nomi dei file xml di richiesta e risposta dell'aggiornamento
            String xmlRichName = "IndiceSip.xml";
            String xmlRispName = "RdV.xml";

            // Ricavo i tre CLOB, che sono delle stringone
            String xmlRichClob = updRB.getBlXmlRich();
            String xmlRispClob = updRB.getBlXmlRisp();

            // MEV#29089
            boolean xmlVuoti = xmlRichClob == null && xmlRispClob == null;
            // verify if xml on O.S.
            if (xmlVuoti) {
                // recupero oggetti da O.S. (se presenti)
                Map<String, String> xmlFiles = objectStorageService
                        .getObjectXmlVersAggMd(updRB.getIdUpdUnitaDoc().longValue());

                if (!xmlFiles.isEmpty()) {
                    xmlRichClob = xmlFiles.get(CostantiDB.TipiXmlDati.RICHIESTA);
                    xmlRispClob = xmlFiles.get(CostantiDB.TipiXmlDati.RISPOSTA);
                }
            }
            // end MEV#29089

            // Inserisco nello zippone il CLOB dell'xml di richiesta dell'aggiornamento
            if (xmlRichClob != null && !xmlRichClob.isEmpty()) {
                is = new ByteArrayInputStream(xmlRichClob.getBytes(StandardCharsets.UTF_8));
                int count;
                out.putNextEntry(new ZipEntry(dirname + "/" + xmlRichName));
                while ((count = is.read(data, 0, 1000)) != -1) {
                    out.write(data, 0, count);
                }
                out.closeEntry();
            }

            // Inserisco nello zippone il CLOB dell'xml di risposta dell'aggiornamento
            if (xmlRispClob != null && !xmlRispClob.isEmpty()) {
                is = new ByteArrayInputStream(xmlRispClob.getBytes(StandardCharsets.UTF_8));
                int count;
                out.putNextEntry(new ZipEntry(dirname + "/" + xmlRispName));
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

    private void updateInterfacciaOnLineDatiSpec(List<DecCriterioDatiSpecBean> listaDatiSpecOnLine,
            boolean isAggiunta) {
        // Costruisco l'interfaccia on-line sulla base
        // delle due strutture in memoria
        BaseTableInterface tabellaDatiSpec = listBean2TableBean(listaDatiSpecOnLine);

        // Setto la "nuova" Lista Dati Specifici a video
        getForm().getFiltriDatiSpecUnitaDocumentarieList().setTable(tabellaDatiSpec);
        getForm().getFiltriDatiSpecUnitaDocumentarieList().getTable().setPageSize(300);
        // Salvo in sessione le informazioni
        getSession().setAttribute("listaDatiSpecOnLine", listaDatiSpecOnLine);

        // Aggiorno la text area Filtri dati specifici
        checkFiltriSettatiSuDatiSpecifici();

        // Ordino per nome dato specifico
        getForm().getFiltriDatiSpecUnitaDocumentarieList().getTable().addSortingRule(
                DecAttribDatiSpecTableDescriptor.COL_NM_ATTRIB_DATI_SPEC, SortingRule.ASC);
        getForm().getFiltriDatiSpecUnitaDocumentarieList().getTable().sort();
        getForm().getFiltriDatiSpecUnitaDocumentarieList().getTable().first();
        getForm().getFiltriDatiSpecUnitaDocumentarieList().getTi_oper().setEditMode();
        getForm().getFiltriDatiSpecUnitaDocumentarieList().getDl_valore().setEditMode();
    }

    private void insertFiltroDatoSpecifico(DecAttribDatiSpecRowBean rigaDatoSpecifico,
            List<DecCriterioDatiSpecBean> listaDatiSpecOnLine,
            DecCriterioAttribBean criterioAttrib) {
        boolean giaPresente = false;
        List<DecCriterioAttribBean> totaleDefinitoDaList = new ArrayList();
        // Controllo se il dato specifico che sto trattando Ã¨ giÃ stato inserito
        // nella Lista Dati Specifici presentata a video
        if (!listaDatiSpecOnLine.isEmpty()) {
            for (int j = 0; j < listaDatiSpecOnLine.size(); j++) {
                if (listaDatiSpecOnLine.get(j).getNmAttribDatiSpec()
                        .equals(rigaDatoSpecifico.getNmAttribDatiSpec())) {
                    giaPresente = true;
                    // Se il dato specifico è già presente, ricavo la lista dei suoi
                    // totaliDefinitoDa
                    // e vi aggiungo ad essa il TIPO UNITA' DOCUMENTARIA
                    totaleDefinitoDaList = listaDatiSpecOnLine.get(j).getDecCriterioAttribs();
                    totaleDefinitoDaList.add(criterioAttrib);
                    // FIXME: Controllare l'ordinamento (vedi voce Ordinamenti di liste con
                    // "delegate" sulla wiki)
                    Collections.sort(totaleDefinitoDaList);
                    break;
                }
            }
        }

        // Se invece il dato specifico non Ã¨ giÃ presente, lo inserisco
        // e aggiunto l'informazione su dove Ã¨ "Definito da"
        if (!giaPresente) {
            DecCriterioDatiSpecBean datoSpec = new DecCriterioDatiSpecBean();
            datoSpec.setNmAttribDatiSpec(rigaDatoSpecifico.getString("nm_attrib_dati_spec"));
            datoSpec.setTiOper(rigaDatoSpecifico.getString("ti_oper"));
            datoSpec.setDlValore(rigaDatoSpecifico.getString("dl_valore"));
            totaleDefinitoDaList.add(criterioAttrib);
            datoSpec.setDecCriterioAttribs(totaleDefinitoDaList);
            listaDatiSpecOnLine.add(datoSpec);
        }
    }

    // Metodo utilizzato per costruire l'interfaccia on-line sulla base
    // della lista di bean in memoria
    private BaseTableInterface listBean2TableBean(List<DecCriterioDatiSpecBean> listaDatiSpec) {
        BaseTableInterface tabellaDatiSpec = new BaseTable();
        if (listaDatiSpec != null) {
            for (DecCriterioDatiSpecBean datoSpec : listaDatiSpec) {
                BaseRowInterface rigaDatoSpec = new BaseRow();
                rigaDatoSpec.setString("nm_attrib_dati_spec", datoSpec.getNmAttribDatiSpec());
                rigaDatoSpec.setString("ti_oper", datoSpec.getTiOper());
                rigaDatoSpec.setString("dl_valore", datoSpec.getDlValore());

                BaseRowInterface newRow = new BaseRow();
                if (rigaDatoSpec.getObject(Values.SUB_LIST) == null) {
                    rigaDatoSpec.setObject(Values.SUB_LIST, new BaseTable());
                }

                List<DecCriterioAttribBean> definitoDa = datoSpec.getDecCriterioAttribs();
                for (DecCriterioAttribBean definitoRow : definitoDa) {
                    String rigaDefinitoDa = "";
                    if (definitoRow.getNmTipoUnitaDoc() != null
                            && definitoRow.getNmSistemaMigraz() == null) {
                        rigaDefinitoDa = "Tipo unit\u00e0 documentaria: "
                                + definitoRow.getNmTipoUnitaDoc()
                                + (!definitoRow.getDsListaVersioniXsd().equals("")
                                        ? " (" + definitoRow.getDsListaVersioniXsd() + ")"
                                        : "");
                    } else if (definitoRow.getNmTipoDoc() != null
                            && definitoRow.getNmSistemaMigraz() == null) {
                        rigaDefinitoDa = "Tipo documento: " + definitoRow.getNmTipoDoc()
                                + (!definitoRow.getDsListaVersioniXsd().equals("")
                                        ? " (" + definitoRow.getDsListaVersioniXsd() + ")"
                                        : "");
                    } else if (definitoRow.getTiEntitaSacer().equals(TipoEntitaSacer.UNI_DOC.name())
                            && definitoRow.getNmSistemaMigraz() != null) {
                        rigaDefinitoDa = Constants.TI_SIS_MIGR_UD + ":" + " "
                                + definitoRow.getNmSistemaMigraz()
                                + (!definitoRow.getDsListaVersioniXsd().equals("")
                                        ? " (" + definitoRow.getDsListaVersioniXsd() + ")"
                                        : "");
                    } else if (definitoRow.getTiEntitaSacer().equals(TipoEntitaSacer.DOC.name())
                            && definitoRow.getNmSistemaMigraz() != null) {
                        rigaDefinitoDa = Constants.TI_SIS_MIGR_DOC + ":" + " "
                                + definitoRow.getNmSistemaMigraz()
                                + (!definitoRow.getDsListaVersioniXsd().equals("")
                                        ? " (" + definitoRow.getDsListaVersioniXsd() + ")"
                                        : "");
                    }
                    newRow.setString("definito_da_record", rigaDefinitoDa);
                    ((BaseTableInterface) rigaDatoSpec.getObject(Values.SUB_LIST)).add(newRow);
                }
                tabellaDatiSpec.add(rigaDatoSpec);
            }
        }
        return tabellaDatiSpec;
    }

    @Override
    public JSONObject triggerFiltriComponentiUnitaDocumentarieNm_tipo_strut_docOnTrigger()
            throws EMFError {
        getForm().getFiltriComponentiUnitaDocumentarie().post(getRequest());
        // In base al campo Nm_tipo_strut_doc ricavo il TableBean del tipo struttura documento
        BigDecimal idStrutDoc = getForm().getFiltriComponentiUnitaDocumentarie()
                .getNm_tipo_strut_doc().parse();
        if (idStrutDoc != null) {
            // Setto i valori della combo TIPO COMPONENTE DOCUMENTO ricavati dalla tabella
            // DEC_TIPO_STRUT_DOC
            DecTipoCompDocTableBean tmpTableBeanTipoCompDoc = tipoStrutDocEjb
                    .getDecTipoCompDocTableBean(idStrutDoc, false);
            // Setto i dati ricavati dalla query nella combo NM_TIPO_COMP_DOC
            getForm().getFiltriComponentiUnitaDocumentarie().getNm_tipo_comp_doc()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanTipoCompDoc,
                            "id_tipo_comp_doc", "nm_tipo_comp_doc"));
        } else {
            getForm().getFiltriComponentiUnitaDocumentarie().getNm_tipo_comp_doc()
                    .setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriComponentiUnitaDocumentarie().asJSON();
    }

    @Override
    public void scarica_indice_aip_last() throws EMFError {
        BigDecimal idVerIndiceAip = getForm().getVersioneIndiceAIPLast().getId_ver_indice_aip()
                .parse();
        scarica_indice_aip(idVerIndiceAip);
    }

    @Override
    public void scarica_indice_aip_detail() throws EMFError {
        BigDecimal idVerIndiceAip = getForm().getVersioneIndiceAIPDetail().getId_ver_indice_aip()
                .parse();
        scarica_indice_aip(idVerIndiceAip);
    }

    public void scarica_indice_aip(BigDecimal idVerIndiceAip) throws EMFError {
        String maxResultStandard = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
        AroVerIndiceAipUdRowBean verIndiceRowBean = udHelper
                .getAroVerIndiceAipUdRowBean(idVerIndiceAip, Integer.parseInt(maxResultStandard));
        String filename = verIndiceRowBean.getString("urn");

        ZipArchiveOutputStream out = new ZipArchiveOutputStream(getServletOutputStream());
        getResponse().setContentType("application/zip");
        getResponse().setHeader("Content-Disposition", "attachment; filename=\""
                + ComponenteRec.estraiNomeFileCompleto(filename) + ".zip");

        try {
            zippaIndiceAIPOs(out, idVerIndiceAip.longValue());
            out.flush();
            out.close();
        } catch (Exception e) {
            log.error("Eccezione", e);
        } finally {
            freeze();
        }
    }

    // MEV#30395
    private void zippaIndiceAIPOs(ZipArchiveOutputStream zipOutputStream, Long idVerIndiceAip)
            throws IOException {

        // MEV #27035
        String xmlName = "PIndexUD.xml";
        // end MEV #27035

        ZipArchiveEntry verIndiceAipZae = new ZipArchiveEntry(xmlName);
        zipOutputStream.putArchiveEntry(verIndiceAipZae);

        // recupero documento blob vs obj storage
        // build dto per recupero
        RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(
                Constants.TiEntitaSacerObjectStorage.INDICE_AIP, idVerIndiceAip, zipOutputStream,
                RecClbOracle.TabellaClob.CLOB);
        // recupero
        boolean esitoRecupero = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);

        if (!esitoRecupero) {
            throw new IOException(ECCEZIONE_RECUPERO_INDICE_AIP);
        }

        zipOutputStream.closeArchiveEntry();
    }
    // end MEV#30395

    @Override
    public void scarica_dip_ud() throws EMFError {
        DownloadDip ddip = null;
        try {
            ddip = new DownloadDip(getUser(),
                    getForm().getUnitaDocumentarieDetail().getId_unita_doc().parse());
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
            log.error("Eccezione", ex);
            getMessageBox().addFatal(
                    "Impossibile completare l'operazione, contattare l'assistenza tecnica", ex);
        }
        if (getMessageBox().isEmpty() && ddip != null) {
            getSession().setAttribute(WebConstants.DOWNLOAD_DIP.DIP_RECUPERO_EXT.name(),
                    ddip.getRecuperoExt());
            getSession().setAttribute(WebConstants.DOWNLOAD_DIP.DIP_RISPOSTA_WS.name(),
                    ddip.getRispostaWs());
            getSession().setAttribute(WebConstants.DOWNLOAD_DIP.DIP_ENTITA.name(),
                    CostantiDB.TipiEntitaRecupero.UNI_DOC_DIP.name());
            getForm().getScaricaDipBL().setEditMode();
            forwardToPublisher(Application.Publisher.LISTA_DIP_UD_DOC);
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void scarica_dip_doc() throws EMFError {
        BigDecimal idDoc = (BigDecimal) getSession().getAttribute("iddoc");
        BigDecimal idUd = (BigDecimal) getSession().getAttribute("idud");
        DownloadDip ddip = null;
        if (idDoc != null && idUd != null) {
            try {
                ddip = new DownloadDip(getUser(), idUd);
                ddip.setIdDoc(idDoc);
                ddip.setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.DOC_DIP);
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
                log.error("Eccezione", ex);
                getMessageBox().addFatal(
                        "Impossibile completare l'operazione, contattare l'assistenza tecnica", ex);
            }
        } else {
            getMessageBox().addError("Errore nel recupero degli id del documento");
        }
        if (getMessageBox().isEmpty() && ddip != null) {
            getSession().setAttribute(WebConstants.DOWNLOAD_DIP.DIP_RECUPERO_EXT.name(),
                    ddip.getRecuperoExt());
            getSession().setAttribute(WebConstants.DOWNLOAD_DIP.DIP_RISPOSTA_WS.name(),
                    ddip.getRispostaWs());
            getSession().setAttribute(WebConstants.DOWNLOAD_DIP.DIP_ENTITA.name(),
                    CostantiDB.TipiEntitaRecupero.DOC_DIP.name());
            getForm().getScaricaDipBL().setEditMode();
            forwardToPublisher(Application.Publisher.LISTA_DIP_UD_DOC);
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

        myRecuperoExt.getParametriRecupero().setIdComponente(getForm().getComponentiDipList()
                .getTable().getCurrentRow().getBigDecimal("id_comp").longValue());
        myRecuperoExt.getParametriRecupero()
                .setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.COMP_DIP);
        DownloadDip ddip;
        try {
            ddip = new DownloadDip(myRecuperoExt, rispostaWs);
            ddip.scaricaDipZip(DownloadDip.TIPO_DOWNLOAD.SCARICA_COMP_CONV);
        } catch (NamingException ex) {
            log.error("Eccezione", ex);
            getMessageBox().addFatal(
                    "Impossibile completare l'operazione, contattare l'assistenza tecnica", ex);
        }
        if (getMessageBox().isEmpty()) {
            switch (rispostaWs.getSeverity()) {
            case OK:
                getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(),
                        getControllerName());
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
                getForm().getComponentiDipList().setTable(DownloadDip.generaTableBean(
                        rispostaWs.getDatiRecuperoDip().getElementiTrovati().values()));
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
        CostantiDB.TipiEntitaRecupero tipoEntita = CostantiDB.TipiEntitaRecupero.valueOf(
                (String) getSession().getAttribute(WebConstants.DOWNLOAD_DIP.DIP_ENTITA.name()));

        DownloadDip ddip;
        try {
            ddip = new DownloadDip(myRecuperoExt, rispostaWs);
            ddip.setTipoEntitaSacer(tipoEntita);
            ddip.scaricaDipZip(DownloadDip.TIPO_DOWNLOAD.SCARICA_ZIP);
        } catch (NamingException ex) {
            log.error("Eccezione", ex);
            getMessageBox().addFatal(
                    "Impossibile completare l'operazione, contattare l'assistenza tecnica", ex);
        }
        if (getMessageBox().isEmpty()) {
            switch (rispostaWs.getSeverity()) {
            case OK:
                getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(),
                        getControllerName());
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
                getForm().getComponentiDipList().setTable(DownloadDip.generaTableBean(
                        rispostaWs.getDatiRecuperoDip().getElementiTrovati().values()));
            } else {
                getForm().getComponentiDipList().setTable(null);
            }
            forwardToPublisher(getLastPublisher());
        } else {
            forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
        }
    }

    @Override
    public void visualizza_ud_annul() throws EMFError {
        // Carico l'ud annullata
        BigDecimal idUnitaDoc = getForm().getUnitaDocumentarieDetail().getId_unita_doc_annullata()
                .parse();
        /*
         * Utilizzo idUdStack per navigare tra ud e ud, inserendo nello stack l'id dell'ud in cui mi
         * trovo prima di passare alla successiva
         */
        List<BigDecimal> idUdStack = getIdUdStack();
        idUdStack.add(getForm().getUnitaDocumentarieDetail().getId_unita_doc().parse());
        getSession().setAttribute("idUdStack", idUdStack);
        getDettaglioUD(idUnitaDoc);

        if (getForm().getFakeUnitaDocumentarieList().getTable() == null) {
            getForm().getFakeUnitaDocumentarieList().setTable(new BaseTable());
        } else {
            getForm().getFakeUnitaDocumentarieList().getTable().last();
        }
        BaseRow row = new BaseRow();
        row.setBigDecimal("id_unita_doc", idUnitaDoc);
        getForm().getFakeUnitaDocumentarieList().getTable().add(row);

        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    @Override
    public void visualizza_ud_versata() throws EMFError {
        // Carico l'ud versata
        BigDecimal idUnitaDoc = getForm().getUnitaDocumentarieDetail().getId_unita_doc_versata()
                .parse();
        /*
         * Utilizzo idUdStack per navigare tra ud e ud, inserendo nello stack l'id dell'ud in cui mi
         * trovo prima di passare alla successiva
         */
        List<BigDecimal> idUdStack = getIdUdStack();
        idUdStack.add(getForm().getUnitaDocumentarieDetail().getId_unita_doc().parse());
        getSession().setAttribute("idUdStack", idUdStack);
        getDettaglioUD(idUnitaDoc);

        if (getForm().getFakeUnitaDocumentarieList().getTable() == null) {
            getForm().getFakeUnitaDocumentarieList().setTable(new BaseTable());
        } else {
            getForm().getFakeUnitaDocumentarieList().getTable().last();
        }
        BaseRow row = new BaseRow();
        row.setBigDecimal("id_unita_doc", idUnitaDoc);
        getForm().getFakeUnitaDocumentarieList().getTable().add(row);

        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_DETAIL);
    }

    @Override
    public void selectUnitaDocumentarieList() throws EMFError {
        /* Ricavo il record interessato della lista ud */
        boolean forceGoBack = false;
        BaseRowInterface row = getForm().getUnitaDocumentarieList().getTable().getCurrentRow();
        int index = getForm().getUnitaDocumentarieList().getTable().getCurrentRowIndex();
        BigDecimal idUnitaDoc = row.getBigDecimal("id_unita_doc");
        String cdRegistroUnitaDoc = row.getString("cd_registro_key_unita_doc");
        BigDecimal aaUnitaDoc = row.getBigDecimal("aa_key_unita_doc");
        String cdKeyUnitaDoc = row.getString("cd_key_unita_doc");

        if (!getForm().getUnitaDocumentarieToSerieSection().isHidden()) {
            // Aggiunta a una serie
            String nmTipoUnitaDoc = row.getString("nm_tipo_unita_doc");
            // Mi riprendo il rowBean in quanto ho bisogno di aggiungere alcune informazioni
            // mancanti
            DecRegistroUnitaDocRowBean decRegistroUnitaDocRowBean = registroEjb
                    .getDecRegistroUnitaDocRowBean(cdRegistroUnitaDoc,
                            getUser().getIdOrganizzazioneFoglia());
            row.setBigDecimal("id_registro_unita_doc",
                    decRegistroUnitaDocRowBean.getIdRegistroUnitaDoc());
            DecTipoUnitaDocRowBean decTipoUnitaDocRowBean = tipoUnitaDocEjb
                    .getDecTipoUnitaDocRowBean(nmTipoUnitaDoc,
                            getUser().getIdOrganizzazioneFoglia());
            row.setBigDecimal("id_tipo_unita_doc", decTipoUnitaDocRowBean.getIdTipoUnitaDoc());

            BigDecimal idContenutoSerie = getForm().getUnitaDocumentariePerSerie()
                    .getId_contenuto_serie().parse();
            // Non deve mai capitare, ma per sicurezza eseguo il controllo
            if (idContenutoSerie != null) {
                if (!serieEjb.existUdInContenutoSerie(idContenutoSerie, idUnitaDoc)) {
                    if (serieEjb.existQueryContenutoVerSerie(idContenutoSerie,
                            decRegistroUnitaDocRowBean.getIdRegistroUnitaDoc(),
                            decTipoUnitaDocRowBean.getIdTipoUnitaDoc())) {
                        // Dato che la lista è paginata, devo richiamare il metodo removeFullIdx()
                        // al posto di remove()
                        getForm().getUnitaDocumentarieList().getTable().removeFullIdx(index);
                        /* Aggiungo il record nella lista delle ud selezionate */
                        getForm().getUnitaDocumentariePerSerieList().add(row);
                    } else {
                        getMessageBox().addError("L'unit\u00E0 documentaria " + cdRegistroUnitaDoc
                                + "-" + aaUnitaDoc.toPlainString() + "-" + cdKeyUnitaDoc
                                + " non pu\u00F2 essere inserita nella serie");
                    }
                } else {
                    getMessageBox().addError("L'unit\u00E0 documentaria " + cdRegistroUnitaDoc + "-"
                            + aaUnitaDoc.toPlainString() + "-" + cdKeyUnitaDoc
                            + " \u00E8 gi\u00E0 presente nella serie");
                }
            } else {
                getMessageBox().addError(
                        "Errore inaspettato nell'aggiunta di unit\u00E0 documentarie al contenuto");
                forceGoBack = true;
            }
        } else if (!getForm().getUnitaDocumentarieToRichAnnulVersSection().isHidden()) {
            // Aggiunta a una richiesta di annullamento
            BigDecimal idRichAnnulVers = getForm().getUnitaDocumentariePerRichAnnulVers()
                    .getId_rich_annul_vers().parse();
            // Non deve mai capitare, ma per sicurezza eseguo il controllo
            if (idRichAnnulVers != null) {
                if (!annulVersEjb.isUdInRichAnnulVers(idUnitaDoc)) {
                    // Dato che la lista è paginata, devo richiamare il metodo removeFullIdx() al
                    // posto di remove()
                    getForm().getUnitaDocumentarieList().getTable().removeFullIdx(index);
                    /* Aggiungo il record nella lista delle ud selezionate */
                    getForm().getUnitaDocumentariePerRichAnnulVersList().add(row);
                } else {
                    getMessageBox().addError("L'unit\u00E0 documentaria " + cdRegistroUnitaDoc + "-"
                            + aaUnitaDoc.toPlainString() + "-" + cdKeyUnitaDoc
                            + " \u00E8 gi\u00E0 presente in una richiesta di annullamento");
                }
            } else {
                getMessageBox().addError(
                        "Errore inaspettato nell'aggiunta di unit\u00E0 documentarie alla richiesta di annullamento");
                forceGoBack = true;
            }
        }

        if (getMessageBox().hasError() && forceGoBack) {
            goBack();
        } else {
            forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_AVANZATA);
        }
    }

    @Override
    public void selectUnitaDocumentariePerSerieList() throws EMFError {
        BaseRowInterface row = getForm().getUnitaDocumentariePerSerieList().getTable()
                .getCurrentRow();
        int index = getForm().getUnitaDocumentariePerSerieList().getTable().getCurrentRowIndex();
        getForm().getUnitaDocumentariePerSerieList().getTable().remove(index);
        getForm().getUnitaDocumentarieList().getTable().addFullIdx(row);

        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_AVANZATA);
    }

    @Override
    public void addToSerie() throws EMFError {
        boolean forceGoBack = false;
        if (!getForm().getUnitaDocumentariePerSerieList().getTable().isEmpty()) {
            BigDecimal idContenutoSerie = getForm().getUnitaDocumentariePerSerie()
                    .getId_contenuto_serie().parse();
            if (idContenutoSerie != null) {
                // Registro una mappa che mi permetta di raggruppare i dati per eseguire le query
                // strettamente
                // necessarie
                Map<RegistroTipoUnitaDoc, List<BigDecimal>> map = new HashMap<>();
                for (BaseRowInterface row : getForm().getUnitaDocumentariePerSerieList()
                        .getTable()) {
                    BigDecimal idUnitaDoc = row.getBigDecimal("id_unita_doc");
                    BigDecimal aaUnitaDoc = row.getBigDecimal("aa_key_unita_doc");
                    String cdKeyUnitaDoc = row.getString("cd_key_unita_doc");
                    BigDecimal idTipoUnitaDoc = row.getBigDecimal("id_tipo_unita_doc");
                    BigDecimal idRegistroUnitaDoc = row.getBigDecimal("id_registro_unita_doc");
                    String nmTipoUnitaDoc = row.getString("nm_tipo_unita_doc");
                    String cdRegistroUnitaDoc = row.getString("cd_registro_key_unita_doc");

                    if (serieEjb.existQueryContenutoVerSerie(idContenutoSerie, idRegistroUnitaDoc,
                            idTipoUnitaDoc)) {
                        RegistroTipoUnitaDoc registroTipoUnitaDoc = new RegistroTipoUnitaDoc(
                                idRegistroUnitaDoc, cdRegistroUnitaDoc, idTipoUnitaDoc,
                                nmTipoUnitaDoc);
                        List<BigDecimal> unitaDocList = map.get(registroTipoUnitaDoc);
                        if (unitaDocList == null) {
                            unitaDocList = new ArrayList<>();
                            map.put(registroTipoUnitaDoc, unitaDocList);
                        }
                        unitaDocList.add(idUnitaDoc);
                    } else {
                        getMessageBox().addError("L'unit\u00E0 documentaria " + cdRegistroUnitaDoc
                                + "-" + aaUnitaDoc.toPlainString() + "-" + cdKeyUnitaDoc
                                + " non pu\u00F2 essere inserita nella serie<br/>");
                    }
                }
                try {
                    if (!getMessageBox().hasError()) {
                        int countUd = serieEjb.aggiungiUnitaDocAlContenuto(getUser().getIdUtente(),
                                idContenutoSerie, map);
                        int maxUd = getForm().getUnitaDocumentariePerSerieList().getTable().size();
                        getMessageBox().addInfo("Sono state aggiunte alla serie " + countUd
                                + " unit\u00E0 documentarie su " + maxUd + " selezionate");
                        getMessageBox().setViewMode(MessageBox.ViewMode.plain);

                        getForm().getUnitaDocumentariePerSerieList().getTable().clear();
                    }
                } catch (ParerUserError ex) {
                    getMessageBox().addError(ex.getDescription());
                }
            } else {
                getMessageBox().addError(
                        "Errore inaspettato nell'aggiunta di unit\u00E0 documentarie al contenuto : contenuto non caricato");
                forceGoBack = true;
            }
        } else {
            getMessageBox().addError(
                    "Selezionare almeno una unit\u00E0 documentaria da aggiungere alla serie selezionata");
        }
        if (forceGoBack) {
            goBack();
        } else {
            forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_AVANZATA);
        }
    }

    @Override
    public void selectUnitaDocumentariePerRichAnnulVersList() throws EMFError {
        BaseRowInterface row = getForm().getUnitaDocumentariePerRichAnnulVersList().getTable()
                .getCurrentRow();
        int index = getForm().getUnitaDocumentariePerRichAnnulVersList().getTable()
                .getCurrentRowIndex();
        getForm().getUnitaDocumentariePerRichAnnulVersList().getTable().remove(index);
        getForm().getUnitaDocumentarieList().getTable().addFullIdx(row);

        forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_AVANZATA);
    }

    @Override
    public void addToRichAnnul() throws EMFError {
        boolean forceGoBack = false;
        if (!getForm().getUnitaDocumentariePerRichAnnulVersList().getTable().isEmpty()) {
            BigDecimal idRichAnnulVers = getForm().getUnitaDocumentariePerRichAnnulVers()
                    .getId_rich_annul_vers().parse();
            try {
                if (idRichAnnulVers != null) {
                    // Registro una mappa che mi permetta di raggruppare i dati per eseguire le
                    // query strettamente
                    // necessarie
                    BigDecimal ultimoProgressivoItemRichiesta = annulVersEjb
                            .getUltimoProgressivoItemRichiesta(idRichAnnulVers);
                    int progressivo = ultimoProgressivoItemRichiesta.add(BigDecimal.ONE).intValue();
                    for (BaseRowInterface row : getForm().getUnitaDocumentariePerRichAnnulVersList()
                            .getTable()) {
                        BigDecimal idUnitaDoc = row.getBigDecimal("id_unita_doc");
                        BigDecimal aaUnitaDoc = row.getBigDecimal("aa_key_unita_doc");
                        String cdKeyUnitaDoc = row.getString("cd_key_unita_doc");
                        String cdRegistroUnitaDoc = row.getString("cd_registro_key_unita_doc");

                        if (!annulVersEjb.isUdInRichAnnulVers(idUnitaDoc)) {
                            annulVersEjb.addUnitaDocToRichAnnulVers(idRichAnnulVers,
                                    cdRegistroUnitaDoc, aaUnitaDoc, cdKeyUnitaDoc, progressivo++,
                                    getUser().getIdUtente());
                        } else {
                            getMessageBox().addError("L'unit\u00E0 documentaria "
                                    + cdRegistroUnitaDoc + "-" + aaUnitaDoc.toPlainString() + "-"
                                    + cdKeyUnitaDoc
                                    + " \u00E8 gi\u00E0 presente in una richiesta di annullamento");
                        }
                    }
                    if (!getMessageBox().hasError()) {
                        getMessageBox().addInfo(
                                "Le unit\u00E0 documentarie selezionate sono state aggiunte con successo alla richiesta");
                        getMessageBox().setViewMode(MessageBox.ViewMode.plain);

                        getForm().getUnitaDocumentariePerRichAnnulVersList().getTable().clear();
                    }
                } else {
                    getMessageBox().addError(
                            "Errore inaspettato nell'aggiunta di unit\u00E0 documentarie alla richiesta di annullamento : richiesta non caricata");
                    forceGoBack = true;
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        } else {
            getMessageBox().addError(
                    "Selezionare almeno una unit\u00E0 documentaria da aggiungere alla richiesta di annullamento selezionata");
        }
        if (forceGoBack) {
            goBack();
        } else {
            forwardToPublisher(Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_AVANZATA);
        }
    }

    @Override
    public JSONObject triggerFiltriUnitaDocumentarieSempliceNm_tipo_unita_docOnTrigger()
            throws EMFError {
        getForm().getFiltriUnitaDocumentarieSemplice().post(getRequest());
        BigDecimal idTipoUnitaDoc = getForm().getFiltriUnitaDocumentarieSemplice()
                .getNm_tipo_unita_doc().parse();

        if (idTipoUnitaDoc != null) {
            // Recupero le versioni XSD associate al tipo unità documentaria selezionato
            DecodeMap mappaVersioniXsd = new DecodeMap();
            mappaVersioniXsd
                    .populatedMap(
                            datiSpecEjb.getXsdDatiSpecTableBeanByTipoEntita(
                                    idTipoUnitaDoc.longValue(), TipoEntitaSacer.UNI_DOC),
                            "cd_versione_xsd", "cd_versione_xsd");
            getForm().getFiltriUnitaDocumentarieSemplice().getCd_versione_xsd_ud()
                    .setDecodeMap(mappaVersioniXsd);
        } else {
            getForm().getFiltriUnitaDocumentarieSemplice().getCd_versione_xsd_ud()
                    .setDecodeMap(new DecodeMap());
        }

        return getForm().getFiltriUnitaDocumentarieSemplice().asJSON();
    }

    @Override
    public JSONObject triggerFiltriUnitaDocumentarieSempliceNm_tipo_docOnTrigger() throws EMFError {
        getForm().getFiltriUnitaDocumentarieSemplice().post(getRequest());
        BigDecimal idTipoDoc = getForm().getFiltriUnitaDocumentarieSemplice().getNm_tipo_doc()
                .parse();

        if (idTipoDoc != null) {
            // Recupero le versioni XSD associate al tipo documento selezionato
            DecodeMap mappaVersioniXsd = new DecodeMap();
            mappaVersioniXsd.populatedMap(
                    datiSpecEjb.getXsdDatiSpecTableBeanByTipoEntita(idTipoDoc.longValue(),
                            TipoEntitaSacer.DOC),
                    "cd_versione_xsd", "cd_versione_xsd");
            getForm().getFiltriUnitaDocumentarieSemplice().getCd_versione_xsd_doc()
                    .setDecodeMap(mappaVersioniXsd);

            // Gestione Elemento
            getForm().getFiltriUnitaDocumentarieSemplice().getTi_doc().setDecodeMap(
                    ComboGetter.getMappaSortedGenericEnum("ti_doc", Constants.TiDoc.values()));
        } else {
            getForm().getFiltriUnitaDocumentarieSemplice().getCd_versione_xsd_doc()
                    .setDecodeMap(new DecodeMap());
            getForm().getFiltriUnitaDocumentarieSemplice().getTi_doc()
                    .setDecodeMap(new DecodeMap());
        }

        return getForm().getFiltriUnitaDocumentarieSemplice().asJSON();
    }

    // MEV#24597
    @Override
    public void deleteNoteList() throws EMFError {
        AroVLisNotaUnitaDocRowBean currentRow = (AroVLisNotaUnitaDocRowBean) getForm().getNoteList()
                .getTable().getCurrentRow();
        if (currentRow.getIdVerIndiceAip() != null) {
            getMessageBox()
                    .addError("La nota \u00E8 presente nella versione dell'indice aip con id: "
                            + currentRow.getIdVerIndiceAip().toPlainString()
                            + ", non può pertanto essere eliminata.");
            forwardToPublisher(getLastPublisher());
        } else if (currentRow.getCdTipoNotaUnitaDoc().equals("NOTE_CONSERVATORE")
                && !udHelper.isUserAppartAllOk(getUser().getIdUtente())) {
            getMessageBox().addError(
                    "L'utente non pu\u00F2 eliminare la nota perch\u00E9 non appartenente ad ente AMMINISTRATORE o CONSERVATORE");
            forwardToPublisher(getLastPublisher());
        } else {
            BigDecimal idNota = currentRow.getIdNotaUnitaDoc();
            int riga = getForm().getNoteList().getTable().getCurrentRowIndex();
            // Eseguo giusto un controllo per verificare che io stia prendendo la riga giusta se
            // sono nel dettaglio
            if (getLastPublisher().equals(Application.Publisher.NOTA_UD_DETAIL)) {
                if (!idNota.equals(getForm().getNotaDetail().getId_nota_unita_doc().parse())) {
                    getMessageBox().addError("Eccezione imprevista nell'eliminazione della nota");
                }
            }

            if (!getMessageBox().hasError() && idNota != null) {
                try {
                    udHelper.deleteNota(idNota);
                    getForm().getNoteList().getTable().remove(riga);

                    getMessageBox().addInfo("Nota eliminata con successo");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                } catch (ParerUserError ex) {
                    getMessageBox().addError(
                            "La nota non pu\u00F2 essere eliminata: " + ex.getDescription());
                }
            }
        }
        if (!getMessageBox().hasError()
                && getLastPublisher().equals(Application.Publisher.NOTA_UD_DETAIL)) {
            goBack();
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void deleteNotaDetail() throws EMFError {
        deleteNoteList();
    }

    @Override
    public void updateNoteList() throws EMFError {
        AroVLisNotaUnitaDocRowBean currentRow = (AroVLisNotaUnitaDocRowBean) getForm().getNoteList()
                .getTable().getCurrentRow();
        BigDecimal idNotaUnitaDoc = currentRow.getBigDecimal("id_nota_unita_doc");
        if (currentRow.getIdVerIndiceAip() != null) {
            getMessageBox()
                    .addError("La nota \u00E8 presente nella versione dell'indice aip con id: "
                            + currentRow.getIdVerIndiceAip().toPlainString()
                            + ", non può pertanto essere modificata.");
            forwardToPublisher(getLastPublisher());
        } else if (currentRow.getCdTipoNotaUnitaDoc().equals("NOTE_CONSERVATORE")
                && !udHelper.isUserAppartAllOk(getUser().getIdUtente())) {
            getMessageBox().addError(
                    "L'utente non pu\u00F2 modificare la nota perch\u00E9 non appartenente ad ente AMMINISTRATORE o CONSERVATORE");
            forwardToPublisher(getLastPublisher());
        } else {
            BaseRow tmpRow = new BaseRow();
            getForm().getUnitaDocumentarieDetail().copyToBean(tmpRow);
            getForm().getDatiUDDetail().copyFromBean(tmpRow);

            getForm().getNotaDetail().getId_tipo_nota_unita_doc()
                    .setDecodeMap(DecodeMap.Factory.newInstance(
                            udHelper.getSingleDecTipoNotaUnitaDocTableBean(
                                    currentRow.getIdTipoNotaUnitaDoc()),
                            "id_tipo_nota_unita_doc", "ds_tipo_nota_unita_doc"));
            getForm().getNotaDetail().setViewMode();

            AroVVisNotaUnitaDocRowBean notaRB = udHelper
                    .getAroVVisNotaUnitaDocRowBean(idNotaUnitaDoc);
            // Copio i dati della nota sulla form di dettaglio
            getForm().getNotaDetail().copyFromBean(notaRB);

            getForm().getNotaDetail().getDs_nota_unita_doc().setEditMode();
            DateFormat formato = new SimpleDateFormat(WebConstants.DATE_FORMAT_HOUR_MINUTE_TYPE);
            getForm().getNotaDetail().getDt_nota_unita_doc()
                    .setValue(formato.format(Calendar.getInstance().getTime()));

            getForm().getNoteList().setStatus(BaseElements.Status.update);
            getForm().getNotaDetail().setStatus(BaseElements.Status.update);

            forwardToPublisher(Application.Publisher.NOTA_UD_DETAIL);
        }
    }

    @Override
    public void updateNotaDetail() throws EMFError {
        updateNoteList();
    }

    @Override
    public JSONObject triggerNotaDetailId_tipo_nota_unita_docOnTrigger() throws EMFError {
        getForm().getNotaDetail().post(getRequest());
        BigDecimal idTipoNotaUnitaDoc = getForm().getNotaDetail().getId_tipo_nota_unita_doc()
                .parse();
        BigDecimal idUnitaDoc = getForm().getUnitaDocumentarieDetail().getId_unita_doc().parse();
        // VERIFICARE

        if (idTipoNotaUnitaDoc != null) {
            BigDecimal lastPgNota = udHelper.getMaxPgNota(idUnitaDoc, idTipoNotaUnitaDoc);
            String nextPg = lastPgNota.add(BigDecimal.ONE).toPlainString();
            getForm().getNotaDetail().getPg_nota_unita_doc().setValue(nextPg);
        } else {
            getForm().getNotaDetail().getPg_nota_unita_doc()
                    .setValue(BigDecimal.ONE.toPlainString());
        }

        return getForm().getNotaDetail().asJSON();
    }
    // end MEV#24597

    @Override
    public void downloadContenuto() throws Throwable {
        ricercaEDownload(true);
    }

    @Override
    public void downloadContenutoAnnullate() throws Throwable {
        ricercaEDownloadAnnullate(true);
    }

    @Override
    protected void postLoad() {
        super.postLoad();
        Object ogg = getForm();
        if (ogg instanceof UnitaDocumentarieForm) {
            UnitaDocumentarieForm form = getForm();
            form.getUnitaDocumentarieRicercaButtonList().getDownloadContenuto().setEditMode();
            form.getUnitaDocumentarieRicercaButtonList().getDownloadContenuto()
                    .setDisableHourGlass(true);
            if (form.getUnitaDocumentarieList().getTable() != null
                    && form.getUnitaDocumentarieList().getTable().size() > 0) {
                form.getUnitaDocumentarieRicercaButtonList().getDownloadContenuto()
                        .setHidden(false);
            } else {
                form.getUnitaDocumentarieRicercaButtonList().getDownloadContenuto().setHidden(true);
            }

            if (form.getUnitaDocumentarieAnnullateList().getTable() != null
                    && form.getUnitaDocumentarieAnnullateList().getTable().size() > 0) {
                form.getUnitaDocumentarieRicercaButtonList().getDownloadContenutoAnnullate()
                        .setHidden(false);
                form.getUnitaDocumentarieRicercaButtonList().getDownloadContenutoAnnullate()
                        .setDisableHourGlass(true);
            } else {
                form.getUnitaDocumentarieRicercaButtonList().getDownloadContenutoAnnullate()
                        .setHidden(true);
            }
        }

    }

    private void redirectToPage(final String action, BaseForm form, String listToPopulate,
            BaseTableInterface<?> table, String event) throws EMFError {
        ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) form.getComponent(listToPopulate))
                .setTable(table);
        redirectToAction(action, "?operation=listNavigationOnClick&navigationEvent=" + event
                + "&table=" + listToPopulate + "&riga=" + table.getCurrentRowIndex(), form);
    }

    /**
     * Metodo richiamato dal link per accedere alla pagina di dettaglio Elenco di Versamento
     *
     * @throws EMFError errore generico
     */
    public void loadDettaglioElencoVersamento() throws EMFError {
        String riga = getRequest().getParameter("riga");
        BigDecimal numberRiga = BigDecimal.ZERO;
        if (StringUtils.isNotBlank(riga)) {
            numberRiga = new BigDecimal(riga);
        }
        // Recupero l'elenco di versamento
        BigDecimal idElencoVers = ((BaseTableInterface) getForm().getAggiornamentiMetadatiList()
                .getTable()).getRow(numberRiga.intValue()).getBigDecimal("id_elenco_vers");
        ElvElencoVerRowBean elencoVersRow = evEjb.getElvElencoVersRowBean(idElencoVers);
        // Setto la tabella degli elenchi di versamento aggiungendo solo quella recuperata
        ElvElencoVerTableBean elencoVersTable = new ElvElencoVerTableBean();
        elencoVersTable.add(elencoVersRow);
        elencoVersTable.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        setNavigationEvent(NE_DETTAGLIO_VIEW);

        ElenchiVersamentoForm form = new ElenchiVersamentoForm();
        redirectToPage(Application.Actions.ELENCHI_VERSAMENTO, form,
                form.getElenchiVersamentoList().getName(), elencoVersTable, getNavigationEvent());
    }

    /**
     * Metodo richiamato dal link per accedere alla pagina di dettaglio di una versione dell'Indice
     * Aip
     *
     * @throws EMFError errore generico
     */
    public void loadDettaglioVersioneIndiceAip() throws EMFError {
        String riga = getRequest().getParameter("riga");
        BigDecimal numberRiga = BigDecimal.ZERO;
        if (StringUtils.isNotBlank(riga)) {
            numberRiga = new BigDecimal(riga);
        }
        // Recupero la versione indice aip
        BigDecimal idVerIndiceAipUd = ((BaseTableInterface) getForm().getNoteList().getTable())
                .getRow(numberRiga.intValue()).getBigDecimal("id_ver_indice_aip");

        AroVerIndiceAipUdRowBean versioneIndice = udHelper
                .retrieveVersioneIndiceAipUdById(idVerIndiceAipUd.longValue());
        getForm().getVersioneIndiceAIPDetail().copyFromBean(versioneIndice);
        getForm().getVersioneIndiceAIPDetail().getCd_registro_key_unita_doc().setValue(
                getForm().getUnitaDocumentarieDetail().getCd_registro_key_unita_doc().parse());
        getForm().getVersioneIndiceAIPDetail().getAa_key_unita_doc().setValue(
                getForm().getUnitaDocumentarieDetail().getAa_key_unita_doc().parse().toString());
        getForm().getVersioneIndiceAIPDetail().getCd_key_unita_doc()
                .setValue(getForm().getUnitaDocumentarieDetail().getCd_key_unita_doc().parse());
        getForm().getVersioneIndiceAIPDetail().getNm_ambiente()
                .setValue(getForm().getUnitaDocumentarieDetail().getNm_ambiente().parse());
        getForm().getVersioneIndiceAIPDetail().getNm_ente()
                .setValue(getForm().getUnitaDocumentarieDetail().getNm_ente().parse());
        getForm().getVersioneIndiceAIPDetail().getNm_strut()
                .setValue(getForm().getUnitaDocumentarieDetail().getNm_strut().parse());
        getForm().getVersioneIndiceAIPDetail().getScarica_indice_aip_detail().setEditMode();
        getForm().getVersioneIndiceAIPDetail().getScarica_indice_aip_detail()
                .setDisableHourGlass(true);

        // MEV#30395
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // recupero documento blob vs obj storage
            // build dto per recupero
            RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(
                    Constants.TiEntitaSacerObjectStorage.INDICE_AIP,
                    versioneIndice.getIdVerIndiceAip().longValue(), baos,
                    RecClbOracle.TabellaClob.CLOB);
            // recupero
            boolean esitoRecupero = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);
            if (!esitoRecupero) {
                throw new IOException(ECCEZIONE_RECUPERO_INDICE_AIP);
            }
            XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
            String xmlIndice = formatter
                    .prettyPrintWithDOM3LS(baos.toString(StandardCharsets.UTF_8.displayName()));
            getForm().getVersioneIndiceAIPDetail().getBl_file_ver_indice_aip().setValue(xmlIndice);
        } catch (IOException ex) {
            getMessageBox().addError(ex.getMessage());
        }
        // MEV#30395

        // Setto la tabella delle versioni indici aip aggiungendo solo quella recuperata
        AroVerIndiceAipUdTableBean listIndiciAIPTB = new AroVerIndiceAipUdTableBean();
        listIndiciAIPTB.add(versioneIndice);
        listIndiciAIPTB.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getIndiciAIPList().setTable(listIndiciAIPTB);
        getForm().getIndiciAIPList().getTable().setPageSize(10);
        getForm().getIndiciAIPList().getTable().first();

        setNavigationEvent(NE_DETTAGLIO_VIEW);

        forwardToPublisher(Application.Publisher.INDICE_AIP_DETAIL);
    }

    @Override
    public void ricercaUDAnnullate() throws Throwable {
        ricercaEDownloadAnnullate(false);
    }

    private void ricercaEDownloadAnnullate(boolean effettuaDownload) throws EMFError {
        // /* Resetto la lista per evitare conflitti tra la ricerca avanzata (con selectedList)
        // e la ricerca versamenti annullati (lista "normale") aolo sw non sto facendo download
        // altrimenti
        // quando builda il csv non trova nulla nella table */
        getForm().getUnitaDocumentarieAnnullateList().setHideDetailButton(false);
        // Valida i filtri per verificare quelli obbligatori
        String pageToRedirect = Application.Publisher.UNITA_DOCUMENTARIE_RICERCA_UDANNULLATE;
        final UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate filtri = getForm()
                .getFiltriUnitaDocumentarieAnnullate();
        // Esegue la post dei filtri compilati
        filtri.post(getRequest());
        // Valida i filtri per verificare quelli obbligatori e che siano del tipo corretto
        if (filtri.validate(getMessageBox())) {
            // Valida i campi di ricerca
            UnitaDocumentarieValidator validator = new UnitaDocumentarieValidator(getMessageBox());
            Date[] dateAcquisizioneValidate = null;
            if (!getMessageBox().hasError()) {
                // Valido i filtri data creazione da - a restituendo le date comprensive di orario
                dateAcquisizioneValidate = validator.validaDate(
                        filtri.getDt_creazione_unita_doc_da().parse(),
                        filtri.getOre_dt_creazione_unita_doc_da().parse(),
                        filtri.getMinuti_dt_creazione_unita_doc_da().parse(),
                        filtri.getDt_creazione_unita_doc_a().parse(),
                        filtri.getOre_dt_creazione_unita_doc_a().parse(),
                        filtri.getMinuti_dt_creazione_unita_doc_a().parse(),
                        filtri.getDt_creazione_unita_doc_da().getHtmlDescription(),
                        filtri.getDt_creazione_unita_doc_a().getHtmlDescription());
            }

            Date[] dateUnitaDocValidate = null;
            if (!getMessageBox().hasError()) {
                dateUnitaDocValidate = validator.validaDate(filtri.getDt_reg_unita_doc_da().parse(),
                        null, null, filtri.getDt_reg_unita_doc_a().parse(), null, null,
                        filtri.getDt_reg_unita_doc_da().getHtmlDescription(),
                        filtri.getDt_reg_unita_doc_a().getHtmlDescription());
            }

            Date[] dateAnnulValidate = null;
            if (!getMessageBox().hasError()) {
                dateAnnulValidate = validator.validaDate(filtri.getDt_annul_da().parse(), null,
                        null, filtri.getDt_annul_a().parse(), null, null,
                        filtri.getDt_annul_da().getHtmlDescription(),
                        filtri.getDt_annul_a().getHtmlDescription());
            }

            if (!getMessageBox().hasError()) {
                // Controllo l'obbligatorietÃ di anno o range anni di chiave unitÃ documentaria
                validator.controllaPresenzaAnno(filtri.getAa_key_unita_doc().parse(),
                        filtri.getAa_key_unita_doc_da().parse(),
                        filtri.getAa_key_unita_doc_a().parse());
            }

            Set<String> cdRegistroKeyUnitaDocSetPerRicerca = getValoriForQueryFromFiltroCdRegistroMultiselect(
                    filtri.getCd_registro_key_unita_doc());
            List<BigDecimal> idTipoUnitaDocListPerRicerca = getValoriForQueryFromFiltroIdTipoUdMultiselect(
                    filtri.getId_tipo_unita_doc());
            List<BigDecimal> idTipoDocListPerRicerca = getValoriForQueryFromFiltroIdTipoDocMultiselect(
                    filtri.getId_tipo_doc());

            // Valida i campi di Range di chiavi unità documentaria
            Object[] chiavi = null;
            if (!getMessageBox().hasError()) {
                String[] registro = Arrays.copyOf(
                        filtri.getCd_registro_key_unita_doc().getDecodedValues().toArray(),
                        filtri.getCd_registro_key_unita_doc().getDecodedValues().toArray().length,
                        String[].class);
                chiavi = validator.validaChiaviUnitaDoc(registro,
                        filtri.getAa_key_unita_doc().parse(), filtri.getCd_key_unita_doc().parse(),
                        filtri.getAa_key_unita_doc_da().parse(),
                        filtri.getAa_key_unita_doc_a().parse(),
                        filtri.getCd_key_unita_doc_da().parse(),
                        filtri.getCd_key_unita_doc_a().parse());
            }

            if (!getMessageBox().hasError()) {
                // La validazione non ha riportato errori.
                // Setto i filtri di chiavi unitÃ documentaria impostando gli eventuali valori di
                // default
                if (chiavi != null && chiavi.length == 5) {
                    filtri.getAa_key_unita_doc_da().setValue(
                            chiavi[1] != null ? ((BigDecimal) chiavi[1]).toString() : null);
                    filtri.getAa_key_unita_doc_a().setValue(
                            chiavi[2] != null ? ((BigDecimal) chiavi[2]).toString() : null);
                    filtri.getCd_key_unita_doc_da()
                            .setValue(chiavi[3] != null ? (String) chiavi[3] : null);
                    filtri.getCd_key_unita_doc_a()
                            .setValue(chiavi[4] != null ? (String) chiavi[4] : null);
                }

                if (effettuaDownload) {
                    AroVRicUnitaDocTableBean tb = udHelper
                            .getAroVRicUnitaDocRicAnnullateTableBeanNoLimit(filtri,
                                    idTipoUnitaDocListPerRicerca,
                                    cdRegistroKeyUnitaDocSetPerRicerca, idTipoDocListPerRicerca,
                                    dateAcquisizioneValidate, dateUnitaDocValidate,
                                    dateAnnulValidate);
                    // Carico la tabella con i risultati nella lista usata per buildare il CSV
                    if (!getMessageBox().hasError()) {
                        // MAC#39494 - Correzione metodo di generazione file in fase di esportazione
                        // di una ricerca
                        // File tmpFile = new File(System.getProperty("java.io.tmpdir"),
                        // "Contenuto_ricerca_unita_documentarie.csv");
                        try {
                            // MAC#39494 - Correzione metodo di generazione file in fase di
                            // esportazione di una ricerca
                            Path tmpPath = Files.createTempFile(
                                    "Contenuto_ricerca_unita_documentarie_", ".csv");
                            File tmpFile = tmpPath.toFile();

                            ActionUtils.buildCsvString(getForm().getUnitaDocumentarieList(), tb,
                                    AroVRicUnitaDocTableBean.TABLE_DESCRIPTOR, tmpFile);
                            getRequest().setAttribute(
                                    WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(),
                                    getControllerName());

                            // MAC#39494 - Correzione metodo di generazione file in fase di
                            // esportazione di una ricerca
                            String nomeFile = tmpFile.getName();
                            String nomeFinale = nomeFile.substring(0, nomeFile.lastIndexOf("_") - 1)
                                    + ".csv";
                            getSession().setAttribute(
                                    WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(),
                                    nomeFinale);

                            getSession().setAttribute(
                                    WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(),
                                    tmpFile.getPath());
                            getSession().setAttribute(
                                    WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
                                    Boolean.toString(true));
                            getSession().setAttribute(
                                    WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                                    "text/csv");
                        } catch (IOException ex) {
                            log.error(
                                    "Errore in download " + ExceptionUtils.getRootCauseMessage(ex),
                                    ex);
                            getMessageBox()
                                    .addError("Errore inatteso nella preparazione del download");
                        }
                    }

                    if (getMessageBox().hasError()) {
                        pageToRedirect = getLastPublisher();
                    } else {
                        pageToRedirect = Application.Publisher.DOWNLOAD_PAGE;
                    }

                } else {
                    AroVRicUnitaDocTableBean tb = udHelper.getAroVRicUnitaDocRicAnnullateTableBean(
                            filtri, idTipoUnitaDocListPerRicerca,
                            cdRegistroKeyUnitaDocSetPerRicerca, idTipoDocListPerRicerca,
                            dateAcquisizioneValidate, dateUnitaDocValidate, dateAnnulValidate);
                    // Carico la tabella con i filtri impostati
                    getForm().getUnitaDocumentarieAnnullateList().setTable(tb);
                    getForm().getUnitaDocumentarieAnnullateList().getTable().setPageSize(10);
                    // Aggiungo alla lista una regola di ordinamento
                    getForm().getUnitaDocumentarieAnnullateList().getTable().addSortingRule(
                            AroVRicUnitaDocTableDescriptor.COL_DS_KEY_ORD, SortingRule.ASC);

                    // Workaround in modo che la lista punti al primo record, non all'ultimo
                    getForm().getUnitaDocumentarieAnnullateList().getTable().first();
                }
            }
        }
        // Carico la pagina di ricerca
        forwardToPublisher(pageToRedirect);
        postLoad();
    }

    // EVO#16486
    public void verificaUrnUd(long idUnitaDoc) throws ParerInternalError, ParseException {
        AroUnitaDoc aroUnitaDoc = udHelper.findById(AroUnitaDoc.class, idUnitaDoc);
        String sistemaConservazione = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
        CSVersatore versatore = this.getVersatoreUd(aroUnitaDoc, sistemaConservazione);
        CSChiave chiave = this.getChiaveUd(aroUnitaDoc);

        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
        String dataInizioParam = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.DATA_INIZIO_CALC_NUOVI_URN);
        Date dataInizio = dateFormat.parse(dataInizioParam);

        // Gestione KEY NORMALIZED / URN PREGRESSI
        this.sistemaUrnUnitaDoc(aroUnitaDoc, dataInizio, versatore, chiave);
        // Sistema URN INDICI AIP PREGRESSI
        AroVerIndiceAipUd aroVerIndiceAipUd = udHelper
                .getUltimaVersioneIndiceAip(aroUnitaDoc.getIdUnitaDoc());
        if (aroVerIndiceAipUd != null && !aroVerIndiceAipUd.getDtCreazione().after(dataInizio)) {
            // eseguo registra urn aip pregressi
            urnHelper.scriviUrnAipUdPreg(aroUnitaDoc, versatore, chiave);
        }

        // ATTENZIONE: lo stesso metodo è presente in ElaborazioneRigaIndiceAipDaElab (nell'ambito
        // della creazione
        // indice AIP)
        // dove, oltre alla logica sopra presente, è riportato il calcolo degli URN delle UD
        // collegate. Tale logica
        // è correttamente presente per la creazione indice AIP (informazioni che sono necessarie)
        // mentre in questo caso
        // non è
        // presente in quanto si sta scaricando solo l'UD in questione. Nel caso, gli URN delle UD
        // collegate verranno
        // comunque generati
        // proprio nel processo di scarica UD dell'ud collegata.
    }

    public void sistemaUrnUnitaDoc(AroUnitaDoc aroUnitaDoc, Date dataInizio, CSVersatore versatore,
            CSChiave chiave) throws ParerInternalError {
        // 1. se il numero normalizzato sull’unità doc nel DB è nullo ->
        // il sistema aggiorna ARO_UNITA_DOC
        // controllo : dtVersMax <= dataInizioCalcNuoviUrn
        AroVDtVersMaxByUnitaDoc aroVDtVersMaxByUd = udHelper
                .getAroVDtVersMaxByUd(aroUnitaDoc.getIdUnitaDoc());
        if (!aroVDtVersMaxByUd.getDtVersMax().after(dataInizio)
                && StringUtils.isBlank(aroUnitaDoc.getCdKeyUnitaDocNormaliz())) {
            // calcola e verifica la chiave normalizzata
            String cdKeyNormalized = MessaggiWSFormat
                    .normalizingKey(aroUnitaDoc.getCdKeyUnitaDoc()); // base
            if (urnHelper.existsCdKeyNormalized(
                    aroUnitaDoc.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc(),
                    aroUnitaDoc.getAaKeyUnitaDoc(), aroUnitaDoc.getCdKeyUnitaDoc(),
                    cdKeyNormalized)) {
                // urn normalizzato già presente su sistema
                throw new ParerInternalError("Il numero normalizzato per l'unità documentaria "
                        + MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave) + " della struttura "
                        + versatore.getEnte() + "/" + versatore.getStruttura() + " è già presente");
            } else {
                // cd key normalized (se calcolato)
                if (StringUtils.isBlank(aroUnitaDoc.getCdKeyUnitaDocNormaliz())) {
                    aroUnitaDoc.setCdKeyUnitaDocNormaliz(cdKeyNormalized);
                }
            }
        }

        // 2. verifica pregresso
        // A. check data massima versamento recuperata in precedenza rispetto parametro
        // su db
        if (!aroVDtVersMaxByUd.getDtVersMax().after(dataInizio)) {
            // B. eseguo registra urn comp pregressi
            urnHelper.scriviUrnCompPreg(aroUnitaDoc, versatore, chiave);
            // C. eseguo registra urn sip pregressi
            // C.1. eseguo registra urn sip pregressi ud
            urnHelper.scriviUrnSipUdPreg(aroUnitaDoc, versatore, chiave);
            // C.2. eseguo registra urn sip pregressi documenti aggiunti
            urnHelper.scriviUrnSipDocAggPreg(aroUnitaDoc, versatore, chiave);
            // C.3. eseguo registra urn pregressi upd
            urnHelper.scriviUrnSipUpdPreg(aroUnitaDoc, versatore, chiave);
        }
    }

    public CSChiave getChiaveUd(AroUnitaDoc ud) {
        CSChiave csc = new CSChiave();
        csc.setTipoRegistro(ud.getCdRegistroKeyUnitaDoc());
        csc.setAnno(ud.getAaKeyUnitaDoc().longValue());
        csc.setNumero(ud.getCdKeyUnitaDoc());

        return csc;
    }

    public CSVersatore getVersatoreUd(AroUnitaDoc ud, String sistemaConservazione) {
        CSVersatore csv = new CSVersatore();
        csv.setStruttura(ud.getOrgStrut().getNmStrut());
        csv.setEnte(ud.getOrgStrut().getOrgEnte().getNmEnte());
        csv.setAmbiente(ud.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
        // sistema (new URN)
        csv.setSistemaConservazione(sistemaConservazione);

        return csv;
    }

    @Override
    public JSONObject triggerFiltriUnitaDocumentarieDatiSpecNm_tipo_unita_docOnTrigger()
            throws EMFError {
        FiltriUnitaDocumentarieDatiSpec udfa = getForm().getFiltriUnitaDocumentarieDatiSpec();
        // Ricavo l'elenco dei tipi unitÃ documentaria PRIMA di modificare (aggiunta/rimozione) il
        // relativo filtro
        List<BigDecimal> tipoUnitaDocPre = udfa.getNm_tipo_unita_doc().parse();
        // Eseguo la post del filtri
        udfa.post(getRequest());
        // Ricavo l'elenco dei tipi unitÃ documentaria DOPO aver modificato (aggiunta/rimozione) il
        // relativo filtro
        List<BigDecimal> tipoUnitaDocPost = udfa.getNm_tipo_unita_doc().parse();
        // Confronto i due elenchi: se la lunghezza di tipoUnitaDocPre Ã¨ inferiore
        // a quella di tipoUnitaDocPost significa che ho fatto un'aggiunta
        boolean aggiunta = false;
        List<BigDecimal> elementoDiverso = (List<BigDecimal>) CollectionUtils
                .disjunction(tipoUnitaDocPre, tipoUnitaDocPost);
        if (tipoUnitaDocPre.size() < tipoUnitaDocPost.size()) {
            aggiunta = true;
        }

        // Ricavo la Lista Dati Specifici compilati a video
        List<DecCriterioDatiSpecBean> listaDatiSpecOnLine = (ArrayList) getSession()
                .getAttribute("listaDatiSpecOnLine") != null
                        ? (ArrayList) getSession().getAttribute("listaDatiSpecOnLine")
                        : new ArrayList();

        // HO FATTO UN'AGGIUNTA
        if (aggiunta) {
            // Ricavo i dati specifici di quel TIPO UNITA' DOCUMENTARIA aggiunto o tolto
            DecAttribDatiSpecTableBean datiSpecTB = udHelper
                    .getDecAttribDatiSpecTableBean(elementoDiverso.get(0), TipoEntitaSacer.UNI_DOC);
            aggiungiDatiSpecPerTipoUnitaDoc(datiSpecTB, listaDatiSpecOnLine);
        } // HO FATTO UNA RIMOZIONE
        else {
            // Ricavo il nome del TIPO UNITA' DOCUMENTARIA RIMOSSO
            String nmTipoUnitaDoc = udHelper.getDecTipoUnitaDocRowBean(elementoDiverso.get(0))
                    .getNmTipoUnitaDoc();

            // Per ogni DATO SPECIFICO di questo TIPO UNITA' DOCUMENTARIA
            // rimuovo il riferimento al tipo unitÃ documentaria
            for (DecCriterioDatiSpecBean datoSpec : listaDatiSpecOnLine) {
                List<DecCriterioAttribBean> tabellaDefinitoDa = datoSpec.getDecCriterioAttribs();
                for (int i = 0; i < tabellaDefinitoDa.size(); i++) {
                    if (tabellaDefinitoDa.get(i).getNmTipoUnitaDoc() != null && tabellaDefinitoDa
                            .get(i).getNmTipoUnitaDoc().equals(nmTipoUnitaDoc)) {
                        tabellaDefinitoDa.remove(i);
                    }
                }
            }

            // Controllo se ho ancora dati specifici per tutti i tipi unitÃ documentaria
            // e nel frattempo rimuovo gli eventuali dati specifici che non hanno piÃ¹ elementi
            // del tipo "definitoDa"
            boolean hasDSsuTipiUnitaDoc = false;
            Iterator it = listaDatiSpecOnLine.iterator();
            while (it.hasNext()) {
                DecCriterioDatiSpecBean datoSpec = (DecCriterioDatiSpecBean) it.next();
                List<DecCriterioAttribBean> tabellaDefinitoDa = datoSpec.getDecCriterioAttribs();
                if (tabellaDefinitoDa.isEmpty()) {
                    it.remove();
                } else {
                    for (DecCriterioAttribBean rigaDefinitoDa : tabellaDefinitoDa) {
                        if (rigaDefinitoDa.getNmTipoUnitaDoc() != null) {
                            hasDSsuTipiUnitaDoc = true;
                        }
                    }
                }
            }

            // Se non ho piÃ¹ dati specifici, tolgo la spunta alla CheckBox
            if (!hasDSsuTipiUnitaDoc) {
                getForm().getFiltriUnitaDocumentarieDatiSpec().getFlag_dati_spec_presenti_ud()
                        .setChecked(false);
            }

            // Aggiorno l'interfaccia online
            updateInterfacciaOnLineDatiSpec(listaDatiSpecOnLine, false);
        } // end ELSE

        if (tipoUnitaDocPost.size() == 1) {
            // Recupero le versioni XSD associate al tipo unità documentaria selezionato
            DecodeMap mappaVersioniXsd = new DecodeMap();
            mappaVersioniXsd.populatedMap(
                    datiSpecEjb.getXsdDatiSpecTableBeanByTipoEntita(
                            tipoUnitaDocPost.get(0).longValue(), TipoEntitaSacer.UNI_DOC),
                    "cd_versione_xsd", "cd_versione_xsd");
            getForm().getFiltriUnitaDocumentarieDatiSpec().getCd_versione_xsd_ud()
                    .setDecodeMap(mappaVersioniXsd);
        } else {
            getForm().getFiltriUnitaDocumentarieDatiSpec().getCd_versione_xsd_ud()
                    .setDecodeMap(new DecodeMap());
        }

        return getForm().getFiltriUnitaDocumentarieDatiSpec().asJSON();
    }

    @Override
    public JSONObject triggerFiltriUnitaDocumentarieDatiSpecNm_tipo_docOnTrigger() throws EMFError {
        FiltriUnitaDocumentarieDatiSpec udfa = getForm().getFiltriUnitaDocumentarieDatiSpec();
        // Ricavo l'elenco dei tipi documento PRIMA di modificare (aggiunta/rimozione) il relativo
        // filtro
        List<BigDecimal> tipoDocPre = udfa.getNm_tipo_doc().parse();
        // Eseguo la post del filtri
        udfa.post(getRequest());
        // Ricavo l'elenco dei tipi documento DOPO aver modificato (aggiunta/rimozione) il relativo
        // filtro
        List<BigDecimal> tipoDocPost = udfa.getNm_tipo_doc().parse();
        // Confronto i due elenchi: se la lunghezza di tipoDocPre Ã¨ inferiore
        // a quella di tipoDocPost significa che ho fatto un'aggiunta
        boolean aggiunta = false;
        List<BigDecimal> elementoDiverso = (List<BigDecimal>) CollectionUtils
                .disjunction(tipoDocPre, tipoDocPost);
        if (tipoDocPre.size() < tipoDocPost.size()) {
            aggiunta = true;
        }

        // Ricavo la Lista Dati Specifici compilati a video
        List<DecCriterioDatiSpecBean> listaDatiSpecOnLine = (ArrayList) getSession()
                .getAttribute("listaDatiSpecOnLine") != null
                        ? (ArrayList) getSession().getAttribute("listaDatiSpecOnLine")
                        : new ArrayList();

        // HO FATTO UN'AGGIUNTA
        if (aggiunta) {
            DecAttribDatiSpecTableBean datiSpecTB = udHelper
                    .getDecAttribDatiSpecTableBean(elementoDiverso.get(0), TipoEntitaSacer.DOC);
            aggiungiDatiSpecPerTipoDoc(datiSpecTB, listaDatiSpecOnLine);
        } // HO FATTO UNA RIMOZIONE
        else {
            // Ricavo il nome del tipo documento
            String nmTipoDoc = udHelper.getDecTipoDocRowBean(elementoDiverso.get(0)).getNmTipoDoc();

            // Per ogni DATO SPECIFICO di questo TIPO DOCUMENTO
            // rimuovo il riferimento al tipo documento
            for (DecCriterioDatiSpecBean datoSpec : listaDatiSpecOnLine) {
                List<DecCriterioAttribBean> tabellaDefinitoDa = datoSpec.getDecCriterioAttribs();
                for (int i = 0; i < tabellaDefinitoDa.size(); i++) {
                    if (tabellaDefinitoDa.get(i).getNmTipoDoc() != null
                            && tabellaDefinitoDa.get(i).getNmTipoDoc().equals(nmTipoDoc)) {
                        tabellaDefinitoDa.remove(i);
                    }
                }
            }

            // Controllo se ho ancora dati specifici per tutti i tipi documento
            boolean hasDSsuTipiDoc = false;
            Iterator it = listaDatiSpecOnLine.iterator();
            while (it.hasNext()) {
                DecCriterioDatiSpecBean datoSpec = (DecCriterioDatiSpecBean) it.next();
                List<DecCriterioAttribBean> tabellaDefinitoDa = datoSpec.getDecCriterioAttribs();
                if (tabellaDefinitoDa.isEmpty()) {
                    it.remove();
                } else {
                    for (DecCriterioAttribBean rigaDefinitoDa : tabellaDefinitoDa) {
                        if (rigaDefinitoDa.getNmTipoDoc() != null) {
                            hasDSsuTipiDoc = true;
                        }
                    }
                }
            }

            if (!hasDSsuTipiDoc) {
                getForm().getFiltriUnitaDocumentarieDatiSpec().getFlag_dati_spec_presenti_doc()
                        .setChecked(false);
            }

            // Aggiorno l'interfaccia online
            updateInterfacciaOnLineDatiSpec(listaDatiSpecOnLine, false);
        } // end ELSE

        if (tipoDocPost.size() == 1) {
            // Recupero le versioni XSD associate al tipo documento selezionato
            DecodeMap mappaVersioniXsd = new DecodeMap();
            mappaVersioniXsd
                    .populatedMap(
                            datiSpecEjb.getXsdDatiSpecTableBeanByTipoEntita(
                                    tipoDocPost.get(0).longValue(), TipoEntitaSacer.DOC),
                            "cd_versione_xsd", "cd_versione_xsd");
            getForm().getFiltriUnitaDocumentarieDatiSpec().getCd_versione_xsd_doc()
                    .setDecodeMap(mappaVersioniXsd);

            // Gestione Elemento
            // getForm().getFiltriUnitaDocumentarieDatiSpec().getTi_doc()
            // .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_doc",
            // Constants.TiDoc.values()));
        } else {
            getForm().getFiltriUnitaDocumentarieDatiSpec().getCd_versione_xsd_doc()
                    .setDecodeMap(new DecodeMap());
            // getForm().getFiltriUnitaDocumentarieDatiSpec().getTi_doc().setDecodeMap(new
            // DecodeMap());
        }

        return getForm().getFiltriUnitaDocumentarieDatiSpec().asJSON();
    }

    // <editor-fold defaultstate="collapsed" desc="GESTIONE CANCELLAZIONI">
    /**
     * Metodo di inizializzazione pagina di ricerca data mart
     *
     * @throws EMFError errore generico
     */
    @Secure(action = "Menu.Amministrazione.RicercaCancellazioni")
    public void ricercaDataMartPage() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Amministrazione.RicercaCancellazioni");
        getSession().setAttribute(UnitaDocAttributes.TIPORICERCA.name(),
                TipoRicercaAttribute.DATA_MART.name());
        // Pulisco i filtri di ricerca data mart
        getForm().getFiltriRicercaDataMart().reset();

        // Imposto i filtri in edit mode
        getForm().getFiltriRicercaDataMart().setEditMode();
        getForm().getMicroservizioDataMartFields().setEditMode();

        // Setto le varie combo dei filtri di ricerca
        getForm().getFiltriRicercaDataMart().getTi_mot_cancellazione()
                .setDecodeMap(ComboGetter.getMappaTiMotCancellazione());
        getForm().getFiltriRicercaDataMart().getTi_stato_richiesta()
                .setDecodeMap(ComboGetter.getMappaTiStatoRichiesta());
        getForm().getFiltriRicercaDataMart().getId_ente().setDecodeMap(
                DecodeMap.Factory.newInstance(dataMartEjb.getEntiDataMart(), "id_ente", "nm_ente"));
        getForm().getFiltriRicercaDataMart().getId_strut().setDecodeMap(new DecodeMap());

        // Inizializzo la liste data mart vuote e le section nascoste
        getForm().getRichiesteDataMartSection().setHidden(true);
        getForm().getNumUdDataMartSection().setHidden(true);
        getForm().getUdDataMartSection().setHidden(true);
        getForm().getRichiesteDataMartList().setTable(null);
        getForm().getNumUdDataMartList().setTable(null);
        getForm().getUdDataMartList().setTable(null);

        // Nascondo i pulsanti per la cancellazione logica e fisica
        getForm().getMicroservizioDataMartFields().getCallMicroservizioDataMart().setViewMode();
        getForm().getMicroservizioDataMartFields().getRecupCancellazioneLogicaDataMart()
                .setViewMode();
        getForm().getMicroservizioDataMartFields().getEseguiCancellazioneDataMart().setViewMode();

        // Carico la pagina di ricerca
        forwardToPublisher(Application.Publisher.RICERCA_DATA_MART);
    }

    @Override
    public void ricercaDataMart() throws EMFError {
        getForm().getMicroservizioDataMartFields().getCallMicroservizioDataMart().setViewMode();
        getForm().getMicroservizioDataMartFields().getRecupCancellazioneLogicaDataMart()
                .setViewMode();
        getForm().getMicroservizioDataMartFields().getEseguiCancellazioneDataMart().setViewMode();

        UnitaDocumentarieForm.FiltriRicercaDataMart filtriDataMart = getForm()
                .getFiltriRicercaDataMart();
        filtriDataMart.post(getRequest());

        if (filtriDataMart.validate(getMessageBox())) {
            if (!isValidaLogicaChiaveUnitaDoc(filtriDataMart.getCd_registro_key_unita_doc().parse(),
                    filtriDataMart.getAa_key_unita_doc().parse(),
                    filtriDataMart.getCd_key_unita_doc().parse())) {
                getMessageBox().setViewMode(MessageBox.ViewMode.alert);
                getMessageBox().addError(
                        "Errore di validazione: se si valorizza uno dei filtri della chiave ud (registro, anno e numero) devono essere valorizzati tutti");
            }

            if (!getMessageBox().hasError()) {
                getForm().getRichiesteDataMartSection().setHidden(false);
                DmUdDelRichiesteTableBean richiesteDataMart = dataMartEjb
                        .getDmUdDelRichiesteTableBean(filtriDataMart);

                getForm().getRichiesteDataMartList().setTable(richiesteDataMart);
                getForm().getRichiesteDataMartList().getTable().setPageSize(10);
                getForm().getRichiesteDataMartList().getTable().first();

                getForm().getNumUdDataMartSection().setHidden(true);
                getForm().getUdDataMartSection().setHidden(true);

                if (richiesteDataMart.size() == 1) {
                    BigDecimal idUdDelRichiesta = richiesteDataMart.getRow(0).getIdUdDelRichiesta();
                    String tiMotCancellazione = richiesteDataMart.getRow(0).getTiMotCancellazione();
                    // Unica chiamata al metodo "regista"
                    preparaAttributiPerJSP(idUdDelRichiesta, tiMotCancellazione);
                }
            }
        }
        forwardToPublisher(Application.Publisher.RICERCA_DATA_MART);
    }

    /**
     * Carica le liste del 2° e 3° riquadro sulla base della selezione di una riga nella lista
     * principale
     *
     * @throws EMFError errore generico
     */
    public void caricaNumUdDataMartList() throws EMFError {
        try {
            String idUdDelRichiestaStr = getRequest().getParameter("idUdDelRichiesta");
            String clickedMotivoR = getRequest().getParameter("selectedRigaMotivoR");
            String rigaCliccataR = getRequest().getParameter("indiceRigaCliccata");
            BigDecimal idUdDelRichiesta = new BigDecimal(idUdDelRichiestaStr);
            Integer rigaCliccata = Integer.valueOf(rigaCliccataR);

            // Recupero dal DB il record di RichiesteDataMartList sul quale ho cliccato per
            // "aggiornarlo", nel caso in cui nel frattempo sia cambiato lo stato della richiesta
            // (da DA_EVADERE ad EVASA)
            DmUdDelRichiesteRowBean richiesta = dataMartEjb
                    .getDmUdDelRichiesteRowBean(idUdDelRichiesta);
            getForm().getRichiesteDataMartList().getTable().getRow(rigaCliccata)
                    .copyFromBaseRow(richiesta);

            // Unica chiamata al metodo "regista" che fa tutto il lavoro.
            preparaAttributiPerJSP(idUdDelRichiesta, clickedMotivoR);

            getRequest().setAttribute("selectedRigaStatoN", null);
            getRequest().setAttribute("selectedRigaIdForN", null);

        } catch (Exception ex) {
            getMessageBox().addError(
                    "Errore durante il caricamento delle liste unità documentarie data mart");
            forwardToPublisher(getLastPublisher());
        }
        forwardToPublisher(Application.Publisher.RICERCA_DATA_MART);
    }

    /**
     * Carica la lista del 3° riquadro sulla base della selezione di una riga nella lista del 2°
     * riquadro, mantenendo lo stato del monitoraggio.
     *
     * @throws EMFError errore generico
     */
    public void caricaUdDataMartList() throws EMFError {
        // Recupero i parametri dalla richiesta
        String idUdDelRichiesta = getRequest().getParameter("idUdDelRichiesta");
        String idRichiesta = getRequest().getParameter("idRichiesta");
        String tiStatoUdCancellate = getRequest().getParameter("tiStatoUdCancellate");

        // Dobbiamo recuperare anche il motivo per passarlo al nostro helper
        String tiMotCancellazione = getRequest().getParameter("tiMotCancellazione");

        // Identificatori per mantenere la selezione della riga padre
        String parentSelectedIdR = getRequest().getParameter("selectedRigaIdR");
        String parentSelectedMotivoR = getRequest().getParameter("selectedRigaMotivoR");

        // Identificatori della riga figlia cliccata
        String clickedStatoN = getRequest().getParameter("selectedRigaStatoN");
        String clickedIdForN = getRequest().getParameter("selectedRigaIdForN");

        // Esegue la logica di business per filtrare la tabella UdDataMartList
        getForm().getUdDataMartSection()
                .setLegend("Lista unità documentarie in stato " + tiStatoUdCancellate
                        + " per richiesta " + idRichiesta + " di "
                        + dataMartEjb.getDsMotCancellazione(tiMotCancellazione));
        DmUdDelTableBean udDataMart = dataMartEjb
                .getDmUdDelTableBeanByStato(new BigDecimal(idUdDelRichiesta), tiStatoUdCancellate);
        getForm().getUdDataMartList().setTable(udDataMart);
        getForm().getUdDataMartList().getTable().setPageSize(10);
        getForm().getUdDataMartList().getTable().first();

        // Unica chiamata al metodo "regista" che fa tutto il lavoro.
        preparaAttributiPerJSP(new BigDecimal(parentSelectedIdR), parentSelectedMotivoR);

        // Reimposta gli attributi per l'evidenziazione della riga figlia, ora che
        // quelli della padre sono stati ripristinati.
        if (clickedStatoN != null) {
            getRequest().setAttribute("selectedRigaStatoN", clickedStatoN);
        }
        if (clickedIdForN != null) {
            getRequest().setAttribute("selectedRigaIdForN", clickedIdForN);
        }

        forwardToPublisher(Application.Publisher.RICERCA_DATA_MART);
    }

    /**
     * Classe POJO (Plain Old Java Object) per mantenere lo stato di visibilità dei pulsanti. L'uso
     * di questa classe rende il codice più leggibile rispetto a un array di booleani.
     */
    public static class ButtonVisibilityState {
        private final boolean microserviceButtonVisible;
        private final boolean resumeButtonVisible;
        private final boolean deletionButtonVisible;

        public ButtonVisibilityState(boolean microserviceButtonVisible,
                boolean deletionButtonVisible, boolean resumeButtonVisible) {
            this.microserviceButtonVisible = microserviceButtonVisible;
            this.resumeButtonVisible = resumeButtonVisible;
            this.deletionButtonVisible = deletionButtonVisible;
        }

        public boolean isMicroserviceButtonVisible() {
            return microserviceButtonVisible;
        }

        public boolean isResumeButtonVisible() {
            return resumeButtonVisible;
        }

        public boolean isDeletionButtonVisible() {
            return deletionButtonVisible;
        }

        /**
         * Metodo opzionale per la retrocompatibilità, se altre parti del codice si aspettano ancora
         * di ricevere un array di booleani.
         *
         * @return Un array booleano con lo stato dei pulsanti.
         */
        public boolean[] toArray() {
            return new boolean[] {
                    this.microserviceButtonVisible, this.deletionButtonVisible,
                    this.resumeButtonVisible };
        }
    }

    private ButtonVisibilityState checkDataMartButtonVisibility(BigDecimal idUdDelRichiesta) {

        // 1. Recupera tutti i dati necessari in una sola volta.
        String statoRichiesta = dataMartEjb.getStatoRichiesta(idUdDelRichiesta);

        // GUARD CLAUSE: Gestisci subito il caso terminale e esci.
        // Se la richiesta è evasa, nessun pulsante è visibile.
        if (CostantiDB.TiStatoRichiesta.EVASA.name().equals(statoRichiesta)) {
            updateButtonUI(false, false, false);
            return new ButtonVisibilityState(false, false, false);
        }

        // 2. Se non siamo in uno stato terminale, procedi con la logica dello stato interno.
        String statoInterno = dataMartEjb.getStatoInternoRichiesta(idUdDelRichiesta);
        CostantiDB.TiStatoInternoRich statoInternoEnum = CostantiDB.TiStatoInternoRich
                .valueOf(statoInterno);

        boolean isMicroserviceButtonVisible = false;
        boolean isDeletionButtonVisible = false;
        boolean isRiprendiLogicaButtonVisible = false;

        // 3. Lo switch ora gestisce solo i casi "positivi" (quando mostrare un pulsante).
        // I flag sono già false di default.
        switch (statoInternoEnum) {
        case INIZIALE:
        case ERRORE_INVIO_MS:
        case ERRORE_LOGICO:
            isMicroserviceButtonVisible = true;
            break;

        case ERRORE_LOGICO_RIPRISTINABILE:
            isRiprendiLogicaButtonVisible = true;
            break;

        case PRONTA_PER_FISICA:
        case ERRORE_PREPARAZIONE:
        case ERRORE_FISICO_CRITICO:
        case ERRORE_FISICO_PARZIALE:
            isDeletionButtonVisible = true;
            break;

        // Non serve più un default per impostare i flag a false,
        // perché quella è la loro condizione iniziale.
        // I casi "in corso" (es. IN_ELABORAZIONE_LOGICA) non faranno nulla,
        // lasciando correttamente i flag a false.
        }

        // 4. Aggiorna la UI e ritorna lo stato calcolato.
        updateButtonUI(isMicroserviceButtonVisible, isDeletionButtonVisible,
                isRiprendiLogicaButtonVisible);
        return new ButtonVisibilityState(isMicroserviceButtonVisible, isDeletionButtonVisible,
                isRiprendiLogicaButtonVisible);
    }

    /**
     * Aggiorna la modalità (Edit/View) dei pulsanti nell'interfaccia utente.
     *
     * @param isMicroserviceVisible Se il pulsante del microservizio deve essere in modalità edit.
     * @param isDeletionVisible     Se il pulsante di cancellazione deve essere in modalità edit.
     */
    private void updateButtonUI(boolean isMicroserviceVisible, boolean isDeletionVisible,
            boolean isRiprendiLogicaVisible) {
        var microservizioFields = getForm().getMicroservizioDataMartFields();

        if (isMicroserviceVisible) {
            microservizioFields.getCallMicroservizioDataMart().setEditMode();
        } else {
            microservizioFields.getCallMicroservizioDataMart().setViewMode();
        }

        if (isDeletionVisible) {
            microservizioFields.getEseguiCancellazioneDataMart().setEditMode();
        } else {
            microservizioFields.getEseguiCancellazioneDataMart().setViewMode();
        }

        if (isRiprendiLogicaVisible) {
            getForm().getMicroservizioDataMartFields().getRecupCancellazioneLogicaDataMart()
                    .setEditMode();
        } else {
            getForm().getMicroservizioDataMartFields().getRecupCancellazioneLogicaDataMart()
                    .setViewMode();
        }
    }

    @Override
    public JSONObject triggerFiltriRicercaDataMartId_enteOnTrigger() throws EMFError {
        getForm().getFiltriRicercaDataMart().post(getRequest());
        if (getForm().getFiltriRicercaDataMart().getId_ente().parse() != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb.getOrgStrutTableBean(
                    getUser().getIdUtente(),
                    getForm().getFiltriRicercaDataMart().getId_ente().parse(), Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");
            getForm().getFiltriRicercaDataMart().getId_strut().setDecodeMap(mappaStrut);
            // Se ho una sola struttura la setto già impostata nella combo
            if (tmpTableBeanStrut.size() == 1) {
                getForm().getFiltriRicercaDataMart().getId_strut()
                        .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
            }
        } else {
            getForm().getFiltriRicercaDataMart().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaDataMart().getCd_registro_key_unita_doc()
                    .setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriRicercaDataMart().asJSON();
    }

    @Override
    public JSONObject triggerFiltriRicercaDataMartId_strutOnTrigger() throws EMFError {
        getForm().getFiltriRicercaDataMart().post(getRequest());
        if (getForm().getFiltriRicercaDataMart().getId_strut().parse() != null) {
            // Setto i valori della mappa TIPO REGISTRO ricavati dalla tabella
            // DEC_REGISTRO_UNITA_DOC
            DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb.getRegistriUnitaDocAbilitati(
                    getUser().getIdUtente(),
                    getForm().getFiltriRicercaDataMart().getId_strut().parse());
            DecodeMap mappaRegistro = new DecodeMap();
            mappaRegistro.populatedMap(tmpTableBeanReg, "cd_registro_unita_doc",
                    "cd_registro_unita_doc");
            getForm().getFiltriRicercaDataMart().getCd_registro_key_unita_doc()
                    .setDecodeMap(mappaRegistro);
        } else {
            getForm().getFiltriRicercaDataMart().getCd_registro_key_unita_doc()
                    .setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriRicercaDataMart().asJSON();
    }

    /**
     * Controlla se i tre campi chiave dell'unità documentale sono tutti valorizzati o tutti non
     * valorizzati. Restituisce true se la condizione è rispettata, false altrimenti.
     *
     * Per "valorizzato" si intende non nullo e non una stringa vuota (dopo il trim).
     *
     * @param cdRegistroKeyUnitaDoc Il codice del registro.
     * @param aaKeyUnitaDoc         L'anno chiave.
     * @param cdKeyUnitaDoc         Il codice chiave.
     *
     * @return true se la validazione passa, false altrimenti.
     */
    public static boolean isValidaLogicaChiaveUnitaDoc(String cdRegistroKeyUnitaDoc,
            BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc) {
        // Normalizziamo "valorizzato" come non nullo e non vuoto dopo il trim
        boolean cdRegistroValorizzato = cdRegistroKeyUnitaDoc != null
                && !cdRegistroKeyUnitaDoc.trim().isEmpty();
        boolean aaKeyValorizzato = aaKeyUnitaDoc != null;
        boolean cdKeyValorizzato = cdKeyUnitaDoc != null && !cdKeyUnitaDoc.trim().isEmpty();

        // Contiamo quanti sono valorizzati
        int countValorizzati = 0;
        if (cdRegistroValorizzato) {
            countValorizzati++;
        }
        if (aaKeyValorizzato) {
            countValorizzati++;
        }
        if (cdKeyValorizzato) {
            countValorizzati++;
        }

        // La logica è: o tutti e tre sono valorizzati (count = 3)
        // o nessuno dei tre è valorizzato (count = 0).
        // Qualsiasi altra combinazione (1 o 2 valorizzati) non è valida.
        return countValorizzati == 0 || countValorizzati == 3;
    }

    @Override
    public void callMicroservizioDataMart() throws EMFError {
        eseguiMicroservizioDataMart();
    }

    public void eseguiMicroservizioDataMart() throws EMFError {
        BigDecimal idUdDelRichiestaSacer = ((DmUdDelTableBean) getForm().getNumUdDataMartList()
                .getTable()).getRow(0).getIdUdDelRichiesta();
        BigDecimal idRichiestaSacer = getForm().getNumUdDataMartList().getTable().getRow(0)
                .getBigDecimal("id_richiesta");
        BigDecimal idStrut = ((DmUdDelTableBean) getForm().getNumUdDataMartList().getTable())
                .getRow(0).getIdStrut();
        String tiMotCancellazione = getForm().getNumUdDataMartList().getTable().getRow(0)
                .getString("ti_mot_cancellazione");

        try {
            // Logica di business per chiamare il microservizio
            String tipoRichiesta = TipoRichiesta.valueOf(tiMotCancellazione).getDescrizione();
            String[] organizzazione = dataMartEjb.getAmbienteEnteStruttura(idStrut);
            String ambiente = organizzazione[0];
            String ente = organizzazione[1];
            String struttura = organizzazione[2];
            String motivazione = CostantiDB.TiMotCancellazione.valueOf(tiMotCancellazione)
                    .getDescrizione();
            String descrizione = "Cancellazione UD a seguito di " + motivazione;

            String versione = configurationHelper.getAplValoreParamApplic(
                    CostantiDB.ParametroAppl.VERSIONE_XML_MS_UD_DEL,
                    AplValoreParamApplic.TiAppart.APPLIC.name(), null, null, null, null);
            String loginname = configurationHelper.getAplValoreParamApplic(
                    CostantiDB.ParametroAppl.USERID_MS_UD_DEL,
                    AplValoreParamApplic.TiAppart.APPLIC.name(), null, null, null, null);
            String password = configurationHelper.getAplValoreParamApplic(
                    CostantiDB.ParametroAppl.PSW_MS_UD_DEL,
                    AplValoreParamApplic.TiAppart.APPLIC.name(), null, null, null, null);
            Integer timeout = Integer.valueOf(configurationHelper.getAplValoreParamApplic(
                    CostantiDB.ParametroAppl.TIMEOUT_MS_UD_DEL,
                    AplValoreParamApplic.TiAppart.APPLIC.name(), null, null, null, null));
            String url = configurationHelper.getAplValoreParamApplic(
                    CostantiDB.ParametroAppl.URL_MS_UD_DEL,
                    AplValoreParamApplic.TiAppart.APPLIC.name(), null, null, null, null);
            String tiModDel = dataMartHelper.getTiModDelRichiesta(idUdDelRichiestaSacer);

            HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            clientHttpRequestFactory.setConnectTimeout(timeout);
            RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);

            dataMartEjb.deleteAroRichSoftDelete(idRichiestaSacer,
                    dataMartEjb.getTiItemRichSoftDelete(tiMotCancellazione));

            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
            multipartRequest.add("VERSIONE", versione);
            multipartRequest.add("LOGINNAME", loginname);
            multipartRequest.add("PASSWORD", password);
            multipartRequest.add("XMLREQ",
                    getXmlMsDmUdDel(versione, loginname, ambiente, ente, struttura, descrizione,
                            motivazione, tiModDel, tipoRichiesta, idRichiestaSacer));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(
                    multipartRequest, header);

            restTemplate.exchange(url, HttpMethod.POST, requestEntity, Resource.class);

            // Aggiorniamo lo stato a INVIATA_A_MS
            dataMartEjb.impostaStatoInternoRichiesta(idUdDelRichiestaSacer,
                    CostantiDB.TiStatoInternoRich.INVIATA_A_MS.name());
            getMessageBox().addInfo("Microservizio invocato con successo.");

        } catch (Exception e) {
            // Aggiorniamo lo stato a ERRORE_INVIO_MS
            dataMartEjb.impostaStatoInternoRichiesta(idUdDelRichiestaSacer,
                    CostantiDB.TiStatoInternoRich.ERRORE_INVIO_MS.name());
            log.error(
                    "Errore grave durante l'invocazione del microservizio di cancellazione logica",
                    e);
            getMessageBox().addError(
                    "Si è verificato un errore durante la chiamata al microservizio. Controllare i log.");
        }

        ricaricaPaginaDataMart(idUdDelRichiestaSacer, tiMotCancellazione);
    }

    @Override
    public void recupCancellazioneLogicaDataMart() throws EMFError {
        BigDecimal idUdDelRichiesta = ((DmUdDelTableBean) getForm().getNumUdDataMartList()
                .getTable()).getRow(0).getIdUdDelRichiesta();
        BigDecimal idRichiesta = ((DmUdDelTableBean) getForm().getNumUdDataMartList().getTable())
                .getRow(0).getBigDecimal("id_richiesta");
        String tiMotCancellazione = getForm().getNumUdDataMartList().getTable().getRow(0)
                .getString("ti_mot_cancellazione");

        try {
            log.info("Ripresa della cancellazione logica per la richiesta {}", idUdDelRichiesta);

            // PASSO 1: ESEGUI LE OPERAZIONI DI CORREZIONE SUL DB
            dataMartEjb.eseguiCorrezionePerRipresaLogica(idRichiesta,
                    dataMartEjb.getTiItemRichSoftDelete(tiMotCancellazione),
                    getUser().getIdUtente());

            // PASSO 2: "RIARMA" LO STATO
            // Dopo la correzione, va riportato lo stato a uno stato "attivo"
            // in modo che il polling possa ripartire. "INVIATA_A_MS" è adeguato.
            dataMartEjb.impostaStatoInternoRichiesta(idUdDelRichiesta,
                    CostantiDB.TiStatoInternoRich.INVIATA_A_MS.name());

            getMessageBox().addInfo(
                    "Operazioni di correzione completate. Il monitoraggio riprenderà a breve.");

        } catch (Exception e) {
            log.error("Errore durante la ripresa della cancellazione logica", e);
            // Se la correzione fallisce, rimaniamo nello stato di errore ripristinabile
            // e mostriamo un messaggio all'utente.
            dataMartEjb.impostaStatoInternoRichiesta(idUdDelRichiesta,
                    CostantiDB.TiStatoInternoRich.ERRORE_LOGICO_RIPRISTINABILE.name(),
                    e.getMessage());
            getMessageBox().addError("Impossibile eseguire le operazioni di correzione.");
        }

        // Ricarica la pagina per far ripartire il polling.
        ricaricaPaginaDataMart(idUdDelRichiesta, tiMotCancellazione);
    }

    private String getXmlMsDmUdDel(String versione, String nmUserid, String ambiente, String ente,
            String struttura, String descrizione, String motivazione, String tipoCancellazione,
            String tipoRichiesta, BigDecimal idRichiestaSacer) {

        StringBuilder xmlMicroservizio = new StringBuilder();
        // Tag di apertura
        xmlMicroservizio.append("<RichiestaCancellazioneLogica>");
        // Inserisco i tag che identificano la richiesta
        xmlMicroservizio.append("<VersioneXmlRichiesta>").append(versione)
                .append("</VersioneXmlRichiesta><Versatore><Ambiente>").append(ambiente)
                .append("</Ambiente><Ente>").append(ente).append("</Ente><Struttura>")
                .append(struttura).append("</Struttura><UserID>").append(nmUserid)
                .append("</UserID></Versatore><Richiesta><Descrizione>").append(descrizione)
                .append("</Descrizione><Motivazione>").append(motivazione)
                .append("</Motivazione><TipoCancellazione>").append(tipoCancellazione)
                .append("</TipoCancellazione></Richiesta>");
        // Richiesta di cancellazione
        xmlMicroservizio
                .append("<RichiesteDiCancellazione><RichiestaDiCancellazione><TipoRichiesta>");
        xmlMicroservizio.append(tipoRichiesta).append("</TipoRichiesta><IDRichiestaSacer>");
        xmlMicroservizio.append(idRichiestaSacer);
        xmlMicroservizio.append(
                "</IDRichiestaSacer></RichiestaDiCancellazione></RichiesteDiCancellazione>");
        // Tag chiusura
        xmlMicroservizio.append("</RichiestaCancellazioneLogica>");

        return xmlMicroservizio.toString();
    }

    @Override
    public void eseguiCancellazioneDataMart() throws EMFError {
        BigDecimal idUdDelRichiesta = ((DmUdDelTableBean) getForm().getNumUdDataMartList()
                .getTable()).getRow(0).getIdUdDelRichiesta();
        String tiMotCancellazione = getForm().getNumUdDataMartList().getTable().getRow(0)
                .getString("ti_mot_cancellazione");

        try {
            String statoInterno = dataMartEjb.getStatoInternoRichiesta(idUdDelRichiesta);

            // Definisce gli stati in cui è necessario eseguire la PREPARAZIONE da capo
            List<String> statiPerPreparazioneCompleta = List.of(
                    CostantiDB.TiStatoInternoRich.PRONTA_PER_FISICA.name(),
                    CostantiDB.TiStatoInternoRich.ERRORE_PREPARAZIONE.name());

            // Definisce gli stati in cui la preparazione è GIA' STATA FATTA e serve solo riavviare
            // il job
            List<String> statiPerRiavvioJob = List.of(
                    CostantiDB.TiStatoInternoRich.ERRORE_AVVIO_JOB.name(),
                    CostantiDB.TiStatoInternoRich.ERRORE_FISICO_CRITICO.name(),
                    CostantiDB.TiStatoInternoRich.ERRORE_FISICO_PARZIALE.name());

            if (statiPerPreparazioneCompleta.contains(statoInterno)) {
                log.info("Avvio preparazione completa per la richiesta {} (stato attuale: {})",
                        idUdDelRichiesta, statoInterno);
                dataMartEjb.preparaEAvviaCancellazioneFisica(idUdDelRichiesta);
                getMessageBox()
                        .addInfo("Preparazione e avvio del processo completati con successo.");

            } else if (statiPerRiavvioJob.contains(statoInterno)) {
                log.info("Avvio 'retry' del solo job per la richiesta {} (stato attuale: {})",
                        idUdDelRichiesta, statoInterno);
                dataMartEjb.riavviaJobCancellazioneFisica(idUdDelRichiesta);
                getMessageBox()
                        .addInfo("Riavvio del processo di cancellazione richiesto con successo.");

            } else {
                getMessageBox()
                        .addWarning("Azione non permessa per lo stato attuale: " + statoInterno);
            }

        } catch (Exception e) {
            // Determina se il fallimento è avvenuto durante la preparazione o l'avvio del job
            int index = org.apache.commons.lang3.exception.ExceptionUtils.indexOfThrowable(e,
                    PreparazioneFisicaException.class);

            if (index != -1) {
                // È un nostro errore gestito
                Throwable causaSpecifica = org.apache.commons.lang3.exception.ExceptionUtils
                        .getThrowableList(e).get(index);
                Throwable causaOriginale = causaSpecifica.getCause();
                String messaggioErrore = (causaOriginale != null) ? causaOriginale.getMessage()
                        : causaSpecifica.getMessage();

                // Distinguiamo se l'errore è avvenuto durante la preparazione o il riavvio
                if (causaSpecifica.getMessage().contains("preparazione")) {
                    log.error("Fallimento durante la preparazione per la richiesta {}",
                            idUdDelRichiesta, e);
                    dataMartEjb.impostaStatoInternoRichiesta(idUdDelRichiesta,
                            CostantiDB.TiStatoInternoRich.ERRORE_PREPARAZIONE.name(),
                            messaggioErrore);
                    getMessageBox().addError(
                            "Errore durante la preparazione. Operazione annullata. Riprovare.");
                } else { // Errore durante il riavvio
                    log.error("Fallimento durante il riavvio del job per la richiesta {}",
                            idUdDelRichiesta, e);
                    dataMartEjb.impostaStatoInternoRichiesta(idUdDelRichiesta,
                            CostantiDB.TiStatoInternoRich.ERRORE_AVVIO_JOB.name(), messaggioErrore);
                    getMessageBox().addError(
                            "Errore durante l'avvio del job di cancellazione. Riprovare.");
                }
            } else {
                // Errore non previsto
                log.error("Errore imprevisto durante la fase fisica per la richiesta {}",
                        idUdDelRichiesta, e);
                getMessageBox()
                        .addError("Si è verificato un errore imprevisto. Controllare i log.");
            }
        }

        ricaricaPaginaDataMart(idUdDelRichiesta, tiMotCancellazione);
    }

    private void ricaricaPaginaDataMart(BigDecimal idUdDelRichiesta, String tiMotCancellazione)
            throws EMFError {
        preparaAttributiPerJSP(idUdDelRichiesta, tiMotCancellazione);
        forwardToPublisher(getLastPublisher());
    }

    /**
     * Centralizza tutta la preparazione della vista di dettaglio.
     */
    private void preparaAttributiPerJSP(BigDecimal idUdDelRichiesta, String tiMotCancellazione)
            throws EMFError {
        CostantiDB.TiMotCancellazione tiMotCancellazioneEnum = CostantiDB.TiMotCancellazione
                .valueOf(tiMotCancellazione);

        // --- 1. SINCRONIZZAZIONE DELLO STATO (Auto-riparazione) ---
        // Determina il tipo di item per la query sulla vista
        String tiItemRichSoftDelete = dataMartEjb.getTiItemRichSoftDelete(tiMotCancellazione);

        // Ora che lo stato è sicuramente aggiornato, lo leggiamo per passarlo alla JSP
        DmUdDelRichiesteRowBean richiesta = dataMartEjb
                .getDmUdDelRichiesteForPollingRowBean(idUdDelRichiesta);

        // Chiama il metodo unificato EJB. Ignoriamo il DTO restituito,
        // ci interessa solo l'effetto collaterale della sincronizzazione.
        dataMartEjb.sincronizzaEcalcolaStatoLogico(idUdDelRichiesta, richiesta.getIdRichiesta(),
                tiItemRichSoftDelete);

        // --- 2. PASSAGGIO DATI ALLA JSP (per polling e selezione riga) ---
        getRequest().setAttribute("statoRichiestaSelezionata", richiesta.getTiStatoRichiesta());
        getRequest().setAttribute("statoInternoRichiesta", richiesta.getTiStatoInternoRich());
        getRequest().setAttribute("idUdDelRichiestaPerPolling", idUdDelRichiesta.toPlainString());
        getRequest().setAttribute("idRichiestaPerPolling", richiesta.getIdRichiesta());
        getRequest().setAttribute("selectedRigaIdR", idUdDelRichiesta.toPlainString());
        getRequest().setAttribute("selectedRigaMotivoR", tiMotCancellazione);

        // --- 3. LOGICA DI VISIBILITÀ DEI PULSANTI ---
        // Questa chiamata ora si basa sullo stato appena sincronizzato.
        checkDataMartButtonVisibility(idUdDelRichiesta);

        // --- 4. POPOLAMENTO DELLE TABELLE DI DETTAGLIO ---
        String dsMotCancellazione = tiMotCancellazioneEnum.getDescrizione();

        // Popola la tabella dei conteggi per stato (NumUdDataMartList)
        getForm().getNumUdDataMartSection().setHidden(false);
        getForm().getNumUdDataMartSection()
                .setLegend("Numero unità documentarie per la richiesta di " + dsMotCancellazione
                        + ": " + richiesta.getCdRichiesta());
        DmUdDelTableBean numUdDataMart = dataMartEjb.getDmUdDelGroupedByStato(idUdDelRichiesta);
        getForm().getNumUdDataMartList().setTable(numUdDataMart);
        getForm().getNumUdDataMartList().getTable().setPageSize(10);
        getForm().getNumUdDataMartList().getTable().first();

        String tiStatoUdCancellate = getRequest().getParameter("tiStatoUdCancellate");
        // Popola la tabella di dettaglio delle UD (UdDataMartList)
        getForm().getUdDataMartSection().setHidden(false);
        getForm().getUdDataMartSection().setLegend("Lista unità documentarie per richiesta di "
                + dsMotCancellazione + ": " + richiesta.getCdRichiesta());
        DmUdDelTableBean udDataMart = dataMartEjb.getDmUdDelTableBeanByStato(idUdDelRichiesta,
                tiStatoUdCancellate);
        getForm().getUdDataMartList().setTable(udDataMart);
        getForm().getUdDataMartList().getTable().setPageSize(10);
        getForm().getUdDataMartList().getTable().first();
    }

    // </editor-fold>
}
