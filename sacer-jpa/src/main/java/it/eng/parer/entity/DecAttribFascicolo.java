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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The persistent class for the DEC_ATTRIB_FASCICOLO database table.
 *
 */
@Entity
@Table(name = "DEC_ATTRIB_FASCICOLO")
public class DecAttribFascicolo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idAttribFascicolo;
    private DecAaTipoFascicolo decAaTipoFascicolo;
    private String tiUsoAttrib;
    private DecTipoFascicolo decTipoFascicolo;
    private String nmSistemaMigraz;
    private String nmAttribFascicolo;
    private String dsAttribFascicolo;
    private String tiAttribFascicolo;
    private String tiVettorScalare;
    private String tiCharSep;
    private String tiCharFineRec;
    private BigDecimal idGruppoFascicolo;

    public DecAttribFascicolo() {
        /* hibernate */
    }

    @Id
    @SequenceGenerator(name = "DEC_ATTRIB_FASCICOLO_IDATTRIBFASCICOLO_GENERATOR", sequenceName = "SDEC_ATTRIB_FASCICOLO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEC_ATTRIB_FASCICOLO_IDATTRIBFASCICOLO_GENERATOR")
    @Column(name = "ID_ATTRIB_FASCICOLO")
    @XmlID
    public Long getIdAttribFascicolo() {
        return this.idAttribFascicolo;
    }

    public void setIdAttribFascicolo(Long idAttribFascicolo) {
        this.idAttribFascicolo = idAttribFascicolo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AA_TIPO_FASCICOLO")
    public DecAaTipoFascicolo getDecAaTipoFascicolo() {
        return this.decAaTipoFascicolo;
    }

    public void setDecAaTipoFascicolo(DecAaTipoFascicolo decAaTipoFascicolo) {
        this.decAaTipoFascicolo = decAaTipoFascicolo;
    }

    @Column(name = "TI_USO_ATTRIB")
    public String getTiUsoAttrib() {
        return this.tiUsoAttrib;
    }

    public void setTiUsoAttrib(String tiUsoAttrib) {
        this.tiUsoAttrib = tiUsoAttrib;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_FASCICOLO")
    @XmlTransient
    public DecTipoFascicolo getDecTipoFascicolo() {
        return this.decTipoFascicolo;
    }

    public void setDecTipoFascicolo(DecTipoFascicolo decTipoFascicolo) {
        this.decTipoFascicolo = decTipoFascicolo;
    }

    @Column(name = "NM_SISTEMA_MIGRAZ")
    public String getNmSistemaMigraz() {
        return this.nmSistemaMigraz;
    }

    public void setNmSistemaMigraz(String nmSistemaMigraz) {
        this.nmSistemaMigraz = nmSistemaMigraz;
    }

    @Column(name = "NM_ATTRIB_FASCICOLO")
    public String getNmAttribFascicolo() {
        return this.nmAttribFascicolo;
    }

    public void setNmAttribFascicolo(String nmAttribFascicolo) {
        this.nmAttribFascicolo = nmAttribFascicolo;
    }

    @Column(name = "DS_ATTRIB_FASCICOLO")
    public String getDsAttribFascicolo() {
        return this.dsAttribFascicolo;
    }

    public void setDsAttribFascicolo(String dsAttribFascicolo) {
        this.dsAttribFascicolo = dsAttribFascicolo;
    }

    @Column(name = "TI_ATTRIB_FASCICOLO")
    public String getTiAttribFascicolo() {
        return tiAttribFascicolo;
    }

    public void setTiAttribFascicolo(String tiAttribFascicolo) {
        this.tiAttribFascicolo = tiAttribFascicolo;
    }

    @Column(name = "TI_VETTOR_SCALARE")
    public String getTiVettorScalare() {
        return tiVettorScalare;
    }

    public void setTiVettorScalare(String tiVettorScalare) {
        this.tiVettorScalare = tiVettorScalare;
    }

    @Column(name = "TI_CHAR_SEP", columnDefinition = "CHAR")
    public String getTiCharSep() {
        return this.tiCharSep;
    }

    public void setTiCharSep(String tiCharSep) {
        this.tiCharSep = tiCharSep;
    }

    @Column(name = "TI_CHAR_FINE_REC", columnDefinition = "CHAR")
    public String getTiCharFineRec() {
        return this.tiCharFineRec;
    }

    public void setTiCharFineRec(String tiCharFineRec) {
        this.tiCharFineRec = tiCharFineRec;
    }

    @Column(name = "ID_GRUPPO_FASCICOLO")
    public BigDecimal getIdGruppoFascicolo() {
        return this.idGruppoFascicolo;
    }

    public void setIdGruppoFascicolo(BigDecimal idGruppoFascicolo) {
        this.idGruppoFascicolo = idGruppoFascicolo;
    }

    /**
     * Gestione dei default. Risulta la migliore pratica in quanto è indipendente dal db utilizzato
     * e sfrutta diretta JPA quindi calabile sotto ogni contesto in termini di ORM
     *
     * ref. https://stackoverflow.com/a/13432234
     */
    @PrePersist
    void preInsert() {
        if (this.tiVettorScalare == null) {
            this.tiVettorScalare = "SCALARE";
        }
    }
}
