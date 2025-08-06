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
 * The persistent class for the DEC_MODELLO_FILTRO_TI_DOC database table.
 */
@Entity
@Table(name = "DEC_MODELLO_FILTRO_TI_DOC")

public class DecModelloFiltroTiDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idModelloFiltroTiDoc;

    private String nmTipoDoc;

    private DecModelloTipoSerie decModelloTipoSerie;

    public DecModelloFiltroTiDoc() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_MODELLO_FILTRO_TI_DOC")
    @GenericGenerator(name = "SDEC_MODELLO_FILTRO_TI_DOC_ID_MODELLO_FILTRO_TI_DOC_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_MODELLO_FILTRO_TI_DOC"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_MODELLO_FILTRO_TI_DOC_ID_MODELLO_FILTRO_TI_DOC_GENERATOR")
    public Long getIdModelloFiltroTiDoc() {
	return this.idModelloFiltroTiDoc;
    }

    public void setIdModelloFiltroTiDoc(Long idModelloFiltroTiDoc) {
	this.idModelloFiltroTiDoc = idModelloFiltroTiDoc;
    }

    @Column(name = "NM_TIPO_DOC")
    public String getNmTipoDoc() {
	return this.nmTipoDoc;
    }

    public void setNmTipoDoc(String nmTipoDoc) {
	this.nmTipoDoc = nmTipoDoc;
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
