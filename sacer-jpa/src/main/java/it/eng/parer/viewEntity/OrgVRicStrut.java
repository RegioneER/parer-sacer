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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Table;

/**
 * The persistent class for the ORG_V_RIC_STRUT database table.
 *
 */
@Entity
@Table(name = "ORG_V_RIC_STRUT")

public class OrgVRicStrut implements Serializable {

    private static final long serialVersionUID = 1L;
    private String cdCategEnte;
    private String dsStrut;
    private String dsTreeCdAmbitoTerrit;
    private String dsTreeIdAmbitoTerrit;
    private String flParametriStrut;
    private String flParametriTipoUd;
    private String flPartOk;
    private String flTemplate;
    private BigDecimal idAmbiente;
    private BigDecimal idAmbienteEnteConvenz;
    private BigDecimal idAmbitoTerrit;
    private BigDecimal idCategEnte;
    private BigDecimal idEnte;
    private BigDecimal idEnteConvenz;
    private String nmAmbiente;
    private String nmAmbienteEnteConvenz;
    private String nmEnte;
    private String nmEnteConvenz;
    private String nmSistemaVersante;
    private String nmStrut;
    private String tiAmbitoTerrit;
    private OrgVRicStrutId orgVRicStrutId;

    public OrgVRicStrut() {/* Hibernate */
    }

    public OrgVRicStrut(BigDecimal idAmbiente, String nmAmbiente, BigDecimal idEnte, String nmEnte,
	    BigDecimal idStrut, String nmStrut, String dsStrut, String flTemplate, String flPartOk,
	    BigDecimal idAmbitoTerrit, String tiAmbitoTerrit, String dsTreeCdAmbitoTerrit,
	    String dsTreeIdAmbitoTerrit, BigDecimal idCategEnte, String cdCategEnte,
	    String flParametriStrut, String flParametriTipoUd) {
	this.idAmbiente = idAmbiente;
	this.nmAmbiente = nmAmbiente;
	this.idEnte = idEnte;
	this.nmEnte = nmEnte;
	this.orgVRicStrutId = new OrgVRicStrutId();
	orgVRicStrutId.setIdStrut(idStrut);
	this.nmStrut = nmStrut;
	this.dsStrut = dsStrut;
	this.flTemplate = flTemplate;
	this.flPartOk = flPartOk;
	this.idAmbitoTerrit = idAmbitoTerrit;
	this.tiAmbitoTerrit = tiAmbitoTerrit;
	this.dsTreeCdAmbitoTerrit = dsTreeCdAmbitoTerrit;
	this.dsTreeIdAmbitoTerrit = dsTreeIdAmbitoTerrit;
	this.idCategEnte = idCategEnte;
	this.cdCategEnte = cdCategEnte;
	this.flParametriStrut = flParametriStrut;
	this.flParametriTipoUd = flParametriTipoUd;
    }

    @Column(name = "CD_CATEG_ENTE")
    public String getCdCategEnte() {
	return this.cdCategEnte;
    }

    public void setCdCategEnte(String cdCategEnte) {
	this.cdCategEnte = cdCategEnte;
    }

    @Column(name = "DS_STRUT")
    public String getDsStrut() {
	return this.dsStrut;
    }

    public void setDsStrut(String dsStrut) {
	this.dsStrut = dsStrut;
    }

    @Column(name = "DS_TREE_CD_AMBITO_TERRIT")
    public String getDsTreeCdAmbitoTerrit() {
	return this.dsTreeCdAmbitoTerrit;
    }

    public void setDsTreeCdAmbitoTerrit(String dsTreeCdAmbitoTerrit) {
	this.dsTreeCdAmbitoTerrit = dsTreeCdAmbitoTerrit;
    }

    @Column(name = "DS_TREE_ID_AMBITO_TERRIT")
    public String getDsTreeIdAmbitoTerrit() {
	return this.dsTreeIdAmbitoTerrit;
    }

    public void setDsTreeIdAmbitoTerrit(String dsTreeIdAmbitoTerrit) {
	this.dsTreeIdAmbitoTerrit = dsTreeIdAmbitoTerrit;
    }

    @Column(name = "FL_PARAMETRI_STRUT", columnDefinition = "char(1)")
    public String getFlParametriStrut() {
	return this.flParametriStrut;
    }

    public void setFlParametriStrut(String flParametriStrut) {
	this.flParametriStrut = flParametriStrut;
    }

    @Column(name = "FL_PARAMETRI_TIPO_UD", columnDefinition = "char(1)")
    public String getFlParametriTipoUd() {
	return this.flParametriTipoUd;
    }

    public void setFlParametriTipoUd(String flParametriTipoUd) {
	this.flParametriTipoUd = flParametriTipoUd;
    }

    @Column(name = "FL_PART_OK", columnDefinition = "char(1)")
    public String getFlPartOk() {
	return this.flPartOk;
    }

    public void setFlPartOk(String flPartOk) {
	this.flPartOk = flPartOk;
    }

    @Column(name = "FL_TEMPLATE", columnDefinition = "char(1)")
    public String getFlTemplate() {
	return this.flTemplate;
    }

    public void setFlTemplate(String flTemplate) {
	this.flTemplate = flTemplate;
    }

    @Column(name = "ID_AMBIENTE")
    public BigDecimal getIdAmbiente() {
	return this.idAmbiente;
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
	this.idAmbiente = idAmbiente;
    }

    @Column(name = "ID_AMBITO_TERRIT")
    public BigDecimal getIdAmbitoTerrit() {
	return this.idAmbitoTerrit;
    }

    public void setIdAmbitoTerrit(BigDecimal idAmbitoTerrit) {
	this.idAmbitoTerrit = idAmbitoTerrit;
    }

    @Column(name = "ID_CATEG_ENTE")
    public BigDecimal getIdCategEnte() {
	return this.idCategEnte;
    }

    public void setIdCategEnte(BigDecimal idCategEnte) {
	this.idCategEnte = idCategEnte;
    }

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
	return this.idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
	this.idEnte = idEnte;
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

    @Column(name = "NM_SISTEMA_VERSANTE")
    public String getNmSistemaVersante() {
	return this.nmSistemaVersante;
    }

    public void setNmSistemaVersante(String nmSistemaVersante) {
	this.nmSistemaVersante = nmSistemaVersante;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
	return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
	this.nmStrut = nmStrut;
    }

    @Column(name = "TI_AMBITO_TERRIT")
    public String getTiAmbitoTerrit() {
	return this.tiAmbitoTerrit;
    }

    public void setTiAmbitoTerrit(String tiAmbitoTerrit) {
	this.tiAmbitoTerrit = tiAmbitoTerrit;
    }

    @Column(name = "ID_AMBIENTE_ENTE_CONVENZ")
    public BigDecimal getIdAmbienteEnteConvenz() {
	return idAmbienteEnteConvenz;
    }

    public void setIdAmbienteEnteConvenz(BigDecimal idAmbienteEnteConvenz) {
	this.idAmbienteEnteConvenz = idAmbienteEnteConvenz;
    }

    @Column(name = "ID_ENTE_CONVENZ")
    public BigDecimal getIdEnteConvenz() {
	return idEnteConvenz;
    }

    public void setIdEnteConvenz(BigDecimal idEnteConvenz) {
	this.idEnteConvenz = idEnteConvenz;
    }

    @Column(name = "NM_AMBIENTE_ENTE_CONVENZ")
    public String getNmAmbienteEnteConvenz() {
	return nmAmbienteEnteConvenz;
    }

    public void setNmAmbienteEnteConvenz(String nmAmbienteEnteConvenz) {
	this.nmAmbienteEnteConvenz = nmAmbienteEnteConvenz;
    }

    @Column(name = "NM_ENTE_CONVENZ")
    public String getNmEnteConvenz() {
	return nmEnteConvenz;
    }

    public void setNmEnteConvenz(String nmEnteConvenz) {
	this.nmEnteConvenz = nmEnteConvenz;
    }

    @EmbeddedId
    public OrgVRicStrutId getOrgVRicStrutId() {
	return orgVRicStrutId;
    }

    public void setOrgVRicStrutId(OrgVRicStrutId orgVRicStrutId) {
	this.orgVRicStrutId = orgVRicStrutId;
    }

}
