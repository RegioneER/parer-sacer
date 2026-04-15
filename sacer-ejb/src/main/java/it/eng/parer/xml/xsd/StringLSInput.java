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

package it.eng.parer.xml.xsd;

import org.w3c.dom.ls.LSInput;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

/**
 * Implementazione di LSInput per fornire XSD da validazione in-memory.
 *
 * Questa classe viene usata dal LSResourceResolver per restituire il contenuto di uno schema XSD
 * importato/incluso, caricato dal database.
 */
public class StringLSInput implements LSInput {

    private String publicId;
    private String systemId;
    private String baseURI;
    private String encoding;
    private Reader characterStream;
    private String stringData;

    public StringLSInput() {
    }

    public StringLSInput(String systemId, String stringData) {
        this.systemId = systemId;
        this.stringData = stringData;
        this.characterStream = new StringReader(stringData);
        this.encoding = "UTF-8";
    }

    @Override
    public Reader getCharacterStream() {
        return characterStream;
    }

    @Override
    public void setCharacterStream(Reader characterStream) {
        this.characterStream = characterStream;
    }

    @Override
    public InputStream getByteStream() {
        return null;
    }

    @Override
    public void setByteStream(InputStream byteStream) {
        // Non implementato - usiamo il character stream
    }

    @Override
    public String getStringData() {
        return stringData;
    }

    @Override
    public void setStringData(String stringData) {
        this.stringData = stringData;
    }

    @Override
    public String getSystemId() {
        return systemId;
    }

    @Override
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override
    public String getPublicId() {
        return publicId;
    }

    @Override
    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    @Override
    public String getBaseURI() {
        return baseURI;
    }

    @Override
    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public boolean getCertifiedText() {
        return false;
    }

    @Override
    public void setCertifiedText(boolean certifiedText) {
        // Non implementato
    }
}
