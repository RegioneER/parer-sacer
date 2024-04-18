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
 * The persistent class for the DEC_V_LIS_TI_UNI_DOC_AMS database table.
 * 
 */
@Entity
@Table(name = "DEC_V_LIS_TI_UNI_DOC_AMS")
public class DecVLisTiUniDocAms implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cdRegistroUnitaDoc;
    private BigDecimal idRegistroUnitaDoc;
    private BigDecimal idTipoUnitaDoc;
    private BigDecimal idTipoUnitaDocAmmesso;
    private String nmTipoUnitaDoc;

    public DecVLisTiUniDocAms() {/* Hibernate */
    }

    @Column(name = "CD_REGISTRO_UNITA_DOC")
    public String getCdRegistroUnitaDoc() {
        return this.cdRegistroUnitaDoc;
    }

    public void setCdRegistroUnitaDoc(String cdRegistroUnitaDoc) {
        this.cdRegistroUnitaDoc = cdRegistroUnitaDoc;
    }

    @Column(name = "ID_REGISTRO_UNITA_DOC")
    public BigDecimal getIdRegistroUnitaDoc() {
        return this.idRegistroUnitaDoc;
    }

    public void setIdRegistroUnitaDoc(BigDecimal idRegistroUnitaDoc) {
        this.idRegistroUnitaDoc = idRegistroUnitaDoc;
    }

    @Column(name = "ID_TIPO_UNITA_DOC")
    public BigDecimal getIdTipoUnitaDoc() {
        return this.idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    @Id
    @Column(name = "ID_TIPO_UNITA_DOC_AMMESSO")
    public BigDecimal getIdTipoUnitaDocAmmesso() {
        return this.idTipoUnitaDocAmmesso;
    }

    public void setIdTipoUnitaDocAmmesso(BigDecimal idTipoUnitaDocAmmesso) {
        this.idTipoUnitaDocAmmesso = idTipoUnitaDocAmmesso;
    }

    @Column(name = "NM_TIPO_UNITA_DOC")
    public String getNmTipoUnitaDoc() {
        return this.nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
        this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

}
