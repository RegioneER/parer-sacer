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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the ORG_AMBIENTE database table.
 */
@Entity
@Cacheable(true)
@Table(name = "ORG_AMBIENTE")
public class OrgAmbiente implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idAmbiente;

    private String dsAmbiente;

    private String nmAmbiente;

    private String dsNote;

    private Date dtFinVal;

    private Date dtIniVal;

    private BigDecimal idEnteConserv;

    private BigDecimal idEnteGestore;

    private List<DecModelloTipoSerie> decModelloTipoSeries = new ArrayList<>();

    private List<OrgEnte> orgEntes = new ArrayList<>();

    private List<AplValoreParamApplic> aplValoreParamApplics = new ArrayList<>();

    private List<AplValParamApplicMulti> aplValParamApplicMultis = new ArrayList<>();

    private List<OrgStoricoEnteAmbiente> orgStoricoEnteAmbientes = new ArrayList<>();

    public OrgAmbiente() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_AMBIENTE")
    @GenericGenerator(name = "SORG_AMBIENTE_ID_AMBIENTE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SORG_AMBIENTE"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SORG_AMBIENTE_ID_AMBIENTE_GENERATOR")
    public Long getIdAmbiente() {
        return this.idAmbiente;
    }

    public void setIdAmbiente(Long idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    @Column(name = "DS_AMBIENTE")
    public String getDsAmbiente() {
        return this.dsAmbiente;
    }

    public void setDsAmbiente(String dsAmbiente) {
        this.dsAmbiente = dsAmbiente;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
        return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    @OneToMany(mappedBy = "orgAmbiente")
    @XmlTransient
    public List<DecModelloTipoSerie> getDecModelloTipoSeries() {
        return this.decModelloTipoSeries;
    }

    public void setDecModelloTipoSeries(List<DecModelloTipoSerie> decModelloTipoSeries) {
        this.decModelloTipoSeries = decModelloTipoSeries;
    }

    public DecModelloTipoSerie addDecModelloTipoSery(DecModelloTipoSerie decModelloTipoSery) {
        getDecModelloTipoSeries().add(decModelloTipoSery);
        decModelloTipoSery.setOrgAmbiente(this);
        return decModelloTipoSery;
    }

    public DecModelloTipoSerie removeDecModelloTipoSery(DecModelloTipoSerie decModelloTipoSery) {
        getDecModelloTipoSeries().remove(decModelloTipoSery);
        decModelloTipoSery.setOrgAmbiente(null);
        return decModelloTipoSery;
    }

    @OneToMany(mappedBy = "orgAmbiente")
    public List<OrgEnte> getOrgEntes() {
        return this.orgEntes;
    }

    public void setOrgEntes(List<OrgEnte> orgEntes) {
        this.orgEntes = orgEntes;
    }

    public OrgEnte addOrgEnte(OrgEnte orgEnte) {
        getOrgEntes().add(orgEnte);
        orgEnte.setOrgAmbiente(this);
        return orgEnte;
    }

    public OrgEnte removeOrgEnte(OrgEnte orgEnte) {
        getOrgEntes().remove(orgEnte);
        orgEnte.setOrgAmbiente(null);
        return orgEnte;
    }

    @Column(name = "DS_NOTE")
    public String getDsNote() {
        return this.dsNote;
    }

    public void setDsNote(String dsNote) {
        this.dsNote = dsNote;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIN_VAL")
    public Date getDtFinVal() {
        return this.dtFinVal;
    }

    public void setDtFinVal(Date dtFinVal) {
        this.dtFinVal = dtFinVal;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL")
    public Date getDtIniVal() {
        return this.dtIniVal;
    }

    public void setDtIniVal(Date dtIniVal) {
        this.dtIniVal = dtIniVal;
    }

    @Column(name = "ID_ENTE_CONSERV")
    public java.math.BigDecimal getIdEnteConserv() {
        return this.idEnteConserv;
    }

    public void setIdEnteConserv(java.math.BigDecimal idEnteConserv) {
        this.idEnteConserv = idEnteConserv;
    }

    @Column(name = "ID_ENTE_GESTORE")
    public java.math.BigDecimal getIdEnteGestore() {
        return this.idEnteGestore;
    }

    public void setIdEnteGestore(java.math.BigDecimal idEnteGestore) {
        this.idEnteGestore = idEnteGestore;
    }

    @OneToMany(mappedBy = "orgAmbiente", cascade = { CascadeType.REMOVE })
    public List<AplValoreParamApplic> getAplValoreParamApplics() {
        return this.aplValoreParamApplics;
    }

    public void setAplValoreParamApplics(List<AplValoreParamApplic> aplValoreParamApplics) {
        this.aplValoreParamApplics = aplValoreParamApplics;
    }

    public AplValoreParamApplic addAplValoreParamApplic(AplValoreParamApplic aplValoreParamApplic) {
        getAplValoreParamApplics().add(aplValoreParamApplic);
        aplValoreParamApplic.setOrgAmbiente(this);
        return aplValoreParamApplic;
    }

    public AplValoreParamApplic removeAplValoreParamApplic(AplValoreParamApplic aplValoreParamApplic) {
        getAplValoreParamApplics().remove(aplValoreParamApplic);
        aplValoreParamApplic.setOrgAmbiente(null);
        return aplValoreParamApplic;
    }

    @OneToMany(mappedBy = "orgAmbiente", cascade = { CascadeType.REMOVE })
    public List<AplValParamApplicMulti> getAplValParamApplicMultis() {
        return this.aplValParamApplicMultis;
    }

    public void setAplValParamApplicMultis(List<AplValParamApplicMulti> aplValParamApplicMultis) {
        this.aplValParamApplicMultis = aplValParamApplicMultis;
    }

    public AplValParamApplicMulti addAplValParamApplicMulti(AplValParamApplicMulti aplValParamApplicMulti) {
        getAplValParamApplicMultis().add(aplValParamApplicMulti);
        aplValParamApplicMulti.setOrgAmbiente(this);
        return aplValParamApplicMulti;
    }

    public AplValParamApplicMulti removeAplValParamApplicMulti(AplValParamApplicMulti aplValParamApplicMulti) {
        getAplValParamApplicMultis().remove(aplValParamApplicMulti);
        aplValParamApplicMulti.setOrgAmbiente(null);
        return aplValParamApplicMulti;
    }

    @OneToMany(mappedBy = "orgAmbiente")
    public List<OrgStoricoEnteAmbiente> getOrgStoricoEnteAmbientes() {
        return this.orgStoricoEnteAmbientes;
    }

    public void setOrgStoricoEnteAmbientes(List<OrgStoricoEnteAmbiente> orgStoricoEnteAmbientes) {
        this.orgStoricoEnteAmbientes = orgStoricoEnteAmbientes;
    }

    public OrgStoricoEnteAmbiente addOrgStoricoEnteAmbiente(OrgStoricoEnteAmbiente orgStoricoEnteAmbiente) {
        getOrgStoricoEnteAmbientes().add(orgStoricoEnteAmbiente);
        orgStoricoEnteAmbiente.setOrgAmbiente(this);
        return orgStoricoEnteAmbiente;
    }

    public OrgStoricoEnteAmbiente removeOrgStoricoEnteAmbiente(OrgStoricoEnteAmbiente orgStoricoEnteAmbiente) {
        getOrgStoricoEnteAmbientes().remove(orgStoricoEnteAmbiente);
        orgStoricoEnteAmbiente.setOrgAmbiente(null);
        return orgStoricoEnteAmbiente;
    }
}
