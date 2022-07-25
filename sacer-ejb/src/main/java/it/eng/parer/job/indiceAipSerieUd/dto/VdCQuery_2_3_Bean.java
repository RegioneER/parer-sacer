package it.eng.parer.job.indiceAipSerieUd.dto;

import java.math.BigDecimal;

/**
 *
 * @author gilioli_p
 */
public class VdCQuery_2_3_Bean {

    private Long idVerSerieCor;
    private String tiModLacuna;
    private BigDecimal niIniLacuna;
    private BigDecimal niFinLacuna;
    private String dlLacuna;
    private String dlNotaLacuna;

    public Long getIdVerSerieCor() {
        return idVerSerieCor;
    }

    public void setIdVerSerieCor(Long idVerSerieCor) {
        this.idVerSerieCor = idVerSerieCor;
    }

    public String getTiModLacuna() {
        return tiModLacuna;
    }

    public void setTiModLacuna(String tiModLacuna) {
        this.tiModLacuna = tiModLacuna;
    }

    public BigDecimal getNiIniLacuna() {
        return niIniLacuna;
    }

    public void setNiIniLacuna(BigDecimal niIniLacuna) {
        this.niIniLacuna = niIniLacuna;
    }

    public BigDecimal getNiFinLacuna() {
        return niFinLacuna;
    }

    public void setNiFinLacuna(BigDecimal niFinLacuna) {
        this.niFinLacuna = niFinLacuna;
    }

    public String getDlLacuna() {
        return dlLacuna;
    }

    public void setDlLacuna(String dlLacuna) {
        this.dlLacuna = dlLacuna;
    }

    public String getDlNotaLacuna() {
        return dlNotaLacuna;
    }

    public void setDlNotaLacuna(String dlNotaLacuna) {
        this.dlNotaLacuna = dlNotaLacuna;
    }

}
