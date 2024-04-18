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
import javax.xml.bind.annotation.XmlID;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the DEC_TIPO_RAPPR_AMMESSO database table.
 */
@Entity
@Table(name = "DEC_TIPO_RAPPR_AMMESSO")

public class DecTipoRapprAmmesso implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idTipoRapprAmmesso;

    private DecTipoCompDoc decTipoCompDoc;

    private DecTipoRapprComp decTipoRapprComp;

    public DecTipoRapprAmmesso() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_TIPO_RAPPR_AMMESSO")
    @XmlID
    @GenericGenerator(name = "SDEC_TIPO_RAPPR_AMMESSO_ID_TIPO_RAPPR_AMMESSO_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_TIPO_RAPPR_AMMESSO"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_TIPO_RAPPR_AMMESSO_ID_TIPO_RAPPR_AMMESSO_GENERATOR")
    public Long getIdTipoRapprAmmesso() {
        return this.idTipoRapprAmmesso;
    }

    public void setIdTipoRapprAmmesso(Long idTipoRapprAmmesso) {
        this.idTipoRapprAmmesso = idTipoRapprAmmesso;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_COMP_DOC")
    @XmlInverseReference(mappedBy = "decTipoRapprAmmessos")
    public DecTipoCompDoc getDecTipoCompDoc() {
        return this.decTipoCompDoc;
    }

    public void setDecTipoCompDoc(DecTipoCompDoc decTipoCompDoc) {
        this.decTipoCompDoc = decTipoCompDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_RAPPR_COMP")
    @XmlInverseReference(mappedBy = "decTipoRapprAmmessos")
    public DecTipoRapprComp getDecTipoRapprComp() {
        return this.decTipoRapprComp;
    }

    public void setDecTipoRapprComp(DecTipoRapprComp decTipoRapprComp) {
        this.decTipoRapprComp = decTipoRapprComp;
    }

}
