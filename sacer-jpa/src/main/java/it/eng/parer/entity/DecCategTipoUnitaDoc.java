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
import java.util.ArrayList;
import java.util.List;

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
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the DEC_CATEG_TIPO_UNITA_DOC database table.
 */
@Entity
@Table(name = "DEC_CATEG_TIPO_UNITA_DOC")
public class DecCategTipoUnitaDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idCategTipoUnitaDoc;

    private String cdCategTipoUnitaDoc;

    private String dsCategTipoUnitaDoc;

    private DecCategTipoUnitaDoc decCategTipoUnitaDoc;

    private List<DecCategTipoUnitaDoc> decCategTipoUnitaDocs = new ArrayList<>();

    private List<DecTipoUnitaDoc> decTipoUnitaDocs = new ArrayList<>();

    public DecCategTipoUnitaDoc() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_CATEG_TIPO_UNITA_DOC")
    @GenericGenerator(name = "SDEC_CATEG_TIPO_UNITA_DOC_ID_CATEG_TIPO_UNITA_DOC_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_CATEG_TIPO_UNITA_DOC"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_CATEG_TIPO_UNITA_DOC_ID_CATEG_TIPO_UNITA_DOC_GENERATOR")
    public Long getIdCategTipoUnitaDoc() {
        return this.idCategTipoUnitaDoc;
    }

    public void setIdCategTipoUnitaDoc(Long idCategTipoUnitaDoc) {
        this.idCategTipoUnitaDoc = idCategTipoUnitaDoc;
    }

    @Column(name = "CD_CATEG_TIPO_UNITA_DOC")
    public String getCdCategTipoUnitaDoc() {
        return this.cdCategTipoUnitaDoc;
    }

    public void setCdCategTipoUnitaDoc(String cdCategTipoUnitaDoc) {
        this.cdCategTipoUnitaDoc = cdCategTipoUnitaDoc;
    }

    @Column(name = "DS_CATEG_TIPO_UNITA_DOC")
    public String getDsCategTipoUnitaDoc() {
        return this.dsCategTipoUnitaDoc;
    }

    public void setDsCategTipoUnitaDoc(String dsCategTipoUnitaDoc) {
        this.dsCategTipoUnitaDoc = dsCategTipoUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CATEG_TIPO_UNITA_DOC_PADRE")
    @XmlTransient
    public DecCategTipoUnitaDoc getDecCategTipoUnitaDoc() {
        return this.decCategTipoUnitaDoc;
    }

    public void setDecCategTipoUnitaDoc(DecCategTipoUnitaDoc decCategTipoUnitaDoc) {
        this.decCategTipoUnitaDoc = decCategTipoUnitaDoc;
    }

    @OneToMany(mappedBy = "decCategTipoUnitaDoc")
    public List<DecCategTipoUnitaDoc> getDecCategTipoUnitaDocs() {
        return this.decCategTipoUnitaDocs;
    }

    public void setDecCategTipoUnitaDocs(List<DecCategTipoUnitaDoc> decCategTipoUnitaDocs) {
        this.decCategTipoUnitaDocs = decCategTipoUnitaDocs;
    }

    @OneToMany(mappedBy = "decCategTipoUnitaDoc")
    @XmlInverseReference(mappedBy = "decCategTipoUnitaDoc")
    public // @XmlTransient
    List<DecTipoUnitaDoc> getDecTipoUnitaDocs() {
        return this.decTipoUnitaDocs;
    }

    public void setDecTipoUnitaDocs(List<DecTipoUnitaDoc> decTipoUnitaDocs) {
        this.decTipoUnitaDocs = decTipoUnitaDocs;
    }
}
