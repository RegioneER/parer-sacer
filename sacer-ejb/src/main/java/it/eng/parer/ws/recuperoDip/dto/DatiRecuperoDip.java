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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.recuperoDip.dto;

import java.util.LinkedHashMap;

/**
 *
 * @author Fioravanti_F
 */
public class DatiRecuperoDip {

    private long numeroElementiTrovati;
    private LinkedHashMap<Long, CompRecDip> elementiTrovati;

    public long getNumeroElementiTrovati() {
        return numeroElementiTrovati;
    }

    public void setNumeroElementiTrovati(long numeroElementiTrovati) {
        this.numeroElementiTrovati = numeroElementiTrovati;
    }

    public LinkedHashMap<Long, CompRecDip> getElementiTrovati() {
        return elementiTrovati;
    }

    public void setElementiTrovati(LinkedHashMap<Long, CompRecDip> elementiTrovati) {
        this.elementiTrovati = elementiTrovati;
    }

}
