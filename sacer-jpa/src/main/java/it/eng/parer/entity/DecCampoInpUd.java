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
 * The persistent class for the DEC_CAMPO_INP_UD database table.
 */
@Entity
@Table(name = "DEC_CAMPO_INP_UD")

public class DecCampoInpUd implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idCampoInpUd;

    private String nmCampo;

    private BigDecimal pgOrdCampo;

    private String tiCampo;

    private String tiTrasformCampo;

    private DecAttribDatiSpec decAttribDatiSpec;

    private DecTipoSerie decTipoSerie;

    public DecCampoInpUd() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_CAMPO_INP_UD")
    @GenericGenerator(name = "SDEC_CAMPO_INP_UD_ID_CAMPO_INP_UD_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_CAMPO_INP_UD"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_CAMPO_INP_UD_ID_CAMPO_INP_UD_GENERATOR")
    public Long getIdCampoInpUd() {
        return this.idCampoInpUd;
    }

    public void setIdCampoInpUd(Long idCampoInpUd) {
        this.idCampoInpUd = idCampoInpUd;
    }

    @Column(name = "NM_CAMPO")
    public String getNmCampo() {
        return this.nmCampo;
    }

    public void setNmCampo(String nmCampo) {
        this.nmCampo = nmCampo;
    }

    @Column(name = "PG_ORD_CAMPO")
    public BigDecimal getPgOrdCampo() {
        return this.pgOrdCampo;
    }

    public void setPgOrdCampo(BigDecimal pgOrdCampo) {
        this.pgOrdCampo = pgOrdCampo;
    }

    @Column(name = "TI_CAMPO")
    public String getTiCampo() {
        return this.tiCampo;
    }

    public void setTiCampo(String tiCampo) {
        this.tiCampo = tiCampo;
    }

    @Column(name = "TI_TRASFORM_CAMPO")
    public String getTiTrasformCampo() {
        return this.tiTrasformCampo;
    }

    public void setTiTrasformCampo(String tiTrasformCampo) {
        this.tiTrasformCampo = tiTrasformCampo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ATTRIB_DATI_SPEC")
    public DecAttribDatiSpec getDecAttribDatiSpec() {
        return this.decAttribDatiSpec;
    }

    public void setDecAttribDatiSpec(DecAttribDatiSpec decAttribDatiSpec) {
        this.decAttribDatiSpec = decAttribDatiSpec;
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
