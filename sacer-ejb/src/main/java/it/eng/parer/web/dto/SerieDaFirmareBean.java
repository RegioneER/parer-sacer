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

package it.eng.parer.web.dto;

import java.math.BigDecimal;

/**
 *
 * @author gilioli_p
 */
public class SerieDaFirmareBean {

    private String ambEnteStrut;
    private String cdCompositoSerie;
    private BigDecimal aaSerie;
    private String dsSerie;
    private String nmTipoSerie;
    private String cdVerSerie;
    private String rangeDate;

    public String getAmbEnteStrut() {
	return ambEnteStrut;
    }

    public void setAmbEnteStrut(String ambEnteStrut) {
	this.ambEnteStrut = ambEnteStrut;
    }

    public String getCdCompositoSerie() {
	return cdCompositoSerie;
    }

    public void setCdCompositoSerie(String cdCompositoSerie) {
	this.cdCompositoSerie = cdCompositoSerie;
    }

    public BigDecimal getAaSerie() {
	return aaSerie;
    }

    public void setAaSerie(BigDecimal aaSerie) {
	this.aaSerie = aaSerie;
    }

    public String getDsSerie() {
	return dsSerie;
    }

    public void setDsSerie(String dsSerie) {
	this.dsSerie = dsSerie;
    }

    public String getNmTipoSerie() {
	return nmTipoSerie;
    }

    public void setNmTipoSerie(String nmTipoSerie) {
	this.nmTipoSerie = nmTipoSerie;
    }

    public String getCdVerSerie() {
	return cdVerSerie;
    }

    public void setCdVerSerie(String cdVerSerie) {
	this.cdVerSerie = cdVerSerie;
    }

    public String getRangeDate() {
	return rangeDate;
    }

    public void setRangeDate(String rangeDate) {
	this.rangeDate = rangeDate;
    }

}
