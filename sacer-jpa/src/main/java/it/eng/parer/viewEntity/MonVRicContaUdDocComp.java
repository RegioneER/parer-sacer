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
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * The persistent class for the MON_V_RIC_CONTA_UD_DOC_COMP database table.
 *
 */
@Entity
@Table(name = "MON_V_RIC_CONTA_UD_DOC_COMP")

public class MonVRicContaUdDocComp implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaKeyUnitaDoc;
    private Date dtRifConta;
    private BigDecimal idContaUdDocComp;
    private BigDecimal idRegistroUnitaDoc;
    private BigDecimal idStrut;
    private BigDecimal idSubStrut;
    private BigDecimal idTipoDocPrinc;
    private BigDecimal idTipoUnitaDoc;
    private BigDecimal niCompAgg;
    private BigDecimal niCompAnnulUd;
    private BigDecimal niCompVers;
    private BigDecimal niDocAgg;
    private BigDecimal niDocAnnulUd;
    private BigDecimal niDocVers;
    private BigDecimal niSizeAgg;
    private BigDecimal niSizeAnnulUd;
    private BigDecimal niSizeVers;
    private BigDecimal niUnitaDocAnnul;
    private BigDecimal niUnitaDocVers;

    public MonVRicContaUdDocComp() {
	/* hibernate */
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
	return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
	this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_RIF_CONTA")
    public Date getDtRifConta() {
	return this.dtRifConta;
    }

    public void setDtRifConta(Date dtRifConta) {
	this.dtRifConta = dtRifConta;
    }

    @Id
    @Column(name = "ID_CONTA_UD_DOC_COMP")
    public BigDecimal getIdContaUdDocComp() {
	return this.idContaUdDocComp;
    }

    public void setIdContaUdDocComp(BigDecimal idContaUdDocComp) {
	this.idContaUdDocComp = idContaUdDocComp;
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

    @Column(name = "ID_SUB_STRUT")
    public BigDecimal getIdSubStrut() {
	return this.idSubStrut;
    }

    public void setIdSubStrut(BigDecimal idSubStrut) {
	this.idSubStrut = idSubStrut;
    }

    @Column(name = "ID_TIPO_DOC_PRINC")
    public BigDecimal getIdTipoDocPrinc() {
	return this.idTipoDocPrinc;
    }

    public void setIdTipoDocPrinc(BigDecimal idTipoDocPrinc) {
	this.idTipoDocPrinc = idTipoDocPrinc;
    }

    @Column(name = "ID_TIPO_UNITA_DOC")
    public BigDecimal getIdTipoUnitaDoc() {
	return this.idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
	this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    @Column(name = "NI_COMP_AGG")
    public BigDecimal getNiCompAgg() {
	return this.niCompAgg;
    }

    public void setNiCompAgg(BigDecimal niCompAgg) {
	this.niCompAgg = niCompAgg;
    }

    @Column(name = "NI_COMP_ANNUL_UD")
    public BigDecimal getNiCompAnnulUd() {
	return this.niCompAnnulUd;
    }

    public void setNiCompAnnulUd(BigDecimal niCompAnnulUd) {
	this.niCompAnnulUd = niCompAnnulUd;
    }

    @Column(name = "NI_COMP_VERS")
    public BigDecimal getNiCompVers() {
	return this.niCompVers;
    }

    public void setNiCompVers(BigDecimal niCompVers) {
	this.niCompVers = niCompVers;
    }

    @Column(name = "NI_DOC_AGG")
    public BigDecimal getNiDocAgg() {
	return this.niDocAgg;
    }

    public void setNiDocAgg(BigDecimal niDocAgg) {
	this.niDocAgg = niDocAgg;
    }

    @Column(name = "NI_DOC_ANNUL_UD")
    public BigDecimal getNiDocAnnulUd() {
	return this.niDocAnnulUd;
    }

    public void setNiDocAnnulUd(BigDecimal niDocAnnulUd) {
	this.niDocAnnulUd = niDocAnnulUd;
    }

    @Column(name = "NI_DOC_VERS")
    public BigDecimal getNiDocVers() {
	return this.niDocVers;
    }

    public void setNiDocVers(BigDecimal niDocVers) {
	this.niDocVers = niDocVers;
    }

    @Column(name = "NI_SIZE_AGG")
    public BigDecimal getNiSizeAgg() {
	return this.niSizeAgg;
    }

    public void setNiSizeAgg(BigDecimal niSizeAgg) {
	this.niSizeAgg = niSizeAgg;
    }

    @Column(name = "NI_SIZE_ANNUL_UD")
    public BigDecimal getNiSizeAnnulUd() {
	return this.niSizeAnnulUd;
    }

    public void setNiSizeAnnulUd(BigDecimal niSizeAnnulUd) {
	this.niSizeAnnulUd = niSizeAnnulUd;
    }

    @Column(name = "NI_SIZE_VERS")
    public BigDecimal getNiSizeVers() {
	return this.niSizeVers;
    }

    public void setNiSizeVers(BigDecimal niSizeVers) {
	this.niSizeVers = niSizeVers;
    }

    @Column(name = "NI_UNITA_DOC_ANNUL")
    public BigDecimal getNiUnitaDocAnnul() {
	return this.niUnitaDocAnnul;
    }

    public void setNiUnitaDocAnnul(BigDecimal niUnitaDocAnnul) {
	this.niUnitaDocAnnul = niUnitaDocAnnul;
    }

    @Column(name = "NI_UNITA_DOC_VERS")
    public BigDecimal getNiUnitaDocVers() {
	return this.niUnitaDocVers;
    }

    public void setNiUnitaDocVers(BigDecimal niUnitaDocVers) {
	this.niUnitaDocVers = niUnitaDocVers;
    }

    public enum TipoConteggio {

	UD_VERS, DOC_VERS, COMP_VERS, BYTE_VERS, DOC_AGG, COMP_AGG, BYTE_AGG, UD_ANNULL, DOC_ANNULL,
	COMP_ANNULL, BYTE_ANNULL
    }

}
