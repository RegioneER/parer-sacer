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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the ARO_UD_APPART_VER_SERIE database table.
 */
@Entity
@Table(name = "ARO_UD_APPART_VER_SERIE")

public class AroUdAppartVerSerie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idUdAppartVerSerie;

    private String cdUdSerie;

    private String dsKeyOrdUdSerie;

    private Date dtUdSerie;

    private BigDecimal idVerIndiceAipUd;

    private String infoUdSerie;

    private BigDecimal pgUdSerie;

    private AroUnitaDoc aroUnitaDoc;

    private SerContenutoVerSerie serContenutoVerSerie;

    private SerVolVerSerie serVolVerSerie;

    public AroUdAppartVerSerie() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_UD_APPART_VER_SERIE")
    @GenericGenerator(name = "SARO_UD_APPART_VER_SERIE_ID_UD_APPART_VER_SERIE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SARO_UD_APPART_VER_SERIE"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SARO_UD_APPART_VER_SERIE_ID_UD_APPART_VER_SERIE_GENERATOR")
    public Long getIdUdAppartVerSerie() {
        return this.idUdAppartVerSerie;
    }

    public void setIdUdAppartVerSerie(Long idUdAppartVerSerie) {
        this.idUdAppartVerSerie = idUdAppartVerSerie;
    }

    @Column(name = "CD_UD_SERIE")
    public String getCdUdSerie() {
        return this.cdUdSerie;
    }

    public void setCdUdSerie(String cdUdSerie) {
        this.cdUdSerie = cdUdSerie;
    }

    @Column(name = "DS_KEY_ORD_UD_SERIE")
    public String getDsKeyOrdUdSerie() {
        return this.dsKeyOrdUdSerie;
    }

    public void setDsKeyOrdUdSerie(String dsKeyOrdUdSerie) {
        this.dsKeyOrdUdSerie = dsKeyOrdUdSerie;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_UD_SERIE")
    public Date getDtUdSerie() {
        return this.dtUdSerie;
    }

    public void setDtUdSerie(Date dtUdSerie) {
        this.dtUdSerie = dtUdSerie;
    }

    @Column(name = "ID_VER_INDICE_AIP_UD")
    public BigDecimal getIdVerIndiceAipUd() {
        return this.idVerIndiceAipUd;
    }

    public void setIdVerIndiceAipUd(BigDecimal idVerIndiceAipUd) {
        this.idVerIndiceAipUd = idVerIndiceAipUd;
    }

    @Column(name = "INFO_UD_SERIE")
    public String getInfoUdSerie() {
        return this.infoUdSerie;
    }

    public void setInfoUdSerie(String infoUdSerie) {
        this.infoUdSerie = infoUdSerie;
    }

    @Column(name = "PG_UD_SERIE")
    public BigDecimal getPgUdSerie() {
        return this.pgUdSerie;
    }

    public void setPgUdSerie(BigDecimal pgUdSerie) {
        this.pgUdSerie = pgUdSerie;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNITA_DOC")
    public AroUnitaDoc getAroUnitaDoc() {
        return this.aroUnitaDoc;
    }

    public void setAroUnitaDoc(AroUnitaDoc aroUnitaDoc) {
        this.aroUnitaDoc = aroUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONTENUTO_VER_SERIE")
    public SerContenutoVerSerie getSerContenutoVerSerie() {
        return this.serContenutoVerSerie;
    }

    public void setSerContenutoVerSerie(SerContenutoVerSerie serContenutoVerSerie) {
        this.serContenutoVerSerie = serContenutoVerSerie;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VOL_VER_SERIE")
    public SerVolVerSerie getSerVolVerSerie() {
        return this.serVolVerSerie;
    }

    public void setSerVolVerSerie(SerVolVerSerie serVolVerSerie) {
        this.serVolVerSerie = serVolVerSerie;
    }

}
