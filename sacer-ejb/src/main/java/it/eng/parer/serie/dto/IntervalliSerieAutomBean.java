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

package it.eng.parer.serie.dto;

import java.util.Date;
import java.util.Objects;

public class IntervalliSerieAutomBean {

    private Date dtInizioSerie;
    private Date dtFineSerie;
    private String cdSerie;
    private String dsSerie;

    public IntervalliSerieAutomBean(Date dtInizioSerie, Date dtFineSerie, String cdSerie,
	    String dsSerie) {
	this.dtInizioSerie = dtInizioSerie;
	this.dtFineSerie = dtFineSerie;
	this.cdSerie = cdSerie;
	this.dsSerie = dsSerie;
    }

    public Date getDtInizioSerie() {
	return dtInizioSerie;
    }

    public void setDtInizioSerie(Date dtInizioSerie) {
	this.dtInizioSerie = dtInizioSerie;
    }

    public Date getDtFineSerie() {
	return dtFineSerie;
    }

    public void setDtFineSerie(Date dtFineSerie) {
	this.dtFineSerie = dtFineSerie;
    }

    public String getCdSerie() {
	return cdSerie;
    }

    public void setCdSerie(String cdSerie) {
	this.cdSerie = cdSerie;
    }

    public String getDsSerie() {
	return dsSerie;
    }

    public void setDsSerie(String dsSerie) {
	this.dsSerie = dsSerie;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 41 * hash + Objects.hashCode(this.dtInizioSerie);
	hash = 41 * hash + Objects.hashCode(this.dtFineSerie);
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final IntervalliSerieAutomBean other = (IntervalliSerieAutomBean) obj;

	if (other.dtInizioSerie == null || this.dtInizioSerie == null
		|| this.dtInizioSerie.getTime() != other.dtInizioSerie.getTime()) {
	    return false;
	}
	if (other.dtFineSerie == null || this.dtFineSerie == null
		|| this.dtFineSerie.getTime() != other.dtFineSerie.getTime()) {
	    return false;
	}
	return true;
    }

}
