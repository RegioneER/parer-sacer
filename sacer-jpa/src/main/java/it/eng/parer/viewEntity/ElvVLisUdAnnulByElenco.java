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
 * The persistent class for the ELV_V_LIS_UD_ANNUL_BY_ELENCO database table.
 *
 */
@Entity
@Table(name = "ELV_V_LIS_UD_ANNUL_BY_ELENCO")

public class ElvVLisUdAnnulByElenco implements Serializable {

    private static final long serialVersionUID = 1L;
    private String dsUrnUnitaDocAnnul;
    private BigDecimal idElencoVers;
    private BigDecimal idRichAnnulVers;
    private BigDecimal idUnitaDoc;

    public ElvVLisUdAnnulByElenco() {/* Hibernate */
    }

    @Column(name = "DS_URN_UNITA_DOC_ANNUL")
    public String getDsUrnUnitaDocAnnul() {
        return this.dsUrnUnitaDocAnnul;
    }

    public void setDsUrnUnitaDocAnnul(String dsUrnUnitaDocAnnul) {
        this.dsUrnUnitaDocAnnul = dsUrnUnitaDocAnnul;
    }

    @Column(name = "ID_ELENCO_VERS")
    public BigDecimal getIdElencoVers() {
        return this.idElencoVers;
    }

    public void setIdElencoVers(BigDecimal idElencoVers) {
        this.idElencoVers = idElencoVers;
    }

    @Column(name = "ID_RICH_ANNUL_VERS")
    public BigDecimal getIdRichAnnulVers() {
        return this.idRichAnnulVers;
    }

    public void setIdRichAnnulVers(BigDecimal idRichAnnulVers) {
        this.idRichAnnulVers = idRichAnnulVers;
    }

    @Id
    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

}
