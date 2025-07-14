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
 * The persistent class for the ELV_V_LIS_LOG_OPER database table.
 *
 */
@Entity
@Table(name = "ELV_V_LIS_LOG_OPER")

public class ElvVLisLogOper implements Serializable {

    private static final long serialVersionUID = 1L;
    private String cdDoc;
    private String dlMotivoChius;
    private Date dtScadChius;
    private BigDecimal idAmbiente;
    private BigDecimal idElencoVers;
    private BigDecimal idEnte;
    private BigDecimal idLogElencoVers;
    private BigDecimal idLogJob;
    private BigDecimal idStrut;
    private BigDecimal niMaxComp;
    private String nmAmbiente;
    private String nmCognomeFirmatario;
    private String nmElenco;
    private String nmEnte;
    private String nmNomeFirmatario;
    private String nmStrut;
    private String tiModOper;
    private String tiOper;
    private Date tmOper;

    public ElvVLisLogOper() {/* Hibernate */
    }

    @Column(name = "CD_DOC")
    public String getCdDoc() {
	return this.cdDoc;
    }

    public void setCdDoc(String cdDoc) {
	this.cdDoc = cdDoc;
    }

    @Column(name = "DL_MOTIVO_CHIUS")
    public String getDlMotivoChius() {
	return this.dlMotivoChius;
    }

    public void setDlMotivoChius(String dlMotivoChius) {
	this.dlMotivoChius = dlMotivoChius;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SCAD_CHIUS")
    public Date getDtScadChius() {
	return this.dtScadChius;
    }

    public void setDtScadChius(Date dtScadChius) {
	this.dtScadChius = dtScadChius;
    }

    @Column(name = "ID_AMBIENTE")
    public BigDecimal getIdAmbiente() {
	return this.idAmbiente;
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
	this.idAmbiente = idAmbiente;
    }

    @Column(name = "ID_ELENCO_VERS")
    public BigDecimal getIdElencoVers() {
	return this.idElencoVers;
    }

    public void setIdElencoVers(BigDecimal idElencoVers) {
	this.idElencoVers = idElencoVers;
    }

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
	return this.idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
	this.idEnte = idEnte;
    }

    @Id
    @Column(name = "ID_LOG_ELENCO_VERS")
    public BigDecimal getIdLogElencoVers() {
	return this.idLogElencoVers;
    }

    public void setIdLogElencoVers(BigDecimal idLogElencoVers) {
	this.idLogElencoVers = idLogElencoVers;
    }

    @Column(name = "ID_LOG_JOB")
    public BigDecimal getIdLogJob() {
	return this.idLogJob;
    }

    public void setIdLogJob(BigDecimal idLogJob) {
	this.idLogJob = idLogJob;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }

    @Column(name = "NI_MAX_COMP")
    public BigDecimal getNiMaxComp() {
	return this.niMaxComp;
    }

    public void setNiMaxComp(BigDecimal niMaxComp) {
	this.niMaxComp = niMaxComp;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
	return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
	this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_COGNOME_FIRMATARIO")
    public String getNmCognomeFirmatario() {
	return this.nmCognomeFirmatario;
    }

    public void setNmCognomeFirmatario(String nmCognomeFirmatario) {
	this.nmCognomeFirmatario = nmCognomeFirmatario;
    }

    @Column(name = "NM_ELENCO")
    public String getNmElenco() {
	return this.nmElenco;
    }

    public void setNmElenco(String nmElenco) {
	this.nmElenco = nmElenco;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
	return this.nmEnte;
    }

    public void setNmEnte(String nmEnte) {
	this.nmEnte = nmEnte;
    }

    @Column(name = "NM_NOME_FIRMATARIO")
    public String getNmNomeFirmatario() {
	return this.nmNomeFirmatario;
    }

    public void setNmNomeFirmatario(String nmNomeFirmatario) {
	this.nmNomeFirmatario = nmNomeFirmatario;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
	return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
	this.nmStrut = nmStrut;
    }

    @Column(name = "TI_MOD_OPER")
    public String getTiModOper() {
	return this.tiModOper;
    }

    public void setTiModOper(String tiModOper) {
	this.tiModOper = tiModOper;
    }

    @Column(name = "TI_OPER")
    public String getTiOper() {
	return this.tiOper;
    }

    public void setTiOper(String tiOper) {
	this.tiOper = tiOper;
    }

    @Column(name = "TM_OPER")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTmOper() {
	return this.tmOper;
    }

    public void setTmOper(Date tmOper) {
	this.tmOper = tmOper;
    }

}
