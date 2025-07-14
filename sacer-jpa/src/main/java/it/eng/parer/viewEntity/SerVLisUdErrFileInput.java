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
 * The persistent class for the SER_V_LIS_UD_ERR_FILE_INPUT database table.
 *
 */
@Entity
@Table(name = "SER_V_LIS_UD_ERR_FILE_INPUT")

public class SerVLisUdErrFileInput implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cdKeyUnitaDoc;
    private String cdUdSerie;
    private String dsKeyOrdUdSerie;
    private Date dtUdSerie;
    private String flUnitaDocAnnul;
    private BigDecimal idErrFileInput;
    private BigDecimal idUdErrFileInput;
    private BigDecimal idUnitaDoc;
    private String infoUdSerie;
    private BigDecimal pgUdSerie;

    public SerVLisUdErrFileInput() {/* Hibernate */
    }

    @Column(name = "CD_KEY_UNITA_DOC")
    public String getCdKeyUnitaDoc() {
	return this.cdKeyUnitaDoc;
    }

    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
	this.cdKeyUnitaDoc = cdKeyUnitaDoc;
    }

    @Column(name = "CD_UD_SERIE")
    public String getCdUdSerie() {
	return this.cdUdSerie;
    }

    public void setCdUdSerie(String cdUdSerie) {
	this.cdUdSerie = cdUdSerie;
    }

    @Column(name = "DS_KEY_ORD_UD_SERIE")
    public String getDsKeyOrdUdSerie() {
	return this.dsKeyOrdUdSerie;
    }

    public void setDsKeyOrdUdSerie(String dsKeyOrdUdSerie) {
	this.dsKeyOrdUdSerie = dsKeyOrdUdSerie;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_UD_SERIE")
    public Date getDtUdSerie() {
	return this.dtUdSerie;
    }

    public void setDtUdSerie(Date dtUdSerie) {
	this.dtUdSerie = dtUdSerie;
    }

    @Column(name = "FL_UNITA_DOC_ANNUL", columnDefinition = "char(1)")
    public String getFlUnitaDocAnnul() {
	return this.flUnitaDocAnnul;
    }

    public void setFlUnitaDocAnnul(String flUnitaDocAnnul) {
	this.flUnitaDocAnnul = flUnitaDocAnnul;
    }

    @Column(name = "ID_ERR_FILE_INPUT")
    public BigDecimal getIdErrFileInput() {
	return this.idErrFileInput;
    }

    public void setIdErrFileInput(BigDecimal idErrFileInput) {
	this.idErrFileInput = idErrFileInput;
    }

    @Id
    @Column(name = "ID_UD_ERR_FILE_INPUT")
    public BigDecimal getIdUdErrFileInput() {
	return this.idUdErrFileInput;
    }

    public void setIdUdErrFileInput(BigDecimal idUdErrFileInput) {
	this.idUdErrFileInput = idUdErrFileInput;
    }

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
	return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
	this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "INFO_UD_SERIE")
    public String getInfoUdSerie() {
	return this.infoUdSerie;
    }

    public void setInfoUdSerie(String infoUdSerie) {
	this.infoUdSerie = infoUdSerie;
    }

    @Column(name = "PG_UD_SERIE")
    public BigDecimal getPgUdSerie() {
	return this.pgUdSerie;
    }

    public void setPgUdSerie(BigDecimal pgUdSerie) {
	this.pgUdSerie = pgUdSerie;
    }

}
