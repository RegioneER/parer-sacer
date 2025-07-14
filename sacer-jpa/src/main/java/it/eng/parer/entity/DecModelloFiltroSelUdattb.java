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
 * The persistent class for the DEC_MODELLO_FILTRO_SEL_UDATTB database table.
 */
@Entity
@Table(name = "DEC_MODELLO_FILTRO_SEL_UDATTB")

public class DecModelloFiltroSelUdattb implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idModelloFiltroSelUdattb;

    private String dlValore;

    private String nmFiltro;

    private String tiFiltro;

    private String tiOper;

    private DecModelloTipoSerie decModelloTipoSerie;

    public DecModelloFiltroSelUdattb() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_MODELLO_FILTRO_SEL_UDATTB")
    @GenericGenerator(name = "SDEC_MODELLO_FILTRO_SEL_UDATTB_ID_MODELLO_FILTRO_SEL_UDATTB_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_MODELLO_FILTRO_SEL_UDATTB"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_MODELLO_FILTRO_SEL_UDATTB_ID_MODELLO_FILTRO_SEL_UDATTB_GENERATOR")
    public Long getIdModelloFiltroSelUdattb() {
	return this.idModelloFiltroSelUdattb;
    }

    public void setIdModelloFiltroSelUdattb(Long idModelloFiltroSelUdattb) {
	this.idModelloFiltroSelUdattb = idModelloFiltroSelUdattb;
    }

    @Column(name = "DL_VALORE")
    public String getDlValore() {
	return this.dlValore;
    }

    public void setDlValore(String dlValore) {
	this.dlValore = dlValore;
    }

    @Column(name = "NM_FILTRO")
    public String getNmFiltro() {
	return this.nmFiltro;
    }

    public void setNmFiltro(String nmFiltro) {
	this.nmFiltro = nmFiltro;
    }

    @Column(name = "TI_FILTRO")
    public String getTiFiltro() {
	return this.tiFiltro;
    }

    public void setTiFiltro(String tiFiltro) {
	this.tiFiltro = tiFiltro;
    }

    @Column(name = "TI_OPER")
    public String getTiOper() {
	return this.tiOper;
    }

    public void setTiOper(String tiOper) {
	this.tiOper = tiOper;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MODELLO_TIPO_SERIE")
    public DecModelloTipoSerie getDecModelloTipoSerie() {
	return this.decModelloTipoSerie;
    }

    public void setDecModelloTipoSerie(DecModelloTipoSerie decModelloTipoSerie) {
	this.decModelloTipoSerie = decModelloTipoSerie;
    }

}
