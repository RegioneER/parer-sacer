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

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlID;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the DEC_CRITERIO_FILTRO_MULTIPLO database table.
 */
@Entity
@Cacheable(true)
@Table(name = "DEC_CRITERIO_FILTRO_MULTIPLO")
public class DecCriterioFiltroMultiplo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idCriterioFiltroMult;

    private String tiEsitoVerifFirme;

    private String nmSistemaMigraz;

    private String tiFiltroMultiplo;

    private DecCriterioRaggr decCriterioRaggr;

    private DecRegistroUnitaDoc decRegistroUnitaDoc;

    private DecRegistroUnitaDoc decRegistroRangeUnitaDoc;

    private DecTipoDoc decTipoDoc;

    private DecTipoUnitaDoc decTipoUnitaDoc;

    public DecCriterioFiltroMultiplo() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_CRITERIO_FILTRO_MULT")
    @XmlID
    @GenericGenerator(name = "SDEC_CRITERIO_FILTRO_MULTIPLO_ID_CRITERIO_FILTRO_MULT_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_CRITERIO_FILTRO_MULTIPLO"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_CRITERIO_FILTRO_MULTIPLO_ID_CRITERIO_FILTRO_MULT_GENERATOR")
    public Long getIdCriterioFiltroMult() {
        return this.idCriterioFiltroMult;
    }

    public void setIdCriterioFiltroMult(Long idCriterioFiltroMult) {
        this.idCriterioFiltroMult = idCriterioFiltroMult;
    }

    @Column(name = "NM_SISTEMA_MIGRAZ")
    public String getNmSistemaMigraz() {
        return this.nmSistemaMigraz;
    }

    public void setNmSistemaMigraz(String nmSistemaMigraz) {
        this.nmSistemaMigraz = nmSistemaMigraz;
    }

    @Column(name = "TI_ESITO_VERIF_FIRME")
    public String getTiEsitoVerifFirme() {
        return this.tiEsitoVerifFirme;
    }

    public void setTiEsitoVerifFirme(String tiEsitoVerifFirme) {
        this.tiEsitoVerifFirme = tiEsitoVerifFirme;
    }

    @Column(name = "TI_FILTRO_MULTIPLO")
    public String getTiFiltroMultiplo() {
        return this.tiFiltroMultiplo;
    }

    public void setTiFiltroMultiplo(String tiFiltroMultiplo) {
        this.tiFiltroMultiplo = tiFiltroMultiplo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CRITERIO_RAGGR")
    @XmlInverseReference(mappedBy = "decCriterioFiltroMultiplos")
    public DecCriterioRaggr getDecCriterioRaggr() {
        return this.decCriterioRaggr;
    }

    public void setDecCriterioRaggr(DecCriterioRaggr decCriterioRaggr) {
        this.decCriterioRaggr = decCriterioRaggr;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_REGISTRO_UNITA_DOC")
    @XmlInverseReference(mappedBy = "decCriterioFiltroMultiplos")
    public DecRegistroUnitaDoc getDecRegistroUnitaDoc() {
        return this.decRegistroUnitaDoc;
    }

    public void setDecRegistroUnitaDoc(DecRegistroUnitaDoc decRegistroUnitaDoc) {
        this.decRegistroUnitaDoc = decRegistroUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_REGISTRO_RANGE_UNITA_DOC")
    @XmlInverseReference(mappedBy = "decRangeCriterioFiltroMultiplos")
    public DecRegistroUnitaDoc getDecRegistroRangeUnitaDoc() {
        return this.decRegistroRangeUnitaDoc;
    }

    public void setDecRegistroRangeUnitaDoc(DecRegistroUnitaDoc decRegistroRangeUnitaDoc) {
        this.decRegistroRangeUnitaDoc = decRegistroRangeUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_DOC")
    @XmlInverseReference(mappedBy = "decCriterioFiltroMultiplos")
    public DecTipoDoc getDecTipoDoc() {
        return this.decTipoDoc;
    }

    public void setDecTipoDoc(DecTipoDoc decTipoDoc) {
        this.decTipoDoc = decTipoDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_UNITA_DOC")
    @XmlInverseReference(mappedBy = "decCriterioFiltroMultiplos")
    public DecTipoUnitaDoc getDecTipoUnitaDoc() {
        return this.decTipoUnitaDoc;
    }

    public void setDecTipoUnitaDoc(DecTipoUnitaDoc decTipoUnitaDoc) {
        this.decTipoUnitaDoc = decTipoUnitaDoc;
    }
}
