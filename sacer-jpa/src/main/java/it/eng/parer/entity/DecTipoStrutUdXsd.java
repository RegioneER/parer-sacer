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
import javax.xml.bind.annotation.XmlID;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the DEC_TIPO_STRUT_UD_XSD database table.
 */
@Entity
@Table(name = "DEC_TIPO_STRUT_UD_XSD")

public class DecTipoStrutUdXsd implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idTipoStrutUdXsd;

    private DecXsdDatiSpec decXsdDatiSpec;

    private DecTipoStrutUnitaDoc decTipoStrutUnitaDoc;

    public DecTipoStrutUdXsd() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_TIPO_STRUT_UD_XSD")
    @XmlID
    @GenericGenerator(name = "SDEC_TIPO_STRUT_UD_XSD_ID_TIPO_STRUT_UD_XSD_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_TIPO_STRUT_UD_XSD"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_TIPO_STRUT_UD_XSD_ID_TIPO_STRUT_UD_XSD_GENERATOR")
    public Long getIdTipoStrutUdXsd() {
	return this.idTipoStrutUdXsd;
    }

    public void setIdTipoStrutUdXsd(Long idTipoStrutUdXsd) {
	this.idTipoStrutUdXsd = idTipoStrutUdXsd;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_XSD_DATI_SPEC")
    public // @XmlInverseReference(mappedBy = "decTipoStrutUdXsds")
    DecXsdDatiSpec getDecXsdDatiSpec() {
	return this.decXsdDatiSpec;
    }

    public void setDecXsdDatiSpec(DecXsdDatiSpec decXsdDatiSpec) {
	this.decXsdDatiSpec = decXsdDatiSpec;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_STRUT_UNITA_DOC")
    @XmlInverseReference(mappedBy = "decTipoStrutUdXsds")
    public DecTipoStrutUnitaDoc getDecTipoStrutUnitaDoc() {
	return this.decTipoStrutUnitaDoc;
    }

    public void setDecTipoStrutUnitaDoc(DecTipoStrutUnitaDoc decTipoStrutUnitaDoc) {
	this.decTipoStrutUnitaDoc = decTipoStrutUnitaDoc;
    }
}
