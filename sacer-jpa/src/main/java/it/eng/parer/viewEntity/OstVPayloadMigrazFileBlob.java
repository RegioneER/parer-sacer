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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the OST_V_PAYLOAD_MIGRAZ_FILE_BLOB database table.
 *
 */
@Entity
@Table(name = "OST_V_PAYLOAD_MIGRAZ_FILE_BLOB")
public class OstVPayloadMigrazFileBlob implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dsUrnIniziale;
    private String dsUrnNormaliz;
    private String dsUrnOriginale;
    private Date dtCreazione;

    private BigDecimal idCompDoc;
    private BigDecimal niSizeFileCalc;

    public OstVPayloadMigrazFileBlob() {/* Hibernate */
    }

    @Column(name = "DS_URN_INIZIALE")
    public String getDsUrnIniziale() {
	return this.dsUrnIniziale;
    }

    public void setDsUrnIniziale(String dsUrnIniziale) {
	this.dsUrnIniziale = dsUrnIniziale;
    }

    @Column(name = "DS_URN_NORMALIZ")
    public String getDsUrnNormaliz() {
	return this.dsUrnNormaliz;
    }

    public void setDsUrnNormaliz(String dsUrnNormaliz) {
	this.dsUrnNormaliz = dsUrnNormaliz;
    }

    @Column(name = "DS_URN_ORIGINALE")
    public String getDsUrnOriginale() {
	return this.dsUrnOriginale;
    }

    public void setDsUrnOriginale(String dsUrnOriginale) {
	this.dsUrnOriginale = dsUrnOriginale;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_CREAZIONE")
    public Date getDtCreazione() {
	return this.dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
	this.dtCreazione = dtCreazione;
    }

    @Id
    @Column(name = "ID_COMP_DOC")
    public BigDecimal getIdCompDoc() {
	return this.idCompDoc;
    }

    public void setIdCompDoc(BigDecimal idCompDoc) {
	this.idCompDoc = idCompDoc;
    }

    @Column(name = "NI_SIZE_FILE_CALC")
    public BigDecimal getNiSizeFileCalc() {
	return this.niSizeFileCalc;
    }

    public void setNiSizeFileCalc(BigDecimal niSizeFileCalc) {
	this.niSizeFileCalc = niSizeFileCalc;
    }

}
