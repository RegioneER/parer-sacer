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
 * The persistent class for the DEC_FILTRO_SEL_UD_DATO database table.
 */
@Entity
@Table(name = "DEC_FILTRO_SEL_UD_DATO")

public class DecFiltroSelUdDato implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idFiltroSelUdDato;

    private String dsListaVersioniXsd;

    private String nmTipoDoc;

    private String nmTipoUnitaDoc;

    private String tiEntitaSacer;

    private DecAttribDatiSpec decAttribDatiSpec;

    private DecFiltroSelUdAttb decFiltroSelUdAttb;

    public DecFiltroSelUdDato() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_FILTRO_SEL_UD_DATO")
    @GenericGenerator(name = "SDEC_FILTRO_SEL_UD_DATO_ID_FILTRO_SEL_UD_DATO_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_FILTRO_SEL_UD_DATO"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_FILTRO_SEL_UD_DATO_ID_FILTRO_SEL_UD_DATO_GENERATOR")
    public Long getIdFiltroSelUdDato() {
        return this.idFiltroSelUdDato;
    }

    public void setIdFiltroSelUdDato(Long idFiltroSelUdDato) {
        this.idFiltroSelUdDato = idFiltroSelUdDato;
    }

    @Column(name = "DS_LISTA_VERSIONI_XSD")
    public String getDsListaVersioniXsd() {
        return this.dsListaVersioniXsd;
    }

    public void setDsListaVersioniXsd(String dsListaVersioniXsd) {
        this.dsListaVersioniXsd = dsListaVersioniXsd;
    }

    @Column(name = "NM_TIPO_DOC")
    public String getNmTipoDoc() {
        return this.nmTipoDoc;
    }

    public void setNmTipoDoc(String nmTipoDoc) {
        this.nmTipoDoc = nmTipoDoc;
    }

    @Column(name = "NM_TIPO_UNITA_DOC")
    public String getNmTipoUnitaDoc() {
        return this.nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
        this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    @Column(name = "TI_ENTITA_SACER")
    public String getTiEntitaSacer() {
        return this.tiEntitaSacer;
    }

    public void setTiEntitaSacer(String tiEntitaSacer) {
        this.tiEntitaSacer = tiEntitaSacer;
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
    @JoinColumn(name = "ID_FILTRO_SEL_UD_ATTB")
    public DecFiltroSelUdAttb getDecFiltroSelUdAttb() {
        return this.decFiltroSelUdAttb;
    }

    public void setDecFiltroSelUdAttb(DecFiltroSelUdAttb decFiltroSelUdAttb) {
        this.decFiltroSelUdAttb = decFiltroSelUdAttb;
    }

}
