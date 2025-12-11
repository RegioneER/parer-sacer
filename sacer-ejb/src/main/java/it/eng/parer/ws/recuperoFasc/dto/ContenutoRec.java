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
package it.eng.parer.ws.recuperoFasc.dto;

import it.eng.parer.async.utils.IOUtils;
import it.eng.parer.ws.utils.MessaggiWSFormat;

/**
 *
 * @author DiLorenzo_F
 */
public class ContenutoRec {

    protected long idVerIndiceAipUd;
    protected String urnCompleto;
    protected String urnCompletoIniziale;
    protected String nomeFileBreve;
    protected String nomeFileCompleto;
    protected String estensioneFile;
    protected String mimeType;
    protected String urnOriginaleVersata;
    protected String nomeFileOriginaleVersato;

    public ContenutoRec() {
        super();
    }

    public ContenutoRec(String urnCompleto, String urnCompletoIniziale) {
        super();
        setUrnAndElabNomiFile(urnCompleto, urnCompletoIniziale);
    }

    private void setUrnAndElabNomiFile(String urnCompleto, String urnCompletoIniziale) {
        setUrnCompleto(urnCompleto);
        setUrnCompletoIniziale(urnCompletoIniziale);

        this.nomeFileBreve = estraiNomeFileBreve(urnCompleto);
        this.nomeFileCompleto = estraiNomeFileCompleto(urnCompleto);
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

    public long getIdVerIndiceAipUd() {
        return idVerIndiceAipUd;
    }

    public void setIdVerIndiceAipUd(long idVerIndiceAipUd) {
        this.idVerIndiceAipUd = idVerIndiceAipUd;
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

    public String getNomeFilePerZipAIPV2() {
        if (this.nomeFileBreve != null && this.estensioneFile != null) {
            return this.nomeFileBreve + "." + this.estensioneFile;
        } else {
            return null;
        }
    }

    // richiama logica di estrazione del nome file a partire dall'urn
    private String estraiNomeFileBreve(String urnCompleto) {
        return IOUtils.extractPartUrnName(urnCompleto, true);
    }

    // richiama logica di estrazione dell'urn del nome file
    public static String estraiNomeFileCompleto(String urnCompleto) {
        return MessaggiWSFormat.estraiNomeFileCompleto(urnCompleto);
    }
}
