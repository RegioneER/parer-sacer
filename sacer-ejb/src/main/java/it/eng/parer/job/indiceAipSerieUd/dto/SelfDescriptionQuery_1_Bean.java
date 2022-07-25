package it.eng.parer.job.indiceAipSerieUd.dto;

import java.math.BigDecimal;

/**
 *
 * @author gilioli_p
 */
public class SelfDescriptionQuery_1_Bean {

    private Long idVerSerieCor;
    private BigDecimal pgVerSeriePrec;
    private String dsUrnFilePrec;
    private String dsHashFilePrec;

    public Long getIdVerSerieCor() {
        return idVerSerieCor;
    }

    public void setIdVerSerieCor(Long idVerSerieCor) {
        this.idVerSerieCor = idVerSerieCor;
    }

    public BigDecimal getPgVerSeriePrec() {
        return pgVerSeriePrec;
    }

    public void setPgVerSeriePrec(BigDecimal pgVerSeriePrec) {
        this.pgVerSeriePrec = pgVerSeriePrec;
    }

    public String getDsUrnFilePrec() {
        return dsUrnFilePrec;
    }

    public void setDsUrnFilePrec(String dsUrnFilePrec) {
        this.dsUrnFilePrec = dsUrnFilePrec;
    }

    public String getDsHashFilePrec() {
        return dsHashFilePrec;
    }

    public void setDsHashFilePrec(String dsHashFilePrec) {
        this.dsHashFilePrec = dsHashFilePrec;
    }

}
