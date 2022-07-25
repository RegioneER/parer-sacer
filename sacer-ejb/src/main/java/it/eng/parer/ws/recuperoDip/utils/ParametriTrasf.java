/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
