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

import javax.persistence.*;
import java.io.Serializable;

/**
 * The persistent class for the ARO_V_CHK_RA_UD database table.
 *
 */
@Entity
@Table(name = "ARO_V_CHK_RA_UD")
@NamedQuery(name = "AroVChkRaUd.findAll", query = "SELECT e FROM AroVChkRaUd e")
public class AroVChkRaUd implements Serializable {

    private static final long serialVersionUID = 1L;
    private String flUdNonInElenco;
    private String flElencoNonCompletato;
    private String flUdAipNonFirmato;

    public AroVChkRaUd() {/* Hibernate */
    }

    @Column(name = "FL_UD_NON_IN_ELENCO", columnDefinition = "number")
    public String getFlUdNonInElenco() {
        return this.flUdNonInElenco;
    }

    public void setFlUdNonInElenco(String flUdNonInElenco) {
        this.flUdNonInElenco = flUdNonInElenco;
    }

    @Column(name = "FL_ELENCO_NON_COMPLETATO", columnDefinition = "number")
    public String getFlElencoNonCompletato() {
        return this.flElencoNonCompletato;
    }

    public void setFlElencoNonCompletato(String flElencoNonCompletato) {
        this.flElencoNonCompletato = flElencoNonCompletato;
    }

    @Column(name = "FL_UD_AIP_NON_FIRMATO", columnDefinition = "number")
    public String getFlUdAipNonFirmato() {
        return flUdAipNonFirmato;
    }

    public void setFlUdAipNonFirmato(String flUdAipNonFirmato) {
        this.flUdAipNonFirmato = flUdAipNonFirmato;
    }

    private AroVChkRaUdId aroVChkRaUdId;

    @EmbeddedId()
    public AroVChkRaUdId getAroVChkRaUdId() {
        return aroVChkRaUdId;
    }

    public void setAroVChkRaUdId(AroVChkRaUdId aroVChkRaUdId) {
        this.aroVChkRaUdId = aroVChkRaUdId;
    }

}
