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
package it.eng.parer.ws.recupero.utils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.recupero.ejb.ProduzioneDipEsibizione;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.xml.versReqStato.Recupero;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author fioravanti_f
 */
public class RecuperoTxtGen {

    private static final Logger log = LoggerFactory.getLogger(RecuperoTxtGen.class);

    // Timestamp fisso per gli entry ZIP (1 gennaio 2000)
    private static final long DEFAULT_ZIP_TIMESTAMP = LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0)
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

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
                    "GestSessRecDip Errore nel recupero dell'EJB dei controlli recupero DIP  "
                            + ex.getMessage());
            log.error("Errore nel recupero dell'EJB dei controlli  recupero DIP ", ex);
        }
    }

    /**
     * Crea un nuovo ZipEntry configurato con timestamp standard.
     */
    private ZipEntry createZipEntry(String name) {
        ZipEntry entry = new ZipEntry(name);
        entry.setTime(DEFAULT_ZIP_TIMESTAMP);
        return entry;
    }

    public void generaDipEsibizione(ZipOutputStream tmpZipOutputStream, RecuperoExt recupero)
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

        rispostaControlli = dipEsibizione.caricaModello(
                recupero.getParametriRecupero().getIdUnitaDoc(),
                ProduzioneDipEsibizione.TipiUsoModello.ESIBIZIONE, toqm);
        if (rispostaControlli.isrBoolean() == false) {
            setRispostaWsError();
            if (rispostaControlli.getCodErr() == null || rispostaControlli.getCodErr().isEmpty()) {
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.RECDIP_003_001,
                        rispostaControlli.getDsErr());
            } else {
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
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
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.RECDIP_003_001,
                        rispostaControlli.getDsErr());
            } else {
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            }
            return;
        }
        Map<String, String> mappaValori = (HashMap<String, String>) rispostaControlli.getrObject();

        // preparo il DIP per esibizione
        StrSubstitutor sub = new StrSubstitutor(mappaValori);
        String resolvedString = sub.replace(testoModello);

        // includo il DIP generato nell'archivio
        ZipEntry zipEntry = createZipEntry("dichiarazione_DIP_esibizione.txt");
        tmpZipOutputStream.putNextEntry(zipEntry);
        tmpZipOutputStream.write(resolvedString.getBytes("UTF-8"));
        tmpZipOutputStream.closeEntry();
    }

    private void setRispostaWsError() {
        rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
    }
}