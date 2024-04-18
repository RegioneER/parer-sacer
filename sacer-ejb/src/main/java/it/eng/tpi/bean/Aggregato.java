/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

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
