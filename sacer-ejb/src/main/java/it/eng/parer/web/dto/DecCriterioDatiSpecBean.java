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

import java.util.List;

/**
 * Copia modificata della entity JPA DecCriterioDatiSpec per la gestione dei dati specifici in fase di costruzione query
 * 
 * @author Gilioli_P
 */
public class DecCriterioDatiSpecBean {

    private long idCriterioDatiSpec;
    private String dlValore;
    private String nmAttribDatiSpec;
    private String tiOper;
    private List<DecCriterioAttribBean> decCriterioAttribs;

    public DecCriterioDatiSpecBean() {
    }

    public long getIdCriterioDatiSpec() {
        return this.idCriterioDatiSpec;
    }

    public void setIdCriterioDatiSpec(long idCriterioDatiSpec) {
        this.idCriterioDatiSpec = idCriterioDatiSpec;
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

    public List<DecCriterioAttribBean> getDecCriterioAttribs() {
        return this.decCriterioAttribs;
    }

    public void setDecCriterioAttribs(List<DecCriterioAttribBean> decCriterioAttribs) {
        this.decCriterioAttribs = decCriterioAttribs;
    }
}
