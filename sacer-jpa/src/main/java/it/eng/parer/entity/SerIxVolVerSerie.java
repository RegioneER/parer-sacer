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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the SER_IX_VOL_VER_SERIE database table.
 */
@Entity
@Table(name = "SER_IX_VOL_VER_SERIE")

public class SerIxVolVerSerie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idIxVolVerSerie;

    private String blIxVol;

    private String cdEncodingHashIxVol;

    private String cdVerXsdIxVol;

    private String dsAlgoHashIxVol;

    private String dsHashIxVol;

    private Date dtCreazione;

    private BigDecimal idStrut;

    private SerVolVerSerie serVolVerSerie;

    private List<SerUrnIxVolVerSerie> serUrnIxVolVerSeries = new ArrayList<>();

    public SerIxVolVerSerie() {
        // hibernate
    }

    @Id

    @Column(name = "ID_IX_VOL_VER_SERIE")
    @GenericGenerator(name = "SSER_IX_VOL_VER_SERIE_ID_IX_VOL_VER_SERIE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SSER_IX_VOL_VER_SERIE"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SSER_IX_VOL_VER_SERIE_ID_IX_VOL_VER_SERIE_GENERATOR")
    public Long getIdIxVolVerSerie() {
        return this.idIxVolVerSerie;
    }

    public void setIdIxVolVerSerie(Long idIxVolVerSerie) {
        this.idIxVolVerSerie = idIxVolVerSerie;
    }

    @Lob
    @Column(name = "BL_IX_VOL")
    public String getBlIxVol() {
        return this.blIxVol;
    }

    public void setBlIxVol(String blIxVol) {
        this.blIxVol = blIxVol;
    }

    @Column(name = "CD_ENCODING_HASH_IX_VOL")
    public String getCdEncodingHashIxVol() {
        return this.cdEncodingHashIxVol;
    }

    public void setCdEncodingHashIxVol(String cdEncodingHashIxVol) {
        this.cdEncodingHashIxVol = cdEncodingHashIxVol;
    }

    @Column(name = "CD_VER_XSD_IX_VOL")
    public String getCdVerXsdIxVol() {
        return this.cdVerXsdIxVol;
    }

    public void setCdVerXsdIxVol(String cdVerXsdIxVol) {
        this.cdVerXsdIxVol = cdVerXsdIxVol;
    }

    @Column(name = "DS_ALGO_HASH_IX_VOL")
    public String getDsAlgoHashIxVol() {
        return this.dsAlgoHashIxVol;
    }

    public void setDsAlgoHashIxVol(String dsAlgoHashIxVol) {
        this.dsAlgoHashIxVol = dsAlgoHashIxVol;
    }

    @Column(name = "DS_HASH_IX_VOL")
    public String getDsHashIxVol() {
        return this.dsHashIxVol;
    }

    public void setDsHashIxVol(String dsHashIxVol) {
        this.dsHashIxVol = dsHashIxVol;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE")
    public Date getDtCreazione() {
        return this.dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
        this.dtCreazione = dtCreazione;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VOL_VER_SERIE")
    public SerVolVerSerie getSerVolVerSerie() {
        return this.serVolVerSerie;
    }

    public void setSerVolVerSerie(SerVolVerSerie serVolVerSerie) {
        this.serVolVerSerie = serVolVerSerie;
    }

    @OneToMany(mappedBy = "serIxVolVerSerie", cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    public List<SerUrnIxVolVerSerie> getSerUrnIxVolVerSeries() {
        return this.serUrnIxVolVerSeries;
    }

    public void setSerUrnIxVolVerSeries(List<SerUrnIxVolVerSerie> serUrnIxVolVerSeries) {
        this.serUrnIxVolVerSeries = serUrnIxVolVerSeries;
    }
}
