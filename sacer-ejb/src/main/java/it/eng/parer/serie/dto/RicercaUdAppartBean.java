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
import java.util.Date;

import it.eng.parer.slite.gen.form.SerieUDForm;
import it.eng.spagoCore.error.EMFError;

public class RicercaUdAppartBean {

    String cdUdSerie;
    Date dtUdSerieDa;
    Date dtUdSerieA;
    String infoUdSerie;
    BigDecimal pgUdSerieDa;
    BigDecimal pgUdSerieA;
    String tiStatoConservazione;

    public RicercaUdAppartBean() {
    }

    public RicercaUdAppartBean(SerieUDForm.FiltriContenutoSerieDetail filtri) throws EMFError {
	this.cdUdSerie = filtri.getCd_ud_serie().parse();
	this.dtUdSerieDa = filtri.getDt_ud_serie_da().parse();
	this.dtUdSerieA = filtri.getDt_ud_serie_a().parse();
	this.infoUdSerie = filtri.getInfo_ud_serie().parse();
	this.pgUdSerieDa = filtri.getPg_ud_serie_da().parse();
	this.pgUdSerieA = filtri.getPg_ud_serie_a().parse();
	this.tiStatoConservazione = filtri.getTi_stato_conservazione().parse();
    }

    public String getCdUdSerie() {
	return cdUdSerie;
    }

    public void setCdUdSerie(String cdUdSerie) {
	this.cdUdSerie = cdUdSerie;
    }

    public Date getDtUdSerieDa() {
	return dtUdSerieDa;
    }

    public void setDtUdSerieDa(Date dtUdSerieDa) {
	this.dtUdSerieDa = dtUdSerieDa;
    }

    public Date getDtUdSerieA() {
	return dtUdSerieA;
    }

    public void setDtUdSerieA(Date dtUdSerieA) {
	this.dtUdSerieA = dtUdSerieA;
    }

    public String getInfoUdSerie() {
	return infoUdSerie;
    }

    public void setInfoUdSerie(String infoUdSerie) {
	this.infoUdSerie = infoUdSerie;
    }

    public BigDecimal getPgUdSerieDa() {
	return pgUdSerieDa;
    }

    public void setPgUdSerieDa(BigDecimal pgUdSerieDa) {
	this.pgUdSerieDa = pgUdSerieDa;
    }

    public BigDecimal getPgUdSerieA() {
	return pgUdSerieA;
    }

    public void setPgUdSerieA(BigDecimal pgUdSerieA) {
	this.pgUdSerieA = pgUdSerieA;
    }

    public String getTiStatoConservazione() {
	return tiStatoConservazione;
    }

    public void setTiStatoConservazione(String tiStatoConservazione) {
	this.tiStatoConservazione = tiStatoConservazione;
    }

}
