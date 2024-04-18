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
 * The persistent class for the DEC_CAMPO_OUT_SEL_UD database table.
 */
@Entity
@Table(name = "DEC_CAMPO_OUT_SEL_UD")

public class DecCampoOutSelUd implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idCampoOutSelUd;

    private String nmCampo;

    private String tiCampo;

    private String tiTrasformCampo;

    private DecAttribDatiSpec decAttribDatiSpec;

    private DecOutSelUd decOutSelUd;

    public DecCampoOutSelUd() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_CAMPO_OUT_SEL_UD")
    @GenericGenerator(name = "SDEC_CAMPO_OUT_SEL_UD_ID_CAMPO_OUT_SEL_UD_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_CAMPO_OUT_SEL_UD"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_CAMPO_OUT_SEL_UD_ID_CAMPO_OUT_SEL_UD_GENERATOR")
    public Long getIdCampoOutSelUd() {
        return this.idCampoOutSelUd;
    }

    public void setIdCampoOutSelUd(Long idCampoOutSelUd) {
        this.idCampoOutSelUd = idCampoOutSelUd;
    }

    @Column(name = "NM_CAMPO")
    public String getNmCampo() {
        return this.nmCampo;
    }

    public void setNmCampo(String nmCampo) {
        this.nmCampo = nmCampo;
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
    @JoinColumn(name = "ID_OUT_SEL_UD")
    public DecOutSelUd getDecOutSelUd() {
        return this.decOutSelUd;
    }

    public void setDecOutSelUd(DecOutSelUd decOutSelUd) {
        this.decOutSelUd = decOutSelUd;
    }

}
