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
 * The persistent class for the DEC_FILTRO_SEL_UD database table.
 */
@Entity
@Table(name = "DEC_FILTRO_SEL_UD")

public class DecFiltroSelUd implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idFiltroSelUd;

    private BigDecimal pgFiltro;

    private String tiFiltro;

    private DecTipoDoc decTipoDoc;

    private DecTipoSerieUd decTipoSerieUd;

    public DecFiltroSelUd() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_FILTRO_SEL_UD")
    @GenericGenerator(name = "SDEC_FILTRO_SEL_UD_ID_FILTRO_SEL_UD_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_FILTRO_SEL_UD"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_FILTRO_SEL_UD_ID_FILTRO_SEL_UD_GENERATOR")
    public Long getIdFiltroSelUd() {
        return this.idFiltroSelUd;
    }

    public void setIdFiltroSelUd(Long idFiltroSelUd) {
        this.idFiltroSelUd = idFiltroSelUd;
    }

    @Column(name = "PG_FILTRO")
    public BigDecimal getPgFiltro() {
        return this.pgFiltro;
    }

    public void setPgFiltro(BigDecimal pgFiltro) {
        this.pgFiltro = pgFiltro;
    }

    @Column(name = "TI_FILTRO")
    public String getTiFiltro() {
        return this.tiFiltro;
    }

    public void setTiFiltro(String tiFiltro) {
        this.tiFiltro = tiFiltro;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_DOC_PRINC")
    public DecTipoDoc getDecTipoDoc() {
        return this.decTipoDoc;
    }

    public void setDecTipoDoc(DecTipoDoc decTipoDoc) {
        this.decTipoDoc = decTipoDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_SERIE_UD")
    public DecTipoSerieUd getDecTipoSerieUd() {
        return this.decTipoSerieUd;
    }

    public void setDecTipoSerieUd(DecTipoSerieUd decTipoSerieUd) {
        this.decTipoSerieUd = decTipoSerieUd;
    }

}
