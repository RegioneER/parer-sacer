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

package it.eng.tpi.util;

import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.xml.utils.XmlUtils;
import it.eng.tpi.bean.*;
import it.eng.tpi.dto.EsitoConnessione;
import it.eng.tpi.dto.RichiestaTpi.TipoRichiesta;
import it.eng.tpi.dto.RichiestaTpiInput;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RichiestaWSTpi {

    private static Logger log = LoggerFactory.getLogger(RichiestaWSTpi.class);
    private static XmlContextCache xmlContextCache = null;

    public static EsitoConnessione callWs(RichiestaTpiInput input) {
        return callWs(input.getTipoRichiesta(), input.getUrlRichiesta(), input.getParams(),
                input.getTimeout());
    }

    public static EsitoConnessione callWs(TipoRichiesta tipoRichiesta, String url,
            List<NameValuePair> inputParams, int timeout) {

        EsitoConnessione esitoConnessione = new EsitoConnessione(tipoRichiesta);

        try {
            xmlContextCache = (XmlContextCache) new InitialContext()
                    .lookup("java:app/Parer-ejb/XmlContextCache");
        } catch (NamingException ex) {
            esitoConnessione.setResponse(null);
            esitoConnessione.setXmlResponse(null);
            esitoConnessione.setErroreConnessione(false);
            esitoConnessione.setDescrErrConnessione(null);
            esitoConnessione.setCodiceEsito(EsitoConnessione.Esito.KO.name());
            esitoConnessione.setCodiceErrore(null);
            esitoConnessione
                    .setMessaggioErrore("Impossibile decodificare il messaggio di risposta");
            log.error("Errore nel recupero dell'EJB singleton XMLContext ", ex);
        }

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout)
                .setSocketTimeout(timeout).build();

        try (CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig).build()) {

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(inputParams, StandardCharsets.UTF_8));

            log.debug("Chiamata del servizio all'url {}", url);

            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {

                int statusCode = response.getStatusLine().getStatusCode();

                boolean isErrore = (statusCode == 404);

                if (isErrore) {
                    setError(esitoConnessione, "Server returned HTTP status=" + statusCode);
                }

                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    byte[] entityBA = EntityUtils.toByteArray(resEntity);
                    InputStream responseIS = new ByteArrayInputStream(entityBA);
                    String xmlResponse = new String(entityBA, StandardCharsets.UTF_8);

                    processResponse(responseIS, tipoRichiesta, esitoConnessione);

                    esitoConnessione.setXmlResponse(xmlResponse);
                    if (!isErrore) {
                        esitoConnessione.setErroreConnessione(false);
                    }
                }
            }

        } catch (SocketTimeoutException timeoutEx) {
            log.error("Timeout durante la chiamata WS", timeoutEx);
            setError(esitoConnessione, "Timeout durante la chiamata WS");

            esitoConnessione.setCodiceEsito(EsitoConnessione.Esito.KO.name());
            esitoConnessione.setMessaggioErrore("Timeout durante la chiamata WS");
        } catch (Exception ex) {
            log.error("Impossibile decodificare il messaggio di risposta", ex);
            esitoConnessione.setResponse(null);
            esitoConnessione.setXmlResponse(null);
            setError(esitoConnessione, null);
            esitoConnessione.setCodiceEsito(EsitoConnessione.Esito.KO.name());
            esitoConnessione.setCodiceErrore(null);
            esitoConnessione
                    .setMessaggioErrore("Impossibile decodificare il messaggio di risposta");
        }
        return esitoConnessione;
    }

    private static void setError(EsitoConnessione esito, String errorMessage) {
        esito.setErroreConnessione(true);
        esito.setDescrErrConnessione(errorMessage);
    }

    private static void processResponse(InputStream responseIS, TipoRichiesta tipoRichiesta,
            EsitoConnessione esitoConnessione) {
        try {
            Unmarshaller um;
            switch (tipoRichiesta) {
            case STATO_ARK_CARTELLA:
                um = xmlContextCache.getTpiStatoArkCartellaCtx().createUnmarshaller();
                StatoArchiviazioneCartellaRisposta statoArk = XmlUtils.unmarshallResponse(um,
                        responseIS, StatoArchiviazioneCartellaRisposta.class);
                esitoConnessione.setResponse(statoArk);
                esitoConnessione.setCodiceEsito(statoArk.getEsito().getCdEsito().name());
                esitoConnessione.setCodiceErrore(statoArk.getEsito().getCdErr());
                esitoConnessione.setMessaggioErrore(statoArk.getEsito().getDlErr());
                break;

            case ELIMINA_CARTELLA_ARK:
                um = xmlContextCache.getTpiEliminaCartellaArkCtx().createUnmarshaller();
                EliminaCartellaArchiviataRisposta elimArk = XmlUtils.unmarshallResponse(um,
                        responseIS, EliminaCartellaArchiviataRisposta.class);
                esitoConnessione.setResponse(elimArk);
                esitoConnessione.setCodiceEsito(elimArk.getEsito().getCdEsito().name());
                esitoConnessione.setCodiceErrore(elimArk.getEsito().getCdErr());
                esitoConnessione.setMessaggioErrore(elimArk.getEsito().getDlErr());
                break;

            case REGISTRA_CARTELLA_RI_ARK:
                um = xmlContextCache.getTpiRegistraCartellaRiArkCtx().createUnmarshaller();
                RegistraCartellaRiArkRisposta regRiArk = XmlUtils.unmarshallResponse(um, responseIS,
                        RegistraCartellaRiArkRisposta.class);
                esitoConnessione.setResponse(regRiArk);
                esitoConnessione.setCodiceEsito(regRiArk.getEsito().getCdEsito().name());
                esitoConnessione.setCodiceErrore(regRiArk.getEsito().getCdErr());
                esitoConnessione.setMessaggioErrore(regRiArk.getEsito().getDlErr());
                break;

            case RETRIEVE_FILE_UNITA_DOC:
                um = xmlContextCache.getTpiRetrieveFileUnitaDocCtx().createUnmarshaller();
                RetrieveFileUnitaDocRisposta retFileUniDoc = XmlUtils.unmarshallResponse(um,
                        responseIS, RetrieveFileUnitaDocRisposta.class);
                esitoConnessione.setResponse(retFileUniDoc);
                esitoConnessione.setCodiceEsito(retFileUniDoc.getEsito().getCdEsito().name());
                esitoConnessione.setCodiceErrore(retFileUniDoc.getEsito().getCdErr());
                esitoConnessione.setMessaggioErrore(retFileUniDoc.getEsito().getDlErr());
                break;

            case SCHEDULAZIONI_JOB_TPI:
                um = xmlContextCache.getTpiSchedulazioniJobTPICtx().createUnmarshaller();
                SchedulazioniJobTPIRisposta schedJob = XmlUtils.unmarshallResponse(um, responseIS,
                        SchedulazioniJobTPIRisposta.class);
                esitoConnessione.setResponse(schedJob);
                esitoConnessione.setCodiceEsito(schedJob.getEsito().getCdEsito().name());
                esitoConnessione.setCodiceErrore(schedJob.getEsito().getCdErr());
                esitoConnessione.setMessaggioErrore(schedJob.getEsito().getDlErr());
                break;
            }

            esitoConnessione.setErroreConnessione(false);

        } catch (Exception e) {
            final String msg = "Errore nella risposta: l'XML non rispetta l'XSD associato";
            log.error(msg, e);
            esitoConnessione.setCodiceEsito(EsitoConnessione.Esito.KO.name());
            esitoConnessione.setCodiceErrore(null);
            esitoConnessione
                    .setMessaggioErrore("Impossibile decodificare il messaggio di risposta");
            log.error("Errore nel recupero dell'EJB singleton XMLContext ", e);

        }
    }

}
