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
package it.eng.parer.ws.recupero.dto;

import it.eng.parer.ws.utils.MessaggiWSFormat;

/**
 *
 * @author Fioravanti_F
 */
public class ComponenteRec {

    protected long idCompDoc;
    protected String urnCompleto;
    protected String urnCompletoIniziale;
    protected String nomeFileBreve;
    protected String nomeFileBreveTivoli;
    protected String nomeFileCompleto;
    protected String estensioneFile;
    protected String mimeType;
    protected String urnOriginaleVersata;
    protected String nomeFileOriginaleVersato;

    public ComponenteRec() {
	super();
    }

    public ComponenteRec(String urnCompleto, String urnCompletoIniziale) {
	super();
	setUrnAndElabNomiFile(urnCompleto, urnCompletoIniziale);
    }

    private void setUrnAndElabNomiFile(String urnCompleto, String urnCompletoIniziale) {
	setUrnCompleto(urnCompleto);
	setUrnCompletoIniziale(urnCompletoIniziale);

	this.nomeFileBreve = estraiNomeFileBreve(urnCompleto);
	this.nomeFileCompleto = estraiNomeFileCompleto(urnCompleto);
	this.nomeFileBreveTivoli = estraiNomeFilePerTivoli(urnCompletoIniziale, urnCompleto);
    }

    public String getEstensioneFile() {
	return estensioneFile;
    }

    public void setEstensioneFile(String estensioneFile) {
	this.estensioneFile = estensioneFile;
    }

    public String getMimeType() {
	return mimeType;
    }

    public void setMimeType(String mimeType) {
	this.mimeType = mimeType;
    }

    public long getIdCompDoc() {
	return idCompDoc;
    }

    public void setIdCompDoc(long idCompDoc) {
	this.idCompDoc = idCompDoc;
    }

    protected void setUrnCompleto(String urnCompleto) {
	this.urnCompleto = urnCompleto;
    }

    private void setUrnCompletoIniziale(String urnCompletoIniziale) {
	this.urnCompletoIniziale = urnCompletoIniziale;
    }

    public String getUrnCompleto() {
	return urnCompleto;
    }

    public String getUrnCompletoIniziale() {
	return urnCompletoIniziale;
    }

    // MEV#22921 Parametrizzazione servizi di recupero
    public String getUrnOriginaleVersata() {
	return urnOriginaleVersata;
    }

    public void setUrnOriginaleVersata(String urnOriginaleVersata) {
	this.urnOriginaleVersata = urnOriginaleVersata;
    }

    public String getNomeFileOriginaleVersato() {
	return nomeFileOriginaleVersato;
    }

    public void setNomeFileOriginaleVersato(String nomeFileOriginaleVersato) {
	this.nomeFileOriginaleVersato = nomeFileOriginaleVersato;
    }
    // Fine MEV#22921

    public String getNomeFileBreve() {
	return nomeFileBreve;
    }

    public String getNomeFileCompleto() {
	return nomeFileCompleto;
    }

    public String getNomeFilePerZip() {
	if (this.nomeFileCompleto != null && this.estensioneFile != null) {
	    return this.nomeFileCompleto + "." + this.estensioneFile;
	} else {
	    return null;
	}
    }

    // EVO#20972
    public String getNomeFilePerZipAIPV2() {
	if (this.nomeFileBreve != null && this.estensioneFile != null) {
	    return this.nomeFileBreve + "." + this.estensioneFile;
	} else {
	    return null;
	}
    }
    // end EVO#20972

    // richiama logica di estrazione del nome file a partire dall'urn
    private String estraiNomeFileBreve(String urnCompleto) {
	return MessaggiWSFormat.estraiNomeFileBreve(urnCompleto);
    }

    // richiama logica di estrazione del nome file per il componente su tivoli a partire dall'urn
    private String estraiNomeFilePerTivoli(String urnCompletoIniz, String urnCompleto) {
	return MessaggiWSFormat.estraiNomeFilePerTivoli(urnCompletoIniz, urnCompleto);
    }

    // richiama logica di estrazione dell'urn del nome file
    public static String estraiNomeFileCompleto(String urnCompleto) {
	return MessaggiWSFormat.estraiNomeFileCompleto(urnCompleto);
    }

    public String getNomeFileBreveTivoli() {
	return nomeFileBreveTivoli;
    }

    public void setNomeFileBreveTivoli(String nomeFileBreveTivoli) {
	this.nomeFileBreveTivoli = nomeFileBreveTivoli;
    }

}
