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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ARO_V_LIS_LINK_UNITA_DOC database table.
 */
@Entity
@Table(name = "ARO_V_LIS_LINK_UNITA_DOC")
public class AroVLisLinkUnitaDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal aaKeyUnitaDocLink;

    private String cdKeyUnitaDocLink;

    private String cdRegistroKeyUnitaDocLink;

    private String dsLinkUnitaDoc;

    private Date dtRegUnitaDoc;

    private String flRisolto;
    private BigDecimal idUnitaDocColleg;

    public AroVLisLinkUnitaDoc() {/* Hibernate */
    }

    @Column(name = "AA_KEY_UNITA_DOC_LINK")
    public BigDecimal getAaKeyUnitaDocLink() {
        return this.aaKeyUnitaDocLink;
    }

    public void setAaKeyUnitaDocLink(BigDecimal aaKeyUnitaDocLink) {
        this.aaKeyUnitaDocLink = aaKeyUnitaDocLink;
    }

    @Column(name = "CD_KEY_UNITA_DOC_LINK")
    public String getCdKeyUnitaDocLink() {
        return this.cdKeyUnitaDocLink;
    }

    public void setCdKeyUnitaDocLink(String cdKeyUnitaDocLink) {
        this.cdKeyUnitaDocLink = cdKeyUnitaDocLink;
    }

    @Column(name = "CD_REGISTRO_KEY_UNITA_DOC_LINK")
    public String getCdRegistroKeyUnitaDocLink() {
        return this.cdRegistroKeyUnitaDocLink;
    }

    public void setCdRegistroKeyUnitaDocLink(String cdRegistroKeyUnitaDocLink) {
        this.cdRegistroKeyUnitaDocLink = cdRegistroKeyUnitaDocLink;
    }

    @Column(name = "DS_LINK_UNITA_DOC")
    public String getDsLinkUnitaDoc() {
        return this.dsLinkUnitaDoc;
    }

    public void setDsLinkUnitaDoc(String dsLinkUnitaDoc) {
        this.dsLinkUnitaDoc = dsLinkUnitaDoc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_UNITA_DOC")
    public Date getDtRegUnitaDoc() {
        return this.dtRegUnitaDoc;
    }

    public void setDtRegUnitaDoc(Date dtRegUnitaDoc) {
        this.dtRegUnitaDoc = dtRegUnitaDoc;
    }

    @Column(name = "FL_RISOLTO", columnDefinition = "char(1)")
    public String getFlRisolto() {
        return this.flRisolto;
    }

    public void setFlRisolto(String flRisolto) {
        this.flRisolto = flRisolto;
    }

    @Column(name = "ID_UNITA_DOC_COLLEG")
    public BigDecimal getIdUnitaDocColleg() {
        return this.idUnitaDocColleg;
    }

    public void setIdUnitaDocColleg(BigDecimal idUnitaDocColleg) {
        this.idUnitaDocColleg = idUnitaDocColleg;
    }

    private AroVLisLinkUnitaDocId aroVLisLinkUnitaDocId;

    @EmbeddedId()
    public AroVLisLinkUnitaDocId getAroVLisLinkUnitaDocId() {
        return aroVLisLinkUnitaDocId;
    }

    public void setAroVLisLinkUnitaDocId(AroVLisLinkUnitaDocId aroVLisLinkUnitaDocId) {
        this.aroVLisLinkUnitaDocId = aroVLisLinkUnitaDocId;
    }
}
