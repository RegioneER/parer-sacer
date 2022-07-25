package it.eng.parer.web.dto;

import java.math.BigDecimal;

/**
 *
 * @author gilioli_p
 */
public class SerieDaFirmareBean {

    private String ambEnteStrut;
    private String cdCompositoSerie;
    private BigDecimal aaSerie;
    private String dsSerie;
    private String nmTipoSerie;
    private String cdVerSerie;
    private String rangeDate;

    public String getAmbEnteStrut() {
        return ambEnteStrut;
    }

    public void setAmbEnteStrut(String ambEnteStrut) {
        this.ambEnteStrut = ambEnteStrut;
    }

    public String getCdCompositoSerie() {
        return cdCompositoSerie;
    }

    public void setCdCompositoSerie(String cdCompositoSerie) {
        this.cdCompositoSerie = cdCompositoSerie;
    }

    public BigDecimal getAaSerie() {
        return aaSerie;
    }

    public void setAaSerie(BigDecimal aaSerie) {
        this.aaSerie = aaSerie;
    }

    public String getDsSerie() {
        return dsSerie;
    }

    public void setDsSerie(String dsSerie) {
        this.dsSerie = dsSerie;
    }

    public String getNmTipoSerie() {
        return nmTipoSerie;
    }

    public void setNmTipoSerie(String nmTipoSerie) {
        this.nmTipoSerie = nmTipoSerie;
    }

    public String getCdVerSerie() {
        return cdVerSerie;
    }

    public void setCdVerSerie(String cdVerSerie) {
        this.cdVerSerie = cdVerSerie;
    }

    public String getRangeDate() {
        return rangeDate;
    }

    public void setRangeDate(String rangeDate) {
        this.rangeDate = rangeDate;
    }

}
