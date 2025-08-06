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

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;

import it.eng.parer.ws.dto.RispostaControlli;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

/**
 *
 * @author Fioravanti_F
 */
public class XsltTransform implements ICompTransformer {

    private class ErrListener implements ErrorListener {

	private boolean fallito;
	private String messaggio;

	@Override
	public void warning(TransformerException exception) throws TransformerException {
	    fallito = true;
	    messaggio = "WARNING " + exception.getMessageAndLocation();
	    throw exception;
	}

	@Override
	public void error(TransformerException exception) throws TransformerException {
	    fallito = true;
	    messaggio = "ERROR " + exception.getMessageAndLocation();
	    throw exception;
	}

	@Override
	public void fatalError(TransformerException exception) throws TransformerException {
	    fallito = true;
	    messaggio = "FATAL " + exception.getMessageAndLocation();
	    throw exception;
	}

	public boolean isFallito() {
	    return fallito;
	}

	public String getMessaggio() {
	    return messaggio;
	}

    }

    @Override
    public RispostaControlli convertiSuStream(ParametriTrasf parametri) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	try {
	    // predispongo la trasformazione XSLT
	    Source src = new StreamSource(parametri.getFileXml());
	    Source xslt = new StreamSource(parametri.getFileXslt());

	    // inizializzazione
	    ErrListener errListener = new ErrListener();
	    Processor proc = new Processor(false);
	    XsltCompiler comp = proc.newXsltCompiler();
	    XsltExecutable exp = comp.compile(xslt);

	    XdmNode source = proc.newDocumentBuilder().build(src);
	    Serializer out = proc.newSerializer(parametri.getFileXmlOut());
	    XsltTransformer trans = exp.load();
	    trans.setInitialContextNode(source);
	    trans.setDestination(out);
	    trans.setErrorListener(errListener);
	    trans.transform();

	    if (!errListener.isFallito()) {
		rispostaControlli.setrBoolean(true);
	    } else {
		rispostaControlli.setCodErr(null);
		rispostaControlli.setDsErr("Errore: " + errListener.getMessaggio());
	    }

	} catch (Exception ex) {
	    // se cod_err è nullo, vuol dire che l'xslt è errato e va marcato come tale
	    if (ex.getMessage() != null && !ex.getMessage().isEmpty()) {
		rispostaControlli.setCodErr(null);
		rispostaControlli.setDsErr(
			"Errore grave nella conversione XsltTransform: " + ex.getMessage());
	    } else {
		rispostaControlli.setCodErr(null);
		rispostaControlli.setDsErr(
			"Errore grave nella conversione XsltTransform: probabilmente il file xslt è malformato.");
	    }
	} finally {
	    IOUtils.closeQuietly(parametri.getFileXslt());
	    IOUtils.closeQuietly(parametri.getFileXml());
	}

	return rispostaControlli;
    }

}
