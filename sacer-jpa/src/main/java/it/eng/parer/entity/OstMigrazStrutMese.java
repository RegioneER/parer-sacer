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
 * The persistent class for the OST_MIGRAZ_STRUT_MESE database table.
 */
@Entity
@Table(name = "OST_MIGRAZ_STRUT_MESE")

public class OstMigrazStrutMese implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idMigrazStrutMese;

    private Date dtVersFine;

    private Date dtVersIni;

    private String flFileAggiunti;

    private BigDecimal idStrut;

    private BigDecimal mmVers;

    private OstMigrazSubPart ostMigrazSubPart;

    public OstMigrazStrutMese() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_MIGRAZ_STRUT_MESE")
    @GenericGenerator(name = "SOST_MIGRAZ_STRUT_MESE_ID_MIGRAZ_STRUT_MESE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SOST_MIGRAZ_STRUT_MESE"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SOST_MIGRAZ_STRUT_MESE_ID_MIGRAZ_STRUT_MESE_GENERATOR")
    public Long getIdMigrazStrutMese() {
	return this.idMigrazStrutMese;
    }

    public void setIdMigrazStrutMese(Long idMigrazStrutMese) {
	this.idMigrazStrutMese = idMigrazStrutMese;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_VERS_FINE")
    public Date getDtVersFine() {
	return this.dtVersFine;
    }

    public void setDtVersFine(Date dtVersFine) {
	this.dtVersFine = dtVersFine;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_VERS_INI")
    public Date getDtVersIni() {
	return this.dtVersIni;
    }

    public void setDtVersIni(Date dtVersIni) {
	this.dtVersIni = dtVersIni;
    }

    @Column(name = "FL_FILE_AGGIUNTI", columnDefinition = "char(1)")
    public String getFlFileAggiunti() {
	return this.flFileAggiunti;
    }

    public void setFlFileAggiunti(String flFileAggiunti) {
	this.flFileAggiunti = flFileAggiunti;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }

    @Column(name = "MM_VERS")
    public BigDecimal getMmVers() {
	return this.mmVers;
    }

    public void setMmVers(BigDecimal mmVers) {
	this.mmVers = mmVers;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MIGRAZ_SUB_PART")
    public OstMigrazSubPart getOstMigrazSubPart() {
	return this.ostMigrazSubPart;
    }

    public void setOstMigrazSubPart(OstMigrazSubPart ostMigrazSubPart) {
	this.ostMigrazSubPart = ostMigrazSubPart;
    }
}
