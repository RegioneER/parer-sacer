package it.eng.parer.elencoVersFascicoli.utils;

import java.util.ArrayList;
import java.util.List;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;

/**
 *
 * @author DiLorenzo_F
 */
public class ElencoEnums {

    public enum RifTempUsatoEnum {

        DATA_FIRMA, DATA_VERS, MT_VERS_NORMA, MT_VERS_SEMPLICE, RIF_TEMP_VERS
    }

    public enum DocTypeEnum {

        PRINCIPALE, ALLEGATO, ANNESSO, ANNOTAZIONE
    }

    public enum OpTypeEnum {

        INIZIO_SCHEDULAZIONE, FINE_SCHEDULAZIONE, CHIUSURA_ELENCO, CHIUSURA_ELENCO_ERR, CREA_ELENCO,
        AGGIUNGI_DOC_ELENCO, ELIMINA_ELENCO, RIMUOVI_UD_ELENCO, RIMUOVI_DOC_ELENCO, MOD_ELENCO, VALIDAZIONE_ELENCO,
        ERRORE, REVOCA_CA, CREAZIONE_INDICI_ELENCHI_VERS_FASC, CREA_INDICE_ELENCO, VERIFICA_FIRME_A_DATA_VERS,
        INIZIO_CREA_INDICE, INIZIO_VERIF_FIRME, ERR_VERIF_FIRME, SET_ELENCO_DA_CHIUDERE, RECUPERA_ELENCO_APERTO,
        RECUPERA_ELENCO_SCADUTO, RECUPERA_ELENCO_IN_ERRORE, SET_ELENCO_IN_ERRORE, SET_ELENCO_APERTO,
        VERIFICA_VERS_FALLITI, DEF_NOTE_INDICE_ELENCO, DEF_NOTE_ELENCO_CHIUSO, START_CREA_ELENCO_INDICI_AIP_FASC,
        END_CREA_ELENCO_INDICI_AIP_FASC, FIRMA_ELENCO_INDICI_AIP, FIRMA_ELENCO_INDICI_AIP_IN_CORSO,
        FIRMA_ELENCO_INDICI_AIP_FALLITA, FIRMA_IN_CORSO, FIRMA_IN_CORSO_FALLITA, MARCA_ELENCO_INDICI_AIP_FALLITA,
        MARCA_ELENCO_INDICI_AIP
    }

    public enum GestioneElencoEnum {
        FIRMA, NO_FIRMA, MARCA_FIRMA
    }

    public enum ElencoStatusEnum {

        COMPLETATO, APERTO, CHIUSO, DA_CHIUDERE, ELENCO_INDICI_AIP_CREATO, ELENCO_INDICI_AIP_FIRMA_IN_CORSO, FIRMATO,
        FIRMA_IN_CORSO, IN_CODA_CREAZIONE_AIP, AIP_CREATI;

        public static ElencoStatusEnum[] getEnums(ElencoStatusEnum... vals) {
            return vals;
        }

        public static ElencoStatusEnum[] getComboMappaStatoElencoRicerca() {
            return getEnums(APERTO, DA_CHIUDERE, CHIUSO, FIRMATO, FIRMA_IN_CORSO, ELENCO_INDICI_AIP_CREATO,
                    IN_CODA_CREAZIONE_AIP, ELENCO_INDICI_AIP_FIRMA_IN_CORSO, AIP_CREATI, COMPLETATO);
        }

        public static ElencoStatusEnum[] getStatoElencoDeletable() {
            return getEnums(APERTO, CHIUSO, DA_CHIUDERE);
        }
    }

    public enum FascStatusEnum {

        IN_ATTESA_MEMORIZZAZIONE, IN_ATTESA_SCHED, IN_ELENCO_APERTO, IN_ELENCO_CHIUSO, IN_ELENCO_COMPLETATO,
        IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO, IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA,
        IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO, IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS,
        IN_ELENCO_CON_INDICI_AIP_GENERATI, IN_ELENCO_DA_CHIUDERE, IN_ELENCO_FIRMATO, IN_ELENCO_IN_CODA_INDICE_AIP,
        NON_SELEZ_SCHED;

        public static FascStatusEnum[] getEnums(FascStatusEnum... vals) {
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
        INDICE_ELENCO(".xml"), FIRMA_INDICE_ELENCO(".xml.p7m"), ELENCO_INDICI_AIP(".xml"),
        FIRMA_ELENCO_INDICI_AIP(".xml.p7m");

        String fileExtension;

        private FileTypeEnum(String fileExtension) {
            this.fileExtension = fileExtension;
        }

        public static FileTypeEnum[] getEnums(FileTypeEnum... vals) {
            return vals;
        }

        public static FileTypeEnum[] getIndiceFileTypes() {
            return getEnums(INDICE_ELENCO, FIRMA_INDICE_ELENCO);
        }

        public static FileTypeEnum[] getElencoIndiciFileTypes() {
            return getEnums(ELENCO_INDICI_AIP, FIRMA_ELENCO_INDICI_AIP);
        }

        public static String[] getStringEnumsList(FileTypeEnum[] enums) {
            List<String> stringEnumsList = new ArrayList<>();
            for (FileTypeEnum fileTypeEnum : enums) {
                stringEnumsList.add(fileTypeEnum.name());
            }
            return stringEnumsList.toArray(new String[stringEnumsList.size()]);
        }

        public String getFileExtension() {
            return this.fileExtension;
        }
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

        ELENCO_FULL("Raggiunto il numero massimo di componenti"),
        // FASC_SURPLUS("L'aggiunta all'elenco di una ulteriore fascicolo supererebbe il numero massimo di fascicoli"),
        ELENCO_EXPIRED("Elenco di versamento fascicoli scaduto"), MANUAL_CLOSING("Chiusura manuale");

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

    public enum StatoGenerazioneIndiceAip {
        IN_ATTESA_SCHED, NON_SELEZ_SCHED, IN_ELENCO_APERTO, IN_ELENCO_CHIUSO, IN_ELENCO_DA_CHIUDERE, IN_ELENCO_FIRMATO,
        IN_ELENCO_IN_CODA_INDICE_AIP, IN_ELENCO_CON_INDICI_AIP_GENERATI
    }
}
