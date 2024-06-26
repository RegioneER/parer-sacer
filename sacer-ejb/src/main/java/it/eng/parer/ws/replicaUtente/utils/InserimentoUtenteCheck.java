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

package it.eng.parer.ws.replicaUtente.utils;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.integriam.server.ws.Costanti;
import it.eng.integriam.server.ws.Costanti.EsitoServizio;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.ControlliReplicaUtente;
import it.eng.parer.ws.replicaUtente.dto.InserimentoUtenteExt;
import it.eng.parer.ws.replicaUtente.dto.RispostaWSInserimentoUtente;
import it.eng.parer.ws.utils.MessaggiWSBundle;

/**
 *
 * @author Gilioli_P
 */
public class InserimentoUtenteCheck {

    private static final Logger log = LoggerFactory.getLogger(InserimentoUtenteCheck.class);
    InserimentoUtenteExt inserimentoUtenteExt;
    RispostaWSInserimentoUtente rispostaWs;
    private RispostaControlli rispostaControlli;
    ControlliReplicaUtente controlliRU = null;

    public InserimentoUtenteCheck(InserimentoUtenteExt inserimentoUtenteExt, RispostaWSInserimentoUtente rispostaWs) {
        this.inserimentoUtenteExt = inserimentoUtenteExt;
        this.rispostaWs = rispostaWs;
        this.rispostaControlli = new RispostaControlli();

        try {
            controlliRU = (ControlliReplicaUtente) new InitialContext().lookup("java:module/ControlliReplicaUtente");
        } catch (NamingException ex) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setErrorCode(MessaggiWSBundle.ERR_666);
            String msg = MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666, ex.getMessage());
            rispostaWs.setErrorMessage(msg);
            rispostaWs.getInserimentoUtenteRisposta().setCdEsito(Costanti.EsitoServizio.KO);
            rispostaWs.getInserimentoUtenteRisposta().setCdErr(MessaggiWSBundle.ERR_666);
            rispostaWs.getInserimentoUtenteRisposta().setDsErr(msg);
            log.error("Errore nel recupero dell'EJB dei controlli replica utente ", ex);
        }
    }

    public void checkSessione() {
        // Verifica Utente
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliRU
                    .verificaEsistenzaUtenteAttivo(inserimentoUtenteExt.getInserimentoUtenteInput().getIdUserIam());
            if (rispostaControlli.isrBoolean()) {
                if (rispostaControlli.getCodErr() == null) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.SERVIZI_USR_002);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.SERVIZI_USR_002,
                            inserimentoUtenteExt.getInserimentoUtenteInput().getIdUserIam()));
                    setRispostaWsError(SeverityEnum.ERROR, Costanti.EsitoServizio.KO);
                } else {
                    // Errore 666
                    setRispostaWsError(SeverityEnum.ERROR, Costanti.EsitoServizio.KO);
                }
            }
        }

        // Verifica Userid
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliRU
                    .verificaEsistenzaNmUserid(inserimentoUtenteExt.getInserimentoUtenteInput().getNmUserid());
            if (rispostaControlli.isrBoolean()) {
                if (rispostaControlli.getCodErr() == null) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.SERVIZI_USR_003);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.SERVIZI_USR_003,
                            inserimentoUtenteExt.getInserimentoUtenteInput().getNmUserid()));
                    setRispostaWsError(SeverityEnum.ERROR, Costanti.EsitoServizio.KO);
                } else {
                    // Errore 666
                    setRispostaWsError(SeverityEnum.ERROR, Costanti.EsitoServizio.KO);
                }
            }
        }
    }

    public RispostaWSInserimentoUtente getRispostaWs() {
        return rispostaWs;
    }

    private void setRispostaWsError(SeverityEnum sev, EsitoServizio esito) {
        rispostaWs.setSeverity(sev);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
        rispostaWs.getInserimentoUtenteRisposta().setCdEsito(esito);
        rispostaWs.getInserimentoUtenteRisposta().setCdErr(rispostaControlli.getCodErr());
        rispostaWs.getInserimentoUtenteRisposta().setDsErr(rispostaControlli.getDsErr());
    }
}
