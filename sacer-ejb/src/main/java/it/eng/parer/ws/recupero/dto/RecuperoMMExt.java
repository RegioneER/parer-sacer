/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.recupero.dto;

import it.eng.parer.ws.xml.versReqStatoMM.IndiceMM;

/**
 *
 * @author Fioravanti_F
 */
public class RecuperoMMExt extends RecuperoExt {

    private IndiceMM indiceMM;
    private String prefissoPathPerApp;
    private String pathContainerZip;
    //

    public IndiceMM getIndiceMM() {
        return indiceMM;
    }

    public void setIndiceMM(IndiceMM indiceMM) {
        this.indiceMM = indiceMM;
    }

    public String getPrefissoPathPerApp() {
        return prefissoPathPerApp;
    }

    public void setPrefissoPathPerApp(String prefissoPathPerApp) {
        this.prefissoPathPerApp = prefissoPathPerApp;
    }

    public String getPathContainerZip() {
        return pathContainerZip;
    }

    public void setPathContainerZip(String pathContainerZip) {
        this.pathContainerZip = pathContainerZip;
    }
}
