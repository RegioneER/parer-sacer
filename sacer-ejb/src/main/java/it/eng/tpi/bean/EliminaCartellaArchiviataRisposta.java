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

import it.eng.tpi.bean.Esito.EsitoServizio;

@XmlRootElement(name = "EliminaCartellaArchiviata")
@XmlType(propOrder = {
        "esito", "flCartellaMigraz", "dtVers", "rootDtVers" })
public class EliminaCartellaArchiviataRisposta {

    protected Esito esito;
    protected Boolean flCartellaMigraz;
    protected Date dtVers;
    protected String rootDtVers;

    public EliminaCartellaArchiviataRisposta() {
        setEsito(new Esito());
    }

    public EliminaCartellaArchiviataRisposta(Boolean flCartellaMigraz, Date dtVers,
            String rootDtVers) {
        this();
        getEsito().setCdEsito(EsitoServizio.KO);
        this.flCartellaMigraz = flCartellaMigraz;
        this.dtVers = dtVers;
        this.rootDtVers = rootDtVers;
    }

    @XmlElement(name = "Esito")
    public Esito getEsito() {
        return esito;
    }

    public void setEsito(Esito esito) {
        this.esito = esito;
    }

    @XmlElement(name = "FlCartellaMigraz")
    public Boolean getFlCartellaMigraz() {
        return flCartellaMigraz;
    }

    public void setFlCartellaMigraz(Boolean flCartellaMigraz) {
        this.flCartellaMigraz = flCartellaMigraz;
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
}
