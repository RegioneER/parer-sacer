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

import it.eng.parer.ws.xml.versReqStatoMM.IndiceMM;

/**
 *
 * @author Fioravanti_F
 */
public class RecuperoMMExt extends RecuperoExt {

    private static final long serialVersionUID = 1L;
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
