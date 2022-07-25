package it.eng.parer.ws.richiestaAnnullamentoVersamenti.dto;

import java.util.HashMap;

import it.eng.parer.ws.dto.IWSDesc;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.VerificaVersione;

/**
 *
 * @author Gilioli_P
 */
public class WSDescRichiestaAnnullamentoVersamenti implements IWSDesc {

    @Override
    public String getVersione() {
        throw new UnsupportedOperationException(getNomeWs() + ": supporta la versione su DB !");
    }

    @Override
    public String getNomeWs() {
        return Costanti.WS_RICHIESTA_ANNULLAMENTO_VERSAMENTI_NOME;
    }

    @Override
    public String getVersione(HashMap<String, String> mapWsVersion) {
        return VerificaVersione.latestVersion(getNomeWs(), mapWsVersion);
    }

    // @Override
    // public String[] getCompatibilitaWS() {
    // return Costanti.WS_RICHIESTA_ANNULLAMENTO_VERSAMENTI_COMP;
    // }
}