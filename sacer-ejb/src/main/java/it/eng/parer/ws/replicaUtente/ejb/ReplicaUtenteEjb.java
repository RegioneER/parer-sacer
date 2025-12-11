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

package it.eng.parer.ws.replicaUtente.ejb;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.integriam.server.ws.Costanti;
import it.eng.integriam.server.ws.reputente.CancellaUtenteRisposta;
import it.eng.integriam.server.ws.reputente.InserimentoUtenteRisposta;
import it.eng.integriam.server.ws.reputente.ModificaUtenteRisposta;
import it.eng.integriam.server.ws.reputente.ReplicaUtenteInterface;
import it.eng.integriam.server.ws.reputente.Utente;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.replicaUtente.dto.CancellaUtenteExt;
import it.eng.parer.ws.replicaUtente.dto.InserimentoUtenteExt;
import it.eng.parer.ws.replicaUtente.dto.ModificaUtenteExt;
import it.eng.parer.ws.replicaUtente.dto.RispostaWSCancellaUtente;
import it.eng.parer.ws.replicaUtente.dto.RispostaWSInserimentoUtente;
import it.eng.parer.ws.replicaUtente.dto.RispostaWSModificaUtente;
import it.eng.parer.ws.replicaUtente.dto.WSDescCancellaUtente;
import it.eng.parer.ws.replicaUtente.dto.WSDescInserimentoUtente;
import it.eng.parer.ws.replicaUtente.dto.WSDescModificaUtente;
import it.eng.parer.ws.replicaUtente.utils.CancellaUtenteCheck;
import it.eng.parer.ws.replicaUtente.utils.InserimentoUtenteCheck;
import it.eng.parer.ws.replicaUtente.utils.ModificaUtenteCheck;
import it.eng.parer.ws.utils.WsTransactionManager;

/**
 *
 * @author Gilioli_P
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ReplicaUtenteEjb implements ReplicaUtenteInterface {

    @Resource
    private UserTransaction utx;
    private static final Logger log = LoggerFactory.getLogger(ReplicaUtenteEjb.class);
    private WsTransactionManager wtm;
    @EJB
    private InserimentoUtenteHelper iuHelper;
    @EJB
    private ModificaUtenteHelper muHelper;
    @EJB
    private CancellaUtenteHelper cuHelper;

    @Override
    public InserimentoUtenteRisposta inserimentoUtente(Utente utente) {
        /* Istanzio la risposta */
        RispostaWSInserimentoUtente rispostaWs = new RispostaWSInserimentoUtente();
        rispostaWs.setInserimentoUtenteRisposta(new InserimentoUtenteRisposta());

        /* Imposto l'esito della risposta di default OK */
        rispostaWs.getInserimentoUtenteRisposta().setCdEsito(Costanti.EsitoServizio.OK);

        /* Istanzio l'Ext con l'oggetto creato */
        InserimentoUtenteExt iuExt = new InserimentoUtenteExt();
        iuExt.setDescrizione(new WSDescInserimentoUtente());
        iuExt.setInserimentoUtenteInput(utente);

        log.info("Inizio controlli sui parametri di input forniti per Inserimento Utente");
        InserimentoUtenteCheck checker = new InserimentoUtenteCheck(iuExt, rispostaWs);
        checker.checkSessione();
        log.info("Fine controlli sui parametri di input");

        wtm = new WsTransactionManager(utx);

        if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
            try {
                log.info("Inizio inserimento utente");
                wtm.beginTrans(rispostaWs);
                /*
                 * Se i controlli sono andati a buon fine vuol dire che: - o l'utente esiste (e non
                 * è attivo), - o l'utente non esiste. Nel primo caso, lo modifico...
                 */
                if (iuHelper.existsUtente(iuExt.getInserimentoUtenteInput().getIdUserIam())) {
                    iuHelper.updateFromInserimentoIamUser(iuExt, rispostaWs);
                } // ... nel secondo lo inserisco da zero
                else {
                    iuHelper.insertIamUser(iuExt, rispostaWs);
                }

                /* Popola la risposta */
                rispostaWs.getInserimentoUtenteRisposta().setUtente(utente);

                wtm.commit(rispostaWs);
                log.info("Fine inserimento utente: operazione completata con successo!");
            } catch (ParerInternalError e) {
                rispostaWs.getInserimentoUtenteRisposta().setCdEsito(Costanti.EsitoServizio.KO);
                rispostaWs.getInserimentoUtenteRisposta().setCdErr(rispostaWs.getErrorCode());
                rispostaWs.getInserimentoUtenteRisposta().setDsErr(rispostaWs.getErrorMessage());
                wtm.rollback(rispostaWs);
                log.error(
                        "La procedura di Inserimento utente non è stata portata a termine: eseguito rollback",
                        e.getNativeException());
            }
        }
        /* Ritorno la risposta */
        return rispostaWs.getInserimentoUtenteRisposta();
    }

    @Override
    public ModificaUtenteRisposta modificaUtente(Utente utente) {
        /* Istanzio la risposta */
        RispostaWSModificaUtente rispostaWs = new RispostaWSModificaUtente();
        rispostaWs.setModificaUtenteRisposta(new ModificaUtenteRisposta());

        /* Imposto l'esito della risposta di default OK */
        rispostaWs.getModificaUtenteRisposta().setCdEsito(Costanti.EsitoServizio.OK);

        /* Istanzio l'Ext con l'oggetto creato */
        ModificaUtenteExt muExt = new ModificaUtenteExt();
        muExt.setDescrizione(new WSDescModificaUtente());
        muExt.setModificaUtenteInput(utente);

        log.info("Inizio controlli sui parametri di input forniti per Modifica Utente "
                + utente.getNmUserid());
        ModificaUtenteCheck checker = new ModificaUtenteCheck(muExt, rispostaWs);
        checker.checkSessione();
        log.info("Fine controlli sui parametri di input");

        wtm = new WsTransactionManager(utx);

        if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
            try {
                log.info("Inizio modifica utente");
                wtm.beginTrans(rispostaWs);
                /* Se i controlli sono andati a buon fine modifico l'utente... */
                // muHelper.updateIamUser(muExt, rispostaWs);
                muHelper.update2IamUser(muExt, rispostaWs);

                /* Popola la risposta */
                rispostaWs.getModificaUtenteRisposta().setUtente(utente);

                wtm.commit(rispostaWs);
                log.info("Fine modifica utente: operazione completata con successo!");
            } catch (ParerInternalError e) {
                rispostaWs.getModificaUtenteRisposta().setCdEsito(Costanti.EsitoServizio.KO);
                rispostaWs.getModificaUtenteRisposta().setCdErr(rispostaWs.getErrorCode());
                rispostaWs.getModificaUtenteRisposta().setDsErr(rispostaWs.getErrorMessage());
                wtm.rollback(rispostaWs);
                log.error(
                        "La procedura di Modifica utente non è stata portata a termine: eseguito rollback",
                        e.getNativeException());
            }
        }
        /* Ritorno la risposta */
        return rispostaWs.getModificaUtenteRisposta();
    }

    @Override
    public CancellaUtenteRisposta cancellaUtente(Integer idUserIam) {

        /* Istanzio la risposta */
        RispostaWSCancellaUtente rispostaWs = new RispostaWSCancellaUtente();
        rispostaWs.setCancellaUtenteRisposta(new CancellaUtenteRisposta());

        /* Imposto l'esito della risposta di default OK */
        rispostaWs.getCancellaUtenteRisposta().setCdEsito(Costanti.EsitoServizio.OK);

        /* Istanzio l'Ext con l'oggetto creato */
        CancellaUtenteExt cuExt = new CancellaUtenteExt();
        cuExt.setDescrizione(new WSDescCancellaUtente());
        cuExt.setIdUserIam(idUserIam);

        log.info("Inizio controlli sui parametri di input forniti per Cancella Utente");
        CancellaUtenteCheck checker = new CancellaUtenteCheck(cuExt, rispostaWs);
        checker.checkSessione();
        log.info("Fine controlli sui parametri di input");

        wtm = new WsTransactionManager(utx);

        if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
            try {
                log.info("Inizio cancellazione utente");
                wtm.beginTrans(rispostaWs);

                /* Se i controlli sono andati a buon fine modifico/elimino l'utente... */
                cuHelper.deleteIamUser(cuExt, rispostaWs);

                /* Popola la risposta */
                rispostaWs.getCancellaUtenteRisposta().setIdUserIam(idUserIam);

                wtm.commit(rispostaWs);
                log.info("Fine cancellazione utente: operazione completata con successo!");
            } catch (ParerInternalError e) {
                rispostaWs.getCancellaUtenteRisposta().setCdEsito(Costanti.EsitoServizio.KO);
                rispostaWs.getCancellaUtenteRisposta().setCdErr(rispostaWs.getErrorCode());
                rispostaWs.getCancellaUtenteRisposta().setDsErr(rispostaWs.getErrorMessage());
                wtm.rollback(rispostaWs);
                log.error(
                        "La procedura di Cancellazione utente non è stata portata a termine: eseguito rollback",
                        e.getNativeException());
            }
        }
        /* Ritorno la risposta */
        return rispostaWs.getCancellaUtenteRisposta();
    }
}
