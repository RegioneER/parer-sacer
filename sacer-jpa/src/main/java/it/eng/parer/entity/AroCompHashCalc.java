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
 * The persistent class for the ARO_COMP_HASH_CALC database table.
 */
@Entity
@Table(name = "ARO_COMP_HASH_CALC")
public class AroCompHashCalc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idCompHashCalc;

    private String cdEncodingHashFile;

    private String dsAlgoHashFile;

    private String dsHashFile;

    private AroCompDoc aroCompDoc;

    public AroCompHashCalc() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_COMP_HASH_CALC")
    @GenericGenerator(name = "SARO_COMP_HASH_CALC_ID_COMP_HASH_CALC_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SARO_COMP_HASH_CALC"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SARO_COMP_HASH_CALC_ID_COMP_HASH_CALC_GENERATOR")
    public Long getIdCompHashCalc() {
        return this.idCompHashCalc;
    }

    public void setIdCompHashCalc(Long idCompHashCalc) {
        this.idCompHashCalc = idCompHashCalc;
    }

    @Column(name = "CD_ENCODING_HASH_FILE")
    public String getCdEncodingHashFile() {
        return this.cdEncodingHashFile;
    }

    public void setCdEncodingHashFile(String cdEncodingHashFile) {
        this.cdEncodingHashFile = cdEncodingHashFile;
    }

    @Column(name = "DS_ALGO_HASH_FILE")
    public String getDsAlgoHashFile() {
        return this.dsAlgoHashFile;
    }

    public void setDsAlgoHashFile(String dsAlgoHashFile) {
        this.dsAlgoHashFile = dsAlgoHashFile;
    }

    @Column(name = "DS_HASH_FILE")
    public String getDsHashFile() {
        return this.dsHashFile;
    }

    public void setDsHashFile(String dsHashFile) {
        this.dsHashFile = dsHashFile;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMP_DOC")
    public AroCompDoc getAroCompDoc() {
        return this.aroCompDoc;
    }

    public void setAroCompDoc(AroCompDoc aroCompDoc) {
        this.aroCompDoc = aroCompDoc;
    }
}
