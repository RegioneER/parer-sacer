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
import it.eng.parer.ws.replicaUtente.dto.CancellaUtenteExt;
import it.eng.parer.ws.replicaUtente.dto.RispostaWSCancellaUtente;
import it.eng.parer.ws.utils.MessaggiWSBundle;

/**
 *
 * @author Gilioli_P
 */
public class CancellaUtenteCheck {

    private static final Logger log = LoggerFactory.getLogger(CancellaUtenteCheck.class);
    CancellaUtenteExt cancellaUtenteExt;
    RispostaWSCancellaUtente rispostaWs;
    private RispostaControlli rispostaControlli;
    ControlliReplicaUtente controlliRU = null;

    public CancellaUtenteCheck(CancellaUtenteExt cancellaUtenteExt,
            RispostaWSCancellaUtente rispostaWs) {
        this.cancellaUtenteExt = cancellaUtenteExt;
        this.rispostaWs = rispostaWs;
        this.rispostaControlli = new RispostaControlli();

        try {
            controlliRU = (ControlliReplicaUtente) new InitialContext()
                    .lookup("java:module/ControlliReplicaUtente");
        } catch (NamingException ex) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setErrorCode(MessaggiWSBundle.ERR_666);
            String msg = MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666, ex.getMessage());
            rispostaWs.setErrorMessage(msg);
            rispostaWs.getCancellaUtenteRisposta().setCdEsito(Costanti.EsitoServizio.KO);
            rispostaWs.getCancellaUtenteRisposta().setCdErr(MessaggiWSBundle.ERR_666);
            rispostaWs.getCancellaUtenteRisposta().setDsErr(msg);
            log.error("Errore nel recupero dell'EJB dei controlli replica utente ", ex);
        }
    }

    public void checkSessione() {
        // Verifica Utente
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliRU
                    .verificaEsistenzaUtente(cancellaUtenteExt.getIdUserIam());
            if (!rispostaControlli.isrBoolean()) {
                if (rispostaControlli.getCodErr() == null) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.SERVIZI_USR_004);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(
                            MessaggiWSBundle.SERVIZI_USR_004, cancellaUtenteExt.getIdUserIam()));
                    setRispostaWsError(SeverityEnum.ERROR, Costanti.EsitoServizio.KO);
                } else {
                    // Errore 666
                    setRispostaWsError(SeverityEnum.ERROR, Costanti.EsitoServizio.KO);
                }
            }
        }
    }

    public RispostaWSCancellaUtente getRispostaWs() {
        return rispostaWs;
    }

    private void setRispostaWsError(SeverityEnum sev, EsitoServizio esito) {
        rispostaWs.setSeverity(sev);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
        rispostaWs.getCancellaUtenteRisposta().setCdEsito(esito);
        rispostaWs.getCancellaUtenteRisposta().setCdErr(rispostaControlli.getCodErr());
        rispostaWs.getCancellaUtenteRisposta().setDsErr(rispostaControlli.getDsErr());
    }
}
