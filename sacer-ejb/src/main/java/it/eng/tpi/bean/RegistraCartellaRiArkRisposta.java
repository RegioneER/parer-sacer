package it.eng.tpi.bean;

import it.eng.tpi.bean.Esito.EsitoServizio;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "RetrieveFileUnitaDoc")
@XmlType(propOrder = { "esito", "flCartellaMigraz", "dtVers", "tiRiArk" })
public class RegistraCartellaRiArkRisposta {

    protected Esito esito;
    protected Date dtVers;
    protected String tiRiArk;
    protected Boolean flCartellaMigraz;

    public RegistraCartellaRiArkRisposta() {
        setEsito(new Esito());
    }

    public RegistraCartellaRiArkRisposta(Date dtVers, String tiRiArk, Boolean flCartellaMigraz) {
        this();
        getEsito().setCdEsito(EsitoServizio.KO);
        this.dtVers = dtVers;
        this.tiRiArk = tiRiArk;
        this.flCartellaMigraz = flCartellaMigraz;
    }

    @XmlElement(name = "Esito")
    public Esito getEsito() {
        return esito;
    }

    public void setEsito(Esito esito) {
        this.esito = esito;
    }

    @XmlElement(name = "DtVersFile")
    public Date getDtVers() {
        return dtVers;
    }

    public void setDtVers(Date dtVers) {
        this.dtVers = dtVers;
    }

    @XmlElement(name = "TiRi_Ark")
    public String getTiRiArk() {
        return tiRiArk;
    }

    public void setTiRiArk(String tiRiArk) {
        this.tiRiArk = tiRiArk;
    }

    @XmlElement(name = "FlCartellaMigraz")
    public Boolean getFlCartellaMigraz() {
        return flCartellaMigraz;
    }

    public void setFlCartellaMigraz(Boolean flCartellaMigraz) {
        this.flCartellaMigraz = flCartellaMigraz;
    }

}
