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
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ARO_V_VIS_COMP database table.
 *
 */
@Entity
@Table(name = "ARO_V_VIS_COMP")

public class AroVVisComp implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal aaKeyUnitaDoc;
    private BigDecimal aaKeyUnitaDocRif;
    private String cdEncodingHashFile256;
    private String cdEncodingHashFileCalc;
    private String cdKeyDocVers;
    private String cdKeyFile;
    private String cdKeyUnitaDoc;
    private String cdKeyUnitaDocNormaliz;
    private String cdKeyUnitaDocRif;
    private String cdRegistroKeyUnitaDoc;
    private String cdRegistroKeyUnitaDocRif;
    private String cdSubPartition;
    private String dlDoc;
    private String dlOggettoUnitaDoc;
    private String dlUrnCompVers;
    private String dsAlgoHashFile256;
    private String dsAlgoHashFileCalc;
    private String dsAutoreDoc;
    private String dsEsitoVerifFirmeDtVers;
    private String dsFormatoRapprCalc;
    private String dsFormatoRapprEstesoCalc;
    private String dsFormatoRapprFileDoc;
    private String dsHashFile256;
    private String dsHashFileCalc;
    private String dsHashFileVers;
    private String dsIdCompVers;
    private String dsMsgEsitoContrFormato;
    private String dsMsgEsitoVerifFirme;
    private String dsNomeCompVers;
    private String dsNomeFileArk;
    private String dsRifTempVers;
    private String dsUrnCompCalc;
    private String dsUrnNormalizzato;
    private String dsUrnOriginale;
    private String dsServizioVerificaCompDoc;
    private Date dtAnnulDoc;
    private Date dtAnnulUnitaDoc;
    private Date dtChiusElencoVers;
    private Date dtCreazioneDoc;
    private Date dtCreazioneUd;
    private Date dtRegUnitaDoc;
    private String flCompFirmato;
    private String flNoCalcFmtVerifFirme;
    private String flNoCalcHashFile;
    private String flRifTempDataFirmaVers;
    private BigDecimal idCompDoc;
    private BigDecimal idDoc;
    private BigDecimal idElencoVers;
    private BigDecimal idFileOracle;
    private BigDecimal idStrutUnitaDoc;
    private BigDecimal idTipoUnitaDoc;
    private BigDecimal idUnitaDoc;
    private BigDecimal niOrdCompDoc;
    private BigDecimal niOrdCompPadre;
    private BigDecimal niOrdDoc;
    private BigDecimal niSizeFileCalc;
    private String nmAmbiente;
    private String nmBucket;
    private String nmElencoVers;
    private String nmEnte;
    private String nmFormatoCalc;
    private String nmFormatoVers;
    private String nmMimetypeFile;
    private String nmStrut;
    private String nmTenant;
    private String nmTipoCompDoc;
    private String nmTipoDoc;
    private String nmTipoRapprComp;
    private String nmTipoStrutDoc;
    private String nmTipoUnitaDoc;
    private String ntAnnulDoc;
    private String ntAnnulUnitaDoc;
    private BigDecimal pgDoc;
    private String tiAnnulDoc;
    private String tiAnnulUnitaDoc;
    private String tiDoc;
    private String tiEsitoContrFormatoFile;
    private String tiEsitoVerifFirme;
    private String tiEsitoVerifFirmeDtVers;
    private String tiStatoDoc;
    private String tiStatoDtVers;
    private String tiStatoElencoVers;
    private String tiSupportoComp;
    private String tipoArchiviazione;
    private Date tmRifTempVers;

    public AroVVisComp() {/* Hibernate */
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Column(name = "AA_KEY_UNITA_DOC_RIF")
    public BigDecimal getAaKeyUnitaDocRif() {
        return this.aaKeyUnitaDocRif;
    }

    public void setAaKeyUnitaDocRif(BigDecimal aaKeyUnitaDocRif) {
        this.aaKeyUnitaDocRif = aaKeyUnitaDocRif;
    }

    @Column(name = "CD_ENCODING_HASH_FILE_256")
    public String getCdEncodingHashFile256() {
        return this.cdEncodingHashFile256;
    }

    public void setCdEncodingHashFile256(String cdEncodingHashFile256) {
        this.cdEncodingHashFile256 = cdEncodingHashFile256;
    }

    @Column(name = "CD_ENCODING_HASH_FILE_CALC")
    public String getCdEncodingHashFileCalc() {
        return this.cdEncodingHashFileCalc;
    }

    public void setCdEncodingHashFileCalc(String cdEncodingHashFileCalc) {
        this.cdEncodingHashFileCalc = cdEncodingHashFileCalc;
    }

    @Column(name = "CD_KEY_DOC_VERS")
    public String getCdKeyDocVers() {
        return this.cdKeyDocVers;
    }

    public void setCdKeyDocVers(String cdKeyDocVers) {
        this.cdKeyDocVers = cdKeyDocVers;
    }

    @Column(name = "CD_KEY_FILE")
    public String getCdKeyFile() {
        return this.cdKeyFile;
    }

    public void setCdKeyFile(String cdKeyFile) {
        this.cdKeyFile = cdKeyFile;
    }

    @Column(name = "CD_KEY_UNITA_DOC")
    public String getCdKeyUnitaDoc() {
        return this.cdKeyUnitaDoc;
    }

    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
        this.cdKeyUnitaDoc = cdKeyUnitaDoc;
    }

    @Column(name = "CD_KEY_UNITA_DOC_NORMALIZ")
    public String getCdKeyUnitaDocNormaliz() {
        return this.cdKeyUnitaDocNormaliz;
    }

    public void setCdKeyUnitaDocNormaliz(String cdKeyUnitaDocNormaliz) {
        this.cdKeyUnitaDocNormaliz = cdKeyUnitaDocNormaliz;
    }

    @Column(name = "CD_KEY_UNITA_DOC_RIF")
    public String getCdKeyUnitaDocRif() {
        return this.cdKeyUnitaDocRif;
    }

    public void setCdKeyUnitaDocRif(String cdKeyUnitaDocRif) {
        this.cdKeyUnitaDocRif = cdKeyUnitaDocRif;
    }

    @Column(name = "CD_REGISTRO_KEY_UNITA_DOC")
    public String getCdRegistroKeyUnitaDoc() {
        return this.cdRegistroKeyUnitaDoc;
    }

    public void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
        this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
    }

    @Column(name = "CD_REGISTRO_KEY_UNITA_DOC_RIF")
    public String getCdRegistroKeyUnitaDocRif() {
        return this.cdRegistroKeyUnitaDocRif;
    }

    public void setCdRegistroKeyUnitaDocRif(String cdRegistroKeyUnitaDocRif) {
        this.cdRegistroKeyUnitaDocRif = cdRegistroKeyUnitaDocRif;
    }

    @Column(name = "CD_SUB_PARTITION")
    public String getCdSubPartition() {
        return this.cdSubPartition;
    }

    public void setCdSubPartition(String cdSubPartition) {
        this.cdSubPartition = cdSubPartition;
    }

    @Column(name = "DL_DOC")
    public String getDlDoc() {
        return this.dlDoc;
    }

    public void setDlDoc(String dlDoc) {
        this.dlDoc = dlDoc;
    }

    @Column(name = "DL_OGGETTO_UNITA_DOC")
    public String getDlOggettoUnitaDoc() {
        return this.dlOggettoUnitaDoc;
    }

    public void setDlOggettoUnitaDoc(String dlOggettoUnitaDoc) {
        this.dlOggettoUnitaDoc = dlOggettoUnitaDoc;
    }

    @Column(name = "DL_URN_COMP_VERS")
    public String getDlUrnCompVers() {
        return this.dlUrnCompVers;
    }

    public void setDlUrnCompVers(String dlUrnCompVers) {
        this.dlUrnCompVers = dlUrnCompVers;
    }

    @Column(name = "DS_ALGO_HASH_FILE_256")
    public String getDsAlgoHashFile256() {
        return this.dsAlgoHashFile256;
    }

    public void setDsAlgoHashFile256(String dsAlgoHashFile256) {
        this.dsAlgoHashFile256 = dsAlgoHashFile256;
    }

    @Column(name = "DS_ALGO_HASH_FILE_CALC")
    public String getDsAlgoHashFileCalc() {
        return this.dsAlgoHashFileCalc;
    }

    public void setDsAlgoHashFileCalc(String dsAlgoHashFileCalc) {
        this.dsAlgoHashFileCalc = dsAlgoHashFileCalc;
    }

    @Column(name = "DS_AUTORE_DOC")
    public String getDsAutoreDoc() {
        return this.dsAutoreDoc;
    }

    public void setDsAutoreDoc(String dsAutoreDoc) {
        this.dsAutoreDoc = dsAutoreDoc;
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

    @Column(name = "DS_HASH_FILE_256")
    public String getDsHashFile256() {
        return this.dsHashFile256;
    }

    public void setDsHashFile256(String dsHashFile256) {
        this.dsHashFile256 = dsHashFile256;
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

    @Column(name = "DS_NOME_FILE_ARK")
    public String getDsNomeFileArk() {
        return this.dsNomeFileArk;
    }

    public void setDsNomeFileArk(String dsNomeFileArk) {
        this.dsNomeFileArk = dsNomeFileArk;
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

    @Column(name = "DS_URN_NORMALIZZATO")
    public String getDsUrnNormalizzato() {
        return this.dsUrnNormalizzato;
    }

    public void setDsUrnNormalizzato(String dsUrnNormalizzato) {
        this.dsUrnNormalizzato = dsUrnNormalizzato;
    }

    @Column(name = "DS_URN_ORIGINALE")
    public String getDsUrnOriginale() {
        return this.dsUrnOriginale;
    }

    public void setDsUrnOriginale(String dsUrnOriginale) {
        this.dsUrnOriginale = dsUrnOriginale;
    }

    @Column(name = "DS_SERVIZIO_VERIFICA_COMP_DOC")
    public String getDsServizioVerificaCompDoc() {
        return this.dsServizioVerificaCompDoc;
    }

    public void setDsServizioVerificaCompDoc(String dsServizioVerificaCompDoc) {
        this.dsServizioVerificaCompDoc = dsServizioVerificaCompDoc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ANNUL_DOC")
    public Date getDtAnnulDoc() {
        return this.dtAnnulDoc;
    }

    public void setDtAnnulDoc(Date dtAnnulDoc) {
        this.dtAnnulDoc = dtAnnulDoc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ANNUL_UNITA_DOC")
    public Date getDtAnnulUnitaDoc() {
        return this.dtAnnulUnitaDoc;
    }

    public void setDtAnnulUnitaDoc(Date dtAnnulUnitaDoc) {
        this.dtAnnulUnitaDoc = dtAnnulUnitaDoc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CHIUS_ELENCO_VERS")
    public Date getDtChiusElencoVers() {
        return this.dtChiusElencoVers;
    }

    public void setDtChiusElencoVers(Date dtChiusElencoVers) {
        this.dtChiusElencoVers = dtChiusElencoVers;
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
    @Column(name = "DT_CREAZIONE_UD")
    public Date getDtCreazioneUd() {
        return this.dtCreazioneUd;
    }

    public void setDtCreazioneUd(Date dtCreazioneUd) {
        this.dtCreazioneUd = dtCreazioneUd;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_UNITA_DOC")
    public Date getDtRegUnitaDoc() {
        return this.dtRegUnitaDoc;
    }

    public void setDtRegUnitaDoc(Date dtRegUnitaDoc) {
        this.dtRegUnitaDoc = dtRegUnitaDoc;
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

    @Column(name = "ID_FILE_ORACLE")
    public BigDecimal getIdFileOracle() {
        return this.idFileOracle;
    }

    public void setIdFileOracle(BigDecimal idFileOracle) {
        this.idFileOracle = idFileOracle;
    }

    @Column(name = "ID_STRUT_UNITA_DOC")
    public BigDecimal getIdStrutUnitaDoc() {
        return this.idStrutUnitaDoc;
    }

    public void setIdStrutUnitaDoc(BigDecimal idStrutUnitaDoc) {
        this.idStrutUnitaDoc = idStrutUnitaDoc;
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

    @Column(name = "NI_ORD_COMP_DOC")
    public BigDecimal getNiOrdCompDoc() {
        return this.niOrdCompDoc;
    }

    public void setNiOrdCompDoc(BigDecimal niOrdCompDoc) {
        this.niOrdCompDoc = niOrdCompDoc;
    }

    @Column(name = "NI_ORD_COMP_PADRE")
    public BigDecimal getNiOrdCompPadre() {
        return this.niOrdCompPadre;
    }

    public void setNiOrdCompPadre(BigDecimal niOrdCompPadre) {
        this.niOrdCompPadre = niOrdCompPadre;
    }

    @Column(name = "NI_ORD_DOC")
    public BigDecimal getNiOrdDoc() {
        return this.niOrdDoc;
    }

    public void setNiOrdDoc(BigDecimal niOrdDoc) {
        this.niOrdDoc = niOrdDoc;
    }

    @Column(name = "NI_SIZE_FILE_CALC")
    public BigDecimal getNiSizeFileCalc() {
        return this.niSizeFileCalc;
    }

    public void setNiSizeFileCalc(BigDecimal niSizeFileCalc) {
        this.niSizeFileCalc = niSizeFileCalc;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
        return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_BUCKET")
    public String getNmBucket() {
        return this.nmBucket;
    }

    public void setNmBucket(String nmBucket) {
        this.nmBucket = nmBucket;
    }

    @Column(name = "NM_ELENCO_VERS")
    public String getNmElencoVers() {
        return this.nmElencoVers;
    }

    public void setNmElencoVers(String nmElencoVers) {
        this.nmElencoVers = nmElencoVers;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
        return this.nmEnte;
    }

    public void setNmEnte(String nmEnte) {
        this.nmEnte = nmEnte;
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

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
        return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
    }

    @Column(name = "NM_TENANT")
    public String getNmTenant() {
        return this.nmTenant;
    }

    public void setNmTenant(String nmTenant) {
        this.nmTenant = nmTenant;
    }

    @Column(name = "NM_TIPO_COMP_DOC")
    public String getNmTipoCompDoc() {
        return this.nmTipoCompDoc;
    }

    public void setNmTipoCompDoc(String nmTipoCompDoc) {
        this.nmTipoCompDoc = nmTipoCompDoc;
    }

    @Column(name = "NM_TIPO_DOC")
    public String getNmTipoDoc() {
        return this.nmTipoDoc;
    }

    public void setNmTipoDoc(String nmTipoDoc) {
        this.nmTipoDoc = nmTipoDoc;
    }

    @Column(name = "NM_TIPO_RAPPR_COMP")
    public String getNmTipoRapprComp() {
        return this.nmTipoRapprComp;
    }

    public void setNmTipoRapprComp(String nmTipoRapprComp) {
        this.nmTipoRapprComp = nmTipoRapprComp;
    }

    @Column(name = "NM_TIPO_STRUT_DOC")
    public String getNmTipoStrutDoc() {
        return this.nmTipoStrutDoc;
    }

    public void setNmTipoStrutDoc(String nmTipoStrutDoc) {
        this.nmTipoStrutDoc = nmTipoStrutDoc;
    }

    @Column(name = "NM_TIPO_UNITA_DOC")
    public String getNmTipoUnitaDoc() {
        return this.nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
        this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    @Column(name = "NT_ANNUL_DOC")
    public String getNtAnnulDoc() {
        return this.ntAnnulDoc;
    }

    public void setNtAnnulDoc(String ntAnnulDoc) {
        this.ntAnnulDoc = ntAnnulDoc;
    }

    @Column(name = "NT_ANNUL_UNITA_DOC")
    public String getNtAnnulUnitaDoc() {
        return this.ntAnnulUnitaDoc;
    }

    public void setNtAnnulUnitaDoc(String ntAnnulUnitaDoc) {
        this.ntAnnulUnitaDoc = ntAnnulUnitaDoc;
    }

    @Column(name = "PG_DOC")
    public BigDecimal getPgDoc() {
        return this.pgDoc;
    }

    public void setPgDoc(BigDecimal pgDoc) {
        this.pgDoc = pgDoc;
    }

    @Column(name = "TI_ANNUL_DOC")
    public String getTiAnnulDoc() {
        return this.tiAnnulDoc;
    }

    public void setTiAnnulDoc(String tiAnnulDoc) {
        this.tiAnnulDoc = tiAnnulDoc;
    }

    @Column(name = "TI_ANNUL_UNITA_DOC")
    public String getTiAnnulUnitaDoc() {
        return this.tiAnnulUnitaDoc;
    }

    public void setTiAnnulUnitaDoc(String tiAnnulUnitaDoc) {
        this.tiAnnulUnitaDoc = tiAnnulUnitaDoc;
    }

    @Column(name = "TI_DOC")
    public String getTiDoc() {
        return this.tiDoc;
    }

    public void setTiDoc(String tiDoc) {
        this.tiDoc = tiDoc;
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

    @Column(name = "TI_STATO_DOC")
    public String getTiStatoDoc() {
        return this.tiStatoDoc;
    }

    public void setTiStatoDoc(String tiStatoDoc) {
        this.tiStatoDoc = tiStatoDoc;
    }

    @Column(name = "TI_STATO_DT_VERS")
    public String getTiStatoDtVers() {
        return this.tiStatoDtVers;
    }

    public void setTiStatoDtVers(String tiStatoDtVers) {
        this.tiStatoDtVers = tiStatoDtVers;
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

    @Column(name = "TIPO_ARCHIVIAZIONE")
    public String getTipoArchiviazione() {
        return this.tipoArchiviazione;
    }

    public void setTipoArchiviazione(String tipoArchiviazione) {
        this.tipoArchiviazione = tipoArchiviazione;
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
