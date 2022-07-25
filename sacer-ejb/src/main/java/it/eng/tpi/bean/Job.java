package it.eng.tpi.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Job")
@XmlType(propOrder = { "flMigraz", "nmJob", "niOrdSchedJob", "dtSchedJob", "flJobOk", "dsDurataJob", "dlErrJob",
        "flFineSched", "listaErrArk", "listaPathElab" })
public class Job {

    protected Boolean flMigraz;
    protected String nmJob;
    protected Integer niOrdSchedJob;
    protected Date dtSchedJob;
    protected Boolean flJobOk;
    protected String dsDurataJob;
    protected String dlErrJob;
    protected Boolean flFineSched;
    protected List<JobErrArk> listaErrArk = new ArrayList<JobErrArk>();
    protected List<PathElab> listaPathElab = new ArrayList<PathElab>();

    @XmlElement(name = "FlMigraz")
    public Boolean getFlMigraz() {
        return flMigraz;
    }

    public void setFlMigraz(Boolean flMigraz) {
        this.flMigraz = flMigraz;
    }

    @XmlElement(name = "NmJob")
    public String getNmJob() {
        return nmJob;
    }

    public void setNmJob(String nmJob) {
        this.nmJob = nmJob;
    }

    @XmlElement(name = "NiOrdSchedJob")
    public Integer getNiOrdSchedJob() {
        return niOrdSchedJob;
    }

    public void setNiOrdSchedJob(Integer niOrdSchedJob) {
        this.niOrdSchedJob = niOrdSchedJob;
    }

    @XmlElement(name = "DtSchedJob")
    public Date getDtSchedJob() {
        return dtSchedJob;
    }

    public void setDtSchedJob(Date dtSchedJob) {
        this.dtSchedJob = dtSchedJob;
    }

    @XmlElement(name = "FlJobOk")
    public Boolean getFlJobOk() {
        return flJobOk;
    }

    public void setFlJobOk(Boolean flJobOk) {
        this.flJobOk = flJobOk;
    }

    @XmlElement(name = "DsDurataJob")
    public String getDsDurataJob() {
        return dsDurataJob;
    }

    public void setDsDurataJob(String dsDurataJob) {
        this.dsDurataJob = dsDurataJob;
    }

    @XmlElement(name = "DlErrJob")
    public String getDlErrJob() {
        return dlErrJob;
    }

    public void setDlErrJob(String dlErrJob) {
        this.dlErrJob = dlErrJob;
    }

    @XmlElement(name = "FlFineSched")
    public Boolean getFlFineSched() {
        return flFineSched;
    }

    public void setFlFineSched(Boolean flFineSched) {
        this.flFineSched = flFineSched;
    }

    @XmlElement(name = "ErrArk")
    @XmlElementWrapper(name = "ListaErroriArk")
    public List<JobErrArk> getListaErrArk() {
        return listaErrArk;
    }

    public void setListaErrArk(List<JobErrArk> listaErrArk) {
        this.listaErrArk = listaErrArk;
    }

    @XmlElement(name = "PathElab")
    @XmlElementWrapper(name = "ListaPathElab")
    public List<PathElab> getListaPathElab() {
        return listaPathElab;
    }

    public void setListaPathElab(List<PathElab> listaPathElab) {
        this.listaPathElab = listaPathElab;
    }

}