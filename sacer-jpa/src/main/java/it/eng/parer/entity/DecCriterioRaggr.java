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

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import it.eng.parer.entity.constraint.DecCriterioRaggr.TiModValidElencoCriterio;
import it.eng.parer.entity.constraint.DecCriterioRaggr.TiValidElencoCriterio;

/**
 * The persistent class for the DEC_CRITERIO_RAGGR database table.
 */
@Entity
@Cacheable(true)
@Table(name = "DEC_CRITERIO_RAGGR")

public class DecCriterioRaggr implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idCriterioRaggr;

    private BigDecimal aaKeyUnitaDoc;

    private BigDecimal aaKeyUnitaDocA;

    private BigDecimal aaKeyUnitaDocDa;

    private String blFiltriDatiSpecDoc;

    private String cdKeyUnitaDoc;

    private String cdKeyUnitaDocA;

    private String cdKeyUnitaDocDa;

    private String dlDoc;

    private String dlOggettoUnitaDoc;

    private String dsAutoreDoc;

    private String dsCriterioRaggr;

    private Date dtCreazioneUnitaDocA;

    private Date dtCreazioneUnitaDocDa;

    private Date dtIstituz;

    private Date dtRegUnitaDocA;

    private Date dtRegUnitaDocDa;

    private Date dtSoppres;

    private String flCriterioRaggrFisc;

    private String flCriterioRaggrStandard;

    private String flFiltroRangeRegistroKey;

    private String flFiltroRegistroKey;

    private String flFiltroSistemaMigraz;

    private String flFiltroTiEsitoVerifFirme;

    private String flFiltroTipoDoc;

    private String flFiltroTipoUnitaDoc;

    private String flForzaAccettazione;

    private String flForzaConservazione;

    private String flUnitaDocFirmato;

    private BigDecimal niMaxComp;

    private BigDecimal niTempoScadChius;

    private String nmCriterioRaggr;

    private String ntCriterioRaggr;

    private String tiConservazione;

    private String tiGestElencoCriterio;

    private String tiScadChiusVolume;

    private String tiTempoScadChius;

    private TiValidElencoCriterio tiValidElenco;

    private TiModValidElencoCriterio tiModValidElenco;

    private BigDecimal niMaxElenchiByGg;

    private List<DecCriterioFiltroMultiplo> decCriterioFiltroMultiplos = new ArrayList<>();

    private OrgStrut orgStrut;

    private List<VolVolumeConserv> volVolumeConservs = new ArrayList<>();

    private List<ElvElencoVer> elvElencoVers = new ArrayList<>();

    public DecCriterioRaggr() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_CRITERIO_RAGGR")
    @GenericGenerator(name = "SDEC_CRITERIO_RAGGR_ID_CRITERIO_RAGGR_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_CRITERIO_RAGGR"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_CRITERIO_RAGGR_ID_CRITERIO_RAGGR_GENERATOR")
    public Long getIdCriterioRaggr() {
	return this.idCriterioRaggr;
    }

    public void setIdCriterioRaggr(Long idCriterioRaggr) {
	this.idCriterioRaggr = idCriterioRaggr;
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
	return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
	this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Column(name = "AA_KEY_UNITA_DOC_A")
    public BigDecimal getAaKeyUnitaDocA() {
	return this.aaKeyUnitaDocA;
    }

    public void setAaKeyUnitaDocA(BigDecimal aaKeyUnitaDocA) {
	this.aaKeyUnitaDocA = aaKeyUnitaDocA;
    }

    @Column(name = "AA_KEY_UNITA_DOC_DA")
    public BigDecimal getAaKeyUnitaDocDa() {
	return this.aaKeyUnitaDocDa;
    }

    public void setAaKeyUnitaDocDa(BigDecimal aaKeyUnitaDocDa) {
	this.aaKeyUnitaDocDa = aaKeyUnitaDocDa;
    }

    @Lob()
    @Column(name = "BL_FILTRI_DATI_SPEC_DOC")
    public String getBlFiltriDatiSpecDoc() {
	return this.blFiltriDatiSpecDoc;
    }

    public void setBlFiltriDatiSpecDoc(String blFiltriDatiSpecDoc) {
	this.blFiltriDatiSpecDoc = blFiltriDatiSpecDoc;
    }

    @Column(name = "CD_KEY_UNITA_DOC")
    public String getCdKeyUnitaDoc() {
	return this.cdKeyUnitaDoc;
    }

    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
	this.cdKeyUnitaDoc = cdKeyUnitaDoc;
    }

    @Column(name = "CD_KEY_UNITA_DOC_A")
    public String getCdKeyUnitaDocA() {
	return this.cdKeyUnitaDocA;
    }

    public void setCdKeyUnitaDocA(String cdKeyUnitaDocA) {
	this.cdKeyUnitaDocA = cdKeyUnitaDocA;
    }

    @Column(name = "CD_KEY_UNITA_DOC_DA")
    public String getCdKeyUnitaDocDa() {
	return this.cdKeyUnitaDocDa;
    }

    public void setCdKeyUnitaDocDa(String cdKeyUnitaDocDa) {
	this.cdKeyUnitaDocDa = cdKeyUnitaDocDa;
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

    @Column(name = "DS_AUTORE_DOC")
    public String getDsAutoreDoc() {
	return this.dsAutoreDoc;
    }

    public void setDsAutoreDoc(String dsAutoreDoc) {
	this.dsAutoreDoc = dsAutoreDoc;
    }

    @Column(name = "DS_CRITERIO_RAGGR")
    public String getDsCriterioRaggr() {
	return this.dsCriterioRaggr;
    }

    public void setDsCriterioRaggr(String dsCriterioRaggr) {
	this.dsCriterioRaggr = dsCriterioRaggr;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE_UNITA_DOC_A")
    public Date getDtCreazioneUnitaDocA() {
	return this.dtCreazioneUnitaDocA;
    }

    public void setDtCreazioneUnitaDocA(Date dtCreazioneUnitaDocA) {
	this.dtCreazioneUnitaDocA = dtCreazioneUnitaDocA;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE_UNITA_DOC_DA")
    public Date getDtCreazioneUnitaDocDa() {
	return this.dtCreazioneUnitaDocDa;
    }

    public void setDtCreazioneUnitaDocDa(Date dtCreazioneUnitaDocDa) {
	this.dtCreazioneUnitaDocDa = dtCreazioneUnitaDocDa;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ISTITUZ")
    public Date getDtIstituz() {
	return this.dtIstituz;
    }

    public void setDtIstituz(Date dtIstituz) {
	this.dtIstituz = dtIstituz;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_UNITA_DOC_A")
    public Date getDtRegUnitaDocA() {
	return this.dtRegUnitaDocA;
    }

    public void setDtRegUnitaDocA(Date dtRegUnitaDocA) {
	this.dtRegUnitaDocA = dtRegUnitaDocA;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_UNITA_DOC_DA")
    public Date getDtRegUnitaDocDa() {
	return this.dtRegUnitaDocDa;
    }

    public void setDtRegUnitaDocDa(Date dtRegUnitaDocDa) {
	this.dtRegUnitaDocDa = dtRegUnitaDocDa;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SOPPRES")
    public Date getDtSoppres() {
	return this.dtSoppres;
    }

    public void setDtSoppres(Date dtSoppres) {
	this.dtSoppres = dtSoppres;
    }

    @Column(name = "FL_CRITERIO_RAGGR_FISC", columnDefinition = "char(1)")
    public String getFlCriterioRaggrFisc() {
	return this.flCriterioRaggrFisc;
    }

    public void setFlCriterioRaggrFisc(String flCriterioRaggrFisc) {
	this.flCriterioRaggrFisc = flCriterioRaggrFisc;
    }

    @Column(name = "FL_CRITERIO_RAGGR_STANDARD", columnDefinition = "char(1)")
    public String getFlCriterioRaggrStandard() {
	return flCriterioRaggrStandard;
    }

    public void setFlCriterioRaggrStandard(String flCriterioRaggrStandard) {
	this.flCriterioRaggrStandard = flCriterioRaggrStandard;
    }

    @Column(name = "FL_FILTRO_RANGE_REGISTRO_KEY", columnDefinition = "char(1)")
    public String getFlFiltroRangeRegistroKey() {
	return this.flFiltroRangeRegistroKey;
    }

    public void setFlFiltroRangeRegistroKey(String flFiltroRangeRegistroKey) {
	this.flFiltroRangeRegistroKey = flFiltroRangeRegistroKey;
    }

    @Column(name = "FL_FILTRO_REGISTRO_KEY", columnDefinition = "char(1)")
    public String getFlFiltroRegistroKey() {
	return this.flFiltroRegistroKey;
    }

    public void setFlFiltroRegistroKey(String flFiltroRegistroKey) {
	this.flFiltroRegistroKey = flFiltroRegistroKey;
    }

    @Column(name = "FL_FILTRO_SISTEMA_MIGRAZ", columnDefinition = "char(1)")
    public String getFlFiltroSistemaMigraz() {
	return this.flFiltroSistemaMigraz;
    }

    public void setFlFiltroSistemaMigraz(String flFiltroSistemaMigraz) {
	this.flFiltroSistemaMigraz = flFiltroSistemaMigraz;
    }

    @Column(name = "FL_FILTRO_TI_ESITO_VERIF_FIRME", columnDefinition = "char(1)")
    public String getFlFiltroTiEsitoVerifFirme() {
	return this.flFiltroTiEsitoVerifFirme;
    }

    public void setFlFiltroTiEsitoVerifFirme(String flFiltroTiEsitoVerifFirme) {
	this.flFiltroTiEsitoVerifFirme = flFiltroTiEsitoVerifFirme;
    }

    @Column(name = "FL_FILTRO_TIPO_DOC", columnDefinition = "char(1)")
    public String getFlFiltroTipoDoc() {
	return this.flFiltroTipoDoc;
    }

    public void setFlFiltroTipoDoc(String flFiltroTipoDoc) {
	this.flFiltroTipoDoc = flFiltroTipoDoc;
    }

    @Column(name = "FL_FILTRO_TIPO_UNITA_DOC", columnDefinition = "char(1)")
    public String getFlFiltroTipoUnitaDoc() {
	return this.flFiltroTipoUnitaDoc;
    }

    public void setFlFiltroTipoUnitaDoc(String flFiltroTipoUnitaDoc) {
	this.flFiltroTipoUnitaDoc = flFiltroTipoUnitaDoc;
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

    @Column(name = "FL_UNITA_DOC_FIRMATO", columnDefinition = "char(1)")
    public String getFlUnitaDocFirmato() {
	return this.flUnitaDocFirmato;
    }

    public void setFlUnitaDocFirmato(String flUnitaDocFirmato) {
	this.flUnitaDocFirmato = flUnitaDocFirmato;
    }

    @Column(name = "NI_MAX_COMP")
    public BigDecimal getNiMaxComp() {
	return this.niMaxComp;
    }

    public void setNiMaxComp(BigDecimal niMaxComp) {
	this.niMaxComp = niMaxComp;
    }

    @Column(name = "NI_TEMPO_SCAD_CHIUS")
    public BigDecimal getNiTempoScadChius() {
	return this.niTempoScadChius;
    }

    public void setNiTempoScadChius(BigDecimal niTempoScadChius) {
	this.niTempoScadChius = niTempoScadChius;
    }

    @Column(name = "NM_CRITERIO_RAGGR")
    public String getNmCriterioRaggr() {
	return this.nmCriterioRaggr;
    }

    public void setNmCriterioRaggr(String nmCriterioRaggr) {
	this.nmCriterioRaggr = nmCriterioRaggr;
    }

    @Column(name = "NT_CRITERIO_RAGGR")
    public String getNtCriterioRaggr() {
	return this.ntCriterioRaggr;
    }

    public void setNtCriterioRaggr(String ntCriterioRaggr) {
	this.ntCriterioRaggr = ntCriterioRaggr;
    }

    @Column(name = "TI_CONSERVAZIONE")
    public String getTiConservazione() {
	return this.tiConservazione;
    }

    public void setTiConservazione(String tiConservazione) {
	this.tiConservazione = tiConservazione;
    }

    @Column(name = "TI_GEST_ELENCO_CRITERIO")
    public String getTiGestElencoCriterio() {
	return this.tiGestElencoCriterio;
    }

    public void setTiGestElencoCriterio(String tiGestElencoCriterio) {
	this.tiGestElencoCriterio = tiGestElencoCriterio;
    }

    @Column(name = "TI_SCAD_CHIUS_VOLUME")
    public String getTiScadChiusVolume() {
	return this.tiScadChiusVolume;
    }

    public void setTiScadChiusVolume(String tiScadChiusVolume) {
	this.tiScadChiusVolume = tiScadChiusVolume;
    }

    @Column(name = "TI_TEMPO_SCAD_CHIUS")
    public String getTiTempoScadChius() {
	return this.tiTempoScadChius;
    }

    public void setTiTempoScadChius(String tiTempoScadChius) {
	this.tiTempoScadChius = tiTempoScadChius;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_VALID_ELENCO")
    public TiValidElencoCriterio getTiValidElenco() {
	return this.tiValidElenco;
    }

    public void setTiValidElenco(TiValidElencoCriterio tiValidElencoCriterio) {
	this.tiValidElenco = tiValidElencoCriterio;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_MOD_VALID_ELENCO")
    public TiModValidElencoCriterio getTiModValidElenco() {
	return this.tiModValidElenco;
    }

    public void setTiModValidElenco(TiModValidElencoCriterio tiModValidElencoCriterio) {
	this.tiModValidElenco = tiModValidElencoCriterio;
    }

    @Column(name = "NI_MAX_ELENCHI_BY_GG")
    public BigDecimal getNiMaxElenchiByGg() {
	return this.niMaxElenchiByGg;
    }

    public void setNiMaxElenchiByGg(BigDecimal niMaxElenchiByGg) {
	this.niMaxElenchiByGg = niMaxElenchiByGg;
    }

    @OneToMany(mappedBy = "decCriterioRaggr", cascade = {
	    CascadeType.PERSIST, CascadeType.REMOVE })
    public List<DecCriterioFiltroMultiplo> getDecCriterioFiltroMultiplos() {
	return this.decCriterioFiltroMultiplos;
    }

    public void setDecCriterioFiltroMultiplos(
	    List<DecCriterioFiltroMultiplo> decCriterioFiltroMultiplos) {
	this.decCriterioFiltroMultiplos = decCriterioFiltroMultiplos;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    @XmlInverseReference(mappedBy = "decCriterioRaggrs")
    public OrgStrut getOrgStrut() {
	return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
	this.orgStrut = orgStrut;
    }

    @OneToMany(mappedBy = "decCriterioRaggr")
    @XmlTransient
    public List<VolVolumeConserv> getVolVolumeConservs() {
	return this.volVolumeConservs;
    }

    public void setVolVolumeConservs(List<VolVolumeConserv> volVolumeConservs) {
	this.volVolumeConservs = volVolumeConservs;
    }

    @OneToMany(mappedBy = "decCriterioRaggr")
    @XmlTransient
    public List<ElvElencoVer> getElvElencoVers() {
	return this.elvElencoVers;
    }

    public void setElvElencoVers(List<ElvElencoVer> elvElencoVers) {
	this.elvElencoVers = elvElencoVers;
    }
}
