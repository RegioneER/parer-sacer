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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlID;

/**
 * The persistent class for the DEC_TI_EVE_STATO_ELENCO_VERS database table.
 *
 */
@Entity
@Table(name = "DEC_TI_EVE_STATO_ELENCO_VERS")

public class DecTiEveStatoElencoVers implements Serializable {

    private static final long serialVersionUID = 1L;

    private long idTiEveStatoElencoVers;
    private String cdTiEveStatoElencoVers;
    private String dsTiEveStatoElencoVers;

    public DecTiEveStatoElencoVers() {/* Hibernate */
    }

    @Id
    @SequenceGenerator(name = "DEC_TI_EVE_STATO_ELENCO_VERS_IDTIEVESTATOELENCOVERS_GENERATOR", sequenceName = "SDEC_TI_EVE_STATO_ELENCO_VERS", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEC_TI_EVE_STATO_ELENCO_VERS_IDTIEVESTATOELENCOVERS_GENERATOR")
    @Column(name = "ID_TI_EVE_STATO_ELENCO_VERS")
    @XmlID
    public long getIdTiEveStatoElencoVers() {
        return this.idTiEveStatoElencoVers;
    }

    public void setIdTiEveStatoElencoVers(long idTiEveStatoElencoVers) {
        this.idTiEveStatoElencoVers = idTiEveStatoElencoVers;
    }

    @Column(name = "CD_TI_EVE_STATO_ELENCO_VERS")
    public String getCdTiEveStatoElencoVers() {
        return this.cdTiEveStatoElencoVers;
    }

    public void setCdTiEveStatoElencoVers(String cdTiEveStatoElencoVers) {
        this.cdTiEveStatoElencoVers = cdTiEveStatoElencoVers;
    }

    @Column(name = "DS_TI_EVE_STATO_ELENCO_VERS")
    public String getDsTiEveStatoElencoVers() {
        return this.dsTiEveStatoElencoVers;
    }

    public void setDsTiEveStatoElencoVers(String dsTiEveStatoElencoVers) {
        this.dsTiEveStatoElencoVers = dsTiEveStatoElencoVers;
    }

}
