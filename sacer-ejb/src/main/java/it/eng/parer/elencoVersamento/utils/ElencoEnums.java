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

package it.eng.parer.elencoVersamento.utils;

import java.util.ArrayList;
import java.util.List;

import it.eng.parer.ws.utils.CostantiDB.TipiHash;

/**
 *
 * @author Agati_D
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
	AGGIUNGI_DOC_ELENCO, ELIMINA_ELENCO, RIMUOVI_UD_ELENCO, RIMUOVI_DOC_ELENCO,
	RIMUOVI_UPD_UD_ELENCO, MOD_ELENCO, VALIDAZIONE_ELENCO, ERRORE, REVOCA_CA,
	CREAZIONE_INDICI_ELENCHI_VERS, CREA_INDICE_ELENCO, VERIFICA_FIRME_A_DATA_VERS,
	PRODUCER_CODA_INDICI_AIP_DA_ELAB, INIZIO_CREA_INDICE, INIZIO_VERIF_FIRME, ERR_VERIF_FIRME,
	SET_ELENCO_DA_CHIUDERE, RECUPERA_ELENCO_APERTO, RECUPERA_ELENCO_SCADUTO,
	RECUPERA_ELENCO_IN_ERRORE, SET_ELENCO_IN_ERRORE, SET_ELENCO_APERTO, VERIFICA_VERS_FALLITI,
	DEF_NOTE_INDICE_ELENCO, DEF_NOTE_ELENCO_CHIUSO, START_CREA_ELENCO_INDICI_AIP,
	END_CREA_ELENCO_INDICI_AIP, FIRMA_ELENCO_INDICI_AIP, FIRMA_ELENCO_INDICI_AIP_IN_CORSO,
	FIRMA_ELENCO_INDICI_AIP_FALLITA, FIRMA_IN_CORSO, FIRMA_IN_CORSO_FALLITA,
	MARCA_ELENCO_INDICI_AIP_FALLITA, MARCA_ELENCO_INDICI_AIP
    }

    public enum GestioneElencoEnum {
	FIRMA, NO_FIRMA, MARCA_FIRMA, SIGILLO, MARCA_SIGILLO
    }

    public enum ElencoStatusEnum {

	COMPLETATO, APERTO, CHIUSO, DA_CHIUDERE, VALIDATO, ELENCO_INDICI_AIP_CREATO,
	ELENCO_INDICI_AIP_ERR_MARCA, ELENCO_INDICI_AIP_FIRMA_IN_CORSO, ELENCO_INDICI_AIP_FIRMATO,
	FIRMA_IN_CORSO, FIRME_VERIFICATE_DT_VERS, INDICI_AIP_GENERATI, IN_CODA_INDICE_AIP,
	IN_CODA_JMS_VERIFICA_FIRME_DT_VERS, IN_CODA_JMS_GENERA_INDICE_AIP,
	// MAC#16424
	IN_CODA_JMS_INDICE_AIP_DA_ELAB;

	public static ElencoStatusEnum[] getEnums(ElencoStatusEnum... vals) {
	    return vals;
	}

	public static ElencoStatusEnum[] getComboMappaStatoElencoRicerca() {
	    return getEnums(APERTO, DA_CHIUDERE, CHIUSO, VALIDATO, FIRMA_IN_CORSO,
		    FIRME_VERIFICATE_DT_VERS, IN_CODA_JMS_INDICE_AIP_DA_ELAB, IN_CODA_INDICE_AIP,
		    INDICI_AIP_GENERATI, ELENCO_INDICI_AIP_CREATO, ELENCO_INDICI_AIP_FIRMA_IN_CORSO,
		    ELENCO_INDICI_AIP_FIRMATO, ELENCO_INDICI_AIP_ERR_MARCA,
		    IN_CODA_JMS_VERIFICA_FIRME_DT_VERS, IN_CODA_JMS_GENERA_INDICE_AIP, COMPLETATO);
	}

	public static ElencoStatusEnum[] getStatoElencoDeletable() {
	    return getEnums(APERTO, CHIUSO, DA_CHIUDERE);
	}

	public static String[] getStringEnumsList(ElencoStatusEnum[] enums) {
	    List<String> stringEnumsList = new ArrayList<>();
	    for (ElencoStatusEnum elencoStatusEnum : enums) {
		stringEnumsList.add(elencoStatusEnum.name());
	    }
	    return stringEnumsList.toArray(new String[stringEnumsList.size()]);
	}
    }

    public enum DocStatusEnum {
	/*
	 * ATTENZIONE: lo stato IN_ELENCO_IN_ERRORE non esiste nella lista dei vincoli sulla tabella
	 * select * from all_constraints where owner = 'SACER' and constraint_type ='C' and
	 * table_name = 'ARO_DOC' and constraint_name = 'TI_STATO_DOC_ELENCO_VERS';
	 */
	IN_ELENCO_IN_ERRORE, IN_ATTESA_MEMORIZZAZIONE, IN_ATTESA_SCHED, IN_ELENCO_APERTO,
	IN_ELENCO_CHIUSO, IN_ELENCO_COMPLETATO, IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO,
	IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA, IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO,
	IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS, IN_ELENCO_CON_INDICI_AIP_GENERATI,
	IN_ELENCO_DA_CHIUDERE, IN_ELENCO_VALIDATO, IN_ELENCO_IN_CODA_INDICE_AIP, NON_SELEZ_SCHED,
	IN_CODA_JMS_VERIFICA_FIRME_DT_VERS, IN_CODA_JMS_INDICE_AIP_DA_ELAB;

	public static DocStatusEnum[] getEnums(DocStatusEnum... vals) {
	    return vals;
	}

	public static DocStatusEnum[] getFilterEnums() {
	    return getEnums(IN_ELENCO_CHIUSO, IN_ELENCO_APERTO, IN_ATTESA_SCHED,
		    IN_ELENCO_DA_CHIUDERE, IN_ELENCO_VALIDATO, IN_ATTESA_MEMORIZZAZIONE,
		    NON_SELEZ_SCHED, IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS,
		    IN_ELENCO_IN_CODA_INDICE_AIP);
	}
    }

    public enum UdDocStatusEnum {

	IN_ATTESA_MEMORIZZAZIONE, IN_ATTESA_SCHED, IN_ELENCO_APERTO, IN_ELENCO_VALIDATO,
	IN_ELENCO_CHIUSO, IN_ELENCO_COMPLETATO, IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO,
	IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA, IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO,
	IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS, IN_ELENCO_CON_INDICI_AIP_GENERATI,
	IN_ELENCO_DA_CHIUDERE, IN_ELENCO_IN_CODA_INDICE_AIP, NON_SELEZ_SCHED,
	IN_CODA_JMS_VERIFICA_FIRME_DT_VERS, IN_CODA_JMS_INDICE_AIP_DA_ELAB;

	public static UdDocStatusEnum[] getEnums(UdDocStatusEnum... vals) {
	    return vals;
	}
    }

    public enum ExpirationTypeEnum {

	GIORNALIERA, SETTIMANALE, QUINDICINALE, MENSILE
    }

    public enum SignatureInfoEnum {

	TUTTI_SENZA_FIRMA, TUTTI_CON_FIRMA, CON_E_SENZA_FIRMA, TUTTI_FIRMA_VALIDA,
	TUTTI_FIRMA_INVALIDA, VALIDE_E_INVALIDE, POSITIVO, NEGATIVO, WARNING;

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
	INDICE(".xml"), FIRMA(".tsr.p7m"), INDICE_FIRMATO(".xml.p7m"), MARCA_INDICE(".tsr"),
	MARCA_FIRMA(".tsr"), ELENCO_INDICI_AIP(".xml"), FIRMA_ELENCO_INDICI_AIP(".xml.p7m"),
	MARCA_FIRMA_ELENCO_INDICI_AIP(".tsr");

	String fileExtension;

	private FileTypeEnum(String fileExtension) {
	    this.fileExtension = fileExtension;
	}

	public static FileTypeEnum[] getEnums(FileTypeEnum... vals) {
	    return vals;
	}

	public static FileTypeEnum[] getIndiceFileTypes() {
	    return getEnums(INDICE, FIRMA, INDICE_FIRMATO, MARCA_INDICE, MARCA_FIRMA);
	}

	public static FileTypeEnum[] getElencoIndiciFileTypes() {
	    return getEnums(ELENCO_INDICI_AIP, FIRMA_ELENCO_INDICI_AIP,
		    MARCA_FIRMA_ELENCO_INDICI_AIP);
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

    public enum TipoFirma {
	CADES, XADES
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

	ELENCO_FULL(
		"L'aggiunta di una unità documentaria o di un documento o di un aggiornamento metadati provoca il superamento del numero massimo di componenti"),
	// UD_SURPLUS("L'aggiunta all'elenco di una ulteriore unità documentaria supererebbe il
	// numero massimo di unità
	// documentarie"),
	// COMP_SURPLUS("L'aggiunta all'elenco di un ulteriore componente supererebbe il numero
	// massimo di componenti"),
	ELENCO_EXPIRED("Elenco di versamento scaduto"), MANUAL_CLOSING("Chiusura manuale"),
	ELENCO_CHIUSURA_ANTICIP("Chiusura anticipata per scadenza termini conservazione fiscale.”");

	private final String message;

	private MotivazioneChiusura(final String message) {
	    this.message = message;
	}

	public java.lang.String message() {
	    return this.message;
	}
    }

    public enum ElencoInfo {

	NON_DETERMINABILE, VERSIONE_ELENCO("3.1");

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

	DA_FARE_PING(AnnulStatusEnum.DA_ANNULLARE_PING),
	DA_FARE_SACER(AnnulStatusEnum.DA_ANNULLARE_SACER), OK(AnnulStatusEnum.ANNULLAMENTO_FATTO);

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
	IN_ATTESA_SCHED, NON_SELEZ_SCHED, IN_ELENCO_APERTO, IN_ELENCO_CHIUSO, IN_ELENCO_DA_CHIUDERE,
	IN_ELENCO_FIRMATO, IN_ELENCO_IN_CODA_INDICE_AIP, IN_ELENCO_CON_INDICI_AIP_GENERATI
    }
}
