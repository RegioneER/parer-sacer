package it.eng.parer.volume.utils;

import it.eng.parer.ws.utils.CostantiDB.TipiHash;

/**
 *
 * @author Agati_D
 */
public class VolumeEnums {

    public enum RifTempUsatoEnum {

        DATA_FIRMA, DATA_VERS, MT_VERS_NORMA, MT_VERS_SEMPLICE, RIF_TEMP_VERS
    }

    public enum DocTypeEnum {

        PRINCIPALE, ALLEGATO, ANNESSO, ANNOTAZIONE
    }

    // RECUPERA_VOLUME_SCADUTO
    public enum OpTypeEnum {

        INIZIO_SCHEDULAZIONE, FINE_SCHEDULAZIONE, CREAZIONE_VOLUMI, CHIUSURA_VOLUME, CHIUSURA_VOLUME_ERR, CREA_VOLUME,
        AGGIUNGI_DOC_VOLUME, ELIMINA_VOLUME, RIMUOVI_DOC_VOLUME, MODIFICA_VOLUME, FIRMA_NO_MARCA_VOLUME, FIRMA_VOLUME,
        ERRORE, SCARICO_CA, REVOCA_CA, SCARICO_CRL, CALCOLO_CONTENUTO_SACER, CREAZIONE_INDICI, CREA_INDICE_VOLUME,
        MARCA_INDICE_VOLUME, VERIFICA_FIRME, INIZIO_CREA_INDICE, INIZIO_VERIF_FIRME, ERR_VERIF_FIRME,
        SET_VOLUME_DA_CHIUDERE, RECUPERA_VOLUME_APERTO, RECUPERA_VOLUME_SCADUTO, RECUPERA_VOLUME_IN_ERRORE,
        SET_VOLUME_IN_ERRORE, SET_VOLUME_APERTO, ALLINEAMENTO_ORGANIZZAZIONI, CREAZIONE_INDICE_AIP,
        VERIFICA_MASSIVA_VERS_FALLITI, VERIFICA_VERS_FALLITI
    }

    public enum VolStatusEnum {

        IN_ERRORE, CHIUSO, APERTO, NON_SELEZ_SCHED, IN_ATTESA_SCHED, FIRMATO_NO_MARCA, FIRMATO, DA_CHIUDERE,
        DA_VERIFICARE;

        public static VolStatusEnum[] getEnums(VolStatusEnum... vals) {
            return vals;
        }

        public static VolStatusEnum[] getComboMappaStatoVol() {
            return getEnums(CHIUSO, FIRMATO, FIRMATO_NO_MARCA, DA_VERIFICARE);
        }

        public static VolStatusEnum[] getComboMappaStatoVolRicercaUd() {
            return getEnums(FIRMATO, FIRMATO_NO_MARCA, DA_VERIFICARE);
        }

        public static VolStatusEnum[] getComboMappaStatoVolRicerca() {
            return getEnums(APERTO, CHIUSO, IN_ERRORE, FIRMATO, FIRMATO_NO_MARCA, DA_VERIFICARE, DA_CHIUDERE);
        }
    }

    public enum DocStatusEnum {

        IN_VOLUME_CHIUSO, IN_VOLUME_APERTO, IN_VOLUME_IN_ERRORE, IN_ATTESA_SCHED, IN_VOLUME_DA_CHIUDERE,
        IN_ATTESA_MEMORIZZAZIONE, NON_SELEZ_SCHED;

        public static DocStatusEnum[] getEnums(DocStatusEnum... vals) {
            return vals;
        }
    }

    public enum ExpirationTypeEnum {

        GIORNALIERA, SETTIMANALE, QUINDICINALE, MENSILE
    }

    public enum SignatureInfoEnum {

        TUTTI_SENZA_FIRMA, TUTTI_CON_FIRMA, CON_E_SENZA_FIRMA, TUTTI_FIRMA_VALIDA, TUTTI_FIRMA_INVALIDA,
        VALIDE_E_INVALIDE, POSITIVO, NEGATIVO, WARNING;

        public static SignatureInfoEnum[] getEnums(SignatureInfoEnum... vals) {
            return vals;
        }

        public static SignatureInfoEnum[] getComboPresenzaFirme() {
            return getEnums(CON_E_SENZA_FIRMA, TUTTI_CON_FIRMA, TUTTI_SENZA_FIRMA);
        }

        public static SignatureInfoEnum[] getComboValiditaFirme() {
            return getEnums(TUTTI_FIRMA_INVALIDA, TUTTI_FIRMA_VALIDA, VALIDE_E_INVALIDE);
        }
    }

    public enum FileTypeEnum {

        INDICE, MARCA_INDICE, FILE, FIRMA, MARCA_FIRMA
    }

    public enum ModeEnum {

        BATCH, ONLINE, ADD, SUB
    }

    public enum TimeTypeEnum {

        MINUTI, ORE, GIORNI
    }

    public enum ControlloConformitaEnum {

        POSITIVO, FORMATO_NON_CONFORME, FORMATO_NON_CONOSCIUTO, NON_AMMESSO_DELIB_45_CNIPA
    }

    public enum EsitoControllo {

        SIGNED("1"), UNSIGNED("0"), VALIDO("1"), NON_VALIDO("0");

        private final String message;

        private EsitoControllo(final String message) {
            this.message = message;
        }

        public java.lang.String message() {
            return this.message;
        }
    }

    public enum MotivazioneChiusura {

        UD_SURPLUS(
                "L'aggiunta al volume di una ulteriore unità documentaria supererebbe il numero massimo di unità documentarie"),
        COMP_SURPLUS("L'aggiunta al volume di un ulteriore componente supererebbe il numero massimo di componenti"),
        VOLUME_EXPIRED("Volume scaduto"), MANUAL_CLOSING("Chiusura manuale");

        private final String message;

        private MotivazioneChiusura(final String message) {
            this.message = message;
        }

        public java.lang.String message() {
            return this.message;
        }
    }

    public enum ElencoInfo {

        NON_DETERMINABILE, VERSIONE_ELENCO("1.0");

        private final String message;

        private ElencoInfo() {
            this.message = null;
        }

        private ElencoInfo(final String message) {
            this.message = message;
        }

        public java.lang.String message() {
            return this.message;
        }
    }

    public enum IndexFlag {

        FLAG_OFF("0"), FLAG_ON("1");

        private final String message;

        private IndexFlag() {
            this.message = null;
        }

        private IndexFlag(final String message) {
            this.message = message;
        }

        public java.lang.String message() {
            return this.message;
        }
    }

    public enum JobName {

        JOB1("JOB1"), JOB2("JOB2"), JOB3("JOB3");

        private final String message;

        private JobName() {
            this.message = null;
        }

        private JobName(final String message) {
            this.message = message;
        }

        public java.lang.String message() {
            return this.message;
        }
    }

    public enum ModalitaOperazioni {

        MANUALE, AUTOMATICA
    }

    public enum StatoVerifica {

        POSITIVO, NEGATIVO, WARNING;

        public static StatoVerifica[] getEnums(StatoVerifica... vals) {
            return vals;
        }

        public static StatoVerifica[] getComboEsitoVerifFirmeChius() {
            return getEnums(POSITIVO, NEGATIVO);
        }

    }

    public enum StatoFormatoVersamento {

        DEPRECATO, GESTITO, IDONEO, NON_CONTROLLABILE, NEGATIVO, POSITIVO, WARNING, DISABILITATO
    }

    public enum AlgoHash {

        SHA_1(TipiHash.SHA_1.descrivi()), SHA_256(TipiHash.SHA_256.descrivi());

        private final String algorithm;

        private AlgoHash(final String algorithm) {
            this.algorithm = algorithm;
        }

        public java.lang.String algorithm() {
            return this.algorithm;
        }
    }

    public enum EncodingHash {

        HEX_BINARY("hexBinary");

        private final String encoding;

        private EncodingHash(final String encoding) {
            this.encoding = encoding;
        }

        public java.lang.String encoding() {
            return this.encoding;
        }
    }

    public enum TipoConservazione {

        SOSTITUTIVA, FISCALE, MIGRAZIONE, VERSAMENTO_ANTICIPATO, VERSAMENTO_IN_ARCHIVIO

    }

    public enum TipoConservazioneFasc {

        IN_ARCHIVIO, MIGRAZIONE, VERSAMENTO_ANTICIPATO

    }

    public enum TipoOutputMonitoraggio {

        ANALITICO, CRONOLOGICO, AGGREGATO;
    }

    public enum VersStatusEnum {

        RISOLTO, NO_VERIF, VERIF, NO_RISOLUB
    }

    public enum MonitAnnulStatusEnum {

        DA_FARE_PING(AnnulStatusEnum.DA_ANNULLARE_PING), DA_FARE_SACER(AnnulStatusEnum.DA_ANNULLARE_SACER),
        OK(AnnulStatusEnum.ANNULLAMENTO_FATTO);

        public AnnulStatusEnum transcodedValue;

        MonitAnnulStatusEnum(AnnulStatusEnum value) {
            this.transcodedValue = value;
        }

        public AnnulStatusEnum getTranscodedValue() {
            return this.transcodedValue;
        }

    }

    public enum AnnulStatusEnum {

        DA_ANNULLARE_PING, DA_ANNULLARE_SACER, ANNULLAMENTO_FATTO
    }

    public enum TipoVersAnnul {

        VERS_UNITA_DOC, AGG_DOC
    }

}
