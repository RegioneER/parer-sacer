/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.versamento.dto;

import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipoAlgoritmoRappr;
import it.eng.parer.ws.versamentoMM.dto.ComponenteMM;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author Fioravanti_F
 */
public class ComponenteVers implements java.io.Serializable, IDatiSpecEntity {

    public enum TipiSupporto {

        FILE, METADATI, RIFERIMENTO
    }

    // tag <id>
    private String id;
    private String chiaveComp;
    private boolean datiLetti;
    private TipiSupporto tipoSupporto;
    private boolean presenteRifMeta;
    // riferimento al ComponenteMM: questo è non null solo se il versamento è di tipo VersamentoMM
    private ComponenteMM rifComponenteMM = null;
    // i componenti sono:
    // 1.SottoComponente = NULL
    // 2.rifComponenteVersPadre = NULL
    // i sottocomponenti sono:
    // 1.SottoComponente è valorizzato
    // 2.rifComponenteVersPadre è valorizzato
    private DocumentoVers rifDocumentoVers;
    private ComponenteVers rifComponenteVersPadre;
    private long IdTipoComponente;
    private long idTipoRappresentazioneComponente;
    // se idTipoRappresentazioneComponente è valorizzato,
    // tengo anche i formati di file attesi per contenuto e convertitore
    private long idFormatoFileDocCont;
    private long idFormatoFileDocConv;
    private boolean nonAccettareForzaFormato;
    private CostantiDB.TipoAlgoritmoRappr algoritmoRappr;
    //
    private String tipoUso;
    // Proprietà necessarie per controllo su firme e marche
    private Date tmRifTempVers;
    private String flRifTempDataFirmaVers;
    //
    private long idFormatoFileVers;
    private boolean formatoFileVersNonAmmesso;
    private String descFormatoFileVers;
    // id salvataggio di se stesso su db
    private long idRecDB;
    // ordine di presentazione del componente nel documento
    private long ordinePresentazione;
    // riferimento al file binario versato nella servlet
    // oppure dal versamento Multimedia
    private FileBinario rifFileBinario;
    //
    private byte[] hashCalcolato;
    //
    private boolean hashForzato = false;
    private boolean hashVersNonDefinito = false;
    private byte[] hashVersato;
    private CostantiDB.TipiHash hashVersatoAlgoritmo;
    private CostantiDB.TipiEncBinari hashVersatoEncoding;
    //
    // riferimento alle classi di xml di risposta utilizzate per salvare le informazioni di dimensione file
    // NOTA BENE: solo uno tra componente e sottocomponente è valorizzato
    // riferimento all'entity del componente
    private AroCompDoc acdEntity;
    // riferimeno all'UD che contiene eventualmente il foglio-stile o xslt
    private long idUnitaDocRif;
    // dati specifici del componente o del sottocomnponente
    private HashMap<String, DatoSpecifico> datiSpecifici;
    private HashMap<String, DatoSpecifico> datiSpecificiMigrazione;
    // riferimento al record dell'XSD dei dati specifici e di migrazione
    private long idRecXsdDatiSpec;
    private long idRecXsdDatiSpecMigrazione;
    // nome del file da salvare, nel caso si debba usare la memorizzazione basata su file system
    private String nomeFileArk;

    public ComponenteVers() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChiaveComp() {
        return chiaveComp;
    }

    public void setChiaveComp(String chiaveComp) {
        this.chiaveComp = chiaveComp;
    }

    public boolean isDatiLetti() {
        return datiLetti;
    }

    public void setDatiLetti(boolean datiLetti) {
        this.datiLetti = datiLetti;
    }

    public TipiSupporto getTipoSupporto() {
        return tipoSupporto;
    }

    public void setTipoSupporto(TipiSupporto tipoSupporto) {
        this.tipoSupporto = tipoSupporto;
    }

    public boolean isPresenteRifMeta() {
        return presenteRifMeta;
    }

    public void setPresenteRifMeta(boolean presenteRifMeta) {
        this.presenteRifMeta = presenteRifMeta;
    }

    public ComponenteMM getRifComponenteMM() {
        return rifComponenteMM;
    }

    public void setRifComponenteMM(ComponenteMM rifComponenteMM) {
        this.rifComponenteMM = rifComponenteMM;
    }

    public DocumentoVers getRifDocumentoVers() {
        return rifDocumentoVers;
    }

    public void setRifDocumentoVers(DocumentoVers rifDocumentoVers) {
        this.rifDocumentoVers = rifDocumentoVers;
    }

    public ComponenteVers getRifComponenteVersPadre() {
        return rifComponenteVersPadre;
    }

    public void setRifComponenteVersPadre(ComponenteVers rifComponenteVersPadre) {
        this.rifComponenteVersPadre = rifComponenteVersPadre;
    }

    public long getIdTipoComponente() {
        return IdTipoComponente;
    }

    public void setIdTipoComponente(long IdTipoComponente) {
        this.IdTipoComponente = IdTipoComponente;
    }

    public long getIdTipoRappresentazioneComponente() {
        return idTipoRappresentazioneComponente;
    }

    public void setIdTipoRappresentazioneComponente(long idTipoRappresentazioneComponente) {
        this.idTipoRappresentazioneComponente = idTipoRappresentazioneComponente;
    }

    public long getIdFormatoFileDocCont() {
        return idFormatoFileDocCont;
    }

    public void setIdFormatoFileDocCont(long idFormatoFileDocCont) {
        this.idFormatoFileDocCont = idFormatoFileDocCont;
    }

    public long getIdFormatoFileDocConv() {
        return idFormatoFileDocConv;
    }

    public void setIdFormatoFileDocConv(long idFormatoFileDocConv) {
        this.idFormatoFileDocConv = idFormatoFileDocConv;
    }

    public boolean isNonAccettareForzaFormato() {
        return nonAccettareForzaFormato;
    }

    public void setNonAccettareForzaFormato(boolean nonAccettareForzaFormato) {
        this.nonAccettareForzaFormato = nonAccettareForzaFormato;
    }

    public TipoAlgoritmoRappr getAlgoritmoRappr() {
        return algoritmoRappr;
    }

    public void setAlgoritmoRappr(TipoAlgoritmoRappr algoritmoRappr) {
        this.algoritmoRappr = algoritmoRappr;
    }

    public String getTipoUso() {
        return tipoUso;
    }

    public void setTipoUso(String tipoUso) {
        this.tipoUso = tipoUso;
    }

    public Date getTmRifTempVers() {
        return tmRifTempVers;
    }

    public void setTmRifTempVers(Date tmRifTempVers) {
        this.tmRifTempVers = tmRifTempVers;
    }

    public String getFlRifTempDataFirmaVers() {
        return flRifTempDataFirmaVers;
    }

    public void setFlRifTempDataFirmaVers(String flRifTempDataFirmaVers) {
        this.flRifTempDataFirmaVers = flRifTempDataFirmaVers;
    }

    public long getIdFormatoFileVers() {
        return idFormatoFileVers;
    }

    public void setIdFormatoFileVers(long idFormatoFileVers) {
        this.idFormatoFileVers = idFormatoFileVers;
    }

    public boolean isFormatoFileVersNonAmmesso() {
        return formatoFileVersNonAmmesso;
    }

    public void setFormatoFileVersNonAmmesso(boolean formatoFileVersNonAmmesso) {
        this.formatoFileVersNonAmmesso = formatoFileVersNonAmmesso;
    }

    public String getDescFormatoFileVers() {
        return descFormatoFileVers;
    }

    public void setDescFormatoFileVers(String descFormatoFileVers) {
        this.descFormatoFileVers = descFormatoFileVers;
    }

    public long getIdRecDB() {
        return idRecDB;
    }

    public void setIdRecDB(long idRecDB) {
        this.idRecDB = idRecDB;
    }

    public long getOrdinePresentazione() {
        return ordinePresentazione;
    }

    public void setOrdinePresentazione(long ordinePresentazione) {
        this.ordinePresentazione = ordinePresentazione;
    }

    public FileBinario getRifFileBinario() {
        return rifFileBinario;
    }

    public void setRifFileBinario(FileBinario rifFileBinario) {
        this.rifFileBinario = rifFileBinario;
    }

    public byte[] getHashCalcolato() {
        return hashCalcolato;
    }

    public void setHashCalcolato(byte[] hashCalcolato) {
        this.hashCalcolato = hashCalcolato;
    }

    public boolean isHashForzato() {
        return hashForzato;
    }

    public void setHashForzato(boolean hashForzato) {
        this.hashForzato = hashForzato;
    }

    public boolean isHashVersNonDefinito() {
        return hashVersNonDefinito;
    }

    public void setHashVersNonDefinito(boolean hashVersNonDefinito) {
        this.hashVersNonDefinito = hashVersNonDefinito;
    }

    public byte[] getHashVersato() {
        return hashVersato;
    }

    public void setHashVersato(byte[] hashVersato) {
        this.hashVersato = hashVersato;
    }

    public CostantiDB.TipiHash getHashVersatoAlgoritmo() {
        return hashVersatoAlgoritmo;
    }

    public void setHashVersatoAlgoritmo(CostantiDB.TipiHash hashVersatoAlgoritmo) {
        this.hashVersatoAlgoritmo = hashVersatoAlgoritmo;
    }

    public CostantiDB.TipiEncBinari getHashVersatoEncoding() {
        return hashVersatoEncoding;
    }

    public void setHashVersatoEncoding(CostantiDB.TipiEncBinari hashVersatoEncoding) {
        this.hashVersatoEncoding = hashVersatoEncoding;
    }

    public AroCompDoc getAcdEntity() {
        return acdEntity;
    }

    public void setAcdEntity(AroCompDoc acdEntity) {
        this.acdEntity = acdEntity;
    }

    public long getIdUnitaDocRif() {
        return idUnitaDocRif;
    }

    public void setIdUnitaDocRif(long idUnitaDocRif) {
        this.idUnitaDocRif = idUnitaDocRif;
    }

    public HashMap<String, DatoSpecifico> getDatiSpecifici() {
        return datiSpecifici;
    }

    @Override
    public void setDatiSpecifici(HashMap<String, DatoSpecifico> datiSpecifici) {
        this.datiSpecifici = datiSpecifici;
    }

    @Override
    public HashMap<String, DatoSpecifico> getDatiSpecificiMigrazione() {
        return datiSpecificiMigrazione;
    }

    @Override
    public void setDatiSpecificiMigrazione(HashMap<String, DatoSpecifico> datiSpecificiMigrazione) {
        this.datiSpecificiMigrazione = datiSpecificiMigrazione;
    }

    @Override
    public long getIdRecXsdDatiSpec() {
        return idRecXsdDatiSpec;
    }

    @Override
    public void setIdRecXsdDatiSpec(long idRecXsdDatiSpec) {
        this.idRecXsdDatiSpec = idRecXsdDatiSpec;
    }

    @Override
    public long getIdRecXsdDatiSpecMigrazione() {
        return idRecXsdDatiSpecMigrazione;
    }

    @Override
    public void setIdRecXsdDatiSpecMigrazione(long idRecXsdDatiSpecMigrazione) {
        this.idRecXsdDatiSpecMigrazione = idRecXsdDatiSpecMigrazione;
    }

    public String getNomeFileArk() {
        return nomeFileArk;
    }

    public void setNomeFileArk(String nomeFileArk) {
        this.nomeFileArk = nomeFileArk;
    }

}
