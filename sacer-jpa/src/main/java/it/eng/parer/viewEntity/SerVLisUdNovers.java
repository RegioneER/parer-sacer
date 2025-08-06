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
 * The persistent class for the SER_V_LIS_UD_NOVERS database table.
 *
 */
@Entity
@Table(name = "SER_V_LIS_UD_NOVERS")

public class SerVLisUdNovers implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaKeyUnitaDoc;
    private String cdErrPrinc;
    private String cdKeyUnitaDoc;
    private String cdRegistroKeyUnitaDoc;
    private String cdUnitaDoc;
    private String dsErrPrinc;
    private Date dtFirstSesErr;
    private Date dtLastSesErr;
    private BigDecimal idErrContenutoVerSerie;
    private BigDecimal idStrut;
    private BigDecimal idUdNonVersErr;
    private BigDecimal idUnitaDocNonVers;

    public SerVLisUdNovers() {/* Hibernate */
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
	return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
	this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Column(name = "CD_ERR_PRINC")
    public String getCdErrPrinc() {
	return this.cdErrPrinc;
    }

    public void setCdErrPrinc(String cdErrPrinc) {
	this.cdErrPrinc = cdErrPrinc;
    }

    @Column(name = "CD_KEY_UNITA_DOC")
    public String getCdKeyUnitaDoc() {
	return this.cdKeyUnitaDoc;
    }

    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
	this.cdKeyUnitaDoc = cdKeyUnitaDoc;
    }

    @Column(name = "CD_REGISTRO_KEY_UNITA_DOC")
    public String getCdRegistroKeyUnitaDoc() {
	return this.cdRegistroKeyUnitaDoc;
    }

    public void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
	this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
    }

    @Column(name = "CD_UNITA_DOC")
    public String getCdUnitaDoc() {
	return this.cdUnitaDoc;
    }

    public void setCdUnitaDoc(String cdUnitaDoc) {
	this.cdUnitaDoc = cdUnitaDoc;
    }

    @Column(name = "DS_ERR_PRINC")
    public String getDsErrPrinc() {
	return this.dsErrPrinc;
    }

    public void setDsErrPrinc(String dsErrPrinc) {
	this.dsErrPrinc = dsErrPrinc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIRST_SES_ERR")
    public Date getDtFirstSesErr() {
	return this.dtFirstSesErr;
    }

    public void setDtFirstSesErr(Date dtFirstSesErr) {
	this.dtFirstSesErr = dtFirstSesErr;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_LAST_SES_ERR")
    public Date getDtLastSesErr() {
	return this.dtLastSesErr;
    }

    public void setDtLastSesErr(Date dtLastSesErr) {
	this.dtLastSesErr = dtLastSesErr;
    }

    @Column(name = "ID_ERR_CONTENUTO_VER_SERIE")
    public BigDecimal getIdErrContenutoVerSerie() {
	return this.idErrContenutoVerSerie;
    }

    public void setIdErrContenutoVerSerie(BigDecimal idErrContenutoVerSerie) {
	this.idErrContenutoVerSerie = idErrContenutoVerSerie;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }

    @Id
    @Column(name = "ID_UD_NON_VERS_ERR")
    public BigDecimal getIdUdNonVersErr() {
	return this.idUdNonVersErr;
    }

    public void setIdUdNonVersErr(BigDecimal idUdNonVersErr) {
	this.idUdNonVersErr = idUdNonVersErr;
    }

    @Column(name = "ID_UNITA_DOC_NON_VERS")
    public BigDecimal getIdUnitaDocNonVers() {
	return this.idUnitaDocNonVers;
    }

    public void setIdUnitaDocNonVers(BigDecimal idUnitaDocNonVers) {
	this.idUnitaDocNonVers = idUnitaDocNonVers;
    }

}
