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
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import it.eng.tpi.bean.Esito.EsitoServizio;

@XmlRootElement(name = "StatoArchiviazioneCartella")
@XmlType(propOrder = { "esito", "flCartellaMigraz", "dtVers", "flPresenzaSitoSecondario", "flDaArchiviare",
        "flDaRiArchiviare", "listaAggreg" })
public class StatoArchiviazioneCartellaRisposta {

    protected Esito esito;
    protected Boolean flCartellaMigraz;
    protected Date dtVers;
    protected Boolean flPresenzaSitoSecondario;
    protected Boolean flDaArchiviare;
    protected Boolean flDaRiArchiviare;
    protected List<Aggregato> listaAggreg = new ArrayList<Aggregato>();

    public StatoArchiviazioneCartellaRisposta() {
        setEsito(new Esito());
    }

    public StatoArchiviazioneCartellaRisposta(Boolean flCartellaMigraz, Date dtVers) {
        this();
        getEsito().setCdEsito(EsitoServizio.KO);
        this.flCartellaMigraz = flCartellaMigraz;
        this.dtVers = dtVers;
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

    @XmlElement(name = "FlPresenzaSitoSecondario")
    public Boolean getFlPresenzaSitoSecondario() {
        return flPresenzaSitoSecondario;
    }

    public void setFlPresenzaSitoSecondario(Boolean flPresenzaSitoSecondario) {
        this.flPresenzaSitoSecondario = flPresenzaSitoSecondario;
    }

    @XmlElement(name = "FlDaArchiviare")
    public Boolean getFlDaArchiviare() {
        return flDaArchiviare;
    }

    public void setFlDaArchiviare(Boolean flDaArchiviare) {
        this.flDaArchiviare = flDaArchiviare;
    }

    @XmlElement(name = "FlDaRiArchiviare")
    public Boolean getFlDaRiArchiviare() {
        return flDaRiArchiviare;
    }

    public void setFlDaRiArchiviare(Boolean flDaRiArchiviare) {
        this.flDaRiArchiviare = flDaRiArchiviare;
    }

    @XmlElement(name = "Aggregato")
    @XmlElementWrapper(name = "ListaAggreg")
    public List<Aggregato> getListaAggreg() {
        return listaAggreg;
    }
}
