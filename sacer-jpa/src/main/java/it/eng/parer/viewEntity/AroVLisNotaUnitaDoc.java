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
 * The persistent class for the ARO_V_LIS_NOTA_UNITA_DOC database table.
 *
 */
@Entity
@Table(name = "ARO_V_LIS_NOTA_UNITA_DOC")

public class AroVLisNotaUnitaDoc implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cdTipoNotaUnitaDoc;
    private String dsNotaUnitaDoc;
    private String dsTipoNotaUnitaDoc;
    private Date dtNotaUnitaDoc;
    private BigDecimal idNotaUnitaDoc;
    private BigDecimal idTipoNotaUnitaDoc;
    private BigDecimal idUnitaDoc;
    private BigDecimal idUserIam;
    private BigDecimal idVerIndiceAip;
    private BigDecimal niOrd;
    private String nmUserid;
    private BigDecimal pgNotaUnitaDoc;

    public AroVLisNotaUnitaDoc() {/* Hibernate */
    }

    @Column(name = "CD_TIPO_NOTA_UNITA_DOC")
    public String getCdTipoNotaUnitaDoc() {
        return this.cdTipoNotaUnitaDoc;
    }

    public void setCdTipoNotaUnitaDoc(String cdTipoNotaUnitaDoc) {
        this.cdTipoNotaUnitaDoc = cdTipoNotaUnitaDoc;
    }

    @Column(name = "DS_NOTA_UNITA_DOC")
    public String getDsNotaUnitaDoc() {
        return this.dsNotaUnitaDoc;
    }

    public void setDsNotaUnitaDoc(String dsNotaUnitaDoc) {
        this.dsNotaUnitaDoc = dsNotaUnitaDoc;
    }

    @Column(name = "DS_TIPO_NOTA_UNITA_DOC")
    public String getDsTipoNotaUnitaDoc() {
        return this.dsTipoNotaUnitaDoc;
    }

    public void setDsTipoNotaUnitaDoc(String dsTipoNotaUnitaDoc) {
        this.dsTipoNotaUnitaDoc = dsTipoNotaUnitaDoc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_NOTA_UNITA_DOC")
    public Date getDtNotaUnitaDoc() {
        return this.dtNotaUnitaDoc;
    }

    public void setDtNotaUnitaDoc(Date dtNotaUnitaDoc) {
        this.dtNotaUnitaDoc = dtNotaUnitaDoc;
    }

    @Id
    @Column(name = "ID_NOTA_UNITA_DOC")
    public BigDecimal getIdNotaUnitaDoc() {
        return this.idNotaUnitaDoc;
    }

    public void setIdNotaUnitaDoc(BigDecimal idNotaUnitaDoc) {
        this.idNotaUnitaDoc = idNotaUnitaDoc;
    }

    @Column(name = "ID_TIPO_NOTA_UNITA_DOC")
    public BigDecimal getIdTipoNotaUnitaDoc() {
        return this.idTipoNotaUnitaDoc;
    }

    public void setIdTipoNotaUnitaDoc(BigDecimal idTipoNotaUnitaDoc) {
        this.idTipoNotaUnitaDoc = idTipoNotaUnitaDoc;
    }

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "ID_USER_IAM")
    public BigDecimal getIdUserIam() {
        return this.idUserIam;
    }

    public void setIdUserIam(BigDecimal idUserIam) {
        this.idUserIam = idUserIam;
    }

    @Column(name = "ID_VER_INDICE_AIP")
    public BigDecimal getIdVerIndiceAip() {
        return this.idVerIndiceAip;
    }

    public void setIdVerIndiceAip(BigDecimal idVerIndiceAip) {
        this.idVerIndiceAip = idVerIndiceAip;
    }

    @Column(name = "NI_ORD")
    public BigDecimal getNiOrd() {
        return this.niOrd;
    }

    public void setNiOrd(BigDecimal niOrd) {
        this.niOrd = niOrd;
    }

    @Column(name = "NM_USERID")
    public String getNmUserid() {
        return this.nmUserid;
    }

    public void setNmUserid(String nmUserid) {
        this.nmUserid = nmUserid;
    }

    @Column(name = "PG_NOTA_UNITA_DOC")
    public BigDecimal getPgNotaUnitaDoc() {
        return this.pgNotaUnitaDoc;
    }

    public void setPgNotaUnitaDoc(BigDecimal pgNotaUnitaDoc) {
        this.pgNotaUnitaDoc = pgNotaUnitaDoc;
    }

}
