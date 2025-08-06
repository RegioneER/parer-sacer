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
 * The persistent class for the ARO_UPD_UD_INDICE_AIP_DA_ELAB database table.
 */
@Entity
@Table(name = "ARO_UPD_UD_INDICE_AIP_DA_ELAB")

public class AroUpdUdIndiceAipDaElab implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idUpdUdIndiceAipDaElab;

    private AroIndiceAipUdDaElab aroIndiceAipUdDaElab;

    private AroUpdUnitaDoc aroUpdUnitaDoc;

    public AroUpdUdIndiceAipDaElab() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_UPD_UD_INDICE_AIP_DA_ELAB")
    @GenericGenerator(name = "SARO_UPD_UD_INDICE_AIP_DA_ELAB_ID_UPD_UD_INDICE_AIP_DA_ELAB_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SARO_UPD_UD_INDICE_AIP_DA_ELAB"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SARO_UPD_UD_INDICE_AIP_DA_ELAB_ID_UPD_UD_INDICE_AIP_DA_ELAB_GENERATOR")
    public Long getIdUpdUdIndiceAipDaElab() {
	return this.idUpdUdIndiceAipDaElab;
    }

    public void setIdUpdUdIndiceAipDaElab(Long idUpdUdIndiceAipDaElab) {
	this.idUpdUdIndiceAipDaElab = idUpdUdIndiceAipDaElab;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_INDICE_AIP_DA_ELAB")
    public AroIndiceAipUdDaElab getAroIndiceAipUdDaElab() {
	return this.aroIndiceAipUdDaElab;
    }

    public void setAroIndiceAipUdDaElab(AroIndiceAipUdDaElab aroIndiceAipUdDaElab) {
	this.aroIndiceAipUdDaElab = aroIndiceAipUdDaElab;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UPD_UNITA_DOC")
    public AroUpdUnitaDoc getAroUpdUnitaDoc() {
	return this.aroUpdUnitaDoc;
    }

    public void setAroUpdUnitaDoc(AroUpdUnitaDoc aroUpdUnitaDoc) {
	this.aroUpdUnitaDoc = aroUpdUnitaDoc;
    }
}
