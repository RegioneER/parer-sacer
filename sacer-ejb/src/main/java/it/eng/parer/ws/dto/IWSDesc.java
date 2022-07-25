/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.dto;

import java.util.HashMap;

/**
 *
 * @author Fioravanti_F
 */
public interface IWSDesc {

    String getNomeWs();

    @Deprecated
    String getVersione(); // versione standard, senza modifiche indotte dalla versione chiamata

    String getVersione(HashMap<String, String> mapWsVersion);

    // public String[] getCompatibilitaWS(); // lista di versioni compatibili con il parser
}
