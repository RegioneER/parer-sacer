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
 * The persistent class for the ELV_V_LIS_UPD_UD database table.
 *
 */
@Entity
@Table(name = "ELV_V_LIS_UPD_UD")

public class ElvVLisUpdUd implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal idElencoVers;
    private BigDecimal idUnitaDoc;
    private BigDecimal idStrutUnitaDoc;
    private String cdRegistroKeyUnitaDoc;
    private BigDecimal aaKeyUnitaDoc;
    private String cdKeyUnitaDoc;
    private String dsKeyOrd;
    private String tiStatoUpdElencoVers;
    private String tiStatoConservazione;
    private BigDecimal idUpdUnitaDoc;
    private BigDecimal pgUpdUnitaDoc;
    private Date tsIniSes;
    private String dsUrnUpdUnitaDoc;

    public ElvVLisUpdUd() {/* Hibernate */
    }

    public ElvVLisUpdUd(BigDecimal idElencoVers, BigDecimal idUpdUnitaDoc, String dsUrnUpdUnitaDoc,
	    String dsKeyOrd, Date tsIniSes, BigDecimal idUnitaDoc, String cdRegistroKeyUnitaDoc,
	    String tiStatoUpdElencoVers, String tiStatoConservazione) {
	this.idElencoVers = idElencoVers;
	this.idUpdUnitaDoc = idUpdUnitaDoc;
	this.dsUrnUpdUnitaDoc = dsUrnUpdUnitaDoc;
	this.dsKeyOrd = dsKeyOrd;
	this.tsIniSes = tsIniSes;
	this.idUnitaDoc = idUnitaDoc;
	this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
	this.tiStatoUpdElencoVers = tiStatoUpdElencoVers;
	this.tiStatoConservazione = tiStatoConservazione;
    }

    @Column(name = "ID_ELENCO_VERS")
    public BigDecimal getIdElencoVers() {
	return this.idElencoVers;
    }

    public void setIdElencoVers(BigDecimal idElencoVers) {
	this.idElencoVers = idElencoVers;
    }

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
	return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
	this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "ID_STRUT_UNITA_DOC")
    public BigDecimal getIdStrutUnitaDoc() {
	return this.idStrutUnitaDoc;
    }

    public void setIdStrutUnitaDoc(BigDecimal idStrutUnitaDoc) {
	this.idStrutUnitaDoc = idStrutUnitaDoc;
    }

    @Column(name = "CD_REGISTRO_KEY_UNITA_DOC")
    public String getCdRegistroKeyUnitaDoc() {
	return this.cdRegistroKeyUnitaDoc;
    }

    public void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
	this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
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

    @Column(name = "DS_KEY_ORD")
    public String getDsKeyOrd() {
	return this.dsKeyOrd;
    }

    public void setDsKeyOrd(String dsKeyOrd) {
	this.dsKeyOrd = dsKeyOrd;
    }

    @Column(name = "TI_STATO_UPD_ELENCO_VERS")
    public String getTiStatoUpdElencoVers() {
	return this.tiStatoUpdElencoVers;
    }

    public void setTiStatoUpdElencoVers(String tiStatoUpdElencoVers) {
	this.tiStatoUpdElencoVers = tiStatoUpdElencoVers;
    }

    @Column(name = "TI_STATO_CONSERVAZIONE")
    public String getTiStatoConservazione() {
	return this.tiStatoConservazione;
    }

    public void setTiStatoConservazione(String tiStatoConservazione) {
	this.tiStatoConservazione = tiStatoConservazione;
    }

    @Id
    @Column(name = "ID_UPD_UNITA_DOC")
    public BigDecimal getIdUpdUnitaDoc() {
	return this.idUpdUnitaDoc;
    }

    public void setIdUpdUnitaDoc(BigDecimal idUpdUnitaDoc) {
	this.idUpdUnitaDoc = idUpdUnitaDoc;
    }

    @Column(name = "PG_UPD_UNITA_DOC")
    public BigDecimal getPgUpdUnitaDoc() {
	return this.pgUpdUnitaDoc;
    }

    public void setPgUpdUnitaDoc(BigDecimal pgUpdUnitaDoc) {
	this.pgUpdUnitaDoc = pgUpdUnitaDoc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_INI_SES")
    public Date getTsIniSes() {
	return this.tsIniSes;
    }

    public void setTsIniSes(Date tsIniSes) {
	this.tsIniSes = tsIniSes;
    }

    @Column(name = "DS_URN_UPD_UNITA_DOC")
    public String getDsUrnUpdUnitaDoc() {
	return this.dsUrnUpdUnitaDoc;
    }

    public void setDsUrnUpdUnitaDoc(String dsUrnUpdUnitaDoc) {
	this.dsUrnUpdUnitaDoc = dsUrnUpdUnitaDoc;
    }
}
