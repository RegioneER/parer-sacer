package it.eng.tpi.bean;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Esito")
public class Esito {

    @XmlType(name = "esitoServizio")
    @XmlEnum
    public enum EsitoServizio {

        OK, KO;

        public String value() {
            return name();
        }

        public static EsitoServizio fromValue(String v) {
            return valueOf(v);
        }
    }

    protected EsitoServizio cdEsito;
    protected String cdErr;
    protected String dlErr;

    public EsitoServizio getCdEsito() {
        return cdEsito;
    }

    public void setCdEsito(EsitoServizio cdEsito) {
        this.cdEsito = cdEsito;
    }

    public String getCdErr() {
        return cdErr;
    }

    public void setCdErr(String cdErr) {
        this.cdErr = cdErr;
    }

    public String getDlErr() {
        return dlErr;
    }

    public void setDlErr(String dlErr) {
        this.dlErr = dlErr;
    }
}
