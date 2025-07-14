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

package it.eng.parer.job.indiceAipSerieUd.dto;

import java.math.BigDecimal;

/**
 *
 * @author gilioli_p
 */
public class VdCQuery_2_3_Bean {

    private Long idVerSerieCor;
    private String tiModLacuna;
    private BigDecimal niIniLacuna;
    private BigDecimal niFinLacuna;
    private String dlLacuna;
    private String dlNotaLacuna;

    public Long getIdVerSerieCor() {
	return idVerSerieCor;
    }

    public void setIdVerSerieCor(Long idVerSerieCor) {
	this.idVerSerieCor = idVerSerieCor;
    }

    public String getTiModLacuna() {
	return tiModLacuna;
    }

    public void setTiModLacuna(String tiModLacuna) {
	this.tiModLacuna = tiModLacuna;
    }

    public BigDecimal getNiIniLacuna() {
	return niIniLacuna;
    }

    public void setNiIniLacuna(BigDecimal niIniLacuna) {
	this.niIniLacuna = niIniLacuna;
    }

    public BigDecimal getNiFinLacuna() {
	return niFinLacuna;
    }

    public void setNiFinLacuna(BigDecimal niFinLacuna) {
	this.niFinLacuna = niFinLacuna;
    }

    public String getDlLacuna() {
	return dlLacuna;
    }

    public void setDlLacuna(String dlLacuna) {
	this.dlLacuna = dlLacuna;
    }

    public String getDlNotaLacuna() {
	return dlNotaLacuna;
    }

    public void setDlNotaLacuna(String dlNotaLacuna) {
	this.dlNotaLacuna = dlNotaLacuna;
    }

}
