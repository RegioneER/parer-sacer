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
 * The persistent class for the ELV_V_LIS_ELENCO_DA_MARCARE database table.
 *
 */
@Entity
@Table(name = "ELV_V_LIS_ELENCO_DA_MARCARE")

public class ElvVLisElencoDaMarcare implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal idAmbiente;
    private BigDecimal idCriterioRaggr;
    private BigDecimal idElencoVers;
    private BigDecimal idEnte;
    private BigDecimal idStrut;
    private BigDecimal idUserIam;
    private String nmAmbiente;
    private String nmCriterioRaggr;
    private String nmElenco;
    private String nmEnte;
    private String nmStrut;
    private String tiGestElenco;

    public ElvVLisElencoDaMarcare() {/* Hibernate */
    }

    @Column(name = "ID_AMBIENTE")
    public BigDecimal getIdAmbiente() {
	return this.idAmbiente;
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
	this.idAmbiente = idAmbiente;
    }

    @Column(name = "ID_CRITERIO_RAGGR")
    public BigDecimal getIdCriterioRaggr() {
	return this.idCriterioRaggr;
    }

    public void setIdCriterioRaggr(BigDecimal idCriterioRaggr) {
	this.idCriterioRaggr = idCriterioRaggr;
    }

    @Id
    @Column(name = "ID_ELENCO_VERS")
    public BigDecimal getIdElencoVers() {
	return this.idElencoVers;
    }

    public void setIdElencoVers(BigDecimal idElencoVers) {
	this.idElencoVers = idElencoVers;
    }

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
	return this.idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
	this.idEnte = idEnte;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }

    @Column(name = "ID_USER_IAM")
    public BigDecimal getIdUserIam() {
	return this.idUserIam;
    }

    public void setIdUserIam(BigDecimal idUserIam) {
	this.idUserIam = idUserIam;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
	return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
	this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_CRITERIO_RAGGR")
    public String getNmCriterioRaggr() {
	return this.nmCriterioRaggr;
    }

    public void setNmCriterioRaggr(String nmCriterioRaggr) {
	this.nmCriterioRaggr = nmCriterioRaggr;
    }

    @Column(name = "NM_ELENCO")
    public String getNmElenco() {
	return this.nmElenco;
    }

    public void setNmElenco(String nmElenco) {
	this.nmElenco = nmElenco;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
	return this.nmEnte;
    }

    public void setNmEnte(String nmEnte) {
	this.nmEnte = nmEnte;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
	return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
	this.nmStrut = nmStrut;
    }

    @Column(name = "TI_GEST_ELENCO")
    public String getTiGestElenco() {
	return this.tiGestElenco;
    }

    public void setTiGestElenco(String tiGestElenco) {
	this.tiGestElenco = tiGestElenco;
    }

}
