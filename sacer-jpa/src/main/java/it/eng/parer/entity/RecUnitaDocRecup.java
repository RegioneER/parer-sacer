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

import java.util.ArrayList;
import java.util.List;

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
 * The persistent class for the REC_UNITA_DOC_RECUP database table.
 */
@Entity
@Table(name = "REC_UNITA_DOC_RECUP")

public class RecUnitaDocRecup {

    private static final long serialVersionUID = 1L;

    private Long idUnitaDocRecup;

    private List<RecSessioneRecup> recSessioneRecups = new ArrayList<>();

    private AroUnitaDoc aroUnitaDoc;

    public RecUnitaDocRecup() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_UNITA_DOC_RECUP")
    @GenericGenerator(name = "SREC_UNITA_DOC_RECUP_ID_UNITA_DOC_RECUP_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SREC_UNITA_DOC_RECUP"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SREC_UNITA_DOC_RECUP_ID_UNITA_DOC_RECUP_GENERATOR")
    public Long getIdUnitaDocRecup() {
        return this.idUnitaDocRecup;
    }

    public void setIdUnitaDocRecup(Long idUnitaDocRecup) {
        this.idUnitaDocRecup = idUnitaDocRecup;
    }

    @OneToMany(mappedBy = "recUnitaDocRecup")
    public List<RecSessioneRecup> getRecSessioneRecups() {
        return this.recSessioneRecups;
    }

    public void setRecSessioneRecups(List<RecSessioneRecup> recSessioneRecups) {
        this.recSessioneRecups = recSessioneRecups;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNITA_DOC")
    public AroUnitaDoc getAroUnitaDoc() {
        return this.aroUnitaDoc;
    }

    public void setAroUnitaDoc(AroUnitaDoc aroUnitaDoc) {
        this.aroUnitaDoc = aroUnitaDoc;
    }
}
