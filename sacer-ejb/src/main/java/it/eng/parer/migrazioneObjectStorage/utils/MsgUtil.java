/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.migrazioneObjectStorage.utils;

import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Iacolucci_M
 */
public class MsgUtil {

    private static final Properties instance = new Properties();
    private static Logger log = LoggerFactory.getLogger(MsgUtil.class);

    static {
        try {
            instance.load(MsgUtil.class.getResourceAsStream("/messaggi_ejb.properties"));
        } catch (IOException ex) {
            log.error("Errore caricamento messaggi", ex);
        }
    }

    protected MsgUtil() {
    }

    /*
     * public static Object getInstance() { return instance; }
     */
    public static String getMessage(String codice) {
        return (String) instance.get(codice);
    }

    public static String getCompleteMessage(String codice) {
        return codice + " - " + getMessage(codice) + ";";
    }

}
