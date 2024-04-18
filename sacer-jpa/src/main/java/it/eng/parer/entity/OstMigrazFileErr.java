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
 * The persistent class for the OST_MIGRAZ_FILE_ERR database table.
 */
@Entity
@Table(name = "OST_MIGRAZ_FILE_ERR")

public class OstMigrazFileErr implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idMigrazFileErr;

    private String cdErr;

    private String dsErr;

    private String dsHashFileCalcMigraz;

    private Date tsErr;

    private String tiErr;

    private OstMigrazFile ostMigrazFile;

    public OstMigrazFileErr() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_MIGRAZ_FILE_ERR")
    @GenericGenerator(name = "SOST_MIGRAZ_FILE_ERR_ID_MIGRAZ_FILE_ERR_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SOST_MIGRAZ_FILE_ERR"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SOST_MIGRAZ_FILE_ERR_ID_MIGRAZ_FILE_ERR_GENERATOR")
    public Long getIdMigrazFileErr() {
        return this.idMigrazFileErr;
    }

    public void setIdMigrazFileErr(Long idMigrazFileErr) {
        this.idMigrazFileErr = idMigrazFileErr;
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

    @Column(name = "DS_HASH_FILE_CALC_MIGRAZ")
    public String getDsHashFileCalcMigraz() {
        return this.dsHashFileCalcMigraz;
    }

    public void setDsHashFileCalcMigraz(String dsHashFileCalcMigraz) {
        this.dsHashFileCalcMigraz = dsHashFileCalcMigraz;
    }

    @Column(name = "TI_ERR")
    public String getTiErr() {
        return this.tiErr;
    }

    public void setTiErr(String tiErr) {
        this.tiErr = tiErr;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_ERR")
    public Date getTsErr() {
        return this.tsErr;
    }

    public void setTsErr(Date tsErr) {
        this.tsErr = tsErr;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MIGRAZ_FILE")
    public OstMigrazFile getOstMigrazFile() {
        return this.ostMigrazFile;
    }

    public void setOstMigrazFile(OstMigrazFile ostMigrazFile) {
        this.ostMigrazFile = ostMigrazFile;
    }
}
