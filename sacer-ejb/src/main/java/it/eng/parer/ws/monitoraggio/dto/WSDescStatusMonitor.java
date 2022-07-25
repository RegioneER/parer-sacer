/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.monitoraggio.dto;

import java.util.HashMap;

import it.eng.parer.ws.dto.IWSDesc;
import it.eng.parer.ws.utils.Costanti;

/**
 *
 * @author fioravanti_f
 */
public class WSDescStatusMonitor implements IWSDesc {

    @Override
    public String getVersione() {
        return Costanti.WS_STATUS_MONITOR_VRSN;
    }

    @Override
    public String getNomeWs() {
        return Costanti.WS_STATUS_MONITOR_NOME;
    }

    public String[] getCompatibilitaWS() {
        return Costanti.WS_STATUS_MONITOR_COMP;
    }

    @Override
    public String getVersione(HashMap<String, String> mapWsVersion) {
        throw new UnsupportedOperationException(getNomeWs() + ": supporta la versione su DB !");
    }
}
