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

package it.eng.parer.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;

/**
 * The persistent class for the OST_V_LIS_SUBPART_BLOB database table.
 *
 */
@Entity
@Table(name = "OST_V_LIS_SUBPART_BLOB_BY_ISTZ")

public class OstVLisSubpartBlobByIstz implements Serializable {

    private static final long serialVersionUID = 1L;
    private String cdPartition;
    private String cdSubPartition;
    private BigDecimal idSubPartition;
    private String mmMax;
    private String nmColonnaBlobFile;
    private String nmColonnaIdFile;
    private String nmTabellaFile;
    private BigDecimal numRows;
    private String status;
    private String tablespaceName;
    private String tiPartition;
    private BigDecimal niIstanzaJobPrepara;

    public OstVLisSubpartBlobByIstz() {/* Hibernate */
    }

    @Column(name = "CD_PARTITION")
    public String getCdPartition() {
        return this.cdPartition;
    }

    public void setCdPartition(String cdPartition) {
        this.cdPartition = cdPartition;
    }

    @Column(name = "CD_SUB_PARTITION")
    public String getCdSubPartition() {
        return this.cdSubPartition;
    }

    public void setCdSubPartition(String cdSubPartition) {
        this.cdSubPartition = cdSubPartition;
    }

    @Id
    @Column(name = "ID_SUB_PARTITION")
    public BigDecimal getIdSubPartition() {
        return this.idSubPartition;
    }

    public void setIdSubPartition(BigDecimal idSubPartition) {
        this.idSubPartition = idSubPartition;
    }

    @Column(name = "MM_MAX", columnDefinition = "number")
    public String getMmMax() {
        return this.mmMax;
    }

    public void setMmMax(String mmMax) {
        this.mmMax = mmMax;
    }

    @Column(name = "NM_COLONNA_BLOB_FILE")
    public String getNmColonnaBlobFile() {
        return this.nmColonnaBlobFile;
    }

    public void setNmColonnaBlobFile(String nmColonnaBlobFile) {
        this.nmColonnaBlobFile = nmColonnaBlobFile;
    }

    @Column(name = "NM_COLONNA_ID_FILE")
    public String getNmColonnaIdFile() {
        return this.nmColonnaIdFile;
    }

    public void setNmColonnaIdFile(String nmColonnaIdFile) {
        this.nmColonnaIdFile = nmColonnaIdFile;
    }

    @Column(name = "NM_TABELLA_FILE")
    public String getNmTabellaFile() {
        return this.nmTabellaFile;
    }

    public void setNmTabellaFile(String nmTabellaFile) {
        this.nmTabellaFile = nmTabellaFile;
    }

    @Column(name = "NUM_ROWS")
    public BigDecimal getNumRows() {
        return this.numRows;
    }

    public void setNumRows(BigDecimal numRows) {
        this.numRows = numRows;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "TABLESPACE_NAME")
    public String getTablespaceName() {
        return this.tablespaceName;
    }

    public void setTablespaceName(String tablespaceName) {
        this.tablespaceName = tablespaceName;
    }

    @Column(name = "TI_PARTITION")
    public String getTiPartition() {
        return this.tiPartition;
    }

    public void setTiPartition(String tiPartition) {
        this.tiPartition = tiPartition;
    }

    @Column(name = "NI_ISTANZA_JOB_PREPARA")
    public BigDecimal getNiIstanzaJobPrepara() {
        return this.niIstanzaJobPrepara;
    }

    public void setNiIstanzaJobPrepara(BigDecimal niIstanzaJobPrepara) {
        this.niIstanzaJobPrepara = niIstanzaJobPrepara;
    }

}
