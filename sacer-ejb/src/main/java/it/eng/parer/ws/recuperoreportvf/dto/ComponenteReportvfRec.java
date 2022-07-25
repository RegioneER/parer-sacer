/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
