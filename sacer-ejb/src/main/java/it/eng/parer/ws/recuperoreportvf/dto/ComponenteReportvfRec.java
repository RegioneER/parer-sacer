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

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.ws.recuperoreportvf.dto;

import it.eng.parer.ws.recupero.dto.ComponenteRec;
import it.eng.parer.ws.utils.MessaggiWSFormat;

public class ComponenteReportvfRec extends ComponenteRec {

    private long idReportvf;
    private String urnCompletoOrig;

    public ComponenteReportvfRec() {
	super();
    }

    public ComponenteReportvfRec(String urnCompleto, String urnCompletoOrig) {
	super();
	setUrnAndElabNomeFileReport(urnCompleto, urnCompletoOrig);
    }

    private void setUrnAndElabNomeFileReport(String urnCompleto, String urnCompletoOrig) {
	setUrnCompleto(urnCompleto);
	setUrnCompletoOrig(urnCompletoOrig);

	this.nomeFileBreve = estraiNomeFileBreveReport(urnCompletoOrig);
	this.nomeFileCompleto = estraiNomeFileCompleto(urnCompletoOrig);
    }

    public long getIdReportvf() {
	return idReportvf;
    }

    public void setIdReportvf(long idReportvf) {
	this.idReportvf = idReportvf;
    }

    public String getUrnCompletoOrig() {
	return urnCompletoOrig;
    }

    public void setUrnCompletoOrig(String urnCompletoOrig) {
	this.urnCompletoOrig = urnCompletoOrig;
    }

    // richiama logica di estrazione del nome file a partire dall'urn
    private String estraiNomeFileBreveReport(String urnCompleto) {
	return MessaggiWSFormat.estraiNomeFileBrevePerReportvf(urnCompleto);
    }

}
