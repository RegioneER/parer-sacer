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
 * The persistent class for the DEC_ERR_AA_REGISTRO_UNITA_DOC database table.
 */
@Entity
@Table(name = "DEC_ERR_AA_REGISTRO_UNITA_DOC")

public class DecErrAaRegistroUnitaDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idErrAaRegistroUnitaDoc;

    private BigDecimal aaRegistroUnitaDoc;

    private String dsErrFmtNumero;

    private BigDecimal idUnitaDocErrFmtNumero;

    private DecAaRegistroUnitaDoc decAaRegistroUnitaDoc;

    public DecErrAaRegistroUnitaDoc() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_ERR_AA_REGISTRO_UNITA_DOC")
    @GenericGenerator(name = "SDEC_ERR_AA_REGISTRO_UNITA_DOC_ID_ERR_AA_REGISTRO_UNITA_DOC_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_ERR_AA_REGISTRO_UNITA_DOC"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_ERR_AA_REGISTRO_UNITA_DOC_ID_ERR_AA_REGISTRO_UNITA_DOC_GENERATOR")
    public Long getIdErrAaRegistroUnitaDoc() {
        return this.idErrAaRegistroUnitaDoc;
    }

    public void setIdErrAaRegistroUnitaDoc(Long idErrAaRegistroUnitaDoc) {
        this.idErrAaRegistroUnitaDoc = idErrAaRegistroUnitaDoc;
    }

    @Column(name = "AA_REGISTRO_UNITA_DOC")
    public BigDecimal getAaRegistroUnitaDoc() {
        return this.aaRegistroUnitaDoc;
    }

    public void setAaRegistroUnitaDoc(BigDecimal aaRegistroUnitaDoc) {
        this.aaRegistroUnitaDoc = aaRegistroUnitaDoc;
    }

    @Column(name = "DS_ERR_FMT_NUMERO")
    public String getDsErrFmtNumero() {
        return this.dsErrFmtNumero;
    }

    public void setDsErrFmtNumero(String dsErrFmtNumero) {
        this.dsErrFmtNumero = dsErrFmtNumero;
    }

    @Column(name = "ID_UNITA_DOC_ERR_FMT_NUMERO")
    public BigDecimal getIdUnitaDocErrFmtNumero() {
        return this.idUnitaDocErrFmtNumero;
    }

    public void setIdUnitaDocErrFmtNumero(BigDecimal idUnitaDocErrFmtNumero) {
        this.idUnitaDocErrFmtNumero = idUnitaDocErrFmtNumero;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AA_REGISTRO_UNITA_DOC")
    public DecAaRegistroUnitaDoc getDecAaRegistroUnitaDoc() {
        return this.decAaRegistroUnitaDoc;
    }

    public void setDecAaRegistroUnitaDoc(DecAaRegistroUnitaDoc decAaRegistroUnitaDoc) {
        this.decAaRegistroUnitaDoc = decAaRegistroUnitaDoc;
    }

}
