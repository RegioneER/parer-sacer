package it.eng.parer.job.allineamentoOrganizzazioni.dto;

import it.eng.integriam.client.ws.reporg.ListaTipiDato;
import java.util.Date;

/**
 *
 * @author Gilioli_P
 */
public class ParametriInputOrganizzazioni {

    private String nmUserid;
    private String cdPsw;
    private String nmApplic;
    private Integer idOrganizApplic;
    private String nmTipoOrganiz;
    private Integer idOrganizApplicPadre;
    private String nmTipoOrganizPadre;
    private String nmOrganiz;
    private String dsOrganiz;
    private it.eng.integriam.client.ws.reporg.ListaTipiDato listaTipiDato;
    private boolean orgPresente;
    private Integer idEnteConvenz;
    private Integer idEnteConserv;
    private Integer idEnteGestore;
    private Date dtIniVal;
    private Date dtFineVal;
    private String urlReplicaOrganizzazioni;
    // valore predefinito 100 secondi (espresso in ms)
    private int timeout = 100_000;

    public String getNmTipoOrganizPadre() {
        return nmTipoOrganizPadre;
    }

    public void setNmTipoOrganizPadre(String nmTipoOrganizPadre) {
        this.nmTipoOrganizPadre = nmTipoOrganizPadre;
    }

    public String getNmUserid() {
        return nmUserid;
    }

    public void setNmUserid(String nmUserid) {
        this.nmUserid = nmUserid;
    }

    public String getCdPsw() {
        return cdPsw;
    }

    public void setCdPsw(String cdPsw) {
        this.cdPsw = cdPsw;
    }

    public String getNmApplic() {
        return nmApplic;
    }

    public void setNmApplic(String nmApplic) {
        this.nmApplic = nmApplic;
    }

    public Integer getIdOrganizApplic() {
        return idOrganizApplic;
    }

    public void setIdOrganizApplic(Integer idOrganizApplic) {
        this.idOrganizApplic = idOrganizApplic;
    }

    public String getNmTipoOrganiz() {
        return nmTipoOrganiz;
    }

    public void setNmTipoOrganiz(String nmTipoOrganiz) {
        this.nmTipoOrganiz = nmTipoOrganiz;
    }

    public Integer getIdOrganizApplicPadre() {
        return idOrganizApplicPadre;
    }

    public void setIdOrganizApplicPadre(Integer idOrganizApplicPadre) {
        this.idOrganizApplicPadre = idOrganizApplicPadre;
    }

    public String getNmOrganiz() {
        return nmOrganiz;
    }

    public void setNmOrganiz(String nmOrganiz) {
        this.nmOrganiz = nmOrganiz;
    }

    public String getDsOrganiz() {
        return dsOrganiz;
    }

    public void setDsOrganiz(String dsOrganiz) {
        this.dsOrganiz = dsOrganiz;
    }

    public ListaTipiDato getListaTipiDato() {
        return listaTipiDato;
    }

    public void setListaTipiDato(ListaTipiDato listaTipiDato) {
        this.listaTipiDato = listaTipiDato;
    }

    public boolean isOrgPresente() {
        return orgPresente;
    }

    public void setOrgPresente(boolean orgPresente) {
        this.orgPresente = orgPresente;
    }

    public Integer getIdEnteConvenz() {
        return idEnteConvenz;
    }

    public void setIdEnteConvenz(Integer idEnteConvenz) {
        this.idEnteConvenz = idEnteConvenz;
    }

    public Integer getIdEnteConserv() {
        return idEnteConserv;
    }

    public void setIdEnteConserv(Integer idEnteConserv) {
        this.idEnteConserv = idEnteConserv;
    }

    public Integer getIdEnteGestore() {
        return idEnteGestore;
    }

    public void setIdEnteGestore(Integer idEnteGestore) {
        this.idEnteGestore = idEnteGestore;
    }

    public Date getDtIniVal() {
        return dtIniVal;
    }

    public void setDtIniVal(Date dtIniVal) {
        this.dtIniVal = dtIniVal;
    }

    public Date getDtFineVal() {
        return dtFineVal;
    }

    public void setDtFineVal(Date dtFineVal) {
        this.dtFineVal = dtFineVal;
    }

    public String getUrlReplicaOrganizzazioni() {
        return urlReplicaOrganizzazioni;
    }

    public void setUrlReplicaOrganizzazioni(String urlReplicaOrganizzazioni) {
        this.urlReplicaOrganizzazioni = urlReplicaOrganizzazioni;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}
