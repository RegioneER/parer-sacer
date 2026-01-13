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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import it.eng.parer.entity.constraint.HsmVerSerieSessioneFirma.TiEsitoFirmaVerSerie;

/**
 * The persistent class for the HSM_VER_SERIE_SESSIONE_FIRMA database table.
 */
@Entity
@Table(name = "HSM_VER_SERIE_SESSIONE_FIRMA")

public class HsmVerSerieSessioneFirma implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idVerSerieSessioneFirma;

    private String cdErr;

    private String dsErr;

    private TiEsitoFirmaVerSerie tiEsito;

    private Date tsEsito;

    private HsmSessioneFirma hsmSessioneFirma;

    private SerVerSerie serVerSerie;

    public HsmVerSerieSessioneFirma() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_VER_SERIE_SESSIONE_FIRMA")
    @GenericGenerator(name = "SHSM_VER_SERIE_SESSIONE_FIRMA_ID_VER_SERIE_SESSIONE_FIRMA_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SHSM_VER_SERIE_SESSIONE_FIRMA"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SHSM_VER_SERIE_SESSIONE_FIRMA_ID_VER_SERIE_SESSIONE_FIRMA_GENERATOR")
    public Long getIdVerSerieSessioneFirma() {
        return this.idVerSerieSessioneFirma;
    }

    public void setIdVerSerieSessioneFirma(Long idVerSerieSessioneFirma) {
        this.idVerSerieSessioneFirma = idVerSerieSessioneFirma;
    }

    @Column(name = "CD_ERR")
    public String getCdErr() {
        return this.cdErr;
    }

    public void setCdErr(String cdErr) {
        this.cdErr = cdErr;
    }

    @Column(name = "DS_ERR")
    public String getDsErr() {
        return this.dsErr;
    }

    public void setDsErr(String dsErr) {
        this.dsErr = dsErr;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_ESITO")
    public TiEsitoFirmaVerSerie getTiEsito() {
        return this.tiEsito;
    }

    public void setTiEsito(TiEsitoFirmaVerSerie tiEsito) {
        this.tiEsito = tiEsito;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_ESITO")
    public Date getTsEsito() {
        return this.tsEsito;
    }

    public void setTsEsito(Date tsEsito) {
        this.tsEsito = tsEsito;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SESSIONE_FIRMA")
    public HsmSessioneFirma getHsmSessioneFirma() {
        return this.hsmSessioneFirma;
    }

    public void setHsmSessioneFirma(HsmSessioneFirma hsmSessioneFirma) {
        this.hsmSessioneFirma = hsmSessioneFirma;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VER_SERIE")
    public SerVerSerie getSerVerSerie() {
        return this.serVerSerie;
    }

    public void setSerVerSerie(SerVerSerie serVerSerie) {
        this.serVerSerie = serVerSerie;
    }

    @Transient
    public boolean is2sign() {
        return this.tiEsito.equals(TiEsitoFirmaVerSerie.DA_FARE);
    }

    @Transient
    public boolean isSigned() {
        return this.tiEsito.equals(TiEsitoFirmaVerSerie.OK);
    }
}
