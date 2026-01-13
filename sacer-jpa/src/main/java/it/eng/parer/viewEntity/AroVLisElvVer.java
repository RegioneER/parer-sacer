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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ARO_V_LIS_ELV_VERS database table.
 *
 */
@Entity
@Table(name = "ARO_V_LIS_ELV_VERS")

public class AroVLisElvVer implements Serializable {

    private static final long serialVersionUID = 1L;
    private Date dtCreazioneElenco;
    private Date dtCreazioneElencoIxAip;
    private Date dtFirmaElencoIxAip;
    private Date dtMarcaElencoIxAip;
    private AroVLisElvVerId aroVLisElvVerId;
    private String nmElenco;
    private String tiStatoElenco;

    public AroVLisElvVer() {/* Hibernate */
    }

    @EmbeddedId
    public AroVLisElvVerId getAroVLisElvVerId() {
        return aroVLisElvVerId;
    }

    public void setAroVLisElvVerId(AroVLisElvVerId aroVLisElvVerId) {
        this.aroVLisElvVerId = aroVLisElvVerId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE_ELENCO")
    public Date getDtCreazioneElenco() {
        return this.dtCreazioneElenco;
    }

    public void setDtCreazioneElenco(Date dtCreazioneElenco) {
        this.dtCreazioneElenco = dtCreazioneElenco;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE_ELENCO_IX_AIP")
    public Date getDtCreazioneElencoIxAip() {
        return this.dtCreazioneElencoIxAip;
    }

    public void setDtCreazioneElencoIxAip(Date dtCreazioneElencoIxAip) {
        this.dtCreazioneElencoIxAip = dtCreazioneElencoIxAip;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIRMA_ELENCO_IX_AIP")
    public Date getDtFirmaElencoIxAip() {
        return this.dtFirmaElencoIxAip;
    }

    public void setDtFirmaElencoIxAip(Date dtFirmaElencoIxAip) {
        this.dtFirmaElencoIxAip = dtFirmaElencoIxAip;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_MARCA_ELENCO_IX_AIP")
    public Date getDtMarcaElencoIxAip() {
        return this.dtMarcaElencoIxAip;
    }

    public void setDtMarcaElencoIxAip(Date dtMarcaElencoIxAip) {
        this.dtMarcaElencoIxAip = dtMarcaElencoIxAip;
    }

    @Column(name = "NM_ELENCO")
    public String getNmElenco() {
        return this.nmElenco;
    }

    public void setNmElenco(String nmElenco) {
        this.nmElenco = nmElenco;
    }

    @Column(name = "TI_STATO_ELENCO")
    public String getTiStatoElenco() {
        return this.tiStatoElenco;
    }

    public void setTiStatoElenco(String tiStatoElenco) {
        this.tiStatoElenco = tiStatoElenco;
    }

}
