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

package it.eng.parer.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ELV_V_LISTA_COMP_ELV database table.
 */
@Entity
@Table(name = "ELV_V_LISTA_COMP_ELV")

public class ElvVListaCompElv implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal aaKeyUnitaDoc;

    private String cdEncodingHashFileCalc;

    private String cdKeyUnitaDoc;

    private String cdRegistroKeyUnitaDoc;

    private String dlUrnCompVers;

    private String dsAlgoHashFileCalc;

    private String dsFormatoRapprCalc;

    private String dsFormatoRapprEstesoCalc;

    private String dsHashFileCalc;

    private String dsHashFileVers;

    private String dsNomeCompVers;

    private String dsOrdComp;

    private String dsUrnCompCalc;

    private String dsUrnCompCalcShort;

    private Date dtCreazioneDoc;

    private Date dtScadCertifFirmatario;

    private String flCompFirmato;

    private String flForzaAccettazione;

    private String flForzaConservazione;

    private BigDecimal idDoc;

    private BigDecimal idElencoVers;

    private BigDecimal idFormatoFileDocVers;

    private BigDecimal idRegistroUnitaDoc;

    private BigDecimal idStrutDoc;

    private BigDecimal idStrutFormatoFileVers;

    private BigDecimal idStrutTipoStrut;

    private BigDecimal idStrutUnitaDoc;

    private BigDecimal idSubStrut;

    private BigDecimal idTipoCompDoc;

    private BigDecimal idTipoDoc;

    private BigDecimal idTipoStrutDoc;

    private BigDecimal idTipoUnitaDoc;

    private BigDecimal idUnitaDoc;

    private BigDecimal niSizeFileCalc;

    private String nmFormatoFileDocVers;

    private String nmMimetypeFile;

    private String nmTipoCompDoc;

    private String nmTipoStrutDoc;

    private String tiEsitoContrConforme;

    private String tiEsitoContrFormatoFile;

    private String tiEsitoVerifFirmeVers;

    private String tiStatoConservazione;

    private String tiStatoElencoVers;

    private String tiSupportoComp;

    public ElvVListaCompElv() {/* Hibernate */
    }

    public ElvVListaCompElv(BigDecimal idElencoVers, BigDecimal idCompDoc, String dsUrnCompCalc,
            String dsUrnCompCalcShort, String dsNomeCompVers, String dsOrdComp, String tiSupportoComp,
            String nmTipoStrutDoc, String nmTipoCompDoc, String nmFormatoFileDocVers, Date dtCreazioneDoc,
            BigDecimal niFileSizeCalc, String flCompFirmato, String tiEsitoVerifFirmeVers, BigDecimal idUnitaDoc,
            BigDecimal idDoc, String cdRegistroKeyUnitaDoc, BigDecimal idTipoUnitaDoc, BigDecimal idTipoDoc,
            BigDecimal idSubStrut, String tiStatoElencoVers, String tiStatoConservazione) {
        this.idElencoVers = idElencoVers;
        this.elvVListaCompElvId = new ElvVListaCompElvId();
        this.elvVListaCompElvId.setIdCompDoc(idCompDoc);
        this.dsUrnCompCalc = dsUrnCompCalc;
        this.dsUrnCompCalcShort = dsUrnCompCalcShort;
        this.dsNomeCompVers = dsNomeCompVers;
        this.dsOrdComp = dsOrdComp;
        this.tiSupportoComp = tiSupportoComp;
        this.nmTipoStrutDoc = nmTipoStrutDoc;
        this.nmTipoCompDoc = nmTipoCompDoc;
        this.nmFormatoFileDocVers = nmFormatoFileDocVers;
        this.dtCreazioneDoc = dtCreazioneDoc;
        this.niSizeFileCalc = niFileSizeCalc;
        this.flCompFirmato = flCompFirmato;
        this.tiEsitoVerifFirmeVers = tiEsitoVerifFirmeVers;
        this.idUnitaDoc = idUnitaDoc;
        this.idDoc = idDoc;
        this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
        this.idTipoUnitaDoc = idTipoUnitaDoc;
        this.idTipoDoc = idTipoDoc;
        this.idSubStrut = idSubStrut;
        this.tiStatoElencoVers = tiStatoElencoVers;
        this.tiStatoConservazione = tiStatoConservazione;
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Column(name = "CD_ENCODING_HASH_FILE_CALC")
    public String getCdEncodingHashFileCalc() {
        return this.cdEncodingHashFileCalc;
    }

    public void setCdEncodingHashFileCalc(String cdEncodingHashFileCalc) {
        this.cdEncodingHashFileCalc = cdEncodingHashFileCalc;
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

    @Column(name = "DS_NOME_COMP_VERS")
    public String getDsNomeCompVers() {
        return this.dsNomeCompVers;
    }

    public void setDsNomeCompVers(String dsNomeCompVers) {
        this.dsNomeCompVers = dsNomeCompVers;
    }

    @Column(name = "DS_ORD_COMP")
    public String getDsOrdComp() {
        return this.dsOrdComp;
    }

    public void setDsOrdComp(String dsOrdComp) {
        this.dsOrdComp = dsOrdComp;
    }

    @Column(name = "DS_URN_COMP_CALC")
    public String getDsUrnCompCalc() {
        return this.dsUrnCompCalc;
    }

    public void setDsUrnCompCalc(String dsUrnCompCalc) {
        this.dsUrnCompCalc = dsUrnCompCalc;
    }

    @Column(name = "DS_URN_COMP_CALC_SHORT")
    public String getDsUrnCompCalcShort() {
        return this.dsUrnCompCalcShort;
    }

    public void setDsUrnCompCalcShort(String dsUrnCompCalcShort) {
        this.dsUrnCompCalcShort = dsUrnCompCalcShort;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE_DOC")
    public Date getDtCreazioneDoc() {
        return this.dtCreazioneDoc;
    }

    public void setDtCreazioneDoc(Date dtCreazioneDoc) {
        this.dtCreazioneDoc = dtCreazioneDoc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SCAD_CERTIF_FIRMATARIO")
    public Date getDtScadCertifFirmatario() {
        return this.dtScadCertifFirmatario;
    }

    public void setDtScadCertifFirmatario(Date dtScadCertifFirmatario) {
        this.dtScadCertifFirmatario = dtScadCertifFirmatario;
    }

    @Column(name = "FL_COMP_FIRMATO", columnDefinition = "char(1)")
    public String getFlCompFirmato() {
        return this.flCompFirmato;
    }

    public void setFlCompFirmato(String flCompFirmato) {
        this.flCompFirmato = flCompFirmato;
    }

    @Column(name = "FL_FORZA_ACCETTAZIONE", columnDefinition = "char(1)")
    public String getFlForzaAccettazione() {
        return this.flForzaAccettazione;
    }

    public void setFlForzaAccettazione(String flForzaAccettazione) {
        this.flForzaAccettazione = flForzaAccettazione;
    }

    @Column(name = "FL_FORZA_CONSERVAZIONE", columnDefinition = "char(1)")
    public String getFlForzaConservazione() {
        return this.flForzaConservazione;
    }

    public void setFlForzaConservazione(String flForzaConservazione) {
        this.flForzaConservazione = flForzaConservazione;
    }

    @Column(name = "ID_DOC")
    public BigDecimal getIdDoc() {
        return this.idDoc;
    }

    public void setIdDoc(BigDecimal idDoc) {
        this.idDoc = idDoc;
    }

    @Column(name = "ID_ELENCO_VERS")
    public BigDecimal getIdElencoVers() {
        return this.idElencoVers;
    }

    public void setIdElencoVers(BigDecimal idElencoVers) {
        this.idElencoVers = idElencoVers;
    }

    @Column(name = "ID_FORMATO_FILE_DOC_VERS")
    public BigDecimal getIdFormatoFileDocVers() {
        return this.idFormatoFileDocVers;
    }

    public void setIdFormatoFileDocVers(BigDecimal idFormatoFileDocVers) {
        this.idFormatoFileDocVers = idFormatoFileDocVers;
    }

    @Column(name = "ID_REGISTRO_UNITA_DOC")
    public BigDecimal getIdRegistroUnitaDoc() {
        return this.idRegistroUnitaDoc;
    }

    public void setIdRegistroUnitaDoc(BigDecimal idRegistroUnitaDoc) {
        this.idRegistroUnitaDoc = idRegistroUnitaDoc;
    }

    @Column(name = "ID_STRUT_DOC")
    public BigDecimal getIdStrutDoc() {
        return this.idStrutDoc;
    }

    public void setIdStrutDoc(BigDecimal idStrutDoc) {
        this.idStrutDoc = idStrutDoc;
    }

    @Column(name = "ID_STRUT_FORMATO_FILE_VERS")
    public BigDecimal getIdStrutFormatoFileVers() {
        return this.idStrutFormatoFileVers;
    }

    public void setIdStrutFormatoFileVers(BigDecimal idStrutFormatoFileVers) {
        this.idStrutFormatoFileVers = idStrutFormatoFileVers;
    }

    @Column(name = "ID_STRUT_TIPO_STRUT")
    public BigDecimal getIdStrutTipoStrut() {
        return this.idStrutTipoStrut;
    }

    public void setIdStrutTipoStrut(BigDecimal idStrutTipoStrut) {
        this.idStrutTipoStrut = idStrutTipoStrut;
    }

    @Column(name = "ID_STRUT_UNITA_DOC")
    public BigDecimal getIdStrutUnitaDoc() {
        return this.idStrutUnitaDoc;
    }

    public void setIdStrutUnitaDoc(BigDecimal idStrutUnitaDoc) {
        this.idStrutUnitaDoc = idStrutUnitaDoc;
    }

    @Column(name = "ID_SUB_STRUT")
    public BigDecimal getIdSubStrut() {
        return this.idSubStrut;
    }

    public void setIdSubStrut(BigDecimal idSubStrut) {
        this.idSubStrut = idSubStrut;
    }

    @Column(name = "ID_TIPO_COMP_DOC")
    public BigDecimal getIdTipoCompDoc() {
        return this.idTipoCompDoc;
    }

    public void setIdTipoCompDoc(BigDecimal idTipoCompDoc) {
        this.idTipoCompDoc = idTipoCompDoc;
    }

    @Column(name = "ID_TIPO_DOC")
    public BigDecimal getIdTipoDoc() {
        return this.idTipoDoc;
    }

    public void setIdTipoDoc(BigDecimal idTipoDoc) {
        this.idTipoDoc = idTipoDoc;
    }

    @Column(name = "ID_TIPO_STRUT_DOC")
    public BigDecimal getIdTipoStrutDoc() {
        return this.idTipoStrutDoc;
    }

    public void setIdTipoStrutDoc(BigDecimal idTipoStrutDoc) {
        this.idTipoStrutDoc = idTipoStrutDoc;
    }

    @Column(name = "ID_TIPO_UNITA_DOC")
    public BigDecimal getIdTipoUnitaDoc() {
        return this.idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "NI_SIZE_FILE_CALC")
    public BigDecimal getNiSizeFileCalc() {
        return this.niSizeFileCalc;
    }

    public void setNiSizeFileCalc(BigDecimal niSizeFileCalc) {
        this.niSizeFileCalc = niSizeFileCalc;
    }

    @Column(name = "NM_FORMATO_FILE_DOC_VERS")
    public String getNmFormatoFileDocVers() {
        return this.nmFormatoFileDocVers;
    }

    public void setNmFormatoFileDocVers(String nmFormatoFileDocVers) {
        this.nmFormatoFileDocVers = nmFormatoFileDocVers;
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

    @Column(name = "NM_TIPO_STRUT_DOC")
    public String getNmTipoStrutDoc() {
        return this.nmTipoStrutDoc;
    }

    public void setNmTipoStrutDoc(String nmTipoStrutDoc) {
        this.nmTipoStrutDoc = nmTipoStrutDoc;
    }

    @Column(name = "TI_ESITO_CONTR_CONFORME")
    public String getTiEsitoContrConforme() {
        return this.tiEsitoContrConforme;
    }

    public void setTiEsitoContrConforme(String tiEsitoContrConforme) {
        this.tiEsitoContrConforme = tiEsitoContrConforme;
    }

    @Column(name = "TI_ESITO_CONTR_FORMATO_FILE")
    public String getTiEsitoContrFormatoFile() {
        return this.tiEsitoContrFormatoFile;
    }

    public void setTiEsitoContrFormatoFile(String tiEsitoContrFormatoFile) {
        this.tiEsitoContrFormatoFile = tiEsitoContrFormatoFile;
    }

    @Column(name = "TI_ESITO_VERIF_FIRME_VERS")
    public String getTiEsitoVerifFirmeVers() {
        return this.tiEsitoVerifFirmeVers;
    }

    public void setTiEsitoVerifFirmeVers(String tiEsitoVerifFirmeVers) {
        this.tiEsitoVerifFirmeVers = tiEsitoVerifFirmeVers;
    }

    @Column(name = "TI_STATO_CONSERVAZIONE")
    public String getTiStatoConservazione() {
        return this.tiStatoConservazione;
    }

    public void setTiStatoConservazione(String tiStatoConservazione) {
        this.tiStatoConservazione = tiStatoConservazione;
    }

    @Column(name = "TI_STATO_ELENCO_VERS")
    public String getTiStatoElencoVers() {
        return this.tiStatoElencoVers;
    }

    public void setTiStatoElencoVers(String tiStatoElencoVers) {
        this.tiStatoElencoVers = tiStatoElencoVers;
    }

    @Column(name = "TI_SUPPORTO_COMP")
    public String getTiSupportoComp() {
        return this.tiSupportoComp;
    }

    public void setTiSupportoComp(String tiSupportoComp) {
        this.tiSupportoComp = tiSupportoComp;
    }

    private ElvVListaCompElvId elvVListaCompElvId;

    @EmbeddedId()
    public ElvVListaCompElvId getElvVListaCompElvId() {
        return elvVListaCompElvId;
    }

    public void setElvVListaCompElvId(ElvVListaCompElvId elvVListaCompElvId) {
        this.elvVListaCompElvId = elvVListaCompElvId;
    }
}
