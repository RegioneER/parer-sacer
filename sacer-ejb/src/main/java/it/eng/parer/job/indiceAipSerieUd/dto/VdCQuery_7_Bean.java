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

/**
 *
 * @author gilioli_p
 */
public class VdCQuery_7_Bean {

    private Long idFiltroSelUdAttb;
    private String tiEntitaSacer;
    private String nmTipoUnitaDoc;
    private String nmTipoDoc;
    private String dsListaVersioniXsd;

    public Long getIdFiltroSelUdAttb() {
	return idFiltroSelUdAttb;
    }

    public void setIdFiltroSelUdAttb(Long idFiltroSelUdAttb) {
	this.idFiltroSelUdAttb = idFiltroSelUdAttb;
    }

    public String getTiEntitaSacer() {
	return tiEntitaSacer;
    }

    public void setTiEntitaSacer(String tiEntitaSacer) {
	this.tiEntitaSacer = tiEntitaSacer;
    }

    public String getNmTipoUnitaDoc() {
	return nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
	this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    public String getNmTipoDoc() {
	return nmTipoDoc;
    }

    public void setNmTipoDoc(String nmTipoDoc) {
	this.nmTipoDoc = nmTipoDoc;
    }

    public String getDsListaVersioniXsd() {
	return dsListaVersioniXsd;
    }

    public void setDsListaVersioniXsd(String dsListaVersioniXsd) {
	this.dsListaVersioniXsd = dsListaVersioniXsd;
    }
}
