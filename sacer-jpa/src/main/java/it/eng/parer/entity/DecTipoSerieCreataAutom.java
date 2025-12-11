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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the DEC_TIPO_SERIE_CREATA_AUTOM database table.
 */
@Entity
@Table(name = "DEC_TIPO_SERIE_CREATA_AUTOM")

public class DecTipoSerieCreataAutom implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idTipoSerieCreataAutom;

    private Date dtCreaAutom;

    private DecTipoSerie decTipoSerie;

    public DecTipoSerieCreataAutom() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_TIPO_SERIE_CREATA_AUTOM")
    @GenericGenerator(name = "SDEC_TIPO_SERIE_CREATA_AUTOM_ID_TIPO_SERIE_CREATA_AUTOM_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_TIPO_SERIE_CREATA_AUTOM"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_TIPO_SERIE_CREATA_AUTOM_ID_TIPO_SERIE_CREATA_AUTOM_GENERATOR")
    public Long getIdTipoSerieCreataAutom() {
        return this.idTipoSerieCreataAutom;
    }

    public void setIdTipoSerieCreataAutom(Long idTipoSerieCreataAutom) {
        this.idTipoSerieCreataAutom = idTipoSerieCreataAutom;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREA_AUTOM")
    public Date getDtCreaAutom() {
        return this.dtCreaAutom;
    }

    public void setDtCreaAutom(Date dtCreaAutom) {
        this.dtCreaAutom = dtCreaAutom;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_SERIE")
    public DecTipoSerie getDecTipoSerie() {
        return this.decTipoSerie;
    }

    public void setDecTipoSerie(DecTipoSerie decTipoSerie) {
        this.decTipoSerie = decTipoSerie;
    }

}
