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
 * The persistent class for the ELV_ELENCO_VERS_UD_ANNUL database table.
 */
@Entity
@Table(name = "ELV_ELENCO_VERS_UD_ANNUL")

public class ElvElencoVersUdAnnul implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idElencoVersUdAnnul;

    private String dsUrnUnitaDocAnnul;

    private AroUnitaDoc aroUnitaDoc;

    private ElvElencoVer elvElencoVer;

    public ElvElencoVersUdAnnul() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_ELENCO_VERS_UD_ANNUL")
    @GenericGenerator(name = "SELV_ELENCO_VERS_UD_ANNUL_ID_ELENCO_VERS_UD_ANNUL_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SELV_ELENCO_VERS_UD_ANNUL"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SELV_ELENCO_VERS_UD_ANNUL_ID_ELENCO_VERS_UD_ANNUL_GENERATOR")
    public Long getIdElencoVersUdAnnul() {
        return this.idElencoVersUdAnnul;
    }

    public void setIdElencoVersUdAnnul(Long idElencoVersUdAnnul) {
        this.idElencoVersUdAnnul = idElencoVersUdAnnul;
    }

    @Column(name = "DS_URN_UNITA_DOC_ANNUL")
    public String getDsUrnUnitaDocAnnul() {
        return this.dsUrnUnitaDocAnnul;
    }

    public void setDsUrnUnitaDocAnnul(String dsUrnUnitaDocAnnul) {
        this.dsUrnUnitaDocAnnul = dsUrnUnitaDocAnnul;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNITA_DOC")
    public AroUnitaDoc getAroUnitaDoc() {
        return this.aroUnitaDoc;
    }

    public void setAroUnitaDoc(AroUnitaDoc aroUnitaDoc) {
        this.aroUnitaDoc = aroUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ELENCO_VERS")
    public ElvElencoVer getElvElencoVer() {
        return this.elvElencoVer;
    }

    public void setElvElencoVer(ElvElencoVer elvElencoVer) {
        this.elvElencoVer = elvElencoVer;
    }
}
