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
 * The persistent class for the FAS_XSD_META_VER_AIP_FASC database table.
 */
@Entity
@Table(name = "FAS_XSD_META_VER_AIP_FASC")
public class FasXsdMetaVerAipFasc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idXsdMetaVerAipFasc;

    private String nmXsd;

    private FasMetaVerAipFascicolo fasMetaVerAipFascicolo;

    private DecModelloXsdFascicolo decModelloXsdFascicolo;

    public FasXsdMetaVerAipFasc() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_XSD_META_VER_AIP_FASC")
    @GenericGenerator(name = "SFAS_XSD_META_VER_AIP_FASC_ID_XSD_META_VER_AIP_FASC_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SFAS_XSD_META_VER_AIP_FASC"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SFAS_XSD_META_VER_AIP_FASC_ID_XSD_META_VER_AIP_FASC_GENERATOR")
    public Long getIdXsdMetaVerAipFasc() {
	return this.idXsdMetaVerAipFasc;
    }

    public void setIdXsdMetaVerAipFasc(Long idXsdMetaVerAipFasc) {
	this.idXsdMetaVerAipFasc = idXsdMetaVerAipFasc;
    }

    @Column(name = "NM_XSD")
    public String getNmXsd() {
	return this.nmXsd;
    }

    public void setNmXsd(String nmXsd) {
	this.nmXsd = nmXsd;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_META_VER_AIP_FASCICOLO")
    public FasMetaVerAipFascicolo getFasMetaVerAipFascicolo() {
	return this.fasMetaVerAipFascicolo;
    }

    public void setFasMetaVerAipFascicolo(FasMetaVerAipFascicolo fasMetaVerAipFascicolo) {
	this.fasMetaVerAipFascicolo = fasMetaVerAipFascicolo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MODELLO_XSD_FASCICOLO")
    public DecModelloXsdFascicolo getDecModelloXsdFascicolo() {
	return this.decModelloXsdFascicolo;
    }

    public void setDecModelloXsdFascicolo(DecModelloXsdFascicolo decModelloXsdFascicolo) {
	this.decModelloXsdFascicolo = decModelloXsdFascicolo;
    }
}
