/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.web.dto;

import it.eng.parer.entity.DecFiltroSelUdAttb;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Parucci_M
 */
public class DecFiltroSelUdDatoBean implements Serializable, Comparable<DecFiltroSelUdDatoBean> {

    private static final long serialVersionUID = 1L;
    private long idFiltroSelUdDato;
    private String dsListaVersioniXsd;
    private String nmTipoDoc;
    private String nmTipoUnitaDoc;
    private String tiEntitaSacer;
    private BigDecimal idAttribDatiSpec;
    private BigDecimal ordine;
    private DecFiltroSelUdAttb decCriterioDatiSpec;

    /*
     * 
     * private long idFiltroSelUdDato; private String dsListaVersioniXsd; private String nmTipoDoc; private String
     * nmTipoUnitaDoc; private String tiEntitaSacer; private DecAttribDatiSpec decAttribDatiSpec; private
     * DecFiltroSelUdAttb decFiltroSelUdAttb;
     * 
     * 
     * 
     */

    public DecFiltroSelUdDatoBean() {
    }

    public long getIdFiltroSelUdDato() {
        return this.idFiltroSelUdDato;
    }

    public void setIdFiltroSelUdDato(long idFiltroSelUdDato) {
        this.idFiltroSelUdDato = idFiltroSelUdDato;
    }

    public String getDsListaVersioniXsd() {
        return this.dsListaVersioniXsd;
    }

    public void setDsListaVersioniXsd(String dsListaVersioniXsd) {
        this.dsListaVersioniXsd = dsListaVersioniXsd;
    }

    public String getNmTipoDoc() {
        return this.nmTipoDoc;
    }

    public void setNmTipoDoc(String nmTipoDoc) {
        this.nmTipoDoc = nmTipoDoc;
    }

    public String getNmTipoUnitaDoc() {
        return this.nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
        this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    public String getTiEntitaSacer() {
        return this.tiEntitaSacer;
    }

    public void setTiEntitaSacer(String tiEntitaSacer) {
        this.tiEntitaSacer = tiEntitaSacer;
    }

    public DecFiltroSelUdAttb getDecCriterioDatiSpec() {
        return this.decCriterioDatiSpec;
    }

    public void setDecCriterioDatiSpec(DecFiltroSelUdAttb decCriterioDatiSpec) {
        this.decCriterioDatiSpec = decCriterioDatiSpec;
    }

    public BigDecimal getIdAttribDatiSpec() {
        return idAttribDatiSpec;
    }

    public void setIdAttribDatiSpec(BigDecimal idAttribDatiSpec) {
        this.idAttribDatiSpec = idAttribDatiSpec;
    }

    public BigDecimal getOrdine() {
        return ordine;
    }

    public void setOrdine(BigDecimal ordine) {
        this.ordine = ordine;
    }

    @Override
    public int compareTo(DecFiltroSelUdDatoBean o) {
        int result = this.ordine.compareTo(o.ordine);
        if (result == 0) {
            // Se sono tipi unità documentaria
            if (this.nmTipoUnitaDoc != null) {
                result = this.nmTipoUnitaDoc.compareTo(o.nmTipoUnitaDoc);
            } else if (this.nmTipoDoc != null) {
                result = this.nmTipoDoc.compareTo(o.nmTipoDoc);
            }
        } else {
            result = this.ordine.compareTo(o.ordine);
        }
        return result;
    }

}