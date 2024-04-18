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
 * The persistent class for the TPI_PATH_ELAB database table.
 */
@Entity
@Table(name = "TPI_PATH_ELAB")
public class TpiPathElab implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idPathElab;

    private String dlPathElab;

    private Date dtVersElab;

    private BigDecimal niFileDaElab;

    private BigDecimal niFileElab;

    private TpiSchedJob tpiSchedJob;

    public TpiPathElab() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_PATH_ELAB")
    @GenericGenerator(name = "STPI_PATH_ELAB_ID_PATH_ELAB_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "STPI_PATH_ELAB"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STPI_PATH_ELAB_ID_PATH_ELAB_GENERATOR")
    public Long getIdPathElab() {
        return this.idPathElab;
    }

    public void setIdPathElab(Long idPathElab) {
        this.idPathElab = idPathElab;
    }

    @Column(name = "DL_PATH_ELAB")
    public String getDlPathElab() {
        return this.dlPathElab;
    }

    public void setDlPathElab(String dlPathElab) {
        this.dlPathElab = dlPathElab;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_VERS_ELAB")
    public Date getDtVersElab() {
        return this.dtVersElab;
    }

    public void setDtVersElab(Date dtVersElab) {
        this.dtVersElab = dtVersElab;
    }

    @Column(name = "NI_FILE_DA_ELAB")
    public BigDecimal getNiFileDaElab() {
        return this.niFileDaElab;
    }

    public void setNiFileDaElab(BigDecimal niFileDaElab) {
        this.niFileDaElab = niFileDaElab;
    }

    @Column(name = "NI_FILE_ELAB")
    public BigDecimal getNiFileElab() {
        return this.niFileElab;
    }

    public void setNiFileElab(BigDecimal niFileElab) {
        this.niFileElab = niFileElab;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SCHED_JOB")
    public TpiSchedJob getTpiSchedJob() {
        return this.tpiSchedJob;
    }

    public void setTpiSchedJob(TpiSchedJob tpiSchedJob) {
        this.tpiSchedJob = tpiSchedJob;
    }

}
