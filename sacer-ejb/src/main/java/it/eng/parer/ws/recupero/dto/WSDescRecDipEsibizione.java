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
public class WSDescRecDipEsibizione implements IWSDesc {

    @Override
    public String getVersione() {
        throw new UnsupportedOperationException(getNomeWs() + ": supporta la versione su DB !");
    }

    @Override
    public String getNomeWs() {
        return Costanti.WS_REC_DIP_ESIBIZIONE_NOME;
    }

    @Override
    public String getVersione(HashMap<String, String> mapWsVersion) {
        return VerificaVersione.latestVersion(getNomeWs(), mapWsVersion);
    }

    // @Override
    // public String[] getCompatibilitaWS() {
    // return Costanti.WS_REC_DIP_ESIBIZIONE_COMP;
    // }
}
