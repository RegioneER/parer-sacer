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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the TPI_SCHED_JOB database table.
 */
@Entity
@Table(name = "TPI_SCHED_JOB")
public class TpiSchedJob implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idSchedJob;

    private String dlErrJob;

    private String dsDurataJob;

    private Date dtSchedJob;

    private String flFineSched;

    private String flJobOk;

    private String flMigraz;

    private BigDecimal niOrdSchedJob;

    private String nmJob;

    private String tiTpiSchedJob;

    private List<TpiErrArk> tpiErrArks = new ArrayList<>();

    private TpiDtSched tpiDtSched;

    private List<TpiPathElab> tpiPathElabs = new ArrayList<>();

    public TpiSchedJob() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_SCHED_JOB")
    @GenericGenerator(name = "STPI_SCHED_JOB_ID_SCHED_JOB_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "STPI_SCHED_JOB"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STPI_SCHED_JOB_ID_SCHED_JOB_GENERATOR")
    public Long getIdSchedJob() {
        return this.idSchedJob;
    }

    public void setIdSchedJob(Long idSchedJob) {
        this.idSchedJob = idSchedJob;
    }

    @Column(name = "DL_ERR_JOB")
    public String getDlErrJob() {
        return this.dlErrJob;
    }

    public void setDlErrJob(String dlErrJob) {
        this.dlErrJob = dlErrJob;
    }

    @Column(name = "DS_DURATA_JOB")
    public String getDsDurataJob() {
        return this.dsDurataJob;
    }

    public void setDsDurataJob(String dsDurataJob) {
        this.dsDurataJob = dsDurataJob;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SCHED_JOB")
    public Date getDtSchedJob() {
        return this.dtSchedJob;
    }

    public void setDtSchedJob(Date dtSchedJob) {
        this.dtSchedJob = dtSchedJob;
    }

    @Column(name = "FL_FINE_SCHED", columnDefinition = "char(1)")
    public String getFlFineSched() {
        return this.flFineSched;
    }

    public void setFlFineSched(String flFineSched) {
        this.flFineSched = flFineSched;
    }

    @Column(name = "FL_JOB_OK", columnDefinition = "char(1)")
    public String getFlJobOk() {
        return this.flJobOk;
    }

    public void setFlJobOk(String flJobOk) {
        this.flJobOk = flJobOk;
    }

    @Column(name = "FL_MIGRAZ", columnDefinition = "char(1)")
    public String getFlMigraz() {
        return this.flMigraz;
    }

    public void setFlMigraz(String flMigraz) {
        this.flMigraz = flMigraz;
    }

    @Column(name = "NI_ORD_SCHED_JOB")
    public BigDecimal getNiOrdSchedJob() {
        return this.niOrdSchedJob;
    }

    public void setNiOrdSchedJob(BigDecimal niOrdSchedJob) {
        this.niOrdSchedJob = niOrdSchedJob;
    }

    @Column(name = "NM_JOB")
    public String getNmJob() {
        return this.nmJob;
    }

    public void setNmJob(String nmJob) {
        this.nmJob = nmJob;
    }

    @Column(name = "TI_TPI_SCHED_JOB")
    public String getTiTpiSchedJob() {
        return this.tiTpiSchedJob;
    }

    public void setTiTpiSchedJob(String tiTpiSchedJob) {
        this.tiTpiSchedJob = tiTpiSchedJob;
    }

    @OneToMany(cascade = { CascadeType.PERSIST }, mappedBy = "tpiSchedJob")
    public List<TpiErrArk> getTpiErrArks() {
        return this.tpiErrArks;
    }

    public void setTpiErrArks(List<TpiErrArk> tpiErrArks) {
        this.tpiErrArks = tpiErrArks;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DT_SCHED")
    public TpiDtSched getTpiDtSched() {
        return this.tpiDtSched;
    }

    public void setTpiDtSched(TpiDtSched tpiDtSched) {
        this.tpiDtSched = tpiDtSched;
    }

    @OneToMany(cascade = { CascadeType.PERSIST }, mappedBy = "tpiSchedJob")
    public List<TpiPathElab> getTpiPathElabs() {
        return this.tpiPathElabs;
    }

    public void setTpiPathElabs(List<TpiPathElab> tpiPathElabs) {
        this.tpiPathElabs = tpiPathElabs;
    }

}
