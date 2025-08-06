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
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;

/**
 * The persistent class for the MON_V_LIS_UPD_UD_ERR database table.
 *
 */
@Entity
@Table(name = "MON_V_LIS_UPD_UD_ERR")

public class MonVLisUpdUdErr implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaKeyUnitaDoc;
    private String cdControlloWsPrinc;
    private String cdErrPrinc;
    private String cdKeyUnitaDoc;
    private String cdRegistroKeyUnitaDoc;
    private String dsErrPrinc;
    private String dsTsIniSes;
    private BigDecimal idClasseErrSacer;
    private BigDecimal idControlloWsPrinc;
    private BigDecimal idErrSacer;
    private BigDecimal idErrSacerPrinc;
    private BigDecimal idSesUpdUnitaDocErr;
    private String nmAmbiente;
    private String nmEnte;
    private String nmStrut;
    private String nmTipoDocPrinc;
    private String nmTipoUnitaDoc;
    private String tiStatoSes;
    private Timestamp tsIniSes;

    public MonVLisUpdUdErr() {/* Hibernate */
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
	return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
	this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Column(name = "CD_CONTROLLO_WS_PRINC")
    public String getCdControlloWsPrinc() {
	return this.cdControlloWsPrinc;
    }

    public void setCdControlloWsPrinc(String cdControlloWsPrinc) {
	this.cdControlloWsPrinc = cdControlloWsPrinc;
    }

    @Column(name = "CD_ERR_PRINC")
    public String getCdErrPrinc() {
	return this.cdErrPrinc;
    }

    public void setCdErrPrinc(String cdErrPrinc) {
	this.cdErrPrinc = cdErrPrinc;
    }

    @Column(name = "CD_KEY_UNITA_DOC")
    public String getCdKeyUnitaDoc() {
	return this.cdKeyUnitaDoc;
    }

    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
	this.cdKeyUnitaDoc = cdKeyUnitaDoc;
    }

    @Column(name = "CD_REGISTRO_KEY_UNITA_DOC")
    public String getCdRegistroKeyUnitaDoc() {
	return this.cdRegistroKeyUnitaDoc;
    }

    public void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
	this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
    }

    @Column(name = "DS_ERR_PRINC")
    public String getDsErrPrinc() {
	return this.dsErrPrinc;
    }

    public void setDsErrPrinc(String dsErrPrinc) {
	this.dsErrPrinc = dsErrPrinc;
    }

    @Column(name = "DS_TS_INI_SES")
    public String getDsTsIniSes() {
	return this.dsTsIniSes;
    }

    public void setDsTsIniSes(String dsTsIniSes) {
	this.dsTsIniSes = dsTsIniSes;
    }

    @Column(name = "ID_CLASSE_ERR_SACER")
    public BigDecimal getIdClasseErrSacer() {
	return this.idClasseErrSacer;
    }

    public void setIdClasseErrSacer(BigDecimal idClasseErrSacer) {
	this.idClasseErrSacer = idClasseErrSacer;
    }

    @Column(name = "ID_CONTROLLO_WS_PRINC")
    public BigDecimal getIdControlloWsPrinc() {
	return this.idControlloWsPrinc;
    }

    public void setIdControlloWsPrinc(BigDecimal idControlloWsPrinc) {
	this.idControlloWsPrinc = idControlloWsPrinc;
    }

    @Column(name = "ID_ERR_SACER")
    public BigDecimal getIdErrSacer() {
	return this.idErrSacer;
    }

    public void setIdErrSacer(BigDecimal idErrSacer) {
	this.idErrSacer = idErrSacer;
    }

    @Column(name = "ID_ERR_SACER_PRINC")
    public BigDecimal getIdErrSacerPrinc() {
	return this.idErrSacerPrinc;
    }

    public void setIdErrSacerPrinc(BigDecimal idErrSacerPrinc) {
	this.idErrSacerPrinc = idErrSacerPrinc;
    }

    @Id
    @Column(name = "ID_SES_UPD_UNITA_DOC_ERR")
    public BigDecimal getIdSesUpdUnitaDocErr() {
	return this.idSesUpdUnitaDocErr;
    }

    public void setIdSesUpdUnitaDocErr(BigDecimal idSesUpdUnitaDocErr) {
	this.idSesUpdUnitaDocErr = idSesUpdUnitaDocErr;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
	return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
	this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
	return this.nmEnte;
    }

    public void setNmEnte(String nmEnte) {
	this.nmEnte = nmEnte;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
	return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
	this.nmStrut = nmStrut;
    }

    @Column(name = "NM_TIPO_DOC_PRINC")
    public String getNmTipoDocPrinc() {
	return this.nmTipoDocPrinc;
    }

    public void setNmTipoDocPrinc(String nmTipoDocPrinc) {
	this.nmTipoDocPrinc = nmTipoDocPrinc;
    }

    @Column(name = "NM_TIPO_UNITA_DOC")
    public String getNmTipoUnitaDoc() {
	return this.nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
	this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    @Column(name = "TI_STATO_SES")
    public String getTiStatoSes() {
	return this.tiStatoSes;
    }

    public void setTiStatoSes(String tiStatoSes) {
	this.tiStatoSes = tiStatoSes;
    }

    @Column(name = "TS_INI_SES")
    public Timestamp getTsIniSes() {
	return this.tsIniSes;
    }

    public void setTsIniSes(Timestamp tsIniSes) {
	this.tsIniSes = tsIniSes;
    }

}
