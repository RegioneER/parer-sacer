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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the SER_LACUNA_CONSIST_VER_SERIE database table.
 */
@Entity
@Table(name = "SER_LACUNA_CONSIST_VER_SERIE")

public class SerLacunaConsistVerSerie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idLacunaConsistVerSerie;

    private String dlLacuna;

    private String dlNotaLacuna;

    private BigDecimal niFinLacuna;

    private BigDecimal niIniLacuna;

    private BigDecimal pgLacuna;

    private String tiLacuna;

    private String tiModLacuna;

    private SerConsistVerSerie serConsistVerSerie;

    public SerLacunaConsistVerSerie() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_LACUNA_CONSIST_VER_SERIE")
    @GenericGenerator(name = "SSER_LACUNA_CONSIST_VER_SERIE_ID_LACUNA_CONSIST_VER_SERIE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SSER_LACUNA_CONSIST_VER_SERIE"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SSER_LACUNA_CONSIST_VER_SERIE_ID_LACUNA_CONSIST_VER_SERIE_GENERATOR")
    public Long getIdLacunaConsistVerSerie() {
	return this.idLacunaConsistVerSerie;
    }

    public void setIdLacunaConsistVerSerie(Long idLacunaConsistVerSerie) {
	this.idLacunaConsistVerSerie = idLacunaConsistVerSerie;
    }

    @Column(name = "DL_LACUNA")
    public String getDlLacuna() {
	return this.dlLacuna;
    }

    public void setDlLacuna(String dlLacuna) {
	this.dlLacuna = dlLacuna;
    }

    @Column(name = "DL_NOTA_LACUNA")
    public String getDlNotaLacuna() {
	return this.dlNotaLacuna;
    }

    public void setDlNotaLacuna(String dlNotaLacuna) {
	this.dlNotaLacuna = dlNotaLacuna;
    }

    @Column(name = "NI_FIN_LACUNA")
    public BigDecimal getNiFinLacuna() {
	return this.niFinLacuna;
    }

    public void setNiFinLacuna(BigDecimal niFinLacuna) {
	this.niFinLacuna = niFinLacuna;
    }

    @Column(name = "NI_INI_LACUNA")
    public BigDecimal getNiIniLacuna() {
	return this.niIniLacuna;
    }

    public void setNiIniLacuna(BigDecimal niIniLacuna) {
	this.niIniLacuna = niIniLacuna;
    }

    @Column(name = "PG_LACUNA")
    public BigDecimal getPgLacuna() {
	return this.pgLacuna;
    }

    public void setPgLacuna(BigDecimal pgLacuna) {
	this.pgLacuna = pgLacuna;
    }

    @Column(name = "TI_LACUNA")
    public String getTiLacuna() {
	return this.tiLacuna;
    }

    public void setTiLacuna(String tiLacuna) {
	this.tiLacuna = tiLacuna;
    }

    @Column(name = "TI_MOD_LACUNA")
    public String getTiModLacuna() {
	return this.tiModLacuna;
    }

    public void setTiModLacuna(String tiModLacuna) {
	this.tiModLacuna = tiModLacuna;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONSIST_VER_SERIE")
    public SerConsistVerSerie getSerConsistVerSerie() {
	return this.serConsistVerSerie;
    }

    public void setSerConsistVerSerie(SerConsistVerSerie serConsistVerSerie) {
	this.serConsistVerSerie = serConsistVerSerie;
    }

}
