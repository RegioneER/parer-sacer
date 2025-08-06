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
 * The persistent class for the ARO_V_VIS_COMP_AIP database table.
 *
 */
@Entity
@Table(name = "ARO_V_VIS_COMP_AIP")

public class AroVVisCompAip implements Serializable {

    private static final long serialVersionUID = 1L;
    private String cdEncodingHashFileCalc;
    private String dlUrnCompVers;
    private String dsAlgoHashFileCalc;
    private String dsEsitoVerifFirmeDtVers;
    private String dsFormatoRapprCalc;
    private String dsFormatoRapprEstesoCalc;
    private String dsFormatoRapprFileDoc;
    private String dsHashFileCalc;
    private String dsHashFileVers;
    private String dsIdCompVers;
    private String dsMsgEsitoContrFormato;
    private String dsMsgEsitoVerifFirme;
    private String dsNomeCompVers;
    private String dsRifTempVers;
    private String dsUrnCompCalc;
    private String flCompFirmato;
    private String flNoCalcFmtVerifFirme;
    private String flNoCalcHashFile;
    private String flRifTempDataFirmaVers;
    private BigDecimal idCompDoc;
    private BigDecimal niOrdCompDoc;
    private BigDecimal niSizeFileCalc;
    private String nmFormatoCalc;
    private String nmFormatoVers;
    private String nmMimetypeFile;
    private String nmTipoCompDoc;
    private String nmTipoRapprComp;
    private String tiEsitoContrFormatoFile;
    private String tiEsitoVerifFirme;
    private String tiEsitoVerifFirmeDtVers;
    private String tiSupportoComp;
    private Date tmRifTempVers;

    public AroVVisCompAip() {/* Hibernate */
    }

    @Column(name = "CD_ENCODING_HASH_FILE_CALC")
    public String getCdEncodingHashFileCalc() {
	return this.cdEncodingHashFileCalc;
    }

    public void setCdEncodingHashFileCalc(String cdEncodingHashFileCalc) {
	this.cdEncodingHashFileCalc = cdEncodingHashFileCalc;
    }

    @Column(name = "DL_URN_COMP_VERS")
    public String getDlUrnCompVers() {
	return this.dlUrnCompVers;
    }

    public void setDlUrnCompVers(String dlUrnCompVers) {
	this.dlUrnCompVers = dlUrnCompVers;
    }

    @Column(name = "DS_ALGO_HASH_FILE_CALC")
    public String getDsAlgoHashFileCalc() {
	return this.dsAlgoHashFileCalc;
    }

    public void setDsAlgoHashFileCalc(String dsAlgoHashFileCalc) {
	this.dsAlgoHashFileCalc = dsAlgoHashFileCalc;
    }

    @Column(name = "DS_ESITO_VERIF_FIRME_DT_VERS")
    public String getDsEsitoVerifFirmeDtVers() {
	return this.dsEsitoVerifFirmeDtVers;
    }

    public void setDsEsitoVerifFirmeDtVers(String dsEsitoVerifFirmeDtVers) {
	this.dsEsitoVerifFirmeDtVers = dsEsitoVerifFirmeDtVers;
    }

    @Column(name = "DS_FORMATO_RAPPR_CALC")
    public String getDsFormatoRapprCalc() {
	return this.dsFormatoRapprCalc;
    }

    public void setDsFormatoRapprCalc(String dsFormatoRapprCalc) {
	this.dsFormatoRapprCalc = dsFormatoRapprCalc;
    }

    @Column(name = "DS_FORMATO_RAPPR_ESTESO_CALC")
    public String getDsFormatoRapprEstesoCalc() {
	return this.dsFormatoRapprEstesoCalc;
    }

    public void setDsFormatoRapprEstesoCalc(String dsFormatoRapprEstesoCalc) {
	this.dsFormatoRapprEstesoCalc = dsFormatoRapprEstesoCalc;
    }

    @Column(name = "DS_FORMATO_RAPPR_FILE_DOC")
    public String getDsFormatoRapprFileDoc() {
	return this.dsFormatoRapprFileDoc;
    }

    public void setDsFormatoRapprFileDoc(String dsFormatoRapprFileDoc) {
	this.dsFormatoRapprFileDoc = dsFormatoRapprFileDoc;
    }

    @Column(name = "DS_HASH_FILE_CALC")
    public String getDsHashFileCalc() {
	return this.dsHashFileCalc;
    }

    public void setDsHashFileCalc(String dsHashFileCalc) {
	this.dsHashFileCalc = dsHashFileCalc;
    }

    @Column(name = "DS_HASH_FILE_VERS")
    public String getDsHashFileVers() {
	return this.dsHashFileVers;
    }

    public void setDsHashFileVers(String dsHashFileVers) {
	this.dsHashFileVers = dsHashFileVers;
    }

    @Column(name = "DS_ID_COMP_VERS")
    public String getDsIdCompVers() {
	return this.dsIdCompVers;
    }

    public void setDsIdCompVers(String dsIdCompVers) {
	this.dsIdCompVers = dsIdCompVers;
    }

    @Column(name = "DS_MSG_ESITO_CONTR_FORMATO")
    public String getDsMsgEsitoContrFormato() {
	return this.dsMsgEsitoContrFormato;
    }

    public void setDsMsgEsitoContrFormato(String dsMsgEsitoContrFormato) {
	this.dsMsgEsitoContrFormato = dsMsgEsitoContrFormato;
    }

    @Column(name = "DS_MSG_ESITO_VERIF_FIRME")
    public String getDsMsgEsitoVerifFirme() {
	return this.dsMsgEsitoVerifFirme;
    }

    public void setDsMsgEsitoVerifFirme(String dsMsgEsitoVerifFirme) {
	this.dsMsgEsitoVerifFirme = dsMsgEsitoVerifFirme;
    }

    @Column(name = "DS_NOME_COMP_VERS")
    public String getDsNomeCompVers() {
	return this.dsNomeCompVers;
    }

    public void setDsNomeCompVers(String dsNomeCompVers) {
	this.dsNomeCompVers = dsNomeCompVers;
    }

    @Column(name = "DS_RIF_TEMP_VERS")
    public String getDsRifTempVers() {
	return this.dsRifTempVers;
    }

    public void setDsRifTempVers(String dsRifTempVers) {
	this.dsRifTempVers = dsRifTempVers;
    }

    @Column(name = "DS_URN_COMP_CALC")
    public String getDsUrnCompCalc() {
	return this.dsUrnCompCalc;
    }

    public void setDsUrnCompCalc(String dsUrnCompCalc) {
	this.dsUrnCompCalc = dsUrnCompCalc;
    }

    @Column(name = "FL_COMP_FIRMATO", columnDefinition = "char(1)")
    public String getFlCompFirmato() {
	return this.flCompFirmato;
    }

    public void setFlCompFirmato(String flCompFirmato) {
	this.flCompFirmato = flCompFirmato;
    }

    @Column(name = "FL_NO_CALC_FMT_VERIF_FIRME", columnDefinition = "char(1)")
    public String getFlNoCalcFmtVerifFirme() {
	return this.flNoCalcFmtVerifFirme;
    }

    public void setFlNoCalcFmtVerifFirme(String flNoCalcFmtVerifFirme) {
	this.flNoCalcFmtVerifFirme = flNoCalcFmtVerifFirme;
    }

    @Column(name = "FL_NO_CALC_HASH_FILE", columnDefinition = "char(1)")
    public String getFlNoCalcHashFile() {
	return this.flNoCalcHashFile;
    }

    public void setFlNoCalcHashFile(String flNoCalcHashFile) {
	this.flNoCalcHashFile = flNoCalcHashFile;
    }

    @Column(name = "FL_RIF_TEMP_DATA_FIRMA_VERS", columnDefinition = "char(1)")
    public String getFlRifTempDataFirmaVers() {
	return this.flRifTempDataFirmaVers;
    }

    public void setFlRifTempDataFirmaVers(String flRifTempDataFirmaVers) {
	this.flRifTempDataFirmaVers = flRifTempDataFirmaVers;
    }

    @Id
    @Column(name = "ID_COMP_DOC")
    public BigDecimal getIdCompDoc() {
	return this.idCompDoc;
    }

    public void setIdCompDoc(BigDecimal idCompDoc) {
	this.idCompDoc = idCompDoc;
    }

    @Column(name = "NI_ORD_COMP_DOC")
    public BigDecimal getNiOrdCompDoc() {
	return this.niOrdCompDoc;
    }

    public void setNiOrdCompDoc(BigDecimal niOrdCompDoc) {
	this.niOrdCompDoc = niOrdCompDoc;
    }

    @Column(name = "NI_SIZE_FILE_CALC")
    public BigDecimal getNiSizeFileCalc() {
	return this.niSizeFileCalc;
    }

    public void setNiSizeFileCalc(BigDecimal niSizeFileCalc) {
	this.niSizeFileCalc = niSizeFileCalc;
    }

    @Column(name = "NM_FORMATO_CALC")
    public String getNmFormatoCalc() {
	return this.nmFormatoCalc;
    }

    public void setNmFormatoCalc(String nmFormatoCalc) {
	this.nmFormatoCalc = nmFormatoCalc;
    }

    @Column(name = "NM_FORMATO_VERS")
    public String getNmFormatoVers() {
	return this.nmFormatoVers;
    }

    public void setNmFormatoVers(String nmFormatoVers) {
	this.nmFormatoVers = nmFormatoVers;
    }

    @Column(name = "NM_MIMETYPE_FILE")
    public String getNmMimetypeFile() {
	return this.nmMimetypeFile;
    }

    public void setNmMimetypeFile(String nmMimetypeFile) {
	this.nmMimetypeFile = nmMimetypeFile;
    }

    @Column(name = "NM_TIPO_COMP_DOC")
    public String getNmTipoCompDoc() {
	return this.nmTipoCompDoc;
    }

    public void setNmTipoCompDoc(String nmTipoCompDoc) {
	this.nmTipoCompDoc = nmTipoCompDoc;
    }

    @Column(name = "NM_TIPO_RAPPR_COMP")
    public String getNmTipoRapprComp() {
	return this.nmTipoRapprComp;
    }

    public void setNmTipoRapprComp(String nmTipoRapprComp) {
	this.nmTipoRapprComp = nmTipoRapprComp;
    }

    @Column(name = "TI_ESITO_CONTR_FORMATO_FILE")
    public String getTiEsitoContrFormatoFile() {
	return this.tiEsitoContrFormatoFile;
    }

    public void setTiEsitoContrFormatoFile(String tiEsitoContrFormatoFile) {
	this.tiEsitoContrFormatoFile = tiEsitoContrFormatoFile;
    }

    @Column(name = "TI_ESITO_VERIF_FIRME")
    public String getTiEsitoVerifFirme() {
	return this.tiEsitoVerifFirme;
    }

    public void setTiEsitoVerifFirme(String tiEsitoVerifFirme) {
	this.tiEsitoVerifFirme = tiEsitoVerifFirme;
    }

    @Column(name = "TI_ESITO_VERIF_FIRME_DT_VERS")
    public String getTiEsitoVerifFirmeDtVers() {
	return this.tiEsitoVerifFirmeDtVers;
    }

    public void setTiEsitoVerifFirmeDtVers(String tiEsitoVerifFirmeDtVers) {
	this.tiEsitoVerifFirmeDtVers = tiEsitoVerifFirmeDtVers;
    }

    @Column(name = "TI_SUPPORTO_COMP")
    public String getTiSupportoComp() {
	return this.tiSupportoComp;
    }

    public void setTiSupportoComp(String tiSupportoComp) {
	this.tiSupportoComp = tiSupportoComp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TM_RIF_TEMP_VERS")
    public Date getTmRifTempVers() {
	return this.tmRifTempVers;
    }

    public void setTmRifTempVers(Date tmRifTempVers) {
	this.tmRifTempVers = tmRifTempVers;
    }

}
