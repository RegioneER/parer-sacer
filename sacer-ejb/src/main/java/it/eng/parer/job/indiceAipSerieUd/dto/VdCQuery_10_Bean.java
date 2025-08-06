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
public class VdCQuery_10_Bean {

    private Long idVerSerie;
    private String nmTipoDoc;
    private String dsTipoDoc;

    public Long getIdVerSerie() {
	return idVerSerie;
    }

    public void setIdVerSerie(Long idVerSerie) {
	this.idVerSerie = idVerSerie;
    }

    public String getNmTipoDoc() {
	return nmTipoDoc;
    }

    public void setNmTipoDoc(String nmTipoDoc) {
	this.nmTipoDoc = nmTipoDoc;
    }

    public String getDsTipoDoc() {
	return dsTipoDoc;
    }

    public void setDsTipoDoc(String dsTipoDoc) {
	this.dsTipoDoc = dsTipoDoc;
    }
}
