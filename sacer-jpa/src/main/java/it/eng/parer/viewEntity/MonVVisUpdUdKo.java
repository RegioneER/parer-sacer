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
 * The persistent class for the MON_V_VIS_UPD_UD_KO database table.
 *
 */
@Entity
@Table(name = "MON_V_VIS_UPD_UD_KO")

public class MonVVisUpdUdKo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String blXmlRich;
    private String blXmlRisp;
    private String cdControlloWsPrinc;
    private String cdErrPrinc;
    private String cdVersioneWs;
    private String dsEnteStrut;
    private String dsErrPrinc;
    private String dsTsFineSes;
    private String dsTsIniSes;
    private String dsUnitaDoc;
    private BigDecimal idControlloWsPrinc;
    private BigDecimal idEnte;
    private BigDecimal idErrSacerPrinc;
    private BigDecimal idRegistroUnitaDoc;
    private BigDecimal idSesUpdUnitaDocKo;
    private BigDecimal idStrut;
    private BigDecimal idTipoDocPrinc;
    private BigDecimal idTipoUnitaDoc;
    private BigDecimal idUpdUnitaDocKo;
    private BigDecimal idUserIam;
    private BigDecimal idXmlSesUpdKoRich;
    private BigDecimal idXmlSesUpdKoRisp;
    private String nmTipoDocPrinc;
    private String nmTipoUnitaDoc;
    private String nmUserid;
    private String tiStatoSesUpdKo;

    public MonVVisUpdUdKo() {/* Hibernate */
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

    @Column(name = "CD_VERSIONE_WS")
    public String getCdVersioneWs() {
	return this.cdVersioneWs;
    }

    public void setCdVersioneWs(String cdVersioneWs) {
	this.cdVersioneWs = cdVersioneWs;
    }

    @Column(name = "DS_ENTE_STRUT")
    public String getDsEnteStrut() {
	return this.dsEnteStrut;
    }

    public void setDsEnteStrut(String dsEnteStrut) {
	this.dsEnteStrut = dsEnteStrut;
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

    @Column(name = "DS_UNITA_DOC")
    public String getDsUnitaDoc() {
	return this.dsUnitaDoc;
    }

    public void setDsUnitaDoc(String dsUnitaDoc) {
	this.dsUnitaDoc = dsUnitaDoc;
    }

    @Column(name = "ID_CONTROLLO_WS_PRINC")
    public BigDecimal getIdControlloWsPrinc() {
	return this.idControlloWsPrinc;
    }

    public void setIdControlloWsPrinc(BigDecimal idControlloWsPrinc) {
	this.idControlloWsPrinc = idControlloWsPrinc;
    }

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
	return this.idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
	this.idEnte = idEnte;
    }

    @Column(name = "ID_ERR_SACER_PRINC")
    public BigDecimal getIdErrSacerPrinc() {
	return this.idErrSacerPrinc;
    }

    public void setIdErrSacerPrinc(BigDecimal idErrSacerPrinc) {
	this.idErrSacerPrinc = idErrSacerPrinc;
    }

    @Column(name = "ID_REGISTRO_UNITA_DOC")
    public BigDecimal getIdRegistroUnitaDoc() {
	return this.idRegistroUnitaDoc;
    }

    public void setIdRegistroUnitaDoc(BigDecimal idRegistroUnitaDoc) {
	this.idRegistroUnitaDoc = idRegistroUnitaDoc;
    }

    @Id
    @Column(name = "ID_SES_UPD_UNITA_DOC_KO")
    public BigDecimal getIdSesUpdUnitaDocKo() {
	return this.idSesUpdUnitaDocKo;
    }

    public void setIdSesUpdUnitaDocKo(BigDecimal idSesUpdUnitaDocKo) {
	this.idSesUpdUnitaDocKo = idSesUpdUnitaDocKo;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }

    @Column(name = "ID_TIPO_DOC_PRINC")
    public BigDecimal getIdTipoDocPrinc() {
	return this.idTipoDocPrinc;
    }

    public void setIdTipoDocPrinc(BigDecimal idTipoDocPrinc) {
	this.idTipoDocPrinc = idTipoDocPrinc;
    }

    @Column(name = "ID_TIPO_UNITA_DOC")
    public BigDecimal getIdTipoUnitaDoc() {
	return this.idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
	this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    @Column(name = "ID_UPD_UNITA_DOC_KO")
    public BigDecimal getIdUpdUnitaDocKo() {
	return this.idUpdUnitaDocKo;
    }

    public void setIdUpdUnitaDocKo(BigDecimal idUpdUnitaDocKo) {
	this.idUpdUnitaDocKo = idUpdUnitaDocKo;
    }

    @Column(name = "ID_USER_IAM")
    public BigDecimal getIdUserIam() {
	return this.idUserIam;
    }

    public void setIdUserIam(BigDecimal idUserIam) {
	this.idUserIam = idUserIam;
    }

    @Column(name = "ID_XML_SES_UPD_KO_RICH")
    public BigDecimal getIdXmlSesUpdKoRich() {
	return this.idXmlSesUpdKoRich;
    }

    public void setIdXmlSesUpdKoRich(BigDecimal idXmlSesUpdKoRich) {
	this.idXmlSesUpdKoRich = idXmlSesUpdKoRich;
    }

    @Column(name = "ID_XML_SES_UPD_KO_RISP")
    public BigDecimal getIdXmlSesUpdKoRisp() {
	return this.idXmlSesUpdKoRisp;
    }

    public void setIdXmlSesUpdKoRisp(BigDecimal idXmlSesUpdKoRisp) {
	this.idXmlSesUpdKoRisp = idXmlSesUpdKoRisp;
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

    @Column(name = "NM_USERID")
    public String getNmUserid() {
	return this.nmUserid;
    }

    public void setNmUserid(String nmUserid) {
	this.nmUserid = nmUserid;
    }

    @Column(name = "TI_STATO_SES_UPD_KO")
    public String getTiStatoSesUpdKo() {
	return this.tiStatoSesUpdKo;
    }

    public void setTiStatoSesUpdKo(String tiStatoSesUpdKo) {
	this.tiStatoSesUpdKo = tiStatoSesUpdKo;
    }

}
