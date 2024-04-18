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
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the DEC_SEL_CRITERIO_RAGGR_FASC database table.
 */
@Entity
@Cacheable(true)
@Table(name = "DEC_SEL_CRITERIO_RAGGR_FASC")

public class DecSelCriterioRaggrFasc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idSelCriterioRaggrFasc;

    private DecCriterioRaggrFasc decCriterioRaggrFasc;

    private String tiSel;

    private AplSistemaMigraz aplSistemaMigraz;

    private DecTipoFascicolo decTipoFascicolo;

    private DecVoceTitol decVoceTitol;

    public DecSelCriterioRaggrFasc() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_SEL_CRITERIO_RAGGR_FASC")
    @XmlID
    @GenericGenerator(name = "SDEC_SEL_CRITERIO_RAGGR_FASC_ID_SEL_CRITERIO_RAGGR_FASC_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_SEL_CRITERIO_RAGGR_FASC"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_SEL_CRITERIO_RAGGR_FASC_ID_SEL_CRITERIO_RAGGR_FASC_GENERATOR")
    public Long getIdSelCriterioRaggrFasc() {
        return this.idSelCriterioRaggrFasc;
    }

    public void setIdSelCriterioRaggrFasc(Long idSelCriterioRaggrFasc) {
        this.idSelCriterioRaggrFasc = idSelCriterioRaggrFasc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CRITERIO_RAGGR_FASC")
    public DecCriterioRaggrFasc getDecCriterioRaggrFasc() {
        return this.decCriterioRaggrFasc;
    }

    public void setDecCriterioRaggrFasc(DecCriterioRaggrFasc decCriterioRaggrFasc) {
        this.decCriterioRaggrFasc = decCriterioRaggrFasc;
    }

    @Column(name = "TI_SEL")
    public String getTiSel() {
        return this.tiSel;
    }

    public void setTiSel(String tiSel) {
        this.tiSel = tiSel;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SISTEMA_MIGRAZ")
    @XmlTransient
    public AplSistemaMigraz getAplSistemaMigraz() {
        return this.aplSistemaMigraz;
    }

    public void setAplSistemaMigraz(AplSistemaMigraz aplSistemaMigraz) {
        this.aplSistemaMigraz = aplSistemaMigraz;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_FASCICOLO")
    @XmlInverseReference(mappedBy = "decSelCriterioRaggrFascicoli")
    public DecTipoFascicolo getDecTipoFascicolo() {
        return this.decTipoFascicolo;
    }

    public void setDecTipoFascicolo(DecTipoFascicolo decTipoFascicolo) {
        this.decTipoFascicolo = decTipoFascicolo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VOCE_TITOL")
    @XmlTransient
    public DecVoceTitol getDecVoceTitol() {
        return this.decVoceTitol;
    }

    public void setDecVoceTitol(DecVoceTitol decVoceTitol) {
        this.decVoceTitol = decVoceTitol;
    }

}
