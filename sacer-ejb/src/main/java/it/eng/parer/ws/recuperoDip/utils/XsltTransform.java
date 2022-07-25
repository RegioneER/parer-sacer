/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.recuperoDip.utils;

import it.eng.parer.ws.dto.RispostaControlli;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fioravanti_F
 */
public class XsltTransform implements ICompTransformer {

    private static final Logger log = LoggerFactory.getLogger(XsltTransform.class);

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
                rispostaControlli.setDsErr("Errore grave nella conversione XsltTransform: " + ex.getMessage());
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
