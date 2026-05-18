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
import javax.persistence.SqlResultSetMapping;
import javax.persistence.ConstructorResult;
import javax.persistence.ColumnResult;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import it.eng.parer.entity.dto.LogVpLisIniSchedJobHistDTO;

/**
 * The persistent class for the LOG_VP_LIS_INI_SCHED_JOB_HIST database table/view/macro.
 *
 */
@Entity
@Immutable // Indica a Hibernate che l'entità è in sola lettura
@Subselect("SELECT * FROM DUAL") // Evita che Hibernate cerchi una tabella fisica all'avvio
// ===================================================================================
// Definisce come mappare il risultato della query nativa al DTO.
// ===================================================================================
@SqlResultSetMapping(name = "LogVpLisIniSchedJobHistDTOMapping", classes = @ConstructorResult(targetClass = LogVpLisIniSchedJobHistDTO.class, columns = {
        @ColumnResult(name = "ID_LOG_JOB_HIST", type = BigDecimal.class),
        @ColumnResult(name = "NM_JOB", type = String.class),
        @ColumnResult(name = "DT_REG_LOG_JOB_INI", type = Date.class),
        @ColumnResult(name = "DT_REG_LOG_JOB_FINE", type = Date.class),
        @ColumnResult(name = "DURATA", type = String.class),
        @ColumnResult(name = "DL_MSG_ERR", type = String.class)
}))
public class LogVpLisIniSchedJobHist implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal idLogJobHist;
    private String nmJob;
    private Date dtRegLogJobIni;
    private Date dtRegLogJobFine;
    private String durata;
    private String dlMsgErr;

    public LogVpLisIniSchedJobHist() {
        /* Hibernate */
    }

    @Id
    @Column(name = "ID_LOG_JOB_HIST")
    public BigDecimal getIdLogJobHist() {
        return idLogJobHist;
    }

    public void setIdLogJobHist(BigDecimal idLogJobHist) {
        this.idLogJobHist = idLogJobHist;
    }

    @Column(name = "NM_JOB")
    public String getNmJob() {
        return nmJob;
    }

    public void setNmJob(String nmJob) {
        this.nmJob = nmJob;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_LOG_JOB_INI")
    public Date getDtRegLogJobIni() {
        return dtRegLogJobIni;
    }

    public void setDtRegLogJobIni(Date dtRegLogJobIni) {
        this.dtRegLogJobIni = dtRegLogJobIni;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_LOG_JOB_FINE")
    public Date getDtRegLogJobFine() {
        return dtRegLogJobFine;
    }

    public void setDtRegLogJobFine(Date dtRegLogJobFine) {
        this.dtRegLogJobFine = dtRegLogJobFine;
    }

    @Column(name = "DURATA")
    public String getDurata() {
        return durata;
    }

    public void setDurata(String durata) {
        this.durata = durata;
    }

    @Column(name = "DL_MSG_ERR")
    public String getDlMsgErr() {
        return dlMsgErr;
    }

    public void setDlMsgErr(String dlMsgErr) {
        this.dlMsgErr = dlMsgErr;
    }
}