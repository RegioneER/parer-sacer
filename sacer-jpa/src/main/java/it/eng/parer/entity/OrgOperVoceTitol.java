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
 * The persistent class for the ORG_OPER_VOCE_TITOL database table.
 */
@Entity
@Table(name = "ORG_OPER_VOCE_TITOL")
public class OrgOperVoceTitol implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idOperVoceTitol;

    private String dlNote;

    private Date dtValOperVoceTitol;

    private BigDecimal pgOperVoceTitol;

    private String tiOperVoceTitol;

    private DecVoceTitol decVoceTitol;

    private OrgOperTitol orgOperTitol;

    public OrgOperVoceTitol() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_OPER_VOCE_TITOL")
    @GenericGenerator(name = "SORG_OPER_VOCE_TITOL_ID_OPER_VOCE_TITOL_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SORG_OPER_VOCE_TITOL"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SORG_OPER_VOCE_TITOL_ID_OPER_VOCE_TITOL_GENERATOR")
    public Long getIdOperVoceTitol() {
        return this.idOperVoceTitol;
    }

    public void setIdOperVoceTitol(Long idOperVoceTitol) {
        this.idOperVoceTitol = idOperVoceTitol;
    }

    @Column(name = "DL_NOTE")
    public String getDlNote() {
        return this.dlNote;
    }

    public void setDlNote(String dlNote) {
        this.dlNote = dlNote;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_VAL_OPER_VOCE_TITOL")
    public Date getDtValOperVoceTitol() {
        return this.dtValOperVoceTitol;
    }

    public void setDtValOperVoceTitol(Date dtValOperVoceTitol) {
        this.dtValOperVoceTitol = dtValOperVoceTitol;
    }

    @Column(name = "PG_OPER_VOCE_TITOL")
    public BigDecimal getPgOperVoceTitol() {
        return this.pgOperVoceTitol;
    }

    public void setPgOperVoceTitol(BigDecimal pgOperVoceTitol) {
        this.pgOperVoceTitol = pgOperVoceTitol;
    }

    @Column(name = "TI_OPER_VOCE_TITOL")
    public String getTiOperVoceTitol() {
        return this.tiOperVoceTitol;
    }

    public void setTiOperVoceTitol(String tiOperVoceTitol) {
        this.tiOperVoceTitol = tiOperVoceTitol;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VOCE_TITOL")
    public DecVoceTitol getDecVoceTitol() {
        return this.decVoceTitol;
    }

    public void setDecVoceTitol(DecVoceTitol decVoceTitol) {
        this.decVoceTitol = decVoceTitol;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_OPER_TITOL", nullable = false)
    public OrgOperTitol getOrgOperTitol() {
        return this.orgOperTitol;
    }

    public void setOrgOperTitol(OrgOperTitol orgOperTitol) {
        this.orgOperTitol = orgOperTitol;
    }

}
