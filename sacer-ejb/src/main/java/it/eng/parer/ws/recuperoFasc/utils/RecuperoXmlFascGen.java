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

package it.eng.parer.ws.recuperoFasc.utils;

import java.math.BigInteger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.recuperoFasc.dto.RecuperoFascExt;
import it.eng.parer.ws.recuperoFasc.dto.RispostaWSRecuperoFasc;
import it.eng.parer.ws.recuperoFasc.ejb.ControlliRecuperoFasc;
import it.eng.parer.ws.utils.AvanzamentoWs;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.parer.ws.xml.versReqStatoFasc.RecuperoFascicolo;
import it.eng.parer.ws.xml.versRespStatoFasc.ChiaveFascType;
import it.eng.parer.ws.xml.versRespStatoFasc.DatiFascicoloType;
import it.eng.parer.ws.xml.versRespStatoFasc.SCVersatoreFascType;
import it.eng.parer.ws.xml.versRespStatoFasc.StatoConservazioneFasc;
import it.eng.parer.ws.xml.versRespStatoFasc.StatoConservazioneFascType;

/**
 *
 * @author DiLorenzo_F
 */
public class RecuperoXmlFascGen {

    private static final Logger log = LoggerFactory.getLogger(RecuperoXmlFascGen.class);
    private RispostaWSRecuperoFasc rispostaWsFasc;
    private RispostaControlli rispostaControlli;
    // l'istanza della request decodificata dall'XML di versamento
    RecuperoFascicolo parsedFasc = null;
    // stateless ejb per la lettura di informazioni relative ai dati da recuperare
    ControlliRecuperoFasc controlliRecuperoFasc = null;

    public RispostaWSRecuperoFasc getRispostaWsFasc() {
	return rispostaWsFasc;
    }

    public RecuperoXmlFascGen(RispostaWSRecuperoFasc risp) throws NamingException {
	rispostaWsFasc = risp;
	rispostaControlli = new RispostaControlli();

	// recupera l'ejb per la lettura di informazioni, se possibile
	controlliRecuperoFasc = (ControlliRecuperoFasc) new InitialContext()
		.lookup("java:module/ControlliRecuperoFasc");
    }

    public void generaStatoConservazioneFasc(RecuperoFascExt recuperoFasc) {
	StatoConservazioneFasc myEsito = rispostaWsFasc.getIstanzaEsito();
	AvanzamentoWs myAvanzamentoWs = rispostaWsFasc.getAvanzamento();
	parsedFasc = recuperoFasc.getStrutturaRecuperoFasc();

	CSVersatore tmpCsVersatore = null;
	CSChiaveFasc tmpCsChiaveFasc = null;

	// genero i nomi delle cartelle relative alla struttura ed al fascicolo per il recupero
	if (rispostaWsFasc.getSeverity() == IRispostaWS.SeverityEnum.OK) {
	    rispostaControlli = controlliRecuperoFasc
		    .leggiChiaveFascicolo(recuperoFasc.getParametriRecuperoFasc().getIdFascicolo());
	    if (rispostaControlli.isrBoolean()) {
		tmpCsChiaveFasc = (CSChiaveFasc) rispostaControlli.getrObject();
	    } else {
		setRispostaWsFascError();
		rispostaWsFasc.setEsitoWsError(rispostaControlli.getCodErr(),
			rispostaControlli.getDsErr());
	    }
	}

	if (rispostaWsFasc.getSeverity() == IRispostaWS.SeverityEnum.OK) {
	    rispostaControlli = controlliRecuperoFasc.leggiVersatoreFascicolo(
		    recuperoFasc.getParametriRecuperoFasc().getIdFascicolo());
	    if (rispostaControlli.isrBoolean()) {
		tmpCsVersatore = (CSVersatore) rispostaControlli.getrObject();
	    } else {
		setRispostaWsFascError();
		rispostaWsFasc.setEsitoWsError(rispostaControlli.getCodErr(),
			rispostaControlli.getDsErr());
	    }
	}

	// crea Stato fasc
	// compila Chiave e tipo conservazione
	if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
	    myEsito.setFascicolo(new DatiFascicoloType());
	    SCVersatoreFascType tmpVersatoreFasc = new SCVersatoreFascType();
	    tmpVersatoreFasc.setAmbiente(tmpCsVersatore.getAmbiente());
	    tmpVersatoreFasc.setEnte(tmpCsVersatore.getEnte());
	    tmpVersatoreFasc.setStruttura(tmpCsVersatore.getStruttura());
	    tmpVersatoreFasc.setUserID(parsedFasc.getVersatore().getUserID());
	    //
	    if (parsedFasc.getVersatore().getUtente() != null
		    && !parsedFasc.getVersatore().getUtente().isEmpty()) {
		tmpVersatoreFasc.setUtente(parsedFasc.getVersatore().getUtente());
	    }
	    myEsito.getFascicolo().setVersatore(tmpVersatoreFasc);

	    ChiaveFascType tmpChiaveFasc = new ChiaveFascType();
	    tmpChiaveFasc.setAnno(BigInteger.valueOf(tmpCsChiaveFasc.getAnno()));
	    tmpChiaveFasc.setNumero(tmpCsChiaveFasc.getNumero());
	    myEsito.getFascicolo().setChiave(tmpChiaveFasc);
	}

	// compila Stato Conservazione
	if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
	    String tmpUrnFasc = MessaggiWSFormat
		    .formattaUrnDocUniDoc(MessaggiWSFormat.formattaBaseUrnUnitaDoc(
			    MessaggiWSFormat.formattaUrnPartVersatore(tmpCsVersatore),
			    MessaggiWSFormat.formattaUrnPartFasc(tmpCsChiaveFasc)));
	    myEsito.getFascicolo().setUrnFASC(tmpUrnFasc);
	    //
	    rispostaControlli.reset();
	    rispostaControlli = controlliRecuperoFasc
		    .leggiFasc(recuperoFasc.getParametriRecuperoFasc().getIdFascicolo());
	    if (rispostaControlli.isrBoolean() == false) {
		setRispostaWsFascError();
		rispostaWsFasc.setEsitoWsError(rispostaControlli.getCodErr(),
			rispostaControlli.getDsErr());
	    } else {
		myEsito.getFascicolo()
			.setStatoConservazioneFASC(StatoConservazioneFascType
				.valueOf((((FasFascicolo) rispostaControlli.getrObject())
					.getTiStatoConservazione().name())));
	    }
	}
    }

    private void setRispostaWsFascError() {
	rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
	rispostaWsFasc.setErrorCode(rispostaControlli.getCodErr());
	rispostaWsFasc.setErrorMessage(rispostaControlli.getDsErr());
    }
}
