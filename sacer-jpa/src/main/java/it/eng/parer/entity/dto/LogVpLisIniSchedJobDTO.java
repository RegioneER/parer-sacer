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

package it.eng.parer.entity.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) per rappresentare il risultato dei log relativi ai Job Schedulati.
 */
public class LogVpLisIniSchedJobDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal idLogJob;
    private String nmJob;
    private Date dtRegLogJobIni;
    private Date dtRegLogJobFine;
    // private Date dtRegLogJobIniSuc;
    private String durata;
    private String dlMsgErr;

    /**
     * Costruttore di default.
     */
    public LogVpLisIniSchedJobDTO() {
        // Costruttore vuoto
    }

    public LogVpLisIniSchedJobDTO(BigDecimal idLogJob, String nmJob, Date dtRegLogJobIni,
            Date dtRegLogJobFine,
            // Date dtRegLogJobIniSuc,
            String durata, String dlMsgErr) {
        this.idLogJob = idLogJob;
        this.nmJob = nmJob;
        this.dtRegLogJobIni = dtRegLogJobIni;
        this.dtRegLogJobFine = dtRegLogJobFine;
        // this.dtRegLogJobIniSuc = dtRegLogJobIniSuc;
        this.durata = durata;
        this.dlMsgErr = dlMsgErr;
    }

    public BigDecimal getIdLogJob() {
        return idLogJob;
    }

    public void setIdLogJob(BigDecimal idLogJob) {
        this.idLogJob = idLogJob;
    }

    public String getNmJob() {
        return nmJob;
    }

    public void setNmJob(String nmJob) {
        this.nmJob = nmJob;
    }

    public Date getDtRegLogJobIni() {
        return dtRegLogJobIni;
    }

    public void setDtRegLogJobIni(Date dtRegLogJobIni) {
        this.dtRegLogJobIni = dtRegLogJobIni;
    }

    public Date getDtRegLogJobFine() {
        return dtRegLogJobFine;
    }

    public void setDtRegLogJobFine(Date dtRegLogJobFine) {
        this.dtRegLogJobFine = dtRegLogJobFine;
    }

    // public Date getDtRegLogJobIniSuc() {
    // return dtRegLogJobIniSuc;
    // }
    //
    // public void setDtRegLogJobIniSuc(Date dtRegLogJobIniSuc) {
    // this.dtRegLogJobIniSuc = dtRegLogJobIniSuc;
    // }

    public String getDurata() {
        return durata;
    }

    public void setDurata(String durata) {
        this.durata = durata;
    }

    public String getDlMsgErr() {
        return dlMsgErr;
    }

    public void setDlMsgErr(String dlMsgErr) {
        this.dlMsgErr = dlMsgErr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LogVpLisIniSchedJobDTO that = (LogVpLisIniSchedJobDTO) o;
        // Due DTO sono uguali se il loro idLogJob è uguale.
        return Objects.equals(idLogJob, that.idLogJob);
    }

    @Override
    public int hashCode() {
        // Coerentemente, l'hashCode si basa solo su idLogJob.
        return Objects.hash(idLogJob);
    }
}