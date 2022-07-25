package it.eng.parer.ws.replicaUtente.dto;

import it.eng.integriam.server.ws.reputente.Utente;
import it.eng.parer.ws.dto.IWSDesc;

/**
 *
 * @author Gilioli_P
 */
public class InserimentoUtenteExt {

    private IWSDesc descrizione;
    private Utente inserimentoUtenteInput;

    public IWSDesc getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(IWSDesc descrizione) {
        this.descrizione = descrizione;
    }

    public Utente getInserimentoUtenteInput() {
        return inserimentoUtenteInput;
    }

    public void setInserimentoUtenteInput(Utente utente) {
        this.inserimentoUtenteInput = utente;
    }
}