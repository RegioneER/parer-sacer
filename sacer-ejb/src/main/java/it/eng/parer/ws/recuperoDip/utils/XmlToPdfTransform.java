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

import java.io.File;
import java.io.StringWriter;
import java.util.logging.Level;

import javax.xml.XMLConstants;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.configuration.Configuration;
import org.apache.fop.configuration.ConfigurationException;
import org.apache.fop.configuration.DefaultConfigurationBuilder;
import org.apache.fop.events.Event;
import org.apache.fop.events.EventFormatter;
import org.apache.fop.events.EventListener;
import org.apache.fop.events.model.EventSeverity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.utils.MessaggiWSBundle;

/**
 *
 * @author Fioravanti_F
 */
public class XmlToPdfTransform implements ICompTransformer {

    private static final Logger log = LoggerFactory.getLogger(XmlToPdfTransform.class);

    private class SysOutEventListener implements EventListener {

	StringWriter tmpWriter = new StringWriter();

	@Override
	public void processEvent(Event event) {
	    String msg = EventFormatter.format(event);
	    EventSeverity severity = event.getSeverity();

	    if (severity == EventSeverity.ERROR || severity == EventSeverity.FATAL) {
		tmpWriter.append(msg + "; ");
	    }
	}

	public String getErrors() {
	    return tmpWriter.toString();
	}
    }

    @Override
    public RispostaControlli convertiSuStream(ParametriTrasf parametri) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	try {
	    // Inizializzo la factory (sarebbe consigliabile riusarla...)
	    DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
	    Configuration cfg = cfgBuilder.build(
		    this.getClass().getClassLoader().getResourceAsStream("META-INF/fop.xconf"));
	    FopFactoryBuilder fopFactoryBuilder = new FopFactoryBuilder(new File(".").toURI())
		    .setConfiguration(cfg);

	    FopFactory fopFactory = fopFactoryBuilder.build();
	    SysOutEventListener tmpOutListener = new SysOutEventListener();
	    FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
	    foUserAgent.getEventBroadcaster().addEventListener(tmpOutListener);
	    Fop fop = null;
	    // inizializzo FOP con il tipo di output desiderato
	    fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, parametri.getFileXmlOut());
	    // gli eventi SAX verranno processati dall'handler FOP
	    Result res = new SAXResult(fop.getDefaultHandler());
	    // predispongo la trasformazione XSLT ed FO
	    Source src = null;
	    if (parametri.getFileXslFo() != null) {
		src = new StreamSource(parametri.getFileXslFo());
	    } else {
		src = new StreamSource(parametri.getFileXml());
	    }
	    // iniz. JAXP
	    // Nota: al fine di evitare problemi di classloading e "override" del parser (vedi
	    // libreria Saxon-HE)
	    // viene esplicitato a codice quale impementazione (xalan standard in questo caso)
	    // utilizzare
	    TransformerFactory factory = TransformerFactory.newInstance();
	    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
	    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
	    Transformer transformer = null;
	    // verifico se devo effettuare la trasformazione XSLT o solo quella FO
	    if (parametri.getFileXslt() != null) {
		transformer = factory.newTransformer(new StreamSource(parametri.getFileXslt()));
	    } else {
		transformer = factory.newTransformer(); // identity transformer
	    }
	    if (transformer != null) {
		// trasformazione XSLT ed FO
		try {
		    transformer.transform(src, res);
		    if (tmpOutListener.getErrors() != null
			    && !tmpOutListener.getErrors().isEmpty()) {
			rispostaControlli.setCodErr(null);
			rispostaControlli.setDsErr("Errore nella conversione XmlToPdfTransform: "
				+ tmpOutListener.getErrors());
		    } else {
			rispostaControlli.setrBoolean(true);
		    }
		} catch (Exception ex) {
		    // se cod_err è nullo, vuol dire che l'xslt è errato e va marcato come tale
		    if (tmpOutListener.getErrors() != null
			    && !tmpOutListener.getErrors().isEmpty()) {
			rispostaControlli.setCodErr(null);
			rispostaControlli.setDsErr("Errore nella conversione XmlToPdfTransform: "
				+ tmpOutListener.getErrors());
		    } else if (ex.getMessage() != null && !ex.getMessage().isEmpty()) {
			rispostaControlli.setCodErr(null);
			rispostaControlli
				.setDsErr("Errore grave nella conversione XmlToPdfTransform: "
					+ ex.getMessage());
		    } else {
			rispostaControlli.setCodErr(null);
			rispostaControlli.setDsErr(
				"Errore grave nella conversione XmlToPdfTransform: probabilmente il file xslt è malformato.");
		    }
		}
	    } else {
		// se cod_err è nullo, vuol dire che l'xslt è errato e va marcato come tale
		rispostaControlli.setCodErr(null);
		rispostaControlli.setDsErr(
			"Errore nella conversione XmlToPdfTransform: il file xslt non è valido");
	    }

	} catch (TransformerException | FOPException ex) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione XmlToPdfTransform.convertiSuStream " + ex.getMessage()));
	    log.error("Eccezione XmlToPdfTransform.convertiSuStream " + ex);
	} catch (ConfigurationException ex) {
	    java.util.logging.Logger.getLogger(XmlToPdfTransform.class.getName()).log(Level.SEVERE,
		    null, ex);
	} finally {
	    IOUtils.closeQuietly(parametri.getFileXslt());
	    IOUtils.closeQuietly(parametri.getFileXslFo());
	    IOUtils.closeQuietly(parametri.getFileXml());
	}

	return rispostaControlli;
    }

}
