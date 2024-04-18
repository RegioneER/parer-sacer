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
 * The persistent class for the DEC_V_TREE_CATEG_TIPO_UD database table.
 *
 */
@Entity
@Table(name = "DEC_V_TREE_CATEG_TIPO_UD")

public class DecVTreeCategTipoUd implements Serializable {

    private static final long serialVersionUID = 1L;
    private String cdCategTipoUnitaDoc;
    private String dlIdCategTipoUnitaDoc;
    private String dlPathCategTipoUnitaDoc;
    private BigDecimal idCategTipoUnitaDoc;

    public DecVTreeCategTipoUd() {/* Hibernate */
    }

    @Column(name = "CD_CATEG_TIPO_UNITA_DOC")
    public String getCdCategTipoUnitaDoc() {
        return this.cdCategTipoUnitaDoc;
    }

    public void setCdCategTipoUnitaDoc(String cdCategTipoUnitaDoc) {
        this.cdCategTipoUnitaDoc = cdCategTipoUnitaDoc;
    }

    @Column(name = "DL_ID_CATEG_TIPO_UNITA_DOC")
    public String getDlIdCategTipoUnitaDoc() {
        return this.dlIdCategTipoUnitaDoc;
    }

    public void setDlIdCategTipoUnitaDoc(String dlIdCategTipoUnitaDoc) {
        this.dlIdCategTipoUnitaDoc = dlIdCategTipoUnitaDoc;
    }

    @Column(name = "DL_PATH_CATEG_TIPO_UNITA_DOC")
    public String getDlPathCategTipoUnitaDoc() {
        return this.dlPathCategTipoUnitaDoc;
    }

    public void setDlPathCategTipoUnitaDoc(String dlPathCategTipoUnitaDoc) {
        this.dlPathCategTipoUnitaDoc = dlPathCategTipoUnitaDoc;
    }

    @Id
    @Column(name = "ID_CATEG_TIPO_UNITA_DOC")
    public BigDecimal getIdCategTipoUnitaDoc() {
        return this.idCategTipoUnitaDoc;
    }

    public void setIdCategTipoUnitaDoc(BigDecimal idCategTipoUnitaDoc) {
        this.idCategTipoUnitaDoc = idCategTipoUnitaDoc;
    }

}
