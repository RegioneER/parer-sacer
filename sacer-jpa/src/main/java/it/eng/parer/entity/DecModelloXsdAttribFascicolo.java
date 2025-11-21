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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The persistent class for the DEC_MODELLO_XSD_ATTRIB_FASC database table.
 *
 */
@Entity
@Table(name = "DEC_MODELLO_XSD_ATTRIB_FASC")

public class DecModelloXsdAttribFascicolo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idModelloXsdAttribFasc;
    private DecAttribFascicolo decAttribFascicolo;
    private DecModelloXsdFascicolo decModelloXsdFascicolo;
    private BigDecimal niOrdAttrib;
    private String flObbl;
    private BigDecimal niCharMin;
    private BigDecimal niCharMax;
    private String dsValEnum;
    private String dsRegExp;
    private String cdLabel;

    public DecModelloXsdAttribFascicolo() {
        /* hibernate */
    }

    @Id
    @SequenceGenerator(name = "DEC_MODELLO_XSD_ATTRIB_FASC_IDMODELLOXSDATTRIBFASC_GENERATOR", sequenceName = "SDEC_MODELLO_XSD_ATTRIB_FASC", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEC_MODELLO_XSD_ATTRIB_FASC_IDMODELLOXSDATTRIBFASC_GENERATOR")
    @Column(name = "ID_MODELLO_XSD_ATTRIB_FASC")
    public Long getIdModelloXsdAttribFasc() {
        return this.idModelloXsdAttribFasc;
    }

    public void setIdModelloXsdAttribFasc(Long idModelloXsdAttribFasc) {
        this.idModelloXsdAttribFasc = idModelloXsdAttribFasc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ATTRIB_FASCICOLO")
    public DecAttribFascicolo getDecAttribFascicolo() {
        return this.decAttribFascicolo;
    }

    public void setDecAttribFascicolo(DecAttribFascicolo decAttribFascicolo) {
        this.decAttribFascicolo = decAttribFascicolo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MODELLO_XSD_FASCICOLO")
    public DecModelloXsdFascicolo getDecModelloXsdFascicolo() {
        return this.decModelloXsdFascicolo;
    }

    public void setDecModelloXsdFascicolo(DecModelloXsdFascicolo decModelloXsdFascicolo) {
        this.decModelloXsdFascicolo = decModelloXsdFascicolo;
    }

    @Column(name = "NI_ORD_ATTRIB")
    public BigDecimal getNiOrdAttrib() {
        return this.niOrdAttrib;
    }

    public void setNiOrdAttrib(BigDecimal niOrdAttrib) {
        this.niOrdAttrib = niOrdAttrib;
    }

    @Column(name = "FL_OBBL", columnDefinition = "CHAR")
    public String getCdXsd() {
        return this.flObbl;
    }

    public void setCdXsd(String flObbl) {
        this.flObbl = flObbl;
    }

    @Column(name = "NI_CHAR_MIN")
    public BigDecimal getNiCharMin() {
        return this.niCharMin;
    }

    public void setNiCharMin(BigDecimal niCharMin) {
        this.niCharMin = niCharMin;
    }

    @Column(name = "NI_CHAR_MAX")
    public BigDecimal getNiCharMax() {
        return this.niCharMax;
    }

    public void setNiCharMax(BigDecimal niCharMax) {
        this.niCharMax = niCharMax;
    }

    @Column(name = "DS_VAL_ENUM")
    public String getDsValEnum() {
        return this.dsValEnum;
    }

    public void setDsValEnum(String dsValEnum) {
        this.dsValEnum = dsValEnum;
    }

    @Column(name = "DS_REG_EXP")
    public String getDsRegExp() {
        return this.dsRegExp;
    }

    public void setDsRegExp(String dsRegExp) {
        this.dsRegExp = dsRegExp;
    }

    @Column(name = "CD_LABEL")
    public String getCdLabel() {
        return this.cdLabel;
    }

    public void setCdLabel(String cdLabel) {
        this.cdLabel = cdLabel;
    }

}
