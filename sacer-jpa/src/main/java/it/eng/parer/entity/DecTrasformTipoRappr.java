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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the DEC_TRASFORM_TIPO_RAPPR database table.
 */
@Entity
@Table(name = "DEC_TRASFORM_TIPO_RAPPR")

public class DecTrasformTipoRappr implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idTrasformTipoRappr;

    private byte[] blFileTrasform;

    private String cdVersioneTrasform;

    private String dsHashFileTrasform;

    private Date dtInsTrasform;

    private Date dtLastModTrasform;

    private BigDecimal idCompDocTest;

    private String nmTrasform;

    private String tiStatoFileTrasform;

    private DecTipoRapprComp decTipoRapprComp;

    private List<DecImageTrasform> decImageTrasforms = new ArrayList<>();

    public DecTrasformTipoRappr() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_TRASFORM_TIPO_RAPPR")
    @XmlID
    @GenericGenerator(name = "SDEC_TRASFORM_TIPO_RAPPR_ID_TRASFORM_TIPO_RAPPR_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_TRASFORM_TIPO_RAPPR"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_TRASFORM_TIPO_RAPPR_ID_TRASFORM_TIPO_RAPPR_GENERATOR")
    public Long getIdTrasformTipoRappr() {
        return this.idTrasformTipoRappr;
    }

    public void setIdTrasformTipoRappr(Long idTrasformTipoRappr) {
        this.idTrasformTipoRappr = idTrasformTipoRappr;
    }

    @Lob
    @Column(name = "BL_FILE_TRASFORM")
    @XmlTransient
    public byte[] getBlFileTrasform() {
        return this.blFileTrasform;
    }

    public void setBlFileTrasform(byte[] blFileTrasform) {
        this.blFileTrasform = blFileTrasform;
    }

    @Column(name = "CD_VERSIONE_TRASFORM")
    public String getCdVersioneTrasform() {
        return this.cdVersioneTrasform;
    }

    public void setCdVersioneTrasform(String cdVersioneTrasform) {
        this.cdVersioneTrasform = cdVersioneTrasform;
    }

    @Column(name = "DS_HASH_FILE_TRASFORM")
    public String getDsHashFileTrasform() {
        return this.dsHashFileTrasform;
    }

    public void setDsHashFileTrasform(String dsHashFileTrasform) {
        this.dsHashFileTrasform = dsHashFileTrasform;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INS_TRASFORM")
    public Date getDtInsTrasform() {
        return this.dtInsTrasform;
    }

    public void setDtInsTrasform(Date dtInsTrasform) {
        this.dtInsTrasform = dtInsTrasform;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_LAST_MOD_TRASFORM")
    public Date getDtLastModTrasform() {
        return this.dtLastModTrasform;
    }

    public void setDtLastModTrasform(Date dtLastModTrasform) {
        this.dtLastModTrasform = dtLastModTrasform;
    }

    @Column(name = "ID_COMP_DOC_TEST")
    public BigDecimal getIdCompDocTest() {
        return this.idCompDocTest;
    }

    public void setIdCompDocTest(BigDecimal idCompDocTest) {
        this.idCompDocTest = idCompDocTest;
    }

    @Column(name = "NM_TRASFORM")
    public String getNmTrasform() {
        return this.nmTrasform;
    }

    public void setNmTrasform(String nmTrasform) {
        this.nmTrasform = nmTrasform;
    }

    @Column(name = "TI_STATO_FILE_TRASFORM", columnDefinition = "char")
    public String getTiStatoFileTrasform() {
        return this.tiStatoFileTrasform;
    }

    public void setTiStatoFileTrasform(String tiStatoFileTrasform) {
        this.tiStatoFileTrasform = tiStatoFileTrasform;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_RAPPR_COMP")
    @XmlInverseReference(mappedBy = "decTrasformTipoRapprs")
    public DecTipoRapprComp getDecTipoRapprComp() {
        return this.decTipoRapprComp;
    }

    public void setDecTipoRapprComp(DecTipoRapprComp decTipoRapprComp) {
        this.decTipoRapprComp = decTipoRapprComp;
    }

    @OneToMany(mappedBy = "decTrasformTipoRappr", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    @XmlTransient
    public List<DecImageTrasform> getDecImageTrasforms() {
        return this.decImageTrasforms;
    }

    public void setDecImageTrasforms(List<DecImageTrasform> decImageTrasforms) {
        this.decImageTrasforms = decImageTrasforms;
    }
}
