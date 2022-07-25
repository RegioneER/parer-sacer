package it.eng.tpi.bean;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Archiviazione")
public class Archiviazione {
    protected String dsArk;
    protected Date dtArk;

    public String getDsArk() {
        return dsArk;
    }

    public void setDsArk(String dsArk) {
        this.dsArk = dsArk;
    }

    public Date getDtArk() {
        return dtArk;
    }

    public void setDtArk(Date dtArk) {
        this.dtArk = dtArk;
    }
}
