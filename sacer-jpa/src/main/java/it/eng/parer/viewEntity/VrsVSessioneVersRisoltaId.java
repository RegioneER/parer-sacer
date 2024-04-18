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
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable()
public class VrsVSessioneVersRisoltaId implements Serializable {

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.dtChiusura);
        hash = 67 * hash + Objects.hashCode(this.flSesNonRisolub);
        hash = 67 * hash + Objects.hashCode(this.flSesRisolta);
        hash = 67 * hash + Objects.hashCode(this.flVerif);
        hash = 67 * hash + Objects.hashCode(this.tiDtCreazione);
        hash = 67 * hash + Objects.hashCode(this.tiSessioneVers);
        hash = 67 * hash + Objects.hashCode(this.tiStatoSessioneVers);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VrsVSessioneVersRisoltaId other = (VrsVSessioneVersRisoltaId) obj;
        if (!Objects.equals(this.flSesNonRisolub, other.flSesNonRisolub)) {
            return false;
        }
        if (!Objects.equals(this.flSesRisolta, other.flSesRisolta)) {
            return false;
        }
        if (!Objects.equals(this.flVerif, other.flVerif)) {
            return false;
        }
        if (!Objects.equals(this.tiDtCreazione, other.tiDtCreazione)) {
            return false;
        }
        if (!Objects.equals(this.tiSessioneVers, other.tiSessioneVers)) {
            return false;
        }
        if (!Objects.equals(this.tiStatoSessioneVers, other.tiStatoSessioneVers)) {
            return false;
        }
        if (!Objects.equals(this.dtChiusura, other.dtChiusura)) {
            return false;
        }
        return true;
    }

    private Date dtChiusura;

    @Column(name = "DT_CHIUSURA")
    public Date getDtChiusura() {
        return dtChiusura;
    }

    public void setDtChiusura(Date dtChiusura) {
        this.dtChiusura = dtChiusura;
    }

    private String flSesNonRisolub;

    @Column(name = "FL_SES_NON_RISOLUB", columnDefinition = "char(1)")
    public String getFlSesNonRisolub() {
        return flSesNonRisolub;
    }

    public void setFlSesNonRisolub(String flSesNonRisolub) {
        this.flSesNonRisolub = flSesNonRisolub;
    }

    private String flSesRisolta;

    @Column(name = "FL_SES_RISOLTA", columnDefinition = "char(1)")
    public String getFlSesRisolta() {
        return flSesRisolta;
    }

    public void setFlSesRisolta(String flSesRisolta) {
        this.flSesRisolta = flSesRisolta;
    }

    private String flVerif;

    @Column(name = "FL_VERIF", columnDefinition = "char(1)")
    public String getFlVerif() {
        return flVerif;
    }

    public void setFlVerif(String flVerif) {
        this.flVerif = flVerif;
    }

    private String tiDtCreazione;

    @Column(name = "TI_DT_CREAZIONE")
    public String getTiDtCreazione() {
        return tiDtCreazione;
    }

    public void setTiDtCreazione(String tiDtCreazione) {
        this.tiDtCreazione = tiDtCreazione;
    }

    private String tiSessioneVers;

    @Column(name = "TI_SESSIONE_VERS")
    public String getTiSessioneVers() {
        return tiSessioneVers;
    }

    public void setTiSessioneVers(String tiSessioneVers) {
        this.tiSessioneVers = tiSessioneVers;
    }

    private String tiStatoSessioneVers;

    @Column(name = "TI_STATO_SESSIONE_VERS")
    public String getTiStatoSessioneVers() {
        return tiStatoSessioneVers;
    }

    public void setTiStatoSessioneVers(String tiStatoSessioneVers) {
        this.tiStatoSessioneVers = tiStatoSessioneVers;
    }
}
