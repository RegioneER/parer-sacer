package it.eng.tpi.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.eng.tpi.bean.Esito.EsitoServizio;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "SchedulazioniJob")
@XmlType(propOrder = { "esito", "dtSched", "flPresenzaSitoSecondario", "flMigrazInCorso", "listaJob",
        "listaJobSecondario" })
public class SchedulazioniJobTPIRisposta {

    protected Esito esito;
    protected Date dtSched;
    protected Boolean flPresenzaSitoSecondario;
    protected Boolean flMigrazInCorso;
    protected List<Job> listaJob = new ArrayList<Job>();
    protected List<Job> listaJobSecondario = new ArrayList<Job>();

    public SchedulazioniJobTPIRisposta() {
        setEsito(new Esito());
        getEsito().setCdEsito(EsitoServizio.KO);
    }

    @XmlElement(name = "Esito")
    public Esito getEsito() {
        return esito;
    }

    public void setEsito(Esito esito) {
        this.esito = esito;
    }

    @XmlElement(name = "DtVers")
    public Date getDtSched() {
        return dtSched;
    }

    public void setDtSched(Date dtSched) {
        this.dtSched = dtSched;
    }

    @XmlElement(name = "FlPresenzaSitoSecondario")
    public Boolean getFlPresenzaSitoSecondario() {
        return flPresenzaSitoSecondario;
    }

    public void setFlPresenzaSitoSecondario(Boolean flPresenzaSitoSecondario) {
        this.flPresenzaSitoSecondario = flPresenzaSitoSecondario;
    }

    @XmlElement(name = "FlMigrazInCorso")
    public Boolean getFlMigrazInCorso() {
        return flMigrazInCorso;
    }

    public void setFlMigrazInCorso(Boolean flMigrazInCorso) {
        this.flMigrazInCorso = flMigrazInCorso;
    }

    @XmlElement(name = "Job")
    @XmlElementWrapper(name = "ListaJob")
    public List<Job> getListaJob() {
        return listaJob;
    }

    public void setListaJob(List<Job> listaJob) {
        this.listaJob = listaJob;
    }

    @XmlElement(name = "Job")
    @XmlElementWrapper(name = "ListaJobSecondario")
    public List<Job> getListaJobSecondario() {
        return listaJobSecondario;
    }

    public void setListaJobSecondario(List<Job> listaJobSecondario) {
        this.listaJobSecondario = listaJobSecondario;
    }

}
