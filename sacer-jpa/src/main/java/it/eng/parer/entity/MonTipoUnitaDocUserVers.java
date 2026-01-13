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
 * The persistent class for the MON_TIPO_UNITA_DOC_USER_VERS database table.
 */
@Entity
@Table(name = "MON_TIPO_UNITA_DOC_USER_VERS")

public class MonTipoUnitaDocUserVers implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idTipoUnitaDocUserVers;

    private Date dtRifConta;

    private BigDecimal niUnitaDocVers;

    private DecTipoUnitaDoc decTipoUnitaDoc;

    private IamUser iamUser;

    public MonTipoUnitaDocUserVers() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_TIPO_UNITA_DOC_USER_VERS")
    @GenericGenerator(name = "SMON_TIPO_UNITA_DOC_USER_VERS_ID_TIPO_UNITA_DOC_USER_VERS_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SMON_TIPO_UNITA_DOC_USER_VERS"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SMON_TIPO_UNITA_DOC_USER_VERS_ID_TIPO_UNITA_DOC_USER_VERS_GENERATOR")
    public Long getIdTipoUnitaDocUserVers() {
        return this.idTipoUnitaDocUserVers;
    }

    public void setIdTipoUnitaDocUserVers(Long idTipoUnitaDocUserVers) {
        this.idTipoUnitaDocUserVers = idTipoUnitaDocUserVers;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_RIF_CONTA")
    public Date getDtRifConta() {
        return this.dtRifConta;
    }

    public void setDtRifConta(Date dtRifConta) {
        this.dtRifConta = dtRifConta;
    }

    @Column(name = "NI_UNITA_DOC_VERS")
    public BigDecimal getNiUnitaDocVers() {
        return this.niUnitaDocVers;
    }

    public void setNiUnitaDocVers(BigDecimal niUnitaDocVers) {
        this.niUnitaDocVers = niUnitaDocVers;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_UNITA_DOC")
    public DecTipoUnitaDoc getDecTipoUnitaDoc() {
        return this.decTipoUnitaDoc;
    }

    public void setDecTipoUnitaDoc(DecTipoUnitaDoc decTipoUnitaDoc) {
        this.decTipoUnitaDoc = decTipoUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_IAM")
    public IamUser getIamUser() {
        return this.iamUser;
    }

    public void setIamUser(IamUser iamUser) {
        this.iamUser = iamUser;
    }
}
