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
 * The persistent class for the ELV_V_LIS_MODIF_BY_UD database table.
 *
 */
@Entity
@Table(name = "ELV_V_LIS_MODIF_BY_UD")

public class ElvVLisModifByUd implements Serializable {

    private static final long serialVersionUID = 1L;
    private String tiModif;
    private BigDecimal idElencoVers;
    private Date dtVers;
    private BigDecimal idUnitaDoc;
    private BigDecimal idModif;
    private String dsUrnModif;
    private String dsUrnRappVers;
    private String dsHashRappVers;
    private String dsAlgoHashRappVers;
    private String cdEncodingHashRappVers;

    public ElvVLisModifByUd() {/* Hibernate */
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_VERS")
    public Date getDtVers() {
	return this.dtVers;
    }

    public void setDtVers(Date dtVers) {
	this.dtVers = dtVers;
    }

    @Column(name = "TI_MODIF")
    public String getTiModif() {
	return this.tiModif;
    }

    public void setTiModif(String tiModif) {
	this.tiModif = tiModif;
    }

    @Column(name = "ID_ELENCO_VERS")
    public BigDecimal getIdElencoVers() {
	return this.idElencoVers;
    }

    public void setIdElencoVers(BigDecimal idElencoVers) {
	this.idElencoVers = idElencoVers;
    }

    @Column(name = "DS_URN_MODIF")
    public String getDsUrnModif() {
	return this.dsUrnModif;
    }

    public void setDsUrnModif(String dsUrnModif) {
	this.dsUrnModif = dsUrnModif;
    }

    @Column(name = "DS_URN_RAPP_VERS")
    public String getDsUrnRappVers() {
	return this.dsUrnRappVers;
    }

    public void setDsUrnRappVers(String dsUrnRappVers) {
	this.dsUrnRappVers = dsUrnRappVers;
    }

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
	return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
	this.idUnitaDoc = idUnitaDoc;
    }

    @Id
    @Column(name = "ID_MODIF")
    public BigDecimal getIdModif() {
	return this.idModif;
    }

    public void setIdModif(BigDecimal idModif) {
	this.idModif = idModif;
    }

    @Column(name = "DS_HASH_RAPP_VERS")
    public String getDsHashRappVers() {
	return this.dsHashRappVers;
    }

    public void setDsHashRappVers(String dsHashRappVers) {
	this.dsHashRappVers = dsHashRappVers;
    }

    @Column(name = "DS_ALGO_HASH_RAPP_VERS")
    public String getDsAlgoHashRappVers() {
	return this.dsAlgoHashRappVers;
    }

    public void setDsAlgoHashRappVers(String dsAlgoHashRappVers) {
	this.dsAlgoHashRappVers = dsAlgoHashRappVers;
    }

    @Column(name = "CD_ENCODING_HASH_RAPP_VERS")
    public String getCdEncodingHashRappVers() {
	return this.cdEncodingHashRappVers;
    }

    public void setCdEncodingHashRappVers(String cdEncodingHashRappVers) {
	this.cdEncodingHashRappVers = cdEncodingHashRappVers;
    }
}
