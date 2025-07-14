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
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.EmbeddedId;

/**
 * The persistent class for the ELV_V_LISAIPUD_URNDACALC_BYELE database table.
 */
@Entity
@Table(name = "ELV_V_LISAIPUD_URNDACALC_BYELE")
public class ElvVLisaipudUrndacalcByele implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date dtVersMax;

    private Date dtCreazione;

    private String cdKeyUnitaDocNormaliz;

    public ElvVLisaipudUrndacalcByele() {
	/* Hibernate */
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_VERS_MAX")
    public Date getDtVersMax() {
	return this.dtVersMax;
    }

    public void setDtVersMax(Date dtVersMax) {
	this.dtVersMax = dtVersMax;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE")
    public Date getDtCreazione() {
	return this.dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
	this.dtCreazione = dtCreazione;
    }

    @Column(name = "CD_KEY_UNITA_DOC_NORMALIZ")
    public String getCdKeyUnitaDocNormaliz() {
	return this.cdKeyUnitaDocNormaliz;
    }

    public void setCdKeyUnitaDocNormaliz(String cdKeyUnitaDocNormaliz) {
	this.cdKeyUnitaDocNormaliz = cdKeyUnitaDocNormaliz;
    }

    private ElvVLisaipudUrndacalcByeleId id;

    @EmbeddedId()
    public ElvVLisaipudUrndacalcByeleId getId() {
	return id;
    }

    public void setId(ElvVLisaipudUrndacalcByeleId id) {
	this.id = id;
    }
}
