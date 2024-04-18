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

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Table;

/**
 * The persistent class for the ELV_V_SEL_USR_INDICE_ELENCO database table.
 */
@Entity
@Table(name = "ELV_V_SEL_USR_INDICE_ELENCO")

public class ElvVSelUsrIndiceElenco implements Serializable {

    private static final long serialVersionUID = 1L;

    public ElvVSelUsrIndiceElenco() {/* Hibernate */
    }

    private ElvVSelUsrIndiceElencoId elvVSelUsrIndiceElencoId;

    @EmbeddedId()
    public ElvVSelUsrIndiceElencoId getElvVSelUsrIndiceElencoId() {
        return elvVSelUsrIndiceElencoId;
    }

    public void setElvVSelUsrIndiceElencoId(ElvVSelUsrIndiceElencoId elvVSelUsrIndiceElencoId) {
        this.elvVSelUsrIndiceElencoId = elvVSelUsrIndiceElencoId;
    }
}
