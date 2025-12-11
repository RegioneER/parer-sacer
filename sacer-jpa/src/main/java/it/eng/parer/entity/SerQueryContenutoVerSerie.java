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
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the SER_QUERY_CONTENUTO_VER_SERIE database table.
 */
@Entity
@Table(name = "SER_QUERY_CONTENUTO_VER_SERIE")

public class SerQueryContenutoVerSerie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idQueryContenutoVerSerie;

    private String blQuery;

    private BigDecimal idRegistroUnitaDoc;

    private BigDecimal idTipoUnitaDoc;

    private SerContenutoVerSerie serContenutoVerSerie;

    public SerQueryContenutoVerSerie() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_QUERY_CONTENUTO_VER_SERIE")
    @GenericGenerator(name = "SSER_QUERY_CONTENUTO_VER_SERIE_ID_QUERY_CONTENUTO_VER_SERIE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SSER_QUERY_CONTENUTO_VER_SERIE"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SSER_QUERY_CONTENUTO_VER_SERIE_ID_QUERY_CONTENUTO_VER_SERIE_GENERATOR")
    public Long getIdQueryContenutoVerSerie() {
        return this.idQueryContenutoVerSerie;
    }

    public void setIdQueryContenutoVerSerie(Long idQueryContenutoVerSerie) {
        this.idQueryContenutoVerSerie = idQueryContenutoVerSerie;
    }

    @Lob
    @Column(name = "BL_QUERY")
    public String getBlQuery() {
        return this.blQuery;
    }

    public void setBlQuery(String blQuery) {
        this.blQuery = blQuery;
    }

    @Column(name = "ID_REGISTRO_UNITA_DOC")
    public BigDecimal getIdRegistroUnitaDoc() {
        return this.idRegistroUnitaDoc;
    }

    public void setIdRegistroUnitaDoc(BigDecimal idRegistroUnitaDoc) {
        this.idRegistroUnitaDoc = idRegistroUnitaDoc;
    }

    @Column(name = "ID_TIPO_UNITA_DOC")
    public BigDecimal getIdTipoUnitaDoc() {
        return this.idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONTENUTO_VER_SERIE")
    public SerContenutoVerSerie getSerContenutoVerSerie() {
        return this.serContenutoVerSerie;
    }

    public void setSerContenutoVerSerie(SerContenutoVerSerie serContenutoVerSerie) {
        this.serContenutoVerSerie = serContenutoVerSerie;
    }

}
