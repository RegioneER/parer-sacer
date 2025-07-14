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
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the LOG_OPER database table.
 */
@Entity
@Table(name = "LOG_OPER")
public class LogOper implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idLogOper;

    private BigDecimal aaKeyUnitaDoc;

    private String cdKeyUnitaDoc;

    private String cdRegistroKeyUnitaDoc;

    private Date dtOper;

    private BigDecimal idDoc;

    private BigDecimal idVolumeConserv;

    private String nmVolumeConserv;

    private BigDecimal pgDoc;

    private String tiDoc;

    private String tiOper;

    private LogJobHistory logJob;

    private OrgStrut orgStrut;

    private IamUser iamUser;

    public LogOper() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_LOG_OPER")
    @GenericGenerator(name = "SLOG_OPER_ID_LOG_OPER_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SLOG_OPER"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SLOG_OPER_ID_LOG_OPER_GENERATOR")
    public Long getIdLogOper() {
	return this.idLogOper;
    }

    public void setIdLogOper(Long idLogOper) {
	this.idLogOper = idLogOper;
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

    @Column(name = "CD_REGISTRO_KEY_UNITA_DOC")
    public String getCdRegistroKeyUnitaDoc() {
	return this.cdRegistroKeyUnitaDoc;
    }

    public void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
	this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_OPER")
    public Date getDtOper() {
	return this.dtOper;
    }

    public void setDtOper(Date dtOper) {
	this.dtOper = dtOper;
    }

    @Column(name = "ID_DOC")
    public BigDecimal getIdDoc() {
	return this.idDoc;
    }

    public void setIdDoc(BigDecimal idDoc) {
	this.idDoc = idDoc;
    }

    @Column(name = "ID_VOLUME_CONSERV")
    public BigDecimal getIdVolumeConserv() {
	return this.idVolumeConserv;
    }

    public void setIdVolumeConserv(BigDecimal idVolumeConserv) {
	this.idVolumeConserv = idVolumeConserv;
    }

    @Column(name = "NM_VOLUME_CONSERV")
    public String getNmVolumeConserv() {
	return this.nmVolumeConserv;
    }

    public void setNmVolumeConserv(String nmVolumeConserv) {
	this.nmVolumeConserv = nmVolumeConserv;
    }

    @Column(name = "PG_DOC")
    public BigDecimal getPgDoc() {
	return this.pgDoc;
    }

    public void setPgDoc(BigDecimal pgDoc) {
	this.pgDoc = pgDoc;
    }

    @Column(name = "TI_DOC")
    public String getTiDoc() {
	return this.tiDoc;
    }

    public void setTiDoc(String tiDoc) {
	this.tiDoc = tiDoc;
    }

    @Column(name = "TI_OPER")
    public String getTiOper() {
	return this.tiOper;
    }

    public void setTiOper(String tiOper) {
	this.tiOper = tiOper;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_LOG_JOB")
    public LogJobHistory getLogJob() {
	return this.logJob;
    }

    public void setLogJob(LogJobHistory logJob) {
	this.logJob = logJob;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    public OrgStrut getOrgStrut() {
	return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
	this.orgStrut = orgStrut;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER")
    public IamUser getIamUser() {
	return this.iamUser;
    }

    public void setIamUser(IamUser iamUser) {
	this.iamUser = iamUser;
    }
}
