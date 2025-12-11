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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "ErrArk")
@XmlType(propOrder = {
        "niErrArk", "tiErrArk", "cdErrArk", "dsErrArk" })
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
