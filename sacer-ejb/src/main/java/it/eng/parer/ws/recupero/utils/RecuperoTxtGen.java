/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.recupero.utils;

import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.recupero.ejb.ProduzioneDipEsibizione;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.xml.versReqStato.Recupero;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fioravanti_f
 */
public class RecuperoTxtGen {

    private static final Logger log = LoggerFactory.getLogger(RecuperoTxtGen.class);

    private RispostaWSRecupero rispostaWs;
    private RispostaControlli rispostaControlli;
    // l'istanza della request decodificata dall'XML di versamento
    Recupero parsedUnitaDoc = null;
    // stateless ejb per la lettura di informazioni relative ai dati da recuperare
    ProduzioneDipEsibizione dipEsibizione = null;

    public RispostaWSRecupero getRispostaWs() {
        return rispostaWs;
    }

    public RecuperoTxtGen(RispostaWSRecupero risp) {
        rispostaWs = risp;
        rispostaControlli = new RispostaControlli();

        try {
            // recupera l'ejb per la lettura di informazioni, se possibile
            dipEsibizione = (ProduzioneDipEsibizione) new InitialContext()
                    .lookup("java:module/ProduzioneDipEsibizione");
        } catch (NamingException ex) {
            rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    "GestSessRecDip Errore nel recupero dell'EJB dei controlli recupero DIP  " + ex.getMessage());
            log.error("Errore nel recupero dell'EJB dei controlli  recupero DIP ", ex);
        }
    }

    public void generaDipEsibizione(ZipArchiveOutputStream tmpZipOutputStream, RecuperoExt recupero)
            throws IOException {
        RispostaControlli rispostaControlli;

        // individua il modello di comunicazione
        ProduzioneDipEsibizione.TipiOggQryModello toqm = null;
        Long tmpId = null;
        switch (recupero.getParametriRecupero().getTipoEntitaSacer()) {
        case UNI_DOC_DIP_ESIBIZIONE:
            toqm = ProduzioneDipEsibizione.TipiOggQryModello.UNITA_DOC;
            tmpId = recupero.getParametriRecupero().getIdUnitaDoc();
            break;
        case DOC_DIP_ESIBIZIONE:
            toqm = ProduzioneDipEsibizione.TipiOggQryModello.DOC;
            tmpId = recupero.getParametriRecupero().getIdDocumento();
            break;
        case COMP_DIP_ESIBIZIONE:
            toqm = ProduzioneDipEsibizione.TipiOggQryModello.COMP;
            tmpId = recupero.getParametriRecupero().getIdComponente();
            break;
        }

        rispostaControlli = dipEsibizione.caricaModello(recupero.getParametriRecupero().getIdUnitaDoc(),
                ProduzioneDipEsibizione.TipiUsoModello.ESIBIZIONE, toqm);
        if (rispostaControlli.isrBoolean() == false) {
            setRispostaWsError();
            if (rispostaControlli.getCodErr() == null || rispostaControlli.getCodErr().isEmpty()) {
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.RECDIP_003_001, rispostaControlli.getDsErr());
            } else {
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            }
            return;
        }
        String testoModello = rispostaControlli.getrString();

        // leggi i dati con cui popolare il modello
        rispostaControlli = dipEsibizione.caricaDatiDaQuery(rispostaControlli.getrLong(),
                ProduzioneDipEsibizione.TipiUsoQuery.TESTO, tmpId);
        if (rispostaControlli.isrBoolean() == false) {
            setRispostaWsError();
            if (rispostaControlli.getCodErr() == null || rispostaControlli.getCodErr().isEmpty()) {
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.RECDIP_003_001, rispostaControlli.getDsErr());
            } else {
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            }
            return;
        }
        Map<String, String> mappaValori = (HashMap<String, String>) rispostaControlli.getrObject();

        // preparo il DIP per esibizione
        StrSubstitutor sub = new StrSubstitutor(mappaValori);
        String resolvedString = sub.replace(testoModello);

        // includo il DIP generato nell'archivio
        tmpZipOutputStream.putArchiveEntry(new ZipArchiveEntry("dichiarazione_DIP_esibizione.txt"));
        tmpZipOutputStream.write((byte[]) resolvedString.getBytes("UTF-8"));
        tmpZipOutputStream.closeArchiveEntry();
    }

    private void setRispostaWsError() {
        rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
    }
}
