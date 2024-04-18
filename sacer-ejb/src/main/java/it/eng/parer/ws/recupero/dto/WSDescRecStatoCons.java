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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.recupero.dto;

import java.util.HashMap;

import it.eng.parer.ws.dto.IWSDesc;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.VerificaVersione;

/**
 *
 * @author Fioravanti_F
 */
public class WSDescRecStatoCons implements IWSDesc {

    @Override
    public String getVersione() {
        throw new UnsupportedOperationException(getNomeWs() + ": supporta la versione su DB !");
    }

    @Override
    public String getNomeWs() {
        return Costanti.WS_REC_STATO_CON_NOME;
    }

    @Override
    public String getVersione(HashMap<String, String> mapWsVersion) {
        return VerificaVersione.latestVersion(getNomeWs(), mapWsVersion);
    }

    // @Override
    // public String[] getCompatibilitaWS() {
    // return Costanti.WS_REC_STATO_CON_COMP;
    // }
}
