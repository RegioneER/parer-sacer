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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SerieAutomBean {

    private BigDecimal niMesiCreazioneSerie;
    private BigDecimal niGiorniCalcolo;
    private int numeroSerieDaCreare;
    private String tipoIntervallo;
    private int numMesiIntervallo;
    private final List<IntervalliSerieAutomBean> intervalli;

    public SerieAutomBean(BigDecimal niMesiCreazioneSerie) {
	this.niMesiCreazioneSerie = niMesiCreazioneSerie;
	this.niGiorniCalcolo = null;
	this.numeroSerieDaCreare = 1;
	this.numMesiIntervallo = 1;
	this.tipoIntervallo = null;
	this.intervalli = new ArrayList<>();
    }

    public BigDecimal getNiMesiCreazioneSerie() {
	return niMesiCreazioneSerie;
    }

    public void setNiMesiCreazioneSerie(BigDecimal niMesiCreazioneSerie) {
	this.niMesiCreazioneSerie = niMesiCreazioneSerie;
    }

    public BigDecimal getNiGiorniCalcolo() {
	return niGiorniCalcolo;
    }

    public void setNiGiorniCalcolo(BigDecimal niGiorniCalcolo) {
	this.niGiorniCalcolo = niGiorniCalcolo;
    }

    public int getNumeroSerieDaCreare() {
	return numeroSerieDaCreare;
    }

    public void setNumeroSerieDaCreare(int numeroSerieDaCreare) {
	this.numeroSerieDaCreare = numeroSerieDaCreare;
    }

    public String getTipoIntervallo() {
	return tipoIntervallo;
    }

    public void setTipoIntervallo(String tipoIntervallo) {
	this.tipoIntervallo = tipoIntervallo;
    }

    public int getNumMesiIntervallo() {
	return numMesiIntervallo;
    }

    public void setNumMesiIntervallo(int numMesiIntervallo) {
	this.numMesiIntervallo = numMesiIntervallo;
    }

    public List<IntervalliSerieAutomBean> getIntervalli() {
	return intervalli;
    }

}
