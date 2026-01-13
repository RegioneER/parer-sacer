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
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.ws.recuperoDip.utils;

import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Fioravanti_F
 */
public class ParametriTrasf {

    private InputStream fileXml;
    private InputStream fileXslt;
    private InputStream fileXslFo;
    private OutputStream fileXmlOut;

    public InputStream getFileXml() {
        return fileXml;
    }

    public void setFileXml(InputStream fileXml) {
        this.fileXml = fileXml;
    }

    public InputStream getFileXslt() {
        return fileXslt;
    }

    public void setFileXslt(InputStream fileXslt) {
        this.fileXslt = fileXslt;
    }

    public OutputStream getFileXmlOut() {
        return fileXmlOut;
    }

    public void setFileXmlOut(OutputStream fileXmlOut) {
        this.fileXmlOut = fileXmlOut;
    }

    public InputStream getFileXslFo() {
        return fileXslFo;
    }

    public void setFileXslFo(InputStream fileXslFo) {
        this.fileXslFo = fileXslFo;
    }

}
