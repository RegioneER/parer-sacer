package it.eng.tpi.bean;

import it.eng.tpi.bean.Esito.EsitoServizio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "StatoArchiviazioneCartella")
@XmlType(propOrder = { "esito", "flCartellaMigraz", "dtVers", "flPresenzaSitoSecondario", "flDaArchiviare",
        "flDaRiArchiviare", "listaAggreg" })
public class StatoArchiviazioneCartellaRisposta {

    protected Esito esito;
    protected Boolean flCartellaMigraz;
    protected Date dtVers;
    protected Boolean flPresenzaSitoSecondario;
    protected Boolean flDaArchiviare;
    protected Boolean flDaRiArchiviare;
    protected List<Aggregato> listaAggreg = new ArrayList<Aggregato>();

    public StatoArchiviazioneCartellaRisposta() {
        setEsito(new Esito());
    }

    public StatoArchiviazioneCartellaRisposta(Boolean flCartellaMigraz, Date dtVers) {
        this();
        getEsito().setCdEsito(EsitoServizio.KO);
        this.flCartellaMigraz = flCartellaMigraz;
        this.dtVers = dtVers;
    }

    @XmlElement(name = "Esito")
    public Esito getEsito() {
        return esito;
    }

    public void setEsito(Esito esito) {
        this.esito = esito;
    }

    @XmlElement(name = "FlCartellaMigraz")
    public Boolean getFlCartellaMigraz() {
        return flCartellaMigraz;
    }

    public void setFlCartellaMigraz(Boolean flCartellaMigraz) {
        this.flCartellaMigraz = flCartellaMigraz;
    }

    @XmlElement(name = "DtVers")
    public Date getDtVers() {
        return dtVers;
    }

    public void setDtVers(Date dtVers) {
        this.dtVers = dtVers;
    }

    @XmlElement(name = "FlPresenzaSitoSecondario")
    public Boolean getFlPresenzaSitoSecondario() {
        return flPresenzaSitoSecondario;
    }

    public void setFlPresenzaSitoSecondario(Boolean flPresenzaSitoSecondario) {
        this.flPresenzaSitoSecondario = flPresenzaSitoSecondario;
    }

    @XmlElement(name = "FlDaArchiviare")
    public Boolean getFlDaArchiviare() {
        return flDaArchiviare;
    }

    public void setFlDaArchiviare(Boolean flDaArchiviare) {
        this.flDaArchiviare = flDaArchiviare;
    }

    @XmlElement(name = "FlDaRiArchiviare")
    public Boolean getFlDaRiArchiviare() {
        return flDaRiArchiviare;
    }

    public void setFlDaRiArchiviare(Boolean flDaRiArchiviare) {
        this.flDaRiArchiviare = flDaRiArchiviare;
    }

    @XmlElement(name = "Aggregato")
    @XmlElementWrapper(name = "ListaAggreg")
    public List<Aggregato> getListaAggreg() {
        return listaAggreg;
    }
}
