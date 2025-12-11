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
 * The persistent class for the LOG_V_LIS_INI_SCHED_JOB database table.
 *
 */
@Entity
@Table(name = "LOG_V_LIS_INI_SCHED_JOB")

public class LogVLisIniSchedJob implements Serializable {

    private static final long serialVersionUID = 1L;
    private Date dtRegLogJobFineSuc;
    private Date dtRegLogJobIni;
    private Date dtRegLogJobIniSuc;
    private BigDecimal idLogJob;
    private String nmJob;

    public LogVLisIniSchedJob() {/* Hibernate */
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_LOG_JOB_FINE_SUC")
    public Date getDtRegLogJobFineSuc() {
        return this.dtRegLogJobFineSuc;
    }

    public void setDtRegLogJobFineSuc(Date dtRegLogJobFineSuc) {
        this.dtRegLogJobFineSuc = dtRegLogJobFineSuc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_LOG_JOB_INI")
    public Date getDtRegLogJobIni() {
        return this.dtRegLogJobIni;
    }

    public void setDtRegLogJobIni(Date dtRegLogJobIni) {
        this.dtRegLogJobIni = dtRegLogJobIni;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_LOG_JOB_INI_SUC")
    public Date getDtRegLogJobIniSuc() {
        return this.dtRegLogJobIniSuc;
    }

    public void setDtRegLogJobIniSuc(Date dtRegLogJobIniSuc) {
        this.dtRegLogJobIniSuc = dtRegLogJobIniSuc;
    }

    @Id
    @Column(name = "ID_LOG_JOB")
    public BigDecimal getIdLogJob() {
        return this.idLogJob;
    }

    public void setIdLogJob(BigDecimal idLogJob) {
        this.idLogJob = idLogJob;
    }

    @Column(name = "NM_JOB")
    public String getNmJob() {
        return this.nmJob;
    }

    public void setNmJob(String nmJob) {
        this.nmJob = nmJob;
    }

}
