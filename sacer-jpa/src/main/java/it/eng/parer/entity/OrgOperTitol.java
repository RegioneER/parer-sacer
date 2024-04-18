/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the ORG_OPER_TITOL database table.
 */
@Entity
@Table(name = "ORG_OPER_TITOL")
public class OrgOperTitol implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idOperTitol;

    private BigDecimal aaDocInvio;

    private String cdDocInvio;

    private String cdRegistroDocInvio;

    private Date dtDocInvio;

    private Date dtValOperTitol;

    private String tiOperTitol;

    private DecTitol decTitol;

    private OrgStrut orgStrut;

    private List<OrgOperVoceTitol> orgOperVoceTitols = new ArrayList<>();

    public OrgOperTitol() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_OPER_TITOL")
    @GenericGenerator(name = "SORG_OPER_TITOL_ID_OPER_TITOL_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SORG_OPER_TITOL"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SORG_OPER_TITOL_ID_OPER_TITOL_GENERATOR")
    public Long getIdOperTitol() {
        return this.idOperTitol;
    }

    public void setIdOperTitol(Long idOperTitol) {
        this.idOperTitol = idOperTitol;
    }

    @Column(name = "AA_DOC_INVIO")
    public BigDecimal getAaDocInvio() {
        return this.aaDocInvio;
    }

    public void setAaDocInvio(BigDecimal aaDocInvio) {
        this.aaDocInvio = aaDocInvio;
    }

    @Column(name = "CD_DOC_INVIO")
    public String getCdDocInvio() {
        return this.cdDocInvio;
    }

    public void setCdDocInvio(String cdDocInvio) {
        this.cdDocInvio = cdDocInvio;
    }

    @Column(name = "CD_REGISTRO_DOC_INVIO")
    public String getCdRegistroDocInvio() {
        return this.cdRegistroDocInvio;
    }

    public void setCdRegistroDocInvio(String cdRegistroDocInvio) {
        this.cdRegistroDocInvio = cdRegistroDocInvio;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_DOC_INVIO")
    public Date getDtDocInvio() {
        return this.dtDocInvio;
    }

    public void setDtDocInvio(Date dtDocInvio) {
        this.dtDocInvio = dtDocInvio;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_VAL_OPER_TITOL")
    public Date getDtValOperTitol() {
        return this.dtValOperTitol;
    }

    public void setDtValOperTitol(Date dtValOperTitol) {
        this.dtValOperTitol = dtValOperTitol;
    }

    @Column(name = "TI_OPER_TITOL")
    public String getTiOperTitol() {
        return this.tiOperTitol;
    }

    public void setTiOperTitol(String tiOperTitol) {
        this.tiOperTitol = tiOperTitol;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TITOL")
    public DecTitol getDecTitol() {
        return this.decTitol;
    }

    public void setDecTitol(DecTitol decTitol) {
        this.decTitol = decTitol;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    public OrgStrut getOrgStrut() {
        return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
        this.orgStrut = orgStrut;
    }

    @OneToMany(mappedBy = "orgOperTitol", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    public List<OrgOperVoceTitol> getOrgOperVoceTitols() {
        return this.orgOperVoceTitols;
    }

    public void setOrgOperVoceTitols(List<OrgOperVoceTitol> orgOperVoceTitols) {
        this.orgOperVoceTitols = orgOperVoceTitols;
    }

    public OrgOperVoceTitol addOrgOperVoceTitol(OrgOperVoceTitol orgOperVoceTitol) {
        getOrgOperVoceTitols().add(orgOperVoceTitol);
        orgOperVoceTitol.setOrgOperTitol(this);
        return orgOperVoceTitol;
    }

    public OrgOperVoceTitol removeOrgOperVoceTitol(OrgOperVoceTitol orgOperVoceTitol) {
        getOrgOperVoceTitols().remove(orgOperVoceTitol);
        orgOperVoceTitol.setOrgOperTitol(null);
        return orgOperVoceTitol;
    }
}
