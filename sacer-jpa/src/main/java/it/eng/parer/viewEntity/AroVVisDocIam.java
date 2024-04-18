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
import javax.persistence.Lob;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ARO_V_VIS_DOC_IAM database table.
 *
 */
@Entity
@Table(name = "ARO_V_VIS_DOC_IAM")

public class AroVVisDocIam implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal aaKeyUnitaDoc;
    private String blXmlRappDoc;
    private String blXmlRichDoc;
    private String blXmlRispDoc;
    private String cdEncodingHashXmlRappDoc;
    private String cdEncodingHashXmlRichDoc;
    private String cdEncodingHashXmlRispDoc;
    private String cdKeyDocVers;
    private String cdKeyUnitaDoc;
    private String cdKeyUnitaDocNormaliz;
    private String cdRegistroKeyUnitaDoc;
    private String cdVersioneXml;
    private String dlDoc;
    private String dlOggettoUnitaDoc;
    private String dsAlgoHashXmlRappDoc;
    private String dsAlgoHashXmlRichDoc;
    private String dsAlgoHashXmlRispDoc;
    private String dsAutoreDoc;
    private String dsHashXmlRappDoc;
    private String dsHashXmlRichDoc;
    private String dsHashXmlRispDoc;
    private String dsMsgEsitoVerifFirme;
    private String dsUrnXmlRappDoc;
    private String dsUrnXmlRappDocNormaliz;
    private String dsUrnXmlRappDocIniziale;
    private String dsUrnXmlRichDoc;
    private String dsUrnXmlRichDocNormaliz;
    private String dsUrnXmlRichDocIniziale;
    private String dsUrnXmlRispDoc;
    private String dsUrnXmlRispDocNormaliz;
    private String dsUrnXmlRispDocIniziale;
    private Date dtAnnulDoc;
    private Date dtAnnulUnitaDoc;
    private Date dtCreazione;
    private Date dtCreazioneUd;
    private Date dtRegUnitaDoc;
    private String flDocFirmato;
    private String flForzaAccettazione;
    private String flForzaConservazione;
    private BigDecimal idDoc;
    private BigDecimal idElencoVers;
    private BigDecimal idStrutDoc;
    private BigDecimal idTipoUnitaDoc;
    private BigDecimal idUnitaDoc;
    private BigDecimal idVolumeConserv;
    private BigDecimal niOrdDoc;
    private String nmAmbiente;
    private String nmElencoVers;
    private String nmEnte;
    private String nmSistemaMigraz;
    private String nmStrut;
    private String nmTipoDoc;
    private String nmTipoStrutDoc;
    private String nmTipoUnitaDoc;
    private String nmVolumeConserv;
    private String ntAnnulDoc;
    private String ntAnnulUnitaDoc;
    private String ntDoc;
    private BigDecimal pgDoc;
    private String tiAnnulDoc;
    private String tiAnnulUnitaDoc;
    private String tiConservazione;
    private String tiDoc;
    private String tiEsitoVerifFirme;
    private String tiStatoConservazione;
    private String tiStatoDoc;
    private String tiStatoElencoVers;
    private String tiStatoVolumeConserv;
    private String utenteVers;
    private String utente;
    private BigDecimal idSessioneVers;

    public AroVVisDocIam() {/* Hibernate */
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Lob
    @Column(name = "BL_XML_RAPP_DOC")
    public String getBlXmlRappDoc() {
        return this.blXmlRappDoc;
    }

    public void setBlXmlRappDoc(String blXmlRappDoc) {
        this.blXmlRappDoc = blXmlRappDoc;
    }

    @Lob
    @Column(name = "BL_XML_RICH_DOC")
    public String getBlXmlRichDoc() {
        return this.blXmlRichDoc;
    }

    public void setBlXmlRichDoc(String blXmlRichDoc) {
        this.blXmlRichDoc = blXmlRichDoc;
    }

    @Lob()
    @Column(name = "BL_XML_RISP_DOC")
    public String getBlXmlRispDoc() {
        return this.blXmlRispDoc;
    }

    public void setBlXmlRispDoc(String blXmlRispDoc) {
        this.blXmlRispDoc = blXmlRispDoc;
    }

    @Column(name = "CD_ENCODING_HASH_XML_RAPP_DOC")
    public String getCdEncodingHashXmlRappDoc() {
        return this.cdEncodingHashXmlRappDoc;
    }

    public void setCdEncodingHashXmlRappDoc(String cdEncodingHashXmlRappDoc) {
        this.cdEncodingHashXmlRappDoc = cdEncodingHashXmlRappDoc;
    }

    @Column(name = "CD_ENCODING_HASH_XML_RICH_DOC")
    public String getCdEncodingHashXmlRichDoc() {
        return this.cdEncodingHashXmlRichDoc;
    }

    public void setCdEncodingHashXmlRichDoc(String cdEncodingHashXmlRichDoc) {
        this.cdEncodingHashXmlRichDoc = cdEncodingHashXmlRichDoc;
    }

    @Column(name = "CD_ENCODING_HASH_XML_RISP_DOC")
    public String getCdEncodingHashXmlRispDoc() {
        return this.cdEncodingHashXmlRispDoc;
    }

    public void setCdEncodingHashXmlRispDoc(String cdEncodingHashXmlRispDoc) {
        this.cdEncodingHashXmlRispDoc = cdEncodingHashXmlRispDoc;
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

    @Column(name = "CD_KEY_UNITA_DOC_NORMALIZ")
    public String getCdKeyUnitaDocNormaliz() {
        return this.cdKeyUnitaDocNormaliz;
    }

    public void setCdKeyUnitaDocNormaliz(String cdKeyUnitaDocNormaliz) {
        this.cdKeyUnitaDocNormaliz = cdKeyUnitaDocNormaliz;
    }

    @Column(name = "CD_REGISTRO_KEY_UNITA_DOC")
    public String getCdRegistroKeyUnitaDoc() {
        return this.cdRegistroKeyUnitaDoc;
    }

    public void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
        this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
    }

    @Column(name = "CD_VERSIONE_XML")
    public String getCdVersioneXml() {
        return this.cdVersioneXml;
    }

    public void setCdVersioneXml(String cdVersioneXml) {
        this.cdVersioneXml = cdVersioneXml;
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

    @Column(name = "DS_ALGO_HASH_XML_RAPP_DOC")
    public String getDsAlgoHashXmlRappDoc() {
        return this.dsAlgoHashXmlRappDoc;
    }

    public void setDsAlgoHashXmlRappDoc(String dsAlgoHashXmlRappDoc) {
        this.dsAlgoHashXmlRappDoc = dsAlgoHashXmlRappDoc;
    }

    @Column(name = "DS_ALGO_HASH_XML_RICH_DOC")
    public String getDsAlgoHashXmlRichDoc() {
        return this.dsAlgoHashXmlRichDoc;
    }

    public void setDsAlgoHashXmlRichDoc(String dsAlgoHashXmlRichDoc) {
        this.dsAlgoHashXmlRichDoc = dsAlgoHashXmlRichDoc;
    }

    @Column(name = "DS_ALGO_HASH_XML_RISP_DOC")
    public String getDsAlgoHashXmlRispDoc() {
        return this.dsAlgoHashXmlRispDoc;
    }

    public void setDsAlgoHashXmlRispDoc(String dsAlgoHashXmlRispDoc) {
        this.dsAlgoHashXmlRispDoc = dsAlgoHashXmlRispDoc;
    }

    @Column(name = "DS_AUTORE_DOC")
    public String getDsAutoreDoc() {
        return this.dsAutoreDoc;
    }

    public void setDsAutoreDoc(String dsAutoreDoc) {
        this.dsAutoreDoc = dsAutoreDoc;
    }

    @Column(name = "DS_HASH_XML_RAPP_DOC")
    public String getDsHashXmlRappDoc() {
        return this.dsHashXmlRappDoc;
    }

    public void setDsHashXmlRappDoc(String dsHashXmlRappDoc) {
        this.dsHashXmlRappDoc = dsHashXmlRappDoc;
    }

    @Column(name = "DS_HASH_XML_RICH_DOC")
    public String getDsHashXmlRichDoc() {
        return this.dsHashXmlRichDoc;
    }

    public void setDsHashXmlRichDoc(String dsHashXmlRichDoc) {
        this.dsHashXmlRichDoc = dsHashXmlRichDoc;
    }

    @Column(name = "DS_HASH_XML_RISP_DOC")
    public String getDsHashXmlRispDoc() {
        return this.dsHashXmlRispDoc;
    }

    public void setDsHashXmlRispDoc(String dsHashXmlRispDoc) {
        this.dsHashXmlRispDoc = dsHashXmlRispDoc;
    }

    @Column(name = "DS_MSG_ESITO_VERIF_FIRME")
    public String getDsMsgEsitoVerifFirme() {
        return this.dsMsgEsitoVerifFirme;
    }

    public void setDsMsgEsitoVerifFirme(String dsMsgEsitoVerifFirme) {
        this.dsMsgEsitoVerifFirme = dsMsgEsitoVerifFirme;
    }

    @Column(name = "DS_URN_XML_RAPP_DOC")
    public String getDsUrnXmlRappDoc() {
        return this.dsUrnXmlRappDoc;
    }

    public void setDsUrnXmlRappDoc(String dsUrnXmlRappDoc) {
        this.dsUrnXmlRappDoc = dsUrnXmlRappDoc;
    }

    @Column(name = "DS_URN_XML_RAPP_DOC_NORMALIZ")
    public String getDsUrnXmlRappDocNormaliz() {
        return this.dsUrnXmlRappDocNormaliz;
    }

    public void setDsUrnXmlRappDocNormaliz(String dsUrnXmlRappDocNormaliz) {
        this.dsUrnXmlRappDocNormaliz = dsUrnXmlRappDocNormaliz;
    }

    @Column(name = "DS_URN_XML_RAPP_DOC_INIZIALE")
    public String getDsUrnXmlRappDocIniziale() {
        return this.dsUrnXmlRappDocIniziale;
    }

    public void setDsUrnXmlRappDocIniziale(String dsUrnXmlRappDocIniziale) {
        this.dsUrnXmlRappDocIniziale = dsUrnXmlRappDocIniziale;
    }

    @Column(name = "DS_URN_XML_RICH_DOC")
    public String getDsUrnXmlRichDoc() {
        return this.dsUrnXmlRichDoc;
    }

    public void setDsUrnXmlRichDoc(String dsUrnXmlRichDoc) {
        this.dsUrnXmlRichDoc = dsUrnXmlRichDoc;
    }

    @Column(name = "DS_URN_XML_RICH_DOC_NORMALIZ")
    public String getDsUrnXmlRichDocNormaliz() {
        return this.dsUrnXmlRichDocNormaliz;
    }

    public void setDsUrnXmlRichDocNormaliz(String dsUrnXmlRichDocNormaliz) {
        this.dsUrnXmlRichDocNormaliz = dsUrnXmlRichDocNormaliz;
    }

    @Column(name = "DS_URN_XML_RICH_DOC_INIZIALE")
    public String getDsUrnXmlRichDocIniziale() {
        return this.dsUrnXmlRichDocIniziale;
    }

    public void setDsUrnXmlRichDocIniziale(String dsUrnXmlRichDocIniziale) {
        this.dsUrnXmlRichDocIniziale = dsUrnXmlRichDocIniziale;
    }

    @Column(name = "DS_URN_XML_RISP_DOC")
    public String getDsUrnXmlRispDoc() {
        return this.dsUrnXmlRispDoc;
    }

    public void setDsUrnXmlRispDoc(String dsUrnXmlRispDoc) {
        this.dsUrnXmlRispDoc = dsUrnXmlRispDoc;
    }

    @Column(name = "DS_URN_XML_RISP_DOC_NORMALIZ")
    public String getDsUrnXmlRispDocNormaliz() {
        return this.dsUrnXmlRispDocNormaliz;
    }

    public void setDsUrnXmlRispDocNormaliz(String dsUrnXmlRispDocNormaliz) {
        this.dsUrnXmlRispDocNormaliz = dsUrnXmlRispDocNormaliz;
    }

    @Column(name = "DS_URN_XML_RISP_DOC_INIZIALE")
    public String getDsUrnXmlRispDocIniziale() {
        return this.dsUrnXmlRispDocIniziale;
    }

    public void setDsUrnXmlRispDocIniziale(String dsUrnXmlRispDocIniziale) {
        this.dsUrnXmlRispDocIniziale = dsUrnXmlRispDocIniziale;
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
    @Column(name = "DT_CREAZIONE")
    public Date getDtCreazione() {
        return this.dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
        this.dtCreazione = dtCreazione;
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

    @Column(name = "FL_DOC_FIRMATO", columnDefinition = "char(1)")
    public String getFlDocFirmato() {
        return this.flDocFirmato;
    }

    public void setFlDocFirmato(String flDocFirmato) {
        this.flDocFirmato = flDocFirmato;
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

    @Id
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

    @Column(name = "ID_STRUT_DOC")
    public BigDecimal getIdStrutDoc() {
        return this.idStrutDoc;
    }

    public void setIdStrutDoc(BigDecimal idStrutDoc) {
        this.idStrutDoc = idStrutDoc;
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

    @Column(name = "ID_VOLUME_CONSERV")
    public BigDecimal getIdVolumeConserv() {
        return this.idVolumeConserv;
    }

    public void setIdVolumeConserv(BigDecimal idVolumeConserv) {
        this.idVolumeConserv = idVolumeConserv;
    }

    @Column(name = "NI_ORD_DOC")
    public BigDecimal getNiOrdDoc() {
        return this.niOrdDoc;
    }

    public void setNiOrdDoc(BigDecimal niOrdDoc) {
        this.niOrdDoc = niOrdDoc;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
        return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
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

    @Column(name = "NM_SISTEMA_MIGRAZ")
    public String getNmSistemaMigraz() {
        return this.nmSistemaMigraz;
    }

    public void setNmSistemaMigraz(String nmSistemaMigraz) {
        this.nmSistemaMigraz = nmSistemaMigraz;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
        return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
    }

    @Column(name = "NM_TIPO_DOC")
    public String getNmTipoDoc() {
        return this.nmTipoDoc;
    }

    public void setNmTipoDoc(String nmTipoDoc) {
        this.nmTipoDoc = nmTipoDoc;
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

    @Column(name = "NT_DOC")
    public String getNtDoc() {
        return this.ntDoc;
    }

    public void setNtDoc(String ntDoc) {
        this.ntDoc = ntDoc;
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

    @Column(name = "TI_CONSERVAZIONE")
    public String getTiConservazione() {
        return this.tiConservazione;
    }

    public void setTiConservazione(String tiConservazione) {
        this.tiConservazione = tiConservazione;
    }

    @Column(name = "TI_DOC")
    public String getTiDoc() {
        return this.tiDoc;
    }

    public void setTiDoc(String tiDoc) {
        this.tiDoc = tiDoc;
    }

    @Column(name = "TI_ESITO_VERIF_FIRME")
    public String getTiEsitoVerifFirme() {
        return this.tiEsitoVerifFirme;
    }

    public void setTiEsitoVerifFirme(String tiEsitoVerifFirme) {
        this.tiEsitoVerifFirme = tiEsitoVerifFirme;
    }

    @Column(name = "TI_STATO_CONSERVAZIONE")
    public String getTiStatoConservazione() {
        return this.tiStatoConservazione;
    }

    public void setTiStatoConservazione(String tiStatoConservazione) {
        this.tiStatoConservazione = tiStatoConservazione;
    }

    @Column(name = "TI_STATO_DOC")
    public String getTiStatoDoc() {
        return this.tiStatoDoc;
    }

    public void setTiStatoDoc(String tiStatoDoc) {
        this.tiStatoDoc = tiStatoDoc;
    }

    @Column(name = "TI_STATO_ELENCO_VERS")
    public String getTiStatoElencoVers() {
        return this.tiStatoElencoVers;
    }

    public void setTiStatoElencoVers(String tiStatoElencoVers) {
        this.tiStatoElencoVers = tiStatoElencoVers;
    }

    @Column(name = "TI_STATO_VOLUME_CONSERV")
    public String getTiStatoVolumeConserv() {
        return this.tiStatoVolumeConserv;
    }

    public void setTiStatoVolumeConserv(String tiStatoVolumeConserv) {
        this.tiStatoVolumeConserv = tiStatoVolumeConserv;
    }

    @Column(name = "UTENTE_VERS")
    public String getUtenteVers() {
        return this.utenteVers;
    }

    public void setUtenteVers(String utenteVers) {
        this.utenteVers = utenteVers;
    }

    @Column(name = "UTENTE")
    public String getUtente() {
        return this.utente;
    }

    public void setUtente(String utente) {
        this.utente = utente;
    }

    @Column(name = "ID_SESSIONE_VERS")
    public BigDecimal getIdSessioneVers() {
        return idSessioneVers;
    }

    public void setIdSessioneVers(BigDecimal idSessioneVers) {
        this.idSessioneVers = idSessioneVers;
    }

}
