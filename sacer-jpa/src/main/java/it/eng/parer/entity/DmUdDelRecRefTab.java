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

package it.eng.parer.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;

/**
 * The persistent class for the DM_UD_DEL_REC_REF_TAB database table.
 *
 */
@Entity
@Table(name = "DM_UD_DEL_REC_REF_TAB")
@NamedQuery(name = "DmUdDelRecRefTab.findAll", query = "SELECT d FROM DmUdDelRecRefTab d")
public class DmUdDelRecRefTab implements Serializable {
    private static final long serialVersionUID = 1L;
    private long idUdDelRecRefTab;
    private BigDecimal idFkRecTab;
    private BigDecimal idPkRecTab;
    private BigDecimal idUnitaDoc;
    private BigDecimal niLivello;
    private String nmColumnFk;
    private String nmColumnPk;
    private String nmTab;

    public DmUdDelRecRefTab() {
        /* Hibernate */
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_UD_DEL_REC_REF_TAB")
    public long getIdUdDelRecRefTab() {
        return this.idUdDelRecRefTab;
    }

    public void setIdUdDelRecRefTab(long idUdDelRecRefTab) {
        this.idUdDelRecRefTab = idUdDelRecRefTab;
    }

    @Column(name = "ID_FK_REC_TAB")
    public BigDecimal getIdFkRecTab() {
        return this.idFkRecTab;
    }

    public void setIdFkRecTab(BigDecimal idFkRecTab) {
        this.idFkRecTab = idFkRecTab;
    }

    @Column(name = "ID_PK_REC_TAB")
    public BigDecimal getIdPkRecTab() {
        return this.idPkRecTab;
    }

    public void setIdPkRecTab(BigDecimal idPkRecTab) {
        this.idPkRecTab = idPkRecTab;
    }

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "NI_LIVELLO")
    public BigDecimal getNiLivello() {
        return this.niLivello;
    }

    public void setNiLivello(BigDecimal niLivello) {
        this.niLivello = niLivello;
    }

    @Column(name = "NM_COLUMN_FK")
    public String getNmColumnFk() {
        return this.nmColumnFk;
    }

    public void setNmColumnFk(String nmColumnFk) {
        this.nmColumnFk = nmColumnFk;
    }

    @Column(name = "NM_COLUMN_PK")
    public String getNmColumnPk() {
        return this.nmColumnPk;
    }

    public void setNmColumnPk(String nmColumnPk) {
        this.nmColumnPk = nmColumnPk;
    }

    @Column(name = "NM_TAB")
    public String getNmTab() {
        return this.nmTab;
    }

    public void setNmTab(String nmTab) {
        this.nmTab = nmTab;
    }

}
