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

package it.eng.parer.serie.dto;

import java.math.BigDecimal;

import it.eng.parer.ws.utils.CostantiDB;

/**
 *
 * @author Bonora_L
 */
public class CampiInputBean {

    BigDecimal pgOrdCampo;
    String nmCampo;
    CostantiDB.TipoCampo tipoCampo;
    String tiTransformCampo;
    String vlCampoRecord;
    String vlCampoTransform;

    public BigDecimal getPgOrdCampo() {
        return pgOrdCampo;
    }

    public void setPgOrdCampo(BigDecimal pgOrdCampo) {
        this.pgOrdCampo = pgOrdCampo;
    }

    public String getNmCampo() {
        return nmCampo;
    }

    public void setNmCampo(String nmCampo) {
        this.nmCampo = nmCampo;
    }

    public String getTiTransformCampo() {
        return tiTransformCampo;
    }

    public void setTiTransformCampo(String tiTransformCampo) {
        this.tiTransformCampo = tiTransformCampo;
    }

    public String getVlCampoRecord() {
        return vlCampoRecord;
    }

    public void setVlCampoRecord(String vlCampoRecord) {
        this.vlCampoRecord = vlCampoRecord;
    }

    public String getVlCampoTransform() {
        return vlCampoTransform;
    }

    public void setVlCampoTransform(String vlCampoTransform) {
        this.vlCampoTransform = vlCampoTransform;
    }

    public CostantiDB.TipoCampo getTipoCampo() {
        return tipoCampo;
    }

    public void setTipoCampo(CostantiDB.TipoCampo tipoCampo) {
        this.tipoCampo = tipoCampo;
    }
}
