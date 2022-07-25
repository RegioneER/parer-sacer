/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.utils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Stream;

import it.eng.parer.ws.utils.CostantiDB.ReportvfZipVersion;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;

/**
 *
 * @author Fioravanti_F
 */
public class CostantiDB {

    //
    public class DatiCablati {

        public static final String TPI_PATH_LISTA_FILE = "ListaFile";
        public static final String VERS_TIPO_CONSERVAZIONE_DEFAULT = "VERSAMENTO_ANTICIPATO";
    }

    //
    public class TipoDocumento {

        public static final String PRINCIPALE = "PRINCIPALE";
        public static final String ALLEGATO = "ALLEGATO";
        public static final String ANNESSO = "ANNESSO";
        public static final String ANNOTAZIONE = "ANNOTAZIONE";
    }

    //
    public class TipoParametroAppl {

        public static final String VERSAMENTO_DEFAULT = "Default di versamento";
        public static final String MAX_RESULT = "Paginazione risultati";
        public static final String PATH = "Gestione servizi asincroni";
        public static final String TPI = "Salvataggio su nastro";
        public static final String IMAGE = "Trasformazione componenti";
        public static final String LOG_APPLIC = "Log accessi";
        public static final String IAM = "Gestione utenti";
        // GG DELTA AIP
        public static final String PARERMT_INDICE_UD_DAELAB_NI_GG_MAX = "PARERMT_INDICE_UD_DAELAB_NI_GG_MAX";
        //
        public static final String VERSIONI_WS = "Versioni servizi";
    }

    //
    public class ParametroAppl {

        public static final String NM_APPLIC = "NM_APPLIC";

        public static final String TIPO_STRUT_DOC = "TIPO_STRUT_DOC";
        public static final String TIPO_COMP_DOC = "TIPO_COMP_DOC";
        public static final String TIPO_SUPPORTO_COMP = "TIPO_SUPPORTO";
        public static final String USO_DATA_FIRMA = "USO_DATA_FIRMA";
        public static final String VERIFICA_PARTIZIONI = "VERIFICA_PARTIZIONI";
        public static final String FMT_UNKNOWN = "FMT_UNKNOWN";
        public static final String SERVER_NAME_SYSTEM_PROPERTY = "SERVER_NAME_SYSTEM_PROPERTY";
        //
        public static final String PATH_MM_IN_ = "PATH_MM_IN_";
        public static final String PATH_MM_OUT_ = "PATH_MM_OUT_";
        //
        public static final String TPI_ROOT_SACER = "root_SACER";
        public static final String TPI_ROOT_TPI = "root_TPI";
        public static final String TPI_ROOT_VERS = "root_vers";
        public static final String TPI_ROOT_ARK_VERS = "root_ark_vers";
        public static final String TPI_ROOT_DA_MIGRARE = "root_da_migrare";
        public static final String TPI_ROOT_MIGRAZ = "root_migraz";
        public static final String TPI_ROOT_ARK_MIGRAZ = "root_ark_migraz";
        public static final String TPI_ROOT_RECUP = "root_recup";
        public static final String TPI_DATA_INIZIO_USO_BLOB = "dataInizioUsoBlob";
        public static final String TPI_DATA_FINE_USO_BLOB = "dataFineUsoBlob";
        public static final String TPI_NM_USER_TPI = "nmUserTPI";
        public static final String TPI_CD_PSW_TPI = "cdPswTPI";
        public static final String TPI_TPI_HOST_URL = "TPI_HostURL";
        public static final String TPI_URL_STATOARKCARTELLE = "URL_StatoArkCartelle";
        public static final String TPI_URL_ELIMINACARTELLAARK = "URL_EliminaCartellaArk";
        public static final String TPI_URL_RETRIEVEFILEUNITADOC = "URL_RetrieveFileUnitaDoc";
        public static final String TPI_URL_REGISTRACARTELLARIARK = "URL_RegistraCartellaRiArk";
        public static final String TPI_URL_SCHEDULAZIONIJOB = "URL_SchedulazioniJobTPI";
        public static final String TPI_NI_GG_MAX_MIGRAZ = "niGgMaxMigraz";
        public static final String TPI_TIMEOUT = "timeoutTPI";
        public static final String TPI_TIMEOUT_RETRIEVE = "timeoutTPIRetrieve";
        //
        // questi verranno rimossi quando la migrazione sarà completata
        public static final String TPI_NM_USER_MIG_BLB = "nmUserMigBlb";
        public static final String TPI_CD_PSW_MIG_BLB = "cdPswMigBlb";
        public static final String TPI_MIG_BLB_WS_URL = "MigBlb_WsURL";
        //
        public static final String TPI_DATA_INIZIO_CONTROLLO_NUM_FILE_ARK = "dataInizioControlloNumFileArk";
        //
        public static final String IMAGE_ROOT_IMAGE_TRASFORM = "root_image_trasform";

        // Costanti per indice AIP
        public static final String AGENT_PRESERVER_FORMALNAME = "AGENT_PRESERVER_FORMALNAME";
        public static final String AGENT_PRESERVER_TAXCODE = "AGENT_PRESERVER_TAXCODE";
        public static final String AGENT_PRESERVATION_MNGR_TAXCODE = "AGENT_PRESERVATION_MNGR_TAXCODE";
        public static final String AGENT_PRESERVATION_MNGR_LASTNAME = "AGENT_PRESERVATION_MNGR_LASTNAME";
        public static final String AGENT_PRESERVATION_MNGR_FIRSTNAME = "AGENT_PRESERVATION_MNGR_FIRSTNAME";
        public static final String AGENT_PRESERVATION_MNGR_USERNAME = "AGENT_PRESERVATION_MNGR_USERNAME";

        // EVO#20972
        // Costanti per indice AIP v2.0
        public static final String AGENT_SUBMITTER_FORMALNAME = "AGENT_SUBMITTER_FORMALNAME";
        public static final String AGENT_SUBMITTER_TAXCODE = "AGENT_SUBMITTER_TAXCODE";
        public static final String AGENT_AUTHORIZED_SIGNER_ROLE = "AGENT_AUTHORIZED_SIGNER_ROLE";
        // end EVO#20972
        // MEV#25903
        public static final String AGENT_HOLDER_RELEVANTDOCUMENT = "AGENT_HOLDER_RELEVANTDOCUMENT";
        public static final String AGENT_SUBMITTER_RELEVANTDOCUMENT = "AGENT_SUBMITTER_RELEVANTDOCUMENT";
        // end MEV#25903

        // Costanti per dati applicazione
        public static final String REG_ANNO_VALID_MINIMO = "REG_ANNO_VALID_MINIMO";
        // Costanti per scarto scadenza password utente
        public static final String NUM_GIORNI_ESPONI_SCAD_PSW = "NUM_GIORNI_ESPONI_SCAD_PSW";

        // Costanti per il log dei login ws e la disattivazione automatica utenti
        public static final String IDP_MAX_TENTATIVI_FALLITI = "MAX_TENTATIVI_FALLITI";
        public static final String IDP_MAX_GIORNI = "MAX_GIORNI";
        public static final String IDP_QRY_DISABLE_USER = "QRY_DISABLE_USER";
        public static final String IDP_QRY_VERIFICA_DISATTIVAZIONE_UTENTE = "QRY_VERIFICA_DISATTIVAZIONE_UTENTE";
        public static final String IDP_QRY_REGISTRA_EVENTO_UTENTE = "QRY_REGISTRA_EVENTO_UTENTE";

        // Costati per URN (SISTEMA)
        public static final String NM_SISTEMACONSERVAZIONE = "SISTEMA_CONSERVAZIONE";

        public static final String OBJECT_STORAGE_ADDR = "OBJECT_STORAGE_ADDR";

        public static final String VERSIONI_WS_PREFIX = "VERSIONI_";

        public static final String DATA_INIZIO_CALC_NUOVI_URN = "DATA_INIZIO_CALC_NUOVI_URN";

        // Report verifica firma su Object storage
        public static final String REPORTVF_S3_ACCESS_KEY_ID = "REPORTVF_S3_ACCESS_KEY_ID";
        public static final String REPORTVF_S3_SECRET_KEY = "REPORTVF_S3_SECRET_KEY";
        public static final String BUCKET_REPORT_VERIFICAFIRMA = "BUCKET_REPORT_VERIFICAFIRMA";

        // Costati per AIP
        public static final String UNISINCRO_VERSION = "UNISINCRO_VERSION";

    }

    //
    public class TipiXmlDati {

        public static final String INDICE_FILE = "INDICE_FILE";
        public static final String RICHIESTA = "RICHIESTA";
        public static final String RISPOSTA = "RISPOSTA";
        public static final String RAPP_VERS = "RAPP_VERS";
    }

    //
    public class TipoUsoComponente {

        public static final String CONTENUTO = "CONTENUTO";
        public static final String CONVERTITORE = "CONVERTITORE";
        public static final String FIRMA = "FIRMA";
        public static final String MARCA = "MARCA";
        public static final String RAPPRESENTAZIONE = "RAPPRESENTAZIONE";
        public static final String SEGNATURA = "SEGNATURA";
        public static final String FIRMA_ELETTRONICA = "FIRMA_ELETTRONICA";
    }

    public class SubStruttura {

        public static final String DEFAULT_NAME = "DEFAULT";
        public static final String DEFAULT_DESC = "Sub struttura di default per la struttura ";
        public static final String DEFAULT_TEMPLATE_DESC = "Sub struttura di default";
    }

    //
    public enum TipiUsoDatiSpec {

        MIGRAZ, VERS
    }

    public enum TiUsoModelloXsd {
        MIGRAZ, VERS
    }

    public enum TiModelloXsd {
        PROFILO_GENERALE_FASCICOLO, PROFILO_ARCHIVISTICO_FASCICOLO, PROFILO_SPECIFICO_FASCICOLO, FASCICOLO,
        AIP_SELF_DESCRIPTION_MORE_INFO,
        // FILE_GROUP_FILE_MORE_INFO,
        AIP_UNISYNCRO, PROFILO_NORMATIVO_FASCICOLO
    }

    public enum TipiStatoElementoVersato {

        IN_ATTESA_MEMORIZZAZIONE, IN_ATTESA_SCHED,
    }

    //
    public enum TipiEntitaSacer {

        UNI_DOC("Unit\u00E0 Documentaria"), DOC("Documento"), COMP("Componente"), SUB_COMP("Sottocomponente"),
        // MEV#26446
        FASC("Fascicolo");
        // end MEV#26446

        private String valore;

        private TipiEntitaSacer(String val) {
            this.valore = val;
        }

        public String descrivi() {
            return valore;
        }
    }

    public enum TipiEntitaRecupero {

        UNI_DOC("Unit\u00E0 Documentaria"), DOC("Documento"), COMP("Componente"), SUB_COMP("Sottocomponente"),
        //
        UNI_DOC_UNISYNCRO("Unit\u00E0 Documentaria UniSyncro"),
        //
        UNI_DOC_DIP("Unit\u00E0 Documentaria DIP"), DOC_DIP("Documento DIP"), COMP_DIP("Componente DIP"),
        //
        UNI_DOC_DIP_ESIBIZIONE("Unit\u00E0 Documentaria DIP per esibizione"),
        DOC_DIP_ESIBIZIONE("Documento DIP per esibizione"), COMP_DIP_ESIBIZIONE("Componente DIP per esibizione"),
        //
        REPORT_FIRMA("Report verifica firma"),
        // EVO#20972
        PROVE_CONSERV_AIPV2("Prove di conservazione"),
        //
        UNI_DOC_UNISYNCRO_V2("Unit\u00E0 Documentaria UniSyncro v2.0");
        // end EVO#20972

        private String valore;

        private TipiEntitaRecupero(String val) {
            this.valore = val;
        }

        public String descrivi() {
            return valore;
        }
    }

    public enum TipoSalvataggioFile {
        BLOB, FILE
    }

    public enum TipoCausaleNoMigraz {
        SUPPORTO_NON_FILE, FILE_NON_BLOB
    }

    public enum TipoCampo {

        DATO_PROFILO("Dato di profilo"), DATO_SPEC_DOC_PRINC("Dato specifico documento principale"),
        DATO_SPEC_UNI_DOC("Dato specifico unit\u00E0 documentaria"), SUB_STRUT("Sotto struttura");

        private final String descrizione;

        private TipoCampo(String descrizione) {
            this.descrizione = descrizione;
        }

        public String getDescrizione() {
            return this.descrizione;
        }

        public static TipoCampo[] getEnums(TipoCampo... vals) {
            return vals;
        }

        public static TipoCampo[] getCampiOutSelUd() {
            return getEnums(DATO_PROFILO, DATO_SPEC_DOC_PRINC, DATO_SPEC_UNI_DOC);
        }
    }

    public enum NomeCampo {

        REGISTRO("Registro", 1), ANNO("Anno", 2), NUMERO("Numero", 3), KEY_ORD_UD("Ordinamento ud", 4),
        TIPO_UNITA_DOC("Tipologia unit\u00E0 documentaria", 5), DATA_REG("Data unit\u00E0 documentaria", 6),
        OGGETTO("Oggetto", 7), TIPO_DOC_PRINC("Tipo documento principale", 8), DATA_VERS("Data di versamento", 9),
        ANNO_SERIE("Anno della serie", 10), CODICE_SERIE("Codice della serie", 11), SUB_STRUTTURA("SUB_STRUTTURA", 12);

        private final String descrizione;
        private final int numeroOrdine;

        private NomeCampo(String descrizione, int numeroOrdine) {
            this.descrizione = descrizione;
            this.numeroOrdine = numeroOrdine;
        }

        public String getDescrizione() {
            return this.descrizione;
        }

        public int getNumeroOrdine() {
            return numeroOrdine;
        }

        public static NomeCampo[] getEnums(NomeCampo... vals) {
            return vals;
        }

        public static NomeCampo[] getComboDatoProfilo() {
            return getEnums(TIPO_UNITA_DOC, REGISTRO, TIPO_DOC_PRINC);
        }

        public static NomeCampo[] getComboSubStruttura() {
            return getEnums(SUB_STRUTTURA);
        }

        public static NomeCampo[] getListaDatoProfilo() {
            return getEnums(REGISTRO, ANNO, NUMERO, KEY_ORD_UD, TIPO_UNITA_DOC, DATA_REG, OGGETTO, TIPO_DOC_PRINC,
                    DATA_VERS, ANNO_SERIE, CODICE_SERIE);
        }

        public static NomeCampo[] getListaDatoProfiloIndividuazione() {
            return getEnums(REGISTRO, ANNO, NUMERO, TIPO_UNITA_DOC, DATA_REG, OGGETTO, TIPO_DOC_PRINC, DATA_VERS);
        }

        public static NomeCampo fromString(String text) {
            NomeCampo result = null;
            for (NomeCampo b : NomeCampo.values()) {
                if (b.descrizione.equals(text)) {
                    result = b;
                    break;
                }
            }
            return result;
        }

        public static NomeCampo byName(String name) {
            NomeCampo result = null;
            for (NomeCampo b : NomeCampo.values()) {
                if (b.name().equals(name)) {
                    result = b;
                    break;
                }
            }
            return result;
        }

        public static NomeCampo[] getListaCampiCsvRichiestaAnnul() {
            return getEnums(REGISTRO, ANNO, NUMERO);
        }

    }

    public enum TipoPartizione {

        UNI_DOC, UNI_DOC_SUB_STRUT, BLOB, FILE, SES, FILE_SES, AIP_UD
    }

    public enum TipiOutputRappr {

        AIP("AIP"), DIP("DIP");

        private final String valore;

        private TipiOutputRappr(String val) {
            this.valore = val;
        }

        @Override
        public String toString() {
            return valore;
        }

        public static TipiOutputRappr[] getEnums(TipiOutputRappr... vals) {
            return vals;
        }

        public static TipiOutputRappr[] getComboTipoOutputRappr() {
            return getEnums(DIP);
        }
    }

    public enum TipoAlgoritmoRappr {

        XSLT("XSLT"),
        //
        ALTRO("Altro");

        private final String valore;

        private TipoAlgoritmoRappr(String val) {
            this.valore = val;
        }

        public static TipoAlgoritmoRappr[] getEnums(TipoAlgoritmoRappr... vals) {
            return vals;
        }

        public static TipoAlgoritmoRappr[] getComboTipoAlgoritmoRappr() {
            return getEnums(ALTRO, XSLT);
        }

        @Override
        public String toString() {
            return valore;
        }

        public static TipoAlgoritmoRappr fromString(String text) {
            if (text != null) {
                for (TipoAlgoritmoRappr b : TipoAlgoritmoRappr.values()) {
                    if (text.equalsIgnoreCase(b.valore)) {
                        return b;
                    }
                }
            }
            return ALTRO;
        }
    }

    public enum StatoFileTrasform {

        ERRATO, INSERITO, MODIFICATO, VERIFICATO
    }

    public enum TipoCreazioneDoc {

        VERSAMENTO_UNITA_DOC, AGGIUNTA_DOCUMENTO
    }

    public enum TipoUpdUnitaDoc {
        METADATI
    }

    public enum StatoTitolario {

        VALIDATO, DA_VALIDARE
    }

    public enum TipoFormatoLivelloTitolario {

        NUMERICO, ROMANO, ALFABETICO, ALFANUMERICO;
    }

    public enum TipoOrdinamentoTipiSerie {

        KEY_UD_SERIE("Chiave unit\u00E0 documentaria"), DT_UD_SERIE("Data unit\u00E0 documentaria"),
        DT_KEY_UD_SERIE("Chiave e Data unit\u00E0 documentaria"),
        //
        ALTRO("Altro");

        private final String valore;

        private TipoOrdinamentoTipiSerie(String val) {
            this.valore = val;
        }

        public static TipoOrdinamentoTipiSerie[] getEnums(TipoOrdinamentoTipiSerie... vals) {
            return vals;
        }

        public static TipoOrdinamentoTipiSerie[] getComboTipoOrdinamentoTipiSerie() {
            return getEnums(KEY_UD_SERIE, DT_UD_SERIE, DT_KEY_UD_SERIE);
        }

        @Override
        public String toString() {
            return valore;
        }

        public static TipoOrdinamentoTipiSerie fromString(String text) {
            if (text != null) {
                for (TipoOrdinamentoTipiSerie b : TipoOrdinamentoTipiSerie.values()) {
                    if (text.equalsIgnoreCase(b.valore)) {
                        return b;
                    }
                }
            }
            return null;
        }
    }

    public enum TipoSelUdTipiSerie {

        ANNO_KEY("Anno della chiave delle unit\u00E0 documentarie"),
        DT_UD_SERIE("Range di date di selezione delle unit\u00E0 documentarie"),
        //
        ALTRO("Altro");

        private final String valore;

        private TipoSelUdTipiSerie(String val) {
            this.valore = val;
        }

        public static TipoSelUdTipiSerie[] getEnums(TipoSelUdTipiSerie... vals) {
            return vals;
        }

        public static TipoSelUdTipiSerie[] getComboTipoSelUdTipiSerie() {
            return getEnums(ANNO_KEY, DT_UD_SERIE);
        }

        @Override
        public String toString() {
            return valore;
        }

        public static TipoSelUdTipiSerie fromString(String text) {
            if (text != null) {
                for (TipoSelUdTipiSerie b : TipoSelUdTipiSerie.values()) {
                    if (text.equalsIgnoreCase(b.valore)) {
                        return b;
                    }
                }
            }
            return null;
        }
    }

    public enum TipoFiltroSerieUd {

        TIPO_DOC_PRINC("Tipo di documento principale"),
        // RANGE_DT_UD("Intervallo di date delle unit\u00E0 documentarie"),
        // RANGE_DT_CREA("Intervallo di date di versamento delle unit\u00E0 documentarie"),
        //
        ALTRO("Altro");

        private final String valore;

        private TipoFiltroSerieUd(String val) {
            this.valore = val;
        }

        public static TipoFiltroSerieUd[] getEnums(TipoFiltroSerieUd... vals) {
            return vals;
        }

        public static TipoFiltroSerieUd[] getComboTipoDiFiltro() {
            return getEnums(TIPO_DOC_PRINC);
        }

        @Override
        public String toString() {
            return valore;
        }

        public static TipoFiltroSerieUd fromString(String text) {
            if (text != null) {
                for (TipoFiltroSerieUd b : TipoFiltroSerieUd.values()) {
                    if (text.equalsIgnoreCase(b.valore)) {
                        return b;
                    }
                }
            }
            return null;
        }

        public static TipoFiltroSerieUd byName(String text) {
            if (text != null) {
                for (TipoFiltroSerieUd b : TipoFiltroSerieUd.values()) {
                    if (text.equalsIgnoreCase(b.name())) {
                        return b;
                    }
                }
            }
            return null;
        }
    }

    public enum TipoCreazioneSerie {

        CALCOLO_AUTOMATICO, ACQUISIZIONE_FILE
    }

    public enum TipoChiamataAsync {

        CALCOLO_AUTOMATICO, ACQUISIZIONE_FILE, GENERAZIONE_EFFETTIVO, CONTROLLO_CONTENUTO, VALIDAZIONE_SERIE
    }

    public enum StatoVersioneSerie {

        APERTA, APERTURA_IN_CORSO, CONTROLLATA, DA_CONTROLLARE, DA_FIRMARE, DA_VALIDARE, FIRMATA, VALIDATA, IN_CUSTODIA,
        ANNULLATA, VALIDAZIONE_IN_CORSO, FIRMA_IN_CORSO, FIRMATA_NO_MARCA;

        public static StatoVersioneSerie[] getEnums(StatoVersioneSerie... vals) {
            return vals;
        }

        public static StatoVersioneSerie[] getStatiVerSerieAutom() {
            return getEnums(APERTA, DA_CONTROLLARE, CONTROLLATA, DA_VALIDARE);
        }
    }

    public enum StatoVersioneSerieDaElab {

        APERTA, CONTROLLATA, DA_CONTROLLARE, DA_FIRMARE, DA_VALIDARE, FIRMATA, FIRMATA_NO_MARCA, VALIDATA

    }

    public enum StatoConservazioneSerie {

        AIP_DA_AGGIORNARE, AIP_GENERATO, AIP_IN_AGGIORNAMENTO, ANNULLATA, IN_ARCHIVIO, IN_CUSTODIA, PRESA_IN_CARICO
    }

    public enum StatoConservazioneUnitaDoc {

        ANNULLATA, AIP_DA_GENERARE, AIP_GENERATO, AIP_FIRMATO, AIP_IN_AGGIORNAMENTO, IN_ARCHIVIO, IN_CUSTODIA,
        IN_VOLUME_DI_CONSERVAZIONE, PRESA_IN_CARICO, VERSAMENTO_IN_ARCHIVIO
    }

    public enum StatoConservazioneUnitaDocNonAnnullata {
        AIP_DA_GENERARE, AIP_GENERATO, AIP_FIRMATO, AIP_IN_AGGIORNAMENTO, IN_ARCHIVIO, IN_CUSTODIA,
        IN_VOLUME_DI_CONSERVAZIONE, PRESA_IN_CARICO, VERSAMENTO_IN_ARCHIVIO
    }

    public enum TipoAnnullamentoUnitaDoc {

        ANNULLAMENTO, SOSTITUZIONE
    }

    public enum TipoContenSerie {

        UNITA_DOC, UNITA_ARK
    }

    public enum TipoContenutoVerSerie {

        ACQUISITO, CALCOLATO, EFFETTIVO
    }

    public enum StatoContenutoVerSerie {

        CONTROLLATA_CONSIST, CONTROLLO_CONSIST_IN_CORSO, CREATO, CREAZIONE_IN_CORSO, DA_CONTROLLARE_CONSIST
    }

    public enum ScopoFileInputVerSerie {

        ACQUISIRE_CONTENUTO, CONTROLLO_CONTENUTO
    }

    public enum TipoDiRappresentazione {

        KEY_UD_SERIE("Chiave delle unit\u00E0 documentarie appartenenti alla serie (OBBLIGATORIO)", 1),
        DT_UD_SERIE("Data delle unit\u00E0 documentarie appartenenti alla serie (OBBLIGATORIO)", 2),
        INFO_UD_SERIE("Informazioni delle unit\u00E0 documentarie appartenenti alla serie (OBBLIGATORIO)", 3),
        DS_KEY_ORD_UD_SERIE(
                "Chiave di ordinamento delle unit\u00E0 documentarie appartenenti alla serie (OBBLIGATORIO)", 4),
        PG_UD_SERIE("Progressivo delle unit\u00E0 documentarie appartenenti alla serie", 5),
        //
        ALTRO("Altro", 10);

        private final String descrizione;
        private final int numeroOrdine;

        private TipoDiRappresentazione(String descrizione, int numeroOrdine) {
            this.descrizione = descrizione;
            this.numeroOrdine = numeroOrdine;
        }

        public static TipoDiRappresentazione[] getEnums(TipoDiRappresentazione... vals) {
            return vals;
        }

        public static TipoDiRappresentazione[] getComboTipoDiRappresentazione() {
            return getEnums(KEY_UD_SERIE, DT_UD_SERIE, INFO_UD_SERIE, DS_KEY_ORD_UD_SERIE, PG_UD_SERIE);
        }

        @Override
        public String toString() {
            return this.descrizione;
        }

        public int getNumeroOrdine() {
            return this.numeroOrdine;
        }

        public static TipoDiRappresentazione fromString(String text) {
            if (text != null) {
                for (TipoDiRappresentazione b : TipoDiRappresentazione.values()) {
                    if (text.equalsIgnoreCase(b.descrizione)) {
                        return b;
                    }
                }
            }
            return null;
        }

        public static TipoDiRappresentazione byName(String text) {
            if (text != null) {
                for (TipoDiRappresentazione b : TipoDiRappresentazione.values()) {
                    if (text.equalsIgnoreCase(b.name())) {
                        return b;
                    }
                }
            }
            return null;
        }
    }

    public enum TipoTrasformatore {

        OUT_NO_SEP_CHAR("FROM_YYYYMMDD_TO_DATECHAR"), OUT_ANY_SEP_CHAR("FROM_YYYY-MM-DD_TO_DATECHAR"),
        OUT_PAD_CHAR("FROM_CHAR_TO_CHARPADSX"), OUT_NO_SEP_TO_YEAR("FROM_YYYYMMDD_TO_YYYY"),
        OUT_ANY_SEP_TO_YEAR("FROM_YYYY-MM-DD_TO_YYYY"), IN_NO_SEP_CHAR("FROM_DATECHAR_TO_YYYYMMDD");

        private final String transformString;

        private TipoTrasformatore(String transformString) {
            this.transformString = transformString;
        }

        public final String getTransformString() {
            return transformString;
        }

        public static TipoTrasformatore[] getEnums(TipoTrasformatore... vals) {
            return vals;
        }

        public static TipoTrasformatore[] getComboTipiOut() {
            return getEnums(OUT_NO_SEP_CHAR, OUT_ANY_SEP_CHAR, OUT_NO_SEP_TO_YEAR, OUT_ANY_SEP_TO_YEAR, OUT_PAD_CHAR);
        }

        public static TipoTrasformatore[] getComboTipiIn() {
            return getEnums(IN_NO_SEP_CHAR);
        }

        public static TipoTrasformatore fromString(String text) {
            if (text != null) {
                for (TipoTrasformatore b : TipoTrasformatore.values()) {
                    if (text.equalsIgnoreCase(b.transformString)) {
                        return b;
                    }
                }
            }
            return null;
        }

        public static TipoTrasformatore byName(String text) {
            if (text != null) {
                for (TipoTrasformatore b : TipoTrasformatore.values()) {
                    if (text.equalsIgnoreCase(b.name())) {
                        return b;
                    }
                }
            }
            return null;
        }

    }

    // Enum per operatori dati specifici
    public enum TipoOperatoreDatiSpec {

        CONTIENE, INIZIA_PER, DIVERSO, MAGGIORE, MAGGIORE_UGUALE, MINORE, MINORE_UGUALE, NON_CONTIENE, NULLO, UGUALE,
        NON_NULLO, E_UNO_FRA
    }

    public enum TipoErroreFileInputSerie {

        NO_UD, SQL_ERRATA, TROPPE_UD, UD_DOPPIA, CAMPI_NON_VALORIZZATI
    }

    public enum TipoContenutoPerEffettivo {

        CALCOLATO, ACQUISITO, ENTRAMBI
    }

    public enum ModalitaDefPrimaUltimaUd {

        CODICE, PROGRESSIVO, CHIAVE_UD
    }

    public enum TipoGravitaErrore {

        ERRORE, WARNING
    }

    public enum TipoErroreContenuto {

        BUCO_NUMERAZIONE, CHIAVE_DOPPIA, CHIAVE_FINALE, CHIAVE_INIZIALE, CONSISTENZA_NON_DEFINITA, LACUNA_ERRATA,
        PROGRESSIVO_DOPPIO, PROGRESSIVO_NULLO, NUMERO_TOTALE, UD_NON_VERS, UD_NON_SELEZIONATE, UD_ANNULLATA,
        ANOMALIA_ORDINAMENTO, UD_VERSATA_DOPO_CALCOLO, CONTENUTO_EFFETTIVO_VUOTO
    }

    public enum TipoOrigineErroreContenuto {
        CONTROLLO, VALIDAZIONE
    }

    public enum TipoLacuna {

        NON_PRODOTTE, MANCANTI
    }

    public enum TipoModLacuna {

        DESCRIZIONE, RANGE_PROGRESSIVI
    }

    public static final BigDecimal NUM_SERIE_36 = new BigDecimal(0.3);
    public static final BigDecimal NUM_SERIE_24 = new BigDecimal(0.5);
    public static final BigDecimal NUM_SERIE_12 = new BigDecimal(1);
    public static final BigDecimal NUM_SERIE_6 = new BigDecimal(2);
    public static final BigDecimal NUM_SERIE_4 = new BigDecimal(3);
    public static final BigDecimal NUM_SERIE_3 = new BigDecimal(4);
    public static final BigDecimal NUM_SERIE_2 = new BigDecimal(6);

    public enum IntervalliMeseCreazioneSerie {

        DECADE(CostantiDB.NUM_SERIE_36), QUINDICINA(CostantiDB.NUM_SERIE_24), MESE(CostantiDB.NUM_SERIE_12),
        BIMESTRE(CostantiDB.NUM_SERIE_6), TRIMESTRE(CostantiDB.NUM_SERIE_4), QUADRIMESTRE(CostantiDB.NUM_SERIE_3),
        SEMESTRE(CostantiDB.NUM_SERIE_2);

        BigDecimal numSerie;

        private IntervalliMeseCreazioneSerie(BigDecimal numSerie) {
            this.numSerie = numSerie;
        }

        public BigDecimal getNumSerie() {
            return this.numSerie;
        }

    }

    public enum TipoConservazioneSerie {

        FISCALE, IN_ARCHIVIO
    }

    public enum TipoFileVerSerie {

        IX_AIP_UNISINCRO, IX_AIP_UNISINCRO_FIRMATO, MARCA_IX_AIP_UNISINCRO
    }

    public enum TipoCreazioneIndiceAip {

        ANTICIPATO, ARCHIVIO
    }

    public enum TipoNotaSerie {

        NOTE_CONSERVATORE, NOTE_PRODUTTORE
    }

    public enum TipoDefTemplateEnte {

        NO_TEMPLATE("Ente NON template"), TEMPLATE_DEF_AMBIENTE("Ente template"),
        TEMPLATE_DEF_ENTE("Ente con strutture template definite specificatamente per l'ente");

        private String descrizione;

        TipoDefTemplateEnte(String descrizione) {
            this.descrizione = descrizione;
        }

        public String descrizione() {
            return this.descrizione;
        }
    }

    public enum TiPartition {

        AIP_UD, BLOB, FILE, FILE_ELENCHI_VERS, FILE_ELENCO_VERS_FASC, FILE_SER, FILE_SES, FILE_VOL_SER, SES, UNI_DOC,
        XML_SES
    }

    public enum TipiEsitoVerificaHash {

        POSITIVO, NEGATIVO, DISABILITATO, // nel caso la verifica hash non fosse da fare o nel caso di mancata
                                          // identificazione dell'algoritmo
        NON_EFFETTUATO // nel caso di hash forzato nel versamento MM
    }

    public enum TipiHash {

        SCONOSCIUTO("SCONOSCIUTO"), MD5("MD5"), SHA_1("SHA-1"), SHA_256("SHA-256");

        private String desc;

        private TipiHash(String ds) {
            desc = ds;
        }

        public String descrivi() {
            return desc;
        }

        public static TipiHash evaluateByDesc(String desc) {
            for (TipiHash hash : values()) {
                if (hash.descrivi().equals(desc)) {
                    return hash;
                }
            }
            return SCONOSCIUTO;
        }

    }

    public enum TipiEncBinari {

        SCONOSCIUTO("SCONOSCIUTO"), HEX_BINARY("hexBinary"), BASE64("base64");

        private String desc;

        private TipiEncBinari(String ds) {
            desc = ds;
        }

        public String descrivi() {
            return desc;
        }
    }

    public enum TiRichAnnulVers {
        FASCICOLI, UNITA_DOC
    }

    public enum TiItemRichAnnulVers {
        DOC, FASC, UNI_DOC
    }

    public enum TipoCreazioneRichAnnulVers {

        ON_LINE, UPLOAD_FILE, WEB_SERVICE
    }

    public enum StatoRichAnnulVers {

        APERTA, CHIUSA, COMUNICATA_A_SACER, EVASA, INVIO_FALLITO, RECUPERATA_DA_PING, RIFIUTATA
    }

    public enum TipoFileRichAnnulVers {

        FILE_UD_ANNUL, XML_RICH, XML_RISP, FILE_FASC_ANNUL
    }

    public enum StatoItemRichAnnulVers {

        ANNULLATO, DA_ANNULLARE_IN_PING, DA_ANNULLARE_IN_SACER, NON_ANNULLABILE
    }

    public enum TipoErrRichAnnulVers {

        ITEM_GIA_PRESENTE, ITEM_IN_CORSO_DI_ANNUL, ITEM_GIA_ANNULLATO, ITEM_NON_ESISTE, ITEM_RIFERITO,
        ITEM_VERSATA_IN_DATA_RICH, STATO_CONSERV_NON_AMMESSO, REGISTRO_NON_ABILITATO, TIPO_DOC_PRINC_NON_ABILITATO,
        TIPO_UNITA_DOC_NON_ABILITATO, TIPO_FASCICOLO_NON_ABILITATO, DA_ANNULLARE_IN_PING;

        public static String[] getStatiControlloItem() {
            // <<<<<<< Ritorna tutti gli stati tranne ITEM_NON_ESISTE e ITEM_GIA_PRESENTE - DA MODIFICARE IN CASO DI
            // AGGIUNTE

            String[] stati = new String[] { CostantiDB.TipoErrRichAnnulVers.ITEM_IN_CORSO_DI_ANNUL.name(),
                    CostantiDB.TipoErrRichAnnulVers.ITEM_RIFERITO.name(),
                    CostantiDB.TipoErrRichAnnulVers.ITEM_VERSATA_IN_DATA_RICH.name(),
                    CostantiDB.TipoErrRichAnnulVers.STATO_CONSERV_NON_AMMESSO.name(),
                    CostantiDB.TipoErrRichAnnulVers.REGISTRO_NON_ABILITATO.name(),
                    CostantiDB.TipoErrRichAnnulVers.TIPO_DOC_PRINC_NON_ABILITATO.name(),
                    CostantiDB.TipoErrRichAnnulVers.TIPO_UNITA_DOC_NON_ABILITATO.name(),
                    CostantiDB.TipoErrRichAnnulVers.TIPO_FASCICOLO_NON_ABILITATO.name(),
                    CostantiDB.TipoErrRichAnnulVers.DA_ANNULLARE_IN_PING.name(), };
            return stati;
        }
    }

    public enum TipoRegolaModelloTipoSerie {

        DEFINITO_NEL_MODELLO, TUTTI, EREDITA_DA_REG, EREDITA_DA_TIPO_UD_REG;

        public static TipoRegolaModelloTipoSerie[] getEnums(TipoRegolaModelloTipoSerie... vals) {
            return vals;
        }

        public static TipoRegolaModelloTipoSerie[] getTiRglAnniConserv() {
            return getEnums(DEFINITO_NEL_MODELLO, EREDITA_DA_REG);
        }

        public static TipoRegolaModelloTipoSerie[] getTiRglCdSerie() {
            return getEnums(DEFINITO_NEL_MODELLO, EREDITA_DA_TIPO_UD_REG);
        }

        public static TipoRegolaModelloTipoSerie[] getTiRglConservazioneSerie() {
            return getEnums(DEFINITO_NEL_MODELLO);
        }

        public static TipoRegolaModelloTipoSerie[] getTiRglDsSerie() {
            return getEnums(DEFINITO_NEL_MODELLO, EREDITA_DA_TIPO_UD_REG);
        }

        public static TipoRegolaModelloTipoSerie[] getTiRglDsTipoSerie() {
            return getEnums(DEFINITO_NEL_MODELLO, EREDITA_DA_TIPO_UD_REG);
        }

        public static TipoRegolaModelloTipoSerie[] getTiRglFiltroTiDoc() {
            return getEnums(DEFINITO_NEL_MODELLO, TUTTI);
        }

        public static TipoRegolaModelloTipoSerie[] getTiRglNmTipoSerie() {
            return getEnums(DEFINITO_NEL_MODELLO, EREDITA_DA_TIPO_UD_REG);
        }

        public static TipoRegolaModelloTipoSerie[] getTiRglRangeAnniCreaAutom() {
            return getEnums(DEFINITO_NEL_MODELLO, EREDITA_DA_REG);
        }
    }

    public enum TipoSerieCreaStandard {

        BASATA_SU_REGISTRO, BASATA_SU_TIPO_UNITA_DOC
    }

    public enum TiXmlRichAnnulVers {

        RICHIESTA, RISPOSTA
    }

    public enum TiClasseTipoServizio {

        ALTRO, ATTIVAZIONE_SISTEMA_VERSANTE, CONSERVAZIONE, ATTIVAZIONE_TIPO_UD
    }

    public enum TiParte {
        ANNO, CLASSIF, PROGR_FASC, PROGR_SUB_FASC
    }

    public enum TiGestElencoCriterio {
        FIRMA, NO_FIRMA, MARCA_FIRMA
    }

    // vista da cui recuperare i valori
    public enum TipoAplVGetValAppart {
        AATIPOFASCICOLO, TIPOUNITADOC, STRUT, AMBIENTE, APPLIC;

        public static TipoAplVGetValAppart next(TipoAplVGetValAppart last) {
            switch (last) {
            case AATIPOFASCICOLO:
                return STRUT;
            case TIPOUNITADOC:
                return STRUT;
            case STRUT:
                return AMBIENTE;
            case AMBIENTE:
                return APPLIC;
            default:
                return null;
            }
        }
    }

    // MEV#25903
    // vista da cui recuperare i valori su Iam
    public enum TipoIamVGetValAppart {
        AMBIENTEENTECONVENZ, ENTECONVENZ, APPLIC;

        public static TipoIamVGetValAppart next(TipoIamVGetValAppart last) {
            switch (last) {
            case ENTECONVENZ:
                return AMBIENTEENTECONVENZ;
            case AMBIENTEENTECONVENZ:
                return APPLIC;
            default:
                return null;
            }
        }
    }
    // end MEV#25903

    //
    public class Flag {

        public static final String TRUE = "1";
        public static final String FALSE = "0";
    }

    /*
     * Tutte le versioni relative ai report zip gestiti su servizi di verifica firma
     * 
     * V_10 -> all, none versions (il report versione 1.0 è il primo supportato da tutte le versioni di tutti i servizi
     * delle librerie sui micro di verifica firma in essere).
     * 
     * Una eventuale versione 1.1 o 2.0 specifica per certi servizi / versioni dovrà essere censita nella logica
     * sottostante del tipo:
     * 
     * V_11("EIDAS|CRYPTO","6.0|1.13.0") oppure V_20("EIDAS","7.0")
     * 
     * differenziare poi la gestione sia lato generazione del report che in fase di parsing.
     *
     */
    public enum ReportvfZipVersion {

        V_10("", "all", "none");

        private final String[] services;
        private final String[] versions;

        private ReportvfZipVersion(String delimiter, String services, String versions) {
            this.services = services.split(delimiter);
            this.versions = versions.split(delimiter);
        }

        public static ReportvfZipVersion getByServiceAndVersion(String service, String version) {
            return Stream.of(values()).filter(
                    v -> Arrays.asList(v.services).contains(service) && Arrays.asList(v.versions).contains(version))
                    .findAny().orElse(V_10);
        }

    }
}
