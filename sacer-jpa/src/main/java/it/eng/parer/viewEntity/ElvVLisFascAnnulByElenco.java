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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;

/**
 * The persistent class for the ELV_V_LIS_FASC_ANNUL_BY_ELENCO database table.
 *
 */
@Entity
@Table(name = "ELV_V_LIS_FASC_ANNUL_BY_ELENCO")

public class ElvVLisFascAnnulByElenco implements Serializable {

    private static final long serialVersionUID = 1L;
    private String dsUrnFascicoloAnnul;
    private BigDecimal idElencoVersFasc;
    private BigDecimal idFascicolo;
    private BigDecimal idRichAnnulVers;

    public ElvVLisFascAnnulByElenco() {/* Hibernate */
    }

    @Column(name = "DS_URN_FASCICOLO_ANNUL")
    public String getDsUrnFascicoloAnnul() {
	return this.dsUrnFascicoloAnnul;
    }

    public void setDsUrnFascicoloAnnul(String dsUrnFascicoloAnnul) {
	this.dsUrnFascicoloAnnul = dsUrnFascicoloAnnul;
    }

    @Column(name = "ID_ELENCO_VERS_FASC")
    public BigDecimal getIdElencoVersFasc() {
	return this.idElencoVersFasc;
    }

    public void setIdElencoVersFasc(BigDecimal idElencoVersFasc) {
	this.idElencoVersFasc = idElencoVersFasc;
    }

    @Id
    @Column(name = "ID_FASCICOLO")
    public BigDecimal getIdFascicolo() {
	return this.idFascicolo;
    }

    public void setIdFascicolo(BigDecimal idFascicolo) {
	this.idFascicolo = idFascicolo;
    }

    @Column(name = "ID_RICH_ANNUL_VERS")
    public BigDecimal getIdRichAnnulVers() {
	return this.idRichAnnulVers;
    }

    public void setIdRichAnnulVers(BigDecimal idRichAnnulVers) {
	this.idRichAnnulVers = idRichAnnulVers;
    }

}
