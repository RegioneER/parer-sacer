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

/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.web.dto;

import java.util.List;

/**
 *
 * @author Parucci_M
 */
public class DecFiltroSelUdAttbBean {

    private long idFiltroSelUdAttb;
    private String dlValore;
    private String nmAttribDatiSpec;
    private String tiOper;
    private List<DecFiltroSelUdDatoBean> decFiltroSelUdDatos;

    public DecFiltroSelUdAttbBean() {
    }

    public long getIdFiltroSelUdAttb() {
	return this.idFiltroSelUdAttb;
    }

    public void setIdFiltroSelUdAttb(long idFiltroSelUdAttb) {
	this.idFiltroSelUdAttb = idFiltroSelUdAttb;
    }

    public String getDlValore() {
	return this.dlValore;
    }

    public void setDlValore(String dlValore) {
	this.dlValore = dlValore;
    }

    public String getNmAttribDatiSpec() {
	return this.nmAttribDatiSpec;
    }

    public void setNmAttribDatiSpec(String nmAttribDatiSpec) {
	this.nmAttribDatiSpec = nmAttribDatiSpec;
    }

    public String getTiOper() {
	return this.tiOper;
    }

    public void setTiOper(String tiOper) {
	this.tiOper = tiOper;
    }

    public List<DecFiltroSelUdDatoBean> getDecFiltroSelUdDatos() {
	return this.decFiltroSelUdDatos;
    }

    public void setDecFiltroSelUdDatos(List<DecFiltroSelUdDatoBean> decFiltroSelUdDatos) {
	this.decFiltroSelUdDatos = decFiltroSelUdDatos;
    }
}
