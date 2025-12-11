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
 * The persistent class for the ARO_FILE_VER_INDICE_AIP_UD database table.
 */
@Entity
@Table(name = "ARO_FILE_VER_INDICE_AIP_UD")
public class AroFileVerIndiceAipUd implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idFileVerIndiceAip;

    private String blFileVerIndiceAip;

    private BigDecimal idStrut;

    private BigDecimal mmCreazione;

    private AroVerIndiceAipUd aroVerIndiceAipUd;

    public AroFileVerIndiceAipUd() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_FILE_VER_INDICE_AIP")
    @GenericGenerator(name = "SARO_FILE_VER_INDICE_AIP_UD_ID_FILE_VER_INDICE_AIP_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SARO_FILE_VER_INDICE_AIP_UD"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SARO_FILE_VER_INDICE_AIP_UD_ID_FILE_VER_INDICE_AIP_GENERATOR")
    public Long getIdFileVerIndiceAip() {
        return this.idFileVerIndiceAip;
    }

    public void setIdFileVerIndiceAip(Long idFileVerIndiceAip) {
        this.idFileVerIndiceAip = idFileVerIndiceAip;
    }

    @Lob()
    @Column(name = "BL_FILE_VER_INDICE_AIP")
    public String getBlFileVerIndiceAip() {
        return this.blFileVerIndiceAip;
    }

    public void setBlFileVerIndiceAip(String blFileVerIndiceAip) {
        this.blFileVerIndiceAip = blFileVerIndiceAip;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "MM_CREAZIONE")
    public BigDecimal getMmCreazione() {
        return this.mmCreazione;
    }

    public void setMmCreazione(BigDecimal mmCreazione) {
        this.mmCreazione = mmCreazione;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VER_INDICE_AIP")
    public AroVerIndiceAipUd getAroVerIndiceAipUd() {
        return this.aroVerIndiceAipUd;
    }

    public void setAroVerIndiceAipUd(AroVerIndiceAipUd aroVerIndiceAipUd) {
        this.aroVerIndiceAipUd = aroVerIndiceAipUd;
    }
}
