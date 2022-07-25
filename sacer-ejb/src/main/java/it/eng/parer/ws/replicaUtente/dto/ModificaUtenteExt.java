package it.eng.parer.ws.replicaUtente.dto;

import it.eng.integriam.server.ws.reputente.Utente;
import it.eng.parer.ws.dto.IWSDesc;

/**
 *
 * @author Gilioli_P
 */
public class ModificaUtenteExt {

    private IWSDesc descrizione;
    private Utente modificaUtenteInput;

    public IWSDesc getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(IWSDesc descrizione) {
        this.descrizione = descrizione;
    }

    public Utente getModificaUtenteInput() {
        return modificaUtenteInput;
    }

    public void setModificaUtenteInput(Utente modificaUtenteInput) {
        this.modificaUtenteInput = modificaUtenteInput;
    }
}