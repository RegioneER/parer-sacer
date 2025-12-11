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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the OST_STATO_MIGRAZ_SUB_PART database table.
 */
@Entity
@Table(name = "OST_STATO_MIGRAZ_SUB_PART")

public class OstStatoMigrazSubPart implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idStatoMigrazSubPart;

    private String tiStato;

    private Date tsRegStato;

    private OstMigrazSubPart ostMigrazSubPart;

    public OstStatoMigrazSubPart() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_STATO_MIGRAZ_SUB_PART")
    @GenericGenerator(name = "SOST_STATO_MIGRAZ_SUB_PART_ID_STATO_MIGRAZ_SUB_PART_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SOST_STATO_MIGRAZ_SUB_PART"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SOST_STATO_MIGRAZ_SUB_PART_ID_STATO_MIGRAZ_SUB_PART_GENERATOR")
    public Long getIdStatoMigrazSubPart() {
        return this.idStatoMigrazSubPart;
    }

    public void setIdStatoMigrazSubPart(Long idStatoMigrazSubPart) {
        this.idStatoMigrazSubPart = idStatoMigrazSubPart;
    }

    @Column(name = "TI_STATO")
    public String getTiStato() {
        return this.tiStato;
    }

    public void setTiStato(String tiStato) {
        this.tiStato = tiStato;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_REG_STATO")
    public Date getTsRegStato() {
        return this.tsRegStato;
    }

    public void setTsRegStato(Date tsRegStato) {
        this.tsRegStato = tsRegStato;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MIGRAZ_SUB_PART")
    public OstMigrazSubPart getOstMigrazSubPart() {
        return this.ostMigrazSubPart;
    }

    public void setOstMigrazSubPart(OstMigrazSubPart ostMigrazSubPart) {
        this.ostMigrazSubPart = ostMigrazSubPart;
    }
}
