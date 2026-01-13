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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the DEC_CRITERIO_RAGGR_FASC database table.
 */
@Entity
@Table(name = "DEC_CRITERIO_RAGGR_FASC")

public class DecCriterioRaggrFasc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idCriterioRaggrFasc;

    private BigDecimal aaFascicolo;

    private BigDecimal aaFascicoloA;

    private BigDecimal aaFascicoloDa;

    private String dsCriterioRaggr;

    private String dsOggettoFascicolo;

    private Date dtApeFascicoloA;

    private Date dtApeFascicoloDa;

    private Date dtChiuFascicoloA;

    private Date dtChiuFascicoloDa;

    private Date dtIstituz;

    private Date dtSoppres;

    private Date dtVersA;

    private Date dtVersDa;

    private String flCriterioRaggrStandard;

    private String flFiltroSistemaMigraz;

    private String flFiltroTipoFascicolo;

    private String flFiltroVoceTitol;

    private BigDecimal niMaxFasc;

    private BigDecimal niTempoScadChius;

    private String nmCriterioRaggr;

    private String ntCriterioRaggr;

    private String tiConservazione;

    private String tiScadChius;

    private String tiTempoScadChius;

    private OrgStrut orgStrut;

    private List<DecSelCriterioRaggrFasc> decSelCriterioRaggrFascicoli = new ArrayList<>();

    private List<ElvElencoVersFasc> elvElencoVersFasc = new ArrayList<>();

    public DecCriterioRaggrFasc() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_CRITERIO_RAGGR_FASC")
    @GenericGenerator(name = "SDEC_CRITERIO_RAGGR_FASC_ID_CRITERIO_RAGGR_FASC_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_CRITERIO_RAGGR_FASC"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_CRITERIO_RAGGR_FASC_ID_CRITERIO_RAGGR_FASC_GENERATOR")
    public Long getIdCriterioRaggrFasc() {
        return this.idCriterioRaggrFasc;
    }

    public void setIdCriterioRaggrFasc(Long idCriterioRaggrFasc) {
        this.idCriterioRaggrFasc = idCriterioRaggrFasc;
    }

    @Column(name = "AA_FASCICOLO")
    public BigDecimal getAaFascicolo() {
        return this.aaFascicolo;
    }

    public void setAaFascicolo(BigDecimal aaFascicolo) {
        this.aaFascicolo = aaFascicolo;
    }

    @Column(name = "AA_FASCICOLO_A")
    public BigDecimal getAaFascicoloA() {
        return this.aaFascicoloA;
    }

    public void setAaFascicoloA(BigDecimal aaFascicoloA) {
        this.aaFascicoloA = aaFascicoloA;
    }

    @Column(name = "AA_FASCICOLO_DA")
    public BigDecimal getAaFascicoloDa() {
        return this.aaFascicoloDa;
    }

    public void setAaFascicoloDa(BigDecimal aaFascicoloDa) {
        this.aaFascicoloDa = aaFascicoloDa;
    }

    @Column(name = "DS_CRITERIO_RAGGR")
    public String getDsCriterioRaggr() {
        return this.dsCriterioRaggr;
    }

    public void setDsCriterioRaggr(String dsCriterioRaggr) {
        this.dsCriterioRaggr = dsCriterioRaggr;
    }

    @Column(name = "DS_OGGETTO_FASCICOLO")
    public String getDsOggettoFascicolo() {
        return this.dsOggettoFascicolo;
    }

    public void setDsOggettoFascicolo(String dsOggettoFascicolo) {
        this.dsOggettoFascicolo = dsOggettoFascicolo;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_APE_FASCICOLO_A")
    public Date getDtApeFascicoloA() {
        return this.dtApeFascicoloA;
    }

    public void setDtApeFascicoloA(Date dtApeFascicoloA) {
        this.dtApeFascicoloA = dtApeFascicoloA;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_APE_FASCICOLO_DA")
    public Date getDtApeFascicoloDa() {
        return this.dtApeFascicoloDa;
    }

    public void setDtApeFascicoloDa(Date dtApeFascicoloDa) {
        this.dtApeFascicoloDa = dtApeFascicoloDa;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CHIU_FASCICOLO_A")
    public Date getDtChiuFascicoloA() {
        return this.dtChiuFascicoloA;
    }

    public void setDtChiuFascicoloA(Date dtChiuFascicoloA) {
        this.dtChiuFascicoloA = dtChiuFascicoloA;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CHIU_FASCICOLO_DA")
    public Date getDtChiuFascicoloDa() {
        return this.dtChiuFascicoloDa;
    }

    public void setDtChiuFascicoloDa(Date dtChiuFascicoloDa) {
        this.dtChiuFascicoloDa = dtChiuFascicoloDa;
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
    @Column(name = "DT_SOPPRES")
    public Date getDtSoppres() {
        return this.dtSoppres;
    }

    public void setDtSoppres(Date dtSoppres) {
        this.dtSoppres = dtSoppres;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_VERS_A")
    public Date getDtVersA() {
        return this.dtVersA;
    }

    public void setDtVersA(Date dtVersA) {
        this.dtVersA = dtVersA;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_VERS_DA")
    public Date getDtVersDa() {
        return this.dtVersDa;
    }

    public void setDtVersDa(Date dtVersDa) {
        this.dtVersDa = dtVersDa;
    }

    @Column(name = "FL_CRITERIO_RAGGR_STANDARD", columnDefinition = "char(1)")
    public String getFlCriterioRaggrStandard() {
        return this.flCriterioRaggrStandard;
    }

    public void setFlCriterioRaggrStandard(String flCriterioRaggrStandard) {
        this.flCriterioRaggrStandard = flCriterioRaggrStandard;
    }

    @Column(name = "FL_FILTRO_SISTEMA_MIGRAZ", columnDefinition = "char(1)")
    public String getFlFiltroSistemaMigraz() {
        return this.flFiltroSistemaMigraz;
    }

    public void setFlFiltroSistemaMigraz(String flFiltroSistemaMigraz) {
        this.flFiltroSistemaMigraz = flFiltroSistemaMigraz;
    }

    @Column(name = "FL_FILTRO_TIPO_FASCICOLO", columnDefinition = "char(1)")
    public String getFlFiltroTipoFascicolo() {
        return this.flFiltroTipoFascicolo;
    }

    public void setFlFiltroTipoFascicolo(String flFiltroTipoFascicolo) {
        this.flFiltroTipoFascicolo = flFiltroTipoFascicolo;
    }

    @Column(name = "FL_FILTRO_VOCE_TITOL", columnDefinition = "char(1)")
    public String getFlFiltroVoceTitol() {
        return this.flFiltroVoceTitol;
    }

    public void setFlFiltroVoceTitol(String flFiltroVoceTitol) {
        this.flFiltroVoceTitol = flFiltroVoceTitol;
    }

    @Column(name = "NI_MAX_FASC")
    public BigDecimal getNiMaxFasc() {
        return this.niMaxFasc;
    }

    public void setNiMaxFasc(BigDecimal niMaxFasc) {
        this.niMaxFasc = niMaxFasc;
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

    @Column(name = "TI_SCAD_CHIUS")
    public String getTiScadChius() {
        return this.tiScadChius;
    }

    public void setTiScadChius(String tiScadChius) {
        this.tiScadChius = tiScadChius;
    }

    @Column(name = "TI_TEMPO_SCAD_CHIUS")
    public String getTiTempoScadChius() {
        return this.tiTempoScadChius;
    }

    public void setTiTempoScadChius(String tiTempoScadChius) {
        this.tiTempoScadChius = tiTempoScadChius;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    @XmlTransient
    public OrgStrut getOrgStrut() {
        return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
        this.orgStrut = orgStrut;
    }

    @OneToMany(mappedBy = "decCriterioRaggrFasc", cascade = {
            CascadeType.PERSIST, CascadeType.REMOVE })
    @XmlTransient
    public List<DecSelCriterioRaggrFasc> getDecSelCriterioRaggrFascicoli() {
        return this.decSelCriterioRaggrFascicoli;
    }

    public void setDecSelCriterioRaggrFascicoli(
            List<DecSelCriterioRaggrFasc> decSelCriterioRaggrFascicoli) {
        this.decSelCriterioRaggrFascicoli = decSelCriterioRaggrFascicoli;
    }

    @OneToMany(mappedBy = "decCriterioRaggrFasc")
    @XmlTransient
    public List<ElvElencoVersFasc> getElvElencoVersFasc() {
        return this.elvElencoVersFasc;
    }

    public void setElvElencoVersFasc(List<ElvElencoVersFasc> elvElencoVersFasc) {
        this.elvElencoVersFasc = elvElencoVersFasc;
    }
}
