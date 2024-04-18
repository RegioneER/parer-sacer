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

package it.eng.parer.grantedViewEntity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The persistent class for the ORG_V_CHK_SERV_FATT_BY_STRUT database table.
 */
@Entity
@Table(schema = "SACER_IAM", name = "ORG_V_CHK_SERV_FATT_BY_STRUT")
public class OrgVChkServFattByStrut implements Serializable {

    private static final long serialVersionUID = 1L;

    private String flEliminaEnteStrut;

    public OrgVChkServFattByStrut() {/* Hibernate */
    }

    @Column(name = "FL_ELIMINA_ENTE_STRUT", columnDefinition = "char(1)")
    public String getFlEliminaEnteStrut() {
        return this.flEliminaEnteStrut;
    }

    public void setFlEliminaEnteStrut(String flEliminaEnteStrut) {
        this.flEliminaEnteStrut = flEliminaEnteStrut;
    }

    private OrgVChkServFattByStrutId orgVChkServFattByStrutId;

    @EmbeddedId()
    public OrgVChkServFattByStrutId getOrgVChkServFattByStrutId() {
        return orgVChkServFattByStrutId;
    }

    public void setOrgVChkServFattByStrutId(OrgVChkServFattByStrutId orgVChkServFattByStrutId) {
        this.orgVChkServFattByStrutId = orgVChkServFattByStrutId;
    }
}
