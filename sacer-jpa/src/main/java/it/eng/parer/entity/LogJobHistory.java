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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the LOG_JOB_HISTORY database table.
 */
@Entity
@Table(name = "LOG_JOB_HISTORY")
public class LogJobHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idLogJob;

    private String dlMsgErr;

    private Date dtRegLogJob;

    private BigDecimal idRecord;

    private String nmJob;

    private String nmEntityRecord;

    private String tiRegLogJob;

    private String cdIndServer;

    private OrgStrut orgStrut;

    public LogJobHistory() {/* Hibernate */
    }

    @Id
    // sequenceName = "SLOG_JOB", allocationSize = 1)
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LOG_JOB_IDLOGJOB_GENERATOR")
    @Column(name = "ID_LOG_JOB_HIST")
    @GenericGenerator(name = "SLOG_JOB_ID_LOG_JOB_HIST_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SLOG_JOB"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SLOG_JOB_ID_LOG_JOB_HIST_GENERATOR")
    public Long getIdLogJob() {
        return this.idLogJob;
    }

    public void setIdLogJob(Long idLogJob) {
        this.idLogJob = idLogJob;
    }

    @Column(name = "DL_MSG_ERR")
    public String getDlMsgErr() {
        return this.dlMsgErr;
    }

    public void setDlMsgErr(String dlMsgErr) {
        this.dlMsgErr = dlMsgErr;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_LOG_JOB")
    public Date getDtRegLogJob() {
        return this.dtRegLogJob;
    }

    public void setDtRegLogJob(Date dtRegLogJob) {
        this.dtRegLogJob = dtRegLogJob;
    }

    @Column(name = "ID_RECORD")
    public BigDecimal getIdRecord() {
        return this.idRecord;
    }

    public void setIdRecord(BigDecimal idRecord) {
        this.idRecord = idRecord;
    }

    @Column(name = "NM_JOB")
    public String getNmJob() {
        return this.nmJob;
    }

    public void setNmJob(String nmJob) {
        this.nmJob = nmJob;
    }

    @Column(name = "NM_ENTITY_RECORD")
    public String getNmEntityRecord() {
        return this.nmEntityRecord;
    }

    public void setNmEntityRecord(String nmEntityRecord) {
        this.nmEntityRecord = nmEntityRecord;
    }

    @Column(name = "TI_REG_LOG_JOB")
    public String getTiRegLogJob() {
        return this.tiRegLogJob;
    }

    public void setTiRegLogJob(String tiRegLogJob) {
        this.tiRegLogJob = tiRegLogJob;
    }

    @Column(name = "CD_IND_SERVER")
    public String getCdIndServer() {
        return cdIndServer;
    }

    public void setCdIndServer(String cdIndServer) {
        this.cdIndServer = cdIndServer;
    }

    // bi-directional many-to-one association to OrgStrut
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    public OrgStrut getOrgStrut() {
        return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
        this.orgStrut = orgStrut;
    }

}
