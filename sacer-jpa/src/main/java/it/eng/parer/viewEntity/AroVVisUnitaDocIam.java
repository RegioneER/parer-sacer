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
import javax.persistence.Lob;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ARO_V_VIS_UNITA_DOC_IAM database table.
 *
 */
@Entity
@Table(name = "ARO_V_VIS_UNITA_DOC_IAM")

public class AroVVisUnitaDocIam implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal aaKeyUnitaDoc;
    private String blXmlIndexUd;
    private String blXmlRappUd;
    private String blXmlRichUd;
    private String blXmlRispUd;
    private String cdEncodingHashXmlIndexUd;
    private String cdEncodingHashXmlRappUd;
    private String cdEncodingHashXmlRichUd;
    private String cdEncodingHashXmlRispUd;
    private String cdFascicPrinc;
    private String cdKeyDocVers;
    private String cdKeyUnitaDoc;
    private String cdKeyUnitaDocNormaliz;
    private String cdRegistroKeyUnitaDoc;
    private String cdSottofascicPrinc;
    private String cdVersioneXml;
    private String dlDoc;
    private String dlOggettoUnitaDoc;
    private String dsAlgoHashXmlIndexUd;
    private String dsAlgoHashXmlRappUd;
    private String dsAlgoHashXmlRichUd;
    private String dsAlgoHashXmlRispUd;
    private String dsAutoreDoc;
    private String dsClassifPrinc;
    private String dsHashXmlIndexUd;
    private String dsHashXmlRappUd;
    private String dsHashXmlRichUd;
    private String dsHashXmlRispUd;
    private String dsMsgEsitoVerifFirmeUd;
    private String dsOggettoFascicPrinc;
    private String dsOggettoSottofascicPrinc;
    private String dsUffCompUnitaDoc;
    private String dsUrnXmlIndexUd;
    private String dsUrnXmlIndexUdNormaliz;
    private String dsUrnXmlIndexUdIniziale;
    private String dsUrnXmlRappUd;
    private String dsUrnXmlRappUdNormaliz;
    private String dsUrnXmlRappUdIniziale;
    private String dsUrnXmlRichUd;
    private String dsUrnXmlRichUdNormaliz;
    private String dsUrnXmlRichUdIniziale;
    private String dsUrnXmlRispUd;
    private String dsUrnXmlRispUdNormaliz;
    private String dsUrnXmlRispUdIniziale;
    private Date dtAnnul;
    private Date dtCreazione;
    private Date dtRegUnitaDoc;
    private String flCartaceo;
    private String flDocFirmato;
    private String flForzaAccettazione;
    private String flForzaCollegamento;
    private String flForzaConservazione;
    private String flUnitaDocFirmato;
    private BigDecimal idDoc;
    private BigDecimal idTipoUnitaDoc;
    private BigDecimal idUnitaDoc;
    private BigDecimal niAlleg;
    private BigDecimal niAnnessi;
    private BigDecimal niAnnot;
    private String nmAmbiente;
    private String nmEnte;
    private String nmSistemaMigraz;
    private String nmStrut;
    private String nmTipoDoc;
    private String nmTipoUnitaDoc;
    private String ntAnnul;
    private String ntUnitaDoc;
    private String tiAnnul;
    private String tiConservazione;
    private String tiEsitoVerifFirmeDoc;
    private String tiEsitoVerifFirmeUd;
    private String tiStatoConservazione;
    private String utenteVers;
    private String utente;
    private String cdIndIpClient;
    private String nmSistemaVersante;
    private BigDecimal idSessioneVers;

    public AroVVisUnitaDocIam() {/* Hibernate */
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
	return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
	this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Lob()
    @Column(name = "BL_XML_INDEX_UD")
    public String getBlXmlIndexUd() {
	return this.blXmlIndexUd;
    }

    public void setBlXmlIndexUd(String blXmlIndexUd) {
	this.blXmlIndexUd = blXmlIndexUd;
    }

    @Lob
    @Column(name = "BL_XML_RAPP_UD")
    public String getBlXmlRappUd() {
	return this.blXmlRappUd;
    }

    public void setBlXmlRappUd(String blXmlRappUd) {
	this.blXmlRappUd = blXmlRappUd;
    }

    @Lob
    @Column(name = "BL_XML_RICH_UD")
    public String getBlXmlRichUd() {
	return this.blXmlRichUd;
    }

    public void setBlXmlRichUd(String blXmlRichUd) {
	this.blXmlRichUd = blXmlRichUd;
    }

    @Lob()
    @Column(name = "BL_XML_RISP_UD")
    public String getBlXmlRispUd() {
	return this.blXmlRispUd;
    }

    public void setBlXmlRispUd(String blXmlRispUd) {
	this.blXmlRispUd = blXmlRispUd;
    }

    @Column(name = "CD_ENCODING_HASH_XML_INDEX_UD")
    public String getCdEncodingHashXmlIndexUd() {
	return this.cdEncodingHashXmlIndexUd;
    }

    public void setCdEncodingHashXmlIndexUd(String cdEncodingHashXmlIndexUd) {
	this.cdEncodingHashXmlIndexUd = cdEncodingHashXmlIndexUd;
    }

    @Column(name = "CD_ENCODING_HASH_XML_RAPP_UD")
    public String getCdEncodingHashXmlRappUd() {
	return this.cdEncodingHashXmlRappUd;
    }

    public void setCdEncodingHashXmlRappUd(String cdEncodingHashXmlRappUd) {
	this.cdEncodingHashXmlRappUd = cdEncodingHashXmlRappUd;
    }

    @Column(name = "CD_ENCODING_HASH_XML_RICH_UD")
    public String getCdEncodingHashXmlRichUd() {
	return this.cdEncodingHashXmlRichUd;
    }

    public void setCdEncodingHashXmlRichUd(String cdEncodingHashXmlRichUd) {
	this.cdEncodingHashXmlRichUd = cdEncodingHashXmlRichUd;
    }

    @Column(name = "CD_ENCODING_HASH_XML_RISP_UD")
    public String getCdEncodingHashXmlRispUd() {
	return this.cdEncodingHashXmlRispUd;
    }

    public void setCdEncodingHashXmlRispUd(String cdEncodingHashXmlRispUd) {
	this.cdEncodingHashXmlRispUd = cdEncodingHashXmlRispUd;
    }

    @Column(name = "CD_FASCIC_PRINC")
    public String getCdFascicPrinc() {
	return this.cdFascicPrinc;
    }

    public void setCdFascicPrinc(String cdFascicPrinc) {
	this.cdFascicPrinc = cdFascicPrinc;
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

    @Column(name = "CD_SOTTOFASCIC_PRINC")
    public String getCdSottofascicPrinc() {
	return this.cdSottofascicPrinc;
    }

    public void setCdSottofascicPrinc(String cdSottofascicPrinc) {
	this.cdSottofascicPrinc = cdSottofascicPrinc;
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

    @Column(name = "DS_ALGO_HASH_XML_INDEX_UD")
    public String getDsAlgoHashXmlIndexUd() {
	return this.dsAlgoHashXmlIndexUd;
    }

    public void setDsAlgoHashXmlIndexUd(String dsAlgoHashXmlIndexUd) {
	this.dsAlgoHashXmlIndexUd = dsAlgoHashXmlIndexUd;
    }

    @Column(name = "DS_ALGO_HASH_XML_RAPP_UD")
    public String getDsAlgoHashXmlRappUd() {
	return this.dsAlgoHashXmlRappUd;
    }

    public void setDsAlgoHashXmlRappUd(String dsAlgoHashXmlRappUd) {
	this.dsAlgoHashXmlRappUd = dsAlgoHashXmlRappUd;
    }

    @Column(name = "DS_ALGO_HASH_XML_RICH_UD")
    public String getDsAlgoHashXmlRichUd() {
	return this.dsAlgoHashXmlRichUd;
    }

    public void setDsAlgoHashXmlRichUd(String dsAlgoHashXmlRichUd) {
	this.dsAlgoHashXmlRichUd = dsAlgoHashXmlRichUd;
    }

    @Column(name = "DS_ALGO_HASH_XML_RISP_UD")
    public String getDsAlgoHashXmlRispUd() {
	return this.dsAlgoHashXmlRispUd;
    }

    public void setDsAlgoHashXmlRispUd(String dsAlgoHashXmlRispUd) {
	this.dsAlgoHashXmlRispUd = dsAlgoHashXmlRispUd;
    }

    @Column(name = "DS_AUTORE_DOC")
    public String getDsAutoreDoc() {
	return this.dsAutoreDoc;
    }

    public void setDsAutoreDoc(String dsAutoreDoc) {
	this.dsAutoreDoc = dsAutoreDoc;
    }

    @Column(name = "DS_CLASSIF_PRINC")
    public String getDsClassifPrinc() {
	return this.dsClassifPrinc;
    }

    public void setDsClassifPrinc(String dsClassifPrinc) {
	this.dsClassifPrinc = dsClassifPrinc;
    }

    @Column(name = "DS_HASH_XML_INDEX_UD")
    public String getDsHashXmlIndexUd() {
	return this.dsHashXmlIndexUd;
    }

    public void setDsHashXmlIndexUd(String dsHashXmlIndexUd) {
	this.dsHashXmlIndexUd = dsHashXmlIndexUd;
    }

    @Column(name = "DS_HASH_XML_RAPP_UD")
    public String getDsHashXmlRappUd() {
	return this.dsHashXmlRappUd;
    }

    public void setDsHashXmlRappUd(String dsHashXmlRappUd) {
	this.dsHashXmlRappUd = dsHashXmlRappUd;
    }

    @Column(name = "DS_HASH_XML_RICH_UD")
    public String getDsHashXmlRichUd() {
	return this.dsHashXmlRichUd;
    }

    public void setDsHashXmlRichUd(String dsHashXmlRichUd) {
	this.dsHashXmlRichUd = dsHashXmlRichUd;
    }

    @Column(name = "DS_HASH_XML_RISP_UD")
    public String getDsHashXmlRispUd() {
	return this.dsHashXmlRispUd;
    }

    public void setDsHashXmlRispUd(String dsHashXmlRispUd) {
	this.dsHashXmlRispUd = dsHashXmlRispUd;
    }

    @Column(name = "DS_MSG_ESITO_VERIF_FIRME_UD")
    public String getDsMsgEsitoVerifFirmeUd() {
	return this.dsMsgEsitoVerifFirmeUd;
    }

    public void setDsMsgEsitoVerifFirmeUd(String dsMsgEsitoVerifFirmeUd) {
	this.dsMsgEsitoVerifFirmeUd = dsMsgEsitoVerifFirmeUd;
    }

    @Column(name = "DS_OGGETTO_FASCIC_PRINC")
    public String getDsOggettoFascicPrinc() {
	return this.dsOggettoFascicPrinc;
    }

    public void setDsOggettoFascicPrinc(String dsOggettoFascicPrinc) {
	this.dsOggettoFascicPrinc = dsOggettoFascicPrinc;
    }

    @Column(name = "DS_OGGETTO_SOTTOFASCIC_PRINC")
    public String getDsOggettoSottofascicPrinc() {
	return this.dsOggettoSottofascicPrinc;
    }

    public void setDsOggettoSottofascicPrinc(String dsOggettoSottofascicPrinc) {
	this.dsOggettoSottofascicPrinc = dsOggettoSottofascicPrinc;
    }

    @Column(name = "DS_UFF_COMP_UNITA_DOC")
    public String getDsUffCompUnitaDoc() {
	return this.dsUffCompUnitaDoc;
    }

    public void setDsUffCompUnitaDoc(String dsUffCompUnitaDoc) {
	this.dsUffCompUnitaDoc = dsUffCompUnitaDoc;
    }

    @Column(name = "DS_URN_XML_INDEX_UD")
    public String getDsUrnXmlIndexUd() {
	return this.dsUrnXmlIndexUd;
    }

    public void setDsUrnXmlIndexUd(String dsUrnXmlIndexUd) {
	this.dsUrnXmlIndexUd = dsUrnXmlIndexUd;
    }

    @Column(name = "DS_URN_XML_INDEX_UD_NORMALIZ")
    public String getDsUrnXmlIndexUdNormaliz() {
	return this.dsUrnXmlIndexUdNormaliz;
    }

    public void setDsUrnXmlIndexUdNormaliz(String dsUrnXmlIndexUdNormaliz) {
	this.dsUrnXmlIndexUdNormaliz = dsUrnXmlIndexUdNormaliz;
    }

    @Column(name = "DS_URN_XML_INDEX_UD_INIZIALE")
    public String getDsUrnXmlIndexUdIniziale() {
	return this.dsUrnXmlIndexUdIniziale;
    }

    public void setDsUrnXmlIndexUdIniziale(String dsUrnXmlIndexUdIniziale) {
	this.dsUrnXmlIndexUdIniziale = dsUrnXmlIndexUdIniziale;
    }

    @Column(name = "DS_URN_XML_RAPP_UD")
    public String getDsUrnXmlRappUd() {
	return this.dsUrnXmlRappUd;
    }

    public void setDsUrnXmlRappUd(String dsUrnXmlRappUd) {
	this.dsUrnXmlRappUd = dsUrnXmlRappUd;
    }

    @Column(name = "DS_URN_XML_RAPP_UD_NORMALIZ")
    public String getDsUrnXmlRappUdNormaliz() {
	return this.dsUrnXmlRappUdNormaliz;
    }

    public void setDsUrnXmlRappUdNormaliz(String dsUrnXmlRappUdNormaliz) {
	this.dsUrnXmlRappUdNormaliz = dsUrnXmlRappUdNormaliz;
    }

    @Column(name = "DS_URN_XML_RAPP_UD_INIZIALE")
    public String getDsUrnXmlRappUdIniziale() {
	return this.dsUrnXmlRappUdIniziale;
    }

    public void setDsUrnXmlRappUdIniziale(String dsUrnXmlRappUdIniziale) {
	this.dsUrnXmlRappUdIniziale = dsUrnXmlRappUdIniziale;
    }

    @Column(name = "DS_URN_XML_RICH_UD")
    public String getDsUrnXmlRichUd() {
	return this.dsUrnXmlRichUd;
    }

    public void setDsUrnXmlRichUd(String dsUrnXmlRichUd) {
	this.dsUrnXmlRichUd = dsUrnXmlRichUd;
    }

    @Column(name = "DS_URN_XML_RICH_UD_NORMALIZ")
    public String getDsUrnXmlRichUdNormaliz() {
	return this.dsUrnXmlRichUdNormaliz;
    }

    public void setDsUrnXmlRichUdNormaliz(String dsUrnXmlRichUdNormaliz) {
	this.dsUrnXmlRichUdNormaliz = dsUrnXmlRichUdNormaliz;
    }

    @Column(name = "DS_URN_XML_RICH_UD_INIZIALE")
    public String getDsUrnXmlRichUdIniziale() {
	return this.dsUrnXmlRichUdIniziale;
    }

    public void setDsUrnXmlRichUdIniziale(String dsUrnXmlRichUdIniziale) {
	this.dsUrnXmlRichUdIniziale = dsUrnXmlRichUdIniziale;
    }

    @Column(name = "DS_URN_XML_RISP_UD")
    public String getDsUrnXmlRispUd() {
	return this.dsUrnXmlRispUd;
    }

    public void setDsUrnXmlRispUd(String dsUrnXmlRispUd) {
	this.dsUrnXmlRispUd = dsUrnXmlRispUd;
    }

    @Column(name = "DS_URN_XML_RISP_UD_NORMALIZ")
    public String getDsUrnXmlRispUdNormaliz() {
	return this.dsUrnXmlRispUdNormaliz;
    }

    public void setDsUrnXmlRispUdNormaliz(String dsUrnXmlRispUdNormaliz) {
	this.dsUrnXmlRispUdNormaliz = dsUrnXmlRispUdNormaliz;
    }

    @Column(name = "DS_URN_XML_RISP_UD_INIZIALE")
    public String getDsUrnXmlRispUdIniziale() {
	return this.dsUrnXmlRispUdIniziale;
    }

    public void setDsUrnXmlRispUdIniziale(String dsUrnXmlRispUdIniziale) {
	this.dsUrnXmlRispUdIniziale = dsUrnXmlRispUdIniziale;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ANNUL")
    public Date getDtAnnul() {
	return this.dtAnnul;
    }

    public void setDtAnnul(Date dtAnnul) {
	this.dtAnnul = dtAnnul;
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
    @Column(name = "DT_REG_UNITA_DOC")
    public Date getDtRegUnitaDoc() {
	return this.dtRegUnitaDoc;
    }

    public void setDtRegUnitaDoc(Date dtRegUnitaDoc) {
	this.dtRegUnitaDoc = dtRegUnitaDoc;
    }

    @Column(name = "FL_CARTACEO", columnDefinition = "char(1)")
    public String getFlCartaceo() {
	return this.flCartaceo;
    }

    public void setFlCartaceo(String flCartaceo) {
	this.flCartaceo = flCartaceo;
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

    @Column(name = "FL_FORZA_COLLEGAMENTO", columnDefinition = "char(1)")
    public String getFlForzaCollegamento() {
	return this.flForzaCollegamento;
    }

    public void setFlForzaCollegamento(String flForzaCollegamento) {
	this.flForzaCollegamento = flForzaCollegamento;
    }

    @Column(name = "FL_FORZA_CONSERVAZIONE", columnDefinition = "char(1)")
    public String getFlForzaConservazione() {
	return this.flForzaConservazione;
    }

    public void setFlForzaConservazione(String flForzaConservazione) {
	this.flForzaConservazione = flForzaConservazione;
    }

    @Column(name = "FL_UNITA_DOC_FIRMATO", columnDefinition = "char(1)")
    public String getFlUnitaDocFirmato() {
	return this.flUnitaDocFirmato;
    }

    public void setFlUnitaDocFirmato(String flUnitaDocFirmato) {
	this.flUnitaDocFirmato = flUnitaDocFirmato;
    }

    @Column(name = "ID_DOC")
    public BigDecimal getIdDoc() {
	return this.idDoc;
    }

    public void setIdDoc(BigDecimal idDoc) {
	this.idDoc = idDoc;
    }

    @Column(name = "ID_TIPO_UNITA_DOC")
    public BigDecimal getIdTipoUnitaDoc() {
	return this.idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
	this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    @Id
    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
	return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
	this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "NI_ALLEG")
    public BigDecimal getNiAlleg() {
	return this.niAlleg;
    }

    public void setNiAlleg(BigDecimal niAlleg) {
	this.niAlleg = niAlleg;
    }

    @Column(name = "NI_ANNESSI")
    public BigDecimal getNiAnnessi() {
	return this.niAnnessi;
    }

    public void setNiAnnessi(BigDecimal niAnnessi) {
	this.niAnnessi = niAnnessi;
    }

    @Column(name = "NI_ANNOT")
    public BigDecimal getNiAnnot() {
	return this.niAnnot;
    }

    public void setNiAnnot(BigDecimal niAnnot) {
	this.niAnnot = niAnnot;
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

    @Column(name = "NM_TIPO_UNITA_DOC")
    public String getNmTipoUnitaDoc() {
	return this.nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
	this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    @Column(name = "NT_ANNUL")
    public String getNtAnnul() {
	return this.ntAnnul;
    }

    public void setNtAnnul(String ntAnnul) {
	this.ntAnnul = ntAnnul;
    }

    @Column(name = "NT_UNITA_DOC")
    public String getNtUnitaDoc() {
	return this.ntUnitaDoc;
    }

    public void setNtUnitaDoc(String ntUnitaDoc) {
	this.ntUnitaDoc = ntUnitaDoc;
    }

    @Column(name = "TI_ANNUL")
    public String getTiAnnul() {
	return this.tiAnnul;
    }

    public void setTiAnnul(String tiAnnul) {
	this.tiAnnul = tiAnnul;
    }

    @Column(name = "TI_CONSERVAZIONE")
    public String getTiConservazione() {
	return this.tiConservazione;
    }

    public void setTiConservazione(String tiConservazione) {
	this.tiConservazione = tiConservazione;
    }

    @Column(name = "TI_ESITO_VERIF_FIRME_DOC")
    public String getTiEsitoVerifFirmeDoc() {
	return this.tiEsitoVerifFirmeDoc;
    }

    public void setTiEsitoVerifFirmeDoc(String tiEsitoVerifFirmeDoc) {
	this.tiEsitoVerifFirmeDoc = tiEsitoVerifFirmeDoc;
    }

    @Column(name = "TI_ESITO_VERIF_FIRME_UD")
    public String getTiEsitoVerifFirmeUd() {
	return this.tiEsitoVerifFirmeUd;
    }

    public void setTiEsitoVerifFirmeUd(String tiEsitoVerifFirmeUd) {
	this.tiEsitoVerifFirmeUd = tiEsitoVerifFirmeUd;
    }

    @Column(name = "TI_STATO_CONSERVAZIONE")
    public String getTiStatoConservazione() {
	return this.tiStatoConservazione;
    }

    public void setTiStatoConservazione(String tiStatoConservazione) {
	this.tiStatoConservazione = tiStatoConservazione;
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

    @Column(name = "CD_IND_IP_CLIENT")
    public String getCdIndIpClient() {
	return this.cdIndIpClient;
    }

    public void setCdIndIpClient(String cdIndIpClient) {
	this.cdIndIpClient = cdIndIpClient;
    }

    @Column(name = "NM_SISTEMA_VERSANTE")
    public String getNmSistemaVersante() {
	return this.nmSistemaVersante;
    }

    public void setNmSistemaVersante(String nmSistemaVersante) {
	this.nmSistemaVersante = nmSistemaVersante;
    }

    @Column(name = "ID_SESSIONE_VERS")
    public BigDecimal getIdSessioneVers() {
	return idSessioneVers;
    }

    public void setIdSessioneVers(BigDecimal idSessioneVers) {
	this.idSessioneVers = idSessioneVers;
    }

}
