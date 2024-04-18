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

package it.eng.parer.web.util;

import java.math.BigDecimal;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.recupero.dto.ParametriRecupero;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.recupero.dto.WSDescRecUnitaDoc;
import it.eng.parer.ws.recupero.ejb.RecuperoSync;
import it.eng.parer.ws.utils.AvanzamentoWs;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.xml.versReqStato.Recupero;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.security.User;

/**
 *
 * @author Gilioli_P
 */
public class RecuperoWeb {

    private static final Logger log = LoggerFactory.getLogger(RecuperoWeb.class);
    // private String instanceName;
    private final Recupero recupero;
    private final User user;
    private final BigDecimal idUnitaDoc;
    private final CostantiDB.TipoSalvataggioFile tipoSalvataggioFile;
    private final CostantiDB.TipiEntitaRecupero tipoEntitaSacer;
    private BigDecimal idCompDoc;

    /**
     * Costruttore recupero unita documentaria oppure UD per UniSyncro
     *
     * @param recupero
     *            bean Recupero
     * @param user
     *            bean User
     * @param idUnitaDoc
     *            id unita doc
     * @param tipoSalvataggioFile
     *            tipo salvataggio file CostantiDB.TipoSalvataggioFile
     * @param tipoEntitaSacer
     *            tipo entita sacer CostantiDB.TipiEntitaRecupero
     */
    public RecuperoWeb(Recupero recupero, User user, BigDecimal idUnitaDoc,
            CostantiDB.TipoSalvataggioFile tipoSalvataggioFile, CostantiDB.TipiEntitaRecupero tipoEntitaSacer) {
        this.recupero = recupero;
        this.user = user;
        this.idUnitaDoc = idUnitaDoc;
        this.tipoSalvataggioFile = tipoSalvataggioFile;
        // tipoEntita = UNI_DOC o UNI_DOC_UNISYNCRO o UNIDOC_DIP_ESIBIZIONE o UNI_DOC_UNISYNCRO_V2
        this.tipoEntitaSacer = tipoEntitaSacer;
    }

    /**
     * Costruttore recupero documento / componente / sottocomponente
     *
     * @param recupero
     *            bean Recupero
     * @param user
     *            bean User
     * @param idUnitaDoc
     *            id unita doc
     * @param idCompDoc
     *            id documento componente
     * @param tipoSalvataggioFile
     *            tipo salvataggio file CostantiDB.TipoSalvataggioFile
     * @param tipoEntitaSacer
     *            tipo entita sacer CostantiDB.TipiEntitaRecupero
     */
    public RecuperoWeb(Recupero recupero, User user, BigDecimal idUnitaDoc, BigDecimal idCompDoc,
            CostantiDB.TipoSalvataggioFile tipoSalvataggioFile, CostantiDB.TipiEntitaRecupero tipoEntitaSacer) {
        this.recupero = recupero;
        this.user = user;
        this.idUnitaDoc = idUnitaDoc;
        this.idCompDoc = idCompDoc;
        this.tipoSalvataggioFile = tipoSalvataggioFile;
        // tipoEntita = DOC DOC_DIP_ESIBIZIONE COMP COMP_DIP_ESIBIZIONE SUB_COMP
        this.tipoEntitaSacer = tipoEntitaSacer;
    }

    public RispostaWSRecupero recuperaOggetto() throws EMFError {
        RecuperoSync recuperoSync;
        RispostaWSRecupero rispostaWs;
        RecuperoExt myRecuperoExt;
        AvanzamentoWs tmpAvanzamento;

        rispostaWs = new RispostaWSRecupero();
        myRecuperoExt = new RecuperoExt();
        WSDescRecUnitaDoc wsdes = new WSDescRecUnitaDoc();
        // recupero.setVersione(wsdes.getVersione(myRecuperoExt.getXmlDefaults()));
        myRecuperoExt.setDescrizione(wsdes);

        tmpAvanzamento = AvanzamentoWs.nuovoAvanzamentoWS("prova", AvanzamentoWs.Funzioni.RecuperoWeb);
        tmpAvanzamento.logAvanzamento();

        // Recupera l'ejb, se possibile - altrimenti segnala errore
        try {
            recuperoSync = (RecuperoSync) new InitialContext().lookup("java:app/Parer-ejb/RecuperoSync");
        } catch (NamingException ex) {
            log.error("Errore nel recupero dell'EJB ", ex);
            throw new EMFError(EMFError.ERROR, "Impossibile recuperare l'ejb: " + ex);
        }

        tmpAvanzamento.setFase("EJB recuperato").logAvanzamento();
        recuperoSync.initRispostaWs(rispostaWs, tmpAvanzamento, myRecuperoExt);
        // set versione after initRispostaWs
        recupero.setVersione(wsdes.getVersione(myRecuperoExt.getWsVersions()));

        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            // Popolo parzialmente RecuperoExt con i valori che mi
            // serviranno in fase di recupero unità documentaria
            ParametriRecupero tmpParametriRecupero = myRecuperoExt.getParametriRecupero();
            tmpParametriRecupero.setIdUnitaDoc(idUnitaDoc.longValue());
            tmpParametriRecupero.setUtente(user);
            tmpParametriRecupero.setTipoRichiedente(JobConstants.TipoSessioniRecupEnum.DOWNLOAD);
            tmpParametriRecupero.setTipoEntitaSacer(tipoEntitaSacer);
            switch (tipoEntitaSacer) {
            case DOC:
            case DOC_DIP_ESIBIZIONE:
                tmpParametriRecupero.setIdDocumento(idCompDoc.longValue());
                break;
            case COMP:
            case COMP_DIP_ESIBIZIONE:
            case SUB_COMP:
                tmpParametriRecupero.setIdComponente(idCompDoc.longValue());
                break;
            default:
                break;
            }

            myRecuperoExt.setDatiXml(" ");
            myRecuperoExt.setStrutturaRecupero(recupero);
            myRecuperoExt.setVersioneWsChiamata("---");
            myRecuperoExt.setTipoSalvataggioFile(tipoSalvataggioFile);
            // prepara risposta
            recuperoSync.recuperaOggetto(rispostaWs, myRecuperoExt, System.getProperty("java.io.tmpdir"));
        }

        return rispostaWs;
    }

    public RispostaWSRecupero recuperaOggettoSip() throws EMFError {
        DownloadSip downloadSip;
        RispostaWSRecupero rispostaWs;
        RecuperoExt myRecuperoExt;
        AvanzamentoWs tmpAvanzamento;

        rispostaWs = new RispostaWSRecupero();
        myRecuperoExt = new RecuperoExt();
        WSDescRecUnitaDoc wsdes = new WSDescRecUnitaDoc();
        // recupero.setVersione(wsdes.getVersione(myRecuperoExt.getXmlDefaults()));
        myRecuperoExt.setDescrizione(wsdes);

        tmpAvanzamento = AvanzamentoWs.nuovoAvanzamentoWS("prova", AvanzamentoWs.Funzioni.RecuperoWeb);
        tmpAvanzamento.logAvanzamento();

        // Recupera l'ejb, se possibile - altrimenti segnala errore
        try {
            downloadSip = (DownloadSip) new InitialContext().lookup("java:app/Parer-ejb/DownloadSip");
        } catch (NamingException ex) {
            log.error("Errore nel recupero dell'EJB ", ex);
            throw new EMFError(EMFError.ERROR, "Impossibile recuperare l'ejb: " + ex);
        }

        tmpAvanzamento.setFase("EJB recuperato").logAvanzamento();
        downloadSip.initRispostaWs(rispostaWs, tmpAvanzamento, myRecuperoExt);
        // versione
        recupero.setVersione(wsdes.getVersione(myRecuperoExt.getWsVersions()));

        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            // Popolo parzialmente RecuperoExt con i valori che mi
            // serviranno in fase di recupero SIP per unità documentaria o documento
            ParametriRecupero tmpParametriRecupero = myRecuperoExt.getParametriRecupero();
            tmpParametriRecupero.setIdUnitaDoc(idUnitaDoc.longValue());
            tmpParametriRecupero.setUtente(user);
            tmpParametriRecupero.setTipoRichiedente(JobConstants.TipoSessioniRecupEnum.DOWNLOAD);
            tmpParametriRecupero.setTipoEntitaSacer(tipoEntitaSacer);
            if (tipoEntitaSacer.name().equals(CostantiDB.TipiEntitaRecupero.DOC.name())) {
                tmpParametriRecupero.setIdDocumento(idCompDoc.longValue());
            }
            myRecuperoExt.setDatiXml(" ");
            myRecuperoExt.setStrutturaRecupero(recupero);
            myRecuperoExt.setVersioneWsChiamata("---");
            myRecuperoExt.setTipoSalvataggioFile(tipoSalvataggioFile);
            // prepara risposta
            downloadSip.recuperaSip(rispostaWs, myRecuperoExt, System.getProperty("java.io.tmpdir"));
        }
        return rispostaWs;
    }

    public RispostaWSRecupero recuperaReportFirma() throws EMFError {
        DownloadReportFirma downloadReportFirma;
        RispostaWSRecupero rispostaWs;
        RecuperoExt myRecuperoExt;
        AvanzamentoWs tmpAvanzamento;

        rispostaWs = new RispostaWSRecupero();
        myRecuperoExt = new RecuperoExt();
        WSDescRecUnitaDoc wsdes = new WSDescRecUnitaDoc();
        // recupero.setVersione(wsdes.getVersione(myRecuperoExt.getXmlDefaults()));
        myRecuperoExt.setDescrizione(wsdes);

        tmpAvanzamento = AvanzamentoWs.nuovoAvanzamentoWS("prova", AvanzamentoWs.Funzioni.RecuperoWeb);
        tmpAvanzamento.logAvanzamento();

        // Recupera l'ejb, se possibile - altrimenti segnala errore
        try {
            downloadReportFirma = (DownloadReportFirma) new InitialContext()
                    .lookup("java:app/Parer-ejb/DownloadReportFirma");
        } catch (NamingException ex) {
            log.error("Errore nel recupero dell'EJB ", ex);
            throw new EMFError(EMFError.ERROR, "Impossibile recuperare l'ejb: " + ex);
        }

        tmpAvanzamento.setFase("EJB recuperato").logAvanzamento();
        downloadReportFirma.initRispostaWs(rispostaWs, tmpAvanzamento, myRecuperoExt);
        // versione
        recupero.setVersione(wsdes.getVersione(myRecuperoExt.getWsVersions()));

        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            // Popolo parzialmente RecuperoExt con i valori che mi
            // serviranno in fase di recupero SIP per unità documentaria o documento
            ParametriRecupero tmpParametriRecupero = myRecuperoExt.getParametriRecupero();
            tmpParametriRecupero.setIdUnitaDoc(idUnitaDoc.longValue());
            tmpParametriRecupero.setUtente(user);
            tmpParametriRecupero.setTipoRichiedente(JobConstants.TipoSessioniRecupEnum.DOWNLOAD);
            tmpParametriRecupero.setTipoEntitaSacer(tipoEntitaSacer);
            tmpParametriRecupero.setIdComponente(idCompDoc.longValue());
            myRecuperoExt.setDatiXml(" ");
            myRecuperoExt.setStrutturaRecupero(recupero);
            myRecuperoExt.setVersioneWsChiamata("---");
            myRecuperoExt.setTipoSalvataggioFile(tipoSalvataggioFile);
            // prepara risposta
            downloadReportFirma.recuperaReportFirma(rispostaWs, myRecuperoExt, System.getProperty("java.io.tmpdir"));
        }
        return rispostaWs;
    }
}
