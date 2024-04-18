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

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author gilioli_p
 */
public class VdCQuery_11_Bean {

    private Long idVerSerie;
    private String cdTipoNotaSerie;
    private BigDecimal pgNotaVerSerie;
    private String dsNota;
    private Date dtNota;
    private String dsAutore;

    public Long getIdVerSerie() {
        return idVerSerie;
    }

    public void setIdVerSerie(Long idVerSerie) {
        this.idVerSerie = idVerSerie;
    }

    public String getCdTipoNotaSerie() {
        return cdTipoNotaSerie;
    }

    public void setCdTipoNotaSerie(String cdTipoNotaSerie) {
        this.cdTipoNotaSerie = cdTipoNotaSerie;
    }

    public BigDecimal getPgNotaVerSerie() {
        return pgNotaVerSerie;
    }

    public void setPgNotaVerSerie(BigDecimal pgNotaVerSerie) {
        this.pgNotaVerSerie = pgNotaVerSerie;
    }

    public String getDsNota() {
        return dsNota;
    }

    public void setDsNota(String dsNota) {
        this.dsNota = dsNota;
    }

    public Date getDtNota() {
        return dtNota;
    }

    public void setDtNota(Date dtNota) {
        this.dtNota = dtNota;
    }

    public String getDsAutore() {
        return dsAutore;
    }

    public void setDsAutore(String dsAutore) {
        this.dsAutore = dsAutore;
    }

}
