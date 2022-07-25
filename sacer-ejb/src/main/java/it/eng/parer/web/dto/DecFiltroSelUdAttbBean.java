/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.web.dto;

import java.util.List;

/**
 *
 * @author Parucci_M
 */
public class DecFiltroSelUdAttbBean {

    private long idFiltroSelUdAttb;
    private String dlValore;
    private String nmAttribDatiSpec;
    private String tiOper;
    private List<DecFiltroSelUdDatoBean> decFiltroSelUdDatos;

    public DecFiltroSelUdAttbBean() {
    }

    public long getIdFiltroSelUdAttb() {
        return this.idFiltroSelUdAttb;
    }

    public void setIdFiltroSelUdAttb(long idFiltroSelUdAttb) {
        this.idFiltroSelUdAttb = idFiltroSelUdAttb;
    }

    public String getDlValore() {
        return this.dlValore;
    }

    public void setDlValore(String dlValore) {
        this.dlValore = dlValore;
    }

    public String getNmAttribDatiSpec() {
        return this.nmAttribDatiSpec;
    }

    public void setNmAttribDatiSpec(String nmAttribDatiSpec) {
        this.nmAttribDatiSpec = nmAttribDatiSpec;
    }

    public String getTiOper() {
        return this.tiOper;
    }

    public void setTiOper(String tiOper) {
        this.tiOper = tiOper;
    }

    public List<DecFiltroSelUdDatoBean> getDecFiltroSelUdDatos() {
        return this.decFiltroSelUdDatos;
    }

    public void setDecFiltroSelUdDatos(List<DecFiltroSelUdDatoBean> decFiltroSelUdDatos) {
        this.decFiltroSelUdDatos = decFiltroSelUdDatos;
    }
}