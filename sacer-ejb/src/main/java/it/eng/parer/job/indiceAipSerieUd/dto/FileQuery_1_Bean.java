package it.eng.parer.job.indiceAipSerieUd.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author gilioli_p
 */
public class FileQuery_1_Bean {

    private Long idVerSerie;
    private BigDecimal pgVolVerSerie;
    private BigDecimal niUnitaDocVol;
    private String cdFirstUnitaDocVol;
    private Date dtFirstUnitaDocVol;
    private String cdLastUnitaDocVol;
    private Date dtLastUnitaDocVol;
    private String dsUrnIxVol;
    private String dsHashIxVol;

    public Long getIdVerSerie() {
        return idVerSerie;
    }

    public void setIdVerSerie(Long idVerSerie) {
        this.idVerSerie = idVerSerie;
    }

    public BigDecimal getPgVolVerSerie() {
        return pgVolVerSerie;
    }

    public void setPgVolVerSerie(BigDecimal pgVolVerSerie) {
        this.pgVolVerSerie = pgVolVerSerie;
    }

    public BigDecimal getNiUnitaDocVol() {
        return niUnitaDocVol;
    }

    public void setNiUnitaDocVol(BigDecimal niUnitaDocVol) {
        this.niUnitaDocVol = niUnitaDocVol;
    }

    public String getCdFirstUnitaDocVol() {
        return cdFirstUnitaDocVol;
    }

    public void setCdFirstUnitaDocVol(String cdFirstUnitaDocVol) {
        this.cdFirstUnitaDocVol = cdFirstUnitaDocVol;
    }

    public Date getDtFirstUnitaDocVol() {
        return dtFirstUnitaDocVol;
    }

    public void setDtFirstUnitaDocVol(Date dtFirstUnitaDocVol) {
        this.dtFirstUnitaDocVol = dtFirstUnitaDocVol;
    }

    public String getCdLastUnitaDocVol() {
        return cdLastUnitaDocVol;
    }

    public void setCdLastUnitaDocVol(String cdLastUnitaDocVol) {
        this.cdLastUnitaDocVol = cdLastUnitaDocVol;
    }

    public Date getDtLastUnitaDocVol() {
        return dtLastUnitaDocVol;
    }

    public void setDtLastUnitaDocVol(Date dtLastUnitaDocVol) {
        this.dtLastUnitaDocVol = dtLastUnitaDocVol;
    }

    public String getDsUrnIxVol() {
        return dsUrnIxVol;
    }

    public void setDsUrnIxVol(String dsUrnIxVol) {
        this.dsUrnIxVol = dsUrnIxVol;
    }

    public String getDsHashIxVol() {
        return dsHashIxVol;
    }

    public void setDsHashIxVol(String dsHashIxVol) {
        this.dsHashIxVol = dsHashIxVol;
    }
}