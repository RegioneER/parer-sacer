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
 * The persistent class for the ARO_V_VIS_FIRMA_COMP database table.
 *
 */
@Entity
@Table(name = "ARO_V_VIS_FIRMA_COMP")

public class AroVVisFirmaComp implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal aaKeyUnitaDoc;
    private String cdFirmatario;
    private String cdKeyDocVers;
    private String cdKeyUnitaDoc;
    private String cdRegistroKeyUnitaDoc;
    private String dsAlgoFirma;
    private String dsFirmaBase64;
    private String dsMsgEsitoContrConforme;
    private String dsMsgEsitoVerifFirma;
    private String dsMsgEsitoVerifFirmaChius;
    private String dsMsgVerifFirmaDtVers;
    private String dsUrnCompCalc;
    private String dsVolumeConserv;
    private Date dtAnnulDoc;
    private Date dtAnnulUnitaDoc;
    private Date dtChiusVolume;
    private Date dtCreazione;
    private Date dtFinValCertifFirmatario;
    private Date dtFirma;
    private Date dtIniValCertifFirmatario;
    private Date dtScadCrl;
    private Date dtScadCrlDtVers;
    private Date dtScadCrlVol;
    private String esitoCatena;
    private String esitoCatenaDtVers;
    private String esitoCatenaVol;
    private String esitoCertificato;
    private String esitoCertificatoDtVers;
    private String esitoCertificatoVol;
    private String esitoCrittogDtVers;
    private String esitoCrittografico;
    private String esitoCrittograficoVol;
    private String esitoCrl;
    private String esitoOcsp;
    private String esitoCrlDtVers;
    private String esitoOcspDtVers;
    private String esitoCrlVol;
    private BigDecimal idCompDoc;
    private BigDecimal idContrFirmaCatena;
    private BigDecimal idDoc;
    private BigDecimal idFirmaComp;
    private BigDecimal idUnitaDoc;
    private String issuerCertifFirmatario;
    private String issuerCrl;
    private String msgEsitoCatena;
    private String msgEsitoCatenaDtVers;
    private String msgEsitoCatenaVol;
    private String msgEsitoCertificato;
    private String msgEsitoCertificatoDtVers;
    private String msgEsitoCertificatoVol;
    private String msgEsitoCrittogDtVers;
    private String msgEsitoCrittografico;
    private String msgEsitoCrittograficoVol;
    private String msgEsitoCrl;
    private String msgEsitoOcsp;
    private String msgEsitoCrlDtVers;
    private String msgEsitoOcspDtVers;
    private String msgEsitoCrlVol;
    private BigDecimal niOrdCompDoc;
    private String dsSerialCertifCa;
    private String dsSerialCertifFirmatario;
    private String nmAmbiente;
    private String nmCognomeFirmatario;
    private String nmCognomeFirmatarioPadre;
    private String nmEnte;
    private String nmFirmatario;
    private String nmFirmatarioPadre;
    private String nmFormatoCalc;
    private String nmStrut;
    private String nmTipoCompDoc;
    private String nmTipoDoc;
    private String nmTipoUnitaDoc;
    private String nmVolumeConserv;
    private String ntAnnulDoc;
    private String ntAnnulUnitaDoc;
    private BigDecimal pgBusta;
    private BigDecimal pgDoc;
    private BigDecimal pgFirma;
    private String serialCaCrl;
    private String serialCrl;
    private String serialCrlDtVers;
    private String serialCrlVol;
    private String tiAnnulDoc;
    private String tiAnnulUnitaDoc;
    private String tiDoc;
    private String tiEsitoContrConforme;
    private String tiEsitoVerifFirma;
    private String tiEsitoVerifFirmaChius;
    private String tiEsitoVerifFirmaDtVers;
    private String tiFirma;
    private String tiFormatoFirma;
    private String tiRifTempUsato;
    private String tiStatoVolumeConserv;
    private Date tmMarcaTempRiferita;
    private Date tmRifTempUsato;
    private String blFirmaBase64;
    private String identifierOcsp;
    private String issuerCertiftOcsp;
    private String serialCertifOcsp;
    private String identifierOcspDtVers;
    private String issuerCertiftOcspDtVers;
    private String serialCertifOcspDtVers;
    private String dsNote;

    public AroVVisFirmaComp() {
        // hibernate
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Column(name = "CD_FIRMATARIO")
    public String getCdFirmatario() {
        return this.cdFirmatario;
    }

    public void setCdFirmatario(String cdFirmatario) {
        this.cdFirmatario = cdFirmatario;
    }

    @Column(name = "CD_KEY_DOC_VERS")
    public String getCdKeyDocVers() {
        return this.cdKeyDocVers;
    }

    public void setCdKeyDocVers(String cdKeyDocVers) {
        this.cdKeyDocVers = cdKeyDocVers;
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

    @Column(name = "DS_ALGO_FIRMA")
    public String getDsAlgoFirma() {
        return this.dsAlgoFirma;
    }

    public void setDsAlgoFirma(String dsAlgoFirma) {
        this.dsAlgoFirma = dsAlgoFirma;
    }

    @Column(name = "DS_FIRMA_BASE64")
    public String getDsFirmaBase64() {
        return this.dsFirmaBase64;
    }

    public void setDsFirmaBase64(String dsFirmaBase64) {
        this.dsFirmaBase64 = dsFirmaBase64;
    }

    @Column(name = "DS_MSG_ESITO_CONTR_CONFORME")
    public String getDsMsgEsitoContrConforme() {
        return this.dsMsgEsitoContrConforme;
    }

    public void setDsMsgEsitoContrConforme(String dsMsgEsitoContrConforme) {
        this.dsMsgEsitoContrConforme = dsMsgEsitoContrConforme;
    }

    @Column(name = "DS_MSG_ESITO_VERIF_FIRMA")
    public String getDsMsgEsitoVerifFirma() {
        return this.dsMsgEsitoVerifFirma;
    }

    public void setDsMsgEsitoVerifFirma(String dsMsgEsitoVerifFirma) {
        this.dsMsgEsitoVerifFirma = dsMsgEsitoVerifFirma;
    }

    @Column(name = "DS_MSG_ESITO_VERIF_FIRMA_CHIUS")
    public String getDsMsgEsitoVerifFirmaChius() {
        return this.dsMsgEsitoVerifFirmaChius;
    }

    public void setDsMsgEsitoVerifFirmaChius(String dsMsgEsitoVerifFirmaChius) {
        this.dsMsgEsitoVerifFirmaChius = dsMsgEsitoVerifFirmaChius;
    }

    @Column(name = "DS_MSG_VERIF_FIRMA_DT_VERS")
    public String getDsMsgVerifFirmaDtVers() {
        return this.dsMsgVerifFirmaDtVers;
    }

    public void setDsMsgVerifFirmaDtVers(String dsMsgVerifFirmaDtVers) {
        this.dsMsgVerifFirmaDtVers = dsMsgVerifFirmaDtVers;
    }

    @Column(name = "DS_URN_COMP_CALC")
    public String getDsUrnCompCalc() {
        return this.dsUrnCompCalc;
    }

    public void setDsUrnCompCalc(String dsUrnCompCalc) {
        this.dsUrnCompCalc = dsUrnCompCalc;
    }

    @Column(name = "DS_VOLUME_CONSERV")
    public String getDsVolumeConserv() {
        return this.dsVolumeConserv;
    }

    public void setDsVolumeConserv(String dsVolumeConserv) {
        this.dsVolumeConserv = dsVolumeConserv;
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
    @Column(name = "DT_CHIUS_VOLUME")
    public Date getDtChiusVolume() {
        return this.dtChiusVolume;
    }

    public void setDtChiusVolume(Date dtChiusVolume) {
        this.dtChiusVolume = dtChiusVolume;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE")
    public Date getDtCreazione() {
        return this.dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
        this.dtCreazione = dtCreazione;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIN_VAL_CERTIF_FIRMATARIO")
    public Date getDtFinValCertifFirmatario() {
        return this.dtFinValCertifFirmatario;
    }

    public void setDtFinValCertifFirmatario(Date dtFinValCertifFirmatario) {
        this.dtFinValCertifFirmatario = dtFinValCertifFirmatario;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIRMA")
    public Date getDtFirma() {
        return this.dtFirma;
    }

    public void setDtFirma(Date dtFirma) {
        this.dtFirma = dtFirma;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL_CERTIF_FIRMATARIO")
    public Date getDtIniValCertifFirmatario() {
        return this.dtIniValCertifFirmatario;
    }

    public void setDtIniValCertifFirmatario(Date dtIniValCertifFirmatario) {
        this.dtIniValCertifFirmatario = dtIniValCertifFirmatario;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SCAD_CRL")
    public Date getDtScadCrl() {
        return this.dtScadCrl;
    }

    public void setDtScadCrl(Date dtScadCrl) {
        this.dtScadCrl = dtScadCrl;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SCAD_CRL_DT_VERS")
    public Date getDtScadCrlDtVers() {
        return dtScadCrlDtVers;
    }

    public void setDtScadCrlDtVers(Date dtScadCrlDtVers) {
        this.dtScadCrlDtVers = dtScadCrlDtVers;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SCAD_CRL_VOL")
    public Date getDtScadCrlVol() {
        return this.dtScadCrlVol;
    }

    public void setDtScadCrlVol(Date dtScadCrlVol) {
        this.dtScadCrlVol = dtScadCrlVol;
    }

    @Column(name = "ESITO_CATENA")
    public String getEsitoCatena() {
        return this.esitoCatena;
    }

    public void setEsitoCatena(String esitoCatena) {
        this.esitoCatena = esitoCatena;
    }

    @Column(name = "ESITO_CATENA_DT_VERS")
    public String getEsitoCatenaDtVers() {
        return this.esitoCatenaDtVers;
    }

    public void setEsitoCatenaDtVers(String esitoCatenaDtVers) {
        this.esitoCatenaDtVers = esitoCatenaDtVers;
    }

    @Column(name = "ESITO_CATENA_VOL")
    public String getEsitoCatenaVol() {
        return this.esitoCatenaVol;
    }

    public void setEsitoCatenaVol(String esitoCatenaVol) {
        this.esitoCatenaVol = esitoCatenaVol;
    }

    @Column(name = "ESITO_CERTIFICATO")
    public String getEsitoCertificato() {
        return this.esitoCertificato;
    }

    public void setEsitoCertificato(String esitoCertificato) {
        this.esitoCertificato = esitoCertificato;
    }

    @Column(name = "ESITO_CERTIFICATO_DT_VERS")
    public String getEsitoCertificatoDtVers() {
        return this.esitoCertificatoDtVers;
    }

    public void setEsitoCertificatoDtVers(String esitoCertificatoDtVers) {
        this.esitoCertificatoDtVers = esitoCertificatoDtVers;
    }

    @Column(name = "ESITO_CERTIFICATO_VOL")
    public String getEsitoCertificatoVol() {
        return this.esitoCertificatoVol;
    }

    public void setEsitoCertificatoVol(String esitoCertificatoVol) {
        this.esitoCertificatoVol = esitoCertificatoVol;
    }

    @Column(name = "ESITO_CRITTOG_DT_VERS")
    public String getEsitoCrittogDtVers() {
        return this.esitoCrittogDtVers;
    }

    public void setEsitoCrittogDtVers(String esitoCrittogDtVers) {
        this.esitoCrittogDtVers = esitoCrittogDtVers;
    }

    @Column(name = "ESITO_CRITTOGRAFICO")
    public String getEsitoCrittografico() {
        return this.esitoCrittografico;
    }

    public void setEsitoCrittografico(String esitoCrittografico) {
        this.esitoCrittografico = esitoCrittografico;
    }

    @Column(name = "ESITO_CRITTOGRAFICO_VOL")
    public String getEsitoCrittograficoVol() {
        return this.esitoCrittograficoVol;
    }

    public void setEsitoCrittograficoVol(String esitoCrittograficoVol) {
        this.esitoCrittograficoVol = esitoCrittograficoVol;
    }

    @Column(name = "ESITO_CRL")
    public String getEsitoCrl() {
        return this.esitoCrl;
    }

    public void setEsitoCrl(String esitoCrl) {
        this.esitoCrl = esitoCrl;
    }

    @Column(name = "ESITO_OCSP")
    public String getEsitoOcsp() {
        return this.esitoOcsp;
    }

    public void setEsitoOcsp(String esitoOcsp) {
        this.esitoOcsp = esitoOcsp;
    }

    @Column(name = "ESITO_CRL_DT_VERS")
    public String getEsitoCrlDtVers() {
        return this.esitoCrlDtVers;
    }

    public void setEsitoCrlDtVers(String esitoCrlDtVers) {
        this.esitoCrlDtVers = esitoCrlDtVers;
    }

    @Column(name = "ESITO_OCSP_DT_VERS")
    public String getEsitoOcspDtVers() {
        return this.esitoOcspDtVers;
    }

    public void setEsitoOcspDtVers(String esitoOcspDtVers) {
        this.esitoOcspDtVers = esitoOcspDtVers;
    }

    @Column(name = "ESITO_CRL_VOL")
    public String getEsitoCrlVol() {
        return this.esitoCrlVol;
    }

    public void setEsitoCrlVol(String esitoCrlVol) {
        this.esitoCrlVol = esitoCrlVol;
    }

    @Column(name = "ID_COMP_DOC")
    public BigDecimal getIdCompDoc() {
        return this.idCompDoc;
    }

    public void setIdCompDoc(BigDecimal idCompDoc) {
        this.idCompDoc = idCompDoc;
    }

    @Column(name = "ID_CONTR_FIRMA_CATENA")
    public BigDecimal getIdContrFirmaCatena() {
        return this.idContrFirmaCatena;
    }

    public void setIdContrFirmaCatena(BigDecimal idContrFirmaCatena) {
        this.idContrFirmaCatena = idContrFirmaCatena;
    }

    @Column(name = "ID_DOC")
    public BigDecimal getIdDoc() {
        return this.idDoc;
    }

    public void setIdDoc(BigDecimal idDoc) {
        this.idDoc = idDoc;
    }

    @Id
    @Column(name = "ID_FIRMA_COMP")
    public BigDecimal getIdFirmaComp() {
        return this.idFirmaComp;
    }

    public void setIdFirmaComp(BigDecimal idFirmaComp) {
        this.idFirmaComp = idFirmaComp;
    }

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "ISSUER_CERTIF_FIRMATARIO")
    public String getIssuerCertifFirmatario() {
        return this.issuerCertifFirmatario;
    }

    public void setIssuerCertifFirmatario(String issuerCertifFirmatario) {
        this.issuerCertifFirmatario = issuerCertifFirmatario;
    }

    @Column(name = "ISSUER_CRL")
    public String getIssuerCrl() {
        return this.issuerCrl;
    }

    public void setIssuerCrl(String issuerCrl) {
        this.issuerCrl = issuerCrl;
    }

    @Column(name = "MSG_ESITO_CATENA")
    public String getMsgEsitoCatena() {
        return this.msgEsitoCatena;
    }

    public void setMsgEsitoCatena(String msgEsitoCatena) {
        this.msgEsitoCatena = msgEsitoCatena;
    }

    @Column(name = "MSG_ESITO_CATENA_DT_VERS")
    public String getMsgEsitoCatenaDtVers() {
        return this.msgEsitoCatenaDtVers;
    }

    public void setMsgEsitoCatenaDtVers(String msgEsitoCatenaDtVers) {
        this.msgEsitoCatenaDtVers = msgEsitoCatenaDtVers;
    }

    @Column(name = "MSG_ESITO_CATENA_VOL")
    public String getMsgEsitoCatenaVol() {
        return this.msgEsitoCatenaVol;
    }

    public void setMsgEsitoCatenaVol(String msgEsitoCatenaVol) {
        this.msgEsitoCatenaVol = msgEsitoCatenaVol;
    }

    @Column(name = "MSG_ESITO_CERTIFICATO")
    public String getMsgEsitoCertificato() {
        return this.msgEsitoCertificato;
    }

    public void setMsgEsitoCertificato(String msgEsitoCertificato) {
        this.msgEsitoCertificato = msgEsitoCertificato;
    }

    @Column(name = "MSG_ESITO_CERTIFICATO_DT_VERS")
    public String getMsgEsitoCertificatoDtVers() {
        return this.msgEsitoCertificatoDtVers;
    }

    public void setMsgEsitoCertificatoDtVers(String msgEsitoCertificatoDtVers) {
        this.msgEsitoCertificatoDtVers = msgEsitoCertificatoDtVers;
    }

    @Column(name = "MSG_ESITO_CERTIFICATO_VOL")
    public String getMsgEsitoCertificatoVol() {
        return this.msgEsitoCertificatoVol;
    }

    public void setMsgEsitoCertificatoVol(String msgEsitoCertificatoVol) {
        this.msgEsitoCertificatoVol = msgEsitoCertificatoVol;
    }

    @Column(name = "MSG_ESITO_CRITTOG_DT_VERS")
    public String getMsgEsitoCrittogDtVers() {
        return this.msgEsitoCrittogDtVers;
    }

    public void setMsgEsitoCrittogDtVers(String msgEsitoCrittogDtVers) {
        this.msgEsitoCrittogDtVers = msgEsitoCrittogDtVers;
    }

    @Column(name = "MSG_ESITO_CRITTOGRAFICO")
    public String getMsgEsitoCrittografico() {
        return this.msgEsitoCrittografico;
    }

    public void setMsgEsitoCrittografico(String msgEsitoCrittografico) {
        this.msgEsitoCrittografico = msgEsitoCrittografico;
    }

    @Column(name = "MSG_ESITO_CRITTOGRAFICO_VOL")
    public String getMsgEsitoCrittograficoVol() {
        return this.msgEsitoCrittograficoVol;
    }

    public void setMsgEsitoCrittograficoVol(String msgEsitoCrittograficoVol) {
        this.msgEsitoCrittograficoVol = msgEsitoCrittograficoVol;
    }

    @Column(name = "MSG_ESITO_CRL")
    public String getMsgEsitoCrl() {
        return this.msgEsitoCrl;
    }

    public void setMsgEsitoCrl(String msgEsitoCrl) {
        this.msgEsitoCrl = msgEsitoCrl;
    }

    @Column(name = "MSG_ESITO_OCSP")
    public String getMsgEsitoOcsp() {
        return this.msgEsitoOcsp;
    }

    public void setMsgEsitoOcsp(String msgEsitoOcsp) {
        this.msgEsitoOcsp = msgEsitoOcsp;
    }

    @Column(name = "MSG_ESITO_CRL_DT_VERS")
    public String getMsgEsitoCrlDtVers() {
        return this.msgEsitoCrlDtVers;
    }

    public void setMsgEsitoCrlDtVers(String msgEsitoCrlDtVers) {
        this.msgEsitoCrlDtVers = msgEsitoCrlDtVers;
    }

    @Column(name = "MSG_ESITO_OCSP_DT_VERS")
    public String getMsgEsitoOcspDtVers() {
        return this.msgEsitoOcspDtVers;
    }

    public void setMsgEsitoOcspDtVers(String msgEsitoOcspDtVers) {
        this.msgEsitoOcspDtVers = msgEsitoOcspDtVers;
    }

    @Column(name = "MSG_ESITO_CRL_VOL")
    public String getMsgEsitoCrlVol() {
        return this.msgEsitoCrlVol;
    }

    public void setMsgEsitoCrlVol(String msgEsitoCrlVol) {
        this.msgEsitoCrlVol = msgEsitoCrlVol;
    }

    @Column(name = "NI_ORD_COMP_DOC")
    public BigDecimal getNiOrdCompDoc() {
        return this.niOrdCompDoc;
    }

    public void setNiOrdCompDoc(BigDecimal niOrdCompDoc) {
        this.niOrdCompDoc = niOrdCompDoc;
    }

    @Column(name = "DS_SERIAL_CERTIF_CA")
    public String getDsSerialCertifCa() {
        return this.dsSerialCertifCa;
    }

    public void setDsSerialCertifCa(String dsSerialCertifCa) {
        this.dsSerialCertifCa = dsSerialCertifCa;
    }

    @Column(name = "DS_SERIAL_CERTIF_FIRMATARIO")
    public String getDsSerialCertifFirmatario() {
        return this.dsSerialCertifFirmatario;
    }

    public void setDsSerialCertifFirmatario(String dsSerialCertifFirmatario) {
        this.dsSerialCertifFirmatario = dsSerialCertifFirmatario;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
        return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_COGNOME_FIRMATARIO")
    public String getNmCognomeFirmatario() {
        return this.nmCognomeFirmatario;
    }

    public void setNmCognomeFirmatario(String nmCognomeFirmatario) {
        this.nmCognomeFirmatario = nmCognomeFirmatario;
    }

    @Column(name = "NM_COGNOME_FIRMATARIO_PADRE")
    public String getNmCognomeFirmatarioPadre() {
        return this.nmCognomeFirmatarioPadre;
    }

    public void setNmCognomeFirmatarioPadre(String nmCognomeFirmatarioPadre) {
        this.nmCognomeFirmatarioPadre = nmCognomeFirmatarioPadre;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
        return this.nmEnte;
    }

    public void setNmEnte(String nmEnte) {
        this.nmEnte = nmEnte;
    }

    @Column(name = "NM_FIRMATARIO")
    public String getNmFirmatario() {
        return this.nmFirmatario;
    }

    public void setNmFirmatario(String nmFirmatario) {
        this.nmFirmatario = nmFirmatario;
    }

    @Column(name = "NM_FIRMATARIO_PADRE")
    public String getNmFirmatarioPadre() {
        return this.nmFirmatarioPadre;
    }

    public void setNmFirmatarioPadre(String nmFirmatarioPadre) {
        this.nmFirmatarioPadre = nmFirmatarioPadre;
    }

    @Column(name = "NM_FORMATO_CALC")
    public String getNmFormatoCalc() {
        return this.nmFormatoCalc;
    }

    public void setNmFormatoCalc(String nmFormatoCalc) {
        this.nmFormatoCalc = nmFormatoCalc;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
        return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
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

    @Column(name = "NM_TIPO_UNITA_DOC")
    public String getNmTipoUnitaDoc() {
        return this.nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
        this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    @Column(name = "NM_VOLUME_CONSERV")
    public String getNmVolumeConserv() {
        return this.nmVolumeConserv;
    }

    public void setNmVolumeConserv(String nmVolumeConserv) {
        this.nmVolumeConserv = nmVolumeConserv;
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

    @Column(name = "PG_BUSTA")
    public BigDecimal getPgBusta() {
        return this.pgBusta;
    }

    public void setPgBusta(BigDecimal pgBusta) {
        this.pgBusta = pgBusta;
    }

    @Column(name = "PG_DOC")
    public BigDecimal getPgDoc() {
        return this.pgDoc;
    }

    public void setPgDoc(BigDecimal pgDoc) {
        this.pgDoc = pgDoc;
    }

    @Column(name = "PG_FIRMA")
    public BigDecimal getPgFirma() {
        return this.pgFirma;
    }

    public void setPgFirma(BigDecimal pgFirma) {
        this.pgFirma = pgFirma;
    }

    @Column(name = "SERIAL_CA_CRL")
    public String getSerialCaCrl() {
        return this.serialCaCrl;
    }

    public void setSerialCaCrl(String serialCaCrl) {
        this.serialCaCrl = serialCaCrl;
    }

    @Column(name = "SERIAL_CRL")
    public String getSerialCrl() {
        return this.serialCrl;
    }

    public void setSerialCrl(String serialCrl) {
        this.serialCrl = serialCrl;
    }

    @Column(name = "SERIAL_CRL_DT_VERS")
    public String getSerialCrlDtVers() {
        return this.serialCrlDtVers;
    }

    public void setSerialCrlDtVers(String serialCrlDtVers) {
        this.serialCrlDtVers = serialCrlDtVers;
    }

    @Column(name = "SERIAL_CRL_VOL")
    public String getSerialCrlVol() {
        return this.serialCrlVol;
    }

    public void setSerialCrlVol(String serialCrlVol) {
        this.serialCrlVol = serialCrlVol;
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

    @Column(name = "TI_ESITO_CONTR_CONFORME")
    public String getTiEsitoContrConforme() {
        return this.tiEsitoContrConforme;
    }

    public void setTiEsitoContrConforme(String tiEsitoContrConforme) {
        this.tiEsitoContrConforme = tiEsitoContrConforme;
    }

    @Column(name = "TI_ESITO_VERIF_FIRMA")
    public String getTiEsitoVerifFirma() {
        return this.tiEsitoVerifFirma;
    }

    public void setTiEsitoVerifFirma(String tiEsitoVerifFirma) {
        this.tiEsitoVerifFirma = tiEsitoVerifFirma;
    }

    @Column(name = "TI_ESITO_VERIF_FIRMA_CHIUS")
    public String getTiEsitoVerifFirmaChius() {
        return this.tiEsitoVerifFirmaChius;
    }

    public void setTiEsitoVerifFirmaChius(String tiEsitoVerifFirmaChius) {
        this.tiEsitoVerifFirmaChius = tiEsitoVerifFirmaChius;
    }

    @Column(name = "TI_ESITO_VERIF_FIRMA_DT_VERS")
    public String getTiEsitoVerifFirmaDtVers() {
        return this.tiEsitoVerifFirmaDtVers;
    }

    public void setTiEsitoVerifFirmaDtVers(String tiEsitoVerifFirmaDtVers) {
        this.tiEsitoVerifFirmaDtVers = tiEsitoVerifFirmaDtVers;
    }

    @Column(name = "TI_FIRMA")
    public String getTiFirma() {
        return this.tiFirma;
    }

    public void setTiFirma(String tiFirma) {
        this.tiFirma = tiFirma;
    }

    @Column(name = "TI_FORMATO_FIRMA")
    public String getTiFormatoFirma() {
        return this.tiFormatoFirma;
    }

    public void setTiFormatoFirma(String tiFormatoFirma) {
        this.tiFormatoFirma = tiFormatoFirma;
    }

    @Column(name = "TI_RIF_TEMP_USATO")
    public String getTiRifTempUsato() {
        return this.tiRifTempUsato;
    }

    public void setTiRifTempUsato(String tiRifTempUsato) {
        this.tiRifTempUsato = tiRifTempUsato;
    }

    @Column(name = "TI_STATO_VOLUME_CONSERV")
    public String getTiStatoVolumeConserv() {
        return this.tiStatoVolumeConserv;
    }

    public void setTiStatoVolumeConserv(String tiStatoVolumeConserv) {
        this.tiStatoVolumeConserv = tiStatoVolumeConserv;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TM_MARCA_TEMP_RIFERITA")
    public Date getTmMarcaTempRiferita() {
        return this.tmMarcaTempRiferita;
    }

    public void setTmMarcaTempRiferita(Date tmMarcaTempRiferita) {
        this.tmMarcaTempRiferita = tmMarcaTempRiferita;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TM_RIF_TEMP_USATO")
    public Date getTmRifTempUsato() {
        return this.tmRifTempUsato;
    }

    public void setTmRifTempUsato(Date tmRifTempUsato) {
        this.tmRifTempUsato = tmRifTempUsato;
    }

    @Column(name = "BL_FIRMA_BASE64", columnDefinition = "clob")
    public String getBlFirmaBase64() {
        return this.blFirmaBase64;
    }

    public void setBlFirmaBase64(String blFirmaBase64) {
        this.blFirmaBase64 = blFirmaBase64;
    }

    @Column(name = "IDENTIFIER_OCSP")
    public String getIdentifierOcsp() {
        return this.identifierOcsp;
    }

    public void setIdentifierOcsp(String identifierOcsp) {
        this.identifierOcsp = identifierOcsp;
    }

    @Column(name = "ISSUER_CERTIF_OCSP")
    public String getIssuerCertifOcsp() {
        return this.issuerCertiftOcsp;
    }

    public void setIssuerCertifOcsp(String issuerCertiftOcsp) {
        this.issuerCertiftOcsp = issuerCertiftOcsp;
    }

    @Column(name = "SERIAL_CERTIF_OCSP")
    public String getSerialCertifOcsp() {
        return this.serialCertifOcsp;
    }

    public void setSerialCertifOcsp(String serialCertifOcsp) {
        this.serialCertifOcsp = serialCertifOcsp;
    }

    @Column(name = "IDENTIFIER_OCSP_DT_VERS")
    public String getIdentifierOcspDtVers() {
        return this.identifierOcspDtVers;
    }

    public void setIdentifierOcspDtVers(String identifierOcspDtVers) {
        this.identifierOcspDtVers = identifierOcspDtVers;
    }

    @Column(name = "ISSUER_CERTIF_OCSP_DT_VERS")
    public String getIssuerCertifOcspDtVers() {
        return this.issuerCertiftOcspDtVers;
    }

    public void setIssuerCertifOcspDtVers(String issuerCertiftOcspDtVers) {
        this.issuerCertiftOcspDtVers = issuerCertiftOcspDtVers;
    }

    @Column(name = "SERIAL_CERTIF_OCSP_DT_VERS")
    public String getSerialCertifOcspDtVers() {
        return this.serialCertifOcspDtVers;
    }

    public void setSerialCertifOcspDtVers(String serialCertifOcspDtVers) {
        this.serialCertifOcspDtVers = serialCertifOcspDtVers;
    }

    @Column(name = "DS_NOTE")
    public String getDsNote() {
        return this.dsNote;
    }

    public void setDsNote(String dsNote) {
        this.dsNote = dsNote;
    }
}
