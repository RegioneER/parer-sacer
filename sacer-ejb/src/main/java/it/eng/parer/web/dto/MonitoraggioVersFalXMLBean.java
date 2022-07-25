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
public class MonitoraggioVersFalXMLBean {

    private BigDecimal idSessioneVers;
    private String blXml;

    /**
     * @return the idSessioneVers
     */
    public BigDecimal getIdSessioneVers() {
        return idSessioneVers;
    }

    /**
     * @param idSessioneVers
     *            the idSessioneVers to set
     */
    public void setIdSessioneVers(BigDecimal idSessioneVers) {
        this.idSessioneVers = idSessioneVers;
    }

    /**
     * @return the blXml
     */
    public String getBlXml() {
        return blXml;
    }

    /**
     * @param blXml
     *            the blXml to set
     */
    public void setBlXml(String blXml) {
        this.blXml = blXml;
    }

}
