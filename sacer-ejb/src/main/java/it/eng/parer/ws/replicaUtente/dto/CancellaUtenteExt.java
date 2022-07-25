package it.eng.parer.ws.replicaUtente.dto;

import it.eng.parer.ws.dto.IWSDesc;

/**
 *
 * @author Gilioli_P
 */
public class CancellaUtenteExt {

    private IWSDesc descrizione;
    private Integer idUserIam;

    public IWSDesc getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(IWSDesc descrizione) {
        this.descrizione = descrizione;
    }

    public Integer getIdUserIam() {
        return idUserIam;
    }

    public void setIdUserIam(Integer idUserIam) {
        this.idUserIam = idUserIam;
    }
}