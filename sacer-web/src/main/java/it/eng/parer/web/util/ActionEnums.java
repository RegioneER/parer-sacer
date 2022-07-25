package it.eng.parer.web.util;

public class ActionEnums {

    public enum TipoDoc {

        ALLEGATO, ANNESSO, ANNOTAZIONE
    }

    public enum VolumiAttributes {

        TABLE, TIPOLISTA, SINGLERECORD
    }

    public enum UnitaDocAttributes {

        TIPORICERCA
    }

    public enum TipoListaAttribute {

        ERRORE, FIRMA, FIRMATI_NO_MARCATI
    }

    public enum TipoRicercaAttribute {

        SEMPLICE, AVANZATA, VERS_ANNULLATI
    }

    public enum DichAutor {

        ENTRY_MENU, AZIONE, SERVIZIO_WEB, PAGINA
    }

    public enum ScopoDichAutor {

        ALL_ABILITAZIONI, ALL_ABILITAZIONI_CHILD, UNA_ABILITAZIONE
    }

    // Constants for Configuration
    public enum Configuration {

        // Indice standard
        MAX_RESULT_STANDARD,
        // Ricerche
        MAX_RESULT_RICERCA_UD, MAX_RESULT_RICERCA_COMP, MAX_RESULT_RICERCA_VOL, MAX_RESULT_RICERCA_UTENTI,
        // Liste monitoraggio
        MAX_RESULT_SESSIONI_ERRATE, MAX_RESULT_VERSAMENTI_FALLITI, MAX_RESULT_SCHED_JOB, MAX_RESULT_LISTA_DOCUMENTI,
        MAX_RESULT_VERSAMENTI_FALLITI_DIST_DOC, MAX_RESULT_RIEPILOGO_STRUTTURE, MAX_RESULT_OPERAZIONI_VOLUMI,
        MAX_RESULT_REPLICA_ORGANIZ,
        // Liste in dettaglio documento
        MAX_RESULT_COMP_DOCS, MAX_RESULT_DATI_SPEC_DOCS, MAX_RESULT_VOL_DOCS,
        // Criterio di raggruppamento
        MAX_RESULT_CRITERI_RAGGR,
        // Liste in dettaglio ud
        MAX_RESULT_COLLEGAMENTI_UD, MAX_RESULT_ARCHIVIAZIONI_UD, MAX_RESULT_INDICI_AIP,
        // Liste in dettaglio comp
        MAX_RESULT_DATI_COMP, MAX_RESULT_FIRME_COMP, MAX_RESULT_MARCHE_COMP,
        // Liste in dettaglio marca
        MAX_RESULT_CERTIF_CA_MARCA,
        // Liste in dettaglio firma
        MAX_RESULT_CERTIF_CA_FIRMA, MAX_RESULT_CONTRO_FIRMATARI_FIRMA,
        // Liste in dettaglio volume
        MAX_RESULT_COMP_VOL,
        // Liste volumi
        MAX_RESULT_VOLUMI_ERRORE, MAX_RESULT_VOLUMI_FIRMA,
        // Lista ruoli
        MAX_RESULT_RUOLI,
        // Liste in dettaglio ruoli
        MAX_RESULT_DICH_ENTRY_MENU, MAX_RESULT_DICH_PAGES, MAX_RESULT_DICH_ACTIONS, MAX_RESULT_DICH_SERVICES,
        // Combo
        MAX_RESULT_TIPO_UD_COMBO, MAX_RESULT_TIPO_DOC_COMBO, MAX_RESULT_TIPO_COMP_DOC_COMBO,
        MAX_RESULT_TIPO_STRUT_DOC_COMBO, MAX_RESULT_FORMATO_FILE_DOC_COMBO, MAX_RESULT_CRITERIO_RAGGR_COMBO,
        MAX_RESULT_AMBIENTE_COMBO, MAX_RESULT_ENTI_AMBIENTE_COMBO, MAX_RESULT_STRUTTURE_ENTI_COMBO,
        MAX_RESULT_RUOLI_COMBO
    }

    public enum Application {

        SACER
    }

    // Constants for MenuEntry fields in csv
    public enum MenuEntryFields {

        APPLIC, NM_ENTRY_MENU, DS_ENTRY_MENU, NI_LIVELLO_ENTRY_MENU, NI_ORD_ENTRY_MENU, NM_ENTRY_MENU_PADRE,
        DL_LINK_ENTRY_MENU
    }

    // Constants for WebPages fields in csv
    public enum WebPagesFields {

        APPLIC, NM_PAGINA_WEB, DS_PAGINA_WEB
    }

    // Constants for Actions fields in csv
    public enum ActionsFields {

        APPLIC, NM_PAGINA_WEB, NM_AZIONE_PAGINA, DS_AZIONE_PAGINA
    }

    // Constants for WebServices fields in csv
    public enum WebServicesFields {

        APPLIC, NM_SERVIZIO_WEB, DS_SERVIZIO_WEB
    }

    // Constants for user configuration
    public enum ScopoDichAbilStrut {

        TUTTI_AMBIENTI, UN_AMBIENTE, UN_ENTE, UNA_STRUTTURA, STRUTTURA_DEFAULT
    }

    public enum ScopoDichAbilDati {

        TUTTI_AMBIENTI, UN_AMBIENTE, UN_ENTE, UNA_STRUTTURA, UNA_ABILITAZIONE
    }

    public enum TipoDichAbilDati {

        TIPO_UD, REGISTRO_UD
    }

    public enum SezioneMonitoraggio {

        RIEPILOGO_STRUTTURA, RIEPILOGO_VERSAMENTI, FILTRI_DOCUMENTI, FILTRI_VERSAMENTI, FILTRI_DOCUMENTI_NON_VERS,
        SESSIONI_ERRATE, OPERAZIONI_VOLUMI, OPERAZIONI_ELENCHI_VERSAMENTO, CONTENUTO_SACER, JOB_SCHEDULATI, REPLICA_ORG,
        FILTRI_DOCUMENTI_ANNUL
    }

    public enum SezioneCriteriRaggr {
        FILTRI_CRITERI_RAGGR, CRITERIO_RAGGR_DETAIL
    }

    public enum SezioneCriteriRaggrFasc {
        FILTRI_CRITERI_RAGGR_FASC, CRITERIO_RAGGR_FASC_DETAIL
    }

    public enum SezioneElenchi {
        RICERCA_ELENCHI, RICERCA_ELENCHI_INDICI_AIP
    }

    public enum SezioneElenchiVersFascicoli {
        RICERCA_ELENCHI_VERS_FASC, RICERCA_ELENCHI_VERS_FASC_DA_FIRMARE, RICERCA_ELENCHI_VERS_FASC_INDICI_AIP
    }

    public enum TipoFiltroMultiploCriteriRaggr {

        RANGE_REGISTRO_UNI_DOC, REGISTRO_UNI_DOC, TIPO_DOC, TIPO_ESITO_VERIF_FIRME, TIPO_UNI_DOC, SISTEMA_MIGRAZ
    }

    public enum TipoSelCriteriRaggrFasc {

        TIPO_FASCICOLO, SISTEMA_MIGRAZ, VOCE_TITOL
    }
}
