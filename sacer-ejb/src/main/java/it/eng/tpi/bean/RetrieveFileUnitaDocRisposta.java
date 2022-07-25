package it.eng.tpi.bean;

import it.eng.tpi.bean.Esito.EsitoServizio;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "RetrieveFileUnitaDoc")
@XmlType(propOrder = { "esito", "dtVers", "rootDtVers", "dirStruttura", "dirUnitaDoc" })
public class RetrieveFileUnitaDocRisposta {

    protected Esito esito;
    protected Date dtVers;
    protected String rootDtVers;
    protected String dirStruttura;
    protected String dirUnitaDoc;

    public RetrieveFileUnitaDocRisposta() {
        setEsito(new Esito());
    }

    public RetrieveFileUnitaDocRisposta(Date dtVers, String rootDtVers, String dirStruttura, String dirUnitaDoc) {
        this();
        getEsito().setCdEsito(EsitoServizio.KO);
        this.dtVers = dtVers;
        this.rootDtVers = rootDtVers;
        this.dirStruttura = dirStruttura;
        this.dirUnitaDoc = dirUnitaDoc;
    }

    @XmlElement(name = "Esito")
    public Esito getEsito() {
        return esito;
    }

    public void setEsito(Esito esito) {
        this.esito = esito;
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

    @XmlElement(name = "DirStruttura")
    public String getDirStruttura() {
        return dirStruttura;
    }

    public void setDirStruttura(String dirStruttura) {
        this.dirStruttura = dirStruttura;
    }

    @XmlElement(name = "DirUnitaDoc")
    public String getDirUnitaDoc() {
        return dirUnitaDoc;
    }

    public void setDirUnitaDoc(String dirUnitaDoc) {
        this.dirUnitaDoc = dirUnitaDoc;
    }
}
