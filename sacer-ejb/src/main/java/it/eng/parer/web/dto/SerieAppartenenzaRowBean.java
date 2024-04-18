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

package it.eng.parer.web.dto;

import java.math.BigDecimal;

import it.eng.spagoLite.db.base.row.BaseRow;

/**
 *
 * @author gilioli_p
 */
public class SerieAppartenenzaRowBean extends BaseRow {

    private static final long serialVersionUID = 1L;

    public BigDecimal getIdVerSerie() {
        return getBigDecimal("id_ver_serie");
    }

    public void setIdVerSerie(BigDecimal idVerSerie) {
        setObject("id_ver_serie", idVerSerie);
    }

    public String getCdCompositoSerie() {
        return getString("cd_composito_serie");
    }

    public void setCdCompositoSerie(String cdCompositoSerie) {
        setObject("cd_composito_serie", cdCompositoSerie);
    }

    public BigDecimal getAaSerie() {
        return getBigDecimal("aa_serie");
    }

    public void setAaSerie(BigDecimal aaSerie) {
        setObject("aa_serie", aaSerie);
    }

    public String getDsSerie() {
        return getString("ds_serie");
    }

    public void setDsSerie(String dsSerie) {
        setObject("ds_serie", dsSerie);
    }

    public String getCdVerSerie() {
        return getString("cd_ver_serie");
    }

    public void setCdVerSerie(String cdVerSerie) {
        setObject("cd_ver_serie", cdVerSerie);
    }

    public String getTiStatoVerSerie() {
        return getString("ti_stato_ver_serie");
    }

    public void setTiStatoVerSerie(String tiStatoVerSerie) {
        setObject("ti_stato_ver_serie", tiStatoVerSerie);
    }

    public String getTiStatoSerie() {
        return getString("ti_stato_serie");
    }

    public void setTiStatoSerie(String tiStatoSerie) {
        setObject("ti_stato_serie", tiStatoSerie);
    }

}
