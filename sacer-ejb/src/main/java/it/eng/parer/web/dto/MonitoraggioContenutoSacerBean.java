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

/**
 *
 * @author Gilioli_P
 */
public class MonitoraggioContenutoSacerBean {

    private String nmEnte;
    private String nmStrut;
    private String nmSubStrut;
    private String cdRegistroUnitaDoc;
    private BigDecimal aaKeyUnitaDoc;
    private String nmTipoUnitaDoc;
    private String nmTipoDoc;
    private String nmCategTipoUnitaDoc;
    private String nmSottocategTipoUnitaDoc;
    private String niUnitaDoc;
    private String niDoc;
    private String niComp;
    private String niSize;

    public String getNiUnitaDoc() {
        return niUnitaDoc;
    }

    public void setNiUnitaDoc(String niUnitaDoc) {
        this.niUnitaDoc = niUnitaDoc;
    }

    public String getNiDoc() {
        return niDoc;
    }

    public void setNiDoc(String niDoc) {
        this.niDoc = niDoc;
    }

    public String getNiComp() {
        return niComp;
    }

    public void setNiComp(String niComp) {
        this.niComp = niComp;
    }

    public String getNiSize() {
        return niSize;
    }

    public void setNiSize(String niSize) {
        this.niSize = niSize;
    }

    public String getNmEnte() {
        return nmEnte;
    }

    public void setNmEnte(String nmEnte) {
        this.nmEnte = nmEnte;
    }

    public String getNmStrut() {
        return nmStrut;
    }

    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
    }

    public String getNmSubStrut() {
        return nmSubStrut;
    }

    public void setNmSubStrut(String nmSubStrut) {
        this.nmSubStrut = nmSubStrut;
    }

    public String getCdRegistroUnitaDoc() {
        return cdRegistroUnitaDoc;
    }

    public void setCdRegistroUnitaDoc(String cdRegistroUnitaDoc) {
        this.cdRegistroUnitaDoc = cdRegistroUnitaDoc;
    }

    public BigDecimal getAaKeyUnitaDoc() {
        return aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    public String getNmCategTipoUnitaDoc() {
        return nmCategTipoUnitaDoc;
    }

    public void setNmCategTipoUnitaDoc(String nmCategTipoUnitaDoc) {
        this.nmCategTipoUnitaDoc = nmCategTipoUnitaDoc;
    }

    public String getNmSottocategTipoUnitaDoc() {
        return nmSottocategTipoUnitaDoc;
    }

    public void setNmSottocategTipoUnitaDoc(String nmSottocategTipoUnitaDoc) {
        this.nmSottocategTipoUnitaDoc = nmSottocategTipoUnitaDoc;
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
}
