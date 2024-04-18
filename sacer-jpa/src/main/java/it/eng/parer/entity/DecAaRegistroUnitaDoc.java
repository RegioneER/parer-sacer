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
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the DEC_AA_REGISTRO_UNITA_DOC database table.
 */
@Entity
@Table(name = "DEC_AA_REGISTRO_UNITA_DOC")

public class DecAaRegistroUnitaDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idAaRegistroUnitaDoc;

    private BigDecimal aaMaxRegistroUnitaDoc;

    private BigDecimal aaMinRegistroUnitaDoc;

    private String cdFormatoNumero;

    private String dsFormatoNumero;

    private String flUpdFmtNumero;

    private DecRegistroUnitaDoc decRegistroUnitaDoc;

    private List<DecErrAaRegistroUnitaDoc> decErrAaRegistroUnitaDocs = new ArrayList<>();

    private List<DecWarnAaRegistroUd> decWarnAaRegistroUds = new ArrayList<>();

    private List<DecParteNumeroRegistro> decParteNumeroRegistros = new ArrayList<>();

    public DecAaRegistroUnitaDoc() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_AA_REGISTRO_UNITA_DOC")
    @XmlID
    @GenericGenerator(name = "SDEC_AA_REGISTRO_UNITA_DOC_ID_AA_REGISTRO_UNITA_DOC_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_AA_REGISTRO_UNITA_DOC"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_AA_REGISTRO_UNITA_DOC_ID_AA_REGISTRO_UNITA_DOC_GENERATOR")
    public Long getIdAaRegistroUnitaDoc() {
        return this.idAaRegistroUnitaDoc;
    }

    public void setIdAaRegistroUnitaDoc(Long idAaRegistroUnitaDoc) {
        this.idAaRegistroUnitaDoc = idAaRegistroUnitaDoc;
    }

    @Column(name = "AA_MAX_REGISTRO_UNITA_DOC")
    public BigDecimal getAaMaxRegistroUnitaDoc() {
        return this.aaMaxRegistroUnitaDoc;
    }

    public void setAaMaxRegistroUnitaDoc(BigDecimal aaMaxRegistroUnitaDoc) {
        this.aaMaxRegistroUnitaDoc = aaMaxRegistroUnitaDoc;
    }

    @Column(name = "AA_MIN_REGISTRO_UNITA_DOC")
    public BigDecimal getAaMinRegistroUnitaDoc() {
        return this.aaMinRegistroUnitaDoc;
    }

    public void setAaMinRegistroUnitaDoc(BigDecimal aaMinRegistroUnitaDoc) {
        this.aaMinRegistroUnitaDoc = aaMinRegistroUnitaDoc;
    }

    @Column(name = "CD_FORMATO_NUMERO")
    public String getCdFormatoNumero() {
        return this.cdFormatoNumero;
    }

    public void setCdFormatoNumero(String cdFormatoNumero) {
        this.cdFormatoNumero = cdFormatoNumero;
    }

    @Column(name = "DS_FORMATO_NUMERO")
    public String getDsFormatoNumero() {
        return this.dsFormatoNumero;
    }

    public void setDsFormatoNumero(String dsFormatoNumero) {
        this.dsFormatoNumero = dsFormatoNumero;
    }

    @Column(name = "FL_UPD_FMT_NUMERO", columnDefinition = "char(1)")
    public String getFlUpdFmtNumero() {
        return this.flUpdFmtNumero;
    }

    public void setFlUpdFmtNumero(String flUpdFmtNumero) {
        this.flUpdFmtNumero = flUpdFmtNumero;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_REGISTRO_UNITA_DOC")
    @XmlInverseReference(mappedBy = "decAaRegistroUnitaDocs")
    public DecRegistroUnitaDoc getDecRegistroUnitaDoc() {
        return this.decRegistroUnitaDoc;
    }

    public void setDecRegistroUnitaDoc(DecRegistroUnitaDoc decRegistroUnitaDoc) {
        this.decRegistroUnitaDoc = decRegistroUnitaDoc;
    }

    @OneToMany(mappedBy = "decAaRegistroUnitaDoc", cascade = CascadeType.REMOVE)
    @XmlTransient
    public List<DecErrAaRegistroUnitaDoc> getDecErrAaRegistroUnitaDocs() {
        return this.decErrAaRegistroUnitaDocs;
    }

    public void setDecErrAaRegistroUnitaDocs(List<DecErrAaRegistroUnitaDoc> decErrAaRegistroUnitaDocs) {
        this.decErrAaRegistroUnitaDocs = decErrAaRegistroUnitaDocs;
    }

    public DecErrAaRegistroUnitaDoc addDecErrAaRegistroUnitaDoc(DecErrAaRegistroUnitaDoc decErrAaRegistroUnitaDoc) {
        getDecErrAaRegistroUnitaDocs().add(decErrAaRegistroUnitaDoc);
        decErrAaRegistroUnitaDoc.setDecAaRegistroUnitaDoc(this);
        return decErrAaRegistroUnitaDoc;
    }

    public DecErrAaRegistroUnitaDoc removeDecErrAaRegistroUnitaDoc(DecErrAaRegistroUnitaDoc decErrAaRegistroUnitaDoc) {
        getDecErrAaRegistroUnitaDocs().remove(decErrAaRegistroUnitaDoc);
        decErrAaRegistroUnitaDoc.setDecAaRegistroUnitaDoc(null);
        return decErrAaRegistroUnitaDoc;
    }

    @OneToMany(mappedBy = "decAaRegistroUnitaDoc", cascade = CascadeType.REMOVE)
    @XmlTransient
    public List<DecWarnAaRegistroUd> getDecWarnAaRegistroUds() {
        return this.decWarnAaRegistroUds;
    }

    public void setDecWarnAaRegistroUds(List<DecWarnAaRegistroUd> decWarnAaRegistroUds) {
        this.decWarnAaRegistroUds = decWarnAaRegistroUds;
    }

    public DecWarnAaRegistroUd addDecWarnAaRegistroUd(DecWarnAaRegistroUd decWarnAaRegistroUd) {
        getDecWarnAaRegistroUds().add(decWarnAaRegistroUd);
        decWarnAaRegistroUd.setDecAaRegistroUnitaDoc(this);
        return decWarnAaRegistroUd;
    }

    public DecWarnAaRegistroUd removeDecWarnAaRegistroUd(DecWarnAaRegistroUd decWarnAaRegistroUd) {
        getDecWarnAaRegistroUds().remove(decWarnAaRegistroUd);
        decWarnAaRegistroUd.setDecAaRegistroUnitaDoc(null);
        return decWarnAaRegistroUd;
    }

    @OneToMany(mappedBy = "decAaRegistroUnitaDoc", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    public List<DecParteNumeroRegistro> getDecParteNumeroRegistros() {
        return this.decParteNumeroRegistros;
    }

    public void setDecParteNumeroRegistros(List<DecParteNumeroRegistro> decParteNumeroRegistros) {
        this.decParteNumeroRegistros = decParteNumeroRegistros;
    }

    public DecParteNumeroRegistro addDecParteNumeroRegistro(DecParteNumeroRegistro decParteNumeroRegistro) {
        getDecParteNumeroRegistros().add(decParteNumeroRegistro);
        decParteNumeroRegistro.setDecAaRegistroUnitaDoc(this);
        return decParteNumeroRegistro;
    }

    public DecParteNumeroRegistro removeDecParteNumeroRegistro(DecParteNumeroRegistro decParteNumeroRegistro) {
        getDecParteNumeroRegistros().remove(decParteNumeroRegistro);
        decParteNumeroRegistro.setDecAaRegistroUnitaDoc(null);
        return decParteNumeroRegistro;
    }
}
