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
 * The persistent class for the DEC_TIPO_SERIE_UD database table.
 */
@Entity
@Table(name = "DEC_TIPO_SERIE_UD")

public class DecTipoSerieUd implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idTipoSerieUd;

    private String flSelUnitaDocAnnul;

    private List<DecFiltroSelUd> decFiltroSelUds = new ArrayList<>();

    private List<DecFiltroSelUdAttb> decFiltroSelUdAttbs = new ArrayList<>();

    private List<DecOutSelUd> decOutSelUds = new ArrayList<>();

    private DecRegistroUnitaDoc decRegistroUnitaDoc;

    private DecTipoSerie decTipoSerie;

    private DecTipoUnitaDoc decTipoUnitaDoc;

    public DecTipoSerieUd() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_TIPO_SERIE_UD")
    @GenericGenerator(name = "SDEC_TIPO_SERIE_UD_ID_TIPO_SERIE_UD_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_TIPO_SERIE_UD"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_TIPO_SERIE_UD_ID_TIPO_SERIE_UD_GENERATOR")
    public Long getIdTipoSerieUd() {
        return this.idTipoSerieUd;
    }

    public void setIdTipoSerieUd(Long idTipoSerieUd) {
        this.idTipoSerieUd = idTipoSerieUd;
    }

    @Column(name = "FL_SEL_UNITA_DOC_ANNUL", columnDefinition = "char(1)")
    public String getFlSelUnitaDocAnnul() {
        return this.flSelUnitaDocAnnul;
    }

    public void setFlSelUnitaDocAnnul(String flSelUnitaDocAnnul) {
        this.flSelUnitaDocAnnul = flSelUnitaDocAnnul;
    }

    @OneToMany(mappedBy = "decTipoSerieUd", cascade = CascadeType.PERSIST)
    public List<DecFiltroSelUd> getDecFiltroSelUds() {
        return this.decFiltroSelUds;
    }

    public void setDecFiltroSelUds(List<DecFiltroSelUd> decFiltroSelUds) {
        this.decFiltroSelUds = decFiltroSelUds;
    }

    public DecFiltroSelUd addDecFiltroSelUd(DecFiltroSelUd decFiltroSelUd) {
        getDecFiltroSelUds().add(decFiltroSelUd);
        decFiltroSelUd.setDecTipoSerieUd(this);
        return decFiltroSelUd;
    }

    public DecFiltroSelUd removeDecFiltroSelUd(DecFiltroSelUd decFiltroSelUd) {
        getDecFiltroSelUds().remove(decFiltroSelUd);
        decFiltroSelUd.setDecTipoSerieUd(null);
        return decFiltroSelUd;
    }

    @OneToMany(mappedBy = "decTipoSerieUd", cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    public List<DecFiltroSelUdAttb> getDecFiltroSelUdAttbs() {
        return this.decFiltroSelUdAttbs;
    }

    public void setDecFiltroSelUdAttbs(List<DecFiltroSelUdAttb> decFiltroSelUdAttbs) {
        this.decFiltroSelUdAttbs = decFiltroSelUdAttbs;
    }

    public DecFiltroSelUdAttb addDecFiltroSelUdAttb(DecFiltroSelUdAttb decFiltroSelUdAttb) {
        getDecFiltroSelUdAttbs().add(decFiltroSelUdAttb);
        decFiltroSelUdAttb.setDecTipoSerieUd(this);
        return decFiltroSelUdAttb;
    }

    public DecFiltroSelUdAttb removeDecFiltroSelUdAttb(DecFiltroSelUdAttb decFiltroSelUdAttb) {
        getDecFiltroSelUdAttbs().remove(decFiltroSelUdAttb);
        decFiltroSelUdAttb.setDecTipoSerieUd(null);
        return decFiltroSelUdAttb;
    }

    @OneToMany(mappedBy = "decTipoSerieUd", cascade = CascadeType.PERSIST)
    public List<DecOutSelUd> getDecOutSelUds() {
        return this.decOutSelUds;
    }

    public void setDecOutSelUds(List<DecOutSelUd> decOutSelUds) {
        this.decOutSelUds = decOutSelUds;
    }

    public DecOutSelUd addDecOutSelUd(DecOutSelUd decOutSelUd) {
        getDecOutSelUds().add(decOutSelUd);
        decOutSelUd.setDecTipoSerieUd(this);
        return decOutSelUd;
    }

    public DecOutSelUd removeDecOutSelUd(DecOutSelUd decOutSelUd) {
        getDecOutSelUds().remove(decOutSelUd);
        decOutSelUd.setDecTipoSerieUd(null);
        return decOutSelUd;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_REGISTRO_UNITA_DOC")
    public DecRegistroUnitaDoc getDecRegistroUnitaDoc() {
        return this.decRegistroUnitaDoc;
    }

    public void setDecRegistroUnitaDoc(DecRegistroUnitaDoc decRegistroUnitaDoc) {
        this.decRegistroUnitaDoc = decRegistroUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_SERIE")
    public DecTipoSerie getDecTipoSerie() {
        return this.decTipoSerie;
    }

    public void setDecTipoSerie(DecTipoSerie decTipoSerie) {
        this.decTipoSerie = decTipoSerie;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_UNITA_DOC")
    public DecTipoUnitaDoc getDecTipoUnitaDoc() {
        return this.decTipoUnitaDoc;
    }

    public void setDecTipoUnitaDoc(DecTipoUnitaDoc decTipoUnitaDoc) {
        this.decTipoUnitaDoc = decTipoUnitaDoc;
    }

}
