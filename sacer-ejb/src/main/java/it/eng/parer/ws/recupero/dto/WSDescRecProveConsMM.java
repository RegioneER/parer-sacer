/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.recupero.dto;

import java.util.HashMap;

import it.eng.parer.ws.dto.IWSDesc;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.VerificaVersione;

/**
 * 
 * @author Fioravanti_F
 */
public class WSDescRecProveConsMM implements IWSDesc {

    public String getVersione() {
        return Costanti.WS_REC_PROVE_CON_VRSN;
    }

    @Override
    public String getNomeWs() {
        return Costanti.WS_REC_PROVE_CON_MM_NOME;
    }

    @Override
    public String getVersione(HashMap<String, String> mapWsVersion) {
        return VerificaVersione.latestVersion(getNomeWs(), mapWsVersion);
    }

    // @Override
    // public String[] getCompatibilitaWS() {
    // return Costanti.WS_REC_PROVE_CON_COMP;
    // }
}
