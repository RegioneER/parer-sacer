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

package it.eng.parer.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the DM_UD_DEL_DECOD_STATO_INTERNO database table. Tabella di decodifica
 * degli stati interni delle richieste di cancellazione DataMart. Associa il codice tecnico
 * TI_STATO_INTERNO_RICH alla descrizione visualizzata all'utente DS_STATO_INTERNO_RICH.
 */
@Entity
@Table(name = "DM_UD_DEL_DECOD_STATO_INTERNO")
public class DmUdDelDecodStatoInterno implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idDecodStatoInterno;
    private String tiStatoInternoRich;
    private String dsStatoInternoRich;

    public DmUdDelDecodStatoInterno() {
        /* Hibernate */
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DECOD_STATO_INTERNO")
    public Long getIdDecodStatoInterno() {
        return this.idDecodStatoInterno;
    }

    public void setIdDecodStatoInterno(Long idDecodStatoInterno) {
        this.idDecodStatoInterno = idDecodStatoInterno;
    }

    @Column(name = "TI_STATO_INTERNO_RICH", nullable = false, length = 100)
    public String getTiStatoInternoRich() {
        return this.tiStatoInternoRich;
    }

    public void setTiStatoInternoRich(String tiStatoInternoRich) {
        this.tiStatoInternoRich = tiStatoInternoRich;
    }

    @Column(name = "DS_STATO_INTERNO_RICH", nullable = false, length = 200)
    public String getDsStatoInternoRich() {
        return this.dsStatoInternoRich;
    }

    public void setDsStatoInternoRich(String dsStatoInternoRich) {
        this.dsStatoInternoRich = dsStatoInternoRich;
    }
}
