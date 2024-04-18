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
 * The persistent class for the ARO_V_LIS_UPD_COMP_UNITA_DOC database table.
 *
 */
@Entity
@Table(name = "ARO_V_LIS_UPD_COMP_UNITA_DOC")
public class AroVLisUpdCompUnitaDoc implements Serializable {

    private static final long serialVersionUID = 1L;
    private String dsElemento;
    private String flUpdDatiSpec;
    private String flUpdDatiSpecMigraz;
    private String flUpdInfoVers;
    private BigDecimal idDoc;
    private BigDecimal idUpdCompUnitaDoc;
    private BigDecimal idUpdDocUnitaDoc;
    private BigDecimal idUpdUnitaDoc;
    private BigDecimal niOrdCompDoc;
    private String nmTipoDoc;
    private BigDecimal pgDoc;
    private BigDecimal tiDocOrd;

    public AroVLisUpdCompUnitaDoc() {/* Hibernate */
    }

    @Column(name = "DS_ELEMENTO")
    public String getDsElemento() {
        return this.dsElemento;
    }

    public void setDsElemento(String dsElemento) {
        this.dsElemento = dsElemento;
    }

    @Column(name = "FL_UPD_DATI_SPEC", columnDefinition = "char(1)")
    public String getFlUpdDatiSpec() {
        return this.flUpdDatiSpec;
    }

    public void setFlUpdDatiSpec(String flUpdDatiSpec) {
        this.flUpdDatiSpec = flUpdDatiSpec;
    }

    @Column(name = "FL_UPD_DATI_SPEC_MIGRAZ", columnDefinition = "char(1)")
    public String getFlUpdDatiSpecMigraz() {
        return this.flUpdDatiSpecMigraz;
    }

    public void setFlUpdDatiSpecMigraz(String flUpdDatiSpecMigraz) {
        this.flUpdDatiSpecMigraz = flUpdDatiSpecMigraz;
    }

    @Column(name = "FL_UPD_INFO_VERS", columnDefinition = "char(1)")
    public String getFlUpdInfoVers() {
        return this.flUpdInfoVers;
    }

    public void setFlUpdInfoVers(String flUpdInfoVers) {
        this.flUpdInfoVers = flUpdInfoVers;
    }

    @Column(name = "ID_DOC")
    public BigDecimal getIdDoc() {
        return this.idDoc;
    }

    public void setIdDoc(BigDecimal idDoc) {
        this.idDoc = idDoc;
    }

    @Id
    @Column(name = "ID_UPD_COMP_UNITA_DOC")
    public BigDecimal getIdUpdCompUnitaDoc() {
        return this.idUpdCompUnitaDoc;
    }

    public void setIdUpdCompUnitaDoc(BigDecimal idUpdCompUnitaDoc) {
        this.idUpdCompUnitaDoc = idUpdCompUnitaDoc;
    }

    @Column(name = "ID_UPD_DOC_UNITA_DOC")
    public BigDecimal getIdUpdDocUnitaDoc() {
        return this.idUpdDocUnitaDoc;
    }

    public void setIdUpdDocUnitaDoc(BigDecimal idUpdDocUnitaDoc) {
        this.idUpdDocUnitaDoc = idUpdDocUnitaDoc;
    }

    @Column(name = "ID_UPD_UNITA_DOC")
    public BigDecimal getIdUpdUnitaDoc() {
        return this.idUpdUnitaDoc;
    }

    public void setIdUpdUnitaDoc(BigDecimal idUpdUnitaDoc) {
        this.idUpdUnitaDoc = idUpdUnitaDoc;
    }

    @Column(name = "NI_ORD_COMP_DOC")
    public BigDecimal getNiOrdCompDoc() {
        return this.niOrdCompDoc;
    }

    public void setNiOrdCompDoc(BigDecimal niOrdCompDoc) {
        this.niOrdCompDoc = niOrdCompDoc;
    }

    @Column(name = "NM_TIPO_DOC")
    public String getNmTipoDoc() {
        return this.nmTipoDoc;
    }

    public void setNmTipoDoc(String nmTipoDoc) {
        this.nmTipoDoc = nmTipoDoc;
    }

    @Column(name = "PG_DOC")
    public BigDecimal getPgDoc() {
        return this.pgDoc;
    }

    public void setPgDoc(BigDecimal pgDoc) {
        this.pgDoc = pgDoc;
    }

    @Column(name = "TI_DOC_ORD")
    public BigDecimal getTiDocOrd() {
        return this.tiDocOrd;
    }

    public void setTiDocOrd(BigDecimal tiDocOrd) {
        this.tiDocOrd = tiDocOrd;
    }
}
