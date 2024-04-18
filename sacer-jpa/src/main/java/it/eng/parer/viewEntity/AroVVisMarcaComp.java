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
 * The persistent class for the ARO_V_VIS_MARCA_COMP database table.
 * 
 */
@Entity
@Table(name = "ARO_V_VIS_MARCA_COMP")
public class AroVVisMarcaComp implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaKeyUnitaDoc;
    private String cdKeyDocVers;
    private String cdKeyUnitaDoc;
    private String cdRegistroKeyUnitaDoc;
    private String dsAlgoMarca;
    private String dsMarcaBase64;
    private String dsMsgEsitoContrConforme;
    private String dsMsgEsitoVerifMarca;
    private String dsUrnCompCalc;
    private Date dtFinValCertifTsa;
    private Date dtIniValCertifTsa;
    private Date dtScadCrl;
    private Date dtScadMarca;
    private String esitoCatena;
    private String esitoCertificato;
    private String esitoCrittografico;
    private String esitoCrl;
    private String esitoOcsp;
    private BigDecimal idCompDoc;
    private BigDecimal idContrMarcaCatena;
    private BigDecimal idDoc;
    private BigDecimal idMarcaComp;
    private BigDecimal idUnitaDoc;
    private String issuerCertifTsa;
    private String issuerCrl;
    private String msgEsitoCatena;
    private String msgEsitoCertificato;
    private String msgEsitoCrittografico;
    private String msgEsitoCrl;
    private String msgEsitoOcsp;
    private BigDecimal niOrdCompDoc;
    private String nmAmbiente;
    private String nmEnte;
    private String nmFormatoCalc;
    private String nmStrut;
    private String nmTipoCompDoc;
    private String nmTipoDoc;
    private String nmTipoUnitaDoc;
    private BigDecimal pgBusta;
    private BigDecimal pgDoc;
    private BigDecimal pgMarca;
    private BigDecimal serialCaCrl;
    private BigDecimal serialCrl;
    private BigDecimal serialTsa;
    private String tiDoc;
    private String tiEsitoContrConforme;
    private String tiEsitoVerifMarca;
    private String tiFormatoMarca;
    private String tiMarcaTemp;
    private Date tmMarcaTemp;
    private String identifierOcsp;
    private String issuerCertiftOcsp;
    private String serialCertifOcsp;
    private String dsNote;

    public AroVVisMarcaComp() {
        // hibernate
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
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

    @Column(name = "DS_ALGO_MARCA")
    public String getDsAlgoMarca() {
        return this.dsAlgoMarca;
    }

    public void setDsAlgoMarca(String dsAlgoMarca) {
        this.dsAlgoMarca = dsAlgoMarca;
    }

    @Column(name = "DS_MARCA_BASE64")
    public String getDsMarcaBase64() {
        return this.dsMarcaBase64;
    }

    public void setDsMarcaBase64(String dsMarcaBase64) {
        this.dsMarcaBase64 = dsMarcaBase64;
    }

    @Column(name = "DS_MSG_ESITO_CONTR_CONFORME")
    public String getDsMsgEsitoContrConforme() {
        return this.dsMsgEsitoContrConforme;
    }

    public void setDsMsgEsitoContrConforme(String dsMsgEsitoContrConforme) {
        this.dsMsgEsitoContrConforme = dsMsgEsitoContrConforme;
    }

    @Column(name = "DS_MSG_ESITO_VERIF_MARCA")
    public String getDsMsgEsitoVerifMarca() {
        return this.dsMsgEsitoVerifMarca;
    }

    public void setDsMsgEsitoVerifMarca(String dsMsgEsitoVerifMarca) {
        this.dsMsgEsitoVerifMarca = dsMsgEsitoVerifMarca;
    }

    @Column(name = "DS_URN_COMP_CALC")
    public String getDsUrnCompCalc() {
        return this.dsUrnCompCalc;
    }

    public void setDsUrnCompCalc(String dsUrnCompCalc) {
        this.dsUrnCompCalc = dsUrnCompCalc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIN_VAL_CERTIF_TSA")
    public Date getDtFinValCertifTsa() {
        return this.dtFinValCertifTsa;
    }

    public void setDtFinValCertifTsa(Date dtFinValCertifTsa) {
        this.dtFinValCertifTsa = dtFinValCertifTsa;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL_CERTIF_TSA")
    public Date getDtIniValCertifTsa() {
        return this.dtIniValCertifTsa;
    }

    public void setDtIniValCertifTsa(Date dtIniValCertifTsa) {
        this.dtIniValCertifTsa = dtIniValCertifTsa;
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
    @Column(name = "DT_SCAD_MARCA")
    public Date getDtScadMarca() {
        return this.dtScadMarca;
    }

    public void setDtScadMarca(Date dtScadMarca) {
        this.dtScadMarca = dtScadMarca;
    }

    @Column(name = "ESITO_CATENA")
    public String getEsitoCatena() {
        return this.esitoCatena;
    }

    public void setEsitoCatena(String esitoCatena) {
        this.esitoCatena = esitoCatena;
    }

    @Column(name = "ESITO_CERTIFICATO")
    public String getEsitoCertificato() {
        return this.esitoCertificato;
    }

    public void setEsitoCertificato(String esitoCertificato) {
        this.esitoCertificato = esitoCertificato;
    }

    @Column(name = "ESITO_CRITTOGRAFICO")
    public String getEsitoCrittografico() {
        return this.esitoCrittografico;
    }

    public void setEsitoCrittografico(String esitoCrittografico) {
        this.esitoCrittografico = esitoCrittografico;
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

    @Column(name = "ID_COMP_DOC")
    public BigDecimal getIdCompDoc() {
        return this.idCompDoc;
    }

    public void setIdCompDoc(BigDecimal idCompDoc) {
        this.idCompDoc = idCompDoc;
    }

    @Column(name = "ID_CONTR_MARCA_CATENA")
    public BigDecimal getIdContrMarcaCatena() {
        return this.idContrMarcaCatena;
    }

    public void setIdContrMarcaCatena(BigDecimal idContrMarcaCatena) {
        this.idContrMarcaCatena = idContrMarcaCatena;
    }

    @Column(name = "ID_DOC")
    public BigDecimal getIdDoc() {
        return this.idDoc;
    }

    public void setIdDoc(BigDecimal idDoc) {
        this.idDoc = idDoc;
    }

    @Id
    @Column(name = "ID_MARCA_COMP")
    public BigDecimal getIdMarcaComp() {
        return this.idMarcaComp;
    }

    public void setIdMarcaComp(BigDecimal idMarcaComp) {
        this.idMarcaComp = idMarcaComp;
    }

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "ISSUER_CERTIF_TSA")
    public String getIssuerCertifTsa() {
        return this.issuerCertifTsa;
    }

    public void setIssuerCertifTsa(String issuerCertifTsa) {
        this.issuerCertifTsa = issuerCertifTsa;
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

    @Column(name = "MSG_ESITO_CERTIFICATO")
    public String getMsgEsitoCertificato() {
        return this.msgEsitoCertificato;
    }

    public void setMsgEsitoCertificato(String msgEsitoCertificato) {
        this.msgEsitoCertificato = msgEsitoCertificato;
    }

    @Column(name = "MSG_ESITO_CRITTOGRAFICO")
    public String getMsgEsitoCrittografico() {
        return this.msgEsitoCrittografico;
    }

    public void setMsgEsitoCrittografico(String msgEsitoCrittografico) {
        this.msgEsitoCrittografico = msgEsitoCrittografico;
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

    @Column(name = "NI_ORD_COMP_DOC")
    public BigDecimal getNiOrdCompDoc() {
        return this.niOrdCompDoc;
    }

    public void setNiOrdCompDoc(BigDecimal niOrdCompDoc) {
        this.niOrdCompDoc = niOrdCompDoc;
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

    @Column(name = "PG_MARCA")
    public BigDecimal getPgMarca() {
        return this.pgMarca;
    }

    public void setPgMarca(BigDecimal pgMarca) {
        this.pgMarca = pgMarca;
    }

    @Column(name = "SERIAL_CA_CRL")
    public BigDecimal getSerialCaCrl() {
        return this.serialCaCrl;
    }

    public void setSerialCaCrl(BigDecimal serialCaCrl) {
        this.serialCaCrl = serialCaCrl;
    }

    @Column(name = "SERIAL_CRL")
    public BigDecimal getSerialCrl() {
        return this.serialCrl;
    }

    public void setSerialCrl(BigDecimal serialCrl) {
        this.serialCrl = serialCrl;
    }

    @Column(name = "SERIAL_TSA")
    public BigDecimal getSerialTsa() {
        return this.serialTsa;
    }

    public void setSerialTsa(BigDecimal serialTsa) {
        this.serialTsa = serialTsa;
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

    @Column(name = "TI_ESITO_VERIF_MARCA")
    public String getTiEsitoVerifMarca() {
        return this.tiEsitoVerifMarca;
    }

    public void setTiEsitoVerifMarca(String tiEsitoVerifMarca) {
        this.tiEsitoVerifMarca = tiEsitoVerifMarca;
    }

    @Column(name = "TI_FORMATO_MARCA")
    public String getTiFormatoMarca() {
        return this.tiFormatoMarca;
    }

    public void setTiFormatoMarca(String tiFormatoMarca) {
        this.tiFormatoMarca = tiFormatoMarca;
    }

    @Column(name = "TI_MARCA_TEMP")
    public String getTiMarcaTemp() {
        return this.tiMarcaTemp;
    }

    public void setTiMarcaTemp(String tiMarcaTemp) {
        this.tiMarcaTemp = tiMarcaTemp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TM_MARCA_TEMP")
    public Date getTmMarcaTemp() {
        return this.tmMarcaTemp;
    }

    public void setTmMarcaTemp(Date tmMarcaTemp) {
        this.tmMarcaTemp = tmMarcaTemp;
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

    @Column(name = "SERIAL_CERTIF_OCSP", columnDefinition = "NUMBER")
    public String getSerialCertifOcsp() {
        return this.serialCertifOcsp;
    }

    public void setSerialCertifOcsp(String serialCertifOcsp) {
        this.serialCertifOcsp = serialCertifOcsp;
    }

    @Column(name = "DS_NOTE")
    public String getDsNote() {
        return this.dsNote;
    }

    public void setDsNote(String dsNote) {
        this.dsNote = dsNote;
    }
}
