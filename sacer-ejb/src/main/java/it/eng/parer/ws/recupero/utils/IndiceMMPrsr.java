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
package it.eng.parer.ws.recupero.utils;

import java.io.File;
import java.io.StringReader;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.ControlliMM;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.recupero.dto.RecuperoMMExt;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.utils.AvanzamentoWs;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.xml.versReqStatoMM.IndiceMM;
import it.eng.parer.ws.xml.versRespStato.StatoConservazione;
import it.eng.spagoLite.security.User;

/**
 *
 * @author Fioravanti_F
 */
public class IndiceMMPrsr {

    private static final Logger log = LoggerFactory.getLogger(IndiceMMPrsr.class);
    private RispostaWSRecupero rispostaWs;
    RecuperoMMExt recupero;
    private RispostaControlli rispostaControlli;
    // l'istanza dell'unità documentaria decodificata dall'XML di recupero
    IndiceMM parsedIndice = null;
    // stateless ejb per i controlli sul db
    ControlliMM controlliMM = null;
    // singleton ejb - cache dei JAXBContext
    XmlContextCache xmlContextCache = null;

    public RispostaWSRecupero getRispostaWs() {
        return rispostaWs;
    }

    public IndiceMMPrsr(RecuperoMMExt rec, RispostaWSRecupero risp) {
        recupero = rec;
        rispostaWs = risp;
        rispostaControlli = new RispostaControlli();
    }

    public void parseXML(String datiXml, User utente) {
        StatoConservazione myEsito = rispostaWs.getIstanzaEsito();
        AvanzamentoWs myAvanzamentoWs = rispostaWs.getAvanzamento();
        StringReader tmpReader;
        parsedIndice = null;

        // recupera l'ejb dei controlli, se possibile - altrimenti segnala errore
        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            try {
                controlliMM = (ControlliMM) new InitialContext().lookup("java:module/ControlliMM");
            } catch (NamingException ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nel recupero dell'EJB ControlliMM " + ex.getMessage());
                log.error("Errore nel recupero dell'EJB ControlliMM ", ex);
            }
        }

        // recupera l'ejb singleton, se possibile - altrimenti segnala errore
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            try {
                xmlContextCache = (XmlContextCache) new InitialContext()
                        .lookup("java:module/XmlContextCache");
            } catch (NamingException ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nel recupero dell'EJB singleton XMLContext " + ex.getMessage());
                log.error("Errore nel recupero dell'EJB singleton XMLContext ", ex);
            }
        }

        XmlValidationEventHandler validationHandler = new XmlValidationEventHandler();
        Unmarshaller unmarshaller = null;
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            try {
                unmarshaller = xmlContextCache.getVersReqStatoMMCtx_Indice().createUnmarshaller();
                unmarshaller.setSchema(xmlContextCache.getSchemaOfVersReqStatoMM());
                unmarshaller.setEventHandler(validationHandler);
            } catch (JAXBException ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nella creazione dell'unmarshaller " + ex.getMessage());
                log.error("Errore nella creazione dell'unmarshaller dell'UnitaDocumentaria per ",
                        ex);
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            tmpReader = new StringReader(datiXml);
            try {
                myAvanzamentoWs.setFase("Unmarshall XML").logAvanzamento();
                parsedIndice = (IndiceMM) unmarshaller.unmarshal(tmpReader);
                recupero.setIndiceMM(parsedIndice);
            } catch (UnmarshalException e) {
                ValidationEvent event = validationHandler.getFirstErrorValidationEvent();
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.MM_XSD_001_001, event.getMessage());
            } catch (ValidationException e) {
                ValidationEvent event = validationHandler.getFirstErrorValidationEvent();
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.MM_XSD_001_002, event.getMessage());
            } catch (Exception e) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.MM_XSD_001_002, e.getMessage());
            }
        }

        // *********************************************************************************************
        String prefissoPathPerApp = "";
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli = controlliMM.caricaRootPath(parsedIndice.getApplicativoChiamante(),
                    ControlliMM.TipiRootPath.OUT);
            if (rispostaControlli.isrBoolean()) {
                prefissoPathPerApp = rispostaControlli.getrString();
                recupero.setPrefissoPathPerApp(prefissoPathPerApp);
            } else {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            }
        }

        // verifica se presente un riferimento ad un eventuale directory in cui scrivere lo ZIP
        // verifica se la directory è presente sul disco
        String zipFilePath = "";
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            File tmpFile;
            if (parsedIndice == null) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.MM_XSD_001_002,
                        "parsedIndice nullo dopo unmarshalling");
                return;
            }
            if (parsedIndice.getOutputPath() != null && parsedIndice.getOutputPath().length() > 0) {
                // verifica esistenza del file zip
                zipFilePath = prefissoPathPerApp + parsedIndice.getOutputPath();
                tmpFile = new File(zipFilePath);
                recupero.setPathContainerZip(zipFilePath);
                if (!tmpFile.isDirectory() || !tmpFile.canWrite()) {
                    rispostaWs.setSeverity(SeverityEnum.ERROR);
                    // La directory {0} dichiarata nell'indiceMM non esiste o non è raggiungibile
                    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.MM_FILE_004_001, zipFilePath);
                }
            }
        }
    }
}
