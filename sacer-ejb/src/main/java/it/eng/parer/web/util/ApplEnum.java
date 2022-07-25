package it.eng.parer.web.util;

public class ApplEnum {

    public enum DichAutor {

        ENTRY_MENU, AZIONE, SERVIZIO_WEB, PAGINA
    }

    public enum ScopoDichAutor {

        ALL_ABILITAZIONI, ALL_ABILITAZIONI_CHILD, UNA_ABILITAZIONE
    }

    public enum ScopoDichAbilStrut {

        TUTTI_AMBIENTI, UN_AMBIENTE, UN_ENTE, UNA_STRUTTURA, STRUTTURA_DEFAULT
    }

    public enum ScopoDichAbilDati {

        TUTTI_AMBIENTI, UN_AMBIENTE, UN_ENTE, UNA_STRUTTURA, UNA_ABILITAZIONE
    }

    public enum TipoDichAbilDati {

        TIPO_UD, REGISTRO_UD
    }

    public enum TipoFiltroMultiploCriteriRaggr {

        RANGE_REGISTRO_UNI_DOC, REGISTRO_UNI_DOC, TIPO_DOC, TIPO_ESITO_VERIF_FIRME, TIPO_UNI_DOC, SISTEMA_MIGRAZ
    }

    public enum TipoSelCriteriRaggrFasc {

        TIPO_FASCICOLO, SISTEMA_MIGRAZ, VOCE_TITOL
    }

    public enum TiOperReplic {

        INS, MOD, CANC
    }

    public enum NmOrganizReplic {

        AMBIENTE, ENTE, STRUTTURA
    }

    public enum ComboFlag {

        SI("1"), NO("0");

        private String value;

        private ComboFlag(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum FlagFiscaleMessage {
        FISCALE("Criterio fiscale"), NON_FISCALE("Criterio non fiscale"), NON_AMMESSO("Criterio non ammesso");

        private String descrizione;

        private FlagFiscaleMessage(String descrizione) {
            this.descrizione = descrizione;
        }

        public String getDescrizione() {
            return descrizione;
        }
    }
}
