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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "PathStrutture")
@XmlType(propOrder = { "dsPath", "listaFileNoArk", "listaFileNoArkSecondario", "niFilePathArk",
        "niFilePathArkSecondario" })
public class PathStrutture {

    protected String dsPath;

    protected List<FilePathNoArk> listaFileNoArk = new ArrayList<FilePathNoArk>();
    protected List<FilePathNoArk> listaFileNoArkSecondario = new ArrayList<FilePathNoArk>();
    protected BigDecimal niFilePathArk;
    protected BigDecimal niFilePathArkSecondario;

    public String getDsPath() {
        return dsPath;
    }

    public void setDsPath(String dsPath) {
        this.dsPath = dsPath;
    }

    @XmlElement(name = "FileNoArk")
    @XmlElementWrapper(name = "ListaFileNoArk")
    public List<FilePathNoArk> getListaFileNoArk() {
        return listaFileNoArk;
    }

    @XmlElement(name = "FileNoArkSecondario")
    @XmlElementWrapper(name = "ListaFileNoArkSecondario")
    public List<FilePathNoArk> getListaFileNoArkSecondario() {
        return listaFileNoArkSecondario;
    }

    /**
     * @return the niFilePathArk
     */
    public BigDecimal getNiFilePathArk() {
        return niFilePathArk;
    }

    /**
     * @param niFilePathArk
     *            the niFilePathArk to set
     */
    public void setNiFilePathArk(BigDecimal niFilePathArk) {
        this.niFilePathArk = niFilePathArk;
    }

    /**
     * @return the niFilePathArkSecondario
     */
    public BigDecimal getNiFilePathArkSecondario() {
        return niFilePathArkSecondario;
    }

    /**
     * @param niFilePathArkSecondario
     *            the niFilePathArkSecondario to set
     */
    public void setNiFilePathArkSecondario(BigDecimal niFilePathArkSecondario) {
        this.niFilePathArkSecondario = niFilePathArkSecondario;
    }
}
