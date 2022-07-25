package it.eng.parer.annulVers.dto;

import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Gilioli_P
 */
public class FascBean {

    private BigDecimal idStrut;
    private BigDecimal anno;
    private String numero;

    public FascBean(BigDecimal idStrut, BigDecimal anno, String numero) {
        this.idStrut = idStrut;
        this.anno = anno;
        this.numero = numero;
    }

    public BigDecimal getIdStrut() {
        return idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
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

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FascBean other = (FascBean) obj;
        if (this.getIdStrut().compareTo(other.getIdStrut()) != 0) {
            return false;
        }
        if (this.getAnno().compareTo(other.getAnno()) != 0) {
            return false;
        }
        if (!StringUtils.equals(this.getNumero(), other.getNumero())) {
            return false;
        }

        return true;
    }

}
