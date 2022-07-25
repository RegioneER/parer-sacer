/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.web.util;

/**
 *
 * @author 101000_exmonetatedesca
 */
public class WebConstants extends Constants {

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int STRUTLIST_PAGE_SIZE = 20;
    public static final int FORMATI_PAGE_SIZE = 10;
    public static final int FILESIZE = 1024;
    public static final int DICH_ABIL_STRUT = 0;
    public static final int DICH_ABIL_DATI = 1;
    public static final int CRITERIO_INSERT = 0;
    public static final int CRITERIO_EDIT = 1;
    public static final String FORMATO_DATA = "(gg/mm/aaaa)";

    public enum Organizzazione {

        AMBIENTE, ENTE, STRUTTURA, AMBIENTE_ID, ENTE_ID, STRUTTURA_ID, STRUTTURA_LINK
    }

    public enum XsdType {

        TIPO_DOC, TIPO_COMP_DOC, TIPO_UNITA_DOC
    }

    public enum SignerType {

        CADES_BES, CADES_C, CADES_T, CADES_X_Long, M7M, P7M, P7S, PADES, PADES_BES, PADES_C, PADES_T, PDF_DSIG, TSD,
        TSR, TST, XADES, XADES_BES, XADES_C, XADES_T, XADES_X, XADES_XL, XML_DSIG,

    }

    public enum TipoCompDocCombo {

        CONTENUTO, CONVERTITORE, FIRMA, MARCA, RAPPRESENTAZIONE, SEGNATURA, FIRMA_ELETTRONICA
    }

    public enum TipoAmbitoTerritoriale {

        FORMA_ASSOCIATA("FORMA_ASSOCIATA"), PROVINCIA("PROVINCIA"), REGIONE_STATO("REGIONE/STATO");

        private String nome;

        public String getNome() {
            return nome;
        }

        private TipoAmbitoTerritoriale(String nome) {
            this.nome = nome;
        }
    }

    public enum DOWNLOAD_ATTRS {

        DOWNLOAD_ACTION, DOWNLOAD_FILENAME, DOWNLOAD_FILEPATH, DOWNLOAD_DELETEFILE, DOWNLOAD_CONTENTTYPE
    }

    public enum DOWNLOAD_DIP {

        DIP_RISPOSTA_WS, DIP_RECUPERO_EXT, DIP_ENTITA
    }

    // Nome parametri monitoraggio sintetico
    public static final String PARAMETER_STATO = "ti_stato";
    public static final String PARAMETER_CREAZIONE = "ti_creazione";
    public static final String PARAMETER_TIPO = "tipo";

    public static final String PARAMETER_STATO_TUTTI = "TUTTI";
    public static final String PARAMETER_CREAZIONE_OGGI = "OGGI";
    public static final String PARAMETER_CREAZIONE_30GG = "30gg";
    public static final String PARAMETER_CREAZIONE_B30 = "B30gg";
    public static final String PARAMETER_TIPO_UD = "UNITA_DOC";
    public static final String PARAMETER_TIPO_DOC = "DOCUMENTI";

    public static final String PARAMETER_SESSION_GET_CNT = "SINTETICO_CALCOLA_CONTATORI";
    public static final String PARAMETER_SESSION_GET_CNT_FASCICOLI = "FASCICOLI_CALCOLA_CONTATORI";
    public static final String PARAMETER_SESSION_GET_CNT_AGG_META = "AGG_META_CALCOLA_CONTATORI";

    // Nome parametri sotto strutture
    public static final String PARAMETER_EVENT = "event";
    public static final String PARAMETER_RIGA = "riga";

    // Nome parametri Dettaglio serie
    public static final String PARAMETER_VER_SERIE = "VerSerieList";

    public enum PARAMETER_JSON_FUTURE_SERIE {

        TIPO_CREAZIONE, CODICE_SERIE, ANNO_SERIE, ID_VERSIONE, RESULT, ID_STRUT
    }

    public enum PARAMETER_JSON_FUTURE_SERIE_RESULT {

        OK, NO_LOCK, ERROR, WORKING
    }

    // Nome parametro titolario
    public enum PARAMETER_TITOLARIO {

        DATE_TITOLARIO, DATE_TITOLARIO_STRING, LIVELLI_TITOLARIO, NOME_LIVELLI_TITOLARIO, LIVELLI_PARSING,
        NUM_ORDINE_VOCI, VOCI_MAP, LIVELLI_VOCI
    }

    // MIME TYPES
    public static final String MIME_TYPE_ZIP = "application/zip, application/octet-stream";
    public static final String MIME_TYPE_PDF = "application/pdf, application/octet-stream";

    // REGEXP MODELLO XSD VERSION VALIDATOR
    public static final String MODELLO_XSD_VERSION_REGEXP = "^(([A-Za-z0-9][.\\-_]{0,1}).+(?![^0-9a-zA-Z]+)(?![.\\-_]).)$";

}
