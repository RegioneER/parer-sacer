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

package it.eng.parer.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the ARO_UNITA_DOC database table.
 */
@Entity
@Table(name = "ARO_UNITA_DOC")
public class AroUnitaDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idUnitaDoc;

    private BigDecimal aaKeyUnitaDoc;

    private String cdFascicPrinc;

    private String cdKeyUnitaDoc;

    private String cdKeyUnitaDocNormaliz;

    private String cdRegistroKeyUnitaDoc;

    private String cdSottofascicPrinc;

    private String dlOggettoUnitaDoc;

    private String dsClassifPrinc;

    private String dsKeyOrd;

    private String dsMsgEsitoVerifFirme;

    private String dsOggettoFascicPrinc;

    private String dsOggettoSottofascicPrinc;

    private String dsUffCompUnitaDoc;

    private Date dtAnnul;

    private Date dtCreazione;

    private Date dtRegUnitaDoc;

    private String flCartaceo;

    private String flForzaAccettazione;

    private String flForzaCollegamento;

    private String flForzaConservazione;

    private String flUnitaDocFirmato;

    private BigDecimal niAlleg;

    private BigDecimal niAnnessi;

    private BigDecimal niAnnot;

    private BigDecimal niResetStato;

    private String nmSistemaMigraz;

    private String ntAnnul;

    private String ntUnitaDoc;

    private BigDecimal pgUnitaDoc;

    private String tiAnnul;

    private String tiConservazione;

    private String tiEsitoVerifFirme;

    private String tiStatoConservazione;

    private String tiStatoUdElencoVers;

    private Date tsStatoElencoVers;

    private Date tsLastResetStato;

    private List<AroArchivSec> aroArchivSecs = new ArrayList<>();

    private List<AroCompDoc> aroCompDocs = new ArrayList<>();

    private List<AroDoc> aroDocs = new ArrayList<>();

    private List<AroIndiceAipUd> aroIndiceAipUds = new ArrayList<>();

    private List<AroIndiceAipUdDaElab> aroIndiceAipUdDaElabs = new ArrayList<>();

    private List<AroLinkUnitaDoc> aroLinkUnitaDocLinks = new ArrayList<>();

    private List<AroLinkUnitaDoc> aroLinkUnitaDocs = new ArrayList<>();
    private List<AroNotaUnitaDoc> aroNotaUnitaDocs = new ArrayList<>();
    private List<AroUdAppartVerSerie> aroUdAppartVerSeries = new ArrayList<>();

    private DecRegistroUnitaDoc decRegistroUnitaDoc;

    private Long idDecRegistroUnitaDoc;

    private DecTipoUnitaDoc decTipoUnitaDoc;

    private Long idDecTipoUnitaDoc;

    private OrgStrut orgStrut;

    private Long idOrgStrut;

    private OrgSubStrut orgSubStrut;

    private Long idOrgSubStrut;

    private IamUser iamUser;

    private List<AroUsoXsdDatiSpec> aroUsoXsdDatiSpecs = new ArrayList<>();

    private List<RecUnitaDocRecup> recUnitaDocRecups = new ArrayList<>();

    private List<SerUdErrFileInput> serUdErrFileInputs = new ArrayList<>();

    private List<VolAppartUnitaDocVolume> volAppartUnitaDocVolumes = new ArrayList<>();

    private List<VrsSessioneVers> vrsSessioneVers = new ArrayList<>();

    private ElvElencoVer elvElencoVer;

    private AroXmlUnitaDocObjectStorage aroXmlUnitaDocObjectStorage;

    private List<ElvUdVersDaElabElenco> elvUdVersDaElabElencos = new ArrayList<>();

    private List<AroItemRichAnnulVers> aroItemRichAnnulVers = new ArrayList<>();

    private List<ElvElencoVersUdAnnul> elvElencoVersUdAnnuls = new ArrayList<>();

    private List<VolVolumeVersUdAnnul> volVolumeVersUdAnnuls = new ArrayList<>();

    private List<FasUnitaDocFascicolo> fasUnitaDocFascicolos = new ArrayList<>();

    private List<AroVersIniUnitaDoc> aroVersIniUnitaDocs = new ArrayList<>();

    private List<AroUpdUnitaDoc> aroUpdUnitaDocs = new ArrayList<>();

    private List<AroLogStatoConservUd> aroLogStatoConservUds;

    public AroUnitaDoc() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_UNITA_DOC")
    @GenericGenerator(name = "SARO_UNITA_DOC_ID_UNITA_DOC_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SARO_UNITA_DOC"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SARO_UNITA_DOC_ID_UNITA_DOC_GENERATOR")
    public Long getIdUnitaDoc() {
	return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(Long idUnitaDoc) {
	this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
	return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
	this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Column(name = "CD_FASCIC_PRINC")
    public String getCdFascicPrinc() {
	return this.cdFascicPrinc;
    }

    public void setCdFascicPrinc(String cdFascicPrinc) {
	this.cdFascicPrinc = cdFascicPrinc;
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

    @Column(name = "DL_OGGETTO_UNITA_DOC")
    public String getDlOggettoUnitaDoc() {
	return this.dlOggettoUnitaDoc;
    }

    public void setDlOggettoUnitaDoc(String dlOggettoUnitaDoc) {
	this.dlOggettoUnitaDoc = dlOggettoUnitaDoc;
    }

    @Column(name = "DS_CLASSIF_PRINC")
    public String getDsClassifPrinc() {
	return this.dsClassifPrinc;
    }

    public void setDsClassifPrinc(String dsClassifPrinc) {
	this.dsClassifPrinc = dsClassifPrinc;
    }

    @Column(name = "DS_KEY_ORD")
    public String getDsKeyOrd() {
	return this.dsKeyOrd;
    }

    public void setDsKeyOrd(String dsKeyOrd) {
	this.dsKeyOrd = dsKeyOrd;
    }

    @Column(name = "DS_MSG_ESITO_VERIF_FIRME")
    public String getDsMsgEsitoVerifFirme() {
	return this.dsMsgEsitoVerifFirme;
    }

    public void setDsMsgEsitoVerifFirme(String dsMsgEsitoVerifFirme) {
	this.dsMsgEsitoVerifFirme = dsMsgEsitoVerifFirme;
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

    @Column(name = "NI_RESET_STATO")
    public BigDecimal getNiResetStato() {
	return this.niResetStato;
    }

    public void setNiResetStato(BigDecimal niResetStato) {
	this.niResetStato = niResetStato;
    }

    @Column(name = "NM_SISTEMA_MIGRAZ")
    public String getNmSistemaMigraz() {
	return this.nmSistemaMigraz;
    }

    public void setNmSistemaMigraz(String nmSistemaMigraz) {
	this.nmSistemaMigraz = nmSistemaMigraz;
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

    @Column(name = "PG_UNITA_DOC")
    public BigDecimal getPgUnitaDoc() {
	return this.pgUnitaDoc;
    }

    public void setPgUnitaDoc(BigDecimal pgUnitaDoc) {
	this.pgUnitaDoc = pgUnitaDoc;
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

    @Column(name = "TI_STATO_UD_ELENCO_VERS")
    public String getTiStatoUdElencoVers() {
	return this.tiStatoUdElencoVers;
    }

    public void setTiStatoUdElencoVers(String tiStatoUdElencoVers) {
	this.tiStatoUdElencoVers = tiStatoUdElencoVers;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_STATO_ELENCO_VERS")
    public Date getTsStatoElencoVers() {
	return this.tsStatoElencoVers;
    }

    public void setTsStatoElencoVers(Date tsStatoElencoVers) {
	this.tsStatoElencoVers = tsStatoElencoVers;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_LAST_RESET_STATO")
    public Date getTsLastResetStato() {
	return this.tsLastResetStato;
    }

    public void setTsLastResetStato(Date tsLastResetStato) {
	this.tsLastResetStato = tsLastResetStato;
    }

    @OneToMany(mappedBy = "aroUnitaDoc", cascade = {
	    CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    public List<AroArchivSec> getAroArchivSecs() {
	return this.aroArchivSecs;
    }

    public void setAroArchivSecs(List<AroArchivSec> aroArchivSecs) {
	this.aroArchivSecs = aroArchivSecs;
    }

    @OneToMany(mappedBy = "aroUnitaDoc")
    public List<AroCompDoc> getAroCompDocs() {
	return this.aroCompDocs;
    }

    public void setAroCompDocs(List<AroCompDoc> aroCompDocs) {
	this.aroCompDocs = aroCompDocs;
    }

    @OneToMany(mappedBy = "aroUnitaDoc")
    public List<AroDoc> getAroDocs() {
	return this.aroDocs;
    }

    public void setAroDocs(List<AroDoc> aroDocs) {
	this.aroDocs = aroDocs;
    }

    @OneToMany(mappedBy = "aroUnitaDocLink")
    public List<AroLinkUnitaDoc> getAroLinkUnitaDocLinks() {
	return this.aroLinkUnitaDocLinks;
    }

    public void setAroLinkUnitaDocLinks(List<AroLinkUnitaDoc> aroLinkUnitaDocLinks) {
	this.aroLinkUnitaDocLinks = aroLinkUnitaDocLinks;
    }

    @OneToMany(mappedBy = "aroUnitaDoc")
    public List<AroLinkUnitaDoc> getAroLinkUnitaDocs() {
	return this.aroLinkUnitaDocs;
    }

    public void setAroLinkUnitaDocs(List<AroLinkUnitaDoc> aroLinkUnitaDocs) {
	this.aroLinkUnitaDocs = aroLinkUnitaDocs;
    }

    @OneToMany(mappedBy = "aroUnitaDoc", cascade = CascadeType.PERSIST)
    public List<AroNotaUnitaDoc> getAroNotaUnitaDocs() {
	return this.aroNotaUnitaDocs;
    }

    public void setAroNotaUnitaDocs(List<AroNotaUnitaDoc> aroNotaUnitaDocs) {
	this.aroNotaUnitaDocs = aroNotaUnitaDocs;
    }

    public AroNotaUnitaDoc addAroNotaUnitaDoc(AroNotaUnitaDoc aroNotaUnitaDoc) {
	getAroNotaUnitaDocs().add(aroNotaUnitaDoc);
	aroNotaUnitaDoc.setAroUnitaDoc(this);

	return aroNotaUnitaDoc;
    }

    public AroNotaUnitaDoc removeAroNotaUnitaDoc(AroNotaUnitaDoc aroNotaUnitaDoc) {
	getAroNotaUnitaDocs().remove(aroNotaUnitaDoc);
	aroNotaUnitaDoc.setAroUnitaDoc(null);

	return aroNotaUnitaDoc;
    }

    @OneToMany(mappedBy = "aroUnitaDoc")
    public List<AroUdAppartVerSerie> getAroUdAppartVerSeries() {
	return this.aroUdAppartVerSeries;
    }

    public void setAroUdAppartVerSeries(List<AroUdAppartVerSerie> aroUdAppartVerSeries) {
	this.aroUdAppartVerSeries = aroUdAppartVerSeries;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_REGISTRO_UNITA_DOC")
    public DecRegistroUnitaDoc getDecRegistroUnitaDoc() {
	return this.decRegistroUnitaDoc;
    }

    public void setDecRegistroUnitaDoc(DecRegistroUnitaDoc decRegistroUnitaDoc) {
	this.decRegistroUnitaDoc = decRegistroUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_UNITA_DOC")
    public DecTipoUnitaDoc getDecTipoUnitaDoc() {
	return this.decTipoUnitaDoc;
    }

    public void setDecTipoUnitaDoc(DecTipoUnitaDoc decTipoUnitaDoc) {
	this.decTipoUnitaDoc = decTipoUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    public OrgStrut getOrgStrut() {
	return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
	this.orgStrut = orgStrut;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SUB_STRUT")
    public OrgSubStrut getOrgSubStrut() {
	return this.orgSubStrut;
    }

    public void setOrgSubStrut(OrgSubStrut orgSubStrut) {
	this.orgSubStrut = orgSubStrut;
    }

    @Column(name = "ID_REGISTRO_UNITA_DOC", insertable = false, updatable = false)
    public Long getIdDecRegistroUnitaDoc() {
	return idDecRegistroUnitaDoc;
    }

    public void setIdDecRegistroUnitaDoc(Long idDecRegistroUnitaDoc) {
	this.idDecRegistroUnitaDoc = idDecRegistroUnitaDoc;
    }

    @Column(name = "ID_TIPO_UNITA_DOC", insertable = false, updatable = false)
    public Long getIdDecTipoUnitaDoc() {
	return idDecTipoUnitaDoc;
    }

    public void setIdDecTipoUnitaDoc(Long idDecTipoUnitaDoc) {
	this.idDecTipoUnitaDoc = idDecTipoUnitaDoc;
    }

    @Column(name = "ID_STRUT", insertable = false, updatable = false)
    public Long getIdOrgStrut() {
	return idOrgStrut;
    }

    public void setIdOrgStrut(Long idOrgStrut) {
	this.idOrgStrut = idOrgStrut;
    }

    @Column(name = "ID_SUB_STRUT", insertable = false, updatable = false)
    public Long getIdOrgSubStrut() {
	return idOrgSubStrut;
    }

    public void setIdOrgSubStrut(Long idOrgSubStrut) {
	this.idOrgSubStrut = idOrgSubStrut;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_VERS")
    public IamUser getIamUser() {
	return this.iamUser;
    }

    public void setIamUser(IamUser iamUser) {
	this.iamUser = iamUser;
    }

    @OneToMany(mappedBy = "aroUnitaDoc")
    public List<VolAppartUnitaDocVolume> getVolAppartUnitaDocVolumes() {
	return this.volAppartUnitaDocVolumes;
    }

    public void setVolAppartUnitaDocVolumes(
	    List<VolAppartUnitaDocVolume> volAppartUnitaDocVolumes) {
	this.volAppartUnitaDocVolumes = volAppartUnitaDocVolumes;
    }

    @OneToMany(mappedBy = "aroUnitaDoc")
    public List<VrsSessioneVers> getVrsSessioneVers() {
	return this.vrsSessioneVers;
    }

    public void setVrsSessioneVers(List<VrsSessioneVers> vrsSessioneVers) {
	this.vrsSessioneVers = vrsSessioneVers;
    }

    @OneToMany(mappedBy = "aroUnitaDoc")
    public List<AroUsoXsdDatiSpec> getAroUsoXsdDatiSpecs() {
	return this.aroUsoXsdDatiSpecs;
    }

    public void setAroUsoXsdDatiSpecs(List<AroUsoXsdDatiSpec> aroUsoXsdDatiSpecs) {
	this.aroUsoXsdDatiSpecs = aroUsoXsdDatiSpecs;
    }

    @OneToMany(mappedBy = "aroUnitaDoc")

    public List<RecUnitaDocRecup> getRecUnitaDocRecups() {
	return this.recUnitaDocRecups;
    }

    public void setRecUnitaDocRecups(List<RecUnitaDocRecup> recUnitaDocRecups) {
	this.recUnitaDocRecups = recUnitaDocRecups;
    }

    @OneToMany(mappedBy = "aroUnitaDoc", cascade = {
	    CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    public List<AroIndiceAipUd> getAroIndiceAipUds() {
	return this.aroIndiceAipUds;
    }

    public void setAroIndiceAipUds(List<AroIndiceAipUd> aroIndiceAipUds) {
	this.aroIndiceAipUds = aroIndiceAipUds;
    }

    @OneToMany(mappedBy = "aroUnitaDoc", cascade = {
	    CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    public List<AroIndiceAipUdDaElab> getAroIndiceAipUdDaElabs() {
	return this.aroIndiceAipUdDaElabs;
    }

    public void setAroIndiceAipUdDaElabs(List<AroIndiceAipUdDaElab> aroIndiceAipUdDaElabs) {
	this.aroIndiceAipUdDaElabs = aroIndiceAipUdDaElabs;
    }

    @OneToMany(mappedBy = "aroUnitaDoc")
    public List<SerUdErrFileInput> getSerUdErrFileInputs() {
	return this.serUdErrFileInputs;
    }

    public void setSerUdErrFileInputs(List<SerUdErrFileInput> serUdErrFileInputs) {
	this.serUdErrFileInputs = serUdErrFileInputs;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ELENCO_VERS")
    public ElvElencoVer getElvElencoVer() {
	return this.elvElencoVer;
    }

    public void setElvElencoVer(ElvElencoVer elvElencoVer) {
	this.elvElencoVer = elvElencoVer;
    }

    @OneToMany(mappedBy = "aroUnitaDoc")
    public List<ElvUdVersDaElabElenco> getElvUdVersDaElabElencos() {
	return this.elvUdVersDaElabElencos;
    }

    public void setElvUdVersDaElabElencos(List<ElvUdVersDaElabElenco> elvUdVersDaElabElencos) {
	this.elvUdVersDaElabElencos = elvUdVersDaElabElencos;
    }

    @OneToMany(mappedBy = "aroUnitaDoc")
    public List<AroItemRichAnnulVers> getAroItemRichAnnulVers() {
	return this.aroItemRichAnnulVers;
    }

    public void setAroItemRichAnnulVers(List<AroItemRichAnnulVers> aroItemRichAnnulVers) {
	this.aroItemRichAnnulVers = aroItemRichAnnulVers;
    }

    public AroItemRichAnnulVers addAroItemRichAnnulVer(AroItemRichAnnulVers aroItemRichAnnulVer) {
	getAroItemRichAnnulVers().add(aroItemRichAnnulVer);
	aroItemRichAnnulVer.setAroUnitaDoc(this);
	return aroItemRichAnnulVer;
    }

    public AroItemRichAnnulVers removeAroItemRichAnnulVer(
	    AroItemRichAnnulVers aroItemRichAnnulVer) {
	getAroItemRichAnnulVers().remove(aroItemRichAnnulVer);
	aroItemRichAnnulVer.setAroUnitaDoc(null);
	return aroItemRichAnnulVer;
    }

    @OneToMany(mappedBy = "aroUnitaDoc")
    public List<ElvElencoVersUdAnnul> getElvElencoVersUdAnnuls() {
	return this.elvElencoVersUdAnnuls;
    }

    public void setElvElencoVersUdAnnuls(List<ElvElencoVersUdAnnul> elvElencoVersUdAnnuls) {
	this.elvElencoVersUdAnnuls = elvElencoVersUdAnnuls;
    }

    public ElvElencoVersUdAnnul addElvElencoVersUdAnnul(ElvElencoVersUdAnnul elvElencoVersUdAnnul) {
	getElvElencoVersUdAnnuls().add(elvElencoVersUdAnnul);
	elvElencoVersUdAnnul.setAroUnitaDoc(this);
	return elvElencoVersUdAnnul;
    }

    public ElvElencoVersUdAnnul removeElvElencoVersUdAnnul(
	    ElvElencoVersUdAnnul elvElencoVersUdAnnul) {
	getElvElencoVersUdAnnuls().remove(elvElencoVersUdAnnul);
	elvElencoVersUdAnnul.setAroUnitaDoc(null);
	return elvElencoVersUdAnnul;
    }

    @OneToMany(mappedBy = "aroUnitaDoc")
    public List<VolVolumeVersUdAnnul> getVolVolumeVersUdAnnuls() {
	return this.volVolumeVersUdAnnuls;
    }

    public void setVolVolumeVersUdAnnuls(List<VolVolumeVersUdAnnul> volVolumeVersUdAnnuls) {
	this.volVolumeVersUdAnnuls = volVolumeVersUdAnnuls;
    }

    public VolVolumeVersUdAnnul addVolVolumeVersUdAnnul(VolVolumeVersUdAnnul volVolumeVersUdAnnul) {
	getVolVolumeVersUdAnnuls().add(volVolumeVersUdAnnul);
	volVolumeVersUdAnnul.setAroUnitaDoc(this);
	return volVolumeVersUdAnnul;
    }

    public VolVolumeVersUdAnnul removeVolVolumeVersUdAnnul(
	    VolVolumeVersUdAnnul volVolumeVersUdAnnul) {
	getVolVolumeVersUdAnnuls().remove(volVolumeVersUdAnnul);
	volVolumeVersUdAnnul.setAroUnitaDoc(null);
	return volVolumeVersUdAnnul;
    }

    @OneToMany(mappedBy = "aroUnitaDoc")
    public List<FasUnitaDocFascicolo> getFasUnitaDocFascicolos() {
	return this.fasUnitaDocFascicolos;
    }

    public void setFasUnitaDocFascicolos(List<FasUnitaDocFascicolo> fasUnitaDocFascicolos) {
	this.fasUnitaDocFascicolos = fasUnitaDocFascicolos;
    }

    public FasUnitaDocFascicolo addFasUnitaDocFascicolo(FasUnitaDocFascicolo fasUnitaDocFascicolo) {
	getFasUnitaDocFascicolos().add(fasUnitaDocFascicolo);
	fasUnitaDocFascicolo.setAroUnitaDoc(this);
	return fasUnitaDocFascicolo;
    }

    public FasUnitaDocFascicolo removeFasUnitaDocFascicolo(
	    FasUnitaDocFascicolo fasUnitaDocFascicolo) {
	getFasUnitaDocFascicolos().remove(fasUnitaDocFascicolo);
	fasUnitaDocFascicolo.setAroUnitaDoc(null);
	return fasUnitaDocFascicolo;
    }

    @OneToMany(mappedBy = "aroUnitaDoc")
    public List<AroVersIniUnitaDoc> getAroVersIniUnitaDocs() {
	return this.aroVersIniUnitaDocs;
    }

    public void setAroVersIniUnitaDocs(List<AroVersIniUnitaDoc> aroVersIniUnitaDocs) {
	this.aroVersIniUnitaDocs = aroVersIniUnitaDocs;
    }

    @OneToMany(mappedBy = "aroUnitaDoc")
    public List<AroUpdUnitaDoc> getAroUpdUnitaDocs() {
	return this.aroUpdUnitaDocs;
    }

    public void setAroUpdUnitaDocs(List<AroUpdUnitaDoc> aroUpdUnitaDocs) {
	this.aroUpdUnitaDocs = aroUpdUnitaDocs;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    public AroXmlUnitaDocObjectStorage getAroXmlUnitaDocObjectStorage() {
	return aroXmlUnitaDocObjectStorage;
    }

    public void setAroXmlUnitaDocObjectStorage(
	    AroXmlUnitaDocObjectStorage aroXmlUnitaDocObjectStorage) {
	this.aroXmlUnitaDocObjectStorage = aroXmlUnitaDocObjectStorage;
    }

    @OneToMany(mappedBy = "aroUnitaDoc")
    public List<AroLogStatoConservUd> getAroLogStatoConservUds() {
	return this.aroLogStatoConservUds;
    }

    public void setAroLogStatoConservUds(List<AroLogStatoConservUd> aroLogStatoConservUds) {
	this.aroLogStatoConservUds = aroLogStatoConservUds;
    }
}
