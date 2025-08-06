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
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;

/**
 * The persistent class for the DEC_V_CREA_CRIT_RAGGR_TIPO_DOC database table.
 *
 */
@Entity
@Table(name = "DEC_V_CREA_CRIT_RAGGR_TIPO_DOC")

public class DecVCreaCritRaggrTipoDoc implements Serializable {

    private static final long serialVersionUID = 1L;
    private String dsCriterioRaggr;
    private String flCriterioRaggrFisc;
    private String flFiltroRangeRegistroKey;
    private String flFiltroRegistroKey;
    private String flFiltroSistemaMigraz;
    private String flFiltroTiEsitoVerifFirme;
    private String flFiltroTipoDoc;
    private String flFiltroTipoUnitaDoc;
    private BigDecimal idStrut;
    private BigDecimal idTipoDoc;
    private BigDecimal niMaxComp;
    private BigDecimal niTempoScadChius;
    private BigDecimal niTempoScadChiusFirme;
    private String nmCriterioRaggr;
    private String tiTempoScadChius;
    private String tiTempoScadChiusFirme;
    private String tiValidElenco;
    private String tiModValidElenco;

    public DecVCreaCritRaggrTipoDoc() {/* Hibernate */
    }

    @Column(name = "DS_CRITERIO_RAGGR")
    public String getDsCriterioRaggr() {
	return this.dsCriterioRaggr;
    }

    public void setDsCriterioRaggr(String dsCriterioRaggr) {
	this.dsCriterioRaggr = dsCriterioRaggr;
    }

    @Column(name = "FL_CRITERIO_RAGGR_FISC", columnDefinition = "char(1)")
    public String getFlCriterioRaggrFisc() {
	return this.flCriterioRaggrFisc;
    }

    public void setFlCriterioRaggrFisc(String flCriterioRaggrFisc) {
	this.flCriterioRaggrFisc = flCriterioRaggrFisc;
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

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }

    @Id
    @Column(name = "ID_TIPO_DOC")
    public BigDecimal getIdTipoDoc() {
	return this.idTipoDoc;
    }

    public void setIdTipoDoc(BigDecimal idTipoDoc) {
	this.idTipoDoc = idTipoDoc;
    }

    @Column(name = "NI_MAX_COMP", columnDefinition = "varchar2")
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

    @Column(name = "NI_TEMPO_SCAD_CHIUS_FIRME")
    public BigDecimal getNiTempoScadChiusFirme() {
	return this.niTempoScadChiusFirme;
    }

    public void setNiTempoScadChiusFirme(BigDecimal niTempoScadChiusFirme) {
	this.niTempoScadChiusFirme = niTempoScadChiusFirme;
    }

    @Column(name = "NM_CRITERIO_RAGGR")
    public String getNmCriterioRaggr() {
	return this.nmCriterioRaggr;
    }

    public void setNmCriterioRaggr(String nmCriterioRaggr) {
	this.nmCriterioRaggr = nmCriterioRaggr;
    }

    @Column(name = "TI_TEMPO_SCAD_CHIUS", columnDefinition = "char")
    public String getTiTempoScadChius() {
	return this.tiTempoScadChius;
    }

    public void setTiTempoScadChius(String tiTempoScadChius) {
	this.tiTempoScadChius = tiTempoScadChius;
    }

    @Column(name = "TI_TEMPO_SCAD_CHIUS_FIRME", columnDefinition = "char")
    public String getTiTempoScadChiusFirme() {
	return this.tiTempoScadChiusFirme;
    }

    public void setTiTempoScadChiusFirme(String tiTempoScadChiusFirme) {
	this.tiTempoScadChiusFirme = tiTempoScadChiusFirme;
    }

    @Column(name = "TI_VALID_ELENCO")
    public String getTiValidElenco() {
	return this.tiValidElenco;
    }

    public void setTiValidElenco(String tiValidElenco) {
	this.tiValidElenco = tiValidElenco;
    }

    @Column(name = "TI_MOD_VALID_ELENCO")
    public String getTiModValidElenco() {
	return this.tiModValidElenco;
    }

    public void setTiModValidElenco(String tiModValidElenco) {
	this.tiModValidElenco = tiModValidElenco;
    }

}
