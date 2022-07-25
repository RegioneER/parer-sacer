package it.eng.tpi.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Aggregato")
@XmlType(propOrder = { "dsAggreg", "listaArk", "listaArkSecondario", "listaPath" })
public class Aggregato {

    protected String dsAggreg;

    protected List<Archiviazione> listaArk = new ArrayList<Archiviazione>();
    protected List<Archiviazione> listaArkSecondario = new ArrayList<Archiviazione>();
    protected List<PathStrutture> listaPath = new ArrayList<PathStrutture>();

    public String getDsAggreg() {
        return dsAggreg;
    }

    public void setDsAggreg(String dsAggreg) {
        this.dsAggreg = dsAggreg;
    }

    @XmlElement(name = "Archiviazione")
    @XmlElementWrapper(name = "ListaArk")
    public List<Archiviazione> getListaArk() {
        return listaArk;
    }

    @XmlElement(name = "Archiviazione")
    @XmlElementWrapper(name = "ListaArkSecondario")
    public List<Archiviazione> getListaArkSecondario() {
        return listaArkSecondario;
    }

    @XmlElement(name = "PathStrutture")
    @XmlElementWrapper(name = "ListaPath")
    public List<PathStrutture> getListaPath() {
        return listaPath;
    }
}
