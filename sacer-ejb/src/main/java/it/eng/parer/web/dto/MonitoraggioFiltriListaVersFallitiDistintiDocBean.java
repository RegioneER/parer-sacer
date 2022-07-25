package it.eng.parer.web.dto;

import java.math.BigDecimal;
import java.util.Set;

/**
 *
 * @author Gilioli_P
 */
public class MonitoraggioFiltriListaVersFallitiDistintiDocBean {

    private BigDecimal idAmbiente;
    private BigDecimal idEnte;
    private BigDecimal idStrut;
    private BigDecimal idUserIam;
    private String tipoLista;
    private String flVerificato;
    private String flNonRisolub;
    private Set<String> registro;
    private BigDecimal anno;
    private String numero;
    private BigDecimal anno_range_da;
    private BigDecimal anno_range_a;
    private String numero_range_da;
    private String numero_range_a;
    private String classeErrore;
    private String sottoClasseErrore;
    private String codiceErrore;

    public BigDecimal getIdAmbiente() {
        return idAmbiente;
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    public BigDecimal getIdEnte() {
        return idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
        this.idEnte = idEnte;
    }

    public BigDecimal getIdStrut() {
        return idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    public String getTipoLista() {
        return tipoLista;
    }

    public void setTipoLista(String tipoLista) {
        this.tipoLista = tipoLista;
    }

    public String getFlVerificato() {
        return flVerificato;
    }

    public void setFlVerificato(String flVerificato) {
        this.flVerificato = flVerificato;
    }

    public String getFlNonRisolub() {
        return flNonRisolub;
    }

    public void setFlNonRisolub(String flNonRisolub) {
        this.flNonRisolub = flNonRisolub;
    }

    public Set<String> getRegistro() {
        return registro;
    }

    public void setRegistro(Set<String> registro) {
        this.registro = registro;
    }

    public BigDecimal getAnno() {
        return anno;
    }

    public void setAnno(BigDecimal anno) {
        this.anno = anno;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public BigDecimal getAnno_range_da() {
        return anno_range_da;
    }

    public void setAnno_range_da(BigDecimal anno_range_da) {
        this.anno_range_da = anno_range_da;
    }

    public BigDecimal getAnno_range_a() {
        return anno_range_a;
    }

    public void setAnno_range_a(BigDecimal anno_range_a) {
        this.anno_range_a = anno_range_a;
    }

    public String getNumero_range_da() {
        return numero_range_da;
    }

    public void setNumero_range_da(String numero_range_da) {
        this.numero_range_da = numero_range_da;
    }

    public String getNumero_range_a() {
        return numero_range_a;
    }

    public void setNumero_range_a(String numero_range_a) {
        this.numero_range_a = numero_range_a;
    }

    public BigDecimal getIdUserIam() {
        return idUserIam;
    }

    public void setIdUserIam(BigDecimal idUserIam) {
        this.idUserIam = idUserIam;
    }

    public String getClasseErrore() {
        return classeErrore;
    }

    public void setClasseErrore(String classeErrore) {
        this.classeErrore = classeErrore;
    }

    public String getSottoClasseErrore() {
        return sottoClasseErrore;
    }

    public void setSottoClasseErrore(String sottoClasseErrore) {
        this.sottoClasseErrore = sottoClasseErrore;
    }

    public String getCodiceErrore() {
        return codiceErrore;
    }

    public void setCodiceErrore(String codiceErrore) {
        this.codiceErrore = codiceErrore;
    }
}
