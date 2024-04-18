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

package it.eng.parer.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

/**
 * The persistent class for the FAS_EVENTO_SOG database table.
 * 
 */
@Entity
@Table(name = "FAS_EVENTO_SOG")

@NamedQuery(name = "FasEventoSog.findEventiSoggetto", query = "SELECT f FROM FasEventoSog f WHERE f.fasSogFascicolo.idSogFascicolo = :idSogFascicolo")
public class FasEventoSog implements Serializable {
    private static final long serialVersionUID = 1L;
    private long idEventoSog;
    private String dsDenomEvento;
    private Date tsApertura;
    private Date tsChiusura;

    private FasSogFascicolo fasSogFascicolo;

    public FasEventoSog() {
        /* hibernate */
    }

    @Id
    @SequenceGenerator(name = "FAS_EVENTO_FASCICOLO_IDEVENTOSOG_GENERATOR", sequenceName = "SFAS_EVENTO_SOG", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FAS_EVENTO_FASCICOLO_IDEVENTOSOG_GENERATOR")
    @Column(name = "ID_EVENTO_SOG")
    public long getIdEventoSog() {
        return this.idEventoSog;
    }

    public void setIdEventoSog(long idEventoFascicolo) {
        this.idEventoSog = idEventoFascicolo;
    }

    @Column(name = "DS_DENOM_EVENTO")
    public String getDsDenomEvento() {
        return this.dsDenomEvento;
    }

    public void setDsDenomEvento(String dsDenomEvento) {
        this.dsDenomEvento = dsDenomEvento;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_APERTURA")
    public Date getTsApertura() {
        return this.tsApertura;
    }

    public void setTsApertura(Date tsApertura) {
        this.tsApertura = tsApertura;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_CHIUSURA")
    public Date getTsChiusura() {
        return this.tsChiusura;
    }

    public void setTsChiusura(Date tsChiusura) {
        this.tsChiusura = tsChiusura;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SOG_FASCICOLO")
    public FasSogFascicolo getFasSogFascicolo() {
        return this.fasSogFascicolo;
    }

    public void setFasSogFascicolo(FasSogFascicolo fasSogFascicolo) {
        this.fasSogFascicolo = fasSogFascicolo;
    }

}
