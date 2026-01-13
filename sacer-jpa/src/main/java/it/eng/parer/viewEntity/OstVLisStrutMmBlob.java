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

package it.eng.parer.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the OST_V_LIS_STRUT_MM_BLOB database table.
 */
@Entity
@Table(name = "OST_V_LIS_STRUT_MM_BLOB")

public class OstVLisStrutMmBlob implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cdPartition;

    private String cdSubPartition;

    private Date dtVersFine;

    private Date dtVersIni;

    private String flFileAggiunti;

    private BigDecimal idPartition;

    public OstVLisStrutMmBlob() {/* Hibernate */
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

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_VERS_FINE")
    public Date getDtVersFine() {
        return this.dtVersFine;
    }

    public void setDtVersFine(Date dtVersFine) {
        this.dtVersFine = dtVersFine;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_VERS_INI")
    public Date getDtVersIni() {
        return this.dtVersIni;
    }

    public void setDtVersIni(Date dtVersIni) {
        this.dtVersIni = dtVersIni;
    }

    @Column(name = "FL_FILE_AGGIUNTI", columnDefinition = "char(1)")
    public String getFlFileAggiunti() {
        return this.flFileAggiunti;
    }

    public void setFlFileAggiunti(String flFileAggiunti) {
        this.flFileAggiunti = flFileAggiunti;
    }

    @Column(name = "ID_PARTITION")
    public BigDecimal getIdPartition() {
        return this.idPartition;
    }

    public void setIdPartition(BigDecimal idPartition) {
        this.idPartition = idPartition;
    }

    private OstVLisStrutMmBlobId ostVLisStrutMmBlobId;

    @EmbeddedId()
    public OstVLisStrutMmBlobId getOstVLisStrutMmBlobId() {
        return ostVLisStrutMmBlobId;
    }

    public void setOstVLisStrutMmBlobId(OstVLisStrutMmBlobId ostVLisStrutMmBlobId) {
        this.ostVLisStrutMmBlobId = ostVLisStrutMmBlobId;
    }
}
