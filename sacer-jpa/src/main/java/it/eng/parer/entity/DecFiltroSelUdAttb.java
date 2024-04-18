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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the DEC_FILTRO_SEL_UD_ATTB database table.
 */
@Entity
@Table(name = "DEC_FILTRO_SEL_UD_ATTB")

public class DecFiltroSelUdAttb implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idFiltroSelUdAttb;

    private String dlValore;

    private String nmAttribDatiSpec;

    private String tiOper;

    private DecTipoSerieUd decTipoSerieUd;

    private List<DecFiltroSelUdDato> decFiltroSelUdDatos = new ArrayList<>();

    public DecFiltroSelUdAttb() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_FILTRO_SEL_UD_ATTB")
    @GenericGenerator(name = "SDEC_FILTRO_SEL_UD_ATTB_ID_FILTRO_SEL_UD_ATTB_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_FILTRO_SEL_UD_ATTB"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_FILTRO_SEL_UD_ATTB_ID_FILTRO_SEL_UD_ATTB_GENERATOR")
    public Long getIdFiltroSelUdAttb() {
        return this.idFiltroSelUdAttb;
    }

    public void setIdFiltroSelUdAttb(Long idFiltroSelUdAttb) {
        this.idFiltroSelUdAttb = idFiltroSelUdAttb;
    }

    @Column(name = "DL_VALORE")
    public String getDlValore() {
        return this.dlValore;
    }

    public void setDlValore(String dlValore) {
        this.dlValore = dlValore;
    }

    @Column(name = "NM_ATTRIB_DATI_SPEC")
    public String getNmAttribDatiSpec() {
        return this.nmAttribDatiSpec;
    }

    public void setNmAttribDatiSpec(String nmAttribDatiSpec) {
        this.nmAttribDatiSpec = nmAttribDatiSpec;
    }

    @Column(name = "TI_OPER")
    public String getTiOper() {
        return this.tiOper;
    }

    public void setTiOper(String tiOper) {
        this.tiOper = tiOper;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_SERIE_UD")
    public DecTipoSerieUd getDecTipoSerieUd() {
        return this.decTipoSerieUd;
    }

    public void setDecTipoSerieUd(DecTipoSerieUd decTipoSerieUd) {
        this.decTipoSerieUd = decTipoSerieUd;
    }

    @OneToMany(mappedBy = "decFiltroSelUdAttb", cascade = { CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REFRESH })
    public List<DecFiltroSelUdDato> getDecFiltroSelUdDatos() {
        return this.decFiltroSelUdDatos;
    }

    public void setDecFiltroSelUdDatos(List<DecFiltroSelUdDato> decFiltroSelUdDatos) {
        this.decFiltroSelUdDatos = decFiltroSelUdDatos;
    }

    public DecFiltroSelUdDato addDecFiltroSelUdDato(DecFiltroSelUdDato decFiltroSelUdDato) {
        getDecFiltroSelUdDatos().add(decFiltroSelUdDato);
        decFiltroSelUdDato.setDecFiltroSelUdAttb(this);
        return decFiltroSelUdDato;
    }

    public DecFiltroSelUdDato removeDecFiltroSelUdDato(DecFiltroSelUdDato decFiltroSelUdDato) {
        getDecFiltroSelUdDatos().remove(decFiltroSelUdDato);
        decFiltroSelUdDato.setDecFiltroSelUdAttb(null);
        return decFiltroSelUdDato;
    }

}
