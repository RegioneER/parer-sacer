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
 * The persistent class for the ARO_V_VIS_COMP_VOL database table.
 *
 */
@Entity
@Table(name = "ARO_V_VIS_COMP_VOL")

public class AroVVisCompVol implements Serializable {

    private static final long serialVersionUID = 1L;
    private String dsEsitoVerifFirmeChius;
    private Date dtChiusVolume;
    private BigDecimal idCompDoc;
    private BigDecimal idVolumeConserv;
    private String nmVolumeConserv;
    private String tiEsitoVerifFirmeChius;
    private String tiStatoVolumeConserv;

    public AroVVisCompVol() {/* Hibernate */
    }

    @Column(name = "DS_ESITO_VERIF_FIRME_CHIUS")
    public String getDsEsitoVerifFirmeChius() {
	return this.dsEsitoVerifFirmeChius;
    }

    public void setDsEsitoVerifFirmeChius(String dsEsitoVerifFirmeChius) {
	this.dsEsitoVerifFirmeChius = dsEsitoVerifFirmeChius;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CHIUS_VOLUME")
    public Date getDtChiusVolume() {
	return this.dtChiusVolume;
    }

    public void setDtChiusVolume(Date dtChiusVolume) {
	this.dtChiusVolume = dtChiusVolume;
    }

    @Id
    @Column(name = "ID_COMP_DOC")
    public BigDecimal getIdCompDoc() {
	return this.idCompDoc;
    }

    public void setIdCompDoc(BigDecimal idCompDoc) {
	this.idCompDoc = idCompDoc;
    }

    @Column(name = "ID_VOLUME_CONSERV")
    public BigDecimal getIdVolumeConserv() {
	return this.idVolumeConserv;
    }

    public void setIdVolumeConserv(BigDecimal idVolumeConserv) {
	this.idVolumeConserv = idVolumeConserv;
    }

    @Column(name = "NM_VOLUME_CONSERV")
    public String getNmVolumeConserv() {
	return this.nmVolumeConserv;
    }

    public void setNmVolumeConserv(String nmVolumeConserv) {
	this.nmVolumeConserv = nmVolumeConserv;
    }

    @Column(name = "TI_ESITO_VERIF_FIRME_CHIUS")
    public String getTiEsitoVerifFirmeChius() {
	return this.tiEsitoVerifFirmeChius;
    }

    public void setTiEsitoVerifFirmeChius(String tiEsitoVerifFirmeChius) {
	this.tiEsitoVerifFirmeChius = tiEsitoVerifFirmeChius;
    }

    @Column(name = "TI_STATO_VOLUME_CONSERV")
    public String getTiStatoVolumeConserv() {
	return this.tiStatoVolumeConserv;
    }

    public void setTiStatoVolumeConserv(String tiStatoVolumeConserv) {
	this.tiStatoVolumeConserv = tiStatoVolumeConserv;
    }

}
