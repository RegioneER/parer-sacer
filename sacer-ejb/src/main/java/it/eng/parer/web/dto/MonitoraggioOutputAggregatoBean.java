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
public class MonitoraggioOutputAggregatoBean {

    private String tiOper;
    private String numeroOperazioni;

    /**
     * @return the tiOper
     */
    public String getTiOper() {
        return tiOper;
    }

    /**
     * @param tiOper
     *            the tiOper to set
     */
    public void setTiOper(String tiOper) {
        this.tiOper = tiOper;
    }

    /**
     * @return the numeroOperazioni
     */
    public String getNumeroOperazioni() {
        return numeroOperazioni;
    }

    /**
     * @param numeroOperazioni
     *            the numeroOperazioni to set
     */
    public void setNumeroOperazioni(String numeroOperazioni) {
        this.numeroOperazioni = numeroOperazioni;
    }

}
