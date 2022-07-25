package it.eng.parer.web.dto;

import java.util.List;

/**
 * Copia modificata della entity JPA DecCriterioDatiSpec per la gestione dei dati specifici in fase di costruzione query
 * 
 * @author Gilioli_P
 */
public class DecCriterioDatiSpecBean {

    private long idCriterioDatiSpec;
    private String dlValore;
    private String nmAttribDatiSpec;
    private String tiOper;
    private List<DecCriterioAttribBean> decCriterioAttribs;

    public DecCriterioDatiSpecBean() {
    }

    public long getIdCriterioDatiSpec() {
        return this.idCriterioDatiSpec;
    }

    public void setIdCriterioDatiSpec(long idCriterioDatiSpec) {
        this.idCriterioDatiSpec = idCriterioDatiSpec;
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

    public List<DecCriterioAttribBean> getDecCriterioAttribs() {
        return this.decCriterioAttribs;
    }

    public void setDecCriterioAttribs(List<DecCriterioAttribBean> decCriterioAttribs) {
        this.decCriterioAttribs = decCriterioAttribs;
    }
}