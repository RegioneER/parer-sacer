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

package it.eng.parer.volume.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Agati_D
 */
public final class DatiSpecQueryParams {

    private String tiOper;
    private String dlValore;
    private List<BigDecimal> idAttribDatiSpec;
    private List<String> nmSistemaMigraz;

    public DatiSpecQueryParams(String tiOper, String dlValore) {
        this.tiOper = tiOper;
        this.dlValore = dlValore;
        idAttribDatiSpec = new ArrayList<>();
        nmSistemaMigraz = new ArrayList<>();
    }

    public boolean add(BigDecimal e) {
        return idAttribDatiSpec.add(e);
    }

    public boolean addSM(String e) {
        return nmSistemaMigraz.add(e);
    }

    public String getDlValore() {
        return dlValore;
    }

    public void setDlValore(String dlValore) {
        this.dlValore = dlValore;
    }

    public List<BigDecimal> getIdAttribDatiSpec() {
        return idAttribDatiSpec;
    }

    public void setIdAttribDatiSpec(List<BigDecimal> idAttribDatiSpec) {
        this.idAttribDatiSpec = idAttribDatiSpec;
    }

    public List<String> getNmSistemaMigraz() {
        return nmSistemaMigraz;
    }

    public void setNmSistemaMigraz(List<String> nmSistemaMigraz) {
        this.nmSistemaMigraz = nmSistemaMigraz;
    }

    public String getTiOper() {
        return tiOper;
    }

    public void setTiOper(String tiOper) {
        this.tiOper = tiOper;
    }
}
