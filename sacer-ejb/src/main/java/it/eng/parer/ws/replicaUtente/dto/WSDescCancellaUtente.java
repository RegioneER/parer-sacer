package it.eng.parer.ws.replicaUtente.dto;

import java.util.HashMap;

import it.eng.parer.ws.dto.IWSDesc;
import it.eng.parer.ws.utils.Costanti;

/**
 *
 * @author Gilioli_P
 */
public class WSDescCancellaUtente implements IWSDesc {

    @Override
    public String getVersione() {
        return Costanti.WS_CANCELLA_UTENTE_VRSN;
    }

    @Override
    public String getNomeWs() {
        return Costanti.WS_CANCELLA_UTENTE_NOME;
    }

    public String[] getCompatibilitaWS() {
        return Costanti.WS_CANCELLA_UTENTE_COMP;
    }

    @Override
    public String getVersione(HashMap<String, String> mapWsVersion) {
        throw new UnsupportedOperationException(getNomeWs() + ": non supporta la versione su DB !");

    }
}