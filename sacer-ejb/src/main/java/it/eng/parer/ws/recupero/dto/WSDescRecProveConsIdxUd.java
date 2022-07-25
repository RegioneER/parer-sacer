/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.recupero.dto;

import java.util.HashMap;

import it.eng.parer.ws.dto.IWSDesc;
import it.eng.parer.ws.utils.Costanti;

/**
 *
 * @author Fioravanti_F
 */
public class WSDescRecProveConsIdxUd implements IWSDesc {

    @Override
    public String getVersione() {
        return Costanti.WS_REC_PROVE_CON_IDX_UD_VRSN;
    }

    @Override
    public String getNomeWs() {
        return Costanti.WS_REC_PROVE_CON_IDX_UD_NOME;
    }

    public String[] getCompatibilitaWS() {
        return Costanti.WS_REC_PROVE_CON_IDX_UD_COMP;
    }

    @Override
    public String getVersione(HashMap<String, String> mapWsVersion) {
        throw new UnsupportedOperationException(getNomeWs() + ": supporta la versione su DB !");
    }
}
