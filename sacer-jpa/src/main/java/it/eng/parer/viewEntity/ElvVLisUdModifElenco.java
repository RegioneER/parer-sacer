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

package it.eng.parer.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ELV_V_LIS_UD_MODIF_ELENCO database table.
 *
 */
@Entity
@Table(name = "ELV_V_LIS_UD_MODIF_ELENCO")

public class ElvVLisUdModifElenco implements Serializable {

    private static final long serialVersionUID = 1L;
    private String dsUrnUnitaDoc;
    private BigDecimal idElencoVers;
    private Date dtVersMin;
    private BigDecimal idUnitaDoc;

    public ElvVLisUdModifElenco() {/* Hibernate */
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_VERS_MIN")
    public Date getDtVersMin() {
        return this.dtVersMin;
    }

    public void setDtVersMin(Date dtVersMin) {
        this.dtVersMin = dtVersMin;
    }

    @Column(name = "ID_ELENCO_VERS")
    public BigDecimal getIdElencoVers() {
        return this.idElencoVers;
    }

    public void setIdElencoVers(BigDecimal idElencoVers) {
        this.idElencoVers = idElencoVers;
    }

    @Column(name = "DS_URN_UNITA_DOC")
    public String getDsUrnUnitaDoc() {
        return this.dsUrnUnitaDoc;
    }

    public void setDsUrnUnitaDoc(String dsUrnUnitaDoc) {
        this.dsUrnUnitaDoc = dsUrnUnitaDoc;
    }

    @Id
    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

}
