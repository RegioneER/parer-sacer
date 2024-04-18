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
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the OST_MIGRAZ_FILE database table.
 */
@Entity
@Table(name = "OST_MIGRAZ_FILE")

public class OstMigrazFile implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idMigrazFile;

    private String cdKeyFile;

    private BigDecimal idOggetto;

    private BigDecimal idStrut;

    private BigDecimal mmFile;

    private BigDecimal niMigrazErr;

    private String nmBucket;

    private String nmTabellaIdOggetto;

    private String nmTenant;

    private String tiStatoCor;

    private Date tsMigrato;

    private Date tsRegStatoCor;

    private Date tsVerificato;

    private OstMigrazSubPart ostMigrazSubPart;

    private List<OstMigrazFileErr> ostMigrazFileErrs = new ArrayList<>();

    public OstMigrazFile() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_MIGRAZ_FILE")
    @GenericGenerator(name = "SOST_MIGRAZ_FILE_ID_MIGRAZ_FILE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SOST_MIGRAZ_FILE"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SOST_MIGRAZ_FILE_ID_MIGRAZ_FILE_GENERATOR")
    public Long getIdMigrazFile() {
        return this.idMigrazFile;
    }

    public void setIdMigrazFile(Long idMigrazFile) {
        this.idMigrazFile = idMigrazFile;
    }

    @Column(name = "CD_KEY_FILE")
    public String getCdKeyFile() {
        return this.cdKeyFile;
    }

    public void setCdKeyFile(String cdKeyFile) {
        this.cdKeyFile = cdKeyFile;
    }

    @Column(name = "ID_OGGETTO")
    public BigDecimal getIdOggetto() {
        return this.idOggetto;
    }

    public void setIdOggetto(BigDecimal idOggetto) {
        this.idOggetto = idOggetto;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "MM_FILE")
    public BigDecimal getMmFile() {
        return this.mmFile;
    }

    public void setMmFile(BigDecimal mmFile) {
        this.mmFile = mmFile;
    }

    @Column(name = "NI_MIGRAZ_ERR")
    public BigDecimal getNiMigrazErr() {
        return this.niMigrazErr;
    }

    public void setNiMigrazErr(BigDecimal niMigrazErr) {
        this.niMigrazErr = niMigrazErr;
    }

    @Column(name = "NM_BUCKET")
    public String getNmBucket() {
        return this.nmBucket;
    }

    public void setNmBucket(String nmBucket) {
        this.nmBucket = nmBucket;
    }

    @Column(name = "NM_TABELLA_ID_OGGETTO")
    public String getNmTabellaIdOggetto() {
        return this.nmTabellaIdOggetto;
    }

    public void setNmTabellaIdOggetto(String nmTabellaIdOggetto) {
        this.nmTabellaIdOggetto = nmTabellaIdOggetto;
    }

    @Column(name = "NM_TENANT")
    public String getNmTenant() {
        return this.nmTenant;
    }

    public void setNmTenant(String nmTenant) {
        this.nmTenant = nmTenant;
    }

    @Column(name = "TI_STATO_COR")
    public String getTiStatoCor() {
        return this.tiStatoCor;
    }

    public void setTiStatoCor(String tiStatoCor) {
        this.tiStatoCor = tiStatoCor;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_MIGRATO")
    public Date getTsMigrato() {
        return this.tsMigrato;
    }

    public void setTsMigrato(Date tsMigrato) {
        this.tsMigrato = tsMigrato;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_REG_STATO_COR")
    public Date getTsRegStatoCor() {
        return this.tsRegStatoCor;
    }

    public void setTsRegStatoCor(Date tsRegStatoCor) {
        this.tsRegStatoCor = tsRegStatoCor;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_VERIFICATO")
    public Date getTsVerificato() {
        return this.tsVerificato;
    }

    public void setTsVerificato(Date tsVerificato) {
        this.tsVerificato = tsVerificato;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MIGRAZ_SUB_PART")
    public OstMigrazSubPart getOstMigrazSubPart() {
        return this.ostMigrazSubPart;
    }

    public void setOstMigrazSubPart(OstMigrazSubPart ostMigrazSubPart) {
        this.ostMigrazSubPart = ostMigrazSubPart;
    }

    @OneToMany(mappedBy = "ostMigrazFile")
    public List<OstMigrazFileErr> getOstMigrazFileErrs() {
        return this.ostMigrazFileErrs;
    }

    public void setOstMigrazFileErrs(List<OstMigrazFileErr> ostMigrazFileErrs) {
        this.ostMigrazFileErrs = ostMigrazFileErrs;
    }

    public OstMigrazFileErr addOstMigrazFileErr(OstMigrazFileErr ostMigrazFileErr) {
        getOstMigrazFileErrs().add(ostMigrazFileErr);
        ostMigrazFileErr.setOstMigrazFile(this);
        return ostMigrazFileErr;
    }

    public OstMigrazFileErr removeOstMigrazFileErr(OstMigrazFileErr ostMigrazFileErr) {
        getOstMigrazFileErrs().remove(ostMigrazFileErr);
        ostMigrazFileErr.setOstMigrazFile(null);
        return ostMigrazFileErr;
    }
}
