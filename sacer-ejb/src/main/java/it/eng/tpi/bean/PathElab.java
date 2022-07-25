package it.eng.tpi.bean;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "PathElab")
@XmlType(propOrder = { "dtSched", "dsPath", "niFileDaElab", "niFileElab" })
public class PathElab {

    protected Date dtSched;
    protected String dsPath;
    protected Integer niFileDaElab;
    protected Integer niFileElab;

    @XmlElement(name = "DtSched")
    public Date getDtSched() {
        return dtSched;
    }

    public void setDtSched(Date dtSched) {
        this.dtSched = dtSched;
    }

    @XmlElement(name = "DsPath")
    public String getDsPath() {
        return dsPath;
    }

    public void setDsPath(String dsPath) {
        this.dsPath = dsPath;
    }

    @XmlElement(name = "NiFileDaElab")
    public Integer getNiFileDaElab() {
        return niFileDaElab;
    }

    public void setNiFileDaElab(Integer niFileDaElab) {
        this.niFileDaElab = niFileDaElab;
    }

    @XmlElement(name = "NiFileElab")
    public Integer getNiFileElab() {
        return niFileElab;
    }

    public void setNiFileElab(Integer niFileElab) {
        this.niFileElab = niFileElab;
    }
}
