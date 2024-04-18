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

package it.eng.parer.job.indiceAipSerieUd.dto;

/**
 *
 * @author gilioli_p
 */
public class VdCQuery_6_Bean {

    private Long idTipoSerieUd;
    private Long idFiltroSelUdAttb;
    private String nmAttribDatiSpec;
    private String tiOper;
    private String dlValore;

    public Long getIdTipoSerieUd() {
        return idTipoSerieUd;
    }

    public void setIdTipoSerieUd(Long idTipoSerieUd) {
        this.idTipoSerieUd = idTipoSerieUd;
    }

    public Long getIdFiltroSelUdAttb() {
        return idFiltroSelUdAttb;
    }

    public void setIdFiltroSelUdAttb(Long idFiltroSelUdAttb) {
        this.idFiltroSelUdAttb = idFiltroSelUdAttb;
    }

    public String getNmAttribDatiSpec() {
        return nmAttribDatiSpec;
    }

    public void setNmAttribDatiSpec(String nmAttribDatiSpec) {
        this.nmAttribDatiSpec = nmAttribDatiSpec;
    }

    public String getTiOper() {
        return tiOper;
    }

    public void setTiOper(String tiOper) {
        this.tiOper = tiOper;
    }

    public String getDlValore() {
        return dlValore;
    }

    public void setDlValore(String dlValore) {
        this.dlValore = dlValore;
    }

}
