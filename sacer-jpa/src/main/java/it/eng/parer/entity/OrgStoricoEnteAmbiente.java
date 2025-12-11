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
 * The persistent class for the ORG_STORICO_ENTE_AMBIENTE database table.
 */
@Entity
@Table(name = "ORG_STORICO_ENTE_AMBIENTE")

public class OrgStoricoEnteAmbiente implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idStoricoEnteAmbiente;

    private Date dtFinVal;

    private Date dtIniVal;

    private OrgAmbiente orgAmbiente;

    private OrgEnte orgEnte;

    public OrgStoricoEnteAmbiente() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_STORICO_ENTE_AMBIENTE")
    @GenericGenerator(name = "SORG_STORICO_ENTE_AMBIENTE_ID_STORICO_ENTE_AMBIENTE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SORG_STORICO_ENTE_AMBIENTE"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SORG_STORICO_ENTE_AMBIENTE_ID_STORICO_ENTE_AMBIENTE_GENERATOR")
    public Long getIdStoricoEnteAmbiente() {
        return this.idStoricoEnteAmbiente;
    }

    public void setIdStoricoEnteAmbiente(Long idStoricoEnteAmbiente) {
        this.idStoricoEnteAmbiente = idStoricoEnteAmbiente;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIN_VAL")
    public Date getDtFinVal() {
        return this.dtFinVal;
    }

    public void setDtFinVal(Date dtFinVal) {
        this.dtFinVal = dtFinVal;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL")
    public Date getDtIniVal() {
        return this.dtIniVal;
    }

    public void setDtIniVal(Date dtIniVal) {
        this.dtIniVal = dtIniVal;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AMBIENTE")
    public OrgAmbiente getOrgAmbiente() {
        return this.orgAmbiente;
    }

    public void setOrgAmbiente(OrgAmbiente orgAmbiente) {
        this.orgAmbiente = orgAmbiente;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENTE")
    public OrgEnte getOrgEnte() {
        return this.orgEnte;
    }

    public void setOrgEnte(OrgEnte orgEnte) {
        this.orgEnte = orgEnte;
    }
}
