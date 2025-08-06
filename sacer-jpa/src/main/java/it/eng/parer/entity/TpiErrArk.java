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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the TPI_ERR_ARK database table.
 */
@Entity
@Table(name = "TPI_ERR_ARK")
public class TpiErrArk implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idErrArk;

    private String cdErrArk;

    private String dlErrArk;

    private BigDecimal niErrArk;

    private String tiErrArk;

    private TpiSchedJob tpiSchedJob;

    public TpiErrArk() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_ERR_ARK")
    @GenericGenerator(name = "STPI_ERR_ARK_ID_ERR_ARK_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "STPI_ERR_ARK"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STPI_ERR_ARK_ID_ERR_ARK_GENERATOR")
    public Long getIdErrArk() {
	return this.idErrArk;
    }

    public void setIdErrArk(Long idErrArk) {
	this.idErrArk = idErrArk;
    }

    @Column(name = "CD_ERR_ARK")
    public String getCdErrArk() {
	return this.cdErrArk;
    }

    public void setCdErrArk(String cdErrArk) {
	this.cdErrArk = cdErrArk;
    }

    @Column(name = "DL_ERR_ARK")
    public String getDlErrArk() {
	return this.dlErrArk;
    }

    public void setDlErrArk(String dlErrArk) {
	this.dlErrArk = dlErrArk;
    }

    @Column(name = "NI_ERR_ARK")
    public BigDecimal getNiErrArk() {
	return this.niErrArk;
    }

    public void setNiErrArk(BigDecimal niErrArk) {
	this.niErrArk = niErrArk;
    }

    @Column(name = "TI_ERR_ARK")
    public String getTiErrArk() {
	return this.tiErrArk;
    }

    public void setTiErrArk(String tiErrArk) {
	this.tiErrArk = tiErrArk;
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
