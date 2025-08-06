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
 * The persistent class for the OST_V_LIS_FILE_BLOB_BYSTRUMESE database table.
 *
 */
@Entity
@Table(name = "OST_V_LIS_FILE_BLOB_BYSTRUMESE")

public class OstVLisFileBlobBystrumese implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal aaKeyUnitaDoc;
    private String cdKeyUnitaDoc;
    private String cdKeyUnitaDocNormaliz;
    private String cdRegistroNormaliz;
    private String cdRegistroUnitaDoc;
    private Date dtCreazione;
    private BigDecimal idCompDoc;
    private BigDecimal idDoc;
    private BigDecimal idRegistroUnitaDoc;
    private BigDecimal idStrut;
    private BigDecimal idUnitaDoc;
    private BigDecimal niOrdCompDoc;
    private BigDecimal niOrdCompDocPadre;
    private BigDecimal niOrdDoc;
    private String tiSupportoComp;
    private String tiSaveFile;

    public OstVLisFileBlobBystrumese() {/* Hibernate */
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
	return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
	this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Column(name = "CD_KEY_UNITA_DOC")
    public String getCdKeyUnitaDoc() {
	return this.cdKeyUnitaDoc;
    }

    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
	this.cdKeyUnitaDoc = cdKeyUnitaDoc;
    }

    @Column(name = "CD_KEY_UNITA_DOC_NORMALIZ")
    public String getCdKeyUnitaDocNormaliz() {
	return this.cdKeyUnitaDocNormaliz;
    }

    public void setCdKeyUnitaDocNormaliz(String cdKeyUnitaDocNormaliz) {
	this.cdKeyUnitaDocNormaliz = cdKeyUnitaDocNormaliz;
    }

    @Column(name = "CD_REGISTRO_NORMALIZ")
    public String getCdRegistroNormaliz() {
	return this.cdRegistroNormaliz;
    }

    public void setCdRegistroNormaliz(String cdRegistroNormaliz) {
	this.cdRegistroNormaliz = cdRegistroNormaliz;
    }

    @Column(name = "CD_REGISTRO_UNITA_DOC")
    public String getCdRegistroUnitaDoc() {
	return this.cdRegistroUnitaDoc;
    }

    public void setCdRegistroUnitaDoc(String cdRegistroUnitaDoc) {
	this.cdRegistroUnitaDoc = cdRegistroUnitaDoc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE")
    public Date getDtCreazione() {
	return this.dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
	this.dtCreazione = dtCreazione;
    }

    @Id
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

    @Column(name = "ID_REGISTRO_UNITA_DOC")
    public BigDecimal getIdRegistroUnitaDoc() {
	return this.idRegistroUnitaDoc;
    }

    public void setIdRegistroUnitaDoc(BigDecimal idRegistroUnitaDoc) {
	this.idRegistroUnitaDoc = idRegistroUnitaDoc;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
	return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
	this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "NI_ORD_COMP_DOC")
    public BigDecimal getNiOrdCompDoc() {
	return this.niOrdCompDoc;
    }

    public void setNiOrdCompDoc(BigDecimal niOrdCompDoc) {
	this.niOrdCompDoc = niOrdCompDoc;
    }

    @Column(name = "NI_ORD_COMP_DOC_PADRE")
    public BigDecimal getNiOrdCompDocPadre() {
	return this.niOrdCompDocPadre;
    }

    public void setNiOrdCompDocPadre(BigDecimal niOrdCompDocPadre) {
	this.niOrdCompDocPadre = niOrdCompDocPadre;
    }

    @Column(name = "NI_ORD_DOC")
    public BigDecimal getNiOrdDoc() {
	return this.niOrdDoc;
    }

    public void setNiOrdDoc(BigDecimal niOrdDoc) {
	this.niOrdDoc = niOrdDoc;
    }

    @Column(name = "TI_SUPPORTO_COMP")
    public String getTiSupportoComp() {
	return this.tiSupportoComp;
    }

    public void setTiSupportoComp(String tiSupportoComp) {
	this.tiSupportoComp = tiSupportoComp;
    }

    @Column(name = "TI_SAVE_FILE")
    public String getTiSaveFile() {
	return this.tiSaveFile;
    }

    public void setTiSaveFile(String tiSaveFile) {
	this.tiSaveFile = tiSaveFile;
    }

}
