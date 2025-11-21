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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the ELV_ELENCO_VERS_FASC database table.
 */
@Entity
@Table(name = "ELV_ELENCO_VERS_FASC")

public class ElvElencoVersFasc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idElencoVersFasc;

    private BigDecimal aaFascicolo;

    private String dlMotivoChius;

    private String dsUrnElenco;

    private String dsUrnNormalizElenco;

    private Date dtScadChius;

    private String flElencoStandard;

    private BigDecimal idStatoElencoVersFascCor;

    private BigDecimal niFascVersElenco;

    private BigDecimal niMaxFascCrit;

    private BigDecimal niTempoScadChiusCrit;

    private BigDecimal niIndiciAip;

    private String ntElencoChiuso;

    private String ntIndiceElenco;

    private String tiScadChiusCrit;

    private String tiTempoScadChiusCrit;

    private Date tsCreazioneElenco;

    private List<FasFascicolo> fasFascicoli = new ArrayList<>();

    private DecCriterioRaggrFasc decCriterioRaggrFasc;

    private OrgStrut orgStrut;

    private List<ElvElencoVersFascDaElab> elvElencoVersFascDaElabs = new ArrayList<>();

    private List<ElvStatoElencoVersFasc> elvStatoElencoVersFascicoli = new ArrayList<>();

    private List<ElvFileElencoVersFasc> elvFileElencoVersFasc = new ArrayList<>();

    private List<FasVerAipFascicolo> fasVerAipFascicolos = new ArrayList<>();

    private List<FasAipFascicoloDaElab> fasAipFascicoloDaElabs = new ArrayList<>();

    private List<ElvElencoVersFascAnnul> elvElencoVersFascAnnuls = new ArrayList<>();

    public ElvElencoVersFasc() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_ELENCO_VERS_FASC")
    @GenericGenerator(name = "SELV_ELENCO_VERS_FASC_ID_ELENCO_VERS_FASC_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SELV_ELENCO_VERS_FASC"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SELV_ELENCO_VERS_FASC_ID_ELENCO_VERS_FASC_GENERATOR")
    public Long getIdElencoVersFasc() {
        return this.idElencoVersFasc;
    }

    public void setIdElencoVersFasc(Long idElencoVersFasc) {
        this.idElencoVersFasc = idElencoVersFasc;
    }

    @Column(name = "AA_FASCICOLO")
    public BigDecimal getAaFascicolo() {
        return this.aaFascicolo;
    }

    public void setAaFascicolo(BigDecimal aaFascicolo) {
        this.aaFascicolo = aaFascicolo;
    }

    @Column(name = "DL_MOTIVO_CHIUS")
    public String getDlMotivoChius() {
        return this.dlMotivoChius;
    }

    public void setDlMotivoChius(String dlMotivoChius) {
        this.dlMotivoChius = dlMotivoChius;
    }

    @Column(name = "DS_URN_ELENCO")
    public String getDsUrnElenco() {
        return this.dsUrnElenco;
    }

    public void setDsUrnElenco(String dsUrnElenco) {
        this.dsUrnElenco = dsUrnElenco;
    }

    @Column(name = "DS_URN_NORMALIZ_ELENCO")
    public String getDsUrnNormalizElenco() {
        return this.dsUrnNormalizElenco;
    }

    public void setDsUrnNormalizElenco(String dsUrnNormalizElenco) {
        this.dsUrnNormalizElenco = dsUrnNormalizElenco;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SCAD_CHIUS")
    public Date getDtScadChius() {
        return this.dtScadChius;
    }

    public void setDtScadChius(Date dtScadChius) {
        this.dtScadChius = dtScadChius;
    }

    @Column(name = "FL_ELENCO_STANDARD", columnDefinition = "char(1)")
    public String getFlElencoStandard() {
        return this.flElencoStandard;
    }

    public void setFlElencoStandard(String flElencoStandard) {
        this.flElencoStandard = flElencoStandard;
    }

    @Column(name = "ID_STATO_ELENCO_VERS_FASC_COR")
    public BigDecimal getIdStatoElencoVersFascCor() {
        return this.idStatoElencoVersFascCor;
    }

    public void setIdStatoElencoVersFascCor(BigDecimal idStatoElencoVersFascCor) {
        this.idStatoElencoVersFascCor = idStatoElencoVersFascCor;
    }

    @Column(name = "NI_FASC_VERS_ELENCO")
    public BigDecimal getNiFascVersElenco() {
        return this.niFascVersElenco;
    }

    public void setNiFascVersElenco(BigDecimal niFascVersElenco) {
        this.niFascVersElenco = niFascVersElenco;
    }

    @Column(name = "NI_MAX_FASC_CRIT")
    public BigDecimal getNiMaxFascCrit() {
        return this.niMaxFascCrit;
    }

    public void setNiMaxFascCrit(BigDecimal niMaxFascCrit) {
        this.niMaxFascCrit = niMaxFascCrit;
    }

    @Column(name = "NI_TEMPO_SCAD_CHIUS_CRIT")
    public BigDecimal getNiTempoScadChiusCrit() {
        return this.niTempoScadChiusCrit;
    }

    public void setNiTempoScadChiusCrit(BigDecimal niTempoScadChiusCrit) {
        this.niTempoScadChiusCrit = niTempoScadChiusCrit;
    }

    @Column(name = "NI_INDICI_AIP")
    public BigDecimal getNiIndiciAip() {
        return this.niIndiciAip;
    }

    public void setNiIndiciAip(BigDecimal niIndiciAip) {
        this.niIndiciAip = niIndiciAip;
    }

    @Column(name = "NT_ELENCO_CHIUSO")
    public String getNtElencoChiuso() {
        return this.ntElencoChiuso;
    }

    public void setNtElencoChiuso(String ntElencoChiuso) {
        this.ntElencoChiuso = ntElencoChiuso;
    }

    @Column(name = "NT_INDICE_ELENCO")
    public String getNtIndiceElenco() {
        return this.ntIndiceElenco;
    }

    public void setNtIndiceElenco(String ntIndiceElenco) {
        this.ntIndiceElenco = ntIndiceElenco;
    }

    @Column(name = "TI_SCAD_CHIUS_CRIT")
    public String getTiScadChiusCrit() {
        return this.tiScadChiusCrit;
    }

    public void setTiScadChiusCrit(String tiScadChiusCrit) {
        this.tiScadChiusCrit = tiScadChiusCrit;
    }

    @Column(name = "TI_TEMPO_SCAD_CHIUS_CRIT")
    public String getTiTempoScadChiusCrit() {
        return this.tiTempoScadChiusCrit;
    }

    public void setTiTempoScadChiusCrit(String tiTempoScadChiusCrit) {
        this.tiTempoScadChiusCrit = tiTempoScadChiusCrit;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_CREAZIONE_ELENCO")
    public Date getTsCreazioneElenco() {
        return this.tsCreazioneElenco;
    }

    public void setTsCreazioneElenco(Date tsCreazioneElenco) {
        this.tsCreazioneElenco = tsCreazioneElenco;
    }

    @OneToMany(mappedBy = "elvElencoVersFasc")
    public List<FasFascicolo> getFasFascicoli() {
        return this.fasFascicoli;
    }

    public void setFasFascicoli(List<FasFascicolo> fasFascicoli) {
        this.fasFascicoli = fasFascicoli;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CRITERIO_RAGGR_FASC")
    public DecCriterioRaggrFasc getDecCriterioRaggrFasc() {
        return this.decCriterioRaggrFasc;
    }

    public void setDecCriterioRaggrFasc(DecCriterioRaggrFasc decCriterioRaggrFasc) {
        this.decCriterioRaggrFasc = decCriterioRaggrFasc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    public OrgStrut getOrgStrut() {
        return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
        this.orgStrut = orgStrut;
    }

    @OneToMany(mappedBy = "elvElencoVersFasc")
    public List<ElvElencoVersFascDaElab> getElvElencoVersFascDaElabs() {
        return this.elvElencoVersFascDaElabs;
    }

    public void setElvElencoVersFascDaElabs(
            List<ElvElencoVersFascDaElab> elvElencoVersFascDaElabs) {
        this.elvElencoVersFascDaElabs = elvElencoVersFascDaElabs;
    }

    @OneToMany(mappedBy = "elvElencoVersFasc", cascade = CascadeType.PERSIST)
    public List<ElvStatoElencoVersFasc> getElvStatoElencoVersFascicoli() {
        return this.elvStatoElencoVersFascicoli;
    }

    public void setElvStatoElencoVersFascicoli(
            List<ElvStatoElencoVersFasc> elvStatoElencoVersFasc) {
        this.elvStatoElencoVersFascicoli = elvStatoElencoVersFasc;
    }

    @OneToMany(mappedBy = "elvElencoVersFasc", cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
    public List<ElvFileElencoVersFasc> getElvFileElencoVersFasc() {
        return this.elvFileElencoVersFasc;
    }

    public void setElvFileElencoVersFasc(List<ElvFileElencoVersFasc> elvFileElencoVersFasc) {
        this.elvFileElencoVersFasc = elvFileElencoVersFasc;
    }

    public ElvFileElencoVersFasc addElvFileElencoVersFasc(
            ElvFileElencoVersFasc elvFileElencoVersFasc) {
        getElvFileElencoVersFasc().add(elvFileElencoVersFasc);
        elvFileElencoVersFasc.setElvElencoVersFasc(this);
        return elvFileElencoVersFasc;
    }

    public ElvFileElencoVersFasc removeElvFileElencoVersFasc(
            ElvFileElencoVersFasc elvFileElencoVersFasc) {
        getElvFileElencoVersFasc().remove(elvFileElencoVersFasc);
        elvFileElencoVersFasc.setElvElencoVersFasc(null);
        return elvFileElencoVersFasc;
    }

    @OneToMany(mappedBy = "elvElencoVersFasc")
    public List<FasVerAipFascicolo> getFasVerAipFascicolos() {
        return this.fasVerAipFascicolos;
    }

    public void setFasVerAipFascicolos(List<FasVerAipFascicolo> fasVerAipFascicolos) {
        this.fasVerAipFascicolos = fasVerAipFascicolos;
    }

    public FasVerAipFascicolo addFasVerAipFascicolo(FasVerAipFascicolo fasVerAipFascicolo) {
        getFasVerAipFascicolos().add(fasVerAipFascicolo);
        fasVerAipFascicolo.setElvElencoVersFasc(this);
        return fasVerAipFascicolo;
    }

    public FasVerAipFascicolo removeFasVerAipFascicolo(FasVerAipFascicolo fasVerAipFascicolo) {
        getFasVerAipFascicolos().remove(fasVerAipFascicolo);
        fasVerAipFascicolo.setElvElencoVersFasc(null);
        return fasVerAipFascicolo;
    }

    @OneToMany(mappedBy = "elvElencoVersFasc")
    public List<FasAipFascicoloDaElab> getFasAipFascicoloDaElabs() {
        return this.fasAipFascicoloDaElabs;
    }

    public void setFasAipFascicoloDaElabs(List<FasAipFascicoloDaElab> fasAipFascicoloDaElabs) {
        this.fasAipFascicoloDaElabs = fasAipFascicoloDaElabs;
    }

    public FasAipFascicoloDaElab addFasAipFascicoloDaElab(
            FasAipFascicoloDaElab fasAipFascicoloDaElab) {
        getFasAipFascicoloDaElabs().add(fasAipFascicoloDaElab);
        fasAipFascicoloDaElab.setElvElencoVersFasc(this);
        return fasAipFascicoloDaElab;
    }

    public FasAipFascicoloDaElab removeFasAipFascicoloDaElab(
            FasAipFascicoloDaElab fasAipFascicoloDaElab) {
        getFasAipFascicoloDaElabs().remove(fasAipFascicoloDaElab);
        fasAipFascicoloDaElab.setElvElencoVersFasc(null);
        return fasAipFascicoloDaElab;
    }

    @OneToMany(mappedBy = "elvElencoVersFasc")
    public List<ElvElencoVersFascAnnul> getElvElencoVersFascAnnuls() {
        return this.elvElencoVersFascAnnuls;
    }

    public void setElvElencoVersFascAnnuls(List<ElvElencoVersFascAnnul> elvElencoVersFascAnnuls) {
        this.elvElencoVersFascAnnuls = elvElencoVersFascAnnuls;
    }

    public ElvElencoVersFascAnnul addElvElencoVersFascAnnul(
            ElvElencoVersFascAnnul elvElencoVersFascAnnul) {
        getElvElencoVersFascAnnuls().add(elvElencoVersFascAnnul);
        elvElencoVersFascAnnul.setElvElencoVersFasc(this);
        return elvElencoVersFascAnnul;
    }

    public ElvElencoVersFascAnnul removeElvElencoVersFascAnnul(
            ElvElencoVersFascAnnul elvElencoVersFascAnnul) {
        getElvElencoVersFascAnnuls().remove(elvElencoVersFascAnnul);
        elvElencoVersFascAnnul.setElvElencoVersFasc(null);
        return elvElencoVersFascAnnul;
    }
}
