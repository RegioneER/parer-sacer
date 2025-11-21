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
 * The persistent class for the ARO_NOTA_UNITA_DOC database table.
 *
 */
@Entity
@Table(name = "ARO_NOTA_UNITA_DOC")

public class AroNotaUnitaDoc implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idNotaUnitaDoc;
    private String dsNotaUnitaDoc;
    private Date dtNotaUnitaDoc;
    private BigDecimal pgNotaUnitaDoc;
    private IamUser iamUser;
    private AroUnitaDoc aroUnitaDoc;
    private AroVerIndiceAipUd aroVerIndiceAipUd;
    private DecTipoNotaUnitaDoc decTipoNotaUnitaDoc;

    public AroNotaUnitaDoc() {/* Hibernate */
    }

    @Id
    @GenericGenerator(name = "ARO_NOTA_UNITA_DOC_IDNOTAUNITADOC_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SARO_NOTA_UNITA_DOC"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ARO_NOTA_UNITA_DOC_IDNOTAUNITADOC_GENERATOR")
    @Column(name = "ID_NOTA_UNITA_DOC")
    public Long getIdNotaUnitaDoc() {
        return this.idNotaUnitaDoc;
    }

    public void setIdNotaUnitaDoc(Long idNotaUnitaDoc) {
        this.idNotaUnitaDoc = idNotaUnitaDoc;
    }

    @Column(name = "DS_NOTA_UNITA_DOC")
    public String getDsNotaUnitaDoc() {
        return this.dsNotaUnitaDoc;
    }

    public void setDsNotaUnitaDoc(String dsNotaUnitaDoc) {
        this.dsNotaUnitaDoc = dsNotaUnitaDoc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_NOTA_UNITA_DOC")
    public Date getDtNotaUnitaDoc() {
        return this.dtNotaUnitaDoc;
    }

    public void setDtNotaUnitaDoc(Date dtNotaUnitaDoc) {
        this.dtNotaUnitaDoc = dtNotaUnitaDoc;
    }

    @Column(name = "PG_NOTA_UNITA_DOC")
    public BigDecimal getPgNotaUnitaDoc() {
        return this.pgNotaUnitaDoc;
    }

    public void setPgNotaUnitaDoc(BigDecimal pgNotaUnitaDoc) {
        this.pgNotaUnitaDoc = pgNotaUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_IAM")
    public IamUser getIamUser() {
        return this.iamUser;
    }

    public void setIamUser(IamUser iamUser) {
        this.iamUser = iamUser;
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
    @JoinColumn(name = "ID_VER_INDICE_AIP")
    public AroVerIndiceAipUd getAroVerIndiceAipUd() {
        return this.aroVerIndiceAipUd;
    }

    public void setAroVerIndiceAipUd(AroVerIndiceAipUd aroVerIndiceAipUd) {
        this.aroVerIndiceAipUd = aroVerIndiceAipUd;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_NOTA_UNITA_DOC")
    public DecTipoNotaUnitaDoc getDecTipoNotaUnitaDoc() {
        return this.decTipoNotaUnitaDoc;
    }

    public void setDecTipoNotaUnitaDoc(DecTipoNotaUnitaDoc decTipoNotaUnitaDoc) {
        this.decTipoNotaUnitaDoc = decTipoNotaUnitaDoc;
    }

}
