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
 * The persistent class for the ARO_V_LIS_CONTROFIRMA_FIRMA database table.
 * 
 */
@Entity
@Table(name = "ARO_V_LIS_CONTROFIRMA_FIRMA")
public class AroVLisControfirmaFirma implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "CD_FIRMATARIO")
    private String cdFirmatario;

    @Id

    @Column(name = "ID_CONTROFIRMA_FIRMA")
    private BigDecimal idControfirmaFirma;

    @Column(name = "ID_FIRMA_COMP")
    private BigDecimal idFirmaComp;

    @Column(name = "NM_COGNOME_FIRMATARIO")
    private String nmCognomeFirmatario;

    @Column(name = "NM_FIRMATARIO")
    private String nmFirmatario;

    public AroVLisControfirmaFirma() {/* Hibernate */
    }

    public String getCdFirmatario() {
        return this.cdFirmatario;
    }

    public void setCdFirmatario(String cdFirmatario) {
        this.cdFirmatario = cdFirmatario;
    }

    public BigDecimal getIdControfirmaFirma() {
        return this.idControfirmaFirma;
    }

    public void setIdControfirmaFirma(BigDecimal idControfirmaFirma) {
        this.idControfirmaFirma = idControfirmaFirma;
    }

    public BigDecimal getIdFirmaComp() {
        return this.idFirmaComp;
    }

    public void setIdFirmaComp(BigDecimal idFirmaComp) {
        this.idFirmaComp = idFirmaComp;
    }

    public String getNmCognomeFirmatario() {
        return this.nmCognomeFirmatario;
    }

    public void setNmCognomeFirmatario(String nmCognomeFirmatario) {
        this.nmCognomeFirmatario = nmCognomeFirmatario;
    }

    public String getNmFirmatario() {
        return this.nmFirmatario;
    }

    public void setNmFirmatario(String nmFirmatario) {
        this.nmFirmatario = nmFirmatario;
    }

}
