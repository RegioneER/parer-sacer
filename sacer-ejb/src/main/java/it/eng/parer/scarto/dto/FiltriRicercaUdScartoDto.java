package it.eng.parer.scarto.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Data Transfer Object per la gestione dei filtri di ricerca delle Unità Documentarie da inserire
 * nella Proposta di Scarto.
 */
public class FiltriRicercaUdScartoDto {

    private BigDecimal idStrut;
    private BigDecimal registro;
    private BigDecimal anno;
    private BigDecimal annoDa;
    private BigDecimal annoA;
    private String numeroUd;
    private String numeroDa;
    private String numeroA;
    private Date dataUd;
    private Date dataUdDa;
    private Date dataUdA;
    private String oggettoUd;
    private BigDecimal tipologiaUd;
    private String classifica;
    private BigDecimal tempoConservazione;
    private String illimitato;
    private String tempoSuperato;
    private boolean includiFascicoli;
    private boolean includiSerie;

    // --- Costruttore ---
    public FiltriRicercaUdScartoDto() {
        super();
    }

    // --- Getters e Setters ---

    public BigDecimal getIdStrut() {
        return idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    public BigDecimal getRegistro() {
        return registro;
    }

    public void setRegistro(BigDecimal registro) {
        this.registro = registro;
    }

    public BigDecimal getAnno() {
        return anno;
    }

    public void setAnno(BigDecimal anno) {
        this.anno = anno;
    }

    public BigDecimal getAnnoDa() {
        return annoDa;
    }

    public void setAnnoDa(BigDecimal annoDa) {
        this.annoDa = annoDa;
    }

    public BigDecimal getAnnoA() {
        return annoA;
    }

    public void setAnnoA(BigDecimal annoA) {
        this.annoA = annoA;
    }

    public String getNumeroUd() {
        return numeroUd;
    }

    public void setNumeroUd(String numeroUd) {
        this.numeroUd = numeroUd;
    }

    public String getNumeroDa() {
        return numeroDa;
    }

    public void setNumeroDa(String numeroDa) {
        this.numeroDa = numeroDa;
    }

    public String getNumeroA() {
        return numeroA;
    }

    public void setNumeroA(String numeroA) {
        this.numeroA = numeroA;
    }

    public Date getDataUd() {
        return dataUd;
    }

    public void setDataUd(Date dataUd) {
        this.dataUd = dataUd;
    }

    public Date getDataUdDa() {
        return dataUdDa;
    }

    public void setDataUdDa(Date dataUdDa) {
        this.dataUdDa = dataUdDa;
    }

    public Date getDataUdA() {
        return dataUdA;
    }

    public void setDataUdA(Date dataUdA) {
        this.dataUdA = dataUdA;
    }

    public String getOggettoUd() {
        return oggettoUd;
    }

    public void setOggettoUd(String oggettoUd) {
        this.oggettoUd = oggettoUd;
    }

    public BigDecimal getTipologiaUd() {
        return tipologiaUd;
    }

    public void setTipologiaUd(BigDecimal tipologiaUd) {
        this.tipologiaUd = tipologiaUd;
    }

    public String getClassifica() {
        return classifica;
    }

    public void setClassifica(String classifica) {
        this.classifica = classifica;
    }

    public BigDecimal getTempoConservazione() {
        return tempoConservazione;
    }

    public void setTempoConservazione(BigDecimal tempoConservazione) {
        this.tempoConservazione = tempoConservazione;
    }

    public String getIllimitato() {
        return illimitato;
    }

    public void setIllimitato(String illimitato) {
        this.illimitato = illimitato;
    }

    public String getTempoSuperato() {
        return tempoSuperato;
    }

    public void setTempoSuperato(String tempoSuperato) {
        this.tempoSuperato = tempoSuperato;
    }

    public boolean isIncludiFascicoli() {
        return includiFascicoli;
    }

    public void setIncludiFascicoli(boolean includiFascicoli) {
        this.includiFascicoli = includiFascicoli;
    }

    public boolean isIncludiSerie() {
        return includiSerie;
    }

    public void setIncludiSerie(boolean includiSerie) {
        this.includiSerie = includiSerie;
    }
}