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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import javax.persistence.Table;

/**
 * The persistent class for the MON_V_VIS_UPD_UD_ERR database table.
 *
 */
@Entity
@Table(name = "MON_V_VIS_UPD_UD_ERR")

public class MonVVisUpdUdErr implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaKeyUnitaDoc;
    private String blXmlRich;
    private String blXmlRisp;
    private String cdControlloWsPrinc;
    private String cdErrPrinc;
    private String cdKeyUnitaDoc;
    private String cdRegistroKeyUnitaDoc;
    private String cdVersioneWs;
    private String dsErrPrinc;
    private String dsTsFineSes;
    private String dsTsIniSes;
    private String flEsisteRegistro;
    private String flEsisteStrut;
    private String flEsisteTipoDocPrinc;
    private String flEsisteTipoUnitaDoc;
    private BigDecimal idControlloWsPrinc;
    private BigDecimal idErrSacerPrinc;
    private BigDecimal idSesUpdUnitaDocErr;
    private BigDecimal idXmlSesUpdErrRich;
    private BigDecimal idXmlSesUpdErrRisp;
    private String nmAmbiente;
    private String nmEnte;
    private String nmStrut;
    private String nmTipoDocPrinc;
    private String nmTipoUnitaDoc;
    private String nmUseridWs;
    private String tiStatoSes;

    public MonVVisUpdUdErr() {/* Hibernate */
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
	return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
	this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Lob
    @Column(name = "BL_XML_RICH")
    public String getBlXmlRich() {
	return this.blXmlRich;
    }

    public void setBlXmlRich(String blXmlRich) {
	this.blXmlRich = blXmlRich;
    }

    @Lob
    @Column(name = "BL_XML_RISP")
    public String getBlXmlRisp() {
	return this.blXmlRisp;
    }

    public void setBlXmlRisp(String blXmlRisp) {
	this.blXmlRisp = blXmlRisp;
    }

    @Column(name = "CD_CONTROLLO_WS_PRINC")
    public String getCdControlloWsPrinc() {
	return this.cdControlloWsPrinc;
    }

    public void setCdControlloWsPrinc(String cdControlloWsPrinc) {
	this.cdControlloWsPrinc = cdControlloWsPrinc;
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

    @Column(name = "CD_VERSIONE_WS")
    public String getCdVersioneWs() {
	return this.cdVersioneWs;
    }

    public void setCdVersioneWs(String cdVersioneWs) {
	this.cdVersioneWs = cdVersioneWs;
    }

    @Column(name = "DS_ERR_PRINC")
    public String getDsErrPrinc() {
	return this.dsErrPrinc;
    }

    public void setDsErrPrinc(String dsErrPrinc) {
	this.dsErrPrinc = dsErrPrinc;
    }

    @Column(name = "DS_TS_FINE_SES")
    public String getDsTsFineSes() {
	return this.dsTsFineSes;
    }

    public void setDsTsFineSes(String dsTsFineSes) {
	this.dsTsFineSes = dsTsFineSes;
    }

    @Column(name = "DS_TS_INI_SES")
    public String getDsTsIniSes() {
	return this.dsTsIniSes;
    }

    public void setDsTsIniSes(String dsTsIniSes) {
	this.dsTsIniSes = dsTsIniSes;
    }

    @Column(name = "FL_ESISTE_REGISTRO", columnDefinition = "char(1)")
    public String getFlEsisteRegistro() {
	return this.flEsisteRegistro;
    }

    public void setFlEsisteRegistro(String flEsisteRegistro) {
	this.flEsisteRegistro = flEsisteRegistro;
    }

    @Column(name = "FL_ESISTE_STRUT", columnDefinition = "char(1)")
    public String getFlEsisteStrut() {
	return this.flEsisteStrut;
    }

    public void setFlEsisteStrut(String flEsisteStrut) {
	this.flEsisteStrut = flEsisteStrut;
    }

    @Column(name = "FL_ESISTE_TIPO_DOC_PRINC", columnDefinition = "char(1)")
    public String getFlEsisteTipoDocPrinc() {
	return this.flEsisteTipoDocPrinc;
    }

    public void setFlEsisteTipoDocPrinc(String flEsisteTipoDocPrinc) {
	this.flEsisteTipoDocPrinc = flEsisteTipoDocPrinc;
    }

    @Column(name = "FL_ESISTE_TIPO_UNITA_DOC", columnDefinition = "char(1)")
    public String getFlEsisteTipoUnitaDoc() {
	return this.flEsisteTipoUnitaDoc;
    }

    public void setFlEsisteTipoUnitaDoc(String flEsisteTipoUnitaDoc) {
	this.flEsisteTipoUnitaDoc = flEsisteTipoUnitaDoc;
    }

    @Column(name = "ID_CONTROLLO_WS_PRINC")
    public BigDecimal getIdControlloWsPrinc() {
	return this.idControlloWsPrinc;
    }

    public void setIdControlloWsPrinc(BigDecimal idControlloWsPrinc) {
	this.idControlloWsPrinc = idControlloWsPrinc;
    }

    @Column(name = "ID_ERR_SACER_PRINC")
    public BigDecimal getIdErrSacerPrinc() {
	return this.idErrSacerPrinc;
    }

    public void setIdErrSacerPrinc(BigDecimal idErrSacerPrinc) {
	this.idErrSacerPrinc = idErrSacerPrinc;
    }

    @Id
    @Column(name = "ID_SES_UPD_UNITA_DOC_ERR")
    public BigDecimal getIdSesUpdUnitaDocErr() {
	return this.idSesUpdUnitaDocErr;
    }

    public void setIdSesUpdUnitaDocErr(BigDecimal idSesUpdUnitaDocErr) {
	this.idSesUpdUnitaDocErr = idSesUpdUnitaDocErr;
    }

    @Column(name = "ID_XML_SES_UPD_ERR_RICH")
    public BigDecimal getIdXmlSesUpdErrRich() {
	return this.idXmlSesUpdErrRich;
    }

    public void setIdXmlSesUpdErrRich(BigDecimal idXmlSesUpdErrRich) {
	this.idXmlSesUpdErrRich = idXmlSesUpdErrRich;
    }

    @Column(name = "ID_XML_SES_UPD_ERR_RISP")
    public BigDecimal getIdXmlSesUpdErrRisp() {
	return this.idXmlSesUpdErrRisp;
    }

    public void setIdXmlSesUpdErrRisp(BigDecimal idXmlSesUpdErrRisp) {
	this.idXmlSesUpdErrRisp = idXmlSesUpdErrRisp;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
	return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
	this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
	return this.nmEnte;
    }

    public void setNmEnte(String nmEnte) {
	this.nmEnte = nmEnte;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
	return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
	this.nmStrut = nmStrut;
    }

    @Column(name = "NM_TIPO_DOC_PRINC")
    public String getNmTipoDocPrinc() {
	return this.nmTipoDocPrinc;
    }

    public void setNmTipoDocPrinc(String nmTipoDocPrinc) {
	this.nmTipoDocPrinc = nmTipoDocPrinc;
    }

    @Column(name = "NM_TIPO_UNITA_DOC")
    public String getNmTipoUnitaDoc() {
	return this.nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
	this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    @Column(name = "NM_USERID_WS")
    public String getNmUseridWs() {
	return this.nmUseridWs;
    }

    public void setNmUseridWs(String nmUseridWs) {
	this.nmUseridWs = nmUseridWs;
    }

    @Column(name = "TI_STATO_SES")
    public String getTiStatoSes() {
	return this.tiStatoSes;
    }

    public void setTiStatoSes(String tiStatoSes) {
	this.tiStatoSes = tiStatoSes;
    }

}
