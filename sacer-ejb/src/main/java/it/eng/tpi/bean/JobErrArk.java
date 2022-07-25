package it.eng.tpi.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "ErrArk")
@XmlType(propOrder = { "niErrArk", "tiErrArk", "cdErrArk", "dsErrArk" })
public class JobErrArk {

    protected Integer niErrArk;
    protected String tiErrArk;
    protected String cdErrArk;
    protected String dsErrArk;

    @XmlElement(name = "NiErrArk")
    public Integer getNiErrArk() {
        return niErrArk;
    }

    public void setNiErrArk(Integer niErrArk) {
        this.niErrArk = niErrArk;
    }

    @XmlElement(name = "TiErrArk")
    public String getTiErrArk() {
        return tiErrArk;
    }

    public void setTiErrArk(String tiErrArk) {
        this.tiErrArk = tiErrArk;
    }

    @XmlElement(name = "CdErrArk")
    public String getCdErrArk() {
        return cdErrArk;
    }

    public void setCdErrArk(String cdErrArk) {
        this.cdErrArk = cdErrArk;
    }

    @XmlElement(name = "DsErrArk")
    public String getDsErrArk() {
        return dsErrArk;
    }

    public void setDsErrArk(String dsErrArk) {
        this.dsErrArk = dsErrArk;
    }

}
