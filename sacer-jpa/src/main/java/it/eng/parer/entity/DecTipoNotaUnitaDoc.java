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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the DEC_TIPO_NOTA_UNITA_DOC database table.
 *
 */
@Entity
@Table(name = "DEC_TIPO_NOTA_UNITA_DOC")

public class DecTipoNotaUnitaDoc implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idTipoNotaUnitaDoc;
    private String cdTipoNotaUnitaDoc;
    private String dsTipoNotaUnitaDoc;
    private String flMolt;
    private String flObblig;
    private BigDecimal niOrd;
    private List<AroNotaUnitaDoc> aroNotaUnitaDocs = new ArrayList<>();

    public DecTipoNotaUnitaDoc() {
        // hibernate
    }

    @Id

    @GenericGenerator(name = "DEC_TIPO_NOTA_UNITA_DOC_IDTIPONOTAUNITADOC_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_TIPO_NOTA_UNITA_DOC"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEC_TIPO_NOTA_UNITA_DOC_IDTIPONOTAUNITADOC_GENERATOR")
    @Column(name = "ID_TIPO_NOTA_UNITA_DOC")
    public Long getIdTipoNotaUnitaDoc() {
        return this.idTipoNotaUnitaDoc;
    }

    public void setIdTipoNotaUnitaDoc(Long idTipoNotaUnitaDoc) {
        this.idTipoNotaUnitaDoc = idTipoNotaUnitaDoc;
    }

    @Column(name = "CD_TIPO_NOTA_UNITA_DOC")
    public String getCdTipoNotaUnitaDoc() {
        return this.cdTipoNotaUnitaDoc;
    }

    public void setCdTipoNotaUnitaDoc(String cdTipoNotaUnitaDoc) {
        this.cdTipoNotaUnitaDoc = cdTipoNotaUnitaDoc;
    }

    @Column(name = "DS_TIPO_NOTA_UNITA_DOC")
    public String getDsTipoNotaUnitaDoc() {
        return this.dsTipoNotaUnitaDoc;
    }

    public void setDsTipoNotaUnitaDoc(String dsTipoNotaUnitaDoc) {
        this.dsTipoNotaUnitaDoc = dsTipoNotaUnitaDoc;
    }

    @Column(name = "FL_MOLT", columnDefinition = "CHAR")
    public String getFlMolt() {
        return this.flMolt;
    }

    public void setFlMolt(String flMolt) {
        this.flMolt = flMolt;
    }

    @Column(name = "FL_OBBLIG", columnDefinition = "CHAR")
    public String getFlObblig() {
        return this.flObblig;
    }

    public void setFlObblig(String flObblig) {
        this.flObblig = flObblig;
    }

    @Column(name = "NI_ORD")
    public BigDecimal getNiOrd() {
        return this.niOrd;
    }

    public void setNiOrd(BigDecimal niOrd) {
        this.niOrd = niOrd;
    }

    @OneToMany(mappedBy = "decTipoNotaUnitaDoc")
    public List<AroNotaUnitaDoc> getAroNotaUnitaDocs() {
        return this.aroNotaUnitaDocs;
    }

    public void setAroNotaUnitaDocs(List<AroNotaUnitaDoc> aroNotaUnitaDocs) {
        this.aroNotaUnitaDocs = aroNotaUnitaDocs;
    }

    public AroNotaUnitaDoc addAroNotaUnitaDoc(AroNotaUnitaDoc aroNotaUnitaDoc) {
        getAroNotaUnitaDocs().add(aroNotaUnitaDoc);
        aroNotaUnitaDoc.setDecTipoNotaUnitaDoc(this);

        return aroNotaUnitaDoc;
    }

    public AroNotaUnitaDoc removeAroNotaUnitaDoc(AroNotaUnitaDoc aroNotaUnitaDoc) {
        getAroNotaUnitaDocs().remove(aroNotaUnitaDoc);
        aroNotaUnitaDoc.setDecTipoNotaUnitaDoc(null);

        return aroNotaUnitaDoc;
    }

}
