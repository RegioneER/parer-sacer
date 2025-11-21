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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Cacheable;
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
import javax.xml.bind.annotation.XmlIDREF;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the DEC_TIPO_STRUT_UNITA_DOC database table.
 */
@Entity
@Cacheable(true)
@Table(name = "DEC_TIPO_STRUT_UNITA_DOC")
public class DecTipoStrutUnitaDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idTipoStrutUnitaDoc;

    private String dsTipoStrutUnitaDoc;

    private String dsDataTipoStrutUnitaDoc;

    private String dsOggTipoStrutUnitaDoc;

    private String dsNumeroTipoStrutUnitaDoc;

    private String dsAnnoTipoStrutUnitaDoc;

    private BigDecimal aaMinTipoStrutUnitaDoc;

    private BigDecimal aaMaxTipoStrutUnitaDoc;

    private String dsRifTempTipoStrutUd;

    private String dsCollegamentiUd;

    private String dsPeriodicitaVers;

    private String dsFirma;

    private Date dtIstituz;

    private Date dtSoppres;

    private String nmTipoStrutUnitaDoc;

    private List<DecTipoDocAmmesso> decTipoDocAmmessos = new ArrayList<>();

    private List<DecTipoStrutUdReg> decTipoStrutUdRegs = new ArrayList<>();

    private List<DecTipoStrutUdXsd> decTipoStrutUdXsds = new ArrayList<>();

    private List<DecTipoStrutUdSisVer> decTipoStrutUdSisVers = new ArrayList<>();

    private DecTipoUnitaDoc decTipoUnitaDoc;

    public DecTipoStrutUnitaDoc() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_TIPO_STRUT_UNITA_DOC")
    @GenericGenerator(name = "SDEC_TIPO_STRUT_UNITA_DOC_ID_TIPO_STRUT_UNITA_DOC_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_TIPO_STRUT_UNITA_DOC"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_TIPO_STRUT_UNITA_DOC_ID_TIPO_STRUT_UNITA_DOC_GENERATOR")
    public Long getIdTipoStrutUnitaDoc() {
        return this.idTipoStrutUnitaDoc;
    }

    public void setIdTipoStrutUnitaDoc(Long idTipoStrutUnitaDoc) {
        this.idTipoStrutUnitaDoc = idTipoStrutUnitaDoc;
    }

    @Column(name = "DS_TIPO_STRUT_UNITA_DOC")
    public String getDsTipoStrutUnitaDoc() {
        return this.dsTipoStrutUnitaDoc;
    }

    public void setDsTipoStrutUnitaDoc(String dsTipoStrutUnitaDoc) {
        this.dsTipoStrutUnitaDoc = dsTipoStrutUnitaDoc;
    }

    @Column(name = "DS_DATA_TIPO_STRUT_UNITA_DOC")
    public String getDsDataTipoStrutUnitaDoc() {
        return dsDataTipoStrutUnitaDoc;
    }

    public void setDsDataTipoStrutUnitaDoc(String dsDataTipoStrutUnitaDoc) {
        this.dsDataTipoStrutUnitaDoc = dsDataTipoStrutUnitaDoc;
    }

    @Column(name = "DS_OGG_TIPO_STRUT_UNITA_DOC")
    public String getDsOggTipoStrutUnitaDoc() {
        return dsOggTipoStrutUnitaDoc;
    }

    public void setDsOggTipoStrutUnitaDoc(String dsOggTipoStrutUnitaDoc) {
        this.dsOggTipoStrutUnitaDoc = dsOggTipoStrutUnitaDoc;
    }

    @Column(name = "DS_NUMERO_TIPO_STRUT_UNITA_DOC")
    public String getDsNumeroTipoStrutUnitaDoc() {
        return dsNumeroTipoStrutUnitaDoc;
    }

    public void setDsNumeroTipoStrutUnitaDoc(String dsNumeroTipoStrutUnitaDoc) {
        this.dsNumeroTipoStrutUnitaDoc = dsNumeroTipoStrutUnitaDoc;
    }

    @Column(name = "DS_ANNO_TIPO_STRUT_UNITA_DOC")
    public String getDsAnnoTipoStrutUnitaDoc() {
        return dsAnnoTipoStrutUnitaDoc;
    }

    public void setDsAnnoTipoStrutUnitaDoc(String dsAnnoTipoStrutUnitaDoc) {
        this.dsAnnoTipoStrutUnitaDoc = dsAnnoTipoStrutUnitaDoc;
    }

    @Column(name = "AA_MIN_TIPO_STRUT_UNITA_DOC")
    public BigDecimal getAaMinTipoStrutUnitaDoc() {
        return aaMinTipoStrutUnitaDoc;
    }

    public void setAaMinTipoStrutUnitaDoc(BigDecimal aaMinTipoStrutUnitaDoc) {
        this.aaMinTipoStrutUnitaDoc = aaMinTipoStrutUnitaDoc;
    }

    @Column(name = "AA_MAX_TIPO_STRUT_UNITA_DOC")
    public BigDecimal getAaMaxTipoStrutUnitaDoc() {
        return aaMaxTipoStrutUnitaDoc;
    }

    public void setAaMaxTipoStrutUnitaDoc(BigDecimal aaMaxTipoStrutUnitaDoc) {
        this.aaMaxTipoStrutUnitaDoc = aaMaxTipoStrutUnitaDoc;
    }

    @Column(name = "DS_RIF_TEMP_TIPO_STRUT_UD")
    public String getDsRifTempTipoStrutUd() {
        return dsRifTempTipoStrutUd;
    }

    public void setDsRifTempTipoStrutUd(String dsRifTempTipoStrutUd) {
        this.dsRifTempTipoStrutUd = dsRifTempTipoStrutUd;
    }

    @Column(name = "DS_COLLEGAMENTI_UD")
    public String getDsCollegamentiUd() {
        return dsCollegamentiUd;
    }

    public void setDsCollegamentiUd(String dsCollegamentiUd) {
        this.dsCollegamentiUd = dsCollegamentiUd;
    }

    @Column(name = "DS_PERIODICITA_VERS")
    public String getDsPeriodicitaVers() {
        return dsPeriodicitaVers;
    }

    public void setDsPeriodicitaVers(String dsPeriodicitaVers) {
        this.dsPeriodicitaVers = dsPeriodicitaVers;
    }

    @Column(name = "DS_FIRMA")
    public String getDsFirma() {
        return dsFirma;
    }

    public void setDsFirma(String dsFirma) {
        this.dsFirma = dsFirma;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ISTITUZ")
    public Date getDtIstituz() {
        return this.dtIstituz;
    }

    public void setDtIstituz(Date dtIstituz) {
        this.dtIstituz = dtIstituz;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SOPPRES")
    public Date getDtSoppres() {
        return this.dtSoppres;
    }

    public void setDtSoppres(Date dtSoppres) {
        this.dtSoppres = dtSoppres;
    }

    @Column(name = "NM_TIPO_STRUT_UNITA_DOC")
    public String getNmTipoStrutUnitaDoc() {
        return this.nmTipoStrutUnitaDoc;
    }

    public void setNmTipoStrutUnitaDoc(String nmTipoStrutUnitaDoc) {
        this.nmTipoStrutUnitaDoc = nmTipoStrutUnitaDoc;
    }

    @OneToMany(mappedBy = "decTipoStrutUnitaDoc", cascade = {
            CascadeType.PERSIST, CascadeType.REMOVE })
    @XmlIDREF
    public List<DecTipoDocAmmesso> getDecTipoDocAmmessos() {
        return this.decTipoDocAmmessos;
    }

    public void setDecTipoDocAmmessos(List<DecTipoDocAmmesso> decTipoDocAmmessos) {
        this.decTipoDocAmmessos = decTipoDocAmmessos;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_UNITA_DOC")
    @XmlInverseReference(mappedBy = "decTipoStrutUnitaDocs")
    public DecTipoUnitaDoc getDecTipoUnitaDoc() {
        return this.decTipoUnitaDoc;
    }

    public void setDecTipoUnitaDoc(DecTipoUnitaDoc decTipoUnitaDoc) {
        this.decTipoUnitaDoc = decTipoUnitaDoc;
    }

    @OneToMany(mappedBy = "decTipoStrutUnitaDoc", cascade = {
            CascadeType.PERSIST, CascadeType.REMOVE })
    public // @XmlIDREF
    List<DecTipoStrutUdSisVer> getDecTipoStrutUdSisVers() {
        return this.decTipoStrutUdSisVers;
    }

    public void setDecTipoStrutUdSisVers(List<DecTipoStrutUdSisVer> decTipoStrutUdSisVers) {
        this.decTipoStrutUdSisVers = decTipoStrutUdSisVers;
    }

    @OneToMany(mappedBy = "decTipoStrutUnitaDoc", cascade = {
            CascadeType.PERSIST, CascadeType.REMOVE })
    public // @XmlIDREF
    List<DecTipoStrutUdReg> getDecTipoStrutUdRegs() {
        return this.decTipoStrutUdRegs;
    }

    public void setDecTipoStrutUdRegs(List<DecTipoStrutUdReg> decTipoStrutUdRegs) {
        this.decTipoStrutUdRegs = decTipoStrutUdRegs;
    }

    @OneToMany(mappedBy = "decTipoStrutUnitaDoc", cascade = {
            CascadeType.PERSIST, CascadeType.REMOVE })
    public // @XmlIDREF
    List<DecTipoStrutUdXsd> getDecTipoStrutUdXsds() {
        return this.decTipoStrutUdXsds;
    }

    public void setDecTipoStrutUdXsds(List<DecTipoStrutUdXsd> decTipoStrutUdXsds) {
        this.decTipoStrutUdXsds = decTipoStrutUdXsds;
    }
}
