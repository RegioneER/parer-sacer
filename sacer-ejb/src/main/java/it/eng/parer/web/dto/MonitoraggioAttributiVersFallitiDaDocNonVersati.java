/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.web.dto;

import java.math.BigDecimal;

/**
 *
 * @author Gilioli_P
 */
public class MonitoraggioAttributiVersFallitiDaDocNonVersati {

    private String cdRegistroKeyUnitaDoc;
    private BigDecimal aaKeyUnitaDoc;
    private String cdKeyUnitaDoc;
    private String cdKeyDocVers;
    private BigDecimal idStrut;
    private String tipoVers;

    /**
     * @return the cdRegistroKeyUnitaDoc
     */
    public String getCdRegistroKeyUnitaDoc() {
        return cdRegistroKeyUnitaDoc;
    }

    /**
     * @param cdRegistroKeyUnitaDoc
     *            the cdRegistroKeyUnitaDoc to set
     */
    public void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
        this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
    }

    /**
     * @return the aaKeyUnitaDoc
     */
    public BigDecimal getAaKeyUnitaDoc() {
        return aaKeyUnitaDoc;
    }

    /**
     * @param aaKeyUnitaDoc
     *            the aaKeyUnitaDoc to set
     */
    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    /**
     * @return the cdKeyUnitaDoc
     */
    public String getCdKeyUnitaDoc() {
        return cdKeyUnitaDoc;
    }

    /**
     * @param cdKeyUnitaDoc
     *            the cdKeyUnitaDoc to set
     */
    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
        this.cdKeyUnitaDoc = cdKeyUnitaDoc;
    }

    /**
     * @return the cdKeyDocVers
     */
    public String getCdKeyDocVers() {
        return cdKeyDocVers;
    }

    /**
     * @param cdKeyDocVers
     *            the cdKeyDocVers to set
     */
    public void setCdKeyDocVers(String cdKeyDocVers) {
        this.cdKeyDocVers = cdKeyDocVers;
    }

    /**
     * @return the idStrut
     */
    public BigDecimal getIdStrut() {
        return idStrut;
    }

    /**
     * @param idStrut
     *            the idStrut to set
     */
    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    /**
     * @return the tipoVers
     */
    public String getTipoVers() {
        return tipoVers;
    }

    /**
     * @param tipoVers
     *            the tipoVers to set
     */
    public void setTipoVers(String tipoVers) {
        this.tipoVers = tipoVers;
    }

}
