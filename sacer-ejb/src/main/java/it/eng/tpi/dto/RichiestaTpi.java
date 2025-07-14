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

package it.eng.tpi.dto;

public class RichiestaTpi {

    private TipoRichiesta tipoRichiesta;
    public static final String NM_USER = "nmUser";
    public static final String CD_PSW = "cdPsw";
    public static final String FL_CARTELLA_MIGRAZ = "flCartellaMigraz";
    public static final String DT_VERS = "dtVers";
    public static final String ROOT_DT_VERS = "rootDtVers";
    public static final String TI_RI_ARK = "tiRiArk";
    public static final String DIR_STRUTTURA = "dirStruttura";
    public static final String DIR_UNITA_DOC = "dirUnitaDoc";
    public static final String DT_SCHED = "dtSched";

    public enum TipoRichiesta {

	STATO_ARK_CARTELLA, ELIMINA_CARTELLA_ARK, RETRIEVE_FILE_UNITA_DOC, REGISTRA_CARTELLA_RI_ARK,
	SCHEDULAZIONI_JOB_TPI;
    }

    public enum TipoRiArk {

	LOCALE, SECONDARIO, ENTRAMBI;

	public static TipoRiArk getTipoRiArk(String tipoRiArk) {
	    for (TipoRiArk tipo : TipoRiArk.values()) {
		if (tipo.toString().equals(tipoRiArk)) {
		    return tipo;
		}
	    }
	    return null;
	}
    }

    public RichiestaTpi(TipoRichiesta tipoRichiesta) {
	this.tipoRichiesta = tipoRichiesta;
    }

    public TipoRichiesta getTipoRichiesta() {
	return tipoRichiesta;
    }

    public void setTipoRichiesta(TipoRichiesta tipoRichiesta) {
	this.tipoRichiesta = tipoRichiesta;
    }
}
