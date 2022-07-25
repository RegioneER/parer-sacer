/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.recuperoDip.dto;

import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.recupero.dto.ComponenteRec;
import it.eng.parer.ws.utils.CostantiDB;
import java.util.Date;

/**
 *
 * @author Fioravanti_F
 */
public class CompRecDip extends ComponenteRec {

    protected long idCompConvertitore;
    protected long idFileTrasform;
    protected String nomeFormatoRappresentazione;
    protected String nomeConvertitore;
    protected String versioneConvertitore;
    protected Date dataUltimoAggiornamento;
    protected CostantiDB.TipoAlgoritmoRappr tipoAlgoritmoRappresentazione;
    protected CostantiDB.StatoFileTrasform statoFileTrasform;
    protected IRispostaWS.SeverityEnum severity = IRispostaWS.SeverityEnum.OK;
    protected String errorMessage;
    protected boolean erroreFormatoContenuto;
    protected String dsFormatoContAtteso;
    protected String dsFormatoContReale;
    //
    protected String dsAlgoHashFileCalc;

    public CompRecDip() {
        super();
    }

    public CompRecDip(String urnCompleto, String urnCompletoIniziale) {
        super(urnCompleto, urnCompletoIniziale);
    }

    public long getIdCompConvertitore() {
        return idCompConvertitore;
    }

    public void setIdCompConvertitore(long idCompConvertitore) {
        this.idCompConvertitore = idCompConvertitore;
    }

    public long getIdFileTrasform() {
        return idFileTrasform;
    }

    public void setIdFileTrasform(long idFileTrasform) {
        this.idFileTrasform = idFileTrasform;
    }

    public String getNomeFormatoRappresentazione() {
        return nomeFormatoRappresentazione;
    }

    public void setNomeFormatoRappresentazione(String nomeFormatoRappresentazione) {
        this.nomeFormatoRappresentazione = nomeFormatoRappresentazione;
    }

    public String getNomeConvertitore() {
        return nomeConvertitore;
    }

    public void setNomeConvertitore(String nomeConvertitore) {
        this.nomeConvertitore = nomeConvertitore;
    }

    public String getVersioneConvertitore() {
        return versioneConvertitore;
    }

    public void setVersioneConvertitore(String versioneConvertitore) {
        this.versioneConvertitore = versioneConvertitore;
    }

    public Date getDataUltimoAggiornamento() {
        return dataUltimoAggiornamento;
    }

    public void setDataUltimoAggiornamento(Date dataUltimoAggiornamento) {
        this.dataUltimoAggiornamento = dataUltimoAggiornamento;
    }

    public CostantiDB.TipoAlgoritmoRappr getTipoAlgoritmoRappresentazione() {
        return tipoAlgoritmoRappresentazione;
    }

    public void setTipoAlgoritmoRappresentazione(CostantiDB.TipoAlgoritmoRappr tipoAlgoritmoRappresentazione) {
        this.tipoAlgoritmoRappresentazione = tipoAlgoritmoRappresentazione;
    }

    public CostantiDB.StatoFileTrasform getStatoFileTrasform() {
        return statoFileTrasform;
    }

    public void setStatoFileTrasform(CostantiDB.StatoFileTrasform statoFileTrasform) {
        this.statoFileTrasform = statoFileTrasform;
    }

    public IRispostaWS.SeverityEnum getSeverity() {
        return severity;
    }

    public void setSeverity(IRispostaWS.SeverityEnum severity) {
        this.severity = severity;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isErroreFormatoContenuto() {
        return erroreFormatoContenuto;
    }

    public void setErroreFormatoContenuto(boolean erroreFormatoContenuto) {
        this.erroreFormatoContenuto = erroreFormatoContenuto;
    }

    public String getDsFormatoContAtteso() {
        return dsFormatoContAtteso;
    }

    public void setDsFormatoContAtteso(String dsFormatoContAtteso) {
        this.dsFormatoContAtteso = dsFormatoContAtteso;
    }

    public String getDsFormatoContReale() {
        return dsFormatoContReale;
    }

    public void setDsFormatoContReale(String dsFormatoContReale) {
        this.dsFormatoContReale = dsFormatoContReale;
    }

    /**
     * @return the dsAlgoHashFileCalc
     */
    public String getDsAlgoHashFileCalc() {
        return dsAlgoHashFileCalc;
    }

    /**
     * @param dsAlgoHashFileCalc
     *            the dsAlgoHashFileCalc to set
     */
    public void setDsAlgoHashFileCalc(String dsAlgoHashFileCalc) {
        this.dsAlgoHashFileCalc = dsAlgoHashFileCalc;
    }

}
