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
 * The persistent class for the ELV_LOG_ELENCO_VERS database table.
 */
@Entity
@Table(name = "ELV_LOG_ELENCO_VERS")

public class ElvLogElencoVer implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idLogElencoVers;

    private BigDecimal aaKeyUnitaDoc;

    private String cdKeyUnitaDoc;

    private String cdRegistroKeyUnitaDoc;

    private BigDecimal idElencoVers;

    private String nmElenco;

    private BigDecimal pgDoc;

    private BigDecimal pgUpdUnitaDoc;

    private String tiDoc;

    private String tiOper;

    private Date tmOper;

    private IamUser iamUser;

    private LogJob logJob;

    private OrgStrut orgStrut;

    public ElvLogElencoVer() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_LOG_ELENCO_VERS")
    @GenericGenerator(name = "SELV_LOG_ELENCO_VERS_ID_LOG_ELENCO_VERS_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SELV_LOG_ELENCO_VERS"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SELV_LOG_ELENCO_VERS_ID_LOG_ELENCO_VERS_GENERATOR")
    public Long getIdLogElencoVers() {
        return this.idLogElencoVers;
    }

    public void setIdLogElencoVers(Long idLogElencoVers) {
        this.idLogElencoVers = idLogElencoVers;
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

    @Column(name = "ID_ELENCO_VERS")
    public BigDecimal getIdElencoVers() {
        return this.idElencoVers;
    }

    public void setIdElencoVers(BigDecimal idElencoVers) {
        this.idElencoVers = idElencoVers;
    }

    @Column(name = "NM_ELENCO")
    public String getNmElenco() {
        return this.nmElenco;
    }

    public void setNmElenco(String nmElenco) {
        this.nmElenco = nmElenco;
    }

    @Column(name = "PG_DOC")
    public BigDecimal getPgDoc() {
        return this.pgDoc;
    }

    public void setPgDoc(BigDecimal pgDoc) {
        this.pgDoc = pgDoc;
    }

    @Column(name = "PG_UPD_UNITA_DOC")
    public BigDecimal getPgUpdUnitaDoc() {
        return this.pgUpdUnitaDoc;
    }

    public void setPgUpdUnitaDoc(BigDecimal pgUpdUnitaDoc) {
        this.pgUpdUnitaDoc = pgUpdUnitaDoc;
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

    @Column(name = "TM_OPER")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTmOper() {
        return this.tmOper;
    }

    public void setTmOper(Date tmOper) {
        this.tmOper = tmOper;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER")
    public IamUser getIamUser() {
        return this.iamUser;
    }

    public void setIamUser(IamUser iamUser) {
        this.iamUser = iamUser;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_LOG_JOB")
    public LogJob getLogJob() {
        return this.logJob;
    }

    public void setLogJob(LogJob logJob) {
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

}
