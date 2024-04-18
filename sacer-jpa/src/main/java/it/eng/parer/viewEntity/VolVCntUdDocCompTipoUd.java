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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the VOL_V_CNT_UD_DOC_COMP_TIPO_UD database table.
 * 
 */
@Entity
@Table(name = "VOL_V_CNT_UD_DOC_COMP_TIPO_UD")
public class VolVCntUdDocCompTipoUd implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal idVolumeConserv;
    private BigDecimal niComp;
    private BigDecimal niDoc;
    private BigDecimal niUd;
    private String nmTipoUnitaDoc;

    public VolVCntUdDocCompTipoUd() {/* Hibernate */
    }

    @Column(name = "ID_VOLUME_CONSERV")
    public BigDecimal getIdVolumeConserv() {
        return this.idVolumeConserv;
    }

    public void setIdVolumeConserv(BigDecimal idVolumeConserv) {
        this.idVolumeConserv = idVolumeConserv;
    }

    @Column(name = "NI_COMP")
    public BigDecimal getNiComp() {
        return this.niComp;
    }

    public void setNiComp(BigDecimal niComp) {
        this.niComp = niComp;
    }

    @Column(name = "NI_DOC")
    public BigDecimal getNiDoc() {
        return this.niDoc;
    }

    public void setNiDoc(BigDecimal niDoc) {
        this.niDoc = niDoc;
    }

    @Column(name = "NI_UD")
    public BigDecimal getNiUd() {
        return this.niUd;
    }

    public void setNiUd(BigDecimal niUd) {
        this.niUd = niUd;
    }

    @Id
    @Column(name = "NM_TIPO_UNITA_DOC")
    public String getNmTipoUnitaDoc() {
        return this.nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
        this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

}
