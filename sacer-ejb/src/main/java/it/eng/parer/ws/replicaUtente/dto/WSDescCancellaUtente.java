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

package it.eng.parer.ws.replicaUtente.dto;

import java.util.HashMap;

import it.eng.parer.ws.dto.IWSDesc;
import it.eng.parer.ws.utils.Costanti;

/**
 *
 * @author Gilioli_P
 */
public class WSDescCancellaUtente implements IWSDesc {

    @Override
    public String getVersione() {
        return Costanti.WS_CANCELLA_UTENTE_VRSN;
    }

    @Override
    public String getNomeWs() {
        return Costanti.WS_CANCELLA_UTENTE_NOME;
    }

    public String[] getCompatibilitaWS() {
        return Costanti.WS_CANCELLA_UTENTE_COMP;
    }

    @Override
    public String getVersione(HashMap<String, String> mapWsVersion) {
        throw new UnsupportedOperationException(getNomeWs() + ": non supporta la versione su DB !");

    }
}
