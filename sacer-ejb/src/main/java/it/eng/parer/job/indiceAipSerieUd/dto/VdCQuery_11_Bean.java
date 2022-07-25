package it.eng.parer.job.indiceAipSerieUd.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author gilioli_p
 */
public class VdCQuery_11_Bean {

    private Long idVerSerie;
    private String cdTipoNotaSerie;
    private BigDecimal pgNotaVerSerie;
    private String dsNota;
    private Date dtNota;
    private String dsAutore;

    public Long getIdVerSerie() {
        return idVerSerie;
    }

    public void setIdVerSerie(Long idVerSerie) {
        this.idVerSerie = idVerSerie;
    }

    public String getCdTipoNotaSerie() {
        return cdTipoNotaSerie;
    }

    public void setCdTipoNotaSerie(String cdTipoNotaSerie) {
        this.cdTipoNotaSerie = cdTipoNotaSerie;
    }

    public BigDecimal getPgNotaVerSerie() {
        return pgNotaVerSerie;
    }

    public void setPgNotaVerSerie(BigDecimal pgNotaVerSerie) {
        this.pgNotaVerSerie = pgNotaVerSerie;
    }

    public String getDsNota() {
        return dsNota;
    }

    public void setDsNota(String dsNota) {
        this.dsNota = dsNota;
    }

    public Date getDtNota() {
        return dtNota;
    }

    public void setDtNota(Date dtNota) {
        this.dtNota = dtNota;
    }

    public String getDsAutore() {
        return dsAutore;
    }

    public void setDsAutore(String dsAutore) {
        this.dsAutore = dsAutore;
    }

}
