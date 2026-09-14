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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entità JPA per la vista ELV_V_LIS_ELENCO_UD_SCARTO, analoga a ElvVLisElencoUdAnnul per
 * l'annullamento. Usata per aggiungere note su elenchi di versamento contenenti UD scartate.
 */
@Entity
@Table(name = "ELV_V_LIS_ELENCO_UD_SCARTO")
public class ElvVLisElencoUdScarto implements Serializable {

    private static final long serialVersionUID = 1L;

    private ElvVLisElencoUdScartoId elvVLisElencoUdScartoId;
    private String ntElencoChiuso;

    public ElvVLisElencoUdScarto() {
        /* Hibernate */ }

    @EmbeddedId
    public ElvVLisElencoUdScartoId getElvVLisElencoUdScartoId() {
        return elvVLisElencoUdScartoId;
    }

    public void setElvVLisElencoUdScartoId(ElvVLisElencoUdScartoId elvVLisElencoUdScartoId) {
        this.elvVLisElencoUdScartoId = elvVLisElencoUdScartoId;
    }

    @Column(name = "NT_ELENCO_CHIUSO")
    public String getNtElencoChiuso() {
        return ntElencoChiuso;
    }

    public void setNtElencoChiuso(String ntElencoChiuso) {
        this.ntElencoChiuso = ntElencoChiuso;
    }
}
