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
 * The persistent class for the ARO_V_RIC_DATI_SPEC database table.
 * 
 */
@Entity
@Table(name = "ARO_V_RIC_DATI_SPEC")
public class AroVRicDatiSpec implements Serializable {
    private static final long serialVersionUID = 1L;
    private String dlValore;
    private BigDecimal idAttribDatiSpec;
    private BigDecimal idCompDoc;
    private BigDecimal idDoc;
    private BigDecimal idUnitaDoc;
    private BigDecimal idValoreAttribDatiSpec;
    private String tiEntitaSacer;

    public AroVRicDatiSpec() {/* Hibernate */
    }

    @Column(name = "DL_VALORE")
    public String getDlValore() {
        return this.dlValore;
    }

    public void setDlValore(String dlValore) {
        this.dlValore = dlValore;
    }

    @Column(name = "ID_ATTRIB_DATI_SPEC")
    public BigDecimal getIdAttribDatiSpec() {
        return this.idAttribDatiSpec;
    }

    public void setIdAttribDatiSpec(BigDecimal idAttribDatiSpec) {
        this.idAttribDatiSpec = idAttribDatiSpec;
    }

    @Column(name = "ID_COMP_DOC")
    public BigDecimal getIdCompDoc() {
        return this.idCompDoc;
    }

    public void setIdCompDoc(BigDecimal idCompDoc) {
        this.idCompDoc = idCompDoc;
    }

    @Column(name = "ID_DOC")
    public BigDecimal getIdDoc() {
        return this.idDoc;
    }

    public void setIdDoc(BigDecimal idDoc) {
        this.idDoc = idDoc;
    }

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    @Id
    @Column(name = "ID_VALORE_ATTRIB_DATI_SPEC")
    public BigDecimal getIdValoreAttribDatiSpec() {
        return this.idValoreAttribDatiSpec;
    }

    public void setIdValoreAttribDatiSpec(BigDecimal idValoreAttribDatiSpec) {
        this.idValoreAttribDatiSpec = idValoreAttribDatiSpec;
    }

    @Column(name = "TI_ENTITA_SACER")
    public String getTiEntitaSacer() {
        return this.tiEntitaSacer;
    }

    public void setTiEntitaSacer(String tiEntitaSacer) {
        this.tiEntitaSacer = tiEntitaSacer;
    }

}
