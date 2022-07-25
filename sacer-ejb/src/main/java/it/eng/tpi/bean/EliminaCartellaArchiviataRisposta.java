package it.eng.tpi.bean;

import it.eng.tpi.bean.Esito.EsitoServizio;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "EliminaCartellaArchiviata")
@XmlType(propOrder = { "esito", "flCartellaMigraz", "dtVers", "rootDtVers" })
public class EliminaCartellaArchiviataRisposta {

    protected Esito esito;
    protected Boolean flCartellaMigraz;
    protected Date dtVers;
    protected String rootDtVers;

    public EliminaCartellaArchiviataRisposta() {
        setEsito(new Esito());
    }

    public EliminaCartellaArchiviataRisposta(Boolean flCartellaMigraz, Date dtVers, String rootDtVers) {
        this();
        getEsito().setCdEsito(EsitoServizio.KO);
        this.flCartellaMigraz = flCartellaMigraz;
        this.dtVers = dtVers;
        this.rootDtVers = rootDtVers;
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

    @XmlElement(name = "RootDtVers")
    public String getRootDtVers() {
        return rootDtVers;
    }

    public void setRootDtVers(String rootDtVers) {
        this.rootDtVers = rootDtVers;
    }
}
