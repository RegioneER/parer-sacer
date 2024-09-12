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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Table;

/**
 * The persistent class for the SER_V_JOB_CONTENUTO_BLOCCATO database table.
 *
 */
@Entity
@Table(name = "SER_V_JOB_CONTENUTO_BLOCCATO")

public class SerVJobContenutoBloccato implements Serializable {
    private static final long serialVersionUID = 1L;
    private String dlMsgJobBloccato;
    private String flJobBloccato;
    private SerVJobContenutoBloccatoId serVJobContenutoBloccatoId;

    @EmbeddedId
    public SerVJobContenutoBloccatoId getSerVJobContenutoBloccatoId() {
        return serVJobContenutoBloccatoId;
    }

    public void setSerVJobContenutoBloccatoId(SerVJobContenutoBloccatoId serVJobContenutoBloccatoId) {
        this.serVJobContenutoBloccatoId = serVJobContenutoBloccatoId;
    }

    public SerVJobContenutoBloccato() {/* Hibernate */
    }

    @Column(name = "DL_MSG_JOB_BLOCCATO")
    public String getDlMsgJobBloccato() {
        return this.dlMsgJobBloccato;
    }

    public void setDlMsgJobBloccato(String dlMsgJobBloccato) {
        this.dlMsgJobBloccato = dlMsgJobBloccato;
    }

    @Column(name = "FL_JOB_BLOCCATO", columnDefinition = "char(1)")
    public String getFlJobBloccato() {
        return this.flJobBloccato;
    }

    public void setFlJobBloccato(String flJobBloccato) {
        this.flJobBloccato = flJobBloccato;
    }

}
