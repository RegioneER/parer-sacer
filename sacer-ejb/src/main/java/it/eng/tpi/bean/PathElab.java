/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.tpi.bean;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "PathElab")
@XmlType(propOrder = {
	"dtSched", "dsPath", "niFileDaElab", "niFileElab" })
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
