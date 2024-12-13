/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package it.eng.parer.ws.recupero.utils;

import it.eng.parer.entity.AroLogStatoConservUd;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.datatype.XMLGregorianCalendar;

import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.VolVolumeConserv;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.recupero.ejb.ControlliRecupero;
import it.eng.parer.ws.utils.AvanzamentoWs;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.parer.ws.xml.versReqStato.Recupero;
import it.eng.parer.ws.xml.versRespStato.ChiaveType;
import it.eng.parer.ws.xml.versRespStato.DatiUnitaDocType;
import it.eng.parer.ws.xml.versRespStato.ECEsitoExtType;
import it.eng.parer.ws.xml.versRespStato.ECEsitoPosNegType;
import it.eng.parer.ws.xml.versRespStato.EsitoChiamataWSType;
import it.eng.parer.ws.xml.versRespStato.EsitoGenericoType;
import it.eng.parer.ws.xml.versRespStato.IndiceProveConservazione;
import it.eng.parer.ws.xml.versRespStato.PCVolumeType;
import it.eng.parer.ws.xml.versRespStato.SCVersatoreType;
import it.eng.parer.ws.xml.versRespStato.StatoConservazione;
import it.eng.parer.ws.xml.versRespStato.StatoConservazioneType;
import it.eng.parer.ws.xml.versRespStato.LogStatoConservazioneType;
import it.eng.parer.ws.xml.versRespStato.LogType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

/**
 *
 * @author Fioravanti_F
 */
public class RecuperoXmlGen {

    private RispostaWSRecupero rispostaWs;
    private RispostaControlli rispostaControlli;
    // l'istanza della request decodificata dall'XML di versamento
    Recupero parsedUnitaDoc = null;
    // stateless ejb per la lettura di informazioni relative ai dati da recuperare
    ControlliRecupero controlliRecupero = null;

    public RispostaWSRecupero getRispostaWs() {
        return rispostaWs;
    }

    public RecuperoXmlGen(RispostaWSRecupero risp) throws NamingException {
        rispostaWs = risp;
        rispostaControlli = new RispostaControlli();

        // recupera l'ejb per la lettura di informazioni, se possibile
        controlliRecupero = (ControlliRecupero) new InitialContext().lookup("java:module/ControlliRecupero");
    }

    public void generaStatoConservazione(RecuperoExt recupero) {
        StatoConservazione myEsito = rispostaWs.getIstanzaEsito();
        AvanzamentoWs myAvanzamentoWs = rispostaWs.getAvanzamento();
        parsedUnitaDoc = recupero.getStrutturaRecupero();

        CSVersatore tmpCsVersatore = null;
        CSChiave tmpCsChiave = null;

        // genero i nomi delle cartelle relative alla struttura ed all'UD per il recupero
        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            rispostaControlli = controlliRecupero.leggiChiaveUnitaDoc(recupero.getParametriRecupero().getIdUnitaDoc());
            if (rispostaControlli.isrBoolean()) {
                tmpCsChiave = (CSChiave) rispostaControlli.getrObject();
            } else {
                setRispostaWsError();
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            }
        }

        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            rispostaControlli = controlliRecupero
                    .leggiVersatoreUnitaDoc(recupero.getParametriRecupero().getIdUnitaDoc());
            if (rispostaControlli.isrBoolean()) {
                tmpCsVersatore = (CSVersatore) rispostaControlli.getrObject();
            } else {
                setRispostaWsError();
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            }
        }

        // crea Stato unità doc
        // compila Chiave e tipo conservazione
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            myEsito.setUnitaDocumentaria(new DatiUnitaDocType());
            SCVersatoreType tmpVersatore = new SCVersatoreType();
            tmpVersatore.setAmbiente(tmpCsVersatore.getAmbiente());
            tmpVersatore.setEnte(tmpCsVersatore.getEnte());
            tmpVersatore.setStruttura(tmpCsVersatore.getStruttura());
            tmpVersatore.setUserID(parsedUnitaDoc.getVersatore().getUserID());
            //
            if (parsedUnitaDoc.getVersatore().getUtente() != null
                    && !parsedUnitaDoc.getVersatore().getUtente().isEmpty()) {
                tmpVersatore.setUtente(parsedUnitaDoc.getVersatore().getUtente());
            }
            myEsito.getUnitaDocumentaria().setVersatore(tmpVersatore);

            ChiaveType tmpChiave = new ChiaveType();
            tmpChiave.setAnno(BigInteger.valueOf(tmpCsChiave.getAnno()));
            tmpChiave.setNumero(tmpCsChiave.getNumero());
            tmpChiave.setTipoRegistro(tmpCsChiave.getTipoRegistro());
            myEsito.getUnitaDocumentaria().setChiave(tmpChiave);
        }

        // compila Stato Conservazione
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            String tmpUrnUd = MessaggiWSFormat.formattaUrnDocUniDoc(
                    MessaggiWSFormat.formattaBaseUrnUnitaDoc(MessaggiWSFormat.formattaUrnPartVersatore(tmpCsVersatore),
                            MessaggiWSFormat.formattaUrnPartUnitaDoc(tmpCsChiave)));
            myEsito.getUnitaDocumentaria().setUrnUD(tmpUrnUd);
            //
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiUnitaDoc(recupero.getParametriRecupero().getIdUnitaDoc());
            if (rispostaControlli.isrBoolean() == false) {
                setRispostaWsError();
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                myEsito.getUnitaDocumentaria().setStatoConservazioneUD(StatoConservazioneType
                        .valueOf((((AroUnitaDoc) rispostaControlli.getrObject()).getTiStatoConservazione())));
            }
        }
    }

    public void generaIndiceProveCons(RecuperoExt recupero) {
        parsedUnitaDoc = recupero.getStrutturaRecupero();
        IndiceProveConservazione myIndice = null;
        String prefissoVolume = "proveConservazione_vol-";

        CSChiave tmpCSChiave = new CSChiave();
        tmpCSChiave.setAnno(parsedUnitaDoc.getChiave().getAnno().longValue());
        tmpCSChiave.setNumero(parsedUnitaDoc.getChiave().getNumero());
        tmpCSChiave.setTipoRegistro(parsedUnitaDoc.getChiave().getTipoRegistro());

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaWs.setIndiceProveConservazione(new IndiceProveConservazione());
            myIndice = rispostaWs.getIndiceProveConservazione();

            myIndice.setVersione(recupero.getDescrizione().getVersione(recupero.getWsVersions()));
            myIndice.setVersioneXMLChiamata(parsedUnitaDoc.getVersione());

            XMLGregorianCalendar d = XmlDateUtility.dateToXMLGregorianCalendar(new Date());
            myIndice.setDataRichiestaStato(d);

            myIndice.setEsitoGenerale(new EsitoGenericoType());
            myIndice.getEsitoGenerale().setCodiceEsito(ECEsitoExtType.POSITIVO);
            myIndice.getEsitoGenerale().setCodiceErrore("");
            myIndice.getEsitoGenerale().setMessaggioErrore("");

            myIndice.setEsitoChiamataWS(new EsitoChiamataWSType());
            myIndice.getEsitoChiamataWS().setCredenzialiOperatore(ECEsitoPosNegType.POSITIVO);
            myIndice.getEsitoChiamataWS().setVersioneWSCorretta(ECEsitoPosNegType.POSITIVO);

            ChiaveType tmpChiave = new ChiaveType();
            tmpChiave.setAnno(BigInteger.valueOf(tmpCSChiave.getAnno()));
            tmpChiave.setNumero(tmpCSChiave.getNumero());
            tmpChiave.setTipoRegistro(tmpCSChiave.getTipoRegistro());
            myIndice.setChiave(tmpChiave);

            rispostaWs.setNomeFile("PC-" + this.calcolaNomeFileZip(tmpCSChiave));
        }

        // verifica se è in un qualche volume e li aggiunge allo stato
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiVolumiUnitaDoc(recupero.getParametriRecupero().getIdUnitaDoc());
            if (rispostaControlli.isrBoolean() == false) {
                setRispostaWsError();
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                List<VolVolumeConserv> tmpVolumi = (List<VolVolumeConserv>) rispostaControlli.getrObject();
                if (!tmpVolumi.isEmpty()) {
                    myIndice.setVolumi(new IndiceProveConservazione.Volumi());
                    for (VolVolumeConserv tmpVolConserv : tmpVolumi) {
                        PCVolumeType tmpVolume = new PCVolumeType();
                        tmpVolume.setIdVolume(Long.toString(tmpVolConserv.getIdVolumeConserv()));
                        tmpVolume.setNomeVolume(tmpVolConserv.getNmVolumeConserv());
                        tmpVolume.setDirectory(prefissoVolume + tmpVolConserv.getIdVolumeConserv());
                        myIndice.getVolumi().getVolume().add(tmpVolume);
                    }
                } else {
                    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.UD_005_002,
                            MessaggiWSFormat.formattaUrnPartUnitaDoc(tmpCSChiave));
                    myIndice.setNota("L'unità documentaria non è inserita in alcun volume / elenco di versamento.");
                }
            }
        }
    }

    private String calcolaNomeFileZip(CSChiave chiave) {
        StringBuilder tmpString = new StringBuilder();

        tmpString.append("UD_");
        tmpString.append(chiave.getTipoRegistro());
        tmpString.append("-");
        tmpString.append(chiave.getAnno());
        tmpString.append("-");
        tmpString.append(chiave.getNumero());
        tmpString.append(".zip");

        return tmpString.toString().replace(':', '_');
    }

    private void setRispostaWsError() {
        rispostaWs.setSeverity(SeverityEnum.ERROR);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
    }

    public void generaLogStatoConservazione(RecuperoExt recupero) {
        StatoConservazione myEsito = rispostaWs.getIstanzaEsito();
        AvanzamentoWs myAvanzamentoWs = rispostaWs.getAvanzamento();
        parsedUnitaDoc = recupero.getStrutturaRecupero();

        // compila Log Stato Conservazione
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero
                    .leggiLogStatoConservazione(recupero.getParametriRecupero().getIdUnitaDoc());
            if (rispostaControlli.isrBoolean() == false) {
                setRispostaWsError();
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                List<AroLogStatoConservUd> list = (List<AroLogStatoConservUd>) rispostaControlli.getrObject();
                if (!list.isEmpty()) {
                    List<LogType> l = new ArrayList<>();
                    list.forEach(logStato -> {
                        LogType logUD = new LogType();
                        Instant i = new java.util.Date(logStato.getDtStato().getTime()).toInstant();
                        String dateTimeString = i.toString();
                        XMLGregorianCalendar date2 = null;
                        try {
                            date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTimeString);
                        } catch (DatatypeConfigurationException ex) {
                            Logger.getLogger(RecuperoXmlGen.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (date2 != null) {
                            logUD.setDataEvento(date2);
                        }

                        logUD.setNomeAgente(logStato.getNmAgente());
                        logUD.setTipoEvento(logStato.getTiEvento());
                        logUD.setTipoStatoConservazione(logStato.getTiStatoConservazione());
                        logUD.setModalita(logStato.getTiMod());
                        l.add(logUD);
                    });

                    if (!l.isEmpty()) {
                        myEsito.setLogStatoConservazione(new LogStatoConservazioneType());
                        myEsito.getLogStatoConservazione().getLog().addAll(l);
                    }
                }
            }
        }
    }
}
