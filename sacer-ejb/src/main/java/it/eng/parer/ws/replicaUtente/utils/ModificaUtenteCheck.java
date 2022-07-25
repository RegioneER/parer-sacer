package it.eng.parer.ws.replicaUtente.utils;

import it.eng.integriam.server.ws.Costanti;
import it.eng.integriam.server.ws.Costanti.EsitoServizio;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.ControlliReplicaUtente;
import it.eng.parer.ws.replicaUtente.dto.ModificaUtenteExt;
import it.eng.parer.ws.replicaUtente.dto.RispostaWSModificaUtente;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
public class ModificaUtenteCheck {

    private static final Logger log = LoggerFactory.getLogger(ModificaUtenteCheck.class);
    ModificaUtenteExt modificaUtenteExt;
    RispostaWSModificaUtente rispostaWs;
    private RispostaControlli rispostaControlli;
    ControlliReplicaUtente controlliRU = null;

    public ModificaUtenteCheck(ModificaUtenteExt modificaUtenteExt, RispostaWSModificaUtente rispostaWs) {
        this.modificaUtenteExt = modificaUtenteExt;
        this.rispostaWs = rispostaWs;
        this.rispostaControlli = new RispostaControlli();

        try {
            controlliRU = (ControlliReplicaUtente) new InitialContext().lookup("java:module/ControlliReplicaUtente");
        } catch (NamingException ex) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setErrorCode(MessaggiWSBundle.ERR_666);
            String msg = MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666, ex.getMessage());
            rispostaWs.setErrorMessage(msg);
            rispostaWs.getModificaUtenteRisposta().setCdEsito(Costanti.EsitoServizio.KO);
            rispostaWs.getModificaUtenteRisposta().setCdErr(MessaggiWSBundle.ERR_666);
            rispostaWs.getModificaUtenteRisposta().setDsErr(msg);
            log.error("Errore nel recupero dell'EJB dei controlli replica utente ", ex);
        }
    }

    public void checkSessione() {
        // Verifica Utente
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            rispostaControlli.reset();
            rispostaControlli = controlliRU
                    .verificaEsistenzaUtente(modificaUtenteExt.getModificaUtenteInput().getIdUserIam());
            if (!rispostaControlli.isrBoolean()) {
                if (rispostaControlli.getCodErr() == null) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.SERVIZI_USR_004);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.SERVIZI_USR_004,
                            modificaUtenteExt.getModificaUtenteInput().getIdUserIam()));
                    setRispostaWsError(SeverityEnum.ERROR, Costanti.EsitoServizio.KO);
                } else {
                    // Errore 666
                    setRispostaWsError(SeverityEnum.ERROR, Costanti.EsitoServizio.KO);
                }
            }
        }
    }

    public RispostaWSModificaUtente getRispostaWs() {
        return rispostaWs;
    }

    private void setRispostaWsError(SeverityEnum sev, EsitoServizio esito) {
        rispostaWs.setSeverity(sev);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
        rispostaWs.getModificaUtenteRisposta().setCdEsito(esito);
        rispostaWs.getModificaUtenteRisposta().setCdErr(rispostaControlli.getCodErr());
        rispostaWs.getModificaUtenteRisposta().setDsErr(rispostaControlli.getDsErr());
    }
}